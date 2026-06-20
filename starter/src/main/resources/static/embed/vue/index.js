import { messages as e } from "../locales/index/index.js";
import t from "../index/index.js";
import { defineComponent as n, h as r, onMounted as i, onUnmounted as a } from "vue";
import { createI18n as o } from "vue-i18n";
//#region src/adapters/vue.ts
var s = o({
	locale: "zh-cn",
	messages: e
}), c = null, l = 0, u = n({
	name: "BytedeskVue",
	props: { locale: {
		type: String,
		default: "zh-cn"
	} },
	emits: ["init"],
	setup(e, { attrs: n, emit: o }) {
		let u = null;
		return i(() => {
			l++, s.global.locale = e.locale;
			let r = {
				...n,
				locale: e.locale
			};
			if (c) {
				u = c, o("init", u);
				return;
			}
			c = new t(r), u = c, c.init(), o("init", u);
		}), a(() => {
			l--, u = null, l <= 0 && setTimeout(() => {
				c && l <= 0 && (c.destroy(), c = null, l = 0);
			}, 100);
		}), () => r("div", { style: { display: "none" } });
	}
});
//#endregion
export { u as BytedeskVue };
