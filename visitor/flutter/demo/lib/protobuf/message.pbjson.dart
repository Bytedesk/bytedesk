///
//  Generated code. Do not modify.
//  source: message.proto
//
// @dart = 2.12
// ignore_for_file: annotate_overrides,camel_case_types,unnecessary_const,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name,return_of_invalid_type,unnecessary_this,prefer_final_fields,deprecated_member_use_from_same_package

import 'dart:core' as $core;
import 'dart:convert' as $convert;
import 'dart:typed_data' as $typed_data;

@$core.Deprecated('Use textDescriptor instead')
const Text$json = const {
  '1': 'Text',
  '2': const [
    const {'1': 'content', '3': 1, '4': 1, '5': 9, '10': 'content'},
  ],
};

/// Descriptor for `Text`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List textDescriptor =
    $convert.base64Decode('CgRUZXh0EhgKB2NvbnRlbnQYASABKAlSB2NvbnRlbnQ=');
@$core.Deprecated('Use imageDescriptor instead')
const Image$json = const {
  '1': 'Image',
  '2': const [
    const {'1': 'mediaId', '3': 1, '4': 1, '5': 9, '10': 'mediaId'},
    const {'1': 'picUrl', '3': 2, '4': 1, '5': 9, '10': 'picUrl'},
    const {'1': 'imageUrl', '3': 3, '4': 1, '5': 9, '10': 'imageUrl'},
  ],
};

/// Descriptor for `Image`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List imageDescriptor = $convert.base64Decode(
    'CgVJbWFnZRIYCgdtZWRpYUlkGAEgASgJUgdtZWRpYUlkEhYKBnBpY1VybBgCIAEoCVIGcGljVXJsEhoKCGltYWdlVXJsGAMgASgJUghpbWFnZVVybA==');
@$core.Deprecated('Use fileDescriptor instead')
const File$json = const {
  '1': 'File',
  '2': const [
    const {'1': 'fileUrl', '3': 1, '4': 1, '5': 9, '10': 'fileUrl'},
    const {'1': 'fileName', '3': 2, '4': 1, '5': 9, '10': 'fileName'},
    const {'1': 'fileSize', '3': 3, '4': 1, '5': 9, '10': 'fileSize'},
  ],
};

/// Descriptor for `File`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List fileDescriptor = $convert.base64Decode(
    'CgRGaWxlEhgKB2ZpbGVVcmwYASABKAlSB2ZpbGVVcmwSGgoIZmlsZU5hbWUYAiABKAlSCGZpbGVOYW1lEhoKCGZpbGVTaXplGAMgASgJUghmaWxlU2l6ZQ==');
@$core.Deprecated('Use voiceDescriptor instead')
const Voice$json = const {
  '1': 'Voice',
  '2': const [
    const {'1': 'mediaId', '3': 1, '4': 1, '5': 9, '10': 'mediaId'},
    const {'1': 'format', '3': 2, '4': 1, '5': 9, '10': 'format'},
    const {'1': 'voiceUrl', '3': 3, '4': 1, '5': 9, '10': 'voiceUrl'},
    const {'1': 'length', '3': 4, '4': 1, '5': 5, '10': 'length'},
    const {'1': 'played', '3': 5, '4': 1, '5': 8, '10': 'played'},
  ],
};

/// Descriptor for `Voice`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List voiceDescriptor = $convert.base64Decode(
    'CgVWb2ljZRIYCgdtZWRpYUlkGAEgASgJUgdtZWRpYUlkEhYKBmZvcm1hdBgCIAEoCVIGZm9ybWF0EhoKCHZvaWNlVXJsGAMgASgJUgh2b2ljZVVybBIWCgZsZW5ndGgYBCABKAVSBmxlbmd0aBIWCgZwbGF5ZWQYBSABKAhSBnBsYXllZA==');
@$core.Deprecated('Use videoDescriptor instead')
const Video$json = const {
  '1': 'Video',
  '2': const [
    const {'1': 'mediaId', '3': 1, '4': 1, '5': 9, '10': 'mediaId'},
    const {'1': 'thumbMediaId', '3': 2, '4': 1, '5': 9, '10': 'thumbMediaId'},
    const {
      '1': 'videoOrShortUrl',
      '3': 3,
      '4': 1,
      '5': 9,
      '10': 'videoOrShortUrl'
    },
    const {
      '1': 'videoOrShortThumbUrl',
      '3': 4,
      '4': 1,
      '5': 9,
      '10': 'videoOrShortThumbUrl'
    },
  ],
};

/// Descriptor for `Video`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List videoDescriptor = $convert.base64Decode(
    'CgVWaWRlbxIYCgdtZWRpYUlkGAEgASgJUgdtZWRpYUlkEiIKDHRodW1iTWVkaWFJZBgCIAEoCVIMdGh1bWJNZWRpYUlkEigKD3ZpZGVvT3JTaG9ydFVybBgDIAEoCVIPdmlkZW9PclNob3J0VXJsEjIKFHZpZGVvT3JTaG9ydFRodW1iVXJsGAQgASgJUhR2aWRlb09yU2hvcnRUaHVtYlVybA==');
@$core.Deprecated('Use locationDescriptor instead')
const Location$json = const {
  '1': 'Location',
  '2': const [
    const {'1': 'locationX', '3': 1, '4': 1, '5': 9, '10': 'locationX'},
    const {'1': 'locationY', '3': 2, '4': 1, '5': 9, '10': 'locationY'},
    const {'1': 'scale', '3': 3, '4': 1, '5': 9, '10': 'scale'},
    const {'1': 'label', '3': 4, '4': 1, '5': 9, '10': 'label'},
  ],
};

/// Descriptor for `Location`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List locationDescriptor = $convert.base64Decode(
    'CghMb2NhdGlvbhIcCglsb2NhdGlvblgYASABKAlSCWxvY2F0aW9uWBIcCglsb2NhdGlvblkYAiABKAlSCWxvY2F0aW9uWRIUCgVzY2FsZRgDIAEoCVIFc2NhbGUSFAoFbGFiZWwYBCABKAlSBWxhYmVs');
@$core.Deprecated('Use linkDescriptor instead')
const Link$json = const {
  '1': 'Link',
  '2': const [
    const {'1': 'title', '3': 1, '4': 1, '5': 9, '10': 'title'},
    const {'1': 'description', '3': 2, '4': 1, '5': 9, '10': 'description'},
    const {'1': 'url', '3': 3, '4': 1, '5': 9, '10': 'url'},
  ],
};

/// Descriptor for `Link`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List linkDescriptor = $convert.base64Decode(
    'CgRMaW5rEhQKBXRpdGxlGAEgASgJUgV0aXRsZRIgCgtkZXNjcmlwdGlvbhgCIAEoCVILZGVzY3JpcHRpb24SEAoDdXJsGAMgASgJUgN1cmw=');
@$core.Deprecated('Use receiptDescriptor instead')
const Receipt$json = const {
  '1': 'Receipt',
  '2': const [
    const {'1': 'mid', '3': 1, '4': 1, '5': 9, '10': 'mid'},
    const {'1': 'status', '3': 2, '4': 1, '5': 9, '10': 'status'},
  ],
};

/// Descriptor for `Receipt`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List receiptDescriptor = $convert.base64Decode(
    'CgdSZWNlaXB0EhAKA21pZBgBIAEoCVIDbWlkEhYKBnN0YXR1cxgCIAEoCVIGc3RhdHVz');
@$core.Deprecated('Use replyDescriptor instead')
const Reply$json = const {
  '1': 'Reply',
  '2': const [
    const {'1': 'mid', '3': 1, '4': 1, '5': 9, '10': 'mid'},
    const {'1': 'content', '3': 2, '4': 1, '5': 9, '10': 'content'},
  ],
};

/// Descriptor for `Reply`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List replyDescriptor = $convert.base64Decode(
    'CgVSZXBseRIQCgNtaWQYASABKAlSA21pZBIYCgdjb250ZW50GAIgASgJUgdjb250ZW50');
@$core.Deprecated('Use previewDescriptor instead')
const Preview$json = const {
  '1': 'Preview',
  '2': const [
    const {'1': 'content', '3': 1, '4': 1, '5': 9, '10': 'content'},
  ],
};

/// Descriptor for `Preview`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List previewDescriptor =
    $convert.base64Decode('CgdQcmV2aWV3EhgKB2NvbnRlbnQYASABKAlSB2NvbnRlbnQ=');
@$core.Deprecated('Use recallDescriptor instead')
const Recall$json = const {
  '1': 'Recall',
  '2': const [
    const {'1': 'mid', '3': 1, '4': 1, '5': 9, '10': 'mid'},
  ],
};

/// Descriptor for `Recall`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List recallDescriptor =
    $convert.base64Decode('CgZSZWNhbGwSEAoDbWlkGAEgASgJUgNtaWQ=');
@$core.Deprecated('Use transferDescriptor instead')
const Transfer$json = const {
  '1': 'Transfer',
  '2': const [
    const {'1': 'topic', '3': 1, '4': 1, '5': 9, '10': 'topic'},
    const {'1': 'type', '3': 2, '4': 1, '5': 9, '10': 'type'},
    const {'1': 'content', '3': 3, '4': 1, '5': 9, '10': 'content'},
    const {'1': 'accept', '3': 4, '4': 1, '5': 8, '10': 'accept'},
  ],
};

/// Descriptor for `Transfer`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List transferDescriptor = $convert.base64Decode(
    'CghUcmFuc2ZlchIUCgV0b3BpYxgBIAEoCVIFdG9waWMSEgoEdHlwZRgCIAEoCVIEdHlwZRIYCgdjb250ZW50GAMgASgJUgdjb250ZW50EhYKBmFjY2VwdBgEIAEoCFIGYWNjZXB0');
@$core.Deprecated('Use inviteDescriptor instead')
const Invite$json = const {
  '1': 'Invite',
  '2': const [
    const {'1': 'topic', '3': 1, '4': 1, '5': 9, '10': 'topic'},
    const {'1': 'type', '3': 2, '4': 1, '5': 9, '10': 'type'},
    const {'1': 'content', '3': 3, '4': 1, '5': 9, '10': 'content'},
    const {'1': 'accept', '3': 4, '4': 1, '5': 8, '10': 'accept'},
  ],
};

/// Descriptor for `Invite`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List inviteDescriptor = $convert.base64Decode(
    'CgZJbnZpdGUSFAoFdG9waWMYASABKAlSBXRvcGljEhIKBHR5cGUYAiABKAlSBHR5cGUSGAoHY29udGVudBgDIAEoCVIHY29udGVudBIWCgZhY2NlcHQYBCABKAhSBmFjY2VwdA==');
@$core.Deprecated('Use noticeDescriptor instead')
const Notice$json = const {
  '1': 'Notice',
  '2': const [
    const {'1': 'topic', '3': 1, '4': 1, '5': 9, '10': 'topic'},
    const {'1': 'type', '3': 2, '4': 1, '5': 9, '10': 'type'},
    const {'1': 'content', '3': 3, '4': 1, '5': 9, '10': 'content'},
  ],
};

/// Descriptor for `Notice`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List noticeDescriptor = $convert.base64Decode(
    'CgZOb3RpY2USFAoFdG9waWMYASABKAlSBXRvcGljEhIKBHR5cGUYAiABKAlSBHR5cGUSGAoHY29udGVudBgDIAEoCVIHY29udGVudA==');
@$core.Deprecated('Use extraDescriptor instead')
const Extra$json = const {
  '1': 'Extra',
  '2': const [
    const {'1': 'content', '3': 1, '4': 1, '5': 9, '10': 'content'},
  ],
};

/// Descriptor for `Extra`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List extraDescriptor =
    $convert.base64Decode('CgVFeHRyYRIYCgdjb250ZW50GAEgASgJUgdjb250ZW50');
@$core.Deprecated('Use messageDescriptor instead')
const Message$json = const {
  '1': 'Message',
  '2': const [
    const {'1': 'mid', '3': 1, '4': 1, '5': 9, '10': 'mid'},
    const {'1': 'status', '3': 3, '4': 1, '5': 9, '10': 'status'},
    const {'1': 'timestamp', '3': 4, '4': 1, '5': 9, '10': 'timestamp'},
    const {'1': 'client', '3': 6, '4': 1, '5': 9, '10': 'client'},
    const {'1': 'version', '3': 7, '4': 1, '5': 9, '10': 'version'},
    const {'1': 'type', '3': 8, '4': 1, '5': 9, '10': 'type'},
    const {'1': 'user', '3': 9, '4': 1, '5': 11, '6': '.User', '10': 'user'},
    const {
      '1': 'text',
      '3': 10,
      '4': 1,
      '5': 11,
      '6': '.Text',
      '9': 0,
      '10': 'text'
    },
    const {
      '1': 'image',
      '3': 11,
      '4': 1,
      '5': 11,
      '6': '.Image',
      '9': 0,
      '10': 'image'
    },
    const {
      '1': 'file',
      '3': 12,
      '4': 1,
      '5': 11,
      '6': '.File',
      '9': 0,
      '10': 'file'
    },
    const {
      '1': 'voice',
      '3': 13,
      '4': 1,
      '5': 11,
      '6': '.Voice',
      '9': 0,
      '10': 'voice'
    },
    const {
      '1': 'video',
      '3': 14,
      '4': 1,
      '5': 11,
      '6': '.Video',
      '9': 0,
      '10': 'video'
    },
    const {
      '1': 'location',
      '3': 15,
      '4': 1,
      '5': 11,
      '6': '.Location',
      '9': 0,
      '10': 'location'
    },
    const {
      '1': 'link',
      '3': 16,
      '4': 1,
      '5': 11,
      '6': '.Link',
      '9': 0,
      '10': 'link'
    },
    const {
      '1': 'receipt',
      '3': 17,
      '4': 1,
      '5': 11,
      '6': '.Receipt',
      '9': 0,
      '10': 'receipt'
    },
    const {
      '1': 'reply',
      '3': 18,
      '4': 1,
      '5': 11,
      '6': '.Reply',
      '9': 0,
      '10': 'reply'
    },
    const {
      '1': 'preview',
      '3': 19,
      '4': 1,
      '5': 11,
      '6': '.Preview',
      '9': 0,
      '10': 'preview'
    },
    const {
      '1': 'recall',
      '3': 20,
      '4': 1,
      '5': 11,
      '6': '.Recall',
      '9': 0,
      '10': 'recall'
    },
    const {
      '1': 'transfer',
      '3': 21,
      '4': 1,
      '5': 11,
      '6': '.Transfer',
      '9': 0,
      '10': 'transfer'
    },
    const {
      '1': 'invite',
      '3': 22,
      '4': 1,
      '5': 11,
      '6': '.Invite',
      '9': 0,
      '10': 'invite'
    },
    const {
      '1': 'notice',
      '3': 23,
      '4': 1,
      '5': 11,
      '6': '.Notice',
      '9': 0,
      '10': 'notice'
    },
    const {
      '1': 'extra',
      '3': 30,
      '4': 1,
      '5': 11,
      '6': '.Extra',
      '9': 0,
      '10': 'extra'
    },
    const {
      '1': 'thread',
      '3': 31,
      '4': 1,
      '5': 11,
      '6': '.Thread',
      '10': 'thread'
    },
    const {'1': 'encrypted', '3': 32, '4': 1, '5': 8, '10': 'encrypted'},
  ],
  '8': const [
    const {'1': 'body'},
  ],
};

/// Descriptor for `Message`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List messageDescriptor = $convert.base64Decode(
    'CgdNZXNzYWdlEhAKA21pZBgBIAEoCVIDbWlkEhYKBnN0YXR1cxgDIAEoCVIGc3RhdHVzEhwKCXRpbWVzdGFtcBgEIAEoCVIJdGltZXN0YW1wEhYKBmNsaWVudBgGIAEoCVIGY2xpZW50EhgKB3ZlcnNpb24YByABKAlSB3ZlcnNpb24SEgoEdHlwZRgIIAEoCVIEdHlwZRIZCgR1c2VyGAkgASgLMgUuVXNlclIEdXNlchIbCgR0ZXh0GAogASgLMgUuVGV4dEgAUgR0ZXh0Eh4KBWltYWdlGAsgASgLMgYuSW1hZ2VIAFIFaW1hZ2USGwoEZmlsZRgMIAEoCzIFLkZpbGVIAFIEZmlsZRIeCgV2b2ljZRgNIAEoCzIGLlZvaWNlSABSBXZvaWNlEh4KBXZpZGVvGA4gASgLMgYuVmlkZW9IAFIFdmlkZW8SJwoIbG9jYXRpb24YDyABKAsyCS5Mb2NhdGlvbkgAUghsb2NhdGlvbhIbCgRsaW5rGBAgASgLMgUuTGlua0gAUgRsaW5rEiQKB3JlY2VpcHQYESABKAsyCC5SZWNlaXB0SABSB3JlY2VpcHQSHgoFcmVwbHkYEiABKAsyBi5SZXBseUgAUgVyZXBseRIkCgdwcmV2aWV3GBMgASgLMgguUHJldmlld0gAUgdwcmV2aWV3EiEKBnJlY2FsbBgUIAEoCzIHLlJlY2FsbEgAUgZyZWNhbGwSJwoIdHJhbnNmZXIYFSABKAsyCS5UcmFuc2ZlckgAUgh0cmFuc2ZlchIhCgZpbnZpdGUYFiABKAsyBy5JbnZpdGVIAFIGaW52aXRlEiEKBm5vdGljZRgXIAEoCzIHLk5vdGljZUgAUgZub3RpY2USHgoFZXh0cmEYHiABKAsyBi5FeHRyYUgAUgVleHRyYRIfCgZ0aHJlYWQYHyABKAsyBy5UaHJlYWRSBnRocmVhZBIcCgllbmNyeXB0ZWQYICABKAhSCWVuY3J5cHRlZEIGCgRib2R5');
@$core.Deprecated('Use messageListDescriptor instead')
const MessageList$json = const {
  '1': 'MessageList',
  '2': const [
    const {'1': 'list', '3': 1, '4': 3, '5': 11, '6': '.Message', '10': 'list'},
  ],
};

/// Descriptor for `MessageList`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List messageListDescriptor = $convert.base64Decode(
    'CgtNZXNzYWdlTGlzdBIcCgRsaXN0GAEgAygLMgguTWVzc2FnZVIEbGlzdA==');
