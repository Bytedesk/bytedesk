// ignore_for_file: avoid_print, prefer_interpolation_to_compose_strings

import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:bytedesk_kefu_example/example.dart';
import 'package:get/get.dart';
// import 'package:overlay_support/overlay_support.dart';
import 'package:flutter/material.dart';
// import 'package:vibration/vibration.dart';
// import 'package:audioplayers/audioplayers.dart';

void main() {
  // runApp(const OverlaySupport(child: MyApp()));
  //
  runApp(const MyApp());

  // 参考文档：https://github.com/Bytedesk/bytedesk-flutter
  // 管理后台：https://www.bytedesk.com/admin
  // appkey和subDomain请替换为真实值
  // 获取appkey，登录后台->渠道管理->Flutter->添加应用->获取appkey
  String appKey = '81f427ea-4467-4c7c-b0cd-5c0e4b51456f';
  // 获取subDomain，也即企业号：登录后台->客服管理->客服账号->企业号
  String subDomain = "vip";
  // 第一步：初始化
  BytedeskKefu.init(appKey, subDomain);
  // 注：如果需要多平台统一用户（用于同步聊天记录等），可使用下列接口，其中：username只能包含数字或字母，不能含有汉字和特殊字符等，nickname可以使用汉字
  // 注：如需切换用户，请首先执行BytedeskKefu.logout()
  // BytedeskKefu.initWithUsernameAndNicknameAndAvatar('myflutterusername', '我是美女', 'https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png', _appKey, _subDomain);
  // BytedeskKefu.initWithUsername('myflutterusername', _appKey, _subDomain); // 其中：username为自定义用户名，可与开发者所在用户系统对接
  // 如果还需要自定义昵称/头像，可以使用 initWithUsernameAndNickname或initWithUsernameAndNicknameAndAvatar，
  // 具体参数可以参考 bytedesk_kefu/bytedesk_kefu.dart 文件
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
  static const _mobileWidthThreshold = 500;
  static const _mobileWidth = 420.0;
  static const _mobileHeight = 900.0;
  final _appKey = GlobalKey();
  bool _hasFrame = false;
  bool get _checkSize => BytedeskUtils.isWeb;
  bool _mobileStyle = true;

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      key: _appKey,
      debugShowCheckedModeBanner: false, // 去除右上角debug的标签
      home: const ExamplePage(),
      builder: (context, widget) {
        if (_checkSize) {
          final size = Get.size;
          final hasFrame = size.width > _mobileWidthThreshold;
          if (hasFrame) {
            return _buildFrame(widget!);
          }
        }
        return widget!;
      },
    );
  }

  // @override
  // void didChangeAppLifecycleState(AppLifecycleState state) {
  //   debugPrint("main didChangeAppLifecycleState:" + state.toString());
  //   switch (state) {
  //     case AppLifecycleState.inactive: // 处于这种状态的应用程序应该假设它们可能在任何时候暂停。
  //       break;
  //     case AppLifecycleState.paused: // 应用程序不可见，后台
  //       break;
  //     case AppLifecycleState.resumed: // 应用程序可见，前台
  //       // APP切换到前台之后，重连
  //       // BytedeskUtils.mqttReConnect();
  //       break;
  //     case AppLifecycleState.detached: // 申请将暂时暂停
  //       break;
  //   }
  // }

  @override
  void didChangeMetrics() {
    if (_checkSize) {
      final size = Get.size;
      final hasFrame = size.width > _mobileWidthThreshold;
      if (_hasFrame != hasFrame) {
        setState(() {
          _hasFrame = hasFrame;
        });
      }
    }
  }

  // 主要用于在web端调试，右上角添加一个按钮，切换宽窄页面
  Widget _buildFrame(Widget app) {
    return Builder(builder: (context) {
      return Scaffold(
        backgroundColor: Theme.of(context).colorScheme.onInverseSurface,
        body: _mobileStyle
            ? Center(
                child: Card(
                  elevation: 10,
                  margin: const EdgeInsets.symmetric(vertical: 16),
                  clipBehavior: Clip.hardEdge,
                  child: SizedBox(
                    height: _mobileHeight,
                    width: _mobileWidth,
                    child: app,
                  ),
                ),
              )
            : app,
        floatingActionButtonLocation: FloatingActionButtonLocation.endTop,
        floatingActionButton: FloatingActionButton(
          onPressed: () {
            setState(() {
              _mobileStyle = !_mobileStyle;
            });
          },
          child: Icon(_mobileStyle ? Icons.computer : Icons.phone_android),
        ).marginOnly(top: 16),
      );
    });
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
    // audioCache?
  }
}
