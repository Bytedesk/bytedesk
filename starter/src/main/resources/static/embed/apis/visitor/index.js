import "../../utils/constants/index.js";
import e from "../request/index.js";
//#region src/apis/visitor.ts
async function t(t) {
	let n = t.channel || "WEB_FLOAT";
	return e("/visitor/api/v1/init", {
		method: "POST",
		data: {
			...t,
			channel: n,
			client: n
		}
	});
}
async function n(t) {
	let n = t.channel || "WEB_FLOAT";
	return e("/visitor/api/v1/browse", {
		method: "POST",
		data: {
			...t,
			channel: n,
			client: n
		}
	});
}
//#endregion
export { n as browse, t as initVisitor };
