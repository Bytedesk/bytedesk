import 'package:bytedesk_kefu/http/bytedesk_message_api.dart';
import 'package:bytedesk_kefu/http/bytedesk_user_api.dart';
import 'package:bytedesk_kefu/model/uploadJsonResult.dart';

class TicketRepository {
  final BytedeskUserHttpApi bytedeskHttpApi = BytedeskUserHttpApi();

  final BytedeskMessageHttpApi bytedeskMessageHttpApi =
      BytedeskMessageHttpApi();

  TicketRepository();

  // Future<List<HelpCategory>> getHelpTicketCategories() async {
  //   return await bytedeskHttpApi.getHelpTicketCategories();
  // }

  // Future<JsonResult> submitTicket(String? content, List<String> imageUrls) async {
  //   return await bytedeskHttpApi.submitTicket(content, imageUrls);
  // }

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
