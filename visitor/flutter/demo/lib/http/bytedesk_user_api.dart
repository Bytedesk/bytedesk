import 'dart:convert';
import 'dart:io';

import 'package:bytedesk_kefu/http/bytedesk_base_api.dart';
import 'package:bytedesk_kefu/model/app.dart';
import 'package:bytedesk_kefu/model/model.dart';
import 'package:bytedesk_kefu/model/userJsonResult.dart';
import 'package:bytedesk_kefu/model/wechatResult.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_events.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:flutter/material.dart';
// import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:sp_util/sp_util.dart';
// import 'package:http/http.dart' as http;

//
class BytedeskUserHttpApi extends BytedeskBaseHttpApi {
  // 授权
  Future<OAuth> oauth(String? username, String? password) async {
    //
    var oauthUrl = BytedeskUtils.getHostUri('/oauth/token');
    Map<String, String> headers = {
      "Authorization": "Basic Y2xpZW50OnNlY3JldA=="
    };
    Map<String, String> bodyMap = {
      "username": "$username",
      "password": "$password",
      "grant_type": "password",
      "scope": "all"
    };
    final oauthResponse =
        await httpClient.post(oauthUrl, headers: headers, body: bodyMap);
    // debugPrint('oauth result: $oauthResponse');
    int statusCode = oauthResponse.statusCode;
    // 200: 授权成功，否则授权失败
    final oauthJson = jsonDecode(oauthResponse.body);
    debugPrint('statusCode:$statusCode, oauth:$oauthJson');
    if (statusCode == 200) {
      SpUtil.putBool(BytedeskConstants.isLogin, true);
      SpUtil.putString(
          BytedeskConstants.accessToken, oauthJson['access_token']);
    } else if (statusCode == 400) {
      // token过期 {error: invalid_grant, error_description: Bad credentials}
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return OAuth.fromJson(statusCode, oauthJson);
  }

  // 授权
  Future<OAuth> visitorOauth(String? username, String? password) async {
    //
    var oauthUrl = BytedeskUtils.getHostUri('/visitor/token');
    Map<String, String> headers = {
      "Authorization": "Basic Y2xpZW50OnNlY3JldA=="
    };
    Map<String, String> bodyMap = {
      "username": "$username",
      "password": "$password",
      "grant_type": "password",
      "scope": "all"
    };
    final oauthResponse =
        await httpClient.post(oauthUrl, headers: headers, body: bodyMap);
    // debugPrint('oauth result: $oauthResponse');
    int statusCode = oauthResponse.statusCode;
    // 200: 授权成功，否则授权失败
    final oauthJson = jsonDecode(oauthResponse.body);
    debugPrint('statusCode:$statusCode, oauth:$oauthJson');
    if (statusCode == 200) {
      SpUtil.putBool(BytedeskConstants.isLogin, true);
      SpUtil.putString(
          BytedeskConstants.accessToken, oauthJson['access_token']);
    } else if (statusCode == 400) {
      // token过期 {error: invalid_grant, error_description: Bad credentials}
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return OAuth.fromJson(statusCode, oauthJson);
  }

  // 验证码登录
  Future<OAuth> smsOAuth(String? mobile, String? code) async {
    //
    final oauthUrl = BytedeskUtils.getHostUri('/mobile/token');
    Map<String, String> headers = {
      "Authorization": "Basic Y2xpZW50OnNlY3JldA=="
    };
    Map<String, String> bodyMap = {
      "mobile": "$mobile",
      "code": "$code",
      "grant_type": "mobile",
      "scope": "all"
    };
    //
    final oauthResponse =
        await httpClient.post(oauthUrl, headers: headers, body: bodyMap);
    // debugPrint('oauth result: $oauthResponse');
    int statusCode = oauthResponse.statusCode;
    // 200: 授权成功，否则授权失败
    final oauthJson = jsonDecode(oauthResponse.body);
    debugPrint('statusCode:$statusCode, smsOAuth:$oauthJson');
    if (statusCode == 200) {
      SpUtil.putBool(BytedeskConstants.isLogin, true);
      SpUtil.putBool(BytedeskConstants.isAuthenticated, true);
      SpUtil.putString(BytedeskConstants.mobile, mobile!);
      SpUtil.putString(
          BytedeskConstants.accessToken, oauthJson['access_token']);
      // 广播登录成功事件
      bytedeskEventBus.fire(SmsLoginSuccessEventBus(mobile));
    } else if (statusCode == 400) {
      // token过期 {error: invalid_grant, error_description: Bad credentials}
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }

    return OAuth.fromJson(statusCode, oauthJson);
  }

  // 通过微信unionId登录
  Future<OAuth> unionIdOAuth(String? unionid) async {
    //
    final oauthUrl = BytedeskUtils.getHostUri('/wechat/token');
    // debugPrint("http api client: oauthUrl $oauthUrl");
    Map<String, String> headers = {
      "Authorization": "Basic Y2xpZW50OnNlY3JldA=="
    };
    Map<String, String> bodyMap = {
      "unionid": "$unionid",
      "grant_type": "wechat",
      "scope": "all"
    };
    //
    final oauthResponse =
        await httpClient.post(oauthUrl, headers: headers, body: bodyMap);
    // debugPrint('oauth result: $oauthResponse');
    // check the status code for the result
    int statusCode = oauthResponse.statusCode;
    // debugPrint("statusCode $statusCode");
    // 200: 授权成功，否则授权失败
    final oauthJson = jsonDecode(oauthResponse.body);
    debugPrint('statusCode:$statusCode, unionIdOAuth:$oauthJson');
    if (statusCode == 200) {
      SpUtil.putBool(BytedeskConstants.isLogin, true);
      SpUtil.putBool(BytedeskConstants.isAuthenticated, true);
      SpUtil.putString(BytedeskConstants.unionid, unionid!);
      SpUtil.putString(
          BytedeskConstants.accessToken, oauthJson['access_token']);
    } else if (statusCode == 400) {
      // token过期 {error: invalid_grant, error_description: Bad credentials}
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    return OAuth.fromJson(statusCode, oauthJson);
  }

  // 良师-手机号注册
  Future<JsonResult> register(String? mobile, String? password) async {
    //
    Map<String, String> headers = {"Content-Type": "application/json"};

    var body = json.encode({
      "mobile": mobile,
      "password": password,
      "admin": false, // 学校端时，修改为true
      "client": client
    });

    final initUrl =
        BytedeskUtils.getHostUri('/visitors/api/v1/register/mobile');
    final initResponse =
        await httpClient.post(initUrl, headers: headers, body: body);

    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("register: $responseJson");

    return JsonResult.fromJson(responseJson);
  }

  // 萝卜丝-访客端-注册匿名用户
  Future<User> registerAnonymous(String? appkey, String? subDomain) async {
    //
    Map<String, String> headers = {"Content-Type": "application/json"};
    //
    final initUrl = BytedeskUtils.getHostUri('/visitor/api/username',
        {'appkey': appkey, 'subDomain': subDomain, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: headers);

    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("registerAnonymous: $responseJson");
    //
    User user = User.fromJson(responseJson['data']);
    //
    SpUtil.putString(BytedeskConstants.uid, user.uid!);
    SpUtil.putString(BytedeskConstants.username, user.username!);
    SpUtil.putString(BytedeskConstants.nickname, user.nickname!);
    SpUtil.putString(BytedeskConstants.avatar, user.avatar!);
    SpUtil.putString(BytedeskConstants.description, user.description!);
    SpUtil.putString(BytedeskConstants.subDomain, user.subDomain!);
    SpUtil.putString(BytedeskConstants.role, BytedeskConstants.ROLE_VISITOR);
    // 解析用户资料
    return user;
  }

  // 注册自定义普通用户：用于IM,
  Future<User> registerUser(String? username, String? nickname,
      String? password, String? avatar, String? subDomain) async {
    //
    Map<String, String> headers = {"Content-Type": "application/json"};
    var body = json.encode({
      "username": username,
      "nickname": nickname,
      "password": password,
      "avatar": avatar,
      "subDomain": subDomain,
      "client": client
    });
    //
    final initUrl = BytedeskUtils.getHostUri('/visitor/api/register/user');
    final initResponse =
        await httpClient.post(initUrl, headers: headers, body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("registerUser:$responseJson");
    //
    int statusCode = responseJson['status_code'];
    if (statusCode == 200) {
      User user = User.fromJson(responseJson['data']);
      //
      SpUtil.putString(BytedeskConstants.uid, user.uid!);
      SpUtil.putString(BytedeskConstants.username, user.username!);
      SpUtil.putString(BytedeskConstants.password, password!);
      SpUtil.putString(BytedeskConstants.nickname, user.nickname!);
      SpUtil.putString(BytedeskConstants.avatar, user.avatar!);
      SpUtil.putString(BytedeskConstants.description, user.description!);
      SpUtil.putString(BytedeskConstants.subDomain, user.subDomain!);
      // 解析用户资料
      return user;
    } else {
      //
      SpUtil.putString(BytedeskConstants.uid, responseJson['data']);
      SpUtil.putString(
          BytedeskConstants.username, '${username!}@${subDomain!}');
      SpUtil.putString(BytedeskConstants.password, password!);
      SpUtil.putString(BytedeskConstants.nickname, nickname!);
      SpUtil.putString(BytedeskConstants.avatar, avatar!);
      SpUtil.putString(BytedeskConstants.description, "");
      SpUtil.putString(BytedeskConstants.subDomain, subDomain);
    }
    return const User();
  }

  // 修改密码
  Future<JsonResult> changePassword(String? mobile, String? password) async {
    Map<String, String> headers = {"Content-Type": "application/json"};

    var body =
        json.encode({"mobile": mobile, "password": password, "client": client});

    final initUrl = BytedeskUtils.getHostUri('/visitors/api/v1/change');
    final initResponse =
        await httpClient.post(initUrl, headers: headers, body: body);

    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // final responseJson = json.decode(initResponse.body);
    // debugPrint("changePassword");
    // BytedeskUtils.printLog(responseJson);
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return JsonResult.fromJson(responseJson);
  }

  // 良师宝-请求验证码
  Future<CodeResult> requestCode(String? mobile) async {
    //
    Map<String, String> headers = {"Content-Type": "application/json"};
    final initUrl = BytedeskUtils.getHostUri(
        '/sms/api/send/liangshibao', {'mobile': mobile, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: headers);
    // .timeout(
    //   const Duration(seconds: 1),
    //   onTimeout: () {
    //     return http.Response('Error', 408); // Request Timeout response status code
    //   },
    // );
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("requestCode: ${responseJson}");

    SpUtil.putBool(BytedeskConstants.exist, responseJson['data']['exist']);
    SpUtil.putString(BytedeskConstants.code, responseJson['data']['code']);
    //
    User user = User.fromJson(responseJson['data']['user']);
    SpUtil.putString(BytedeskConstants.uid, user.uid!);
    SpUtil.putString(BytedeskConstants.username, user.username!);
    SpUtil.putString(BytedeskConstants.nickname, user.nickname!);
    SpUtil.putString(BytedeskConstants.avatar, user.avatar!);
    SpUtil.putString(BytedeskConstants.mobile, user.mobile ?? '');
    SpUtil.putString(BytedeskConstants.description, user.description!);
    SpUtil.putString(BytedeskConstants.subDomain, user.subDomain!);

    return CodeResult.fromJson(responseJson);
  }

  // 剪贴助手-请求验证码
  Future<CodeResult> requestCodeWeiTongbu(String? mobile) async {
    //
    Map<String, String> headers = {"Content-Type": "application/json"};
    final initUrl = BytedeskUtils.getHostUri(
        '/sms/api/send/jiantie', {'mobile': mobile, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: headers);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("requestCode: ${responseJson}");

    SpUtil.putBool(BytedeskConstants.exist, responseJson['data']['exist']);
    SpUtil.putString(BytedeskConstants.code, responseJson['data']['code']);
    //
    User user = User.fromJson(responseJson['data']['user']);
    SpUtil.putString(BytedeskConstants.uid, user.uid!);
    SpUtil.putString(BytedeskConstants.username, user.username!);
    SpUtil.putString(BytedeskConstants.nickname, user.nickname!);
    SpUtil.putString(BytedeskConstants.avatar, user.avatar!);
    SpUtil.putString(BytedeskConstants.mobile, user.mobile ?? '');
    SpUtil.putString(BytedeskConstants.description, user.description!);
    SpUtil.putString(BytedeskConstants.subDomain, user.subDomain!);
    //
    return CodeResult.fromJson(responseJson);
  }

  // 微语AI-请求验证码
  Future<CodeResult> requestCodeWeiyuAI(String? mobile) async {
    //
    Map<String, String> headers = {"Content-Type": "application/json"};
    final initUrl = BytedeskUtils.getHostUri(
        '/sms/api/send/weiyu', {'mobile': mobile, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: headers);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("requestCode: ${responseJson}");

    SpUtil.putBool(BytedeskConstants.exist, responseJson['data']['exist']);
    SpUtil.putString(BytedeskConstants.code, responseJson['data']['code']);
    //
    User user = User.fromJson(responseJson['data']['user']);
    SpUtil.putString(BytedeskConstants.uid, user.uid!);
    SpUtil.putString(BytedeskConstants.username, user.username!);
    SpUtil.putString(BytedeskConstants.nickname, user.nickname!);
    SpUtil.putString(BytedeskConstants.avatar, user.avatar!);
    SpUtil.putString(BytedeskConstants.mobile, user.mobile ?? '');
    SpUtil.putString(BytedeskConstants.description, user.description!);
    SpUtil.putString(BytedeskConstants.subDomain, user.subDomain!);

    return CodeResult.fromJson(responseJson);
  }

  // 绑定手机号
  Future<JsonResult> bindMobile(String? mobile) async {
    //
    String? uid = SpUtil.getString(BytedeskConstants.uid);
    var body = json.encode({"uid": uid, "mobile": mobile, "client": client});
    //
    final initUrl = BytedeskUtils.getHostUri('/api/user/bind/mobile');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("responseJson $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    int statusCode = responseJson['status_code'];
    if (statusCode == 200) {
      SpUtil.putBool(BytedeskConstants.isAuthenticated, true);
      SpUtil.putString(BytedeskConstants.mobile, mobile!);
      SpUtil.putString(BytedeskConstants.nickname, '用户${mobile.substring(7)}');
    }
    return JsonResult.fromJson(responseJson);
  }

  /// 初始化
  Future<User> getProfile() async {
    //
    String? uid = SpUtil.getString(BytedeskConstants.uid);
    final initUrl = BytedeskUtils.getHostUri(
        '/api/user/profile/uid', {'uid': uid, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("$initUrl, getProfile:$responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
      return User.fromInvalidToken();
    } else {
      //
      User user = User.fromJson(responseJson['data']);
      //
      SpUtil.putString(BytedeskConstants.uid, user.uid!);
      SpUtil.putString(BytedeskConstants.username, user.username!);
      SpUtil.putString(BytedeskConstants.nickname, user.nickname!);
      SpUtil.putString(BytedeskConstants.avatar, user.avatar!);
      SpUtil.putString(BytedeskConstants.mobile, user.mobile ?? '');
      SpUtil.putString(BytedeskConstants.description, user.description!);
      SpUtil.putString(BytedeskConstants.subDomain, user.subDomain!);
      // TODO: 通知前端更新
      // 解析用户资料
      return user;
    }
  }

  // 更新昵称
  Future<User> updateNickname(String? nickname) async {
    //
    var body = json.encode({"nickname": nickname, "client": client});
    final initUrl = BytedeskUtils.getHostUri('/api/user/nickname');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("updateNickname:$responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // 更新本地数据
    SpUtil.putString(BytedeskConstants.nickname, nickname!);

    return User.fromJson(responseJson['data']);
  }

  // 更新头像
  Future<User> updateAvatar(String? avatar) async {
    //
    var body = json.encode({"avatar": avatar, "client": client});
    //
    final initUrl = BytedeskUtils.getHostUri('/api/user/avatar');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("updateAvatar:$responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // 更新本地数据
    SpUtil.putString(BytedeskConstants.avatar, avatar!);

    return User.fromJson(responseJson['data']);
  }

  // 更新个性签名
  Future<User> updateDescription(String? description) async {
    //
    var body = json.encode({"description": description, "client": client});
    //
    // final initUrl = '$baseUrl/api/user/description';
    final initUrl = BytedeskUtils.getHostUri('/api/user/description');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("updateDescription:$responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // 更新本地数据
    SpUtil.putString(BytedeskConstants.description, description!);

    return User.fromJson(responseJson['data']);
  }

  // 一个接口同时设置：昵称、头像、备注
  Future<User> updateProfile(
      String? nickname, String? avatar, String? description) async {
    //
    var body = json.encode({
      "nickname": nickname,
      "avatar": avatar,
      "description": description,
      "client": client
    });
    //
    final initUrl =
        BytedeskUtils.getHostUri('/api/user/update/visitor/profile');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("updateProfile:$responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // 更新本地数据
    SpUtil.putString(BytedeskConstants.nickname, nickname!);
    SpUtil.putString(BytedeskConstants.avatar, avatar!);
    SpUtil.putString(BytedeskConstants.description, description!);

    return User.fromJson(responseJson['data']);
  }

  // 更新性别
  Future<User> updateSex(bool? sex) async {
    //
    var body = json.encode({"sex": sex, "client": client});
    //
    // final initUrl = '$baseUrl/api/user/sex';
    final initUrl = BytedeskUtils.getHostUri('/api/user/sex');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("updateSex $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // 更新本地数据
    SpUtil.putBool(BytedeskConstants.sex, sex!);

    return User.fromJson(responseJson['data']);
  }

  // 更新地区
  Future<User> updateLocation(String? location) async {
    //
    var body = json.encode({"location": location, "client": client});
    //
    // final initUrl = '$baseUrl/api/user/location';
    final initUrl = BytedeskUtils.getHostUri('/api/user/location');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("updateLocation $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // 更新本地数据
    SpUtil.putString(BytedeskConstants.location, location!);

    return User.fromJson(responseJson['data']);
  }

  // 更新生日
  Future<User> updateBirthday(String? birthday) async {
    //
    var body = json.encode({"birthday": birthday, "client": client});
    //
    // final initUrl = '$baseUrl/api/user/birthday';
    final initUrl = BytedeskUtils.getHostUri('/api/user/birthday');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("updateBirthday $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // 更新本地数据
    SpUtil.putString(BytedeskConstants.birthday, birthday!);

    return User.fromJson(responseJson['data']);
  }

  // 更新手机号
  Future<User> updateMobile(String? mobile) async {
    //
    var body = json.encode({"mobile": mobile, "client": client});
    //
    // final initUrl = '$baseUrl/api/user/mobile';
    final initUrl = BytedeskUtils.getHostUri('/api/user/mobile');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("updateMobile $responseJson");
    // 更新本地数据
    SpUtil.putString(BytedeskConstants.mobile, mobile!);

    return User.fromJson(responseJson['data']);
  }

  // https://pub.dev/documentation/http/latest/http/MultipartRequest-class.html
  // Future<String> upload(String? filePath) async {
  //   //
  //   String? fileName = filePath!.split("/").last;
  //   String? username = SpUtil.getString(BytedeskConstants.uid);

  //   final uploadUrl = '$baseUrl/visitor/api/upload/image';
  //   BytedeskUtils.printLog(
  //       "fileName $fileName, username $username, upload Url $uploadUrl");

  //   var uri = Uri.parse(uploadUrl);
  //   var request = http.MultipartRequest('POST', uri)
  //     ..fields['file_name'] = fileName
  //     ..fields['username'] = username!
  //     ..files.add(await http.MultipartFile.fromPath('file', filePath));

  //   http.Response response =
  //       await http.Response.fromStream(await request.send());
  //   // debugPrint("Result: ${response.body}");

  //   //解决json解析中的乱码问题
  //   Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
  //   //将string类型数据 转换为json类型的数据
  //   final responseJson = json.decode(utf8decoder.convert(response.bodyBytes));
  //   // debugPrint("responseJson $responseJson");
  //   // TODO: 根据status_code判断结果，并解析

  //   String? url = responseJson['data'];
  //   // debugPrint('url:' + url);
  //   return url!;
  // }

  // 获取技能组在线状态
  Future<String> getWorkGroupStatus(String? workGroupWid) async {
    //
    final initUrl = BytedeskUtils.getHostUri(
        '/api/status/workGroup', {'wid': workGroupWid, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("responseJson $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // TODO: 根据status_code判断结果，并解析
    // 解析
    return responseJson['data']['status'].toString();
  }

  // 获取客服在线状态
  Future<String> getAgentStatus(String? agentUid) async {
    //
    final initUrl = BytedeskUtils.getHostUri(
        '/api/status/agent', {'uid': agentUid, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("responseJson $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // TODO: 根据status_code判断结果，并解析
    // 解析
    return responseJson['data']['status'].toString();
  }

  // 查询当前用户-某技能组wid或指定客服未读消息数目
  // 注意：技能组wid或指定客服唯一id
  // 适用于 访客 和 客服
  Future<String> getUnreadCount(String? wid) async {
    //
    final initUrl = BytedeskUtils.getHostUri(
        '/api/messages/unreadCount', {'wid': wid, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("responseJson $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // TODO: 根据status_code判断结果，并解析
    // 解析
    return responseJson['data'].toString();
  }

  // 访客端-查询访客所有未读消息数目
  Future<String> getUnreadCountVisitor() async {
    //
    final initUrl = BytedeskUtils.getHostUri(
        '/api/messages/unreadCount/visitor', {'client': client});
    final initResponse = await httpClient.get(initUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("responseJson $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // TODO: 根据status_code判断结果，并解析
    // {error: invalid_token, error_description: Invalid access token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2ODA1MTEwMjQsInVzZXJfbmFtZSI6IjIwMjMwMzA0MTYzNzAyMWZlN2Y3ODA0NjI1YTQ1MTJiMzJmOWYxZTc2NmY0ODAyIiwianRpIjoiODNjMzM0ZDItZWM5Ny00MGEwLTg3MTgtOWRmYjIxYzVjMWJhIiwiY2xpZW50X2lkIjoiY2xpZW50Iiwic2NvcGUiOlsiYWxsIl19.-8aqZBq4mqEvKUyaHtisrH-uZVQGJswMB9jBaZWTbLs}
    // 解析
    return responseJson['data'].toString();
  }

  // 客服端-查询客服所有未读消息数目
  Future<String> getUnreadCountAgent() async {
    //
    final initUrl = BytedeskUtils.getHostUri(
        '/api/messages/unreadCount/agent', {'client': client});
    final initResponse = await httpClient.get(initUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("responseJson $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // TODO: 根据status_code判断结果，并解析
    // 解析
    return responseJson['data'].toString();
  }

  // 检测是否有新版本
  Future<App> checkAppVersion(String? appkey) async {
    //
    final initUrl = BytedeskUtils.getHostUri(
        '/visitor/api/app/version', {'key': appkey, 'client': client});
    final initResponse =
        await httpClient.get(initUrl, headers: getHeadersForVisitor());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("checkAppVersion:$responseJson");
    // 判断token是否过期
    // if (responseJson.toString().contains('invalid_token')) {
    //   bytedeskEventBus.fire(InvalidTokenEventBus());
    // }
    // TODO: 根据status_code判断结果，并解析
    int statusCode = responseJson['status_code'];
    if (statusCode == 200) {
      return App.fromJson(responseJson['data']);
    }
    return App(version: "0");
  }

  // 通过token获取手机号
  Future<String> getAliyunOneKeyLoginMobile(String? token) async {
    //
    final initUrl = BytedeskUtils.getHostUri(
        '/aliyun/mobile', {'token': token, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("responseJson $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    // 解析
    return responseJson['data'].toString();
  }

  // 微信登录之后，获取微信用户信息
  Future<WeChatResult> getWechatUserinfo(String? code) async {
    //
    Map<String, String> headers = {
      HttpHeaders.contentTypeHeader: "application/json",
    };
    final initUrl = BytedeskUtils.getHostUri(
        '/visitor/api/lsb/app/wechat/info', {'code': code, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: headers);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("responseJson $responseJson");
    // 解析
    return WeChatResult.fromJson(responseJson);
  }

  // 手机端注册微信登录用户-绑定手机号
  Future<UserJsonResult> registerWechatMobile(String? mobile, String? nickname,
      String? avatar, String? unionid, String? openid) async {
    //
    Map<String, String> headers = {
      HttpHeaders.contentTypeHeader: "application/json"
    };
    //
    var body = json.encode({
      "mobile": mobile,
      "nickname": nickname,
      "avatar": avatar,
      "unionid": unionid,
      "openid": openid,
      "admin": false,
      "client": client
    });
    //
    final initUrl =
        BytedeskUtils.getHostUri('/visitor/api/register/wechat/mobile');
    final initResponse =
        await httpClient.post(initUrl, headers: headers, body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("responseJson $responseJson");
    //
    UserJsonResult userJsonResult = UserJsonResult.fromJson(responseJson);
    if (userJsonResult.statusCode == 200) {
      SpUtil.putString(BytedeskConstants.mobile, mobile!);
      SpUtil.putString(BytedeskConstants.nickname, nickname!);
      SpUtil.putString(BytedeskConstants.avatar, avatar!);
      SpUtil.putString(BytedeskConstants.unionid, unionid!);
      SpUtil.putString(BytedeskConstants.openid, openid!);
    }
    // 新账号，mqtt需要重连
    return userJsonResult;
  }

  // 将unionid绑定到已经存在的手机账号
  Future<UserJsonResult> bindWeChatMobile(
      String? mobile, String? unionid) async {
    //
    Map<String, String> headers = {
      HttpHeaders.contentTypeHeader: "application/json"
    };
    //
    var body =
        json.encode({"mobile": mobile, "unionid": unionid, "client": client});
    //
    // final initUrl = '$baseUrl/visitor/api/bind/wechat/mobile';
    final initUrl = BytedeskUtils.getHostUri('/visitor/api/bind/wechat/mobile');
    final initResponse =
        await httpClient.post(initUrl, headers: headers, body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("responseJson $responseJson");
    //
    UserJsonResult userJsonResult = UserJsonResult.fromJson(responseJson);
    if (userJsonResult.statusCode == 200) {
      SpUtil.putString(BytedeskConstants.mobile, mobile!);
      SpUtil.putString(BytedeskConstants.unionid, unionid!);
      SpUtil.putBool(BytedeskConstants.isAuthenticated, true);
    }
    //
    return userJsonResult;
  }

  // 查询是否已经关注
  Future<bool> isFollowed(String? uid) async {
    //
    final initUrl = BytedeskUtils.getHostUri(
        '/api/user/isfollowed', {'uid': uid, 'client': client});
    final initResponse = await httpClient.get(initUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("responseJson $responseJson");
    // 解析
    return responseJson['data'];
  }

  // 关注
  Future<JsonResult> follow(String? uid) async {
    //
    var body = json.encode({"uid": uid, "client": client});
    final initUrl = BytedeskUtils.getHostUri('/api/user/follow');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("responseJson $responseJson");
    //
    return JsonResult.fromJson(responseJson);
  }

  // 取消关注
  Future<JsonResult> unfollow(String? uid) async {
    //
    var body = json.encode({"uid": uid, "client": client});
    final initUrl = BytedeskUtils.getHostUri('/api/user/unfollow');
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // debugPrint("unfollow:");
    // BytedeskUtils.printLog(responseJson);
    return JsonResult.fromJson(responseJson);
  }

  // 退出登录
  Future<void> logout() async {
    String? accessToken = SpUtil.getString(BytedeskConstants.accessToken);
    if (accessToken!.isNotEmpty) {
      Map<String, String> headers = {"Content-Type": "application/json"};
      //
      var body = json.encode({"client": client});
      final initUrl = BytedeskUtils.getHostUri(
          '/api/user/logout', {'access_token': accessToken});
      final initResponse =
          await httpClient.post(initUrl, headers: headers, body: body);
      //解决json解析中的乱码问题
      Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
      //将string类型数据 转换为json类型的数据
      final responseJson =
          json.decode(utf8decoder.convert(initResponse.bodyBytes));
      debugPrint("logout:$responseJson");
      BytedeskUtils.clearUserCache();
    }
  }
}
