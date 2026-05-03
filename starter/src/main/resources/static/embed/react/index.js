import e from "../index/index.js";
import { messages as t } from "../locales/index/index.js";
import { useEffect as n, useRef as r } from "react";
import { IntlProvider as i } from "react-intl";
import { jsx as a } from "react/jsx-runtime";
//#region src/adapters/react.tsx
var o = (e) => Array.isArray(e) ? e.map(o) : typeof e == "function" ? e.toString() : e && typeof e == "object" ? Object.keys(e).sort().reduce((t, n) => (t[n] = o(e[n]), t), {}) : e, s = (e) => JSON.stringify(o(e)), c = ({ locale: e = "zh-cn", ...n }) => /* @__PURE__ */ a(i, {
	messages: t[e],
	locale: e,
	defaultLocale: "zh-cn",
	children: /* @__PURE__ */ a(d, {
		...n,
		locale: e
	})
}), l = null, u = 0, d = (t) => {
	let i = r(null), a = r(!1), { onInit: o, ...c } = t;
	return n(() => (u++, l ? (i.current = l, window.bytedesk = l, l.setConfig(c, { replaceChatConfig: !0 }), a.current || (a.current = !0, o?.())) : (l = new e(c), i.current = l, window.bytedesk = l, l.init().then(() => {
		a.current = !0, o?.();
	}).catch((e) => {
		console.error("BytedeskWeb 初始化失败:", e), a.current = !0, o?.();
	})), () => {
		u--, i.current = null, u <= 0 && setTimeout(() => {
			l && u <= 0 && (l.destroy(), l = null, delete window.bytedesk, u = 0);
		}, 100);
	}), [s(c)]), null;
};
//#endregion
export { c as BytedeskReact };
