// import 'dart:async';

// import 'package:meta/meta.dart';
// import 'package:bloc/bloc.dart';
// import 'package:equatable/equatable.dart';

// abstract class SettingsEvent extends Equatable {
//   const SettingsEvent();

//   @override
//   List<Object> get props => [];
// }

// class TemperatureUnitsToggled extends SettingsEvent {}

// enum TemperatureUnits { fahrenheit, celsius }

// class SettingsState extends Equatable {
//   final TemperatureUnits temperatureUnits;

//   @override
//   List<Object> get props => [];

//   SettingsState({@required this.temperatureUnits})
//       : assert(temperatureUnits != null),
//         super();
// }

// class SettingsBloc extends Bloc<SettingsEvent, SettingsState> {
//   @override
//   SettingsState get initialState =>
//       SettingsState(temperatureUnits: TemperatureUnits.celsius);

//   @override
//   Stream<SettingsState> mapEventToState(SettingsEvent event) async* {
//     if (event is TemperatureUnitsToggled) {
//       // yield SettingsState(
//       //   temperatureUnits:
//       //       currentState.temperatureUnits == TemperatureUnits.celsius
//       //           ? TemperatureUnits.fahrenheit
//       //           : TemperatureUnits.celsius,
//       // );
//     }
//   }
// }
