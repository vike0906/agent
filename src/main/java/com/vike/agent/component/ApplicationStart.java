package com.vike.agent.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * @Author: lsl
 * @Date: Create in 2018/11/16
 */
@Component
@Slf4j
public class ApplicationStart implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    SystemCache systemCache;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        log.info("应用已启动完成");

        /**初始化角色路径对应菜单*/
        systemCache.updateMenuCache();
    }

}
