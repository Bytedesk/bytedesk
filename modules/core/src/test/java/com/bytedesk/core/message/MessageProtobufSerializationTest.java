package com.bytedesk.core.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.utils.BdDateUtils;

/**
 * 回归测试：验证 MessageProtobuf 的 fastjson2 序列化包含 createdAt 字段。
 *
 * 背景：MessageProtobuf 之前在手写 getCreatedAt() 上挂了 Jackson @JsonIgnore，
 * fastjson2 读取该注解后将 createdAt 属性整体忽略，导致 toJson() 不输出 createdAt。
 * 经 MessageConvertUtils.toProtoBean() 转换为 protobuf 后，desktop MQTT 客户端
 * getCreatedat() 得到空串，moment('') 解析为 NaN，时间戳显示异常。
 * visitor 走 STOMP(JSON) 且客户端自填 createdAt，故表现正常。
 *
 * 修复：删除手写 getCreatedAt()(String)，改由 Lombok 生成 ZonedDateTime getter，
 * 配合 field 上的 @JSONField(format = "yyyy-MM-dd HH:mm:ss") 控制序列化格式。
 */
class MessageProtobufSerializationTest {

    @Test
    void toJsonShouldContainCreatedAt() {
        ZonedDateTime now = BdDateUtils.now();
        MessageProtobuf message = MessageProtobuf.builder()
                .uid("test-uid")
                .type(MessageTypeEnum.TEXT)
                .content("hello")
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(now)
                .channel(ChannelEnum.WEB)
                .extra("{}")
                .build();

        String json = message.toJson();

        JSONObject obj = JSON.parseObject(json);
        assertThat(obj.containsKey("createdAt")).isTrue();
        assertThat(obj.getString("createdAt")).isNotEmpty();
        // 应为 yyyy-MM-dd HH:mm:ss 格式
        assertThat(obj.getString("createdAt")).contains("-");
        // 不应再出现冗余的 getter 派生字段
        assertThat(obj.containsKey("createdAtDateTime")).isFalse();
        assertThat(obj.containsKey("timestamp")).isFalse();
    }

    @Test
    void toJsonAndFromJsonShouldPreserveCreatedAt() {
        ZonedDateTime now = BdDateUtils.now();
        MessageProtobuf message = MessageProtobuf.builder()
                .uid("test-uid-2")
                .type(MessageTypeEnum.TEXT)
                .content("hello2")
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(now)
                .channel(ChannelEnum.WEB)
                .extra("{}")
                .build();

        String json = message.toJson();
        MessageProtobuf parsed = MessageProtobuf.fromJson(json);

        assertThat(parsed.getCreatedAt()).isNotNull();
        assertThat(parsed.getCreatedAtDateTime()).isNotNull();
    }
}
