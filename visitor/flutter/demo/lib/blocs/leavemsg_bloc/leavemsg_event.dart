import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class LeaveMsgEvent extends Equatable {
  const LeaveMsgEvent();

  @override
  List<Object> get props => [];
}

class GetLeaveMsgCategoryEvent extends LeaveMsgEvent {
  final String? uid;
  const GetLeaveMsgCategoryEvent({@required this.uid}) : super();
}

class SubmitLeaveMsgEvent extends LeaveMsgEvent {
  final List<String>? imageUrls;
  final String? wid;
  final String? aid;
  final String? type;
  final String? mobile;
  final String? email;
  final String? content;
  // @required this.imageUrls
  const SubmitLeaveMsgEvent({@required this.wid, @required this.aid, @required this.type, @required this.mobile, @required this.email, @required this.content, @required this.imageUrls})
      : super();
}

class UploadImageEvent extends LeaveMsgEvent {
  final String? filePath;

  const UploadImageEvent({@required this.filePath}) : super();
}

class UploadImageBytesEvent extends LeaveMsgEvent {
  final String? fileName;
  final List<int>? fileBytes;
  final String? mimeType;

  const UploadImageBytesEvent(
      {@required this.fileName,
      @required this.fileBytes,
      @required this.mimeType})
      : super();
}
