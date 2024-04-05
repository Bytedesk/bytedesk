// import 'package:carousel_slider/carousel_slider.dart';
// ignore_for_file: constant_identifier_names, library_private_types_in_public_api

import 'package:flutter/material.dart';
// import 'package:flutter/widgets.dart';
// import 'package:get/get.dart';
// import 'package:get/get_utils/src/extensions/internacionalization.dart';
// import 'package:imboy/config/const.dart';

class ExtraItem extends StatelessWidget {
  const ExtraItem({
    Key? key,
    required this.onPressed,
    required this.image,
    this.width,
    this.height,
    required this.title,
  }) : super(key: key);

  final ImageProvider image;
  final void Function()? onPressed;
  final double? width;
  final double? height;
  final String title;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onPressed ?? () => {debugPrint('功能暂未实现')},
      child: Padding(
        padding: const EdgeInsets.only(left: 15, top: 13, right: 15, bottom: 0),
        child: Column(
          children: [
            SizedBox(
              width: width ?? 56,
              height: height ?? 56,
              // margin: EdgeInsets.symmetric(horizontal: 10),
              child: Material(
                color: AppColors.ChatInputBackgroundColor,
                // INK可以实现装饰容器
                child: Ink(
                  // 用ink圆角矩形
                  decoration: BoxDecoration(
                    // 背景
                    color: AppColors.ChatInputBackgroundColor,
                    // 设置四周圆角 角度
                    borderRadius: const BorderRadius.all(Radius.circular(16.0)),
                    // 设置四周边框
                    border: Border.all(
                      width: 1,
                      color: AppColors.ChatInputBackgroundColor,
                    ),
                  ),
                  child: Image(
                    image: image,
                  ),
                ),
              ),
            ),
            Text(title),
          ],
        ),
      ),
    );
  }
}

class ExtraItems extends StatefulWidget {
  const ExtraItems({
    Key? key,
    this.handleImageSelection,
    this.handleFileSelection,
    this.handlePickerSelection,
    this.handleUploadVideo,
    this.handleCaptureVideo,
  }) : super(key: key);

  final void Function()? handleImageSelection;
  final void Function()? handleFileSelection;
  final void Function()? handlePickerSelection;
  final void Function()? handleUploadVideo;
  final void Function()? handleCaptureVideo;

  @override
  _ExtraItemsState createState() => _ExtraItemsState();
}

class _ExtraItemsState extends State<ExtraItems> {
  // int _current = 0;
  // CarouselController _controller = CarouselController();

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: Column(
        children: <Widget>[
          Row(
            children: [
              ExtraItem(
                title: "照片",
                image: const AssetImage('assets/images/chat/extra_photo.webp'),
                onPressed: widget.handleImageSelection,
              ),
              ExtraItem(
                title: "拍摄",
                image: const AssetImage('assets/images/chat/extra_camera.webp'),
                onPressed: widget.handlePickerSelection,
              ),
              ExtraItem(
                title: "上传视频",
                image: const AssetImage('assets/images/chat/extra_media.webp'),
                onPressed: widget.handleUploadVideo,
              ),
              ExtraItem(
                title: "录制视频",
                image:
                    const AssetImage('assets/images/chat/extra_videocall.webp'),
                onPressed: widget.handleCaptureVideo,
              ),
              // ExtraItem(
              //   title: "位置",
              //   image: AssetImage('assets/images/chat/extra_localtion.webp'),
              //   onPressed: null,
              // ),
            ],
          ),
        ],
      ),
    );
  }

  // @override
  // Widget build(BuildContext context) {
  //   var items = [
  //     Column(
  //       children: <Widget>[
  //         Row(
  //           children: [
  //             ExtraItem(
  //               title: "照片",
  //               image: AssetImage('assets/images/chat/extra_photo.webp'),
  //               onPressed: widget.handleImageSelection,
  //             ),
  //             ExtraItem(
  //               title: "拍摄",
  //               image: AssetImage('assets/images/chat/extra_camera.webp'),
  //               onPressed: widget.handlePickerSelection,
  //             ),
  //             ExtraItem(
  //               title: "上传视频",
  //               image: AssetImage('assets/images/chat/extra_media.webp'),
  //               onPressed: widget.handleUploadVideo,
  //             ),
  //             ExtraItem(
  //               title: "录制视频",
  //               image: AssetImage('assets/images/chat/extra_videocall.webp'),
  //               onPressed: widget.handleCaptureVideo,
  //             ),
  //             // ExtraItem(
  //             //   title: "位置",
  //             //   image: AssetImage('assets/images/chat/extra_localtion.webp'),
  //             //   onPressed: null,
  //             // ),
  //           ],
  //         ),
  //       ],
  //     ),
  //   ];
  //   return Column(
  //     children: <Widget>[
  //       Expanded(
  //         child: CarouselSlider(
  //           options: CarouselOptions(
  //             height: 50, // Get.height,
  //             viewportFraction: 1.0,
  //             aspectRatio: 2.0,
  //             scrollDirection: Axis.horizontal,
  //             disableCenter: true,
  //             initialPage: 1,
  //             enableInfiniteScroll: false,
  //             onPageChanged: (index, reason) {
  //               setState(() {
  //                 _current = index;
  //               });
  //             },
  //           ),
  //           items: items.map((tab) {
  //             return Padding(
  //               padding: EdgeInsets.only(left: 8),
  //               child: tab,
  //             );
  //           }).toList(),
  //         ),
  //       ),
  //       Row(
  //         mainAxisAlignment: MainAxisAlignment.center,
  //         children: items.asMap().entries.map((entry) {
  //           return GestureDetector(
  //             onTap: () => _controller.animateToPage(entry.key),
  //             child: Container(
  //               width: 10.0,
  //               height: 10.0,
  //               margin: EdgeInsets.symmetric(
  //                 vertical: 8.0,
  //                 horizontal: 6.0,
  //               ),
  //               decoration: BoxDecoration(
  //                 shape: BoxShape.circle,
  //                 color: (Theme.of(context).brightness == Brightness.dark
  //                         ? Colors.white
  //                         : Colors.black)
  //                     .withOpacity(_current == entry.key ? 0.7 : 0.2),
  //               ),
  //             ),
  //           );
  //         }).toList(),
  //       ),
  //     ],
  //   );
  // }
}

class AppColors {
  static const AppBarColor = Color.fromRGBO(237, 237, 237, 1);

  static const BgColor = Color.fromRGBO(255, 255, 255, 1);

  static const LineColor = Colors.grey;

  static const TipColor = Color.fromRGBO(89, 96, 115, 1.0);

  static const MainTextColor = Color.fromRGBO(115, 115, 115, 1.0);

  static const LabelTextColor = Color.fromRGBO(144, 144, 144, 1.0);

  static const ItemBgColor = Color.fromRGBO(75, 75, 75, 1.0);

  static const ItemOnColor = Color.fromRGBO(68, 68, 68, 1.0);

  static const ButtonTextColor = Color.fromRGBO(112, 113, 135, 1.0);

  static const TitleColor = 0xff181818;
  static const ButtonArrowColor = 0xffadadad;

  ///

  /// 主背景 白色
  static const Color primaryBackground = Color.fromARGB(255, 255, 255, 255);

  /// 主文本 灰色
  static const Color primaryText = Color.fromARGB(255, 45, 45, 47);

  /// 主控件-背景 绿色
  static const Color primaryElement = Color.fromARGB(255, 109, 192, 102);

  /// 主控件-文本 白色
  static const Color primaryElementText = Color.fromARGB(255, 255, 255, 255);

  // *****************************************

  /// 第二种控件-背景色 淡灰色
  static const Color secondaryElement = Color.fromARGB(255, 246, 246, 246);

  /// 第二种控件-文本 浅绿色
  static const Color secondaryElementText = Color.fromRGBO(169, 234, 122, 1.0);

  // *****************************************

  /// 第三种控件-背景色 石墨色
  static const Color thirdElement = Color.fromARGB(255, 45, 45, 47);

  /// 第三种控件-文本 浅灰色2
  static const Color thirdElementText = Color.fromARGB(255, 141, 141, 142);

  // *****************************************

  /// tabBar 默认颜色 灰色
  static const Color tabBarElement = Color.fromARGB(255, 208, 208, 208);

  /// tabCellSeparator 单元格底部分隔条 颜色
  static const Color tabCellSeparator = Color.fromARGB(255, 230, 230, 231);

  // for chat
  static const ChatBg = Color.fromRGBO(243, 243, 243, 1.0);
  static const ChatSendMessgeBgColor = Color.fromRGBO(169, 234, 122, 1.0);
  static const ChatSentMessageBodyTextColor = Color.fromRGBO(19, 29, 13, 1.0);

  static const ChatReceivedMessageBodyTextColor =
      Color.fromRGBO(25, 25, 25, 1.0);
  static const ChatReceivedMessageBodyBgColor =
      Color.fromRGBO(255, 255, 255, 1.0);
  static const ChatInputBackgroundColor = Color.fromRGBO(240, 240, 240, 1.0);
  static const ChatInputFillGgColor = Color.fromRGBO(251, 251, 251, 1.0);
  // end for chat
}
