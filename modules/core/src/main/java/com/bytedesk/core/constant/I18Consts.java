/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 22:25:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-07 12:19:47
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

    private I18Consts() {
    }

    // 国际化常量定义
    public static final String I18N_PREFIX = "i18n.";
    // 账号相关
    public static final String I18N_USERNAME_OR_PASSWORD_INCORRECT = I18N_PREFIX + "username.or.password.incorrect"; // 用户名或密码不正确
    public static final String I18N_MOBILE_ALREADY_EXISTS = I18N_PREFIX + "mobile.already.exists"; // 手机号已存在
    public static final String I18N_EMAIL_ALREADY_EXISTS = I18N_PREFIX + "email.already.exists"; // 邮箱已存在
    public static final String I18N_MOBILE_NOT_EXISTS = I18N_PREFIX + "mobile.not.exists"; // 手机号不存在
    public static final String I18N_EMAIL_NOT_EXISTS = I18N_PREFIX + "email.not.exists"; // 邮箱不存在
    public static final String I18N_MOBILE_FORMAT_ERROR = I18N_PREFIX + "mobile.format.error"; // 手机号格式错误
    public static final String I18N_EMAIL_FORMAT_ERROR = I18N_PREFIX + "email.format.error"; // 邮箱格式错误
    // 验证码相关
    public static final String I18N_AUTH_CAPTCHA_SEND_SUCCESS = I18N_PREFIX + "auth.captcha.send.success"; // 验证码发送成功
    public static final String I18N_AUTH_CAPTCHA_ERROR = I18N_PREFIX + "auth.captcha.error"; // 验证码错误
    public static final String I18N_AUTH_CAPTCHA_EXPIRED = I18N_PREFIX + "auth.captcha.expired"; // 验证码已过期
    public static final String I18N_AUTH_CAPTCHA_ALREADY_SEND = I18N_PREFIX + "auth.captcha.already.send"; // 验证码已发送
    public static final String I18N_AUTH_CAPTCHA_VALIDATE_FAILED = I18N_PREFIX + "auth.captcha.validate.failed"; // 验证码验证失败
    // 助手相关
    public static final String I18N_FILE_ASSISTANT_NAME = I18N_PREFIX + "file.assistant"; // 文件助手
    public static final String I18N_CLIPBOARD_ASSISTANT_NAME = I18N_PREFIX + "clipboard.assistant"; // 剪贴板助手
    public static final String I18N_INTENT_REWRITE_ASSISTANT_NAME = I18N_PREFIX + "intent.rewrite.assistant"; // 意图改写
    public static final String I18N_INTENT_CLASSIFICATION_ASSISTANT_NAME = I18N_PREFIX + "intent.classification.assistant"; // 意图识别
    public static final String I18N_EMOTION_ASSISTANT_NAME = I18N_PREFIX + "emotion.assistant"; // 情绪分析
    public static final String I18N_FILE_ASSISTANT_DESCRIPTION = I18N_PREFIX + "file.assistant.description"; // 手机、电脑文件互传
    public static final String I18N_CLIPBOARD_ASSISTANT_DESCRIPTION = I18N_PREFIX + "clipboard.assistant.description"; // 手机、电脑剪贴板内容互传
    public static final String I18N_INTENT_REWRITE_ASSISTANT_DESCRIPTION = I18N_PREFIX + "intent.rewrite.assistant.description"; // 用于改写客户意图
    public static final String I18N_INTENT_CLASSIFICATION_ASSISTANT_DESCRIPTION = I18N_PREFIX + "intent.classification.assistant.description"; // 用于识别客户意图
    public static final String I18N_EMOTION_ASSISTANT_DESCRIPTION = I18N_PREFIX + "emotion.assistant.description"; // 用于分析客户情绪
    // 系统通知
    public static final String I18N_SYSTEM_NOTIFICATION_NAME = I18N_PREFIX + "system.notification"; // 系统通知
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
    public static final String I18N_LEAVEMSG_TIP = I18N_PREFIX + "leavemsg.tip"; // 留言提示
    public static final String I18N_REENTER_TIP = I18N_PREFIX + "reenter.tip"; // 重新进入提示
    public static final String I18N_QUEUE_TIP = I18N_PREFIX + "queue.tip"; // 排队提示
    public static final String I18N_QUEUE_MESSAGE_TEMPLATE = I18N_PREFIX + "queue.message.template"; // 您前面还有{0}人排队
    public static final String I18N_AUTO_CLOSE_TIP = I18N_PREFIX + "auto.close.tip"; // 会话已自动关闭
    public static final String I18N_AGENT_CLOSE_TIP = I18N_PREFIX + "agent.close.tip"; // 客服已关闭会话
    public static final String I18N_AGENT_TRANSFER_TIP = I18N_PREFIX + "agent.transfer.tip"; // 客服已将会话转接
    // 评价提示
    public static final String I18N_INVITE_RATE_TIP = I18N_PREFIX + "invite.rate.tip"; // 邀请评价提示
    // 统一
    public static final String I18N_UNIFIED_NICKNAME = I18N_PREFIX + "unified.nickname"; // 统一昵称
    public static final String I18N_UNIFIED_DESCRIPTION = I18N_PREFIX + "unified.description"; // 统一描述
    // 工作组
    public static final String I18N_WORKGROUP_NICKNAME = I18N_PREFIX + "workgroup.nickname"; // 工作组昵称
    public static final String I18N_WORKGROUP_BEFORE_NICKNAME = I18N_PREFIX + "workgroup.before.nickname"; // 工作组前缀昵称
    public static final String I18N_WORKGROUP_AFTER_NICKNAME = I18N_PREFIX + "workgroup.after.nickname"; // 工作组后缀昵称
    public static final String I18N_WORKGROUP_DESCRIPTION = I18N_PREFIX + "workgroup.description"; // 工作组描述
    public static final String I18N_WORKGROUP_BEFORE_DESCRIPTION = I18N_PREFIX + "workgroup.before.description"; // 工作组前缀描述
    public static final String I18N_WORKGROUP_AFTER_DESCRIPTION = I18N_PREFIX + "workgroup.after.description"; // 工作组后缀描述
    // 客服相关
    public static final String I18N_AGENT_NICKNAME = I18N_PREFIX + "agent.nickname"; // 客服昵称
    public static final String I18N_AGENT_DESCRIPTION = I18N_PREFIX + "agent.description"; // 客服描述
    public static final String I18N_AGENT_OFFLINE = I18N_PREFIX + "agent.offline"; // 客服离线
    public static final String I18N_AGENT_UNAVAILABLE = I18N_PREFIX + "agent.unavailable"; // 客服不可用
    public static final String I18N_AGENT_AVAILABLE = I18N_PREFIX + "agent.available"; // 客服可用
    // 用户相关
    public static final String I18N_USER_NICKNAME = I18N_PREFIX + "user.nickname"; // 用户昵称
    public static final String I18N_USER_DESCRIPTION = I18N_PREFIX + "user.description"; // 用户描述
    public static final String I18N_USER_NOT_FOUND = I18N_PREFIX + "user.not.found"; // 用户未找到
    public static final String I18N_CREATE_FAILED = I18N_PREFIX + "create.failed"; // 创建失败
    // 其他
    public static final String I18N_DESCRIPTION = I18N_PREFIX + "description"; // 描述
    public static final String I18N_TODO = I18N_PREFIX + "todo"; // 待办事项
    // 机器人相关
    public static final String I18N_ROBOT_NICKNAME = I18N_PREFIX + "robot.nickname"; // 机器人昵称
    public static final String I18N_ROBOT_NAME = I18N_PREFIX + "robot.name"; // 机器人名称
    public static final String I18N_ROBOT_DESCRIPTION = I18N_PREFIX + "robot.description"; // 机器人描述
    public static final String I18N_ROBOT_AGENT_ASSISTANT_NICKNAME = I18N_PREFIX + "robot.agent.assistant.nickname"; // 客服助理机器人昵称
    public static final String I18N_ROBOT_REPLY = I18N_PREFIX + "robot.reply"; // 机器人回复
    public static final String I18N_ROBOT_NO_REPLY = I18N_PREFIX + "robot.noreply"; // 机器人无回复
    // 角色相关
    public static final String I18N_ADMIN = I18N_PREFIX + "admin"; // 管理员
    public static final String I18N_ADMIN_DESCRIPTION = I18N_PREFIX + "admin.description"; // 管理员描述
    public static final String I18N_MEMBER = I18N_PREFIX + "member"; // 成员
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
    public static final String I18N_VIP_REST_API = I18N_PREFIX + "vip.api"; // VIP REST API
    // 初始化组织时使用
    public static final String I18N_FAQ_CATEGORY_DEMO_1 = I18N_PREFIX + "faq.category.demo.1"; // FAQ示例分类1
    public static final String I18N_FAQ_CATEGORY_DEMO_2 = I18N_PREFIX + "faq.category.demo.2"; // FAQ示例分类2
    // FAQ示例
    public static final String I18N_FAQ_DEMO_QUESTION_1 = I18N_PREFIX + "faq.demo.title.1"; // FAQ示例问题1
    public static final String I18N_FAQ_DEMO_ANSWER_1 = I18N_PREFIX + "faq.demo.content.1"; // FAQ示例答案1
    public static final String I18N_FAQ_DEMO_QUESTION_2 = I18N_PREFIX + "faq.demo.title.2"; // FAQ示例问题2
    public static final String I18N_FAQ_DEMO_ANSWER_2 = I18N_PREFIX + "faq.demo.content.2"; // FAQ示例答案2
    // 快捷按钮示例
    public static final String I18N_QUICK_BUTTON_DEMO_TITLE_1 = I18N_PREFIX + "quick.button.demo.title.1"; // 快捷按钮示例标题1
    public static final String I18N_QUICK_BUTTON_DEMO_CONTENT_1 = I18N_PREFIX + "quick.button.demo.content.1"; // 快捷按钮示例内容1
    public static final String I18N_QUICK_BUTTON_DEMO_TITLE_2 = I18N_PREFIX + "quick.button.demo.title.2"; // 快捷按钮示例标题2
    public static final String I18N_QUICK_BUTTON_DEMO_CONTENT_2 = I18N_PREFIX + "quick.button.demo.content.2"; // 快捷按钮示例内容2
    // 群组相关
    public static final String I18N_GROUP_NAME = I18N_PREFIX + "group.name"; // 群组名称
    public static final String I18N_GROUP_DESCRIPTION = I18N_PREFIX + "group.description"; // 群组描述
    // 通知相关
    public static final String I18N_NOTICE_TITLE = I18N_PREFIX + "notice.title"; // 通知标题
    public static final String I18N_NOTICE_TYPE = I18N_PREFIX + "notice.type"; // 通知类型
    public static final String I18N_NOTICE_CONTENT = I18N_PREFIX + "notice.content"; // 通知内容
    public static final String I18N_NOTICE_IP = I18N_PREFIX + "notice.ip"; // 通知IP
    public static final String I18N_NOTICE_IP_LOCATION = I18N_PREFIX + "notice.ipLocation"; // 通知IP位置
    public static final String I18N_NOTICE_URL = I18N_PREFIX + "notice.url"; // 通知URL
    public static final String I18N_NOTICE_EXTRA = I18N_PREFIX + "notice.extra"; // 通知额外信息
    // 文件解析
    public static final String I18N_NOTICE_PARSE_FILE_SUCCESS = I18N_PREFIX + "notice.parse.file.success"; // 文件解析成功
    public static final String I18N_NOTICE_PARSE_FILE_ERROR = I18N_PREFIX + "notice.parse.file.error"; // 文件解析错误
    // 会话状态
    public static final String I18N_AUTO_CLOSED = I18N_PREFIX + "auto.closed"; // 已自动关闭
    public static final String I18N_AGENT_CLOSED = I18N_PREFIX + "agent.closed"; // 已被客服关闭
    public static final String I18N_AGENT_TRANSFER = I18N_PREFIX + "agent.transfer"; // 已被客服转接
    // 黑名单
    public static final String I18N_BLACK_USER_ALREADY_EXISTS = I18N_PREFIX + "black.user.already.exists"; // 黑名单用户已存在
    // 输入状态
    public static final String I18N_TYPING = I18N_PREFIX + "typing"; // 正在输入

    // GlobalControllerAdvice 错误国际化常量
    public static final String I18N_USER_SIGNUP_FIRST = I18N_PREFIX + "user.signup.first"; // 请先注册用户
    public static final String I18N_EMAIL_SIGNUP_FIRST = I18N_PREFIX + "email.signup.first"; // 请先使用邮箱注册
    public static final String I18N_MOBILE_SIGNUP_FIRST = I18N_PREFIX + "mobile.signup.first"; // 请先使用手机号注册
    public static final String I18N_RESOURCE_NOT_FOUND = I18N_PREFIX + "resource.not.found"; // 资源未找到
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
    public static final String I18N_INTERNAL_SERVER_ERROR = I18N_PREFIX + "internal.server.error"; // 内部服务器错误
}
