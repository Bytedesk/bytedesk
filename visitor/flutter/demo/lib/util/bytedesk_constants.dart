// import 'dart:io';
// ignore_for_file: constant_identifier_names

class BytedeskConstants {
  //
  // 默认工作组: 201807171659201
  // 机器人: 201809061716221
  // 良师官方客服：201808101819291
  // aid = 201808221551193
  //
  // TODO: 增加自定义服务器地址接口
  // 公司debug
  // static const bool isDebug = true;
  // static const bool isSecure = false;
  // static const bool isWebSocketWss = false;
  // static const String webSocketWssUrl = 'wss://www.bytedesk.com/websocket';
  // static const String httpBaseUrl = 'http://$mqttHost:8000';
  // static const String httpBaseUrlAndroid = 'http://$mqttHost:8000';
  // static const String httpBaseUrliOS = 'http://$mqttHost:8000';
  // static const String httpUploadUrl = 'http://$mqttHost:8000';
  // static const String host = '$mqttHost:8000';
  // static const int mqttPort = 3883; // not secure
  // static const String mqttHost = '172.20.10.7';

  // 其他
  // static const bool isDebug = true;
  // static const bool isSecure = false;
  // static const bool isWebSocketWss = false;
  // static const String webSocketWssUrl = 'wss://www.bytedesk.com/websocket';
  // static const String httpBaseUrl = 'http://$mqttHost:8000';
  // static const String httpBaseUrlAndroid = 'http://$mqttHost:8000';
  // static const String httpBaseUrliOS = 'http://$mqttHost:8000';
  // static const String httpUploadUrl = 'http://$mqttHost:8000';
  // static const String host = '$mqttHost:8000';
  // static const int mqttPort = 3883; // not secure
  // static const String mqttHost = '10.11.4.162';

  // 家
  // static const bool isDebug = true;
  // static const bool isSecure = false;
  // static const bool isWebSocketWss = false;
  // static const String webSocketWssUrl = 'wss://flutter.bytedesk.com/websocket';
  // static const int mqttPort = 3883; // not secure
  // static const String httpBaseUrl = 'http://$mqttHost:8000';
  // static const String httpBaseUrlAndroid = 'http://$mqttHost:8000';
  // static const String httpBaseUrliOS = 'http://$mqttHost:8000';
  // static const String httpUploadUrl = 'http://$mqttHost:8000';
  // static const String host = '$mqttHost:8000';
  // static const String mqttHost = '192.168.110.121';

  // 本地
  // static const bool isDebug = true;
  // static const bool isSecure = false;
  // static const bool isWebSocketWss = false;
  // static const String webSocketWssUrl = 'wss://flutter.bytedesk.com/websocket';
  // static const int mqttPort = 3883; // not secure
  // static const String httpBaseUrl = 'http://$mqttHost:8000';
  // static const String httpBaseUrlAndroid = 'http://$mqttHost:8000';
  // static const String httpBaseUrliOS = 'http://$mqttHost:8000';
  // static const String httpUploadUrl = 'http://$mqttHost:8000';
  // static const String host = '$mqttHost:8000';
  // static const String mqttHost = '127.0.0.1';

  // 线上
  static const bool isDebug = false;
  static const bool isSecure = true;
  static const bool isWebSocketWss = true;
  static const String webSocketWssUrl = 'wss://$mqttHost/websocket';
  static const int mqttPort = 13883;
  static const String httpBaseUrl = 'https://$mqttHost';
  static const String httpBaseUrlAndroid = 'https://$mqttHost';
  static const String httpBaseUrliOS = 'https://$mqttHost';
  static const String httpUploadUrl = 'https://upload.bytedesk.com';
  static const String host = mqttHost;
  static const String mqttHost = 'flutter.bytedesk.com';

  //
  static const String WORKGROUP_WID_LIANGSHIBAO = '201808101819291';
  static const String CHAT_TYPE_WORKGROUP = 'workGroup';
  static const String CHAT_TYPE_APPOINTED = 'appointed';

  static const String exist = 'bytedesk_exist';
  static const String code = 'bytedesk_code';

  // 含是否匿名登录
  static const String isLogin = 'bytedesk_isLogin';
  // 此用户是否绑定手机号
  static const String isAuthenticated = 'bytedesk_isAuthenticated';
  static const String isCurrentChatKfPage = 'bytedesk_isCurrentChatKfPage';
  static const String isExitLogin = 'bytedesk_isExitLogin';
  //
  static const String accessToken = 'bytedesk_accessToken';
  static const String refreshToken = 'bytedesk_refreshToken';
  //
  static const String appkey = 'bytedesk_appkey';
  static const String subdomain = 'bytedesk_subdomain';
  //
  static const String user = 'bytedesk_user';
  static const String uid = 'bytedesk_uid';
  static const String username = 'bytedesk_username';
  static const String password = 'bytedesk_password';
  static const String nickname = 'bytedesk_nickname';
  static const String avatar = 'bytedesk_avatar';
  static const String description = 'bytedesk_description';
  static const String sex = 'bytedesk_sex';
  static const String location = 'bytedesk_location';
  static const String birthday = 'bytedesk_birthday';
  static const String subDomain = 'bytedesk_subDomain';
  static const String role = 'bytedesk_role';
  static const String mobile = 'bytedesk_mobile';
  static const String unionid = 'bytedesk_unionid';
  static const String openid = 'bytedesk_openid';
  // rest client
  // static const String client = 'flutter';
  // static const String clientSchool = 'flutter_school';
  //
  static const String latitude = 'latitude';
  static const String longtitude = 'longtitude';
  //
  static const String build = 'release';
  //
  static const String PLAY_AUDIO_ON_SEND_MESSAGE = 'playAudioOnSendMessage';
  static const String PLAY_AUDIO_ON_RECEIVE_MESSAGE =
      'playAudioOnReceiveMessage';
  static const String VIBRATE_ON_RECEIVE_MESSAGE = 'vibrateOnReceiveMessage';
  //
  static const String DEFAULT_AVATA =
      "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/admin_default_avatar.png";
  static const String VIDEO_PLAY =
      "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/video_play.png";
  // 连接中
  static const String USER_STATUS_CONNECTING = "connecting";
  // 跟服务器建立长连接
  static const String USER_STATUS_CONNECTED = "connected";
  // 断开长连接
  static const String USER_STATUS_DISCONNECTED = "disconnected";
  //
  // 访客
  static const String ROLE_VISITOR = "ROLE_VISITOR";
  // 注册管理员：注册用户默认角色
  static const String ROLE_ADMIN = "ROLE_ADMIN";
  //
  // 文本消息类型
  static const String MESSAGE_TYPE_TEXT = 'text';
  // 图片消息类型
  static const String MESSAGE_TYPE_IMAGE = 'image';
  // 文件消息类型
  static const String MESSAGE_TYPE_FILE = 'file';
  // 语音消息类型
  static const String MESSAGE_TYPE_VOICE = 'voice';
  // 视频消息类型
  static const String MESSAGE_TYPE_VIDEO = 'video';
  // 自定义消息类型
  static const String MESSAGE_TYPE_CUSTOM = 'custom';
  // 红包
  static const String MESSAGE_TYPE_RED_PACKET = 'red_packet';
  // 商品
  static const String MESSAGE_TYPE_COMMODITY = 'commodity';
  // 短视频消息类型
  static const String MESSAGE_TYPE_SHORT_VIDEO = 'shortvideo';
  // 位置消息类型
  static const String MESSAGE_TYPE_LOCATION = 'location';
  // 链接消息类型
  static const String MESSAGE_TYPE_LINK = 'link';
  // 事件消息类型
  static const String MESSAGE_TYPE_EVENT = 'event';
  // 机器人 自动回复
  static const String MESSAGE_TYPE_ROBOT = 'robot';
  //
  static const String MESSAGE_TYPE_ROBOT_V2 = 'robotv2';
  // 机器人欢迎语
  static const String MESSAGE_TYPE_ROBOT_WELCOME = 'robot_welcome';
  // 提示语 promot list
  static const String MESSAGE_TYPE_ROBOT_PROMOT = 'robot_promot';
  //
  static const String MESSAGE_TYPE_ROBOT_RESULT = 'robot_result';
  // 问卷
  static const String MESSAGE_TYPE_QUESTIONNAIRE = 'questionnaire';
  // 分公司，方便提取分公司所包含的国家，金吉列大学长
  static const String MESSAGE_TYPE_COMPANY = 'company';
  // 选择工作组
  static const String MESSAGE_TYPE_WORK_GROUP = 'workGroup';
  // 通知消息类型
  static const String MESSAGE_TYPE_NOTIFICATION = 'notification';
  // 非工作时间
  static const String MESSAGE_TYPE_NOTIFICATION_NON_WORKING_TIME =
      'notification_non_working_time';
  // 客服离线，当前无客服在线
  static const String MESSAGE_TYPE_NOTIFICATION_OFFLINE =
      'notification_offline';
  // 访客开始网页浏览
  static const String MESSAGE_TYPE_NOTIFICATION_BROWSE_START =
      'notification_browse_start';
  // 访客关闭网页
  static const String MESSAGE_TYPE_NOTIFICATION_BROWSE_END =
      'notification_browse_end';
  // 邀请访客
  static const String MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE =
      'notification_browse_invite';
  // 访客接受邀请
  static const String MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE_ACCEPT =
      'notification_browse_invite_accept';
  // 访客拒绝邀请
  static const String MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE_REJECT =
      'notification_browse_invite_reject';
  // 新会话thread
  static const String MESSAGE_TYPE_NOTIFICATION_THREAD = 'notification_thread';
  // 重新进入会话
  static const String MESSAGE_TYPE_NOTIFICATION_THREAD_REENTRY =
      'notification_thread_reentry';
  // 新建工单
  static const String MESSAGE_TYPE_NOTIFICATION_TICKET = 'notification_ticket';
  // 意见反馈
  static const String MESSAGE_TYPE_NOTIFICATION_FEEDBACK =
      'notification_feedback';
  // 排队通知类型
  static const String MESSAGE_TYPE_NOTIFICATION_QUEUE = 'notification_queue';
  // 排队中离开
  static const String MESSAGE_TYPE_NOTIFICATION_QUEUE_LEAVE =
      'notification_queue_leave';
  // 接入队列访客
  static const String MESSAGE_TYPE_NOTIFICATION_QUEUE_ACCEPT =
      'notification_queue_accept';
  // 忽略队列访客
  static const String MESSAGE_TYPE_NOTIFICATION_QUEUE_IGNORE =
      "notification_queue_ignore";
  // 超时队列访客
  static const String MESSAGE_TYPE_NOTIFICATION_QUEUE_TIMEOUT =
      "notification_queue_timeout";
  // 自动接入会话
  static const String MESSAGE_TYPE_NOTIFICATION_ACCEPT_AUTO =
      'notification_accept_auto';
  // 手动接入
  static const String MESSAGE_TYPE_NOTIFICATION_ACCEPT_MANUAL =
      'notification_accept_manual';
  // 上线
  static const String MESSAGE_TYPE_NOTIFICATION_CONNECT =
      'notification_connect';
  // 离线
  static const String MESSAGE_TYPE_NOTIFICATION_DISCONNECT =
      'notification_disconnect';
  // 离开会话页面
  static const String MESSAGE_TYPE_NOTIFICATION_LEAVE = 'notification_leave';
  // 客服关闭会话
  static const String MESSAGE_TYPE_NOTIFICATION_AGENT_CLOSE =
      'notification_agent_close';
  // 访客关闭会话
  static const String MESSAGE_TYPE_NOTIFICATION_VISITOR_CLOSE =
      'notification_visitor_close';
  // 自动关闭会话
  static const String MESSAGE_TYPE_NOTIFICATION_AUTO_CLOSE =
      'notification_auto_close';
  // 邀请评价
  static const String MESSAGE_TYPE_NOTIFICATION_INVITE_RATE =
      'notification_invite_rate';
  // 评价结果
  static const String MESSAGE_TYPE_NOTIFICATION_RATE_RESULT =
      'notification_rate_result';
  // 邀请会话
  static const String MESSAGE_TYPE_NOTIFICATION_INVITE = 'notification_invite';
  // 接受邀请
  static const String MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT =
      'notification_invite_accept';
  // 拒绝邀请
  static const String MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT =
      'notification_invite_reject';
  // 转接会话
  static const String MESSAGE_TYPE_NOTIFICATION_TRANSFER =
      'notification_transfer';
  // 接受转接
  static const String MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT =
      'notification_transfer_accept';
  // 拒绝转接
  static const String MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT =
      'notification_transfer_reject';
  // 满意度请求
  static const String MESSAGE_TYPE_NOTIFICATION_RATE_REQUEST =
      'notification_rate_request';
  // 评价
  static const String MESSAGE_TYPE_NOTIFICATION_RATE = 'notification_rate';
  // 连接状态
  static const String MESSAGE_TYPE_NOTIFICATION_CONNECTION_STATUS =
      'notification_connection_status';
  // 接待状态
  static const String MESSAGE_TYPE_NOTIFICATION_ACCEPT_STATUS =
      'notification_accept_status';
  // 消息预知
  static const String MESSAGE_TYPE_NOTIFICATION_PREVIEW =
      'notification_preview';
  // 消息撤回
  static const String MESSAGE_TYPE_NOTIFICATION_RECALL = 'notification_recall';
  // 浏览
  static const String MESSAGE_TYPE_NOTIFICATION_BROWSE = 'notification_browse';
  // 非会话类消息通知
  static const String MESSAGE_TYPE_NOTIFICATION_NOTICE = 'notification_notice';
  // 频道通知
  static const String MESSAGE_TYPE_NOTIFICATION_CHANNEL =
      'notification_channel';
  // 消息回执
  static const String MESSAGE_TYPE_NOTIFICATION_RECEIPT =
      'notification_receipt';
  // 踢掉其他客户端
  static const String MESSAGE_TYPE_NOTIFICATION_KICKOFF =
      'notification_kickoff';
  // 发送表单请求
  static const String MESSAGE_TYPE_NOTIFICATION_FORM = 'notification_form';
  // 表单内嵌类型
  static const String MESSAGE_TYPE_NOTIFICATION_FORM_REQUEST =
      "notification_form_request";
  static const String MESSAGE_TYPE_NOTIFICATION_FORM_RESULT =
      "notification_form_result";
  // 通知初始化localStream
  // static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE = 'notification_webrtc_invite'
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_VIDEO =
      'notification_webrtc_invite_video';
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_AUDIO =
      'notification_webrtc_invite_audio';
  // webrtc取消邀请
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANCEL =
      'notification_webrtc_cancel';
  // webrtc邀请视频会话
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_VIDEO =
      'notification_webrtc_offer_video';
  // webrtc邀请音频会话
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_AUDIO =
      'notification_webrtc_offer_audio';
  // 接受webrtc邀请
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_ANSWER =
      'notification_webrtc_answer';
  // webrtccandidate信息
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANDIDATE =
      'notification_webrtc_candidate';
  // 接受webrtc邀请
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_ACCEPT =
      'notification_webrtc_accept';
  // 拒绝webrtc邀请
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_REJECT =
      'notification_webrtc_reject';
  // 被邀请方视频设备 + peeConnection已经就绪
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_READY =
      'notification_webrtc_ready';
  // webrtc忙线
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_BUSY =
      'notification_webrtc_busy';
  // 结束webrtc会话
  static const String MESSAGE_TYPE_NOTIFICATION_WEBRTC_CLOSE =
      'notification_webrtc_close';
  // 创建群组
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_CREATE =
      'notification_group_create';
  // 更新群名称、简介等
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_UPDATE =
      'notification_group_update';
  // 群公告
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_ANNOUNCEMENT =
      'notification_group_announcement';
  // 邀请多人加入群
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE =
      'notification_group_invite';
  // 受邀请：同意
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE_ACCEPT =
      'notification_group_invite_accept';
  // 受邀请：拒绝
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE_REJECT =
      'notification_group_invite_reject';
  // 不需要审核加入群组
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_JOIN =
      'notification_group_join';
  // 主动申请加入群组
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY =
      'notification_group_apply';
  // 同意：主动申请加群
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_APPROVE =
      'notification_group_apply_approve';
  // 拒绝：主动申请加群
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_DENY =
      'notification_group_apply_deny';
  // 踢人
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_KICK =
      'notification_group_kick';
  // 禁言
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_MUTE =
      'notification_group_mute';
  // 移交群组
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER =
      'notification_group_transfer';
  // 移交群组：同意、接受
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_ACCEPT =
      'notification_group_transfer_accept';
  // 移交群组：拒绝
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_REJECT =
      'notification_group_transfer_reject';
  // 退出群组
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_WITHDRAW =
      'notification_group_withdraw';
  // 解散群组
  static const String MESSAGE_TYPE_NOTIFICATION_GROUP_DISMISS =
      'notification_group_dismiss';

  // 消息发送状态:
  // 1. 发送中
  static const String MESSAGE_STATUS_SENDING = "sending";
  // 2. 已经存储到服务器
  static const String MESSAGE_STATUS_STORED = "stored";
  // 3. 对方已收到
  static const String MESSAGE_STATUS_RECEIVED = "received";
  // 4. 对方已读
  static const String MESSAGE_STATUS_READ = "read";
  // 5. 发送错误
  static const String MESSAGE_STATUS_ERROR = "error";
  // 6. 阅后即焚已销毁
  static const String MESSAGE_STATUS_DESTROYED = "destroyed";
  // 7. 消息撤回
  static const String MESSAGE_STATUS_RECALL = "recall";

  // 工作组请求会话
  static const String THREAD_REQUEST_TYPE_WORK_GROUP = "workGroup";
  // 指定客服会话
  static const String THREAD_REQUEST_TYPE_APPOINTED = "appointed";

  // 会话类型: 工作组会话、访客跟客服一对一
  // THREAD_TYPE_THREAD 修改为 workGroup，并同步修改 安卓+iOS+web
  static const String THREAD_TYPE_WORKGROUP = "workgroup";
  static const String THREAD_TYPE_APPOINTED = "appointed";
  static const String THREAD_TYPE_CONTACT = "contact";
  static const String THREAD_TYPE_GROUP = "group";
  static const String THREAD_TYPE_ROBOT = "robot";
  static const String THREAD_TYPE_LEAVEMSG = "leavemsg";
  static const String THREAD_TYPE_FEEDBACK = "feedback";
  // 渠道会话
  static const String THREAD_TYPE_CHANNEL = "channel";
  // 机器人转人工
  static const String THREAD_TYPE_ROBOT_TO_AGENT = "robot_to_agent";
  // 工单
  static const String THREAD_TYPE_TICKET_WORKGROUP = "ticket_workgroup";
  static const String THREAD_TYPE_TICKET_APPOINTED = "ticket_appointed";
  // 文件助手
  static const String THREAD_TYPE_FILEHELPER = "filehelper";
  // 智谱AI
  static const String THREAD_TYPE_ZHIPUAI = "zhipuai";

  // 访客会话、同事一对一、群组会话
  static const String MESSAGE_SESSION_TYPE_WORKGROUP = THREAD_TYPE_WORKGROUP;
  static const String MESSAGE_SESSION_TYPE_APPOINTED = THREAD_TYPE_APPOINTED;
  static const String MESSAGE_SESSION_TYPE_CONTACT = THREAD_TYPE_CONTACT;
  static const String MESSAGE_SESSION_TYPE_GROUP = THREAD_TYPE_GROUP;
  static const String MESSAGE_SESSION_TYPE_ROBOT = THREAD_TYPE_ROBOT;
  static const String MESSAGE_SESSION_TYPE_LEAVEMSG = THREAD_TYPE_LEAVEMSG;
  static const String MESSAGE_SESSION_TYPE_FEEDBACK = THREAD_TYPE_FEEDBACK;
  static const String MESSAGE_SESSION_TYPE_ROBOT_TO_AGENT =
      THREAD_TYPE_ROBOT_TO_AGENT;
  //
  static const String MESSAGE_SESSION_TYPE_TICKET_WORKGROUP =
      THREAD_TYPE_TICKET_WORKGROUP;
  static const String MESSAGE_SESSION_TYPE_TICKET_APPOINTED =
      THREAD_TYPE_TICKET_APPOINTED;

  // 会话关闭类型：客服关闭、访客关闭、超时自动关闭、非工作时间关闭、客服离线无效会话关闭
  static const String THREAD_CLOSE_TYPE_AGENT = "agent";
  static const String THREAD_CLOSE_TYPE_VISITOR = "visitor";
  static const String THREAD_CLOSE_TYPE_TIMEOUT = "timeout";
  static const String THREAD_CLOSE_TYPE_NON_WORKING_TIME = "non_working_time";
  static const String THREAD_CLOSE_TYPE_OFFLINE = "offline";
  static const String THREAD_CLOSE_TYPE_ROBOT_TO_AGENT = "robot_to_agent";
  static const String THREAD_CLOSE_TYPE_TICKET_IGNORE = "ticket_ignore";
  static const String THREAD_CLOSE_TYPE_TICKET_DONE = "ticket_done";
}
