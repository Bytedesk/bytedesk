// ignore_for_file: library_private_types_in_public_api

import 'package:bytedesk_kefu/model/message.dart';
import 'package:flutter/material.dart';

class ChannelDetailPage extends StatefulWidget {
  //
  final Message? message;
  const ChannelDetailPage({Key? key, @required this.message}) : super(key: key);

  @override
  _ChannelDetailPageState createState() => _ChannelDetailPageState();
}

class _ChannelDetailPageState extends State<ChannelDetailPage> {
  //
  String? _title = '';
  String? _content = '';

  @override
  void initState() {
    _title = widget.message!.channelTitle();
    _content = widget.message!.channelContent();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_title!),
      ),
      body: Text(_content!),
    );
  }
}
