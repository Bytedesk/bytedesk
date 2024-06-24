/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:16:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-23 11:16:25
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

import com.bytedesk.ai.kb.Kb;
import com.bytedesk.ai.settings.RobotServiceSettings;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.fasterxml.jackson.annotation.JsonIgnore;

// import jakarta.persistence.AssociationOverride;
// import jakarta.persistence.AssociationOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private String nickname;

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

    // 客服机器人、问答机器人、闲聊
    // service、ask、chat
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "robot_type", nullable = false)
    // private String type = TypeConsts.ROBOT_TYPE_SERVICE;
    private RobotTypeEnum type = RobotTypeEnum.SERVICE;

    // is_published or not
    @Builder.Default
    private boolean published = false;

    // /**
    // * llm
    // */
    // @ManyToOne()
    // @JoinColumn(name = "llm_id", foreignKey = @ForeignKey(name = "none", value =
    // ConstraintMode.NO_CONSTRAINT))
    // private Llm llm;

    /**
     * 知识库
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kb_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Kb kb;

    /**
     * belong to org
     */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // private Organization organization;
    // private String orgUid;

    

}
