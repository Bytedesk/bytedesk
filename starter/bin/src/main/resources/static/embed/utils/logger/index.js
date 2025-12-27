import { LOG_ENABLE as f } from "../constants/index.js";
let s = null;
const L = (t) => {
  s = t;
}, n = () => {
  const t = localStorage.getItem(f);
  return t === null ? (s == null ? void 0 : s.isDebug) ?? !1 : t === "true";
}, a = () => {
  const t = (s == null ? void 0 : s.isDebug) ?? !1;
  return t && process.env.NODE_ENV === "production" ? 1 : t ? 0 : 1;
}, c = a(), l = /* @__PURE__ */ new Map(), d = 1e3, p = (t) => {
  const e = Date.now(), o = l.get(t);
  return !o || e - o > d ? (l.set(t, e), !0) : !1;
}, r = () => {
  const t = /* @__PURE__ */ new Date(), e = t.getFullYear(), o = String(t.getMonth() + 1).padStart(2, "0"), i = String(t.getDate()).padStart(2, "0"), u = String(t.getHours()).padStart(2, "0"), g = String(t.getMinutes()).padStart(2, "0"), $ = String(t.getSeconds()).padStart(2, "0");
  return `${e}-${o}-${i} ${u}:${g}:${$}`;
}, E = {
  debug: (t, ...e) => {
    if (n() && c <= 0) {
      if (!p(t))
        return;
      const o = r();
      e.length ? console.debug(`${o} [DEBUG]: ${t}`, ...e) : console.debug(`${o} [DEBUG]: ${t}`);
    }
  },
  info: (t, ...e) => {
    if (n() && c <= 1) {
      const o = r();
      e.length ? console.info(`${o} [INFO]: ${t}`, ...e) : console.info(`${o} [INFO]: ${t}`);
    }
  },
  warn: (t, ...e) => {
    if (n() && c <= 2) {
      const o = r();
      e.length ? console.warn(`${o} [WARN]: ${t}`, ...e) : console.warn(`${o} [WARN]: ${t}`);
    }
  },
  error: (t, ...e) => {
    const o = r();
    e.length ? console.error(`${o} [ERROR]: ${t}`, ...e) : console.error(`${o} [ERROR]: ${t}`);
  },
  // 输出日志的快捷方法
  debugIf: (t, ...e) => {
    n() && E.debug(t, ...e);
  },
  // 新增的日志方法
  log: (t, ...e) => {
    if (n()) {
      const o = r();
      e.length ? console.log(`${o} [LOG]: ${t}`, ...e) : console.log(`${o} [LOG]: ${t}`);
    }
  },
  // 分组日志
  group: (t) => {
    n() && console.group(`[ByteDesk] ${t}`);
  },
  groupEnd: () => {
    n() && console.groupEnd();
  },
  // 表格日志
  table: (t) => {
    n() && console.table(t);
  },
  // 时间日志
  time: (t) => {
    n() && console.time(`[ByteDesk] ${t}`);
  },
  timeEnd: (t) => {
    n() && console.timeEnd(`[ByteDesk] ${t}`);
  }
};
export {
  E as default,
  L as setGlobalConfig
};
