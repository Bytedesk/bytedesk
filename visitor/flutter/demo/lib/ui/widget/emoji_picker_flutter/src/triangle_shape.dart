import 'package:flutter/material.dart';

/// Triangle shape used as skin tone indicator
class TriangleShape extends CustomPainter {
  /// Constructor
  /// Expects color that the triangle will be filled with
  TriangleShape(Color color) {
    _painter = Paint()
      ..color = color
      ..style = PaintingStyle.fill;
  }

  late final Paint _painter;

  @override
  void paint(Canvas canvas, Size size) {
    var path = Path()
      ..moveTo(size.width, 0)
      ..lineTo(0, size.height)
      ..lineTo(size.width, size.height)
      ..lineTo(size.width, 0)
      ..close();

    canvas.drawPath(path, _painter);
  }

  @override
  bool shouldRepaint(CustomPainter oldDelegate) {
    return false;
  }
}
