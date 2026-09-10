package com.bytedesk.core.message.reaction;

import com.bytedesk.core.black.BlacklistCheckable;

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
public class MessageReactionToggleRequest implements BlacklistCheckable {

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

    /**
     * 黑名单校验：访客切换表情回复时，操作者为 userUid（orgUid 缺省时仅平台级黑名单生效）
     */
    @Override
    public String getBlacklistOperatorUid() {
        return userUid;
    }
}
