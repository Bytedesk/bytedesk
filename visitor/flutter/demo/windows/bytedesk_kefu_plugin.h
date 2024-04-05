#ifndef FLUTTER_PLUGIN_BYTEDESK_KEFU_PLUGIN_H_
#define FLUTTER_PLUGIN_BYTEDESK_KEFU_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace bytedesk_kefu {

class BytedeskKefuPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  BytedeskKefuPlugin();

  virtual ~BytedeskKefuPlugin();

  // Disallow copy and assign.
  BytedeskKefuPlugin(const BytedeskKefuPlugin&) = delete;
  BytedeskKefuPlugin& operator=(const BytedeskKefuPlugin&) = delete;

  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace bytedesk_kefu

#endif  // FLUTTER_PLUGIN_BYTEDESK_KEFU_PLUGIN_H_
