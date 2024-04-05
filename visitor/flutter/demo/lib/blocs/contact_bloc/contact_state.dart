import 'package:bytedesk_kefu/model/contact.dart';
import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class ContactState extends Equatable {
  // ContactState([List props = const []]) : super();
  const ContactState();

  @override
  List<Object> get props => [];
}

class ContactUninitialized extends ContactState {
  @override
  String toString() => 'ContactUninitialized';
}

class ContactEmpty extends ContactState {
  @override
  String toString() => 'ContactEmpty';
}

class ContactLoading extends ContactState {
  @override
  String toString() => 'ContactLoading';
}

class ContactLoadError extends ContactState {
  @override
  String toString() => 'ContactLoadError';
}

class ContactLoadSuccess extends ContactState {
  final List<Contact> contactList;

  const ContactLoadSuccess(this.contactList);

  @override
  List<Object> get props => [contactList];

  @override
  String toString() =>
      'contactLoadSuccess { contactList: ${contactList.length} }';
}
