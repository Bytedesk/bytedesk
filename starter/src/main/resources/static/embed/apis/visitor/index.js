import { HTTP_CLIENT as i } from "../../utils/constants/index.js";
import o from "../request/index.js";
async function c(n) {
  const t = n.channel || i;
  return o("/visitor/api/v1/init", {
    method: "POST",
    data: {
      ...n,
      channel: t,
      client: t
    }
  });
}
async function a(n) {
  const t = n.channel || i;
  return o("/visitor/api/v1/browse", {
    method: "POST",
    data: {
      ...n,
      channel: t,
      client: t
    }
  });
}
export {
  a as browse,
  c as initVisitor
};
