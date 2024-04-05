// import 'package:equatable/equatable.dart';
// import 'package:azlistview/azlistview.dart';

class Contact {
  // extends ISuspensionBean

  final String? uid;
  final String? username;
  final String? nickname;
  final String? avatar;
  final String? description;
  String? tagIndex;
  String? namePinyin;

  Contact(
      {this.uid,
      this.username,
      this.nickname,
      this.avatar,
      this.description,
      this.tagIndex,
      this.namePinyin});

  // @override
  // List<Object> get props => [uid, username, nickname, avatar, description, tagIndex, namePinyin];

  static Contact fromJson(dynamic json) {
    return Contact(
        uid: json['uid'],
        username: json['username'],
        nickname: json['nickname'],
        avatar: json['avatar'],
        description: json['description']);
  }

  // @override
  // String? getSuspensionTag() => tagIndex;

}
