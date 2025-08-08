var N = Object.create;
var h = Object.defineProperty;
var P = Object.getOwnPropertyDescriptor;
var E = (e, t) => (t = Symbol[e]) ? t : Symbol.for("Symbol." + e), p = (e) => {
  throw TypeError(e);
};
var F = (e, t, n) => t in e ? h(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var j = (e, t) => h(e, "name", { value: t, configurable: !0 });
var G = (e) => [, , , N((e == null ? void 0 : e[E("metadata")]) ?? null)], H = ["class", "method", "getter", "setter", "accessor", "field", "value", "get", "set"], m = (e) => e !== void 0 && typeof e != "function" ? p("Function expected") : e, Q = (e, t, n, r, s) => ({ kind: H[e], name: t, metadata: r, addInitializer: (l) => n._ ? p("Already initialized") : s.push(m(l || null)) }), O = (e, t) => F(t, E("metadata"), e[3]), v = (e, t, n, r) => {
  for (var s = 0, l = e[t >> 1], g = l && l.length; s < g; s++) t & 1 ? l[s].call(n) : r = l[s].call(n, r);
  return r;
}, J = (e, t, n, r, s, l) => {
  var g, d, D, k, b, o = t & 7, B = !!(t & 8), a = !!(t & 16), A = o > 3 ? e.length + 1 : o ? B ? 1 : 2 : 0, T = H[o + 5], W = o > 3 && (e[A - 1] = []), M = e[A] || (e[A] = []), y = o && (!a && !B && (s = s.prototype), o < 5 && (o > 3 || !a) && P(o < 4 ? s : { get [n]() {
    return q(this, l);
  }, set [n](i) {
    return z(this, l, i);
  } }, n));
  o ? a && o < 4 && j(l, (o > 2 ? "set " : o > 1 ? "get " : "") + n) : j(s, n);
  for (var C = r.length - 1; C >= 0; C--)
    k = Q(o, n, D = {}, e[3], M), o && (k.static = B, k.private = a, b = k.access = { has: a ? (i) => R(s, i) : (i) => n in i }, o ^ 3 && (b.get = a ? (i) => (o ^ 1 ? q : S)(i, s, o ^ 4 ? l : y.get) : (i) => i[n]), o > 2 && (b.set = a ? (i, I) => z(i, s, I, o ^ 4 ? l : y.set) : (i, I) => i[n] = I)), d = (0, r[C])(o ? o < 4 ? a ? l : y[T] : o > 4 ? void 0 : { get: y.get, set: y.set } : s, k), D._ = 1, o ^ 4 || d === void 0 ? m(d) && (o > 4 ? W.unshift(d) : o ? a ? l = d : y[T] = d : s = d) : typeof d != "object" || d === null ? p("Object expected") : (m(g = d.get) && (y.get = g), m(g = d.set) && (y.set = g), m(g = d.init) && W.unshift(g));
  return o || O(e, s), y && h(s, n, y), a ? o ^ 4 ? l : y : s;
}, K = (e, t, n) => F(e, typeof t != "symbol" ? t + "" : t, n), x = (e, t, n) => t.has(e) || p("Cannot " + n), R = (e, t) => Object(t) !== t ? p('Cannot use the "in" operator on this value') : e.has(t), q = (e, t, n) => (x(e, t, "read from private field"), n ? n.call(e) : t.get(e));
var z = (e, t, n, r) => (x(e, t, "write to private field"), r ? r.call(e, n) : t.set(e, n), n), S = (e, t, n) => (x(e, t, "access private method"), n);
import { Component as U, Input as V } from "@angular/core";
import X from "../core/BytedeskWeb/index.js";
let u = null, c = 0;
var L, f, w;
const $ = U({
  selector: "bytedesk-angular",
  template: "",
  styles: [`
    :host {
      display: none;
    }
  `]
})((L = [V()], w = class {
  constructor() {
    K(this, "config", v(f, 8, this)), v(f, 11, this);
  }
  ngOnInit() {
    if (c++, u) {
      window.bytedesk = u;
      return;
    }
    u = new X(this.config), u.init(), window.bytedesk = u;
  }
  ngOnDestroy() {
    c--, console.log("BytedeskAngular: 组件卸载，当前活跃组件数:", c), c <= 0 && (console.log("BytedeskAngular: 没有活跃组件，清理全局实例"), setTimeout(() => {
      u && c <= 0 && (u.destroy(), u = null, delete window.bytedesk, c = 0);
    }, 100));
  }
}, f = G(null), J(f, 5, "config", L, w), O(f, w), w));
export {
  $ as BytedeskAngular
};
