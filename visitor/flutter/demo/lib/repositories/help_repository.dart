import 'package:bytedesk_kefu/http/bytedesk_faq_api.dart';
import 'package:bytedesk_kefu/model/helpArticle.dart';
import 'package:bytedesk_kefu/model/helpCategory.dart';

class HelpRepository {
  //
  final BytedeskFaqHttpApi bytedeskHttpApi = BytedeskFaqHttpApi();

  HelpRepository();

  Future<List<HelpCategory>> getHelpCategories(String? uid) async {
    return await bytedeskHttpApi.getHelpSupportCategories(uid);
  }

  Future<List<HelpArticle>> getCategoryArticles(int? categoryId) async {
    return await bytedeskHttpApi.getHelpSupportArticles(categoryId);
  }
}
