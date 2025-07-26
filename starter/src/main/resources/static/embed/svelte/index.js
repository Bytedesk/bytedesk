import { onMount as r, onDestroy as i } from "svelte";
import { addMessages as a, init as c, getLocaleFromNavigator as n } from "svelte-i18n";
import m from "../core/BytedeskWeb/index.js";
import { messages as d } from "../locales/index/index.js";
Object.entries(d).forEach(([l, t]) => {
  a(l, t);
});
c({
  fallbackLocale: "en",
  initialLocale: n()
});
let o = null, e = 0;
const u = (l, t) => (console.log("config", t, l), r(() => {
  e++;
  const s = {
    ...t,
    locale: t.locale || n() || "zh-cn"
  };
  if (o) {
    console.log("BytedeskSvelte: 使用现有全局实例，当前活跃组件数:", e);
    return;
  }
  console.log("BytedeskSvelte: 创建新的全局实例"), o = new m(s), o.init();
}), i(() => {
  e--, console.log("BytedeskSvelte: 组件卸载，当前活跃组件数:", e), e <= 0 && (console.log("BytedeskSvelte: 没有活跃组件，清理全局实例"), setTimeout(() => {
    o && e <= 0 && (o.destroy(), o = null, e = 0);
  }, 100));
}), {
  destroy() {
  }
});
export {
  u as BytedeskSvelte
};
