package com.vike.agent.dao;

import com.vike.agent.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author: lsl
 * @createDate: 2019/11/5
 */
@Repository
public interface WithdrawRepository extends JpaRepository<Withdraw,Long>,JpaSpecificationExecutor<Withdraw> {

}
