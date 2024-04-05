import 'package:bytedesk_kefu/http/bytedesk_feedback_api.dart';
import 'package:bytedesk_kefu/http/bytedesk_message_api.dart';
import 'package:bytedesk_kefu/model/bfeedbackJsonResult.dart';
// import 'package:bytedesk_kefu/model/helpCategory.dart';
import 'package:bytedesk_kefu/model/model.dart';
// import 'package:bytedesk_kefu/model/jsonResult.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';

class FeedbackRepository {
  final BytedeskFeedbackHttpApi bytedeskHttpApi = BytedeskFeedbackHttpApi();

  final BytedeskMessageHttpApi bytedeskMessageHttpApi = BytedeskMessageHttpApi();

  FeedbackRepository();

  Future<List<HelpCategory>> getHelpFeedbackCategories(String? uid) async {
    return await bytedeskHttpApi.getHelpFeedbackCategories(uid);
  }

  Future<FeedbackJsonResult> submitFeedback(String? cid, String? mobile,
      String? content, List<String>? imageUrls) async {
    return await bytedeskHttpApi.submitFeedback(cid, mobile, content, imageUrls);
  }

  Future<List<BFeedback>> getMyFeedback() async {
    return await bytedeskHttpApi.getMyFeedback();
  }

  Future<UploadJsonResult> uploadImage(String? filePath) async {
    return await bytedeskMessageHttpApi.uploadImage(filePath);
  }

  Future<UploadJsonResult> uploadImageBytes(
      String? fileName, List<int>? fileBytes, String? mimeType) async {
    return await bytedeskMessageHttpApi.uploadImageBytes(
        fileName, fileBytes, mimeType);
  }

  // Future<String> upload(String? filePath) async {
  //   return await bytedeskHttpApi.upload(filePath);
  // }


}
