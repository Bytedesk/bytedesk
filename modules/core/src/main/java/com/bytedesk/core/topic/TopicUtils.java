/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:51:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 14:21:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
    //
    public static final String TOPIC_FILE_ASSISTANT = "file";
    public static final String TOPIC_SYSTEM_NOTIFICATION = "system";
    //
    // public static final String TOPIC_PRIVATE_PREFIX = "private/";
    // public static final String TOPIC_GROUP_PREFIX = "group/";
    public static final String TOPIC_FILE_PREFIX = "file/";
    public static final String TOPIC_SYSTEM_PREFIX = "system/";
    // public static final String TOPIC_ROBOT_PREFIX = "robot/";
    //
    public static final String TOPIC_ORG_PREFIX = "org/";
    public static final String TOPIC_ORG_MEMBER_PREFIX = "org/member/";
    public static final String TOPIC_ORG_DEPARTMENT_PREFIX = "org/department/";
    public static final String TOPIC_ORG_GROUP_PREFIX = "org/group/";
    public static final String TOPIC_ORG_PRIVATE_PREFIX = "org/private/";
    public static final String TOPIC_ORG_ROBOT_PREFIX = "org/robot/";
    public static final String TOPIC_ORG_AGENT_PREFIX = "org/agent/";
    public static final String TOPIC_ORG_WORKGROUP_PREFIX = "org/workgroup/";
    public static final String TOPIC_ORG_KB_PREFIX = "org/kb/";
    public static final String TOPIC_ORG_KBDOC_PREFIX = "org/kbdoc/";
    public static final String TOPIC_ORG_QUEUE_PREFIX = "org/queue/";

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
    private static final String TOPIC_SYSTEM_PATTERN = "system/%s";
    private static final String TOPIC_GROUP_PATTERN = "group/%s";
    private static final String TOPIC_PRIVATE_PATTERN = "private/%s/%s";
    private static final String TOPIC_ROBOT_PATTERN = "robot/%s/%s";
    // private static final String TOPIC_AGENT_PATTERN = "agent/%s";
    // private static final String TOPIC_WORKGROUP_PATTERN = "workgroup/%s";
    // private static final String TOPIC_QUEUE_PATTERN = "queue/%s";

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
    private static final String TOPIC_ORG_KB_THREAD_PATTERN = TOPIC_ORG_KB_PREFIX + "%s/%s"; // "org/kb/%s/%s";
    private static final String TOPIC_ORG_KBDOC_THREAD_PATTERN = TOPIC_ORG_KBDOC_PREFIX + "%s/%s"; // "org/kbdoc/%s/%s";

    // 客服:
    // 用户默认订阅客服uid：org/agent/{agent_uid}
    // 一对一客服会话：org/agent/{agent_uid}/{visitor_uid}
    // 用户默认订阅技能组uid：org/workgroup/{workgroup_uid}
    // 废弃：技能组客服会话：org/workgroup/{workgroup_uid}/{agent_uid}/{visitor_uid}
    // 技能组客服会话：org/workgroup/{workgroup_uid}/{visitor_uid}
    private static final String TOPIC_ORG_AGENT_PATTERN = TOPIC_ORG_AGENT_PREFIX + "%s"; // "org/agent/%s";
    private static final String TOPIC_ORG_AGENT_THREAD_PATTERN = TOPIC_ORG_AGENT_PREFIX + "%s/%s"; // "org/agent/%s/%s";
    private static final String TOPIC_ORG_WORKGROUP_PATTERN = TOPIC_ORG_WORKGROUP_PREFIX + "%s"; // "org/workgroup/%s";
    // private static final String TOPIC_ORG_WORKGROUP_THREAD_PATTERN = TOPIC_ORG_WORKGROUP_PREFIX + "%s/%s/%s"; // "org/workgroup/%s/%s/%s";
    private static final String TOPIC_ORG_WORKGROUP_THREAD_PATTERN = TOPIC_ORG_WORKGROUP_PREFIX + "%s/%s"; // "org/workgroup/%s/%s";
    private static final String TOPIC_ORG_QUEUE_PATTERN = TOPIC_ORG_QUEUE_PREFIX + "%s"; // "org/queue/%s";

    //
    public static String getUserTopic(String userUid) {
        return String.format(TOPIC_USER_PATTERN, userUid);
    }

    public static String getFileTopic(String userUid) {
        return String.format(TOPIC_FILE_PATTERN, userUid);
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

    //////////////////////////////////////////////////////////////////////////

    public static Boolean isOrgWorkgroupTopic(String topic) {
        return topic.startsWith(TOPIC_ORG_WORKGROUP_PREFIX);
    }

    public static String getOrgWorkgroupTopic(String workgroupUid) {
        return String.format(TOPIC_ORG_WORKGROUP_PATTERN, workgroupUid);
    }

    // public static String formatOrgWorkgroupThreadTopic(String workgroupUid, String agentUid, String visitorUid) {
    //     return String.format(TOPIC_ORG_WORKGROUP_THREAD_PATTERN, workgroupUid, agentUid, visitorUid);
    // }
    public static String formatOrgWorkgroupThreadTopic(String workgroupUid, String visitorUid) {
        return String.format(TOPIC_ORG_WORKGROUP_THREAD_PATTERN, workgroupUid, visitorUid);
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
        String[] topicArr = threadTopic.split("/");
        if (topicArr.length != 4) {
            throw new RuntimeException("Invalid private topic: " + threadTopic);
        }
        return String.format(TOPIC_ORG_QUEUE_PATTERN, topicArr[2]);
    }
    


}
