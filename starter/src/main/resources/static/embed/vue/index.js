import { defineComponent as l, onMounted as n, onUnmounted as r, h as m } from "vue";
import { createI18n as a } from "vue-i18n";
import s from "../core/BytedeskWeb/index.js";
import { messages as d } from "../locales/index/index.js";
const i = a({
  locale: "zh-CN",
  messages: d
}), f = l({
  name: "BytedeskVue",
  props: {
    locale: {
      type: String,
      default: "zh-CN"
    }
  },
  setup(o, { attrs: t }) {
    let e = null;
    return n(() => {
      i.global.locale = o.locale, e = new s({
        ...t,
        locale: o.locale
      }), e.init();
    }), r(() => {
      e == null || e.destroy();
    }), () => m("div", { style: { display: "none" } });
  }
});
export {
  f as BytedeskVue
};
