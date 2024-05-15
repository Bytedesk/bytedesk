/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:31:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.RouteConsts;
import com.bytedesk.service.agent.Agent;
import com.bytedesk.service.worktime.Worktime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 技能组
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_workgroup")
public class Workgroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // @NotBlank(message = "wid is required")
    // @Column(unique = true, nullable = false)
    // private String wid;

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_WORK_GROUP_AVATAR_URL;

    @Builder.Default
    private String description = BdConstants.DEFAULT_WORK_GROUP_DESCRIPTION;

    /**
     * route type
     */
    @Builder.Default
    private String routeType = RouteConsts.ROUTE_TYPE_ROBIN;

    /**
     * 熟客优先
     */
    @Builder.Default
    @Column(name = "is_recent")
    private boolean recent = false;

    @Builder.Default
    @Column(name = "is_auto_pop")
    private boolean autoPop = false;

    /**
     * tips
     * TODO: set different tips for different lang
     */
    @Builder.Default
    private boolean showTopTip = false;

    @Builder.Default
    @Column(length = 512)
    private String topTip = BdConstants.DEFAULT_WORK_GROUP_DEFAULT_TOP_TIP;

    @Builder.Default
    private String welcomeTip = BdConstants.DEFAULT_WORK_GROUP_WELCOME_TIP;

    /**
     * robot
     * 是否默认机器人接待
     */
    @Builder.Default
    private boolean defaultRobot = false;

    /** 无客服在线时，是否启用机器人接待 */
    @Builder.Default
    private boolean offlineRobot = false;
     
    /** 非工作时间段，是否启用机器人接待 */
    @Builder.Default
    private boolean nonWorktimeRobot = false;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);

    /** work time */
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY)
    private List<Worktime> workTimes = new ArrayList<>();

    /**
	 * one wg can have many agents, one agent can belong to many wgs
	 */
	@JsonIgnore
	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Agent> agents = new HashSet<>();

    /** 存储下一个待分配的客服等信息 */
    @Builder.Default
    @Column(columnDefinition = "json")
    // 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is of type character varying
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BdConstants.EMPTY_JSON_STRING;
    
    /**
     * belong to org
     */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // private Organization organization;
    private String orgUid;

    /**
     * belongs to user
     */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // private User owner;
}
