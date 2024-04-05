import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'bytedesk_kefu_method_channel.dart';

abstract class BytedeskKefuPlatform extends PlatformInterface {
  /// Constructs a BytedeskKefuPlatform.
  BytedeskKefuPlatform() : super(token: _token);

  static final Object _token = Object();

  static BytedeskKefuPlatform _instance = MethodChannelBytedeskKefu();

  /// The default instance of [BytedeskKefuPlatform] to use.
  ///
  /// Defaults to [MethodChannelBytedeskKefu].
  static BytedeskKefuPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [BytedeskKefuPlatform] when
  /// they register themselves.
  static set instance(BytedeskKefuPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
