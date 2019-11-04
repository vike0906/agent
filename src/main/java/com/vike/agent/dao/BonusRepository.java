package com.vike.agent.dao;

import com.vike.agent.entity.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * @author: lsl
 * @createDate: 2019/11/1
 */
@Repository
public interface BonusRepository extends JpaRepository<Bonus,Long>, JpaSpecificationExecutor<Bonus> {

}
