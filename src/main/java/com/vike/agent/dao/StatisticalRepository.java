package com.vike.agent.dao;

import com.vike.agent.entity.Statistical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author: lsl
 * @createDate: 2019/11/1
 */
@Repository
public interface StatisticalRepository extends JpaRepository<Statistical,Long>, JpaSpecificationExecutor<Statistical> {

}
