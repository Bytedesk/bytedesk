import 'package:bytedesk_kefu/blocs/feedback_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/ui/feedback/page/feedback_submit_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class FeedbackSubmitProvider extends StatelessWidget {
  final HelpCategory? helpCategory;
  const FeedbackSubmitProvider({Key? key, this.helpCategory}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    //
    return BlocProvider(
      create: (BuildContext context) => FeedbackBloc(),
      child: FeedbackSubmitPage(
        helpCategory: helpCategory,
      ),
    );
  }
}
