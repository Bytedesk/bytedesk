// ignore_for_file: file_names

import 'package:bytedesk_kefu/model/thread.dart';
import 'package:equatable/equatable.dart';

class RequestThreadFileHelperResult extends Equatable {
  final String? message;
  final int? statusCode;
  //
  final Thread? thread;

  const RequestThreadFileHelperResult({this.message, this.statusCode, this.thread})
      : super();

  static RequestThreadFileHelperResult fromJson(dynamic json) {
    return RequestThreadFileHelperResult(
        message: json["message"],
        statusCode: json["status_code"],
        thread: Thread.fromFileHelperJson(json["data"]));
  }

  @override
  List<Object> get props => [thread!.tid!];
}
