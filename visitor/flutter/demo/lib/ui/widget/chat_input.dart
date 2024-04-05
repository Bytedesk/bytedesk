import 'dart:async';

import 'package:bytedesk_kefu/ui/widget/emoji_picker_view.dart';
import 'package:bytedesk_kefu/ui/widget/image_button.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import './emoji_picker_flutter/emoji_picker_flutter.dart';
// import 'package:bytedesk_kefu/ui/widget/send_button_visibility_mode.dart';
// import 'package:emoji_picker_flutter/emoji_picker_flutter.dart';
// import 'package:emoji_picker_flutter/src/emoji_view_state.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'send_button_visibility_mode.dart';
// import 'package:flutter_chat_types/flutter_chat_types.dart' as types;
// import 'package:flutter_chat_ui/flutter_chat_ui.dart';
// import 'package:flutter_chat_ui/src/widgets/inherited_chat_theme.dart';

// 部分代码来自该项目，感谢作者 CaiJingLong https://github.com/CaiJingLong/flutter_like_wechat_input
enum InputType {
  text,
  voice,
  emoji,
  extra,
}

Widget _buildVoiceButton(BuildContext context) {
  return SizedBox(
    width: double.infinity,
    child: TextButton(
      // color: Colors.white70,
      onPressed: () {
        // Get.snackbar('Tips', '语音输入功能暂无实现');
      },
      child: const Text(
        'chat_hold_down_talk',
      ),
    ),
  );
}

typedef OnSend = void Function(String text);

InputType _initType = InputType.text;

double _softKeyHeight = 210;

class ChatInput extends StatefulWidget {
  const ChatInput({
    Key? key,
    required this.isRobot,
    this.isAttachmentUploading,
    this.onAttachmentPressed,
    required this.onSendPressed,
    this.onTextChanged,
    this.onTextFieldTap,
    required this.sendButtonVisibilityMode,
    this.extraWidget,
    this.voiceWidget,
  }) : super(key: key);

  /// See [AttachmentButton.onPressed]
  final void Function()? onAttachmentPressed;

  // 是否是机器人状态
  final bool? isRobot;

  /// Whether attachment is uploading. Will replace attachment button with a
  /// [CircularProgressIndicator]. Since we don't have libraries for
  /// managing media in dependencies we have no way of knowing if
  /// something is uploading so you need to set this manually.
  final bool? isAttachmentUploading;

  /// Will be called on [SendButton] tap. Has [types.PartialText] which can
  /// be transformed to [types.TextMessage] and added to the messages list.
  // final Future<bool> Function(types.PartialText) onSendPressed;
  final Future<bool> Function(String) onSendPressed;

  /// Will be called whenever the text inside [TextField] changes
  final void Function(String)? onTextChanged;

  /// Will be called on [TextField] tap
  final void Function()? onTextFieldTap;

  /// Controls the visibility behavior of the [SendButton] based on the
  /// [TextField] state inside the [Input] widget.
  /// Defaults to [SendButtonVisibilityMode.editing].
  final SendButtonVisibilityMode sendButtonVisibilityMode;

  final Widget? extraWidget;
  final Widget? voiceWidget;

  @override
  State<ChatInput> createState() => _ChatInputState();
}

/// [Input] widget state
class _ChatInputState extends State<ChatInput> with TickerProviderStateMixin {
  InputType inputType = _initType;
  final _inputFocusNode = FocusNode();
  // bool _sendButtonVisible = false;
  final _textController = TextEditingController();
  late AnimationController _bottomHeightController;
  bool emojiShowing = false;
  // https://stackoverflow.com/questions/60057840/flutter-how-to-insert-text-in-middle-of-text-field-text
  void _setText(String val) {
    String text = _textController.text;
    TextSelection textSelection = _textController.selection;
    int start = textSelection.start > -1 ? textSelection.start : 0;
    String newText = text.replaceRange(
      start,
      textSelection.end > -1 ? textSelection.end : 0,
      val,
    );
    _textController.text = newText;
    int offset = start + val.length;
    _textController.selection = textSelection.copyWith(
      baseOffset: offset,
      extentOffset: offset,
    );
  }

  @override
  void initState() {
    super.initState();
    if (!mounted) {
      return;
    }
    if (widget.sendButtonVisibilityMode == SendButtonVisibilityMode.editing) {
      // _sendButtonVisible = _textController.text.trim() != '';
      _textController.addListener(_handleTextControllerChange);
    } else {
      // _sendButtonVisible = true;
    }

    _bottomHeightController = AnimationController(
      vsync: this,
      duration: const Duration(
        milliseconds: 150,
      ),
    );
    // 解决"重新进入聊天页面的时候_bottomHeightController在开启状态"的问题
    _bottomHeightController.animateBack(0);

    // 接收到新的消息订阅
    // eventBus.on<ReEditMessage>().listen((msg) async {
    //   if (_textController.text.toString() != msg.text) {
    //     _setText(msg.text);
    //   }
    // });
    //添加listener监听
    //对应的TextField失去或者获取焦点都会回调此监听
    _inputFocusNode.addListener(() {
      // debugPrint(">>> on chatinput ${_inputFocusNode.hasFocus}");
      if (_inputFocusNode.hasFocus) {
        updateState(InputType.text);
      } else {}
    });
  }

  @override
  void dispose() {
    // Get.delete<AnimationController>();
    _inputFocusNode.dispose();
    _textController.dispose();
    super.dispose();
  }

  Future<void> _handleSendPressed() async {
    final trimmedText = _textController.text.trim();
    if (trimmedText != '') {
      // final _partialText = types.PartialText(text: trimmedText);
      // bool res = await widget.onSendPressed(_partialText);
      bool res = await widget.onSendPressed(trimmedText);
      if (res) {
        _textController.clear();
      } else {
        // WSService.to.openSocket();
        // 网络原因，发送失败
      }
    }
  }

  void _handleTextControllerChange() {
    setState(() {
      // _sendButtonVisible = _textController.text.trim() != '';
    });
  }

  void changeBottomHeight(final double height) {
    if (height > 0) {
      _bottomHeightController.animateTo(1);
    } else {
      _bottomHeightController.animateBack(0);
    }
  }

  // 语音按钮事件
  Future<void> _voiceBtnOnPressed(InputType type) async {
    if (type == inputType) {
      return;
    }
    if (type != InputType.text) {
      hideSoftKey();
    } else {
      showSoftKey();
    }

    setState(() {
      inputType = type;
    });
  }

  Future<void> updateState(InputType type) async {
    if (type == InputType.text || type == InputType.voice) {
      _initType = type;
    }
    if (type == inputType) {
      return;
    }
    inputType = type;
    // InputTypeNotification(type).dispatch(context);

    if (type != InputType.text) {
      hideSoftKey();
    } else {
      showSoftKey();
    }

    if (type == InputType.emoji || type == InputType.extra) {
      changeBottomHeight(1);
      hideSoftKey();
    } else {
      changeBottomHeight(0);
    }

    setState(() {
      emojiShowing = type == InputType.emoji;
      inputType;
    });
  }

  void showSoftKey() {
    FocusScope.of(context).requestFocus(_inputFocusNode);
    changeBottomHeight(0);
    // debugPrint(">>> on chatinput showSoftKey");
  }

  void hideSoftKey() {
    _inputFocusNode.unfocus();

    // 隐藏键盘而不丢失文本字段焦点：from https://developer.aliyun.com/article/763095
    SystemChannels.textInput.invokeMethod('TextInput.hide');
  }

  Widget _buildBottomContainer({required Widget child}) {
    return SizeTransition(
      sizeFactor: _bottomHeightController,
      child: SizedBox(
        height: _softKeyHeight,
        child: child,
      ),
    );
  }

  Widget _buildBottomItems() {
    if (inputType == InputType.extra) {
      return widget.extraWidget ?? const Center(child: Text("其他item"));
    } else if (inputType == InputType.emoji) {
      return Offstage(
        offstage: !emojiShowing,
        child: SizedBox(
          height: 400,
          child: EmojiPicker(
            onEmojiSelected: (Category category, Emoji emoji) {
              _setText(emoji.emoji);
            },
            onBackspacePressed: () {
              _textController
                ..text = _textController.text.characters.skipLast(1).toString()
                ..selection = TextSelection.fromPosition(
                  TextPosition(offset: _textController.text.length),
                );
            },
            config: const Config(
              columns: 7,
              // Issue: https://github.com/flutter/flutter/issues/28894
              // emojiSizeMax: 24 * (GetPlatform.isIOS ? 1.30 : 1.0),
              verticalSpacing: 0,
              horizontalSpacing: 0,
              initCategory: Category.RECENT,
              bgColor: Color(0xFFF2F2F2),
              indicatorColor: Colors.black87,
              iconColorSelected: Colors.black87,
              iconColor: Colors.grey,
              progressIndicatorColor: Colors.blue,
              backspaceColor: Colors.black54,
              showRecentsTab: true,
              recentsLimit: 19,
              // noRecentsText: 'No Recents'.tr,
              // noRecentsStyle: const TextStyle(
              //   fontSize: 20,
              //   color: Colors.black87,
              // ),
              tabIndicatorAnimDuration: kTabScrollDuration,
              categoryIcons: CategoryIcons(),
              buttonMode: ButtonMode.MATERIAL,
            ),
            customWidget: (Config config, EmojiViewState state) =>
                EmojiPickerView(
              config,
              state,
              _handleSendPressed,
            ),
          ),
        ),
      );
    } else {
      return Container();
    }
  }

  Widget _buildInputButton(BuildContext ctx) {
    final voiceButton = widget.voiceWidget ?? _buildVoiceButton(ctx);
    final inputButton = TextField(
      controller: _textController,
      decoration: InputDecoration(
        hintText: '请简单描述您的问题',
        suffixIcon: _textController.text.isNotEmpty
            ? IconButton(
                onPressed: _textController.clear,
                icon: const Icon(Icons.clear),
              )
            : const Text(''),
      ),
      // cursorColor: InheritedChatTheme.of(ctx).theme.inputTextCursorColor,
      // decoration: InheritedChatTheme.of(ctx).theme.inputTextDecoration.copyWith(
      //       hintStyle: InheritedChatTheme.of(ctx).theme.inputTextStyle.copyWith(
      //             color: InheritedChatTheme.of(ctx)
      //                 .theme
      //                 .inputTextColor
      //                 .withOpacity(0.5),
      //           ),
      //       hintText: '',
      //     ),
      focusNode: _inputFocusNode,
      // maxLength: 400,
      maxLines: 6,
      minLines: 1,
      // 长按是否展示【剪切/复制/粘贴菜单LengthLimitingTextInputFormatter】
      enableInteractiveSelection: true,
      keyboardType: TextInputType.multiline,
      textCapitalization: TextCapitalization.sentences,
      textInputAction: TextInputAction.newline,
      onChanged: widget.onTextChanged,
      onTap: () {
        updateState(inputType);
        widget.onTextFieldTap;
      },
      // style: InheritedChatTheme.of(ctx).theme.inputTextStyle.copyWith(
      //       color: InheritedChatTheme.of(ctx).theme.inputTextColor,
      //     ),
      // 点击键盘的动作按钮时的回调，参数为当前输入框中的值
      onSubmitted: (_) => _handleSendPressed(),
    );

    return Stack(
      children: <Widget>[
        Offstage(
          offstage: inputType == InputType.voice,
          child: inputButton,
        ),
        Offstage(
          offstage: inputType != InputType.voice,
          child: voiceButton,
        ),
      ],
    );
  }

  Widget buildLeftButton() {
    return ImageButton(
      onPressed: () {
        if (inputType == InputType.voice) {
          _voiceBtnOnPressed(InputType.text);
        } else {
          _voiceBtnOnPressed(InputType.voice);
        }
        changeBottomHeight(0);
      },
      image: AssetImage(
        inputType != InputType.voice
            ? 'assets/images/chat/input_voice.png'
            : 'assets/images/chat/input_keyboard.png',
      ),
    );
  }

  // FIXME: 表情在web和Android有问题？暂时不在web和Android启用表情
  Widget buildEmojiButton() {
    if (BytedeskUtils.isIOS) {
      return ImageButton(
        image: AssetImage(inputType != InputType.emoji
            ? 'assets/images/chat/input_emoji.png'
            : 'assets/images/chat/input_keyboard.png'),
        onPressed: () {
          if (inputType != InputType.emoji) {
            updateState(InputType.emoji);
          } else {
            updateState(InputType.text);
          }
        },
      );
    }
    return const Text('');
  }

  // 更多输入消息类型入口
  Widget buildExtra() {
    return ImageButton(
      image: const AssetImage('assets/images/chat/input_extra.png'),
      onPressed: () {
        if (inputType != InputType.extra) {
          updateState(InputType.extra);
        } else {
          updateState(InputType.text);
        }
      },
    );
  }

  // 实现换行效果
  // void _handleNewLine() {
  //   final _newValue = '${_textController.text}\r\n';
  //   _textController.value = TextEditingValue(
  //     text: _newValue,
  //     selection: TextSelection.fromPosition(
  //       TextPosition(offset: _newValue.length),
  //     ),
  //   );
  // }

  @override
  Widget build(BuildContext context) {
    final query = MediaQuery.of(context);

    // final isAndroid = Theme.of(context).platform == TargetPlatform.android;
    // final isIOS = Theme.of(context).platform == TargetPlatform.iOS;

    return InkWell(
      child: Focus(
        autofocus: true,
        child: Padding(
          padding: const EdgeInsets.fromLTRB(
              0, 0, 0, 0), //InheritedChatTheme.of(context).theme.inputPadding,
          child: Material(
            borderRadius: const BorderRadius.vertical(
              top: Radius.circular(10),
            ),
            //InheritedChatTheme.of(context).theme.inputBorderRadius,
            // color: Color.white, //Color(0xff1d1c21),//InheritedChatTheme.of(context).theme.inputBackgroundColor,
            child: Container(
              padding: EdgeInsets.fromLTRB(
                query.padding.left,
                4,
                query.padding.right,
                4 + query.viewInsets.bottom + query.padding.bottom,
              ),
              child: Column(
                children: <Widget>[
                  Row(
                    children: [
                      // TODO: 录制语音
                      // buildLeftButton(),
                      const SizedBox(
                        width: 10,
                      ),
                      // input
                      Expanded(
                        child: _buildInputButton(context),
                        // child: Shortcuts(
                        //   shortcuts: isAndroid || isIOS
                        //       ? {
                        //           LogicalKeySet(LogicalKeyboardKey.enter):
                        //               const NewLineIntent(),
                        //           LogicalKeySet(LogicalKeyboardKey.enter,
                        //                   LogicalKeyboardKey.alt):
                        //               const NewLineIntent(),
                        //         }
                        //       : {
                        //           LogicalKeySet(LogicalKeyboardKey.enter):
                        //               const SendMessageIntent(),
                        //           LogicalKeySet(LogicalKeyboardKey.enter,
                        //                   LogicalKeyboardKey.alt):
                        //               const NewLineIntent(),
                        //           LogicalKeySet(LogicalKeyboardKey.enter,
                        //                   LogicalKeyboardKey.shift):
                        //               const NewLineIntent(),
                        //         },
                        //   child: Actions(
                        //     actions: {
                        //       // SendMessageIntent:
                        //       //     CallbackAction<SendMessageIntent>(
                        //       //   onInvoke: (SendMessageIntent intent) =>
                        //       //       _handleSendPressed(),
                        //       // ),
                        //       // NewLineIntent: CallbackAction<NewLineIntent>(
                        //       //   onInvoke: (NewLineIntent intent) =>
                        //       //       _handleNewLine(),
                        //       // ),
                        //     },
                        //     child: _buildInputButton(context),
                        //   ),
                        // ),
                      ),
                      // emoji
                      // buildEmojiButton(),
                      //extra
                      getExtraWidget(),
                      // _textController.text.isEmpty
                      //     ? buildExtra()
                      //     : IconButton(
                      //         icon: const Icon(Icons.send),
                      //         onPressed: _handleSendPressed,
                      //         padding: const EdgeInsets.only(left: 0),
                      //       ),
                    ],
                  ),
                  inputType == InputType.emoji || inputType == InputType.extra
                      ? const Divider()
                      : const SizedBox.shrink(), // 横线
                  _buildBottomContainer(child: _buildBottomItems()),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget getExtraWidget() {
    if (widget.isRobot!) {
      return IconButton(
        icon: const Icon(Icons.send),
        onPressed: _handleSendPressed,
        padding: const EdgeInsets.only(left: 0),
      );
    }
    return _textController.text.isEmpty
        ? buildExtra()
        : IconButton(
            icon: const Icon(Icons.send),
            onPressed: _handleSendPressed,
            padding: const EdgeInsets.only(left: 0),
          );
  }
}
