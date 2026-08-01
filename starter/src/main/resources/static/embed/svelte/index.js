import { messages as e } from "../locales/index/index.js";
import t from "../utils/logger/index.js";
import n from "../index/index.js";
import { onDestroy as r, onMount as i } from "svelte";
import { addMessages as a, getLocaleFromNavigator as o, init as s } from "svelte-i18n";
Object.entries(e).forEach(([e, t]) => {
	a(e, t);
}), s({
	fallbackLocale: "en",
	initialLocale: o()
});
var c = null, l = 0, u = (e, a) => (t.debug("config", a, e), i(() => {
	l++;
	let e = {
		...a,
		locale: a.locale || o() || "zh-cn"
	};
	c || (c = new n(e), c.init());
}), r(() => {
	l--, l <= 0 && setTimeout(() => {
		c && l <= 0 && (c.destroy(), c = null, l = 0);
	}, 100);
}), { destroy() {} });
//#endregion
export { u as BytedeskSvelte };
