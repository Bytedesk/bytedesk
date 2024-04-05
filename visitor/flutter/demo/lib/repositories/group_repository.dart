import 'package:meta/meta.dart';
import 'package:bytedesk_kefu/http/bytedesk_user_api.dart';

class GroupRepository {
  final BytedeskUserHttpApi? bytedeskHttpApi;

  GroupRepository({@required this.bytedeskHttpApi});

  // Future<List<Group>> getGroups() async {
  //   return await bytedeskHttpApi.getGroups();
  // }
}
