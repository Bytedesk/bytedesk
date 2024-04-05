import 'package:bytedesk_kefu/model/message.dart';
import 'package:bytedesk_kefu/model/robot.dart';
import 'package:event_bus/event_bus.dart';

EventBus bytedeskEventBus = EventBus();

class ReceiveMessageEventBus {
  Message message;
  ReceiveMessageEventBus(this.message);
}

class ReceiveMessageReceiptEventBus {
  String mid;
  String status;
  ReceiveMessageReceiptEventBus(this.mid, this.status);
}

class ReceiveMessagePreviewEventBus {
  String content;
  ReceiveMessagePreviewEventBus(this.content);
}

class ReceiveMessageRecallEventBus {
  String mid;
  ReceiveMessageRecallEventBus(this.mid);
}

class ConnectionEventBus {
  String content;
  ConnectionEventBus(this.content);
}

class DeleteMessageEventBus {
  String mid;
  DeleteMessageEventBus(this.mid);
}

// token过期
class InvalidTokenEventBus {
  InvalidTokenEventBus();
}

class QueryAnswerEventBus {
  String aid;
  String question;
  String answer;
  QueryAnswerEventBus(this.aid, this.question, this.answer);
}

class QueryCategoryEventBus {
  String cid;
  String name;
  QueryCategoryEventBus(this.cid, this.name);
}

class QueryRobotEventBus {
  String cid;
  String name;
  QueryRobotEventBus(this.cid, this.name);
}

class RequestAgentThreadEventBus {
  RequestAgentThreadEventBus();
}

class ChooseRobotEventBus {
  Robot robot;
  ChooseRobotEventBus(this.robot);
}

class SmsLoginSuccessEventBus {
  String mobile;
  SmsLoginSuccessEventBus(this.mobile);
}
