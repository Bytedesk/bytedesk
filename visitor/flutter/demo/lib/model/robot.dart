import 'package:equatable/equatable.dart';

class Robot extends Equatable {
  //
  final String? rid;
  final String? nickname;
  final String? avatar;
  final String? type;
  final String? description;
  final String? promot;
  final String? tip;
  final double? temperature;
  final double? topP;
  //
  const Robot({this.rid, this.nickname, this.avatar, this.type, this.description, this.promot, this.tip, this.temperature, this.topP}) : super();
  //
  static Robot fromJson(dynamic json) {
    return Robot(
        rid: json['rid'], 
        nickname: json['nickname'], 
        avatar: json['avatar'],
        type: json['type'],
        description: json['description'],
        promot: json['promot'],
        tip: json['tip'],
        temperature: json['temperature'],
        topP: json['topP']
    );
  }

  //
  @override
  List<Object> get props => [rid!];
}
