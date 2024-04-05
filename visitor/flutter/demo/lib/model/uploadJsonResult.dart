// ignore_for_file: file_names

import 'package:equatable/equatable.dart';

class UploadJsonResult extends Equatable {
  final String? message;
  final int? statusCode;
  final String? url;

  const UploadJsonResult({this.message, this.statusCode, this.url}) : super();

  static UploadJsonResult fromJson(dynamic json) {
    return UploadJsonResult(
        message: json["message"],
        statusCode: json["status_code"],
        url: json['data']);
  }

  @override
  List<Object> get props => [url!];
}
