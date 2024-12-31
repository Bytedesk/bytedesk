import { jsx as o } from "react/jsx-runtime";
import { useRef as d, useEffect as m } from "react";
import { IntlProvider as s } from "react-intl";
import i from "../core/BytedeskWeb/index.js";
import { messages as u } from "../locales/index/index.js";
const b = ({ locale: e = "zh-CN", ...t }) => /* @__PURE__ */ o(
  s,
  {
    messages: u[e],
    locale: e,
    defaultLocale: "zh-CN",
    children: /* @__PURE__ */ o(f, { ...t, locale: e })
  }
), f = (e) => {
  const t = d(null);
  return m(() => {
    var r;
    return t.current = new i(e), t.current.init(), (r = e.onInit) == null || r.call(e), window.bytedesk = t.current, () => {
      var n;
      (n = t.current) == null || n.destroy(), delete window.bytedesk;
    };
  }, [e]), null;
};
export {
  b as BytedeskReact
};
