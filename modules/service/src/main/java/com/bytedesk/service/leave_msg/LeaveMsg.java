/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:11:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-05 21:33:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.leave_msg;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.utils.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 留言
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_leavemsg")
public class LeaveMsg extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 联系方式
    private String contact;

    // 留言内容
    private String content;

    // 支持图片
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> images = new ArrayList<>();

    private String threadTopic;

    @Builder.Default
    @Column(name = "leavemsg_user", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String user = BdConstants.EMPTY_JSON_STRING;
}
