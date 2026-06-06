import { __toESM as e } from "../_virtual/_rolldown/runtime/index.js";
import t from "../utils/logger/index.js";
import { messages as n } from "../locales/index/index.js";
import { require_dynamic as r } from "../node_modules/.pnpm/next@16.2.6_@babel_core@7.29.0_react-dom@19.2.6_react@19.2.6__react@19.2.6_sass@1.97.3/node_modules/next/dynamic/index.js";
import { useEffect as i, useRef as a } from "react";
import { IntlProvider as o } from "react-intl";
import { jsx as s } from "react/jsx-runtime";
//#region src/adapters/nextjs.tsx
var c = /* @__PURE__ */ e(r(), 1), l = null, u = 0, d = (0, c.default)(() => Promise.resolve(({ locale: e = "zh-cn", ...t }) => /* @__PURE__ */ s(o, {
	messages: n[e],
	locale: e,
	defaultLocale: "zh-cn",
	children: /* @__PURE__ */ s(f, {
		...t,
		locale: e
	})
})), { ssr: !1 }), f = (e) => {
	let n = a(null);
	return i(() => (u++, import("../index/index.js").then(({ default: t }) => {
		if (l) {
			n.current = l, window.bytedesk = l, e.onInit?.();
			return;
		}
		l = new t(e), n.current = l, l.init(), e.onInit?.(), window.bytedesk = l;
	}), () => {
		u--, t.debug("BytedeskNextjs: 组件卸载，当前活跃组件数:", u), n.current = null, u <= 0 && (t.debug("BytedeskNextjs: 没有活跃组件，清理全局实例"), setTimeout(() => {
			l && u <= 0 && (l.destroy(), l = null, delete window.bytedesk, u = 0);
		}, 100));
	}), [e]), null;
};
//#endregion
export { d as BytedeskNextjs };
