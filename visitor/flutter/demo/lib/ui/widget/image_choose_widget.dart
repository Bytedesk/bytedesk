// import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:flutter/material.dart';

//选择头像底部弹出框
class ImageChooseWidget extends StatelessWidget {
  //
  final VoidCallback? pickImageCallBack;
  final VoidCallback? takeImageCallBack;

  const ImageChooseWidget(
      {Key? key,
      this.pickImageCallBack,
      this.takeImageCallBack,})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min, //wrap_content
      children: <Widget>[
        Material(
          color: Colors.white,
          child: InkWell(
              onTap: () {
                //
                Navigator.pop(context);
                takeImageCallBack!();
              },
              child: Container(
                height: 50,
                padding: const EdgeInsets.symmetric(
                  vertical: 15.0,
                ),
                child: const Center(
                  child: Text('立即拍照',
                      style: TextStyle(
                        fontSize: 16,
                      )),
                ),
              )),
        ),
        Container(
          height: 1,
          color: const Color(0xffEFF1F0),
          //  margin: EdgeInsets.only(left: 60),
        ),
        Material(
          color: Colors.white,
          child: InkWell(
              onTap: () {
                //
                Navigator.pop(context);
                pickImageCallBack!();
              },
              child: Container(
                height: 50,
                padding: const EdgeInsets.symmetric(
                  vertical: 15.0,
                ),
                child: const Center(
                  child: Text('从相册选择', style: TextStyle(fontSize: 16)),
                ),
              )),
        ),
      ],
    );
  }
}
