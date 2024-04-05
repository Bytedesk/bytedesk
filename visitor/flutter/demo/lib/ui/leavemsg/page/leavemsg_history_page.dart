// ignore_for_file: library_private_types_in_public_api

import 'package:flutter/material.dart';

class LeaveMsgHistoryPage extends StatefulWidget {
  //
  final String? wid;
  final String? aid;
  final String? type;
  //
  // String? mobile;
  // String? email;
  // String? content;
  //
  const LeaveMsgHistoryPage(
      {Key? key,
      @required this.wid,
      @required this.aid,
      @required this.type})
      : super(key: key);

  @override
  _LeaveMsgHistoryPageState createState() => _LeaveMsgHistoryPageState();
}

class _LeaveMsgHistoryPageState extends State<LeaveMsgHistoryPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("留言历史"),
        elevation: 0,
      ),
      body: Container(),
    );
  }
}
