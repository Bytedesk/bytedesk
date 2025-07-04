import { HTTP_CLIENT as t } from "../../utils/constants/index.js";
import r from "../request/index.js";
async function e(i) {
  return r("/visitor/api/v1/init", {
    method: "POST",
    data: {
      ...i,
      client: t
    }
  });
}
export {
  e as initVisitor
};
