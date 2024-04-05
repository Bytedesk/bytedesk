import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:flutter/material.dart';

// 查询技能组和指定客服账号的在线状态
class OnlineStatusPage extends StatefulWidget {
  const OnlineStatusPage({Key? key}) : super(key: key);

  @override
  State<OnlineStatusPage> createState() => _OnlineStatusPageState();
}

class _OnlineStatusPageState extends State<OnlineStatusPage> {
  // 到 客服管理->技能组-有一列 ‘唯一ID（wId）’
  final String _workGroupWid = "201807171659201";
  // 到 客服管理->客服账号-有一列 ‘唯一ID（uId）’
  final String _agentUid = "201808221551193";
  //
  String _workGroupStatus = ''; // 注：online 代表在线，offline 代表离线
  String _agentStatus = ''; // 注：online 代表在线，offline 代表离线
  //
  @override
  void initState() {
    // 获取技能组在线状态
    _getWorkGroupStatus();
    // 获取指定客服在线状态
    _getAgentStatus();
    super.initState();
  }

  //
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('在线状态'),
        elevation: 0,
      ),
      body: ListView(
          children: ListTile.divideTiles(
        context: context,
        tiles: [
          ListTile(
            title: const Text('技能组在线状态'),
            subtitle: Text(_workGroupStatus),
            onTap: () {
              _getWorkGroupStatus();
            },
          ),
          ListTile(
            title: const Text('客服在线状态'),
            subtitle: Text(_agentStatus),
            onTap: () {
              _getAgentStatus();
            },
          ),
        ],
      ).toList()),
    );
  }

  void _getWorkGroupStatus() {
    // 获取技能组在线状态：当技能组中至少有一个客服在线时，显示在线
    BytedeskKefu.getWorkGroupStatus(_workGroupWid).then((status) => {
          debugPrint(status),
          setState(() {
            _workGroupStatus = status;
          })
        });
  }

  void _getAgentStatus() {
    // 获取指定客服在线状态
    BytedeskKefu.getAgentStatus(_agentUid).then((status) => {
          debugPrint(status),
          setState(() {
            _agentStatus = status;
          })
        });
  }
}
