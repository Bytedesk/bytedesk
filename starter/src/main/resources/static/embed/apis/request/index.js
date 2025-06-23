import "../../node_modules/.pnpm/axios@1.10.0/node_modules/axios/index/index.js";
import { ACCESS_TOKEN as u, ANONYMOUS as n, EVENT_BUS_HTTP_ERROR as s, EVENT_BUS_SERVER_ERROR_500 as l } from "../../utils/constants/index.js";
import t from "../../utils/eventsEmitter/index.js";
import p from "../../node_modules/.pnpm/axios@1.10.0/node_modules/axios/lib/axios/index.js";
let o = "";
function a() {
  return o || "https://api.weiyuai.cn";
}
function R(e) {
  return e && e.trim() !== "" ? (o = e, console.log("API URL已设置为:", o)) : console.warn("尝试设置无效的API URL"), o;
}
const c = p.create({
  timeout: 2e4,
  // 初始化时设置一个默认值，后续会通过request拦截器动态获取
  baseURL: a()
});
c.interceptors.request.use(
  (e) => {
    e.baseURL = a();
    const r = localStorage.getItem(u);
    return r && r.length > 10 && e.url && e.url.startsWith("/api") && (e.headers.Authorization = `Bearer ${r}`), !r && e.url && e.url.startsWith("/api") ? Promise.reject(m) : e;
  },
  (e) => (console.log("request error", e), e.response.status === 403 && t.emit(s, "403"), e.response.status === 401 && t.emit(s, "401"), Promise.reject(e))
);
c.interceptors.response.use(
  (e) => e,
  (e) => {
    if (console.log("response error", e), e.response)
      switch (e.response.status) {
        case 400:
          console.log("axios interception error 400"), t.emit(s, "400");
          break;
        case 401:
          console.log("axios interception error 401"), t.emit(s, "401");
          break;
        case 403:
          console.log("axios interception error 403"), t.emit(s, "403");
          break;
        case 500:
          console.log("axios interception error 500"), t.emit(l, "500");
          break;
      }
    return "return axios interception error";
  }
);
const i = {
  data: null,
  // 通常错误时不会有数据
  status: 601,
  // 自定义HTTP状态码，用于表示匿名状态
  statusText: n,
  // HTTP 状态文本
  headers: {},
  // 响应头
  config: {
    headers: {}
  },
  // 请求配置
  request: null
  // 原始请求对象
}, m = {
  message: "匿名用户，无需访问服务器接口",
  // 错误消息
  name: n,
  // 错误名称
  code: "601",
  // 自定义的错误代码
  config: i.config,
  // 请求配置
  request: i.request,
  // 原始请求对象
  response: i,
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
  c as default,
  a as getApiUrl,
  R as setApiUrl
};
