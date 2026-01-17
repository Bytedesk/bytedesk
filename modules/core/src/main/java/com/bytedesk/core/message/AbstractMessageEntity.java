/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 10:21:32
 * @Description: 消息实体抽象基类，用于统一所有消息类型的字段结构
 */
package com.bytedesk.core.message;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ChannelEnum;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.ToString;

import java.time.ZonedDateTime;

@MappedSuperclass
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public abstract class AbstractMessageEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @Column(name = "message_type")
    private String type = MessageTypeEnum.TEXT.name();

    // 仅对一对一/客服/工作组聊天有效，表示对方是否已读。群聊无效
    @Builder.Default
    private String status = MessageStatusEnum.SUCCESS.name();

    // 复杂类型可以使用json存储在此，通过type字段区分
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // 额外信息，用于存储消息的额外信息
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // 消息来源，用于区分消息是来自web、android还是ios等
    @Builder.Default
    private String channel = ChannelEnum.WEB.name();

    /**
     * sender信息的JSON表示
     */
    @Builder.Default
    @Column(name = "message_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 客服是否已回复该访客消息（用于超时提醒/统计）。
     * 注意：该字段的业务语义是“是否已被客服消息覆盖回复”，并非投递/回执状态。
     */
    @Builder.Default
    @Column(name = "agent_replied")
    private Boolean agentReplied = false;

    /**
     * 客服回复时间（当 agentReplied=true 时记录），用于计算响应时长。
     */
    @Column(name = "agent_replied_at")
    private ZonedDateTime agentRepliedAt;

    /**
     * 回复该批访客消息的客服 uid（可为空）。
     */
    @Column(name = "agent_replied_by_uid")
    private String agentRepliedByUid;
    
}
