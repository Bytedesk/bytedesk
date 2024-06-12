/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:21:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-27 11:59:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.doc;

import com.bytedesk.ai.file.KbFile;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文档
 *
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ai_kb_doc")
public class KbDoc extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    private String meta;

    /**
     * 
     */
    @JoinColumn(name = "kb_file_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private KbFile kbFile;

    /**
     * belong to org
     */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // private Organization organization;
    private String orgUid;

    /**
     * 所属用户
     */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value =
    // ConstraintMode.NO_CONSTRAINT))
    // private User user;
}
