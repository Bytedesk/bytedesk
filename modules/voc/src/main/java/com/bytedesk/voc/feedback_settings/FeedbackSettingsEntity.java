/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback_settings;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.voc.feedback.FeedbackTypeEnum;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * FeedbackSettings entity for content categorization and organization
 * Provides feedback_settingsging functionality for various system entities
 * 
 * Database Table: bytedesk_voc_feedback_settings
 * Purpose: Stores feedback_settings definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({FeedbackSettingsEntityListener.class})
@Table(name = "bytedesk_voc_feedback_settings")
public class FeedbackSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the feedback_settings
     */
    private String name;

    /**
     * Description of the feedback_settings
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of feedback_settings (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "feedback_settings_type")
    private String type = FeedbackTypeEnum.FEEDBACK.name();

    /**
     * Whether this scene widget is enabled
     */
    @Builder.Default
    @Column(name = "enabled")
    private Boolean enabled = Boolean.TRUE;

    /**
     * Score threshold for positive (default: 9)
     */
    @Builder.Default
    @Column(name = "positive_score_min")
    private Integer positiveScoreMin = 9;

    /**
     * Max score for this feedback template (default: 10)
     * For example: 5 (star rating) or 10 (NPS/CSAT style)
     */
    @Builder.Default
    @Column(name = "score_max")
    private Integer scoreMax = 10;

    /**
     * Max selectable reasons (default: 3)
     */
    @Builder.Default
    @Column(name = "max_reasons")
    private Integer maxReasons = 3;

    /**
     * Title shown in widget
     */
    @Builder.Default
    @Column(name = "title", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String title = I18Consts.I18N_FEEDBACK_SETTINGS_TITLE_DEFAULT;

    /**
     * Question for positive reasons
     */
    @Builder.Default
    @Column(name = "positive_question", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String positiveQuestion = I18Consts.I18N_FEEDBACK_SETTINGS_POSITIVE_QUESTION_DEFAULT;

    /**
     * Question for negative reasons
     */
    @Builder.Default
    @Column(name = "negative_question", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String negativeQuestion = I18Consts.I18N_FEEDBACK_SETTINGS_NEGATIVE_QUESTION_DEFAULT;

    /**
     * Placeholder for comment box
     */
    @Builder.Default
    @Column(name = "comment_placeholder", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String commentPlaceholder = I18Consts.I18N_FEEDBACK_SETTINGS_COMMENT_PLACEHOLDER_DEFAULT;

    /**
     * Positive reasons options
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "positive_reasons", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> positiveReasons = new ArrayList<>();

    /**
     * Negative reasons options
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "negative_reasons", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> negativeReasons = new ArrayList<>();

    

}
