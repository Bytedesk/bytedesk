import 'dart:convert';

import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:flutter/material.dart';

// 多种客服对话类型列表页面
class ChatTypePage extends StatefulWidget {
  const ChatTypePage({Key? key}) : super(key: key);

  @override
  State<ChatTypePage> createState() => _ChatTypePageState();
}

class _ChatTypePageState extends State<ChatTypePage> {
  // 第二步：到 客服管理->技能组-有一列 ‘唯一ID（wId）’, 默认设置工作组wid
  // 说明：一个技能组可以分配多个客服，访客会按照一定的规则分配给组内的各个客服账号
  final String _workGroupWid = "201807171659201"; // 默认人工
  final String _workGroupWidRobot = "201809061716221"; // 默认机器人, 在管理后台开启或关闭机器人
  // 说明：直接发送给此一个客服账号，一对一会话
  final String _agentUid = "201808221551193";
  // 未读消息数目
  String _unreadMessageCount = "0";
  //
  @override
  void initState() {
    // 加载未读消息数目
    _getUnreadCountVisitor();
    //
    super.initState();
  }

  //
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('对话类型'),
        elevation: 0,
      ),
      body: ListView(
        children: ListTile.divideTiles(context: context, tiles: [
          ListTile(
            title: Text('未读消息数目：$_unreadMessageCount'),
            // trailing: Icon(Icons.keyboard_arrow_right),
            onTap: () {
              // 加载未读消息数目
              _getUnreadCountVisitor();
            },
          ),
          Container(
            height: 20,
          ),
          ListTile(
            title: const Text('技能组客服'),
            subtitle: const Text('默认人工'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              BytedeskKefu.startWorkGroupChat(
                  context, _workGroupWid, "技能组客服-默认人工");
            },
          ),
          ListTile(
            title: const Text('技能组客服-机器人'),
            subtitle: const Text('默认热门问题'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              BytedeskKefu.startWorkGroupChat(
                  context, _workGroupWidRobot, "技能组客服-默认机器人问题");
            },
          ),
          ListTile(
            title: const Text('技能组客服-机器人2'),
            subtitle: const Text('默认问题分类'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              BytedeskKefu.startWorkGroupChatV2Robot(
                  context, _workGroupWidRobot, "技能组客服-默认机器人分类");
            },
          ),
          ListTile(
            title: const Text('技能组客服-电商'),
            subtitle: const Text('携带商品参数'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
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
            },
          ),
          ListTile(
            title: const Text('技能组客服-电商-回调'),
            subtitle: const Text('携带商品参数，点击商品支持回调'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
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
                debugPrint('value为custom参数原样返回 $value');
                // 主要用途：用户在聊天页面点击商品消息，回调此接口，开发者可在此打开进入商品详情页
              });
            },
          ),
          ListTile(
            title: const Text('技能组客服-附言'),
            subtitle: const Text('自动发送一条消息'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              BytedeskKefu.startWorkGroupChatPostscript(
                  context, _workGroupWid, "技能组客服-附言", "随便说点什么吧，我会自动发送给客服");
            },
          ),
          Container(
            height: 20,
          ),
          ListTile(
            title: const Text('指定一对一客服'),
            subtitle: const Text('默认人工'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              BytedeskKefu.startAppointedChat(context, _agentUid, "指定一对一客服");
            },
          ),
          ListTile(
            title: const Text('指定一对一客服-电商'),
            subtitle: const Text('携带商品参数'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              // 商品信息，type/title/content/price/url/imageUrl/id/categoryCode
              // 注意：长度不能大于500字符
              var custom = json.encode({
                "type": BytedeskConstants.MESSAGE_TYPE_COMMODITY,
                "title": "商品标题",
                "content": "商品详情",
                "price": "9.99",
                "url": "https://item.m.jd.com/product/12172344.html",
                "imageUrl":
                    "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/123.webp",
                "id": 123,
                "categoryCode": "100010003",
                "client": "flutter"
              });
              BytedeskKefu.startAppointedChatShop(
                  context, _agentUid, "指定一对一客服-电商", custom);
            },
          ),
          ListTile(
            title: const Text('指定一对一客服-电商-回调'),
            subtitle: const Text('携带商品参数，点击商品支持回调'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              // 商品信息，type/title/content/price/url/imageUrl/id/categoryCode
              // 注意：长度不能大于500字符
              var custom = json.encode({
                "type": BytedeskConstants.MESSAGE_TYPE_COMMODITY,
                "title": "商品标题",
                "content": "商品详情",
                "price": "9.99",
                "url": "https://item.m.jd.com/product/12172344.html",
                "imageUrl":
                    "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/123.webp",
                "id": 123,
                "categoryCode": "100010003",
                "client": "flutter",
                // 可自定义添加key:value, 客服端不可见，可用于回调原样返回
                "other1": "", // 可另外添加自定义字段，客服端不可见，可用于回调原样返回
                "other2": "", // 可另外添加自定义字段，客服端不可见，可用于回调原样返回
                "other3": "", // 可另外添加自定义字段，客服端不可见，可用于回调原样返回
              });
              BytedeskKefu.startAppointedChatShopCallback(
                  context, _agentUid, "指定一对一客服-电商-回调", custom, (value) {
                debugPrint('value为custom参数原样返回 $value');
                // 主要用途：用户在聊天页面点击商品消息，回调此接口，开发者可在此打开进入商品详情页
              });
            },
          ),
          ListTile(
            title: const Text('指定一对一客服-附言'),
            subtitle: const Text('自动发送一条消息'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              BytedeskKefu.startAppointedChatPostscript(
                  context, _agentUid, "指定一对一客服-附言", "随便说点什么吧，我会自动发送给客服");
            },
          ),
          Container(
            height: 20,
          ),
          ListTile(
            title: const Text('H5网页会话'),
            trailing: const Icon(Icons.keyboard_arrow_right),
            onTap: () {
              debugPrint('h5 chat');
              // 注意: 登录后台->客服管理->技能组(或客服账号)->获取客服代码 获取相应URL
              String url =
                  "https://h2.kefux.com/chat/h5/index.html?sub=vip&uid=201808221551193&wid=201807171659201&type=workGroup&aid=&hidenav=1&ph=ph";
              String title = 'H5在线客服演示';
              BytedeskKefu.startH5Chat(context, url, title);
            },
          ),
        ]).toList(),
      ),
    );
  }

  void _getUnreadCountVisitor() {
    // 获取消息未读数目
    BytedeskKefu.getUnreadCountVisitor().then((count) => {
          debugPrint('unreadcount:$count'),
          setState(() {
            _unreadMessageCount = count;
          })
        });
  }
}
