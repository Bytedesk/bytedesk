import 'package:bytedesk_kefu/blocs/help_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/ui/faq/provider/help_article_list_provider.dart';
// import 'package:bytedesk_kefu/ui/widget/empty_widget.dart';
import 'package:bytedesk_kefu/ui/widget/expanded_viewport.dart';
// import 'package:bytedesk_kefu/ui/widget/loading_widget.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
// import 'package:easy_refresh/easy_refresh.dart';
// import 'package:bytedesk_kefu/util/bytedesk_utils.dart';

class HelpPage extends StatefulWidget {
  final String? uid;
  final String? title;
  const HelpPage({Key? key, @required this.uid, this.title}) : super(key: key);

  @override
  State<HelpPage> createState() => _HelpPageState();
}

class _HelpPageState extends State<HelpPage>
    with AutomaticKeepAliveClientMixin<HelpPage> {
  //
  // int _currentPage = 0;
  Set<HelpCategory>? _categoryList;
  HelpBloc? _courseBloc;
  // 下拉刷新
  final RefreshController _refreshController = RefreshController();
  // 滚动监听
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    //
    _categoryList = Set.from([]);
    _courseBloc = BlocProvider.of<HelpBloc>(context);
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    //
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title!),
        centerTitle: true,
      ),
      body: BlocConsumer<HelpBloc, HelpState>(listener: (context, state) {
        // do stuff here based on BlocA's state
        if (state is GetCategorySuccess) {
          debugPrint(
              'help category load success length: ${state.categoryList.length}');
          if (state.categoryList.isEmpty) {
            // Toast.show('没有更多了哦');
          } else {
            // TODO: 加载下来的数据本地缓存
            _categoryList!.addAll(state.categoryList);
          }
        }
      }, builder: (context, state) {
        // return widget here based on BlocA's state

        return SmartRefresher(
          controller: _refreshController,
          // header: PhoenixHeader(),
          // firstRefresh: true,
          // firstRefreshWidget: LoadingWidget(),
          // emptyWidget: _categoryList!.length == 0
          //     ? EmptyWidget(
          //         tip: '未找到相关数据',
          //       )
          //     : null,
          onRefresh: () async {
            _courseBloc!.add(GetHelpCategoryEvent(uid: widget.uid));
          },
          onLoading: () async {
            // _courseBloc!.add(GetHelpCategoryEvent(uid: widget.uid));
          },
          child: Scrollable(
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
                            title: Text(_categoryList!.elementAt(index).name!),
                            trailing: const Icon(Icons.keyboard_arrow_right),
                            onTap: () {
                              //
                              Navigator.of(context)
                                  .push(MaterialPageRoute(builder: (context) {
                                return HelpArticleListProvider(
                                  helpCategory: _categoryList!.elementAt(index),
                                );
                              }));
                            },
                          ),
                        );
                      },
                      childCount: _categoryList!.length,
                    ),
                  ),
                ],
              );
            },
          ),
          // slivers: <Widget>[
          // SliverList(
          //   delegate: SliverChildBuilderDelegate(
          //     (context, index) {
          //       return new ListTile(
          //         // leading: new Icon(Icons.message),
          //         title: new Text(_categoryList!.elementAt(index).name!),
          //         trailing: Icon(Icons.keyboard_arrow_right),
          //         onTap: () {
          //           //
          //           Navigator.of(context)
          //               .push(new MaterialPageRoute(builder: (context) {
          //             return new HelpArticleListProvider(
          //               helpCategory: _categoryList!.elementAt(index),
          //             );
          //           }));
          //         },
          //       );
          //     },
          //     childCount: _categoryList!.length,
          //   ),
          // ),
          // ],
        );
      }),
    );
  }

  @override
  bool get wantKeepAlive => true;
}
