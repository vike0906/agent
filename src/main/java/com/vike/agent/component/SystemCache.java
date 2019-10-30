package com.vike.agent.component;

import com.vike.agent.common.GloableConstant;
import com.vike.agent.dao.SysPermissionRepository;
import com.vike.agent.dao.SysRolePermissionRepository;
import com.vike.agent.dao.SysRoleRepository;
import com.vike.agent.entity.SysPermission;
import com.vike.agent.entity.SysRole;
import com.vike.agent.entity.SysRolePermission;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author: lsl
 * @createDate: 2019/10/31
 */
@Component
public class SystemCache {

    public static Map<String,List<SysPermission>> MENU_CACHE = new HashMap<>();

    @Autowired
    SysRoleRepository sysRoleRepository;
    @Autowired
    SysRolePermissionRepository sysRolePermissionRepository;
    @Autowired
    SysPermissionRepository sysPermissionRepository;

    /**更新系统菜单*/
    public void updateMenuCache(){

        List<SysRole> allRole = sysRoleRepository.findAll();
        List<SysRolePermission> allRolePermission = sysRolePermissionRepository.findAll();
        List<SysPermission> allPermission = sysPermissionRepository.findAll();

        for(SysRole role:allRole){

            List<SysRolePermission> list1 = new ArrayList<>();
            List<SysPermission> rolePermissions = new ArrayList<>();

            for(SysRolePermission rolePermission:allRolePermission){
                if(rolePermission.getStatus()==GloableConstant.NORMALL_STATUS&&role.getId()==rolePermission.getRoleId()){
                    list1.add(rolePermission);
                }
            }

            for(SysRolePermission rolePermission:list1){
                for(SysPermission permission:allPermission){
                    if(permission.getId()==rolePermission.getPermissionId()){
                        rolePermissions.add(permission);
                    }
                }
            }

            MENU_CACHE.put(role.getName(),builderMenus(rolePermissions,""));

            for(SysPermission permission:rolePermissions){
                MENU_CACHE.put(role.getName()+permission.getUrl(),builderMenus(rolePermissions,permission.getUrl()));
            }
        }
    }

    private List<SysPermission> builderMenus(List<SysPermission> menus, String url){
        AtomicLong parentId = new AtomicLong(0L);
        List<SysPermission> collect = menus.stream().sorted(Comparator.comparingInt(SysPermission::getSort)).map(a -> {
            SysPermission sysPermission = new SysPermission();
            BeanUtils.copyProperties(a,sysPermission);
            if (url.equals(a.getUrl())) {
                sysPermission.setIsActive(1);
                parentId.set(a.getParentId());
            }
            return sysPermission;
        }).collect(Collectors.toList());
        Map<Long, List<SysPermission>> map = collect.stream().collect(Collectors.groupingBy(SysPermission::getParentId));
        List<SysPermission> collect1 = collect.stream().filter(a -> a.getParentId() == 0).map(b -> {
            SysPermission sysPermission = new SysPermission();
            BeanUtils.copyProperties(b,sysPermission);
            if (b.getId() == parentId.get()) {
                sysPermission.setIsActive(1);
            }
            sysPermission.setSub(map.get(b.getId()));
            return sysPermission;
        }).collect(Collectors.toList());
        return collect1;
    }
}
