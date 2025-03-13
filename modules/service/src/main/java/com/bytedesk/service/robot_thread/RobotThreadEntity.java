/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 22:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-29 22:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.robot_thread;

import com.bytedesk.core.thread.AbstractThreadEntity;

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
 * 机器人会话实体，用于特定的机器人会话场景
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_robot_thread")
public class RobotThreadEntity extends AbstractThreadEntity {

    private static final long serialVersionUID = 1L;

    // 机器人ID
    private String robotId;
    
    // 机器人类型
    private String robotType;
    
    // 用户提问记录数
    @Column(name = "question_count")
    private int questionCount;
    
    // 机器人回答记录数
    @Column(name = "answer_count")
    private int answerCount;
    
    // 机器人回答正确率
    @Column(name = "correct_rate")
    private double correctRate;
    
    // 重写方法以确保正确的返回类型
    @Override
    public RobotThreadEntity setOffline() {
        return super.setOffline();
    }

    @Override
    public RobotThreadEntity setStarted() {
        return super.setStarted();
    }

    @Override
    public RobotThreadEntity setClose() {
        return super.setClose();
    }

    @Override
    public RobotThreadEntity setQueuing() {
        return super.setQueuing();
    }
    
    @Override
    public RobotThreadEntity reInit(Boolean isRobot) {
        return super.reInit(isRobot);
    }
    
    // 统计分析方法
    public void incrementQuestionCount() {
        this.questionCount++;
    }
    
    public void incrementAnswerCount() {
        this.answerCount++;
    }
    
    public void updateCorrectRate(boolean wasCorrect) {
        // 简单实现，实际可能需要更复杂的公式
        if (this.answerCount > 0) {
            if (wasCorrect) {
                this.correctRate = ((this.correctRate * (this.answerCount - 1)) + 1.0) / this.answerCount;
            } else {
                this.correctRate = (this.correctRate * (this.answerCount - 1)) / this.answerCount;
            }
        }
    }
}
