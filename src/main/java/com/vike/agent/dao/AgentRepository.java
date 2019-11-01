package com.vike.agent.dao;

import com.vike.agent.entity.Agent;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/11/1
 */
@Repository
public interface AgentRepository extends JpaRepository<Agent,Long>, JpaSpecificationExecutor<Agent> {

    Optional<Agent> findAgentBySysId(long sysId);

    Optional<Agent> findAgentByUrl(String url);

    List<Agent> findAgentByParId(long parentId);
}
