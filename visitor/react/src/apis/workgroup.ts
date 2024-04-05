import { HTTP_CLIENT } from "src/utils/constants";
import request from "./request";

// 获取技能组基本信息接口
export function getWorkGroup(wid: string) {
  return request.get<WORKGROUP.HttpResultWorkGroup>(
    "/visitor/api/v3/workGroup",
    {
      params: {
        wid: wid,
        client: HTTP_CLIENT,
      },
    },
  );
}
