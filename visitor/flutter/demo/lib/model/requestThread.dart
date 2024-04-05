// ignore_for_file: file_names

import 'package:equatable/equatable.dart';
import 'message.dart';

class RequestThreadResult extends Equatable {
  final String? message;
  final int? statusCode;
  final Message? msg;

  const RequestThreadResult({this.message, this.statusCode, this.msg})
      : super();

  static RequestThreadResult fromJson(dynamic json) {
    return RequestThreadResult(
        message: json["message"],
        statusCode: json["status_code"],
        msg: Message.fromJsonThread(json["data"]));
  }

  static RequestThreadResult fromJsonV2(dynamic json) {
    return RequestThreadResult(
        message: json["message"],
        statusCode: json["status_code"],
        msg: Message.fromJsonThreadWorkGroupV2(json["data"]));
  }

  @override
  List<Object> get props => [msg!.mid!];
}
