import 'package:flutter_test/flutter_test.dart';
import 'package:bytedesk_kefu/bytedesk_kefu.dart';
import 'package:bytedesk_kefu/bytedesk_kefu_platform_interface.dart';
import 'package:bytedesk_kefu/bytedesk_kefu_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockBytedeskKefuPlatform
    with MockPlatformInterfaceMixin
    implements BytedeskKefuPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final BytedeskKefuPlatform initialPlatform = BytedeskKefuPlatform.instance;

  test('$MethodChannelBytedeskKefu is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelBytedeskKefu>());
  });

  test('getPlatformVersion', () async {
    BytedeskKefu bytedeskKefuPlugin = BytedeskKefu();
    MockBytedeskKefuPlatform fakePlatform = MockBytedeskKefuPlatform();
    BytedeskKefuPlatform.instance = fakePlatform;

    expect(await bytedeskKefuPlugin.getPlatformVersion(), '42');
  });
}
