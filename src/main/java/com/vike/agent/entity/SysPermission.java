package com.vike.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @Author: lsl
 * @CreateDate: 2019/10/29
 */
@Entity
@Table(name = "sys_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "icon")
    private String icon;

    @Column(name = "url")
    private String url;

    @Column(name = "level")
    private int level;

    @Column(name = "sort")
    private int sort;

    @Column(name = "parent_id")
    private long parentId;

    @Column(name = "create_time")
    private Date createTime;

    @Transient
    private List<SysPermission> sub;

    @Transient
    private int isActive;

}
