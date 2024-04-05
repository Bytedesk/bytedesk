/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2023-11-17 07:32:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 12:10:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
import { HTTP_CLIENT, PLATFORM } from "src/utils/constants";
import request from "./request";

// 注册访客
export function registerVisitor(wid: string) {
  return request.get<USER.PageResultProfile>(
    "/visitor/api/v3/registe/visitor",
    {
      params: {
        wid: wid,
        platform: PLATFORM,
        client: HTTP_CLIENT,
      },
    },
  );
}

/**
 * 访客登录OAuth2
 * @param {*} username 用户名
 */
export function loginVisitor(username: string) {
  return request<AUTH_AI.LoginResult>({
    method: "post",
    url: "/visitor/token",
    params: {
      username: username,
      password: username,
      grant_type: "password",
      scope: "all",
    },
    auth: {
      username: "client",
      password: "secret",
    },
  });
}

export function getUserProfile(uid: string) {
  return request.get<USER.Profile>("/visitor/api/profile", {
    params: {
      uid: uid,
      client: HTTP_CLIENT,
    },
  });
}

//
// export function register(
//   wid: string,
//   successcb: (jsonContent: string) => void,
//   failedcb: (jsonContent: string) => void
// ) {
//   return request.get(
//     "/visitor/api/v3/registe/visitor",
//     {
//       params: {
//         wid: wid,
//         platform: PLATFORM,
//         client: HTTP_CLIENT,
//       },
//     }
//   ).then((response) => {
//     console.log("registerVisitor", response.data);
//     successcb(response.data);
//   })
//   .catch((error) => {
//     failedcb(error);
//   });
// }
