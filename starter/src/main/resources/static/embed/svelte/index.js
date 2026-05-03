import e from "../utils/logger/index.js";
import t from "../index/index.js";
import { messages as n } from "../locales/index/index.js";
import { onDestroy as r, onMount as i } from "svelte";
import { addMessages as a, getLocaleFromNavigator as o, init as s } from "svelte-i18n";
Object.entries(n).forEach(([e, t]) => {
	a(e, t);
}), s({
	fallbackLocale: "en",
	initialLocale: o()
});
var c = null, l = 0, u = (n, a) => (e.debug("config", a, n), i(() => {
	l++;
	let e = {
		...a,
		locale: a.locale || o() || "zh-cn"
	};
	c || (c = new t(e), c.init());
}), r(() => {
	l--, l <= 0 && setTimeout(() => {
		c && l <= 0 && (c.destroy(), c = null, l = 0);
	}, 100);
}), { destroy() {} });
//#endregion
export { u as BytedeskSvelte };
