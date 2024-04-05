import 'package:bytedesk_kefu/model/codeResult.dart';
import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

abstract class LoginState extends Equatable {
  const LoginState();

  @override
  List<Object> get props => [];
}

class LoginInitial extends LoginState {
  @override
  String toString() => 'LoginInitial';
}

class LoginInProgress extends LoginState {
  @override
  String toString() => 'LoginInProgress';
}

class LoginSuccess extends LoginState {
  @override
  String toString() => 'LoginSuccess';
}

class SMSLoginSuccess extends LoginState {
  @override
  String toString() => 'SMSLoginSuccess';
}

class UnionidLoginSuccess extends LoginState {
  @override
  String toString() => 'UnionidLoginSuccess';
}

class LoginError extends LoginState {
  @override
  String toString() => 'LoginError';
}

class LoginFailure extends LoginState {
  final String? error;

  const LoginFailure({@required this.error});

  @override
  List<Object> get props => [error!];

  @override
  String toString() => 'LoginFailure { error: $error }';
}

class ResultSuccess extends LoginState {
  @override
  String toString() => 'ResultSuccess';
}

class ResultError extends LoginState {
  final String? message;
  final int? statusCode;

  const ResultError({@required this.message, @required this.statusCode});

  @override
  String toString() => 'ResultError { error: $message }';
}

class RegisterInProgress extends LoginState {
  @override
  String toString() => 'RegisterInProgress';
}

class RegisterSuccess extends LoginState {
  @override
  String toString() => 'RegisterSuccess';
}

class RegisterError extends LoginState {
  final String? message;
  final int? statusCode;

  const RegisterError({@required this.message, @required this.statusCode});

  @override
  String toString() => 'RegisterError { error: $message }';
}

class RequestCodeInProgress extends LoginState {
  @override
  String toString() => 'RequestCodeInProgress';
}

class RequestCodeSuccess extends LoginState {
  final CodeResult? codeResult;
  const RequestCodeSuccess({@required this.codeResult}) : super();
  @override
  String toString() => 'RequestCodeSuccess';
}

class RequestCodeError extends LoginState {
  final String? message;
  final int? statusCode;
  const RequestCodeError({@required this.message, @required this.statusCode});
  @override
  String toString() => 'RequestCodeError { error: $message }';
}

class ResetPasswordInProgress extends LoginState {
  @override
  String toString() => 'ResetPasswordInProgress';
}

class ResetPasswordSuccess extends LoginState {
  @override
  String toString() => 'ResetPasswordSuccess';
}

class ResetPasswordError extends LoginState {
  final String? message;
  final int? statusCode;

  const ResetPasswordError({@required this.message, @required this.statusCode});

  @override
  String toString() => 'ResetPasswordError { error: $message }';
}

class UpdatePasswordInProgress extends LoginState {
  @override
  String toString() => 'UpdatePasswordInProgress';
}

class UpdatePasswordSuccess extends LoginState {
  @override
  String toString() => 'UpdatePasswordSuccess';
}

class UpdatePasswordError extends LoginState {
  final String? message;
  final int? statusCode;

  const UpdatePasswordError(
      {@required this.message, @required this.statusCode});

  @override
  String toString() => 'UpdatePasswordError { error: $message }';
}

class BindMobileInProgress extends LoginState {
  @override
  String toString() => 'BindMobileInProgress';
}

class BindMobileSuccess extends LoginState {
  final JsonResult? jsonResult;
  const BindMobileSuccess({@required this.jsonResult}) : super();
  @override
  String toString() => 'BindMobileSuccess';
}

class BindMobileError extends LoginState {
  final String? message;
  final int? statusCode;
  const BindMobileError({@required this.message, @required this.statusCode});
  @override
  String toString() => 'BindMobileError { error: $message }';
}

class UnionidOAuthInProgress extends LoginState {
  @override
  String toString() => 'UnionidOAuthInProgress';
}
