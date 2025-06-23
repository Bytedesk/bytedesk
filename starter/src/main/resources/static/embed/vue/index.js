import { defineComponent as l, onMounted as i, onUnmounted as r, h as m } from "vue";
import { createI18n as s } from "vue-i18n";
import a from "../core/BytedeskWeb/index.js";
import { messages as c } from "../locales/index/index.js";
const d = s({
  locale: "zh-cn",
  messages: c
}), g = l({
  name: "BytedeskVue",
  props: {
    locale: {
      type: String,
      default: "zh-cn"
    }
  },
  emits: ["init"],
  setup(o, { attrs: t, emit: n }) {
    let e = null;
    return i(() => {
      d.global.locale = o.locale, e = new a({
        ...t,
        locale: o.locale
      }), e.init(), n("init", e);
    }), r(() => {
      e == null || e.destroy();
    }), () => m("div", { style: { display: "none" } });
  }
});
export {
  g as BytedeskVue
};
