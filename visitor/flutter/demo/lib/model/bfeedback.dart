import 'package:equatable/equatable.dart';

class BFeedback extends Equatable {
  //
  final String? fid;
  final String? mobile;
  final String? content;
  final String? replyContent;
  final List<String>? imageUrls;
  //
  const BFeedback({this.fid, this.mobile, this.content, this.replyContent, this.imageUrls})
      : super();
  // imageUrls: json['images']
  static BFeedback fromJson(dynamic json) {
    return BFeedback(
      fid: json['fid'],
      mobile: json['mobile'],
      content: json['content'],
      replyContent: json['replyContent']
    );
  }

  //
  @override
  List<Object> get props => [fid!];
}
