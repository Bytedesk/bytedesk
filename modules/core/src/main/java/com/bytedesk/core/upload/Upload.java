/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-16 10:46:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:10:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.utils.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Upload
 *
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "core_upload")
public class Upload extends AbstractEntity {
    
    private static final long serialVersionUID = 1L;

    // @NotBlank
    // @Column(name = "uuid", unique = true, nullable = false)
    // private String uid;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件大小
     */
    private String size;

    /**
     * URL网址
     */
    private String url;

    /**
     * 类型：图片、文件、语音、视频等
     */
    @Column(name = "by_type")
    private String type;

    /**
     *  备注
     */
    private String note;

    /**
     * 来源客户端
     */
    private String client;

    /**
     * 所属用户
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    
}
