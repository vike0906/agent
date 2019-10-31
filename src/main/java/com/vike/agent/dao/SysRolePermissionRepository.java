package com.vike.agent.dao;

import com.vike.agent.entity.SysRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission,Long>, JpaSpecificationExecutor<SysRolePermission> {

    List<SysRolePermission> findAllByRoleId(long roleId);

    List<SysRolePermission> findAllByPermissionId(long permissionId);
}
