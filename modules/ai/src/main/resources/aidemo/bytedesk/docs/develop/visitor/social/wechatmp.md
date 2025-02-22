---
sidebar_label: 微信公众号
sidebar_position: 2
---

# 微信公众号

:::tip
因微信接口限制，必须是已经认证过的公众号才能够使用客服接口。
:::

## 公众号管理后台-》开发接口管理-》基本配置

![wechat_mp_dev_0](/img/wechatmp/wechat_mp_dev_0.png)

## 点击 修改配置“ 按钮

![wechat_mp_dev_1](/img/wechatmp/wechat_mp_dev_1.png)

## 到微语后台获取配置信息，填写完毕之后点击提交

其中：名称添加自己公众号的名称，appId 和 appSecret 分别填写自己公众号的 appId 和 appSecret

![wechat_mp_dev_3](/img/wechatmp/wechat_mp_dev_3.png)
![wechat_mp_dev_6](/img/wechatmp/wechat_mp_dev_6.png)

将上图标记的 "URL" 和 ”Token“，还有随机生成的 EncodingAesKey 分别填写到微信公众号管理后台的 “URL” 、 ”Token“ 和 EncodingAesKey 配置中

![wechat_mp_dev_2](/img/wechatmp/wechat_mp_dev_2.png)

## 点击提交，配置完成

![wechat_mp_dev_4](/img/wechatmp/wechat_mp_dev_4.png)

## 设置IP白名单

```bash
# IP白名单
124.220.58.234
```

- ![wechat_mp_dev_5](/img/wechatmp/wechat_mp_dev_5.png)
