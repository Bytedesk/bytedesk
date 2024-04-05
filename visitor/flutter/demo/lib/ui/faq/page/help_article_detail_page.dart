import 'package:bytedesk_kefu/blocs/help_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/helpArticle.dart';
import 'package:bytedesk_kefu/ui/chat/widget/flutter_html/flutter_html.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class HelpArticleDetailPage extends StatefulWidget {
  final HelpArticle? helpArticle;
  const HelpArticleDetailPage({Key? key, this.helpArticle}) : super(key: key);

  @override
  State<HelpArticleDetailPage> createState() => _HelpArticleDetailPageState();
}

class _HelpArticleDetailPageState extends State<HelpArticleDetailPage>
    with AutomaticKeepAliveClientMixin<HelpArticleDetailPage> {
  @override
  Widget build(BuildContext context) {
    super.build(context);
    return Scaffold(
        appBar: AppBar(
          title: Text(widget.helpArticle!.title!),
          centerTitle: true,
        ),
        body: BlocConsumer<HelpBloc, HelpState>(listener: (context, state) {
          // do stuff here based on BlocA's state
        }, builder: (context, state) {
          // return widget here based on BlocA's state
          return Container(
              padding: const EdgeInsets.only(top: 5.0,),
              child: Column(
                children: <Widget>[
                  Html(data: widget.helpArticle!.content)
                  // TODO: 增加有帮助、无帮助按钮
                  
                  // TODO: 增加联系客服按钮
                  // TODO: 右上角支持浏览器打开？
                ],
              )
              );
        }),
        // bottomNavigationBar: BottomAppBar(
        //   child: Row(
        //     children: [
        //       InkWell(
        //         onTap: () => {
        //           //
        //         },
        //         child: const Column(
        //           // mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        //           children: <Widget>[
        //             // Icon(
        //             //   Icons.upcoming,
        //             //   color: Colors.grey,
        //             // ),
        //             Text('有帮助', style: TextStyle(fontSize: 13.0)),
        //           ],
        //         ),
        //       ),
        //       InkWell(
        //         onTap: () => {
        //           //
        //         },
        //         child: const Column(
        //           // mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        //           children: <Widget>[
        //             // Icon(
        //             //   Icons.upcoming,
        //             //   color: Colors.grey,
        //             // ),
        //             Text('无帮助', style: TextStyle(fontSize: 13.0)),
        //           ],
        //         ),
        //       ),
        //       InkWell(
        //         onTap: () => {
        //           //
        //         },
        //         child: const Column(
        //           // mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        //           children: <Widget>[
        //             // Icon(
        //             //   Icons.upcoming,
        //             //   color: Colors.grey,
        //             // ),
        //             Text('联系客服', style: TextStyle(fontSize: 13.0)),
        //           ],
        //         ),
        //       )
        //     ],
        //   )
        // ),
    );
  }

  @override
  bool get wantKeepAlive => true;
}
