// 常量
module.exports = {
  // 本地测试
  // IS_PRODUCTION: false,
  // API_BASE_URL: 'http://127.0.0.1:8000',
  // WEBSOCKET_URL: 'ws://127.0.0.1:8000/stomp/mini?access_token=',
  // 线上环境
  IS_PRODUCTION: true,
  API_BASE_URL: 'https://uniapp.bytedesk.com',
  WEBSOCKET_URL: 'wss://uniapp.bytedesk.com/stomp/mini?access_token=',
  //
  uid: 'bytedesk_uid',
  username: 'bytedesk_username',
  password: 'bytedesk_password',
  nickname: 'bytedesk_nickname',
  avatar: 'bytedesk_avatar',
  description: 'bytedesk_description',
  subDomain: 'bytedesk_subDomain',
  isLogin: 'bytedesk_isLogin',
  isLoginMobile: 'bytedesk_isLogin_Mobile',
  accessToken: 'bytedesk_accessToken',
  refreshToken: 'bytedesk_refreshToken',
  client: 'uniapp',
  appKey: 'bytedesk_appkey',
  //
  // 文本消息类型
  MESSAGE_TYPE_TEXT: 'text',
  // 图片消息类型
  MESSAGE_TYPE_IMAGE: 'image',
  // 文件消息类型
  MESSAGE_TYPE_FILE: 'file',
  // 语音消息类型
  MESSAGE_TYPE_VOICE: 'voice',
  // 视频消息类型
  MESSAGE_TYPE_VIDEO: 'video',
  // 商品
  MESSAGE_TYPE_COMMODITY: 'commodity',
  // 短视频消息类型
  MESSAGE_TYPE_SHORT_VIDEO: 'shortvideo',
  // 位置消息类型
  MESSAGE_TYPE_LOCATION: 'location',
  // 链接消息类型
  MESSAGE_TYPE_LINK: 'link',
  // 事件消息类型
  MESSAGE_TYPE_EVENT: 'event',
  // 机器人 自动回复
  MESSAGE_TYPE_ROBOT: 'robot',
  MESSAGE_TYPE_ROBOTV2: 'robotv2',
  //
  //
  MESSAGE_STATUS_SENDING: 'sending',
  // 送达
  MESSAGE_STATUS_RECEIVED: 'received',
  // 已读
  MESSAGE_STATUS_READ: 'read',
  // 发送到服务器，成功存储数据库中
  MESSAGE_STATUS_STORED: 'stored',
  // 发送错误
  MESSAGE_STATUS_ERROR: 'error',
  //
  CHAT_TYPE_WORKGROUP: 'workGroup',
  CHAT_TYPE_APPOINTED: 'appointed',
  //
  vibrate: 'bytedesk_vibrate',
  playAudio: 'bytedesk_playAudio',
  //
  EVENT_BUS_MESSAGE: 'BYTEDESK_EVENT_BUS_MESSAGE',
  // stomp连接中
  STOMP_CONNECTION_STATUS_CONNECTING: 'connecting',
  // stomp连接成功
  STOMP_CONNECTION_STATUS_CONNECTED: 'connected',
  // stomp连接断开
  STOMP_CONNECTION_STATUS_DISCONNECTED: 'disconnncted',
  // 长连接状态
  EVENT_BUS_STOMP_CONNECTION_STATUS: 'EVENT_BUS_STOMP_CONNECTION_STATUS',
  // 上传图片
  UPLOAD_IMAGE_URL: 'https://upload.bytedesk.com/visitor/api/upload/image',
  // 上传语音
  UPLOAD_VOICE_URL: 'https://upload.bytedesk.com/visitor/api/upload/voice',
  // 上传视频
  UPLOAD_VIDEO_URL: 'https://upload.bytedesk.com/visitor/api/upload/video',
  // 上传文件
  UPLOAD_FILE_URL: 'https://upload.bytedesk.com/visitor/api/upload/file',
}
