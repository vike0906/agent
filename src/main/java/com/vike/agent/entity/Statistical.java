package com.vike.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/11/1
 */
@Entity
@Table(name = "se_statistical")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Statistical {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "agent_id")
    private long agentId;
    @Column(name = "wx_amount")
    private int wxAmount;
    @Column(name = "total_amount")
    private int totalAmount;
    @Column(name = "sta_date")
    private Date staDate;
    @Column(name = "create_time")
    private Date createTime;
}
