/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-28 11:53:03
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
import com.bytedesk.socket.mqtt.service.MqttRetainMessageStoreService;
import com.bytedesk.socket.mqtt.model.MqttRetainMessage;
import com.bytedesk.socket.mqtt.redis.MqttRetainMessageCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MqttRetainMessageStoreService {

    private final MqttRetainMessageCache mqttRetainMessageCache;

    public void put(String topic, MqttRetainMessage retainMessageStore) {
        mqttRetainMessageCache.put(topic, retainMessageStore);
    }

    public MqttRetainMessage get(String topic) {
        return mqttRetainMessageCache.get(topic);
    }

    public void remove(String topic) {
        mqttRetainMessageCache.remove(topic);
    }

    public boolean containsKey(String topic) {
        return mqttRetainMessageCache.containsKey(topic);
    }

    public List<MqttRetainMessage> search(String topicFilter) {
        List<MqttRetainMessage> retainMessageStores = new ArrayList<MqttRetainMessage>();
        if (!StrUtil.contains(topicFilter, '#') && !StrUtil.contains(topicFilter, '+')) {
            if (mqttRetainMessageCache.containsKey(topicFilter)) {
                retainMessageStores.add(mqttRetainMessageCache.get(topicFilter));
            }
        } else {
            mqttRetainMessageCache.all().forEach((topic, val) -> {
                if (StrUtil.split(topic, '/').size() >= StrUtil.split(topicFilter, '/').size()) {
                    List<String> splitTopics = StrUtil.split(topic, '/');
                    List<String> spliteTopicFilters = StrUtil.split(topicFilter, '/');
                    String newTopicFilter = "";
                    for (int i = 0; i < spliteTopicFilters.size(); i++) {
                        String value = spliteTopicFilters.get(i);
                        if (value.equals("+")) {
                            newTopicFilter = newTopicFilter + "+/";
                        } else if (value.equals("#")) {
                            newTopicFilter = newTopicFilter + "#/";
                            break;
                        } else {
                            newTopicFilter = newTopicFilter + splitTopics.get(i) + "/";
                        }
                    }
                    newTopicFilter = StrUtil.removeSuffix(newTopicFilter, "/");
                    if (topicFilter.equals(newTopicFilter)) {
                        retainMessageStores.add(val);
                    }
                }
            });
        }
        return retainMessageStores;
    }
}
