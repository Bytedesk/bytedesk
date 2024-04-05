// ignore_for_file: file_names

import 'package:bytedesk_kefu/model/messageZhipuAI.dart';
import 'package:equatable/equatable.dart';

class RequestThreadZhipuAIResult extends Equatable {
  final String? message;
  final int? statusCode;
  final MessageZhipuAI? msg;

  const RequestThreadZhipuAIResult({this.message, this.statusCode, this.msg})
      : super();

  static RequestThreadZhipuAIResult fromJson(dynamic json) {
    return RequestThreadZhipuAIResult(
        message: json["message"],
        statusCode: json["status_code"],
        msg: MessageZhipuAI.fromJsonThread(json["data"]));
  }

  @override
  List<Object> get props => [msg!.mid!];
}
