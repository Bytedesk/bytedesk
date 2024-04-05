// ignore_for_file: annotate_overrides

import 'dart:typed_data';

import 'package:bytedesk_kefu/mqtt/lib/mqtt_client.dart';

// TODO: 搞定继承之后，将mqtt包改为package依赖
class BytedeskPayloadBuilder extends MqttClientPayloadBuilder {
  /// Construction
  BytedeskPayloadBuilder() {
    // _payload = typed.Uint8Buffer();
  }

  // typed.Uint8Buffer _payload;

  // added by jackning, 2019/12/03
  void addProtobuf(Uint8List val) {
    // super._payload.addAll(val);
  }
}
