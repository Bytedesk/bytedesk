import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

import 'user_info_page.dart';

// 切换用户
class SwitchUserPage extends StatefulWidget {
  const SwitchUserPage({Key? key}) : super(key: key);

  @override
  State<SwitchUserPage> createState() => _SwitchUserPageState();
}

class _SwitchUserPageState extends State<SwitchUserPage> {
  // 获取appkey，登录后台->渠道管理->Flutter->添加应用->获取appkey
  final String _appKey = '81f427ea-4467-4c7c-b0cd-5c0e4b51456f';
  // 获取subDomain，也即企业号：登录后台->客服管理->客服账号->企业号
  final String _subDomain = "vip";

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('切换用户'),
        elevation: 0,
      ),
      body: ListView(
          children: ListTile.divideTiles(
        context: context,
        tiles: [
          ListTile(
            title: const Text('用户信息'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              _userInfo();
            },
          ),
          ListTile(
            title: const Text('用户1男'),
            onTap: () {
              _userBoyLogin();
            },
          ),
          ListTile(
            title: const Text('用户2女'),
            onTap: () {
              _userGirlLogin();
            },
          ),
          ListTile(
            title: const Text('退出登录'),
            onTap: () {
              _userLogout();
            },
          ),
        ],
      ).toList()),
    );
  }

  void _userInfo() {
    Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      return const UserInfoPage();
    }));
  }

  void _userBoyLogin() {
    if (BytedeskKefu.isLogin()) {
      Fluttertoast.showToast(msg: '请先退出登录');
      return;
    }
    _initWithUsernameAndNicknameAndAvatar(
        "myflutteruserboy",
        "我是帅哥flutter",
        "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/boy.png",
        _appKey,
        _subDomain);
  }

  void _userGirlLogin() {
    if (BytedeskKefu.isLogin()) {
      Fluttertoast.showToast(msg: '请先退出登录');
      return;
    }
    Fluttertoast.showToast(msg: '登录中');
    _initWithUsernameAndNicknameAndAvatar(
        "myflutterusergirl",
        "我是美女flutter",
        "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png",
        _appKey,
        _subDomain);
  }

  void _initWithUsernameAndNicknameAndAvatar(String username, String nickname,
      String avatar, String appKey, String subDomain) {
    BytedeskKefu.initWithUsernameAndNicknameAndAvatar(username, nickname, avatar, appKey, subDomain);
  }

  void _userLogout() {
    Fluttertoast.showToast(msg: '退出中');
    BytedeskKefu.logout();
  }
}
