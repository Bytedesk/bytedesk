import 'package:equatable/equatable.dart';

class Queue extends Equatable {
  final String? qid;
  final String? nickname;
  final String? avatar;

  const Queue({this.qid, this.nickname, this.avatar}) : super();

  static Queue fromJson(dynamic json) {
    return Queue(
        qid: json['qid'], nickname: json['nickname'], avatar: json['avatar']);
  }

  @override
  List<Object> get props => [];
}
