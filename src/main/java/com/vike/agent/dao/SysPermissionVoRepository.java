package com.vike.agent.dao;

import com.vike.agent.entity.SysPermissionVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Repository
public interface SysPermissionVoRepository extends JpaRepository<SysPermissionVo,Long>, JpaSpecificationExecutor<SysPermissionVo> {

    @Query(value = "select b.*, a.status, a.id rp_id from sys_role_permission a left join sys_permission b on a.permission_id = b.id where a.role_id=?1 order by b.sort",nativeQuery = true)
    List<SysPermissionVo> findAllByRoleId(long roleId);
}
