package com.vike.agent.dao;

import com.vike.agent.entity.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission,Long>, JpaSpecificationExecutor<SysPermission> {

}
