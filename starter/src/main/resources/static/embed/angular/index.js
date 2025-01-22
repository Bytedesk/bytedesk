var L = Object.create;
var B = Object.defineProperty;
var M = Object.getOwnPropertyDescriptor;
var z = (e, t) => (t = Symbol[e]) ? t : Symbol.for("Symbol." + e), p = (e) => {
  throw TypeError(e);
};
var E = (e, t, s) => t in e ? B(e, t, { enumerable: !0, configurable: !0, writable: !0, value: s }) : e[t] = s;
var j = (e, t) => B(e, "name", { value: t, configurable: !0 });
var F = (e) => [, , , L((e == null ? void 0 : e[z("metadata")]) ?? null)], G = ["class", "method", "getter", "setter", "accessor", "field", "value", "get", "set"], h = (e) => e !== void 0 && typeof e != "function" ? p("Function expected") : e, N = (e, t, s, f, i) => ({ kind: G[e], name: t, metadata: f, addInitializer: (d) => s._ ? p("Already initialized") : i.push(h(d || null)) }), I = (e, t) => E(t, z("metadata"), e[3]), O = (e, t, s, f) => {
  for (var i = 0, d = e[t >> 1], b = d && d.length; i < b; i++) t & 1 ? d[i].call(s) : f = d[i].call(s, f);
  return f;
}, H = (e, t, s, f, i, d) => {
  var b, y, C, r, g, n = t & 7, m = !!(t & 8), l = !!(t & 16), c = n > 3 ? e.length + 1 : n ? m ? 1 : 2 : 0, D = G[n + 5], W = n > 3 && (e[c - 1] = []), K = e[c] || (e[c] = []), k = n && (!l && !m && (i = i.prototype), n < 5 && (n > 3 || !l) && M(n < 4 ? i : { get [s]() {
    return q(this, d);
  }, set [s](o) {
    return v(this, d, o);
  } }, s));
  n ? l && n < 4 && j(d, (n > 2 ? "set " : n > 1 ? "get " : "") + s) : j(i, s);
  for (var u = f.length - 1; u >= 0; u--)
    r = N(n, s, C = {}, e[3], K), n && (r.static = m, r.private = l, g = r.access = { has: l ? (o) => P(i, o) : (o) => s in o }, n ^ 3 && (g.get = l ? (o) => (n ^ 1 ? q : Q)(o, i, n ^ 4 ? d : k.get) : (o) => o[s]), n > 2 && (g.set = l ? (o, w) => v(o, i, w, n ^ 4 ? d : k.set) : (o, w) => o[s] = w)), y = (0, f[u])(n ? n < 4 ? l ? d : k[D] : n > 4 ? void 0 : { get: k.get, set: k.set } : i, r), C._ = 1, n ^ 4 || y === void 0 ? h(y) && (n > 4 ? W.unshift(y) : n ? l ? d = y : k[D] = y : i = y) : typeof y != "object" || y === null ? p("Object expected") : (h(b = y.get) && (k.get = b), h(b = y.set) && (k.set = b), h(b = y.init) && W.unshift(b));
  return n || I(e, i), k && B(i, s, k), l ? n ^ 4 ? d : k : i;
}, x = (e, t, s) => E(e, typeof t != "symbol" ? t + "" : t, s), A = (e, t, s) => t.has(e) || p("Cannot " + s), P = (e, t) => Object(t) !== t ? p('Cannot use the "in" operator on this value') : e.has(t), q = (e, t, s) => (A(e, t, "read from private field"), s ? s.call(e) : t.get(e));
var v = (e, t, s, f) => (A(e, t, "write to private field"), f ? f.call(e, s) : t.set(e, s), s), Q = (e, t, s) => (A(e, t, "access private method"), s);
import { Component as S, Input as T } from "@angular/core";
import U from "../core/BytedeskWeb/index.js";
var J, R, a;
const Z = S({
  selector: "bytedesk-angular",
  template: "",
  styles: [`
    :host {
      display: none;
    }
  `]
})((J = [T()], a = class {
  constructor() {
    x(this, "config", O(R, 8, this)), O(R, 11, this);
    x(this, "bytedeskRef", null);
  }
  ngOnInit() {
    this.bytedeskRef = new U(this.config), this.bytedeskRef.init(), window.bytedesk = this.bytedeskRef;
  }
  ngOnDestroy() {
    this.bytedeskRef && (this.bytedeskRef.destroy(), delete window.bytedesk, this.bytedeskRef = null);
  }
}, R = F(null), H(R, 5, "config", J, a), I(R, a), a));
export {
  Z as BytedeskAngular
};
