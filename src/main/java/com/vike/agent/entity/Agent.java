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
@Table(name = "se_agent")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "sys_id")
    private long sysId;
    @Column(name = "par_id")
    private long parId;
    @Column(name = "login_name")
    private String loginName;
    @Column(name = "nick_name")
    private String nickName;
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "url")
    private String url;
    @Column(name = "ratio")
    private int ratio;
    @Column(name = "amount")
    private int amount;
    @Column(name = "level")
    private int level;
    @Column(name = "status")
    private int status;
    @Column(name = "create_time")
    private Date createTime;
}
