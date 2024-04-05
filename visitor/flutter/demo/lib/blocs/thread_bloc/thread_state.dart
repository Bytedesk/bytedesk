import 'package:bytedesk_kefu/model/markThread.dart';
import 'package:bytedesk_kefu/model/requestThreadZhipuAI.dart';
import 'package:bytedesk_kefu/model/threadZhipuAI.dart';
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';
import 'package:bytedesk_kefu/model/model.dart';

@immutable
abstract class ThreadState extends Equatable {
  const ThreadState();

  @override
  List<Object> get props => [];
}

class ThreadUninitialized extends ThreadState {
  @override
  String toString() => 'ThreadUninitialized';
}

class InitialThreadState extends ThreadState {
  @override
  List<Object> get props => [];

  @override
  String toString() => 'InitialThreadState';
}

class ThreadEmpty extends ThreadState {
  @override
  String toString() => 'ThreadEmpty';
}

class ThreadLoading extends ThreadState {
  @override
  String toString() => 'ThreadLoading';
}

class ThreadVisitorLoading extends ThreadState {
  @override
  String toString() => 'ThreadVisitorLoading';
}

class ThreadHistoryLoading extends ThreadState {
  @override
  String toString() => 'ThreadHistoryLoading';
}

class ThreadLoadError extends ThreadState {
  @override
  String toString() => 'ThreadLoadError';
}

class ThreadLoadSuccess extends ThreadState {
  final List<Thread>? threadList;

  const ThreadLoadSuccess(this.threadList);

  ThreadLoadSuccess copyWith({List<Thread>? threadList}) {
    return ThreadLoadSuccess(threadList);
  }

  @override
  List<Object> get props => [threadList!];

  @override
  String toString() =>
      'ThreadLoadSuccess { threadList: ${threadList!.length} }';
}

class ThreadZhipuAILoadSuccess extends ThreadState {
  final List<ThreadZhipuAI>? threadList;

  const ThreadZhipuAILoadSuccess(this.threadList);

  ThreadZhipuAILoadSuccess copyWith({List<ThreadZhipuAI>? threadList}) {
    return ThreadZhipuAILoadSuccess(threadList);
  }

  @override
  List<Object> get props => [threadList!];

  @override
  String toString() =>
      'ThreadZhipuAILoadSuccess { threadList: ${threadList!.length} }';
}

class RequestThreading extends ThreadState {
  @override
  String toString() => 'RequestThreading';
}

class RequestThreadSuccess extends ThreadState {
  final RequestThreadResult threadResult;

  const RequestThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'RequestThreadSuccess';
}

class RequestThreadZhipuAISuccess extends ThreadState {
  final RequestThreadZhipuAIResult threadResult;

  const RequestThreadZhipuAISuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'RequestThreadZhipuAISuccess';
}

class RequestThreadError extends ThreadState {
  @override
  String toString() => 'RequestThreadError';
}

class RequestAgentThreading extends ThreadState {
  @override
  String toString() => 'RequestAgentThreading';
}

class RequestAgentSuccess extends ThreadState {
  final RequestThreadResult threadResult;

  const RequestAgentSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'RequestAgentSuccess';
}

class RequestAgentThreadError extends ThreadState {
  @override
  String toString() => 'RequestAgentThreadError';
}

class RequestContactThreadSuccess extends ThreadState {
  final RequestThreadResult threadResult;

  const RequestContactThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'RequestContactThreadSuccess';
}

class RequestGroupThreadSuccess extends ThreadState {
  final RequestThreadResult threadResult;

  const RequestGroupThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'RequestGroupThreadSuccess';
}

class MarkTopThreadSuccess extends ThreadState {
  final MarkThreadResult threadResult;

  const MarkTopThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'MarkTopThreadSuccess';
}

class UnMarkTopThreadSuccess extends ThreadState {
  final MarkThreadResult threadResult;

  const UnMarkTopThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'UnMarkTopThreadSuccess';
}

class MarkUnreadThreadSuccess extends ThreadState {
  final MarkThreadResult threadResult;

  const MarkUnreadThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'MarkUnreadThreadSuccess';
}

class UnMarkUnreadThreadSuccess extends ThreadState {
  final MarkThreadResult threadResult;

  const UnMarkUnreadThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'UnMarkUnreadThreadSuccess';
}

class MarkNodisturbThreadSuccess extends ThreadState {
  final MarkThreadResult threadResult;

  const MarkNodisturbThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'MarkNodisturbThreadSuccess';
}

class UnMarkNodisturbThreadSuccess extends ThreadState {
  final MarkThreadResult threadResult;

  const UnMarkNodisturbThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'UnMarkNodisturbThreadSuccess';
}

class DeleteThreadSuccess extends ThreadState {
  final MarkThreadResult threadResult;

  const DeleteThreadSuccess(this.threadResult);

  @override
  List<Object> get props => [threadResult];

  @override
  String toString() => 'DeleteThreadSuccess';
}
