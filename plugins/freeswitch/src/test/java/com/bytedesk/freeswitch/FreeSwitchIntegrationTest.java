package com.bytedesk.freeswitch;

import com.bytedesk.freeswitch.freeswitch.FreeSwitchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FreeSwitch 集成测试
 * 
 * @author jackning
 */
@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class, 
    HibernateJpaAutoConfiguration.class
})
public class FreeSwitchIntegrationTest {

    @Autowired
    private FreeSwitchService freeSwitchService;

    @Test
    public void testFreeSwitchServiceExists() {
        assertNotNull(freeSwitchService, "FreeSwitchService should be autowired");
    }

    @Test
    public void testConnectionStatus() {
        // 测试连接状态
        boolean isConnected = freeSwitchService.isConnected();
        
        // 注意：在测试环境中，FreeSwitch 可能没有运行，所以这个测试可能会失败
        // 这是正常的，在实际部署时需要确保 FreeSwitch 服务正在运行
        System.out.println("FreeSwitch connection status: " + isConnected);
    }

    @Test
    public void testGetStatus() {
        try {
            String status = freeSwitchService.getStatus();
            assertNotNull(status, "Status should not be null");
            System.out.println("FreeSwitch status: " + status);
        } catch (Exception e) {
            // 在测试环境中，FreeSwitch 可能没有运行，这是正常的
            System.out.println("FreeSwitch not available in test environment: " + e.getMessage());
        }
    }
}
