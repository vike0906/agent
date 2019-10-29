package com.vike.agent.controller;

import com.vike.agent.common.PageLimit;
import com.vike.agent.common.Response;
import com.vike.agent.entity.SysPermission;
import com.vike.agent.entity.SysRole;
import com.vike.agent.entity.SysUser;
import com.vike.agent.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Slf4j
@Controller
@RequestMapping("system")
public class SystemController {

    @Autowired
    SystemService systemService;

    @GetMapping("users")
    public String users(ModelMap map, PageLimit pageLimit){

        Page<SysUser> page = systemService.findUsers(pageLimit);
        List<SysRole> roles = systemService.findRoles();
        map.addAttribute("page",page);
        map.addAttribute("roles",roles);
        return "sys/user::user";
    }

    @PostMapping("add-user")
    @ResponseBody
    public Response addUser(@RequestParam(required = false)Long id,
                          @RequestParam String name,
                          @RequestParam String loginName,
                          @RequestParam String password,
                          @RequestParam long roleId){
        if(id==null){
            Optional<SysUser> op = systemService.findUsers(loginName);
            if(op.isPresent()){
                return new Response(Response.ERROR,"用户名已存在");
            }
            systemService.save(name, loginName, password, roleId);
            return new Response(Response.SUCCESS,"用户添加成功");
        }
        return new Response(Response.ERROR,"未添加");
    }

    @PostMapping("change-user")
    @ResponseBody
    public Response changeUser(@RequestParam long userId, @RequestParam int type){
        String s = systemService.changeUser(userId, type);
        if(s!=null){
            return new Response(Response.ERROR,s);
        }
        return new Response(Response.SUCCESS,"修改成功");
    }

    @GetMapping("roles")
    public String roles(ModelMap map, PageLimit pageLimit){
        Page<SysRole> page = systemService.findRoles(pageLimit);
        map.addAttribute("page",page);
        return "sys/role::role";
    }

    @GetMapping("permissions")
    public String permissions(ModelMap map, PageLimit pageLimit){
        Page<SysPermission> page = systemService.findPermissions(pageLimit);
        map.addAttribute("page",page);
        return "sys/permission::permission";
    }
}
