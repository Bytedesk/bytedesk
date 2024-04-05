import 'dart:io';

import 'package:bytedesk_kefu/util/bytedesk_constants.dart';
import 'package:bytedesk_kefu/util/bytedesk_utils.dart';
import 'package:sp_util/sp_util.dart';
import 'package:http/http.dart' as http;

class BytedeskBaseHttpApi {
  //
  String client = BytedeskUtils.getClient();
  String baseUrl = BytedeskUtils.getBaseUrl();
  //
  final http.Client httpClient = http.Client();

  BytedeskBaseHttpApi();
  //
  Map<String, String> getHeaders() {
    String? accessToken = SpUtil.getString(BytedeskConstants.accessToken);
    Map<String, String> headers = {
      HttpHeaders.contentTypeHeader: "application/json",
      HttpHeaders.authorizationHeader: "Bearer $accessToken"
    };
    return headers;
  }

  //
  Map<String, String> getHeadersForVisitor() {
    Map<String, String> headers = {
      HttpHeaders.contentTypeHeader: "application/json",
    };
    return headers;
  }

}
