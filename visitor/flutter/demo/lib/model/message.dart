import 'dart:convert';

import 'package:bytedesk_kefu/model/answer.dart';
import 'package:bytedesk_kefu/model/thread.dart';
import 'package:bytedesk_kefu/model/user.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:sp_util/sp_util.dart';

import 'category.dart';
// import 'package:equatable/equatable.dart';

class Message {
  //
  String? mid;
  String? content;
  String? imageUrl;
  String? voiceUrl;
  String? videoUrl;
  String? fileUrl;
  String? nickname;
  String? avatar;
  String? type;
  String? topic;
  String? timestamp;
  String? status;
  int? isSend;
  String? currentUid;
  String? client;
  //
  Thread? thread;
  User? user;
  //
  List<Answer>? answers;
  String? answersJson;
  //
  List<Category>? categories;
  String? categoriesJson;

  Message(
      {this.mid,
      this.content,
      this.imageUrl,
      this.voiceUrl,
      this.videoUrl,
      this.fileUrl,
      this.nickname,
      this.avatar,
      this.type,
      this.topic,
      this.timestamp,
      this.isSend,
      this.thread,
      this.user,
      this.status,
      this.currentUid,
      this.client,
      this.answers,
      this.answersJson,
      this.categories,
      this.categoriesJson})
      : super();

  //
  static Message fromJsonThread(dynamic json) {
    //
    // String? content = json['content'];
    List<Answer> robotQaList = [];
    if (json['type'] == BytedeskConstants.MESSAGE_TYPE_ROBOT) {
      robotQaList = json['answers'] == null
          ? []
          : (json['answers'] as List<dynamic>)
              .map((item) => Answer.fromJson(item))
              .toList();
      // for (var i = 0; i < robotQaList.length; i++) {
      //   Answer answer = robotQaList[i];
      //   content += '\n\n' + answer.aid + ':' + answer.question;
      // }
    }
    //
    return Message(
        mid: json['mid'],
        // topic: json['thread']['topic'],
        content: json['content'],
        imageUrl: json['imageUrl'],
        voiceUrl: json['voiceUrl'],
        fileUrl: json['fileUrl'],
        videoUrl: json['videoOrShortUrl'],
        nickname: json['user']['nickname'],
        avatar: json['user']['avatar'],
        type: json['type'],
        timestamp: json['createdAt'],
        status: 'stored',
        isSend: 0,
        currentUid: SpUtil.getString(BytedeskConstants.uid),
        client: json['client'],
        thread: Thread.fromVisitorJson(json['thread']),
        user: User.fromJson(json['user']),
        answers: robotQaList,
        answersJson: json['answers'].toString());
  }

  //
  static Message fromJsonThreadWorkGroupV2(dynamic json) {
    //
    List<Category> categoriesList = [];
    if (json['type'] == BytedeskConstants.MESSAGE_TYPE_ROBOT_V2) {
      categoriesList = json['categories'] == null
          ? []
          : (json['categories'] as List<dynamic>)
              .map((item) => Category.fromJson(item))
              .toList();
    }
    //
    return Message(
        mid: json['mid'],
        // topic: json['thread']['topic'],
        content: json['content'],
        imageUrl: json['imageUrl'],
        voiceUrl: json['voiceUrl'],
        fileUrl: json['fileUrl'],
        videoUrl: json['videoOrShortUrl'],
        nickname: json['user']['nickname'],
        avatar: json['user']['avatar'],
        type: json['type'],
        timestamp: json['createdAt'],
        status: 'stored',
        isSend: 0,
        currentUid: SpUtil.getString(BytedeskConstants.uid),
        client: json['client'],
        thread: Thread.fromVisitorJson(json['thread']),
        user: User.fromJson(json['user']),
        categories: categoriesList,
        categoriesJson: json['categories'].toString());
  }

  //
  static Message fromJsonRobotQuery(dynamic json) {
    //
    // String? content = json['content'];
    List<Answer> robotQaList = [];
    if (json['type'] == BytedeskConstants.MESSAGE_TYPE_ROBOT) {
      robotQaList = json['answers'] == null
          ? []
          : (json['answers'] as List<dynamic>)
              .map((item) => Answer.fromJson(item))
              .toList();
      // for (var i = 0; i < robotQaList.length; i++) {
      //   Answer answer = robotQaList[i];
      //   content += '\n\n' + answer.aid + ':' + answer.question;
      // }
    }
    //
    return Message(
        mid: json['mid'],
        // topic: json['thread']['topic'],
        content: json['content'],
        imageUrl: json['imageUrl'],
        voiceUrl: json['voiceUrl'],
        fileUrl: json['fileUrl'],
        videoUrl: json['videoOrShortUrl'],
        nickname: json['user']['nickname'],
        avatar: json['user']['avatar'],
        type: json['type'],
        timestamp: json['createdAt'],
        status: 'stored',
        isSend: 0,
        currentUid: SpUtil.getString(BytedeskConstants.uid),
        client: json['client'],
        // thread: Thread.fromVisitorJson(json['thread']),
        user: User.fromJson(json['user']),
        answers: robotQaList,
        answersJson: json['answers'].toString());
  }


  static Message fromJson(dynamic json) {
    //
    List<Answer> robotQaList = [];
    if (json['type'] == BytedeskConstants.MESSAGE_TYPE_ROBOT) {
      robotQaList = json['answers'] == null
          ? []
          : (json['answers'] as List<dynamic>)
              .map((item) => Answer.fromJson(item))
              .toList();
    }
    return Message(
        mid: json['mid'],
        // topic: json['thread']['topic'],
        content: json['content'],
        imageUrl: json['imageUrl'],
        voiceUrl: json['voiceUrl'],
        fileUrl: json['fileUrl'],
        videoUrl: json['videoOrShortUrl'],
        nickname: json['user']['nickname'],
        avatar: json['user']['avatar'],
        type: json['type'],
        timestamp: json['createdAt'],
        client: json['client'],
        currentUid: SpUtil.getString(BytedeskConstants.uid),
        thread: Thread.fromUnreadJson(json['thread']),
        answers: robotQaList,
        answersJson: json['answers'].toString());
  }

  static Message fromJsonFileHelper(dynamic json) {
    //
    return Message(
        mid: json['mid'],
        // topic: json['thread']['topic'],
        content: json['content'],
        imageUrl: json['imageUrl'],
        voiceUrl: json['voiceUrl'],
        fileUrl: json['fileUrl'],
        videoUrl: json['videoOrShortUrl'],
        nickname: json['user']['nickname'],
        avatar: json['user']['avatar'],
        type: json['type'],
        timestamp: json['createdAt'],
        client: json['client'],
        isSend: 1,
        currentUid: SpUtil.getString(BytedeskConstants.uid));
  }

  // @override
  // List<Object> get props => [mid];

  // Convert a Message into a Map. The keys must correspond to the names of the
  // columns in the database.
  Map<String, dynamic> toMap() {
    return {
      'mid': mid,
      'content': content,
      'imageUrl': imageUrl,
      'voiceUrl': voiceUrl,
      'videoUrl': videoUrl,
      'fileUrl': fileUrl,
      'nickname': nickname,
      'avatar': avatar,
      'type': type,
      'topic': thread?.topic,
      'status': status,
      'timestamp': timestamp,
      'isSend': isSend,
      'currentUid': currentUid,
      'client': client,
      'answers': answersJson,
      'categories': categoriesJson
    };
  }

  Message.fromMap(Map<String, dynamic> map) {
    mid = map['mid'];
    content = map['content'];
    imageUrl = map['imageUrl'];
    voiceUrl = map['voiceUrl'];
    videoUrl = map['videoUrl'];
    fileUrl = map['fileUrl'];
    nickname = map['nickname'];
    avatar = map['avatar'];
    type = map['type'];
    topic = map['topic'];
    status = map['status'];
    timestamp = map['timestamp'];
    isSend = map['isSend'];
    client = map['client'];
    currentUid = SpUtil.getString(BytedeskConstants.uid);
  }

  String? channelTitle() {
    return json.decode(content!)['title'];
  }

  String? channelType() {
    return json.decode(content!)['type'];
  }

  String? channelContent() {
    return json.decode(content!)['content'];
  }
}
