import 'package:flutter/material.dart';

class LoadingWidget extends StatelessWidget {
  const LoadingWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return const Center(
        child: SizedBox(
      width: double.infinity,
      height: double.infinity,
      child: Center(
          child: SizedBox(
        height: 200.0,
        width: 300.0,
        child: Card(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              SizedBox(
                width: 50.0,
                height: 50.0,
                // child: SpinKitFadingCube(
                //   color: Theme.of(context).primaryColor,
                //   size: 25.0,
                // ),
              ),
              Text('数据加载中，请稍后')
            ],
          ),
        ),
      )),
    ));
  }
}
