import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class ContactEvent extends Equatable {
  // ContactEvent([List props = const []]) : super(props);
  const ContactEvent();

  @override
  List<Object> get props => [];
}

class RefreshContactEvent extends ContactEvent {}

class UpdateContactEvent extends ContactEvent {
  final String? tid;

  const UpdateContactEvent({@required this.tid})
      : assert(tid != null),
        super();
}

class DeleteContactEvent extends ContactEvent {
  final String? tid;

  const DeleteContactEvent({@required this.tid})
      : assert(tid != null),
        super();
}
