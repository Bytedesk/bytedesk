import { jsx as i } from "react/jsx-runtime";
import { useRef as d, useEffect as m } from "react";
import u from "../node_modules/.pnpm/next@14.2.28_@babel_core@7.26.10_react-dom@18.3.1_react@18.3.1__react@18.3.1/node_modules/next/dynamic/index.js";
import { IntlProvider as c } from "react-intl";
import { messages as f } from "../locales/index/index.js";
let t = null, n = 0;
const B = u(() => Promise.resolve(({ locale: e = "zh-cn", ...o }) => /* @__PURE__ */ i(
  c,
  {
    messages: f[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ i(y, { ...o, locale: e })
  }
)), {
  ssr: !1
  // 禁用服务端渲染
}), y = (e) => {
  const o = d(null);
  return m(() => (n++, import("../index/index.js").then(({ default: r }) => {
    var s, l;
    if (t) {
      console.log("BytedeskNextjs: 使用现有全局实例，当前活跃组件数:", n), o.current = t, window.bytedesk = t, (s = e.onInit) == null || s.call(e);
      return;
    }
    console.log("BytedeskNextjs: 创建新的全局实例"), t = new r(e), o.current = t, t.init(), (l = e.onInit) == null || l.call(e), window.bytedesk = t;
  }), () => {
    n--, console.log("BytedeskNextjs: 组件卸载，当前活跃组件数:", n), o.current = null, n <= 0 && (console.log("BytedeskNextjs: 没有活跃组件，清理全局实例"), setTimeout(() => {
      t && n <= 0 && (t.destroy(), t = null, delete window.bytedesk, n = 0);
    }, 100));
  }), [e]), null;
};
export {
  B as BytedeskNextjs
};
