<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-30 10:35:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-30 10:56:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->

# 离线消息推送

## 通过 苹果、小米、华为 等通道推送离线消息

- 首先执行如下命令将小米推送jar包安装到本地maven库，然后添加依赖到pom
- mvn install:install-file -DgroupId=com.xiaomi -DartifactId=xpush -Dversion=1.0.2 -Dpackaging=jar -Dfile=./libs/MiPush_SDK_Server_Http2/MiPush_SDK_Server_Http2_1.0.11.jar

# 萝卜丝 · protobuf 说明

- [Protocol Buffers](https://developers.google.com/protocol-buffers/docs/proto3?hl=zh-CN)

## 手动生成（废弃）

- 切换到项目源码根目录，如：cd xiaper-spring-boot-data-jpa/src/main/proto
- 生成java文件：protoc --java_out=. message.proto
- 生成objective-c文件：protoc --objc_out=. message.proto
- 生成javascript文件：protoc --js_out=. message.proto

## 自动生成 (推荐)

- 在main下面创建文件夹proto，并存放.proto文件
- 注意在pom.xml文件里面增加的plugins
- mvn clean compile
- 在项目的 target/generated-sources/protobuf/java 下能找到生成的文件，可以直接在本项目中引用，无需拷贝

# mqtt broker

- [Github](https://github.com/sanshengshui/netty-learning-example)
- [博客](https://www.cnblogs.com/sanshengshui/p/9859030.html)

目标是借鉴mqtt标准协议来自定义用于im的私有通信协议。不再以iot为目标，而是以IM为首要目标

## 基于mqtt增加自定义协议

- 群操作
- 好友操作

## 升级协议到-mqttv5
