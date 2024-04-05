import 'package:bytedesk_kefu/model/message.dart';
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class MessageEvent extends Equatable {
  const MessageEvent();

  @override
  List<Object> get props => [];
}

class ReceiveMessageEvent extends MessageEvent {
  final Message? message;

  const ReceiveMessageEvent({@required this.message}) : super();
}

class UploadImageEvent extends MessageEvent {
  final String? filePath;

  const UploadImageEvent({@required this.filePath}) : super();
}

class UploadImageBytesEvent extends MessageEvent {
  final String? fileName;
  final List<int>? fileBytes;
  final String? mimeType;

  const UploadImageBytesEvent(
      {@required this.fileName, @required this.fileBytes, @required this.mimeType}) : super();
}

class UploadVideoEvent extends MessageEvent {
  final String? filePath;

  const UploadVideoEvent({@required this.filePath}) : super();
}

class UploadVideoBytesEvent extends MessageEvent {
  final String? fileName;
  final List<int>? fileBytes;
  final String? mimeType;

  const UploadVideoBytesEvent(
      {@required this.fileName,
      @required this.fileBytes,
      @required this.mimeType})
      : super();
}

class SendMessageRestEvent extends MessageEvent {
  final String? json;

  const SendMessageRestEvent({@required this.json}) : super();
}

class SendZhipuAIMessageRestEvent extends MessageEvent {
  final String? json;

  const SendZhipuAIMessageRestEvent({@required this.json}) : super();
}

class LoadHistoryMessageEvent extends MessageEvent {
  final String? uid;
  final int? page;
  final int? size;

  const LoadHistoryMessageEvent(
      {@required this.uid, @required this.page, @required this.size})
      : super();
}

class LoadTopicMessageEvent extends MessageEvent {
  final String? topic;
  final int? page;
  final int? size;

  const LoadTopicMessageEvent(
      {@required this.topic, @required this.page, @required this.size})
      : super();
}

class LoadMessageFileHelperEvent extends MessageEvent {
  final String? topic;
  final int? page;
  final int? size;

  const LoadMessageFileHelperEvent(
      {@required this.topic, @required this.page, @required this.size})
      : super();
}

class LoadMessageZhipuAIEvent extends MessageEvent {
  final String? tid;
  final int? page;
  final int? size;

  const LoadMessageZhipuAIEvent(
      {@required this.tid, @required this.page, @required this.size})
      : super();
}

class LoadChannelMessageEvent extends MessageEvent {
  final String? cid;
  final int? page;
  final int? size;

  const LoadChannelMessageEvent(
      {@required this.cid, @required this.page, @required this.size})
      : super();
}

class LoadUnreadMessagesEvent extends MessageEvent {
  final String? wid;
  final int? page;
  final int? size;

  const LoadUnreadMessagesEvent(
      {@required this.wid, @required this.page, @required this.size})
      : super();
}

class LoadUnreadVisitorMessagesEvent extends MessageEvent {
  final int? page;
  final int? size;

  const LoadUnreadVisitorMessagesEvent(
      {@required this.page, @required this.size})
      : super();
}

class LoadUnreadAgentMessagesEvent extends MessageEvent {
  final int? page;
  final int? size;

  const LoadUnreadAgentMessagesEvent({@required this.page, @required this.size})
      : super();
}

class QueryAnswerEvent extends MessageEvent {
  final String? tid;
  final String? aid;
  final String? mid;

  const QueryAnswerEvent(
      {@required this.tid, @required this.aid, @required this.mid})
      : super();
}

class QueryCategoryEvent extends MessageEvent {
  final String? tid;
  final String? cid;

  const QueryCategoryEvent({@required this.tid, @required this.cid}) : super();
}

class MessageAnswerEvent extends MessageEvent {
  // final String? type;
  final String? wid;
  // final String? aid;
  final String? content;

  const MessageAnswerEvent(
      {
      // @required this.type,
      @required this.wid,
      // @required this.aid,
      @required this.content})
      : super();
}

class RateAnswerEvent extends MessageEvent {
  final String? aid;
  final String? mid;
  final bool? rate;

  const RateAnswerEvent(
      {@required this.aid, @required this.mid, @required this.rate})
      : super();
}
