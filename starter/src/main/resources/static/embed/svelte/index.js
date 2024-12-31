import { onMount as l, onDestroy as s } from "svelte";
import { addMessages as a, init as m, getLocaleFromNavigator as r } from "svelte-i18n";
import i from "../core/BytedeskWeb/index.js";
import { messages as d } from "../locales/index/index.js";
Object.entries(d).forEach(([t, o]) => {
  a(t, o);
});
m({
  fallbackLocale: "en-US",
  initialLocale: r()
});
const p = (t, o) => {
  let e = null;
  return console.log("config", o, t), l(() => {
    e = new i({
      ...o,
      locale: o.locale || r() || "zh-CN"
    }), e.init();
  }), s(() => {
    e == null || e.destroy();
  }), {
    destroy() {
      e == null || e.destroy();
    }
  };
};
export {
  p as BytedeskSvelte
};
