// import 'package:bytedesk_kefu/blocs/message_bloc/bloc.dart';
// import 'package:bytedesk_kefu/model/message.dart';
// import 'package:bytedesk_kefu/model/thread.dart';
// import 'package:bytedesk_kefu/ui/channel/page/channel_detail_page.dart';
// import 'package:bytedesk_kefu/ui/widget/empty_widget.dart';
// import 'package:bytedesk_kefu/ui/widget/loading_widget.dart';
// import 'package:flutter/material.dart';
// import 'package:flutter_bloc/flutter_bloc.dart';
// import 'package:easy_refresh/easy_refresh.dart';

// class ChannelPage extends StatefulWidget {
//   final Thread? thread;

//   ChannelPage({Key? key, @required this.thread}) : super(key: key);

//   @override
//   _ChannelPageState createState() => _ChannelPageState();
// }

// class _ChannelPageState extends State<ChannelPage> {
//   List<Message> _messageList = [];

//   @override
//   void initState() {
//     super.initState();
//   }

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: Text(widget.thread!.nickname!),
//       ),
//       body: MultiBlocListener(
//         listeners: [
//           BlocListener<MessageBloc, MessageState>(listener: (context, state) {
//             if (state is LoadChannelMessageSuccess) {
//               setState(() {
//                 _messageList = state.messageList!;
//               });
//             }
//           })
//         ],
//         child: EasyRefresh.custom(
//           controller: EasyRefreshController(),
//           header: PhoenixHeader(),
//           firstRefresh: true,
//           firstRefreshWidget: LoadingWidget(),
//           emptyWidget: _messageList.length == 0
//               ? EmptyWidget(
//                   tip: '未找到相关数据',
//                 )
//               : null,
//           onRefresh: () async {},
//           onLoad: () async {},
//           slivers: <Widget>[
//             SliverList(
//               delegate: SliverChildBuilderDelegate(
//                 (context, index) {
//                   return new ListTile(
//                     // TODO: 显示时间戳
//                     title: Text(_messageList.elementAt(index).channelTitle()!),
//                     subtitle:
//                         Text(_messageList.elementAt(index).channelContent()!),
//                     trailing: Icon(Icons.keyboard_arrow_right),
//                     onTap: () {
//                       //
//                       Navigator.of(context)
//                           .push(new MaterialPageRoute(builder: (context) {
//                         return ChannelDetailPage(
//                             message: _messageList.elementAt(index));
//                       }));
//                     },
//                   );
//                 },
//                 childCount: _messageList.length,
//               ),
//             ),
//           ],
//         ),
//       ),
//     );
//   }
// }
