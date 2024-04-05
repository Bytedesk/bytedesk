// import 'package:equatable/equatable.dart';
// import 'package:azlistview/azlistview.dart';

class Friend {
  // extends ISuspensionBean

  final String? uid;
  final String? username;
  final String? nickname;
  final String? avatar;
  final String? description;
  final String? mobile;
  final double? latitude;
  final double? longtitude;
  String? tagIndex;
  String? namePinyin;

  Friend(
      {this.uid,
      this.username,
      this.nickname,
      this.avatar,
      this.description,
      this.mobile,
      this.latitude,
      this.longtitude,
      this.tagIndex,
      this.namePinyin});

  // @override
  // List<Object> get props => [uid, username, nickname, avatar, description, tagIndex, namePinyin];

  static Friend fromJson(dynamic json) {
    return Friend(
        uid: json['uid'],
        username: json['username'],
        nickname: json['nickname'],
        avatar: json['avatar'],
        description: json['description'],
        mobile: json['mobile']);
  }

  // TODO: 未显示距离
  static Friend fromElasticJson(dynamic json) {
    return Friend(
        uid: json['uid'],
        username: json['username'],
        nickname: json['nickname'],
        avatar: json['avatar'],
        description: json['description'],
        mobile: json['mobile'],
        latitude: json['location']['lat'],
        longtitude: json['location']['lon']);
  }

  // @override
  // String? getSuspensionTag() => tagIndex;

}
