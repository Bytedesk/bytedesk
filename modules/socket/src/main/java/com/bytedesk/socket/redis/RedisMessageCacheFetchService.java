package com.bytedesk.socket.redis;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;

import com.bytedesk.socket.protobuf.constant.ProtobufConsts;
import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * 缓存消息
 */
// @Slf4j
@Service
@AllArgsConstructor
public class RedisMessageCacheFetchService {

    private final StringRedisTemplate stringRedisTemplate;

    // 根据访客端uid
    // private static final String MESSAGE_FETCH_PREFIX =
    // "bytedeskim:message:fetch:";

    // 缓存7天-访客uid，供客服端根据访客visitorUid拉取
    public void addMessage(String visitorUid, final String messageJson) {
        stringRedisTemplate.opsForSet().add(ProtobufConsts.MESSAGE_FETCH_PREFIX + visitorUid, messageJson);
        stringRedisTemplate.expire(ProtobufConsts.MESSAGE_FETCH_PREFIX + visitorUid, 7, TimeUnit.DAYS);
    }

    // 消息撤回，从缓存中删除
    public void removeMessage(String visitorUid, String mid) {
        Set<String> jsonSet = stringRedisTemplate.opsForSet().members(ProtobufConsts.MESSAGE_FETCH_PREFIX + visitorUid);
        Iterator<String> iterator = jsonSet.iterator();
        while (iterator.hasNext()) {
            String jsonString = iterator.next();
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            String midString = jsonObject.getString("mid");
            if (midString.equals(mid)) {
                // log.debug("do uid {}, mid {}", uid, mid);
                stringRedisTemplate.opsForSet().remove(ProtobufConsts.MESSAGE_FETCH_PREFIX + visitorUid, jsonString);
            }
        }
    }

    public Set<String> getMessages(String visitorUid) {
        return (Set<String>) stringRedisTemplate.opsForSet().members(ProtobufConsts.MESSAGE_FETCH_PREFIX + visitorUid);
    }

    public Long length(String visitorUid) {
        return stringRedisTemplate.opsForSet().size(ProtobufConsts.MESSAGE_FETCH_PREFIX + visitorUid);
    }

    // 根据topic
    // private static final String MESSAGE_TOPIC_PREFIX =
    // "bytedeskim:message:fetch:topic:";

    // 缓存7天
    public void addMessageByTopic(String topic, final String messageJson) {
        stringRedisTemplate.opsForSet().add(ProtobufConsts.MESSAGE_TOPIC_PREFIX + topic, messageJson);
        stringRedisTemplate.expire(ProtobufConsts.MESSAGE_TOPIC_PREFIX + topic, 7, TimeUnit.DAYS);
    }

    public Set<String> getMessagesByTopic(String topic) {
        return (Set<String>) stringRedisTemplate.opsForSet().members(ProtobufConsts.MESSAGE_TOPIC_PREFIX + topic);
    }

    // 更新缓存中消息已读状态
    public void updateMessageStatusByTopic(String topic, String mid) {
        Set<String> jsonSet = stringRedisTemplate.opsForSet().members(ProtobufConsts.MESSAGE_TOPIC_PREFIX + topic);
        Iterator<String> iterator = jsonSet.iterator();
        while (iterator.hasNext()) {
            String jsonString = iterator.next();
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            String midString = jsonObject.getString("mid");
            if (midString.equals(mid)) {
                stringRedisTemplate.opsForSet().remove(ProtobufConsts.MESSAGE_TOPIC_PREFIX + topic, jsonString);
                //
                jsonObject.put("status", "read");
                addMessageByTopic(topic, jsonObject.toJSONString());
            }
        }
    }

    // 消息撤回，从缓存中删除
    public void removeMessageByTopic(String topic, String mid) {
        Set<String> jsonSet = stringRedisTemplate.opsForSet().members(ProtobufConsts.MESSAGE_TOPIC_PREFIX + topic);
        Iterator<String> iterator = jsonSet.iterator();
        while (iterator.hasNext()) {
            String jsonString = iterator.next();
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            String midString = jsonObject.getString("mid");
            if (midString.equals(mid)) {
                stringRedisTemplate.opsForSet().remove(ProtobufConsts.MESSAGE_TOPIC_PREFIX + topic, jsonString);
            }
        }
    }

    public Long lengthTopic(String topic) {
        return stringRedisTemplate.opsForSet().size(ProtobufConsts.MESSAGE_TOPIC_PREFIX + topic);
    }

    // 客服未读消息
    // private static final String MESSAGE_AGENT_UNREAD_PREFIX =
    // "bytedeskim:message:fetch:agentunread:";

    // 缓存2天-客服未读消息
    public void addMessageAgentUnread(String agentUid, final String messageJson) {
        stringRedisTemplate.opsForSet().add(ProtobufConsts.MESSAGE_AGENT_UNREAD_PREFIX + agentUid, messageJson);
        stringRedisTemplate.expire(ProtobufConsts.MESSAGE_AGENT_UNREAD_PREFIX + agentUid, 2, TimeUnit.DAYS);
    }

    public Set<String> getMessagesAgentUnread(String agentUid) {
        return (Set<String>) stringRedisTemplate.opsForSet()
                .members(ProtobufConsts.MESSAGE_AGENT_UNREAD_PREFIX + agentUid);
    }

    public void removeMessageAgentUnread(String agentUid, final String mid) {
        //
        Set<String> jsonSet = stringRedisTemplate.opsForSet()
                .members(ProtobufConsts.MESSAGE_AGENT_UNREAD_PREFIX + agentUid);
        Iterator<String> iterator = jsonSet.iterator();
        while (iterator.hasNext()) {
            String jsonString = iterator.next();
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            String midString = jsonObject.getString("mid");
            if (midString.equals(mid)) {
                // log.debug("do agentUid {}, mid {}", agentUid, mid);
                stringRedisTemplate.opsForSet().remove(ProtobufConsts.MESSAGE_AGENT_UNREAD_PREFIX + agentUid,
                        jsonString);
            }
        }
    }

    public void clearMessagesAgentUnread(String agentUid) {
        //
        Set<String> jsonSet = stringRedisTemplate.opsForSet()
                .members(ProtobufConsts.MESSAGE_AGENT_UNREAD_PREFIX + agentUid);
        Iterator<String> iterator = jsonSet.iterator();
        while (iterator.hasNext()) {
            String jsonString = iterator.next();
            // 删除
            stringRedisTemplate.opsForSet().remove(ProtobufConsts.MESSAGE_AGENT_UNREAD_PREFIX + agentUid, jsonString);
        }
    }

    // 访客未读消息
    // private static final String MESSAGE_VISITOR_UNREAD_PREFIX =
    // "bytedeskim:message:fetch:visitorunread:";

    // 缓存2天-访客未读消息
    public void addMessageVisitorUnread(String visitorUid, final String messageJson) {
        stringRedisTemplate.opsForSet().add(ProtobufConsts.MESSAGE_VISITOR_UNREAD_PREFIX + visitorUid, messageJson);
        stringRedisTemplate.expire(ProtobufConsts.MESSAGE_VISITOR_UNREAD_PREFIX + visitorUid, 2, TimeUnit.DAYS);
    }

    public Set<String> getMessagesVisitorUnread(String visitorUid) {
        return (Set<String>) stringRedisTemplate.opsForSet()
                .members(ProtobufConsts.MESSAGE_VISITOR_UNREAD_PREFIX + visitorUid);
    }

    public void removeMessageVisitorUnread(String visitorUid, final String mid) {
        //
        Set<String> jsonSet = stringRedisTemplate.opsForSet()
                .members(ProtobufConsts.MESSAGE_VISITOR_UNREAD_PREFIX + visitorUid);
        Iterator<String> iterator = jsonSet.iterator();
        while (iterator.hasNext()) {
            String jsonString = iterator.next();
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            String midString = jsonObject.getString("mid");
            if (midString.equals(mid)) {
                // log.debug("do visitorUid {}, mid {}", visitorUid, mid);
                stringRedisTemplate.opsForSet().remove(ProtobufConsts.MESSAGE_VISITOR_UNREAD_PREFIX + visitorUid,
                        jsonString);
            }
        }
    }

    public void clearMessagesVisitorUnread(String visitorUid) {
        //
        Set<String> jsonSet = stringRedisTemplate.opsForSet()
                .members(ProtobufConsts.MESSAGE_VISITOR_UNREAD_PREFIX + visitorUid);
        Iterator<String> iterator = jsonSet.iterator();
        while (iterator.hasNext()) {
            String jsonString = iterator.next();
            // 删除
            stringRedisTemplate.opsForSet().remove(ProtobufConsts.MESSAGE_VISITOR_UNREAD_PREFIX + visitorUid,
                    jsonString);
        }
    }

}