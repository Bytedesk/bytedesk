import { LOG_ENABLE as e } from "../constants/index.js";
//#region src/utils/logger.ts
var t = /* @__PURE__ */ function(e) {
	return e[e.DEBUG = 0] = "DEBUG", e[e.INFO = 1] = "INFO", e[e.WARN = 2] = "WARN", e[e.ERROR = 3] = "ERROR", e;
}(t || {}), n = null, r = (e) => {
	n = e;
}, i = () => {
	let t = localStorage.getItem(e);
	return t === null ? n?.isDebug ?? !1 : t === "true";
}, a = (() => {
	let e = n?.isDebug ?? !1;
	return e && process.env.NODE_ENV === "production" ? t.INFO : e ? t.DEBUG : t.INFO;
})(), o = /* @__PURE__ */ new Map(), s = 1e3, c = (e) => {
	let t = Date.now(), n = o.get(e);
	return !n || t - n > s ? (o.set(e, t), !0) : !1;
}, l = () => {
	let e = /* @__PURE__ */ new Date();
	return `${e.getFullYear()}-${String(e.getMonth() + 1).padStart(2, "0")}-${String(e.getDate()).padStart(2, "0")} ${String(e.getHours()).padStart(2, "0")}:${String(e.getMinutes()).padStart(2, "0")}:${String(e.getSeconds()).padStart(2, "0")}`;
}, u = {
	debug: (e, ...n) => {
		if (i() && a <= t.DEBUG) {
			if (!c(e)) return;
			let t = l();
			n.length ? console.debug(`${t} [DEBUG]: ${e}`, ...n) : console.debug(`${t} [DEBUG]: ${e}`);
		}
	},
	info: (e, ...n) => {
		if (i() && a <= t.INFO) {
			let t = l();
			n.length ? console.info(`${t} [INFO]: ${e}`, ...n) : console.info(`${t} [INFO]: ${e}`);
		}
	},
	warn: (e, ...n) => {
		if (i() && a <= t.WARN) {
			let t = l();
			n.length ? console.warn(`${t} [WARN]: ${e}`, ...n) : console.warn(`${t} [WARN]: ${e}`);
		}
	},
	error: (e, ...t) => {
		let n = l();
		t.length ? console.error(`${n} [ERROR]: ${e}`, ...t) : console.error(`${n} [ERROR]: ${e}`);
	},
	debugIf: (e, ...t) => {
		i() && u.debug(e, ...t);
	},
	log: (e, ...t) => {
		if (i()) {
			let n = l();
			t.length ? console.log(`${n} [LOG]: ${e}`, ...t) : console.log(`${n} [LOG]: ${e}`);
		}
	},
	group: (e) => {
		i() && console.group(`[ByteDesk] ${e}`);
	},
	groupEnd: () => {
		i() && console.groupEnd();
	},
	table: (e) => {
		i() && console.table(e);
	},
	time: (e) => {
		i() && console.time(`[ByteDesk] ${e}`);
	},
	timeEnd: (e) => {
		i() && console.timeEnd(`[ByteDesk] ${e}`);
	}
};
//#endregion
export { u as default, r as setGlobalConfig };
