// ignore_for_file: use_build_context_synchronously, use_full_hex_values_for_flutter_colors

import 'dart:async';
import 'dart:convert';
import 'dart:typed_data';
// import 'dart:io';

import 'package:bytedesk_kefu/blocs/message_bloc/bloc.dart';
import 'package:bytedesk_kefu/blocs/thread_bloc/bloc.dart';
import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:bytedesk_kefu/model/messageProvider.dart';
import 'package:bytedesk_kefu/model/model.dart';
import 'package:bytedesk_kefu/mqtt/bytedesk_mqtt.dart';
import 'package:bytedesk_kefu/ui/chat/widget/message_widget.dart';
import 'package:bytedesk_kefu/ui/leavemsg/provider/leavemsg_provider.dart';
import 'package:bytedesk_kefu/ui/widget/expanded_viewport.dart';
// import 'package:bytedesk_kefu/ui/widget/image_choose_widget.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_events.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:bytedesk_kefu/util/bytedesk_uuid.dart';
// import 'package:file_picker/file_picker.dart';
import 'package:sp_util/sp_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
// import 'package:flutter_video_compress/flutter_video_compress.dart';
// import 'package:flutter_image_compress/flutter_image_compress.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:image_picker/image_picker.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
// import 'package:path_provider/path_provider.dart' as path_provider;
import 'package:bytedesk_kefu/ui/widget/chat_input.dart';
import 'package:bytedesk_kefu/ui/widget/extra_item.dart';

import '../../widget/send_button_visibility_mode.dart';
// import 'package:bytedesk_kefu/ui/widget/send_button_visibility_mode.dart';
// import 'package:bytedesk_kefu/ui/widget/voice_record/voice_widget.dart';
// import 'package:flutter_chat_ui/flutter_chat_ui.dart' as chat_ui;
// import 'package:flutter_chat_types/flutter_chat_types.dart' as types;

// TODO: 接通客服之前，在title显示loading
// 客服关闭会话，或者 自动关闭会话，则禁止继续发送消息
// 点击商品信息，回调接口-进入商品详情页面
// TODO: 右上角增加按钮回调入口，支持用户自定义按钮，进入店铺/学校详情页面
// TODO: 增加是否显示历史记录参数
// 系统消息居中显示
class ChatGPTPage extends StatefulWidget {
  //
  final String? wid;
  final String? aid;
  final String? type;
  final String? title;
  final String? custom;
  final String? postscript;
  final bool? isV2Robot;
  // 从历史会话或者点击通知栏进入
  final bool? isThread;
  final Thread? thread;
  final ValueSetter<String>? customCallback;
  //
  const ChatGPTPage(
      {Key? key,
      this.wid,
      this.aid,
      this.type,
      this.title,
      this.custom,
      this.postscript,
      this.isV2Robot,
      this.isThread,
      this.thread,
      this.customCallback})
      : super(key: key);
  //
  @override
  State<ChatGPTPage> createState() => _ChatGPTPageState();
}

class _ChatGPTPageState extends State<ChatGPTPage>
    with
        AutomaticKeepAliveClientMixin<ChatGPTPage>,
        TickerProviderStateMixin,
        WidgetsBindingObserver {
  //
  String? _title;
  // 下拉刷新
  final RefreshController _refreshController = RefreshController();
  // 输入文字
  final TextEditingController _textController = TextEditingController();
  // 滚动监听
  final ScrollController _scrollController = ScrollController();
  // 聊天记录本地存储
  final MessageProvider _messageProvider = MessageProvider();
  // 聊天记录内存存储
  final List<MessageWidget> _messages = <MessageWidget>[];
  // 图片
  final ImagePicker _picker = ImagePicker();
  // 长连接
  final BytedeskMqtt _bdMqtt = BytedeskMqtt();
  // 当前用户uid
  final String? _currentUid = SpUtil.getString(BytedeskConstants.uid);
  final String? _currentUsername = SpUtil.getString(BytedeskConstants.username);
  final String? _currentNickname = SpUtil.getString(BytedeskConstants.nickname);
  final String? _currentAvatar = SpUtil.getString(BytedeskConstants.avatar);
  // 当前会话
  Thread? _currentThread;
  User? _robotUser;
  // 判断是否机器人对话状态
  bool _isRobot = false;
  // 分页加载聊天记录
  int _page = 0;
  final int _size = 20;
  // 延迟发送preview消息
  Timer? _debounce;
  // 定时拉取聊天记录
  Timer? _loadHistoryTimer;
  //
  Timer? _resendTimer;
  // 视频压缩
  // final _flutterVideoCompress = FlutterVideoCompress();
  bool _isRequestingThread = true;
  //
  @override
  void initState() {
    // debugPrint('chat_kf_page init');
    SpUtil.putBool(BytedeskConstants.isCurrentChatKfPage, true);
    // 从历史会话或者顶部通知栏进入
    if (widget.isThread! && widget.thread != null) {
      _currentThread = widget.thread;
      // FIXME: 在访客端-标题显示访客的名字，应该显示客服或技能组名字或固定写死
      _title = widget.title!.trim().isNotEmpty
          ? widget.title
          : widget.thread!.nickname;
      // _getMessages(_page, _size);
    } else {
      // 从请求客服页面进入
      _title = widget.title;
    }
    WidgetsBinding.instance.addObserver(this);
    // 监听build完成，https://blog.csdn.net/baoolong/article/details/85097318
    // WidgetsBinding.instance.addPostFrameCallback((_) {
    //   debugPrint('addPostFrameCallback');
    // });
    // Fluttertoast.showToast(msg: "请求中, 请稍后...");
    _listener();
    super.initState();
    // 定时拉取聊天记录 10s
    _loadHistoryTimer = Timer.periodic(const Duration(seconds: 15), (timer) {
      // debugPrint('从服务器 load history');
      //   // TODO: 暂时禁用从服务器加载历史记录
      // BlocProvider.of<MessageBloc>(context)
      //   ..add(LoadHistoryMessageEvent(uid: _currentUid, page: 0, size: 10));
      //   // 每隔 1 秒钟会调用一次，如果要结束调用
      //   // timer.cancel();
    });
    _resendTimer = Timer.periodic(const Duration(seconds: 2), (timer) {
      // TODO: 检测-消息是否超时发送失败
      for (var i = 0; i < _messages.length; i++) {
        Message? message = _messages[i].message;
        // 自己发送的 && 消息状态为发送中...
        if (message!.isSend == 1 &&
            message.status == BytedeskConstants.MESSAGE_STATUS_SENDING) {
          var nowTime = DateTime.now();
          var messageTime = DateTime.parse(message.timestamp!);
          int diff = nowTime.difference(messageTime).inSeconds;
          if (diff > 15) {
            // 超时15秒，设置为消息状态为error
            _messageProvider.update(
                message.mid, BytedeskConstants.MESSAGE_STATUS_ERROR);
          } else if (diff > 5) {
            // 5秒没有发送成功，则尝试使用http rest接口发送
            String content = '';
            if (message.type == BytedeskConstants.MESSAGE_TYPE_TEXT) {
              content = message.content!;
            } else if (message.type == BytedeskConstants.MESSAGE_TYPE_IMAGE) {
              content = message.imageUrl!;
            } else if (message.type == BytedeskConstants.MESSAGE_TYPE_FILE) {
              content = message.fileUrl!;
            } else if (message.type == BytedeskConstants.MESSAGE_TYPE_VOICE) {
              content = message.voiceUrl!;
            } else if (message.type == BytedeskConstants.MESSAGE_TYPE_VIDEO) {
              content = message.videoUrl!;
            } else {
              content = message.content!;
            }
            sendMessageRest(message.mid!, message.type!, content);
          }
        }
      }
    });
    // BlocProvider.of<MessageBloc>(context)
    //   ..add(LoadUnreadVisitorMessagesEvent(page: 0, size: 10));
  }

  //
  @override
  Widget build(BuildContext context) {
    super.build(context);
    //
    return Scaffold(
        appBar: AppBar(
          title: Text(_title ?? '请求中, 请稍后...'),
          centerTitle: true,
          elevation: 0,
          actions: [
            // TODO: 评价
            // TODO: 常见问题
            Visibility(
              visible: _isRobot,
              child: Align(
                  alignment: Alignment.centerRight,
                  child: Container(
                      padding: const EdgeInsets.only(right: 10),
                      width: 60,
                      child: InkWell(
                        onTap: () {
                          BlocProvider.of<ThreadBloc>(context).add(
                              RequestAgentEvent(
                                  wid: widget.wid,
                                  aid: widget.aid,
                                  type: widget.type));
                        },
                        child: const Text(
                          '转人工',
                          // style: TextStyle(color: Colors.black),
                        ),
                      ))),
            )
          ],
        ),
        body: MultiBlocListener(
            listeners: [
              BlocListener<ThreadBloc, ThreadState>(
                listener: (context, state) {
                  // 隐藏toast
                  // Fluttertoast.cancel();
                  if (state is RequestThreading) {
                    setState(() {
                      _isRequestingThread = true;
                    });
                  } else if (state is RequestThreadSuccess) {
                    setState(() {
                      _isRobot = false; // 需要，勿删
                      _currentThread = state.threadResult.msg!.thread;
                      _isRequestingThread = false;
                    });
                    // TODO: 加载本地历史消息
                    _getMessages(_page, _size);
                    // 加载未读消息
                    BlocProvider.of<MessageBloc>(context).add(
                        const LoadUnreadVisitorMessagesEvent(
                            page: 0, size: 10));
                    // 结果解析
                    if (state.threadResult.statusCode == 200 ||
                        state.threadResult.statusCode == 201) {
                      debugPrint('创建新会话');
                      // TODO: 参考拼多多，在发送按钮上方显示pop商品信息，用户确认之后才会发送商品信息
                      // 发送商品信息
                      if (widget.custom!.trim().isNotEmpty) {
                        _bdMqtt.sendCommodityMessage(
                            widget.custom!, _currentThread!);
                      }
                      // 发送附言消息
                      if (widget.postscript!.trim().isNotEmpty) {
                        _bdMqtt.sendTextMessage(
                            widget.postscript!, _currentThread!);
                      }
                    } else if (state.threadResult.statusCode == 202) {
                      debugPrint('提示排队中');
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // 加载本地历史消息
                      _appendMessage(state.threadResult.msg!);
                      // 发送商品信息
                      if (widget.custom!.trim().isNotEmpty) {
                        _bdMqtt.sendCommodityMessage(
                            widget.custom!, _currentThread!);
                      }
                      // 发送附言消息
                      if (widget.postscript!.trim().isNotEmpty) {
                        _bdMqtt.sendTextMessage(
                            widget.postscript!, _currentThread!);
                      }
                    } else if (state.threadResult.statusCode == 203) {
                      debugPrint('当前非工作时间，请自助查询或留言');
                      setState(() {
                        _currentThread = state.threadResult.msg!.thread;
                      });
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // TODO: 加载本地历史消息
                      _appendMessage(state.threadResult.msg!);
                      // 跳转留言页面，TODO: 关闭当前页面？
                      BytedeskKefu.showLeaveMessage(
                          context,
                          widget.wid!,
                          widget.aid!,
                          widget.type!,
                          state.threadResult.msg!.content!);
                      // Navigator.of(context)
                      //     .push(MaterialPageRoute(builder: (context) {
                      //   return LeaveMsgProvider(
                      //       wid: widget.wid,
                      //       aid: widget.aid,
                      //       type: widget.type,
                      //       tip: state.threadResult.msg!.content);
                      // }));
                    } else if (state.threadResult.statusCode == 204) {
                      debugPrint('当前无客服在线，请自助查询或留言');
                      setState(() {
                        _currentThread = state.threadResult.msg!.thread;
                      });
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // TODO: 加载本地历史消息
                      // _getMessages(_page, _size);
                      _appendMessage(state.threadResult.msg!);
                      // 跳转留言页面, TODO: 关闭当前页面？
                      Navigator.of(context)
                          .push(MaterialPageRoute(builder: (context) {
                        return LeaveMsgProvider(
                            wid: widget.wid,
                            aid: widget.aid,
                            type: widget.type,
                            tip: state.threadResult.msg!.content);
                      }));
                    } else if (state.threadResult.statusCode == 205) {
                      debugPrint('咨询前问卷');
                      setState(() {
                        _currentThread = state.threadResult.msg!.thread;
                      });
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // TODO: 加载本地历史消息
                      // _getMessages(_page, _size);
                      _appendMessage(state.threadResult.msg!);
                      //
                    } else if (state.threadResult.statusCode == 206) {
                      // debugPrint('返回机器人初始欢迎语 + 欢迎问题列表');
                      // TODO: 显示问题列表
                      setState(() {
                        _isRobot = true;
                        _robotUser = state.threadResult.msg!.user;
                        _currentThread = state.threadResult.msg!.thread;
                      });
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // TODO: 加载本地历史消息
                      // _getMessages(_page, _size);
                      _appendMessage(state.threadResult.msg!);
                      //
                    } else if (state.threadResult.statusCode == -1) {
                      Fluttertoast.showToast(msg: "请求会话失败");
                    } else if (state.threadResult.statusCode == -2) {
                      Fluttertoast.showToast(msg: "siteId或者工作组id错误");
                    } else if (state.threadResult.statusCode == -3) {
                      Fluttertoast.showToast(msg: "您已经被禁言");
                    } else if (state.threadResult.statusCode == -4) {
                      Fluttertoast.showToast(msg: "系统流量过大，请稍后再试");
                    } else if (state.threadResult.statusCode == -5) {
                      Fluttertoast.showToast(msg: "客服涉嫌诈骗，已经封号");
                    } else {
                      Fluttertoast.showToast(msg: "请求会话失败");
                    }
                  } else if (state is RequestThreadError) {
                    Fluttertoast.showToast(msg: "请求会话失败");
                    setState(() {
                      _isRequestingThread = false;
                    });
                  } else if (state is RequestAgentThreading) {
                    setState(() {
                      _isRequestingThread = true;
                    });
                  } else if (state is RequestAgentSuccess) {
                    // 请求人工客服，不管此工作组是否设置为默认机器人，只要有人工客服在线，则可以直接对接人工
                    setState(() {
                      _isRobot = false; // 需要，勿删
                      _currentThread = state.threadResult.msg!.thread;
                      _isRequestingThread = false;
                    });
                    // 插入本地
                    _messageProvider.insert(state.threadResult.msg!);
                    // _appendMessage(state.threadResult.msg!);
                  } else if (state is RequestAgentThreadError) {
                    Fluttertoast.showToast(msg: "请求会话失败");
                    setState(() {
                      _isRequestingThread = false;
                    });
                  }
                },
              ),
              BlocListener<MessageBloc, MessageState>(
                listener: (context, state) {
                  // debugPrint('message state change');
                  if (state is ReceiveMessageState) {
                    // debugPrint('receive message:' + state.message!.content!);
                  } else if (state is MessageUpLoading) {
                    Fluttertoast.showToast(msg: '上传中...');
                  } else if (state is UploadImageSuccess) {
                    if (_bdMqtt.isConnected()) {
                      _bdMqtt.sendImageMessage(
                          state.uploadJsonResult.url!, _currentThread!);
                    } else {
                      sendImageMessageRest(state.uploadJsonResult.url!);
                    }
                  } else if (state is UpLoadImageError) {
                    Fluttertoast.showToast(msg: '上传图片失败');
                  } else if (state is UploadVideoSuccess) {
                    if (_bdMqtt.isConnected()) {
                      _bdMqtt.sendVideoMessage(
                          state.uploadJsonResult.url!, _currentThread!);
                    } else {
                      sendVideoMessageRest(state.uploadJsonResult.url!);
                    }
                  } else if (state is UpLoadVideoError) {
                    Fluttertoast.showToast(msg: '上传视频失败');
                  } else if (state is QueryAnswerSuccess) {
                    // 已经修改为直接插入本地
                    // Message queryMessage = state.query!;
                    // queryMessage.isSend = 1;
                    // _messageProvider.insert(queryMessage);
                    // _appendMessage(queryMessage);
                    // //
                    // _messageProvider.insert(state.answer!);
                    // _appendMessage(state.answer!);
                  } else if (state is QueryCategorySuccess) {
                    _messageProvider.insert(state.answer!);
                    _appendMessage(state.answer!);
                  } else if (state is MessageAnswerSuccess) {
                    // Message queryMessage = state.query!;
                    // queryMessage.isSend = 1;
                    // _messageProvider.insert(queryMessage);
                    // _appendMessage(queryMessage);
                    //
                    if (state.query!.content!.contains('人工')) {
                      BlocProvider.of<ThreadBloc>(context).add(
                          RequestAgentEvent(
                              wid: widget.wid,
                              aid: widget.aid,
                              type: widget.type));
                    } else {
                      _messageProvider.insert(state.answer!);
                      _appendMessage(state.answer!);
                    }
                  } else if (state is RateAnswerSuccess) {
                    // TODO:
                  } else if (state is LoadHistoryMessageSuccess) {
                    // debugPrint('LoadHistoryMessageSuccess');
                    // 插入历史聊天记录
                    for (var i = 0; i < state.messageList!.length; i++) {
                      Message message = state.messageList![i];
                      _appendMessage(message);
                    }
                  } else if (state is LoadUnreadVisitorMessageSuccess) {
                    // 插入历史聊天记录
                    if (state.messageList!.isNotEmpty) {
                      // for (var i = 0; i < state.messageList!.length; i++) {
                      for (var i = state.messageList!.length - 1; i >= 0; i--) {
                        Message message = state.messageList![i];
                        // 本地持久化
                        _messageProvider.insert(message);
                        // 界面显示
                        _appendMessage(message);
                        // 发送已读回执
                        _bdMqtt.sendReceiptReadMessage(
                            message.mid!, _currentThread!);
                      }
                    }
                  } else if (state is SendMessageRestSuccess) {
                    // http rest 发送消息成功
                    String jsonMessage = state.jsonResult.data!;
                    String mid = json.decode(jsonMessage);
                    _messageProvider.update(
                        mid, BytedeskConstants.MESSAGE_STATUS_STORED);
                  } else if (state is SendMessageRestError) {
                    // http rest 发送消息失败
                    String jsonMessage = state.json;
                    String mid = json.decode(jsonMessage);
                    _messageProvider.update(
                        mid, BytedeskConstants.MESSAGE_STATUS_STORED);
                  }
                },
              ),
            ],
            child: _isRequestingThread
                ? Container(
                    margin: const EdgeInsets.only(top: 50),
                    alignment: Alignment.center,
                    child: const Column(children: <Widget>[
                      CircularProgressIndicator(
                        strokeWidth: 2,
                      ),
                      Text('会话请求中, 请稍后...')
                    ]))
                : Container(
                    alignment: Alignment.bottomCenter,
                    color: const Color(0xffdeeeeee),
                    child: Column(
                      children: <Widget>[
                        // 参考pull_to_refresh库中 QQChatList例子
                        Expanded(
                          //
                          child: SmartRefresher(
                            enablePullDown: false,
                            onLoading: () async {
                              // debugPrint('TODO: 下拉刷新'); // 注意：方向跟默认是反着的
                              // await Future.delayed(Duration(milliseconds: 1000));
                              _getMessages(_page, _size);
                              setState(() {});
                              _refreshController.loadComplete();
                            },
                            footer: const ClassicFooter(
                              loadStyle: LoadStyle.ShowWhenLoading,
                            ),
                            enablePullUp: true,
                            //
                            controller: _refreshController,
                            //
                            child: Scrollable(
                              controller: _scrollController,
                              axisDirection: AxisDirection.up,
                              viewportBuilder: (context, offset) {
                                return ExpandedViewport(
                                  offset: offset,
                                  axisDirection: AxisDirection.up,
                                  slivers: <Widget>[
                                    SliverExpanded(),
                                    SliverList(
                                      delegate: SliverChildBuilderDelegate(
                                          (c, i) => _messages[i],
                                          childCount: _messages.length),
                                    )
                                  ],
                                );
                              },
                            ),
                          ),
                        ),
                        const Divider(
                          height: 1.0,
                        ),
                        Container(
                            decoration: BoxDecoration(
                              color: Theme.of(context).cardColor,
                            ),
                            // child: _textComposerWidget(),
                            // FIXME: 表情在web和Android有问题？暂时仅在iOS启用表情
                            // child: (BytedeskUtils.isWeb || BytedeskUtils.isAndroid) ? _textComposerWidget() : _chatInput(),
                            child: _chatInput()),
                      ],
                    ),
                  )));
  }

  Widget _chatInput() {
    return ChatInput(
      // 发送触发事件
      isRobot: _isRobot,
      onSendPressed: _handleSendPressed,
      sendButtonVisibilityMode: SendButtonVisibilityMode.editing,
      // voiceWidget: VoiceRecord(),
      // voiceWidget: VoiceWidget(
      //   startRecord: () {},
      //   stopRecord: _handleVoiceSelection,
      //   // 加入定制化Container的相关属性
      //   height: 40.0,
      //   margin: EdgeInsets.zero,
      // ),
      extraWidget: ExtraItems(
          // 照片
          handleImageSelection: _handleImageSelection,
          // 文件
          handleFileSelection: _handleFileSelection,
          // 拍摄
          handlePickerSelection: _handlePickerSelection,
          // 上传视频
          handleUploadVideo: _handleUploadVideo,
          // 录制视频
          handleCaptureVideo: _handleCaptureVideo),
    );
  }

  //
  // void _handleVoiceSelection(AudioFile? obj) async {
  //   debugPrint('_handleVoiceSelection');
  //   if (obj != null) {
  //   }
  // }

  //
  Future<bool> _handleSendPressed(String content) async {
    debugPrint('send: $content');
    _handleSubmitted(content);
    return true;
  }

  void _handleImageSelection() async {
    debugPrint('_handleImageSelection');
    _pickImage();
  }

  void _handleFileSelection() async {
    debugPrint('_handleFileSelection');
  }

  Future<void> _handlePickerSelection() async {
    debugPrint('_handlePickerSelection');
    _takeImage();
    return;
  }

  void _handleUploadVideo() async {
    debugPrint('_handleUploadVideo');
    _pickVideo();
  }

  void _handleCaptureVideo() async {
    debugPrint('_handleCaptureVideo');
    _captureVideo();
  }

  //
  @override
  bool get wantKeepAlive => true;

  // 发送消息
  void _handleSubmitted(String? text) {
    _textController.clear();
    // 内容为空，直接返回
    if (text!.trim().isEmpty) {
      return;
    }
    //
    if (_isRobot) {
      //
      appendQueryMessage(text);
      // 请求机器人答案
      BlocProvider.of<MessageBloc>(context).add(MessageAnswerEvent(
          // type: widget.type,
          wid: widget.wid,
          // aid: widget.aid,
          content: text));
    } else if (_bdMqtt.isConnected()) {
      if (_currentThread == null) {
        Fluttertoast.showToast(msg: '请求客服中, 请稍后...');
        return;
      }
      // 长连接正常情况下，调用长连接接口
      _bdMqtt.sendTextMessage(text, _currentThread!);
    } else {
      // debugPrint('长连接断开的情况下，调用rest接口');
      sendTextMessageRest(text);
    }
  }

  // http rest 接口发生文本消息，长链接断开情况下调用
  void sendTextMessageRest(String text) {
    //
    String? mid = BytedeskUuid.uuid();
    sendMessageRest(mid, BytedeskConstants.MESSAGE_TYPE_TEXT, text);
  }

  // http rest 接口发送图片消息，长链接断开情况下调用
  void sendImageMessageRest(String imageUrl) {
    //
    String? mid = BytedeskUuid.uuid();
    sendMessageRest(mid, BytedeskConstants.MESSAGE_TYPE_IMAGE, imageUrl);
  }

  void sendVideoMessageRest(String videoUrl) {
    //
    String? mid = BytedeskUuid.uuid();
    sendMessageRest(mid, BytedeskConstants.MESSAGE_TYPE_VIDEO, videoUrl);
  }

  // http rest 接口发送消息，长链接断开情况下调用
  void sendMessageRest(String mid, String type, String content) {
    // String? mid = BytedeskUuid.uuid();
    String? timestamp = BytedeskUtils.formatedDateNow();
    String? client = BytedeskUtils.getClient();
    //
    // 暂时没有将插入本地函数独立出来，暂时
    Message message = Message();
    message.mid = mid;
    message.type = type;
    message.timestamp = timestamp;
    message.client = client;
    message.avatar = _currentAvatar;
    message.topic = _currentThread!.topic;
    message.status = BytedeskConstants.MESSAGE_STATUS_SENDING;
    message.isSend = 1;
    message.currentUid = _currentUid;
    message.answersJson = '';
    message.thread = _currentThread;
    message.user = User(
        uid: _currentUid, avatar: _currentAvatar, nickname: _currentNickname);
    //
    Map<String, Object> jsonContent = {};
    if (type == BytedeskConstants.MESSAGE_TYPE_TEXT) {
      message.content = content;
      //
      jsonContent = {
        "mid": mid,
        "timestamp": timestamp,
        "client": client,
        "version": "1",
        "type": type,
        "status": BytedeskConstants.MESSAGE_STATUS_SENDING,
        "user": {
          "uid": _currentUid,
          "username": _currentUsername,
          "nickname": _currentNickname,
          "avatar": _currentAvatar,
          "extra": {"agent": false}
        },
        "text": {"content": content},
        "thread": {
          "tid": _currentThread!.tid,
          "type": _currentThread!.type,
          "content": content,
          "nickname": _currentThread!.nickname,
          "avatar": _currentThread!.avatar,
          "topic": _currentThread!.topic,
          "client": client,
          "timestamp": timestamp,
          "unreadCount": 0
        }
      };
    } else if (type == BytedeskConstants.MESSAGE_TYPE_IMAGE) {
      message.imageUrl = content;
      jsonContent = {
        "mid": mid,
        "timestamp": timestamp,
        "client": client,
        "version": "1",
        "type": type,
        "status": "sending",
        "user": {
          "uid": _currentUid,
          "username": _currentUsername,
          "nickname": _currentNickname,
          "avatar": _currentAvatar,
          "extra": {"agent": false}
        },
        "image": {"imageUrl": content},
        "thread": {
          "tid": _currentThread!.tid,
          "type": _currentThread!.type,
          "content": "[图片]",
          "nickname": _currentThread!.nickname,
          "avatar": _currentThread!.avatar,
          "topic": _currentThread!.topic,
          "client": client,
          "timestamp": timestamp,
          "unreadCount": 0
        }
      };
    } else if (type == BytedeskConstants.MESSAGE_TYPE_FILE) {
      message.fileUrl = content;
      jsonContent = {
        "mid": mid,
        "timestamp": timestamp,
        "client": client,
        "version": "1",
        "type": type,
        "status": BytedeskConstants.MESSAGE_STATUS_SENDING,
        "user": {
          "uid": _currentUid,
          "username": _currentUsername,
          "nickname": _currentNickname,
          "avatar": _currentAvatar,
          "extra": {"agent": false}
        },
        "file": {"fileUrl": content},
        "thread": {
          "tid": _currentThread!.tid,
          "type": _currentThread!.type,
          "content": "[文件]",
          "nickname": _currentThread!.nickname,
          "avatar": _currentThread!.avatar,
          "topic": _currentThread!.topic,
          "client": client,
          "timestamp": timestamp,
          "unreadCount": 0
        }
      };
    } else if (type == BytedeskConstants.MESSAGE_TYPE_VOICE) {
      message.voiceUrl = content;
      jsonContent = {
        "mid": mid,
        "timestamp": timestamp,
        "client": client,
        "version": "1",
        "type": type,
        "status": BytedeskConstants.MESSAGE_STATUS_SENDING,
        "user": {
          "uid": _currentUid,
          "username": _currentUsername,
          "nickname": _currentNickname,
          "avatar": _currentAvatar,
          "extra": {"agent": false}
        },
        "voice": {
          "voiceUrl": content,
          "length": '0', // TODO:替换为真实值
          "format": "wav",
        },
        "thread": {
          "tid": _currentThread!.tid,
          "type": _currentThread!.type,
          "content": content,
          "nickname": _currentThread!.nickname,
          "avatar": _currentThread!.avatar,
          "topic": _currentThread!.topic,
          "client": client,
          "timestamp": timestamp,
          "unreadCount": 0
        }
      };
    } else if (type == BytedeskConstants.MESSAGE_TYPE_VIDEO) {
      message.videoUrl = content;
      jsonContent = {
        "mid": mid,
        "timestamp": timestamp,
        "client": client,
        "version": "1",
        "type": type,
        "status": BytedeskConstants.MESSAGE_STATUS_SENDING,
        "user": {
          "uid": _currentUid,
          "username": _currentUsername,
          "nickname": _currentNickname,
          "avatar": _currentAvatar,
          "extra": {"agent": false}
        },
        "video": {"videoOrShortUrl": content},
        "thread": {
          "tid": _currentThread!.tid,
          "type": _currentThread!.type,
          "content": content,
          "nickname": _currentThread!.nickname,
          "avatar": _currentThread!.avatar,
          "topic": _currentThread!.topic,
          "client": client,
          "timestamp": timestamp,
          "unreadCount": 0
        }
      };
    }

    String? jsonString = json.encode(jsonContent);
    BlocProvider.of<MessageBloc>(context)
        .add(SendMessageRestEvent(json: jsonString));
    // 插入本地数据库
    _messageProvider.insert(message);
    //
    pushToMessageArray(message, true);
  }

  void appendQueryMessage(String content) {
    //
    String? mid = BytedeskUuid.uuid();
    String? timestamp = BytedeskUtils.formatedDateNow();
    String? client = BytedeskUtils.getClient();
    String? type = BytedeskConstants.MESSAGE_TYPE_ROBOT;
    //
    // 暂时没有将插入本地函数独立出来，暂时
    Message message = Message();
    message.mid = mid;
    message.type = type;
    message.timestamp = timestamp;
    message.client = client;
    message.nickname = _currentNickname;
    message.avatar = _currentAvatar;
    message.topic = _currentThread!.topic;
    message.status = BytedeskConstants.MESSAGE_STATUS_STORED;
    message.isSend = 1;
    message.currentUid = _currentUid;
    message.answersJson = '';
    message.thread = _currentThread;
    message.user = User(
        uid: _currentUid, avatar: _currentAvatar, nickname: _currentNickname);
    //
    message.content = content;
    // 插入本地数据库
    _messageProvider.insert(message);
    //
    pushToMessageArray(message, true);
  }

  void appendReplyMessage(String aid, String mid, String content) {
    //
    String? timestamp = BytedeskUtils.formatedDateNow();
    String? client = BytedeskUtils.getClient();
    // String? type = BytedeskConstants.MESSAGE_TYPE_ROBOT_RESULT;
    String? type = BytedeskConstants.MESSAGE_TYPE_ROBOT;
    //
    // 暂时没有将插入本地函数独立出来，暂时R
    Message message = Message();
    message.mid = mid;
    message.type = type;
    message.timestamp = timestamp;
    message.client = client;
    message.nickname = _robotUser!.nickname;
    message.avatar = _robotUser!.avatar;
    message.topic = _currentThread!.topic;
    message.status = BytedeskConstants.MESSAGE_STATUS_STORED;
    message.isSend = 0;
    message.currentUid = _currentUid;
    message.answersJson = '';
    message.thread = _currentThread;
    message.user = User(
        uid: _robotUser!.uid,
        avatar: _robotUser!.avatar,
        nickname: _robotUser!.nickname);
    //
    message.content = content;
    // 插入本地数据库
    _messageProvider.insert(message);
    //
    pushToMessageArray(message, true);
  }

  //
  _listener() {
    // 更新消息状态
    bytedeskEventBus.on<ReceiveMessageReceiptEventBus>().listen((event) {
      // debugPrint('更新状态:' + event.mid + '-' + event.status);
      if (mounted) {
        // 更新界面
        for (var i = 0; i < _messages.length; i++) {
          MessageWidget messageWidget = _messages[i];
          if (messageWidget.message!.mid == event.mid &&
              _messages[i].message!.status !=
                  BytedeskConstants.MESSAGE_STATUS_READ) {
            // debugPrint('do update status:' + messageWidget.message!.mid!);
            // setState(() {
            //   _messages[i].message!.status = event.status; // 不更新
            // });
            // 必须重新创建一个messageWidget才会更新
            Message message = messageWidget.message!;
            message.status = event.status;
            MessageWidget messageWidget2 = MessageWidget(
                message: message,
                customCallback: widget.customCallback,
                animationController: AnimationController(
                    vsync: this, duration: const Duration(milliseconds: 500)));
            setState(() {
              _messages[i] = messageWidget2;
            });
          }
        }
      }
    });
    bytedeskEventBus.on<ReceiveMessagePreviewEventBus>().listen((event) {
      // debugPrint('消息预知');
      if (mounted) {
        setState(() {
          // TODO: 国际化，支持英文
          _title = '对方正在输入';
        });
      }
      // 还原title
      Timer(
        const Duration(seconds: 3),
        () {
          // debugPrint('timer');
          if (mounted) {
            setState(() {
              _title = widget.title;
            });
          }
        },
      );
    });
    // 接收到新消息
    bytedeskEventBus.on<ReceiveMessageEventBus>().listen((event) {
      // debugPrint('receive message:' + event.message!.content);
      if (_currentThread != null &&
          (event.message.thread!.topic != _currentThread!.topic)) {
        return;
      }
      // 非自己发送的，发送已读回执
      if (event.message.isSend == 0) {
        _bdMqtt.sendReceiptReadMessage(
            event.message.mid!, event.message.thread!);
      }
      //
      pushToMessageArray(event.message, true);
      // if (this.mounted) {
      //   // 界面显示
      //   MessageWidget messageWidget = new MessageWidget(
      //       message: event.message,
      //       customCallback: widget.customCallback,
      //       animationController: new AnimationController(
      //           vsync: this, duration: Duration(milliseconds: 500)));
      //   setState(() {
      //     _messages.insert(0, messageWidget);
      //     // _messages.add(messageWidget);
      //   });
      // }
    });
    // 删除消息
    bytedeskEventBus.on<DeleteMessageEventBus>().listen((event) {
      //
      if (mounted) {
        // 从sqlite中删除
        _messageProvider.delete(event.mid);
        // 更新界面
        setState(() {
          _messages.removeWhere((element) => element.message!.mid == event.mid);
        });
      }
    });
    // 查询机器人消息
    bytedeskEventBus.on<QueryAnswerEventBus>().listen((event) {
      if (mounted) {
        // debugPrint('aid ${event.aid}, question ${event.question}, answer ${event.answer}');
        // 可以直接将问题和答案插入本地，并显示，但为了服务器保存查询记录，特将请求发送给服务器
        appendQueryMessage(event.question);
        //
        String? mid = BytedeskUuid.uuid();
        appendReplyMessage(event.aid, mid, event.answer);
        //
        BlocProvider.of<MessageBloc>(context).add(QueryAnswerEvent(
            tid: _currentThread!.tid, aid: event.aid, mid: mid));
      }
    });
    // 查询分类下属问题
    bytedeskEventBus.on<QueryCategoryEventBus>().listen((event) {
      if (mounted) {
        debugPrint('cid ${event.cid}, name ${event.name}');
        // 可以直接将问题和答案插入本地，并显示，但为了服务器保存查询记录，特将请求发送给服务器
        appendQueryMessage(event.name);
        //
        BlocProvider.of<MessageBloc>(context)
            .add(QueryCategoryEvent(tid: _currentThread!.tid, cid: event.cid));
      }
    });
    // 点击机器人消息 ‘人工客服’
    bytedeskEventBus.on<RequestAgentThreadEventBus>().listen((event) {
      if (mounted) {
        BlocProvider.of<ThreadBloc>(context).add(RequestAgentEvent(
            wid: widget.wid, aid: widget.aid, type: widget.type));
      }
    });
    // 滚动监听, https://learnku.com/articles/30338
    _scrollController.addListener(() {
      // 隐藏软键盘
      FocusScope.of(context).requestFocus(FocusNode());
      // 如果滑动到底部
      // if (_scrollController.position.pixels ==
      //     _scrollController.position.maxScrollExtent) {
      //   debugPrint('已经到底了');
      // }
    });
  }

  // 选择图片
  Future<void> _pickImage() async {
    try {
      //
      XFile? pickedFile = await _picker.pickImage(
          source: ImageSource.gallery, maxWidth: 800, imageQuality: 95);
      //
      if (pickedFile != null) {
        debugPrint('pick image path: ${pickedFile.path}');
        // TODO: 将图片显示到对话消息中
        // TODO: 显示处理中loading
        // 压缩
        // final dir = await path_provider.getTemporaryDirectory();
        // final targetPath = dir.absolute.path +
        //     "/" +
        //     BytedeskUtils.currentTimeMillis().toString() +
        //     ".jpg";
        // debugPrint('targetPath: $targetPath');
        // await BytedeskUtils.compressImage(File(pickedFile.path), targetPath);
        // // 上传压缩后图片
        // BlocProvider.of<MessageBloc>(context)
        //   ..add(UploadImageEvent(filePath: targetPath));
        //
        if (BytedeskUtils.isWeb) {
          // web
          String? fileName = pickedFile.path.split("/").last;
          Uint8List? fileBytes = await pickedFile.readAsBytes();
          String? mimeType = pickedFile.mimeType;
          //
          BlocProvider.of<MessageBloc>(context).add(UploadImageBytesEvent(
              fileName: fileName, fileBytes: fileBytes, mimeType: mimeType,));
        } else {
          // 其他
          BlocProvider.of<MessageBloc>(context)
              .add(UploadImageEvent(filePath: pickedFile.path));
        }
      } else {
        Fluttertoast.showToast(msg: '未选取图片');
      }
    } catch (e) {
      debugPrint('pick image error ${e.toString()}');
      Fluttertoast.showToast(msg: "未选取图片");
    }
  }


  // 拍照
  Future<void> _takeImage() async {
    try {
      XFile? pickedFile = await _picker.pickImage(
          source: ImageSource.camera, maxWidth: 800, imageQuality: 95);
      //
      if (pickedFile != null) {
        debugPrint('take image path: ${pickedFile.path}');
        // TODO: 将图片显示到对话消息中
        // TODO: 显示处理中loading
        // 压缩
        // final dir = await path_provider.getTemporaryDirectory();
        // final targetPath = dir.absolute.path +
        //     "/" +
        //     BytedeskUtils.currentTimeMillis().toString() +
        //     ".jpg";
        // debugPrint('targetPath: $targetPath');
        // await BytedeskUtils.compressImage(File(pickedFile.path), targetPath);
        // // 上传压缩后图片
        // BlocProvider.of<MessageBloc>(context)
        //   ..add(UploadImageEvent(filePath: targetPath));
        //
        if (BytedeskUtils.isWeb) {
          // web
          String? fileName = pickedFile.path.split("/").last;
          Uint8List? fileBytes = await pickedFile.readAsBytes();
          String? mimeType = pickedFile.mimeType;
          //
          BlocProvider.of<MessageBloc>(context).add(UploadImageBytesEvent(
            fileName: fileName,
            fileBytes: fileBytes,
            mimeType: mimeType,
          ));
        } else {
          // 其他
          BlocProvider.of<MessageBloc>(context)
              .add(UploadImageEvent(filePath: pickedFile.path));
        }
      } else {
        Fluttertoast.showToast(msg: '未拍照');
      }
    } catch (e) {
      debugPrint('take image error ${e.toString()}');
      Fluttertoast.showToast(msg: "未选取图片");
    }
  }

  // 上传视频
  // FIXME: image_picker有bug，选择视频后缀为.jpg
  // 手机可以播放，但chrome无法播放
  Future<void> _pickVideo() async {
    try {
      XFile? pickedFile = await _picker.pickVideo(
          source: ImageSource.gallery,
          maxDuration: const Duration(seconds: 10));

      if (pickedFile != null) {
        debugPrint('pick video path: ${pickedFile.path}');
        //
        if (BytedeskUtils.isWeb) {
          // web
          String? fileName = pickedFile.path.split("/").last.replaceAll(".jpg", ".mp4");
          Uint8List? fileBytes = await pickedFile.readAsBytes();
          String? mimeType = pickedFile.mimeType;
          //
          BlocProvider.of<MessageBloc>(context).add(UploadVideoBytesEvent(
            fileName: fileName,
            fileBytes: fileBytes,
            mimeType: mimeType,
          ));
        } else {
          // 其他
          BlocProvider.of<MessageBloc>(context)
              .add(UploadVideoEvent(filePath: pickedFile.path));
        }
        // BlocProvider.of<MessageBloc>(context)
        //     .add(UploadVideoEvent(filePath: pickedFile.path));
      } else {
        Fluttertoast.showToast(msg: '未选取视频');
      }
      // 使用file_picker替换image_picker
      // List<PlatformFile>? _paths = (await FilePicker.platform.pickFiles(
      //   type: FileType.video,
      //   allowMultiple: false,
      //   allowedExtensions: [],
      // ))
      //     ?.files;
      // if (_paths!.length > 0) {
      //   // TODO: 将视频显示到对话消息中
      //   // TODO: 显示处理中loading
      //   // 压缩
      //   // final info = await _flutterVideoCompress.compressVideo(
      //   //   _paths[0].path,
      //   //   quality:
      //   //       VideoQuality.LowQuality, // default(VideoQuality.DefaultQuality)
      //   //   deleteOrigin: false, // default(false)
      //   // );
      //   // // debugBytedeskUtils.printLog(info.toJson().toString());
      //   // String? afterPath = info.toJson()['path'];
      //   // // debugPrint('video path: ${_paths[0].path}, compress path: $afterPath');
      //   // // 上传
      //   // BlocProvider.of<MessageBloc>(context)
      //   //   ..add(UploadVideoEvent(filePath: afterPath));
      //   // 压缩后上传
      //   BlocProvider.of<MessageBloc>(context)
      //     ..add(UploadVideoEvent(filePath: _paths[0].path));
      // }
    } catch (e) {
      debugPrint('pick video error ${e.toString()}');
      Fluttertoast.showToast(msg: "未选取视频");
    }
  }

  // 录制视频
  Future<void> _captureVideo() async {
    try {
      XFile? pickedFile = await _picker.pickVideo(
          source: ImageSource.camera, maxDuration: const Duration(seconds: 10));
      //
      if (pickedFile != null) {
        debugPrint('take video path: ${pickedFile.path}');
        // TODO: 将图片显示到对话消息中
        // TODO: 显示处理中loading
        // 压缩
        // final info = await _flutterVideoCompress.compressVideo(
        //   pickedFile.path,
        //   quality:
        //       VideoQuality.LowQuality, // default(VideoQuality.DefaultQuality)
        //   deleteOrigin: false, // default(false)
        // );
        // // debugBytedeskUtils.printLog(info.toJson().toString());
        // String? afterPath = info.toJson()['path'];
        // // debugPrint('video path: ${pickedFile.path}, compress path: $afterPath');
        // // 上传
        // BlocProvider.of<MessageBloc>(context)
        //   ..add(UploadVideoEvent(filePath: afterPath));
        //
        //
        if (BytedeskUtils.isWeb) {
          // web
          String? fileName = pickedFile.path.split("/").last.replaceAll(".jpg", ".mp4");
          Uint8List? fileBytes = await pickedFile.readAsBytes();
          String? mimeType = pickedFile.mimeType;
          //
          BlocProvider.of<MessageBloc>(context).add(UploadVideoBytesEvent(
            fileName: fileName,
            fileBytes: fileBytes,
            mimeType: mimeType,
          ));
        } else {
          // 其他
          BlocProvider.of<MessageBloc>(context)
              .add(UploadVideoEvent(filePath: pickedFile.path));
        }
      } else {
        Fluttertoast.showToast(msg: '未录制视频');
      }
    } catch (e) {
      debugPrint('take video error ${e.toString()}');
      Fluttertoast.showToast(msg: "未录制视频");
    }
  }

  // 加载更多聊天记录
  // Future<void> _loadMoreMessages() async {
  //   debugPrint('load more');
  //   // TODO: 从服务器加载
  //   _getMessages(_page, _size);
  // }

  // 分页加载本地历史聊天记录
  // TODO: 从服务器加载聊天记录
  // FIXME: 消息排序错乱
  Future<void> _getMessages(int page, int size) async {
    // BlocProvider.of<MessageBloc>(context)
    //     ..add(LoadHistoryMessageEvent(uid: _currentUid, page: page, size: size));
    //
    List<Message> messageList = await _messageProvider.getTopicMessages(
        _currentThread!.topic, _currentUid, page, size);
    // BytedeskUtils.printLog(messageList.length);
    int length = messageList.length;
    for (var i = 0; i < length; i++) {
      Message message = messageList[i];
      if (message.type ==
              BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_FORM_REQUEST ||
          message.type ==
              BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_FORM_RESULT) {
        // 暂时忽略表单消息
      } else if (message.type ==
          BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_THREAD_REENTRY) {
        // 连续的 ‘继续会话’ 消息，只显示最后一条
        if (i + 1 < length) {
          var nextmsg = messageList[i + 1];
          if (nextmsg.type ==
              BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_THREAD_REENTRY) {
            continue;
          } else {
            pushToMessageArray(message, false);
          }
        }
      } else {
        pushToMessageArray(message, false);
      }
    }
    //
    _page += 1;
  }

  void pushToMessageArray(Message message, bool append) {
    if (mounted) {
      bool contains = false;
      for (var i = 0; i < _messages.length; i++) {
        Message? element = _messages[i].message;
        if (element!.mid == message.mid) {
          contains = true;
          // 更新消息状态
          _messageProvider.update(element.mid, message.status);
        }
      }
      if (!contains) {
        MessageWidget messageWidget = MessageWidget(
            message: message,
            customCallback: widget.customCallback,
            animationController: AnimationController(
                vsync: this, duration: const Duration(milliseconds: 500)));
        setState(() {
          if (append) {
            _messages.insert(0, messageWidget);
          } else {
            _messages.add(messageWidget);
            _messages.sort((a, b) {
              return b.message!.timestamp!.compareTo(a.message!.timestamp!);
            });
          }
        });
      }
    }
    if (message.status != BytedeskConstants.MESSAGE_STATUS_READ) {
      // 发送已读回执
      if (message.isSend == 0 && _currentThread != null) {
        // debugPrint('message.mid ${message.mid}');
        // debugPrint('_currentThread ${_currentThread!.tid}');
        _bdMqtt.sendReceiptReadMessage(message.mid!, _currentThread!);
      }
    }
  }

  Future<void> _appendMessage(Message message) async {
    debugPrint('append:${message.mid!}content:${message.content!}');
    pushToMessageArray(message, true);
  }

  void scrollToBottom() {
    // After 1 second, it takes you to the bottom of the ListView
    // Timer(
    //   Duration(seconds: 1),
    //   () => _scrollController.jumpTo(_scrollController.position.maxScrollExtent),
    // );
    _scrollController.animateTo(
      _scrollController.position.maxScrollExtent,
      duration: const Duration(seconds: 1),
      curve: Curves.fastOutSlowIn,
    );
  }

  // @override
  // void didChangeAppLifecycleState(AppLifecycleState state) {
  //   // debugPrint("didChangeAppLifecycleState:" + state.toString());
  //   switch (state) {
  //     case AppLifecycleState.inactive: // 处于这种状态的应用程序应该假设它们可能在任何时候暂停。
  //       break;
  //     case AppLifecycleState.paused: // 应用程序不可见，后台
  //       break;
  //     case AppLifecycleState.resumed: // 应用程序可见，前台
  //       // APP切换到前台之后，重连
  //       // BytedeskUtils.mqttReConnect();
  //       // TODO: 拉取离线消息
  //       break;
  //     case AppLifecycleState.detached: // 申请将暂时暂停
  //       break;
  //   }
  // }

  @override
  void dispose() {
    // debugPrint('chat_kf_page dispose');
    SpUtil.putBool(BytedeskConstants.isCurrentChatKfPage, false);
    WidgetsBinding.instance.removeObserver(this);
    _debounce?.cancel();
    _loadHistoryTimer?.cancel();
    _resendTimer?.cancel();
    // bytedeskEventBus.destroy(); // FIXME: 只能取消监听，不能destroy
    super.dispose();
  }
}


  //
  // Widget _textComposerWidget() {
  //   return IconTheme(
  //     data: const IconThemeData(color: Colors.blue),
  //     child: Container(
  //       margin: const EdgeInsets.symmetric(horizontal: 8.0),
  //       height: 54,
  //       child: Row(
  //         children: <Widget>[
  //           Container(
  //             // margin: const EdgeInsets.symmetric(horizontal: 4.0),
  //             child: IconButton(
  //               icon: const Icon(Icons.add),
  //               onPressed: () {
  //                 // _getImage();
  //                 showModalBottomSheet(
  //                     context: context,
  //                     builder: (context) {
  //                       return ImageChooseWidget(pickImageCallBack: () {
  //                         _pickImage();
  //                       }, takeImageCallBack: () {
  //                         _takeImage();
  //                       }, pickVideoCallBack: () {
  //                         _pickVideo();
  //                       }, captureVideoCallBack: () {
  //                         _captureVideo();
  //                       });
  //                     });
  //               },
  //             ),
  //           ),
  //           Flexible(
  //             child: TextField(
  //               onChanged: (value) {
  //                 if (_debounce?.isActive ?? false) _debounce!.cancel();
  //                 // 积累500毫秒，再发送。否则发送过于频繁
  //                 _debounce = Timer(const Duration(milliseconds: 500), () {
  //                   // debugPrint('send preview $value');
  //                   // 发送预知消息
  //                   if (value.trim().length > 0) {
  //                     _bdMqtt.sendPreviewMessage(value, _currentThread!);
  //                   } else {
  //                     _bdMqtt.sendPreviewMessage('', _currentThread!);
  //                   }
  //                 });
  //               },
  //               decoration: const InputDecoration.collapsed(hintText: "输入内容..."),
  //               controller: _textController,
  //               onSubmitted: _handleSubmitted,
  //             ),
  //           ),
  //           Container(
  //             margin: const EdgeInsets.symmetric(horizontal: 4.0),
  //             child: IconButton(
  //               icon: const Icon(Icons.send),
  //               onPressed: () => _handleSubmitted(_textController.text),
  //             ),
  //           )
  //         ],
  //       ),
  //     ),
  //   );
  // }
