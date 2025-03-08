/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:52:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 15:04:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.customer;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 客户留资，自动提取，手动添加
 * @author jackning 270580156@qq.com
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_customer")
public class CustomerEntity extends BaseEntity {

    @NotBlank(message = "name is required")
    @Column(nullable = false)
    private String nickname;

    private String email;

    private String mobile;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    // private String note;
    
    // /**
    //  * https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html
    //  */
    // @Embedded
    // Address address;

    // @Embeddable
    // public static class Address {
    //     String zipCode, city, street;
    // }
    
}
