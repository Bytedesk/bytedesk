package com.bytedesk.core.message.reaction;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 轻量级表情回复（Reaction）
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MessageReaction implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 表情符号，如 👍 / 👌 / 👎 */
    private String emoji;

    /**
     * 点选该表情的用户信息列表（用于头像/昵称展示）。
     */
    private List<MessageReactionUser> users;
}
