/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 22:38:17
 * @Description: 消息实体抽象基类，用于统一所有消息类型的字段结构
 */
package com.bytedesk.core.message;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractMessageEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @Column(name = "message_type", nullable = false)
    private String type = MessageTypeEnum.TEXT.name();

    // 仅对一对一/客服/技能组聊天有效，表示对方是否已读。群聊无效
    @Builder.Default
    private String status = MessageStatusEnum.SUCCESS.name();

    // 复杂类型可以使用json存储在此，通过type字段区分
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    private String client = ClientEnum.WEB.name();

    /** message belongs to */
    @Column(name = "thread_topic")
    private String topic;

    @Column(name = "thread_uid")
    private String threadUid;

    /**
     * sender信息的JSON表示
     */
    @Builder.Default
    @Column(name = "message_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;


    public UserProtobuf getUserProtobuf() {
        return UserProtobuf.parseFromJson(user);
    }

    // 通过解析user字段中的type字段来判断 type=robot则为机器人，否则为访客
    public boolean isFromRobot() {
        // 忽略大小写，判断是否包含"type":"robot"字段
        // return user.toLowerCase().contains("\"type\":\"robot\"");
        return getUserProtobuf().getType().equalsIgnoreCase(UserTypeEnum.ROBOT.name());
    }

    // 通过解析user字段中的type字段来判断 type=visitor则为访客，否则为客服
    public boolean isFromVisitor() {
        // 忽略大小写，判断是否包含"type":"visitor"字段
        // return user.toLowerCase().contains("\"type\":\"visitor\"");
        return getUserProtobuf().getType().equalsIgnoreCase(UserTypeEnum.VISITOR.name());
    }

    // 是否系统消息
    public boolean isFromSystem() {
        // 忽略大小写，判断是否包含"type":"system"字段
        // return user.toLowerCase().contains("\"type\":\"system\"");
        return getUserProtobuf().getType().equalsIgnoreCase(UserTypeEnum.SYSTEM.name());
    }

    // 是否客服消息
    public boolean isFromAgent() {
        // 忽略大小写，判断是否包含"type":"agent"字段
        // return user.toLowerCase().contains("\"type\":\"agent\"");
        return getUserProtobuf().getType().equalsIgnoreCase(UserTypeEnum.AGENT.name());
    }
}
