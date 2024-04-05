import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class FeedbackEvent extends Equatable {
  const FeedbackEvent();

  @override
  List<Object> get props => [];
}

class GetFeedbackCategoryEvent extends FeedbackEvent {
  final String? uid;
  const GetFeedbackCategoryEvent({@required this.uid}) : super();
}

class GetMyFeedbackEvent extends FeedbackEvent {
  const GetMyFeedbackEvent() : super();
}

class SubmitFeedbackEvent extends FeedbackEvent {
  final List<String>? imageUrls;
  final String? content;
  final String? cid;
  final String? mobile;

  const SubmitFeedbackEvent({@required this.cid,
      @required this.mobile,
      @required this.content, @required this.imageUrls})
      : super();
}

class UploadImageEvent extends FeedbackEvent {
  final String? filePath;

  const UploadImageEvent({@required this.filePath}) : super();
}

class UploadImageBytesEvent extends FeedbackEvent {
  final String? fileName;
  final List<int>? fileBytes;
  final String? mimeType;

  const UploadImageBytesEvent(
      {@required this.fileName,
      @required this.fileBytes,
      @required this.mimeType})
      : super();
}
