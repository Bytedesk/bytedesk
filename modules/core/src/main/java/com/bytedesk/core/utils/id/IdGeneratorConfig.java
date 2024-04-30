/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-08 12:13:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-08 12:13:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils.id;

/**
 * ID生成器的配置接口
 * @author Ivan.Ma
 */
public interface IdGeneratorConfig {

    /**
     * 获取分隔符
     * @return string
     */
    String getSplitString();

    /**
     * 获取初始值
     * @return int
     */
    int getInitial();

    /**
     * 获取ID前缀
     * @return string
     */
    String getPrefix();

    /**
     * 获取滚动间隔, 单位: 秒
     * @return int
     */
    int getRollingInterval();

}
