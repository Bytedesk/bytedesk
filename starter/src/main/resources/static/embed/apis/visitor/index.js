import { HTTP_CLIENT as i } from "../../utils/constants/index.js";
import r from "../request/index.js";
async function e(t) {
  return r("/visitor/api/v1/init", {
    method: "POST",
    data: {
      ...t,
      client: i
    }
  });
}
async function a(t) {
  return r("/visitor/api/v1/browse", {
    method: "POST",
    data: {
      ...t,
      client: i
    }
  });
}
export {
  a as browse,
  e as initVisitor
};
