import 'package:bytedesk_kefu/blocs/feedback_bloc/bloc.dart';
import 'package:bytedesk_kefu/ui/feedback/page/feedback_history_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class FeedbackHistoryProvider extends StatelessWidget {
  const FeedbackHistoryProvider({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    //
    return BlocProvider(
      create: (BuildContext context) => FeedbackBloc()..add(const GetMyFeedbackEvent()),
      child: const FeedbackHistoryPage(),
    );
  }
}
