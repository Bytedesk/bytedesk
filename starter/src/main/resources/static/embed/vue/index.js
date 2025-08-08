import { defineComponent as a, onMounted as c, onUnmounted as r, h as m } from "vue";
import { createI18n as u } from "vue-i18n";
import d from "../core/BytedeskWeb/index.js";
import { messages as f } from "../locales/index/index.js";
const p = u({
  locale: "zh-cn",
  messages: f
});
let e = null, t = 0;
const b = a({
  name: "BytedeskVue",
  props: {
    locale: {
      type: String,
      default: "zh-cn"
    }
  },
  emits: ["init"],
  setup(o, { attrs: i, emit: l }) {
    let n = null;
    return c(() => {
      t++, p.global.locale = o.locale;
      const s = {
        ...i,
        locale: o.locale
      };
      if (e) {
        n = e, l("init", n);
        return;
      }
      e = new d(s), n = e, e.init(), l("init", n);
    }), r(() => {
      t--, n = null, t <= 0 && setTimeout(() => {
        e && t <= 0 && (e.destroy(), e = null, t = 0);
      }, 100);
    }), () => m("div", { style: { display: "none" } });
  }
});
export {
  b as BytedeskVue
};
