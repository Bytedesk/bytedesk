const e = (r) => {
  if (typeof r != "string")
    return;
  const t = r.trim();
  return t || void 0;
}, s = (r) => {
  if (!r)
    return {};
  const t = {}, i = e(r.referer) || e(r.referrer), n = e(r.title), o = e(r.url);
  return i && (t.referer = i), n && (t.title = n), o && (t.url = o), t;
}, c = (r) => {
  const t = s(r);
  return Object.keys(t).length > 0 ? JSON.stringify(t) : void 0;
};
export {
  s as normalizeBrowseConfig,
  c as serializeBrowseConfig
};
