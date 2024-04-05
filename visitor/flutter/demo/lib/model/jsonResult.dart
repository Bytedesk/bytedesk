// ignore_for_file: file_names

import 'package:equatable/equatable.dart';

class JsonResult extends Equatable {
  final String? message;
  final int? statusCode;
  final String? data;

  const JsonResult({this.message, this.statusCode, this.data}) : super();

  static JsonResult fromJson(dynamic json) {
    return JsonResult(
        message: json["message"], 
        statusCode: json["status_code"], 
        data: json["data"]
      );
  }

  @override
  List<Object> get props => [message!, statusCode!];
}
