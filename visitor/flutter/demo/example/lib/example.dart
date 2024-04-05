import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_events.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
// import 'package:bytedesk_kefu_example/notification/custom_notification.dart';
import 'package:bytedesk_kefu_example/page/leave_message_page.dart';
import 'package:flutter/material.dart';
// import 'package:overlay_support/overlay_support.dart';

import 'page/chat_type_page.dart';
import 'page/history_thread_page.dart';
import 'page/online_status_page.dart';
import 'page/setting_page.dart';
import 'page/switch_user_page.dart';
import 'page/user_info_page.dart';

class ExamplePage extends StatefulWidget {
  const ExamplePage({Key? key}) : super(key: key);

  @override
  State<ExamplePage> createState() => _ExamplePageState();
}

class _ExamplePageState extends State<ExamplePage> {
  //
  String _title = '萝卜丝客服Demo(连接中...)';
  // 登录 萝卜丝客服管理后台-》客服-》客服账号-》在客服账号表中，唯一uid 列获取。
  // 如果 有多个客服账号，请务必使用 管理员账号的 唯一uid
  // 如图：https://bytedesk.oss-cn-shenzhen.aliyuncs.com/placeholder/kefuAdminUid.png
  final String _kefuAdminUid = "201808221551193"; // 管理员uid

  // AudioCache audioCache = AudioCache();
  // bool _isConnected = false;
  //

  @override
  void initState() {
    _listener();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_title),
        // elevation: 0,
      ),
      body: ListView(children: ListTile.divideTiles(
        context: context,
        tiles: [
          ListTile(
            title: const Text('联系客服'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              // 第二步：联系客服，完毕
              Navigator.of(context).push(MaterialPageRoute(builder: (context) {
                return const ChatTypePage();
              }));
            },
          ),
          ListTile(
            title: const Text('帮助中心'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              BytedeskKefu.showFaq(context, _kefuAdminUid, "常见问题");
            },
          ),
          ListTile(
            title: const Text('意见反馈'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              BytedeskKefu.showFeedback(context, _kefuAdminUid);
            },
          ),
          ListTile(
            title: const Text('自定义用户信息'), // 自定义用户资料，设置
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              // 需要首先调用anonymousLogin之后，再调用设置用户信息接口
              Navigator.of(context).push(MaterialPageRoute(builder: (context) {
                return const UserInfoPage();
              }));
            },
          ),
          ListTile(
            title: const Text('在线状态'), // 技能组或客服账号 在线状态
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              Navigator.of(context).push(MaterialPageRoute(builder: (context) {
                return const OnlineStatusPage();
              }));
            },
          ),
          ListTile(
            title: const Text('历史会话'), // 历史会话
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              Navigator.of(context).push(MaterialPageRoute(builder: (context) {
                return const HistoryThreadPage();
              }));
            },
          ),
          ListTile(
            title: const Text('客户留言'), // 留言
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              Navigator.of(context).push(MaterialPageRoute(builder: (context) {
                return const LeaveMessagePage();
              }));
            },
          ),
          // ListTile(
          //   title: Text('TODO:提交工单'),
          //   trailing: Icon(Icons.keyboard_arrow_right),
          //   onTap: () {
          //     debugPrint('ticket');
          //     // TODO: 提交工单
          //   },
          // ),
          // ListTile(
          //   title: Text('TODO:意见反馈'),
          //   trailing: Icon(Icons.keyboard_arrow_right),
          //   onTap: () {
          //     debugPrint('feedback');
          //     // TODO: 意见反馈
          //   },
          // ),
          ListTile(
            title: const Text('消息提示'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              Navigator.of(context).push(MaterialPageRoute(builder: (context) {
                return const SettingPage();
              }));
            },
          ),
          ListTile(
            title: const Text('切换用户'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              Navigator.of(context).push(MaterialPageRoute(builder: (context) {
                return const SwitchUserPage();
              }));
            },
          ),
          const ListTile(
            title: Text('技术支持: QQ-3群: 825257535'),
          )
        ],
      ).toList()),
    );
  }

  // 监听状态
  _listener() {
    // token过期，要求重新登录
    bytedeskEventBus.on<InvalidTokenEventBus>().listen((event) {
      debugPrint("InvalidTokenEventBus token过期，请重新登录");
      // 也即执行init初始接口 BytedeskKefu.init(appKey, subDomain);
    });
    // 监听连接状态
    bytedeskEventBus.on<ConnectionEventBus>().listen((event) {
      // debugPrint('长连接状态:' + event.content);
      if (event.content == BytedeskConstants.USER_STATUS_CONNECTING) {
        setState(() {
          _title = "萝卜丝客服Demo(连接中...)";
        });
      } else if (event.content == BytedeskConstants.USER_STATUS_CONNECTED) {
        setState(() {
          _title = "萝卜丝客服Demo(连接成功)";
        });
      } else if (event.content == BytedeskConstants.USER_STATUS_DISCONNECTED) {
        setState(() {
          _title = "萝卜丝客服Demo(连接断开)";
        });
      }
    });
    // 监听消息，开发者可在此决定是否振动或播放提示音声音
    bytedeskEventBus.on<ReceiveMessageEventBus>().listen((event) {
      // debugPrint('receive message:' + event.message.content);
      // 1. 首先将example/assets/audio文件夹中文件拷贝到自己项目；2.在自己项目pubspec.yaml中添加assets
      // 播放发送消息提示音
      if (BytedeskKefu.getPlayAudioOnSendMessage()! &&
          event.message.isSend == 1) {
        debugPrint('play send audio');
        // 修改为自己项目中语音文件路径
        // audioCache.play('audio/bytedesk_dingdong.wav');
      }
      if (event.message.isSend == 1) {
        // 自己发送的消息，直接返回
        return;
      }
      // 接收消息播放提示音
      if (BytedeskKefu.getPlayAudioOnReceiveMessage()! &&
          event.message.isSend == 0) {
        debugPrint('play receive audio');
        // audioCache.play('audio/bytedesk_dingdong.wav');
      }
      // 振动
      if (BytedeskKefu.getVibrateOnReceiveMessage()! &&
          event.message.isSend == 0) {
        debugPrint('should vibrate');
        // vibrate();
      }
      if (event.message.type == BytedeskConstants.MESSAGE_TYPE_TEXT) {
        debugPrint('文字消息: ${event.message.content!}');
        // 判断当前是否客服页面，如否，则显示顶部通知栏
        if (!BytedeskUtils.isCurrentChatKfPage()!) {
          // https://github.com/boyan01/overlay_support
          // showOverlayNotification((context) {
          //   return MessageNotification(
          //     avatar: event.message.user!.avatar!,
          //     nickname: event.message.user!.nickname!,
          //     content: event.message.content!,
          //     onReply: () {
          //       //
          //       OverlaySupportEntry.of(context)!.dismiss();
          //       // 进入客服页面，支持自定义页面标题
          //       BytedeskKefu.startChatThread(context, event.message.thread!,
          //           title: '客服会话');
          //     },
          //   );
          // }, duration: const Duration(milliseconds: 4000));
        }
      } else if (event.message.type == BytedeskConstants.MESSAGE_TYPE_IMAGE) {
        debugPrint('图片消息:${event.message.imageUrl!}');
      } else if (event.message.type == BytedeskConstants.MESSAGE_TYPE_VOICE) {
        debugPrint('语音消息:${event.message.voiceUrl!}');
      } else if (event.message.type == BytedeskConstants.MESSAGE_TYPE_VIDEO) {
        debugPrint('视频消息:${event.message.videoUrl!}');
      } else if (event.message.type == BytedeskConstants.MESSAGE_TYPE_FILE) {
        debugPrint('文件消息:${event.message.fileUrl!}');
      } else {
        debugPrint('其他类型消息:${event.message.type!}');
      }
    });
    // token过期
    // bytedeskEventBus.on<InvalidTokenEventBus>().listen((event) {
    //   // 执行重新初始化
    //   debugPrint('InvalidTokenEventBus, token过期');
    // });
  }

  // 振动
  void vibrate() async {
    // if (await Vibration.hasVibrator()) {
    //   Vibration.vibrate();
    // }
  }
}
