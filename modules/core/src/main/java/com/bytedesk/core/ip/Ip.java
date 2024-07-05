/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-17 13:03:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-27 12:19:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.utils.StringSetConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "core_ip")
public class Ip extends BaseEntity {
    //
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Convert(converter = StringSetConverter.class)
    private Set<String> ips = new HashSet<>();
    // private String ip;
    // private String ipLocation;

    private String ipRangeStart;

    private String ipRangeEnd;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "ip_type")
    // private String type;
    private IpTypeEnum type = IpTypeEnum.BLACKLIST;

    private String reason;

    // time duration
    private Date untilDate;
}
