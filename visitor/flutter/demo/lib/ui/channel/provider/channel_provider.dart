// import 'package:bytedesk_kefu/blocs/message_bloc/bloc.dart';
// import 'package:bytedesk_kefu/model/thread.dart';
// import 'package:bytedesk_kefu/ui/channel/page/channel_page.dart';
// import 'package:flutter/material.dart';
// import 'package:flutter_bloc/flutter_bloc.dart';

// class ChannelProvider extends StatelessWidget {
//   final Thread? thread;
//   //
//   const ChannelProvider({Key? key, @required this.thread}) : super(key: key);

//   @override
//   Widget build(BuildContext context) {
//     //
//     return MultiBlocProvider(
//       providers: [
//         BlocProvider<MessageBloc>(
//             create: (BuildContext context) => MessageBloc()
//               ..add(LoadChannelMessageEvent(
//                   cid: thread!.topic, page: 0, size: 20))),
//       ],
//       child: ChannelPage(
//         thread: thread,
//       ),
//     );
//   }
// }
