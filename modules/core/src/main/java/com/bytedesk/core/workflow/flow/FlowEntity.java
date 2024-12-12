/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 17:01:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 17:24:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LevelEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "bytedesk_core_workflow_flow")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FlowEntity extends BaseEntity {

    private String name;

    private String description;

    private String icon;

    @Builder.Default
    @Column(name = "flow_groups", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String groups = "[]";

    @Builder.Default
    @Column(name = "flow_events", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String events = "[]";

    @Builder.Default
    @Column(name = "flow_variables", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String variables = "[]";

    @Builder.Default
    @Column(name = "flow_edges", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String edges = "[]";

    @Builder.Default
    @Column(name = "flow_theme", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String theme = "{}";

    private String selectedThemeTemplateId;
    
    @Builder.Default
    @Column(name = "flow_settings", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String settings = "{}";

    private String publicId;

    private String customDomain;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String resultsTablePreferences = "{}";

    @Builder.Default
    private boolean isArchived = false;

    @Builder.Default
    private boolean isClosed = false;

    private String whatsAppCredentialsId;

    private Integer riskLevel;

    @Builder.Default
    private String level = LevelEnum.PLATFORM.name();
}
