// import 'dart:async';
// import 'dart:io';

// import 'package:audio_session/audio_session.dart';
// import 'package:flutter/foundation.dart' show kIsWeb;
// import 'package:flutter/material.dart';
// import 'package:flutter_sound/flutter_sound.dart';
// // import 'package:get/get.dart';
// // import 'package:imboy/component/helper/func.dart';
// import 'package:intl/intl.dart' show DateFormat;
// import 'package:path/path.dart';
// import 'package:path_provider/path_provider.dart';
// import 'package:permission_handler/permission_handler.dart';

// import 'custom_overlay.dart';

// bool strEmpty(String? val) {
//   return !strNoEmpty(val);
// }

// /// 字符串不为空
// bool strNoEmpty(String? value) {
//   if (value == null) return false;

//   return value.trim().isNotEmpty;
// }

// class AudioFile {
//   const AudioFile({
//     required this.file,
//     required this.duration,
//     required this.mimeType,
//   });

//   final File file;
//   final Duration duration;
//   final String mimeType;
// }

// class VoiceWidget extends StatefulWidget {
//   final Function()? startRecord;
//   final Function(AudioFile? obj)? stopRecord;

//   final double? height;
//   final EdgeInsets? margin;
//   final Decoration? decoration;

//   /// startRecord 开始录制回调  stopRecord回调
//   VoiceWidget({
//     Key? key,
//     this.startRecord,
//     this.stopRecord,
//     this.height,
//     this.decoration,
//     this.margin,
//   }) : super(key: key);

//   @override
//   _VoiceWidgetState createState() => _VoiceWidgetState();
// }

// class _VoiceWidgetState extends State<VoiceWidget> {
//   // 倒计时总时长
//   int _countTotal = 300;
//   double starty = 0.0;
//   double offset = 0.0;
//   bool isUp = false;
//   String textShow = "按住说话";
//   String toastShow = "手指上滑,取消发送";
//   String voiceIco = "assets/images/chat/voice_volume_1.png";

//   Duration recordingDuration = const Duration();
//   // final List<double> _levels = [];
//   late String recordingMimeType;
//   late Codec recordCodec;

//   Timer? _timer;
//   int _count = 0;
//   OverlayEntry? overlayEntry;

//   /////
//   final FlutterSoundRecorder recorderModule = FlutterSoundRecorder();
//   String recorderTxt = '00:00.000';

//   String filePath = '';
//   StreamSubscription? recorderSubscription;
//   int pos = 0;
//   double dbLevel = 0;

//   late AudioSession session;

//   @override
//   void initState() {
//     super.initState();
//     debugPrint(">>> on chat _VoiceWidgetState initState");
//   }

//   ///显示录音悬浮布局
//   buildOverLayView(BuildContext context) {
//     if (overlayEntry == null) {
//       overlayEntry = OverlayEntry(builder: (content) {
//         return CustomOverlay(
//           icon: Column(
//             children: <Widget>[
//               Container(
//                 margin: const EdgeInsets.only(top: 10),
//                 child: _countTotal - _count < 11
//                     ? Center(
//                         child: Padding(
//                           padding: const EdgeInsets.only(bottom: 15.0),
//                           child: Text(
//                             (_countTotal - _count).toString(),
//                             style: TextStyle(
//                               fontSize: 70.0,
//                               color: Colors.white,
//                             ),
//                           ),
//                         ),
//                       )
//                     : new Image.asset(
//                         voiceIco,
//                         width: 100,
//                         height: 100,
//                         // package: 'flutter_plugin_record',
//                       ),
//               ),
//               Container(
//                 child: Text(
//                   toastShow + "\n" + recorderTxt,
//                   textAlign: TextAlign.center,
//                   style: TextStyle(
//                     fontStyle: FontStyle.normal,
//                     color: Colors.white,
//                     fontSize: 14,
//                   ),
//                 ),
//               )
//             ],
//           ),
//         );
//       });
//       Overlay.of(context)!.insert(overlayEntry!);
//     }
//   }

//   showVoiceView(BuildContext ctx) {
//     setState(() {
//       textShow = "松开结束";
//     });

//     ///显示录音悬浮布局
//     buildOverLayView(ctx);

//     debugPrint(">>> on record showVoiceView");
//     recorderStart(ctx);
//   }

//   hideVoiceView(BuildContext ctx) async {
//     if (_timer!.isActive) {
//       if (_count < 1) {
//         Toast.showView(
//             context: ctx,
//             msg: '说话时间太短',
//             icon: Text(
//               '!',
//               style: TextStyle(
//                 fontSize: 60,
//                 color: Colors.white,
//               ),
//             ));
//         isUp = true;
//       }
//       _timer?.cancel();
//       _count = 0;
//     }

//     setState(() {
//       textShow = "按住说话";
//     });

//     recorderStop(recorderModule);
//     if (overlayEntry != null) {
//       overlayEntry?.remove();
//       overlayEntry = null;
//     }
//     debugPrint(
//         ">>> on record hideVoiceView isUp ${isUp}, filepath: ${filePath}");
//     if (isUp) {
//       // debugPrint("取消发送");
//     } else {
//       debugPrint("进行发送");

//       widget.stopRecord!.call(
//         strEmpty(filePath)
//             ? null
//             : AudioFile(
//                 file: File(filePath),
//                 duration: this.recordingDuration,
//                 // waveForm: _levels,
//                 mimeType: recordingMimeType,
//               ),
//       );
//     }
//   }

//   moveVoiceView() {
//     // debugPrint(offset - start);
//     setState(() {
//       isUp = starty - offset > 100 ? true : false;
//       if (isUp) {
//         textShow = "松开手指,取消发送";
//         toastShow = textShow;
//       } else {
//         textShow = "松开结束";
//         toastShow = "手指上滑,取消发送";
//       }
//     });
//   }

//   void cancelRecorderSubscriptions() {
//     if (recorderSubscription != null) {
//       recorderSubscription!.cancel();
//       recorderSubscription = null;
//     }
//   }

//   /// Creates an path to a temporary file.
//   Future<String> _createTempAacFilePath(String name) async {
//     if (kIsWeb) {
//       throw Exception(
//         'This method only works for mobile as it creates a temporary AAC file',
//       );
//     }
//     String path;
//     final tmpDir = await getTemporaryDirectory();
//     path = '${join(tmpDir.path, name)}.aac';
//     final parent = dirname(path);
//     await Directory(parent).create(recursive: true);

//     return path;
//   }

//   Future<void> openTheRecorder() async {
//     if (!kIsWeb) {
//       var status = await Permission.microphone.request();
//       if (status != PermissionStatus.granted) {
//         throw RecordingPermissionException('Microphone permission not granted');
//       }
//     }
//     if (session == null) {
//       session = await AudioSession.instance;
//       await session.configure(AudioSessionConfiguration(
//         avAudioSessionCategory: AVAudioSessionCategory.playAndRecord,
//         avAudioSessionCategoryOptions:
//             AVAudioSessionCategoryOptions.allowBluetooth |
//                 AVAudioSessionCategoryOptions.defaultToSpeaker,
//         avAudioSessionMode: AVAudioSessionMode.spokenAudio,
//         avAudioSessionRouteSharingPolicy:
//             AVAudioSessionRouteSharingPolicy.defaultPolicy,
//         avAudioSessionSetActiveOptions: AVAudioSessionSetActiveOptions.none,
//         androidAudioAttributes: const AndroidAudioAttributes(
//           contentType: AndroidAudioContentType.speech,
//           flags: AndroidAudioFlags.none,
//           usage: AndroidAudioUsage.voiceCommunication,
//         ),
//         androidAudioFocusGainType: AndroidAudioFocusGainType.gain,
//         androidWillPauseWhenDucked: true,
//       ));
//     }
//   }

//   // -------  Here is the code to playback  -----------------------
//   /// 开始录音
//   void recorderStart(BuildContext ctx) async {
//     debugPrint(">>> on record start");
//     try {
//       var status = await Permission.microphone.request();
//       if (status != PermissionStatus.granted) {
//         // Get.snackbar("", "未获取到麦克风权限");
//         throw RecordingPermissionException("未获取到麦克风权限");
//       }

//       await recorderModule.openRecorder();

//       // String name = "${Xid().toString()}";
//       String name = "recardtmp";
//       if (kIsWeb) {
//         if (await recorderModule.isEncoderSupported(Codec.opusWebM)) {
//           filePath = '$name.webm';
//           recordCodec = Codec.opusWebM;
//           recordingMimeType = 'audio/webm;codecs="opus"';
//         } else {
//           filePath = '$name.mp4';
//           recordCodec = Codec.aacMP4;
//           recordingMimeType = 'audio/aac';
//         }
//       } else {
//         filePath = await _createTempAacFilePath(name);
//         recordCodec = Codec.aacADTS;
//         recordingMimeType = 'audio/aac';
//       }
//       // 必须要设置，才能够监听 振幅大小
//       setSubscriptionDuration(40);

//       await recorderModule.startRecorder(
//         toFile: filePath,
//         codec: recordCodec,
//         bitRate: 8000,
//         sampleRate: 8000,
//       );

//       /// 监听录音
//       recorderSubscription = recorderModule.onProgress!.listen((e) {
//         // debugPrint(">>> on record listen e ${e.toString()}");
//         setState(() {
//           pos = e.duration.inMilliseconds;
//         });
//         // debugPrint(">>> on record listen pos: ${pos}, dbLevel: ${e.decibels};");
//         if (e != null && e.duration != null) {
//           recordingDuration = e.duration;
//           // _levels.add(e.decibels ?? 0.0);

//           DateTime date = new DateTime.fromMillisecondsSinceEpoch(
//             e.duration.inMilliseconds,
//             isUtc: true,
//           );
//           String txt = DateFormat('mm:ss.SSS').format(date);
//           if (date.second >= _countTotal) {
//             // recorderStop(recorderModule);
//             hideVoiceView(ctx);
//           }
//           if (e.decibels != null) {
//             setState(() {
//               recorderTxt = txt.substring(0, 9);
//               dbLevel = e.decibels!.toDouble();
//               // debugPrint(">>> on record 当前振幅：$dbLevel");
//             });
//           }
//         }

//         if (e.decibels != null) {
//           dbLevel = e.decibels as double;
//           double voiceData = ((dbLevel * 100.0).floor()) / 10000;
//           if (voiceData > 0 && voiceData < 0.1) {
//             voiceIco = "assets/images/chat/voice_volume_2.png";
//           } else if (voiceData > 0.2 && voiceData < 0.3) {
//             voiceIco = "assets/images/chat/voice_volume_3.png";
//           } else if (voiceData > 0.3 && voiceData < 0.4) {
//             voiceIco = "assets/images/chat/voice_volume_4.png";
//           } else if (voiceData > 0.4 && voiceData < 0.5) {
//             voiceIco = "assets/images/chat/voice_volume_5.png";
//           } else if (voiceData > 0.5 && voiceData < 0.6) {
//             voiceIco = "assets/images/chat/voice_volume_6.png";
//           } else if (voiceData > 0.6 && voiceData < 0.7) {
//             voiceIco = "assets/images/chat/voice_volume_7.png";
//           } else if (voiceData > 0.7 && voiceData < 1) {
//             voiceIco = "assets/images/chat/voice_volume_7.png";
//           } else {
//             voiceIco = "assets/images/chat/voice_volume_1.png";
//           }
//           if (overlayEntry != null) {
//             overlayEntry!.markNeedsBuild();
//           }
//           // debugPrint(
//           //     ">>> on record 振幅大小   " + voiceData.toString() + "  " + voiceIco);
//           setState(() {
//             dbLevel = dbLevel;
//             voiceIco = voiceIco;
//           });
//         }
//       });
//       setState(() {
//         filePath = filePath;
//       });
//     } catch (err) {
//       setState(() {
//         recorderStop(recorderModule);
//         cancelRecorderSubscriptions();
//       });
//     }
//   }

//   /// 结束录音
//   Future<String?> recorderStop(FlutterSoundRecorder recorder) async {
//     try {
//       String? filepath = await recorder.stopRecorder();
//       cancelRecorderSubscriptions();
//       setState(() {
//         dbLevel = 0.0;
//         pos = 0;
//         recorderTxt = '00:00.000';
//       });
//       // _getDuration();
//       return filepath;
//     } catch (err) {
//       debugPrint('stopRecorder error: $err');
//     }
//   }

//   /**
//    * 设置订阅周期
//    */
//   Future<void> setSubscriptionDuration(
//       double d) async // d is between 0.0 and 2000 (milliseconds)
//   {
//     setState(() {});
//     await recorderModule.setSubscriptionDuration(
//       Duration(milliseconds: d.floor()),
//     );
//   }
//   // --------------------- UI -------------------

//   @override
//   Widget build(BuildContext context) {
//     return Container(
//       child: GestureDetector(
//         onLongPressStart: (details) {
//           starty = details.globalPosition.dy;
//           _timer = Timer.periodic(Duration(milliseconds: 1000), (t) {
//             _count++;
//             if (_count == _countTotal) {
//               hideVoiceView(context);
//             }
//           });
//           showVoiceView(context);
//         },
//         onLongPressEnd: (details) {
//           hideVoiceView(context);
//         },
//         onLongPressMoveUpdate: (details) {
//           offset = details.globalPosition.dy;
//           moveVoiceView();
//         },
//         child: Container(
//           height: widget.height ?? 60,
//           decoration: widget.decoration ??
//               BoxDecoration(
//                 borderRadius: BorderRadius.circular(6.0),
//                 border: Border.all(
//                   width: 1.0,
//                   color: Colors.white70,
//                 ),
//                 color: Colors.white70,
//               ),
//           margin: widget.margin ?? EdgeInsets.fromLTRB(50, 0, 50, 20),
//           child: Center(
//             child: Text(
//               textShow,
//               // '${textShow}(pos: ${pos})',
//             ),
//           ),
//         ),
//       ),
//     );
//   }

//   @override
//   void dispose() {
//     recorderStop(recorderModule);
//     cancelRecorderSubscriptions();

//     // Be careful : you must `close` the audio session when you have finished with it.
//     recorderModule.closeRecorder();

//     // recordPlugin?.dispose();
//     _timer?.cancel();
//     super.dispose();
//   }
// }

// class Toast {
//   static showView({
//     BuildContext? context,
//     String? msg,
//     TextStyle? style,
//     Widget? icon,
//     Duration duration = const Duration(seconds: 1),
//     int count = 3,
//     Function? onTap,
//   }) {
//     OverlayEntry? overlayEntry;
//     int _count = 0;

//     void removeOverlay() {
//       overlayEntry?.remove();
//       overlayEntry = null;
//     }

//     if (overlayEntry == null) {
//       overlayEntry = OverlayEntry(builder: (content) {
//         return Container(
//           child: GestureDetector(
//             onTap: () {
//               if (onTap != null) {
//                 removeOverlay();
//                 onTap();
//               }
//             },
//             child: CustomOverlay(
//               icon: Column(
//                 children: [
//                   Padding(
//                     child: icon,
//                     padding: const EdgeInsets.only(
//                       bottom: 10.0,
//                     ),
//                   ),
//                   Container(
//                     child: Text(
//                       msg ?? '',
//                       style: style ??
//                           TextStyle(
//                             fontStyle: FontStyle.normal,
//                             color: Colors.white,
//                             fontSize: 16,
//                           ),
//                     ),
//                   )
//                 ],
//               ),
//             ),
//           ),
//         );
//       });
//       Overlay.of(context!)!.insert(overlayEntry!);
//       if (onTap != null) return;
//       Timer.periodic(duration, (timer) {
//         _count++;
//         if (_count == count) {
//           _count = 0;
//           timer.cancel();
//           removeOverlay();
//         }
//       });
//     }
//   }
// }
