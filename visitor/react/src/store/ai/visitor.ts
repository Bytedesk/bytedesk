import {
  getUserProfile,
  loginVisitor,
  registerVisitor,
} from "src/apis/ai/visitor";
import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

//
interface VisitorAiState {
  // 机器人rid
  rid: string;
  // 用户信息
  userResult: VISITOR_AI.PageResultProfile;
  userProfile: VISITOR_AI.Visitor;
  loginResult: AUTH_AI.LoginResult;
  //
  isLogin: () => boolean;
  // 访客注册
  register: (rid: string) => void;
  // 访客登录
  login: (username: string, force?: boolean) => void;
  // token过期，强制登录，更新token
  loginToRefreshToken: () => void;
  // 访客个人资料
  getProfile: () => void;
  // 清空所有信息
  deleteEverything: () => void;
}

//
export const useUserAiStore = create<VisitorAiState>()(
  devtools(
    persist(
      immer((set, get) => ({
        rid: "",
        userResult: {},
        userProfile: {
          vid: "",
          username: "",
          nickname: "",
          avatar: "",
        },
        loginResult: {
          // 登录成功
          access_token: "",
          token_type: "",
          refresh_token: "",
          expires_in: 0,
          scope: "",
          jti: "",
          // 登录失败
          error: "",
          error_description: "",
        },
        isLogin: () => {
          return get().loginResult.access_token !== "";
        },
        register: async (rid: string) => {
          set({
            rid: rid,
          });
          //
          let username = get().userProfile.username;
          if (username) {
            console.log("已经存在用户，不重复注册，username:", username);
            // 登录
            get().login(username);
            return;
          }
          //
          const response = await registerVisitor();
          console.log("注册结果 register response", response);
          if (response.status === 200) {
            console.log("注册成功", response);
            set({
              userResult: response.data,
              userProfile: response.data.data,
            });
            // 登录
            get().login(response.data.data.username);
          } else {
            console.log("注册失败", response);
          }
        },
        login: async (username: string, force: boolean = false) => {
          //
          if (!force) {
            // not force login, so if local have access_token, then return
            let access_token = get().loginResult.access_token;
            if (access_token) {
              console.log("已经登录，无需重复登录");
              return;
            }
          }
          //
          if (username.trim().length == 0) {
            console.log("username should not be null");
            return;
          }
          //
          const response = await loginVisitor(username, username);
          console.log("登录结果 register response", response);
          set({
            loginResult: response.data,
          });
          localStorage.access_token = response.data.access_token;
        },
        loginToRefreshToken() {
          console.log("loginToRefreshToken");
          //
          let username = get().userProfile.username;
          // 登录
          get().login(username, true);
        },
        getProfile: async () => {
          //
          let vid = get().userProfile.vid;
          const response = await getUserProfile(vid);
          if (response.status === 200) {
            set({
              userProfile: response.data,
            });
          } else {
            console.log(
              "获取个人资料失败, 估计是token过期，尝试强制登录",
              response,
            );
            // 强制登录
            get().login(get().userProfile.username, true);
          }
        },
        deleteEverything: () => set({}, true),
      })),
      {
        name: "VISITOR_AI_STORE",
      },
    ),
  ),
);
