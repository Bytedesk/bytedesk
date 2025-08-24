/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-24 16:30:00
 * @Description: 线程路由策略常量定义
 */
package com.bytedesk.service.routing_strategy;

/**
 * 线程路由策略相关常量
 * 
 * @author jackning 270580156@qq.com
 * @since 1.0.0
 */
public final class ThreadRoutingConstants {
    
    /**
     * 私有构造函数，防止实例化
     */
    private ThreadRoutingConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * 默认消息常量
     */
    public static final class Messages {
        public static final String DEFAULT_WELCOME = "您好，请问有什么可以帮助您？";
        public static final String DEFAULT_OFFLINE = "您好，请留言，我们会尽快回复您";
        public static final String QUEUE_NEXT = "请稍后，下一个就是您";
        public static final String QUEUE_WAITING_TEMPLATE = "当前排队人数：%d 大约等待时间：%d 分钟";
        
        private Messages() {}
    }
    
    /**
     * 时间常量
     */
    public static final class Timing {
        public static final int MESSAGE_CACHE_MINUTES = 30;
        public static final int ESTIMATED_WAIT_TIME_PER_PERSON = 2; // 分钟
        
        private Timing() {}
    }
    
    /**
     * 策略Bean名称常量
     */
    public static final class StrategyBeanNames {
        public static final String AGENT_STRATEGY = "agentThreadStrategy";
        public static final String ROBOT_STRATEGY = "robotThreadStrategy";
        public static final String WORKGROUP_STRATEGY = "workgroupThreadStrategy";
        public static final String WORKFLOW_STRATEGY = "workflowThreadStrategy";
        public static final String UNIFIED_STRATEGY = "unifiedThreadStrategy";
        
        private StrategyBeanNames() {}
    }
    
    /**
     * 错误消息常量
     */
    public static final class ErrorMessages {
        public static final String THREAD_NOT_FOUND = "Thread with uid %s not found";
        public static final String STRATEGY_NOT_SUPPORTED = "Thread type %s not supported";
        public static final String INVALID_UID = "%s UID cannot be null or empty";
        public static final String SAVE_THREAD_FAILED = "Failed to save thread %s";
        
        private ErrorMessages() {}
    }
}
