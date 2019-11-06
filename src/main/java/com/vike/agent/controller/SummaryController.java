package com.vike.agent.controller;

import com.vike.agent.common.BusinessException;
import com.vike.agent.common.GloableConstant;
import com.vike.agent.common.PageLimit;
import com.vike.agent.common.Response;
import com.vike.agent.entity.*;
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

import javax.naming.AuthenticationException;
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
            queryDate = sd.format(calendar.getTime())+" 至 "+sd.format(new Date(currentTimeMillis));
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
    public String withdraw(ModelMap map, @RequestParam(required = false) String queryStr, @RequestParam(required = false) Integer queryType, PageLimit pageLimit){
        SysUser user = ShiroUtil.getUser();
        if(queryType==null){
            queryType=0;
        }
        int isAudit = 0;
        if(user.getRole().getId()==GloableConstant.ADMIN_ID||user.getRole().getId()==GloableConstant.OP_ID){
            isAudit = 1;
        }
        Page<Withdraw> page = summaryService.findWithdraw(user, queryStr, queryType, pageLimit);
        map.put("page", page);
        map.put("queryStr", queryStr);
        map.put("queryType", queryType);
        map.put("isAudit",isAudit);
        return "summary/withdraw::withdraw";
    }

    @PostMapping("add-withdraw")
    @ResponseBody
    public Response addWithdraw(@RequestParam String account,
                                @RequestParam String name,
                                @RequestParam String type,
                                @RequestParam Double amount,
                                @RequestParam String remark){
        SysUser user = ShiroUtil.getUser();
        if(user.getRole().getId()!=GloableConstant.AGENT_LEVEL_FIRST) return new Response(Response.ERROR,"仅一级代理可申请提现");
        amount = amount*100;
        try {
            Withdraw withdraw = summaryService.saveWithdraw(user, account, name, type, amount.intValue(), remark);
            return new Response(Response.SUCCESS, withdraw.getOrderNo());
        }catch (BusinessException e){
            return new Response(Response.ERROR,e.getMessage());
        }
    }

    @PostMapping("audit-withdraw")
    @ResponseBody
    public Response addWithdraw(@RequestParam Long id, @RequestParam Integer type, @RequestParam String remark){
        SysUser user = ShiroUtil.getUser();
        if(user.getRole().getId()!=GloableConstant.ADMIN_ID && user.getRole().getId()!=GloableConstant.OP_ID) {
            return new Response(Response.ERROR,"权限不足");
        }
        String audit = summaryService.audit(id, user.getId(), type, remark);
        if(audit==null){
            return new Response(Response.SUCCESS,"审核成功");
        }
        return new Response(Response.ERROR, audit);
    }

    @GetMapping("statistical")
    public String statistical(ModelMap map, PageLimit pageLimit){
        Page<Statistical> page = summaryService.statistical(ShiroUtil.getUser(), pageLimit);
        map.addAttribute("page", page);
        return "summary/statistical::statistical";
    }
}
