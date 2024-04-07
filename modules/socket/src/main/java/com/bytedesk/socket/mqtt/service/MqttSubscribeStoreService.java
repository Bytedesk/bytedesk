/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 12:07:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.service;

import cn.hutool.core.util.StrUtil;
import com.bytedesk.socket.mqtt.model.MqttSubscribe;
import com.bytedesk.socket.mqtt.redis.MqttSubscribeNotWildcardCache;
import com.bytedesk.socket.mqtt.redis.MqttSubscribeWildcardCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
// import java.util.Collection;
import java.util.List;

/**
 * 订阅存储服务
 */
@Service
@AllArgsConstructor
public class MqttSubscribeStoreService {

    private final MqttSubscribeWildcardCache mqttSubscribeWildcardCache;

    private final MqttSubscribeNotWildcardCache mqttSubscribeNotWildcardCache;

    public void put(String topicFilter, MqttSubscribe mqttSubscribe) {
        if (StrUtil.contains(topicFilter, '#') || StrUtil.contains(topicFilter, '+')) {
            mqttSubscribeWildcardCache.put(topicFilter, mqttSubscribe.getClientId(), mqttSubscribe);
        } else {
            mqttSubscribeNotWildcardCache.put(topicFilter, mqttSubscribe.getClientId(), mqttSubscribe);
        }
    }

    public void remove(String topicFilter, String clientId) {
        if (StrUtil.contains(topicFilter, '#') || StrUtil.contains(topicFilter, '+')) {
            mqttSubscribeWildcardCache.remove(topicFilter, clientId);
        } else {
            mqttSubscribeNotWildcardCache.remove(topicFilter, clientId);
        }
    }

    public void removeForClient(String clientId) {
        mqttSubscribeNotWildcardCache.removeForClient(clientId);
        mqttSubscribeWildcardCache.removeForClient(clientId);
    }

    public List<MqttSubscribe> search(String topic) {
        List<MqttSubscribe> subscribeStores = new ArrayList<>();
        List<MqttSubscribe> list = mqttSubscribeNotWildcardCache.all(topic);
        if (!list.isEmpty()) {
            subscribeStores.addAll(list);
        }
        // TODO: 暂时没用到，注释掉，加快处理速度。待后期需要通配符的时候，放开注释
        // mqttSubscribeWildcardCache.all().forEach((topicFilter, map) -> {
        // if (StrUtil.split(topic, '/').size() >= StrUtil.split(topicFilter,
        // '/').size()) {
        // List<String> splitTopics = StrUtil.split(topic, '/');//a
        // List<String> spliteTopicFilters = StrUtil.split(topicFilter, '/');//#
        // String newTopicFilter = "";
        // for (int i = 0; i < spliteTopicFilters.size(); i++) {
        // String value = spliteTopicFilters.get(i);
        // if (value.equals("+")) {
        // newTopicFilter = newTopicFilter + "+/";
        // } else if (value.equals("#")) {
        // newTopicFilter = newTopicFilter + "#/";
        // break;
        // } else {
        // newTopicFilter = newTopicFilter + splitTopics.get(i) + "/";
        // }
        // }
        // newTopicFilter = StrUtil.removeSuffix(newTopicFilter, "/");
        // if (topicFilter.equals(newTopicFilter)) {
        // Collection<MqttSubscribe> collection = map.values();
        // List<MqttSubscribe> list2 = new ArrayList<MqttSubscribe>(collection);
        // subscribeStores.addAll(list2);
        // }
        // }
        // });
        return subscribeStores;
    }
}
