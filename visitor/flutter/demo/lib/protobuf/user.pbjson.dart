///
//  Generated code. Do not modify.
//  source: user.proto
//
// @dart = 2.12
// ignore_for_file: annotate_overrides,camel_case_types,unnecessary_const,non_constant_identifier_names,library_prefixes,unused_import,unused_shown_name,return_of_invalid_type,unnecessary_this,prefer_final_fields,deprecated_member_use_from_same_package

import 'dart:core' as $core;
import 'dart:convert' as $convert;
import 'dart:typed_data' as $typed_data;

@$core.Deprecated('Use userDescriptor instead')
const User$json = const {
  '1': 'User',
  '2': const [
    const {'1': 'uid', '3': 1, '4': 1, '5': 9, '10': 'uid'},
    const {'1': 'username', '3': 2, '4': 1, '5': 9, '10': 'username'},
    const {'1': 'nickname', '3': 3, '4': 1, '5': 9, '10': 'nickname'},
    const {'1': 'avatar', '3': 4, '4': 1, '5': 9, '10': 'avatar'},
    const {'1': 'client', '3': 5, '4': 1, '5': 9, '10': 'client'},
    const {'1': 'subDomain', '3': 6, '4': 1, '5': 9, '10': 'subDomain'},
    const {'1': 'extra', '3': 7, '4': 1, '5': 9, '10': 'extra'},
  ],
};

/// Descriptor for `User`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List userDescriptor = $convert.base64Decode(
    'CgRVc2VyEhAKA3VpZBgBIAEoCVIDdWlkEhoKCHVzZXJuYW1lGAIgASgJUgh1c2VybmFtZRIaCghuaWNrbmFtZRgDIAEoCVIIbmlja25hbWUSFgoGYXZhdGFyGAQgASgJUgZhdmF0YXISFgoGY2xpZW50GAUgASgJUgZjbGllbnQSHAoJc3ViRG9tYWluGAYgASgJUglzdWJEb21haW4SFAoFZXh0cmEYByABKAlSBWV4dHJh');
@$core.Deprecated('Use userListDescriptor instead')
const UserList$json = const {
  '1': 'UserList',
  '2': const [
    const {'1': 'list', '3': 1, '4': 3, '5': 11, '6': '.User', '10': 'list'},
  ],
};

/// Descriptor for `UserList`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List userListDescriptor = $convert
    .base64Decode('CghVc2VyTGlzdBIZCgRsaXN0GAEgAygLMgUuVXNlclIEbGlzdA==');
