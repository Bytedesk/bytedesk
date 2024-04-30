/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-18 12:35:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesa
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

// import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.io.Serializable;
// import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Date -> ZonedDateTime ？
 * spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
 * spring.jackson.time-zone=GMT+8
 * 
 * @see https://docs.spring.io/spring-data/jpa/reference/auditing.html
 * @author im.bytedesk.com
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditModel implements Serializable {

    // @Value("${bytedesk.timezone}")
    // private static final String timezone = "GMT+8";
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = timezone) // config in properties
    private Date createdAt;
    // private ZonedDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    @LastModifiedDate
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = timezone) // config in properties
    private Date updatedAt;
    // private ZonedDateTime updatedAt;

    /**
	 * soft delete
	 */
	@Column(name = "is_deleted")
	private boolean deleted = false;

}
