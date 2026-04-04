import { jsx as c } from "react/jsx-runtime";
import { useRef as m, useEffect as u } from "react";
import { IntlProvider as y } from "react-intl";
import l from "../core/BytedeskWeb/index.js";
import { messages as g } from "../locales/index/index.js";
const s = (e) => Array.isArray(e) ? e.map(s) : typeof e == "function" ? e.toString() : e && typeof e == "object" ? Object.keys(e).sort().reduce((n, r) => (n[r] = s(e[r]), n), {}) : e, p = (e) => JSON.stringify(s(e)), C = ({ locale: e = "zh-cn", ...n }) => /* @__PURE__ */ c(
  y,
  {
    messages: g[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ c(b, { ...n, locale: e })
  }
);
let t = null, o = 0;
const b = (e) => {
  const n = m(null), { onInit: r, ...i } = e, f = p(i);
  return u(() => (o++, t && (t.destroy(), t = null, delete window.bytedesk), t = new l(i), n.current = t, window.bytedesk = t, t.init().then(() => {
    r == null || r();
  }).catch((d) => {
    console.error("BytedeskWeb 初始化失败:", d), r == null || r();
  }), () => {
    o--, n.current = null, o <= 0 && setTimeout(() => {
      t && o <= 0 && (t.destroy(), t = null, delete window.bytedesk, o = 0);
    }, 100);
  }), [f]), null;
};
export {
  C as BytedeskReact
};
