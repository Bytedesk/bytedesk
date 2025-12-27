import { jsx as d } from "react/jsx-runtime";
import { useRef as u, useEffect as l } from "react";
import f from "../node_modules/.pnpm/next@14.2.28_@babel_core@7.26.10_react-dom@18.3.1_react@18.3.1__react@18.3.1/node_modules/next/dynamic/index.js";
import { IntlProvider as c } from "react-intl";
import { messages as y } from "../locales/index/index.js";
import s from "../utils/logger/index.js";
let t = null, n = 0;
const h = f(() => Promise.resolve(({ locale: e = "zh-cn", ...r }) => /* @__PURE__ */ d(
  c,
  {
    messages: y[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ d(k, { ...r, locale: e })
  }
)), {
  ssr: !1
  // 禁用服务端渲染
}), k = (e) => {
  const r = u(null);
  return l(() => (n++, import("../index/index.js").then(({ default: m }) => {
    var i, o;
    if (t) {
      r.current = t, window.bytedesk = t, (i = e.onInit) == null || i.call(e);
      return;
    }
    t = new m(e), r.current = t, t.init(), (o = e.onInit) == null || o.call(e), window.bytedesk = t;
  }), () => {
    n--, s.debug("BytedeskNextjs: 组件卸载，当前活跃组件数:", n), r.current = null, n <= 0 && (s.debug("BytedeskNextjs: 没有活跃组件，清理全局实例"), setTimeout(() => {
      t && n <= 0 && (t.destroy(), t = null, delete window.bytedesk, n = 0);
    }, 100));
  }), [e]), null;
};
export {
  h as BytedeskNextjs
};
