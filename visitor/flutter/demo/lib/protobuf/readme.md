# Protobuf

## 生成protobuf

- pub global activate protoc_plugin
- cd /Users/ningjinpeng/Desktop/GitOSChina/bytedeskflutter/bytedesk_sdk/bytedesk_kefu/lib/protobuf
- protoc --proto_path=./proto --dart_out=. message.proto thread.proto user.proto
