package com.vike.agent.service;

import com.vike.agent.common.PageLimit;
import com.vike.agent.entity.Agent;
import com.vike.agent.entity.Bonus;
import com.vike.agent.entity.Statistical;
import com.vike.agent.entity.SysUser;
import com.vike.agent.vo.SummaryVo;
import org.springframework.data.domain.Page;

import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/11/1
 */
public interface SummaryService {

    Page<Agent> findByParId(SysUser sysUser, String mobile, PageLimit pageLimit);

    String changeAgent(SysUser sysUser, long id, int type);

    String editAgent(SysUser sysUser, long id, String nickName, String mobile, int ratio);

    void saveAgent(SysUser sysUserP, String nickName, String loginName, String mobile, Integer ratio, String password,long parSysId);

    Page<Bonus> findBonus(SysUser sysUser, String queryStr, String queryDate, PageLimit pageLimit);

    SummaryVo summary(SysUser sysUser);

    Page<Statistical> statistical(SysUser sysUser, PageLimit pageLimit);
}
