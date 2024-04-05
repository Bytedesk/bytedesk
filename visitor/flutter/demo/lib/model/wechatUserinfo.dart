// ignore_for_file: file_names

import 'package:equatable/equatable.dart';

// {"openid":"oFyip1EJwsOQjXpsYWddVQh-LReM",
// "nickname":"宁金鹏",
// "sex":1,
// "language":"zh_CN",
// "city":"Haidian",
// "province":"Beijing",
// "country":"CN",
// "headimgurl":"http:\/\/thirdwx.qlogo.cn\/mmopen\/vi_32\/DYAIOgq83erzHNgbAaT1qkWe2lUicBdAmqrSmOceA6eD2RFSUEV546ibt7SgHiaew3IxLGWFjzm8icB4wNXe5sCzBA\/132",
// "privilege":[],
// "unionid":"os9j41CZ6hPITe_P9rIXwsLuR0JM"}
class WechatUserinfo extends Equatable {
  //
  final String? openid;
  final String? nickname;
  final int? sex;
  final String? language;
  final String? city;
  final String? province;
  final String? country;
  final String? headimgurl;
  final String? unionid;

  const WechatUserinfo(
      {this.openid,
      this.nickname,
      this.sex,
      this.language,
      this.city,
      this.province,
      this.country,
      this.headimgurl,
      this.unionid})
      : super();

  static WechatUserinfo fromJson(dynamic json) {
    return WechatUserinfo(
        openid: json['openid'],
        nickname: json['nickname'],
        sex: json['sex'],
        language: json['language'],
        city: json['city'],
        province: json['province'],
        country: json['country'],
        headimgurl: json['headimgurl'],
        unionid: json['unionid']);
  }

  @override
  List<Object> get props => [unionid!];
}
