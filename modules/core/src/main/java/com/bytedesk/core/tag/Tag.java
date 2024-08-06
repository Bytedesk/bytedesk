/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-24 10:16:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.tag;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.enums.PlatformEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
@Table(name = "core_tag")
public class Tag extends BaseEntity {

    private String name;

    /**
     * 类别：会话标签 or 客户标签 or 学校 or 课程 or 老师
     * 不固定，暂时不能使用枚举类型，因为需要动态扩展
     */
    @Column(name = "tag_type", nullable = false)
    private String type;

    /**
     * Tag color.
     */
    // @Column(length = 25)
    // private String color;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PlatformEnum platform = PlatformEnum.BYTEDESK;

}
