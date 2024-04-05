//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/21.
//

import Foundation

// 默认json格式
let BD_MQTT_TOPIC_MESSAGE               = "message/mqtt"
let BD_MQTT_TOPIC_RECEIPT               = "receipt/mqtt"
let BD_MQTT_TOPIC_RECALL                = "recall/mqtt"
let BD_MQTT_TOPIC_WEBRTC                = "webrtc/mqtt"

// 发送protobuf格式: TODO
let BD_MQTT_TOPIC_MESSAGE_PROTOBUF      = "protobuf/message/mqtt"

let BD_MQTT_TOPIC_STATUS                = "status/mqtt"
let BD_MQTT_TOPIC_LASTWILL              = "lastWill/mqtt"
let BD_MQTT_TOPIC_PROTOBUF_PREFIX       = "protobuf/"

// 测试域名
// web后台此域名非统一的，而是每一家使用自己独特的二级域名
//let BD_WEBRTC_STUN_SERVER               = "stun:47.99.38.99:3478"
//let BD_IS_DEBUG                         true
//let BD_MQTT_HOST                        = "127.0.0.1"
//let BD_HTTP_HOST_ADDRESS                = "http://127.0.0.1:8000"
//let HTTP_VISITOR_API_BASE_URL           = "http://127.0.0.1:8000/visitor/api"
//let HTTP_API_BASE_URL                   = "http://127.0.0.1:8000/api"
//let CLIENT_OAUTH_TOKEN                  = "http://127.0.0.1:8000/oauth/token"

// 上线发布域名
//let BD_WEBRTC_STUN_SERVER               = "stun:47.99.38.99:3478"
//let BD_IS_DEBUG                         false
//let BD_MQTT_HOST                        = "mq.bytedesk.com"
//let BD_HTTP_HOST_ADDRESS                = "https://api.bytedesk.com"
//let HTTP_VISITOR_API_BASE_URL           = "https://api.bytedesk.com/visitor/api"
//let HTTP_API_BASE_URL                   = "https://api.bytedesk.com/api"
//let CLIENT_OAUTH_TOKEN                  = "https://api.bytedesk.com/oauth/token"

// 加载聊天记录
let BD_GET_MESSAGE_TYPE_WORKGROUP            = "workgroup"  // 加载工作组会话
let BD_GET_MESSAGE_TYPE_APPOINTED            = "appointed"  // 指定坐席
let BD_GET_MESSAGE_TYPE_USER                 = "user"  // 加载所有访客会话
let BD_GET_MESSAGE_TYPE_THREAD               = "thread"  // 加载thread会话
let BD_GET_MESSAGE_TYPE_CONTACT              = "contact" // 一对一
let BD_GET_MESSAGE_TYPE_GROUP                = "group"   // 群组

let BD_THREAD_REQUEST_TYPE_WORK_GROUP       = "workgroup"
let BD_THREAD_REQUEST_TYPE_APPOINTED        = "appointed"

// 会话类型
//let BD_THREAD_TYPE_THREAD               = "thread"  // 客服会话
let BD_THREAD_TYPE_WORKGROUP            = "workgroup" // 工作组会话
let BD_THREAD_TYPE_APPOINTED            = "appointed" // 指定坐席
let BD_THREAD_TYPE_CONTACT              = "contact" // 一对一
let BD_THREAD_TYPE_GROUP                = "group"   // 群组
let BD_THREAD_TYPE_ROBOT                = "robot" // 机器人会话
let BD_THREAD_TYPE_ROBOT_TO_AGENT       = "robot_to_agent" // 机器转人工
let BD_THREAD_TYPE_TICKET_WORKGROUP     = "ticket_workgroup" // 技能组工单
let BD_THREAD_TYPE_TICKET_APPOINTED     = "ticket_appointed" // 一对一工单

// 消息会话类型
//let BD_MESSAGE_SESSION_TYPE_THREAD      = "thread"  // 访客会话
let BD_MESSAGE_SESSION_TYPE_WORKGROUP   = "workgroup" // 工作组会话
let BD_MESSAGE_SESSION_TYPE_APPOINTED   = "appointed" // 指定坐席
let BD_MESSAGE_SESSION_TYPE_CONTACT     = "contact" // 一对一
let BD_MESSAGE_SESSION_TYPE_GROUP       = "group"   // 群组
let BD_MESSAGE_SESSION_TYPE_ROBOT       = "robot" // 机器人会话

let CLIENT_SWIFT                         = "ios_swift"
// passport 授权访客端参数
let CLIENT_ID_VISITOR                   = "ios"
let CLIENT_SECRET_VISITOR               = "XSf9jKCAPpeMwDZakt8AkvKppHEmXAb5sX0FtXwn"
// passport 授权客服端参数
let CLIENT_ID_ADMIN                     = "ios"
let CLIENT_SECRET_ADMIN                 = "XSf9jKCAPpeMwDZakt8AkvKppHEmXAb5sX0FtXwn"
//
let kTimeOutInterval                    = 60

// 角色类型
let BD_ROLE_VISITOR                     = "visitor" // 访客
let BD_ROLE_ADMIN                       = "admin"   // 管理员
let BD_ROLE_COMPANY                     = "company"  // 企业
let BD_ROLE_PROXY                       = "proxy"  // 代理
let BD_ROLE_AGENT                       = "agent" // 客服人员
let BD_ROLE_AGENT_ADMIN                 = "agent_admin" // 客服组长
let BD_ROLE_CHECKER                     = "checker" // 质检人员
let BD_ROLE_CHECKER_ADMIN               = "checker_admin" // 质检组长
let BD_ROLE_WECHAT                      = "wechat" // 微信
let BD_ROLE_MINI_PROGRAM                = "mini_program" // 微信小程序
let BD_ROLE_WORKGROUP                   = "workgroup" // 工作组
let BD_ROLE_ROBOT                       = "robot" // 机器人

// 访客端
let BD_CLIENT_WEB                       = "web"   // 访客pc网站
let BD_CLIENT_WAP                       = "wap"    // 访客手机网站
let BD_CLIENT_ANDROID                   = "android"  // 访客安卓
let BD_CLIENT_IOS                       = "ios"         // 访客苹果
let BD_CLIENT_WECHAT_MINI               = "wechat_mini"  // 访客小程序
let BD_CLIENT_WECHAT_MP                 = "wechat_mp"    // 访客微信客服接口
let BD_CLIENT_WECHAT_URL                = "wechat_url"   // 访客微信自定义菜单

// 客服端
let BD_CLIENT_WINDOW_ADMIN              = "window_admin"     // Windwow客服端
let BD_CLIENT_MAC_ADMIN                 = "mac_admin"           // MAC客服端
let BD_CLIENT_ANDROID_ADMIN             = "android_admin"   // 安卓手机客服端
let BD_CLIENT_IOS_ADMIN                 = "ios_admin"           // 苹果手机客服端
let BD_CLIENT_WEB_ADMIN                 = "web_admin"           // web客服端
let BD_CLIENT_WECHAT_MINI_ADMIN         = "wechat_mini_admin"   // 小程序客服端
//let BD_CLIENT_POMELO_ADMIN              = "pomelo_admin"     // Pomelo服务器
let BD_CLIENT_SYSTEM                    = "system"           // 系统端

let BD_ERROR_WITH_DOMAIN                = "error.ios.bytedesk.com"

let BD_NOTIFICATION_OAUTH_RESULT        = "bd_notification_oauth_result"

let BD_NOTIFICATION_INIT_STATUS         = "bd_notification_init_status"
let BD_NOTIFICATION_INIT_STATUS_LOADING = "bd_notification_init_status_loading"
let BD_NOTIFICATION_INIT_STATUS_LOADED  = "bd_notification_init_status_loaded"
let BD_NOTIFICATION_INIT_STATUS_ERROR   = "bd_notification_init_status_error"

//
let BD_NOTIFICATION_GROUP_UPDATE        = "bd_notification_group_update"
let BD_NOTIFICATION_CONTACT_UPDATE      = "bd_notification_contact_update"
let BD_NOTIFICATION_PROFILE_UPDATE      = "bd_notification_profile_update"

// 账号异地登录
let BD_NOTIFICATION_KICKOFF             = "bd_notification_kickoff"
let BD_NOTIFICATION_OUTOFDATE           = "bd_notification_outofdate"
let BD_NOTIFICATION_TRANSFER            = "bd_notification_transfer"
let BD_NOTIFICATION_TRANSFER_ACCEPT     = "bd_notification_transfer_accept"
let BD_NOTIFICATION_TRANSFER_REJECT     = "bd_notification_transfer_reject"

// 通知UI thread状态
let BD_NOTIFICATION_THREAD              = "bd_notification_thread"
let BD_NOTIFICATION_THREAD_ADD          = "bd_notification_thread_add"
let BD_NOTIFICATION_THREAD_UPDATE       = "bd_notification_thread_update"
let BD_NOTIFICATION_THREAD_DELETE       = "bd_notification_thread_delete"
let BD_NOTIFICATION_THREAD_CLOSE        = "bd_notification_thread_close"

// 通知UI queue状态
let BD_NOTIFICATION_QUEUE               = "bd_notification_queue"
let BD_NOTIFICATION_QUEUE_ADD           = "bd_notification_queue_add"
let BD_NOTIFICATION_QUEUE_UPDATE        = "bd_notification_queue_update"
let BD_NOTIFICATION_QUEUE_DELETE        = "bd_notification_queue_delete"
let BD_NOTIFICATION_QUEUE_ACCEPT        = "bd_notification_queue_accept"

// 通知UI message状态
let BD_NOTIFICATION_MESSAGE_LOCALID     = "bd_notification_message_localid"
let BD_NOTIFICATION_MESSAGE_ADD         = "bd_notification_message_add"
let BD_NOTIFICATION_MESSAGE_DELETE      = "bd_notification_message_delete"
let BD_NOTIFICATION_MESSAGE_RECALL     = "bd_notification_message_recall"
let BD_NOTIFICATION_MESSAGE_STATUS      = "bd_notification_message_status"
let BD_NOTIFICATION_MESSAGE_PREVIEW     = "bd_notification_message_preview"

// 通知连接状态
let BD_NOTIFICATION_CONNECTION_STATUS               = "notification_connection_status"

// 通知webrtc
let BD_NOTIFICATION_WEBRTC_MESSAGE       = "bd_notification_webrtc_message"
// 接受webrtc邀请
let BD_NOTIFICATION_WEBRTC_INVITE       = "bd_notification_webrtc_invite"
// webrtc取消邀请
let BD_NOTIFICATION_WEBRTC_CANCEL       = "bd_notification_webrtc_cancel"
// webrtc邀请视频会话
let BD_NOTIFICATION_WEBRTC_OFFER_VIDEO  = "bd_notification_webrtc_offer_video"
// webrtc邀请音频会话
let BD_NOTIFICATION_WEBRTC_OFFER_AUDIO  = "bd_notification_webrtc_offer_audio"
// 接受webrtc邀请
let BD_NOTIFICATION_WEBRTC_ANSWER       = "bd_notification_webrtc_answer"
// webrtc candidate信息
let BD_NOTIFICATION_WEBRTC_CANDIDATE    = "bd_notification_webrtc_candidate"
// 接受webrtc邀请
let BD_NOTIFICATION_WEBRTC_ACCEPT       = "bd_notification_webrtc_accept"
// 拒绝webrtc邀请
let BD_NOTIFICATION_WEBRTC_REJECT       = "bd_notification_webrtc_reject"
// 被邀请方视频设备 + peeConnection已经就绪
let BD_NOTIFICATION_WEBRTC_READY        = "bd_notification_webrtc_ready"
// webrtc忙线
let BD_NOTIFICATION_WEBRTC_BUSY         = "bd_notification_webrtc_busy"
// 结束webrtc会话
let BD_NOTIFICATION_WEBRTC_CLOSE        = "bd_notification_webrtc_close"
// 微信操作开始
let BD_NOTIFICATION_WECHAT_START        = "bd_notification_wechat_start"
// 微信操作成功
let BD_NOTIFICATION_WECHAT_SUCCESS      = "bd_notification_wechat_success"
// 微信操作失败
let BD_NOTIFICATION_WECHAT_ERROR        = "bd_notification_wechat_error"

/**
 * 用户在线状态：
 */
let BD_USER_STATUS_CONNECTING           = "connecting" //开始服务器建立长连接
let BD_USER_STATUS_CONNECTED            = "connected" //跟服务器建立长连接
let BD_USER_STATUS_DISCONNECTED         = "disconnected" //断开长连接
let BD_USER_STATUS_CONNECT_ERROR        = "connect_error" //断开长连接

let BD_USER_STATUS_ONLINE               = "online" //在线状态
let BD_USER_STATUS_OFFLINE              = "offline" //离线状态
let BD_USER_STATUS_BUSY                 = "busy" //忙
let BD_USER_STATUS_AWAY                 = "away" //离开
let BD_USER_STATUS_LOGOUT               = "logout" //登出
let BD_USER_STATUS_LOGIN                = "login" //登录
let BD_USER_STATUS_LEAVE                = "leave" //离开
let BD_USER_STATUS_AFTER                = "after" //话后
let BD_USER_STATUS_EAT                  = "eat"  //就餐
let BD_USER_STATUS_REST                 = "rest" //小休

// 消息发送状态
let BD_MESSAGE_STATUS_SENDING           = "sending"  // 发送中
let BD_MESSAGE_STATUS_RECEIVED          = "received" // 送达
let BD_MESSAGE_STATUS_READ              = "read" // 已读
let BD_MESSAGE_STATUS_STORED            = "stored" // 发送到服务器，成功存储数据库中
let BD_MESSAGE_STATUS_ERROR             = "error" // 发送错误
let BD_MESSAGE_STATUS_DESTROYED         = "destroyed" // 阅后即焚已销毁
let BD_MESSAGE_STATUS_RECALL            = "recall" // 消息撤回

// 消息类型
let BD_MESSAGE_TYPE_TEXT                = "text"  // 文本消息类型
let BD_MESSAGE_TYPE_IMAGE               = "image"  // 图片消息类型
let BD_MESSAGE_TYPE_FILE                = "file"  // 图片消息类型
let BD_MESSAGE_TYPE_VOICE               = "voice"  // 语音消息类型
let BD_MESSAGE_TYPE_VIDEO               = "video"  // 视频消息类型
let BD_MESSAGE_TYPE_SHORTVIDEO          = "shortvideo"    // 短视频消息类型
let BD_MESSAGE_TYPE_LOCATION            = "location"    // 位置消息类型
let BD_MESSAGE_TYPE_LINK                = "link"    // 链接消息类型
let BD_MESSAGE_TYPE_EVENT               = "event"  // 事件消息类型
let BD_MESSAGE_TYPE_ROBOT               = "robot"  // 智能问答
let BD_MESSAGE_TYPE_ROBOTV2             = "robotv2"  // 智能问答
let BD_MESSAGE_TYPE_QUESTIONNAIRE       = "questionnaire"   // 调查问卷
let BD_MESSAGE_TYPE_WORKGROUP           = "workGroup"   // 选择工作组
let BD_MESSAGE_TYPE_NOTIFICATION        = "notification"    // 通知消息类型
let BD_MESSAGE_TYPE_CUSTOM              = "custom"   // 自定义消息类型：内容放在content字段
let BD_MESSAGE_TYPE_RED_PACKET          = "red_packet" // 红包
let BD_MESSAGE_TYPE_COMMODITY           = "commodity" // 商品


let BD_MESSAGE_TYPE_NOTIFICATION_NON_WORKING_TIME    = "notification_non_working_time"    // 非工作时间
let BD_MESSAGE_TYPE_NOTIFICATION_OFFLINE            = "notification_offline"    // 客服离线，当前无客服在线
let BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_START       = "notification_browse_start" // 开始浏览页面
let BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_END         = "notification_browse_end" // 浏览页面结束
let BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE      = "notification_browse_invite" // 邀请访客
let BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE_ACCEPT = "notification_browse_invite_accept" // 访客接受邀请
let BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE_REJECT = "notification_browse_invite_reject" // 访客拒绝邀请
let BD_MESSAGE_TYPE_NOTIFICATION_THREAD             = "notification_thread"  // 新会话thread
let BD_MESSAGE_TYPE_NOTIFICATION_THREAD_REENTRY     = "notification_thread_reentry" // 重新进入会话
let BD_MESSAGE_TYPE_NOTIFICATION_TICKET             = "notification_ticket"  // 新建工单
let BD_MESSAGE_TYPE_NOTIFICATION_FEEDBACK           = "notification_feedback"  // 意见反馈
let BD_MESSAGE_TYPE_NOTIFICATION_QUEUE              = "notification_queue"    // 排队通知类型
// 排队中离开
let BD_MESSAGE_TYPE_NOTIFICATION_QUEUE_LEAVE        = "notification_queue_leave"
// 接入队列访客
let BD_MESSAGE_TYPE_NOTIFICATION_QUEUE_ACCEPT       = "notification_queue_accept"
let BD_MESSAGE_TYPE_NOTIFICATION_QUEUE_IGNORE       = "notification_queue_ignore"
let BD_MESSAGE_TYPE_NOTIFICATION_QUEUE_TIMEOUT      = "notification_queue_timeout"

let BD_MESSAGE_TYPE_NOTIFICATION_ACCEPT_AUTO        = "notification_accept_auto"    // 自动接入会话
let BD_MESSAGE_TYPE_NOTIFICATION_ACCEPT_MANUAL      = "notification_accept_manual"    // 手动接入
let BD_MESSAGE_TYPE_NOTIFICATION_CONNECT            = "notification_connect"    // 上线
let BD_MESSAGE_TYPE_NOTIFICATION_DISCONNECT         = "notification_disconnect"  // 离线
let BD_MESSAGE_TYPE_NOTIFICATION_LEAVE              = "notification_leave"    // 离开会话页面
let BD_MESSAGE_TYPE_NOTIFICATION_AGENT_CLOSE        = "notification_agent_close"    // 客服关闭会话
let BD_MESSAGE_TYPE_NOTIFICATION_VISITOR_CLOSE      = "notification_visitor_close"    // 访客关闭会话
let BD_MESSAGE_TYPE_NOTIFICATION_AUTO_CLOSE         = "notification_auto_close" // 自动关闭会话
let BD_MESSAGE_TYPE_NOTIFICATION_INVITE_RATE        = "notification_invite_rate"    // 邀请评价
let BD_MESSAGE_TYPE_NOTIFICATION_INVITE             = "notification_invite"  // 邀请会话
let BD_MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT      = "notification_invite_accept"    // 接受邀请
let BD_MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT      = "notification_invite_reject"    // 拒绝邀请
let BD_MESSAGE_TYPE_NOTIFICATION_RATE_RESULT        = "notification_rate_result" // 评价结果
let BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER           = "notification_transfer"  // 转接会话
let BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT    = "notification_transfer_accept"    // 接受转接
let BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT    = "notification_transfer_reject"    // 拒绝转接
let BD_MESSAGE_TYPE_NOTIFICATION_RATE_REQUEST       = "notification_rate_request"  // 满意度请求
let BD_MESSAGE_TYPE_NOTIFICATION_RATE               = "notification_rate"  // 评价

// 普通用户设置 在线状态消息
let BD_MESSAGE_TYPE_NOTIFICATION_ONLINE_STATUS      = "notification_online_status"
// 长连接状态
let BD_MESSAGE_TYPE_NOTIFICATION_CONNECTION_STATUS  = "notification_connection_status"
// 客服设置接待状态
let BD_MESSAGE_TYPE_NOTIFICATION_ACCEPT_STATUS      = "notification_accept_status"
// 消息预知
let BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW            = "notification_preview"
// 消息撤回
let BD_MESSAGE_TYPE_NOTIFICATION_RECALL             = "notification_recall"
// 消息被拒绝
let BD_MESSAGE_TYPE_NOTIFICATION_BLOCK              = "notification_block"
// 浏览
let BD_MESSAGE_TYPE_NOTIFICATION_BROWSE             = "notification_browse"
// 非会话类消息通知
let BD_MESSAGE_TYPE_NOTIFICATION_NOTICE             = "notification_notice"
// 频道通知
let BD_MESSAGE_TYPE_NOTIFICATION_CHANNEL            = "notification_channel"
// 消息回执：收到消息之后回复给消息发送方
let BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT            = "notification_receipt"
// 踢掉其他客户端
let BD_MESSAGE_TYPE_NOTIFICATION_KICKOFF            = "notification_kickoff"
// 发送表单请求
//let BD_MESSAGE_TYPE_NOTIFICATION_FORM               = "notification_form"
// 表单内嵌类型
let BD_MESSAGE_TYPE_NOTIFICATION_FORM_REQUEST       = "notification_form_request"
let BD_MESSAGE_TYPE_NOTIFICATION_FORM_RESULT        = "notification_form_result"
// webrtc通知初始化localStream
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_VIDEO     = "notification_webrtc_invite_video"
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_AUDIO     = "notification_webrtc_invite_audio"
// webrtc取消邀请
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANCEL     = "notification_webrtc_cancel"
// webrtc邀请视频会话
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_VIDEO     = "notification_webrtc_offer_video"
// webrtc邀请音频会话
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_AUDIO     = "notification_webrtc_offer_audio"
// 接受webrtc邀请
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ANSWER     = "notification_webrtc_answer"
// webrtc candidate信息
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANDIDATE     = "notification_webrtc_candidate"
// 接受webrtc邀请
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ACCEPT     = "notification_webrtc_accept"
// 拒绝webrtc邀请
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_REJECT     = "notification_webrtc_reject"
// 被邀请方视频设备 + peeConnection已经就绪
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_READY     = "notification_webrtc_ready"
// webrtc忙线
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_BUSY     = "notification_webrtc_busy"
// 结束webrtc会话
let BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CLOSE     = "notification_webrtc_close"
// 用户进入页面，来源于小程序
let BD_MESSAGE_TYPE_USER_ENTER_TEMPSESSION     = "user_enter_tempsession"
// 创建群组
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_CREATE     = "notification_group_create"
// 更新群名称、简介等
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_UPDATE     = "notification_group_update"
// 邀请多人加入群
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE     = "notification_group_invite"
// 受邀请：同意
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE_ACCEPT     = "notification_group_invite_accept"
// 受邀请：拒绝
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE_REJECT     = "notification_group_invite_reject"
// 不需要审核加入群组
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_JOIN     = "notification_group_join"
// 主动申请加入群组
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY     = "notification_group_apply"
// 同意：主动申请加群
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_APPROVE     = "notification_group_apply_approve"
// 拒绝：主动申请加群
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_DENY     = "notification_group_apply_deny"
// 踢人
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_KICK     = "notification_group_kick"
// 禁言
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_MUTE     = "notification_group_mute"
// 取消禁言
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_UNMUTE   = "notification_group_unmute"
// 设置管理员
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_SET_ADMIN    = "notification_group_set_admin"
// 取消设置管理员
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_UNSET_ADMIN  = "notification_group_unset_admin"
// 移交群组
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER     = "notification_group_transfer"
// 移交群组：同意、接受
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_ACCEPT     = "notification_group_transfer_accept"
// 移交群组：拒绝
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_REJECT     = "notification_group_transfer_reject"
// 退出群组
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_WITHDRAW     = "notification_group_withdraw"
// 解散群组
let BD_MESSAGE_TYPE_NOTIFICATION_GROUP_DISMISS     = "notification_group_dismiss"

let BD_GROUP_TYPE_GROUP = "group"
let BD_GROUP_TYPE_DISCUSS = "discuss"

let BD_IOS_BUILD_DEBUG = "debug"
let BD_IOS_BUILD_RELEASE = "release"

let BD_SHOULD_PLAY_SEND_MESSAGE_SOUND           = "bd_should_play_send_message_sound"
let BD_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND        = "bd_should_play_receive_message_sound"
let BD_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE        = "bd_should_vibrate_on_receiving_message"
let BD_SHOULD_SET_DEVICE_INFO                   = "bd_should_set_device_info"

let BD_UPLOAD_NOTIFICATION_PERCENTAGE           = "bd_upload_notification_percentage"
let BD_UPLOAD_NOTIFICATION_ERROR                = "bd_upload_notification_error"
