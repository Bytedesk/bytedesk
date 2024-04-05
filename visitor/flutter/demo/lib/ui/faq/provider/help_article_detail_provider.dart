import 'package:bytedesk_kefu/blocs/help_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/helpArticle.dart';
import 'package:bytedesk_kefu/ui/faq/page/help_article_detail_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class HelpArticleDetailProvider extends StatelessWidget {
  final HelpArticle? helpArticle;
  const HelpArticleDetailProvider({Key? key, this.helpArticle})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    //
    return BlocProvider(
      create: (BuildContext context) => HelpBloc(),
      child: HelpArticleDetailPage(helpArticle: helpArticle),
    );
  }
}
