import { LOG_ENABLE as e } from "../constants/index.js";
//#region src/utils/logger.ts
var t = null, n = (e) => {
	t = e;
}, r = () => {
	let n = localStorage.getItem(e);
	return n === null ? t?.isDebug ?? !1 : n === "true";
}, i = (() => {
	let e = t?.isDebug ?? !1;
	return e && process.env.NODE_ENV === "production" ? 1 : +!e;
})(), a = /* @__PURE__ */ new Map(), o = 1e3, s = (e) => {
	let t = Date.now(), n = a.get(e);
	return !n || t - n > o ? (a.set(e, t), !0) : !1;
}, c = () => {
	let e = /* @__PURE__ */ new Date();
	return `${e.getFullYear()}-${String(e.getMonth() + 1).padStart(2, "0")}-${String(e.getDate()).padStart(2, "0")} ${String(e.getHours()).padStart(2, "0")}:${String(e.getMinutes()).padStart(2, "0")}:${String(e.getSeconds()).padStart(2, "0")}`;
}, l = {
	debug: (e, ...t) => {
		if (r() && i <= 0) {
			if (!s(e)) return;
			let n = c();
			t.length ? console.debug(`${n} [DEBUG]: ${e}`, ...t) : console.debug(`${n} [DEBUG]: ${e}`);
		}
	},
	info: (e, ...t) => {
		if (r() && i <= 1) {
			let n = c();
			t.length ? console.info(`${n} [INFO]: ${e}`, ...t) : console.info(`${n} [INFO]: ${e}`);
		}
	},
	warn: (e, ...t) => {
		if (r() && i <= 2) {
			let n = c();
			t.length ? console.warn(`${n} [WARN]: ${e}`, ...t) : console.warn(`${n} [WARN]: ${e}`);
		}
	},
	error: (e, ...t) => {
		let n = c();
		t.length ? console.error(`${n} [ERROR]: ${e}`, ...t) : console.error(`${n} [ERROR]: ${e}`);
	},
	debugIf: (e, ...t) => {
		r() && l.debug(e, ...t);
	},
	log: (e, ...t) => {
		if (r()) {
			let n = c();
			t.length ? console.log(`${n} [LOG]: ${e}`, ...t) : console.log(`${n} [LOG]: ${e}`);
		}
	},
	group: (e) => {
		r() && console.group(`[ByteDesk] ${e}`);
	},
	groupEnd: () => {
		r() && console.groupEnd();
	},
	table: (e) => {
		r() && console.table(e);
	},
	time: (e) => {
		r() && console.time(`[ByteDesk] ${e}`);
	},
	timeEnd: (e) => {
		r() && console.timeEnd(`[ByteDesk] ${e}`);
	}
};
//#endregion
export { l as default, n as setGlobalConfig };
