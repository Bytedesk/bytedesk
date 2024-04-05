/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2023-11-17 14:57:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-28 10:49:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
import { getUserProfile, loginVisitor, registerVisitor } from "src/apis/user";
import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface UserState {
  // 技能组wid
  wid: string;
  // 用户信息
  userResult: USER.PageResultProfile;
  userProfile: USER.Profile;
  loginResult: AUTH.LoginResult;
  //
  isLogin: () => boolean;
  // 访客注册
  register: (wid: string) => void;
  // 访客登录
  login: (username: string, force?: boolean) => void;
  // token过期，强制登录，更新token
  loginToRefreshToken: () => void;
  // 访客个人资料
  getProfile: () => void;
  // 清空所有信息
  deleteEverything: () => void;
}

export const useUserStore = create<UserState>()(
  devtools(
    // persist(
    immer((set, get) => ({
      wid: "",
      userResult: {},
      userProfile: {
        uid: "",
        username: "",
        nickname: "",
        avatar: "",
        subDomain: "",
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
      register: async (wid: string) => {
        set({
          wid: wid,
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
        const response = await registerVisitor(wid);
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
        let access_token = get().loginResult.access_token;
        if (access_token && !force) {
          console.log("已经登录，无需重复登录");
          return;
        }

        if (force) {
          // token过期的情况下，强制登录
          console.log("强制登录");
        }

        const response = await loginVisitor(username);
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
        let uid = get().userProfile.uid;
        const response = await getUserProfile(uid);
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
      name: "VISITOR_STORE_VISITOR",
    },
  ),
  // ),
);
