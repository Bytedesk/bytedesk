import 'dart:convert';

import 'package:bytedesk_kefu/http/bytedesk_base_api.dart';
import 'package:bytedesk_kefu/model/bfeedbackJsonResult.dart';
// import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/model/model.dart';
// import 'package:bytedesk_kefu/model/jsonResult.dart';
// import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_events.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:flutter/material.dart';

// import '../model/bfeedback.dart';
// import 'package:sp_util/sp_util.dart';
// import 'package:http/http.dart' as http;

class BytedeskFeedbackHttpApi extends BytedeskBaseHttpApi {
  //
  // 获取意见反馈分类
  Future<List<HelpCategory>> getHelpFeedbackCategories(String? uid) async {
    //
    final categoriesUrl = BytedeskUtils.getHostUri(
        '/visitor/api/category/feedback', {'uid': uid, 'client': client});
    debugPrint("getHelpFeedbackCategories Url $categoriesUrl");
    final initResponse = await httpClient.get(categoriesUrl);
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("getHelpFeedbackCategories: $responseJson");
    //
    List<HelpCategory> categories = (responseJson['data'] as List<dynamic>)
        .map((item) => HelpCategory.fromJson(item))
        .toList();

    return categories;
  }

  // 提交意见反馈
  Future<FeedbackJsonResult> submitFeedback(String? cid, String? mobile,
      String? content, List<String>? imageUrls) async {
    //
    var body = json
        .encode({"cid": cid, "mobile": mobile, "content": content, "images": imageUrls, "client": client});
    final initUrl = BytedeskUtils.getHostUri('/api/feedback/create');
    debugPrint("submitFeedback Url $initUrl");
    final initResponse =
        await httpClient.post(initUrl, headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("submitFeedback: $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }

    return FeedbackJsonResult.fromJson(responseJson);
  }

  // 获取访客历史反馈
  Future<List<BFeedback>> getMyFeedback() async {
    //
    final mineUrl = BytedeskUtils.getHostUri(
        '/api/feedback/mine', {'client': client});
    debugPrint("getMyFeedback Url $mineUrl");
    final initResponse = await httpClient.get(mineUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("getMyFeedback: $responseJson");
    //
    List<BFeedback> myfeedbacks = (responseJson['data']['content'] as List<dynamic>)
        .map((item) => BFeedback.fromJson(item))
        .toList();

    return myfeedbacks;
  }

  // https://pub.dev/documentation/http/latest/http/MultipartRequest-class.html
  // Future<String> upload(String? filePath) async {
  //   //
  //   String fileName = filePath!.split("/").last;
  //   String? username = SpUtil.getString(BytedeskConstants.uid);

  //   final uploadUrl = '$baseUrl/visitor/api/upload/image';
  //   BytedeskUtils.printLog(
  //       "fileName $fileName, username $username, upload Url $uploadUrl");

  //   var uri = Uri.parse(uploadUrl);
  //   var request = http.MultipartRequest('POST', uri)
  //     ..fields['file_name'] = fileName
  //     ..fields['username'] = username!
  //     ..files.add(await http.MultipartFile.fromPath('file', filePath));

  //   http.Response response =
  //       await http.Response.fromStream(await request.send());
  //   // debugPrint("Result: ${response.body}");

  //   //解决json解析中的乱码问题
  //   Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
  //   //将string类型数据 转换为json类型的数据
  //   final responseJson = json.decode(utf8decoder.convert(response.bodyBytes));
  //   debugPrint("responseJson $responseJson");

  //   String url = responseJson['data'];
  //   debugPrint('url:$url');
  //   return url;
  // }
}
