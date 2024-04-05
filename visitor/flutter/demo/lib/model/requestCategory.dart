// ignore_for_file: file_names

import 'package:equatable/equatable.dart';
import 'message.dart';

class RequestCategoryResult extends Equatable {
  final String? message;
  final int? statusCode;
  //
  final Message? query;
  final Message? anwser;

  const RequestCategoryResult({this.message, this.statusCode, this.query, this.anwser})
      : super();

  static RequestCategoryResult fromJson(dynamic json) {
    return RequestCategoryResult(
        message: json["message"],
        statusCode: json["status_code"],
        query: Message.fromJsonRobotQuery(json["data"]["query"]),
        anwser: Message.fromJsonRobotQuery(json["data"]["reply"]));
  }

  @override
  List<Object> get props => [statusCode!];
}
