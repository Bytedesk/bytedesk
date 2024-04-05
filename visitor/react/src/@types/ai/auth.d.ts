// @ts-ignore
/* eslint-disable */

declare namespace AUTH_AI {
  /**
  //  登录成功
  {
    "access_token": "eyJhbG.......",
    "token_type": "bearer",
    "refresh_token": "eyJhbG...",
    "expires_in": 2591999,
    "scope": "all",
    "jti": "d64bc73f-5d84-4d19-a640-980b77c63395"
  }
  // 登录失败
  {
    "error" : "invalid_request",
    "error_description" : "Missing grant type"
  }
  */
  type LoginResult = {
    // 登录成功
    access_token?: string;
    token_type?: string;
    //
    // refresh_token?: string;
    // expires_in?: int;
    // scope?: string;
    // jti?: string;
    // // 登录失败
    // error?: string;
    // error_description?: string;
    // //
    // status?: string;
    // type?: string;
    // currentAuthority?: string;
  };

  type PageParams = {
    page?: number;
    size?: number;
    type?: string;
  };

  type PageResult = {
    message?: string;
    status_code?: Int;
    data?: {
      content: [];
      empty?: boolean;
      first?: boolean;
      last?: boolean;
      number?: 0;
      numberOfElements?: Int;
    };
  };

  type HttpResult = {
    message?: string;
    status_code?: Int;
    data?: any;
  };

  type LoginParams = {
    username?: string;
    password?: string;
    grant_type: "password";
    scope: "all";
  };

  type RegisterParams = {
    email?: string;
    password?: string;
    mobile?: string;
    code?: string;
  };
}
