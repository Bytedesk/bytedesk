/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-16 09:38:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.cache;

// import com.bytedesk.socket.mqtt.model.MqttSubscribe;
// import com.bytedesk.socket.mqtt.util.MqttConsts;
// // import org.springframework.data.redis.core.RedisTemplate;
// // import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.stereotype.Service;

// import java.util.*;
// import java.util.concurrent.ConcurrentHashMap;

// /**
//  * topic通配符说明：
//  * 单层通配符"+"：只能匹配一层主题。例如，"aaa/+"可以匹配"aaa/bbb"，但不能匹配"aaa/bbb/ccc"。
//  * 多层通配符"#"：可以匹配多层主题。例如，"aaa/#"不但可以匹配"aaa/bbb"，还可以匹配"aaa/bbb/ccc/ddd"。它必须作为主题的最后一个级别使用，并且只能出现在最后
//  * 
//  * @author jackning
//  */
// @Service
// public class MqttSubscribeWildcardCache {

//     public static final String CACHE_PRE = MqttConsts.MQTT_PREFIX + "subwildcard:";

//     public static final String CACHE_CLIENT_PRE = MqttConsts.MQTT_PREFIX + "client:";

//     // @Autowired
//     // private StringRedisTemplate stringRedisTemplate;

//     // @Autowired
//     // private RedisTemplate<String, Serializable> redisTemplate;

//     public void put(String topic, String clientId, MqttSubscribe subscribeStore) {
//         // redisTemplate.opsForHash().put(CACHE_PRE + topic, clientId, subscribeStore);
//         // stringRedisTemplate.opsForSet().add(CACHE_CLIENT_PRE + clientId, topic);
//     }

//     public MqttSubscribe get(String topic, String clientId) {
//         // return (MqttSubscribe) redisTemplate.opsForHash().get(CACHE_PRE + topic, clientId);
//         return null;
//     }

//     public boolean containsKey(String topic, String clientId) {
//         // return redisTemplate.opsForHash().hasKey(CACHE_PRE + topic, clientId);
//         return false;
//     }

//     public void remove(String topic, String clientId) {
//         // stringRedisTemplate.opsForSet().remove(CACHE_CLIENT_PRE + clientId, topic);
//         // redisTemplate.opsForHash().delete(CACHE_PRE + topic, clientId);
//     }

//     public void removeForClient(String clientId) {
//         // for (String topic : stringRedisTemplate.opsForSet().members(CACHE_CLIENT_PRE + clientId)) {
//         //     redisTemplate.opsForHash().delete(CACHE_PRE + topic, clientId);
//         // }
//         // stringRedisTemplate.delete(CACHE_CLIENT_PRE + clientId);
//     }

//     public Map<String, ConcurrentHashMap<String, MqttSubscribe>> all() {
//         Map<String, ConcurrentHashMap<String, MqttSubscribe>> map = new HashMap<>();
//         // Set<String> set = redisTemplate.keys(CACHE_PRE + "*");
//         // if (set != null && !set.isEmpty()) {
//         //     set.forEach(
//         //             entry -> {
//         //                 ConcurrentHashMap<String, MqttSubscribe> map1 = new ConcurrentHashMap<>();
//         //                 Map<Object, Object> map2 = redisTemplate.opsForHash().entries(entry);
//         //                 if (map2 != null && !map2.isEmpty()) {
//         //                     map2.forEach((k, v) -> {
//         //                         map1.put((String) k, (MqttSubscribe) v);
//         //                     });
//         //                     map.put(entry.substring(CACHE_PRE.length()), map1);
//         //                 }
//         //             });
//         // }
//         return map;
//     }

//     public List<MqttSubscribe> all(String topic) {
//         List<MqttSubscribe> list = new ArrayList<>();
//         // Map<Object, Object> map = redisTemplate.opsForHash().entries(CACHE_PRE + topic);
//         // if (map != null && !map.isEmpty()) {
//         //     map.forEach((k, v) -> {
//         //         list.add((MqttSubscribe) v);
//         //     });
//         // }
//         return list;
//     }
// }
