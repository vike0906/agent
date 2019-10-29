package com.vike.agent.dao;

import com.vike.agent.entity.SysRole;
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
public interface SysRoleRepository extends JpaRepository<SysRole,Long>, JpaSpecificationExecutor<SysRole> {

    @Query(value = "select * from sys_role order by id",nativeQuery = true)
    List<SysRole> findAllItem();

}
