package com.vike.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/11/5
 */
@Entity
@Table(name = "se_withdraw")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Withdraw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "order_no")
    private String orderNo;
    @Column(name = "agent_id")
    private long agentId;
    @Column(name = "agent_info")
    private String agentInfo;
    private String account;
    private String name;
    private String type;
    private int amount;
    private int charge;
    private int balance;
    private String remark;
    private int status;
    @Column(name = "audit_id")
    private long audit_id;
    private String audit;
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "create_time")
    private Date createTime;
}
