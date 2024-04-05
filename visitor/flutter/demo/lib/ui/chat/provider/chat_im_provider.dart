import 'package:bytedesk_kefu/blocs/message_bloc/bloc.dart';
import 'package:bytedesk_kefu/blocs/thread_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/thread.dart';
import 'package:bytedesk_kefu/ui/chat/page/chat_im_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class ChatIMProvider extends StatelessWidget {
  // final String? cid;
  final String? title;
  final String? custom;
  final String? postscript;
  final Thread? thread;
  final bool? isThread;
  const ChatIMProvider(
      {Key? key,
      this.title,
      this.custom,
      this.postscript,
      this.thread,
      this.isThread})
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
          create: (BuildContext context) => MessageBloc()
            ..add(
                LoadTopicMessageEvent(topic: thread!.topic, page: 0, size: 20)),
        ),
      ],
      child: ChatIMPage(
        // cid: cid,
        title: title,
        custom: custom,
        postscript: postscript,
        thread: thread,
        isThread: isThread,
      ),
    );
  }
}
