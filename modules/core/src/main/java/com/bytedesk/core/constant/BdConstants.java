package com.bytedesk.core.constant;

import java.io.File;

/**
 *
 * @author xiaper.io
 */
public class BdConstants {

    // Prevents instantiation
    private BdConstants() {
    }

    /**
     * 项目名称
     */
    public static final String PROJECT_NAME = "ByteDesk";
    public static final boolean IS_DEBUG = false;

    // 空字符串
    public static final String EMPTY_STRING = "";
    public static final String EMPTY_JSON_STRING = "{}";

    /**
     * Path separator.
     */
    public static final String FILE_SEPARATOR = File.separator;

    public static final String DEFAULT_USER_DESCRIPTION = "user default description";

    /**
     * 是否私有部署
     */
    public static final boolean IS_PRIVATE_DEPLOY = false; // 私有部署
    public static final String PRIVATE_DEPLOY_VALIDEDATE = "2022-09-01 00:00:00"; // 到期时间
    public static final int PRIVATE_DEPLOY_AGENTCOUNT = 2; // 客服数量

    /**
     * 创建web默认值
     */
    public static final String DEFAULT_ADMIN_NICKNAME = "管理员";
    public static final String DEFAULT_WEB_NAME = "默认网站";
    public static final String DEFAULT_WEB_URL = "www.bytedesk.com";
    public static final String DEFAULT_AT_EMAIL = "@email.com";
    // public static final String DEFAULT_AT_WECHAT = "@wechat.qq.com";
    public static final String DEFAULT_BROWSE_START = " 网页浏览开始";
    public static final String DEFAULT_BROWSE_END = " 网页浏览结束";
    public static final String DEFAULT_BROWSE_INVITE = "邀请访客会话";
    public static final String DEFAULT_ROBOT_NICKNAME = "智能客服";
    public static final String DEFAULT_ROBOT_DESCRIPTION = "让客户服务更智能";
    public static final String DEFAULT_ONLINE = "上线";
    public static final String DEFAULT_OFFLINE = "离线";
    public static final String DEFAULT_QUEUE_LEAVE = "退出排队";
    //
    public static final String DEFAULT_TICKET_CONTENT = "ticketInit";
    public static final String DEFAULT_TICKET_IGNORE_CLOSE_TIP = "忽略工单-关闭会话";
    public static final String DEFAULT_TICKET_DONE_CLOSE_TIP = "解决工单-关闭会话";
    public static final String DEFAULT_TICKET_CLOSE_TIP = "访客关闭工单";

    // 此uid为系统通知用户uid
    public static final String SYSTEM_NOTIFICATION_USER_UUID = "201808221551195";

    /**
     * 创建workgroup默认值
     */
    public static final String DEFAULT_WORK_GROUP_NAME = "默认工作组";
    public static final String DEFAULT_WORK_GROUP_SLOGAN = "全心全意为您服务";
    public static final String DEFAULT_WORK_GROUP_ROBOT_TIP = "您好, 我是智能机器人, 有什么可以帮您的？";
    public static final String DEFAULT_WORK_GROUP_WELCOME_TIP = "您好，有什么可以帮您的？";
    public static final String DEFAULT_WORK_GROUP_ROBOT_WELCOME_TIP = "您好，我是智能助理，请直接输入问题";
    /**
     * TODO: 支持管理后台自定义
     */
    public static final String DEFAULT_WORK_GROUP_ACCEPT_TIP = "您好，有什么可以帮您的？";
    /**
     * TODO: 支持管理后台自定义
     */
    public static final String DEFAULT_WORK_GROUP_NON_WORKING_TIME_TIP = "当前非工作时间，请自助查询或留言";
    /**
     * TODO: 支持管理后台自定义
     */
    public static final String DEFAULT_WORK_GROUP_OFFLINE_TIP = "当前无客服在线，请自助查询或留言";
    public static final String DEFAULT_WORK_GROUP_DESCRIPTION = "DEFAULT_WORKGROUP_DESCRIPTION";
    public static final String DEFAULT_WORK_GROUP_ABOUT = "关于我们";
    public static final String DEFAULT_WORK_GROUP_AGENT_CLOSE_TIP = "客服关闭会话";
    public static final String DEFAULT_WORK_GROUP_VISITOR_CLOSE_TIP = "访客关闭会话";
    public static final String DEFAULT_WORK_GROUP_AUTO_CLOSE_TIP = "长时间没有对话，系统自动关闭会话";
    public static final String DEFAULT_WORK_GROUP_NON_WORKING_TIME_CLOSE_TIP = "非工作时间关闭会话";
    public static final String DEFAULT_WORK_GROUP_OFFLINE_CLOSE_TIP = "客服离线关闭会话";
    public static final String DEFAULT_WORK_GROUP_INVITE_RATE = "邀请评价";
    public static final String DEFAULT_WORK_GROUP_VISITOR_RATE = "已评价";
    public static final String DEFAULT_WORK_GROUP_QUESTIONNAIRE_TIP = "咨询前问卷";
    public static final String DEFAULT_WORK_GROUP_ROBOT_ANSWER_NOT_FOUND = "抱歉，未找到相应答案";
    public static final String DEFAULT_WORK_GROUP_ROBOT_MAX_QUEUE_COUNT_EXCEED_TIP = "人工繁忙，请稍后再试";
    public static final String DEFAULT_WORK_GROUP_ROBOT_MAX_QUEUE_SECOND_EXCEED_TIP = "很抱歉，目前人工服务全忙，请稍后";
    public static final String DEFAULT_WORK_GROUP_ROBOT_TO_AGENT_TIP = "转人工客服，关闭机器人会话";
    public static final String DEFAULT_WORK_GROUP_DEFAULT_TOP_TIP = "DEFAULT_WORKGROUP_TOP_TIP";

    /**
     * 工作组
     */
    public static final String DEFAULT_WORK_GROUP_REQUEST_TIP = "请求会话"; // = "requestThread";
    public static final String DEFAULT_WORK_GROUP_RE_REQUEST_TIP = "继续会话"; // = "continueThread";
    public static final String DEFAULT_WORK_GROUP_JOIN_THREAD = "joinThread";
    public static final String DEFAULT_WORK_GROUP_TRANSFER = "transferThread";
    public static final String DEFAULT_WORK_GROUP_TRANSFER_ACCEPT = "acceptTransferThread";
    public static final String DEFAULT_WORK_GROUP_TRANSFER_REJECT = "rejectTransferThread";
    public static final String DEFAULT_WORK_GROUP_INVITE = "inviteThread";
    public static final String DEFAULT_WORK_GROUP_INVITE_VIDEO = "inviteVideoThread";
    public static final String DEFAULT_WORK_GROUP_INVITE_AUDIO = "inviteAudioThread";
    public static final String DEFAULT_WORK_GROUP_INVITE_ACCEPT = "acceptInviteThread";
    public static final String DEFAULT_WORK_GROUP_INVITE_REJECT = "rejectInviteThread";
    public static final String DEFAULT_WORK_GROUP_QUEUE_ACCEPT = "joinQueueThread";
    public static final String DEFAULT_WORK_GROUP_QUEUE_IGNORE = "ignoreQueueThread";
    public static final String DEFAULT_WORK_GROUP_QUEUE_TIMEOUT = "timeoutQueueThread";
    //
    public static final String DEFAULT_VISITOR_CONNECT = "visitorConnect";
    public static final String DEFAULT_VISITOR_DISCONNECT = "visitorDisconnect";

    /**
     * 群组
     */
    public static final String DEFAULT_GROUP_DESCRIPTION = "群组描述：这个家伙很懒，什么也没写";
    public static final String DEFAULT_GROUP_ANNOUNCEMENT = "群组公告：这个家伙很懒，什么也没写";

    /**
     * 请求一对一会话
     */
    public static final String DEFAULT_VISITOR_REQUEST_THREAD = " 请求会话";
    public static final String DEFAULT_AGENT_OFFLINE_TIP = "当前客服不在线，请留言";
    public static final String DEFAULT_AGENT_WELCOME_TIP = "您好，有什么可以帮您的?";
    public static final String DEFAULT_AGENT_DESCRIPTION = "DEFAULT_AGENT_DESCRIPTION";
    public static final String DEFAULT_AGENT_AUTO_REPLY_CONTENT = "无自动回复";

    /**
     * 微信公众号默认关注提示语
     */
    public static final String DEFAULT_WECHAT_WELCOME_TIP = "欢迎关注";

    // 权限常量
    public static final String AUTHORITY_VISITOR_CHAT = "visitor_chat";

    /**
     * 通过淘宝接口查询ip所属城市：
     * http://ip.taobao.com/service/getIpInfo.php?ip=60.186.185.142
     */
    public static final String IP_INFO_TAOBAO_URL = "http://ip.taobao.com/service/getIpInfo.php?ip=";

    /**
     * 私有部署用户验证有效性URL
     */
    public static final String PRIVATE_SERVER_VALIDATE_URL = "https://api.bytedesk.com/visitor/api/server/validate?uId=";

    /**
     * 时间格式
     */
    public static final String DATETIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 微信授权事件类型
     */
    public static final String WECHAT_OPEN_PLATFORM_INFO_TYPE_AUTHORIZED = "authorized";
    public static final String WECHAT_OPEN_PLATFORM_INFO_TYPE_UNAUTHORIZED = "unauthorized";
    public static final String WECHAT_OPEN_PLATFORM_INFO_TYPE_UPDATE_AUTHORIZED = "updateauthorized";
    public static final String WECHAT_OPEN_PLATFORM_INFO_TYPE_COMPONENT_VERIFY_TICKET = "component_verify_ticket";

    /**
     * 跳转去授权URL
     */
    public static final String WECHAT_OPEN_PLATFORM_REDIRECT_URI = "https://wechat.bytedesk.com/wechat/mp/oauth/redirect/";
    /**
     * 授权事件接收URL
     */
    public static final String WECHAT_OPEN_PLATFORM_OAUTH_CALLBACK_URI = "https://wechat.bytedesk.com/wechat/mp/oauth/callback";
    /**
     * 微信客服授权回调URL
     */
    public static final String WECHAT_KEFU_OAUTH_CALLBACK_URI = "https://wechat.bytedesk.com/wechat/kefu/oauth/callback";
    /**
     * 绑定公众后跳转URL
     */
    // public static final String WECHAT_OPEN_PLATFORM_ADMIN_CALLBACK_URL =
    // ".bytedesk.com/admin#/admin/setting/app";
    public static final String WECHAT_OPEN_PLATFORM_ADMIN_CALLBACK_URL = "https://www.bytedesk.com/antv/kefu/channel-list";
    /**
     * 访客验证URL
     */
    public static final String VISITOR_TOKEN_URL = "/visitor/token";
    public static final String VISITOR_TOKEN_USERNAME = "username";
    public static final String VISITOR_TOKEN_PASSWORD = "password";
    /**
     * 短信验证URL
     */
    public static final String MOBILE_TOKEN_URL = "/mobile/token";
    public static final String MOBILE_TOKEN_MOBILE = "mobile";
    public static final String MOBILE_TOKEN_CODE = "code";
    /**
     * 邮箱验证URL
     */
    public static final String EMAIL_TOKEN_URL = "/email/token";
    public static final String EMAIL_TOKEN_EMAIL = "email";
    public static final String EMAIL_TOKEN_CODE = "code";
    /**
     * 
     */
    public static final String SCAN_URL = "/scan/token";
    public static final String SCAN_USERNAME = "username";
    public static final String SCAN_CODE = "code";
    /**
     * 微信登录URL
     */
    public static final String WECHAT_TOKEN_URL = "/wechat/token";
    public static final String WECHAT_TOKEN_GRANT_TYPE = "wechat";
    public static final String WECHAT_TOKEN_UNIONID = "unionid";
    /**
     * 企业微信登录URL
     */
    public static final String WECHAT_WORK_TOKEN_URL = "/wechat/work/token";
    public static final String WECHAT_WORK_TOKEN_GRANT_TYPE = "wechat_work";
    public static final String WECHAT_WORK_TOKEN_USERID = "userid";
    /**
     *
     */
    // public static final String TOPIC_PROTOBUF_PREFIX = "protobuf/";
    // public static final String TOPIC_JSON_PREFIX = "json/";

    // 微语
    public static final String PLATFORM_BYTEDESK = "bytedesk";
    // 良师宝
    public static final String PLATFORM_LIANGSHIBAO = "liangshibao";
    // 招投标
    public static final String PLATFORM_ZHAOBIAO = "zhaobiao";
    // 今日美语
    public static final String PLATFORM_MEIYU = "meiyu";
    // 微语题库
    public static final String PLATFORM_TIKU = "tiku";
    // 
    public static final String PLATFORM_WEITONGBU = "weitongbu";
    // 
    public static final String PLATFORM_WEIYU_AI = "weiyu_ai";
    // 微语TTS-文字转语音
    public static final String PLATFORM_WEIYU_TTS = "weiyu_tts";
    

    /**
     * 华为推送
     */
    public static final String HW_PUSH_TOKEN_SERVER = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";
    public static final String HW_PUSH_OPEN_URL = "https://push-api.cloud.huawei.com";

    //
    public static final String TOKEN = "token";

    /**
     * 当无客服在线时，是跳转留言表单 or 使用对话窗口留言
     */
    public static final String LEAVE_MESSAGE_TYPE_FORM = "form";
    public static final String LEAVE_MESSAGE_TYPE_CHAT = "chat";


    // 
    

}
