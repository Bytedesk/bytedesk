// ignore_for_file: file_names

import 'package:bytedesk_kefu/model/model.dart';
import 'package:equatable/equatable.dart';

class FeedbackJsonResult extends Equatable {
  final String? message;
  final int? statusCode;
  final BFeedback? feedback;

  const FeedbackJsonResult({this.message, this.statusCode, this.feedback}) : super();

  static FeedbackJsonResult fromJson(dynamic json) {
    return FeedbackJsonResult(
        message: json["message"], 
        statusCode: json["status_code"], 
        feedback: BFeedback.fromJson(json["data"])
      );
  }

  @override
  List<Object> get props => [message!, statusCode!];
}
