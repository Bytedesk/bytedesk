import 'package:bytedesk_kefu/blocs/leavemsg_bloc/leavemsg_bloc.dart';
import 'package:bytedesk_kefu/ui/leavemsg/page/leavemsg_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class LeaveMsgProvider extends StatelessWidget {
  final String? wid;
  final String? aid;
  final String? type;
  final String? tip;
  const LeaveMsgProvider(
      {Key? key,
      @required this.wid,
      @required this.aid,
      @required this.type,
      @required this.tip})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (BuildContext context) => LeaveMsgBloc(),
      child: LeaveMsgPage(
        wid: wid,
        aid: aid,
        type: type,
        tip: tip,
      ),
    );
  }
}
