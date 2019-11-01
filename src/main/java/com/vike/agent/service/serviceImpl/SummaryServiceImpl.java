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
    public void saveAgent(String nickName, String loginName, String mobile, Integer ratio, String password,long parSysId) {
        Agent agentParent = agentRepository.findAgentBySysId(parSysId).get();
        /**构造SysUser并添加*/
        SysUser sysUser = new SysUser();
        SysRole sysRole = sysRoleRepository.findById(GloableConstant.AGENT_LEVEL_SECOND).get();
        sysUser.setName(nickName).setLoginName(loginName)
                .setStatus(GloableConstant.CANCEL_STATUS)
                .setPassword(EncryptUtils.MD5(password))
                .setRole(sysRole);
        SysUser save = sysUserRepository.save(sysUser);
        /**构造Agent并添加*/
        Agent agent = new Agent();
        agent.setNickName(nickName).setLoginName(loginName)
                .setSysId(save.getId()).setParId(agentParent.getId()).setLevel(2)
                .setStatus(GloableConstant.CANCEL_STATUS).setMobile(mobile)
                .setRatio(ratio).setUrl(RandomUtil.UUID());
        agentRepository.save(agent);
    }

}
