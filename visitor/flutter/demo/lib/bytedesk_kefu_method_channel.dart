import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'bytedesk_kefu_platform_interface.dart';

/// An implementation of [BytedeskKefuPlatform] that uses method channels.
class MethodChannelBytedeskKefu extends BytedeskKefuPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('bytedesk_kefu');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
