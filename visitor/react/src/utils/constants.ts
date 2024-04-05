/*
 * @Author: jack ning github@bytedesk.com
 * @Date: 2023-11-17 07:49:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 15:44:21
 * @FilePath: /visitor/src/utils/constants.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
// 开发环境配置
import * as config from "./config.dev";
// 线上环境配置
// import * as config from "./config.prod";
//
export const BASE_HOST = config.BASE_HOST;
export const BASE_PORT = config.BASE_PORT;
export const BASE_URL = config.BASE_URL;
export const STOMP_WS_URL = config.STOMP_WS_URL;
export const STOMP_SOCKJS_URL = config.STOMP_SOCKJS_URL;
//
export const HTTP_CLIENT = "web";
export const PLATFORM = "weiyu-ai";
export const LOCALE = "locale";
// 登录超时
export const EVENT_BUS_LOGIN_TIMEOUT = "EVENT_BUS_LOGIN_TIMEOUT";
// 用户名或密码错误
export const EVENT_BUS_LOGIN_ERROR_400 = "EVENT_BUS_LOGIN_ERROR_400";
// 服务器错误500
export const EVENT_BUS_SERVER_ERROR_500 = "EVENT_BUS_SERVER_ERROR_500";
// token失效
export const EVENT_BUS_TOKEN_INVALID = "EVENT_BUS_TOKEN_INVALID";
//
export const EVENT_BUS_SWITCH_THEME = "EVENT_BUS_SWITCH_THEME";
//
export const THEME_MODE_TYPE = "THEME_MODE_TYPE";
export const THEME_MODE_TYPE_LIGHT = "light";
export const THEME_MODE_TYPE_DARK = "dark";
export const THEME_MODE_TYPE_SYSTEM = "system";
//
export const THEME_NAME_TYPE = "THEME_NAME_TYPE";
export const THEME_NAME_TYPE_DARK = "dark";
export const THEME_NAME_TYPE_LIGHT = "light";
