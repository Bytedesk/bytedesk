import 'package:bytedesk_kefu/blocs/feedback_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/ui/feedback/provider/feedback_history_provider.dart';
import 'package:bytedesk_kefu/ui/feedback/provider/feedback_submit_provider.dart';
// import 'package:bytedesk_kefu/ui/widget/empty_widget.dart';
import 'package:bytedesk_kefu/ui/widget/expanded_viewport.dart';
// import 'package:bytedesk_kefu/ui/widget/loading_widget.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
// import 'package:easy_refresh/easy_refresh.dart';
// import 'package:bytedesk_kefu/util/bytedesk_utils.dart';

class FeedbackPage extends StatefulWidget {
  final String? uid;
  const FeedbackPage({Key? key, @required this.uid}) : super(key: key);

  @override
  State<FeedbackPage> createState() => _FeedbackPageState();
}

class _FeedbackPageState extends State<FeedbackPage>
    with AutomaticKeepAliveClientMixin<FeedbackPage> {
  //
  Set<HelpCategory>? _categoryList;
  FeedbackBloc? _courseBloc;
  // 下拉刷新
  final RefreshController _refreshController = RefreshController();
  // 滚动监听
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    //
    _categoryList = Set.from([]);
    _courseBloc = BlocProvider.of<FeedbackBloc>(context);
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    //
    return Scaffold(
      appBar: AppBar(
        title: const Text('意见反馈'),
        centerTitle: true,
        actions: [
          Align(
            alignment: Alignment.centerRight,
            child: Container(
              margin: const EdgeInsets.only(right: 10),
              child: InkWell(
                onTap: () {
                  debugPrint('我的反馈');
                  Navigator.of(context)
                      .push(MaterialPageRoute(builder: (context) {
                    return const FeedbackHistoryProvider();
                  }));
                },
                child: const Text(
                  '我的反馈',
                ),
              ),
            ),
          )
        ],
      ),
      body:
          BlocConsumer<FeedbackBloc, FeedbackState>(listener: (context, state) {
        // do stuff here based on BlocA's state
        if (state is GetFeedbackCategorySuccess) {
          debugPrint('feedback category load success length: ${state.categoryList.length}');
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
            _courseBloc!.add(GetFeedbackCategoryEvent(uid: widget.uid));
          },
          onLoading: () async {
            // _courseBloc!.add(GetFeedbackCategoryEvent(uid: widget.uid));
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
                            Navigator.of(context)
                                .push(MaterialPageRoute(builder: (context) {
                              return FeedbackSubmitProvider(
                                helpCategory: _categoryList!.elementAt(index),
                              );
                            }));
                          },
                        ),
                      );
                    },
                    childCount: _categoryList!.length,
                  )
                      // delegate: SliverChildBuilderDelegate(
                      //   (c, i) => _categoryList!.elementAt(i),
                      //   childCount: _categoryList!.length
                      // ),
                      )
                ],
              );
            },
          ),
          // slivers: <Widget>[
          //   SliverList(
          // delegate: SliverChildBuilderDelegate(
          //   (context, index) {
          //     return ListTile(
          //       // leading: new Icon(Icons.message),
          //       title: Text(_categoryList!.elementAt(index).name!),
          //       trailing: const Icon(Icons.keyboard_arrow_right),
          //       onTap: () {
          //         Navigator.of(context)
          //             .push(MaterialPageRoute(builder: (context) {
          //           return FeedbackSubmitProvider(
          //             helpCategory: _categoryList!.elementAt(index),
          //           );
          //         }));
          //       },
          //     );
          //   },
          //   childCount: _categoryList!.length,
          //     ),
          //   ),
          // ],
        );
      }),
    );
  }

  @override
  bool get wantKeepAlive => true;
}
