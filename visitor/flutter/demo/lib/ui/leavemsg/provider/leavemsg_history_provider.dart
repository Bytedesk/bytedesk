import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../blocs/leavemsg_bloc/leavemsg_bloc.dart';
import '../page/leavemsg_history_page.dart';

class LeaveMsgHistoryProvider extends StatelessWidget {

  final String? wid;
  final String? aid;
  final String? type;
  const LeaveMsgHistoryProvider(
      {Key? key,
      @required this.wid,
      @required this.aid,
      @required this.type})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (BuildContext context) => LeaveMsgBloc(),
      child: LeaveMsgHistoryPage(
        wid: wid,
        aid: aid,
        type: type,
      ),
    );
  }
}
