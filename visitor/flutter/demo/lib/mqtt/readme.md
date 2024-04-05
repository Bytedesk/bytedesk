# mqtt 说明

- <https://pub.dev/packages/mqtt_client>
- 当前版本10.0.0, 更新日期2023/06/26
- 在mqtt/lib/src/utility/mqtt_client_payload_builder.dart添加

```dart
  // added by jackning, 2019/12/03
  void addProtobuf(Uint8List val) {
    _payload!.addAll(val);
  }
```

- 修改src/messages/connect/mqtt_client_mqtt_connect_payload.dart注释掉函数 set clientIdentifier限制clientId长度的代码
- 修改lib/mqtt_browser_client.dart,替换 import 'dart:html'; 为 import 'package:universal_html/html.dart';

## TODO

- 搞定继承MqttClientPayloadBuilder之后，将mqtt包改为package依赖
