/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:03:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-22 12:16:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.Set;
import java.util.HashSet;

import com.bytedesk.core.base.BaseEntityNoOrg;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.utils.StringSetConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_topic")
public class TopicEntity extends BaseEntityNoOrg {

    private static final long serialVersionUID = 1L;

    /** 为防止后添加的记录，clientIds缺失，所以用数组代替，这样每个用户在topic中只有一条记录，clientIds可共用 */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Convert(converter = StringSetConverter.class)
    private Set<String> topics = new HashSet<>();

    // 管理员监控的topic
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Convert(converter = StringSetConverter.class)
    private Set<String> monitorTopics = new HashSet<>();

    // 每个用户仅存在一条记录
    // user, no need map, just uid
    // 用户uid或者robotUid
    @NotBlank
    @Column(unique = true, nullable = false)
    private String userUid;

    /** AT_MOST_ONCE(0),AT_LEAST_ONCE(1), EXACTLY_ONCE(2), */
    // @Builder.Default
    // private int qos = 1;

    // @Builder.Default
    // @Column(name = "is_subscribed")
    // private boolean subscribed = false;

    /**
     * topic通配符说明：
     * 单层通配符"+"：只能匹配一层主题。例如，"aaa/+"可以匹配"aaa/bbb"，但不能匹配"aaa/bbb/ccc"。
     * 多层通配符"#"：可以匹配多层主题。例如，"aaa/#"不但可以匹配"aaa/bbb"，还可以匹配"aaa/bbb/ccc/ddd"。它必须作为主题的最后一个级别使用，并且只能出现在最后
     */
    // @Builder.Default
    // @Column(name = "is_wildcard")
    // private boolean wildcard = false;

    /**
     * current online clientIds
     * 用户clientId格式: uid/client/deviceUid
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    @Convert(converter = StringSetConverter.class)
    private Set<String> clientIds = new HashSet<>();

    /** 描述 */
    // private String description;
}
