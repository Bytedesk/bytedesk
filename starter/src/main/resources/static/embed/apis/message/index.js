import { HTTP_CLIENT as e } from "../../utils/constants/index.js";
import t from "../request/index.js";
//#region src/apis/message.ts
async function n(n) {
	return t("/visitor/api/v1/message/unread/count", {
		method: "GET",
		params: {
			...n,
			client: e
		}
	});
}
async function r(n) {
	return t("/visitor/api/v1/message/unread/clear", {
		method: "POST",
		data: {
			...n,
			client: e
		}
	});
}
//#endregion
export { r as clearUnreadMessages, n as getUnreadMessageCount };
