/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-28 12:08:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.protobuf.util;

import java.lang.reflect.Method;

import com.google.protobuf.GeneratedMessageV3;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import lombok.Getter;
import lombok.Setter;

/**
 * ProtocbufRedisSerializer
 */
@Setter
@Getter
public class ProtobufRedisSerializer<T> implements RedisSerializer<T> {

    private Class<T> type;

    public ProtobufRedisSerializer(Class<T> type) {
        this.type = type;
    }

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        try {
            GeneratedMessageV3 gm = (GeneratedMessageV3) t;
            return gm.toByteArray();
        } catch (Exception ex) {
            throw new SerializationException("Cannot serialize", ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        try {
            Method method = type.getMethod("parseFrom", new Class[] { bytes.getClass() });
            return (T) method.invoke(type, new Object[] { bytes });
        } catch (Exception ex) {
            throw new SerializationException("Cannot deserialize", ex);
        }
    }
}