---
sidebar_label: Flutter
sidebar_position: 6
---

# Flutter

- [pub.dev](https://pub.dev/packages/bytedesk_kefu)

## 部分功能

- SDK源码100%开源
- 支持安卓、iOS、Web、Mac、Windows
- 机器人对话
- 技能组客服
- 一对一客服
- 支持发送电商商品信息(支持点击商品回调)
- 支持发送附言消息
- 对接APP用户信息(昵称/头像)
- 获取当前客服在线状态
- 获取历史会话
- 消息提示音/振动设置
- 消息送达/已读
- 消息撤回
- 输入状态(对方正在输入)
- 发送/播放视频
- 查询未读消息数
- 支持绑定第三方账号及多账号切换
<!-- - 提交工单 -->
<!-- - 意见反馈 -->

## SDK源码及Demo下载

- [Gitee Demo](https://gitee.com/270580156/bytedesk-flutter)
- [Github Demo](https://github.com/Bytedesk/bytedesk-flutter)

## 集成步骤

### 第一步

- 微语 pubspec.yaml添加：bytedesk_kefu: ^2.0.0
<!-- - 最新版本：[![Pub](https://img.shields.io/pub/v/bytedesk_kefu.svg)](https://pub.dev/packages/bytedesk_kefu) -->
<!-- - [![pub package](https://img.shields.io/pub/v/bytedesk_kefu.svg)](https://pub.dev/packages/bytedesk_kefu) -->
<!-- [![pub points](https://badges.bar/bytedesk_kefu/pub%20points)](https://pub.dev/packages/bytedesk_kefu/score) | [![popularity](https://badges.bar/bytedesk_kefu/popularity)](https://pub.dev/packages/bytedesk_kefu/score) | [![likes](https://badges.bar/bytedesk_kefu/likes)](https://pub.dev/packages/bytedesk_kefu/score) -->
- [注册账号](https://www.weiyuai.cn/admin/)

- 复制SDK中assets文件夹到自己项目中，并配置pubspec.yaml文件

```dart
# 添加下面3条
assets:
    - assets/audio/
    - assets/images/chat/
    - assets/images/feedback/
```

### 第二步：初始化

```dart
// 获取企业uid，登录后台->客服->渠道->flutter
// http://www.weiyuai.cn/admin/cs/channel
String orgUid = "df_org_uid";
// 第一步：初始化
BytedeskKefu.init(orgUid);
```

### 第三步：联系客服

- 获取技能组workGroupWid：登录后台->客服管理->技能组->唯一wid
- BytedeskKefu.startWorkGroupChat(context, workGroupWid, "技能组客服wid");

<!-- ### 集成完毕

| image1 | image2 | image3 |
| :----------: | :----------: | :----------: |
| <img src="./images/home.jpeg" width="250"> | <img src="./images/robot1.jpeg" width="250"> | <img src="./images/robot2.jpeg" width="250"> |
| <img src="./images/chat.png" width="250"> | <img src="./images/shop.png" width="250"> |<img src="./images/postscript.png" width="250"> |
| <img src="./images/feedback.png" width="250"> |<img src="./images/faq.png" width="250"> |<img src="./images/notice.jpeg" width="250"> | -->

## 以下步骤为非必须步骤，开发者可根据需要调用

<!-- ### 获取未读消息数目

用于访客端-查询访客所有未读消息数目

```dart
void _getUnreadCountVisitor() {
    // 获取消息未读数目
    BytedeskKefu.getUnreadCountVisitor().then((count) => {
        print('unreadcount:' + count),
        setState(() {
        _unreadMessageCount = count;
        })
    });
}
```

### 开启机器人

机器人会话仅针对技能组开启，指定会话不支持开启机器人

- 登录[管理后台](https://www.weikefu.net/admin)

- 首先添加分类，其次添加问答
-

<img src="./images/robot_1.png" width="250">

- 在技能组开启机器人。   找到 “客服管理”-》技能组-》点击相应技能组“编辑”按钮
-

<img src="./images/robot_2.png" width="250">

- 找到“默认机器人”和“离线机器人”，选择“是”
-

<img src="./images/robot_3.png" width="250">

- 开始测试使用机器人

### 对接电商商品信息

具体请参考bytedesk_demo/lib/page/chat_type_page.dart文件

- 演示代码：

```dart
// 商品信息，type/title/content/price/url/imageUrl/id/categoryCode
// 注意：长度不能大于500字符
var custom = json.encode({
"type": BytedeskConstants.MESSAGE_TYPE_COMMODITY, // 不能修改
"title": "商品标题", // 可自定义, 类型为字符串
"content": "商品详情", // 可自定义, 类型为字符串
"price": "9.99", // 可自定义, 类型为字符串
"url":
    "https://item.m.jd.com/product/12172344.html", // 必须为url网址, 类型为字符串
"imageUrl":
    "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/123.webp", //必须为图片网址, 类型为字符串
"id": 123, // 可自定义
"categoryCode": "100010003", // 可自定义, 类型为字符串
"client": "flutter" // 可自定义, 类型为字符串
});
BytedeskKefu.startWorkGroupChatShop(
    context, _workGroupWid, "技能组客服-电商", custom);
```

### 点击商品回调

- 可用于点击商品后，跳转自定义页面

```dart
// 商品信息，type/title/content/price/url/imageUrl/id/categoryCode
// 注意：长度不能大于500字符
var custom = json.encode({
    "type": BytedeskConstants.MESSAGE_TYPE_COMMODITY, // 不能修改
    "title": "商品标题", // 可自定义, 类型为字符串
    "content": "商品详情", // 可自定义, 类型为字符串
    "price": "9.99", // 可自定义, 类型为字符串
    "url":
        "https://item.m.jd.com/product/12172344.html", // 必须为url网址, 类型为字符串
    "imageUrl":
        "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/123.webp", //必须为图片网址, 类型为字符串
    "id": 123, // 可自定义
    "categoryCode": "100010003", // 可自定义, 类型为字符串
    "client": "flutter", // 可自定义, 类型为字符串
    // 可自定义添加key:value, 客服端不可见，可用于回调原样返回
    "other1": "", // 可另外添加自定义字段，客服端不可见，可用于回调原样返回
    "other2": "", // 可另外添加自定义字段，客服端不可见，可用于回调原样返回
    "other3": "", // 可另外添加自定义字段，客服端不可见，可用于回调原样返回
});
BytedeskKefu.startWorkGroupChatShopCallback(
    context, _workGroupWid, "技能组客服-电商-回调", custom, (value) {
    print('value为custom参数原样返回 $value');
    // 主要用途：用户在聊天页面点击商品消息，回调此接口，开发者可在此打开进入商品详情页
});
```

### 自定义昵称、头像和备注

具体请参考bytedesk_demo//lib/page/user_info_page.dart文件

- 查询当前用户信息：昵称、头像、备注

```dart
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
```

- 可自定义用户昵称-客服端可见

```dart
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
```

- 可自定义用户备注信息-客服端可见

```dart
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
```

- 可自定义用户头像url-客服端可见

```dart
void _setAvatar() {
    // 可自定义用户头像url-客服端可见，注意：是头像网址，非本地图片路径
    String myavatarurl = 'https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png'; // 头像网址url
    BytedeskKefu.updateAvatar(myavatarurl).then((user) => {
          setState(() {
            _avatar = myavatarurl;
          }),
          Fluttertoast.showToast(msg: "设置头像成功")
        });
}
```

- 将设置昵称、头像、描述接口合并为一个接口

```dart
// 一次调用接口，同时设置：昵称、头像、备注
  void _updateProfile() {
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
```

### 获取客服当前在线状态

具体请参考bytedesk_demo/lib/page/online_status_page.dart文件

- 获取技能组在线状态：当技能组中至少有一个客服在线时，显示在线, 其中：workGroupWid为要查询技能组唯一wid

```dart
void _getWorkGroupStatus() {
    // 获取技能组在线状态：当技能组中至少有一个客服在线时，显示在线
    BytedeskKefu.getWorkGroupStatus(_workGroupWid).then((status) => {
          print(status),
          setState(() {
            _workGroupStatus = status;
          })
        });
}
```

- 获取指定客服在线状态，其中：agentUid为要查询客服唯一uid

```dart
void _getAgentStatus() {
    // 获取指定客服在线状态
    BytedeskKefu.getAgentStatus(_agentUid).then((status) => {
          print(status),
          setState(() {
            _agentStatus = status;
          })
        });
}
```

### 多用户切换

具体请参考bytedesk_demo/lib/page/switch_user_page.dart文件

- 执行登录之前请先判断是否有用户登录

```dart
if (BytedeskKefu.isLogin()) {
    // Fluttertoast.showToast(msg: '请先退出登录');
    return;
}
```

- 如果已经存在用户登录，则先执行退出登录logout

```dart
void _userLogout() {
    Fluttertoast.showToast(msg: '退出中');
    BytedeskKefu.logout();
}
```

- 调用登录接口登录

```dart
void _initWithUsernameAndNicknameAndAvatar(String username, String nickname,
      String avatar, String appKey, String subDomain) {
    BytedeskKefu.initWithUsernameAndNicknameAndAvatar(username, nickname, avatar, appKey, subDomain);
}
``` -->

### 自定义界面

- 项目中创建文件夹: vendors
<!-- - 去[下载](https://pub.dev/packages/bytedesk_kefu/versions)最新源码, 放在 vendors 文件夹中 -->
- 将 bytedesk_kefu 文件夹放在 vendors 文件夹中
- pubspect.yaml中填写引用本地源码

```dart
bytedesk_kefu:
    path: ./vendors/bytedesk_kefu
```
