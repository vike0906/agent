package com.vike.agent.controller;

import com.vike.agent.common.GloableConstant;
import com.vike.agent.common.PageLimit;
import com.vike.agent.common.Response;
import com.vike.agent.entity.Agent;
import com.vike.agent.entity.SysUser;
import com.vike.agent.service.SummaryService;
import com.vike.agent.service.SystemService;
import com.vike.agent.utils.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Slf4j
@Controller
@RequestMapping("summary")
public class SummaryController {

    @Value("${system.queryUrl:queryUrl}")
    private String queryUrl;
    @Autowired
    SummaryService summaryService;
    @Autowired
    SystemService systemService;

    @GetMapping("bonus")
    public String bonus(){
        return "summary/bonus::bonus";
    }

    @GetMapping("agent")
    public String agent(ModelMap map, @RequestParam(required = false) String mobile, PageLimit pageLimit){
        Page<Agent> page = summaryService.findByParId(ShiroUtil.getUser(), mobile, pageLimit);
        map.put("page", page);
        map.put("queryUrl", queryUrl);
        map.put("mobile", mobile);
        return "summary/agent::agent";
    }

    @PostMapping("change-agent")
    @ResponseBody
    public Response changeUser(@RequestParam long agentId, @RequestParam int type){

        String s = summaryService.changeAgent(ShiroUtil.getUser(), agentId, type);

        if(s!=null){
            return new Response(Response.ERROR,s);
        }
        return new Response(Response.SUCCESS,"修改成功");
    }

    @PostMapping("add-agent")
    @ResponseBody
    public Response addAgent(@RequestParam String nickName,
                             @RequestParam String loginName,
                             @RequestParam String mobile,
                             @RequestParam Integer ratio,
                             @RequestParam String password){

        SysUser user = ShiroUtil.getUser();

        Optional<SysUser> op = systemService.findUsers(loginName);
        if(op.isPresent()){
            return new Response(Response.ERROR,"登录名已存在");
        }
        summaryService.saveAgent(user, nickName, loginName, mobile, ratio,password,user.getId());
        return new Response(Response.SUCCESS,"用户添加成功");
    }
    @PostMapping("edit-agent")
    @ResponseBody
    public Response editAgent(@RequestParam long id,
                              @RequestParam String nickName,
                              @RequestParam String mobile,
                              @RequestParam int ratio){
        String s = summaryService.editAgent(ShiroUtil.getUser(),id,nickName,mobile,ratio);
        if(s!=null){
            return new Response(Response.ERROR,s);
        }
        return new Response(Response.SUCCESS,"修改成功");
    }
    @GetMapping("withdraw")
    public String withdraw(){
        return "summary/withdraw::withdraw";
    }
}
