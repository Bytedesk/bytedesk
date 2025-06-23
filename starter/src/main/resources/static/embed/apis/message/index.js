import { HTTP_CLIENT as r } from "../../utils/constants/index.js";
import a from "../request/index.js";
async function s(e) {
  return a("/visitor/api/v1/message/unread/count", {
    method: "GET",
    params: {
      ...e,
      client: r
    }
  });
}
async function o(e) {
  return a("/visitor/api/v1/message/unread/clear", {
    method: "POST",
    data: {
      ...e,
      client: r
    }
  });
}
export {
  o as clearMessageUnread,
  s as getMessageUnreadCount
};
