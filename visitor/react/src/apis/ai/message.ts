// import axios from 'axios';
import { BASE_URL, HTTP_CLIENT } from "src/utils/constants";
import { EventSourcePolyfill } from "event-source-polyfill";
// import request from '../request';

// export function getMessages(wid: string) {
//   return request.get<VISITOR_AI.Visitor>('/api/thread/workGroup', {
//     params: {
//       wId: wid,
//       client: HTTP_CLIENT,
//     },
//   });
// }

// https://tr.javascript.info/server-sent-events
export async function sendMessageSSE(
  message: MESSAGE_AI.knowledge_base_chat,
  callback: (jsonContent: string) => void,
) {
  //
  console.log("sendMessageSSE: ", message);
  //
  const url = `${BASE_URL}/api/v1/chat/q?query=${message.query}&rid=${message.rid}`;
  const eventSource = new EventSource(url, {
    withCredentials: true,
  });
  eventSource.onopen = (event) => {
    console.log("onopen:", event.target);
    // sse = event.target;
  };
  eventSource.onmessage = (event) => {
    // onmessage: {"id": "8059178098561234419", "answer": " 根据", "type": "add"}
    // onmessage: {"id": "8059178098561234419", "answer": "", "type": "finish"}
    // console.log("onmessage:", event.data);
    callback(event.data);
    //
    let json_data = JSON.parse(event.data);
    if (json_data.type == "finish") {
      if (eventSource) {
        eventSource.close();
      }
      return;
    }
  };
  eventSource.onerror = (event) => {
    console.log("onerror:", event);
    // sse = event.target;
    alert("server error");
    if (eventSource.readyState === EventSource.CLOSED) {
      console.log("connection is closed");
    } else {
      console.log("Error occured", event);
    }
    eventSource.close();
  };
  //
  eventSource.addEventListener("customEventName", (event) => {
    console.log("Message id is " + event.lastEventId);
  });
}

// add headers to sse
// https://blog.csdn.net/lx1996082566/article/details/116004768
export async function sendMessageSSEPolyfill(
  message: MESSAGE_AI.knowledge_base_chat,
  callback: (jsonContent: string) => void,
) {
  //
  console.log("sendMessageSSEPolyfill: ", message);
  //
  let token = localStorage.access_token;
  const url = `${BASE_URL}/api/v1/chat/q?query=${message.query}&rid=${message.rid}`;
  const eventSource = new EventSourcePolyfill(url, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  eventSource.onopen = (event) => {
    console.log("onopen:", event.target);
    // sse = event.target;
  };
  eventSource.onmessage = (event) => {
    // onmessage: {"id": "8059178098561234419", "answer": " 根据", "type": "add"}
    // onmessage: {"id": "8059178098561234419", "answer": "", "type": "finish"}
    // console.log("onmessage:", event.data);
    callback(event.data);
    //
    let json_data = JSON.parse(event.data);
    if (json_data.type == "finish") {
      if (eventSource) {
        eventSource.close();
      }
      return;
    }
  };
  eventSource.onerror = (event) => {
    console.log("onerror:", event);
    // sse = event.target;
    alert("server error");
    if (eventSource.readyState === EventSource.CLOSED) {
      console.log("connection is closed");
    } else {
      console.log("Error occured", event);
    }
    eventSource.close();
  };
  //
  eventSource.addEventListener("customEventName", (event) => {
    console.log("Message id is " + event.lastEventId);
  });
}
