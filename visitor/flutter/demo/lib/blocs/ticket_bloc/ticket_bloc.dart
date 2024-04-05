// import 'dart:async';
import 'package:bytedesk_kefu/blocs/ticket_bloc/bloc.dart';
import 'package:bloc/bloc.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:bytedesk_kefu/repositories/ticket_repository.dart';

class TicketBloc extends Bloc<TicketEvent, TicketState> {
  //
  final TicketRepository ticketRepository = TicketRepository();

  TicketBloc() : super(const UnTicketState()) {
    on<GetTicketCategoryEvent>(_mapGetTicketCategoryToState);
    on<SubmitTicketEvent>(_mapSubmitTicketToState);
    // on<UploadImageEvent>(_mapUploadImageToState);
    on<UploadImageEvent>(_mapUploadImageToState);
    on<UploadImageBytesEvent>(_mapUploadImageBytesToState);
  }

  void _mapGetTicketCategoryToState(
      GetTicketCategoryEvent event, Emitter<TicketState> emit) async {
    emit(TicketLoading());
    try {
      // final List<HelpCategory> categoryList =
      //     await feedbackRepository.getHelpTicketCategories();
      // emit(TicketCategoryState(categoryList);
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(TicketLoadError());
    }
  }

  void _mapSubmitTicketToState(
      SubmitTicketEvent event, Emitter<TicketState> emit) async {
    emit(TicketLoading());
    try {
      // final List<HelpCategory> categoryList =
      //     await feedbackRepository.getHelpTicketCategories();
      // emit(TicketCategoryState(categoryList);
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(TicketLoadError());
    }
  }

  void _mapUploadImageToState(
      UploadImageEvent event, Emitter<TicketState> emit) async {
    emit(TicketLoading());
    try {
      final UploadJsonResult uploadJsonResult =
          await ticketRepository.uploadImage(event.filePath);
      emit(UploadImageSuccess(uploadJsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUploadImageBytesToState(
      UploadImageBytesEvent event, Emitter<TicketState> emit) async {
    emit(TicketLoading());
    try {
      final UploadJsonResult uploadJsonResult = await ticketRepository
          .uploadImageBytes(event.fileName, event.fileBytes, event.mimeType);
      emit(UploadImageSuccess(uploadJsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  // void _mapUploadImageToState(
  //     UploadImageEvent event, Emitter<TicketState> emit) async {
  //   emit(TicketLoading());
  //   try {
  //     final String url = await feedbackRepository.uploadImage(event.filePath);
  //     emit(UploadImageSuccess(url));
  //   } catch (error) {
  //     BytedeskUtils.printLog(error);
  //     emit(UpLoadImageError());
  //   }
  // }
}
