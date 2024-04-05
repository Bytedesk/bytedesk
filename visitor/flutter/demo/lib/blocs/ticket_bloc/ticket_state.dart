import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';
import 'package:equatable/equatable.dart';

abstract class TicketState extends Equatable {
  const TicketState();

  @override
  List<Object> get props => [];
}

/// UnInitialized
class UnTicketState extends TicketState {
  const UnTicketState();

  @override
  String toString() => 'UnTicketState';
}

class TicketEmpty extends TicketState {
  @override
  String toString() => 'TicketEmpty';
}

class TicketLoading extends TicketState {
  @override
  String toString() => 'TicketLoading';
}

class TicketLoadError extends TicketState {
  @override
  String toString() => 'TicketLoadError';
}

/// Initialized
class TicketCategoryState extends TicketState {
  final List<HelpCategory> categoryList;

  const TicketCategoryState(this.categoryList) : super();

  @override
  String toString() => 'GetTicketCategoryState';
}


class UploadImageSuccess extends TicketState {
  final UploadJsonResult uploadJsonResult;

  const UploadImageSuccess(this.uploadJsonResult);

  @override
  List<Object> get props => [uploadJsonResult];

  @override
  String toString() => 'UploadImageSuccess { logo: ${uploadJsonResult.url} }';
}


// class UploadImageSuccess extends TicketState {
//   //
//   final String url;
//   const UploadImageSuccess(this.url);
//   @override
//   List<Object> get props => [url];
//   @override
//   String toString() => 'UploadImageSuccess { logo: $url }';
// }

class UpLoadImageError extends TicketState {
  @override
  String toString() => 'UpLoadImageError';
}
