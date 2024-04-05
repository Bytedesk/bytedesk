///
//  Generated code. Do not modify.
//  source: thread.proto
//
// @dart = 2.12
// ignore_for_file: annotate_overrides,camel_case_types,unnecessary_const,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name,return_of_invalid_type,unnecessary_this,prefer_final_fields,deprecated_member_use_from_same_package

import 'dart:core' as $core;
import 'dart:convert' as $convert;
import 'dart:typed_data' as $typed_data;

@$core.Deprecated('Use threadDescriptor instead')
const Thread$json = const {
  '1': 'Thread',
  '2': const [
    const {'1': 'tid', '3': 1, '4': 1, '5': 9, '10': 'tid'},
    const {'1': 'type', '3': 2, '4': 1, '5': 9, '10': 'type'},
    const {'1': 'client', '3': 5, '4': 1, '5': 9, '10': 'client'},
    const {'1': 'nickname', '3': 6, '4': 1, '5': 9, '10': 'nickname'},
    const {'1': 'avatar', '3': 7, '4': 1, '5': 9, '10': 'avatar'},
    const {'1': 'content', '3': 8, '4': 1, '5': 9, '10': 'content'},
    const {'1': 'timestamp', '3': 9, '4': 1, '5': 9, '10': 'timestamp'},
    const {'1': 'unreadCount', '3': 10, '4': 1, '5': 5, '10': 'unreadCount'},
    const {'1': 'topic', '3': 11, '4': 1, '5': 9, '10': 'topic'},
    const {'1': 'extra', '3': 20, '4': 1, '5': 9, '10': 'extra'},
  ],
};

/// Descriptor for `Thread`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List threadDescriptor = $convert.base64Decode(
    'CgZUaHJlYWQSEAoDdGlkGAEgASgJUgN0aWQSEgoEdHlwZRgCIAEoCVIEdHlwZRIWCgZjbGllbnQYBSABKAlSBmNsaWVudBIaCghuaWNrbmFtZRgGIAEoCVIIbmlja25hbWUSFgoGYXZhdGFyGAcgASgJUgZhdmF0YXISGAoHY29udGVudBgIIAEoCVIHY29udGVudBIcCgl0aW1lc3RhbXAYCSABKAlSCXRpbWVzdGFtcBIgCgt1bnJlYWRDb3VudBgKIAEoBVILdW5yZWFkQ291bnQSFAoFdG9waWMYCyABKAlSBXRvcGljEhQKBWV4dHJhGBQgASgJUgVleHRyYQ==');
@$core.Deprecated('Use threadListDescriptor instead')
const ThreadList$json = const {
  '1': 'ThreadList',
  '2': const [
    const {'1': 'list', '3': 1, '4': 3, '5': 11, '6': '.Thread', '10': 'list'},
  ],
};

/// Descriptor for `ThreadList`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List threadListDescriptor = $convert
    .base64Decode('CgpUaHJlYWRMaXN0EhsKBGxpc3QYASADKAsyBy5UaHJlYWRSBGxpc3Q=');
