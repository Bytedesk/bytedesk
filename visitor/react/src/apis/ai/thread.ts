// import axios from 'axios';
import { HTTP_CLIENT } from "src/utils/constants";
import request from "../request";

//
export function requestThread(rid: string) {
  return request.get<THREAD_AI.HttpResultThread>("/api/v1/thread/get", {
    params: {
      rid: rid,
      client: HTTP_CLIENT,
    },
  });
}
