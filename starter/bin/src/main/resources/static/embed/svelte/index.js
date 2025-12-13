import { onMount as i, onDestroy as a } from "svelte";
import { addMessages as s, init as m, getLocaleFromNavigator as l } from "svelte-i18n";
import c from "../core/BytedeskWeb/index.js";
import { messages as f } from "../locales/index/index.js";
import u from "../utils/logger/index.js";
Object.entries(f).forEach(([r, o]) => {
  s(r, o);
});
m({
  fallbackLocale: "en",
  initialLocale: l()
});
let e = null, t = 0;
const k = (r, o) => (u.debug("config", o, r), i(() => {
  t++;
  const n = {
    ...o,
    locale: o.locale || l() || "zh-cn"
  };
  e || (e = new c(n), e.init());
}), a(() => {
  t--, t <= 0 && setTimeout(() => {
    e && t <= 0 && (e.destroy(), e = null, t = 0);
  }, 100);
}), {
  destroy() {
  }
});
export {
  k as BytedeskSvelte
};
