// import axios from "axios";
// import { request } from "@umijs/max";
import { HTTP_CLIENT } from "src/utils/constants";
import request from "../request";

// 注册访客
export function registerVisitor() {
  return request<VISITOR_AI.PageResultProfile>({
    method: "post",
    url: "/api/v1/oauth/register/visitor",
    params: {
      client: HTTP_CLIENT,
    },
  });
}

/**
 * 访客登录OAuth2
 * @param {*} username 用户名
 */
export function loginVisitor(username: string, password: string) {
  return request<AUTH_AI.LoginResult>({
    method: "post",
    url: "/api/v1/oauth/login/visitor",
    data: {
      username: username,
      password: password,
      grant_type: "password",
      scope: "all",
    },
  });
}

export function getUserProfile(uid: string) {
  return request.get<VISITOR_AI.Visitor>("/visitor/api/profile", {
    params: {
      uid: uid,
      client: HTTP_CLIENT,
    },
  });
}
