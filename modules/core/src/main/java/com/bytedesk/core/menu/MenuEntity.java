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
package com.bytedesk.core.menu;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
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
 * Menu entity for content categorization and organization
 * Provides menuging functionality for various system entities
 * 
 * Database Table: bytedesk_core_menu
 * Purpose: Stores menu definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({MenuEntityListener.class})
@Table(name = "bytedesk_core_menu")
public class MenuEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_COLOR = "blue";

    /**
     * Name of the menu
     */
    private String name;

    /**
     * I18n key for menu display name
     */
    @Column(name = "menu_nickname", length = 128)
    private String nickname;

    /**
     * Description of the menu
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of menu (ADMIN/DESKTOP, etc.)
     */
    @Builder.Default
    @Column(name = "menu_type")
    private String type = MenuTypeEnum.DESKTOP.name();

    /**
     * Primary color used when rendering this menu entry
     */
    @Builder.Default
    @Column(name = "menu_color", length = 32)
    private String color = DEFAULT_COLOR;

    /**
     * Icon key (Ant Design icon name, etc.) for the menu entry
     */
    @Column(name = "menu_icon", length = 64)
    private String icon;

    /**
     * External link that the menu points to
     */
    @Column(name = "menu_link", length = 1024)
    private String link;

    /**
     * Sorting order (lower numbers appear first)
     */
    @Builder.Default
    @Column(name = "menu_order_index")
    private Integer order = 0;

    /**
     * Parent menu uid for multi-level menus
     */
    @Column(name = "parent_uid", length = 64)
    private String parentUid;

    /**
     * Whether the menu entry is currently enabled
     */
    @Builder.Default
    @Column(name = "menu_enabled")
    private Boolean enabled = true;

    /**
     * Whether to open the link in a new window/tab
     */
    @Builder.Default
    @Column(name = "menu_open_new_window")
    private Boolean openInNewWindow = true;
 
}
