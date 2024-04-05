// import 'package:meta/meta.dart';
import 'package:bytedesk_kefu/http/bytedesk_thread_api.dart';
import 'package:bytedesk_kefu/model/markThread.dart';
// import 'package:bytedesk_kefu/model/requestThread.dart';
import 'package:bytedesk_kefu/model/model.dart';
import 'package:bytedesk_kefu/model/requestThreadZhipuAI.dart';
import 'package:bytedesk_kefu/model/threadZhipuAI.dart';
// import 'package:http/http.dart' as http;

class ThreadRepository {
  //
  final BytedeskThreadHttpApi bytedeskHttpApi = BytedeskThreadHttpApi();

  ThreadRepository();

  Future<List<Thread>> getThreads() async {
    return await bytedeskHttpApi.getThreads();
  }

  Future<List<Thread>> getHistoryThreads(int? page, int? size) async {
    return await bytedeskHttpApi.getHistoryThreads(page, size);
  }

  Future<List<Thread>> getVisitorThreads(int? page, int? size) async {
    return await bytedeskHttpApi.getVisitorThreads(page, size);
  }

  Future<List<Thread>> getVisitorThreadsAll() async {
    return await bytedeskHttpApi.getVisitorThreadsAll();
  }

  Future<RequestThreadResult> requestThread(
      String? wid, String? type, String? aid, bool? isV2Robot) async {
    if (isV2Robot!) {
      return await bytedeskHttpApi.requestWorkGroupThreadV2(wid);
    }
    return await bytedeskHttpApi.requestThread(wid, type, aid);
  }

  Future<RequestThreadZhipuAIResult> requestZhipuAIThread(String? wid, String? forceNew) async {
    return await bytedeskHttpApi.requestZhipuAIThread(wid, forceNew);
  }

  Future<List<ThreadZhipuAI>> getZhipuAIThreadHistory(int? page, int? size) async {
    return await bytedeskHttpApi.getZhipuAIThreadHistory(page, size);
  }

  Future<RequestThreadResult> requestAgent(
      String? wid, String? type, String? aid) async {
    return await bytedeskHttpApi.requestAgent(wid, type, aid);
  }

  Future<RequestThreadResult> requestContactThread(String? cid) async {
    return await bytedeskHttpApi.requestContactThread(cid);
  }

  Future<RequestThreadResult> requestGroupThread(String? gid) async {
    return await bytedeskHttpApi.requestGroupThread(gid);
  }

  Future<MarkThreadResult> markTop(String? tid) async {
    return await bytedeskHttpApi.markTop(tid);
  }

  Future<MarkThreadResult> unmarkTop(String? tid) async {
    return await bytedeskHttpApi.unmarkTop(tid);
  }

  Future<MarkThreadResult> markNodisturb(String? tid) async {
    return await bytedeskHttpApi.markNodisturb(tid);
  }

  Future<MarkThreadResult> unmarkNodisturb(String? tid) async {
    return await bytedeskHttpApi.unmarkNodisturb(tid);
  }

  Future<MarkThreadResult> markUnread(String? tid) async {
    return await bytedeskHttpApi.markUnread(tid);
  }

  Future<MarkThreadResult> unmarkUnread(String? tid) async {
    return await bytedeskHttpApi.unmarkUnread(tid);
  }

  Future<MarkThreadResult> delete(String? tid) async {
    return await bytedeskHttpApi.delete(tid);
  }
}
