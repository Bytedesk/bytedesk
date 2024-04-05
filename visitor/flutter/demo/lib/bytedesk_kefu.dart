import 'bytedesk_kefu_platform_interface.dart';
import 'dart:async';
// import 'dart:io';

import 'package:bytedesk_kefu/http/bytedesk_device_api.dart';
import 'package:bytedesk_kefu/http/bytedesk_thread_api.dart';
import 'package:bytedesk_kefu/model/app.dart';
import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:bytedesk_kefu/model/thread.dart';
import 'package:bytedesk_kefu/model/userJsonResult.dart';
import 'package:bytedesk_kefu/model/wechatResult.dart';
// import 'package:bytedesk_kefu/ui/channel/provider/channel_provider.dart';
import 'package:bytedesk_kefu/ui/chat/page/chat_webview_page.dart';
import 'package:bytedesk_kefu/ui/chat/provider/chat_im_provider.dart';
import 'package:bytedesk_kefu/ui/chat/provider/chat_thread_provider.dart';
import 'package:bytedesk_kefu/ui/faq/provider/help_provider.dart';
import 'package:bytedesk_kefu/ui/feedback/provider/feedback_provider.dart';
import 'package:bytedesk_kefu/ui/leavemsg/provider/leavemsg_provider.dart';
import 'package:bytedesk_kefu/ui/ticket/provider/ticket_provider.dart';
// import 'package:flutter/services.dart';

import 'package:bytedesk_kefu/http/bytedesk_user_api.dart';
import 'package:bytedesk_kefu/ui/chat/provider/chat_kf_provider.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:sp_util/sp_util.dart';
import 'package:flutter/material.dart';

import 'model/user.dart';
import 'ui/leavemsg/provider/leavemsg_history_provider.dart';

class BytedeskKefu {
  //
  Future<String?> getPlatformVersion() {
    return BytedeskKefuPlatform.instance.getPlatformVersion();
  }

  // 下面为 自定义接口
  static void init(String appKey, String subDomain) async {
    anonymousLogin(appKey, subDomain);
  }

  // 支持自定义用户名，方便跟APP业务系统对接
  static void initWithUsername(
      String username, String appKey, String subDomain) async {
    initWithUsernameAndNickname(username, "", appKey, subDomain);
  }

  static void initWithUsernameAndNickname(
      String username, String nickname, String appKey, String subDomain) async {
    initWithUsernameAndNicknameAndAvatar(
        username, nickname, "", appKey, subDomain);
  }

  static void initWithUsernameAndNicknameAndAvatar(String username,
      String nickname, String avatar, String appKey, String subDomain) async {
    /// sp初始化
    await SpUtil.getInstance();
    // 首先检测是否是第一次，如果是第一此启动
    String? spusername = SpUtil.getString(BytedeskConstants.username);
    String? sppassword = SpUtil.getString(BytedeskConstants.password);
    if (spusername!.isNotEmpty) {
      // 登录
      userLogin(spusername, sppassword!, appKey, subDomain);
    } else {
      // 调用注册接口
      // 默认密码同用户名
      String password = username;
      await BytedeskUserHttpApi()
          .registerUser(username, nickname, password, avatar, subDomain);
      // 注册成功之后，调用登录接口
      String usernameCompose = "$username@$subDomain";
      userLogin(usernameCompose, password, appKey, subDomain);
    }
  }

  // 判断是否已经登录
  static bool isLogin() {
    String? accessToken = SpUtil.getString(BytedeskConstants.accessToken);
    if (accessToken!.isEmpty) {
      return false;
    }
    return true;
  }

  static bool? isAuthenticated() {
    return SpUtil.getBool(BytedeskConstants.isAuthenticated);
  }

  // 访客匿名登录接口
  static void anonymousLogin(String appKey, String subDomain) async {
    /// sp初始化
    await SpUtil.getInstance();
    // 首先检测是否是第一次，如果是第一此启动
    String? username = SpUtil.getString(BytedeskConstants.username);
    String? unionid = SpUtil.getString(BytedeskConstants.unionid);
    if (username!.isEmpty && unionid!.isEmpty) {
      // 第一此启动, 则调用注册接口，否则调用登录接口
      User user =
          await BytedeskUserHttpApi().registerAnonymous(appKey, subDomain);
      visitorLogin(user.username!, appKey, subDomain);
    } else if (username.isNotEmpty) {
      // 用户名登录，非微信登录
      visitorLogin(username, appKey, subDomain);
    } else if (unionid!.isNotEmpty) {
      // 微信登录
      unionidOAuth(unionid);
    } else {
      // 验证码登录其他账号，非当前匿名登录账户
      otherOAuth();
    }
    // 缓存appkey
    SpUtil.putString(BytedeskConstants.appkey, appKey);
    SpUtil.putString(BytedeskConstants.subDomain, subDomain);
  }

  static void anonymousLogin2() async {
    /// sp初始化
    await SpUtil.getInstance();
    String? appKey = SpUtil.getString(BytedeskConstants.appkey);
    String? subDomain = SpUtil.getString(BytedeskConstants.subdomain);
    if (appKey!.isNotEmpty && subDomain!.isNotEmpty) {
      anonymousLogin(appKey, subDomain);
    } else {
      debugPrint("请首先是anonymousLogin函数登录，并传递相应参数");
    }
  }

  // 匿名访客登录
  static void visitorLogin(String username, String appkey, String subDomain) {
    String password = username;
    loginVisitor(username, password, appkey, subDomain);
  }

  // 开发者自定义用户名登录接口
  static void userLogin(
      String username, String password, String appkey, String subDomain) {
    login(
        username, password, appkey, subDomain, BytedeskConstants.ROLE_VISITOR);
  }

  // 用户名登录
  static void login(String username, String password, String appkey,
      String subDomain, String role) async {
    //
    SpUtil.putString(BytedeskConstants.role, role);
    if (role == BytedeskConstants.ROLE_ADMIN) {
      if (!username.contains("@")) {
        username = "$username@$subDomain";
      }
    }
    await BytedeskUserHttpApi().oauth(username, password);
    // 登录成功之后，建立长连接
    connect();
    // if (role == BytedeskConstants.ROLE_ADMIN) {
    //   // TODO: 如果是客服账号，加载个人信息
    // }
    // 上传设备信息
    await BytedeskDeviceHttpApi().setDeviceInfo();
  }

  // 访客用户名登录
  static void loginVisitor(
      String username, String password, String appkey, String subDomain) async {
    //
    SpUtil.putString(BytedeskConstants.role, BytedeskConstants.ROLE_VISITOR);
    //
    await BytedeskUserHttpApi().visitorOauth(username, password);
    // 登录成功之后，建立长连接
    connect();
    // if (role == BytedeskConstants.ROLE_ADMIN) {
    //   // TODO: 如果是客服账号，加载个人信息
    // }
    // 上传设备信息
    await BytedeskDeviceHttpApi().setDeviceInfo();
  }

  // 微信unionid登录
  static void unionidOAuth(String unionid) async {
    //
    await BytedeskUserHttpApi().unionIdOAuth(unionid);
    // 登录成功之后，建立长连接
    connect();
    // 上传设备信息
    await BytedeskDeviceHttpApi().setDeviceInfo();
  }

  // 直接建立长连接
  static void otherOAuth() async {
    // 登录成功之后，建立长连接
    connect();
    // 上传设备信息
    await BytedeskDeviceHttpApi().setDeviceInfo();
  }

  // 建立长连接
  static bool connect() {
    return BytedeskUtils.mqttConnect();
  }

  // 重连
  static bool reconnect() {
    return BytedeskUtils.mqttReConnect();
  }

  // 断开
  static void disconnect() {
    BytedeskUtils.mqttDisconnect();
  }

  // 判断长连接状态
  static bool isConnected() {
    return BytedeskUtils.isMqttConnected();
  }

  // 技能组客服会话
  static void startWorkGroupChat(
      BuildContext context, String wid, String title) {
    startChatDefault(
        context, wid, BytedeskConstants.CHAT_TYPE_WORKGROUP, title, false);
  }

  static void startWorkGroupChatV2Robot(
      BuildContext context, String wid, String title) {
    startChatDefault(
        context, wid, BytedeskConstants.CHAT_TYPE_WORKGROUP, title, true);
  }

  // 发送附言消息
  static void startWorkGroupChatPostscript(
      BuildContext context, String wid, String title, String postScript) {
    startChat(context, wid, BytedeskConstants.CHAT_TYPE_WORKGROUP, title, '',
        postScript, false, null);
  }

  // 电商接口，携带商品参数
  // type/title/content/price/url/imageUrl/id/categoryCode
  static void startWorkGroupChatShop(
      BuildContext context, String wid, String title, String commodity) {
    startChatShop(context, wid, BytedeskConstants.CHAT_TYPE_WORKGROUP, title,
        commodity, null);
  }

  static void startWorkGroupChatShopCallback(BuildContext context, String wid,
      String title, String commodity, ValueSetter<String> customCallback) {
    startChatShop(context, wid, BytedeskConstants.CHAT_TYPE_WORKGROUP, title,
        commodity, customCallback);
  }

  // 指定客服会话
  static void startAppointedChat(
      BuildContext context, String uid, String title) {
    startChatDefault(
        context, uid, BytedeskConstants.CHAT_TYPE_APPOINTED, title, false);
  }

  // 发送附言消息
  static void startAppointedChatPostscript(
      BuildContext context, String uid, String title, String postScript) {
    startChat(context, uid, BytedeskConstants.CHAT_TYPE_APPOINTED, title, '',
        postScript, false, null);
  }

  // 电商接口，携带商品参数
  static void startAppointedChatShop(
      BuildContext context, String uid, String title, String commodity) {
    startChatShop(context, uid, BytedeskConstants.CHAT_TYPE_APPOINTED, title,
        commodity, null);
  }

  static void startAppointedChatShopCallback(BuildContext context, String uid,
      String title, String commodity, ValueSetter<String> customCallback) {
    startChatShop(context, uid, BytedeskConstants.CHAT_TYPE_APPOINTED, title,
        commodity, customCallback);
  }

  // 默认设置商品信息和附言为空
  static void startChatDefault(BuildContext context, String uuid, String type,
      String title, bool isV2Robot) {
    startChat(context, uuid, type, title, '', '', isV2Robot, null);
  }

  // 电商对话-自定义类型(技能组、指定客服)
  static void startChatShop(BuildContext context, String uuid, String type,
      String title, String commodity, ValueSetter<String>? customCallback) {
    startChat(context, uuid, type, title, commodity, '', false, customCallback);
  }

  // 发送附言消息-自定义类型(技能组、指定客服)
  static void startChatPostscript(BuildContext context, String uuid,
      String type, String title, String postScript) {
    startChat(context, uuid, type, title, '', postScript, false, null);
  }

  // 客服会话-自定义类型(技能组、指定客服)
  static void startChat(
      BuildContext context,
      String uuid,
      String type,
      String title,
      String commodity,
      String postScript,
      bool isV2Robot,
      ValueSetter<String>? customCallback) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return ChatKFProvider(
        wid: uuid,
        aid: uuid,
        type: type,
        title: title,
        custom: commodity,
        postscript: postScript,
        isV2Robot: isV2Robot,
        customCallback: customCallback,
      );
    }));
  }

  // 从历史会话或者点击通知栏进入
  static void startChatThread(BuildContext context, Thread thread,
      {String title = ''}) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return ChatThreadProvider(
        thread: thread,
        title: title,
      );
    }));
  }

  // 应用内打开H5客服页面
  static void startH5Chat(BuildContext context, String url, String title) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return ChatWebViewPage(
        url: url,
        title: title,
      );
    }));
  }

  // 应用内打开网页
  static void openWebView(BuildContext context, String url, String title) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return ChatWebViewPage(
        url: url,
        title: title,
      );
    }));
  }

  // TODO: 好友一对一聊天
  static void startContactChat(BuildContext context, String cid, String title) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return ChatIMProvider(
        // cid: cid,
        title: title,
      );
    }));
  }

  // TODO: 好友一对一，携带商品参数
  // static void startContactChatShop(
  //     BuildContext context, String cid, String title, String commodity) {
  //   Navigator.of(context).push(new MaterialPageRoute(builder: (context) {
  //     return new ChatIMProvider(
  //       // cid: cid,
  //       title: title,
  //       custom: commodity,
  //     );
  //   }));
  // }

  // TODO: 客服端-进入接待访客对话页面
  static void startChatThreadIM(BuildContext context, Thread thread) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return ChatIMProvider(
        thread: thread,
        isThread: true,
      );
    }));
  }

  // 常见问题列表
  static void showFaq(BuildContext context, String uid, String title) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return HelpProvider(
        uid: uid,
        title: title,
      );
    }));
  }

  // 意见反馈
  static void showFeedback(BuildContext context, String uid) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return FeedbackProvider(uid: uid);
    }));
  }

  // TODO: 提交工单
  static void showTicket(BuildContext context, String uid) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return TicketProvider(uid: uid);
    }));
  }

  static void showWorkGroupLeaveMessage(
      BuildContext context, String wid, String tip) {
    showLeaveMessage(
        context, wid, wid, BytedeskConstants.CHAT_TYPE_WORKGROUP, tip);
  }

  static void showAppointedLeaveMessage(
      BuildContext context, String aid, String tip) {
    showLeaveMessage(
        context, aid, aid, BytedeskConstants.CHAT_TYPE_APPOINTED, tip);
  }

  // 留言
  static void showLeaveMessage(
      BuildContext context, String wid, String aid, String type, String tip) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return LeaveMsgProvider(
        wid: wid,
        aid: aid,
        type: type,
        tip: tip,
      );
    }));
  }

  // 留言历史
  static void showLeaveMessageHistory(
      BuildContext context, String wid, String aid, String type) {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return LeaveMsgHistoryProvider(
        wid: wid,
        aid: aid,
        type: type,
      );
    }));
  }

  // TODO: 提交工单
  // 频道消息
  // static void showChannel(BuildContext context, Thread thread) {
  //   Navigator.of(context).push(new MaterialPageRoute(builder: (context) {
  //     return new ChannelProvider(
  //       thread: thread,
  //     );
  //   }));
  // }

  // 获取个人资料
  static Future<User> getProfile() async {
    return BytedeskUserHttpApi().getProfile();
  }

  // 设置用户昵称
  static Future<User> updateNickname(String nickname) async {
    return BytedeskUserHttpApi().updateNickname(nickname);
  }

  // 设置用户头像
  static Future<User> updateAvatar(String avatar) async {
    return BytedeskUserHttpApi().updateAvatar(avatar);
  }

  // 设置用户备注
  static Future<User> updateDescription(String description) async {
    return BytedeskUserHttpApi().updateDescription(description);
  }

  // 一个接口，设置：昵称、头像、备注
  static Future<User> updateProfile(
      String nickname, String avatar, String description) async {
    return BytedeskUserHttpApi().updateProfile(nickname, avatar, description);
  }

  // 查询技能组在线状态
  static Future<String> getWorkGroupStatus(String workGroupWid) async {
    return BytedeskUserHttpApi().getWorkGroupStatus(workGroupWid);
  }

  // 查询客服在线状态
  static Future<String> getAgentStatus(String agentUid) async {
    return BytedeskUserHttpApi().getAgentStatus(agentUid);
  }

  // 查询当前用户-某技能组wid或指定客服未读消息数目
  static Future<String> getUnreadCount(String wid) async {
    return BytedeskUserHttpApi().getUnreadCount(wid);
  }

  // 访客端-查询访客所有未读消息数目
  static Future<String> getUnreadCountVisitor() async {
    return BytedeskUserHttpApi().getUnreadCountVisitor();
  }

  // 客服端-查询客服所有未读消息数目
  static Future<String> getUnreadCountAgent() async {
    return BytedeskUserHttpApi().getUnreadCountAgent();
  }

  // 访客历史会话
  static Future<List<Thread>> getVisitorThreads(int page, int size) async {
    return BytedeskThreadHttpApi().getVisitorThreads(page, size);
  }

  // 消息提示设置
  static bool? getPlayAudioOnSendMessage() {
    return SpUtil.getBool(BytedeskConstants.PLAY_AUDIO_ON_SEND_MESSAGE,
        defValue: true);
  }

  static void setPlayAudioOnSendMessage(bool flag) {
    SpUtil.putBool(BytedeskConstants.PLAY_AUDIO_ON_SEND_MESSAGE, flag);
  }

  static bool? getPlayAudioOnReceiveMessage() {
    return SpUtil.getBool(BytedeskConstants.PLAY_AUDIO_ON_RECEIVE_MESSAGE,
        defValue: true);
  }

  static void setPlayAudioOnReceiveMessage(bool flag) {
    SpUtil.putBool(BytedeskConstants.PLAY_AUDIO_ON_RECEIVE_MESSAGE, flag);
  }

  static bool? getVibrateOnReceiveMessage() {
    return SpUtil.getBool(BytedeskConstants.VIBRATE_ON_RECEIVE_MESSAGE,
        defValue: true);
  }

  static void setVibrateOnReceiveMessage(bool flag) {
    SpUtil.putBool(BytedeskConstants.VIBRATE_ON_RECEIVE_MESSAGE, flag);
  }

  // 上传ios devicetoken
  static Future<void> uploadIOSDeviceToken(
      String appkey, String deviceToken) async {
    BytedeskDeviceHttpApi()
        .updateIOSDeviceToken(appkey, BytedeskConstants.build, deviceToken);
  }

  // build的值只有两种情况，测试设置为：debug 或 线上设置为：release
  // static Future<void> uploadIOSDeviceToken(
  //     String appkey, String build, String deviceToken) async {
  //   BytedeskDeviceHttpApi().updateIOSDeviceToken(appkey, build, deviceToken);
  // }

  // build的值只有两种情况，测试设置为：debug 或 线上设置为：release
  static Future<void> deleteIOSDeviceToken(String build) async {
    BytedeskDeviceHttpApi().deleteIOSDeviceToken(build);
  }

  // The package version. `CFBundleShortVersionString` on iOS, `versionName` on Android.
  static Future<String> getAppVersion() {
    return BytedeskDeviceHttpApi().getAppVersion();
  }

  // The build number. `CFBundleVersion` on iOS, `versionCode` on Android.
  static Future<String> getAppBuildNumber() {
    return BytedeskDeviceHttpApi().getAppBuildNumber();
  }

  // 从服务器检测当前APP是否有新版
  static Future<App> checkAppVersion(String androidKey, String iosKey) {
    if (BytedeskUtils.isWeb) {
      // FIXME: 仅用于占位，待修改
      return BytedeskUserHttpApi().checkAppVersion(iosKey);
    } else if (BytedeskUtils.isAndroid) {
      return BytedeskUserHttpApi().checkAppVersion(androidKey);
    }
    return BytedeskUserHttpApi().checkAppVersion(iosKey);
  }

  // 从服务器检测当前APP是否有新版
  static Future<App> checkAppVersion2(String flutterKey) {
    return BytedeskUserHttpApi().checkAppVersion(flutterKey);
  }

  // 退出登录
  static Future<void> logout() {
    // 断开长链接
    disconnect();
    // 通知服务器，并清空本地数据
    return BytedeskUserHttpApi().logout();
  }

  // 以下为专用接口，普通用户请忽略

  // 通过token获取手机号-良师app-专用
  static Future<String> getAliyunOneKeyLoginMobile(String token) async {
    return BytedeskUserHttpApi().getAliyunOneKeyLoginMobile(token);
  }

  // 匿名登录之后，绑定手机号-良师app-专用
  static Future<JsonResult> bindMobile(String mobile) async {
    return BytedeskUserHttpApi().bindMobile(mobile);
  }

  // 微信登录之后，获取微信用户信息-良师app-专用
  static Future<WeChatResult> getWechatUserinfo(String code) async {
    return BytedeskUserHttpApi().getWechatUserinfo(code);
  }

  // 手机端注册微信登录用户-并绑定手机号-良师app-专用
  static Future<UserJsonResult> registerWechatMobile(String mobile,
      String nickname, String avatar, String unionid, String openid) async {
    return BytedeskUserHttpApi()
        .registerWechatMobile(mobile, nickname, avatar, unionid, openid);
  }

  // 将unionid绑定到已经存在的手机账号-良师app-专用
  static Future<UserJsonResult> bindWeChatMobile(
      String mobile, String unionid) async {
    return BytedeskUserHttpApi().bindWeChatMobile(mobile, unionid);
  }
}
