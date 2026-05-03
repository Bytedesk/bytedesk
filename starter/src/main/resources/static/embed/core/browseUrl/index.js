//#region src/core/browseUrl.ts
var e = (e) => typeof e == "string" && e.trim() || void 0, t = (t) => {
	if (!t) return {};
	let n = {}, r = e(t.referer) || e(t.referrer), i = e(t.title), a = e(t.url);
	return r && (n.referer = r), i && (n.title = i), a && (n.url = a), n;
}, n = (e) => {
	let n = t(e);
	return Object.keys(n).length > 0 ? JSON.stringify(n) : void 0;
};
//#endregion
export { n as serializeBrowseConfig };
