// ignore_for_file: unnecessary_null_comparison, constant_identifier_names, avoid_print

import 'dart:io';
import 'dart:math';

import 'package:bytedesk_kefu/mqtt/bytedesk_mqtt.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:sp_util/sp_util.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:intl/intl.dart';
// import 'package:flutter_image_compress/flutter_image_compress.dart';
// import 'package:permission_handler/permission_handler.dart';
/// 使用 DefaultCacheManager 类（可能无法自动引入，需要手动引入）
import 'package:flutter_cache_manager/flutter_cache_manager.dart';

/// 授权管理
// import 'package:permission_handler/permission_handler.dart';
/// 图片缓存管理
import 'package:cached_network_image/cached_network_image.dart';

/// 保存文件或图片到本地
import 'package:image_gallery_saver/image_gallery_saver.dart';
import 'package:flutter/services.dart' show rootBundle;

class BytedeskUtils {
  //
  static bool get isDesktop => !isWeb && (isWindows || isLinux || isMacOS);
  static bool get isMobile => isAndroid || isIOS;
  static bool get isWeb => kIsWeb;
  static bool get isWindows => Platform.isWindows;
  static bool get isLinux => Platform.isLinux;
  static bool get isMacOS => Platform.isMacOS;
  static bool get isAndroid => Platform.isAndroid;
  static bool get isFuchsia => Platform.isFuchsia;
  static bool get isIOS => Platform.isIOS;

  static void printLog(content) {
    if (BytedeskConstants.isDebug) {
      print(content);
    }
  }

  static bool isDarkMode(BuildContext context) {
    return Theme.of(context).brightness == Brightness.dark;
  }

  static String client() {
    if (isWeb) {
      return 'flutter_web';
    } else if (isAndroid) {
      return 'flutter_android';
    } else if (isIOS) {
      return 'flutter_ios';
    } else if (isMacOS) {
      return 'flutter_mac';
    } else if (isWindows) {
      return 'flutter_windows';
    } else if (isLinux) {
      return 'flutter_linux';
    } else {
      return 'flutter_web';
    }
  }

  static String clientSchool() {
    if (isWeb) {
      return 'flutter_web_school';
    } else if (isAndroid) {
      return 'flutter_android_school';
    } else if (isIOS) {
      return 'flutter_ios_school';
    } else if (isMacOS) {
      return 'flutter_mac_school';
    } else if (isWindows) {
      return 'flutter_windows_school';
    } else if (isLinux) {
      return 'flutter_linux_school';
    } else {
      return 'flutter_web_school';
    }
  }

  static String getClient() {
    String? role = SpUtil.getString(BytedeskConstants.role,
        defValue: BytedeskConstants.ROLE_VISITOR);
    // 区分用户端，还是学校端
    return (role == BytedeskConstants.ROLE_VISITOR)
        ? BytedeskUtils.client()
        : BytedeskUtils.clientSchool();
  }

  static String getBaseUrl() {
    if (isWeb) {
      return BytedeskConstants.httpBaseUrl;
    } else if (isAndroid) {
      return BytedeskConstants.httpBaseUrlAndroid;
    } else if (isIOS) {
      return BytedeskConstants.httpBaseUrliOS;
    } else {
      return BytedeskConstants.httpBaseUrl;
    }
  }

  static Uri getHostUri(
    String path, [
    Map<String, dynamic>? queryParameters,
  ]) {
    if (BytedeskConstants.isDebug) {
      return Uri.http(BytedeskConstants.host, path, queryParameters);
    } else {
      return Uri.https(BytedeskConstants.host, path, queryParameters);
    }
  }

  // 含访客登录
  static bool? isLogin() {
    return SpUtil.getBool(BytedeskConstants.isLogin);
  }

  // 验证手机号登录
  static bool? isAuthenticated() {
    return SpUtil.getBool(BytedeskConstants.isAuthenticated);
  }

  // 当前是否客服页面
  static bool? isCurrentChatKfPage() {
    return SpUtil.getBool(BytedeskConstants.isCurrentChatKfPage);
  }

  //
  static String? getAccessToken() {
    return SpUtil.getString(BytedeskConstants.accessToken);
  }

  static String? getUid() {
    return SpUtil.getString(BytedeskConstants.uid);
  }

  static String? getNickname() {
    return SpUtil.getString(BytedeskConstants.nickname);
  }

  static String? getAvatar() {
    return SpUtil.getString(BytedeskConstants.avatar);
  }

  static String? getDescription() {
    return SpUtil.getString(BytedeskConstants.description);
  }

  static bool mqttConnect() {
    var isLogin = SpUtil.getBool(BytedeskConstants.isLogin);
    if (isLogin!) {
      BytedeskMqtt().connect();
      return true;
    }
    return false;
  }

  static bool mqttReConnect() {
    bool isConnected = BytedeskMqtt().isConnected();
    if (!isConnected) {
      BytedeskMqtt().connect();
      return true;
    }
    return false;
  }

  static bool isMqttConnected() {
    return BytedeskMqtt().isConnected();
  }

  static void mqttDisconnect() {
    BytedeskMqtt().disconnect();
  }

  static double? getLatitude() {
    if (isWeb) {
      return SpUtil.getDouble(BytedeskConstants.latitude);
    } else if (isAndroid) {
      return SpUtil.getDouble(BytedeskConstants.latitude);
    } else {
      return double.parse(SpUtil.getString(BytedeskConstants.latitude)!);
    }
  }

  static double? getLongtitude() {
    if (isWeb) {
      return SpUtil.getDouble(BytedeskConstants.longtitude);
    } else if (isAndroid) {
      return SpUtil.getDouble(BytedeskConstants.longtitude);
    } else {
      return double.parse(SpUtil.getString(BytedeskConstants.longtitude)!);
    }
  }

  // static Color string2Color(String colorString) {
  //   int value = 0x00000000;

  //   if (isNotEmpty(colorString)) {
  //     if (colorString[0] == '#') {
  //       colorString = colorString.substring(1);
  //     }
  //     value = int.tryParse(colorString, radix: 16);
  //     if (value != null) {
  //       if (value < 0xFF000000) {
  //         value += 0xFF000000;
  //       }
  //     }
  //   }
  //   return Color(value);
  // }

  static String formatedDateNow() {
    var format = DateFormat('yyyy-MM-dd HH:mm:ss');
    return format.format(DateTime.now());
  }

  static String formatedTimestampNow() {
    var format = DateFormat('yyyyMMddHHmmss');
    return format.format(DateTime.now());
  }

  static Map url2query(String url) {
    var search = RegExp('([^&=]+)=?([^&]*)');
    var result = {};

    // Get rid off the beginning ? in query strings.
    if (url.startsWith('?')) url = url.substring(1);

    // A custom decoder.
    decode(String s) => Uri.decodeComponent(s.replaceAll('+', ' '));

    // Go through all the matches and build the result map.
    for (Match match in search.allMatches(url)) {
      result[decode(match.group(1)!)] = decode(match.group(2)!);
    }

    return result;
  }

  static Color randomColor() {
    return Color((Random().nextDouble() * 0xFFFFFF).toInt() << 0)
        .withOpacity(1.0);
  }

  static String removeDecimalZeroFormat(double n) {
    return n.toStringAsFixed(n.truncateToDouble() == n ? 0 : 1);
  }

  static String getDateStr(DateTime date) {
    if (date.toString() == null) {
      return "";
    } else if (date.toString().length < 10) {
      return date.toString();
    }
    return date.toString().substring(0, 10);
  }

  static int currentTimeMillis() {
    return DateTime.now().millisecondsSinceEpoch;
  }

  // 图片压缩 https://pub.dev/packages/flutter_image_compress
  // static Future<File> compressImage(File file, String targetPath) async {
  //   // debugPrint("compressImage");
  //   final result = await FlutterImageCompress.compressAndGetFile(
  //     file.absolute.path,
  //     targetPath,
  //     quality: 60,
  //     minWidth: 256,
  //     minHeight: 256,
  //     rotate: 0,
  //   );
  //   return result;
  // }

  //
  static const double MILLIS_LIMIT = 1000.0;

  static const double SECONDS_LIMIT = 60 * MILLIS_LIMIT;

  static const double MINUTES_LIMIT = 60 * SECONDS_LIMIT;

  static const double HOURS_LIMIT = 24 * MINUTES_LIMIT;

  static const double DAYS_LIMIT = 30 * HOURS_LIMIT;

  static String getNewsTimeStr(DateTime date) {
    int subTime =
        DateTime.now().millisecondsSinceEpoch - date.millisecondsSinceEpoch;

    if (subTime < MILLIS_LIMIT) {
      return "刚刚";
    } else if (subTime < SECONDS_LIMIT) {
      return "${(subTime / MILLIS_LIMIT).round()} 秒前";
    } else if (subTime < MINUTES_LIMIT) {
      return "${(subTime / SECONDS_LIMIT).round()} 分钟前";
    } else if (subTime < HOURS_LIMIT) {
      return "${(subTime / MINUTES_LIMIT).round()} 小时前";
    } else if (subTime < DAYS_LIMIT) {
      return "${(subTime / HOURS_LIMIT).round()} 天前";
    } else {
      return getDateStr(date);
    }
  }

  static String getTimeDuration(String comTime) {
    var nowTime = DateTime.now();
    var compareTime = DateTime.parse(comTime);
    if (nowTime.isAfter(compareTime)) {
      if (nowTime.year == compareTime.year) {
        if (nowTime.month == compareTime.month) {
          if (nowTime.day == compareTime.day) {
            // if (nowTime.hour == compareTime.hour) {
            //   if (nowTime.minute == compareTime.minute) {
            //
            return '${compareTime.hour}:${(compareTime.minute < 10) ? '0${compareTime.minute}' : compareTime.minute.toString()}';
            //   }
            //   return (nowTime.minute - compareTime.minute).toString() + '分钟前';
            // }
            // return (nowTime.hour - compareTime.hour).toString() + '小时前';
          }
          return '${nowTime.day - compareTime.day}天前';
        }
        return '${nowTime.month - compareTime.month}月前';
      }
      return '${nowTime.year - compareTime.year}年前';
    }
    return 'time error';
  }

  static Future<bool> requestPermission() async {
    debugPrint('请求定位');
    return true;
    // FIXME:
    // if (await Permission.location.request().isGranted) {
    //   return true;
    // } else {
    //   // decorated.toast('需要定位权限!');
    //   return false;
    // }
  }

  /// 根据两点经纬度 使用math 算出之间距离
  /// https://blog.csdn.net/weixin_38025168/article/details/107491452
  static double getDistance(
      double lat1, double lng1, double lat2, double lng2) {
    /// 单位：米
    double def = 6378137.0;
    double radLat1 = _rad(lat1);
    double radLat2 = _rad(lat2);
    double a = radLat1 - radLat2;
    double b = _rad(lng1) - _rad(lng2);
    double s = 2 *
        asin(sqrt(pow(sin(a / 2), 2) +
            cos(radLat1) * cos(radLat2) * pow(sin(b / 2), 2)));
    return (s * def).roundToDouble() / 1000; // 返回千米
  }

  static double _rad(double d) {
    return d * pi / 180.0;
  }

  // static Color textColor(BuildContext context) {
  //   return ThemeUtils.isDark(context) ? Colours.dark_text : Colours.text;
  // }

  //销量转换
  static String calculateSales(int sales) {
    String prefix = '已拼';
    if (sales >= 10000 && sales < 100000) {
      return '$prefix${(sales / 10000).toStringAsFixed(1)}万件';
    } else if (sales >= 100000) {
      return '${prefix}10万+件';
    }
    return '$prefix$sales件';
  }

  //价格转换
  static String convertPrice(int price) {
    String result = (price / 100).toString();
    //如果小数点后只有1位,且为 0
    if (result.endsWith('.0')) {
      return result.substring(0, result.lastIndexOf('.'));
    }
    return result;
  }

  /// 保存图片到相册
  /// 默认为下载网络图片，如需下载资源图片，需要指定 [isAsset] 为 `true`。
  static Future<void> saveImage(String imageUrl, {bool isAsset = false}) async {
    try {
      // if (imageUrl == null) throw '保存失败，图片不存在！';

      /// 权限检测
      // PermissionStatus storageStatus = await Permission.storage.status;
      // if (storageStatus != PermissionStatus.granted) {
      //   storageStatus = await Permission.storage.request();
      //   if (storageStatus != PermissionStatus.granted) {
      //     throw '无法存储图片，请先授权！';
      //   }
      // }

      /// 保存的图片数据
      Uint8List imageBytes;

      if (isAsset == true) {
        /// 保存资源图片
        ByteData bytes = await rootBundle.load(imageUrl);
        imageBytes = bytes.buffer.asUint8List();
      } else {
        /// 保存网络图片
        CachedNetworkImage image = CachedNetworkImage(imageUrl: imageUrl);
        DefaultCacheManager manager = DefaultCacheManager();
        Map<String, String>? headers = image.httpHeaders;
        File file = await manager.getSingleFile(
          image.imageUrl,
          headers: headers,
        );
        imageBytes = await file.readAsBytes();
      }

      /// 保存图片
      final result = await ImageGallerySaver.saveImage(imageBytes);
      if (result == null || result == '') throw '图片保存失败';
      // 提示
      Fluttertoast.showToast(msg: "保存成功");
      debugPrint("保存成功");
    } catch (e) {
      BytedeskUtils.printLog(e.toString());
    }
  }

  // 退出登录
  static void exitLogin(BuildContext context) {
    // 友盟统计：取消用户账号
    // UmengCommonSdk.onProfileSignOff();
    // 清空本地
    // SpUtil.putBool(BytedeskConstants.isLogin, false);
    // SpUtil.clear();
    // NavigatorUtils.push(context, LoginRouter.loginPage, clearStack: true);
  }

  // 清空本地缓存
  static void clearUserCache() {
    //
    SpUtil.putString(BytedeskConstants.uid, '');
    SpUtil.putString(BytedeskConstants.username, '');
    SpUtil.putString(BytedeskConstants.nickname, '');
    SpUtil.putString(BytedeskConstants.avatar, '');
    SpUtil.putString(BytedeskConstants.description, '');
    SpUtil.putString(BytedeskConstants.subDomain, '');
    SpUtil.putString(BytedeskConstants.role, '');
    //
    SpUtil.putString(BytedeskConstants.unionid, '');
    SpUtil.putString(BytedeskConstants.openid, '');
    //
    SpUtil.putBool(BytedeskConstants.isLogin, false);
    SpUtil.putBool(BytedeskConstants.isAuthenticated, false);
    SpUtil.putString(BytedeskConstants.mobile, '');
    SpUtil.putString(BytedeskConstants.accessToken, '');
  }
}
