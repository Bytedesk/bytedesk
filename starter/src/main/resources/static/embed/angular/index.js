var M = Object.create;
var C = Object.defineProperty;
var N = Object.getOwnPropertyDescriptor;
var E = (e, t) => (t = Symbol[e]) ? t : Symbol.for("Symbol." + e), b = (e) => {
  throw TypeError(e);
};
var F = (e, t, n) => t in e ? C(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var j = (e, t) => C(e, "name", { value: t, configurable: !0 });
var G = (e) => [, , , M((e == null ? void 0 : e[E("metadata")]) ?? null)], H = ["class", "method", "getter", "setter", "accessor", "field", "value", "get", "set"], f = (e) => e !== void 0 && typeof e != "function" ? b("Function expected") : e, P = (e, t, n, y, s) => ({ kind: H[e], name: t, metadata: y, addInitializer: (l) => n._ ? b("Already initialized") : s.push(f(l || null)) }), I = (e, t) => F(t, E("metadata"), e[3]), O = (e, t, n, y) => {
  for (var s = 0, l = e[t >> 1], u = l && l.length; s < u; s++) t & 1 ? l[s].call(n) : y = l[s].call(n, y);
  return y;
}, J = (e, t, n, y, s, l) => {
  var u, d, D, c, w, o = t & 7, B = !!(t & 8), r = !!(t & 16), h = o > 3 ? e.length + 1 : o ? B ? 1 : 2 : 0, T = H[o + 5], W = o > 3 && (e[h - 1] = []), L = e[h] || (e[h] = []), g = o && (!r && !B && (s = s.prototype), o < 5 && (o > 3 || !r) && N(o < 4 ? s : { get [n]() {
    return q(this, l);
  }, set [n](i) {
    return z(this, l, i);
  } }, n));
  o ? r && o < 4 && j(l, (o > 2 ? "set " : o > 1 ? "get " : "") + n) : j(s, n);
  for (var A = y.length - 1; A >= 0; A--)
    c = P(o, n, D = {}, e[3], L), o && (c.static = B, c.private = r, w = c.access = { has: r ? (i) => Q(s, i) : (i) => n in i }, o ^ 3 && (w.get = r ? (i) => (o ^ 1 ? q : S)(i, s, o ^ 4 ? l : g.get) : (i) => i[n]), o > 2 && (w.set = r ? (i, R) => z(i, s, R, o ^ 4 ? l : g.set) : (i, R) => i[n] = R)), d = (0, y[A])(o ? o < 4 ? r ? l : g[T] : o > 4 ? void 0 : { get: g.get, set: g.set } : s, c), D._ = 1, o ^ 4 || d === void 0 ? f(d) && (o > 4 ? W.unshift(d) : o ? r ? l = d : g[T] = d : s = d) : typeof d != "object" || d === null ? b("Object expected") : (f(u = d.get) && (g.get = u), f(u = d.set) && (g.set = u), f(u = d.init) && W.unshift(u));
  return o || I(e, s), g && C(s, n, g), r ? o ^ 4 ? l : g : s;
}, v = (e, t, n) => F(e, typeof t != "symbol" ? t + "" : t, n), x = (e, t, n) => t.has(e) || b("Cannot " + n), Q = (e, t) => Object(t) !== t ? b('Cannot use the "in" operator on this value') : e.has(t), q = (e, t, n) => (x(e, t, "read from private field"), n ? n.call(e) : t.get(e));
var z = (e, t, n, y) => (x(e, t, "write to private field"), y ? y.call(e, n) : t.set(e, n), n), S = (e, t, n) => (x(e, t, "access private method"), n);
import { Component as U, Input as V } from "@angular/core";
import X from "../core/BytedeskWeb/index.js";
let k = null, a = 0;
var K, m, p;
const $ = U({
  selector: "bytedesk-angular",
  template: "",
  styles: [`
    :host {
      display: none;
    }
  `]
})((K = [V()], p = class {
  constructor() {
    v(this, "config", O(m, 8, this)), O(m, 11, this);
    v(this, "bytedeskRef", null);
  }
  ngOnInit() {
    if (a++, k) {
      console.log("BytedeskAngular: 使用现有全局实例，当前活跃组件数:", a), this.bytedeskRef = k, window.bytedesk = k;
      return;
    }
    console.log("BytedeskAngular: 创建新的全局实例"), k = new X(this.config), this.config, this.bytedeskRef = k, k.init(), window.bytedesk = k;
  }
  ngOnDestroy() {
    a--, console.log("BytedeskAngular: 组件卸载，当前活跃组件数:", a), this.bytedeskRef = null, a <= 0 && (console.log("BytedeskAngular: 没有活跃组件，清理全局实例"), setTimeout(() => {
      k && a <= 0 && (k.destroy(), k = null, delete window.bytedesk, a = 0);
    }, 100));
  }
}, m = G(null), J(m, 5, "config", K, p), I(m, p), p));
export {
  $ as BytedeskAngular
};
