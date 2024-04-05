/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-04 11:58:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 16:10:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
// https://www.npmjs.com/package/@stomp/stompjs
// https://stomp-js.github.io/stomp-websocket/codo/extra/docs-src/Usage.md.html
//
// https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html
// https://stomp-js.github.io/guide/stompjs/rx-stomp/using-stomp-with-sockjs.html
import { STOMP_SOCKJS_URL, STOMP_WS_URL } from "src/utils/constants";
import * as StompJs from "@stomp/stompjs";
// import { Client, Message } from '@stomp/stompjs';
import * as SockJS from "sockjs-client";

//
// http://127.0.0.1:9003/stomp
// http://127.0.0.1:9003/sockjs
//
let stompClient: StompJs.Client;
//
export function stompConnect() {
  console.log("stompConnect");
  //
  stompClient = new StompJs.Client({
    brokerURL: STOMP_WS_URL,
    //   connectHeaders: {
    //     login: 'user',
    //     passcode: 'password',
    //   },
    // https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html#debug
    debug: function (str) {
      console.log("debug:", str);
    },
    //   https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html#auto-reconnect
    reconnectDelay: 5000,
    // https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html#heart-beating
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  // Fallback code
  if (typeof WebSocket !== "function") {
    // 仅当需要兼容低版本浏览器时，使用sockjs
    // For SockJS you need to set a factory that creates a new SockJS instance
    // to be used for each (re)connect
    stompClient.webSocketFactory = function () {
      // Note that the URL is different from the WebSocket URL
      return new SockJS(STOMP_SOCKJS_URL);
    };
  }

  // https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html#callbacks
  stompClient.onConnect = function (frame) {
    console.log("Connected: " + frame);
    // Do something, all subscribes must be done is this callback
    // This is needed because this will be executed after a (re)connect
    // 订阅消息
    stompClient.subscribe("/topic/test.greetings", (greeting) => {
      console.log("receive message: ", greeting.body);
    });
    // 发送消息
    stompClient.publish({
      destination: "/app/test.greetings",
      body: JSON.stringify({ name: "hello" }),
    });
  };

  stompClient.onWebSocketError = (error) => {
    console.error("Error with websocket", error);
  };

  stompClient.onStompError = function (frame) {
    // Will be invoked in case of error encountered at Broker
    // Bad login/passcode typically will cause an error
    // Complaint brokers will set `message` header with a brief message. Body may contain details.
    // Compliant brokers will terminate the connection after any error
    console.error("Broker reported error: " + frame.headers["message"]);
    console.error("Additional details: " + frame.body);
  };

  stompClient.activate();

  return stompClient;
}

// https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html#acknowledgment
export function stompSubscribe(topic) {
  console.log("stompSubscribe");
  stompClient.subscribe(
    topic,
    (message) => {
      console.log("receive message: ", message.body);
      if (message.body) {
        // 处理
        const messageObject = JSON.parse(message.body);
      } else {
        console.log("empty message");
      }

      // and acknowledge it
      message.ack();
    },
    { ack: "client" },
  );
}

// https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html#transactions
export function stompSendTextMessage(mid, content) {
  console.log("stompSendTextMessage");
  stompClient.publish({
    destination: "/app/test.greetings",
    body: JSON.stringify({ mid: mid, content: content }),
    // skipContentLengthHeader: true,
    // headers: { priority: '9' },
    // headers: { 'content-type': 'application/octet-stream' },
  });
}

export function stompDisconnect() {
  console.log("stompDisconnect");
  stompClient.deactivate();
}
