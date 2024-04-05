import 'package:bytedesk_kefu/blocs/help_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/helpArticle.dart';
import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/ui/faq/provider/help_article_detail_provider.dart';
import 'package:bytedesk_kefu/ui/widget/empty_widget.dart';
// import 'package:bytedesk_kefu/ui/widget/empty_widget.dart';
import 'package:bytedesk_kefu/ui/widget/expanded_viewport.dart';
// import 'package:bytedesk_kefu/ui/widget/loading_widget.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
// import 'package:easy_refresh/easy_refresh.dart';
// import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';

class HelpArticleListPage extends StatefulWidget {
  //
  final HelpCategory? helpCategory;
  const HelpArticleListPage({Key? key, this.helpCategory}) : super(key: key);

  @override
  State<HelpArticleListPage> createState() => _HelpArticleListPageState();
}

class _HelpArticleListPageState extends State<HelpArticleListPage>
    with AutomaticKeepAliveClientMixin<HelpArticleListPage> {
  //
  Set<HelpArticle>? _articleList;
  HelpBloc? _helpBloc;
  // 下拉刷新
  final RefreshController _refreshController = RefreshController();
  // 滚动监听
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    //
    _articleList = Set.from([]);
    _helpBloc = BlocProvider.of<HelpBloc>(context);
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    debugPrint("HelpArticleListPage categoryId ${widget.helpCategory!.id!}");
    //
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.helpCategory!.name!),
        centerTitle: true,
      ),
      body: BlocConsumer<HelpBloc, HelpState>(listener: (context, state) {
        // do stuff here based on BlocA's state
        debugPrint("HelpArticleListPage HelpBloc $state");
        //
        if (state is GetArticleSuccess) {
          debugPrint(
              'help article load success length: ${state.articleList.length}');
          if (state.articleList.isEmpty) {
            // Toast.show('没有更多了哦');
          } else {
            // TODO: 加载下来的数据本地缓存
            _articleList!.addAll(state.articleList);
          }
        }
      }, builder: (context, state) {
        // return widget here based on BlocA's state

        return SmartRefresher(
          // controller: EasyRefreshController(),
          controller: _refreshController,
          // header: PhoenixHeader(),
          // firstRefresh: true,
          // firstRefreshWidget: LoadingWidget(),
          // emptyWidget: _articleList!.length == 0
          //     ? EmptyWidget(
          //         tip: '未找到相关数据',
          //       )
          //     : null,
          onRefresh: () async {
            _helpBloc!
                .add(GetHelpArticleEvent(categoryId: widget.helpCategory!.id));
          },
          onLoading: () async {
            _helpBloc!
                .add(GetHelpArticleEvent(categoryId: widget.helpCategory!.id));
          },
          child: _articleList!.isEmpty
              ? const EmptyWidget(
                  tip: "内容为空",
                )
              : Scrollable(
            controller: _scrollController,
            axisDirection: AxisDirection.up,
            viewportBuilder: (context, offset) {
              return ExpandedViewport(
                offset: offset,
                axisDirection: AxisDirection.up,
                slivers: <Widget>[
                  SliverExpanded(),
                  SliverList(
                    delegate: SliverChildBuilderDelegate(
                      (context, index) {
                        return Card(
                          child: ListTile(
                                  // leading: new Icon(Icons.message),
                                  title: Text(
                                      _articleList!.elementAt(index).title!),
                                  trailing:
                                      const Icon(Icons.keyboard_arrow_right),
                                  onTap: () {
                                    //
                                    Navigator.of(context).push(
                                        MaterialPageRoute(builder: (context) {
                                      return HelpArticleDetailProvider(
                                        helpArticle:
                                            _articleList!.elementAt(index),
                                      );
                                    }));
                                  },
                                ),
                        );
                      },
                      childCount: _articleList!.length,
                    ),
                  ),
                ],
              );
            },
          ),
        );
      }),
    );
  }

  @override
  bool get wantKeepAlive => true;
}
