package com.vike.agent.controller;

import com.vike.agent.entity.SysPermission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Controller
public class ViewController {

    @GetMapping("view/{url1}/{url2}")
    public String view(ModelMap map, @PathVariable String url1, @PathVariable String url2){
        //TODO 获取当前用户拥有的菜单，然后筛选当前菜单
        String url = "/"+url1+"/"+url2;
//        List<SysPermission> menus = null;
//        List<SysPermission> sysPermissions = builderMenus(menus, url);
//        map.addAttribute("menus",sysPermissions);
        map.addAttribute("url",url);
        return "index";
    }

    @GetMapping("view/main")
    public String view(){
        return "module::main";
    }

    public List<SysPermission> builderMenus(List<SysPermission> menus, String url){
        AtomicLong parentId = new AtomicLong(0L);
        List<SysPermission> collect = menus.stream().sorted(Comparator.comparingInt(SysPermission::getSort)).map(a -> {
            if (url.equals(a.getUrl())) {
                a.setIsActive(1);
            }
            parentId.set(a.getParentId());
            return a;
        }).collect(Collectors.toList());
        Map<Long, List<SysPermission>> map = collect.stream().collect(Collectors.groupingBy(SysPermission::getParentId));
        List<SysPermission> collect1 = collect.stream().filter(a -> a.getParentId() == 0).map(b -> {
            if (b.getId() == parentId.get()) {
                b.setIsActive(1);
            }
            b.setSub(map.get(b.getId()));
            return b;
        }).collect(Collectors.toList());
        return collect1;
    }
}
