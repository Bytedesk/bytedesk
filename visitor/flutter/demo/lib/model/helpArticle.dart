// ignore_for_file: file_names

import 'package:equatable/equatable.dart';

class HelpArticle extends Equatable {
  final int? id;
  final String? aid;
  final String? title;
  final String? type;
  final String? content;

  const HelpArticle({this.id, this.aid, this.title, this.type, this.content})
      : super();

  static HelpArticle fromJson(dynamic json) {
    return HelpArticle(
        id: json['id'],
        aid: json['aid'],
        title: json['title'],
        type: json['type'],
        content: json['content']);
  }

  @override
  List<Object> get props => [aid!];
}
