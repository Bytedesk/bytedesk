import 'package:bytedesk_kefu/http/bytedesk_leavemsg_api.dart';
import 'package:bytedesk_kefu/http/bytedesk_message_api.dart';
import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';

class LeaveMsgRepository {
  //
  final BytedeskLeaveMsgHttpApi bytedeskHttpApi = BytedeskLeaveMsgHttpApi();

  final BytedeskMessageHttpApi bytedeskMessageHttpApi =
      BytedeskMessageHttpApi();

  LeaveMsgRepository();

  Future<List<HelpCategory>> getHelpLeaveMsgCategories(String? uid) async {
    return await bytedeskHttpApi.getHelpLeaveMsgCategories(uid);
  }

  // , List<String>? imageUrls
  Future<JsonResult> submitLeaveMsg(
      String? wid,
      String? aid,
      String? type,
      String? mobile,
      String? email,
      String? content,
      List<String>? imageUrls) async {
    return await bytedeskHttpApi.submitLeaveMsg(
        wid, aid, type, mobile, email, content, imageUrls);
  }

  // Future<String> uploadImage(String? filePath) async {
  //   return await bytedeskHttpApi.upload(filePath);
  // }

  Future<UploadJsonResult> uploadImage(String? filePath) async {
    return await bytedeskMessageHttpApi.uploadImage(filePath);
  }

  Future<UploadJsonResult> uploadImageBytes(
      String? fileName, List<int>? fileBytes, String? mimeType) async {
    return await bytedeskMessageHttpApi.uploadImageBytes(
        fileName, fileBytes, mimeType);
  }
  
}
