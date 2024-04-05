import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:equatable/equatable.dart';
import 'package:sp_util/sp_util.dart';

class Thread extends Equatable {
  //
  final String? tid;
  final String? topic;
  final String? wid;
  final String? uid;
  final String? nickname;
  final String? avatar;
  final String? content;
  final String? timestamp;
  final int? unreadCount;
  final String? type;
  //
  final bool? current;
  //
  final bool? top;
  final bool? topVisitor;
  //
  final bool? nodisturb;
  final bool? nodisturbVisitor;
  //
  final bool? unread;
  final bool? unreadVisitor;
  //
  final String? client;
  final String? currentUid;

  const Thread(
      {this.tid,
      this.topic,
      this.wid,
      this.uid,
      this.nickname,
      this.avatar,
      this.content,
      this.timestamp,
      this.unreadCount,
      this.type,
      this.current,
      this.top,
      this.topVisitor,
      this.nodisturb,
      this.nodisturbVisitor,
      this.unread,
      this.unreadVisitor,
      this.client,
      this.currentUid});

  static Thread fromWorkGroupJson(dynamic json) {
    return Thread(
        tid: json['tid'],
        topic: json['topic'],
        wid: json['workGroup']['wid'],
        nickname: json['workGroup']['nickname'],
        avatar: json['workGroup']['avatar'],
        content: json['content'],
        timestamp: BytedeskUtils.getTimeDuration(json['timestamp']),
        unreadCount: json['unreadCount'],
        type: json['type'],
        current: json['current'],
        client: json['client'],
        top: json['top'],
        topVisitor: json['topVisitor'],
        nodisturb: json['nodisturb'],
        nodisturbVisitor: json['nodisturbVisitor'],
        unread: json['unread'],
        unreadVisitor: json['unreadVisitor']);
  }

  static Thread fromWorkGroupJson2(dynamic json) {
    if (json['type'] == BytedeskConstants.THREAD_TYPE_WORKGROUP) {
      return Thread(
          tid: json['tid'],
          topic: json['topic'],
          wid: json['workGroup']['wid'],
          nickname: json['workGroup']['nickname'],
          avatar: json['workGroup']['avatar'],
          content: json['content'],
          timestamp: json['timestamp'],
          unreadCount: json['unreadCount'],
          type: json['type'],
          current: json['current'],
          client: json['client'],
          top: json['top'],
          topVisitor: json['topVisitor'],
          nodisturb: json['nodisturb'],
          nodisturbVisitor: json['nodisturbVisitor'],
          unread: json['unread'],
          unreadVisitor: json['unreadVisitor']);
    } else if (json['type'] == BytedeskConstants.THREAD_TYPE_APPOINTED) {
      return Thread(
          tid: json['tid'],
          topic: json['topic'],
          wid: json['agent']['uid'],
          nickname: json['agent']['nickname'],
          avatar: json['agent']['avatar'],
          content: json['content'],
          timestamp: json['timestamp'],
          unreadCount: json['unreadCount'],
          type: json['type'],
          current: json['current'],
          client: json['client'],
          top: json['top'],
          topVisitor: json['topVisitor'],
          nodisturb: json['nodisturb'],
          nodisturbVisitor: json['nodisturbVisitor'],
          unread: json['unread'],
          unreadVisitor: json['unreadVisitor']);
    } else if (json['type'] == BytedeskConstants.THREAD_TYPE_CHANNEL) {
      return Thread(
          tid: json['tid'],
          topic: json['topic'],
          wid: json['channel']['cid'],
          nickname: json['channel']['nickname'],
          avatar: json['channel']['avatar'],
          content: json['content'],
          timestamp: json['timestamp'],
          unreadCount: json['unreadCount'],
          type: json['type'],
          current: json['current'],
          client: json['client'],
          top: json['top'],
          topVisitor: json['topVisitor'],
          nodisturb: json['nodisturb'],
          nodisturbVisitor: json['nodisturbVisitor'],
          unread: json['unread'],
          unreadVisitor: json['unreadVisitor']);
    }
    // 其他类型
    return Thread(
        tid: json['tid'],
        topic: json['topic'],
        wid: json['admin']['uid'],
        nickname: json['admin']['nickname'],
        avatar: json['admin']['avatar'],
        content: json['content'],
        timestamp: json['timestamp'],
        unreadCount: json['unreadCount'],
        type: json['type'],
        current: json['current'],
        client: json['client'],
        top: json['top'],
        topVisitor: json['topVisitor'],
        nodisturb: json['nodisturb'],
        nodisturbVisitor: json['nodisturbVisitor'],
        unread: json['unread'],
        unreadVisitor: json['unreadVisitor']);
  }

  static Thread fromHistoryJson(dynamic json) {
    return Thread(
        tid: json['tid'],
        topic: json['topic'],
        // wid: json['wid'],
        nickname: json['nickname'],
        avatar: json['avatar'],
        content: json['content'],
        timestamp: BytedeskUtils.getTimeDuration(json['timestamp']),
        unreadCount: json['unreadCount'],
        type: json['type'],
        current: json['current'],
        client: json['client'],
        top: json['top'],
        topVisitor: json['topVisitor'],
        nodisturb: json['nodisturb'],
        nodisturbVisitor: json['nodisturbVisitor'],
        unread: json['unread'],
        unreadVisitor: json['unreadVisitor']);
  }

  static Thread fromVisitorJson(dynamic json) {
    return Thread(
        tid: json['tid'],
        topic: json['topic'],
        uid: json['visitor']['uid'],
        // nickname: json['visitor']['nickname'],
        nickname: SpUtil.getString(BytedeskConstants.nickname)!,
        // avatar: json['visitor']['avatar'],
        avatar: SpUtil.getString(BytedeskConstants.avatar)!,
        content: json['content'],
        timestamp: BytedeskUtils.getTimeDuration(json['timestamp']),
        unreadCount: json['unreadCount'],
        type: json['type'],
        current: json['current'],
        client: json['client'],
        top: json['top'],
        topVisitor: json['topVisitor'],
        nodisturb: json['nodisturb'],
        nodisturbVisitor: json['nodisturbVisitor'],
        unread: json['unread'],
        unreadVisitor: json['unreadVisitor']);
  }

  static Thread fromContactJson(dynamic json) {
    return Thread(
        tid: json['tid'],
        topic: json['topic'],
        nickname: json['contact']['nickname'],
        avatar: json['contact']['avatar'],
        content: json['content'],
        timestamp: BytedeskUtils.getTimeDuration(json['timestamp']),
        unreadCount: json['unreadCount'],
        type: json['type'],
        current: json['current'],
        client: json['client'],
        top: json['top'],
        topVisitor: json['topVisitor'],
        nodisturb: json['nodisturb'],
        nodisturbVisitor: json['nodisturbVisitor'],
        unread: json['unread'],
        unreadVisitor: json['unreadVisitor']);
  }

  static Thread fromGroupJson(dynamic json) {
    return Thread(
        tid: json['tid'],
        topic: json['topic'],
        nickname: json['group']['nickname'],
        avatar: json['group']['avatar'],
        content: json['content'],
        timestamp: BytedeskUtils.getTimeDuration(json['timestamp']),
        unreadCount: json['unreadCount'],
        type: json['type'],
        current: json['current'],
        client: json['client'],
        top: json['top'],
        topVisitor: json['topVisitor'],
        nodisturb: json['nodisturb'],
        nodisturbVisitor: json['nodisturbVisitor'],
        unread: json['unread'],
        unreadVisitor: json['unreadVisitor']);
  }

  static Thread fromFileHelperJson(dynamic json) {
    return Thread(
        tid: json['tid'],
        topic: json['topic'],
        uid: json['visitor']['uid'],
        nickname: json['visitor']['nickname'],
        avatar: json['visitor']['avatar'],
        content: json['content'],
        timestamp: BytedeskUtils.getTimeDuration(json['timestamp']),
        unreadCount: json['unreadCount'],
        type: json['type'],
        current: json['current'],
        client: json['client'],
        top: json['top'],
        topVisitor: json['topVisitor'],
        nodisturb: json['nodisturb'],
        nodisturbVisitor: json['nodisturbVisitor'],
        unread: json['unread'],
        unreadVisitor: json['unreadVisitor']);
  }

  static Thread fromUnreadJson(dynamic json) {
    return Thread(
        tid: json['tid'],
        topic: json['topic']);
  }

  @override
  List<Object> get props => [topic!];

  // Convert a Thread into a Map. The keys must correspond to the names of the
  // columns in the database.
  Map<String, dynamic> toMap() {
    return {
      'tid': tid,
      'topic': topic,
      'wid': wid,
      'uid': uid,
      'nickname': nickname,
      'avatar': avatar,
      'content': content,
      'timestamp': timestamp,
      'unreadCount': unreadCount,
      'type': type,
      'currentUid': currentUid,
      'client': client,
      'current': current,
      'top': top,
      'topVisitor': topVisitor,
      'nodisturb': nodisturb,
      'nodisturbVisitor': nodisturbVisitor,
      'unread': unread,
      'unreadVisitor': unreadVisitor
    };
  }

  // Thread.fromMap(Map<String, dynamic> map) {
  //   tid = map['tid'];
  //   topic = map['topic'];
  //   wid = map['wid'];
  //   uid = map['uid'];
  //   nickname = map['nickname'];
  //   avatar = map['avatar'];
  //   content = map['content'];
  //   timestamp = map['timestamp'];
  //   unreadCount = map['unreadCount'];
  //   type = map['type'];
  //   current = map['current'];
  //   client = map['client'];
  //   top = map['top'];
  //   topVisitor = map['topVisitor'];
  //   nodisturb = map['nodisturb'];
  //   nodisturbVisitor = map['nodisturbVisitor'];
  //   unread = map['unread'];
  //   unreadVisitor = map['unreadVisitor'];
  //   currentUid = map['currentUid'];
  //   client = map['client'];
  // }
}
