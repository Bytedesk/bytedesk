// import axios from 'axios';
import { HTTP_CLIENT } from "src/utils/constants";
import request from "./request";

export function requestThread(wid: string) {
  return request.get<MESSAGE.PageResultMessage>("/api/thread/workGroup", {
    params: {
      wid: wid,
      client: HTTP_CLIENT,
    },
  });
}
