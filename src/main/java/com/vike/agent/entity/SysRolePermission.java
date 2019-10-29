package com.vike.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: lsl
 * @CreateDate: 2019/10/29
 */
@Entity
@Table(name = "sys_role_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysRolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "role_id")
    private long roleId;

    @Column(name = "permission_id")
    private long permissionId;

    @Column(name = "status")
    private int status;

    @Column(name = "create_time")
    private Date createTime;

}
