import e from "../request/index.js";
function i(t) {
  return e({
    url: "/visitor/api/feedback/submit",
    method: "post",
    data: t
  });
}
export {
  i as submitFeedback
};
