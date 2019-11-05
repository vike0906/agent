package com.vike.agent.service.serviceImpl;

import com.vike.agent.common.GloableConstant;
import com.vike.agent.common.OrderHelp;
import com.vike.agent.common.PageLimit;
import com.vike.agent.dao.*;
import com.vike.agent.entity.*;
import com.vike.agent.service.SummaryService;
import com.vike.agent.utils.EncryptUtils;
import com.vike.agent.utils.RandomUtil;
import com.vike.agent.vo.SummaryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/11/1
 */
@Service
@Slf4j
public class SummaryServiceImpl implements SummaryService {


    private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    AgentRepository agentRepository;
    @Autowired
    BonusRepository bonusRepository;
    @Autowired
    StatisticalRepository statisticalRepository;
    @Autowired
    WithdrawRepository withdrawRepository;
    @Autowired
    SysUserRepository sysUserRepository;
    @Autowired
    SysRoleRepository sysRoleRepository;
    @Autowired
    EntityManager entityManager;

    @Override
    public Page<Agent> findByParId(SysUser sysUser, String mobile, PageLimit pageLimit) {
        Specification<Agent> specification = (Specification<Agent>)(root, query, builder)->{
            List<Predicate> list = new ArrayList<>();
            if(sysUser.getRole().getId()==GloableConstant.AGENT_LEVEL_FIRST){
                long parId = agentRepository.findAgentBySysId(sysUser.getId()).get().getId();
                Path<Long> path = root.get("parId");
                list.add(builder.equal(path, parId));
            }
            if(!StringUtils.isEmpty(mobile)){
                Path<String> path = root.get("mobile");
                list.add(builder.equal(path, mobile));
            }
            Predicate [] predicates = new Predicate[list.size()];
            query.where(builder.and(list.toArray(predicates)));
            query.orderBy(builder.desc(root.get("createTime")));
            return query.getRestriction();
        };
        return agentRepository.findAll(specification,pageLimit.page());
    }

    @Override
    @Transactional
    public String changeAgent(SysUser sysUser, long id, int type) {

        Optional<Agent> op = agentRepository.findById(id);
        if(op.isPresent()){
            Agent agent = op.get();

            if(!checkAgentPermission(sysUser,agent)) return "非法请求";

            if(type==1){
                if(agent.getStatus()!=GloableConstant.NORMALL_STATUS) return "状态错误";

                SysUser user = sysUserRepository.findById(agent.getSysId()).get();

                agent.setStatus(GloableConstant.CANCEL_STATUS);
                sysUserRepository.save(user);

                user.setStatus(GloableConstant.CANCEL_STATUS);
                agentRepository.save(agent);

                return null;
            }else {
                if(agent.getStatus()!=GloableConstant.CANCEL_STATUS) return "状态错误";

                SysUser user = sysUserRepository.findById(agent.getSysId()).get();

                agent.setStatus(GloableConstant.NORMALL_STATUS);
                sysUserRepository.save(user);

                user.setStatus(GloableConstant.NORMALL_STATUS);
                agentRepository.save(agent);

                return null;
            }
        }
        return "该代理不存在";
    }

    @Override
    @Transactional
    public String editAgent(SysUser sysUser, long id, String nickName, String mobile, int ratio) {
        Optional<Agent> op = agentRepository.findById(id);
        if(op.isPresent()){
            Agent agent = op.get();

            if(!checkAgentPermission(sysUser,agent)) return "非法请求";

            agent.setNickName(nickName).setMobile(mobile).setRatio(ratio);
            agentRepository.save(agent);
            return null;
        }
        return "代理不存在";
    }

    @Override
    @Transactional
    public void saveAgent(SysUser sysUserP, String nickName, String loginName, String mobile, Integer ratio, String password,long parSysId) {
        SysUser sysUser = new SysUser();
        SysRole sysRole = null;
        long parId = 0L;
        int level = 1;
        String url = "#";

        if(sysUserP.getRole().getId()==GloableConstant.AGENT_LEVEL_FIRST){
            sysRole = sysRoleRepository.findById(GloableConstant.AGENT_LEVEL_SECOND).get();
            Agent agentParent = agentRepository.findAgentBySysId(parSysId).get();
            parId = agentParent.getId();
            level = 2;
            url = RandomUtil.UUID();
        }else {
            sysRole = sysRoleRepository.findById(GloableConstant.AGENT_LEVEL_FIRST).get();
        }

        /**构造SysUser并添加*/
        sysUser.setName(nickName).setLoginName(loginName)
                .setStatus(GloableConstant.CANCEL_STATUS)
                .setPassword(EncryptUtils.MD5(password))
                .setRole(sysRole);
        SysUser save = sysUserRepository.save(sysUser);

        /**构造Agent并添加*/
        Agent agent = new Agent();
        agent.setNickName(nickName).setLoginName(loginName)
                .setSysId(save.getId()).setRatio(ratio)
                .setStatus(GloableConstant.CANCEL_STATUS).setMobile(mobile)
                .setParId(parId).setLevel(level).setUrl(url);
        agentRepository.save(agent);
    }

    @Override
    public Page<Bonus> findBonus(SysUser sysUser, String queryStr, String queryDate, PageLimit pageLimit) {

        long roleId = sysUser.getRole().getId();

        Specification<Bonus> specification = (Specification<Bonus>)(root, query, builder)->{
            List<Predicate> list = new ArrayList<>();
            if(!StringUtils.isEmpty(queryStr)){
                Path<String> clientMobile = root.get("clientMobile");
                Path<String> agentMobile = root.get("agentMobile");
                Predicate equal1 = builder.equal(clientMobile, queryStr);
                Predicate equal2 = builder.equal(agentMobile, queryStr);
                list.add(builder.or(equal1,equal2));
            }
            if(!StringUtils.isEmpty(queryDate)){
                String[] split = queryDate.split(" 至 ");
                if(split.length==2){
                    try {
                        Date start = sd.parse(split[0]+" 00:00:00");
                        Date end = sd.parse(split[1]+" 23:59:59");
                        Path<Date> createTime = root.get("createTime");
                        list.add(builder.between(createTime,start,end));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        log.error("时间格式解析出错");
                    }

                }
            }
            if(roleId==GloableConstant.AGENT_LEVEL_SECOND){ //二级代理 查看个人分佣情况
                Agent agent = agentRepository.findAgentBySysId(sysUser.getId()).get();
                Path<Long> agentId = root.get("agentId");
                list.add(builder.equal(agentId, agent.getId()));

            }else if(roleId==GloableConstant.AGENT_LEVEL_FIRST){ //一级代理 查看所属渠道分佣情况
                Agent agent = agentRepository.findAgentBySysId(sysUser.getId()).get();
                Path<Long> parentAgentId = root.get("parentAgentId");
                list.add(builder.equal(parentAgentId, agent.getId()));
            }else { //运维 查看全部一级代理分佣情况
                Path<Integer> level = root.get("level");
                list.add(builder.notEqual(level, GloableConstant.AGENT_SECOND_LEVEL));
            }
            Predicate [] Predicates = new Predicate[list.size()];
            query.where(builder.and(list.toArray(Predicates)));
            query.orderBy(builder.desc(root.get("createTime")));
            return query.getRestriction();
        };
        return bonusRepository.findAll(specification,pageLimit.page());
    }

    @Override
    public SummaryVo summary(SysUser sysUser) {
        return new SummaryVo(898900,80000,50,100000,88,556300,352,556300,352,85256300,4180);
//        long roleId = sysUser.getRole().getId();
//        if(roleId==GloableConstant.AGENT_LEVEL_SECOND){
//            Optional<Agent> op = agentRepository.findAgentBySysId(sysUser.getId());
//            if(op.isPresent()){
//                Agent agent = op.get();
//                String totalSql = "select IFNULL(sum(amount),0) amount, IFNULL(count(id),0) count from se_bonus where agent_id = "+agent.getId();
//                return vo(agent.getAmount(), totalSql);
//            }
//        }else if(roleId==GloableConstant.AGENT_LEVEL_FIRST){
//            Optional<Agent> op = agentRepository.findAgentBySysId(sysUser.getId());
//            if(op.isPresent()){
//                Agent agent = op.get();
//                String totalSql = "select IFNULL(sum(amount),0) amount, IFNULL(count(id),0) count from se_bonus where parent_agent_id = "+agent.getId();
//                return vo(agent.getAmount(), totalSql);
//            }
//        }else {
//            String amountSql = "select IFNULL(sum(amount),0) amount from se_bonus where parent_agent_id = 0 ";
//            Query nativeQuery = entityManager.createNativeQuery(amountSql);
//            BigDecimal amount = (BigDecimal)nativeQuery.getSingleResult();
//            String totalSql = "select IFNULL(sum(amount),0) amount, IFNULL(count(id),0) count from se_bonus where parent_agent_id = 0 ";
//            return vo(amount.intValue(), totalSql);
//        }
//        return new SummaryVo(0,0,0,0,0,0,0,0,0,0,0);
    }

    @Override
    public Page<Statistical> statistical(SysUser sysUser, PageLimit pageLimit) {
        long roleId = sysUser.getRole().getId();
        if(roleId==GloableConstant.AGENT_LEVEL_FIRST||roleId==GloableConstant.AGENT_LEVEL_SECOND){
            Specification<Statistical> specification = (Specification<Statistical>)(root,query,builder)->{
                Path<Object> path = root.get("agentId");
                Optional<Agent> op = agentRepository.findAgentBySysId(sysUser.getId());
                op.ifPresent(agent -> query.where(builder.equal(path, agent.getId())));
                if(op.isPresent()){
                    query.where(builder.equal(path, op.get().getId()));
                }else {
                    query.where(builder.equal(path, -1L));
                }
                query.orderBy(builder.desc(root.get("createTime")));
                return query.getRestriction();
            };
            return statisticalRepository.findAll(specification,pageLimit.page());
        }else if(roleId==GloableConstant.ADMIN_ID||roleId==GloableConstant.OP_ID){
            Specification<Statistical> specification = (Specification<Statistical>)(root,query,builder)->{
                Path<Object> path = root.get("agentId");
                query.where(builder.equal(path, 0L));
                query.orderBy(builder.desc(root.get("createTime")));
                return query.getRestriction();
            };
            return statisticalRepository.findAll(specification,pageLimit.page());
        }

        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    @Transactional
    public Withdraw saveWithdraw(SysUser sysUser, String account, String name, String type, int amount, String remark) {
        Optional<Agent> op = agentRepository.findAgentBySysId(sysUser.getId());
        if(op.isPresent()){
            Agent agent = op.get();
            Withdraw withdraw = new Withdraw();
            withdraw.setOrderNo(OrderHelp.createOrderNo()).setAgentId(agent.getId()).setAgentInfo(agent.getNickName()+"("+agent.getMobile()+")")
                    .setAccount(account).setName(name).setType(type).setAmount(amount).setCharge(0).setBalance(agent.getAmount())
                    .setRemark(remark).setStatus(1);
            Withdraw save = withdrawRepository.save(withdraw);
            return save;
        }
        return null;
    }

    @Override
    public Page<Withdraw> findWithdraw(SysUser sysUser, String queryStr, int queryStatus, PageLimit pageLimit) {
        long roleId = sysUser.getRole().getId();
        Specification<Withdraw> specification = (Specification<Withdraw>)(r,q,b)->{
            List<Predicate> list = new ArrayList<>();
            if(roleId==GloableConstant.AGENT_LEVEL_FIRST){
                Agent agent = agentRepository.findAgentBySysId(sysUser.getId()).get();
                Path<Long> path = r.get("agentId");
                list.add(b.equal(path,agent.getId()));
            }
            if(!StringUtils.isEmpty(queryStr)){
                Path<String> accountPath = r.get("account");
                Path<String> orderNoPath = r.get("orderNo");
                list.add(b.or(b.equal(accountPath,queryStr),b.equal(orderNoPath,queryStr)));
            }
            if(queryStatus!=0){
                Path<Integer> path = r.get("status");
                list.add(b.equal(path,queryStatus));
            }
            Predicate [] predicates = new Predicate[list.size()];
            q.where(b.and(list.toArray(predicates)));
            q.orderBy(b.desc(r.get("createTime")));
            return q.getRestriction();
        };
        return withdrawRepository.findAll(specification,pageLimit.page());
    }

    @Override
    public String audit(long id, long auditId, int type, String remark) {
        Optional<Withdraw> op = withdrawRepository.findById(id);
        if(op.isPresent()){
            Withdraw withdraw = op.get();
            if(type==1){
                Optional<Agent> op1 = agentRepository.findById(withdraw.getAgentId());
                if(op1.isPresent()&&op1.get().getStatus()==GloableConstant.NORMALL_STATUS){
                    Agent agent = op1.get();
                    if(agent.getStatus()==GloableConstant.CANCEL_STATUS){
                        return "账户异常";
                    }
                    if(agent.getAmount()<withdraw.getAmount()){
                        return "余额不足";
                    }
                    agent.setAmount(agent.getAmount()-withdraw.getAmount());
                    withdraw.setStatus(2);
                    agentRepository.save(agent);
                    withdrawRepository.save(withdraw);
                }
                return "账户异常";
            }else if(type==2){
                withdraw.setStatus(3);
            }else {
                return "参数错误";
            }
            withdraw.setAudit_id(auditId).setRemark(remark).setUpdateTime(new Date());
            withdrawRepository.save(withdraw);
            return null;
        }
        return "申请不存在";
    }

    private boolean checkAgentPermission(SysUser sysUser, Agent agent){
        if(sysUser.getRole().getId() == GloableConstant.AGENT_LEVEL_FIRST){
            Agent agentParent = agentRepository.findAgentBySysId(sysUser.getId()).get();
            if(agentParent.getId()!=agent.getParId()) return false;
        }
        return true;
    }

    private SummaryPo po(String sql){
        Query nativeQuery = entityManager.createNativeQuery(sql,SummaryPo.class);
        SummaryPo po = (SummaryPo)nativeQuery.getSingleResult();
        return po;
    }

    private SummaryVo vo(int amount, String totalSql){

        SummaryVo summaryVo = new SummaryVo();

        summaryVo.setBalance(amount);

        SummaryPo po1 = po(totalSql);
        summaryVo.setTotalAmount(po1.getAmount()).setTotalBonus(po1.getCount());

        String todaySql = totalSql+" and Date(create_time) = Date(now())";
        SummaryPo po2 = po(todaySql);
        summaryVo.setTodayAmount(po2.getAmount()).setTodayBonus(po2.getCount());

        String lastDaySql = totalSql+" and Date(create_time) = Date(DATE_SUB(NOW(),INTERVAL 1 DAY))";
        SummaryPo po3 = po(lastDaySql);
        summaryVo.setLastDayAmount(po3.getAmount()).setLastDayBonus(po3.getCount());

        String monthSql = totalSql+" and Year(create_time) = Year(NOW()) and Month(create_time) = Month(NOW())";
        SummaryPo po4 = po(monthSql);
        summaryVo.setMonthAmount(po4.getAmount()).setMonthBonus(po4.getCount());

        String lastMonthSql = totalSql+" and Year(create_time) = Year(DATE_SUB(NOW(),INTERVAL 1 MONTH)) and Month(create_time) = Month(DATE_SUB(NOW(),INTERVAL 1 MONTH))";
        SummaryPo po5 = po(lastMonthSql);
        summaryVo.setLastMonthAmount(po5.getAmount()).setLastMonthBonus(po5.getCount());

        return summaryVo;
    }

}
