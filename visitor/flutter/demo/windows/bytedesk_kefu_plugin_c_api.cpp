#include "include/bytedesk_kefu/bytedesk_kefu_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "bytedesk_kefu_plugin.h"

void BytedeskKefuPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  bytedesk_kefu::BytedeskKefuPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
