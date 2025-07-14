/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-29 11:32:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-24 11:25:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.mqtt")
@Description("MQTT Properties Configuration - MQTT broker connection and configuration properties")
public class MqttProperties {

    public static final String CONFIG_PREFIX = "bytedesk.socket";

    private Boolean enabled = true;

    private String host = "0.0.0.0";

    // private Integer port = 9883;
    // private Integer tslPort = 9884;

    private Integer websocketPort = 9885;
    
    // private Integer websocketTlsPort = 9886;
    // private String sslCertPath = "";
    // private String sslKeyPath = "";

    private String leakDetectorLevel = "SIMPLE";

    private Integer parentEventLoopGroupThreadCount = 1;

    private Integer childEventLoopGroupThreadCount = 8;

    private Integer maxPayloadSize = 10240;

}
