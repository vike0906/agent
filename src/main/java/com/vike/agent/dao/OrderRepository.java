package com.vike.agent.dao;

import com.vike.agent.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author: lsl
 * @createDate: 2019/11/3
 */
public interface OrderRepository extends JpaRepository<Order,Long>, JpaSpecificationExecutor<Order> {

    List<Order> findOrdersByBonusStatusOrderByCreateTimeDesc(int bonusStatus);
}
