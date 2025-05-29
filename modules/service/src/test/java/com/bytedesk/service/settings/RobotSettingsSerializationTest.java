/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-29 14:42:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-29 14:42:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.settings;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.bytedesk.ai.robot.RobotEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试 RobotSettings 的 Redis 缓存序列化问题修复
 * 验证 @JsonIgnoreProperties 注解是否能正确处理 Hibernate 懒加载代理对象
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class RobotSettingsSerializationTest {

    @Autowired
    @Qualifier("redisObjectMapper")
    private ObjectMapper redisObjectMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 测试 RobotSettings 的 JSON 序列化
     * 验证即使包含 null 的 robot 字段也能正常序列化
     */
    @Test
    public void testRobotSettingsJsonSerialization() throws Exception {
        // 创建一个基本的 RobotSettings 对象
        RobotSettings robotSettings = RobotSettings.builder()
                .defaultRobot(true)
                .offlineRobot(false)
                .nonWorktimeRobot(true)
                .robot(null) // 模拟懒加载未初始化的情况
                .build();

        // 测试序列化到 JSON
        String json = redisObjectMapper.writeValueAsString(robotSettings);
        log.info("序列化结果: {}", json);
        
        assertNotNull(json);
        assertTrue(json.contains("defaultRobot"));
        assertTrue(json.contains("true"));
        
        // 测试反序列化
        RobotSettings deserializedSettings = redisObjectMapper.readValue(json, RobotSettings.class);
        assertNotNull(deserializedSettings);
        assertEquals(robotSettings.getDefaultRobot(), deserializedSettings.getDefaultRobot());
        assertEquals(robotSettings.getOfflineRobot(), deserializedSettings.getOfflineRobot());
        assertEquals(robotSettings.getNonWorktimeRobot(), deserializedSettings.getNonWorktimeRobot());
    }

    /**
     * 测试包含 RobotEntity 的 RobotSettings 序列化
     */
    @Test
    public void testRobotSettingsWithEntitySerialization() throws Exception {
        // 创建一个简单的 RobotEntity 对象（非代理对象）
        RobotEntity robot = new RobotEntity();
        robot.setUid("test-robot-uid");
        robot.setNickname("测试机器人");

        RobotSettings robotSettings = RobotSettings.builder()
                .defaultRobot(true)
                .offlineRobot(false)
                .nonWorktimeRobot(true)
                .robot(robot)
                .build();

        // 测试序列化
        String json = redisObjectMapper.writeValueAsString(robotSettings);
        log.info("包含 Robot 的序列化结果: {}", json);
        
        assertNotNull(json);
        assertTrue(json.contains("test-robot-uid"));
        
        // 测试反序列化
        RobotSettings deserializedSettings = redisObjectMapper.readValue(json, RobotSettings.class);
        assertNotNull(deserializedSettings);
        assertNotNull(deserializedSettings.getRobot());
        assertEquals("test-robot-uid", deserializedSettings.getRobot().getUid());
    }

    /**
     * 测试 Redis 缓存存储和读取
     * 验证 RobotSettings 是否能正确缓存到 Redis 并读取
     */
    @Test
    public void testRobotSettingsRedisCaching() {
        String cacheKey = "test:robot-settings:1";
        
        // 创建测试对象
        RobotSettings robotSettings = RobotSettings.builder()
                .defaultRobot(true)
                .offlineRobot(true)
                .nonWorktimeRobot(false)
                .robot(null)
                .build();

        try {
            // 存储到 Redis
            redisTemplate.opsForValue().set(cacheKey, robotSettings);
            log.info("成功存储到 Redis: {}", cacheKey);

            // 从 Redis 读取
            Object cachedObject = redisTemplate.opsForValue().get(cacheKey);
            assertNotNull(cachedObject);
            
            // 验证类型和内容
            assertTrue(cachedObject instanceof RobotSettings);
            RobotSettings cachedSettings = (RobotSettings) cachedObject;
            
            assertEquals(robotSettings.getDefaultRobot(), cachedSettings.getDefaultRobot());
            assertEquals(robotSettings.getOfflineRobot(), cachedSettings.getOfflineRobot());
            assertEquals(robotSettings.getNonWorktimeRobot(), cachedSettings.getNonWorktimeRobot());
            
            log.info("Redis 缓存测试通过: {}", cachedSettings);
            
        } finally {
            // 清理测试数据
            redisTemplate.delete(cacheKey);
        }
    }

    /**
     * 测试业务逻辑方法
     */
    @Test
    public void testShouldTransferToRobotLogic() {
        RobotSettings robotSettings = RobotSettings.builder()
                .defaultRobot(false)
                .offlineRobot(true)
                .nonWorktimeRobot(true)
                .build();

        // 测试离线机器人逻辑
        assertTrue(robotSettings.shouldTransferToRobot(true, true)); // 离线状态
        assertFalse(robotSettings.shouldTransferToRobot(false, true)); // 在线状态

        // 测试非工作时间机器人逻辑
        assertTrue(robotSettings.shouldTransferToRobot(false, false)); // 非工作时间
        assertFalse(robotSettings.shouldTransferToRobot(false, true)); // 工作时间

        // 测试默认机器人逻辑
        robotSettings.setDefaultRobot(true);
        assertTrue(robotSettings.shouldTransferToRobot(false, true)); // 默认机器人总是优先
    }
}
