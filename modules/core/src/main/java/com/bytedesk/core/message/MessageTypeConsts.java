/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 15:51:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-10 14:47:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

public class MessageTypeConsts {

    private MessageTypeConsts() {
    }

    /**
     * 消息类型：文本
     */
    public static final String TEXT = "text";
    /**
     * 图片消息
     */
    public static final String IMAGE = "image";
    /**
     * 文件类型
     */
    public static final String FILE = "file";
    /**
     * 录音消息
     */
    public static final String VOICE = "voice";
    /**
     * 短视频
     */
    public static final String VIDEO = "video";
    /**
     * 选择消息类型
     */
    public static final String CHOICE = "choice";
    /**
     * 表单问卷消息类型
     */
    public static final String SURVEY = "survey";
    /**
     * 音乐
     */
    public static final String MUSIC = "music";
    /**
     *
     */
    public static final String SHORT_VIDEO = "shortvideo";
    /**
     *
     */
    public static final String LOCATION = "location";
    /**
     * 卡片消息
     */
    public static final String CARD = "card";
    /**
     * 视频号商品消息
     */
    public static final String CHANNELS_SHOP_PRODUCT = "channels_shop_product";
    /**
     * 视频号订单消息
     */
    public static final String CHANNELS_SHOP_ORDER = "channels_shop_order";
    /**
     * 图文链接
     * https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/kf-mgnt/kf-message/sendCustomMessage.html
     */
    public static final String LINK = "link";
    /**
     * 小程序卡片
     * https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/kf-mgnt/kf-message/sendCustomMessage.html
     */
    public static final String MINIPROGRAMPAGE = "miniprogrampage";
    /**
     * 菜单消息 https://kf.weixin.qq.com/api/doc/path/94744
     */
    public static final String MSGMENU = "msgmenu";
    public static final String CLICK = "click";
    public static final String MINIPROGRAM = "miniprogram";
    /**
     * 名片消息
     * https://kf.weixin.qq.com/api/doc/path/94744
     */
    public static final String BUSINESS_CARD = "business_card";
    /**
     *
     */
    public static final String EVENT = "event";
    /**
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/subscribe-message.html
     * 1、当用户触发订阅消息弹框后，用户的相关行为事件结果会推送至开发者所配置的服务器地址或微信云托管服务。
     */
    public static final String SUBSCRIBE_MSG_POP_EVENT = "subscribe_msg_popup_event";
    /**
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/subscribe-message.html
     * 2、当用户在手机端服务通知里消息卡片右上角“...”管理消息时，相应的行为事件会推送至开发者所配置的服务器地址或微信云托管服务。（目前只推送取消订阅的事件，即对消息设置“拒收”）
     */
    public static final String SUBSCRIBE_MSG_CHANGE_EVENT = "subscribe_msg_change_event";
    /**
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/subscribe-message.html
     * 3、调用订阅消息接口发送消息给用户的最终结果，会推送下发结果事件至开发者所配置的服务器地址或微信云托管服务。
     */
    public static final String SUBSCRIBE_MSG_SENT_EVENT = "subscribe_msg_sent_event";

    /**
     * 自定义消息类型：内容放在content字段
     */
    public static final String CUSTOM = "custom";
    /**
     * 红包
     */
    public static final String RED_PACKET = "red_packet";
    /**
     * 商品
     */
    public static final String COMMODITY = "commodity";
    /**
     * <a href=
     * "https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140454">subscribe(订阅)</a>
     * TODO: 用户未关注时，进行关注后的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_SUBSCRIBE = "subscribe";
    /**
     * unsubscribe(取消订阅)
     */
    public static final String MESSAGE_EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    /**
     * 事件类型 annual_renew，提醒公众号需要去年审了
     */
    public static final String MESSAGE_EVENT_TYPE_ANNUAL_RENEW = "annual_renew";
    // 资质认证成功（此时立即获得接口权限）
    public static final String MESSAGE_EVENT_TYPE_QUALIFICATION_SUCCESS = "qualification_verify_success";
    // 资质认证失败
    public static final String MESSAGE_EVENT_TYPE_QUALIFICATION_FAILED = "qualification_verify_fail";
    // 名称认证成功（即命名成功）
    public static final String MESSAGE_EVENT_TYPE_NAMING_SUCCESS = "naming_verify_success";
    // 名称认证失败
    public static final String MESSAGE_EVENT_TYPE_NAMING_FAILED = "naming_verify_fail";
    // 消息发送失败事件
    public static final String MESSAGE_EVENT_TYPE_MSG_SEND_FAILED = "msg_send_fail";
    // 用户撤回消息事件
    public static final String MESSAGE_EVENT_TYPE_USER_RECALL_MSG = "user_recall_msg";
    /**
     * 用户已关注时的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_SCAN = "SCAN";
    /**
     * 上报地理位置事件
     */
    public static final String MESSAGE_EVENT_TYPE_LOCATION = "LOCATION";
    /**
     * 点击菜单跳转链接时的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_MENU_VIEW = "VIEW";
    /**
     * 自定义菜单事件
     */
    public static final String MESSAGE_EVENT_TYPE_MENU_CLICK = "CLICK";
    /**
     * 群发消息推送结果
     */
    public static final String MESSAGE_EVENT_TYPE_MASSSENDJOBFINISH = "MASSSENDJOBFINISH";
    /**
     * 模板消息推送结果
     */
    public static final String MESSAGE_EVENT_TYPE_TEMPLATESENDJOBFINISH = "TEMPLATESENDJOBFINISH";
    /**
     * 点击自定义菜单请求客服
     */
    public static final String MESSAGE_EVENT_KEY_MENU_CLICK_AGENT = "event_agent";
    /**
     * 点击自定义菜单请求'关于我们'
     */
    public static final String MESSAGE_EVENT_KEY_MENU_CLICK_ABOUT = "event_about";
    /**
     * 自定义菜单事件推送: 扫码推事件的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_MENU_SCANCODE_PUSH = "scancode_push";
    /**
     * 自定义菜单事件推送: 扫码推事件且弹出“消息接收中”提示框的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_MENU_SCANCODE_WAITMSG = "scancode_waitmsg";
    /**
     * 自定义菜单事件推送: 弹出系统拍照发图的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_MENU_PIC_SYSPHOTO = "pic_sysphoto";
    /**
     * 自定义菜单事件推送: 弹出拍照或者相册发图的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_MENU_PIC_PHOTO_OR_ALBUM = "pic_photo_or_album";
    /**
     * 自定义菜单事件推送: 弹出微信相册发图器的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_MENU_PIC_WEIXIN = "pic_weixin";
    /**
     * 自定义菜单事件推送: 弹出地理位置选择器的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_MENU_LOCATION_SELECT = "location_select";
    /**
     * 自定义菜单事件推送: 点击菜单跳转小程序的事件推送
     */
    public static final String MESSAGE_EVENT_TYPE_MENU_VIEW_MINIPROGRAM = "view_miniprogram";
    /**
     * 机器人, 自动回复
     */
    public static final String ROBOT = "robot";

    public static final String ROBOT_V2 = "robotv2";

    public static final String ROBOT_WELCOME = "robot_welcome";
    /**
     * 问卷
     */
    public static final String QUESTIONNAIRE = "questionnaire";
    /**
     * 分公司，方便提取分公司所包含的国家，金吉列大学长
     */
    public static final String COMPANY = "company";
    /**
     * 选择工作组
     */
    public static final String WORK_GROUP = "workgroup";
    /**
     *
     */
    public static final String NOTIFICATION = "notification";
    /**
     * 非工作时间
     */
    public static final String NOTIFICATION_NON_WORKING_TIME = "notification_non_working_time";
    /**
     * 客服离线，当前无客服在线
     */
    public static final String NOTIFICATION_OFFLINE = "notification_offline";
    /**
     * 开始浏览页面
     */
    public static final String NOTIFICATION_BROWSE_START = "notification_browse_start";
    /**
     * 浏览页面结束
     */
    public static final String NOTIFICATION_BROWSE_END = "notification_browse_end";
    /**
     * 邀请访客
     */
    public static final String NOTIFICATION_BROWSE_INVITE = "notification_browse_invite";
    /**
     * 访客接受邀请
     */
    public static final String NOTIFICATION_BROWSE_INVITE_ACCEPT = "notification_browse_invite_accept";
    /**
     * 访客拒绝邀请
     */
    public static final String NOTIFICATION_BROWSE_INVITE_REJECT = "notification_browse_invite_reject";
    /**
     * 新进入会话
     */
    public static final String NOTIFICATION_THREAD = "notification_thread";
    /**
     * 重新进入会话
     */
    public static final String NOTIFICATION_THREAD_REENTRY = "notification_thread_reentry";
    /**
     * 新建工单
     */
    public static final String NOTIFICATION_TICKET = "notification_ticket";
    /**
     * 意见反馈
     */
    public static final String NOTIFICATION_FEEDBACK = "notification_feedback";
    /**
     * 留言
     */
    public static final String NOTIFICATION_LEAVEMESSAGE = "notification_leavemessage";
    /**
     * 新进入队列
     */
    public static final String NOTIFICATION_QUEUE = "notification_queue";
    /**
     * 排队中离开
     */
    public static final String NOTIFICATION_QUEUE_LEAVE = "notification_queue_leave";
    /**
     * 接入队列访客
     */
    public static final String NOTIFICATION_QUEUE_ACCEPT = "notification_queue_accept";
    /**
     * 忽略队列访客
     */
    public static final String NOTIFICATION_QUEUE_IGNORE = "notification_queue_ignore";
    /**
     * 超时队列访客
     */
    public static final String NOTIFICATION_QUEUE_TIMEOUT = "notification_queue_timeout";
    /**
     * 自动接入会话
     */
    public static final String NOTIFICATION_ACCEPT_AUTO = "notification_accept_auto";
    /**
     * 手动接入
     */
    public static final String NOTIFICATION_ACCEPT_MANUAL = "notification_accept_manual";
    /**
     * 上线
     */
    public static final String NOTIFICATION_CONNECT = "notification_connect";
    /**
     * 离线
     */
    public static final String NOTIFICATION_DISCONNECT = "notification_disconnect";
    /**
     * 离开会话页面
     */
    public static final String NOTIFICATION_LEAVE = "notification_leave";
    /**
     * 客服关闭会话
     */
    public static final String NOTIFICATION_AGENT_CLOSE = "notification_agent_close";
    /**
     * 访客关闭会话
     */
    public static final String NOTIFICATION_VISITOR_CLOSE = "notification_visitor_close";
    /**
     * 自动关闭会话
     */
    public static final String NOTIFICATION_AUTO_CLOSE = "notification_auto_close";
    /**
     * 邀请会话
     */
    public static final String NOTIFICATION_INVITE = "notification_invite";
    /**
     * 接受邀请
     */
    public static final String NOTIFICATION_INVITE_ACCEPT = "notification_invite_accept";
    /**
     * 拒绝邀请
     */
    public static final String NOTIFICATION_INVITE_REJECT = "notification_invite_reject";
    /**
     * 转接会话
     */
    public static final String NOTIFICATION_TRANSFER = "notification_transfer";
    /**
     * 接受转接
     */
    public static final String NOTIFICATION_TRANSFER_ACCEPT = "notification_transfer_accept";
    /**
     * 拒绝转接
     */
    public static final String NOTIFICATION_TRANSFER_REJECT = "notification_transfer_reject";
    /**
     * 满意度请求
     */
    // public static final String NOTIFICATION_RATE_REQUEST =
    // "notification_rate_request";
    /**
     * 评价
     */
    // public static final String NOTIFICATION_RATE =
    // "notification_rate";
    /**
     * 人工客服邀请评价
     */
    public static final String NOTIFICATION_INVITE_RATE = "notification_invite_rate";
    /**
     * 评价人工客服结果
     */
    public static final String NOTIFICATION_RATE_RESULT = "notification_rate_result";
    /**
     * 评价机器人答案：有帮助
     */
    public static final String NOTIFICATION_RATE_HELPFUL = "notification_rate_helpful";
    /**
     * 评价机器人答案：无帮助
     */
    public static final String NOTIFICATION_RATE_HELPLESS = "notification_rate_helpless";
    /**
     * 通知支付结果
     */
    public static final String NOTIFICATION_PAY_RESULT = "notification_pay_result";
    /**
     * 普通用户设置 在线状态消息
     */
    public static final String NOTIFICATION_ONLINE_STATUS = "notification_online_status";
    /**
     * 长连接状态
     */
    public static final String NOTIFICATION_CONNECTION_STATUS = "notification_connection_status";
    /**
     * 客服设置接待状态
     */
    public static final String NOTIFICATION_ACCEPT_STATUS = "notification_accept_status";
    /**
     * 消息预知
     */
    public static final String NOTIFICATION_PREVIEW = "notification_preview";
    // 正在输入
    public static final String NOTIFICATION_TYPING = "notification_typing";
    /**
     * 消息撤回
     */
    public static final String NOTIFICATION_RECALL = "notification_recall";
    /**
     * 消息被拒绝
     */
    public static final String NOTIFICATION_BLOCK = "notification_block";
    /**
     * 访客浏览网页，通知客服端
     */
    public static final String NOTIFICATION_BROWSE = "notification_browse";
    /**
     * 非会话类消息通知
     */
    public static final String NOTIFICATION_NOTICE = "notification_notice";
    /**
     * 频道通知
     */
    public static final String NOTIFICATION_CHANNEL = "notification_channel";
    /**
     * 消息回执：收到消息之后回复给消息发送方
     * 消息content字段存放status: 1. MESSAGE_STATUS_RECEIVED(received), 2.
     * MESSAGE_STATUS_READ(read)
     */
    public static final String NOTIFICATION_RECEIPT = "notification_receipt";
    /**
     * 发送消息错误：推送给微信等第三方平台回执
     */
    public static final String NOTIFICATION_ERROR = "notification_error";
    /**
     * 踢掉其他客户端
     */
    public static final String NOTIFICATION_KICKOFF = "notification_kickoff";
    /**
     * 窗口抖动提醒
     */
    public static final String NOTIFICATION_SHAKE = "notification_shake";
    /**
     * 发送表单请求
     */
    public static final String NOTIFICATION_FORM = "notification_form";

    // 表单内嵌类型
    public static final String NOTIFICATION_FORM_REQUEST = "notification_form_request";
    public static final String NOTIFICATION_FORM_RESULT = "notification_form_result";
    /**
     * webrtc通知初始化localStream
     */
    public static final String NOTIFICATION_WEBRTC_INVITE_VIDEO = "notification_webrtc_invite_video";
    public static final String NOTIFICATION_WEBRTC_INVITE_AUDIO = "notification_webrtc_invite_audio";
    /**
     * webrtc取消邀请
     */
    public static final String NOTIFICATION_WEBRTC_CANCEL = "notification_webrtc_cancel";
    /**
     * webrtc邀请视频会话
     */
    public static final String NOTIFICATION_WEBRTC_OFFER_VIDEO = "notification_webrtc_offer_video";
    /**
     * webrtc邀请音频会话
     */
    public static final String NOTIFICATION_WEBRTC_OFFER_AUDIO = "notification_webrtc_offer_audio";
    /**
     * 接受webrtc邀请
     */
    public static final String NOTIFICATION_WEBRTC_ANSWER = "notification_webrtc_answer";
    /**
     * webrtc candidate信息
     */
    public static final String NOTIFICATION_WEBRTC_CANDIDATE = "notification_webrtc_candidate";
    /**
     * 接受webrtc邀请
     */
    public static final String NOTIFICATION_WEBRTC_ACCEPT = "notification_webrtc_accept";
    /**
     * 拒绝webrtc邀请
     */
    public static final String NOTIFICATION_WEBRTC_REJECT = "notification_webrtc_reject";
    /**
     * 被邀请方视频设备 + peeConnection已经就绪
     */
    public static final String NOTIFICATION_WEBRTC_READY = "notification_webrtc_ready";
    /**
     * webrtc忙线
     */
    public static final String NOTIFICATION_WEBRTC_BUSY = "notification_webrtc_busy";
    /**
     * 结束webrtc会话
     */
    public static final String NOTIFICATION_WEBRTC_CLOSE = "notification_webrtc_close";
    /**
     * 用户进入页面，来源于小程序
     */
    public static final String USER_ENTER_TEMPSESSION = "user_enter_tempsession";
    /**
     * 创建群组
     */
    public static final String NOTIFICATION_GROUP_CREATE = "notification_group_create";
    /**
     * 更新群名称、简介等
     */
    public static final String NOTIFICATION_GROUP_UPDATE = "notification_group_update";
    /**
     * 群公告
     */
    public static final String NOTIFICATION_GROUP_ANNOUNCEMENT = "notification_group_announcement";
    /**
     * 邀请多人加入群
     */
    public static final String NOTIFICATION_GROUP_INVITE = "notification_group_invite";
    /**
     * 受邀请：同意
     */
    public static final String NOTIFICATION_GROUP_INVITE_ACCEPT = "notification_group_invite_accept";
    /**
     * 受邀请：拒绝
     */
    public static final String NOTIFICATION_GROUP_INVITE_REJECT = "notification_group_invite_reject";
    /**
     * 不需要审核加入群组
     */
    public static final String NOTIFICATION_GROUP_JOIN = "notification_group_join";
    /**
     * 主动申请加入群组
     */
    public static final String NOTIFICATION_GROUP_APPLY = "notification_group_apply";
    /**
     * 同意：主动申请加群
     */
    public static final String NOTIFICATION_GROUP_APPLY_APPROVE = "notification_group_apply_approve";
    /**
     * 拒绝：主动申请加群
     */
    public static final String NOTIFICATION_GROUP_APPLY_DENY = "notification_group_apply_deny";
    /**
     * 踢人
     */
    public static final String NOTIFICATION_GROUP_KICK = "notification_group_kick";
    /**
     * 禁言
     */
    public static final String NOTIFICATION_GROUP_MUTE = "notification_group_mute";
    /**
     * 取消禁言
     */
    public static final String NOTIFICATION_GROUP_UNMUTE = "notification_group_unmute";
    /**
     * 设置管理员
     */
    public static final String NOTIFICATION_GROUP_SET_ADMIN = "notification_group_set_admin";
    /**
     * 取消设置管理员
     */
    public static final String NOTIFICATION_GROUP_UNSET_ADMIN = "notification_group_unset_admin";
    /**
     * 移交群组
     */
    public static final String NOTIFICATION_GROUP_TRANSFER = "notification_group_transfer";
    /**
     * 移交群组：同意、接受
     */
    public static final String NOTIFICATION_GROUP_TRANSFER_ACCEPT = "notification_group_transfer_accept";
    /**
     * 移交群组：拒绝
     */
    public static final String NOTIFICATION_GROUP_TRANSFER_REJECT = "notification_group_transfer_reject";
    /**
     * 退出群组
     */
    public static final String NOTIFICATION_GROUP_WITHDRAW = "notification_group_withdraw";
    /**
     * 解散群组
     */
    public static final String NOTIFICATION_GROUP_DISMISS = "notification_group_dismiss";
    


    /**
     * 回执
     */
    // 服务器回复客户端，已经收到。消息发送成功
    public static final String NOTIFICATION_ACK_SUCCESS = "notification_ack_success";
    // 消息送达
    public static final String NOTIFICATION_ACK_RECEIVED = "notification_ack_received";
    // 消息已读
    public static final String NOTIFICATION_ACK_READ = "notification_ack_read";

}
