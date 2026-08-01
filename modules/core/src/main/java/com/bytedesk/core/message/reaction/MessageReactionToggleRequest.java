package com.bytedesk.core.message.reaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MessageReactionToggleRequest {

    /** 消息 uid */
    private String messageUid;

    /** 表情符号，如 👍 / 👌 / 👎 */
    private String emoji;

    /**
     * 点选用户 uid（visitor 侧通常需要传；agent 侧优先从登录态获取）。
     */
    private String userUid;

    /** 可选：用户昵称（visitor 侧用于写入 reactions.users） */
    private String userNickname;

    /** 可选：用户头像（visitor 侧用于写入 reactions.users） */
    private String userAvatar;
}
