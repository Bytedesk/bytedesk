/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-06 15:13:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-27 12:02:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RobotFlow {

    @Builder.Default
    @Column(name = "is_flow_enabled")
    private boolean enabled = false;

    @Builder.Default
    @Column(name = "flow_groups", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String groups = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    @Column(name = "flow_events", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String events = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    @Column(name = "flow_variables", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String variables = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    @Column(name = "flow_edges", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String edges = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    @Column(name = "flow_themes", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String themes = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    @Column(name = "flow_settings", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String settings = BytedeskConsts.EMPTY_JSON_STRING;

}
