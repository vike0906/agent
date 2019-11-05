package com.vike.agent.component;

import com.vike.agent.common.GloableConstant;
import com.vike.agent.dao.AgentRepository;
import com.vike.agent.dao.StatisticalRepository;
import com.vike.agent.entity.Agent;
import com.vike.agent.entity.Statistical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: lsl
 * @createDate: 2019/11/3
 */
@Component
@Slf4j
public class BonusStatistical {

    /**运行间隔24小时*/
    private static final long PERIOD = 24 * 60 * 60 * 1000L;
    /**每次处理数据量*/
    private static final int ONCE_DEAL = 100;
    /**代理收益统计SQL*/
    private static String AGENT_STATISTICAL_SQL = "select IFNULL(sum(amount),0) amount from se_bonus where agent_id = %s and Date(create_time) = Date(DATE_SUB(NOW(),INTERVAL 1 DAY))";
    /**系统收益统计SQL*/
    private static String SYSTEM_STATISTICAL_SQL = "select IFNULL(sum(amount),0) amount from se_bonus where Date(create_time) = Date(DATE_SUB(NOW(),INTERVAL 1 DAY))";

    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private StatisticalRepository statisticalRepository;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private TransactionDefinition transactionDefinition;
    @PersistenceContext
    private EntityManager entityManager;

    public void statistical(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,5);
        calendar.set(Calendar.SECOND,0);
        if(System.currentTimeMillis() > calendar.getTimeInMillis()){
            calendar.add(Calendar.DATE,1);
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                all();
            }
        }, calendar.getTime(), PERIOD);
    }

    private void all(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        Date date = calendar.getTime();

        /**计算系统收益*/
        Query nativeQuery = entityManager.createNativeQuery(SYSTEM_STATISTICAL_SQL);
        BigDecimal amount = (BigDecimal)nativeQuery.getSingleResult();
        Statistical statistical = new Statistical();
        statistical.setAgentId(0L).setWxAmount(amount.intValue())
                .setTotalAmount(amount.intValue()).setStaDate(date);
        statisticalRepository.save(statistical);

        /**计算全部代理收益*/
        Page<Agent> page = agentRepository.findAll(PageRequest.of(0, ONCE_DEAL));
        iterator(page,date);
        if(page.getTotalPages()>1){
            for(int i =1; i<page.getTotalPages(); i++){
                Page<Agent> nextPage = agentRepository.findAll(PageRequest.of(i, ONCE_DEAL));
                iterator(nextPage,date);
            }
        }
    }

    /**批量插入*/
    private void iterator(Page<Agent> page, Date date){
        TransactionStatus transaction = platformTransactionManager.getTransaction(transactionDefinition);
        try {
            for(Agent agent:page.getContent()){
                Statistical statistical = query(agent);
                statistical.setStaDate(date);
                entityManager.persist(statistical);
            }
            entityManager.clear();
            entityManager.close();
            platformTransactionManager.commit(transaction);
        }catch (Exception e){
            log.error("处理统计数据异常：{}，{}，{}",page.getNumber(),page.getSize(),page.getTotalElements());
            platformTransactionManager.rollback(transaction);
        }
    }

    private Statistical query(Agent agent)throws RuntimeException{
        if(agent.getLevel() == GloableConstant.AGENT_SECOND_LEVEL || agent.getLevel()== GloableConstant.AGENT_FIRST_LEVEL){
            Query nativeQuery = entityManager.createNativeQuery(String.format(AGENT_STATISTICAL_SQL, agent.getId()));
            BigDecimal amount = (BigDecimal)nativeQuery.getSingleResult();
            Statistical statistical = new Statistical();
            statistical.setAgentId(agent.getId()).setWxAmount(amount.intValue())
                    .setTotalAmount(amount.intValue());
            return statistical;
        }else {
            throw new RuntimeException("代理级别参数错误");
        }
    }

}
