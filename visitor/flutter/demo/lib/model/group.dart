import 'package:equatable/equatable.dart';

class Group extends Equatable {
  final String? gid;
  final String? username;
  final String? nickname;

  const Group({this.gid, this.username, this.nickname}) : super();

  static Group fromJson(dynamic json) {
    return Group(
        gid: json['gid'],
        username: json['username'],
        nickname: json['nickname']);
  }

  @override
  List<Object> get props => [];
}
