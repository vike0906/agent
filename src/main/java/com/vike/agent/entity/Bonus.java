package com.vike.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/11/3
 */
@Entity
@Table(name = "se_bonus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Bonus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "agent_id")
    private long agentId;
    @Column(name = "parent_agent_id")
    private long parentAgentId;
    @Column(name = "agent_mobile")
    private String agentMobile;
    @Column(name = "agent_tag")
    private String agentTag;
    @Column(name = "client_name")
    private String clientName;
    @Column(name = "client__mobile")
    private String clientMobile;
    @Column(name = "business")
    private String business;
    @Column(name = "amount")
    private int amount;
    @Column(name = "level")
    private int level;
    @Column(name = "status")
    private int status;
    @Column(name = "create_time")
    private Date createTime;
}
