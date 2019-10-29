package com.vike.agent.dao;

import com.vike.agent.entity.SysPermission;
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
public interface SysPermissionRepository extends JpaRepository<SysPermission,Long>, JpaSpecificationExecutor<SysPermission> {

    @Query(value = "select * from sys_permission where parent_id = 0 order by sort",nativeQuery = true)
    List<SysPermission> findAllByParentId();
}
