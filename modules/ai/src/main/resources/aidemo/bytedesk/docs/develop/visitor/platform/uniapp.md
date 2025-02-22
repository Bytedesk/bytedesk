---
sidebar_label: Uniapp
sidebar_position: 3
---

# Uniapp

## 部分功能

- 全部基于 vuejs 开发，不依赖原生 SDK，100%全部开源，支持自定义界面
- 支持 web/h5/小程序/安卓/iOS 等全平台
- 支持人工客服
- 支持机器人
- 支持文字、图片、语音、表情
- 支持消息预知：对方正在输入
- 支持消息状态：送达、已读
- 支持消息撤回
- 对接第三方账号系统/多用户切换
- 支持 vue2/vue3
- 注意：运行项目 bytedesk_demo_vue3 之前，首先需要进入项目文件夹执行 yarn 或者 npm install 初始化
<!-- - 支持发送商品信息 -->
<!-- - 未读消息数查询接口 -->
<!-- - 支持视频客服 -->

## SDK源码及Demo下载

- bytedesk_demo_vue2 和 bytedesk_demo_vue3 的分别是vue2和vue3的demo，请选择其中一个即可
- [Gitee Demo](https://gitee.com/270580156/bytedesk-uniapp)
- [Github Demo](https://github.com/Bytedesk/bytedesk-uniapp)

## 配置步骤说明（共两步）

- 首先：将 components/bytedesk_sdk 文件夹拷贝到自己应用 components 文件夹，
- 然后：在 pages.json 中添加以下几个页面，具体可参考 demo 中 pages.json 文件

```js
{
 "path": "components/bytedesk_sdk/chat-kf",
 "style": {
  "navigationBarTitleText": "微语智能客服",
  "navigationBarBackgroundColor":"#007AFF",
  "navigationBarTextStyle":"white"
 }
},
{
 "path": "components/bytedesk_sdk/rate",
 "style": {
  "navigationBarTitleText": "满意度评价",
  "navigationBarBackgroundColor":"#007AFF",
  "navigationBarTextStyle":"white"
 }
},
{
 "path": "components/bytedesk_sdk/webview",
 "style": {
  "navigationBarTitleText": "微语H5",
  "navigationBarBackgroundColor":"#007AFF",
  "navigationBarTextStyle":"white"
 }
},
{
 "path": "components/bytedesk_sdk/leavemsg",
 "style": {
  "navigationBarTitleText": "留言",
  "navigationBarBackgroundColor":"#007AFF",
  "navigationBarTextStyle":"white"
 }
}
```

## 开发步骤说明（共三步）

- 第一步：引入文件。在调用客服的 vue 页面，如：index.vue，引入

```js
import * as bytedesk from "@/components/bytedesk_sdk/js/bytedesk.js";
```

- 第二步：初始化。在 index.vue 页面 onLoad 函数

```js
// 第二步：初始化
// 获取企业uid，登录后台->客服->渠道->uniapp
// http://www.weiyuai.cn/admin/cs/channel
let orgUid = 'df_org_uid'
bytedesk.init(orgUid);
// 注：如果需要多平台统一用户（用于同步聊天记录等），可使用:
// bytedesk.initWithUidAndNicknameAndAvatar(orgUid, 'myuniappuid', '我是美女', 'https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png');
// bytedesk.initWithUid(orgUid, 'myuniappuid'); // 其中：uid为自定义uid，可与开发者所在用户系统对接，用于多用户切换
// 具体参数可以参考 @/components/bytedesk_sdk/js/bytedesk.js 文件中接口
```

- 第三步：开始会话

```js
// 第三步：获取技能组uid，登录后台->客服->渠道->uniapp
// http://www.weiyuai.cn/admin/cs/channel
startChat () {
  uni.navigateTo({
    url: '../../components/bytedesk_sdk/chat-kf?sid=' + this.workGroupWid + '&type=1'
  });
}
```

- 结束
- 具体请参考 demo 中 index.vue 页面

<!-- |                     首页                     |                     聊天                     |                       H5                       | -->
<!-- | :------------------------------------------: | :------------------------------------------: | :--------------------------------------------: |
| <img src="/img/uniapp/index.jpg?raw=true" width="250"> | <img src="/img/uniapp/robot.jpg?raw=true" width="250"> |   <img src="/img/uniapp/h5.jpg?raw=true" width="250">    |
| <img src="/img/uniapp/chat.jpg?raw=true" width="250">  | <img src="/img/uniapp/rate.png?raw=true" width="250">  | <img src="/img/uniapp/setting.jpg?raw=true" width="250"> | -->

<!-- ## 以下步骤为非必须步骤，开发者可根据需要调用 -->

<!-- ### 视频客服
- 权限配置
- 集成代码
```bash
登录管理后台：https://www.weiyuai.cn/admin，客服管理-》技能组-》获取视频客服代码
``` -->

<!-- ### 获取未读消息数目

用于访客端-查询访客所有未读消息数目

```bash
1. 首先引入 import * as httpApi from '@/components/bytedesk_sdk/js/httpapi.js' (后面说明将省略此步骤说明)
2. 调用接口：
httpApi.getUnreadCountVisitor(response => {
 // console.log('getUnreadCountVisitor: ', response.data)
 let unreadCount = response.data
 if (unreadCount > 0) {
  uni.showToast({ title: '未读消息数目：' + unreadCount, duration: 2000 });
 }
}, error => {
 console.log(error)
})
``` -->

<!-- ### [开启机器人](https://vip.docs.weiyuai.cn/article_202104291459561.html)

机器人会话仅针对技能组开启，指定会话不支持开启机器人

- 登录[管理后台](https://www.weiyuai.cn/admin)

- 首先添加分类，其次添加问答
- <img src="/img/uniapp/images/robot1.png" width="250">

- 在技能组开启机器人。 找到 “客服管理”-》技能组-》点击相应技能组“编辑”按钮
- <img src="/img/uniapp/images/robot2.png" width="250">

- 找到“默认机器人”和“离线机器人”，选择“是”
- <img src="/img/uniapp/images/robot3.png" width="250">

- 开始测试使用机器人 -->

<!-- ### 对接电商商品信息

具体请参考 bytedesk_demo/pages/index/chat_type.vue 文件

- 参数说明：

```bash
goods 是否显示商品信息，如果要显示，设置为goods=1，设置为其他值，则不显示商品信息
goods_id 商品信息id，参数goods=1的情况有效
goods_title 商品信息标题，参数goods=1的情况有效
goods_content 商品信息详情，参数goods=1的情况有效
goods_price 商品信息价格，参数goods=1的情况有效
goods_url 商品信息网址，参数goods=1的情况有效
goods_imageUrl 商品图片，参数goods=1的情况有效
goods_categoryCode 可选，商品信息类别，参数goods=1的情况有效
``` -->

<!-- - 演示代码：

```bash
// url编码
let goodsUrl = encodeURI('https://item.m.jd.com/product/12172344.html')
// 增加商品信息参数
uni.navigateTo({
 url: '../../components/bytedesk_sdk/chat-kf?wid=' + this.workGroupWid
  + '&type=workGroup&aid=&title=微语'
  + '&goods=1'
  + '&goods_categoryCode=101'
  + '&goods_content=商品详情'
  + '&goods_id=123'
  + '&goods_imageUrl=https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/123.webp'
  + '&goods_price=1000'
  + '&goods_title=商品标题'
  + '&goods_url=' + goodsUrl
  + '&history=0'
  + '&lang=cn'
});
``` -->

<!-- ### 点击商品回调

- 可用于点击商品后，跳转自定义页面

```js
// 具体参考demo中chat_type.vue页面
onLoad(option) {
 // 监听点击商品回调
 uni.$on('commodity',function(content) {
  console.log('点击商品回调:', content);
 })
},
onUnload() {
 // 移除点击商品回调监听
 uni.$off('commodity');
}
``` -->

<!-- ### 自定义昵称、头像和备注

具体请参考 bytedesk_demo/pages/index/user_info.vue 文件

```js
// 首先引入 
import * as httpApi from '@/components/bytedesk_sdk/js/httpapi.js'
``` -->

<!-- - 查询当前用户信息：昵称、头像、备注

```js
getProfile () {
 // 查询当前用户信息：昵称、头像
 let app = this
 httpApi.getProfile(function(response) {
  console.log('getProfile success:', response)
  app.uid = response.data.uid
  app.nickname = response.data.nickname
  app.description = response.data.description
  app.avatar = response.data.avatar
 }, function(error) {
  console.log('getProfile error', error)
  uni.showToast({ title: error, duration: 2000 });
 })
}
``` -->

<!-- - 可自定义用户昵称-客服端可见

```js
setNickname () {
 // 可自定义用户昵称-客服端可见
 let mynickname = '自定义APP昵称uniapp'
 let app = this
 httpApi.updateNickname(mynickname, function(response) {
  console.log('updateNickname success:', response)
  app.nickname = mynickname
 }, function(error) {
  console.log('updateNickname error', error)
  uni.showToast({ title: error, duration: 2000 });
 })
}
``` -->

<!-- - 可自定义用户备注信息-客服端可见

```js
setDescription () {
 // 可自定义用户备注信息-客服端可见
 let mydescription = '自定义APP用户备注信息uniapp'
 let app = this
 httpApi.updateDescription(mydescription, function(response) {
  console.log('updateDescription success:', response)
  app.description = mydescription
 }, function(error) {
  console.log('updateDescription error', error)
  uni.showToast({ title: error, duration: 2000 });
 })
}
``` -->

<!-- - 可自定义用户头像 url-客服端可见

```js
setAvatar () {
 // 可自定义用户头像url-客服端可见
 let myavatarurl = 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png'; // 头像网址url
 let app = this
 httpApi.updateAvatar(myavatarurl, function(response) {
  console.log('updateAvatar success:', response)
  app.avatar = myavatarurl
 }, function(error) {
  console.log('updateAvatar error', error)
  uni.showToast({ title: error, duration: 2000 });
 })
}
``` -->

<!-- - 将设置昵称、头像、描述接口合并为一个接口

```js
setProfile () {
 let mynickname = '自定义APP昵称uniapp'
 let myavatarurl = 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png'; // 头像网址url
 let mydescription = '自定义APP用户备注信息uniapp'
 let app = this
 httpApi.updateProfile(mynickname, myavatarurl, mydescription, response => {
  console.log('updateProfile success:', response)
  app.nickname = mynickname
  app.avatar = myavatarurl
  app.description = mydescription
 }, error => {
  console.log('updateAvatar error', error)
  uni.showToast({ title: error, duration: 2000 });
 })
}
``` -->

<!-- ### 获取客服当前在线状态

具体请参考 bytedesk_demo/pages/index/online_status.vue 文件

- 获取技能组在线状态：当技能组中至少有一个客服在线时，显示在线, 其中：workGroupWid 为要查询技能组唯一 wid

```js
getWorkGroupStatus () {
 // 获取技能组在线状态：当技能组中至少有一个客服在线时，显示在线
 // 获取workGroupWid：客服管理->技能组-有一列 ‘唯一ID（wId）’, 默认设置工作组wid
 let app = this
 httpApi.getWorkGroupStatus(this.workGroupWid, function(response) {
  console.log('getWorkGroupStatus success:', response)
  // online代表在线，否则为离线
  app.workGroupOnlineStatus = response.data.status
 }, function(error) {
  console.log('getWorkGroupStatus error', error)
  uni.showToast({ title: error, duration: 2000 });
 })
}
``` -->

<!-- - 获取指定客服在线状态，其中：agentUid 为要查询客服唯一 uid

```js
getAgentStatus () {
 // 获取指定客服在线状态
 let app = this
 httpApi.getAgentStatus(this.agentUid, function(response) {
  console.log('getAgentStatus success:', response)
  // online代表在线，否则为离线
  app.agentOnlineStatus = response.data.status
 }, function(error) {
  console.log('getAgentStatus error', error)
  uni.showToast({ title: error, duration: 2000 });
 })
}
``` -->

<!-- ### 多用户切换

具体请参考 bytedesk_demo/pages/index/switch_user.vue 文件

- 引入文件

```js
// 引入js文件
import * as constants from '@/components/bytedesk_sdk/js/constants.js'
import * as bytedesk from '@/components/bytedesk_sdk/js/bytedesk.js'
import * as httpApi from '@/components/bytedesk_sdk/js/httpapi.js'
``` -->

<!-- - 执行登录之前请先判断是否有用户登录

```js
let isLogin = uni.getStorageSync(constants.isLogin);
if (isLogin) {
 uni.showToast({ title:'请先退出登录', icon:'none', duration: 2000 })
 return
}
``` -->

<!-- - 如果已经存在用户登录，则先执行退出登录 logout

```js
userLogout() {
 uni.showLoading({ title: '退出登录中', icon:'none', duration: 2000 });
 // 退出登录
 httpApi.logout(response => {
  uni.hideLoading();
  uni.showToast({ title:'退出登录成功', icon:'none', duration: 2000 })
 }, error => {
  uni.hideLoading();
  uni.showToast({ title:'退出登录失败', icon:'none', duration: 2000 })
 })
}
``` -->

<!-- - 调用登录接口登录

```js
initWithUsernameAndNicknameAndAvatar(username, nickname, avatar, subDomain, appKey) {
 bytedesk.initWithUsernameAndNicknameAndAvatar(username, nickname, avatar, subDomain, appKey)
 uni.showToast({ title:'登录成功', icon:'none', duration: 2000 })
}
``` -->

### 国际化

- [官方国际化文档配置](https://uniapp.dcloud.net.cn/collocation/i18n)

<!-- ## 微信小程序(百度等小程序，同理参考)

- 配置服务器域名：
- request 合法域名添加：<https://uniapp.weiyuai.cn>;
- socket 合法域名添加：wss://uniapp.weiyuai.cn;
- uploadFile 合法域名：<https://upload.weiyuai.cn>;
- downloadFile 合法域名：<https://upload.weiyuai.cn>; -->

<!-- ## 消息推送

客服消息会额外推送到此地址，开发者可据此实现消息存储和 App 离线推送等。
以 http 或 https 开头，GET 方式调用，参数名: json。注意: url 中不能含有‘?’等字符。
例如：您填写的 url 为：<https://www.example.com/abc>,
系统会自动在 url 末尾添加字符串 ‘?json=’，组成 url：<https://www.example.com/abc?json=消息内容。>
在您服务器，只需要解析 json 参数内容即可

- 技能组：登录管理后台-》客服管理-》技能组-》编辑，滚动到最下方，填写 webhook URL 网址，客服消息会额外推送到此地址
- 指定客服：登录管理后台-》客服管理-》客服账号-》编辑，滚动到最下方，填写 webhook URL 网址，客服消息会额外推送到此地址
- 客服和访客发送的消息均会推送："extra": "{\"agent\":true}" // 其中：true 为客服发送消息，false 为访客发送消息
- 推送消息体 json 格式及说明如下：

```js
{
    "mid": "658835ef-69af-e231-eb3c-4e6685ffc4d3",
    "timestamp": "2021-05-11 17:19:34",
    "client": "web",
    "version": "1",
    "type": "text",
    "user": {
        "uid": "201808221551193",
  "username": "username",
        "nickname": "客服001",
        "avatar": "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/admin_default_avatar.png",
        "extra": "{\"agent\":true}" // 说明：true 为客服发送消息，false 为访客发送消息
    },
    "text": {
        "content": "2"
    },
    "thread": {
        "tid": "202105111719261_20210507193724225efbd47566648d1bb1608b4d1f1a3f2", // 格式说明：时间戳_访客uid
        "type": "workgroup",
        "nickname": "局域网7241[172.16.0.75]",
        "avatar": "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/chrome_default_avatar.png",
        "content": "2",
        "timestamp": "2021-05-11 17:19:34",
        "topic": "201809061716221/20210507193724225efbd47566648d1bb1608b4d1f1a3f2" // 格式说明：技能组wid/访客uid
    }
}
``` -->
