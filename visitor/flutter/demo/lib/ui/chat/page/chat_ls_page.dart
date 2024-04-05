// ignore_for_file: use_build_context_synchronously, use_full_hex_values_for_flutter_colors

import 'dart:async';
// import 'dart:io';

import 'package:bytedesk_kefu/blocs/message_bloc/bloc.dart';
import 'package:bytedesk_kefu/blocs/thread_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/messageProvider.dart';
import 'package:bytedesk_kefu/model/model.dart';
import 'package:bytedesk_kefu/mqtt/bytedesk_mqtt.dart';
import 'package:bytedesk_kefu/ui/chat/widget/message_widget.dart';
import 'package:bytedesk_kefu/ui/widget/expanded_viewport.dart';
// import 'package:bytedesk_kefu/ui/widget/image_choose_widget.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_events.dart';
// import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
// import 'package:file_picker/file_picker.dart';
import 'package:sp_util/sp_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
// import 'package:flutter_video_compress/flutter_video_compress.dart';
// import 'package:flutter_image_compress/flutter_image_compress.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:image_picker/image_picker.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';

import '../../widget/media_choose_widget.dart';
// import 'package:path_provider/path_provider.dart' as path_provider;

// TODO: 良师APP对话页面
// TODO: 接通客服之前，在title显示loading
// 客服关闭会话，或者 自动关闭会话，则禁止继续发送消息
// 点击商品信息，回调接口-进入商品详情页面
// TODO: 右上角增加按钮回调入口，支持用户自定义按钮，进入店铺/学校详情页面
// 系统消息居中显示
class ChatLSPage extends StatefulWidget {
  //
  final String? wid;
  final String? aid;
  final String? type;
  final String? title;
  final String? custom;
  final String? postscript;
  // 从历史会话或者点击通知栏进入
  final bool? isThread;
  final Thread? thread;
  final ValueSetter<String>? customCallback;
  //
  const ChatLSPage(
      {Key? key,
      this.wid,
      this.aid,
      this.type,
      this.title,
      this.custom,
      this.postscript,
      this.isThread,
      this.thread,
      this.customCallback})
      : super(key: key);
  //
  @override
  State<ChatLSPage> createState() => _ChatLSPageState();
}

class _ChatLSPageState extends State<ChatLSPage>
    with
        AutomaticKeepAliveClientMixin<ChatLSPage>,
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
  // 当前会话
  Thread? _currentThread;
  // 判断是否机器人对话状态
  bool _isRobot = false;
  // 分页加载聊天记录
  int _page = 0;
  final int _size = 20;
  // 延迟发送preview消息
  Timer? _debounce;
  // 视频压缩
  // final _flutterVideoCompress = FlutterVideoCompress();
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
      _getMessages(_page, _size);
    } else {
      // 从请求客服页面进入
      _title = widget.title;
    }
    WidgetsBinding.instance.addObserver(this);
    // 监听build完成，https://blog.csdn.net/baoolong/article/details/85097318
    // WidgetsBinding.instance.addPostFrameCallback((_) {
    //   debugPrint('addPostFrameCallback');
    // });
    _listener();
    super.initState();
  }

  //
  @override
  Widget build(BuildContext context) {
    super.build(context);
    //
    return Scaffold(
        appBar: AppBar(
          title: Text(_title ?? ''),
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
                          BlocProvider.of<ThreadBloc>(context)
                            .add(RequestAgentEvent(
                                wid: widget.wid,
                                aid: widget.aid,
                                type: widget.type));
                        },
                        child: const Text(
                          '转人工',
                          style: TextStyle(color: Colors.black),
                        ),
                      ))),
            )
          ],
        ),
        body: MultiBlocListener(
            listeners: [
              BlocListener<ThreadBloc, ThreadState>(
                listener: (context, state) {
                  if (state is RequestThreadSuccess) {
                    setState(() {
                      _isRobot = false; // 需要，勿删
                      _currentThread = state.threadResult.msg!.thread;
                    });
                    // 插入本地
                    // _messageProvider.insert(state.threadResult.msg!);
                    // 加载本地历史消息
                    _getMessages(_page, _size);
                    // _appendMessage(state.threadResult.msg!);
                    //
                    if (state.threadResult.statusCode == 200 ||
                        state.threadResult.statusCode == 201) {
                      debugPrint('创建新会话');
                      // 插入本地
                      // _messageProvider.insert(state.threadResult.msg!);
                      // TODO: 参考拼多多，在发送按钮上方显示pop商品信息，用户确认之后才会发送商品信息
                      // 发送商品信息
                      if (widget.custom != null &&
                          widget.custom!.trim().isNotEmpty) {
                        _bdMqtt.sendCommodityMessage(
                            widget.custom!, _currentThread!);
                      }
                      // 发送附言消息
                      if (widget.postscript != null &&
                          widget.postscript!.trim().isNotEmpty) {
                        _bdMqtt.sendTextMessage(
                            widget.postscript!, _currentThread!);
                      }
                    } else if (state.threadResult.statusCode == 202) {
                      debugPrint('提示排队中');
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // 加载本地历史消息
                      // _getMessages(_page, _size);
                      _appendMessage(state.threadResult.msg!);
                      // 发送商品信息
                      if (widget.custom != null &&
                          widget.custom!.trim().isNotEmpty) {
                        _bdMqtt.sendCommodityMessage(
                            widget.custom!, _currentThread!);
                      }
                      // 发送附言消息
                      if (widget.postscript != null &&
                          widget.postscript!.trim().isNotEmpty) {
                        _bdMqtt.sendTextMessage(
                            widget.postscript!, _currentThread!);
                      }
                    } else if (state.threadResult.statusCode == 203) {
                      debugPrint('当前非工作时间，请自助查询或留言');
                      // TODO: 显示留言页面
                      setState(() {
                        _currentThread = state.threadResult.msg!.thread;
                      });
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // 加载本地历史消息
                      _getMessages(_page, _size);
                      _appendMessage(state.threadResult.msg!);
                    } else if (state.threadResult.statusCode == 204) {
                      debugPrint('当前无客服在线，请自助查询或留言');
                      // TODO: 显示留言页面
                      setState(() {
                        _currentThread = state.threadResult.msg!.thread;
                      });
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // 加载本地历史消息
                      _getMessages(_page, _size);
                      _appendMessage(state.threadResult.msg!);
                    } else if (state.threadResult.statusCode == 205) {
                      debugPrint('咨询前问卷');
                      setState(() {
                        _currentThread = state.threadResult.msg!.thread;
                      });
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // 加载本地历史消息
                      _getMessages(_page, _size);
                      _appendMessage(state.threadResult.msg!);
                      //
                    } else if (state.threadResult.statusCode == 206) {
                      debugPrint('返回机器人初始欢迎语 + 欢迎问题列表');
                      // TODO: 显示问题列表
                      setState(() {
                        _isRobot = true;
                        _currentThread = state.threadResult.msg!.thread;
                      });
                      // 插入本地
                      _messageProvider.insert(state.threadResult.msg!);
                      // 加载本地历史消息
                      _getMessages(_page, _size);
                      _appendMessage(state.threadResult.msg!);
                      //
                    } else if (state.threadResult.statusCode == -1) {
                      Fluttertoast.showToast(msg: "请求会话失败");
                    } else if (state.threadResult.statusCode == -2) {
                      Fluttertoast.showToast(msg: "siteId或者工作组id错误");
                    } else if (state.threadResult.statusCode == -3) {
                      Fluttertoast.showToast(msg: "您已经被禁言");
                    } else {
                      Fluttertoast.showToast(msg: "请求会话失败");
                    }
                  } else if (state is RequestAgentSuccess) {
                    // 请求人工客服，不管此工作组是否设置为默认机器人，只要有人工客服在线，则可以直接对接人工
                    setState(() {
                      _isRobot = false; // 需要，勿删
                      _currentThread = state.threadResult.msg!.thread;
                    });
                    // 插入本地
                    _messageProvider.insert(state.threadResult.msg!);
                    // _appendMessage(state.threadResult.msg!);
                  }
                },
              ),
              BlocListener<MessageBloc, MessageState>(
                listener: (context, state) {
                  debugPrint('message state change');
                  if (state is ReceiveMessageState) {
                    BytedeskUtils.printLog(
                        'receive message:${state.message!.content!}');
                  } else if (state is UploadImageSuccess) {
                    _bdMqtt.sendImageMessage(
                        state.uploadJsonResult.url!, _currentThread!);
                  } else if (state is UploadVideoSuccess) {
                    _bdMqtt.sendVideoMessage(
                        state.uploadJsonResult.url!, _currentThread!);
                  } else if (state is QueryAnswerSuccess) {
                    Message queryMessage = state.query!;
                    queryMessage.isSend = 1;
                    _messageProvider.insert(queryMessage);
                    _appendMessage(queryMessage);
                    //
                    _messageProvider.insert(state.answer!);
                    _appendMessage(state.answer!);
                  } else if (state is MessageAnswerSuccess) {
                    Message queryMessage = state.query!;
                    queryMessage.isSend = 1;
                    _messageProvider.insert(queryMessage);
                    _appendMessage(queryMessage);
                    //
                    if (state.query!.content!.contains('人工')) {
                      BlocProvider.of<ThreadBloc>(context)
                        .add(RequestAgentEvent(
                            wid: widget.wid,
                            aid: widget.aid,
                            type: widget.type));
                    } else {
                      _messageProvider.insert(state.answer!);
                      _appendMessage(state.answer!);
                    }
                  } else if (state is RateAnswerSuccess) {}
                },
              ),
            ],
            child: Container(
              alignment: Alignment.bottomCenter,
              color: const Color(0xffdeeeeee),
              child: Column(
                children: <Widget>[
                  // Expanded(
                  //   child: RefreshIndicator(
                  //     child: ListView.builder(
                  //       padding: EdgeInsets.all(8.0),
                  //       // reverse: true,
                  //       controller: _scrollController,
                  //       itemCount: _messages.length,
                  //       itemBuilder: (_, int index) => _messages[index],
                  //     ),
                  //     onRefresh: _loadMoreMessages,
                  //   ),
                  // ),
                  // Expanded(
                  //   child: ListView.builder(
                  //     padding: new EdgeInsets.all(8.0),
                  //     reverse: true,
                  //     shrinkWrap: true,
                  //     controller: _scrollController,
                  //     itemBuilder: (_, int index) => _messages[index],
                  //     itemCount: _messages.length,
                  //   ),
                  // ),
                  // 参考pull_to_refresh库中 QQChatList例子
                  Expanded(
                    child: SmartRefresher(
                      enablePullDown: false,
                      onLoading: () async {
                        debugPrint('TODO: 下拉刷新'); // 注意：方向跟默认是反着的
                        // await Future.delayed(Duration(milliseconds: 1000));
                        _getMessages(_page, _size);
                        setState(() {});
                        _refreshController.loadComplete();
                      },
                      footer: const ClassicFooter(
                        loadStyle: LoadStyle.ShowWhenLoading,
                      ),
                      enablePullUp: true,
                      controller: _refreshController,
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
                    child: _textComposerWidget(),
                  ),
                ],
              ),
            )));
  }

  //
  Widget _textComposerWidget() {
    return IconTheme(
      data: const IconThemeData(color: Colors.blue),
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 8.0),
        height: 54,
        child: Row(
          children: <Widget>[
            Container(
              // margin: const EdgeInsets.symmetric(horizontal: 4.0),
              child: IconButton(
                icon: const Icon(Icons.add),
                onPressed: () {
                  // _getImage();
                  showModalBottomSheet(
                      context: context,
                      builder: (context) {
                        return MediaChooseWidget(pickImageCallBack: () {
                          _pickImage();
                        }, takeImageCallBack: () {
                          _takeImage();
                        }, pickVideoCallBack: () {
                          _pickVideo();
                        }, takeVideoCallBack: () {
                          _captureVideo();
                        });
                      });
                },
              ),
            ),
            Flexible(
              child: TextField(
                onChanged: (value) {
                  if (_debounce?.isActive ?? false) _debounce!.cancel();
                  // 积累500毫秒，再发送。否则发送过于频繁
                  _debounce = Timer(const Duration(milliseconds: 500), () {
                    debugPrint('send preview $value');
                    // 发送预知消息 value != null &&
                    if (value.trim().isNotEmpty) {
                      _bdMqtt.sendPreviewMessage(value, _currentThread!);
                    } else {
                      _bdMqtt.sendPreviewMessage('', _currentThread!);
                    }
                  });
                },
                decoration: const InputDecoration.collapsed(hintText: "输入内容..."),
                controller: _textController,
                onSubmitted: _handleSubmitted,
              ),
            ),
            Container(
              margin: const EdgeInsets.symmetric(horizontal: 4.0),
              child: IconButton(
                icon: const Icon(Icons.send),
                onPressed: () => _handleSubmitted(_textController.text),
              ),
            )
          ],
        ),
      ),
    );
  }

  //
  @override
  bool get wantKeepAlive => true;

  //
  void _handleSubmitted(String? text) {
    _textController.clear();
    //
    if (text!.trim().isEmpty) {
      return;
    }
    //
    if (_isRobot) {
      BlocProvider.of<MessageBloc>(context)
        .add(MessageAnswerEvent(
            // type: widget.type,
            wid: widget.wid,
            // aid: widget.aid,
            content: text));
    } else {
      if (_currentThread == null) {
        Fluttertoast.showToast(msg: '请求客服中, 请稍后...');
        return;
      }
      _bdMqtt.sendTextMessage(text, _currentThread!);
    }
  }

  //
  _listener() {
    // 更新消息状态
    bytedeskEventBus.on<ReceiveMessageReceiptEventBus>().listen((event) {
      // debugPrint('更新状态:' + event.status);
      if (!mounted) {
        return;
      }
      // 更新界面, FIXME: 只有插入新消息，才会更新？
      for (var i = 0; i < _messages.length; i++) {
        MessageWidget messageWidget = _messages[i];
        if (messageWidget.message!.mid == event.mid &&
            _messages[i].message!.status !=
                BytedeskConstants.MESSAGE_STATUS_READ) {
          setState(() {
            _messages[i].message!.status = event.status;
          });
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
    // 同 DeleteMessageEventBus 事件
    // bytedeskEventBus.on<ReceiveMessageRecallEventBus>().listen((event) {
    //   debugPrint('消息撤回');
    // });
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
      // 界面显示
      MessageWidget messageWidget = MessageWidget(
          message: event.message,
          customCallback: widget.customCallback,
          animationController: AnimationController(
              vsync: this, duration: const Duration(milliseconds: 500)));
      if (mounted) {
        setState(() {
          _messages.insert(0, messageWidget);
          // _messages.add(messageWidget);
        });
      }
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
      //
      if (mounted) {
        BytedeskUtils.printLog(
            'aid ${event.aid}, question ${event.question}, answer ${event.answer}');
        // 可以直接将问题和答案插入本地，并显示，但为了服务器保存查询记录，特将请求发送给服务器
        // BlocProvider.of<MessageBloc>(context)
        //   ..add(QueryAnswerEvent(
        //     tid: _currentThread!.tid,
        //     aid: event.aid,
        //   ));
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
        BlocProvider.of<MessageBloc>(context)
          .add(UploadImageEvent(filePath: pickedFile.path));
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
        BlocProvider.of<MessageBloc>(context)
          .add(UploadImageEvent(filePath: pickedFile.path));
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
        BlocProvider.of<MessageBloc>(context)
          .add(UploadVideoEvent(filePath: pickedFile.path));
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
        BlocProvider.of<MessageBloc>(context)
          .add(UploadVideoEvent(filePath: pickedFile.path));
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
  Future<void> _getMessages(int page, int size) async {
    //
    List<Message> messageList = await _messageProvider.getTopicMessages(
        _currentThread!.topic, _currentUid, page, size);
    for (var message in messageList) {
      MessageWidget messageWidget = MessageWidget(
          message: message,
          customCallback: widget.customCallback,
          animationController: AnimationController(
              vsync: this, duration: const Duration(milliseconds: 500)));
      if (mounted) {
        setState(() {
          // _messages.insert(0, messageWidget);
          _messages.add(messageWidget);
        });
      }
    }
    //
    _page += 1;
  }

  Future<void> _appendMessage(Message message) async {
    // debugPrint('append:' + message!.mid);
    _messageProvider.insert(message);
    MessageWidget messageWidget = MessageWidget(
        message: message,
        customCallback: widget.customCallback,
        animationController: AnimationController(
            vsync: this, duration: const Duration(milliseconds: 500)));
    if (mounted) {
      setState(() {
        _messages.insert(0, messageWidget);
      });
    }
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
    super.dispose();
  }
}
