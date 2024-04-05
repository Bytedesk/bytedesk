// import 'package:flutter/material.dart';

// class CustomOverlay extends StatelessWidget {
//   final Widget? icon;
//   final BoxDecoration decoration;
//   final double width;
//   final double height;

//   const CustomOverlay({
//     Key? key,
//     this.icon,
//     this.decoration = const BoxDecoration(
//       color: Color(0xff77797A),
//       borderRadius: BorderRadius.all(Radius.circular(20.0)),
//     ),
//     this.width = 160,
//     this.height = 160,
//   }) : super(key: key);

//   @override
//   Widget build(BuildContext context) {
//     return Positioned(
//       top: MediaQuery.of(context).size.height * 0.5 - width / 2,
//       left: MediaQuery.of(context).size.width * 0.5 - height / 2,
//       child: Material(
//         type: MaterialType.transparency,
//         child: Center(
//           child: Opacity(
//             opacity: 0.8,
//             child: Container(
//               width: width,
//               height: height,
//               decoration: decoration,
//               child: icon,
//             ),
//           ),
//         ),
//       ),
//     );
//   }
// }
