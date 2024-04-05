// import 'dart:async';
import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';
import 'package:bytedesk_kefu/model/user.dart';
import 'package:bytedesk_kefu/repositories/user_repository.dart';
import 'package:bloc/bloc.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import './bloc.dart';

class ProfileBloc extends Bloc<ProfileEvent, ProfileState> {
  //
  final UserRepository userRepository = UserRepository();

  ProfileBloc() : super(InitialProfileState()) {
    on<GetProfileEvent>(_mapProfileState);
    // on<UploadImageEvent>(_mapUploadImageToState);
    on<UploadImageEvent>(_mapUploadImageToState);
    on<UploadImageBytesEvent>(_mapUploadImageBytesToState);
    on<UpdateAvatarEvent>(_mapUpdateAvatarToState);
    on<UpdateNicknameEvent>(_mapUpdateNicknameToState);
    on<UpdateDescriptionEvent>(_mapUpdateDescriptionToState);
    on<UpdateMobileEvent>(_mapUpdateMobileToState);

    on<UpdateSexEvent>(_mapUpdateSexToState);
    on<UpdateLocationEvent>(_mapUpdateLocationToState);
    on<UpdateBirthdayEvent>(_mapUpdateBirthdayToState);
    on<QueryFollowEvent>(_mapQueryFollowToState);
    on<UserFollowEvent>(_mapUserFollowToState);

    on<UserUnfollowEvent>(_mapUserUnfollowToState);
  }

  void _mapProfileState(
      GetProfileEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      User user = await userRepository.getProfile();
      if (user.uid == 'invalid_token') {
        emit(const ProfileFailure(error: 'token过期，请重新登录'));
      } else {
        emit(ProfileSuccess(user: user));
      }
    } catch (error) {
      // 网络或其他错误
      emit(ProfileFailure(error: error.toString()));
    }
  }

  void _mapUploadImageToState(
      UploadImageEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      final UploadJsonResult uploadJsonResult =
          await userRepository.uploadImage(event.filePath);
      emit(UploadImageSuccess(uploadJsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUploadImageBytesToState(
      UploadImageBytesEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      final UploadJsonResult uploadJsonResult = await userRepository
          .uploadImageBytes(event.fileName, event.fileBytes, event.mimeType);
      emit(UploadImageSuccess(uploadJsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  // void _mapUploadImageToState(
  //     UploadImageEvent event, Emitter<ProfileState> emit) async {
  //   emit(ProfileInProgress());
  //   try {
  //     final String url = await userRepository.uploadImage(event.filePath);
  //     emit(UploadImageSuccess(url));
  //   } catch (error) {
  //     BytedeskUtils.printLog(error);
  //     emit(UpLoadImageError());
  //   }
  // }

  void _mapUpdateAvatarToState(
      UpdateAvatarEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      final User user = await userRepository.updateAvatar(event.avatar);
      emit(UpdateAvatarSuccess(user));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUpdateNicknameToState(
      UpdateNicknameEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      final User user = await userRepository.updateNickname(event.nickname);
      emit(UpdateNicknameSuccess(user));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUpdateDescriptionToState(
      UpdateDescriptionEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      final User user =
          await userRepository.updateDescription(event.description);
      emit(UpdateDescriptionSuccess(user));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUpdateMobileToState(
      UpdateMobileEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      final User user = await userRepository.updateMobile(event.mobile);
      emit(UpdateMobileSuccess(user));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUpdateSexToState(
      UpdateSexEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      final User user = await userRepository.updateSex(event.sex);
      emit(UpdateSexSuccess(user));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUpdateLocationToState(
      UpdateLocationEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      final User user = await userRepository.updateLocation(event.location);
      emit(UpdateLocationSuccess(user));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapUpdateBirthdayToState(
      UpdateBirthdayEvent event, Emitter<ProfileState> emit) async {
    emit(ProfileInProgress());
    try {
      final User user = await userRepository.updateBirthday(event.birthday);
      emit(UpdateBirthdaySuccess(user));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UpLoadImageError());
    }
  }

  void _mapQueryFollowToState(
      QueryFollowEvent event, Emitter<ProfileState> emit) async {
    emit(QueryFollowing());
    try {
      final bool isFollowed = await userRepository.isFollowed(event.uid);
      emit(QueryFollowSuccess(isFollowed));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(QueryFollowError());
    }
  }

  void _mapUserFollowToState(
      UserFollowEvent event, Emitter<ProfileState> emit) async {
    emit(Following());
    try {
      final JsonResult jsonResult = await userRepository.follow(event.uid);
      emit(FollowResultSuccess(jsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(FollowError());
    }
  }

  void _mapUserUnfollowToState(
      UserUnfollowEvent event, Emitter<ProfileState> emit) async {
    emit(Following());
    try {
      final JsonResult jsonResult = await userRepository.unfollow(event.uid);
      emit(UnfollowResultSuccess(jsonResult));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(UnFollowError());
    }
  }
}
