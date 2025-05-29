/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-29 16:58:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesa
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
// import java.time.ZonedDateTime;
// import java.time.LocalDateTime;

/**
 * Date -> ZonedDateTime ？
 * spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
 * spring.jackson.time-zone=GMT+8
 * 
 * //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = timezone)
 * config in properties
 * @see https://docs.spring.io/spring-data/jpa/reference/auditing.html
 * @author im.bytedesk.com
 */
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityNoOrg implements Serializable {

    // @Value("${bytedesk.timezone}")
    // private static final String timezone = "GMT+8";

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @NotBlank 在应用层（业务逻辑或表单验证）确保uid字段在提交时必须是非空且去除空格后有实际内容的。
	// nullable = false 通过@Column注解告知JPA，数据库中的uuid列不允许NULL值，这是一个数据库级别的约束
	@NotBlank(message = "uid is required")
	@Column(name = "uuid", unique = true, nullable = false)
	private String uid;
    
    // 乐观锁版本字段，每次更新时版本号加1
    @Version
    private int version;
    
     // 在配置文件中存储时区信息
    // 数据库DDL中： created_at timestamp(6) without time zone,
    // @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    // @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

	@Builder.Default
	@Column(name = "is_deleted")
	private boolean deleted = false;

    // user uid
    private String userUid;

    // platform: 只有超级管理员才有权限
    // organization: 管理员才有权限
    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    // 默认bytedesk平台
    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();
}
