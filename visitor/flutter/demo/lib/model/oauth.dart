import 'package:equatable/equatable.dart';

class OAuth extends Equatable {
  final int? statusCode;
  final String? accessToken;
  final int? expiresIn;
  final String? jti;
  final String? refreshToken;
  final String? scope;
  final String? tokenType;

  const OAuth(
      {this.statusCode,
      this.accessToken,
      this.expiresIn,
      this.jti,
      this.refreshToken,
      this.scope,
      this.tokenType})
      : super();

  static OAuth fromJson(int? statusCode, dynamic json) {
    return OAuth(
        statusCode: statusCode,
        accessToken: json['access_token'],
        expiresIn: json['expires_in'],
        jti: json['jti'],
        refreshToken: json['refresh_token'],
        scope: json['scope'],
        tokenType: json['token_type']);
  }

  @override
  List<Object> get props => [accessToken!];
}
