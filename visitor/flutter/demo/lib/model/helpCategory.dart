// ignore_for_file: file_names

import 'package:equatable/equatable.dart';

class HelpCategory extends Equatable {
  final int? id;
  final String? cid;
  final String? name;
  final String? type;

  const HelpCategory({this.id, this.cid, this.name, this.type}) : super();

  static HelpCategory fromJson(dynamic json) {
    return HelpCategory(
        id: json['id'],
        cid: json['cid'],
        name: json['name'],
        type: json['type']);
  }

  @override
  List<Object> get props => [cid!];
}
