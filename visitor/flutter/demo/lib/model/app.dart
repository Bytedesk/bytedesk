class App {
  // 唯一数字id，保证唯一性
  String? aid;
  // 名称
  String? name;
  //
  int? versionCode;
  // 版本
  String? version;
  // 头像
  String? avatar;
  // 网站url，或者app下载url
  String? url;
  // key为关键字
  // String? key;
  // 类型：网站、App
  // String? type;
  // android、ios 或者 both
  // String? platform;
  // 上线、测试、新版本
  String? status;
  // 升级新版tip
  String? tip;

  /// 是否强制升级
  bool? forceUpgrade;
  // 描述，介绍
  // String? description;

  //
  App(
      {this.aid,
      this.name,
      this.versionCode,
      this.version,
      this.avatar,
      this.url,
      this.status,
      this.tip,
      this.forceUpgrade})
      : super();

  //
  static App fromJson(dynamic app) {
    return App(
        aid: app['aid'],
        name: app['name'],
        versionCode: app['versionCode'],
        version: app['version'],
        avatar: app['avatar'],
        url: app['url'],
        status: app['status'],
        tip: app['tip'],
        forceUpgrade: app['forceUpgrade']);
  }
}
