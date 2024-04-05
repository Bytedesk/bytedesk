import 'package:equatable/equatable.dart';
import 'package:meta/meta.dart';

@immutable
abstract class GroupEvent extends Equatable {
  // GroupEvent([List props = const []]) : super(props);
  const GroupEvent();

  @override
  List<Object> get props => [];
}
