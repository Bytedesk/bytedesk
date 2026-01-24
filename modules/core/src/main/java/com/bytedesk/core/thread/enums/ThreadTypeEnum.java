/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 16:32:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 11:20:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread.enums;

/**
 * 会话类型
 */
public enum ThreadTypeEnum {
    AGENT(0), // 一对一客服，不支持机器人接待
    WORKGROUP(1), // 工作组客服，支持机器人接待，支持转人工
    ROBOT(2), // 机器人客服，不支持转人工
    // 
    MEMBER(3), // 组织成员对话
    GROUP(4), // 群组对话
    FEEDBACK(6), // 意见反馈
    ASSISTANT(7), // 助理，包括文件助理、剪贴板助理
    CHANNEL(8), // 渠道对话，包括系统通知、订阅号、服务号，NoticeAccountTypeEnum
    LOCAL(9), // 本地对话
    FRIEND(10), // 好友对话
    TICKET_INTERNAL(11), // 内部工单会话
    // 
    KBASE(12), // 机器人-知识库对话，后台模拟测试
    KBDOC(13), // 机器人-知识库某一个文档对话，后台模拟测试
    // 
    LLM(14), // 机器人-直接调用大模型
    UNIFIED(15), // 统一客服入口
    HISTORY(16), // 历史对话，用于管理后台查看历史对话
    WORKFLOW(17), // 工作流对话
    // 
    QUEUE(18), // 一对一排队会话-用于更新排队状态
    CALLCENTER(19), // 呼叫中心会话-用于呼叫中心场景
    TICKET_EXTERNAL(20) // 外部工单会话
    ;

    private final int value;

    // 枚举构造器，每个枚举常量都有一个与之关联的整型值
    ThreadTypeEnum(int value) {
        this.value = value;
    }

    // 获取枚举常量的整型值
    public int getValue() {
        return value;
    }

    // 根据整型值查找对应的枚举常量
    public static ThreadTypeEnum fromValue(int value) {
        for (ThreadTypeEnum type : ThreadTypeEnum.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
    
    /**
     * 获取枚举类型对应的中文名称
     * @return 对应的中文名称
     */
    public String getChineseName() {
        switch (this) {
            case AGENT:
                return "一对一客服";
            case WORKGROUP:
                return "工作组客服";
            case ROBOT:
                return "机器人客服";
            case MEMBER:
                return "组织成员对话";
            case GROUP:
                return "群组对话";
            case FEEDBACK:
                return "意见反馈";
            case ASSISTANT:
                return "助理";
            case CHANNEL:
                return "渠道对话";
            case LOCAL:
                return "本地对话";
            case FRIEND:
                return "好友对话";
            case TICKET_INTERNAL:
                return "内部工单会话";
            case KBASE:
                return "知识库对话";
            case KBDOC:
                return "知识库文档对话";
            case LLM:
                return "大模型对话";
            case UNIFIED:
                return "统一客服入口";
            case HISTORY:
                return "历史对话";
            case TICKET_EXTERNAL:
                return "外部工单会话";
            default:
                return this.name();
        }
    }
    
    /**
     * 根据枚举名称获取对应的中文名称
     * @param name 枚举名称
     * @return 对应的中文名称，如果找不到匹配的枚举则返回原始名称
     */
    public static String getChineseNameByString(String name) {
        try {
            ThreadTypeEnum type = ThreadTypeEnum.valueOf(name);
            return type.getChineseName();
        } catch (IllegalArgumentException e) {
            return name;
        }
    }
}
