// import 'dart:async';

// import 'package:flutter/material.dart';

// import 'package:meta/meta.dart';
// import 'package:bloc/bloc.dart';
// import 'package:equatable/equatable.dart';

// class ThemeState extends Equatable {
//   final ThemeData theme;
//   final MaterialColor color;

//   @override
//   List<Object> get props => [];

//   ThemeState({@required this.theme, @required this.color})
//       : assert(theme != null),
//         assert(color != null),
//         super();
// }

// abstract class ThemeEvent extends Equatable {
//   // ThemeEvent([List props = const []]) : super(props);
//   const ThemeEvent();

//   @override
//   List<Object> get props => [];
// }

// class ThemeBloc extends Bloc<ThemeEvent, ThemeState> {

//   @override
//   ThemeState get initialState => ThemeState(
//         theme: ThemeData.light(),
//         color: Colors.lightBlue,
//       );

//   @override
//   Stream<ThemeState> mapEventToState(ThemeEvent event) async* {

//   }
// }
