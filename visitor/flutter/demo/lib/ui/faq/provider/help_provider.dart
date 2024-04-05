import 'package:bytedesk_kefu/blocs/help_bloc/bloc.dart';
import 'package:bytedesk_kefu/ui/faq/page/help_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class HelpProvider extends StatelessWidget {
  final String? uid;
  final String? title;
  const HelpProvider({Key? key, @required this.uid, this.title}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    //
    return BlocProvider(
      create: (BuildContext context) =>
          HelpBloc()..add(GetHelpCategoryEvent(uid: uid)),
      child: HelpPage(
        uid: uid,
        title: title,
      ),
    );
  }
}
