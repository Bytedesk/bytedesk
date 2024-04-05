import 'package:bytedesk_kefu/blocs/feedback_bloc/bloc.dart';
import 'package:bytedesk_kefu/ui/feedback/page/feedback_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class FeedbackProvider extends StatelessWidget {
  final String? uid;
  const FeedbackProvider({Key? key, @required this.uid}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    //
    return BlocProvider(
      create: (BuildContext context) => FeedbackBloc()..add(GetFeedbackCategoryEvent(uid: uid)),
      child: FeedbackPage(uid: uid),
    );
  }
}
