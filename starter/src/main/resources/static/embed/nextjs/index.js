import { jsx as n } from "react/jsx-runtime";
import { useRef as i, useEffect as o } from "react";
import u from "../node_modules/.pnpm/next@14.2.28_@babel_core@7.26.10_react-dom@18.3.1_react@18.3.1__react@18.3.1/node_modules/next/dynamic/index.js";
import { IntlProvider as m } from "react-intl";
import { messages as c } from "../locales/index/index.js";
const w = u(() => Promise.resolve(({ locale: e = "zh-cn", ...t }) => /* @__PURE__ */ n(
  m,
  {
    messages: c[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ n(d, { ...t, locale: e })
  }
)), {
  ssr: !1
  // 禁用服务端渲染
}), d = (e) => {
  const t = i(null);
  return o(() => (import("../index/index.js").then(({ default: s }) => {
    var r;
    t.current || (t.current = new s(e), t.current.init(), (r = e.onInit) == null || r.call(e), window.bytedesk = t.current);
  }), () => {
    t.current && (t.current.destroy(), delete window.bytedesk, t.current = null);
  }), [e]), null;
};
export {
  w as BytedeskNextjs
};
