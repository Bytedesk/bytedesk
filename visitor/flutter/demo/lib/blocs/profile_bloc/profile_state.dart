import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';
import 'package:bytedesk_kefu/model/user.dart';
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class ProfileState extends Equatable {
  // ProfileState([List props = const []]) : super(props);
  const ProfileState();

  @override
  List<Object> get props => [];
}

class InitialProfileState extends ProfileState {}

class ProfileInProgress extends ProfileState {
  @override
  String toString() => 'ProfileInProgress';
}

class ProfileSuccess extends ProfileState {
  final User? user;

  const ProfileSuccess({@required this.user});

  @override
  String toString() => 'ProfileSuccess';
}

class ProfileError extends ProfileState {
  @override
  String toString() => 'ProfileError';
}

class ProfileFailure extends ProfileState {
  final String? error;

  const ProfileFailure({@required this.error});

  @override
  String toString() => 'ProfileFailure { error: $error }';
}


class UploadImageSuccess extends ProfileState {
  final UploadJsonResult uploadJsonResult;

  const UploadImageSuccess(this.uploadJsonResult);

  @override
  List<Object> get props => [uploadJsonResult];

  @override
  String toString() => 'UploadImageSuccess { logo: ${uploadJsonResult.url} }';
}


// class UploadImageSuccess extends ProfileState {
//   final String? url;

//   const UploadImageSuccess(this.url);

//   @override
//   String toString() => 'UploadImageSuccess { logo: $url }';
// }

class UpLoadImageError extends ProfileState {
  @override
  String toString() => 'UpLoadImageError';
}

class UpdateAvatarSuccess extends ProfileState {
  final User user;

  const UpdateAvatarSuccess(this.user);

  @override
  String toString() => 'UpdateAvatarSuccess';
}

class UpdateNicknameSuccess extends ProfileState {
  final User user;

  const UpdateNicknameSuccess(this.user);

  @override
  String toString() => 'UpdateNicknameSuccess';
}

class UpdateDescriptionSuccess extends ProfileState {
  final User user;

  const UpdateDescriptionSuccess(this.user);

  @override
  String toString() => 'UpdateDescriptionSuccess';
}

class UpdateMobileSuccess extends ProfileState {
  final User user;

  const UpdateMobileSuccess(this.user);

  @override
  String toString() => 'UpdateMobileSuccess';
}

class UpdateSexSuccess extends ProfileState {
  final User user;

  const UpdateSexSuccess(this.user);

  @override
  String toString() => 'UpdateSexSuccess';
}

class UpdateLocationSuccess extends ProfileState {
  final User user;

  const UpdateLocationSuccess(this.user);

  @override
  String toString() => 'UpdateLocationSuccess';
}

class UpdateBirthdaySuccess extends ProfileState {
  final User user;

  const UpdateBirthdaySuccess(this.user);

  @override
  String toString() => 'UpdateBirthdaySuccess';
}

class UpdateError extends ProfileState {
  @override
  String toString() => 'UpdateError';
}

class QueryFollowing extends ProfileState {
  @override
  String toString() => 'QueryFollowing';
}

class QueryFollowSuccess extends ProfileState {
  final bool isFollowed;

  const QueryFollowSuccess(this.isFollowed);

  @override
  String toString() => 'QueryFollowSuccess';
}

class QueryFollowError extends ProfileState {
  @override
  String toString() => 'QueryFollowError';
}

class Following extends ProfileState {
  @override
  String toString() => 'Following';
}

class FollowResultSuccess extends ProfileState {
  final JsonResult jsonResult;

  const FollowResultSuccess(this.jsonResult);

  @override
  String toString() => 'FollowResultSuccess';
}

class FollowError extends ProfileState {
  @override
  String toString() => 'FollowError';
}

class Unfollowing extends ProfileState {
  @override
  String toString() => 'Unfollowing';
}

class UnfollowResultSuccess extends ProfileState {
  final JsonResult jsonResult;

  const UnfollowResultSuccess(this.jsonResult);

  @override
  String toString() => 'UnfollowResultSuccess';
}

class UnFollowError extends ProfileState {
  @override
  String toString() => 'UnFollowError';
}
