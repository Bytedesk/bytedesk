// ignore_for_file: library_private_types_in_public_api

import 'package:flutter/material.dart';

class TicketPage extends StatefulWidget {
  final String? uid;
  const TicketPage({Key? key, @required this.uid}) : super(key: key);

  @override
  _TicketPageState createState() => _TicketPageState();
}

class _TicketPageState extends State<TicketPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('工单记录'),
        centerTitle: true,
        actions: const [
          // TODO: 提交工单
        ],
      ),
      // TODO: 历史工单：待我回复、等待回复、已结束
      body: const Text('ticket'),
    );
  }
}
