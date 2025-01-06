/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 11:35:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 23:38:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.result;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Map;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "bytedesk_core_workflow_result")
public class Result extends BaseEntity {

    @Column(name = "bot_id")
    private String botId;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String answersJson;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String variablesJson;

    private String status; // COMPLETED, IN_PROGRESS, etc.

    @Transient
    private Map<String, Object> answers;

    @Transient
    private Map<String, Object> variables;

    @PostLoad
    private void loadJsonFields() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (answersJson != null) {
                this.answers = mapper.readValue(answersJson, new TypeReference<Map<String, Object>>() {
                });
            }
            if (variablesJson != null) {
                this.variables = mapper.readValue(variablesJson, new TypeReference<Map<String, Object>>() {
                });
            }
        } catch (

        Exception e) {
            throw new RuntimeException("Error loading JSON fields", e);
        }
    }

    @PrePersist
    @PreUpdate
    private void saveJsonFields() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (answers != null) {
                this.answersJson = mapper.writeValueAsString(answers);
            }
            if (variables != null) {
                this.variablesJson = mapper.writeValueAsString(variables);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving JSON fields", e);
        }
    }

}
