// import 'dart:io';
// ignore_for_file: use_build_context_synchronously

import 'dart:typed_data';
import 'package:bytedesk_kefu/blocs/feedback_bloc/bloc.dart';
import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/ui/feedback/provider/feedback_history_provider.dart';
import 'package:bytedesk_kefu/ui/widget/image_choose_widget.dart';
import 'package:bytedesk_kefu/ui/widget/my_button.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:image_picker/image_picker.dart';

class FeedbackSubmitPage extends StatefulWidget {
  final HelpCategory? helpCategory;
  const FeedbackSubmitPage({Key? key, this.helpCategory}) : super(key: key);

  @override
  State<FeedbackSubmitPage> createState() => _FeedbackSubmitPageState();
}

class _FeedbackSubmitPageState extends State<FeedbackSubmitPage> {
  //
  final ScrollController _scrollController = ScrollController(); // 滚动监听
  final TextEditingController _mobileTextEditController =
      TextEditingController();
  final TextEditingController _contentTextEditController =
      TextEditingController();
  // 图片
  final ImagePicker _picker = ImagePicker();
  final List<String> _imageUrls = [];
  // final List<File> _fileList = [];
  // File? _selectedImageFile;
  // List<MultipartFile> mSubmitFileList = [];

  @override
  void initState() {
    // 滚动监听, https://learnku.com/articles/30338
    _scrollController.addListener(() {
      // 隐藏软键盘
      FocusScope.of(context).requestFocus(FocusNode());
      // 如果滑动到底部
      if (_scrollController.position.pixels ==
          _scrollController.position.maxScrollExtent) {
        // debugPrint('scroll to bottom');
      }
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    //
    // debugPrint('fileList的内容: $_fileList');
    // if (_selectedImageFile != null) {
    //   _fileList.add(_selectedImageFile!);
    // }
    // _selectedImageFile = null;
    //
    return Scaffold(
        appBar: AppBar(
          title: Text(widget.helpCategory!.name!),
          centerTitle: true,
          actions: [
            Align(
              alignment: Alignment.centerRight,
              child: Container(
                margin: const EdgeInsets.only(right: 10),
                child: InkWell(
                  onTap: () {
                    debugPrint('我的反馈');
                    Navigator.of(context)
                        .push(MaterialPageRoute(builder: (context) {
                      return const FeedbackHistoryProvider();
                    }));
                  },
                  child: const Text(
                    '我的反馈',
                  ),
                ),
              ),
            )
          ],
        ),
        body: BlocConsumer<FeedbackBloc, FeedbackState>(
            listener: (context, state) {
          debugPrint("FeedbackBloc $state");
          if (state is ImageUploading) {
            Fluttertoast.showToast(msg: '上传图片中');
          } else if (state is UploadImageSuccess) {
            // 图片url
            if (!_imageUrls.contains(state.uploadJsonResult.url)) {
              setState(() {
                _imageUrls.add(state.uploadJsonResult.url!);
              });
            }
          } else if (state is UpLoadImageError) {
            Fluttertoast.showToast(msg: '上传图片失败');
          } else if (state is FeedbackSubmiting) {
            Fluttertoast.showToast(msg: '提交反馈中');
          } else if (state is FeedbackSubmitSuccess) {
            Fluttertoast.showToast(msg: '提交反馈成功');
            // 返回上一层
            Navigator.of(context).pop();
          } else if (state is FeedbackSubmitError) {
            Fluttertoast.showToast(msg: '提交反馈失败');
          }
        }, builder: (context, state) {
          // return widget here based on BlocA's state
          return SingleChildScrollView(
            controller: _scrollController,
            child: Container(
                padding: const EdgeInsets.only(top: 5.0, left: 10, right: 10),
                child: Column(
                  children: <Widget>[
                    TextField(
                      controller: _mobileTextEditController,
                      decoration: const InputDecoration(
                        hintText: "手机号",
                        hintStyle:
                            TextStyle(color: Color(0xff999999), fontSize: 16),
                        contentPadding: EdgeInsets.only(left: 15, right: 15),
                        // border: InputBorder.none,
                      ),
                    ),
                    Container(
                      constraints: const BoxConstraints(
                        minHeight: 150,
                      ),
                      // color: Color(0xffffffff),
                      margin: const EdgeInsets.only(top: 15),
                      child: TextField(
                        controller: _contentTextEditController,
                        maxLines: 5,
                        maxLength: 500,
                        decoration: const InputDecoration(
                          hintText: "请输入反馈内容",
                          hintStyle:
                              TextStyle(color: Color(0xff999999), fontSize: 16),
                          contentPadding: EdgeInsets.only(left: 15, right: 15),
                          border: InputBorder.none,
                        ),
                      ),
                    ),
                    GridView.count(
                      shrinkWrap: true,
                      primary: false,
                      crossAxisCount: 3,
                      children: List.generate(_imageUrls.length + 1, (index) {
                        // 这个方法体用于生成GridView中的一个item
                        Widget content;
                        if (index == _imageUrls.length) {
                          // 添加图片按钮
                          var addCell = Center(
                              child: Image.asset(
                            'assets/images/feedback/mine_feedback_add_image.png',
                            width: double.infinity,
                            height: double.infinity,
                          ));
                          content = GestureDetector(
                            onTap: () {
                              // 添加图片
                              // pickImage(context);
                              showModalBottomSheet(
                                  context: context,
                                  builder: (context) {
                                    return ImageChooseWidget(
                                      pickImageCallBack: () {
                                        _pickImage();
                                      },
                                      takeImageCallBack: () {
                                        _takeImage();
                                      },
                                    );
                                  });
                            },
                            child: addCell,
                          );
                        } else {
                          // 被选中的图片
                          content = Stack(
                            children: <Widget>[
                              Center(
                                child: CachedNetworkImage(
                                  imageUrl: _imageUrls[index],
                                  errorWidget: (context, url, error) =>
                                      const Icon(Icons.error),
                                ),
                                // child: Image.file(
                                //   _fileList[index],
                                //   width: double.infinity,
                                //   height: double.infinity,
                                //   fit: BoxFit.cover,
                                // ),
                              ),
                              Align(
                                alignment: Alignment.topRight,
                                child: InkWell(
                                  onTap: () {
                                    _imageUrls.removeAt(index);
                                    // _selectedImageFile = null;
                                    setState(() {});
                                  },
                                  child: Image.asset(
                                    'assets/images/feedback/mine_feedback_ic_del.png',
                                    width: 20.0,
                                    height: 20.0,
                                  ),
                                ),
                              )
                            ],
                          );
                        }
                        return Container(
                          margin: const EdgeInsets.all(10.0),
                          width: 80.0,
                          height: 80.0,
                          color: const Color(0xFFffffff),
                          child: content,
                        );
                      }),
                    ),
                    MyButton(
                      onPressed: () {
                        // TODO: 避免重复提交
                        // 提交
                        BlocProvider.of<FeedbackBloc>(context).add(
                            SubmitFeedbackEvent(
                                cid: widget.helpCategory!.cid,
                                mobile: _mobileTextEditController.text,
                                imageUrls: _imageUrls,
                                content: _contentTextEditController.text));
                      },
                      text: '提交',
                    )
                  ],
                )),
          );
        }));
  }

  // 选择图片
  Future<void> _pickImage() async {
    if (_imageUrls.length >= 9) {
      Fluttertoast.showToast(msg: "最多选取9张图片");
      return;
    }
    try {
      XFile? pickedFile = await _picker.pickImage(
          source: ImageSource.gallery, maxWidth: 800, imageQuality: 95);
      debugPrint('pick image path: ${pickedFile!.path}');
      //
      if (BytedeskUtils.isWeb) {
        // web
        String? fileName = pickedFile.path.split("/").last;
        Uint8List? fileBytes = await pickedFile.readAsBytes();
        String? mimeType = pickedFile.mimeType;
        //
        BlocProvider.of<FeedbackBloc>(context).add(UploadImageBytesEvent(
          fileName: fileName,
          fileBytes: fileBytes,
          mimeType: mimeType,
        ));
      } else {
        BlocProvider.of<FeedbackBloc>(context)
            .add(UploadImageEvent(filePath: pickedFile.path));
      }
    } catch (e) {
      debugPrint('pick image error ${e.toString()}');
      Fluttertoast.showToast(msg: "未选取图片");
    }
  }

  // 拍照
  Future<void> _takeImage() async {
    if (_imageUrls.length >= 9) {
      Fluttertoast.showToast(msg: "最多选取9张图片");
      return;
    }
    try {
      XFile? pickedFile = await _picker.pickImage(
          source: ImageSource.camera, maxWidth: 800, imageQuality: 95);
      debugPrint('take image path: ${pickedFile!.path}');
      //
      if (BytedeskUtils.isWeb) {
        // web
        String? fileName = pickedFile.path.split("/").last;
        Uint8List? fileBytes = await pickedFile.readAsBytes();
        String? mimeType = pickedFile.mimeType;
        //
        BlocProvider.of<FeedbackBloc>(context).add(UploadImageBytesEvent(
          fileName: fileName,
          fileBytes: fileBytes,
          mimeType: mimeType,
        ));
      } else {
        BlocProvider.of<FeedbackBloc>(context)
            .add(UploadImageEvent(filePath: pickedFile.path));
      }
    } catch (e) {
      debugPrint('take image error ${e.toString()}');
      Fluttertoast.showToast(msg: "未选取图片");
    }
  }
}
