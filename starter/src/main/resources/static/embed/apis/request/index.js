import { ACCESS_TOKEN as e, ANONYMOUS as t, EVENT_BUS_HTTP_ERROR as n, EVENT_BUS_SERVER_ERROR_500 as r } from "../../utils/constants/index.js";
import i from "../../utils/logger/index.js";
import a from "../../node_modules/.pnpm/axios@1.16.1_debug@4.4.3/node_modules/axios/lib/axios/index.js";
import o from "../../utils/eventsEmitter/index.js";
//#region src/apis/request.ts
var s = "";
function c() {
	return s || "https://api.weiyuai.cn";
}
function l(e) {
	return e && e.trim() !== "" ? (s = e, i.debug("API URL已设置为:", s)) : i.warn("尝试设置无效的API URL"), s;
}
var u = a.create({
	timeout: 2e4,
	baseURL: c()
});
u.interceptors.request.use((t) => {
	t.baseURL = c();
	let n = localStorage.getItem(e);
	return n && n.length > 10 && t.url && t.url.startsWith("/api") && (t.headers.Authorization = `Bearer ${n}`), !n && t.url && t.url.startsWith("/api") ? Promise.reject(f) : t;
}, (e) => (i.error("request error", e), e.response.status === 403 && o.emit(n, "403"), e.response.status === 401 && o.emit(n, "401"), Promise.reject(e))), u.interceptors.response.use((e) => e, (e) => {
	if (i.error("response error", e), e.response) switch (e.response.status) {
		case 400:
			i.error("axios interception error 400"), o.emit(n, "400");
			break;
		case 401:
			i.error("axios interception error 401"), o.emit(n, "401");
			break;
		case 403:
			i.error("axios interception error 403"), o.emit(n, "403");
			break;
		case 500:
			i.error("axios interception error 500"), o.emit(r, "500");
			break;
	}
	return "return axios interception error";
});
var d = {
	data: null,
	status: 601,
	statusText: t,
	headers: {},
	config: { headers: {} },
	request: null
}, f = {
	message: "匿名用户，无需访问服务器接口",
	name: t,
	code: "601",
	config: d.config,
	request: d.request,
	response: d,
	isAxiosError: !0,
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
//#endregion
export { u as default, c as getApiUrl, l as setApiUrl };
