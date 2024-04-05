# 萝卜丝智能客服 - Swift版客服SDK [OC版](https://gitee.com/270580156/bytedesk-oc)

- [官网](https://www.weikefu.net/)
- [价格](https://www.weikefu.net/pages/price.html)
- [管理后台](https://www.weikefu.net/admin)
- [客服工作台](https://www.weikefu.net/chaty)
- [客服端下载](https://www.weikefu.net/pages/download.html)

## 部分功能

- 萝卜丝官方技术支持
- 全部基于Swift开发，100%全部开源，支持自定义界面
- 支持人工客服
- 支持机器人
- 支持文字、图片、语音、表情
- 支持消息预知：对方正在输入
- 支持消息状态：送达、已读
- 支持消息撤回
- 支持发送商品信息
- 未读消息数查询接口
- 对接第三方账号系统
- 支持多用户切换

## 源码及Demo下载

- [Gitee](https://gitee.com/270580156/bytedesk-swift)
- [Github](https://github.com/Bytedesk/bytedesk-swift)

## 集成方式

建议: Xcode Version 14.3，最低兼容: iOS 13

### 1. 本地集成

此方法适用于有自定义界面需求的开发者

- 下载源码，直接拖到自己项目中
- 选择项目，选中项目TARGET，选中 General，在 framework 中添加 bytedesk_swift.framework

### 2. Swift Package Manager (SPM)

<!-- FIXME: 集成报错：
public headers ("include") directory path for 'bytedesk-oc' is invalid or not contained in the target -->
- 源地址1-gitee：<https://gitee.com/270580156/bytedesk-swift>
- 源地址2-github：<https://github.com/Bytedesk/bytedesk-swift>
- 国内用户建议使用源地址1
  
```bash
dependencies: [
    .package(url: "https://gitee.com/270580156/bytedesk-swift", .upToNextMajor(from: "2.9.2"))
]
或
dependencies: [
    .package(url: "https://github.com/Bytedesk/bytedesk-swift", .upToNextMajor(from: "2.9.2"))
]
```

### 3. Carthage

```bash
github 'bytedesk-swift/bytedesk-swift' ~> 2.9.2
```

### 预览

|                      image1                      |                       image2                       |                        image3                        |
| :----------------------------------------------: | :------------------------------------------------: | :--------------------------------------------------: |
| <img src="https://www.weikefu.net/assets/screen/ios_1.jpg" width="250"> | <img src="https://www.weikefu.net/assets/screen/ios_2.jpg" width="250">  |  <img src="https://www.weikefu.net/assets/screen/ios_3.jpg" width="250">  |
| <img src="https://www.weikefu.net/assets/screen/ios_4.jpg" width="250">  | <img src="https://www.weikefu.net/assets/screen/ios_5.jpg" width="250"> | <img src="https://www.weikefu.net/assets/screen/ios_6.jpg" width="250"> |
| <img src="https://www.weikefu.net/assets/screen/ios_7.jpg" width="250">  | <img src="https://www.weikefu.net/assets/screen/ios_8.jpg" width="250"> | <img src="https://www.weikefu.net/assets/screen/ios_9.jpg" width="250"> |

<!-- ### 参考步骤 -->

<!-- - <img src="https://www.weikefu.net/assets/spm/add-package-1.png" width="250"> 
- 此处输入源地址：<img src="https://www.weikefu.net/assets/spm/add-package-2.png" width="500">
- 加载中：<img src="https://www.weikefu.net/assets/spm/add-package-3.png" width="500">
- 点击Add Package：<img src="https://www.weikefu.net/assets/spm/add-package-4.png" width="500">
- 此处查看，如图为添加成功：<img src="https://www.weikefu.net/assets/spm/add-package-5.png" width="500">
- 如果没有bytedesk-oc，则需要手动添加：<img src="https://www.weikefu.net/assets/spm/add-package-6.png" width="500">
- 添加成功之后，便可以在源文件中引用：<img src="https://www.weikefu.net/assets/spm/add-package-7.png" width="500"> -->
<!-- #### 如果加载失败，建议重置 -->
<!-- - <img src="https://www.weikefu.net/assets/spm/add-package-8.png" width="500"> -->

## 技术支持

- 创始于2013年
- QQ-3群: 825257535
- 公众号：
- <img src="https://www.weikefu.net/assets/img/luobosi_mp.png" width="250">

## 其他

- [UniApp SDK](https://github.com/bytedesk/bytedesk-uniapp)
- [iOS SDK](https://github.com/bytedesk/bytedesk-ios)
- [Android SDK](https://github.com/bytedesk/bytedesk-android)
- [Web 端接口](https://github.com/bytedesk/bytedesk-web)
- [微信公众号/小程序接口](https://github.com/bytedesk/bytedesk-wechat)
- [服务器端接口](https://github.com/bytedesk/bytedesk-server)
- [机器人](https://github.com/bytedesk/bytedesk-chatbot)
