// 本地测试
// export const IS_PRODUCTION = false
// export const API_BASE_URL = 'http://127.0.0.1:8000'
// export const WEBSOCKET_URL = 'ws://127.0.0.1:8000/stomp/mini?access_token='
// export const MQTT_PORT = 3885
// export const MQTT_WS_HOST = '127.0.0.1'
// export const MQTT_HOST = 'wss://127.0.0.1:3886/websocket'

// 线上环境
export const IS_PRODUCTION = true
export const API_BASE_URL = 'https://uniapp.bytedesk.com'
export const WEBSOCKET_URL = 'wss://uniapp.bytedesk.com/stomp/mini?access_token='
export const MQTT_PORT = 3885
export const MQTT_WS_HOST = 'mqtt.bytedesk.com'
export const MQTT_HOST = 'wss://mqtt.bytedesk.com/websocket'
//
export const uid = 'bytedesk_uid'
export const username = 'bytedesk_username'
export const password = 'bytedesk_password'
export const nickname = 'bytedesk_nickname'
export const avatar = 'bytedesk_avatar'
export const description = 'bytedesk_description'
export const subDomain = 'bytedesk_subDomain'
export const isLogin = 'bytedesk_isLogin'
export const isLoginMobile = 'bytedesk_isLogin_Mobile'
export const accessToken = 'bytedesk_accessToken'
export const refreshToken = 'bytedesk_refreshToken'
export const client = 'uniapp'
export const appKey = 'bytedesk_appkey'
// 文本消息类型
export const MESSAGE_TYPE_TEXT = 'text'
  // 图片消息类型
export const MESSAGE_TYPE_IMAGE = 'image'
  // 文件消息类型
export const MESSAGE_TYPE_FILE = 'file'
  // 语音消息类型
export const MESSAGE_TYPE_VOICE = 'voice'
  // 视频消息类型
export const MESSAGE_TYPE_VIDEO = 'video'
  // 商品
export const MESSAGE_TYPE_COMMODITY = 'commodity'
  // 短视频消息类型
export const MESSAGE_TYPE_SHORT_VIDEO = 'shortvideo'
  // 位置消息类型
export const MESSAGE_TYPE_LOCATION = 'location'
  // 链接消息类型
export const MESSAGE_TYPE_LINK = 'link'
  // 事件消息类型
export const MESSAGE_TYPE_EVENT = 'event'
  // 机器人 自动回复
export const MESSAGE_TYPE_ROBOT = 'robot'
export const MESSAGE_TYPE_ROBOTV2 = 'robotv2'
// 问卷
export const MESSAGE_TYPE_QUESTIONNAIRE = 'questionnaire'
// 选择工作组
export const MESSAGE_TYPE_WORK_GROUP = 'workGroup'
// 通知消息类型
export const MESSAGE_TYPE_NOTIFICATION = 'notification'
// 非工作时间
export const MESSAGE_TYPE_NOTIFICATION_NON_WORKING_TIME = 'notification_non_working_time'
// 客服离线，当前无客服在线
export const MESSAGE_TYPE_NOTIFICATION_OFFLINE = 'notification_offline'
// 访客开始网页浏览
export const MESSAGE_TYPE_NOTIFICATION_BROWSE_START = 'notification_browse_start'
// 访客关闭网页
export const MESSAGE_TYPE_NOTIFICATION_BROWSE_END = 'notification_browse_end'
// 邀请访客
export const MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE = 'notification_browse_invite'
// 访客接受邀请
export const MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE_ACCEPT = 'notification_browse_invite_accept'
// 访客拒绝邀请
export const MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE_REJECT = 'notification_browse_invite_reject'
// 新会话thread
export const MESSAGE_TYPE_NOTIFICATION_THREAD = 'notification_thread'
// 重新进入会话
export const MESSAGE_TYPE_NOTIFICATION_THREAD_REENTRY = 'notification_thread_reentry'
// 新建工单
export const MESSAGE_TYPE_NOTIFICATION_TICKET = 'notification_ticket'
// 意见反馈
export const MESSAGE_TYPE_NOTIFICATION_FEEDBACK = 'notification_feedback'
// 留言
export const MESSAGE_TYPE_NOTIFICATION_LEAVEMESSAGE = 'notification_leavemessage'
// 排队通知类型
export const MESSAGE_TYPE_NOTIFICATION_QUEUE = 'notification_queue'
// 排队中离开
export const MESSAGE_TYPE_NOTIFICATION_QUEUE_LEAVE = 'notification_queue_leave'
// 接入队列访客
export const MESSAGE_TYPE_NOTIFICATION_QUEUE_ACCEPT = 'notification_queue_accept'
// 忽略队列访客
export const MESSAGE_TYPE_NOTIFICATION_QUEUE_IGNORE = 'notification_queue_ignore'
// 超时队列访客
export const MESSAGE_TYPE_NOTIFICATION_QUEUE_TIMEOUT = 'notification_queue_timeout'
// 自动接入会话
export const MESSAGE_TYPE_NOTIFICATION_ACCEPT_AUTO = 'notification_accept_auto'
// 手动接入
export const MESSAGE_TYPE_NOTIFICATION_ACCEPT_MANUAL = 'notification_accept_manual'
// 上线
export const MESSAGE_TYPE_NOTIFICATION_CONNECT = 'notification_connect'
// 离线
export const MESSAGE_TYPE_NOTIFICATION_DISCONNECT = 'notification_disconnect'
// 离开会话页面
export const MESSAGE_TYPE_NOTIFICATION_LEAVE = 'notification_leave'
// 客服关闭会话
export const MESSAGE_TYPE_NOTIFICATION_AGENT_CLOSE = 'notification_agent_close'
// 访客关闭会话
export const MESSAGE_TYPE_NOTIFICATION_VISITOR_CLOSE = 'notification_visitor_close'
// 自动关闭会话
export const MESSAGE_TYPE_NOTIFICATION_AUTO_CLOSE = 'notification_auto_close'
// 邀请评价
export const MESSAGE_TYPE_NOTIFICATION_INVITE_RATE = 'notification_invite_rate'
// 评价结果
export const MESSAGE_TYPE_NOTIFICATION_RATE_RESULT = 'notification_rate_result'
// 邀请会话
export const MESSAGE_TYPE_NOTIFICATION_INVITE = 'notification_invite'
// 接受邀请
export const MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT = 'notification_invite_accept'
// 拒绝邀请
export const MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT = 'notification_invite_reject'
// 转接会话
export const MESSAGE_TYPE_NOTIFICATION_TRANSFER = 'notification_transfer'
// 接受转接
export const MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT = 'notification_transfer_accept'
// 拒绝转接
export const MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT = 'notification_transfer_reject'
// 满意度请求
export const MESSAGE_TYPE_NOTIFICATION_RATE_REQUEST = 'notification_rate_request'
// 评价
export const MESSAGE_TYPE_NOTIFICATION_RATE = 'notification_rate'
// 连接状态
export const MESSAGE_TYPE_NOTIFICATION_CONNECTION_STATUS = 'notification_connection_status'
// 接待状态
export const MESSAGE_TYPE_NOTIFICATION_ACCEPT_STATUS = 'notification_accept_status'
// 消息预知
export const MESSAGE_TYPE_NOTIFICATION_PREVIEW = 'notification_preview'
// 消息撤回
export const MESSAGE_TYPE_NOTIFICATION_RECALL = 'notification_recall'
// 消息被拒绝
export const MESSAGE_TYPE_NOTIFICATION_BLOCK = 'notification_block'
// 浏览
export const MESSAGE_TYPE_NOTIFICATION_BROWSE = 'notification_browse'
// 非会话类消息通知
export const MESSAGE_TYPE_NOTIFICATION_NOTICE = 'notification_notice'
// 频道通知
export const MESSAGE_TYPE_NOTIFICATION_CHANNEL = 'notification_channel'
// 消息回执
export const MESSAGE_TYPE_NOTIFICATION_RECEIPT = 'notification_receipt'
// 踢掉其他客户端
export const MESSAGE_TYPE_NOTIFICATION_KICKOFF = 'notification_kickoff'
// 窗口抖动提醒
export const MESSAGE_TYPE_NOTIFICATION_SHAKE = 'notification_shake'
// 发送表单请求
// export const MESSAGE_TYPE_NOTIFICATION_FORM = 'notification_form'
// 表单内嵌类型
export const MESSAGE_TYPE_NOTIFICATION_FORM_REQUEST = 'notification_form_request'
export const MESSAGE_TYPE_NOTIFICATION_FORM_RESULT = 'notification_form_result'
//
export const MESSAGE_STATUS_SENDING = 'sending'
// 送达
export const MESSAGE_STATUS_RECEIVED = 'received'
// 已读
export const MESSAGE_STATUS_READ = 'read'
// 发送到服务器，成功存储数据库中
export const MESSAGE_STATUS_STORED = 'stored'
// 发送错误
export const MESSAGE_STATUS_ERROR = 'error'
//
export const CHAT_TYPE_WORKGROUP = 'workGroup'
export const CHAT_TYPE_APPOINTED = 'appointed'
//
export const vibrate = 'bytedesk_vibrate'
export const playAudio = 'bytedesk_playAudio'
//
export const EVENT_BUS_MESSAGE = 'BYTEDESK_EVENT_BUS_MESSAGE'
// stomp连接中
export const STOMP_CONNECTION_STATUS_CONNECTING = 'connecting'
// stomp连接成功
export const STOMP_CONNECTION_STATUS_CONNECTED = 'connected'
// stomp连接断开
export const STOMP_CONNECTION_STATUS_DISCONNECTED = 'disconnncted'
// 长连接状态
export const EVENT_BUS_STOMP_CONNECTION_STATUS = 'EVENT_BUS_STOMP_CONNECTION_STATUS'
// 上传图片
export const UPLOAD_IMAGE_URL = 'https://upload.bytedesk.com/visitor/api/upload/image'
// 上传语音
export const UPLOAD_VOICE_URL = 'https://upload.bytedesk.com/visitor/api/upload/voice'
// 上传视频
export const UPLOAD_VIDEO_URL = 'https://upload.bytedesk.com/visitor/api/upload/video'
// 上传文件
export const UPLOAD_FILE_URL = 'https://upload.bytedesk.com/visitor/api/upload/file'
