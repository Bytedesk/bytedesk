/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-29 11:32:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 12:16:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = MqttProperties.CONFIG_PREFIX)
public class MqttProperties {

    public static final String CONFIG_PREFIX = "bytedesk.socket";

    private String host = "0.0.0.0";

    // private int port = 9883;
    // private int tslPort = 9884;

    private int websocketPort = 9885;
    
    // private int websocketTlsPort = 9886;
    // private String sslCertPath = "";
    // private String sslKeyPath = "";

    private String leakDetectorLevel = "SIMPLE";

    private int parentEventLoopGroupThreadCount = 1;

    private int childEventLoopGroupThreadCount = 8;

    private int maxPayloadSize = 10240;

}
