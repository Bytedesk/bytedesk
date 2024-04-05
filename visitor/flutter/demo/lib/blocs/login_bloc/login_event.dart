import 'package:meta/meta.dart';
import 'package:equatable/equatable.dart';

@immutable
abstract class LoginEvent extends Equatable {
  const LoginEvent();

  @override
  List<Object> get props => [];
}

class LoginButtonPressed extends LoginEvent {
  //
  final String? username;
  final String? password;

  const LoginButtonPressed({@required this.username, @required this.password})
      : super();

  @override
  String toString() {
    return 'LoginButtonPressed { username: $username, password: $password }';
  }
}

class SMSLoginButtonPressed extends LoginEvent {
  //
  final String? mobile;
  final String? code;

  const SMSLoginButtonPressed({@required this.mobile, @required this.code}) : super();

  @override
  String toString() {
    return 'SMSLoginButtonPressed { username: $mobile, password: $code }';
  }
}

class RegisterButtonPressed extends LoginEvent {
  //
  final String? mobile;
  final String? password;
  //
  const RegisterButtonPressed({@required this.mobile, @required this.password})
      : super();
  @override
  String toString() {
    return 'RegisterButtonPressed { mobile: $mobile, password: $password }';
  }
}

class RequestCodeButtonPressed extends LoginEvent {
  //
  final String? mobile;
  const RequestCodeButtonPressed({@required this.mobile}) : super();
  @override
  String toString() {
    return 'RequestCodeButtonPressed { mobile: $mobile}';
  }
}

class RequestCodeJianTieButtonPressed extends LoginEvent {
  //
  final String? mobile;
  const RequestCodeJianTieButtonPressed({@required this.mobile}) : super();
  @override
  String toString() {
    return 'RequestCodeJianTieButtonPressed { mobile: $mobile}';
  }
}

class RequestCodeWeiyuButtonPressed extends LoginEvent {
  //
  final String? mobile;
  const RequestCodeWeiyuButtonPressed({@required this.mobile}) : super();
  @override
  String toString() {
    return 'RequestCodeWeiyuButtonPressed { mobile: $mobile}';
  }
}

class RequestCodeTikuButtonPressed extends LoginEvent {
  //
  final String? mobile;
  const RequestCodeTikuButtonPressed({@required this.mobile}) : super();
  @override
  String toString() {
    return 'RequestCodeTikuButtonPressed { mobile: $mobile}';
  }
}

class BindMobileEvent extends LoginEvent {
  //
  final String? mobile;
  const BindMobileEvent({@required this.mobile}) : super();
  @override
  String toString() {
    return 'BindMobileEvent { mobile: $mobile}';
  }
}

class UnionidOAuthEvent extends LoginEvent {
  //
  final String? unionid;
  const UnionidOAuthEvent({@required this.unionid}) : super();
  //
  @override
  String toString() {
    return 'UnionidOAuthEvent { unionid: $unionid}';
  }
}

class ResetPasswordButtonPressed extends LoginEvent {
  //
  final String? mobile;
  final String? password;
  const ResetPasswordButtonPressed({@required this.mobile, @required this.password})
      : super();
  @override
  String toString() {
    return 'ResetPasswordButtonPressed { mobile: $mobile, password: $password }';
  }
}

class UpdatePasswordButtonPressed extends LoginEvent {
  //
  final String? mobile;
  final String? password;

  const UpdatePasswordButtonPressed({@required this.mobile, @required this.password})
      : super();

  @override
  String toString() {
    return 'UpdatePasswordButtonPressed { mobile: $mobile, password: $password }';
  }
}

// 将unionid绑定到已经存在的手机账号
class BindWechatMobileEvent extends LoginEvent {
  //
  final String? mobile;
  final String? unionid;
  //
  const BindWechatMobileEvent({@required this.mobile, @required this.unionid})
      : super();
  //
  @override
  String toString() {
    return 'BindWechatMobileEvent { username: $mobile, password: $unionid }';
  }
}
