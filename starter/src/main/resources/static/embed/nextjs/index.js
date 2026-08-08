import { __toESM as e } from "../_virtual/_rolldown/runtime/index.js";
import { messages as t } from "../locales/index/index.js";
import n from "../utils/logger/index.js";
import { require_dynamic as r } from "../node_modules/.pnpm/next@16.2.12_@babel_core@7.29.7_react-dom@19.2.8_react@19.2.8__react@19.2.8_sass@1.97.3/node_modules/next/dynamic/index.js";
import { useEffect as i, useRef as a } from "react";
import { IntlProvider as o } from "react-intl";
import { jsx as s } from "react/jsx-runtime";
//#region src/adapters/nextjs.tsx
var c = /* @__PURE__ */ e(r(), 1), l = null, u = 0, d = (0, c.default)(() => Promise.resolve(({ locale: e = "zh-cn", ...n }) => /* @__PURE__ */ s(o, {
	messages: t[e],
	locale: e,
	defaultLocale: "zh-cn",
	children: /* @__PURE__ */ s(f, {
		...n,
		locale: e
	})
})), { ssr: !1 }), f = (e) => {
	let t = a(null);
	return i(() => (u++, import("../index/index.js").then(({ default: n }) => {
		if (l) {
			t.current = l, window.bytedesk = l, e.onInit?.();
			return;
		}
		l = new n(e), t.current = l, l.init(), e.onInit?.(), window.bytedesk = l;
	}), () => {
		u--, n.debug("BytedeskNextjs: 组件卸载，当前活跃组件数:", u), t.current = null, u <= 0 && (n.debug("BytedeskNextjs: 没有活跃组件，清理全局实例"), setTimeout(() => {
			l && u <= 0 && (l.destroy(), l = null, delete window.bytedesk, u = 0);
		}, 100));
	}), [e]), null;
};
//#endregion
export { d as BytedeskNextjs };
