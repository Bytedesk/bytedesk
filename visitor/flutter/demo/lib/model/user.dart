import 'package:equatable/equatable.dart';

class User extends Equatable {
  final String? uid;
  final String? username;
  final String? nickname;
  final String? avatar;
  final String? mobile;
  final String? description;
  final bool? sex;
  final String? location;
  final String? birthday;
  final String? subDomain;

  const User(
      {this.uid,
      this.username,
      this.nickname,
      this.avatar,
      this.mobile,
      this.description,
      this.sex,
      this.location,
      this.birthday,
      this.subDomain});

  @override
  List<Object> get props => [uid!];

  static User fromJson(dynamic json) {
    return User(
        uid: json['uid'],
        username: json['username'],
        nickname: json['nickname'],
        avatar: json['avatar'],
        mobile: json['mobile'],
        description: json['description'],
        sex: json['sex'],
        location: json['location'],
        birthday: json['birthday'],
        subDomain: json['subDomain']);
  }

  static User fromInvalidToken() {
    return const User(
        uid: 'invalid_token',
        username: '',
        nickname: '',
        avatar: '',
        mobile: '',
        description: '',
        sex: false,
        location: '',
        birthday: '',
        subDomain: '');
  }

  // static User fromProperties(String? uid, String? nickname, String? avatar) {
  //   return User()
  // }
}
