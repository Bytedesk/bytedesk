/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-11-25
 * @Description: Queue Tip Template Variables Utility
 * 排队提示语模板变量工具类
 * 
 * 支持的模板变量：
 * - {position} - 当前排队位置（从1开始）
 * - {queueSize} - 当前队列总人数
 * - {waitSeconds} - 预计等待秒数
 * - {waitMinutes} - 预计等待分钟数
 * - {waitTime} - 格式化的等待时间描述（如"约5分钟"）
 * 
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.service.queue_settings;

import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.I18Consts;

import lombok.extern.slf4j.Slf4j;

/**
 * 排队提示语模板变量工具类
 * <p>
 * 用于将排队提示语模板中的变量替换为实际值
 */
@Slf4j
public class QueueTipTemplateUtils {

    // 模板变量常量
    public static final String VAR_POSITION = "{position}";
    public static final String VAR_QUEUE_SIZE = "{queueSize}";
    public static final String VAR_WAIT_SECONDS = "{waitSeconds}";
    public static final String VAR_WAIT_MINUTES = "{waitMinutes}";
    public static final String VAR_WAIT_TIME = "{waitTime}";

    // 默认每人等待时间（秒）
    public static final int DEFAULT_AVG_WAIT_TIME_PER_PERSON = 60;

    private QueueTipTemplateUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 解析排队提示语模板，替换模板变量为实际值
     *
     * @param template              排队提示语模板
     * @param position              当前排队位置（从1开始）
     * @param queueSize             当前队列总人数
     * @param avgWaitTimePerPerson  每人平均等待时长（秒），用于计算预估等待时间
     * @return 替换变量后的提示语
     */
    public static String resolveTemplate(String template, int position, int queueSize, int avgWaitTimePerPerson) {
        if (!StringUtils.hasText(template)) {
            // 使用默认模板
            template = I18Consts.I18N_QUEUE_TIP_TEMPLATE;
        }

        // 计算等待时间
        int waitSeconds = position * avgWaitTimePerPerson;
        int waitMinutes = (int) Math.ceil(waitSeconds / 60.0);
        String waitTime = formatWaitTime(waitSeconds);

        // 替换模板变量
        String result = template
                .replace(VAR_POSITION, String.valueOf(position))
                .replace(VAR_QUEUE_SIZE, String.valueOf(queueSize))
                .replace(VAR_WAIT_SECONDS, String.valueOf(waitSeconds))
                .replace(VAR_WAIT_MINUTES, String.valueOf(waitMinutes))
                .replace(VAR_WAIT_TIME, waitTime);

        log.debug("解析排队提示语模板 - 模板: {}, position: {}, queueSize: {}, avgWaitTime: {}s, 结果: {}",
                template, position, queueSize, avgWaitTimePerPerson, result);

        return result;
    }

    /**
     * 解析排队提示语模板（使用默认每人等待时间）
     *
     * @param template   排队提示语模板
     * @param position   当前排队位置
     * @param queueSize  当前队列总人数
     * @return 替换变量后的提示语
     */
    public static String resolveTemplate(String template, int position, int queueSize) {
        return resolveTemplate(template, position, queueSize, DEFAULT_AVG_WAIT_TIME_PER_PERSON);
    }

    /**
     * 格式化等待时间为人性化描述
     *
     * @param waitSeconds 等待秒数
     * @return 格式化的等待时间描述
     */
    public static String formatWaitTime(int waitSeconds) {
        if (waitSeconds <= 0) {
            return "即将开始";
        }

        int minutes = (int) Math.ceil(waitSeconds / 60.0);
        if (minutes < 1) {
            return "不到1分钟";
        } else if (minutes == 1) {
            return "约1分钟";
        } else if (minutes < 60) {
            return "约" + minutes + "分钟";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            if (remainingMinutes == 0) {
                return "约" + hours + "小时";
            } else {
                return "约" + hours + "小时" + remainingMinutes + "分钟";
            }
        }
    }

    /**
     * 获取支持的模板变量说明
     *
     * @return 模板变量说明
     */
    public static String getTemplateVariablesDescription() {
        return "支持的模板变量：\n" +
                "- {position}: 当前排队位置（您前面的排队人数+1）\n" +
                "- {queueSize}: 当前队列总人数\n" +
                "- {waitSeconds}: 预计等待秒数\n" +
                "- {waitMinutes}: 预计等待分钟数\n" +
                "- {waitTime}: 格式化的等待时间（如'约5分钟'）\n" +
                "\n" +
                "示例模板：\n" +
                "您前面还有 {position} 人排队，预计等待 {waitTime}，请耐心等候。";
    }

    /**
     * 验证模板是否有效
     *
     * @param template 模板字符串
     * @return 如果模板包含至少一个有效变量或不为空则返回 true
     */
    public static boolean isValidTemplate(String template) {
        if (!StringUtils.hasText(template)) {
            return false;
        }

        // 检查是否包含任何模板变量
        return template.contains(VAR_POSITION) ||
                template.contains(VAR_QUEUE_SIZE) ||
                template.contains(VAR_WAIT_SECONDS) ||
                template.contains(VAR_WAIT_MINUTES) ||
                template.contains(VAR_WAIT_TIME) ||
                template.length() > 0; // 即使没有变量，纯文本也是有效的
    }

    /**
     * 获取默认的排队提示语模板
     *
     * @return 默认模板
     */
    public static String getDefaultTemplate() {
        return I18Consts.I18N_QUEUE_TIP_TEMPLATE;
    }
}
