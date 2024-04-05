// ignore_for_file: file_names

import 'dart:convert';

import 'package:bytedesk_kefu/model/answer.dart';
import 'package:bytedesk_kefu/model/robot.dart';
// import 'package:bytedesk_kefu/model/thread.dart';
import 'package:bytedesk_kefu/model/threadZhipuAI.dart';
import 'package:bytedesk_kefu/model/user.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:sp_util/sp_util.dart';

import 'category.dart';
import 'message.dart';
// import 'package:equatable/equatable.dart';

class MessageZhipuAI {
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
  ThreadZhipuAI? thread;
  User? user;
  //
  List<Answer>? answers;
  String? answersJson;
  //
  List<Category>? categories;
  String? categoriesJson;
  //
  List<Robot>? robotList;

  MessageZhipuAI(
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
      this.categoriesJson,
      this.robotList})
      : super();

  static MessageZhipuAI fromMessage(Message message) {
    return MessageZhipuAI(
        mid: message.mid,
        content: message.content,
        imageUrl: message.imageUrl,
        voiceUrl: message.voiceUrl,
        videoUrl: message.videoUrl,
        fileUrl: message.fileUrl,
        nickname: message.nickname,
        avatar: message.avatar,
        type: message.type,
        topic: message.topic,
        timestamp: message.timestamp,
        isSend: message.isSend,
        thread: ThreadZhipuAI.fromThread(message.thread!),
        user: message.user,
        status: message.status,
        currentUid: message.currentUid,
        client: message.client,
        answers: message.answers,
        answersJson: message.answersJson,
        categories: message.categories,
        categoriesJson: message.categoriesJson);
  }

  //
  static MessageZhipuAI fromJsonThread(dynamic json) {
    //
    List<Category> categoriesList = [];
    if (json['type'] == BytedeskConstants.MESSAGE_TYPE_ROBOT_WELCOME) {
      categoriesList = json['categories'] == null
          ? []
          : (json['categories'] as List<dynamic>)
              .map((item) => Category.fromJson(item))
              .toList();
    }
    //
    MessageZhipuAI messageZhipuAI = MessageZhipuAI(
        mid: json['mid'],
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
        thread: ThreadZhipuAI.fromVisitorJson(json['thread']),
        user: User.fromJson(json['user']),
        categories: categoriesList,
        categoriesJson: json['categories'].toString()
        // answers: robotQaList,
        // answersJson: json['answers'].toString()
        );
    if (messageZhipuAI.currentUid == messageZhipuAI.user!.uid) {
      messageZhipuAI.isSend = 1;
    }
    return messageZhipuAI;
  }

  static MessageZhipuAI fromJsonZhipuAI(dynamic json) {
    //
    MessageZhipuAI messageZhipuAI = MessageZhipuAI(
      mid: json['mid'],
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
      isSend: 0,
      currentUid: SpUtil.getString(BytedeskConstants.uid),
      thread: ThreadZhipuAI.fromVisitorJson(json['thread']),
      user: User.fromJson(json['user']),
    );
    if (messageZhipuAI.currentUid == messageZhipuAI.user!.uid) {
      messageZhipuAI.isSend = 1;
    }
    return messageZhipuAI;
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

  MessageZhipuAI.fromMap(Map<String, dynamic> map) {
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
