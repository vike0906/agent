package com.vike.agent.controller;

import com.vike.agent.common.PageLimit;
import com.vike.agent.common.Response;
import com.vike.agent.entity.Agent;
import com.vike.agent.entity.Bonus;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Slf4j
@Controller
@RequestMapping("summary")
public class SummaryController {

    private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${system.queryUrl:queryUrl}")
    private String queryUrl;

    @Autowired
    SummaryService summaryService;
    @Autowired
    SystemService systemService;


    @GetMapping("bonus")
    public String bonus(ModelMap map, @RequestParam(required = false) String queryStr, @RequestParam(required = false) String queryDate, PageLimit pageLimit) throws ParseException {
        if(StringUtils.isEmpty(queryDate)){
            /** 默认查询一周的数据 */
            long currentTimeMillis = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentTimeMillis);
            calendar.add(Calendar.DATE,-7);
            calendar.getTime();
            queryDate = sd.format(new Date(currentTimeMillis))+" 至 "+sd.format(calendar.getTime());
        }
        Page<Bonus> page = summaryService.findBonus(ShiroUtil.getUser(), queryStr, queryDate, pageLimit);
        map.put("page", page);
        map.put("queryStr", queryStr);

        map.put("queryDate", queryDate);
        return "summary/bonus::bonus";
    }

    @GetMapping("agent")
    public String agent(ModelMap map, @RequestParam(required = false) String queryStr, PageLimit pageLimit){
        Page<Agent> page = summaryService.findByParId(ShiroUtil.getUser(), queryStr, pageLimit);
        map.put("page", page);
        map.put("queryUrl", queryUrl);
        map.put("queryStr", queryStr);
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
