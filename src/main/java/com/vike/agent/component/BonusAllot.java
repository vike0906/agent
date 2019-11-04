package com.vike.agent.component;

import com.vike.agent.common.GloableConstant;
import com.vike.agent.dao.AgentRepository;
import com.vike.agent.dao.BonusRepository;
import com.vike.agent.dao.OrderRepository;
import com.vike.agent.entity.Agent;
import com.vike.agent.entity.Bonus;
import com.vike.agent.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/11/3
 */
@Component
@Slf4j
public class BonusAllot {

    /**订单状态--未分红*/
    private static final int ORDER_UN_ALLOT_STATUS = 2;
    /**订单状态--已分红*/
    private static final int ORDER_IS_ALLOT_STATUS = 3;
    /**业务名称*/
    private static final String BUSINSESS_NAME = "征信查询";

    @Value("${system.allotRefreshTime:3600}")
    private long allotRefreshTime;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AgentRepository agentRepository;
    @Autowired
    BonusRepository bonusRepository;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private TransactionDefinition transactionDefinition;

    /**
     * Specification<Order> specification = (Specification<Order>)(root,query,builder)->{
     *  query.where(builder.and(builder.equal(root.get("bonusStatus"),ORDER_UN_ALLOT_STATUS)));
     *  return query.getRestriction();
     *  };
     */

    public void allot(){
        new Thread(()->{
            while (true){
                try {
                    Thread.sleep(allotRefreshTime*1000);
                    /**获取全部未分配订单*/
                    List<Order> unAllotOrders = orderRepository.findOrdersByBonusStatusOrderByCreateTimeDesc(ORDER_UN_ALLOT_STATUS);
                    for(Order order:unAllotOrders){
                        TransactionStatus transaction = platformTransactionManager.getTransaction(transactionDefinition);
                        try {
                            allot(order);
                            platformTransactionManager.commit(transaction);
                        }catch (Exception e){
                            log.error("订单分佣失败：订单号：{}，订单TAG：{}，电话：{}",order.getIdNo(),order.getAgentTag(),order.getMobile());
                            platformTransactionManager.rollback(transaction);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            }
        ).start();

    }

    private void allot(Order order){
        Optional<Agent> op = agentRepository.findAgentByUrl(order.getAgentTag());//获取二级代理信息

        if(op.isPresent()){
            Agent agent = op.get();
            Agent agentParent = agentRepository.findById(agent.getParId()).get();//获取上级代理

            int amount = order.getAmount();
            int amountParentRatio = (amount*agentParent.getRatio())/100;
            int amountSubRatio = (amountParentRatio*agent.getRatio())/100;

            //分配一级代理佣金并记录
            agentRepository.updateAgentAddAmount(agentParent.getId(), amountParentRatio);//一级代理
            Bonus bonusParent = new Bonus();
            bonusParent.setAgentId(agentParent.getId()).setParentAgentId(0L).setAgentMobile(agentParent.getMobile())
                    .setAgentTag(order.getAgentTag()).setClientName(order.getName()).setClientMobile(order.getMobile())
                    .setBusiness(BUSINSESS_NAME).setLevel(1).setStatus(GloableConstant.NORMALL_STATUS);
            bonusRepository.save(bonusParent);

            //分配二级代理佣金并记录
            agentRepository.updateAgentAddAmount(agent.getId(), amountSubRatio);//二级代理
            Bonus bonus = new Bonus();
            bonus.setAgentId(agent.getId()).setParentAgentId(agentParent.getId()).setAgentMobile(agent.getMobile())
                    .setAgentTag(order.getAgentTag()).setClientName(order.getName()).setClientMobile(order.getMobile())
                    .setBusiness(BUSINSESS_NAME).setLevel(2).setStatus(GloableConstant.NORMALL_STATUS);
            bonusRepository.save(bonus);

        }else {
            //TODO  写入分佣记录，收益人为系统，记录tag
            Bonus bonusSystem = new Bonus();
            bonusSystem.setAgentId(0L).setParentAgentId(0L).setAgentMobile("system")
                    .setAgentTag(order.getAgentTag()).setClientName(order.getName()).setClientMobile(order.getMobile())
                    .setBusiness(BUSINSESS_NAME).setLevel(0).setStatus(GloableConstant.NORMALL_STATUS);
            bonusRepository.save(bonusSystem);
        }

        //更改订单状态
        Order order1 = orderRepository.findById(order.getOrderNo()).get();
        order1.setBonusStatus(ORDER_IS_ALLOT_STATUS);
        orderRepository.save(order1);
    }
}
