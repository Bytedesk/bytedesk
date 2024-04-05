// ignore_for_file: file_names

import 'package:bytedesk_kefu/model/wechatId.dart';
import 'package:bytedesk_kefu/model/wechatUserinfo.dart';
import 'package:equatable/equatable.dart';

class WeChatResult extends Equatable {
  //
  final String? message;
  final int? statusCode;
  final WechatId? wechatId;
  final WechatUserinfo? wechatUserinfo;

  const WeChatResult(
      {this.message, this.statusCode, this.wechatId, this.wechatUserinfo})
      : super();

  static WeChatResult fromJson(dynamic json) {
    return WeChatResult(
        message: json["message"],
        statusCode: json["status_code"],
        wechatId: json["status_code"] == 200
            ? WechatId.fromJson(json['data'])
            : null, // 此微信已经绑定
        wechatUserinfo: json["status_code"] == 201
            ? WechatUserinfo.fromJson(json['data'])
            : null // 此微信首次登录
        );
  }

  @override
  List<Object> get props => [wechatUserinfo!.openid!];
}
