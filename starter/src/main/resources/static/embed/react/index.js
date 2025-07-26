import { jsx as d } from "react/jsx-runtime";
import { useRef as s, useEffect as i } from "react";
import { IntlProvider as r } from "react-intl";
import u from "../core/BytedeskWeb/index.js";
import { messages as m } from "../locales/index/index.js";
const w = ({ locale: e = "zh-cn", ...o }) => /* @__PURE__ */ d(
  r,
  {
    messages: m[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ d(f, { ...o, locale: e })
  }
);
let t = null, n = 0;
const f = (e) => {
  const o = s(null);
  return i(() => {
    var l, c;
    if (n++, t) {
      console.log("BytedeskReact: 使用现有全局实例，当前活跃组件数:", n), o.current = t, window.bytedesk = t, (l = e.onInit) == null || l.call(e);
      return;
    }
    return console.log("BytedeskReact: 创建新的全局实例"), t = new u(e), o.current = t, t.init(), (c = e.onInit) == null || c.call(e), window.bytedesk = t, () => {
      n--, console.log("BytedeskReact: 组件卸载，当前活跃组件数:", n), o.current = null, n <= 0 && (console.log("BytedeskReact: 没有活跃组件，清理全局实例"), setTimeout(() => {
        t && n <= 0 && (t.destroy(), t = null, delete window.bytedesk, n = 0);
      }, 100));
    };
  }, [e]), null;
};
export {
  w as BytedeskReact
};
