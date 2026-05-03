import e from "../request/index.js";
//#region src/apis/feedback.ts
function t(t) {
	return e({
		url: "/visitor/api/feedback/submit",
		method: "post",
		data: t
	});
}
//#endregion
export { t as submitFeedback };
