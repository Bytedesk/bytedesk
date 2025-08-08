import { jsx as d } from "react/jsx-runtime";
import { useRef as o, useEffect as l } from "react";
import { IntlProvider as m } from "react-intl";
import f from "../core/BytedeskWeb/index.js";
import { messages as c } from "../locales/index/index.js";
const B = ({ locale: e = "zh-cn", ...n }) => /* @__PURE__ */ d(
  m,
  {
    messages: c[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ d(s, { ...n, locale: e })
  }
);
let t = null, i = 0;
const s = (e) => {
  const n = o(null);
  return l(() => {
    var r, u;
    if (i++, t) {
      n.current = t, window.bytedesk = t, (r = e.onInit) == null || r.call(e);
      return;
    }
    return t = new f(e), n.current = t, t.init(), (u = e.onInit) == null || u.call(e), window.bytedesk = t, () => {
      i--, n.current = null, i <= 0 && setTimeout(() => {
        t && i <= 0 && (t.destroy(), t = null, delete window.bytedesk, i = 0);
      }, 100);
    };
  }, [e]), null;
};
export {
  B as BytedeskReact
};
