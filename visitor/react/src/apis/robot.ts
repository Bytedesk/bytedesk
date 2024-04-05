import { HTTP_CLIENT } from "src/utils/constants";
import request from "./request";

// 机器人相关接口
export function getRobot(wid: string) {
  return request.get<THREAD.PageResultThread>("/api/thread/workGroup", {
    params: {
      wid: wid,
      client: HTTP_CLIENT,
    },
  });
}
