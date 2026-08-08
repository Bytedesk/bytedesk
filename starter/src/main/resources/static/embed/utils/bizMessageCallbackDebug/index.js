//#region src/utils/bizMessageCallbackDebug.ts
var e = ["bizMessageCallbackDebug", "bizCallbackDebug"], t = "[biz-callback]", n = (e) => {
	if (!e) return !1;
	let t = e.trim().toLowerCase();
	return t === "1" || t === "true" || t === "yes" || t === "on";
}, r = () => {
	if (typeof window > "u") return null;
	try {
		let t = new URLSearchParams(window.location.search);
		for (let n of e) {
			let e = t.get(n);
			if (e !== null) return e;
		}
	} catch {
		return null;
	}
	return null;
}, i = () => {
	let e = r();
	if (e !== null) return n(e);
	if (typeof window > "u") return !1;
	try {
		return n(window.localStorage?.getItem("BIZ_MESSAGE_CALLBACK_DEBUG"));
	} catch {
		return !1;
	}
}, a = (e, n) => {
	i() && console.log(`${t} ${e}`, n || {});
};
//#endregion
export { i as isBizMessageCallbackDebugEnabled, a as logBizMessageCallbackDebug };
