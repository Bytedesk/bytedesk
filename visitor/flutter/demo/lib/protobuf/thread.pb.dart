///
//  Generated code. Do not modify.
//  source: thread.proto
//
// @dart = 2.12
// ignore_for_file: annotate_overrides,camel_case_types,unnecessary_const,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name,return_of_invalid_type,unnecessary_this,prefer_final_fields

import 'dart:core' as $core;

import 'package:protobuf/protobuf.dart' as $pb;

class Thread extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Thread',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'tid')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'type')
    ..aOS(
        5,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'client')
    ..aOS(
        6,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'nickname')
    ..aOS(
        7,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'avatar')
    ..aOS(
        8,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'content')
    ..aOS(
        9,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'timestamp')
    ..a<$core.int>(
        10,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'unreadCount',
        $pb.PbFieldType.O3,
        protoName: 'unreadCount')
    ..aOS(
        11,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'topic')
    ..aOS(
        20,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'extra')
    ..hasRequiredFields = false;

  Thread._() : super();
  factory Thread({
    $core.String? tid,
    $core.String? type,
    $core.String? client,
    $core.String? nickname,
    $core.String? avatar,
    $core.String? content,
    $core.String? timestamp,
    $core.int? unreadCount,
    $core.String? topic,
    $core.String? extra,
  }) {
    final _result = create();
    if (tid != null) {
      _result.tid = tid;
    }
    if (type != null) {
      _result.type = type;
    }
    if (client != null) {
      _result.client = client;
    }
    if (nickname != null) {
      _result.nickname = nickname;
    }
    if (avatar != null) {
      _result.avatar = avatar;
    }
    if (content != null) {
      _result.content = content;
    }
    if (timestamp != null) {
      _result.timestamp = timestamp;
    }
    if (unreadCount != null) {
      _result.unreadCount = unreadCount;
    }
    if (topic != null) {
      _result.topic = topic;
    }
    if (extra != null) {
      _result.extra = extra;
    }
    return _result;
  }
  factory Thread.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Thread.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Thread clone() => Thread()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Thread copyWith(void Function(Thread) updates) =>
      super.copyWith((message) => updates(message as Thread))
          as Thread; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Thread create() => Thread._();
  Thread createEmptyInstance() => create();
  static $pb.PbList<Thread> createRepeated() => $pb.PbList<Thread>();
  @$core.pragma('dart2js:noInline')
  static Thread getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Thread>(create);
  static Thread? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get tid => $_getSZ(0);
  @$pb.TagNumber(1)
  set tid($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasTid() => $_has(0);
  @$pb.TagNumber(1)
  void clearTid() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get type => $_getSZ(1);
  @$pb.TagNumber(2)
  set type($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(2)
  $core.bool hasType() => $_has(1);
  @$pb.TagNumber(2)
  void clearType() => clearField(2);

  @$pb.TagNumber(5)
  $core.String get client => $_getSZ(2);
  @$pb.TagNumber(5)
  set client($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(5)
  $core.bool hasClient() => $_has(2);
  @$pb.TagNumber(5)
  void clearClient() => clearField(5);

  @$pb.TagNumber(6)
  $core.String get nickname => $_getSZ(3);
  @$pb.TagNumber(6)
  set nickname($core.String v) {
    $_setString(3, v);
  }

  @$pb.TagNumber(6)
  $core.bool hasNickname() => $_has(3);
  @$pb.TagNumber(6)
  void clearNickname() => clearField(6);

  @$pb.TagNumber(7)
  $core.String get avatar => $_getSZ(4);
  @$pb.TagNumber(7)
  set avatar($core.String v) {
    $_setString(4, v);
  }

  @$pb.TagNumber(7)
  $core.bool hasAvatar() => $_has(4);
  @$pb.TagNumber(7)
  void clearAvatar() => clearField(7);

  @$pb.TagNumber(8)
  $core.String get content => $_getSZ(5);
  @$pb.TagNumber(8)
  set content($core.String v) {
    $_setString(5, v);
  }

  @$pb.TagNumber(8)
  $core.bool hasContent() => $_has(5);
  @$pb.TagNumber(8)
  void clearContent() => clearField(8);

  @$pb.TagNumber(9)
  $core.String get timestamp => $_getSZ(6);
  @$pb.TagNumber(9)
  set timestamp($core.String v) {
    $_setString(6, v);
  }

  @$pb.TagNumber(9)
  $core.bool hasTimestamp() => $_has(6);
  @$pb.TagNumber(9)
  void clearTimestamp() => clearField(9);

  @$pb.TagNumber(10)
  $core.int get unreadCount => $_getIZ(7);
  @$pb.TagNumber(10)
  set unreadCount($core.int v) {
    $_setSignedInt32(7, v);
  }

  @$pb.TagNumber(10)
  $core.bool hasUnreadCount() => $_has(7);
  @$pb.TagNumber(10)
  void clearUnreadCount() => clearField(10);

  @$pb.TagNumber(11)
  $core.String get topic => $_getSZ(8);
  @$pb.TagNumber(11)
  set topic($core.String v) {
    $_setString(8, v);
  }

  @$pb.TagNumber(11)
  $core.bool hasTopic() => $_has(8);
  @$pb.TagNumber(11)
  void clearTopic() => clearField(11);

  @$pb.TagNumber(20)
  $core.String get extra => $_getSZ(9);
  @$pb.TagNumber(20)
  set extra($core.String v) {
    $_setString(9, v);
  }

  @$pb.TagNumber(20)
  $core.bool hasExtra() => $_has(9);
  @$pb.TagNumber(20)
  void clearExtra() => clearField(20);
}

class ThreadList extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'ThreadList',
      createEmptyInstance: create)
    ..pc<Thread>(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'list',
        $pb.PbFieldType.PM,
        subBuilder: Thread.create)
    ..hasRequiredFields = false;

  ThreadList._() : super();
  factory ThreadList({
    $core.Iterable<Thread>? list,
  }) {
    final _result = create();
    if (list != null) {
      _result.list.addAll(list);
    }
    return _result;
  }
  factory ThreadList.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory ThreadList.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  ThreadList clone() => ThreadList()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  ThreadList copyWith(void Function(ThreadList) updates) =>
      super.copyWith((message) => updates(message as ThreadList))
          as ThreadList; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static ThreadList create() => ThreadList._();
  ThreadList createEmptyInstance() => create();
  static $pb.PbList<ThreadList> createRepeated() => $pb.PbList<ThreadList>();
  @$core.pragma('dart2js:noInline')
  static ThreadList getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<ThreadList>(create);
  static ThreadList? _defaultInstance;

  @$pb.TagNumber(1)
  $core.List<Thread> get list => $_getList(0);
}
