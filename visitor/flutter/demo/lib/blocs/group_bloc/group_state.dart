import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class GroupState extends Equatable {
  // GroupState([List props = const []]) : super(props);
  const GroupState();

  @override
  List<Object> get props => [];
}

class InitialGroupState extends GroupState {}
