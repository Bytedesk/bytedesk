import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class ProfileEvent extends Equatable {
  const ProfileEvent();

  @override
  List<Object> get props => [];
}

class GetProfileEvent extends ProfileEvent {}

class UpdateProfileEvent extends ProfileEvent {}

// 上传头像
class UploadImageEvent extends ProfileEvent {
  final String? filePath;

  const UploadImageEvent({@required this.filePath}) : super();
}

class UploadImageBytesEvent extends ProfileEvent {
  final String? fileName;
  final List<int>? fileBytes;
  final String? mimeType;

  const UploadImageBytesEvent(
      {@required this.fileName,
      @required this.fileBytes,
      @required this.mimeType})
      : super();
}

// 更新头像
class UpdateAvatarEvent extends ProfileEvent {
  final String? avatar;

  const UpdateAvatarEvent({@required this.avatar}) : super();
}

// 更新昵称
class UpdateNicknameEvent extends ProfileEvent {
  final String? nickname;

  const UpdateNicknameEvent({@required this.nickname}) : super();
}

// 更新个性签名
class UpdateDescriptionEvent extends ProfileEvent {
  final String? description;

  const UpdateDescriptionEvent({@required this.description}) : super();
}

// 更新手机号
class UpdateMobileEvent extends ProfileEvent {
  final String? mobile;

  const UpdateMobileEvent({@required this.mobile}) : super();
}

// 更新性别
class UpdateSexEvent extends ProfileEvent {
  final bool? sex;

  const UpdateSexEvent({@required this.sex}) : super();
}

// 更新地区
class UpdateLocationEvent extends ProfileEvent {
  final String? location;

  const UpdateLocationEvent({@required this.location}) : super();
}

// 更新生日
class UpdateBirthdayEvent extends ProfileEvent {
  final String? birthday;

  const UpdateBirthdayEvent({@required this.birthday}) : super();
}

// 查询是否关注
class QueryFollowEvent extends ProfileEvent {
  final String? uid;

  const QueryFollowEvent({@required this.uid}) : super();
}

// 关注用户
class UserFollowEvent extends ProfileEvent {
  final String? uid;

  const UserFollowEvent({@required this.uid}) : super();
}

// 取消关注用户
class UserUnfollowEvent extends ProfileEvent {
  final String? uid;

  const UserUnfollowEvent({@required this.uid}) : super();
}
