import { jsx as o } from "react/jsx-runtime";
import { useRef as d, useEffect as m } from "react";
import { IntlProvider as s } from "react-intl";
import i from "../core/BytedeskWeb/index.js";
import { messages as u } from "../locales/index/index.js";
const b = ({ locale: e = "zh-cn", ...t }) => /* @__PURE__ */ o(
  s,
  {
    messages: u[e],
    locale: e,
    defaultLocale: "zh-cn",
    children: /* @__PURE__ */ o(c, { ...t, locale: e })
  }
), c = (e) => {
  const t = d(null);
  return m(() => {
    var n;
    return t.current = new i(e), t.current.init(), (n = e.onInit) == null || n.call(e), window.bytedesk = t.current, () => {
      var r;
      (r = t.current) == null || r.destroy(), delete window.bytedesk;
    };
  }, [e]), null;
};
export {
  b as BytedeskReact
};
