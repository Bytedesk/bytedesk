var N = Object.create;
var h = Object.defineProperty;
var P = Object.getOwnPropertyDescriptor;
var E = (e, t) => (t = Symbol[e]) ? t : Symbol.for("Symbol." + e), p = (e) => {
  throw TypeError(e);
};
var F = (e, t, n) => t in e ? h(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var j = (e, t) => h(e, "name", { value: t, configurable: !0 });
var G = (e) => [, , , N((e == null ? void 0 : e[E("metadata")]) ?? null)], H = ["class", "method", "getter", "setter", "accessor", "field", "value", "get", "set"], m = (e) => e !== void 0 && typeof e != "function" ? p("Function expected") : e, Q = (e, t, n, r, l) => ({ kind: H[e], name: t, metadata: r, addInitializer: (s) => n._ ? p("Already initialized") : l.push(m(s || null)) }), O = (e, t) => F(t, E("metadata"), e[3]), v = (e, t, n, r) => {
  for (var l = 0, s = e[t >> 1], a = s && s.length; l < a; l++) t & 1 ? s[l].call(n) : r = s[l].call(n, r);
  return r;
}, J = (e, t, n, r, l, s) => {
  var a, i, D, k, B, o = t & 7, b = !!(t & 8), y = !!(t & 16), A = o > 3 ? e.length + 1 : o ? b ? 1 : 2 : 0, T = H[o + 5], W = o > 3 && (e[A - 1] = []), M = e[A] || (e[A] = []), g = o && (!y && !b && (l = l.prototype), o < 5 && (o > 3 || !y) && P(o < 4 ? l : { get [n]() {
    return q(this, s);
  }, set [n](d) {
    return z(this, s, d);
  } }, n));
  o ? y && o < 4 && j(s, (o > 2 ? "set " : o > 1 ? "get " : "") + n) : j(l, n);
  for (var C = r.length - 1; C >= 0; C--)
    k = Q(o, n, D = {}, e[3], M), o && (k.static = b, k.private = y, B = k.access = { has: y ? (d) => R(l, d) : (d) => n in d }, o ^ 3 && (B.get = y ? (d) => (o ^ 1 ? q : S)(d, l, o ^ 4 ? s : g.get) : (d) => d[n]), o > 2 && (B.set = y ? (d, I) => z(d, l, I, o ^ 4 ? s : g.set) : (d, I) => d[n] = I)), i = (0, r[C])(o ? o < 4 ? y ? s : g[T] : o > 4 ? void 0 : { get: g.get, set: g.set } : l, k), D._ = 1, o ^ 4 || i === void 0 ? m(i) && (o > 4 ? W.unshift(i) : o ? y ? s = i : g[T] = i : l = i) : typeof i != "object" || i === null ? p("Object expected") : (m(a = i.get) && (g.get = a), m(a = i.set) && (g.set = a), m(a = i.init) && W.unshift(a));
  return o || O(e, l), g && h(l, n, g), y ? o ^ 4 ? s : g : l;
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
      console.log("BytedeskAngular: 使用现有全局实例，当前活跃组件数:", c), window.bytedesk = u;
      return;
    }
    console.log("BytedeskAngular: 创建新的全局实例"), u = new X(this.config), u.init(), window.bytedesk = u;
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
