/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:54:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 21:42:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoReplyKeywordInitData {

    /**
     * Default category name for auto reply
     * 默认自动回复分类名称
     */
    public static final String DEFAULT_CATEGORY_NAME = "默认分类";

    /**
     * Auto reply keyword data structure
     * 自动回复关键词数据结构
     */
    public static class AutoReplyKeywordData {
        private List<String> keywordList;
        private List<String> replyList;
        private String matchType;
        private boolean enabled;

        public AutoReplyKeywordData(List<String> keywordList, List<String> replyList, String matchType, boolean enabled) {
            this.keywordList = keywordList;
            this.replyList = replyList;
            this.matchType = matchType;
            this.enabled = enabled;
        }

        public List<String> getKeywordList() {
            return keywordList;
        }

        public List<String> getReplyList() {
            return replyList;
        }

        public String getMatchType() {
            return matchType;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    /**
     * Get default auto reply keyword data list
     * 获取默认自动回复关键词数据列表
     * 
     * @return List of AutoReplyKeywordData
     */
    public static List<AutoReplyKeywordData> getDefaultAutoReplyKeywordData() {
        List<AutoReplyKeywordData> keywordDataList = new ArrayList<>();
        
        // 问候关键词
        keywordDataList.add(new AutoReplyKeywordData(
            Arrays.asList("你好", "您好", "hi", "hello", "在吗", "在么"),
            Arrays.asList("您好！欢迎使用我们的客服系统，请问有什么可以帮助您的吗？", "您好！很高兴为您服务，请问有什么可以帮助您的吗？"),
            AutoReplyKeywordMatchEnum.FUZZY.name(),
            true
        ));

        // 工作时间关键词
        keywordDataList.add(new AutoReplyKeywordData(
            Arrays.asList("工作时间", "上班时间", "营业时间", "几点上班", "几点下班"),
            Arrays.asList("我们的工作时间是周一至周五 9:00-18:00，周末和节假日休息。如有紧急问题，请留言，我们会在下一个工作日尽快回复您。"),
            AutoReplyKeywordMatchEnum.FUZZY.name(),
            true
        ));

        // 联系方式关键词
        keywordDataList.add(new AutoReplyKeywordData(
            Arrays.asList("联系方式", "电话", "手机", "邮箱", "怎么联系", "联系你们"),
            Arrays.asList("您可以通过以下方式联系我们：\n1. 客服热线：400-123-4567\n2. 邮箱：support@example.com\n3. 在线客服：工作时间在线"),
            AutoReplyKeywordMatchEnum.FUZZY.name(),
            true
        ));

        // 价格关键词
        keywordDataList.add(new AutoReplyKeywordData(
            Arrays.asList("价格", "多少钱", "费用", "收费", "价格表", "报价"),
            Arrays.asList("关于价格信息，建议您联系我们的销售团队获取详细报价。您可以拨打客服热线400-123-4567或发送邮件至sales@example.com咨询。"),
            AutoReplyKeywordMatchEnum.FUZZY.name(),
            true
        ));

        return keywordDataList;
    }

}