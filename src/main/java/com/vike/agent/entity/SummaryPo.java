package com.vike.agent.entity;


import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @Author: lsl
 * @CreateDate: 2019/10/29
 */
@Data
@Entity
public class SummaryPo {

    @Id
    @Column(name = "amount")
    private long amount;

    @Column(name = "count")
    private long count;


}
