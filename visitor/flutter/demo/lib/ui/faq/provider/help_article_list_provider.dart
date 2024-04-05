import 'package:bytedesk_kefu/blocs/help_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/ui/faq/page/help_article_list_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class HelpArticleListProvider extends StatelessWidget {
  final HelpCategory? helpCategory;

  const HelpArticleListProvider({Key? key, this.helpCategory})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    //
    return BlocProvider(
      create: (BuildContext context) => HelpBloc()..add(GetHelpArticleEvent(categoryId: helpCategory!.id)),
      child: HelpArticleListPage(
        helpCategory: helpCategory,
      ),
    );
  }
}
