// ignore_for_file: file_names

import 'package:equatable/equatable.dart';

class WorkGroup extends Equatable {
  final String? wid;
  final String? nickname;

  const WorkGroup({this.wid, this.nickname}) : super();

  static WorkGroup fromJson(dynamic json) {
    return WorkGroup(wid: json['wid'], nickname: json['nickname']);
  }

  @override
  List<Object> get props => [];
}
