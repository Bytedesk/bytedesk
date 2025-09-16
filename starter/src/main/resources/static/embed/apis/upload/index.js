import { getApiUrl as b } from "../request/index.js";
async function n(a, l, d, e) {
  try {
    const r = (/* @__PURE__ */ new Date()).toISOString().replace(/[-:T.]/g, "").slice(0, 14), s = l || `${r}_${a.name}`, m = d || a.type || "image/jpeg", t = new FormData();
    t.append("file", a), t.append("fileName", s), t.append("fileType", m), t.append("isAvatar", (e == null ? void 0 : e.isAvatar) || "false"), t.append("kbType", (e == null ? void 0 : e.kbType) || "feedback");
    const i = (e == null ? void 0 : e.visitorUid) || localStorage.getItem("bytedesk_uid") || localStorage.getItem("bytedesk_visitor_uid") || "", u = (e == null ? void 0 : e.visitorNickname) || localStorage.getItem("bytedesk_nickname") || "", v = (e == null ? void 0 : e.visitorAvatar) || localStorage.getItem("bytedesk_avatar") || "", k = (e == null ? void 0 : e.orgUid) || "";
    t.append("visitorUid", i), t.append("visitorNickname", u), t.append("visitorAvatar", v), t.append("orgUid", k), t.append("client", (e == null ? void 0 : e.client) || "web"), e != null && e.isDebug && console.log("handleUpload formData", t);
    const y = `${b()}/visitor/api/upload/file`, p = await fetch(y, {
      method: "POST",
      headers: {
        // Authorization: "Bearer ", // + localStorage.getItem(ACCESS_TOKEN),
      },
      body: t
    });
    if (!p.ok)
      throw new Error(`上传失败: ${p.status} ${p.statusText}`);
    const o = await p.json();
    return e != null && e.isDebug && console.log("upload data:", o), o;
  } catch (r) {
    throw console.error("文件上传失败:", r), r;
  }
}
async function w(a, l) {
  var s;
  const e = `screenshot_${(/* @__PURE__ */ new Date()).toISOString().replace(/[-:T.]/g, "").slice(0, 14)}.jpg`;
  return ((s = (await n(a, e, "image/jpeg", {
    ...l,
    kbType: "feedback"
  })).data) == null ? void 0 : s.fileUrl) || "";
}
export {
  n as handleUpload,
  w as uploadScreenshot
};
