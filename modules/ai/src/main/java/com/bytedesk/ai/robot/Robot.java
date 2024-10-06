/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:16:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-25 13:43:45
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

import com.bytedesk.ai.settings.RobotServiceSettings;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
 * 
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ RobotEntityListener.class })
@Table(name = "ai_robot")
public class Robot extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private String nickname = I18Consts.I18N_ROBOT_NICKNAME;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

    @Builder.Default
    private String description = I18Consts.I18N_ROBOT_DESCRIPTION;

    @Embedded
    @Builder.Default
    private RobotServiceSettings serviceSettings = new RobotServiceSettings();

    @Embedded
    @Builder.Default
    private RobotLlm llm = new RobotLlm();

    @Embedded
    @Builder.Default
    private RobotFlow flow = new RobotFlow();

    // 如果未匹配到关键词，默认回复内容
    @Builder.Default
    private String defaultReply = I18Consts.I18N_ROBOT_REPLY;

    @Builder.Default
    @Column(name = "robot_category")
    private String category = RobotCategoryEnum.DEFAULT.name();

    // service、ask、chat
    @Builder.Default
    // @Enumerated(EnumType.STRING)
    @Column(name = "robot_type", nullable = false)
    // private RobotTypeEnum type = RobotTypeEnum.SERVICE;
    private String type = RobotTypeEnum.SERVICE.name();

    // private、team、public
    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private LevelEnum level = LevelEnum.ORGNIZATION;
    private String level = LevelEnum.ORGNIZATION.name();

    // @Builder.Default
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    // private String flow = BdConstants.EMPTY_JSON_STRING;

    @Builder.Default
    private boolean published = false;

    // @Builder.Default
    // @Column(name = "is_private")
    // private boolean isPrivate = false;

    private String kbUid; // 对应知识库

    // only used when created by user, not by org
    // private String userUid; // 创建用户
}
