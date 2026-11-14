package com.bytedesk.core.message;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 群聊 @提及目标（成员/机器人/所有人）。
 * 
 * targetType: USER（人类成员，userUid）/ ROBOT（群机器人，robotUid）/ ALL（所有人，固定协议目标）
 * targetUid: 目标唯一标识；ALL 固定为 "all"
 * label: 展示昵称快照（仅展示用，不用于后端身份解析）
 * start/end: 正文中 "@昵称" 片段的 UTF-16 code unit 起止位置，end 为开区间
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageMention implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TARGET_TYPE_USER = "USER";
    public static final String TARGET_TYPE_ROBOT = "ROBOT";
    public static final String TARGET_TYPE_ALL = "ALL";
    public static final String ALL_TARGET_UID = "all";

    private String targetType;

    private String targetUid;

    private String label;

    private Integer start;

    private Integer end;

    public boolean isRobot() {
        return TARGET_TYPE_ROBOT.equals(targetType);
    }

    public boolean isAll() {
        return TARGET_TYPE_ALL.equals(targetType) || ALL_TARGET_UID.equals(targetUid);
    }
}
