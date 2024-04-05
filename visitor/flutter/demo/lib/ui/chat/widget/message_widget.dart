import 'dart:convert';

import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:bytedesk_kefu/model/message.dart';
import 'package:bytedesk_kefu/ui/chat/page/video_play_page.dart';
// import 'package:bytedesk_kefu/ui/widget/bubble.dart';
// import 'package:bytedesk_kefu/ui/widget/bubble_menu.dart';
import 'package:bytedesk_kefu/ui/widget/photo_view_wrapper.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_events.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
// import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
// import 'package:flutter_html/flutter_html.dart';
import '../../widget/pop_up_menu.dart';
import './flutter_html/flutter_html.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';

// 系统消息居中显示
class MessageWidget extends StatelessWidget {
  //
  final Message? message;
  final themeColor = const Color(0xfff5a623);
  final primaryColor = const Color(0xff203152);
  final greyColor = const Color(0xffaeaeae);
  final greyColor2 = const Color(0xffE8E8E8);

  final ValueSetter<String>? customCallback;
  final AnimationController? animationController;
  final BytedeskPopupMenuController popupMenuController =
      BytedeskPopupMenuController();

  final List<ItemModel> menuItems = [
    ItemModel('copy', '复制', Icons.content_copy),
    // ItemModel('转发', Icons.send),
    // ItemModel('收藏', Icons.collections),
    ItemModel('delete', '删除', Icons.delete),
    // ItemModel('多选', Icons.playlist_add_check),
    // ItemModel('引用', Icons.format_quote),
    // ItemModel('提醒', Icons.add_alert),
    // ItemModel('搜一搜', Icons.search),
  ];

  MessageWidget(
      {super.key, this.message, this.customCallback, this.animationController});

  @override
  Widget build(BuildContext context) {
    // 头像组件
    return message!.isSend == 1
        ? _buildSendWidget(context)
        : _buildReceiveWidget(context);
  }

  // 发送消息widget
  Widget _buildSendWidget(BuildContext context) {
    // double tWidth = MediaQuery.of(context).size.width - 160;
    // FIXME: 消息状态，待完善
    String status = '';
    if (message!.status == BytedeskConstants.MESSAGE_STATUS_SENDING) {
      status = '发送中';
    } else if (message!.status == BytedeskConstants.MESSAGE_STATUS_STORED) {
      status = ''; // 发送成功
    } else if (message!.status == BytedeskConstants.MESSAGE_STATUS_RECEIVED) {
      status = '送达';
    } else if (message!.status == BytedeskConstants.MESSAGE_STATUS_READ) {
      status = '已读';
    } else if (message!.status == BytedeskConstants.MESSAGE_STATUS_ERROR) {
      status = '失败';
    }
    return Container(
        margin: const EdgeInsets.only(top: 8.0, left: 8.0),
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: <Widget>[
            // 时间戳
            _buildTimestampWidget(),
            // 消息内容
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Expanded(
                  child: Row(
                    children: <Widget>[
                      Expanded(flex: 1, child: Container()),
                      Text(
                        status,
                        style: const TextStyle(fontSize: 10),
                      ),
                      Container(
                        width: 5,
                      ),
                      Column(
                        // Column被Expanded包裹起来，使其内部文本可自动换行
                        crossAxisAlignment: CrossAxisAlignment.end,
                        children: <Widget>[
                          BytedeskPopupMenu(
                            menuBuilder: _buildPopupMenuWidget(context, message!),
                            // menuBuilder: () => GestureDetector(
                            //   child: _buildSendMenuWidget(context, message!),
                            //   onLongPress: () {
                            //     print("onLongPress");
                            //   },
                            //   onTap: () {
                            //     print("onTap");
                            //   },
                            // ),
                            barrierColor: Colors.transparent,
                            pressType: PressType.longPress,
                            controller: popupMenuController,
                            child: Container(
                              padding: const EdgeInsets.all(10),
                              constraints: const BoxConstraints(maxWidth: 240, minHeight: 40),
                              decoration: BoxDecoration(
                                color: const Color(0xff98e165),
                                borderRadius: BorderRadius.circular(3.0),
                              ),
                              child: _buildSendContent(context, message!),
                            ),
                          )
                          // FIXME: 升级2.12兼容null-safty之后，无法显示长按气泡
                          // FLBubble(
                          //     bubbleFrom: FLBubbleFrom.right,
                          //     backgroundColor:
                          //         const Color.fromRGBO(160, 231, 90, 1),
                          //     child: Container(
                          //       constraints: BoxConstraints(maxWidth: tWidth),
                          //       padding: const EdgeInsets.symmetric(
                          //           horizontal: 2, vertical: 2),
                          //       child: _buildSendMenuWidget(context, message!),
                          //     )),
                        ],
                      ),
                      Container(
                        width: 5,
                      ),
                    ],
                  )
                ),
                // 头像
                _buildAvatarWidget()
              ],
            ),
          ],
        ));
  }

  // 点击消息体菜单
  Widget _buildPopupMenuWidget(BuildContext context, Message message) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(5),
      child: Container(
        width: 220,
        color: const Color(0xFF4C4C4C),
        child: GridView.count(
          padding: const EdgeInsets.symmetric(horizontal: 5, vertical: 10),
          crossAxisCount: 5,
          crossAxisSpacing: 0,
          mainAxisSpacing: 10,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          children: menuItems
              .map((item) => GestureDetector(
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: <Widget>[
                        Icon(
                          item.icon,
                          size: 20,
                          color: Colors.white,
                        ),
                        Container(
                          margin: const EdgeInsets.only(top: 2),
                          child: Text(
                            item.title,
                            style: const TextStyle(
                                color: Colors.white, fontSize: 12),
                          ),
                        ),
                      ],
                    ),
                    // onLongPress: () {
                    //   print("onLongPress ${item.name}, ${message.mid}");
                    //   popupMenuController.hideMenu();
                    //   Fluttertoast.showToast(msg: "onLongPress ${item.name}, ${message.mid}");
                    // },
                    onTap: () {
                      // print("onTap ${item.name}, ${message.mid}");
                      popupMenuController.hideMenu();
                      // Fluttertoast.showToast(
                      //     msg: "onTap ${item.name}, ${message.mid}");
                      String value = item.name;
                      if (value == 'copy') {
                        /// 把文本复制进入粘贴板
                        if (message.type ==
                            BytedeskConstants.MESSAGE_TYPE_TEXT) {
                          Clipboard.setData(
                              ClipboardData(text: message.content!));
                        } else if (message.type ==
                            BytedeskConstants.MESSAGE_TYPE_IMAGE) {
                          Clipboard.setData(
                              ClipboardData(text: message.imageUrl!));
                        } else if (message.type ==
                            BytedeskConstants.MESSAGE_TYPE_VIDEO) {
                          Clipboard.setData(
                              ClipboardData(text: message.videoUrl!));
                        } else if (message.type ==
                            BytedeskConstants.MESSAGE_TYPE_FILE) {
                          Clipboard.setData(
                              ClipboardData(text: message.fileUrl!));
                        } else if (message.type ==
                            BytedeskConstants.MESSAGE_TYPE_VOICE) {
                          Clipboard.setData(
                              ClipboardData(text: message.voiceUrl!));
                        } else {
                          Clipboard.setData(
                              ClipboardData(text: message.content!));
                        }
                        // Toast
                        Fluttertoast.showToast(msg: '复制成功',);
                      } else if (value == 'delete') {
                        bytedeskEventBus
                            .fire(DeleteMessageEventBus(message.mid!));
                      } else if (value == 'recall') {
                        // TODO: 消息撤回, 限制在5分钟之内允许撤回
                      }
                    },
                  ))
              .toList(),
        ),
      ),
    );
  }

  // 发送消息体
  Widget _buildSendContent(BuildContext context, Message message) {
    //
    if (message.type == BytedeskConstants.MESSAGE_TYPE_TEXT) {
      return Text(
        //SelectableText(
        message.content ?? '',
        textAlign: TextAlign.left,
        // softWrap: true,
        style: const TextStyle(color: Colors.black, fontSize: 16.0),
      );
    } else if (message.type == BytedeskConstants.MESSAGE_TYPE_IMAGE) {
      return InkWell(
        onTap: () {
          // 支持将图片保存到相册
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => PhotoViewWrapper(
                imageUrl: message.imageUrl!,
                imageProvider: NetworkImage(
                  message.imageUrl!,
                ),
                loadingBuilder: (context, event) {
                  if (event == null) {
                    return const Center(
                      child: Text("Loading"),
                    );
                  }
                  final value =
                      event.cumulativeBytesLoaded / event.expectedTotalBytes!;
                  final percentage = (100 * value).floor();
                  return Center(
                    child: Text("$percentage%"),
                  );
                },
              ),
            ),
          );
        },
        child: SizedBox(
          width: 100,
          child: CachedNetworkImage(
            imageUrl: message.imageUrl!,
            // placeholder: (context, url) => CircularProgressIndicator(),
            errorWidget: (context, url, error) => const Icon(Icons.error),
          ),
        ),
      );
    } else if (message.type == BytedeskConstants.MESSAGE_TYPE_COMMODITY) {
      // 商品信息, TODO: add send button
      final commodityJson = json.decode(message.content!);
      String title = commodityJson['title'].toString();
      String content = commodityJson['content'].toString();
      String price = commodityJson['price'].toString();
      String imageUrl = commodityJson['imageUrl'].toString();
      return InkWell(
        onTap: () {
          // debugPrint('message!.type ${message!.type}, message!.content ${message!.content}');
          if (customCallback != null) {
            customCallback!(message.content!);
          } else {
            debugPrint('customCallback is null');
          }
        },
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            SizedBox(
              width: 90.0,
              height: 90.0,
              child: CachedNetworkImage(
                imageUrl: imageUrl,
              ),
            ),
            // Gaps.hGap8,
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  // Gaps.vGap10,
                  Container(
                    margin: const EdgeInsets.only(left: 8),
                    child: Text(
                      title,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  Container(
                    margin: const EdgeInsets.only(top: 10, left: 8),
                    child: Text(
                      '¥$price',
                      style: const TextStyle(fontSize: 12, color: Colors.red),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  Container(
                    margin: const EdgeInsets.only(top: 10, left: 8),
                    child: Text(
                      content,
                      style: const TextStyle(fontSize: 12, color: Colors.grey),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  )
                  // Gaps.vGap4,
                ],
              ),
            ),
          ],
        ),
      );
    } else if (message.type == BytedeskConstants.MESSAGE_TYPE_VIDEO) {
      return InkWell(
        onTap: () {
          debugPrint('play video');
          Navigator.of(context).push(MaterialPageRoute(builder: (context) {
            return VideoPlayPage(videoUrl: message.videoUrl);
          }));
        },
        child: SizedBox(
          width: 100,
          height: 100,
          child: CachedNetworkImage(
            imageUrl: BytedeskConstants.VIDEO_PLAY,
            errorWidget: (context, url, error) => const Icon(Icons.error),
          ),
        ),
      );
    } else {
      return Text(
        //SelectableText(
        message.content ?? '',
        textAlign: TextAlign.left,
        // softWrap: true,
        style: const TextStyle(color: Colors.black, fontSize: 16.0),
      );
    }
  }

  // 接收消息widget
  Widget _buildReceiveWidget(BuildContext context) {
    // double tWidth = MediaQuery.of(context).size.width - 160;
    return Container(
        margin: const EdgeInsets.only(top: 8.0, right: 8.0),
        padding: const EdgeInsets.all(8.0),
        child: Column(children: <Widget>[
          // 时间戳
          _buildTimestampWidget(),
          // 消息
          (message!.type!.startsWith(
                      BytedeskConstants.MESSAGE_TYPE_NOTIFICATION) &&
                  message!.type !=
                      BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_THREAD &&
                  message!.type !=
                      BytedeskConstants.MESSAGE_TYPE_NOTIFICATION_PREVIEW)
              ? _buildSystemMessageWidget()
              : Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    // 头像
                    _buildAvatarWidget(),
                    Container(
                      width: 5,
                    ),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // 昵称
                        Container(
                          // color: Colors.grey,
                          margin: const EdgeInsets.only(bottom: 5),
                          child: SelectableText(
                            message!.nickname!,
                            style: const TextStyle(fontSize: 10),
                          ),
                        ),
                        
                        BytedeskPopupMenu(
                          menuBuilder: _buildPopupMenuWidget(context, message!),
                          // menuBuilder:_buildReceiveMenuWidget(context, message!),
                          // menuBuilder: () => GestureDetector(
                          //   child: _buildReceiveMenuWidget(context, message!),
                          //   onLongPress: () {
                          //     print("onLongPress");
                          //   },
                          //   onTap: () {
                          //     print("onTap");
                          //   },
                          // ),
                          barrierColor: Colors.transparent,
                          pressType: PressType.longPress,
                          controller: popupMenuController,
                          child: Container(
                            padding: const EdgeInsets.all(10),
                            constraints: const BoxConstraints(
                                maxWidth: 240, minHeight: 40),
                            decoration: BoxDecoration(
                              color: BytedeskUtils.isDarkMode(context) ? Colors.grey : Colors.white,
                              // color: Colors.grey,
                              borderRadius: BorderRadius.circular(3.0),
                            ),
                            child: _buildReceivedContent(context, message!),
                          ),
                        )
                        // FIXME: 升级2.12兼容null-safty之后，无法显示长按气泡
                        // FLBubble(
                        //     bubbleFrom: FLBubbleFrom.left,
                        //     backgroundColor: Colors.white,
                        //     child: Container(
                        //       constraints: BoxConstraints(maxWidth: tWidth),
                        //       padding: const EdgeInsets.symmetric(
                        //           horizontal: 2, vertical: 2),
                        //       child: _buildReceiveMenuWidget(context, message!),
                        //     ))
                      ],
                    )
                  ],
                ),
        ]));
  }

  // 接收消息体
  Widget _buildReceivedContent(BuildContext context, Message message) {
    //
    if (message.type == BytedeskConstants.MESSAGE_TYPE_TEXT) {
      return Text(
        //SelectableText(
        message.content ?? '',
        textAlign: TextAlign.left,
        // softWrap: true,
        style: const TextStyle(
          color: Colors.black,
          fontSize: 16.0,
        ),
      );
    } else if (message.type == BytedeskConstants.MESSAGE_TYPE_IMAGE) {
      return InkWell(
        onTap: () {
          // 支持将图片保存到相册
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => PhotoViewWrapper(
                imageUrl: message.imageUrl!,
                imageProvider: NetworkImage(
                  message.imageUrl!,
                ),
                loadingBuilder: (context, event) {
                  if (event == null) {
                    return const Center(
                      child: Text("Loading"),
                    );
                  }
                  final value =
                      event.cumulativeBytesLoaded / event.expectedTotalBytes!;
                  final percentage = (100 * value).floor();
                  return Center(
                    child: Text("$percentage%"),
                  );
                },
              ),
            ),
          );
        },
        child: SizedBox(
          width: 100,
          child: CachedNetworkImage(
            imageUrl: message.imageUrl!,
            // placeholder: (context, url) => CircularProgressIndicator(),
            errorWidget: (context, url, error) => const Icon(Icons.error),
          ),
        ),
      );
    } else if (message.type == BytedeskConstants.MESSAGE_TYPE_ROBOT) {
      return Column(
        children: <Widget>[
          // Text(
          //   message.content ?? '',
          //   textAlign: TextAlign.left,
          //   softWrap: true,
          //   style: TextStyle(color: Colors.black, fontSize: 16.0),
          // ),
          Visibility(
            visible: message.content != null && message.content!.isNotEmpty,
            child: Html(
              data: message.content ?? '',
              onLinkTap: (url, _, __, ___) {
                // 打开url
                BytedeskKefu.openWebView(context, url!, '网页');
              },
              onImageTap: (src, _, __, ___) {
                // 查看大图
                // debugPrint("open image $src");
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => PhotoViewWrapper(
                      imageUrl: message.imageUrl!,
                      imageProvider: NetworkImage(
                        src!,
                      ),
                      loadingBuilder: (context, event) {
                        if (event == null) {
                          return const Center(
                            child: Text("Loading"),
                          );
                        }
                        final value = event.cumulativeBytesLoaded /
                            event.expectedTotalBytes!;
                        final percentage = (100 * value).floor();
                        return Center(
                          child: Text("$percentage%"),
                        );
                      },
                    ),
                  ),
                );
              },
              onImageError: (exception, stackTrace) {
                BytedeskUtils.printLog(exception);
              },
            ),
          ),
          Visibility(
              visible: message.answers != null && message.answers!.isNotEmpty,
              // visible: false,
              child: ListView.builder(
                // 如果滚动视图在滚动方向无界约束，那么shrinkWrap必须为true
                shrinkWrap: true,
                // 禁用ListView滑动，使用外层的ScrollView滑动
                physics: const NeverScrollableScrollPhysics(),
                padding: const EdgeInsets.all(0),
                itemCount:
                    message.answers == null ? 0 : message.answers!.length,
                itemBuilder: (_, index) {
                  //
                  var answer = message.answers![index];
                  // return Text(answer.question!);
                  return DecoratedBox(
                      decoration: BoxDecoration(
                          border: Border(
                        bottom: Divider.createBorderSide(context, width: 0.8),
                      )),
                      child: Container(
                        margin:
                            const EdgeInsets.only(top: 6, left: 8, bottom: 8),
                        // color: Colors.pink,
                        child: InkWell(
                            child: Text(
                              answer.question!,
                              style: const TextStyle(color: Colors.blue),
                            ),
                            onTap: () => {
                                  // debugPrint('object:' + answer.question),
                                  bytedeskEventBus.fire(QueryAnswerEventBus(
                                      answer.aid!,
                                      answer.question!,
                                      answer.answer!))
                                }),
                      ));
                },
              )),
          Container(
            margin: const EdgeInsets.only(left: 10, top: 10),
            child: Row(
              children: [
                const Text('没有找到答案？'),
                GestureDetector(
                  child: Text(
                    '人工客服',
                    style: TextStyle(color: Theme.of(context).primaryColor),
                  ),
                  onTap: () {
                    debugPrint('请求人工客服');
                    bytedeskEventBus.fire(RequestAgentThreadEventBus());
                  },
                )
              ],
            ),
          )
        ],
      );
    } else if (message.type == BytedeskConstants.MESSAGE_TYPE_ROBOT_V2) {
      return Column(
        children: <Widget>[
          SelectableText(
            message.content ?? '',
            textAlign: TextAlign.left,
            // softWrap: true,
            style: const TextStyle(
              color: Colors.black,
              fontSize: 16.0,
            ),
          ),
          Visibility(
              visible:
                  message.categories != null && message.categories!.isNotEmpty,
              // visible: false,
              child: ListView.builder(
                // 如果滚动视图在滚动方向无界约束，那么shrinkWrap必须为true
                shrinkWrap: true,
                // 禁用ListView滑动，使用外层的ScrollView滑动
                physics: const NeverScrollableScrollPhysics(),
                padding: const EdgeInsets.all(0),
                itemCount:
                    message.categories == null ? 0 : message.categories!.length,
                itemBuilder: (_, index) {
                  //
                  var category = message.categories![index];
                  // return Text(answer.question!);
                  return DecoratedBox(
                      decoration: BoxDecoration(
                          border: Border(
                        bottom: Divider.createBorderSide(context, width: 0.8),
                      )),
                      child: Container(
                        margin:
                            const EdgeInsets.only(top: 6, left: 8, bottom: 8),
                        // color: Colors.pink,
                        child: InkWell(
                            child: Text(
                              category.name!,
                              style: const TextStyle(color: Colors.blue),
                            ),
                            onTap: () => {
                                  // BytedeskUtils.printLog(category.name),
                                  bytedeskEventBus.fire(QueryCategoryEventBus(
                                      category.cid!, category.name!))
                                }),
                      ));
                },
              )),
          Container(
            margin: const EdgeInsets.only(left: 10, top: 10),
            child: Row(
              children: [
                const Text('没有找到答案？'),
                GestureDetector(
                  child: Text(
                    '人工客服',
                    style: TextStyle(color: Theme.of(context).primaryColor),
                  ),
                  onTap: () {
                    debugPrint('请求人工客服');
                    bytedeskEventBus.fire(RequestAgentThreadEventBus());
                  },
                )
              ],
            ),
          )
        ],
      );
    } else if (message.type == BytedeskConstants.MESSAGE_TYPE_ROBOT_RESULT) {
      return SelectableText(
        message.content ?? '',
        textAlign: TextAlign.left,
        // softWrap: true,
        style: const TextStyle(
          color: Colors.black,
          fontSize: 16.0,
        ),
      );
    } else if (message.type == BytedeskConstants.MESSAGE_TYPE_COMMODITY) {
      // 商品信息, TODO: add send button
      final commodityJson = json.decode(message.content!);
      String title = commodityJson['title'].toString();
      String content = commodityJson['content'].toString();
      String price = commodityJson['price'].toString();
      String imageUrl = commodityJson['imageUrl'].toString();
      return InkWell(
        onTap: () {
          // debugPrint('message!.type ${message!.type}, message!.content ${message!.content}');
          if (customCallback != null) {
            customCallback!(message.content!);
          } else {
            debugPrint('customCallback is null');
          }
        },
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            SizedBox(
              width: 90.0,
              height: 90.0,
              child: CachedNetworkImage(
                imageUrl: imageUrl,
              ),
            ),
            // Gaps.hGap8,
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  // Gaps.vGap10,
                  Container(
                    margin: const EdgeInsets.only(left: 8),
                    child: Text(
                      title,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  Container(
                    margin: const EdgeInsets.only(top: 10, left: 8),
                    child: Text(
                      '¥$price',
                      style: const TextStyle(fontSize: 12, color: Colors.red),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  Container(
                    margin: const EdgeInsets.only(top: 10, left: 8),
                    child: Text(
                      content,
                      style: const TextStyle(fontSize: 12, color: Colors.grey),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  )
                  // Gaps.vGap4,
                ],
              ),
            ),
          ],
        ),
      );
    } else if (message.type == BytedeskConstants.MESSAGE_TYPE_VIDEO) {
      return InkWell(
        onTap: () {
          debugPrint('play video');
          Navigator.of(context).push(MaterialPageRoute(builder: (context) {
            return VideoPlayPage(videoUrl: message.videoUrl);
          }));
        },
        child: SizedBox(
          width: 100,
          height: 100,
          child: CachedNetworkImage(
            imageUrl: BytedeskConstants.VIDEO_PLAY,
            errorWidget: (context, url, error) => const Icon(Icons.error),
          ),
        ),
      );
    } else {
      return Text(
        //SelectableText(
        message.content ?? '',
        textAlign: TextAlign.left,
        // softWrap: true,
        style: const TextStyle(color: Colors.black, fontSize: 16.0),
      );
    }
  }

  // 时间戳
  Widget _buildTimestampWidget() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: <Widget>[
        Container(
          padding: const EdgeInsets.only(bottom: 5, top: 5),
          child: Text(
            message!.timestamp ?? '',
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 10.0),
          ),
        )
      ],
    );
  }

  // 系统消息居中显示
  Widget _buildSystemMessageWidget() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: <Widget>[
        Container(
          padding: const EdgeInsets.only(bottom: 5, top: 5),
          child: Text(
            message!.content ?? '',
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 10.0),
          ),
        )
      ],
    );
  }

  // 头像
  Widget _buildAvatarWidget() {
    return SizedBox(
      width: 35,
      height: 35,
      child: CachedNetworkImage(
        imageUrl: message!.avatar ?? BytedeskConstants.DEFAULT_AVATA,
        errorWidget: (context, url, error) => const Icon(Icons.error),
      ),
    );
  }
}
