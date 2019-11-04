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
@Table(name = "wx_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Order {

    @Id
    @Column(name = "order_no")
    private long orderNo;
    @Column(name = "fans_id")
    private long fansId;
    @Column(name = "agent_tag")
    private String agentTag;
    @Column(name = "name")
    private String name;
    @Column(name = "id_no")
    private String idNo;
    @Column(name = "credit_card_no")
    private String creditCardNo;
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "price")
    private int price;
    @Column(name = "amount")
    private int amount;
    @Column(name = "order_status")
    private int orderStatus;
    @Column(name = "bonus_status")
    private int bonusStatus;
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "create_time")
    private Date createTime;
}
