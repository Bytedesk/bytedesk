import { jsx as l } from "react/jsx-runtime";
import { useRef as d, useEffect as m } from "react";
import u from "../node_modules/.pnpm/next@14.2.28_@babel_core@7.26.10_react-dom@18.3.1_react@18.3.1__react@18.3.1/node_modules/next/dynamic/index.js";
import { IntlProvider as f } from "react-intl";
import { messages as c } from "../locales/index/index.js";
let t = null, n = 0;
const x = u(() => Promise.resolve(({ locale: e = "zh-cn", ...o }) => /* @__PURE__ */ l(
  f,
  {
    messages: c[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ l(y, { ...o, locale: e })
  }
)), {
  ssr: !1
  // 禁用服务端渲染
}), y = (e) => {
  const o = d(null);
  return m(() => (n++, import("../index/index.js").then(({ default: r }) => {
    var s, i;
    if (t) {
      o.current = t, window.bytedesk = t, (s = e.onInit) == null || s.call(e);
      return;
    }
    t = new r(e), o.current = t, t.init(), (i = e.onInit) == null || i.call(e), window.bytedesk = t;
  }), () => {
    n--, console.log("BytedeskNextjs: 组件卸载，当前活跃组件数:", n), o.current = null, n <= 0 && (console.log("BytedeskNextjs: 没有活跃组件，清理全局实例"), setTimeout(() => {
      t && n <= 0 && (t.destroy(), t = null, delete window.bytedesk, n = 0);
    }, 100));
  }), [e]), null;
};
export {
  x as BytedeskNextjs
};
