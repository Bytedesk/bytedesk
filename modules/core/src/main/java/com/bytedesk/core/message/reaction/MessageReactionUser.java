package com.bytedesk.core.message.reaction;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Reaction 用户信息（用于前端展示头像/昵称）。
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MessageReactionUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    /** 用户昵称（可能为空） */
    private String nickname;

    /** 用户头像（可能为空） */
    private String avatar;
}
