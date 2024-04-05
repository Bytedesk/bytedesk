// // ignore_for_file: must_be_immutable

// import 'package:flutter/material.dart';

// class VoiceAnimation extends StatefulWidget {
//   final double width;
//   final double height;
//   int interval;
//   bool isStop = false;
//   bool userIsAuthor;
//   var callStart;
//   late VoiceAnimationState voiceAnimationImageState;

//   VoiceAnimation({super.key, 
//     required this.width,
//     required this.height,
//     required this.isStop,
//     required this.userIsAuthor,
//     this.interval = 200,
//   });

//   @override
//   State<StatefulWidget> createState() {
//     voiceAnimationImageState = VoiceAnimationState();
//     return voiceAnimationImageState;
//   }

//   start() {
//     voiceAnimationImageState.start();
//   }

//   stop() {
//     voiceAnimationImageState.stop();
//   }
// }

// class VoiceAnimationState extends State<VoiceAnimation>
//     with SingleTickerProviderStateMixin {
//   // 动画控制
//   late Animation<double> _animation;
//   late AnimationController _controller;
//   int interval = 200;

//   List<String> voicePlayingAsset = [];

//   @override
//   void initState() {
//     super.initState();

//     voicePlayingAsset.add("voice_playing_1.png");
//     voicePlayingAsset.add("voice_playing_2.png");
//     voicePlayingAsset.add("voice_playing_3.png");

//     interval = widget.interval;
//     const int imageCount = 3;
//     final int maxTime = interval * imageCount;

//     // 启动动画controller
//     _controller = AnimationController(
//       duration: Duration(milliseconds: maxTime),
//       vsync: this,
//     );
//     _controller.addStatusListener((AnimationStatus status) {
//       if (status == AnimationStatus.completed) {
//         _controller.forward(from: 2.0); // 完成后重新开始
//       }
//     });

//     _animation = Tween<double>(begin: -(imageCount - 2).toDouble(), end: 2)
//         .animate(_controller)
//       ..addListener(() {
//         setState(() {});
//       });
//   }

//   @override
//   void dispose() {
//     _controller.dispose();
//     super.dispose();
//   }

//   stop() {
//     _controller.stop();
//   }

//   start() {
//     _controller.forward();
//   }

//   @override
//   Widget build(BuildContext context) {
//     if (widget.isStop) {
//       start();
//     } else {
//       stop();
//     }
//     int ix = _animation.value.floor() % voicePlayingAsset.length;

//     List<Widget> images = [];
//     String prefix = "assets/images/chat/";
//     // 把所有图片都加载进内容，否则每一帧加载时会卡顿
//     // receiverImages
//     for (int i = 0; i < voicePlayingAsset.length; ++i) {
//       if (i != ix) {
//         images.add(Image.asset(
//           prefix + voicePlayingAsset[i],
//           width: 0,
//           height: 0,
//         ));
//       }
//     }
//     images.add(Image.asset(
//       prefix + voicePlayingAsset[ix],
//       width: widget.width,
//       height: widget.height,
//       color: Colors.black,
//     ));
//     return Stack(
//       alignment: AlignmentDirectional.centerEnd,
//       children: images,
//     );
//   }
// }
