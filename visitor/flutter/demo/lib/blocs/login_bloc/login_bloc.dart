// import 'dart:async';
import 'package:bytedesk_kefu/model/codeResult.dart';
import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:bytedesk_kefu/model/oauth.dart';
import 'package:bloc/bloc.dart';
import 'package:bytedesk_kefu/blocs/login_bloc/bloc.dart';
import 'package:bytedesk_kefu/repositories/repositories.dart';

class LoginBloc extends Bloc<LoginEvent, LoginState> {
  //
  // ignore: unnecessary_new
  final UserRepository _userRepository = new UserRepository();

  LoginBloc() : super(LoginInitial()) {
    on<LoginButtonPressed>(_mapLoginState);
    on<SMSLoginButtonPressed>(_mapSMSLoginState);
    on<RegisterButtonPressed>(_mapRegisterState);
    on<RequestCodeButtonPressed>(_mapRequestCodeState);
    on<RequestCodeJianTieButtonPressed>(_mapRequestCodeJianTieState);
    on<RequestCodeWeiyuButtonPressed>(_mapRequestCodeWeiyuState);
    on<BindMobileEvent>(_mapBindMobileState);
    on<UnionidOAuthEvent>(_mapUnionidOAuthState);
    on<ResetPasswordButtonPressed>(_mapResetPasswordState);
    on<UpdatePasswordButtonPressed>(_mapUpdatePasswordState);
  }

  void _mapLoginState(LoginButtonPressed event, Emitter<LoginState> emit) async {
    emit(LoginInProgress());
    try {
      OAuth oauth = await _userRepository.login(event.username, event.password);
      if (oauth.statusCode == 200) {
        // 用户名密码正确，登录成功
        emit(LoginSuccess());
      } else {
        // 用户名密码错误，登录失败
        emit(LoginError());
      }
    } catch (error) {
      // 网络或其他错误
      emit(LoginFailure(error: error.toString()));
    }
  }

  void _mapSMSLoginState(SMSLoginButtonPressed event, Emitter<LoginState> emit) async {
    emit(LoginInProgress());
    try {
      //
      OAuth oauth = await _userRepository.smsOAuth(event.mobile, event.code);
      if (oauth.statusCode == 200) {
        // 用户名密码正确，登录成功
        emit(SMSLoginSuccess());
      } else {
        // 用户名密码错误，登录失败
        emit(LoginError());
      }
    } catch (error) {
      // 网络或其他错误
      emit(LoginFailure(error: error.toString()));
    }
  }

  void _mapRegisterState(RegisterButtonPressed event, Emitter<LoginState> emit) async {
    emit(RegisterInProgress());
    try {
      //
      JsonResult jsonResult =
          await _userRepository.register(event.mobile, event.password);
      if (jsonResult.statusCode == 200) {
        emit(RegisterSuccess());
      } else {
        emit(RegisterError(
            message: jsonResult.message, statusCode: jsonResult.statusCode));
      }
    } catch (error) {
      // 网络或其他错误
      emit(LoginFailure(error: error.toString()));
    }
  }

  void _mapRequestCodeState(RequestCodeButtonPressed event, Emitter<LoginState> emit) async {
    emit(RequestCodeInProgress());
    try {
      //
      CodeResult codeResult = await _userRepository.requestCode(event.mobile);
      if (codeResult.statusCode == 200) {
        emit(RequestCodeSuccess(codeResult: codeResult));
      } else {
        emit(RequestCodeError(
            message: codeResult.message, statusCode: codeResult.statusCode));
      }
    } catch (error) {
      // 网络或其他错误
      emit(RequestCodeError(message: error.toString(), statusCode: -1));
    }
  }

  void _mapRequestCodeJianTieState(
      RequestCodeJianTieButtonPressed event, Emitter<LoginState> emit) async {
    emit(RequestCodeInProgress());
    try {
      //
      CodeResult codeResult = await _userRepository.requestCodeJianTie(event.mobile);
      if (codeResult.statusCode == 200) {
        emit(RequestCodeSuccess(codeResult: codeResult));
      } else {
        emit(RequestCodeError(
            message: codeResult.message, statusCode: codeResult.statusCode));
      }
    } catch (error) {
      // 网络或其他错误
      emit(RequestCodeError(message: error.toString(), statusCode: -1));
    }
  }

  void _mapRequestCodeWeiyuState(
      RequestCodeWeiyuButtonPressed event, Emitter<LoginState> emit) async {
    emit(RequestCodeInProgress());
    try {
      //
      CodeResult codeResult =
          await _userRepository.requestCodeWeiyu(event.mobile);
      if (codeResult.statusCode == 200) {
        emit(RequestCodeSuccess(codeResult: codeResult));
      } else {
        emit(RequestCodeError(
            message: codeResult.message, statusCode: codeResult.statusCode));
      }
    } catch (error) {
      // 网络或其他错误
      emit(RequestCodeError(message: error.toString(), statusCode: -1));
    }
  }

  void _mapBindMobileState(BindMobileEvent event, Emitter<LoginState> emit) async {
    emit(BindMobileInProgress());
    try {
      //
      JsonResult jsonResult = await _userRepository.bindMobile(event.mobile);
      if (jsonResult.statusCode == 200) {
        emit(BindMobileSuccess(jsonResult: jsonResult));
      } else {
        emit(BindMobileError(
            message: jsonResult.message, statusCode: jsonResult.statusCode));
      }
    } catch (error) {
      // 网络或其他错误
      emit(LoginFailure(error: error.toString()));
    }
  }

  void _mapUnionidOAuthState(UnionidOAuthEvent event, Emitter<LoginState> emit) async {
    emit(UnionidOAuthInProgress());
    try {
      //
      OAuth oauth = await _userRepository.unionIdOAuth(event.unionid);
      if (oauth.statusCode == 200) {
        // 登录成功
        emit(UnionidLoginSuccess());
      } else {
        // 登录失败
        emit(LoginError());
      }
    } catch (error) {
      // 网络或其他错误
      emit(LoginFailure(error: error.toString()));
    }
  }

  void _mapResetPasswordState(ResetPasswordButtonPressed event, Emitter<LoginState> emit) async {
    emit(ResetPasswordInProgress());
    try {
      //
      JsonResult jsonResult =
          await _userRepository.changePassword(event.mobile, event.password);
      if (jsonResult.statusCode == 200) {
        emit(ResetPasswordSuccess());
      } else {
        emit(ResetPasswordError(
            message: jsonResult.message, statusCode: jsonResult.statusCode));
      }
    } catch (error) {
      // 网络或其他错误
      emit(LoginFailure(error: error.toString()));
    }
  }

  void _mapUpdatePasswordState(UpdatePasswordButtonPressed event, Emitter<LoginState> emit) async {
    //
    emit(UpdatePasswordInProgress());
    try {
      //
      JsonResult jsonResult =
          await _userRepository.changePassword(event.mobile, event.password);
      if (jsonResult.statusCode == 200) {
        emit(UpdatePasswordSuccess());
      } else {
        emit(UpdatePasswordError(
            message: jsonResult.message, statusCode: jsonResult.statusCode));
      }
    } catch (error) {
      // 网络或其他错误
      emit(LoginFailure(error: error.toString()));
    }
  }

}
