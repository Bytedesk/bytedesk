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
package com.bytedesk.core.city;

import com.bytedesk.core.base.BaseEntityNoOrg;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * City entity for content categorization and organization
 * Provides cityging functionality for various system entities
 * 
 * Database Table: bytedesk_core_city
 * Purpose: Stores city definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({CityEntityListener.class})
@Table(name = "bytedesk_core_city")
public class CityEntity extends BaseEntityNoOrg {

    private static final long serialVersionUID = 1L;

    /**
     * City name
     */
    private String name;

    /**
     * Short name / abbreviation (cap)
     */
    private String cap;

    /**
     * Administrative code
     */
    private String code;

    /**
     * Whether this city is hot
     */
    @Column(name = "is_hot")
    private Boolean hot;

    /**
     * Latitude
     */
    private String lat;

    /**
     * Longitude
     */
    private String lng;

    /**
     * Whether this city is open/enabled
     */
    @Column(name = "is_open")
    private Boolean open;

    /**
     * City level type from source data: province/city/county/circle
     */
    @Column(name = "by_type")
    private String type;

    /**
     * Parent city id
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * Pinyin
     */
    private String pinyin;
    
}
