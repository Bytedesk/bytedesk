import { jsx as f } from "react/jsx-runtime";
import { useRef as u, useEffect as y } from "react";
import { IntlProvider as l } from "react-intl";
import g from "../core/BytedeskWeb/index.js";
import { messages as p } from "../locales/index/index.js";
const i = (e) => Array.isArray(e) ? e.map(i) : typeof e == "function" ? e.toString() : e && typeof e == "object" ? Object.keys(e).sort().reduce((n, o) => (n[o] = i(e[o]), n), {}) : e, a = (e) => JSON.stringify(i(e)), R = ({ locale: e = "zh-cn", ...n }) => /* @__PURE__ */ f(
  l,
  {
    messages: p[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ f(b, { ...n, locale: e })
  }
);
let t = null, s = 0;
const b = (e) => {
  const n = u(null), o = u(!1), { onInit: r, ...c } = e, d = a(c);
  return y(() => (s++, t ? (n.current = t, window.bytedesk = t, t.setConfig(c), o.current || (o.current = !0, r == null || r())) : (t = new g(c), n.current = t, window.bytedesk = t, t.init().then(() => {
    o.current = !0, r == null || r();
  }).catch((m) => {
    console.error("BytedeskWeb 初始化失败:", m), o.current = !0, r == null || r();
  })), () => {
    s--, n.current = null, s <= 0 && setTimeout(() => {
      t && s <= 0 && (t.destroy(), t = null, delete window.bytedesk, s = 0);
    }, 100);
  }), [d]), null;
};
export {
  R as BytedeskReact
};
