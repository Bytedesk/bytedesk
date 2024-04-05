import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:bytedesk_kefu/model/message.dart';
import 'package:bytedesk_kefu/model/messageZhipuAI.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class MessageState extends Equatable {
  const MessageState();

  @override
  List<Object> get props => [];
}

class InitialMessageState extends MessageState {}

class MessageLoading extends MessageState {
  @override
  String toString() => 'MessageLoading';
}

class MessageUpLoading extends MessageState {
  @override
  String toString() => 'MessageUpLoading';
}

class RestMessageSending extends MessageState {
  @override
  String toString() => 'RestMessageSending';
}

class ReceiveMessageState extends MessageState {
  final Message? message;

  const ReceiveMessageState({@required this.message}) : super();
}

class SendMessageRestSuccess extends MessageState {
  final JsonResult jsonResult;

  const SendMessageRestSuccess(this.jsonResult);

  @override
  List<Object> get props => [jsonResult];

  @override
  String toString() => 'SendMessageRestSuccess';
}

class UploadImageSuccess extends MessageState {
  final UploadJsonResult uploadJsonResult;

  const UploadImageSuccess(this.uploadJsonResult);

  @override
  List<Object> get props => [uploadJsonResult];

  @override
  String toString() => 'UploadImageSuccess { logo: ${uploadJsonResult.url} }';
}

class UpLoadImageError extends MessageState {
  @override
  String toString() => 'UpLoadImageError';
}

class SendMessageRestError extends MessageState {
  final String json;

  const SendMessageRestError(this.json);

  @override
  List<Object> get props => [json];

  @override
  String toString() => 'SendMessageRestError';
}

class LoadHistoryMessageError extends MessageState {
  @override
  String toString() => 'LoadHistoryMessageError';
}

class LoadTopicMessageError extends MessageState {
  @override
  String toString() => 'LoadTopicMessageError';
}

class LoadMessageFileHelperError extends MessageState {
  @override
  String toString() => 'LoadMessageFileHelperError';
}

class LoadMessageZhipuAIError extends MessageState {
  @override
  String toString() => 'LoadMessageZhipuAIError';
}

class LoadChannelMessageError extends MessageState {
  @override
  String toString() => 'LoadChannelMessageError';
}

class LoadUnreadMessageError extends MessageState {
  @override
  String toString() => 'LoadUnreadMessageError';
}

class LoadUnreadVisitorMessageError extends MessageState {
  @override
  String toString() => 'LoadUnreadVisitorMessageError';
}

class LoadUnreadAgentMessageError extends MessageState {
  @override
  String toString() => 'LoadUnreadAgentMessageError';
}

class UploadVideoSuccess extends MessageState {
  final UploadJsonResult uploadJsonResult;

  const UploadVideoSuccess(this.uploadJsonResult);

  @override
  List<Object> get props => [uploadJsonResult];

  @override
  String toString() => 'UploadVideoSuccess { logo: ${uploadJsonResult.url} }';
}

class UpLoadVideoError extends MessageState {
  @override
  String toString() => 'UpLoadVideoError';
}

class LoadHistoryMessageSuccess extends MessageState {
  final List<Message>? messageList;

  const LoadHistoryMessageSuccess({@required this.messageList}) : super();
}

class LoadTopicMessageSuccess extends MessageState {
  final List<Message>? messageList;

  const LoadTopicMessageSuccess({@required this.messageList}) : super();
}

class LoadMessageFileHelperSuccess extends MessageState {
  final List<Message>? messageList;

  const LoadMessageFileHelperSuccess({@required this.messageList}) : super();
}

class LoadMessageZhipuAISuccess extends MessageState {
  final List<MessageZhipuAI>? messageList;

  const LoadMessageZhipuAISuccess({@required this.messageList}) : super();
}

class LoadChannelMessageSuccess extends MessageState {
  final List<Message>? messageList;

  const LoadChannelMessageSuccess({@required this.messageList}) : super();
}

class LoadUnreadMessageSuccess extends MessageState {
  final List<Message>? messageList;

  const LoadUnreadMessageSuccess({@required this.messageList}) : super();
}

class LoadUnreadVisitorMessageSuccess extends MessageState {
  final List<Message>? messageList;

  const LoadUnreadVisitorMessageSuccess({@required this.messageList}) : super();
}

class LoadUnreadAgentMessageSuccess extends MessageState {
  final List<Message>? messageList;

  const LoadUnreadAgentMessageSuccess({@required this.messageList}) : super();
}

class QueryAnswerSuccess extends MessageState {
  final Message? query;
  final Message? answer;

  const QueryAnswerSuccess({@required this.query, @required this.answer}) : super();
}

class QueryCategorySuccess extends MessageState {
  final Message? query;
  final Message? answer;

  const QueryCategorySuccess({@required this.query, @required this.answer}) : super();
}

class MessageAnswerSuccess extends MessageState {
  final Message? query;
  final Message? answer;

  const MessageAnswerSuccess({@required this.query, @required this.answer}) : super();
}

class RateAnswerSuccess extends MessageState {
  final Message? result;

  const RateAnswerSuccess({@required this.result}) : super();
}
