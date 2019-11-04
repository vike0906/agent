package com.vike.agent.controller;

import com.vike.agent.common.Response;
import com.vike.agent.component.SystemCache;
import com.vike.agent.config.CustomizeShiroException;
import com.vike.agent.dao.SysUserRepository;
import com.vike.agent.entity.SysPermission;
import com.vike.agent.entity.SysUser;
import com.vike.agent.service.SummaryService;
import com.vike.agent.utils.EncryptUtils;
import com.vike.agent.utils.ShiroUtil;
import com.vike.agent.vo.SummaryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Slf4j
@Controller
public class ViewController {

    @Value("${system.loginTimeOut:1800}")
    private Long loginTimeOut;

    @Autowired
    SysUserRepository sysUserRepository;
    @Autowired
    SummaryService summaryService;

    @RequestMapping("/")
    public String root(ModelMap map){
        return index(map);
    }

    @GetMapping("index")
    public String index(ModelMap map){
        SysUser user = ShiroUtil.getUser();
        List<SysPermission> menus = SystemCache.MENU_CACHE.get(user.getRole().getName());
        SummaryVo summary = summaryService.summary(ShiroUtil.getUser());
        map.addAttribute("userName",user.getName());
        map.addAttribute("menus",menus);
        map.addAttribute("summary",summary);
        return "index";
    }

    @GetMapping("login")
    public String login(){
        return "login";
    }

    @PostMapping("/user/login")
    @ResponseBody
    public Response login(String loginName, String password){
        loginName = loginName.trim();
        password = password.trim();
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginName, EncryptUtils.MD5(password));
        try {
            subject.login(token);
            log.info("登陆成功：{}", loginName);
            subject.getSession().setTimeout(loginTimeOut*1000);
            return new Response(Response.SUCCESS, "登录成功");
        }catch (CustomizeShiroException e){
            return new Response(Response.ERROR, e.getMessage());
        }catch (Exception e){
            return new Response(Response.ERROR, "用户名或密码错误");
        }
    }

    @GetMapping("logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "login";
    }

    @PostMapping("/change/password")
    @ResponseBody
    public Response changePassword(String oldPassword, String newPassword){
        oldPassword = oldPassword.trim();
        newPassword = newPassword.trim();
        SysUser user = ShiroUtil.getUser();
        if(EncryptUtils.MD5(oldPassword).equals((user.getPassword()))){
            user.setPassword(EncryptUtils.MD5(newPassword));
            sysUserRepository.save(user);
            return new Response(Response.SUCCESS,"修改成功");
        }
        return new Response(Response.ERROR,"密码错误");

    }

    @GetMapping("view/{url1}/{url2}")
    public String view(ModelMap map, @PathVariable String url1, @PathVariable String url2){
        //TODO 获取当前用户拥有的菜单，然后筛选当前菜单
        String url = "/"+url1+"/"+url2;
        SysUser user = ShiroUtil.getUser();
        List<SysPermission> menus = SystemCache.MENU_CACHE.get(user.getRole().getName()+url);
        map.addAttribute("url",url);
        map.addAttribute("userName",user.getName());
        map.addAttribute("menus",menus);
        return "home";
    }

    @GetMapping("view/summary/summary/index")
    public String summaryIndex(ModelMap map){
        SysUser user = ShiroUtil.getUser();
        List<SysPermission> menus = SystemCache.MENU_CACHE.get(user.getRole().getName()+"/summary/summary/index");
        SummaryVo summary = summaryService.summary(user);
        map.addAttribute("userName",user.getName());
        map.addAttribute("menus",menus);
        map.addAttribute("summary",summary);
        return "index";
    }

}
