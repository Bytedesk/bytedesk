import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class ThreadEvent extends Equatable {
  const ThreadEvent();

  @override
  List<Object> get props => [];
}

// class InitThreadEvent extends ThreadEvent {}

class RefreshThreadEvent extends ThreadEvent {}

class RefreshHistoryThreadEvent extends ThreadEvent {
  final int? page;
  final int? size;
  //
  const RefreshHistoryThreadEvent({@required this.page, @required this.size})
      : super();
}

class RefreshVisitorThreadEvent extends ThreadEvent {
  final int? page;
  final int? size;
  //
  const RefreshVisitorThreadEvent({@required this.page, @required this.size})
      : super();
}

class RefreshVisitorThreadAllEvent extends ThreadEvent {
  //
  const RefreshVisitorThreadAllEvent() : super();
}

class UpdateThreadEvent extends ThreadEvent {
  final String? tid;

  const UpdateThreadEvent({@required this.tid})
      : assert(tid != null),
        super();
}

class DeleteThreadEvent extends ThreadEvent {
  final String? tid;

  const DeleteThreadEvent({@required this.tid})
      : assert(tid != null),
        super();
}

// 请求客服会话
class RequestThreadEvent extends ThreadEvent {
  final String? wid;
  final String? type;
  final String? aid;
  final bool? isV2Robot;

  const RequestThreadEvent(
      {@required this.wid, @required this.type, @required this.aid, @required this.isV2Robot})
      : super();
}

// TODO: 添加key验证，防止用户滥用
// 请求智谱AI客服会话
class RequestZhipuAIThreadEvent extends ThreadEvent {
  final String? wid;
  final String? forceNew;

  const RequestZhipuAIThreadEvent(
      {@required this.wid,
      @required this.forceNew})
      : super();
}

class RefreshZhipuAIThreadHistoryEvent extends ThreadEvent {
  final int? page;
  final int? size;
  //
  const RefreshZhipuAIThreadHistoryEvent({@required this.page, @required this.size})
      : super();
}

// 请求人工客服，不管此工作组是否设置为默认机器人，只要有人工客服在线，则可以直接对接人工
class RequestAgentEvent extends ThreadEvent {
  final String? wid;
  final String? type;
  final String? aid;

  const RequestAgentEvent(
      {@required this.wid, @required this.type, @required this.aid})
      : super();
}

class RequestContactThreadEvent extends ThreadEvent {
  final String? cid;

  const RequestContactThreadEvent({@required this.cid}) : super();
}

class RequestGroupThreadEvent extends ThreadEvent {
  final String? gid;

  const RequestGroupThreadEvent({@required this.gid}) : super();
}

class MarkTopThreadEvent extends ThreadEvent {
  final String? tid;

  const MarkTopThreadEvent({@required this.tid}) : super();
}

class UnMarkTopThreadEvent extends ThreadEvent {
  final String? tid;

  const UnMarkTopThreadEvent({@required this.tid}) : super();
}

class MarkNodisturbThreadEvent extends ThreadEvent {
  final String? tid;

  const MarkNodisturbThreadEvent({@required this.tid}) : super();
}

class UnMarkNodisturbThreadEvent extends ThreadEvent {
  final String? tid;

  const UnMarkNodisturbThreadEvent({@required this.tid}) : super();
}

class MarkUnreadThreadEvent extends ThreadEvent {
  final String? tid;

  const MarkUnreadThreadEvent({@required this.tid}) : super();
}

class UnMarkUnreadThreadEvent extends ThreadEvent {
  final String? tid;

  const UnMarkUnreadThreadEvent({@required this.tid}) : super();
}
