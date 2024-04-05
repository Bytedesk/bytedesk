import 'package:flutter/material.dart';

class MyButton extends StatelessWidget {
  const MyButton({
    Key? key,
    this.text = '',
    @required this.onPressed,
  }) : super(key: key);

  final String? text;
  final VoidCallback? onPressed;

  @override
  Widget build(BuildContext context) {
    final themeData = Theme.of(context);
    return Material(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8),
        side: BorderSide(
          color: themeData.colorScheme.primary,
          width: 1,
        ),
      ),
      child: TextButton(
        onPressed: onPressed,
        child: Container(
          height: 36,
          width: double.infinity,
          alignment: Alignment.center,
          child: Text(
            text!,
            style: const TextStyle(
              fontSize: 18,
            ),
          ),
        ),
      ),
    );
  }
}
