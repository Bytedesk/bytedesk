// import 'dart:async';
import 'package:bytedesk_kefu/model/markThread.dart';
import 'package:bloc/bloc.dart';
import 'package:bytedesk_kefu/model/requestThreadZhipuAI.dart';
import 'package:bytedesk_kefu/model/threadZhipuAI.dart';
import './bloc.dart';
import 'package:bytedesk_kefu/repositories/repositories.dart';
import 'package:bytedesk_kefu/model/model.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';

class ThreadBloc extends Bloc<ThreadEvent, ThreadState> {
  //
  final ThreadRepository threadRepository = ThreadRepository();

  ThreadBloc() : super(ThreadEmpty()) {
    // on<InitThreadEvent>(InitialThreadState);
    on<RefreshThreadEvent>(_mapRefreshThreadToState);
    on<RefreshHistoryThreadEvent>(_mapRefreshHistoryThreadToState);
    on<RefreshVisitorThreadEvent>(_mapRefreshVisitorThreadToState);
    on<RefreshVisitorThreadAllEvent>(_mapRefreshVisitorThreadAllToState);

    on<RequestThreadEvent>(_mapRequestThreadToState);
    on<RequestZhipuAIThreadEvent>(_mapRequestZhipuAIThreadToState);
    on<RefreshZhipuAIThreadHistoryEvent>(_mapRefreshZhipuAIThreadHistoryToState);
    on<RequestAgentEvent>(_mapRequestAgentToState);
    on<RequestContactThreadEvent>(_mapRequestContactThreadToState);
    on<RequestGroupThreadEvent>(_mapRequestGroupThreadToState);
    on<MarkTopThreadEvent>(_mapMarkTopThreadEventToState);
    on<UnMarkTopThreadEvent>(_mapUnMarkTopThreadEventToState);

    on<MarkNodisturbThreadEvent>(_mapMarkNodisturbThreadEventToState);
    on<UnMarkNodisturbThreadEvent>(_mapUnMarkNodisturbThreadEventToState);
    on<MarkUnreadThreadEvent>(_mapMarkUnreadThreadEventToState);
    on<UnMarkUnreadThreadEvent>(_mapUnMarkUnreadThreadEventToState);
    on<DeleteThreadEvent>(_mapDeleteThreadEventToState);
  }

  void _mapRefreshThreadToState(
      RefreshThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final List<Thread> threadList = await threadRepository.getThreads();
      emit(ThreadLoadSuccess(threadList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapRefreshHistoryThreadToState(
      RefreshHistoryThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadHistoryLoading());
    try {
      final List<Thread> threadList =
          await threadRepository.getHistoryThreads(event.page, event.size);
      emit(ThreadLoadSuccess(threadList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapRefreshVisitorThreadToState(
      RefreshVisitorThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadVisitorLoading());
    try {
      final List<Thread> threadList =
          await threadRepository.getVisitorThreads(event.page, event.size);
      emit(ThreadLoadSuccess(threadList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapRefreshVisitorThreadAllToState(
      RefreshVisitorThreadAllEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final List<Thread> threadList =
          await threadRepository.getVisitorThreadsAll();
      emit(ThreadLoadSuccess(threadList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapRequestThreadToState(
      RequestThreadEvent event, Emitter<ThreadState> emit) async {
    emit(RequestThreading());
    try {
      final RequestThreadResult thread = await threadRepository.requestThread(
          event.wid, event.type, event.aid, event.isV2Robot);
      emit(RequestThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(RequestThreadError());
    }
  }

  void _mapRequestZhipuAIThreadToState(
      RequestZhipuAIThreadEvent event, Emitter<ThreadState> emit) async {
    emit(RequestThreading());
    try {
      final RequestThreadZhipuAIResult thread = await threadRepository.requestZhipuAIThread(
          event.wid, event.forceNew);
      emit(RequestThreadZhipuAISuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(RequestThreadError());
    }
  }

  void _mapRefreshZhipuAIThreadHistoryToState(
      RefreshZhipuAIThreadHistoryEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadHistoryLoading());
    try {
      final List<ThreadZhipuAI> threadList =
          await threadRepository.getZhipuAIThreadHistory(event.page, event.size);
      emit(ThreadZhipuAILoadSuccess(threadList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapRequestAgentToState(
      RequestAgentEvent event, Emitter<ThreadState> emit) async {
    emit(RequestAgentThreading());
    try {
      final RequestThreadResult thread =
          await threadRepository.requestAgent(event.wid, event.type, event.aid);
      emit(RequestAgentSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(RequestAgentThreadError());
    }
  }

  void _mapRequestContactThreadToState(
      RequestContactThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final RequestThreadResult thread =
          await threadRepository.requestContactThread(event.cid);
      emit(RequestContactThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapRequestGroupThreadToState(
      RequestGroupThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final RequestThreadResult thread =
          await threadRepository.requestGroupThread(event.gid);
      emit(RequestGroupThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapMarkTopThreadEventToState(
      MarkTopThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final MarkThreadResult thread = await threadRepository.markTop(event.tid);
      emit(MarkTopThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapUnMarkTopThreadEventToState(
      UnMarkTopThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final MarkThreadResult thread =
          await threadRepository.unmarkTop(event.tid);
      emit(UnMarkTopThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapMarkNodisturbThreadEventToState(
      MarkNodisturbThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final MarkThreadResult thread =
          await threadRepository.markNodisturb(event.tid);
      emit(MarkNodisturbThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapUnMarkNodisturbThreadEventToState(
      UnMarkNodisturbThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final MarkThreadResult thread =
          await threadRepository.unmarkNodisturb(event.tid);
      emit(UnMarkNodisturbThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapMarkUnreadThreadEventToState(
      MarkUnreadThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final MarkThreadResult thread =
          await threadRepository.markUnread(event.tid);
      emit(MarkUnreadThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapUnMarkUnreadThreadEventToState(
      UnMarkUnreadThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final MarkThreadResult thread =
          await threadRepository.unmarkUnread(event.tid);
      emit(UnMarkUnreadThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }

  void _mapDeleteThreadEventToState(
      DeleteThreadEvent event, Emitter<ThreadState> emit) async {
    emit(ThreadLoading());
    try {
      final MarkThreadResult thread = await threadRepository.delete(event.tid);
      emit(DeleteThreadSuccess(thread));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(ThreadLoadError());
    }
  }
}
