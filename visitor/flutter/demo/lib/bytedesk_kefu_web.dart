// In order to *not* need this ignore, consider extracting the "web" version
// of your plugin as a separate package, instead of inlining it in the same
// package as the core of your plugin.
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html show window;

import 'package:flutter_web_plugins/flutter_web_plugins.dart';

import 'bytedesk_kefu_platform_interface.dart';

/// A web implementation of the BytedeskKefuPlatform of the BytedeskKefu plugin.
class BytedeskKefuWeb extends BytedeskKefuPlatform {
  /// Constructs a BytedeskKefuWeb
  BytedeskKefuWeb();

  static void registerWith(Registrar registrar) {
    BytedeskKefuPlatform.instance = BytedeskKefuWeb();
  }

  /// Returns a [String] containing the version of the platform.
  @override
  Future<String?> getPlatformVersion() async {
    final version = html.window.navigator.userAgent;
    return version;
  }
}
