// import 'dart:async';
import 'package:bloc/bloc.dart';
import 'package:bytedesk_kefu/blocs/friend_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/friend.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:bytedesk_kefu/repositories/friend_repository.dart';

class FriendBloc extends Bloc<FriendEvent, FriendState> {
  // ignore: unnecessary_new
  final FriendRepository friendRepository = new FriendRepository();

  FriendBloc() : super(const UnFriendState()) {
    on<QueryFriendEvent>(_mapQueryFriendToState);
    on<QueryFriendAddressEvent>(_mapQueryFriendAddressToState);
    on<UploadFriendAddressEvent>(_mapUploadFriendAddressToState);
    on<QueryFriendNearbyEvent>(_mapQueryFriendNearbyToState);
    on<UpdateFriendNearbyEvent>(_mapUpdateFriendNearbyToState);
  }

  void _mapQueryFriendToState(
      QueryFriendEvent event, Emitter<FriendState> emit) async {
    emit(const FriendLoading());
    try {
      final List<Friend> friendList =
          await friendRepository.getFriends(event.page, event.size);
      emit(FriendLoadSuccess(friendList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(const ErrorFriendState('friend error'));
    }
  }

  void _mapQueryFriendAddressToState(
      QueryFriendAddressEvent event, Emitter<FriendState> emit) async {
    emit(const FriendLoading());
    try {
      final List<Friend> friendList =
          await friendRepository.getFriendsAddress(event.page, event.size);
      emit(FriendLoadSuccess(friendList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(const ErrorFriendState('friend error'));
    }
  }

  void _mapUploadFriendAddressToState(
      UploadFriendAddressEvent event, Emitter<FriendState> emit) async {
    emit(const FriendLoading());
    try {
      final Friend friend =
          await friendRepository.uploadAddress(event.nickname, event.mobile);
      emit(FriendCreateSuccess(friend: friend));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(const ErrorFriendState('friend error'));
    }
  }

  void _mapQueryFriendNearbyToState(
      QueryFriendNearbyEvent event, Emitter<FriendState> emit) async {
    emit(const FriendLoading());
    try {
      final List<Friend> friendList =
          await friendRepository.getFriendsNearby(event.page, event.size);
      emit(FriendLoadSuccess(friendList));
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(const ErrorFriendState('friend error'));
    }
  }

  void _mapUpdateFriendNearbyToState(
      UpdateFriendNearbyEvent event, Emitter<FriendState> emit) async {
    emit(const FriendLoading());
    try {
      await friendRepository.updateLocation(event.latitude, event.longtitude);
      emit(const FriendUpdateSuccess());
    } catch (error) {
      BytedeskUtils.printLog(error);
      emit(const ErrorFriendState('friend error'));
    }
  }
}
