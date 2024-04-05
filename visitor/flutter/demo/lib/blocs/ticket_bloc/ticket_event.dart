import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class TicketEvent extends Equatable {
  const TicketEvent();

  @override
  List<Object> get props => [];
}

class GetTicketCategoryEvent extends TicketEvent {}

class SubmitTicketEvent extends TicketEvent {
  final List<String>? imageUrls;
  final String? content;

  const SubmitTicketEvent({@required this.content, @required this.imageUrls})
      : super();
}

class UploadImageEvent extends TicketEvent {
  final String? filePath;

  const UploadImageEvent({@required this.filePath}) : super();
}

class UploadImageBytesEvent extends TicketEvent {
  final String? fileName;
  final List<int>? fileBytes;
  final String? mimeType;

  const UploadImageBytesEvent(
      {@required this.fileName,
      @required this.fileBytes,
      @required this.mimeType})
      : super();
}
