package com.vike.agent.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * @author: lsl
 * @createDate: 2019/10/29
 */
@Slf4j
@Controller
@RequestMapping("summary")
public class SummaryController {

    @GetMapping("income")
    public String users(ModelMap map){
        return "summary/income::income";

    }
}
