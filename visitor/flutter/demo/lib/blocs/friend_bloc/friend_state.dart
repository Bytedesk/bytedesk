import 'package:bytedesk_kefu/model/friend.dart';
import 'package:equatable/equatable.dart';

abstract class FriendState extends Equatable {
  const FriendState();
  @override
  List<Object> get props => [];
}

/// UnInitialized
class UnFriendState extends FriendState {
  const UnFriendState();

  @override
  String toString() => 'UnFriendState';
}

/// Initialized
class FriendLoading extends FriendState {
  const FriendLoading() : super();

  @override
  String toString() => 'FriendLoading';
}

class FriendUpdateSuccess extends FriendState {
  const FriendUpdateSuccess() : super();

  @override
  String toString() => 'FriendUpdateSuccess';
}

class FriendCreateSuccess extends FriendState {
  final Friend? friend;
  const FriendCreateSuccess({this.friend});

  @override
  String toString() => 'FriendCreateSuccess';
}

class FriendLoadSuccess extends FriendState {
  final List<Friend> friendList;

  const FriendLoadSuccess(this.friendList);

  @override
  List<Object> get props => [friendList];

  @override
  String toString() => 'FriendLoadSuccess { FriendList: ${friendList.length} }';
}

class ErrorFriendState extends FriendState {
  final String errorMessage;

  const ErrorFriendState(this.errorMessage);

  @override
  String toString() => 'ErrorFriendState';
}
