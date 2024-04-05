import 'dart:convert';
// import 'dart:io';

import 'package:bytedesk_kefu/http/bytedesk_base_api.dart';
// import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
// import 'package:device_info/device_info.dart';
// import 'package:devicelocale/devicelocale.dart';
import 'package:package_info/package_info.dart';
// import 'package:package_info_plus/package_info_plus.dart';

class BytedeskDeviceHttpApi extends BytedeskBaseHttpApi {
  //
  // static final DeviceInfoPlugin deviceInfoPlugin = DeviceInfoPlugin();
  //
  Future<void> setDeviceInfo() async {
    //
    if (BytedeskUtils.isWeb) {
      //
    } else if (BytedeskUtils.isAndroid) {
      updateAndroidDeviceInfo();
    } else if (BytedeskUtils.isIOS) {
      updateIOSDeviceInfo();
    } else {
      // TODO: web/windows/mac
    }
  }

  // APP当前版本
  // The package version. `CFBundleShortVersionString` on iOS, `versionName` on Android.
  Future<String> getAppVersion() async {
    PackageInfo packageInfo = await PackageInfo.fromPlatform();
    return packageInfo.version;
  }

  // The build number. `CFBundleVersion` on iOS, `versionCode` on Android.
  Future<String> getAppBuildNumber() async {
    PackageInfo packageInfo = await PackageInfo.fromPlatform();
    return packageInfo.buildNumber;
  }

  // 上传安卓设备信息
  Future<void> updateAndroidDeviceInfo() async {
    //
    // Map<String, dynamic> deviceData =
    //     _readAndroidBuildData(await deviceInfoPlugin.androidInfo);
    // //
    // String? locale = ""; //await Devicelocale.currentLocale;
    // PackageInfo packageInfo = await PackageInfo.fromPlatform();
    // var body = json.encode({
    //   "sdkVersion": deviceData['version.sdkInt'],
    //   "osVersion": deviceData['version.release'],
    //   "deviceModel": deviceData['model'],
    //   "brand": deviceData['brand'],
    //   "language": locale,
    //   "appVersion": packageInfo.version,
    //   "appVersionName": packageInfo.buildNumber,
    //   "client": client
    // });
    // //
    // // final initUrl = '$baseUrl/api/fingerprint2/android/deviceInfo';
    // final initUrl = Uri.http(
    //     BytedeskConstants.host, '/api/fingerprint2/android/deviceInfo');
    // await this.httpClient.post(initUrl, headers: getHeaders(), body: body);
  }

  // 上传苹果设备信息
  Future<void> updateIOSDeviceInfo() async {
    //
    // Map<String, dynamic> deviceData =
    //     _readIosDeviceInfo(await deviceInfoPlugin.iosInfo);
    // //
    // String? locale = "";
    // //await Devicelocale.currentLocale;
    // // Locale locale2 = await Devicelocale.currentAsLocale;
    // PackageInfo packageInfo = await PackageInfo.fromPlatform();
    // var body = json.encode({
    //   "os": deviceData['systemName'],
    //   "osVersion": deviceData['systemVersion'],
    //   "deviceName": deviceData['name'],
    //   "deviceModel": deviceData['utsname.machine'],
    //   "appName": packageInfo.appName,
    //   "appVersion": packageInfo.version,
    //   "language": locale,
    //   "appCountry": deviceData[''], // TODO: 获取国家
    //   "client": client
    // });
    // //
    // // final initUrl = '$baseUrl/api/fingerprint2/ios/deviceInfo';
    // final initUrl =
    //     BytedeskUtils.getHostUri('/api/fingerprint2/ios/deviceInfo');
    // await this.httpClient.post(initUrl, headers: getHeaders(), body: body);
  }

  // 上传苹果deviceToken
  Future<void> updateIOSDeviceToken(
      String appkey, String build, String deviceToken) async {
    //
    var body = json.encode({
      "appkey": appkey,
      "build": build,
      "token": deviceToken,
      "client": client
    });
    //
    // final initUrl = '$baseUrl/api/push/update/token';
    final initUrl = BytedeskUtils.getHostUri('/api/push/update/token');
    await httpClient.post(initUrl, headers: getHeaders(), body: body);
  }

  // 删除设备信息
  Future<void> deleteIOSDeviceToken(String build) async {
    //
    var body = json.encode({"build": build, "client": client});
    //
    // final initUrl = '$baseUrl/api/push/delete/token';
    final initUrl = BytedeskUtils.getHostUri('/api/push/delete/token');
    await httpClient.post(initUrl, headers: getHeaders(), body: body);
  }

  // Map<String, dynamic> _readAndroidBuildData(AndroidDeviceInfo build) {
  //   return <String, dynamic>{
  //     'version.securityPatch': build.version.securityPatch,
  //     'version.sdkInt': build.version.sdkInt,
  //     'version.release': build.version.release,
  //     'version.previewSdkInt': build.version.previewSdkInt,
  //     'version.incremental': build.version.incremental,
  //     'version.codename': build.version.codename,
  //     'version.baseOS': build.version.baseOS,
  //     'board': build.board,
  //     'bootloader': build.bootloader,
  //     'brand': build.brand,
  //     'device': build.device,
  //     'display': build.display,
  //     'fingerprint': build.fingerprint,
  //     'hardware': build.hardware,
  //     'host': build.host,
  //     'id': build.id,
  //     'manufacturer': build.manufacturer,
  //     'model': build.model,
  //     'product': build.product,
  //     'supported32BitAbis': build.supported32BitAbis,
  //     'supported64BitAbis': build.supported64BitAbis,
  //     'supportedAbis': build.supportedAbis,
  //     'tags': build.tags,
  //     'type': build.type,
  //     'isPhysicalDevice': build.isPhysicalDevice,
  //     'androidId': build.androidId,
  //     'systemFeatures': build.systemFeatures,
  //   };
  // }

  // Map<String, dynamic> _readIosDeviceInfo(IosDeviceInfo data) {
  //   return <String, dynamic>{
  //     'name': data.name,
  //     'systemName': data.systemName,
  //     'systemVersion': data.systemVersion,
  //     'model': data.model,
  //     'localizedModel': data.localizedModel,
  //     'identifierForVendor': data.identifierForVendor,
  //     'isPhysicalDevice': data.isPhysicalDevice,
  //     'utsname.sysname:': data.utsname.sysname,
  //     'utsname.nodename:': data.utsname.nodename,
  //     'utsname.release:': data.utsname.release,
  //     'utsname.version:': data.utsname.version,
  //     'utsname.machine:': data.utsname.machine,
  //   };
  // }
}
