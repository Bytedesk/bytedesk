import { onMount as i, onDestroy as s } from "svelte";
import { addMessages as a, init as c, getLocaleFromNavigator as n } from "svelte-i18n";
import m from "../core/BytedeskWeb/index.js";
import { messages as f } from "../locales/index/index.js";
Object.entries(f).forEach(([l, o]) => {
  a(l, o);
});
c({
  fallbackLocale: "en",
  initialLocale: n()
});
let e = null, t = 0;
const y = (l, o) => (console.log("config", o, l), i(() => {
  t++;
  const r = {
    ...o,
    locale: o.locale || n() || "zh-cn"
  };
  e || (e = new m(r), e.init());
}), s(() => {
  t--, t <= 0 && setTimeout(() => {
    e && t <= 0 && (e.destroy(), e = null, t = 0);
  }, 100);
}), {
  destroy() {
  }
});
export {
  y as BytedeskSvelte
};
