import 'package:meta/meta.dart';
import 'package:bytedesk_kefu/http/bytedesk_user_api.dart';

class ProfileRepository {
  final BytedeskUserHttpApi? bytedeskHttpApi;

  ProfileRepository({@required this.bytedeskHttpApi});
}
