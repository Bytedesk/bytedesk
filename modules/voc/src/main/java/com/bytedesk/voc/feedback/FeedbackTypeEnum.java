/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 15:49:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

/**
 * CSAT是回顾性评价，衡量客户对近期一次服务或产品体验的满意度，通常通过具体问题评分，结果范围较广（如1-5分）
 * 1-3 分：划分为不满意的群体；
 * 4-5 分：划分为满意的群体。
 * 
 * NPS是前瞻性预测。衡量客户向他人推荐公司/产品/服务的意愿，通过单项选择题（如0-10分），结果分为三类（推荐、被动、不推荐），更侧重于客户忠诚度和获取潜力
 * 推荐者：9~10 分；推荐者理论上是最可能推荐你的产品的人，他们基本上对你的产品表示满意，也是你的忠实客户。
 * 中立者：7~8 分；中立者是处于摇摆立场的人，他们喜欢你的产品，但是并不会足以让他们愿意冒着影响声誉的风险去推荐。
 * 批评者：0~6 分；批评者是对你的产品低满意度或完全不满意的人，他们大多表现为不推荐，甚至会鼓励其他人不购买。
 */
public enum FeedbackTypeEnum {
    FEEDBACK,       // 意见反馈
    REPORT,         // 举报
    COMPLAINT,       // 投诉
    CSAT,           // 客户满意度调查
    NPS,            // 净推荐值调查，
    SURVEY,         // 调查问卷
    PRAISE,         // 表扬
    SUGGESTION;     // 建议

    // 根据字符串查找对应的枚举常量
    public static FeedbackTypeEnum fromValue(String value) {
        for (FeedbackTypeEnum type : FeedbackTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return FEEDBACK; // 默认返回通用类型
    }
}
