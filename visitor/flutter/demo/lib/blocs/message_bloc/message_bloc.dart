// import 'dart:async';
import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:bytedesk_kefu/model/message.dart';
import 'package:bytedesk_kefu/model/messageZhipuAI.dart';
import 'package:bytedesk_kefu/model/requestAnswer.dart';
import 'package:bytedesk_kefu/model/requestCategory.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';
import 'package:bytedesk_kefu/repositories/message_repository.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:bloc/bloc.dart';
import './bloc.dart';

class MessageBloc extends Bloc<MessageEvent, MessageState> {
  //
  final MessageRepository messageRepository = MessageRepository();

  MessageBloc() : super(InitialMessageState()) {
    on<ReceiveMessageEvent>(_mapRefreshCourseToState);
    on<UploadImageEvent>(_mapUploadImageToState);
    on<UploadImageBytesEvent>(_mapUploadImageBytesToState);
    on<UploadVideoEvent>(_mapUploadVideoToState);
    on<UploadVideoBytesEvent>(_mapUploadVideoBytesToState);
    on<SendMessageRestEvent>(_mapSendMessageRestToState);
    on<SendZhipuAIMessageRestEvent>(_mapSendZhipuAIMessageRestToState);
    on<LoadHistoryMessageEvent>(_mapLoadHistoryMessageToState);
    on<LoadUnreadMessagesEvent>(_mapLoadUnreadMessageToState);
    on<LoadUnreadVisitorMessagesEvent>(_mapLoadUnreadVisitorMessageToState);
    on<LoadUnreadAgentMessagesEvent>(_mapLoadUnreadAgentMessageToState);
    on<LoadTopicMessageEvent>(_mapLoadTopicMessageToState);
    on<LoadMessageFileHelperEvent>(_mapLoadMessageFileHelperToState);
    on<LoadMessageZhipuAIEvent>(_mapLoadMessageZhipuAIToState);

    on<LoadChannelMessageEvent>(_mapLoadChannelMessageToState);
    on<QueryAnswerEvent>(_mapQueryAnswerToState);
    on<QueryCategoryEvent>(_mapQueryCategoryToState);
    on<MessageAnswerEvent>(_mapMessageAnswerToState);
    on<RateAnswerEvent>(_mapRateAnswerToState);
  }

  void _mapRefreshCourseToState(
      ReceiveMessageEvent event, Emitter<MessageState> emit) async {
    try {
      emit(ReceiveMessageState(message: event.message));
    } catch (error) {
      BytedeskUtils.printLog(error);
    }
  }

  void _mapUploadImageToState(
      UploadImageEvent event, Emitter<MessageState> emit) async {
    emit(MessageUpLoading());
    try {
      final UploadJsonResult uploadJsonResult =
          await messageRepository.uploadImage(event.filePath);
      emit(UploadImageSuccess(uploadJsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUploadImageBytesToState(
      UploadImageBytesEvent event, Emitter<MessageState> emit) async {
    emit(MessageUpLoading());
    try {
      final UploadJsonResult uploadJsonResult =
          await messageRepository.uploadImageBytes(event.fileName, event.fileBytes, event.mimeType);
      emit(UploadImageSuccess(uploadJsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUploadVideoToState(
      UploadVideoEvent event, Emitter<MessageState> emit) async {
    emit(MessageUpLoading());
    try {
      final UploadJsonResult uploadJsonResult =
          await messageRepository.uploadVideo(event.filePath);
      emit(UploadVideoSuccess(uploadJsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadVideoError());
    }
  }

  void _mapUploadVideoBytesToState(
      UploadVideoBytesEvent event, Emitter<MessageState> emit) async {
    emit(MessageUpLoading());
    try {
      final UploadJsonResult uploadJsonResult = await messageRepository
          .uploadVideoBytes(event.fileName, event.fileBytes, event.mimeType);
      emit(UploadVideoSuccess(uploadJsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadVideoError());
    }
  }

  void _mapSendMessageRestToState(
      SendMessageRestEvent event, Emitter<MessageState> emit) async {
    emit(RestMessageSending());
    try {
      final JsonResult jsonResult =
          await messageRepository.sendMessageRest(event.json);
      emit(SendMessageRestSuccess(jsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(SendMessageRestError(event.json!));
    }
  }

  void _mapSendZhipuAIMessageRestToState(
      SendZhipuAIMessageRestEvent event, Emitter<MessageState> emit) async {
    emit(RestMessageSending());
    try {
      final JsonResult jsonResult =
          await messageRepository.sendZhipuAIMessageRest(event.json);
      emit(SendMessageRestSuccess(jsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(SendMessageRestError(event.json!));
    }
  }

  void _mapLoadHistoryMessageToState(
      LoadHistoryMessageEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final List<Message> messageList = await messageRepository
          .loadHistoryMessages(event.uid, event.page, event.size);
      emit(LoadHistoryMessageSuccess(messageList: messageList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(LoadHistoryMessageError());
    }
  }

  void _mapLoadTopicMessageToState(
      LoadTopicMessageEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final List<Message> messageList = await messageRepository
          .loadTopicMessages(event.topic, event.page, event.size);
      emit(LoadTopicMessageSuccess(messageList: messageList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(LoadTopicMessageError());
    }
  }

  void _mapLoadMessageFileHelperToState(
      LoadMessageFileHelperEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final List<Message> messageList = await messageRepository
          .loadTopicMessagesFileHelper(event.topic, event.page, event.size);
      emit(LoadMessageFileHelperSuccess(messageList: messageList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(LoadMessageFileHelperError());
    }
  }

  void _mapLoadMessageZhipuAIToState(
      LoadMessageZhipuAIEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final List<MessageZhipuAI> messageList = await messageRepository
          .loadTidMessagesZhipuAI(event.tid, event.page, event.size);
      emit(LoadMessageZhipuAISuccess(messageList: messageList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(LoadMessageZhipuAIError());
    }
  }

  void _mapLoadChannelMessageToState(
      LoadChannelMessageEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final List<Message> messageList = await messageRepository
          .loadChannelMessages(event.cid, event.page, event.size);
      emit(LoadChannelMessageSuccess(messageList: messageList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(LoadChannelMessageError());
    }
  }

  void _mapLoadUnreadMessageToState(
      LoadUnreadMessagesEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final List<Message> messageList = await messageRepository
          .loadUnreadMessages(event.wid, event.page, event.size);
      emit(LoadUnreadMessageSuccess(messageList: messageList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(LoadUnreadMessageError());
    }
  }

  void _mapLoadUnreadVisitorMessageToState(
      LoadUnreadVisitorMessagesEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final List<Message> messageList = await messageRepository
          .loadUnreadVisitorMessages(event.page, event.size);
      emit(LoadUnreadVisitorMessageSuccess(messageList: messageList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(LoadUnreadMessageError());
    }
  }

  void _mapLoadUnreadAgentMessageToState(
      LoadUnreadAgentMessagesEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final List<Message> messageList = await messageRepository
          .loadUnreadAgentMessages(event.page, event.size);
      emit(LoadUnreadAgentMessageSuccess(messageList: messageList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(LoadUnreadAgentMessageError());
    }
  }

  void _mapQueryAnswerToState(
      QueryAnswerEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final RequestAnswerResult requestAnswerResult =
          await messageRepository.queryAnswer(event.tid, event.aid, event.mid);
      emit(QueryAnswerSuccess(
          query: requestAnswerResult.query,
          answer: requestAnswerResult.anwser));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapQueryCategoryToState(
      QueryCategoryEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final RequestCategoryResult requestAnswerResult =
          await messageRepository.queryCategory(event.tid, event.cid);
      emit(QueryCategorySuccess(
          query: requestAnswerResult.query,
          answer: requestAnswerResult.anwser));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapMessageAnswerToState(
      MessageAnswerEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final RequestAnswerResult requestAnswerResult =
          await messageRepository.messageAnswer(event.wid, event.content);
      emit(MessageAnswerSuccess(
          query: requestAnswerResult.query,
          answer: requestAnswerResult.anwser));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapRateAnswerToState(
      RateAnswerEvent event, Emitter<MessageState> emit) async {
    emit(MessageLoading());
    try {
      final RequestAnswerResult requestAnswerResult =
          await messageRepository.rateAnswer(event.aid, event.mid, event.rate);
      emit(RateAnswerSuccess(result: requestAnswerResult.anwser));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }
}
