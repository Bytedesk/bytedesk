import "../../node_modules/.pnpm/axios@1.10.0/node_modules/axios/index/index.js";
import { ACCESS_TOKEN as c, ANONYMOUS as a, EVENT_BUS_HTTP_ERROR as s, EVENT_BUS_SERVER_ERROR_500 as m } from "../../utils/constants/index.js";
import t from "../../utils/eventsEmitter/index.js";
import r from "../../utils/logger/index.js";
import l from "../../node_modules/.pnpm/axios@1.10.0/node_modules/axios/lib/axios/index.js";
let o = "";
function u() {
  return o || "https://api.weiyuai.cn";
}
function d(e) {
  return e && e.trim() !== "" ? (o = e, r.info("API URL已设置为:", o)) : r.warn("尝试设置无效的API URL"), o;
}
const p = l.create({
  timeout: 2e4,
  // 初始化时设置一个默认值，后续会通过request拦截器动态获取
  baseURL: u()
});
p.interceptors.request.use(
  (e) => {
    e.baseURL = u();
    const i = localStorage.getItem(c);
    return i && i.length > 10 && e.url && e.url.startsWith("/api") && (e.headers.Authorization = `Bearer ${i}`), !i && e.url && e.url.startsWith("/api") ? Promise.reject(h) : e;
  },
  (e) => (r.error("request error", e), e.response.status === 403 && t.emit(s, "403"), e.response.status === 401 && t.emit(s, "401"), Promise.reject(e))
);
p.interceptors.response.use(
  (e) => e,
  (e) => {
    if (r.error("response error", e), e.response)
      switch (e.response.status) {
        case 400:
          r.error("axios interception error 400"), t.emit(s, "400");
          break;
        case 401:
          r.error("axios interception error 401"), t.emit(s, "401");
          break;
        case 403:
          r.error("axios interception error 403"), t.emit(s, "403");
          break;
        case 500:
          r.error("axios interception error 500"), t.emit(m, "500");
          break;
      }
    return "return axios interception error";
  }
);
const n = {
  data: null,
  // 通常错误时不会有数据
  status: 601,
  // 自定义HTTP状态码，用于表示匿名状态
  statusText: a,
  // HTTP 状态文本
  headers: {},
  // 响应头
  config: {
    headers: {}
  },
  // 请求配置
  request: null
  // 原始请求对象
}, h = {
  message: "匿名用户，无需访问服务器接口",
  // 错误消息
  name: a,
  // 错误名称
  code: "601",
  // 自定义的错误代码
  config: n.config,
  // 请求配置
  request: n.request,
  // 原始请求对象
  response: n,
  // 响应对象
  isAxiosError: !0,
  // 标记这是一个 AxiosError 对象
  toJSON: function() {
    return {
      message: this.message,
      name: this.name,
      code: this.code,
      config: this.config,
      request: this.request,
      response: this.response
    };
  }
};
export {
  p as default,
  u as getApiUrl,
  d as setApiUrl
};
