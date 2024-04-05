/**
 * bytedesk.com
 */
var bd_kfe_data = {
  // 前面这几个变量是需要修改的变量
  IS_PRODUCTION: false,
  HTTP_HOST: "http://127.0.0.1:9003",
  STOMP_HOST: "http://127.0.0.1:9003",
  CHAT_URL: "http://127.0.0.1:9005",
  // IS_PRODUCTION: true,
  // HTTP_HOST: "https://pcapi.bytedesk.com",
  // STOMP_HOST: "https://pcstomp.bytedesk.com",
  // CHAT_URL: "https://chat.kefubaobao.com",
  // 下面的不动
  closable: "0",
  column: "1",
  history: "0",
  lang: "cn",
  color: "#ffffff",
  background: "#007bff",
  voiceMuted: false,
  v2robot: "0",
  // 短链
  shortCode: "",
  //
  hidden: false,
  browserTitle: "",
  websiteUrl: "",
  websiteTitle: "",
  refererUrl: "",
  preload: "0",
  //
  // uni_wid: "",
  // 是否还有更多历史记录
  hasMoreHistoryMessage: true,
  imageDialogVisible: false,
  currentImageUrl: "",
  currentVoiceUrl: "",
  // 表情面板
  show_emoji: false,
  emojiBaseUrl: "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/emojis/gif/",
  inputContent: "",
  messages: [],
  // 上传图片相关参数
  upload_headers: {
    "X-CSRF-TOKEN": "",
    "X-Requested-With": "XMLHttpRequest",
    Accept: "application/json",
    Authorization: "Bearer "
  },
  upload_data: {
    file_name: "test.png",
    username: ""
  },
  upload_name: "file",
  // 接收图片服务器地址
  uploadImageServerUrl: "https://cdn.bytedesk.com/visitor/api/upload/image",
  // 上传图片结果集, 格式：{name: "", url: ""}
  uploadedImageList: [],
  //
  inputTipVisible: false,
  // 仅允许评价一次
  isRated: false,
  // 是否客服邀请评价
  isInviteRate: false,
  // 满意度评价对话框是否可见
  // rateDialogVisible: false,
  // 满意度评分
  rateScore: 5,
  // 满意度附言
  rateContent: "",
  // 留言
  showLeaveMessage: false,
  leaveMessageForm: {
    mobile: "",
    email: "",
    content: ""
  },
  //
  isLoading: false,
  stompClient: "",
  sessionId: "",
  preSessionId: "",
  browseInviteBIid: "",
  passport: {
    token: {
      access_token: "",
      expires_in: 0,
      jti: "",
      refresh_token: "",
      scope: "",
      token_type: ""
    }
  },
  // 左上角标题title
  title: "",
  adminUid: "",
  workGroupWid: "",
  workGroupNickname: "",
  workGroupAvatar: "",
  workGroupDescription: "",
  subDomain: "",
  client: "web_pc",
  //
  agentAvatar: '',
  agentNickname: '',
  agentDescription: "描述",
  thread: {
    id: 0,
    tid: "",
    topic: "",
    visitor: {
      uid: "",
      username: "",
      nickname: "",
      avatar: ""
    }
  },
  // 已经订阅的topic
  subscribedTopics: [],
  // 加载聊天记录offset
  page: 0,
  // 是否是最后一批聊天记录
  last: false,
  // workGroup/visitor/contact/group
  type: "workGroup",
  // 指定客服
  agentUid: "",
  // 当前访客用户名
  selfuser: "0",
  uid: "",
  avatar: "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/chrome_default_avatar.png",
  username: "",
  password: "",
  nickname: "",
  access_token: "",
  // 本地存储access_token的key
  token: "bd_kfe_token",
  isConnected: false,
  answers: [],
  isRobot: false,
  // isAutoPop: false,
  // isThreadStarted: false,
  isThreadClosed: true,
  // 默认不自动请求会话
  noRequestThread: false,
  // 
  postscript: '',
  postScriptPrefix: '<附言>:',
  // 转人工关键词
  transferWords: [],
  //
  browserTitle: "",
  browserTabHidden: false,
  // 消息发送状态
  // 发送中
  MESSAGE_STATUS_SENDING: 'sending',
  // 送达
  MESSAGE_STATUS_RECEIVED: 'received',
  // 已读
  MESSAGE_STATUS_READ: 'read',
  // 发送到服务器，成功存储数据库中
  MESSAGE_STATUS_STORED: 'stored',
  // 发送错误
  MESSAGE_STATUS_ERROR: 'error',
  // 部分消息类型
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
  // 机器人
  MESSAGE_TYPE_ROBOT: 'robot',
  // 长按复制
  // toucheX: 0,
  // toucheY: 0,
  // timeOutEvent: '',
  //
  questionnaireItemItems: Object,
  //
  emotionBaseUrl: "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/emojis/gif/",
  // 表情
  emotionMap: {
    "[微笑]": "100.gif",
    "[撇嘴]": "101.gif",
    "[色]": "102.gif",
    "[发呆]": "103.gif",
    "[得意]": "104.gif",
    "[流泪]": "105.gif",
    "[害羞]": "106.gif",
    "[闭嘴]": "107.gif",
    "[睡]": "108.gif",
    "[大哭]": "109.gif",

    "[尴尬]": "110.gif",
    "[发怒]": "111.gif",
    "[调皮]": "112.gif",
    "[呲牙]": "113.gif",
    "[惊讶]": "114.gif",
    "[难过]": "115.gif",
    "[酷]": "116.gif",
    "[冷汗]": "117.gif",
    "[抓狂]": "118.gif",
    "[吐]": "119.gif",

    "[偷笑]": "120.gif",
    "[愉快]": "121.gif",
    "[白眼]": "122.gif",
    "[傲慢]": "123.gif",
    "[饥饿]": "124.gif",
    "[困]": "125.gif",
    "[惊恐]": "126.gif",
    "[流汗]": "127.gif",
    "[憨笑]": "128.gif",
    "[悠闲]": "129.gif",

    "[奋斗]": "130.gif",
    "[咒骂]": "131.gif",
    "[疑问]": "132.gif",
    "[嘘]": "133.gif",
    "[晕]": "134.gif",
    "[疯了]": "135.gif",
    "[衰]": "136.gif",
    "[骷髅]": "137.gif",
    "[敲打]": "138.gif",
    "[再见]": "139.gif",

    "[擦汗]": "140.gif",
    "[抠鼻]": "141.gif",
    "[鼓掌]": "142.gif",
    "[糗大了]": "143.gif",
    "[坏笑]": "144.gif",
    "[左哼哼]": "145.gif",
    "[右哼哼]": "146.gif",
    "[哈欠]": "147.gif",
    "[鄙视]": "148.gif",
    "[委屈]": "149.gif",

    "[快哭]": "150.gif",
    "[阴险]": "151.gif",
    "[亲亲]": "152.gif",
    "[吓]": "153.gif",
    "[可怜]": "154.gif",
    "[菜刀]": "155.gif",
    "[西瓜]": "156.gif",
    "[啤酒]": "157.gif",
    "[篮球]": "158.gif",
    "[乒乓]": "159.gif",

    "[咖啡]": "160.gif",
    "[饭]": "161.gif",
    "[猪头]": "162.gif",
    "[玫瑰]": "163.gif",
    "[凋谢]": "164.gif",
    "[嘴唇]": "165.gif",
    "[爱心]": "166.gif",
    "[心碎]": "167.gif",
    "[蛋糕]": "168.gif",
    "[闪电]": "169.gif",

    "[炸弹]": "170.gif",
    "[刀]": "171.gif",
    "[足球]": "172.gif",
    "[瓢虫]": "173.gif",
    "[便便]": "174.gif",
    "[月亮]": "175.gif",
    "[太阳]": "176.gif",
    "[礼物]": "177.gif",
    "[拥抱]": "178.gif",
    "[强]": "179.gif",

    "[弱]": "180.gif",
    "[握手]": "181.gif",
    "[胜利]": "182.gif",
    "[抱拳]": "183.gif",
    "[勾引]": "184.gif",
    "[拳头]": "185.gif",
    "[差劲]": "186.gif",
    "[爱你]": "187.gif",
    "[No]": "188.gif",
    "[OK]": "189.gif",

    "[爱情]": "190.gif",
    "[飞吻]": "191.gif",
    "[跳跳]": "192.gif",
    "[发抖]": "193.gif",
    "[怄火]": "194.gif",
    "[转圈]": "195.gif",
    "[磕头]": "196.gif",
    "[回头]": "197.gif",
    "[跳绳]": "198.gif",
    "[投降]": "199.gif",

    "[激动]": "201.gif",
    "[乱舞]": "202.gif",
    "[献吻]": "203.gif",
    "[左太极]": "204.gif",
    "[右太极]": "205.gif"
  },
  // emoji表情, code代表来自微信端的表情字符，目前已经在服务器端处理替换为title字段，故code字段暂无用途
  emojis: [
    { title: "[微笑]", file: "100.gif" },
    { title: "[撇嘴]", file: "101.gif" },
    { title: "[色]", file: "102.gif" },
    { title: "[发呆]", file: "103.gif" },
    { title: "[得意]", file: "104.gif" },
    { title: "[流泪]", file: "105.gif" },
    { title: "[害羞]", file: "106.gif" },
    { title: "[闭嘴]", file: "107.gif" },
    { title: "[睡]", file: "108.gif" },
    { title: "[大哭]", file: "109.gif" },

    { title: "[尴尬]", file: "110.gif" },
    { title: "[发怒]", file: "111.gif" },
    { title: "[调皮]", file: "112.gif" },
    { title: "[呲牙]", file: "113.gif" },
    { title: "[惊讶]", file: "114.gif" },
    { title: "[难过]", file: "115.gif" },
    { title: "[酷]", file: "116.gif" },
    { title: "[冷汗]", file: "117.gif" },
    { title: "[抓狂]", file: "118.gif" },
    { title: "[吐]", file: "119.gif" },

    { title: "[偷笑]", file: "120.gif" },
    { title: "[愉快]", file: "121.gif" },
    { title: "[白眼]", file: "122.gif" },
    { title: "[傲慢]", file: "123.gif" },
    { title: "[饥饿]", file: "124.gif" },
    { title: "[困]", file: "125.gif" },
    { title: "[惊恐]", file: "126.gif" },
    { title: "[流汗]", file: "127.gif" },
    { title: "[憨笑]", file: "128.gif" },
    { title: "[悠闲]", file: "129.gif" },

    { title: "[奋斗]", file: "130.gif" },
    { title: "[咒骂]", file: "131.gif" },
    { title: "[疑问]", file: "132.gif" },
    { title: "[嘘]", file: "133.gif" },
    { title: "[晕]", file: "134.gif" },
    { title: "[疯了]", file: "135.gif" },
    { title: "[衰]", file: "136.gif" },
    { title: "[骷髅]", file: "137.gif" },
    { title: "[敲打]", file: "138.gif" },
    { title: "[再见]", file: "139.gif" },

    { title: "[擦汗]", file: "140.gif" },
    { title: "[抠鼻]", file: "141.gif" },
    { title: "[鼓掌]", file: "142.gif" },
    { title: "[糗大了]", file: "143.gif" },
    { title: "[坏笑]", file: "144.gif" },
    { title: "[左哼哼]", file: "145.gif" },
    { title: "[右哼哼]", file: "146.gif" },
    { title: "[哈欠]", file: "147.gif" },
    { title: "[鄙视]", file: "148.gif" },
    { title: "[委屈]", file: "149.gif" },

    { title: "[快哭]", file: "150.gif" },
    { title: "[阴险]", file: "151.gif" },
    { title: "[亲亲]", file: "152.gif" },
    { title: "[吓]", file: "153.gif" }
  ],
  //
  showEmoji: function () {
    return !bd_kfe_data.disabled() && bd_kfe_data.show_emoji;
  },
  disabled: function () {
    return bd_kfe_data.thread.tid === "";
  },
  sendButtonDisabled: function () {
    return bd_kfe_data.inputContent.trim().length === 0;
  },
  threadTopic: function () {
    return bd_kfe_data.thread.topic.replace(/\//g, ".");
  },
  show_header: function () {
    return true;
  },
  my_uid() {
    return bd_kfe_data.uid;
  },
  my_username() {
    return bd_kfe_data.username;
  },
  thread_nickname() {
    return (bd_kfe_data.nickname !== "null" && bd_kfe_data.nickname.trim().length > 0) ? bd_kfe_data.nickname : bd_kfe_data.thread.visitor.nickname;
  },
  my_nickname() {
    return (bd_kfe_data.nickname !== "null" && bd_kfe_data.nickname.trim().length > 0) ? bd_kfe_data.nickname : bd_kfe_data.thread.visitor.nickname;
  },
  my_avatar() {
    return (bd_kfe_data.avatar !== "null" && bd_kfe_data.avatar.trim().length > 0) ? bd_kfe_data.avatar : bd_kfe_data.thread.visitor.avatar;
  },
  connectedImage: function () {
    return bd_kfe_data.isConnected
      ? "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/util/connected.png"
      : "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/util/disconnected.png";
  },
  // pc链接转h5链接
  getH5Url: function () {
    if (bd_kfe_data.type === 'appointed') {
      return bd_kfe_data.CHAT_URL + "/chat/h5/index.html?type=appointed&aid=" + bd_kfe_data.agentUid + "&selfuser=2&uid=" + bd_kfe_data.uid + "&p"
    } else {
      return bd_kfe_data.CHAT_URL + "/chat/h5/index.html?type=workGroup&wid=" + bd_kfe_data.workGroupWid + "&selfuser=2&uid=" + bd_kfe_data.uid + "&p"
    }
  }
};