/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 22:25:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-24 15:10:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

// 国际化常量
public class I18Consts {

    public static final String I18N_ARG_SEPARATOR = "|";

    private I18Consts() {
    }

    public static String withArgs(String key, Object... args) {
        if (args == null || args.length == 0) {
            return key;
        }
        StringBuilder builder = new StringBuilder(key);
        for (Object arg : args) {
            builder.append(I18N_ARG_SEPARATOR);
            if (arg != null) {
                builder.append(String.valueOf(arg).replace(I18N_ARG_SEPARATOR, "/"));
            }
        }
        return builder.toString();
    }

    // 国际化常量定义
    public static final String I18N_PREFIX = "i18n.";
    public static final String I18N_DESCRIPTION_PREFIX = "i18n.description.";

    // 城市导入/初始化相关（后端返回 i18n key，前端通过 translateString 翻译）
    public static final String I18N_CITY_RESET_SUCCESS = I18N_PREFIX + "city.reset.success";
    public static final String I18N_CITY_INIT_SCHEDULED = I18N_PREFIX + "city.init.scheduled";
    public static final String I18N_CITY_INIT_SKIPPED = I18N_PREFIX + "city.init.skipped";

    // 通用操作动作与模块标题
    public static final String I18N_TAG = I18N_PREFIX + "tag";
    public static final String I18N_APNS_P12 = I18N_PREFIX + "apns.p12";
    public static final String I18N_APNS_TOKEN = I18N_PREFIX + "apns.token";
    public static final String I18N_APNS_PUSH = I18N_PREFIX + "apns.push";
    public static final String I18N_EMAIL_PUSH = I18N_PREFIX + "email.push";
    public static final String I18N_DATA_SOURCE = I18N_PREFIX + "data.source";
    public static final String I18N_TOOL = I18N_PREFIX + "tool";
    public static final String I18N_TOOL_APPROVAL = I18N_PREFIX + "tool.approval";
    public static final String I18N_TOOL_AUDIT = I18N_PREFIX + "tool.audit";
    public static final String I18N_TOOL_GUARD = I18N_PREFIX + "tool.guard";
    public static final String I18N_TOOL_RULE = I18N_PREFIX + "tool.rule";
    public static final String I18N_PLAN = I18N_PREFIX + "plan";
    public static final String I18N_PLAN_SUB = I18N_PREFIX + "plan.sub";
    public static final String I18N_SKILL = I18N_PREFIX + "skill";
    public static final String I18N_MEMORY = I18N_PREFIX + "memory";
    public static final String I18N_MULTI_MODAL = I18N_PREFIX + "multi.modal";
    public static final String I18N_MCP_SERVER = I18N_PREFIX + "mcp.server";
    public static final String I18N_MEMBER = I18N_PREFIX + "member";
    public static final String I18N_MEMBER_NOT_FOUND = I18N_PREFIX + "member.not.found"; // 成员未找到
    public static final String I18N_DEPARTMENT = I18N_PREFIX + "department";
    public static final String I18N_RELATION = I18N_PREFIX + "relation";
    public static final String I18N_CATEGORY = I18N_PREFIX + "category";
    public static final String I18N_ASSET = I18N_PREFIX + "asset";
    public static final String I18N_CALENDAR = I18N_PREFIX + "calendar";
    public static final String I18N_EMAIL = I18N_PREFIX + "email";
    public static final String I18N_SMS_PUSH = I18N_PREFIX + "sms.push";
    public static final String I18N_SCHEDULE = I18N_PREFIX + "schedule";
    public static final String I18N_ROLE = I18N_PREFIX + "role";
    public static final String I18N_TASK_LIST = I18N_PREFIX + "task.list";
    public static final String I18N_WORKFLOW_NODE = I18N_PREFIX + "workflow.node";
    public static final String I18N_ORGANIZATION = I18N_PREFIX + "organization";
    public static final String I18N_ORGANIZATION_APPLY = I18N_PREFIX + "organization.apply";
    public static final String I18N_ORGANIZATION_SETTINGS = I18N_PREFIX + "organization.settings";
    public static final String I18N_CITY = I18N_PREFIX + "city";
    public static final String I18N_WORKFLOW = I18N_PREFIX + "workflow";
    public static final String I18N_WORKFLOW_SETTINGS = I18N_PREFIX + "workflow.settings";
    public static final String I18N_WEBRTC_SETTINGS = I18N_PREFIX + "webrtc.settings.management";
    public static final String I18N_AUDIO_RECORDINGS = I18N_PREFIX + "audio.recordings.management";
    public static final String I18N_VIDEO_RECORDINGS = I18N_PREFIX + "video.recordings.management";
    public static final String I18N_DEVICE = I18N_PREFIX + "device";
    public static final String I18N_EQUIPMENT = I18N_PREFIX + "equipment";
    public static final String I18N_LOGISTICS = I18N_PREFIX + "logistics";
    public static final String I18N_DOCUMENT = I18N_PREFIX + "document";
    public static final String I18N_WORKFLOW_EDGE = I18N_PREFIX + "workflow.edge";
    public static final String I18N_TOPIC_SUBSCRIPTION = I18N_PREFIX + "topic.subscription";
    public static final String I18N_MENU = I18N_PREFIX + "menu";
    public static final String I18N_QUARTZ_TASK = I18N_PREFIX + "quartz.task";
    public static final String I18N_OPEN_PLATFORM = I18N_PREFIX + "open.platform";
    public static final String I18N_APP = I18N_PREFIX + "app";
        public static final String I18N_ASR = I18N_PREFIX + "asr.management";
        public static final String I18N_TTS = I18N_PREFIX + "tts.management";
        public static final String I18N_OCR = I18N_PREFIX + "ocr.management";
    public static final String I18N_AI_TOKEN_STATISTIC = I18N_PREFIX + "ai.token.statistic";
    public static final String I18N_META_APP = I18N_PREFIX + "meta.app";
    public static final String I18N_SHOP_APP = I18N_PREFIX + "shop.app";
    public static final String I18N_SERVER_METRICS = I18N_PREFIX + "server.metrics";
    public static final String I18N_ANNOUNCEMENT = I18N_PREFIX + "announcement";
    public static final String I18N_SERVER = I18N_PREFIX + "server";
    public static final String I18N_REPORT = I18N_PREFIX + "report";
    public static final String I18N_WORKFLOW_LOG = I18N_PREFIX + "workflow.log";
    public static final String I18N_MOMENT = I18N_PREFIX + "moment";
    public static final String I18N_CONNECTION = I18N_PREFIX + "connection";
    public static final String I18N_TOPIC = I18N_PREFIX + "topic";
    public static final String I18N_USER = I18N_PREFIX + "user";
    public static final String I18N_TOKEN = I18N_PREFIX + "token";
    public static final String I18N_EMAIL_MESSAGE = I18N_PREFIX + "email.message";
    public static final String I18N_TASK = I18N_PREFIX + "task";
    public static final String I18N_TASK_COMMENT = I18N_PREFIX + "task.comment";
    public static final String I18N_GROUP = I18N_PREFIX + "group.management";
    public static final String I18N_MESSAGE = I18N_PREFIX + "message";
    public static final String I18N_MESSAGE_NOT_FOUND = I18N_PREFIX + "message.not.found"; // 消息未找到
    public static final String I18N_TRANSLATE_BAIDU_CONFIGURED = I18N_PREFIX + "translate.baidu.configured";
    public static final String I18N_TRANSLATE_BAIDU_NOT_CONFIGURED = I18N_PREFIX + "translate.baidu.not.configured";
    public static final String I18N_TRANSLATE_CONTENT_REQUIRED = I18N_PREFIX + "translate.content.required";
    public static final String I18N_UPLOAD = I18N_PREFIX + "upload";
    public static final String I18N_WORKFLOW_VARIABLE = I18N_PREFIX + "workflow.variable";
    public static final String I18N_WORKFLOW_LOCAL_VARIABLE = I18N_PREFIX + "workflow.local.variable";
    public static final String I18N_THREAD = I18N_PREFIX + "thread.management";
    public static final String I18N_THREAD_NOT_FOUND = I18N_PREFIX + "thread.not.found"; // 会话未找到
    public static final String I18N_THREAD_NOT_FOUND_WITH_UID = I18N_PREFIX + "thread.not.found.with.uid"; // 指定 uid 的会话未找到
    public static final String I18N_THREAD_TYPE_AGENT = I18N_PREFIX + "thread.type.agent";
    public static final String I18N_THREAD_TYPE_WORKGROUP = I18N_PREFIX + "thread.type.workgroup";
    public static final String I18N_THREAD_TYPE_ROBOT = I18N_PREFIX + "thread.type.robot";
    public static final String I18N_THREAD_TYPE_MEMBER = I18N_PREFIX + "thread.type.member";
    public static final String I18N_THREAD_TYPE_GROUP = I18N_PREFIX + "thread.type.group";
    public static final String I18N_THREAD_TYPE_FEEDBACK = I18N_PREFIX + "thread.type.feedback";
    public static final String I18N_THREAD_TYPE_ASSISTANT = I18N_PREFIX + "thread.type.assistant";
    public static final String I18N_THREAD_TYPE_CHANNEL = I18N_PREFIX + "thread.type.channel";
    public static final String I18N_THREAD_TYPE_LOCAL = I18N_PREFIX + "thread.type.local";
    public static final String I18N_THREAD_TYPE_FRIEND = I18N_PREFIX + "thread.type.friend";
    public static final String I18N_THREAD_TYPE_TICKET_INTERNAL = I18N_PREFIX + "thread.type.ticket.internal";
    public static final String I18N_THREAD_TYPE_TICKET_EXTERNAL = I18N_PREFIX + "thread.type.ticket.external";
    public static final String I18N_THREAD_TYPE_KBASE = I18N_PREFIX + "thread.type.kbase";
    public static final String I18N_THREAD_TYPE_KBDOC = I18N_PREFIX + "thread.type.kbdoc";
    public static final String I18N_THREAD_TYPE_LLM = I18N_PREFIX + "thread.type.llm";
    public static final String I18N_THREAD_TYPE_UNIFIED = I18N_PREFIX + "thread.type.unified";
    public static final String I18N_THREAD_TYPE_HISTORY = I18N_PREFIX + "thread.type.history";
    public static final String I18N_THREAD_TYPE_WORKFLOW = I18N_PREFIX + "thread.type.workflow";
    public static final String I18N_THREAD_TYPE_QUEUE = I18N_PREFIX + "thread.type.queue";
    public static final String I18N_THREAD_TYPE_CALLCENTER = I18N_PREFIX + "thread.type.callcenter";
    public static final String I18N_MATERIAL = I18N_PREFIX + "material";
    public static final String I18N_QUICK_BUTTON = I18N_PREFIX + "quick.button.management";
    public static final String I18N_WORKGROUP = I18N_PREFIX + "workgroup.management";
    public static final String I18N_ARTICLE = I18N_PREFIX + "article.management";
    public static final String I18N_MESSAGE_TEMPLATE = I18N_PREFIX + "message.template.management";
    public static final String I18N_QUEUE = I18N_PREFIX + "queue.management";
    public static final String I18N_ROOM = I18N_PREFIX + "room.management";
    public static final String I18N_FEEDBACK = I18N_PREFIX + "feedback.management";
    public static final String I18N_FEEDBACK_SETTINGS = I18N_PREFIX + "feedback.settings.management";
    public static final String I18N_QUICK_REPLY = I18N_PREFIX + "quick.reply.management";
    public static final String I18N_AUTO_REPLY_FIXED = I18N_PREFIX + "auto.reply.fixed.management";
    public static final String I18N_AUTO_REPLY_KEYWORD = I18N_PREFIX + "auto.reply.keyword.management";
    public static final String I18N_ROUTING_POOL = I18N_PREFIX + "routing.pool.management";
    public static final String I18N_WORKGROUP_ROUTING = I18N_PREFIX + "workgroup.routing.management";
    public static final String I18N_QUEUE_MEMBER = I18N_PREFIX + "queue.member.management";
    public static final String I18N_CALL_STATISTIC = I18N_PREFIX + "call.statistic";
    public static final String I18N_CALL_STATISTIC_AGENT = I18N_PREFIX + "call.statistic.agent";
    public static final String I18N_CALL_SETTINGS = I18N_PREFIX + "call.settings.management";
    public static final String I18N_CALL_IP_BLACKLIST = I18N_PREFIX + "call.ip.blacklist.management";
    public static final String I18N_FORM = I18N_PREFIX + "form.management";
    public static final String I18N_HOLIDAY = I18N_PREFIX + "holiday.management";
    public static final String I18N_BLOG = I18N_PREFIX + "blog.management";
    public static final String I18N_COURSE = I18N_PREFIX + "course.management";
    public static final String I18N_NOTE = I18N_PREFIX + "note.management";
    public static final String I18N_CHANNEL = I18N_PREFIX + "channel.management";
    public static final String I18N_KBASE = I18N_PREFIX + "kbase.management";
    public static final String I18N_KBASE_STATISTIC = I18N_PREFIX + "kbase.statistic";
    public static final String I18N_AGENT = I18N_PREFIX + "agent.management";
    public static final String I18N_AGENT_SEAT = I18N_PREFIX + "agent.seat";
    public static final String I18N_VISITOR = I18N_PREFIX + "visitor.management";
    public static final String I18N_TICKET = I18N_PREFIX + "ticket.management";
    public static final String I18N_TICKET_RATING = I18N_PREFIX + "ticket.rating";
    public static final String I18N_BUG = I18N_PREFIX + "bug.management";
    public static final String I18N_MESSAGE_LEAVE = I18N_PREFIX + "message.leave.management";
    public static final String I18N_TICKET_SETTINGS = I18N_PREFIX + "ticket.settings.management";
    public static final String I18N_FORM_RESULT = I18N_PREFIX + "form.result.management";
    public static final String I18N_AGENT_STATUS = I18N_PREFIX + "agent.status.management";
    public static final String I18N_AGENT_STATUS_SETTING = I18N_PREFIX + "agent.status.setting.management";
    public static final String I18N_VISITOR_CUSTOM_FIELD_SETTINGS = I18N_PREFIX + "visitor.custom.field.settings.management";
    public static final String I18N_CUSTOMER_CUSTOM_FIELD_SETTINGS = I18N_PREFIX + "customer.custom.field.settings.management";
    public static final String I18N_TABOO = I18N_PREFIX + "taboo.management";
    public static final String I18N_WEBSITE = I18N_PREFIX + "website.management";
    public static final String I18N_FILE = I18N_PREFIX + "file.management";
    public static final String I18N_TEXT = I18N_PREFIX + "text.management";
    public static final String I18N_FAQ = I18N_PREFIX + "faq.management";
    public static final String I18N_WEBPAGE = I18N_PREFIX + "webpage.management";
    public static final String I18N_CHUNK = I18N_PREFIX + "chunk.management";
    public static final String I18N_ROBOT = I18N_PREFIX + "robot.management";
    public static final String I18N_ROBOT_WORKSPACE = I18N_PREFIX + "robot.workspace";
    public static final String I18N_SIGN = I18N_PREFIX + "sign.management";
    public static final String I18N_COMMENT = I18N_PREFIX + "comment.management";
    public static final String I18N_TRIGGER = I18N_PREFIX + "trigger.management";
    public static final String I18N_LEAD = I18N_PREFIX + "lead.management";
    public static final String I18N_PRODUCT = I18N_PREFIX + "product.management";
    public static final String I18N_LEAD_FOLLOW = I18N_PREFIX + "lead.follow.management";
    public static final String I18N_TENDER = I18N_PREFIX + "tender.management";
    public static final String I18N_CUSTOMER_GROUP = I18N_PREFIX + "customer.group.management";
    public static final String I18N_CONTRACT = I18N_PREFIX + "contract.management";
    public static final String I18N_OPPORTUNITY = I18N_PREFIX + "opportunity.management";
    public static final String I18N_ESL_EVENT = I18N_PREFIX + "esl.event.management";
    public static final String I18N_CUSTOMER_COMPANY = I18N_PREFIX + "customer.company.management";
    public static final String I18N_VISITOR_TOKEN = I18N_PREFIX + "visitor.token.management";
    public static final String I18N_AUTH = I18N_PREFIX + "auth";
    public static final String I18N_ACTION_QUERY_ORG = I18N_PREFIX + "action.query.org";
    public static final String I18N_ACTION_QUERY_USER = I18N_PREFIX + "action.query.user";
    public static final String I18N_ACTION_QUERY_DETAIL = I18N_PREFIX + "action.query.detail";
    public static final String I18N_ACTION_QUERY_USER_UID = I18N_PREFIX + "action.query.user.uid";
    public static final String I18N_ACTION_CREATE = I18N_PREFIX + "action.create";
    public static final String I18N_ACTION_UPDATE = I18N_PREFIX + "action.update";
    public static final String I18N_ACTION_DELETE = I18N_PREFIX + "action.delete";
    public static final String I18N_ACTION_EXPORT = I18N_PREFIX + "action.export";
    public static final String I18N_ACTION_REFRESH_TOKEN = I18N_PREFIX + "action.refresh.token";
    public static final String I18N_ACTION_CHECK_SERVICE_REACHABLE = I18N_PREFIX + "action.check.service.reachable";
    public static final String I18N_ACTION_START_BOT = I18N_PREFIX + "action.start.bot";
    public static final String I18N_ACTION_STOP_BOT = I18N_PREFIX + "action.stop.bot";
    public static final String I18N_ACTION_ACTIVATE = I18N_PREFIX + "action.activate";
    public static final String I18N_ACTION_AUTHORITIES_RESET_LEVEL = I18N_PREFIX + "action.authorities.reset.level";
    public static final String I18N_ACTION_AUTHORITIES_ADD = I18N_PREFIX + "action.authorities.add";
    public static final String I18N_ACTION_AUTHORITIES_REMOVE = I18N_PREFIX + "action.authorities.remove";
    public static final String I18N_ACTION_RESET = I18N_PREFIX + "action.reset";
    public static final String I18N_ACTION_INIT = I18N_PREFIX + "action.init";
    public static final String I18N_ACTION_EXECUTE = I18N_PREFIX + "action.execute";
    public static final String I18N_ACTION_LOGIN_USERNAME = I18N_PREFIX + "action.login.username";
    public static final String I18N_ACTION_LOGIN_MOBILE = I18N_PREFIX + "action.login.mobile";
    public static final String I18N_ACTION_LOGIN_EMAIL = I18N_PREFIX + "action.login.email";
    public static final String I18N_ACTION_LOGIN_SCAN = I18N_PREFIX + "action.login.scan";
    public static final String I18N_ACTION_LOGOUT = I18N_PREFIX + "action.logout";
    public static final String I18N_ACTION_SWITCH_ORGANIZATION = I18N_PREFIX + "action.switch.organization";
    public static final String I18N_ACTION_CHANGE_PASSWORD = I18N_PREFIX + "action.change.password";
    public static final String I18N_ACTION_CHANGE_EMAIL = I18N_PREFIX + "action.change.email";
    public static final String I18N_ACTION_CHANGE_MOBILE = I18N_PREFIX + "action.change.mobile";
    public static final String I18N_ACTION_GENERATE_TOKEN = I18N_PREFIX + "action.generate.token";
    public static final String I18N_ACTION_LOGIN_ACCESS_TOKEN = I18N_PREFIX + "action.login.access.token";
    public static final String I18N_ACTION_KB_INDEX = I18N_PREFIX + "action.kb.index";
    public static final String I18N_ACTION_REVOKE_TOKEN = I18N_PREFIX + "action.revoke.token";
    public static final String I18N_ACTION_SUBMIT_APPLY = I18N_PREFIX + "action.submit.apply";
    public static final String I18N_ACTION_APPROVE = I18N_PREFIX + "action.approve";
    public static final String I18N_ACTION_REJECT = I18N_PREFIX + "action.reject";
    public static final String I18N_ACTION_APPROVE_APPEAL = I18N_PREFIX + "action.approve.appeal";
    public static final String I18N_ACTION_REJECT_APPEAL = I18N_PREFIX + "action.reject.appeal";
    public static final String I18N_ACTION_QUERY_DATE_RANGE_STATISTIC = I18N_PREFIX + "action.query.date.range.statistic";
    public static final String I18N_ACTION_CALCULATE_TODAY_STATISTIC = I18N_PREFIX + "action.calculate.today.statistic";
    public static final String I18N_ACTION_QUERY_BY_HOUR = I18N_PREFIX + "action.query.by.hour";
    public static final String I18N_ACTION_QUERY_BY_HOUR_AND_PROVIDER = I18N_PREFIX + "action.query.by.hour.and.provider";
    public static final String I18N_ACTION_QUERY_BY_HOUR_AND_MODEL = I18N_PREFIX + "action.query.by.hour.and.model";
    public static final String I18N_ACTION_INTENTION_RECOGNITION = I18N_PREFIX + "action.intention.recognition";
    public static final String I18N_ACTION_EMOTION_ANALYSIS = I18N_PREFIX + "action.emotion.analysis";
    public static final String I18N_ACTION_THREAD_SUMMARY = I18N_PREFIX + "action.thread.summary";
    public static final String I18N_ACTION_INTELLIGENT_QUALITY_INSPECTION = I18N_PREFIX + "action.intelligent.quality.inspection";
    public static final String I18N_ACTION_UPDATE_KID = I18N_PREFIX + "action.update.kid";
    public static final String I18N_ACTION_UPDATE_ACCESS_TOKEN = I18N_PREFIX + "action.update.access.token";
    public static final String I18N_ACTION_QUERY_PLATFORM_EMAIL_SETTINGS = I18N_PREFIX + "action.query.platform.email.settings";
    public static final String I18N_ACTION_SAVE_PLATFORM_EMAIL_SETTINGS = I18N_PREFIX + "action.save.platform.email.settings";
    public static final String I18N_ACTION_QUERY_PLATFORM_SMS_SETTINGS = I18N_PREFIX + "action.query.platform.sms.settings";
    public static final String I18N_ACTION_SAVE_PLATFORM_SMS_SETTINGS = I18N_PREFIX + "action.save.platform.sms.settings";
    public static final String I18N_ACTION_QUERY_PLATFORM_TICKET_CENTER_SETTINGS = I18N_PREFIX + "action.query.platform.ticket.center.settings";
    public static final String I18N_ACTION_SAVE_PLATFORM_TICKET_CENTER_SETTINGS = I18N_PREFIX + "action.save.platform.ticket.center.settings";
    public static final String I18N_ACTION_TEST_PLATFORM_EMAIL_SETTINGS = I18N_PREFIX + "action.test.platform.email.settings";
    public static final String I18N_ACTION_TEST_PLATFORM_SMS_SETTINGS = I18N_PREFIX + "action.test.platform.sms.settings";
    public static final String I18N_ACTION_SEND_EMAIL = I18N_PREFIX + "action.send.email";
    public static final String I18N_ACTION_SEND_TEST_EMAIL = I18N_PREFIX + "action.send.test.email";
    public static final String I18N_ACTION_TEST_SMTP_CONNECTION = I18N_PREFIX + "action.test.smtp.connection";
    public static final String I18N_ACTION_IMPORT = I18N_PREFIX + "action.import";
    public static final String I18N_ACTION_SYNC = I18N_PREFIX + "action.sync";
    public static final String I18N_ACTION_SCORE = I18N_PREFIX + "action.score";
    public static final String I18N_ACTION_OPTIMIZE = I18N_PREFIX + "action.optimize";
    public static final String I18N_ACTION_RESET_ALL = I18N_PREFIX + "action.reset.all";
    public static final String I18N_ACTION_RESET_ONE = I18N_PREFIX + "action.reset.one";
    public static final String I18N_ACTION_TOGGLE = I18N_PREFIX + "action.toggle";
    public static final String I18N_ACTION_SWITCH_CURRENT_MANAGED_DOMAIN = I18N_PREFIX + "action.switch.current.managed.domain";
    public static final String I18N_ACTION_QUERY_CURRENT_MANAGED_DOMAIN = I18N_PREFIX + "action.query.current.managed.domain";
    public static final String I18N_ACTION_LIKE = I18N_PREFIX + "action.like";
    public static final String I18N_ACTION_UNLIKE = I18N_PREFIX + "action.unlike";
    public static final String I18N_ACTION_FAVORITE = I18N_PREFIX + "action.favorite";
    public static final String I18N_ACTION_UNFAVORITE = I18N_PREFIX + "action.unfavorite";
    public static final String I18N_ACTION_INVITE = I18N_PREFIX + "action.invite";
    public static final String I18N_ACTION_JOIN = I18N_PREFIX + "action.join";
    public static final String I18N_ACTION_REMOVE = I18N_PREFIX + "action.remove";
    public static final String I18N_ACTION_LEAVE = I18N_PREFIX + "action.leave";
    public static final String I18N_ACTION_DISMISS = I18N_PREFIX + "action.dismiss";
    public static final String I18N_ACTION_QUERY_TASK = I18N_PREFIX + "action.query.task";
    public static final String I18N_ACTION_DELETE_ALL = I18N_PREFIX + "action.delete.all";
    public static final String I18N_ACTION_QUERY_INVITE_THREAD = I18N_PREFIX + "action.query.invite.thread";
    public static final String I18N_ACTION_QUERY_BY_TOPIC = I18N_PREFIX + "action.query.by.topic";
    public static final String I18N_ACTION_QUERY_BY_TOPIC_USER = I18N_PREFIX + "action.query.by.topic.user";
    public static final String I18N_ACTION_UPDATE_TOP = I18N_PREFIX + "action.update.top";
    public static final String I18N_ACTION_UPDATE_STAR = I18N_PREFIX + "action.update.star";
    public static final String I18N_ACTION_UPDATE_MUTE = I18N_PREFIX + "action.update.mute";
    public static final String I18N_ACTION_UPDATE_HIDE = I18N_PREFIX + "action.update.hide";
    public static final String I18N_ACTION_UPDATE_FOLD = I18N_PREFIX + "action.update.fold";
    public static final String I18N_ACTION_UPDATE_USER = I18N_PREFIX + "action.update.user";
    public static final String I18N_ACTION_UPDATE_TAG_LIST = I18N_PREFIX + "action.update.tag.list";
    public static final String I18N_ACTION_UPDATE_UNREAD = I18N_PREFIX + "action.update.unread";
    public static final String I18N_ACTION_UPDATE_STATUS = I18N_PREFIX + "action.update.status";
    public static final String I18N_ACTION_QUERY_USER_SERVICE_THREAD = I18N_PREFIX + "action.query.user.service.thread";
    public static final String I18N_ACTION_UPDATE_NOTE = I18N_PREFIX + "action.update.note";
    public static final String I18N_ACTION_ADMIN_UPDATE = I18N_PREFIX + "action.admin.update";
    public static final String I18N_ACTION_CLOSE = I18N_PREFIX + "action.close";
    public static final String I18N_ACTION_CLOSE_BY_TOPIC = I18N_PREFIX + "action.close.by.topic";
    public static final String I18N_ACTION_REQUEST_MESSAGE_METADATA = I18N_PREFIX + "action.request.message.metadata";
    public static final String I18N_ACTION_ENABLE = I18N_PREFIX + "action.enable";
    public static final String I18N_ACTION_UPDATE_AVATAR = I18N_PREFIX + "action.update.avatar";
    public static final String I18N_ACTION_QUERY_ADMIN_WORKGROUP = I18N_PREFIX + "action.query.admin.workgroup";
    public static final String I18N_ACTION_QUERY_ADMIN_ONGOING_THREAD = I18N_PREFIX + "action.query.admin.ongoing.thread";
    public static final String I18N_ACTION_BATCH_UPDATE_ADMIN_WORKGROUP = I18N_PREFIX + "action.batch.update.admin.workgroup";
    public static final String I18N_ACTION_UPDATE_INDEX = I18N_PREFIX + "action.update.index";
    public static final String I18N_ACTION_UPDATE_VECTOR_INDEX = I18N_PREFIX + "action.update.vector.index";
    public static final String I18N_ACTION_UPDATE_ALL_INDEX = I18N_PREFIX + "action.update.all.index";
    public static final String I18N_ACTION_UPDATE_ALL_VECTOR_INDEX = I18N_PREFIX + "action.update.all.vector.index";
    public static final String I18N_ACTION_SEARCH = I18N_PREFIX + "action.search";
    public static final String I18N_ACTION_QUERY_QUEUING_THREAD = I18N_PREFIX + "action.query.queuing.thread";
    public static final String I18N_ACTION_QUERY_UNREPLIED_THREAD = I18N_PREFIX + "action.query.unreplied.thread";
    public static final String I18N_ACTION_GET_AGENT_QUEUING_COUNT = I18N_PREFIX + "action.get.agent.queuing.count";
    public static final String I18N_ACTION_GET_AGENT_QUEUE_STATS = I18N_PREFIX + "action.get.agent.queue.stats";
    public static final String I18N_ACTION_PUBLISH = I18N_PREFIX + "action.publish";
    public static final String I18N_ACTION_ACCEPT = I18N_PREFIX + "action.accept";
    public static final String I18N_ACTION_QUERY_STATE = I18N_PREFIX + "action.query.state";
    public static final String I18N_ACTION_REFRESH_STATE = I18N_PREFIX + "action.refresh.state";
    public static final String I18N_ACTION_QUERY_VISITOR_UID = I18N_PREFIX + "action.query.visitor.uid";
    public static final String I18N_ACTION_UPDATE_AGENT_STATUS = I18N_PREFIX + "action.update.agent.status";
    public static final String I18N_ACTION_UPDATE_AUTO_REPLY = I18N_PREFIX + "action.update.auto.reply";
    public static final String I18N_ACTION_SEND_AGENT_SSE_MESSAGE = I18N_PREFIX + "action.send.agent.sse.message";
    public static final String I18N_ACTION_QUERY_BY_THREAD_UID = I18N_PREFIX + "action.query.by.thread.uid";
    public static final String I18N_ACTION_QUERY_BY_VISITOR_THREAD_UID = I18N_PREFIX + "action.query.by.visitor.thread.uid";
    public static final String I18N_ACTION_QUERY_BY_VISITOR_THREAD_TOPIC = I18N_PREFIX + "action.query.by.visitor.thread.topic";
    public static final String I18N_ACTION_COUNT_STATUS = I18N_PREFIX + "action.count.status";
    public static final String I18N_ACTION_QUERY_RELATED_THREAD = I18N_PREFIX + "action.query.related.thread";
    public static final String I18N_ACTION_COUNT_PENDING = I18N_PREFIX + "action.count.pending";
    public static final String I18N_ACTION_REPLY = I18N_PREFIX + "action.reply";
    public static final String I18N_ACTION_UPDATE_MESSAGE_LEAVE_STATUS = I18N_PREFIX + "action.update.message.leave.status";
    public static final String I18N_ACTION_MARK_AS_READ = I18N_PREFIX + "action.mark.as.read";
    public static final String I18N_ACTION_TRANSFER = I18N_PREFIX + "action.transfer";
    public static final String I18N_ACTION_MARK_AS_SPAM = I18N_PREFIX + "action.mark.as.spam";
    public static final String I18N_ACTION_QUERY_BY_WORKGROUP = I18N_PREFIX + "action.query.by.workgroup";
    public static final String I18N_ACTION_SAVE_BY_WORKGROUP = I18N_PREFIX + "action.save.by.workgroup";
    public static final String I18N_ACTION_PUBLISH_BY_WORKGROUP = I18N_PREFIX + "action.publish.by.workgroup";
    public static final String I18N_ACTION_BATCH_BIND_WORKGROUP = I18N_PREFIX + "action.batch.bind.workgroup";
    public static final String I18N_ACTION_QUERY_BIND_WORKGROUP = I18N_PREFIX + "action.query.bind.workgroup";
    public static final String I18N_ACTION_QUERY_CATEGORIES_BY_WORKGROUP = I18N_PREFIX + "action.query.categories.by.workgroup";
    public static final String I18N_ACTION_DELETE_INDEX = I18N_PREFIX + "action.delete.index";
    public static final String I18N_ACTION_SYNC_INDEX_STATUS = I18N_PREFIX + "action.sync.index.status";
    public static final String I18N_ACTION_BATCH_SYNC_INDEX_STATUS = I18N_PREFIX + "action.batch.sync.index.status";
    public static final String I18N_ACTION_DELETE_INDEX_BY_KB = I18N_PREFIX + "action.delete.index.by.kb";
    public static final String I18N_ACTION_DELETE_VECTOR_INDEX = I18N_PREFIX + "action.delete.vector.index";
    public static final String I18N_ACTION_SYNC_VECTOR_STATUS = I18N_PREFIX + "action.sync.vector.status";
    public static final String I18N_ACTION_QUERY_ELASTIC_INDEX = I18N_PREFIX + "action.query.elastic.index";
    public static final String I18N_ACTION_QUERY_VECTOR_INDEX = I18N_PREFIX + "action.query.vector.index";
    public static final String I18N_ACTION_BATCH_SYNC_VECTOR_STATUS = I18N_PREFIX + "action.batch.sync.vector.status";
    public static final String I18N_ACTION_DELETE_VECTOR_INDEX_BY_KB = I18N_PREFIX + "action.delete.vector.index.by.kb";
    public static final String I18N_ACTION_CREATE_THREAD = I18N_PREFIX + "action.create.thread";
    public static final String I18N_ACTION_UPDATE_THREAD = I18N_PREFIX + "action.update.thread";
    public static final String I18N_THREAD_CLOSE_SUCCESS = I18N_PREFIX + "thread.close.success";
    public static final String I18N_THREAD_CLOSE_ALREADY_CLOSED = I18N_PREFIX + "thread.close.already.closed";
    public static final String I18N_ACTION_CREATE_PROMPT = I18N_PREFIX + "action.create.prompt";
    public static final String I18N_ACTION_UPDATE_PROMPT = I18N_PREFIX + "action.update.prompt";
    public static final String I18N_ACTION_UPDATE_PROMPT_TEXT = I18N_PREFIX + "action.update.prompt.text";
    public static final String I18N_ACTION_UPDATE_KB_UID = I18N_PREFIX + "action.update.kb.uid";
    public static final String I18N_EMBEDDING_SETTINGS = I18N_PREFIX + "embedding.settings";

    // 角色描述（用于 RoleInitializer 默认角色 description，前端通过 translateString 翻译）
    public static final String I18N_ROLE_SUPER_DESCRIPTION = I18N_DESCRIPTION_PREFIX + "role.super";
    public static final String I18N_ROLE_ADMIN_DESCRIPTION = I18N_DESCRIPTION_PREFIX + "role.admin";
    public static final String I18N_ROLE_AGENT_DESCRIPTION = I18N_DESCRIPTION_PREFIX + "role.agent";
    public static final String I18N_ROLE_USER_DESCRIPTION = I18N_DESCRIPTION_PREFIX + "role.user";
    // 系统通知
    public static final String I18N_SYSTEM_NOTIFICATION_NAME = I18N_PREFIX + "system.notification"; // 系统通知
    public static final String I18N_REENTER_TIP = I18N_PREFIX + "reenter.tip"; // 重新进入提示
    public static final String I18N_LOGIN_REQUIRED = I18N_PREFIX + "login.required"; // 请先登录
    public static final String I18N_FORCE_LOGOUT_REASON = I18N_PREFIX + "force.logout.reason"; // 账号已被管理员强制下线，请联系管理员恢复后再登录。
    public static final String I18N_SUPER_ADMIN_REQUIRED = I18N_PREFIX + "super.admin.required"; // 仅超级管理员可操作

    // 账号相关
    public static final String I18N_USERNAME_OR_PASSWORD_INCORRECT = I18N_PREFIX + "username.or.password.incorrect"; // 用户名或密码不正确
    public static final String I18N_MOBILE_ALREADY_EXISTS = I18N_PREFIX + "mobile.already.exists"; // 手机号已存在
    public static final String I18N_EMAIL_ALREADY_EXISTS = I18N_PREFIX + "email.already.exists"; // 邮箱已存在
    public static final String I18N_MEMBER_RESTRICTED_ROLE_UPDATE_NOT_SUPPORTED = I18N_PREFIX + "member.restricted.role.update.not.supported"; // 不支持在成员入口修改管理员/超管角色
    public static final String I18N_PERMISSION_CREATE_DENIED = I18N_PREFIX + "permission.create.denied"; // 无权限创建当前层级数据
    public static final String I18N_PERMISSION_UPDATE_DENIED = I18N_PREFIX + "permission.update.denied"; // 无权限更新当前数据
    public static final String I18N_PERMISSION_DELETE_DENIED = I18N_PREFIX + "permission.delete.denied"; // 无权限删除当前数据
    public static final String I18N_COMMENT_UPDATE_DENIED = I18N_PREFIX + "comment.update.denied"; // 无权限更新该评论
    public static final String I18N_COMMENT_DELETE_DENIED = I18N_PREFIX + "comment.delete.denied"; // 无权限删除该评论
    public static final String I18N_DEPARTMENT_PARENT_SELF_NOT_ALLOWED = I18N_PREFIX + "department.parent.self.not.allowed"; // 不能将当前部门设置为父部门
    public static final String I18N_WORKFLOW_CONTENT_EMPTY = I18N_PREFIX + "workflow.content.empty"; // 工作流内容为空
    public static final String I18N_WORKFLOW_START_NODE_NOT_FOUND = I18N_PREFIX + "workflow.start.node.not.found"; // 工作流中未找到开始节点
    public static final String I18N_ACCESS_DENIED = I18N_PREFIX + "access.denied"; // 无权限访问
    public static final String I18N_VISITOR_MESSAGE_RECEIVE_DENIED = I18N_PREFIX + "visitor.message.receive.denied"; // 当前对方无法接收您的消息！
    public static final String I18N_ORG_UID_REQUIRED = I18N_PREFIX + "org.uid.required"; // 非超级管理员必须指定组织
    public static final String I18N_ORGANIZATION_ACCESS_DENIED = I18N_PREFIX + "organization.access.denied"; // 无权访问其他组织数据
    public static final String I18N_ORGANIZATION_NOT_FOUND = I18N_PREFIX + "organization.not.found"; // 组织未找到
    public static final String I18N_ORGANIZATION_NOT_FOUND_WITH_UID = I18N_PREFIX + "organization.not.found.with.uid"; // 指定 uid 的组织未找到
    public static final String I18N_ORGANIZATION_NAME_EXISTS = I18N_PREFIX + "organization.name.exists"; // 组织名称已存在
    public static final String I18N_ORGANIZATION_CODE_EXISTS = I18N_PREFIX + "organization.code.exists"; // 组织代码已存在
    public static final String I18N_ORGANIZATION_CREATE_CONSTRAINT_FAILED = I18N_PREFIX + "organization.create.constraint.failed"; // 创建组织失败，名称或代码冲突
    public static final String I18N_ORGANIZATION_SUPER_USER_DISABLE_DENIED = I18N_PREFIX + "organization.super.user.disable.denied"; // 超级用户组织不允许禁用
    public static final String I18N_EMAIL_REQUIRED = I18N_PREFIX + "email.required"; // 邮箱不能为空
    public static final String I18N_MOBILE_REQUIRED = I18N_PREFIX + "mobile.required"; // 手机号不能为空
    public static final String I18N_EMAIL_OR_MOBILE_REQUIRED = I18N_PREFIX + "email.or.mobile.required"; // 邮箱或手机号不能为空
    public static final String I18N_EMAIL_RESERVED_BY_SUPER_ADMIN = I18N_PREFIX + "email.reserved.by.super.admin"; // 邮箱为系统超级管理员保留，禁止使用
    public static final String I18N_MOBILE_RESERVED_BY_SUPER_ADMIN = I18N_PREFIX + "mobile.reserved.by.super.admin"; // 手机号为系统超级管理员保留，禁止使用
    public static final String I18N_PASSWORD_DECRYPT_FAILED = I18N_PREFIX + "password.decrypt.failed"; // 密码解密失败
    public static final String I18N_PASSWORD_DECRYPT_KEY_INVALID = I18N_PREFIX + "password.decrypt.key.invalid"; // 密钥错误导致密码解密失败
    public static final String I18N_MOBILE_NOT_EXISTS = I18N_PREFIX + "mobile.not.exists"; // 手机号不存在
    public static final String I18N_EMAIL_NOT_EXISTS = I18N_PREFIX + "email.not.exists"; // 邮箱不存在
    public static final String I18N_MOBILE_FORMAT_ERROR = I18N_PREFIX + "mobile.format.error"; // 手机号格式错误
    public static final String I18N_EMAIL_FORMAT_ERROR = I18N_PREFIX + "email.format.error"; // 邮箱格式错误
    // 验证码相关
    public static final String I18N_AUTH_CAPTCHA_SEND_SUCCESS = I18N_PREFIX + "auth.captcha.send.success"; // 验证码发送成功
    public static final String I18N_AUTH_CAPTCHA_ERROR = I18N_PREFIX + "auth.captcha.error"; // 验证码错误
    public static final String I18N_AUTH_CAPTCHA_EXPIRED = I18N_PREFIX + "auth.captcha.expired"; // 验证码已过期
    public static final String I18N_AUTH_CAPTCHA_ALREADY_SEND = I18N_PREFIX + "auth.captcha.already.send"; // 验证码已发送
    public static final String I18N_AUTH_CAPTCHA_SEND_TOO_FREQUENT = I18N_PREFIX + "auth.captcha.send.too.frequent"; // 验证码发送过于频繁
    public static final String I18N_AUTH_CAPTCHA_VALIDATE_FAILED = I18N_PREFIX + "auth.captcha.validate.failed"; // 验证码验证失败
    // 助手相关
    public static final String I18N_FILE_ASSISTANT_NAME = I18N_PREFIX + "file.assistant.name"; // 文件助手
    public static final String I18N_QUEUE_ASSISTANT_NAME = I18N_PREFIX + "queue.assistant.name"; // 排队助手
    public static final String I18N_CLIPBOARD_ASSISTANT_NAME = I18N_PREFIX + "clipboard.assistant.name"; // 剪贴板助手
    public static final String I18N_INTENT_REWRITE_ASSISTANT_NAME = I18N_PREFIX + "intent.rewrite.assistant.name"; // 意图改写
    public static final String I18N_INTENT_CLASSIFICATION_ASSISTANT_NAME = I18N_PREFIX + "intent.classification.assistant.name"; // 意图识别
    public static final String I18N_EMOTION_ASSISTANT_NAME = I18N_PREFIX + "emotion.assistant.name"; // 情绪分析
    public static final String I18N_FILE_ASSISTANT_DESCRIPTION = I18N_PREFIX + "file.assistant.description"; // 手机、电脑文件互传
    public static final String I18N_QUEUE_ASSISTANT_DESCRIPTION = I18N_PREFIX + "queue.assistant.description"; // 排队助手描述
    public static final String I18N_CLIPBOARD_ASSISTANT_DESCRIPTION = I18N_PREFIX + "clipboard.assistant.description"; // 手机、电脑剪贴板内容互传
    public static final String I18N_INTENT_REWRITE_ASSISTANT_DESCRIPTION = I18N_PREFIX + "intent.rewrite.assistant.description"; // 用于改写客户意图
    public static final String I18N_INTENT_CLASSIFICATION_ASSISTANT_DESCRIPTION = I18N_PREFIX + "intent.classification.assistant.description"; // 用于识别客户意图
    public static final String I18N_EMOTION_ASSISTANT_DESCRIPTION = I18N_PREFIX + "emotion.assistant.description"; // 用于分析客户情绪
    // 系统通知
    // public static final String I18N_SYSTEM_NOTIFICATION_NAME = "系统通知"; // 系统通知
    public static final String I18N_SYSTEM_NOTIFICATION_DESCRIPTION = I18N_PREFIX + "system.notification.description"; // 系统通知消息
    // 用户相关
    public static final String I18N_USER_OLD_PASSWORD_WRONG = I18N_PREFIX + "old.password.wrong"; // 旧密码错误
    // 内容类型
    public static final String I18N_THREAD_CONTENT_IMAGE = I18N_PREFIX + "thread.content.image"; // 图片
    public static final String I18N_THREAD_CONTENT_FILE = I18N_PREFIX + "thread.content.file"; // 文件
    public static final String I18N_THREAD_CONTENT_AUDIO = I18N_PREFIX + "thread.content.audio"; // 语音
    public static final String I18N_THREAD_CONTENT_VIDEO = I18N_PREFIX + "thread.content.video"; // 视频
    // 提示信息
    public static final String I18N_WELCOME_TIP = I18N_PREFIX + "welcome.tip"; // 欢迎提示
    public static final String I18N_TOP_TIP = I18N_PREFIX + "top.tip"; // 置顶提示
    public static final String I18N_MESSAGE_LEAVE_TIP = I18N_PREFIX + "message.leave.tip"; // 留言提示
    // public static final String I18N_REENTER_TIP = "继续会话"; // 重新进入提示
    public static final String I18N_QUEUE_TIP = I18N_PREFIX + "queue.tip"; // 排队提示（旧版，保持兼容）
    public static final String I18N_QUEUE_MESSAGE_TEMPLATE = I18N_PREFIX + "queue.message.template"; // 您前面还有{0}人排队（旧版）
    // 排队提示语模板（新版，支持模板变量）
    // 支持变量: {position}-排队位置, {queueSize}-队列总人数, {waitSeconds}-等待秒数, {waitMinutes}-等待分钟数, {waitTime}-格式化等待时间
    public static final String I18N_QUEUE_TIP_TEMPLATE = I18N_PREFIX + "queue.tip.template"; // 排队提示语模板
    // 即将接入提示语（当排队位置为0时使用）
    public static final String I18N_QUEUE_READY_TIP = I18N_PREFIX + "queue.ready.tip"; // 即将接入提示语
    public static final String I18N_AUTO_CLOSE_TIP = I18N_PREFIX + "auto.close.tip"; // 会话已自动关闭
    public static final String I18N_AGENT_CLOSE_TIP = I18N_PREFIX + "agent.close.tip"; // 客服已关闭会话
    public static final String I18N_AGENT_TRANSFER_TIP = I18N_PREFIX + "agent.transfer.tip"; // 客服已将会话转接
    public static final String I18N_AGENT_TIMEOUT_TIP = I18N_PREFIX + "agent.timeout.tip"; // 超时未回复
    // 评价提示
    public static final String I18N_INVITE_RATE_TIP = I18N_PREFIX + "invite.rate.tip"; // 邀请评价提示
    public static final String I18N_RATE_SUBMITTED_NOTICE = I18N_PREFIX + "rate.submitted.notice"; // 访客已提交评价

    // VOC 评价设置默认文案（FeedbackSettingsEntity 默认值）
    public static final String I18N_FEEDBACK_SETTINGS_TITLE_DEFAULT = I18N_PREFIX + "feedback.settings.title.default";
    public static final String I18N_FEEDBACK_SETTINGS_POSITIVE_QUESTION_DEFAULT = I18N_PREFIX + "feedback.settings.positive.question.default";
    public static final String I18N_FEEDBACK_SETTINGS_NEGATIVE_QUESTION_DEFAULT = I18N_PREFIX + "feedback.settings.negative.question.default";
    public static final String I18N_FEEDBACK_SETTINGS_COMMENT_PLACEHOLDER_DEFAULT = I18N_PREFIX + "feedback.settings.comment.placeholder.default";
    // 统一
    public static final String I18N_UNIFIED_NICKNAME = I18N_PREFIX + "unified.nickname"; // 统一入口
    public static final String I18N_UNIFIED_DESCRIPTION = I18N_PREFIX + "unified.description"; // 统一描述
    // 工作组
    public static final String I18N_WORKGROUP_NICKNAME = I18N_PREFIX + "workgroup.nickname"; // 工作组昵称
    public static final String I18N_WORKGROUP_BOOKING_NICKNAME = I18N_PREFIX + "workgroup.booking.nickname"; // booking 工作组昵称
    public static final String I18N_WORKGROUP_BEFORE_NICKNAME = I18N_PREFIX + "workgroup.before.nickname"; // 工作组前缀昵称
    public static final String I18N_WORKGROUP_AFTER_NICKNAME = I18N_PREFIX + "workgroup.after.nickname"; // 工作组后缀昵称
    public static final String I18N_WORKGROUP_TICKET_NICKNAME = I18N_PREFIX + "workgroup.ticket.nickname"; // 工单工作组昵称
    public static final String I18N_WORKGROUP_DESCRIPTION = I18N_PREFIX + "workgroup.description"; // 工作组描述
    public static final String I18N_WORKGROUP_BOOKING_DESCRIPTION = I18N_PREFIX + "workgroup.booking.description"; // booking 工作组描述
    public static final String I18N_WORKGROUP_BEFORE_DESCRIPTION = I18N_PREFIX + "workgroup.before.description"; // 工作组前缀描述
    public static final String I18N_WORKGROUP_AFTER_DESCRIPTION = I18N_PREFIX + "workgroup.after.description"; // 工作组后缀描述
    public static final String I18N_WORKGROUP_TICKET_DESCRIPTION = I18N_PREFIX + "workgroup.ticket.description"; // 工单工作组描述
    // 客服相关
    public static final String I18N_AGENT_NICKNAME = I18N_PREFIX + "agent.nickname"; // 客服昵称
    public static final String I18N_AGENT_DESCRIPTION = I18N_PREFIX + "agent.description"; // 客服描述
    public static final String I18N_AGENT_EXISTS = I18N_PREFIX + "agent.exists"; // 坐席已存在
    public static final String I18N_AGENT_LIMIT_EXCEEDED = I18N_PREFIX + "agent.limit.exceeded"; // 坐席人数已达上限
    public static final String I18N_AGENT_SEAT_LIMIT_EXCEEDED = I18N_PREFIX + "agent.seat.limit.exceeded"; // 坐席席位已达上限
    public static final String I18N_AGENT_OFFLINE = I18N_PREFIX + "agent.offline"; // 客服离线
    public static final String I18N_AGENT_UNAVAILABLE = I18N_PREFIX + "agent.unavailable"; // 客服不可用
    public static final String I18N_AGENT_AVAILABLE = I18N_PREFIX + "agent.available"; // 客服可用
    // 用户相关
    public static final String I18N_USER_NICKNAME = I18N_PREFIX + "user.nickname"; // 用户昵称
    public static final String I18N_USER_DESCRIPTION = I18N_PREFIX + "user.description"; // 用户描述
    public static final String I18N_USER_NOT_FOUND = I18N_PREFIX + "user.not.found"; // 用户未找到
    public static final String I18N_CREATE_FAILED = I18N_PREFIX + "create.failed"; // 创建失败
    public static final String I18N_UPDATE_FAILED = I18N_PREFIX + "update.failed"; // 更新失败
    // 其他
    public static final String I18N_DESCRIPTION = I18N_PREFIX + "description"; // 描述
    // 机器人相关
    public static final String I18N_ROBOT_NICKNAME = I18N_PREFIX + "robot.nickname"; // 机器人昵称
    public static final String I18N_ROBOT_NAME = I18N_PREFIX + "robot.name"; // 机器人名称
    public static final String I18N_ROBOT_DESCRIPTION = I18N_PREFIX + "robot.description"; // 机器人描述
    public static final String I18N_ROBOT_AGENT_ASSISTANT_NICKNAME = I18N_PREFIX + "robot.agent.assistant.nickname"; // 客服助理机器人昵称
    public static final String I18N_ROBOT_DEFAULT_REPLY = I18N_PREFIX + "robot.noreply"; // 机器人回复
    public static final String I18N_ROBOT_TO_AGENT_TIP = I18N_PREFIX + "robot.to.agent.tip"; // 机器人转人工提示
    // 角色相关
    public static final String I18N_ADMIN = I18N_PREFIX + "admin"; // 管理员
    public static final String I18N_ADMIN_DESCRIPTION = I18N_PREFIX + "admin.description"; // 管理员描述
    public static final String I18N_MEMBER_LABEL = I18N_PREFIX + "member.label"; // 成员
    public static final String I18N_MEMBER_DESCRIPTION = I18N_PREFIX + "member.description"; // 成员描述
    // 快捷回复分类
    public static final String I18N_QUICK_REPLY_CATEGORY_CONTACT = I18N_PREFIX + "contact"; // 询问联系方式
    public static final String I18N_QUICK_REPLY_CATEGORY_THANKS = I18N_PREFIX + "thanks"; // 感谢
    public static final String I18N_QUICK_REPLY_CATEGORY_WELCOME = I18N_PREFIX + "welcome"; // 问候
    public static final String I18N_QUICK_REPLY_CATEGORY_BYE = I18N_PREFIX + "bye"; // 告别
    // 快捷回复内容
    public static final String I18N_QUICK_REPLY_CONTACT_TITLE = I18N_PREFIX + "contact.title"; // 联系方式标题
    public static final String I18N_QUICK_REPLY_CONTACT_CONTENT = I18N_PREFIX + "contact.content"; // 联系方式内容
    public static final String I18N_QUICK_REPLY_THANKS_TITLE = I18N_PREFIX + "thanks.title"; // 感谢标题
    public static final String I18N_QUICK_REPLY_THANKS_CONTENT = I18N_PREFIX + "thanks.content"; // 感谢内容
    public static final String I18N_QUICK_REPLY_WELCOME_TITLE = I18N_PREFIX + "welcome.title"; // 欢迎标题
    public static final String I18N_QUICK_REPLY_WELCOME_CONTENT = I18N_PREFIX + "welcome.content"; // 欢迎内容
    public static final String I18N_QUICK_REPLY_BYE_TITLE = I18N_PREFIX + "bye.title"; // 再见标题
    public static final String I18N_QUICK_REPLY_BYE_CONTENT = I18N_PREFIX + "bye.content"; // 再见内容
    // API相关
    public static final String I18N_VIP_REST_API = "VIP REST API"; // VIP REST API
    // 群组相关
    public static final String I18N_GROUP_NAME = I18N_PREFIX + "group.name"; // 群组名称
    public static final String I18N_GROUP_DESCRIPTION = I18N_PREFIX + "group.description"; // 群组描述
    // 工单相关
    public static final String I18N_TICKET_SETTINGS_EXTERNAL_NAME = I18N_PREFIX + "ticket.settings.external.name"; // 外部工单配置名称
    public static final String I18N_TICKET_SETTINGS_EXTERNAL_DESCRIPTION = I18N_PREFIX + "ticket.settings.external.description"; // 外部工单配置描述
    public static final String I18N_TICKET_SETTINGS_INTERNAL_NAME = I18N_PREFIX + "ticket.settings.internal.name"; // 内部工单配置名称
    public static final String I18N_TICKET_SETTINGS_INTERNAL_DESCRIPTION = I18N_PREFIX + "ticket.settings.internal.description"; // 内部工单配置描述

    // 工单提示语默认文案（TicketBasicSettingsEntity 默认值）
    public static final String I18N_TICKET_ACCESS_TIP = I18N_PREFIX + "ticket.access.tip"; // 工单接入提示语
    public static final String I18N_TICKET_CLOSE_TIP = I18N_PREFIX + "ticket.close.tip"; // 工单关闭提示语
    public static final String I18N_TICKET_AGENT_TIMEOUT_TIP = I18N_PREFIX + "ticket.agent.timeout.tip"; // 工单客服超时未回复提示语
    public static final String I18N_TICKET_VISITOR_TIMEOUT_TIP = I18N_PREFIX + "ticket.visitor.timeout.tip"; // 工单访客超时未回复提示语
    // 文件解析
    public static final String I18N_NOTICE_PARSE_FILE_SUCCESS = I18N_PREFIX + "notice.parse.file.success"; // 文件解析成功
    public static final String I18N_NOTICE_PARSE_FILE_ERROR = I18N_PREFIX + "notice.parse.file.error"; // 文件解析错误
    // 会话状态,  用于更新thread.content
    public static final String I18N_AUTO_CLOSED = I18N_PREFIX + "auto.closed"; // 已自动关闭
    public static final String I18N_AGENT_CLOSED = I18N_PREFIX + "agent.closed"; // 已被客服关闭
    public static final String I18N_AGENT_TRANSFER = I18N_PREFIX + "agent.transfer"; // 已被客服转接
    public static final String I18N_AGENT_TIMEOUT = I18N_PREFIX + "agent.timeout"; // 超时未回复
    // 黑名单
    public static final String I18N_BLACK_USER_ALREADY_EXISTS = I18N_PREFIX + "black.user.already.exists"; // 黑名单用户已存在
    // 输入状态
    public static final String I18N_TYPING = I18N_PREFIX + "typing"; // 正在输入

    // GlobalControllerAdvice 错误国际化常量
    public static final String I18N_USER_SIGNUP_FIRST = I18N_PREFIX + "user.signup.first"; // 请先注册用户
    public static final String I18N_EMAIL_SIGNUP_FIRST = I18N_PREFIX + "email.signup.first"; // 请先使用邮箱注册
    public static final String I18N_MOBILE_SIGNUP_FIRST = I18N_PREFIX + "mobile.signup.first"; // 请先使用手机号注册
    public static final String I18N_RESOURCE_NOT_FOUND = I18N_PREFIX + "resource.not.found"; // 资源未找到
    public static final String I18N_RESOURCE_NOT_FOUND_WITH_UID = I18N_PREFIX + "resource.not.found.with.uid"; // 按 uid 未找到资源
    public static final String I18N_ROLE_NOT_FOUND = I18N_PREFIX + "role.not.found"; // 角色未找到
    public static final String I18N_ROLE_NOT_FOUND_BY_ID = I18N_PREFIX + "role.not.found.by.id"; // 按 id 未找到角色
    public static final String I18N_ROLE_NOT_FOUND_BY_ID_AND_UID = I18N_PREFIX + "role.not.found.by.id.and.uid"; // 按 id 和 uid 未找到角色
    public static final String I18N_ROLE_NOT_FOUND_BY_ID_AND_NAME = I18N_PREFIX + "role.not.found.by.id.and.name"; // 按 id 和 name 未找到角色
    public static final String I18N_TOKEN_NOT_FOUND_FOR_UID = I18N_PREFIX + "token.not.found.for.uid"; // 指定 uid 的 token 未找到
    public static final String I18N_FILE_NOT_FOUND = I18N_PREFIX + "file.not.found"; // 文件未找到
    // public static final String I18N_NOT_LOGIN = "请先登录"; // 请先登录
    public static final String I18N_USER_DISABLED = I18N_PREFIX + "user.disabled"; // 用户已被禁用
    public static final String I18N_FORBIDDEN_ACCESS = I18N_PREFIX + "forbidden.access"; // 禁止访问
    public static final String I18N_USER_BLOCKED = I18N_PREFIX + "user.blocked"; // 用户已被封禁
    public static final String I18N_SENSITIVE_CONTENT = I18N_PREFIX + "sensitive.content"; // 包含敏感内容
    public static final String I18N_MESSAGE_PROCESSING_FAILED = I18N_PREFIX + "message.processing.failed"; // 消息处理失败
    public static final String I18N_NULL_POINTER_EXCEPTION = I18N_PREFIX + "null.pointer.exception"; // 空指针异常
    public static final String I18N_RESPONSE_STATUS_EXCEPTION = I18N_PREFIX + "response.status.exception"; // 响应状态异常
    public static final String I18N_WEBSOCKET_TIMEOUT_EXCEPTION = I18N_PREFIX + "websocket.timeout.exception"; // WebSocket超时异常
    public static final String I18N_HTTP_METHOD_NOT_SUPPORTED = I18N_PREFIX + "http.method.not.supported"; // 不支持的HTTP请求方法
    public static final String I18N_AUTHORIZATION_DENIED = I18N_PREFIX + "authorization.denied"; // 授权拒绝
    public static final String I18N_REQUEST_REJECTED = I18N_PREFIX + "request.rejected"; // 请求被拒绝
    public static final String I18N_ENTITY_NOT_FOUND = I18N_PREFIX + "entity.not.found"; // 实体未找到
    public static final String I18N_INVALID_REQUEST_BODY = I18N_PREFIX + "invalid.request.body"; // 请求体格式错误（JSON 不合法/不完整）
    public static final String I18N_CONNECTION_NO_LONGER_AVAILABLE = I18N_PREFIX + "connection.no.longer.available"; // 连接已不可用
    public static final String I18N_EXTERNAL_SERVICE_TEMPORARILY_UNAVAILABLE = I18N_PREFIX + "external.service.temporarily.unavailable"; // 外部服务暂时不可用
    public static final String I18N_RESOURCE_CONCURRENTLY_MODIFIED = I18N_PREFIX + "resource.concurrently.modified"; // 资源已被并发修改
    public static final String I18N_INPUT_TOO_LONG = I18N_PREFIX + "input.too.long"; // 输入内容过长
    public static final String I18N_DATA_ALREADY_EXISTS = I18N_PREFIX + "data.already.exists"; // 数据已存在
    public static final String I18N_DATA_RELATION_CONSTRAINT_VIOLATED = I18N_PREFIX + "data.relation.constraint.violated"; // 数据关联约束冲突
    public static final String I18N_DATA_SAVE_FAILED = I18N_PREFIX + "data.save.failed"; // 数据保存失败
    public static final String I18N_OPERATION_NOT_SUPPORTED = I18N_PREFIX + "operation.not.supported"; // 不支持的操作/功能暂未实现
    public static final String I18N_INTERNAL_SERVER_ERROR = I18N_PREFIX + "internal.server.error"; // 内部服务器错误

    public static final String I18N_NO_ANSWER = I18N_PREFIX + "no.answer";
    public static final String I18N_CANT_ANSWER = I18N_PREFIX + "cant.answer"; 

    public static final String I18N_SERVICE_TEMPORARILY_UNAVAILABLE = "请首先在管理后台配置大模型apiUrl和apiKey。如果已经配置，请检查大模型提供商账号是否欠费。"; // 服务暂时不可用，请稍后重试
    public static final String I18N_LLM_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体"; // 大模型配置提示
    public static final String I18N_LLM_THREAD_INTENTION_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》意图识别"; // 会话意图识别大模型配置提示
    public static final String I18N_LLM_THREAD_EMOTION_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》情绪分析"; // 情绪分析大模型配置提示
    public static final String I18N_LLM_THREAD_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》会话总结"; // 工单大模型配置提示
    public static final String I18N_LLM_TICKET_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》工单生成"; // 工单大模型配置提示
    public static final String I18N_LLM_AGENT_INSPECTION_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》客服质检"; // 客服质检大模型配置提示
    public static final String I18N_LLM_LANGUAGE_TRANSLATION_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》语言翻译"; // 语言翻译大模型配置提示
    public static final String I18N_FAQ_SIMILAR_QUESTIONS_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》FAQ相似问题 faq_similar_questions 提示词 大模型"; // FAQ相似问题配置提示
    
    // AI 服务相关常量
    public static final String I18N_THINKING = I18N_PREFIX + "thinking"; // 正在思考中...
    public static final String I18N_SORRY_LLM_DISABLED = I18N_PREFIX + "sorry.llm.disabled"; // 抱歉，大模型功能未启用
    public static final String I18N_SORRY_SERVICE_UNAVAILABLE = I18N_PREFIX + "sorry.service.unavailable"; // 抱歉，服务暂时不可用，请稍后再试。
    public static final String I18N_CONTEXT_BASED_ANSWER = I18N_PREFIX + "context.based.answer";
    public static final String I18N_CONTEXT_LABEL = I18N_PREFIX + "context.label";
    public static final String I18N_QUESTION_LABEL = I18N_PREFIX + "question.label";
    public static final String I18N_SEARCH_RESULT_PREFIX = I18N_PREFIX + "search.result.prefix";
    public static final String I18N_SYSTEM_PREFIX = I18N_PREFIX + "system.prefix";
    public static final String I18N_USER_PREFIX = I18N_PREFIX + "user.prefix";
    public static final String I18N_ASSISTANT_PREFIX = I18N_PREFIX + "assistant.prefix";
    public static final String I18N_DEFAULT_SYSTEM_PROMPT = I18N_PREFIX + "default.system.prompt";

    // 线程路由策略相关常量
    /** 默认欢迎消息 */
    public static final String I18N_DEFAULT_WELCOME_MESSAGE = I18N_PREFIX + "default.welcome.message";
    
    /** 默认离线消息 */
    public static final String I18N_DEFAULT_OFFLINE_MESSAGE = I18N_PREFIX + "message.leave.tip";

    /** 工作组离线消息（兼容旧逻辑的兜底文案） */
    public static final String I18N_WORKGROUP_OFFLINE_FALLBACK_MESSAGE = I18N_PREFIX + "workgroup.offline.fallback.message";

    /** 工作组路由池（手动接入）等待提示 */
    public static final String I18N_WORKGROUP_MANUAL_ROUTING_POOL_WAITING_TIP = I18N_PREFIX + "workgroup.manual.routing.pool.waiting.tip";
    
    /** 排队等待消息 - 下一个 */
    public static final String I18N_QUEUE_NEXT_MESSAGE = I18N_PREFIX + "queue.next.message";
    
    /** 排队等待消息模板 */
    public static final String I18N_QUEUE_WAITING_MESSAGE_TEMPLATE = I18N_PREFIX + "queue.waiting.message.template";

    // "验证码发送过于频繁"
    public static final String I18N_CAPTCHA_SEND_TOO_FREQUENT = I18N_PREFIX + "auth.captcha.send.too.frequent"; // 验证码发送过于频繁，请稍后再试

    // "验证码已经发送，请勿重复发送"
    public static final String I18N_CAPTCHA_ALREADY_SENT = I18N_PREFIX + "auth.captcha.already.send"; // 验证码已经发送，请勿重复发送

    // "不支持的发送类型"
    public static final String I18N_CAPTCHA_UNSUPPORTED_TYPE = I18N_PREFIX + "auth.captcha.unsupported.type"; // 不支持的发送类型

    // "短信服务配置异常，请联系管理员"
    // public static final String I18N_SMS_SERVICE_CONFIG_ERROR = "短信服务配置异常，请联系管理员"; // 短信服务配置异常，请联系管理员

    // "短信服务暂时不可用，请稍后重试"
    public static final String I18N_SMS_PUSH_SERVICE_UNAVAILABLE = I18N_PREFIX + "sms.push.service.unavailable"; // 短信服务暂时不可用，请稍后重试

    // "邮件服务配置异常，请联系管理员"
    public static final String I18N_EMAIL_SERVICE_CONFIG_ERROR = I18N_PREFIX + "email.service.config.error"; // 邮件服务配置异常，请联系管理员

    // "邮件服务暂时不可用，请稍后重试"
    public static final String I18N_EMAIL_SERVICE_UNAVAILABLE = I18N_PREFIX + "email.service.unavailable"; // 邮件服务暂时不可用，请稍后重试

    // "处理请求时发生错误，请稍后重试"
    public static final String I18N_ROBOT_PROCESSING_ERROR = I18N_PREFIX + "robot.processing.error"; // 处理请求时发生错误，请稍后重试

    // 消息类型国际化常量
    public static final String I18N_MESSAGE_TYPE_WELCOME = I18N_PREFIX + "message.type.welcome";
    public static final String I18N_MESSAGE_TYPE_CONTINUE = I18N_PREFIX + "message.type.continue";
    public static final String I18N_MESSAGE_TYPE_SYSTEM = I18N_PREFIX + "message.type.system";
    public static final String I18N_MESSAGE_TYPE_DOCUMENT = I18N_PREFIX + "message.type.document";
    public static final String I18N_MESSAGE_TYPE_QUEUE = I18N_PREFIX + "message.type.queue";
    public static final String I18N_MESSAGE_TYPE_QUEUE_NOTICE = I18N_PREFIX + "message.type.queue.notice";
    public static final String I18N_MESSAGE_TYPE_QUEUE_UPDATE = I18N_PREFIX + "message.type.queue.update";
    public static final String I18N_MESSAGE_TYPE_QUEUE_ACCEPT = I18N_PREFIX + "message.type.queue.accept";
    public static final String I18N_MESSAGE_TYPE_QUEUE_TIMEOUT = I18N_PREFIX + "message.type.queue.timeout";
    public static final String I18N_MESSAGE_TYPE_QUEUE_CANCEL = I18N_PREFIX + "message.type.queue.cancel";
    public static final String I18N_MESSAGE_TYPE_NOTICE = I18N_PREFIX + "message.type.notice";
    public static final String I18N_MESSAGE_TYPE_TEXT = I18N_PREFIX + "message.type.text";
    public static final String I18N_MESSAGE_TYPE_IMAGE = I18N_PREFIX + "message.type.image";
    public static final String I18N_MESSAGE_TYPE_FILE = I18N_PREFIX + "message.type.file";
    public static final String I18N_MESSAGE_TYPE_EXTRA = I18N_PREFIX + "message.type.extra";
    public static final String I18N_MESSAGE_TYPE_AUDIO = I18N_PREFIX + "message.type.audio";
    public static final String I18N_MESSAGE_TYPE_VOICE = I18N_PREFIX + "message.type.voice";
    public static final String I18N_MESSAGE_TYPE_VIDEO = I18N_PREFIX + "message.type.video";
    public static final String I18N_MESSAGE_TYPE_MUSIC = I18N_PREFIX + "message.type.music";
    public static final String I18N_MESSAGE_TYPE_LOCATION = I18N_PREFIX + "message.type.location";
    public static final String I18N_MESSAGE_TYPE_LINK = I18N_PREFIX + "message.type.link";
    public static final String I18N_MESSAGE_TYPE_GOODS = I18N_PREFIX + "message.type.goods";
    public static final String I18N_MESSAGE_TYPE_CARD = I18N_PREFIX + "message.type.card";
    public static final String I18N_MESSAGE_TYPE_EVENT = I18N_PREFIX + "message.type.event";
    public static final String I18N_MESSAGE_TYPE_GUESS = I18N_PREFIX + "message.type.guess";
    public static final String I18N_MESSAGE_TYPE_HOT = I18N_PREFIX + "message.type.hot";
    public static final String I18N_MESSAGE_TYPE_SHORTCUT = I18N_PREFIX + "message.type.shortcut";
    public static final String I18N_MESSAGE_TYPE_ORDER = I18N_PREFIX + "message.type.order";
    public static final String I18N_MESSAGE_TYPE_POLL = I18N_PREFIX + "message.type.poll";
    public static final String I18N_MESSAGE_TYPE_POLL_SUBMIT = I18N_PREFIX + "message.type.poll.submit";
    public static final String I18N_MESSAGE_TYPE_PREFORM = I18N_PREFIX + "message.type.preform";
    public static final String I18N_MESSAGE_TYPE_FORM = I18N_PREFIX + "message.type.form";
    public static final String I18N_MESSAGE_TYPE_FORM_SUBMIT = I18N_PREFIX + "message.type.form.submit";
    public static final String I18N_MESSAGE_TYPE_CHOICE = I18N_PREFIX + "message.type.choice";
    public static final String I18N_MESSAGE_TYPE_CHOICE_SUBMIT = I18N_PREFIX + "message.type.choice.submit";
    public static final String I18N_MESSAGE_TYPE_CONFIRM = I18N_PREFIX + "message.type.confirm";
    public static final String I18N_MESSAGE_TYPE_CONFIRM_SUBMIT = I18N_PREFIX + "message.type.confirm.submit";
    public static final String I18N_MESSAGE_TYPE_LEAVE_MSG = I18N_PREFIX + "message.type.leave.msg";
    public static final String I18N_MESSAGE_TYPE_LEAVE_MSG_SUBMIT = I18N_PREFIX + "message.type.leave.msg.submit";
    public static final String I18N_MESSAGE_TYPE_LEAVE_MSG_REPLIED = I18N_PREFIX + "message.type.leave.msg.replied";
    public static final String I18N_MESSAGE_TYPE_CUSTOMER_SUBMIT = I18N_PREFIX + "message.type.customer.submit";
    public static final String I18N_MESSAGE_TYPE_THREAD = I18N_PREFIX + "message.type.thread";
    public static final String I18N_MESSAGE_TYPE_SYSTEM_ALARM = I18N_PREFIX + "message.type.system.alarm";
    public static final String I18N_MESSAGE_TYPE_TICKET = I18N_PREFIX + "message.type.ticket";
    public static final String I18N_MESSAGE_TYPE_TICKET_SUBMIT = I18N_PREFIX + "message.type.ticket.submit";
    public static final String I18N_MESSAGE_TYPE_TYPING = I18N_PREFIX + "message.type.typing";
    public static final String I18N_MESSAGE_TYPE_PROCESSING = I18N_PREFIX + "message.type.processing";
    public static final String I18N_MESSAGE_TYPE_STICKER = I18N_PREFIX + "message.type.sticker";
    public static final String I18N_MESSAGE_TYPE_EMAIL = I18N_PREFIX + "message.type.email";
    public static final String I18N_MESSAGE_TYPE_BUTTON = I18N_PREFIX + "message.type.button";
    public static final String I18N_MESSAGE_TYPE_BUTTON_SUBMIT = I18N_PREFIX + "message.type.button.submit";
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM = I18N_PREFIX + "message.type.robot.stream";
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_START = I18N_PREFIX + "message.type.robot.stream.start";
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_END = I18N_PREFIX + "message.type.robot.stream.end";
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_CANCEL = I18N_PREFIX + "message.type.robot.stream.cancel";
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_UNANSWERED = I18N_PREFIX + "message.type.robot.stream.unanswered";
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_ERROR = I18N_PREFIX + "message.type.robot.stream.error";
    public static final String I18N_MESSAGE_TYPE_PREVIEW = I18N_PREFIX + "message.type.preview";
    public static final String I18N_MESSAGE_TYPE_RECALL = I18N_PREFIX + "message.type.recall";
    public static final String I18N_MESSAGE_TYPE_DELIVERED = I18N_PREFIX + "message.type.delivered";
    public static final String I18N_MESSAGE_TYPE_READ = I18N_PREFIX + "message.type.read";
    public static final String I18N_MESSAGE_TYPE_QUOTATION = I18N_PREFIX + "message.type.quotation";
    public static final String I18N_MESSAGE_TYPE_KICKOFF = I18N_PREFIX + "message.type.kickoff";
    public static final String I18N_MESSAGE_TYPE_SHAKE = I18N_PREFIX + "message.type.shake";
    public static final String I18N_MESSAGE_TYPE_FAQ = I18N_PREFIX + "message.type.faq";
    public static final String I18N_MESSAGE_TYPE_FAQ_QUESTION = I18N_PREFIX + "message.type.faq.question";
    public static final String I18N_MESSAGE_TYPE_FAQ_ANSWER = I18N_PREFIX + "message.type.faq.answer";
    public static final String I18N_MESSAGE_TYPE_ROBOT = I18N_PREFIX + "message.type.robot";
    public static final String I18N_MESSAGE_TYPE_ROBOT_CANCEL = I18N_PREFIX + "message.type.robot.cancel";
    public static final String I18N_MESSAGE_TYPE_ROBOT_UNANSWERED = I18N_PREFIX + "message.type.robot.unanswered";
    public static final String I18N_MESSAGE_TYPE_ROBOT_ERROR = I18N_PREFIX + "message.type.robot.error";
    public static final String I18N_MESSAGE_TYPE_ROBOT_UP = I18N_PREFIX + "message.type.robot.up";
    public static final String I18N_MESSAGE_TYPE_ROBOT_DOWN = I18N_PREFIX + "message.type.robot.down";
    public static final String I18N_MESSAGE_TYPE_RATE = I18N_PREFIX + "message.type.rate";
    public static final String I18N_MESSAGE_TYPE_RATE_INVITE = I18N_PREFIX + "message.type.rate.invite";
    public static final String I18N_MESSAGE_TYPE_RATE_SUBMIT = I18N_PREFIX + "message.type.rate.submit";
    public static final String I18N_MESSAGE_TYPE_RATE_CANCEL = I18N_PREFIX + "message.type.rate.cancel";
    public static final String I18N_MESSAGE_TYPE_AUTO_CLOSED = I18N_PREFIX + "message.type.auto.closed";
    public static final String I18N_MESSAGE_TYPE_AGENT_CLOSED = I18N_PREFIX + "message.type.agent.closed";
    public static final String I18N_MESSAGE_TYPE_TRANSFER = I18N_PREFIX + "message.type.transfer";
    public static final String I18N_MESSAGE_TYPE_TRANSFER_REJECT = I18N_PREFIX + "message.type.transfer.reject";
    public static final String I18N_MESSAGE_TYPE_TRANSFER_ACCEPT = I18N_PREFIX + "message.type.transfer.accept";
    public static final String I18N_MESSAGE_TYPE_TRANSFER_TIMEOUT = I18N_PREFIX + "message.type.transfer.timeout";
    public static final String I18N_MESSAGE_TYPE_TRANSFER_CANCEL = I18N_PREFIX + "message.type.transfer.cancel";
    public static final String I18N_MESSAGE_TYPE_INVITE = I18N_PREFIX + "message.type.invite";
    public static final String I18N_MESSAGE_TYPE_INVITE_REJECT = I18N_PREFIX + "message.type.invite.reject";
    public static final String I18N_MESSAGE_TYPE_INVITE_ACCEPT = I18N_PREFIX + "message.type.invite.accept";
    public static final String I18N_MESSAGE_TYPE_INVITE_TIMEOUT = I18N_PREFIX + "message.type.invite.timeout";
    public static final String I18N_MESSAGE_TYPE_INVITE_CANCEL = I18N_PREFIX + "message.type.invite.cancel";
    public static final String I18N_MESSAGE_TYPE_INVITE_EXIT = I18N_PREFIX + "message.type.invite.exit";
    public static final String I18N_MESSAGE_TYPE_INVITE_REMOVE = I18N_PREFIX + "message.type.invite.remove";
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR = I18N_PREFIX + "message.type.invite.visitor";
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR_REJECT = I18N_PREFIX + "message.type.invite.visitor.reject";
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR_ACCEPT = I18N_PREFIX + "message.type.invite.visitor.accept";
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR_TIMEOUT = I18N_PREFIX + "message.type.invite.visitor.timeout";
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR_CANCEL = I18N_PREFIX + "message.type.invite.visitor.cancel";
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP = I18N_PREFIX + "message.type.invite.group";
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP_REJECT = I18N_PREFIX + "message.type.invite.group.reject";
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP_ACCEPT = I18N_PREFIX + "message.type.invite.group.accept";
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP_TIMEOUT = I18N_PREFIX + "message.type.invite.group.timeout";
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP_CANCEL = I18N_PREFIX + "message.type.invite.group.cancel";
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE = I18N_PREFIX + "message.type.invite.kbase";
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE_REJECT = I18N_PREFIX + "message.type.invite.kbase.reject";
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE_ACCEPT = I18N_PREFIX + "message.type.invite.kbase.accept";
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE_TIMEOUT = I18N_PREFIX + "message.type.invite.kbase.timeout";
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE_CANCEL = I18N_PREFIX + "message.type.invite.kbase.cancel";
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION = I18N_PREFIX + "message.type.invite.organization";
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_REJECT = I18N_PREFIX + "message.type.invite.organization.reject";
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_ACCEPT = I18N_PREFIX + "message.type.invite.organization.accept";
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_TIMEOUT = I18N_PREFIX + "message.type.invite.organization.timeout";
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_CANCEL = I18N_PREFIX + "message.type.invite.organization.cancel";
    public static final String I18N_MESSAGE_TYPE_ARTICLE = I18N_PREFIX + "message.type.article";
    public static final String I18N_MESSAGE_TYPE_INVITE_AUDIO = I18N_PREFIX + "message.type.invite.audio";
    public static final String I18N_MESSAGE_TYPE_INVITE_VIDEO = I18N_PREFIX + "message.type.invite.video";
    public static final String I18N_MESSAGE_TYPE_INVITE_AUDIO_REJECT = I18N_PREFIX + "message.type.invite.audio.reject";
    public static final String I18N_MESSAGE_TYPE_INVITE_VIDEO_REJECT = I18N_PREFIX + "message.type.invite.video.reject";
    public static final String I18N_MESSAGE_TYPE_INVITE_AUDIO_ACCEPT = I18N_PREFIX + "message.type.invite.audio.accept";
    public static final String I18N_MESSAGE_TYPE_INVITE_VIDEO_ACCEPT = I18N_PREFIX + "message.type.invite.video.accept";
    public static final String I18N_MESSAGE_TYPE_INVITE_AUDIO_CANCEL = I18N_PREFIX + "message.type.invite.audio.cancel";
    public static final String I18N_MESSAGE_TYPE_INVITE_VIDEO_CANCEL = I18N_PREFIX + "message.type.invite.video.cancel";
    public static final String I18N_MESSAGE_TYPE_INVITE_AUDIO_TIMEOUT = I18N_PREFIX + "message.type.invite.audio.timeout";
    public static final String I18N_MESSAGE_TYPE_INVITE_VIDEO_TIMEOUT = I18N_PREFIX + "message.type.invite.video.timeout";
    public static final String I18N_MESSAGE_TYPE_GROUP_CREATE = I18N_PREFIX + "message.type.group.create";
    public static final String I18N_MESSAGE_TYPE_GROUP_INVITE = I18N_PREFIX + "message.type.group.invite";
    public static final String I18N_MESSAGE_TYPE_GROUP_DISMISS = I18N_PREFIX + "message.type.group.dismiss";
    public static final String I18N_MESSAGE_TYPE_NOTIFICATION_AGENT_REPLY_TIMEOUT = I18N_PREFIX + "message.type.notification.agent.reply.timeout";
    public static final String I18N_MESSAGE_TYPE_NOTIFICATION_RATE_SUBMITTED = I18N_PREFIX + "message.type.notification.rate.submitted";
    public static final String I18N_MESSAGE_TYPE_ERROR = I18N_PREFIX + "message.type.error";
    public static final String I18N_MESSAGE_TYPE_UNKNOWN = I18N_PREFIX + "message.type.unknown";
    // 补充的消息类型
    public static final String I18N_MESSAGE_TYPE_URL = I18N_PREFIX + "message.type.url";
    public static final String I18N_MESSAGE_TYPE_PHONE_NUMBER = I18N_PREFIX + "message.type.phone.number";
    public static final String I18N_MESSAGE_TYPE_EMAIL_ADDRESS = I18N_PREFIX + "message.type.email.address";
    public static final String I18N_MESSAGE_TYPE_WECHAT_NUMBER = I18N_PREFIX + "message.type.wechat.number";
    public static final String I18N_MESSAGE_TYPE_BLOG = I18N_PREFIX + "message.type.blog";

}
