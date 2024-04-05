// ignore_for_file: file_names

import 'package:equatable/equatable.dart';

// {"access_token":"32_Lzn24923t_quQsCfKy7sl1kwbprKGI2kEYcvlBC5pANPOuiqLqF1S7L3Oeuj5nzP2CP4oSeNMlDmzDqEJsfpplRXku9CetoWx-MNaR1Pgxs",
// "expires_in":7200,
// "refresh_token":"32_r1s5XqFYRo0UTjcpPy5kS2pHvKV3KhQimK4v7aXhkO0_Vx94dGRS2vKrZXVtBtLy0fZkg_pUQQ7cIDZaYeSxJogoWoaXzDkyt9SxPXRI3nc",
// "openid":"oFyip1EJwsOQjXpsYWddVQh-LReM",
// "scope":"snsapi_login",
// "unionid":"os9j41CZ6hPITe_P9rIXwsLuR0JM"}
class WechatId extends Equatable {
  //
  final String? accessToken;
  final int? expiresIn;
  final String? refreshToken;
  final String? openid;
  final String? scope;
  final String? unionid;

  const WechatId(
      {this.accessToken,
      this.expiresIn,
      this.refreshToken,
      this.openid,
      this.scope,
      this.unionid})
      : super();

  static WechatId fromJson(dynamic json) {
    return WechatId(
        accessToken: json['access_token'],
        expiresIn: json['expires_in'],
        refreshToken: json['refresh_token'],
        openid: json['openid'],
        scope: json['scope'],
        unionid: json['unionid']);
  }

  @override
  List<Object> get props => [unionid!];
}
