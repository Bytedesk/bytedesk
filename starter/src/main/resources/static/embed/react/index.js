import { jsx as l } from "react/jsx-runtime";
import { useRef as r, useEffect as u } from "react";
import { IntlProvider as m } from "react-intl";
import s from "../core/BytedeskWeb/index.js";
import { messages as c } from "../locales/index/index.js";
const h = ({ locale: e = "zh-cn", ...o }) => /* @__PURE__ */ l(
  m,
  {
    messages: c[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ l(f, { ...o, locale: e })
  }
);
let t = null, n = 0;
const f = (e) => {
  const o = r(null);
  return u(() => (n++, t && (t.destroy(), t = null, delete window.bytedesk), t = new s(e), o.current = t, window.bytedesk = t, t.init().then(() => {
    var d;
    (d = e.onInit) == null || d.call(e);
  }).catch((d) => {
    var i;
    console.error("BytedeskWeb 初始化失败:", d), (i = e.onInit) == null || i.call(e);
  }), () => {
    n--, o.current = null, n <= 0 && setTimeout(() => {
      t && n <= 0 && (t.destroy(), t = null, delete window.bytedesk, n = 0);
    }, 100);
  }), [e]), null;
};
export {
  h as BytedeskReact
};
