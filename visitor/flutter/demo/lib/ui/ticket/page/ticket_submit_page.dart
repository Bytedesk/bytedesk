// ignore_for_file: library_private_types_in_public_api

import 'package:flutter/material.dart';

class TicketSubmitPage extends StatefulWidget {
  const TicketSubmitPage({Key? key}) : super(key: key);

  @override
  _TicketSubmitPageState createState() => _TicketSubmitPageState();
}

class _TicketSubmitPageState extends State<TicketSubmitPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('提交工单'),
        centerTitle: true,
      ),
      body: const Text('ticket'),
    );
  }
}
