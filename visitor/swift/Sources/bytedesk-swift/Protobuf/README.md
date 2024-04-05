#  proto使用说明

## 生成 .pb.swift 文件

- https://github.com/apple/swift-protobuf
- mac安装：arch -arm64 brew install swift-protobuf
- 因为proto生成文件冲突，需要修改.proto文件，在文件添加
- package Bytedesk;
- 首先进入.proto存放文件夹，如：cd  /Users/ningjinpeng/Desktop/git/private/bytedeskios/bytedesk-swift/Sources/bytedesk-swift/Protobuf
- 运行命令生成：
- swift版本：protoc --swift_out=. message.proto thread.proto user.proto
