import 'package:bytedesk_kefu/blocs/message_bloc/bloc.dart';
import 'package:bytedesk_kefu/blocs/thread_bloc/bloc.dart';
import 'package:bytedesk_kefu/ui/chat/page/chat_gpt_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

// 需要请求会话
class ChatGPTProvider extends StatelessWidget {
  final String? wid;
  final String? aid;
  final String? type;
  final String? title;
  final String? custom;
  final String? postscript;
  final bool? isV2Robot;
  final ValueSetter<String>? customCallback;
  //
  const ChatGPTProvider(
      {Key? key,
      this.wid,
      this.aid,
      this.type,
      this.title,
      this.custom,
      this.postscript,
      this.isV2Robot,
      this.customCallback})
      : super(key: key);
  //
  @override
  Widget build(BuildContext context) {
    //
    return MultiBlocProvider(
      providers: [
        BlocProvider<ThreadBloc>(
          create: (BuildContext context) => ThreadBloc()
            ..add(RequestZhipuAIThreadEvent(wid: wid, forceNew: "0")),
        ),
        BlocProvider<MessageBloc>(
          create: (BuildContext context) => MessageBloc(),
        ),
      ],
      child: ChatGPTPage(
          wid: wid,
          aid: aid,
          type: type,
          title: title,
          custom: custom,
          postscript: postscript,
          isV2Robot: isV2Robot,
          isThread: false,
          customCallback: customCallback),
    );
  }
}
