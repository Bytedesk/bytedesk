// ignore_for_file: prefer_typing_uninitialized_variables

import 'dart:convert';

import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:bytedesk_kefu/model/message.dart';
import 'package:bytedesk_kefu/model/thread.dart';
import 'package:bytedesk_kefu/model/user.dart';
// TODO: 条件引入web，格式类似：import 'src/configure_imp.dart' if (dart.library.html) 'src/configure_web.dart' as conf;
// ignore: uri_does_not_exist
import 'package:bytedesk_kefu/mqtt/lib/mqtt_browser_client.dart';
import 'package:bytedesk_kefu/mqtt/lib/mqtt_client.dart';
import 'package:bytedesk_kefu/mqtt/lib/mqtt_server_client.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/model/messageProvider.dart';
import 'package:bytedesk_kefu/util/bytedesk_events.dart';
import 'package:bytedesk_kefu/util/bytedesk_extraparam.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:bytedesk_kefu/util/bytedesk_uuid.dart';
import 'package:flutter/material.dart';
import 'package:sp_util/sp_util.dart';
// FIXME: proto文件重新生成兼容null-safty报错
// ignore: import_of_legacy_library_into_null_safe
import 'package:bytedesk_kefu/protobuf/message.pb.dart' as protomsg;
// ignore: import_of_legacy_library_into_null_safe
import 'package:bytedesk_kefu/protobuf/thread.pb.dart' as protothread;
// ignore: import_of_legacy_library_into_null_safe
import 'package:bytedesk_kefu/protobuf/user.pb.dart' as protouser;
import 'package:flutter/services.dart';
// import 'package:fluttertoast/fluttertoast.dart';
// import 'package:vibration/vibration.dart';

class BytedeskMqtt {
  //
  var mqttClient;
  String? clientId;
  int keepAlivePeriod = 20;
  // final key = Key.fromUtf8('16BytesLengthKey');
  // final iv = IV.fromUtf8('A-16-Byte-String');
  MessageProvider messageProvider = MessageProvider();
  // List<String> midList = [];
  // bool _isConnected = false;
  String? currentUid;
  String? client;

  // 单例模式
  static final BytedeskMqtt _singleton = BytedeskMqtt._internal();
  factory BytedeskMqtt() {
    return _singleton;
  }
  BytedeskMqtt._internal();

  void connect() async {
    // eventbus发送广播，连接中...
    bytedeskEventBus
        .fire(ConnectionEventBus(BytedeskConstants.USER_STATUS_CONNECTING));
    //
    currentUid = SpUtil.getString(BytedeskConstants.uid);
    client = BytedeskUtils.getClient();
    clientId = "$currentUid/$client";

    //注意：必须要先判断web，否则在web运行会报错：
    //  Unsupported operation: Platform._operatingSystem
    if (BytedeskUtils.isWeb) {
      if (BytedeskConstants.isDebug) {
        mqttClient = MqttBrowserClient.withPort(
            'ws://127.0.0.1/websocket', clientId!, 3885);
      } else {
        mqttClient = MqttBrowserClient.withPort(
            BytedeskConstants.webSocketWssUrl, clientId!, 443);
      }
    } else {
      if (BytedeskConstants.isWebSocketWss) {
        mqttClient =
            MqttServerClient(BytedeskConstants.webSocketWssUrl, clientId!);
        mqttClient.useWebSocket = true;
        mqttClient.port = 443;

        /// You can also supply your own websocket protocol list or disable this feature using the websocketProtocols
        /// setter, read the API docs for further details here, the vast majority of brokers will support the client default
        /// list so in most cases you can ignore this. Mosquito needs the single default setting.
        // mqttClient.websocketProtocols = MqttClientConstants.protocolsSingleDefault;
      } else {
        mqttClient = MqttServerClient(BytedeskConstants.mqttHost, clientId!);
        mqttClient.port = BytedeskConstants.mqttPort;
        mqttClient.secure = BytedeskConstants.isSecure;
      }
    }

    // 启用3.1.1版本协议，否则clientId限制最大长度为23
    mqttClient.setProtocolV311();

    /// Set logging on if needed, defaults to off
    // mqttClient.logging(on: BytedeskConstants.isDebug);
    mqttClient.logging(on: false); // BytedeskConstants.isDebug
    /// If you intend to use a keep alive value in your connect message that is not the default(60s)
    /// you must set it here
    mqttClient.keepAlivePeriod = keepAlivePeriod;
    mqttClient.autoReconnect = true; // FIXME:
    mqttClient.onAutoReconnect = _onAutoReconnect; // FIXME:
    mqttClient.onDisconnected = _onDisconnected;
    mqttClient.onConnected = _onConnected;
    mqttClient.onSubscribed = _onSubscribed;
    mqttClient.onUnsubscribed = _onUnSubscribed;
    mqttClient.onSubscribeFail = _onSubscribeFailed;

    /// Set a ping received callback if needed, called whenever a ping response(pong) is received from the broker.
    mqttClient.pongCallback = _onPong;

    /// Create a connection message to use or use the default one. The default one sets the
    /// client identifier, any supplied username/password, the default keepalive interval(60s)
    /// and clean session, an example of a specific one below.

    final MqttConnectMessage connMessage = MqttConnectMessage()
        .withClientIdentifier(clientId!)
        .authenticateAs('username', 'password'); // TODO: 服务器暂时不需要auth，随便填写
    // .keepAliveFor(keepAlivePeriod); // Must agree with the keep alive set above or not set
    // 取消客户端设置，直接在服务器端统一内容格式推送
    // .withWillTopic('protobuf/lastWill/mqtt') // If you set this you must set a will message
    // .withWillMessage('My Will message')
    // .startClean() // Non persistent session for testing
    // .withWillQos(MqttQos.atLeastOnce);
    debugPrint('mqttClient connecting....');
    mqttClient.connectionMessage = connMessage;

    /// Connect the client, any errors here are communicated by raising of the appropriate exception. Note
    /// in some circumstances the broker will just disconnect us, see the spec about this, we however eill
    /// never send malformed messages.
    try {
      await mqttClient.connect();
    } on Exception catch (e) {
      debugPrint('mqttClient exception - $e');
      mqttClient.disconnect();
    }

    /// Check we are connected
    if (mqttClient.connectionStatus.state == MqttConnectionState.connected) {
      debugPrint('mqttClient connected');
    } else {
      /// Use status here rather than state if you also want the broker return code.
      debugPrint(
          'ERROR mqttClient connection failed - disconnecting, status is ${mqttClient.connectionStatus}');
      mqttClient.disconnect();
      // exit(-1);
    }

    /// The client has a change notifier object(see the Observable class) which we then listen to to get
    /// notifications of published updates to each subscribed topic.
    // mqttClient.updates!.listen((List<MqttReceivedMessage<MqttMessage>> c) {
    //   final MqttPublishMessage recMess = c[0].payload;
    //   final String pt =
    //       MqttPublishPayload.bytesToStringAsString(recMess.payload.message);
    //   // debugPrint('Change notification:: topic is <${c[0].topic}>, payload is <-- $pt -->');
    // });

    /// 收到的消息
    // if (mqttClient != null && mqttClient.published != null) {
    //
    mqttClient.published!.listen((MqttPublishMessage messageBinary) {
      // debugPrint('Published notification');
      //
      protomsg.Message messageProto =
          protomsg.Message.fromBuffer(messageBinary.payload.message);
      // FIXME: 自己发送的消息显示两条？此处根据mid去个重
      var mid = messageProto.mid;
      // if (midList.contains(mid)) {
      //   return;
      // }
      // midList.add(mid);
      //
      var uid = messageProto.user.uid;
      var username = messageProto.user.username;
      var nickname = messageProto.user.nickname;
      var avatar = messageProto.user.avatar;
      //
      var content = '';
      var type = messageProto.type;
      var timestamp = messageProto.timestamp;
      var client = messageProto.client;
      //
      debugPrint(
          'bytedesk_mqtt.dart receive type:$type client:$client mid:$mid');
      // 非会话消息，如：会议通知等, 另行处理
      if (type == BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_NOTICE) {
        // TODO: 待处理
        return;
      }
      //
      User user = User(
          uid: uid, username: username, nickname: nickname, avatar: avatar);
      //
      Thread thread = Thread(
          tid: messageProto.thread.tid,
          type: messageProto.thread.type,
          // uid: '',
          nickname: messageProto.thread.nickname,
          avatar: messageProto.thread.avatar,
          content: messageProto.thread.content,
          timestamp: messageProto.thread.timestamp,
          unreadCount: messageProto.thread.unreadCount,
          topic: messageProto.thread.topic,
          client: client);
      Message message = Message(
          mid: mid,
          content: content,
          imageUrl: content,
          nickname: nickname,
          avatar: avatar,
          type: type,
          timestamp: timestamp,
          status: 'stored',
          isSend: uid == currentUid ? 1 : 0,
          currentUid: currentUid,
          thread: thread,
          user: user,
          client: client);
      // 是否发送消息回执
      // var autoReply = false;
      var sendReceipt = false;
      // var webRTCVideoInvite = false;
      // var webRTCAudioInvite = false;
      switch (type) {
        case BytedeskConstants.MESSAGE_TYPE_TEXT:
          {
            //
            // autoReply = true;
            sendReceipt = true;
            // TODO: 判断是否加密，暂时不需要
            message.content = messageProto.text.content;
            // debugPrint('receive text:${message.content!}');
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_IMAGE:
          {
            //
            // autoReply = true;
            sendReceipt = true;
            message.imageUrl = messageProto.image.imageUrl;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_VOICE:
          {
            //
            // autoReply = true;
            sendReceipt = true;
            message.voiceUrl = messageProto.voice.voiceUrl;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_FILE:
          {
            //
            // autoReply = true;
            sendReceipt = true;
            message.fileUrl = messageProto.file.fileUrl;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_VIDEO:
        case BytedeskConstants.MESSAGE_TYPE_SHORT_VIDEO:
          {
            //
            // autoReply = true;
            sendReceipt = true;
            message.videoUrl = messageProto.video.videoOrShortUrl;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_THREAD:
          {
            message.content = messageProto.text.content;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_THREAD_REENTRY:
        case BytedeskConstants.MESSAGE_TYPE_COMMODITY:
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_CONNECT:
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_DISCONNECT:
          {
            message.content = messageProto.text.content;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_QUEUE:
          {
            message.content = messageProto.text.content;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_QUEUE_ACCEPT:
          {
            // 替换 'joinQueueThread'
            message.content = '接入队列会话'; // TODO: 国际化
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_AGENT_CLOSE:
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_VISITOR_CLOSE:
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_AUTO_CLOSE:
          {
            message.content = messageProto.text.content;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_TICKET:
          {
            // TODO: 工单消息
            message.content = messageProto.text.content;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_FEEDBACK:
          {
            // TODO: 意见反馈
            message.content = messageProto.text.content;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_CHANNEL:
          {
            // TODO: 渠道消息
            message.content = messageProto.text.content;
            break;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_PREVIEW:
          {
            // 对方正在输入
            String uid = messageProto.user.uid;
            String previewContent = messageProto.preview.content;
            if (uid != currentUid) {
              bytedeskEventBus
                  .fire(ReceiveMessagePreviewEventBus(previewContent));
            }
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_RECEIPT:
          {
            // 消息回执
            var midR = messageProto.receipt.mid;
            var statusR = messageProto.receipt.status;
            messageProvider.update(midR, statusR);
            // 通知界面更新聊天记录状态
            bytedeskEventBus.fire(ReceiveMessageReceiptEventBus(midR, statusR));
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_RECALL:
          {
            // 消息撤回
            String mid = messageProto.recall.mid;
            bytedeskEventBus.fire(DeleteMessageEventBus(mid));
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_TRANSFER:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_INVITE:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_INVITE_RATE:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_RATE_RESULT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_VIDEO:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_AUDIO:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANCEL:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_VIDEO:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_AUDIO:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_ANSWER:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANDIDATE:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_ACCEPT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_REJECT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_READY:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_BUSY:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_CLOSE:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_CREATE:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_UPDATE:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_ANNOUNCEMENT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE_ACCEPT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE_REJECT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_APPROVE:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_DENY:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_KICK:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_MUTE:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_ACCEPT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_REJECT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_WITHDRAW:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_DISMISS:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_LOCATION:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_LINK:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_EVENT:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_CUSTOM:
          {
            //
            return;
          }
        case BytedeskConstants.MESSAGE_TYPE_RED_PACKET:
          {
            //
            return;
          }
        default:
          debugPrint('other message type:$type');
      }
      // 客服端使用
      // if (autoReply) {
      //   //
      // }
      // 自己发送的消息、智谱AI返回信息不发送回执
      if (sendReceipt &&
          message.isSend == 0 &&
          thread.type != BytedeskConstants.THREAD_TYPE_ZHIPUAI) {
        // 发送送达回执
        sendReceiptReceivedMessage(mid, thread);
      }
      //
      // final encrypter = Encrypter(AES(key, mode: AESMode.cbc));
      // final decrypted = encrypter.decrypt64(messageProto.text.content, iv: iv);
      // debugPrint('content: ' + decrypted);
      //
      // 忽略连接信息
      if (message.content == 'visitorConnect' ||
          message.content == 'visitorDisconnect') {
        return;
      }
      // 插入本地数据库
      // if (messageProvider != null) {
      messageProvider.insert(message);
      // }
      // 通知界面显示聊天记录
      bytedeskEventBus.fire(ReceiveMessageEventBus(message));
      // 接收消息播放提示音，放到SDK外实现，迁移到demo中
      if (BytedeskKefu.getPlayAudioOnReceiveMessage()! && message.isSend == 0) {
        // debugPrint('play audio');
        SystemSound.play(SystemSoundType.click);
      }
      // 振动，放到SDK外实现，迁移到demo中
      if (BytedeskKefu.getVibrateOnReceiveMessage()! && message.isSend == 0) {
        // debugPrint('should vibrate');
        vibrate();
      }
    });
    //
    // } else {
    //   debugPrint('mqttClient.published is null');
    // }
  }

  // FIXME: ld: library not found for -lvibration
  void vibrate() async {
    // if (await Vibration.hasVibrator()) {
    //   Vibration.vibrate();
    // }
  }

  void subscribe(String topic) {
    // debugPrint('Subscribing to the hello topic');
    mqttClient.subscribe(topic, MqttQos.exactlyOnce);
  }

  void unsubscribe(String topic) {
    mqttClient.unsubscribe(topic);
  }

  void sendTextMessage(String content, Thread currentThread) {
    publish(content, BytedeskConstants.MESSAGE_TYPE_TEXT, currentThread, null);
  }

  void sendImageMessage(String content, Thread currentThread) {
    publish(content, BytedeskConstants.MESSAGE_TYPE_IMAGE, currentThread, null);
  }

  void sendFileMessage(String content, Thread currentThread) {
    publish(content, BytedeskConstants.MESSAGE_TYPE_FILE, currentThread, null);
  }

  void sendVoiceMessage(String content, Thread currentThread) {
    publish(content, BytedeskConstants.MESSAGE_TYPE_VOICE, currentThread, null);
  }

  void sendVideoMessage(String content, Thread currentThread) {
    publish(content, BytedeskConstants.MESSAGE_TYPE_VIDEO, currentThread, null);
  }

  // 商品消息
  void sendCommodityMessage(String content, Thread currentThread) {
    publish(
        content, BytedeskConstants.MESSAGE_TYPE_COMMODITY, currentThread, null);
  }

  // 消息预知
  void sendPreviewMessage(String previewContent, Thread currentThread) {
    publish(previewContent, BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_PREVIEW,
        currentThread, null);
  }

  // 消息撤回
  void sendRecallMessage(String mid, Thread currentThread) {
    ExtraParam extraParam = ExtraParam();
    extraParam.recallMid = mid;
    publish(mid, BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_RECALL,
        currentThread, extraParam);
  }

  // 送达回执
  void sendReceiptReceivedMessage(String mid, Thread currentThread) {
    sendReceiptMessage(
        mid, BytedeskConstants.MESSAGE_STATUS_RECEIVED, currentThread);
  }

  // 已读回执
  void sendReceiptReadMessage(String mid, Thread currentThread) {
    sendReceiptMessage(
        mid, BytedeskConstants.MESSAGE_STATUS_READ, currentThread);
  }

  void sendReceiptMessage(String mid, String status, Thread currentThread) {
    ExtraParam extraParam = ExtraParam();
    extraParam.receiptMid = mid;
    extraParam.receiptStatus = status;
    publish('content', BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_RECEIPT,
        currentThread, extraParam);
  }

  void publish(String content, String type, Thread currentThread,
      ExtraParam? extraParam) {
    // if (currentThread == null) {
    //   debugPrint('连接客服失败,请退出页面重新进入。注意: 请在App启动的时候，调用init接口');
    //   Fluttertoast.showToast(msg: '连接客服失败,请退出页面重新进入');
    //   return;
    // }
    // https://pub.dev/packages/encrypt#aes
    // final encrypter = Encrypter(AES(key, mode: AESMode.cbc));
    // final encrypted = encrypter.encrypt(content, iv: iv);
    // BytedeskUtils.printLog(encrypted.base64);
    // final decrypted = encrypter.decrypt(encrypted, iv: iv);
    // BytedeskUtils.printLog(decrypted);
    // thread
    protothread.Thread thread = protothread.Thread();
    thread.tid = currentThread.tid!;
    thread.type = currentThread.type!;
    thread.topic = currentThread.topic!;
    thread.nickname = currentThread.nickname!;
    thread.avatar = currentThread.avatar!;
    thread.client = currentThread.client!;
    thread.timestamp = BytedeskUtils.formatedDateNow(); // 没有必要填写，服务器端会填充
    thread.unreadCount = 0;
    var extra = {'top': false, 'undisturb': false};
    thread.extra = jsonEncode(extra);
    // user
    protouser.User user = protouser.User();
    user.uid = SpUtil.getString(BytedeskConstants.uid)!;
    user.username = SpUtil.getString(BytedeskConstants.username)!;
    user.nickname = SpUtil.getString(BytedeskConstants.nickname)!;
    user.avatar = SpUtil.getString(BytedeskConstants.avatar)!;
    // msg
    protomsg.Message messageProto = protomsg.Message();
    messageProto.mid = BytedeskUuid.uuid();
    messageProto.type = type;
    messageProto.timestamp = BytedeskUtils.formatedDateNow();
    messageProto.client = BytedeskUtils.getClient(); //BytedeskConstants.client;
    messageProto.version = '1';
    messageProto.encrypted = false;
    // 用来在发送之前显示到界面
    Message message = Message();
    message.mid = messageProto.mid;
    message.type = messageProto.type;
    message.timestamp = messageProto.timestamp;
    message.client = messageProto.client;
    message.nickname = user.nickname;
    message.avatar = user.avatar;
    message.topic = thread.topic;
    message.status = BytedeskConstants.MESSAGE_STATUS_SENDING;
    message.isSend = 1;
    message.currentUid = currentUid;
    message.answersJson = '';
    message.thread = currentThread;
    message.user =
        User(uid: user.uid, avatar: user.avatar, nickname: user.nickname);
    // 判断是否应该插入本地并显示
    bool shouldInsertLocal = false;
    // 发送protobuf
    if (type == BytedeskConstants.MESSAGE_TYPE_TEXT) {
      protomsg.Text text = protomsg.Text();
      text.content = content;
      messageProto.text = text;
      //
      thread.content = content;
      //
      shouldInsertLocal = true;
      message.content = content;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_IMAGE) {
      protomsg.Image image = protomsg.Image();
      image.imageUrl = content;
      messageProto.image = image;
      //
      thread.content = '[图片]';
      //
      shouldInsertLocal = true;
      message.imageUrl = content;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_VOICE) {
      protomsg.Voice voice = protomsg.Voice();
      voice.voiceUrl = content;
      // voice.length
      // voice.format
      messageProto.voice = voice;
      //
      thread.content = '[语音]';
      //
      shouldInsertLocal = true;
      message.voiceUrl = content;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_FILE) {
      protomsg.File file = protomsg.File();
      file.fileUrl = content;
      messageProto.file = file;
      //
      thread.content = '[文件]';
      //
      shouldInsertLocal = true;
      message.fileUrl = content;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_VIDEO ||
        type == BytedeskConstants.MESSAGE_TYPE_SHORT_VIDEO) {
      protomsg.Video video = protomsg.Video();
      video.videoOrShortUrl = content;
      messageProto.video = video;
      //
      thread.content = '[视频]';
      //
      shouldInsertLocal = true;
      message.videoUrl = content;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_COMMODITY) {
      protomsg.Text text = protomsg.Text();
      text.content = content;
      messageProto.text = text;
      //
      thread.content = '[商品]';
      //
      shouldInsertLocal = true;
      message.content = content;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_PREVIEW) {
      protomsg.Preview preview = protomsg.Preview();
      preview.content = content;
      messageProto.preview = preview;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_RECEIPT) {
      // 发送消息回执
      protomsg.Receipt receipt = protomsg.Receipt();
      receipt.mid = extraParam!.receiptMid!;
      receipt.status = extraParam.receiptStatus!;
      //
      messageProto.receipt = receipt;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_RECALL) {
      //
      protomsg.Recall recall = protomsg.Recall();
      recall.mid = extraParam!.recallMid!;
      //
      messageProto.recall = recall;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_TRANSFER) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT) {
      //
      return;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_INVITE) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_INVITE_RATE) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_RATE_RESULT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_VIDEO) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_AUDIO) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANCEL) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_VIDEO) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_AUDIO) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_ANSWER) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANDIDATE) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_ACCEPT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_REJECT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_READY) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_BUSY) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_WEBRTC_CLOSE) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_CREATE) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_UPDATE) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_ANNOUNCEMENT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE_ACCEPT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE_REJECT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_APPROVE) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_DENY) {
      //
      return;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_KICK) {
      //
      return;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_MUTE) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_ACCEPT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_REJECT) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_WITHDRAW) {
      //
      return;
    } else if (type ==
        BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_GROUP_DISMISS) {
      //
      return;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_LOCATION) {
      //
      return;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_LINK) {
      //
      return;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_EVENT) {
      //
      return;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_CUSTOM) {
      //
      return;
    } else if (type == BytedeskConstants.MESSAGE_TYPE_RED_PACKET) {
      //
      return;
    } else {
      // 其他类型消息
      debugPrint('other type:$type');
    }
    //
    messageProto.user = user;
    messageProto.thread = thread;
    // 先通知界面本地显示聊天记录，然后再发送
    if (shouldInsertLocal) {
      // 插入本地数据库
      // if (messageProvider != null) {
      messageProvider.insert(message);
      // }
      bytedeskEventBus.fire(ReceiveMessageEventBus(message));
      // midList.add(message.mid!);
    }
    //
    final MqttClientPayloadBuilder builder = MqttClientPayloadBuilder();
    // 注意：此函数为自行添加，原先库中没有
    builder.addProtobuf(messageProto.writeToBuffer());
    mqttClient.publishMessage(
        currentThread.topic, MqttQos.exactlyOnce, builder.payload);
    // 播放发送消息提示音，放到SDK外实现，迁移到demo中
    if (BytedeskKefu.getPlayAudioOnSendMessage()!) {
      //
    }
  }

  // 断开长连接
  void disconnect() {
    if (mqttClient != null) {
      mqttClient.disconnect();
    }
  }

  /// The subscribed callback
  void _onSubscribed(String? topic) {
    debugPrint('Subscription confirmed for topic $topic');
  }

  /// The unsubscribed callback
  void _onUnSubscribed(String? topic) {
    debugPrint('UnSubscription confirmed for topic $topic');
  }

  /// The subscribed callback
  void _onSubscribeFailed(String? topic) {
    debugPrint('Subscribe Failed confirmed for topic $topic');
  }

  /// The unsolicited disconnect callback
  void _onDisconnected() {
    // _isConnected = false;
    debugPrint('OnDisconnected client callback - Client disconnection');
    // eventbus发广播，通知长连接断开
    bytedeskEventBus
        .fire(ConnectionEventBus(BytedeskConstants.USER_STATUS_DISCONNECTED));
    // if (mqttClient.connectionStatus.returnCode == MqttConnectReturnCode.solicited) {
    //   debugPrint('OnDisconnected callback is solicited, this is correct');
    // }
    // 延时10s执行重连
    // Future.delayed(const Duration(seconds: 10), () {
    //   debugPrint('start reconnecting');
    //   // reconnect();
    // });
  }

  /// The unsolicited disconnect callback
  void _onAutoReconnect() {
    if (isConnected()) {
      return;
    }
    debugPrint(
        'EXAMPLE::onAutoReconnect client callback - Client auto reconnection sequence will start');
    connect();
  }

  /// The successful connect callback
  void _onConnected() {
    // _isConnected = true;
    debugPrint('OnConnected client callback - Client connection was sucessful');
    // TODO: eventbus发广播，通知长连接建立
    bytedeskEventBus
        .fire(ConnectionEventBus(BytedeskConstants.USER_STATUS_CONNECTED));
  }

  /// Pong callback
  void _onPong() {
    // debugPrint('Ping response client callback invoked');
  }

  bool isConnected() {
    // return _isConnected;
    return mqttClient.connectionStatus.state == MqttConnectionState.connected;
  }
}
