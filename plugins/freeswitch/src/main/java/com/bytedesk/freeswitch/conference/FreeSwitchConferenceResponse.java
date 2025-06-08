/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.conference;

import com.bytedesk.core.base.BaseResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FreeSwitchConferenceResponse extends BaseResponse {

    /**
     * 会议室名称
     */
    private String conferenceName;

    /**
     * 会议室描述
     */
    private String description;

    /**
     * 会议室密码
     */
    @JsonIgnore
    private String password;

    /**
     * 最大参与者数量
     */
    private Integer maxMembers;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否录音
     */
    private Boolean recordEnabled;

    /**
     * 录音文件路径
     */
    private String recordPath;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 会议室配置参数（JSON格式）
     */
    private String configJson;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否有密码保护
     */
    private Boolean passwordProtected;

    /**
     * 从实体对象创建响应对象
     */
    public static FreeSwitchConferenceResponse fromEntity(FreeSwitchConferenceEntity entity) {
        return FreeSwitchConferenceResponse.builder()
                .uid(entity.getUid())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .conferenceName(entity.getConferenceName())
                .description(entity.getDescription())
                .password(entity.getPassword())
                .maxMembers(entity.getMaxMembers())
                .enabled(entity.getEnabled())
                .recordEnabled(entity.getRecordEnabled())
                .recordPath(entity.getRecordPath())
                .creator(entity.getCreator())
                .configJson(entity.getConfigJson())
                .remarks(entity.getRemarks())
                .passwordProtected(entity.getPassword() != null && !entity.getPassword().isEmpty())
                .build();
    }
}
