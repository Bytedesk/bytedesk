// ignore_for_file: file_names

import 'package:equatable/equatable.dart';

class CodeResult extends Equatable {
  //
  final String? message;
  final int? statusCode;
  final bool? exist;
  final String? code;

  const CodeResult({this.message, this.statusCode, this.exist, this.code}) : super();

  static CodeResult fromJson(dynamic json) {
    return CodeResult(
        message: json["message"],
        statusCode: json["status_code"],
        exist: json["data"]["exist"],
        code: json["data"]["code"]);
  }

  @override
  List<Object> get props =>
      [message!, statusCode!, exist!, code!];
}
