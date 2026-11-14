/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.jobtitle;

/**
 * 新组织默认初始化的常用职务清单。
 * 以中文名称作为实际存储值：成员 jobTitle 保存名称字符串，
 * 不使用 i18n key，避免同一默认项随界面语言写入不同文本。
 */
public class JobTitleInitData {

    public record DefaultJobTitle(String name, int order) {
    }

    public static final DefaultJobTitle[] DEFAULT_JOB_TITLES = {
            new DefaultJobTitle("总经理", 10),
            new DefaultJobTitle("经理", 20),
            new DefaultJobTitle("主管", 30),
            new DefaultJobTitle("专员", 40),
            new DefaultJobTitle("客服", 50),
            new DefaultJobTitle("工程师", 60),
    };
}
