//#region src/utils/bizMessageCallbackDebug.ts
var e = "BIZ_MESSAGE_CALLBACK_DEBUG", t = ["bizMessageCallbackDebug", "bizCallbackDebug"], n = "[biz-callback]", r = (e) => {
	if (!e) return !1;
	let t = e.trim().toLowerCase();
	return t === "1" || t === "true" || t === "yes" || t === "on";
}, i = () => {
	if (typeof window > "u") return null;
	try {
		let e = new URLSearchParams(window.location.search);
		for (let n of t) {
			let t = e.get(n);
			if (t !== null) return t;
		}
	} catch {
		return null;
	}
	return null;
}, a = () => {
	let t = i();
	if (t !== null) return r(t);
	if (typeof window > "u") return !1;
	try {
		return r(window.localStorage?.getItem(e));
	} catch {
		return !1;
	}
}, o = (e, t) => {
	a() && console.log(`${n} ${e}`, t || {});
};
//#endregion
export { o as logBizMessageCallbackDebug };
