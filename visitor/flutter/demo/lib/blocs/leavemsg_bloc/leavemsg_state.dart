// import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';
import 'package:equatable/equatable.dart';

abstract class LeaveMsgState extends Equatable {
  const LeaveMsgState();

  @override
  List<Object> get props => [];
}

/// UnInitialized
class UnLeaveMsgState extends LeaveMsgState {
  const UnLeaveMsgState();

  @override
  String toString() => 'UnLeaveMsgState';
}

class LeaveMsgEmpty extends LeaveMsgState {
  @override
  String toString() => 'LeaveMsgEmpty';
}

class LeaveMsgSubmiting extends LeaveMsgState {
  @override
  String toString() => 'LeaveMsgSubmiting';
}

class LeaveMsgSubmitError extends LeaveMsgState {
  @override
  String toString() => 'LeaveMsgSubmitError';
}

/// Initialized
class LeaveMsgSubmitSuccessState extends LeaveMsgState {
  const LeaveMsgSubmitSuccessState() : super();

  @override
  String toString() => 'LeaveMsgSubmitSuccessState';
}

class ImageUploading extends LeaveMsgState {
  @override
  String toString() => 'ImageUploading';
}

class UploadImageSuccess extends LeaveMsgState {
  final UploadJsonResult uploadJsonResult;

  const UploadImageSuccess(this.uploadJsonResult);

  @override
  List<Object> get props => [uploadJsonResult];

  @override
  String toString() => 'UploadImageSuccess { logo: ${uploadJsonResult.url} }';
}

// class UploadImageSuccess extends LeaveMsgState {
//   //
//   final String url;
//   const UploadImageSuccess(this.url);
//   @override
//   List<Object> get props => [url];
//   @override
//   String toString() => 'UploadImageSuccess { logo: $url }';
// }

class UpLoadImageError extends LeaveMsgState {
  @override
  String toString() => 'UpLoadImageError';
}
