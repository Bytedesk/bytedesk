# 说明

- js压缩工具：<https://www.css-js.com/>
- js压缩工具2：<https://skalman.github.io/UglifyJS-online/>
- css压缩工具：<https://tool.css-js.com/>
- mintui库：<https://mint-ui.github.io/#!/zh-cn>
- weui库：<https://github.com/Tencent/weui.js>

## 功能需求 TODO:

<!-- - 传递用户名、昵称、头像-跟开发者系统用户对接 -->
<!-- - 显示消息发送状态，发送中/发送成功/发送失败 -->
<!-- - 发送消息失败，显示'错误'按钮，点击‘错误’消息继续发送 -->
<!-- - 长链接断开，或网络恢复之后，自动拉取未读消息 -->
- 发送表情
<!-- - 发送视频 -->
- 判断是否在微信内，并：
  - 获取微信用户信息
  - 支持录音
  - 获取并发送位置
<!-- - 压缩图片，支持发送大图片 -->
<!-- - 浏览器本地存储部分聊天记录，直接从本地加载。可加快聊天记录加载速度 -->
- 留言界面增加图片上传
- 手机上支持半弹窗/半屏
- 微信营销组件：引导复制微信号，扫描二维码，跳转小程序加粉
<!-- - 检测网络，网络断开提示 -->
<!-- - 长按一条消息复制 -->
<!-- - bug: 机器人回答答案因为长按功能导致无法点击‘有帮助’和‘无帮助’ -->
<!-- - bug：点击‘有帮助’和‘无帮助’提示aid找不到 -->
- 增加调试参数：开启此参数，生产环境可查看打印log，默认关闭
- 聊天记录有多条，当划到上方时，显示滚动到最下方按钮；当滑动到上方时，收到新消息，提示未读数目，点击可快速定位到新消息
- 超过一定时长未收到客服回复，提示用户留言：邮箱、手机号

## h5视频客服 TODO:

- 客服端支持不开启摄像头，使用自定义视频/图片作为客服视频源展现
<!-- - 视频对话 -->
- 纯语音对话，并可升级为视频对话
<!-- - 支持微信里面调用摄像头 -->
<!-- - 支持发送消息 -->
- BUG: 视频会话结束后，web客服端未完全释放摄像头（摄像头仍旧亮着）
- 客服端支持截屏，并保存到服务器(新建表单独保存)
- 客服端支持录屏，并保存到服务器(新建表单独保存)
- 屏幕共享
- 点击视频可点击 切换本地和远程视频 画面
- 支持客服端主动发起视频会话
- 支持客服端主动发起音频会话
- index.html支持视频会话，由客服端发起
- 页面右侧增加投诉入口，用户可发起投诉。认证的用户可隐藏，超级管理员后台设置
- 不打开聊天窗口，发送消息和显示消息气泡

## 刷新cdn

```js
https://cdn.kefux.com/assets/css/vendor/mintui/2.2.13/style.css
https://cdn.kefux.com/assets/css/vendor/vuephotopreview/skin.css
https://cdn.kefux.com/assets/js/vendor/jquery/1.9.1/jquery.min.js
https://cdn.kefux.com/assets/js/vendor/sockjs/1.1.4/sockjs.min.js
https://cdn.kefux.com/assets/js/vendor/stomp/1.2/stomp.min.js
https://cdn.kefux.com/assets/js/vendor/vue/2.6.10/vue.min.js
https://cdn.kefux.com/assets/js/vendor/mintui/2.2.13/index.js
https://cdn.kefux.com/assets/js/vendor/i18n/8.21.1/vue-i18n.min.js
https://cdn.kefux.com/assets/js/vendor/lodash/4.17.20/lodash.min.js
https://cdn.kefux.com/assets/js/vendor/moment/2.22.1/moment.min.js
https://cdn.kefux.com/assets/js/vendor/vuephotopreview/vue-photo-preview.js
https://cdn.kefux.com/assets/js/float/narrow/common/bd_kfe_device.min.js
```
