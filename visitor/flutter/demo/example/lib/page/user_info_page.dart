import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

// 需要首先调用anonymousLogin之后，再调用此接口
// 自定义用户信息接口-对接APP用户信息
class UserInfoPage extends StatefulWidget {
  const UserInfoPage({Key? key}) : super(key: key);

  @override
  State<UserInfoPage> createState() => _UserInfoPageState();
}

class _UserInfoPageState extends State<UserInfoPage> {
  String _uid = ''; // 用户唯一uid
  String _username = ''; // 用户唯一用户名
  String _nickname = ''; // 用户昵称
  String _avatar = BytedeskConstants.DEFAULT_AVATA; // 用户头像
  String _description = ''; // 用户备注
  @override
  void initState() {
    _getProfile();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('自定义用户信息'),
        elevation: 0,
      ),
      body: ListView(
          children: ListTile.divideTiles(
        context: context,
        tiles: [
          ListTile(
            title: const Text('唯一uid'),
            subtitle: Text(_uid),
          ),
          ListTile(
            title: const Text('用户名'),
            subtitle: Text(_username),
          ),
          ListTile(
            title: const Text('设置昵称(见代码)'),
            subtitle: Text(_nickname),
            onTap: () {
              //
              _setNickname();
            },
          ),
          ListTile(
            leading: Image.network(
              _avatar,
              height: 30,
              width: 30,
            ),
            title: const Text('设置头像(见代码)'),
            onTap: () {
              //
              _setAvatar();
            },
          ),
          ListTile(
            title: const Text('设置备注(见代码)'),
            subtitle: Text(_description),
            onTap: () {
              //
              _setDescription();
            },
          ),
        ],
      ).toList()),
    );
  }

  void _getProfile() {
    // 查询当前用户信息：昵称、头像
    BytedeskKefu.getProfile().then((user) => {
          setState(() {
            _uid = user.uid!;
            _username = user.username!;
            _nickname = user.nickname!;
            _avatar = user.avatar!;
            _description = user.description!;
          })
        });
  }

  void _setNickname() {
    // 可自定义用户昵称-客服端可见
    String mynickname = '自定义APP昵称flutter';
    BytedeskKefu.updateNickname(mynickname).then((user) => {
          setState(() {
            _nickname = mynickname;
          }),
          Fluttertoast.showToast(msg: "设置昵称成功")
        });
  }

  void _setAvatar() {
    // 可自定义用户头像url-客服端可见，注意：是头像网址，非本地图片路径
    String myavatarurl =
        'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png'; // 头像网址url
    BytedeskKefu.updateAvatar(myavatarurl).then((user) => {
          setState(() {
            _avatar = myavatarurl;
          }),
          Fluttertoast.showToast(msg: "设置头像成功")
        });
  }

  void _setDescription() {
    // 可自定义用户备注-客服端可见
    String description = '自定义用户备注';
    BytedeskKefu.updateDescription(description).then((user) => {
          setState(() {
            _description = description;
          }),
          Fluttertoast.showToast(msg: "设置备注成功")
        });
  }

  // 一次调用接口，同时设置：昵称、头像、备注
  void updateProfile() {
    //
    String mynickname = '自定义APP昵称flutter';
    String myavatarurl =
        'https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png'; // 头像网址url
    String mydescription = '自定义用户备注';
    BytedeskKefu.updateProfile(mynickname, myavatarurl, mydescription)
        .then((user) => {
              setState(() {
                _nickname = mynickname;
                _avatar = myavatarurl;
                _description = mydescription;
              }),
              Fluttertoast.showToast(msg: "设置成功")
            });
  }
}
