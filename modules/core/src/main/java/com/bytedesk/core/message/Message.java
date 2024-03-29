/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-27 16:12:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.utils.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "core_message")
public class Message extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 
     */
    @Column(unique = true, nullable = false)
    private String mid;

    /**
     * 
     * 消息类型
     */
    @Column(name = "by_type")
    private String type;

    /**
     * 
     */
    @Column(length = 512)
    private String content;

    /**
     * 
     */
    @Lob
    private String extra;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private String client;

    /**
     * message belongs to
     */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "thread_id", foreignKey = @ForeignKey(name = "none", value
    // = ConstraintMode.NO_CONSTRAINT))
    // private Thread thread;

    @JsonIgnore
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "core_message_threads", 
        joinColumns = @JoinColumn(name = "message_id"), 
        inverseJoinColumns = @JoinColumn(name = "thread_id"))
    private List<Thread> threads = new ArrayList<>();

    /**
     * sender
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;

}
