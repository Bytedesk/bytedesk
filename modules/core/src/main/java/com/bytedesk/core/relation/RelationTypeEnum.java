/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 08:57:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.relation;

/**
 * 关系类型枚举
 * 参考小红书等社交平台的关系设计
 */
public enum RelationTypeEnum {
    // 基础关系类型
    THREAD,     // 会话关系
    CUSTOMER,   // 客户关系
    TICKET,     // 工单关系
    
    // 社交关系类型
    FOLLOW,     // 关注关系 - 用户关注其他用户
    FAN,        // 粉丝关系 - 被其他用户关注
    FRIEND,     // 好友关系 - 双向关注
    LIKE,       // 点赞关系 - 用户点赞内容
    FAVORITE,   // 收藏关系 - 用户收藏内容
    SHARE,      // 分享关系 - 用户分享内容
    COMMENT,    // 评论关系 - 用户评论内容
    
    // 内容关系类型
    ARTICLE,    // 文章关系
    NOTE,       // 笔记关系
    VIDEO,      // 视频关系
    IMAGE,      // 图片关系
    AUDIO,      // 音频关系
    
    // 互动关系类型
    MENTION,    // 提及关系 - @用户
    REPLY,      // 回复关系 - 回复评论
    QUOTE,      // 引用关系 - 引用内容
    REPOST,     // 转发关系 - 转发内容
    
    // 商业关系类型
    BRAND,      // 品牌关系
    PRODUCT,    // 产品关系
    SERVICE,    // 服务关系
    PROMOTION,  // 推广关系
    
    // 组织关系类型
    GROUP,      // 群组关系
    TEAM,       // 团队关系
    ORGANIZATION, // 组织关系
    COMMUNITY;  // 社区关系
    
    /**
     * 根据字符串查找对应的枚举常量
     */
    public static RelationTypeEnum fromValue(String value) {
        for (RelationTypeEnum type : RelationTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No RelationTypeEnum constant with value: " + value);
    }
    
    /**
     * 获取关系类型的中文名称
     */
    public String getChineseName() {
        switch (this) {
            case THREAD:
                return "会话关系";
            case CUSTOMER:
                return "客户关系";
            case TICKET:
                return "工单关系";
            case FOLLOW:
                return "关注关系";
            case FAN:
                return "粉丝关系";
            case FRIEND:
                return "好友关系";
            case LIKE:
                return "点赞关系";
            case FAVORITE:
                return "收藏关系";
            case SHARE:
                return "分享关系";
            case COMMENT:
                return "评论关系";
            case ARTICLE:
                return "文章关系";
            case NOTE:
                return "笔记关系";
            case VIDEO:
                return "视频关系";
            case IMAGE:
                return "图片关系";
            case AUDIO:
                return "音频关系";
            case MENTION:
                return "提及关系";
            case REPLY:
                return "回复关系";
            case QUOTE:
                return "引用关系";
            case REPOST:
                return "转发关系";
            case BRAND:
                return "品牌关系";
            case PRODUCT:
                return "产品关系";
            case SERVICE:
                return "服务关系";
            case PROMOTION:
                return "推广关系";
            case GROUP:
                return "群组关系";
            case TEAM:
                return "团队关系";
            case ORGANIZATION:
                return "组织关系";
            case COMMUNITY:
                return "社区关系";
            default:
                return this.name();
        }
    }
    
    /**
     * 判断是否为社交关系类型
     */
    public boolean isSocialRelation() {
        return this == FOLLOW || this == FAN || this == FRIEND || 
               this == LIKE || this == FAVORITE || this == SHARE || 
               this == COMMENT || this == MENTION || this == REPLY || 
               this == QUOTE || this == REPOST;
    }
    
    /**
     * 判断是否为内容关系类型
     */
    public boolean isContentRelation() {
        return this == ARTICLE || this == NOTE || this == VIDEO || 
               this == IMAGE || this == AUDIO;
    }
    
    /**
     * 判断是否为商业关系类型
     */
    public boolean isBusinessRelation() {
        return this == BRAND || this == PRODUCT || this == SERVICE || 
               this == PROMOTION;
    }
    
    /**
     * 判断是否为组织关系类型
     */
    public boolean isOrganizationRelation() {
        return this == GROUP || this == TEAM || this == ORGANIZATION || 
               this == COMMUNITY;
    }
}
