package com.vike.agent.service.serviceImpl;

import com.vike.agent.common.GloableConstant;
import com.vike.agent.common.PageLimit;
import com.vike.agent.dao.AgentRepository;
import com.vike.agent.dao.SysRoleRepository;
import com.vike.agent.dao.SysUserRepository;
import com.vike.agent.entity.Agent;
import com.vike.agent.entity.SysRole;
import com.vike.agent.entity.SysUser;
import com.vike.agent.service.SummaryService;
import com.vike.agent.utils.EncryptUtils;
import com.vike.agent.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/11/1
 */
@Service
public class SummaryServiceImpl implements SummaryService {

    @Autowired
    AgentRepository agentRepository;
    @Autowired
    SysUserRepository sysUserRepository;
    @Autowired
    SysRoleRepository sysRoleRepository;

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
            if(sysUser.getRole().getId()==GloableConstant.AGENT_LEVEL_FIRST){
                Agent agentParent = agentRepository.findAgentBySysId(sysUser.getId()).get();
                if(agentParent.getId()!=agent.getParId()) return "非法请求";
            }
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
            if(sysUser.getRole().getId() == GloableConstant.AGENT_LEVEL_FIRST){
                Agent agentParent = agentRepository.findAgentBySysId(sysUser.getId()).get();
                if(agentParent.getId()!=agent.getParId()) return "非法请求";
            }
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

}
