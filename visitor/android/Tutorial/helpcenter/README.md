# 5 分钟集成帮助中心

- [Demo](https://gitee.com/270580156/bytedesk-android/blob/master/Tutorial/helpcenter)

## 准备工作

- 到[萝卜丝官网](https://www.bytedesk.com/admin/#/antv/user/login)注册管理员账号，并登录管理后台。
- 到 客服管理->渠道管理 添加网站/App
- 到 客服管理->客服账号 有一列 ‘唯一 ID(uid)’ 会在指定客服接口中使用
- 到 客服管理->技能组 有一列 ‘唯一 ID（wId）’ 会在技能组会话中用到
- 获取管理员 uid, 登录后台->客服管理->客服账号->管理员账号(唯一 ID(uid))列
- 获取 appkey，登录后台->客服管理->渠道管理->添加应用->appkey
- 获取 subDomain，也即企业号：登录后台->客服管理->客服账号->企业号

## 开始集成

> 第一步：在项目 build.gradle 的 allprojects -> repositories 添加

```java
maven {
    url  "https://dl.bintray.com/jackning/maven"
}
```

> 修改完后，效果如下：

```java
allprojects {
    repositories {
        jcenter()
        google()
        maven {
            url  "https://dl.bintray.com/jackning/maven"
        }
    }
}
```

> 第二步：在 module 的 build.gradle android{}添加

```java
android {
    ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    ...
}
```

> 第三步：在 module 的 build.gradle dependencies{}添加

```java
// 萝卜丝第三步
// 加载萝卜丝核心库
implementation 'com.bytedesk:libcore:2.8.0'
// 加载萝卜丝默认UI库
implementation 'com.bytedesk:libui:2.8.0'

// 腾讯QMUI界面库
// http://qmuiteam.com/android/page/start.html
// https://bintray.com/chanthuang/qmuirepo
implementation 'com.qmuiteam:qmui:2.0.0-alpha10'
```

> 第四步：AndroidManifest.xml 添加权限

```xml
<!--添加萝卜丝权限-->
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

> 第五步：AndroidManifest.xml 添加 Activity 和 Service

```xml
<!--萝卜丝bytedesk.com代码 开始-->
<!--URL浏览器页面-->
<activity
    android:name="com.bytedesk.ui.activity.BrowserActivity"
    android:theme="@style/AppTheme.ByteDesk"/>
<!--./萝卜丝bytedesk.com代码 结束-->
```

> 第六步：在样式文件 styles.xml 中添加

```xml
<!--添加萝卜丝样式 开始-->
<style name="AppTheme.ByteDesk" parent="QMUI.Compat.NoActionBar">
    <!--导航栏样式-->
    <item name="QMUITopBarStyle">@style/ByteDeskTopBar</item>
</style>
<style name="ByteDeskTopBar" parent="QMUI.TopBar">
    <!--导航栏背景颜色-->
    <item name="android:background">@color/app_color_blue</item>
    <!--导航栏字体颜色-->
    <item name="qmui_topbar_title_color">@color/qmui_config_color_white</item>
    <item name="qmui_topbar_subtitle_color">@color/qmui_config_color_white</item>
    <item name="qmui_topbar_text_btn_color_state_list">@color/qmui_config_color_white</item>
    <!--导航栏高度-->
    <item name="qmui_topbar_height">48dp</item>
    <item name="qmui_topbar_image_btn_height">48dp</item>
</style>
<!--添加萝卜丝样式 结束-->
```

> 第七部：初始化 UI 和建立长连接

参考 demo 中 MainActivity.java

```java
// 初始化萝卜丝UI界面库
BDUiApi.init(this);
// 具体代码请参考MainActivity.java
anonymousLogin();
```

> 第八步：打开帮助中心

```java
// 打开帮助中心界面
// 获取管理员adminUid, 登录后台->客服管理->客服账号->管理员账号(唯一ID(uid))列
BDUiApi.startSupportURLActivity(context, adminUId);
```

## 集成完毕
