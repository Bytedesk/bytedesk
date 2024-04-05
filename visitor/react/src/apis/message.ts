// import axios from 'axios';
import { BASE_URL, HTTP_CLIENT } from "src/utils/constants";
import request from "./request";

export function getMessages(wid: string) {
  return request.get<VISITOR_AI.Visitor>("/api/thread/workGroup", {
    params: {
      wId: wid,
      client: HTTP_CLIENT,
    },
  });
}

export async function sendMessageSSE(
  message: MESSAGE.knowledge_base_chat,
  callback: (jsonContent: string) => void,
) {
  //
  console.log("sendMessageSSE: ", message);
  //
  const url = `${BASE_URL}/q?query=${message.query}&kbname=${message.knowledge_base_name}`;
  const eventSource = new EventSource(url);
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
    alert("服务异常请重试并联系开发者！");
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
