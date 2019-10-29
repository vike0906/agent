package com.vike.agent.service.serviceImpl;

import com.vike.agent.common.GloableConstant;
import com.vike.agent.common.PageLimit;
import com.vike.agent.dao.SysPermissionRepository;
import com.vike.agent.dao.SysRolePermissionRepository;
import com.vike.agent.dao.SysRoleRepository;
import com.vike.agent.dao.SysUserRepository;
import com.vike.agent.entity.SysPermission;
import com.vike.agent.entity.SysRole;
import com.vike.agent.entity.SysRolePermission;
import com.vike.agent.entity.SysUser;
import com.vike.agent.service.SystemService;
import com.vike.agent.utils.EncryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Service
@Slf4j
public class SystemServiceImpl implements SystemService {

    @Autowired
    SysUserRepository sysUserRepository;
    @Autowired
    SysRoleRepository sysRoleRepository;
    @Autowired
    SysRolePermissionRepository sysRolePermissionRepository;
    @Autowired
    SysPermissionRepository sysPermissionRepository;

    @Override
    public Page<SysUser> findUsers(PageLimit pageLimit) {

        return sysUserRepository.findAll(pageLimit.page("createTime"));
    }

    @Override
    public SysUser save(String name, String loginName, String password, long roleId) {
        SysUser sysUser = new SysUser();
        sysUser.setName(name).setLoginName(loginName)
                .setPassword(EncryptUtils.MD5(password))
                .setStatus(GloableConstant.CANCEL_STATUS);
        Optional<SysRole> op = sysRoleRepository.findById(roleId);
        sysUser.setRole(op.get());
        SysUser save = sysUserRepository.save(sysUser);
        return save;
    }

    @Override
    public String changeUser(long id, int type) {
        Optional<SysUser> op = sysUserRepository.findById(id);
        if(op.isPresent()){
            SysUser sysUser = op.get();
            if(type==1){
                if(sysUser.getStatus()!=GloableConstant.NORMALL_STATUS) return "状态错误";
                sysUser.setStatus(GloableConstant.CANCEL_STATUS);
                sysUserRepository.save(sysUser);
                return null;
            }else{
                if(sysUser.getStatus()!=GloableConstant.CANCEL_STATUS) return "状态错误";
                sysUser.setStatus(GloableConstant.NORMALL_STATUS);
                sysUserRepository.save(sysUser);
                return null;
            }
        }
        return "用户不存在";
    }

    @Override
    public Optional<SysUser> findUsers(String loginName) {
        return sysUserRepository.findByLoginName(loginName);
    }

    @Override
    public Page<SysRole> findRoles(PageLimit pageLimit) {

        return sysRoleRepository.findAll(pageLimit.page("createTime"));
    }

    @Override
    public List<SysRole> findRoles() {

        return sysRoleRepository.findAllItem();
    }

    @Override
    @Transactional
    public SysRole saveRole(String name) {
        SysRole sysRole = new SysRole();
        sysRole.setName(name);
        SysRole save = sysRoleRepository.save(sysRole);
        List<SysPermission> list = sysPermissionRepository.findAll();
        List<SysRolePermission> list1 = new ArrayList<>(list.size());
        for(SysPermission s:list){
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setRoleId(save.getId()).setPermissionId(s.getId()).setStatus(GloableConstant.CANCEL_STATUS);
            list1.add(sysRolePermission);
        }
        sysRolePermissionRepository.saveAll(list1);
        return save;
    }


    @Override
    public Page<SysPermission> findPermissions(PageLimit pageLimit) {
        Sort sort = Sort.by(Sort.Direction.ASC,"sort").and(Sort.by(Sort.Direction.ASC,"createTime"));
        return sysPermissionRepository.findAll(pageLimit.page(sort));

    }

    @Override
    public List<SysPermission> findSysPermission() {
        return sysPermissionRepository.findAllByParentId();
    }

    @Override
    @Transactional
    public SysPermission savePermission(String name, String url, long parentId) {
        Optional<SysPermission> op = sysPermissionRepository.findById(parentId);
        if(op.isPresent()){
            SysPermission sysPermission = op.get();
            SysPermission sysPermissionNew = new SysPermission();
            sysPermissionNew.setName(name).setUrl(url).setParentId(sysPermission.getId())
                    .setLevel(sysPermission.getLevel()).setSort(sysPermission.getSort());
            return sysPermissionRepository.save(sysPermissionNew);
        }
        return null;
    }

    @Override
    @Transactional
    public void deletePermission(long id) {
        sysPermissionRepository.deleteById(id);
    }
}
