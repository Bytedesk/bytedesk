import 'package:bytedesk_kefu/blocs/feedback_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/model.dart';
import 'package:bytedesk_kefu/ui/widget/empty_widget.dart';
import 'package:bytedesk_kefu/ui/widget/expanded_viewport.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';

class FeedbackHistoryPage extends StatefulWidget {
  const FeedbackHistoryPage({super.key});

  @override
  State<FeedbackHistoryPage> createState() => _FeedbackHistoryPageState();
}

class _FeedbackHistoryPageState extends State<FeedbackHistoryPage> with AutomaticKeepAliveClientMixin<FeedbackHistoryPage> {
  //
  Set<BFeedback>? _feedbackList;
  FeedbackBloc? _feedbackBloc;
  // 下拉刷新
  final RefreshController _refreshController = RefreshController();
  // 滚动监听
  final ScrollController _scrollController = ScrollController();
  //
  @override
  void initState() {
    super.initState();
    //
    _feedbackList = Set.from([]);
    _feedbackBloc = BlocProvider.of<FeedbackBloc>(context);
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return Scaffold(
        appBar: AppBar(
          title: const Text("我的反馈"),
          centerTitle: true,
        ),
        body: BlocConsumer<FeedbackBloc, FeedbackState>(listener: (context, state) {
          //
          debugPrint("FeedbackHistoryPage FeedbackBloc $state");
          if (state is GetMyFeedbackSuccess) {
            //
            debugPrint('feedback load success length: ${state.feedbackList.length}');
            if (state.feedbackList.isEmpty) {
              // Toast.show('没有更多了哦');
            } else {
              // TODO: 加载下来的数据本地缓存
              setState(() {
                _feedbackList!.addAll(state.feedbackList);
              });
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
              _feedbackBloc!.add(const GetMyFeedbackEvent());
            },
            onLoading: () async {
              _feedbackBloc!.add(const GetMyFeedbackEvent());
            },
            child: _feedbackList!.isEmpty
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
                                    title: Text(_feedbackList!
                                        .elementAt(index)
                                        .content!),
                                    subtitle: Text(_feedbackList!
                                        .elementAt(index)
                                        .replyContent ?? '待回复'),
                                    // trailing: const Icon(Icons.keyboard_arrow_right),
                                    onTap: () {
                                      //
                                    },
                                  ),
                                );
                              },
                              childCount: _feedbackList!.length,
                            ),
                          ),
                        ],
                      );
                    },
                  ),
          );
        }));
  }
  
  @override
  bool get wantKeepAlive => true;

}
