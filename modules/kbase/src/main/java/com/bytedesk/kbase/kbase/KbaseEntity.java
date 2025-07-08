/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-27 10:24:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.member.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Knowledge base entity for organizing and managing knowledge articles
 * Combines knowledge base functionality with workspace features
 * 
 * Database Table: bytedesk_kbase
 * Purpose: Stores knowledge base configurations, themes, and member management
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ KbaseEntityListener.class })
@Table(
    name = "bytedesk_kbase",
    indexes = {
        @Index(name = "idx_kbase_uid", columnList = "uuid")
    }
)
public class KbaseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the knowledge base
     */
    private String name;

    /**
     * Description of the knowledge base
     */
    private String description;

    /**
     * Type of knowledge base (HELPCENTER, DOCUMENTATION, FAQ, etc.)
     */
    @Builder.Default
    @Column(name = "kb_type")
    private String type = KbaseTypeEnum.HELPCENTER.name();

    /**
     * Main headline or title displayed on the knowledge base
     */
    @Builder.Default
    private String headline = KbaseConsts.HEADLINE;

    /**
     * Subtitle or secondary headline for the knowledge base
     */
    @Builder.Default
    @Column(name = "sub_headline")
    private String subHeadline = KbaseConsts.SUB_HEADLINE;

    /**
     * Custom URL for the knowledge base
     */
    @Builder.Default
    private String url = KbaseConsts.KBASE_URL;

    /**
     * URL of the knowledge base logo image
     */
    @Builder.Default
    private String logoUrl = KbaseConsts.KBASE_LOGO_URL;

    /**
     * URL of the knowledge base favicon
     */
    @Builder.Default
    private String faviconUrl = KbaseConsts.KBASE_FAVICON_URL;

    /**
     * URL of the custom cover image for the knowledge base
     */
    private String coverImageUrl;

    /**
     * URL of the custom background image for the knowledge base
     */
    private String backgroundImageUrl;

    /**
     * Primary color theme for the knowledge base
     */
    @Builder.Default
    private String primaryColor = BytedeskConsts.EMPTY_STRING;

    /**
     * Theme name for the knowledge base appearance
     */
    @Builder.Default
    private String theme = KbaseThemeEnum.DEFAULT.name();

    /**
     * Number of members in the knowledge base
     */
    @Builder.Default
    private Integer memberCount = 0;

    /**
     * Number of articles in the knowledge base
     */
    @Builder.Default
    private Integer articleCount = 0;

    /**
     * Whether the knowledge base is marked as favorite
     */
    @Builder.Default
    @Column(name = "is_favorite")
    private Boolean favorite = false;

    /**
     * Whether the knowledge base is publicly accessible
     */
    @Builder.Default
    @Column(name = "is_public")
    private Boolean isPublic = false;

    /**
     * HTML description for SEO purposes (meta description tag)
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String descriptionHtml = KbaseConsts.KB_DESCRIPTION;

    /**
     * Custom HTML header code displayed at the top of the knowledge base
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String headerHtml = BytedeskConsts.EMPTY_STRING;

    /**
     * Custom HTML footer code displayed at the bottom of the knowledge base
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String footerHtml = BytedeskConsts.EMPTY_STRING;

    /**
     * Custom CSS/Less code for styling the knowledge base
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String css = BytedeskConsts.EMPTY_STRING;

    /**
     * Language setting for the knowledge base
     */
    @Builder.Default
    private String language = LanguageEnum.ZH_CN.name();

    /**
     * Start date when the knowledge base becomes active
     */
    private ZonedDateTime startDate;

    /**
     * End date when the knowledge base expires
     */
    private ZonedDateTime endDate;

    /**
     * Tags for knowledge base categorization and search
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    /**
     * LLM embedding provider for vector search functionality
     */
    @Builder.Default
    @Column(name = "llm_embedding_provider")
    private String embeddingProvider = LlmConsts.DEFAULT_EMBEDDING_PROVIDER;
    
    /**
     * LLM embedding model for vector search functionality
     */
    @Builder.Default
    @Column(name = "llm_embedding_model")
    private String embeddingModel = LlmConsts.DEFAULT_EMBEDDING_MODEL;

    /**
     * Whether to show chat functionality in the knowledge base
     */
    @Builder.Default
    private Boolean showChat = false;

    /**
     * Whether the knowledge base is published and accessible
     */
    @Builder.Default
    private Boolean published = true;

    /**
     * Associated agent UID for agent-specific knowledge base
     */
    private String agentUid;

    /**
     * Members who have access to this knowledge base
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<MemberEntity> members = new ArrayList<>();

    public String getTheme() {
        return this.theme.toLowerCase();
    }
}
