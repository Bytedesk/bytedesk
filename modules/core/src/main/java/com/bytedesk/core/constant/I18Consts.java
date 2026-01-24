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

    private I18Consts() {
    }

    // 国际化常量定义
    public static final String I18N_PREFIX = "i18n.";
    public static final String I18N_DESCRIPTION_PREFIX = "i18n.description.";

    // 城市导入/初始化相关（后端返回 i18n key，前端通过 translateString 翻译）
    public static final String I18N_CITY_RESET_SUCCESS = I18N_PREFIX + "city.reset.success";
    public static final String I18N_CITY_INIT_SCHEDULED = I18N_PREFIX + "city.init.scheduled";
    public static final String I18N_CITY_INIT_SKIPPED = I18N_PREFIX + "city.init.skipped";

    // 角色描述（用于 RoleInitializer 默认角色 description，前端通过 translateString 翻译）
    public static final String I18N_ROLE_SUPER_DESCRIPTION = I18N_DESCRIPTION_PREFIX + "role.super";
    public static final String I18N_ROLE_ADMIN_DESCRIPTION = I18N_DESCRIPTION_PREFIX + "role.admin";
    public static final String I18N_ROLE_AGENT_DESCRIPTION = I18N_DESCRIPTION_PREFIX + "role.agent";
    public static final String I18N_ROLE_USER_DESCRIPTION = I18N_DESCRIPTION_PREFIX + "role.user";
    // 系统通知
    public static final String I18N_SYSTEM_NOTIFICATION_NAME = I18N_PREFIX + "system.notification"; // 系统通知
    public static final String I18N_REENTER_TIP = I18N_PREFIX + "reenter.tip"; // 重新进入提示
    public static final String I18N_LOGIN_REQUIRED = I18N_PREFIX + "login.required"; // 请先登录

    // 账号相关
    public static final String I18N_USERNAME_OR_PASSWORD_INCORRECT = "用户名或密码不正确"; // 用户名或密码不正确
    public static final String I18N_MOBILE_ALREADY_EXISTS = "手机号已存在"; // 手机号已存在
    public static final String I18N_EMAIL_ALREADY_EXISTS = "邮箱已存在"; // 邮箱已存在
    public static final String I18N_MOBILE_NOT_EXISTS = "手机号不存在"; // 手机号不存在
    public static final String I18N_EMAIL_NOT_EXISTS = "邮箱不存在"; // 邮箱不存在
    public static final String I18N_MOBILE_FORMAT_ERROR = "手机号格式错误"; // 手机号格式错误
    public static final String I18N_EMAIL_FORMAT_ERROR = "邮箱格式错误"; // 邮箱格式错误
    // 验证码相关
    public static final String I18N_AUTH_CAPTCHA_SEND_SUCCESS = "验证码发送成功"; // 验证码发送成功
    public static final String I18N_AUTH_CAPTCHA_ERROR = "验证码错误"; // 验证码错误
    public static final String I18N_AUTH_CAPTCHA_EXPIRED = "验证码已过期"; // 验证码已过期
    public static final String I18N_AUTH_CAPTCHA_ALREADY_SEND = "验证码已发送"; // 验证码已发送
    public static final String I18N_AUTH_CAPTCHA_SEND_TOO_FREQUENT = "验证码发送过于频繁"; // 验证码发送过于频繁
    public static final String I18N_AUTH_CAPTCHA_VALIDATE_FAILED = "验证码验证失败"; // 验证码验证失败
    // 助手相关
    public static final String I18N_FILE_ASSISTANT_NAME = "文件助手"; // 文件助手
    public static final String I18N_QUEUE_ASSISTANT_NAME = "排队助手"; // 排队助手
    public static final String I18N_CLIPBOARD_ASSISTANT_NAME = "剪贴板助手"; // 剪贴板助手
    public static final String I18N_INTENT_REWRITE_ASSISTANT_NAME = "意图改写"; // 意图改写
    public static final String I18N_INTENT_CLASSIFICATION_ASSISTANT_NAME = "意图识别"; // 意图识别
    public static final String I18N_EMOTION_ASSISTANT_NAME = "情绪分析"; // 情绪分析
    public static final String I18N_FILE_ASSISTANT_DESCRIPTION = "手机、电脑文件互传"; // 手机、电脑文件互传
    public static final String I18N_QUEUE_ASSISTANT_DESCRIPTION = "排队助手描述"; // 排队助手描述
    public static final String I18N_CLIPBOARD_ASSISTANT_DESCRIPTION = "手机、电脑剪贴板内容互传"; // 手机、电脑剪贴板内容互传
    public static final String I18N_INTENT_REWRITE_ASSISTANT_DESCRIPTION = "用于改写客户意图"; // 用于改写客户意图
    public static final String I18N_INTENT_CLASSIFICATION_ASSISTANT_DESCRIPTION = "用于识别客户意图"; // 用于识别客户意图
    public static final String I18N_EMOTION_ASSISTANT_DESCRIPTION = "用于分析客户情绪"; // 用于分析客户情绪
    // 系统通知
    // public static final String I18N_SYSTEM_NOTIFICATION_NAME = "系统通知"; // 系统通知
    public static final String I18N_SYSTEM_NOTIFICATION_DESCRIPTION = "系统通知消息"; // 系统通知消息
    // 用户相关
    public static final String I18N_USER_OLD_PASSWORD_WRONG = "旧密码错误"; // 旧密码错误
    // 内容类型
    public static final String I18N_THREAD_CONTENT_IMAGE = "图片"; // 图片
    public static final String I18N_THREAD_CONTENT_FILE = "文件"; // 文件
    public static final String I18N_THREAD_CONTENT_AUDIO = "语音"; // 语音
    public static final String I18N_THREAD_CONTENT_VIDEO = "视频"; // 视频
    // 提示信息
    public static final String I18N_WELCOME_TIP = "您好，请问有什么可以帮助您？"; // 欢迎提示
    public static final String I18N_TOP_TIP = "置顶提示"; // 置顶提示
    public static final String I18N_MESSAGE_LEAVE_TIP = "您好，请留言，我们会尽快回复您"; // 留言提示
    // public static final String I18N_REENTER_TIP = "继续会话"; // 重新进入提示
    public static final String I18N_QUEUE_TIP = "您好，客服繁忙，请稍后"; // 排队提示（旧版，保持兼容）
    public static final String I18N_QUEUE_MESSAGE_TEMPLATE = "您前面还有{0}人排队"; // 您前面还有{0}人排队（旧版）
    // 排队提示语模板（新版，支持模板变量）
    // 支持变量: {position}-排队位置, {queueSize}-队列总人数, {waitSeconds}-等待秒数, {waitMinutes}-等待分钟数, {waitTime}-格式化等待时间
    public static final String I18N_QUEUE_TIP_TEMPLATE = "您前面还有 {position} 人排队，预计等待 {waitTime}，请耐心等候。"; // 排队提示语模板
    // 即将接入提示语（当排队位置为0时使用）
    public static final String I18N_QUEUE_READY_TIP = "客服即将为您服务，请稍候..."; // 即将接入提示语
    public static final String I18N_AUTO_CLOSE_TIP = "会话已结束，感谢您的咨询，祝您生活愉快！"; // 会话已自动关闭
    public static final String I18N_AGENT_CLOSE_TIP = "会话已结束，感谢您的咨询，祝您生活愉快！"; // 客服已关闭会话
    public static final String I18N_AGENT_TRANSFER_TIP = "客服已将会话转接"; // 客服已将会话转接
    public static final String I18N_AGENT_TIMEOUT_TIP = "超时未回复，请尽快回复客户"; // 超时未回复
    // 评价提示
    public static final String I18N_INVITE_RATE_TIP = "邀请评价提示"; // 邀请评价提示
    // 统一
    public static final String I18N_UNIFIED_NICKNAME = "统一昵称"; // 统一昵称
    public static final String I18N_UNIFIED_DESCRIPTION = "统一描述"; // 统一描述
    // 工作组
    public static final String I18N_WORKGROUP_NICKNAME = "默认工作组"; // 工作组昵称
    public static final String I18N_WORKGROUP_BEFORE_NICKNAME = "售前工作组"; // 工作组前缀昵称
    public static final String I18N_WORKGROUP_AFTER_NICKNAME = "售后工作组"; // 工作组后缀昵称
    public static final String I18N_WORKGROUP_TICKET_NICKNAME = "工单工作组"; // 工单工作组昵称
    public static final String I18N_WORKGROUP_DESCRIPTION = "工作组描述"; // 工作组描述
    public static final String I18N_WORKGROUP_BEFORE_DESCRIPTION = "售前工作组描述"; // 工作组前缀描述
    public static final String I18N_WORKGROUP_AFTER_DESCRIPTION = "售后工作组描述"; // 工作组后缀描述
    public static final String I18N_WORKGROUP_TICKET_DESCRIPTION = "工单工作组描述"; // 工单工作组描述
    // 客服相关
    public static final String I18N_AGENT_NICKNAME = "客服昵称"; // 客服昵称
    public static final String I18N_AGENT_DESCRIPTION = "客服描述"; // 客服描述
    public static final String I18N_AGENT_OFFLINE = "客服离线"; // 客服离线
    public static final String I18N_AGENT_UNAVAILABLE = "客服不可用"; // 客服不可用
    public static final String I18N_AGENT_AVAILABLE = "客服可用"; // 客服可用
    // 用户相关
    public static final String I18N_USER_NICKNAME = "用户昵称"; // 用户昵称
    public static final String I18N_USER_DESCRIPTION = "用户描述"; // 用户描述
    public static final String I18N_USER_NOT_FOUND = "用户未找到"; // 用户未找到
    public static final String I18N_CREATE_FAILED = "创建失败"; // 创建失败
    public static final String I18N_UPDATE_FAILED = "更新失败"; // 更新失败
    // 其他
    public static final String I18N_DESCRIPTION = "描述"; // 描述
    public static final String I18N_TODO = "待办事项"; // 待办事项
    // 机器人相关
    public static final String I18N_ROBOT_NICKNAME = "默认机器人"; // 机器人昵称
    public static final String I18N_ROBOT_NAME = "默认机器人名称"; // 机器人名称
    public static final String I18N_ROBOT_DESCRIPTION = "默认机器人描述"; // 机器人描述
    public static final String I18N_ROBOT_AGENT_ASSISTANT_NICKNAME = "客服助理机器人昵称"; // 客服助理机器人昵称
    public static final String I18N_ROBOT_DEFAULT_REPLY = "抱歉，我暂时无法回答这个问题。"; // 机器人回复
    public static final String I18N_ROBOT_TO_AGENT_TIP = "正在为您转接人工客服，请稍候..."; // 机器人转人工提示
    // 角色相关
    public static final String I18N_ADMIN = "管理员"; // 管理员
    public static final String I18N_ADMIN_DESCRIPTION = "管理员描述"; // 管理员描述
    public static final String I18N_MEMBER = "成员"; // 成员
    public static final String I18N_MEMBER_DESCRIPTION = "成员描述"; // 成员描述
    // 快捷回复分类
    public static final String I18N_QUICK_REPLY_CATEGORY_CONTACT = "询问联系方式"; // 询问联系方式
    public static final String I18N_QUICK_REPLY_CATEGORY_THANKS = "感谢"; // 感谢
    public static final String I18N_QUICK_REPLY_CATEGORY_WELCOME = "问候"; // 问候
    public static final String I18N_QUICK_REPLY_CATEGORY_BYE = "告别"; // 告别
    // 快捷回复内容
    public static final String I18N_QUICK_REPLY_CONTACT_TITLE = "联系方式标题"; // 联系方式标题
    public static final String I18N_QUICK_REPLY_CONTACT_CONTENT = "联系方式内容"; // 联系方式内容
    public static final String I18N_QUICK_REPLY_THANKS_TITLE = "感谢标题"; // 感谢标题
    public static final String I18N_QUICK_REPLY_THANKS_CONTENT = "感谢内容"; // 感谢内容
    public static final String I18N_QUICK_REPLY_WELCOME_TITLE = "欢迎标题"; // 欢迎标题
    public static final String I18N_QUICK_REPLY_WELCOME_CONTENT = "欢迎内容"; // 欢迎内容
    public static final String I18N_QUICK_REPLY_BYE_TITLE = "再见标题"; // 再见标题
    public static final String I18N_QUICK_REPLY_BYE_CONTENT = "再见内容"; // 再见内容
    // API相关
    public static final String I18N_VIP_REST_API = "VIP REST API"; // VIP REST API
    // 初始化组织时使用
    public static final String I18N_FAQ_CATEGORY_DEMO_1 = "FAQ示例分类1"; // FAQ示例分类1
    public static final String I18N_FAQ_CATEGORY_DEMO_2 = "FAQ示例分类2"; // FAQ示例分类2
    // FAQ示例
    public static final String I18N_FAQ_DEMO_QUESTION_1 = "FAQ示例问题1"; // FAQ示例问题1
    public static final String I18N_FAQ_DEMO_ANSWER_1 = "FAQ示例答案1"; // FAQ示例答案1
    public static final String I18N_FAQ_DEMO_QUESTION_2 = "FAQ示例问题2"; // FAQ示例问题2
    public static final String I18N_FAQ_DEMO_ANSWER_2 = "FAQ示例答案2"; // FAQ示例答案2
    // 快捷按钮示例
    public static final String I18N_QUICK_BUTTON_DEMO_TITLE_1 = "快捷按钮示例标题1"; // 快捷按钮示例标题1
    public static final String I18N_QUICK_BUTTON_DEMO_CONTENT_1 = "快捷按钮示例内容1"; // 快捷按钮示例内容1
    public static final String I18N_QUICK_BUTTON_DEMO_TITLE_2 = "快捷按钮示例标题2"; // 快捷按钮示例标题2
    public static final String I18N_QUICK_BUTTON_DEMO_CONTENT_2 = "快捷按钮示例内容2"; // 快捷按钮示例内容2
    // 群组相关
    public static final String I18N_GROUP_NAME = "群组名称"; // 群组名称
    public static final String I18N_GROUP_DESCRIPTION = "群组描述"; // 群组描述
    // 工单相关
    public static final String I18N_TICKET_SETTINGS_EXTERNAL_NAME = "默认外部工单配置"; // 外部工单配置名称
    public static final String I18N_TICKET_SETTINGS_EXTERNAL_DESCRIPTION = "系统默认外部工单配置"; // 外部工单配置描述
    public static final String I18N_TICKET_SETTINGS_INTERNAL_NAME = "默认内部工单配置"; // 内部工单配置名称
    public static final String I18N_TICKET_SETTINGS_INTERNAL_DESCRIPTION = "系统默认内部工单配置"; // 内部工单配置描述

    // 工单提示语默认文案（TicketBasicSettingsEntity 默认值）
    public static final String I18N_TICKET_ACCESS_TIP = "您好，工单已接入，我们将尽快为您处理。"; // 工单接入提示语
    public static final String I18N_TICKET_CLOSE_TIP = "本工单会话已关闭，如需继续请提交新工单。"; // 工单关闭提示语
    public static final String I18N_TICKET_AGENT_TIMEOUT_TIP = "客服暂时未回复，我们正在处理中，请您稍候。"; // 工单客服超时未回复提示语
    public static final String I18N_TICKET_VISITOR_TIMEOUT_TIP = "如您暂时不方便回复，后续可随时继续补充信息。"; // 工单访客超时未回复提示语
    // 通知相关
    public static final String I18N_NOTICE_TITLE = "通知标题"; // 通知标题
    public static final String I18N_NOTICE_TYPE = "通知类型"; // 通知类型
    public static final String I18N_NOTICE_CONTENT = "通知内容"; // 通知内容
    public static final String I18N_NOTICE_IP = "通知IP"; // 通知IP
    public static final String I18N_NOTICE_IP_LOCATION = "通知IP位置"; // 通知IP位置
    public static final String I18N_NOTICE_URL = "通知URL"; // 通知URL
    public static final String I18N_NOTICE_EXTRA = "通知额外信息"; // 通知额外信息
    // 文件解析
    public static final String I18N_NOTICE_PARSE_FILE_SUCCESS = "文件解析成功"; // 文件解析成功
    public static final String I18N_NOTICE_PARSE_FILE_ERROR = "文件解析错误"; // 文件解析错误
    // 会话状态,  用于更新thread.content
    public static final String I18N_AUTO_CLOSED = "已自动关闭"; // 已自动关闭
    public static final String I18N_AGENT_CLOSED = "已被客服关闭"; // 已被客服关闭
    public static final String I18N_AGENT_TRANSFER = "已被客服转接"; // 已被客服转接
    public static final String I18N_AGENT_TIMEOUT = "超时未回复"; // 超时未回复
    // 黑名单
    public static final String I18N_BLACK_USER_ALREADY_EXISTS = "黑名单用户已存在"; // 黑名单用户已存在
    // 输入状态
    public static final String I18N_TYPING = "正在输入"; // 正在输入

    // GlobalControllerAdvice 错误国际化常量
    public static final String I18N_USER_SIGNUP_FIRST = "请先注册用户"; // 请先注册用户
    public static final String I18N_EMAIL_SIGNUP_FIRST = "请先使用邮箱注册"; // 请先使用邮箱注册
    public static final String I18N_MOBILE_SIGNUP_FIRST = "请先使用手机号注册"; // 请先使用手机号注册
    public static final String I18N_RESOURCE_NOT_FOUND = "资源未找到"; // 资源未找到
    // public static final String I18N_NOT_LOGIN = "请先登录"; // 请先登录
    public static final String I18N_USER_DISABLED = "用户已被禁用"; // 用户已被禁用
    public static final String I18N_FORBIDDEN_ACCESS = "禁止访问"; // 禁止访问
    public static final String I18N_USER_BLOCKED = "用户已被封禁"; // 用户已被封禁
    public static final String I18N_SENSITIVE_CONTENT = "包含敏感内容"; // 包含敏感内容
    public static final String I18N_MESSAGE_PROCESSING_FAILED = "消息处理失败"; // 消息处理失败
    public static final String I18N_NULL_POINTER_EXCEPTION = "空指针异常"; // 空指针异常
    public static final String I18N_RESPONSE_STATUS_EXCEPTION = "响应状态异常"; // 响应状态异常
    public static final String I18N_WEBSOCKET_TIMEOUT_EXCEPTION = "WebSocket超时异常"; // WebSocket超时异常
    public static final String I18N_HTTP_METHOD_NOT_SUPPORTED = "不支持的HTTP请求方法"; // 不支持的HTTP请求方法
    public static final String I18N_AUTHORIZATION_DENIED = "授权拒绝"; // 授权拒绝
    public static final String I18N_REQUEST_REJECTED = "请求被拒绝"; // 请求被拒绝
    public static final String I18N_ENTITY_NOT_FOUND = "实体未找到"; // 实体未找到
    public static final String I18N_INVALID_REQUEST_BODY = "请求体格式错误"; // 请求体格式错误（JSON 不合法/不完整）
    public static final String I18N_OPERATION_NOT_SUPPORTED = "不支持的操作"; // 不支持的操作/功能暂未实现
    public static final String I18N_INTERNAL_SERVER_ERROR = "内部服务器错误"; // 内部服务器错误

    public static final String I18N_NO_ANSWER = "您的这个问题暂时无法回答，请提问其他问题。";
    public static final String I18N_CANT_ANSWER = "您的这个问题我不能回答，请提问其他问题。"; 

    public static final String I18N_NOT_AUTHORIZED = "您没有权限访问此资源"; // 您没有权限访问此资源
    public static final String I18N_SERVICE_TEMPORARILY_UNAVAILABLE = "请首先在管理后台配置大模型apiUrl和apiKey。如果已经配置，请检查大模型提供商账号是否欠费。"; // 服务暂时不可用，请稍后重试
    public static final String I18N_LLM_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体"; // 大模型配置提示
    public static final String I18N_LLM_THREAD_INTENTION_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》意图识别"; // 会话意图识别大模型配置提示
    public static final String I18N_LLM_THREAD_EMOTION_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》情绪分析"; // 情绪分析大模型配置提示
    public static final String I18N_LLM_THREAD_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》会话总结"; // 工单大模型配置提示
    public static final String I18N_LLM_TICKET_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》工单生成"; // 工单大模型配置提示
    public static final String I18N_LLM_AGENT_INSPECTION_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》客服质检"; // 客服质检大模型配置提示
    public static final String I18N_FAQ_SIMILAR_QUESTIONS_CONFIG_TIP = "请首先在管理后台配置大模型apiUrl和apiKey，修改：智能助手-》智能体-》FAQ相似问题 faq_similar_questions 提示词 大模型"; // FAQ相似问题配置提示
    
    // AI 服务相关常量
    public static final String I18N_THINKING = "正在思考中..."; // 正在思考中...
    public static final String I18N_SORRY_LLM_DISABLED = "抱歉，大模型功能未启用"; // 抱歉，大模型功能未启用
    public static final String I18N_SORRY_SERVICE_UNAVAILABLE = "抱歉，服务暂时不可用，请稍后再试。"; // 抱歉，服务暂时不可用，请稍后再试。
    public static final String I18N_CONTEXT_BASED_ANSWER = "请根据以下上下文回答问题：\n\n"; // 请根据以下上下文回答问题：
    public static final String I18N_CONTEXT_LABEL = "上下文：\n"; // 上下文：
    public static final String I18N_QUESTION_LABEL = "问题：\n"; // 问题：
    public static final String I18N_SEARCH_RESULT_PREFIX = "搜索结果: "; // 搜索结果: 
    public static final String I18N_SYSTEM_PREFIX = "[系统] "; // [系统] 
    public static final String I18N_USER_PREFIX = "[用户] "; // [用户] 
    public static final String I18N_ASSISTANT_PREFIX = "[助手] "; // [助手] 
    public static final String I18N_DEFAULT_SYSTEM_PROMPT = "你是一个智能助手,请根据用户的问题提供有帮助的回答。"; // 默认系统提示词

    // 线程路由策略相关常量
    /** 默认欢迎消息 */
    public static final String I18N_DEFAULT_WELCOME_MESSAGE = "您好，请问有什么可以帮助您？";
    
    /** 默认离线消息 */
    public static final String I18N_DEFAULT_OFFLINE_MESSAGE = "您好，请留言，我们会尽快回复您";
    
    /** 排队等待消息 - 下一个 */
    public static final String I18N_QUEUE_NEXT_MESSAGE = "请稍后，下一个就是您";
    
    /** 排队等待消息模板 */
    public static final String I18N_QUEUE_WAITING_MESSAGE_TEMPLATE = "当前排队人数：%d 大约等待时间：%d 分钟";

    // "验证码发送过于频繁"
    public static final String I18N_CAPTCHA_SEND_TOO_FREQUENT = "验证码发送过于频繁，请稍后再试"; // 验证码发送过于频繁，请稍后再试

    // "验证码已经发送，请勿重复发送"
    public static final String I18N_CAPTCHA_ALREADY_SENT = "验证码已经发送，请勿重复发送"; // 验证码已经发送，请勿重复发送

    // "不支持的发送类型"
    public static final String I18N_CAPTCHA_UNSUPPORTED_TYPE = "不支持的发送类型"; // 不支持的发送类型

    // "处理请求时发生错误，请稍后重试"
    public static final String I18N_ROBOT_PROCESSING_ERROR = "处理请求时发生错误，请稍后重试"; // 处理请求时发生错误，请稍后重试

    // 消息类型国际化常量
    public static final String I18N_MESSAGE_TYPE_WELCOME = "欢迎消息"; // 欢迎消息
    public static final String I18N_MESSAGE_TYPE_CONTINUE = "继续消息"; // 继续消息
    public static final String I18N_MESSAGE_TYPE_SYSTEM = "系统消息"; // 系统消息
    public static final String I18N_MESSAGE_TYPE_DOCUMENT = "文档消息"; // 文档消息
    public static final String I18N_MESSAGE_TYPE_QUEUE = "排队消息"; // 排队消息
    public static final String I18N_MESSAGE_TYPE_QUEUE_NOTICE = "排队通知"; // 排队通知
    public static final String I18N_MESSAGE_TYPE_QUEUE_UPDATE = "排队更新"; // 排队更新
    public static final String I18N_MESSAGE_TYPE_QUEUE_ACCEPT = "排队接受"; // 排队接受
    public static final String I18N_MESSAGE_TYPE_QUEUE_TIMEOUT = "排队超时"; // 排队超时
    public static final String I18N_MESSAGE_TYPE_QUEUE_CANCEL = "排队取消"; // 排队取消
    public static final String I18N_MESSAGE_TYPE_NOTICE = "通知消息"; // 通知消息
    public static final String I18N_MESSAGE_TYPE_TEXT = "文本消息"; // 文本消息
    public static final String I18N_MESSAGE_TYPE_IMAGE = "图片消息"; // 图片消息
    public static final String I18N_MESSAGE_TYPE_FILE = "文件消息"; // 文件消息
    public static final String I18N_MESSAGE_TYPE_EXTRA = "附加信息"; // 附加信息
    public static final String I18N_MESSAGE_TYPE_AUDIO = "语音消息"; // 语音消息
    public static final String I18N_MESSAGE_TYPE_VOICE = "语音通话"; // 语音通话
    public static final String I18N_MESSAGE_TYPE_VIDEO = "视频消息"; // 视频消息
    public static final String I18N_MESSAGE_TYPE_MUSIC = "音乐消息"; // 音乐消息
    public static final String I18N_MESSAGE_TYPE_LOCATION = "位置消息"; // 位置消息
    public static final String I18N_MESSAGE_TYPE_LINK = "链接消息"; // 链接消息
    public static final String I18N_MESSAGE_TYPE_GOODS = "商品消息"; // 商品消息
    public static final String I18N_MESSAGE_TYPE_CARD = "卡片消息"; // 卡片消息
    public static final String I18N_MESSAGE_TYPE_EVENT = "事件消息"; // 事件消息
    public static final String I18N_MESSAGE_TYPE_GUESS = "猜你想问"; // 猜你想问
    public static final String I18N_MESSAGE_TYPE_HOT = "热门话题"; // 热门话题
    public static final String I18N_MESSAGE_TYPE_SHORTCUT = "快捷路径"; // 快捷路径
    public static final String I18N_MESSAGE_TYPE_ORDER = "订单消息"; // 订单消息
    public static final String I18N_MESSAGE_TYPE_POLL = "投票消息"; // 投票消息
    public static final String I18N_MESSAGE_TYPE_POLL_SUBMIT = "投票提交"; // 投票提交
    public static final String I18N_MESSAGE_TYPE_FORM = "表单消息"; // 表单消息
    public static final String I18N_MESSAGE_TYPE_FORM_SUBMIT = "表单提交"; // 表单提交
    public static final String I18N_MESSAGE_TYPE_CHOICE = "选项消息"; // 选项消息
    public static final String I18N_MESSAGE_TYPE_CHOICE_SUBMIT = "选项提交"; // 选项提交
    public static final String I18N_MESSAGE_TYPE_CONFIRM = "确认消息"; // 确认消息
    public static final String I18N_MESSAGE_TYPE_CONFIRM_SUBMIT = "确认提交"; // 确认提交
    public static final String I18N_MESSAGE_TYPE_LEAVE_MSG = "留言消息"; // 留言消息
    public static final String I18N_MESSAGE_TYPE_LEAVE_MSG_SUBMIT = "留言提交"; // 留言提交
    public static final String I18N_MESSAGE_TYPE_LEAVE_MSG_REPLIED = "留言回复"; // 留言回复
    public static final String I18N_MESSAGE_TYPE_CUSTOMER_SUBMIT = "客户留资提交"; // 客户留资提交
    public static final String I18N_MESSAGE_TYPE_SYSTEM_ALARM = "系统报警"; // 系统报警
    public static final String I18N_MESSAGE_TYPE_TICKET = "工单消息"; // 工单消息
    public static final String I18N_MESSAGE_TYPE_TICKET_SUBMIT = "工单提交"; // 工单提交
    public static final String I18N_MESSAGE_TYPE_TYPING = "正在输入"; // 正在输入
    public static final String I18N_MESSAGE_TYPE_PROCESSING = "处理中"; // 处理中
    public static final String I18N_MESSAGE_TYPE_STICKER = "贴纸消息"; // 贴纸消息
    public static final String I18N_MESSAGE_TYPE_EMAIL = "邮件消息"; // 邮件消息
    public static final String I18N_MESSAGE_TYPE_BUTTON = "按钮消息"; // 按钮消息
    public static final String I18N_MESSAGE_TYPE_BUTTON_SUBMIT = "按钮提交"; // 按钮提交
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM = "流式消息"; // 流式消息
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_START = "流式开始"; // 流式开始
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_END = "流式结束"; // 流式结束
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_CANCEL = "流式取消"; // 流式取消
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_UNANSWERED = "流式未回答"; // 流式未回答
    public static final String I18N_MESSAGE_TYPE_ROBOT_STREAM_ERROR = "流式错误"; // 流式错误
    public static final String I18N_MESSAGE_TYPE_PREVIEW = "消息预览"; // 消息预览
    public static final String I18N_MESSAGE_TYPE_RECALL = "消息撤回"; // 消息撤回
    public static final String I18N_MESSAGE_TYPE_DELIVERED = "已送达"; // 已送达
    public static final String I18N_MESSAGE_TYPE_READ = "已读"; // 已读
    public static final String I18N_MESSAGE_TYPE_QUOTATION = "引用消息"; // 引用消息
    public static final String I18N_MESSAGE_TYPE_KICKOFF = "踢出"; // 踢出
    public static final String I18N_MESSAGE_TYPE_SHAKE = "窗口抖动"; // 窗口抖动
    public static final String I18N_MESSAGE_TYPE_FAQ = "常见问题"; // 常见问题
    public static final String I18N_MESSAGE_TYPE_FAQ_QUESTION = "常见问题问题"; // 常见问题问题
    public static final String I18N_MESSAGE_TYPE_FAQ_ANSWER = "常见问题答案"; // 常见问题答案
    public static final String I18N_MESSAGE_TYPE_ROBOT = "机器人回答"; // 机器人回答
    public static final String I18N_MESSAGE_TYPE_ROBOT_CANCEL = "机器人回答取消"; // 机器人回答取消
    public static final String I18N_MESSAGE_TYPE_ROBOT_UNANSWERED = "机器人未回答"; // 机器人未回答
    public static final String I18N_MESSAGE_TYPE_ROBOT_ERROR = "机器人错误"; // 机器人错误
    public static final String I18N_MESSAGE_TYPE_ROBOT_UP = "机器人点赞"; // 机器人点赞
    public static final String I18N_MESSAGE_TYPE_ROBOT_DOWN = "机器人点踩"; // 机器人点踩
    public static final String I18N_MESSAGE_TYPE_RATE = "评价"; // 评价
    public static final String I18N_MESSAGE_TYPE_RATE_INVITE = "邀请评价"; // 邀请评价
    public static final String I18N_MESSAGE_TYPE_RATE_SUBMIT = "评价提交"; // 评价提交
    public static final String I18N_MESSAGE_TYPE_RATE_CANCEL = "评价取消"; // 评价取消
    public static final String I18N_MESSAGE_TYPE_AUTO_CLOSED = "自动关闭"; // 自动关闭
    public static final String I18N_MESSAGE_TYPE_AGENT_CLOSED = "客服关闭"; // 客服关闭
    public static final String I18N_MESSAGE_TYPE_TRANSFER = "转接"; // 转接
    public static final String I18N_MESSAGE_TYPE_TRANSFER_REJECT = "拒绝转接"; // 拒绝转接
    public static final String I18N_MESSAGE_TYPE_TRANSFER_ACCEPT = "接受转接"; // 接受转接
    public static final String I18N_MESSAGE_TYPE_TRANSFER_TIMEOUT = "转接超时"; // 转接超时
    public static final String I18N_MESSAGE_TYPE_TRANSFER_CANCEL = "取消转接"; // 取消转接
    public static final String I18N_MESSAGE_TYPE_INVITE = "邀请"; // 邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_REJECT = "拒绝邀请"; // 拒绝邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_ACCEPT = "接受邀请"; // 接受邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_TIMEOUT = "邀请超时"; // 邀请超时
    public static final String I18N_MESSAGE_TYPE_INVITE_CANCEL = "取消邀请"; // 取消邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_EXIT = "邀请退出"; // 邀请退出
    public static final String I18N_MESSAGE_TYPE_INVITE_REMOVE = "邀请移除"; // 邀请移除
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR = "邀请访客"; // 邀请访客
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR_REJECT = "访客拒绝邀请"; // 访客拒绝邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR_ACCEPT = "访客接受邀请"; // 访客接受邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR_TIMEOUT = "访客邀请超时"; // 访客邀请超时
    public static final String I18N_MESSAGE_TYPE_INVITE_VISITOR_CANCEL = "访客邀请取消"; // 访客邀请取消
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP = "邀请群组"; // 邀请群组
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP_REJECT = "群组拒绝邀请"; // 群组拒绝邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP_ACCEPT = "群组接受邀请"; // 群组接受邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP_TIMEOUT = "群组邀请超时"; // 群组邀请超时
    public static final String I18N_MESSAGE_TYPE_INVITE_GROUP_CANCEL = "群组邀请取消"; // 群组邀请取消
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE = "邀请知识库"; // 邀请知识库
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE_REJECT = "知识库拒绝邀请"; // 知识库拒绝邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE_ACCEPT = "知识库接受邀请"; // 知识库接受邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE_TIMEOUT = "知识库邀请超时"; // 知识库邀请超时
    public static final String I18N_MESSAGE_TYPE_INVITE_KBASE_CANCEL = "知识库邀请取消"; // 知识库邀请取消
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION = "邀请组织"; // 邀请组织
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_REJECT = "组织拒绝邀请"; // 组织拒绝邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_ACCEPT = "组织接受邀请"; // 组织接受邀请
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_TIMEOUT = "组织邀请超时"; // 组织邀请超时
    public static final String I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_CANCEL = "组织邀请取消"; // 组织邀请取消
    public static final String I18N_MESSAGE_TYPE_ARTICLE = "文章"; // 文章
    public static final String I18N_MESSAGE_TYPE_WEBRTC_AUDIO_INVITE = "音频通话邀请"; // 音频通话邀请
    public static final String I18N_MESSAGE_TYPE_WEBRTC_VIDEO_INVITE = "视频通话邀请"; // 视频通话邀请
    public static final String I18N_MESSAGE_TYPE_WEBRTC_AUDIO_INVITE_REJECT = "音频通话拒绝"; // 音频通话拒绝
    public static final String I18N_MESSAGE_TYPE_WEBRTC_VIDEO_INVITE_REJECT = "视频通话拒绝"; // 视频通话拒绝
    public static final String I18N_MESSAGE_TYPE_WEBRTC_AUDIO_INVITE_ACCEPT = "音频通话接受"; // 音频通话接受
    public static final String I18N_MESSAGE_TYPE_WEBRTC_VIDEO_INVITE_ACCEPT = "视频通话接受"; // 视频通话接受
    public static final String I18N_MESSAGE_TYPE_WEBRTC_AUDIO_INVITE_CANCEL = "音频通话取消"; // 音频通话取消
    public static final String I18N_MESSAGE_TYPE_WEBRTC_VIDEO_INVITE_CANCEL = "视频通话取消"; // 视频通话取消
    public static final String I18N_MESSAGE_TYPE_GROUP_CREATE = "创建群组"; // 创建群组
    public static final String I18N_MESSAGE_TYPE_GROUP_INVITE = "邀请入群"; // 邀请入群
    public static final String I18N_MESSAGE_TYPE_GROUP_DISMISS = "解散群组"; // 解散群组
    public static final String I18N_MESSAGE_TYPE_NOTIFICATION_AGENT_REPLY_TIMEOUT = "客服回复超时提醒"; // 客服回复超时提醒
    public static final String I18N_MESSAGE_TYPE_NOTIFICATION_RATE_SUBMITTED = "访客评价提交提醒"; // 访客评价提交提醒
    public static final String I18N_MESSAGE_TYPE_ERROR = "错误消息"; // 错误消息
    public static final String I18N_MESSAGE_TYPE_UNKNOWN = "未知类型"; // 未知类型
    // 补充的消息类型
    public static final String I18N_MESSAGE_TYPE_URL = "网址消息"; // 网址消息
    public static final String I18N_MESSAGE_TYPE_PHONE_NUMBER = "电话号码"; // 电话号码
    public static final String I18N_MESSAGE_TYPE_EMAIL_ADDRESS = "邮箱地址"; // 邮箱地址
    public static final String I18N_MESSAGE_TYPE_WECHAT_NUMBER = "微信号"; // 微信号
    public static final String I18N_MESSAGE_TYPE_BLOG = "博客"; // 博客

}
