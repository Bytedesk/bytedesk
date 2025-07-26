import { defineComponent as c, onMounted as u, onUnmounted as a, h as r } from "vue";
import { createI18n as d } from "vue-i18n";
import m from "../core/BytedeskWeb/index.js";
import { messages as y } from "../locales/index/index.js";
const f = d({
  locale: "zh-cn",
  messages: y
});
let e = null, o = 0;
const V = c({
  name: "BytedeskVue",
  props: {
    locale: {
      type: String,
      default: "zh-cn"
    }
  },
  emits: ["init"],
  setup(t, { attrs: s, emit: l }) {
    let n = null;
    return u(() => {
      o++, f.global.locale = t.locale;
      const i = {
        ...s,
        locale: t.locale
      };
      if (e) {
        console.log("BytedeskVue: 使用现有全局实例，当前活跃组件数:", o), n = e, l("init", n);
        return;
      }
      console.log("BytedeskVue: 创建新的全局实例"), e = new m(i), n = e, e.init(), l("init", n);
    }), a(() => {
      o--, console.log("BytedeskVue: 组件卸载，当前活跃组件数:", o), n = null, o <= 0 && (console.log("BytedeskVue: 没有活跃组件，清理全局实例"), setTimeout(() => {
        e && o <= 0 && (e.destroy(), e = null, o = 0);
      }, 100));
    }), () => r("div", { style: { display: "none" } });
  }
});
export {
  V as BytedeskVue
};
