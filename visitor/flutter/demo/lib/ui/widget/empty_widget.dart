import 'package:flutter/material.dart';

typedef TapCallback = void Function();

class EmptyWidget extends StatelessWidget {
  final String? tip;
  final String? tip2;
  final String? tip3;
  final bool? showIcon;
  final TapCallback? tapCallback;

  const EmptyWidget(
      {Key? key,
      this.tip = '内容为空',
      this.tip2 = '',
      this.tip3 = '',
      this.showIcon = true,
      this.tapCallback})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Center(
        child: InkWell(
      onTap: () {
        debugPrint("EmptyWidget onTap");
        tapCallback!();
      },
      child: SizedBox(
        height: double.infinity,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            const Expanded(
              flex: 2,
              child: SizedBox(),
            ),
            Visibility(
              visible: showIcon!,
              child: SizedBox(
                width: 100.0,
                height: 100.0,
                child: Image.asset('assets/images/common/nodata.png'),
              ),
            ),
            Container(
              margin: const EdgeInsets.only(left: 20.0, right: 20.0),
              child: Text(
                tip!,
                style: TextStyle(fontSize: 16.0, color: Colors.grey[400]),
              ),
            ),
            Container(
              margin: const EdgeInsets.only(left: 20.0, right: 20.0),
              child: Text(
                tip2!,
                style: TextStyle(fontSize: 16.0, color: Colors.grey[400]),
              ),
            ),
            Container(
              margin: const EdgeInsets.only(left: 20.0, right: 20.0),
              child: Text(
                tip3!,
                style: TextStyle(fontSize: 16.0, color: Colors.grey[400]),
              ),
            ),
            const Expanded(
              flex: 3,
              child: SizedBox(),
            ),
          ],
        ),
      ),
    ));
  }
}
