import 'package:flutter/material.dart';

class MessageNotification extends StatelessWidget {
  //
  final VoidCallback? onReply;
  final String? avatar;
  final String? nickname;
  final String? content;

  const MessageNotification({
    Key? key,
    @required this.onReply,
    @required this.avatar,
    @required this.nickname,
    @required this.content,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 4),
      child: SafeArea(
        child: ListTile(
          leading: SizedBox.fromSize(
              size: const Size(40, 40),
              child: ClipOval(child: Image.network(avatar!))),
          title: Text(nickname!),
          subtitle: Text(content!),
          trailing: IconButton(
              icon: const Icon(Icons.reply),
              onPressed: () {
                if (onReply != null) onReply!();
              }),
        ),
      ),
    );
  }
}
