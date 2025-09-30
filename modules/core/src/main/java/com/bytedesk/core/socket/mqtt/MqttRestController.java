/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-28 22:51:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-21 13:08:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.socket.mqtt.service.MqttConnectionService;
import com.bytedesk.core.socket.mqtt.service.MqttSessionService;
import com.bytedesk.core.uid.utils.NetUtils;
import com.bytedesk.core.utils.JsonResult;
import com.alibaba.fastjson2.JSONObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/mqtt/api/v1")
@Tag(name = "MQTT Management", description = "MQTT connection and session management APIs")
public class MqttRestController {

    private final MqttSessionService mqttSessionService;

    private final MqttConnectionService mqttConnectionService;

    /**
     * get clientIds that has session in the local mqtt server
     * http://127.0.0.1:9003/mqtt/api/v1/session/clients
     * @return
     */
    @Operation(summary = "Get Session Clients", description = "Retrieve all client IDs that have sessions in the local MQTT server")
    @GetMapping("/session/clients")
    public ResponseEntity<?> getSessionClients() {
        //
        String host = NetUtils.getLocalAddress();
        List<String> clientIds = mqttSessionService.getAllClientIds();
        //
        JSONObject json = new JSONObject();
        json.put("host", host);
        json.put("clientIds", clientIds);

        return ResponseEntity.ok(JsonResult.success(json));
    }

    // http://127.0.0.1:9003/mqtt/api/v1/connected/clients
    @Operation(summary = "Get Connected Clients", description = "Retrieve all currently connected MQTT client IDs")
    @GetMapping("/connected/clients")
    public ResponseEntity<?> isConnectedClients() {

        Set<String> clientIds = mqttConnectionService.getConnectedClientIds();

        return ResponseEntity.ok(JsonResult.success(clientIds));
    }

}
