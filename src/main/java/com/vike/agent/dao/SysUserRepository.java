package com.vike.agent.dao;

import com.vike.agent.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser,Long>, JpaSpecificationExecutor<SysUser> {

    Optional<SysUser> findByLoginName(String loginName);
}
