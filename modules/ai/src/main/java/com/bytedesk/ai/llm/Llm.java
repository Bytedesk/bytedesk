/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-25 11:13:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:59:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.llm;

import com.bytedesk.core.utils.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * 大模型
 *
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ai_llm")
public class Llm extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    // @Column(name = "lid", unique = true, nullable = false)
    // private String lid;
    
    // 
    private String name;

    private String description;

    /**
     * 模型类型：系统自带参数模板、robot对应参数、用户自添加大模型
     * system/robot/user
     */
    @Column(name = "by_type")
    private String type;

    private Integer topK;

    private Double scoreThreshold;

    private String apiKey;

    private String apiSecret;

    private String apiUrl;

    private String embeddings;
    
    private Double temperature;

    private String prompt;

    // 

    /**
     * 支持用户自行添加大模型
     */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    // private User user;
}
