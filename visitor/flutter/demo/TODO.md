# 客服SDK

- [pub.dev](https://pub.dev/packages/bytedesk_kefu)

## 启用 web

- flutter channel beta
- flutter upgrade
- flutter config --enable-web

- 切换回来：flutter channel stable

## 创建

- [docs](https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms)
- flutter create --org com.bytedesk --template=plugin -i objc -a java bytedesk_kefu
- flutter create --org com.bytedesk --template=plugin --platforms=android,ios,web,macos,windows,linux -i objc -a java bytedesk_kefu
- flutter create --org com.bytedesk --template=plugin --platforms=android,ios,web,macos,windows,linux bytedesk_kefu
- flutter create --org com.bytedesk --template=plugin --platforms=android,ios,web,macos,windows,linux bytedesk_feedback
- -i, 表示指定iOS的语言, objc, swift
- -a, 表示指定安卓的语言, java, kotlin

## 现有plugin添加平台支持，如

- flutter create --template=plugin --platforms=web .
- 格式化代码：dart format .
- 查看依赖：flutter pub deps

## 发布

- [发布插件教程](https://www.jianshu.com/p/d253c3671ecc) 或 [参考2](https://www.jianshu.com/p/f1ed21dc2e30)
- Mac: 启用 Shadowsocks 的 HTTP 代理，确认 HTTP 代理的端口，默认是 10818
- 检查代码质量：flutter analyze
- export http_proxy=http://127.0.0.1:10818
- export https_proxy=http://127.0.0.1:10818
- 检查：flutter pub publish --dry-run
- 发布：flutter packages pub publish --server=https://pub.dev
<!-- dart pub publish --dry-run -->
<!-- dart pub publish --server=https://pub.dev -->

## TODO

- 语音转文本
- deviceToken上传
- TODO:商品信息添加发送按钮
- 小米离线推送、苹果apns离线推送
- 通过参数修改聊天界面为Cupertino风格
- 提交工单
- 意见反馈
- 满意度评价
- 国际化多语言
- 完善机器人
- 常见问题
- 填写表单
- 搜索知识库
- 询前问卷
  - 后台配置称呼
  - 手机
  - 邮箱
  - 问题等选项
<!-- - 对话界面背景色适配-深色、浅色、跟随系统 -->
- 发送图片，上传过程中，UI给出进度提示
- 录制gif-Demo演示图，防止readme.md文件中
- 创建另外几个SDK：bytedesk_ticket, bytedesk_feedback
- 监测网络连接状态，重连
- 聊天记录排序错误
- 进入聊天页面，默认仅显示当前会话聊天记录。下拉显示之前历史记录
- 历史记录开关-关掉之后，新进入页面仅显示进行中会话聊天记录
- 点击头像进入客服详情页面
<!-- TODO:flutter sdk-机器人界面 -->
- 输入框上方 - 热门问题快捷按钮
- 对机器人给出答案进行评价：有帮助、没帮助，并针对“没帮助”的情况，请求用户反馈
- 机器人答案 携带 相关问题列表
- 断网的情况下发送消息？
- 定期检测消息发送状态，超时未发送成功的，给出提示并可重新发送
- 输入框有内容未发送时，退出对话页面，则保存草稿，下次进入支持显示草稿
- 在flutter里面直接嵌入h5页面，支持选择图片

## BUG

- 有时候界面会卡
- 聊天界面打开URL-界面白屏
