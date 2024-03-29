package com.vike.agent.service;

import com.vike.agent.common.PageLimit;
import com.vike.agent.entity.SysPermission;
import com.vike.agent.entity.SysPermissionVo;
import com.vike.agent.entity.SysRole;
import com.vike.agent.entity.SysUser;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;


/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
public interface SystemService {

    Page<SysUser> findUsers(PageLimit pageLimit);

    SysUser save(String name, String loginName, String password, long roleId);

    String changeUser(long id, int type);

    Optional<SysUser> findUsers(String loginName);

    Page<SysRole> findRoles(PageLimit pageLimit);

    List<SysRole> findRoles();

    SysRole saveRole(String name);

    List<SysPermissionVo> findPermissionForEditRole(long roleId);

    String editRole(long id, int type);

    Page<SysPermission> findPermissions(PageLimit pageLimit);

    List<SysPermission> findSysPermission();

    SysPermission savePermission(String name, String url, long parentId);

    void deletePermission(long id);
}
