///
//  Generated code. Do not modify.
//  source: message.proto
//
// @dart = 2.12
// ignore_for_file: annotate_overrides,camel_case_types,unnecessary_const,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name,return_of_invalid_type,unnecessary_this,prefer_final_fields

import 'dart:core' as $core;

import 'package:protobuf/protobuf.dart' as $pb;

import 'user.pb.dart' as $0;
import 'thread.pb.dart' as $1;

class Text extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Text',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'content')
    ..hasRequiredFields = false;

  Text._() : super();
  factory Text({
    $core.String? content,
  }) {
    final _result = create();
    if (content != null) {
      _result.content = content;
    }
    return _result;
  }
  factory Text.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Text.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Text clone() => Text()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Text copyWith(void Function(Text) updates) =>
      super.copyWith((message) => updates(message as Text))
          as Text; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Text create() => Text._();
  Text createEmptyInstance() => create();
  static $pb.PbList<Text> createRepeated() => $pb.PbList<Text>();
  @$core.pragma('dart2js:noInline')
  static Text getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Text>(create);
  static Text? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get content => $_getSZ(0);
  @$pb.TagNumber(1)
  set content($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasContent() => $_has(0);
  @$pb.TagNumber(1)
  void clearContent() => clearField(1);
}

class Image extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Image',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'mediaId',
        protoName: 'mediaId')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'picUrl',
        protoName: 'picUrl')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'imageUrl',
        protoName: 'imageUrl')
    ..hasRequiredFields = false;

  Image._() : super();
  factory Image({
    $core.String? mediaId,
    $core.String? picUrl,
    $core.String? imageUrl,
  }) {
    final _result = create();
    if (mediaId != null) {
      _result.mediaId = mediaId;
    }
    if (picUrl != null) {
      _result.picUrl = picUrl;
    }
    if (imageUrl != null) {
      _result.imageUrl = imageUrl;
    }
    return _result;
  }
  factory Image.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Image.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Image clone() => Image()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Image copyWith(void Function(Image) updates) =>
      super.copyWith((message) => updates(message as Image))
          as Image; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Image create() => Image._();
  Image createEmptyInstance() => create();
  static $pb.PbList<Image> createRepeated() => $pb.PbList<Image>();
  @$core.pragma('dart2js:noInline')
  static Image getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Image>(create);
  static Image? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get mediaId => $_getSZ(0);
  @$pb.TagNumber(1)
  set mediaId($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasMediaId() => $_has(0);
  @$pb.TagNumber(1)
  void clearMediaId() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get picUrl => $_getSZ(1);
  @$pb.TagNumber(2)
  set picUrl($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(2)
  $core.bool hasPicUrl() => $_has(1);
  @$pb.TagNumber(2)
  void clearPicUrl() => clearField(2);

  @$pb.TagNumber(3)
  $core.String get imageUrl => $_getSZ(2);
  @$pb.TagNumber(3)
  set imageUrl($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasImageUrl() => $_has(2);
  @$pb.TagNumber(3)
  void clearImageUrl() => clearField(3);
}

class File extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'File',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'fileUrl',
        protoName: 'fileUrl')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'fileName',
        protoName: 'fileName')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'fileSize',
        protoName: 'fileSize')
    ..hasRequiredFields = false;

  File._() : super();
  factory File({
    $core.String? fileUrl,
    $core.String? fileName,
    $core.String? fileSize,
  }) {
    final _result = create();
    if (fileUrl != null) {
      _result.fileUrl = fileUrl;
    }
    if (fileName != null) {
      _result.fileName = fileName;
    }
    if (fileSize != null) {
      _result.fileSize = fileSize;
    }
    return _result;
  }
  factory File.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory File.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  File clone() => File()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  File copyWith(void Function(File) updates) =>
      super.copyWith((message) => updates(message as File))
          as File; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static File create() => File._();
  File createEmptyInstance() => create();
  static $pb.PbList<File> createRepeated() => $pb.PbList<File>();
  @$core.pragma('dart2js:noInline')
  static File getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<File>(create);
  static File? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get fileUrl => $_getSZ(0);
  @$pb.TagNumber(1)
  set fileUrl($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasFileUrl() => $_has(0);
  @$pb.TagNumber(1)
  void clearFileUrl() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get fileName => $_getSZ(1);
  @$pb.TagNumber(2)
  set fileName($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(2)
  $core.bool hasFileName() => $_has(1);
  @$pb.TagNumber(2)
  void clearFileName() => clearField(2);

  @$pb.TagNumber(3)
  $core.String get fileSize => $_getSZ(2);
  @$pb.TagNumber(3)
  set fileSize($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasFileSize() => $_has(2);
  @$pb.TagNumber(3)
  void clearFileSize() => clearField(3);
}

class Voice extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Voice',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'mediaId',
        protoName: 'mediaId')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'format')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'voiceUrl',
        protoName: 'voiceUrl')
    ..a<$core.int>(
        4,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'length',
        $pb.PbFieldType.O3)
    ..aOB(
        5,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'played')
    ..hasRequiredFields = false;

  Voice._() : super();
  factory Voice({
    $core.String? mediaId,
    $core.String? format,
    $core.String? voiceUrl,
    $core.int? length,
    $core.bool? played,
  }) {
    final _result = create();
    if (mediaId != null) {
      _result.mediaId = mediaId;
    }
    if (format != null) {
      _result.format = format;
    }
    if (voiceUrl != null) {
      _result.voiceUrl = voiceUrl;
    }
    if (length != null) {
      _result.length = length;
    }
    if (played != null) {
      _result.played = played;
    }
    return _result;
  }
  factory Voice.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Voice.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Voice clone() => Voice()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Voice copyWith(void Function(Voice) updates) =>
      super.copyWith((message) => updates(message as Voice))
          as Voice; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Voice create() => Voice._();
  Voice createEmptyInstance() => create();
  static $pb.PbList<Voice> createRepeated() => $pb.PbList<Voice>();
  @$core.pragma('dart2js:noInline')
  static Voice getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Voice>(create);
  static Voice? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get mediaId => $_getSZ(0);
  @$pb.TagNumber(1)
  set mediaId($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasMediaId() => $_has(0);
  @$pb.TagNumber(1)
  void clearMediaId() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get format => $_getSZ(1);
  @$pb.TagNumber(2)
  set format($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(2)
  $core.bool hasFormat() => $_has(1);
  @$pb.TagNumber(2)
  void clearFormat() => clearField(2);

  @$pb.TagNumber(3)
  $core.String get voiceUrl => $_getSZ(2);
  @$pb.TagNumber(3)
  set voiceUrl($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasVoiceUrl() => $_has(2);
  @$pb.TagNumber(3)
  void clearVoiceUrl() => clearField(3);

  @$pb.TagNumber(4)
  $core.int get length => $_getIZ(3);
  @$pb.TagNumber(4)
  set length($core.int v) {
    $_setSignedInt32(3, v);
  }

  @$pb.TagNumber(4)
  $core.bool hasLength() => $_has(3);
  @$pb.TagNumber(4)
  void clearLength() => clearField(4);

  @$pb.TagNumber(5)
  $core.bool get played => $_getBF(4);
  @$pb.TagNumber(5)
  set played($core.bool v) {
    $_setBool(4, v);
  }

  @$pb.TagNumber(5)
  $core.bool hasPlayed() => $_has(4);
  @$pb.TagNumber(5)
  void clearPlayed() => clearField(5);
}

class Video extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Video',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'mediaId',
        protoName: 'mediaId')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'thumbMediaId',
        protoName: 'thumbMediaId')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'videoOrShortUrl',
        protoName: 'videoOrShortUrl')
    ..aOS(
        4,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'videoOrShortThumbUrl',
        protoName: 'videoOrShortThumbUrl')
    ..hasRequiredFields = false;

  Video._() : super();
  factory Video({
    $core.String? mediaId,
    $core.String? thumbMediaId,
    $core.String? videoOrShortUrl,
    $core.String? videoOrShortThumbUrl,
  }) {
    final _result = create();
    if (mediaId != null) {
      _result.mediaId = mediaId;
    }
    if (thumbMediaId != null) {
      _result.thumbMediaId = thumbMediaId;
    }
    if (videoOrShortUrl != null) {
      _result.videoOrShortUrl = videoOrShortUrl;
    }
    if (videoOrShortThumbUrl != null) {
      _result.videoOrShortThumbUrl = videoOrShortThumbUrl;
    }
    return _result;
  }
  factory Video.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Video.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Video clone() => Video()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Video copyWith(void Function(Video) updates) =>
      super.copyWith((message) => updates(message as Video))
          as Video; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Video create() => Video._();
  Video createEmptyInstance() => create();
  static $pb.PbList<Video> createRepeated() => $pb.PbList<Video>();
  @$core.pragma('dart2js:noInline')
  static Video getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Video>(create);
  static Video? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get mediaId => $_getSZ(0);
  @$pb.TagNumber(1)
  set mediaId($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasMediaId() => $_has(0);
  @$pb.TagNumber(1)
  void clearMediaId() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get thumbMediaId => $_getSZ(1);
  @$pb.TagNumber(2)
  set thumbMediaId($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(2)
  $core.bool hasThumbMediaId() => $_has(1);
  @$pb.TagNumber(2)
  void clearThumbMediaId() => clearField(2);

  @$pb.TagNumber(3)
  $core.String get videoOrShortUrl => $_getSZ(2);
  @$pb.TagNumber(3)
  set videoOrShortUrl($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasVideoOrShortUrl() => $_has(2);
  @$pb.TagNumber(3)
  void clearVideoOrShortUrl() => clearField(3);

  @$pb.TagNumber(4)
  $core.String get videoOrShortThumbUrl => $_getSZ(3);
  @$pb.TagNumber(4)
  set videoOrShortThumbUrl($core.String v) {
    $_setString(3, v);
  }

  @$pb.TagNumber(4)
  $core.bool hasVideoOrShortThumbUrl() => $_has(3);
  @$pb.TagNumber(4)
  void clearVideoOrShortThumbUrl() => clearField(4);
}

class Location extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Location',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'locationX',
        protoName: 'locationX')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'locationY',
        protoName: 'locationY')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'scale')
    ..aOS(
        4,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'label')
    ..hasRequiredFields = false;

  Location._() : super();
  factory Location({
    $core.String? locationX,
    $core.String? locationY,
    $core.String? scale,
    $core.String? label,
  }) {
    final _result = create();
    if (locationX != null) {
      _result.locationX = locationX;
    }
    if (locationY != null) {
      _result.locationY = locationY;
    }
    if (scale != null) {
      _result.scale = scale;
    }
    if (label != null) {
      _result.label = label;
    }
    return _result;
  }
  factory Location.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Location.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Location clone() => Location()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Location copyWith(void Function(Location) updates) =>
      super.copyWith((message) => updates(message as Location))
          as Location; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Location create() => Location._();
  Location createEmptyInstance() => create();
  static $pb.PbList<Location> createRepeated() => $pb.PbList<Location>();
  @$core.pragma('dart2js:noInline')
  static Location getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Location>(create);
  static Location? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get locationX => $_getSZ(0);
  @$pb.TagNumber(1)
  set locationX($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasLocationX() => $_has(0);
  @$pb.TagNumber(1)
  void clearLocationX() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get locationY => $_getSZ(1);
  @$pb.TagNumber(2)
  set locationY($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(2)
  $core.bool hasLocationY() => $_has(1);
  @$pb.TagNumber(2)
  void clearLocationY() => clearField(2);

  @$pb.TagNumber(3)
  $core.String get scale => $_getSZ(2);
  @$pb.TagNumber(3)
  set scale($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasScale() => $_has(2);
  @$pb.TagNumber(3)
  void clearScale() => clearField(3);

  @$pb.TagNumber(4)
  $core.String get label => $_getSZ(3);
  @$pb.TagNumber(4)
  set label($core.String v) {
    $_setString(3, v);
  }

  @$pb.TagNumber(4)
  $core.bool hasLabel() => $_has(3);
  @$pb.TagNumber(4)
  void clearLabel() => clearField(4);
}

class Link extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Link',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'title')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'description')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'url')
    ..hasRequiredFields = false;

  Link._() : super();
  factory Link({
    $core.String? title,
    $core.String? description,
    $core.String? url,
  }) {
    final _result = create();
    if (title != null) {
      _result.title = title;
    }
    if (description != null) {
      _result.description = description;
    }
    if (url != null) {
      _result.url = url;
    }
    return _result;
  }
  factory Link.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Link.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Link clone() => Link()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Link copyWith(void Function(Link) updates) =>
      super.copyWith((message) => updates(message as Link))
          as Link; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Link create() => Link._();
  Link createEmptyInstance() => create();
  static $pb.PbList<Link> createRepeated() => $pb.PbList<Link>();
  @$core.pragma('dart2js:noInline')
  static Link getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Link>(create);
  static Link? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get title => $_getSZ(0);
  @$pb.TagNumber(1)
  set title($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasTitle() => $_has(0);
  @$pb.TagNumber(1)
  void clearTitle() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get description => $_getSZ(1);
  @$pb.TagNumber(2)
  set description($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(2)
  $core.bool hasDescription() => $_has(1);
  @$pb.TagNumber(2)
  void clearDescription() => clearField(2);

  @$pb.TagNumber(3)
  $core.String get url => $_getSZ(2);
  @$pb.TagNumber(3)
  set url($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasUrl() => $_has(2);
  @$pb.TagNumber(3)
  void clearUrl() => clearField(3);
}

class Receipt extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Receipt',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'mid')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'status')
    ..hasRequiredFields = false;

  Receipt._() : super();
  factory Receipt({
    $core.String? mid,
    $core.String? status,
  }) {
    final _result = create();
    if (mid != null) {
      _result.mid = mid;
    }
    if (status != null) {
      _result.status = status;
    }
    return _result;
  }
  factory Receipt.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Receipt.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Receipt clone() => Receipt()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Receipt copyWith(void Function(Receipt) updates) =>
      super.copyWith((message) => updates(message as Receipt))
          as Receipt; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Receipt create() => Receipt._();
  Receipt createEmptyInstance() => create();
  static $pb.PbList<Receipt> createRepeated() => $pb.PbList<Receipt>();
  @$core.pragma('dart2js:noInline')
  static Receipt getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Receipt>(create);
  static Receipt? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get mid => $_getSZ(0);
  @$pb.TagNumber(1)
  set mid($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasMid() => $_has(0);
  @$pb.TagNumber(1)
  void clearMid() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get status => $_getSZ(1);
  @$pb.TagNumber(2)
  set status($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(2)
  $core.bool hasStatus() => $_has(1);
  @$pb.TagNumber(2)
  void clearStatus() => clearField(2);
}

class Reply extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Reply',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'mid')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'content')
    ..hasRequiredFields = false;

  Reply._() : super();
  factory Reply({
    $core.String? mid,
    $core.String? content,
  }) {
    final _result = create();
    if (mid != null) {
      _result.mid = mid;
    }
    if (content != null) {
      _result.content = content;
    }
    return _result;
  }
  factory Reply.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Reply.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Reply clone() => Reply()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Reply copyWith(void Function(Reply) updates) =>
      super.copyWith((message) => updates(message as Reply))
          as Reply; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Reply create() => Reply._();
  Reply createEmptyInstance() => create();
  static $pb.PbList<Reply> createRepeated() => $pb.PbList<Reply>();
  @$core.pragma('dart2js:noInline')
  static Reply getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Reply>(create);
  static Reply? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get mid => $_getSZ(0);
  @$pb.TagNumber(1)
  set mid($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasMid() => $_has(0);
  @$pb.TagNumber(1)
  void clearMid() => clearField(1);

  @$pb.TagNumber(2)
  $core.String get content => $_getSZ(1);
  @$pb.TagNumber(2)
  set content($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(2)
  $core.bool hasContent() => $_has(1);
  @$pb.TagNumber(2)
  void clearContent() => clearField(2);
}

class Preview extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Preview',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'content')
    ..hasRequiredFields = false;

  Preview._() : super();
  factory Preview({
    $core.String? content,
  }) {
    final _result = create();
    if (content != null) {
      _result.content = content;
    }
    return _result;
  }
  factory Preview.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Preview.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Preview clone() => Preview()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Preview copyWith(void Function(Preview) updates) =>
      super.copyWith((message) => updates(message as Preview))
          as Preview; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Preview create() => Preview._();
  Preview createEmptyInstance() => create();
  static $pb.PbList<Preview> createRepeated() => $pb.PbList<Preview>();
  @$core.pragma('dart2js:noInline')
  static Preview getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Preview>(create);
  static Preview? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get content => $_getSZ(0);
  @$pb.TagNumber(1)
  set content($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasContent() => $_has(0);
  @$pb.TagNumber(1)
  void clearContent() => clearField(1);
}

class Recall extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Recall',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'mid')
    ..hasRequiredFields = false;

  Recall._() : super();
  factory Recall({
    $core.String? mid,
  }) {
    final _result = create();
    if (mid != null) {
      _result.mid = mid;
    }
    return _result;
  }
  factory Recall.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Recall.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Recall clone() => Recall()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Recall copyWith(void Function(Recall) updates) =>
      super.copyWith((message) => updates(message as Recall))
          as Recall; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Recall create() => Recall._();
  Recall createEmptyInstance() => create();
  static $pb.PbList<Recall> createRepeated() => $pb.PbList<Recall>();
  @$core.pragma('dart2js:noInline')
  static Recall getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Recall>(create);
  static Recall? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get mid => $_getSZ(0);
  @$pb.TagNumber(1)
  set mid($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasMid() => $_has(0);
  @$pb.TagNumber(1)
  void clearMid() => clearField(1);
}

class Transfer extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Transfer',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'topic')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'type')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'content')
    ..aOB(
        4,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'accept')
    ..hasRequiredFields = false;

  Transfer._() : super();
  factory Transfer({
    $core.String? topic,
    $core.String? type,
    $core.String? content,
    $core.bool? accept,
  }) {
    final _result = create();
    if (topic != null) {
      _result.topic = topic;
    }
    if (type != null) {
      _result.type = type;
    }
    if (content != null) {
      _result.content = content;
    }
    if (accept != null) {
      _result.accept = accept;
    }
    return _result;
  }
  factory Transfer.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Transfer.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Transfer clone() => Transfer()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Transfer copyWith(void Function(Transfer) updates) =>
      super.copyWith((message) => updates(message as Transfer))
          as Transfer; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Transfer create() => Transfer._();
  Transfer createEmptyInstance() => create();
  static $pb.PbList<Transfer> createRepeated() => $pb.PbList<Transfer>();
  @$core.pragma('dart2js:noInline')
  static Transfer getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Transfer>(create);
  static Transfer? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get topic => $_getSZ(0);
  @$pb.TagNumber(1)
  set topic($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasTopic() => $_has(0);
  @$pb.TagNumber(1)
  void clearTopic() => clearField(1);

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

  @$pb.TagNumber(3)
  $core.String get content => $_getSZ(2);
  @$pb.TagNumber(3)
  set content($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasContent() => $_has(2);
  @$pb.TagNumber(3)
  void clearContent() => clearField(3);

  @$pb.TagNumber(4)
  $core.bool get accept => $_getBF(3);
  @$pb.TagNumber(4)
  set accept($core.bool v) {
    $_setBool(3, v);
  }

  @$pb.TagNumber(4)
  $core.bool hasAccept() => $_has(3);
  @$pb.TagNumber(4)
  void clearAccept() => clearField(4);
}

class Invite extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Invite',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'topic')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'type')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'content')
    ..aOB(
        4,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'accept')
    ..hasRequiredFields = false;

  Invite._() : super();
  factory Invite({
    $core.String? topic,
    $core.String? type,
    $core.String? content,
    $core.bool? accept,
  }) {
    final _result = create();
    if (topic != null) {
      _result.topic = topic;
    }
    if (type != null) {
      _result.type = type;
    }
    if (content != null) {
      _result.content = content;
    }
    if (accept != null) {
      _result.accept = accept;
    }
    return _result;
  }
  factory Invite.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Invite.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Invite clone() => Invite()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Invite copyWith(void Function(Invite) updates) =>
      super.copyWith((message) => updates(message as Invite))
          as Invite; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Invite create() => Invite._();
  Invite createEmptyInstance() => create();
  static $pb.PbList<Invite> createRepeated() => $pb.PbList<Invite>();
  @$core.pragma('dart2js:noInline')
  static Invite getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Invite>(create);
  static Invite? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get topic => $_getSZ(0);
  @$pb.TagNumber(1)
  set topic($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasTopic() => $_has(0);
  @$pb.TagNumber(1)
  void clearTopic() => clearField(1);

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

  @$pb.TagNumber(3)
  $core.String get content => $_getSZ(2);
  @$pb.TagNumber(3)
  set content($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasContent() => $_has(2);
  @$pb.TagNumber(3)
  void clearContent() => clearField(3);

  @$pb.TagNumber(4)
  $core.bool get accept => $_getBF(3);
  @$pb.TagNumber(4)
  set accept($core.bool v) {
    $_setBool(3, v);
  }

  @$pb.TagNumber(4)
  $core.bool hasAccept() => $_has(3);
  @$pb.TagNumber(4)
  void clearAccept() => clearField(4);
}

class Notice extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Notice',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'topic')
    ..aOS(
        2,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'type')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'content')
    ..hasRequiredFields = false;

  Notice._() : super();
  factory Notice({
    $core.String? topic,
    $core.String? type,
    $core.String? content,
  }) {
    final _result = create();
    if (topic != null) {
      _result.topic = topic;
    }
    if (type != null) {
      _result.type = type;
    }
    if (content != null) {
      _result.content = content;
    }
    return _result;
  }
  factory Notice.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Notice.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Notice clone() => Notice()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Notice copyWith(void Function(Notice) updates) =>
      super.copyWith((message) => updates(message as Notice))
          as Notice; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Notice create() => Notice._();
  Notice createEmptyInstance() => create();
  static $pb.PbList<Notice> createRepeated() => $pb.PbList<Notice>();
  @$core.pragma('dart2js:noInline')
  static Notice getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Notice>(create);
  static Notice? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get topic => $_getSZ(0);
  @$pb.TagNumber(1)
  set topic($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasTopic() => $_has(0);
  @$pb.TagNumber(1)
  void clearTopic() => clearField(1);

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

  @$pb.TagNumber(3)
  $core.String get content => $_getSZ(2);
  @$pb.TagNumber(3)
  set content($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasContent() => $_has(2);
  @$pb.TagNumber(3)
  void clearContent() => clearField(3);
}

class Extra extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Extra',
      createEmptyInstance: create)
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'content')
    ..hasRequiredFields = false;

  Extra._() : super();
  factory Extra({
    $core.String? content,
  }) {
    final _result = create();
    if (content != null) {
      _result.content = content;
    }
    return _result;
  }
  factory Extra.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Extra.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Extra clone() => Extra()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Extra copyWith(void Function(Extra) updates) =>
      super.copyWith((message) => updates(message as Extra))
          as Extra; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Extra create() => Extra._();
  Extra createEmptyInstance() => create();
  static $pb.PbList<Extra> createRepeated() => $pb.PbList<Extra>();
  @$core.pragma('dart2js:noInline')
  static Extra getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Extra>(create);
  static Extra? _defaultInstance;

  @$pb.TagNumber(1)
  $core.String get content => $_getSZ(0);
  @$pb.TagNumber(1)
  set content($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasContent() => $_has(0);
  @$pb.TagNumber(1)
  void clearContent() => clearField(1);
}

enum Message_Body {
  text,
  image,
  file,
  voice,
  video,
  location,
  link,
  receipt,
  reply,
  preview,
  recall,
  transfer,
  invite,
  notice,
  extra,
  notSet
}

class Message extends $pb.GeneratedMessage {
  static const $core.Map<$core.int, Message_Body> _Message_BodyByTag = {
    10: Message_Body.text,
    11: Message_Body.image,
    12: Message_Body.file,
    13: Message_Body.voice,
    14: Message_Body.video,
    15: Message_Body.location,
    16: Message_Body.link,
    17: Message_Body.receipt,
    18: Message_Body.reply,
    19: Message_Body.preview,
    20: Message_Body.recall,
    21: Message_Body.transfer,
    22: Message_Body.invite,
    23: Message_Body.notice,
    30: Message_Body.extra,
    0: Message_Body.notSet
  };
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'Message',
      createEmptyInstance: create)
    ..oo(0, [10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 30])
    ..aOS(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'mid')
    ..aOS(
        3,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'status')
    ..aOS(
        4,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'timestamp')
    ..aOS(
        6,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'client')
    ..aOS(
        7,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'version')
    ..aOS(
        8,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'type')
    ..aOM<$0.User>(
        9,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'user',
        subBuilder: $0.User.create)
    ..aOM<Text>(
        10,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'text',
        subBuilder: Text.create)
    ..aOM<Image>(
        11,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'image',
        subBuilder: Image.create)
    ..aOM<File>(
        12,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'file',
        subBuilder: File.create)
    ..aOM<Voice>(
        13,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'voice',
        subBuilder: Voice.create)
    ..aOM<Video>(
        14,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'video',
        subBuilder: Video.create)
    ..aOM<Location>(
        15,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'location',
        subBuilder: Location.create)
    ..aOM<Link>(
        16,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'link',
        subBuilder: Link.create)
    ..aOM<Receipt>(
        17,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'receipt',
        subBuilder: Receipt.create)
    ..aOM<Reply>(
        18,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'reply',
        subBuilder: Reply.create)
    ..aOM<Preview>(
        19,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'preview',
        subBuilder: Preview.create)
    ..aOM<Recall>(
        20,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'recall',
        subBuilder: Recall.create)
    ..aOM<Transfer>(
        21,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'transfer',
        subBuilder: Transfer.create)
    ..aOM<Invite>(
        22,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'invite',
        subBuilder: Invite.create)
    ..aOM<Notice>(
        23,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'notice',
        subBuilder: Notice.create)
    ..aOM<Extra>(
        30,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'extra',
        subBuilder: Extra.create)
    ..aOM<$1.Thread>(
        31,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'thread',
        subBuilder: $1.Thread.create)
    ..aOB(
        32,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'encrypted')
    ..hasRequiredFields = false;

  Message._() : super();
  factory Message({
    $core.String? mid,
    $core.String? status,
    $core.String? timestamp,
    $core.String? client,
    $core.String? version,
    $core.String? type,
    $0.User? user,
    Text? text,
    Image? image,
    File? file,
    Voice? voice,
    Video? video,
    Location? location,
    Link? link,
    Receipt? receipt,
    Reply? reply,
    Preview? preview,
    Recall? recall,
    Transfer? transfer,
    Invite? invite,
    Notice? notice,
    Extra? extra,
    $1.Thread? thread,
    $core.bool? encrypted,
  }) {
    final _result = create();
    if (mid != null) {
      _result.mid = mid;
    }
    if (status != null) {
      _result.status = status;
    }
    if (timestamp != null) {
      _result.timestamp = timestamp;
    }
    if (client != null) {
      _result.client = client;
    }
    if (version != null) {
      _result.version = version;
    }
    if (type != null) {
      _result.type = type;
    }
    if (user != null) {
      _result.user = user;
    }
    if (text != null) {
      _result.text = text;
    }
    if (image != null) {
      _result.image = image;
    }
    if (file != null) {
      _result.file = file;
    }
    if (voice != null) {
      _result.voice = voice;
    }
    if (video != null) {
      _result.video = video;
    }
    if (location != null) {
      _result.location = location;
    }
    if (link != null) {
      _result.link = link;
    }
    if (receipt != null) {
      _result.receipt = receipt;
    }
    if (reply != null) {
      _result.reply = reply;
    }
    if (preview != null) {
      _result.preview = preview;
    }
    if (recall != null) {
      _result.recall = recall;
    }
    if (transfer != null) {
      _result.transfer = transfer;
    }
    if (invite != null) {
      _result.invite = invite;
    }
    if (notice != null) {
      _result.notice = notice;
    }
    if (extra != null) {
      _result.extra = extra;
    }
    if (thread != null) {
      _result.thread = thread;
    }
    if (encrypted != null) {
      _result.encrypted = encrypted;
    }
    return _result;
  }
  factory Message.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory Message.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  Message clone() => Message()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  Message copyWith(void Function(Message) updates) =>
      super.copyWith((message) => updates(message as Message))
          as Message; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static Message create() => Message._();
  Message createEmptyInstance() => create();
  static $pb.PbList<Message> createRepeated() => $pb.PbList<Message>();
  @$core.pragma('dart2js:noInline')
  static Message getDefault() =>
      _defaultInstance ??= $pb.GeneratedMessage.$_defaultFor<Message>(create);
  static Message? _defaultInstance;

  Message_Body whichBody() => _Message_BodyByTag[$_whichOneof(0)]!;
  void clearBody() => clearField($_whichOneof(0));

  @$pb.TagNumber(1)
  $core.String get mid => $_getSZ(0);
  @$pb.TagNumber(1)
  set mid($core.String v) {
    $_setString(0, v);
  }

  @$pb.TagNumber(1)
  $core.bool hasMid() => $_has(0);
  @$pb.TagNumber(1)
  void clearMid() => clearField(1);

  @$pb.TagNumber(3)
  $core.String get status => $_getSZ(1);
  @$pb.TagNumber(3)
  set status($core.String v) {
    $_setString(1, v);
  }

  @$pb.TagNumber(3)
  $core.bool hasStatus() => $_has(1);
  @$pb.TagNumber(3)
  void clearStatus() => clearField(3);

  @$pb.TagNumber(4)
  $core.String get timestamp => $_getSZ(2);
  @$pb.TagNumber(4)
  set timestamp($core.String v) {
    $_setString(2, v);
  }

  @$pb.TagNumber(4)
  $core.bool hasTimestamp() => $_has(2);
  @$pb.TagNumber(4)
  void clearTimestamp() => clearField(4);

  @$pb.TagNumber(6)
  $core.String get client => $_getSZ(3);
  @$pb.TagNumber(6)
  set client($core.String v) {
    $_setString(3, v);
  }

  @$pb.TagNumber(6)
  $core.bool hasClient() => $_has(3);
  @$pb.TagNumber(6)
  void clearClient() => clearField(6);

  @$pb.TagNumber(7)
  $core.String get version => $_getSZ(4);
  @$pb.TagNumber(7)
  set version($core.String v) {
    $_setString(4, v);
  }

  @$pb.TagNumber(7)
  $core.bool hasVersion() => $_has(4);
  @$pb.TagNumber(7)
  void clearVersion() => clearField(7);

  @$pb.TagNumber(8)
  $core.String get type => $_getSZ(5);
  @$pb.TagNumber(8)
  set type($core.String v) {
    $_setString(5, v);
  }

  @$pb.TagNumber(8)
  $core.bool hasType() => $_has(5);
  @$pb.TagNumber(8)
  void clearType() => clearField(8);

  @$pb.TagNumber(9)
  $0.User get user => $_getN(6);
  @$pb.TagNumber(9)
  set user($0.User v) {
    setField(9, v);
  }

  @$pb.TagNumber(9)
  $core.bool hasUser() => $_has(6);
  @$pb.TagNumber(9)
  void clearUser() => clearField(9);
  @$pb.TagNumber(9)
  $0.User ensureUser() => $_ensure(6);

  @$pb.TagNumber(10)
  Text get text => $_getN(7);
  @$pb.TagNumber(10)
  set text(Text v) {
    setField(10, v);
  }

  @$pb.TagNumber(10)
  $core.bool hasText() => $_has(7);
  @$pb.TagNumber(10)
  void clearText() => clearField(10);
  @$pb.TagNumber(10)
  Text ensureText() => $_ensure(7);

  @$pb.TagNumber(11)
  Image get image => $_getN(8);
  @$pb.TagNumber(11)
  set image(Image v) {
    setField(11, v);
  }

  @$pb.TagNumber(11)
  $core.bool hasImage() => $_has(8);
  @$pb.TagNumber(11)
  void clearImage() => clearField(11);
  @$pb.TagNumber(11)
  Image ensureImage() => $_ensure(8);

  @$pb.TagNumber(12)
  File get file => $_getN(9);
  @$pb.TagNumber(12)
  set file(File v) {
    setField(12, v);
  }

  @$pb.TagNumber(12)
  $core.bool hasFile() => $_has(9);
  @$pb.TagNumber(12)
  void clearFile() => clearField(12);
  @$pb.TagNumber(12)
  File ensureFile() => $_ensure(9);

  @$pb.TagNumber(13)
  Voice get voice => $_getN(10);
  @$pb.TagNumber(13)
  set voice(Voice v) {
    setField(13, v);
  }

  @$pb.TagNumber(13)
  $core.bool hasVoice() => $_has(10);
  @$pb.TagNumber(13)
  void clearVoice() => clearField(13);
  @$pb.TagNumber(13)
  Voice ensureVoice() => $_ensure(10);

  @$pb.TagNumber(14)
  Video get video => $_getN(11);
  @$pb.TagNumber(14)
  set video(Video v) {
    setField(14, v);
  }

  @$pb.TagNumber(14)
  $core.bool hasVideo() => $_has(11);
  @$pb.TagNumber(14)
  void clearVideo() => clearField(14);
  @$pb.TagNumber(14)
  Video ensureVideo() => $_ensure(11);

  @$pb.TagNumber(15)
  Location get location => $_getN(12);
  @$pb.TagNumber(15)
  set location(Location v) {
    setField(15, v);
  }

  @$pb.TagNumber(15)
  $core.bool hasLocation() => $_has(12);
  @$pb.TagNumber(15)
  void clearLocation() => clearField(15);
  @$pb.TagNumber(15)
  Location ensureLocation() => $_ensure(12);

  @$pb.TagNumber(16)
  Link get link => $_getN(13);
  @$pb.TagNumber(16)
  set link(Link v) {
    setField(16, v);
  }

  @$pb.TagNumber(16)
  $core.bool hasLink() => $_has(13);
  @$pb.TagNumber(16)
  void clearLink() => clearField(16);
  @$pb.TagNumber(16)
  Link ensureLink() => $_ensure(13);

  @$pb.TagNumber(17)
  Receipt get receipt => $_getN(14);
  @$pb.TagNumber(17)
  set receipt(Receipt v) {
    setField(17, v);
  }

  @$pb.TagNumber(17)
  $core.bool hasReceipt() => $_has(14);
  @$pb.TagNumber(17)
  void clearReceipt() => clearField(17);
  @$pb.TagNumber(17)
  Receipt ensureReceipt() => $_ensure(14);

  @$pb.TagNumber(18)
  Reply get reply => $_getN(15);
  @$pb.TagNumber(18)
  set reply(Reply v) {
    setField(18, v);
  }

  @$pb.TagNumber(18)
  $core.bool hasReply() => $_has(15);
  @$pb.TagNumber(18)
  void clearReply() => clearField(18);
  @$pb.TagNumber(18)
  Reply ensureReply() => $_ensure(15);

  @$pb.TagNumber(19)
  Preview get preview => $_getN(16);
  @$pb.TagNumber(19)
  set preview(Preview v) {
    setField(19, v);
  }

  @$pb.TagNumber(19)
  $core.bool hasPreview() => $_has(16);
  @$pb.TagNumber(19)
  void clearPreview() => clearField(19);
  @$pb.TagNumber(19)
  Preview ensurePreview() => $_ensure(16);

  @$pb.TagNumber(20)
  Recall get recall => $_getN(17);
  @$pb.TagNumber(20)
  set recall(Recall v) {
    setField(20, v);
  }

  @$pb.TagNumber(20)
  $core.bool hasRecall() => $_has(17);
  @$pb.TagNumber(20)
  void clearRecall() => clearField(20);
  @$pb.TagNumber(20)
  Recall ensureRecall() => $_ensure(17);

  @$pb.TagNumber(21)
  Transfer get transfer => $_getN(18);
  @$pb.TagNumber(21)
  set transfer(Transfer v) {
    setField(21, v);
  }

  @$pb.TagNumber(21)
  $core.bool hasTransfer() => $_has(18);
  @$pb.TagNumber(21)
  void clearTransfer() => clearField(21);
  @$pb.TagNumber(21)
  Transfer ensureTransfer() => $_ensure(18);

  @$pb.TagNumber(22)
  Invite get invite => $_getN(19);
  @$pb.TagNumber(22)
  set invite(Invite v) {
    setField(22, v);
  }

  @$pb.TagNumber(22)
  $core.bool hasInvite() => $_has(19);
  @$pb.TagNumber(22)
  void clearInvite() => clearField(22);
  @$pb.TagNumber(22)
  Invite ensureInvite() => $_ensure(19);

  @$pb.TagNumber(23)
  Notice get notice => $_getN(20);
  @$pb.TagNumber(23)
  set notice(Notice v) {
    setField(23, v);
  }

  @$pb.TagNumber(23)
  $core.bool hasNotice() => $_has(20);
  @$pb.TagNumber(23)
  void clearNotice() => clearField(23);
  @$pb.TagNumber(23)
  Notice ensureNotice() => $_ensure(20);

  @$pb.TagNumber(30)
  Extra get extra => $_getN(21);
  @$pb.TagNumber(30)
  set extra(Extra v) {
    setField(30, v);
  }

  @$pb.TagNumber(30)
  $core.bool hasExtra() => $_has(21);
  @$pb.TagNumber(30)
  void clearExtra() => clearField(30);
  @$pb.TagNumber(30)
  Extra ensureExtra() => $_ensure(21);

  @$pb.TagNumber(31)
  $1.Thread get thread => $_getN(22);
  @$pb.TagNumber(31)
  set thread($1.Thread v) {
    setField(31, v);
  }

  @$pb.TagNumber(31)
  $core.bool hasThread() => $_has(22);
  @$pb.TagNumber(31)
  void clearThread() => clearField(31);
  @$pb.TagNumber(31)
  $1.Thread ensureThread() => $_ensure(22);

  @$pb.TagNumber(32)
  $core.bool get encrypted => $_getBF(23);
  @$pb.TagNumber(32)
  set encrypted($core.bool v) {
    $_setBool(23, v);
  }

  @$pb.TagNumber(32)
  $core.bool hasEncrypted() => $_has(23);
  @$pb.TagNumber(32)
  void clearEncrypted() => clearField(32);
}

class MessageList extends $pb.GeneratedMessage {
  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      const $core.bool.fromEnvironment('protobuf.omit_message_names')
          ? ''
          : 'MessageList',
      createEmptyInstance: create)
    ..pc<Message>(
        1,
        const $core.bool.fromEnvironment('protobuf.omit_field_names')
            ? ''
            : 'list',
        $pb.PbFieldType.PM,
        subBuilder: Message.create)
    ..hasRequiredFields = false;

  MessageList._() : super();
  factory MessageList({
    $core.Iterable<Message>? list,
  }) {
    final _result = create();
    if (list != null) {
      _result.list.addAll(list);
    }
    return _result;
  }
  factory MessageList.fromBuffer($core.List<$core.int> i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(i, r);
  factory MessageList.fromJson($core.String i,
          [$pb.ExtensionRegistry r = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(i, r);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.deepCopy] instead. '
      'Will be removed in next major version')
  MessageList clone() => MessageList()..mergeFromMessage(this);
  @$core.Deprecated('Using this can add significant overhead to your binary. '
      'Use [GeneratedMessageGenericExtensions.rebuild] instead. '
      'Will be removed in next major version')
  MessageList copyWith(void Function(MessageList) updates) =>
      super.copyWith((message) => updates(message as MessageList))
          as MessageList; // ignore: deprecated_member_use
  $pb.BuilderInfo get info_ => _i;
  @$core.pragma('dart2js:noInline')
  static MessageList create() => MessageList._();
  MessageList createEmptyInstance() => create();
  static $pb.PbList<MessageList> createRepeated() => $pb.PbList<MessageList>();
  @$core.pragma('dart2js:noInline')
  static MessageList getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<MessageList>(create);
  static MessageList? _defaultInstance;

  @$pb.TagNumber(1)
  $core.List<Message> get list => $_getList(0);
}
