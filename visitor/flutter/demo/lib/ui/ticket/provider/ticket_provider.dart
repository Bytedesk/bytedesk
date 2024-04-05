import 'package:bytedesk_kefu/blocs/ticket_bloc/ticket_bloc.dart';
import 'package:bytedesk_kefu/ui/ticket/page/ticket_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class TicketProvider extends StatelessWidget {
  final String? uid;
  const TicketProvider({Key? key, @required this.uid}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (BuildContext context) => TicketBloc(),
      child: TicketPage(uid: uid),
    );
  }
}
