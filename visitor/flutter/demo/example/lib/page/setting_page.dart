import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:flutter/material.dart';
import 'package:list_tile_switch/list_tile_switch.dart';

// 消息声音、振动设置页面
class SettingPage extends StatefulWidget {
  const SettingPage({Key? key}) : super(key: key);

  @override
  State<SettingPage> createState() => _SettingPageState();
}

class _SettingPageState extends State<SettingPage> {
  bool _playAudioOnSendMessage = false;
  bool _playAudioOnReceiveMessage = false;
  bool _vibrateOnReceiveMessage = false;
  //
  @override
  void initState() {
    _playAudioOnSendMessage = BytedeskKefu.getPlayAudioOnSendMessage()!;
    _playAudioOnReceiveMessage = BytedeskKefu.getPlayAudioOnReceiveMessage()!;
    _vibrateOnReceiveMessage = BytedeskKefu.getVibrateOnReceiveMessage()!;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('消息设置'),
        elevation: 0,
      ),
      body: ListView(
          children: ListTile.divideTiles(
        context: context,
        tiles: [
          ListTileSwitch(
            value: _playAudioOnSendMessage,
            onChanged: (value) {
              setState(() {
                _playAudioOnSendMessage = value;
              });
              BytedeskKefu.setPlayAudioOnSendMessage(value);
            },
            title: const Text('发送消息时播放声音'),
          ),
          ListTileSwitch(
            value: _playAudioOnReceiveMessage,
            onChanged: (value) {
              setState(() {
                _playAudioOnReceiveMessage = value;
              });
              BytedeskKefu.setPlayAudioOnReceiveMessage(value);
            },
            title: const Text('收到消息时播放声音'),
          ),
          ListTileSwitch(
            value: _vibrateOnReceiveMessage,
            onChanged: (value) {
              setState(() {
                _vibrateOnReceiveMessage = value;
              });
              // 注意：需要在安卓AndroidManifest.xml添加权限<uses-permission android:name="android.permission.VIBRATE"/>
              BytedeskKefu.setVibrateOnReceiveMessage(value);
            },
            title: const Text('收到消息时振动'),
          ),
        ],
      ).toList()),
    );
  }
}
