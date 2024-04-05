import 'package:bytedesk_kefu/http/bytedesk_user_api.dart';

class ContactRepository {
  final BytedeskUserHttpApi bytedeskHttpApi = BytedeskUserHttpApi();

  ContactRepository();

  // Future<List<Contact>> getContacts() async {
  //   return await bytedeskHttpApi.getContacts();
  // }
}
