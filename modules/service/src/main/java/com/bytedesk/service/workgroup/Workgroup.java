/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-05 22:51:38
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

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.RouteConsts;
import com.bytedesk.core.utils.AuditModel;
import com.bytedesk.service.agent.Agent;
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
public class Workgroup extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 
     */
    @Column(unique = true, nullable = false)
    private String wid;

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_WORK_GROUP_AVATAR_URL;

    @Builder.Default
    private String description = BdConstants.DEFAULT_WORK_GROUP_DESCRIPTION;

    /**
     * 
     */


    /**
     * 
     */
    @Builder.Default
    private String routeType = RouteConsts.ROUTE_TYPE_ROBIN;

    /**
     * 
     */
    @Builder.Default
    @Column(name = "is_recent")
    private Boolean recent = false;

    /**
     * 
     */
    @Builder.Default
    @Column(name = "is_auto_pop")
    private Boolean autoPop = false;

    /**
     * 
     */
    @Builder.Default
    private Boolean showTopTip = false;

    @Builder.Default
    @Column(length = 512)
    private String topTip = BdConstants.DEFAULT_WORK_GROUP_DEFAULT_TOP_TIP;

    /**
	 * 
	 */
	@JsonIgnore
	@Builder.Default
	@OneToMany
	private List<Agent> agents = new ArrayList<>();


    
}