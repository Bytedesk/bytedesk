import 'package:equatable/equatable.dart';

class Category extends Equatable {
  //
  final int? id;
  final String? cid;
  final String? name;
  final String? slug;
  //
  const Category({this.id, this.cid, this.name, this.slug}) : super();
  //
  static Category fromJson(dynamic json) {
    return Category(id: json['id'], cid: json['cid'], name: json['name']);
  }
  //
  static Category init() {
    return const Category(id: 0, cid: '', name: '', slug: '');
  }
  //
  @override
  List<Object> get props => [cid!];
}
