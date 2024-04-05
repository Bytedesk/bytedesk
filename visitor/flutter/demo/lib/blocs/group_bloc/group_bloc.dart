// import 'dart:async';
import 'package:bloc/bloc.dart';
import './bloc.dart';

class GroupBloc extends Bloc<GroupEvent, GroupState> {
  GroupBloc() : super(InitialGroupState());

  // @override
  // Stream<GroupState> mapEventToState(
  //   GroupEvent event,
  // ) async* {
  //   // TODO: Add Logic
  // }
}
