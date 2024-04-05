import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:bytedesk_kefu/bytedesk_kefu_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelBytedeskKefu platform = MethodChannelBytedeskKefu();
  const MethodChannel channel = MethodChannel('bytedesk_kefu');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
