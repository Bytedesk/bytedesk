/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:51:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 09:33:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

public class TopicUtils {

    private TopicUtils() {
    }

    public static final String TOPIC_PREFIX = "/topic/";
    // internal
    public static final String TOPIC_INTERNAL_SUFFIX = "_internal";
    //
    public static final String TOPIC_FILE_ASSISTANT = "file";
    public static final String TOPIC_QUEUE_ASSISTANT = "queue";
    public static final String TOPIC_CLIPBOARD_ASSISTANT = "clipboard";
    public static final String TOPIC_INTENT_CLASSIFICATION_ASSISTANT = "intent_classification";
    public static final String TOPIC_INTENT_REWRITE_ASSISTANT = "intent_rewrite";
    public static final String TOPIC_EMOTION_ASSISTANT = "emotion";
    public static final String TOPIC_SYSTEM_NOTIFICATION = "system";
    //
    public static final String TOPIC_FILE_PREFIX = "file/";
    public static final String TOPIC_CLIPBOARD_PREFIX = "clipboard/";
    public static final String TOPIC_SYSTEM_PREFIX = "system/";
    //
    public static final String TOPIC_ORG_PREFIX = "org/";
    public static final String TOPIC_ORG_MEMBER_PREFIX = "org/member/";
    public static final String TOPIC_ORG_DEPARTMENT_PREFIX = "org/department/";
    public static final String TOPIC_ORG_GROUP_PREFIX = "org/group/";
    public static final String TOPIC_ORG_PRIVATE_PREFIX = "org/private/";
    public static final String TOPIC_ORG_ROBOT_PREFIX = "org/robot/";
    public static final String TOPIC_ORG_AGENT_PREFIX = "org/agent/";
    public static final String TOPIC_ORG_WORKGROUP_PREFIX = "org/workgroup/";
    public static final String TOPIC_ORG_UNIFIED_PREFIX = "org/unified/";
    public static final String TOPIC_ORG_KB_PREFIX = "org/kb/";
    public static final String TOPIC_ORG_KBDOC_PREFIX = "org/kbdoc/";
    public static final String TOPIC_ORG_QUEUE_PREFIX = "org/queue/";
    public static final String TOPIC_ORG_WORKFLOW_PREFIX = "org/workflow/";
    // 
    // public static final String TOPIC_ORG_AGENT_TICKET_THREAD_PREFIX = "org/ticket/agent/";
    // public static final String TOPIC_ORG_WORKGROUP_TICKET_THREAD_PREFIX = "org/ticket/workgroup/";
    // department ticket thread
    public static final String TOPIC_ORG_DEPARTMENT_TICKET_THREAD_PREFIX = "org/ticket/department/";
    // workgroup ticket thread
    public static final String TOPIC_ORG_WORKGROUP_TICKET_THREAD_PREFIX = "org/ticket/workgroup/";
    // org ticket thread (fallback when no department/workgroup)
    public static final String TOPIC_ORG_TICKET_THREAD_PREFIX = "org/ticket/org/";

    // topic格式定义：
    // 注意：开头没有 '/' ，防止stomp主题中将 '/' 替换为 '.'之后，在最前面多余一个 '.'
    // saas平台消息添加前缀platform，其余默认都是企业消息，可不添加前缀

    // 平台消息: 所有uid全平台唯一
    // 用户默认订阅：user/{user_uid}，
    // 文件助手会话：file/{user_uid}
    // 系统通知会话：system/{user_uid}
    // 群组会话：group/{group_uid}
    // 用户私聊会话：private/{self_user_uid}/{other_user_uid}
    private static final String TOPIC_USER_PATTERN = "user/%s";
    private static final String TOPIC_FILE_PATTERN = "file/%s";
    private static final String TOPIC_CLIPBOARD_PATTERN = "clipboard/%s";
    private static final String TOPIC_SYSTEM_PATTERN = "system/%s";
    private static final String TOPIC_GROUP_PATTERN = "group/%s";
    private static final String TOPIC_PRIVATE_PATTERN = "private/%s/%s";
    private static final String TOPIC_ROBOT_PATTERN = "robot/%s/%s";

    // 企业消息: 所有uid全平台唯一，包括不同表之间uid也唯一，所以未在企业topic中添加org_uid前缀为前缀
    // 用户默认订阅组织uid：org/{org_uid}
    // 用户默认订阅成员uid：org/member/{member_uid}
    // 部门消息：org/department/{department_uid}
    // 同事群组会话：org/group/{group_uid}
    // 同事私聊会话：org/member/{self_member_uid}/{other_member_uid}
    // 机器人会话：org/robot/{robot_uid}/{visitor_uid}
    private static final String TOPIC_ORG_PATTERN = TOPIC_ORG_PREFIX + "%s"; // "org/%s";
    private static final String TOPIC_ORG_MEMBER_PATTERN = TOPIC_ORG_MEMBER_PREFIX + "%s"; // "org/member/%s";
    private static final String TOPIC_ORG_MEMBER_THREAD_PATTERN = TOPIC_ORG_MEMBER_PREFIX + "%s/%s"; // "org/member/%s/%s";
    private static final String TOPIC_ORG_DEPARTMENT_PATTERN = TOPIC_ORG_DEPARTMENT_PREFIX + "%s"; // "org/department/%s";
    private static final String TOPIC_ORG_GROUP_PATTERN = TOPIC_ORG_GROUP_PREFIX + "%s"; // "org/group/%s";
    private static final String TOPIC_ORG_ROBOT_THREAD_PATTERN = TOPIC_ORG_ROBOT_PREFIX + "%s/%s"; // "org/robot/%s/%s";
    private static final String TOPIC_ORG_ROBOT_LLM_THREAD_PATTERN = TOPIC_ORG_ROBOT_PREFIX + "%s/%s/%s"; // "org/robot/%s/%s/%s";
    private static final String TOPIC_ORG_KB_THREAD_PATTERN = TOPIC_ORG_KB_PREFIX + "%s/%s"; // "org/kb/%s/%s";
    private static final String TOPIC_ORG_KBDOC_THREAD_PATTERN = TOPIC_ORG_KBDOC_PREFIX + "%s/%s"; // "org/kbdoc/%s/%s";

    // 客服:
    // 用户默认订阅客服uid：org/agent/{agent_uid}
    // 一对一客服会话：org/agent/{agent_uid}/{visitor_uid}
    // 用户默认订阅工作组uid：org/workgroup/{workgroup_uid}
    // 工作组客服会话：org/workgroup/{workgroup_uid}/{visitor_uid}
    // 统一客服入口：org/unified/{unified_uid}/{visitor_uid}
    // 部门ticket会话：org/ticket/department/{department_uid}/{visitor_uid}
    private static final String TOPIC_ORG_AGENT_PATTERN = TOPIC_ORG_AGENT_PREFIX + "%s"; // "org/agent/%s";
    private static final String TOPIC_ORG_AGENT_THREAD_PATTERN = TOPIC_ORG_AGENT_PREFIX + "%s/%s"; // "org/agent/%s/%s";
    private static final String TOPIC_ORG_WORKGROUP_PATTERN = TOPIC_ORG_WORKGROUP_PREFIX + "%s"; // "org/workgroup/%s";
    private static final String TOPIC_ORG_WORKGROUP_THREAD_PATTERN = TOPIC_ORG_WORKGROUP_PREFIX + "%s/%s"; // "org/workgroup/%s/%s";
    private static final String TOPIC_ORG_UNIFIED_THREAD_PATTERN = TOPIC_ORG_UNIFIED_PREFIX + "%s/%s"; // "org/unified/%s/%s";
    private static final String TOPIC_ORG_QUEUE_PATTERN = TOPIC_ORG_QUEUE_PREFIX + "%s"; // "org/queue/%s";
    private static final String TOPIC_ORG_DEPARTMENT_TICKET_THREAD_PATTERN = TOPIC_ORG_DEPARTMENT_TICKET_THREAD_PREFIX + "%s/%s"; // "org/ticket/department/%s/%s";
    private static final String TOPIC_ORG_WORKGROUP_TICKET_THREAD_PATTERN = TOPIC_ORG_WORKGROUP_TICKET_THREAD_PREFIX + "%s/%s"; // "org/ticket/workgroup/%s/%s";
    private static final String TOPIC_ORG_TICKET_THREAD_PATTERN = TOPIC_ORG_TICKET_THREAD_PREFIX + "%s/%s"; // "org/ticket/org/%s/%s";
    private static final String TOPIC_ORG_WORKFLOW_THREAD_PATTERN = TOPIC_ORG_WORKFLOW_PREFIX + "%s/%s"; // "org/workflow/%s/%s";


    /**
     * 判断一个主题是否为客服相关主题
     * 包括：客服、工作组、统一入口和部门工单等
     * 
     * @param topic 需要判断的主题
     * @return 如果是客服相关主题返回true，否则返回false
     */
    public static Boolean isCustomerServiceTopic(String topic) {
        return isOrgAgentTopic(topic) || 
               isOrgWorkgroupTopic(topic) || 
               isOrgUnifiedTopic(topic) || 
               isOrgDepartmentTicketThreadTopic(topic) ||
               isOrgWorkgroupTicketThreadTopic(topic) ||
               isOrgTicketThreadTopic(topic) ||
               isOrgQueueTopic(topic);
    }

    public static String getUserTopic(String userUid) {
        return String.format(TOPIC_USER_PATTERN, userUid);
    }

    public static String getFileTopic(String userUid) {
        return String.format(TOPIC_FILE_PATTERN, userUid);
    }

    public static String getClipboardTopic(String userUid) {
        return String.format(TOPIC_CLIPBOARD_PATTERN, userUid);
    }

    public static String getSystemTopic(String userUid) {
        return String.format(TOPIC_SYSTEM_PATTERN, userUid);
    }

    public static String getOrgTopic(String orgUid) {
        return String.format(TOPIC_ORG_PATTERN, orgUid);
    }

    public static String getGroupTopic(String groupUid) {
        return String.format(TOPIC_GROUP_PATTERN, groupUid);
    }

    public static String getPrivateTopic(String selfUid, String otherUid) {
        return String.format(TOPIC_PRIVATE_PATTERN, selfUid, otherUid);
    }

    public static String getRobotTopic(String robotUid, String visitorUid) {
        return String.format(TOPIC_ROBOT_PATTERN, robotUid, visitorUid);
    }

    public static String formatOrgDepartmentTopic(String departmentUid) {
        return String.format(TOPIC_ORG_DEPARTMENT_PATTERN, departmentUid);
    }

    public static Boolean isOrgMemberTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_MEMBER_PREFIX);
    }

    public static String formatOrgMemberTopic(String memberUid) {
        return String.format(TOPIC_ORG_MEMBER_PATTERN, memberUid);
    }

    public static String formatOrgMemberThreadTopic(String selfMemberUid, String otherMemberUid) {
        return String.format(TOPIC_ORG_MEMBER_THREAD_PATTERN, selfMemberUid, otherMemberUid);
    }

    public static String getOrgMemberTopicReverse(String topic) {
        String[] topicArr = topic.split("/");
        if (topicArr.length != 4) {
            throw new RuntimeException("Invalid private topic: " + topic);
        }
        return String.format(TOPIC_ORG_MEMBER_THREAD_PATTERN, topicArr[3], topicArr[2]);
    }

    //////////////////////////////////////////////////////////////////////////

    public static Boolean isOrgGroupTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_GROUP_PREFIX);
    }

    public static String getOrgGroupTopic(String groupUid) {
        return String.format(TOPIC_ORG_GROUP_PATTERN, groupUid);
    }

    //////////////////////////////////////////////////////////////////////////

    public static Boolean isOrgRobotTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_ROBOT_PREFIX);
    }

    public static String formatOrgRobotThreadTopic(String robotUid, String visitorUid) {
        return String.format(TOPIC_ORG_ROBOT_THREAD_PATTERN, robotUid, visitorUid);
    }

    public static String formatOrgRobotLlmThreadTopic(String robotUid, String userUid, String randomUid) {
        return String.format(TOPIC_ORG_ROBOT_LLM_THREAD_PATTERN, robotUid, userUid, randomUid);
    }

    // get robot uid from thread topic
    public static String getRobotUidFromThreadTopic(String threadTopic) {
        // org/robot/{robot_uid}/{visitor_uid}
        String[] topicArr = threadTopic.split("/");
        if (topicArr.length != 4) {
            throw new RuntimeException("Invalid private topic: " + threadTopic);
        }
        return topicArr[2];
    }

    //////////////////////////////////////////////////////////////////////////

    public static Boolean isOrgKbTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_KB_PREFIX);
    }

    public static String formatOrgKbThreadTopic(String kbUid, String visitorUid) {
        return String.format(TOPIC_ORG_KB_THREAD_PATTERN, kbUid, visitorUid);
    }

    //////////////////////////////////////////////////////////////////////////

    public static Boolean isOrgKbdocTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_KBDOC_PREFIX);
    }

    public static String formatOrgKbdocThreadTopic(String kbdocUid, String visitorUid) {
        return String.format(TOPIC_ORG_KBDOC_THREAD_PATTERN, kbdocUid, visitorUid);
    }

    //////////////////////////////////////////////////////////////////////////

    public static Boolean isOrgAgentTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_AGENT_PREFIX);
    }

    public static String getOrgAgentTopic(String agentUid) {
        return String.format(TOPIC_ORG_AGENT_PATTERN, agentUid);
    }

    public static String formatOrgAgentThreadTopic(String agentUid, String visitorUid) {
        return String.format(TOPIC_ORG_AGENT_THREAD_PATTERN, agentUid, visitorUid);
    }

    public static String formatOrgAgentThreadTopicInternal(String agentUid, String visitorUid) {
        return String.format(TOPIC_ORG_AGENT_THREAD_PATTERN, agentUid, visitorUid) + TOPIC_INTERNAL_SUFFIX;
    }

    // get agent uid from thread topic
    public static String getAgentUidFromThreadTopic(String threadTopic) {
        // org/agent/{agent_uid}/{visitor_uid}
        String[] topicArr = threadTopic.split("/");
        if (topicArr.length != 4) {
            throw new RuntimeException("Invalid private topic: " + threadTopic);
        }
        return topicArr[2];
    }

    //////////////////////////////////////////////////////////////////////////

    public static Boolean isOrgWorkgroupTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_WORKGROUP_PREFIX);
    }

    public static String getOrgWorkgroupTopic(String workgroupUid) {
        return String.format(TOPIC_ORG_WORKGROUP_PATTERN, workgroupUid);
    }

    public static String formatOrgWorkgroupThreadTopic(String workgroupUid, String visitorUid) {
        return String.format(TOPIC_ORG_WORKGROUP_THREAD_PATTERN, workgroupUid, visitorUid);
    }

    public static String formatOrgWorkgroupThreadTopicInternal(String workgroupUid, String visitorUid) {
        return String.format(TOPIC_ORG_WORKGROUP_THREAD_PATTERN, workgroupUid, visitorUid) + TOPIC_INTERNAL_SUFFIX;
    }

    // get workgroup uid from thread topic
    public static String getWorkgroupUidFromThreadTopic(String threadTopic) {
        // org/workgroup/{workgroup_uid}/{visitor_uid}
        String[] topicArr = threadTopic.split("/");
        if (topicArr.length != 4) {
            throw new RuntimeException("Invalid private topic: " + threadTopic);
        }
        return topicArr[2];
    }

    //////////////////////////////////////////////////////////////////////////

    public static Boolean isOrgQueueTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_QUEUE_PREFIX);
    }

    public static String getOrgQueueTopic(String agentUidOrWorkgroupUid) {
        return String.format(TOPIC_ORG_QUEUE_PATTERN, agentUidOrWorkgroupUid);
    }

    public static String getQueueTopicFromThreadTopic(String threadTopic) {
        // org/workgroup/{workgroup_uid}/{visitor_uid}
        // org/agent/{agent_uid}/{visitor_uid}
        // org/robot/{robot_uid}/{visitor_uid}
        String[] topicArr = threadTopic.split("/");
        if (topicArr.length != 4) {
            throw new RuntimeException("Invalid private topic: " + threadTopic);
        }
        return String.format(TOPIC_ORG_QUEUE_PATTERN, topicArr[2]);
    }

    // agentUid or workgroupUid or robotUid
    public static String getQueueTopicFromUid(String uid) {
        return String.format(TOPIC_ORG_QUEUE_PATTERN, uid);
    }

    //////////////////////////////////////////////////////////////////////////
    // 工单会话 department ticket thread

    public static Boolean isOrgDepartmentTicketThreadTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_DEPARTMENT_TICKET_THREAD_PREFIX);
    }

    public static String formatOrgDepartmentTicketThreadTopic(String departmentUid, String ticketUid) {
        return String.format(TOPIC_ORG_DEPARTMENT_TICKET_THREAD_PATTERN, departmentUid, ticketUid);
    }

    public static Boolean isOrgWorkgroupTicketThreadTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_WORKGROUP_TICKET_THREAD_PREFIX);
    }

    public static String formatOrgWorkgroupTicketThreadTopic(String workgroupUid, String ticketUid) {
        return String.format(TOPIC_ORG_WORKGROUP_TICKET_THREAD_PATTERN, workgroupUid, ticketUid);
    }

    public static Boolean isOrgTicketThreadTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_TICKET_THREAD_PREFIX);
    }

    public static String formatOrgTicketThreadTopic(String orgUid, String ticketUid) {
        return String.format(TOPIC_ORG_TICKET_THREAD_PATTERN, orgUid, ticketUid);
    }
    //////////////////////////////////////////////////////////////////////////
    // 统一客服入口

    public static Boolean isOrgUnifiedTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_UNIFIED_PREFIX);
    }

    public static String formatOrgUnifiedThreadTopic(String unifiedUid, String visitorUid) {    
        return String.format(TOPIC_ORG_UNIFIED_THREAD_PATTERN, unifiedUid, visitorUid);
    }

    public static String formatOrgUnifiedThreadTopicInternal(String unifiedUid, String visitorUid) {    
        return String.format(TOPIC_ORG_UNIFIED_THREAD_PATTERN, unifiedUid, visitorUid) + TOPIC_INTERNAL_SUFFIX;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // 
    public static final String formatTopicInternal(String topic) {
        return topic + TOPIC_INTERNAL_SUFFIX;
    }

    //////////////////////////////////////////////////////////////////////////
    // 工作流

    public static Boolean isOrgWorkflowTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_WORKFLOW_PREFIX);
    }
    
    // userUid or visitorUid
    public static String formatOrgWorkflowThreadTopic(String workflowUid, String userUid) {
        return String.format(TOPIC_ORG_WORKFLOW_THREAD_PATTERN, workflowUid, userUid);
    }


}
