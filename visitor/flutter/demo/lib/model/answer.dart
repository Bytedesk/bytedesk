import 'package:equatable/equatable.dart';

class Answer extends Equatable {
  //
  final String? aid;
  final String? question;
  final String? answer;
  //
  const Answer({this.aid, this.question, this.answer}) : super();
  //
  static Answer fromJson(dynamic json) {
    // debugPrint('aid:' + json['aid']);
    // debugPrint('question:' + json['question']);
    return Answer(
        aid: json['aid'], question: json['question'], answer: json['answer']);
  }

  //
  @override
  List<Object> get props => [aid!];
}
