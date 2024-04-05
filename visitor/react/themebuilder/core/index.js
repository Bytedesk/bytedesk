!(function (e, t) {
  "object" == typeof exports && "undefined" != typeof module
    ? t(exports, require("react"), require("react-dom"))
    : "function" == typeof define && define.amd
      ? define(["exports", "react", "react-dom"], t)
      : t(
          ((e =
            "undefined" != typeof globalThis ? globalThis : e || self).ChatUI =
            {}),
          e.React,
          e.ReactDOM,
        );
})(this, function (e, t, n) {
  "use strict";
  function r(e) {
    return e && "object" == typeof e && "default" in e ? e : { default: e };
  }
  e.version = "2.3.1";
  var o = r(t),
    a = r(n);
  function i(e) {
    var t,
      n,
      r = "";
    if ("string" == typeof e || "number" == typeof e) r += e;
    else if ("object" == typeof e)
      if (Array.isArray(e))
        for (t = 0; t < e.length; t++)
          e[t] && (n = i(e[t])) && (r && (r += " "), (r += n));
      else for (t in e) e[t] && (r && (r += " "), (r += t));
    return r;
  }
  function c() {
    for (var e, t, n = 0, r = ""; n < arguments.length; )
      (e = arguments[n++]) && (t = i(e)) && (r && (r += " "), (r += t));
    return r;
  }
  function u(e) {
    return (
      (u =
        "function" == typeof Symbol && "symbol" == typeof Symbol.iterator
          ? function (e) {
              return typeof e;
            }
          : function (e) {
              return e &&
                "function" == typeof Symbol &&
                e.constructor === Symbol &&
                e !== Symbol.prototype
                ? "symbol"
                : typeof e;
            }),
      u(e)
    );
  }
  function l(e, t) {
    return (
      (l =
        Object.setPrototypeOf ||
        function (e, t) {
          return (e.__proto__ = t), e;
        }),
      l(e, t)
    );
  }
  function s() {
    if ("undefined" == typeof Reflect || !Reflect.construct) return !1;
    if (Reflect.construct.sham) return !1;
    if ("function" == typeof Proxy) return !0;
    try {
      return (
        Boolean.prototype.valueOf.call(
          Reflect.construct(Boolean, [], function () {}),
        ),
        !0
      );
    } catch (e) {
      return !1;
    }
  }
  function f(e, t, n) {
    return (
      (f = s()
        ? Reflect.construct
        : function (e, t, n) {
            var r = [null];
            r.push.apply(r, t);
            var o = new (Function.bind.apply(e, r))();
            return n && l(o, n.prototype), o;
          }),
      f.apply(null, arguments)
    );
  }
  function d(e) {
    return (
      (function (e) {
        if (Array.isArray(e)) return p(e);
      })(e) ||
      (function (e) {
        if (
          ("undefined" != typeof Symbol && null != e[Symbol.iterator]) ||
          null != e["@@iterator"]
        )
          return Array.from(e);
      })(e) ||
      (function (e, t) {
        if (!e) return;
        if ("string" == typeof e) return p(e, t);
        var n = Object.prototype.toString.call(e).slice(8, -1);
        "Object" === n && e.constructor && (n = e.constructor.name);
        if ("Map" === n || "Set" === n) return Array.from(e);
        if (
          "Arguments" === n ||
          /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)
        )
          return p(e, t);
      })(e) ||
      (function () {
        throw new TypeError(
          "Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.",
        );
      })()
    );
  }
  function p(e, t) {
    (null == t || t > e.length) && (t = e.length);
    for (var n = 0, r = new Array(t); n < t; n++) r[n] = e[n];
    return r;
  }
  !(function () {
    if ("object" == typeof window)
      if (
        "IntersectionObserver" in window &&
        "IntersectionObserverEntry" in window &&
        "intersectionRatio" in window.IntersectionObserverEntry.prototype
      )
        "isIntersecting" in window.IntersectionObserverEntry.prototype ||
          Object.defineProperty(
            window.IntersectionObserverEntry.prototype,
            "isIntersecting",
            {
              get: function () {
                return this.intersectionRatio > 0;
              },
            },
          );
      else {
        var e = (function (e) {
            for (var t = window.document, n = o(t); n; )
              n = o((t = n.ownerDocument));
            return t;
          })(),
          t = [],
          n = null,
          r = null;
        (i.prototype.THROTTLE_TIMEOUT = 100),
          (i.prototype.POLL_INTERVAL = null),
          (i.prototype.USE_MUTATION_OBSERVER = !0),
          (i._setupCrossOriginUpdater = function () {
            return (
              n ||
                (n = function (e, n) {
                  (r =
                    e && n
                      ? f(e, n)
                      : {
                          top: 0,
                          bottom: 0,
                          left: 0,
                          right: 0,
                          width: 0,
                          height: 0,
                        }),
                    t.forEach(function (e) {
                      e._checkForIntersections();
                    });
                }),
              n
            );
          }),
          (i._resetCrossOriginUpdater = function () {
            (n = null), (r = null);
          }),
          (i.prototype.observe = function (e) {
            if (
              !this._observationTargets.some(function (t) {
                return t.element == e;
              })
            ) {
              if (!e || 1 != e.nodeType)
                throw new Error("target must be an Element");
              this._registerInstance(),
                this._observationTargets.push({ element: e, entry: null }),
                this._monitorIntersections(e.ownerDocument),
                this._checkForIntersections();
            }
          }),
          (i.prototype.unobserve = function (e) {
            (this._observationTargets = this._observationTargets.filter(
              function (t) {
                return t.element != e;
              },
            )),
              this._unmonitorIntersections(e.ownerDocument),
              0 == this._observationTargets.length &&
                this._unregisterInstance();
          }),
          (i.prototype.disconnect = function () {
            (this._observationTargets = []),
              this._unmonitorAllIntersections(),
              this._unregisterInstance();
          }),
          (i.prototype.takeRecords = function () {
            var e = this._queuedEntries.slice();
            return (this._queuedEntries = []), e;
          }),
          (i.prototype._initThresholds = function (e) {
            var t = e || [0];
            return (
              Array.isArray(t) || (t = [t]),
              t.sort().filter(function (e, t, n) {
                if ("number" != typeof e || isNaN(e) || e < 0 || e > 1)
                  throw new Error(
                    "threshold must be a number between 0 and 1 inclusively",
                  );
                return e !== n[t - 1];
              })
            );
          }),
          (i.prototype._parseRootMargin = function (e) {
            var t = (e || "0px").split(/\s+/).map(function (e) {
              var t = /^(-?\d*\.?\d+)(px|%)$/.exec(e);
              if (!t)
                throw new Error(
                  "rootMargin must be specified in pixels or percent",
                );
              return { value: parseFloat(t[1]), unit: t[2] };
            });
            return (
              (t[1] = t[1] || t[0]),
              (t[2] = t[2] || t[0]),
              (t[3] = t[3] || t[1]),
              t
            );
          }),
          (i.prototype._monitorIntersections = function (t) {
            var n = t.defaultView;
            if (n && -1 == this._monitoringDocuments.indexOf(t)) {
              var r = this._checkForIntersections,
                a = null,
                i = null;
              this.POLL_INTERVAL
                ? (a = n.setInterval(r, this.POLL_INTERVAL))
                : (c(n, "resize", r, !0),
                  c(t, "scroll", r, !0),
                  this.USE_MUTATION_OBSERVER &&
                    "MutationObserver" in n &&
                    (i = new n.MutationObserver(r)).observe(t, {
                      attributes: !0,
                      childList: !0,
                      characterData: !0,
                      subtree: !0,
                    })),
                this._monitoringDocuments.push(t),
                this._monitoringUnsubscribes.push(function () {
                  var e = t.defaultView;
                  e && (a && e.clearInterval(a), u(e, "resize", r, !0)),
                    u(t, "scroll", r, !0),
                    i && i.disconnect();
                });
              var l =
                (this.root && (this.root.ownerDocument || this.root)) || e;
              if (t != l) {
                var s = o(t);
                s && this._monitorIntersections(s.ownerDocument);
              }
            }
          }),
          (i.prototype._unmonitorIntersections = function (t) {
            var n = this._monitoringDocuments.indexOf(t);
            if (-1 != n) {
              var r =
                  (this.root && (this.root.ownerDocument || this.root)) || e,
                a = this._observationTargets.some(function (e) {
                  var n = e.element.ownerDocument;
                  if (n == t) return !0;
                  for (; n && n != r; ) {
                    var a = o(n);
                    if ((n = a && a.ownerDocument) == t) return !0;
                  }
                  return !1;
                });
              if (!a) {
                var i = this._monitoringUnsubscribes[n];
                if (
                  (this._monitoringDocuments.splice(n, 1),
                  this._monitoringUnsubscribes.splice(n, 1),
                  i(),
                  t != r)
                ) {
                  var c = o(t);
                  c && this._unmonitorIntersections(c.ownerDocument);
                }
              }
            }
          }),
          (i.prototype._unmonitorAllIntersections = function () {
            var e = this._monitoringUnsubscribes.slice(0);
            (this._monitoringDocuments.length = 0),
              (this._monitoringUnsubscribes.length = 0);
            for (var t = 0; t < e.length; t++) e[t]();
          }),
          (i.prototype._checkForIntersections = function () {
            if (this.root || !n || r) {
              var e = this._rootIsInDom(),
                t = e
                  ? this._getRootRect()
                  : {
                      top: 0,
                      bottom: 0,
                      left: 0,
                      right: 0,
                      width: 0,
                      height: 0,
                    };
              this._observationTargets.forEach(function (r) {
                var o = r.element,
                  i = l(o),
                  c = this._rootContainsTarget(o),
                  u = r.entry,
                  s = e && c && this._computeTargetAndRootIntersection(o, i, t),
                  f = null;
                this._rootContainsTarget(o)
                  ? (n && !this.root) || (f = t)
                  : (f = {
                      top: 0,
                      bottom: 0,
                      left: 0,
                      right: 0,
                      width: 0,
                      height: 0,
                    });
                var d = (r.entry = new a({
                  time:
                    window.performance && performance.now && performance.now(),
                  target: o,
                  boundingClientRect: i,
                  rootBounds: f,
                  intersectionRect: s,
                }));
                u
                  ? e && c
                    ? this._hasCrossedThreshold(u, d) &&
                      this._queuedEntries.push(d)
                    : u && u.isIntersecting && this._queuedEntries.push(d)
                  : this._queuedEntries.push(d);
              }, this),
                this._queuedEntries.length &&
                  this._callback(this.takeRecords(), this);
            }
          }),
          (i.prototype._computeTargetAndRootIntersection = function (t, o, a) {
            if ("none" != window.getComputedStyle(t).display) {
              for (
                var i, c, u, s, d, m, v, h, g = o, y = p(t), b = !1;
                !b && y;

              ) {
                var E = null,
                  w = 1 == y.nodeType ? window.getComputedStyle(y) : {};
                if ("none" == w.display) return null;
                if (y == this.root || 9 == y.nodeType)
                  if (((b = !0), y == this.root || y == e))
                    n && !this.root
                      ? !r || (0 == r.width && 0 == r.height)
                        ? ((y = null), (E = null), (g = null))
                        : (E = r)
                      : (E = a);
                  else {
                    var x = p(y),
                      N = x && l(x),
                      T = x && this._computeTargetAndRootIntersection(x, N, a);
                    N && T
                      ? ((y = x), (E = f(N, T)))
                      : ((y = null), (g = null));
                  }
                else {
                  var S = y.ownerDocument;
                  y != S.body &&
                    y != S.documentElement &&
                    "visible" != w.overflow &&
                    (E = l(y));
                }
                if (
                  (E &&
                    ((i = E),
                    (c = g),
                    (u = void 0),
                    (s = void 0),
                    (d = void 0),
                    (m = void 0),
                    (v = void 0),
                    (h = void 0),
                    (u = Math.max(i.top, c.top)),
                    (s = Math.min(i.bottom, c.bottom)),
                    (d = Math.max(i.left, c.left)),
                    (m = Math.min(i.right, c.right)),
                    (h = s - u),
                    (g =
                      ((v = m - d) >= 0 &&
                        h >= 0 && {
                          top: u,
                          bottom: s,
                          left: d,
                          right: m,
                          width: v,
                          height: h,
                        }) ||
                      null)),
                  !g)
                )
                  break;
                y = y && p(y);
              }
              return g;
            }
          }),
          (i.prototype._getRootRect = function () {
            var t;
            if (this.root && !m(this.root)) t = l(this.root);
            else {
              var n = m(this.root) ? this.root : e,
                r = n.documentElement,
                o = n.body;
              t = {
                top: 0,
                left: 0,
                right: r.clientWidth || o.clientWidth,
                width: r.clientWidth || o.clientWidth,
                bottom: r.clientHeight || o.clientHeight,
                height: r.clientHeight || o.clientHeight,
              };
            }
            return this._expandRectByRootMargin(t);
          }),
          (i.prototype._expandRectByRootMargin = function (e) {
            var t = this._rootMarginValues.map(function (t, n) {
                return "px" == t.unit
                  ? t.value
                  : (t.value * (n % 2 ? e.width : e.height)) / 100;
              }),
              n = {
                top: e.top - t[0],
                right: e.right + t[1],
                bottom: e.bottom + t[2],
                left: e.left - t[3],
              };
            return (
              (n.width = n.right - n.left), (n.height = n.bottom - n.top), n
            );
          }),
          (i.prototype._hasCrossedThreshold = function (e, t) {
            var n = e && e.isIntersecting ? e.intersectionRatio || 0 : -1,
              r = t.isIntersecting ? t.intersectionRatio || 0 : -1;
            if (n !== r)
              for (var o = 0; o < this.thresholds.length; o++) {
                var a = this.thresholds[o];
                if (a == n || a == r || a < n != a < r) return !0;
              }
          }),
          (i.prototype._rootIsInDom = function () {
            return !this.root || d(e, this.root);
          }),
          (i.prototype._rootContainsTarget = function (t) {
            var n = (this.root && (this.root.ownerDocument || this.root)) || e;
            return d(n, t) && (!this.root || n == t.ownerDocument);
          }),
          (i.prototype._registerInstance = function () {
            t.indexOf(this) < 0 && t.push(this);
          }),
          (i.prototype._unregisterInstance = function () {
            var e = t.indexOf(this);
            -1 != e && t.splice(e, 1);
          }),
          (window.IntersectionObserver = i),
          (window.IntersectionObserverEntry = a);
      }
    function o(e) {
      try {
        return (e.defaultView && e.defaultView.frameElement) || null;
      } catch (e) {
        return null;
      }
    }
    function a(e) {
      (this.time = e.time),
        (this.target = e.target),
        (this.rootBounds = s(e.rootBounds)),
        (this.boundingClientRect = s(e.boundingClientRect)),
        (this.intersectionRect = s(
          e.intersectionRect || {
            top: 0,
            bottom: 0,
            left: 0,
            right: 0,
            width: 0,
            height: 0,
          },
        )),
        (this.isIntersecting = !!e.intersectionRect);
      var t = this.boundingClientRect,
        n = t.width * t.height,
        r = this.intersectionRect,
        o = r.width * r.height;
      this.intersectionRatio = n
        ? Number((o / n).toFixed(4))
        : this.isIntersecting
          ? 1
          : 0;
    }
    function i(e, t) {
      var n,
        r,
        o,
        a = t || {};
      if ("function" != typeof e)
        throw new Error("callback must be a function");
      if (a.root && 1 != a.root.nodeType && 9 != a.root.nodeType)
        throw new Error("root must be a Document or Element");
      (this._checkForIntersections =
        ((n = this._checkForIntersections.bind(this)),
        (r = this.THROTTLE_TIMEOUT),
        (o = null),
        function () {
          o ||
            (o = setTimeout(function () {
              n(), (o = null);
            }, r));
        })),
        (this._callback = e),
        (this._observationTargets = []),
        (this._queuedEntries = []),
        (this._rootMarginValues = this._parseRootMargin(a.rootMargin)),
        (this.thresholds = this._initThresholds(a.threshold)),
        (this.root = a.root || null),
        (this.rootMargin = this._rootMarginValues
          .map(function (e) {
            return e.value + e.unit;
          })
          .join(" ")),
        (this._monitoringDocuments = []),
        (this._monitoringUnsubscribes = []);
    }
    function c(e, t, n, r) {
      "function" == typeof e.addEventListener
        ? e.addEventListener(t, n, r || !1)
        : "function" == typeof e.attachEvent && e.attachEvent("on" + t, n);
    }
    function u(e, t, n, r) {
      "function" == typeof e.removeEventListener
        ? e.removeEventListener(t, n, r || !1)
        : "function" == typeof e.detachEvent && e.detachEvent("on" + t, n);
    }
    function l(e) {
      var t;
      try {
        t = e.getBoundingClientRect();
      } catch (e) {}
      return t
        ? ((t.width && t.height) ||
            (t = {
              top: t.top,
              right: t.right,
              bottom: t.bottom,
              left: t.left,
              width: t.right - t.left,
              height: t.bottom - t.top,
            }),
          t)
        : { top: 0, bottom: 0, left: 0, right: 0, width: 0, height: 0 };
    }
    function s(e) {
      return !e || "x" in e
        ? e
        : {
            top: e.top,
            y: e.top,
            bottom: e.bottom,
            left: e.left,
            x: e.left,
            right: e.right,
            width: e.width,
            height: e.height,
          };
    }
    function f(e, t) {
      var n = t.top - e.top,
        r = t.left - e.left;
      return {
        top: n,
        left: r,
        height: t.height,
        width: t.width,
        bottom: n + t.height,
        right: r + t.width,
      };
    }
    function d(e, t) {
      for (var n = t; n; ) {
        if (n == e) return !0;
        n = p(n);
      }
      return !1;
    }
    function p(t) {
      var n = t.parentNode;
      return 9 == t.nodeType && t != e
        ? o(t)
        : (n && n.assignedSlot && (n = n.assignedSlot.parentNode),
          n && 11 == n.nodeType && n.host ? n.host : n);
    }
    function m(e) {
      return e && 9 === e.nodeType;
    }
  })();
  var m = Object.hasOwnProperty,
    v = Object.setPrototypeOf,
    h = Object.isFrozen,
    g = Object.getPrototypeOf,
    y = Object.getOwnPropertyDescriptor,
    b = Object.freeze,
    E = Object.seal,
    w = Object.create,
    x = "undefined" != typeof Reflect && Reflect,
    N = x.apply,
    T = x.construct;
  N ||
    (N = function (e, t, n) {
      return e.apply(t, n);
    }),
    b ||
      (b = function (e) {
        return e;
      }),
    E ||
      (E = function (e) {
        return e;
      }),
    T ||
      (T = function (e, t) {
        return f(e, d(t));
      });
  var S,
    C = P(Array.prototype.forEach),
    O = P(Array.prototype.pop),
    R = P(Array.prototype.push),
    k = P(String.prototype.toLowerCase),
    I = P(String.prototype.match),
    A = P(String.prototype.replace),
    M = P(String.prototype.indexOf),
    _ = P(String.prototype.trim),
    j = P(RegExp.prototype.test),
    L =
      ((S = TypeError),
      function () {
        for (var e = arguments.length, t = new Array(e), n = 0; n < e; n++)
          t[n] = arguments[n];
        return T(S, t);
      });
  function P(e) {
    return function (t) {
      for (
        var n = arguments.length, r = new Array(n > 1 ? n - 1 : 0), o = 1;
        o < n;
        o++
      )
        r[o - 1] = arguments[o];
      return N(e, t, r);
    };
  }
  function D(e, t, n) {
    (n = n || k), v && v(e, null);
    for (var r = t.length; r--; ) {
      var o = t[r];
      if ("string" == typeof o) {
        var a = n(o);
        a !== o && (h(t) || (t[r] = a), (o = a));
      }
      e[o] = !0;
    }
    return e;
  }
  function F(e) {
    var t,
      n = w(null);
    for (t in e) N(m, e, [t]) && (n[t] = e[t]);
    return n;
  }
  function H(e, t) {
    for (; null !== e; ) {
      var n = y(e, t);
      if (n) {
        if (n.get) return P(n.get);
        if ("function" == typeof n.value) return P(n.value);
      }
      e = g(e);
    }
    return function (e) {
      return null;
    };
  }
  var B = b([
      "a",
      "abbr",
      "acronym",
      "address",
      "area",
      "article",
      "aside",
      "audio",
      "b",
      "bdi",
      "bdo",
      "big",
      "blink",
      "blockquote",
      "body",
      "br",
      "button",
      "canvas",
      "caption",
      "center",
      "cite",
      "code",
      "col",
      "colgroup",
      "content",
      "data",
      "datalist",
      "dd",
      "decorator",
      "del",
      "details",
      "dfn",
      "dialog",
      "dir",
      "div",
      "dl",
      "dt",
      "element",
      "em",
      "fieldset",
      "figcaption",
      "figure",
      "font",
      "footer",
      "form",
      "h1",
      "h2",
      "h3",
      "h4",
      "h5",
      "h6",
      "head",
      "header",
      "hgroup",
      "hr",
      "html",
      "i",
      "img",
      "input",
      "ins",
      "kbd",
      "label",
      "legend",
      "li",
      "main",
      "map",
      "mark",
      "marquee",
      "menu",
      "menuitem",
      "meter",
      "nav",
      "nobr",
      "ol",
      "optgroup",
      "option",
      "output",
      "p",
      "picture",
      "pre",
      "progress",
      "q",
      "rp",
      "rt",
      "ruby",
      "s",
      "samp",
      "section",
      "select",
      "shadow",
      "small",
      "source",
      "spacer",
      "span",
      "strike",
      "strong",
      "style",
      "sub",
      "summary",
      "sup",
      "table",
      "tbody",
      "td",
      "template",
      "textarea",
      "tfoot",
      "th",
      "thead",
      "time",
      "tr",
      "track",
      "tt",
      "u",
      "ul",
      "var",
      "video",
      "wbr",
    ]),
    U = b([
      "svg",
      "a",
      "altglyph",
      "altglyphdef",
      "altglyphitem",
      "animatecolor",
      "animatemotion",
      "animatetransform",
      "circle",
      "clippath",
      "defs",
      "desc",
      "ellipse",
      "filter",
      "font",
      "g",
      "glyph",
      "glyphref",
      "hkern",
      "image",
      "line",
      "lineargradient",
      "marker",
      "mask",
      "metadata",
      "mpath",
      "path",
      "pattern",
      "polygon",
      "polyline",
      "radialgradient",
      "rect",
      "stop",
      "style",
      "switch",
      "symbol",
      "text",
      "textpath",
      "title",
      "tref",
      "tspan",
      "view",
      "vkern",
    ]),
    z = b([
      "feBlend",
      "feColorMatrix",
      "feComponentTransfer",
      "feComposite",
      "feConvolveMatrix",
      "feDiffuseLighting",
      "feDisplacementMap",
      "feDistantLight",
      "feFlood",
      "feFuncA",
      "feFuncB",
      "feFuncG",
      "feFuncR",
      "feGaussianBlur",
      "feImage",
      "feMerge",
      "feMergeNode",
      "feMorphology",
      "feOffset",
      "fePointLight",
      "feSpecularLighting",
      "feSpotLight",
      "feTile",
      "feTurbulence",
    ]),
    G = b([
      "animate",
      "color-profile",
      "cursor",
      "discard",
      "fedropshadow",
      "font-face",
      "font-face-format",
      "font-face-name",
      "font-face-src",
      "font-face-uri",
      "foreignobject",
      "hatch",
      "hatchpath",
      "mesh",
      "meshgradient",
      "meshpatch",
      "meshrow",
      "missing-glyph",
      "script",
      "set",
      "solidcolor",
      "unknown",
      "use",
    ]),
    V = b([
      "math",
      "menclose",
      "merror",
      "mfenced",
      "mfrac",
      "mglyph",
      "mi",
      "mlabeledtr",
      "mmultiscripts",
      "mn",
      "mo",
      "mover",
      "mpadded",
      "mphantom",
      "mroot",
      "mrow",
      "ms",
      "mspace",
      "msqrt",
      "mstyle",
      "msub",
      "msup",
      "msubsup",
      "mtable",
      "mtd",
      "mtext",
      "mtr",
      "munder",
      "munderover",
    ]),
    Y = b([
      "maction",
      "maligngroup",
      "malignmark",
      "mlongdiv",
      "mscarries",
      "mscarry",
      "msgroup",
      "mstack",
      "msline",
      "msrow",
      "semantics",
      "annotation",
      "annotation-xml",
      "mprescripts",
      "none",
    ]),
    W = b(["#text"]),
    X = b([
      "accept",
      "action",
      "align",
      "alt",
      "autocapitalize",
      "autocomplete",
      "autopictureinpicture",
      "autoplay",
      "background",
      "bgcolor",
      "border",
      "capture",
      "cellpadding",
      "cellspacing",
      "checked",
      "cite",
      "class",
      "clear",
      "color",
      "cols",
      "colspan",
      "controls",
      "controlslist",
      "coords",
      "crossorigin",
      "datetime",
      "decoding",
      "default",
      "dir",
      "disabled",
      "disablepictureinpicture",
      "disableremoteplayback",
      "download",
      "draggable",
      "enctype",
      "enterkeyhint",
      "face",
      "for",
      "headers",
      "height",
      "hidden",
      "high",
      "href",
      "hreflang",
      "id",
      "inputmode",
      "integrity",
      "ismap",
      "kind",
      "label",
      "lang",
      "list",
      "loading",
      "loop",
      "low",
      "max",
      "maxlength",
      "media",
      "method",
      "min",
      "minlength",
      "multiple",
      "muted",
      "name",
      "nonce",
      "noshade",
      "novalidate",
      "nowrap",
      "open",
      "optimum",
      "pattern",
      "placeholder",
      "playsinline",
      "poster",
      "preload",
      "pubdate",
      "radiogroup",
      "readonly",
      "rel",
      "required",
      "rev",
      "reversed",
      "role",
      "rows",
      "rowspan",
      "spellcheck",
      "scope",
      "selected",
      "shape",
      "size",
      "sizes",
      "span",
      "srclang",
      "start",
      "src",
      "srcset",
      "step",
      "style",
      "summary",
      "tabindex",
      "title",
      "translate",
      "type",
      "usemap",
      "valign",
      "value",
      "width",
      "xmlns",
      "slot",
    ]),
    q = b([
      "accent-height",
      "accumulate",
      "additive",
      "alignment-baseline",
      "ascent",
      "attributename",
      "attributetype",
      "azimuth",
      "basefrequency",
      "baseline-shift",
      "begin",
      "bias",
      "by",
      "class",
      "clip",
      "clippathunits",
      "clip-path",
      "clip-rule",
      "color",
      "color-interpolation",
      "color-interpolation-filters",
      "color-profile",
      "color-rendering",
      "cx",
      "cy",
      "d",
      "dx",
      "dy",
      "diffuseconstant",
      "direction",
      "display",
      "divisor",
      "dur",
      "edgemode",
      "elevation",
      "end",
      "fill",
      "fill-opacity",
      "fill-rule",
      "filter",
      "filterunits",
      "flood-color",
      "flood-opacity",
      "font-family",
      "font-size",
      "font-size-adjust",
      "font-stretch",
      "font-style",
      "font-variant",
      "font-weight",
      "fx",
      "fy",
      "g1",
      "g2",
      "glyph-name",
      "glyphref",
      "gradientunits",
      "gradienttransform",
      "height",
      "href",
      "id",
      "image-rendering",
      "in",
      "in2",
      "k",
      "k1",
      "k2",
      "k3",
      "k4",
      "kerning",
      "keypoints",
      "keysplines",
      "keytimes",
      "lang",
      "lengthadjust",
      "letter-spacing",
      "kernelmatrix",
      "kernelunitlength",
      "lighting-color",
      "local",
      "marker-end",
      "marker-mid",
      "marker-start",
      "markerheight",
      "markerunits",
      "markerwidth",
      "maskcontentunits",
      "maskunits",
      "max",
      "mask",
      "media",
      "method",
      "mode",
      "min",
      "name",
      "numoctaves",
      "offset",
      "operator",
      "opacity",
      "order",
      "orient",
      "orientation",
      "origin",
      "overflow",
      "paint-order",
      "path",
      "pathlength",
      "patterncontentunits",
      "patterntransform",
      "patternunits",
      "points",
      "preservealpha",
      "preserveaspectratio",
      "primitiveunits",
      "r",
      "rx",
      "ry",
      "radius",
      "refx",
      "refy",
      "repeatcount",
      "repeatdur",
      "restart",
      "result",
      "rotate",
      "scale",
      "seed",
      "shape-rendering",
      "specularconstant",
      "specularexponent",
      "spreadmethod",
      "startoffset",
      "stddeviation",
      "stitchtiles",
      "stop-color",
      "stop-opacity",
      "stroke-dasharray",
      "stroke-dashoffset",
      "stroke-linecap",
      "stroke-linejoin",
      "stroke-miterlimit",
      "stroke-opacity",
      "stroke",
      "stroke-width",
      "style",
      "surfacescale",
      "systemlanguage",
      "tabindex",
      "targetx",
      "targety",
      "transform",
      "transform-origin",
      "text-anchor",
      "text-decoration",
      "text-rendering",
      "textlength",
      "type",
      "u1",
      "u2",
      "unicode",
      "values",
      "viewbox",
      "visibility",
      "version",
      "vert-adv-y",
      "vert-origin-x",
      "vert-origin-y",
      "width",
      "word-spacing",
      "wrap",
      "writing-mode",
      "xchannelselector",
      "ychannelselector",
      "x",
      "x1",
      "x2",
      "xmlns",
      "y",
      "y1",
      "y2",
      "z",
      "zoomandpan",
    ]),
    $ = b([
      "accent",
      "accentunder",
      "align",
      "bevelled",
      "close",
      "columnsalign",
      "columnlines",
      "columnspan",
      "denomalign",
      "depth",
      "dir",
      "display",
      "displaystyle",
      "encoding",
      "fence",
      "frame",
      "height",
      "href",
      "id",
      "largeop",
      "length",
      "linethickness",
      "lspace",
      "lquote",
      "mathbackground",
      "mathcolor",
      "mathsize",
      "mathvariant",
      "maxsize",
      "minsize",
      "movablelimits",
      "notation",
      "numalign",
      "open",
      "rowalign",
      "rowlines",
      "rowspacing",
      "rowspan",
      "rspace",
      "rquote",
      "scriptlevel",
      "scriptminsize",
      "scriptsizemultiplier",
      "selection",
      "separator",
      "separators",
      "stretchy",
      "subscriptshift",
      "supscriptshift",
      "symmetric",
      "voffset",
      "width",
      "xmlns",
    ]),
    K = b(["xlink:href", "xml:id", "xlink:title", "xml:space", "xmlns:xlink"]),
    J = E(/\{\{[\w\W]*|[\w\W]*\}\}/gm),
    Q = E(/<%[\w\W]*|[\w\W]*%>/gm),
    Z = E(/^data-[\-\w.\u00B7-\uFFFF]/),
    ee = E(/^aria-[\-\w]+$/),
    te = E(
      /^(?:(?:(?:f|ht)tps?|mailto|tel|callto|cid|xmpp):|[^a-z]|[a-z+.\-]+(?:[^a-z+.\-:]|$))/i,
    ),
    ne = E(/^(?:\w+script|data):/i),
    re = E(/[\u0000-\u0020\u00A0\u1680\u180E\u2000-\u2029\u205F\u3000]/g),
    oe = E(/^html$/i),
    ae = function () {
      return "undefined" == typeof window ? null : window;
    },
    ie = function (e, t) {
      if ("object" !== u(e) || "function" != typeof e.createPolicy) return null;
      var n = null,
        r = "data-tt-policy-suffix";
      t.currentScript &&
        t.currentScript.hasAttribute(r) &&
        (n = t.currentScript.getAttribute(r));
      var o = "dompurify" + (n ? "#" + n : "");
      try {
        return e.createPolicy(o, {
          createHTML: function (e) {
            return e;
          },
          createScriptURL: function (e) {
            return e;
          },
        });
      } catch (e) {
        return null;
      }
    };
  var ce = (function e() {
      var t =
          arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : ae(),
        n = function (t) {
          return e(t);
        };
      if (
        ((n.version = "2.4.0"),
        (n.removed = []),
        !t || !t.document || 9 !== t.document.nodeType)
      )
        return (n.isSupported = !1), n;
      var r = t.document,
        o = t.document,
        a = t.DocumentFragment,
        i = t.HTMLTemplateElement,
        c = t.Node,
        l = t.Element,
        s = t.NodeFilter,
        f = t.NamedNodeMap,
        p = void 0 === f ? t.NamedNodeMap || t.MozNamedAttrMap : f,
        m = t.HTMLFormElement,
        v = t.DOMParser,
        h = t.trustedTypes,
        g = l.prototype,
        y = H(g, "cloneNode"),
        E = H(g, "nextSibling"),
        w = H(g, "childNodes"),
        x = H(g, "parentNode");
      if ("function" == typeof i) {
        var N = o.createElement("template");
        N.content && N.content.ownerDocument && (o = N.content.ownerDocument);
      }
      var T = ie(h, r),
        S = T ? T.createHTML("") : "",
        P = o,
        ce = P.implementation,
        ue = P.createNodeIterator,
        le = P.createDocumentFragment,
        se = P.getElementsByTagName,
        fe = r.importNode,
        de = {};
      try {
        de = F(o).documentMode ? o.documentMode : {};
      } catch (e) {}
      var pe = {};
      n.isSupported =
        "function" == typeof x &&
        ce &&
        void 0 !== ce.createHTMLDocument &&
        9 !== de;
      var me,
        ve,
        he = J,
        ge = Q,
        ye = Z,
        be = ee,
        Ee = ne,
        we = re,
        xe = te,
        Ne = null,
        Te = D({}, [].concat(d(B), d(U), d(z), d(V), d(W))),
        Se = null,
        Ce = D({}, [].concat(d(X), d(q), d($), d(K))),
        Oe = Object.seal(
          Object.create(null, {
            tagNameCheck: {
              writable: !0,
              configurable: !1,
              enumerable: !0,
              value: null,
            },
            attributeNameCheck: {
              writable: !0,
              configurable: !1,
              enumerable: !0,
              value: null,
            },
            allowCustomizedBuiltInElements: {
              writable: !0,
              configurable: !1,
              enumerable: !0,
              value: !1,
            },
          }),
        ),
        Re = null,
        ke = null,
        Ie = !0,
        Ae = !0,
        Me = !1,
        _e = !1,
        je = !1,
        Le = !1,
        Pe = !1,
        De = !1,
        Fe = !1,
        He = !1,
        Be = !0,
        Ue = !1,
        ze = "user-content-",
        Ge = !0,
        Ve = !1,
        Ye = {},
        We = null,
        Xe = D({}, [
          "annotation-xml",
          "audio",
          "colgroup",
          "desc",
          "foreignobject",
          "head",
          "iframe",
          "math",
          "mi",
          "mn",
          "mo",
          "ms",
          "mtext",
          "noembed",
          "noframes",
          "noscript",
          "plaintext",
          "script",
          "style",
          "svg",
          "template",
          "thead",
          "title",
          "video",
          "xmp",
        ]),
        qe = null,
        $e = D({}, ["audio", "video", "img", "source", "image", "track"]),
        Ke = null,
        Je = D({}, [
          "alt",
          "class",
          "for",
          "id",
          "label",
          "name",
          "pattern",
          "placeholder",
          "role",
          "summary",
          "title",
          "value",
          "style",
          "xmlns",
        ]),
        Qe = "http://www.w3.org/1998/Math/MathML",
        Ze = "http://www.w3.org/2000/svg",
        et = "http://www.w3.org/1999/xhtml",
        tt = et,
        nt = !1,
        rt = ["application/xhtml+xml", "text/html"],
        ot = "text/html",
        at = null,
        it = o.createElement("form"),
        ct = function (e) {
          return e instanceof RegExp || e instanceof Function;
        },
        ut = function (e) {
          (at && at === e) ||
            ((e && "object" === u(e)) || (e = {}),
            (e = F(e)),
            (me = me =
              -1 === rt.indexOf(e.PARSER_MEDIA_TYPE)
                ? ot
                : e.PARSER_MEDIA_TYPE),
            (ve =
              "application/xhtml+xml" === me
                ? function (e) {
                    return e;
                  }
                : k),
            (Ne = "ALLOWED_TAGS" in e ? D({}, e.ALLOWED_TAGS, ve) : Te),
            (Se = "ALLOWED_ATTR" in e ? D({}, e.ALLOWED_ATTR, ve) : Ce),
            (Ke =
              "ADD_URI_SAFE_ATTR" in e
                ? D(F(Je), e.ADD_URI_SAFE_ATTR, ve)
                : Je),
            (qe =
              "ADD_DATA_URI_TAGS" in e
                ? D(F($e), e.ADD_DATA_URI_TAGS, ve)
                : $e),
            (We = "FORBID_CONTENTS" in e ? D({}, e.FORBID_CONTENTS, ve) : Xe),
            (Re = "FORBID_TAGS" in e ? D({}, e.FORBID_TAGS, ve) : {}),
            (ke = "FORBID_ATTR" in e ? D({}, e.FORBID_ATTR, ve) : {}),
            (Ye = "USE_PROFILES" in e && e.USE_PROFILES),
            (Ie = !1 !== e.ALLOW_ARIA_ATTR),
            (Ae = !1 !== e.ALLOW_DATA_ATTR),
            (Me = e.ALLOW_UNKNOWN_PROTOCOLS || !1),
            (_e = e.SAFE_FOR_TEMPLATES || !1),
            (je = e.WHOLE_DOCUMENT || !1),
            (De = e.RETURN_DOM || !1),
            (Fe = e.RETURN_DOM_FRAGMENT || !1),
            (He = e.RETURN_TRUSTED_TYPE || !1),
            (Pe = e.FORCE_BODY || !1),
            (Be = !1 !== e.SANITIZE_DOM),
            (Ue = e.SANITIZE_NAMED_PROPS || !1),
            (Ge = !1 !== e.KEEP_CONTENT),
            (Ve = e.IN_PLACE || !1),
            (xe = e.ALLOWED_URI_REGEXP || xe),
            (tt = e.NAMESPACE || et),
            e.CUSTOM_ELEMENT_HANDLING &&
              ct(e.CUSTOM_ELEMENT_HANDLING.tagNameCheck) &&
              (Oe.tagNameCheck = e.CUSTOM_ELEMENT_HANDLING.tagNameCheck),
            e.CUSTOM_ELEMENT_HANDLING &&
              ct(e.CUSTOM_ELEMENT_HANDLING.attributeNameCheck) &&
              (Oe.attributeNameCheck =
                e.CUSTOM_ELEMENT_HANDLING.attributeNameCheck),
            e.CUSTOM_ELEMENT_HANDLING &&
              "boolean" ==
                typeof e.CUSTOM_ELEMENT_HANDLING
                  .allowCustomizedBuiltInElements &&
              (Oe.allowCustomizedBuiltInElements =
                e.CUSTOM_ELEMENT_HANDLING.allowCustomizedBuiltInElements),
            _e && (Ae = !1),
            Fe && (De = !0),
            Ye &&
              ((Ne = D({}, d(W))),
              (Se = []),
              !0 === Ye.html && (D(Ne, B), D(Se, X)),
              !0 === Ye.svg && (D(Ne, U), D(Se, q), D(Se, K)),
              !0 === Ye.svgFilters && (D(Ne, z), D(Se, q), D(Se, K)),
              !0 === Ye.mathMl && (D(Ne, V), D(Se, $), D(Se, K))),
            e.ADD_TAGS && (Ne === Te && (Ne = F(Ne)), D(Ne, e.ADD_TAGS, ve)),
            e.ADD_ATTR && (Se === Ce && (Se = F(Se)), D(Se, e.ADD_ATTR, ve)),
            e.ADD_URI_SAFE_ATTR && D(Ke, e.ADD_URI_SAFE_ATTR, ve),
            e.FORBID_CONTENTS &&
              (We === Xe && (We = F(We)), D(We, e.FORBID_CONTENTS, ve)),
            Ge && (Ne["#text"] = !0),
            je && D(Ne, ["html", "head", "body"]),
            Ne.table && (D(Ne, ["tbody"]), delete Re.tbody),
            b && b(e),
            (at = e));
        },
        lt = D({}, ["mi", "mo", "mn", "ms", "mtext"]),
        st = D({}, ["foreignobject", "desc", "title", "annotation-xml"]),
        ft = D({}, ["title", "style", "font", "a", "script"]),
        dt = D({}, U);
      D(dt, z), D(dt, G);
      var pt = D({}, V);
      D(pt, Y);
      var mt = function (e) {
          var t = x(e);
          (t && t.tagName) || (t = { namespaceURI: et, tagName: "template" });
          var n = k(e.tagName),
            r = k(t.tagName);
          return e.namespaceURI === Ze
            ? t.namespaceURI === et
              ? "svg" === n
              : t.namespaceURI === Qe
                ? "svg" === n && ("annotation-xml" === r || lt[r])
                : Boolean(dt[n])
            : e.namespaceURI === Qe
              ? t.namespaceURI === et
                ? "math" === n
                : t.namespaceURI === Ze
                  ? "math" === n && st[r]
                  : Boolean(pt[n])
              : e.namespaceURI === et &&
                !(t.namespaceURI === Ze && !st[r]) &&
                !(t.namespaceURI === Qe && !lt[r]) &&
                !pt[n] &&
                (ft[n] || !dt[n]);
        },
        vt = function (e) {
          R(n.removed, { element: e });
          try {
            e.parentNode.removeChild(e);
          } catch (t) {
            try {
              e.outerHTML = S;
            } catch (t) {
              e.remove();
            }
          }
        },
        ht = function (e, t) {
          try {
            R(n.removed, { attribute: t.getAttributeNode(e), from: t });
          } catch (e) {
            R(n.removed, { attribute: null, from: t });
          }
          if ((t.removeAttribute(e), "is" === e && !Se[e]))
            if (De || Fe)
              try {
                vt(t);
              } catch (e) {}
            else
              try {
                t.setAttribute(e, "");
              } catch (e) {}
        },
        gt = function (e) {
          var t, n;
          if (Pe) e = "<remove></remove>" + e;
          else {
            var r = I(e, /^[\r\n\t ]+/);
            n = r && r[0];
          }
          "application/xhtml+xml" === me &&
            (e =
              '<html xmlns="http://www.w3.org/1999/xhtml"><head></head><body>' +
              e +
              "</body></html>");
          var a = T ? T.createHTML(e) : e;
          if (tt === et)
            try {
              t = new v().parseFromString(a, me);
            } catch (e) {}
          if (!t || !t.documentElement) {
            t = ce.createDocument(tt, "template", null);
            try {
              t.documentElement.innerHTML = nt ? "" : a;
            } catch (e) {}
          }
          var i = t.body || t.documentElement;
          return (
            e &&
              n &&
              i.insertBefore(o.createTextNode(n), i.childNodes[0] || null),
            tt === et
              ? se.call(t, je ? "html" : "body")[0]
              : je
                ? t.documentElement
                : i
          );
        },
        yt = function (e) {
          return ue.call(
            e.ownerDocument || e,
            e,
            s.SHOW_ELEMENT | s.SHOW_COMMENT | s.SHOW_TEXT,
            null,
            !1,
          );
        },
        bt = function (e) {
          return (
            e instanceof m &&
            ("string" != typeof e.nodeName ||
              "string" != typeof e.textContent ||
              "function" != typeof e.removeChild ||
              !(e.attributes instanceof p) ||
              "function" != typeof e.removeAttribute ||
              "function" != typeof e.setAttribute ||
              "string" != typeof e.namespaceURI ||
              "function" != typeof e.insertBefore)
          );
        },
        Et = function (e) {
          return "object" === u(c)
            ? e instanceof c
            : e &&
                "object" === u(e) &&
                "number" == typeof e.nodeType &&
                "string" == typeof e.nodeName;
        },
        wt = function (e, t, r) {
          pe[e] &&
            C(pe[e], function (e) {
              e.call(n, t, r, at);
            });
        },
        xt = function (e) {
          var t;
          if ((wt("beforeSanitizeElements", e, null), bt(e))) return vt(e), !0;
          if (j(/[\u0080-\uFFFF]/, e.nodeName)) return vt(e), !0;
          var r = ve(e.nodeName);
          if (
            (wt("uponSanitizeElement", e, { tagName: r, allowedTags: Ne }),
            e.hasChildNodes() &&
              !Et(e.firstElementChild) &&
              (!Et(e.content) || !Et(e.content.firstElementChild)) &&
              j(/<[/\w]/g, e.innerHTML) &&
              j(/<[/\w]/g, e.textContent))
          )
            return vt(e), !0;
          if ("select" === r && j(/<template/i, e.innerHTML)) return vt(e), !0;
          if (!Ne[r] || Re[r]) {
            if (!Re[r] && Tt(r)) {
              if (Oe.tagNameCheck instanceof RegExp && j(Oe.tagNameCheck, r))
                return !1;
              if (Oe.tagNameCheck instanceof Function && Oe.tagNameCheck(r))
                return !1;
            }
            if (Ge && !We[r]) {
              var o = x(e) || e.parentNode,
                a = w(e) || e.childNodes;
              if (a && o)
                for (var i = a.length - 1; i >= 0; --i)
                  o.insertBefore(y(a[i], !0), E(e));
            }
            return vt(e), !0;
          }
          return e instanceof l && !mt(e)
            ? (vt(e), !0)
            : ("noscript" !== r && "noembed" !== r) ||
                !j(/<\/no(script|embed)/i, e.innerHTML)
              ? (_e &&
                  3 === e.nodeType &&
                  ((t = e.textContent),
                  (t = A(t, he, " ")),
                  (t = A(t, ge, " ")),
                  e.textContent !== t &&
                    (R(n.removed, { element: e.cloneNode() }),
                    (e.textContent = t))),
                wt("afterSanitizeElements", e, null),
                !1)
              : (vt(e), !0);
        },
        Nt = function (e, t, n) {
          if (Be && ("id" === t || "name" === t) && (n in o || n in it))
            return !1;
          if (Ae && !ke[t] && j(ye, t));
          else if (Ie && j(be, t));
          else if (!Se[t] || ke[t]) {
            if (
              !(
                (Tt(e) &&
                  ((Oe.tagNameCheck instanceof RegExp &&
                    j(Oe.tagNameCheck, e)) ||
                    (Oe.tagNameCheck instanceof Function &&
                      Oe.tagNameCheck(e))) &&
                  ((Oe.attributeNameCheck instanceof RegExp &&
                    j(Oe.attributeNameCheck, t)) ||
                    (Oe.attributeNameCheck instanceof Function &&
                      Oe.attributeNameCheck(t)))) ||
                ("is" === t &&
                  Oe.allowCustomizedBuiltInElements &&
                  ((Oe.tagNameCheck instanceof RegExp &&
                    j(Oe.tagNameCheck, n)) ||
                    (Oe.tagNameCheck instanceof Function &&
                      Oe.tagNameCheck(n))))
              )
            )
              return !1;
          } else if (Ke[t]);
          else if (j(xe, A(n, we, "")));
          else if (
            ("src" !== t && "xlink:href" !== t && "href" !== t) ||
            "script" === e ||
            0 !== M(n, "data:") ||
            !qe[e]
          ) {
            if (Me && !j(Ee, A(n, we, "")));
            else if (n) return !1;
          } else;
          return !0;
        },
        Tt = function (e) {
          return e.indexOf("-") > 0;
        },
        St = function (e) {
          var t, r, o, a;
          wt("beforeSanitizeAttributes", e, null);
          var i = e.attributes;
          if (i) {
            var c = {
              attrName: "",
              attrValue: "",
              keepAttr: !0,
              allowedAttributes: Se,
            };
            for (a = i.length; a--; ) {
              var l = (t = i[a]),
                s = l.name,
                f = l.namespaceURI;
              if (
                ((r = "value" === s ? t.value : _(t.value)),
                (o = ve(s)),
                (c.attrName = o),
                (c.attrValue = r),
                (c.keepAttr = !0),
                (c.forceKeepAttr = void 0),
                wt("uponSanitizeAttribute", e, c),
                (r = c.attrValue),
                !c.forceKeepAttr && (ht(s, e), c.keepAttr))
              )
                if (j(/\/>/i, r)) ht(s, e);
                else {
                  _e && ((r = A(r, he, " ")), (r = A(r, ge, " ")));
                  var d = ve(e.nodeName);
                  if (Nt(d, o, r)) {
                    if (
                      (!Ue ||
                        ("id" !== o && "name" !== o) ||
                        (ht(s, e), (r = ze + r)),
                      T &&
                        "object" === u(h) &&
                        "function" == typeof h.getAttributeType)
                    )
                      if (f);
                      else
                        switch (h.getAttributeType(d, o)) {
                          case "TrustedHTML":
                            r = T.createHTML(r);
                            break;
                          case "TrustedScriptURL":
                            r = T.createScriptURL(r);
                        }
                    try {
                      f ? e.setAttributeNS(f, s, r) : e.setAttribute(s, r),
                        O(n.removed);
                    } catch (e) {}
                  }
                }
            }
            wt("afterSanitizeAttributes", e, null);
          }
        },
        Ct = function e(t) {
          var n,
            r = yt(t);
          for (wt("beforeSanitizeShadowDOM", t, null); (n = r.nextNode()); )
            wt("uponSanitizeShadowNode", n, null),
              xt(n) || (n.content instanceof a && e(n.content), St(n));
          wt("afterSanitizeShadowDOM", t, null);
        };
      return (
        (n.sanitize = function (e) {
          var o,
            i,
            l,
            s,
            f,
            d =
              arguments.length > 1 && void 0 !== arguments[1]
                ? arguments[1]
                : {};
          if (
            ((nt = !e) && (e = "\x3c!--\x3e"), "string" != typeof e && !Et(e))
          ) {
            if ("function" != typeof e.toString)
              throw L("toString is not a function");
            if ("string" != typeof (e = e.toString()))
              throw L("dirty is not a string, aborting");
          }
          if (!n.isSupported) {
            if (
              "object" === u(t.toStaticHTML) ||
              "function" == typeof t.toStaticHTML
            ) {
              if ("string" == typeof e) return t.toStaticHTML(e);
              if (Et(e)) return t.toStaticHTML(e.outerHTML);
            }
            return e;
          }
          if (
            (Le || ut(d),
            (n.removed = []),
            "string" == typeof e && (Ve = !1),
            Ve)
          ) {
            if (e.nodeName) {
              var p = ve(e.nodeName);
              if (!Ne[p] || Re[p])
                throw L(
                  "root node is forbidden and cannot be sanitized in-place",
                );
            }
          } else if (e instanceof c)
            (1 ===
              (i = (o = gt("\x3c!----\x3e")).ownerDocument.importNode(e, !0))
                .nodeType &&
              "BODY" === i.nodeName) ||
            "HTML" === i.nodeName
              ? (o = i)
              : o.appendChild(i);
          else {
            if (!De && !_e && !je && -1 === e.indexOf("<"))
              return T && He ? T.createHTML(e) : e;
            if (!(o = gt(e))) return De ? null : He ? S : "";
          }
          o && Pe && vt(o.firstChild);
          for (var m = yt(Ve ? e : o); (l = m.nextNode()); )
            (3 === l.nodeType && l === s) ||
              xt(l) ||
              (l.content instanceof a && Ct(l.content), St(l), (s = l));
          if (((s = null), Ve)) return e;
          if (De) {
            if (Fe)
              for (f = le.call(o.ownerDocument); o.firstChild; )
                f.appendChild(o.firstChild);
            else f = o;
            return Se.shadowroot && (f = fe.call(r, f, !0)), f;
          }
          var v = je ? o.outerHTML : o.innerHTML;
          return (
            je &&
              Ne["!doctype"] &&
              o.ownerDocument &&
              o.ownerDocument.doctype &&
              o.ownerDocument.doctype.name &&
              j(oe, o.ownerDocument.doctype.name) &&
              (v = "<!DOCTYPE " + o.ownerDocument.doctype.name + ">\n" + v),
            _e && ((v = A(v, he, " ")), (v = A(v, ge, " "))),
            T && He ? T.createHTML(v) : v
          );
        }),
        (n.setConfig = function (e) {
          ut(e), (Le = !0);
        }),
        (n.clearConfig = function () {
          (at = null), (Le = !1);
        }),
        (n.isValidAttribute = function (e, t, n) {
          at || ut({});
          var r = ve(e),
            o = ve(t);
          return Nt(r, o, n);
        }),
        (n.addHook = function (e, t) {
          "function" == typeof t && ((pe[e] = pe[e] || []), R(pe[e], t));
        }),
        (n.removeHook = function (e) {
          if (pe[e]) return O(pe[e]);
        }),
        (n.removeHooks = function (e) {
          pe[e] && (pe[e] = []);
        }),
        (n.removeAllHooks = function () {
          pe = {};
        }),
        n
      );
    })(),
    ue =
      "undefined" != typeof globalThis
        ? globalThis
        : "undefined" != typeof window
          ? window
          : "undefined" != typeof global
            ? global
            : "undefined" != typeof self
              ? self
              : {};
  function le(e) {
    return e &&
      e.__esModule &&
      Object.prototype.hasOwnProperty.call(e, "default")
      ? e.default
      : e;
  }
  var se = { exports: {} },
    fe = function (e) {
      return e && e.Math == Math && e;
    },
    de =
      fe("object" == typeof globalThis && globalThis) ||
      fe("object" == typeof window && window) ||
      fe("object" == typeof self && self) ||
      fe("object" == typeof ue && ue) ||
      (function () {
        return this;
      })() ||
      Function("return this")(),
    pe = function (e) {
      try {
        return !!e();
      } catch (e) {
        return !0;
      }
    },
    me = !pe(function () {
      var e = function () {}.bind();
      return "function" != typeof e || e.hasOwnProperty("prototype");
    }),
    ve = me,
    he = Function.prototype,
    ge = he.apply,
    ye = he.call,
    be =
      ("object" == typeof Reflect && Reflect.apply) ||
      (ve
        ? ye.bind(ge)
        : function () {
            return ye.apply(ge, arguments);
          }),
    Ee = me,
    we = Function.prototype,
    xe = we.bind,
    Ne = we.call,
    Te = Ee && xe.bind(Ne, Ne),
    Se = Ee
      ? function (e) {
          return e && Te(e);
        }
      : function (e) {
          return (
            e &&
            function () {
              return Ne.apply(e, arguments);
            }
          );
        },
    Ce = function (e) {
      return "function" == typeof e;
    },
    Oe = {},
    Re = !pe(function () {
      return (
        7 !=
        Object.defineProperty({}, 1, {
          get: function () {
            return 7;
          },
        })[1]
      );
    }),
    ke = me,
    Ie = Function.prototype.call,
    Ae = ke
      ? Ie.bind(Ie)
      : function () {
          return Ie.apply(Ie, arguments);
        },
    Me = {},
    _e = {}.propertyIsEnumerable,
    je = Object.getOwnPropertyDescriptor,
    Le = je && !_e.call({ 1: 2 }, 1);
  Me.f = Le
    ? function (e) {
        var t = je(this, e);
        return !!t && t.enumerable;
      }
    : _e;
  var Pe,
    De,
    Fe = function (e, t) {
      return {
        enumerable: !(1 & e),
        configurable: !(2 & e),
        writable: !(4 & e),
        value: t,
      };
    },
    He = Se,
    Be = He({}.toString),
    Ue = He("".slice),
    ze = function (e) {
      return Ue(Be(e), 8, -1);
    },
    Ge = pe,
    Ve = ze,
    Ye = Object,
    We = Se("".split),
    Xe = Ge(function () {
      return !Ye("z").propertyIsEnumerable(0);
    })
      ? function (e) {
          return "String" == Ve(e) ? We(e, "") : Ye(e);
        }
      : Ye,
    qe = function (e) {
      return null == e;
    },
    $e = qe,
    Ke = TypeError,
    Je = function (e) {
      if ($e(e)) throw Ke("Can't call method on " + e);
      return e;
    },
    Qe = Xe,
    Ze = Je,
    et = function (e) {
      return Qe(Ze(e));
    },
    tt = Ce,
    nt = "object" == typeof document && document.all,
    rt =
      void 0 === nt && void 0 !== nt
        ? function (e) {
            return "object" == typeof e ? null !== e : tt(e) || e === nt;
          }
        : function (e) {
            return "object" == typeof e ? null !== e : tt(e);
          },
    ot = {},
    at = ot,
    it = de,
    ct = Ce,
    ut = function (e) {
      return ct(e) ? e : void 0;
    },
    lt = function (e, t) {
      return arguments.length < 2
        ? ut(at[e]) || ut(it[e])
        : (at[e] && at[e][t]) || (it[e] && it[e][t]);
    },
    st = Se({}.isPrototypeOf),
    ft = lt("navigator", "userAgent") || "",
    dt = de,
    pt = ft,
    mt = dt.process,
    vt = dt.Deno,
    ht = (mt && mt.versions) || (vt && vt.version),
    gt = ht && ht.v8;
  gt && (De = (Pe = gt.split("."))[0] > 0 && Pe[0] < 4 ? 1 : +(Pe[0] + Pe[1])),
    !De &&
      pt &&
      (!(Pe = pt.match(/Edge\/(\d+)/)) || Pe[1] >= 74) &&
      (Pe = pt.match(/Chrome\/(\d+)/)) &&
      (De = +Pe[1]);
  var yt = De,
    bt = yt,
    Et = pe,
    wt =
      !!Object.getOwnPropertySymbols &&
      !Et(function () {
        var e = Symbol();
        return (
          !String(e) ||
          !(Object(e) instanceof Symbol) ||
          (!Symbol.sham && bt && bt < 41)
        );
      }),
    xt = wt && !Symbol.sham && "symbol" == typeof Symbol.iterator,
    Nt = lt,
    Tt = Ce,
    St = st,
    Ct = Object,
    Ot = xt
      ? function (e) {
          return "symbol" == typeof e;
        }
      : function (e) {
          var t = Nt("Symbol");
          return Tt(t) && St(t.prototype, Ct(e));
        },
    Rt = String,
    kt = function (e) {
      try {
        return Rt(e);
      } catch (e) {
        return "Object";
      }
    },
    It = Ce,
    At = kt,
    Mt = TypeError,
    _t = function (e) {
      if (It(e)) return e;
      throw Mt(At(e) + " is not a function");
    },
    jt = _t,
    Lt = qe,
    Pt = function (e, t) {
      var n = e[t];
      return Lt(n) ? void 0 : jt(n);
    },
    Dt = Ae,
    Ft = Ce,
    Ht = rt,
    Bt = TypeError,
    Ut = { exports: {} },
    zt = de,
    Gt = Object.defineProperty,
    Vt = function (e, t) {
      try {
        Gt(zt, e, { value: t, configurable: !0, writable: !0 });
      } catch (n) {
        zt[e] = t;
      }
      return t;
    },
    Yt = "__core-js_shared__",
    Wt = de[Yt] || Vt(Yt, {}),
    Xt = Wt;
  (Ut.exports = function (e, t) {
    return Xt[e] || (Xt[e] = void 0 !== t ? t : {});
  })("versions", []).push({
    version: "3.25.0",
    mode: "pure",
    copyright: "© 2014-2022 Denis Pushkarev (zloirock.ru)",
    license: "https://github.com/zloirock/core-js/blob/v3.25.0/LICENSE",
    source: "https://github.com/zloirock/core-js",
  });
  var qt = Je,
    $t = Object,
    Kt = function (e) {
      return $t(qt(e));
    },
    Jt = Kt,
    Qt = Se({}.hasOwnProperty),
    Zt =
      Object.hasOwn ||
      function (e, t) {
        return Qt(Jt(e), t);
      },
    en = Se,
    tn = 0,
    nn = Math.random(),
    rn = en((1).toString),
    on = function (e) {
      return "Symbol(" + (void 0 === e ? "" : e) + ")_" + rn(++tn + nn, 36);
    },
    an = de,
    cn = Ut.exports,
    un = Zt,
    ln = on,
    sn = wt,
    fn = xt,
    dn = cn("wks"),
    pn = an.Symbol,
    mn = pn && pn.for,
    vn = fn ? pn : (pn && pn.withoutSetter) || ln,
    hn = function (e) {
      if (!un(dn, e) || (!sn && "string" != typeof dn[e])) {
        var t = "Symbol." + e;
        sn && un(pn, e) ? (dn[e] = pn[e]) : (dn[e] = fn && mn ? mn(t) : vn(t));
      }
      return dn[e];
    },
    gn = Ae,
    yn = rt,
    bn = Ot,
    En = Pt,
    wn = function (e, t) {
      var n, r;
      if ("string" === t && Ft((n = e.toString)) && !Ht((r = Dt(n, e))))
        return r;
      if (Ft((n = e.valueOf)) && !Ht((r = Dt(n, e)))) return r;
      if ("string" !== t && Ft((n = e.toString)) && !Ht((r = Dt(n, e))))
        return r;
      throw Bt("Can't convert object to primitive value");
    },
    xn = TypeError,
    Nn = hn("toPrimitive"),
    Tn = function (e, t) {
      if (!yn(e) || bn(e)) return e;
      var n,
        r = En(e, Nn);
      if (r) {
        if (
          (void 0 === t && (t = "default"), (n = gn(r, e, t)), !yn(n) || bn(n))
        )
          return n;
        throw xn("Can't convert object to primitive value");
      }
      return void 0 === t && (t = "number"), wn(e, t);
    },
    Sn = Ot,
    Cn = function (e) {
      var t = Tn(e, "string");
      return Sn(t) ? t : t + "";
    },
    On = rt,
    Rn = de.document,
    kn = On(Rn) && On(Rn.createElement),
    In = function (e) {
      return kn ? Rn.createElement(e) : {};
    },
    An = In,
    Mn =
      !Re &&
      !pe(function () {
        return (
          7 !=
          Object.defineProperty(An("div"), "a", {
            get: function () {
              return 7;
            },
          }).a
        );
      }),
    _n = Re,
    jn = Ae,
    Ln = Me,
    Pn = Fe,
    Dn = et,
    Fn = Cn,
    Hn = Zt,
    Bn = Mn,
    Un = Object.getOwnPropertyDescriptor;
  Oe.f = _n
    ? Un
    : function (e, t) {
        if (((e = Dn(e)), (t = Fn(t)), Bn))
          try {
            return Un(e, t);
          } catch (e) {}
        if (Hn(e, t)) return Pn(!jn(Ln.f, e, t), e[t]);
      };
  var zn = pe,
    Gn = Ce,
    Vn = /#|\.prototype\./,
    Yn = function (e, t) {
      var n = Xn[Wn(e)];
      return n == $n || (n != qn && (Gn(t) ? zn(t) : !!t));
    },
    Wn = (Yn.normalize = function (e) {
      return String(e).replace(Vn, ".").toLowerCase();
    }),
    Xn = (Yn.data = {}),
    qn = (Yn.NATIVE = "N"),
    $n = (Yn.POLYFILL = "P"),
    Kn = Yn,
    Jn = _t,
    Qn = me,
    Zn = Se(Se.bind),
    er = function (e, t) {
      return (
        Jn(e),
        void 0 === t
          ? e
          : Qn
            ? Zn(e, t)
            : function () {
                return e.apply(t, arguments);
              }
      );
    },
    tr = {},
    nr =
      Re &&
      pe(function () {
        return (
          42 !=
          Object.defineProperty(function () {}, "prototype", {
            value: 42,
            writable: !1,
          }).prototype
        );
      }),
    rr = rt,
    or = String,
    ar = TypeError,
    ir = function (e) {
      if (rr(e)) return e;
      throw ar(or(e) + " is not an object");
    },
    cr = Re,
    ur = Mn,
    lr = nr,
    sr = ir,
    fr = Cn,
    dr = TypeError,
    pr = Object.defineProperty,
    mr = Object.getOwnPropertyDescriptor,
    vr = "enumerable",
    hr = "configurable",
    gr = "writable";
  tr.f = cr
    ? lr
      ? function (e, t, n) {
          if (
            (sr(e),
            (t = fr(t)),
            sr(n),
            "function" == typeof e &&
              "prototype" === t &&
              "value" in n &&
              gr in n &&
              !n.writable)
          ) {
            var r = mr(e, t);
            r &&
              r.writable &&
              ((e[t] = n.value),
              (n = {
                configurable: hr in n ? n.configurable : r.configurable,
                enumerable: vr in n ? n.enumerable : r.enumerable,
                writable: !1,
              }));
          }
          return pr(e, t, n);
        }
      : pr
    : function (e, t, n) {
        if ((sr(e), (t = fr(t)), sr(n), ur))
          try {
            return pr(e, t, n);
          } catch (e) {}
        if ("get" in n || "set" in n) throw dr("Accessors not supported");
        return "value" in n && (e[t] = n.value), e;
      };
  var yr = tr,
    br = Fe,
    Er = Re
      ? function (e, t, n) {
          return yr.f(e, t, br(1, n));
        }
      : function (e, t, n) {
          return (e[t] = n), e;
        },
    wr = de,
    xr = be,
    Nr = Se,
    Tr = Ce,
    Sr = Oe.f,
    Cr = Kn,
    Or = ot,
    Rr = er,
    kr = Er,
    Ir = Zt,
    Ar = function (e) {
      var t = function (n, r, o) {
        if (this instanceof t) {
          switch (arguments.length) {
            case 0:
              return new e();
            case 1:
              return new e(n);
            case 2:
              return new e(n, r);
          }
          return new e(n, r, o);
        }
        return xr(e, this, arguments);
      };
      return (t.prototype = e.prototype), t;
    },
    Mr = function (e, t) {
      var n,
        r,
        o,
        a,
        i,
        c,
        u,
        l,
        s = e.target,
        f = e.global,
        d = e.stat,
        p = e.proto,
        m = f ? wr : d ? wr[s] : (wr[s] || {}).prototype,
        v = f ? Or : Or[s] || kr(Or, s, {})[s],
        h = v.prototype;
      for (o in t)
        (n = !Cr(f ? o : s + (d ? "." : "#") + o, e.forced) && m && Ir(m, o)),
          (i = v[o]),
          n && (c = e.dontCallGetSet ? (l = Sr(m, o)) && l.value : m[o]),
          (a = n && c ? c : t[o]),
          (n && typeof i == typeof a) ||
            ((u =
              e.bind && n
                ? Rr(a, wr)
                : e.wrap && n
                  ? Ar(a)
                  : p && Tr(a)
                    ? Nr(a)
                    : a),
            (e.sham || (a && a.sham) || (i && i.sham)) && kr(u, "sham", !0),
            kr(v, o, u),
            p &&
              (Ir(Or, (r = s + "Prototype")) || kr(Or, r, {}),
              kr(Or[r], o, a),
              e.real && h && !h[o] && kr(h, o, a)));
    },
    _r = Ut.exports,
    jr = on,
    Lr = _r("keys"),
    Pr = function (e) {
      return Lr[e] || (Lr[e] = jr(e));
    },
    Dr = !pe(function () {
      function e() {}
      return (
        (e.prototype.constructor = null),
        Object.getPrototypeOf(new e()) !== e.prototype
      );
    }),
    Fr = Zt,
    Hr = Ce,
    Br = Kt,
    Ur = Dr,
    zr = Pr("IE_PROTO"),
    Gr = Object,
    Vr = Gr.prototype,
    Yr = Ur
      ? Gr.getPrototypeOf
      : function (e) {
          var t = Br(e);
          if (Fr(t, zr)) return t[zr];
          var n = t.constructor;
          return Hr(n) && t instanceof n
            ? n.prototype
            : t instanceof Gr
              ? Vr
              : null;
        },
    Wr = Ce,
    Xr = String,
    qr = TypeError,
    $r = Se,
    Kr = ir,
    Jr = function (e) {
      if ("object" == typeof e || Wr(e)) return e;
      throw qr("Can't set " + Xr(e) + " as a prototype");
    },
    Qr =
      Object.setPrototypeOf ||
      ("__proto__" in {}
        ? (function () {
            var e,
              t = !1,
              n = {};
            try {
              (e = $r(
                Object.getOwnPropertyDescriptor(Object.prototype, "__proto__")
                  .set,
              ))(n, []),
                (t = n instanceof Array);
            } catch (e) {}
            return function (n, r) {
              return Kr(n), Jr(r), t ? e(n, r) : (n.__proto__ = r), n;
            };
          })()
        : void 0),
    Zr = {},
    eo = Math.ceil,
    to = Math.floor,
    no =
      Math.trunc ||
      function (e) {
        var t = +e;
        return (t > 0 ? to : eo)(t);
      },
    ro = function (e) {
      var t = +e;
      return t != t || 0 === t ? 0 : no(t);
    },
    oo = ro,
    ao = Math.max,
    io = Math.min,
    co = function (e, t) {
      var n = oo(e);
      return n < 0 ? ao(n + t, 0) : io(n, t);
    },
    uo = ro,
    lo = Math.min,
    so = function (e) {
      return e > 0 ? lo(uo(e), 9007199254740991) : 0;
    },
    fo = function (e) {
      return so(e.length);
    },
    po = et,
    mo = co,
    vo = fo,
    ho = function (e) {
      return function (t, n, r) {
        var o,
          a = po(t),
          i = vo(a),
          c = mo(r, i);
        if (e && n != n) {
          for (; i > c; ) if ((o = a[c++]) != o) return !0;
        } else
          for (; i > c; c++)
            if ((e || c in a) && a[c] === n) return e || c || 0;
        return !e && -1;
      };
    },
    go = { includes: ho(!0), indexOf: ho(!1) },
    yo = {},
    bo = Zt,
    Eo = et,
    wo = go.indexOf,
    xo = yo,
    No = Se([].push),
    To = function (e, t) {
      var n,
        r = Eo(e),
        o = 0,
        a = [];
      for (n in r) !bo(xo, n) && bo(r, n) && No(a, n);
      for (; t.length > o; ) bo(r, (n = t[o++])) && (~wo(a, n) || No(a, n));
      return a;
    },
    So = [
      "constructor",
      "hasOwnProperty",
      "isPrototypeOf",
      "propertyIsEnumerable",
      "toLocaleString",
      "toString",
      "valueOf",
    ],
    Co = To,
    Oo = So.concat("length", "prototype");
  Zr.f =
    Object.getOwnPropertyNames ||
    function (e) {
      return Co(e, Oo);
    };
  var Ro = {};
  Ro.f = Object.getOwnPropertySymbols;
  var ko = lt,
    Io = Zr,
    Ao = Ro,
    Mo = ir,
    _o = Se([].concat),
    jo =
      ko("Reflect", "ownKeys") ||
      function (e) {
        var t = Io.f(Mo(e)),
          n = Ao.f;
        return n ? _o(t, n(e)) : t;
      },
    Lo = Zt,
    Po = jo,
    Do = Oe,
    Fo = tr,
    Ho = {},
    Bo = To,
    Uo = So,
    zo =
      Object.keys ||
      function (e) {
        return Bo(e, Uo);
      },
    Go = Re,
    Vo = nr,
    Yo = tr,
    Wo = ir,
    Xo = et,
    qo = zo;
  Ho.f =
    Go && !Vo
      ? Object.defineProperties
      : function (e, t) {
          Wo(e);
          for (var n, r = Xo(t), o = qo(t), a = o.length, i = 0; a > i; )
            Yo.f(e, (n = o[i++]), r[n]);
          return e;
        };
  var $o,
    Ko = lt("document", "documentElement"),
    Jo = ir,
    Qo = Ho,
    Zo = So,
    ea = yo,
    ta = Ko,
    na = In,
    ra = Pr("IE_PROTO"),
    oa = function () {},
    aa = function (e) {
      return "<script>" + e + "</" + "script>";
    },
    ia = function (e) {
      e.write(aa("")), e.close();
      var t = e.parentWindow.Object;
      return (e = null), t;
    },
    ca = function () {
      try {
        $o = new ActiveXObject("htmlfile");
      } catch (e) {}
      var e, t;
      ca =
        "undefined" != typeof document
          ? document.domain && $o
            ? ia($o)
            : (((t = na("iframe")).style.display = "none"),
              ta.appendChild(t),
              (t.src = String("javascript:")),
              (e = t.contentWindow.document).open(),
              e.write(aa("document.F=Object")),
              e.close(),
              e.F)
          : ia($o);
      for (var n = Zo.length; n--; ) delete ca.prototype[Zo[n]];
      return ca();
    };
  ea[ra] = !0;
  var ua =
      Object.create ||
      function (e, t) {
        var n;
        return (
          null !== e
            ? ((oa.prototype = Jo(e)),
              (n = new oa()),
              (oa.prototype = null),
              (n[ra] = e))
            : (n = ca()),
          void 0 === t ? n : Qo.f(n, t)
        );
      },
    la = Error,
    sa = Se("".replace),
    fa = String(la("zxcasd").stack),
    da = /\n\s*at [^:]*:[^\n]*/,
    pa = da.test(fa),
    ma = rt,
    va = Er,
    ha = {},
    ga = ha,
    ya = hn("iterator"),
    ba = Array.prototype,
    Ea = function (e) {
      return void 0 !== e && (ga.Array === e || ba[ya] === e);
    },
    wa = {};
  wa[hn("toStringTag")] = "z";
  var xa = "[object z]" === String(wa),
    Na = xa,
    Ta = Ce,
    Sa = ze,
    Ca = hn("toStringTag"),
    Oa = Object,
    Ra =
      "Arguments" ==
      Sa(
        (function () {
          return arguments;
        })(),
      ),
    ka = Na
      ? Sa
      : function (e) {
          var t, n, r;
          return void 0 === e
            ? "Undefined"
            : null === e
              ? "Null"
              : "string" ==
                  typeof (n = (function (e, t) {
                    try {
                      return e[t];
                    } catch (e) {}
                  })((t = Oa(e)), Ca))
                ? n
                : Ra
                  ? Sa(t)
                  : "Object" == (r = Sa(t)) && Ta(t.callee)
                    ? "Arguments"
                    : r;
        },
    Ia = ka,
    Aa = Pt,
    Ma = qe,
    _a = ha,
    ja = hn("iterator"),
    La = function (e) {
      if (!Ma(e)) return Aa(e, ja) || Aa(e, "@@iterator") || _a[Ia(e)];
    },
    Pa = Ae,
    Da = _t,
    Fa = ir,
    Ha = kt,
    Ba = La,
    Ua = TypeError,
    za = function (e, t) {
      var n = arguments.length < 2 ? Ba(e) : t;
      if (Da(n)) return Fa(Pa(n, e));
      throw Ua(Ha(e) + " is not iterable");
    },
    Ga = Ae,
    Va = ir,
    Ya = Pt,
    Wa = function (e, t, n) {
      var r, o;
      Va(e);
      try {
        if (!(r = Ya(e, "return"))) {
          if ("throw" === t) throw n;
          return n;
        }
        r = Ga(r, e);
      } catch (e) {
        (o = !0), (r = e);
      }
      if ("throw" === t) throw n;
      if (o) throw r;
      return Va(r), n;
    },
    Xa = er,
    qa = Ae,
    $a = ir,
    Ka = kt,
    Ja = Ea,
    Qa = fo,
    Za = st,
    ei = za,
    ti = La,
    ni = Wa,
    ri = TypeError,
    oi = function (e, t) {
      (this.stopped = e), (this.result = t);
    },
    ai = oi.prototype,
    ii = function (e, t, n) {
      var r,
        o,
        a,
        i,
        c,
        u,
        l,
        s = n && n.that,
        f = !(!n || !n.AS_ENTRIES),
        d = !(!n || !n.IS_RECORD),
        p = !(!n || !n.IS_ITERATOR),
        m = !(!n || !n.INTERRUPTED),
        v = Xa(t, s),
        h = function (e) {
          return r && ni(r, "normal", e), new oi(!0, e);
        },
        g = function (e) {
          return f
            ? ($a(e), m ? v(e[0], e[1], h) : v(e[0], e[1]))
            : m
              ? v(e, h)
              : v(e);
        };
      if (d) r = e.iterator;
      else if (p) r = e;
      else {
        if (!(o = ti(e))) throw ri(Ka(e) + " is not iterable");
        if (Ja(o)) {
          for (a = 0, i = Qa(e); i > a; a++)
            if ((c = g(e[a])) && Za(ai, c)) return c;
          return new oi(!1);
        }
        r = ei(e, o);
      }
      for (u = d ? e.next : r.next; !(l = qa(u, r)).done; ) {
        try {
          c = g(l.value);
        } catch (e) {
          ni(r, "throw", e);
        }
        if ("object" == typeof c && c && Za(ai, c)) return c;
      }
      return new oi(!1);
    },
    ci = ka,
    ui = String,
    li = function (e) {
      if ("Symbol" === ci(e))
        throw TypeError("Cannot convert a Symbol value to a string");
      return ui(e);
    },
    si = li,
    fi = Fe,
    di = !pe(function () {
      var e = Error("a");
      return (
        !("stack" in e) ||
        (Object.defineProperty(e, "stack", fi(1, 7)), 7 !== e.stack)
      );
    }),
    pi = Mr,
    mi = st,
    vi = Yr,
    hi = Qr,
    gi = function (e, t, n) {
      for (var r = Po(t), o = Fo.f, a = Do.f, i = 0; i < r.length; i++) {
        var c = r[i];
        Lo(e, c) || (n && Lo(n, c)) || o(e, c, a(t, c));
      }
    },
    yi = ua,
    bi = Er,
    Ei = Fe,
    wi = function (e, t) {
      if (pa && "string" == typeof e && !la.prepareStackTrace)
        for (; t--; ) e = sa(e, da, "");
      return e;
    },
    xi = function (e, t) {
      ma(t) && "cause" in t && va(e, "cause", t.cause);
    },
    Ni = ii,
    Ti = function (e, t) {
      return void 0 === e ? (arguments.length < 2 ? "" : t) : si(e);
    },
    Si = di,
    Ci = hn("toStringTag"),
    Oi = Error,
    Ri = [].push,
    ki = function (e, t) {
      var n,
        r = arguments.length > 2 ? arguments[2] : void 0,
        o = mi(Ii, this);
      hi
        ? (n = hi(Oi(), o ? vi(this) : Ii))
        : ((n = o ? this : yi(Ii)), bi(n, Ci, "Error")),
        void 0 !== t && bi(n, "message", Ti(t)),
        Si && bi(n, "stack", wi(n.stack, 1)),
        xi(n, r);
      var a = [];
      return Ni(e, Ri, { that: a }), bi(n, "errors", a), n;
    };
  hi ? hi(ki, Oi) : gi(ki, Oi, { name: !0 });
  var Ii = (ki.prototype = yi(Oi.prototype, {
    constructor: Ei(1, ki),
    message: Ei(1, ""),
    name: Ei(1, "AggregateError"),
  }));
  pi({ global: !0, constructor: !0, arity: 2 }, { AggregateError: ki });
  var Ai,
    Mi,
    _i,
    ji = Ce,
    Li = de.WeakMap,
    Pi = ji(Li) && /native code/.test(String(Li)),
    Di = de,
    Fi = Se,
    Hi = rt,
    Bi = Er,
    Ui = Zt,
    zi = Wt,
    Gi = Pr,
    Vi = yo,
    Yi = "Object already initialized",
    Wi = Di.TypeError,
    Xi = Di.WeakMap;
  if (Pi || zi.state) {
    var qi = zi.state || (zi.state = new Xi()),
      $i = Fi(qi.get),
      Ki = Fi(qi.has),
      Ji = Fi(qi.set);
    (Ai = function (e, t) {
      if (Ki(qi, e)) throw Wi(Yi);
      return (t.facade = e), Ji(qi, e, t), t;
    }),
      (Mi = function (e) {
        return $i(qi, e) || {};
      }),
      (_i = function (e) {
        return Ki(qi, e);
      });
  } else {
    var Qi = Gi("state");
    (Vi[Qi] = !0),
      (Ai = function (e, t) {
        if (Ui(e, Qi)) throw Wi(Yi);
        return (t.facade = e), Bi(e, Qi, t), t;
      }),
      (Mi = function (e) {
        return Ui(e, Qi) ? e[Qi] : {};
      }),
      (_i = function (e) {
        return Ui(e, Qi);
      });
  }
  var Zi,
    ec,
    tc,
    nc = {
      set: Ai,
      get: Mi,
      has: _i,
      enforce: function (e) {
        return _i(e) ? Mi(e) : Ai(e, {});
      },
      getterFor: function (e) {
        return function (t) {
          var n;
          if (!Hi(t) || (n = Mi(t)).type !== e)
            throw Wi("Incompatible receiver, " + e + " required");
          return n;
        };
      },
    },
    rc = Re,
    oc = Zt,
    ac = Function.prototype,
    ic = rc && Object.getOwnPropertyDescriptor,
    cc = oc(ac, "name"),
    uc = {
      EXISTS: cc,
      PROPER: cc && "something" === function () {}.name,
      CONFIGURABLE: cc && (!rc || (rc && ic(ac, "name").configurable)),
    },
    lc = Er,
    sc = function (e, t, n, r) {
      return r && r.enumerable ? (e[t] = n) : lc(e, t, n), e;
    },
    fc = pe,
    dc = Ce,
    pc = rt,
    mc = ua,
    vc = Yr,
    hc = sc,
    gc = hn("iterator"),
    yc = !1;
  [].keys &&
    ("next" in (tc = [].keys())
      ? (ec = vc(vc(tc))) !== Object.prototype && (Zi = ec)
      : (yc = !0));
  var bc =
    !pc(Zi) ||
    fc(function () {
      var e = {};
      return Zi[gc].call(e) !== e;
    });
  dc((Zi = bc ? {} : mc(Zi))[gc]) ||
    hc(Zi, gc, function () {
      return this;
    });
  var Ec = { IteratorPrototype: Zi, BUGGY_SAFARI_ITERATORS: yc },
    wc = ka,
    xc = xa
      ? {}.toString
      : function () {
          return "[object " + wc(this) + "]";
        },
    Nc = xa,
    Tc = tr.f,
    Sc = Er,
    Cc = Zt,
    Oc = xc,
    Rc = hn("toStringTag"),
    kc = function (e, t, n, r) {
      if (e) {
        var o = n ? e : e.prototype;
        Cc(o, Rc) || Tc(o, Rc, { configurable: !0, value: t }),
          r && !Nc && Sc(o, "toString", Oc);
      }
    },
    Ic = Ec.IteratorPrototype,
    Ac = ua,
    Mc = Fe,
    _c = kc,
    jc = ha,
    Lc = function () {
      return this;
    },
    Pc = Mr,
    Dc = Ae,
    Fc = function (e, t, n, r) {
      var o = t + " Iterator";
      return (
        (e.prototype = Ac(Ic, { next: Mc(+!r, n) })),
        _c(e, o, !1, !0),
        (jc[o] = Lc),
        e
      );
    },
    Hc = Yr,
    Bc = kc,
    Uc = sc,
    zc = ha,
    Gc = uc.PROPER,
    Vc = Ec.BUGGY_SAFARI_ITERATORS,
    Yc = hn("iterator"),
    Wc = "keys",
    Xc = "values",
    qc = "entries",
    $c = function () {
      return this;
    },
    Kc = function (e, t, n, r, o, a, i) {
      Fc(n, t, r);
      var c,
        u,
        l,
        s = function (e) {
          if (e === o && v) return v;
          if (!Vc && e in p) return p[e];
          switch (e) {
            case Wc:
            case Xc:
            case qc:
              return function () {
                return new n(this, e);
              };
          }
          return function () {
            return new n(this);
          };
        },
        f = t + " Iterator",
        d = !1,
        p = e.prototype,
        m = p[Yc] || p["@@iterator"] || (o && p[o]),
        v = (!Vc && m) || s(o),
        h = ("Array" == t && p.entries) || m;
      if (
        (h &&
          (c = Hc(h.call(new e()))) !== Object.prototype &&
          c.next &&
          (Bc(c, f, !0, !0), (zc[f] = $c)),
        Gc &&
          o == Xc &&
          m &&
          m.name !== Xc &&
          ((d = !0),
          (v = function () {
            return Dc(m, this);
          })),
        o)
      )
        if (((u = { values: s(Xc), keys: a ? v : s(Wc), entries: s(qc) }), i))
          for (l in u) (Vc || d || !(l in p)) && Uc(p, l, u[l]);
        else Pc({ target: t, proto: !0, forced: Vc || d }, u);
      return i && p[Yc] !== v && Uc(p, Yc, v, { name: o }), (zc[t] = v), u;
    },
    Jc = et,
    Qc = ha,
    Zc = nc;
  tr.f;
  var eu = Kc,
    tu = "Array Iterator",
    nu = Zc.set,
    ru = Zc.getterFor(tu);
  eu(
    Array,
    "Array",
    function (e, t) {
      nu(this, { type: tu, target: Jc(e), index: 0, kind: t });
    },
    function () {
      var e = ru(this),
        t = e.target,
        n = e.kind,
        r = e.index++;
      return !t || r >= t.length
        ? ((e.target = void 0), { value: void 0, done: !0 })
        : "keys" == n
          ? { value: r, done: !1 }
          : "values" == n
            ? { value: t[r], done: !1 }
            : { value: [r, t[r]], done: !1 };
    },
    "values",
  ),
    (Qc.Arguments = Qc.Array);
  var ou = "process" == ze(de.process),
    au = lt,
    iu = tr,
    cu = Re,
    uu = hn("species"),
    lu = st,
    su = TypeError,
    fu = Ce,
    du = Wt,
    pu = Se(Function.toString);
  fu(du.inspectSource) ||
    (du.inspectSource = function (e) {
      return pu(e);
    });
  var mu = du.inspectSource,
    vu = Se,
    hu = pe,
    gu = Ce,
    yu = ka,
    bu = mu,
    Eu = function () {},
    wu = [],
    xu = lt("Reflect", "construct"),
    Nu = /^\s*(?:class|function)\b/,
    Tu = vu(Nu.exec),
    Su = !Nu.exec(Eu),
    Cu = function (e) {
      if (!gu(e)) return !1;
      try {
        return xu(Eu, wu, e), !0;
      } catch (e) {
        return !1;
      }
    },
    Ou = function (e) {
      if (!gu(e)) return !1;
      switch (yu(e)) {
        case "AsyncFunction":
        case "GeneratorFunction":
        case "AsyncGeneratorFunction":
          return !1;
      }
      try {
        return Su || !!Tu(Nu, bu(e));
      } catch (e) {
        return !0;
      }
    };
  Ou.sham = !0;
  var Ru,
    ku,
    Iu,
    Au,
    Mu =
      !xu ||
      hu(function () {
        var e;
        return (
          Cu(Cu.call) ||
          !Cu(Object) ||
          !Cu(function () {
            e = !0;
          }) ||
          e
        );
      })
        ? Ou
        : Cu,
    _u = Mu,
    ju = kt,
    Lu = TypeError,
    Pu = function (e) {
      if (_u(e)) return e;
      throw Lu(ju(e) + " is not a constructor");
    },
    Du = ir,
    Fu = Pu,
    Hu = qe,
    Bu = hn("species"),
    Uu = function (e, t) {
      var n,
        r = Du(e).constructor;
      return void 0 === r || Hu((n = Du(r)[Bu])) ? t : Fu(n);
    },
    zu = Se([].slice),
    Gu = TypeError,
    Vu = /(?:ipad|iphone|ipod).*applewebkit/i.test(ft),
    Yu = de,
    Wu = be,
    Xu = er,
    qu = Ce,
    $u = Zt,
    Ku = pe,
    Ju = Ko,
    Qu = zu,
    Zu = In,
    el = function (e, t) {
      if (e < t) throw Gu("Not enough arguments");
      return e;
    },
    tl = Vu,
    nl = ou,
    rl = Yu.setImmediate,
    ol = Yu.clearImmediate,
    al = Yu.process,
    il = Yu.Dispatch,
    cl = Yu.Function,
    ul = Yu.MessageChannel,
    ll = Yu.String,
    sl = 0,
    fl = {},
    dl = "onreadystatechange";
  try {
    Ru = Yu.location;
  } catch (e) {}
  var pl = function (e) {
      if ($u(fl, e)) {
        var t = fl[e];
        delete fl[e], t();
      }
    },
    ml = function (e) {
      return function () {
        pl(e);
      };
    },
    vl = function (e) {
      pl(e.data);
    },
    hl = function (e) {
      Yu.postMessage(ll(e), Ru.protocol + "//" + Ru.host);
    };
  (rl && ol) ||
    ((rl = function (e) {
      el(arguments.length, 1);
      var t = qu(e) ? e : cl(e),
        n = Qu(arguments, 1);
      return (
        (fl[++sl] = function () {
          Wu(t, void 0, n);
        }),
        ku(sl),
        sl
      );
    }),
    (ol = function (e) {
      delete fl[e];
    }),
    nl
      ? (ku = function (e) {
          al.nextTick(ml(e));
        })
      : il && il.now
        ? (ku = function (e) {
            il.now(ml(e));
          })
        : ul && !tl
          ? ((Au = (Iu = new ul()).port2),
            (Iu.port1.onmessage = vl),
            (ku = Xu(Au.postMessage, Au)))
          : Yu.addEventListener &&
              qu(Yu.postMessage) &&
              !Yu.importScripts &&
              Ru &&
              "file:" !== Ru.protocol &&
              !Ku(hl)
            ? ((ku = hl), Yu.addEventListener("message", vl, !1))
            : (ku =
                dl in Zu("script")
                  ? function (e) {
                      Ju.appendChild(Zu("script")).onreadystatechange =
                        function () {
                          Ju.removeChild(this), pl(e);
                        };
                    }
                  : function (e) {
                      setTimeout(ml(e), 0);
                    }));
  var gl,
    yl,
    bl,
    El,
    wl,
    xl,
    Nl,
    Tl,
    Sl = { set: rl, clear: ol },
    Cl = de,
    Ol = /ipad|iphone|ipod/i.test(ft) && void 0 !== Cl.Pebble,
    Rl = /web0s(?!.*chrome)/i.test(ft),
    kl = de,
    Il = er,
    Al = Oe.f,
    Ml = Sl.set,
    _l = Vu,
    jl = Ol,
    Ll = Rl,
    Pl = ou,
    Dl = kl.MutationObserver || kl.WebKitMutationObserver,
    Fl = kl.document,
    Hl = kl.process,
    Bl = kl.Promise,
    Ul = Al(kl, "queueMicrotask"),
    zl = Ul && Ul.value;
  zl ||
    ((gl = function () {
      var e, t;
      for (Pl && (e = Hl.domain) && e.exit(); yl; ) {
        (t = yl.fn), (yl = yl.next);
        try {
          t();
        } catch (e) {
          throw (yl ? El() : (bl = void 0), e);
        }
      }
      (bl = void 0), e && e.enter();
    }),
    _l || Pl || Ll || !Dl || !Fl
      ? !jl && Bl && Bl.resolve
        ? (((Nl = Bl.resolve(void 0)).constructor = Bl),
          (Tl = Il(Nl.then, Nl)),
          (El = function () {
            Tl(gl);
          }))
        : Pl
          ? (El = function () {
              Hl.nextTick(gl);
            })
          : ((Ml = Il(Ml, kl)),
            (El = function () {
              Ml(gl);
            }))
      : ((wl = !0),
        (xl = Fl.createTextNode("")),
        new Dl(gl).observe(xl, { characterData: !0 }),
        (El = function () {
          xl.data = wl = !wl;
        })));
  var Gl =
      zl ||
      function (e) {
        var t = { fn: e, next: void 0 };
        bl && (bl.next = t), yl || ((yl = t), El()), (bl = t);
      },
    Vl = de,
    Yl = function (e) {
      try {
        return { error: !1, value: e() };
      } catch (e) {
        return { error: !0, value: e };
      }
    },
    Wl = function () {
      (this.head = null), (this.tail = null);
    };
  Wl.prototype = {
    add: function (e) {
      var t = { item: e, next: null };
      this.head ? (this.tail.next = t) : (this.head = t), (this.tail = t);
    },
    get: function () {
      var e = this.head;
      if (e)
        return (
          (this.head = e.next), this.tail === e && (this.tail = null), e.item
        );
    },
  };
  var Xl = Wl,
    ql = de.Promise,
    $l = "object" == typeof Deno && Deno && "object" == typeof Deno.version,
    Kl = !$l && !ou && "object" == typeof window && "object" == typeof document,
    Jl = de,
    Ql = ql,
    Zl = Ce,
    es = Kn,
    ts = mu,
    ns = hn,
    rs = Kl,
    os = $l,
    as = yt,
    is = Ql && Ql.prototype,
    cs = ns("species"),
    us = !1,
    ls = Zl(Jl.PromiseRejectionEvent),
    ss = es("Promise", function () {
      var e = ts(Ql),
        t = e !== String(Ql);
      if (!t && 66 === as) return !0;
      if (!is.catch || !is.finally) return !0;
      if (!as || as < 51 || !/native code/.test(e)) {
        var n = new Ql(function (e) {
            e(1);
          }),
          r = function (e) {
            e(
              function () {},
              function () {},
            );
          };
        if (
          (((n.constructor = {})[cs] = r),
          !(us = n.then(function () {}) instanceof r))
        )
          return !0;
      }
      return !t && (rs || os) && !ls;
    }),
    fs = { CONSTRUCTOR: ss, REJECTION_EVENT: ls, SUBCLASSING: us },
    ds = {},
    ps = _t,
    ms = TypeError,
    vs = function (e) {
      var t, n;
      (this.promise = new e(function (e, r) {
        if (void 0 !== t || void 0 !== n) throw ms("Bad Promise constructor");
        (t = e), (n = r);
      })),
        (this.resolve = ps(t)),
        (this.reject = ps(n));
    };
  ds.f = function (e) {
    return new vs(e);
  };
  var hs,
    gs,
    ys = Mr,
    bs = ou,
    Es = de,
    ws = Ae,
    xs = sc,
    Ns = kc,
    Ts = function (e) {
      var t = au(e),
        n = iu.f;
      cu &&
        t &&
        !t[uu] &&
        n(t, uu, {
          configurable: !0,
          get: function () {
            return this;
          },
        });
    },
    Ss = _t,
    Cs = Ce,
    Os = rt,
    Rs = function (e, t) {
      if (lu(t, e)) return e;
      throw su("Incorrect invocation");
    },
    ks = Uu,
    Is = Sl.set,
    As = Gl,
    Ms = function (e, t) {
      var n = Vl.console;
      n && n.error && (1 == arguments.length ? n.error(e) : n.error(e, t));
    },
    _s = Yl,
    js = Xl,
    Ls = nc,
    Ps = ql,
    Ds = ds,
    Fs = "Promise",
    Hs = fs.CONSTRUCTOR,
    Bs = fs.REJECTION_EVENT,
    Us = Ls.getterFor(Fs),
    zs = Ls.set,
    Gs = Ps && Ps.prototype,
    Vs = Ps,
    Ys = Gs,
    Ws = Es.TypeError,
    Xs = Es.document,
    qs = Es.process,
    $s = Ds.f,
    Ks = $s,
    Js = !!(Xs && Xs.createEvent && Es.dispatchEvent),
    Qs = "unhandledrejection",
    Zs = function (e) {
      var t;
      return !(!Os(e) || !Cs((t = e.then))) && t;
    },
    ef = function (e, t) {
      var n,
        r,
        o,
        a = t.value,
        i = 1 == t.state,
        c = i ? e.ok : e.fail,
        u = e.resolve,
        l = e.reject,
        s = e.domain;
      try {
        c
          ? (i || (2 === t.rejection && af(t), (t.rejection = 1)),
            !0 === c
              ? (n = a)
              : (s && s.enter(), (n = c(a)), s && (s.exit(), (o = !0))),
            n === e.promise
              ? l(Ws("Promise-chain cycle"))
              : (r = Zs(n))
                ? ws(r, n, u, l)
                : u(n))
          : l(a);
      } catch (e) {
        s && !o && s.exit(), l(e);
      }
    },
    tf = function (e, t) {
      e.notified ||
        ((e.notified = !0),
        As(function () {
          for (var n, r = e.reactions; (n = r.get()); ) ef(n, e);
          (e.notified = !1), t && !e.rejection && rf(e);
        }));
    },
    nf = function (e, t, n) {
      var r, o;
      Js
        ? (((r = Xs.createEvent("Event")).promise = t),
          (r.reason = n),
          r.initEvent(e, !1, !0),
          Es.dispatchEvent(r))
        : (r = { promise: t, reason: n }),
        !Bs && (o = Es["on" + e])
          ? o(r)
          : e === Qs && Ms("Unhandled promise rejection", n);
    },
    rf = function (e) {
      ws(Is, Es, function () {
        var t,
          n = e.facade,
          r = e.value;
        if (
          of(e) &&
          ((t = _s(function () {
            bs ? qs.emit("unhandledRejection", r, n) : nf(Qs, n, r);
          })),
          (e.rejection = bs || of(e) ? 2 : 1),
          t.error)
        )
          throw t.value;
      });
    },
    of = function (e) {
      return 1 !== e.rejection && !e.parent;
    },
    af = function (e) {
      ws(Is, Es, function () {
        var t = e.facade;
        bs
          ? qs.emit("rejectionHandled", t)
          : nf("rejectionhandled", t, e.value);
      });
    },
    cf = function (e, t, n) {
      return function (r) {
        e(t, r, n);
      };
    },
    uf = function (e, t, n) {
      e.done ||
        ((e.done = !0), n && (e = n), (e.value = t), (e.state = 2), tf(e, !0));
    },
    lf = function (e, t, n) {
      if (!e.done) {
        (e.done = !0), n && (e = n);
        try {
          if (e.facade === t) throw Ws("Promise can't be resolved itself");
          var r = Zs(t);
          r
            ? As(function () {
                var n = { done: !1 };
                try {
                  ws(r, t, cf(lf, n, e), cf(uf, n, e));
                } catch (t) {
                  uf(n, t, e);
                }
              })
            : ((e.value = t), (e.state = 1), tf(e, !1));
        } catch (t) {
          uf({ done: !1 }, t, e);
        }
      }
    };
  Hs &&
    ((Ys = (Vs = function (e) {
      Rs(this, Ys), Ss(e), ws(hs, this);
      var t = Us(this);
      try {
        e(cf(lf, t), cf(uf, t));
      } catch (e) {
        uf(t, e);
      }
    }).prototype),
    ((hs = function (e) {
      zs(this, {
        type: Fs,
        done: !1,
        notified: !1,
        parent: !1,
        reactions: new js(),
        rejection: !1,
        state: 0,
        value: void 0,
      });
    }).prototype = xs(Ys, "then", function (e, t) {
      var n = Us(this),
        r = $s(ks(this, Vs));
      return (
        (n.parent = !0),
        (r.ok = !Cs(e) || e),
        (r.fail = Cs(t) && t),
        (r.domain = bs ? qs.domain : void 0),
        0 == n.state
          ? n.reactions.add(r)
          : As(function () {
              ef(r, n);
            }),
        r.promise
      );
    })),
    (gs = function () {
      var e = new hs(),
        t = Us(e);
      (this.promise = e), (this.resolve = cf(lf, t)), (this.reject = cf(uf, t));
    }),
    (Ds.f = $s =
      function (e) {
        return e === Vs || undefined === e ? new gs(e) : Ks(e);
      })),
    ys({ global: !0, constructor: !0, wrap: !0, forced: Hs }, { Promise: Vs }),
    Ns(Vs, Fs, !1, !0),
    Ts(Fs);
  var sf = hn("iterator"),
    ff = !1;
  try {
    var df = 0,
      pf = {
        next: function () {
          return { done: !!df++ };
        },
        return: function () {
          ff = !0;
        },
      };
    (pf[sf] = function () {
      return this;
    }),
      Array.from(pf, function () {
        throw 2;
      });
  } catch (e) {}
  var mf = function (e, t) {
      if (!t && !ff) return !1;
      var n = !1;
      try {
        var r = {};
        (r[sf] = function () {
          return {
            next: function () {
              return { done: (n = !0) };
            },
          };
        }),
          e(r);
      } catch (e) {}
      return n;
    },
    vf = ql,
    hf =
      fs.CONSTRUCTOR ||
      !mf(function (e) {
        vf.all(e).then(void 0, function () {});
      }),
    gf = Ae,
    yf = _t,
    bf = ds,
    Ef = Yl,
    wf = ii;
  Mr(
    { target: "Promise", stat: !0, forced: hf },
    {
      all: function (e) {
        var t = this,
          n = bf.f(t),
          r = n.resolve,
          o = n.reject,
          a = Ef(function () {
            var n = yf(t.resolve),
              a = [],
              i = 0,
              c = 1;
            wf(e, function (e) {
              var u = i++,
                l = !1;
              c++,
                gf(n, t, e).then(function (e) {
                  l || ((l = !0), (a[u] = e), --c || r(a));
                }, o);
            }),
              --c || r(a);
          });
        return a.error && o(a.value), n.promise;
      },
    },
  );
  var xf = Mr,
    Nf = fs.CONSTRUCTOR;
  ql && ql.prototype,
    xf(
      { target: "Promise", proto: !0, forced: Nf, real: !0 },
      {
        catch: function (e) {
          return this.then(void 0, e);
        },
      },
    );
  var Tf = Ae,
    Sf = _t,
    Cf = ds,
    Of = Yl,
    Rf = ii;
  Mr(
    { target: "Promise", stat: !0, forced: hf },
    {
      race: function (e) {
        var t = this,
          n = Cf.f(t),
          r = n.reject,
          o = Of(function () {
            var o = Sf(t.resolve);
            Rf(e, function (e) {
              Tf(o, t, e).then(n.resolve, r);
            });
          });
        return o.error && r(o.value), n.promise;
      },
    },
  );
  var kf = Ae,
    If = ds;
  Mr(
    { target: "Promise", stat: !0, forced: fs.CONSTRUCTOR },
    {
      reject: function (e) {
        var t = If.f(this);
        return kf(t.reject, void 0, e), t.promise;
      },
    },
  );
  var Af = ir,
    Mf = rt,
    _f = ds,
    jf = function (e, t) {
      if ((Af(e), Mf(t) && t.constructor === e)) return t;
      var n = _f.f(e);
      return (0, n.resolve)(t), n.promise;
    },
    Lf = Mr,
    Pf = ql,
    Df = fs.CONSTRUCTOR,
    Ff = jf,
    Hf = lt("Promise"),
    Bf = !Df;
  Lf(
    { target: "Promise", stat: !0, forced: true },
    {
      resolve: function (e) {
        return Ff(Bf && this === Hf ? Pf : this, e);
      },
    },
  );
  var Uf = Ae,
    zf = _t,
    Gf = ds,
    Vf = Yl,
    Yf = ii;
  Mr(
    { target: "Promise", stat: !0 },
    {
      allSettled: function (e) {
        var t = this,
          n = Gf.f(t),
          r = n.resolve,
          o = n.reject,
          a = Vf(function () {
            var n = zf(t.resolve),
              o = [],
              a = 0,
              i = 1;
            Yf(e, function (e) {
              var c = a++,
                u = !1;
              i++,
                Uf(n, t, e).then(
                  function (e) {
                    u ||
                      ((u = !0),
                      (o[c] = { status: "fulfilled", value: e }),
                      --i || r(o));
                  },
                  function (e) {
                    u ||
                      ((u = !0),
                      (o[c] = { status: "rejected", reason: e }),
                      --i || r(o));
                  },
                );
            }),
              --i || r(o);
          });
        return a.error && o(a.value), n.promise;
      },
    },
  );
  var Wf = Ae,
    Xf = _t,
    qf = lt,
    $f = ds,
    Kf = Yl,
    Jf = ii,
    Qf = "No one promise resolved";
  Mr(
    { target: "Promise", stat: !0 },
    {
      any: function (e) {
        var t = this,
          n = qf("AggregateError"),
          r = $f.f(t),
          o = r.resolve,
          a = r.reject,
          i = Kf(function () {
            var r = Xf(t.resolve),
              i = [],
              c = 0,
              u = 1,
              l = !1;
            Jf(e, function (e) {
              var s = c++,
                f = !1;
              u++,
                Wf(r, t, e).then(
                  function (e) {
                    f || l || ((l = !0), o(e));
                  },
                  function (e) {
                    f || l || ((f = !0), (i[s] = e), --u || a(new n(i, Qf)));
                  },
                );
            }),
              --u || a(new n(i, Qf));
          });
        return i.error && a(i.value), r.promise;
      },
    },
  );
  var Zf = Mr,
    ed = ql,
    td = pe,
    nd = lt,
    rd = Ce,
    od = Uu,
    ad = jf,
    id = ed && ed.prototype;
  Zf(
    {
      target: "Promise",
      proto: !0,
      real: !0,
      forced:
        !!ed &&
        td(function () {
          id.finally.call({ then: function () {} }, function () {});
        }),
    },
    {
      finally: function (e) {
        var t = od(this, nd("Promise")),
          n = rd(e);
        return this.then(
          n
            ? function (n) {
                return ad(t, e()).then(function () {
                  return n;
                });
              }
            : e,
          n
            ? function (n) {
                return ad(t, e()).then(function () {
                  throw n;
                });
              }
            : e,
        );
      },
    },
  );
  var cd = Se,
    ud = ro,
    ld = li,
    sd = Je,
    fd = cd("".charAt),
    dd = cd("".charCodeAt),
    pd = cd("".slice),
    md = function (e) {
      return function (t, n) {
        var r,
          o,
          a = ld(sd(t)),
          i = ud(n),
          c = a.length;
        return i < 0 || i >= c
          ? e
            ? ""
            : void 0
          : (r = dd(a, i)) < 55296 ||
              r > 56319 ||
              i + 1 === c ||
              (o = dd(a, i + 1)) < 56320 ||
              o > 57343
            ? e
              ? fd(a, i)
              : r
            : e
              ? pd(a, i, i + 2)
              : o - 56320 + ((r - 55296) << 10) + 65536;
      };
    },
    vd = { codeAt: md(!1), charAt: md(!0) }.charAt,
    hd = li,
    gd = nc,
    yd = Kc,
    bd = "String Iterator",
    Ed = gd.set,
    wd = gd.getterFor(bd);
  yd(
    String,
    "String",
    function (e) {
      Ed(this, { type: bd, string: hd(e), index: 0 });
    },
    function () {
      var e,
        t = wd(this),
        n = t.string,
        r = t.index;
      return r >= n.length
        ? { value: void 0, done: !0 }
        : ((e = vd(n, r)), (t.index += e.length), { value: e, done: !1 });
    },
  );
  var xd = ot.Promise,
    Nd = {
      CSSRuleList: 0,
      CSSStyleDeclaration: 0,
      CSSValueList: 0,
      ClientRectList: 0,
      DOMRectList: 0,
      DOMStringList: 0,
      DOMTokenList: 1,
      DataTransferItemList: 0,
      FileList: 0,
      HTMLAllCollection: 0,
      HTMLCollection: 0,
      HTMLFormElement: 0,
      HTMLSelectElement: 0,
      MediaList: 0,
      MimeTypeArray: 0,
      NamedNodeMap: 0,
      NodeList: 1,
      PaintRequestList: 0,
      Plugin: 0,
      PluginArray: 0,
      SVGLengthList: 0,
      SVGNumberList: 0,
      SVGPathSegList: 0,
      SVGPointList: 0,
      SVGStringList: 0,
      SVGTransformList: 0,
      SourceBufferList: 0,
      StyleSheetList: 0,
      TextTrackCueList: 0,
      TextTrackList: 0,
      TouchList: 0,
    },
    Td = de,
    Sd = ka,
    Cd = Er,
    Od = ha,
    Rd = hn("toStringTag");
  for (var kd in Nd) {
    var Id = Td[kd],
      Ad = Id && Id.prototype;
    Ad && Sd(Ad) !== Rd && Cd(Ad, Rd, kd), (Od[kd] = Od.Array);
  }
  var Md = xd;
  !(function (e) {
    e.exports = Md;
  })(se);
  var _d = le(se.exports);
  function jd(e, t) {
    return new _d(function (n, r) {
      var o = document.createElement("script");
      (o.async = !0), (o.crossOrigin = "anonymous");
      var a = function () {
        o.parentNode && o.parentNode.removeChild(o),
          t && window[t] && delete window[t];
      };
      (o.onload = function () {
        n(window[t]), a();
      }),
        (o.onerror = function () {
          r(new Error("Failed to import: ".concat(e))), a();
        }),
        (o.src = e),
        document.head.appendChild(o);
    });
  }
  function Ld(e, t, n, r) {
    var a = o.default.lazy(function () {
      return jd(e, t)
        .then(function (e) {
          if (!e.default)
            throw new Error(
              "Failed to import ".concat(t, " component: no default export"),
            );
          return (a.WrappedComponent = e.default || e), n && n(), e;
        })
        .catch(function (e) {
          return (
            r && r(e),
            {
              default: function () {
                return o.default.createElement(o.default.Fragment, null);
              },
            }
          );
        });
    });
    return a;
  }
  function Pd(e) {
    var t =
        arguments.length > 1 && void 0 !== arguments[1]
          ? arguments[1]
          : document.body,
      n = document.createElement("div");
    t.appendChild(n);
    var r = o.default.cloneElement(e, {
      onUnmount: function () {
        a.default.unmountComponentAtNode(n), t.removeChild(n);
      },
    });
    return a.default.render(r, n), n;
  }
  function Dd(e) {
    var n =
        arguments.length > 1 && void 0 !== arguments[1]
          ? arguments[1]
          : "click",
      r = t.useRef();
    return (
      t.useEffect(
        function () {
          var t = function (t) {
            var n = r.current;
            n && !n.contains(t.target) && e && e(t);
          };
          return (
            document.addEventListener(n, t),
            function () {
              document.removeEventListener(n, t);
            }
          );
        },
        [n, e],
      ),
      r
    );
  }
  function Fd(e) {
    var n = t.useRef(null);
    return (
      t.useEffect(
        function () {
          e &&
            ("function" == typeof e ? e(n.current) : (e.current = n.current));
        },
        [e],
      ),
      n
    );
  }
  var Hd = function (e) {
      return e && e.Math == Math && e;
    },
    Bd =
      Hd("object" == typeof globalThis && globalThis) ||
      Hd("object" == typeof window && window) ||
      Hd("object" == typeof self && self) ||
      Hd("object" == typeof ue && ue) ||
      (function () {
        return this;
      })() ||
      Function("return this")(),
    Ud = { exports: {} },
    zd = Bd,
    Gd = Object.defineProperty,
    Vd = function (e, t) {
      try {
        Gd(zd, e, { value: t, configurable: !0, writable: !0 });
      } catch (n) {
        zd[e] = t;
      }
      return t;
    },
    Yd = Vd,
    Wd = "__core-js_shared__",
    Xd = Bd[Wd] || Yd(Wd, {}),
    qd = Xd;
  (Ud.exports = function (e, t) {
    return qd[e] || (qd[e] = void 0 !== t ? t : {});
  })("versions", []).push({
    version: "3.25.0",
    mode: "global",
    copyright: "© 2014-2022 Denis Pushkarev (zloirock.ru)",
    license: "https://github.com/zloirock/core-js/blob/v3.25.0/LICENSE",
    source: "https://github.com/zloirock/core-js",
  });
  var $d,
    Kd,
    Jd = function (e) {
      try {
        return !!e();
      } catch (e) {
        return !0;
      }
    },
    Qd = !Jd(function () {
      var e = function () {}.bind();
      return "function" != typeof e || e.hasOwnProperty("prototype");
    }),
    Zd = Qd,
    ep = Function.prototype,
    tp = ep.bind,
    np = ep.call,
    rp = Zd && tp.bind(np, np),
    op = Zd
      ? function (e) {
          return e && rp(e);
        }
      : function (e) {
          return (
            e &&
            function () {
              return np.apply(e, arguments);
            }
          );
        },
    ap = function (e) {
      return null == e;
    },
    ip = ap,
    cp = TypeError,
    up = function (e) {
      if (ip(e)) throw cp("Can't call method on " + e);
      return e;
    },
    lp = up,
    sp = Object,
    fp = function (e) {
      return sp(lp(e));
    },
    dp = fp,
    pp = op({}.hasOwnProperty),
    mp =
      Object.hasOwn ||
      function (e, t) {
        return pp(dp(e), t);
      },
    vp = op,
    hp = 0,
    gp = Math.random(),
    yp = vp((1).toString),
    bp = function (e) {
      return "Symbol(" + (void 0 === e ? "" : e) + ")_" + yp(++hp + gp, 36);
    },
    Ep = function (e) {
      return "function" == typeof e;
    },
    wp = Bd,
    xp = Ep,
    Np = function (e) {
      return xp(e) ? e : void 0;
    },
    Tp = function (e, t) {
      return arguments.length < 2 ? Np(wp[e]) : wp[e] && wp[e][t];
    },
    Sp = Tp("navigator", "userAgent") || "",
    Cp = Bd,
    Op = Sp,
    Rp = Cp.process,
    kp = Cp.Deno,
    Ip = (Rp && Rp.versions) || (kp && kp.version),
    Ap = Ip && Ip.v8;
  Ap && (Kd = ($d = Ap.split("."))[0] > 0 && $d[0] < 4 ? 1 : +($d[0] + $d[1])),
    !Kd &&
      Op &&
      (!($d = Op.match(/Edge\/(\d+)/)) || $d[1] >= 74) &&
      ($d = Op.match(/Chrome\/(\d+)/)) &&
      (Kd = +$d[1]);
  var Mp = Kd,
    _p = Mp,
    jp = Jd,
    Lp =
      !!Object.getOwnPropertySymbols &&
      !jp(function () {
        var e = Symbol();
        return (
          !String(e) ||
          !(Object(e) instanceof Symbol) ||
          (!Symbol.sham && _p && _p < 41)
        );
      }),
    Pp = Lp && !Symbol.sham && "symbol" == typeof Symbol.iterator,
    Dp = Bd,
    Fp = Ud.exports,
    Hp = mp,
    Bp = bp,
    Up = Lp,
    zp = Pp,
    Gp = Fp("wks"),
    Vp = Dp.Symbol,
    Yp = Vp && Vp.for,
    Wp = zp ? Vp : (Vp && Vp.withoutSetter) || Bp,
    Xp = function (e) {
      if (!Hp(Gp, e) || (!Up && "string" != typeof Gp[e])) {
        var t = "Symbol." + e;
        Up && Hp(Vp, e) ? (Gp[e] = Vp[e]) : (Gp[e] = zp && Yp ? Yp(t) : Wp(t));
      }
      return Gp[e];
    },
    qp = {};
  qp[Xp("toStringTag")] = "z";
  var $p = "[object z]" === String(qp),
    Kp = {},
    Jp = !Jd(function () {
      return (
        7 !=
        Object.defineProperty({}, 1, {
          get: function () {
            return 7;
          },
        })[1]
      );
    }),
    Qp = Ep,
    Zp = "object" == typeof document && document.all,
    em =
      void 0 === Zp && void 0 !== Zp
        ? function (e) {
            return "object" == typeof e ? null !== e : Qp(e) || e === Zp;
          }
        : function (e) {
            return "object" == typeof e ? null !== e : Qp(e);
          },
    tm = em,
    nm = Bd.document,
    rm = tm(nm) && tm(nm.createElement),
    om = function (e) {
      return rm ? nm.createElement(e) : {};
    },
    am = om,
    im =
      !Jp &&
      !Jd(function () {
        return (
          7 !=
          Object.defineProperty(am("div"), "a", {
            get: function () {
              return 7;
            },
          }).a
        );
      }),
    cm =
      Jp &&
      Jd(function () {
        return (
          42 !=
          Object.defineProperty(function () {}, "prototype", {
            value: 42,
            writable: !1,
          }).prototype
        );
      }),
    um = em,
    lm = String,
    sm = TypeError,
    fm = function (e) {
      if (um(e)) return e;
      throw sm(lm(e) + " is not an object");
    },
    dm = Qd,
    pm = Function.prototype.call,
    mm = dm
      ? pm.bind(pm)
      : function () {
          return pm.apply(pm, arguments);
        },
    vm = op({}.isPrototypeOf),
    hm = Tp,
    gm = Ep,
    ym = vm,
    bm = Object,
    Em = Pp
      ? function (e) {
          return "symbol" == typeof e;
        }
      : function (e) {
          var t = hm("Symbol");
          return gm(t) && ym(t.prototype, bm(e));
        },
    wm = String,
    xm = function (e) {
      try {
        return wm(e);
      } catch (e) {
        return "Object";
      }
    },
    Nm = Ep,
    Tm = xm,
    Sm = TypeError,
    Cm = function (e) {
      if (Nm(e)) return e;
      throw Sm(Tm(e) + " is not a function");
    },
    Om = Cm,
    Rm = ap,
    km = function (e, t) {
      var n = e[t];
      return Rm(n) ? void 0 : Om(n);
    },
    Im = mm,
    Am = Ep,
    Mm = em,
    _m = TypeError,
    jm = mm,
    Lm = em,
    Pm = Em,
    Dm = km,
    Fm = function (e, t) {
      var n, r;
      if ("string" === t && Am((n = e.toString)) && !Mm((r = Im(n, e))))
        return r;
      if (Am((n = e.valueOf)) && !Mm((r = Im(n, e)))) return r;
      if ("string" !== t && Am((n = e.toString)) && !Mm((r = Im(n, e))))
        return r;
      throw _m("Can't convert object to primitive value");
    },
    Hm = TypeError,
    Bm = Xp("toPrimitive"),
    Um = function (e, t) {
      if (!Lm(e) || Pm(e)) return e;
      var n,
        r = Dm(e, Bm);
      if (r) {
        if (
          (void 0 === t && (t = "default"), (n = jm(r, e, t)), !Lm(n) || Pm(n))
        )
          return n;
        throw Hm("Can't convert object to primitive value");
      }
      return void 0 === t && (t = "number"), Fm(e, t);
    },
    zm = Um,
    Gm = Em,
    Vm = function (e) {
      var t = zm(e, "string");
      return Gm(t) ? t : t + "";
    },
    Ym = Jp,
    Wm = im,
    Xm = cm,
    qm = fm,
    $m = Vm,
    Km = TypeError,
    Jm = Object.defineProperty,
    Qm = Object.getOwnPropertyDescriptor,
    Zm = "enumerable",
    ev = "configurable",
    tv = "writable";
  Kp.f = Ym
    ? Xm
      ? function (e, t, n) {
          if (
            (qm(e),
            (t = $m(t)),
            qm(n),
            "function" == typeof e &&
              "prototype" === t &&
              "value" in n &&
              tv in n &&
              !n.writable)
          ) {
            var r = Qm(e, t);
            r &&
              r.writable &&
              ((e[t] = n.value),
              (n = {
                configurable: ev in n ? n.configurable : r.configurable,
                enumerable: Zm in n ? n.enumerable : r.enumerable,
                writable: !1,
              }));
          }
          return Jm(e, t, n);
        }
      : Jm
    : function (e, t, n) {
        if ((qm(e), (t = $m(t)), qm(n), Wm))
          try {
            return Jm(e, t, n);
          } catch (e) {}
        if ("get" in n || "set" in n) throw Km("Accessors not supported");
        return "value" in n && (e[t] = n.value), e;
      };
  var nv = { exports: {} },
    rv = Jp,
    ov = mp,
    av = Function.prototype,
    iv = rv && Object.getOwnPropertyDescriptor,
    cv = ov(av, "name"),
    uv = {
      EXISTS: cv,
      PROPER: cv && "something" === function () {}.name,
      CONFIGURABLE: cv && (!rv || (rv && iv(av, "name").configurable)),
    },
    lv = Ep,
    sv = Xd,
    fv = op(Function.toString);
  lv(sv.inspectSource) ||
    (sv.inspectSource = function (e) {
      return fv(e);
    });
  var dv,
    pv,
    mv,
    vv = sv.inspectSource,
    hv = Ep,
    gv = Bd.WeakMap,
    yv = hv(gv) && /native code/.test(String(gv)),
    bv = function (e, t) {
      return {
        enumerable: !(1 & e),
        configurable: !(2 & e),
        writable: !(4 & e),
        value: t,
      };
    },
    Ev = Kp,
    wv = bv,
    xv = Jp
      ? function (e, t, n) {
          return Ev.f(e, t, wv(1, n));
        }
      : function (e, t, n) {
          return (e[t] = n), e;
        },
    Nv = Ud.exports,
    Tv = bp,
    Sv = Nv("keys"),
    Cv = function (e) {
      return Sv[e] || (Sv[e] = Tv(e));
    },
    Ov = {},
    Rv = yv,
    kv = Bd,
    Iv = op,
    Av = em,
    Mv = xv,
    _v = mp,
    jv = Xd,
    Lv = Cv,
    Pv = Ov,
    Dv = "Object already initialized",
    Fv = kv.TypeError,
    Hv = kv.WeakMap;
  if (Rv || jv.state) {
    var Bv = jv.state || (jv.state = new Hv()),
      Uv = Iv(Bv.get),
      zv = Iv(Bv.has),
      Gv = Iv(Bv.set);
    (dv = function (e, t) {
      if (zv(Bv, e)) throw Fv(Dv);
      return (t.facade = e), Gv(Bv, e, t), t;
    }),
      (pv = function (e) {
        return Uv(Bv, e) || {};
      }),
      (mv = function (e) {
        return zv(Bv, e);
      });
  } else {
    var Vv = Lv("state");
    (Pv[Vv] = !0),
      (dv = function (e, t) {
        if (_v(e, Vv)) throw Fv(Dv);
        return (t.facade = e), Mv(e, Vv, t), t;
      }),
      (pv = function (e) {
        return _v(e, Vv) ? e[Vv] : {};
      }),
      (mv = function (e) {
        return _v(e, Vv);
      });
  }
  var Yv = {
      set: dv,
      get: pv,
      has: mv,
      enforce: function (e) {
        return mv(e) ? pv(e) : dv(e, {});
      },
      getterFor: function (e) {
        return function (t) {
          var n;
          if (!Av(t) || (n = pv(t)).type !== e)
            throw Fv("Incompatible receiver, " + e + " required");
          return n;
        };
      },
    },
    Wv = Jd,
    Xv = Ep,
    qv = mp,
    $v = Jp,
    Kv = uv.CONFIGURABLE,
    Jv = vv,
    Qv = Yv.enforce,
    Zv = Yv.get,
    eh = Object.defineProperty,
    th =
      $v &&
      !Wv(function () {
        return 8 !== eh(function () {}, "length", { value: 8 }).length;
      }),
    nh = String(String).split("String"),
    rh = (nv.exports = function (e, t, n) {
      "Symbol(" === String(t).slice(0, 7) &&
        (t = "[" + String(t).replace(/^Symbol\(([^)]*)\)/, "$1") + "]"),
        n && n.getter && (t = "get " + t),
        n && n.setter && (t = "set " + t),
        (!qv(e, "name") || (Kv && e.name !== t)) &&
          ($v ? eh(e, "name", { value: t, configurable: !0 }) : (e.name = t)),
        th &&
          n &&
          qv(n, "arity") &&
          e.length !== n.arity &&
          eh(e, "length", { value: n.arity });
      try {
        n && qv(n, "constructor") && n.constructor
          ? $v && eh(e, "prototype", { writable: !1 })
          : e.prototype && (e.prototype = void 0);
      } catch (e) {}
      var r = Qv(e);
      return (
        qv(r, "source") || (r.source = nh.join("string" == typeof t ? t : "")),
        e
      );
    });
  Function.prototype.toString = rh(function () {
    return (Xv(this) && Zv(this).source) || Jv(this);
  }, "toString");
  var oh = Ep,
    ah = Kp,
    ih = nv.exports,
    ch = Vd,
    uh = function (e, t, n, r) {
      r || (r = {});
      var o = r.enumerable,
        a = void 0 !== r.name ? r.name : t;
      if ((oh(n) && ih(n, a, r), r.global)) o ? (e[t] = n) : ch(t, n);
      else {
        try {
          r.unsafe ? e[t] && (o = !0) : delete e[t];
        } catch (e) {}
        o
          ? (e[t] = n)
          : ah.f(e, t, {
              value: n,
              enumerable: !1,
              configurable: !r.nonConfigurable,
              writable: !r.nonWritable,
            });
      }
      return e;
    },
    lh = op,
    sh = lh({}.toString),
    fh = lh("".slice),
    dh = function (e) {
      return fh(sh(e), 8, -1);
    },
    ph = $p,
    mh = Ep,
    vh = dh,
    hh = Xp("toStringTag"),
    gh = Object,
    yh =
      "Arguments" ==
      vh(
        (function () {
          return arguments;
        })(),
      ),
    bh = ph
      ? vh
      : function (e) {
          var t, n, r;
          return void 0 === e
            ? "Undefined"
            : null === e
              ? "Null"
              : "string" ==
                  typeof (n = (function (e, t) {
                    try {
                      return e[t];
                    } catch (e) {}
                  })((t = gh(e)), hh))
                ? n
                : yh
                  ? vh(t)
                  : "Object" == (r = vh(t)) && mh(t.callee)
                    ? "Arguments"
                    : r;
        },
    Eh = bh,
    wh = $p
      ? {}.toString
      : function () {
          return "[object " + Eh(this) + "]";
        };
  $p || uh(Object.prototype, "toString", wh, { unsafe: !0 });
  var xh = om("span").classList,
    Nh = xh && xh.constructor && xh.constructor.prototype,
    Th = Nh === Object.prototype ? void 0 : Nh,
    Sh = Cm,
    Ch = Qd,
    Oh = op(op.bind),
    Rh = function (e, t) {
      return (
        Sh(e),
        void 0 === t
          ? e
          : Ch
            ? Oh(e, t)
            : function () {
                return e.apply(t, arguments);
              }
      );
    },
    kh = Jd,
    Ih = dh,
    Ah = Object,
    Mh = op("".split),
    _h = kh(function () {
      return !Ah("z").propertyIsEnumerable(0);
    })
      ? function (e) {
          return "String" == Ih(e) ? Mh(e, "") : Ah(e);
        }
      : Ah,
    jh = Math.ceil,
    Lh = Math.floor,
    Ph =
      Math.trunc ||
      function (e) {
        var t = +e;
        return (t > 0 ? Lh : jh)(t);
      },
    Dh = function (e) {
      var t = +e;
      return t != t || 0 === t ? 0 : Ph(t);
    },
    Fh = Dh,
    Hh = Math.min,
    Bh = function (e) {
      return e > 0 ? Hh(Fh(e), 9007199254740991) : 0;
    },
    Uh = Bh,
    zh = function (e) {
      return Uh(e.length);
    },
    Gh = dh,
    Vh =
      Array.isArray ||
      function (e) {
        return "Array" == Gh(e);
      },
    Yh = op,
    Wh = Jd,
    Xh = Ep,
    qh = bh,
    $h = vv,
    Kh = function () {},
    Jh = [],
    Qh = Tp("Reflect", "construct"),
    Zh = /^\s*(?:class|function)\b/,
    eg = Yh(Zh.exec),
    tg = !Zh.exec(Kh),
    ng = function (e) {
      if (!Xh(e)) return !1;
      try {
        return Qh(Kh, Jh, e), !0;
      } catch (e) {
        return !1;
      }
    },
    rg = function (e) {
      if (!Xh(e)) return !1;
      switch (qh(e)) {
        case "AsyncFunction":
        case "GeneratorFunction":
        case "AsyncGeneratorFunction":
          return !1;
      }
      try {
        return tg || !!eg(Zh, $h(e));
      } catch (e) {
        return !0;
      }
    };
  rg.sham = !0;
  var og =
      !Qh ||
      Wh(function () {
        var e;
        return (
          ng(ng.call) ||
          !ng(Object) ||
          !ng(function () {
            e = !0;
          }) ||
          e
        );
      })
        ? rg
        : ng,
    ag = Vh,
    ig = og,
    cg = em,
    ug = Xp("species"),
    lg = Array,
    sg = function (e) {
      var t;
      return (
        ag(e) &&
          ((t = e.constructor),
          ((ig(t) && (t === lg || ag(t.prototype))) ||
            (cg(t) && null === (t = t[ug]))) &&
            (t = void 0)),
        void 0 === t ? lg : t
      );
    },
    fg = Rh,
    dg = _h,
    pg = fp,
    mg = zh,
    vg = function (e, t) {
      return new (sg(e))(0 === t ? 0 : t);
    },
    hg = op([].push),
    gg = function (e) {
      var t = 1 == e,
        n = 2 == e,
        r = 3 == e,
        o = 4 == e,
        a = 6 == e,
        i = 7 == e,
        c = 5 == e || a;
      return function (u, l, s, f) {
        for (
          var d,
            p,
            m = pg(u),
            v = dg(m),
            h = fg(l, s),
            g = mg(v),
            y = 0,
            b = f || vg,
            E = t ? b(u, g) : n || i ? b(u, 0) : void 0;
          g > y;
          y++
        )
          if ((c || y in v) && ((p = h((d = v[y]), y, m)), e))
            if (t) E[y] = p;
            else if (p)
              switch (e) {
                case 3:
                  return !0;
                case 5:
                  return d;
                case 6:
                  return y;
                case 2:
                  hg(E, d);
              }
            else
              switch (e) {
                case 4:
                  return !1;
                case 7:
                  hg(E, d);
              }
        return a ? -1 : r || o ? o : E;
      };
    },
    yg = {
      forEach: gg(0),
      map: gg(1),
      filter: gg(2),
      some: gg(3),
      every: gg(4),
      find: gg(5),
      findIndex: gg(6),
      filterReject: gg(7),
    },
    bg = Jd,
    Eg = yg.forEach,
    wg = function (e, t) {
      var n = [][e];
      return (
        !!n &&
        bg(function () {
          n.call(
            null,
            t ||
              function () {
                return 1;
              },
            1,
          );
        })
      );
    },
    xg = wg("forEach")
      ? [].forEach
      : function (e) {
          return Eg(this, e, arguments.length > 1 ? arguments[1] : void 0);
        },
    Ng = Bd,
    Tg = {
      CSSRuleList: 0,
      CSSStyleDeclaration: 0,
      CSSValueList: 0,
      ClientRectList: 0,
      DOMRectList: 0,
      DOMStringList: 0,
      DOMTokenList: 1,
      DataTransferItemList: 0,
      FileList: 0,
      HTMLAllCollection: 0,
      HTMLCollection: 0,
      HTMLFormElement: 0,
      HTMLSelectElement: 0,
      MediaList: 0,
      MimeTypeArray: 0,
      NamedNodeMap: 0,
      NodeList: 1,
      PaintRequestList: 0,
      Plugin: 0,
      PluginArray: 0,
      SVGLengthList: 0,
      SVGNumberList: 0,
      SVGPathSegList: 0,
      SVGPointList: 0,
      SVGStringList: 0,
      SVGTransformList: 0,
      SourceBufferList: 0,
      StyleSheetList: 0,
      TextTrackCueList: 0,
      TextTrackList: 0,
      TouchList: 0,
    },
    Sg = Th,
    Cg = xg,
    Og = xv,
    Rg = function (e) {
      if (e && e.forEach !== Cg)
        try {
          Og(e, "forEach", Cg);
        } catch (t) {
          e.forEach = Cg;
        }
    };
  for (var kg in Tg) Tg[kg] && Rg(Ng[kg] && Ng[kg].prototype);
  Rg(Sg);
  var Ig = { exports: {} },
    Ag = Kt,
    Mg = zo;
  Mr(
    {
      target: "Object",
      stat: !0,
      forced: pe(function () {
        Mg(1);
      }),
    },
    {
      keys: function (e) {
        return Mg(Ag(e));
      },
    },
  );
  var _g = ot.Object.keys;
  !(function (e) {
    e.exports = _g;
  })(Ig);
  var jg = le(Ig.exports),
    Lg = { exports: {} },
    Pg = {},
    Dg = Cn,
    Fg = tr,
    Hg = Fe,
    Bg = function (e, t, n) {
      var r = Dg(t);
      r in e ? Fg.f(e, r, Hg(0, n)) : (e[r] = n);
    },
    Ug = co,
    zg = fo,
    Gg = Bg,
    Vg = Array,
    Yg = Math.max,
    Wg = ze,
    Xg = et,
    qg = Zr.f,
    $g = function (e, t, n) {
      for (
        var r = zg(e),
          o = Ug(t, r),
          a = Ug(void 0 === n ? r : n, r),
          i = Vg(Yg(a - o, 0)),
          c = 0;
        o < a;
        o++, c++
      )
        Gg(i, c, e[o]);
      return (i.length = c), i;
    },
    Kg =
      "object" == typeof window && window && Object.getOwnPropertyNames
        ? Object.getOwnPropertyNames(window)
        : [];
  Pg.f = function (e) {
    return Kg && "Window" == Wg(e)
      ? (function (e) {
          try {
            return qg(e);
          } catch (e) {
            return $g(Kg);
          }
        })(e)
      : qg(Xg(e));
  };
  var Jg = {},
    Qg = hn;
  Jg.f = Qg;
  var Zg = ot,
    ey = Zt,
    ty = Jg,
    ny = tr.f,
    ry = function (e) {
      var t = Zg.Symbol || (Zg.Symbol = {});
      ey(t, e) || ny(t, e, { value: ty.f(e) });
    },
    oy = Ae,
    ay = lt,
    iy = hn,
    cy = sc,
    uy = function () {
      var e = ay("Symbol"),
        t = e && e.prototype,
        n = t && t.valueOf,
        r = iy("toPrimitive");
      t &&
        !t[r] &&
        cy(
          t,
          r,
          function (e) {
            return oy(n, this);
          },
          { arity: 1 },
        );
    },
    ly = ze,
    sy =
      Array.isArray ||
      function (e) {
        return "Array" == ly(e);
      },
    fy = sy,
    dy = Mu,
    py = rt,
    my = hn("species"),
    vy = Array,
    hy = function (e) {
      var t;
      return (
        fy(e) &&
          ((t = e.constructor),
          ((dy(t) && (t === vy || fy(t.prototype))) ||
            (py(t) && null === (t = t[my]))) &&
            (t = void 0)),
        void 0 === t ? vy : t
      );
    },
    gy = function (e, t) {
      return new (hy(e))(0 === t ? 0 : t);
    },
    yy = er,
    by = Xe,
    Ey = Kt,
    wy = fo,
    xy = gy,
    Ny = Se([].push),
    Ty = function (e) {
      var t = 1 == e,
        n = 2 == e,
        r = 3 == e,
        o = 4 == e,
        a = 6 == e,
        i = 7 == e,
        c = 5 == e || a;
      return function (u, l, s, f) {
        for (
          var d,
            p,
            m = Ey(u),
            v = by(m),
            h = yy(l, s),
            g = wy(v),
            y = 0,
            b = f || xy,
            E = t ? b(u, g) : n || i ? b(u, 0) : void 0;
          g > y;
          y++
        )
          if ((c || y in v) && ((p = h((d = v[y]), y, m)), e))
            if (t) E[y] = p;
            else if (p)
              switch (e) {
                case 3:
                  return !0;
                case 5:
                  return d;
                case 6:
                  return y;
                case 2:
                  Ny(E, d);
              }
            else
              switch (e) {
                case 4:
                  return !1;
                case 7:
                  Ny(E, d);
              }
        return a ? -1 : r || o ? o : E;
      };
    },
    Sy = {
      forEach: Ty(0),
      map: Ty(1),
      filter: Ty(2),
      some: Ty(3),
      every: Ty(4),
      find: Ty(5),
      findIndex: Ty(6),
      filterReject: Ty(7),
    },
    Cy = Mr,
    Oy = de,
    Ry = Ae,
    ky = Se,
    Iy = Re,
    Ay = wt,
    My = pe,
    _y = Zt,
    jy = st,
    Ly = ir,
    Py = et,
    Dy = Cn,
    Fy = li,
    Hy = Fe,
    By = ua,
    Uy = zo,
    zy = Zr,
    Gy = Pg,
    Vy = Ro,
    Yy = Oe,
    Wy = tr,
    Xy = Ho,
    qy = Me,
    $y = sc,
    Ky = Ut.exports,
    Jy = yo,
    Qy = on,
    Zy = hn,
    eb = Jg,
    tb = ry,
    nb = uy,
    rb = kc,
    ob = nc,
    ab = Sy.forEach,
    ib = Pr("hidden"),
    cb = "Symbol",
    ub = ob.set,
    lb = ob.getterFor(cb),
    sb = Object.prototype,
    fb = Oy.Symbol,
    db = fb && fb.prototype,
    pb = Oy.TypeError,
    mb = Oy.QObject,
    vb = Yy.f,
    hb = Wy.f,
    gb = Gy.f,
    yb = qy.f,
    bb = ky([].push),
    Eb = Ky("symbols"),
    wb = Ky("op-symbols"),
    xb = Ky("wks"),
    Nb = !mb || !mb.prototype || !mb.prototype.findChild,
    Tb =
      Iy &&
      My(function () {
        return (
          7 !=
          By(
            hb({}, "a", {
              get: function () {
                return hb(this, "a", { value: 7 }).a;
              },
            }),
          ).a
        );
      })
        ? function (e, t, n) {
            var r = vb(sb, t);
            r && delete sb[t], hb(e, t, n), r && e !== sb && hb(sb, t, r);
          }
        : hb,
    Sb = function (e, t) {
      var n = (Eb[e] = By(db));
      return (
        ub(n, { type: cb, tag: e, description: t }),
        Iy || (n.description = t),
        n
      );
    },
    Cb = function (e, t, n) {
      e === sb && Cb(wb, t, n), Ly(e);
      var r = Dy(t);
      return (
        Ly(n),
        _y(Eb, r)
          ? (n.enumerable
              ? (_y(e, ib) && e[ib][r] && (e[ib][r] = !1),
                (n = By(n, { enumerable: Hy(0, !1) })))
              : (_y(e, ib) || hb(e, ib, Hy(1, {})), (e[ib][r] = !0)),
            Tb(e, r, n))
          : hb(e, r, n)
      );
    },
    Ob = function (e, t) {
      Ly(e);
      var n = Py(t),
        r = Uy(n).concat(Ab(n));
      return (
        ab(r, function (t) {
          (Iy && !Ry(Rb, n, t)) || Cb(e, t, n[t]);
        }),
        e
      );
    },
    Rb = function (e) {
      var t = Dy(e),
        n = Ry(yb, this, t);
      return (
        !(this === sb && _y(Eb, t) && !_y(wb, t)) &&
        (!(n || !_y(this, t) || !_y(Eb, t) || (_y(this, ib) && this[ib][t])) ||
          n)
      );
    },
    kb = function (e, t) {
      var n = Py(e),
        r = Dy(t);
      if (n !== sb || !_y(Eb, r) || _y(wb, r)) {
        var o = vb(n, r);
        return (
          !o || !_y(Eb, r) || (_y(n, ib) && n[ib][r]) || (o.enumerable = !0), o
        );
      }
    },
    Ib = function (e) {
      var t = gb(Py(e)),
        n = [];
      return (
        ab(t, function (e) {
          _y(Eb, e) || _y(Jy, e) || bb(n, e);
        }),
        n
      );
    },
    Ab = function (e) {
      var t = e === sb,
        n = gb(t ? wb : Py(e)),
        r = [];
      return (
        ab(n, function (e) {
          !_y(Eb, e) || (t && !_y(sb, e)) || bb(r, Eb[e]);
        }),
        r
      );
    };
  Ay ||
    ((fb = function () {
      if (jy(db, this)) throw pb("Symbol is not a constructor");
      var e =
          arguments.length && void 0 !== arguments[0]
            ? Fy(arguments[0])
            : void 0,
        t = Qy(e),
        n = function (e) {
          this === sb && Ry(n, wb, e),
            _y(this, ib) && _y(this[ib], t) && (this[ib][t] = !1),
            Tb(this, t, Hy(1, e));
        };
      return Iy && Nb && Tb(sb, t, { configurable: !0, set: n }), Sb(t, e);
    }),
    $y((db = fb.prototype), "toString", function () {
      return lb(this).tag;
    }),
    $y(fb, "withoutSetter", function (e) {
      return Sb(Qy(e), e);
    }),
    (qy.f = Rb),
    (Wy.f = Cb),
    (Xy.f = Ob),
    (Yy.f = kb),
    (zy.f = Gy.f = Ib),
    (Vy.f = Ab),
    (eb.f = function (e) {
      return Sb(Zy(e), e);
    }),
    Iy &&
      hb(db, "description", {
        configurable: !0,
        get: function () {
          return lb(this).description;
        },
      })),
    Cy(
      { global: !0, constructor: !0, wrap: !0, forced: !Ay, sham: !Ay },
      { Symbol: fb },
    ),
    ab(Uy(xb), function (e) {
      tb(e);
    }),
    Cy(
      { target: cb, stat: !0, forced: !Ay },
      {
        useSetter: function () {
          Nb = !0;
        },
        useSimple: function () {
          Nb = !1;
        },
      },
    ),
    Cy(
      { target: "Object", stat: !0, forced: !Ay, sham: !Iy },
      {
        create: function (e, t) {
          return void 0 === t ? By(e) : Ob(By(e), t);
        },
        defineProperty: Cb,
        defineProperties: Ob,
        getOwnPropertyDescriptor: kb,
      },
    ),
    Cy(
      { target: "Object", stat: !0, forced: !Ay },
      { getOwnPropertyNames: Ib },
    ),
    nb(),
    rb(fb, cb),
    (Jy[ib] = !0);
  var Mb = wt && !!Symbol.for && !!Symbol.keyFor,
    _b = Mr,
    jb = lt,
    Lb = Zt,
    Pb = li,
    Db = Ut.exports,
    Fb = Mb,
    Hb = Db("string-to-symbol-registry"),
    Bb = Db("symbol-to-string-registry");
  _b(
    { target: "Symbol", stat: !0, forced: !Fb },
    {
      for: function (e) {
        var t = Pb(e);
        if (Lb(Hb, t)) return Hb[t];
        var n = jb("Symbol")(t);
        return (Hb[t] = n), (Bb[n] = t), n;
      },
    },
  );
  var Ub = Mr,
    zb = Zt,
    Gb = Ot,
    Vb = kt,
    Yb = Mb,
    Wb = (0, Ut.exports)("symbol-to-string-registry");
  Ub(
    { target: "Symbol", stat: !0, forced: !Yb },
    {
      keyFor: function (e) {
        if (!Gb(e)) throw TypeError(Vb(e) + " is not a symbol");
        if (zb(Wb, e)) return Wb[e];
      },
    },
  );
  var Xb = Mr,
    qb = lt,
    $b = be,
    Kb = Ae,
    Jb = Se,
    Qb = pe,
    Zb = sy,
    eE = Ce,
    tE = rt,
    nE = Ot,
    rE = zu,
    oE = wt,
    aE = qb("JSON", "stringify"),
    iE = Jb(/./.exec),
    cE = Jb("".charAt),
    uE = Jb("".charCodeAt),
    lE = Jb("".replace),
    sE = Jb((1).toString),
    fE = /[\uD800-\uDFFF]/g,
    dE = /^[\uD800-\uDBFF]$/,
    pE = /^[\uDC00-\uDFFF]$/,
    mE =
      !oE ||
      Qb(function () {
        var e = qb("Symbol")();
        return (
          "[null]" != aE([e]) || "{}" != aE({ a: e }) || "{}" != aE(Object(e))
        );
      }),
    vE = Qb(function () {
      return (
        '"\\udf06\\ud834"' !== aE("\udf06\ud834") ||
        '"\\udead"' !== aE("\udead")
      );
    }),
    hE = function (e, t) {
      var n = rE(arguments),
        r = t;
      if ((tE(t) || void 0 !== e) && !nE(e))
        return (
          Zb(t) ||
            (t = function (e, t) {
              if ((eE(r) && (t = Kb(r, this, e, t)), !nE(t))) return t;
            }),
          (n[1] = t),
          $b(aE, null, n)
        );
    },
    gE = function (e, t, n) {
      var r = cE(n, t - 1),
        o = cE(n, t + 1);
      return (iE(dE, e) && !iE(pE, o)) || (iE(pE, e) && !iE(dE, r))
        ? "\\u" + sE(uE(e, 0), 16)
        : e;
    };
  aE &&
    Xb(
      { target: "JSON", stat: !0, arity: 3, forced: mE || vE },
      {
        stringify: function (e, t, n) {
          var r = rE(arguments),
            o = $b(mE ? hE : aE, null, r);
          return vE && "string" == typeof o ? lE(o, fE, gE) : o;
        },
      },
    );
  var yE = Ro,
    bE = Kt;
  Mr(
    {
      target: "Object",
      stat: !0,
      forced:
        !wt ||
        pe(function () {
          yE.f(1);
        }),
    },
    {
      getOwnPropertySymbols: function (e) {
        var t = yE.f;
        return t ? t(bE(e)) : [];
      },
    },
  );
  var EE = ot.Object.getOwnPropertySymbols;
  !(function (e) {
    e.exports = EE;
  })(Lg);
  var wE = le(Lg.exports),
    xE = { exports: {} },
    NE = { exports: {} },
    TE = Mr,
    SE = pe,
    CE = et,
    OE = Oe.f,
    RE = Re,
    kE = SE(function () {
      OE(1);
    });
  TE(
    { target: "Object", stat: !0, forced: !RE || kE, sham: !RE },
    {
      getOwnPropertyDescriptor: function (e, t) {
        return OE(CE(e), t);
      },
    },
  );
  var IE = ot.Object,
    AE = (NE.exports = function (e, t) {
      return IE.getOwnPropertyDescriptor(e, t);
    });
  IE.getOwnPropertyDescriptor.sham && (AE.sham = !0);
  var ME = NE.exports;
  !(function (e) {
    e.exports = ME;
  })(xE);
  var _E = le(xE.exports),
    jE = { exports: {} },
    LE = jo,
    PE = et,
    DE = Oe,
    FE = Bg;
  Mr(
    { target: "Object", stat: !0, sham: !Re },
    {
      getOwnPropertyDescriptors: function (e) {
        for (
          var t, n, r = PE(e), o = DE.f, a = LE(r), i = {}, c = 0;
          a.length > c;

        )
          void 0 !== (n = o(r, (t = a[c++]))) && FE(i, t, n);
        return i;
      },
    },
  );
  var HE = ot.Object.getOwnPropertyDescriptors;
  !(function (e) {
    e.exports = HE;
  })(jE);
  var BE = le(jE.exports),
    UE = { exports: {} },
    zE = { exports: {} },
    GE = Mr,
    VE = Re,
    YE = Ho.f;
  GE(
    {
      target: "Object",
      stat: !0,
      forced: Object.defineProperties !== YE,
      sham: !VE,
    },
    { defineProperties: YE },
  );
  var WE = ot.Object,
    XE = (zE.exports = function (e, t) {
      return WE.defineProperties(e, t);
    });
  WE.defineProperties.sham && (XE.sham = !0);
  var qE = zE.exports;
  !(function (e) {
    e.exports = qE;
  })(UE);
  var $E = le(UE.exports),
    KE = { exports: {} },
    JE = { exports: {} },
    QE = Mr,
    ZE = Re,
    ew = tr.f;
  QE(
    {
      target: "Object",
      stat: !0,
      forced: Object.defineProperty !== ew,
      sham: !ZE,
    },
    { defineProperty: ew },
  );
  var tw = ot.Object,
    nw = (JE.exports = function (e, t, n) {
      return tw.defineProperty(e, t, n);
    });
  tw.defineProperty.sham && (nw.sham = !0);
  var rw = JE.exports;
  !(function (e) {
    e.exports = rw;
  })(KE);
  var ow = le(KE.exports),
    aw = { exports: {} },
    iw = { exports: {} };
  Mr({ target: "Array", stat: !0 }, { isArray: sy });
  var cw = ot.Array.isArray;
  !(function (e) {
    e.exports = cw;
  })(iw),
    (function (e) {
      e.exports = iw.exports;
    })(aw);
  var uw = le(aw.exports);
  function lw(e, t) {
    (null == t || t > e.length) && (t = e.length);
    for (var n = 0, r = new Array(t); n < t; n++) r[n] = e[n];
    return r;
  }
  var sw = { exports: {} },
    fw = { exports: {} },
    dw = TypeError,
    pw = pe,
    mw = yt,
    vw = hn("species"),
    hw = function (e) {
      return (
        mw >= 51 ||
        !pw(function () {
          var t = [];
          return (
            ((t.constructor = {})[vw] = function () {
              return { foo: 1 };
            }),
            1 !== t[e](Boolean).foo
          );
        })
      );
    },
    gw = Mr,
    yw = pe,
    bw = sy,
    Ew = rt,
    ww = Kt,
    xw = fo,
    Nw = function (e) {
      if (e > 9007199254740991) throw dw("Maximum allowed index exceeded");
      return e;
    },
    Tw = Bg,
    Sw = gy,
    Cw = hw,
    Ow = yt,
    Rw = hn("isConcatSpreadable"),
    kw =
      Ow >= 51 ||
      !yw(function () {
        var e = [];
        return (e[Rw] = !1), e.concat()[0] !== e;
      }),
    Iw = Cw("concat"),
    Aw = function (e) {
      if (!Ew(e)) return !1;
      var t = e[Rw];
      return void 0 !== t ? !!t : bw(e);
    };
  gw(
    { target: "Array", proto: !0, arity: 1, forced: !kw || !Iw },
    {
      concat: function (e) {
        var t,
          n,
          r,
          o,
          a,
          i = ww(this),
          c = Sw(i, 0),
          u = 0;
        for (t = -1, r = arguments.length; t < r; t++)
          if (Aw((a = -1 === t ? i : arguments[t])))
            for (o = xw(a), Nw(u + o), n = 0; n < o; n++, u++)
              n in a && Tw(c, u, a[n]);
          else Nw(u + 1), Tw(c, u++, a);
        return (c.length = u), c;
      },
    },
  ),
    ry("asyncIterator"),
    ry("hasInstance"),
    ry("isConcatSpreadable"),
    ry("iterator"),
    ry("match"),
    ry("matchAll"),
    ry("replace"),
    ry("search"),
    ry("species"),
    ry("split");
  var Mw = uy;
  ry("toPrimitive"), Mw();
  var _w = lt,
    jw = kc;
  ry("toStringTag"),
    jw(_w("Symbol"), "Symbol"),
    ry("unscopables"),
    kc(de.JSON, "JSON", !0);
  var Lw = ot.Symbol;
  ry("asyncDispose"),
    ry("dispose"),
    ry("matcher"),
    ry("metadataKey"),
    ry("observable"),
    ry("metadata"),
    ry("patternMatch"),
    ry("replaceAll");
  var Pw = Lw;
  !(function (e) {
    e.exports = Pw;
  })(fw),
    (function (e) {
      e.exports = fw.exports;
    })(sw);
  var Dw = le(sw.exports),
    Fw = { exports: {} },
    Hw = { exports: {} },
    Bw = La;
  !(function (e) {
    e.exports = Bw;
  })(Hw),
    (function (e) {
      e.exports = Hw.exports;
    })(Fw);
  var Uw = le(Fw.exports),
    zw = { exports: {} },
    Gw = { exports: {} },
    Vw = ir,
    Yw = Wa,
    Ww = er,
    Xw = Ae,
    qw = Kt,
    $w = function (e, t, n, r) {
      try {
        return r ? t(Vw(n)[0], n[1]) : t(n);
      } catch (t) {
        Yw(e, "throw", t);
      }
    },
    Kw = Ea,
    Jw = Mu,
    Qw = fo,
    Zw = Bg,
    ex = za,
    tx = La,
    nx = Array,
    rx = function (e) {
      var t = qw(e),
        n = Jw(this),
        r = arguments.length,
        o = r > 1 ? arguments[1] : void 0,
        a = void 0 !== o;
      a && (o = Ww(o, r > 2 ? arguments[2] : void 0));
      var i,
        c,
        u,
        l,
        s,
        f,
        d = tx(t),
        p = 0;
      if (!d || (this === nx && Kw(d)))
        for (i = Qw(t), c = n ? new this(i) : nx(i); i > p; p++)
          (f = a ? o(t[p], p) : t[p]), Zw(c, p, f);
      else
        for (
          s = (l = ex(t, d)).next, c = n ? new this() : [];
          !(u = Xw(s, l)).done;
          p++
        )
          (f = a ? $w(l, o, [u.value, p], !0) : u.value), Zw(c, p, f);
      return (c.length = p), c;
    };
  Mr(
    {
      target: "Array",
      stat: !0,
      forced: !mf(function (e) {
        Array.from(e);
      }),
    },
    { from: rx },
  );
  var ox = ot.Array.from;
  !(function (e) {
    e.exports = ox;
  })(Gw),
    (function (e) {
      e.exports = Gw.exports;
    })(zw);
  var ax = le(zw.exports);
  var ix = { exports: {} },
    cx = { exports: {} },
    ux = Mr,
    lx = sy,
    sx = Mu,
    fx = rt,
    dx = co,
    px = fo,
    mx = et,
    vx = Bg,
    hx = hn,
    gx = zu,
    yx = hw("slice"),
    bx = hx("species"),
    Ex = Array,
    wx = Math.max;
  ux(
    { target: "Array", proto: !0, forced: !yx },
    {
      slice: function (e, t) {
        var n,
          r,
          o,
          a = mx(this),
          i = px(a),
          c = dx(e, i),
          u = dx(void 0 === t ? i : t, i);
        if (
          lx(a) &&
          ((n = a.constructor),
          ((sx(n) && (n === Ex || lx(n.prototype))) ||
            (fx(n) && null === (n = n[bx]))) &&
            (n = void 0),
          n === Ex || void 0 === n)
        )
          return gx(a, c, u);
        for (
          r = new (void 0 === n ? Ex : n)(wx(u - c, 0)), o = 0;
          c < u;
          c++, o++
        )
          c in a && vx(r, o, a[c]);
        return (r.length = o), r;
      },
    },
  );
  var xx = ot,
    Nx = function (e) {
      return xx[e + "Prototype"];
    },
    Tx = Nx("Array").slice,
    Sx = st,
    Cx = Tx,
    Ox = Array.prototype,
    Rx = function (e) {
      var t = e.slice;
      return e === Ox || (Sx(Ox, e) && t === Ox.slice) ? Cx : t;
    },
    kx = Rx;
  !(function (e) {
    e.exports = kx;
  })(cx),
    (function (e) {
      e.exports = cx.exports;
    })(ix);
  var Ix = le(ix.exports);
  function Ax(e, t) {
    var n;
    if (e) {
      if ("string" == typeof e) return lw(e, t);
      var r = Ix((n = Object.prototype.toString.call(e))).call(n, 8, -1);
      return (
        "Object" === r && e.constructor && (r = e.constructor.name),
        "Map" === r || "Set" === r
          ? ax(e)
          : "Arguments" === r ||
              /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(r)
            ? lw(e, t)
            : void 0
      );
    }
  }
  function Mx(e) {
    return (
      (function (e) {
        if (uw(e)) return lw(e);
      })(e) ||
      (function (e) {
        if ((void 0 !== Dw && null != Uw(e)) || null != e["@@iterator"])
          return ax(e);
      })(e) ||
      Ax(e) ||
      (function () {
        throw new TypeError(
          "Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.",
        );
      })()
    );
  }
  function _x(e, t) {
    return (
      (function (e) {
        if (uw(e)) return e;
      })(e) ||
      (function (e, t) {
        var n = null == e ? null : (void 0 !== Dw && Uw(e)) || e["@@iterator"];
        if (null != n) {
          var r,
            o,
            a = [],
            i = !0,
            c = !1;
          try {
            for (
              n = n.call(e);
              !(i = (r = n.next()).done) &&
              (a.push(r.value), !t || a.length !== t);
              i = !0
            );
          } catch (e) {
            (c = !0), (o = e);
          } finally {
            try {
              i || null == n.return || n.return();
            } finally {
              if (c) throw o;
            }
          }
          return a;
        }
      })(e, t) ||
      Ax(e, t) ||
      (function () {
        throw new TypeError(
          "Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.",
        );
      })()
    );
  }
  var jx = { exports: {} },
    Lx = { exports: {} },
    Px = rw;
  !(function (e) {
    e.exports = Px;
  })(Lx),
    (function (e) {
      e.exports = Lx.exports;
    })(jx);
  var Dx = le(jx.exports);
  function Fx(e, t, n) {
    return (
      t in e
        ? Dx(e, t, { value: n, enumerable: !0, configurable: !0, writable: !0 })
        : (e[t] = n),
      e
    );
  }
  var Hx = { exports: {} },
    Bx = Sy.map;
  Mr(
    { target: "Array", proto: !0, forced: !hw("map") },
    {
      map: function (e) {
        return Bx(this, e, arguments.length > 1 ? arguments[1] : void 0);
      },
    },
  );
  var Ux = Nx("Array").map,
    zx = st,
    Gx = Ux,
    Vx = Array.prototype,
    Yx = function (e) {
      var t = e.map;
      return e === Vx || (zx(Vx, e) && t === Vx.map) ? Gx : t;
    };
  !(function (e) {
    e.exports = Yx;
  })(Hx);
  var Wx = le(Hx.exports),
    Xx = { exports: {} },
    qx = Nx("Array").concat,
    $x = st,
    Kx = qx,
    Jx = Array.prototype,
    Qx = function (e) {
      var t = e.concat;
      return e === Jx || ($x(Jx, e) && t === Jx.concat) ? Kx : t;
    };
  !(function (e) {
    e.exports = Qx;
  })(Xx);
  var Zx = le(Xx.exports),
    eN = { exports: {} },
    tN = Sy.filter;
  Mr(
    { target: "Array", proto: !0, forced: !hw("filter") },
    {
      filter: function (e) {
        return tN(this, e, arguments.length > 1 ? arguments[1] : void 0);
      },
    },
  );
  var nN = Nx("Array").filter,
    rN = st,
    oN = nN,
    aN = Array.prototype,
    iN = function (e) {
      var t = e.filter;
      return e === aN || (rN(aN, e) && t === aN.filter) ? oN : t;
    };
  !(function (e) {
    e.exports = iN;
  })(eN);
  var cN = le(eN.exports),
    uN = bh,
    lN = String,
    sN = function (e) {
      if ("Symbol" === uN(e))
        throw TypeError("Cannot convert a Symbol value to a string");
      return lN(e);
    },
    fN = fm,
    dN = function () {
      var e = fN(this),
        t = "";
      return (
        e.hasIndices && (t += "d"),
        e.global && (t += "g"),
        e.ignoreCase && (t += "i"),
        e.multiline && (t += "m"),
        e.dotAll && (t += "s"),
        e.unicode && (t += "u"),
        e.unicodeSets && (t += "v"),
        e.sticky && (t += "y"),
        t
      );
    },
    pN = mm,
    mN = mp,
    vN = vm,
    hN = dN,
    gN = RegExp.prototype,
    yN = uv.PROPER,
    bN = uh,
    EN = fm,
    wN = sN,
    xN = Jd,
    NN = function (e) {
      var t = e.flags;
      return void 0 !== t || "flags" in gN || mN(e, "flags") || !vN(gN, e)
        ? t
        : pN(hN, e);
    },
    TN = "toString",
    SN = RegExp.prototype.toString,
    CN = xN(function () {
      return "/a/b" != SN.call({ source: "a", flags: "b" });
    }),
    ON = yN && SN.name != TN;
  function RN(e, t) {
    var n = jg(e);
    if (wE) {
      var r = wE(e);
      t &&
        (r = cN(r).call(r, function (t) {
          return _E(e, t).enumerable;
        })),
        n.push.apply(n, r);
    }
    return n;
  }
  function kN(e) {
    for (var t = 1; t < arguments.length; t++) {
      var n = null != arguments[t] ? arguments[t] : {};
      t % 2
        ? RN(Object(n), !0).forEach(function (t) {
            Fx(e, t, n[t]);
          })
        : BE
          ? $E(e, BE(n))
          : RN(Object(n)).forEach(function (t) {
              ow(e, t, _E(n, t));
            });
    }
    return e;
  }
  (CN || ON) &&
    bN(
      RegExp.prototype,
      TN,
      function () {
        var e = EN(this);
        return "/" + wN(e.source) + "/" + wN(NN(e));
      },
      { unsafe: !0 },
    );
  var IN = 0,
    AN = function (e, t) {
      var n,
        r = e.createdAt || Date.now(),
        o = e.hasTime || r - IN > 3e5;
      return (
        o && (IN = r),
        kN(
          kN({}, e),
          {},
          {
            _id:
              e._id ||
              t ||
              ((n = 2147483648),
              Math.floor(Math.random() * n).toString(36) +
                Math.abs(Math.floor(Math.random() * n) ^ Date.now()).toString(
                  36,
                )),
            createdAt: r,
            position: e.position || "left",
            hasTime: o,
          },
        )
      );
    },
    MN = "_TYPING_";
  function _N(e) {
    var n = e.active,
      r = void 0 !== n && n,
      o = e.ref,
      a = e.delay,
      i = void 0 === a ? 300 : a,
      c = _x(t.useState(!1), 2),
      u = c[0],
      l = c[1],
      s = _x(t.useState(!1), 2),
      f = s[0],
      d = s[1],
      p = t.useRef();
    return (
      t.useEffect(
        function () {
          r
            ? (p.current && clearTimeout(p.current), d(r))
            : (l(r),
              (p.current = setTimeout(function () {
                d(r);
              }, i)));
        },
        [r, i],
      ),
      t.useEffect(
        function () {
          o.current && o.current.offsetHeight, l(f);
        },
        [f, o],
      ),
      { didMount: f, isShow: u }
    );
  }
  var jN = { exports: {} },
    LN = { exports: {} },
    PN = Re,
    DN = Se,
    FN = Ae,
    HN = pe,
    BN = zo,
    UN = Ro,
    zN = Me,
    GN = Kt,
    VN = Xe,
    YN = Object.assign,
    WN = Object.defineProperty,
    XN = DN([].concat),
    qN =
      !YN ||
      HN(function () {
        if (
          PN &&
          1 !==
            YN(
              { b: 1 },
              YN(
                WN({}, "a", {
                  enumerable: !0,
                  get: function () {
                    WN(this, "b", { value: 3, enumerable: !1 });
                  },
                }),
                { b: 2 },
              ),
            ).b
        )
          return !0;
        var e = {},
          t = {},
          n = Symbol(),
          r = "abcdefghijklmnopqrst";
        return (
          (e[n] = 7),
          r.split("").forEach(function (e) {
            t[e] = e;
          }),
          7 != YN({}, e)[n] || BN(YN({}, t)).join("") != r
        );
      })
        ? function (e, t) {
            for (
              var n = GN(e), r = arguments.length, o = 1, a = UN.f, i = zN.f;
              r > o;

            )
              for (
                var c,
                  u = VN(arguments[o++]),
                  l = a ? XN(BN(u), a(u)) : BN(u),
                  s = l.length,
                  f = 0;
                s > f;

              )
                (c = l[f++]), (PN && !FN(i, u, c)) || (n[c] = u[c]);
            return n;
          }
        : YN,
    $N = qN;
  Mr(
    { target: "Object", stat: !0, arity: 2, forced: Object.assign !== $N },
    { assign: $N },
  );
  var KN = ot.Object.assign;
  !(function (e) {
    e.exports = KN;
  })(LN),
    (function (e) {
      e.exports = LN.exports;
    })(jN);
  var JN = le(jN.exports),
    QN = { exports: {} },
    ZN = { exports: {} },
    eT = Se,
    tT = _t,
    nT = rt,
    rT = Zt,
    oT = zu,
    aT = me,
    iT = Function,
    cT = eT([].concat),
    uT = eT([].join),
    lT = {},
    sT = function (e, t, n) {
      if (!rT(lT, t)) {
        for (var r = [], o = 0; o < t; o++) r[o] = "a[" + o + "]";
        lT[t] = iT("C,a", "return new C(" + uT(r, ",") + ")");
      }
      return lT[t](e, n);
    },
    fT = aT
      ? iT.bind
      : function (e) {
          var t = tT(this),
            n = t.prototype,
            r = oT(arguments, 1),
            o = function () {
              var n = cT(r, oT(arguments));
              return this instanceof o ? sT(t, n.length, n) : t.apply(e, n);
            };
          return nT(n) && (o.prototype = n), o;
        },
    dT = fT;
  Mr(
    { target: "Function", proto: !0, forced: Function.bind !== dT },
    { bind: dT },
  );
  var pT = Nx("Function").bind,
    mT = st,
    vT = pT,
    hT = Function.prototype,
    gT = function (e) {
      var t = e.bind;
      return e === hT || (mT(hT, e) && t === hT.bind) ? vT : t;
    };
  !(function (e) {
    e.exports = gT;
  })(ZN),
    (function (e) {
      e.exports = ZN.exports;
    })(QN);
  var yT = le(QN.exports);
  function bT() {
    var e;
    return (
      (bT = JN
        ? yT((e = JN)).call(e)
        : function (e) {
            for (var t = 1; t < arguments.length; t++) {
              var n = arguments[t];
              for (var r in n)
                Object.prototype.hasOwnProperty.call(n, r) && (e[r] = n[r]);
            }
            return e;
          }),
      bT.apply(this, arguments)
    );
  }
  var ET = { exports: {} },
    wT = { exports: {} },
    xT = EE;
  !(function (e) {
    e.exports = xT;
  })(wT),
    (function (e) {
      e.exports = wT.exports;
    })(ET);
  var NT = le(ET.exports),
    TT = { exports: {} },
    ST = { exports: {} },
    CT = pe,
    OT = function (e, t) {
      var n = [][e];
      return (
        !!n &&
        CT(function () {
          n.call(
            null,
            t ||
              function () {
                return 1;
              },
            1,
          );
        })
      );
    },
    RT = Mr,
    kT = go.indexOf,
    IT = OT,
    AT = Se([].indexOf),
    MT = !!AT && 1 / AT([1], 1, -0) < 0,
    _T = IT("indexOf");
  RT(
    { target: "Array", proto: !0, forced: MT || !_T },
    {
      indexOf: function (e) {
        var t = arguments.length > 1 ? arguments[1] : void 0;
        return MT ? AT(this, e, t) || 0 : kT(this, e, t);
      },
    },
  );
  var jT = Nx("Array").indexOf,
    LT = st,
    PT = jT,
    DT = Array.prototype,
    FT = function (e) {
      var t = e.indexOf;
      return e === DT || (LT(DT, e) && t === DT.indexOf) ? PT : t;
    },
    HT = FT;
  !(function (e) {
    e.exports = HT;
  })(ST),
    (function (e) {
      e.exports = ST.exports;
    })(TT);
  var BT = le(TT.exports),
    UT = { exports: {} },
    zT = { exports: {} },
    GT = _g;
  !(function (e) {
    e.exports = GT;
  })(zT),
    (function (e) {
      e.exports = zT.exports;
    })(UT);
  var VT = le(UT.exports);
  function YT(e, t) {
    if (null == e) return {};
    var n,
      r,
      o = (function (e, t) {
        if (null == e) return {};
        var n,
          r,
          o = {},
          a = VT(e);
        for (r = 0; r < a.length; r++)
          (n = a[r]), BT(t).call(t, n) >= 0 || (o[n] = e[n]);
        return o;
      })(e, t);
    if (NT) {
      var a = NT(e);
      for (r = 0; r < a.length; r++)
        (n = a[r]),
          BT(t).call(t, n) >= 0 ||
            (Object.prototype.propertyIsEnumerable.call(e, n) && (o[n] = e[n]));
    }
    return o;
  }
  var WT = { exports: {} },
    XT = Mr,
    qT = be,
    $T = fT,
    KT = Pu,
    JT = ir,
    QT = rt,
    ZT = ua,
    eS = pe,
    tS = lt("Reflect", "construct"),
    nS = Object.prototype,
    rS = [].push,
    oS = eS(function () {
      function e() {}
      return !(tS(function () {}, [], e) instanceof e);
    }),
    aS = !eS(function () {
      tS(function () {});
    }),
    iS = oS || aS;
  XT(
    { target: "Reflect", stat: !0, forced: iS, sham: iS },
    {
      construct: function (e, t) {
        KT(e), JT(t);
        var n = arguments.length < 3 ? e : KT(arguments[2]);
        if (aS && !oS) return tS(e, t, n);
        if (e == n) {
          switch (t.length) {
            case 0:
              return new e();
            case 1:
              return new e(t[0]);
            case 2:
              return new e(t[0], t[1]);
            case 3:
              return new e(t[0], t[1], t[2]);
            case 4:
              return new e(t[0], t[1], t[2], t[3]);
          }
          var r = [null];
          return qT(rS, r, t), new (qT($T, e, r))();
        }
        var o = n.prototype,
          a = ZT(QT(o) ? o : nS),
          i = qT(e, a, t);
        return QT(i) ? i : a;
      },
    },
  );
  var cS = ot.Reflect.construct;
  !(function (e) {
    e.exports = cS;
  })(WT);
  var uS = le(WT.exports);
  function lS(e, t) {
    for (var n = 0; n < t.length; n++) {
      var r = t[n];
      (r.enumerable = r.enumerable || !1),
        (r.configurable = !0),
        "value" in r && (r.writable = !0),
        Dx(e, r.key, r);
    }
  }
  var sS = { exports: {} },
    fS = { exports: {} };
  Mr({ target: "Object", stat: !0, sham: !Re }, { create: ua });
  var dS = ot.Object,
    pS = function (e, t) {
      return dS.create(e, t);
    };
  !(function (e) {
    e.exports = pS;
  })(fS),
    (function (e) {
      e.exports = fS.exports;
    })(sS);
  var mS = le(sS.exports),
    vS = { exports: {} },
    hS = { exports: {} };
  Mr({ target: "Object", stat: !0 }, { setPrototypeOf: Qr });
  var gS = ot.Object.setPrototypeOf;
  !(function (e) {
    e.exports = gS;
  })(hS),
    (function (e) {
      e.exports = hS.exports;
    })(vS);
  var yS = le(vS.exports);
  function bS(e, t) {
    var n;
    return (
      (bS = yS
        ? yT((n = yS)).call(n)
        : function (e, t) {
            return (e.__proto__ = t), e;
          }),
      bS(e, t)
    );
  }
  var ES = { exports: {} },
    wS = { exports: {} },
    xS = Jg.f("iterator");
  !(function (e) {
    e.exports = xS;
  })(wS),
    (function (e) {
      e.exports = wS.exports;
    })(ES);
  var NS = le(ES.exports);
  function TS(e) {
    return (
      (TS =
        "function" == typeof Dw && "symbol" == typeof NS
          ? function (e) {
              return typeof e;
            }
          : function (e) {
              return e &&
                "function" == typeof Dw &&
                e.constructor === Dw &&
                e !== Dw.prototype
                ? "symbol"
                : typeof e;
            }),
      TS(e)
    );
  }
  function SS(e, t) {
    if (t && ("object" === TS(t) || "function" == typeof t)) return t;
    if (void 0 !== t)
      throw new TypeError(
        "Derived constructors may only return object or undefined",
      );
    return (function (e) {
      if (void 0 === e)
        throw new ReferenceError(
          "this hasn't been initialised - super() hasn't been called",
        );
      return e;
    })(e);
  }
  var CS = { exports: {} },
    OS = { exports: {} },
    RS = Kt,
    kS = Yr,
    IS = Dr;
  Mr(
    {
      target: "Object",
      stat: !0,
      forced: pe(function () {
        kS(1);
      }),
      sham: !IS,
    },
    {
      getPrototypeOf: function (e) {
        return kS(RS(e));
      },
    },
  );
  var AS = ot.Object.getPrototypeOf;
  !(function (e) {
    e.exports = AS;
  })(OS),
    (function (e) {
      e.exports = OS.exports;
    })(CS);
  var MS = le(CS.exports);
  function _S(e) {
    var t;
    return (
      (_S = yS
        ? yT((t = MS)).call(t)
        : function (e) {
            return e.__proto__ || MS(e);
          }),
      _S(e)
    );
  }
  var jS = ["FallbackComponent", "children"];
  function LS(e) {
    var t = (function () {
      if ("undefined" == typeof Reflect || !uS) return !1;
      if (uS.sham) return !1;
      if ("function" == typeof Proxy) return !0;
      try {
        return (
          Boolean.prototype.valueOf.call(uS(Boolean, [], function () {})), !0
        );
      } catch (e) {
        return !1;
      }
    })();
    return function () {
      var n,
        r = _S(e);
      if (t) {
        var o = _S(this).constructor;
        n = uS(r, arguments, o);
      } else n = r.apply(this, arguments);
      return SS(this, n);
    };
  }
  var PS = (function (e) {
      !(function (e, t) {
        if ("function" != typeof t && null !== t)
          throw new TypeError(
            "Super expression must either be null or a function",
          );
        (e.prototype = mS(t && t.prototype, {
          constructor: { value: e, writable: !0, configurable: !0 },
        })),
          Dx(e, "prototype", { writable: !1 }),
          t && bS(e, t);
      })(i, e);
      var t,
        n,
        r,
        a = LS(i);
      function i(e) {
        var t;
        return (
          (function (e, t) {
            if (!(e instanceof t))
              throw new TypeError("Cannot call a class as a function");
          })(this, i),
          ((t = a.call(this, e)).state = { error: null, errorInfo: null }),
          t
        );
      }
      return (
        (t = i),
        (n = [
          {
            key: "componentDidCatch",
            value: function (e, t) {
              var n = this.props.onError;
              n && n(e, t), this.setState({ error: e, errorInfo: t });
            },
          },
          {
            key: "render",
            value: function () {
              var e = this.props,
                t = e.FallbackComponent,
                n = e.children,
                r = YT(e, jS),
                a = this.state,
                i = a.error,
                c = a.errorInfo;
              return c
                ? t
                  ? o.default.createElement(
                      t,
                      bT({ error: i, errorInfo: c }, r),
                    )
                  : null
                : n;
            },
          },
        ]) && lS(t.prototype, n),
        r && lS(t, r),
        Dx(t, "prototype", { writable: !1 }),
        i
      );
    })(o.default.Component),
    DS = ["component", "onError", "fallback"],
    FS = function (e) {
      var n = e.component,
        r = e.onError,
        a = e.fallback,
        i = YT(e, DS);
      return n
        ? o.default.createElement(
            PS,
            { onError: r },
            o.default.createElement(
              t.Suspense,
              { fallback: a || null },
              o.default.createElement(n, i),
            ),
          )
        : null;
    },
    HS = o.default.createContext({
      addComponent: function () {},
      hasComponent: function () {
        return !1;
      },
      getComponent: function () {
        return null;
      },
    });
  function BS() {
    return o.default.useContext(HS);
  }
  var US = ["code", "fallback", "onLoad", "onError"],
    zS = ["component", "code", "onLoad"],
    GS = function (e) {
      var t = e.code,
        n = e.fallback,
        r = e.onLoad,
        a = e.onError,
        i = YT(e, US),
        c = (0, BS().getComponent)(t, function (e) {
          "async" in e && r
            ? r(e)
            : "errCode" in e && a && a(new Error(e.errCode));
        });
      return o.default.createElement(
        FS,
        bT({ component: c, onError: a, fallback: n }, i),
      );
    };
  function VS(e, t) {
    var n = jg(e);
    if (wE) {
      var r = wE(e);
      t &&
        (r = cN(r).call(r, function (t) {
          return _E(e, t).enumerable;
        })),
        n.push.apply(n, r);
    }
    return n;
  }
  function YS(e) {
    for (var t = 1; t < arguments.length; t++) {
      var n = null != arguments[t] ? arguments[t] : {};
      t % 2
        ? VS(Object(n), !0).forEach(function (t) {
            Fx(e, t, n[t]);
          })
        : BE
          ? $E(e, BE(n))
          : VS(Object(n)).forEach(function (t) {
              ow(e, t, _E(n, t));
            });
    }
    return e;
  }
  var WS = function (e) {
      var t = e.className,
        n = e.src,
        r = e.alt,
        a = e.url,
        i = e.size,
        u = void 0 === i ? "md" : i,
        l = e.shape,
        s = void 0 === l ? "circle" : l,
        f = e.children,
        d = a ? "a" : "span";
      return o.default.createElement(
        d,
        {
          className: c("Avatar", "Avatar--".concat(u), "Avatar--".concat(s), t),
          href: a,
        },
        n ? o.default.createElement("img", { src: n, alt: r }) : f,
      );
    },
    XS = ["className", "active", "onClick"],
    qS = function (e) {
      var t = e.className,
        n = e.active,
        r = e.onClick,
        a = YT(e, XS);
      return o.default.createElement(
        "div",
        bT(
          {
            className: c("Backdrop", t, { active: n }),
            onClick: r,
            role: "button",
            tabIndex: -1,
            "aria-hidden": !0,
          },
          a,
        ),
      );
    },
    $S = ["type", "content", "children"],
    KS = function (e) {
      var t = e.type,
        n = void 0 === t ? "text" : t,
        r = e.content,
        a = e.children,
        i = YT(e, $S);
      return o.default.createElement(
        "div",
        bT({ className: "Bubble ".concat(n), "data-type": n }, i),
        r && o.default.createElement("p", null, r),
        a,
      );
    },
    JS = ["type", "className", "spin", "name"],
    QS = function (e) {
      var t = e.type,
        n = e.className,
        r = e.spin,
        a = e.name,
        i = YT(e, JS),
        u = "string" == typeof a ? { "aria-label": a } : { "aria-hidden": !0 };
      return o.default.createElement(
        "svg",
        bT({ className: c("Icon", { "is-spin": r }, n) }, u, i),
        o.default.createElement("use", { xlinkHref: "#icon-".concat(t) }),
      );
    },
    ZS = [
      "className",
      "label",
      "color",
      "variant",
      "size",
      "icon",
      "loading",
      "block",
      "disabled",
      "children",
      "onClick",
    ];
  function eC(e) {
    return e && "Btn--".concat(e);
  }
  var tC = function (e) {
      var t = e.className,
        n = e.label,
        r = e.color,
        a = e.variant,
        i = e.size,
        u = e.icon,
        l = e.loading,
        s = e.block,
        f = e.disabled,
        d = e.children,
        p = e.onClick,
        m = YT(e, ZS),
        v = u || (l && "spinner"),
        h = i || (s ? "lg" : "");
      return o.default.createElement(
        "button",
        bT(
          {
            className: c("Btn", eC(r), eC(a), eC(h), { "Btn--block": s }, t),
            type: "button",
            disabled: f,
            onClick: function (e) {
              f || l || !p || p(e);
            },
          },
          m,
        ),
        v &&
          o.default.createElement(
            "span",
            { className: "Btn-icon" },
            o.default.createElement(QS, { type: v, spin: l }),
          ),
        n || d,
      );
    },
    nC = ["className", "size", "fluid", "children"],
    rC = o.default.forwardRef(function (e, t) {
      var n = e.className,
        r = e.size,
        a = e.fluid,
        i = e.children,
        u = YT(e, nC);
      return o.default.createElement(
        "div",
        bT(
          {
            className: c(
              "Card",
              r && "Card--".concat(r),
              { "Card--fluid": a },
              n,
            ),
            "data-fluid": a,
          },
          u,
          { ref: t },
        ),
        i,
      );
    }),
    oC = [
      "as",
      "className",
      "inline",
      "center",
      "direction",
      "wrap",
      "justifyContent",
      "justify",
      "alignItems",
      "align",
      "children",
    ],
    aC = {
      row: "Flex--d-r",
      "row-reverse": "Flex--d-rr",
      column: "Flex--d-c",
      "column-reverse": "Flex--d-cr",
    },
    iC = {
      nowrap: "Flex--w-n",
      wrap: "Flex--w-w",
      "wrap-reverse": "Flex--w-wr",
    },
    cC = {
      "flex-start": "Flex--jc-fs",
      "flex-end": "Flex--jc-fe",
      center: "Flex--jc-c",
      "space-between": "Flex--jc-sb",
      "space-around": "Flex--jc-sa",
    },
    uC = {
      "flex-start": "Flex--ai-fs",
      "flex-end": "Flex--ai-fe",
      center: "Flex--ai-c",
    },
    lC = o.default.forwardRef(function (e, t) {
      var n = e.as,
        r = void 0 === n ? "div" : n,
        a = e.className,
        i = e.inline,
        u = e.center,
        l = e.direction,
        s = e.wrap,
        f = e.justifyContent,
        d = e.justify,
        p = void 0 === d ? f : d,
        m = e.alignItems,
        v = e.align,
        h = void 0 === v ? m : v,
        g = e.children,
        y = YT(e, oC);
      return o.default.createElement(
        r,
        bT(
          {
            className: c(
              "Flex",
              l && aC[l],
              p && cC[p],
              h && uC[h],
              s && iC[s],
              { "Flex--inline": i, "Flex--center": u },
              a,
            ),
            ref: t,
          },
          y,
        ),
        g,
      );
    }),
    sC = ["className", "flex", "alignSelf", "order", "style", "children"];
  function fC(e, t) {
    var n = jg(e);
    if (wE) {
      var r = wE(e);
      t &&
        (r = cN(r).call(r, function (t) {
          return _E(e, t).enumerable;
        })),
        n.push.apply(n, r);
    }
    return n;
  }
  function dC(e) {
    for (var t = 1; t < arguments.length; t++) {
      var n = null != arguments[t] ? arguments[t] : {};
      t % 2
        ? fC(Object(n), !0).forEach(function (t) {
            Fx(e, t, n[t]);
          })
        : BE
          ? $E(e, BE(n))
          : fC(Object(n)).forEach(function (t) {
              ow(e, t, _E(n, t));
            });
    }
    return e;
  }
  var pC = function (e) {
      var t = e.className,
        n = e.flex,
        r = e.alignSelf,
        a = e.order,
        i = e.style,
        u = e.children,
        l = YT(e, sC);
      return o.default.createElement(
        "div",
        bT(
          {
            className: c("FlexItem", t),
            style: dC(dC({}, i), {}, { flex: n, alignSelf: r, order: a }),
          },
          l,
        ),
        u,
      );
    },
    mC = ["className", "aspectRatio", "color", "image", "children"],
    vC = ["className", "children"],
    hC = ["className", "title", "subtitle", "center", "children"],
    gC = ["className", "children"],
    yC = ["children", "className", "direction"],
    bC = {},
    EC = {},
    wC = {}.propertyIsEnumerable,
    xC = Object.getOwnPropertyDescriptor,
    NC = xC && !wC.call({ 1: 2 }, 1);
  EC.f = NC
    ? function (e) {
        var t = xC(this, e);
        return !!t && t.enumerable;
      }
    : wC;
  var TC = _h,
    SC = up,
    CC = function (e) {
      return TC(SC(e));
    },
    OC = Jp,
    RC = mm,
    kC = EC,
    IC = bv,
    AC = CC,
    MC = Vm,
    _C = mp,
    jC = im,
    LC = Object.getOwnPropertyDescriptor;
  bC.f = OC
    ? LC
    : function (e, t) {
        if (((e = AC(e)), (t = MC(t)), jC))
          try {
            return LC(e, t);
          } catch (e) {}
        if (_C(e, t)) return IC(!RC(kC.f, e, t), e[t]);
      };
  var PC = {},
    DC = Dh,
    FC = Math.max,
    HC = Math.min,
    BC = function (e, t) {
      var n = DC(e);
      return n < 0 ? FC(n + t, 0) : HC(n, t);
    },
    UC = CC,
    zC = BC,
    GC = zh,
    VC = function (e) {
      return function (t, n, r) {
        var o,
          a = UC(t),
          i = GC(a),
          c = zC(r, i);
        if (e && n != n) {
          for (; i > c; ) if ((o = a[c++]) != o) return !0;
        } else
          for (; i > c; c++)
            if ((e || c in a) && a[c] === n) return e || c || 0;
        return !e && -1;
      };
    },
    YC = { includes: VC(!0), indexOf: VC(!1) },
    WC = mp,
    XC = CC,
    qC = YC.indexOf,
    $C = Ov,
    KC = op([].push),
    JC = function (e, t) {
      var n,
        r = XC(e),
        o = 0,
        a = [];
      for (n in r) !WC($C, n) && WC(r, n) && KC(a, n);
      for (; t.length > o; ) WC(r, (n = t[o++])) && (~qC(a, n) || KC(a, n));
      return a;
    },
    QC = [
      "constructor",
      "hasOwnProperty",
      "isPrototypeOf",
      "propertyIsEnumerable",
      "toLocaleString",
      "toString",
      "valueOf",
    ],
    ZC = JC,
    eO = QC.concat("length", "prototype");
  PC.f =
    Object.getOwnPropertyNames ||
    function (e) {
      return ZC(e, eO);
    };
  var tO = {};
  tO.f = Object.getOwnPropertySymbols;
  var nO,
    rO,
    oO,
    aO,
    iO = Tp,
    cO = PC,
    uO = tO,
    lO = fm,
    sO = op([].concat),
    fO =
      iO("Reflect", "ownKeys") ||
      function (e) {
        var t = cO.f(lO(e)),
          n = uO.f;
        return n ? sO(t, n(e)) : t;
      },
    dO = mp,
    pO = fO,
    mO = bC,
    vO = Kp,
    hO = Jd,
    gO = Ep,
    yO = /#|\.prototype\./,
    bO = function (e, t) {
      var n = wO[EO(e)];
      return n == NO || (n != xO && (gO(t) ? hO(t) : !!t));
    },
    EO = (bO.normalize = function (e) {
      return String(e).replace(yO, ".").toLowerCase();
    }),
    wO = (bO.data = {}),
    xO = (bO.NATIVE = "N"),
    NO = (bO.POLYFILL = "P"),
    TO = bO,
    SO = Bd,
    CO = bC.f,
    OO = xv,
    RO = uh,
    kO = Vd,
    IO = function (e, t, n) {
      for (var r = pO(t), o = vO.f, a = mO.f, i = 0; i < r.length; i++) {
        var c = r[i];
        dO(e, c) || (n && dO(n, c)) || o(e, c, a(t, c));
      }
    },
    AO = TO,
    MO = function (e, t) {
      var n,
        r,
        o,
        a,
        i,
        c = e.target,
        u = e.global,
        l = e.stat;
      if ((n = u ? SO : l ? SO[c] || kO(c, {}) : (SO[c] || {}).prototype))
        for (r in t) {
          if (
            ((a = t[r]),
            (o = e.dontCallGetSet ? (i = CO(n, r)) && i.value : n[r]),
            !AO(u ? r : c + (l ? "." : "#") + r, e.forced) && void 0 !== o)
          ) {
            if (typeof a == typeof o) continue;
            IO(a, o);
          }
          (e.sham || (o && o.sham)) && OO(a, "sham", !0), RO(n, r, a, e);
        }
    },
    _O = "process" == dh(Bd.process),
    jO = Ep,
    LO = String,
    PO = TypeError,
    DO = op,
    FO = fm,
    HO = function (e) {
      if ("object" == typeof e || jO(e)) return e;
      throw PO("Can't set " + LO(e) + " as a prototype");
    },
    BO =
      Object.setPrototypeOf ||
      ("__proto__" in {}
        ? (function () {
            var e,
              t = !1,
              n = {};
            try {
              (e = DO(
                Object.getOwnPropertyDescriptor(Object.prototype, "__proto__")
                  .set,
              ))(n, []),
                (t = n instanceof Array);
            } catch (e) {}
            return function (n, r) {
              return FO(n), HO(r), t ? e(n, r) : (n.__proto__ = r), n;
            };
          })()
        : void 0),
    UO = Kp.f,
    zO = mp,
    GO = Xp("toStringTag"),
    VO = Tp,
    YO = Kp,
    WO = Jp,
    XO = Xp("species"),
    qO = vm,
    $O = TypeError,
    KO = og,
    JO = xm,
    QO = TypeError,
    ZO = fm,
    eR = function (e) {
      if (KO(e)) return e;
      throw QO(JO(e) + " is not a constructor");
    },
    tR = ap,
    nR = Xp("species"),
    rR = function (e, t) {
      var n,
        r = ZO(e).constructor;
      return void 0 === r || tR((n = ZO(r)[nR])) ? t : eR(n);
    },
    oR = Qd,
    aR = Function.prototype,
    iR = aR.apply,
    cR = aR.call,
    uR =
      ("object" == typeof Reflect && Reflect.apply) ||
      (oR
        ? cR.bind(iR)
        : function () {
            return cR.apply(iR, arguments);
          }),
    lR = Tp("document", "documentElement"),
    sR = op([].slice),
    fR = TypeError,
    dR = /(?:ipad|iphone|ipod).*applewebkit/i.test(Sp),
    pR = Bd,
    mR = uR,
    vR = Rh,
    hR = Ep,
    gR = mp,
    yR = Jd,
    bR = lR,
    ER = sR,
    wR = om,
    xR = function (e, t) {
      if (e < t) throw fR("Not enough arguments");
      return e;
    },
    NR = dR,
    TR = _O,
    SR = pR.setImmediate,
    CR = pR.clearImmediate,
    OR = pR.process,
    RR = pR.Dispatch,
    kR = pR.Function,
    IR = pR.MessageChannel,
    AR = pR.String,
    MR = 0,
    _R = {},
    jR = "onreadystatechange";
  try {
    nO = pR.location;
  } catch (e) {}
  var LR = function (e) {
      if (gR(_R, e)) {
        var t = _R[e];
        delete _R[e], t();
      }
    },
    PR = function (e) {
      return function () {
        LR(e);
      };
    },
    DR = function (e) {
      LR(e.data);
    },
    FR = function (e) {
      pR.postMessage(AR(e), nO.protocol + "//" + nO.host);
    };
  (SR && CR) ||
    ((SR = function (e) {
      xR(arguments.length, 1);
      var t = hR(e) ? e : kR(e),
        n = ER(arguments, 1);
      return (
        (_R[++MR] = function () {
          mR(t, void 0, n);
        }),
        rO(MR),
        MR
      );
    }),
    (CR = function (e) {
      delete _R[e];
    }),
    TR
      ? (rO = function (e) {
          OR.nextTick(PR(e));
        })
      : RR && RR.now
        ? (rO = function (e) {
            RR.now(PR(e));
          })
        : IR && !NR
          ? ((aO = (oO = new IR()).port2),
            (oO.port1.onmessage = DR),
            (rO = vR(aO.postMessage, aO)))
          : pR.addEventListener &&
              hR(pR.postMessage) &&
              !pR.importScripts &&
              nO &&
              "file:" !== nO.protocol &&
              !yR(FR)
            ? ((rO = FR), pR.addEventListener("message", DR, !1))
            : (rO =
                jR in wR("script")
                  ? function (e) {
                      bR.appendChild(wR("script")).onreadystatechange =
                        function () {
                          bR.removeChild(this), LR(e);
                        };
                    }
                  : function (e) {
                      setTimeout(PR(e), 0);
                    }));
  var HR,
    BR,
    UR,
    zR,
    GR,
    VR,
    YR,
    WR,
    XR = { set: SR, clear: CR },
    qR = Bd,
    $R = /ipad|iphone|ipod/i.test(Sp) && void 0 !== qR.Pebble,
    KR = /web0s(?!.*chrome)/i.test(Sp),
    JR = Bd,
    QR = Rh,
    ZR = bC.f,
    ek = XR.set,
    tk = dR,
    nk = $R,
    rk = KR,
    ok = _O,
    ak = JR.MutationObserver || JR.WebKitMutationObserver,
    ik = JR.document,
    ck = JR.process,
    uk = JR.Promise,
    lk = ZR(JR, "queueMicrotask"),
    sk = lk && lk.value;
  sk ||
    ((HR = function () {
      var e, t;
      for (ok && (e = ck.domain) && e.exit(); BR; ) {
        (t = BR.fn), (BR = BR.next);
        try {
          t();
        } catch (e) {
          throw (BR ? zR() : (UR = void 0), e);
        }
      }
      (UR = void 0), e && e.enter();
    }),
    tk || ok || rk || !ak || !ik
      ? !nk && uk && uk.resolve
        ? (((YR = uk.resolve(void 0)).constructor = uk),
          (WR = QR(YR.then, YR)),
          (zR = function () {
            WR(HR);
          }))
        : ok
          ? (zR = function () {
              ck.nextTick(HR);
            })
          : ((ek = QR(ek, JR)),
            (zR = function () {
              ek(HR);
            }))
      : ((GR = !0),
        (VR = ik.createTextNode("")),
        new ak(HR).observe(VR, { characterData: !0 }),
        (zR = function () {
          VR.data = GR = !GR;
        })));
  var fk =
      sk ||
      function (e) {
        var t = { fn: e, next: void 0 };
        UR && (UR.next = t), BR || ((BR = t), zR()), (UR = t);
      },
    dk = Bd,
    pk = function (e) {
      try {
        return { error: !1, value: e() };
      } catch (e) {
        return { error: !0, value: e };
      }
    },
    mk = function () {
      (this.head = null), (this.tail = null);
    };
  mk.prototype = {
    add: function (e) {
      var t = { item: e, next: null };
      this.head ? (this.tail.next = t) : (this.head = t), (this.tail = t);
    },
    get: function () {
      var e = this.head;
      if (e)
        return (
          (this.head = e.next), this.tail === e && (this.tail = null), e.item
        );
    },
  };
  var vk = mk,
    hk = Bd.Promise,
    gk = "object" == typeof Deno && Deno && "object" == typeof Deno.version,
    yk = !gk && !_O && "object" == typeof window && "object" == typeof document,
    bk = Bd,
    Ek = hk,
    wk = Ep,
    xk = TO,
    Nk = vv,
    Tk = Xp,
    Sk = yk,
    Ck = gk,
    Ok = Mp;
  Ek && Ek.prototype;
  var Rk = Tk("species"),
    kk = !1,
    Ik = wk(bk.PromiseRejectionEvent),
    Ak = xk("Promise", function () {
      var e = Nk(Ek),
        t = e !== String(Ek);
      if (!t && 66 === Ok) return !0;
      if (!Ok || Ok < 51 || !/native code/.test(e)) {
        var n = new Ek(function (e) {
            e(1);
          }),
          r = function (e) {
            e(
              function () {},
              function () {},
            );
          };
        if (
          (((n.constructor = {})[Rk] = r),
          !(kk = n.then(function () {}) instanceof r))
        )
          return !0;
      }
      return !t && (Sk || Ck) && !Ik;
    }),
    Mk = { CONSTRUCTOR: Ak, REJECTION_EVENT: Ik, SUBCLASSING: kk },
    _k = {},
    jk = Cm,
    Lk = TypeError,
    Pk = function (e) {
      var t, n;
      (this.promise = new e(function (e, r) {
        if (void 0 !== t || void 0 !== n) throw Lk("Bad Promise constructor");
        (t = e), (n = r);
      })),
        (this.resolve = jk(t)),
        (this.reject = jk(n));
    };
  _k.f = function (e) {
    return new Pk(e);
  };
  var Dk,
    Fk,
    Hk,
    Bk = MO,
    Uk = _O,
    zk = Bd,
    Gk = mm,
    Vk = uh,
    Yk = BO,
    Wk = function (e, t, n) {
      e && !n && (e = e.prototype),
        e && !zO(e, GO) && UO(e, GO, { configurable: !0, value: t });
    },
    Xk = function (e) {
      var t = VO(e),
        n = YO.f;
      WO &&
        t &&
        !t[XO] &&
        n(t, XO, {
          configurable: !0,
          get: function () {
            return this;
          },
        });
    },
    qk = Cm,
    $k = Ep,
    Kk = em,
    Jk = function (e, t) {
      if (qO(t, e)) return e;
      throw $O("Incorrect invocation");
    },
    Qk = rR,
    Zk = XR.set,
    eI = fk,
    tI = function (e, t) {
      var n = dk.console;
      n && n.error && (1 == arguments.length ? n.error(e) : n.error(e, t));
    },
    nI = pk,
    rI = vk,
    oI = Yv,
    aI = hk,
    iI = _k,
    cI = "Promise",
    uI = Mk.CONSTRUCTOR,
    lI = Mk.REJECTION_EVENT,
    sI = Mk.SUBCLASSING,
    fI = oI.getterFor(cI),
    dI = oI.set,
    pI = aI && aI.prototype,
    mI = aI,
    vI = pI,
    hI = zk.TypeError,
    gI = zk.document,
    yI = zk.process,
    bI = iI.f,
    EI = bI,
    wI = !!(gI && gI.createEvent && zk.dispatchEvent),
    xI = "unhandledrejection",
    NI = function (e) {
      var t;
      return !(!Kk(e) || !$k((t = e.then))) && t;
    },
    TI = function (e, t) {
      var n,
        r,
        o,
        a = t.value,
        i = 1 == t.state,
        c = i ? e.ok : e.fail,
        u = e.resolve,
        l = e.reject,
        s = e.domain;
      try {
        c
          ? (i || (2 === t.rejection && kI(t), (t.rejection = 1)),
            !0 === c
              ? (n = a)
              : (s && s.enter(), (n = c(a)), s && (s.exit(), (o = !0))),
            n === e.promise
              ? l(hI("Promise-chain cycle"))
              : (r = NI(n))
                ? Gk(r, n, u, l)
                : u(n))
          : l(a);
      } catch (e) {
        s && !o && s.exit(), l(e);
      }
    },
    SI = function (e, t) {
      e.notified ||
        ((e.notified = !0),
        eI(function () {
          for (var n, r = e.reactions; (n = r.get()); ) TI(n, e);
          (e.notified = !1), t && !e.rejection && OI(e);
        }));
    },
    CI = function (e, t, n) {
      var r, o;
      wI
        ? (((r = gI.createEvent("Event")).promise = t),
          (r.reason = n),
          r.initEvent(e, !1, !0),
          zk.dispatchEvent(r))
        : (r = { promise: t, reason: n }),
        !lI && (o = zk["on" + e])
          ? o(r)
          : e === xI && tI("Unhandled promise rejection", n);
    },
    OI = function (e) {
      Gk(Zk, zk, function () {
        var t,
          n = e.facade,
          r = e.value;
        if (
          RI(e) &&
          ((t = nI(function () {
            Uk ? yI.emit("unhandledRejection", r, n) : CI(xI, n, r);
          })),
          (e.rejection = Uk || RI(e) ? 2 : 1),
          t.error)
        )
          throw t.value;
      });
    },
    RI = function (e) {
      return 1 !== e.rejection && !e.parent;
    },
    kI = function (e) {
      Gk(Zk, zk, function () {
        var t = e.facade;
        Uk
          ? yI.emit("rejectionHandled", t)
          : CI("rejectionhandled", t, e.value);
      });
    },
    II = function (e, t, n) {
      return function (r) {
        e(t, r, n);
      };
    },
    AI = function (e, t, n) {
      e.done ||
        ((e.done = !0), n && (e = n), (e.value = t), (e.state = 2), SI(e, !0));
    },
    MI = function (e, t, n) {
      if (!e.done) {
        (e.done = !0), n && (e = n);
        try {
          if (e.facade === t) throw hI("Promise can't be resolved itself");
          var r = NI(t);
          r
            ? eI(function () {
                var n = { done: !1 };
                try {
                  Gk(r, t, II(MI, n, e), II(AI, n, e));
                } catch (t) {
                  AI(n, t, e);
                }
              })
            : ((e.value = t), (e.state = 1), SI(e, !1));
        } catch (t) {
          AI({ done: !1 }, t, e);
        }
      }
    };
  if (
    uI &&
    ((vI = (mI = function (e) {
      Jk(this, vI), qk(e), Gk(Dk, this);
      var t = fI(this);
      try {
        e(II(MI, t), II(AI, t));
      } catch (e) {
        AI(t, e);
      }
    }).prototype),
    ((Dk = function (e) {
      dI(this, {
        type: cI,
        done: !1,
        notified: !1,
        parent: !1,
        reactions: new rI(),
        rejection: !1,
        state: 0,
        value: void 0,
      });
    }).prototype = Vk(vI, "then", function (e, t) {
      var n = fI(this),
        r = bI(Qk(this, mI));
      return (
        (n.parent = !0),
        (r.ok = !$k(e) || e),
        (r.fail = $k(t) && t),
        (r.domain = Uk ? yI.domain : void 0),
        0 == n.state
          ? n.reactions.add(r)
          : eI(function () {
              TI(r, n);
            }),
        r.promise
      );
    })),
    (Fk = function () {
      var e = new Dk(),
        t = fI(e);
      (this.promise = e), (this.resolve = II(MI, t)), (this.reject = II(AI, t));
    }),
    (iI.f = bI =
      function (e) {
        return e === mI || undefined === e ? new Fk(e) : EI(e);
      }),
    $k(aI) && pI !== Object.prototype)
  ) {
    (Hk = pI.then),
      sI ||
        Vk(
          pI,
          "then",
          function (e, t) {
            var n = this;
            return new mI(function (e, t) {
              Gk(Hk, n, e, t);
            }).then(e, t);
          },
          { unsafe: !0 },
        );
    try {
      delete pI.constructor;
    } catch (e) {}
    Yk && Yk(pI, vI);
  }
  Bk({ global: !0, constructor: !0, wrap: !0, forced: uI }, { Promise: mI }),
    Wk(mI, cI, !1),
    Xk(cI);
  var _I = {},
    jI = _I,
    LI = Xp("iterator"),
    PI = Array.prototype,
    DI = bh,
    FI = km,
    HI = ap,
    BI = _I,
    UI = Xp("iterator"),
    zI = function (e) {
      if (!HI(e)) return FI(e, UI) || FI(e, "@@iterator") || BI[DI(e)];
    },
    GI = mm,
    VI = Cm,
    YI = fm,
    WI = xm,
    XI = zI,
    qI = TypeError,
    $I = mm,
    KI = fm,
    JI = km,
    QI = Rh,
    ZI = mm,
    eA = fm,
    tA = xm,
    nA = function (e) {
      return void 0 !== e && (jI.Array === e || PI[LI] === e);
    },
    rA = zh,
    oA = vm,
    aA = function (e, t) {
      var n = arguments.length < 2 ? XI(e) : t;
      if (VI(n)) return YI(GI(n, e));
      throw qI(WI(e) + " is not iterable");
    },
    iA = zI,
    cA = function (e, t, n) {
      var r, o;
      KI(e);
      try {
        if (!(r = JI(e, "return"))) {
          if ("throw" === t) throw n;
          return n;
        }
        r = $I(r, e);
      } catch (e) {
        (o = !0), (r = e);
      }
      if ("throw" === t) throw n;
      if (o) throw r;
      return KI(r), n;
    },
    uA = TypeError,
    lA = function (e, t) {
      (this.stopped = e), (this.result = t);
    },
    sA = lA.prototype,
    fA = function (e, t, n) {
      var r,
        o,
        a,
        i,
        c,
        u,
        l,
        s = n && n.that,
        f = !(!n || !n.AS_ENTRIES),
        d = !(!n || !n.IS_RECORD),
        p = !(!n || !n.IS_ITERATOR),
        m = !(!n || !n.INTERRUPTED),
        v = QI(t, s),
        h = function (e) {
          return r && cA(r, "normal", e), new lA(!0, e);
        },
        g = function (e) {
          return f
            ? (eA(e), m ? v(e[0], e[1], h) : v(e[0], e[1]))
            : m
              ? v(e, h)
              : v(e);
        };
      if (d) r = e.iterator;
      else if (p) r = e;
      else {
        if (!(o = iA(e))) throw uA(tA(e) + " is not iterable");
        if (nA(o)) {
          for (a = 0, i = rA(e); i > a; a++)
            if ((c = g(e[a])) && oA(sA, c)) return c;
          return new lA(!1);
        }
        r = aA(e, o);
      }
      for (u = d ? e.next : r.next; !(l = ZI(u, r)).done; ) {
        try {
          c = g(l.value);
        } catch (e) {
          cA(r, "throw", e);
        }
        if ("object" == typeof c && c && oA(sA, c)) return c;
      }
      return new lA(!1);
    },
    dA = Xp("iterator"),
    pA = !1;
  try {
    var mA = 0,
      vA = {
        next: function () {
          return { done: !!mA++ };
        },
        return: function () {
          pA = !0;
        },
      };
    (vA[dA] = function () {
      return this;
    }),
      Array.from(vA, function () {
        throw 2;
      });
  } catch (e) {}
  var hA = hk,
    gA = function (e, t) {
      if (!t && !pA) return !1;
      var n = !1;
      try {
        var r = {};
        (r[dA] = function () {
          return {
            next: function () {
              return { done: (n = !0) };
            },
          };
        }),
          e(r);
      } catch (e) {}
      return n;
    },
    yA =
      Mk.CONSTRUCTOR ||
      !gA(function (e) {
        hA.all(e).then(void 0, function () {});
      }),
    bA = mm,
    EA = Cm,
    wA = _k,
    xA = pk,
    NA = fA;
  MO(
    { target: "Promise", stat: !0, forced: yA },
    {
      all: function (e) {
        var t = this,
          n = wA.f(t),
          r = n.resolve,
          o = n.reject,
          a = xA(function () {
            var n = EA(t.resolve),
              a = [],
              i = 0,
              c = 1;
            NA(e, function (e) {
              var u = i++,
                l = !1;
              c++,
                bA(n, t, e).then(function (e) {
                  l || ((l = !0), (a[u] = e), --c || r(a));
                }, o);
            }),
              --c || r(a);
          });
        return a.error && o(a.value), n.promise;
      },
    },
  );
  var TA = MO,
    SA = Mk.CONSTRUCTOR,
    CA = hk,
    OA = Tp,
    RA = Ep,
    kA = uh,
    IA = CA && CA.prototype;
  if (
    (TA(
      { target: "Promise", proto: !0, forced: SA, real: !0 },
      {
        catch: function (e) {
          return this.then(void 0, e);
        },
      },
    ),
    RA(CA))
  ) {
    var AA = OA("Promise").prototype.catch;
    IA.catch !== AA && kA(IA, "catch", AA, { unsafe: !0 });
  }
  var MA = mm,
    _A = Cm,
    jA = _k,
    LA = pk,
    PA = fA;
  MO(
    { target: "Promise", stat: !0, forced: yA },
    {
      race: function (e) {
        var t = this,
          n = jA.f(t),
          r = n.reject,
          o = LA(function () {
            var o = _A(t.resolve);
            PA(e, function (e) {
              MA(o, t, e).then(n.resolve, r);
            });
          });
        return o.error && r(o.value), n.promise;
      },
    },
  );
  var DA = mm,
    FA = _k;
  MO(
    { target: "Promise", stat: !0, forced: Mk.CONSTRUCTOR },
    {
      reject: function (e) {
        var t = FA.f(this);
        return DA(t.reject, void 0, e), t.promise;
      },
    },
  );
  var HA = fm,
    BA = em,
    UA = _k,
    zA = MO,
    GA = Mk.CONSTRUCTOR,
    VA = function (e, t) {
      if ((HA(e), BA(t) && t.constructor === e)) return t;
      var n = UA.f(e);
      return (0, n.resolve)(t), n.promise;
    };
  Tp("Promise"),
    zA(
      { target: "Promise", stat: !0, forced: GA },
      {
        resolve: function (e) {
          return VA(this, e);
        },
      },
    );
  var YA = { exports: {} },
    WA = go.includes;
  Mr(
    {
      target: "Array",
      proto: !0,
      forced: pe(function () {
        return !Array(1).includes();
      }),
    },
    {
      includes: function (e) {
        return WA(this, e, arguments.length > 1 ? arguments[1] : void 0);
      },
    },
  );
  var XA = Nx("Array").includes,
    qA = rt,
    $A = ze,
    KA = hn("match"),
    JA = function (e) {
      var t;
      return qA(e) && (void 0 !== (t = e[KA]) ? !!t : "RegExp" == $A(e));
    },
    QA = TypeError,
    ZA = hn("match"),
    eM = Mr,
    tM = function (e) {
      if (JA(e)) throw QA("The method doesn't accept regular expressions");
      return e;
    },
    nM = Je,
    rM = li,
    oM = function (e) {
      var t = /./;
      try {
        "/./"[e](t);
      } catch (n) {
        try {
          return (t[ZA] = !1), "/./"[e](t);
        } catch (e) {}
      }
      return !1;
    },
    aM = Se("".indexOf);
  eM(
    { target: "String", proto: !0, forced: !oM("includes") },
    {
      includes: function (e) {
        return !!~aM(
          rM(nM(this)),
          rM(tM(e)),
          arguments.length > 1 ? arguments[1] : void 0,
        );
      },
    },
  );
  var iM = Nx("String").includes,
    cM = st,
    uM = XA,
    lM = iM,
    sM = Array.prototype,
    fM = String.prototype,
    dM = function (e) {
      var t = e.includes;
      return e === sM || (cM(sM, e) && t === sM.includes)
        ? uM
        : "string" == typeof e || e === fM || (cM(fM, e) && t === fM.includes)
          ? lM
          : t;
    };
  !(function (e) {
    e.exports = dM;
  })(YA);
  var pM = le(YA.exports),
    mM = { exports: {} },
    vM = "\t\n\v\f\r                　\u2028\u2029\ufeff",
    hM = Je,
    gM = li,
    yM = Se("".replace),
    bM = "[\t\n\v\f\r                　\u2028\u2029\ufeff]",
    EM = RegExp("^" + bM + bM + "*"),
    wM = RegExp(bM + bM + "*$"),
    xM = function (e) {
      return function (t) {
        var n = gM(hM(t));
        return 1 & e && (n = yM(n, EM, "")), 2 & e && (n = yM(n, wM, "")), n;
      };
    },
    NM = { start: xM(1), end: xM(2), trim: xM(3) },
    TM = de,
    SM = pe,
    CM = Se,
    OM = li,
    RM = NM.trim,
    kM = vM,
    IM = TM.parseInt,
    AM = TM.Symbol,
    MM = AM && AM.iterator,
    _M = /^[+-]?0x/i,
    jM = CM(_M.exec),
    LM =
      8 !== IM(kM + "08") ||
      22 !== IM(kM + "0x16") ||
      (MM &&
        !SM(function () {
          IM(Object(MM));
        }))
        ? function (e, t) {
            var n = RM(OM(e));
            return IM(n, t >>> 0 || (jM(_M, n) ? 16 : 10));
          }
        : IM;
  Mr({ global: !0, forced: parseInt != LM }, { parseInt: LM });
  var PM = ot.parseInt;
  !(function (e) {
    e.exports = PM;
  })(mM);
  var DM = le(mM.exports),
    FM = function (e) {
      var t = e.width,
        n = e.children;
      return o.default.createElement(
        "div",
        { className: "Carousel-item", style: { width: t } },
        n,
      );
    },
    HM = function (e, t) {
      (e.style.transform = t), (e.style.webkitTransform = t);
    },
    BM = function (e, t) {
      (e.style.transition = t), (e.style.webkitTransition = t);
    },
    UM = {
      passiveListener: function () {
        var e = !1;
        try {
          var t = ow({}, "passive", {
            get: function () {
              e = !0;
            },
          });
          window.addEventListener("test", null, t);
        } catch (e) {}
        return e;
      },
      smoothScroll: function () {
        return "scrollBehavior" in document.documentElement.style;
      },
      touch: function () {
        return "ontouchstart" in window;
      },
    };
  function zM(e) {
    return UM[e]();
  }
  var GM = ["TEXTAREA", "OPTION", "INPUT", "SELECT"],
    VM = zM("touch"),
    YM = o.default.forwardRef(function (e, n) {
      var r,
        a,
        i,
        u = e.className,
        l = e.startIndex,
        s = void 0 === l ? 0 : l,
        f = e.draggable,
        d = void 0 === f || f,
        p = e.duration,
        m = void 0 === p ? 300 : p,
        v = e.easing,
        h = void 0 === v ? "ease" : v,
        g = e.threshold,
        y = void 0 === g ? 20 : g,
        b = e.clickDragThreshold,
        E = void 0 === b ? 10 : b,
        w = e.loop,
        x = void 0 === w || w,
        N = e.rtl,
        T = void 0 !== N && N,
        S = e.autoPlay,
        C = void 0 === S ? e.autoplay || !1 : S,
        O = e.interval,
        R = void 0 === O ? e.autoplaySpeed || 4e3 : O,
        k = e.dots,
        I = void 0 === k ? e.indicators || !0 : k,
        A = e.onChange,
        M = e.children,
        _ = o.default.Children.count(M),
        j = "".concat(100 / _, "%"),
        L = t.useRef(null),
        P = t.useRef(null),
        D = t.useRef(null),
        F = t.useRef({
          first: !0,
          wrapWidth: 0,
          hover: !1,
          startX: 0,
          endX: 0,
          startY: 0,
          canMove: null,
          pressDown: !1,
        }),
        H = t.useCallback(
          function (e) {
            return x ? e % _ : Math.max(0, Math.min(e, _ - 1));
          },
          [_, x],
        ),
        B = _x(t.useState(H(s)), 2),
        U = B[0],
        z = B[1],
        G = _x(t.useState(!1), 2),
        V = G[0],
        Y = G[1],
        W = t.useCallback(
          function () {
            var e;
            BM(P.current, Zx((e = "transform ".concat(m, "ms "))).call(e, h));
          },
          [m, h],
        ),
        X = function () {
          BM(P.current, "transform 0s");
        },
        q = function (e) {
          HM(P.current, "translate3d(".concat(e, "px, 0, 0)"));
        },
        $ = t.useCallback(
          function (e, t) {
            var n = (T ? 1 : -1) * (x ? e + 1 : e) * F.current.wrapWidth;
            t
              ? requestAnimationFrame(function () {
                  requestAnimationFrame(function () {
                    W(), q(n);
                  });
                })
              : q(n);
          },
          [W, x, T],
        ),
        K = t.useCallback(
          function (e) {
            if (!(_ <= 1)) {
              var t = H(e);
              t !== U && z(t);
            }
          },
          [U, _, H],
        ),
        J = t.useCallback(
          function () {
            if (!(_ <= 1)) {
              var e = U - 1;
              if (x) {
                if (e < 0) {
                  var t = F.current,
                    n = (T ? 1 : -1) * (_ + 1) * t.wrapWidth,
                    r = d ? t.endX - t.startX : 0;
                  X(), q(n + r), (e = _ - 1);
                }
              } else e = Math.max(e, 0);
              e !== U && z(e);
            }
          },
          [U, _, d, x, T],
        ),
        Q = t.useCallback(
          function () {
            if (!(_ <= 1)) {
              var e = U + 1;
              if (x) {
                if (e > _ - 1) {
                  e = 0;
                  var t = F.current,
                    n = d ? t.endX - t.startX : 0;
                  X(), q(n);
                }
              } else e = Math.min(e, _ - 1);
              e !== U && z(e);
            }
          },
          [U, _, d, x],
        ),
        Z = t.useCallback(
          function () {
            C &&
              !F.current.hover &&
              (D.current = setTimeout(function () {
                W(), Q();
              }, R));
          },
          [C, R, W, Q],
        ),
        ee = function () {
          clearTimeout(D.current);
        },
        te = function () {
          $(U, !0), Z();
        },
        ne = function () {
          var e = F.current,
            t = (T ? -1 : 1) * (e.endX - e.startX),
            n = Math.abs(t),
            r = t > 0 && U - 1 < 0;
          r || (t < 0 && U + 1 > _ - 1)
            ? x
              ? r
                ? J()
                : Q()
              : te()
            : t > 0 && n > y && _ > 1
              ? J()
              : t < 0 && n > y && _ > 1
                ? Q()
                : te();
        },
        re = function () {
          var e = F.current;
          (e.startX = 0),
            (e.endX = 0),
            (e.startY = 0),
            (e.canMove = null),
            (e.pressDown = !1);
        },
        oe = function (e) {
          if (!pM(GM).call(GM, e.target.nodeName)) {
            e.preventDefault(), e.stopPropagation();
            var t = "touches" in e ? e.touches[0] : e,
              n = F.current;
            (n.pressDown = !0),
              (n.startX = t.pageX),
              (n.startY = t.pageY),
              ee();
          }
        },
        ae = function (e) {
          e.stopPropagation();
          var t = "touches" in e ? e.touches[0] : e,
            n = F.current;
          if (n.pressDown) {
            if (
              "touches" in e &&
              (null === n.canMove &&
                (n.canMove =
                  Math.abs(n.startY - t.pageY) < Math.abs(n.startX - t.pageX)),
              !n.canMove)
            )
              return;
            e.preventDefault(), X(), (n.endX = t.pageX);
            var r = (x ? U + 1 : U) * n.wrapWidth,
              o = n.endX - n.startX;
            !V && Math.abs(o) > E && Y(!0), q(T ? r + o : o - r);
          }
        },
        ie = function (e) {
          e.stopPropagation();
          var t = F.current;
          (t.pressDown = !1), Y(!1), W(), t.endX ? ne() : Z(), re();
        },
        ce = function () {
          (F.current.hover = !0), ee();
        },
        ue = function (e) {
          var t = F.current;
          (t.hover = !1),
            t.pressDown &&
              ((t.pressDown = !1), (t.endX = e.pageX), W(), ne(), re()),
            Z();
        },
        le = function (e) {
          var t = e.currentTarget.dataset.slideTo;
          if (t) {
            var n = DM(t, 10);
            K(n);
          }
          e.preventDefault();
        };
      return (
        t.useImperativeHandle(
          n,
          function () {
            return { goTo: K, prev: J, next: Q };
          },
          [K, J, Q],
        ),
        t.useEffect(
          function () {
            function e() {
              (F.current.wrapWidth = L.current.offsetWidth), $(U);
            }
            return (
              F.current.first && e(),
              window.addEventListener("resize", e),
              function () {
                window.removeEventListener("resize", e);
              }
            );
          },
          [U, $],
        ),
        t.useEffect(
          function () {
            A && !F.current.first && A(U);
          },
          [U, A],
        ),
        t.useEffect(
          function () {
            F.current.first ? ($(U), (F.current.first = !1)) : $(U, !0);
          },
          [U, $],
        ),
        t.useEffect(
          function () {
            return (
              Z(),
              function () {
                ee();
              }
            );
          },
          [C, U, Z],
        ),
        (i = d
          ? VM
            ? { onTouchStart: oe, onTouchMove: ae, onTouchEnd: ie }
            : {
                onMouseDown: oe,
                onMouseMove: ae,
                onMouseUp: ie,
                onMouseEnter: ce,
                onMouseLeave: ue,
              }
          : { onMouseEnter: ce, onMouseLeave: ue }),
        o.default.createElement(
          "div",
          bT(
            {
              className: c(
                "Carousel",
                {
                  "Carousel--draggable": d,
                  "Carousel--rtl": T,
                  "Carousel--dragging": V,
                },
                u,
              ),
              ref: L,
            },
            i,
          ),
          o.default.createElement(
            "div",
            {
              className: "Carousel-inner",
              style: { width: "".concat(x ? _ + 2 : _, "00%") },
              ref: P,
            },
            x &&
              o.default.createElement(
                FM,
                { width: j },
                o.default.Children.toArray(M)[_ - 1],
              ),
            Wx((r = o.default.Children)).call(r, M, function (e, t) {
              return o.default.createElement(FM, { width: j, key: t }, e);
            }),
            x &&
              o.default.createElement(
                FM,
                { width: j },
                o.default.Children.toArray(M)[0],
              ),
          ),
          I &&
            o.default.createElement(
              "ol",
              { className: "Carousel-dots" },
              Wx((a = o.default.Children)).call(a, M, function (e, t) {
                return o.default.createElement(
                  "li",
                  { key: t },
                  o.default.createElement("button", {
                    className: c("Carousel-dot", { active: U === t }),
                    type: "button",
                    "aria-label": "Go to slide ".concat(t + 1),
                    "data-slide-to": t,
                    onClick: le,
                  }),
                );
              }),
            ),
        )
      );
    }),
    WM = ["className", "label", "checked", "disabled", "onChange"],
    XM = function (e) {
      var t = e.className,
        n = e.label,
        r = e.checked,
        a = e.disabled,
        i = e.onChange,
        u = YT(e, WM);
      return o.default.createElement(
        "label",
        {
          className: c("Checkbox", t, {
            "Checkbox--checked": r,
            "Checkbox--disabled": a,
          }),
        },
        o.default.createElement(
          "input",
          bT(
            {
              type: "checkbox",
              className: "Checkbox-input",
              checked: r,
              disabled: a,
              onChange: i,
            },
            u,
          ),
        ),
        o.default.createElement("span", { className: "Checkbox-text" }, n),
      );
    },
    qM = ["children", "onClick", "mouseEvent"],
    $M = document,
    KM = $M.documentElement,
    JM = function (e) {
      var n = e.children,
        r = e.onClick,
        a = e.mouseEvent,
        i = void 0 === a ? "mouseup" : a,
        c = YT(e, qM),
        u = t.useRef(null);
      function l(e) {
        u.current &&
          KM.contains(e.target) &&
          !u.current.contains(e.target) &&
          r(e);
      }
      return (
        t.useEffect(function () {
          return (
            i && $M.addEventListener(i, l),
            function () {
              $M.removeEventListener(i, l);
            }
          );
        }),
        o.default.createElement("div", bT({ ref: u }, c), n)
      );
    },
    QM = ["className", "position", "children"],
    ZM = ["className", "theme", "children"],
    e_ = o.default.createContext(""),
    t_ = ["children"],
    n_ = function (e) {
      var t = e.children,
        n = YT(e, t_);
      return o.default.createElement("label", bT({ className: "Label" }, n), t);
    },
    r_ = ["children"],
    o_ = function (e) {
      var t = e.children,
        n = YT(e, r_);
      return o.default.createElement(
        "div",
        bT({ className: "HelpText" }, n),
        t,
      );
    },
    a_ = ["children"],
    i_ = ["className", "icon", "img"],
    c_ = function (e) {
      var t = e.className,
        n = e.icon,
        r = e.img,
        a = YT(e, i_);
      return o.default.createElement(
        tC,
        bT({ className: c("IconBtn", t) }, a),
        n && o.default.createElement(QS, { type: n }),
        !n && r && o.default.createElement("img", { src: r, alt: "" }),
      );
    },
    u_ = ["className", "src", "lazy", "fluid", "children"],
    l_ = o.default.forwardRef(function (e, n) {
      var r = e.className,
        a = e.src,
        i = e.lazy,
        u = e.fluid;
      e.children;
      var l = YT(e, u_),
        s = _x(t.useState(""), 2),
        f = s[0],
        d = s[1],
        p = Fd(n),
        m = t.useRef(""),
        v = t.useRef(!1);
      return (
        t.useEffect(
          function () {
            if (i) {
              var e = new IntersectionObserver(function (t) {
                var n = _x(t, 1)[0];
                n.isIntersecting &&
                  (d(m.current), (v.current = !0), e.unobserve(n.target));
              });
              return (
                p.current && e.observe(p.current),
                function () {
                  e.disconnect();
                }
              );
            }
          },
          [p, i],
        ),
        t.useEffect(
          function () {
            (m.current = a), d(i && !v.current ? "" : a);
          },
          [i, a],
        ),
        o.default.createElement(
          "img",
          bT(
            {
              className: c("Image", { "Image--fluid": u }, r),
              src: f,
              alt: "",
              ref: p,
            },
            l,
          ),
        )
      );
    });
  function s_(e) {
    return e.scrollHeight - e.scrollTop - e.offsetHeight;
  }
  var f_ = [
      "className",
      "disabled",
      "distance",
      "children",
      "onLoadMore",
      "onScroll",
    ],
    d_ = o.default.forwardRef(function (e, t) {
      var n = e.className,
        r = e.disabled,
        a = e.distance,
        i = void 0 === a ? 0 : a,
        u = e.children,
        l = e.onLoadMore,
        s = e.onScroll,
        f = YT(e, f_),
        d = Fd(t);
      return o.default.createElement(
        "div",
        bT(
          {
            className: c("InfiniteScroll", n),
            role: "feed",
            onScroll: r
              ? void 0
              : function (e) {
                  s && s(e);
                  var t = d.current;
                  t && s_(t) <= i && l();
                },
            ref: d,
          },
          f,
        ),
        u,
      );
    }),
    p_ = Ep,
    m_ = em,
    v_ = BO,
    h_ = op((1).valueOf),
    g_ = up,
    y_ = sN,
    b_ = op("".replace),
    E_ = "[\t\n\v\f\r                　\u2028\u2029\ufeff]",
    w_ = RegExp("^" + E_ + E_ + "*"),
    x_ = RegExp(E_ + E_ + "*$"),
    N_ = function (e) {
      return function (t) {
        var n = y_(g_(t));
        return 1 & e && (n = b_(n, w_, "")), 2 & e && (n = b_(n, x_, "")), n;
      };
    },
    T_ = { start: N_(1), end: N_(2), trim: N_(3) },
    S_ = Jp,
    C_ = Bd,
    O_ = op,
    R_ = TO,
    k_ = uh,
    I_ = mp,
    A_ = function (e, t, n) {
      var r, o;
      return (
        v_ &&
          p_((r = t.constructor)) &&
          r !== n &&
          m_((o = r.prototype)) &&
          o !== n.prototype &&
          v_(e, o),
        e
      );
    },
    M_ = vm,
    __ = Em,
    j_ = Um,
    L_ = Jd,
    P_ = PC.f,
    D_ = bC.f,
    F_ = Kp.f,
    H_ = h_,
    B_ = T_.trim,
    U_ = "Number",
    z_ = C_.Number,
    G_ = z_.prototype,
    V_ = C_.TypeError,
    Y_ = O_("".slice),
    W_ = O_("".charCodeAt),
    X_ = function (e) {
      var t = j_(e, "number");
      return "bigint" == typeof t ? t : q_(t);
    },
    q_ = function (e) {
      var t,
        n,
        r,
        o,
        a,
        i,
        c,
        u,
        l = j_(e, "number");
      if (__(l)) throw V_("Cannot convert a Symbol value to a number");
      if ("string" == typeof l && l.length > 2)
        if (((l = B_(l)), 43 === (t = W_(l, 0)) || 45 === t)) {
          if (88 === (n = W_(l, 2)) || 120 === n) return NaN;
        } else if (48 === t) {
          switch (W_(l, 1)) {
            case 66:
            case 98:
              (r = 2), (o = 49);
              break;
            case 79:
            case 111:
              (r = 8), (o = 55);
              break;
            default:
              return +l;
          }
          for (i = (a = Y_(l, 2)).length, c = 0; c < i; c++)
            if ((u = W_(a, c)) < 48 || u > o) return NaN;
          return parseInt(a, r);
        }
      return +l;
    };
  if (R_(U_, !z_(" 0o1") || !z_("0b1") || z_("+0x1"))) {
    for (
      var $_,
        K_ = function (e) {
          var t = arguments.length < 1 ? 0 : z_(X_(e)),
            n = this;
          return M_(G_, n) &&
            L_(function () {
              H_(n);
            })
            ? A_(Object(t), n, K_)
            : t;
        },
        J_ = S_
          ? P_(z_)
          : "MAX_VALUE,MIN_VALUE,NaN,NEGATIVE_INFINITY,POSITIVE_INFINITY,EPSILON,MAX_SAFE_INTEGER,MIN_SAFE_INTEGER,isFinite,isInteger,isNaN,isSafeInteger,parseFloat,parseInt,fromString,range".split(
              ",",
            ),
        Q_ = 0;
      J_.length > Q_;
      Q_++
    )
      I_(z_, ($_ = J_[Q_])) && !I_(K_, $_) && F_(K_, $_, D_(z_, $_));
    (K_.prototype = G_),
      (G_.constructor = K_),
      k_(C_, U_, K_, { constructor: !0 });
  }
  var Z_ = Jd,
    ej = Bd.RegExp,
    tj = Z_(function () {
      var e = ej("a", "y");
      return (e.lastIndex = 2), null != e.exec("abcd");
    }),
    nj =
      tj ||
      Z_(function () {
        return !ej("a", "y").sticky;
      }),
    rj = {
      BROKEN_CARET:
        tj ||
        Z_(function () {
          var e = ej("^r", "gy");
          return (e.lastIndex = 2), null != e.exec("str");
        }),
      MISSED_STICKY: nj,
      UNSUPPORTED_Y: tj,
    },
    oj = {},
    aj = JC,
    ij = QC,
    cj =
      Object.keys ||
      function (e) {
        return aj(e, ij);
      },
    uj = Jp,
    lj = cm,
    sj = Kp,
    fj = fm,
    dj = CC,
    pj = cj;
  oj.f =
    uj && !lj
      ? Object.defineProperties
      : function (e, t) {
          fj(e);
          for (var n, r = dj(t), o = pj(t), a = o.length, i = 0; a > i; )
            sj.f(e, (n = o[i++]), r[n]);
          return e;
        };
  var mj,
    vj = fm,
    hj = oj,
    gj = QC,
    yj = Ov,
    bj = lR,
    Ej = om,
    wj = Cv("IE_PROTO"),
    xj = function () {},
    Nj = function (e) {
      return "<script>" + e + "</" + "script>";
    },
    Tj = function (e) {
      e.write(Nj("")), e.close();
      var t = e.parentWindow.Object;
      return (e = null), t;
    },
    Sj = function () {
      try {
        mj = new ActiveXObject("htmlfile");
      } catch (e) {}
      var e, t;
      Sj =
        "undefined" != typeof document
          ? document.domain && mj
            ? Tj(mj)
            : (((t = Ej("iframe")).style.display = "none"),
              bj.appendChild(t),
              (t.src = String("javascript:")),
              (e = t.contentWindow.document).open(),
              e.write(Nj("document.F=Object")),
              e.close(),
              e.F)
          : Tj(mj);
      for (var n = gj.length; n--; ) delete Sj.prototype[gj[n]];
      return Sj();
    };
  yj[wj] = !0;
  var Cj,
    Oj,
    Rj =
      Object.create ||
      function (e, t) {
        var n;
        return (
          null !== e
            ? ((xj.prototype = vj(e)),
              (n = new xj()),
              (xj.prototype = null),
              (n[wj] = e))
            : (n = Sj()),
          void 0 === t ? n : hj.f(n, t)
        );
      },
    kj = Jd,
    Ij = Bd.RegExp,
    Aj = kj(function () {
      var e = Ij(".", "s");
      return !(e.dotAll && e.exec("\n") && "s" === e.flags);
    }),
    Mj = Jd,
    _j = Bd.RegExp,
    jj = Mj(function () {
      var e = _j("(?<a>b)", "g");
      return "b" !== e.exec("b").groups.a || "bc" !== "b".replace(e, "$<a>c");
    }),
    Lj = mm,
    Pj = op,
    Dj = sN,
    Fj = dN,
    Hj = rj,
    Bj = Ud.exports,
    Uj = Rj,
    zj = Yv.get,
    Gj = Aj,
    Vj = jj,
    Yj = Bj("native-string-replace", String.prototype.replace),
    Wj = RegExp.prototype.exec,
    Xj = Wj,
    qj = Pj("".charAt),
    $j = Pj("".indexOf),
    Kj = Pj("".replace),
    Jj = Pj("".slice),
    Qj =
      ((Oj = /b*/g),
      Lj(Wj, (Cj = /a/), "a"),
      Lj(Wj, Oj, "a"),
      0 !== Cj.lastIndex || 0 !== Oj.lastIndex),
    Zj = Hj.BROKEN_CARET,
    eL = void 0 !== /()??/.exec("")[1];
  (Qj || eL || Zj || Gj || Vj) &&
    (Xj = function (e) {
      var t,
        n,
        r,
        o,
        a,
        i,
        c,
        u = this,
        l = zj(u),
        s = Dj(e),
        f = l.raw;
      if (f)
        return (
          (f.lastIndex = u.lastIndex),
          (t = Lj(Xj, f, s)),
          (u.lastIndex = f.lastIndex),
          t
        );
      var d = l.groups,
        p = Zj && u.sticky,
        m = Lj(Fj, u),
        v = u.source,
        h = 0,
        g = s;
      if (
        (p &&
          ((m = Kj(m, "y", "")),
          -1 === $j(m, "g") && (m += "g"),
          (g = Jj(s, u.lastIndex)),
          u.lastIndex > 0 &&
            (!u.multiline ||
              (u.multiline && "\n" !== qj(s, u.lastIndex - 1))) &&
            ((v = "(?: " + v + ")"), (g = " " + g), h++),
          (n = new RegExp("^(?:" + v + ")", m))),
        eL && (n = new RegExp("^" + v + "$(?!\\s)", m)),
        Qj && (r = u.lastIndex),
        (o = Lj(Wj, p ? n : u, g)),
        p
          ? o
            ? ((o.input = Jj(o.input, h)),
              (o[0] = Jj(o[0], h)),
              (o.index = u.lastIndex),
              (u.lastIndex += o[0].length))
            : (u.lastIndex = 0)
          : Qj && o && (u.lastIndex = u.global ? o.index + o[0].length : r),
        eL &&
          o &&
          o.length > 1 &&
          Lj(Yj, o[0], n, function () {
            for (a = 1; a < arguments.length - 2; a++)
              void 0 === arguments[a] && (o[a] = void 0);
          }),
        o && d)
      )
        for (o.groups = i = Uj(null), a = 0; a < d.length; a++)
          i[(c = d[a])[0]] = o[c[1]];
      return o;
    });
  var tL = Xj;
  MO({ target: "RegExp", proto: !0, forced: /./.exec !== tL }, { exec: tL });
  var nL = op,
    rL = uh,
    oL = tL,
    aL = Jd,
    iL = Xp,
    cL = xv,
    uL = iL("species"),
    lL = RegExp.prototype,
    sL = function (e, t, n, r) {
      var o = iL(e),
        a = !aL(function () {
          var t = {};
          return (
            (t[o] = function () {
              return 7;
            }),
            7 != ""[e](t)
          );
        }),
        i =
          a &&
          !aL(function () {
            var t = !1,
              n = /a/;
            return (
              "split" === e &&
                (((n = {}).constructor = {}),
                (n.constructor[uL] = function () {
                  return n;
                }),
                (n.flags = ""),
                (n[o] = /./[o])),
              (n.exec = function () {
                return (t = !0), null;
              }),
              n[o](""),
              !t
            );
          });
      if (!a || !i || n) {
        var c = nL(/./[o]),
          u = t(o, ""[e], function (e, t, n, r, o) {
            var i = nL(e),
              u = t.exec;
            return u === oL || u === lL.exec
              ? a && !o
                ? { done: !0, value: c(t, n, r) }
                : { done: !0, value: i(n, t, r) }
              : { done: !1 };
          });
        rL(String.prototype, e, u[0]), rL(lL, o, u[1]);
      }
      r && cL(lL[o], "sham", !0);
    },
    fL = op,
    dL = Dh,
    pL = sN,
    mL = up,
    vL = fL("".charAt),
    hL = fL("".charCodeAt),
    gL = fL("".slice),
    yL = function (e) {
      return function (t, n) {
        var r,
          o,
          a = pL(mL(t)),
          i = dL(n),
          c = a.length;
        return i < 0 || i >= c
          ? e
            ? ""
            : void 0
          : (r = hL(a, i)) < 55296 ||
              r > 56319 ||
              i + 1 === c ||
              (o = hL(a, i + 1)) < 56320 ||
              o > 57343
            ? e
              ? vL(a, i)
              : r
            : e
              ? gL(a, i, i + 2)
              : o - 56320 + ((r - 55296) << 10) + 65536;
      };
    },
    bL = { codeAt: yL(!1), charAt: yL(!0) }.charAt,
    EL = function (e, t, n) {
      return t + (n ? bL(e, t).length : 1);
    },
    wL = op,
    xL = fp,
    NL = Math.floor,
    TL = wL("".charAt),
    SL = wL("".replace),
    CL = wL("".slice),
    OL = /\$([$&'`]|\d{1,2}|<[^>]*>)/g,
    RL = /\$([$&'`]|\d{1,2})/g,
    kL = mm,
    IL = fm,
    AL = Ep,
    ML = dh,
    _L = tL,
    jL = TypeError,
    LL = function (e, t) {
      var n = e.exec;
      if (AL(n)) {
        var r = kL(n, e, t);
        return null !== r && IL(r), r;
      }
      if ("RegExp" === ML(e)) return kL(_L, e, t);
      throw jL("RegExp#exec called on incompatible receiver");
    },
    PL = uR,
    DL = mm,
    FL = op,
    HL = sL,
    BL = Jd,
    UL = fm,
    zL = Ep,
    GL = ap,
    VL = Dh,
    YL = Bh,
    WL = sN,
    XL = up,
    qL = EL,
    $L = km,
    KL = function (e, t, n, r, o, a) {
      var i = n + e.length,
        c = r.length,
        u = RL;
      return (
        void 0 !== o && ((o = xL(o)), (u = OL)),
        SL(a, u, function (a, u) {
          var l;
          switch (TL(u, 0)) {
            case "$":
              return "$";
            case "&":
              return e;
            case "`":
              return CL(t, 0, n);
            case "'":
              return CL(t, i);
            case "<":
              l = o[CL(u, 1, -1)];
              break;
            default:
              var s = +u;
              if (0 === s) return a;
              if (s > c) {
                var f = NL(s / 10);
                return 0 === f
                  ? a
                  : f <= c
                    ? void 0 === r[f - 1]
                      ? TL(u, 1)
                      : r[f - 1] + TL(u, 1)
                    : a;
              }
              l = r[s - 1];
          }
          return void 0 === l ? "" : l;
        })
      );
    },
    JL = LL,
    QL = Xp("replace"),
    ZL = Math.max,
    eP = Math.min,
    tP = FL([].concat),
    nP = FL([].push),
    rP = FL("".indexOf),
    oP = FL("".slice),
    aP = "$0" === "a".replace(/./, "$0"),
    iP = !!/./[QL] && "" === /./[QL]("a", "$0");
  HL(
    "replace",
    function (e, t, n) {
      var r = iP ? "$" : "$0";
      return [
        function (e, n) {
          var r = XL(this),
            o = GL(e) ? void 0 : $L(e, QL);
          return o ? DL(o, e, r, n) : DL(t, WL(r), e, n);
        },
        function (e, o) {
          var a = UL(this),
            i = WL(e);
          if ("string" == typeof o && -1 === rP(o, r) && -1 === rP(o, "$<")) {
            var c = n(t, a, i, o);
            if (c.done) return c.value;
          }
          var u = zL(o);
          u || (o = WL(o));
          var l = a.global;
          if (l) {
            var s = a.unicode;
            a.lastIndex = 0;
          }
          for (var f = []; ; ) {
            var d = JL(a, i);
            if (null === d) break;
            if ((nP(f, d), !l)) break;
            "" === WL(d[0]) && (a.lastIndex = qL(i, YL(a.lastIndex), s));
          }
          for (var p, m = "", v = 0, h = 0; h < f.length; h++) {
            for (
              var g = WL((d = f[h])[0]),
                y = ZL(eP(VL(d.index), i.length), 0),
                b = [],
                E = 1;
              E < d.length;
              E++
            )
              nP(b, void 0 === (p = d[E]) ? p : String(p));
            var w = d.groups;
            if (u) {
              var x = tP([g], b, y, i);
              void 0 !== w && nP(x, w);
              var N = WL(PL(o, void 0, x));
            } else N = KL(g, i, y, b, w, o);
            y >= v && ((m += oP(i, v, y) + N), (v = y + g.length));
          }
          return m + oP(i, v);
        },
      ];
    },
    !!BL(function () {
      var e = /./;
      return (
        (e.exec = function () {
          var e = [];
          return (e.groups = { a: "7" }), e;
        }),
        "7" !== "".replace(e, "$<a>")
      );
    }) ||
      !aP ||
      iP,
  );
  var cP = [
    "className",
    "type",
    "variant",
    "value",
    "placeholder",
    "rows",
    "minRows",
    "maxRows",
    "maxLength",
    "showCount",
    "multiline",
    "autoSize",
    "onChange",
  ];
  var uP = o.default.forwardRef(function (e, n) {
      var r = e.className,
        a = e.type,
        i = void 0 === a ? "text" : a,
        u = e.variant,
        l = e.value,
        s = e.placeholder,
        f = e.rows,
        d = void 0 === f ? 1 : f,
        p = e.minRows,
        m = void 0 === p ? d : p,
        v = e.maxRows,
        h = void 0 === v ? 5 : v,
        g = e.maxLength,
        y = e.showCount,
        b = void 0 === y ? !!g : y,
        E = e.multiline,
        w = e.autoSize,
        x = e.onChange,
        N = YT(e, cP),
        T = d;
      T < m ? (T = m) : T > h && (T = h);
      var S = _x(t.useState(T), 2),
        C = S[0],
        O = S[1],
        R = _x(t.useState(21), 2),
        k = R[0],
        I = R[1],
        A = Fd(n),
        M = t.useContext(e_),
        _ = u || ("light" === M ? "flushed" : "outline"),
        j = E || w || d > 1 ? "textarea" : "input";
      t.useEffect(
        function () {
          if (A.current) {
            var e = getComputedStyle(A.current, null).lineHeight,
              t = Number(e.replace("px", ""));
            t !== k && I(t);
          }
        },
        [A, k],
      );
      var L = t.useCallback(
        function () {
          if (w && A.current) {
            var e = A.current,
              t = e.rows;
            (e.rows = m), s && (e.placeholder = "");
            var n = ~~(e.scrollHeight / k);
            n === t && (e.rows = n),
              n >= h && ((e.rows = h), (e.scrollTop = e.scrollHeight)),
              O(n < h ? n : h),
              s && (e.placeholder = s);
          }
        },
        [w, A, k, h, m, s],
      );
      t.useEffect(
        function () {
          "" === l ? O(T) : L();
        },
        [T, L, l],
      );
      var P = t.useCallback(
          function (e) {
            if ((L(), x)) {
              var t = e.target.value,
                n = g && t.length > g ? t.substr(0, g) : t;
              x(n, e);
            }
          },
          [g, x, L],
        ),
        D = o.default.createElement(
          j,
          bT(
            {
              className: c("Input", "Input--".concat(_), r),
              type: i,
              value: l,
              placeholder: s,
              maxLength: g,
              ref: A,
              rows: C,
              onChange: P,
            },
            N,
          ),
        );
      return b
        ? o.default.createElement(
            "div",
            { className: c("InputWrapper", { "has-counter": b }) },
            D,
            b &&
              o.default.createElement(
                "div",
                { className: "Input-counter" },
                (function (e, t) {
                  var n;
                  return Zx((n = "".concat("".concat(e).length))).call(
                    n,
                    t ? "/".concat(t) : "",
                  );
                })(l, g),
              ),
          )
        : D;
    }),
    lP = ["className", "as", "content", "rightIcon", "children", "onClick"],
    sP = em,
    fP = dh,
    dP = Xp("match"),
    pP = Vm,
    mP = Kp,
    vP = bv,
    hP = BC,
    gP = zh,
    yP = function (e, t, n) {
      var r = pP(t);
      r in e ? mP.f(e, r, vP(0, n)) : (e[r] = n);
    },
    bP = Array,
    EP = Math.max,
    wP = uR,
    xP = mm,
    NP = op,
    TP = sL,
    SP = fm,
    CP = ap,
    OP = function (e) {
      var t;
      return sP(e) && (void 0 !== (t = e[dP]) ? !!t : "RegExp" == fP(e));
    },
    RP = up,
    kP = rR,
    IP = EL,
    AP = Bh,
    MP = sN,
    _P = km,
    jP = function (e, t, n) {
      for (
        var r = gP(e),
          o = hP(t, r),
          a = hP(void 0 === n ? r : n, r),
          i = bP(EP(a - o, 0)),
          c = 0;
        o < a;
        o++, c++
      )
        yP(i, c, e[o]);
      return (i.length = c), i;
    },
    LP = LL,
    PP = tL,
    DP = Jd,
    FP = rj.UNSUPPORTED_Y,
    HP = 4294967295,
    BP = Math.min,
    UP = [].push,
    zP = NP(/./.exec),
    GP = NP(UP),
    VP = NP("".slice),
    YP = !DP(function () {
      var e = /(?:)/,
        t = e.exec;
      e.exec = function () {
        return t.apply(this, arguments);
      };
      var n = "ab".split(e);
      return 2 !== n.length || "a" !== n[0] || "b" !== n[1];
    });
  TP(
    "split",
    function (e, t, n) {
      var r;
      return (
        (r =
          "c" == "abbc".split(/(b)*/)[1] ||
          4 != "test".split(/(?:)/, -1).length ||
          2 != "ab".split(/(?:ab)*/).length ||
          4 != ".".split(/(.?)(.?)/).length ||
          ".".split(/()()/).length > 1 ||
          "".split(/.?/).length
            ? function (e, n) {
                var r = MP(RP(this)),
                  o = void 0 === n ? HP : n >>> 0;
                if (0 === o) return [];
                if (void 0 === e) return [r];
                if (!OP(e)) return xP(t, r, e, o);
                for (
                  var a,
                    i,
                    c,
                    u = [],
                    l =
                      (e.ignoreCase ? "i" : "") +
                      (e.multiline ? "m" : "") +
                      (e.unicode ? "u" : "") +
                      (e.sticky ? "y" : ""),
                    s = 0,
                    f = new RegExp(e.source, l + "g");
                  (a = xP(PP, f, r)) &&
                  !(
                    (i = f.lastIndex) > s &&
                    (GP(u, VP(r, s, a.index)),
                    a.length > 1 && a.index < r.length && wP(UP, u, jP(a, 1)),
                    (c = a[0].length),
                    (s = i),
                    u.length >= o)
                  );

                )
                  f.lastIndex === a.index && f.lastIndex++;
                return (
                  s === r.length
                    ? (!c && zP(f, "")) || GP(u, "")
                    : GP(u, VP(r, s)),
                  u.length > o ? jP(u, 0, o) : u
                );
              }
            : "0".split(void 0, 0).length
              ? function (e, n) {
                  return void 0 === e && 0 === n ? [] : xP(t, this, e, n);
                }
              : t),
        [
          function (t, n) {
            var o = RP(this),
              a = CP(t) ? void 0 : _P(t, e);
            return a ? xP(a, t, o, n) : xP(r, MP(o), t, n);
          },
          function (e, o) {
            var a = SP(this),
              i = MP(e),
              c = n(r, a, i, o, r !== t);
            if (c.done) return c.value;
            var u = kP(a, RegExp),
              l = a.unicode,
              s =
                (a.ignoreCase ? "i" : "") +
                (a.multiline ? "m" : "") +
                (a.unicode ? "u" : "") +
                (FP ? "g" : "y"),
              f = new u(FP ? "^(?:" + a.source + ")" : a, s),
              d = void 0 === o ? HP : o >>> 0;
            if (0 === d) return [];
            if (0 === i.length) return null === LP(f, i) ? [i] : [];
            for (var p = 0, m = 0, v = []; m < i.length; ) {
              f.lastIndex = FP ? 0 : m;
              var h,
                g = LP(f, FP ? VP(i, m) : i);
              if (
                null === g ||
                (h = BP(AP(f.lastIndex + (FP ? m : 0)), i.length)) === p
              )
                m = IP(i, m, l);
              else {
                if ((GP(v, VP(i, p, m)), v.length === d)) return v;
                for (var y = 1; y <= g.length - 1; y++)
                  if ((GP(v, g[y]), v.length === d)) return v;
                m = p = h;
              }
            }
            return GP(v, VP(i, p)), v;
          },
        ]
      );
    },
    !YP,
    FP,
  );
  var WP = {
      BackBottom: {
        newMsgOne: "{n} رسالة جديدة",
        newMsgOther: "{n} رسالة جديدة",
        bottom: "الأسفل",
      },
      Time: {
        weekdays: "الأحد_الإثنين_الثلاثاء_الأربعاء_الخميس_الجمعة_السبت".split(
          "_",
        ),
        formats: {
          LT: "HH:mm",
          lll: "YYYY/M/D HH:mm",
          WT: "HH:mm dddd",
          YT: "HH:mm أمس",
        },
      },
      Composer: { send: "إرسال" },
      SendConfirm: { title: "إرسال صورة", send: "أرسل", cancel: "إلغاء" },
      RateActions: { up: "التصويت", down: "تصويت سلبي" },
      Recorder: {
        hold2talk: "أستمر بالضغط لتتحدث",
        release2send: "حرر للإرسال",
        releaseOrSwipe: "حرر للإرسال ، اسحب لأعلى للإلغاء",
        release2cancel: "حرر للإلغاء",
      },
      Search: { search: "يبحث" },
    },
    XP = {
      BackBottom: {
        newMsgOne: "{n} new message",
        newMsgOther: "{n} new messages",
        bottom: "Bottom",
      },
      Time: {
        weekdays:
          "Sunday_Monday_Tuesday_Wednesday_Thursday_Friday_Saturday".split("_"),
        formats: {
          LT: "HH:mm",
          lll: "M/D/YYYY HH:mm",
          WT: "dddd HH:mm",
          YT: "Yesterday HH:mm",
        },
      },
      Composer: { send: "Send" },
      SendConfirm: { title: "Send photo", send: "Send", cancel: "Cancel" },
      RateActions: { up: "Up vote", down: "Down vote" },
      Recorder: {
        hold2talk: "Hold to Talk",
        release2send: "Release to Send",
        releaseOrSwipe: "Release to send, swipe up to cancel",
        release2cancel: "Release to cancel",
      },
      Search: { search: "Search" },
    },
    qP = {
      "ar-EG": WP,
      "fr-FR": {
        BackBottom: {
          newMsgOne: "{n} nouveau message",
          newMsgOther: "{n} nouveau messages",
          bottom: "Fond",
        },
        Time: {
          weekdays: "Dimanche_Lundi_Mardi_Mercredi_Jeudi_Vendredi_Samedi".split(
            "_",
          ),
          formats: {
            LT: "HH:mm",
            lll: "D/M/YYYY HH:mm",
            WT: "dddd HH:mm",
            YT: "Hier HH:mm",
          },
        },
        Composer: { send: "Envoyer" },
        SendConfirm: {
          title: "Envoyer une photo",
          send: "Envoyer",
          cancel: "Annuler",
        },
        RateActions: { up: "Voter pour", down: "Vote négatif" },
        Recorder: {
          hold2talk: "Tenir pour parler",
          release2send: "Libérer pour envoyer",
          releaseOrSwipe:
            "Relâchez pour envoyer, balayez vers le haut pour annuler",
          release2cancel: "Relâcher pour annuler",
        },
        Search: { search: "Chercher" },
      },
      "en-US": XP,
      "zh-CN": {
        BackBottom: {
          newMsgOne: "{n}条新消息",
          newMsgOther: "{n}条新消息",
          bottom: "回到底部",
        },
        Time: {
          weekdays: "星期日_星期一_星期二_星期三_星期四_星期五_星期六".split(
            "_",
          ),
          formats: {
            LT: "HH:mm",
            lll: "YYYY年M月D日 HH:mm",
            WT: "dddd HH:mm",
            YT: "昨天 HH:mm",
          },
        },
        Composer: { send: "发送" },
        SendConfirm: { title: "发送图片", send: "发送", cancel: "取消" },
        RateActions: { up: "赞同", down: "反对" },
        Recorder: {
          hold2talk: "按住 说话",
          release2send: "松开 发送",
          releaseOrSwipe: "松开发送，上滑取消",
          release2cancel: "松开手指，取消发送",
        },
        Search: { search: "搜索" },
      },
    };
  function $P(e, t) {
    var n = jg(e);
    if (wE) {
      var r = wE(e);
      t &&
        (r = cN(r).call(r, function (t) {
          return _E(e, t).enumerable;
        })),
        n.push.apply(n, r);
    }
    return n;
  }
  function KP(e) {
    for (var t = 1; t < arguments.length; t++) {
      var n = null != arguments[t] ? arguments[t] : {};
      t % 2
        ? $P(Object(n), !0).forEach(function (t) {
            Fx(e, t, n[t]);
          })
        : BE
          ? $E(e, BE(n))
          : $P(Object(n)).forEach(function (t) {
              ow(e, t, _E(n, t));
            });
    }
    return e;
  }
  var JP = o.default.createContext(void 0),
    QP = "en-US",
    ZP = function (e) {
      var t = e.locale,
        n = e.locales,
        r = e.children;
      return o.default.createElement(
        JP.Provider,
        { value: { locale: t, locales: n } },
        r,
      );
    };
  ZP.defaultProps = { locale: QP };
  var eD = function (e, n) {
      var r = t.useContext(JP),
        o = r || {},
        a = o.locale,
        i = o.locales,
        c = (a && qP[a]) || qP["en-US"],
        u = i ? KP(KP({}, c), i) : c;
      return (
        !r && n ? (u = n) : e && (u = u[e] || {}),
        {
          locale: a,
          trans: function (e) {
            return e ? u[e] : u;
          },
        }
      );
    },
    tD = function (e) {
      var t = e.className,
        n = e.content,
        r = e.action;
      return o.default.createElement(
        "div",
        { className: c("Message SystemMessage", t) },
        o.default.createElement(
          "div",
          { className: "SystemMessage-inner" },
          o.default.createElement("span", null, n),
          r &&
            o.default.createElement(
              "a",
              { href: "javascript:;", onClick: r.onClick },
              r.text,
            ),
        ),
      );
    },
    nD = fp,
    rD = Um;
  MO(
    {
      target: "Date",
      proto: !0,
      arity: 1,
      forced: Jd(function () {
        return (
          null !== new Date(NaN).toJSON() ||
          1 !==
            Date.prototype.toJSON.call({
              toISOString: function () {
                return 1;
              },
            })
        );
      }),
    },
    {
      toJSON: function (e) {
        var t = nD(this),
          n = rD(t, "number");
        return "number" != typeof n || isFinite(n) ? t.toISOString() : null;
      },
    },
  );
  var oD = mm;
  MO(
    { target: "URL", proto: !0, enumerable: !0 },
    {
      toJSON: function () {
        return oD(URL.prototype.toString, this);
      },
    },
  );
  var aD = /YYYY|M|D|dddd|HH|mm/g,
    iD = 864e5,
    cD = function (e) {
      return (e <= 9 ? "0" : "") + e;
    },
    uD = function (e) {
      var t = new Date(new Date().setHours(0, 0, 0, 0)).getTime() - e.getTime();
      return t < 0 ? "LT" : t < iD ? "YT" : t < 6048e5 ? "WT" : "lll";
    };
  function lD(e, t) {
    var n = (function (e) {
        return e instanceof Date ? e : new Date(e);
      })(e),
      r = t.formats[uD(n)],
      o = {
        YYYY: "".concat(n.getFullYear()),
        M: "".concat(n.getMonth() + 1),
        D: "".concat(n.getDate()),
        dddd: t.weekdays[n.getDay()],
        HH: cD(n.getHours()),
        mm: cD(n.getMinutes()),
      };
    return r.replace(aD, function (e) {
      return o[e];
    });
  }
  var sD = function (e) {
    var t = e.date,
      n = eD("Time").trans;
    return o.default.createElement(
      "time",
      { className: "Time", dateTime: new Date(t).toJSON() },
      lD(t, n()),
    );
  };
  function fD() {
    return o.default.createElement(
      KS,
      { type: "typing" },
      o.default.createElement(
        "div",
        { className: "Typing", "aria-busy": "true" },
        o.default.createElement("div", { className: "Typing-dot" }),
        o.default.createElement("div", { className: "Typing-dot" }),
        o.default.createElement("div", { className: "Typing-dot" }),
      ),
    );
  }
  var dD = ["renderMessageContent"],
    pD = function (e) {
      var t = e.renderMessageContent,
        n =
          void 0 === t
            ? function () {
                return null;
              }
            : t,
        r = YT(e, dD),
        a = r.type,
        i = r.content,
        u = r.user,
        l = void 0 === u ? {} : u,
        s = r._id,
        f = r.position,
        d = void 0 === f ? "left" : f,
        p = r.hasTime,
        m = void 0 === p || p,
        v = r.createdAt,
        h = l.name,
        g = l.avatar;
      if ("system" === a)
        return o.default.createElement(tD, {
          content: i.text,
          action: i.action,
        });
      var y = "right" === d || "left" === d;
      return o.default.createElement(
        "div",
        { className: c("Message", d), "data-id": s, "data-type": a },
        m &&
          v &&
          o.default.createElement(
            "div",
            { className: "Message-meta" },
            o.default.createElement(sD, { date: v }),
          ),
        o.default.createElement(
          "div",
          { className: "Message-main" },
          y && g && o.default.createElement(WS, { src: g, alt: h, url: l.url }),
          o.default.createElement(
            "div",
            { className: "Message-inner" },
            y &&
              h &&
              o.default.createElement(
                "div",
                { className: "Message-author" },
                h,
              ),
            o.default.createElement(
              "div",
              {
                className: "Message-content",
                role: "alert",
                "aria-live": "assertive",
                "aria-atomic": "false",
              },
              "typing" === a ? o.default.createElement(fD, null) : n(r),
            ),
          ),
        ),
      );
    },
    mD = o.default.memo(pD),
    vD = 0,
    hD = function () {
      return vD++;
    };
  function gD() {
    var e,
      n =
        arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : "id-",
      r = t.useRef(Zx((e = "".concat(n))).call(e, hD()));
    return r.current;
  }
  var yD = function (e, t) {
    var n =
      arguments.length > 2 && void 0 !== arguments[2]
        ? arguments[2]
        : document.body;
    n.classList[t ? "add" : "remove"](e);
  };
  function bD() {
    document.querySelector(".Modal") ||
      document.querySelector(".Popup") ||
      yD("S--modalOpen", !1);
  }
  var ED = function (e) {
      var r,
        a,
        i = e.baseClass,
        u = e.active,
        l = e.className,
        s = e.title,
        f = e.showClose,
        d = void 0 === f || f,
        p = e.autoFocus,
        m = void 0 === p || p,
        v = e.backdrop,
        h = void 0 === v || v,
        g = e.height,
        y = e.overflow,
        b = e.actions,
        E = e.vertical,
        w = void 0 === E || E,
        x = e.btnVariant,
        N = e.bgColor,
        T = e.children,
        S = e.onBackdropClick,
        C = e.onClose,
        O = gD("modal-"),
        R = e.titleId || O,
        k = t.useRef(null),
        I = _N({ active: u, ref: k }),
        A = I.didMount,
        M = I.isShow;
      if (
        (t.useEffect(
          function () {
            setTimeout(function () {
              m && k.current && k.current.focus();
            });
          },
          [m],
        ),
        t.useEffect(
          function () {
            M && yD("S--modalOpen", M);
          },
          [M],
        ),
        t.useEffect(
          function () {
            u || A || bD();
          },
          [u, A],
        ),
        t.useEffect(function () {
          return function () {
            bD();
          };
        }, []),
        !A)
      )
        return null;
      var _ = "Popup" === i;
      return n.createPortal(
        o.default.createElement(
          "div",
          { className: c(i, l, { active: M }), ref: k, tabIndex: -1 },
          h &&
            o.default.createElement(qS, {
              active: M,
              onClick: !0 === h ? S || C : void 0,
            }),
          o.default.createElement(
            "div",
            {
              className: c("".concat(i, "-dialog"), { "pb-safe": _ && !b }),
              "data-bg-color": N,
              "data-height": _ && g ? g : void 0,
              role: "dialog",
              "aria-labelledby": R,
              "aria-modal": !0,
            },
            o.default.createElement(
              "div",
              { className: "".concat(i, "-content") },
              o.default.createElement(
                "div",
                { className: "".concat(i, "-header") },
                o.default.createElement(
                  "h5",
                  { className: "".concat(i, "-title"), id: R },
                  s,
                ),
                d &&
                  C &&
                  o.default.createElement(c_, {
                    className: "".concat(i, "-close"),
                    icon: "close",
                    size: "lg",
                    onClick: C,
                    "aria-label": "关闭",
                  }),
              ),
              o.default.createElement(
                "div",
                { className: c("".concat(i, "-body"), { overflow: y }) },
                T,
              ),
              b &&
                o.default.createElement(
                  "div",
                  {
                    className: Zx(
                      (r = Zx((a = "".concat(i, "-footer "))).call(
                        a,
                        i,
                        "-footer--",
                      )),
                    ).call(r, w ? "v" : "h"),
                    "data-variant": x || "round",
                  },
                  Wx(b).call(b, function (e) {
                    return o.default.createElement(
                      tC,
                      bT({ size: "lg", block: _, variant: x }, e, {
                        key: e.label,
                      }),
                    );
                  }),
                ),
            ),
          ),
        ),
        document.body,
      );
    },
    wD = function (e) {
      return o.default.createElement(
        ED,
        bT(
          {
            baseClass: "Modal",
            btnVariant: !1 === e.vertical ? void 0 : "outline",
          },
          e,
        ),
      );
    },
    xD = ["className"],
    ND = function (e) {
      var t = e.className,
        n = e.title,
        r = e.logo,
        a = e.leftContent,
        i = e.rightContent,
        u = void 0 === i ? [] : i;
      return o.default.createElement(
        "header",
        { className: c("Navbar", t) },
        o.default.createElement(
          "div",
          { className: "Navbar-left" },
          a && o.default.createElement(c_, bT({ size: "lg" }, a)),
        ),
        o.default.createElement(
          "div",
          { className: "Navbar-main" },
          r
            ? o.default.createElement("img", {
                className: "Navbar-logo",
                src: r,
                alt: n,
              })
            : o.default.createElement("h2", { className: "Navbar-title" }, n),
        ),
        o.default.createElement(
          "div",
          { className: "Navbar-right" },
          Wx(u).call(u, function (e) {
            return o.default.createElement(
              c_,
              bT({ size: "lg" }, e, { key: e.icon }),
            );
          }),
        ),
      );
    },
    TD = { exports: {} },
    SD = rt,
    CD = Math.floor;
  Mr(
    { target: "Number", stat: !0 },
    {
      isInteger:
        Number.isInteger ||
        function (e) {
          return !SD(e) && isFinite(e) && CD(e) === e;
        },
    },
  );
  var OD = ot.Number.isInteger;
  !(function (e) {
    e.exports = OD;
  })(TD);
  var RD = le(TD.exports),
    kD = ["as", "className", "align", "breakWord", "truncate", "children"],
    ID = function (e) {
      var t = e.as,
        n = void 0 === t ? "div" : t,
        r = e.className,
        a = e.align,
        i = e.breakWord,
        u = e.truncate,
        l = e.children,
        s = YT(e, kD),
        f = RD(u),
        d = c(
          a && "Text--".concat(a),
          { "Text--break": i, "Text--truncate": !0 === u, "Text--ellipsis": f },
          r,
        ),
        p = f ? { WebkitLineClamp: u } : null;
      return o.default.createElement(n, bT({ className: d, style: p }, s), l);
    };
  var AD = ["className", "price", "currency", "locale", "original"],
    MD =
      "Intl" in window &&
      "function" == typeof Intl.NumberFormat.prototype.formatToParts,
    _D = o.default.forwardRef(function (e, t) {
      var n = e.className,
        r = e.price,
        a = e.currency,
        i = e.locale,
        u = e.original,
        l = YT(e, AD),
        s = [];
      if (
        !(s =
          i && a && MD
            ? new Intl.NumberFormat(i, {
                style: "currency",
                currency: a,
              }).formatToParts(r)
            : void 0)
      ) {
        var f = _x("".concat(r).split("."), 2),
          d = f[0],
          p = f[1];
        s = [
          { type: "currency", value: a },
          { type: "integer", value: d },
          { type: "decimal", value: p && "." },
          { type: "fraction", value: p },
        ];
      }
      return o.default.createElement(
        "div",
        bT({ className: c("Price", { "Price--original": u }, n), ref: t }, l),
        Wx(s).call(s, function (e, t) {
          return e.value
            ? o.default.createElement(
                "span",
                { className: "Price-".concat(e.type), key: t },
                e.value,
              )
            : null;
        }),
      );
    }),
    jD = ["className", "value", "status", "children"],
    LD = o.default.forwardRef(function (e, t) {
      var n = e.className,
        r = e.value,
        a = e.status,
        i = e.children,
        u = YT(e, jD);
      return o.default.createElement(
        "div",
        bT(
          { className: c("Progress", a && "Progress--".concat(a), n), ref: t },
          u,
        ),
        o.default.createElement(
          "div",
          {
            className: "Progress-bar",
            role: "progressbar",
            style: { width: "".concat(r, "%") },
            "aria-valuenow": r,
            "aria-valuemin": 0,
            "aria-valuemax": 100,
          },
          i,
        ),
      );
    }),
    PD = requestAnimationFrame;
  function DD(e) {
    var t = e.el,
      n = e.to,
      r = e.duration,
      o = void 0 === r ? 300 : r,
      a = e.x,
      i = 0,
      c = a ? "scrollLeft" : "scrollTop",
      u = t[c],
      l = Math.round(o / 16),
      s = (n - u) / l;
    PD
      ? (function e() {
          (t[c] += s), ++i < l && PD(e);
        })()
      : (t[c] = n);
  }
  var FD = zM("passiveListener"),
    HD = !!FD && { passive: !0 },
    BD = !!FD && { passive: !1 },
    UD = o.default.forwardRef(function (e, n) {
      var r = e.distance,
        a = void 0 === r ? 30 : r,
        i = e.loadingDistance,
        u = void 0 === i ? 30 : i,
        l = e.maxDistance,
        s = e.distanceRatio,
        f = void 0 === s ? 2 : s,
        d = e.loadMoreText,
        p = void 0 === d ? "点击加载更多" : d,
        m = e.children,
        v = e.onScroll,
        h = e.onRefresh,
        g = e.renderIndicator,
        y =
          void 0 === g
            ? function (e) {
                return "active" === e || "loading" === e
                  ? o.default.createElement(QS, {
                      className: "PullToRefresh-spinner",
                      type: "spinner",
                      spin: !0,
                    })
                  : null;
              }
            : g,
        b = t.useRef(null),
        E = t.useRef(null),
        w = _x(t.useState(0), 2),
        x = w[0],
        N = w[1],
        T = _x(t.useState("pending"), 2),
        S = T[0],
        C = T[1],
        O = _x(t.useState(!1), 2),
        R = O[0],
        k = O[1],
        I = _x(t.useState(!e.onRefresh), 2),
        A = I[0],
        M = I[1],
        _ = t.useRef({}),
        j = t.useRef(S),
        L = t.useRef(),
        P = t.useRef(),
        D = !zM("touch");
      t.useEffect(
        function () {
          j.current = S;
        },
        [S],
      );
      var F = function (e) {
          var t = E.current;
          t && HM(t, "translate3d(0px,".concat(e, "px,0)"));
        },
        H = function (e) {
          var t = e.y,
            n = e.animated,
            r = void 0 === n || n,
            o = b.current;
          if (o) {
            var a = "100%" === t ? o.scrollHeight - o.offsetHeight : t;
            r ? DD({ el: o, to: a, x: !1 }) : (o.scrollTop = a);
          }
        },
        B = t.useCallback(function () {
          var e =
              arguments.length > 0 && void 0 !== arguments[0]
                ? arguments[0]
                : {},
            t = e.animated,
            n = void 0 === t || t;
          H({ y: "100%", animated: n });
        }, []),
        U = t.useCallback(function () {
          N(0), C("pending"), F(0);
        }, []),
        z = t.useCallback(
          function () {
            var e = b.current;
            if (e) {
              C("loading");
              try {
                var t = e.scrollHeight;
                h().then(function (n) {
                  var r = function () {
                    H({ y: e.scrollHeight - t - 50, animated: !1 });
                  };
                  clearTimeout(L.current),
                    clearTimeout(P.current),
                    r(),
                    (L.current = setTimeout(r, 150)),
                    (P.current = setTimeout(r, 250)),
                    U(),
                    n && n.noMore && M(!0);
                });
              } catch (e) {
                U();
              }
            }
          },
          [h, U],
        ),
        G = function (e) {
          (_.current.startY = e.touches[0].clientY),
            (_.current.canPull = b.current && b.current.scrollTop <= 0),
            _.current.canPull && (C("pull"), k(!1));
        },
        V = t.useCallback(
          function (e) {
            var t = e.touches[0].clientY,
              n = _.current,
              r = n.canPull,
              o = n.startY;
            if (r && !(t < o) && "loading" !== j.current) {
              var i = (t - o) / f;
              l && i > l && (i = l),
                i > 0 &&
                  (e.cancelable && e.preventDefault(),
                  e.stopPropagation(),
                  F(i),
                  N(i),
                  C(i >= a ? "active" : "pull"));
            }
          },
          [f, l, a],
        ),
        Y = t.useCallback(
          function () {
            k(!0), "active" === j.current ? z() : U();
          },
          [z, U],
        );
      return (
        t.useEffect(
          function () {
            var e = b.current;
            e &&
              !D &&
              (A
                ? (e.removeEventListener("touchstart", G),
                  e.removeEventListener("touchmove", V),
                  e.removeEventListener("touchend", Y),
                  e.removeEventListener("touchcancel", Y))
                : (e.addEventListener("touchstart", G, HD),
                  e.addEventListener("touchmove", V, BD),
                  e.addEventListener("touchend", Y),
                  e.addEventListener("touchcancel", Y)));
          },
          [A, Y, V, D],
        ),
        t.useEffect(
          function () {
            "loading" !== S || D || F(u);
          },
          [u, S, D],
        ),
        t.useImperativeHandle(
          n,
          function () {
            return { scrollTo: H, scrollToEnd: B, wrapperRef: b };
          },
          [B],
        ),
        o.default.createElement(
          "div",
          { className: "PullToRefresh", ref: b, onScroll: v },
          o.default.createElement(
            "div",
            { className: "PullToRefresh-inner" },
            o.default.createElement(
              "div",
              {
                className: c("PullToRefresh-content", {
                  "PullToRefresh-transition": R,
                }),
                ref: E,
              },
              o.default.createElement(
                "div",
                { className: "PullToRefresh-indicator" },
                y(S, x),
              ),
              !A &&
                D &&
                o.default.createElement(
                  lC,
                  { className: "PullToRefresh-fallback", center: !0 },
                  y(S, a),
                  o.default.createElement(
                    tC,
                    {
                      className: "PullToRefresh-loadMore",
                      variant: "text",
                      onClick: z,
                    },
                    p,
                  ),
                ),
              o.default.Children.only(m),
            ),
          ),
        )
      );
    }),
    zD = { threshold: [0, 0.1] },
    GD = function (e) {
      var n = e.item,
        r = e.effect,
        a = e.children,
        i = e.onIntersect,
        u = t.useRef(null);
      return (
        t.useEffect(
          function () {
            if (i) {
              var e = new IntersectionObserver(function (t) {
                var r = _x(t, 1)[0];
                r.intersectionRatio > 0 && (i(n, r) || e.unobserve(r.target));
              }, zD);
              return (
                u.current && e.observe(u.current),
                function () {
                  e.disconnect();
                }
              );
            }
          },
          [n, i],
        ),
        o.default.createElement(
          "div",
          {
            className: c("ScrollView-item", {
              "slide-in-right-item": "slide" === r,
              "A-fadeIn": "fade" === r,
            }),
            ref: u,
          },
          a,
        )
      );
    },
    VD = [
      "className",
      "fullWidth",
      "scrollX",
      "effect",
      "data",
      "itemKey",
      "renderItem",
      "onIntersect",
      "onScroll",
      "children",
    ],
    YD = !zM("touch"),
    WD = o.default.forwardRef(function (e, n) {
      var r = e.className,
        a = e.fullWidth,
        i = e.scrollX,
        u = void 0 === i || i,
        l = e.effect,
        s = void 0 === l ? "slide" : l,
        f = e.data,
        d = e.itemKey,
        p = e.renderItem,
        m = e.onIntersect,
        v = e.onScroll,
        h = e.children,
        g = YT(e, VD),
        y = t.useRef(null);
      var b = t.useCallback(
        function (e, t) {
          var n;
          return d && (n = "function" == typeof d ? d(e, t) : e[d]), n || t;
        },
        [d],
      );
      return (
        t.useImperativeHandle(n, function () {
          return {
            scrollTo: function (e) {
              var t = e.x,
                n = e.y;
              null != t && (y.current.scrollLeft = t),
                null != n && (y.current.scrollTop = n);
            },
          };
        }),
        o.default.createElement(
          "div",
          bT(
            {
              className: c(
                "ScrollView",
                {
                  "ScrollView--fullWidth": a,
                  "ScrollView--x": u,
                  "ScrollView--hasControls": YD,
                },
                r,
              ),
              ref: n,
            },
            g,
          ),
          YD &&
            o.default.createElement(c_, {
              className: "ScrollView-control",
              icon: "chevron-left",
              "aria-label": "Previous",
              onClick: function () {
                var e = y.current;
                e.scrollLeft -= e.offsetWidth;
              },
            }),
          o.default.createElement(
            "div",
            { className: "ScrollView-scroller", ref: y, onScroll: v },
            o.default.createElement(
              "div",
              { className: "ScrollView-inner" },
              Wx(f).call(f, function (e, t) {
                return o.default.createElement(
                  GD,
                  {
                    item: e,
                    effect: e.effect || s,
                    onIntersect: m,
                    key: b(e, t),
                  },
                  p(e, t),
                );
              }),
              h
                ? o.default.createElement(
                    GD,
                    { item: {}, effect: s, onIntersect: m },
                    h,
                  )
                : null,
            ),
          ),
          YD &&
            o.default.createElement(c_, {
              className: "ScrollView-control",
              icon: "chevron-right",
              "aria-label": "Next",
              onClick: function () {
                var e = y.current;
                e.scrollLeft += e.offsetWidth;
              },
            }),
        )
      );
    }),
    XD = function (e) {
      var t = e.item,
        n = e.index,
        r = e.onClick;
      return o.default.createElement(
        "button",
        {
          className: c("QuickReply", {
            new: t.isNew,
            highlight: t.isHighlight,
          }),
          type: "button",
          "data-code": t.code,
          "aria-label": "快捷短语: ".concat(t.name, "，双击发送"),
          onClick: function () {
            r(t, n);
          },
        },
        o.default.createElement(
          "div",
          { className: "QuickReply-inner" },
          t.icon && o.default.createElement(QS, { type: t.icon }),
          t.img &&
            o.default.createElement("img", {
              className: "QuickReply-img",
              src: t.img,
              alt: "",
            }),
          o.default.createElement("span", null, t.name),
        ),
      );
    },
    qD = function (e) {
      var n = e.items,
        r = e.visible,
        a = e.onClick,
        i = e.onScroll,
        c = t.useRef(null),
        u = _x(t.useState(!!i), 2),
        l = u[0],
        s = u[1];
      return (
        t.useLayoutEffect(
          function () {
            var e;
            return (
              c.current &&
                (s(!1),
                c.current.scrollTo({ x: 0, y: 0 }),
                (e = setTimeout(function () {
                  s(!0);
                }, 500))),
              function () {
                clearTimeout(e);
              }
            );
          },
          [n],
        ),
        n.length
          ? o.default.createElement(WD, {
              className: "QuickReplies",
              data: n,
              itemKey: "name",
              ref: c,
              "data-visible": r,
              onScroll: l ? i : void 0,
              renderItem: function (e, t) {
                return o.default.createElement(XD, {
                  item: e,
                  index: t,
                  onClick: a,
                  key: e.name,
                });
              },
            })
          : null
      );
    };
  qD.defaultProps = { items: [], visible: !0 };
  var $D = o.default.memo(qD),
    KD = ["className", "label", "checked", "disabled", "onChange"],
    JD = function (e) {
      var t = e.className,
        n = e.label,
        r = e.checked,
        a = e.disabled,
        i = e.onChange,
        u = YT(e, KD);
      return o.default.createElement(
        "label",
        {
          className: c("Radio", t, {
            "Radio--checked": r,
            "Radio--disabled": a,
          }),
        },
        o.default.createElement(
          "input",
          bT(
            {
              type: "radio",
              className: "Radio-input",
              checked: r,
              disabled: a,
              onChange: i,
            },
            u,
          ),
        ),
        o.default.createElement("span", { className: "Radio-text" }, n),
      );
    },
    QD = "up",
    ZD = "down";
  ce.addHook("beforeSanitizeAttributes", function (e) {
    if (e instanceof HTMLElement && e.hasAttribute("href")) {
      var t = e.getAttribute("href");
      t && (e.dataset.cuiHref = t),
        "_blank" === e.getAttribute("target") && (e.dataset.cuiTarget = "1");
    }
  }),
    ce.addHook("afterSanitizeAttributes", function (e) {
      e instanceof HTMLElement &&
        (e.dataset.cuiHref &&
          e.hasAttribute("href") &&
          e.removeAttribute("data-cui-href"),
        e.dataset.cuiTarget &&
          (e.setAttribute("target", "_blank"),
          e.setAttribute("rel", "noopener noreferrer"),
          e.removeAttribute("data-cui-target")));
    });
  var eF = ["className", "content", "options"],
    tF = o.default.forwardRef(function (e, t) {
      var n = e.className,
        r = e.content,
        a = e.options,
        i = void 0 === a ? {} : a,
        u = YT(e, eF),
        l = { __html: ce.sanitize(r, i) };
      return o.default.createElement(
        "div",
        bT(
          { className: c("RichText", n), dangerouslySetInnerHTML: l, ref: t },
          u,
        ),
      );
    }),
    nF = [
      "className",
      "onSearch",
      "onChange",
      "onClear",
      "value",
      "clearable",
      "showSearch",
    ],
    rF = ["className", "placeholder", "variant", "children"],
    oF = o.default.forwardRef(function (e, t) {
      var n = e.className,
        r = e.placeholder,
        a = e.variant,
        i = void 0 === a ? "outline" : a,
        u = e.children,
        l = YT(e, rF);
      return o.default.createElement(
        "select",
        bT({ className: c("Input Select", "Input--".concat(i), n) }, l, {
          ref: t,
        }),
        r && o.default.createElement("option", { value: "" }, r),
        u,
      );
    }),
    aF = ["className", "current", "status", "inverted", "children"];
  function iF(e, t) {
    var n = jg(e);
    if (wE) {
      var r = wE(e);
      t &&
        (r = cN(r).call(r, function (t) {
          return _E(e, t).enumerable;
        })),
        n.push.apply(n, r);
    }
    return n;
  }
  function cF(e) {
    for (var t = 1; t < arguments.length; t++) {
      var n = null != arguments[t] ? arguments[t] : {};
      t % 2
        ? iF(Object(n), !0).forEach(function (t) {
            Fx(e, t, n[t]);
          })
        : BE
          ? $E(e, BE(n))
          : iF(Object(n)).forEach(function (t) {
              ow(e, t, _E(n, t));
            });
    }
    return e;
  }
  var uF = o.default.forwardRef(function (e, t) {
      var n = e.className,
        r = e.current,
        a = void 0 === r ? 0 : r,
        i = e.status,
        u = e.inverted,
        l = e.children,
        s = YT(e, aF),
        f = o.default.Children.toArray(l),
        d = Wx(f).call(f, function (e, t) {
          var n = { index: t, active: !1, completed: !1, disabled: !1 };
          return (
            a === t
              ? ((n.active = !0), (n.status = i))
              : a > t
                ? (n.completed = !0)
                : ((n.disabled = !u), (n.completed = u)),
            o.default.isValidElement(e)
              ? o.default.cloneElement(e, cF(cF({}, n), e.props))
              : null
          );
        });
      return o.default.createElement(
        "ul",
        bT({ className: c("Stepper", n), ref: t }, s),
        d,
      );
    }),
    lF = [
      "className",
      "active",
      "completed",
      "disabled",
      "status",
      "index",
      "title",
      "subTitle",
      "desc",
      "children",
    ];
  var sF = o.default.forwardRef(function (e, t) {
      var n = e.className,
        r = e.active,
        a = void 0 !== r && r,
        i = e.completed,
        u = void 0 !== i && i,
        l = e.disabled,
        s = void 0 !== l && l,
        f = e.status;
      e.index;
      var d = e.title,
        p = e.subTitle,
        m = e.desc,
        v = e.children,
        h = YT(e, lF);
      return o.default.createElement(
        "li",
        bT(
          {
            className: c(
              "Step",
              { "Step--active": a, "Step--completed": u, "Step--disabled": s },
              n,
            ),
            ref: t,
            "data-status": f,
          },
          h,
        ),
        o.default.createElement(
          "div",
          { className: "Step-icon" },
          (function (e) {
            if (e)
              return o.default.createElement(QS, {
                type: {
                  success: "check-circle-fill",
                  fail: "warning-circle-fill",
                  abort: "dash-circle-fill",
                }[e],
              });
            return o.default.createElement("div", { className: "Step-dot" });
          })(f),
        ),
        o.default.createElement("div", { className: "Step-line" }),
        o.default.createElement(
          "div",
          { className: "Step-content" },
          d &&
            o.default.createElement(
              "div",
              { className: "Step-title" },
              d && o.default.createElement("span", null, d),
              p && o.default.createElement("small", null, p),
            ),
          m && o.default.createElement("div", { className: "Step-desc" }, m),
          v,
        ),
      );
    }),
    fF = ["active", "index", "children", "onClick"],
    dF = ["active", "children"],
    pF = function (e) {
      var t = e.active,
        n = e.index,
        r = e.children,
        a = e.onClick,
        i = YT(e, fF);
      return o.default.createElement(
        "div",
        { className: "Tabs-navItem" },
        o.default.createElement(
          "button",
          bT(
            {
              className: c("Tabs-navLink", { active: t }),
              type: "button",
              role: "tab",
              "aria-selected": t,
              onClick: function (e) {
                a(n, e);
              },
            },
            i,
          ),
          o.default.createElement("span", null, r),
        ),
      );
    },
    mF = function (e) {
      var t = e.active,
        n = e.children,
        r = YT(e, dF);
      return o.default.createElement(
        "div",
        bT({ className: c("Tabs-pane", { active: t }) }, r, {
          role: "tabpanel",
        }),
        n,
      );
    },
    vF = ["as", "className", "color", "children"],
    hF = o.default.forwardRef(function (e, t) {
      var n = e.as,
        r = void 0 === n ? "span" : n,
        a = e.className,
        i = e.color,
        u = e.children,
        l = YT(e, vF);
      return o.default.createElement(
        r,
        bT({ className: c("Tag", i && "Tag--".concat(i), a), ref: t }, l),
        u,
      );
    });
  var gF = function (e) {
    var n = e.content,
      r = e.type,
      a = e.duration,
      i = e.onUnmount,
      u = _x(t.useState(!1), 2),
      l = u[0],
      s = u[1];
    return (
      t.useEffect(
        function () {
          s(!0),
            -1 !== a &&
              (setTimeout(function () {
                s(!1);
              }, a),
              setTimeout(function () {
                i && i();
              }, a + 300));
        },
        [a, i],
      ),
      o.default.createElement(
        "div",
        {
          className: c("Toast", { show: l }),
          "data-type": r,
          role: "alert",
          "aria-live": "assertive",
          "aria-atomic": "true",
        },
        o.default.createElement(
          "div",
          { className: "Toast-content", role: "presentation" },
          (function (e) {
            switch (e) {
              case "success":
                return o.default.createElement(QS, { type: "check-circle" });
              case "error":
                return o.default.createElement(QS, { type: "warning-circle" });
              case "loading":
                return o.default.createElement(QS, {
                  type: "spinner",
                  spin: !0,
                });
              default:
                return null;
            }
          })(r),
          o.default.createElement("p", { className: "Toast-message" }, n),
        ),
      )
    );
  };
  function yF(e, t) {
    var n =
      arguments.length > 2 && void 0 !== arguments[2] ? arguments[2] : 2e3;
    Pd(o.default.createElement(gF, { content: e, type: t, duration: n }));
  }
  var bF = {
      show: yF,
      success: function (e, t) {
        yF(e, "success", t);
      },
      fail: function (e, t) {
        yF(e, "error", t);
      },
      loading: function (e, t) {
        yF(e, "loading", t);
      },
    },
    EF = function (e) {
      var t = e.item,
        n = e.onClick,
        r = t.type,
        a = t.icon,
        i = t.img,
        c = t.title;
      return o.default.createElement(
        "div",
        { className: "Toolbar-item", "data-type": r },
        o.default.createElement(
          tC,
          {
            className: "Toolbar-btn",
            onClick: function (e) {
              return n(t, e);
            },
          },
          o.default.createElement(
            "span",
            { className: "Toolbar-btnIcon" },
            a && o.default.createElement(QS, { type: a }),
            i &&
              o.default.createElement("img", {
                className: "Toolbar-img",
                src: i,
                alt: "",
              }),
          ),
          o.default.createElement("span", { className: "Toolbar-btnText" }, c),
        ),
      );
    },
    wF = function (e) {
      var t = e.items,
        n = e.onClick;
      return o.default.createElement(
        "div",
        { className: "Toolbar" },
        Wx(t).call(t, function (e) {
          return o.default.createElement(EF, {
            item: e,
            onClick: n,
            key: e.type,
          });
        }),
      );
    };
  function xF(e, t) {
    var n = jg(e);
    if (wE) {
      var r = wE(e);
      t &&
        (r = cN(r).call(r, function (t) {
          return _E(e, t).enumerable;
        })),
        n.push.apply(n, r);
    }
    return n;
  }
  function NF(e) {
    for (var t = 1; t < arguments.length; t++) {
      var n = null != arguments[t] ? arguments[t] : {};
      t % 2
        ? xF(Object(n), !0).forEach(function (t) {
            Fx(e, t, n[t]);
          })
        : BE
          ? $E(e, BE(n))
          : xF(Object(n)).forEach(function (t) {
              ow(e, t, _E(n, t));
            });
    }
    return e;
  }
  var TF = [
      "className",
      "src",
      "cover",
      "duration",
      "onClick",
      "onCoverLoad",
      "style",
      "videoRef",
      "children",
    ],
    SF = {
      position: "absolute",
      height: "1px",
      width: "1px",
      overflow: "hidden",
      clip: "rect(0 0 0 0)",
      margin: "-1px",
      whiteSpace: "nowrap",
    },
    CF = { exports: {} };
  !(function (e) {
    e.exports = Rx;
  })(CF);
  var OF = le(CF.exports),
    RF = { exports: {} },
    kF = be,
    IF = et,
    AF = ro,
    MF = fo,
    _F = OT,
    jF = Math.min,
    LF = [].lastIndexOf,
    PF = !!LF && 1 / [1].lastIndexOf(1, -0) < 0,
    DF = _F("lastIndexOf"),
    FF =
      PF || !DF
        ? function (e) {
            if (PF) return kF(LF, this, arguments) || 0;
            var t = IF(this),
              n = MF(t),
              r = n - 1;
            for (
              arguments.length > 1 && (r = jF(r, AF(arguments[1]))),
                r < 0 && (r = n + r);
              r >= 0;
              r--
            )
              if (r in t && t[r] === e) return r || 0;
            return -1;
          }
        : LF;
  Mr(
    { target: "Array", proto: !0, forced: FF !== [].lastIndexOf },
    { lastIndexOf: FF },
  );
  var HF = Nx("Array").lastIndexOf,
    BF = st,
    UF = HF,
    zF = Array.prototype,
    GF = function (e) {
      var t = e.lastIndexOf;
      return e === zF || (BF(zF, e) && t === zF.lastIndexOf) ? UF : t;
    };
  !(function (e) {
    e.exports = GF;
  })(RF);
  var VF = le(RF.exports),
    YF = { exports: {} },
    WF = de,
    XF = pe,
    qF = li,
    $F = NM.trim,
    KF = Se("".charAt),
    JF = WF.parseFloat,
    QF = WF.Symbol,
    ZF = QF && QF.iterator,
    eH =
      1 / JF("\t\n\v\f\r                　\u2028\u2029\ufeff-0") != -1 / 0 ||
      (ZF &&
        !XF(function () {
          JF(Object(ZF));
        }))
        ? function (e) {
            var t = $F(qF(e)),
              n = JF(t);
            return 0 === n && "-" == KF(t, 0) ? -0 : n;
          }
        : JF;
  Mr({ global: !0, forced: parseFloat != eH }, { parseFloat: eH });
  var tH = ot.parseFloat;
  !(function (e) {
    e.exports = tH;
  })(YF);
  var nH = le(YF.exports),
    rH = ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
    oH = [
      "className",
      "type",
      "img",
      "name",
      "desc",
      "tags",
      "locale",
      "currency",
      "price",
      "count",
      "unit",
      "action",
      "children",
      "originalPrice",
      "meta",
      "status",
    ],
    aH = o.default.forwardRef(function (e, t) {
      var n = e.className,
        r = e.type,
        a = e.img,
        i = e.name,
        u = e.desc,
        l = e.tags,
        s = void 0 === l ? [] : l,
        f = e.locale,
        d = e.currency,
        p = e.price,
        m = e.count,
        v = e.unit,
        h = e.action,
        g = e.children,
        y = e.originalPrice,
        b = e.meta,
        E = e.status,
        w = YT(e, oH),
        x = "order" === r,
        N = o.default.createElement(
          o.default.Fragment,
          null,
          o.default.createElement(
            ID,
            { as: "h4", truncate: !x || 2, className: "Goods-name" },
            i,
          ),
          o.default.createElement(ID, { className: "Goods-desc" }, u),
          o.default.createElement(
            "div",
            { className: "Goods-tags" },
            Wx(s).call(s, function (e) {
              return o.default.createElement(
                hF,
                { color: "primary", key: e.name },
                e.name,
              );
            }),
          ),
        ),
        T = { currency: d, locale: f },
        S = null != p && o.default.createElement(_D, bT({ price: p }, T)),
        C = o.default.createElement(
          "div",
          { className: "Goods-countUnit" },
          m &&
            o.default.createElement(
              "span",
              { className: "Goods-count" },
              "×",
              m,
            ),
          v && o.default.createElement("span", { className: "Goods-unit" }, v),
        ),
        O = x
          ? N
          : o.default.createElement(
              o.default.Fragment,
              null,
              h &&
                o.default.createElement(
                  c_,
                  bT({ className: "Goods-buyBtn", icon: "cart" }, h),
                ),
              N,
              o.default.createElement(
                lC,
                { alignItems: "flex-end" },
                o.default.createElement(
                  pC,
                  null,
                  S,
                  y &&
                    o.default.createElement(
                      _D,
                      bT({ price: y, original: !0 }, T),
                    ),
                  b &&
                    o.default.createElement(
                      "span",
                      { className: "Goods-meta" },
                      b,
                    ),
                ),
                C,
              ),
            );
      return o.default.createElement(
        lC,
        bT({ className: c("Goods", n), "data-type": r, ref: t }, w),
        a &&
          o.default.createElement("img", {
            className: "Goods-img",
            src: a,
            alt: i,
          }),
        o.default.createElement(pC, { className: "Goods-main" }, O, g),
        x &&
          o.default.createElement(
            "div",
            { className: "Goods-aside" },
            S,
            C,
            o.default.createElement("span", { className: "Goods-status" }, E),
            h &&
              o.default.createElement(
                tC,
                bT({ className: "Goods-detailBtn" }, h),
              ),
          ),
      );
    }),
    iH = function (e) {
      var n = e.count,
        r = e.onClick,
        a = e.onDidMount,
        i = eD("BackBottom").trans,
        c = i("bottom");
      return (
        n && (c = i(1 === n ? "newMsgOne" : "newMsgOther").replace("{n}", n)),
        t.useEffect(
          function () {
            a && a();
          },
          [a],
        ),
        o.default.createElement(
          "div",
          { className: "BackBottom" },
          o.default.createElement(
            tC,
            { className: "slide-in-right-item", onClick: r },
            c,
            o.default.createElement(QS, { type: "chevron-double-down" }),
          ),
        )
      );
    };
  var cH = !!zM("passiveListener") && { passive: !0 };
  function uH(e, t) {
    var n = Math.max(e.offsetHeight, 600);
    return s_(e) < n * t;
  }
  var lH = o.default.forwardRef(function (e, n) {
      var r = e.messages,
        a = e.loadMoreText,
        i = e.onRefresh,
        c = e.onScroll,
        u = e.renderBeforeMessageList,
        l = e.renderMessageContent,
        s = e.onBackBottomShow,
        f = e.onBackBottomClick,
        d = _x(t.useState(!1), 2),
        p = d[0],
        m = d[1],
        v = _x(t.useState(0), 2),
        h = v[0],
        g = v[1],
        y = t.useRef(p),
        b = t.useRef(h),
        E = t.useRef(null),
        w = t.useRef(null),
        x = r[r.length - 1],
        N = function () {
          g(0), m(!1);
        },
        T = t.useCallback(function (e) {
          w.current &&
            (!y.current || (e && e.force)) &&
            (w.current.scrollToEnd(e), y.current && N());
        }, []),
        S = t.useRef(
          (function (e) {
            var t =
                arguments.length > 1 && void 0 !== arguments[1]
                  ? arguments[1]
                  : 300,
              n = !0;
            return function () {
              n &&
                ((n = !1),
                e.apply(void 0, arguments),
                setTimeout(function () {
                  n = !0;
                }, t));
            };
          })(function (e) {
            uH(e, 3) ? (b.current ? uH(e, 0.5) && N() : m(!1)) : m(!0);
          }),
        );
      return (
        t.useEffect(
          function () {
            b.current = h;
          },
          [h],
        ),
        t.useEffect(
          function () {
            y.current = p;
          },
          [p],
        ),
        t.useEffect(
          function () {
            var e = w.current,
              t = e && e.wrapperRef.current;
            if (t && x && "pop" !== x.position)
              if ("right" === x.position) T({ force: !0 });
              else if (uH(t, 2)) {
                var n = !!t.scrollTop;
                T({ animated: n, force: !0 });
              } else
                g(function (e) {
                  return e + 1;
                }),
                  m(!0);
          },
          [x, T],
        ),
        t.useEffect(function () {
          var e = E.current,
            t = !1,
            n = 0;
          function r() {
            (t = !1), (n = 0);
          }
          function o(e) {
            var r = document.activeElement;
            r &&
              "TEXTAREA" === r.nodeName &&
              ((t = !0), (n = e.touches[0].clientY));
          }
          function a(e) {
            t &&
              Math.abs(e.touches[0].clientY - n) > 20 &&
              (document.activeElement.blur(), r());
          }
          return (
            e.addEventListener("touchstart", o, cH),
            e.addEventListener("touchmove", a, cH),
            e.addEventListener("touchend", r),
            e.addEventListener("touchcancel", r),
            function () {
              e.removeEventListener("touchstart", o),
                e.removeEventListener("touchmove", a),
                e.removeEventListener("touchend", r),
                e.removeEventListener("touchcancel", r);
            }
          );
        }, []),
        t.useImperativeHandle(
          n,
          function () {
            return { ref: E, scrollToEnd: T };
          },
          [T],
        ),
        o.default.createElement(
          "div",
          { className: "MessageContainer", ref: E, tabIndex: -1 },
          u && u(),
          o.default.createElement(
            UD,
            {
              onRefresh: i,
              onScroll: function (e) {
                S.current(e.target), c && c(e);
              },
              loadMoreText: a,
              ref: w,
            },
            o.default.createElement(
              "div",
              { className: "MessageList" },
              Wx(r).call(r, function (e) {
                return o.default.createElement(
                  mD,
                  bT({}, e, { renderMessageContent: l, key: e._id }),
                );
              }),
            ),
          ),
          p &&
            o.default.createElement(iH, {
              count: h,
              onClick: function () {
                T({ animated: !1, force: !0 }), f && f();
              },
              onDidMount: s,
            }),
        )
      );
    }),
    sH = zM("passiveListener"),
    fH = !!sH && { passive: !0 },
    dH = !!sH && { passive: !1 },
    pH = {
      inited: "hold2talk",
      recording: "release2send",
      willCancel: "release2send",
    },
    mH = 0,
    vH = 0,
    hH = o.default.forwardRef(function (e, n) {
      var r = e.volume,
        a = e.onStart,
        i = e.onEnd,
        u = e.onCancel,
        l = _x(t.useState("inited"), 2),
        s = l[0],
        f = l[1],
        d = t.useRef(null),
        p = eD("Recorder").trans,
        m = t.useCallback(
          function () {
            var e = Date.now() - mH;
            i && i({ duration: e });
          },
          [i],
        );
      t.useImperativeHandle(n, function () {
        return {
          stop: function () {
            f("inited"), m(), (mH = 0);
          },
        };
      }),
        t.useEffect(
          function () {
            var e = d.current;
            function t(e) {
              e.cancelable && e.preventDefault();
              var t = e.touches[0];
              (vH = t.pageY), (mH = Date.now()), f("recording"), a && a();
            }
            function n(e) {
              if (mH) {
                var t = e.touches[0].pageY;
                f(vH - t > 80 ? "willCancel" : "recording");
              }
            }
            function r(e) {
              if (mH) {
                var t = e.changedTouches[0].pageY,
                  n = vH - t < 80;
                f("inited"), n ? m() : u && u();
              }
            }
            return (
              e.addEventListener("touchstart", t, dH),
              e.addEventListener("touchmove", n, fH),
              e.addEventListener("touchend", r),
              e.addEventListener("touchcancel", r),
              function () {
                e.removeEventListener("touchstart", t),
                  e.removeEventListener("touchmove", n),
                  e.removeEventListener("touchend", r),
                  e.removeEventListener("touchcancel", r);
              }
            );
          },
          [m, u, a],
        );
      var v = "willCancel" === s,
        h = { transform: "scale(".concat((r || 1) / 100 + 1, ")") };
      return o.default.createElement(
        "div",
        { className: c("Recorder", { "Recorder--cancel": v }), ref: d },
        "inited" !== s &&
          o.default.createElement(
            lC,
            { className: "RecorderToast", direction: "column", center: !0 },
            o.default.createElement(
              "div",
              {
                className: "RecorderToast-waves",
                hidden: "recording" !== s,
                style: h,
              },
              o.default.createElement(QS, {
                className: "RecorderToast-wave-1",
                type: "hexagon",
              }),
              o.default.createElement(QS, {
                className: "RecorderToast-wave-2",
                type: "hexagon",
              }),
              o.default.createElement(QS, {
                className: "RecorderToast-wave-3",
                type: "hexagon",
              }),
            ),
            o.default.createElement(QS, {
              className: "RecorderToast-icon",
              type: v ? "cancel" : "mic",
            }),
            o.default.createElement(
              "span",
              null,
              p(v ? "release2cancel" : "releaseOrSwipe"),
            ),
          ),
        o.default.createElement(
          "div",
          {
            className: "Recorder-btn",
            role: "button",
            "aria-label": p("hold2talk"),
          },
          o.default.createElement("span", null, p(pH[s])),
        ),
      );
    }),
    gH = function (e) {
      var t = e.onClickOutside,
        n = e.children;
      return o.default.createElement(JM, { onClick: t }, n);
    };
  var yH = function (e) {
      var r,
        a,
        i = e.className,
        u = e.active,
        l = e.target,
        s = e.children,
        f = Dd(e.onClose, "mousedown"),
        d = _N({ active: u, ref: f }),
        p = d.didMount,
        m = d.isShow,
        v = _x(t.useState({}), 2),
        h = v[0],
        g = v[1],
        y = t.useCallback(
          function () {
            if (f.current) {
              var e = l.getBoundingClientRect(),
                t = f.current.getBoundingClientRect();
              g({
                top: "".concat(e.top - t.height, "px"),
                left: "".concat(e.left, "px"),
              });
            }
          },
          [l, f],
        );
      return (
        t.useEffect(
          function () {
            f.current && (f.current.focus(), y());
          },
          [p, y, f],
        ),
        (r = y),
        (a = t.useRef(!1)),
        t.useEffect(
          function () {
            function e() {
              r(), (a.current = !1);
            }
            function t() {
              a.current ||
                ((a.current = !0),
                window.requestAnimationFrame
                  ? window.requestAnimationFrame(e)
                  : setTimeout(e, 66));
            }
            return (
              window.addEventListener("resize", t),
              function () {
                window.removeEventListener("resize", t);
              }
            );
          },
          [r],
        ),
        p
          ? n.createPortal(
              o.default.createElement(
                "div",
                { className: c("Popover", i, { active: m }), ref: f, style: h },
                o.default.createElement(
                  "div",
                  { className: "Popover-body" },
                  s,
                ),
                o.default.createElement(
                  "svg",
                  { className: "Popover-arrow", viewBox: "0 0 9 5" },
                  o.default.createElement("polygon", {
                    points: "0,0 5,5, 9,0",
                  }),
                ),
              ),
              document.body,
            )
          : null
      );
    },
    bH = function (e) {
      return o.default.createElement(
        "div",
        { className: "Composer-actions", "data-action-icon": e.icon },
        o.default.createElement(c_, bT({ size: "lg" }, e)),
      );
    },
    EH = function (e) {
      var t = e.item,
        n = e.onClick;
      return o.default.createElement(bH, {
        icon: t.icon,
        img: t.img,
        "data-icon": t.icon,
        "data-tooltip": t.title || null,
        "aria-label": t.title,
        onClick: n,
      });
    },
    wH = function (e) {
      var n = e.file,
        r = e.onCancel,
        a = e.onSend,
        i = _x(t.useState(""), 2),
        c = i[0],
        u = i[1],
        l = eD("SendConfirm").trans;
      return (
        t.useEffect(
          function () {
            var e = new FileReader();
            (e.onload = function (e) {
              e.target && u(e.target.result);
            }),
              e.readAsDataURL(n);
          },
          [n],
        ),
        o.default.createElement(
          wD,
          {
            className: "SendConfirm",
            title: l("title"),
            active: !!c,
            vertical: !1,
            actions: [
              { label: l("cancel"), onClick: r },
              { label: l("send"), color: "primary", onClick: a },
            ],
          },
          o.default.createElement(
            lC,
            { className: "SendConfirm-inner", center: !0 },
            o.default.createElement("img", { src: c, alt: "" }),
          ),
        )
      );
    },
    xH = { exports: {} };
  !(function (e) {
    e.exports = FT;
  })(xH);
  var NH = le(xH.exports),
    TH = navigator.userAgent,
    SH = /iPad|iPhone|iPod/.test(TH);
  function CH() {
    return SH
      ? ((e = "Safari/"),
        -1 !== NH(TH).call(TH, e) || /OS 11_[0-3]\D/.test(TH) ? 0 : 1)
      : 2;
    var e;
  }
  var OH = ["inputRef", "invisible", "onImageSend"],
    RH = zM("touch"),
    kH = function (e) {
      var n = e.inputRef,
        r = e.invisible,
        a = e.onImageSend,
        i = YT(e, OH),
        u = _x(t.useState(null), 2),
        l = u[0],
        s = u[1],
        f = t.useRef(null),
        d = t.useCallback(function (e) {
          !(function (e, t) {
            var n = e.clipboardData.items;
            if (n && n.length)
              for (var r = 0; r < n.length; r++) {
                var o,
                  a = n[r];
                if (-1 !== NH((o = a.type)).call(o, "image")) {
                  var i = a.getAsFile();
                  i && t(i), e.preventDefault();
                  break;
                }
              }
          })(e, s);
        }, []),
        p = t.useCallback(function () {
          s(null);
        }, []),
        m = t.useCallback(
          function () {
            a &&
              l &&
              _d.resolve(a(l)).then(function () {
                s(null);
              });
          },
          [a, l],
        );
      return (
        t.useEffect(
          function () {
            RH &&
              n.current &&
              f.current &&
              (function (e, t) {
                var n,
                  r = CH();
                t || (t = e);
                var o = function () {
                  0 !== r &&
                    (1 === r
                      ? (document.body.scrollTop = document.body.scrollHeight)
                      : t.scrollIntoView(!1));
                };
                e.addEventListener("focus", function () {
                  setTimeout(o, 300), (n = setTimeout(o, 1e3));
                }),
                  e.addEventListener("blur", function () {
                    clearTimeout(n),
                      r &&
                        SH &&
                        setTimeout(function () {
                          document.body.scrollIntoView();
                        });
                  });
              })(n.current, f.current);
          },
          [n],
        ),
        o.default.createElement(
          "div",
          { className: c({ "S--invisible": r }), ref: f },
          o.default.createElement(
            uP,
            bT(
              {
                className: "Composer-input",
                rows: 1,
                autoSize: !0,
                enterKeyHint: "send",
                onPaste: a ? d : void 0,
                ref: n,
              },
              i,
            ),
          ),
          l && o.default.createElement(wH, { file: l, onCancel: p, onSend: m }),
        )
      );
    },
    IH = function (e) {
      var t = e.disabled,
        n = e.onClick,
        r = eD("Composer").trans;
      return o.default.createElement(
        "div",
        { className: "Composer-actions" },
        o.default.createElement(
          tC,
          {
            className: "Composer-sendBtn",
            disabled: t,
            onMouseDown: n,
            color: "primary",
          },
          r("send"),
        ),
      );
    };
  function AH(e, t) {
    var n = jg(e);
    if (wE) {
      var r = wE(e);
      t &&
        (r = cN(r).call(r, function (t) {
          return _E(e, t).enumerable;
        })),
        n.push.apply(n, r);
    }
    return n;
  }
  function MH(e) {
    for (var t = 1; t < arguments.length; t++) {
      var n = null != arguments[t] ? arguments[t] : {};
      t % 2
        ? AH(Object(n), !0).forEach(function (t) {
            Fx(e, t, n[t]);
          })
        : BE
          ? $E(e, BE(n))
          : AH(Object(n)).forEach(function (t) {
              ow(e, t, _E(n, t));
            });
    }
    return e;
  }
  var _H = "S--focusing",
    jH = o.default.forwardRef(function (e, n) {
      var r = e.text,
        a = void 0 === r ? "" : r,
        i = e.inputType,
        u = void 0 === i ? "text" : i,
        l = e.wideBreakpoint,
        s = e.placeholder,
        f = void 0 === s ? "请输入..." : s,
        d = e.recorder,
        p = void 0 === d ? {} : d,
        m = e.onInputTypeChange,
        v = e.onFocus,
        h = e.onBlur,
        g = e.onChange,
        y = e.onSend,
        b = e.onImageSend,
        E = e.onAccessoryToggle,
        w = e.toolbar,
        x = void 0 === w ? [] : w,
        N = e.onToolbarClick,
        T = e.rightAction,
        S = e.inputOptions,
        C = _x(t.useState(a), 2),
        O = C[0],
        R = C[1],
        k = _x(t.useState(u || "text"), 2),
        I = k[0],
        A = k[1],
        M = _x(t.useState(!1), 2),
        _ = M[0],
        j = M[1],
        L = _x(t.useState(""), 2),
        P = L[0],
        D = L[1],
        F = t.useRef(null),
        H = t.useRef(!1),
        B = t.useRef(),
        U = t.useRef(),
        z = t.useRef(!1),
        G = _x(t.useState(!1), 2),
        V = G[0],
        Y = G[1];
      t.useEffect(
        function () {
          var e =
            !(!l || !window.matchMedia) &&
            window.matchMedia("(min-width: ".concat(l, ")"));
          function t(e) {
            Y(e.matches);
          }
          return (
            Y(e && e.matches),
            e && e.addListener(t),
            function () {
              e && e.removeListener(t);
            }
          );
        },
        [l],
      ),
        t.useEffect(
          function () {
            yD("S--wide", V), V || D("");
          },
          [V],
        ),
        t.useEffect(
          function () {
            z.current && E && E(_);
          },
          [_, E],
        ),
        t.useEffect(function () {
          z.current = !0;
        }, []),
        t.useImperativeHandle(n, function () {
          return { setText: R };
        });
      var W = t.useCallback(
          function () {
            var e = "voice" === I,
              t = e ? "text" : "voice";
            if ((A(t), e)) {
              var n = F.current;
              n.focus(), (n.selectionStart = n.selectionEnd = n.value.length);
            }
            m && m(t);
          },
          [I, m],
        ),
        X = t.useCallback(
          function (e) {
            clearTimeout(B.current), yD(_H, !0), (H.current = !0), v && v(e);
          },
          [v],
        ),
        q = t.useCallback(
          function (e) {
            (B.current = setTimeout(function () {
              yD(_H, !1), (H.current = !1);
            }, 0)),
              h && h(e);
          },
          [h],
        ),
        $ = t.useCallback(
          function () {
            y("text", O), R(""), H.current && F.current.focus();
          },
          [y, O],
        ),
        K = t.useCallback(
          function (e) {
            e.shiftKey || 13 !== e.keyCode || ($(), e.preventDefault());
          },
          [$],
        ),
        J = t.useCallback(
          function (e, t) {
            R(e), g && g(e, t);
          },
          [g],
        ),
        Q = t.useCallback(
          function (e) {
            $(), e.preventDefault();
          },
          [$],
        ),
        Z = t.useCallback(
          function () {
            j(!_);
          },
          [_],
        ),
        ee = t.useCallback(function () {
          setTimeout(function () {
            j(!1), D("");
          });
        }, []),
        te = t.useCallback(
          function (e, t) {
            N && N(e, t),
              e.render && ((U.current = t.currentTarget), D(e.render));
          },
          [N],
        ),
        ne = t.useCallback(function () {
          D("");
        }, []),
        re = "text" === I,
        oe = re ? "volume-circle" : "keyboard-circle",
        ae = x.length > 0,
        ie = MH(
          MH({}, S),
          {},
          {
            value: O,
            inputRef: F,
            placeholder: f,
            onFocus: X,
            onBlur: q,
            onKeyDown: K,
            onChange: J,
            onImageSend: b,
          },
        );
      return V
        ? o.default.createElement(
            "div",
            { className: "Composer Composer--lg" },
            ae &&
              Wx(x).call(x, function (e) {
                return o.default.createElement(EH, {
                  item: e,
                  onClick: function (t) {
                    return te(e, t);
                  },
                  key: e.type,
                });
              }),
            P &&
              o.default.createElement(
                yH,
                { active: !!P, target: U.current, onClose: ne },
                P,
              ),
            o.default.createElement(
              "div",
              { className: "Composer-inputWrap" },
              o.default.createElement(kH, bT({ invisible: !1 }, ie)),
            ),
            o.default.createElement(IH, { onClick: Q, disabled: !O }),
          )
        : o.default.createElement(
            o.default.Fragment,
            null,
            o.default.createElement(
              "div",
              { className: "Composer" },
              p.canRecord &&
                o.default.createElement(bH, {
                  className: "Composer-inputTypeBtn",
                  "data-icon": oe,
                  icon: oe,
                  onClick: W,
                  "aria-label": re ? "切换到语音输入" : "切换到键盘输入",
                }),
              o.default.createElement(
                "div",
                { className: "Composer-inputWrap" },
                o.default.createElement(kH, bT({ invisible: !re }, ie)),
                !re && o.default.createElement(hH, p),
              ),
              !O && T && o.default.createElement(bH, T),
              ae &&
                o.default.createElement(bH, {
                  className: c("Composer-toggleBtn", { active: _ }),
                  icon: "plus-circle",
                  onClick: Z,
                  "aria-label": _ ? "关闭工具栏" : "展开工具栏",
                }),
              O && o.default.createElement(IH, { onClick: Q, disabled: !1 }),
            ),
            _ &&
              o.default.createElement(
                gH,
                { onClickOutside: ee },
                P || o.default.createElement(wF, { items: x, onClick: te }),
              ),
          );
    }),
    LH = o.default.forwardRef(function (e, n) {
      var r = e.wideBreakpoint,
        a = e.locale,
        i = void 0 === a ? "zh-CN" : a,
        c = e.locales,
        u = e.navbar,
        l = e.renderNavbar,
        s = e.loadMoreText,
        f = e.renderBeforeMessageList,
        d = e.messagesRef,
        p = e.onRefresh,
        m = e.onScroll,
        v = e.messages,
        h = void 0 === v ? [] : v,
        g = e.renderMessageContent,
        y = e.onBackBottomShow,
        b = e.onBackBottomClick,
        E = e.quickReplies,
        w = void 0 === E ? [] : E,
        x = e.quickRepliesVisible,
        N = e.onQuickReplyClick,
        T = void 0 === N ? function () {} : N,
        S = e.onQuickReplyScroll,
        C = e.renderQuickReplies,
        O = e.text,
        R = e.placeholder,
        k = e.onInputFocus,
        I = e.onInputChange,
        A = e.onInputBlur,
        M = e.onSend,
        _ = e.onImageSend,
        j = e.inputOptions,
        L = e.composerRef,
        P = e.inputType,
        D = e.onInputTypeChange,
        F = e.recorder,
        H = e.toolbar,
        B = e.onToolbarClick,
        U = e.onAccessoryToggle,
        z = e.rightAction,
        G = e.Composer,
        V = void 0 === G ? jH : G;
      return (
        t.useEffect(function () {
          /^((?!chrome|android|crios|fxios).)*safari/i.test(
            navigator.userAgent,
          ) && (document.documentElement.dataset.safari = "");
        }, []),
        o.default.createElement(
          ZP,
          { locale: i, locales: c },
          o.default.createElement(
            "div",
            { className: "ChatApp", ref: n },
            l ? l() : u && o.default.createElement(ND, u),
            o.default.createElement(lH, {
              ref: d,
              loadMoreText: s,
              messages: h,
              renderBeforeMessageList: f,
              renderMessageContent: g,
              onRefresh: p,
              onScroll: m,
              onBackBottomShow: y,
              onBackBottomClick: b,
            }),
            o.default.createElement(
              "div",
              { className: "ChatFooter" },
              C
                ? C()
                : o.default.createElement($D, {
                    items: w,
                    visible: x,
                    onClick: T,
                    onScroll: S,
                  }),
              o.default.createElement(V, {
                wideBreakpoint: r,
                ref: L,
                inputType: P,
                text: O,
                inputOptions: j,
                placeholder: R,
                onAccessoryToggle: U,
                recorder: F,
                toolbar: H,
                onToolbarClick: B,
                onInputTypeChange: D,
                onFocus: function (e) {
                  d &&
                    d.current &&
                    d.current.scrollToEnd({ animated: !1, force: !0 }),
                    k && k(e);
                },
                onChange: I,
                onBlur: A,
                onSend: M,
                onImageSend: _,
                rightAction: z,
              }),
            ),
          ),
        )
      );
    });
  (e.Avatar = WS),
    (e.Backdrop = qS),
    (e.Bubble = KS),
    (e.Button = tC),
    (e.Card = rC),
    (e.CardActions = function (e) {
      var t = e.children,
        n = e.className,
        r = e.direction,
        a = YT(e, yC);
      return o.default.createElement(
        "div",
        bT(
          { className: c("CardActions", n, r && "CardActions--".concat(r)) },
          a,
        ),
        t,
      );
    }),
    (e.CardContent = function (e) {
      var t = e.className,
        n = e.children,
        r = YT(e, vC);
      return o.default.createElement(
        "div",
        bT({ className: c("CardContent", t) }, r),
        n,
      );
    }),
    (e.CardMedia = function (e) {
      var t = e.className,
        n = e.aspectRatio,
        r = void 0 === n ? "square" : n,
        a = e.color,
        i = e.image,
        u = e.children,
        l = YT(e, mC),
        s = {
          backgroundColor: a || void 0,
          backgroundImage:
            "string" == typeof i ? "url('".concat(i, "')") : void 0,
        };
      return o.default.createElement(
        "div",
        bT(
          {
            className: c(
              "CardMedia",
              {
                "CardMedia--wide": "wide" === r,
                "CardMedia--square": "square" === r,
              },
              t,
            ),
            style: s,
          },
          l,
        ),
        u &&
          o.default.createElement(
            lC,
            { className: "CardMedia-content", direction: "column", center: !0 },
            u,
          ),
      );
    }),
    (e.CardText = function (e) {
      var t = e.className,
        n = e.children,
        r = YT(e, gC);
      return o.default.createElement(
        "div",
        bT({ className: c("CardText", t) }, r),
        "string" == typeof n ? o.default.createElement("p", null, n) : n,
      );
    }),
    (e.CardTitle = function (e) {
      var t = e.className,
        n = e.title,
        r = e.subtitle,
        a = e.center,
        i = e.children,
        u = YT(e, hC);
      return o.default.createElement(
        "div",
        bT({ className: c("CardTitle", { "CardTitle--center": a }, t) }, u),
        n && o.default.createElement("h5", { className: "CardTitle-title" }, n),
        i &&
          "string" == typeof i &&
          o.default.createElement("h5", { className: "CardTitle-title" }, i),
        r &&
          o.default.createElement("p", { className: "CardTitle-subtitle" }, r),
        i && "string" != typeof i && i,
      );
    }),
    (e.Carousel = YM),
    (e.Checkbox = XM),
    (e.CheckboxGroup = function (e) {
      var t = e.className,
        n = e.options,
        r = e.value,
        a = e.name,
        i = e.disabled,
        u = e.block,
        l = e.onChange;
      function s(e, t) {
        var n = t.target.checked
          ? Zx(r).call(r, e)
          : cN(r).call(r, function (t) {
              return t !== e;
            });
        l(n, t);
      }
      return o.default.createElement(
        "div",
        { className: c("CheckboxGroup", { "CheckboxGroup--block": u }, t) },
        Wx(n).call(n, function (e) {
          return o.default.createElement(XM, {
            label: e.label || e.value,
            value: e.value,
            name: a,
            checked: pM(r).call(r, e.value),
            disabled: "disabled" in e ? e.disabled : i,
            onChange: function (t) {
              s(e.value, t);
            },
            key: e.value,
          });
        }),
      );
    }),
    (e.ClickOutside = JM),
    (e.ComponentsProvider = function (e) {
      var n = e.components,
        r = e.children,
        a = o.default.useRef(YS({}, n));
      return (
        t.useEffect(
          function () {
            a.current = YS(YS({}, n), a.current);
          },
          [n],
        ),
        o.default.createElement(
          HS.Provider,
          {
            value: {
              addComponent: function (e, t) {
                a.current[e] = t;
              },
              hasComponent: function (e) {
                return a.current.hasOwnProperty(e);
              },
              getComponent: function (e) {
                var t =
                    arguments.length > 1 && void 0 !== arguments[1]
                      ? arguments[1]
                      : function () {},
                  n = a.current[e];
                if (!n) return t({ code: e, errCode: "NO_CODE" }), null;
                if ("component" in n)
                  return (
                    "decorator" !== n.type &&
                      t({ code: e, async: !1, component: n.component }),
                    n.component
                  );
                if ("decorator" in n) {
                  var r = function (e) {
                    return o.default.createElement(
                      GS,
                      bT(
                        { code: n.decorator, decoratorData: n.data, onLoad: t },
                        e,
                      ),
                    );
                  };
                  return (
                    (a.current[e] = { component: r, type: "decorator" }), r
                  );
                }
                if ("url" in n) {
                  var i = Ld(
                    n.url,
                    n.name,
                    function () {
                      (a.current[e] = { component: i }),
                        t({ code: e, async: !0, component: i });
                    },
                    function () {
                      t({ code: e, errCode: "ERR_IMPORT_SCRIPT" });
                    },
                  );
                  return i;
                }
                return t({ code: e, errCode: "NO_HANDLER" }), null;
              },
            },
          },
          r,
        )
      );
    }),
    (e.Confirm = function (e) {
      var t = e.className,
        n = YT(e, xD);
      return o.default.createElement(
        ED,
        bT(
          {
            baseClass: "Modal",
            className: c("Confirm", t),
            showClose: !1,
            btnVariant: "outline",
          },
          n,
        ),
      );
    }),
    (e.DOMPurify = ce),
    (e.Divider = function (e) {
      var t = e.className,
        n = e.position,
        r = void 0 === n ? "center" : n,
        a = e.children,
        i = YT(e, QM);
      return o.default.createElement(
        "div",
        bT(
          {
            className: c("Divider", !!a && "Divider--text-".concat(r), t),
            role: "separator",
          },
          i,
        ),
        a,
      );
    }),
    (e.Empty = function (e) {
      var t = e.className,
        n = e.type,
        r = e.image,
        a = e.tip,
        i = e.children,
        u =
          r ||
          ("error" === n
            ? "//gw.alicdn.com/tfs/TB1lRjJRbvpK1RjSZPiXXbmwXXa-300-250.svg"
            : "//gw.alicdn.com/tfs/TB1fnnLRkvoK1RjSZFDXXXY3pXa-300-250.svg");
      return o.default.createElement(
        lC,
        { className: c("Empty", t), direction: "column", center: !0 },
        o.default.createElement("img", {
          className: "Empty-img",
          src: u,
          alt: a,
        }),
        a && o.default.createElement("p", { className: "Empty-tip" }, a),
        i,
      );
    }),
    (e.ErrorBoundary = PS),
    (e.FileCard = function (e) {
      var t,
        n = e.className,
        r = e.file,
        a = e.extension,
        i = e.children,
        u = r.name,
        l = r.size,
        s = a || OF((t = u)).call(t, 2 + ((VF(t).call(t, ".") - 1) >>> 0));
      return o.default.createElement(
        rC,
        { className: c("FileCard", n), size: "xl" },
        o.default.createElement(
          lC,
          null,
          o.default.createElement(
            "div",
            { className: "FileCard-icon", "data-type": s },
            o.default.createElement(QS, { type: "file" }),
            o.default.createElement(
              ID,
              { truncate: !0, as: "span", className: "FileCard-ext" },
              s,
            ),
          ),
          o.default.createElement(
            pC,
            null,
            o.default.createElement(
              ID,
              { truncate: 2, breakWord: !0, className: "FileCard-name" },
              u,
            ),
            o.default.createElement(
              "div",
              { className: "FileCard-meta" },
              null != l &&
                o.default.createElement(
                  "span",
                  { className: "FileCard-size" },
                  (function (e, t) {
                    var n, r;
                    if (e < 1)
                      return Zx((r = "".concat(e, " "))).call(r, rH[0]);
                    var o = t || 2,
                      a = Math.floor(Math.log(e) / Math.log(1024));
                    return Zx(
                      (n = "".concat(
                        nH((e / Math.pow(1024, a)).toFixed(o)),
                        " ",
                      )),
                    ).call(n, rH[a]);
                  })(l),
                ),
              i,
            ),
          ),
        ),
      );
    }),
    (e.Flex = lC),
    (e.FlexItem = pC),
    (e.Form = function (e) {
      var t = e.className,
        n = e.theme,
        r = void 0 === n ? "" : n,
        a = e.children,
        i = YT(e, ZM);
      return o.default.createElement(
        e_.Provider,
        { value: r },
        o.default.createElement(
          "form",
          bT({ className: c("Form", { "is-light": "light" === r }, t) }, i),
          a,
        ),
      );
    }),
    (e.FormActions = function (e) {
      var t = e.children,
        n = YT(e, a_);
      return o.default.createElement(
        "div",
        bT({ className: c("FormActions") }, n),
        t,
      );
    }),
    (e.FormItem = function (e) {
      var t = e.label,
        n = e.help,
        r = e.required,
        a = e.invalid,
        i = e.hidden,
        u = e.children;
      return o.default.createElement(
        "div",
        {
          className: c("FormItem", { required: r, "is-invalid": a }),
          hidden: i,
        },
        t && o.default.createElement(n_, null, t),
        u,
        n && o.default.createElement(o_, null, n),
      );
    }),
    (e.Goods = aH),
    (e.Icon = QS),
    (e.IconButton = c_),
    (e.Image = l_),
    (e.InfiniteScroll = d_),
    (e.Input = uP),
    (e.LazyComponent = function (e) {
      var t = e.component,
        n = e.code,
        r = e.onLoad,
        a = YT(e, zS);
      return t
        ? (r && r({ async: !1, component: t }),
          o.default.createElement(FS, bT({ component: t }, a)))
        : o.default.createElement(GS, bT({ code: n, onLoad: r }, a));
    }),
    (e.List = function (e) {
      var t = e.bordered,
        n = void 0 !== t && t,
        r = e.className,
        a = e.children;
      return o.default.createElement(
        "div",
        { className: c("List", { "List--bordered": n }, r), role: "list" },
        a,
      );
    }),
    (e.ListItem = function (e) {
      var t = e.className,
        n = e.as,
        r = void 0 === n ? "div" : n,
        a = e.content,
        i = e.rightIcon,
        u = e.children,
        l = e.onClick,
        s = YT(e, lP);
      return o.default.createElement(
        r,
        bT({ className: c("ListItem", t), onClick: l, role: "listitem" }, s),
        o.default.createElement(
          "div",
          { className: "ListItem-content" },
          a || u,
        ),
        i && o.default.createElement(QS, { type: i }),
      );
    }),
    (e.Loading = function (e) {
      var t = e.tip,
        n = e.children;
      return o.default.createElement(
        lC,
        { className: "Loading", center: !0 },
        o.default.createElement(QS, { type: "spinner", spin: !0 }),
        t && o.default.createElement("p", { className: "Loading-tip" }, t),
        n,
      );
    }),
    (e.LocaleContext = JP),
    (e.LocaleProvider = ZP),
    (e.MediaObject = function (e) {
      var t = e.className,
        n = e.picUrl,
        r = e.picSize,
        a = e.title,
        i = e.picAlt,
        u = e.meta;
      return o.default.createElement(
        "div",
        { className: c("MediaObject", t) },
        n &&
          o.default.createElement(
            "div",
            {
              className: c(
                "MediaObject-pic",
                r && "MediaObject-pic--".concat(r),
              ),
            },
            o.default.createElement("img", { src: n, alt: i || a }),
          ),
        o.default.createElement(
          "div",
          { className: "MediaObject-info" },
          o.default.createElement("h3", { className: "MediaObject-title" }, a),
          o.default.createElement("div", { className: "MediaObject-meta" }, u),
        ),
      );
    }),
    (e.Message = mD),
    (e.MessageStatus = function (e) {
      var n = e.status,
        r = e.delay,
        a = void 0 === r ? 1500 : r,
        i = e.maxDelay,
        c = void 0 === i ? 5e3 : i,
        u = e.onRetry,
        l = e.onChange,
        s = _x(t.useState(""), 2),
        f = s[0],
        d = s[1],
        p = t.useRef(),
        m = t.useRef(),
        v = t.useCallback(
          function () {
            (p.current = setTimeout(function () {
              d("loading");
            }, a)),
              (m.current = setTimeout(function () {
                d("fail");
              }, c));
          },
          [a, c],
        );
      function h() {
        p.current && clearTimeout(p.current),
          m.current && clearTimeout(m.current);
      }
      function g() {
        d("loading"), v(), u && u();
      }
      return (
        t.useEffect(
          function () {
            return (
              h(),
              "pending" === n
                ? v()
                : "sent" === n
                  ? d("")
                  : "fail" === n && d("fail"),
              h
            );
          },
          [n, v],
        ),
        t.useEffect(
          function () {
            l && l(f);
          },
          [l, f],
        ),
        f
          ? o.default.createElement(
              "div",
              { className: "MessageStatus", "data-status": f },
              "fail" === f
                ? o.default.createElement(c_, {
                    icon: "warning-circle-fill",
                    onClick: g,
                  })
                : o.default.createElement(QS, {
                    type: "spinner",
                    spin: !0,
                    onClick: g,
                  }),
            )
          : null
      );
    }),
    (e.Modal = wD),
    (e.Navbar = ND),
    (e.Notice = function (e) {
      var t = e.content,
        n = e.closable,
        r = void 0 === n || n,
        a = e.leftIcon,
        i = void 0 === a ? "bullhorn" : a,
        c = e.onClick,
        u = e.onClose;
      return o.default.createElement(
        "div",
        {
          className: "Notice",
          role: "alert",
          "aria-atomic": !0,
          "aria-live": "assertive",
        },
        i && o.default.createElement(QS, { className: "Notice-icon", type: i }),
        o.default.createElement(
          "div",
          { className: "Notice-content", onClick: c },
          o.default.createElement(
            ID,
            { className: "Notice-text", truncate: !0 },
            t,
          ),
        ),
        r &&
          o.default.createElement(c_, {
            className: "Notice-close",
            icon: "close",
            onClick: u,
            "aria-label": "关闭通知",
          }),
      );
    }),
    (e.Popup = function (e) {
      return o.default.createElement(
        ED,
        bT({ baseClass: "Popup", overflow: !0 }, e),
      );
    }),
    (e.Portal = function (e) {
      var r = e.children,
        o = e.container,
        a = void 0 === o ? document.body : o,
        i = e.onRendered,
        c = _x(t.useState(null), 2),
        u = c[0],
        l = c[1];
      return (
        t.useEffect(
          function () {
            var e;
            l(
              (e = a)
                ? e instanceof Element
                  ? e
                  : "function" == typeof e
                    ? e()
                    : e.current || e
                : null,
            );
          },
          [a],
        ),
        t.useLayoutEffect(
          function () {
            i && u && i();
          },
          [u, i],
        ),
        u ? n.createPortal(r, u) : u
      );
    }),
    (e.Price = _D),
    (e.Progress = LD),
    (e.PullToRefresh = UD),
    (e.QuickReplies = $D),
    (e.Radio = JD),
    (e.RadioGroup = function (e) {
      var t = e.className,
        n = e.options,
        r = e.value,
        a = e.name,
        i = e.disabled,
        u = e.block,
        l = e.onChange;
      return o.default.createElement(
        "div",
        { className: c("RadioGroup", { "RadioGroup--block": u }, t) },
        Wx(n).call(n, function (e) {
          return o.default.createElement(JD, {
            label: e.label || e.value,
            value: e.value,
            name: a,
            checked: r === e.value,
            disabled: "disabled" in e ? e.disabled : i,
            onChange: function (t) {
              l(e.value, t);
            },
            key: e.value,
          });
        }),
      );
    }),
    (e.RateActions = function (e) {
      var n = eD("RateActions", { up: "赞同", down: "反对" }).trans,
        r = e.upTitle,
        a = void 0 === r ? n("up") : r,
        i = e.downTitle,
        u = void 0 === i ? n("down") : i,
        l = e.onClick,
        s = _x(t.useState(""), 2),
        f = s[0],
        d = s[1];
      function p(e) {
        f || (d(e), l(e));
      }
      return o.default.createElement(
        "div",
        { className: "RateActions" },
        f !== ZD &&
          o.default.createElement(c_, {
            className: c("RateBtn", { active: f === QD }),
            title: a,
            "data-type": QD,
            icon: "thumbs-up",
            onClick: function () {
              p(QD);
            },
          }),
        f !== QD &&
          o.default.createElement(c_, {
            className: c("RateBtn", { active: f === ZD }),
            title: u,
            "data-type": ZD,
            icon: "thumbs-down",
            onClick: function () {
              p(ZD);
            },
          }),
      );
    }),
    (e.RichText = tF),
    (e.ScrollView = WD),
    (e.Search = function (e) {
      var n = e.className,
        r = e.onSearch,
        a = e.onChange,
        i = e.onClear,
        u = e.value,
        l = e.clearable,
        s = void 0 === l || l,
        f = e.showSearch,
        d = void 0 === f || f,
        p = YT(e, nF),
        m = _x(t.useState(u || ""), 2),
        v = m[0],
        h = m[1],
        g = eD("Search").trans;
      return o.default.createElement(
        "div",
        { className: c("Search", n) },
        o.default.createElement(QS, {
          className: "Search-icon",
          type: "search",
        }),
        o.default.createElement(
          uP,
          bT(
            {
              className: "Search-input",
              type: "search",
              value: v,
              enterKeyHint: "search",
              onChange: function (e) {
                h(e), a && a(e);
              },
              onKeyDown: function (e) {
                13 === e.keyCode && (r && r(v, e), e.preventDefault());
              },
            },
            p,
          ),
        ),
        s &&
          v &&
          o.default.createElement(c_, {
            className: "Search-clear",
            icon: "x-circle-fill",
            onClick: function () {
              h(""), i && i();
            },
          }),
        d &&
          o.default.createElement(
            tC,
            {
              className: "Search-btn",
              color: "primary",
              onClick: function (e) {
                r && r(v, e);
              },
            },
            g("search"),
          ),
      );
    }),
    (e.Select = oF),
    (e.Step = sF),
    (e.Stepper = uF),
    (e.SystemMessage = tD),
    (e.Tab = function (e) {
      var t = e.children;
      return o.default.createElement("div", null, t);
    }),
    (e.Tabs = function (e) {
      var n = e.className,
        r = e.index,
        a = void 0 === r ? 0 : r,
        i = e.scrollable,
        u = e.hideNavIfOnlyOne,
        l = e.children,
        s = e.onChange,
        f = _x(t.useState({}), 2),
        d = f[0],
        p = f[1],
        m = _x(t.useState(a || 0), 2),
        v = m[0],
        h = m[1],
        g = t.useRef(v),
        y = t.useRef(null),
        b = [],
        E = [],
        w = gD("tabs-");
      function x(e, t) {
        h(e), s && s(e, t);
      }
      o.default.Children.forEach(l, function (e, t) {
        var n;
        if (e) {
          var r = v === t,
            a = Zx((n = "".concat(w, "-"))).call(n, t);
          b.push(
            o.default.createElement(
              pF,
              {
                active: r,
                index: t,
                key: a,
                onClick: x,
                "aria-controls": a,
                tabIndex: r ? -1 : 0,
              },
              e.props.label,
            ),
          ),
            e.props.children &&
              E.push(
                o.default.createElement(
                  mF,
                  { active: r, key: a, id: a },
                  e.props.children,
                ),
              );
        }
      }),
        t.useEffect(
          function () {
            h(a);
          },
          [a],
        );
      var N = t.useCallback(
        function () {
          var e = y.current;
          if (e) {
            var t = e.children[g.current];
            if (t) {
              var n = t.querySelector("span");
              if (n) {
                var r = t,
                  o = r.offsetWidth,
                  a = r.offsetLeft,
                  c = n.getBoundingClientRect().width,
                  u = Math.max(c - 16, 26),
                  l = a + o / 2;
                p({
                  transform: "translateX(".concat(l - u / 2, "px)"),
                  width: "".concat(u, "px"),
                }),
                  i && DD({ el: e, to: l - e.offsetWidth / 2, x: !0 });
              }
            }
          }
        },
        [i],
      );
      t.useEffect(
        function () {
          var e,
            t = y.current;
          return (
            t &&
              "ResizeObserver" in window &&
              (e = new ResizeObserver(N)).observe(t),
            function () {
              e && t && e.unobserve(t);
            }
          );
        },
        [N],
      ),
        t.useEffect(
          function () {
            (g.current = v), N();
          },
          [v, N],
        );
      var T = b.length > (u ? 1 : 0);
      return o.default.createElement(
        "div",
        { className: c("Tabs", { "Tabs--scrollable": i }, n) },
        T &&
          o.default.createElement(
            "div",
            { className: "Tabs-nav", role: "tablist", ref: y },
            b,
            o.default.createElement("span", {
              className: "Tabs-navPointer",
              style: d,
            }),
          ),
        o.default.createElement("div", { className: "Tabs-content" }, E),
      );
    }),
    (e.Tag = hF),
    (e.Text = ID),
    (e.Time = sD),
    (e.Toast = gF),
    (e.Toolbar = wF),
    (e.Tree = function (e) {
      var t = e.className,
        n = e.children;
      return o.default.createElement(
        "div",
        { className: c("Tree", t), role: "tree" },
        n,
      );
    }),
    (e.TreeNode = function (e) {
      var n = e.title,
        r = e.content,
        a = e.link,
        i = e.children,
        u = void 0 === i ? [] : i,
        l = e.onClick,
        s = e.onExpand,
        f = _x(t.useState(!1), 2),
        d = f[0],
        p = f[1],
        m = u.length > 0;
      return o.default.createElement(
        "div",
        { className: "TreeNode", role: "treeitem", "aria-expanded": d },
        o.default.createElement(
          "div",
          {
            className: "TreeNode-title",
            onClick: function () {
              m ? (p(!d), s(n, !d)) : l({ title: n, content: r, link: a });
            },
            role: "treeitem",
            "aria-expanded": d,
            tabIndex: 0,
          },
          o.default.createElement(
            "span",
            { className: "TreeNode-title-text" },
            n,
          ),
          m
            ? o.default.createElement(QS, {
                className: "TreeNode-title-icon",
                type: d ? "chevron-up" : "chevron-down",
              })
            : null,
        ),
        m
          ? Wx(u).call(u, function (e, t) {
              return o.default.createElement(
                "div",
                {
                  className: c("TreeNode-children", {
                    "TreeNode-children-active": d,
                  }),
                  key: t,
                },
                o.default.createElement(
                  "div",
                  {
                    className: "TreeNode-title TreeNode-children-title",
                    onClick: function () {
                      return l(NF(NF({}, e), { index: t }));
                    },
                    role: "treeitem",
                  },
                  o.default.createElement(
                    "span",
                    { className: "TreeNode-title-text" },
                    e.title,
                  ),
                ),
              );
            })
          : null,
      );
    }),
    (e.Typing = fD),
    (e.Video = function (e) {
      var n = e.className,
        r = e.src,
        a = e.cover,
        i = e.duration,
        u = e.onClick,
        l = e.onCoverLoad,
        s = e.style,
        f = e.videoRef,
        d = e.children,
        p = YT(e, TF),
        m = t.useRef(null),
        v = f || m,
        h = _x(t.useState(!1), 2),
        g = h[0],
        y = h[1],
        b = _x(t.useState(!0), 2),
        E = b[0],
        w = b[1];
      function x() {
        w(!0);
      }
      var N = !g && !!a,
        T = N && !!i;
      return o.default.createElement(
        "div",
        {
          className: c("Video", "Video--".concat(E ? "paused" : "playing"), n),
          style: s,
        },
        N &&
          o.default.createElement("img", {
            className: "Video-cover",
            src: a,
            onLoad: l,
            alt: "",
          }),
        T &&
          o.default.createElement("span", { className: "Video-duration" }, i),
        o.default.createElement(
          "video",
          bT(
            {
              className: "Video-video",
              src: r,
              ref: v,
              hidden: N,
              controls: !0,
              onPlay: function () {
                w(!1);
              },
              onPause: x,
              onEnded: x,
            },
            p,
          ),
          d,
        ),
        N &&
          o.default.createElement(
            "button",
            {
              className: c("Video-playBtn", { paused: E }),
              type: "button",
              onClick: function (e) {
                y(!0);
                var t = v.current;
                t && (t.ended || t.paused ? t.play() : t.pause()), u && u(E, e);
              },
            },
            o.default.createElement("span", { className: "Video-playIcon" }),
          ),
      );
    }),
    (e.VisuallyHidden = function (e) {
      return o.default.createElement("span", bT({ style: SF }, e));
    }),
    (e.clsx = c),
    (e.default = LH),
    (e.importScript = jd),
    (e.lazyComponent = Ld),
    (e.mountComponent = Pd),
    (e.toast = bF),
    (e.useClickOutside = Dd),
    (e.useComponents = BS),
    (e.useForwardRef = Fd),
    (e.useLocale = eD),
    (e.useMessages = function () {
      var e =
          arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : [],
        n = t.useMemo(
          function () {
            return Wx(e).call(e, function (e) {
              return AN(e);
            });
          },
          [e],
        ),
        r = t.useState(n),
        o = _x(r, 2),
        a = o[0],
        i = o[1],
        c = t.useRef(!1),
        u = t.useCallback(function (e) {
          i(function (t) {
            var n;
            return Zx((n = [])).call(n, Mx(e), Mx(t));
          });
        }, []),
        l = t.useCallback(function (e, t) {
          i(function (n) {
            return Wx(n).call(n, function (n) {
              return n._id === e ? AN(t, e) : n;
            });
          });
        }, []),
        s = t.useCallback(
          function (e) {
            var t = AN(e);
            c.current
              ? ((c.current = !1), l(MN, t))
              : i(function (e) {
                  var n;
                  return Zx((n = [])).call(n, Mx(e), [t]);
                });
          },
          [l],
        ),
        f = t.useCallback(function (e) {
          i(function (t) {
            return cN(t).call(t, function (t) {
              return t._id !== e;
            });
          });
        }, []),
        d = t.useCallback(function () {
          var e =
            arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : [];
          i(e);
        }, []),
        p = t.useCallback(
          function (e) {
            e !== c.current &&
              (e ? s({ _id: MN, type: "typing" }) : f(MN), (c.current = e));
          },
          [s, f],
        );
      return {
        messages: a,
        prependMsgs: u,
        appendMsg: s,
        updateMsg: l,
        deleteMsg: f,
        resetList: d,
        setTyping: p,
      };
    }),
    (e.useMount = _N),
    (e.useQuickReplies = function () {
      var e =
          arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : [],
        n = t.useState(e),
        r = _x(n, 2),
        o = r[0],
        a = r[1],
        i = t.useState(!0),
        c = _x(i, 2),
        u = c[0],
        l = c[1],
        s = t.useRef(),
        f = t.useRef();
      t.useEffect(
        function () {
          s.current = o;
        },
        [o],
      );
      var d = function (e) {
          a(function (t) {
            var n;
            return Zx((n = [])).call(n, Mx(e), Mx(t));
          });
        },
        p = function () {
          f.current = s.current;
        },
        m = function () {
          f.current && a(f.current);
        };
      return {
        quickReplies: o,
        prepend: d,
        replace: a,
        visible: u,
        setVisible: l,
        save: p,
        pop: m,
      };
    }),
    Object.defineProperty(e, "__esModule", { value: !0 });
});
