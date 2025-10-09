var P = Object.create;
var h = Object.defineProperty;
var Q = Object.getOwnPropertyDescriptor;
var E = (e, t) => (t = Symbol[e]) ? t : Symbol.for("Symbol." + e), b = (e) => {
  throw TypeError(e);
};
var F = (e, t, n) => t in e ? h(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var j = (e, t) => h(e, "name", { value: t, configurable: !0 });
var G = (e) => [, , , P((e == null ? void 0 : e[E("metadata")]) ?? null)], H = ["class", "method", "getter", "setter", "accessor", "field", "value", "get", "set"], p = (e) => e !== void 0 && typeof e != "function" ? b("Function expected") : e, R = (e, t, n, r, s) => ({ kind: H[e], name: t, metadata: r, addInitializer: (l) => n._ ? b("Already initialized") : s.push(p(l || null)) }), O = (e, t) => F(t, E("metadata"), e[3]), v = (e, t, n, r) => {
  for (var s = 0, l = e[t >> 1], u = l && l.length; s < u; s++) t & 1 ? l[s].call(n) : r = l[s].call(n, r);
  return r;
}, J = (e, t, n, r, s, l) => {
  var u, d, D, k, w, o = t & 7, B = !!(t & 8), y = !!(t & 16), A = o > 3 ? e.length + 1 : o ? B ? 1 : 2 : 0, T = H[o + 5], W = o > 3 && (e[A - 1] = []), N = e[A] || (e[A] = []), g = o && (!y && !B && (s = s.prototype), o < 5 && (o > 3 || !y) && Q(o < 4 ? s : { get [n]() {
    return q(this, l);
  }, set [n](i) {
    return z(this, l, i);
  } }, n));
  o ? y && o < 4 && j(l, (o > 2 ? "set " : o > 1 ? "get " : "") + n) : j(s, n);
  for (var C = r.length - 1; C >= 0; C--)
    k = R(o, n, D = {}, e[3], N), o && (k.static = B, k.private = y, w = k.access = { has: y ? (i) => S(s, i) : (i) => n in i }, o ^ 3 && (w.get = y ? (i) => (o ^ 1 ? q : U)(i, s, o ^ 4 ? l : g.get) : (i) => i[n]), o > 2 && (w.set = y ? (i, I) => z(i, s, I, o ^ 4 ? l : g.set) : (i, I) => i[n] = I)), d = (0, r[C])(o ? o < 4 ? y ? l : g[T] : o > 4 ? void 0 : { get: g.get, set: g.set } : s, k), D._ = 1, o ^ 4 || d === void 0 ? p(d) && (o > 4 ? W.unshift(d) : o ? y ? l = d : g[T] = d : s = d) : typeof d != "object" || d === null ? b("Object expected") : (p(u = d.get) && (g.get = u), p(u = d.set) && (g.set = u), p(u = d.init) && W.unshift(u));
  return o || O(e, s), g && h(s, n, g), y ? o ^ 4 ? l : g : s;
}, K = (e, t, n) => F(e, typeof t != "symbol" ? t + "" : t, n), x = (e, t, n) => t.has(e) || b("Cannot " + n), S = (e, t) => Object(t) !== t ? b('Cannot use the "in" operator on this value') : e.has(t), q = (e, t, n) => (x(e, t, "read from private field"), n ? n.call(e) : t.get(e));
var z = (e, t, n, r) => (x(e, t, "write to private field"), r ? r.call(e, n) : t.set(e, n), n), U = (e, t, n) => (x(e, t, "access private method"), n);
import { Component as V, Input as X } from "@angular/core";
import Y from "../core/BytedeskWeb/index.js";
import L from "../utils/logger/index.js";
let a = null, m = 0;
var M, f, c;
const te = V({
  selector: "bytedesk-angular",
  template: "",
  styles: [`
    :host {
      display: none;
    }
  `]
})((M = [X()], c = class {
  constructor() {
    K(this, "config", v(f, 8, this)), v(f, 11, this);
  }
  ngOnInit() {
    if (m++, a) {
      window.bytedesk = a;
      return;
    }
    a = new Y(this.config), a.init(), window.bytedesk = a;
  }
  ngOnDestroy() {
    m--, L.debug("BytedeskAngular: 组件卸载，当前活跃组件数:", m), m <= 0 && (L.debug("BytedeskAngular: 没有活跃组件，清理全局实例"), setTimeout(() => {
      a && m <= 0 && (a.destroy(), a = null, delete window.bytedesk, m = 0);
    }, 100));
  }
}, f = G(null), J(f, 5, "config", M, c), O(f, c), c));
export {
  te as BytedeskAngular
};
