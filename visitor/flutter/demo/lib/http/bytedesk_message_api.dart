import 'dart:convert';
import 'dart:io';

import 'package:bytedesk_kefu/http/bytedesk_base_api.dart';
import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:bytedesk_kefu/model/message.dart';
import 'package:bytedesk_kefu/model/messageZhipuAI.dart';
import 'package:bytedesk_kefu/model/requestAnswer.dart';
import 'package:bytedesk_kefu/model/requestCategory.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';
import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_events.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:flutter/material.dart';
import 'package:sp_util/sp_util.dart';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';

class BytedeskMessageHttpApi extends BytedeskBaseHttpApi {
  //
  Future<JsonResult> sendMessageRest(String? jsonString) async {
    //
    var body = json.encode({"json": jsonString, "client": client});
    //
    final sendMessageUrl = BytedeskUtils.getHostUri('/api/messages/send');
    // final sendMessageUrl = '$baseUrl/api/messages/send';
    final sendMessageResponse = await httpClient.post(sendMessageUrl,
        headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(sendMessageResponse.bodyBytes));
    debugPrint("responseJson $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return JsonResult(
        message: responseJson['message'],
        statusCode: responseJson['status_code'],
        data: responseJson['data']);
  }

  // 发送给智谱AI消息
  Future<JsonResult> sendZhipuAIMessageRest(String? jsonString) async {
    //
    var body = json.encode({"json": jsonString, "client": client});
    //
    final sendMessageUrl = BytedeskUtils.getHostUri('/api/zhipu/send');
    final sendMessageResponse = await httpClient.post(sendMessageUrl,
        headers: getHeaders(), body: body);
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(sendMessageResponse.bodyBytes));
    debugPrint("sendMessageUrl $sendMessageUrl, responseJson $responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return JsonResult(
        message: responseJson['message'],
        statusCode: responseJson['status_code'],
        data: responseJson['data']);
  }

  Future<List<Message>> loadHistoryMessages(
      String? uid, int? page, int? size) async {
    //
    final loadHistoryMessagesUrl = BytedeskUtils.getHostUri(
        '/api/messages/user', {
      'page': page.toString(),
      'size': size.toString(),
      'uid': uid,
      'client': client
    });
    // debugPrint("loadHistoryMessages Url $loadHistoryMessages");
    final initResponse =
        await httpClient.get(loadHistoryMessagesUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    List<Message> messageList =
        (responseJson['data']['content'] as List<dynamic>)
            .map((item) => Message.fromJson(item))
            .toList();

    return messageList;
  }

  Future<List<Message>> loadTopicMessages(
      String? topic, int? page, int? size) async {
    //
    final loadTopicMessagesUrl =
        BytedeskUtils.getHostUri('/api/messages/topic', {
      'page': page.toString(),
      'size': size.toString(),
      'topic': topic,
      'client': client
    });
    debugPrint("loadTopicMessages Url $loadTopicMessagesUrl");
    final initResponse =
        await httpClient.get(loadTopicMessagesUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("loadTopicMessages:$responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    List<Message> messageList =
        (responseJson['data']['content'] as List<dynamic>)
            .map((item) => Message.fromJson(item))
            .toList();

    return messageList;
  }

  Future<List<Message>> loadTopicMessagesFileHelper(
      String? topic, int? page, int? size) async {
    //
    final loadTopicMessagesUrl =
        BytedeskUtils.getHostUri('/api/messages/file/topic', {
      'page': page.toString(),
      'size': size.toString(),
      'topic': topic,
      'client': client
    });
    BytedeskUtils.printLog(
        "loadTopicMessagesFileHelper Url $loadTopicMessagesUrl");
    final initResponse =
        await httpClient.get(loadTopicMessagesUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("loadTopicMessagesFileHelper:$responseJson");
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    List<Message> messageList =
        (responseJson['data']['content'] as List<dynamic>)
            .map((item) => Message.fromJsonFileHelper(item))
            .toList();

    return messageList;
  }

  Future<List<MessageZhipuAI>> loadTidMessagesZhipuAI(
      String? tid, int? page, int? size) async {
    //
    final loadTopicMessagesUrl = BytedeskUtils.getHostUri('/api/messages/thread/tid', {
      'page': page.toString(),
      'size': size.toString(),
      'tid': tid,
      'client': client
    });
    debugPrint("loadTidMessagesZhipuAI Url $loadTopicMessagesUrl");
    final initResponse = await httpClient.get(loadTopicMessagesUrl, headers: getHeaders());
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("loadTidMessagesZhipuAI: $responseJson", wrapWidth: 2048);
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    List<MessageZhipuAI> messageList =
        (responseJson['data']['content'] as List<dynamic>)
            .map((item) => MessageZhipuAI.fromJsonZhipuAI(item))
            .toList();

    return messageList;
  }

  Future<List<Message>> loadChannelMessages(
      String? cid, int? page, int? size) async {
    //
    final loadChannelMessagesUrl = BytedeskUtils.getHostUri(
        '/api/messages/channel', {
      'page': page.toString(),
      'size': size.toString(),
      'cid': cid,
      'client': client
    });
    // debugPrint("loadChannelMessagesUrl Url $loadHistoryMessages");
    final initResponse =
        await httpClient.get(loadChannelMessagesUrl, headers: getHeaders());

    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    List<Message> messageList =
        (responseJson['data']['content'] as List<dynamic>)
            .map((item) => Message.fromJson(item))
            .toList();

    return messageList;
  }

  /// 查询当前用户-某技能组wid或指定客服未读消息
  /// 注意：技能组wid或指定客服唯一id
  /// 适用于 访客 和 客服
  Future<List<Message>> loadUnreadMessages(
      String? wid, int? page, int? size) async {
    //
    final loadUnreadMessagesUrl = BytedeskUtils.getHostUri(
        '/api/messages/unread/message', {
      'page': page.toString(),
      'size': size.toString(),
      'wid': wid,
      'client': client
    });
    // debugPrint("loadHistoryMessages Url $loadHistoryMessages");
    final initResponse =
        await httpClient.get(loadUnreadMessagesUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    List<Message> messageList =
        (responseJson['data']['content'] as List<dynamic>)
            .map((item) => Message.fromJson(item))
            .toList();

    return messageList;
  }

  // 访客端-查询访客所有未读消息
  Future<List<Message>> loadUnreadMessagesVisitor(int? page, int? size) async {
    //
    final loadUnreadMessagesUrl = BytedeskUtils.getHostUri(
        '/api/messages/unread/message/visitor',
        {'page': page.toString(), 'size': size.toString(), 'client': client});
    // debugPrint("loadHistoryMessages Url $loadHistoryMessages");
    final initResponse =
        await httpClient.get(loadUnreadMessagesUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    List<Message> messageList =
        (responseJson['data']['content'] as List<dynamic>)
            .map((item) => Message.fromJson(item))
            .toList();

    return messageList;
  }

  // 客服端-查询客服所有未读消息
  Future<List<Message>> loadUnreadMessagesAgent(int? page, int? size) async {
    //
    final loadUnreadMessagesAgentUrl = BytedeskUtils.getHostUri(
        '/api/messages/unread/message/agent',
        {'page': page.toString(), 'size': size.toString(), 'client': client});
    // debugPrint("loadHistoryMessages Url $loadHistoryMessages");
    final initResponse =
        await httpClient.get(loadUnreadMessagesAgentUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    List<Message> messageList =
        (responseJson['data']['content'] as List<dynamic>)
            .map((item) => Message.fromJson(item))
            .toList();

    return messageList;
  }

  //
  Future<RequestAnswerResult> queryAnswer(String? tid, String? aid) async {
    //
    final queryAnswerUrl = BytedeskUtils.getHostUri(
        '/api/answer/query', {'tid': tid, 'aid': aid, 'client': client});
    debugPrint("query Url $queryAnswerUrl");
    final initResponse =
        await httpClient.get(queryAnswerUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return RequestAnswerResult.fromJson(responseJson);
  }

  //
  Future<RequestAnswerResult> queryAnswer2(
      String? tid, String? aid, String? mid) async {
    //
    final queryAnswerUrl = BytedeskUtils.getHostUri('/api/v2/answer/query',
        {'tid': tid, 'aid': aid, 'mid': mid, 'client': client});
    debugPrint("query Url $queryAnswerUrl");
    final initResponse =
        await httpClient.get(queryAnswerUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return RequestAnswerResult.fromJson(responseJson);
  }

  //
  Future<RequestCategoryResult> queryCategory(String? tid, String? cid) async {
    //
    final queryCategoryUrl = BytedeskUtils.getHostUri(
        '/api/v2/answer/category', {'tid': tid, 'cid': cid, 'client': client});
    debugPrint("query Url $queryCategoryUrl");
    final initResponse =
        await httpClient.get(queryCategoryUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return RequestCategoryResult.fromJson(responseJson);
  }

  //
  Future<RequestAnswerResult> messageAnswer(
      String? wid, String? content) async {
    // String? aid, String? type,
    final messageAnswerUrl =
        BytedeskUtils.getHostUri('/api/v2/answer/message', {
      // 'type': type,
      'wid': wid,
      // 'aid': aid,
      'content': content,
      'client': client
    });
    debugPrint("message Url $messageAnswerUrl");
    final initResponse =
        await httpClient.get(messageAnswerUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    debugPrint("messageAnswer:");
    BytedeskUtils.printLog(responseJson);
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return RequestAnswerResult.fromJson(responseJson);
  }

  //
  Future<RequestAnswerResult> rateAnswer(
      String? aid, String? mid, bool? rate) async {
    //
    final rateAnswerUrl = BytedeskUtils.getHostUri('/api/answer/rate',
        {'aid': aid, 'mid': mid, 'rate': rate, 'client': client});
    debugPrint("rate Url $rateAnswerUrl");
    final initResponse =
        await httpClient.get(rateAnswerUrl, headers: getHeaders());
    //
    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson =
        json.decode(utf8decoder.convert(initResponse.bodyBytes));
    // 判断token是否过期
    if (responseJson.toString().contains('invalid_token')) {
      bytedeskEventBus.fire(InvalidTokenEventBus());
    }
    //
    return RequestAnswerResult.fromRateJson(responseJson);
  }

  // https://pub.dev/documentation/http/latest/http/MultipartRequest-class.html
  Future<UploadJsonResult> uploadImage(String? filePath) async {
    //
    String? fileName = filePath!.split("/").last;
    // 检测是否含有后缀，如果没有后缀，则添加 .png后缀
    if (!fileName.contains(".")) {
      fileName += '.png';
    }
    String? username = SpUtil.getString(BytedeskConstants.uid);

    const uploadUrl =
        '${BytedeskConstants.httpUploadUrl}/visitor/api/upload/image';
    BytedeskUtils.printLog(
        "uploadImage fileName $fileName, username $username, upload Url $uploadUrl");

    // web browser 浏览器中不支持此种上传图片方式
    // Unsupported operation: MultipartFile is only supported where dart:io is available.
    var uri = Uri.parse(uploadUrl);
    var request = http.MultipartRequest('POST', uri)
      ..fields['file_name'] = "${username!}_$fileName"
      ..fields['username'] = username
      ..files.add(await http.MultipartFile.fromPath('file', filePath));

    http.Response response =
        await http.Response.fromStream(await request.send());
    // debugPrint("Result: ${response.body}");

    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson = json.decode(utf8decoder.convert(response.bodyBytes));
    debugPrint("upload image responseJson $responseJson");
    //
    return UploadJsonResult.fromJson(responseJson);
  }

  // 专门用于浏览器中上传图片
  Future<UploadJsonResult> uploadImageBytes(
      String? fileName, List<int>? fileBytes, String? mimeType) async {
    // 检测是否含有后缀，如果没有后缀，则添加 .png后缀
    if (!fileName!.contains(".")) {
      fileName += '.png';
    }
    String? username = SpUtil.getString(BytedeskConstants.uid);
    const uploadUrl =
        '${BytedeskConstants.httpUploadUrl}/visitor/api/upload/image';
    BytedeskUtils.printLog(
      "web uploadImageBytes fileName $fileName, username $username, upload Url $uploadUrl, bytes ${fileBytes!.length}, mimeType $mimeType",
    );

    var uri = Uri.parse(uploadUrl);
    var request = http.MultipartRequest('POST', uri)
      ..fields['file_name'] = "${username!}_$fileName"
      ..fields['username'] = username
      ..files.add(http.MultipartFile.fromBytes('file', fileBytes,
          filename: fileName, contentType: MediaType.parse(mimeType!)));

    http.Response response =
        await http.Response.fromStream(await request.send());
    // debugPrint("Result: ${response.body}");

    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson = json.decode(utf8decoder.convert(response.bodyBytes));
    debugPrint("web upload image responseJson $responseJson");
    //
    return UploadJsonResult.fromJson(responseJson);
  }

  // https://pub.dev/documentation/http/latest/http/MultipartRequest-class.html
  Future<UploadJsonResult> uploadVideo(String? filePath) async {
    // FIXME: image_picker有bug，选择视频后缀为.jpg，此处替换一下
    String? fileName = filePath!.split("/").last.replaceAll(".jpg", ".mp4");
    String? username = SpUtil.getString(BytedeskConstants.uid);
    const uploadUrl =
        '${BytedeskConstants.httpUploadUrl}/visitor/api/upload/video';
    BytedeskUtils.printLog(
        "uploadVideo fileName $fileName, username $username, upload Url $uploadUrl");
    //
    Map<String, String> headers = {
      HttpHeaders.contentTypeHeader: "video/mp4",
    };
    var uri = Uri.parse(uploadUrl);
    var request = http.MultipartRequest('POST', uri)
      ..fields['file_name'] = "${username!}_$fileName"
      ..fields['username'] = username
      ..headers.addAll(headers)
      ..files.add(await http.MultipartFile.fromPath('file', filePath,
          // FIXME: 设置不起作用？
          contentType: MediaType('video', 'mp4')));

    http.Response response =
        await http.Response.fromStream(await request.send());
    // debugPrint("Result: ${response.body}");

    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson = json.decode(utf8decoder.convert(response.bodyBytes));
    debugPrint("upload Video responseJson $responseJson");
    //
    return UploadJsonResult.fromJson(responseJson);
  }

  Future<UploadJsonResult> uploadVideoBytes(
      String? fileName, List<int>? fileBytes, String? mimeType) async {
    // FIXME: image_picker有bug，选择视频后缀为.jpg，此处替换一下
    // String? fileName = filePath!.split("/").last.replaceAll(".jpg", ".mp4");
    String? username = SpUtil.getString(BytedeskConstants.uid);
    const uploadUrl =
        '${BytedeskConstants.httpUploadUrl}/visitor/api/upload/video';
    BytedeskUtils.printLog(
        "uploadVideo fileName $fileName, username $username, upload Url $uploadUrl, bytes ${fileBytes!.length}, mimeType $mimeType");
    //
    Map<String, String> headers = {
      HttpHeaders.contentTypeHeader: "video/mp4",
    };
    var uri = Uri.parse(uploadUrl);
    var request = http.MultipartRequest('POST', uri)
      ..fields['file_name'] = "${username!}_$fileName"
      ..fields['username'] = username
      ..headers.addAll(headers)
      ..files.add(http.MultipartFile.fromBytes('file', fileBytes,
          filename: fileName,
          // FIXME: 设置不起作用？
          contentType: MediaType('video', 'mp4')));

    http.Response response =
        await http.Response.fromStream(await request.send());
    // debugPrint("Result: ${response.body}");

    //解决json解析中的乱码问题
    Utf8Decoder utf8decoder = const Utf8Decoder(); // fix 中文乱码
    //将string类型数据 转换为json类型的数据
    final responseJson = json.decode(utf8decoder.convert(response.bodyBytes));
    debugPrint("upload Video responseJson $responseJson");
    //
    return UploadJsonResult.fromJson(responseJson);
  }
}
