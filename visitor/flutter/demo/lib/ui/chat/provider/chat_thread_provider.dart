import 'package:bytedesk_kefu/blocs/message_bloc/bloc.dart';
import 'package:bytedesk_kefu/blocs/thread_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/model.dart';
import 'package:bytedesk_kefu/ui/chat/page/chat_kf_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

// 点击通知栏进入聊天页面，无需请求会话
class ChatThreadProvider extends StatelessWidget {
  final Thread? thread;
  final String? title;
  const ChatThreadProvider({Key? key, this.thread, this.title})
      : super(key: key);
  @override
  Widget build(BuildContext context) {
    //
    return MultiBlocProvider(
      providers: [
        BlocProvider<ThreadBloc>(
          create: (BuildContext context) => ThreadBloc(),
        ),
        BlocProvider<MessageBloc>(
          create: (BuildContext context) => MessageBloc(),
        ),
      ],
      child: ChatKFPage(
        isThread: true,
        thread: thread,
        title: title,
      ),
    );
  }
}
