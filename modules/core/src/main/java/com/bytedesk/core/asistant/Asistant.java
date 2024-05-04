/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 20:32:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:46:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.asistant;

import com.bytedesk.core.utils.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * asistant - 如：文件助手
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ AsistantListener.class })
@Table(name = "core_asistant")
public class Asistant extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    
    // @NotBlank
	// @Column(unique = true, nullable = false, length = 127)
    // private String aid;

    private String topic;

    @Column(name = "by_type")
    private String type;
    
    private String name;
    
    private String avatar;

    private String description;

    /** belong to org */
    private String orgUid;
}
