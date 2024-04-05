// ignore_for_file: file_names

import 'package:bytedesk_kefu/model/user.dart';
import 'package:equatable/equatable.dart';

class UserJsonResult extends Equatable {
  //
  final String? message;
  final int? statusCode;
  final User? user;

  const UserJsonResult({this.message, this.statusCode, this.user}) : super();

  static UserJsonResult fromJson(dynamic json) {
    return UserJsonResult(
        message: json["message"],
        statusCode: json["status_code"],
        user: json["status_code"] == 200 ? User.fromJson(json['data']) : null);
  }

  @override
  List<Object> get props => [user!.uid!];
}
