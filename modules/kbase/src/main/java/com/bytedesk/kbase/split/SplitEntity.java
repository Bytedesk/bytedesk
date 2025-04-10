/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 16:08:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.split;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({SplitEntityListener.class})
@Table(name = "bytedesk_kbase_split")
public class SplitEntity extends BaseEntity {

    private String name;

    // split 之后可能不需要这么长的 content，待优化
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Builder.Default
    @Column(name = "split_type")
    private String type = SplitTypeEnum.TEXT.name();

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    @Builder.Default
    @Column(name = "is_enabled")
    private boolean enabled = true;


    // 对应 拆分 document 的 id
    private String docId;

    // 合并fileEntity/textEntity/qaEntity/websiteEntity 的 uid
    private String typeUid;

    // 所属分类
    private String categoryUid;

    // kbaseEntity 的 uid
    private String kbUid;

}
