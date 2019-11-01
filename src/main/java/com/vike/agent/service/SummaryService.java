package com.vike.agent.service;

import com.vike.agent.common.PageLimit;
import com.vike.agent.entity.Agent;
import com.vike.agent.entity.SysUser;
import org.springframework.data.domain.Page;

/**
 * @author: lsl
 * @createDate: 2019/11/1
 */
public interface SummaryService {

    Page<Agent> findByParId(SysUser sysUser, String mobile, PageLimit pageLimit);

    String changeAgent(SysUser sysUser, long id, int type);

    void saveAgent(String nickName, String loginName, String mobile, Integer ratio, String password,long parSysId);
}
