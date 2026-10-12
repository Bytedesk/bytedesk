const scriptRel = /* @__PURE__ */ (function detectScriptRel() {
  const relList = typeof document !== "undefined" && document.createElement("link").relList;
  return relList && relList.supports && relList.supports("modulepreload") ? "modulepreload" : "preload";
})();
const assetsURL = function(dep, importerUrl) {
  return new URL(dep, importerUrl).href;
};
const seen = {};
const __vitePreload = function preload(baseModule, deps, importerUrl) {
  let promise = Promise.resolve();
  if (deps && deps.length > 0) {
    let allSettled = function(promises$2) {
      return Promise.all(promises$2.map((p) => Promise.resolve(p).then((value$1) => ({
        status: "fulfilled",
        value: value$1
      }), (reason) => ({
        status: "rejected",
        reason
      }))));
    };
    const links = document.getElementsByTagName("link");
    const cspNonceMeta = document.querySelector("meta[property=csp-nonce]");
    const cspNonce = cspNonceMeta?.nonce || cspNonceMeta?.getAttribute("nonce");
    promise = allSettled(deps.map((dep) => {
      dep = assetsURL(dep, importerUrl);
      if (dep in seen) return;
      seen[dep] = true;
      const isCss = dep.endsWith(".css");
      const cssSelector = isCss ? '[rel="stylesheet"]' : "";
      if (!!importerUrl) for (let i$1 = links.length - 1; i$1 >= 0; i$1--) {
        const link$1 = links[i$1];
        if (link$1.href === dep && (!isCss || link$1.rel === "stylesheet")) return;
      }
      else if (document.querySelector(`link[href="${dep}"]${cssSelector}`)) return;
      const link = document.createElement("link");
      link.rel = isCss ? "stylesheet" : scriptRel;
      if (!isCss) link.as = "script";
      link.crossOrigin = "";
      link.href = dep;
      if (cspNonce) link.setAttribute("nonce", cspNonce);
      document.head.appendChild(link);
      if (isCss) return new Promise((res, rej) => {
        link.addEventListener("load", res);
        link.addEventListener("error", () => rej(/* @__PURE__ */ new Error(`Unable to preload CSS for ${dep}`)));
      });
    }));
  }
  function handlePreloadError(err$2) {
    const e$1 = new Event("vite:preloadError", { cancelable: true });
    e$1.payload = err$2;
    window.dispatchEvent(e$1);
    if (!e$1.defaultPrevented) throw err$2;
  }
  return promise.then((res) => {
    for (const item of res || []) {
      if (item.status !== "rejected") continue;
      handlePreloadError(item.reason);
    }
    return baseModule().catch(handlePreloadError);
  });
};
function getDefaultExportFromCjs(x) {
  return x && x.__esModule && Object.prototype.hasOwnProperty.call(x, "default") ? x["default"] : x;
}
var jsxRuntime = { exports: {} };
var reactJsxRuntime_production = {};
var hasRequiredReactJsxRuntime_production;
function requireReactJsxRuntime_production() {
  if (hasRequiredReactJsxRuntime_production) return reactJsxRuntime_production;
  hasRequiredReactJsxRuntime_production = 1;
  var REACT_ELEMENT_TYPE = /* @__PURE__ */ Symbol.for("react.transitional.element"), REACT_FRAGMENT_TYPE = /* @__PURE__ */ Symbol.for("react.fragment");
  function jsxProd(type, config, maybeKey) {
    var key = null;
    void 0 !== maybeKey && (key = "" + maybeKey);
    void 0 !== config.key && (key = "" + config.key);
    if ("key" in config) {
      maybeKey = {};
      for (var propName in config)
        "key" !== propName && (maybeKey[propName] = config[propName]);
    } else maybeKey = config;
    config = maybeKey.ref;
    return {
      $$typeof: REACT_ELEMENT_TYPE,
      type,
      key,
      ref: void 0 !== config ? config : null,
      props: maybeKey
    };
  }
  reactJsxRuntime_production.Fragment = REACT_FRAGMENT_TYPE;
  reactJsxRuntime_production.jsx = jsxProd;
  reactJsxRuntime_production.jsxs = jsxProd;
  return reactJsxRuntime_production;
}
var hasRequiredJsxRuntime;
function requireJsxRuntime() {
  if (hasRequiredJsxRuntime) return jsxRuntime.exports;
  hasRequiredJsxRuntime = 1;
  {
    jsxRuntime.exports = requireReactJsxRuntime_production();
  }
  return jsxRuntime.exports;
}
var jsxRuntimeExports = requireJsxRuntime();
var react = { exports: {} };
var react_production = {};
var hasRequiredReact_production;
function requireReact_production() {
  if (hasRequiredReact_production) return react_production;
  hasRequiredReact_production = 1;
  var REACT_ELEMENT_TYPE = /* @__PURE__ */ Symbol.for("react.transitional.element"), REACT_PORTAL_TYPE = /* @__PURE__ */ Symbol.for("react.portal"), REACT_FRAGMENT_TYPE = /* @__PURE__ */ Symbol.for("react.fragment"), REACT_STRICT_MODE_TYPE = /* @__PURE__ */ Symbol.for("react.strict_mode"), REACT_PROFILER_TYPE = /* @__PURE__ */ Symbol.for("react.profiler"), REACT_CONSUMER_TYPE = /* @__PURE__ */ Symbol.for("react.consumer"), REACT_CONTEXT_TYPE = /* @__PURE__ */ Symbol.for("react.context"), REACT_FORWARD_REF_TYPE = /* @__PURE__ */ Symbol.for("react.forward_ref"), REACT_SUSPENSE_TYPE = /* @__PURE__ */ Symbol.for("react.suspense"), REACT_MEMO_TYPE = /* @__PURE__ */ Symbol.for("react.memo"), REACT_LAZY_TYPE = /* @__PURE__ */ Symbol.for("react.lazy"), REACT_ACTIVITY_TYPE = /* @__PURE__ */ Symbol.for("react.activity"), MAYBE_ITERATOR_SYMBOL = Symbol.iterator;
  function getIteratorFn(maybeIterable) {
    if (null === maybeIterable || "object" !== typeof maybeIterable) return null;
    maybeIterable = MAYBE_ITERATOR_SYMBOL && maybeIterable[MAYBE_ITERATOR_SYMBOL] || maybeIterable["@@iterator"];
    return "function" === typeof maybeIterable ? maybeIterable : null;
  }
  var ReactNoopUpdateQueue = {
    isMounted: function() {
      return false;
    },
    enqueueForceUpdate: function() {
    },
    enqueueReplaceState: function() {
    },
    enqueueSetState: function() {
    }
  }, assign = Object.assign, emptyObject = {};
  function Component(props, context, updater) {
    this.props = props;
    this.context = context;
    this.refs = emptyObject;
    this.updater = updater || ReactNoopUpdateQueue;
  }
  Component.prototype.isReactComponent = {};
  Component.prototype.setState = function(partialState, callback) {
    if ("object" !== typeof partialState && "function" !== typeof partialState && null != partialState)
      throw Error(
        "takes an object of state variables to update or a function which returns an object of state variables."
      );
    this.updater.enqueueSetState(this, partialState, callback, "setState");
  };
  Component.prototype.forceUpdate = function(callback) {
    this.updater.enqueueForceUpdate(this, callback, "forceUpdate");
  };
  function ComponentDummy() {
  }
  ComponentDummy.prototype = Component.prototype;
  function PureComponent(props, context, updater) {
    this.props = props;
    this.context = context;
    this.refs = emptyObject;
    this.updater = updater || ReactNoopUpdateQueue;
  }
  var pureComponentPrototype = PureComponent.prototype = new ComponentDummy();
  pureComponentPrototype.constructor = PureComponent;
  assign(pureComponentPrototype, Component.prototype);
  pureComponentPrototype.isPureReactComponent = true;
  var isArrayImpl = Array.isArray;
  function noop() {
  }
  var ReactSharedInternals = { H: null, A: null, T: null, S: null }, hasOwnProperty = Object.prototype.hasOwnProperty;
  function ReactElement(type, key, props) {
    var refProp = props.ref;
    return {
      $$typeof: REACT_ELEMENT_TYPE,
      type,
      key,
      ref: void 0 !== refProp ? refProp : null,
      props
    };
  }
  function cloneAndReplaceKey(oldElement, newKey) {
    return ReactElement(oldElement.type, newKey, oldElement.props);
  }
  function isValidElement(object) {
    return "object" === typeof object && null !== object && object.$$typeof === REACT_ELEMENT_TYPE;
  }
  function escape(key) {
    var escaperLookup = { "=": "=0", ":": "=2" };
    return "$" + key.replace(/[=:]/g, function(match) {
      return escaperLookup[match];
    });
  }
  var userProvidedKeyEscapeRegex = /\/+/g;
  function getElementKey(element, index) {
    return "object" === typeof element && null !== element && null != element.key ? escape("" + element.key) : index.toString(36);
  }
  function resolveThenable(thenable) {
    switch (thenable.status) {
      case "fulfilled":
        return thenable.value;
      case "rejected":
        throw thenable.reason;
      default:
        switch ("string" === typeof thenable.status ? thenable.then(noop, noop) : (thenable.status = "pending", thenable.then(
          function(fulfilledValue) {
            "pending" === thenable.status && (thenable.status = "fulfilled", thenable.value = fulfilledValue);
          },
          function(error) {
            "pending" === thenable.status && (thenable.status = "rejected", thenable.reason = error);
          }
        )), thenable.status) {
          case "fulfilled":
            return thenable.value;
          case "rejected":
            throw thenable.reason;
        }
    }
    throw thenable;
  }
  function mapIntoArray(children, array, escapedPrefix, nameSoFar, callback) {
    var type = typeof children;
    if ("undefined" === type || "boolean" === type) children = null;
    var invokeCallback = false;
    if (null === children) invokeCallback = true;
    else
      switch (type) {
        case "bigint":
        case "string":
        case "number":
          invokeCallback = true;
          break;
        case "object":
          switch (children.$$typeof) {
            case REACT_ELEMENT_TYPE:
            case REACT_PORTAL_TYPE:
              invokeCallback = true;
              break;
            case REACT_LAZY_TYPE:
              return invokeCallback = children._init, mapIntoArray(
                invokeCallback(children._payload),
                array,
                escapedPrefix,
                nameSoFar,
                callback
              );
          }
      }
    if (invokeCallback)
      return callback = callback(children), invokeCallback = "" === nameSoFar ? "." + getElementKey(children, 0) : nameSoFar, isArrayImpl(callback) ? (escapedPrefix = "", null != invokeCallback && (escapedPrefix = invokeCallback.replace(userProvidedKeyEscapeRegex, "$&/") + "/"), mapIntoArray(callback, array, escapedPrefix, "", function(c) {
        return c;
      })) : null != callback && (isValidElement(callback) && (callback = cloneAndReplaceKey(
        callback,
        escapedPrefix + (null == callback.key || children && children.key === callback.key ? "" : ("" + callback.key).replace(
          userProvidedKeyEscapeRegex,
          "$&/"
        ) + "/") + invokeCallback
      )), array.push(callback)), 1;
    invokeCallback = 0;
    var nextNamePrefix = "" === nameSoFar ? "." : nameSoFar + ":";
    if (isArrayImpl(children))
      for (var i = 0; i < children.length; i++)
        nameSoFar = children[i], type = nextNamePrefix + getElementKey(nameSoFar, i), invokeCallback += mapIntoArray(
          nameSoFar,
          array,
          escapedPrefix,
          type,
          callback
        );
    else if (i = getIteratorFn(children), "function" === typeof i)
      for (children = i.call(children), i = 0; !(nameSoFar = children.next()).done; )
        nameSoFar = nameSoFar.value, type = nextNamePrefix + getElementKey(nameSoFar, i++), invokeCallback += mapIntoArray(
          nameSoFar,
          array,
          escapedPrefix,
          type,
          callback
        );
    else if ("object" === type) {
      if ("function" === typeof children.then)
        return mapIntoArray(
          resolveThenable(children),
          array,
          escapedPrefix,
          nameSoFar,
          callback
        );
      array = String(children);
      throw Error(
        "Objects are not valid as a React child (found: " + ("[object Object]" === array ? "object with keys {" + Object.keys(children).join(", ") + "}" : array) + "). If you meant to render a collection of children, use an array instead."
      );
    }
    return invokeCallback;
  }
  function mapChildren(children, func, context) {
    if (null == children) return children;
    var result = [], count = 0;
    mapIntoArray(children, result, "", "", function(child) {
      return func.call(context, child, count++);
    });
    return result;
  }
  function lazyInitializer(payload) {
    if (-1 === payload._status) {
      var ctor = payload._result;
      ctor = ctor();
      ctor.then(
        function(moduleObject) {
          if (0 === payload._status || -1 === payload._status)
            payload._status = 1, payload._result = moduleObject;
        },
        function(error) {
          if (0 === payload._status || -1 === payload._status)
            payload._status = 2, payload._result = error;
        }
      );
      -1 === payload._status && (payload._status = 0, payload._result = ctor);
    }
    if (1 === payload._status) return payload._result.default;
    throw payload._result;
  }
  var reportGlobalError = "function" === typeof reportError ? reportError : function(error) {
    if ("object" === typeof window && "function" === typeof window.ErrorEvent) {
      var event = new window.ErrorEvent("error", {
        bubbles: true,
        cancelable: true,
        message: "object" === typeof error && null !== error && "string" === typeof error.message ? String(error.message) : String(error),
        error
      });
      if (!window.dispatchEvent(event)) return;
    } else if ("object" === typeof process && "function" === typeof process.emit) {
      process.emit("uncaughtException", error);
      return;
    }
    console.error(error);
  }, Children = {
    map: mapChildren,
    forEach: function(children, forEachFunc, forEachContext) {
      mapChildren(
        children,
        function() {
          forEachFunc.apply(this, arguments);
        },
        forEachContext
      );
    },
    count: function(children) {
      var n = 0;
      mapChildren(children, function() {
        n++;
      });
      return n;
    },
    toArray: function(children) {
      return mapChildren(children, function(child) {
        return child;
      }) || [];
    },
    only: function(children) {
      if (!isValidElement(children))
        throw Error(
          "React.Children.only expected to receive a single React element child."
        );
      return children;
    }
  };
  react_production.Activity = REACT_ACTIVITY_TYPE;
  react_production.Children = Children;
  react_production.Component = Component;
  react_production.Fragment = REACT_FRAGMENT_TYPE;
  react_production.Profiler = REACT_PROFILER_TYPE;
  react_production.PureComponent = PureComponent;
  react_production.StrictMode = REACT_STRICT_MODE_TYPE;
  react_production.Suspense = REACT_SUSPENSE_TYPE;
  react_production.__CLIENT_INTERNALS_DO_NOT_USE_OR_WARN_USERS_THEY_CANNOT_UPGRADE = ReactSharedInternals;
  react_production.__COMPILER_RUNTIME = {
    __proto__: null,
    c: function(size) {
      return ReactSharedInternals.H.useMemoCache(size);
    }
  };
  react_production.cache = function(fn) {
    return function() {
      return fn.apply(null, arguments);
    };
  };
  react_production.cacheSignal = function() {
    return null;
  };
  react_production.cloneElement = function(element, config, children) {
    if (null === element || void 0 === element)
      throw Error(
        "The argument must be a React element, but you passed " + element + "."
      );
    var props = assign({}, element.props), key = element.key;
    if (null != config)
      for (propName in void 0 !== config.key && (key = "" + config.key), config)
        !hasOwnProperty.call(config, propName) || "key" === propName || "__self" === propName || "__source" === propName || "ref" === propName && void 0 === config.ref || (props[propName] = config[propName]);
    var propName = arguments.length - 2;
    if (1 === propName) props.children = children;
    else if (1 < propName) {
      for (var childArray = Array(propName), i = 0; i < propName; i++)
        childArray[i] = arguments[i + 2];
      props.children = childArray;
    }
    return ReactElement(element.type, key, props);
  };
  react_production.createContext = function(defaultValue) {
    defaultValue = {
      $$typeof: REACT_CONTEXT_TYPE,
      _currentValue: defaultValue,
      _currentValue2: defaultValue,
      _threadCount: 0,
      Provider: null,
      Consumer: null
    };
    defaultValue.Provider = defaultValue;
    defaultValue.Consumer = {
      $$typeof: REACT_CONSUMER_TYPE,
      _context: defaultValue
    };
    return defaultValue;
  };
  react_production.createElement = function(type, config, children) {
    var propName, props = {}, key = null;
    if (null != config)
      for (propName in void 0 !== config.key && (key = "" + config.key), config)
        hasOwnProperty.call(config, propName) && "key" !== propName && "__self" !== propName && "__source" !== propName && (props[propName] = config[propName]);
    var childrenLength = arguments.length - 2;
    if (1 === childrenLength) props.children = children;
    else if (1 < childrenLength) {
      for (var childArray = Array(childrenLength), i = 0; i < childrenLength; i++)
        childArray[i] = arguments[i + 2];
      props.children = childArray;
    }
    if (type && type.defaultProps)
      for (propName in childrenLength = type.defaultProps, childrenLength)
        void 0 === props[propName] && (props[propName] = childrenLength[propName]);
    return ReactElement(type, key, props);
  };
  react_production.createRef = function() {
    return { current: null };
  };
  react_production.forwardRef = function(render) {
    return { $$typeof: REACT_FORWARD_REF_TYPE, render };
  };
  react_production.isValidElement = isValidElement;
  react_production.lazy = function(ctor) {
    return {
      $$typeof: REACT_LAZY_TYPE,
      _payload: { _status: -1, _result: ctor },
      _init: lazyInitializer
    };
  };
  react_production.memo = function(type, compare) {
    return {
      $$typeof: REACT_MEMO_TYPE,
      type,
      compare: void 0 === compare ? null : compare
    };
  };
  react_production.startTransition = function(scope) {
    var prevTransition = ReactSharedInternals.T, currentTransition = {};
    ReactSharedInternals.T = currentTransition;
    try {
      var returnValue = scope(), onStartTransitionFinish = ReactSharedInternals.S;
      null !== onStartTransitionFinish && onStartTransitionFinish(currentTransition, returnValue);
      "object" === typeof returnValue && null !== returnValue && "function" === typeof returnValue.then && returnValue.then(noop, reportGlobalError);
    } catch (error) {
      reportGlobalError(error);
    } finally {
      null !== prevTransition && null !== currentTransition.types && (prevTransition.types = currentTransition.types), ReactSharedInternals.T = prevTransition;
    }
  };
  react_production.unstable_useCacheRefresh = function() {
    return ReactSharedInternals.H.useCacheRefresh();
  };
  react_production.use = function(usable) {
    return ReactSharedInternals.H.use(usable);
  };
  react_production.useActionState = function(action, initialState, permalink) {
    return ReactSharedInternals.H.useActionState(action, initialState, permalink);
  };
  react_production.useCallback = function(callback, deps) {
    return ReactSharedInternals.H.useCallback(callback, deps);
  };
  react_production.useContext = function(Context) {
    return ReactSharedInternals.H.useContext(Context);
  };
  react_production.useDebugValue = function() {
  };
  react_production.useDeferredValue = function(value, initialValue) {
    return ReactSharedInternals.H.useDeferredValue(value, initialValue);
  };
  react_production.useEffect = function(create, deps) {
    return ReactSharedInternals.H.useEffect(create, deps);
  };
  react_production.useEffectEvent = function(callback) {
    return ReactSharedInternals.H.useEffectEvent(callback);
  };
  react_production.useId = function() {
    return ReactSharedInternals.H.useId();
  };
  react_production.useImperativeHandle = function(ref, create, deps) {
    return ReactSharedInternals.H.useImperativeHandle(ref, create, deps);
  };
  react_production.useInsertionEffect = function(create, deps) {
    return ReactSharedInternals.H.useInsertionEffect(create, deps);
  };
  react_production.useLayoutEffect = function(create, deps) {
    return ReactSharedInternals.H.useLayoutEffect(create, deps);
  };
  react_production.useMemo = function(create, deps) {
    return ReactSharedInternals.H.useMemo(create, deps);
  };
  react_production.useOptimistic = function(passthrough, reducer) {
    return ReactSharedInternals.H.useOptimistic(passthrough, reducer);
  };
  react_production.useReducer = function(reducer, initialArg, init) {
    return ReactSharedInternals.H.useReducer(reducer, initialArg, init);
  };
  react_production.useRef = function(initialValue) {
    return ReactSharedInternals.H.useRef(initialValue);
  };
  react_production.useState = function(initialState) {
    return ReactSharedInternals.H.useState(initialState);
  };
  react_production.useSyncExternalStore = function(subscribe, getSnapshot, getServerSnapshot) {
    return ReactSharedInternals.H.useSyncExternalStore(
      subscribe,
      getSnapshot,
      getServerSnapshot
    );
  };
  react_production.useTransition = function() {
    return ReactSharedInternals.H.useTransition();
  };
  react_production.version = "19.2.7";
  return react_production;
}
var hasRequiredReact;
function requireReact() {
  if (hasRequiredReact) return react.exports;
  hasRequiredReact = 1;
  {
    react.exports = requireReact_production();
  }
  return react.exports;
}
var reactExports = requireReact();
const React = /* @__PURE__ */ getDefaultExportFromCjs(reactExports);
var client = { exports: {} };
var reactDomClient_production = {};
var scheduler = { exports: {} };
var scheduler_production = {};
var hasRequiredScheduler_production;
function requireScheduler_production() {
  if (hasRequiredScheduler_production) return scheduler_production;
  hasRequiredScheduler_production = 1;
  (function(exports) {
    function push(heap, node) {
      var index = heap.length;
      heap.push(node);
      a: for (; 0 < index; ) {
        var parentIndex = index - 1 >>> 1, parent = heap[parentIndex];
        if (0 < compare(parent, node))
          heap[parentIndex] = node, heap[index] = parent, index = parentIndex;
        else break a;
      }
    }
    function peek(heap) {
      return 0 === heap.length ? null : heap[0];
    }
    function pop(heap) {
      if (0 === heap.length) return null;
      var first = heap[0], last = heap.pop();
      if (last !== first) {
        heap[0] = last;
        a: for (var index = 0, length = heap.length, halfLength = length >>> 1; index < halfLength; ) {
          var leftIndex = 2 * (index + 1) - 1, left = heap[leftIndex], rightIndex = leftIndex + 1, right = heap[rightIndex];
          if (0 > compare(left, last))
            rightIndex < length && 0 > compare(right, left) ? (heap[index] = right, heap[rightIndex] = last, index = rightIndex) : (heap[index] = left, heap[leftIndex] = last, index = leftIndex);
          else if (rightIndex < length && 0 > compare(right, last))
            heap[index] = right, heap[rightIndex] = last, index = rightIndex;
          else break a;
        }
      }
      return first;
    }
    function compare(a, b) {
      var diff = a.sortIndex - b.sortIndex;
      return 0 !== diff ? diff : a.id - b.id;
    }
    exports.unstable_now = void 0;
    if ("object" === typeof performance && "function" === typeof performance.now) {
      var localPerformance = performance;
      exports.unstable_now = function() {
        return localPerformance.now();
      };
    } else {
      var localDate = Date, initialTime = localDate.now();
      exports.unstable_now = function() {
        return localDate.now() - initialTime;
      };
    }
    var taskQueue = [], timerQueue = [], taskIdCounter = 1, currentTask = null, currentPriorityLevel = 3, isPerformingWork = false, isHostCallbackScheduled = false, isHostTimeoutScheduled = false, needsPaint = false, localSetTimeout = "function" === typeof setTimeout ? setTimeout : null, localClearTimeout = "function" === typeof clearTimeout ? clearTimeout : null, localSetImmediate = "undefined" !== typeof setImmediate ? setImmediate : null;
    function advanceTimers(currentTime) {
      for (var timer = peek(timerQueue); null !== timer; ) {
        if (null === timer.callback) pop(timerQueue);
        else if (timer.startTime <= currentTime)
          pop(timerQueue), timer.sortIndex = timer.expirationTime, push(taskQueue, timer);
        else break;
        timer = peek(timerQueue);
      }
    }
    function handleTimeout(currentTime) {
      isHostTimeoutScheduled = false;
      advanceTimers(currentTime);
      if (!isHostCallbackScheduled)
        if (null !== peek(taskQueue))
          isHostCallbackScheduled = true, isMessageLoopRunning || (isMessageLoopRunning = true, schedulePerformWorkUntilDeadline());
        else {
          var firstTimer = peek(timerQueue);
          null !== firstTimer && requestHostTimeout(handleTimeout, firstTimer.startTime - currentTime);
        }
    }
    var isMessageLoopRunning = false, taskTimeoutID = -1, frameInterval = 5, startTime = -1;
    function shouldYieldToHost() {
      return needsPaint ? true : exports.unstable_now() - startTime < frameInterval ? false : true;
    }
    function performWorkUntilDeadline() {
      needsPaint = false;
      if (isMessageLoopRunning) {
        var currentTime = exports.unstable_now();
        startTime = currentTime;
        var hasMoreWork = true;
        try {
          a: {
            isHostCallbackScheduled = false;
            isHostTimeoutScheduled && (isHostTimeoutScheduled = false, localClearTimeout(taskTimeoutID), taskTimeoutID = -1);
            isPerformingWork = true;
            var previousPriorityLevel = currentPriorityLevel;
            try {
              b: {
                advanceTimers(currentTime);
                for (currentTask = peek(taskQueue); null !== currentTask && !(currentTask.expirationTime > currentTime && shouldYieldToHost()); ) {
                  var callback = currentTask.callback;
                  if ("function" === typeof callback) {
                    currentTask.callback = null;
                    currentPriorityLevel = currentTask.priorityLevel;
                    var continuationCallback = callback(
                      currentTask.expirationTime <= currentTime
                    );
                    currentTime = exports.unstable_now();
                    if ("function" === typeof continuationCallback) {
                      currentTask.callback = continuationCallback;
                      advanceTimers(currentTime);
                      hasMoreWork = true;
                      break b;
                    }
                    currentTask === peek(taskQueue) && pop(taskQueue);
                    advanceTimers(currentTime);
                  } else pop(taskQueue);
                  currentTask = peek(taskQueue);
                }
                if (null !== currentTask) hasMoreWork = true;
                else {
                  var firstTimer = peek(timerQueue);
                  null !== firstTimer && requestHostTimeout(
                    handleTimeout,
                    firstTimer.startTime - currentTime
                  );
                  hasMoreWork = false;
                }
              }
              break a;
            } finally {
              currentTask = null, currentPriorityLevel = previousPriorityLevel, isPerformingWork = false;
            }
            hasMoreWork = void 0;
          }
        } finally {
          hasMoreWork ? schedulePerformWorkUntilDeadline() : isMessageLoopRunning = false;
        }
      }
    }
    var schedulePerformWorkUntilDeadline;
    if ("function" === typeof localSetImmediate)
      schedulePerformWorkUntilDeadline = function() {
        localSetImmediate(performWorkUntilDeadline);
      };
    else if ("undefined" !== typeof MessageChannel) {
      var channel = new MessageChannel(), port = channel.port2;
      channel.port1.onmessage = performWorkUntilDeadline;
      schedulePerformWorkUntilDeadline = function() {
        port.postMessage(null);
      };
    } else
      schedulePerformWorkUntilDeadline = function() {
        localSetTimeout(performWorkUntilDeadline, 0);
      };
    function requestHostTimeout(callback, ms) {
      taskTimeoutID = localSetTimeout(function() {
        callback(exports.unstable_now());
      }, ms);
    }
    exports.unstable_IdlePriority = 5;
    exports.unstable_ImmediatePriority = 1;
    exports.unstable_LowPriority = 4;
    exports.unstable_NormalPriority = 3;
    exports.unstable_Profiling = null;
    exports.unstable_UserBlockingPriority = 2;
    exports.unstable_cancelCallback = function(task) {
      task.callback = null;
    };
    exports.unstable_forceFrameRate = function(fps) {
      0 > fps || 125 < fps ? console.error(
        "forceFrameRate takes a positive int between 0 and 125, forcing frame rates higher than 125 fps is not supported"
      ) : frameInterval = 0 < fps ? Math.floor(1e3 / fps) : 5;
    };
    exports.unstable_getCurrentPriorityLevel = function() {
      return currentPriorityLevel;
    };
    exports.unstable_next = function(eventHandler) {
      switch (currentPriorityLevel) {
        case 1:
        case 2:
        case 3:
          var priorityLevel = 3;
          break;
        default:
          priorityLevel = currentPriorityLevel;
      }
      var previousPriorityLevel = currentPriorityLevel;
      currentPriorityLevel = priorityLevel;
      try {
        return eventHandler();
      } finally {
        currentPriorityLevel = previousPriorityLevel;
      }
    };
    exports.unstable_requestPaint = function() {
      needsPaint = true;
    };
    exports.unstable_runWithPriority = function(priorityLevel, eventHandler) {
      switch (priorityLevel) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
          break;
        default:
          priorityLevel = 3;
      }
      var previousPriorityLevel = currentPriorityLevel;
      currentPriorityLevel = priorityLevel;
      try {
        return eventHandler();
      } finally {
        currentPriorityLevel = previousPriorityLevel;
      }
    };
    exports.unstable_scheduleCallback = function(priorityLevel, callback, options) {
      var currentTime = exports.unstable_now();
      "object" === typeof options && null !== options ? (options = options.delay, options = "number" === typeof options && 0 < options ? currentTime + options : currentTime) : options = currentTime;
      switch (priorityLevel) {
        case 1:
          var timeout = -1;
          break;
        case 2:
          timeout = 250;
          break;
        case 5:
          timeout = 1073741823;
          break;
        case 4:
          timeout = 1e4;
          break;
        default:
          timeout = 5e3;
      }
      timeout = options + timeout;
      priorityLevel = {
        id: taskIdCounter++,
        callback,
        priorityLevel,
        startTime: options,
        expirationTime: timeout,
        sortIndex: -1
      };
      options > currentTime ? (priorityLevel.sortIndex = options, push(timerQueue, priorityLevel), null === peek(taskQueue) && priorityLevel === peek(timerQueue) && (isHostTimeoutScheduled ? (localClearTimeout(taskTimeoutID), taskTimeoutID = -1) : isHostTimeoutScheduled = true, requestHostTimeout(handleTimeout, options - currentTime))) : (priorityLevel.sortIndex = timeout, push(taskQueue, priorityLevel), isHostCallbackScheduled || isPerformingWork || (isHostCallbackScheduled = true, isMessageLoopRunning || (isMessageLoopRunning = true, schedulePerformWorkUntilDeadline())));
      return priorityLevel;
    };
    exports.unstable_shouldYield = shouldYieldToHost;
    exports.unstable_wrapCallback = function(callback) {
      var parentPriorityLevel = currentPriorityLevel;
      return function() {
        var previousPriorityLevel = currentPriorityLevel;
        currentPriorityLevel = parentPriorityLevel;
        try {
          return callback.apply(this, arguments);
        } finally {
          currentPriorityLevel = previousPriorityLevel;
        }
      };
    };
  })(scheduler_production);
  return scheduler_production;
}
var hasRequiredScheduler;
function requireScheduler() {
  if (hasRequiredScheduler) return scheduler.exports;
  hasRequiredScheduler = 1;
  {
    scheduler.exports = requireScheduler_production();
  }
  return scheduler.exports;
}
var reactDom = { exports: {} };
var reactDom_production = {};
var hasRequiredReactDom_production;
function requireReactDom_production() {
  if (hasRequiredReactDom_production) return reactDom_production;
  hasRequiredReactDom_production = 1;
  var React2 = requireReact();
  function formatProdErrorMessage(code) {
    var url = "https://react.dev/errors/" + code;
    if (1 < arguments.length) {
      url += "?args[]=" + encodeURIComponent(arguments[1]);
      for (var i = 2; i < arguments.length; i++)
        url += "&args[]=" + encodeURIComponent(arguments[i]);
    }
    return "Minified React error #" + code + "; visit " + url + " for the full message or use the non-minified dev environment for full errors and additional helpful warnings.";
  }
  function noop() {
  }
  var Internals = {
    d: {
      f: noop,
      r: function() {
        throw Error(formatProdErrorMessage(522));
      },
      D: noop,
      C: noop,
      L: noop,
      m: noop,
      X: noop,
      S: noop,
      M: noop
    },
    p: 0,
    findDOMNode: null
  }, REACT_PORTAL_TYPE = /* @__PURE__ */ Symbol.for("react.portal");
  function createPortal$1(children, containerInfo, implementation) {
    var key = 3 < arguments.length && void 0 !== arguments[3] ? arguments[3] : null;
    return {
      $$typeof: REACT_PORTAL_TYPE,
      key: null == key ? null : "" + key,
      children,
      containerInfo,
      implementation
    };
  }
  var ReactSharedInternals = React2.__CLIENT_INTERNALS_DO_NOT_USE_OR_WARN_USERS_THEY_CANNOT_UPGRADE;
  function getCrossOriginStringAs(as, input) {
    if ("font" === as) return "";
    if ("string" === typeof input)
      return "use-credentials" === input ? input : "";
  }
  reactDom_production.__DOM_INTERNALS_DO_NOT_USE_OR_WARN_USERS_THEY_CANNOT_UPGRADE = Internals;
  reactDom_production.createPortal = function(children, container) {
    var key = 2 < arguments.length && void 0 !== arguments[2] ? arguments[2] : null;
    if (!container || 1 !== container.nodeType && 9 !== container.nodeType && 11 !== container.nodeType)
      throw Error(formatProdErrorMessage(299));
    return createPortal$1(children, container, null, key);
  };
  reactDom_production.flushSync = function(fn) {
    var previousTransition = ReactSharedInternals.T, previousUpdatePriority = Internals.p;
    try {
      if (ReactSharedInternals.T = null, Internals.p = 2, fn) return fn();
    } finally {
      ReactSharedInternals.T = previousTransition, Internals.p = previousUpdatePriority, Internals.d.f();
    }
  };
  reactDom_production.preconnect = function(href, options) {
    "string" === typeof href && (options ? (options = options.crossOrigin, options = "string" === typeof options ? "use-credentials" === options ? options : "" : void 0) : options = null, Internals.d.C(href, options));
  };
  reactDom_production.prefetchDNS = function(href) {
    "string" === typeof href && Internals.d.D(href);
  };
  reactDom_production.preinit = function(href, options) {
    if ("string" === typeof href && options && "string" === typeof options.as) {
      var as = options.as, crossOrigin = getCrossOriginStringAs(as, options.crossOrigin), integrity = "string" === typeof options.integrity ? options.integrity : void 0, fetchPriority = "string" === typeof options.fetchPriority ? options.fetchPriority : void 0;
      "style" === as ? Internals.d.S(
        href,
        "string" === typeof options.precedence ? options.precedence : void 0,
        {
          crossOrigin,
          integrity,
          fetchPriority
        }
      ) : "script" === as && Internals.d.X(href, {
        crossOrigin,
        integrity,
        fetchPriority,
        nonce: "string" === typeof options.nonce ? options.nonce : void 0
      });
    }
  };
  reactDom_production.preinitModule = function(href, options) {
    if ("string" === typeof href)
      if ("object" === typeof options && null !== options) {
        if (null == options.as || "script" === options.as) {
          var crossOrigin = getCrossOriginStringAs(
            options.as,
            options.crossOrigin
          );
          Internals.d.M(href, {
            crossOrigin,
            integrity: "string" === typeof options.integrity ? options.integrity : void 0,
            nonce: "string" === typeof options.nonce ? options.nonce : void 0
          });
        }
      } else null == options && Internals.d.M(href);
  };
  reactDom_production.preload = function(href, options) {
    if ("string" === typeof href && "object" === typeof options && null !== options && "string" === typeof options.as) {
      var as = options.as, crossOrigin = getCrossOriginStringAs(as, options.crossOrigin);
      Internals.d.L(href, as, {
        crossOrigin,
        integrity: "string" === typeof options.integrity ? options.integrity : void 0,
        nonce: "string" === typeof options.nonce ? options.nonce : void 0,
        type: "string" === typeof options.type ? options.type : void 0,
        fetchPriority: "string" === typeof options.fetchPriority ? options.fetchPriority : void 0,
        referrerPolicy: "string" === typeof options.referrerPolicy ? options.referrerPolicy : void 0,
        imageSrcSet: "string" === typeof options.imageSrcSet ? options.imageSrcSet : void 0,
        imageSizes: "string" === typeof options.imageSizes ? options.imageSizes : void 0,
        media: "string" === typeof options.media ? options.media : void 0
      });
    }
  };
  reactDom_production.preloadModule = function(href, options) {
    if ("string" === typeof href)
      if (options) {
        var crossOrigin = getCrossOriginStringAs(options.as, options.crossOrigin);
        Internals.d.m(href, {
          as: "string" === typeof options.as && "script" !== options.as ? options.as : void 0,
          crossOrigin,
          integrity: "string" === typeof options.integrity ? options.integrity : void 0
        });
      } else Internals.d.m(href);
  };
  reactDom_production.requestFormReset = function(form) {
    Internals.d.r(form);
  };
  reactDom_production.unstable_batchedUpdates = function(fn, a) {
    return fn(a);
  };
  reactDom_production.useFormState = function(action, initialState, permalink) {
    return ReactSharedInternals.H.useFormState(action, initialState, permalink);
  };
  reactDom_production.useFormStatus = function() {
    return ReactSharedInternals.H.useHostTransitionStatus();
  };
  reactDom_production.version = "19.2.7";
  return reactDom_production;
}
var hasRequiredReactDom;
function requireReactDom() {
  if (hasRequiredReactDom) return reactDom.exports;
  hasRequiredReactDom = 1;
  function checkDCE() {
    if (typeof __REACT_DEVTOOLS_GLOBAL_HOOK__ === "undefined" || typeof __REACT_DEVTOOLS_GLOBAL_HOOK__.checkDCE !== "function") {
      return;
    }
    try {
      __REACT_DEVTOOLS_GLOBAL_HOOK__.checkDCE(checkDCE);
    } catch (err) {
      console.error(err);
    }
  }
  {
    checkDCE();
    reactDom.exports = requireReactDom_production();
  }
  return reactDom.exports;
}
var hasRequiredReactDomClient_production;
function requireReactDomClient_production() {
  if (hasRequiredReactDomClient_production) return reactDomClient_production;
  hasRequiredReactDomClient_production = 1;
  var Scheduler = requireScheduler(), React2 = requireReact(), ReactDOM = requireReactDom();
  function formatProdErrorMessage(code) {
    var url = "https://react.dev/errors/" + code;
    if (1 < arguments.length) {
      url += "?args[]=" + encodeURIComponent(arguments[1]);
      for (var i = 2; i < arguments.length; i++)
        url += "&args[]=" + encodeURIComponent(arguments[i]);
    }
    return "Minified React error #" + code + "; visit " + url + " for the full message or use the non-minified dev environment for full errors and additional helpful warnings.";
  }
  function isValidContainer(node) {
    return !(!node || 1 !== node.nodeType && 9 !== node.nodeType && 11 !== node.nodeType);
  }
  function getNearestMountedFiber(fiber) {
    var node = fiber, nearestMounted = fiber;
    if (fiber.alternate) for (; node.return; ) node = node.return;
    else {
      fiber = node;
      do
        node = fiber, 0 !== (node.flags & 4098) && (nearestMounted = node.return), fiber = node.return;
      while (fiber);
    }
    return 3 === node.tag ? nearestMounted : null;
  }
  function getSuspenseInstanceFromFiber(fiber) {
    if (13 === fiber.tag) {
      var suspenseState = fiber.memoizedState;
      null === suspenseState && (fiber = fiber.alternate, null !== fiber && (suspenseState = fiber.memoizedState));
      if (null !== suspenseState) return suspenseState.dehydrated;
    }
    return null;
  }
  function getActivityInstanceFromFiber(fiber) {
    if (31 === fiber.tag) {
      var activityState = fiber.memoizedState;
      null === activityState && (fiber = fiber.alternate, null !== fiber && (activityState = fiber.memoizedState));
      if (null !== activityState) return activityState.dehydrated;
    }
    return null;
  }
  function assertIsMounted(fiber) {
    if (getNearestMountedFiber(fiber) !== fiber)
      throw Error(formatProdErrorMessage(188));
  }
  function findCurrentFiberUsingSlowPath(fiber) {
    var alternate = fiber.alternate;
    if (!alternate) {
      alternate = getNearestMountedFiber(fiber);
      if (null === alternate) throw Error(formatProdErrorMessage(188));
      return alternate !== fiber ? null : fiber;
    }
    for (var a = fiber, b = alternate; ; ) {
      var parentA = a.return;
      if (null === parentA) break;
      var parentB = parentA.alternate;
      if (null === parentB) {
        b = parentA.return;
        if (null !== b) {
          a = b;
          continue;
        }
        break;
      }
      if (parentA.child === parentB.child) {
        for (parentB = parentA.child; parentB; ) {
          if (parentB === a) return assertIsMounted(parentA), fiber;
          if (parentB === b) return assertIsMounted(parentA), alternate;
          parentB = parentB.sibling;
        }
        throw Error(formatProdErrorMessage(188));
      }
      if (a.return !== b.return) a = parentA, b = parentB;
      else {
        for (var didFindChild = false, child$0 = parentA.child; child$0; ) {
          if (child$0 === a) {
            didFindChild = true;
            a = parentA;
            b = parentB;
            break;
          }
          if (child$0 === b) {
            didFindChild = true;
            b = parentA;
            a = parentB;
            break;
          }
          child$0 = child$0.sibling;
        }
        if (!didFindChild) {
          for (child$0 = parentB.child; child$0; ) {
            if (child$0 === a) {
              didFindChild = true;
              a = parentB;
              b = parentA;
              break;
            }
            if (child$0 === b) {
              didFindChild = true;
              b = parentB;
              a = parentA;
              break;
            }
            child$0 = child$0.sibling;
          }
          if (!didFindChild) throw Error(formatProdErrorMessage(189));
        }
      }
      if (a.alternate !== b) throw Error(formatProdErrorMessage(190));
    }
    if (3 !== a.tag) throw Error(formatProdErrorMessage(188));
    return a.stateNode.current === a ? fiber : alternate;
  }
  function findCurrentHostFiberImpl(node) {
    var tag = node.tag;
    if (5 === tag || 26 === tag || 27 === tag || 6 === tag) return node;
    for (node = node.child; null !== node; ) {
      tag = findCurrentHostFiberImpl(node);
      if (null !== tag) return tag;
      node = node.sibling;
    }
    return null;
  }
  var assign = Object.assign, REACT_LEGACY_ELEMENT_TYPE = /* @__PURE__ */ Symbol.for("react.element"), REACT_ELEMENT_TYPE = /* @__PURE__ */ Symbol.for("react.transitional.element"), REACT_PORTAL_TYPE = /* @__PURE__ */ Symbol.for("react.portal"), REACT_FRAGMENT_TYPE = /* @__PURE__ */ Symbol.for("react.fragment"), REACT_STRICT_MODE_TYPE = /* @__PURE__ */ Symbol.for("react.strict_mode"), REACT_PROFILER_TYPE = /* @__PURE__ */ Symbol.for("react.profiler"), REACT_CONSUMER_TYPE = /* @__PURE__ */ Symbol.for("react.consumer"), REACT_CONTEXT_TYPE = /* @__PURE__ */ Symbol.for("react.context"), REACT_FORWARD_REF_TYPE = /* @__PURE__ */ Symbol.for("react.forward_ref"), REACT_SUSPENSE_TYPE = /* @__PURE__ */ Symbol.for("react.suspense"), REACT_SUSPENSE_LIST_TYPE = /* @__PURE__ */ Symbol.for("react.suspense_list"), REACT_MEMO_TYPE = /* @__PURE__ */ Symbol.for("react.memo"), REACT_LAZY_TYPE = /* @__PURE__ */ Symbol.for("react.lazy");
  var REACT_ACTIVITY_TYPE = /* @__PURE__ */ Symbol.for("react.activity");
  var REACT_MEMO_CACHE_SENTINEL = /* @__PURE__ */ Symbol.for("react.memo_cache_sentinel");
  var MAYBE_ITERATOR_SYMBOL = Symbol.iterator;
  function getIteratorFn(maybeIterable) {
    if (null === maybeIterable || "object" !== typeof maybeIterable) return null;
    maybeIterable = MAYBE_ITERATOR_SYMBOL && maybeIterable[MAYBE_ITERATOR_SYMBOL] || maybeIterable["@@iterator"];
    return "function" === typeof maybeIterable ? maybeIterable : null;
  }
  var REACT_CLIENT_REFERENCE = /* @__PURE__ */ Symbol.for("react.client.reference");
  function getComponentNameFromType(type) {
    if (null == type) return null;
    if ("function" === typeof type)
      return type.$$typeof === REACT_CLIENT_REFERENCE ? null : type.displayName || type.name || null;
    if ("string" === typeof type) return type;
    switch (type) {
      case REACT_FRAGMENT_TYPE:
        return "Fragment";
      case REACT_PROFILER_TYPE:
        return "Profiler";
      case REACT_STRICT_MODE_TYPE:
        return "StrictMode";
      case REACT_SUSPENSE_TYPE:
        return "Suspense";
      case REACT_SUSPENSE_LIST_TYPE:
        return "SuspenseList";
      case REACT_ACTIVITY_TYPE:
        return "Activity";
    }
    if ("object" === typeof type)
      switch (type.$$typeof) {
        case REACT_PORTAL_TYPE:
          return "Portal";
        case REACT_CONTEXT_TYPE:
          return type.displayName || "Context";
        case REACT_CONSUMER_TYPE:
          return (type._context.displayName || "Context") + ".Consumer";
        case REACT_FORWARD_REF_TYPE:
          var innerType = type.render;
          type = type.displayName;
          type || (type = innerType.displayName || innerType.name || "", type = "" !== type ? "ForwardRef(" + type + ")" : "ForwardRef");
          return type;
        case REACT_MEMO_TYPE:
          return innerType = type.displayName || null, null !== innerType ? innerType : getComponentNameFromType(type.type) || "Memo";
        case REACT_LAZY_TYPE:
          innerType = type._payload;
          type = type._init;
          try {
            return getComponentNameFromType(type(innerType));
          } catch (x) {
          }
      }
    return null;
  }
  var isArrayImpl = Array.isArray, ReactSharedInternals = React2.__CLIENT_INTERNALS_DO_NOT_USE_OR_WARN_USERS_THEY_CANNOT_UPGRADE, ReactDOMSharedInternals = ReactDOM.__DOM_INTERNALS_DO_NOT_USE_OR_WARN_USERS_THEY_CANNOT_UPGRADE, sharedNotPendingObject = {
    pending: false,
    data: null,
    method: null,
    action: null
  }, valueStack = [], index = -1;
  function createCursor(defaultValue) {
    return { current: defaultValue };
  }
  function pop(cursor) {
    0 > index || (cursor.current = valueStack[index], valueStack[index] = null, index--);
  }
  function push(cursor, value) {
    index++;
    valueStack[index] = cursor.current;
    cursor.current = value;
  }
  var contextStackCursor = createCursor(null), contextFiberStackCursor = createCursor(null), rootInstanceStackCursor = createCursor(null), hostTransitionProviderCursor = createCursor(null);
  function pushHostContainer(fiber, nextRootInstance) {
    push(rootInstanceStackCursor, nextRootInstance);
    push(contextFiberStackCursor, fiber);
    push(contextStackCursor, null);
    switch (nextRootInstance.nodeType) {
      case 9:
      case 11:
        fiber = (fiber = nextRootInstance.documentElement) ? (fiber = fiber.namespaceURI) ? getOwnHostContext(fiber) : 0 : 0;
        break;
      default:
        if (fiber = nextRootInstance.tagName, nextRootInstance = nextRootInstance.namespaceURI)
          nextRootInstance = getOwnHostContext(nextRootInstance), fiber = getChildHostContextProd(nextRootInstance, fiber);
        else
          switch (fiber) {
            case "svg":
              fiber = 1;
              break;
            case "math":
              fiber = 2;
              break;
            default:
              fiber = 0;
          }
    }
    pop(contextStackCursor);
    push(contextStackCursor, fiber);
  }
  function popHostContainer() {
    pop(contextStackCursor);
    pop(contextFiberStackCursor);
    pop(rootInstanceStackCursor);
  }
  function pushHostContext(fiber) {
    null !== fiber.memoizedState && push(hostTransitionProviderCursor, fiber);
    var context = contextStackCursor.current;
    var JSCompiler_inline_result = getChildHostContextProd(context, fiber.type);
    context !== JSCompiler_inline_result && (push(contextFiberStackCursor, fiber), push(contextStackCursor, JSCompiler_inline_result));
  }
  function popHostContext(fiber) {
    contextFiberStackCursor.current === fiber && (pop(contextStackCursor), pop(contextFiberStackCursor));
    hostTransitionProviderCursor.current === fiber && (pop(hostTransitionProviderCursor), HostTransitionContext._currentValue = sharedNotPendingObject);
  }
  var prefix, suffix;
  function describeBuiltInComponentFrame(name) {
    if (void 0 === prefix)
      try {
        throw Error();
      } catch (x) {
        var match = x.stack.trim().match(/\n( *(at )?)/);
        prefix = match && match[1] || "";
        suffix = -1 < x.stack.indexOf("\n    at") ? " (<anonymous>)" : -1 < x.stack.indexOf("@") ? "@unknown:0:0" : "";
      }
    return "\n" + prefix + name + suffix;
  }
  var reentry = false;
  function describeNativeComponentFrame(fn, construct) {
    if (!fn || reentry) return "";
    reentry = true;
    var previousPrepareStackTrace = Error.prepareStackTrace;
    Error.prepareStackTrace = void 0;
    try {
      var RunInRootFrame = {
        DetermineComponentFrameRoot: function() {
          try {
            if (construct) {
              var Fake = function() {
                throw Error();
              };
              Object.defineProperty(Fake.prototype, "props", {
                set: function() {
                  throw Error();
                }
              });
              if ("object" === typeof Reflect && Reflect.construct) {
                try {
                  Reflect.construct(Fake, []);
                } catch (x) {
                  var control = x;
                }
                Reflect.construct(fn, [], Fake);
              } else {
                try {
                  Fake.call();
                } catch (x$1) {
                  control = x$1;
                }
                fn.call(Fake.prototype);
              }
            } else {
              try {
                throw Error();
              } catch (x$2) {
                control = x$2;
              }
              (Fake = fn()) && "function" === typeof Fake.catch && Fake.catch(function() {
              });
            }
          } catch (sample) {
            if (sample && control && "string" === typeof sample.stack)
              return [sample.stack, control.stack];
          }
          return [null, null];
        }
      };
      RunInRootFrame.DetermineComponentFrameRoot.displayName = "DetermineComponentFrameRoot";
      var namePropDescriptor = Object.getOwnPropertyDescriptor(
        RunInRootFrame.DetermineComponentFrameRoot,
        "name"
      );
      namePropDescriptor && namePropDescriptor.configurable && Object.defineProperty(
        RunInRootFrame.DetermineComponentFrameRoot,
        "name",
        { value: "DetermineComponentFrameRoot" }
      );
      var _RunInRootFrame$Deter = RunInRootFrame.DetermineComponentFrameRoot(), sampleStack = _RunInRootFrame$Deter[0], controlStack = _RunInRootFrame$Deter[1];
      if (sampleStack && controlStack) {
        var sampleLines = sampleStack.split("\n"), controlLines = controlStack.split("\n");
        for (namePropDescriptor = RunInRootFrame = 0; RunInRootFrame < sampleLines.length && !sampleLines[RunInRootFrame].includes("DetermineComponentFrameRoot"); )
          RunInRootFrame++;
        for (; namePropDescriptor < controlLines.length && !controlLines[namePropDescriptor].includes(
          "DetermineComponentFrameRoot"
        ); )
          namePropDescriptor++;
        if (RunInRootFrame === sampleLines.length || namePropDescriptor === controlLines.length)
          for (RunInRootFrame = sampleLines.length - 1, namePropDescriptor = controlLines.length - 1; 1 <= RunInRootFrame && 0 <= namePropDescriptor && sampleLines[RunInRootFrame] !== controlLines[namePropDescriptor]; )
            namePropDescriptor--;
        for (; 1 <= RunInRootFrame && 0 <= namePropDescriptor; RunInRootFrame--, namePropDescriptor--)
          if (sampleLines[RunInRootFrame] !== controlLines[namePropDescriptor]) {
            if (1 !== RunInRootFrame || 1 !== namePropDescriptor) {
              do
                if (RunInRootFrame--, namePropDescriptor--, 0 > namePropDescriptor || sampleLines[RunInRootFrame] !== controlLines[namePropDescriptor]) {
                  var frame = "\n" + sampleLines[RunInRootFrame].replace(" at new ", " at ");
                  fn.displayName && frame.includes("<anonymous>") && (frame = frame.replace("<anonymous>", fn.displayName));
                  return frame;
                }
              while (1 <= RunInRootFrame && 0 <= namePropDescriptor);
            }
            break;
          }
      }
    } finally {
      reentry = false, Error.prepareStackTrace = previousPrepareStackTrace;
    }
    return (previousPrepareStackTrace = fn ? fn.displayName || fn.name : "") ? describeBuiltInComponentFrame(previousPrepareStackTrace) : "";
  }
  function describeFiber(fiber, childFiber) {
    switch (fiber.tag) {
      case 26:
      case 27:
      case 5:
        return describeBuiltInComponentFrame(fiber.type);
      case 16:
        return describeBuiltInComponentFrame("Lazy");
      case 13:
        return fiber.child !== childFiber && null !== childFiber ? describeBuiltInComponentFrame("Suspense Fallback") : describeBuiltInComponentFrame("Suspense");
      case 19:
        return describeBuiltInComponentFrame("SuspenseList");
      case 0:
      case 15:
        return describeNativeComponentFrame(fiber.type, false);
      case 11:
        return describeNativeComponentFrame(fiber.type.render, false);
      case 1:
        return describeNativeComponentFrame(fiber.type, true);
      case 31:
        return describeBuiltInComponentFrame("Activity");
      default:
        return "";
    }
  }
  function getStackByFiberInDevAndProd(workInProgress2) {
    try {
      var info = "", previous = null;
      do
        info += describeFiber(workInProgress2, previous), previous = workInProgress2, workInProgress2 = workInProgress2.return;
      while (workInProgress2);
      return info;
    } catch (x) {
      return "\nError generating stack: " + x.message + "\n" + x.stack;
    }
  }
  var hasOwnProperty = Object.prototype.hasOwnProperty, scheduleCallback$3 = Scheduler.unstable_scheduleCallback, cancelCallback$1 = Scheduler.unstable_cancelCallback, shouldYield = Scheduler.unstable_shouldYield, requestPaint = Scheduler.unstable_requestPaint, now = Scheduler.unstable_now, getCurrentPriorityLevel = Scheduler.unstable_getCurrentPriorityLevel, ImmediatePriority = Scheduler.unstable_ImmediatePriority, UserBlockingPriority = Scheduler.unstable_UserBlockingPriority, NormalPriority$1 = Scheduler.unstable_NormalPriority, LowPriority = Scheduler.unstable_LowPriority, IdlePriority = Scheduler.unstable_IdlePriority, log$1 = Scheduler.log, unstable_setDisableYieldValue = Scheduler.unstable_setDisableYieldValue, rendererID = null, injectedHook = null;
  function setIsStrictModeForDevtools(newIsStrictMode) {
    "function" === typeof log$1 && unstable_setDisableYieldValue(newIsStrictMode);
    if (injectedHook && "function" === typeof injectedHook.setStrictMode)
      try {
        injectedHook.setStrictMode(rendererID, newIsStrictMode);
      } catch (err) {
      }
  }
  var clz32 = Math.clz32 ? Math.clz32 : clz32Fallback, log = Math.log, LN2 = Math.LN2;
  function clz32Fallback(x) {
    x >>>= 0;
    return 0 === x ? 32 : 31 - (log(x) / LN2 | 0) | 0;
  }
  var nextTransitionUpdateLane = 256, nextTransitionDeferredLane = 262144, nextRetryLane = 4194304;
  function getHighestPriorityLanes(lanes) {
    var pendingSyncLanes = lanes & 42;
    if (0 !== pendingSyncLanes) return pendingSyncLanes;
    switch (lanes & -lanes) {
      case 1:
        return 1;
      case 2:
        return 2;
      case 4:
        return 4;
      case 8:
        return 8;
      case 16:
        return 16;
      case 32:
        return 32;
      case 64:
        return 64;
      case 128:
        return 128;
      case 256:
      case 512:
      case 1024:
      case 2048:
      case 4096:
      case 8192:
      case 16384:
      case 32768:
      case 65536:
      case 131072:
        return lanes & 261888;
      case 262144:
      case 524288:
      case 1048576:
      case 2097152:
        return lanes & 3932160;
      case 4194304:
      case 8388608:
      case 16777216:
      case 33554432:
        return lanes & 62914560;
      case 67108864:
        return 67108864;
      case 134217728:
        return 134217728;
      case 268435456:
        return 268435456;
      case 536870912:
        return 536870912;
      case 1073741824:
        return 0;
      default:
        return lanes;
    }
  }
  function getNextLanes(root2, wipLanes, rootHasPendingCommit) {
    var pendingLanes = root2.pendingLanes;
    if (0 === pendingLanes) return 0;
    var nextLanes = 0, suspendedLanes = root2.suspendedLanes, pingedLanes = root2.pingedLanes;
    root2 = root2.warmLanes;
    var nonIdlePendingLanes = pendingLanes & 134217727;
    0 !== nonIdlePendingLanes ? (pendingLanes = nonIdlePendingLanes & ~suspendedLanes, 0 !== pendingLanes ? nextLanes = getHighestPriorityLanes(pendingLanes) : (pingedLanes &= nonIdlePendingLanes, 0 !== pingedLanes ? nextLanes = getHighestPriorityLanes(pingedLanes) : rootHasPendingCommit || (rootHasPendingCommit = nonIdlePendingLanes & ~root2, 0 !== rootHasPendingCommit && (nextLanes = getHighestPriorityLanes(rootHasPendingCommit))))) : (nonIdlePendingLanes = pendingLanes & ~suspendedLanes, 0 !== nonIdlePendingLanes ? nextLanes = getHighestPriorityLanes(nonIdlePendingLanes) : 0 !== pingedLanes ? nextLanes = getHighestPriorityLanes(pingedLanes) : rootHasPendingCommit || (rootHasPendingCommit = pendingLanes & ~root2, 0 !== rootHasPendingCommit && (nextLanes = getHighestPriorityLanes(rootHasPendingCommit))));
    return 0 === nextLanes ? 0 : 0 !== wipLanes && wipLanes !== nextLanes && 0 === (wipLanes & suspendedLanes) && (suspendedLanes = nextLanes & -nextLanes, rootHasPendingCommit = wipLanes & -wipLanes, suspendedLanes >= rootHasPendingCommit || 32 === suspendedLanes && 0 !== (rootHasPendingCommit & 4194048)) ? wipLanes : nextLanes;
  }
  function checkIfRootIsPrerendering(root2, renderLanes2) {
    return 0 === (root2.pendingLanes & ~(root2.suspendedLanes & ~root2.pingedLanes) & renderLanes2);
  }
  function computeExpirationTime(lane, currentTime) {
    switch (lane) {
      case 1:
      case 2:
      case 4:
      case 8:
      case 64:
        return currentTime + 250;
      case 16:
      case 32:
      case 128:
      case 256:
      case 512:
      case 1024:
      case 2048:
      case 4096:
      case 8192:
      case 16384:
      case 32768:
      case 65536:
      case 131072:
      case 262144:
      case 524288:
      case 1048576:
      case 2097152:
        return currentTime + 5e3;
      case 4194304:
      case 8388608:
      case 16777216:
      case 33554432:
        return -1;
      case 67108864:
      case 134217728:
      case 268435456:
      case 536870912:
      case 1073741824:
        return -1;
      default:
        return -1;
    }
  }
  function claimNextRetryLane() {
    var lane = nextRetryLane;
    nextRetryLane <<= 1;
    0 === (nextRetryLane & 62914560) && (nextRetryLane = 4194304);
    return lane;
  }
  function createLaneMap(initial) {
    for (var laneMap = [], i = 0; 31 > i; i++) laneMap.push(initial);
    return laneMap;
  }
  function markRootUpdated$1(root2, updateLane) {
    root2.pendingLanes |= updateLane;
    268435456 !== updateLane && (root2.suspendedLanes = 0, root2.pingedLanes = 0, root2.warmLanes = 0);
  }
  function markRootFinished(root2, finishedLanes, remainingLanes, spawnedLane, updatedLanes, suspendedRetryLanes) {
    var previouslyPendingLanes = root2.pendingLanes;
    root2.pendingLanes = remainingLanes;
    root2.suspendedLanes = 0;
    root2.pingedLanes = 0;
    root2.warmLanes = 0;
    root2.expiredLanes &= remainingLanes;
    root2.entangledLanes &= remainingLanes;
    root2.errorRecoveryDisabledLanes &= remainingLanes;
    root2.shellSuspendCounter = 0;
    var entanglements = root2.entanglements, expirationTimes = root2.expirationTimes, hiddenUpdates = root2.hiddenUpdates;
    for (remainingLanes = previouslyPendingLanes & ~remainingLanes; 0 < remainingLanes; ) {
      var index$7 = 31 - clz32(remainingLanes), lane = 1 << index$7;
      entanglements[index$7] = 0;
      expirationTimes[index$7] = -1;
      var hiddenUpdatesForLane = hiddenUpdates[index$7];
      if (null !== hiddenUpdatesForLane)
        for (hiddenUpdates[index$7] = null, index$7 = 0; index$7 < hiddenUpdatesForLane.length; index$7++) {
          var update = hiddenUpdatesForLane[index$7];
          null !== update && (update.lane &= -536870913);
        }
      remainingLanes &= ~lane;
    }
    0 !== spawnedLane && markSpawnedDeferredLane(root2, spawnedLane, 0);
    0 !== suspendedRetryLanes && 0 === updatedLanes && 0 !== root2.tag && (root2.suspendedLanes |= suspendedRetryLanes & ~(previouslyPendingLanes & ~finishedLanes));
  }
  function markSpawnedDeferredLane(root2, spawnedLane, entangledLanes) {
    root2.pendingLanes |= spawnedLane;
    root2.suspendedLanes &= ~spawnedLane;
    var spawnedLaneIndex = 31 - clz32(spawnedLane);
    root2.entangledLanes |= spawnedLane;
    root2.entanglements[spawnedLaneIndex] = root2.entanglements[spawnedLaneIndex] | 1073741824 | entangledLanes & 261930;
  }
  function markRootEntangled(root2, entangledLanes) {
    var rootEntangledLanes = root2.entangledLanes |= entangledLanes;
    for (root2 = root2.entanglements; rootEntangledLanes; ) {
      var index$8 = 31 - clz32(rootEntangledLanes), lane = 1 << index$8;
      lane & entangledLanes | root2[index$8] & entangledLanes && (root2[index$8] |= entangledLanes);
      rootEntangledLanes &= ~lane;
    }
  }
  function getBumpedLaneForHydration(root2, renderLanes2) {
    var renderLane = renderLanes2 & -renderLanes2;
    renderLane = 0 !== (renderLane & 42) ? 1 : getBumpedLaneForHydrationByLane(renderLane);
    return 0 !== (renderLane & (root2.suspendedLanes | renderLanes2)) ? 0 : renderLane;
  }
  function getBumpedLaneForHydrationByLane(lane) {
    switch (lane) {
      case 2:
        lane = 1;
        break;
      case 8:
        lane = 4;
        break;
      case 32:
        lane = 16;
        break;
      case 256:
      case 512:
      case 1024:
      case 2048:
      case 4096:
      case 8192:
      case 16384:
      case 32768:
      case 65536:
      case 131072:
      case 262144:
      case 524288:
      case 1048576:
      case 2097152:
      case 4194304:
      case 8388608:
      case 16777216:
      case 33554432:
        lane = 128;
        break;
      case 268435456:
        lane = 134217728;
        break;
      default:
        lane = 0;
    }
    return lane;
  }
  function lanesToEventPriority(lanes) {
    lanes &= -lanes;
    return 2 < lanes ? 8 < lanes ? 0 !== (lanes & 134217727) ? 32 : 268435456 : 8 : 2;
  }
  function resolveUpdatePriority() {
    var updatePriority = ReactDOMSharedInternals.p;
    if (0 !== updatePriority) return updatePriority;
    updatePriority = window.event;
    return void 0 === updatePriority ? 32 : getEventPriority(updatePriority.type);
  }
  function runWithPriority(priority, fn) {
    var previousPriority = ReactDOMSharedInternals.p;
    try {
      return ReactDOMSharedInternals.p = priority, fn();
    } finally {
      ReactDOMSharedInternals.p = previousPriority;
    }
  }
  var randomKey = Math.random().toString(36).slice(2), internalInstanceKey = "__reactFiber$" + randomKey, internalPropsKey = "__reactProps$" + randomKey, internalContainerInstanceKey = "__reactContainer$" + randomKey, internalEventHandlersKey = "__reactEvents$" + randomKey, internalEventHandlerListenersKey = "__reactListeners$" + randomKey, internalEventHandlesSetKey = "__reactHandles$" + randomKey, internalRootNodeResourcesKey = "__reactResources$" + randomKey, internalHoistableMarker = "__reactMarker$" + randomKey;
  function detachDeletedInstance(node) {
    delete node[internalInstanceKey];
    delete node[internalPropsKey];
    delete node[internalEventHandlersKey];
    delete node[internalEventHandlerListenersKey];
    delete node[internalEventHandlesSetKey];
  }
  function getClosestInstanceFromNode(targetNode) {
    var targetInst = targetNode[internalInstanceKey];
    if (targetInst) return targetInst;
    for (var parentNode = targetNode.parentNode; parentNode; ) {
      if (targetInst = parentNode[internalContainerInstanceKey] || parentNode[internalInstanceKey]) {
        parentNode = targetInst.alternate;
        if (null !== targetInst.child || null !== parentNode && null !== parentNode.child)
          for (targetNode = getParentHydrationBoundary(targetNode); null !== targetNode; ) {
            if (parentNode = targetNode[internalInstanceKey]) return parentNode;
            targetNode = getParentHydrationBoundary(targetNode);
          }
        return targetInst;
      }
      targetNode = parentNode;
      parentNode = targetNode.parentNode;
    }
    return null;
  }
  function getInstanceFromNode(node) {
    if (node = node[internalInstanceKey] || node[internalContainerInstanceKey]) {
      var tag = node.tag;
      if (5 === tag || 6 === tag || 13 === tag || 31 === tag || 26 === tag || 27 === tag || 3 === tag)
        return node;
    }
    return null;
  }
  function getNodeFromInstance(inst) {
    var tag = inst.tag;
    if (5 === tag || 26 === tag || 27 === tag || 6 === tag) return inst.stateNode;
    throw Error(formatProdErrorMessage(33));
  }
  function getResourcesFromRoot(root2) {
    var resources = root2[internalRootNodeResourcesKey];
    resources || (resources = root2[internalRootNodeResourcesKey] = { hoistableStyles: /* @__PURE__ */ new Map(), hoistableScripts: /* @__PURE__ */ new Map() });
    return resources;
  }
  function markNodeAsHoistable(node) {
    node[internalHoistableMarker] = true;
  }
  var allNativeEvents = /* @__PURE__ */ new Set(), registrationNameDependencies = {};
  function registerTwoPhaseEvent(registrationName, dependencies) {
    registerDirectEvent(registrationName, dependencies);
    registerDirectEvent(registrationName + "Capture", dependencies);
  }
  function registerDirectEvent(registrationName, dependencies) {
    registrationNameDependencies[registrationName] = dependencies;
    for (registrationName = 0; registrationName < dependencies.length; registrationName++)
      allNativeEvents.add(dependencies[registrationName]);
  }
  var VALID_ATTRIBUTE_NAME_REGEX = RegExp(
    "^[:A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD][:A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD\\-.0-9\\u00B7\\u0300-\\u036F\\u203F-\\u2040]*$"
  ), illegalAttributeNameCache = {}, validatedAttributeNameCache = {};
  function isAttributeNameSafe(attributeName) {
    if (hasOwnProperty.call(validatedAttributeNameCache, attributeName))
      return true;
    if (hasOwnProperty.call(illegalAttributeNameCache, attributeName)) return false;
    if (VALID_ATTRIBUTE_NAME_REGEX.test(attributeName))
      return validatedAttributeNameCache[attributeName] = true;
    illegalAttributeNameCache[attributeName] = true;
    return false;
  }
  function setValueForAttribute(node, name, value) {
    if (isAttributeNameSafe(name))
      if (null === value) node.removeAttribute(name);
      else {
        switch (typeof value) {
          case "undefined":
          case "function":
          case "symbol":
            node.removeAttribute(name);
            return;
          case "boolean":
            var prefix$10 = name.toLowerCase().slice(0, 5);
            if ("data-" !== prefix$10 && "aria-" !== prefix$10) {
              node.removeAttribute(name);
              return;
            }
        }
        node.setAttribute(name, "" + value);
      }
  }
  function setValueForKnownAttribute(node, name, value) {
    if (null === value) node.removeAttribute(name);
    else {
      switch (typeof value) {
        case "undefined":
        case "function":
        case "symbol":
        case "boolean":
          node.removeAttribute(name);
          return;
      }
      node.setAttribute(name, "" + value);
    }
  }
  function setValueForNamespacedAttribute(node, namespace, name, value) {
    if (null === value) node.removeAttribute(name);
    else {
      switch (typeof value) {
        case "undefined":
        case "function":
        case "symbol":
        case "boolean":
          node.removeAttribute(name);
          return;
      }
      node.setAttributeNS(namespace, name, "" + value);
    }
  }
  function getToStringValue(value) {
    switch (typeof value) {
      case "bigint":
      case "boolean":
      case "number":
      case "string":
      case "undefined":
        return value;
      case "object":
        return value;
      default:
        return "";
    }
  }
  function isCheckable(elem) {
    var type = elem.type;
    return (elem = elem.nodeName) && "input" === elem.toLowerCase() && ("checkbox" === type || "radio" === type);
  }
  function trackValueOnNode(node, valueField, currentValue) {
    var descriptor = Object.getOwnPropertyDescriptor(
      node.constructor.prototype,
      valueField
    );
    if (!node.hasOwnProperty(valueField) && "undefined" !== typeof descriptor && "function" === typeof descriptor.get && "function" === typeof descriptor.set) {
      var get = descriptor.get, set = descriptor.set;
      Object.defineProperty(node, valueField, {
        configurable: true,
        get: function() {
          return get.call(this);
        },
        set: function(value) {
          currentValue = "" + value;
          set.call(this, value);
        }
      });
      Object.defineProperty(node, valueField, {
        enumerable: descriptor.enumerable
      });
      return {
        getValue: function() {
          return currentValue;
        },
        setValue: function(value) {
          currentValue = "" + value;
        },
        stopTracking: function() {
          node._valueTracker = null;
          delete node[valueField];
        }
      };
    }
  }
  function track(node) {
    if (!node._valueTracker) {
      var valueField = isCheckable(node) ? "checked" : "value";
      node._valueTracker = trackValueOnNode(
        node,
        valueField,
        "" + node[valueField]
      );
    }
  }
  function updateValueIfChanged(node) {
    if (!node) return false;
    var tracker = node._valueTracker;
    if (!tracker) return true;
    var lastValue = tracker.getValue();
    var value = "";
    node && (value = isCheckable(node) ? node.checked ? "true" : "false" : node.value);
    node = value;
    return node !== lastValue ? (tracker.setValue(node), true) : false;
  }
  function getActiveElement(doc) {
    doc = doc || ("undefined" !== typeof document ? document : void 0);
    if ("undefined" === typeof doc) return null;
    try {
      return doc.activeElement || doc.body;
    } catch (e) {
      return doc.body;
    }
  }
  var escapeSelectorAttributeValueInsideDoubleQuotesRegex = /[\n"\\]/g;
  function escapeSelectorAttributeValueInsideDoubleQuotes(value) {
    return value.replace(
      escapeSelectorAttributeValueInsideDoubleQuotesRegex,
      function(ch) {
        return "\\" + ch.charCodeAt(0).toString(16) + " ";
      }
    );
  }
  function updateInput(element, value, defaultValue, lastDefaultValue, checked, defaultChecked, type, name) {
    element.name = "";
    null != type && "function" !== typeof type && "symbol" !== typeof type && "boolean" !== typeof type ? element.type = type : element.removeAttribute("type");
    if (null != value)
      if ("number" === type) {
        if (0 === value && "" === element.value || element.value != value)
          element.value = "" + getToStringValue(value);
      } else
        element.value !== "" + getToStringValue(value) && (element.value = "" + getToStringValue(value));
    else
      "submit" !== type && "reset" !== type || element.removeAttribute("value");
    null != value ? setDefaultValue(element, type, getToStringValue(value)) : null != defaultValue ? setDefaultValue(element, type, getToStringValue(defaultValue)) : null != lastDefaultValue && element.removeAttribute("value");
    null == checked && null != defaultChecked && (element.defaultChecked = !!defaultChecked);
    null != checked && (element.checked = checked && "function" !== typeof checked && "symbol" !== typeof checked);
    null != name && "function" !== typeof name && "symbol" !== typeof name && "boolean" !== typeof name ? element.name = "" + getToStringValue(name) : element.removeAttribute("name");
  }
  function initInput(element, value, defaultValue, checked, defaultChecked, type, name, isHydrating2) {
    null != type && "function" !== typeof type && "symbol" !== typeof type && "boolean" !== typeof type && (element.type = type);
    if (null != value || null != defaultValue) {
      if (!("submit" !== type && "reset" !== type || void 0 !== value && null !== value)) {
        track(element);
        return;
      }
      defaultValue = null != defaultValue ? "" + getToStringValue(defaultValue) : "";
      value = null != value ? "" + getToStringValue(value) : defaultValue;
      isHydrating2 || value === element.value || (element.value = value);
      element.defaultValue = value;
    }
    checked = null != checked ? checked : defaultChecked;
    checked = "function" !== typeof checked && "symbol" !== typeof checked && !!checked;
    element.checked = isHydrating2 ? element.checked : !!checked;
    element.defaultChecked = !!checked;
    null != name && "function" !== typeof name && "symbol" !== typeof name && "boolean" !== typeof name && (element.name = name);
    track(element);
  }
  function setDefaultValue(node, type, value) {
    "number" === type && getActiveElement(node.ownerDocument) === node || node.defaultValue === "" + value || (node.defaultValue = "" + value);
  }
  function updateOptions(node, multiple, propValue, setDefaultSelected) {
    node = node.options;
    if (multiple) {
      multiple = {};
      for (var i = 0; i < propValue.length; i++)
        multiple["$" + propValue[i]] = true;
      for (propValue = 0; propValue < node.length; propValue++)
        i = multiple.hasOwnProperty("$" + node[propValue].value), node[propValue].selected !== i && (node[propValue].selected = i), i && setDefaultSelected && (node[propValue].defaultSelected = true);
    } else {
      propValue = "" + getToStringValue(propValue);
      multiple = null;
      for (i = 0; i < node.length; i++) {
        if (node[i].value === propValue) {
          node[i].selected = true;
          setDefaultSelected && (node[i].defaultSelected = true);
          return;
        }
        null !== multiple || node[i].disabled || (multiple = node[i]);
      }
      null !== multiple && (multiple.selected = true);
    }
  }
  function updateTextarea(element, value, defaultValue) {
    if (null != value && (value = "" + getToStringValue(value), value !== element.value && (element.value = value), null == defaultValue)) {
      element.defaultValue !== value && (element.defaultValue = value);
      return;
    }
    element.defaultValue = null != defaultValue ? "" + getToStringValue(defaultValue) : "";
  }
  function initTextarea(element, value, defaultValue, children) {
    if (null == value) {
      if (null != children) {
        if (null != defaultValue) throw Error(formatProdErrorMessage(92));
        if (isArrayImpl(children)) {
          if (1 < children.length) throw Error(formatProdErrorMessage(93));
          children = children[0];
        }
        defaultValue = children;
      }
      null == defaultValue && (defaultValue = "");
      value = defaultValue;
    }
    defaultValue = getToStringValue(value);
    element.defaultValue = defaultValue;
    children = element.textContent;
    children === defaultValue && "" !== children && null !== children && (element.value = children);
    track(element);
  }
  function setTextContent(node, text) {
    if (text) {
      var firstChild = node.firstChild;
      if (firstChild && firstChild === node.lastChild && 3 === firstChild.nodeType) {
        firstChild.nodeValue = text;
        return;
      }
    }
    node.textContent = text;
  }
  var unitlessNumbers = new Set(
    "animationIterationCount aspectRatio borderImageOutset borderImageSlice borderImageWidth boxFlex boxFlexGroup boxOrdinalGroup columnCount columns flex flexGrow flexPositive flexShrink flexNegative flexOrder gridArea gridRow gridRowEnd gridRowSpan gridRowStart gridColumn gridColumnEnd gridColumnSpan gridColumnStart fontWeight lineClamp lineHeight opacity order orphans scale tabSize widows zIndex zoom fillOpacity floodOpacity stopOpacity strokeDasharray strokeDashoffset strokeMiterlimit strokeOpacity strokeWidth MozAnimationIterationCount MozBoxFlex MozBoxFlexGroup MozLineClamp msAnimationIterationCount msFlex msZoom msFlexGrow msFlexNegative msFlexOrder msFlexPositive msFlexShrink msGridColumn msGridColumnSpan msGridRow msGridRowSpan WebkitAnimationIterationCount WebkitBoxFlex WebKitBoxFlexGroup WebkitBoxOrdinalGroup WebkitColumnCount WebkitColumns WebkitFlex WebkitFlexGrow WebkitFlexPositive WebkitFlexShrink WebkitLineClamp".split(
      " "
    )
  );
  function setValueForStyle(style2, styleName, value) {
    var isCustomProperty = 0 === styleName.indexOf("--");
    null == value || "boolean" === typeof value || "" === value ? isCustomProperty ? style2.setProperty(styleName, "") : "float" === styleName ? style2.cssFloat = "" : style2[styleName] = "" : isCustomProperty ? style2.setProperty(styleName, value) : "number" !== typeof value || 0 === value || unitlessNumbers.has(styleName) ? "float" === styleName ? style2.cssFloat = value : style2[styleName] = ("" + value).trim() : style2[styleName] = value + "px";
  }
  function setValueForStyles(node, styles, prevStyles) {
    if (null != styles && "object" !== typeof styles)
      throw Error(formatProdErrorMessage(62));
    node = node.style;
    if (null != prevStyles) {
      for (var styleName in prevStyles)
        !prevStyles.hasOwnProperty(styleName) || null != styles && styles.hasOwnProperty(styleName) || (0 === styleName.indexOf("--") ? node.setProperty(styleName, "") : "float" === styleName ? node.cssFloat = "" : node[styleName] = "");
      for (var styleName$16 in styles)
        styleName = styles[styleName$16], styles.hasOwnProperty(styleName$16) && prevStyles[styleName$16] !== styleName && setValueForStyle(node, styleName$16, styleName);
    } else
      for (var styleName$17 in styles)
        styles.hasOwnProperty(styleName$17) && setValueForStyle(node, styleName$17, styles[styleName$17]);
  }
  function isCustomElement(tagName) {
    if (-1 === tagName.indexOf("-")) return false;
    switch (tagName) {
      case "annotation-xml":
      case "color-profile":
      case "font-face":
      case "font-face-src":
      case "font-face-uri":
      case "font-face-format":
      case "font-face-name":
      case "missing-glyph":
        return false;
      default:
        return true;
    }
  }
  var aliases = /* @__PURE__ */ new Map([
    ["acceptCharset", "accept-charset"],
    ["htmlFor", "for"],
    ["httpEquiv", "http-equiv"],
    ["crossOrigin", "crossorigin"],
    ["accentHeight", "accent-height"],
    ["alignmentBaseline", "alignment-baseline"],
    ["arabicForm", "arabic-form"],
    ["baselineShift", "baseline-shift"],
    ["capHeight", "cap-height"],
    ["clipPath", "clip-path"],
    ["clipRule", "clip-rule"],
    ["colorInterpolation", "color-interpolation"],
    ["colorInterpolationFilters", "color-interpolation-filters"],
    ["colorProfile", "color-profile"],
    ["colorRendering", "color-rendering"],
    ["dominantBaseline", "dominant-baseline"],
    ["enableBackground", "enable-background"],
    ["fillOpacity", "fill-opacity"],
    ["fillRule", "fill-rule"],
    ["floodColor", "flood-color"],
    ["floodOpacity", "flood-opacity"],
    ["fontFamily", "font-family"],
    ["fontSize", "font-size"],
    ["fontSizeAdjust", "font-size-adjust"],
    ["fontStretch", "font-stretch"],
    ["fontStyle", "font-style"],
    ["fontVariant", "font-variant"],
    ["fontWeight", "font-weight"],
    ["glyphName", "glyph-name"],
    ["glyphOrientationHorizontal", "glyph-orientation-horizontal"],
    ["glyphOrientationVertical", "glyph-orientation-vertical"],
    ["horizAdvX", "horiz-adv-x"],
    ["horizOriginX", "horiz-origin-x"],
    ["imageRendering", "image-rendering"],
    ["letterSpacing", "letter-spacing"],
    ["lightingColor", "lighting-color"],
    ["markerEnd", "marker-end"],
    ["markerMid", "marker-mid"],
    ["markerStart", "marker-start"],
    ["overlinePosition", "overline-position"],
    ["overlineThickness", "overline-thickness"],
    ["paintOrder", "paint-order"],
    ["panose-1", "panose-1"],
    ["pointerEvents", "pointer-events"],
    ["renderingIntent", "rendering-intent"],
    ["shapeRendering", "shape-rendering"],
    ["stopColor", "stop-color"],
    ["stopOpacity", "stop-opacity"],
    ["strikethroughPosition", "strikethrough-position"],
    ["strikethroughThickness", "strikethrough-thickness"],
    ["strokeDasharray", "stroke-dasharray"],
    ["strokeDashoffset", "stroke-dashoffset"],
    ["strokeLinecap", "stroke-linecap"],
    ["strokeLinejoin", "stroke-linejoin"],
    ["strokeMiterlimit", "stroke-miterlimit"],
    ["strokeOpacity", "stroke-opacity"],
    ["strokeWidth", "stroke-width"],
    ["textAnchor", "text-anchor"],
    ["textDecoration", "text-decoration"],
    ["textRendering", "text-rendering"],
    ["transformOrigin", "transform-origin"],
    ["underlinePosition", "underline-position"],
    ["underlineThickness", "underline-thickness"],
    ["unicodeBidi", "unicode-bidi"],
    ["unicodeRange", "unicode-range"],
    ["unitsPerEm", "units-per-em"],
    ["vAlphabetic", "v-alphabetic"],
    ["vHanging", "v-hanging"],
    ["vIdeographic", "v-ideographic"],
    ["vMathematical", "v-mathematical"],
    ["vectorEffect", "vector-effect"],
    ["vertAdvY", "vert-adv-y"],
    ["vertOriginX", "vert-origin-x"],
    ["vertOriginY", "vert-origin-y"],
    ["wordSpacing", "word-spacing"],
    ["writingMode", "writing-mode"],
    ["xmlnsXlink", "xmlns:xlink"],
    ["xHeight", "x-height"]
  ]), isJavaScriptProtocol = /^[\u0000-\u001F ]*j[\r\n\t]*a[\r\n\t]*v[\r\n\t]*a[\r\n\t]*s[\r\n\t]*c[\r\n\t]*r[\r\n\t]*i[\r\n\t]*p[\r\n\t]*t[\r\n\t]*:/i;
  function sanitizeURL(url) {
    return isJavaScriptProtocol.test("" + url) ? "javascript:throw new Error('React has blocked a javascript: URL as a security precaution.')" : url;
  }
  function noop$1() {
  }
  var currentReplayingEvent = null;
  function getEventTarget(nativeEvent) {
    nativeEvent = nativeEvent.target || nativeEvent.srcElement || window;
    nativeEvent.correspondingUseElement && (nativeEvent = nativeEvent.correspondingUseElement);
    return 3 === nativeEvent.nodeType ? nativeEvent.parentNode : nativeEvent;
  }
  var restoreTarget = null, restoreQueue = null;
  function restoreStateOfTarget(target) {
    var internalInstance = getInstanceFromNode(target);
    if (internalInstance && (target = internalInstance.stateNode)) {
      var props = target[internalPropsKey] || null;
      a: switch (target = internalInstance.stateNode, internalInstance.type) {
        case "input":
          updateInput(
            target,
            props.value,
            props.defaultValue,
            props.defaultValue,
            props.checked,
            props.defaultChecked,
            props.type,
            props.name
          );
          internalInstance = props.name;
          if ("radio" === props.type && null != internalInstance) {
            for (props = target; props.parentNode; ) props = props.parentNode;
            props = props.querySelectorAll(
              'input[name="' + escapeSelectorAttributeValueInsideDoubleQuotes(
                "" + internalInstance
              ) + '"][type="radio"]'
            );
            for (internalInstance = 0; internalInstance < props.length; internalInstance++) {
              var otherNode = props[internalInstance];
              if (otherNode !== target && otherNode.form === target.form) {
                var otherProps = otherNode[internalPropsKey] || null;
                if (!otherProps) throw Error(formatProdErrorMessage(90));
                updateInput(
                  otherNode,
                  otherProps.value,
                  otherProps.defaultValue,
                  otherProps.defaultValue,
                  otherProps.checked,
                  otherProps.defaultChecked,
                  otherProps.type,
                  otherProps.name
                );
              }
            }
            for (internalInstance = 0; internalInstance < props.length; internalInstance++)
              otherNode = props[internalInstance], otherNode.form === target.form && updateValueIfChanged(otherNode);
          }
          break a;
        case "textarea":
          updateTextarea(target, props.value, props.defaultValue);
          break a;
        case "select":
          internalInstance = props.value, null != internalInstance && updateOptions(target, !!props.multiple, internalInstance, false);
      }
    }
  }
  var isInsideEventHandler = false;
  function batchedUpdates$1(fn, a, b) {
    if (isInsideEventHandler) return fn(a, b);
    isInsideEventHandler = true;
    try {
      var JSCompiler_inline_result = fn(a);
      return JSCompiler_inline_result;
    } finally {
      if (isInsideEventHandler = false, null !== restoreTarget || null !== restoreQueue) {
        if (flushSyncWork$1(), restoreTarget && (a = restoreTarget, fn = restoreQueue, restoreQueue = restoreTarget = null, restoreStateOfTarget(a), fn))
          for (a = 0; a < fn.length; a++) restoreStateOfTarget(fn[a]);
      }
    }
  }
  function getListener(inst, registrationName) {
    var stateNode = inst.stateNode;
    if (null === stateNode) return null;
    var props = stateNode[internalPropsKey] || null;
    if (null === props) return null;
    stateNode = props[registrationName];
    a: switch (registrationName) {
      case "onClick":
      case "onClickCapture":
      case "onDoubleClick":
      case "onDoubleClickCapture":
      case "onMouseDown":
      case "onMouseDownCapture":
      case "onMouseMove":
      case "onMouseMoveCapture":
      case "onMouseUp":
      case "onMouseUpCapture":
      case "onMouseEnter":
        (props = !props.disabled) || (inst = inst.type, props = !("button" === inst || "input" === inst || "select" === inst || "textarea" === inst));
        inst = !props;
        break a;
      default:
        inst = false;
    }
    if (inst) return null;
    if (stateNode && "function" !== typeof stateNode)
      throw Error(
        formatProdErrorMessage(231, registrationName, typeof stateNode)
      );
    return stateNode;
  }
  var canUseDOM = !("undefined" === typeof window || "undefined" === typeof window.document || "undefined" === typeof window.document.createElement), passiveBrowserEventsSupported = false;
  if (canUseDOM)
    try {
      var options = {};
      Object.defineProperty(options, "passive", {
        get: function() {
          passiveBrowserEventsSupported = true;
        }
      });
      window.addEventListener("test", options, options);
      window.removeEventListener("test", options, options);
    } catch (e) {
      passiveBrowserEventsSupported = false;
    }
  var root = null, startText = null, fallbackText = null;
  function getData() {
    if (fallbackText) return fallbackText;
    var start, startValue = startText, startLength = startValue.length, end, endValue = "value" in root ? root.value : root.textContent, endLength = endValue.length;
    for (start = 0; start < startLength && startValue[start] === endValue[start]; start++) ;
    var minEnd = startLength - start;
    for (end = 1; end <= minEnd && startValue[startLength - end] === endValue[endLength - end]; end++) ;
    return fallbackText = endValue.slice(start, 1 < end ? 1 - end : void 0);
  }
  function getEventCharCode(nativeEvent) {
    var keyCode = nativeEvent.keyCode;
    "charCode" in nativeEvent ? (nativeEvent = nativeEvent.charCode, 0 === nativeEvent && 13 === keyCode && (nativeEvent = 13)) : nativeEvent = keyCode;
    10 === nativeEvent && (nativeEvent = 13);
    return 32 <= nativeEvent || 13 === nativeEvent ? nativeEvent : 0;
  }
  function functionThatReturnsTrue() {
    return true;
  }
  function functionThatReturnsFalse() {
    return false;
  }
  function createSyntheticEvent(Interface) {
    function SyntheticBaseEvent(reactName, reactEventType, targetInst, nativeEvent, nativeEventTarget) {
      this._reactName = reactName;
      this._targetInst = targetInst;
      this.type = reactEventType;
      this.nativeEvent = nativeEvent;
      this.target = nativeEventTarget;
      this.currentTarget = null;
      for (var propName in Interface)
        Interface.hasOwnProperty(propName) && (reactName = Interface[propName], this[propName] = reactName ? reactName(nativeEvent) : nativeEvent[propName]);
      this.isDefaultPrevented = (null != nativeEvent.defaultPrevented ? nativeEvent.defaultPrevented : false === nativeEvent.returnValue) ? functionThatReturnsTrue : functionThatReturnsFalse;
      this.isPropagationStopped = functionThatReturnsFalse;
      return this;
    }
    assign(SyntheticBaseEvent.prototype, {
      preventDefault: function() {
        this.defaultPrevented = true;
        var event = this.nativeEvent;
        event && (event.preventDefault ? event.preventDefault() : "unknown" !== typeof event.returnValue && (event.returnValue = false), this.isDefaultPrevented = functionThatReturnsTrue);
      },
      stopPropagation: function() {
        var event = this.nativeEvent;
        event && (event.stopPropagation ? event.stopPropagation() : "unknown" !== typeof event.cancelBubble && (event.cancelBubble = true), this.isPropagationStopped = functionThatReturnsTrue);
      },
      persist: function() {
      },
      isPersistent: functionThatReturnsTrue
    });
    return SyntheticBaseEvent;
  }
  var EventInterface = {
    eventPhase: 0,
    bubbles: 0,
    cancelable: 0,
    timeStamp: function(event) {
      return event.timeStamp || Date.now();
    },
    defaultPrevented: 0,
    isTrusted: 0
  }, SyntheticEvent = createSyntheticEvent(EventInterface), UIEventInterface = assign({}, EventInterface, { view: 0, detail: 0 }), SyntheticUIEvent = createSyntheticEvent(UIEventInterface), lastMovementX, lastMovementY, lastMouseEvent, MouseEventInterface = assign({}, UIEventInterface, {
    screenX: 0,
    screenY: 0,
    clientX: 0,
    clientY: 0,
    pageX: 0,
    pageY: 0,
    ctrlKey: 0,
    shiftKey: 0,
    altKey: 0,
    metaKey: 0,
    getModifierState: getEventModifierState,
    button: 0,
    buttons: 0,
    relatedTarget: function(event) {
      return void 0 === event.relatedTarget ? event.fromElement === event.srcElement ? event.toElement : event.fromElement : event.relatedTarget;
    },
    movementX: function(event) {
      if ("movementX" in event) return event.movementX;
      event !== lastMouseEvent && (lastMouseEvent && "mousemove" === event.type ? (lastMovementX = event.screenX - lastMouseEvent.screenX, lastMovementY = event.screenY - lastMouseEvent.screenY) : lastMovementY = lastMovementX = 0, lastMouseEvent = event);
      return lastMovementX;
    },
    movementY: function(event) {
      return "movementY" in event ? event.movementY : lastMovementY;
    }
  }), SyntheticMouseEvent = createSyntheticEvent(MouseEventInterface), DragEventInterface = assign({}, MouseEventInterface, { dataTransfer: 0 }), SyntheticDragEvent = createSyntheticEvent(DragEventInterface), FocusEventInterface = assign({}, UIEventInterface, { relatedTarget: 0 }), SyntheticFocusEvent = createSyntheticEvent(FocusEventInterface), AnimationEventInterface = assign({}, EventInterface, {
    animationName: 0,
    elapsedTime: 0,
    pseudoElement: 0
  }), SyntheticAnimationEvent = createSyntheticEvent(AnimationEventInterface), ClipboardEventInterface = assign({}, EventInterface, {
    clipboardData: function(event) {
      return "clipboardData" in event ? event.clipboardData : window.clipboardData;
    }
  }), SyntheticClipboardEvent = createSyntheticEvent(ClipboardEventInterface), CompositionEventInterface = assign({}, EventInterface, { data: 0 }), SyntheticCompositionEvent = createSyntheticEvent(CompositionEventInterface), normalizeKey = {
    Esc: "Escape",
    Spacebar: " ",
    Left: "ArrowLeft",
    Up: "ArrowUp",
    Right: "ArrowRight",
    Down: "ArrowDown",
    Del: "Delete",
    Win: "OS",
    Menu: "ContextMenu",
    Apps: "ContextMenu",
    Scroll: "ScrollLock",
    MozPrintableKey: "Unidentified"
  }, translateToKey = {
    8: "Backspace",
    9: "Tab",
    12: "Clear",
    13: "Enter",
    16: "Shift",
    17: "Control",
    18: "Alt",
    19: "Pause",
    20: "CapsLock",
    27: "Escape",
    32: " ",
    33: "PageUp",
    34: "PageDown",
    35: "End",
    36: "Home",
    37: "ArrowLeft",
    38: "ArrowUp",
    39: "ArrowRight",
    40: "ArrowDown",
    45: "Insert",
    46: "Delete",
    112: "F1",
    113: "F2",
    114: "F3",
    115: "F4",
    116: "F5",
    117: "F6",
    118: "F7",
    119: "F8",
    120: "F9",
    121: "F10",
    122: "F11",
    123: "F12",
    144: "NumLock",
    145: "ScrollLock",
    224: "Meta"
  }, modifierKeyToProp = {
    Alt: "altKey",
    Control: "ctrlKey",
    Meta: "metaKey",
    Shift: "shiftKey"
  };
  function modifierStateGetter(keyArg) {
    var nativeEvent = this.nativeEvent;
    return nativeEvent.getModifierState ? nativeEvent.getModifierState(keyArg) : (keyArg = modifierKeyToProp[keyArg]) ? !!nativeEvent[keyArg] : false;
  }
  function getEventModifierState() {
    return modifierStateGetter;
  }
  var KeyboardEventInterface = assign({}, UIEventInterface, {
    key: function(nativeEvent) {
      if (nativeEvent.key) {
        var key = normalizeKey[nativeEvent.key] || nativeEvent.key;
        if ("Unidentified" !== key) return key;
      }
      return "keypress" === nativeEvent.type ? (nativeEvent = getEventCharCode(nativeEvent), 13 === nativeEvent ? "Enter" : String.fromCharCode(nativeEvent)) : "keydown" === nativeEvent.type || "keyup" === nativeEvent.type ? translateToKey[nativeEvent.keyCode] || "Unidentified" : "";
    },
    code: 0,
    location: 0,
    ctrlKey: 0,
    shiftKey: 0,
    altKey: 0,
    metaKey: 0,
    repeat: 0,
    locale: 0,
    getModifierState: getEventModifierState,
    charCode: function(event) {
      return "keypress" === event.type ? getEventCharCode(event) : 0;
    },
    keyCode: function(event) {
      return "keydown" === event.type || "keyup" === event.type ? event.keyCode : 0;
    },
    which: function(event) {
      return "keypress" === event.type ? getEventCharCode(event) : "keydown" === event.type || "keyup" === event.type ? event.keyCode : 0;
    }
  }), SyntheticKeyboardEvent = createSyntheticEvent(KeyboardEventInterface), PointerEventInterface = assign({}, MouseEventInterface, {
    pointerId: 0,
    width: 0,
    height: 0,
    pressure: 0,
    tangentialPressure: 0,
    tiltX: 0,
    tiltY: 0,
    twist: 0,
    pointerType: 0,
    isPrimary: 0
  }), SyntheticPointerEvent = createSyntheticEvent(PointerEventInterface), TouchEventInterface = assign({}, UIEventInterface, {
    touches: 0,
    targetTouches: 0,
    changedTouches: 0,
    altKey: 0,
    metaKey: 0,
    ctrlKey: 0,
    shiftKey: 0,
    getModifierState: getEventModifierState
  }), SyntheticTouchEvent = createSyntheticEvent(TouchEventInterface), TransitionEventInterface = assign({}, EventInterface, {
    propertyName: 0,
    elapsedTime: 0,
    pseudoElement: 0
  }), SyntheticTransitionEvent = createSyntheticEvent(TransitionEventInterface), WheelEventInterface = assign({}, MouseEventInterface, {
    deltaX: function(event) {
      return "deltaX" in event ? event.deltaX : "wheelDeltaX" in event ? -event.wheelDeltaX : 0;
    },
    deltaY: function(event) {
      return "deltaY" in event ? event.deltaY : "wheelDeltaY" in event ? -event.wheelDeltaY : "wheelDelta" in event ? -event.wheelDelta : 0;
    },
    deltaZ: 0,
    deltaMode: 0
  }), SyntheticWheelEvent = createSyntheticEvent(WheelEventInterface), ToggleEventInterface = assign({}, EventInterface, {
    newState: 0,
    oldState: 0
  }), SyntheticToggleEvent = createSyntheticEvent(ToggleEventInterface), END_KEYCODES = [9, 13, 27, 32], canUseCompositionEvent = canUseDOM && "CompositionEvent" in window, documentMode = null;
  canUseDOM && "documentMode" in document && (documentMode = document.documentMode);
  var canUseTextInputEvent = canUseDOM && "TextEvent" in window && !documentMode, useFallbackCompositionData = canUseDOM && (!canUseCompositionEvent || documentMode && 8 < documentMode && 11 >= documentMode), SPACEBAR_CHAR = String.fromCharCode(32), hasSpaceKeypress = false;
  function isFallbackCompositionEnd(domEventName, nativeEvent) {
    switch (domEventName) {
      case "keyup":
        return -1 !== END_KEYCODES.indexOf(nativeEvent.keyCode);
      case "keydown":
        return 229 !== nativeEvent.keyCode;
      case "keypress":
      case "mousedown":
      case "focusout":
        return true;
      default:
        return false;
    }
  }
  function getDataFromCustomEvent(nativeEvent) {
    nativeEvent = nativeEvent.detail;
    return "object" === typeof nativeEvent && "data" in nativeEvent ? nativeEvent.data : null;
  }
  var isComposing = false;
  function getNativeBeforeInputChars(domEventName, nativeEvent) {
    switch (domEventName) {
      case "compositionend":
        return getDataFromCustomEvent(nativeEvent);
      case "keypress":
        if (32 !== nativeEvent.which) return null;
        hasSpaceKeypress = true;
        return SPACEBAR_CHAR;
      case "textInput":
        return domEventName = nativeEvent.data, domEventName === SPACEBAR_CHAR && hasSpaceKeypress ? null : domEventName;
      default:
        return null;
    }
  }
  function getFallbackBeforeInputChars(domEventName, nativeEvent) {
    if (isComposing)
      return "compositionend" === domEventName || !canUseCompositionEvent && isFallbackCompositionEnd(domEventName, nativeEvent) ? (domEventName = getData(), fallbackText = startText = root = null, isComposing = false, domEventName) : null;
    switch (domEventName) {
      case "paste":
        return null;
      case "keypress":
        if (!(nativeEvent.ctrlKey || nativeEvent.altKey || nativeEvent.metaKey) || nativeEvent.ctrlKey && nativeEvent.altKey) {
          if (nativeEvent.char && 1 < nativeEvent.char.length)
            return nativeEvent.char;
          if (nativeEvent.which) return String.fromCharCode(nativeEvent.which);
        }
        return null;
      case "compositionend":
        return useFallbackCompositionData && "ko" !== nativeEvent.locale ? null : nativeEvent.data;
      default:
        return null;
    }
  }
  var supportedInputTypes = {
    color: true,
    date: true,
    datetime: true,
    "datetime-local": true,
    email: true,
    month: true,
    number: true,
    password: true,
    range: true,
    search: true,
    tel: true,
    text: true,
    time: true,
    url: true,
    week: true
  };
  function isTextInputElement(elem) {
    var nodeName = elem && elem.nodeName && elem.nodeName.toLowerCase();
    return "input" === nodeName ? !!supportedInputTypes[elem.type] : "textarea" === nodeName ? true : false;
  }
  function createAndAccumulateChangeEvent(dispatchQueue, inst, nativeEvent, target) {
    restoreTarget ? restoreQueue ? restoreQueue.push(target) : restoreQueue = [target] : restoreTarget = target;
    inst = accumulateTwoPhaseListeners(inst, "onChange");
    0 < inst.length && (nativeEvent = new SyntheticEvent(
      "onChange",
      "change",
      null,
      nativeEvent,
      target
    ), dispatchQueue.push({ event: nativeEvent, listeners: inst }));
  }
  var activeElement$1 = null, activeElementInst$1 = null;
  function runEventInBatch(dispatchQueue) {
    processDispatchQueue(dispatchQueue, 0);
  }
  function getInstIfValueChanged(targetInst) {
    var targetNode = getNodeFromInstance(targetInst);
    if (updateValueIfChanged(targetNode)) return targetInst;
  }
  function getTargetInstForChangeEvent(domEventName, targetInst) {
    if ("change" === domEventName) return targetInst;
  }
  var isInputEventSupported = false;
  if (canUseDOM) {
    var JSCompiler_inline_result$jscomp$286;
    if (canUseDOM) {
      var isSupported$jscomp$inline_427 = "oninput" in document;
      if (!isSupported$jscomp$inline_427) {
        var element$jscomp$inline_428 = document.createElement("div");
        element$jscomp$inline_428.setAttribute("oninput", "return;");
        isSupported$jscomp$inline_427 = "function" === typeof element$jscomp$inline_428.oninput;
      }
      JSCompiler_inline_result$jscomp$286 = isSupported$jscomp$inline_427;
    } else JSCompiler_inline_result$jscomp$286 = false;
    isInputEventSupported = JSCompiler_inline_result$jscomp$286 && (!document.documentMode || 9 < document.documentMode);
  }
  function stopWatchingForValueChange() {
    activeElement$1 && (activeElement$1.detachEvent("onpropertychange", handlePropertyChange), activeElementInst$1 = activeElement$1 = null);
  }
  function handlePropertyChange(nativeEvent) {
    if ("value" === nativeEvent.propertyName && getInstIfValueChanged(activeElementInst$1)) {
      var dispatchQueue = [];
      createAndAccumulateChangeEvent(
        dispatchQueue,
        activeElementInst$1,
        nativeEvent,
        getEventTarget(nativeEvent)
      );
      batchedUpdates$1(runEventInBatch, dispatchQueue);
    }
  }
  function handleEventsForInputEventPolyfill(domEventName, target, targetInst) {
    "focusin" === domEventName ? (stopWatchingForValueChange(), activeElement$1 = target, activeElementInst$1 = targetInst, activeElement$1.attachEvent("onpropertychange", handlePropertyChange)) : "focusout" === domEventName && stopWatchingForValueChange();
  }
  function getTargetInstForInputEventPolyfill(domEventName) {
    if ("selectionchange" === domEventName || "keyup" === domEventName || "keydown" === domEventName)
      return getInstIfValueChanged(activeElementInst$1);
  }
  function getTargetInstForClickEvent(domEventName, targetInst) {
    if ("click" === domEventName) return getInstIfValueChanged(targetInst);
  }
  function getTargetInstForInputOrChangeEvent(domEventName, targetInst) {
    if ("input" === domEventName || "change" === domEventName)
      return getInstIfValueChanged(targetInst);
  }
  function is(x, y) {
    return x === y && (0 !== x || 1 / x === 1 / y) || x !== x && y !== y;
  }
  var objectIs = "function" === typeof Object.is ? Object.is : is;
  function shallowEqual(objA, objB) {
    if (objectIs(objA, objB)) return true;
    if ("object" !== typeof objA || null === objA || "object" !== typeof objB || null === objB)
      return false;
    var keysA = Object.keys(objA), keysB = Object.keys(objB);
    if (keysA.length !== keysB.length) return false;
    for (keysB = 0; keysB < keysA.length; keysB++) {
      var currentKey = keysA[keysB];
      if (!hasOwnProperty.call(objB, currentKey) || !objectIs(objA[currentKey], objB[currentKey]))
        return false;
    }
    return true;
  }
  function getLeafNode(node) {
    for (; node && node.firstChild; ) node = node.firstChild;
    return node;
  }
  function getNodeForCharacterOffset(root2, offset) {
    var node = getLeafNode(root2);
    root2 = 0;
    for (var nodeEnd; node; ) {
      if (3 === node.nodeType) {
        nodeEnd = root2 + node.textContent.length;
        if (root2 <= offset && nodeEnd >= offset)
          return { node, offset: offset - root2 };
        root2 = nodeEnd;
      }
      a: {
        for (; node; ) {
          if (node.nextSibling) {
            node = node.nextSibling;
            break a;
          }
          node = node.parentNode;
        }
        node = void 0;
      }
      node = getLeafNode(node);
    }
  }
  function containsNode(outerNode, innerNode) {
    return outerNode && innerNode ? outerNode === innerNode ? true : outerNode && 3 === outerNode.nodeType ? false : innerNode && 3 === innerNode.nodeType ? containsNode(outerNode, innerNode.parentNode) : "contains" in outerNode ? outerNode.contains(innerNode) : outerNode.compareDocumentPosition ? !!(outerNode.compareDocumentPosition(innerNode) & 16) : false : false;
  }
  function getActiveElementDeep(containerInfo) {
    containerInfo = null != containerInfo && null != containerInfo.ownerDocument && null != containerInfo.ownerDocument.defaultView ? containerInfo.ownerDocument.defaultView : window;
    for (var element = getActiveElement(containerInfo.document); element instanceof containerInfo.HTMLIFrameElement; ) {
      try {
        var JSCompiler_inline_result = "string" === typeof element.contentWindow.location.href;
      } catch (err) {
        JSCompiler_inline_result = false;
      }
      if (JSCompiler_inline_result) containerInfo = element.contentWindow;
      else break;
      element = getActiveElement(containerInfo.document);
    }
    return element;
  }
  function hasSelectionCapabilities(elem) {
    var nodeName = elem && elem.nodeName && elem.nodeName.toLowerCase();
    return nodeName && ("input" === nodeName && ("text" === elem.type || "search" === elem.type || "tel" === elem.type || "url" === elem.type || "password" === elem.type) || "textarea" === nodeName || "true" === elem.contentEditable);
  }
  var skipSelectionChangeEvent = canUseDOM && "documentMode" in document && 11 >= document.documentMode, activeElement = null, activeElementInst = null, lastSelection = null, mouseDown = false;
  function constructSelectEvent(dispatchQueue, nativeEvent, nativeEventTarget) {
    var doc = nativeEventTarget.window === nativeEventTarget ? nativeEventTarget.document : 9 === nativeEventTarget.nodeType ? nativeEventTarget : nativeEventTarget.ownerDocument;
    mouseDown || null == activeElement || activeElement !== getActiveElement(doc) || (doc = activeElement, "selectionStart" in doc && hasSelectionCapabilities(doc) ? doc = { start: doc.selectionStart, end: doc.selectionEnd } : (doc = (doc.ownerDocument && doc.ownerDocument.defaultView || window).getSelection(), doc = {
      anchorNode: doc.anchorNode,
      anchorOffset: doc.anchorOffset,
      focusNode: doc.focusNode,
      focusOffset: doc.focusOffset
    }), lastSelection && shallowEqual(lastSelection, doc) || (lastSelection = doc, doc = accumulateTwoPhaseListeners(activeElementInst, "onSelect"), 0 < doc.length && (nativeEvent = new SyntheticEvent(
      "onSelect",
      "select",
      null,
      nativeEvent,
      nativeEventTarget
    ), dispatchQueue.push({ event: nativeEvent, listeners: doc }), nativeEvent.target = activeElement)));
  }
  function makePrefixMap(styleProp, eventName) {
    var prefixes = {};
    prefixes[styleProp.toLowerCase()] = eventName.toLowerCase();
    prefixes["Webkit" + styleProp] = "webkit" + eventName;
    prefixes["Moz" + styleProp] = "moz" + eventName;
    return prefixes;
  }
  var vendorPrefixes = {
    animationend: makePrefixMap("Animation", "AnimationEnd"),
    animationiteration: makePrefixMap("Animation", "AnimationIteration"),
    animationstart: makePrefixMap("Animation", "AnimationStart"),
    transitionrun: makePrefixMap("Transition", "TransitionRun"),
    transitionstart: makePrefixMap("Transition", "TransitionStart"),
    transitioncancel: makePrefixMap("Transition", "TransitionCancel"),
    transitionend: makePrefixMap("Transition", "TransitionEnd")
  }, prefixedEventNames = {}, style = {};
  canUseDOM && (style = document.createElement("div").style, "AnimationEvent" in window || (delete vendorPrefixes.animationend.animation, delete vendorPrefixes.animationiteration.animation, delete vendorPrefixes.animationstart.animation), "TransitionEvent" in window || delete vendorPrefixes.transitionend.transition);
  function getVendorPrefixedEventName(eventName) {
    if (prefixedEventNames[eventName]) return prefixedEventNames[eventName];
    if (!vendorPrefixes[eventName]) return eventName;
    var prefixMap = vendorPrefixes[eventName], styleProp;
    for (styleProp in prefixMap)
      if (prefixMap.hasOwnProperty(styleProp) && styleProp in style)
        return prefixedEventNames[eventName] = prefixMap[styleProp];
    return eventName;
  }
  var ANIMATION_END = getVendorPrefixedEventName("animationend"), ANIMATION_ITERATION = getVendorPrefixedEventName("animationiteration"), ANIMATION_START = getVendorPrefixedEventName("animationstart"), TRANSITION_RUN = getVendorPrefixedEventName("transitionrun"), TRANSITION_START = getVendorPrefixedEventName("transitionstart"), TRANSITION_CANCEL = getVendorPrefixedEventName("transitioncancel"), TRANSITION_END = getVendorPrefixedEventName("transitionend"), topLevelEventsToReactNames = /* @__PURE__ */ new Map(), simpleEventPluginEvents = "abort auxClick beforeToggle cancel canPlay canPlayThrough click close contextMenu copy cut drag dragEnd dragEnter dragExit dragLeave dragOver dragStart drop durationChange emptied encrypted ended error gotPointerCapture input invalid keyDown keyPress keyUp load loadedData loadedMetadata loadStart lostPointerCapture mouseDown mouseMove mouseOut mouseOver mouseUp paste pause play playing pointerCancel pointerDown pointerMove pointerOut pointerOver pointerUp progress rateChange reset resize seeked seeking stalled submit suspend timeUpdate touchCancel touchEnd touchStart volumeChange scroll toggle touchMove waiting wheel".split(
    " "
  );
  simpleEventPluginEvents.push("scrollEnd");
  function registerSimpleEvent(domEventName, reactName) {
    topLevelEventsToReactNames.set(domEventName, reactName);
    registerTwoPhaseEvent(reactName, [domEventName]);
  }
  var reportGlobalError = "function" === typeof reportError ? reportError : function(error) {
    if ("object" === typeof window && "function" === typeof window.ErrorEvent) {
      var event = new window.ErrorEvent("error", {
        bubbles: true,
        cancelable: true,
        message: "object" === typeof error && null !== error && "string" === typeof error.message ? String(error.message) : String(error),
        error
      });
      if (!window.dispatchEvent(event)) return;
    } else if ("object" === typeof process && "function" === typeof process.emit) {
      process.emit("uncaughtException", error);
      return;
    }
    console.error(error);
  }, concurrentQueues = [], concurrentQueuesIndex = 0, concurrentlyUpdatedLanes = 0;
  function finishQueueingConcurrentUpdates() {
    for (var endIndex = concurrentQueuesIndex, i = concurrentlyUpdatedLanes = concurrentQueuesIndex = 0; i < endIndex; ) {
      var fiber = concurrentQueues[i];
      concurrentQueues[i++] = null;
      var queue = concurrentQueues[i];
      concurrentQueues[i++] = null;
      var update = concurrentQueues[i];
      concurrentQueues[i++] = null;
      var lane = concurrentQueues[i];
      concurrentQueues[i++] = null;
      if (null !== queue && null !== update) {
        var pending = queue.pending;
        null === pending ? update.next = update : (update.next = pending.next, pending.next = update);
        queue.pending = update;
      }
      0 !== lane && markUpdateLaneFromFiberToRoot(fiber, update, lane);
    }
  }
  function enqueueUpdate$1(fiber, queue, update, lane) {
    concurrentQueues[concurrentQueuesIndex++] = fiber;
    concurrentQueues[concurrentQueuesIndex++] = queue;
    concurrentQueues[concurrentQueuesIndex++] = update;
    concurrentQueues[concurrentQueuesIndex++] = lane;
    concurrentlyUpdatedLanes |= lane;
    fiber.lanes |= lane;
    fiber = fiber.alternate;
    null !== fiber && (fiber.lanes |= lane);
  }
  function enqueueConcurrentHookUpdate(fiber, queue, update, lane) {
    enqueueUpdate$1(fiber, queue, update, lane);
    return getRootForUpdatedFiber(fiber);
  }
  function enqueueConcurrentRenderForLane(fiber, lane) {
    enqueueUpdate$1(fiber, null, null, lane);
    return getRootForUpdatedFiber(fiber);
  }
  function markUpdateLaneFromFiberToRoot(sourceFiber, update, lane) {
    sourceFiber.lanes |= lane;
    var alternate = sourceFiber.alternate;
    null !== alternate && (alternate.lanes |= lane);
    for (var isHidden = false, parent = sourceFiber.return; null !== parent; )
      parent.childLanes |= lane, alternate = parent.alternate, null !== alternate && (alternate.childLanes |= lane), 22 === parent.tag && (sourceFiber = parent.stateNode, null === sourceFiber || sourceFiber._visibility & 1 || (isHidden = true)), sourceFiber = parent, parent = parent.return;
    return 3 === sourceFiber.tag ? (parent = sourceFiber.stateNode, isHidden && null !== update && (isHidden = 31 - clz32(lane), sourceFiber = parent.hiddenUpdates, alternate = sourceFiber[isHidden], null === alternate ? sourceFiber[isHidden] = [update] : alternate.push(update), update.lane = lane | 536870912), parent) : null;
  }
  function getRootForUpdatedFiber(sourceFiber) {
    if (50 < nestedUpdateCount)
      throw nestedUpdateCount = 0, rootWithNestedUpdates = null, Error(formatProdErrorMessage(185));
    for (var parent = sourceFiber.return; null !== parent; )
      sourceFiber = parent, parent = sourceFiber.return;
    return 3 === sourceFiber.tag ? sourceFiber.stateNode : null;
  }
  var emptyContextObject = {};
  function FiberNode(tag, pendingProps, key, mode) {
    this.tag = tag;
    this.key = key;
    this.sibling = this.child = this.return = this.stateNode = this.type = this.elementType = null;
    this.index = 0;
    this.refCleanup = this.ref = null;
    this.pendingProps = pendingProps;
    this.dependencies = this.memoizedState = this.updateQueue = this.memoizedProps = null;
    this.mode = mode;
    this.subtreeFlags = this.flags = 0;
    this.deletions = null;
    this.childLanes = this.lanes = 0;
    this.alternate = null;
  }
  function createFiberImplClass(tag, pendingProps, key, mode) {
    return new FiberNode(tag, pendingProps, key, mode);
  }
  function shouldConstruct(Component) {
    Component = Component.prototype;
    return !(!Component || !Component.isReactComponent);
  }
  function createWorkInProgress(current, pendingProps) {
    var workInProgress2 = current.alternate;
    null === workInProgress2 ? (workInProgress2 = createFiberImplClass(
      current.tag,
      pendingProps,
      current.key,
      current.mode
    ), workInProgress2.elementType = current.elementType, workInProgress2.type = current.type, workInProgress2.stateNode = current.stateNode, workInProgress2.alternate = current, current.alternate = workInProgress2) : (workInProgress2.pendingProps = pendingProps, workInProgress2.type = current.type, workInProgress2.flags = 0, workInProgress2.subtreeFlags = 0, workInProgress2.deletions = null);
    workInProgress2.flags = current.flags & 65011712;
    workInProgress2.childLanes = current.childLanes;
    workInProgress2.lanes = current.lanes;
    workInProgress2.child = current.child;
    workInProgress2.memoizedProps = current.memoizedProps;
    workInProgress2.memoizedState = current.memoizedState;
    workInProgress2.updateQueue = current.updateQueue;
    pendingProps = current.dependencies;
    workInProgress2.dependencies = null === pendingProps ? null : { lanes: pendingProps.lanes, firstContext: pendingProps.firstContext };
    workInProgress2.sibling = current.sibling;
    workInProgress2.index = current.index;
    workInProgress2.ref = current.ref;
    workInProgress2.refCleanup = current.refCleanup;
    return workInProgress2;
  }
  function resetWorkInProgress(workInProgress2, renderLanes2) {
    workInProgress2.flags &= 65011714;
    var current = workInProgress2.alternate;
    null === current ? (workInProgress2.childLanes = 0, workInProgress2.lanes = renderLanes2, workInProgress2.child = null, workInProgress2.subtreeFlags = 0, workInProgress2.memoizedProps = null, workInProgress2.memoizedState = null, workInProgress2.updateQueue = null, workInProgress2.dependencies = null, workInProgress2.stateNode = null) : (workInProgress2.childLanes = current.childLanes, workInProgress2.lanes = current.lanes, workInProgress2.child = current.child, workInProgress2.subtreeFlags = 0, workInProgress2.deletions = null, workInProgress2.memoizedProps = current.memoizedProps, workInProgress2.memoizedState = current.memoizedState, workInProgress2.updateQueue = current.updateQueue, workInProgress2.type = current.type, renderLanes2 = current.dependencies, workInProgress2.dependencies = null === renderLanes2 ? null : {
      lanes: renderLanes2.lanes,
      firstContext: renderLanes2.firstContext
    });
    return workInProgress2;
  }
  function createFiberFromTypeAndProps(type, key, pendingProps, owner, mode, lanes) {
    var fiberTag = 0;
    owner = type;
    if ("function" === typeof type) shouldConstruct(type) && (fiberTag = 1);
    else if ("string" === typeof type)
      fiberTag = isHostHoistableType(
        type,
        pendingProps,
        contextStackCursor.current
      ) ? 26 : "html" === type || "head" === type || "body" === type ? 27 : 5;
    else
      a: switch (type) {
        case REACT_ACTIVITY_TYPE:
          return type = createFiberImplClass(31, pendingProps, key, mode), type.elementType = REACT_ACTIVITY_TYPE, type.lanes = lanes, type;
        case REACT_FRAGMENT_TYPE:
          return createFiberFromFragment(pendingProps.children, mode, lanes, key);
        case REACT_STRICT_MODE_TYPE:
          fiberTag = 8;
          mode |= 24;
          break;
        case REACT_PROFILER_TYPE:
          return type = createFiberImplClass(12, pendingProps, key, mode | 2), type.elementType = REACT_PROFILER_TYPE, type.lanes = lanes, type;
        case REACT_SUSPENSE_TYPE:
          return type = createFiberImplClass(13, pendingProps, key, mode), type.elementType = REACT_SUSPENSE_TYPE, type.lanes = lanes, type;
        case REACT_SUSPENSE_LIST_TYPE:
          return type = createFiberImplClass(19, pendingProps, key, mode), type.elementType = REACT_SUSPENSE_LIST_TYPE, type.lanes = lanes, type;
        default:
          if ("object" === typeof type && null !== type)
            switch (type.$$typeof) {
              case REACT_CONTEXT_TYPE:
                fiberTag = 10;
                break a;
              case REACT_CONSUMER_TYPE:
                fiberTag = 9;
                break a;
              case REACT_FORWARD_REF_TYPE:
                fiberTag = 11;
                break a;
              case REACT_MEMO_TYPE:
                fiberTag = 14;
                break a;
              case REACT_LAZY_TYPE:
                fiberTag = 16;
                owner = null;
                break a;
            }
          fiberTag = 29;
          pendingProps = Error(
            formatProdErrorMessage(130, null === type ? "null" : typeof type, "")
          );
          owner = null;
      }
    key = createFiberImplClass(fiberTag, pendingProps, key, mode);
    key.elementType = type;
    key.type = owner;
    key.lanes = lanes;
    return key;
  }
  function createFiberFromFragment(elements, mode, lanes, key) {
    elements = createFiberImplClass(7, elements, key, mode);
    elements.lanes = lanes;
    return elements;
  }
  function createFiberFromText(content, mode, lanes) {
    content = createFiberImplClass(6, content, null, mode);
    content.lanes = lanes;
    return content;
  }
  function createFiberFromDehydratedFragment(dehydratedNode) {
    var fiber = createFiberImplClass(18, null, null, 0);
    fiber.stateNode = dehydratedNode;
    return fiber;
  }
  function createFiberFromPortal(portal, mode, lanes) {
    mode = createFiberImplClass(
      4,
      null !== portal.children ? portal.children : [],
      portal.key,
      mode
    );
    mode.lanes = lanes;
    mode.stateNode = {
      containerInfo: portal.containerInfo,
      pendingChildren: null,
      implementation: portal.implementation
    };
    return mode;
  }
  var CapturedStacks = /* @__PURE__ */ new WeakMap();
  function createCapturedValueAtFiber(value, source) {
    if ("object" === typeof value && null !== value) {
      var existing = CapturedStacks.get(value);
      if (void 0 !== existing) return existing;
      source = {
        value,
        source,
        stack: getStackByFiberInDevAndProd(source)
      };
      CapturedStacks.set(value, source);
      return source;
    }
    return {
      value,
      source,
      stack: getStackByFiberInDevAndProd(source)
    };
  }
  var forkStack = [], forkStackIndex = 0, treeForkProvider = null, treeForkCount = 0, idStack = [], idStackIndex = 0, treeContextProvider = null, treeContextId = 1, treeContextOverflow = "";
  function pushTreeFork(workInProgress2, totalChildren) {
    forkStack[forkStackIndex++] = treeForkCount;
    forkStack[forkStackIndex++] = treeForkProvider;
    treeForkProvider = workInProgress2;
    treeForkCount = totalChildren;
  }
  function pushTreeId(workInProgress2, totalChildren, index2) {
    idStack[idStackIndex++] = treeContextId;
    idStack[idStackIndex++] = treeContextOverflow;
    idStack[idStackIndex++] = treeContextProvider;
    treeContextProvider = workInProgress2;
    var baseIdWithLeadingBit = treeContextId;
    workInProgress2 = treeContextOverflow;
    var baseLength = 32 - clz32(baseIdWithLeadingBit) - 1;
    baseIdWithLeadingBit &= ~(1 << baseLength);
    index2 += 1;
    var length = 32 - clz32(totalChildren) + baseLength;
    if (30 < length) {
      var numberOfOverflowBits = baseLength - baseLength % 5;
      length = (baseIdWithLeadingBit & (1 << numberOfOverflowBits) - 1).toString(32);
      baseIdWithLeadingBit >>= numberOfOverflowBits;
      baseLength -= numberOfOverflowBits;
      treeContextId = 1 << 32 - clz32(totalChildren) + baseLength | index2 << baseLength | baseIdWithLeadingBit;
      treeContextOverflow = length + workInProgress2;
    } else
      treeContextId = 1 << length | index2 << baseLength | baseIdWithLeadingBit, treeContextOverflow = workInProgress2;
  }
  function pushMaterializedTreeId(workInProgress2) {
    null !== workInProgress2.return && (pushTreeFork(workInProgress2, 1), pushTreeId(workInProgress2, 1, 0));
  }
  function popTreeContext(workInProgress2) {
    for (; workInProgress2 === treeForkProvider; )
      treeForkProvider = forkStack[--forkStackIndex], forkStack[forkStackIndex] = null, treeForkCount = forkStack[--forkStackIndex], forkStack[forkStackIndex] = null;
    for (; workInProgress2 === treeContextProvider; )
      treeContextProvider = idStack[--idStackIndex], idStack[idStackIndex] = null, treeContextOverflow = idStack[--idStackIndex], idStack[idStackIndex] = null, treeContextId = idStack[--idStackIndex], idStack[idStackIndex] = null;
  }
  function restoreSuspendedTreeContext(workInProgress2, suspendedContext) {
    idStack[idStackIndex++] = treeContextId;
    idStack[idStackIndex++] = treeContextOverflow;
    idStack[idStackIndex++] = treeContextProvider;
    treeContextId = suspendedContext.id;
    treeContextOverflow = suspendedContext.overflow;
    treeContextProvider = workInProgress2;
  }
  var hydrationParentFiber = null, nextHydratableInstance = null, isHydrating = false, hydrationErrors = null, rootOrSingletonContext = false, HydrationMismatchException = Error(formatProdErrorMessage(519));
  function throwOnHydrationMismatch(fiber) {
    var error = Error(
      formatProdErrorMessage(
        418,
        1 < arguments.length && void 0 !== arguments[1] && arguments[1] ? "text" : "HTML",
        ""
      )
    );
    queueHydrationError(createCapturedValueAtFiber(error, fiber));
    throw HydrationMismatchException;
  }
  function prepareToHydrateHostInstance(fiber) {
    var instance = fiber.stateNode, type = fiber.type, props = fiber.memoizedProps;
    instance[internalInstanceKey] = fiber;
    instance[internalPropsKey] = props;
    switch (type) {
      case "dialog":
        listenToNonDelegatedEvent("cancel", instance);
        listenToNonDelegatedEvent("close", instance);
        break;
      case "iframe":
      case "object":
      case "embed":
        listenToNonDelegatedEvent("load", instance);
        break;
      case "video":
      case "audio":
        for (type = 0; type < mediaEventTypes.length; type++)
          listenToNonDelegatedEvent(mediaEventTypes[type], instance);
        break;
      case "source":
        listenToNonDelegatedEvent("error", instance);
        break;
      case "img":
      case "image":
      case "link":
        listenToNonDelegatedEvent("error", instance);
        listenToNonDelegatedEvent("load", instance);
        break;
      case "details":
        listenToNonDelegatedEvent("toggle", instance);
        break;
      case "input":
        listenToNonDelegatedEvent("invalid", instance);
        initInput(
          instance,
          props.value,
          props.defaultValue,
          props.checked,
          props.defaultChecked,
          props.type,
          props.name,
          true
        );
        break;
      case "select":
        listenToNonDelegatedEvent("invalid", instance);
        break;
      case "textarea":
        listenToNonDelegatedEvent("invalid", instance), initTextarea(instance, props.value, props.defaultValue, props.children);
    }
    type = props.children;
    "string" !== typeof type && "number" !== typeof type && "bigint" !== typeof type || instance.textContent === "" + type || true === props.suppressHydrationWarning || checkForUnmatchedText(instance.textContent, type) ? (null != props.popover && (listenToNonDelegatedEvent("beforetoggle", instance), listenToNonDelegatedEvent("toggle", instance)), null != props.onScroll && listenToNonDelegatedEvent("scroll", instance), null != props.onScrollEnd && listenToNonDelegatedEvent("scrollend", instance), null != props.onClick && (instance.onclick = noop$1), instance = true) : instance = false;
    instance || throwOnHydrationMismatch(fiber, true);
  }
  function popToNextHostParent(fiber) {
    for (hydrationParentFiber = fiber.return; hydrationParentFiber; )
      switch (hydrationParentFiber.tag) {
        case 5:
        case 31:
        case 13:
          rootOrSingletonContext = false;
          return;
        case 27:
        case 3:
          rootOrSingletonContext = true;
          return;
        default:
          hydrationParentFiber = hydrationParentFiber.return;
      }
  }
  function popHydrationState(fiber) {
    if (fiber !== hydrationParentFiber) return false;
    if (!isHydrating) return popToNextHostParent(fiber), isHydrating = true, false;
    var tag = fiber.tag, JSCompiler_temp;
    if (JSCompiler_temp = 3 !== tag && 27 !== tag) {
      if (JSCompiler_temp = 5 === tag)
        JSCompiler_temp = fiber.type, JSCompiler_temp = !("form" !== JSCompiler_temp && "button" !== JSCompiler_temp) || shouldSetTextContent(fiber.type, fiber.memoizedProps);
      JSCompiler_temp = !JSCompiler_temp;
    }
    JSCompiler_temp && nextHydratableInstance && throwOnHydrationMismatch(fiber);
    popToNextHostParent(fiber);
    if (13 === tag) {
      fiber = fiber.memoizedState;
      fiber = null !== fiber ? fiber.dehydrated : null;
      if (!fiber) throw Error(formatProdErrorMessage(317));
      nextHydratableInstance = getNextHydratableInstanceAfterHydrationBoundary(fiber);
    } else if (31 === tag) {
      fiber = fiber.memoizedState;
      fiber = null !== fiber ? fiber.dehydrated : null;
      if (!fiber) throw Error(formatProdErrorMessage(317));
      nextHydratableInstance = getNextHydratableInstanceAfterHydrationBoundary(fiber);
    } else
      27 === tag ? (tag = nextHydratableInstance, isSingletonScope(fiber.type) ? (fiber = previousHydratableOnEnteringScopedSingleton, previousHydratableOnEnteringScopedSingleton = null, nextHydratableInstance = fiber) : nextHydratableInstance = tag) : nextHydratableInstance = hydrationParentFiber ? getNextHydratable(fiber.stateNode.nextSibling) : null;
    return true;
  }
  function resetHydrationState() {
    nextHydratableInstance = hydrationParentFiber = null;
    isHydrating = false;
  }
  function upgradeHydrationErrorsToRecoverable() {
    var queuedErrors = hydrationErrors;
    null !== queuedErrors && (null === workInProgressRootRecoverableErrors ? workInProgressRootRecoverableErrors = queuedErrors : workInProgressRootRecoverableErrors.push.apply(
      workInProgressRootRecoverableErrors,
      queuedErrors
    ), hydrationErrors = null);
    return queuedErrors;
  }
  function queueHydrationError(error) {
    null === hydrationErrors ? hydrationErrors = [error] : hydrationErrors.push(error);
  }
  var valueCursor = createCursor(null), currentlyRenderingFiber$1 = null, lastContextDependency = null;
  function pushProvider(providerFiber, context, nextValue) {
    push(valueCursor, context._currentValue);
    context._currentValue = nextValue;
  }
  function popProvider(context) {
    context._currentValue = valueCursor.current;
    pop(valueCursor);
  }
  function scheduleContextWorkOnParentPath(parent, renderLanes2, propagationRoot) {
    for (; null !== parent; ) {
      var alternate = parent.alternate;
      (parent.childLanes & renderLanes2) !== renderLanes2 ? (parent.childLanes |= renderLanes2, null !== alternate && (alternate.childLanes |= renderLanes2)) : null !== alternate && (alternate.childLanes & renderLanes2) !== renderLanes2 && (alternate.childLanes |= renderLanes2);
      if (parent === propagationRoot) break;
      parent = parent.return;
    }
  }
  function propagateContextChanges(workInProgress2, contexts, renderLanes2, forcePropagateEntireTree) {
    var fiber = workInProgress2.child;
    null !== fiber && (fiber.return = workInProgress2);
    for (; null !== fiber; ) {
      var list = fiber.dependencies;
      if (null !== list) {
        var nextFiber = fiber.child;
        list = list.firstContext;
        a: for (; null !== list; ) {
          var dependency = list;
          list = fiber;
          for (var i = 0; i < contexts.length; i++)
            if (dependency.context === contexts[i]) {
              list.lanes |= renderLanes2;
              dependency = list.alternate;
              null !== dependency && (dependency.lanes |= renderLanes2);
              scheduleContextWorkOnParentPath(
                list.return,
                renderLanes2,
                workInProgress2
              );
              forcePropagateEntireTree || (nextFiber = null);
              break a;
            }
          list = dependency.next;
        }
      } else if (18 === fiber.tag) {
        nextFiber = fiber.return;
        if (null === nextFiber) throw Error(formatProdErrorMessage(341));
        nextFiber.lanes |= renderLanes2;
        list = nextFiber.alternate;
        null !== list && (list.lanes |= renderLanes2);
        scheduleContextWorkOnParentPath(nextFiber, renderLanes2, workInProgress2);
        nextFiber = null;
      } else nextFiber = fiber.child;
      if (null !== nextFiber) nextFiber.return = fiber;
      else
        for (nextFiber = fiber; null !== nextFiber; ) {
          if (nextFiber === workInProgress2) {
            nextFiber = null;
            break;
          }
          fiber = nextFiber.sibling;
          if (null !== fiber) {
            fiber.return = nextFiber.return;
            nextFiber = fiber;
            break;
          }
          nextFiber = nextFiber.return;
        }
      fiber = nextFiber;
    }
  }
  function propagateParentContextChanges(current, workInProgress2, renderLanes2, forcePropagateEntireTree) {
    current = null;
    for (var parent = workInProgress2, isInsidePropagationBailout = false; null !== parent; ) {
      if (!isInsidePropagationBailout) {
        if (0 !== (parent.flags & 524288)) isInsidePropagationBailout = true;
        else if (0 !== (parent.flags & 262144)) break;
      }
      if (10 === parent.tag) {
        var currentParent = parent.alternate;
        if (null === currentParent) throw Error(formatProdErrorMessage(387));
        currentParent = currentParent.memoizedProps;
        if (null !== currentParent) {
          var context = parent.type;
          objectIs(parent.pendingProps.value, currentParent.value) || (null !== current ? current.push(context) : current = [context]);
        }
      } else if (parent === hostTransitionProviderCursor.current) {
        currentParent = parent.alternate;
        if (null === currentParent) throw Error(formatProdErrorMessage(387));
        currentParent.memoizedState.memoizedState !== parent.memoizedState.memoizedState && (null !== current ? current.push(HostTransitionContext) : current = [HostTransitionContext]);
      }
      parent = parent.return;
    }
    null !== current && propagateContextChanges(
      workInProgress2,
      current,
      renderLanes2,
      forcePropagateEntireTree
    );
    workInProgress2.flags |= 262144;
  }
  function checkIfContextChanged(currentDependencies) {
    for (currentDependencies = currentDependencies.firstContext; null !== currentDependencies; ) {
      if (!objectIs(
        currentDependencies.context._currentValue,
        currentDependencies.memoizedValue
      ))
        return true;
      currentDependencies = currentDependencies.next;
    }
    return false;
  }
  function prepareToReadContext(workInProgress2) {
    currentlyRenderingFiber$1 = workInProgress2;
    lastContextDependency = null;
    workInProgress2 = workInProgress2.dependencies;
    null !== workInProgress2 && (workInProgress2.firstContext = null);
  }
  function readContext(context) {
    return readContextForConsumer(currentlyRenderingFiber$1, context);
  }
  function readContextDuringReconciliation(consumer, context) {
    null === currentlyRenderingFiber$1 && prepareToReadContext(consumer);
    return readContextForConsumer(consumer, context);
  }
  function readContextForConsumer(consumer, context) {
    var value = context._currentValue;
    context = { context, memoizedValue: value, next: null };
    if (null === lastContextDependency) {
      if (null === consumer) throw Error(formatProdErrorMessage(308));
      lastContextDependency = context;
      consumer.dependencies = { lanes: 0, firstContext: context };
      consumer.flags |= 524288;
    } else lastContextDependency = lastContextDependency.next = context;
    return value;
  }
  var AbortControllerLocal = "undefined" !== typeof AbortController ? AbortController : function() {
    var listeners = [], signal = this.signal = {
      aborted: false,
      addEventListener: function(type, listener) {
        listeners.push(listener);
      }
    };
    this.abort = function() {
      signal.aborted = true;
      listeners.forEach(function(listener) {
        return listener();
      });
    };
  }, scheduleCallback$2 = Scheduler.unstable_scheduleCallback, NormalPriority = Scheduler.unstable_NormalPriority, CacheContext = {
    $$typeof: REACT_CONTEXT_TYPE,
    Consumer: null,
    Provider: null,
    _currentValue: null,
    _currentValue2: null,
    _threadCount: 0
  };
  function createCache() {
    return {
      controller: new AbortControllerLocal(),
      data: /* @__PURE__ */ new Map(),
      refCount: 0
    };
  }
  function releaseCache(cache) {
    cache.refCount--;
    0 === cache.refCount && scheduleCallback$2(NormalPriority, function() {
      cache.controller.abort();
    });
  }
  var currentEntangledListeners = null, currentEntangledPendingCount = 0, currentEntangledLane = 0, currentEntangledActionThenable = null;
  function entangleAsyncAction(transition, thenable) {
    if (null === currentEntangledListeners) {
      var entangledListeners = currentEntangledListeners = [];
      currentEntangledPendingCount = 0;
      currentEntangledLane = requestTransitionLane();
      currentEntangledActionThenable = {
        status: "pending",
        value: void 0,
        then: function(resolve) {
          entangledListeners.push(resolve);
        }
      };
    }
    currentEntangledPendingCount++;
    thenable.then(pingEngtangledActionScope, pingEngtangledActionScope);
    return thenable;
  }
  function pingEngtangledActionScope() {
    if (0 === --currentEntangledPendingCount && null !== currentEntangledListeners) {
      null !== currentEntangledActionThenable && (currentEntangledActionThenable.status = "fulfilled");
      var listeners = currentEntangledListeners;
      currentEntangledListeners = null;
      currentEntangledLane = 0;
      currentEntangledActionThenable = null;
      for (var i = 0; i < listeners.length; i++) (0, listeners[i])();
    }
  }
  function chainThenableValue(thenable, result) {
    var listeners = [], thenableWithOverride = {
      status: "pending",
      value: null,
      reason: null,
      then: function(resolve) {
        listeners.push(resolve);
      }
    };
    thenable.then(
      function() {
        thenableWithOverride.status = "fulfilled";
        thenableWithOverride.value = result;
        for (var i = 0; i < listeners.length; i++) (0, listeners[i])(result);
      },
      function(error) {
        thenableWithOverride.status = "rejected";
        thenableWithOverride.reason = error;
        for (error = 0; error < listeners.length; error++)
          (0, listeners[error])(void 0);
      }
    );
    return thenableWithOverride;
  }
  var prevOnStartTransitionFinish = ReactSharedInternals.S;
  ReactSharedInternals.S = function(transition, returnValue) {
    globalMostRecentTransitionTime = now();
    "object" === typeof returnValue && null !== returnValue && "function" === typeof returnValue.then && entangleAsyncAction(transition, returnValue);
    null !== prevOnStartTransitionFinish && prevOnStartTransitionFinish(transition, returnValue);
  };
  var resumedCache = createCursor(null);
  function peekCacheFromPool() {
    var cacheResumedFromPreviousRender = resumedCache.current;
    return null !== cacheResumedFromPreviousRender ? cacheResumedFromPreviousRender : workInProgressRoot.pooledCache;
  }
  function pushTransition(offscreenWorkInProgress, prevCachePool) {
    null === prevCachePool ? push(resumedCache, resumedCache.current) : push(resumedCache, prevCachePool.pool);
  }
  function getSuspendedCache() {
    var cacheFromPool = peekCacheFromPool();
    return null === cacheFromPool ? null : { parent: CacheContext._currentValue, pool: cacheFromPool };
  }
  var SuspenseException = Error(formatProdErrorMessage(460)), SuspenseyCommitException = Error(formatProdErrorMessage(474)), SuspenseActionException = Error(formatProdErrorMessage(542)), noopSuspenseyCommitThenable = { then: function() {
  } };
  function isThenableResolved(thenable) {
    thenable = thenable.status;
    return "fulfilled" === thenable || "rejected" === thenable;
  }
  function trackUsedThenable(thenableState2, thenable, index2) {
    index2 = thenableState2[index2];
    void 0 === index2 ? thenableState2.push(thenable) : index2 !== thenable && (thenable.then(noop$1, noop$1), thenable = index2);
    switch (thenable.status) {
      case "fulfilled":
        return thenable.value;
      case "rejected":
        throw thenableState2 = thenable.reason, checkIfUseWrappedInAsyncCatch(thenableState2), thenableState2;
      default:
        if ("string" === typeof thenable.status) thenable.then(noop$1, noop$1);
        else {
          thenableState2 = workInProgressRoot;
          if (null !== thenableState2 && 100 < thenableState2.shellSuspendCounter)
            throw Error(formatProdErrorMessage(482));
          thenableState2 = thenable;
          thenableState2.status = "pending";
          thenableState2.then(
            function(fulfilledValue) {
              if ("pending" === thenable.status) {
                var fulfilledThenable = thenable;
                fulfilledThenable.status = "fulfilled";
                fulfilledThenable.value = fulfilledValue;
              }
            },
            function(error) {
              if ("pending" === thenable.status) {
                var rejectedThenable = thenable;
                rejectedThenable.status = "rejected";
                rejectedThenable.reason = error;
              }
            }
          );
        }
        switch (thenable.status) {
          case "fulfilled":
            return thenable.value;
          case "rejected":
            throw thenableState2 = thenable.reason, checkIfUseWrappedInAsyncCatch(thenableState2), thenableState2;
        }
        suspendedThenable = thenable;
        throw SuspenseException;
    }
  }
  function resolveLazy(lazyType) {
    try {
      var init = lazyType._init;
      return init(lazyType._payload);
    } catch (x) {
      if (null !== x && "object" === typeof x && "function" === typeof x.then)
        throw suspendedThenable = x, SuspenseException;
      throw x;
    }
  }
  var suspendedThenable = null;
  function getSuspendedThenable() {
    if (null === suspendedThenable) throw Error(formatProdErrorMessage(459));
    var thenable = suspendedThenable;
    suspendedThenable = null;
    return thenable;
  }
  function checkIfUseWrappedInAsyncCatch(rejectedReason) {
    if (rejectedReason === SuspenseException || rejectedReason === SuspenseActionException)
      throw Error(formatProdErrorMessage(483));
  }
  var thenableState$1 = null, thenableIndexCounter$1 = 0;
  function unwrapThenable(thenable) {
    var index2 = thenableIndexCounter$1;
    thenableIndexCounter$1 += 1;
    null === thenableState$1 && (thenableState$1 = []);
    return trackUsedThenable(thenableState$1, thenable, index2);
  }
  function coerceRef(workInProgress2, element) {
    element = element.props.ref;
    workInProgress2.ref = void 0 !== element ? element : null;
  }
  function throwOnInvalidObjectTypeImpl(returnFiber, newChild) {
    if (newChild.$$typeof === REACT_LEGACY_ELEMENT_TYPE)
      throw Error(formatProdErrorMessage(525));
    returnFiber = Object.prototype.toString.call(newChild);
    throw Error(
      formatProdErrorMessage(
        31,
        "[object Object]" === returnFiber ? "object with keys {" + Object.keys(newChild).join(", ") + "}" : returnFiber
      )
    );
  }
  function createChildReconciler(shouldTrackSideEffects) {
    function deleteChild(returnFiber, childToDelete) {
      if (shouldTrackSideEffects) {
        var deletions = returnFiber.deletions;
        null === deletions ? (returnFiber.deletions = [childToDelete], returnFiber.flags |= 16) : deletions.push(childToDelete);
      }
    }
    function deleteRemainingChildren(returnFiber, currentFirstChild) {
      if (!shouldTrackSideEffects) return null;
      for (; null !== currentFirstChild; )
        deleteChild(returnFiber, currentFirstChild), currentFirstChild = currentFirstChild.sibling;
      return null;
    }
    function mapRemainingChildren(currentFirstChild) {
      for (var existingChildren = /* @__PURE__ */ new Map(); null !== currentFirstChild; )
        null !== currentFirstChild.key ? existingChildren.set(currentFirstChild.key, currentFirstChild) : existingChildren.set(currentFirstChild.index, currentFirstChild), currentFirstChild = currentFirstChild.sibling;
      return existingChildren;
    }
    function useFiber(fiber, pendingProps) {
      fiber = createWorkInProgress(fiber, pendingProps);
      fiber.index = 0;
      fiber.sibling = null;
      return fiber;
    }
    function placeChild(newFiber, lastPlacedIndex, newIndex) {
      newFiber.index = newIndex;
      if (!shouldTrackSideEffects)
        return newFiber.flags |= 1048576, lastPlacedIndex;
      newIndex = newFiber.alternate;
      if (null !== newIndex)
        return newIndex = newIndex.index, newIndex < lastPlacedIndex ? (newFiber.flags |= 67108866, lastPlacedIndex) : newIndex;
      newFiber.flags |= 67108866;
      return lastPlacedIndex;
    }
    function placeSingleChild(newFiber) {
      shouldTrackSideEffects && null === newFiber.alternate && (newFiber.flags |= 67108866);
      return newFiber;
    }
    function updateTextNode(returnFiber, current, textContent, lanes) {
      if (null === current || 6 !== current.tag)
        return current = createFiberFromText(textContent, returnFiber.mode, lanes), current.return = returnFiber, current;
      current = useFiber(current, textContent);
      current.return = returnFiber;
      return current;
    }
    function updateElement(returnFiber, current, element, lanes) {
      var elementType = element.type;
      if (elementType === REACT_FRAGMENT_TYPE)
        return updateFragment(
          returnFiber,
          current,
          element.props.children,
          lanes,
          element.key
        );
      if (null !== current && (current.elementType === elementType || "object" === typeof elementType && null !== elementType && elementType.$$typeof === REACT_LAZY_TYPE && resolveLazy(elementType) === current.type))
        return current = useFiber(current, element.props), coerceRef(current, element), current.return = returnFiber, current;
      current = createFiberFromTypeAndProps(
        element.type,
        element.key,
        element.props,
        null,
        returnFiber.mode,
        lanes
      );
      coerceRef(current, element);
      current.return = returnFiber;
      return current;
    }
    function updatePortal(returnFiber, current, portal, lanes) {
      if (null === current || 4 !== current.tag || current.stateNode.containerInfo !== portal.containerInfo || current.stateNode.implementation !== portal.implementation)
        return current = createFiberFromPortal(portal, returnFiber.mode, lanes), current.return = returnFiber, current;
      current = useFiber(current, portal.children || []);
      current.return = returnFiber;
      return current;
    }
    function updateFragment(returnFiber, current, fragment, lanes, key) {
      if (null === current || 7 !== current.tag)
        return current = createFiberFromFragment(
          fragment,
          returnFiber.mode,
          lanes,
          key
        ), current.return = returnFiber, current;
      current = useFiber(current, fragment);
      current.return = returnFiber;
      return current;
    }
    function createChild(returnFiber, newChild, lanes) {
      if ("string" === typeof newChild && "" !== newChild || "number" === typeof newChild || "bigint" === typeof newChild)
        return newChild = createFiberFromText(
          "" + newChild,
          returnFiber.mode,
          lanes
        ), newChild.return = returnFiber, newChild;
      if ("object" === typeof newChild && null !== newChild) {
        switch (newChild.$$typeof) {
          case REACT_ELEMENT_TYPE:
            return lanes = createFiberFromTypeAndProps(
              newChild.type,
              newChild.key,
              newChild.props,
              null,
              returnFiber.mode,
              lanes
            ), coerceRef(lanes, newChild), lanes.return = returnFiber, lanes;
          case REACT_PORTAL_TYPE:
            return newChild = createFiberFromPortal(
              newChild,
              returnFiber.mode,
              lanes
            ), newChild.return = returnFiber, newChild;
          case REACT_LAZY_TYPE:
            return newChild = resolveLazy(newChild), createChild(returnFiber, newChild, lanes);
        }
        if (isArrayImpl(newChild) || getIteratorFn(newChild))
          return newChild = createFiberFromFragment(
            newChild,
            returnFiber.mode,
            lanes,
            null
          ), newChild.return = returnFiber, newChild;
        if ("function" === typeof newChild.then)
          return createChild(returnFiber, unwrapThenable(newChild), lanes);
        if (newChild.$$typeof === REACT_CONTEXT_TYPE)
          return createChild(
            returnFiber,
            readContextDuringReconciliation(returnFiber, newChild),
            lanes
          );
        throwOnInvalidObjectTypeImpl(returnFiber, newChild);
      }
      return null;
    }
    function updateSlot(returnFiber, oldFiber, newChild, lanes) {
      var key = null !== oldFiber ? oldFiber.key : null;
      if ("string" === typeof newChild && "" !== newChild || "number" === typeof newChild || "bigint" === typeof newChild)
        return null !== key ? null : updateTextNode(returnFiber, oldFiber, "" + newChild, lanes);
      if ("object" === typeof newChild && null !== newChild) {
        switch (newChild.$$typeof) {
          case REACT_ELEMENT_TYPE:
            return newChild.key === key ? updateElement(returnFiber, oldFiber, newChild, lanes) : null;
          case REACT_PORTAL_TYPE:
            return newChild.key === key ? updatePortal(returnFiber, oldFiber, newChild, lanes) : null;
          case REACT_LAZY_TYPE:
            return newChild = resolveLazy(newChild), updateSlot(returnFiber, oldFiber, newChild, lanes);
        }
        if (isArrayImpl(newChild) || getIteratorFn(newChild))
          return null !== key ? null : updateFragment(returnFiber, oldFiber, newChild, lanes, null);
        if ("function" === typeof newChild.then)
          return updateSlot(
            returnFiber,
            oldFiber,
            unwrapThenable(newChild),
            lanes
          );
        if (newChild.$$typeof === REACT_CONTEXT_TYPE)
          return updateSlot(
            returnFiber,
            oldFiber,
            readContextDuringReconciliation(returnFiber, newChild),
            lanes
          );
        throwOnInvalidObjectTypeImpl(returnFiber, newChild);
      }
      return null;
    }
    function updateFromMap(existingChildren, returnFiber, newIdx, newChild, lanes) {
      if ("string" === typeof newChild && "" !== newChild || "number" === typeof newChild || "bigint" === typeof newChild)
        return existingChildren = existingChildren.get(newIdx) || null, updateTextNode(returnFiber, existingChildren, "" + newChild, lanes);
      if ("object" === typeof newChild && null !== newChild) {
        switch (newChild.$$typeof) {
          case REACT_ELEMENT_TYPE:
            return existingChildren = existingChildren.get(
              null === newChild.key ? newIdx : newChild.key
            ) || null, updateElement(returnFiber, existingChildren, newChild, lanes);
          case REACT_PORTAL_TYPE:
            return existingChildren = existingChildren.get(
              null === newChild.key ? newIdx : newChild.key
            ) || null, updatePortal(returnFiber, existingChildren, newChild, lanes);
          case REACT_LAZY_TYPE:
            return newChild = resolveLazy(newChild), updateFromMap(
              existingChildren,
              returnFiber,
              newIdx,
              newChild,
              lanes
            );
        }
        if (isArrayImpl(newChild) || getIteratorFn(newChild))
          return existingChildren = existingChildren.get(newIdx) || null, updateFragment(returnFiber, existingChildren, newChild, lanes, null);
        if ("function" === typeof newChild.then)
          return updateFromMap(
            existingChildren,
            returnFiber,
            newIdx,
            unwrapThenable(newChild),
            lanes
          );
        if (newChild.$$typeof === REACT_CONTEXT_TYPE)
          return updateFromMap(
            existingChildren,
            returnFiber,
            newIdx,
            readContextDuringReconciliation(returnFiber, newChild),
            lanes
          );
        throwOnInvalidObjectTypeImpl(returnFiber, newChild);
      }
      return null;
    }
    function reconcileChildrenArray(returnFiber, currentFirstChild, newChildren, lanes) {
      for (var resultingFirstChild = null, previousNewFiber = null, oldFiber = currentFirstChild, newIdx = currentFirstChild = 0, nextOldFiber = null; null !== oldFiber && newIdx < newChildren.length; newIdx++) {
        oldFiber.index > newIdx ? (nextOldFiber = oldFiber, oldFiber = null) : nextOldFiber = oldFiber.sibling;
        var newFiber = updateSlot(
          returnFiber,
          oldFiber,
          newChildren[newIdx],
          lanes
        );
        if (null === newFiber) {
          null === oldFiber && (oldFiber = nextOldFiber);
          break;
        }
        shouldTrackSideEffects && oldFiber && null === newFiber.alternate && deleteChild(returnFiber, oldFiber);
        currentFirstChild = placeChild(newFiber, currentFirstChild, newIdx);
        null === previousNewFiber ? resultingFirstChild = newFiber : previousNewFiber.sibling = newFiber;
        previousNewFiber = newFiber;
        oldFiber = nextOldFiber;
      }
      if (newIdx === newChildren.length)
        return deleteRemainingChildren(returnFiber, oldFiber), isHydrating && pushTreeFork(returnFiber, newIdx), resultingFirstChild;
      if (null === oldFiber) {
        for (; newIdx < newChildren.length; newIdx++)
          oldFiber = createChild(returnFiber, newChildren[newIdx], lanes), null !== oldFiber && (currentFirstChild = placeChild(
            oldFiber,
            currentFirstChild,
            newIdx
          ), null === previousNewFiber ? resultingFirstChild = oldFiber : previousNewFiber.sibling = oldFiber, previousNewFiber = oldFiber);
        isHydrating && pushTreeFork(returnFiber, newIdx);
        return resultingFirstChild;
      }
      for (oldFiber = mapRemainingChildren(oldFiber); newIdx < newChildren.length; newIdx++)
        nextOldFiber = updateFromMap(
          oldFiber,
          returnFiber,
          newIdx,
          newChildren[newIdx],
          lanes
        ), null !== nextOldFiber && (shouldTrackSideEffects && null !== nextOldFiber.alternate && oldFiber.delete(
          null === nextOldFiber.key ? newIdx : nextOldFiber.key
        ), currentFirstChild = placeChild(
          nextOldFiber,
          currentFirstChild,
          newIdx
        ), null === previousNewFiber ? resultingFirstChild = nextOldFiber : previousNewFiber.sibling = nextOldFiber, previousNewFiber = nextOldFiber);
      shouldTrackSideEffects && oldFiber.forEach(function(child) {
        return deleteChild(returnFiber, child);
      });
      isHydrating && pushTreeFork(returnFiber, newIdx);
      return resultingFirstChild;
    }
    function reconcileChildrenIterator(returnFiber, currentFirstChild, newChildren, lanes) {
      if (null == newChildren) throw Error(formatProdErrorMessage(151));
      for (var resultingFirstChild = null, previousNewFiber = null, oldFiber = currentFirstChild, newIdx = currentFirstChild = 0, nextOldFiber = null, step = newChildren.next(); null !== oldFiber && !step.done; newIdx++, step = newChildren.next()) {
        oldFiber.index > newIdx ? (nextOldFiber = oldFiber, oldFiber = null) : nextOldFiber = oldFiber.sibling;
        var newFiber = updateSlot(returnFiber, oldFiber, step.value, lanes);
        if (null === newFiber) {
          null === oldFiber && (oldFiber = nextOldFiber);
          break;
        }
        shouldTrackSideEffects && oldFiber && null === newFiber.alternate && deleteChild(returnFiber, oldFiber);
        currentFirstChild = placeChild(newFiber, currentFirstChild, newIdx);
        null === previousNewFiber ? resultingFirstChild = newFiber : previousNewFiber.sibling = newFiber;
        previousNewFiber = newFiber;
        oldFiber = nextOldFiber;
      }
      if (step.done)
        return deleteRemainingChildren(returnFiber, oldFiber), isHydrating && pushTreeFork(returnFiber, newIdx), resultingFirstChild;
      if (null === oldFiber) {
        for (; !step.done; newIdx++, step = newChildren.next())
          step = createChild(returnFiber, step.value, lanes), null !== step && (currentFirstChild = placeChild(step, currentFirstChild, newIdx), null === previousNewFiber ? resultingFirstChild = step : previousNewFiber.sibling = step, previousNewFiber = step);
        isHydrating && pushTreeFork(returnFiber, newIdx);
        return resultingFirstChild;
      }
      for (oldFiber = mapRemainingChildren(oldFiber); !step.done; newIdx++, step = newChildren.next())
        step = updateFromMap(oldFiber, returnFiber, newIdx, step.value, lanes), null !== step && (shouldTrackSideEffects && null !== step.alternate && oldFiber.delete(null === step.key ? newIdx : step.key), currentFirstChild = placeChild(step, currentFirstChild, newIdx), null === previousNewFiber ? resultingFirstChild = step : previousNewFiber.sibling = step, previousNewFiber = step);
      shouldTrackSideEffects && oldFiber.forEach(function(child) {
        return deleteChild(returnFiber, child);
      });
      isHydrating && pushTreeFork(returnFiber, newIdx);
      return resultingFirstChild;
    }
    function reconcileChildFibersImpl(returnFiber, currentFirstChild, newChild, lanes) {
      "object" === typeof newChild && null !== newChild && newChild.type === REACT_FRAGMENT_TYPE && null === newChild.key && (newChild = newChild.props.children);
      if ("object" === typeof newChild && null !== newChild) {
        switch (newChild.$$typeof) {
          case REACT_ELEMENT_TYPE:
            a: {
              for (var key = newChild.key; null !== currentFirstChild; ) {
                if (currentFirstChild.key === key) {
                  key = newChild.type;
                  if (key === REACT_FRAGMENT_TYPE) {
                    if (7 === currentFirstChild.tag) {
                      deleteRemainingChildren(
                        returnFiber,
                        currentFirstChild.sibling
                      );
                      lanes = useFiber(
                        currentFirstChild,
                        newChild.props.children
                      );
                      lanes.return = returnFiber;
                      returnFiber = lanes;
                      break a;
                    }
                  } else if (currentFirstChild.elementType === key || "object" === typeof key && null !== key && key.$$typeof === REACT_LAZY_TYPE && resolveLazy(key) === currentFirstChild.type) {
                    deleteRemainingChildren(
                      returnFiber,
                      currentFirstChild.sibling
                    );
                    lanes = useFiber(currentFirstChild, newChild.props);
                    coerceRef(lanes, newChild);
                    lanes.return = returnFiber;
                    returnFiber = lanes;
                    break a;
                  }
                  deleteRemainingChildren(returnFiber, currentFirstChild);
                  break;
                } else deleteChild(returnFiber, currentFirstChild);
                currentFirstChild = currentFirstChild.sibling;
              }
              newChild.type === REACT_FRAGMENT_TYPE ? (lanes = createFiberFromFragment(
                newChild.props.children,
                returnFiber.mode,
                lanes,
                newChild.key
              ), lanes.return = returnFiber, returnFiber = lanes) : (lanes = createFiberFromTypeAndProps(
                newChild.type,
                newChild.key,
                newChild.props,
                null,
                returnFiber.mode,
                lanes
              ), coerceRef(lanes, newChild), lanes.return = returnFiber, returnFiber = lanes);
            }
            return placeSingleChild(returnFiber);
          case REACT_PORTAL_TYPE:
            a: {
              for (key = newChild.key; null !== currentFirstChild; ) {
                if (currentFirstChild.key === key)
                  if (4 === currentFirstChild.tag && currentFirstChild.stateNode.containerInfo === newChild.containerInfo && currentFirstChild.stateNode.implementation === newChild.implementation) {
                    deleteRemainingChildren(
                      returnFiber,
                      currentFirstChild.sibling
                    );
                    lanes = useFiber(currentFirstChild, newChild.children || []);
                    lanes.return = returnFiber;
                    returnFiber = lanes;
                    break a;
                  } else {
                    deleteRemainingChildren(returnFiber, currentFirstChild);
                    break;
                  }
                else deleteChild(returnFiber, currentFirstChild);
                currentFirstChild = currentFirstChild.sibling;
              }
              lanes = createFiberFromPortal(newChild, returnFiber.mode, lanes);
              lanes.return = returnFiber;
              returnFiber = lanes;
            }
            return placeSingleChild(returnFiber);
          case REACT_LAZY_TYPE:
            return newChild = resolveLazy(newChild), reconcileChildFibersImpl(
              returnFiber,
              currentFirstChild,
              newChild,
              lanes
            );
        }
        if (isArrayImpl(newChild))
          return reconcileChildrenArray(
            returnFiber,
            currentFirstChild,
            newChild,
            lanes
          );
        if (getIteratorFn(newChild)) {
          key = getIteratorFn(newChild);
          if ("function" !== typeof key) throw Error(formatProdErrorMessage(150));
          newChild = key.call(newChild);
          return reconcileChildrenIterator(
            returnFiber,
            currentFirstChild,
            newChild,
            lanes
          );
        }
        if ("function" === typeof newChild.then)
          return reconcileChildFibersImpl(
            returnFiber,
            currentFirstChild,
            unwrapThenable(newChild),
            lanes
          );
        if (newChild.$$typeof === REACT_CONTEXT_TYPE)
          return reconcileChildFibersImpl(
            returnFiber,
            currentFirstChild,
            readContextDuringReconciliation(returnFiber, newChild),
            lanes
          );
        throwOnInvalidObjectTypeImpl(returnFiber, newChild);
      }
      return "string" === typeof newChild && "" !== newChild || "number" === typeof newChild || "bigint" === typeof newChild ? (newChild = "" + newChild, null !== currentFirstChild && 6 === currentFirstChild.tag ? (deleteRemainingChildren(returnFiber, currentFirstChild.sibling), lanes = useFiber(currentFirstChild, newChild), lanes.return = returnFiber, returnFiber = lanes) : (deleteRemainingChildren(returnFiber, currentFirstChild), lanes = createFiberFromText(newChild, returnFiber.mode, lanes), lanes.return = returnFiber, returnFiber = lanes), placeSingleChild(returnFiber)) : deleteRemainingChildren(returnFiber, currentFirstChild);
    }
    return function(returnFiber, currentFirstChild, newChild, lanes) {
      try {
        thenableIndexCounter$1 = 0;
        var firstChildFiber = reconcileChildFibersImpl(
          returnFiber,
          currentFirstChild,
          newChild,
          lanes
        );
        thenableState$1 = null;
        return firstChildFiber;
      } catch (x) {
        if (x === SuspenseException || x === SuspenseActionException) throw x;
        var fiber = createFiberImplClass(29, x, null, returnFiber.mode);
        fiber.lanes = lanes;
        fiber.return = returnFiber;
        return fiber;
      } finally {
      }
    };
  }
  var reconcileChildFibers = createChildReconciler(true), mountChildFibers = createChildReconciler(false), hasForceUpdate = false;
  function initializeUpdateQueue(fiber) {
    fiber.updateQueue = {
      baseState: fiber.memoizedState,
      firstBaseUpdate: null,
      lastBaseUpdate: null,
      shared: { pending: null, lanes: 0, hiddenCallbacks: null },
      callbacks: null
    };
  }
  function cloneUpdateQueue(current, workInProgress2) {
    current = current.updateQueue;
    workInProgress2.updateQueue === current && (workInProgress2.updateQueue = {
      baseState: current.baseState,
      firstBaseUpdate: current.firstBaseUpdate,
      lastBaseUpdate: current.lastBaseUpdate,
      shared: current.shared,
      callbacks: null
    });
  }
  function createUpdate(lane) {
    return { lane, tag: 0, payload: null, callback: null, next: null };
  }
  function enqueueUpdate(fiber, update, lane) {
    var updateQueue = fiber.updateQueue;
    if (null === updateQueue) return null;
    updateQueue = updateQueue.shared;
    if (0 !== (executionContext & 2)) {
      var pending = updateQueue.pending;
      null === pending ? update.next = update : (update.next = pending.next, pending.next = update);
      updateQueue.pending = update;
      update = getRootForUpdatedFiber(fiber);
      markUpdateLaneFromFiberToRoot(fiber, null, lane);
      return update;
    }
    enqueueUpdate$1(fiber, updateQueue, update, lane);
    return getRootForUpdatedFiber(fiber);
  }
  function entangleTransitions(root2, fiber, lane) {
    fiber = fiber.updateQueue;
    if (null !== fiber && (fiber = fiber.shared, 0 !== (lane & 4194048))) {
      var queueLanes = fiber.lanes;
      queueLanes &= root2.pendingLanes;
      lane |= queueLanes;
      fiber.lanes = lane;
      markRootEntangled(root2, lane);
    }
  }
  function enqueueCapturedUpdate(workInProgress2, capturedUpdate) {
    var queue = workInProgress2.updateQueue, current = workInProgress2.alternate;
    if (null !== current && (current = current.updateQueue, queue === current)) {
      var newFirst = null, newLast = null;
      queue = queue.firstBaseUpdate;
      if (null !== queue) {
        do {
          var clone = {
            lane: queue.lane,
            tag: queue.tag,
            payload: queue.payload,
            callback: null,
            next: null
          };
          null === newLast ? newFirst = newLast = clone : newLast = newLast.next = clone;
          queue = queue.next;
        } while (null !== queue);
        null === newLast ? newFirst = newLast = capturedUpdate : newLast = newLast.next = capturedUpdate;
      } else newFirst = newLast = capturedUpdate;
      queue = {
        baseState: current.baseState,
        firstBaseUpdate: newFirst,
        lastBaseUpdate: newLast,
        shared: current.shared,
        callbacks: current.callbacks
      };
      workInProgress2.updateQueue = queue;
      return;
    }
    workInProgress2 = queue.lastBaseUpdate;
    null === workInProgress2 ? queue.firstBaseUpdate = capturedUpdate : workInProgress2.next = capturedUpdate;
    queue.lastBaseUpdate = capturedUpdate;
  }
  var didReadFromEntangledAsyncAction = false;
  function suspendIfUpdateReadFromEntangledAsyncAction() {
    if (didReadFromEntangledAsyncAction) {
      var entangledActionThenable = currentEntangledActionThenable;
      if (null !== entangledActionThenable) throw entangledActionThenable;
    }
  }
  function processUpdateQueue(workInProgress$jscomp$0, props, instance$jscomp$0, renderLanes2) {
    didReadFromEntangledAsyncAction = false;
    var queue = workInProgress$jscomp$0.updateQueue;
    hasForceUpdate = false;
    var firstBaseUpdate = queue.firstBaseUpdate, lastBaseUpdate = queue.lastBaseUpdate, pendingQueue = queue.shared.pending;
    if (null !== pendingQueue) {
      queue.shared.pending = null;
      var lastPendingUpdate = pendingQueue, firstPendingUpdate = lastPendingUpdate.next;
      lastPendingUpdate.next = null;
      null === lastBaseUpdate ? firstBaseUpdate = firstPendingUpdate : lastBaseUpdate.next = firstPendingUpdate;
      lastBaseUpdate = lastPendingUpdate;
      var current = workInProgress$jscomp$0.alternate;
      null !== current && (current = current.updateQueue, pendingQueue = current.lastBaseUpdate, pendingQueue !== lastBaseUpdate && (null === pendingQueue ? current.firstBaseUpdate = firstPendingUpdate : pendingQueue.next = firstPendingUpdate, current.lastBaseUpdate = lastPendingUpdate));
    }
    if (null !== firstBaseUpdate) {
      var newState = queue.baseState;
      lastBaseUpdate = 0;
      current = firstPendingUpdate = lastPendingUpdate = null;
      pendingQueue = firstBaseUpdate;
      do {
        var updateLane = pendingQueue.lane & -536870913, isHiddenUpdate = updateLane !== pendingQueue.lane;
        if (isHiddenUpdate ? (workInProgressRootRenderLanes & updateLane) === updateLane : (renderLanes2 & updateLane) === updateLane) {
          0 !== updateLane && updateLane === currentEntangledLane && (didReadFromEntangledAsyncAction = true);
          null !== current && (current = current.next = {
            lane: 0,
            tag: pendingQueue.tag,
            payload: pendingQueue.payload,
            callback: null,
            next: null
          });
          a: {
            var workInProgress2 = workInProgress$jscomp$0, update = pendingQueue;
            updateLane = props;
            var instance = instance$jscomp$0;
            switch (update.tag) {
              case 1:
                workInProgress2 = update.payload;
                if ("function" === typeof workInProgress2) {
                  newState = workInProgress2.call(instance, newState, updateLane);
                  break a;
                }
                newState = workInProgress2;
                break a;
              case 3:
                workInProgress2.flags = workInProgress2.flags & -65537 | 128;
              case 0:
                workInProgress2 = update.payload;
                updateLane = "function" === typeof workInProgress2 ? workInProgress2.call(instance, newState, updateLane) : workInProgress2;
                if (null === updateLane || void 0 === updateLane) break a;
                newState = assign({}, newState, updateLane);
                break a;
              case 2:
                hasForceUpdate = true;
            }
          }
          updateLane = pendingQueue.callback;
          null !== updateLane && (workInProgress$jscomp$0.flags |= 64, isHiddenUpdate && (workInProgress$jscomp$0.flags |= 8192), isHiddenUpdate = queue.callbacks, null === isHiddenUpdate ? queue.callbacks = [updateLane] : isHiddenUpdate.push(updateLane));
        } else
          isHiddenUpdate = {
            lane: updateLane,
            tag: pendingQueue.tag,
            payload: pendingQueue.payload,
            callback: pendingQueue.callback,
            next: null
          }, null === current ? (firstPendingUpdate = current = isHiddenUpdate, lastPendingUpdate = newState) : current = current.next = isHiddenUpdate, lastBaseUpdate |= updateLane;
        pendingQueue = pendingQueue.next;
        if (null === pendingQueue)
          if (pendingQueue = queue.shared.pending, null === pendingQueue)
            break;
          else
            isHiddenUpdate = pendingQueue, pendingQueue = isHiddenUpdate.next, isHiddenUpdate.next = null, queue.lastBaseUpdate = isHiddenUpdate, queue.shared.pending = null;
      } while (1);
      null === current && (lastPendingUpdate = newState);
      queue.baseState = lastPendingUpdate;
      queue.firstBaseUpdate = firstPendingUpdate;
      queue.lastBaseUpdate = current;
      null === firstBaseUpdate && (queue.shared.lanes = 0);
      workInProgressRootSkippedLanes |= lastBaseUpdate;
      workInProgress$jscomp$0.lanes = lastBaseUpdate;
      workInProgress$jscomp$0.memoizedState = newState;
    }
  }
  function callCallback(callback, context) {
    if ("function" !== typeof callback)
      throw Error(formatProdErrorMessage(191, callback));
    callback.call(context);
  }
  function commitCallbacks(updateQueue, context) {
    var callbacks = updateQueue.callbacks;
    if (null !== callbacks)
      for (updateQueue.callbacks = null, updateQueue = 0; updateQueue < callbacks.length; updateQueue++)
        callCallback(callbacks[updateQueue], context);
  }
  var currentTreeHiddenStackCursor = createCursor(null), prevEntangledRenderLanesCursor = createCursor(0);
  function pushHiddenContext(fiber, context) {
    fiber = entangledRenderLanes;
    push(prevEntangledRenderLanesCursor, fiber);
    push(currentTreeHiddenStackCursor, context);
    entangledRenderLanes = fiber | context.baseLanes;
  }
  function reuseHiddenContextOnStack() {
    push(prevEntangledRenderLanesCursor, entangledRenderLanes);
    push(currentTreeHiddenStackCursor, currentTreeHiddenStackCursor.current);
  }
  function popHiddenContext() {
    entangledRenderLanes = prevEntangledRenderLanesCursor.current;
    pop(currentTreeHiddenStackCursor);
    pop(prevEntangledRenderLanesCursor);
  }
  var suspenseHandlerStackCursor = createCursor(null), shellBoundary = null;
  function pushPrimaryTreeSuspenseHandler(handler) {
    var current = handler.alternate;
    push(suspenseStackCursor, suspenseStackCursor.current & 1);
    push(suspenseHandlerStackCursor, handler);
    null === shellBoundary && (null === current || null !== currentTreeHiddenStackCursor.current ? shellBoundary = handler : null !== current.memoizedState && (shellBoundary = handler));
  }
  function pushDehydratedActivitySuspenseHandler(fiber) {
    push(suspenseStackCursor, suspenseStackCursor.current);
    push(suspenseHandlerStackCursor, fiber);
    null === shellBoundary && (shellBoundary = fiber);
  }
  function pushOffscreenSuspenseHandler(fiber) {
    22 === fiber.tag ? (push(suspenseStackCursor, suspenseStackCursor.current), push(suspenseHandlerStackCursor, fiber), null === shellBoundary && (shellBoundary = fiber)) : reuseSuspenseHandlerOnStack();
  }
  function reuseSuspenseHandlerOnStack() {
    push(suspenseStackCursor, suspenseStackCursor.current);
    push(suspenseHandlerStackCursor, suspenseHandlerStackCursor.current);
  }
  function popSuspenseHandler(fiber) {
    pop(suspenseHandlerStackCursor);
    shellBoundary === fiber && (shellBoundary = null);
    pop(suspenseStackCursor);
  }
  var suspenseStackCursor = createCursor(0);
  function findFirstSuspended(row) {
    for (var node = row; null !== node; ) {
      if (13 === node.tag) {
        var state = node.memoizedState;
        if (null !== state && (state = state.dehydrated, null === state || isSuspenseInstancePending(state) || isSuspenseInstanceFallback(state)))
          return node;
      } else if (19 === node.tag && ("forwards" === node.memoizedProps.revealOrder || "backwards" === node.memoizedProps.revealOrder || "unstable_legacy-backwards" === node.memoizedProps.revealOrder || "together" === node.memoizedProps.revealOrder)) {
        if (0 !== (node.flags & 128)) return node;
      } else if (null !== node.child) {
        node.child.return = node;
        node = node.child;
        continue;
      }
      if (node === row) break;
      for (; null === node.sibling; ) {
        if (null === node.return || node.return === row) return null;
        node = node.return;
      }
      node.sibling.return = node.return;
      node = node.sibling;
    }
    return null;
  }
  var renderLanes = 0, currentlyRenderingFiber = null, currentHook = null, workInProgressHook = null, didScheduleRenderPhaseUpdate = false, didScheduleRenderPhaseUpdateDuringThisPass = false, shouldDoubleInvokeUserFnsInHooksDEV = false, localIdCounter = 0, thenableIndexCounter = 0, thenableState = null, globalClientIdCounter = 0;
  function throwInvalidHookError() {
    throw Error(formatProdErrorMessage(321));
  }
  function areHookInputsEqual(nextDeps, prevDeps) {
    if (null === prevDeps) return false;
    for (var i = 0; i < prevDeps.length && i < nextDeps.length; i++)
      if (!objectIs(nextDeps[i], prevDeps[i])) return false;
    return true;
  }
  function renderWithHooks(current, workInProgress2, Component, props, secondArg, nextRenderLanes) {
    renderLanes = nextRenderLanes;
    currentlyRenderingFiber = workInProgress2;
    workInProgress2.memoizedState = null;
    workInProgress2.updateQueue = null;
    workInProgress2.lanes = 0;
    ReactSharedInternals.H = null === current || null === current.memoizedState ? HooksDispatcherOnMount : HooksDispatcherOnUpdate;
    shouldDoubleInvokeUserFnsInHooksDEV = false;
    nextRenderLanes = Component(props, secondArg);
    shouldDoubleInvokeUserFnsInHooksDEV = false;
    didScheduleRenderPhaseUpdateDuringThisPass && (nextRenderLanes = renderWithHooksAgain(
      workInProgress2,
      Component,
      props,
      secondArg
    ));
    finishRenderingHooks(current);
    return nextRenderLanes;
  }
  function finishRenderingHooks(current) {
    ReactSharedInternals.H = ContextOnlyDispatcher;
    var didRenderTooFewHooks = null !== currentHook && null !== currentHook.next;
    renderLanes = 0;
    workInProgressHook = currentHook = currentlyRenderingFiber = null;
    didScheduleRenderPhaseUpdate = false;
    thenableIndexCounter = 0;
    thenableState = null;
    if (didRenderTooFewHooks) throw Error(formatProdErrorMessage(300));
    null === current || didReceiveUpdate || (current = current.dependencies, null !== current && checkIfContextChanged(current) && (didReceiveUpdate = true));
  }
  function renderWithHooksAgain(workInProgress2, Component, props, secondArg) {
    currentlyRenderingFiber = workInProgress2;
    var numberOfReRenders = 0;
    do {
      didScheduleRenderPhaseUpdateDuringThisPass && (thenableState = null);
      thenableIndexCounter = 0;
      didScheduleRenderPhaseUpdateDuringThisPass = false;
      if (25 <= numberOfReRenders) throw Error(formatProdErrorMessage(301));
      numberOfReRenders += 1;
      workInProgressHook = currentHook = null;
      if (null != workInProgress2.updateQueue) {
        var children = workInProgress2.updateQueue;
        children.lastEffect = null;
        children.events = null;
        children.stores = null;
        null != children.memoCache && (children.memoCache.index = 0);
      }
      ReactSharedInternals.H = HooksDispatcherOnRerender;
      children = Component(props, secondArg);
    } while (didScheduleRenderPhaseUpdateDuringThisPass);
    return children;
  }
  function TransitionAwareHostComponent() {
    var dispatcher = ReactSharedInternals.H, maybeThenable = dispatcher.useState()[0];
    maybeThenable = "function" === typeof maybeThenable.then ? useThenable(maybeThenable) : maybeThenable;
    dispatcher = dispatcher.useState()[0];
    (null !== currentHook ? currentHook.memoizedState : null) !== dispatcher && (currentlyRenderingFiber.flags |= 1024);
    return maybeThenable;
  }
  function checkDidRenderIdHook() {
    var didRenderIdHook = 0 !== localIdCounter;
    localIdCounter = 0;
    return didRenderIdHook;
  }
  function bailoutHooks(current, workInProgress2, lanes) {
    workInProgress2.updateQueue = current.updateQueue;
    workInProgress2.flags &= -2053;
    current.lanes &= ~lanes;
  }
  function resetHooksOnUnwind(workInProgress2) {
    if (didScheduleRenderPhaseUpdate) {
      for (workInProgress2 = workInProgress2.memoizedState; null !== workInProgress2; ) {
        var queue = workInProgress2.queue;
        null !== queue && (queue.pending = null);
        workInProgress2 = workInProgress2.next;
      }
      didScheduleRenderPhaseUpdate = false;
    }
    renderLanes = 0;
    workInProgressHook = currentHook = currentlyRenderingFiber = null;
    didScheduleRenderPhaseUpdateDuringThisPass = false;
    thenableIndexCounter = localIdCounter = 0;
    thenableState = null;
  }
  function mountWorkInProgressHook() {
    var hook = {
      memoizedState: null,
      baseState: null,
      baseQueue: null,
      queue: null,
      next: null
    };
    null === workInProgressHook ? currentlyRenderingFiber.memoizedState = workInProgressHook = hook : workInProgressHook = workInProgressHook.next = hook;
    return workInProgressHook;
  }
  function updateWorkInProgressHook() {
    if (null === currentHook) {
      var nextCurrentHook = currentlyRenderingFiber.alternate;
      nextCurrentHook = null !== nextCurrentHook ? nextCurrentHook.memoizedState : null;
    } else nextCurrentHook = currentHook.next;
    var nextWorkInProgressHook = null === workInProgressHook ? currentlyRenderingFiber.memoizedState : workInProgressHook.next;
    if (null !== nextWorkInProgressHook)
      workInProgressHook = nextWorkInProgressHook, currentHook = nextCurrentHook;
    else {
      if (null === nextCurrentHook) {
        if (null === currentlyRenderingFiber.alternate)
          throw Error(formatProdErrorMessage(467));
        throw Error(formatProdErrorMessage(310));
      }
      currentHook = nextCurrentHook;
      nextCurrentHook = {
        memoizedState: currentHook.memoizedState,
        baseState: currentHook.baseState,
        baseQueue: currentHook.baseQueue,
        queue: currentHook.queue,
        next: null
      };
      null === workInProgressHook ? currentlyRenderingFiber.memoizedState = workInProgressHook = nextCurrentHook : workInProgressHook = workInProgressHook.next = nextCurrentHook;
    }
    return workInProgressHook;
  }
  function createFunctionComponentUpdateQueue() {
    return { lastEffect: null, events: null, stores: null, memoCache: null };
  }
  function useThenable(thenable) {
    var index2 = thenableIndexCounter;
    thenableIndexCounter += 1;
    null === thenableState && (thenableState = []);
    thenable = trackUsedThenable(thenableState, thenable, index2);
    index2 = currentlyRenderingFiber;
    null === (null === workInProgressHook ? index2.memoizedState : workInProgressHook.next) && (index2 = index2.alternate, ReactSharedInternals.H = null === index2 || null === index2.memoizedState ? HooksDispatcherOnMount : HooksDispatcherOnUpdate);
    return thenable;
  }
  function use(usable) {
    if (null !== usable && "object" === typeof usable) {
      if ("function" === typeof usable.then) return useThenable(usable);
      if (usable.$$typeof === REACT_CONTEXT_TYPE) return readContext(usable);
    }
    throw Error(formatProdErrorMessage(438, String(usable)));
  }
  function useMemoCache(size) {
    var memoCache = null, updateQueue = currentlyRenderingFiber.updateQueue;
    null !== updateQueue && (memoCache = updateQueue.memoCache);
    if (null == memoCache) {
      var current = currentlyRenderingFiber.alternate;
      null !== current && (current = current.updateQueue, null !== current && (current = current.memoCache, null != current && (memoCache = {
        data: current.data.map(function(array) {
          return array.slice();
        }),
        index: 0
      })));
    }
    null == memoCache && (memoCache = { data: [], index: 0 });
    null === updateQueue && (updateQueue = createFunctionComponentUpdateQueue(), currentlyRenderingFiber.updateQueue = updateQueue);
    updateQueue.memoCache = memoCache;
    updateQueue = memoCache.data[memoCache.index];
    if (void 0 === updateQueue)
      for (updateQueue = memoCache.data[memoCache.index] = Array(size), current = 0; current < size; current++)
        updateQueue[current] = REACT_MEMO_CACHE_SENTINEL;
    memoCache.index++;
    return updateQueue;
  }
  function basicStateReducer(state, action) {
    return "function" === typeof action ? action(state) : action;
  }
  function updateReducer(reducer) {
    var hook = updateWorkInProgressHook();
    return updateReducerImpl(hook, currentHook, reducer);
  }
  function updateReducerImpl(hook, current, reducer) {
    var queue = hook.queue;
    if (null === queue) throw Error(formatProdErrorMessage(311));
    queue.lastRenderedReducer = reducer;
    var baseQueue = hook.baseQueue, pendingQueue = queue.pending;
    if (null !== pendingQueue) {
      if (null !== baseQueue) {
        var baseFirst = baseQueue.next;
        baseQueue.next = pendingQueue.next;
        pendingQueue.next = baseFirst;
      }
      current.baseQueue = baseQueue = pendingQueue;
      queue.pending = null;
    }
    pendingQueue = hook.baseState;
    if (null === baseQueue) hook.memoizedState = pendingQueue;
    else {
      current = baseQueue.next;
      var newBaseQueueFirst = baseFirst = null, newBaseQueueLast = null, update = current, didReadFromEntangledAsyncAction$60 = false;
      do {
        var updateLane = update.lane & -536870913;
        if (updateLane !== update.lane ? (workInProgressRootRenderLanes & updateLane) === updateLane : (renderLanes & updateLane) === updateLane) {
          var revertLane = update.revertLane;
          if (0 === revertLane)
            null !== newBaseQueueLast && (newBaseQueueLast = newBaseQueueLast.next = {
              lane: 0,
              revertLane: 0,
              gesture: null,
              action: update.action,
              hasEagerState: update.hasEagerState,
              eagerState: update.eagerState,
              next: null
            }), updateLane === currentEntangledLane && (didReadFromEntangledAsyncAction$60 = true);
          else if ((renderLanes & revertLane) === revertLane) {
            update = update.next;
            revertLane === currentEntangledLane && (didReadFromEntangledAsyncAction$60 = true);
            continue;
          } else
            updateLane = {
              lane: 0,
              revertLane: update.revertLane,
              gesture: null,
              action: update.action,
              hasEagerState: update.hasEagerState,
              eagerState: update.eagerState,
              next: null
            }, null === newBaseQueueLast ? (newBaseQueueFirst = newBaseQueueLast = updateLane, baseFirst = pendingQueue) : newBaseQueueLast = newBaseQueueLast.next = updateLane, currentlyRenderingFiber.lanes |= revertLane, workInProgressRootSkippedLanes |= revertLane;
          updateLane = update.action;
          shouldDoubleInvokeUserFnsInHooksDEV && reducer(pendingQueue, updateLane);
          pendingQueue = update.hasEagerState ? update.eagerState : reducer(pendingQueue, updateLane);
        } else
          revertLane = {
            lane: updateLane,
            revertLane: update.revertLane,
            gesture: update.gesture,
            action: update.action,
            hasEagerState: update.hasEagerState,
            eagerState: update.eagerState,
            next: null
          }, null === newBaseQueueLast ? (newBaseQueueFirst = newBaseQueueLast = revertLane, baseFirst = pendingQueue) : newBaseQueueLast = newBaseQueueLast.next = revertLane, currentlyRenderingFiber.lanes |= updateLane, workInProgressRootSkippedLanes |= updateLane;
        update = update.next;
      } while (null !== update && update !== current);
      null === newBaseQueueLast ? baseFirst = pendingQueue : newBaseQueueLast.next = newBaseQueueFirst;
      if (!objectIs(pendingQueue, hook.memoizedState) && (didReceiveUpdate = true, didReadFromEntangledAsyncAction$60 && (reducer = currentEntangledActionThenable, null !== reducer)))
        throw reducer;
      hook.memoizedState = pendingQueue;
      hook.baseState = baseFirst;
      hook.baseQueue = newBaseQueueLast;
      queue.lastRenderedState = pendingQueue;
    }
    null === baseQueue && (queue.lanes = 0);
    return [hook.memoizedState, queue.dispatch];
  }
  function rerenderReducer(reducer) {
    var hook = updateWorkInProgressHook(), queue = hook.queue;
    if (null === queue) throw Error(formatProdErrorMessage(311));
    queue.lastRenderedReducer = reducer;
    var dispatch = queue.dispatch, lastRenderPhaseUpdate = queue.pending, newState = hook.memoizedState;
    if (null !== lastRenderPhaseUpdate) {
      queue.pending = null;
      var update = lastRenderPhaseUpdate = lastRenderPhaseUpdate.next;
      do
        newState = reducer(newState, update.action), update = update.next;
      while (update !== lastRenderPhaseUpdate);
      objectIs(newState, hook.memoizedState) || (didReceiveUpdate = true);
      hook.memoizedState = newState;
      null === hook.baseQueue && (hook.baseState = newState);
      queue.lastRenderedState = newState;
    }
    return [newState, dispatch];
  }
  function updateSyncExternalStore(subscribe, getSnapshot, getServerSnapshot) {
    var fiber = currentlyRenderingFiber, hook = updateWorkInProgressHook(), isHydrating$jscomp$0 = isHydrating;
    if (isHydrating$jscomp$0) {
      if (void 0 === getServerSnapshot) throw Error(formatProdErrorMessage(407));
      getServerSnapshot = getServerSnapshot();
    } else getServerSnapshot = getSnapshot();
    var snapshotChanged = !objectIs(
      (currentHook || hook).memoizedState,
      getServerSnapshot
    );
    snapshotChanged && (hook.memoizedState = getServerSnapshot, didReceiveUpdate = true);
    hook = hook.queue;
    updateEffect(subscribeToStore.bind(null, fiber, hook, subscribe), [
      subscribe
    ]);
    if (hook.getSnapshot !== getSnapshot || snapshotChanged || null !== workInProgressHook && workInProgressHook.memoizedState.tag & 1) {
      fiber.flags |= 2048;
      pushSimpleEffect(
        9,
        { destroy: void 0 },
        updateStoreInstance.bind(
          null,
          fiber,
          hook,
          getServerSnapshot,
          getSnapshot
        ),
        null
      );
      if (null === workInProgressRoot) throw Error(formatProdErrorMessage(349));
      isHydrating$jscomp$0 || 0 !== (renderLanes & 127) || pushStoreConsistencyCheck(fiber, getSnapshot, getServerSnapshot);
    }
    return getServerSnapshot;
  }
  function pushStoreConsistencyCheck(fiber, getSnapshot, renderedSnapshot) {
    fiber.flags |= 16384;
    fiber = { getSnapshot, value: renderedSnapshot };
    getSnapshot = currentlyRenderingFiber.updateQueue;
    null === getSnapshot ? (getSnapshot = createFunctionComponentUpdateQueue(), currentlyRenderingFiber.updateQueue = getSnapshot, getSnapshot.stores = [fiber]) : (renderedSnapshot = getSnapshot.stores, null === renderedSnapshot ? getSnapshot.stores = [fiber] : renderedSnapshot.push(fiber));
  }
  function updateStoreInstance(fiber, inst, nextSnapshot, getSnapshot) {
    inst.value = nextSnapshot;
    inst.getSnapshot = getSnapshot;
    checkIfSnapshotChanged(inst) && forceStoreRerender(fiber);
  }
  function subscribeToStore(fiber, inst, subscribe) {
    return subscribe(function() {
      checkIfSnapshotChanged(inst) && forceStoreRerender(fiber);
    });
  }
  function checkIfSnapshotChanged(inst) {
    var latestGetSnapshot = inst.getSnapshot;
    inst = inst.value;
    try {
      var nextValue = latestGetSnapshot();
      return !objectIs(inst, nextValue);
    } catch (error) {
      return true;
    }
  }
  function forceStoreRerender(fiber) {
    var root2 = enqueueConcurrentRenderForLane(fiber, 2);
    null !== root2 && scheduleUpdateOnFiber(root2, fiber, 2);
  }
  function mountStateImpl(initialState) {
    var hook = mountWorkInProgressHook();
    if ("function" === typeof initialState) {
      var initialStateInitializer = initialState;
      initialState = initialStateInitializer();
      if (shouldDoubleInvokeUserFnsInHooksDEV) {
        setIsStrictModeForDevtools(true);
        try {
          initialStateInitializer();
        } finally {
          setIsStrictModeForDevtools(false);
        }
      }
    }
    hook.memoizedState = hook.baseState = initialState;
    hook.queue = {
      pending: null,
      lanes: 0,
      dispatch: null,
      lastRenderedReducer: basicStateReducer,
      lastRenderedState: initialState
    };
    return hook;
  }
  function updateOptimisticImpl(hook, current, passthrough, reducer) {
    hook.baseState = passthrough;
    return updateReducerImpl(
      hook,
      currentHook,
      "function" === typeof reducer ? reducer : basicStateReducer
    );
  }
  function dispatchActionState(fiber, actionQueue, setPendingState, setState, payload) {
    if (isRenderPhaseUpdate(fiber)) throw Error(formatProdErrorMessage(485));
    fiber = actionQueue.action;
    if (null !== fiber) {
      var actionNode = {
        payload,
        action: fiber,
        next: null,
        isTransition: true,
        status: "pending",
        value: null,
        reason: null,
        listeners: [],
        then: function(listener) {
          actionNode.listeners.push(listener);
        }
      };
      null !== ReactSharedInternals.T ? setPendingState(true) : actionNode.isTransition = false;
      setState(actionNode);
      setPendingState = actionQueue.pending;
      null === setPendingState ? (actionNode.next = actionQueue.pending = actionNode, runActionStateAction(actionQueue, actionNode)) : (actionNode.next = setPendingState.next, actionQueue.pending = setPendingState.next = actionNode);
    }
  }
  function runActionStateAction(actionQueue, node) {
    var action = node.action, payload = node.payload, prevState = actionQueue.state;
    if (node.isTransition) {
      var prevTransition = ReactSharedInternals.T, currentTransition = {};
      ReactSharedInternals.T = currentTransition;
      try {
        var returnValue = action(prevState, payload), onStartTransitionFinish = ReactSharedInternals.S;
        null !== onStartTransitionFinish && onStartTransitionFinish(currentTransition, returnValue);
        handleActionReturnValue(actionQueue, node, returnValue);
      } catch (error) {
        onActionError(actionQueue, node, error);
      } finally {
        null !== prevTransition && null !== currentTransition.types && (prevTransition.types = currentTransition.types), ReactSharedInternals.T = prevTransition;
      }
    } else
      try {
        prevTransition = action(prevState, payload), handleActionReturnValue(actionQueue, node, prevTransition);
      } catch (error$66) {
        onActionError(actionQueue, node, error$66);
      }
  }
  function handleActionReturnValue(actionQueue, node, returnValue) {
    null !== returnValue && "object" === typeof returnValue && "function" === typeof returnValue.then ? returnValue.then(
      function(nextState) {
        onActionSuccess(actionQueue, node, nextState);
      },
      function(error) {
        return onActionError(actionQueue, node, error);
      }
    ) : onActionSuccess(actionQueue, node, returnValue);
  }
  function onActionSuccess(actionQueue, actionNode, nextState) {
    actionNode.status = "fulfilled";
    actionNode.value = nextState;
    notifyActionListeners(actionNode);
    actionQueue.state = nextState;
    actionNode = actionQueue.pending;
    null !== actionNode && (nextState = actionNode.next, nextState === actionNode ? actionQueue.pending = null : (nextState = nextState.next, actionNode.next = nextState, runActionStateAction(actionQueue, nextState)));
  }
  function onActionError(actionQueue, actionNode, error) {
    var last = actionQueue.pending;
    actionQueue.pending = null;
    if (null !== last) {
      last = last.next;
      do
        actionNode.status = "rejected", actionNode.reason = error, notifyActionListeners(actionNode), actionNode = actionNode.next;
      while (actionNode !== last);
    }
    actionQueue.action = null;
  }
  function notifyActionListeners(actionNode) {
    actionNode = actionNode.listeners;
    for (var i = 0; i < actionNode.length; i++) (0, actionNode[i])();
  }
  function actionStateReducer(oldState, newState) {
    return newState;
  }
  function mountActionState(action, initialStateProp) {
    if (isHydrating) {
      var ssrFormState = workInProgressRoot.formState;
      if (null !== ssrFormState) {
        a: {
          var JSCompiler_inline_result = currentlyRenderingFiber;
          if (isHydrating) {
            if (nextHydratableInstance) {
              b: {
                var JSCompiler_inline_result$jscomp$0 = nextHydratableInstance;
                for (var inRootOrSingleton = rootOrSingletonContext; 8 !== JSCompiler_inline_result$jscomp$0.nodeType; ) {
                  if (!inRootOrSingleton) {
                    JSCompiler_inline_result$jscomp$0 = null;
                    break b;
                  }
                  JSCompiler_inline_result$jscomp$0 = getNextHydratable(
                    JSCompiler_inline_result$jscomp$0.nextSibling
                  );
                  if (null === JSCompiler_inline_result$jscomp$0) {
                    JSCompiler_inline_result$jscomp$0 = null;
                    break b;
                  }
                }
                inRootOrSingleton = JSCompiler_inline_result$jscomp$0.data;
                JSCompiler_inline_result$jscomp$0 = "F!" === inRootOrSingleton || "F" === inRootOrSingleton ? JSCompiler_inline_result$jscomp$0 : null;
              }
              if (JSCompiler_inline_result$jscomp$0) {
                nextHydratableInstance = getNextHydratable(
                  JSCompiler_inline_result$jscomp$0.nextSibling
                );
                JSCompiler_inline_result = "F!" === JSCompiler_inline_result$jscomp$0.data;
                break a;
              }
            }
            throwOnHydrationMismatch(JSCompiler_inline_result);
          }
          JSCompiler_inline_result = false;
        }
        JSCompiler_inline_result && (initialStateProp = ssrFormState[0]);
      }
    }
    ssrFormState = mountWorkInProgressHook();
    ssrFormState.memoizedState = ssrFormState.baseState = initialStateProp;
    JSCompiler_inline_result = {
      pending: null,
      lanes: 0,
      dispatch: null,
      lastRenderedReducer: actionStateReducer,
      lastRenderedState: initialStateProp
    };
    ssrFormState.queue = JSCompiler_inline_result;
    ssrFormState = dispatchSetState.bind(
      null,
      currentlyRenderingFiber,
      JSCompiler_inline_result
    );
    JSCompiler_inline_result.dispatch = ssrFormState;
    JSCompiler_inline_result = mountStateImpl(false);
    inRootOrSingleton = dispatchOptimisticSetState.bind(
      null,
      currentlyRenderingFiber,
      false,
      JSCompiler_inline_result.queue
    );
    JSCompiler_inline_result = mountWorkInProgressHook();
    JSCompiler_inline_result$jscomp$0 = {
      state: initialStateProp,
      dispatch: null,
      action,
      pending: null
    };
    JSCompiler_inline_result.queue = JSCompiler_inline_result$jscomp$0;
    ssrFormState = dispatchActionState.bind(
      null,
      currentlyRenderingFiber,
      JSCompiler_inline_result$jscomp$0,
      inRootOrSingleton,
      ssrFormState
    );
    JSCompiler_inline_result$jscomp$0.dispatch = ssrFormState;
    JSCompiler_inline_result.memoizedState = action;
    return [initialStateProp, ssrFormState, false];
  }
  function updateActionState(action) {
    var stateHook = updateWorkInProgressHook();
    return updateActionStateImpl(stateHook, currentHook, action);
  }
  function updateActionStateImpl(stateHook, currentStateHook, action) {
    currentStateHook = updateReducerImpl(
      stateHook,
      currentStateHook,
      actionStateReducer
    )[0];
    stateHook = updateReducer(basicStateReducer)[0];
    if ("object" === typeof currentStateHook && null !== currentStateHook && "function" === typeof currentStateHook.then)
      try {
        var state = useThenable(currentStateHook);
      } catch (x) {
        if (x === SuspenseException) throw SuspenseActionException;
        throw x;
      }
    else state = currentStateHook;
    currentStateHook = updateWorkInProgressHook();
    var actionQueue = currentStateHook.queue, dispatch = actionQueue.dispatch;
    action !== currentStateHook.memoizedState && (currentlyRenderingFiber.flags |= 2048, pushSimpleEffect(
      9,
      { destroy: void 0 },
      actionStateActionEffect.bind(null, actionQueue, action),
      null
    ));
    return [state, dispatch, stateHook];
  }
  function actionStateActionEffect(actionQueue, action) {
    actionQueue.action = action;
  }
  function rerenderActionState(action) {
    var stateHook = updateWorkInProgressHook(), currentStateHook = currentHook;
    if (null !== currentStateHook)
      return updateActionStateImpl(stateHook, currentStateHook, action);
    updateWorkInProgressHook();
    stateHook = stateHook.memoizedState;
    currentStateHook = updateWorkInProgressHook();
    var dispatch = currentStateHook.queue.dispatch;
    currentStateHook.memoizedState = action;
    return [stateHook, dispatch, false];
  }
  function pushSimpleEffect(tag, inst, create, deps) {
    tag = { tag, create, deps, inst, next: null };
    inst = currentlyRenderingFiber.updateQueue;
    null === inst && (inst = createFunctionComponentUpdateQueue(), currentlyRenderingFiber.updateQueue = inst);
    create = inst.lastEffect;
    null === create ? inst.lastEffect = tag.next = tag : (deps = create.next, create.next = tag, tag.next = deps, inst.lastEffect = tag);
    return tag;
  }
  function updateRef() {
    return updateWorkInProgressHook().memoizedState;
  }
  function mountEffectImpl(fiberFlags, hookFlags, create, deps) {
    var hook = mountWorkInProgressHook();
    currentlyRenderingFiber.flags |= fiberFlags;
    hook.memoizedState = pushSimpleEffect(
      1 | hookFlags,
      { destroy: void 0 },
      create,
      void 0 === deps ? null : deps
    );
  }
  function updateEffectImpl(fiberFlags, hookFlags, create, deps) {
    var hook = updateWorkInProgressHook();
    deps = void 0 === deps ? null : deps;
    var inst = hook.memoizedState.inst;
    null !== currentHook && null !== deps && areHookInputsEqual(deps, currentHook.memoizedState.deps) ? hook.memoizedState = pushSimpleEffect(hookFlags, inst, create, deps) : (currentlyRenderingFiber.flags |= fiberFlags, hook.memoizedState = pushSimpleEffect(
      1 | hookFlags,
      inst,
      create,
      deps
    ));
  }
  function mountEffect(create, deps) {
    mountEffectImpl(8390656, 8, create, deps);
  }
  function updateEffect(create, deps) {
    updateEffectImpl(2048, 8, create, deps);
  }
  function useEffectEventImpl(payload) {
    currentlyRenderingFiber.flags |= 4;
    var componentUpdateQueue = currentlyRenderingFiber.updateQueue;
    if (null === componentUpdateQueue)
      componentUpdateQueue = createFunctionComponentUpdateQueue(), currentlyRenderingFiber.updateQueue = componentUpdateQueue, componentUpdateQueue.events = [payload];
    else {
      var events = componentUpdateQueue.events;
      null === events ? componentUpdateQueue.events = [payload] : events.push(payload);
    }
  }
  function updateEvent(callback) {
    var ref = updateWorkInProgressHook().memoizedState;
    useEffectEventImpl({ ref, nextImpl: callback });
    return function() {
      if (0 !== (executionContext & 2)) throw Error(formatProdErrorMessage(440));
      return ref.impl.apply(void 0, arguments);
    };
  }
  function updateInsertionEffect(create, deps) {
    return updateEffectImpl(4, 2, create, deps);
  }
  function updateLayoutEffect(create, deps) {
    return updateEffectImpl(4, 4, create, deps);
  }
  function imperativeHandleEffect(create, ref) {
    if ("function" === typeof ref) {
      create = create();
      var refCleanup = ref(create);
      return function() {
        "function" === typeof refCleanup ? refCleanup() : ref(null);
      };
    }
    if (null !== ref && void 0 !== ref)
      return create = create(), ref.current = create, function() {
        ref.current = null;
      };
  }
  function updateImperativeHandle(ref, create, deps) {
    deps = null !== deps && void 0 !== deps ? deps.concat([ref]) : null;
    updateEffectImpl(4, 4, imperativeHandleEffect.bind(null, create, ref), deps);
  }
  function mountDebugValue() {
  }
  function updateCallback(callback, deps) {
    var hook = updateWorkInProgressHook();
    deps = void 0 === deps ? null : deps;
    var prevState = hook.memoizedState;
    if (null !== deps && areHookInputsEqual(deps, prevState[1]))
      return prevState[0];
    hook.memoizedState = [callback, deps];
    return callback;
  }
  function updateMemo(nextCreate, deps) {
    var hook = updateWorkInProgressHook();
    deps = void 0 === deps ? null : deps;
    var prevState = hook.memoizedState;
    if (null !== deps && areHookInputsEqual(deps, prevState[1]))
      return prevState[0];
    prevState = nextCreate();
    if (shouldDoubleInvokeUserFnsInHooksDEV) {
      setIsStrictModeForDevtools(true);
      try {
        nextCreate();
      } finally {
        setIsStrictModeForDevtools(false);
      }
    }
    hook.memoizedState = [prevState, deps];
    return prevState;
  }
  function mountDeferredValueImpl(hook, value, initialValue) {
    if (void 0 === initialValue || 0 !== (renderLanes & 1073741824) && 0 === (workInProgressRootRenderLanes & 261930))
      return hook.memoizedState = value;
    hook.memoizedState = initialValue;
    hook = requestDeferredLane();
    currentlyRenderingFiber.lanes |= hook;
    workInProgressRootSkippedLanes |= hook;
    return initialValue;
  }
  function updateDeferredValueImpl(hook, prevValue, value, initialValue) {
    if (objectIs(value, prevValue)) return value;
    if (null !== currentTreeHiddenStackCursor.current)
      return hook = mountDeferredValueImpl(hook, value, initialValue), objectIs(hook, prevValue) || (didReceiveUpdate = true), hook;
    if (0 === (renderLanes & 42) || 0 !== (renderLanes & 1073741824) && 0 === (workInProgressRootRenderLanes & 261930))
      return didReceiveUpdate = true, hook.memoizedState = value;
    hook = requestDeferredLane();
    currentlyRenderingFiber.lanes |= hook;
    workInProgressRootSkippedLanes |= hook;
    return prevValue;
  }
  function startTransition(fiber, queue, pendingState, finishedState, callback) {
    var previousPriority = ReactDOMSharedInternals.p;
    ReactDOMSharedInternals.p = 0 !== previousPriority && 8 > previousPriority ? previousPriority : 8;
    var prevTransition = ReactSharedInternals.T, currentTransition = {};
    ReactSharedInternals.T = currentTransition;
    dispatchOptimisticSetState(fiber, false, queue, pendingState);
    try {
      var returnValue = callback(), onStartTransitionFinish = ReactSharedInternals.S;
      null !== onStartTransitionFinish && onStartTransitionFinish(currentTransition, returnValue);
      if (null !== returnValue && "object" === typeof returnValue && "function" === typeof returnValue.then) {
        var thenableForFinishedState = chainThenableValue(
          returnValue,
          finishedState
        );
        dispatchSetStateInternal(
          fiber,
          queue,
          thenableForFinishedState,
          requestUpdateLane(fiber)
        );
      } else
        dispatchSetStateInternal(
          fiber,
          queue,
          finishedState,
          requestUpdateLane(fiber)
        );
    } catch (error) {
      dispatchSetStateInternal(
        fiber,
        queue,
        { then: function() {
        }, status: "rejected", reason: error },
        requestUpdateLane()
      );
    } finally {
      ReactDOMSharedInternals.p = previousPriority, null !== prevTransition && null !== currentTransition.types && (prevTransition.types = currentTransition.types), ReactSharedInternals.T = prevTransition;
    }
  }
  function noop() {
  }
  function startHostTransition(formFiber, pendingState, action, formData) {
    if (5 !== formFiber.tag) throw Error(formatProdErrorMessage(476));
    var queue = ensureFormComponentIsStateful(formFiber).queue;
    startTransition(
      formFiber,
      queue,
      pendingState,
      sharedNotPendingObject,
      null === action ? noop : function() {
        requestFormReset$1(formFiber);
        return action(formData);
      }
    );
  }
  function ensureFormComponentIsStateful(formFiber) {
    var existingStateHook = formFiber.memoizedState;
    if (null !== existingStateHook) return existingStateHook;
    existingStateHook = {
      memoizedState: sharedNotPendingObject,
      baseState: sharedNotPendingObject,
      baseQueue: null,
      queue: {
        pending: null,
        lanes: 0,
        dispatch: null,
        lastRenderedReducer: basicStateReducer,
        lastRenderedState: sharedNotPendingObject
      },
      next: null
    };
    var initialResetState = {};
    existingStateHook.next = {
      memoizedState: initialResetState,
      baseState: initialResetState,
      baseQueue: null,
      queue: {
        pending: null,
        lanes: 0,
        dispatch: null,
        lastRenderedReducer: basicStateReducer,
        lastRenderedState: initialResetState
      },
      next: null
    };
    formFiber.memoizedState = existingStateHook;
    formFiber = formFiber.alternate;
    null !== formFiber && (formFiber.memoizedState = existingStateHook);
    return existingStateHook;
  }
  function requestFormReset$1(formFiber) {
    var stateHook = ensureFormComponentIsStateful(formFiber);
    null === stateHook.next && (stateHook = formFiber.alternate.memoizedState);
    dispatchSetStateInternal(
      formFiber,
      stateHook.next.queue,
      {},
      requestUpdateLane()
    );
  }
  function useHostTransitionStatus() {
    return readContext(HostTransitionContext);
  }
  function updateId() {
    return updateWorkInProgressHook().memoizedState;
  }
  function updateRefresh() {
    return updateWorkInProgressHook().memoizedState;
  }
  function refreshCache(fiber) {
    for (var provider = fiber.return; null !== provider; ) {
      switch (provider.tag) {
        case 24:
        case 3:
          var lane = requestUpdateLane();
          fiber = createUpdate(lane);
          var root$69 = enqueueUpdate(provider, fiber, lane);
          null !== root$69 && (scheduleUpdateOnFiber(root$69, provider, lane), entangleTransitions(root$69, provider, lane));
          provider = { cache: createCache() };
          fiber.payload = provider;
          return;
      }
      provider = provider.return;
    }
  }
  function dispatchReducerAction(fiber, queue, action) {
    var lane = requestUpdateLane();
    action = {
      lane,
      revertLane: 0,
      gesture: null,
      action,
      hasEagerState: false,
      eagerState: null,
      next: null
    };
    isRenderPhaseUpdate(fiber) ? enqueueRenderPhaseUpdate(queue, action) : (action = enqueueConcurrentHookUpdate(fiber, queue, action, lane), null !== action && (scheduleUpdateOnFiber(action, fiber, lane), entangleTransitionUpdate(action, queue, lane)));
  }
  function dispatchSetState(fiber, queue, action) {
    var lane = requestUpdateLane();
    dispatchSetStateInternal(fiber, queue, action, lane);
  }
  function dispatchSetStateInternal(fiber, queue, action, lane) {
    var update = {
      lane,
      revertLane: 0,
      gesture: null,
      action,
      hasEagerState: false,
      eagerState: null,
      next: null
    };
    if (isRenderPhaseUpdate(fiber)) enqueueRenderPhaseUpdate(queue, update);
    else {
      var alternate = fiber.alternate;
      if (0 === fiber.lanes && (null === alternate || 0 === alternate.lanes) && (alternate = queue.lastRenderedReducer, null !== alternate))
        try {
          var currentState = queue.lastRenderedState, eagerState = alternate(currentState, action);
          update.hasEagerState = true;
          update.eagerState = eagerState;
          if (objectIs(eagerState, currentState))
            return enqueueUpdate$1(fiber, queue, update, 0), null === workInProgressRoot && finishQueueingConcurrentUpdates(), false;
        } catch (error) {
        } finally {
        }
      action = enqueueConcurrentHookUpdate(fiber, queue, update, lane);
      if (null !== action)
        return scheduleUpdateOnFiber(action, fiber, lane), entangleTransitionUpdate(action, queue, lane), true;
    }
    return false;
  }
  function dispatchOptimisticSetState(fiber, throwIfDuringRender, queue, action) {
    action = {
      lane: 2,
      revertLane: requestTransitionLane(),
      gesture: null,
      action,
      hasEagerState: false,
      eagerState: null,
      next: null
    };
    if (isRenderPhaseUpdate(fiber)) {
      if (throwIfDuringRender) throw Error(formatProdErrorMessage(479));
    } else
      throwIfDuringRender = enqueueConcurrentHookUpdate(
        fiber,
        queue,
        action,
        2
      ), null !== throwIfDuringRender && scheduleUpdateOnFiber(throwIfDuringRender, fiber, 2);
  }
  function isRenderPhaseUpdate(fiber) {
    var alternate = fiber.alternate;
    return fiber === currentlyRenderingFiber || null !== alternate && alternate === currentlyRenderingFiber;
  }
  function enqueueRenderPhaseUpdate(queue, update) {
    didScheduleRenderPhaseUpdateDuringThisPass = didScheduleRenderPhaseUpdate = true;
    var pending = queue.pending;
    null === pending ? update.next = update : (update.next = pending.next, pending.next = update);
    queue.pending = update;
  }
  function entangleTransitionUpdate(root2, queue, lane) {
    if (0 !== (lane & 4194048)) {
      var queueLanes = queue.lanes;
      queueLanes &= root2.pendingLanes;
      lane |= queueLanes;
      queue.lanes = lane;
      markRootEntangled(root2, lane);
    }
  }
  var ContextOnlyDispatcher = {
    readContext,
    use,
    useCallback: throwInvalidHookError,
    useContext: throwInvalidHookError,
    useEffect: throwInvalidHookError,
    useImperativeHandle: throwInvalidHookError,
    useLayoutEffect: throwInvalidHookError,
    useInsertionEffect: throwInvalidHookError,
    useMemo: throwInvalidHookError,
    useReducer: throwInvalidHookError,
    useRef: throwInvalidHookError,
    useState: throwInvalidHookError,
    useDebugValue: throwInvalidHookError,
    useDeferredValue: throwInvalidHookError,
    useTransition: throwInvalidHookError,
    useSyncExternalStore: throwInvalidHookError,
    useId: throwInvalidHookError,
    useHostTransitionStatus: throwInvalidHookError,
    useFormState: throwInvalidHookError,
    useActionState: throwInvalidHookError,
    useOptimistic: throwInvalidHookError,
    useMemoCache: throwInvalidHookError,
    useCacheRefresh: throwInvalidHookError
  };
  ContextOnlyDispatcher.useEffectEvent = throwInvalidHookError;
  var HooksDispatcherOnMount = {
    readContext,
    use,
    useCallback: function(callback, deps) {
      mountWorkInProgressHook().memoizedState = [
        callback,
        void 0 === deps ? null : deps
      ];
      return callback;
    },
    useContext: readContext,
    useEffect: mountEffect,
    useImperativeHandle: function(ref, create, deps) {
      deps = null !== deps && void 0 !== deps ? deps.concat([ref]) : null;
      mountEffectImpl(
        4194308,
        4,
        imperativeHandleEffect.bind(null, create, ref),
        deps
      );
    },
    useLayoutEffect: function(create, deps) {
      return mountEffectImpl(4194308, 4, create, deps);
    },
    useInsertionEffect: function(create, deps) {
      mountEffectImpl(4, 2, create, deps);
    },
    useMemo: function(nextCreate, deps) {
      var hook = mountWorkInProgressHook();
      deps = void 0 === deps ? null : deps;
      var nextValue = nextCreate();
      if (shouldDoubleInvokeUserFnsInHooksDEV) {
        setIsStrictModeForDevtools(true);
        try {
          nextCreate();
        } finally {
          setIsStrictModeForDevtools(false);
        }
      }
      hook.memoizedState = [nextValue, deps];
      return nextValue;
    },
    useReducer: function(reducer, initialArg, init) {
      var hook = mountWorkInProgressHook();
      if (void 0 !== init) {
        var initialState = init(initialArg);
        if (shouldDoubleInvokeUserFnsInHooksDEV) {
          setIsStrictModeForDevtools(true);
          try {
            init(initialArg);
          } finally {
            setIsStrictModeForDevtools(false);
          }
        }
      } else initialState = initialArg;
      hook.memoizedState = hook.baseState = initialState;
      reducer = {
        pending: null,
        lanes: 0,
        dispatch: null,
        lastRenderedReducer: reducer,
        lastRenderedState: initialState
      };
      hook.queue = reducer;
      reducer = reducer.dispatch = dispatchReducerAction.bind(
        null,
        currentlyRenderingFiber,
        reducer
      );
      return [hook.memoizedState, reducer];
    },
    useRef: function(initialValue) {
      var hook = mountWorkInProgressHook();
      initialValue = { current: initialValue };
      return hook.memoizedState = initialValue;
    },
    useState: function(initialState) {
      initialState = mountStateImpl(initialState);
      var queue = initialState.queue, dispatch = dispatchSetState.bind(null, currentlyRenderingFiber, queue);
      queue.dispatch = dispatch;
      return [initialState.memoizedState, dispatch];
    },
    useDebugValue: mountDebugValue,
    useDeferredValue: function(value, initialValue) {
      var hook = mountWorkInProgressHook();
      return mountDeferredValueImpl(hook, value, initialValue);
    },
    useTransition: function() {
      var stateHook = mountStateImpl(false);
      stateHook = startTransition.bind(
        null,
        currentlyRenderingFiber,
        stateHook.queue,
        true,
        false
      );
      mountWorkInProgressHook().memoizedState = stateHook;
      return [false, stateHook];
    },
    useSyncExternalStore: function(subscribe, getSnapshot, getServerSnapshot) {
      var fiber = currentlyRenderingFiber, hook = mountWorkInProgressHook();
      if (isHydrating) {
        if (void 0 === getServerSnapshot)
          throw Error(formatProdErrorMessage(407));
        getServerSnapshot = getServerSnapshot();
      } else {
        getServerSnapshot = getSnapshot();
        if (null === workInProgressRoot)
          throw Error(formatProdErrorMessage(349));
        0 !== (workInProgressRootRenderLanes & 127) || pushStoreConsistencyCheck(fiber, getSnapshot, getServerSnapshot);
      }
      hook.memoizedState = getServerSnapshot;
      var inst = { value: getServerSnapshot, getSnapshot };
      hook.queue = inst;
      mountEffect(subscribeToStore.bind(null, fiber, inst, subscribe), [
        subscribe
      ]);
      fiber.flags |= 2048;
      pushSimpleEffect(
        9,
        { destroy: void 0 },
        updateStoreInstance.bind(
          null,
          fiber,
          inst,
          getServerSnapshot,
          getSnapshot
        ),
        null
      );
      return getServerSnapshot;
    },
    useId: function() {
      var hook = mountWorkInProgressHook(), identifierPrefix = workInProgressRoot.identifierPrefix;
      if (isHydrating) {
        var JSCompiler_inline_result = treeContextOverflow;
        var idWithLeadingBit = treeContextId;
        JSCompiler_inline_result = (idWithLeadingBit & ~(1 << 32 - clz32(idWithLeadingBit) - 1)).toString(32) + JSCompiler_inline_result;
        identifierPrefix = "_" + identifierPrefix + "R_" + JSCompiler_inline_result;
        JSCompiler_inline_result = localIdCounter++;
        0 < JSCompiler_inline_result && (identifierPrefix += "H" + JSCompiler_inline_result.toString(32));
        identifierPrefix += "_";
      } else
        JSCompiler_inline_result = globalClientIdCounter++, identifierPrefix = "_" + identifierPrefix + "r_" + JSCompiler_inline_result.toString(32) + "_";
      return hook.memoizedState = identifierPrefix;
    },
    useHostTransitionStatus,
    useFormState: mountActionState,
    useActionState: mountActionState,
    useOptimistic: function(passthrough) {
      var hook = mountWorkInProgressHook();
      hook.memoizedState = hook.baseState = passthrough;
      var queue = {
        pending: null,
        lanes: 0,
        dispatch: null,
        lastRenderedReducer: null,
        lastRenderedState: null
      };
      hook.queue = queue;
      hook = dispatchOptimisticSetState.bind(
        null,
        currentlyRenderingFiber,
        true,
        queue
      );
      queue.dispatch = hook;
      return [passthrough, hook];
    },
    useMemoCache,
    useCacheRefresh: function() {
      return mountWorkInProgressHook().memoizedState = refreshCache.bind(
        null,
        currentlyRenderingFiber
      );
    },
    useEffectEvent: function(callback) {
      var hook = mountWorkInProgressHook(), ref = { impl: callback };
      hook.memoizedState = ref;
      return function() {
        if (0 !== (executionContext & 2))
          throw Error(formatProdErrorMessage(440));
        return ref.impl.apply(void 0, arguments);
      };
    }
  }, HooksDispatcherOnUpdate = {
    readContext,
    use,
    useCallback: updateCallback,
    useContext: readContext,
    useEffect: updateEffect,
    useImperativeHandle: updateImperativeHandle,
    useInsertionEffect: updateInsertionEffect,
    useLayoutEffect: updateLayoutEffect,
    useMemo: updateMemo,
    useReducer: updateReducer,
    useRef: updateRef,
    useState: function() {
      return updateReducer(basicStateReducer);
    },
    useDebugValue: mountDebugValue,
    useDeferredValue: function(value, initialValue) {
      var hook = updateWorkInProgressHook();
      return updateDeferredValueImpl(
        hook,
        currentHook.memoizedState,
        value,
        initialValue
      );
    },
    useTransition: function() {
      var booleanOrThenable = updateReducer(basicStateReducer)[0], start = updateWorkInProgressHook().memoizedState;
      return [
        "boolean" === typeof booleanOrThenable ? booleanOrThenable : useThenable(booleanOrThenable),
        start
      ];
    },
    useSyncExternalStore: updateSyncExternalStore,
    useId: updateId,
    useHostTransitionStatus,
    useFormState: updateActionState,
    useActionState: updateActionState,
    useOptimistic: function(passthrough, reducer) {
      var hook = updateWorkInProgressHook();
      return updateOptimisticImpl(hook, currentHook, passthrough, reducer);
    },
    useMemoCache,
    useCacheRefresh: updateRefresh
  };
  HooksDispatcherOnUpdate.useEffectEvent = updateEvent;
  var HooksDispatcherOnRerender = {
    readContext,
    use,
    useCallback: updateCallback,
    useContext: readContext,
    useEffect: updateEffect,
    useImperativeHandle: updateImperativeHandle,
    useInsertionEffect: updateInsertionEffect,
    useLayoutEffect: updateLayoutEffect,
    useMemo: updateMemo,
    useReducer: rerenderReducer,
    useRef: updateRef,
    useState: function() {
      return rerenderReducer(basicStateReducer);
    },
    useDebugValue: mountDebugValue,
    useDeferredValue: function(value, initialValue) {
      var hook = updateWorkInProgressHook();
      return null === currentHook ? mountDeferredValueImpl(hook, value, initialValue) : updateDeferredValueImpl(
        hook,
        currentHook.memoizedState,
        value,
        initialValue
      );
    },
    useTransition: function() {
      var booleanOrThenable = rerenderReducer(basicStateReducer)[0], start = updateWorkInProgressHook().memoizedState;
      return [
        "boolean" === typeof booleanOrThenable ? booleanOrThenable : useThenable(booleanOrThenable),
        start
      ];
    },
    useSyncExternalStore: updateSyncExternalStore,
    useId: updateId,
    useHostTransitionStatus,
    useFormState: rerenderActionState,
    useActionState: rerenderActionState,
    useOptimistic: function(passthrough, reducer) {
      var hook = updateWorkInProgressHook();
      if (null !== currentHook)
        return updateOptimisticImpl(hook, currentHook, passthrough, reducer);
      hook.baseState = passthrough;
      return [passthrough, hook.queue.dispatch];
    },
    useMemoCache,
    useCacheRefresh: updateRefresh
  };
  HooksDispatcherOnRerender.useEffectEvent = updateEvent;
  function applyDerivedStateFromProps(workInProgress2, ctor, getDerivedStateFromProps, nextProps) {
    ctor = workInProgress2.memoizedState;
    getDerivedStateFromProps = getDerivedStateFromProps(nextProps, ctor);
    getDerivedStateFromProps = null === getDerivedStateFromProps || void 0 === getDerivedStateFromProps ? ctor : assign({}, ctor, getDerivedStateFromProps);
    workInProgress2.memoizedState = getDerivedStateFromProps;
    0 === workInProgress2.lanes && (workInProgress2.updateQueue.baseState = getDerivedStateFromProps);
  }
  var classComponentUpdater = {
    enqueueSetState: function(inst, payload, callback) {
      inst = inst._reactInternals;
      var lane = requestUpdateLane(), update = createUpdate(lane);
      update.payload = payload;
      void 0 !== callback && null !== callback && (update.callback = callback);
      payload = enqueueUpdate(inst, update, lane);
      null !== payload && (scheduleUpdateOnFiber(payload, inst, lane), entangleTransitions(payload, inst, lane));
    },
    enqueueReplaceState: function(inst, payload, callback) {
      inst = inst._reactInternals;
      var lane = requestUpdateLane(), update = createUpdate(lane);
      update.tag = 1;
      update.payload = payload;
      void 0 !== callback && null !== callback && (update.callback = callback);
      payload = enqueueUpdate(inst, update, lane);
      null !== payload && (scheduleUpdateOnFiber(payload, inst, lane), entangleTransitions(payload, inst, lane));
    },
    enqueueForceUpdate: function(inst, callback) {
      inst = inst._reactInternals;
      var lane = requestUpdateLane(), update = createUpdate(lane);
      update.tag = 2;
      void 0 !== callback && null !== callback && (update.callback = callback);
      callback = enqueueUpdate(inst, update, lane);
      null !== callback && (scheduleUpdateOnFiber(callback, inst, lane), entangleTransitions(callback, inst, lane));
    }
  };
  function checkShouldComponentUpdate(workInProgress2, ctor, oldProps, newProps, oldState, newState, nextContext) {
    workInProgress2 = workInProgress2.stateNode;
    return "function" === typeof workInProgress2.shouldComponentUpdate ? workInProgress2.shouldComponentUpdate(newProps, newState, nextContext) : ctor.prototype && ctor.prototype.isPureReactComponent ? !shallowEqual(oldProps, newProps) || !shallowEqual(oldState, newState) : true;
  }
  function callComponentWillReceiveProps(workInProgress2, instance, newProps, nextContext) {
    workInProgress2 = instance.state;
    "function" === typeof instance.componentWillReceiveProps && instance.componentWillReceiveProps(newProps, nextContext);
    "function" === typeof instance.UNSAFE_componentWillReceiveProps && instance.UNSAFE_componentWillReceiveProps(newProps, nextContext);
    instance.state !== workInProgress2 && classComponentUpdater.enqueueReplaceState(instance, instance.state, null);
  }
  function resolveClassComponentProps(Component, baseProps) {
    var newProps = baseProps;
    if ("ref" in baseProps) {
      newProps = {};
      for (var propName in baseProps)
        "ref" !== propName && (newProps[propName] = baseProps[propName]);
    }
    if (Component = Component.defaultProps) {
      newProps === baseProps && (newProps = assign({}, newProps));
      for (var propName$73 in Component)
        void 0 === newProps[propName$73] && (newProps[propName$73] = Component[propName$73]);
    }
    return newProps;
  }
  function defaultOnUncaughtError(error) {
    reportGlobalError(error);
  }
  function defaultOnCaughtError(error) {
    console.error(error);
  }
  function defaultOnRecoverableError(error) {
    reportGlobalError(error);
  }
  function logUncaughtError(root2, errorInfo) {
    try {
      var onUncaughtError = root2.onUncaughtError;
      onUncaughtError(errorInfo.value, { componentStack: errorInfo.stack });
    } catch (e$74) {
      setTimeout(function() {
        throw e$74;
      });
    }
  }
  function logCaughtError(root2, boundary, errorInfo) {
    try {
      var onCaughtError = root2.onCaughtError;
      onCaughtError(errorInfo.value, {
        componentStack: errorInfo.stack,
        errorBoundary: 1 === boundary.tag ? boundary.stateNode : null
      });
    } catch (e$75) {
      setTimeout(function() {
        throw e$75;
      });
    }
  }
  function createRootErrorUpdate(root2, errorInfo, lane) {
    lane = createUpdate(lane);
    lane.tag = 3;
    lane.payload = { element: null };
    lane.callback = function() {
      logUncaughtError(root2, errorInfo);
    };
    return lane;
  }
  function createClassErrorUpdate(lane) {
    lane = createUpdate(lane);
    lane.tag = 3;
    return lane;
  }
  function initializeClassErrorUpdate(update, root2, fiber, errorInfo) {
    var getDerivedStateFromError = fiber.type.getDerivedStateFromError;
    if ("function" === typeof getDerivedStateFromError) {
      var error = errorInfo.value;
      update.payload = function() {
        return getDerivedStateFromError(error);
      };
      update.callback = function() {
        logCaughtError(root2, fiber, errorInfo);
      };
    }
    var inst = fiber.stateNode;
    null !== inst && "function" === typeof inst.componentDidCatch && (update.callback = function() {
      logCaughtError(root2, fiber, errorInfo);
      "function" !== typeof getDerivedStateFromError && (null === legacyErrorBoundariesThatAlreadyFailed ? legacyErrorBoundariesThatAlreadyFailed = /* @__PURE__ */ new Set([this]) : legacyErrorBoundariesThatAlreadyFailed.add(this));
      var stack = errorInfo.stack;
      this.componentDidCatch(errorInfo.value, {
        componentStack: null !== stack ? stack : ""
      });
    });
  }
  function throwException(root2, returnFiber, sourceFiber, value, rootRenderLanes) {
    sourceFiber.flags |= 32768;
    if (null !== value && "object" === typeof value && "function" === typeof value.then) {
      returnFiber = sourceFiber.alternate;
      null !== returnFiber && propagateParentContextChanges(
        returnFiber,
        sourceFiber,
        rootRenderLanes,
        true
      );
      sourceFiber = suspenseHandlerStackCursor.current;
      if (null !== sourceFiber) {
        switch (sourceFiber.tag) {
          case 31:
          case 13:
            return null === shellBoundary ? renderDidSuspendDelayIfPossible() : null === sourceFiber.alternate && 0 === workInProgressRootExitStatus && (workInProgressRootExitStatus = 3), sourceFiber.flags &= -257, sourceFiber.flags |= 65536, sourceFiber.lanes = rootRenderLanes, value === noopSuspenseyCommitThenable ? sourceFiber.flags |= 16384 : (returnFiber = sourceFiber.updateQueue, null === returnFiber ? sourceFiber.updateQueue = /* @__PURE__ */ new Set([value]) : returnFiber.add(value), attachPingListener(root2, value, rootRenderLanes)), false;
          case 22:
            return sourceFiber.flags |= 65536, value === noopSuspenseyCommitThenable ? sourceFiber.flags |= 16384 : (returnFiber = sourceFiber.updateQueue, null === returnFiber ? (returnFiber = {
              transitions: null,
              markerInstances: null,
              retryQueue: /* @__PURE__ */ new Set([value])
            }, sourceFiber.updateQueue = returnFiber) : (sourceFiber = returnFiber.retryQueue, null === sourceFiber ? returnFiber.retryQueue = /* @__PURE__ */ new Set([value]) : sourceFiber.add(value)), attachPingListener(root2, value, rootRenderLanes)), false;
        }
        throw Error(formatProdErrorMessage(435, sourceFiber.tag));
      }
      attachPingListener(root2, value, rootRenderLanes);
      renderDidSuspendDelayIfPossible();
      return false;
    }
    if (isHydrating)
      return returnFiber = suspenseHandlerStackCursor.current, null !== returnFiber ? (0 === (returnFiber.flags & 65536) && (returnFiber.flags |= 256), returnFiber.flags |= 65536, returnFiber.lanes = rootRenderLanes, value !== HydrationMismatchException && (root2 = Error(formatProdErrorMessage(422), { cause: value }), queueHydrationError(createCapturedValueAtFiber(root2, sourceFiber)))) : (value !== HydrationMismatchException && (returnFiber = Error(formatProdErrorMessage(423), {
        cause: value
      }), queueHydrationError(
        createCapturedValueAtFiber(returnFiber, sourceFiber)
      )), root2 = root2.current.alternate, root2.flags |= 65536, rootRenderLanes &= -rootRenderLanes, root2.lanes |= rootRenderLanes, value = createCapturedValueAtFiber(value, sourceFiber), rootRenderLanes = createRootErrorUpdate(
        root2.stateNode,
        value,
        rootRenderLanes
      ), enqueueCapturedUpdate(root2, rootRenderLanes), 4 !== workInProgressRootExitStatus && (workInProgressRootExitStatus = 2)), false;
    var wrapperError = Error(formatProdErrorMessage(520), { cause: value });
    wrapperError = createCapturedValueAtFiber(wrapperError, sourceFiber);
    null === workInProgressRootConcurrentErrors ? workInProgressRootConcurrentErrors = [wrapperError] : workInProgressRootConcurrentErrors.push(wrapperError);
    4 !== workInProgressRootExitStatus && (workInProgressRootExitStatus = 2);
    if (null === returnFiber) return true;
    value = createCapturedValueAtFiber(value, sourceFiber);
    sourceFiber = returnFiber;
    do {
      switch (sourceFiber.tag) {
        case 3:
          return sourceFiber.flags |= 65536, root2 = rootRenderLanes & -rootRenderLanes, sourceFiber.lanes |= root2, root2 = createRootErrorUpdate(sourceFiber.stateNode, value, root2), enqueueCapturedUpdate(sourceFiber, root2), false;
        case 1:
          if (returnFiber = sourceFiber.type, wrapperError = sourceFiber.stateNode, 0 === (sourceFiber.flags & 128) && ("function" === typeof returnFiber.getDerivedStateFromError || null !== wrapperError && "function" === typeof wrapperError.componentDidCatch && (null === legacyErrorBoundariesThatAlreadyFailed || !legacyErrorBoundariesThatAlreadyFailed.has(wrapperError))))
            return sourceFiber.flags |= 65536, rootRenderLanes &= -rootRenderLanes, sourceFiber.lanes |= rootRenderLanes, rootRenderLanes = createClassErrorUpdate(rootRenderLanes), initializeClassErrorUpdate(
              rootRenderLanes,
              root2,
              sourceFiber,
              value
            ), enqueueCapturedUpdate(sourceFiber, rootRenderLanes), false;
      }
      sourceFiber = sourceFiber.return;
    } while (null !== sourceFiber);
    return false;
  }
  var SelectiveHydrationException = Error(formatProdErrorMessage(461)), didReceiveUpdate = false;
  function reconcileChildren(current, workInProgress2, nextChildren, renderLanes2) {
    workInProgress2.child = null === current ? mountChildFibers(workInProgress2, null, nextChildren, renderLanes2) : reconcileChildFibers(
      workInProgress2,
      current.child,
      nextChildren,
      renderLanes2
    );
  }
  function updateForwardRef(current, workInProgress2, Component, nextProps, renderLanes2) {
    Component = Component.render;
    var ref = workInProgress2.ref;
    if ("ref" in nextProps) {
      var propsWithoutRef = {};
      for (var key in nextProps)
        "ref" !== key && (propsWithoutRef[key] = nextProps[key]);
    } else propsWithoutRef = nextProps;
    prepareToReadContext(workInProgress2);
    nextProps = renderWithHooks(
      current,
      workInProgress2,
      Component,
      propsWithoutRef,
      ref,
      renderLanes2
    );
    key = checkDidRenderIdHook();
    if (null !== current && !didReceiveUpdate)
      return bailoutHooks(current, workInProgress2, renderLanes2), bailoutOnAlreadyFinishedWork(current, workInProgress2, renderLanes2);
    isHydrating && key && pushMaterializedTreeId(workInProgress2);
    workInProgress2.flags |= 1;
    reconcileChildren(current, workInProgress2, nextProps, renderLanes2);
    return workInProgress2.child;
  }
  function updateMemoComponent(current, workInProgress2, Component, nextProps, renderLanes2) {
    if (null === current) {
      var type = Component.type;
      if ("function" === typeof type && !shouldConstruct(type) && void 0 === type.defaultProps && null === Component.compare)
        return workInProgress2.tag = 15, workInProgress2.type = type, updateSimpleMemoComponent(
          current,
          workInProgress2,
          type,
          nextProps,
          renderLanes2
        );
      current = createFiberFromTypeAndProps(
        Component.type,
        null,
        nextProps,
        workInProgress2,
        workInProgress2.mode,
        renderLanes2
      );
      current.ref = workInProgress2.ref;
      current.return = workInProgress2;
      return workInProgress2.child = current;
    }
    type = current.child;
    if (!checkScheduledUpdateOrContext(current, renderLanes2)) {
      var prevProps = type.memoizedProps;
      Component = Component.compare;
      Component = null !== Component ? Component : shallowEqual;
      if (Component(prevProps, nextProps) && current.ref === workInProgress2.ref)
        return bailoutOnAlreadyFinishedWork(current, workInProgress2, renderLanes2);
    }
    workInProgress2.flags |= 1;
    current = createWorkInProgress(type, nextProps);
    current.ref = workInProgress2.ref;
    current.return = workInProgress2;
    return workInProgress2.child = current;
  }
  function updateSimpleMemoComponent(current, workInProgress2, Component, nextProps, renderLanes2) {
    if (null !== current) {
      var prevProps = current.memoizedProps;
      if (shallowEqual(prevProps, nextProps) && current.ref === workInProgress2.ref)
        if (didReceiveUpdate = false, workInProgress2.pendingProps = nextProps = prevProps, checkScheduledUpdateOrContext(current, renderLanes2))
          0 !== (current.flags & 131072) && (didReceiveUpdate = true);
        else
          return workInProgress2.lanes = current.lanes, bailoutOnAlreadyFinishedWork(current, workInProgress2, renderLanes2);
    }
    return updateFunctionComponent(
      current,
      workInProgress2,
      Component,
      nextProps,
      renderLanes2
    );
  }
  function updateOffscreenComponent(current, workInProgress2, renderLanes2, nextProps) {
    var nextChildren = nextProps.children, prevState = null !== current ? current.memoizedState : null;
    null === current && null === workInProgress2.stateNode && (workInProgress2.stateNode = {
      _visibility: 1,
      _pendingMarkers: null,
      _retryCache: null,
      _transitions: null
    });
    if ("hidden" === nextProps.mode) {
      if (0 !== (workInProgress2.flags & 128)) {
        prevState = null !== prevState ? prevState.baseLanes | renderLanes2 : renderLanes2;
        if (null !== current) {
          nextProps = workInProgress2.child = current.child;
          for (nextChildren = 0; null !== nextProps; )
            nextChildren = nextChildren | nextProps.lanes | nextProps.childLanes, nextProps = nextProps.sibling;
          nextProps = nextChildren & ~prevState;
        } else nextProps = 0, workInProgress2.child = null;
        return deferHiddenOffscreenComponent(
          current,
          workInProgress2,
          prevState,
          renderLanes2,
          nextProps
        );
      }
      if (0 !== (renderLanes2 & 536870912))
        workInProgress2.memoizedState = { baseLanes: 0, cachePool: null }, null !== current && pushTransition(
          workInProgress2,
          null !== prevState ? prevState.cachePool : null
        ), null !== prevState ? pushHiddenContext(workInProgress2, prevState) : reuseHiddenContextOnStack(), pushOffscreenSuspenseHandler(workInProgress2);
      else
        return nextProps = workInProgress2.lanes = 536870912, deferHiddenOffscreenComponent(
          current,
          workInProgress2,
          null !== prevState ? prevState.baseLanes | renderLanes2 : renderLanes2,
          renderLanes2,
          nextProps
        );
    } else
      null !== prevState ? (pushTransition(workInProgress2, prevState.cachePool), pushHiddenContext(workInProgress2, prevState), reuseSuspenseHandlerOnStack(), workInProgress2.memoizedState = null) : (null !== current && pushTransition(workInProgress2, null), reuseHiddenContextOnStack(), reuseSuspenseHandlerOnStack());
    reconcileChildren(current, workInProgress2, nextChildren, renderLanes2);
    return workInProgress2.child;
  }
  function bailoutOffscreenComponent(current, workInProgress2) {
    null !== current && 22 === current.tag || null !== workInProgress2.stateNode || (workInProgress2.stateNode = {
      _visibility: 1,
      _pendingMarkers: null,
      _retryCache: null,
      _transitions: null
    });
    return workInProgress2.sibling;
  }
  function deferHiddenOffscreenComponent(current, workInProgress2, nextBaseLanes, renderLanes2, remainingChildLanes) {
    var JSCompiler_inline_result = peekCacheFromPool();
    JSCompiler_inline_result = null === JSCompiler_inline_result ? null : { parent: CacheContext._currentValue, pool: JSCompiler_inline_result };
    workInProgress2.memoizedState = {
      baseLanes: nextBaseLanes,
      cachePool: JSCompiler_inline_result
    };
    null !== current && pushTransition(workInProgress2, null);
    reuseHiddenContextOnStack();
    pushOffscreenSuspenseHandler(workInProgress2);
    null !== current && propagateParentContextChanges(current, workInProgress2, renderLanes2, true);
    workInProgress2.childLanes = remainingChildLanes;
    return null;
  }
  function mountActivityChildren(workInProgress2, nextProps) {
    nextProps = mountWorkInProgressOffscreenFiber(
      { mode: nextProps.mode, children: nextProps.children },
      workInProgress2.mode
    );
    nextProps.ref = workInProgress2.ref;
    workInProgress2.child = nextProps;
    nextProps.return = workInProgress2;
    return nextProps;
  }
  function retryActivityComponentWithoutHydrating(current, workInProgress2, renderLanes2) {
    reconcileChildFibers(workInProgress2, current.child, null, renderLanes2);
    current = mountActivityChildren(workInProgress2, workInProgress2.pendingProps);
    current.flags |= 2;
    popSuspenseHandler(workInProgress2);
    workInProgress2.memoizedState = null;
    return current;
  }
  function updateActivityComponent(current, workInProgress2, renderLanes2) {
    var nextProps = workInProgress2.pendingProps, didSuspend = 0 !== (workInProgress2.flags & 128);
    workInProgress2.flags &= -129;
    if (null === current) {
      if (isHydrating) {
        if ("hidden" === nextProps.mode)
          return current = mountActivityChildren(workInProgress2, nextProps), workInProgress2.lanes = 536870912, bailoutOffscreenComponent(null, current);
        pushDehydratedActivitySuspenseHandler(workInProgress2);
        (current = nextHydratableInstance) ? (current = canHydrateHydrationBoundary(
          current,
          rootOrSingletonContext
        ), current = null !== current && "&" === current.data ? current : null, null !== current && (workInProgress2.memoizedState = {
          dehydrated: current,
          treeContext: null !== treeContextProvider ? { id: treeContextId, overflow: treeContextOverflow } : null,
          retryLane: 536870912,
          hydrationErrors: null
        }, renderLanes2 = createFiberFromDehydratedFragment(current), renderLanes2.return = workInProgress2, workInProgress2.child = renderLanes2, hydrationParentFiber = workInProgress2, nextHydratableInstance = null)) : current = null;
        if (null === current) throw throwOnHydrationMismatch(workInProgress2);
        workInProgress2.lanes = 536870912;
        return null;
      }
      return mountActivityChildren(workInProgress2, nextProps);
    }
    var prevState = current.memoizedState;
    if (null !== prevState) {
      var dehydrated = prevState.dehydrated;
      pushDehydratedActivitySuspenseHandler(workInProgress2);
      if (didSuspend)
        if (workInProgress2.flags & 256)
          workInProgress2.flags &= -257, workInProgress2 = retryActivityComponentWithoutHydrating(
            current,
            workInProgress2,
            renderLanes2
          );
        else if (null !== workInProgress2.memoizedState)
          workInProgress2.child = current.child, workInProgress2.flags |= 128, workInProgress2 = null;
        else throw Error(formatProdErrorMessage(558));
      else if (didReceiveUpdate || propagateParentContextChanges(current, workInProgress2, renderLanes2, false), didSuspend = 0 !== (renderLanes2 & current.childLanes), didReceiveUpdate || didSuspend) {
        nextProps = workInProgressRoot;
        if (null !== nextProps && (dehydrated = getBumpedLaneForHydration(nextProps, renderLanes2), 0 !== dehydrated && dehydrated !== prevState.retryLane))
          throw prevState.retryLane = dehydrated, enqueueConcurrentRenderForLane(current, dehydrated), scheduleUpdateOnFiber(nextProps, current, dehydrated), SelectiveHydrationException;
        renderDidSuspendDelayIfPossible();
        workInProgress2 = retryActivityComponentWithoutHydrating(
          current,
          workInProgress2,
          renderLanes2
        );
      } else
        current = prevState.treeContext, nextHydratableInstance = getNextHydratable(dehydrated.nextSibling), hydrationParentFiber = workInProgress2, isHydrating = true, hydrationErrors = null, rootOrSingletonContext = false, null !== current && restoreSuspendedTreeContext(workInProgress2, current), workInProgress2 = mountActivityChildren(workInProgress2, nextProps), workInProgress2.flags |= 4096;
      return workInProgress2;
    }
    current = createWorkInProgress(current.child, {
      mode: nextProps.mode,
      children: nextProps.children
    });
    current.ref = workInProgress2.ref;
    workInProgress2.child = current;
    current.return = workInProgress2;
    return current;
  }
  function markRef(current, workInProgress2) {
    var ref = workInProgress2.ref;
    if (null === ref)
      null !== current && null !== current.ref && (workInProgress2.flags |= 4194816);
    else {
      if ("function" !== typeof ref && "object" !== typeof ref)
        throw Error(formatProdErrorMessage(284));
      if (null === current || current.ref !== ref)
        workInProgress2.flags |= 4194816;
    }
  }
  function updateFunctionComponent(current, workInProgress2, Component, nextProps, renderLanes2) {
    prepareToReadContext(workInProgress2);
    Component = renderWithHooks(
      current,
      workInProgress2,
      Component,
      nextProps,
      void 0,
      renderLanes2
    );
    nextProps = checkDidRenderIdHook();
    if (null !== current && !didReceiveUpdate)
      return bailoutHooks(current, workInProgress2, renderLanes2), bailoutOnAlreadyFinishedWork(current, workInProgress2, renderLanes2);
    isHydrating && nextProps && pushMaterializedTreeId(workInProgress2);
    workInProgress2.flags |= 1;
    reconcileChildren(current, workInProgress2, Component, renderLanes2);
    return workInProgress2.child;
  }
  function replayFunctionComponent(current, workInProgress2, nextProps, Component, secondArg, renderLanes2) {
    prepareToReadContext(workInProgress2);
    workInProgress2.updateQueue = null;
    nextProps = renderWithHooksAgain(
      workInProgress2,
      Component,
      nextProps,
      secondArg
    );
    finishRenderingHooks(current);
    Component = checkDidRenderIdHook();
    if (null !== current && !didReceiveUpdate)
      return bailoutHooks(current, workInProgress2, renderLanes2), bailoutOnAlreadyFinishedWork(current, workInProgress2, renderLanes2);
    isHydrating && Component && pushMaterializedTreeId(workInProgress2);
    workInProgress2.flags |= 1;
    reconcileChildren(current, workInProgress2, nextProps, renderLanes2);
    return workInProgress2.child;
  }
  function updateClassComponent(current, workInProgress2, Component, nextProps, renderLanes2) {
    prepareToReadContext(workInProgress2);
    if (null === workInProgress2.stateNode) {
      var context = emptyContextObject, contextType = Component.contextType;
      "object" === typeof contextType && null !== contextType && (context = readContext(contextType));
      context = new Component(nextProps, context);
      workInProgress2.memoizedState = null !== context.state && void 0 !== context.state ? context.state : null;
      context.updater = classComponentUpdater;
      workInProgress2.stateNode = context;
      context._reactInternals = workInProgress2;
      context = workInProgress2.stateNode;
      context.props = nextProps;
      context.state = workInProgress2.memoizedState;
      context.refs = {};
      initializeUpdateQueue(workInProgress2);
      contextType = Component.contextType;
      context.context = "object" === typeof contextType && null !== contextType ? readContext(contextType) : emptyContextObject;
      context.state = workInProgress2.memoizedState;
      contextType = Component.getDerivedStateFromProps;
      "function" === typeof contextType && (applyDerivedStateFromProps(
        workInProgress2,
        Component,
        contextType,
        nextProps
      ), context.state = workInProgress2.memoizedState);
      "function" === typeof Component.getDerivedStateFromProps || "function" === typeof context.getSnapshotBeforeUpdate || "function" !== typeof context.UNSAFE_componentWillMount && "function" !== typeof context.componentWillMount || (contextType = context.state, "function" === typeof context.componentWillMount && context.componentWillMount(), "function" === typeof context.UNSAFE_componentWillMount && context.UNSAFE_componentWillMount(), contextType !== context.state && classComponentUpdater.enqueueReplaceState(context, context.state, null), processUpdateQueue(workInProgress2, nextProps, context, renderLanes2), suspendIfUpdateReadFromEntangledAsyncAction(), context.state = workInProgress2.memoizedState);
      "function" === typeof context.componentDidMount && (workInProgress2.flags |= 4194308);
      nextProps = true;
    } else if (null === current) {
      context = workInProgress2.stateNode;
      var unresolvedOldProps = workInProgress2.memoizedProps, oldProps = resolveClassComponentProps(Component, unresolvedOldProps);
      context.props = oldProps;
      var oldContext = context.context, contextType$jscomp$0 = Component.contextType;
      contextType = emptyContextObject;
      "object" === typeof contextType$jscomp$0 && null !== contextType$jscomp$0 && (contextType = readContext(contextType$jscomp$0));
      var getDerivedStateFromProps = Component.getDerivedStateFromProps;
      contextType$jscomp$0 = "function" === typeof getDerivedStateFromProps || "function" === typeof context.getSnapshotBeforeUpdate;
      unresolvedOldProps = workInProgress2.pendingProps !== unresolvedOldProps;
      contextType$jscomp$0 || "function" !== typeof context.UNSAFE_componentWillReceiveProps && "function" !== typeof context.componentWillReceiveProps || (unresolvedOldProps || oldContext !== contextType) && callComponentWillReceiveProps(
        workInProgress2,
        context,
        nextProps,
        contextType
      );
      hasForceUpdate = false;
      var oldState = workInProgress2.memoizedState;
      context.state = oldState;
      processUpdateQueue(workInProgress2, nextProps, context, renderLanes2);
      suspendIfUpdateReadFromEntangledAsyncAction();
      oldContext = workInProgress2.memoizedState;
      unresolvedOldProps || oldState !== oldContext || hasForceUpdate ? ("function" === typeof getDerivedStateFromProps && (applyDerivedStateFromProps(
        workInProgress2,
        Component,
        getDerivedStateFromProps,
        nextProps
      ), oldContext = workInProgress2.memoizedState), (oldProps = hasForceUpdate || checkShouldComponentUpdate(
        workInProgress2,
        Component,
        oldProps,
        nextProps,
        oldState,
        oldContext,
        contextType
      )) ? (contextType$jscomp$0 || "function" !== typeof context.UNSAFE_componentWillMount && "function" !== typeof context.componentWillMount || ("function" === typeof context.componentWillMount && context.componentWillMount(), "function" === typeof context.UNSAFE_componentWillMount && context.UNSAFE_componentWillMount()), "function" === typeof context.componentDidMount && (workInProgress2.flags |= 4194308)) : ("function" === typeof context.componentDidMount && (workInProgress2.flags |= 4194308), workInProgress2.memoizedProps = nextProps, workInProgress2.memoizedState = oldContext), context.props = nextProps, context.state = oldContext, context.context = contextType, nextProps = oldProps) : ("function" === typeof context.componentDidMount && (workInProgress2.flags |= 4194308), nextProps = false);
    } else {
      context = workInProgress2.stateNode;
      cloneUpdateQueue(current, workInProgress2);
      contextType = workInProgress2.memoizedProps;
      contextType$jscomp$0 = resolveClassComponentProps(Component, contextType);
      context.props = contextType$jscomp$0;
      getDerivedStateFromProps = workInProgress2.pendingProps;
      oldState = context.context;
      oldContext = Component.contextType;
      oldProps = emptyContextObject;
      "object" === typeof oldContext && null !== oldContext && (oldProps = readContext(oldContext));
      unresolvedOldProps = Component.getDerivedStateFromProps;
      (oldContext = "function" === typeof unresolvedOldProps || "function" === typeof context.getSnapshotBeforeUpdate) || "function" !== typeof context.UNSAFE_componentWillReceiveProps && "function" !== typeof context.componentWillReceiveProps || (contextType !== getDerivedStateFromProps || oldState !== oldProps) && callComponentWillReceiveProps(
        workInProgress2,
        context,
        nextProps,
        oldProps
      );
      hasForceUpdate = false;
      oldState = workInProgress2.memoizedState;
      context.state = oldState;
      processUpdateQueue(workInProgress2, nextProps, context, renderLanes2);
      suspendIfUpdateReadFromEntangledAsyncAction();
      var newState = workInProgress2.memoizedState;
      contextType !== getDerivedStateFromProps || oldState !== newState || hasForceUpdate || null !== current && null !== current.dependencies && checkIfContextChanged(current.dependencies) ? ("function" === typeof unresolvedOldProps && (applyDerivedStateFromProps(
        workInProgress2,
        Component,
        unresolvedOldProps,
        nextProps
      ), newState = workInProgress2.memoizedState), (contextType$jscomp$0 = hasForceUpdate || checkShouldComponentUpdate(
        workInProgress2,
        Component,
        contextType$jscomp$0,
        nextProps,
        oldState,
        newState,
        oldProps
      ) || null !== current && null !== current.dependencies && checkIfContextChanged(current.dependencies)) ? (oldContext || "function" !== typeof context.UNSAFE_componentWillUpdate && "function" !== typeof context.componentWillUpdate || ("function" === typeof context.componentWillUpdate && context.componentWillUpdate(nextProps, newState, oldProps), "function" === typeof context.UNSAFE_componentWillUpdate && context.UNSAFE_componentWillUpdate(
        nextProps,
        newState,
        oldProps
      )), "function" === typeof context.componentDidUpdate && (workInProgress2.flags |= 4), "function" === typeof context.getSnapshotBeforeUpdate && (workInProgress2.flags |= 1024)) : ("function" !== typeof context.componentDidUpdate || contextType === current.memoizedProps && oldState === current.memoizedState || (workInProgress2.flags |= 4), "function" !== typeof context.getSnapshotBeforeUpdate || contextType === current.memoizedProps && oldState === current.memoizedState || (workInProgress2.flags |= 1024), workInProgress2.memoizedProps = nextProps, workInProgress2.memoizedState = newState), context.props = nextProps, context.state = newState, context.context = oldProps, nextProps = contextType$jscomp$0) : ("function" !== typeof context.componentDidUpdate || contextType === current.memoizedProps && oldState === current.memoizedState || (workInProgress2.flags |= 4), "function" !== typeof context.getSnapshotBeforeUpdate || contextType === current.memoizedProps && oldState === current.memoizedState || (workInProgress2.flags |= 1024), nextProps = false);
    }
    context = nextProps;
    markRef(current, workInProgress2);
    nextProps = 0 !== (workInProgress2.flags & 128);
    context || nextProps ? (context = workInProgress2.stateNode, Component = nextProps && "function" !== typeof Component.getDerivedStateFromError ? null : context.render(), workInProgress2.flags |= 1, null !== current && nextProps ? (workInProgress2.child = reconcileChildFibers(
      workInProgress2,
      current.child,
      null,
      renderLanes2
    ), workInProgress2.child = reconcileChildFibers(
      workInProgress2,
      null,
      Component,
      renderLanes2
    )) : reconcileChildren(current, workInProgress2, Component, renderLanes2), workInProgress2.memoizedState = context.state, current = workInProgress2.child) : current = bailoutOnAlreadyFinishedWork(
      current,
      workInProgress2,
      renderLanes2
    );
    return current;
  }
  function mountHostRootWithoutHydrating(current, workInProgress2, nextChildren, renderLanes2) {
    resetHydrationState();
    workInProgress2.flags |= 256;
    reconcileChildren(current, workInProgress2, nextChildren, renderLanes2);
    return workInProgress2.child;
  }
  var SUSPENDED_MARKER = {
    dehydrated: null,
    treeContext: null,
    retryLane: 0,
    hydrationErrors: null
  };
  function mountSuspenseOffscreenState(renderLanes2) {
    return { baseLanes: renderLanes2, cachePool: getSuspendedCache() };
  }
  function getRemainingWorkInPrimaryTree(current, primaryTreeDidDefer, renderLanes2) {
    current = null !== current ? current.childLanes & ~renderLanes2 : 0;
    primaryTreeDidDefer && (current |= workInProgressDeferredLane);
    return current;
  }
  function updateSuspenseComponent(current, workInProgress2, renderLanes2) {
    var nextProps = workInProgress2.pendingProps, showFallback = false, didSuspend = 0 !== (workInProgress2.flags & 128), JSCompiler_temp;
    (JSCompiler_temp = didSuspend) || (JSCompiler_temp = null !== current && null === current.memoizedState ? false : 0 !== (suspenseStackCursor.current & 2));
    JSCompiler_temp && (showFallback = true, workInProgress2.flags &= -129);
    JSCompiler_temp = 0 !== (workInProgress2.flags & 32);
    workInProgress2.flags &= -33;
    if (null === current) {
      if (isHydrating) {
        showFallback ? pushPrimaryTreeSuspenseHandler(workInProgress2) : reuseSuspenseHandlerOnStack();
        (current = nextHydratableInstance) ? (current = canHydrateHydrationBoundary(
          current,
          rootOrSingletonContext
        ), current = null !== current && "&" !== current.data ? current : null, null !== current && (workInProgress2.memoizedState = {
          dehydrated: current,
          treeContext: null !== treeContextProvider ? { id: treeContextId, overflow: treeContextOverflow } : null,
          retryLane: 536870912,
          hydrationErrors: null
        }, renderLanes2 = createFiberFromDehydratedFragment(current), renderLanes2.return = workInProgress2, workInProgress2.child = renderLanes2, hydrationParentFiber = workInProgress2, nextHydratableInstance = null)) : current = null;
        if (null === current) throw throwOnHydrationMismatch(workInProgress2);
        isSuspenseInstanceFallback(current) ? workInProgress2.lanes = 32 : workInProgress2.lanes = 536870912;
        return null;
      }
      var nextPrimaryChildren = nextProps.children;
      nextProps = nextProps.fallback;
      if (showFallback)
        return reuseSuspenseHandlerOnStack(), showFallback = workInProgress2.mode, nextPrimaryChildren = mountWorkInProgressOffscreenFiber(
          { mode: "hidden", children: nextPrimaryChildren },
          showFallback
        ), nextProps = createFiberFromFragment(
          nextProps,
          showFallback,
          renderLanes2,
          null
        ), nextPrimaryChildren.return = workInProgress2, nextProps.return = workInProgress2, nextPrimaryChildren.sibling = nextProps, workInProgress2.child = nextPrimaryChildren, nextProps = workInProgress2.child, nextProps.memoizedState = mountSuspenseOffscreenState(renderLanes2), nextProps.childLanes = getRemainingWorkInPrimaryTree(
          current,
          JSCompiler_temp,
          renderLanes2
        ), workInProgress2.memoizedState = SUSPENDED_MARKER, bailoutOffscreenComponent(null, nextProps);
      pushPrimaryTreeSuspenseHandler(workInProgress2);
      return mountSuspensePrimaryChildren(workInProgress2, nextPrimaryChildren);
    }
    var prevState = current.memoizedState;
    if (null !== prevState && (nextPrimaryChildren = prevState.dehydrated, null !== nextPrimaryChildren)) {
      if (didSuspend)
        workInProgress2.flags & 256 ? (pushPrimaryTreeSuspenseHandler(workInProgress2), workInProgress2.flags &= -257, workInProgress2 = retrySuspenseComponentWithoutHydrating(
          current,
          workInProgress2,
          renderLanes2
        )) : null !== workInProgress2.memoizedState ? (reuseSuspenseHandlerOnStack(), workInProgress2.child = current.child, workInProgress2.flags |= 128, workInProgress2 = null) : (reuseSuspenseHandlerOnStack(), nextPrimaryChildren = nextProps.fallback, showFallback = workInProgress2.mode, nextProps = mountWorkInProgressOffscreenFiber(
          { mode: "visible", children: nextProps.children },
          showFallback
        ), nextPrimaryChildren = createFiberFromFragment(
          nextPrimaryChildren,
          showFallback,
          renderLanes2,
          null
        ), nextPrimaryChildren.flags |= 2, nextProps.return = workInProgress2, nextPrimaryChildren.return = workInProgress2, nextProps.sibling = nextPrimaryChildren, workInProgress2.child = nextProps, reconcileChildFibers(
          workInProgress2,
          current.child,
          null,
          renderLanes2
        ), nextProps = workInProgress2.child, nextProps.memoizedState = mountSuspenseOffscreenState(renderLanes2), nextProps.childLanes = getRemainingWorkInPrimaryTree(
          current,
          JSCompiler_temp,
          renderLanes2
        ), workInProgress2.memoizedState = SUSPENDED_MARKER, workInProgress2 = bailoutOffscreenComponent(null, nextProps));
      else if (pushPrimaryTreeSuspenseHandler(workInProgress2), isSuspenseInstanceFallback(nextPrimaryChildren)) {
        JSCompiler_temp = nextPrimaryChildren.nextSibling && nextPrimaryChildren.nextSibling.dataset;
        if (JSCompiler_temp) var digest = JSCompiler_temp.dgst;
        JSCompiler_temp = digest;
        nextProps = Error(formatProdErrorMessage(419));
        nextProps.stack = "";
        nextProps.digest = JSCompiler_temp;
        queueHydrationError({ value: nextProps, source: null, stack: null });
        workInProgress2 = retrySuspenseComponentWithoutHydrating(
          current,
          workInProgress2,
          renderLanes2
        );
      } else if (didReceiveUpdate || propagateParentContextChanges(current, workInProgress2, renderLanes2, false), JSCompiler_temp = 0 !== (renderLanes2 & current.childLanes), didReceiveUpdate || JSCompiler_temp) {
        JSCompiler_temp = workInProgressRoot;
        if (null !== JSCompiler_temp && (nextProps = getBumpedLaneForHydration(JSCompiler_temp, renderLanes2), 0 !== nextProps && nextProps !== prevState.retryLane))
          throw prevState.retryLane = nextProps, enqueueConcurrentRenderForLane(current, nextProps), scheduleUpdateOnFiber(JSCompiler_temp, current, nextProps), SelectiveHydrationException;
        isSuspenseInstancePending(nextPrimaryChildren) || renderDidSuspendDelayIfPossible();
        workInProgress2 = retrySuspenseComponentWithoutHydrating(
          current,
          workInProgress2,
          renderLanes2
        );
      } else
        isSuspenseInstancePending(nextPrimaryChildren) ? (workInProgress2.flags |= 192, workInProgress2.child = current.child, workInProgress2 = null) : (current = prevState.treeContext, nextHydratableInstance = getNextHydratable(
          nextPrimaryChildren.nextSibling
        ), hydrationParentFiber = workInProgress2, isHydrating = true, hydrationErrors = null, rootOrSingletonContext = false, null !== current && restoreSuspendedTreeContext(workInProgress2, current), workInProgress2 = mountSuspensePrimaryChildren(
          workInProgress2,
          nextProps.children
        ), workInProgress2.flags |= 4096);
      return workInProgress2;
    }
    if (showFallback)
      return reuseSuspenseHandlerOnStack(), nextPrimaryChildren = nextProps.fallback, showFallback = workInProgress2.mode, prevState = current.child, digest = prevState.sibling, nextProps = createWorkInProgress(prevState, {
        mode: "hidden",
        children: nextProps.children
      }), nextProps.subtreeFlags = prevState.subtreeFlags & 65011712, null !== digest ? nextPrimaryChildren = createWorkInProgress(
        digest,
        nextPrimaryChildren
      ) : (nextPrimaryChildren = createFiberFromFragment(
        nextPrimaryChildren,
        showFallback,
        renderLanes2,
        null
      ), nextPrimaryChildren.flags |= 2), nextPrimaryChildren.return = workInProgress2, nextProps.return = workInProgress2, nextProps.sibling = nextPrimaryChildren, workInProgress2.child = nextProps, bailoutOffscreenComponent(null, nextProps), nextProps = workInProgress2.child, nextPrimaryChildren = current.child.memoizedState, null === nextPrimaryChildren ? nextPrimaryChildren = mountSuspenseOffscreenState(renderLanes2) : (showFallback = nextPrimaryChildren.cachePool, null !== showFallback ? (prevState = CacheContext._currentValue, showFallback = showFallback.parent !== prevState ? { parent: prevState, pool: prevState } : showFallback) : showFallback = getSuspendedCache(), nextPrimaryChildren = {
        baseLanes: nextPrimaryChildren.baseLanes | renderLanes2,
        cachePool: showFallback
      }), nextProps.memoizedState = nextPrimaryChildren, nextProps.childLanes = getRemainingWorkInPrimaryTree(
        current,
        JSCompiler_temp,
        renderLanes2
      ), workInProgress2.memoizedState = SUSPENDED_MARKER, bailoutOffscreenComponent(current.child, nextProps);
    pushPrimaryTreeSuspenseHandler(workInProgress2);
    renderLanes2 = current.child;
    current = renderLanes2.sibling;
    renderLanes2 = createWorkInProgress(renderLanes2, {
      mode: "visible",
      children: nextProps.children
    });
    renderLanes2.return = workInProgress2;
    renderLanes2.sibling = null;
    null !== current && (JSCompiler_temp = workInProgress2.deletions, null === JSCompiler_temp ? (workInProgress2.deletions = [current], workInProgress2.flags |= 16) : JSCompiler_temp.push(current));
    workInProgress2.child = renderLanes2;
    workInProgress2.memoizedState = null;
    return renderLanes2;
  }
  function mountSuspensePrimaryChildren(workInProgress2, primaryChildren) {
    primaryChildren = mountWorkInProgressOffscreenFiber(
      { mode: "visible", children: primaryChildren },
      workInProgress2.mode
    );
    primaryChildren.return = workInProgress2;
    return workInProgress2.child = primaryChildren;
  }
  function mountWorkInProgressOffscreenFiber(offscreenProps, mode) {
    offscreenProps = createFiberImplClass(22, offscreenProps, null, mode);
    offscreenProps.lanes = 0;
    return offscreenProps;
  }
  function retrySuspenseComponentWithoutHydrating(current, workInProgress2, renderLanes2) {
    reconcileChildFibers(workInProgress2, current.child, null, renderLanes2);
    current = mountSuspensePrimaryChildren(
      workInProgress2,
      workInProgress2.pendingProps.children
    );
    current.flags |= 2;
    workInProgress2.memoizedState = null;
    return current;
  }
  function scheduleSuspenseWorkOnFiber(fiber, renderLanes2, propagationRoot) {
    fiber.lanes |= renderLanes2;
    var alternate = fiber.alternate;
    null !== alternate && (alternate.lanes |= renderLanes2);
    scheduleContextWorkOnParentPath(fiber.return, renderLanes2, propagationRoot);
  }
  function initSuspenseListRenderState(workInProgress2, isBackwards, tail, lastContentRow, tailMode, treeForkCount2) {
    var renderState = workInProgress2.memoizedState;
    null === renderState ? workInProgress2.memoizedState = {
      isBackwards,
      rendering: null,
      renderingStartTime: 0,
      last: lastContentRow,
      tail,
      tailMode,
      treeForkCount: treeForkCount2
    } : (renderState.isBackwards = isBackwards, renderState.rendering = null, renderState.renderingStartTime = 0, renderState.last = lastContentRow, renderState.tail = tail, renderState.tailMode = tailMode, renderState.treeForkCount = treeForkCount2);
  }
  function updateSuspenseListComponent(current, workInProgress2, renderLanes2) {
    var nextProps = workInProgress2.pendingProps, revealOrder = nextProps.revealOrder, tailMode = nextProps.tail;
    nextProps = nextProps.children;
    var suspenseContext = suspenseStackCursor.current, shouldForceFallback = 0 !== (suspenseContext & 2);
    shouldForceFallback ? (suspenseContext = suspenseContext & 1 | 2, workInProgress2.flags |= 128) : suspenseContext &= 1;
    push(suspenseStackCursor, suspenseContext);
    reconcileChildren(current, workInProgress2, nextProps, renderLanes2);
    nextProps = isHydrating ? treeForkCount : 0;
    if (!shouldForceFallback && null !== current && 0 !== (current.flags & 128))
      a: for (current = workInProgress2.child; null !== current; ) {
        if (13 === current.tag)
          null !== current.memoizedState && scheduleSuspenseWorkOnFiber(current, renderLanes2, workInProgress2);
        else if (19 === current.tag)
          scheduleSuspenseWorkOnFiber(current, renderLanes2, workInProgress2);
        else if (null !== current.child) {
          current.child.return = current;
          current = current.child;
          continue;
        }
        if (current === workInProgress2) break a;
        for (; null === current.sibling; ) {
          if (null === current.return || current.return === workInProgress2)
            break a;
          current = current.return;
        }
        current.sibling.return = current.return;
        current = current.sibling;
      }
    switch (revealOrder) {
      case "forwards":
        renderLanes2 = workInProgress2.child;
        for (revealOrder = null; null !== renderLanes2; )
          current = renderLanes2.alternate, null !== current && null === findFirstSuspended(current) && (revealOrder = renderLanes2), renderLanes2 = renderLanes2.sibling;
        renderLanes2 = revealOrder;
        null === renderLanes2 ? (revealOrder = workInProgress2.child, workInProgress2.child = null) : (revealOrder = renderLanes2.sibling, renderLanes2.sibling = null);
        initSuspenseListRenderState(
          workInProgress2,
          false,
          revealOrder,
          renderLanes2,
          tailMode,
          nextProps
        );
        break;
      case "backwards":
      case "unstable_legacy-backwards":
        renderLanes2 = null;
        revealOrder = workInProgress2.child;
        for (workInProgress2.child = null; null !== revealOrder; ) {
          current = revealOrder.alternate;
          if (null !== current && null === findFirstSuspended(current)) {
            workInProgress2.child = revealOrder;
            break;
          }
          current = revealOrder.sibling;
          revealOrder.sibling = renderLanes2;
          renderLanes2 = revealOrder;
          revealOrder = current;
        }
        initSuspenseListRenderState(
          workInProgress2,
          true,
          renderLanes2,
          null,
          tailMode,
          nextProps
        );
        break;
      case "together":
        initSuspenseListRenderState(
          workInProgress2,
          false,
          null,
          null,
          void 0,
          nextProps
        );
        break;
      default:
        workInProgress2.memoizedState = null;
    }
    return workInProgress2.child;
  }
  function bailoutOnAlreadyFinishedWork(current, workInProgress2, renderLanes2) {
    null !== current && (workInProgress2.dependencies = current.dependencies);
    workInProgressRootSkippedLanes |= workInProgress2.lanes;
    if (0 === (renderLanes2 & workInProgress2.childLanes))
      if (null !== current) {
        if (propagateParentContextChanges(
          current,
          workInProgress2,
          renderLanes2,
          false
        ), 0 === (renderLanes2 & workInProgress2.childLanes))
          return null;
      } else return null;
    if (null !== current && workInProgress2.child !== current.child)
      throw Error(formatProdErrorMessage(153));
    if (null !== workInProgress2.child) {
      current = workInProgress2.child;
      renderLanes2 = createWorkInProgress(current, current.pendingProps);
      workInProgress2.child = renderLanes2;
      for (renderLanes2.return = workInProgress2; null !== current.sibling; )
        current = current.sibling, renderLanes2 = renderLanes2.sibling = createWorkInProgress(current, current.pendingProps), renderLanes2.return = workInProgress2;
      renderLanes2.sibling = null;
    }
    return workInProgress2.child;
  }
  function checkScheduledUpdateOrContext(current, renderLanes2) {
    if (0 !== (current.lanes & renderLanes2)) return true;
    current = current.dependencies;
    return null !== current && checkIfContextChanged(current) ? true : false;
  }
  function attemptEarlyBailoutIfNoScheduledUpdate(current, workInProgress2, renderLanes2) {
    switch (workInProgress2.tag) {
      case 3:
        pushHostContainer(workInProgress2, workInProgress2.stateNode.containerInfo);
        pushProvider(workInProgress2, CacheContext, current.memoizedState.cache);
        resetHydrationState();
        break;
      case 27:
      case 5:
        pushHostContext(workInProgress2);
        break;
      case 4:
        pushHostContainer(workInProgress2, workInProgress2.stateNode.containerInfo);
        break;
      case 10:
        pushProvider(
          workInProgress2,
          workInProgress2.type,
          workInProgress2.memoizedProps.value
        );
        break;
      case 31:
        if (null !== workInProgress2.memoizedState)
          return workInProgress2.flags |= 128, pushDehydratedActivitySuspenseHandler(workInProgress2), null;
        break;
      case 13:
        var state$102 = workInProgress2.memoizedState;
        if (null !== state$102) {
          if (null !== state$102.dehydrated)
            return pushPrimaryTreeSuspenseHandler(workInProgress2), workInProgress2.flags |= 128, null;
          if (0 !== (renderLanes2 & workInProgress2.child.childLanes))
            return updateSuspenseComponent(current, workInProgress2, renderLanes2);
          pushPrimaryTreeSuspenseHandler(workInProgress2);
          current = bailoutOnAlreadyFinishedWork(
            current,
            workInProgress2,
            renderLanes2
          );
          return null !== current ? current.sibling : null;
        }
        pushPrimaryTreeSuspenseHandler(workInProgress2);
        break;
      case 19:
        var didSuspendBefore = 0 !== (current.flags & 128);
        state$102 = 0 !== (renderLanes2 & workInProgress2.childLanes);
        state$102 || (propagateParentContextChanges(
          current,
          workInProgress2,
          renderLanes2,
          false
        ), state$102 = 0 !== (renderLanes2 & workInProgress2.childLanes));
        if (didSuspendBefore) {
          if (state$102)
            return updateSuspenseListComponent(
              current,
              workInProgress2,
              renderLanes2
            );
          workInProgress2.flags |= 128;
        }
        didSuspendBefore = workInProgress2.memoizedState;
        null !== didSuspendBefore && (didSuspendBefore.rendering = null, didSuspendBefore.tail = null, didSuspendBefore.lastEffect = null);
        push(suspenseStackCursor, suspenseStackCursor.current);
        if (state$102) break;
        else return null;
      case 22:
        return workInProgress2.lanes = 0, updateOffscreenComponent(
          current,
          workInProgress2,
          renderLanes2,
          workInProgress2.pendingProps
        );
      case 24:
        pushProvider(workInProgress2, CacheContext, current.memoizedState.cache);
    }
    return bailoutOnAlreadyFinishedWork(current, workInProgress2, renderLanes2);
  }
  function beginWork(current, workInProgress2, renderLanes2) {
    if (null !== current)
      if (current.memoizedProps !== workInProgress2.pendingProps)
        didReceiveUpdate = true;
      else {
        if (!checkScheduledUpdateOrContext(current, renderLanes2) && 0 === (workInProgress2.flags & 128))
          return didReceiveUpdate = false, attemptEarlyBailoutIfNoScheduledUpdate(
            current,
            workInProgress2,
            renderLanes2
          );
        didReceiveUpdate = 0 !== (current.flags & 131072) ? true : false;
      }
    else
      didReceiveUpdate = false, isHydrating && 0 !== (workInProgress2.flags & 1048576) && pushTreeId(workInProgress2, treeForkCount, workInProgress2.index);
    workInProgress2.lanes = 0;
    switch (workInProgress2.tag) {
      case 16:
        a: {
          var props = workInProgress2.pendingProps;
          current = resolveLazy(workInProgress2.elementType);
          workInProgress2.type = current;
          if ("function" === typeof current)
            shouldConstruct(current) ? (props = resolveClassComponentProps(current, props), workInProgress2.tag = 1, workInProgress2 = updateClassComponent(
              null,
              workInProgress2,
              current,
              props,
              renderLanes2
            )) : (workInProgress2.tag = 0, workInProgress2 = updateFunctionComponent(
              null,
              workInProgress2,
              current,
              props,
              renderLanes2
            ));
          else {
            if (void 0 !== current && null !== current) {
              var $$typeof = current.$$typeof;
              if ($$typeof === REACT_FORWARD_REF_TYPE) {
                workInProgress2.tag = 11;
                workInProgress2 = updateForwardRef(
                  null,
                  workInProgress2,
                  current,
                  props,
                  renderLanes2
                );
                break a;
              } else if ($$typeof === REACT_MEMO_TYPE) {
                workInProgress2.tag = 14;
                workInProgress2 = updateMemoComponent(
                  null,
                  workInProgress2,
                  current,
                  props,
                  renderLanes2
                );
                break a;
              }
            }
            workInProgress2 = getComponentNameFromType(current) || current;
            throw Error(formatProdErrorMessage(306, workInProgress2, ""));
          }
        }
        return workInProgress2;
      case 0:
        return updateFunctionComponent(
          current,
          workInProgress2,
          workInProgress2.type,
          workInProgress2.pendingProps,
          renderLanes2
        );
      case 1:
        return props = workInProgress2.type, $$typeof = resolveClassComponentProps(
          props,
          workInProgress2.pendingProps
        ), updateClassComponent(
          current,
          workInProgress2,
          props,
          $$typeof,
          renderLanes2
        );
      case 3:
        a: {
          pushHostContainer(
            workInProgress2,
            workInProgress2.stateNode.containerInfo
          );
          if (null === current) throw Error(formatProdErrorMessage(387));
          props = workInProgress2.pendingProps;
          var prevState = workInProgress2.memoizedState;
          $$typeof = prevState.element;
          cloneUpdateQueue(current, workInProgress2);
          processUpdateQueue(workInProgress2, props, null, renderLanes2);
          var nextState = workInProgress2.memoizedState;
          props = nextState.cache;
          pushProvider(workInProgress2, CacheContext, props);
          props !== prevState.cache && propagateContextChanges(
            workInProgress2,
            [CacheContext],
            renderLanes2,
            true
          );
          suspendIfUpdateReadFromEntangledAsyncAction();
          props = nextState.element;
          if (prevState.isDehydrated)
            if (prevState = {
              element: props,
              isDehydrated: false,
              cache: nextState.cache
            }, workInProgress2.updateQueue.baseState = prevState, workInProgress2.memoizedState = prevState, workInProgress2.flags & 256) {
              workInProgress2 = mountHostRootWithoutHydrating(
                current,
                workInProgress2,
                props,
                renderLanes2
              );
              break a;
            } else if (props !== $$typeof) {
              $$typeof = createCapturedValueAtFiber(
                Error(formatProdErrorMessage(424)),
                workInProgress2
              );
              queueHydrationError($$typeof);
              workInProgress2 = mountHostRootWithoutHydrating(
                current,
                workInProgress2,
                props,
                renderLanes2
              );
              break a;
            } else {
              current = workInProgress2.stateNode.containerInfo;
              switch (current.nodeType) {
                case 9:
                  current = current.body;
                  break;
                default:
                  current = "HTML" === current.nodeName ? current.ownerDocument.body : current;
              }
              nextHydratableInstance = getNextHydratable(current.firstChild);
              hydrationParentFiber = workInProgress2;
              isHydrating = true;
              hydrationErrors = null;
              rootOrSingletonContext = true;
              renderLanes2 = mountChildFibers(
                workInProgress2,
                null,
                props,
                renderLanes2
              );
              for (workInProgress2.child = renderLanes2; renderLanes2; )
                renderLanes2.flags = renderLanes2.flags & -3 | 4096, renderLanes2 = renderLanes2.sibling;
            }
          else {
            resetHydrationState();
            if (props === $$typeof) {
              workInProgress2 = bailoutOnAlreadyFinishedWork(
                current,
                workInProgress2,
                renderLanes2
              );
              break a;
            }
            reconcileChildren(current, workInProgress2, props, renderLanes2);
          }
          workInProgress2 = workInProgress2.child;
        }
        return workInProgress2;
      case 26:
        return markRef(current, workInProgress2), null === current ? (renderLanes2 = getResource(
          workInProgress2.type,
          null,
          workInProgress2.pendingProps,
          null
        )) ? workInProgress2.memoizedState = renderLanes2 : isHydrating || (renderLanes2 = workInProgress2.type, current = workInProgress2.pendingProps, props = getOwnerDocumentFromRootContainer(
          rootInstanceStackCursor.current
        ).createElement(renderLanes2), props[internalInstanceKey] = workInProgress2, props[internalPropsKey] = current, setInitialProperties(props, renderLanes2, current), markNodeAsHoistable(props), workInProgress2.stateNode = props) : workInProgress2.memoizedState = getResource(
          workInProgress2.type,
          current.memoizedProps,
          workInProgress2.pendingProps,
          current.memoizedState
        ), null;
      case 27:
        return pushHostContext(workInProgress2), null === current && isHydrating && (props = workInProgress2.stateNode = resolveSingletonInstance(
          workInProgress2.type,
          workInProgress2.pendingProps,
          rootInstanceStackCursor.current
        ), hydrationParentFiber = workInProgress2, rootOrSingletonContext = true, $$typeof = nextHydratableInstance, isSingletonScope(workInProgress2.type) ? (previousHydratableOnEnteringScopedSingleton = $$typeof, nextHydratableInstance = getNextHydratable(props.firstChild)) : nextHydratableInstance = $$typeof), reconcileChildren(
          current,
          workInProgress2,
          workInProgress2.pendingProps.children,
          renderLanes2
        ), markRef(current, workInProgress2), null === current && (workInProgress2.flags |= 4194304), workInProgress2.child;
      case 5:
        if (null === current && isHydrating) {
          if ($$typeof = props = nextHydratableInstance)
            props = canHydrateInstance(
              props,
              workInProgress2.type,
              workInProgress2.pendingProps,
              rootOrSingletonContext
            ), null !== props ? (workInProgress2.stateNode = props, hydrationParentFiber = workInProgress2, nextHydratableInstance = getNextHydratable(props.firstChild), rootOrSingletonContext = false, $$typeof = true) : $$typeof = false;
          $$typeof || throwOnHydrationMismatch(workInProgress2);
        }
        pushHostContext(workInProgress2);
        $$typeof = workInProgress2.type;
        prevState = workInProgress2.pendingProps;
        nextState = null !== current ? current.memoizedProps : null;
        props = prevState.children;
        shouldSetTextContent($$typeof, prevState) ? props = null : null !== nextState && shouldSetTextContent($$typeof, nextState) && (workInProgress2.flags |= 32);
        null !== workInProgress2.memoizedState && ($$typeof = renderWithHooks(
          current,
          workInProgress2,
          TransitionAwareHostComponent,
          null,
          null,
          renderLanes2
        ), HostTransitionContext._currentValue = $$typeof);
        markRef(current, workInProgress2);
        reconcileChildren(current, workInProgress2, props, renderLanes2);
        return workInProgress2.child;
      case 6:
        if (null === current && isHydrating) {
          if (current = renderLanes2 = nextHydratableInstance)
            renderLanes2 = canHydrateTextInstance(
              renderLanes2,
              workInProgress2.pendingProps,
              rootOrSingletonContext
            ), null !== renderLanes2 ? (workInProgress2.stateNode = renderLanes2, hydrationParentFiber = workInProgress2, nextHydratableInstance = null, current = true) : current = false;
          current || throwOnHydrationMismatch(workInProgress2);
        }
        return null;
      case 13:
        return updateSuspenseComponent(current, workInProgress2, renderLanes2);
      case 4:
        return pushHostContainer(
          workInProgress2,
          workInProgress2.stateNode.containerInfo
        ), props = workInProgress2.pendingProps, null === current ? workInProgress2.child = reconcileChildFibers(
          workInProgress2,
          null,
          props,
          renderLanes2
        ) : reconcileChildren(current, workInProgress2, props, renderLanes2), workInProgress2.child;
      case 11:
        return updateForwardRef(
          current,
          workInProgress2,
          workInProgress2.type,
          workInProgress2.pendingProps,
          renderLanes2
        );
      case 7:
        return reconcileChildren(
          current,
          workInProgress2,
          workInProgress2.pendingProps,
          renderLanes2
        ), workInProgress2.child;
      case 8:
        return reconcileChildren(
          current,
          workInProgress2,
          workInProgress2.pendingProps.children,
          renderLanes2
        ), workInProgress2.child;
      case 12:
        return reconcileChildren(
          current,
          workInProgress2,
          workInProgress2.pendingProps.children,
          renderLanes2
        ), workInProgress2.child;
      case 10:
        return props = workInProgress2.pendingProps, pushProvider(workInProgress2, workInProgress2.type, props.value), reconcileChildren(current, workInProgress2, props.children, renderLanes2), workInProgress2.child;
      case 9:
        return $$typeof = workInProgress2.type._context, props = workInProgress2.pendingProps.children, prepareToReadContext(workInProgress2), $$typeof = readContext($$typeof), props = props($$typeof), workInProgress2.flags |= 1, reconcileChildren(current, workInProgress2, props, renderLanes2), workInProgress2.child;
      case 14:
        return updateMemoComponent(
          current,
          workInProgress2,
          workInProgress2.type,
          workInProgress2.pendingProps,
          renderLanes2
        );
      case 15:
        return updateSimpleMemoComponent(
          current,
          workInProgress2,
          workInProgress2.type,
          workInProgress2.pendingProps,
          renderLanes2
        );
      case 19:
        return updateSuspenseListComponent(current, workInProgress2, renderLanes2);
      case 31:
        return updateActivityComponent(current, workInProgress2, renderLanes2);
      case 22:
        return updateOffscreenComponent(
          current,
          workInProgress2,
          renderLanes2,
          workInProgress2.pendingProps
        );
      case 24:
        return prepareToReadContext(workInProgress2), props = readContext(CacheContext), null === current ? ($$typeof = peekCacheFromPool(), null === $$typeof && ($$typeof = workInProgressRoot, prevState = createCache(), $$typeof.pooledCache = prevState, prevState.refCount++, null !== prevState && ($$typeof.pooledCacheLanes |= renderLanes2), $$typeof = prevState), workInProgress2.memoizedState = { parent: props, cache: $$typeof }, initializeUpdateQueue(workInProgress2), pushProvider(workInProgress2, CacheContext, $$typeof)) : (0 !== (current.lanes & renderLanes2) && (cloneUpdateQueue(current, workInProgress2), processUpdateQueue(workInProgress2, null, null, renderLanes2), suspendIfUpdateReadFromEntangledAsyncAction()), $$typeof = current.memoizedState, prevState = workInProgress2.memoizedState, $$typeof.parent !== props ? ($$typeof = { parent: props, cache: props }, workInProgress2.memoizedState = $$typeof, 0 === workInProgress2.lanes && (workInProgress2.memoizedState = workInProgress2.updateQueue.baseState = $$typeof), pushProvider(workInProgress2, CacheContext, props)) : (props = prevState.cache, pushProvider(workInProgress2, CacheContext, props), props !== $$typeof.cache && propagateContextChanges(
          workInProgress2,
          [CacheContext],
          renderLanes2,
          true
        ))), reconcileChildren(
          current,
          workInProgress2,
          workInProgress2.pendingProps.children,
          renderLanes2
        ), workInProgress2.child;
      case 29:
        throw workInProgress2.pendingProps;
    }
    throw Error(formatProdErrorMessage(156, workInProgress2.tag));
  }
  function markUpdate(workInProgress2) {
    workInProgress2.flags |= 4;
  }
  function preloadInstanceAndSuspendIfNeeded(workInProgress2, type, oldProps, newProps, renderLanes2) {
    if (type = 0 !== (workInProgress2.mode & 32)) type = false;
    if (type) {
      if (workInProgress2.flags |= 16777216, (renderLanes2 & 335544128) === renderLanes2)
        if (workInProgress2.stateNode.complete) workInProgress2.flags |= 8192;
        else if (shouldRemainOnPreviousScreen()) workInProgress2.flags |= 8192;
        else
          throw suspendedThenable = noopSuspenseyCommitThenable, SuspenseyCommitException;
    } else workInProgress2.flags &= -16777217;
  }
  function preloadResourceAndSuspendIfNeeded(workInProgress2, resource) {
    if ("stylesheet" !== resource.type || 0 !== (resource.state.loading & 4))
      workInProgress2.flags &= -16777217;
    else if (workInProgress2.flags |= 16777216, !preloadResource(resource))
      if (shouldRemainOnPreviousScreen()) workInProgress2.flags |= 8192;
      else
        throw suspendedThenable = noopSuspenseyCommitThenable, SuspenseyCommitException;
  }
  function scheduleRetryEffect(workInProgress2, retryQueue) {
    null !== retryQueue && (workInProgress2.flags |= 4);
    workInProgress2.flags & 16384 && (retryQueue = 22 !== workInProgress2.tag ? claimNextRetryLane() : 536870912, workInProgress2.lanes |= retryQueue, workInProgressSuspendedRetryLanes |= retryQueue);
  }
  function cutOffTailIfNeeded(renderState, hasRenderedATailFallback) {
    if (!isHydrating)
      switch (renderState.tailMode) {
        case "hidden":
          hasRenderedATailFallback = renderState.tail;
          for (var lastTailNode = null; null !== hasRenderedATailFallback; )
            null !== hasRenderedATailFallback.alternate && (lastTailNode = hasRenderedATailFallback), hasRenderedATailFallback = hasRenderedATailFallback.sibling;
          null === lastTailNode ? renderState.tail = null : lastTailNode.sibling = null;
          break;
        case "collapsed":
          lastTailNode = renderState.tail;
          for (var lastTailNode$106 = null; null !== lastTailNode; )
            null !== lastTailNode.alternate && (lastTailNode$106 = lastTailNode), lastTailNode = lastTailNode.sibling;
          null === lastTailNode$106 ? hasRenderedATailFallback || null === renderState.tail ? renderState.tail = null : renderState.tail.sibling = null : lastTailNode$106.sibling = null;
      }
  }
  function bubbleProperties(completedWork) {
    var didBailout = null !== completedWork.alternate && completedWork.alternate.child === completedWork.child, newChildLanes = 0, subtreeFlags = 0;
    if (didBailout)
      for (var child$107 = completedWork.child; null !== child$107; )
        newChildLanes |= child$107.lanes | child$107.childLanes, subtreeFlags |= child$107.subtreeFlags & 65011712, subtreeFlags |= child$107.flags & 65011712, child$107.return = completedWork, child$107 = child$107.sibling;
    else
      for (child$107 = completedWork.child; null !== child$107; )
        newChildLanes |= child$107.lanes | child$107.childLanes, subtreeFlags |= child$107.subtreeFlags, subtreeFlags |= child$107.flags, child$107.return = completedWork, child$107 = child$107.sibling;
    completedWork.subtreeFlags |= subtreeFlags;
    completedWork.childLanes = newChildLanes;
    return didBailout;
  }
  function completeWork(current, workInProgress2, renderLanes2) {
    var newProps = workInProgress2.pendingProps;
    popTreeContext(workInProgress2);
    switch (workInProgress2.tag) {
      case 16:
      case 15:
      case 0:
      case 11:
      case 7:
      case 8:
      case 12:
      case 9:
      case 14:
        return bubbleProperties(workInProgress2), null;
      case 1:
        return bubbleProperties(workInProgress2), null;
      case 3:
        renderLanes2 = workInProgress2.stateNode;
        newProps = null;
        null !== current && (newProps = current.memoizedState.cache);
        workInProgress2.memoizedState.cache !== newProps && (workInProgress2.flags |= 2048);
        popProvider(CacheContext);
        popHostContainer();
        renderLanes2.pendingContext && (renderLanes2.context = renderLanes2.pendingContext, renderLanes2.pendingContext = null);
        if (null === current || null === current.child)
          popHydrationState(workInProgress2) ? markUpdate(workInProgress2) : null === current || current.memoizedState.isDehydrated && 0 === (workInProgress2.flags & 256) || (workInProgress2.flags |= 1024, upgradeHydrationErrorsToRecoverable());
        bubbleProperties(workInProgress2);
        return null;
      case 26:
        var type = workInProgress2.type, nextResource = workInProgress2.memoizedState;
        null === current ? (markUpdate(workInProgress2), null !== nextResource ? (bubbleProperties(workInProgress2), preloadResourceAndSuspendIfNeeded(workInProgress2, nextResource)) : (bubbleProperties(workInProgress2), preloadInstanceAndSuspendIfNeeded(
          workInProgress2,
          type,
          null,
          newProps,
          renderLanes2
        ))) : nextResource ? nextResource !== current.memoizedState ? (markUpdate(workInProgress2), bubbleProperties(workInProgress2), preloadResourceAndSuspendIfNeeded(workInProgress2, nextResource)) : (bubbleProperties(workInProgress2), workInProgress2.flags &= -16777217) : (current = current.memoizedProps, current !== newProps && markUpdate(workInProgress2), bubbleProperties(workInProgress2), preloadInstanceAndSuspendIfNeeded(
          workInProgress2,
          type,
          current,
          newProps,
          renderLanes2
        ));
        return null;
      case 27:
        popHostContext(workInProgress2);
        renderLanes2 = rootInstanceStackCursor.current;
        type = workInProgress2.type;
        if (null !== current && null != workInProgress2.stateNode)
          current.memoizedProps !== newProps && markUpdate(workInProgress2);
        else {
          if (!newProps) {
            if (null === workInProgress2.stateNode)
              throw Error(formatProdErrorMessage(166));
            bubbleProperties(workInProgress2);
            return null;
          }
          current = contextStackCursor.current;
          popHydrationState(workInProgress2) ? prepareToHydrateHostInstance(workInProgress2) : (current = resolveSingletonInstance(type, newProps, renderLanes2), workInProgress2.stateNode = current, markUpdate(workInProgress2));
        }
        bubbleProperties(workInProgress2);
        return null;
      case 5:
        popHostContext(workInProgress2);
        type = workInProgress2.type;
        if (null !== current && null != workInProgress2.stateNode)
          current.memoizedProps !== newProps && markUpdate(workInProgress2);
        else {
          if (!newProps) {
            if (null === workInProgress2.stateNode)
              throw Error(formatProdErrorMessage(166));
            bubbleProperties(workInProgress2);
            return null;
          }
          nextResource = contextStackCursor.current;
          if (popHydrationState(workInProgress2))
            prepareToHydrateHostInstance(workInProgress2);
          else {
            var ownerDocument = getOwnerDocumentFromRootContainer(
              rootInstanceStackCursor.current
            );
            switch (nextResource) {
              case 1:
                nextResource = ownerDocument.createElementNS(
                  "http://www.w3.org/2000/svg",
                  type
                );
                break;
              case 2:
                nextResource = ownerDocument.createElementNS(
                  "http://www.w3.org/1998/Math/MathML",
                  type
                );
                break;
              default:
                switch (type) {
                  case "svg":
                    nextResource = ownerDocument.createElementNS(
                      "http://www.w3.org/2000/svg",
                      type
                    );
                    break;
                  case "math":
                    nextResource = ownerDocument.createElementNS(
                      "http://www.w3.org/1998/Math/MathML",
                      type
                    );
                    break;
                  case "script":
                    nextResource = ownerDocument.createElement("div");
                    nextResource.innerHTML = "<script><\/script>";
                    nextResource = nextResource.removeChild(
                      nextResource.firstChild
                    );
                    break;
                  case "select":
                    nextResource = "string" === typeof newProps.is ? ownerDocument.createElement("select", {
                      is: newProps.is
                    }) : ownerDocument.createElement("select");
                    newProps.multiple ? nextResource.multiple = true : newProps.size && (nextResource.size = newProps.size);
                    break;
                  default:
                    nextResource = "string" === typeof newProps.is ? ownerDocument.createElement(type, { is: newProps.is }) : ownerDocument.createElement(type);
                }
            }
            nextResource[internalInstanceKey] = workInProgress2;
            nextResource[internalPropsKey] = newProps;
            a: for (ownerDocument = workInProgress2.child; null !== ownerDocument; ) {
              if (5 === ownerDocument.tag || 6 === ownerDocument.tag)
                nextResource.appendChild(ownerDocument.stateNode);
              else if (4 !== ownerDocument.tag && 27 !== ownerDocument.tag && null !== ownerDocument.child) {
                ownerDocument.child.return = ownerDocument;
                ownerDocument = ownerDocument.child;
                continue;
              }
              if (ownerDocument === workInProgress2) break a;
              for (; null === ownerDocument.sibling; ) {
                if (null === ownerDocument.return || ownerDocument.return === workInProgress2)
                  break a;
                ownerDocument = ownerDocument.return;
              }
              ownerDocument.sibling.return = ownerDocument.return;
              ownerDocument = ownerDocument.sibling;
            }
            workInProgress2.stateNode = nextResource;
            a: switch (setInitialProperties(nextResource, type, newProps), type) {
              case "button":
              case "input":
              case "select":
              case "textarea":
                newProps = !!newProps.autoFocus;
                break a;
              case "img":
                newProps = true;
                break a;
              default:
                newProps = false;
            }
            newProps && markUpdate(workInProgress2);
          }
        }
        bubbleProperties(workInProgress2);
        preloadInstanceAndSuspendIfNeeded(
          workInProgress2,
          workInProgress2.type,
          null === current ? null : current.memoizedProps,
          workInProgress2.pendingProps,
          renderLanes2
        );
        return null;
      case 6:
        if (current && null != workInProgress2.stateNode)
          current.memoizedProps !== newProps && markUpdate(workInProgress2);
        else {
          if ("string" !== typeof newProps && null === workInProgress2.stateNode)
            throw Error(formatProdErrorMessage(166));
          current = rootInstanceStackCursor.current;
          if (popHydrationState(workInProgress2)) {
            current = workInProgress2.stateNode;
            renderLanes2 = workInProgress2.memoizedProps;
            newProps = null;
            type = hydrationParentFiber;
            if (null !== type)
              switch (type.tag) {
                case 27:
                case 5:
                  newProps = type.memoizedProps;
              }
            current[internalInstanceKey] = workInProgress2;
            current = current.nodeValue === renderLanes2 || null !== newProps && true === newProps.suppressHydrationWarning || checkForUnmatchedText(current.nodeValue, renderLanes2) ? true : false;
            current || throwOnHydrationMismatch(workInProgress2, true);
          } else
            current = getOwnerDocumentFromRootContainer(current).createTextNode(
              newProps
            ), current[internalInstanceKey] = workInProgress2, workInProgress2.stateNode = current;
        }
        bubbleProperties(workInProgress2);
        return null;
      case 31:
        renderLanes2 = workInProgress2.memoizedState;
        if (null === current || null !== current.memoizedState) {
          newProps = popHydrationState(workInProgress2);
          if (null !== renderLanes2) {
            if (null === current) {
              if (!newProps) throw Error(formatProdErrorMessage(318));
              current = workInProgress2.memoizedState;
              current = null !== current ? current.dehydrated : null;
              if (!current) throw Error(formatProdErrorMessage(557));
              current[internalInstanceKey] = workInProgress2;
            } else
              resetHydrationState(), 0 === (workInProgress2.flags & 128) && (workInProgress2.memoizedState = null), workInProgress2.flags |= 4;
            bubbleProperties(workInProgress2);
            current = false;
          } else
            renderLanes2 = upgradeHydrationErrorsToRecoverable(), null !== current && null !== current.memoizedState && (current.memoizedState.hydrationErrors = renderLanes2), current = true;
          if (!current) {
            if (workInProgress2.flags & 256)
              return popSuspenseHandler(workInProgress2), workInProgress2;
            popSuspenseHandler(workInProgress2);
            return null;
          }
          if (0 !== (workInProgress2.flags & 128))
            throw Error(formatProdErrorMessage(558));
        }
        bubbleProperties(workInProgress2);
        return null;
      case 13:
        newProps = workInProgress2.memoizedState;
        if (null === current || null !== current.memoizedState && null !== current.memoizedState.dehydrated) {
          type = popHydrationState(workInProgress2);
          if (null !== newProps && null !== newProps.dehydrated) {
            if (null === current) {
              if (!type) throw Error(formatProdErrorMessage(318));
              type = workInProgress2.memoizedState;
              type = null !== type ? type.dehydrated : null;
              if (!type) throw Error(formatProdErrorMessage(317));
              type[internalInstanceKey] = workInProgress2;
            } else
              resetHydrationState(), 0 === (workInProgress2.flags & 128) && (workInProgress2.memoizedState = null), workInProgress2.flags |= 4;
            bubbleProperties(workInProgress2);
            type = false;
          } else
            type = upgradeHydrationErrorsToRecoverable(), null !== current && null !== current.memoizedState && (current.memoizedState.hydrationErrors = type), type = true;
          if (!type) {
            if (workInProgress2.flags & 256)
              return popSuspenseHandler(workInProgress2), workInProgress2;
            popSuspenseHandler(workInProgress2);
            return null;
          }
        }
        popSuspenseHandler(workInProgress2);
        if (0 !== (workInProgress2.flags & 128))
          return workInProgress2.lanes = renderLanes2, workInProgress2;
        renderLanes2 = null !== newProps;
        current = null !== current && null !== current.memoizedState;
        renderLanes2 && (newProps = workInProgress2.child, type = null, null !== newProps.alternate && null !== newProps.alternate.memoizedState && null !== newProps.alternate.memoizedState.cachePool && (type = newProps.alternate.memoizedState.cachePool.pool), nextResource = null, null !== newProps.memoizedState && null !== newProps.memoizedState.cachePool && (nextResource = newProps.memoizedState.cachePool.pool), nextResource !== type && (newProps.flags |= 2048));
        renderLanes2 !== current && renderLanes2 && (workInProgress2.child.flags |= 8192);
        scheduleRetryEffect(workInProgress2, workInProgress2.updateQueue);
        bubbleProperties(workInProgress2);
        return null;
      case 4:
        return popHostContainer(), null === current && listenToAllSupportedEvents(workInProgress2.stateNode.containerInfo), bubbleProperties(workInProgress2), null;
      case 10:
        return popProvider(workInProgress2.type), bubbleProperties(workInProgress2), null;
      case 19:
        pop(suspenseStackCursor);
        newProps = workInProgress2.memoizedState;
        if (null === newProps) return bubbleProperties(workInProgress2), null;
        type = 0 !== (workInProgress2.flags & 128);
        nextResource = newProps.rendering;
        if (null === nextResource)
          if (type) cutOffTailIfNeeded(newProps, false);
          else {
            if (0 !== workInProgressRootExitStatus || null !== current && 0 !== (current.flags & 128))
              for (current = workInProgress2.child; null !== current; ) {
                nextResource = findFirstSuspended(current);
                if (null !== nextResource) {
                  workInProgress2.flags |= 128;
                  cutOffTailIfNeeded(newProps, false);
                  current = nextResource.updateQueue;
                  workInProgress2.updateQueue = current;
                  scheduleRetryEffect(workInProgress2, current);
                  workInProgress2.subtreeFlags = 0;
                  current = renderLanes2;
                  for (renderLanes2 = workInProgress2.child; null !== renderLanes2; )
                    resetWorkInProgress(renderLanes2, current), renderLanes2 = renderLanes2.sibling;
                  push(
                    suspenseStackCursor,
                    suspenseStackCursor.current & 1 | 2
                  );
                  isHydrating && pushTreeFork(workInProgress2, newProps.treeForkCount);
                  return workInProgress2.child;
                }
                current = current.sibling;
              }
            null !== newProps.tail && now() > workInProgressRootRenderTargetTime && (workInProgress2.flags |= 128, type = true, cutOffTailIfNeeded(newProps, false), workInProgress2.lanes = 4194304);
          }
        else {
          if (!type)
            if (current = findFirstSuspended(nextResource), null !== current) {
              if (workInProgress2.flags |= 128, type = true, current = current.updateQueue, workInProgress2.updateQueue = current, scheduleRetryEffect(workInProgress2, current), cutOffTailIfNeeded(newProps, true), null === newProps.tail && "hidden" === newProps.tailMode && !nextResource.alternate && !isHydrating)
                return bubbleProperties(workInProgress2), null;
            } else
              2 * now() - newProps.renderingStartTime > workInProgressRootRenderTargetTime && 536870912 !== renderLanes2 && (workInProgress2.flags |= 128, type = true, cutOffTailIfNeeded(newProps, false), workInProgress2.lanes = 4194304);
          newProps.isBackwards ? (nextResource.sibling = workInProgress2.child, workInProgress2.child = nextResource) : (current = newProps.last, null !== current ? current.sibling = nextResource : workInProgress2.child = nextResource, newProps.last = nextResource);
        }
        if (null !== newProps.tail)
          return current = newProps.tail, newProps.rendering = current, newProps.tail = current.sibling, newProps.renderingStartTime = now(), current.sibling = null, renderLanes2 = suspenseStackCursor.current, push(
            suspenseStackCursor,
            type ? renderLanes2 & 1 | 2 : renderLanes2 & 1
          ), isHydrating && pushTreeFork(workInProgress2, newProps.treeForkCount), current;
        bubbleProperties(workInProgress2);
        return null;
      case 22:
      case 23:
        return popSuspenseHandler(workInProgress2), popHiddenContext(), newProps = null !== workInProgress2.memoizedState, null !== current ? null !== current.memoizedState !== newProps && (workInProgress2.flags |= 8192) : newProps && (workInProgress2.flags |= 8192), newProps ? 0 !== (renderLanes2 & 536870912) && 0 === (workInProgress2.flags & 128) && (bubbleProperties(workInProgress2), workInProgress2.subtreeFlags & 6 && (workInProgress2.flags |= 8192)) : bubbleProperties(workInProgress2), renderLanes2 = workInProgress2.updateQueue, null !== renderLanes2 && scheduleRetryEffect(workInProgress2, renderLanes2.retryQueue), renderLanes2 = null, null !== current && null !== current.memoizedState && null !== current.memoizedState.cachePool && (renderLanes2 = current.memoizedState.cachePool.pool), newProps = null, null !== workInProgress2.memoizedState && null !== workInProgress2.memoizedState.cachePool && (newProps = workInProgress2.memoizedState.cachePool.pool), newProps !== renderLanes2 && (workInProgress2.flags |= 2048), null !== current && pop(resumedCache), null;
      case 24:
        return renderLanes2 = null, null !== current && (renderLanes2 = current.memoizedState.cache), workInProgress2.memoizedState.cache !== renderLanes2 && (workInProgress2.flags |= 2048), popProvider(CacheContext), bubbleProperties(workInProgress2), null;
      case 25:
        return null;
      case 30:
        return null;
    }
    throw Error(formatProdErrorMessage(156, workInProgress2.tag));
  }
  function unwindWork(current, workInProgress2) {
    popTreeContext(workInProgress2);
    switch (workInProgress2.tag) {
      case 1:
        return current = workInProgress2.flags, current & 65536 ? (workInProgress2.flags = current & -65537 | 128, workInProgress2) : null;
      case 3:
        return popProvider(CacheContext), popHostContainer(), current = workInProgress2.flags, 0 !== (current & 65536) && 0 === (current & 128) ? (workInProgress2.flags = current & -65537 | 128, workInProgress2) : null;
      case 26:
      case 27:
      case 5:
        return popHostContext(workInProgress2), null;
      case 31:
        if (null !== workInProgress2.memoizedState) {
          popSuspenseHandler(workInProgress2);
          if (null === workInProgress2.alternate)
            throw Error(formatProdErrorMessage(340));
          resetHydrationState();
        }
        current = workInProgress2.flags;
        return current & 65536 ? (workInProgress2.flags = current & -65537 | 128, workInProgress2) : null;
      case 13:
        popSuspenseHandler(workInProgress2);
        current = workInProgress2.memoizedState;
        if (null !== current && null !== current.dehydrated) {
          if (null === workInProgress2.alternate)
            throw Error(formatProdErrorMessage(340));
          resetHydrationState();
        }
        current = workInProgress2.flags;
        return current & 65536 ? (workInProgress2.flags = current & -65537 | 128, workInProgress2) : null;
      case 19:
        return pop(suspenseStackCursor), null;
      case 4:
        return popHostContainer(), null;
      case 10:
        return popProvider(workInProgress2.type), null;
      case 22:
      case 23:
        return popSuspenseHandler(workInProgress2), popHiddenContext(), null !== current && pop(resumedCache), current = workInProgress2.flags, current & 65536 ? (workInProgress2.flags = current & -65537 | 128, workInProgress2) : null;
      case 24:
        return popProvider(CacheContext), null;
      case 25:
        return null;
      default:
        return null;
    }
  }
  function unwindInterruptedWork(current, interruptedWork) {
    popTreeContext(interruptedWork);
    switch (interruptedWork.tag) {
      case 3:
        popProvider(CacheContext);
        popHostContainer();
        break;
      case 26:
      case 27:
      case 5:
        popHostContext(interruptedWork);
        break;
      case 4:
        popHostContainer();
        break;
      case 31:
        null !== interruptedWork.memoizedState && popSuspenseHandler(interruptedWork);
        break;
      case 13:
        popSuspenseHandler(interruptedWork);
        break;
      case 19:
        pop(suspenseStackCursor);
        break;
      case 10:
        popProvider(interruptedWork.type);
        break;
      case 22:
      case 23:
        popSuspenseHandler(interruptedWork);
        popHiddenContext();
        null !== current && pop(resumedCache);
        break;
      case 24:
        popProvider(CacheContext);
    }
  }
  function commitHookEffectListMount(flags, finishedWork) {
    try {
      var updateQueue = finishedWork.updateQueue, lastEffect = null !== updateQueue ? updateQueue.lastEffect : null;
      if (null !== lastEffect) {
        var firstEffect = lastEffect.next;
        updateQueue = firstEffect;
        do {
          if ((updateQueue.tag & flags) === flags) {
            lastEffect = void 0;
            var create = updateQueue.create, inst = updateQueue.inst;
            lastEffect = create();
            inst.destroy = lastEffect;
          }
          updateQueue = updateQueue.next;
        } while (updateQueue !== firstEffect);
      }
    } catch (error) {
      captureCommitPhaseError(finishedWork, finishedWork.return, error);
    }
  }
  function commitHookEffectListUnmount(flags, finishedWork, nearestMountedAncestor$jscomp$0) {
    try {
      var updateQueue = finishedWork.updateQueue, lastEffect = null !== updateQueue ? updateQueue.lastEffect : null;
      if (null !== lastEffect) {
        var firstEffect = lastEffect.next;
        updateQueue = firstEffect;
        do {
          if ((updateQueue.tag & flags) === flags) {
            var inst = updateQueue.inst, destroy = inst.destroy;
            if (void 0 !== destroy) {
              inst.destroy = void 0;
              lastEffect = finishedWork;
              var nearestMountedAncestor = nearestMountedAncestor$jscomp$0, destroy_ = destroy;
              try {
                destroy_();
              } catch (error) {
                captureCommitPhaseError(
                  lastEffect,
                  nearestMountedAncestor,
                  error
                );
              }
            }
          }
          updateQueue = updateQueue.next;
        } while (updateQueue !== firstEffect);
      }
    } catch (error) {
      captureCommitPhaseError(finishedWork, finishedWork.return, error);
    }
  }
  function commitClassCallbacks(finishedWork) {
    var updateQueue = finishedWork.updateQueue;
    if (null !== updateQueue) {
      var instance = finishedWork.stateNode;
      try {
        commitCallbacks(updateQueue, instance);
      } catch (error) {
        captureCommitPhaseError(finishedWork, finishedWork.return, error);
      }
    }
  }
  function safelyCallComponentWillUnmount(current, nearestMountedAncestor, instance) {
    instance.props = resolveClassComponentProps(
      current.type,
      current.memoizedProps
    );
    instance.state = current.memoizedState;
    try {
      instance.componentWillUnmount();
    } catch (error) {
      captureCommitPhaseError(current, nearestMountedAncestor, error);
    }
  }
  function safelyAttachRef(current, nearestMountedAncestor) {
    try {
      var ref = current.ref;
      if (null !== ref) {
        switch (current.tag) {
          case 26:
          case 27:
          case 5:
            var instanceToUse = current.stateNode;
            break;
          case 30:
            instanceToUse = current.stateNode;
            break;
          default:
            instanceToUse = current.stateNode;
        }
        "function" === typeof ref ? current.refCleanup = ref(instanceToUse) : ref.current = instanceToUse;
      }
    } catch (error) {
      captureCommitPhaseError(current, nearestMountedAncestor, error);
    }
  }
  function safelyDetachRef(current, nearestMountedAncestor) {
    var ref = current.ref, refCleanup = current.refCleanup;
    if (null !== ref)
      if ("function" === typeof refCleanup)
        try {
          refCleanup();
        } catch (error) {
          captureCommitPhaseError(current, nearestMountedAncestor, error);
        } finally {
          current.refCleanup = null, current = current.alternate, null != current && (current.refCleanup = null);
        }
      else if ("function" === typeof ref)
        try {
          ref(null);
        } catch (error$140) {
          captureCommitPhaseError(current, nearestMountedAncestor, error$140);
        }
      else ref.current = null;
  }
  function commitHostMount(finishedWork) {
    var type = finishedWork.type, props = finishedWork.memoizedProps, instance = finishedWork.stateNode;
    try {
      a: switch (type) {
        case "button":
        case "input":
        case "select":
        case "textarea":
          props.autoFocus && instance.focus();
          break a;
        case "img":
          props.src ? instance.src = props.src : props.srcSet && (instance.srcset = props.srcSet);
      }
    } catch (error) {
      captureCommitPhaseError(finishedWork, finishedWork.return, error);
    }
  }
  function commitHostUpdate(finishedWork, newProps, oldProps) {
    try {
      var domElement = finishedWork.stateNode;
      updateProperties(domElement, finishedWork.type, oldProps, newProps);
      domElement[internalPropsKey] = newProps;
    } catch (error) {
      captureCommitPhaseError(finishedWork, finishedWork.return, error);
    }
  }
  function isHostParent(fiber) {
    return 5 === fiber.tag || 3 === fiber.tag || 26 === fiber.tag || 27 === fiber.tag && isSingletonScope(fiber.type) || 4 === fiber.tag;
  }
  function getHostSibling(fiber) {
    a: for (; ; ) {
      for (; null === fiber.sibling; ) {
        if (null === fiber.return || isHostParent(fiber.return)) return null;
        fiber = fiber.return;
      }
      fiber.sibling.return = fiber.return;
      for (fiber = fiber.sibling; 5 !== fiber.tag && 6 !== fiber.tag && 18 !== fiber.tag; ) {
        if (27 === fiber.tag && isSingletonScope(fiber.type)) continue a;
        if (fiber.flags & 2) continue a;
        if (null === fiber.child || 4 === fiber.tag) continue a;
        else fiber.child.return = fiber, fiber = fiber.child;
      }
      if (!(fiber.flags & 2)) return fiber.stateNode;
    }
  }
  function insertOrAppendPlacementNodeIntoContainer(node, before, parent) {
    var tag = node.tag;
    if (5 === tag || 6 === tag)
      node = node.stateNode, before ? (9 === parent.nodeType ? parent.body : "HTML" === parent.nodeName ? parent.ownerDocument.body : parent).insertBefore(node, before) : (before = 9 === parent.nodeType ? parent.body : "HTML" === parent.nodeName ? parent.ownerDocument.body : parent, before.appendChild(node), parent = parent._reactRootContainer, null !== parent && void 0 !== parent || null !== before.onclick || (before.onclick = noop$1));
    else if (4 !== tag && (27 === tag && isSingletonScope(node.type) && (parent = node.stateNode, before = null), node = node.child, null !== node))
      for (insertOrAppendPlacementNodeIntoContainer(node, before, parent), node = node.sibling; null !== node; )
        insertOrAppendPlacementNodeIntoContainer(node, before, parent), node = node.sibling;
  }
  function insertOrAppendPlacementNode(node, before, parent) {
    var tag = node.tag;
    if (5 === tag || 6 === tag)
      node = node.stateNode, before ? parent.insertBefore(node, before) : parent.appendChild(node);
    else if (4 !== tag && (27 === tag && isSingletonScope(node.type) && (parent = node.stateNode), node = node.child, null !== node))
      for (insertOrAppendPlacementNode(node, before, parent), node = node.sibling; null !== node; )
        insertOrAppendPlacementNode(node, before, parent), node = node.sibling;
  }
  function commitHostSingletonAcquisition(finishedWork) {
    var singleton = finishedWork.stateNode, props = finishedWork.memoizedProps;
    try {
      for (var type = finishedWork.type, attributes = singleton.attributes; attributes.length; )
        singleton.removeAttributeNode(attributes[0]);
      setInitialProperties(singleton, type, props);
      singleton[internalInstanceKey] = finishedWork;
      singleton[internalPropsKey] = props;
    } catch (error) {
      captureCommitPhaseError(finishedWork, finishedWork.return, error);
    }
  }
  var offscreenSubtreeIsHidden = false, offscreenSubtreeWasHidden = false, needsFormReset = false, PossiblyWeakSet = "function" === typeof WeakSet ? WeakSet : Set, nextEffect = null;
  function commitBeforeMutationEffects(root2, firstChild) {
    root2 = root2.containerInfo;
    eventsEnabled = _enabled;
    root2 = getActiveElementDeep(root2);
    if (hasSelectionCapabilities(root2)) {
      if ("selectionStart" in root2)
        var JSCompiler_temp = {
          start: root2.selectionStart,
          end: root2.selectionEnd
        };
      else
        a: {
          JSCompiler_temp = (JSCompiler_temp = root2.ownerDocument) && JSCompiler_temp.defaultView || window;
          var selection = JSCompiler_temp.getSelection && JSCompiler_temp.getSelection();
          if (selection && 0 !== selection.rangeCount) {
            JSCompiler_temp = selection.anchorNode;
            var anchorOffset = selection.anchorOffset, focusNode = selection.focusNode;
            selection = selection.focusOffset;
            try {
              JSCompiler_temp.nodeType, focusNode.nodeType;
            } catch (e$20) {
              JSCompiler_temp = null;
              break a;
            }
            var length = 0, start = -1, end = -1, indexWithinAnchor = 0, indexWithinFocus = 0, node = root2, parentNode = null;
            b: for (; ; ) {
              for (var next; ; ) {
                node !== JSCompiler_temp || 0 !== anchorOffset && 3 !== node.nodeType || (start = length + anchorOffset);
                node !== focusNode || 0 !== selection && 3 !== node.nodeType || (end = length + selection);
                3 === node.nodeType && (length += node.nodeValue.length);
                if (null === (next = node.firstChild)) break;
                parentNode = node;
                node = next;
              }
              for (; ; ) {
                if (node === root2) break b;
                parentNode === JSCompiler_temp && ++indexWithinAnchor === anchorOffset && (start = length);
                parentNode === focusNode && ++indexWithinFocus === selection && (end = length);
                if (null !== (next = node.nextSibling)) break;
                node = parentNode;
                parentNode = node.parentNode;
              }
              node = next;
            }
            JSCompiler_temp = -1 === start || -1 === end ? null : { start, end };
          } else JSCompiler_temp = null;
        }
      JSCompiler_temp = JSCompiler_temp || { start: 0, end: 0 };
    } else JSCompiler_temp = null;
    selectionInformation = { focusedElem: root2, selectionRange: JSCompiler_temp };
    _enabled = false;
    for (nextEffect = firstChild; null !== nextEffect; )
      if (firstChild = nextEffect, root2 = firstChild.child, 0 !== (firstChild.subtreeFlags & 1028) && null !== root2)
        root2.return = firstChild, nextEffect = root2;
      else
        for (; null !== nextEffect; ) {
          firstChild = nextEffect;
          focusNode = firstChild.alternate;
          root2 = firstChild.flags;
          switch (firstChild.tag) {
            case 0:
              if (0 !== (root2 & 4) && (root2 = firstChild.updateQueue, root2 = null !== root2 ? root2.events : null, null !== root2))
                for (JSCompiler_temp = 0; JSCompiler_temp < root2.length; JSCompiler_temp++)
                  anchorOffset = root2[JSCompiler_temp], anchorOffset.ref.impl = anchorOffset.nextImpl;
              break;
            case 11:
            case 15:
              break;
            case 1:
              if (0 !== (root2 & 1024) && null !== focusNode) {
                root2 = void 0;
                JSCompiler_temp = firstChild;
                anchorOffset = focusNode.memoizedProps;
                focusNode = focusNode.memoizedState;
                selection = JSCompiler_temp.stateNode;
                try {
                  var resolvedPrevProps = resolveClassComponentProps(
                    JSCompiler_temp.type,
                    anchorOffset
                  );
                  root2 = selection.getSnapshotBeforeUpdate(
                    resolvedPrevProps,
                    focusNode
                  );
                  selection.__reactInternalSnapshotBeforeUpdate = root2;
                } catch (error) {
                  captureCommitPhaseError(
                    JSCompiler_temp,
                    JSCompiler_temp.return,
                    error
                  );
                }
              }
              break;
            case 3:
              if (0 !== (root2 & 1024)) {
                if (root2 = firstChild.stateNode.containerInfo, JSCompiler_temp = root2.nodeType, 9 === JSCompiler_temp)
                  clearContainerSparingly(root2);
                else if (1 === JSCompiler_temp)
                  switch (root2.nodeName) {
                    case "HEAD":
                    case "HTML":
                    case "BODY":
                      clearContainerSparingly(root2);
                      break;
                    default:
                      root2.textContent = "";
                  }
              }
              break;
            case 5:
            case 26:
            case 27:
            case 6:
            case 4:
            case 17:
              break;
            default:
              if (0 !== (root2 & 1024)) throw Error(formatProdErrorMessage(163));
          }
          root2 = firstChild.sibling;
          if (null !== root2) {
            root2.return = firstChild.return;
            nextEffect = root2;
            break;
          }
          nextEffect = firstChild.return;
        }
  }
  function commitLayoutEffectOnFiber(finishedRoot, current, finishedWork) {
    var flags = finishedWork.flags;
    switch (finishedWork.tag) {
      case 0:
      case 11:
      case 15:
        recursivelyTraverseLayoutEffects(finishedRoot, finishedWork);
        flags & 4 && commitHookEffectListMount(5, finishedWork);
        break;
      case 1:
        recursivelyTraverseLayoutEffects(finishedRoot, finishedWork);
        if (flags & 4)
          if (finishedRoot = finishedWork.stateNode, null === current)
            try {
              finishedRoot.componentDidMount();
            } catch (error) {
              captureCommitPhaseError(finishedWork, finishedWork.return, error);
            }
          else {
            var prevProps = resolveClassComponentProps(
              finishedWork.type,
              current.memoizedProps
            );
            current = current.memoizedState;
            try {
              finishedRoot.componentDidUpdate(
                prevProps,
                current,
                finishedRoot.__reactInternalSnapshotBeforeUpdate
              );
            } catch (error$139) {
              captureCommitPhaseError(
                finishedWork,
                finishedWork.return,
                error$139
              );
            }
          }
        flags & 64 && commitClassCallbacks(finishedWork);
        flags & 512 && safelyAttachRef(finishedWork, finishedWork.return);
        break;
      case 3:
        recursivelyTraverseLayoutEffects(finishedRoot, finishedWork);
        if (flags & 64 && (finishedRoot = finishedWork.updateQueue, null !== finishedRoot)) {
          current = null;
          if (null !== finishedWork.child)
            switch (finishedWork.child.tag) {
              case 27:
              case 5:
                current = finishedWork.child.stateNode;
                break;
              case 1:
                current = finishedWork.child.stateNode;
            }
          try {
            commitCallbacks(finishedRoot, current);
          } catch (error) {
            captureCommitPhaseError(finishedWork, finishedWork.return, error);
          }
        }
        break;
      case 27:
        null === current && flags & 4 && commitHostSingletonAcquisition(finishedWork);
      case 26:
      case 5:
        recursivelyTraverseLayoutEffects(finishedRoot, finishedWork);
        null === current && flags & 4 && commitHostMount(finishedWork);
        flags & 512 && safelyAttachRef(finishedWork, finishedWork.return);
        break;
      case 12:
        recursivelyTraverseLayoutEffects(finishedRoot, finishedWork);
        break;
      case 31:
        recursivelyTraverseLayoutEffects(finishedRoot, finishedWork);
        flags & 4 && commitActivityHydrationCallbacks(finishedRoot, finishedWork);
        break;
      case 13:
        recursivelyTraverseLayoutEffects(finishedRoot, finishedWork);
        flags & 4 && commitSuspenseHydrationCallbacks(finishedRoot, finishedWork);
        flags & 64 && (finishedRoot = finishedWork.memoizedState, null !== finishedRoot && (finishedRoot = finishedRoot.dehydrated, null !== finishedRoot && (finishedWork = retryDehydratedSuspenseBoundary.bind(
          null,
          finishedWork
        ), registerSuspenseInstanceRetry(finishedRoot, finishedWork))));
        break;
      case 22:
        flags = null !== finishedWork.memoizedState || offscreenSubtreeIsHidden;
        if (!flags) {
          current = null !== current && null !== current.memoizedState || offscreenSubtreeWasHidden;
          prevProps = offscreenSubtreeIsHidden;
          var prevOffscreenSubtreeWasHidden = offscreenSubtreeWasHidden;
          offscreenSubtreeIsHidden = flags;
          (offscreenSubtreeWasHidden = current) && !prevOffscreenSubtreeWasHidden ? recursivelyTraverseReappearLayoutEffects(
            finishedRoot,
            finishedWork,
            0 !== (finishedWork.subtreeFlags & 8772)
          ) : recursivelyTraverseLayoutEffects(finishedRoot, finishedWork);
          offscreenSubtreeIsHidden = prevProps;
          offscreenSubtreeWasHidden = prevOffscreenSubtreeWasHidden;
        }
        break;
      case 30:
        break;
      default:
        recursivelyTraverseLayoutEffects(finishedRoot, finishedWork);
    }
  }
  function detachFiberAfterEffects(fiber) {
    var alternate = fiber.alternate;
    null !== alternate && (fiber.alternate = null, detachFiberAfterEffects(alternate));
    fiber.child = null;
    fiber.deletions = null;
    fiber.sibling = null;
    5 === fiber.tag && (alternate = fiber.stateNode, null !== alternate && detachDeletedInstance(alternate));
    fiber.stateNode = null;
    fiber.return = null;
    fiber.dependencies = null;
    fiber.memoizedProps = null;
    fiber.memoizedState = null;
    fiber.pendingProps = null;
    fiber.stateNode = null;
    fiber.updateQueue = null;
  }
  var hostParent = null, hostParentIsContainer = false;
  function recursivelyTraverseDeletionEffects(finishedRoot, nearestMountedAncestor, parent) {
    for (parent = parent.child; null !== parent; )
      commitDeletionEffectsOnFiber(finishedRoot, nearestMountedAncestor, parent), parent = parent.sibling;
  }
  function commitDeletionEffectsOnFiber(finishedRoot, nearestMountedAncestor, deletedFiber) {
    if (injectedHook && "function" === typeof injectedHook.onCommitFiberUnmount)
      try {
        injectedHook.onCommitFiberUnmount(rendererID, deletedFiber);
      } catch (err) {
      }
    switch (deletedFiber.tag) {
      case 26:
        offscreenSubtreeWasHidden || safelyDetachRef(deletedFiber, nearestMountedAncestor);
        recursivelyTraverseDeletionEffects(
          finishedRoot,
          nearestMountedAncestor,
          deletedFiber
        );
        deletedFiber.memoizedState ? deletedFiber.memoizedState.count-- : deletedFiber.stateNode && (deletedFiber = deletedFiber.stateNode, deletedFiber.parentNode.removeChild(deletedFiber));
        break;
      case 27:
        offscreenSubtreeWasHidden || safelyDetachRef(deletedFiber, nearestMountedAncestor);
        var prevHostParent = hostParent, prevHostParentIsContainer = hostParentIsContainer;
        isSingletonScope(deletedFiber.type) && (hostParent = deletedFiber.stateNode, hostParentIsContainer = false);
        recursivelyTraverseDeletionEffects(
          finishedRoot,
          nearestMountedAncestor,
          deletedFiber
        );
        releaseSingletonInstance(deletedFiber.stateNode);
        hostParent = prevHostParent;
        hostParentIsContainer = prevHostParentIsContainer;
        break;
      case 5:
        offscreenSubtreeWasHidden || safelyDetachRef(deletedFiber, nearestMountedAncestor);
      case 6:
        prevHostParent = hostParent;
        prevHostParentIsContainer = hostParentIsContainer;
        hostParent = null;
        recursivelyTraverseDeletionEffects(
          finishedRoot,
          nearestMountedAncestor,
          deletedFiber
        );
        hostParent = prevHostParent;
        hostParentIsContainer = prevHostParentIsContainer;
        if (null !== hostParent)
          if (hostParentIsContainer)
            try {
              (9 === hostParent.nodeType ? hostParent.body : "HTML" === hostParent.nodeName ? hostParent.ownerDocument.body : hostParent).removeChild(deletedFiber.stateNode);
            } catch (error) {
              captureCommitPhaseError(
                deletedFiber,
                nearestMountedAncestor,
                error
              );
            }
          else
            try {
              hostParent.removeChild(deletedFiber.stateNode);
            } catch (error) {
              captureCommitPhaseError(
                deletedFiber,
                nearestMountedAncestor,
                error
              );
            }
        break;
      case 18:
        null !== hostParent && (hostParentIsContainer ? (finishedRoot = hostParent, clearHydrationBoundary(
          9 === finishedRoot.nodeType ? finishedRoot.body : "HTML" === finishedRoot.nodeName ? finishedRoot.ownerDocument.body : finishedRoot,
          deletedFiber.stateNode
        ), retryIfBlockedOn(finishedRoot)) : clearHydrationBoundary(hostParent, deletedFiber.stateNode));
        break;
      case 4:
        prevHostParent = hostParent;
        prevHostParentIsContainer = hostParentIsContainer;
        hostParent = deletedFiber.stateNode.containerInfo;
        hostParentIsContainer = true;
        recursivelyTraverseDeletionEffects(
          finishedRoot,
          nearestMountedAncestor,
          deletedFiber
        );
        hostParent = prevHostParent;
        hostParentIsContainer = prevHostParentIsContainer;
        break;
      case 0:
      case 11:
      case 14:
      case 15:
        commitHookEffectListUnmount(2, deletedFiber, nearestMountedAncestor);
        offscreenSubtreeWasHidden || commitHookEffectListUnmount(4, deletedFiber, nearestMountedAncestor);
        recursivelyTraverseDeletionEffects(
          finishedRoot,
          nearestMountedAncestor,
          deletedFiber
        );
        break;
      case 1:
        offscreenSubtreeWasHidden || (safelyDetachRef(deletedFiber, nearestMountedAncestor), prevHostParent = deletedFiber.stateNode, "function" === typeof prevHostParent.componentWillUnmount && safelyCallComponentWillUnmount(
          deletedFiber,
          nearestMountedAncestor,
          prevHostParent
        ));
        recursivelyTraverseDeletionEffects(
          finishedRoot,
          nearestMountedAncestor,
          deletedFiber
        );
        break;
      case 21:
        recursivelyTraverseDeletionEffects(
          finishedRoot,
          nearestMountedAncestor,
          deletedFiber
        );
        break;
      case 22:
        offscreenSubtreeWasHidden = (prevHostParent = offscreenSubtreeWasHidden) || null !== deletedFiber.memoizedState;
        recursivelyTraverseDeletionEffects(
          finishedRoot,
          nearestMountedAncestor,
          deletedFiber
        );
        offscreenSubtreeWasHidden = prevHostParent;
        break;
      default:
        recursivelyTraverseDeletionEffects(
          finishedRoot,
          nearestMountedAncestor,
          deletedFiber
        );
    }
  }
  function commitActivityHydrationCallbacks(finishedRoot, finishedWork) {
    if (null === finishedWork.memoizedState && (finishedRoot = finishedWork.alternate, null !== finishedRoot && (finishedRoot = finishedRoot.memoizedState, null !== finishedRoot))) {
      finishedRoot = finishedRoot.dehydrated;
      try {
        retryIfBlockedOn(finishedRoot);
      } catch (error) {
        captureCommitPhaseError(finishedWork, finishedWork.return, error);
      }
    }
  }
  function commitSuspenseHydrationCallbacks(finishedRoot, finishedWork) {
    if (null === finishedWork.memoizedState && (finishedRoot = finishedWork.alternate, null !== finishedRoot && (finishedRoot = finishedRoot.memoizedState, null !== finishedRoot && (finishedRoot = finishedRoot.dehydrated, null !== finishedRoot))))
      try {
        retryIfBlockedOn(finishedRoot);
      } catch (error) {
        captureCommitPhaseError(finishedWork, finishedWork.return, error);
      }
  }
  function getRetryCache(finishedWork) {
    switch (finishedWork.tag) {
      case 31:
      case 13:
      case 19:
        var retryCache = finishedWork.stateNode;
        null === retryCache && (retryCache = finishedWork.stateNode = new PossiblyWeakSet());
        return retryCache;
      case 22:
        return finishedWork = finishedWork.stateNode, retryCache = finishedWork._retryCache, null === retryCache && (retryCache = finishedWork._retryCache = new PossiblyWeakSet()), retryCache;
      default:
        throw Error(formatProdErrorMessage(435, finishedWork.tag));
    }
  }
  function attachSuspenseRetryListeners(finishedWork, wakeables) {
    var retryCache = getRetryCache(finishedWork);
    wakeables.forEach(function(wakeable) {
      if (!retryCache.has(wakeable)) {
        retryCache.add(wakeable);
        var retry = resolveRetryWakeable.bind(null, finishedWork, wakeable);
        wakeable.then(retry, retry);
      }
    });
  }
  function recursivelyTraverseMutationEffects(root$jscomp$0, parentFiber) {
    var deletions = parentFiber.deletions;
    if (null !== deletions)
      for (var i = 0; i < deletions.length; i++) {
        var childToDelete = deletions[i], root2 = root$jscomp$0, returnFiber = parentFiber, parent = returnFiber;
        a: for (; null !== parent; ) {
          switch (parent.tag) {
            case 27:
              if (isSingletonScope(parent.type)) {
                hostParent = parent.stateNode;
                hostParentIsContainer = false;
                break a;
              }
              break;
            case 5:
              hostParent = parent.stateNode;
              hostParentIsContainer = false;
              break a;
            case 3:
            case 4:
              hostParent = parent.stateNode.containerInfo;
              hostParentIsContainer = true;
              break a;
          }
          parent = parent.return;
        }
        if (null === hostParent) throw Error(formatProdErrorMessage(160));
        commitDeletionEffectsOnFiber(root2, returnFiber, childToDelete);
        hostParent = null;
        hostParentIsContainer = false;
        root2 = childToDelete.alternate;
        null !== root2 && (root2.return = null);
        childToDelete.return = null;
      }
    if (parentFiber.subtreeFlags & 13886)
      for (parentFiber = parentFiber.child; null !== parentFiber; )
        commitMutationEffectsOnFiber(parentFiber, root$jscomp$0), parentFiber = parentFiber.sibling;
  }
  var currentHoistableRoot = null;
  function commitMutationEffectsOnFiber(finishedWork, root2) {
    var current = finishedWork.alternate, flags = finishedWork.flags;
    switch (finishedWork.tag) {
      case 0:
      case 11:
      case 14:
      case 15:
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        flags & 4 && (commitHookEffectListUnmount(3, finishedWork, finishedWork.return), commitHookEffectListMount(3, finishedWork), commitHookEffectListUnmount(5, finishedWork, finishedWork.return));
        break;
      case 1:
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        flags & 512 && (offscreenSubtreeWasHidden || null === current || safelyDetachRef(current, current.return));
        flags & 64 && offscreenSubtreeIsHidden && (finishedWork = finishedWork.updateQueue, null !== finishedWork && (flags = finishedWork.callbacks, null !== flags && (current = finishedWork.shared.hiddenCallbacks, finishedWork.shared.hiddenCallbacks = null === current ? flags : current.concat(flags))));
        break;
      case 26:
        var hoistableRoot = currentHoistableRoot;
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        flags & 512 && (offscreenSubtreeWasHidden || null === current || safelyDetachRef(current, current.return));
        if (flags & 4) {
          var currentResource = null !== current ? current.memoizedState : null;
          flags = finishedWork.memoizedState;
          if (null === current)
            if (null === flags)
              if (null === finishedWork.stateNode) {
                a: {
                  flags = finishedWork.type;
                  current = finishedWork.memoizedProps;
                  hoistableRoot = hoistableRoot.ownerDocument || hoistableRoot;
                  b: switch (flags) {
                    case "title":
                      currentResource = hoistableRoot.getElementsByTagName("title")[0];
                      if (!currentResource || currentResource[internalHoistableMarker] || currentResource[internalInstanceKey] || "http://www.w3.org/2000/svg" === currentResource.namespaceURI || currentResource.hasAttribute("itemprop"))
                        currentResource = hoistableRoot.createElement(flags), hoistableRoot.head.insertBefore(
                          currentResource,
                          hoistableRoot.querySelector("head > title")
                        );
                      setInitialProperties(currentResource, flags, current);
                      currentResource[internalInstanceKey] = finishedWork;
                      markNodeAsHoistable(currentResource);
                      flags = currentResource;
                      break a;
                    case "link":
                      var maybeNodes = getHydratableHoistableCache(
                        "link",
                        "href",
                        hoistableRoot
                      ).get(flags + (current.href || ""));
                      if (maybeNodes) {
                        for (var i = 0; i < maybeNodes.length; i++)
                          if (currentResource = maybeNodes[i], currentResource.getAttribute("href") === (null == current.href || "" === current.href ? null : current.href) && currentResource.getAttribute("rel") === (null == current.rel ? null : current.rel) && currentResource.getAttribute("title") === (null == current.title ? null : current.title) && currentResource.getAttribute("crossorigin") === (null == current.crossOrigin ? null : current.crossOrigin)) {
                            maybeNodes.splice(i, 1);
                            break b;
                          }
                      }
                      currentResource = hoistableRoot.createElement(flags);
                      setInitialProperties(currentResource, flags, current);
                      hoistableRoot.head.appendChild(currentResource);
                      break;
                    case "meta":
                      if (maybeNodes = getHydratableHoistableCache(
                        "meta",
                        "content",
                        hoistableRoot
                      ).get(flags + (current.content || ""))) {
                        for (i = 0; i < maybeNodes.length; i++)
                          if (currentResource = maybeNodes[i], currentResource.getAttribute("content") === (null == current.content ? null : "" + current.content) && currentResource.getAttribute("name") === (null == current.name ? null : current.name) && currentResource.getAttribute("property") === (null == current.property ? null : current.property) && currentResource.getAttribute("http-equiv") === (null == current.httpEquiv ? null : current.httpEquiv) && currentResource.getAttribute("charset") === (null == current.charSet ? null : current.charSet)) {
                            maybeNodes.splice(i, 1);
                            break b;
                          }
                      }
                      currentResource = hoistableRoot.createElement(flags);
                      setInitialProperties(currentResource, flags, current);
                      hoistableRoot.head.appendChild(currentResource);
                      break;
                    default:
                      throw Error(formatProdErrorMessage(468, flags));
                  }
                  currentResource[internalInstanceKey] = finishedWork;
                  markNodeAsHoistable(currentResource);
                  flags = currentResource;
                }
                finishedWork.stateNode = flags;
              } else
                mountHoistable(
                  hoistableRoot,
                  finishedWork.type,
                  finishedWork.stateNode
                );
            else
              finishedWork.stateNode = acquireResource(
                hoistableRoot,
                flags,
                finishedWork.memoizedProps
              );
          else
            currentResource !== flags ? (null === currentResource ? null !== current.stateNode && (current = current.stateNode, current.parentNode.removeChild(current)) : currentResource.count--, null === flags ? mountHoistable(
              hoistableRoot,
              finishedWork.type,
              finishedWork.stateNode
            ) : acquireResource(
              hoistableRoot,
              flags,
              finishedWork.memoizedProps
            )) : null === flags && null !== finishedWork.stateNode && commitHostUpdate(
              finishedWork,
              finishedWork.memoizedProps,
              current.memoizedProps
            );
        }
        break;
      case 27:
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        flags & 512 && (offscreenSubtreeWasHidden || null === current || safelyDetachRef(current, current.return));
        null !== current && flags & 4 && commitHostUpdate(
          finishedWork,
          finishedWork.memoizedProps,
          current.memoizedProps
        );
        break;
      case 5:
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        flags & 512 && (offscreenSubtreeWasHidden || null === current || safelyDetachRef(current, current.return));
        if (finishedWork.flags & 32) {
          hoistableRoot = finishedWork.stateNode;
          try {
            setTextContent(hoistableRoot, "");
          } catch (error) {
            captureCommitPhaseError(finishedWork, finishedWork.return, error);
          }
        }
        flags & 4 && null != finishedWork.stateNode && (hoistableRoot = finishedWork.memoizedProps, commitHostUpdate(
          finishedWork,
          hoistableRoot,
          null !== current ? current.memoizedProps : hoistableRoot
        ));
        flags & 1024 && (needsFormReset = true);
        break;
      case 6:
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        if (flags & 4) {
          if (null === finishedWork.stateNode)
            throw Error(formatProdErrorMessage(162));
          flags = finishedWork.memoizedProps;
          current = finishedWork.stateNode;
          try {
            current.nodeValue = flags;
          } catch (error) {
            captureCommitPhaseError(finishedWork, finishedWork.return, error);
          }
        }
        break;
      case 3:
        tagCaches = null;
        hoistableRoot = currentHoistableRoot;
        currentHoistableRoot = getHoistableRoot(root2.containerInfo);
        recursivelyTraverseMutationEffects(root2, finishedWork);
        currentHoistableRoot = hoistableRoot;
        commitReconciliationEffects(finishedWork);
        if (flags & 4 && null !== current && current.memoizedState.isDehydrated)
          try {
            retryIfBlockedOn(root2.containerInfo);
          } catch (error) {
            captureCommitPhaseError(finishedWork, finishedWork.return, error);
          }
        needsFormReset && (needsFormReset = false, recursivelyResetForms(finishedWork));
        break;
      case 4:
        flags = currentHoistableRoot;
        currentHoistableRoot = getHoistableRoot(
          finishedWork.stateNode.containerInfo
        );
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        currentHoistableRoot = flags;
        break;
      case 12:
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        break;
      case 31:
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        flags & 4 && (flags = finishedWork.updateQueue, null !== flags && (finishedWork.updateQueue = null, attachSuspenseRetryListeners(finishedWork, flags)));
        break;
      case 13:
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        finishedWork.child.flags & 8192 && null !== finishedWork.memoizedState !== (null !== current && null !== current.memoizedState) && (globalMostRecentFallbackTime = now());
        flags & 4 && (flags = finishedWork.updateQueue, null !== flags && (finishedWork.updateQueue = null, attachSuspenseRetryListeners(finishedWork, flags)));
        break;
      case 22:
        hoistableRoot = null !== finishedWork.memoizedState;
        var wasHidden = null !== current && null !== current.memoizedState, prevOffscreenSubtreeIsHidden = offscreenSubtreeIsHidden, prevOffscreenSubtreeWasHidden = offscreenSubtreeWasHidden;
        offscreenSubtreeIsHidden = prevOffscreenSubtreeIsHidden || hoistableRoot;
        offscreenSubtreeWasHidden = prevOffscreenSubtreeWasHidden || wasHidden;
        recursivelyTraverseMutationEffects(root2, finishedWork);
        offscreenSubtreeWasHidden = prevOffscreenSubtreeWasHidden;
        offscreenSubtreeIsHidden = prevOffscreenSubtreeIsHidden;
        commitReconciliationEffects(finishedWork);
        if (flags & 8192)
          a: for (root2 = finishedWork.stateNode, root2._visibility = hoistableRoot ? root2._visibility & -2 : root2._visibility | 1, hoistableRoot && (null === current || wasHidden || offscreenSubtreeIsHidden || offscreenSubtreeWasHidden || recursivelyTraverseDisappearLayoutEffects(finishedWork)), current = null, root2 = finishedWork; ; ) {
            if (5 === root2.tag || 26 === root2.tag) {
              if (null === current) {
                wasHidden = current = root2;
                try {
                  if (currentResource = wasHidden.stateNode, hoistableRoot)
                    maybeNodes = currentResource.style, "function" === typeof maybeNodes.setProperty ? maybeNodes.setProperty("display", "none", "important") : maybeNodes.display = "none";
                  else {
                    i = wasHidden.stateNode;
                    var styleProp = wasHidden.memoizedProps.style, display = void 0 !== styleProp && null !== styleProp && styleProp.hasOwnProperty("display") ? styleProp.display : null;
                    i.style.display = null == display || "boolean" === typeof display ? "" : ("" + display).trim();
                  }
                } catch (error) {
                  captureCommitPhaseError(wasHidden, wasHidden.return, error);
                }
              }
            } else if (6 === root2.tag) {
              if (null === current) {
                wasHidden = root2;
                try {
                  wasHidden.stateNode.nodeValue = hoistableRoot ? "" : wasHidden.memoizedProps;
                } catch (error) {
                  captureCommitPhaseError(wasHidden, wasHidden.return, error);
                }
              }
            } else if (18 === root2.tag) {
              if (null === current) {
                wasHidden = root2;
                try {
                  var instance = wasHidden.stateNode;
                  hoistableRoot ? hideOrUnhideDehydratedBoundary(instance, true) : hideOrUnhideDehydratedBoundary(wasHidden.stateNode, false);
                } catch (error) {
                  captureCommitPhaseError(wasHidden, wasHidden.return, error);
                }
              }
            } else if ((22 !== root2.tag && 23 !== root2.tag || null === root2.memoizedState || root2 === finishedWork) && null !== root2.child) {
              root2.child.return = root2;
              root2 = root2.child;
              continue;
            }
            if (root2 === finishedWork) break a;
            for (; null === root2.sibling; ) {
              if (null === root2.return || root2.return === finishedWork) break a;
              current === root2 && (current = null);
              root2 = root2.return;
            }
            current === root2 && (current = null);
            root2.sibling.return = root2.return;
            root2 = root2.sibling;
          }
        flags & 4 && (flags = finishedWork.updateQueue, null !== flags && (current = flags.retryQueue, null !== current && (flags.retryQueue = null, attachSuspenseRetryListeners(finishedWork, current))));
        break;
      case 19:
        recursivelyTraverseMutationEffects(root2, finishedWork);
        commitReconciliationEffects(finishedWork);
        flags & 4 && (flags = finishedWork.updateQueue, null !== flags && (finishedWork.updateQueue = null, attachSuspenseRetryListeners(finishedWork, flags)));
        break;
      case 30:
        break;
      case 21:
        break;
      default:
        recursivelyTraverseMutationEffects(root2, finishedWork), commitReconciliationEffects(finishedWork);
    }
  }
  function commitReconciliationEffects(finishedWork) {
    var flags = finishedWork.flags;
    if (flags & 2) {
      try {
        for (var hostParentFiber, parentFiber = finishedWork.return; null !== parentFiber; ) {
          if (isHostParent(parentFiber)) {
            hostParentFiber = parentFiber;
            break;
          }
          parentFiber = parentFiber.return;
        }
        if (null == hostParentFiber) throw Error(formatProdErrorMessage(160));
        switch (hostParentFiber.tag) {
          case 27:
            var parent = hostParentFiber.stateNode, before = getHostSibling(finishedWork);
            insertOrAppendPlacementNode(finishedWork, before, parent);
            break;
          case 5:
            var parent$141 = hostParentFiber.stateNode;
            hostParentFiber.flags & 32 && (setTextContent(parent$141, ""), hostParentFiber.flags &= -33);
            var before$142 = getHostSibling(finishedWork);
            insertOrAppendPlacementNode(finishedWork, before$142, parent$141);
            break;
          case 3:
          case 4:
            var parent$143 = hostParentFiber.stateNode.containerInfo, before$144 = getHostSibling(finishedWork);
            insertOrAppendPlacementNodeIntoContainer(
              finishedWork,
              before$144,
              parent$143
            );
            break;
          default:
            throw Error(formatProdErrorMessage(161));
        }
      } catch (error) {
        captureCommitPhaseError(finishedWork, finishedWork.return, error);
      }
      finishedWork.flags &= -3;
    }
    flags & 4096 && (finishedWork.flags &= -4097);
  }
  function recursivelyResetForms(parentFiber) {
    if (parentFiber.subtreeFlags & 1024)
      for (parentFiber = parentFiber.child; null !== parentFiber; ) {
        var fiber = parentFiber;
        recursivelyResetForms(fiber);
        5 === fiber.tag && fiber.flags & 1024 && fiber.stateNode.reset();
        parentFiber = parentFiber.sibling;
      }
  }
  function recursivelyTraverseLayoutEffects(root2, parentFiber) {
    if (parentFiber.subtreeFlags & 8772)
      for (parentFiber = parentFiber.child; null !== parentFiber; )
        commitLayoutEffectOnFiber(root2, parentFiber.alternate, parentFiber), parentFiber = parentFiber.sibling;
  }
  function recursivelyTraverseDisappearLayoutEffects(parentFiber) {
    for (parentFiber = parentFiber.child; null !== parentFiber; ) {
      var finishedWork = parentFiber;
      switch (finishedWork.tag) {
        case 0:
        case 11:
        case 14:
        case 15:
          commitHookEffectListUnmount(4, finishedWork, finishedWork.return);
          recursivelyTraverseDisappearLayoutEffects(finishedWork);
          break;
        case 1:
          safelyDetachRef(finishedWork, finishedWork.return);
          var instance = finishedWork.stateNode;
          "function" === typeof instance.componentWillUnmount && safelyCallComponentWillUnmount(
            finishedWork,
            finishedWork.return,
            instance
          );
          recursivelyTraverseDisappearLayoutEffects(finishedWork);
          break;
        case 27:
          releaseSingletonInstance(finishedWork.stateNode);
        case 26:
        case 5:
          safelyDetachRef(finishedWork, finishedWork.return);
          recursivelyTraverseDisappearLayoutEffects(finishedWork);
          break;
        case 22:
          null === finishedWork.memoizedState && recursivelyTraverseDisappearLayoutEffects(finishedWork);
          break;
        case 30:
          recursivelyTraverseDisappearLayoutEffects(finishedWork);
          break;
        default:
          recursivelyTraverseDisappearLayoutEffects(finishedWork);
      }
      parentFiber = parentFiber.sibling;
    }
  }
  function recursivelyTraverseReappearLayoutEffects(finishedRoot$jscomp$0, parentFiber, includeWorkInProgressEffects) {
    includeWorkInProgressEffects = includeWorkInProgressEffects && 0 !== (parentFiber.subtreeFlags & 8772);
    for (parentFiber = parentFiber.child; null !== parentFiber; ) {
      var current = parentFiber.alternate, finishedRoot = finishedRoot$jscomp$0, finishedWork = parentFiber, flags = finishedWork.flags;
      switch (finishedWork.tag) {
        case 0:
        case 11:
        case 15:
          recursivelyTraverseReappearLayoutEffects(
            finishedRoot,
            finishedWork,
            includeWorkInProgressEffects
          );
          commitHookEffectListMount(4, finishedWork);
          break;
        case 1:
          recursivelyTraverseReappearLayoutEffects(
            finishedRoot,
            finishedWork,
            includeWorkInProgressEffects
          );
          current = finishedWork;
          finishedRoot = current.stateNode;
          if ("function" === typeof finishedRoot.componentDidMount)
            try {
              finishedRoot.componentDidMount();
            } catch (error) {
              captureCommitPhaseError(current, current.return, error);
            }
          current = finishedWork;
          finishedRoot = current.updateQueue;
          if (null !== finishedRoot) {
            var instance = current.stateNode;
            try {
              var hiddenCallbacks = finishedRoot.shared.hiddenCallbacks;
              if (null !== hiddenCallbacks)
                for (finishedRoot.shared.hiddenCallbacks = null, finishedRoot = 0; finishedRoot < hiddenCallbacks.length; finishedRoot++)
                  callCallback(hiddenCallbacks[finishedRoot], instance);
            } catch (error) {
              captureCommitPhaseError(current, current.return, error);
            }
          }
          includeWorkInProgressEffects && flags & 64 && commitClassCallbacks(finishedWork);
          safelyAttachRef(finishedWork, finishedWork.return);
          break;
        case 27:
          commitHostSingletonAcquisition(finishedWork);
        case 26:
        case 5:
          recursivelyTraverseReappearLayoutEffects(
            finishedRoot,
            finishedWork,
            includeWorkInProgressEffects
          );
          includeWorkInProgressEffects && null === current && flags & 4 && commitHostMount(finishedWork);
          safelyAttachRef(finishedWork, finishedWork.return);
          break;
        case 12:
          recursivelyTraverseReappearLayoutEffects(
            finishedRoot,
            finishedWork,
            includeWorkInProgressEffects
          );
          break;
        case 31:
          recursivelyTraverseReappearLayoutEffects(
            finishedRoot,
            finishedWork,
            includeWorkInProgressEffects
          );
          includeWorkInProgressEffects && flags & 4 && commitActivityHydrationCallbacks(finishedRoot, finishedWork);
          break;
        case 13:
          recursivelyTraverseReappearLayoutEffects(
            finishedRoot,
            finishedWork,
            includeWorkInProgressEffects
          );
          includeWorkInProgressEffects && flags & 4 && commitSuspenseHydrationCallbacks(finishedRoot, finishedWork);
          break;
        case 22:
          null === finishedWork.memoizedState && recursivelyTraverseReappearLayoutEffects(
            finishedRoot,
            finishedWork,
            includeWorkInProgressEffects
          );
          safelyAttachRef(finishedWork, finishedWork.return);
          break;
        case 30:
          break;
        default:
          recursivelyTraverseReappearLayoutEffects(
            finishedRoot,
            finishedWork,
            includeWorkInProgressEffects
          );
      }
      parentFiber = parentFiber.sibling;
    }
  }
  function commitOffscreenPassiveMountEffects(current, finishedWork) {
    var previousCache = null;
    null !== current && null !== current.memoizedState && null !== current.memoizedState.cachePool && (previousCache = current.memoizedState.cachePool.pool);
    current = null;
    null !== finishedWork.memoizedState && null !== finishedWork.memoizedState.cachePool && (current = finishedWork.memoizedState.cachePool.pool);
    current !== previousCache && (null != current && current.refCount++, null != previousCache && releaseCache(previousCache));
  }
  function commitCachePassiveMountEffect(current, finishedWork) {
    current = null;
    null !== finishedWork.alternate && (current = finishedWork.alternate.memoizedState.cache);
    finishedWork = finishedWork.memoizedState.cache;
    finishedWork !== current && (finishedWork.refCount++, null != current && releaseCache(current));
  }
  function recursivelyTraversePassiveMountEffects(root2, parentFiber, committedLanes, committedTransitions) {
    if (parentFiber.subtreeFlags & 10256)
      for (parentFiber = parentFiber.child; null !== parentFiber; )
        commitPassiveMountOnFiber(
          root2,
          parentFiber,
          committedLanes,
          committedTransitions
        ), parentFiber = parentFiber.sibling;
  }
  function commitPassiveMountOnFiber(finishedRoot, finishedWork, committedLanes, committedTransitions) {
    var flags = finishedWork.flags;
    switch (finishedWork.tag) {
      case 0:
      case 11:
      case 15:
        recursivelyTraversePassiveMountEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions
        );
        flags & 2048 && commitHookEffectListMount(9, finishedWork);
        break;
      case 1:
        recursivelyTraversePassiveMountEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions
        );
        break;
      case 3:
        recursivelyTraversePassiveMountEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions
        );
        flags & 2048 && (finishedRoot = null, null !== finishedWork.alternate && (finishedRoot = finishedWork.alternate.memoizedState.cache), finishedWork = finishedWork.memoizedState.cache, finishedWork !== finishedRoot && (finishedWork.refCount++, null != finishedRoot && releaseCache(finishedRoot)));
        break;
      case 12:
        if (flags & 2048) {
          recursivelyTraversePassiveMountEffects(
            finishedRoot,
            finishedWork,
            committedLanes,
            committedTransitions
          );
          finishedRoot = finishedWork.stateNode;
          try {
            var _finishedWork$memoize2 = finishedWork.memoizedProps, id = _finishedWork$memoize2.id, onPostCommit = _finishedWork$memoize2.onPostCommit;
            "function" === typeof onPostCommit && onPostCommit(
              id,
              null === finishedWork.alternate ? "mount" : "update",
              finishedRoot.passiveEffectDuration,
              -0
            );
          } catch (error) {
            captureCommitPhaseError(finishedWork, finishedWork.return, error);
          }
        } else
          recursivelyTraversePassiveMountEffects(
            finishedRoot,
            finishedWork,
            committedLanes,
            committedTransitions
          );
        break;
      case 31:
        recursivelyTraversePassiveMountEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions
        );
        break;
      case 13:
        recursivelyTraversePassiveMountEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions
        );
        break;
      case 23:
        break;
      case 22:
        _finishedWork$memoize2 = finishedWork.stateNode;
        id = finishedWork.alternate;
        null !== finishedWork.memoizedState ? _finishedWork$memoize2._visibility & 2 ? recursivelyTraversePassiveMountEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions
        ) : recursivelyTraverseAtomicPassiveEffects(finishedRoot, finishedWork) : _finishedWork$memoize2._visibility & 2 ? recursivelyTraversePassiveMountEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions
        ) : (_finishedWork$memoize2._visibility |= 2, recursivelyTraverseReconnectPassiveEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions,
          0 !== (finishedWork.subtreeFlags & 10256) || false
        ));
        flags & 2048 && commitOffscreenPassiveMountEffects(id, finishedWork);
        break;
      case 24:
        recursivelyTraversePassiveMountEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions
        );
        flags & 2048 && commitCachePassiveMountEffect(finishedWork.alternate, finishedWork);
        break;
      default:
        recursivelyTraversePassiveMountEffects(
          finishedRoot,
          finishedWork,
          committedLanes,
          committedTransitions
        );
    }
  }
  function recursivelyTraverseReconnectPassiveEffects(finishedRoot$jscomp$0, parentFiber, committedLanes$jscomp$0, committedTransitions$jscomp$0, includeWorkInProgressEffects) {
    includeWorkInProgressEffects = includeWorkInProgressEffects && (0 !== (parentFiber.subtreeFlags & 10256) || false);
    for (parentFiber = parentFiber.child; null !== parentFiber; ) {
      var finishedRoot = finishedRoot$jscomp$0, finishedWork = parentFiber, committedLanes = committedLanes$jscomp$0, committedTransitions = committedTransitions$jscomp$0, flags = finishedWork.flags;
      switch (finishedWork.tag) {
        case 0:
        case 11:
        case 15:
          recursivelyTraverseReconnectPassiveEffects(
            finishedRoot,
            finishedWork,
            committedLanes,
            committedTransitions,
            includeWorkInProgressEffects
          );
          commitHookEffectListMount(8, finishedWork);
          break;
        case 23:
          break;
        case 22:
          var instance = finishedWork.stateNode;
          null !== finishedWork.memoizedState ? instance._visibility & 2 ? recursivelyTraverseReconnectPassiveEffects(
            finishedRoot,
            finishedWork,
            committedLanes,
            committedTransitions,
            includeWorkInProgressEffects
          ) : recursivelyTraverseAtomicPassiveEffects(
            finishedRoot,
            finishedWork
          ) : (instance._visibility |= 2, recursivelyTraverseReconnectPassiveEffects(
            finishedRoot,
            finishedWork,
            committedLanes,
            committedTransitions,
            includeWorkInProgressEffects
          ));
          includeWorkInProgressEffects && flags & 2048 && commitOffscreenPassiveMountEffects(
            finishedWork.alternate,
            finishedWork
          );
          break;
        case 24:
          recursivelyTraverseReconnectPassiveEffects(
            finishedRoot,
            finishedWork,
            committedLanes,
            committedTransitions,
            includeWorkInProgressEffects
          );
          includeWorkInProgressEffects && flags & 2048 && commitCachePassiveMountEffect(finishedWork.alternate, finishedWork);
          break;
        default:
          recursivelyTraverseReconnectPassiveEffects(
            finishedRoot,
            finishedWork,
            committedLanes,
            committedTransitions,
            includeWorkInProgressEffects
          );
      }
      parentFiber = parentFiber.sibling;
    }
  }
  function recursivelyTraverseAtomicPassiveEffects(finishedRoot$jscomp$0, parentFiber) {
    if (parentFiber.subtreeFlags & 10256)
      for (parentFiber = parentFiber.child; null !== parentFiber; ) {
        var finishedRoot = finishedRoot$jscomp$0, finishedWork = parentFiber, flags = finishedWork.flags;
        switch (finishedWork.tag) {
          case 22:
            recursivelyTraverseAtomicPassiveEffects(finishedRoot, finishedWork);
            flags & 2048 && commitOffscreenPassiveMountEffects(
              finishedWork.alternate,
              finishedWork
            );
            break;
          case 24:
            recursivelyTraverseAtomicPassiveEffects(finishedRoot, finishedWork);
            flags & 2048 && commitCachePassiveMountEffect(finishedWork.alternate, finishedWork);
            break;
          default:
            recursivelyTraverseAtomicPassiveEffects(finishedRoot, finishedWork);
        }
        parentFiber = parentFiber.sibling;
      }
  }
  var suspenseyCommitFlag = 8192;
  function recursivelyAccumulateSuspenseyCommit(parentFiber, committedLanes, suspendedState) {
    if (parentFiber.subtreeFlags & suspenseyCommitFlag)
      for (parentFiber = parentFiber.child; null !== parentFiber; )
        accumulateSuspenseyCommitOnFiber(
          parentFiber,
          committedLanes,
          suspendedState
        ), parentFiber = parentFiber.sibling;
  }
  function accumulateSuspenseyCommitOnFiber(fiber, committedLanes, suspendedState) {
    switch (fiber.tag) {
      case 26:
        recursivelyAccumulateSuspenseyCommit(
          fiber,
          committedLanes,
          suspendedState
        );
        fiber.flags & suspenseyCommitFlag && null !== fiber.memoizedState && suspendResource(
          suspendedState,
          currentHoistableRoot,
          fiber.memoizedState,
          fiber.memoizedProps
        );
        break;
      case 5:
        recursivelyAccumulateSuspenseyCommit(
          fiber,
          committedLanes,
          suspendedState
        );
        break;
      case 3:
      case 4:
        var previousHoistableRoot = currentHoistableRoot;
        currentHoistableRoot = getHoistableRoot(fiber.stateNode.containerInfo);
        recursivelyAccumulateSuspenseyCommit(
          fiber,
          committedLanes,
          suspendedState
        );
        currentHoistableRoot = previousHoistableRoot;
        break;
      case 22:
        null === fiber.memoizedState && (previousHoistableRoot = fiber.alternate, null !== previousHoistableRoot && null !== previousHoistableRoot.memoizedState ? (previousHoistableRoot = suspenseyCommitFlag, suspenseyCommitFlag = 16777216, recursivelyAccumulateSuspenseyCommit(
          fiber,
          committedLanes,
          suspendedState
        ), suspenseyCommitFlag = previousHoistableRoot) : recursivelyAccumulateSuspenseyCommit(
          fiber,
          committedLanes,
          suspendedState
        ));
        break;
      default:
        recursivelyAccumulateSuspenseyCommit(
          fiber,
          committedLanes,
          suspendedState
        );
    }
  }
  function detachAlternateSiblings(parentFiber) {
    var previousFiber = parentFiber.alternate;
    if (null !== previousFiber && (parentFiber = previousFiber.child, null !== parentFiber)) {
      previousFiber.child = null;
      do
        previousFiber = parentFiber.sibling, parentFiber.sibling = null, parentFiber = previousFiber;
      while (null !== parentFiber);
    }
  }
  function recursivelyTraversePassiveUnmountEffects(parentFiber) {
    var deletions = parentFiber.deletions;
    if (0 !== (parentFiber.flags & 16)) {
      if (null !== deletions)
        for (var i = 0; i < deletions.length; i++) {
          var childToDelete = deletions[i];
          nextEffect = childToDelete;
          commitPassiveUnmountEffectsInsideOfDeletedTree_begin(
            childToDelete,
            parentFiber
          );
        }
      detachAlternateSiblings(parentFiber);
    }
    if (parentFiber.subtreeFlags & 10256)
      for (parentFiber = parentFiber.child; null !== parentFiber; )
        commitPassiveUnmountOnFiber(parentFiber), parentFiber = parentFiber.sibling;
  }
  function commitPassiveUnmountOnFiber(finishedWork) {
    switch (finishedWork.tag) {
      case 0:
      case 11:
      case 15:
        recursivelyTraversePassiveUnmountEffects(finishedWork);
        finishedWork.flags & 2048 && commitHookEffectListUnmount(9, finishedWork, finishedWork.return);
        break;
      case 3:
        recursivelyTraversePassiveUnmountEffects(finishedWork);
        break;
      case 12:
        recursivelyTraversePassiveUnmountEffects(finishedWork);
        break;
      case 22:
        var instance = finishedWork.stateNode;
        null !== finishedWork.memoizedState && instance._visibility & 2 && (null === finishedWork.return || 13 !== finishedWork.return.tag) ? (instance._visibility &= -3, recursivelyTraverseDisconnectPassiveEffects(finishedWork)) : recursivelyTraversePassiveUnmountEffects(finishedWork);
        break;
      default:
        recursivelyTraversePassiveUnmountEffects(finishedWork);
    }
  }
  function recursivelyTraverseDisconnectPassiveEffects(parentFiber) {
    var deletions = parentFiber.deletions;
    if (0 !== (parentFiber.flags & 16)) {
      if (null !== deletions)
        for (var i = 0; i < deletions.length; i++) {
          var childToDelete = deletions[i];
          nextEffect = childToDelete;
          commitPassiveUnmountEffectsInsideOfDeletedTree_begin(
            childToDelete,
            parentFiber
          );
        }
      detachAlternateSiblings(parentFiber);
    }
    for (parentFiber = parentFiber.child; null !== parentFiber; ) {
      deletions = parentFiber;
      switch (deletions.tag) {
        case 0:
        case 11:
        case 15:
          commitHookEffectListUnmount(8, deletions, deletions.return);
          recursivelyTraverseDisconnectPassiveEffects(deletions);
          break;
        case 22:
          i = deletions.stateNode;
          i._visibility & 2 && (i._visibility &= -3, recursivelyTraverseDisconnectPassiveEffects(deletions));
          break;
        default:
          recursivelyTraverseDisconnectPassiveEffects(deletions);
      }
      parentFiber = parentFiber.sibling;
    }
  }
  function commitPassiveUnmountEffectsInsideOfDeletedTree_begin(deletedSubtreeRoot, nearestMountedAncestor) {
    for (; null !== nextEffect; ) {
      var fiber = nextEffect;
      switch (fiber.tag) {
        case 0:
        case 11:
        case 15:
          commitHookEffectListUnmount(8, fiber, nearestMountedAncestor);
          break;
        case 23:
        case 22:
          if (null !== fiber.memoizedState && null !== fiber.memoizedState.cachePool) {
            var cache = fiber.memoizedState.cachePool.pool;
            null != cache && cache.refCount++;
          }
          break;
        case 24:
          releaseCache(fiber.memoizedState.cache);
      }
      cache = fiber.child;
      if (null !== cache) cache.return = fiber, nextEffect = cache;
      else
        a: for (fiber = deletedSubtreeRoot; null !== nextEffect; ) {
          cache = nextEffect;
          var sibling = cache.sibling, returnFiber = cache.return;
          detachFiberAfterEffects(cache);
          if (cache === fiber) {
            nextEffect = null;
            break a;
          }
          if (null !== sibling) {
            sibling.return = returnFiber;
            nextEffect = sibling;
            break a;
          }
          nextEffect = returnFiber;
        }
    }
  }
  var DefaultAsyncDispatcher = {
    getCacheForType: function(resourceType) {
      var cache = readContext(CacheContext), cacheForType = cache.data.get(resourceType);
      void 0 === cacheForType && (cacheForType = resourceType(), cache.data.set(resourceType, cacheForType));
      return cacheForType;
    },
    cacheSignal: function() {
      return readContext(CacheContext).controller.signal;
    }
  }, PossiblyWeakMap = "function" === typeof WeakMap ? WeakMap : Map, executionContext = 0, workInProgressRoot = null, workInProgress = null, workInProgressRootRenderLanes = 0, workInProgressSuspendedReason = 0, workInProgressThrownValue = null, workInProgressRootDidSkipSuspendedSiblings = false, workInProgressRootIsPrerendering = false, workInProgressRootDidAttachPingListener = false, entangledRenderLanes = 0, workInProgressRootExitStatus = 0, workInProgressRootSkippedLanes = 0, workInProgressRootInterleavedUpdatedLanes = 0, workInProgressRootPingedLanes = 0, workInProgressDeferredLane = 0, workInProgressSuspendedRetryLanes = 0, workInProgressRootConcurrentErrors = null, workInProgressRootRecoverableErrors = null, workInProgressRootDidIncludeRecursiveRenderUpdate = false, globalMostRecentFallbackTime = 0, globalMostRecentTransitionTime = 0, workInProgressRootRenderTargetTime = Infinity, workInProgressTransitions = null, legacyErrorBoundariesThatAlreadyFailed = null, pendingEffectsStatus = 0, pendingEffectsRoot = null, pendingFinishedWork = null, pendingEffectsLanes = 0, pendingEffectsRemainingLanes = 0, pendingPassiveTransitions = null, pendingRecoverableErrors = null, nestedUpdateCount = 0, rootWithNestedUpdates = null;
  function requestUpdateLane() {
    return 0 !== (executionContext & 2) && 0 !== workInProgressRootRenderLanes ? workInProgressRootRenderLanes & -workInProgressRootRenderLanes : null !== ReactSharedInternals.T ? requestTransitionLane() : resolveUpdatePriority();
  }
  function requestDeferredLane() {
    if (0 === workInProgressDeferredLane)
      if (0 === (workInProgressRootRenderLanes & 536870912) || isHydrating) {
        var lane = nextTransitionDeferredLane;
        nextTransitionDeferredLane <<= 1;
        0 === (nextTransitionDeferredLane & 3932160) && (nextTransitionDeferredLane = 262144);
        workInProgressDeferredLane = lane;
      } else workInProgressDeferredLane = 536870912;
    lane = suspenseHandlerStackCursor.current;
    null !== lane && (lane.flags |= 32);
    return workInProgressDeferredLane;
  }
  function scheduleUpdateOnFiber(root2, fiber, lane) {
    if (root2 === workInProgressRoot && (2 === workInProgressSuspendedReason || 9 === workInProgressSuspendedReason) || null !== root2.cancelPendingCommit)
      prepareFreshStack(root2, 0), markRootSuspended(
        root2,
        workInProgressRootRenderLanes,
        workInProgressDeferredLane,
        false
      );
    markRootUpdated$1(root2, lane);
    if (0 === (executionContext & 2) || root2 !== workInProgressRoot)
      root2 === workInProgressRoot && (0 === (executionContext & 2) && (workInProgressRootInterleavedUpdatedLanes |= lane), 4 === workInProgressRootExitStatus && markRootSuspended(
        root2,
        workInProgressRootRenderLanes,
        workInProgressDeferredLane,
        false
      )), ensureRootIsScheduled(root2);
  }
  function performWorkOnRoot(root$jscomp$0, lanes, forceSync) {
    if (0 !== (executionContext & 6)) throw Error(formatProdErrorMessage(327));
    var shouldTimeSlice = !forceSync && 0 === (lanes & 127) && 0 === (lanes & root$jscomp$0.expiredLanes) || checkIfRootIsPrerendering(root$jscomp$0, lanes), exitStatus = shouldTimeSlice ? renderRootConcurrent(root$jscomp$0, lanes) : renderRootSync(root$jscomp$0, lanes, true), renderWasConcurrent = shouldTimeSlice;
    do {
      if (0 === exitStatus) {
        workInProgressRootIsPrerendering && !shouldTimeSlice && markRootSuspended(root$jscomp$0, lanes, 0, false);
        break;
      } else {
        forceSync = root$jscomp$0.current.alternate;
        if (renderWasConcurrent && !isRenderConsistentWithExternalStores(forceSync)) {
          exitStatus = renderRootSync(root$jscomp$0, lanes, false);
          renderWasConcurrent = false;
          continue;
        }
        if (2 === exitStatus) {
          renderWasConcurrent = lanes;
          if (root$jscomp$0.errorRecoveryDisabledLanes & renderWasConcurrent)
            var JSCompiler_inline_result = 0;
          else
            JSCompiler_inline_result = root$jscomp$0.pendingLanes & -536870913, JSCompiler_inline_result = 0 !== JSCompiler_inline_result ? JSCompiler_inline_result : JSCompiler_inline_result & 536870912 ? 536870912 : 0;
          if (0 !== JSCompiler_inline_result) {
            lanes = JSCompiler_inline_result;
            a: {
              var root2 = root$jscomp$0;
              exitStatus = workInProgressRootConcurrentErrors;
              var wasRootDehydrated = root2.current.memoizedState.isDehydrated;
              wasRootDehydrated && (prepareFreshStack(root2, JSCompiler_inline_result).flags |= 256);
              JSCompiler_inline_result = renderRootSync(
                root2,
                JSCompiler_inline_result,
                false
              );
              if (2 !== JSCompiler_inline_result) {
                if (workInProgressRootDidAttachPingListener && !wasRootDehydrated) {
                  root2.errorRecoveryDisabledLanes |= renderWasConcurrent;
                  workInProgressRootInterleavedUpdatedLanes |= renderWasConcurrent;
                  exitStatus = 4;
                  break a;
                }
                renderWasConcurrent = workInProgressRootRecoverableErrors;
                workInProgressRootRecoverableErrors = exitStatus;
                null !== renderWasConcurrent && (null === workInProgressRootRecoverableErrors ? workInProgressRootRecoverableErrors = renderWasConcurrent : workInProgressRootRecoverableErrors.push.apply(
                  workInProgressRootRecoverableErrors,
                  renderWasConcurrent
                ));
              }
              exitStatus = JSCompiler_inline_result;
            }
            renderWasConcurrent = false;
            if (2 !== exitStatus) continue;
          }
        }
        if (1 === exitStatus) {
          prepareFreshStack(root$jscomp$0, 0);
          markRootSuspended(root$jscomp$0, lanes, 0, true);
          break;
        }
        a: {
          shouldTimeSlice = root$jscomp$0;
          renderWasConcurrent = exitStatus;
          switch (renderWasConcurrent) {
            case 0:
            case 1:
              throw Error(formatProdErrorMessage(345));
            case 4:
              if ((lanes & 4194048) !== lanes) break;
            case 6:
              markRootSuspended(
                shouldTimeSlice,
                lanes,
                workInProgressDeferredLane,
                !workInProgressRootDidSkipSuspendedSiblings
              );
              break a;
            case 2:
              workInProgressRootRecoverableErrors = null;
              break;
            case 3:
            case 5:
              break;
            default:
              throw Error(formatProdErrorMessage(329));
          }
          if ((lanes & 62914560) === lanes && (exitStatus = globalMostRecentFallbackTime + 300 - now(), 10 < exitStatus)) {
            markRootSuspended(
              shouldTimeSlice,
              lanes,
              workInProgressDeferredLane,
              !workInProgressRootDidSkipSuspendedSiblings
            );
            if (0 !== getNextLanes(shouldTimeSlice, 0, true)) break a;
            pendingEffectsLanes = lanes;
            shouldTimeSlice.timeoutHandle = scheduleTimeout(
              commitRootWhenReady.bind(
                null,
                shouldTimeSlice,
                forceSync,
                workInProgressRootRecoverableErrors,
                workInProgressTransitions,
                workInProgressRootDidIncludeRecursiveRenderUpdate,
                lanes,
                workInProgressDeferredLane,
                workInProgressRootInterleavedUpdatedLanes,
                workInProgressSuspendedRetryLanes,
                workInProgressRootDidSkipSuspendedSiblings,
                renderWasConcurrent,
                "Throttled",
                -0,
                0
              ),
              exitStatus
            );
            break a;
          }
          commitRootWhenReady(
            shouldTimeSlice,
            forceSync,
            workInProgressRootRecoverableErrors,
            workInProgressTransitions,
            workInProgressRootDidIncludeRecursiveRenderUpdate,
            lanes,
            workInProgressDeferredLane,
            workInProgressRootInterleavedUpdatedLanes,
            workInProgressSuspendedRetryLanes,
            workInProgressRootDidSkipSuspendedSiblings,
            renderWasConcurrent,
            null,
            -0,
            0
          );
        }
      }
      break;
    } while (1);
    ensureRootIsScheduled(root$jscomp$0);
  }
  function commitRootWhenReady(root2, finishedWork, recoverableErrors, transitions, didIncludeRenderPhaseUpdate, lanes, spawnedLane, updatedLanes, suspendedRetryLanes, didSkipSuspendedSiblings, exitStatus, suspendedCommitReason, completedRenderStartTime, completedRenderEndTime) {
    root2.timeoutHandle = -1;
    suspendedCommitReason = finishedWork.subtreeFlags;
    if (suspendedCommitReason & 8192 || 16785408 === (suspendedCommitReason & 16785408)) {
      suspendedCommitReason = {
        stylesheets: null,
        count: 0,
        imgCount: 0,
        imgBytes: 0,
        suspenseyImages: [],
        waitingForImages: true,
        waitingForViewTransition: false,
        unsuspend: noop$1
      };
      accumulateSuspenseyCommitOnFiber(
        finishedWork,
        lanes,
        suspendedCommitReason
      );
      var timeoutOffset = (lanes & 62914560) === lanes ? globalMostRecentFallbackTime - now() : (lanes & 4194048) === lanes ? globalMostRecentTransitionTime - now() : 0;
      timeoutOffset = waitForCommitToBeReady(
        suspendedCommitReason,
        timeoutOffset
      );
      if (null !== timeoutOffset) {
        pendingEffectsLanes = lanes;
        root2.cancelPendingCommit = timeoutOffset(
          commitRoot.bind(
            null,
            root2,
            finishedWork,
            lanes,
            recoverableErrors,
            transitions,
            didIncludeRenderPhaseUpdate,
            spawnedLane,
            updatedLanes,
            suspendedRetryLanes,
            exitStatus,
            suspendedCommitReason,
            null,
            completedRenderStartTime,
            completedRenderEndTime
          )
        );
        markRootSuspended(root2, lanes, spawnedLane, !didSkipSuspendedSiblings);
        return;
      }
    }
    commitRoot(
      root2,
      finishedWork,
      lanes,
      recoverableErrors,
      transitions,
      didIncludeRenderPhaseUpdate,
      spawnedLane,
      updatedLanes,
      suspendedRetryLanes
    );
  }
  function isRenderConsistentWithExternalStores(finishedWork) {
    for (var node = finishedWork; ; ) {
      var tag = node.tag;
      if ((0 === tag || 11 === tag || 15 === tag) && node.flags & 16384 && (tag = node.updateQueue, null !== tag && (tag = tag.stores, null !== tag)))
        for (var i = 0; i < tag.length; i++) {
          var check = tag[i], getSnapshot = check.getSnapshot;
          check = check.value;
          try {
            if (!objectIs(getSnapshot(), check)) return false;
          } catch (error) {
            return false;
          }
        }
      tag = node.child;
      if (node.subtreeFlags & 16384 && null !== tag)
        tag.return = node, node = tag;
      else {
        if (node === finishedWork) break;
        for (; null === node.sibling; ) {
          if (null === node.return || node.return === finishedWork) return true;
          node = node.return;
        }
        node.sibling.return = node.return;
        node = node.sibling;
      }
    }
    return true;
  }
  function markRootSuspended(root2, suspendedLanes, spawnedLane, didAttemptEntireTree) {
    suspendedLanes &= ~workInProgressRootPingedLanes;
    suspendedLanes &= ~workInProgressRootInterleavedUpdatedLanes;
    root2.suspendedLanes |= suspendedLanes;
    root2.pingedLanes &= ~suspendedLanes;
    didAttemptEntireTree && (root2.warmLanes |= suspendedLanes);
    didAttemptEntireTree = root2.expirationTimes;
    for (var lanes = suspendedLanes; 0 < lanes; ) {
      var index$6 = 31 - clz32(lanes), lane = 1 << index$6;
      didAttemptEntireTree[index$6] = -1;
      lanes &= ~lane;
    }
    0 !== spawnedLane && markSpawnedDeferredLane(root2, spawnedLane, suspendedLanes);
  }
  function flushSyncWork$1() {
    return 0 === (executionContext & 6) ? (flushSyncWorkAcrossRoots_impl(0), false) : true;
  }
  function resetWorkInProgressStack() {
    if (null !== workInProgress) {
      if (0 === workInProgressSuspendedReason)
        var interruptedWork = workInProgress.return;
      else
        interruptedWork = workInProgress, lastContextDependency = currentlyRenderingFiber$1 = null, resetHooksOnUnwind(interruptedWork), thenableState$1 = null, thenableIndexCounter$1 = 0, interruptedWork = workInProgress;
      for (; null !== interruptedWork; )
        unwindInterruptedWork(interruptedWork.alternate, interruptedWork), interruptedWork = interruptedWork.return;
      workInProgress = null;
    }
  }
  function prepareFreshStack(root2, lanes) {
    var timeoutHandle = root2.timeoutHandle;
    -1 !== timeoutHandle && (root2.timeoutHandle = -1, cancelTimeout(timeoutHandle));
    timeoutHandle = root2.cancelPendingCommit;
    null !== timeoutHandle && (root2.cancelPendingCommit = null, timeoutHandle());
    pendingEffectsLanes = 0;
    resetWorkInProgressStack();
    workInProgressRoot = root2;
    workInProgress = timeoutHandle = createWorkInProgress(root2.current, null);
    workInProgressRootRenderLanes = lanes;
    workInProgressSuspendedReason = 0;
    workInProgressThrownValue = null;
    workInProgressRootDidSkipSuspendedSiblings = false;
    workInProgressRootIsPrerendering = checkIfRootIsPrerendering(root2, lanes);
    workInProgressRootDidAttachPingListener = false;
    workInProgressSuspendedRetryLanes = workInProgressDeferredLane = workInProgressRootPingedLanes = workInProgressRootInterleavedUpdatedLanes = workInProgressRootSkippedLanes = workInProgressRootExitStatus = 0;
    workInProgressRootRecoverableErrors = workInProgressRootConcurrentErrors = null;
    workInProgressRootDidIncludeRecursiveRenderUpdate = false;
    0 !== (lanes & 8) && (lanes |= lanes & 32);
    var allEntangledLanes = root2.entangledLanes;
    if (0 !== allEntangledLanes)
      for (root2 = root2.entanglements, allEntangledLanes &= lanes; 0 < allEntangledLanes; ) {
        var index$4 = 31 - clz32(allEntangledLanes), lane = 1 << index$4;
        lanes |= root2[index$4];
        allEntangledLanes &= ~lane;
      }
    entangledRenderLanes = lanes;
    finishQueueingConcurrentUpdates();
    return timeoutHandle;
  }
  function handleThrow(root2, thrownValue) {
    currentlyRenderingFiber = null;
    ReactSharedInternals.H = ContextOnlyDispatcher;
    thrownValue === SuspenseException || thrownValue === SuspenseActionException ? (thrownValue = getSuspendedThenable(), workInProgressSuspendedReason = 3) : thrownValue === SuspenseyCommitException ? (thrownValue = getSuspendedThenable(), workInProgressSuspendedReason = 4) : workInProgressSuspendedReason = thrownValue === SelectiveHydrationException ? 8 : null !== thrownValue && "object" === typeof thrownValue && "function" === typeof thrownValue.then ? 6 : 1;
    workInProgressThrownValue = thrownValue;
    null === workInProgress && (workInProgressRootExitStatus = 1, logUncaughtError(
      root2,
      createCapturedValueAtFiber(thrownValue, root2.current)
    ));
  }
  function shouldRemainOnPreviousScreen() {
    var handler = suspenseHandlerStackCursor.current;
    return null === handler ? true : (workInProgressRootRenderLanes & 4194048) === workInProgressRootRenderLanes ? null === shellBoundary ? true : false : (workInProgressRootRenderLanes & 62914560) === workInProgressRootRenderLanes || 0 !== (workInProgressRootRenderLanes & 536870912) ? handler === shellBoundary : false;
  }
  function pushDispatcher() {
    var prevDispatcher = ReactSharedInternals.H;
    ReactSharedInternals.H = ContextOnlyDispatcher;
    return null === prevDispatcher ? ContextOnlyDispatcher : prevDispatcher;
  }
  function pushAsyncDispatcher() {
    var prevAsyncDispatcher = ReactSharedInternals.A;
    ReactSharedInternals.A = DefaultAsyncDispatcher;
    return prevAsyncDispatcher;
  }
  function renderDidSuspendDelayIfPossible() {
    workInProgressRootExitStatus = 4;
    workInProgressRootDidSkipSuspendedSiblings || (workInProgressRootRenderLanes & 4194048) !== workInProgressRootRenderLanes && null !== suspenseHandlerStackCursor.current || (workInProgressRootIsPrerendering = true);
    0 === (workInProgressRootSkippedLanes & 134217727) && 0 === (workInProgressRootInterleavedUpdatedLanes & 134217727) || null === workInProgressRoot || markRootSuspended(
      workInProgressRoot,
      workInProgressRootRenderLanes,
      workInProgressDeferredLane,
      false
    );
  }
  function renderRootSync(root2, lanes, shouldYieldForPrerendering) {
    var prevExecutionContext = executionContext;
    executionContext |= 2;
    var prevDispatcher = pushDispatcher(), prevAsyncDispatcher = pushAsyncDispatcher();
    if (workInProgressRoot !== root2 || workInProgressRootRenderLanes !== lanes)
      workInProgressTransitions = null, prepareFreshStack(root2, lanes);
    lanes = false;
    var exitStatus = workInProgressRootExitStatus;
    a: do
      try {
        if (0 !== workInProgressSuspendedReason && null !== workInProgress) {
          var unitOfWork = workInProgress, thrownValue = workInProgressThrownValue;
          switch (workInProgressSuspendedReason) {
            case 8:
              resetWorkInProgressStack();
              exitStatus = 6;
              break a;
            case 3:
            case 2:
            case 9:
            case 6:
              null === suspenseHandlerStackCursor.current && (lanes = true);
              var reason = workInProgressSuspendedReason;
              workInProgressSuspendedReason = 0;
              workInProgressThrownValue = null;
              throwAndUnwindWorkLoop(root2, unitOfWork, thrownValue, reason);
              if (shouldYieldForPrerendering && workInProgressRootIsPrerendering) {
                exitStatus = 0;
                break a;
              }
              break;
            default:
              reason = workInProgressSuspendedReason, workInProgressSuspendedReason = 0, workInProgressThrownValue = null, throwAndUnwindWorkLoop(root2, unitOfWork, thrownValue, reason);
          }
        }
        workLoopSync();
        exitStatus = workInProgressRootExitStatus;
        break;
      } catch (thrownValue$165) {
        handleThrow(root2, thrownValue$165);
      }
    while (1);
    lanes && root2.shellSuspendCounter++;
    lastContextDependency = currentlyRenderingFiber$1 = null;
    executionContext = prevExecutionContext;
    ReactSharedInternals.H = prevDispatcher;
    ReactSharedInternals.A = prevAsyncDispatcher;
    null === workInProgress && (workInProgressRoot = null, workInProgressRootRenderLanes = 0, finishQueueingConcurrentUpdates());
    return exitStatus;
  }
  function workLoopSync() {
    for (; null !== workInProgress; ) performUnitOfWork(workInProgress);
  }
  function renderRootConcurrent(root2, lanes) {
    var prevExecutionContext = executionContext;
    executionContext |= 2;
    var prevDispatcher = pushDispatcher(), prevAsyncDispatcher = pushAsyncDispatcher();
    workInProgressRoot !== root2 || workInProgressRootRenderLanes !== lanes ? (workInProgressTransitions = null, workInProgressRootRenderTargetTime = now() + 500, prepareFreshStack(root2, lanes)) : workInProgressRootIsPrerendering = checkIfRootIsPrerendering(
      root2,
      lanes
    );
    a: do
      try {
        if (0 !== workInProgressSuspendedReason && null !== workInProgress) {
          lanes = workInProgress;
          var thrownValue = workInProgressThrownValue;
          b: switch (workInProgressSuspendedReason) {
            case 1:
              workInProgressSuspendedReason = 0;
              workInProgressThrownValue = null;
              throwAndUnwindWorkLoop(root2, lanes, thrownValue, 1);
              break;
            case 2:
            case 9:
              if (isThenableResolved(thrownValue)) {
                workInProgressSuspendedReason = 0;
                workInProgressThrownValue = null;
                replaySuspendedUnitOfWork(lanes);
                break;
              }
              lanes = function() {
                2 !== workInProgressSuspendedReason && 9 !== workInProgressSuspendedReason || workInProgressRoot !== root2 || (workInProgressSuspendedReason = 7);
                ensureRootIsScheduled(root2);
              };
              thrownValue.then(lanes, lanes);
              break a;
            case 3:
              workInProgressSuspendedReason = 7;
              break a;
            case 4:
              workInProgressSuspendedReason = 5;
              break a;
            case 7:
              isThenableResolved(thrownValue) ? (workInProgressSuspendedReason = 0, workInProgressThrownValue = null, replaySuspendedUnitOfWork(lanes)) : (workInProgressSuspendedReason = 0, workInProgressThrownValue = null, throwAndUnwindWorkLoop(root2, lanes, thrownValue, 7));
              break;
            case 5:
              var resource = null;
              switch (workInProgress.tag) {
                case 26:
                  resource = workInProgress.memoizedState;
                case 5:
                case 27:
                  var hostFiber = workInProgress;
                  if (resource ? preloadResource(resource) : hostFiber.stateNode.complete) {
                    workInProgressSuspendedReason = 0;
                    workInProgressThrownValue = null;
                    var sibling = hostFiber.sibling;
                    if (null !== sibling) workInProgress = sibling;
                    else {
                      var returnFiber = hostFiber.return;
                      null !== returnFiber ? (workInProgress = returnFiber, completeUnitOfWork(returnFiber)) : workInProgress = null;
                    }
                    break b;
                  }
              }
              workInProgressSuspendedReason = 0;
              workInProgressThrownValue = null;
              throwAndUnwindWorkLoop(root2, lanes, thrownValue, 5);
              break;
            case 6:
              workInProgressSuspendedReason = 0;
              workInProgressThrownValue = null;
              throwAndUnwindWorkLoop(root2, lanes, thrownValue, 6);
              break;
            case 8:
              resetWorkInProgressStack();
              workInProgressRootExitStatus = 6;
              break a;
            default:
              throw Error(formatProdErrorMessage(462));
          }
        }
        workLoopConcurrentByScheduler();
        break;
      } catch (thrownValue$167) {
        handleThrow(root2, thrownValue$167);
      }
    while (1);
    lastContextDependency = currentlyRenderingFiber$1 = null;
    ReactSharedInternals.H = prevDispatcher;
    ReactSharedInternals.A = prevAsyncDispatcher;
    executionContext = prevExecutionContext;
    if (null !== workInProgress) return 0;
    workInProgressRoot = null;
    workInProgressRootRenderLanes = 0;
    finishQueueingConcurrentUpdates();
    return workInProgressRootExitStatus;
  }
  function workLoopConcurrentByScheduler() {
    for (; null !== workInProgress && !shouldYield(); )
      performUnitOfWork(workInProgress);
  }
  function performUnitOfWork(unitOfWork) {
    var next = beginWork(unitOfWork.alternate, unitOfWork, entangledRenderLanes);
    unitOfWork.memoizedProps = unitOfWork.pendingProps;
    null === next ? completeUnitOfWork(unitOfWork) : workInProgress = next;
  }
  function replaySuspendedUnitOfWork(unitOfWork) {
    var next = unitOfWork;
    var current = next.alternate;
    switch (next.tag) {
      case 15:
      case 0:
        next = replayFunctionComponent(
          current,
          next,
          next.pendingProps,
          next.type,
          void 0,
          workInProgressRootRenderLanes
        );
        break;
      case 11:
        next = replayFunctionComponent(
          current,
          next,
          next.pendingProps,
          next.type.render,
          next.ref,
          workInProgressRootRenderLanes
        );
        break;
      case 5:
        resetHooksOnUnwind(next);
      default:
        unwindInterruptedWork(current, next), next = workInProgress = resetWorkInProgress(next, entangledRenderLanes), next = beginWork(current, next, entangledRenderLanes);
    }
    unitOfWork.memoizedProps = unitOfWork.pendingProps;
    null === next ? completeUnitOfWork(unitOfWork) : workInProgress = next;
  }
  function throwAndUnwindWorkLoop(root2, unitOfWork, thrownValue, suspendedReason) {
    lastContextDependency = currentlyRenderingFiber$1 = null;
    resetHooksOnUnwind(unitOfWork);
    thenableState$1 = null;
    thenableIndexCounter$1 = 0;
    var returnFiber = unitOfWork.return;
    try {
      if (throwException(
        root2,
        returnFiber,
        unitOfWork,
        thrownValue,
        workInProgressRootRenderLanes
      )) {
        workInProgressRootExitStatus = 1;
        logUncaughtError(
          root2,
          createCapturedValueAtFiber(thrownValue, root2.current)
        );
        workInProgress = null;
        return;
      }
    } catch (error) {
      if (null !== returnFiber) throw workInProgress = returnFiber, error;
      workInProgressRootExitStatus = 1;
      logUncaughtError(
        root2,
        createCapturedValueAtFiber(thrownValue, root2.current)
      );
      workInProgress = null;
      return;
    }
    if (unitOfWork.flags & 32768) {
      if (isHydrating || 1 === suspendedReason) root2 = true;
      else if (workInProgressRootIsPrerendering || 0 !== (workInProgressRootRenderLanes & 536870912))
        root2 = false;
      else if (workInProgressRootDidSkipSuspendedSiblings = root2 = true, 2 === suspendedReason || 9 === suspendedReason || 3 === suspendedReason || 6 === suspendedReason)
        suspendedReason = suspenseHandlerStackCursor.current, null !== suspendedReason && 13 === suspendedReason.tag && (suspendedReason.flags |= 16384);
      unwindUnitOfWork(unitOfWork, root2);
    } else completeUnitOfWork(unitOfWork);
  }
  function completeUnitOfWork(unitOfWork) {
    var completedWork = unitOfWork;
    do {
      if (0 !== (completedWork.flags & 32768)) {
        unwindUnitOfWork(
          completedWork,
          workInProgressRootDidSkipSuspendedSiblings
        );
        return;
      }
      unitOfWork = completedWork.return;
      var next = completeWork(
        completedWork.alternate,
        completedWork,
        entangledRenderLanes
      );
      if (null !== next) {
        workInProgress = next;
        return;
      }
      completedWork = completedWork.sibling;
      if (null !== completedWork) {
        workInProgress = completedWork;
        return;
      }
      workInProgress = completedWork = unitOfWork;
    } while (null !== completedWork);
    0 === workInProgressRootExitStatus && (workInProgressRootExitStatus = 5);
  }
  function unwindUnitOfWork(unitOfWork, skipSiblings) {
    do {
      var next = unwindWork(unitOfWork.alternate, unitOfWork);
      if (null !== next) {
        next.flags &= 32767;
        workInProgress = next;
        return;
      }
      next = unitOfWork.return;
      null !== next && (next.flags |= 32768, next.subtreeFlags = 0, next.deletions = null);
      if (!skipSiblings && (unitOfWork = unitOfWork.sibling, null !== unitOfWork)) {
        workInProgress = unitOfWork;
        return;
      }
      workInProgress = unitOfWork = next;
    } while (null !== unitOfWork);
    workInProgressRootExitStatus = 6;
    workInProgress = null;
  }
  function commitRoot(root2, finishedWork, lanes, recoverableErrors, transitions, didIncludeRenderPhaseUpdate, spawnedLane, updatedLanes, suspendedRetryLanes) {
    root2.cancelPendingCommit = null;
    do
      flushPendingEffects();
    while (0 !== pendingEffectsStatus);
    if (0 !== (executionContext & 6)) throw Error(formatProdErrorMessage(327));
    if (null !== finishedWork) {
      if (finishedWork === root2.current) throw Error(formatProdErrorMessage(177));
      didIncludeRenderPhaseUpdate = finishedWork.lanes | finishedWork.childLanes;
      didIncludeRenderPhaseUpdate |= concurrentlyUpdatedLanes;
      markRootFinished(
        root2,
        lanes,
        didIncludeRenderPhaseUpdate,
        spawnedLane,
        updatedLanes,
        suspendedRetryLanes
      );
      root2 === workInProgressRoot && (workInProgress = workInProgressRoot = null, workInProgressRootRenderLanes = 0);
      pendingFinishedWork = finishedWork;
      pendingEffectsRoot = root2;
      pendingEffectsLanes = lanes;
      pendingEffectsRemainingLanes = didIncludeRenderPhaseUpdate;
      pendingPassiveTransitions = transitions;
      pendingRecoverableErrors = recoverableErrors;
      0 !== (finishedWork.subtreeFlags & 10256) || 0 !== (finishedWork.flags & 10256) ? (root2.callbackNode = null, root2.callbackPriority = 0, scheduleCallback$1(NormalPriority$1, function() {
        flushPassiveEffects();
        return null;
      })) : (root2.callbackNode = null, root2.callbackPriority = 0);
      recoverableErrors = 0 !== (finishedWork.flags & 13878);
      if (0 !== (finishedWork.subtreeFlags & 13878) || recoverableErrors) {
        recoverableErrors = ReactSharedInternals.T;
        ReactSharedInternals.T = null;
        transitions = ReactDOMSharedInternals.p;
        ReactDOMSharedInternals.p = 2;
        spawnedLane = executionContext;
        executionContext |= 4;
        try {
          commitBeforeMutationEffects(root2, finishedWork, lanes);
        } finally {
          executionContext = spawnedLane, ReactDOMSharedInternals.p = transitions, ReactSharedInternals.T = recoverableErrors;
        }
      }
      pendingEffectsStatus = 1;
      flushMutationEffects();
      flushLayoutEffects();
      flushSpawnedWork();
    }
  }
  function flushMutationEffects() {
    if (1 === pendingEffectsStatus) {
      pendingEffectsStatus = 0;
      var root2 = pendingEffectsRoot, finishedWork = pendingFinishedWork, rootMutationHasEffect = 0 !== (finishedWork.flags & 13878);
      if (0 !== (finishedWork.subtreeFlags & 13878) || rootMutationHasEffect) {
        rootMutationHasEffect = ReactSharedInternals.T;
        ReactSharedInternals.T = null;
        var previousPriority = ReactDOMSharedInternals.p;
        ReactDOMSharedInternals.p = 2;
        var prevExecutionContext = executionContext;
        executionContext |= 4;
        try {
          commitMutationEffectsOnFiber(finishedWork, root2);
          var priorSelectionInformation = selectionInformation, curFocusedElem = getActiveElementDeep(root2.containerInfo), priorFocusedElem = priorSelectionInformation.focusedElem, priorSelectionRange = priorSelectionInformation.selectionRange;
          if (curFocusedElem !== priorFocusedElem && priorFocusedElem && priorFocusedElem.ownerDocument && containsNode(
            priorFocusedElem.ownerDocument.documentElement,
            priorFocusedElem
          )) {
            if (null !== priorSelectionRange && hasSelectionCapabilities(priorFocusedElem)) {
              var start = priorSelectionRange.start, end = priorSelectionRange.end;
              void 0 === end && (end = start);
              if ("selectionStart" in priorFocusedElem)
                priorFocusedElem.selectionStart = start, priorFocusedElem.selectionEnd = Math.min(
                  end,
                  priorFocusedElem.value.length
                );
              else {
                var doc = priorFocusedElem.ownerDocument || document, win = doc && doc.defaultView || window;
                if (win.getSelection) {
                  var selection = win.getSelection(), length = priorFocusedElem.textContent.length, start$jscomp$0 = Math.min(priorSelectionRange.start, length), end$jscomp$0 = void 0 === priorSelectionRange.end ? start$jscomp$0 : Math.min(priorSelectionRange.end, length);
                  !selection.extend && start$jscomp$0 > end$jscomp$0 && (curFocusedElem = end$jscomp$0, end$jscomp$0 = start$jscomp$0, start$jscomp$0 = curFocusedElem);
                  var startMarker = getNodeForCharacterOffset(
                    priorFocusedElem,
                    start$jscomp$0
                  ), endMarker = getNodeForCharacterOffset(
                    priorFocusedElem,
                    end$jscomp$0
                  );
                  if (startMarker && endMarker && (1 !== selection.rangeCount || selection.anchorNode !== startMarker.node || selection.anchorOffset !== startMarker.offset || selection.focusNode !== endMarker.node || selection.focusOffset !== endMarker.offset)) {
                    var range = doc.createRange();
                    range.setStart(startMarker.node, startMarker.offset);
                    selection.removeAllRanges();
                    start$jscomp$0 > end$jscomp$0 ? (selection.addRange(range), selection.extend(endMarker.node, endMarker.offset)) : (range.setEnd(endMarker.node, endMarker.offset), selection.addRange(range));
                  }
                }
              }
            }
            doc = [];
            for (selection = priorFocusedElem; selection = selection.parentNode; )
              1 === selection.nodeType && doc.push({
                element: selection,
                left: selection.scrollLeft,
                top: selection.scrollTop
              });
            "function" === typeof priorFocusedElem.focus && priorFocusedElem.focus();
            for (priorFocusedElem = 0; priorFocusedElem < doc.length; priorFocusedElem++) {
              var info = doc[priorFocusedElem];
              info.element.scrollLeft = info.left;
              info.element.scrollTop = info.top;
            }
          }
          _enabled = !!eventsEnabled;
          selectionInformation = eventsEnabled = null;
        } finally {
          executionContext = prevExecutionContext, ReactDOMSharedInternals.p = previousPriority, ReactSharedInternals.T = rootMutationHasEffect;
        }
      }
      root2.current = finishedWork;
      pendingEffectsStatus = 2;
    }
  }
  function flushLayoutEffects() {
    if (2 === pendingEffectsStatus) {
      pendingEffectsStatus = 0;
      var root2 = pendingEffectsRoot, finishedWork = pendingFinishedWork, rootHasLayoutEffect = 0 !== (finishedWork.flags & 8772);
      if (0 !== (finishedWork.subtreeFlags & 8772) || rootHasLayoutEffect) {
        rootHasLayoutEffect = ReactSharedInternals.T;
        ReactSharedInternals.T = null;
        var previousPriority = ReactDOMSharedInternals.p;
        ReactDOMSharedInternals.p = 2;
        var prevExecutionContext = executionContext;
        executionContext |= 4;
        try {
          commitLayoutEffectOnFiber(root2, finishedWork.alternate, finishedWork);
        } finally {
          executionContext = prevExecutionContext, ReactDOMSharedInternals.p = previousPriority, ReactSharedInternals.T = rootHasLayoutEffect;
        }
      }
      pendingEffectsStatus = 3;
    }
  }
  function flushSpawnedWork() {
    if (4 === pendingEffectsStatus || 3 === pendingEffectsStatus) {
      pendingEffectsStatus = 0;
      requestPaint();
      var root2 = pendingEffectsRoot, finishedWork = pendingFinishedWork, lanes = pendingEffectsLanes, recoverableErrors = pendingRecoverableErrors;
      0 !== (finishedWork.subtreeFlags & 10256) || 0 !== (finishedWork.flags & 10256) ? pendingEffectsStatus = 5 : (pendingEffectsStatus = 0, pendingFinishedWork = pendingEffectsRoot = null, releaseRootPooledCache(root2, root2.pendingLanes));
      var remainingLanes = root2.pendingLanes;
      0 === remainingLanes && (legacyErrorBoundariesThatAlreadyFailed = null);
      lanesToEventPriority(lanes);
      finishedWork = finishedWork.stateNode;
      if (injectedHook && "function" === typeof injectedHook.onCommitFiberRoot)
        try {
          injectedHook.onCommitFiberRoot(
            rendererID,
            finishedWork,
            void 0,
            128 === (finishedWork.current.flags & 128)
          );
        } catch (err) {
        }
      if (null !== recoverableErrors) {
        finishedWork = ReactSharedInternals.T;
        remainingLanes = ReactDOMSharedInternals.p;
        ReactDOMSharedInternals.p = 2;
        ReactSharedInternals.T = null;
        try {
          for (var onRecoverableError = root2.onRecoverableError, i = 0; i < recoverableErrors.length; i++) {
            var recoverableError = recoverableErrors[i];
            onRecoverableError(recoverableError.value, {
              componentStack: recoverableError.stack
            });
          }
        } finally {
          ReactSharedInternals.T = finishedWork, ReactDOMSharedInternals.p = remainingLanes;
        }
      }
      0 !== (pendingEffectsLanes & 3) && flushPendingEffects();
      ensureRootIsScheduled(root2);
      remainingLanes = root2.pendingLanes;
      0 !== (lanes & 261930) && 0 !== (remainingLanes & 42) ? root2 === rootWithNestedUpdates ? nestedUpdateCount++ : (nestedUpdateCount = 0, rootWithNestedUpdates = root2) : nestedUpdateCount = 0;
      flushSyncWorkAcrossRoots_impl(0);
    }
  }
  function releaseRootPooledCache(root2, remainingLanes) {
    0 === (root2.pooledCacheLanes &= remainingLanes) && (remainingLanes = root2.pooledCache, null != remainingLanes && (root2.pooledCache = null, releaseCache(remainingLanes)));
  }
  function flushPendingEffects() {
    flushMutationEffects();
    flushLayoutEffects();
    flushSpawnedWork();
    return flushPassiveEffects();
  }
  function flushPassiveEffects() {
    if (5 !== pendingEffectsStatus) return false;
    var root2 = pendingEffectsRoot, remainingLanes = pendingEffectsRemainingLanes;
    pendingEffectsRemainingLanes = 0;
    var renderPriority = lanesToEventPriority(pendingEffectsLanes), prevTransition = ReactSharedInternals.T, previousPriority = ReactDOMSharedInternals.p;
    try {
      ReactDOMSharedInternals.p = 32 > renderPriority ? 32 : renderPriority;
      ReactSharedInternals.T = null;
      renderPriority = pendingPassiveTransitions;
      pendingPassiveTransitions = null;
      var root$jscomp$0 = pendingEffectsRoot, lanes = pendingEffectsLanes;
      pendingEffectsStatus = 0;
      pendingFinishedWork = pendingEffectsRoot = null;
      pendingEffectsLanes = 0;
      if (0 !== (executionContext & 6)) throw Error(formatProdErrorMessage(331));
      var prevExecutionContext = executionContext;
      executionContext |= 4;
      commitPassiveUnmountOnFiber(root$jscomp$0.current);
      commitPassiveMountOnFiber(
        root$jscomp$0,
        root$jscomp$0.current,
        lanes,
        renderPriority
      );
      executionContext = prevExecutionContext;
      flushSyncWorkAcrossRoots_impl(0, false);
      if (injectedHook && "function" === typeof injectedHook.onPostCommitFiberRoot)
        try {
          injectedHook.onPostCommitFiberRoot(rendererID, root$jscomp$0);
        } catch (err) {
        }
      return true;
    } finally {
      ReactDOMSharedInternals.p = previousPriority, ReactSharedInternals.T = prevTransition, releaseRootPooledCache(root2, remainingLanes);
    }
  }
  function captureCommitPhaseErrorOnRoot(rootFiber, sourceFiber, error) {
    sourceFiber = createCapturedValueAtFiber(error, sourceFiber);
    sourceFiber = createRootErrorUpdate(rootFiber.stateNode, sourceFiber, 2);
    rootFiber = enqueueUpdate(rootFiber, sourceFiber, 2);
    null !== rootFiber && (markRootUpdated$1(rootFiber, 2), ensureRootIsScheduled(rootFiber));
  }
  function captureCommitPhaseError(sourceFiber, nearestMountedAncestor, error) {
    if (3 === sourceFiber.tag)
      captureCommitPhaseErrorOnRoot(sourceFiber, sourceFiber, error);
    else
      for (; null !== nearestMountedAncestor; ) {
        if (3 === nearestMountedAncestor.tag) {
          captureCommitPhaseErrorOnRoot(
            nearestMountedAncestor,
            sourceFiber,
            error
          );
          break;
        } else if (1 === nearestMountedAncestor.tag) {
          var instance = nearestMountedAncestor.stateNode;
          if ("function" === typeof nearestMountedAncestor.type.getDerivedStateFromError || "function" === typeof instance.componentDidCatch && (null === legacyErrorBoundariesThatAlreadyFailed || !legacyErrorBoundariesThatAlreadyFailed.has(instance))) {
            sourceFiber = createCapturedValueAtFiber(error, sourceFiber);
            error = createClassErrorUpdate(2);
            instance = enqueueUpdate(nearestMountedAncestor, error, 2);
            null !== instance && (initializeClassErrorUpdate(
              error,
              instance,
              nearestMountedAncestor,
              sourceFiber
            ), markRootUpdated$1(instance, 2), ensureRootIsScheduled(instance));
            break;
          }
        }
        nearestMountedAncestor = nearestMountedAncestor.return;
      }
  }
  function attachPingListener(root2, wakeable, lanes) {
    var pingCache = root2.pingCache;
    if (null === pingCache) {
      pingCache = root2.pingCache = new PossiblyWeakMap();
      var threadIDs = /* @__PURE__ */ new Set();
      pingCache.set(wakeable, threadIDs);
    } else
      threadIDs = pingCache.get(wakeable), void 0 === threadIDs && (threadIDs = /* @__PURE__ */ new Set(), pingCache.set(wakeable, threadIDs));
    threadIDs.has(lanes) || (workInProgressRootDidAttachPingListener = true, threadIDs.add(lanes), root2 = pingSuspendedRoot.bind(null, root2, wakeable, lanes), wakeable.then(root2, root2));
  }
  function pingSuspendedRoot(root2, wakeable, pingedLanes) {
    var pingCache = root2.pingCache;
    null !== pingCache && pingCache.delete(wakeable);
    root2.pingedLanes |= root2.suspendedLanes & pingedLanes;
    root2.warmLanes &= ~pingedLanes;
    workInProgressRoot === root2 && (workInProgressRootRenderLanes & pingedLanes) === pingedLanes && (4 === workInProgressRootExitStatus || 3 === workInProgressRootExitStatus && (workInProgressRootRenderLanes & 62914560) === workInProgressRootRenderLanes && 300 > now() - globalMostRecentFallbackTime ? 0 === (executionContext & 2) && prepareFreshStack(root2, 0) : workInProgressRootPingedLanes |= pingedLanes, workInProgressSuspendedRetryLanes === workInProgressRootRenderLanes && (workInProgressSuspendedRetryLanes = 0));
    ensureRootIsScheduled(root2);
  }
  function retryTimedOutBoundary(boundaryFiber, retryLane) {
    0 === retryLane && (retryLane = claimNextRetryLane());
    boundaryFiber = enqueueConcurrentRenderForLane(boundaryFiber, retryLane);
    null !== boundaryFiber && (markRootUpdated$1(boundaryFiber, retryLane), ensureRootIsScheduled(boundaryFiber));
  }
  function retryDehydratedSuspenseBoundary(boundaryFiber) {
    var suspenseState = boundaryFiber.memoizedState, retryLane = 0;
    null !== suspenseState && (retryLane = suspenseState.retryLane);
    retryTimedOutBoundary(boundaryFiber, retryLane);
  }
  function resolveRetryWakeable(boundaryFiber, wakeable) {
    var retryLane = 0;
    switch (boundaryFiber.tag) {
      case 31:
      case 13:
        var retryCache = boundaryFiber.stateNode;
        var suspenseState = boundaryFiber.memoizedState;
        null !== suspenseState && (retryLane = suspenseState.retryLane);
        break;
      case 19:
        retryCache = boundaryFiber.stateNode;
        break;
      case 22:
        retryCache = boundaryFiber.stateNode._retryCache;
        break;
      default:
        throw Error(formatProdErrorMessage(314));
    }
    null !== retryCache && retryCache.delete(wakeable);
    retryTimedOutBoundary(boundaryFiber, retryLane);
  }
  function scheduleCallback$1(priorityLevel, callback) {
    return scheduleCallback$3(priorityLevel, callback);
  }
  var firstScheduledRoot = null, lastScheduledRoot = null, didScheduleMicrotask = false, mightHavePendingSyncWork = false, isFlushingWork = false, currentEventTransitionLane = 0;
  function ensureRootIsScheduled(root2) {
    root2 !== lastScheduledRoot && null === root2.next && (null === lastScheduledRoot ? firstScheduledRoot = lastScheduledRoot = root2 : lastScheduledRoot = lastScheduledRoot.next = root2);
    mightHavePendingSyncWork = true;
    didScheduleMicrotask || (didScheduleMicrotask = true, scheduleImmediateRootScheduleTask());
  }
  function flushSyncWorkAcrossRoots_impl(syncTransitionLanes, onlyLegacy) {
    if (!isFlushingWork && mightHavePendingSyncWork) {
      isFlushingWork = true;
      do {
        var didPerformSomeWork = false;
        for (var root$170 = firstScheduledRoot; null !== root$170; ) {
          if (0 !== syncTransitionLanes) {
            var pendingLanes = root$170.pendingLanes;
            if (0 === pendingLanes) var JSCompiler_inline_result = 0;
            else {
              var suspendedLanes = root$170.suspendedLanes, pingedLanes = root$170.pingedLanes;
              JSCompiler_inline_result = (1 << 31 - clz32(42 | syncTransitionLanes) + 1) - 1;
              JSCompiler_inline_result &= pendingLanes & ~(suspendedLanes & ~pingedLanes);
              JSCompiler_inline_result = JSCompiler_inline_result & 201326741 ? JSCompiler_inline_result & 201326741 | 1 : JSCompiler_inline_result ? JSCompiler_inline_result | 2 : 0;
            }
            0 !== JSCompiler_inline_result && (didPerformSomeWork = true, performSyncWorkOnRoot(root$170, JSCompiler_inline_result));
          } else
            JSCompiler_inline_result = workInProgressRootRenderLanes, JSCompiler_inline_result = getNextLanes(
              root$170,
              root$170 === workInProgressRoot ? JSCompiler_inline_result : 0,
              null !== root$170.cancelPendingCommit || -1 !== root$170.timeoutHandle
            ), 0 === (JSCompiler_inline_result & 3) || checkIfRootIsPrerendering(root$170, JSCompiler_inline_result) || (didPerformSomeWork = true, performSyncWorkOnRoot(root$170, JSCompiler_inline_result));
          root$170 = root$170.next;
        }
      } while (didPerformSomeWork);
      isFlushingWork = false;
    }
  }
  function processRootScheduleInImmediateTask() {
    processRootScheduleInMicrotask();
  }
  function processRootScheduleInMicrotask() {
    mightHavePendingSyncWork = didScheduleMicrotask = false;
    var syncTransitionLanes = 0;
    0 !== currentEventTransitionLane && shouldAttemptEagerTransition() && (syncTransitionLanes = currentEventTransitionLane);
    for (var currentTime = now(), prev = null, root2 = firstScheduledRoot; null !== root2; ) {
      var next = root2.next, nextLanes = scheduleTaskForRootDuringMicrotask(root2, currentTime);
      if (0 === nextLanes)
        root2.next = null, null === prev ? firstScheduledRoot = next : prev.next = next, null === next && (lastScheduledRoot = prev);
      else if (prev = root2, 0 !== syncTransitionLanes || 0 !== (nextLanes & 3))
        mightHavePendingSyncWork = true;
      root2 = next;
    }
    0 !== pendingEffectsStatus && 5 !== pendingEffectsStatus || flushSyncWorkAcrossRoots_impl(syncTransitionLanes);
    0 !== currentEventTransitionLane && (currentEventTransitionLane = 0);
  }
  function scheduleTaskForRootDuringMicrotask(root2, currentTime) {
    for (var suspendedLanes = root2.suspendedLanes, pingedLanes = root2.pingedLanes, expirationTimes = root2.expirationTimes, lanes = root2.pendingLanes & -62914561; 0 < lanes; ) {
      var index$5 = 31 - clz32(lanes), lane = 1 << index$5, expirationTime = expirationTimes[index$5];
      if (-1 === expirationTime) {
        if (0 === (lane & suspendedLanes) || 0 !== (lane & pingedLanes))
          expirationTimes[index$5] = computeExpirationTime(lane, currentTime);
      } else expirationTime <= currentTime && (root2.expiredLanes |= lane);
      lanes &= ~lane;
    }
    currentTime = workInProgressRoot;
    suspendedLanes = workInProgressRootRenderLanes;
    suspendedLanes = getNextLanes(
      root2,
      root2 === currentTime ? suspendedLanes : 0,
      null !== root2.cancelPendingCommit || -1 !== root2.timeoutHandle
    );
    pingedLanes = root2.callbackNode;
    if (0 === suspendedLanes || root2 === currentTime && (2 === workInProgressSuspendedReason || 9 === workInProgressSuspendedReason) || null !== root2.cancelPendingCommit)
      return null !== pingedLanes && null !== pingedLanes && cancelCallback$1(pingedLanes), root2.callbackNode = null, root2.callbackPriority = 0;
    if (0 === (suspendedLanes & 3) || checkIfRootIsPrerendering(root2, suspendedLanes)) {
      currentTime = suspendedLanes & -suspendedLanes;
      if (currentTime === root2.callbackPriority) return currentTime;
      null !== pingedLanes && cancelCallback$1(pingedLanes);
      switch (lanesToEventPriority(suspendedLanes)) {
        case 2:
        case 8:
          suspendedLanes = UserBlockingPriority;
          break;
        case 32:
          suspendedLanes = NormalPriority$1;
          break;
        case 268435456:
          suspendedLanes = IdlePriority;
          break;
        default:
          suspendedLanes = NormalPriority$1;
      }
      pingedLanes = performWorkOnRootViaSchedulerTask.bind(null, root2);
      suspendedLanes = scheduleCallback$3(suspendedLanes, pingedLanes);
      root2.callbackPriority = currentTime;
      root2.callbackNode = suspendedLanes;
      return currentTime;
    }
    null !== pingedLanes && null !== pingedLanes && cancelCallback$1(pingedLanes);
    root2.callbackPriority = 2;
    root2.callbackNode = null;
    return 2;
  }
  function performWorkOnRootViaSchedulerTask(root2, didTimeout) {
    if (0 !== pendingEffectsStatus && 5 !== pendingEffectsStatus)
      return root2.callbackNode = null, root2.callbackPriority = 0, null;
    var originalCallbackNode = root2.callbackNode;
    if (flushPendingEffects() && root2.callbackNode !== originalCallbackNode)
      return null;
    var workInProgressRootRenderLanes$jscomp$0 = workInProgressRootRenderLanes;
    workInProgressRootRenderLanes$jscomp$0 = getNextLanes(
      root2,
      root2 === workInProgressRoot ? workInProgressRootRenderLanes$jscomp$0 : 0,
      null !== root2.cancelPendingCommit || -1 !== root2.timeoutHandle
    );
    if (0 === workInProgressRootRenderLanes$jscomp$0) return null;
    performWorkOnRoot(root2, workInProgressRootRenderLanes$jscomp$0, didTimeout);
    scheduleTaskForRootDuringMicrotask(root2, now());
    return null != root2.callbackNode && root2.callbackNode === originalCallbackNode ? performWorkOnRootViaSchedulerTask.bind(null, root2) : null;
  }
  function performSyncWorkOnRoot(root2, lanes) {
    if (flushPendingEffects()) return null;
    performWorkOnRoot(root2, lanes, true);
  }
  function scheduleImmediateRootScheduleTask() {
    scheduleMicrotask(function() {
      0 !== (executionContext & 6) ? scheduleCallback$3(
        ImmediatePriority,
        processRootScheduleInImmediateTask
      ) : processRootScheduleInMicrotask();
    });
  }
  function requestTransitionLane() {
    if (0 === currentEventTransitionLane) {
      var actionScopeLane = currentEntangledLane;
      0 === actionScopeLane && (actionScopeLane = nextTransitionUpdateLane, nextTransitionUpdateLane <<= 1, 0 === (nextTransitionUpdateLane & 261888) && (nextTransitionUpdateLane = 256));
      currentEventTransitionLane = actionScopeLane;
    }
    return currentEventTransitionLane;
  }
  function coerceFormActionProp(actionProp) {
    return null == actionProp || "symbol" === typeof actionProp || "boolean" === typeof actionProp ? null : "function" === typeof actionProp ? actionProp : sanitizeURL("" + actionProp);
  }
  function createFormDataWithSubmitter(form, submitter) {
    var temp = submitter.ownerDocument.createElement("input");
    temp.name = submitter.name;
    temp.value = submitter.value;
    form.id && temp.setAttribute("form", form.id);
    submitter.parentNode.insertBefore(temp, submitter);
    form = new FormData(form);
    temp.parentNode.removeChild(temp);
    return form;
  }
  function extractEvents$1(dispatchQueue, domEventName, maybeTargetInst, nativeEvent, nativeEventTarget) {
    if ("submit" === domEventName && maybeTargetInst && maybeTargetInst.stateNode === nativeEventTarget) {
      var action = coerceFormActionProp(
        (nativeEventTarget[internalPropsKey] || null).action
      ), submitter = nativeEvent.submitter;
      submitter && (domEventName = (domEventName = submitter[internalPropsKey] || null) ? coerceFormActionProp(domEventName.formAction) : submitter.getAttribute("formAction"), null !== domEventName && (action = domEventName, submitter = null));
      var event = new SyntheticEvent(
        "action",
        "action",
        null,
        nativeEvent,
        nativeEventTarget
      );
      dispatchQueue.push({
        event,
        listeners: [
          {
            instance: null,
            listener: function() {
              if (nativeEvent.defaultPrevented) {
                if (0 !== currentEventTransitionLane) {
                  var formData = submitter ? createFormDataWithSubmitter(nativeEventTarget, submitter) : new FormData(nativeEventTarget);
                  startHostTransition(
                    maybeTargetInst,
                    {
                      pending: true,
                      data: formData,
                      method: nativeEventTarget.method,
                      action
                    },
                    null,
                    formData
                  );
                }
              } else
                "function" === typeof action && (event.preventDefault(), formData = submitter ? createFormDataWithSubmitter(nativeEventTarget, submitter) : new FormData(nativeEventTarget), startHostTransition(
                  maybeTargetInst,
                  {
                    pending: true,
                    data: formData,
                    method: nativeEventTarget.method,
                    action
                  },
                  action,
                  formData
                ));
            },
            currentTarget: nativeEventTarget
          }
        ]
      });
    }
  }
  for (var i$jscomp$inline_1577 = 0; i$jscomp$inline_1577 < simpleEventPluginEvents.length; i$jscomp$inline_1577++) {
    var eventName$jscomp$inline_1578 = simpleEventPluginEvents[i$jscomp$inline_1577], domEventName$jscomp$inline_1579 = eventName$jscomp$inline_1578.toLowerCase(), capitalizedEvent$jscomp$inline_1580 = eventName$jscomp$inline_1578[0].toUpperCase() + eventName$jscomp$inline_1578.slice(1);
    registerSimpleEvent(
      domEventName$jscomp$inline_1579,
      "on" + capitalizedEvent$jscomp$inline_1580
    );
  }
  registerSimpleEvent(ANIMATION_END, "onAnimationEnd");
  registerSimpleEvent(ANIMATION_ITERATION, "onAnimationIteration");
  registerSimpleEvent(ANIMATION_START, "onAnimationStart");
  registerSimpleEvent("dblclick", "onDoubleClick");
  registerSimpleEvent("focusin", "onFocus");
  registerSimpleEvent("focusout", "onBlur");
  registerSimpleEvent(TRANSITION_RUN, "onTransitionRun");
  registerSimpleEvent(TRANSITION_START, "onTransitionStart");
  registerSimpleEvent(TRANSITION_CANCEL, "onTransitionCancel");
  registerSimpleEvent(TRANSITION_END, "onTransitionEnd");
  registerDirectEvent("onMouseEnter", ["mouseout", "mouseover"]);
  registerDirectEvent("onMouseLeave", ["mouseout", "mouseover"]);
  registerDirectEvent("onPointerEnter", ["pointerout", "pointerover"]);
  registerDirectEvent("onPointerLeave", ["pointerout", "pointerover"]);
  registerTwoPhaseEvent(
    "onChange",
    "change click focusin focusout input keydown keyup selectionchange".split(" ")
  );
  registerTwoPhaseEvent(
    "onSelect",
    "focusout contextmenu dragend focusin keydown keyup mousedown mouseup selectionchange".split(
      " "
    )
  );
  registerTwoPhaseEvent("onBeforeInput", [
    "compositionend",
    "keypress",
    "textInput",
    "paste"
  ]);
  registerTwoPhaseEvent(
    "onCompositionEnd",
    "compositionend focusout keydown keypress keyup mousedown".split(" ")
  );
  registerTwoPhaseEvent(
    "onCompositionStart",
    "compositionstart focusout keydown keypress keyup mousedown".split(" ")
  );
  registerTwoPhaseEvent(
    "onCompositionUpdate",
    "compositionupdate focusout keydown keypress keyup mousedown".split(" ")
  );
  var mediaEventTypes = "abort canplay canplaythrough durationchange emptied encrypted ended error loadeddata loadedmetadata loadstart pause play playing progress ratechange resize seeked seeking stalled suspend timeupdate volumechange waiting".split(
    " "
  ), nonDelegatedEvents = new Set(
    "beforetoggle cancel close invalid load scroll scrollend toggle".split(" ").concat(mediaEventTypes)
  );
  function processDispatchQueue(dispatchQueue, eventSystemFlags) {
    eventSystemFlags = 0 !== (eventSystemFlags & 4);
    for (var i = 0; i < dispatchQueue.length; i++) {
      var _dispatchQueue$i = dispatchQueue[i], event = _dispatchQueue$i.event;
      _dispatchQueue$i = _dispatchQueue$i.listeners;
      a: {
        var previousInstance = void 0;
        if (eventSystemFlags)
          for (var i$jscomp$0 = _dispatchQueue$i.length - 1; 0 <= i$jscomp$0; i$jscomp$0--) {
            var _dispatchListeners$i = _dispatchQueue$i[i$jscomp$0], instance = _dispatchListeners$i.instance, currentTarget = _dispatchListeners$i.currentTarget;
            _dispatchListeners$i = _dispatchListeners$i.listener;
            if (instance !== previousInstance && event.isPropagationStopped())
              break a;
            previousInstance = _dispatchListeners$i;
            event.currentTarget = currentTarget;
            try {
              previousInstance(event);
            } catch (error) {
              reportGlobalError(error);
            }
            event.currentTarget = null;
            previousInstance = instance;
          }
        else
          for (i$jscomp$0 = 0; i$jscomp$0 < _dispatchQueue$i.length; i$jscomp$0++) {
            _dispatchListeners$i = _dispatchQueue$i[i$jscomp$0];
            instance = _dispatchListeners$i.instance;
            currentTarget = _dispatchListeners$i.currentTarget;
            _dispatchListeners$i = _dispatchListeners$i.listener;
            if (instance !== previousInstance && event.isPropagationStopped())
              break a;
            previousInstance = _dispatchListeners$i;
            event.currentTarget = currentTarget;
            try {
              previousInstance(event);
            } catch (error) {
              reportGlobalError(error);
            }
            event.currentTarget = null;
            previousInstance = instance;
          }
      }
    }
  }
  function listenToNonDelegatedEvent(domEventName, targetElement) {
    var JSCompiler_inline_result = targetElement[internalEventHandlersKey];
    void 0 === JSCompiler_inline_result && (JSCompiler_inline_result = targetElement[internalEventHandlersKey] = /* @__PURE__ */ new Set());
    var listenerSetKey = domEventName + "__bubble";
    JSCompiler_inline_result.has(listenerSetKey) || (addTrappedEventListener(targetElement, domEventName, 2, false), JSCompiler_inline_result.add(listenerSetKey));
  }
  function listenToNativeEvent(domEventName, isCapturePhaseListener, target) {
    var eventSystemFlags = 0;
    isCapturePhaseListener && (eventSystemFlags |= 4);
    addTrappedEventListener(
      target,
      domEventName,
      eventSystemFlags,
      isCapturePhaseListener
    );
  }
  var listeningMarker = "_reactListening" + Math.random().toString(36).slice(2);
  function listenToAllSupportedEvents(rootContainerElement) {
    if (!rootContainerElement[listeningMarker]) {
      rootContainerElement[listeningMarker] = true;
      allNativeEvents.forEach(function(domEventName) {
        "selectionchange" !== domEventName && (nonDelegatedEvents.has(domEventName) || listenToNativeEvent(domEventName, false, rootContainerElement), listenToNativeEvent(domEventName, true, rootContainerElement));
      });
      var ownerDocument = 9 === rootContainerElement.nodeType ? rootContainerElement : rootContainerElement.ownerDocument;
      null === ownerDocument || ownerDocument[listeningMarker] || (ownerDocument[listeningMarker] = true, listenToNativeEvent("selectionchange", false, ownerDocument));
    }
  }
  function addTrappedEventListener(targetContainer, domEventName, eventSystemFlags, isCapturePhaseListener) {
    switch (getEventPriority(domEventName)) {
      case 2:
        var listenerWrapper = dispatchDiscreteEvent;
        break;
      case 8:
        listenerWrapper = dispatchContinuousEvent;
        break;
      default:
        listenerWrapper = dispatchEvent;
    }
    eventSystemFlags = listenerWrapper.bind(
      null,
      domEventName,
      eventSystemFlags,
      targetContainer
    );
    listenerWrapper = void 0;
    !passiveBrowserEventsSupported || "touchstart" !== domEventName && "touchmove" !== domEventName && "wheel" !== domEventName || (listenerWrapper = true);
    isCapturePhaseListener ? void 0 !== listenerWrapper ? targetContainer.addEventListener(domEventName, eventSystemFlags, {
      capture: true,
      passive: listenerWrapper
    }) : targetContainer.addEventListener(domEventName, eventSystemFlags, true) : void 0 !== listenerWrapper ? targetContainer.addEventListener(domEventName, eventSystemFlags, {
      passive: listenerWrapper
    }) : targetContainer.addEventListener(domEventName, eventSystemFlags, false);
  }
  function dispatchEventForPluginEventSystem(domEventName, eventSystemFlags, nativeEvent, targetInst$jscomp$0, targetContainer) {
    var ancestorInst = targetInst$jscomp$0;
    if (0 === (eventSystemFlags & 1) && 0 === (eventSystemFlags & 2) && null !== targetInst$jscomp$0)
      a: for (; ; ) {
        if (null === targetInst$jscomp$0) return;
        var nodeTag = targetInst$jscomp$0.tag;
        if (3 === nodeTag || 4 === nodeTag) {
          var container = targetInst$jscomp$0.stateNode.containerInfo;
          if (container === targetContainer) break;
          if (4 === nodeTag)
            for (nodeTag = targetInst$jscomp$0.return; null !== nodeTag; ) {
              var grandTag = nodeTag.tag;
              if ((3 === grandTag || 4 === grandTag) && nodeTag.stateNode.containerInfo === targetContainer)
                return;
              nodeTag = nodeTag.return;
            }
          for (; null !== container; ) {
            nodeTag = getClosestInstanceFromNode(container);
            if (null === nodeTag) return;
            grandTag = nodeTag.tag;
            if (5 === grandTag || 6 === grandTag || 26 === grandTag || 27 === grandTag) {
              targetInst$jscomp$0 = ancestorInst = nodeTag;
              continue a;
            }
            container = container.parentNode;
          }
        }
        targetInst$jscomp$0 = targetInst$jscomp$0.return;
      }
    batchedUpdates$1(function() {
      var targetInst = ancestorInst, nativeEventTarget = getEventTarget(nativeEvent), dispatchQueue = [];
      a: {
        var reactName = topLevelEventsToReactNames.get(domEventName);
        if (void 0 !== reactName) {
          var SyntheticEventCtor = SyntheticEvent, reactEventType = domEventName;
          switch (domEventName) {
            case "keypress":
              if (0 === getEventCharCode(nativeEvent)) break a;
            case "keydown":
            case "keyup":
              SyntheticEventCtor = SyntheticKeyboardEvent;
              break;
            case "focusin":
              reactEventType = "focus";
              SyntheticEventCtor = SyntheticFocusEvent;
              break;
            case "focusout":
              reactEventType = "blur";
              SyntheticEventCtor = SyntheticFocusEvent;
              break;
            case "beforeblur":
            case "afterblur":
              SyntheticEventCtor = SyntheticFocusEvent;
              break;
            case "click":
              if (2 === nativeEvent.button) break a;
            case "auxclick":
            case "dblclick":
            case "mousedown":
            case "mousemove":
            case "mouseup":
            case "mouseout":
            case "mouseover":
            case "contextmenu":
              SyntheticEventCtor = SyntheticMouseEvent;
              break;
            case "drag":
            case "dragend":
            case "dragenter":
            case "dragexit":
            case "dragleave":
            case "dragover":
            case "dragstart":
            case "drop":
              SyntheticEventCtor = SyntheticDragEvent;
              break;
            case "touchcancel":
            case "touchend":
            case "touchmove":
            case "touchstart":
              SyntheticEventCtor = SyntheticTouchEvent;
              break;
            case ANIMATION_END:
            case ANIMATION_ITERATION:
            case ANIMATION_START:
              SyntheticEventCtor = SyntheticAnimationEvent;
              break;
            case TRANSITION_END:
              SyntheticEventCtor = SyntheticTransitionEvent;
              break;
            case "scroll":
            case "scrollend":
              SyntheticEventCtor = SyntheticUIEvent;
              break;
            case "wheel":
              SyntheticEventCtor = SyntheticWheelEvent;
              break;
            case "copy":
            case "cut":
            case "paste":
              SyntheticEventCtor = SyntheticClipboardEvent;
              break;
            case "gotpointercapture":
            case "lostpointercapture":
            case "pointercancel":
            case "pointerdown":
            case "pointermove":
            case "pointerout":
            case "pointerover":
            case "pointerup":
              SyntheticEventCtor = SyntheticPointerEvent;
              break;
            case "toggle":
            case "beforetoggle":
              SyntheticEventCtor = SyntheticToggleEvent;
          }
          var inCapturePhase = 0 !== (eventSystemFlags & 4), accumulateTargetOnly = !inCapturePhase && ("scroll" === domEventName || "scrollend" === domEventName), reactEventName = inCapturePhase ? null !== reactName ? reactName + "Capture" : null : reactName;
          inCapturePhase = [];
          for (var instance = targetInst, lastHostComponent; null !== instance; ) {
            var _instance = instance;
            lastHostComponent = _instance.stateNode;
            _instance = _instance.tag;
            5 !== _instance && 26 !== _instance && 27 !== _instance || null === lastHostComponent || null === reactEventName || (_instance = getListener(instance, reactEventName), null != _instance && inCapturePhase.push(
              createDispatchListener(instance, _instance, lastHostComponent)
            ));
            if (accumulateTargetOnly) break;
            instance = instance.return;
          }
          0 < inCapturePhase.length && (reactName = new SyntheticEventCtor(
            reactName,
            reactEventType,
            null,
            nativeEvent,
            nativeEventTarget
          ), dispatchQueue.push({ event: reactName, listeners: inCapturePhase }));
        }
      }
      if (0 === (eventSystemFlags & 7)) {
        a: {
          reactName = "mouseover" === domEventName || "pointerover" === domEventName;
          SyntheticEventCtor = "mouseout" === domEventName || "pointerout" === domEventName;
          if (reactName && nativeEvent !== currentReplayingEvent && (reactEventType = nativeEvent.relatedTarget || nativeEvent.fromElement) && (getClosestInstanceFromNode(reactEventType) || reactEventType[internalContainerInstanceKey]))
            break a;
          if (SyntheticEventCtor || reactName) {
            reactName = nativeEventTarget.window === nativeEventTarget ? nativeEventTarget : (reactName = nativeEventTarget.ownerDocument) ? reactName.defaultView || reactName.parentWindow : window;
            if (SyntheticEventCtor) {
              if (reactEventType = nativeEvent.relatedTarget || nativeEvent.toElement, SyntheticEventCtor = targetInst, reactEventType = reactEventType ? getClosestInstanceFromNode(reactEventType) : null, null !== reactEventType && (accumulateTargetOnly = getNearestMountedFiber(reactEventType), inCapturePhase = reactEventType.tag, reactEventType !== accumulateTargetOnly || 5 !== inCapturePhase && 27 !== inCapturePhase && 6 !== inCapturePhase))
                reactEventType = null;
            } else SyntheticEventCtor = null, reactEventType = targetInst;
            if (SyntheticEventCtor !== reactEventType) {
              inCapturePhase = SyntheticMouseEvent;
              _instance = "onMouseLeave";
              reactEventName = "onMouseEnter";
              instance = "mouse";
              if ("pointerout" === domEventName || "pointerover" === domEventName)
                inCapturePhase = SyntheticPointerEvent, _instance = "onPointerLeave", reactEventName = "onPointerEnter", instance = "pointer";
              accumulateTargetOnly = null == SyntheticEventCtor ? reactName : getNodeFromInstance(SyntheticEventCtor);
              lastHostComponent = null == reactEventType ? reactName : getNodeFromInstance(reactEventType);
              reactName = new inCapturePhase(
                _instance,
                instance + "leave",
                SyntheticEventCtor,
                nativeEvent,
                nativeEventTarget
              );
              reactName.target = accumulateTargetOnly;
              reactName.relatedTarget = lastHostComponent;
              _instance = null;
              getClosestInstanceFromNode(nativeEventTarget) === targetInst && (inCapturePhase = new inCapturePhase(
                reactEventName,
                instance + "enter",
                reactEventType,
                nativeEvent,
                nativeEventTarget
              ), inCapturePhase.target = lastHostComponent, inCapturePhase.relatedTarget = accumulateTargetOnly, _instance = inCapturePhase);
              accumulateTargetOnly = _instance;
              if (SyntheticEventCtor && reactEventType)
                b: {
                  inCapturePhase = getParent;
                  reactEventName = SyntheticEventCtor;
                  instance = reactEventType;
                  lastHostComponent = 0;
                  for (_instance = reactEventName; _instance; _instance = inCapturePhase(_instance))
                    lastHostComponent++;
                  _instance = 0;
                  for (var tempB = instance; tempB; tempB = inCapturePhase(tempB))
                    _instance++;
                  for (; 0 < lastHostComponent - _instance; )
                    reactEventName = inCapturePhase(reactEventName), lastHostComponent--;
                  for (; 0 < _instance - lastHostComponent; )
                    instance = inCapturePhase(instance), _instance--;
                  for (; lastHostComponent--; ) {
                    if (reactEventName === instance || null !== instance && reactEventName === instance.alternate) {
                      inCapturePhase = reactEventName;
                      break b;
                    }
                    reactEventName = inCapturePhase(reactEventName);
                    instance = inCapturePhase(instance);
                  }
                  inCapturePhase = null;
                }
              else inCapturePhase = null;
              null !== SyntheticEventCtor && accumulateEnterLeaveListenersForEvent(
                dispatchQueue,
                reactName,
                SyntheticEventCtor,
                inCapturePhase,
                false
              );
              null !== reactEventType && null !== accumulateTargetOnly && accumulateEnterLeaveListenersForEvent(
                dispatchQueue,
                accumulateTargetOnly,
                reactEventType,
                inCapturePhase,
                true
              );
            }
          }
        }
        a: {
          reactName = targetInst ? getNodeFromInstance(targetInst) : window;
          SyntheticEventCtor = reactName.nodeName && reactName.nodeName.toLowerCase();
          if ("select" === SyntheticEventCtor || "input" === SyntheticEventCtor && "file" === reactName.type)
            var getTargetInstFunc = getTargetInstForChangeEvent;
          else if (isTextInputElement(reactName))
            if (isInputEventSupported)
              getTargetInstFunc = getTargetInstForInputOrChangeEvent;
            else {
              getTargetInstFunc = getTargetInstForInputEventPolyfill;
              var handleEventFunc = handleEventsForInputEventPolyfill;
            }
          else
            SyntheticEventCtor = reactName.nodeName, !SyntheticEventCtor || "input" !== SyntheticEventCtor.toLowerCase() || "checkbox" !== reactName.type && "radio" !== reactName.type ? targetInst && isCustomElement(targetInst.elementType) && (getTargetInstFunc = getTargetInstForChangeEvent) : getTargetInstFunc = getTargetInstForClickEvent;
          if (getTargetInstFunc && (getTargetInstFunc = getTargetInstFunc(domEventName, targetInst))) {
            createAndAccumulateChangeEvent(
              dispatchQueue,
              getTargetInstFunc,
              nativeEvent,
              nativeEventTarget
            );
            break a;
          }
          handleEventFunc && handleEventFunc(domEventName, reactName, targetInst);
          "focusout" === domEventName && targetInst && "number" === reactName.type && null != targetInst.memoizedProps.value && setDefaultValue(reactName, "number", reactName.value);
        }
        handleEventFunc = targetInst ? getNodeFromInstance(targetInst) : window;
        switch (domEventName) {
          case "focusin":
            if (isTextInputElement(handleEventFunc) || "true" === handleEventFunc.contentEditable)
              activeElement = handleEventFunc, activeElementInst = targetInst, lastSelection = null;
            break;
          case "focusout":
            lastSelection = activeElementInst = activeElement = null;
            break;
          case "mousedown":
            mouseDown = true;
            break;
          case "contextmenu":
          case "mouseup":
          case "dragend":
            mouseDown = false;
            constructSelectEvent(dispatchQueue, nativeEvent, nativeEventTarget);
            break;
          case "selectionchange":
            if (skipSelectionChangeEvent) break;
          case "keydown":
          case "keyup":
            constructSelectEvent(dispatchQueue, nativeEvent, nativeEventTarget);
        }
        var fallbackData;
        if (canUseCompositionEvent)
          b: {
            switch (domEventName) {
              case "compositionstart":
                var eventType = "onCompositionStart";
                break b;
              case "compositionend":
                eventType = "onCompositionEnd";
                break b;
              case "compositionupdate":
                eventType = "onCompositionUpdate";
                break b;
            }
            eventType = void 0;
          }
        else
          isComposing ? isFallbackCompositionEnd(domEventName, nativeEvent) && (eventType = "onCompositionEnd") : "keydown" === domEventName && 229 === nativeEvent.keyCode && (eventType = "onCompositionStart");
        eventType && (useFallbackCompositionData && "ko" !== nativeEvent.locale && (isComposing || "onCompositionStart" !== eventType ? "onCompositionEnd" === eventType && isComposing && (fallbackData = getData()) : (root = nativeEventTarget, startText = "value" in root ? root.value : root.textContent, isComposing = true)), handleEventFunc = accumulateTwoPhaseListeners(targetInst, eventType), 0 < handleEventFunc.length && (eventType = new SyntheticCompositionEvent(
          eventType,
          domEventName,
          null,
          nativeEvent,
          nativeEventTarget
        ), dispatchQueue.push({ event: eventType, listeners: handleEventFunc }), fallbackData ? eventType.data = fallbackData : (fallbackData = getDataFromCustomEvent(nativeEvent), null !== fallbackData && (eventType.data = fallbackData))));
        if (fallbackData = canUseTextInputEvent ? getNativeBeforeInputChars(domEventName, nativeEvent) : getFallbackBeforeInputChars(domEventName, nativeEvent))
          eventType = accumulateTwoPhaseListeners(targetInst, "onBeforeInput"), 0 < eventType.length && (handleEventFunc = new SyntheticCompositionEvent(
            "onBeforeInput",
            "beforeinput",
            null,
            nativeEvent,
            nativeEventTarget
          ), dispatchQueue.push({
            event: handleEventFunc,
            listeners: eventType
          }), handleEventFunc.data = fallbackData);
        extractEvents$1(
          dispatchQueue,
          domEventName,
          targetInst,
          nativeEvent,
          nativeEventTarget
        );
      }
      processDispatchQueue(dispatchQueue, eventSystemFlags);
    });
  }
  function createDispatchListener(instance, listener, currentTarget) {
    return {
      instance,
      listener,
      currentTarget
    };
  }
  function accumulateTwoPhaseListeners(targetFiber, reactName) {
    for (var captureName = reactName + "Capture", listeners = []; null !== targetFiber; ) {
      var _instance2 = targetFiber, stateNode = _instance2.stateNode;
      _instance2 = _instance2.tag;
      5 !== _instance2 && 26 !== _instance2 && 27 !== _instance2 || null === stateNode || (_instance2 = getListener(targetFiber, captureName), null != _instance2 && listeners.unshift(
        createDispatchListener(targetFiber, _instance2, stateNode)
      ), _instance2 = getListener(targetFiber, reactName), null != _instance2 && listeners.push(
        createDispatchListener(targetFiber, _instance2, stateNode)
      ));
      if (3 === targetFiber.tag) return listeners;
      targetFiber = targetFiber.return;
    }
    return [];
  }
  function getParent(inst) {
    if (null === inst) return null;
    do
      inst = inst.return;
    while (inst && 5 !== inst.tag && 27 !== inst.tag);
    return inst ? inst : null;
  }
  function accumulateEnterLeaveListenersForEvent(dispatchQueue, event, target, common, inCapturePhase) {
    for (var registrationName = event._reactName, listeners = []; null !== target && target !== common; ) {
      var _instance3 = target, alternate = _instance3.alternate, stateNode = _instance3.stateNode;
      _instance3 = _instance3.tag;
      if (null !== alternate && alternate === common) break;
      5 !== _instance3 && 26 !== _instance3 && 27 !== _instance3 || null === stateNode || (alternate = stateNode, inCapturePhase ? (stateNode = getListener(target, registrationName), null != stateNode && listeners.unshift(
        createDispatchListener(target, stateNode, alternate)
      )) : inCapturePhase || (stateNode = getListener(target, registrationName), null != stateNode && listeners.push(
        createDispatchListener(target, stateNode, alternate)
      )));
      target = target.return;
    }
    0 !== listeners.length && dispatchQueue.push({ event, listeners });
  }
  var NORMALIZE_NEWLINES_REGEX = /\r\n?/g, NORMALIZE_NULL_AND_REPLACEMENT_REGEX = /\u0000|\uFFFD/g;
  function normalizeMarkupForTextOrAttribute(markup) {
    return ("string" === typeof markup ? markup : "" + markup).replace(NORMALIZE_NEWLINES_REGEX, "\n").replace(NORMALIZE_NULL_AND_REPLACEMENT_REGEX, "");
  }
  function checkForUnmatchedText(serverText, clientText) {
    clientText = normalizeMarkupForTextOrAttribute(clientText);
    return normalizeMarkupForTextOrAttribute(serverText) === clientText ? true : false;
  }
  function setProp(domElement, tag, key, value, props, prevValue) {
    switch (key) {
      case "children":
        "string" === typeof value ? "body" === tag || "textarea" === tag && "" === value || setTextContent(domElement, value) : ("number" === typeof value || "bigint" === typeof value) && "body" !== tag && setTextContent(domElement, "" + value);
        break;
      case "className":
        setValueForKnownAttribute(domElement, "class", value);
        break;
      case "tabIndex":
        setValueForKnownAttribute(domElement, "tabindex", value);
        break;
      case "dir":
      case "role":
      case "viewBox":
      case "width":
      case "height":
        setValueForKnownAttribute(domElement, key, value);
        break;
      case "style":
        setValueForStyles(domElement, value, prevValue);
        break;
      case "data":
        if ("object" !== tag) {
          setValueForKnownAttribute(domElement, "data", value);
          break;
        }
      case "src":
      case "href":
        if ("" === value && ("a" !== tag || "href" !== key)) {
          domElement.removeAttribute(key);
          break;
        }
        if (null == value || "function" === typeof value || "symbol" === typeof value || "boolean" === typeof value) {
          domElement.removeAttribute(key);
          break;
        }
        value = sanitizeURL("" + value);
        domElement.setAttribute(key, value);
        break;
      case "action":
      case "formAction":
        if ("function" === typeof value) {
          domElement.setAttribute(
            key,
            "javascript:throw new Error('A React form was unexpectedly submitted. If you called form.submit() manually, consider using form.requestSubmit() instead. If you\\'re trying to use event.stopPropagation() in a submit event handler, consider also calling event.preventDefault().')"
          );
          break;
        } else
          "function" === typeof prevValue && ("formAction" === key ? ("input" !== tag && setProp(domElement, tag, "name", props.name, props, null), setProp(
            domElement,
            tag,
            "formEncType",
            props.formEncType,
            props,
            null
          ), setProp(
            domElement,
            tag,
            "formMethod",
            props.formMethod,
            props,
            null
          ), setProp(
            domElement,
            tag,
            "formTarget",
            props.formTarget,
            props,
            null
          )) : (setProp(domElement, tag, "encType", props.encType, props, null), setProp(domElement, tag, "method", props.method, props, null), setProp(domElement, tag, "target", props.target, props, null)));
        if (null == value || "symbol" === typeof value || "boolean" === typeof value) {
          domElement.removeAttribute(key);
          break;
        }
        value = sanitizeURL("" + value);
        domElement.setAttribute(key, value);
        break;
      case "onClick":
        null != value && (domElement.onclick = noop$1);
        break;
      case "onScroll":
        null != value && listenToNonDelegatedEvent("scroll", domElement);
        break;
      case "onScrollEnd":
        null != value && listenToNonDelegatedEvent("scrollend", domElement);
        break;
      case "dangerouslySetInnerHTML":
        if (null != value) {
          if ("object" !== typeof value || !("__html" in value))
            throw Error(formatProdErrorMessage(61));
          key = value.__html;
          if (null != key) {
            if (null != props.children) throw Error(formatProdErrorMessage(60));
            domElement.innerHTML = key;
          }
        }
        break;
      case "multiple":
        domElement.multiple = value && "function" !== typeof value && "symbol" !== typeof value;
        break;
      case "muted":
        domElement.muted = value && "function" !== typeof value && "symbol" !== typeof value;
        break;
      case "suppressContentEditableWarning":
      case "suppressHydrationWarning":
      case "defaultValue":
      case "defaultChecked":
      case "innerHTML":
      case "ref":
        break;
      case "autoFocus":
        break;
      case "xlinkHref":
        if (null == value || "function" === typeof value || "boolean" === typeof value || "symbol" === typeof value) {
          domElement.removeAttribute("xlink:href");
          break;
        }
        key = sanitizeURL("" + value);
        domElement.setAttributeNS(
          "http://www.w3.org/1999/xlink",
          "xlink:href",
          key
        );
        break;
      case "contentEditable":
      case "spellCheck":
      case "draggable":
      case "value":
      case "autoReverse":
      case "externalResourcesRequired":
      case "focusable":
      case "preserveAlpha":
        null != value && "function" !== typeof value && "symbol" !== typeof value ? domElement.setAttribute(key, "" + value) : domElement.removeAttribute(key);
        break;
      case "inert":
      case "allowFullScreen":
      case "async":
      case "autoPlay":
      case "controls":
      case "default":
      case "defer":
      case "disabled":
      case "disablePictureInPicture":
      case "disableRemotePlayback":
      case "formNoValidate":
      case "hidden":
      case "loop":
      case "noModule":
      case "noValidate":
      case "open":
      case "playsInline":
      case "readOnly":
      case "required":
      case "reversed":
      case "scoped":
      case "seamless":
      case "itemScope":
        value && "function" !== typeof value && "symbol" !== typeof value ? domElement.setAttribute(key, "") : domElement.removeAttribute(key);
        break;
      case "capture":
      case "download":
        true === value ? domElement.setAttribute(key, "") : false !== value && null != value && "function" !== typeof value && "symbol" !== typeof value ? domElement.setAttribute(key, value) : domElement.removeAttribute(key);
        break;
      case "cols":
      case "rows":
      case "size":
      case "span":
        null != value && "function" !== typeof value && "symbol" !== typeof value && !isNaN(value) && 1 <= value ? domElement.setAttribute(key, value) : domElement.removeAttribute(key);
        break;
      case "rowSpan":
      case "start":
        null == value || "function" === typeof value || "symbol" === typeof value || isNaN(value) ? domElement.removeAttribute(key) : domElement.setAttribute(key, value);
        break;
      case "popover":
        listenToNonDelegatedEvent("beforetoggle", domElement);
        listenToNonDelegatedEvent("toggle", domElement);
        setValueForAttribute(domElement, "popover", value);
        break;
      case "xlinkActuate":
        setValueForNamespacedAttribute(
          domElement,
          "http://www.w3.org/1999/xlink",
          "xlink:actuate",
          value
        );
        break;
      case "xlinkArcrole":
        setValueForNamespacedAttribute(
          domElement,
          "http://www.w3.org/1999/xlink",
          "xlink:arcrole",
          value
        );
        break;
      case "xlinkRole":
        setValueForNamespacedAttribute(
          domElement,
          "http://www.w3.org/1999/xlink",
          "xlink:role",
          value
        );
        break;
      case "xlinkShow":
        setValueForNamespacedAttribute(
          domElement,
          "http://www.w3.org/1999/xlink",
          "xlink:show",
          value
        );
        break;
      case "xlinkTitle":
        setValueForNamespacedAttribute(
          domElement,
          "http://www.w3.org/1999/xlink",
          "xlink:title",
          value
        );
        break;
      case "xlinkType":
        setValueForNamespacedAttribute(
          domElement,
          "http://www.w3.org/1999/xlink",
          "xlink:type",
          value
        );
        break;
      case "xmlBase":
        setValueForNamespacedAttribute(
          domElement,
          "http://www.w3.org/XML/1998/namespace",
          "xml:base",
          value
        );
        break;
      case "xmlLang":
        setValueForNamespacedAttribute(
          domElement,
          "http://www.w3.org/XML/1998/namespace",
          "xml:lang",
          value
        );
        break;
      case "xmlSpace":
        setValueForNamespacedAttribute(
          domElement,
          "http://www.w3.org/XML/1998/namespace",
          "xml:space",
          value
        );
        break;
      case "is":
        setValueForAttribute(domElement, "is", value);
        break;
      case "innerText":
      case "textContent":
        break;
      default:
        if (!(2 < key.length) || "o" !== key[0] && "O" !== key[0] || "n" !== key[1] && "N" !== key[1])
          key = aliases.get(key) || key, setValueForAttribute(domElement, key, value);
    }
  }
  function setPropOnCustomElement(domElement, tag, key, value, props, prevValue) {
    switch (key) {
      case "style":
        setValueForStyles(domElement, value, prevValue);
        break;
      case "dangerouslySetInnerHTML":
        if (null != value) {
          if ("object" !== typeof value || !("__html" in value))
            throw Error(formatProdErrorMessage(61));
          key = value.__html;
          if (null != key) {
            if (null != props.children) throw Error(formatProdErrorMessage(60));
            domElement.innerHTML = key;
          }
        }
        break;
      case "children":
        "string" === typeof value ? setTextContent(domElement, value) : ("number" === typeof value || "bigint" === typeof value) && setTextContent(domElement, "" + value);
        break;
      case "onScroll":
        null != value && listenToNonDelegatedEvent("scroll", domElement);
        break;
      case "onScrollEnd":
        null != value && listenToNonDelegatedEvent("scrollend", domElement);
        break;
      case "onClick":
        null != value && (domElement.onclick = noop$1);
        break;
      case "suppressContentEditableWarning":
      case "suppressHydrationWarning":
      case "innerHTML":
      case "ref":
        break;
      case "innerText":
      case "textContent":
        break;
      default:
        if (!registrationNameDependencies.hasOwnProperty(key))
          a: {
            if ("o" === key[0] && "n" === key[1] && (props = key.endsWith("Capture"), tag = key.slice(2, props ? key.length - 7 : void 0), prevValue = domElement[internalPropsKey] || null, prevValue = null != prevValue ? prevValue[key] : null, "function" === typeof prevValue && domElement.removeEventListener(tag, prevValue, props), "function" === typeof value)) {
              "function" !== typeof prevValue && null !== prevValue && (key in domElement ? domElement[key] = null : domElement.hasAttribute(key) && domElement.removeAttribute(key));
              domElement.addEventListener(tag, value, props);
              break a;
            }
            key in domElement ? domElement[key] = value : true === value ? domElement.setAttribute(key, "") : setValueForAttribute(domElement, key, value);
          }
    }
  }
  function setInitialProperties(domElement, tag, props) {
    switch (tag) {
      case "div":
      case "span":
      case "svg":
      case "path":
      case "a":
      case "g":
      case "p":
      case "li":
        break;
      case "img":
        listenToNonDelegatedEvent("error", domElement);
        listenToNonDelegatedEvent("load", domElement);
        var hasSrc = false, hasSrcSet = false, propKey;
        for (propKey in props)
          if (props.hasOwnProperty(propKey)) {
            var propValue = props[propKey];
            if (null != propValue)
              switch (propKey) {
                case "src":
                  hasSrc = true;
                  break;
                case "srcSet":
                  hasSrcSet = true;
                  break;
                case "children":
                case "dangerouslySetInnerHTML":
                  throw Error(formatProdErrorMessage(137, tag));
                default:
                  setProp(domElement, tag, propKey, propValue, props, null);
              }
          }
        hasSrcSet && setProp(domElement, tag, "srcSet", props.srcSet, props, null);
        hasSrc && setProp(domElement, tag, "src", props.src, props, null);
        return;
      case "input":
        listenToNonDelegatedEvent("invalid", domElement);
        var defaultValue = propKey = propValue = hasSrcSet = null, checked = null, defaultChecked = null;
        for (hasSrc in props)
          if (props.hasOwnProperty(hasSrc)) {
            var propValue$184 = props[hasSrc];
            if (null != propValue$184)
              switch (hasSrc) {
                case "name":
                  hasSrcSet = propValue$184;
                  break;
                case "type":
                  propValue = propValue$184;
                  break;
                case "checked":
                  checked = propValue$184;
                  break;
                case "defaultChecked":
                  defaultChecked = propValue$184;
                  break;
                case "value":
                  propKey = propValue$184;
                  break;
                case "defaultValue":
                  defaultValue = propValue$184;
                  break;
                case "children":
                case "dangerouslySetInnerHTML":
                  if (null != propValue$184)
                    throw Error(formatProdErrorMessage(137, tag));
                  break;
                default:
                  setProp(domElement, tag, hasSrc, propValue$184, props, null);
              }
          }
        initInput(
          domElement,
          propKey,
          defaultValue,
          checked,
          defaultChecked,
          propValue,
          hasSrcSet,
          false
        );
        return;
      case "select":
        listenToNonDelegatedEvent("invalid", domElement);
        hasSrc = propValue = propKey = null;
        for (hasSrcSet in props)
          if (props.hasOwnProperty(hasSrcSet) && (defaultValue = props[hasSrcSet], null != defaultValue))
            switch (hasSrcSet) {
              case "value":
                propKey = defaultValue;
                break;
              case "defaultValue":
                propValue = defaultValue;
                break;
              case "multiple":
                hasSrc = defaultValue;
              default:
                setProp(domElement, tag, hasSrcSet, defaultValue, props, null);
            }
        tag = propKey;
        props = propValue;
        domElement.multiple = !!hasSrc;
        null != tag ? updateOptions(domElement, !!hasSrc, tag, false) : null != props && updateOptions(domElement, !!hasSrc, props, true);
        return;
      case "textarea":
        listenToNonDelegatedEvent("invalid", domElement);
        propKey = hasSrcSet = hasSrc = null;
        for (propValue in props)
          if (props.hasOwnProperty(propValue) && (defaultValue = props[propValue], null != defaultValue))
            switch (propValue) {
              case "value":
                hasSrc = defaultValue;
                break;
              case "defaultValue":
                hasSrcSet = defaultValue;
                break;
              case "children":
                propKey = defaultValue;
                break;
              case "dangerouslySetInnerHTML":
                if (null != defaultValue) throw Error(formatProdErrorMessage(91));
                break;
              default:
                setProp(domElement, tag, propValue, defaultValue, props, null);
            }
        initTextarea(domElement, hasSrc, hasSrcSet, propKey);
        return;
      case "option":
        for (checked in props)
          if (props.hasOwnProperty(checked) && (hasSrc = props[checked], null != hasSrc))
            switch (checked) {
              case "selected":
                domElement.selected = hasSrc && "function" !== typeof hasSrc && "symbol" !== typeof hasSrc;
                break;
              default:
                setProp(domElement, tag, checked, hasSrc, props, null);
            }
        return;
      case "dialog":
        listenToNonDelegatedEvent("beforetoggle", domElement);
        listenToNonDelegatedEvent("toggle", domElement);
        listenToNonDelegatedEvent("cancel", domElement);
        listenToNonDelegatedEvent("close", domElement);
        break;
      case "iframe":
      case "object":
        listenToNonDelegatedEvent("load", domElement);
        break;
      case "video":
      case "audio":
        for (hasSrc = 0; hasSrc < mediaEventTypes.length; hasSrc++)
          listenToNonDelegatedEvent(mediaEventTypes[hasSrc], domElement);
        break;
      case "image":
        listenToNonDelegatedEvent("error", domElement);
        listenToNonDelegatedEvent("load", domElement);
        break;
      case "details":
        listenToNonDelegatedEvent("toggle", domElement);
        break;
      case "embed":
      case "source":
      case "link":
        listenToNonDelegatedEvent("error", domElement), listenToNonDelegatedEvent("load", domElement);
      case "area":
      case "base":
      case "br":
      case "col":
      case "hr":
      case "keygen":
      case "meta":
      case "param":
      case "track":
      case "wbr":
      case "menuitem":
        for (defaultChecked in props)
          if (props.hasOwnProperty(defaultChecked) && (hasSrc = props[defaultChecked], null != hasSrc))
            switch (defaultChecked) {
              case "children":
              case "dangerouslySetInnerHTML":
                throw Error(formatProdErrorMessage(137, tag));
              default:
                setProp(domElement, tag, defaultChecked, hasSrc, props, null);
            }
        return;
      default:
        if (isCustomElement(tag)) {
          for (propValue$184 in props)
            props.hasOwnProperty(propValue$184) && (hasSrc = props[propValue$184], void 0 !== hasSrc && setPropOnCustomElement(
              domElement,
              tag,
              propValue$184,
              hasSrc,
              props,
              void 0
            ));
          return;
        }
    }
    for (defaultValue in props)
      props.hasOwnProperty(defaultValue) && (hasSrc = props[defaultValue], null != hasSrc && setProp(domElement, tag, defaultValue, hasSrc, props, null));
  }
  function updateProperties(domElement, tag, lastProps, nextProps) {
    switch (tag) {
      case "div":
      case "span":
      case "svg":
      case "path":
      case "a":
      case "g":
      case "p":
      case "li":
        break;
      case "input":
        var name = null, type = null, value = null, defaultValue = null, lastDefaultValue = null, checked = null, defaultChecked = null;
        for (propKey in lastProps) {
          var lastProp = lastProps[propKey];
          if (lastProps.hasOwnProperty(propKey) && null != lastProp)
            switch (propKey) {
              case "checked":
                break;
              case "value":
                break;
              case "defaultValue":
                lastDefaultValue = lastProp;
              default:
                nextProps.hasOwnProperty(propKey) || setProp(domElement, tag, propKey, null, nextProps, lastProp);
            }
        }
        for (var propKey$201 in nextProps) {
          var propKey = nextProps[propKey$201];
          lastProp = lastProps[propKey$201];
          if (nextProps.hasOwnProperty(propKey$201) && (null != propKey || null != lastProp))
            switch (propKey$201) {
              case "type":
                type = propKey;
                break;
              case "name":
                name = propKey;
                break;
              case "checked":
                checked = propKey;
                break;
              case "defaultChecked":
                defaultChecked = propKey;
                break;
              case "value":
                value = propKey;
                break;
              case "defaultValue":
                defaultValue = propKey;
                break;
              case "children":
              case "dangerouslySetInnerHTML":
                if (null != propKey)
                  throw Error(formatProdErrorMessage(137, tag));
                break;
              default:
                propKey !== lastProp && setProp(
                  domElement,
                  tag,
                  propKey$201,
                  propKey,
                  nextProps,
                  lastProp
                );
            }
        }
        updateInput(
          domElement,
          value,
          defaultValue,
          lastDefaultValue,
          checked,
          defaultChecked,
          type,
          name
        );
        return;
      case "select":
        propKey = value = defaultValue = propKey$201 = null;
        for (type in lastProps)
          if (lastDefaultValue = lastProps[type], lastProps.hasOwnProperty(type) && null != lastDefaultValue)
            switch (type) {
              case "value":
                break;
              case "multiple":
                propKey = lastDefaultValue;
              default:
                nextProps.hasOwnProperty(type) || setProp(
                  domElement,
                  tag,
                  type,
                  null,
                  nextProps,
                  lastDefaultValue
                );
            }
        for (name in nextProps)
          if (type = nextProps[name], lastDefaultValue = lastProps[name], nextProps.hasOwnProperty(name) && (null != type || null != lastDefaultValue))
            switch (name) {
              case "value":
                propKey$201 = type;
                break;
              case "defaultValue":
                defaultValue = type;
                break;
              case "multiple":
                value = type;
              default:
                type !== lastDefaultValue && setProp(
                  domElement,
                  tag,
                  name,
                  type,
                  nextProps,
                  lastDefaultValue
                );
            }
        tag = defaultValue;
        lastProps = value;
        nextProps = propKey;
        null != propKey$201 ? updateOptions(domElement, !!lastProps, propKey$201, false) : !!nextProps !== !!lastProps && (null != tag ? updateOptions(domElement, !!lastProps, tag, true) : updateOptions(domElement, !!lastProps, lastProps ? [] : "", false));
        return;
      case "textarea":
        propKey = propKey$201 = null;
        for (defaultValue in lastProps)
          if (name = lastProps[defaultValue], lastProps.hasOwnProperty(defaultValue) && null != name && !nextProps.hasOwnProperty(defaultValue))
            switch (defaultValue) {
              case "value":
                break;
              case "children":
                break;
              default:
                setProp(domElement, tag, defaultValue, null, nextProps, name);
            }
        for (value in nextProps)
          if (name = nextProps[value], type = lastProps[value], nextProps.hasOwnProperty(value) && (null != name || null != type))
            switch (value) {
              case "value":
                propKey$201 = name;
                break;
              case "defaultValue":
                propKey = name;
                break;
              case "children":
                break;
              case "dangerouslySetInnerHTML":
                if (null != name) throw Error(formatProdErrorMessage(91));
                break;
              default:
                name !== type && setProp(domElement, tag, value, name, nextProps, type);
            }
        updateTextarea(domElement, propKey$201, propKey);
        return;
      case "option":
        for (var propKey$217 in lastProps)
          if (propKey$201 = lastProps[propKey$217], lastProps.hasOwnProperty(propKey$217) && null != propKey$201 && !nextProps.hasOwnProperty(propKey$217))
            switch (propKey$217) {
              case "selected":
                domElement.selected = false;
                break;
              default:
                setProp(
                  domElement,
                  tag,
                  propKey$217,
                  null,
                  nextProps,
                  propKey$201
                );
            }
        for (lastDefaultValue in nextProps)
          if (propKey$201 = nextProps[lastDefaultValue], propKey = lastProps[lastDefaultValue], nextProps.hasOwnProperty(lastDefaultValue) && propKey$201 !== propKey && (null != propKey$201 || null != propKey))
            switch (lastDefaultValue) {
              case "selected":
                domElement.selected = propKey$201 && "function" !== typeof propKey$201 && "symbol" !== typeof propKey$201;
                break;
              default:
                setProp(
                  domElement,
                  tag,
                  lastDefaultValue,
                  propKey$201,
                  nextProps,
                  propKey
                );
            }
        return;
      case "img":
      case "link":
      case "area":
      case "base":
      case "br":
      case "col":
      case "embed":
      case "hr":
      case "keygen":
      case "meta":
      case "param":
      case "source":
      case "track":
      case "wbr":
      case "menuitem":
        for (var propKey$222 in lastProps)
          propKey$201 = lastProps[propKey$222], lastProps.hasOwnProperty(propKey$222) && null != propKey$201 && !nextProps.hasOwnProperty(propKey$222) && setProp(domElement, tag, propKey$222, null, nextProps, propKey$201);
        for (checked in nextProps)
          if (propKey$201 = nextProps[checked], propKey = lastProps[checked], nextProps.hasOwnProperty(checked) && propKey$201 !== propKey && (null != propKey$201 || null != propKey))
            switch (checked) {
              case "children":
              case "dangerouslySetInnerHTML":
                if (null != propKey$201)
                  throw Error(formatProdErrorMessage(137, tag));
                break;
              default:
                setProp(
                  domElement,
                  tag,
                  checked,
                  propKey$201,
                  nextProps,
                  propKey
                );
            }
        return;
      default:
        if (isCustomElement(tag)) {
          for (var propKey$227 in lastProps)
            propKey$201 = lastProps[propKey$227], lastProps.hasOwnProperty(propKey$227) && void 0 !== propKey$201 && !nextProps.hasOwnProperty(propKey$227) && setPropOnCustomElement(
              domElement,
              tag,
              propKey$227,
              void 0,
              nextProps,
              propKey$201
            );
          for (defaultChecked in nextProps)
            propKey$201 = nextProps[defaultChecked], propKey = lastProps[defaultChecked], !nextProps.hasOwnProperty(defaultChecked) || propKey$201 === propKey || void 0 === propKey$201 && void 0 === propKey || setPropOnCustomElement(
              domElement,
              tag,
              defaultChecked,
              propKey$201,
              nextProps,
              propKey
            );
          return;
        }
    }
    for (var propKey$232 in lastProps)
      propKey$201 = lastProps[propKey$232], lastProps.hasOwnProperty(propKey$232) && null != propKey$201 && !nextProps.hasOwnProperty(propKey$232) && setProp(domElement, tag, propKey$232, null, nextProps, propKey$201);
    for (lastProp in nextProps)
      propKey$201 = nextProps[lastProp], propKey = lastProps[lastProp], !nextProps.hasOwnProperty(lastProp) || propKey$201 === propKey || null == propKey$201 && null == propKey || setProp(domElement, tag, lastProp, propKey$201, nextProps, propKey);
  }
  function isLikelyStaticResource(initiatorType) {
    switch (initiatorType) {
      case "css":
      case "script":
      case "font":
      case "img":
      case "image":
      case "input":
      case "link":
        return true;
      default:
        return false;
    }
  }
  function estimateBandwidth() {
    if ("function" === typeof performance.getEntriesByType) {
      for (var count = 0, bits = 0, resourceEntries = performance.getEntriesByType("resource"), i = 0; i < resourceEntries.length; i++) {
        var entry = resourceEntries[i], transferSize = entry.transferSize, initiatorType = entry.initiatorType, duration = entry.duration;
        if (transferSize && duration && isLikelyStaticResource(initiatorType)) {
          initiatorType = 0;
          duration = entry.responseEnd;
          for (i += 1; i < resourceEntries.length; i++) {
            var overlapEntry = resourceEntries[i], overlapStartTime = overlapEntry.startTime;
            if (overlapStartTime > duration) break;
            var overlapTransferSize = overlapEntry.transferSize, overlapInitiatorType = overlapEntry.initiatorType;
            overlapTransferSize && isLikelyStaticResource(overlapInitiatorType) && (overlapEntry = overlapEntry.responseEnd, initiatorType += overlapTransferSize * (overlapEntry < duration ? 1 : (duration - overlapStartTime) / (overlapEntry - overlapStartTime)));
          }
          --i;
          bits += 8 * (transferSize + initiatorType) / (entry.duration / 1e3);
          count++;
          if (10 < count) break;
        }
      }
      if (0 < count) return bits / count / 1e6;
    }
    return navigator.connection && (count = navigator.connection.downlink, "number" === typeof count) ? count : 5;
  }
  var eventsEnabled = null, selectionInformation = null;
  function getOwnerDocumentFromRootContainer(rootContainerElement) {
    return 9 === rootContainerElement.nodeType ? rootContainerElement : rootContainerElement.ownerDocument;
  }
  function getOwnHostContext(namespaceURI) {
    switch (namespaceURI) {
      case "http://www.w3.org/2000/svg":
        return 1;
      case "http://www.w3.org/1998/Math/MathML":
        return 2;
      default:
        return 0;
    }
  }
  function getChildHostContextProd(parentNamespace, type) {
    if (0 === parentNamespace)
      switch (type) {
        case "svg":
          return 1;
        case "math":
          return 2;
        default:
          return 0;
      }
    return 1 === parentNamespace && "foreignObject" === type ? 0 : parentNamespace;
  }
  function shouldSetTextContent(type, props) {
    return "textarea" === type || "noscript" === type || "string" === typeof props.children || "number" === typeof props.children || "bigint" === typeof props.children || "object" === typeof props.dangerouslySetInnerHTML && null !== props.dangerouslySetInnerHTML && null != props.dangerouslySetInnerHTML.__html;
  }
  var currentPopstateTransitionEvent = null;
  function shouldAttemptEagerTransition() {
    var event = window.event;
    if (event && "popstate" === event.type) {
      if (event === currentPopstateTransitionEvent) return false;
      currentPopstateTransitionEvent = event;
      return true;
    }
    currentPopstateTransitionEvent = null;
    return false;
  }
  var scheduleTimeout = "function" === typeof setTimeout ? setTimeout : void 0, cancelTimeout = "function" === typeof clearTimeout ? clearTimeout : void 0, localPromise = "function" === typeof Promise ? Promise : void 0, scheduleMicrotask = "function" === typeof queueMicrotask ? queueMicrotask : "undefined" !== typeof localPromise ? function(callback) {
    return localPromise.resolve(null).then(callback).catch(handleErrorInNextTick);
  } : scheduleTimeout;
  function handleErrorInNextTick(error) {
    setTimeout(function() {
      throw error;
    });
  }
  function isSingletonScope(type) {
    return "head" === type;
  }
  function clearHydrationBoundary(parentInstance, hydrationInstance) {
    var node = hydrationInstance, depth = 0;
    do {
      var nextNode = node.nextSibling;
      parentInstance.removeChild(node);
      if (nextNode && 8 === nextNode.nodeType)
        if (node = nextNode.data, "/$" === node || "/&" === node) {
          if (0 === depth) {
            parentInstance.removeChild(nextNode);
            retryIfBlockedOn(hydrationInstance);
            return;
          }
          depth--;
        } else if ("$" === node || "$?" === node || "$~" === node || "$!" === node || "&" === node)
          depth++;
        else if ("html" === node)
          releaseSingletonInstance(parentInstance.ownerDocument.documentElement);
        else if ("head" === node) {
          node = parentInstance.ownerDocument.head;
          releaseSingletonInstance(node);
          for (var node$jscomp$0 = node.firstChild; node$jscomp$0; ) {
            var nextNode$jscomp$0 = node$jscomp$0.nextSibling, nodeName = node$jscomp$0.nodeName;
            node$jscomp$0[internalHoistableMarker] || "SCRIPT" === nodeName || "STYLE" === nodeName || "LINK" === nodeName && "stylesheet" === node$jscomp$0.rel.toLowerCase() || node.removeChild(node$jscomp$0);
            node$jscomp$0 = nextNode$jscomp$0;
          }
        } else
          "body" === node && releaseSingletonInstance(parentInstance.ownerDocument.body);
      node = nextNode;
    } while (node);
    retryIfBlockedOn(hydrationInstance);
  }
  function hideOrUnhideDehydratedBoundary(suspenseInstance, isHidden) {
    var node = suspenseInstance;
    suspenseInstance = 0;
    do {
      var nextNode = node.nextSibling;
      1 === node.nodeType ? isHidden ? (node._stashedDisplay = node.style.display, node.style.display = "none") : (node.style.display = node._stashedDisplay || "", "" === node.getAttribute("style") && node.removeAttribute("style")) : 3 === node.nodeType && (isHidden ? (node._stashedText = node.nodeValue, node.nodeValue = "") : node.nodeValue = node._stashedText || "");
      if (nextNode && 8 === nextNode.nodeType)
        if (node = nextNode.data, "/$" === node)
          if (0 === suspenseInstance) break;
          else suspenseInstance--;
        else
          "$" !== node && "$?" !== node && "$~" !== node && "$!" !== node || suspenseInstance++;
      node = nextNode;
    } while (node);
  }
  function clearContainerSparingly(container) {
    var nextNode = container.firstChild;
    nextNode && 10 === nextNode.nodeType && (nextNode = nextNode.nextSibling);
    for (; nextNode; ) {
      var node = nextNode;
      nextNode = nextNode.nextSibling;
      switch (node.nodeName) {
        case "HTML":
        case "HEAD":
        case "BODY":
          clearContainerSparingly(node);
          detachDeletedInstance(node);
          continue;
        case "SCRIPT":
        case "STYLE":
          continue;
        case "LINK":
          if ("stylesheet" === node.rel.toLowerCase()) continue;
      }
      container.removeChild(node);
    }
  }
  function canHydrateInstance(instance, type, props, inRootOrSingleton) {
    for (; 1 === instance.nodeType; ) {
      var anyProps = props;
      if (instance.nodeName.toLowerCase() !== type.toLowerCase()) {
        if (!inRootOrSingleton && ("INPUT" !== instance.nodeName || "hidden" !== instance.type))
          break;
      } else if (!inRootOrSingleton)
        if ("input" === type && "hidden" === instance.type) {
          var name = null == anyProps.name ? null : "" + anyProps.name;
          if ("hidden" === anyProps.type && instance.getAttribute("name") === name)
            return instance;
        } else return instance;
      else if (!instance[internalHoistableMarker])
        switch (type) {
          case "meta":
            if (!instance.hasAttribute("itemprop")) break;
            return instance;
          case "link":
            name = instance.getAttribute("rel");
            if ("stylesheet" === name && instance.hasAttribute("data-precedence"))
              break;
            else if (name !== anyProps.rel || instance.getAttribute("href") !== (null == anyProps.href || "" === anyProps.href ? null : anyProps.href) || instance.getAttribute("crossorigin") !== (null == anyProps.crossOrigin ? null : anyProps.crossOrigin) || instance.getAttribute("title") !== (null == anyProps.title ? null : anyProps.title))
              break;
            return instance;
          case "style":
            if (instance.hasAttribute("data-precedence")) break;
            return instance;
          case "script":
            name = instance.getAttribute("src");
            if ((name !== (null == anyProps.src ? null : anyProps.src) || instance.getAttribute("type") !== (null == anyProps.type ? null : anyProps.type) || instance.getAttribute("crossorigin") !== (null == anyProps.crossOrigin ? null : anyProps.crossOrigin)) && name && instance.hasAttribute("async") && !instance.hasAttribute("itemprop"))
              break;
            return instance;
          default:
            return instance;
        }
      instance = getNextHydratable(instance.nextSibling);
      if (null === instance) break;
    }
    return null;
  }
  function canHydrateTextInstance(instance, text, inRootOrSingleton) {
    if ("" === text) return null;
    for (; 3 !== instance.nodeType; ) {
      if ((1 !== instance.nodeType || "INPUT" !== instance.nodeName || "hidden" !== instance.type) && !inRootOrSingleton)
        return null;
      instance = getNextHydratable(instance.nextSibling);
      if (null === instance) return null;
    }
    return instance;
  }
  function canHydrateHydrationBoundary(instance, inRootOrSingleton) {
    for (; 8 !== instance.nodeType; ) {
      if ((1 !== instance.nodeType || "INPUT" !== instance.nodeName || "hidden" !== instance.type) && !inRootOrSingleton)
        return null;
      instance = getNextHydratable(instance.nextSibling);
      if (null === instance) return null;
    }
    return instance;
  }
  function isSuspenseInstancePending(instance) {
    return "$?" === instance.data || "$~" === instance.data;
  }
  function isSuspenseInstanceFallback(instance) {
    return "$!" === instance.data || "$?" === instance.data && "loading" !== instance.ownerDocument.readyState;
  }
  function registerSuspenseInstanceRetry(instance, callback) {
    var ownerDocument = instance.ownerDocument;
    if ("$~" === instance.data) instance._reactRetry = callback;
    else if ("$?" !== instance.data || "loading" !== ownerDocument.readyState)
      callback();
    else {
      var listener = function() {
        callback();
        ownerDocument.removeEventListener("DOMContentLoaded", listener);
      };
      ownerDocument.addEventListener("DOMContentLoaded", listener);
      instance._reactRetry = listener;
    }
  }
  function getNextHydratable(node) {
    for (; null != node; node = node.nextSibling) {
      var nodeType = node.nodeType;
      if (1 === nodeType || 3 === nodeType) break;
      if (8 === nodeType) {
        nodeType = node.data;
        if ("$" === nodeType || "$!" === nodeType || "$?" === nodeType || "$~" === nodeType || "&" === nodeType || "F!" === nodeType || "F" === nodeType)
          break;
        if ("/$" === nodeType || "/&" === nodeType) return null;
      }
    }
    return node;
  }
  var previousHydratableOnEnteringScopedSingleton = null;
  function getNextHydratableInstanceAfterHydrationBoundary(hydrationInstance) {
    hydrationInstance = hydrationInstance.nextSibling;
    for (var depth = 0; hydrationInstance; ) {
      if (8 === hydrationInstance.nodeType) {
        var data = hydrationInstance.data;
        if ("/$" === data || "/&" === data) {
          if (0 === depth)
            return getNextHydratable(hydrationInstance.nextSibling);
          depth--;
        } else
          "$" !== data && "$!" !== data && "$?" !== data && "$~" !== data && "&" !== data || depth++;
      }
      hydrationInstance = hydrationInstance.nextSibling;
    }
    return null;
  }
  function getParentHydrationBoundary(targetInstance) {
    targetInstance = targetInstance.previousSibling;
    for (var depth = 0; targetInstance; ) {
      if (8 === targetInstance.nodeType) {
        var data = targetInstance.data;
        if ("$" === data || "$!" === data || "$?" === data || "$~" === data || "&" === data) {
          if (0 === depth) return targetInstance;
          depth--;
        } else "/$" !== data && "/&" !== data || depth++;
      }
      targetInstance = targetInstance.previousSibling;
    }
    return null;
  }
  function resolveSingletonInstance(type, props, rootContainerInstance) {
    props = getOwnerDocumentFromRootContainer(rootContainerInstance);
    switch (type) {
      case "html":
        type = props.documentElement;
        if (!type) throw Error(formatProdErrorMessage(452));
        return type;
      case "head":
        type = props.head;
        if (!type) throw Error(formatProdErrorMessage(453));
        return type;
      case "body":
        type = props.body;
        if (!type) throw Error(formatProdErrorMessage(454));
        return type;
      default:
        throw Error(formatProdErrorMessage(451));
    }
  }
  function releaseSingletonInstance(instance) {
    for (var attributes = instance.attributes; attributes.length; )
      instance.removeAttributeNode(attributes[0]);
    detachDeletedInstance(instance);
  }
  var preloadPropsMap = /* @__PURE__ */ new Map(), preconnectsSet = /* @__PURE__ */ new Set();
  function getHoistableRoot(container) {
    return "function" === typeof container.getRootNode ? container.getRootNode() : 9 === container.nodeType ? container : container.ownerDocument;
  }
  var previousDispatcher = ReactDOMSharedInternals.d;
  ReactDOMSharedInternals.d = {
    f: flushSyncWork,
    r: requestFormReset,
    D: prefetchDNS,
    C: preconnect,
    L: preload2,
    m: preloadModule,
    X: preinitScript,
    S: preinitStyle,
    M: preinitModuleScript
  };
  function flushSyncWork() {
    var previousWasRendering = previousDispatcher.f(), wasRendering = flushSyncWork$1();
    return previousWasRendering || wasRendering;
  }
  function requestFormReset(form) {
    var formInst = getInstanceFromNode(form);
    null !== formInst && 5 === formInst.tag && "form" === formInst.type ? requestFormReset$1(formInst) : previousDispatcher.r(form);
  }
  var globalDocument = "undefined" === typeof document ? null : document;
  function preconnectAs(rel, href, crossOrigin) {
    var ownerDocument = globalDocument;
    if (ownerDocument && "string" === typeof href && href) {
      var limitedEscapedHref = escapeSelectorAttributeValueInsideDoubleQuotes(href);
      limitedEscapedHref = 'link[rel="' + rel + '"][href="' + limitedEscapedHref + '"]';
      "string" === typeof crossOrigin && (limitedEscapedHref += '[crossorigin="' + crossOrigin + '"]');
      preconnectsSet.has(limitedEscapedHref) || (preconnectsSet.add(limitedEscapedHref), rel = { rel, crossOrigin, href }, null === ownerDocument.querySelector(limitedEscapedHref) && (href = ownerDocument.createElement("link"), setInitialProperties(href, "link", rel), markNodeAsHoistable(href), ownerDocument.head.appendChild(href)));
    }
  }
  function prefetchDNS(href) {
    previousDispatcher.D(href);
    preconnectAs("dns-prefetch", href, null);
  }
  function preconnect(href, crossOrigin) {
    previousDispatcher.C(href, crossOrigin);
    preconnectAs("preconnect", href, crossOrigin);
  }
  function preload2(href, as, options2) {
    previousDispatcher.L(href, as, options2);
    var ownerDocument = globalDocument;
    if (ownerDocument && href && as) {
      var preloadSelector = 'link[rel="preload"][as="' + escapeSelectorAttributeValueInsideDoubleQuotes(as) + '"]';
      "image" === as ? options2 && options2.imageSrcSet ? (preloadSelector += '[imagesrcset="' + escapeSelectorAttributeValueInsideDoubleQuotes(
        options2.imageSrcSet
      ) + '"]', "string" === typeof options2.imageSizes && (preloadSelector += '[imagesizes="' + escapeSelectorAttributeValueInsideDoubleQuotes(
        options2.imageSizes
      ) + '"]')) : preloadSelector += '[href="' + escapeSelectorAttributeValueInsideDoubleQuotes(href) + '"]' : preloadSelector += '[href="' + escapeSelectorAttributeValueInsideDoubleQuotes(href) + '"]';
      var key = preloadSelector;
      switch (as) {
        case "style":
          key = getStyleKey(href);
          break;
        case "script":
          key = getScriptKey(href);
      }
      preloadPropsMap.has(key) || (href = assign(
        {
          rel: "preload",
          href: "image" === as && options2 && options2.imageSrcSet ? void 0 : href,
          as
        },
        options2
      ), preloadPropsMap.set(key, href), null !== ownerDocument.querySelector(preloadSelector) || "style" === as && ownerDocument.querySelector(getStylesheetSelectorFromKey(key)) || "script" === as && ownerDocument.querySelector(getScriptSelectorFromKey(key)) || (as = ownerDocument.createElement("link"), setInitialProperties(as, "link", href), markNodeAsHoistable(as), ownerDocument.head.appendChild(as)));
    }
  }
  function preloadModule(href, options2) {
    previousDispatcher.m(href, options2);
    var ownerDocument = globalDocument;
    if (ownerDocument && href) {
      var as = options2 && "string" === typeof options2.as ? options2.as : "script", preloadSelector = 'link[rel="modulepreload"][as="' + escapeSelectorAttributeValueInsideDoubleQuotes(as) + '"][href="' + escapeSelectorAttributeValueInsideDoubleQuotes(href) + '"]', key = preloadSelector;
      switch (as) {
        case "audioworklet":
        case "paintworklet":
        case "serviceworker":
        case "sharedworker":
        case "worker":
        case "script":
          key = getScriptKey(href);
      }
      if (!preloadPropsMap.has(key) && (href = assign({ rel: "modulepreload", href }, options2), preloadPropsMap.set(key, href), null === ownerDocument.querySelector(preloadSelector))) {
        switch (as) {
          case "audioworklet":
          case "paintworklet":
          case "serviceworker":
          case "sharedworker":
          case "worker":
          case "script":
            if (ownerDocument.querySelector(getScriptSelectorFromKey(key)))
              return;
        }
        as = ownerDocument.createElement("link");
        setInitialProperties(as, "link", href);
        markNodeAsHoistable(as);
        ownerDocument.head.appendChild(as);
      }
    }
  }
  function preinitStyle(href, precedence, options2) {
    previousDispatcher.S(href, precedence, options2);
    var ownerDocument = globalDocument;
    if (ownerDocument && href) {
      var styles = getResourcesFromRoot(ownerDocument).hoistableStyles, key = getStyleKey(href);
      precedence = precedence || "default";
      var resource = styles.get(key);
      if (!resource) {
        var state = { loading: 0, preload: null };
        if (resource = ownerDocument.querySelector(
          getStylesheetSelectorFromKey(key)
        ))
          state.loading = 5;
        else {
          href = assign(
            { rel: "stylesheet", href, "data-precedence": precedence },
            options2
          );
          (options2 = preloadPropsMap.get(key)) && adoptPreloadPropsForStylesheet(href, options2);
          var link = resource = ownerDocument.createElement("link");
          markNodeAsHoistable(link);
          setInitialProperties(link, "link", href);
          link._p = new Promise(function(resolve, reject) {
            link.onload = resolve;
            link.onerror = reject;
          });
          link.addEventListener("load", function() {
            state.loading |= 1;
          });
          link.addEventListener("error", function() {
            state.loading |= 2;
          });
          state.loading |= 4;
          insertStylesheet(resource, precedence, ownerDocument);
        }
        resource = {
          type: "stylesheet",
          instance: resource,
          count: 1,
          state
        };
        styles.set(key, resource);
      }
    }
  }
  function preinitScript(src, options2) {
    previousDispatcher.X(src, options2);
    var ownerDocument = globalDocument;
    if (ownerDocument && src) {
      var scripts = getResourcesFromRoot(ownerDocument).hoistableScripts, key = getScriptKey(src), resource = scripts.get(key);
      resource || (resource = ownerDocument.querySelector(getScriptSelectorFromKey(key)), resource || (src = assign({ src, async: true }, options2), (options2 = preloadPropsMap.get(key)) && adoptPreloadPropsForScript(src, options2), resource = ownerDocument.createElement("script"), markNodeAsHoistable(resource), setInitialProperties(resource, "link", src), ownerDocument.head.appendChild(resource)), resource = {
        type: "script",
        instance: resource,
        count: 1,
        state: null
      }, scripts.set(key, resource));
    }
  }
  function preinitModuleScript(src, options2) {
    previousDispatcher.M(src, options2);
    var ownerDocument = globalDocument;
    if (ownerDocument && src) {
      var scripts = getResourcesFromRoot(ownerDocument).hoistableScripts, key = getScriptKey(src), resource = scripts.get(key);
      resource || (resource = ownerDocument.querySelector(getScriptSelectorFromKey(key)), resource || (src = assign({ src, async: true, type: "module" }, options2), (options2 = preloadPropsMap.get(key)) && adoptPreloadPropsForScript(src, options2), resource = ownerDocument.createElement("script"), markNodeAsHoistable(resource), setInitialProperties(resource, "link", src), ownerDocument.head.appendChild(resource)), resource = {
        type: "script",
        instance: resource,
        count: 1,
        state: null
      }, scripts.set(key, resource));
    }
  }
  function getResource(type, currentProps, pendingProps, currentResource) {
    var JSCompiler_inline_result = (JSCompiler_inline_result = rootInstanceStackCursor.current) ? getHoistableRoot(JSCompiler_inline_result) : null;
    if (!JSCompiler_inline_result) throw Error(formatProdErrorMessage(446));
    switch (type) {
      case "meta":
      case "title":
        return null;
      case "style":
        return "string" === typeof pendingProps.precedence && "string" === typeof pendingProps.href ? (currentProps = getStyleKey(pendingProps.href), pendingProps = getResourcesFromRoot(
          JSCompiler_inline_result
        ).hoistableStyles, currentResource = pendingProps.get(currentProps), currentResource || (currentResource = {
          type: "style",
          instance: null,
          count: 0,
          state: null
        }, pendingProps.set(currentProps, currentResource)), currentResource) : { type: "void", instance: null, count: 0, state: null };
      case "link":
        if ("stylesheet" === pendingProps.rel && "string" === typeof pendingProps.href && "string" === typeof pendingProps.precedence) {
          type = getStyleKey(pendingProps.href);
          var styles$243 = getResourcesFromRoot(
            JSCompiler_inline_result
          ).hoistableStyles, resource$244 = styles$243.get(type);
          resource$244 || (JSCompiler_inline_result = JSCompiler_inline_result.ownerDocument || JSCompiler_inline_result, resource$244 = {
            type: "stylesheet",
            instance: null,
            count: 0,
            state: { loading: 0, preload: null }
          }, styles$243.set(type, resource$244), (styles$243 = JSCompiler_inline_result.querySelector(
            getStylesheetSelectorFromKey(type)
          )) && !styles$243._p && (resource$244.instance = styles$243, resource$244.state.loading = 5), preloadPropsMap.has(type) || (pendingProps = {
            rel: "preload",
            as: "style",
            href: pendingProps.href,
            crossOrigin: pendingProps.crossOrigin,
            integrity: pendingProps.integrity,
            media: pendingProps.media,
            hrefLang: pendingProps.hrefLang,
            referrerPolicy: pendingProps.referrerPolicy
          }, preloadPropsMap.set(type, pendingProps), styles$243 || preloadStylesheet(
            JSCompiler_inline_result,
            type,
            pendingProps,
            resource$244.state
          )));
          if (currentProps && null === currentResource)
            throw Error(formatProdErrorMessage(528, ""));
          return resource$244;
        }
        if (currentProps && null !== currentResource)
          throw Error(formatProdErrorMessage(529, ""));
        return null;
      case "script":
        return currentProps = pendingProps.async, pendingProps = pendingProps.src, "string" === typeof pendingProps && currentProps && "function" !== typeof currentProps && "symbol" !== typeof currentProps ? (currentProps = getScriptKey(pendingProps), pendingProps = getResourcesFromRoot(
          JSCompiler_inline_result
        ).hoistableScripts, currentResource = pendingProps.get(currentProps), currentResource || (currentResource = {
          type: "script",
          instance: null,
          count: 0,
          state: null
        }, pendingProps.set(currentProps, currentResource)), currentResource) : { type: "void", instance: null, count: 0, state: null };
      default:
        throw Error(formatProdErrorMessage(444, type));
    }
  }
  function getStyleKey(href) {
    return 'href="' + escapeSelectorAttributeValueInsideDoubleQuotes(href) + '"';
  }
  function getStylesheetSelectorFromKey(key) {
    return 'link[rel="stylesheet"][' + key + "]";
  }
  function stylesheetPropsFromRawProps(rawProps) {
    return assign({}, rawProps, {
      "data-precedence": rawProps.precedence,
      precedence: null
    });
  }
  function preloadStylesheet(ownerDocument, key, preloadProps, state) {
    ownerDocument.querySelector('link[rel="preload"][as="style"][' + key + "]") ? state.loading = 1 : (key = ownerDocument.createElement("link"), state.preload = key, key.addEventListener("load", function() {
      return state.loading |= 1;
    }), key.addEventListener("error", function() {
      return state.loading |= 2;
    }), setInitialProperties(key, "link", preloadProps), markNodeAsHoistable(key), ownerDocument.head.appendChild(key));
  }
  function getScriptKey(src) {
    return '[src="' + escapeSelectorAttributeValueInsideDoubleQuotes(src) + '"]';
  }
  function getScriptSelectorFromKey(key) {
    return "script[async]" + key;
  }
  function acquireResource(hoistableRoot, resource, props) {
    resource.count++;
    if (null === resource.instance)
      switch (resource.type) {
        case "style":
          var instance = hoistableRoot.querySelector(
            'style[data-href~="' + escapeSelectorAttributeValueInsideDoubleQuotes(props.href) + '"]'
          );
          if (instance)
            return resource.instance = instance, markNodeAsHoistable(instance), instance;
          var styleProps = assign({}, props, {
            "data-href": props.href,
            "data-precedence": props.precedence,
            href: null,
            precedence: null
          });
          instance = (hoistableRoot.ownerDocument || hoistableRoot).createElement(
            "style"
          );
          markNodeAsHoistable(instance);
          setInitialProperties(instance, "style", styleProps);
          insertStylesheet(instance, props.precedence, hoistableRoot);
          return resource.instance = instance;
        case "stylesheet":
          styleProps = getStyleKey(props.href);
          var instance$249 = hoistableRoot.querySelector(
            getStylesheetSelectorFromKey(styleProps)
          );
          if (instance$249)
            return resource.state.loading |= 4, resource.instance = instance$249, markNodeAsHoistable(instance$249), instance$249;
          instance = stylesheetPropsFromRawProps(props);
          (styleProps = preloadPropsMap.get(styleProps)) && adoptPreloadPropsForStylesheet(instance, styleProps);
          instance$249 = (hoistableRoot.ownerDocument || hoistableRoot).createElement("link");
          markNodeAsHoistable(instance$249);
          var linkInstance = instance$249;
          linkInstance._p = new Promise(function(resolve, reject) {
            linkInstance.onload = resolve;
            linkInstance.onerror = reject;
          });
          setInitialProperties(instance$249, "link", instance);
          resource.state.loading |= 4;
          insertStylesheet(instance$249, props.precedence, hoistableRoot);
          return resource.instance = instance$249;
        case "script":
          instance$249 = getScriptKey(props.src);
          if (styleProps = hoistableRoot.querySelector(
            getScriptSelectorFromKey(instance$249)
          ))
            return resource.instance = styleProps, markNodeAsHoistable(styleProps), styleProps;
          instance = props;
          if (styleProps = preloadPropsMap.get(instance$249))
            instance = assign({}, props), adoptPreloadPropsForScript(instance, styleProps);
          hoistableRoot = hoistableRoot.ownerDocument || hoistableRoot;
          styleProps = hoistableRoot.createElement("script");
          markNodeAsHoistable(styleProps);
          setInitialProperties(styleProps, "link", instance);
          hoistableRoot.head.appendChild(styleProps);
          return resource.instance = styleProps;
        case "void":
          return null;
        default:
          throw Error(formatProdErrorMessage(443, resource.type));
      }
    else
      "stylesheet" === resource.type && 0 === (resource.state.loading & 4) && (instance = resource.instance, resource.state.loading |= 4, insertStylesheet(instance, props.precedence, hoistableRoot));
    return resource.instance;
  }
  function insertStylesheet(instance, precedence, root2) {
    for (var nodes = root2.querySelectorAll(
      'link[rel="stylesheet"][data-precedence],style[data-precedence]'
    ), last = nodes.length ? nodes[nodes.length - 1] : null, prior = last, i = 0; i < nodes.length; i++) {
      var node = nodes[i];
      if (node.dataset.precedence === precedence) prior = node;
      else if (prior !== last) break;
    }
    prior ? prior.parentNode.insertBefore(instance, prior.nextSibling) : (precedence = 9 === root2.nodeType ? root2.head : root2, precedence.insertBefore(instance, precedence.firstChild));
  }
  function adoptPreloadPropsForStylesheet(stylesheetProps, preloadProps) {
    null == stylesheetProps.crossOrigin && (stylesheetProps.crossOrigin = preloadProps.crossOrigin);
    null == stylesheetProps.referrerPolicy && (stylesheetProps.referrerPolicy = preloadProps.referrerPolicy);
    null == stylesheetProps.title && (stylesheetProps.title = preloadProps.title);
  }
  function adoptPreloadPropsForScript(scriptProps, preloadProps) {
    null == scriptProps.crossOrigin && (scriptProps.crossOrigin = preloadProps.crossOrigin);
    null == scriptProps.referrerPolicy && (scriptProps.referrerPolicy = preloadProps.referrerPolicy);
    null == scriptProps.integrity && (scriptProps.integrity = preloadProps.integrity);
  }
  var tagCaches = null;
  function getHydratableHoistableCache(type, keyAttribute, ownerDocument) {
    if (null === tagCaches) {
      var cache = /* @__PURE__ */ new Map();
      var caches = tagCaches = /* @__PURE__ */ new Map();
      caches.set(ownerDocument, cache);
    } else
      caches = tagCaches, cache = caches.get(ownerDocument), cache || (cache = /* @__PURE__ */ new Map(), caches.set(ownerDocument, cache));
    if (cache.has(type)) return cache;
    cache.set(type, null);
    ownerDocument = ownerDocument.getElementsByTagName(type);
    for (caches = 0; caches < ownerDocument.length; caches++) {
      var node = ownerDocument[caches];
      if (!(node[internalHoistableMarker] || node[internalInstanceKey] || "link" === type && "stylesheet" === node.getAttribute("rel")) && "http://www.w3.org/2000/svg" !== node.namespaceURI) {
        var nodeKey = node.getAttribute(keyAttribute) || "";
        nodeKey = type + nodeKey;
        var existing = cache.get(nodeKey);
        existing ? existing.push(node) : cache.set(nodeKey, [node]);
      }
    }
    return cache;
  }
  function mountHoistable(hoistableRoot, type, instance) {
    hoistableRoot = hoistableRoot.ownerDocument || hoistableRoot;
    hoistableRoot.head.insertBefore(
      instance,
      "title" === type ? hoistableRoot.querySelector("head > title") : null
    );
  }
  function isHostHoistableType(type, props, hostContext) {
    if (1 === hostContext || null != props.itemProp) return false;
    switch (type) {
      case "meta":
      case "title":
        return true;
      case "style":
        if ("string" !== typeof props.precedence || "string" !== typeof props.href || "" === props.href)
          break;
        return true;
      case "link":
        if ("string" !== typeof props.rel || "string" !== typeof props.href || "" === props.href || props.onLoad || props.onError)
          break;
        switch (props.rel) {
          case "stylesheet":
            return type = props.disabled, "string" === typeof props.precedence && null == type;
          default:
            return true;
        }
      case "script":
        if (props.async && "function" !== typeof props.async && "symbol" !== typeof props.async && !props.onLoad && !props.onError && props.src && "string" === typeof props.src)
          return true;
    }
    return false;
  }
  function preloadResource(resource) {
    return "stylesheet" === resource.type && 0 === (resource.state.loading & 3) ? false : true;
  }
  function suspendResource(state, hoistableRoot, resource, props) {
    if ("stylesheet" === resource.type && ("string" !== typeof props.media || false !== matchMedia(props.media).matches) && 0 === (resource.state.loading & 4)) {
      if (null === resource.instance) {
        var key = getStyleKey(props.href), instance = hoistableRoot.querySelector(
          getStylesheetSelectorFromKey(key)
        );
        if (instance) {
          hoistableRoot = instance._p;
          null !== hoistableRoot && "object" === typeof hoistableRoot && "function" === typeof hoistableRoot.then && (state.count++, state = onUnsuspend.bind(state), hoistableRoot.then(state, state));
          resource.state.loading |= 4;
          resource.instance = instance;
          markNodeAsHoistable(instance);
          return;
        }
        instance = hoistableRoot.ownerDocument || hoistableRoot;
        props = stylesheetPropsFromRawProps(props);
        (key = preloadPropsMap.get(key)) && adoptPreloadPropsForStylesheet(props, key);
        instance = instance.createElement("link");
        markNodeAsHoistable(instance);
        var linkInstance = instance;
        linkInstance._p = new Promise(function(resolve, reject) {
          linkInstance.onload = resolve;
          linkInstance.onerror = reject;
        });
        setInitialProperties(instance, "link", props);
        resource.instance = instance;
      }
      null === state.stylesheets && (state.stylesheets = /* @__PURE__ */ new Map());
      state.stylesheets.set(resource, hoistableRoot);
      (hoistableRoot = resource.state.preload) && 0 === (resource.state.loading & 3) && (state.count++, resource = onUnsuspend.bind(state), hoistableRoot.addEventListener("load", resource), hoistableRoot.addEventListener("error", resource));
    }
  }
  var estimatedBytesWithinLimit = 0;
  function waitForCommitToBeReady(state, timeoutOffset) {
    state.stylesheets && 0 === state.count && insertSuspendedStylesheets(state, state.stylesheets);
    return 0 < state.count || 0 < state.imgCount ? function(commit) {
      var stylesheetTimer = setTimeout(function() {
        state.stylesheets && insertSuspendedStylesheets(state, state.stylesheets);
        if (state.unsuspend) {
          var unsuspend = state.unsuspend;
          state.unsuspend = null;
          unsuspend();
        }
      }, 6e4 + timeoutOffset);
      0 < state.imgBytes && 0 === estimatedBytesWithinLimit && (estimatedBytesWithinLimit = 62500 * estimateBandwidth());
      var imgTimer = setTimeout(
        function() {
          state.waitingForImages = false;
          if (0 === state.count && (state.stylesheets && insertSuspendedStylesheets(state, state.stylesheets), state.unsuspend)) {
            var unsuspend = state.unsuspend;
            state.unsuspend = null;
            unsuspend();
          }
        },
        (state.imgBytes > estimatedBytesWithinLimit ? 50 : 800) + timeoutOffset
      );
      state.unsuspend = commit;
      return function() {
        state.unsuspend = null;
        clearTimeout(stylesheetTimer);
        clearTimeout(imgTimer);
      };
    } : null;
  }
  function onUnsuspend() {
    this.count--;
    if (0 === this.count && (0 === this.imgCount || !this.waitingForImages)) {
      if (this.stylesheets) insertSuspendedStylesheets(this, this.stylesheets);
      else if (this.unsuspend) {
        var unsuspend = this.unsuspend;
        this.unsuspend = null;
        unsuspend();
      }
    }
  }
  var precedencesByRoot = null;
  function insertSuspendedStylesheets(state, resources) {
    state.stylesheets = null;
    null !== state.unsuspend && (state.count++, precedencesByRoot = /* @__PURE__ */ new Map(), resources.forEach(insertStylesheetIntoRoot, state), precedencesByRoot = null, onUnsuspend.call(state));
  }
  function insertStylesheetIntoRoot(root2, resource) {
    if (!(resource.state.loading & 4)) {
      var precedences = precedencesByRoot.get(root2);
      if (precedences) var last = precedences.get(null);
      else {
        precedences = /* @__PURE__ */ new Map();
        precedencesByRoot.set(root2, precedences);
        for (var nodes = root2.querySelectorAll(
          "link[data-precedence],style[data-precedence]"
        ), i = 0; i < nodes.length; i++) {
          var node = nodes[i];
          if ("LINK" === node.nodeName || "not all" !== node.getAttribute("media"))
            precedences.set(node.dataset.precedence, node), last = node;
        }
        last && precedences.set(null, last);
      }
      nodes = resource.instance;
      node = nodes.getAttribute("data-precedence");
      i = precedences.get(node) || last;
      i === last && precedences.set(null, nodes);
      precedences.set(node, nodes);
      this.count++;
      last = onUnsuspend.bind(this);
      nodes.addEventListener("load", last);
      nodes.addEventListener("error", last);
      i ? i.parentNode.insertBefore(nodes, i.nextSibling) : (root2 = 9 === root2.nodeType ? root2.head : root2, root2.insertBefore(nodes, root2.firstChild));
      resource.state.loading |= 4;
    }
  }
  var HostTransitionContext = {
    $$typeof: REACT_CONTEXT_TYPE,
    Provider: null,
    Consumer: null,
    _currentValue: sharedNotPendingObject,
    _currentValue2: sharedNotPendingObject,
    _threadCount: 0
  };
  function FiberRootNode(containerInfo, tag, hydrate, identifierPrefix, onUncaughtError, onCaughtError, onRecoverableError, onDefaultTransitionIndicator, formState) {
    this.tag = 1;
    this.containerInfo = containerInfo;
    this.pingCache = this.current = this.pendingChildren = null;
    this.timeoutHandle = -1;
    this.callbackNode = this.next = this.pendingContext = this.context = this.cancelPendingCommit = null;
    this.callbackPriority = 0;
    this.expirationTimes = createLaneMap(-1);
    this.entangledLanes = this.shellSuspendCounter = this.errorRecoveryDisabledLanes = this.expiredLanes = this.warmLanes = this.pingedLanes = this.suspendedLanes = this.pendingLanes = 0;
    this.entanglements = createLaneMap(0);
    this.hiddenUpdates = createLaneMap(null);
    this.identifierPrefix = identifierPrefix;
    this.onUncaughtError = onUncaughtError;
    this.onCaughtError = onCaughtError;
    this.onRecoverableError = onRecoverableError;
    this.pooledCache = null;
    this.pooledCacheLanes = 0;
    this.formState = formState;
    this.incompleteTransitions = /* @__PURE__ */ new Map();
  }
  function createFiberRoot(containerInfo, tag, hydrate, initialChildren, hydrationCallbacks, isStrictMode, identifierPrefix, formState, onUncaughtError, onCaughtError, onRecoverableError, onDefaultTransitionIndicator) {
    containerInfo = new FiberRootNode(
      containerInfo,
      tag,
      hydrate,
      identifierPrefix,
      onUncaughtError,
      onCaughtError,
      onRecoverableError,
      onDefaultTransitionIndicator,
      formState
    );
    tag = 1;
    true === isStrictMode && (tag |= 24);
    isStrictMode = createFiberImplClass(3, null, null, tag);
    containerInfo.current = isStrictMode;
    isStrictMode.stateNode = containerInfo;
    tag = createCache();
    tag.refCount++;
    containerInfo.pooledCache = tag;
    tag.refCount++;
    isStrictMode.memoizedState = {
      element: initialChildren,
      isDehydrated: hydrate,
      cache: tag
    };
    initializeUpdateQueue(isStrictMode);
    return containerInfo;
  }
  function getContextForSubtree(parentComponent) {
    if (!parentComponent) return emptyContextObject;
    parentComponent = emptyContextObject;
    return parentComponent;
  }
  function updateContainerImpl(rootFiber, lane, element, container, parentComponent, callback) {
    parentComponent = getContextForSubtree(parentComponent);
    null === container.context ? container.context = parentComponent : container.pendingContext = parentComponent;
    container = createUpdate(lane);
    container.payload = { element };
    callback = void 0 === callback ? null : callback;
    null !== callback && (container.callback = callback);
    element = enqueueUpdate(rootFiber, container, lane);
    null !== element && (scheduleUpdateOnFiber(element, rootFiber, lane), entangleTransitions(element, rootFiber, lane));
  }
  function markRetryLaneImpl(fiber, retryLane) {
    fiber = fiber.memoizedState;
    if (null !== fiber && null !== fiber.dehydrated) {
      var a = fiber.retryLane;
      fiber.retryLane = 0 !== a && a < retryLane ? a : retryLane;
    }
  }
  function markRetryLaneIfNotHydrated(fiber, retryLane) {
    markRetryLaneImpl(fiber, retryLane);
    (fiber = fiber.alternate) && markRetryLaneImpl(fiber, retryLane);
  }
  function attemptContinuousHydration(fiber) {
    if (13 === fiber.tag || 31 === fiber.tag) {
      var root2 = enqueueConcurrentRenderForLane(fiber, 67108864);
      null !== root2 && scheduleUpdateOnFiber(root2, fiber, 67108864);
      markRetryLaneIfNotHydrated(fiber, 67108864);
    }
  }
  function attemptHydrationAtCurrentPriority(fiber) {
    if (13 === fiber.tag || 31 === fiber.tag) {
      var lane = requestUpdateLane();
      lane = getBumpedLaneForHydrationByLane(lane);
      var root2 = enqueueConcurrentRenderForLane(fiber, lane);
      null !== root2 && scheduleUpdateOnFiber(root2, fiber, lane);
      markRetryLaneIfNotHydrated(fiber, lane);
    }
  }
  var _enabled = true;
  function dispatchDiscreteEvent(domEventName, eventSystemFlags, container, nativeEvent) {
    var prevTransition = ReactSharedInternals.T;
    ReactSharedInternals.T = null;
    var previousPriority = ReactDOMSharedInternals.p;
    try {
      ReactDOMSharedInternals.p = 2, dispatchEvent(domEventName, eventSystemFlags, container, nativeEvent);
    } finally {
      ReactDOMSharedInternals.p = previousPriority, ReactSharedInternals.T = prevTransition;
    }
  }
  function dispatchContinuousEvent(domEventName, eventSystemFlags, container, nativeEvent) {
    var prevTransition = ReactSharedInternals.T;
    ReactSharedInternals.T = null;
    var previousPriority = ReactDOMSharedInternals.p;
    try {
      ReactDOMSharedInternals.p = 8, dispatchEvent(domEventName, eventSystemFlags, container, nativeEvent);
    } finally {
      ReactDOMSharedInternals.p = previousPriority, ReactSharedInternals.T = prevTransition;
    }
  }
  function dispatchEvent(domEventName, eventSystemFlags, targetContainer, nativeEvent) {
    if (_enabled) {
      var blockedOn = findInstanceBlockingEvent(nativeEvent);
      if (null === blockedOn)
        dispatchEventForPluginEventSystem(
          domEventName,
          eventSystemFlags,
          nativeEvent,
          return_targetInst,
          targetContainer
        ), clearIfContinuousEvent(domEventName, nativeEvent);
      else if (queueIfContinuousEvent(
        blockedOn,
        domEventName,
        eventSystemFlags,
        targetContainer,
        nativeEvent
      ))
        nativeEvent.stopPropagation();
      else if (clearIfContinuousEvent(domEventName, nativeEvent), eventSystemFlags & 4 && -1 < discreteReplayableEvents.indexOf(domEventName)) {
        for (; null !== blockedOn; ) {
          var fiber = getInstanceFromNode(blockedOn);
          if (null !== fiber)
            switch (fiber.tag) {
              case 3:
                fiber = fiber.stateNode;
                if (fiber.current.memoizedState.isDehydrated) {
                  var lanes = getHighestPriorityLanes(fiber.pendingLanes);
                  if (0 !== lanes) {
                    var root2 = fiber;
                    root2.pendingLanes |= 2;
                    for (root2.entangledLanes |= 2; lanes; ) {
                      var lane = 1 << 31 - clz32(lanes);
                      root2.entanglements[1] |= lane;
                      lanes &= ~lane;
                    }
                    ensureRootIsScheduled(fiber);
                    0 === (executionContext & 6) && (workInProgressRootRenderTargetTime = now() + 500, flushSyncWorkAcrossRoots_impl(0));
                  }
                }
                break;
              case 31:
              case 13:
                root2 = enqueueConcurrentRenderForLane(fiber, 2), null !== root2 && scheduleUpdateOnFiber(root2, fiber, 2), flushSyncWork$1(), markRetryLaneIfNotHydrated(fiber, 2);
            }
          fiber = findInstanceBlockingEvent(nativeEvent);
          null === fiber && dispatchEventForPluginEventSystem(
            domEventName,
            eventSystemFlags,
            nativeEvent,
            return_targetInst,
            targetContainer
          );
          if (fiber === blockedOn) break;
          blockedOn = fiber;
        }
        null !== blockedOn && nativeEvent.stopPropagation();
      } else
        dispatchEventForPluginEventSystem(
          domEventName,
          eventSystemFlags,
          nativeEvent,
          null,
          targetContainer
        );
    }
  }
  function findInstanceBlockingEvent(nativeEvent) {
    nativeEvent = getEventTarget(nativeEvent);
    return findInstanceBlockingTarget(nativeEvent);
  }
  var return_targetInst = null;
  function findInstanceBlockingTarget(targetNode) {
    return_targetInst = null;
    targetNode = getClosestInstanceFromNode(targetNode);
    if (null !== targetNode) {
      var nearestMounted = getNearestMountedFiber(targetNode);
      if (null === nearestMounted) targetNode = null;
      else {
        var tag = nearestMounted.tag;
        if (13 === tag) {
          targetNode = getSuspenseInstanceFromFiber(nearestMounted);
          if (null !== targetNode) return targetNode;
          targetNode = null;
        } else if (31 === tag) {
          targetNode = getActivityInstanceFromFiber(nearestMounted);
          if (null !== targetNode) return targetNode;
          targetNode = null;
        } else if (3 === tag) {
          if (nearestMounted.stateNode.current.memoizedState.isDehydrated)
            return 3 === nearestMounted.tag ? nearestMounted.stateNode.containerInfo : null;
          targetNode = null;
        } else nearestMounted !== targetNode && (targetNode = null);
      }
    }
    return_targetInst = targetNode;
    return null;
  }
  function getEventPriority(domEventName) {
    switch (domEventName) {
      case "beforetoggle":
      case "cancel":
      case "click":
      case "close":
      case "contextmenu":
      case "copy":
      case "cut":
      case "auxclick":
      case "dblclick":
      case "dragend":
      case "dragstart":
      case "drop":
      case "focusin":
      case "focusout":
      case "input":
      case "invalid":
      case "keydown":
      case "keypress":
      case "keyup":
      case "mousedown":
      case "mouseup":
      case "paste":
      case "pause":
      case "play":
      case "pointercancel":
      case "pointerdown":
      case "pointerup":
      case "ratechange":
      case "reset":
      case "resize":
      case "seeked":
      case "submit":
      case "toggle":
      case "touchcancel":
      case "touchend":
      case "touchstart":
      case "volumechange":
      case "change":
      case "selectionchange":
      case "textInput":
      case "compositionstart":
      case "compositionend":
      case "compositionupdate":
      case "beforeblur":
      case "afterblur":
      case "beforeinput":
      case "blur":
      case "fullscreenchange":
      case "focus":
      case "hashchange":
      case "popstate":
      case "select":
      case "selectstart":
        return 2;
      case "drag":
      case "dragenter":
      case "dragexit":
      case "dragleave":
      case "dragover":
      case "mousemove":
      case "mouseout":
      case "mouseover":
      case "pointermove":
      case "pointerout":
      case "pointerover":
      case "scroll":
      case "touchmove":
      case "wheel":
      case "mouseenter":
      case "mouseleave":
      case "pointerenter":
      case "pointerleave":
        return 8;
      case "message":
        switch (getCurrentPriorityLevel()) {
          case ImmediatePriority:
            return 2;
          case UserBlockingPriority:
            return 8;
          case NormalPriority$1:
          case LowPriority:
            return 32;
          case IdlePriority:
            return 268435456;
          default:
            return 32;
        }
      default:
        return 32;
    }
  }
  var hasScheduledReplayAttempt = false, queuedFocus = null, queuedDrag = null, queuedMouse = null, queuedPointers = /* @__PURE__ */ new Map(), queuedPointerCaptures = /* @__PURE__ */ new Map(), queuedExplicitHydrationTargets = [], discreteReplayableEvents = "mousedown mouseup touchcancel touchend touchstart auxclick dblclick pointercancel pointerdown pointerup dragend dragstart drop compositionend compositionstart keydown keypress keyup input textInput copy cut paste click change contextmenu reset".split(
    " "
  );
  function clearIfContinuousEvent(domEventName, nativeEvent) {
    switch (domEventName) {
      case "focusin":
      case "focusout":
        queuedFocus = null;
        break;
      case "dragenter":
      case "dragleave":
        queuedDrag = null;
        break;
      case "mouseover":
      case "mouseout":
        queuedMouse = null;
        break;
      case "pointerover":
      case "pointerout":
        queuedPointers.delete(nativeEvent.pointerId);
        break;
      case "gotpointercapture":
      case "lostpointercapture":
        queuedPointerCaptures.delete(nativeEvent.pointerId);
    }
  }
  function accumulateOrCreateContinuousQueuedReplayableEvent(existingQueuedEvent, blockedOn, domEventName, eventSystemFlags, targetContainer, nativeEvent) {
    if (null === existingQueuedEvent || existingQueuedEvent.nativeEvent !== nativeEvent)
      return existingQueuedEvent = {
        blockedOn,
        domEventName,
        eventSystemFlags,
        nativeEvent,
        targetContainers: [targetContainer]
      }, null !== blockedOn && (blockedOn = getInstanceFromNode(blockedOn), null !== blockedOn && attemptContinuousHydration(blockedOn)), existingQueuedEvent;
    existingQueuedEvent.eventSystemFlags |= eventSystemFlags;
    blockedOn = existingQueuedEvent.targetContainers;
    null !== targetContainer && -1 === blockedOn.indexOf(targetContainer) && blockedOn.push(targetContainer);
    return existingQueuedEvent;
  }
  function queueIfContinuousEvent(blockedOn, domEventName, eventSystemFlags, targetContainer, nativeEvent) {
    switch (domEventName) {
      case "focusin":
        return queuedFocus = accumulateOrCreateContinuousQueuedReplayableEvent(
          queuedFocus,
          blockedOn,
          domEventName,
          eventSystemFlags,
          targetContainer,
          nativeEvent
        ), true;
      case "dragenter":
        return queuedDrag = accumulateOrCreateContinuousQueuedReplayableEvent(
          queuedDrag,
          blockedOn,
          domEventName,
          eventSystemFlags,
          targetContainer,
          nativeEvent
        ), true;
      case "mouseover":
        return queuedMouse = accumulateOrCreateContinuousQueuedReplayableEvent(
          queuedMouse,
          blockedOn,
          domEventName,
          eventSystemFlags,
          targetContainer,
          nativeEvent
        ), true;
      case "pointerover":
        var pointerId = nativeEvent.pointerId;
        queuedPointers.set(
          pointerId,
          accumulateOrCreateContinuousQueuedReplayableEvent(
            queuedPointers.get(pointerId) || null,
            blockedOn,
            domEventName,
            eventSystemFlags,
            targetContainer,
            nativeEvent
          )
        );
        return true;
      case "gotpointercapture":
        return pointerId = nativeEvent.pointerId, queuedPointerCaptures.set(
          pointerId,
          accumulateOrCreateContinuousQueuedReplayableEvent(
            queuedPointerCaptures.get(pointerId) || null,
            blockedOn,
            domEventName,
            eventSystemFlags,
            targetContainer,
            nativeEvent
          )
        ), true;
    }
    return false;
  }
  function attemptExplicitHydrationTarget(queuedTarget) {
    var targetInst = getClosestInstanceFromNode(queuedTarget.target);
    if (null !== targetInst) {
      var nearestMounted = getNearestMountedFiber(targetInst);
      if (null !== nearestMounted) {
        if (targetInst = nearestMounted.tag, 13 === targetInst) {
          if (targetInst = getSuspenseInstanceFromFiber(nearestMounted), null !== targetInst) {
            queuedTarget.blockedOn = targetInst;
            runWithPriority(queuedTarget.priority, function() {
              attemptHydrationAtCurrentPriority(nearestMounted);
            });
            return;
          }
        } else if (31 === targetInst) {
          if (targetInst = getActivityInstanceFromFiber(nearestMounted), null !== targetInst) {
            queuedTarget.blockedOn = targetInst;
            runWithPriority(queuedTarget.priority, function() {
              attemptHydrationAtCurrentPriority(nearestMounted);
            });
            return;
          }
        } else if (3 === targetInst && nearestMounted.stateNode.current.memoizedState.isDehydrated) {
          queuedTarget.blockedOn = 3 === nearestMounted.tag ? nearestMounted.stateNode.containerInfo : null;
          return;
        }
      }
    }
    queuedTarget.blockedOn = null;
  }
  function attemptReplayContinuousQueuedEvent(queuedEvent) {
    if (null !== queuedEvent.blockedOn) return false;
    for (var targetContainers = queuedEvent.targetContainers; 0 < targetContainers.length; ) {
      var nextBlockedOn = findInstanceBlockingEvent(queuedEvent.nativeEvent);
      if (null === nextBlockedOn) {
        nextBlockedOn = queuedEvent.nativeEvent;
        var nativeEventClone = new nextBlockedOn.constructor(
          nextBlockedOn.type,
          nextBlockedOn
        );
        currentReplayingEvent = nativeEventClone;
        nextBlockedOn.target.dispatchEvent(nativeEventClone);
        currentReplayingEvent = null;
      } else
        return targetContainers = getInstanceFromNode(nextBlockedOn), null !== targetContainers && attemptContinuousHydration(targetContainers), queuedEvent.blockedOn = nextBlockedOn, false;
      targetContainers.shift();
    }
    return true;
  }
  function attemptReplayContinuousQueuedEventInMap(queuedEvent, key, map) {
    attemptReplayContinuousQueuedEvent(queuedEvent) && map.delete(key);
  }
  function replayUnblockedEvents() {
    hasScheduledReplayAttempt = false;
    null !== queuedFocus && attemptReplayContinuousQueuedEvent(queuedFocus) && (queuedFocus = null);
    null !== queuedDrag && attemptReplayContinuousQueuedEvent(queuedDrag) && (queuedDrag = null);
    null !== queuedMouse && attemptReplayContinuousQueuedEvent(queuedMouse) && (queuedMouse = null);
    queuedPointers.forEach(attemptReplayContinuousQueuedEventInMap);
    queuedPointerCaptures.forEach(attemptReplayContinuousQueuedEventInMap);
  }
  function scheduleCallbackIfUnblocked(queuedEvent, unblocked) {
    queuedEvent.blockedOn === unblocked && (queuedEvent.blockedOn = null, hasScheduledReplayAttempt || (hasScheduledReplayAttempt = true, Scheduler.unstable_scheduleCallback(
      Scheduler.unstable_NormalPriority,
      replayUnblockedEvents
    )));
  }
  var lastScheduledReplayQueue = null;
  function scheduleReplayQueueIfNeeded(formReplayingQueue) {
    lastScheduledReplayQueue !== formReplayingQueue && (lastScheduledReplayQueue = formReplayingQueue, Scheduler.unstable_scheduleCallback(
      Scheduler.unstable_NormalPriority,
      function() {
        lastScheduledReplayQueue === formReplayingQueue && (lastScheduledReplayQueue = null);
        for (var i = 0; i < formReplayingQueue.length; i += 3) {
          var form = formReplayingQueue[i], submitterOrAction = formReplayingQueue[i + 1], formData = formReplayingQueue[i + 2];
          if ("function" !== typeof submitterOrAction)
            if (null === findInstanceBlockingTarget(submitterOrAction || form))
              continue;
            else break;
          var formInst = getInstanceFromNode(form);
          null !== formInst && (formReplayingQueue.splice(i, 3), i -= 3, startHostTransition(
            formInst,
            {
              pending: true,
              data: formData,
              method: form.method,
              action: submitterOrAction
            },
            submitterOrAction,
            formData
          ));
        }
      }
    ));
  }
  function retryIfBlockedOn(unblocked) {
    function unblock(queuedEvent) {
      return scheduleCallbackIfUnblocked(queuedEvent, unblocked);
    }
    null !== queuedFocus && scheduleCallbackIfUnblocked(queuedFocus, unblocked);
    null !== queuedDrag && scheduleCallbackIfUnblocked(queuedDrag, unblocked);
    null !== queuedMouse && scheduleCallbackIfUnblocked(queuedMouse, unblocked);
    queuedPointers.forEach(unblock);
    queuedPointerCaptures.forEach(unblock);
    for (var i = 0; i < queuedExplicitHydrationTargets.length; i++) {
      var queuedTarget = queuedExplicitHydrationTargets[i];
      queuedTarget.blockedOn === unblocked && (queuedTarget.blockedOn = null);
    }
    for (; 0 < queuedExplicitHydrationTargets.length && (i = queuedExplicitHydrationTargets[0], null === i.blockedOn); )
      attemptExplicitHydrationTarget(i), null === i.blockedOn && queuedExplicitHydrationTargets.shift();
    i = (unblocked.ownerDocument || unblocked).$$reactFormReplay;
    if (null != i)
      for (queuedTarget = 0; queuedTarget < i.length; queuedTarget += 3) {
        var form = i[queuedTarget], submitterOrAction = i[queuedTarget + 1], formProps = form[internalPropsKey] || null;
        if ("function" === typeof submitterOrAction)
          formProps || scheduleReplayQueueIfNeeded(i);
        else if (formProps) {
          var action = null;
          if (submitterOrAction && submitterOrAction.hasAttribute("formAction"))
            if (form = submitterOrAction, formProps = submitterOrAction[internalPropsKey] || null)
              action = formProps.formAction;
            else {
              if (null !== findInstanceBlockingTarget(form)) continue;
            }
          else action = formProps.action;
          "function" === typeof action ? i[queuedTarget + 1] = action : (i.splice(queuedTarget, 3), queuedTarget -= 3);
          scheduleReplayQueueIfNeeded(i);
        }
      }
  }
  function defaultOnDefaultTransitionIndicator() {
    function handleNavigate(event) {
      event.canIntercept && "react-transition" === event.info && event.intercept({
        handler: function() {
          return new Promise(function(resolve) {
            return pendingResolve = resolve;
          });
        },
        focusReset: "manual",
        scroll: "manual"
      });
    }
    function handleNavigateComplete() {
      null !== pendingResolve && (pendingResolve(), pendingResolve = null);
      isCancelled || setTimeout(startFakeNavigation, 20);
    }
    function startFakeNavigation() {
      if (!isCancelled && !navigation.transition) {
        var currentEntry = navigation.currentEntry;
        currentEntry && null != currentEntry.url && navigation.navigate(currentEntry.url, {
          state: currentEntry.getState(),
          info: "react-transition",
          history: "replace"
        });
      }
    }
    if ("object" === typeof navigation) {
      var isCancelled = false, pendingResolve = null;
      navigation.addEventListener("navigate", handleNavigate);
      navigation.addEventListener("navigatesuccess", handleNavigateComplete);
      navigation.addEventListener("navigateerror", handleNavigateComplete);
      setTimeout(startFakeNavigation, 100);
      return function() {
        isCancelled = true;
        navigation.removeEventListener("navigate", handleNavigate);
        navigation.removeEventListener("navigatesuccess", handleNavigateComplete);
        navigation.removeEventListener("navigateerror", handleNavigateComplete);
        null !== pendingResolve && (pendingResolve(), pendingResolve = null);
      };
    }
  }
  function ReactDOMRoot(internalRoot) {
    this._internalRoot = internalRoot;
  }
  ReactDOMHydrationRoot.prototype.render = ReactDOMRoot.prototype.render = function(children) {
    var root2 = this._internalRoot;
    if (null === root2) throw Error(formatProdErrorMessage(409));
    var current = root2.current, lane = requestUpdateLane();
    updateContainerImpl(current, lane, children, root2, null, null);
  };
  ReactDOMHydrationRoot.prototype.unmount = ReactDOMRoot.prototype.unmount = function() {
    var root2 = this._internalRoot;
    if (null !== root2) {
      this._internalRoot = null;
      var container = root2.containerInfo;
      updateContainerImpl(root2.current, 2, null, root2, null, null);
      flushSyncWork$1();
      container[internalContainerInstanceKey] = null;
    }
  };
  function ReactDOMHydrationRoot(internalRoot) {
    this._internalRoot = internalRoot;
  }
  ReactDOMHydrationRoot.prototype.unstable_scheduleHydration = function(target) {
    if (target) {
      var updatePriority = resolveUpdatePriority();
      target = { blockedOn: null, target, priority: updatePriority };
      for (var i = 0; i < queuedExplicitHydrationTargets.length && 0 !== updatePriority && updatePriority < queuedExplicitHydrationTargets[i].priority; i++) ;
      queuedExplicitHydrationTargets.splice(i, 0, target);
      0 === i && attemptExplicitHydrationTarget(target);
    }
  };
  var isomorphicReactPackageVersion$jscomp$inline_1840 = React2.version;
  if ("19.2.7" !== isomorphicReactPackageVersion$jscomp$inline_1840)
    throw Error(
      formatProdErrorMessage(
        527,
        isomorphicReactPackageVersion$jscomp$inline_1840,
        "19.2.7"
      )
    );
  ReactDOMSharedInternals.findDOMNode = function(componentOrElement) {
    var fiber = componentOrElement._reactInternals;
    if (void 0 === fiber) {
      if ("function" === typeof componentOrElement.render)
        throw Error(formatProdErrorMessage(188));
      componentOrElement = Object.keys(componentOrElement).join(",");
      throw Error(formatProdErrorMessage(268, componentOrElement));
    }
    componentOrElement = findCurrentFiberUsingSlowPath(fiber);
    componentOrElement = null !== componentOrElement ? findCurrentHostFiberImpl(componentOrElement) : null;
    componentOrElement = null === componentOrElement ? null : componentOrElement.stateNode;
    return componentOrElement;
  };
  var internals$jscomp$inline_2347 = {
    bundleType: 0,
    version: "19.2.7",
    rendererPackageName: "react-dom",
    currentDispatcherRef: ReactSharedInternals,
    reconcilerVersion: "19.2.7"
  };
  if ("undefined" !== typeof __REACT_DEVTOOLS_GLOBAL_HOOK__) {
    var hook$jscomp$inline_2348 = __REACT_DEVTOOLS_GLOBAL_HOOK__;
    if (!hook$jscomp$inline_2348.isDisabled && hook$jscomp$inline_2348.supportsFiber)
      try {
        rendererID = hook$jscomp$inline_2348.inject(
          internals$jscomp$inline_2347
        ), injectedHook = hook$jscomp$inline_2348;
      } catch (err) {
      }
  }
  reactDomClient_production.createRoot = function(container, options2) {
    if (!isValidContainer(container)) throw Error(formatProdErrorMessage(299));
    var isStrictMode = false, identifierPrefix = "", onUncaughtError = defaultOnUncaughtError, onCaughtError = defaultOnCaughtError, onRecoverableError = defaultOnRecoverableError;
    null !== options2 && void 0 !== options2 && (true === options2.unstable_strictMode && (isStrictMode = true), void 0 !== options2.identifierPrefix && (identifierPrefix = options2.identifierPrefix), void 0 !== options2.onUncaughtError && (onUncaughtError = options2.onUncaughtError), void 0 !== options2.onCaughtError && (onCaughtError = options2.onCaughtError), void 0 !== options2.onRecoverableError && (onRecoverableError = options2.onRecoverableError));
    options2 = createFiberRoot(
      container,
      1,
      false,
      null,
      null,
      isStrictMode,
      identifierPrefix,
      null,
      onUncaughtError,
      onCaughtError,
      onRecoverableError,
      defaultOnDefaultTransitionIndicator
    );
    container[internalContainerInstanceKey] = options2.current;
    listenToAllSupportedEvents(container);
    return new ReactDOMRoot(options2);
  };
  reactDomClient_production.hydrateRoot = function(container, initialChildren, options2) {
    if (!isValidContainer(container)) throw Error(formatProdErrorMessage(299));
    var isStrictMode = false, identifierPrefix = "", onUncaughtError = defaultOnUncaughtError, onCaughtError = defaultOnCaughtError, onRecoverableError = defaultOnRecoverableError, formState = null;
    null !== options2 && void 0 !== options2 && (true === options2.unstable_strictMode && (isStrictMode = true), void 0 !== options2.identifierPrefix && (identifierPrefix = options2.identifierPrefix), void 0 !== options2.onUncaughtError && (onUncaughtError = options2.onUncaughtError), void 0 !== options2.onCaughtError && (onCaughtError = options2.onCaughtError), void 0 !== options2.onRecoverableError && (onRecoverableError = options2.onRecoverableError), void 0 !== options2.formState && (formState = options2.formState));
    initialChildren = createFiberRoot(
      container,
      1,
      true,
      initialChildren,
      null != options2 ? options2 : null,
      isStrictMode,
      identifierPrefix,
      formState,
      onUncaughtError,
      onCaughtError,
      onRecoverableError,
      defaultOnDefaultTransitionIndicator
    );
    initialChildren.context = getContextForSubtree(null);
    options2 = initialChildren.current;
    isStrictMode = requestUpdateLane();
    isStrictMode = getBumpedLaneForHydrationByLane(isStrictMode);
    identifierPrefix = createUpdate(isStrictMode);
    identifierPrefix.callback = null;
    enqueueUpdate(options2, identifierPrefix, isStrictMode);
    options2 = isStrictMode;
    initialChildren.current.lanes = options2;
    markRootUpdated$1(initialChildren, options2);
    ensureRootIsScheduled(initialChildren);
    container[internalContainerInstanceKey] = initialChildren.current;
    listenToAllSupportedEvents(container);
    return new ReactDOMHydrationRoot(initialChildren);
  };
  reactDomClient_production.version = "19.2.7";
  return reactDomClient_production;
}
var hasRequiredClient;
function requireClient() {
  if (hasRequiredClient) return client.exports;
  hasRequiredClient = 1;
  function checkDCE() {
    if (typeof __REACT_DEVTOOLS_GLOBAL_HOOK__ === "undefined" || typeof __REACT_DEVTOOLS_GLOBAL_HOOK__.checkDCE !== "function") {
      return;
    }
    try {
      __REACT_DEVTOOLS_GLOBAL_HOOK__.checkDCE(checkDCE);
    } catch (err) {
      console.error(err);
    }
  }
  {
    checkDCE();
    client.exports = requireReactDomClient_production();
  }
  return client.exports;
}
var clientExports = requireClient();
const LANGS = [
  "zh",
  "en",
  "ja",
  "ko",
  "fr",
  "de",
  "es",
  "th",
  "id",
  "ru",
  "ar",
  "pt",
  "it",
  "pl",
  "nl",
  "ms",
  "he",
  "hi",
  "zh-TW"
];
function normalizeLang(raw) {
  const value = raw?.trim().toLowerCase();
  if (!value) return "en";
  if (/^zh[-_](tw|hk|mo|hant)/.test(value)) return "zh-TW";
  for (const lang of LANGS) {
    if (lang !== "en" && lang !== "zh-TW" && value.startsWith(lang)) return lang;
  }
  if (/^in\b/.test(value) || /^in[-_]/.test(value)) return "id";
  if (/^iw\b/.test(value) || /^iw[-_]/.test(value)) return "he";
  return "en";
}
const HTML_LANGS = {
  zh: "zh-CN",
  en: "en-US",
  ja: "ja-JP",
  ko: "ko-KR",
  fr: "fr-FR",
  de: "de-DE",
  es: "es-ES",
  th: "th-TH",
  id: "id-ID",
  ru: "ru-RU",
  ar: "ar-SA",
  pt: "pt-BR",
  it: "it-IT",
  pl: "pl-PL",
  nl: "nl-NL",
  ms: "ms-MY",
  he: "he-IL",
  hi: "hi-IN",
  "zh-TW": "zh-TW"
};
function htmlLang(lang) {
  return HTML_LANGS[lang];
}
function brandName(lang) {
  if (lang === "zh") return "微语文档";
  if (lang === "zh-TW") return "微語文檔";
  return "Bytedesk Office";
}
const MAC_KEY_NAMES = {
  "⌫": "Backspace",
  "⌦": "Delete",
  "⏎": "Enter",
  "↩": "Enter",
  "␣": "Space"
};
const HAS_MAC_SYMBOL = /[⌘⌃⌥⇧⌫⌦⏎↩␣]/;
const CHORD = /([⌘⌃⌥⇧]+)(F\d{1,2}|[A-Za-z0-9±=`'\\,./;[\]\-←↑→↓⌫⌦⏎↩␣]|\+)?/g;
function chordToWin(mods, key) {
  const parts = [];
  if (mods.includes("⌘") || mods.includes("⌃")) parts.push("Ctrl");
  if (mods.includes("⌥")) parts.push("Alt");
  if (mods.includes("⇧")) parts.push("Shift");
  if (key) parts.push(MAC_KEY_NAMES[key] ?? key);
  return parts.join("+");
}
function macShortcutsToWin(text) {
  if (!HAS_MAC_SYMBOL.test(text)) return text;
  return text.replace(new RegExp("⌘\\/(?=\\p{L}{2})", "gu"), "").replace(
    CHORD,
    (_m, mods, key) => key === "+" ? `${chordToWin(mods, void 0)}+` : chordToWin(mods, key)
  ).replace(/[⌫⌦⏎↩␣]/g, (glyph) => MAC_KEY_NAMES[glyph] ?? glyph);
}
const IS_MAC = (() => {
  const g = globalThis;
  if (g.navigator?.platform) return /mac/i.test(g.navigator.platform);
  return g.process?.platform === "darwin";
})();
const platformShortcuts = IS_MAC ? (text) => text : macShortcutsToWin;
function format(template, params) {
  if (!params) return template;
  return template.replace(
    /\{(\w+)\}/g,
    (match, name) => name in params ? String(params[name]) : match
  );
}
function createI18n(dicts) {
  return (lang, key, params) => platformShortcuts(format(dicts[lang][key], params));
}
const logoLockup = "data:image/svg+xml,%3csvg%20width='240'%20height='240'%20viewBox='0%200%20240%20240'%20fill='none'%20xmlns='http://www.w3.org/2000/svg'%3e%3c!--%20Bytedesk%20Office%20badge:%20doc%20icon%20only.%20The%20wordmark%20is%20rendered%20as%20HTML%20text%20next%20to%20this%20icon%20so%20it%20can%20be%20localized%20(zh:%20微语文档%20/%20en:%20Bytedesk%20Office).%20--%3e%3crect%20x='36'%20y='42'%20width='156'%20height='156'%20rx='36'%20fill='%230077E6'/%3e%3cpath%20fill-rule='evenodd'%20clip-rule='evenodd'%20d='M96%2078H126L156%20108V162H96V78ZM126%20104V78L156%20104H126ZM105%20122H147V131H105V122ZM105%20139H147V148H105V139ZM105%20156H135V165H105V156Z'%20fill='white'/%3e%3c/svg%3e";
const iconDocx = "data:image/svg+xml,%3csvg%20width='240'%20height='240'%20viewBox='24%2024%20240%20240'%20fill='none'%20xmlns='http://www.w3.org/2000/svg'%3e%3crect%20x='24'%20y='24'%20width='240'%20height='240'%20rx='48'%20fill='%233276CD'/%3e%3cpath%20d='M207.373%2096H196.927C192.749%2096%20189.267%2098.4%20188.57%20102C173.946%20160.8%20174.642%20159%20173.946%20163.8C173.946%20163.2%20173.946%20162.6%20173.249%20161.4C172.553%20158.4%20173.946%20161.4%20157.232%20102C155.839%2098.4%20153.053%2096%20148.875%2096H139.822C135.643%2096%20132.161%2098.4%20131.465%20102C114.751%20161.4%20114.751%20159.6%20114.054%20163.8V161.4C113.358%20158.4%20104.305%20117.6%20100.823%20102C99.4299%2098.4%2096.6442%2096%2092.4658%2096H80.6268C75.0556%2096%2070.8771%20100.8%2072.2699%20105C77.8412%20124.8%2091.073%20171%2095.2514%20186.6C96.6442%20190.2%20100.126%20192%20103.608%20192H121.019C125.197%20192%20128.679%20190.2%20129.375%20186.6L141.911%20144L144%20138L146.089%20144C146.089%20144%20154.446%20174%20158.625%20186.6C159.321%20189.6%20162.803%20192%20166.285%20192H183.695C187.177%20192%20190.659%20190.2%20192.052%20186.6C205.98%20137.4%20212.944%20115.2%20215.73%20105C217.123%20100.8%20212.944%2096%20207.373%2096Z'%20fill='white'/%3e%3c/svg%3e";
const iconXlsx = "data:image/svg+xml,%3csvg%20width='240'%20height='240'%20viewBox='0%200%20240%20240'%20fill='none'%20xmlns='http://www.w3.org/2000/svg'%3e%3crect%20width='240'%20height='240'%20rx='48'%20fill='%234FA16B'/%3e%3cpath%20d='M96.3863%2061.8047C98.5543%2061.8052%20100.377%2063.1591%20101.332%2065.1562L101.367%2065.2148C114.487%2090.1591%20110.279%2082.597%20118.664%20100.758L120.445%20104.637L121.98%20100.641C125.532%2091.3552%20127.882%2085.7897%20138.621%2065.2031L137.031%2064.373L138.632%2065.2031L138.656%2065.1562C139.61%2063.1599%20141.434%2061.8064%20143.601%2061.8047H160.933C164.595%2061.8047%20167.33%2065.8991%20165.75%2069.5977L165.375%2070.3242L165.339%2070.3828C160.568%2079.1142%20153.7%2091.1365%20148.019%20101.062C145.182%20106.02%20142.633%20110.459%20140.8%20113.695C139.885%20115.311%20139.148%20116.643%20138.632%20117.586C138.378%20118.052%20138.169%20118.436%20138.023%20118.723C137.951%20118.864%20137.877%20119.002%20137.824%20119.121C137.799%20119.178%20137.772%20119.259%20137.742%20119.344C137.727%20119.386%20137.704%20119.452%20137.683%20119.531C137.669%20119.588%20137.625%20119.77%20137.625%20120V120.469L137.859%20120.879L165.351%20169.629L165.375%20169.676C167.682%20173.542%20164.837%20178.195%20160.933%20178.195H143.601C141.455%20178.194%20139.917%20177.443%20139.007%20176.086L138.656%20175.477L138.621%20175.418L131.425%20161.473C126.778%20152.195%20128.26%20153.91%20121.945%20139.289L120.316%20135.504L118.652%20139.277C115.953%20145.392%20114.95%20148.635%20109.089%20160.395L101.367%20175.406L101.332%20175.477C100.498%20177.217%2098.8376%20178.195%2096.3863%20178.195H79.6519C75.0237%20178.195%2072.3782%20173.434%2074.6246%20169.676L74.6363%20169.629L102.14%20120.879L102.632%20120L102.14%20119.121L74.6832%2070.4648C73.747%2068.4964%2073.8839%2066.2974%2074.8003%2064.6172C75.6999%2062.9712%2077.3618%2061.8048%2079.6519%2061.8047H96.3863Z'%20fill='white'/%3e%3c/svg%3e";
const iconPptx = "data:image/svg+xml,%3csvg%20width='240'%20height='240'%20viewBox='24%2024%20240%20240'%20fill='none'%20xmlns='http://www.w3.org/2000/svg'%3e%3crect%20x='24'%20y='24'%20width='240'%20height='240'%20rx='48'%20fill='%23D33922'/%3e%3cpath%20d='M154.5%2096C176.5%2096%20191%20111.75%20191%20133.898C191%20178.195%20146.5%20171.797%20135%20171.797V199.852C135%20203.297%20132.5%20205.758%20129%20205.758H114C110.5%20205.758%20108%20203.297%20108%20199.852V101.906C108%2098.4609%20110.5%2096%20114%2096H154.5ZM135%20148.664H148.5C153%20148.664%20156.5%20147.188%20159%20144.727C164%20138.82%20164%20128.484%20159.5%20123.07C157%20120.117%20153.5%20119.133%20149%20119.133H135V148.664Z'%20fill='white'/%3e%3c/svg%3e";
const iconPdf = "data:image/svg+xml,%3csvg%20width='240'%20height='240'%20viewBox='0%200%20240%20240'%20fill='none'%20xmlns='http://www.w3.org/2000/svg'%3e%3crect%20width='240'%20height='240'%20rx='48'%20fill='%23EF4444'/%3e%3cpath%20d='M102.719%2063.0153C105.738%2052.1477%20126.265%2050.9398%20128.68%2066.6374C131.699%2075.6938%20127.472%2090.7878%20125.661%20100.448C130.491%20113.127%20137.133%20122.183%20147.397%20128.22C158.264%20127.013%20179.395%20125.202%20186.641%20132.447C192.678%20138.485%20191.471%20155.389%20175.774%20155.389C166.717%20155.389%20153.434%20151.767%20141.963%20145.126C129.284%20147.541%20114.19%20152.974%20100.907%20157.804C70.7196%20209.727%2053.2104%20186.181%2055.0216%20176.521C57.4366%20164.446%2073.7385%20154.786%2085.8136%20148.749C91.8511%20137.277%20100.907%20117.957%20106.944%20103.466C102.718%2086.5617%20100.304%2072.6754%20102.719%2063.0153ZM85.2149%20158.437C81.5921%20161.456%2070.1214%20171.117%2067.1026%20179.569C67.1026%20179.569%2073.7436%20176.55%2085.2149%20158.437ZM116.605%20113.718C112.378%20124.586%20107.548%20136.662%20101.511%20146.925C111.171%20142.699%20122.039%20137.869%20134.718%20134.85C127.473%20130.02%20121.435%20122.775%20116.605%20113.718ZM180.613%20143.932C183.028%20142.121%20179.406%20137.291%20158.275%20139.102C177.595%20147.555%20180.613%20143.932%20180.613%20143.932ZM116.013%2064.2419C114.805%2064.2436%20114.806%2080.5436%20117.221%2088.9958C120.239%2083.5616%20120.843%2064.2421%20116.013%2064.2419Z'%20fill='white'/%3e%3c/svg%3e";
const iconMd = "data:image/svg+xml,%3csvg%20width='240'%20height='240'%20viewBox='0%200%20240%20240'%20fill='none'%20xmlns='http://www.w3.org/2000/svg'%3e%3cg%20clip-path='url(%23clip0_704_14509)'%3e%3cpath%20d='M48%20240L192%20240C218.51%20240%20240%20218.51%20240%20192L240%2048C240%2021.4903%20218.51%201.91027e-05%20192%201.67852e-05L48%204.19629e-06C21.4903%201.87874e-06%201.91027e-05%2021.4903%201.67852e-05%2048L4.19629e-06%20192C1.87874e-06%20218.51%2021.4903%20240%2048%20240Z'%20fill='%238B5CF6'/%3e%3cpath%20d='M36.4103%20164L44.1723%20164C47.2768%20164%2049.8641%20161.8%2050.382%20158.5C61.2484%20104.6%2060.7313%20106.25%2061.2484%20101.85C61.2484%20102.4%2062%2098.9167%2063.5%20101.85C64.5%20105.792%2061.2484%2092.1333%2073.6679%20146.583C74.7029%20149.883%2076.7731%20152.083%2079.8776%20152.083L86.6045%20152.083C89.7097%20152.083%2092.297%20149.883%2092.8142%20146.583C103.693%2098.8889%20103.329%2099.9343%20104.415%20100.62C104.569%20100.717%20104.752%20100.807%20105%20100.75L105.752%20104.05C106.269%20106.8%20112.996%20144.2%20115.583%20158.5C116.618%20161.8%20118.688%20164%20121.793%20164L130.59%20164C134.729%20164%20137.834%20159.6%20136.799%20155.75C132.66%20137.6%20122.828%2095.25%20119.723%2080.95C118.688%2077.65%20116.101%2076%20113.513%2076L100.576%2076C97.4717%2076%2094.8843%2077.65%2094.3672%2080.95L85.0522%20120L83.5%20125.5L81.9477%20120C81.9477%20120%2075.738%2092.5%2072.6328%2080.95C72.1156%2078.2%2069.5283%2076%2066.941%2076L54.0044%2076C51.4171%2076%2048.8298%2077.65%2047.7947%2080.95C37.4454%20126.05%2032.2708%20146.4%2030.2006%20155.75C29.1655%20159.6%2032.2708%20164%2036.4103%20164Z'%20fill='white'/%3e%3cpath%20d='M168%2082C168%2078.6863%20170.686%2076%20174%2076H184C187.314%2076%20190%2078.6863%20190%2082V142H168V82Z'%20fill='white'/%3e%3cpath%20d='M175.248%20162.741C177.239%20165.001%20180.761%20165.001%20182.752%20162.741L208.684%20133.305C211.528%20130.076%20209.236%20125%20204.932%20125H153.068C148.765%20125%20146.472%20130.076%20149.316%20133.305L175.248%20162.741Z'%20fill='white'/%3e%3c/g%3e%3cdefs%3e%3cclipPath%20id='clip0_704_14509'%3e%3crect%20width='240'%20height='240'%20fill='white'%20transform='translate(240%20240)%20rotate(-180)'/%3e%3c/clipPath%3e%3c/defs%3e%3c/svg%3e";
const INITIAL_DELAY_MS = 500;
const RESHOW_DELAY_MS = 100;
const WARM_WINDOW_MS = 500;
const AUTO_HIDE_MS = 5e3;
const OFFSET_PX = 6;
const EDGE_PAD_PX = 4;
let tip = null;
let showTimer = null;
let autoHideTimer = null;
let anchor = null;
let suppressed = null;
let warmUntil = 0;
function ensureTip(doc) {
  if (tip && tip.el.isConnected) return tip;
  const el = doc.createElement("div");
  el.className = "ui-screentip";
  el.setAttribute("role", "tooltip");
  const name = doc.createElement("span");
  name.className = "ui-screentip-name";
  const kbd = doc.createElement("span");
  kbd.className = "ui-screentip-kbd";
  const detail = doc.createElement("div");
  detail.className = "ui-screentip-detail";
  el.append(name, kbd, detail);
  doc.body.appendChild(el);
  tip = { el, name, kbd, detail };
  return tip;
}
function cancelShow() {
  if (showTimer !== null) {
    window.clearTimeout(showTimer);
    showTimer = null;
  }
}
function hide() {
  cancelShow();
  if (autoHideTimer !== null) {
    window.clearTimeout(autoHideTimer);
    autoHideTimer = null;
  }
  if (tip && tip.el.style.visibility === "visible") {
    tip.el.style.visibility = "hidden";
    warmUntil = Date.now() + WARM_WINDOW_MS;
  }
  anchor = null;
}
function show(el, doc) {
  const text = el.getAttribute("data-tip");
  if (!text) return;
  const t = ensureTip(doc);
  t.name.textContent = text;
  const kbd = el.getAttribute("data-tip-kbd");
  t.kbd.textContent = kbd ?? "";
  t.kbd.style.display = kbd ? "" : "none";
  const detail = el.getAttribute("data-tip-detail");
  t.detail.textContent = detail ?? "";
  t.detail.style.display = detail ? "" : "none";
  t.el.style.visibility = "hidden";
  t.el.style.left = "0px";
  t.el.style.top = "0px";
  const anchorSel = el.getAttribute("data-tip-anchor");
  const rect = (anchorSel && el.querySelector(anchorSel) || el).getBoundingClientRect();
  const tipRect = t.el.getBoundingClientRect();
  const maxLeft = window.innerWidth - tipRect.width - EDGE_PAD_PX;
  let left;
  let top;
  if (el.getAttribute("data-tip-place") === "right") {
    left = rect.right + OFFSET_PX;
    if (left > maxLeft) left = Math.max(EDGE_PAD_PX, rect.left - tipRect.width - OFFSET_PX);
    top = Math.max(
      EDGE_PAD_PX,
      Math.min(
        rect.top + rect.height / 2 - tipRect.height / 2,
        window.innerHeight - tipRect.height - EDGE_PAD_PX
      )
    );
  } else {
    left = Math.max(EDGE_PAD_PX, Math.min(rect.left + rect.width / 2 - tipRect.width / 2, maxLeft));
    top = rect.bottom + OFFSET_PX;
    if (top + tipRect.height > window.innerHeight - EDGE_PAD_PX)
      top = rect.top - tipRect.height - OFFSET_PX;
  }
  t.el.style.left = `${Math.round(left)}px`;
  t.el.style.top = `${Math.round(top)}px`;
  t.el.style.visibility = "visible";
  if (autoHideTimer !== null) window.clearTimeout(autoHideTimer);
  autoHideTimer = window.setTimeout(hide, AUTO_HIDE_MS);
}
function installScreenTips(doc = document) {
  const onPointerOver = (e) => {
    const target = e.target instanceof Element ? e.target : null;
    const el = target?.closest("[data-tip]") ?? null;
    if (el === anchor) return;
    if (suppressed && suppressed !== el) suppressed = null;
    hide();
    if (!el || el === suppressed) return;
    anchor = el;
    const delay = Date.now() < warmUntil ? RESHOW_DELAY_MS : INITIAL_DELAY_MS;
    showTimer = window.setTimeout(() => {
      showTimer = null;
      if (anchor === el && el.isConnected) show(el, doc);
    }, delay);
  };
  const onPointerOut = (e) => {
    if (!e.relatedTarget) {
      suppressed = null;
      hide();
    }
  };
  const onPointerDown = () => {
    suppressed = anchor;
    hide();
    warmUntil = 0;
  };
  const onHide = () => hide();
  doc.addEventListener("pointerover", onPointerOver, true);
  doc.addEventListener("pointerout", onPointerOut, true);
  doc.addEventListener("pointerdown", onPointerDown, true);
  doc.addEventListener("scroll", onHide, true);
  window.addEventListener("blur", onHide);
  window.addEventListener("resize", onHide);
  return () => {
    doc.removeEventListener("pointerover", onPointerOver, true);
    doc.removeEventListener("pointerout", onPointerOut, true);
    doc.removeEventListener("pointerdown", onPointerDown, true);
    doc.removeEventListener("scroll", onHide, true);
    window.removeEventListener("blur", onHide);
    window.removeEventListener("resize", onHide);
    hide();
    tip?.el.remove();
    tip = null;
  };
}
function subscribeChromePressed(handler) {
  const w = window;
  for (const name of [
    "slidesApi",
    "desktopApi",
    "desktop",
    "pdfApi",
    "markdownApi",
    "aiOfficeTabs"
  ]) {
    const sub = w[name]?.onChromePressed;
    if (typeof sub === "function") return sub.call(w[name], handler);
  }
  return void 0;
}
let openPopovers = 0;
function bumpOpenPopovers(delta) {
  openPopovers = Math.max(0, openPopovers + delta);
  document.documentElement.classList.toggle("genoffice-popover-open", openPopovers > 0);
}
function installPopoverDismiss(close, options) {
  const inside = options?.inside;
  const onPress = (e) => {
    if (inside) {
      const target = e.target;
      if (target) {
        for (const root of inside()) if (root && root.contains(target)) return;
      }
    }
    close();
  };
  const onBlur = () => close();
  if (inside) window.addEventListener("pointerdown", onPress, true);
  else window.addEventListener("mousedown", onPress);
  window.addEventListener("blur", onBlur);
  const offChrome = subscribeChromePressed(close);
  bumpOpenPopovers(1);
  return () => {
    if (inside) window.removeEventListener("pointerdown", onPress, true);
    else window.removeEventListener("mousedown", onPress);
    window.removeEventListener("blur", onBlur);
    offChrome?.();
    bumpOpenPopovers(-1);
  };
}
function useDismissablePopover(open, close, options) {
  const closeRef = reactExports.useRef(close);
  closeRef.current = close;
  const insideRef = reactExports.useRef(options?.inside);
  insideRef.current = options?.inside;
  const guarded = options?.inside != null;
  reactExports.useEffect(() => {
    if (!open) return;
    return installPopoverDismiss(
      () => closeRef.current(),
      guarded ? { inside: () => insideRef.current?.() ?? [] } : void 0
    );
  }, [open, guarded]);
}
function Dropdown({
  value,
  options,
  onPick,
  className,
  ariaLabel,
  disabled,
  tip: tip2,
  ariaRequired,
  ariaInvalid
}) {
  const [open, setOpen] = reactExports.useState(false);
  const [active, setActive] = reactExports.useState(0);
  const popRef = reactExports.useRef(null);
  const wrapRef = reactExports.useRef(null);
  useDismissablePopover(open, () => setOpen(false), { inside: () => [wrapRef.current] });
  reactExports.useEffect(() => {
    if (!open) return;
    popRef.current?.querySelectorAll(".gs-dd-item")[active]?.scrollIntoView?.({ block: "nearest" });
  }, [open, active]);
  const current = options.find((o) => o.value === value);
  const openList = () => {
    const i = options.findIndex((o) => o.value === value);
    setActive(i < 0 ? 0 : i);
    setOpen(true);
  };
  const pick = (o) => {
    if (o.disabled) return;
    setOpen(false);
    onPick(o.value);
  };
  const onKeyDown = (e) => {
    if (!open) {
      if (e.key === "ArrowDown" || e.key === "ArrowUp" || e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        openList();
      }
      return;
    }
    if (e.key === "Escape") setOpen(false);
    else if (e.key === "ArrowDown") setActive((i) => Math.min(options.length - 1, i + 1));
    else if (e.key === "ArrowUp") setActive((i) => Math.max(0, i - 1));
    else if (e.key === "Home") setActive(0);
    else if (e.key === "End") setActive(options.length - 1);
    else if (e.key === "Enter" || e.key === " ") {
      const o = options[active];
      if (o) pick(o);
    } else return;
    e.preventDefault();
    e.stopPropagation();
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { ref: wrapRef, className: `gs-dd${className ? ` ${className}` : ""}`, children: [
    /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "button",
      {
        type: "button",
        className: "gs-dd-btn",
        disabled,
        "data-value": value,
        "data-tip": tip2,
        "aria-haspopup": "listbox",
        "aria-expanded": open,
        "aria-label": ariaLabel ?? current?.label ?? value,
        "aria-required": ariaRequired,
        "aria-invalid": ariaInvalid,
        onClick: () => open ? setOpen(false) : openList(),
        onKeyDown,
        onBlur: (e) => {
          if (!wrapRef.current?.contains(e.relatedTarget)) setOpen(false);
        },
        children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "gs-dd-value", children: current ? current.render ?? current.label : value }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "gs-dd-caret", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "11", height: "11", viewBox: "0 0 24 24", fill: "none", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
            "path",
            {
              d: "M5.5 9.25 12 15.75l6.5-6.5",
              stroke: "currentColor",
              strokeWidth: "2.6",
              strokeLinecap: "round",
              strokeLinejoin: "round"
            }
          ) }) })
        ]
      }
    ),
    open && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { ref: popRef, className: "gs-dd-pop", role: "listbox", children: options.map((o, i) => /* @__PURE__ */ jsxRuntimeExports.jsx(
      "button",
      {
        type: "button",
        role: "option",
        tabIndex: -1,
        disabled: o.disabled,
        "aria-selected": o.value === value,
        "aria-label": o.label,
        "data-value": o.value,
        title: o.label,
        className: `gs-dd-item${o.value === value ? " selected" : ""}${i === active && !o.disabled ? " active" : ""}`,
        onMouseEnter: () => setActive(i),
        onMouseDown: (e) => e.preventDefault(),
        onClick: () => pick(o),
        children: o.render ?? o.label
      },
      o.value
    )) })
  ] });
}
function visiblePageCount(page) {
  return page.total;
}
function fileCountKey(count) {
  return count === 1 ? "fileCountOne" : "fileCount";
}
async function buildDocUrl(doc) {
  let url = (doc.fileUrl ?? "").trim();
  if (!url) return null;
  try {
    const state = await window.aiOffice.authGetState?.();
    const serverUrl = state?.serverUrl?.trim();
    if (serverUrl) {
      const file = new URL(url.startsWith("http") ? url : `https://${url}`);
      const server = new URL(serverUrl);
      url = `${server.origin}${file.pathname}${file.search}`;
    }
  } catch {
  }
  return url;
}
const strings = {
  zh: {
    navCollapseSidebar: "折叠侧边栏",
    navExpandSidebar: "展开侧边栏",
    // Sidebar navigation
    navRecent: "最近",
    navStarred: "收藏",
    navCloud: "微语Projects",
    cloudSubtitle: "在网页端用微语AI 创建的项目。编辑在浏览器中继续——点击任意项目即可打开。",
    cloudSearchPlaceholder: "搜索 {n} 个项目…",
    cloudNoResults: "没有匹配的项目。",
    cloudGroupThisWeek: "本周",
    cloudGroupThisMonth: "本月",
    cloudSortLabel: "排序：{v}",
    cloudSortRecent: "最近",
    cloudSortOldest: "最早",
    cloudRefresh: "刷新",
    cloudUpload: "上传文档",
    cloudUploadPick: "选择要上传的文档",
    cloudLoginHint: "登录微语账号，查看你在网页端创建的项目。",
    cloudEmpty: "还没有网页端项目。",
    cloudError: "加载失败，请稍后重试。",
    cloudRetry: "重试",
    cloudLoadMore: "加载更多",
    cloudOpenInBrowser: "在浏览器中打开",
    cloudCopyLink: "复制链接",
    navTrash: "回收站",
    navTrashTip: "删除的文件在系统废纸篓中，可从那里还原",
    secQuickStart: "快速开始",
    secRecent: "最近使用",
    secStarred: "收藏",
    secProjectFiles: "项目文件",
    secActivity: "项目动态",
    colName: "名称",
    colLocation: "位置",
    colModified: "修改时间",
    colSize: "大小",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "打开本地文件",
    dropToOpenTitle: "松开以打开文件",
    greetMorning: "早上好",
    greetAfternoon: "下午好",
    greetEvening: "晚上好",
    greetAsk1: "今天想创建点什么？",
    greetAsk2: "准备好开始了吗？",
    greetAsk3: "有什么想做的吗？",
    greetAsk4: "今天从哪里开始？",
    greetAsk5: "想做点什么新的？",
    greetAsk6: "灵感来了吗？",
    // Filters
    filterAll: "全部",
    filterDocs: "文档",
    filterSheets: "表格",
    filterSlides: "演示",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "按类型筛选",
    // List / bulk actions
    fileCount: "{n} 个文件",
    fileCountOne: "{n} 个文件",
    selectedCount: "已选 {n} 项",
    selectAll: "全选",
    selectFile: "选择 {name}",
    removeFromList: "从列表中移除",
    missingFileTitle: "文件不可用",
    missingFileBody: "找不到“{name}”。它可能位于未连接的磁盘上，或已被移动或删除。要从列表中移除吗？",
    deleteFiles: "删除文件",
    cancel: "取消",
    star: "收藏",
    unstar: "取消收藏",
    moreActions: "更多操作",
    open: "打开",
    revealInFolder: "打开所在文件夹",
    copyPath: "复制路径",
    moveToProject: "移动到项目",
    rename: "重命名",
    duplicate: "创建副本",
    renameFailed: "重命名失败",
    emptyStarred: "还没有收藏 — 悬浮文件行并点击星标即可收藏。",
    emptyRecent: "暂无最近文件 — 新建或打开一个文件开始使用。",
    emptyFiltered: "该类型下暂无文件。",
    // Delete confirmation dialog
    deleteModalTitle: "删除文件",
    deleteConfirmOne: '确定将"{name}"移到废纸篓？',
    deleteConfirmMany: "确定将以下 {n} 个文件移到废纸篓？",
    deleteMoreCount: "… 等共 {n} 个",
    delete: "删除",
    // Project sidebar
    projects: "项目",
    newProject: "新建项目",
    projectName: "项目名称",
    deleteProject: "删除项目…",
    deleteProjectConfirm: '删除该项目？\n项目下的文件将回归"默认项目"，不会丢失。',
    projMoreActions: "{name} 更多操作",
    defaultProject: "默认项目",
    projEmpty: "把相关文档放进同一个项目，AI 对话历史会按项目沉淀。",
    projEmptyHint: '新建文件或从"最近文件"右键选择"移动到项目"。',
    timelineCount: "{n} 条",
    timelineCountOne: "{n} 条",
    timelineEmpty: "该项目还没有 AI 对话记录。",
    timelineYou: "你",
    timelineUserAria: "用户",
    untitled: "未命名",
    noContent: "（无内容）",
    // Account
    accountGenspark: "微语账号",
    account: "账号",
    login: "登录",
    loginGenspark: "登录微语账号",
    loggedIn: "已登录",
    loggedInGenspark: "已登录微语",
    waitingLogin: "等待浏览器登录…点击可重新拉起登录页",
    waitingShort: "等待登录…",
    loginTimeout: "登录超时,点击重试",
    loginLaunchFailed: "无法启动登录,点击重试",
    loginOpenManually: "浏览器没有打开?点此手动打开",
    loginOpenShort: "手动打开登录页",
    loginCopyUrl: "复制登录链接",
    loginCopied: "已复制",
    loginNetworkError: "无法连接微语,请检查网络或代理设置",
    loginExpired: "登录已过期,点击重试",
    loginFailed: "登录失败,点击重试",
    loginTitle: "登录微语",
    loginServer: "服务器地址",
    loginUsername: "账号",
    loginPassword: "密码",
    loginCaptcha: "验证码",
    loginCaptchaRefresh: "重新获取",
    loginFormIncomplete: "请填写服务器地址、账号和密码",
    loginTwoFactorUnsupported: "该账号开启了双重验证，暂不支持在此登录",
    loginBrandName: "微语文档",
    loginBrandDesc: "AI 驱动的文档套件，登录后同步团队文档",
    loginTabAccount: "账号密码登录",
    loginTabMobile: "手机号登录",
    loginMobile: "手机号",
    loginCountry: "国家/地区",
    loginSmsCode: "短信验证码",
    loginSmsGet: "获取验证码",
    loginSmsCountdown: "{s} 获取验证码",
    loginAutoRegisterHint: "未注册的手机号验证通过后将自动注册账号",
    loginErrCaptchaFirst: "请先输入正确的图形验证码",
    loginErrKaptchaLoad: "验证码加载失败，请检查服务器地址或点击刷新重试",
    loginErrCaptcha: "验证码错误",
    loginErrCredentials: "用户名或密码错误",
    loginErrForm: "请完整填写登录信息",
    loginErrMismatch: "用户名与手机号不匹配",
    loginErrSms: "短信验证码发送失败",
    loginErrDisabled: "账号已被管理员禁用，请联系管理员启用后再登录",
    loginErrServer: "服务器错误，请稍后重试",
    loginTwoFactorMissing: "请先完成短信验证再登录",
    loginTwoFactorMobile: "双因子手机号",
    loginMobileRequired: "手机号不能为空",
    loginMobileInvalid: "手机号格式错误！",
    loginPwStrong: "强",
    loginPwMedium: "中",
    loginPwWeak: "弱",
    loginPwVeryWeak: "很弱",
    loginPwWeakTitle: "密码强度提醒",
    loginPwWeakDesc: "您当前使用的密码强度较低，存在安全风险。",
    loginPwSuggestion: "建议：包含大小写字母、数字和特殊字符，长度至少8位",
    loginPwWeakConfirm: "是否仍要使用当前密码继续登录？",
    loginPwWeakCancel: "取消登录",
    loginPwWeakChange: "修改密码",
    loginPwWeakContinue: "继续登录",
    loginPrivacyAgree: "同意",
    loginPrivacyPolicy: "《用户隐私协议》",
    loginPrivacyAnd: "和",
    loginTermsOfService: "《服务条款》",
    loginPrivacyModalTitle: "隐私协议确认",
    loginPrivacyModalDesc: "为了保护您的个人信息安全，请您阅读并同意以下协议：",
    loginPrivacyModalConfirm: "是否同意以上协议并继续登录？",
    loginPrivacyAgreeAndLogin: "同意并登录",
    loginRegisterAccount: "注册账号",
    loginServerApply: "连接",
    loginServerSettings: "服务器设置",
    loginServerChange: "修改",
    loginCountryChina: "中国",
    loginCountryUSA: "美国",
    loginCountryJapan: "日本",
    loginCountryHongkong: "香港",
    loginCountryTaiwan: "台湾",
    loginCountryMacao: "澳门",
    loginCountryKorea: "韩国",
    loginCountryCanada: "加拿大",
    loginCountrySingapore: "新加坡",
    loginCountryMalaysia: "马来西亚",
    loginCountryThailand: "泰国",
    loginCountryVietnam: "越南",
    loginCountryPhilippines: "菲律宾",
    loginCountryIndonesia: "印度尼西亚",
    loginCountryUK: "英国",
    loginCountryGermany: "德国",
    loginCountryFrance: "法国",
    loginCountryItaly: "意大利",
    loginCountrySpain: "西班牙",
    loginCountryRussia: "俄罗斯",
    loginCountryAustralia: "澳大利亚",
    loginCountryNewzealand: "新西兰",
    loggingOut: "正在退出…",
    logout: "退出登录",
    credits: "积分",
    creditsTip: "查看积分用量详情",
    appVersion: "版本 {v}",
    versionLabel: "版本",
    updateChannel: "更新通道",
    channelStable: "稳定版",
    channelBeta: "Beta 版",
    theme: "主题",
    themeLight: "浅色",
    themeDark: "深色",
    themeSystem: "跟随系统",
    saveLocation: "默认保存位置",
    setAnalytics: "发送匿名使用统计",
    setAnalyticsDesc: "该功能默认开启，可随时在“设置 → 常规”中关闭。使用 Google Analytics 4；Google 会接收您的公网 IP 地址和传输元数据，但绝不收集文档内容或文件名。",
    settings: "设置",
    setSecAccount: "账户",
    setSecGeneral: "通用",
    setSecAbout: "关于",
    setSecAiModel: "AI 模型",
    setAiProvider: "模型服务商",
    setAiModelId: "模型",
    setAiApiKey: "API Key",
    setAiKeyHint: "密钥仅保存在本机。",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "留空使用官方端点。",
    setAiGensparkHint: "使用微语账号登录，无需 API key。",
    setAiServerUrl: "服务器地址",
    setAiServerUrlHint: "微语服务器地址，登录后自动同步",
    setAiModelOptional: "留空由服务器决定",
    setAiByokNote: "对话使用你自己的 key；网页搜索、生图等云工具仍需登录微语。",
    setAiSave: "保存",
    setAiSaved: "已保存",
    setAiTest: "测试连接",
    setAiTesting: "测试中…",
    setAiTestOk: "连接成功",
    setAiTestFail: "连接失败",
    setAiMaxTokens: "单次输出上限（tokens）",
    setAiMaxTokensDesc: "一次回合的输出预算。推理模型会先消耗预算用于思考，预算用完时回复可能变成空白，遇到这种情况请调大此项。",
    setAiGskTools: "微语云工具",
    setAiGskToolsDesc: "网页搜索、AI 生图、媒体分析经微语云端并消耗积分；关闭后生图与媒体分析不可用，搜索改用免费来源。",
    setGithub: "开源项目",
    starOnGitHub: "去 GitHub 点 Star",
    starPromptTitle: "喜欢微语文档吗？",
    starPromptTitleN: "你已经用微语文档打开了 {n} 个文档",
    starPromptBody: "微语文档是免费开源软件。到 GitHub 给我们一个 Star，是对团队最好的支持。",
    starPromptGo: "去点 Star",
    starPromptDone: "已经点过了",
    starPromptLater: "以后再说",
    onbStarHint: "如果你喜欢微语文档，欢迎到 GitHub 给我们一个 Star。",
    setEmail: "邮箱",
    setServer: "微语服务器",
    setNotLoggedIn: "未登录",
    setViewUsage: "查看用量",
    setChange: "更改",
    // Dates
    today: "今天",
    yesterday: "昨天",
    daysAgo: "{n}天前",
    // Language / tab strip
    language: "语言",
    closeTab: "关闭标签",
    tabSync: "同步",
    tabSyncing: "同步中…",
    tabSyncDone: "已同步",
    tabSyncFailed: "同步失败",
    tabSyncNeedLogin: "请先登录",
    tabShare: "分享",
    tabShareDone: "链接已复制",
    tabList: "全部标签",
    newTab: "新建标签页",
    // First-run onboarding
    onbTitle1: "欢迎使用微语文档",
    onbSubtitle1: "第一个开源的 AI 原生 Office 套件",
    onbBody1: "创建文档、制作表格、生成演示、审阅 PDF。AI 深度融入每个环节。",
    onbTitle2: "这只是一个开始",
    onbBody2: "微语文档目前处于 alpha 阶段。欢迎加入微语社区群聊，分享反馈，一起塑造它的未来。",
    onbCredits: "活跃贡献者可获得 **1,000+微语积分**",
    onbJoinGenTeam: "加入微语社区",
    onbSkip: "跳过",
    onbNext: "下一步",
    onbStart: "开始使用",
    onbStepAria: "第 {n} 页，共 {total} 页",
    onbTitle3: "人人免费",
    onbBody3: "无授权费用，无广告，无水印。",
    onbNote3: "AI 功能可能消耗微语积分。",
    onbBack: "上一步"
  },
  en: {
    navCollapseSidebar: "Collapse sidebar",
    navExpandSidebar: "Expand sidebar",
    navRecent: "Recent",
    navStarred: "Starred",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Projects created on the web with Bytedesk AI. Editing continues in your browser — click any project to open it.",
    cloudSearchPlaceholder: "Search {n} projects…",
    cloudNoResults: "No matching projects.",
    cloudGroupThisWeek: "This week",
    cloudGroupThisMonth: "Earlier this month",
    cloudSortLabel: "Sort: {v}",
    cloudSortRecent: "Recent",
    cloudSortOldest: "Oldest",
    cloudRefresh: "Refresh",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Sign in to your Bytedesk account to see projects you created on the web.",
    cloudEmpty: "No web projects yet.",
    cloudError: "Failed to load. Try again later.",
    cloudRetry: "Retry",
    cloudLoadMore: "Load more",
    cloudOpenInBrowser: "Open in browser",
    cloudCopyLink: "Copy link",
    navTrash: "Trash",
    navTrashTip: "Deleted files go to the system Trash and can be restored there",
    secQuickStart: "Quick start",
    secRecent: "Recent",
    secStarred: "Starred",
    secProjectFiles: "Project files",
    secActivity: "Activity",
    colName: "Name",
    colLocation: "Location",
    colModified: "Modified",
    colSize: "Size",
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Open Local File",
    dropToOpenTitle: "Drop to open",
    greetMorning: "Good morning",
    greetAfternoon: "Good afternoon",
    greetEvening: "Good evening",
    greetAsk1: "What would you like to create today?",
    greetAsk2: "Ready to get started?",
    greetAsk3: "What are you working on?",
    greetAsk4: "Where shall we begin today?",
    greetAsk5: "Feeling creative?",
    greetAsk6: "What's next on your mind?",
    filterAll: "All",
    filterDocs: "Docs",
    filterSheets: "Sheets",
    filterSlides: "Slides",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Filter by type",
    fileCount: "{n} files",
    fileCountOne: "{n} file",
    selectedCount: "{n} selected",
    selectAll: "Select all",
    selectFile: "Select {name}",
    removeFromList: "Remove from list",
    missingFileTitle: "File unavailable",
    missingFileBody: "“{name}” can’t be found. It may be on a disconnected drive, or it was moved or deleted. Remove it from the list?",
    deleteFiles: "Delete files",
    cancel: "Cancel",
    star: "Star",
    unstar: "Unstar",
    moreActions: "More actions",
    open: "Open",
    revealInFolder: "Reveal in folder",
    copyPath: "Copy path",
    moveToProject: "Move to project",
    rename: "Rename",
    duplicate: "Duplicate",
    renameFailed: "Rename failed",
    emptyStarred: "No starred files — hover a row and click the star.",
    emptyRecent: "No recent files — create or open a file to get started.",
    emptyFiltered: "No files of this type.",
    deleteModalTitle: "Delete Files",
    deleteConfirmOne: 'Move "{name}" to the Trash?',
    deleteConfirmMany: "Move these {n} files to the Trash?",
    deleteMoreCount: "… {n} in total",
    delete: "Delete",
    projects: "Projects",
    newProject: "New project",
    projectName: "Project name",
    deleteProject: "Delete project…",
    deleteProjectConfirm: "Delete this project?\nIts files will return to the Default project and won't be lost.",
    projMoreActions: "More actions for {name}",
    defaultProject: "Default project",
    projEmpty: "Keep related documents in one project — AI chat history accumulates per project.",
    projEmptyHint: 'Create a file, or right-click a recent file and choose "Move to project".',
    timelineCount: "{n} items",
    timelineCountOne: "{n} item",
    timelineEmpty: "No AI conversations in this project yet.",
    timelineYou: "You",
    timelineUserAria: "User",
    untitled: "Untitled",
    noContent: "(empty)",
    accountGenspark: "Bytedesk Account",
    account: "Account",
    login: "Sign in",
    loginGenspark: "Sign in with Bytedesk",
    loggedIn: "Signed in",
    loggedInGenspark: "Signed in to Bytedesk",
    waitingLogin: "Waiting for browser sign-in… Click to relaunch the sign-in page",
    loginTimeout: "Sign-in timed out — click to retry",
    loginLaunchFailed: "Could not start sign-in — click to retry",
    loginOpenManually: "Browser did not open? Click to open manually",
    loginOpenShort: "Open sign-in page manually",
    loginCopyUrl: "Copy sign-in link",
    loginCopied: "Copied",
    loginNetworkError: "Cannot reach Bytedesk — check your network or proxy settings",
    loginExpired: "Sign-in expired — click to retry",
    loginFailed: "Sign-in failed — click to retry",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    waitingShort: "Waiting…",
    loggingOut: "Signing out…",
    logout: "Sign out",
    credits: "Credits",
    creditsTip: "View credit usage details",
    appVersion: "Version {v}",
    versionLabel: "Version",
    updateChannel: "Update Channel",
    channelStable: "Stable",
    channelBeta: "Beta",
    theme: "Theme",
    themeLight: "Light",
    themeDark: "Dark",
    themeSystem: "Follow System",
    saveLocation: "Save Location",
    setAnalytics: "Send anonymous usage statistics",
    setAnalyticsDesc: "Enabled by default and can be turned off anytime in Settings → General. Uses Google Analytics 4; Google receives your public IP address and transport metadata, but document contents and file names are never collected.",
    settings: "Settings",
    setSecAccount: "Account",
    setSecGeneral: "General",
    setSecAbout: "About",
    setSecAiModel: "AI Model",
    setAiProvider: "Provider",
    setAiModelId: "Model",
    setAiApiKey: "API Key",
    setAiKeyHint: "Stored only on this device.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Leave empty for the official endpoint.",
    setAiGensparkHint: "Uses your Bytedesk sign-in; no API key needed.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Chats use your own key; cloud tools (web search, image generation) still require the Bytedesk sign-in.",
    setAiSave: "Save",
    setAiSaved: "Saved",
    setAiTest: "Test connection",
    setAiTesting: "Testing…",
    setAiTestOk: "Connection OK",
    setAiTestFail: "Connection failed",
    setAiMaxTokens: "Max output tokens",
    setAiMaxTokensDesc: "Output budget for one turn. Reasoning models spend part of it thinking, so an answer can come back empty once the budget runs out; raise this value if that happens.",
    setAiGskTools: "Bytedesk cloud tools",
    setAiGskToolsDesc: "Web search, AI image generation and media analysis run through Bytedesk and use credits; when off, image tools are unavailable and search uses free sources.",
    setGithub: "Open Source",
    starOnGitHub: "Star on GitHub",
    starPromptTitle: "Enjoying Bytedesk Office?",
    starPromptTitleN: "You've opened {n} documents with Bytedesk Office",
    starPromptBody: "Bytedesk Office is free and open source. A star on GitHub is the best way to support the team.",
    starPromptGo: "Star on GitHub",
    starPromptDone: "Already starred",
    starPromptLater: "Maybe later",
    onbStarHint: "If you like Bytedesk Office, give us a star on GitHub.",
    setEmail: "Email",
    setServer: "Server",
    setNotLoggedIn: "Not signed in",
    setViewUsage: "View usage",
    setChange: "Change",
    today: "Today",
    yesterday: "Yesterday",
    daysAgo: "{n}d ago",
    language: "Language",
    closeTab: "Close tab",
    tabSync: "Sync",
    tabSyncing: "Syncing…",
    tabSyncDone: "Synced",
    tabSyncFailed: "Sync failed",
    tabSyncNeedLogin: "Sign in first",
    tabShare: "Share",
    tabShareDone: "Link copied",
    tabList: "All tabs",
    newTab: "New tab",
    // First-run onboarding
    onbTitle1: "Welcome to Bytedesk Office",
    onbSubtitle1: "The first open-source, AI-native office suite",
    onbBody1: "Create docs, build sheets, make slides, and review PDFs. AI is built into every step.",
    onbTitle2: "This is just the beginning",
    onbBody2: "Bytedesk Office is still in alpha. Join the group chat on Bytedesk Community to share feedback and help shape what comes next.",
    onbCredits: "Active contributors get **1,000+ Bytedesk credits**",
    onbJoinGenTeam: "Join Bytedesk Community",
    onbSkip: "Skip",
    onbNext: "Next",
    onbStart: "Get started",
    onbStepAria: "Page {n} of {total}",
    onbTitle3: "Free for everyone",
    onbBody3: "No license fees. No ads. No watermarks.",
    onbNote3: "AI features may consume Bytedesk credits.",
    onbBack: "Back"
  },
  ja: {
    navCollapseSidebar: "サイドバーを折りたたむ",
    navExpandSidebar: "サイドバーを展開",
    // Sidebar navigation
    navRecent: "最近使用",
    navStarred: "お気に入り",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Web で Bytedesk AI を使って作成したプロジェクト。編集はブラウザで続行します。クリックで開きます。",
    cloudSearchPlaceholder: "{n} 件のプロジェクトを検索…",
    cloudNoResults: "一致するプロジェクトはありません。",
    cloudGroupThisWeek: "今週",
    cloudGroupThisMonth: "今月",
    cloudSortLabel: "並び替え: {v}",
    cloudSortRecent: "新しい順",
    cloudSortOldest: "古い順",
    cloudRefresh: "更新",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Bytedesk アカウントにサインインすると、Web で作成したプロジェクトを表示できます。",
    cloudEmpty: "Web のプロジェクトはまだありません。",
    cloudError: "読み込みに失敗しました。後でもう一度お試しください。",
    cloudRetry: "再試行",
    cloudLoadMore: "もっと見る",
    cloudOpenInBrowser: "ブラウザで開く",
    cloudCopyLink: "リンクをコピー",
    navTrash: "ゴミ箱",
    navTrashTip: "削除したファイルはシステムのゴミ箱に移動され、そこから復元できます",
    // Section headings
    secQuickStart: "クイックスタート",
    secRecent: "最近使用",
    secStarred: "お気に入り",
    secProjectFiles: "プロジェクトファイル",
    secActivity: "アクティビティ",
    colName: "名前",
    colLocation: "場所",
    colModified: "更新日時",
    colSize: "サイズ",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "ローカルファイルを開く",
    dropToOpenTitle: "ドロップして開く",
    greetMorning: "おはようございます",
    greetAfternoon: "こんにちは",
    greetEvening: "こんばんは",
    greetAsk1: "今日は何を作りますか？",
    greetAsk2: "準備はできましたか？",
    greetAsk3: "何か作りたいものはありますか？",
    greetAsk4: "今日はどこから始めますか？",
    greetAsk5: "新しいことに挑戦しますか？",
    greetAsk6: "インスピレーションは湧きましたか？",
    // Filters
    filterAll: "すべて",
    filterDocs: "文書",
    filterSheets: "スプレッドシート",
    filterSlides: "プレゼンテーション",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "種類で絞り込み",
    // List / bulk actions
    fileCount: "{n} 個のファイル",
    fileCountOne: "{n} 個のファイル",
    selectedCount: "{n} 件選択中",
    selectAll: "すべて選択",
    selectFile: "{name} を選択",
    removeFromList: "リストから削除",
    missingFileTitle: "ファイルを利用できません",
    missingFileBody: "「{name}」が見つかりません。接続されていないドライブにあるか、移動または削除された可能性があります。リストから削除しますか？",
    deleteFiles: "ファイルを削除",
    cancel: "キャンセル",
    star: "お気に入りに追加",
    unstar: "お気に入りから削除",
    moreActions: "その他の操作",
    open: "開く",
    revealInFolder: "フォルダーで表示",
    copyPath: "パスをコピー",
    moveToProject: "プロジェクトに移動",
    rename: "名前の変更",
    duplicate: "複製を作成",
    renameFailed: "名前の変更に失敗しました",
    emptyStarred: "お気に入りはまだありません — ファイルの行にカーソルを合わせて星マークをクリックしてください。",
    emptyRecent: "最近使ったファイルはありません — 新規作成するかファイルを開いて始めましょう。",
    emptyFiltered: "この種類のファイルはありません。",
    // Delete confirmation dialog
    deleteModalTitle: "ファイルの削除",
    deleteConfirmOne: "「{name}」をゴミ箱に移動しますか？",
    deleteConfirmMany: "次の {n} 個のファイルをゴミ箱に移動しますか？",
    deleteMoreCount: "… ほか、計 {n} 個",
    delete: "削除",
    // Project sidebar
    projects: "プロジェクト",
    newProject: "新規プロジェクト",
    projectName: "プロジェクト名",
    deleteProject: "プロジェクトを削除…",
    deleteProjectConfirm: "このプロジェクトを削除しますか？\nプロジェクト内のファイルは「既定のプロジェクト」に戻り、失われることはありません。",
    projMoreActions: "{name} のその他の操作",
    defaultProject: "既定のプロジェクト",
    projEmpty: "関連するドキュメントを同じプロジェクトにまとめると、AI との会話履歴がプロジェクトごとに蓄積されます。",
    projEmptyHint: "新規ファイルを作成するか、「最近使用」のファイルを右クリックして「プロジェクトに移動」を選択してください。",
    timelineCount: "{n} 件",
    timelineCountOne: "{n} 件",
    timelineEmpty: "このプロジェクトにはまだ AI との会話履歴がありません。",
    timelineYou: "あなた",
    timelineUserAria: "ユーザー",
    untitled: "無題",
    noContent: "（内容なし）",
    // Account
    accountGenspark: "Bytedesk アカウント",
    account: "アカウント",
    login: "サインイン",
    loginGenspark: "Bytedesk アカウントでサインイン",
    loggedIn: "サインイン済み",
    loggedInGenspark: "Bytedesk にサインイン済み",
    waitingLogin: "ブラウザーでのサインインを待っています… クリックするとサインインページを再表示します",
    waitingShort: "サインイン待ち…",
    loginTimeout: "サインインがタイムアウトしました。クリックして再試行",
    loginLaunchFailed: "サインインを開始できませんでした。クリックして再試行",
    loginOpenManually: "ブラウザが開かない場合はこちらをクリック",
    loginOpenShort: "ログインページを手動で開く",
    loginCopyUrl: "ログインリンクをコピー",
    loginCopied: "コピーしました",
    loginNetworkError: "Bytedesk に接続できません。ネットワークまたはプロキシ設定を確認してください",
    loginExpired: "サインインの有効期限が切れました。クリックして再試行",
    loginFailed: "サインインに失敗しました。クリックして再試行",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "ユーザー名",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "キャプチャの読み込みに失敗しました。サーバーアドレスを確認するか、更新をクリックしてください",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "サーバー設定",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "サインアウトしています…",
    logout: "サインアウト",
    credits: "クレジット",
    creditsTip: "クレジット使用状況を確認",
    appVersion: "バージョン {v}",
    versionLabel: "バージョン",
    updateChannel: "更新チャネル",
    channelStable: "安定版",
    channelBeta: "ベータ版",
    theme: "テーマ",
    themeLight: "ライト",
    themeDark: "ダーク",
    themeSystem: "システムに従う",
    saveLocation: "保存先",
    setAnalytics: "匿名の使用状況統計を送信",
    setAnalyticsDesc: "既定で有効です。設定 → 一般でいつでも無効にできます。Google Analytics 4 を使用し、Google は公開 IP アドレスと通信メタデータを受け取りますが、文書の内容やファイル名は収集されません。",
    settings: "設定",
    setSecAccount: "アカウント",
    setSecGeneral: "一般",
    setSecAbout: "情報",
    setSecAiModel: "AIモデル",
    setAiProvider: "プロバイダー",
    setAiModelId: "モデル",
    setAiApiKey: "APIキー",
    setAiKeyHint: "キーはこの端末にのみ保存されます。",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "空欄で公式エンドポイントを使用します。",
    setAiGensparkHint: "Bytedesk アカウントでサインインするため、API キーは不要です。",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "チャットは自分のキーを使用します。ウェブ検索や画像生成などのクラウドツールには Bytedesk へのサインインが必要です。",
    setAiSave: "保存",
    setAiSaved: "保存しました",
    setAiTest: "接続テスト",
    setAiTesting: "テスト中…",
    setAiTestOk: "接続に成功しました",
    setAiTestFail: "接続に失敗しました",
    setAiMaxTokens: "1 回あたりの出力トークン上限",
    setAiMaxTokensDesc: "1 ターンの出力予算です。推論モデルは思考に消費するため、耗尽すると返信が空になります。その場合は値を大きくしてください。",
    setAiGskTools: "Bytedesk クラウドツール",
    setAiGskToolsDesc: "ウェブ検索・AI 画像生成・メディア分析は Bytedesk クラウド経由でクレジットを消費します。オフにすると画像ツールは使えず、検索は無料ソースを使用します。",
    setGithub: "オープンソース",
    starOnGitHub: "GitHub でスターを付ける",
    starPromptTitle: "Bytedesk Office はいかがですか？",
    starPromptTitleN: "Bytedesk Office で {n} 件のドキュメントを開きました",
    starPromptBody: "Bytedesk Office は無料のオープンソースソフトウェアです。GitHub のスターがチームへの何よりの応援になります。",
    starPromptGo: "スターを付ける",
    starPromptDone: "スター済み",
    starPromptLater: "あとで",
    onbStarHint: "Bytedesk Office が気に入ったら、GitHub でスターをお願いします。",
    setEmail: "メール",
    setServer: "Server",
    setNotLoggedIn: "未ログイン",
    setViewUsage: "使用状況を見る",
    setChange: "変更",
    // Dates
    today: "今日",
    yesterday: "昨日",
    daysAgo: "{n}日前",
    // Language / tab strip
    language: "言語",
    closeTab: "タブを閉じる",
    tabSync: "同期",
    tabSyncing: "同期中…",
    tabSyncDone: "同期済み",
    tabSyncFailed: "同期に失敗しました",
    tabSyncNeedLogin: "先にログインしてください",
    tabShare: "共有",
    tabShareDone: "リンクをコピーしました",
    tabList: "すべてのタブ",
    newTab: "新しいタブ",
    // First-run onboarding
    onbTitle1: "Bytedesk Office へようこそ",
    onbSubtitle1: "初のオープンソース AI ネイティブ Office スイート",
    onbBody1: "文書の作成、表計算、プレゼン作成、PDF のレビュー。あらゆるステップに AI が組み込まれています。",
    onbTitle2: "これはまだ始まりにすぎません",
    onbBody2: "Bytedesk Office はまだアルファ版です。Bytedesk Community のグループチャットに参加して、フィードバックを共有し、今後の開発を一緒に形作りましょう。",
    onbCredits: "アクティブな貢献者への特典 **1,000+ Bytedesk クレジット**",
    onbJoinGenTeam: "Bytedesk Community に参加",
    onbSkip: "スキップ",
    onbNext: "次へ",
    onbStart: "はじめる",
    onbStepAria: "{total} ページ中 {n} ページ目",
    onbTitle3: "すべての人に無料",
    onbBody3: "ライセンス料なし、広告なし、透かしなし。",
    onbNote3: "AI 機能は Bytedesk クレジットを消費する場合があります。",
    onbBack: "戻る"
  },
  ko: {
    navCollapseSidebar: "사이드바 접기",
    navExpandSidebar: "사이드바 펼치기",
    // Sidebar navigation
    navRecent: "최근 사용",
    navStarred: "즐겨찾기",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Bytedesk AI로 웹에서 만든 프로젝트입니다. 편집은 브라우저에서 계속됩니다. 프로젝트를 클릭하면 열립니다.",
    cloudSearchPlaceholder: "프로젝트 {n}개 검색…",
    cloudNoResults: "일치하는 프로젝트가 없습니다.",
    cloudGroupThisWeek: "이번 주",
    cloudGroupThisMonth: "이번 달",
    cloudSortLabel: "정렬: {v}",
    cloudSortRecent: "최신순",
    cloudSortOldest: "오래된순",
    cloudRefresh: "새로고침",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Bytedesk 계정에 로그인하면 웹에서 만든 프로젝트를 볼 수 있습니다.",
    cloudEmpty: "아직 웹 프로젝트가 없습니다.",
    cloudError: "불러오지 못했습니다. 나중에 다시 시도해 주세요.",
    cloudRetry: "다시 시도",
    cloudLoadMore: "더 보기",
    cloudOpenInBrowser: "브라우저에서 열기",
    cloudCopyLink: "링크 복사",
    navTrash: "휴지통",
    navTrashTip: "삭제된 파일은 시스템 휴지통으로 이동되며 그곳에서 복원할 수 있습니다",
    // Section headings
    secQuickStart: "빠른 시작",
    secRecent: "최근 사용",
    secStarred: "즐겨찾기",
    secProjectFiles: "프로젝트 파일",
    secActivity: "활동",
    colName: "이름",
    colLocation: "위치",
    colModified: "수정한 날짜",
    colSize: "크기",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "로컬 파일 열기",
    dropToOpenTitle: "놓아서 파일 열기",
    greetMorning: "좋은 아침이에요",
    greetAfternoon: "안녕하세요",
    greetEvening: "좋은 저녁이에요",
    greetAsk1: "오늘은 무엇을 만들까요?",
    greetAsk2: "시작할 준비가 되셨나요?",
    greetAsk3: "무엇을 하고 싶으신가요?",
    greetAsk4: "오늘 어디서부터 시작할까요?",
    greetAsk5: "새로운 걸 만들어 볼까요?",
    greetAsk6: "아이디어가 떠오르셨나요?",
    // Filters
    filterAll: "전체",
    filterDocs: "문서",
    filterSheets: "스프레드시트",
    filterSlides: "프레젠테이션",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "유형별 필터",
    // List / bulk actions
    fileCount: "파일 {n}개",
    fileCountOne: "파일 {n}개",
    selectedCount: "{n}개 선택됨",
    selectAll: "모두 선택",
    selectFile: "{name} 선택",
    removeFromList: "목록에서 제거",
    missingFileTitle: "파일을 사용할 수 없음",
    missingFileBody: "“{name}”을(를) 찾을 수 없습니다. 연결되지 않은 드라이브에 있거나 이동 또는 삭제되었을 수 있습니다. 목록에서 제거할까요?",
    deleteFiles: "파일 삭제",
    cancel: "취소",
    star: "즐겨찾기에 추가",
    unstar: "즐겨찾기에서 제거",
    moreActions: "기타 작업",
    open: "열기",
    revealInFolder: "폴더에서 보기",
    copyPath: "경로 복사",
    moveToProject: "프로젝트로 이동",
    rename: "이름 바꾸기",
    duplicate: "복사본 만들기",
    renameFailed: "이름 바꾸기에 실패했습니다",
    emptyStarred: "즐겨찾기가 없습니다 — 파일 행에 마우스를 올리고 별표를 클릭하세요.",
    emptyRecent: "최근 파일이 없습니다 — 새로 만들거나 파일을 열어 시작하세요.",
    emptyFiltered: "이 유형의 파일이 없습니다.",
    // Delete confirmation dialog
    deleteModalTitle: "파일 삭제",
    deleteConfirmOne: '"{name}"을(를) 휴지통으로 이동하시겠습니까?',
    deleteConfirmMany: "다음 {n}개 파일을 휴지통으로 이동하시겠습니까?",
    deleteMoreCount: "… 외 총 {n}개",
    delete: "삭제",
    // Project sidebar
    projects: "프로젝트",
    newProject: "새 프로젝트",
    projectName: "프로젝트 이름",
    deleteProject: "프로젝트 삭제…",
    deleteProjectConfirm: '이 프로젝트를 삭제하시겠습니까?\n프로젝트의 파일은 "기본 프로젝트"로 돌아가며 사라지지 않습니다.',
    projMoreActions: "{name}의 기타 작업",
    defaultProject: "기본 프로젝트",
    projEmpty: "관련 문서를 하나의 프로젝트에 모아 두면 AI 대화 기록이 프로젝트별로 쌓입니다.",
    projEmptyHint: '새 파일을 만들거나 "최근 사용"에서 파일을 마우스 오른쪽 버튼으로 클릭해 "프로젝트로 이동"을 선택하세요.',
    timelineCount: "{n}건",
    timelineCountOne: "{n}건",
    timelineEmpty: "이 프로젝트에는 아직 AI 대화 기록이 없습니다.",
    timelineYou: "나",
    timelineUserAria: "사용자",
    untitled: "제목 없음",
    noContent: "(내용 없음)",
    // Account
    accountGenspark: "Bytedesk 계정",
    account: "계정",
    login: "로그인",
    loginGenspark: "Bytedesk 계정으로 로그인",
    loggedIn: "로그인됨",
    loggedInGenspark: "Bytedesk에 로그인됨",
    waitingLogin: "브라우저 로그인을 기다리는 중… 클릭하면 로그인 페이지를 다시 엽니다",
    waitingShort: "로그인 대기 중…",
    loginTimeout: "로그인 시간이 초과되었습니다. 클릭하여 다시 시도",
    loginLaunchFailed: "로그인을 시작할 수 없습니다. 클릭하여 다시 시도",
    loginOpenManually: "브라우저가 열리지 않았나요? 여기를 클릭해 직접 열기",
    loginOpenShort: "로그인 페이지 직접 열기",
    loginCopyUrl: "로그인 링크 복사",
    loginCopied: "복사됨",
    loginNetworkError: "Bytedesk에 연결할 수 없습니다. 네트워크 또는 프록시 설정을 확인하세요",
    loginExpired: "로그인이 만료되었습니다. 클릭하여 다시 시도",
    loginFailed: "로그인에 실패했습니다. 클릭하여 다시 시도",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "사용자 이름",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "캡차 로드에 실패했습니다. 서버 주소를 확인하거나 새로고침을 클릭하세요",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "서버 설정",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "로그아웃 중…",
    logout: "로그아웃",
    credits: "크레딧",
    creditsTip: "크레딧 사용 내역 보기",
    appVersion: "버전 {v}",
    versionLabel: "버전",
    updateChannel: "업데이트 채널",
    channelStable: "안정 버전",
    channelBeta: "베타 버전",
    theme: "테마",
    themeLight: "라이트",
    themeDark: "다크",
    themeSystem: "시스템 설정 따르기",
    saveLocation: "저장 위치",
    setAnalytics: "익명 사용 통계 보내기",
    setAnalyticsDesc: "기본적으로 켜져 있으며 설정 → 일반에서 언제든 끌 수 있습니다. Google Analytics 4를 사용하며 Google은 공인 IP 주소와 전송 메타데이터를 수신하지만 문서 내용이나 파일 이름은 수집하지 않습니다.",
    settings: "설정",
    setSecAccount: "계정",
    setSecGeneral: "일반",
    setSecAbout: "정보",
    setSecAiModel: "AI 모델",
    setAiProvider: "제공업체",
    setAiModelId: "모델",
    setAiApiKey: "API 키",
    setAiKeyHint: "키는 이 기기에만 저장됩니다.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "비워 두면 공식 엔드포인트를 사용합니다.",
    setAiGensparkHint: "Bytedesk 로그인으로 사용하며 API 키가 필요 없습니다.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "대화는 자신의 키를 사용합니다. 웹 검색·이미지 생성 등 클라우드 도구는 여전히 Bytedesk 로그인이 필요합니다.",
    setAiSave: "저장",
    setAiSaved: "저장됨",
    setAiTest: "연결 테스트",
    setAiTesting: "테스트 중…",
    setAiTestOk: "연결 성공",
    setAiTestFail: "연결 실패",
    setAiMaxTokens: "턴당 출력 토큰 상한",
    setAiMaxTokensDesc: "한 턴의 출력 예산입니다. 추론 모델은 생각하는 데 소모하므로 예산이 떨어지면 응답이 비어 올 수 있습니다. 그럴 때 값을 키우세요.",
    setAiGskTools: "Bytedesk 클라우드 도구",
    setAiGskToolsDesc: "웹 검색·AI 이미지 생성·미디어 분석은 Bytedesk 클라우드를 거치며 크레딧을 사용합니다. 끄면 이미지 도구를 사용할 수 없고 검색은 무료 소스를 사용합니다.",
    setGithub: "오픈 소스",
    starOnGitHub: "GitHub에서 스타 누르기",
    starPromptTitle: "Bytedesk Office가 마음에 드시나요?",
    starPromptTitleN: "Bytedesk Office로 문서를 {n}개 열어보셨네요",
    starPromptBody: "Bytedesk Office는 무료 오픈 소스 소프트웨어입니다. GitHub 스타 하나가 팀에게 가장 큰 응원이 됩니다.",
    starPromptGo: "스타 누르기",
    starPromptDone: "이미 눌렀어요",
    starPromptLater: "나중에",
    onbStarHint: "Bytedesk Office가 마음에 들면 GitHub에서 스타를 눌러 주세요.",
    setEmail: "이메일",
    setServer: "Server",
    setNotLoggedIn: "로그인되지 않음",
    setViewUsage: "사용량 보기",
    setChange: "변경",
    // Dates
    today: "오늘",
    yesterday: "어제",
    daysAgo: "{n}일 전",
    // Language / tab strip
    language: "언어",
    closeTab: "탭 닫기",
    tabSync: "동기화",
    tabSyncing: "동기화 중…",
    tabSyncDone: "동기화됨",
    tabSyncFailed: "동기화 실패",
    tabSyncNeedLogin: "먼저 로그인하세요",
    tabShare: "공유",
    tabShareDone: "링크 복사됨",
    tabList: "모든 탭",
    newTab: "새 탭",
    // First-run onboarding
    onbTitle1: "Bytedesk Office에 오신 것을 환영합니다",
    onbSubtitle1: "최초의 오픈소스 AI 네이티브 오피스 제품군",
    onbBody1: "문서 작성, 스프레드시트 제작, 프레젠테이션 생성, PDF 검토. 모든 단계에 AI가 녹아 있습니다.",
    onbTitle2: "이제 시작일 뿐입니다",
    onbBody2: "Bytedesk Office는 아직 알파 단계입니다. Bytedesk Community 그룹 채팅에 참여해 피드백을 공유하고 앞으로의 방향을 함께 만들어 가세요.",
    onbCredits: "활발한 기여자를 위한 혜택 **1,000+ Bytedesk 크레딧**",
    onbJoinGenTeam: "Bytedesk Community 참여하기",
    onbSkip: "건너뛰기",
    onbNext: "다음",
    onbStart: "시작하기",
    onbStepAria: "총 {total}페이지 중 {n}페이지",
    onbTitle3: "모두에게 무료",
    onbBody3: "라이선스 비용 없음, 광고 없음, 워터마크 없음.",
    onbNote3: "AI 기능은 Bytedesk 크레딧을 소모할 수 있습니다.",
    onbBack: "이전"
  },
  fr: {
    navCollapseSidebar: "Réduire la barre latérale",
    navExpandSidebar: "Développer la barre latérale",
    // Sidebar navigation
    navRecent: "Récents",
    navStarred: "Favoris",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Projets créés sur le web avec Bytedesk AI. L'édition continue dans votre navigateur — cliquez sur un projet pour l'ouvrir.",
    cloudSearchPlaceholder: "Rechercher parmi {n} projets…",
    cloudNoResults: "Aucun projet correspondant.",
    cloudGroupThisWeek: "Cette semaine",
    cloudGroupThisMonth: "Plus tôt ce mois-ci",
    cloudSortLabel: "Tri : {v}",
    cloudSortRecent: "Récents",
    cloudSortOldest: "Plus anciens",
    cloudRefresh: "Actualiser",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Connectez-vous à votre compte Bytedesk pour voir les projets créés sur le web.",
    cloudEmpty: "Aucun projet web pour le moment.",
    cloudError: "Échec du chargement. Réessayez plus tard.",
    cloudRetry: "Réessayer",
    cloudLoadMore: "Charger plus",
    cloudOpenInBrowser: "Ouvrir dans le navigateur",
    cloudCopyLink: "Copier le lien",
    navTrash: "Corbeille",
    navTrashTip: "Les fichiers supprimés sont placés dans la corbeille du système et peuvent y être restaurés",
    // Section headings
    secQuickStart: "Démarrage rapide",
    secRecent: "Récents",
    secStarred: "Favoris",
    secProjectFiles: "Fichiers du projet",
    secActivity: "Activité",
    colName: "Nom",
    colLocation: "Emplacement",
    colModified: "Modifié le",
    colSize: "Taille",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Ouvrir un fichier local",
    dropToOpenTitle: "Déposer pour ouvrir",
    greetMorning: "Bonjour",
    greetAfternoon: "Bonjour",
    greetEvening: "Bonsoir",
    greetAsk1: "Que souhaitez-vous créer aujourd’hui ?",
    greetAsk2: "Prêt à commencer ?",
    greetAsk3: "Sur quoi travaillez-vous ?",
    greetAsk4: "Par où commencer aujourd’hui ?",
    greetAsk5: "Envie de créer quelque chose de nouveau ?",
    greetAsk6: "Une idée en tête ?",
    // Filters
    filterAll: "Tous",
    filterDocs: "Documents",
    filterSheets: "Feuilles de calcul",
    filterSlides: "Présentations",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Filtrer par type",
    // List / bulk actions
    fileCount: "{n} fichiers",
    fileCountOne: "{n} fichier",
    selectedCount: "{n} sélectionné(s)",
    selectAll: "Tout sélectionner",
    selectFile: "Sélectionner {name}",
    removeFromList: "Retirer de la liste",
    missingFileTitle: "Fichier indisponible",
    missingFileBody: "« {name} » est introuvable. Il se trouve peut-être sur un disque déconnecté, ou il a été déplacé ou supprimé. Le retirer de la liste ?",
    deleteFiles: "Supprimer les fichiers",
    cancel: "Annuler",
    star: "Ajouter aux favoris",
    unstar: "Retirer des favoris",
    moreActions: "Autres actions",
    open: "Ouvrir",
    revealInFolder: "Afficher dans le dossier",
    copyPath: "Copier le chemin",
    moveToProject: "Déplacer vers un projet",
    rename: "Renommer",
    duplicate: "Dupliquer",
    renameFailed: "Échec du renommage",
    emptyStarred: "Aucun favori — survolez une ligne et cliquez sur l'étoile.",
    emptyRecent: "Aucun fichier récent — créez ou ouvrez un fichier pour commencer.",
    emptyFiltered: "Aucun fichier de ce type.",
    // Delete confirmation dialog
    deleteModalTitle: "Supprimer les fichiers",
    deleteConfirmOne: "Déplacer « {name} » vers la corbeille ?",
    deleteConfirmMany: "Déplacer ces {n} fichiers vers la corbeille ?",
    deleteMoreCount: "… {n} au total",
    delete: "Supprimer",
    // Project sidebar
    projects: "Projets",
    newProject: "Nouveau projet",
    projectName: "Nom du projet",
    deleteProject: "Supprimer le projet…",
    deleteProjectConfirm: "Supprimer ce projet ?\nSes fichiers retourneront dans le projet par défaut et ne seront pas perdus.",
    projMoreActions: "Autres actions pour {name}",
    defaultProject: "Projet par défaut",
    projEmpty: "Regroupez les documents associés dans un même projet — l'historique des conversations IA s'accumule par projet.",
    projEmptyHint: "Créez un fichier, ou cliquez avec le bouton droit sur un fichier récent et choisissez « Déplacer vers un projet ».",
    timelineCount: "{n} éléments",
    timelineCountOne: "{n} élément",
    timelineEmpty: "Aucune conversation IA dans ce projet pour le moment.",
    timelineYou: "Vous",
    timelineUserAria: "Utilisateur",
    untitled: "Sans titre",
    noContent: "(vide)",
    // Account
    accountGenspark: "Compte Bytedesk",
    account: "Compte",
    login: "Se connecter",
    loginGenspark: "Se connecter avec Bytedesk",
    loggedIn: "Connecté",
    loggedInGenspark: "Connecté à Bytedesk",
    waitingLogin: "En attente de la connexion dans le navigateur… Cliquez pour rouvrir la page de connexion",
    waitingShort: "En attente…",
    loginTimeout: "La connexion a expiré — cliquez pour réessayer",
    loginLaunchFailed: "Impossible de lancer la connexion — cliquez pour réessayer",
    loginOpenManually: "Le navigateur ne s’est pas ouvert ? Cliquez pour ouvrir manuellement",
    loginOpenShort: "Ouvrir la page de connexion",
    loginCopyUrl: "Copier le lien de connexion",
    loginCopied: "Copié",
    loginNetworkError: "Impossible de joindre Bytedesk — vérifiez votre réseau ou vos paramètres de proxy",
    loginExpired: "L’autorisation a expiré — cliquez pour réessayer",
    loginFailed: "Échec de la connexion — cliquez pour réessayer",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Déconnexion…",
    logout: "Se déconnecter",
    credits: "Crédits",
    creditsTip: "Voir le détail de la consommation de crédits",
    appVersion: "Version {v}",
    versionLabel: "Version",
    updateChannel: "Canal de mise à jour",
    channelStable: "Stable",
    channelBeta: "Bêta",
    theme: "Thème",
    themeLight: "Clair",
    themeDark: "Sombre",
    themeSystem: "Suivre le système",
    saveLocation: "Emplacement d'enregistrement",
    setAnalytics: "Envoyer des statistiques d'utilisation anonymes",
    setAnalyticsDesc: "Activé par défaut et désactivable dans Paramètres → Général. Utilise Google Analytics 4 ; Google reçoit votre adresse IP publique et les métadonnées de transport, mais jamais le contenu des documents ni les noms de fichiers.",
    settings: "Paramètres",
    setSecAccount: "Compte",
    setSecGeneral: "Général",
    setSecAbout: "À propos",
    setSecAiModel: "Modèle IA",
    setAiProvider: "Fournisseur",
    setAiModelId: "Modèle",
    setAiApiKey: "Clé API",
    setAiKeyHint: "Stockée uniquement sur cet appareil.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Laisser vide pour le point de terminaison officiel.",
    setAiGensparkHint: "Utilise votre connexion Bytedesk ; aucune clé API requise.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Les conversations utilisent votre propre clé ; les outils cloud (recherche web, génération d'images) nécessitent toujours la connexion Bytedesk.",
    setAiSave: "Enregistrer",
    setAiSaved: "Enregistré",
    setAiTest: "Tester la connexion",
    setAiTesting: "Test en cours…",
    setAiTestOk: "Connexion réussie",
    setAiTestFail: "Échec de la connexion",
    setAiMaxTokens: "Jetons de sortie max.",
    setAiMaxTokensDesc: "Budget de sortie pour un tour. Les modèles à raisonnement le dépensent en réflexion ; quand il est épuisé, la réponse arrive vide : augmentez cette valeur.",
    setAiGskTools: "Outils cloud Bytedesk",
    setAiGskToolsDesc: "Recherche web, génération d'images et analyse de médias passent par Bytedesk et consomment des crédits ; désactivé, les outils d'image sont indisponibles et la recherche utilise des sources gratuites.",
    setGithub: "Open source",
    starOnGitHub: "Mettre une étoile sur GitHub",
    starPromptTitle: "Bytedesk Office vous plaît ?",
    starPromptTitleN: "Vous avez ouvert {n} documents avec Bytedesk Office",
    starPromptBody: "Bytedesk Office est gratuit et open source. Une étoile sur GitHub est le meilleur moyen de soutenir l’équipe.",
    starPromptGo: "Mettre une étoile",
    starPromptDone: "Déjà fait",
    starPromptLater: "Plus tard",
    onbStarHint: "Si Bytedesk Office vous plaît, mettez-nous une étoile sur GitHub.",
    setEmail: "E-mail",
    setServer: "Server",
    setNotLoggedIn: "Non connecté",
    setViewUsage: "Voir l'utilisation",
    setChange: "Modifier",
    // Dates
    today: "Aujourd'hui",
    yesterday: "Hier",
    daysAgo: "il y a {n} j",
    // Language / tab strip
    language: "Langue",
    closeTab: "Fermer l'onglet",
    tabSync: "Synchroniser",
    tabSyncing: "Synchronisation…",
    tabSyncDone: "Synchronisé",
    tabSyncFailed: "Échec de la synchronisation",
    tabSyncNeedLogin: "Connectez-vous d’abord",
    tabShare: "Partager",
    tabShareDone: "Lien copié",
    tabList: "Tous les onglets",
    newTab: "Nouvel onglet",
    // First-run onboarding
    onbTitle1: "Bienvenue dans Bytedesk Office",
    onbSubtitle1: "La première suite bureautique open source et native IA",
    onbBody1: "Créez des documents, des feuilles de calcul et des présentations, et relisez des PDF. L’IA est intégrée à chaque étape.",
    onbTitle2: "Ce n’est qu’un début",
    onbBody2: "Bytedesk Office est encore en alpha. Rejoignez la discussion de groupe sur Bytedesk Community pour partager vos retours et façonner la suite.",
    onbCredits: "Les contributeurs actifs reçoivent **1 000+ crédits Bytedesk**",
    onbJoinGenTeam: "Rejoindre Bytedesk Community",
    onbSkip: "Passer",
    onbNext: "Suivant",
    onbStart: "Commencer",
    onbStepAria: "Page {n} sur {total}",
    onbTitle3: "Gratuit pour tous",
    onbBody3: "Pas de licence. Pas de publicité. Pas de filigrane.",
    onbNote3: "Les fonctions IA peuvent consommer des crédits Bytedesk.",
    onbBack: "Retour"
  },
  de: {
    navCollapseSidebar: "Seitenleiste einklappen",
    navExpandSidebar: "Seitenleiste ausklappen",
    // Sidebar navigation
    navRecent: "Zuletzt verwendet",
    navStarred: "Favoriten",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Mit Bytedesk AI im Web erstellte Projekte. Die Bearbeitung läuft im Browser weiter – klicken Sie auf ein Projekt, um es zu öffnen.",
    cloudSearchPlaceholder: "{n} Projekte durchsuchen…",
    cloudNoResults: "Keine passenden Projekte.",
    cloudGroupThisWeek: "Diese Woche",
    cloudGroupThisMonth: "Früher in diesem Monat",
    cloudSortLabel: "Sortierung: {v}",
    cloudSortRecent: "Neueste",
    cloudSortOldest: "Älteste",
    cloudRefresh: "Aktualisieren",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Melden Sie sich bei Ihrem Bytedesk-Konto an, um Ihre im Web erstellten Projekte zu sehen.",
    cloudEmpty: "Noch keine Web-Projekte.",
    cloudError: "Laden fehlgeschlagen. Bitte später erneut versuchen.",
    cloudRetry: "Erneut versuchen",
    cloudLoadMore: "Mehr laden",
    cloudOpenInBrowser: "Im Browser öffnen",
    cloudCopyLink: "Link kopieren",
    navTrash: "Papierkorb",
    navTrashTip: "Gelöschte Dateien werden in den System-Papierkorb verschoben und können dort wiederhergestellt werden",
    // Section headings
    secQuickStart: "Schnellstart",
    secRecent: "Zuletzt verwendet",
    secStarred: "Favoriten",
    secProjectFiles: "Projektdateien",
    secActivity: "Aktivität",
    colName: "Name",
    colLocation: "Speicherort",
    colModified: "Geändert",
    colSize: "Größe",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Lokale Datei öffnen",
    dropToOpenTitle: "Zum Öffnen ablegen",
    greetMorning: "Guten Morgen",
    greetAfternoon: "Guten Tag",
    greetEvening: "Guten Abend",
    greetAsk1: "Was möchten Sie heute erstellen?",
    greetAsk2: "Bereit loszulegen?",
    greetAsk3: "Woran arbeiten Sie?",
    greetAsk4: "Womit sollen wir heute beginnen?",
    greetAsk5: "Lust auf etwas Neues?",
    greetAsk6: "Schon eine Idee?",
    // Filters
    filterAll: "Alle",
    filterDocs: "Dokumente",
    filterSheets: "Tabellen",
    filterSlides: "Präsentationen",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Nach Typ filtern",
    // List / bulk actions
    fileCount: "{n} Dateien",
    fileCountOne: "{n} Datei",
    selectedCount: "{n} ausgewählt",
    selectAll: "Alle auswählen",
    selectFile: "{name} auswählen",
    removeFromList: "Aus Liste entfernen",
    missingFileTitle: "Datei nicht verfügbar",
    missingFileBody: "„{name}“ wurde nicht gefunden. Sie befindet sich möglicherweise auf einem getrennten Laufwerk oder wurde verschoben oder gelöscht. Aus der Liste entfernen?",
    deleteFiles: "Dateien löschen",
    cancel: "Abbrechen",
    star: "Zu Favoriten hinzufügen",
    unstar: "Aus Favoriten entfernen",
    moreActions: "Weitere Aktionen",
    open: "Öffnen",
    revealInFolder: "Im Ordner anzeigen",
    copyPath: "Pfad kopieren",
    moveToProject: "In Projekt verschieben",
    rename: "Umbenennen",
    duplicate: "Duplizieren",
    renameFailed: "Umbenennen fehlgeschlagen",
    emptyStarred: "Keine Favoriten — bewegen Sie den Mauszeiger über eine Zeile und klicken Sie auf den Stern.",
    emptyRecent: "Keine zuletzt verwendeten Dateien — erstellen oder öffnen Sie eine Datei, um zu beginnen.",
    emptyFiltered: "Keine Dateien dieses Typs.",
    // Delete confirmation dialog
    deleteModalTitle: "Dateien löschen",
    deleteConfirmOne: "„{name}“ in den Papierkorb verschieben?",
    deleteConfirmMany: "Diese {n} Dateien in den Papierkorb verschieben?",
    deleteMoreCount: "… insgesamt {n}",
    delete: "Löschen",
    // Project sidebar
    projects: "Projekte",
    newProject: "Neues Projekt",
    projectName: "Projektname",
    deleteProject: "Projekt löschen…",
    deleteProjectConfirm: "Dieses Projekt löschen?\nSeine Dateien kehren in das Standardprojekt zurück und gehen nicht verloren.",
    projMoreActions: "Weitere Aktionen für {name}",
    defaultProject: "Standardprojekt",
    projEmpty: "Bewahren Sie zusammengehörige Dokumente in einem Projekt auf — der KI-Chatverlauf sammelt sich pro Projekt an.",
    projEmptyHint: "Erstellen Sie eine Datei oder klicken Sie mit der rechten Maustaste auf eine zuletzt verwendete Datei und wählen Sie „In Projekt verschieben“.",
    timelineCount: "{n} Einträge",
    timelineCountOne: "{n} Eintrag",
    timelineEmpty: "Noch keine KI-Unterhaltungen in diesem Projekt.",
    timelineYou: "Sie",
    timelineUserAria: "Benutzer",
    untitled: "Unbenannt",
    noContent: "(leer)",
    // Account
    accountGenspark: "Bytedesk-Konto",
    account: "Konto",
    login: "Anmelden",
    loginGenspark: "Mit Bytedesk anmelden",
    loggedIn: "Angemeldet",
    loggedInGenspark: "Bei Bytedesk angemeldet",
    waitingLogin: "Warten auf Anmeldung im Browser… Klicken Sie, um die Anmeldeseite erneut zu öffnen",
    waitingShort: "Warten…",
    loginTimeout: "Zeitüberschreitung bei der Anmeldung — klicken Sie zum Wiederholen",
    loginLaunchFailed: "Anmeldung konnte nicht gestartet werden — klicken Sie zum Wiederholen",
    loginOpenManually: "Browser nicht geöffnet? Hier klicken, um manuell zu öffnen",
    loginOpenShort: "Anmeldeseite manuell öffnen",
    loginCopyUrl: "Anmeldelink kopieren",
    loginCopied: "Kopiert",
    loginNetworkError: "Bytedesk ist nicht erreichbar — prüfen Sie Netzwerk- oder Proxy-Einstellungen",
    loginExpired: "Die Autorisierung ist abgelaufen — klicken Sie zum Wiederholen",
    loginFailed: "Anmeldung fehlgeschlagen — klicken Sie zum Wiederholen",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Abmelden…",
    logout: "Abmelden",
    credits: "Credits",
    creditsTip: "Credit-Verbrauch ansehen",
    appVersion: "Version {v}",
    versionLabel: "Version",
    updateChannel: "Update-Kanal",
    channelStable: "Stabil",
    channelBeta: "Beta",
    theme: "Thema",
    themeLight: "Hell",
    themeDark: "Dunkel",
    themeSystem: "System folgen",
    saveLocation: "Speicherort",
    setAnalytics: "Anonyme Nutzungsstatistiken senden",
    setAnalyticsDesc: "Standardmäßig aktiviert und unter Einstellungen → Allgemein deaktivierbar. Verwendet Google Analytics 4; Google erhält Ihre öffentliche IP-Adresse und Transportmetadaten, aber keine Dokumentinhalte oder Dateinamen.",
    settings: "Einstellungen",
    setSecAccount: "Konto",
    setSecGeneral: "Allgemein",
    setSecAbout: "Über",
    setSecAiModel: "KI-Modell",
    setAiProvider: "Anbieter",
    setAiModelId: "Modell",
    setAiApiKey: "API-Schlüssel",
    setAiKeyHint: "Wird nur auf diesem Gerät gespeichert.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Leer lassen für den offiziellen Endpunkt.",
    setAiGensparkHint: "Nutzt Ihre Bytedesk-Anmeldung; kein API-Schlüssel nötig.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Chats verwenden Ihren eigenen Schlüssel; Cloud-Tools (Websuche, Bilderzeugung) erfordern weiterhin die Bytedesk-Anmeldung.",
    setAiSave: "Speichern",
    setAiSaved: "Gespeichert",
    setAiTest: "Verbindung testen",
    setAiTesting: "Wird getestet…",
    setAiTestOk: "Verbindung erfolgreich",
    setAiTestFail: "Verbindung fehlgeschlagen",
    setAiMaxTokens: "Max. Ausgabe-Tokens",
    setAiMaxTokensDesc: "Ausgabe-Budget pro Durchlauf. Denk-Modelle verbrauchen es beim Reasoning; ist es erschöpft, kommt eine leere Antwort zurück — dann diesen Wert erhöhen.",
    setAiGskTools: "Bytedesk-Cloud-Tools",
    setAiGskToolsDesc: "Websuche, KI-Bilderzeugung und Medienanalyse laufen über Bytedesk und verbrauchen Guthaben; ausgeschaltet sind Bildtools nicht verfügbar und die Suche nutzt freie Quellen.",
    setGithub: "Open Source",
    starOnGitHub: "Auf GitHub Stern geben",
    starPromptTitle: "Gefällt Ihnen Bytedesk Office?",
    starPromptTitleN: "Sie haben {n} Dokumente mit Bytedesk Office geöffnet",
    starPromptBody: "Bytedesk Office ist kostenlos und Open Source. Ein Stern auf GitHub ist die beste Unterstützung für das Team.",
    starPromptGo: "Stern geben",
    starPromptDone: "Schon erledigt",
    starPromptLater: "Später",
    onbStarHint: "Wenn Ihnen Bytedesk Office gefällt, geben Sie uns einen Stern auf GitHub.",
    setEmail: "E-Mail",
    setServer: "Server",
    setNotLoggedIn: "Nicht angemeldet",
    setViewUsage: "Verbrauch anzeigen",
    setChange: "Ändern",
    // Dates
    today: "Heute",
    yesterday: "Gestern",
    daysAgo: "vor {n} Tagen",
    // Language / tab strip
    language: "Sprache",
    closeTab: "Tab schließen",
    tabSync: "Synchronisieren",
    tabSyncing: "Synchronisiere…",
    tabSyncDone: "Synchronisiert",
    tabSyncFailed: "Synchronisierung fehlgeschlagen",
    tabSyncNeedLogin: "Zuerst anmelden",
    tabShare: "Teilen",
    tabShareDone: "Link kopiert",
    tabList: "Alle Tabs",
    newTab: "Neuer Tab",
    // First-run onboarding
    onbTitle1: "Willkommen bei Bytedesk Office",
    onbSubtitle1: "Die erste quelloffene, KI-native Office-Suite",
    onbBody1: "Dokumente erstellen, Tabellen bauen, Präsentationen gestalten und PDFs prüfen. KI ist in jedem Schritt integriert.",
    onbTitle2: "Das ist erst der Anfang",
    onbBody2: "Bytedesk Office ist noch in der Alpha-Phase. Treten Sie dem Gruppenchat auf Bytedesk Community bei, um Feedback zu teilen und die Zukunft mitzugestalten.",
    onbCredits: "Aktive Mitwirkende erhalten **1.000+ Bytedesk-Guthaben**",
    onbJoinGenTeam: "Bytedesk Community beitreten",
    onbSkip: "Überspringen",
    onbNext: "Weiter",
    onbStart: "Loslegen",
    onbStepAria: "Seite {n} von {total}",
    onbTitle3: "Kostenlos für alle",
    onbBody3: "Keine Lizenzgebühren. Keine Werbung. Keine Wasserzeichen.",
    onbNote3: "KI-Funktionen können Bytedesk-Credits verbrauchen.",
    onbBack: "Zurück"
  },
  es: {
    navCollapseSidebar: "Contraer panel lateral",
    navExpandSidebar: "Expandir panel lateral",
    // Sidebar navigation
    navRecent: "Recientes",
    navStarred: "Destacados",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Proyectos creados en la web con Bytedesk AI. La edición continúa en tu navegador: haz clic en un proyecto para abrirlo.",
    cloudSearchPlaceholder: "Buscar entre {n} proyectos…",
    cloudNoResults: "No hay proyectos coincidentes.",
    cloudGroupThisWeek: "Esta semana",
    cloudGroupThisMonth: "Este mes",
    cloudSortLabel: "Orden: {v}",
    cloudSortRecent: "Recientes",
    cloudSortOldest: "Más antiguos",
    cloudRefresh: "Actualizar",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Inicia sesión en tu cuenta de Bytedesk para ver los proyectos creados en la web.",
    cloudEmpty: "Aún no hay proyectos en la web.",
    cloudError: "Error al cargar. Inténtalo más tarde.",
    cloudRetry: "Reintentar",
    cloudLoadMore: "Cargar más",
    cloudOpenInBrowser: "Abrir en el navegador",
    cloudCopyLink: "Copiar enlace",
    navTrash: "Papelera",
    navTrashTip: "Los archivos eliminados van a la papelera del sistema y pueden restaurarse desde allí",
    // Section headings
    secQuickStart: "Inicio rápido",
    secRecent: "Recientes",
    secStarred: "Destacados",
    secProjectFiles: "Archivos del proyecto",
    secActivity: "Actividad",
    colName: "Nombre",
    colLocation: "Ubicación",
    colModified: "Modificado",
    colSize: "Tamaño",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Abrir archivo local",
    dropToOpenTitle: "Suelta para abrir",
    greetMorning: "Buenos días",
    greetAfternoon: "Buenas tardes",
    greetEvening: "Buenas noches",
    greetAsk1: "¿Qué quieres crear hoy?",
    greetAsk2: "¿Listo para empezar?",
    greetAsk3: "¿En qué estás trabajando?",
    greetAsk4: "¿Por dónde empezamos hoy?",
    greetAsk5: "¿Te apetece crear algo nuevo?",
    greetAsk6: "¿Alguna idea?",
    // Filters
    filterAll: "Todos",
    filterDocs: "Documentos",
    filterSheets: "Hojas de cálculo",
    filterSlides: "Presentaciones",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Filtrar por tipo",
    // List / bulk actions
    fileCount: "{n} archivos",
    fileCountOne: "{n} archivo",
    selectedCount: "{n} seleccionados",
    selectAll: "Seleccionar todo",
    selectFile: "Seleccionar {name}",
    removeFromList: "Quitar de la lista",
    missingFileTitle: "Archivo no disponible",
    missingFileBody: "No se encuentra “{name}”. Puede estar en una unidad desconectada, o se ha movido o eliminado. ¿Quitarlo de la lista?",
    deleteFiles: "Eliminar archivos",
    cancel: "Cancelar",
    star: "Destacar",
    unstar: "Quitar de destacados",
    moreActions: "Más acciones",
    open: "Abrir",
    revealInFolder: "Mostrar en la carpeta",
    copyPath: "Copiar ruta",
    moveToProject: "Mover a un proyecto",
    rename: "Cambiar nombre",
    duplicate: "Duplicar",
    renameFailed: "No se pudo cambiar el nombre",
    emptyStarred: "No hay archivos destacados — pase el cursor sobre una fila y haga clic en la estrella.",
    emptyRecent: "No hay archivos recientes — cree o abra un archivo para empezar.",
    emptyFiltered: "No hay archivos de este tipo.",
    // Delete confirmation dialog
    deleteModalTitle: "Eliminar archivos",
    deleteConfirmOne: '¿Mover "{name}" a la papelera?',
    deleteConfirmMany: "¿Mover estos {n} archivos a la papelera?",
    deleteMoreCount: "… {n} en total",
    delete: "Eliminar",
    // Project sidebar
    projects: "Proyectos",
    newProject: "Nuevo proyecto",
    projectName: "Nombre del proyecto",
    deleteProject: "Eliminar proyecto…",
    deleteProjectConfirm: "¿Eliminar este proyecto?\nSus archivos volverán al proyecto predeterminado y no se perderán.",
    projMoreActions: "Más acciones para {name}",
    defaultProject: "Proyecto predeterminado",
    projEmpty: "Guarde los documentos relacionados en un mismo proyecto — el historial de chat de IA se acumula por proyecto.",
    projEmptyHint: 'Cree un archivo o haga clic derecho en un archivo reciente y elija "Mover a un proyecto".',
    timelineCount: "{n} elementos",
    timelineCountOne: "{n} elemento",
    timelineEmpty: "Aún no hay conversaciones de IA en este proyecto.",
    timelineYou: "Tú",
    timelineUserAria: "Usuario",
    untitled: "Sin título",
    noContent: "(vacío)",
    // Account
    accountGenspark: "Cuenta de Bytedesk",
    account: "Cuenta",
    login: "Iniciar sesión",
    loginGenspark: "Iniciar sesión con Bytedesk",
    loggedIn: "Sesión iniciada",
    loggedInGenspark: "Sesión iniciada en Bytedesk",
    waitingLogin: "Esperando el inicio de sesión en el navegador… Haga clic para volver a abrir la página de inicio de sesión",
    waitingShort: "Esperando…",
    loginTimeout: "El inicio de sesión ha caducado — haga clic para reintentar",
    loginLaunchFailed: "No se pudo iniciar el inicio de sesión — haga clic para reintentar",
    loginOpenManually: "¿No se abrió el navegador? Haga clic para abrirlo manualmente",
    loginOpenShort: "Abrir página de inicio de sesión",
    loginCopyUrl: "Copiar enlace de inicio de sesión",
    loginCopied: "Copiado",
    loginNetworkError: "No se puede conectar con Bytedesk — compruebe su red o la configuración del proxy",
    loginExpired: "La autorización ha caducado — haga clic para reintentar",
    loginFailed: "Error al iniciar sesión — haga clic para reintentar",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Cerrando sesión…",
    logout: "Cerrar sesión",
    credits: "Créditos",
    creditsTip: "Ver el detalle del uso de créditos",
    appVersion: "Versión {v}",
    versionLabel: "Versión",
    updateChannel: "Canal de actualización",
    channelStable: "Estable",
    channelBeta: "Beta",
    theme: "Tema",
    themeLight: "Claro",
    themeDark: "Oscuro",
    themeSystem: "Seguir el sistema",
    saveLocation: "Ubicación de guardado",
    setAnalytics: "Enviar estadísticas de uso anónimas",
    setAnalyticsDesc: "Activado de forma predeterminada y desactivable en Configuración → General. Usa Google Analytics 4; Google recibe tu IP pública y metadatos de transporte, pero nunca el contenido de documentos ni los nombres de archivo.",
    settings: "Configuración",
    setSecAccount: "Cuenta",
    setSecGeneral: "General",
    setSecAbout: "Acerca de",
    setSecAiModel: "Modelo de IA",
    setAiProvider: "Proveedor",
    setAiModelId: "Modelo",
    setAiApiKey: "Clave de API",
    setAiKeyHint: "Se guarda solo en este dispositivo.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Deja vacío para usar el endpoint oficial.",
    setAiGensparkHint: "Usa tu inicio de sesión de Bytedesk; no se necesita clave de API.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Los chats usan tu propia clave; las herramientas en la nube (búsqueda web, generación de imágenes) siguen requiriendo el inicio de sesión de Bytedesk.",
    setAiSave: "Guardar",
    setAiSaved: "Guardado",
    setAiTest: "Probar conexión",
    setAiTesting: "Probando…",
    setAiTestOk: "Conexión correcta",
    setAiTestFail: "Error de conexión",
    setAiMaxTokens: "Tokens de salida máx.",
    setAiMaxTokensDesc: "Presupuesto de salida por turno. Los modelos de razonamiento lo gastan en pensar; si se agota, la respuesta llega vacía: suba este valor.",
    setAiGskTools: "Herramientas en la nube de Bytedesk",
    setAiGskToolsDesc: "La búsqueda web, la generación de imágenes y el análisis de medios pasan por Bytedesk y consumen créditos; desactivado, las herramientas de imagen no están disponibles y la búsqueda usa fuentes gratuitas.",
    setGithub: "Código abierto",
    starOnGitHub: "Dar una estrella en GitHub",
    starPromptTitle: "¿Te gusta Bytedesk Office?",
    starPromptTitleN: "Has abierto {n} documentos con Bytedesk Office",
    starPromptBody: "Bytedesk Office es gratuito y de código abierto. Una estrella en GitHub es la mejor forma de apoyar al equipo.",
    starPromptGo: "Dar una estrella",
    starPromptDone: "Ya la di",
    starPromptLater: "Más tarde",
    onbStarHint: "Si te gusta Bytedesk Office, danos una estrella en GitHub.",
    setEmail: "Correo electrónico",
    setServer: "Server",
    setNotLoggedIn: "Sesión no iniciada",
    setViewUsage: "Ver uso",
    setChange: "Cambiar",
    // Dates
    today: "Hoy",
    yesterday: "Ayer",
    daysAgo: "hace {n} días",
    // Language / tab strip
    language: "Idioma",
    closeTab: "Cerrar pestaña",
    tabSync: "Sincronizar",
    tabSyncing: "Sincronizando…",
    tabSyncDone: "Sincronizado",
    tabSyncFailed: "Error al sincronizar",
    tabSyncNeedLogin: "Inicia sesión primero",
    tabShare: "Compartir",
    tabShareDone: "Enlace copiado",
    tabList: "Todas las pestañas",
    newTab: "Nueva pestaña",
    // First-run onboarding
    onbTitle1: "Bienvenido a Bytedesk Office",
    onbSubtitle1: "La primera suite ofimática de código abierto y nativa de IA",
    onbBody1: "Crea documentos, hojas de cálculo y presentaciones, y revisa PDF. La IA está integrada en cada paso.",
    onbTitle2: "Esto es solo el comienzo",
    onbBody2: "Bytedesk Office aún está en alfa. Únete al chat grupal en Bytedesk Community para compartir comentarios y ayudar a dar forma a lo que viene.",
    onbCredits: "Los colaboradores activos reciben **1.000+ créditos de Bytedesk**",
    onbJoinGenTeam: "Unirse a Bytedesk Community",
    onbSkip: "Omitir",
    onbNext: "Siguiente",
    onbStart: "Empezar",
    onbStepAria: "Página {n} de {total}",
    onbTitle3: "Gratis para todos",
    onbBody3: "Sin licencias. Sin anuncios. Sin marcas de agua.",
    onbNote3: "Las funciones de IA pueden consumir créditos de Bytedesk.",
    onbBack: "Atrás"
  },
  th: {
    navCollapseSidebar: "ย่อแถบข้าง",
    navExpandSidebar: "ขยายแถบข้าง",
    // Sidebar navigation
    navRecent: "ล่าสุด",
    navStarred: "รายการโปรด",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "โปรเจกต์ที่สร้างบนเว็บด้วย Bytedesk AI แก้ไขต่อได้ในเบราว์เซอร์ — คลิกโปรเจกต์เพื่อเปิด",
    cloudSearchPlaceholder: "ค้นหา {n} โปรเจกต์…",
    cloudNoResults: "ไม่มีโปรเจกต์ที่ตรงกัน",
    cloudGroupThisWeek: "สัปดาห์นี้",
    cloudGroupThisMonth: "เดือนนี้",
    cloudSortLabel: "เรียง: {v}",
    cloudSortRecent: "ล่าสุด",
    cloudSortOldest: "เก่าสุด",
    cloudRefresh: "รีเฟรช",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "ลงชื่อเข้าใช้บัญชี Bytedesk เพื่อดูโปรเจกต์ที่คุณสร้างบนเว็บ",
    cloudEmpty: "ยังไม่มีโปรเจกต์บนเว็บ",
    cloudError: "โหลดไม่สำเร็จ โปรดลองอีกครั้งภายหลัง",
    cloudRetry: "ลองอีกครั้ง",
    cloudLoadMore: "โหลดเพิ่มเติม",
    cloudOpenInBrowser: "เปิดในเบราว์เซอร์",
    cloudCopyLink: "คัดลอกลิงก์",
    navTrash: "ถังขยะ",
    navTrashTip: "ไฟล์ที่ถูกลบจะถูกย้ายไปยังถังขยะของระบบ และสามารถกู้คืนได้จากที่นั่น",
    // Section headings
    secQuickStart: "เริ่มต้นอย่างรวดเร็ว",
    secRecent: "ใช้งานล่าสุด",
    secStarred: "รายการโปรด",
    secProjectFiles: "ไฟล์โปรเจ็กต์",
    secActivity: "กิจกรรม",
    colName: "ชื่อ",
    colLocation: "ตำแหน่งที่ตั้ง",
    colModified: "แก้ไขเมื่อ",
    colSize: "ขนาด",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "เปิดไฟล์ในเครื่อง",
    dropToOpenTitle: "วางเพื่อเปิดไฟล์",
    greetMorning: "สวัสดีตอนเช้า",
    greetAfternoon: "สวัสดีตอนบ่าย",
    greetEvening: "สวัสดีตอนเย็น",
    greetAsk1: "วันนี้อยากสร้างอะไร",
    greetAsk2: "พร้อมเริ่มต้นหรือยัง",
    greetAsk3: "อยากทำอะไรดี",
    greetAsk4: "วันนี้เริ่มจากตรงไหนดี",
    greetAsk5: "อยากลองทำอะไรใหม่ไหม",
    greetAsk6: "มีไอเดียอะไรบ้าง",
    // Filters
    filterAll: "ทั้งหมด",
    filterDocs: "เอกสาร",
    filterSheets: "สเปรดชีต",
    filterSlides: "งานนำเสนอ",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "กรองตามชนิด",
    // List / bulk actions
    fileCount: "{n} ไฟล์",
    fileCountOne: "{n} ไฟล์",
    selectedCount: "เลือกแล้ว {n} รายการ",
    selectAll: "เลือกทั้งหมด",
    selectFile: "เลือก {name}",
    removeFromList: "นำออกจากรายการ",
    missingFileTitle: "ไฟล์ไม่พร้อมใช้งาน",
    missingFileBody: "ไม่พบ “{name}” อาจอยู่ในไดรฟ์ที่ไม่ได้เชื่อมต่อ หรือถูกย้ายหรือลบไปแล้ว ต้องการนำออกจากรายการหรือไม่",
    deleteFiles: "ลบไฟล์",
    cancel: "ยกเลิก",
    star: "เพิ่มในรายการโปรด",
    unstar: "นำออกจากรายการโปรด",
    moreActions: "การดำเนินการเพิ่มเติม",
    open: "เปิด",
    revealInFolder: "แสดงในโฟลเดอร์",
    copyPath: "คัดลอกเส้นทาง",
    moveToProject: "ย้ายไปยังโปรเจ็กต์",
    rename: "เปลี่ยนชื่อ",
    duplicate: "สร้างสำเนา",
    renameFailed: "เปลี่ยนชื่อไม่สำเร็จ",
    emptyStarred: "ยังไม่มีรายการโปรด — วางเมาส์เหนือแถวไฟล์แล้วคลิกดาว",
    emptyRecent: "ยังไม่มีไฟล์ล่าสุด — สร้างหรือเปิดไฟล์เพื่อเริ่มต้น",
    emptyFiltered: "ไม่มีไฟล์ชนิดนี้",
    // Delete confirmation dialog
    deleteModalTitle: "ลบไฟล์",
    deleteConfirmOne: 'ย้าย "{name}" ไปยังถังขยะหรือไม่',
    deleteConfirmMany: "ย้ายไฟล์ {n} ไฟล์นี้ไปยังถังขยะหรือไม่",
    deleteMoreCount: "… รวมทั้งหมด {n} ไฟล์",
    delete: "ลบ",
    // Project sidebar
    projects: "โปรเจ็กต์",
    newProject: "โปรเจ็กต์ใหม่",
    projectName: "ชื่อโปรเจ็กต์",
    deleteProject: "ลบโปรเจ็กต์…",
    deleteProjectConfirm: 'ลบโปรเจ็กต์นี้หรือไม่\nไฟล์ในโปรเจ็กต์จะกลับไปยัง "โปรเจ็กต์เริ่มต้น" และจะไม่สูญหาย',
    projMoreActions: "การดำเนินการเพิ่มเติมสำหรับ {name}",
    defaultProject: "โปรเจ็กต์เริ่มต้น",
    projEmpty: "เก็บเอกสารที่เกี่ยวข้องไว้ในโปรเจ็กต์เดียวกัน — ประวัติการสนทนา AI จะสะสมตามโปรเจ็กต์",
    projEmptyHint: 'สร้างไฟล์ใหม่ หรือคลิกขวาที่ไฟล์ล่าสุดแล้วเลือก "ย้ายไปยังโปรเจ็กต์"',
    timelineCount: "{n} รายการ",
    timelineCountOne: "{n} รายการ",
    timelineEmpty: "โปรเจ็กต์นี้ยังไม่มีประวัติการสนทนา AI",
    timelineYou: "คุณ",
    timelineUserAria: "ผู้ใช้",
    untitled: "ไม่มีชื่อ",
    noContent: "(ไม่มีเนื้อหา)",
    // Account
    accountGenspark: "บัญชี Bytedesk",
    account: "บัญชี",
    login: "ลงชื่อเข้าใช้",
    loginGenspark: "ลงชื่อเข้าใช้ด้วย Bytedesk",
    loggedIn: "ลงชื่อเข้าใช้แล้ว",
    loggedInGenspark: "ลงชื่อเข้าใช้ Bytedesk แล้ว",
    waitingLogin: "กำลังรอการลงชื่อเข้าใช้ในเบราว์เซอร์… คลิกเพื่อเปิดหน้าลงชื่อเข้าใช้อีกครั้ง",
    waitingShort: "กำลังรอ…",
    loginTimeout: "การลงชื่อเข้าใช้หมดเวลา — คลิกเพื่อลองอีกครั้ง",
    loginLaunchFailed: "ไม่สามารถเริ่มการลงชื่อเข้าใช้ได้ — คลิกเพื่อลองอีกครั้ง",
    loginOpenManually: "เบราว์เซอร์ไม่เปิด? คลิกที่นี่เพื่อเปิดด้วยตนเอง",
    loginOpenShort: "เปิดหน้าเข้าสู่ระบบด้วยตนเอง",
    loginCopyUrl: "คัดลอกลิงก์เข้าสู่ระบบ",
    loginCopied: "คัดลอกแล้ว",
    loginNetworkError: "ไม่สามารถเชื่อมต่อ Bytedesk ได้ โปรดตรวจสอบเครือข่ายหรือการตั้งค่าพร็อกซี",
    loginExpired: "การอนุญาตหมดอายุ — คลิกเพื่อลองอีกครั้ง",
    loginFailed: "การลงชื่อเข้าใช้ล้มเหลว — คลิกเพื่อลองอีกครั้ง",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "กำลังออกจากระบบ…",
    logout: "ออกจากระบบ",
    credits: "เครดิต",
    creditsTip: "ดูรายละเอียดการใช้เครดิต",
    appVersion: "เวอร์ชัน {v}",
    versionLabel: "เวอร์ชัน",
    updateChannel: "ช่องทางอัปเดต",
    channelStable: "เสถียร",
    channelBeta: "เบต้า",
    theme: "ธีม",
    themeLight: "สว่าง",
    themeDark: "มืด",
    themeSystem: "ตามระบบ",
    saveLocation: "ตำแหน่งบันทึก",
    setAnalytics: "ส่งสถิติการใช้งานแบบไม่ระบุตัวตน",
    setAnalyticsDesc: "เปิดใช้งานเป็นค่าเริ่มต้นและปิดได้ทุกเมื่อใน การตั้งค่า → ทั่วไป ใช้ Google Analytics 4 โดย Google จะได้รับ IP สาธารณะและข้อมูลเมตาการรับส่งข้อมูล แต่จะไม่เก็บเนื้อหาเอกสารหรือชื่อไฟล์",
    settings: "การตั้งค่า",
    setSecAccount: "บัญชี",
    setSecGeneral: "ทั่วไป",
    setSecAbout: "เกี่ยวกับ",
    setSecAiModel: "โมเดล AI",
    setAiProvider: "ผู้ให้บริการ",
    setAiModelId: "โมเดล",
    setAiApiKey: "คีย์ API",
    setAiKeyHint: "จัดเก็บไว้ในเครื่องนี้เท่านั้น",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "เว้นว่างเพื่อใช้ปลายทางอย่างเป็นทางการ",
    setAiGensparkHint: "ใช้การลงชื่อเข้าใช้ Bytedesk ไม่ต้องใช้คีย์ API",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "แชทใช้คีย์ของคุณเอง เครื่องมือคลาวด์ (ค้นเว็บ สร้างภาพ) ยังต้องลงชื่อเข้าใช้ Bytedesk",
    setAiSave: "บันทึก",
    setAiSaved: "บันทึกแล้ว",
    setAiTest: "ทดสอบการเชื่อมต่อ",
    setAiTesting: "กำลังทดสอบ…",
    setAiTestOk: "เชื่อมต่อสำเร็จ",
    setAiTestFail: "การเชื่อมต่อล้มเหลว",
    setAiMaxTokens: "จำนวนโทเคนขาออกสูงสุด",
    setAiMaxTokensDesc: "งบผลลัพท์ต่อหนึ่งรอบ โมเดลท่ีไตร่ตรองจะใช้ส่วนหนึ่่งไปกบการคิด หากงบหมด คำตอบจะกลบมาเปลา ให้เพิ่มคา่นี",
    setAiGskTools: "เครื่องมือคลาวด์ Bytedesk",
    setAiGskToolsDesc: "ค้นเว็บ สร้างภาพ AI และวิเคราะห์สื่อผ่านคลาวด์ Bytedesk และใช้เครดิต ปิดแล้วเครื่องมือภาพจะใช้ไม่ได้ และการค้นหาใช้แหล่งฟรี",
    setGithub: "โอเพนซอร์ส",
    starOnGitHub: "กดดาวบน GitHub",
    starPromptTitle: "ชอบ Bytedesk Office ไหม?",
    starPromptTitleN: "คุณเปิดเอกสารด้วย Bytedesk Office ไปแล้ว {n} ไฟล์",
    starPromptBody: "Bytedesk Office ฟรีและเป็นโอเพนซอร์ส การกดดาวบน GitHub คือกำลังใจที่ดีที่สุดสำหรับทีม",
    starPromptGo: "กดดาวเลย",
    starPromptDone: "กดแล้ว",
    starPromptLater: "ไว้ทีหลัง",
    onbStarHint: "ถ้าชอบ Bytedesk Office ฝากกดดาวให้เราบน GitHub ด้วยนะ",
    setEmail: "อีเมล",
    setServer: "Server",
    setNotLoggedIn: "ยังไม่ได้เข้าสู่ระบบ",
    setViewUsage: "ดูการใช้งาน",
    setChange: "เปลี่ยน",
    // Dates
    today: "วันนี้",
    yesterday: "เมื่อวาน",
    daysAgo: "{n} วันที่แล้ว",
    // Language / tab strip
    language: "ภาษา",
    closeTab: "ปิดแท็บ",
    tabSync: "ซิงก์",
    tabSyncing: "กำลังซิงก์…",
    tabSyncDone: "ซิงก์แล้ว",
    tabSyncFailed: "ซิงก์ไม่สำเร็จ",
    tabSyncNeedLogin: "กรุณาเข้าสู่ระบบก่อน",
    tabShare: "แชร์",
    tabShareDone: "คัดลอกลิงก์แล้ว",
    tabList: "แท็บทั้งหมด",
    newTab: "แท็บใหม่",
    // First-run onboarding
    onbTitle1: "ยินดีต้อนรับสู่ Bytedesk Office",
    onbSubtitle1: "ชุดโปรแกรมออฟฟิศ AI-native โอเพนซอร์สตัวแรก",
    onbBody1: "สร้างเอกสาร ทำสเปรดชีต สร้างงานนำเสนอ และตรวจทาน PDF ทุกขั้นตอนมี AI ในตัว",
    onbTitle2: "นี่เป็นเพียงจุดเริ่มต้น",
    onbBody2: "Bytedesk Office ยังอยู่ในช่วงอัลฟ่า เข้าร่วมแชทกลุ่มบน Bytedesk Community เพื่อแบ่งปันความคิดเห็นและร่วมกำหนดทิศทางต่อไป",
    onbCredits: "ผู้มีส่วนร่วมอย่างต่อเนื่องจะได้รับ **เครดิต Bytedesk กว่า 1,000**",
    onbJoinGenTeam: "เข้าร่วม Bytedesk Community",
    onbSkip: "ข้าม",
    onbNext: "ถัดไป",
    onbStart: "เริ่มใช้งาน",
    onbStepAria: "หน้า {n} จาก {total}",
    onbTitle3: "ฟรีสำหรับทุกคน",
    onbBody3: "ไม่มีค่าลิขสิทธิ์ ไม่มีโฆษณา ไม่มีลายน้ำ",
    onbNote3: "ฟีเจอร์ AI อาจใช้เครดิต Bytedesk",
    onbBack: "ย้อนกลับ"
  },
  id: {
    navCollapseSidebar: "Sembunyikan bilah samping",
    navExpandSidebar: "Tampilkan bilah samping",
    // Sidebar navigation
    navRecent: "Terbaru",
    navStarred: "Berbintang",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Proyek yang dibuat di web dengan Bytedesk AI. Pengeditan berlanjut di browser — klik proyek untuk membukanya.",
    cloudSearchPlaceholder: "Cari {n} proyek…",
    cloudNoResults: "Tidak ada proyek yang cocok.",
    cloudGroupThisWeek: "Minggu ini",
    cloudGroupThisMonth: "Bulan ini",
    cloudSortLabel: "Urutkan: {v}",
    cloudSortRecent: "Terbaru",
    cloudSortOldest: "Terlama",
    cloudRefresh: "Segarkan",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Masuk ke akun Bytedesk untuk melihat proyek yang Anda buat di web.",
    cloudEmpty: "Belum ada proyek web.",
    cloudError: "Gagal memuat. Coba lagi nanti.",
    cloudRetry: "Coba lagi",
    cloudLoadMore: "Muat lebih banyak",
    cloudOpenInBrowser: "Buka di browser",
    cloudCopyLink: "Salin tautan",
    navTrash: "Sampah",
    navTrashTip: "File yang dihapus akan dipindahkan ke tempat sampah sistem dan dapat dipulihkan dari sana",
    // Section headings
    secQuickStart: "Mulai cepat",
    secRecent: "Terbaru",
    secStarred: "Berbintang",
    secProjectFiles: "File proyek",
    secActivity: "Aktivitas",
    colName: "Nama",
    colLocation: "Lokasi",
    colModified: "Diubah",
    colSize: "Ukuran",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Buka File Lokal",
    dropToOpenTitle: "Lepaskan untuk membuka",
    greetMorning: "Selamat pagi",
    greetAfternoon: "Selamat siang",
    greetEvening: "Selamat malam",
    greetAsk1: "Mau membuat apa hari ini?",
    greetAsk2: "Siap mulai?",
    greetAsk3: "Ada yang ingin dikerjakan?",
    greetAsk4: "Mulai dari mana hari ini?",
    greetAsk5: "Mau buat sesuatu yang baru?",
    greetAsk6: "Ada ide bagus?",
    // Filters
    filterAll: "Semua",
    filterDocs: "Dokumen",
    filterSheets: "Spreadsheet",
    filterSlides: "Presentasi",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Filter menurut jenis",
    // List / bulk actions
    fileCount: "{n} file",
    fileCountOne: "{n} file",
    selectedCount: "{n} dipilih",
    selectAll: "Pilih semua",
    selectFile: "Pilih {name}",
    removeFromList: "Hapus dari daftar",
    missingFileTitle: "Berkas tidak tersedia",
    missingFileBody: "“{name}” tidak ditemukan. Mungkin berada di drive yang terputus, atau telah dipindahkan atau dihapus. Hapus dari daftar?",
    deleteFiles: "Hapus file",
    cancel: "Batal",
    star: "Beri bintang",
    unstar: "Hapus bintang",
    moreActions: "Tindakan lainnya",
    open: "Buka",
    revealInFolder: "Tampilkan di folder",
    copyPath: "Salin jalur",
    moveToProject: "Pindahkan ke proyek",
    rename: "Ganti nama",
    duplicate: "Duplikat",
    renameFailed: "Gagal mengganti nama",
    emptyStarred: "Belum ada file berbintang — arahkan kursor ke baris file lalu klik bintang.",
    emptyRecent: "Belum ada file terbaru — buat atau buka file untuk memulai.",
    emptyFiltered: "Tidak ada file jenis ini.",
    // Delete confirmation dialog
    deleteModalTitle: "Hapus File",
    deleteConfirmOne: 'Pindahkan "{name}" ke tempat sampah?',
    deleteConfirmMany: "Pindahkan {n} file ini ke tempat sampah?",
    deleteMoreCount: "… total {n} file",
    delete: "Hapus",
    // Project sidebar
    projects: "Proyek",
    newProject: "Proyek baru",
    projectName: "Nama proyek",
    deleteProject: "Hapus proyek…",
    deleteProjectConfirm: "Hapus proyek ini?\nFile di dalamnya akan kembali ke Proyek Default dan tidak akan hilang.",
    projMoreActions: "Tindakan lainnya untuk {name}",
    defaultProject: "Proyek default",
    projEmpty: "Simpan dokumen terkait dalam satu proyek — riwayat percakapan AI terkumpul per proyek.",
    projEmptyHint: 'Buat file, atau klik kanan file terbaru dan pilih "Pindahkan ke proyek".',
    timelineCount: "{n} item",
    timelineCountOne: "{n} item",
    timelineEmpty: "Belum ada percakapan AI di proyek ini.",
    timelineYou: "Anda",
    timelineUserAria: "Pengguna",
    untitled: "Tanpa judul",
    noContent: "(kosong)",
    // Account
    accountGenspark: "Akun Bytedesk",
    account: "Akun",
    login: "Masuk",
    loginGenspark: "Masuk dengan Bytedesk",
    loggedIn: "Sudah masuk",
    loggedInGenspark: "Sudah masuk ke Bytedesk",
    waitingLogin: "Menunggu login di browser… Klik untuk membuka kembali halaman login",
    waitingShort: "Menunggu…",
    loginTimeout: "Waktu login habis — klik untuk mencoba lagi",
    loginLaunchFailed: "Tidak dapat memulai login — klik untuk mencoba lagi",
    loginOpenManually: "Browser tidak terbuka? Klik untuk membuka secara manual",
    loginOpenShort: "Buka halaman login secara manual",
    loginCopyUrl: "Salin tautan login",
    loginCopied: "Disalin",
    loginNetworkError: "Tidak dapat terhubung ke Bytedesk — periksa jaringan atau pengaturan proxy Anda",
    loginExpired: "Otorisasi kedaluwarsa — klik untuk mencoba lagi",
    loginFailed: "Login gagal — klik untuk mencoba lagi",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Keluar…",
    logout: "Keluar",
    credits: "Kredit",
    creditsTip: "Lihat detail penggunaan kredit",
    appVersion: "Versi {v}",
    versionLabel: "Versi",
    updateChannel: "Saluran Pembaruan",
    channelStable: "Stabil",
    channelBeta: "Beta",
    theme: "Tema",
    themeLight: "Terang",
    themeDark: "Gelap",
    themeSystem: "Ikuti Sistem",
    saveLocation: "Lokasi penyimpanan",
    setAnalytics: "Kirim statistik penggunaan anonim",
    setAnalyticsDesc: "Aktif secara default dan dapat dimatikan di Pengaturan → Umum. Menggunakan Google Analytics 4; Google menerima IP publik dan metadata transport Anda, tetapi tidak pernah mengumpulkan isi dokumen atau nama file.",
    settings: "Pengaturan",
    setSecAccount: "Akun",
    setSecGeneral: "Umum",
    setSecAbout: "Tentang",
    setSecAiModel: "Model AI",
    setAiProvider: "Penyedia",
    setAiModelId: "Model",
    setAiApiKey: "Kunci API",
    setAiKeyHint: "Hanya disimpan di perangkat ini.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Kosongkan untuk endpoint resmi.",
    setAiGensparkHint: "Menggunakan login Bytedesk; tanpa kunci API.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Obrolan memakai kunci Anda sendiri; alat cloud (pencarian web, pembuatan gambar) tetap memerlukan login Bytedesk.",
    setAiSave: "Simpan",
    setAiSaved: "Tersimpan",
    setAiTest: "Uji koneksi",
    setAiTesting: "Menguji…",
    setAiTestOk: "Koneksi berhasil",
    setAiTestFail: "Koneksi gagal",
    setAiMaxTokens: "Token keluaran maks.",
    setAiMaxTokensDesc: "Anggaran keluaran untuk satu giliran. Model penalaran memakainya untuk berpikir; jika habis, balasan datang kosong — naikkan nilai ini.",
    setAiGskTools: "Alat cloud Bytedesk",
    setAiGskToolsDesc: "Pencarian web, pembuatan gambar AI, dan analisis media berjalan lewat Bytedesk dan memakai kredit; jika dimatikan, alat gambar tidak tersedia dan pencarian memakai sumber gratis.",
    setGithub: "Sumber Terbuka",
    starOnGitHub: "Beri Bintang di GitHub",
    starPromptTitle: "Suka Bytedesk Office?",
    starPromptTitleN: "Anda telah membuka {n} dokumen dengan Bytedesk Office",
    starPromptBody: "Bytedesk Office gratis dan open source. Bintang di GitHub adalah dukungan terbaik untuk tim kami.",
    starPromptGo: "Beri Bintang",
    starPromptDone: "Sudah",
    starPromptLater: "Nanti saja",
    onbStarHint: "Jika Anda menyukai Bytedesk Office, beri kami bintang di GitHub.",
    setEmail: "Email",
    setServer: "Server",
    setNotLoggedIn: "Belum masuk",
    setViewUsage: "Lihat penggunaan",
    setChange: "Ubah",
    // Dates
    today: "Hari ini",
    yesterday: "Kemarin",
    daysAgo: "{n} hari lalu",
    // Language / tab strip
    language: "Bahasa",
    closeTab: "Tutup tab",
    tabSync: "Sinkron",
    tabSyncing: "Menyinkronkan…",
    tabSyncDone: "Tersinkron",
    tabSyncFailed: "Sinkron gagal",
    tabSyncNeedLogin: "Masuk dulu",
    tabShare: "Bagikan",
    tabShareDone: "Tautan disalin",
    tabList: "Semua tab",
    newTab: "Tab baru",
    // First-run onboarding
    onbTitle1: "Selamat datang di Bytedesk Office",
    onbSubtitle1: "Suite office open source AI-native pertama",
    onbBody1: "Buat dokumen, susun spreadsheet, rancang presentasi, dan tinjau PDF. AI hadir di setiap langkah.",
    onbTitle2: "Ini baru permulaan",
    onbBody2: "Bytedesk Office masih dalam tahap alpha. Gabung obrolan grup di Bytedesk Community untuk berbagi masukan dan ikut menentukan arah ke depan.",
    onbCredits: "Kontributor aktif mendapat **1.000+ kredit Bytedesk**",
    onbJoinGenTeam: "Gabung Bytedesk Community",
    onbSkip: "Lewati",
    onbNext: "Berikutnya",
    onbStart: "Mulai",
    onbStepAria: "Halaman {n} dari {total}",
    onbTitle3: "Gratis untuk semua",
    onbBody3: "Tanpa biaya lisensi. Tanpa iklan. Tanpa watermark.",
    onbNote3: "Fitur AI dapat menggunakan kredit Bytedesk.",
    onbBack: "Kembali"
  },
  ru: {
    navCollapseSidebar: "Свернуть боковую панель",
    navExpandSidebar: "Развернуть боковую панель",
    // Sidebar navigation
    navRecent: "Недавние",
    navStarred: "Избранное",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Проекты, созданные в вебе с Bytedesk AI. Редактирование продолжается в браузере — нажмите на проект, чтобы открыть его.",
    cloudSearchPlaceholder: "Поиск среди {n} проектов…",
    cloudNoResults: "Нет подходящих проектов.",
    cloudGroupThisWeek: "На этой неделе",
    cloudGroupThisMonth: "Ранее в этом месяце",
    cloudSortLabel: "Сортировка: {v}",
    cloudSortRecent: "Сначала новые",
    cloudSortOldest: "Сначала старые",
    cloudRefresh: "Обновить",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Войдите в аккаунт Bytedesk, чтобы увидеть проекты, созданные в вебе.",
    cloudEmpty: "Пока нет веб-проектов.",
    cloudError: "Не удалось загрузить. Повторите попытку позже.",
    cloudRetry: "Повторить",
    cloudLoadMore: "Загрузить ещё",
    cloudOpenInBrowser: "Открыть в браузере",
    cloudCopyLink: "Копировать ссылку",
    navTrash: "Корзина",
    navTrashTip: "Удалённые файлы перемещаются в системную корзину, откуда их можно восстановить",
    // Section headings
    secQuickStart: "Быстрый старт",
    secRecent: "Недавние",
    secStarred: "Избранное",
    secProjectFiles: "Файлы проекта",
    secActivity: "Активность",
    colName: "Имя",
    colLocation: "Расположение",
    colModified: "Изменён",
    colSize: "Размер",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Открыть локальный файл",
    dropToOpenTitle: "Отпустите, чтобы открыть",
    greetMorning: "Доброе утро",
    greetAfternoon: "Добрый день",
    greetEvening: "Добрый вечер",
    greetAsk1: "Что создадим сегодня?",
    greetAsk2: "Готовы начать?",
    greetAsk3: "Над чем работаем?",
    greetAsk4: "С чего начнём сегодня?",
    greetAsk5: "Хотите создать что-то новое?",
    greetAsk6: "Есть идея?",
    // Filters
    filterAll: "Все",
    filterDocs: "Документы",
    filterSheets: "Таблицы",
    filterSlides: "Презентации",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Фильтр по типу",
    // List / bulk actions
    fileCount: "Файлов: {n}",
    fileCountOne: "{n} файл",
    selectedCount: "Выбрано: {n}",
    selectAll: "Выбрать все",
    selectFile: "Выбрать {name}",
    removeFromList: "Убрать из списка",
    missingFileTitle: "Файл недоступен",
    missingFileBody: "Не удалось найти «{name}». Возможно, он находится на отключённом диске, либо был перемещён или удалён. Убрать его из списка?",
    deleteFiles: "Удалить файлы",
    cancel: "Отмена",
    star: "В избранное",
    unstar: "Убрать из избранного",
    moreActions: "Другие действия",
    open: "Открыть",
    revealInFolder: "Показать в папке",
    copyPath: "Скопировать путь",
    moveToProject: "Переместить в проект",
    rename: "Переименовать",
    duplicate: "Создать копию",
    renameFailed: "Не удалось переименовать",
    emptyStarred: "В избранном пока пусто — наведите курсор на строку файла и нажмите на звёздочку.",
    emptyRecent: "Нет недавних файлов — создайте или откройте файл, чтобы начать.",
    emptyFiltered: "Файлов этого типа нет.",
    // Delete confirmation dialog
    deleteModalTitle: "Удаление файлов",
    deleteConfirmOne: "Переместить «{name}» в корзину?",
    deleteConfirmMany: "Переместить эти файлы ({n}) в корзину?",
    deleteMoreCount: "… всего {n}",
    delete: "Удалить",
    // Project sidebar
    projects: "Проекты",
    newProject: "Создать проект",
    projectName: "Название проекта",
    deleteProject: "Удалить проект…",
    deleteProjectConfirm: "Удалить этот проект?\nЕго файлы вернутся в проект по умолчанию и не будут потеряны.",
    projMoreActions: "Другие действия для {name}",
    defaultProject: "Проект по умолчанию",
    projEmpty: "Храните связанные документы в одном проекте — история диалогов с ИИ накапливается по проектам.",
    projEmptyHint: "Создайте файл или щёлкните недавний файл правой кнопкой мыши и выберите «Переместить в проект».",
    timelineCount: "Записей: {n}",
    timelineCountOne: "{n} запись",
    timelineEmpty: "В этом проекте пока нет диалогов с ИИ.",
    timelineYou: "Вы",
    timelineUserAria: "Пользователь",
    untitled: "Без названия",
    noContent: "(пусто)",
    // Account
    accountGenspark: "Учётная запись Bytedesk",
    account: "Учётная запись",
    login: "Войти",
    loginGenspark: "Войти через Bytedesk",
    loggedIn: "Вход выполнен",
    loggedInGenspark: "Выполнен вход в Bytedesk",
    waitingLogin: "Ожидание входа в браузере… Нажмите, чтобы снова открыть страницу входа",
    waitingShort: "Ожидание…",
    loginTimeout: "Время входа истекло — нажмите, чтобы повторить",
    loginLaunchFailed: "Не удалось запустить вход — нажмите, чтобы повторить",
    loginOpenManually: "Браузер не открылся? Нажмите, чтобы открыть вручную",
    loginOpenShort: "Открыть страницу входа вручную",
    loginCopyUrl: "Скопировать ссылку для входа",
    loginCopied: "Скопировано",
    loginNetworkError: "Не удаётся подключиться к Bytedesk — проверьте сеть или настройки прокси",
    loginExpired: "Срок авторизации истёк — нажмите, чтобы повторить",
    loginFailed: "Не удалось войти — нажмите, чтобы повторить",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Выход…",
    logout: "Выйти",
    credits: "Кредиты",
    creditsTip: "Посмотреть расход кредитов",
    appVersion: "Версия {v}",
    versionLabel: "Версия",
    updateChannel: "Канал обновлений",
    channelStable: "Стабильный",
    channelBeta: "Бета",
    theme: "Тема",
    themeLight: "Светлая",
    themeDark: "Тёмная",
    themeSystem: "Как в системе",
    saveLocation: "Папка сохранения",
    setAnalytics: "Отправлять анонимную статистику использования",
    setAnalyticsDesc: "Включено по умолчанию и отключается в Настройки → Общие. Используется Google Analytics 4; Google получает публичный IP и транспортные метаданные, но не содержимое документов и не имена файлов.",
    settings: "Настройки",
    setSecAccount: "Аккаунт",
    setSecGeneral: "Общие",
    setSecAbout: "О программе",
    setSecAiModel: "Модель ИИ",
    setAiProvider: "Провайдер",
    setAiModelId: "Модель",
    setAiApiKey: "API-ключ",
    setAiKeyHint: "Хранится только на этом устройстве.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Оставьте пустым для официальной конечной точки.",
    setAiGensparkHint: "Использует вход в Bytedesk; ключ API не нужен.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Чаты используют ваш собственный ключ; облачные инструменты (веб-поиск, генерация изображений) по-прежнему требуют входа в Bytedesk.",
    setAiSave: "Сохранить",
    setAiSaved: "Сохранено",
    setAiTest: "Проверить подключение",
    setAiTesting: "Проверка…",
    setAiTestOk: "Подключение успешно",
    setAiTestFail: "Ошибка подключения",
    setAiMaxTokens: "Макс. токенов на ответ",
    setAiMaxTokensDesc: "Бюджет вывода за один ход. Модели рассуждений тратят его на размышления: если бюджет иссякнет, ответ придёт пустым — увеличьте значение.",
    setAiGskTools: "Облачные инструменты Bytedesk",
    setAiGskToolsDesc: "Веб-поиск, генерация изображений и анализ медиа идут через Bytedesk и расходуют кредиты; при отключении инструменты изображений недоступны, а поиск использует бесплатные источники.",
    setGithub: "Открытый код",
    starOnGitHub: "Поставить звезду на GitHub",
    starPromptTitle: "Нравится Bytedesk Office?",
    starPromptTitleN: "Вы уже открыли {n} документов в Bytedesk Office",
    starPromptBody: "Bytedesk Office — бесплатное ПО с открытым кодом. Звезда на GitHub — лучшая поддержка для команды.",
    starPromptGo: "Поставить звезду",
    starPromptDone: "Уже поставлена",
    starPromptLater: "Позже",
    onbStarHint: "Если вам нравится Bytedesk Office, поставьте нам звезду на GitHub.",
    setEmail: "Эл. почта",
    setServer: "Server",
    setNotLoggedIn: "Вы не вошли",
    setViewUsage: "Посмотреть расход",
    setChange: "Изменить",
    // Dates
    today: "Сегодня",
    yesterday: "Вчера",
    daysAgo: "{n} дн. назад",
    // Language / tab strip
    language: "Язык",
    closeTab: "Закрыть вкладку",
    tabSync: "Синхронизировать",
    tabSyncing: "Синхронизация…",
    tabSyncDone: "Синхронизировано",
    tabSyncFailed: "Ошибка синхронизации",
    tabSyncNeedLogin: "Сначала войдите",
    tabShare: "Поделиться",
    tabShareDone: "Ссылка скопирована",
    tabList: "Все вкладки",
    newTab: "Новая вкладка",
    // First-run onboarding
    onbTitle1: "Добро пожаловать в Bytedesk Office",
    onbSubtitle1: "Первый открытый AI-нативный офисный пакет",
    onbBody1: "Создавайте документы, таблицы и презентации, работайте с PDF. ИИ встроен в каждый шаг.",
    onbTitle2: "Это только начало",
    onbBody2: "Bytedesk Office пока в альфа-версии. Присоединяйтесь к групповому чату в Bytedesk Community, чтобы делиться отзывами и влиять на дальнейшее развитие.",
    onbCredits: "Активные участники получают **1000+ кредитов Bytedesk**",
    onbJoinGenTeam: "Присоединиться к Bytedesk Community",
    onbSkip: "Пропустить",
    onbNext: "Далее",
    onbStart: "Начать",
    onbStepAria: "Страница {n} из {total}",
    onbTitle3: "Бесплатно для всех",
    onbBody3: "Без лицензий. Без рекламы. Без водяных знаков.",
    onbNote3: "Функции ИИ могут расходовать кредиты Bytedesk.",
    onbBack: "Назад"
  },
  ar: {
    navCollapseSidebar: "طي الشريط الجانبي",
    navExpandSidebar: "توسيع الشريط الجانبي",
    // Sidebar navigation
    navRecent: "الأخيرة",
    navStarred: "المفضلة",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "مشاريع أُنشئت على الويب باستخدام Bytedesk AI. يستمر التحرير في المتصفح — انقر على أي مشروع لفتحه.",
    cloudSearchPlaceholder: "ابحث في {n} مشروعًا…",
    cloudNoResults: "لا توجد مشاريع مطابقة.",
    cloudGroupThisWeek: "هذا الأسبوع",
    cloudGroupThisMonth: "في وقت سابق من هذا الشهر",
    cloudSortLabel: "الترتيب: {v}",
    cloudSortRecent: "الأحدث",
    cloudSortOldest: "الأقدم",
    cloudRefresh: "تحديث",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "سجّل الدخول إلى حساب Bytedesk لعرض المشاريع التي أنشأتها على الويب.",
    cloudEmpty: "لا توجد مشاريع على الويب بعد.",
    cloudError: "فشل التحميل. حاول مرة أخرى لاحقًا.",
    cloudRetry: "إعادة المحاولة",
    cloudLoadMore: "تحميل المزيد",
    cloudOpenInBrowser: "فتح في المتصفح",
    cloudCopyLink: "نسخ الرابط",
    navTrash: "سلة المهملات",
    navTrashTip: "تُنقل الملفات المحذوفة إلى سلة مهملات النظام ويمكن استعادتها من هناك",
    // Section headings
    secQuickStart: "بدء سريع",
    secRecent: "المستخدمة مؤخرًا",
    secStarred: "المفضلة",
    secProjectFiles: "ملفات المشروع",
    secActivity: "النشاط",
    colName: "الاسم",
    colLocation: "الموقع",
    colModified: "تاريخ التعديل",
    colSize: "الحجم",
    // Quick start
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "فتح ملف محلي",
    dropToOpenTitle: "أفلت للفتح",
    greetMorning: "صباح الخير",
    greetAfternoon: "مساء الخير",
    greetEvening: "مساء الخير",
    greetAsk1: "ماذا تريد أن تنشئ اليوم؟",
    greetAsk2: "هل أنت مستعد للبدء؟",
    greetAsk3: "على ماذا تعمل؟",
    greetAsk4: "من أين نبدأ اليوم؟",
    greetAsk5: "هل ترغب في إنشاء شيء جديد؟",
    greetAsk6: "هل لديك فكرة؟",
    // Filters
    filterAll: "الكل",
    filterDocs: "مستندات",
    filterSheets: "جداول بيانات",
    filterSlides: "عروض تقديمية",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "تصفية حسب النوع",
    // List / bulk actions
    fileCount: "{n} ملفات",
    fileCountOne: "ملف واحد",
    selectedCount: "تم تحديد {n}",
    selectAll: "تحديد الكل",
    selectFile: "تحديد {name}",
    removeFromList: "إزالة من القائمة",
    missingFileTitle: "الملف غير متاح",
    missingFileBody: "تعذّر العثور على “{name}”. قد يكون على قرص غير متصل، أو تم نقله أو حذفه. هل تريد إزالته من القائمة؟",
    deleteFiles: "حذف الملفات",
    cancel: "إلغاء",
    star: "إضافة إلى المفضلة",
    unstar: "إزالة من المفضلة",
    moreActions: "إجراءات أخرى",
    open: "فتح",
    revealInFolder: "إظهار في المجلد",
    copyPath: "نسخ المسار",
    moveToProject: "نقل إلى مشروع",
    rename: "إعادة تسمية",
    duplicate: "إنشاء نسخة",
    renameFailed: "فشلت إعادة التسمية",
    emptyStarred: "لا توجد ملفات مفضلة — مرِّر المؤشر فوق صف الملف وانقر على النجمة.",
    emptyRecent: "لا توجد ملفات حديثة — أنشئ ملفًا أو افتحه للبدء.",
    emptyFiltered: "لا توجد ملفات من هذا النوع.",
    // Delete confirmation dialog
    deleteModalTitle: "حذف الملفات",
    deleteConfirmOne: 'هل تريد نقل "{name}" إلى سلة المهملات؟',
    deleteConfirmMany: "هل تريد نقل هذه الملفات ({n}) إلى سلة المهملات؟",
    deleteMoreCount: "… الإجمالي {n}",
    delete: "حذف",
    // Project sidebar
    projects: "المشاريع",
    newProject: "مشروع جديد",
    projectName: "اسم المشروع",
    deleteProject: "حذف المشروع…",
    deleteProjectConfirm: 'هل تريد حذف هذا المشروع؟\nستعود ملفاته إلى "المشروع الافتراضي" ولن تُفقد.',
    projMoreActions: "إجراءات أخرى لـ {name}",
    defaultProject: "المشروع الافتراضي",
    projEmpty: "احتفظ بالمستندات المرتبطة في مشروع واحد — يتراكم سجل محادثات الذكاء الاصطناعي لكل مشروع.",
    projEmptyHint: 'أنشئ ملفًا، أو انقر بزر الماوس الأيمن على ملف حديث واختر "نقل إلى مشروع".',
    timelineCount: "{n} عناصر",
    timelineCountOne: "عنصر واحد",
    timelineEmpty: "لا توجد محادثات ذكاء اصطناعي في هذا المشروع بعد.",
    timelineYou: "أنت",
    timelineUserAria: "المستخدم",
    untitled: "بدون عنوان",
    noContent: "(فارغ)",
    // Account
    accountGenspark: "حساب Bytedesk",
    account: "الحساب",
    login: "تسجيل الدخول",
    loginGenspark: "تسجيل الدخول باستخدام Bytedesk",
    loggedIn: "تم تسجيل الدخول",
    loggedInGenspark: "تم تسجيل الدخول إلى Bytedesk",
    waitingLogin: "في انتظار تسجيل الدخول في المتصفح… انقر لإعادة فتح صفحة تسجيل الدخول",
    waitingShort: "في الانتظار…",
    loginTimeout: "انتهت مهلة تسجيل الدخول — انقر لإعادة المحاولة",
    loginLaunchFailed: "تعذّر بدء تسجيل الدخول — انقر لإعادة المحاولة",
    loginOpenManually: "لم يفتح المتصفح؟ انقر هنا للفتح يدويًا",
    loginOpenShort: "فتح صفحة تسجيل الدخول يدويًا",
    loginCopyUrl: "نسخ رابط تسجيل الدخول",
    loginCopied: "تم النسخ",
    loginNetworkError: "تعذّر الاتصال بـ Bytedesk — تحقق من الشبكة أو إعدادات الوكيل",
    loginExpired: "انتهت صلاحية التفويض — انقر لإعادة المحاولة",
    loginFailed: "فشل تسجيل الدخول — انقر لإعادة المحاولة",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "جارٍ تسجيل الخروج…",
    logout: "تسجيل الخروج",
    credits: "الأرصدة",
    creditsTip: "عرض تفاصيل استخدام الأرصدة",
    appVersion: "الإصدار {v}",
    versionLabel: "الإصدار",
    updateChannel: "قناة التحديث",
    channelStable: "مستقر",
    channelBeta: "تجريبي",
    theme: "المظهر",
    themeLight: "فاتح",
    themeDark: "داكن",
    themeSystem: "اتباع النظام",
    saveLocation: "موقع الحفظ",
    setAnalytics: "إرسال إحصاءات استخدام مجهولة الهوية",
    setAnalyticsDesc: "مفعّل افتراضيًا ويمكن إيقافه في الإعدادات ← عام. يستخدم Google Analytics 4؛ تتلقى Google عنوان IP العام وبيانات النقل، ولكن لا يتم جمع محتوى المستندات أو أسماء الملفات.",
    settings: "الإعدادات",
    setSecAccount: "الحساب",
    setSecGeneral: "عام",
    setSecAbout: "حول",
    setSecAiModel: "نموذج الذكاء الاصطناعي",
    setAiProvider: "المزوّد",
    setAiModelId: "النموذج",
    setAiApiKey: "مفتاح API",
    setAiKeyHint: "يُحفظ على هذا الجهاز فقط.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "اتركه فارغًا لاستخدام نقطة النهاية الرسمية.",
    setAiGensparkHint: "يستخدم تسجيل الدخول إلى Bytedesk؛ لا حاجة لمفتاح API.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "تستخدم المحادثات مفتاحك الخاص؛ أدوات السحابة (بحث الويب وتوليد الصور) ما زالت تتطلب تسجيل الدخول إلى Bytedesk.",
    setAiSave: "حفظ",
    setAiSaved: "تم الحفظ",
    setAiTest: "اختبار الاتصال",
    setAiTesting: "جارٍ الاختبار…",
    setAiTestOk: "نجح الاتصال",
    setAiTestFail: "فشل الاتصال",
    setAiMaxTokens: "الحد الأقصى لرموز المخرجات",
    setAiMaxTokensDesc: "ميزانية الإخراج في الدورة الواحدة. نماذج الاستدلال تصرفها على التفكير، فإذا نفدت جاء الرد فارغًا؛ ارفع هذه القيمة عندئذ.",
    setAiGskTools: "أدوات Bytedesk السحابية",
    setAiGskToolsDesc: "يمر بحث الويب وتوليد الصور وتحليل الوسائط عبر سحابة Bytedesk ويستهلك الرصيد؛ عند الإيقاف تصبح أدوات الصور غير متاحة ويستخدم البحث مصادر مجانية.",
    setGithub: "مفتوح المصدر",
    starOnGitHub: "ضع نجمة على GitHub",
    starPromptTitle: "هل أعجبك Bytedesk Office؟",
    starPromptTitleN: "لقد فتحت {n} من المستندات في Bytedesk Office",
    starPromptBody: "Bytedesk Office مجاني ومفتوح المصدر. نجمة على GitHub هي أفضل دعم للفريق.",
    starPromptGo: "ضع نجمة",
    starPromptDone: "وضعتها بالفعل",
    starPromptLater: "لاحقًا",
    onbStarHint: "إذا أعجبك Bytedesk Office، ضع لنا نجمة على GitHub.",
    setEmail: "البريد الإلكتروني",
    setServer: "Server",
    setNotLoggedIn: "لم يتم تسجيل الدخول",
    setViewUsage: "عرض الاستخدام",
    setChange: "تغيير",
    // Dates
    today: "اليوم",
    yesterday: "أمس",
    daysAgo: "قبل {n} أيام",
    // Language / tab strip
    language: "اللغة",
    closeTab: "إغلاق علامة التبويب",
    tabSync: "مزامنة",
    tabSyncing: "جارٍ المزامنة…",
    tabSyncDone: "تمت المزامنة",
    tabSyncFailed: "فشلت المزامنة",
    tabSyncNeedLogin: "سجّل الدخول أولاً",
    tabShare: "مشاركة",
    tabShareDone: "تم نسخ الرابط",
    tabList: "كل علامات التبويب",
    newTab: "علامة تبويب جديدة",
    // First-run onboarding
    onbTitle1: "مرحبًا بك في Bytedesk Office",
    onbSubtitle1: "أول حزمة مكتبية مفتوحة المصدر وأصيلة في الذكاء الاصطناعي",
    onbBody1: "أنشئ المستندات وجداول البيانات والعروض التقديمية وراجع ملفات PDF. الذكاء الاصطناعي مدمج في كل خطوة.",
    onbTitle2: "هذه مجرد البداية",
    onbBody2: "لا يزال Bytedesk Office في مرحلة ألفا. انضم إلى الدردشة الجماعية على Bytedesk Community لمشاركة ملاحظاتك والمساهمة في تشكيل المستقبل.",
    onbCredits: "يحصل المساهمون النشطون على **+1,000 من أرصدة Bytedesk**",
    onbJoinGenTeam: "الانضمام إلى Bytedesk Community",
    onbSkip: "تخطي",
    onbNext: "التالي",
    onbStart: "ابدأ الآن",
    onbStepAria: "الصفحة {n} من {total}",
    onbTitle3: "مجاني للجميع",
    onbBody3: "بلا رسوم ترخيص، بلا إعلانات، بلا علامات مائية.",
    onbNote3: "قد تستهلك ميزات الذكاء الاصطناعي أرصدة Bytedesk.",
    onbBack: "رجوع"
  },
  pt: {
    navCollapseSidebar: "Recolher barra lateral",
    navExpandSidebar: "Expandir barra lateral",
    navRecent: "Recentes",
    navStarred: "Favoritos",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Projetos criados na web com o Bytedesk AI. A edição continua no navegador — clique em um projeto para abri-lo.",
    cloudSearchPlaceholder: "Pesquisar {n} projetos…",
    cloudNoResults: "Nenhum projeto correspondente.",
    cloudGroupThisWeek: "Esta semana",
    cloudGroupThisMonth: "Este mês",
    cloudSortLabel: "Ordenar: {v}",
    cloudSortRecent: "Recentes",
    cloudSortOldest: "Mais antigos",
    cloudRefresh: "Atualizar",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Entre na sua conta Bytedesk para ver os projetos criados na web.",
    cloudEmpty: "Ainda não há projetos na web.",
    cloudError: "Falha ao carregar. Tente novamente mais tarde.",
    cloudRetry: "Tentar novamente",
    cloudLoadMore: "Carregar mais",
    cloudOpenInBrowser: "Abrir no navegador",
    cloudCopyLink: "Copiar link",
    navTrash: "Lixeira",
    navTrashTip: "Os arquivos excluídos vão para a lixeira do sistema e podem ser restaurados de lá",
    secQuickStart: "Início rápido",
    secRecent: "Recentes",
    secStarred: "Favoritos",
    secProjectFiles: "Arquivos do projeto",
    secActivity: "Atividade",
    colName: "Nome",
    colLocation: "Local",
    colModified: "Modificado",
    colSize: "Tamanho",
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Abrir arquivo local",
    dropToOpenTitle: "Solte para abrir",
    greetMorning: "Bom dia",
    greetAfternoon: "Boa tarde",
    greetEvening: "Boa noite",
    greetAsk1: "O que você quer criar hoje?",
    greetAsk2: "Pronto para começar?",
    greetAsk3: "No que você está trabalhando?",
    greetAsk4: "Por onde começamos hoje?",
    greetAsk5: "Que tal criar algo novo?",
    greetAsk6: "Alguma ideia?",
    filterAll: "Todos",
    filterDocs: "Documentos",
    filterSheets: "Planilhas",
    filterSlides: "Apresentações",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Filtrar por tipo",
    fileCount: "{n} arquivos",
    fileCountOne: "{n} arquivo",
    selectedCount: "{n} selecionados",
    selectAll: "Selecionar tudo",
    selectFile: "Selecionar {name}",
    removeFromList: "Remover da lista",
    missingFileTitle: "Arquivo indisponível",
    missingFileBody: "Não foi possível encontrar “{name}”. Ele pode estar em uma unidade desconectada, ou foi movido ou excluído. Removê-lo da lista?",
    deleteFiles: "Excluir arquivos",
    cancel: "Cancelar",
    star: "Adicionar aos favoritos",
    unstar: "Remover dos favoritos",
    moreActions: "Mais ações",
    open: "Abrir",
    revealInFolder: "Mostrar na pasta",
    copyPath: "Copiar caminho",
    moveToProject: "Mover para um projeto",
    rename: "Renomear",
    duplicate: "Duplicar",
    renameFailed: "Falha ao renomear",
    emptyStarred: "Nenhum favorito — passe o cursor sobre uma linha e clique na estrela.",
    emptyRecent: "Nenhum arquivo recente — crie ou abra um arquivo para começar.",
    emptyFiltered: "Nenhum arquivo deste tipo.",
    deleteModalTitle: "Excluir arquivos",
    deleteConfirmOne: 'Mover "{name}" para a lixeira?',
    deleteConfirmMany: "Mover estes {n} arquivos para a lixeira?",
    deleteMoreCount: "… {n} no total",
    delete: "Excluir",
    projects: "Projetos",
    newProject: "Novo projeto",
    projectName: "Nome do projeto",
    deleteProject: "Excluir projeto…",
    deleteProjectConfirm: "Excluir este projeto?\nSeus arquivos voltarão ao projeto padrão e não serão perdidos.",
    projMoreActions: "Mais ações para {name}",
    defaultProject: "Projeto padrão",
    projEmpty: "Mantenha documentos relacionados em um mesmo projeto — o histórico de conversas com a IA se acumula por projeto.",
    projEmptyHint: 'Crie um arquivo ou clique com o botão direito em um arquivo recente e escolha "Mover para um projeto".',
    timelineCount: "{n} itens",
    timelineCountOne: "{n} item",
    timelineEmpty: "Ainda não há conversas com a IA neste projeto.",
    timelineYou: "Você",
    timelineUserAria: "Usuário",
    untitled: "Sem título",
    noContent: "(vazio)",
    accountGenspark: "Conta Bytedesk",
    account: "Conta",
    login: "Entrar",
    loginGenspark: "Entrar com a Bytedesk",
    loggedIn: "Conectado",
    loggedInGenspark: "Conectado à Bytedesk",
    waitingLogin: "Aguardando o login no navegador… Clique para reabrir a página de login",
    waitingShort: "Aguardando…",
    loginTimeout: "O login expirou — clique para tentar novamente",
    loginLaunchFailed: "Não foi possível iniciar o login — clique para tentar novamente",
    loginOpenManually: "O navegador não abriu? Clique para abrir manualmente",
    loginOpenShort: "Abrir página de login manualmente",
    loginCopyUrl: "Copiar link de login",
    loginCopied: "Copiado",
    loginNetworkError: "Não foi possível conectar ao Bytedesk — verifique sua rede ou as configurações de proxy",
    loginExpired: "A autorização expirou — clique para tentar novamente",
    loginFailed: "Falha no login — clique para tentar novamente",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Saindo…",
    logout: "Sair",
    credits: "Créditos",
    creditsTip: "Ver detalhes do uso de créditos",
    appVersion: "Versão {v}",
    versionLabel: "Versão",
    updateChannel: "Canal de atualização",
    channelStable: "Estável",
    channelBeta: "Beta",
    theme: "Tema",
    themeLight: "Claro",
    themeDark: "Escuro",
    themeSystem: "Seguir o Sistema",
    saveLocation: "Local de salvamento",
    setAnalytics: "Enviar estatísticas de uso anônimas",
    setAnalyticsDesc: "Ativado por padrão e pode ser desativado em Configurações → Geral. Usa o Google Analytics 4; o Google recebe seu IP público e metadados de transporte, mas nunca o conteúdo dos documentos ou nomes de arquivos.",
    settings: "Configurações",
    setSecAccount: "Conta",
    setSecGeneral: "Geral",
    setSecAbout: "Sobre",
    setSecAiModel: "Modelo de IA",
    setAiProvider: "Provedor",
    setAiModelId: "Modelo",
    setAiApiKey: "Chave de API",
    setAiKeyHint: "Armazenada apenas neste dispositivo.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Deixe vazio para o endpoint oficial.",
    setAiGensparkHint: "Usa seu login Bytedesk; nenhuma chave de API necessária.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Os chats usam sua própria chave; as ferramentas na nuvem (busca na web, geração de imagens) ainda exigem o login Bytedesk.",
    setAiSave: "Salvar",
    setAiSaved: "Salvo",
    setAiTest: "Testar conexão",
    setAiTesting: "Testando…",
    setAiTestOk: "Conexão bem-sucedida",
    setAiTestFail: "Falha na conexão",
    setAiMaxTokens: "Máx. de tokens de saída",
    setAiMaxTokensDesc: "Orçamento de saída por turno. Modelos de raciocínio gastam-no pensando; se esgotar, a resposta vem vazia — aumente este valor.",
    setAiGskTools: "Ferramentas na nuvem Bytedesk",
    setAiGskToolsDesc: "Busca na web, geração de imagens e análise de mídia passam pela Bytedesk e consomem créditos; desligado, as ferramentas de imagem ficam indisponíveis e a busca usa fontes gratuitas.",
    setGithub: "Código aberto",
    starOnGitHub: "Dar uma estrela no GitHub",
    starPromptTitle: "Gostando do Bytedesk Office?",
    starPromptTitleN: "Você já abriu {n} documentos com o Bytedesk Office",
    starPromptBody: "O Bytedesk Office é gratuito e de código aberto. Uma estrela no GitHub é a melhor forma de apoiar a equipe.",
    starPromptGo: "Dar uma estrela",
    starPromptDone: "Já dei",
    starPromptLater: "Mais tarde",
    onbStarHint: "Se você gosta do Bytedesk Office, deixe uma estrela no GitHub.",
    setEmail: "E-mail",
    setServer: "Server",
    setNotLoggedIn: "Não conectado",
    setViewUsage: "Ver uso",
    setChange: "Alterar",
    today: "Hoje",
    yesterday: "Ontem",
    daysAgo: "há {n} dias",
    language: "Idioma",
    closeTab: "Fechar guia",
    tabSync: "Sincronizar",
    tabSyncing: "Sincronizando…",
    tabSyncDone: "Sincronizado",
    tabSyncFailed: "Falha na sincronização",
    tabSyncNeedLogin: "Entre primeiro",
    tabShare: "Compartilhar",
    tabShareDone: "Link copiado",
    tabList: "Todas as guias",
    newTab: "Nova guia",
    // First-run onboarding
    onbTitle1: "Bem-vindo ao Bytedesk Office",
    onbSubtitle1: "A primeira suíte de escritório open source e nativa de IA",
    onbBody1: "Crie documentos, planilhas e apresentações e revise PDFs. A IA está integrada em cada etapa.",
    onbTitle2: "Isto é só o começo",
    onbBody2: "O Bytedesk Office ainda está em alfa. Entre no chat em grupo no Bytedesk Community para compartilhar feedback e ajudar a moldar o que vem a seguir.",
    onbCredits: "Contribuidores ativos recebem **1.000+ créditos Bytedesk**",
    onbJoinGenTeam: "Entrar no Bytedesk Community",
    onbSkip: "Pular",
    onbNext: "Avançar",
    onbStart: "Começar",
    onbStepAria: "Página {n} de {total}",
    onbTitle3: "Gratuito para todos",
    onbBody3: "Sem licenças. Sem anúncios. Sem marcas d'água.",
    onbNote3: "Os recursos de IA podem consumir créditos Bytedesk.",
    onbBack: "Voltar"
  },
  it: {
    navCollapseSidebar: "Comprimi barra laterale",
    navExpandSidebar: "Espandi barra laterale",
    navRecent: "Recenti",
    navStarred: "Preferiti",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Progetti creati sul web con Bytedesk AI. La modifica continua nel browser: fai clic su un progetto per aprirlo.",
    cloudSearchPlaceholder: "Cerca tra {n} progetti…",
    cloudNoResults: "Nessun progetto corrispondente.",
    cloudGroupThisWeek: "Questa settimana",
    cloudGroupThisMonth: "Questo mese",
    cloudSortLabel: "Ordina: {v}",
    cloudSortRecent: "Recenti",
    cloudSortOldest: "Meno recenti",
    cloudRefresh: "Aggiorna",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Accedi al tuo account Bytedesk per vedere i progetti creati sul web.",
    cloudEmpty: "Ancora nessun progetto web.",
    cloudError: "Caricamento non riuscito. Riprova più tardi.",
    cloudRetry: "Riprova",
    cloudLoadMore: "Carica altri",
    cloudOpenInBrowser: "Apri nel browser",
    cloudCopyLink: "Copia link",
    navTrash: "Cestino",
    navTrashTip: "I file eliminati vengono spostati nel cestino di sistema e possono essere ripristinati da lì",
    secQuickStart: "Avvio rapido",
    secRecent: "Recenti",
    secStarred: "Preferiti",
    secProjectFiles: "File del progetto",
    secActivity: "Attività",
    colName: "Nome",
    colLocation: "Posizione",
    colModified: "Modificato",
    colSize: "Dimensione",
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Apri file locale",
    dropToOpenTitle: "Rilascia per aprire",
    greetMorning: "Buongiorno",
    greetAfternoon: "Buon pomeriggio",
    greetEvening: "Buonasera",
    greetAsk1: "Cosa vuoi creare oggi?",
    greetAsk2: "Pronto per iniziare?",
    greetAsk3: "Su cosa stai lavorando?",
    greetAsk4: "Da dove iniziamo oggi?",
    greetAsk5: "Vuoi creare qualcosa di nuovo?",
    greetAsk6: "Hai un'idea?",
    filterAll: "Tutti",
    filterDocs: "Documenti",
    filterSheets: "Fogli di calcolo",
    filterSlides: "Presentazioni",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Filtra per tipo",
    fileCount: "{n} file",
    fileCountOne: "{n} file",
    selectedCount: "{n} selezionati",
    selectAll: "Seleziona tutto",
    selectFile: "Seleziona {name}",
    removeFromList: "Rimuovi dall'elenco",
    missingFileTitle: "File non disponibile",
    missingFileBody: "Impossibile trovare “{name}”. Potrebbe trovarsi su un'unità disconnessa, oppure è stato spostato o eliminato. Rimuoverlo dall'elenco?",
    deleteFiles: "Elimina file",
    cancel: "Annulla",
    star: "Aggiungi ai preferiti",
    unstar: "Rimuovi dai preferiti",
    moreActions: "Altre azioni",
    open: "Apri",
    revealInFolder: "Mostra nella cartella",
    copyPath: "Copia percorso",
    moveToProject: "Sposta in un progetto",
    rename: "Rinomina",
    duplicate: "Duplica",
    renameFailed: "Impossibile rinominare",
    emptyStarred: "Nessun preferito — passa il cursore su una riga e fai clic sulla stella.",
    emptyRecent: "Nessun file recente — crea o apri un file per iniziare.",
    emptyFiltered: "Nessun file di questo tipo.",
    deleteModalTitle: "Elimina file",
    deleteConfirmOne: 'Spostare "{name}" nel cestino?',
    deleteConfirmMany: "Spostare questi {n} file nel cestino?",
    deleteMoreCount: "… {n} in totale",
    delete: "Elimina",
    projects: "Progetti",
    newProject: "Nuovo progetto",
    projectName: "Nome del progetto",
    deleteProject: "Elimina progetto…",
    deleteProjectConfirm: "Eliminare questo progetto?\nI suoi file torneranno nel progetto predefinito e non andranno persi.",
    projMoreActions: "Altre azioni per {name}",
    defaultProject: "Progetto predefinito",
    projEmpty: "Tieni i documenti correlati in uno stesso progetto — la cronologia delle chat IA si accumula per progetto.",
    projEmptyHint: 'Crea un file oppure fai clic con il pulsante destro su un file recente e scegli "Sposta in un progetto".',
    timelineCount: "{n} elementi",
    timelineCountOne: "{n} elemento",
    timelineEmpty: "Non ci sono ancora conversazioni IA in questo progetto.",
    timelineYou: "Tu",
    timelineUserAria: "Utente",
    untitled: "Senza titolo",
    noContent: "(vuoto)",
    accountGenspark: "Account Bytedesk",
    account: "Account",
    login: "Accedi",
    loginGenspark: "Accedi con Bytedesk",
    loggedIn: "Accesso effettuato",
    loggedInGenspark: "Accesso effettuato a Bytedesk",
    waitingLogin: "In attesa dell'accesso nel browser… Fai clic per riaprire la pagina di accesso",
    waitingShort: "In attesa…",
    loginTimeout: "Accesso scaduto — fai clic per riprovare",
    loginLaunchFailed: "Impossibile avviare l'accesso — fai clic per riprovare",
    loginOpenManually: "Il browser non si è aperto? Fai clic per aprirlo manualmente",
    loginOpenShort: "Apri la pagina di accesso",
    loginCopyUrl: "Copia il link di accesso",
    loginCopied: "Copiato",
    loginNetworkError: "Impossibile raggiungere Bytedesk — controlla la rete o le impostazioni del proxy",
    loginExpired: "Autorizzazione scaduta — fai clic per riprovare",
    loginFailed: "Accesso non riuscito — fai clic per riprovare",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Disconnessione…",
    logout: "Esci",
    credits: "Crediti",
    creditsTip: "Vedi i dettagli sull’uso dei crediti",
    appVersion: "Versione {v}",
    versionLabel: "Versione",
    updateChannel: "Canale di aggiornamento",
    channelStable: "Stabile",
    channelBeta: "Beta",
    theme: "Tema",
    themeLight: "Chiaro",
    themeDark: "Scuro",
    themeSystem: "Segui il sistema",
    saveLocation: "Posizione di salvataggio",
    setAnalytics: "Invia statistiche di utilizzo anonime",
    setAnalyticsDesc: "Attivo per impostazione predefinita e disattivabile in Impostazioni → Generali. Utilizza Google Analytics 4; Google riceve l'IP pubblico e i metadati di trasporto, ma mai contenuti o nomi dei file.",
    settings: "Impostazioni",
    setSecAccount: "Account",
    setSecGeneral: "Generale",
    setSecAbout: "Informazioni",
    setSecAiModel: "Modello IA",
    setAiProvider: "Provider",
    setAiModelId: "Modello",
    setAiApiKey: "Chiave API",
    setAiKeyHint: "Salvata solo su questo dispositivo.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Lascia vuoto per l'endpoint ufficiale.",
    setAiGensparkHint: "Usa il tuo accesso Bytedesk; nessuna chiave API richiesta.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Le chat usano la tua chiave; gli strumenti cloud (ricerca web, generazione immagini) richiedono comunque l'accesso Bytedesk.",
    setAiSave: "Salva",
    setAiSaved: "Salvato",
    setAiTest: "Prova connessione",
    setAiTesting: "Test in corso…",
    setAiTestOk: "Connessione riuscita",
    setAiTestFail: "Connessione non riuscita",
    setAiMaxTokens: "Token di output massimi",
    setAiMaxTokensDesc: "Budget di uscita per singolo turno. I modelli di ragionamento lo consumano pensando: se si esaurisce, la risposta arriva vuota; aumentalo.",
    setAiGskTools: "Strumenti cloud Bytedesk",
    setAiGskToolsDesc: "Ricerca web, generazione di immagini e analisi dei media passano da Bytedesk e consumano crediti; se disattivato, gli strumenti immagine non sono disponibili e la ricerca usa fonti gratuite.",
    setGithub: "Open source",
    starOnGitHub: "Metti una stella su GitHub",
    starPromptTitle: "Ti piace Bytedesk Office?",
    starPromptTitleN: "Hai aperto {n} documenti con Bytedesk Office",
    starPromptBody: "Bytedesk Office è gratuito e open source. Una stella su GitHub è il modo migliore per sostenere il team.",
    starPromptGo: "Metti una stella",
    starPromptDone: "Già fatto",
    starPromptLater: "Più tardi",
    onbStarHint: "Se ti piace Bytedesk Office, lasciaci una stella su GitHub.",
    setEmail: "Email",
    setServer: "Server",
    setNotLoggedIn: "Non connesso",
    setViewUsage: "Vedi utilizzo",
    setChange: "Cambia",
    today: "Oggi",
    yesterday: "Ieri",
    daysAgo: "{n} giorni fa",
    language: "Lingua",
    closeTab: "Chiudi scheda",
    tabSync: "Sincronizza",
    tabSyncing: "Sincronizzazione…",
    tabSyncDone: "Sincronizzato",
    tabSyncFailed: "Sincronizzazione non riuscita",
    tabSyncNeedLogin: "Prima accedi",
    tabShare: "Condividi",
    tabShareDone: "Link copiato",
    tabList: "Tutte le schede",
    newTab: "Nuova scheda",
    // First-run onboarding
    onbTitle1: "Benvenuto in Bytedesk Office",
    onbSubtitle1: "La prima suite per ufficio open source e nativa per l’IA",
    onbBody1: "Crea documenti, fogli di calcolo e presentazioni e rivedi i PDF. L’IA è integrata in ogni passaggio.",
    onbTitle2: "Questo è solo l’inizio",
    onbBody2: "Bytedesk Office è ancora in alpha. Unisciti alla chat di gruppo su Bytedesk Community per condividere feedback e contribuire a plasmare il futuro.",
    onbCredits: "I collaboratori attivi ricevono **1.000+ crediti Bytedesk**",
    onbJoinGenTeam: "Unisciti a Bytedesk Community",
    onbSkip: "Salta",
    onbNext: "Avanti",
    onbStart: "Inizia",
    onbStepAria: "Pagina {n} di {total}",
    onbTitle3: "Gratuito per tutti",
    onbBody3: "Nessuna licenza. Nessuna pubblicità. Nessuna filigrana.",
    onbNote3: "Le funzioni IA possono consumare crediti Bytedesk.",
    onbBack: "Indietro"
  },
  pl: {
    navCollapseSidebar: "Zwiń panel boczny",
    navExpandSidebar: "Rozwiń panel boczny",
    navRecent: "Ostatnie",
    navStarred: "Ulubione",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Projekty utworzone w sieci za pomocą Bytedesk AI. Edycja jest kontynuowana w przeglądarce — kliknij projekt, aby go otworzyć.",
    cloudSearchPlaceholder: "Szukaj wśród {n} projektów…",
    cloudNoResults: "Brak pasujących projektów.",
    cloudGroupThisWeek: "W tym tygodniu",
    cloudGroupThisMonth: "Wcześniej w tym miesiącu",
    cloudSortLabel: "Sortuj: {v}",
    cloudSortRecent: "Najnowsze",
    cloudSortOldest: "Najstarsze",
    cloudRefresh: "Odśwież",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Zaloguj się na konto Bytedesk, aby zobaczyć projekty utworzone w sieci.",
    cloudEmpty: "Brak projektów w sieci.",
    cloudError: "Nie udało się wczytać. Spróbuj ponownie później.",
    cloudRetry: "Spróbuj ponownie",
    cloudLoadMore: "Wczytaj więcej",
    cloudOpenInBrowser: "Otwórz w przeglądarce",
    cloudCopyLink: "Kopiuj link",
    navTrash: "Kosz",
    navTrashTip: "Usunięte pliki trafiają do systemowego kosza i można je stamtąd przywrócić",
    secQuickStart: "Szybki start",
    secRecent: "Ostatnie",
    secStarred: "Ulubione",
    secProjectFiles: "Pliki projektu",
    secActivity: "Aktywność",
    colName: "Nazwa",
    colLocation: "Lokalizacja",
    colModified: "Zmodyfikowano",
    colSize: "Rozmiar",
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Otwórz plik lokalny",
    dropToOpenTitle: "Upuść, aby otworzyć",
    greetMorning: "Dzień dobry",
    greetAfternoon: "Dzień dobry",
    greetEvening: "Dobry wieczór",
    greetAsk1: "Co chcesz dziś stworzyć?",
    greetAsk2: "Gotowy, aby zacząć?",
    greetAsk3: "Nad czym pracujesz?",
    greetAsk4: "Od czego dziś zaczniemy?",
    greetAsk5: "Masz ochotę stworzyć coś nowego?",
    greetAsk6: "Masz jakiś pomysł?",
    filterAll: "Wszystkie",
    filterDocs: "Dokumenty",
    filterSheets: "Arkusze",
    filterSlides: "Prezentacje",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Filtruj według typu",
    fileCount: "Pliki: {n}",
    fileCountOne: "{n} plik",
    selectedCount: "Wybrano: {n}",
    selectAll: "Zaznacz wszystko",
    selectFile: "Zaznacz {name}",
    removeFromList: "Usuń z listy",
    missingFileTitle: "Plik niedostępny",
    missingFileBody: "Nie można znaleźć pliku „{name}”. Może znajdować się na odłączonym dysku albo został przeniesiony lub usunięty. Usunąć go z listy?",
    deleteFiles: "Usuń pliki",
    cancel: "Anuluj",
    star: "Dodaj do ulubionych",
    unstar: "Usuń z ulubionych",
    moreActions: "Więcej działań",
    open: "Otwórz",
    revealInFolder: "Pokaż w folderze",
    copyPath: "Kopiuj ścieżkę",
    moveToProject: "Przenieś do projektu",
    rename: "Zmień nazwę",
    duplicate: "Utwórz kopię",
    renameFailed: "Nie udało się zmienić nazwy",
    emptyStarred: "Brak ulubionych — najedź kursorem na wiersz pliku i kliknij gwiazdkę.",
    emptyRecent: "Brak ostatnich plików — utwórz lub otwórz plik, aby rozpocząć.",
    emptyFiltered: "Brak plików tego typu.",
    deleteModalTitle: "Usuwanie plików",
    deleteConfirmOne: "Przenieść „{name}” do kosza?",
    deleteConfirmMany: "Przenieść te pliki ({n}) do kosza?",
    deleteMoreCount: "… łącznie {n}",
    delete: "Usuń",
    projects: "Projekty",
    newProject: "Nowy projekt",
    projectName: "Nazwa projektu",
    deleteProject: "Usuń projekt…",
    deleteProjectConfirm: "Usunąć ten projekt?\nJego pliki wrócą do projektu domyślnego i nie zostaną utracone.",
    projMoreActions: "Więcej działań dla {name}",
    defaultProject: "Projekt domyślny",
    projEmpty: "Trzymaj powiązane dokumenty w jednym projekcie — historia rozmów z AI gromadzi się dla każdego projektu.",
    projEmptyHint: "Utwórz plik lub kliknij ostatni plik prawym przyciskiem myszy i wybierz „Przenieś do projektu”.",
    timelineCount: "Wpisy: {n}",
    timelineCountOne: "{n} wpis",
    timelineEmpty: "W tym projekcie nie ma jeszcze rozmów z AI.",
    timelineYou: "Ty",
    timelineUserAria: "Użytkownik",
    untitled: "Bez tytułu",
    noContent: "(pusto)",
    accountGenspark: "Konto Bytedesk",
    account: "Konto",
    login: "Zaloguj się",
    loginGenspark: "Zaloguj się przez Bytedesk",
    loggedIn: "Zalogowano",
    loggedInGenspark: "Zalogowano do Bytedesk",
    waitingLogin: "Oczekiwanie na logowanie w przeglądarce… Kliknij, aby ponownie otworzyć stronę logowania",
    waitingShort: "Oczekiwanie…",
    loginTimeout: "Upłynął limit czasu logowania — kliknij, aby spróbować ponownie",
    loginLaunchFailed: "Nie udało się uruchomić logowania — kliknij, aby spróbować ponownie",
    loginOpenManually: "Przeglądarka się nie otworzyła? Kliknij, aby otworzyć ręcznie",
    loginOpenShort: "Otwórz stronę logowania ręcznie",
    loginCopyUrl: "Kopiuj link logowania",
    loginCopied: "Skopiowano",
    loginNetworkError: "Nie można połączyć się z Bytedesk — sprawdź sieć lub ustawienia proxy",
    loginExpired: "Autoryzacja wygasła — kliknij, aby spróbować ponownie",
    loginFailed: "Logowanie nie powiodło się — kliknij, aby spróbować ponownie",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Wylogowywanie…",
    logout: "Wyloguj się",
    credits: "Kredyty",
    creditsTip: "Zobacz szczegóły zużycia kredytów",
    appVersion: "Wersja {v}",
    versionLabel: "Wersja",
    updateChannel: "Kanał aktualizacji",
    channelStable: "Stabilny",
    channelBeta: "Beta",
    theme: "Motyw",
    themeLight: "Jasny",
    themeDark: "Ciemny",
    themeSystem: "Zgodnie z systemem",
    saveLocation: "Lokalizacja zapisu",
    setAnalytics: "Wysyłaj anonimowe statystyki użytkowania",
    setAnalyticsDesc: "Domyślnie włączone; można wyłączyć w Ustawienia → Ogólne. Korzysta z Google Analytics 4; Google otrzymuje publiczny adres IP i metadane transportowe, ale nigdy treść dokumentów ani nazwy plików.",
    settings: "Ustawienia",
    setSecAccount: "Konto",
    setSecGeneral: "Ogólne",
    setSecAbout: "O aplikacji",
    setSecAiModel: "Model AI",
    setAiProvider: "Dostawca",
    setAiModelId: "Model",
    setAiApiKey: "Klucz API",
    setAiKeyHint: "Przechowywany tylko na tym urządzeniu.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Pozostaw puste, aby użyć oficjalnego punktu końcowego.",
    setAiGensparkHint: "Korzysta z logowania Bytedesk; klucz API nie jest potrzebny.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Czaty używają Twojego klucza; narzędzia w chmurze (wyszukiwanie, generowanie obrazów) nadal wymagają logowania Bytedesk.",
    setAiSave: "Zapisz",
    setAiSaved: "Zapisano",
    setAiTest: "Testuj połączenie",
    setAiTesting: "Testowanie…",
    setAiTestOk: "Połączenie działa",
    setAiTestFail: "Połączenie nie powiodło się",
    setAiMaxTokens: "Maks. tokeny wyjścia",
    setAiMaxTokensDesc: "Budżet wyjścia na jedną turę. Modele rozumowania zużywają go na myślenie; gdy się wyczerpie, odpowiedź przychodzi pusta — zwiększ tę wartość.",
    setAiGskTools: "Narzędzia chmurowe Bytedesk",
    setAiGskToolsDesc: "Wyszukiwanie w sieci, generowanie obrazów AI i analiza mediów przechodzą przez Bytedesk i zużywają kredyty; po wyłączeniu narzędzia obrazów są niedostępne, a wyszukiwanie korzysta z darmowych źródeł.",
    setGithub: "Open source",
    starOnGitHub: "Gwiazdka na GitHubie",
    starPromptTitle: "Podoba Ci się Bytedesk Office?",
    starPromptTitleN: "Otwarto już {n} dokumentów w Bytedesk Office",
    starPromptBody: "Bytedesk Office jest darmowy i open source. Gwiazdka na GitHubie to najlepsze wsparcie dla zespołu.",
    starPromptGo: "Daj gwiazdkę",
    starPromptDone: "Już zrobione",
    starPromptLater: "Później",
    onbStarHint: "Jeśli podoba Ci się Bytedesk Office, zostaw nam gwiazdkę na GitHubie.",
    setEmail: "E-mail",
    setServer: "Server",
    setNotLoggedIn: "Nie zalogowano",
    setViewUsage: "Zobacz zużycie",
    setChange: "Zmień",
    today: "Dzisiaj",
    yesterday: "Wczoraj",
    daysAgo: "{n} dni temu",
    language: "Język",
    closeTab: "Zamknij kartę",
    tabSync: "Synchronizuj",
    tabSyncing: "Synchronizowanie…",
    tabSyncDone: "Zsynchronizowano",
    tabSyncFailed: "Synchronizacja nie powiodła się",
    tabSyncNeedLogin: "Najpierw się zaloguj",
    tabShare: "Udostępnij",
    tabShareDone: "Skopiowano link",
    tabList: "Wszystkie karty",
    newTab: "Nowa karta",
    // First-run onboarding
    onbTitle1: "Witamy w Bytedesk Office",
    onbSubtitle1: "Pierwszy otwartoźródłowy, natywnie oparty na AI pakiet biurowy",
    onbBody1: "Twórz dokumenty, arkusze i prezentacje oraz przeglądaj pliki PDF. AI jest wbudowana w każdy etap.",
    onbTitle2: "To dopiero początek",
    onbBody2: "Bytedesk Office jest wciąż w fazie alfa. Dołącz do czatu grupowego na Bytedesk Community, aby dzielić się opiniami i współtworzyć jego przyszłość.",
    onbCredits: "Aktywni współtwórcy otrzymują **1000+ kredytów Bytedesk**",
    onbJoinGenTeam: "Dołącz do Bytedesk Community",
    onbSkip: "Pomiń",
    onbNext: "Dalej",
    onbStart: "Rozpocznij",
    onbStepAria: "Strona {n} z {total}",
    onbTitle3: "Za darmo dla każdego",
    onbBody3: "Bez opłat licencyjnych. Bez reklam. Bez znaków wodnych.",
    onbNote3: "Funkcje AI mogą zużywać kredyty Bytedesk.",
    onbBack: "Wstecz"
  },
  nl: {
    navCollapseSidebar: "Zijbalk inklappen",
    navExpandSidebar: "Zijbalk uitklappen",
    navRecent: "Recent",
    navStarred: "Favorieten",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Projecten gemaakt op het web met Bytedesk AI. Bewerken gaat verder in je browser — klik op een project om het te openen.",
    cloudSearchPlaceholder: "Zoek in {n} projecten…",
    cloudNoResults: "Geen overeenkomende projecten.",
    cloudGroupThisWeek: "Deze week",
    cloudGroupThisMonth: "Eerder deze maand",
    cloudSortLabel: "Sorteren: {v}",
    cloudSortRecent: "Recent",
    cloudSortOldest: "Oudste",
    cloudRefresh: "Vernieuwen",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Log in op je Bytedesk-account om projecten te zien die je op het web hebt gemaakt.",
    cloudEmpty: "Nog geen webprojecten.",
    cloudError: "Laden mislukt. Probeer het later opnieuw.",
    cloudRetry: "Opnieuw proberen",
    cloudLoadMore: "Meer laden",
    cloudOpenInBrowser: "Openen in browser",
    cloudCopyLink: "Link kopiëren",
    navTrash: "Prullenbak",
    navTrashTip: "Verwijderde bestanden gaan naar de systeemprullenbak en kunnen daar worden hersteld",
    secQuickStart: "Snel starten",
    secRecent: "Recent",
    secStarred: "Favorieten",
    secProjectFiles: "Projectbestanden",
    secActivity: "Activiteit",
    colName: "Naam",
    colLocation: "Locatie",
    colModified: "Gewijzigd",
    colSize: "Grootte",
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Lokaal bestand openen",
    dropToOpenTitle: "Loslaten om te openen",
    greetMorning: "Goedemorgen",
    greetAfternoon: "Goedemiddag",
    greetEvening: "Goedenavond",
    greetAsk1: "Wat wil je vandaag maken?",
    greetAsk2: "Klaar om te beginnen?",
    greetAsk3: "Waar werk je aan?",
    greetAsk4: "Waar beginnen we vandaag?",
    greetAsk5: "Zin om iets nieuws te maken?",
    greetAsk6: "Al een idee?",
    filterAll: "Alle",
    filterDocs: "Documenten",
    filterSheets: "Spreadsheets",
    filterSlides: "Presentaties",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Filteren op type",
    fileCount: "{n} bestanden",
    fileCountOne: "{n} bestand",
    selectedCount: "{n} geselecteerd",
    selectAll: "Alles selecteren",
    selectFile: "{name} selecteren",
    removeFromList: "Uit lijst verwijderen",
    missingFileTitle: "Bestand niet beschikbaar",
    missingFileBody: "“{name}” is niet gevonden. Het staat mogelijk op een losgekoppelde schijf, of het is verplaatst of verwijderd. Uit de lijst verwijderen?",
    deleteFiles: "Bestanden verwijderen",
    cancel: "Annuleren",
    star: "Aan favorieten toevoegen",
    unstar: "Uit favorieten verwijderen",
    moreActions: "Meer acties",
    open: "Openen",
    revealInFolder: "Tonen in map",
    copyPath: "Pad kopiëren",
    moveToProject: "Naar project verplaatsen",
    rename: "Naam wijzigen",
    duplicate: "Dupliceren",
    renameFailed: "Naam wijzigen mislukt",
    emptyStarred: "Geen favorieten — beweeg de muis over een rij en klik op de ster.",
    emptyRecent: "Geen recente bestanden — maak of open een bestand om te beginnen.",
    emptyFiltered: "Geen bestanden van dit type.",
    deleteModalTitle: "Bestanden verwijderen",
    deleteConfirmOne: '"{name}" naar de prullenbak verplaatsen?',
    deleteConfirmMany: "Deze {n} bestanden naar de prullenbak verplaatsen?",
    deleteMoreCount: "… {n} in totaal",
    delete: "Verwijderen",
    projects: "Projecten",
    newProject: "Nieuw project",
    projectName: "Projectnaam",
    deleteProject: "Project verwijderen…",
    deleteProjectConfirm: "Dit project verwijderen?\nDe bestanden gaan terug naar het standaardproject en gaan niet verloren.",
    projMoreActions: "Meer acties voor {name}",
    defaultProject: "Standaardproject",
    projEmpty: "Bewaar gerelateerde documenten in één project — de AI-chatgeschiedenis wordt per project opgebouwd.",
    projEmptyHint: 'Maak een bestand, of klik met de rechtermuisknop op een recent bestand en kies "Naar project verplaatsen".',
    timelineCount: "{n} items",
    timelineCountOne: "{n} item",
    timelineEmpty: "Nog geen AI-gesprekken in dit project.",
    timelineYou: "Jij",
    timelineUserAria: "Gebruiker",
    untitled: "Naamloos",
    noContent: "(leeg)",
    accountGenspark: "Bytedesk-account",
    account: "Account",
    login: "Inloggen",
    loginGenspark: "Inloggen met Bytedesk",
    loggedIn: "Ingelogd",
    loggedInGenspark: "Ingelogd bij Bytedesk",
    waitingLogin: "Wachten op inloggen in de browser… Klik om de inlogpagina opnieuw te openen",
    waitingShort: "Wachten…",
    loginTimeout: "Inloggen verlopen — klik om het opnieuw te proberen",
    loginLaunchFailed: "Kan het inloggen niet starten — klik om het opnieuw te proberen",
    loginOpenManually: "Browser niet geopend? Klik om handmatig te openen",
    loginOpenShort: "Inlogpagina handmatig openen",
    loginCopyUrl: "Inloglink kopiëren",
    loginCopied: "Gekopieerd",
    loginNetworkError: "Kan Bytedesk niet bereiken — controleer uw netwerk of proxyinstellingen",
    loginExpired: "Autorisatie verlopen — klik om het opnieuw te proberen",
    loginFailed: "Inloggen mislukt — klik om het opnieuw te proberen",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Uitloggen…",
    logout: "Uitloggen",
    credits: "Credits",
    creditsTip: "Bekijk het creditverbruik",
    appVersion: "Versie {v}",
    versionLabel: "Versie",
    updateChannel: "Updatekanaal",
    channelStable: "Stabiel",
    channelBeta: "Bèta",
    theme: "Thema",
    themeLight: "Licht",
    themeDark: "Donker",
    themeSystem: "Systeem volgen",
    saveLocation: "Opslaglocatie",
    setAnalytics: "Anonieme gebruiksstatistieken verzenden",
    setAnalyticsDesc: "Standaard ingeschakeld en uit te schakelen via Instellingen → Algemeen. Gebruikt Google Analytics 4; Google ontvangt uw openbare IP en transportmetadata, maar nooit documentinhoud of bestandsnamen.",
    settings: "Instellingen",
    setSecAccount: "Account",
    setSecGeneral: "Algemeen",
    setSecAbout: "Over",
    setSecAiModel: "AI-model",
    setAiProvider: "Provider",
    setAiModelId: "Model",
    setAiApiKey: "API-sleutel",
    setAiKeyHint: "Alleen op dit apparaat opgeslagen.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Leeg laten voor het officiële eindpunt.",
    setAiGensparkHint: "Gebruikt je Bytedesk-login; geen API-sleutel nodig.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Chats gebruiken je eigen sleutel; cloudtools (webzoeken, beeldgeneratie) vereisen nog steeds de Bytedesk-login.",
    setAiSave: "Opslaan",
    setAiSaved: "Opgeslagen",
    setAiTest: "Verbinding testen",
    setAiTesting: "Testen…",
    setAiTestOk: "Verbinding geslaagd",
    setAiTestFail: "Verbinding mislukt",
    setAiMaxTokens: "Max. outputtokens",
    setAiMaxTokensDesc: "Uitvoerbudget voor één beurt. Redeneermodellen geven dit uit aan denken; is het op, dan komt een leeg antwoord terug — verhoog deze waarde.",
    setAiGskTools: "Bytedesk-cloudtools",
    setAiGskToolsDesc: "Webzoeken, AI-beeldgeneratie en media-analyse lopen via Bytedesk en verbruiken tegoed; uitgeschakeld zijn beeldtools niet beschikbaar en gebruikt zoeken gratis bronnen.",
    setGithub: "Open source",
    starOnGitHub: "Geef een ster op GitHub",
    starPromptTitle: "Bevalt Bytedesk Office?",
    starPromptTitleN: "Je hebt al {n} documenten geopend met Bytedesk Office",
    starPromptBody: "Bytedesk Office is gratis en open source. Een ster op GitHub is de beste steun voor het team.",
    starPromptGo: "Geef een ster",
    starPromptDone: "Al gedaan",
    starPromptLater: "Later",
    onbStarHint: "Als Bytedesk Office je bevalt, geef ons dan een ster op GitHub.",
    setEmail: "E-mail",
    setServer: "Server",
    setNotLoggedIn: "Niet ingelogd",
    setViewUsage: "Verbruik bekijken",
    setChange: "Wijzigen",
    today: "Vandaag",
    yesterday: "Gisteren",
    daysAgo: "{n} dagen geleden",
    language: "Taal",
    closeTab: "Tabblad sluiten",
    tabSync: "Synchroniseren",
    tabSyncing: "Synchroniseren…",
    tabSyncDone: "Gesynchroniseerd",
    tabSyncFailed: "Synchronisatie mislukt",
    tabSyncNeedLogin: "Meld je eerst aan",
    tabShare: "Delen",
    tabShareDone: "Link gekopieerd",
    tabList: "Alle tabbladen",
    newTab: "Nieuw tabblad",
    // First-run onboarding
    onbTitle1: "Welkom bij Bytedesk Office",
    onbSubtitle1: "De eerste open source, AI-native officesuite",
    onbBody1: "Maak documenten, bouw spreadsheets, maak presentaties en beoordeel PDF-bestanden. AI zit in elke stap ingebouwd.",
    onbTitle2: "Dit is nog maar het begin",
    onbBody2: "Bytedesk Office is nog in alfa. Doe mee aan de groepschat op Bytedesk Community om feedback te delen en mee te bepalen wat er komt.",
    onbCredits: "Actieve bijdragers krijgen **1.000+ Bytedesk-credits**",
    onbJoinGenTeam: "Word lid van Bytedesk Community",
    onbSkip: "Overslaan",
    onbNext: "Volgende",
    onbStart: "Aan de slag",
    onbStepAria: "Pagina {n} van {total}",
    onbTitle3: "Gratis voor iedereen",
    onbBody3: "Geen licentiekosten. Geen advertenties. Geen watermerken.",
    onbNote3: "AI-functies kunnen Bytedesk-credits verbruiken.",
    onbBack: "Terug"
  },
  ms: {
    navCollapseSidebar: "Kuncup bar sisi",
    navExpandSidebar: "Kembangkan bar sisi",
    navRecent: "Terkini",
    navStarred: "Berbintang",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Projek yang dicipta di web dengan Bytedesk AI. Penyuntingan diteruskan dalam pelayar — klik projek untuk membukanya.",
    cloudSearchPlaceholder: "Cari {n} projek…",
    cloudNoResults: "Tiada projek sepadan.",
    cloudGroupThisWeek: "Minggu ini",
    cloudGroupThisMonth: "Bulan ini",
    cloudSortLabel: "Isih: {v}",
    cloudSortRecent: "Terbaru",
    cloudSortOldest: "Terlama",
    cloudRefresh: "Muat semula",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "Log masuk ke akaun Bytedesk untuk melihat projek yang anda cipta di web.",
    cloudEmpty: "Belum ada projek web.",
    cloudError: "Gagal memuatkan. Cuba lagi kemudian.",
    cloudRetry: "Cuba lagi",
    cloudLoadMore: "Muat lagi",
    cloudOpenInBrowser: "Buka dalam pelayar",
    cloudCopyLink: "Salin pautan",
    navTrash: "Tong sampah",
    navTrashTip: "Fail yang dipadamkan akan dipindahkan ke tong sampah sistem dan boleh dipulihkan dari sana",
    secQuickStart: "Mula pantas",
    secRecent: "Terkini",
    secStarred: "Berbintang",
    secProjectFiles: "Fail projek",
    secActivity: "Aktiviti",
    colName: "Nama",
    colLocation: "Lokasi",
    colModified: "Diubah suai",
    colSize: "Saiz",
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "Buka Fail Setempat",
    dropToOpenTitle: "Lepaskan untuk membuka",
    greetMorning: "Selamat pagi",
    greetAfternoon: "Selamat tengah hari",
    greetEvening: "Selamat petang",
    greetAsk1: "Apa yang anda mahu cipta hari ini?",
    greetAsk2: "Sedia untuk bermula?",
    greetAsk3: "Apa yang anda sedang usahakan?",
    greetAsk4: "Dari mana kita mula hari ini?",
    greetAsk5: "Mahu cipta sesuatu yang baharu?",
    greetAsk6: "Ada idea?",
    filterAll: "Semua",
    filterDocs: "Dokumen",
    filterSheets: "Hamparan",
    filterSlides: "Persembahan",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "Tapis mengikut jenis",
    fileCount: "{n} fail",
    fileCountOne: "{n} fail",
    selectedCount: "{n} dipilih",
    selectAll: "Pilih semua",
    selectFile: "Pilih {name}",
    removeFromList: "Alih keluar daripada senarai",
    missingFileTitle: "Fail tidak tersedia",
    missingFileBody: "“{name}” tidak ditemui. Ia mungkin berada pada pemacu yang terputus, atau telah dipindahkan atau dipadamkan. Alih keluar daripada senarai?",
    deleteFiles: "Padam fail",
    cancel: "Batal",
    star: "Tambah bintang",
    unstar: "Buang bintang",
    moreActions: "Tindakan lain",
    open: "Buka",
    revealInFolder: "Tunjukkan dalam folder",
    copyPath: "Salin laluan",
    moveToProject: "Pindahkan ke projek",
    rename: "Namakan semula",
    duplicate: "Buat salinan",
    renameFailed: "Gagal menamakan semula",
    emptyStarred: "Belum ada fail berbintang — halakan kursor ke baris fail dan klik bintang.",
    emptyRecent: "Belum ada fail terkini — cipta atau buka fail untuk bermula.",
    emptyFiltered: "Tiada fail jenis ini.",
    deleteModalTitle: "Padam Fail",
    deleteConfirmOne: 'Pindahkan "{name}" ke tong sampah?',
    deleteConfirmMany: "Pindahkan {n} fail ini ke tong sampah?",
    deleteMoreCount: "… {n} kesemuanya",
    delete: "Padam",
    projects: "Projek",
    newProject: "Projek baharu",
    projectName: "Nama projek",
    deleteProject: "Padam projek…",
    deleteProjectConfirm: "Padam projek ini?\nFail di dalamnya akan kembali ke Projek Lalai dan tidak akan hilang.",
    projMoreActions: "Tindakan lain untuk {name}",
    defaultProject: "Projek lalai",
    projEmpty: "Simpan dokumen berkaitan dalam satu projek — sejarah perbualan AI terkumpul mengikut projek.",
    projEmptyHint: 'Cipta fail, atau klik kanan fail terkini dan pilih "Pindahkan ke projek".',
    timelineCount: "{n} item",
    timelineCountOne: "{n} item",
    timelineEmpty: "Belum ada perbualan AI dalam projek ini.",
    timelineYou: "Anda",
    timelineUserAria: "Pengguna",
    untitled: "Tanpa tajuk",
    noContent: "(kosong)",
    accountGenspark: "Akaun Bytedesk",
    account: "Akaun",
    login: "Log masuk",
    loginGenspark: "Log masuk dengan Bytedesk",
    loggedIn: "Telah log masuk",
    loggedInGenspark: "Telah log masuk ke Bytedesk",
    waitingLogin: "Menunggu log masuk dalam pelayar… Klik untuk membuka semula halaman log masuk",
    waitingShort: "Menunggu…",
    loginTimeout: "Log masuk tamat masa — klik untuk cuba lagi",
    loginLaunchFailed: "Tidak dapat memulakan log masuk — klik untuk cuba lagi",
    loginOpenManually: "Pelayar tidak terbuka? Klik untuk membuka secara manual",
    loginOpenShort: "Buka halaman log masuk secara manual",
    loginCopyUrl: "Salin pautan log masuk",
    loginCopied: "Disalin",
    loginNetworkError: "Tidak dapat menyambung ke Bytedesk — semak rangkaian atau tetapan proksi anda",
    loginExpired: "Kebenaran telah tamat tempoh — klik untuk cuba lagi",
    loginFailed: "Log masuk gagal — klik untuk cuba lagi",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "Sedang log keluar…",
    logout: "Log keluar",
    credits: "Kredit",
    creditsTip: "Lihat butiran penggunaan kredit",
    appVersion: "Versi {v}",
    versionLabel: "Versi",
    updateChannel: "Saluran Kemas Kini",
    channelStable: "Stabil",
    channelBeta: "Beta",
    theme: "Tema",
    themeLight: "Cerah",
    themeDark: "Gelap",
    themeSystem: "Ikut Sistem",
    saveLocation: "Lokasi simpanan",
    setAnalytics: "Hantar statistik penggunaan tanpa nama",
    setAnalyticsDesc: "Diaktifkan secara lalai dan boleh dimatikan di Tetapan → Umum. Menggunakan Google Analytics 4; Google menerima IP awam dan metadata pengangkutan, tetapi tidak pernah kandungan dokumen atau nama fail.",
    settings: "Tetapan",
    setSecAccount: "Akaun",
    setSecGeneral: "Umum",
    setSecAbout: "Perihal",
    setSecAiModel: "Model AI",
    setAiProvider: "Penyedia",
    setAiModelId: "Model",
    setAiApiKey: "Kunci API",
    setAiKeyHint: "Disimpan pada peranti ini sahaja.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "Biarkan kosong untuk endpoint rasmi.",
    setAiGensparkHint: "Menggunakan log masuk Bytedesk; tiada kunci API diperlukan.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "Sembang menggunakan kunci anda sendiri; alat awan (carian web, penjanaan imej) masih memerlukan log masuk Bytedesk.",
    setAiSave: "Simpan",
    setAiSaved: "Disimpan",
    setAiTest: "Uji sambungan",
    setAiTesting: "Menguji…",
    setAiTestOk: "Sambungan berjaya",
    setAiTestFail: "Sambungan gagal",
    setAiMaxTokens: "Token output maks.",
    setAiMaxTokensDesc: "Belanjawan output untuk satu pusingan. Model penaakulan menghabiskannya untuk berfikir; jika habis, balasan datang kosong — tingkatkan nilai ini.",
    setAiGskTools: "Alat awan Bytedesk",
    setAiGskToolsDesc: "Carian web, penjanaan imej AI dan analisis media melalui awan Bytedesk dan menggunakan kredit; jika dimatikan, alat imej tidak tersedia dan carian menggunakan sumber percuma.",
    setGithub: "Sumber Terbuka",
    starOnGitHub: "Beri Bintang di GitHub",
    starPromptTitle: "Suka Bytedesk Office?",
    starPromptTitleN: "Anda telah membuka {n} dokumen dengan Bytedesk Office",
    starPromptBody: "Bytedesk Office percuma dan sumber terbuka. Bintang di GitHub ialah sokongan terbaik untuk pasukan kami.",
    starPromptGo: "Beri Bintang",
    starPromptDone: "Sudah",
    starPromptLater: "Kemudian",
    onbStarHint: "Jika anda suka Bytedesk Office, beri kami bintang di GitHub.",
    setEmail: "E-mel",
    setServer: "Server",
    setNotLoggedIn: "Belum log masuk",
    setViewUsage: "Lihat penggunaan",
    setChange: "Tukar",
    today: "Hari ini",
    yesterday: "Semalam",
    daysAgo: "{n} hari lalu",
    language: "Bahasa",
    closeTab: "Tutup tab",
    tabSync: "Segerak",
    tabSyncing: "Menyegerakkan…",
    tabSyncDone: "Disegerakkan",
    tabSyncFailed: "Segerakan gagal",
    tabSyncNeedLogin: "Log masuk dahulu",
    tabShare: "Kongsi",
    tabShareDone: "Pautan disalin",
    tabList: "Semua tab",
    newTab: "Tab baharu",
    // First-run onboarding
    onbTitle1: "Selamat datang ke Bytedesk Office",
    onbSubtitle1: "Suite pejabat sumber terbuka natif AI yang pertama",
    onbBody1: "Cipta dokumen, bina hamparan, hasilkan persembahan dan semak PDF. AI tersedia pada setiap langkah.",
    onbTitle2: "Ini baru permulaan",
    onbBody2: "Bytedesk Office masih dalam peringkat alfa. Sertai sembang kumpulan di Bytedesk Community untuk berkongsi maklum balas dan membentuk masa depannya.",
    onbCredits: "Penyumbang aktif menerima **1,000+ kredit Bytedesk**",
    onbJoinGenTeam: "Sertai Bytedesk Community",
    onbSkip: "Langkau",
    onbNext: "Seterusnya",
    onbStart: "Mula",
    onbStepAria: "Halaman {n} daripada {total}",
    onbTitle3: "Percuma untuk semua",
    onbBody3: "Tiada yuran lesen. Tiada iklan. Tiada tera air.",
    onbNote3: "Ciri AI mungkin menggunakan kredit Bytedesk.",
    onbBack: "Kembali"
  },
  he: {
    navCollapseSidebar: "כווץ את סרגל הצד",
    navExpandSidebar: "הרחב את סרגל הצד",
    navRecent: "אחרונים",
    navStarred: "מועדפים",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "פרויקטים שנוצרו באינטרנט עם Bytedesk AI. העריכה נמשכת בדפדפן — לחצו על פרויקט כדי לפתוח אותו.",
    cloudSearchPlaceholder: "חיפוש בין {n} פרויקטים…",
    cloudNoResults: "אין פרויקטים תואמים.",
    cloudGroupThisWeek: "השבוע",
    cloudGroupThisMonth: "מוקדם יותר החודש",
    cloudSortLabel: "מיון: {v}",
    cloudSortRecent: "החדשים ביותר",
    cloudSortOldest: "הישנים ביותר",
    cloudRefresh: "רענון",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "התחברו לחשבון Bytedesk כדי לראות פרויקטים שיצרתם באתר.",
    cloudEmpty: "אין עדיין פרויקטים מהאתר.",
    cloudError: "הטעינה נכשלה. נסו שוב מאוחר יותר.",
    cloudRetry: "נסו שוב",
    cloudLoadMore: "טענו עוד",
    cloudOpenInBrowser: "פתיחה בדפדפן",
    cloudCopyLink: "העתק קישור",
    navTrash: "אשפה",
    navTrashTip: "קבצים שנמחקו עוברים לאשפה של המערכת וניתן לשחזר אותם משם",
    secQuickStart: "התחלה מהירה",
    secRecent: "אחרונים",
    secStarred: "מועדפים",
    secProjectFiles: "קובצי הפרויקט",
    secActivity: "פעילות",
    colName: "שם",
    colLocation: "מיקום",
    colModified: "שונה",
    colSize: "גודל",
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "פתיחת קובץ מקומי",
    dropToOpenTitle: "שחרר כדי לפתוח",
    greetMorning: "בוקר טוב",
    greetAfternoon: "צהריים טובים",
    greetEvening: "ערב טוב",
    greetAsk1: "מה תרצו ליצור היום?",
    greetAsk2: "מוכנים להתחיל?",
    greetAsk3: "על מה אתם עובדים?",
    greetAsk4: "מאיפה נתחיל היום?",
    greetAsk5: "רוצים ליצור משהו חדש?",
    greetAsk6: "יש לכם רעיון?",
    filterAll: "הכול",
    filterDocs: "מסמכים",
    filterSheets: "גיליונות",
    filterSlides: "מצגות",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "סינון לפי סוג",
    fileCount: "{n} קבצים",
    fileCountOne: "קובץ אחד",
    selectedCount: "נבחרו {n}",
    selectAll: "בחירת הכול",
    selectFile: "בחירת {name}",
    removeFromList: "הסרה מהרשימה",
    missingFileTitle: "הקובץ אינו זמין",
    missingFileBody: "לא ניתן למצוא את “{name}”. ייתכן שהוא נמצא בכונן מנותק, או שהועבר או נמחק. להסיר אותו מהרשימה?",
    deleteFiles: "מחיקת קבצים",
    cancel: "ביטול",
    star: "הוספה למועדפים",
    unstar: "הסרה מהמועדפים",
    moreActions: "פעולות נוספות",
    open: "פתיחה",
    revealInFolder: "הצגה בתיקייה",
    copyPath: "העתקת נתיב",
    moveToProject: "העברה לפרויקט",
    rename: "שינוי שם",
    duplicate: "יצירת עותק",
    renameFailed: "שינוי השם נכשל",
    emptyStarred: "אין מועדפים עדיין — רחפו מעל שורת קובץ ולחצו על הכוכב.",
    emptyRecent: "אין קבצים אחרונים — צרו או פתחו קובץ כדי להתחיל.",
    emptyFiltered: "אין קבצים מסוג זה.",
    deleteModalTitle: "מחיקת קבצים",
    deleteConfirmOne: 'להעביר את "{name}" לאשפה?',
    deleteConfirmMany: "להעביר את {n} הקבצים האלה לאשפה?",
    deleteMoreCount: "… {n} בסך הכול",
    delete: "מחיקה",
    projects: "פרויקטים",
    newProject: "פרויקט חדש",
    projectName: "שם הפרויקט",
    deleteProject: "מחיקת פרויקט…",
    deleteProjectConfirm: 'למחוק את הפרויקט הזה?\nהקבצים שבו יחזרו ל"פרויקט ברירת המחדל" ולא יאבדו.',
    projMoreActions: "פעולות נוספות עבור {name}",
    defaultProject: "פרויקט ברירת מחדל",
    projEmpty: "שמרו מסמכים קשורים באותו פרויקט — היסטוריית שיחות ה-AI נצברת לפי פרויקט.",
    projEmptyHint: 'צרו קובץ, או לחצו לחיצה ימנית על קובץ אחרון ובחרו "העברה לפרויקט".',
    timelineCount: "{n} פריטים",
    timelineCountOne: "פריט אחד",
    timelineEmpty: "אין עדיין שיחות AI בפרויקט הזה.",
    timelineYou: "את/ה",
    timelineUserAria: "משתמש",
    untitled: "ללא שם",
    noContent: "(ריק)",
    accountGenspark: "חשבון Bytedesk",
    account: "חשבון",
    login: "התחברות",
    loginGenspark: "התחברות עם Bytedesk",
    loggedIn: "מחובר",
    loggedInGenspark: "מחובר ל-Bytedesk",
    waitingLogin: "ממתין להתחברות בדפדפן… לחצו כדי לפתוח שוב את דף ההתחברות",
    waitingShort: "ממתין…",
    loginTimeout: "פג תוקף ההתחברות — לחצו כדי לנסות שוב",
    loginLaunchFailed: "לא ניתן להתחיל את ההתחברות — לחצו כדי לנסות שוב",
    loginOpenManually: "הדפדפן לא נפתח? לחצו כאן לפתיחה ידנית",
    loginOpenShort: "פתיחת דף ההתחברות ידנית",
    loginCopyUrl: "העתקת קישור ההתחברות",
    loginCopied: "הועתק",
    loginNetworkError: "לא ניתן להתחבר ל-Bytedesk — בדקו את הרשת או את הגדרות ה-proxy",
    loginExpired: "תוקף ההרשאה פג — לחצו כדי לנסות שוב",
    loginFailed: "ההתחברות נכשלה — לחצו כדי לנסות שוב",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "מתנתק…",
    logout: "התנתקות",
    credits: "קרדיטים",
    creditsTip: "הצגת פרטי השימוש בקרדיטים",
    appVersion: "גרסה {v}",
    versionLabel: "גרסה",
    updateChannel: "ערוץ עדכונים",
    channelStable: "יציב",
    channelBeta: "בטא",
    theme: "ערכת נושא",
    themeLight: "בהיר",
    themeDark: "כהה",
    themeSystem: "עקוב אחר המערכת",
    saveLocation: "מיקום שמירה",
    setAnalytics: "שליחת נתוני שימוש אנונימיים",
    setAnalyticsDesc: "מופעל כברירת מחדל וניתן לכיבוי בהגדרות ← כללי. משתמש ב-Google Analytics 4; Google מקבלת IP ציבורי ומטא-נתוני תעבורה, אך לא תוכן מסמכים או שמות קבצים.",
    settings: "הגדרות",
    setSecAccount: "חשבון",
    setSecGeneral: "כללי",
    setSecAbout: "אודות",
    setSecAiModel: "מודל AI",
    setAiProvider: "ספק",
    setAiModelId: "מודל",
    setAiApiKey: "מפתח API",
    setAiKeyHint: "נשמר רק במכשיר זה.",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "השאר ריק לנקודת הקצה הרשמית.",
    setAiGensparkHint: "משתמש בכניסת Bytedesk שלך; אין צורך במפתח API.",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "שיחות משתמשות במפתח שלך; כלי ענן (חיפוש ברשת, יצירת תמונות) עדיין דורשים כניסת Bytedesk.",
    setAiSave: "שמירה",
    setAiSaved: "נשמר",
    setAiTest: "בדיקת חיבור",
    setAiTesting: "בודק…",
    setAiTestOk: "החיבור תקין",
    setAiTestFail: "החיבור נכשל",
    setAiMaxTokens: "מקסימום טוקנים לתשובה",
    setAiMaxTokensDesc: "תקן פלט לסיבוב אחד. מודלי נימוק מבזבזים אותו על מחשבה, ואם הוא נגמר התשובה חוזרת ריקה — העלו את הערך.",
    setAiGskTools: "כלי הענן של Bytedesk",
    setAiGskToolsDesc: "חיפוש ברשת, יצירת תמונות וניתוח מדיה עוברים דרך Bytedesk וצורכים קרדיטים; בכיבוי, כלי התמונות אינם זמינים והחיפוש משתמש במקורות חינמיים.",
    setGithub: "קוד פתוח",
    starOnGitHub: "תנו כוכב ב-GitHub",
    starPromptTitle: "נהנים מ-Bytedesk Office?",
    starPromptTitleN: "פתחת {n} מסמכים עם Bytedesk Office",
    starPromptBody: "Bytedesk Office חינמי ובקוד פתוח. כוכב ב-GitHub הוא התמיכה הטובה ביותר בצוות.",
    starPromptGo: "לתת כוכב",
    starPromptDone: "כבר נתתי",
    starPromptLater: "אחר כך",
    onbStarHint: "אם Bytedesk Office מוצא חן בעיניכם, תנו לנו כוכב ב-GitHub.",
    setEmail: "אימייל",
    setServer: "Server",
    setNotLoggedIn: "לא מחובר",
    setViewUsage: "הצגת שימוש",
    setChange: "שינוי",
    today: "היום",
    yesterday: "אתמול",
    daysAgo: "לפני {n} ימים",
    language: "שפה",
    closeTab: "סגירת כרטיסייה",
    tabSync: "סנכרון",
    tabSyncing: "מסנכרן…",
    tabSyncDone: "סונכרן",
    tabSyncFailed: "הסנכרון נכשל",
    tabSyncNeedLogin: "היכנס קודם",
    tabShare: "שיתוף",
    tabShareDone: "הקישור הועתק",
    tabList: "כל הכרטיסיות",
    newTab: "כרטיסייה חדשה",
    // First-run onboarding
    onbTitle1: "ברוכים הבאים ל-Bytedesk Office",
    onbSubtitle1: "חבילת המשרד הראשונה בקוד פתוח שהיא AI-נייטיב",
    onbBody1: "צרו מסמכים, בנו גיליונות, הכינו מצגות ובדקו קובצי PDF. ה-AI מובנה בכל שלב.",
    onbTitle2: "זו רק ההתחלה",
    onbBody2: "Bytedesk Office עדיין בגרסת אלפא. הצטרפו לצ׳אט הקבוצתי ב-Bytedesk Community כדי לשתף משוב ולעזור לעצב את ההמשך.",
    onbCredits: "תורמים פעילים מקבלים **1,000+ נקודות Bytedesk**",
    onbJoinGenTeam: "הצטרפו ל-Bytedesk Community",
    onbSkip: "דילוג",
    onbNext: "הבא",
    onbStart: "להתחיל",
    onbStepAria: "עמוד {n} מתוך {total}",
    onbTitle3: "חינם לכולם",
    onbBody3: "ללא דמי רישיון, ללא פרסומות, ללא סימני מים.",
    onbNote3: "תכונות AI עשויות לצרוך קרדיטים של Bytedesk.",
    onbBack: "חזרה"
  },
  hi: {
    navCollapseSidebar: "साइडबार सिकोड़ें",
    navExpandSidebar: "साइडबार विस्तारित करें",
    navRecent: "हाल के",
    navStarred: "तारांकित",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "Bytedesk AI के साथ वेब पर बनाए गए प्रोजेक्ट। संपादन ब्राउज़र में जारी रहता है — खोलने के लिए किसी प्रोजेक्ट पर क्लिक करें।",
    cloudSearchPlaceholder: "{n} प्रोजेक्ट खोजें…",
    cloudNoResults: "कोई मिलान वाला प्रोजेक्ट नहीं।",
    cloudGroupThisWeek: "इस सप्ताह",
    cloudGroupThisMonth: "इस महीने",
    cloudSortLabel: "क्रम: {v}",
    cloudSortRecent: "हाल के",
    cloudSortOldest: "सबसे पुराने",
    cloudRefresh: "रीफ़्रेश",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "वेब पर बनाए गए प्रोजेक्ट देखने के लिए अपने Bytedesk खाते में साइन इन करें।",
    cloudEmpty: "अभी तक कोई वेब प्रोजेक्ट नहीं है।",
    cloudError: "लोड नहीं हो सका। बाद में फिर से कोशिश करें।",
    cloudRetry: "फिर से कोशिश करें",
    cloudLoadMore: "और लोड करें",
    cloudOpenInBrowser: "ब्राउज़र में खोलें",
    cloudCopyLink: "लिंक कॉपी करें",
    navTrash: "ट्रैश",
    navTrashTip: "हटाई गई फ़ाइलें सिस्टम ट्रैश में जाती हैं और वहाँ से पुनर्स्थापित की जा सकती हैं",
    secQuickStart: "त्वरित प्रारंभ",
    secRecent: "हाल के",
    secStarred: "तारांकित",
    secProjectFiles: "प्रोजेक्ट फ़ाइलें",
    secActivity: "गतिविधि",
    colName: "नाम",
    colLocation: "स्थान",
    colModified: "संशोधित",
    colSize: "आकार",
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "स्थानीय फ़ाइल खोलें",
    dropToOpenTitle: "खोलने के लिए छोड़ें",
    greetMorning: "सुप्रभात",
    greetAfternoon: "नमस्ते",
    greetEvening: "शुभ संध्या",
    greetAsk1: "आज आप क्या बनाना चाहेंगे?",
    greetAsk2: "शुरू करने के लिए तैयार?",
    greetAsk3: "आप किस पर काम कर रहे हैं?",
    greetAsk4: "आज कहाँ से शुरू करें?",
    greetAsk5: "कुछ नया बनाना चाहेंगे?",
    greetAsk6: "कोई आइडिया है?",
    filterAll: "सभी",
    filterDocs: "दस्तावेज़",
    filterSheets: "स्प्रेडशीट",
    filterSlides: "प्रस्तुतियाँ",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "प्रकार के अनुसार फ़िल्टर करें",
    fileCount: "{n} फ़ाइलें",
    fileCountOne: "{n} फ़ाइल",
    selectedCount: "{n} चयनित",
    selectAll: "सभी चुनें",
    selectFile: "{name} चुनें",
    removeFromList: "सूची से हटाएँ",
    missingFileTitle: "फ़ाइल उपलब्ध नहीं है",
    missingFileBody: "“{name}” नहीं मिली। यह किसी डिस्कनेक्ट की गई ड्राइव पर हो सकती है, या इसे स्थानांतरित या हटा दिया गया है। इसे सूची से हटाएँ?",
    deleteFiles: "फ़ाइलें हटाएँ",
    cancel: "रद्द करें",
    star: "तारांकित करें",
    unstar: "तारांकन हटाएँ",
    moreActions: "अधिक कार्रवाइयाँ",
    open: "खोलें",
    revealInFolder: "फ़ोल्डर में दिखाएँ",
    copyPath: "पथ कॉपी करें",
    moveToProject: "प्रोजेक्ट में ले जाएँ",
    rename: "नाम बदलें",
    duplicate: "प्रतिलिपि बनाएँ",
    renameFailed: "नाम बदलने में विफल",
    emptyStarred: "कोई तारांकित फ़ाइल नहीं — फ़ाइल की पंक्ति पर कर्सर ले जाएँ और तारे पर क्लिक करें।",
    emptyRecent: "कोई हालिया फ़ाइल नहीं — शुरू करने के लिए कोई फ़ाइल बनाएँ या खोलें।",
    emptyFiltered: "इस प्रकार की कोई फ़ाइल नहीं है।",
    deleteModalTitle: "फ़ाइलें हटाएँ",
    deleteConfirmOne: '"{name}" को ट्रैश में ले जाएँ?',
    deleteConfirmMany: "इन {n} फ़ाइलों को ट्रैश में ले जाएँ?",
    deleteMoreCount: "… कुल {n}",
    delete: "हटाएँ",
    projects: "प्रोजेक्ट",
    newProject: "नया प्रोजेक्ट",
    projectName: "प्रोजेक्ट का नाम",
    deleteProject: "प्रोजेक्ट हटाएँ…",
    deleteProjectConfirm: 'यह प्रोजेक्ट हटाएँ?\nइसकी फ़ाइलें "डिफ़ॉल्ट प्रोजेक्ट" में वापस चली जाएँगी और खोएँगी नहीं।',
    projMoreActions: "{name} के लिए अधिक कार्रवाइयाँ",
    defaultProject: "डिफ़ॉल्ट प्रोजेक्ट",
    projEmpty: "संबंधित दस्तावेज़ों को एक ही प्रोजेक्ट में रखें — AI चैट इतिहास प्रोजेक्ट के अनुसार जमा होता है।",
    projEmptyHint: 'कोई फ़ाइल बनाएँ, या किसी हालिया फ़ाइल पर राइट-क्लिक करके "प्रोजेक्ट में ले जाएँ" चुनें।',
    timelineCount: "{n} आइटम",
    timelineCountOne: "{n} आइटम",
    timelineEmpty: "इस प्रोजेक्ट में अभी तक कोई AI बातचीत नहीं है।",
    timelineYou: "आप",
    timelineUserAria: "उपयोगकर्ता",
    untitled: "बिना शीर्षक",
    noContent: "(खाली)",
    accountGenspark: "Bytedesk खाता",
    account: "खाता",
    login: "साइन इन करें",
    loginGenspark: "Bytedesk से साइन इन करें",
    loggedIn: "साइन इन हो गया",
    loggedInGenspark: "Bytedesk में साइन इन है",
    waitingLogin: "ब्राउज़र में साइन इन की प्रतीक्षा है… साइन इन पेज दोबारा खोलने के लिए क्लिक करें",
    waitingShort: "प्रतीक्षा में…",
    loginTimeout: "साइन इन का समय समाप्त — पुनः प्रयास के लिए क्लिक करें",
    loginLaunchFailed: "साइन इन शुरू नहीं हो सका — पुनः प्रयास के लिए क्लिक करें",
    loginOpenManually: "ब्राउज़र नहीं खुला? मैन्युअल रूप से खोलने के लिए क्लिक करें",
    loginOpenShort: "साइन इन पेज मैन्युअल रूप से खोलें",
    loginCopyUrl: "साइन इन लिंक कॉपी करें",
    loginCopied: "कॉपी हो गया",
    loginNetworkError: "Bytedesk से कनेक्ट नहीं हो सका — नेटवर्क या प्रॉक्सी सेटिंग जांचें",
    loginExpired: "प्राधिकरण की समय सीमा समाप्त — पुनः प्रयास के लिए क्लिक करें",
    loginFailed: "साइन इन विफल — पुनः प्रयास के लिए क्लिक करें",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "Username",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} Get code",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "Failed to load captcha — check the server address or click refresh",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "Invalid mobile number",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "Server settings",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "साइन आउट हो रहा है…",
    logout: "साइन आउट करें",
    credits: "क्रेडिट",
    creditsTip: "क्रेडिट उपयोग का विवरण देखें",
    appVersion: "संस्करण {v}",
    versionLabel: "संस्करण",
    updateChannel: "अपडेट चैनल",
    channelStable: "स्थिर",
    channelBeta: "बीटा",
    theme: "थीम",
    themeLight: "लाइट",
    themeDark: "डार्क",
    themeSystem: "सिस्टम का अनुसरण करें",
    saveLocation: "सहेजने का स्थान",
    setAnalytics: "गुमनाम उपयोग आँकड़े भेजें",
    setAnalyticsDesc: "डिफ़ॉल्ट रूप से चालू; सेटिंग्स → सामान्य में बंद किया जा सकता है। Google Analytics 4 का उपयोग होता है; Google को सार्वजनिक IP और ट्रांसपोर्ट मेटाडेटा मिलता है, लेकिन दस्तावेज़ सामग्री या फ़ाइल नाम नहीं।",
    settings: "सेटिंग्स",
    setSecAccount: "खाता",
    setSecGeneral: "सामान्य",
    setSecAbout: "जानकारी",
    setSecAiModel: "AI मॉडल",
    setAiProvider: "प्रदाता",
    setAiModelId: "मॉडल",
    setAiApiKey: "API कुंजी",
    setAiKeyHint: "केवल इसी डिवाइस पर संग्रहीत।",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "आधिकारिक एंडपॉइंट के लिए खाली छोड़ें।",
    setAiGensparkHint: "आपके Bytedesk साइन-इन का उपयोग करता है; API कुंजी की आवश्यकता नहीं।",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "चैट आपकी अपनी कुंजी का उपयोग करती हैं; क्लाउड टूल (वेब खोज, छवि निर्माण) के लिए अभी भी Bytedesk साइन-इन आवश्यक है।",
    setAiSave: "सहेजें",
    setAiSaved: "सहेजा गया",
    setAiTest: "कनेक्शन परखें",
    setAiTesting: "परख रहे हैं…",
    setAiTestOk: "कनेक्शन सफल",
    setAiTestFail: "कनेक्शन विफल",
    setAiMaxTokens: "अधिकतम आउटपुट टोकन",
    setAiMaxTokensDesc: "एक मोड़ का आउटपुट बजट। तरक मॉडल इसमें से क्छ हिस्सा सोचने पर खरच करते हैं; बजट खत्म होने पर उत्तर खाली आ सकता है — ज़रूरत पर इसे बढ़ाएँ।",
    setAiGskTools: "Bytedesk क्लाउड टूल",
    setAiGskToolsDesc: "वेब खोज, AI छवि निर्माण और मीडिया विश्लेषण Bytedesk क्लाउड से होते हैं और क्रेडिट खर्च करते हैं; बंद करने पर छवि टूल अनुपलब्ध होंगे और खोज मुफ्त स्रोतों का उपयोग करेगी।",
    setGithub: "ओपन सोर्स",
    starOnGitHub: "GitHub पर स्टार दें",
    starPromptTitle: "Bytedesk Office पसंद आ रहा है?",
    starPromptTitleN: "आपने Bytedesk Office में {n} दस्तावेज़ खोले हैं",
    starPromptBody: "Bytedesk Office मुफ़्त और ओपन सोर्स है। GitHub पर एक स्टार टीम का सबसे अच्छा समर्थन है।",
    starPromptGo: "स्टार दें",
    starPromptDone: "पहले ही दे दिया",
    starPromptLater: "बाद में",
    onbStarHint: "अगर आपको Bytedesk Office पसंद है, तो हमें GitHub पर स्टार दें।",
    setEmail: "ईमेल",
    setServer: "Server",
    setNotLoggedIn: "साइन इन नहीं किया गया",
    setViewUsage: "उपयोग देखें",
    setChange: "बदलें",
    today: "आज",
    yesterday: "कल",
    daysAgo: "{n} दिन पहले",
    language: "भाषा",
    closeTab: "टैब बंद करें",
    tabSync: "सिंक",
    tabSyncing: "सिंक हो रहा है…",
    tabSyncDone: "सिंक किया गया",
    tabSyncFailed: "सिंक विफल",
    tabSyncNeedLogin: "पहले साइन इन करें",
    tabShare: "साझा करें",
    tabShareDone: "लिंक कॉपी हो गया",
    tabList: "सभी टैब",
    newTab: "नया टैब",
    // First-run onboarding
    onbTitle1: "Bytedesk Office में आपका स्वागत है",
    onbSubtitle1: "पहला ओपन-सोर्स, AI-नेटिव ऑफ़िस सुइट",
    onbBody1: "दस्तावेज़ बनाएँ, स्प्रेडशीट तैयार करें, प्रस्तुतियाँ बनाएँ और PDF की समीक्षा करें। AI हर चरण में शामिल है।",
    onbTitle2: "यह तो बस शुरुआत है",
    onbBody2: "Bytedesk Office अभी अल्फ़ा में है। Bytedesk Community पर ग्रुप चैट से जुड़ें, फ़ीडबैक साझा करें और आगे की दिशा तय करने में मदद करें।",
    onbCredits: "सक्रिय योगदानकर्ताओं के लिए **1,000+ Bytedesk क्रेडिट**",
    onbJoinGenTeam: "Bytedesk Community से जुड़ें",
    onbSkip: "छोड़ें",
    onbNext: "आगे",
    onbStart: "शुरू करें",
    onbStepAria: "कुल {total} में से पृष्ठ {n}",
    onbTitle3: "सभी के लिए मुफ़्त",
    onbBody3: "कोई लाइसेंस शुल्क नहीं। कोई विज्ञापन नहीं। कोई वॉटरमार्क नहीं।",
    onbNote3: "AI सुविधाएँ Bytedesk क्रेडिट खर्च कर सकती हैं।",
    onbBack: "वापस"
  },
  "zh-TW": {
    navCollapseSidebar: "摺疊側邊欄",
    navExpandSidebar: "展開側邊欄",
    navRecent: "最近",
    navStarred: "收藏",
    navCloud: "Bytedesk Projects",
    cloudSubtitle: "在網頁端用 Bytedesk AI 建立的專案。編輯在瀏覽器中繼續——點擊任意專案即可開啟。",
    cloudSearchPlaceholder: "搜尋 {n} 個專案…",
    cloudNoResults: "沒有符合的專案。",
    cloudGroupThisWeek: "本週",
    cloudGroupThisMonth: "本月",
    cloudSortLabel: "排序：{v}",
    cloudSortRecent: "最近",
    cloudSortOldest: "最早",
    cloudRefresh: "重新整理",
    cloudUpload: "Upload",
    cloudUploadPick: "Choose a document to upload",
    cloudLoginHint: "登入 Bytedesk 帳號，查看你在網頁端建立的專案。",
    cloudEmpty: "還沒有網頁端專案。",
    cloudError: "載入失敗，請稍後再試。",
    cloudRetry: "重試",
    cloudLoadMore: "載入更多",
    cloudOpenInBrowser: "在瀏覽器中開啟",
    cloudCopyLink: "複製連結",
    navTrash: "垃圾桶",
    navTrashTip: "刪除的檔案會移至系統垃圾桶，可從那裡還原",
    secQuickStart: "快速開始",
    secRecent: "最近使用",
    secStarred: "收藏",
    secProjectFiles: "專案檔案",
    secActivity: "專案動態",
    colName: "名稱",
    colLocation: "位置",
    colModified: "修改時間",
    colSize: "大小",
    newDoc: "AI Docs",
    newSheet: "AI Sheets",
    newSlide: "AI Slides",
    newMarkdown: "AI Markdown",
    newPdf: "AI PDF",
    openLocal: "開啟本機檔案",
    dropToOpenTitle: "放開以開啟檔案",
    greetMorning: "早安",
    greetAfternoon: "午安",
    greetEvening: "晚安",
    greetAsk1: "今天想建立點什麼？",
    greetAsk2: "準備好開始了嗎？",
    greetAsk3: "有什麼想做的嗎？",
    greetAsk4: "今天從哪裡開始？",
    greetAsk5: "想做點什麼新的？",
    greetAsk6: "靈感來了嗎？",
    filterAll: "全部",
    filterDocs: "文件",
    filterSheets: "試算表",
    filterSlides: "簡報",
    filterPdf: "PDF",
    filterMd: "Markdown",
    filterAria: "依類型篩選",
    fileCount: "{n} 個檔案",
    fileCountOne: "{n} 個檔案",
    selectedCount: "已選 {n} 項",
    selectAll: "全選",
    selectFile: "選取 {name}",
    removeFromList: "從清單中移除",
    missingFileTitle: "檔案無法使用",
    missingFileBody: "找不到「{name}」。它可能位於未連接的磁碟上，或已被移動或刪除。要從清單中移除嗎？",
    deleteFiles: "刪除檔案",
    cancel: "取消",
    star: "收藏",
    unstar: "取消收藏",
    moreActions: "更多動作",
    open: "開啟",
    revealInFolder: "開啟所在資料夾",
    copyPath: "複製路徑",
    moveToProject: "移至專案",
    rename: "重新命名",
    duplicate: "建立副本",
    renameFailed: "重新命名失敗",
    emptyStarred: "還沒有收藏 — 將游標移到檔案列並點按星號即可收藏。",
    emptyRecent: "暫無最近檔案 — 新增或開啟檔案即可開始使用。",
    emptyFiltered: "此類型下暫無檔案。",
    deleteModalTitle: "刪除檔案",
    deleteConfirmOne: "確定將「{name}」移至垃圾桶？",
    deleteConfirmMany: "確定將以下 {n} 個檔案移至垃圾桶？",
    deleteMoreCount: "… 等共 {n} 個",
    delete: "刪除",
    projects: "專案",
    newProject: "新增專案",
    projectName: "專案名稱",
    deleteProject: "刪除專案…",
    deleteProjectConfirm: "刪除此專案？\n專案內的檔案會回到「預設專案」，不會遺失。",
    projMoreActions: "{name} 更多動作",
    defaultProject: "預設專案",
    projEmpty: "把相關文件放進同一個專案，AI 對話紀錄會依專案累積。",
    projEmptyHint: "新增檔案，或在「最近」檔案上按右鍵選擇「移至專案」。",
    timelineCount: "{n} 筆",
    timelineCountOne: "{n} 筆",
    timelineEmpty: "此專案還沒有 AI 對話紀錄。",
    timelineYou: "你",
    timelineUserAria: "使用者",
    untitled: "未命名",
    noContent: "（無內容）",
    accountGenspark: "Bytedesk 帳號",
    account: "帳號",
    login: "登入",
    loginGenspark: "登入 Bytedesk 帳號",
    loggedIn: "已登入",
    loggedInGenspark: "已登入 Bytedesk",
    waitingLogin: "正在等待瀏覽器登入…點按可重新開啟登入頁面",
    waitingShort: "等待登入…",
    loginTimeout: "登入逾時，點按重試",
    loginLaunchFailed: "無法啟動登入，點按重試",
    loginOpenManually: "瀏覽器沒有開啟？點此手動開啟",
    loginOpenShort: "手動開啟登入頁",
    loginCopyUrl: "複製登入連結",
    loginCopied: "已複製",
    loginNetworkError: "無法連線至 Bytedesk，請檢查網路或代理設定",
    loginExpired: "登入已過期，點按重試",
    loginFailed: "登入失敗，點按重試",
    loginTitle: "Sign in to Bytedesk",
    loginServer: "Server URL",
    loginUsername: "帳號",
    loginPassword: "Password",
    loginCaptcha: "Captcha",
    loginCaptchaRefresh: "Refresh",
    loginFormIncomplete: "Server URL, username, and password are required",
    loginTwoFactorUnsupported: "This account has two-factor authentication enabled, which is not supported here yet",
    loginBrandName: "Bytedesk Office",
    loginBrandDesc: "AI document suite; sign in to sync team documents",
    loginTabAccount: "Account & password",
    loginTabMobile: "Mobile login",
    loginMobile: "Mobile number",
    loginCountry: "Country/region",
    loginSmsCode: "SMS code",
    loginSmsGet: "Get code",
    loginSmsCountdown: "{s} 獲取驗證碼",
    loginAutoRegisterHint: "Unregistered mobile numbers are registered automatically after verification",
    loginErrCaptchaFirst: "Enter the correct captcha first",
    loginErrKaptchaLoad: "驗證碼載入失敗，請檢查伺服器位址或點擊重新整理重試",
    loginErrCaptcha: "Incorrect captcha",
    loginErrCredentials: "Incorrect username or password",
    loginErrForm: "Please complete the login form",
    loginErrMismatch: "Username and mobile number do not match",
    loginErrSms: "Failed to send the SMS code",
    loginErrDisabled: "This account has been disabled by the administrator",
    loginErrServer: "Server error, please try again later",
    loginTwoFactorMissing: "Complete SMS verification before signing in",
    loginTwoFactorMobile: "2FA mobile number",
    loginMobileRequired: "Mobile number is required",
    loginMobileInvalid: "手機號格式錯誤！",
    loginPwStrong: "Strong",
    loginPwMedium: "Medium",
    loginPwWeak: "Weak",
    loginPwVeryWeak: "Very weak",
    loginPwWeakTitle: "Password strength notice",
    loginPwWeakDesc: "Your password is weak and poses a security risk.",
    loginPwSuggestion: "Suggestion: mix upper/lowercase letters, digits, and symbols; at least 8 characters",
    loginPwWeakConfirm: "Continue signing in with the current password?",
    loginPwWeakCancel: "Cancel sign-in",
    loginPwWeakChange: "Change password",
    loginPwWeakContinue: "Continue",
    loginPrivacyAgree: "Agree to",
    loginPrivacyPolicy: " the Privacy Policy",
    loginPrivacyAnd: " and",
    loginTermsOfService: "Terms of Service",
    loginPrivacyModalTitle: "Privacy confirmation",
    loginPrivacyModalDesc: "Please read and agree to the following policies:",
    loginPrivacyModalConfirm: "Agree and continue signing in?",
    loginPrivacyAgreeAndLogin: "Agree & sign in",
    loginRegisterAccount: "Register an account",
    loginServerApply: "Connect",
    loginServerSettings: "伺服器設定",
    loginServerChange: "Change",
    loginCountryChina: "China",
    loginCountryUSA: "United States",
    loginCountryJapan: "Japan",
    loginCountryHongkong: "Hong Kong",
    loginCountryTaiwan: "Taiwan",
    loginCountryMacao: "Macao",
    loginCountryKorea: "South Korea",
    loginCountryCanada: "Canada",
    loginCountrySingapore: "Singapore",
    loginCountryMalaysia: "Malaysia",
    loginCountryThailand: "Thailand",
    loginCountryVietnam: "Vietnam",
    loginCountryPhilippines: "Philippines",
    loginCountryIndonesia: "Indonesia",
    loginCountryUK: "United Kingdom",
    loginCountryGermany: "Germany",
    loginCountryFrance: "France",
    loginCountryItaly: "Italy",
    loginCountrySpain: "Spain",
    loginCountryRussia: "Russia",
    loginCountryAustralia: "Australia",
    loginCountryNewzealand: "New Zealand",
    loggingOut: "正在登出…",
    logout: "登出",
    credits: "點數",
    creditsTip: "查看點數用量詳情",
    appVersion: "版本 {v}",
    versionLabel: "版本",
    updateChannel: "更新通道",
    channelStable: "穩定版",
    channelBeta: "Beta 版",
    theme: "主題",
    themeLight: "淺色",
    themeDark: "深色",
    themeSystem: "跟隨系統",
    saveLocation: "預設儲存位置",
    setAnalytics: "傳送匿名使用統計",
    setAnalyticsDesc: "此功能預設開啟，可隨時在「設定 → 一般」中關閉。使用 Google Analytics 4；Google 會接收您的公開 IP 位址和傳輸中繼資料，但絕不收集文件內容或檔案名稱。",
    settings: "設定",
    setSecAccount: "帳戶",
    setSecGeneral: "一般",
    setSecAbout: "關於",
    setSecAiModel: "AI 模型",
    setAiProvider: "模型服務商",
    setAiModelId: "模型",
    setAiApiKey: "API Key",
    setAiKeyHint: "金鑰僅儲存在本機。",
    setAiBaseUrl: "Base URL",
    setAiBaseUrlHint: "留空使用官方端點。",
    setAiGensparkHint: "使用 Bytedesk 帳號登入，無需 API key。",
    setAiServerUrl: "Server URL",
    setAiServerUrlHint: "Bytedesk server URL; synced automatically after sign-in",
    setAiModelOptional: "optional — the server decides",
    setAiByokNote: "對話使用你自己的 key；網頁搜尋、生圖等雲端工具仍需登入 Bytedesk。",
    setAiSave: "儲存",
    setAiSaved: "已儲存",
    setAiTest: "測試連線",
    setAiTesting: "測試中…",
    setAiTestOk: "連線成功",
    setAiTestFail: "連線失敗",
    setAiMaxTokens: "單次輸出上限（tokens）",
    setAiMaxTokensDesc: "一次回合的輸出預算。推理模型會先消耗預算用於思考，預算用畢時回覆可能變成空白，遇到此情況請調高本項。",
    setAiGskTools: "Bytedesk 雲端工具",
    setAiGskToolsDesc: "網頁搜尋、AI 生圖、媒體分析經 Bytedesk 雲端並消耗點數；關閉後生圖與媒體分析不可用，搜尋改用免費來源。",
    setGithub: "開源專案",
    starOnGitHub: "到 GitHub 給我們一顆星",
    starPromptTitle: "喜歡 Bytedesk Office 嗎？",
    starPromptTitleN: "你已經用 Bytedesk Office 開啟了 {n} 個文件",
    starPromptBody: "Bytedesk Office 是免費的開源軟體。到 GitHub 給我們一顆星，是對團隊最好的支持。",
    starPromptGo: "去給星星",
    starPromptDone: "已經給過了",
    starPromptLater: "以後再說",
    onbStarHint: "如果你喜歡 Bytedesk Office，歡迎到 GitHub 給我們一顆星。",
    setEmail: "電子郵件",
    setServer: "Server",
    setNotLoggedIn: "未登入",
    setViewUsage: "查看用量",
    setChange: "變更",
    today: "今天",
    yesterday: "昨天",
    daysAgo: "{n} 天前",
    language: "語言",
    closeTab: "關閉分頁",
    tabSync: "同步",
    tabSyncing: "同步中…",
    tabSyncDone: "已同步",
    tabSyncFailed: "同步失敗",
    tabSyncNeedLogin: "請先登入",
    tabShare: "分享",
    tabShareDone: "連結已複製",
    tabList: "全部分頁",
    newTab: "新分頁",
    // First-run onboarding
    onbTitle1: "歡迎使用 Bytedesk Office",
    onbSubtitle1: "第一個開源的 AI 原生 Office 套件",
    onbBody1: "建立文件、製作試算表、產生簡報、審閱 PDF。AI 深度融入每個環節。",
    onbTitle2: "這只是一個開始",
    onbBody2: "Bytedesk Office 目前仍在 alpha 階段。歡迎加入 Bytedesk Community 群聊，分享回饋，一起打造它的未來。",
    onbCredits: "活躍貢獻者可獲得 **1,000+ Bytedesk 點數**",
    onbJoinGenTeam: "加入 Bytedesk Community",
    onbSkip: "略過",
    onbNext: "下一步",
    onbStart: "開始使用",
    onbStepAria: "第 {n} 頁，共 {total} 頁",
    onbTitle3: "人人免費",
    onbBody3: "無授權費用，無廣告，無浮水印。",
    onbNote3: "AI 功能可能消耗 Bytedesk 點數。",
    onbBack: "上一步"
  }
};
const translate = createI18n(strings);
const LocaleContext = reactExports.createContext({ lang: "zh", setLang: () => {
} });
function LocaleProvider({ initial, children }) {
  const [lang, setLangState] = reactExports.useState(initial);
  reactExports.useEffect(() => {
    const unsubscribe = window.aiOffice?.onLanguageChanged?.((next) => {
      setLangState(next);
      document.documentElement.lang = htmlLang(next);
    });
    return () => unsubscribe?.();
  }, []);
  const value = reactExports.useMemo(
    () => ({
      lang,
      setLang: (next) => {
        setLangState(next);
        document.documentElement.lang = htmlLang(next);
        void window.aiOffice.setLanguage(next);
      }
    }),
    [lang]
  );
  return /* @__PURE__ */ jsxRuntimeExports.jsx(LocaleContext.Provider, { value, children });
}
const DATE_LOCALES = {
  zh: "zh-CN",
  en: "en-US",
  ja: "ja-JP",
  ko: "ko-KR",
  fr: "fr-FR",
  de: "de-DE",
  es: "es-ES",
  th: "th-TH",
  id: "id-ID",
  ru: "ru-RU",
  ar: "ar-SA",
  pt: "pt-BR",
  it: "it-IT",
  pl: "pl-PL",
  nl: "nl-NL",
  ms: "ms-MY",
  he: "he-IL",
  hi: "hi-IN",
  "zh-TW": "zh-TW"
};
function useI18n() {
  const { lang, setLang } = reactExports.useContext(LocaleContext);
  return {
    lang,
    setLang,
    t: (key, params) => translate(lang, key, params),
    dateLocale: DATE_LOCALES[lang]
  };
}
var define_process_env_default = {};
const envDefaultServer = typeof process !== "undefined" ? define_process_env_default?.BYTEDESK_DOCS_DEFAULT_SERVER : void 0;
const BYTEDESK_DOCS_DEFAULT_SERVER = envDefaultServer?.trim() ? envDefaultServer.trim().replace(/\/+$/, "") : "https://api.weiyuai.cn";
const AI_PROVIDERS = [
  {
    id: "bytedesk",
    label: "Bytedesk（微语）",
    // the server routes to its own configured model; the model list stays a
    // free text field until the server reports models
    models: [],
    defaultModel: "",
    keyPlaceholder: "Not required — sign in to Bytedesk",
    needsBaseUrl: true,
    baseUrlLabel: "Server URL"
  },
  {
    id: "custom",
    label: "Custom (BYOK)",
    models: [],
    defaultModel: "",
    keyPlaceholder: "API Key",
    needsBaseUrl: true
  }
];
function defaultAiSettings(defaultApiKeys) {
  const providers = {};
  for (const meta of AI_PROVIDERS) {
    providers[meta.id] = {
      apiKey: "",
      model: meta.defaultModel,
      baseUrl: meta.needsBaseUrl ? meta.id === "bytedesk" ? BYTEDESK_DOCS_DEFAULT_SERVER : "" : void 0
    };
  }
  return { provider: "bytedesk", providers };
}
const DEFAULT_MAX_OUTPUT_TOKENS = 8192;
const MIN_MAX_OUTPUT_TOKENS = 1024;
const MAX_MAX_OUTPUT_TOKENS = 131072;
function clampMaxOutputTokens(value) {
  const n = typeof value === "number" ? Math.floor(value) : Number.NaN;
  if (!Number.isFinite(n)) return DEFAULT_MAX_OUTPUT_TOKENS;
  return Math.min(MAX_MAX_OUTPUT_TOKENS, Math.max(MIN_MAX_OUTPUT_TOKENS, n));
}
function metaOf(id) {
  return AI_PROVIDERS.find((m) => m.id === id);
}
({
  bytedesk: {
    meta: metaOf("bytedesk")
  },
  custom: {
    meta: metaOf("custom")
  }
});
const LOGOS = {
  bytedesk: /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { viewBox: "0 0 24 24", fill: "none", "aria-hidden": "true", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("rect", { x: "1.5", y: "1.5", width: "21", height: "21", rx: "5", stroke: "currentColor", strokeWidth: "1.8" }),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        fillRule: "evenodd",
        clipRule: "evenodd",
        d: "M8 6.5h4.4L15 9.1v8.4H8V6.5Zm4 2.4V6.9L14.6 8.9H12ZM9 10.5h6v1.1H9v-1.1Zm0 2.4h6V14H9v-1.1Zm0 2.4h4.5v1.1H9v-1.1Z",
        fill: "currentColor"
      }
    )
  ] }),
  custom: /* @__PURE__ */ jsxRuntimeExports.jsxs(
    "svg",
    {
      viewBox: "0 0 24 24",
      fill: "none",
      stroke: "currentColor",
      strokeWidth: "1.8",
      strokeLinecap: "round",
      "aria-hidden": "true",
      children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("path", { d: "M3 6.5h8M15 6.5h6M3 12h3M10 12h11M3 17.5h11M18 17.5h3" }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "13", cy: "6.5", r: "2" }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "8", cy: "12", r: "2" }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "16", cy: "17.5", r: "2" })
      ]
    }
  )
};
function ProviderLogo({ id }) {
  return /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "set-provider-logo", "aria-hidden": "true", children: LOGOS[id] ?? LOGOS.custom });
}
const LANG_OPTIONS = [
  { value: "ar", label: "العربية" },
  { value: "de", label: "Deutsch" },
  { value: "en", label: "English" },
  { value: "es", label: "Español" },
  { value: "fr", label: "Français" },
  { value: "he", label: "עברית" },
  { value: "hi", label: "हिन्दी" },
  { value: "id", label: "Bahasa Indonesia" },
  { value: "it", label: "Italiano" },
  { value: "ja", label: "日本語" },
  { value: "ko", label: "한국어" },
  { value: "ms", label: "Bahasa Melayu" },
  { value: "nl", label: "Nederlands" },
  { value: "pl", label: "Polski" },
  { value: "pt", label: "Português" },
  { value: "ru", label: "Русский" },
  { value: "th", label: "ไทย" },
  { value: "zh", label: "简体中文" },
  { value: "zh-TW", label: "繁體中文" }
];
const THEME_OPTIONS = [
  { value: "system", labelKey: "themeSystem" },
  { value: "light", labelKey: "themeLight" },
  { value: "dark", labelKey: "themeDark" }
];
const CHANNEL_OPTIONS = [
  { value: "stable", labelKey: "channelStable" },
  { value: "beta", labelKey: "channelBeta" }
];
function formatStars(n) {
  if (n < 1e3) return String(n);
  const k = n / 1e3;
  return `${k >= 100 ? Math.round(k) : (Math.round(k * 10) / 10).toString().replace(/\.0$/, "")}k`;
}
const SECTIONS = [
  { id: "account", labelKey: "setSecAccount" },
  { id: "aiModel", labelKey: "setSecAiModel" },
  { id: "general", labelKey: "setSecGeneral" },
  { id: "about", labelKey: "setSecAbout" }
];
function SectionIcon({ id }) {
  if (id === "aiModel") {
    return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 16 16", fill: "none", "aria-hidden": "true", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "path",
        {
          d: "M8 1.8 9.5 6l4.2 1.5L9.5 9 8 13.2 6.5 9 2.3 7.5 6.5 6 8 1.8Z",
          stroke: "currentColor",
          strokeWidth: "1.3",
          strokeLinejoin: "round"
        }
      ),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "path",
        {
          d: "M12.8 11.2v3M11.3 12.7h3",
          stroke: "currentColor",
          strokeWidth: "1.3",
          strokeLinecap: "round"
        }
      )
    ] });
  }
  if (id === "account") {
    return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 16 16", fill: "none", "aria-hidden": "true", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "8", cy: "5.2", r: "2.9", stroke: "currentColor", strokeWidth: "1.3" }),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "path",
        {
          d: "M2.7 13.6a5.5 5.5 0 0 1 10.6 0",
          stroke: "currentColor",
          strokeWidth: "1.3",
          strokeLinecap: "round"
        }
      )
    ] });
  }
  if (id === "general") {
    return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 16 16", fill: "none", "aria-hidden": "true", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "path",
        {
          d: "M2 5h8M13 5h1M2 11h1M6 11h8",
          stroke: "currentColor",
          strokeWidth: "1.3",
          strokeLinecap: "round"
        }
      ),
      /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "11.5", cy: "5", r: "1.7", stroke: "currentColor", strokeWidth: "1.3" }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "4.5", cy: "11", r: "1.7", stroke: "currentColor", strokeWidth: "1.3" })
    ] });
  }
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 16 16", fill: "none", "aria-hidden": "true", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "8", cy: "8", r: "6.3", stroke: "currentColor", strokeWidth: "1.3" }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("path", { d: "M8 7.4v3.4", stroke: "currentColor", strokeWidth: "1.3", strokeLinecap: "round" }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "8", cy: "5.1", r: "0.8", fill: "currentColor" })
  ] });
}
function Field({
  label,
  value,
  valueTitle,
  action
}) {
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field-text", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-label", children: label }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-value", "data-tip": valueTitle, children: value })
    ] }),
    action
  ] });
}
function AiModelPane({ t }) {
  const [catalog] = reactExports.useState(() => window.aiOffice.getAiProviders?.() ?? []);
  const [settings, setSettings] = reactExports.useState(null);
  const [dirty, setDirty] = reactExports.useState(false);
  const [saved, setSaved] = reactExports.useState(false);
  const [testing, setTesting] = reactExports.useState(false);
  const [testResult, setTestResult] = reactExports.useState(null);
  const [maxTokensDraft, setMaxTokensDraft] = reactExports.useState(null);
  reactExports.useEffect(() => {
    let alive = true;
    void window.aiOffice.getAiSettings?.().then((s) => {
      if (!alive || !s) return;
      setSettings(s);
    });
    return () => {
      alive = false;
    };
  }, []);
  if (!settings) return null;
  const provider = settings.provider;
  const meta = catalog.find((c) => c.id === provider);
  const config = settings.providers[provider] ?? {
    apiKey: "",
    model: meta?.defaultModel ?? ""
  };
  const isBytedesk = provider === "bytedesk";
  const touch = () => {
    setDirty(true);
    setSaved(false);
    setTestResult(null);
  };
  const updateConfig = (patch) => {
    setSettings({
      ...settings,
      providers: { ...settings.providers, [provider]: { ...config, ...patch } }
    });
    touch();
  };
  const commitMaxTokens = () => {
    if (maxTokensDraft === null) return;
    setMaxTokensDraft(null);
    const next = clampMaxOutputTokens(Number.parseInt(maxTokensDraft, 10));
    if (next === (settings.maxOutputTokens ?? DEFAULT_MAX_OUTPUT_TOKENS)) return;
    setSettings({ ...settings, maxOutputTokens: next });
    touch();
  };
  const selectProvider = (id) => {
    setSettings({ ...settings, provider: id });
    touch();
  };
  const save = () => {
    window.aiOffice.setAiSettings?.(settings).then(() => {
      setDirty(false);
      setSaved(true);
    }).catch((error) => {
      window.alert(error instanceof Error ? error.message : String(error));
    });
  };
  const test = () => {
    setTesting(true);
    setTestResult(null);
    window.aiOffice.testAiSettings?.(settings).then((r) => setTestResult(r ?? { ok: false })).catch(
      (error) => setTestResult({ ok: false, error: error instanceof Error ? error.message : String(error) })
    ).finally(() => setTesting(false));
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { className: "set-pane-title", children: t("setSecAiModel") }),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-text", children: /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "set-field-label", children: t("setAiProvider") }) }),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        Dropdown,
        {
          className: "set-dd",
          value: provider,
          ariaLabel: t("setAiProvider"),
          options: catalog.map((c) => ({
            value: c.id,
            label: c.label,
            render: /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx(ProviderLogo, { id: c.id }),
              c.label
            ] })
          })),
          onPick: (v) => selectProvider(v)
        }
      )
    ] }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-desc set-ai-note", children: isBytedesk ? t("setAiGensparkHint") : t("setAiByokNote") }),
    isBytedesk ? /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-text", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field-stack", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "set-field-label", htmlFor: "set-ai-base-url", children: t("setAiServerUrl") }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-desc", children: t("setAiServerUrlHint") })
      ] }) }),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "input",
        {
          id: "set-ai-base-url",
          className: "set-input",
          type: "text",
          value: config.baseUrl ?? "",
          placeholder: "https://api.weiyuai.cn",
          spellCheck: false,
          onChange: (e) => updateConfig({ baseUrl: e.target.value.trim() })
        }
      )
    ] }) : null,
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-text", children: /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "set-field-label", children: t("setAiModelId") }) }),
      meta && meta.models.length > 0 ? /* @__PURE__ */ jsxRuntimeExports.jsx(
        Dropdown,
        {
          className: "set-dd",
          value: config.model || meta.defaultModel,
          ariaLabel: t("setAiModelId"),
          options: meta.models.map((m) => ({ value: m, label: m })),
          onPick: (m) => updateConfig({ model: m })
        }
      ) : /* @__PURE__ */ jsxRuntimeExports.jsx(
        "input",
        {
          id: "set-ai-model",
          className: "set-input",
          type: "text",
          value: config.model,
          placeholder: isBytedesk ? t("setAiModelOptional") : "model-id",
          spellCheck: false,
          onChange: (e) => updateConfig({ model: e.target.value })
        }
      )
    ] }),
    !isBytedesk && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-text", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field-stack", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "set-field-label", htmlFor: "set-ai-key", children: t("setAiApiKey") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-desc", children: t("setAiKeyHint") })
        ] }) }),
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "input",
          {
            id: "set-ai-key",
            className: "set-input",
            type: "password",
            value: config.apiKey,
            placeholder: meta?.keyPlaceholder ?? "API Key",
            spellCheck: false,
            autoComplete: "off",
            onChange: (e) => updateConfig({ apiKey: e.target.value.trim() })
          }
        )
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-text", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field-stack", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "set-field-label", htmlFor: "set-ai-base-url", children: t("setAiBaseUrl") }),
          !meta?.needsBaseUrl && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-desc", children: t("setAiBaseUrlHint") })
        ] }) }),
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "input",
          {
            id: "set-ai-base-url",
            className: "set-input",
            type: "text",
            value: config.baseUrl ?? "",
            placeholder: meta?.needsBaseUrl ? "https://…/v1" : meta?.defaultBaseUrl,
            spellCheck: false,
            onChange: (e) => updateConfig({ baseUrl: e.target.value.trim() })
          }
        )
      ] })
    ] }),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-text", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field-stack", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "set-field-label", htmlFor: "set-ai-max-tokens", children: t("setAiMaxTokens") }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-desc", children: t("setAiMaxTokensDesc") })
      ] }) }),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "input",
        {
          id: "set-ai-max-tokens",
          className: "set-input",
          type: "number",
          min: MIN_MAX_OUTPUT_TOKENS,
          max: MAX_MAX_OUTPUT_TOKENS,
          step: 1024,
          value: maxTokensDraft ?? String(settings.maxOutputTokens ?? DEFAULT_MAX_OUTPUT_TOKENS),
          onChange: (e) => setMaxTokensDraft(e.target.value),
          onBlur: commitMaxTokens
        }
      )
    ] }),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-pane-footer", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        AiStatusPill,
        {
          status: testing ? { kind: "testing", text: t("setAiTesting") } : testResult ? testResult.ok ? { kind: "ok", text: t("setAiTestOk") } : { kind: "err", text: testResult.error || t("setAiTestFail") } : saved ? { kind: "ok", text: t("setAiSaved") } : null
        }
      ),
      /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "set-btn", disabled: testing, onClick: test, children: t("setAiTest") }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "set-btn primary", disabled: !dirty, onClick: save, children: t("setAiSave") })
    ] })
  ] });
}
function AiStatusPill({ status }) {
  if (!status) return null;
  return /* @__PURE__ */ jsxRuntimeExports.jsxs(
    "span",
    {
      className: `set-ai-status ${status.kind}`,
      role: "status",
      title: status.kind === "err" ? status.text : void 0,
      children: [
        status.kind === "testing" ? /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "set-ai-spin", "aria-hidden": "true" }) : status.kind === "ok" ? /* @__PURE__ */ jsxRuntimeExports.jsxs(
          "svg",
          {
            className: "set-ai-status-icon",
            width: "14",
            height: "14",
            viewBox: "0 0 14 14",
            "aria-hidden": "true",
            children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "7", cy: "7", r: "6.3", fill: "currentColor", opacity: "0.16" }),
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "path",
                {
                  d: "M4.2 7.3l1.9 1.9 3.7-4.3",
                  stroke: "currentColor",
                  strokeWidth: "1.5",
                  strokeLinecap: "round",
                  strokeLinejoin: "round",
                  fill: "none"
                }
              )
            ]
          }
        ) : /* @__PURE__ */ jsxRuntimeExports.jsxs(
          "svg",
          {
            className: "set-ai-status-icon",
            width: "14",
            height: "14",
            viewBox: "0 0 14 14",
            "aria-hidden": "true",
            children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "7", cy: "7", r: "6.3", fill: "currentColor", opacity: "0.16" }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("path", { d: "M7 3.8v3.9", stroke: "currentColor", strokeWidth: "1.5", strokeLinecap: "round" }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "7", cy: "10.1", r: "1", fill: "currentColor" })
            ]
          }
        ),
        /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "set-ai-status-text", children: status.text })
      ]
    }
  );
}
function SettingsModal({
  status,
  loggingOut,
  onClose,
  onLogout
}) {
  const { lang, setLang, t } = useI18n();
  const [section, setSection] = reactExports.useState("account");
  const [theme, setTheme] = reactExports.useState("system");
  const [saveDir, setSaveDir] = reactExports.useState("");
  const [channel, setChannel] = reactExports.useState("stable");
  const [appVersion, setAppVersion] = reactExports.useState("");
  const [githubStars, setGithubStars] = reactExports.useState(null);
  reactExports.useEffect(() => {
    let alive = true;
    void window.aiOffice.getTheme?.().then((th) => {
      if (alive) setTheme(th);
    });
    void window.aiOffice.getDefaultSaveDir?.().then((dir) => {
      if (alive && dir) setSaveDir(dir);
    });
    void window.aiOffice.getUpdateChannel?.().then((ch) => {
      if (alive) setChannel(ch);
    });
    void window.aiOffice.getAppVersion?.().then((v) => {
      if (alive && v) setAppVersion(v);
    });
    void window.aiOffice.githubStars?.().then((n) => {
      if (alive && n !== null) setGithubStars(n);
    });
    return () => {
      alive = false;
    };
  }, []);
  reactExports.useEffect(() => {
    const handler = (e) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handler);
    return () => window.removeEventListener("keydown", handler);
  }, [onClose]);
  const applyTheme = (next) => {
    setTheme(next);
    void window.aiOffice.setTheme(next);
    if (next === "system") document.documentElement.removeAttribute("data-theme");
    else document.documentElement.setAttribute("data-theme", next);
  };
  const changeSaveDir = () => {
    void window.aiOffice.pickDefaultSaveDir?.().then((dir) => {
      if (dir) setSaveDir(dir);
    });
  };
  const loggedIn = status?.loggedIn ?? false;
  const email = status?.email ?? "";
  return /* @__PURE__ */ jsxRuntimeExports.jsx(
    "div",
    {
      className: "set-overlay",
      onMouseDown: (e) => {
        if (e.target === e.currentTarget) onClose();
      },
      children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-dialog", role: "dialog", "aria-modal": "true", "aria-label": t("settings"), children: [
        /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-header", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("h2", { className: "set-title", children: t("settings") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "set-close", onClick: onClose, "aria-label": t("cancel"), children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "14", height: "14", viewBox: "0 0 14 14", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
            "path",
            {
              d: "M2 2l10 10M12 2L2 12",
              stroke: "currentColor",
              strokeWidth: "1.5",
              strokeLinecap: "round"
            }
          ) }) })
        ] }),
        /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-body", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("nav", { className: "set-nav", "aria-label": t("settings"), children: SECTIONS.map((s) => /* @__PURE__ */ jsxRuntimeExports.jsxs(
            "button",
            {
              className: `set-nav-item${section === s.id ? " active" : ""}`,
              "aria-current": section === s.id,
              onClick: () => setSection(s.id),
              children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx(SectionIcon, { id: s.id }),
                t(s.labelKey)
              ]
            },
            s.id
          )) }),
          /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-pane", children: [
            section === "account" && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { className: "set-pane-title", children: t("setSecAccount") }),
              /* @__PURE__ */ jsxRuntimeExports.jsx(Field, { label: t("setEmail"), value: loggedIn ? email : t("setNotLoggedIn") }),
              loggedIn && /* @__PURE__ */ jsxRuntimeExports.jsx(
                Field,
                {
                  label: t("setServer"),
                  value: status?.serverUrl || "—",
                  action: /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { style: { display: "inline-flex", gap: 8 }, children: [
                    /* @__PURE__ */ jsxRuntimeExports.jsx(
                      "button",
                      {
                        className: "set-btn",
                        "data-tip": t("creditsTip"),
                        onClick: () => void window.aiOffice.openCreditUsage?.(),
                        children: t("setViewUsage")
                      }
                    ),
                    /* @__PURE__ */ jsxRuntimeExports.jsx(
                      "button",
                      {
                        className: "set-btn",
                        disabled: loggingOut,
                        onClick: onLogout,
                        children: t("loginServerSettings")
                      }
                    )
                  ] })
                }
              ),
              /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-pane-footer", children: loggedIn ? /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "set-btn danger", disabled: loggingOut, onClick: onLogout, children: loggingOut ? t("loggingOut") : t("logout") }) : /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "set-field-desc", children: t("setAiGensparkHint") }) })
            ] }),
            section === "aiModel" && /* @__PURE__ */ jsxRuntimeExports.jsx(AiModelPane, { t }),
            section === "general" && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { className: "set-pane-title", children: t("setSecGeneral") }),
              /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-text", children: /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "set-field-label", children: t("language") }) }),
                /* @__PURE__ */ jsxRuntimeExports.jsx(
                  Dropdown,
                  {
                    className: "set-dd",
                    value: lang,
                    ariaLabel: t("language"),
                    options: LANG_OPTIONS.map((opt) => ({ value: opt.value, label: opt.label })),
                    onPick: (v) => setLang(v)
                  }
                )
              ] }),
              /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-text", children: /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "set-field-label", children: t("theme") }) }),
                /* @__PURE__ */ jsxRuntimeExports.jsx(
                  Dropdown,
                  {
                    className: "set-dd",
                    value: theme,
                    ariaLabel: t("theme"),
                    options: THEME_OPTIONS.map((opt) => ({
                      value: opt.value,
                      label: t(opt.labelKey)
                    })),
                    onPick: (v) => applyTheme(v)
                  }
                )
              ] }),
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                Field,
                {
                  label: t("saveLocation"),
                  value: saveDir || "—",
                  valueTitle: saveDir,
                  action: /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "set-btn", onClick: changeSaveDir, children: t("setChange") })
                }
              )
            ] }),
            section === "about" && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { className: "set-pane-title", children: t("setSecAbout") }),
              /* @__PURE__ */ jsxRuntimeExports.jsx(Field, { label: t("versionLabel"), value: appVersion || "—" }),
              /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "set-field", children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "set-field-text", children: /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "set-field-label", children: t("updateChannel") }) }),
                /* @__PURE__ */ jsxRuntimeExports.jsx(
                  Dropdown,
                  {
                    className: "set-dd",
                    value: channel,
                    ariaLabel: t("updateChannel"),
                    options: CHANNEL_OPTIONS.map((opt) => ({
                      value: opt.value,
                      label: t(opt.labelKey)
                    })),
                    onPick: (v) => {
                      const next = v === "beta" ? "beta" : "stable";
                      setChannel(next);
                      void window.aiOffice.setUpdateChannel(next);
                    }
                  }
                )
              ] }),
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                Field,
                {
                  label: t("setGithub"),
                  value: githubStars === null ? "github.com/Bytedesk/bytedesk" : `github.com/Bytedesk/bytedesk · ★ ${formatStars(githubStars)}`,
                  action: /* @__PURE__ */ jsxRuntimeExports.jsx(
                    "button",
                    {
                      className: "set-btn",
                      onClick: () => void window.aiOffice.openGitHubRepo?.(),
                      children: t("starOnGitHub")
                    }
                  )
                }
              )
            ] })
          ] })
        ] })
      ] })
    }
  );
}
const COUNTRY_DEFINITIONS = [
  { value: "86", icon: "🇨🇳", code: "CN", key: "loginCountryChina", fallback: "中国" },
  { value: "1", icon: "🇺🇸", code: "US", key: "loginCountryUSA", fallback: "美国" },
  { value: "81", icon: "🇯🇵", code: "JP", key: "loginCountryJapan", fallback: "日本" },
  { value: "852", icon: "🇭🇰", code: "HK", key: "loginCountryHongkong", fallback: "香港" },
  { value: "886", icon: "🇹🇼", code: "TW", key: "loginCountryTaiwan", fallback: "台湾" },
  { value: "853", icon: "🇲🇴", code: "MO", key: "loginCountryMacao", fallback: "澳门" },
  { value: "82", icon: "🇰🇷", code: "KR", key: "loginCountryKorea", fallback: "韩国" },
  { value: "1-ca", icon: "🇨🇦", code: "CA", key: "loginCountryCanada", fallback: "加拿大" },
  { value: "65", icon: "🇸🇬", code: "SG", key: "loginCountrySingapore", fallback: "新加坡" },
  { value: "60", icon: "🇲🇾", code: "MY", key: "loginCountryMalaysia", fallback: "马来西亚" },
  { value: "66", icon: "🇹🇭", code: "TH", key: "loginCountryThailand", fallback: "泰国" },
  { value: "84", icon: "🇻🇳", code: "VN", key: "loginCountryVietnam", fallback: "越南" },
  { value: "63", icon: "🇵🇭", code: "PH", key: "loginCountryPhilippines", fallback: "菲律宾" },
  { value: "62", icon: "🇮🇩", code: "ID", key: "loginCountryIndonesia", fallback: "印度尼西亚" },
  { value: "44", icon: "🇬🇧", code: "GB", key: "loginCountryUK", fallback: "英国" },
  { value: "49", icon: "🇩🇪", code: "DE", key: "loginCountryGermany", fallback: "德国" },
  { value: "33", icon: "🇫🇷", code: "FR", key: "loginCountryFrance", fallback: "法国" },
  { value: "39", icon: "🇮🇹", code: "IT", key: "loginCountryItaly", fallback: "意大利" },
  { value: "34", icon: "🇪🇸", code: "ES", key: "loginCountrySpain", fallback: "西班牙" },
  { value: "7", icon: "🇷🇺", code: "RU", key: "loginCountryRussia", fallback: "俄罗斯" },
  { value: "61", icon: "🇦🇺", code: "AU", key: "loginCountryAustralia", fallback: "澳大利亚" },
  { value: "64", icon: "🇳🇿", code: "NZ", key: "loginCountryNewzealand", fallback: "新西兰" }
];
function getCountryDialCode(value) {
  if (!value) return "";
  return value.includes("-") ? value.split("-")[0] : value;
}
function buildCountryOptions(t) {
  return COUNTRY_DEFINITIONS.map((item) => ({
    value: item.value,
    icon: item.icon,
    label: `${item.icon} ${t(item.key, {}) || item.fallback} (+${getCountryDialCode(item.value)})`
  }));
}
function formatMobileDisplay(value) {
  if (!value) return "";
  const digits = value.replace(/\s/g, "");
  if (digits.length <= 3) return digits;
  if (digits.length <= 7) return `${digits.slice(0, 3)} ${digits.slice(3)}`;
  return `${digits.slice(0, 3)} ${digits.slice(3, 7)} ${digits.slice(7, 11)}`;
}
function passwordStrength(password) {
  if (!password) return 0;
  let score = 0;
  if (password.length >= 8) score += 20;
  if (password.length >= 12) score += 10;
  if (/\d/.test(password)) score += 20;
  if (/[a-z]/.test(password)) score += 20;
  if (/[A-Z]/.test(password)) score += 20;
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score += 10;
  return Math.min(score, 100);
}
const BACKEND_I18N_KEYS = {
  "i18n.auth.captcha.error": "loginErrCaptcha",
  "i18n.auth.captcha.validate.failed": "loginErrCaptcha",
  "i18n.username.or.password.incorrect": "loginErrCredentials",
  "i18n.auth.username.required": "loginErrForm",
  "i18n.auth.password.required": "loginErrForm",
  "i18n.auth.username.mobile.mismatch": "loginErrMismatch",
  "i18n.auth.captcha.send.failed": "loginErrSms",
  "i18n.force.logout.reason": "loginErrDisabled",
  "i18n.error": "loginErrServer"
};
function translateBackendMessage(raw, t) {
  const key = BACKEND_I18N_KEYS[raw];
  return key ? t(key) : raw;
}
function LoginView({ onClose, onLoggedIn }) {
  const { t, lang, setLang } = useI18n();
  const [serverUrl, setServerUrl] = reactExports.useState("");
  const [serverEditing, setServerEditing] = reactExports.useState(false);
  const [serverDraft, setServerDraft] = reactExports.useState("");
  const [config, setConfig] = reactExports.useState(null);
  const [configLoading, setConfigLoading] = reactExports.useState(true);
  const accountEnabled = config?.loginUsernameEnable !== false;
  const mobileEnabled = config?.loginMobileEnable !== false;
  const [tab, setTab] = reactExports.useState("account");
  const [captcha, setCaptcha] = reactExports.useState(null);
  const [captchaCode, setCaptchaCode] = reactExports.useState("");
  const [captchaOk, setCaptchaOk] = reactExports.useState(false);
  const [captchaLoadFailed, setCaptchaLoadFailed] = reactExports.useState(false);
  const captchaDisabled = config?.captchaDisabled === true;
  const [username, setUsername] = reactExports.useState("");
  const [password, setPassword] = reactExports.useState("");
  const [twoFactorCountry, setTwoFactorCountry] = reactExports.useState("86");
  const [twoFactorMobile, setTwoFactorMobile] = reactExports.useState("");
  const [twoFactorCode, setTwoFactorCode] = reactExports.useState("");
  const twoFactorEnabled = config?.login2faEnable === true;
  const [country, setCountry] = reactExports.useState("86");
  const [mobile, setMobile] = reactExports.useState("");
  const [smsCode, setSmsCode] = reactExports.useState("");
  const [smsCountdown, setSmsCountdown] = reactExports.useState(0);
  const [smsSending, setSmsSending] = reactExports.useState(false);
  const [submitting, setSubmitting] = reactExports.useState(false);
  const [error, setError] = reactExports.useState(null);
  const [privacyChecked, setPrivacyChecked] = reactExports.useState(false);
  const [privacyConfirmOpen, setPrivacyConfirmOpen] = reactExports.useState(false);
  const privacyConfirmAction = reactExports.useRef(null);
  const [weakOpen, setWeakOpen] = reactExports.useState(false);
  const weakAction = reactExports.useRef(null);
  const countryOptions = reactExports.useMemo(() => buildCountryOptions(t), [t]);
  const loadServer = reactExports.useCallback(async (url) => {
    setConfigLoading(true);
    try {
      const state = await window.aiOffice.authGetState?.();
      if (state?.serverUrl && state.serverUrl === url) {
        if (state.rememberedLogin) {
          if (/^\d+$/.test(state.rememberedLogin)) setMobile(formatMobileDisplay(state.rememberedLogin));
          else setUsername(state.rememberedLogin);
        }
      }
    } catch {
    }
    const cfg = await window.aiOffice.authServerConfig?.(url);
    setConfig(cfg ?? {});
    setConfigLoading(false);
    if (cfg?.captchaDisabled) {
      setCaptcha(null);
      setCaptchaOk(true);
      return;
    }
    setCaptchaOk(false);
    setCaptchaCode("");
    const kaptcha = await window.aiOffice.authKaptcha?.(url);
    if (kaptcha && "captchaUid" in kaptcha) {
      setCaptcha({ captchaUid: kaptcha.captchaUid, imageBase64: kaptcha.imageBase64 });
      setCaptchaLoadFailed(false);
    } else {
      setCaptcha(null);
      setCaptchaLoadFailed(true);
    }
  }, []);
  reactExports.useEffect(() => {
    let alive = true;
    void (async () => {
      const state = await window.aiOffice.authGetState?.();
      const initial = state?.serverUrl || state?.defaultServerUrl || "https://api.weiyuai.cn";
      if (!alive) return;
      setServerUrl(initial);
      await loadServer(initial);
    })();
    return () => {
      alive = false;
    };
  }, [loadServer]);
  const showAccountTab = accountEnabled || !mobileEnabled;
  const showMobileTab = mobileEnabled;
  reactExports.useEffect(() => {
    if (tab === "account" && !showAccountTab) setTab("mobile");
    if (tab === "mobile" && !showMobileTab) setTab("account");
  }, [showAccountTab, showMobileTab, tab]);
  reactExports.useEffect(() => {
    if (smsCountdown <= 0) return;
    const timer = setTimeout(() => setSmsCountdown((n) => n - 1), 1e3);
    return () => clearTimeout(timer);
  }, [smsCountdown]);
  const onCaptchaInput = async (value) => {
    setCaptchaCode(value);
    if (captchaDisabled) {
      setCaptchaOk(true);
      return;
    }
    if (value.trim().length === 4) {
      const ok = await window.aiOffice.authCheckKaptcha?.(serverUrl, captcha?.captchaUid, value.trim());
      setCaptchaOk(Boolean(ok));
    } else {
      setCaptchaOk(false);
    }
  };
  const refreshCaptcha = async () => {
    if (captchaDisabled) return;
    setCaptchaOk(false);
    setCaptchaCode("");
    const kaptcha = await window.aiOffice.authKaptcha?.(serverUrl);
    if (kaptcha && "captchaUid" in kaptcha) {
      setCaptcha({ captchaUid: kaptcha.captchaUid, imageBase64: kaptcha.imageBase64 });
      setCaptchaLoadFailed(false);
    } else {
      setCaptcha(null);
      setCaptchaLoadFailed(true);
    }
  };
  const applyServerDraft = async () => {
    const next = serverDraft.trim();
    if (!next) return;
    setServerUrl(next);
    setServerEditing(false);
    await loadServer(next);
  };
  const onMobileInput = (raw, target, setter) => {
    const digits = raw.replace(/[^\d\s]/g, "");
    setter(target === "main" && country === "86" ? formatMobileDisplay(digits) : digits);
  };
  const validateMobileForCountry = (raw, dialCountry) => {
    const digits = raw.replace(/\s/g, "");
    if (!digits) return t("loginMobileRequired");
    if (dialCountry === "86" && !/^1\d{10}$/.test(digits)) {
      return t("loginMobileInvalid");
    }
    return null;
  };
  const sendSms = async (targetMobile, targetCountry) => {
    if (smsCountdown > 0 || smsSending) return;
    const invalid = validateMobileForCountry(targetMobile, targetCountry);
    if (invalid) {
      setError(invalid);
      return;
    }
    if (!captchaDisabled && !captchaOk) {
      setError(t("loginErrCaptchaFirst"));
      return;
    }
    setError(null);
    setSmsSending(true);
    const result = await window.aiOffice.authSendMobileCode?.({
      serverUrl,
      mobile: targetMobile.replace(/\s/g, ""),
      country: targetCountry,
      ...captcha && !captchaDisabled ? { captchaUid: captcha.captchaUid, captchaCode } : {}
    });
    setSmsSending(false);
    if (result?.ok) {
      setSmsCountdown(60);
    } else if (result) {
      setError(translateBackendMessage(result.error, t));
      await refreshCaptcha();
    }
  };
  const succeed = () => {
    onLoggedIn?.();
    onClose();
  };
  const doAccountLogin = async (afterWeak) => {
    if (twoFactorEnabled && (!twoFactorMobile.trim() || !twoFactorCode.trim())) {
      setError(t("loginTwoFactorMissing"));
      return;
    }
    setSubmitting(true);
    setError(null);
    const result = await window.aiOffice.authLogin?.({
      serverUrl,
      username: username.trim(),
      password,
      ...captcha && !captchaDisabled ? { captchaUid: captcha.captchaUid, captchaCode } : {},
      ...twoFactorEnabled ? {
        twoFactorEnabled: true,
        twoFactorMobile: twoFactorMobile.replace(/\s/g, ""),
        twoFactorCountry,
        twoFactorCode: twoFactorCode.trim()
      } : {}
    });
    setSubmitting(false);
    if (!result) {
      setError(t("loginFailed"));
      return;
    }
    if (result.ok) {
      if (afterWeak === "change-password") {
        void window.aiOffice.authOpenExternal?.(
          `${serverUrl.replace(/\/+$/, "")}/admin/setting/profile?showChangePassword=true`
        );
      }
      succeed();
      return;
    }
    setError(translateBackendMessage(result.error, t));
    setCaptchaCode("");
    setCaptchaOk(false);
    await refreshCaptcha();
  };
  const submitAccount = async () => {
    if (submitting) return;
    if (!username.trim() || !password) {
      setError(t("loginFormIncomplete"));
      return;
    }
    if (!captchaDisabled && !captchaOk) {
      setError(t("loginErrCaptchaFirst"));
      return;
    }
    if (!privacyChecked) {
      privacyConfirmAction.current = () => void submitAccount();
      setPrivacyConfirmOpen(true);
      return;
    }
    if (passwordStrength(password) < 40) {
      weakAction.current = null;
      setWeakOpen(true);
      return;
    }
    await doAccountLogin("none");
  };
  const submitMobile = async () => {
    if (submitting) return;
    const mobileInvalid = validateMobileForCountry(mobile, country);
    if (mobileInvalid || !smsCode.trim()) {
      setError(mobileInvalid ?? t("loginFormIncomplete"));
      return;
    }
    if (!captchaDisabled && !captchaOk) {
      setError(t("loginErrCaptchaFirst"));
      return;
    }
    if (!privacyChecked) {
      privacyConfirmAction.current = () => void submitMobile();
      setPrivacyConfirmOpen(true);
      return;
    }
    setSubmitting(true);
    setError(null);
    const result = await window.aiOffice.authLoginMobile?.({
      serverUrl,
      mobile: mobile.replace(/\s/g, ""),
      country,
      code: smsCode.trim(),
      ...captcha && !captchaDisabled ? { captchaUid: captcha.captchaUid, captchaCode } : {}
    });
    setSubmitting(false);
    if (!result) {
      setError(t("loginFailed"));
      return;
    }
    if (result.ok) {
      succeed();
      return;
    }
    setError(translateBackendMessage(result.error, t));
    setCaptchaCode("");
    setCaptchaOk(false);
    await refreshCaptcha();
  };
  const strength = passwordStrength(password);
  const strengthLabel = strength >= 80 ? t("loginPwStrong") : strength >= 60 ? t("loginPwMedium") : strength >= 40 ? t("loginPwWeak") : t("loginPwVeryWeak");
  const strengthColor = strength >= 80 ? "var(--ok, #52c41a)" : strength >= 60 ? "var(--warn, #faad14)" : "var(--danger, #ff4d4f)";
  const submitDisabled = submitting || !captchaDisabled && !captchaOk;
  const privacyUrl = config?.privacyPolicyUrl || "https://www.weiyuai.cn/privacy.html";
  const termsUrl = config?.termsOfServiceUrl || "https://www.weiyuai.cn/terms.html";
  const brandName2 = config?.name || t("loginBrandName");
  const brandDesc = config?.description || t("loginBrandDesc");
  const showRegister = config?.showRegisterButton === true;
  const langOptions = [
    { value: "zh", label: "简体中文" },
    { value: "zh-TW", label: "繁體中文" },
    { value: "en", label: "English" },
    { value: "ja", label: "日本語" },
    { value: "ko", label: "한국어" },
    { value: "fr", label: "Français" },
    { value: "de", label: "Deutsch" },
    { value: "es", label: "Español" },
    { value: "ru", label: "Русский" },
    { value: "th", label: "ไทย" },
    { value: "id", label: "Bahasa Indonesia" },
    { value: "ms", label: "Bahasa Melayu" },
    { value: "vi", label: "Tiếng Việt" },
    { value: "pt", label: "Português" },
    { value: "it", label: "Italiano" },
    { value: "nl", label: "Nederlands" },
    { value: "pl", label: "Polski" },
    { value: "ar", label: "العربية" },
    { value: "he", label: "עברית" },
    { value: "hi", label: "हिन्दी" }
  ];
  return /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "loginview-overlay", role: "dialog", "aria-modal": "true", "aria-label": t("loginTitle"), children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "loginview", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "loginview-top", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "select",
        {
          className: "loginview-lang",
          value: lang,
          "aria-label": t("language"),
          onChange: (e) => setLang(e.target.value),
          children: langOptions.map((o) => /* @__PURE__ */ jsxRuntimeExports.jsx("option", { value: o.value, children: o.label }, o.value))
        }
      ),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "button",
        {
          className: "loginview-settings",
          title: t("loginServerSettings"),
          "aria-label": t("loginServerSettings"),
          onClick: () => {
            setServerDraft(serverUrl);
            setServerEditing((v) => !v);
          },
          children: "⚙"
        }
      ),
      /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "loginview-close", onClick: onClose, "aria-label": t("cancel"), children: "✕" })
    ] }),
    serverEditing && /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "loginview-server-edit", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "input",
        {
          className: "login-input",
          type: "text",
          value: serverDraft,
          autoFocus: true,
          spellCheck: false,
          placeholder: "https://…",
          onChange: (e) => setServerDraft(e.target.value),
          onKeyDown: (e) => {
            if (e.key === "Enter" && !e.nativeEvent.isComposing) void applyServerDraft();
            if (e.key === "Escape") setServerEditing(false);
          }
        }
      ),
      /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "login-sms-btn", disabled: configLoading, onClick: () => void applyServerDraft(), children: t("loginServerApply") })
    ] }),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "loginview-card", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "loginview-brand", children: [
        config?.logo ? /* @__PURE__ */ jsxRuntimeExports.jsx("img", { className: "loginview-logo", src: config.logo, alt: brandName2 }) : /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "loginview-logo-badge", "aria-hidden": "true", children: "W" }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("h1", { className: "loginview-title", children: brandName2 }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "loginview-subtitle", children: brandDesc })
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "loginview-tabs", role: "tablist", children: [
        showAccountTab && /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            role: "tab",
            "aria-selected": tab === "account",
            className: tab === "account" ? "active" : "",
            onClick: () => setTab("account"),
            children: t("loginTabAccount")
          }
        ),
        showMobileTab && /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            role: "tab",
            "aria-selected": tab === "mobile",
            className: tab === "mobile" ? "active" : "",
            onClick: () => setTab("mobile"),
            children: t("loginTabMobile")
          }
        )
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "loginview-form", children: [
        tab === "account" && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "login-label", htmlFor: "lv-username", children: t("loginUsername") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx(
            "input",
            {
              id: "lv-username",
              className: "login-input",
              type: "text",
              value: username,
              autoComplete: "username",
              spellCheck: false,
              onChange: (e) => setUsername(e.target.value)
            }
          ),
          /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "login-label", htmlFor: "lv-password", children: t("loginPassword") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx(
            "input",
            {
              id: "lv-password",
              className: "login-input",
              type: "password",
              value: password,
              autoComplete: "current-password",
              onChange: (e) => setPassword(e.target.value),
              onKeyDown: (e) => {
                if (e.key === "Enter" && !e.nativeEvent.isComposing) void submitAccount();
              }
            }
          ),
          password && /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "login-strength", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "login-strength-bar", children: /* @__PURE__ */ jsxRuntimeExports.jsx("span", { style: { width: `${strength}%`, background: strengthColor } }) }),
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "login-strength-label", style: { color: strengthColor }, children: strengthLabel })
          ] }),
          !captchaDisabled && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "login-label", htmlFor: "lv-captcha", children: t("loginCaptcha") }),
            /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "login-captcha-row", children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "input",
                {
                  id: "lv-captcha",
                  className: "login-input",
                  type: "text",
                  value: captchaCode,
                  maxLength: 4,
                  autoComplete: "off",
                  onChange: (e) => void onCaptchaInput(e.target.value)
                }
              ),
              captcha?.imageBase64 ? /* @__PURE__ */ jsxRuntimeExports.jsx(
                "img",
                {
                  className: "login-captcha-img",
                  src: captcha.imageBase64,
                  alt: t("loginCaptcha"),
                  title: t("loginCaptchaRefresh"),
                  onClick: () => void refreshCaptcha()
                }
              ) : /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "login-captcha-refresh", onClick: () => void refreshCaptcha(), children: t("loginCaptchaRefresh") })
            ] })
          ] }),
          twoFactorEnabled && /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "login-2fa", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "login-label", htmlFor: "lv-2fa-mobile", children: t("loginTwoFactorMobile") }),
            /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "login-country-row", children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "select",
                {
                  className: "login-input",
                  value: twoFactorCountry,
                  "aria-label": t("loginCountry"),
                  onChange: (e) => setTwoFactorCountry(e.target.value),
                  children: countryOptions.map((o) => /* @__PURE__ */ jsxRuntimeExports.jsx("option", { value: o.value, children: o.label }, o.value))
                }
              ),
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "input",
                {
                  id: "lv-2fa-mobile",
                  className: "login-input",
                  type: "text",
                  value: twoFactorMobile,
                  autoComplete: "tel",
                  onChange: (e) => onMobileInput(e.target.value, "2fa", setTwoFactorMobile)
                }
              )
            ] }),
            /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "login-label", htmlFor: "lv-2fa-code", children: t("loginSmsCode") }),
            /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "login-captcha-row", children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "input",
                {
                  id: "lv-2fa-code",
                  className: "login-input",
                  type: "text",
                  value: twoFactorCode,
                  autoComplete: "one-time-code",
                  onChange: (e) => setTwoFactorCode(e.target.value)
                }
              ),
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "button",
                {
                  className: "login-sms-btn",
                  disabled: smsSending || smsCountdown > 0 || !captchaDisabled && !captchaOk,
                  onClick: () => void sendSms(twoFactorMobile, twoFactorCountry),
                  children: smsCountdown > 0 ? t("loginSmsCountdown").replace("{s}", String(smsCountdown)) : t("loginSmsGet")
                }
              )
            ] })
          ] })
        ] }),
        tab === "mobile" && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "login-label", htmlFor: "lv-mobile", children: t("loginMobile") }),
          /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "login-country-row", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "select",
              {
                className: "login-input login-country-select",
                value: country,
                "aria-label": t("loginCountry"),
                onChange: (e) => setCountry(e.target.value),
                children: countryOptions.map((o) => /* @__PURE__ */ jsxRuntimeExports.jsx("option", { value: o.value, children: o.label }, o.value))
              }
            ),
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "input",
              {
                id: "lv-mobile",
                className: "login-input",
                type: "text",
                inputMode: "numeric",
                value: mobile,
                autoComplete: "tel",
                onChange: (e) => onMobileInput(e.target.value, "main", setMobile)
              }
            )
          ] }),
          !captchaDisabled && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "login-label", htmlFor: "lv-captcha2", children: t("loginCaptcha") }),
            /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "login-captcha-row", children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "input",
                {
                  id: "lv-captcha2",
                  className: "login-input",
                  type: "text",
                  value: captchaCode,
                  maxLength: 4,
                  autoComplete: "off",
                  onChange: (e) => void onCaptchaInput(e.target.value)
                }
              ),
              captcha?.imageBase64 ? /* @__PURE__ */ jsxRuntimeExports.jsx(
                "img",
                {
                  className: "login-captcha-img",
                  src: captcha.imageBase64,
                  alt: t("loginCaptcha"),
                  title: t("loginCaptchaRefresh"),
                  onClick: () => void refreshCaptcha()
                }
              ) : /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "login-captcha-refresh", onClick: () => void refreshCaptcha(), children: t("loginCaptchaRefresh") })
            ] })
          ] }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("label", { className: "login-label", htmlFor: "lv-sms", children: t("loginSmsCode") }),
          /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "login-captcha-row", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "input",
              {
                id: "lv-sms",
                className: "login-input",
                type: "text",
                value: smsCode,
                autoComplete: "one-time-code",
                onChange: (e) => setSmsCode(e.target.value),
                onKeyDown: (e) => {
                  if (e.key === "Enter" && !e.nativeEvent.isComposing) void submitMobile();
                }
              }
            ),
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "button",
              {
                className: "login-sms-btn",
                disabled: smsSending || smsCountdown > 0 || !captchaDisabled && !captchaOk,
                onClick: () => void sendSms(mobile, country),
                children: smsCountdown > 0 ? t("loginSmsCountdown").replace("{s}", String(smsCountdown)) : t("loginSmsGet")
              }
            )
          ] })
        ] }),
        error ? /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "login-error", role: "alert", children: error }) : captchaLoadFailed && !captcha && !captchaDisabled ? /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "login-error", role: "alert", children: t("loginErrKaptchaLoad") }) : null,
        /* @__PURE__ */ jsxRuntimeExports.jsxs("label", { className: "login-privacy", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx(
            "input",
            {
              type: "checkbox",
              checked: privacyChecked,
              onChange: (e) => setPrivacyChecked(e.target.checked)
            }
          ),
          /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { children: [
            t("loginPrivacyAgree"),
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "button",
              {
                type: "button",
                className: "login-link",
                onClick: () => void window.aiOffice.authOpenExternal?.(privacyUrl),
                children: t("loginPrivacyPolicy")
              }
            ),
            t("loginPrivacyAnd"),
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "button",
              {
                type: "button",
                className: "login-link",
                onClick: () => void window.aiOffice.authOpenExternal?.(termsUrl),
                children: t("loginTermsOfService")
              }
            )
          ] })
        ] }),
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            className: "login-submit",
            disabled: submitDisabled,
            onClick: () => tab === "account" ? void submitAccount() : void submitMobile(),
            children: submitting ? t("waitingShort") : t("login")
          }
        ),
        showRegister && /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            type: "button",
            className: "login-link login-register",
            onClick: () => void window.aiOffice.authOpenExternal?.(
              `${serverUrl.replace(/\/+$/, "")}/admin/auth/register`
            ),
            children: t("loginRegisterAccount")
          }
        )
      ] })
    ] }),
    privacyConfirmOpen && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "modal-overlay", onClick: () => setPrivacyConfirmOpen(false), children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "modal", role: "dialog", "aria-modal": "true", onClick: (e) => e.stopPropagation(), children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { children: t("loginPrivacyModalTitle") }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: t("loginPrivacyModalDesc") }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("p", { children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("button", { type: "button", className: "login-link", onClick: () => void window.aiOffice.authOpenExternal?.(privacyUrl), children: t("loginPrivacyPolicy") }),
        t("loginPrivacyAnd"),
        /* @__PURE__ */ jsxRuntimeExports.jsx("button", { type: "button", className: "login-link", onClick: () => void window.aiOffice.authOpenExternal?.(termsUrl), children: t("loginTermsOfService") })
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: t("loginPrivacyModalConfirm") }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "modal-buttons", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "btn btn-secondary", onClick: () => setPrivacyConfirmOpen(false), children: t("cancel") }),
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            className: "btn btn-primary",
            onClick: () => {
              setPrivacyChecked(true);
              setPrivacyConfirmOpen(false);
              privacyConfirmAction.current?.();
            },
            children: t("loginPrivacyAgreeAndLogin")
          }
        )
      ] })
    ] }) }),
    weakOpen && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "modal-overlay", onClick: () => setWeakOpen(false), children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "modal", role: "dialog", "aria-modal": "true", onClick: (e) => e.stopPropagation(), children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { children: t("loginPwWeakTitle") }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: t("loginPwWeakDesc") }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: t("loginPwSuggestion") }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: t("loginPwWeakConfirm") }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "modal-buttons", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            className: "btn btn-secondary",
            onClick: () => {
              setWeakOpen(false);
              weakAction.current = null;
            },
            children: t("loginPwWeakCancel")
          }
        ),
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            className: "btn btn-secondary",
            onClick: () => {
              setWeakOpen(false);
              void doAccountLogin("change-password");
            },
            children: t("loginPwWeakChange")
          }
        ),
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            className: "btn btn-primary",
            onClick: () => {
              setWeakOpen(false);
              void doAccountLogin("login");
            },
            children: t("loginPwWeakContinue")
          }
        )
      ] })
    ] }) })
  ] }) });
}
const PAGE_SIZE = 50;
const GREET_ASK_KEYS = [
  "greetAsk1",
  "greetAsk2",
  "greetAsk3",
  "greetAsk4",
  "greetAsk5",
  "greetAsk6"
];
const FILE_ICONS = {
  docx: iconDocx,
  xlsx: iconXlsx,
  xlsm: iconXlsx,
  pptx: iconPptx,
  pdf: iconPdf,
  md: iconMd,
  markdown: iconMd
};
const OPEN_LOCAL_EXTENSIONS = ".docx / .xlsx / .xlsm / .xls / .csv / .pptx / .pdf / .md";
function FileBadge({ ext, size }) {
  const icon = FILE_ICONS[ext];
  if (icon) {
    return /* @__PURE__ */ jsxRuntimeExports.jsx("img", { src: icon, width: size, height: size, alt: "", "aria-hidden": "true" });
  }
  const label = ext ? ext[0].toUpperCase() : "?";
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: size, height: size, viewBox: "0 0 32 32", "aria-hidden": "true", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("rect", { width: "32", height: "32", rx: "7.5", fill: "#98a2b3" }),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "text",
      {
        x: "16",
        y: "16.5",
        textAnchor: "middle",
        dominantBaseline: "central",
        fill: "#fff",
        fontSize: 17,
        fontWeight: "700",
        fontFamily: "system-ui, -apple-system, 'Segoe UI', sans-serif",
        children: label
      }
    )
  ] });
}
function formatModified(mtimeMs, i18n) {
  const date = new Date(mtimeMs);
  const now = /* @__PURE__ */ new Date();
  const startOfDay = (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime();
  const days = Math.round((startOfDay(now) - startOfDay(date)) / 864e5);
  if (days <= 0) {
    return `${i18n.t("today")} · ${date.toLocaleTimeString(i18n.dateLocale, { hour: "2-digit", minute: "2-digit" })}`;
  }
  if (days === 1) return i18n.t("yesterday");
  return date.toLocaleDateString(i18n.dateLocale, { month: "short", day: "numeric" });
}
function formatSize(bytes) {
  if (bytes >= 1048576) return `${(bytes / 1048576).toFixed(1)} MB`;
  return `${Math.max(1, Math.round(bytes / 1024))} KB`;
}
function parentDir(path) {
  const parts = path.split(/[\\/]/).filter(Boolean);
  return parts[parts.length - 2] ?? "";
}
function fileName(path) {
  return path.split(/[\\/]/).pop() ?? path;
}
function baseName(entry) {
  return entry.ext ? entry.name.slice(0, -(entry.ext.length + 1)) : entry.name;
}
function hasProjectApi() {
  return typeof window.aiOfficeProject !== "undefined";
}
const FILTERS = [
  { key: "all", label: "filterAll" },
  { key: "docx", label: "filterDocs" },
  { key: "xlsx", label: "filterSheets" },
  { key: "pptx", label: "filterSlides" },
  { key: "pdf", label: "filterPdf" },
  { key: "md", label: "filterMd" }
];
function SortCheck({ visible }) {
  return /* @__PURE__ */ jsxRuntimeExports.jsx(
    "svg",
    {
      className: "cloud-sort-check",
      width: "12",
      height: "12",
      viewBox: "0 0 16 16",
      fill: "none",
      "aria-hidden": "true",
      style: visible ? void 0 : { visibility: "hidden" },
      children: /* @__PURE__ */ jsxRuntimeExports.jsx(
        "path",
        {
          d: "M3 8.5L6.5 12L13 4.5",
          stroke: "currentColor",
          strokeWidth: "1.6",
          strokeLinecap: "round",
          strokeLinejoin: "round"
        }
      )
    }
  );
}
function ProjectPanel({ projects, selectedId, onSelect, onRefresh }) {
  const { t } = useI18n();
  const [creating, setCreating] = reactExports.useState(false);
  const [newName, setNewName] = reactExports.useState("");
  const [projMenu, setProjMenu] = reactExports.useState(null);
  const [renaming, setRenaming] = reactExports.useState(null);
  const newInputRef = reactExports.useRef(null);
  const projMenuWrapRef = reactExports.useRef(null);
  reactExports.useEffect(() => {
    if (creating && newInputRef.current) newInputRef.current.focus();
  }, [creating]);
  useDismissablePopover(projMenu !== null, () => setProjMenu(null), {
    inside: () => [projMenuWrapRef.current]
  });
  reactExports.useEffect(() => {
    if (!projMenu) return;
    const close = () => setProjMenu(null);
    window.addEventListener("scroll", close, true);
    return () => window.removeEventListener("scroll", close, true);
  }, [projMenu]);
  const commitCreate = async () => {
    const name = newName.trim();
    setCreating(false);
    setNewName("");
    if (!name) return;
    try {
      const created = await window.aiOfficeProject?.createProject(name);
      if (created?.id) {
        void window.aiOffice?.officeProjectSync?.({ action: "create", clientUid: created.id, name });
      }
    } catch (error) {
      window.alert(error instanceof Error ? error.message : String(error));
      return;
    }
    onRefresh();
  };
  const commitRename = async () => {
    if (!renaming) return;
    const name = renaming.value.trim();
    const id = renaming.id;
    setRenaming(null);
    if (!name) return;
    try {
      await window.aiOfficeProject?.renameProject(id, name);
      void window.aiOffice?.officeProjectSync?.({ action: "rename", clientUid: id, name });
    } catch (error) {
      window.alert(error instanceof Error ? error.message : String(error));
      return;
    }
    onRefresh();
  };
  const [confirmDeleteId, setConfirmDeleteId] = reactExports.useState(null);
  const doDelete = (id) => {
    setProjMenu(null);
    setConfirmDeleteId(id);
  };
  const confirmDeleteNow = async () => {
    const id = confirmDeleteId;
    setConfirmDeleteId(null);
    if (!id) return;
    try {
      await window.aiOfficeProject?.deleteProject(id);
      void window.aiOffice?.officeProjectSync?.({ action: "delete", clientUid: id });
    } catch (error) {
      window.alert(error instanceof Error ? error.message : String(error));
      return;
    }
    if (selectedId === id) onSelect(null);
    onRefresh();
  };
  reactExports.useEffect(() => {
    if (!confirmDeleteId) return;
    const onKeyDown = (e) => {
      if (e.key === "Escape") setConfirmDeleteId(null);
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [confirmDeleteId]);
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "proj-panel", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "proj-panel-head", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "proj-panel-title", children: t("projects") }),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "button",
        {
          className: "proj-add-btn",
          "data-tip": t("newProject"),
          onClick: () => setCreating(true),
          "aria-label": t("newProject"),
          children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "14", height: "14", viewBox: "0 0 14 14", fill: "none", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
            "path",
            {
              d: "M7 1v12M1 7h12",
              stroke: "currentColor",
              strokeWidth: "1.7",
              strokeLinecap: "round"
            }
          ) })
        }
      )
    ] }),
    creating && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "proj-new-row", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
      "input",
      {
        ref: newInputRef,
        className: "proj-rename-input",
        placeholder: t("projectName"),
        value: newName,
        onChange: (e) => setNewName(e.target.value),
        onBlur: () => void commitCreate(),
        onKeyDown: (e) => {
          if (e.nativeEvent.isComposing) return;
          if (e.key === "Enter") void commitCreate();
          if (e.key === "Escape") {
            setCreating(false);
            setNewName("");
          }
        }
      }
    ) }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("ul", { className: "proj-list", children: projects.map((proj) => {
      const isActive = selectedId === proj.id;
      const isRenaming = renaming?.id === proj.id;
      return /* @__PURE__ */ jsxRuntimeExports.jsxs("li", { className: `proj-item${isActive ? " active" : ""}`, children: [
        /* @__PURE__ */ jsxRuntimeExports.jsxs(
          "div",
          {
            className: "proj-item-main",
            role: "button",
            tabIndex: 0,
            onClick: () => onSelect(isActive ? null : proj.id),
            onKeyDown: (e) => {
              if (e.key === "Enter") onSelect(isActive ? null : proj.id);
            },
            children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "proj-item-icon", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "16", height: "16", viewBox: "0 0 16 16", fill: "none", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
                "path",
                {
                  d: "M1.5 4A1.5 1.5 0 0 1 3 2.5h3.1c.44 0 .85.19 1.13.52L8.4 4.4H13A1.5 1.5 0 0 1 14.5 5.9v5.6A1.5 1.5 0 0 1 13 13H3a1.5 1.5 0 0 1-1.5-1.5V4z",
                  stroke: "currentColor",
                  strokeWidth: "1.2",
                  strokeLinejoin: "round"
                }
              ) }) }),
              isRenaming ? /* @__PURE__ */ jsxRuntimeExports.jsx(
                "input",
                {
                  className: "proj-rename-input inline",
                  value: renaming.value,
                  autoFocus: true,
                  onFocus: (e) => e.target.select(),
                  onClick: (e) => e.stopPropagation(),
                  onChange: (e) => setRenaming({ id: proj.id, value: e.target.value }),
                  onBlur: () => void commitRename(),
                  onKeyDown: (e) => {
                    e.stopPropagation();
                    if (e.nativeEvent.isComposing) return;
                    if (e.key === "Enter") void commitRename();
                    if (e.key === "Escape") setRenaming(null);
                  }
                }
              ) : /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "proj-item-name", children: proj.isDefault ? t("defaultProject") : proj.name }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "proj-item-meta", children: /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "proj-item-count", children: proj.fileCount }) })
            ]
          }
        ),
        !proj.isDefault && /* @__PURE__ */ jsxRuntimeExports.jsxs(
          "div",
          {
            className: "proj-menu-wrap",
            ref: projMenu?.id === proj.id ? projMenuWrapRef : void 0,
            children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "button",
                {
                  className: "proj-more-btn",
                  "aria-label": t("projMoreActions", { name: proj.name }),
                  "aria-expanded": projMenu?.id === proj.id,
                  onClick: (e) => {
                    e.stopPropagation();
                    if (projMenu?.id === proj.id) {
                      setProjMenu(null);
                      return;
                    }
                    const rect = e.currentTarget.getBoundingClientRect();
                    setProjMenu({
                      id: proj.id,
                      top: rect.bottom + 4,
                      right: window.innerWidth - rect.right
                    });
                  },
                  children: /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "14", height: "14", viewBox: "0 0 16 16", "aria-hidden": "true", children: [
                    /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "3.2", cy: "8", r: "1.35", fill: "currentColor" }),
                    /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "8", cy: "8", r: "1.35", fill: "currentColor" }),
                    /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "12.8", cy: "8", r: "1.35", fill: "currentColor" })
                  ] })
                }
              ),
              projMenu?.id === proj.id && /* @__PURE__ */ jsxRuntimeExports.jsxs(
                "div",
                {
                  className: "proj-menu",
                  role: "menu",
                  style: { top: projMenu.top, right: projMenu.right },
                  children: [
                    /* @__PURE__ */ jsxRuntimeExports.jsx(
                      "button",
                      {
                        role: "menuitem",
                        onClick: (e) => {
                          e.stopPropagation();
                          setProjMenu(null);
                          setRenaming({ id: proj.id, value: proj.name });
                        },
                        children: t("rename")
                      }
                    ),
                    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "row-menu-divider" }),
                    /* @__PURE__ */ jsxRuntimeExports.jsx(
                      "button",
                      {
                        role: "menuitem",
                        className: "danger",
                        onClick: (e) => {
                          e.stopPropagation();
                          doDelete(proj.id);
                        },
                        children: t("deleteProject")
                      }
                    )
                  ]
                }
              )
            ]
          }
        )
      ] }, proj.id);
    }) }),
    confirmDeleteId && (() => {
      const [confirmTitle, ...confirmBody] = t("deleteProjectConfirm").split("\n");
      return /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "modal-overlay", onClick: () => setConfirmDeleteId(null), children: /* @__PURE__ */ jsxRuntimeExports.jsxs(
        "div",
        {
          className: "modal",
          role: "dialog",
          "aria-modal": "true",
          "aria-label": confirmTitle,
          onClick: (event) => event.stopPropagation(),
          children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { children: confirmTitle }),
            /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: confirmBody.join("\n") }),
            /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "modal-buttons", children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "button",
                {
                  className: "btn btn-secondary",
                  autoFocus: true,
                  onClick: () => setConfirmDeleteId(null),
                  children: t("cancel")
                }
              ),
              /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "btn btn-danger", onClick: () => void confirmDeleteNow(), children: t("delete") })
            ] })
          ]
        }
      ) });
    })()
  ] });
}
function AccountEntry({
  onStatusChange,
  collapsed = false
}) {
  const { t, lang, setLang } = useI18n();
  const [status, setStatus] = reactExports.useState(null);
  const [menuOpen, setMenuOpen] = reactExports.useState(false);
  const [menuPos, setMenuPos] = reactExports.useState(null);
  const [theme, setTheme] = reactExports.useState("system");
  const btnRef = reactExports.useRef(null);
  const menuRef = reactExports.useRef(null);
  reactExports.useEffect(() => {
    onStatusChange?.(status);
  }, [status, onStatusChange]);
  const [loginOpen, setLoginOpen] = reactExports.useState(false);
  const [settingsOpen, setSettingsOpen] = reactExports.useState(false);
  const [loggingOut, setLoggingOut] = reactExports.useState(false);
  const statusSeq = reactExports.useRef(0);
  reactExports.useEffect(() => {
    let alive = true;
    void window.aiOffice.accountStatus?.().then((s) => {
      if (alive) setStatus(s);
    });
    return () => {
      alive = false;
    };
  }, []);
  const loggedIn = status?.loggedIn ?? false;
  const email = status?.email ?? "";
  const initial = email ? email[0].toUpperCase() : loggedIn ? "B" : "?";
  reactExports.useEffect(() => {
    return window.aiOffice.onShowLogin?.(() => {
      void window.aiOffice.accountStatus?.().then((s) => {
        if (s.loggedIn) {
          setStatus(s);
          return;
        }
        setStatus(s);
        setLoginOpen(true);
        void window.aiOfficeTabs?.activate("home");
      });
    });
  }, []);
  const doLogout = () => {
    setLoggingOut(true);
    statusSeq.current++;
    void window.aiOffice.accountLogout().then(() => {
      setLoggingOut(false);
      setStatus({ loggedIn: false });
    });
  };
  const refreshStatus = () => {
    const seq = statusSeq.current;
    return window.aiOffice.accountStatus?.().then((s) => {
      if (seq === statusSeq.current) setStatus(s);
    });
  };
  const openMenu = () => {
    const rect = btnRef.current?.getBoundingClientRect();
    if (rect) setMenuPos({ left: rect.left, bottom: window.innerHeight - rect.top + 8 });
    setMenuOpen(true);
  };
  useDismissablePopover(menuOpen, () => setMenuOpen(false), {
    inside: () => [btnRef.current, menuRef.current]
  });
  reactExports.useEffect(() => {
    if (!menuOpen) return;
    const onKey = (e) => {
      if (e.key === "Escape") setMenuOpen(false);
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [menuOpen]);
  reactExports.useEffect(() => {
    if (!menuOpen) return;
    void window.aiOffice.getTheme?.().then((th) => setTheme(th));
  }, [menuOpen]);
  const applyTheme = (next) => {
    setTheme(next);
    void window.aiOffice.setTheme(next);
    if (next === "system") document.documentElement.removeAttribute("data-theme");
    else document.documentElement.setAttribute("data-theme", next);
  };
  const runPrimary = () => {
    setMenuOpen(false);
    if (loggedIn) {
      void refreshStatus();
      setSettingsOpen(true);
    } else {
      setLoginOpen(true);
    }
  };
  const handleClick = () => {
    if (menuOpen) {
      setMenuOpen(false);
      return;
    }
    openMenu();
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "account-entry", children: [
    settingsOpen && /* @__PURE__ */ jsxRuntimeExports.jsx(
      SettingsModal,
      {
        status,
        loggingOut,
        onClose: () => setSettingsOpen(false),
        onLogout: doLogout
      }
    ),
    loginOpen && /* @__PURE__ */ jsxRuntimeExports.jsx(
      LoginView,
      {
        onClose: () => setLoginOpen(false),
        onLoggedIn: () => void refreshStatus()
      }
    ),
    menuOpen && menuPos && /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "div",
      {
        ref: menuRef,
        className: "account-menu",
        style: { left: menuPos.left, bottom: menuPos.bottom },
        role: "menu",
        "aria-label": t("settings"),
        children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("button", { type: "button", className: "account-menu-primary", onClick: runPrimary, children: loggedIn ? t("settings") : t("loginGenspark") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "account-menu-sep" }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "account-menu-caption", children: t("language") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "account-menu-grid", role: "group", "aria-label": t("language"), children: LANG_OPTIONS.map((opt) => /* @__PURE__ */ jsxRuntimeExports.jsx(
            "button",
            {
              type: "button",
              role: "menuitemradio",
              "aria-checked": lang === opt.value,
              className: lang === opt.value ? "checked" : "",
              onClick: () => {
                setLang(opt.value);
                setMenuOpen(false);
              },
              children: opt.label
            },
            opt.value
          )) }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "account-menu-caption", children: t("theme") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "account-menu-grid account-menu-grid-wide", role: "group", "aria-label": t("theme"), children: THEME_OPTIONS.map((opt) => /* @__PURE__ */ jsxRuntimeExports.jsx(
            "button",
            {
              type: "button",
              role: "menuitemradio",
              "aria-checked": theme === opt.value,
              className: theme === opt.value ? "checked" : "",
              onClick: () => {
                applyTheme(opt.value);
                setMenuOpen(false);
              },
              children: t(opt.labelKey)
            },
            opt.value
          )) })
        ]
      }
    ),
    /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "button",
      {
        ref: btnRef,
        type: "button",
        className: `account-btn${collapsed ? " icon-only" : ""}`,
        onClick: handleClick,
        "aria-haspopup": "menu",
        "aria-expanded": menuOpen,
        "aria-label": loggedIn ? email || t("loggedInGenspark") : t("settings"),
        title: collapsed ? loggedIn ? email || t("loggedInGenspark") : t("settings") : void 0,
        children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "account-avatar" + (loggedIn ? " logged-in" : ""), "aria-hidden": "true", children: initial }),
          !collapsed && /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "account-text", children: loggedIn ? email || t("loggedInGenspark") : t("settings") }),
          !collapsed && /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "account-chevron", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "12", height: "12", viewBox: "0 0 16 16", fill: "none", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
            "path",
            {
              d: "M4 10L8 6L12 10",
              stroke: "currentColor",
              strokeWidth: "1.5",
              strokeLinecap: "round",
              strokeLinejoin: "round"
            }
          ) }) })
        ]
      }
    )
  ] });
}
function CloudProjectsView() {
  const i18n = useI18n();
  const { t } = i18n;
  const [docs, setDocs] = reactExports.useState([]);
  const [total, setTotal] = reactExports.useState(0);
  const [loading, setLoading] = reactExports.useState(true);
  const [error, setError] = reactExports.useState(null);
  const [signedIn, setSignedIn] = reactExports.useState(false);
  const [uploading, setUploading] = reactExports.useState(false);
  const [query, setQuery] = reactExports.useState("");
  const load = async (content) => {
    setLoading(true);
    setError(null);
    try {
      const state = await window.aiOffice.authGetState?.();
      setSignedIn(Boolean(state?.loggedIn));
      if (!state?.loggedIn) {
        setDocs([]);
        setTotal(0);
        return;
      }
      const page = await window.aiOffice.officeDocsList?.({
        page: 1,
        size: 100,
        ...content ? { content } : {}
      });
      setDocs(page?.content ?? []);
      setTotal(page?.totalElements ?? 0);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
      setDocs([]);
    } finally {
      setLoading(false);
    }
  };
  reactExports.useEffect(() => {
    void load(query || void 0);
  }, []);
  const pickAndUpload = async () => {
    if (uploading) return;
    const picked = await window.aiOffice.pickDocumentFile?.();
    if (!picked || picked.canceled || !picked.filePaths?.length) return;
    setUploading(true);
    setError(null);
    try {
      await window.aiOffice.officeDocUpload?.({
        fileName: picked.filePaths[0],
        filePath: picked.filePaths[0]
      });
      await load(query || void 0);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    } finally {
      setUploading(false);
    }
  };
  const openDownload = async (doc) => {
    const url = await buildDocUrl(doc);
    if (!url) return;
    void window.aiOffice.openExternalUrl?.(url);
  };
  const copyLink = async (doc) => {
    const url = await buildDocUrl(doc);
    if (!url) return;
    await window.aiOffice.copyText?.(url);
  };
  const removeDoc = async (uid) => {
    try {
      await window.aiOffice.officeDocDelete?.(uid);
      await load(query || void 0);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsx("main", { className: "content", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("section", { className: "cloud-projects", "aria-label": t("navCloud"), children: [
    /* @__PURE__ */ jsxRuntimeExports.jsxs("header", { className: "cloud-hero", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "cloud-hero-top", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("h1", { className: "cloud-title", children: t("navCloud") }),
        signedIn && /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "cloud-controls", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "cloud-refresh", onClick: () => void load(query || void 0), children: t("cloudRefresh") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx(
            "button",
            {
              className: "cloud-refresh",
              disabled: uploading,
              onClick: () => void pickAndUpload(),
              children: uploading ? t("waitingShort") : t("cloudUpload")
            }
          )
        ] })
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "cloud-subtitle", children: t("cloudSubtitle") }),
      signedIn && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "cloud-search", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
        "input",
        {
          className: "cloud-search-input",
          type: "search",
          placeholder: t("cloudSearchPlaceholder").replace("{n}", String(total)),
          value: query,
          spellCheck: false,
          onChange: (e) => setQuery(e.target.value),
          onKeyDown: (e) => {
            if (e.key === "Enter" && !e.nativeEvent.isComposing) void load(query || void 0);
          }
        }
      ) })
    ] }),
    error && /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "cloud-error", role: "alert", children: [
      error,
      /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "cloud-retry", onClick: () => void load(query || void 0), children: t("cloudRetry") })
    ] }),
    !signedIn && !loading ? /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "cloud-empty", children: /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: t("cloudLoginHint") }) }) : loading ? /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "cloud-empty", children: /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: t("cloudLoadMore") }) }) : docs.length === 0 ? /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "cloud-empty", children: /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: t("cloudEmpty") }) }) : /* @__PURE__ */ jsxRuntimeExports.jsx("ul", { className: "cloud-list", children: docs.map((doc) => /* @__PURE__ */ jsxRuntimeExports.jsxs("li", { className: "cloud-item", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "cloud-item-main", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "cloud-item-title", children: doc.title || doc.uid }),
        /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { className: "cloud-item-meta", children: [
          doc.fileType,
          doc.fileSize ? ` · ${Math.max(1, Math.round(doc.fileSize / 1024))} KB` : ""
        ] })
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "cloud-item-actions", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "cloud-open", onClick: () => void openDownload(doc), children: t("cloudOpenInBrowser") }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "cloud-open", onClick: () => void copyLink(doc), children: t("cloudCopyLink") }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "cloud-open", onClick: () => void removeDoc(doc.uid), children: t("delete") })
      ] })
    ] }, doc.uid)) })
  ] }) });
}
function DropToOpenOverlay() {
  const [visible, setVisible] = reactExports.useState(false);
  reactExports.useEffect(() => {
    let depth = 0;
    const hasFiles = (ev) => ev.dataTransfer?.types.includes("Files") ?? false;
    const onDragEnter = (ev) => {
      if (!hasFiles(ev)) return;
      depth += 1;
      setVisible(true);
    };
    const onDragLeave = (ev) => {
      if (!hasFiles(ev)) return;
      depth = Math.max(0, depth - 1);
      if (depth === 0) setVisible(false);
    };
    const onHide = () => {
      depth = 0;
      setVisible(false);
    };
    window.addEventListener("dragenter", onDragEnter);
    window.addEventListener("dragleave", onDragLeave);
    window.addEventListener("drop", onHide);
    window.addEventListener("blur", onHide);
    return () => {
      window.removeEventListener("dragenter", onDragEnter);
      window.removeEventListener("dragleave", onDragLeave);
      window.removeEventListener("drop", onHide);
      window.removeEventListener("blur", onHide);
    };
  }, []);
  const { t } = useI18n();
  if (!visible) return null;
  return /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "home-drop-overlay", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "home-drop-card", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "40", height: "40", viewBox: "0 0 24 24", fill: "none", "aria-hidden": "true", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "path",
        {
          d: "M12 3.5v11M7.5 10.5l4.5 4.5 4.5-4.5",
          stroke: "currentColor",
          strokeWidth: "1.6",
          strokeLinecap: "round",
          strokeLinejoin: "round"
        }
      ),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "path",
        {
          d: "M4 16.5v2A1.5 1.5 0 0 0 5.5 20h13a1.5 1.5 0 0 0 1.5-1.5v-2",
          stroke: "currentColor",
          strokeWidth: "1.6",
          strokeLinecap: "round"
        }
      )
    ] }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("h2", { children: t("dropToOpenTitle") }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: OPEN_LOCAL_EXTENSIONS })
  ] }) });
}
function Home() {
  const i18n = useI18n();
  const { t, lang } = i18n;
  const [entries, setEntries] = reactExports.useState([]);
  const [listTotal, setListTotal] = reactExports.useState(0);
  const [navCounts, setNavCounts] = reactExports.useState({ recent: 0, starred: 0 });
  const [loadingMore, setLoadingMore] = reactExports.useState(false);
  const [view, setView] = reactExports.useState("recent");
  const [cloudMode, setCloudMode] = reactExports.useState(false);
  const [sidebarCollapsed, setSidebarCollapsed] = reactExports.useState(
    () => localStorage.getItem("home.sidebar-collapsed") === "1"
  );
  reactExports.useEffect(() => {
    localStorage.setItem("home.sidebar-collapsed", sidebarCollapsed ? "1" : "0");
  }, [sidebarCollapsed]);
  const [filter, setFilter] = reactExports.useState("all");
  const [fileSort, setFileSort] = reactExports.useState("recent");
  const [fileSortMenuOpen, setFileSortMenuOpen] = reactExports.useState(false);
  const fileSortRef = reactExports.useRef(null);
  const [rowMenu, setRowMenu] = reactExports.useState(null);
  const rowMenuWrapRef = reactExports.useRef(null);
  const [selected, setSelected] = reactExports.useState(/* @__PURE__ */ new Set());
  const [renaming, setRenaming] = reactExports.useState(null);
  const [confirmDelete, setConfirmDelete] = reactExports.useState(null);
  const [confirmMissing, setConfirmMissing] = reactExports.useState(null);
  const [accountName, setAccountName] = reactExports.useState("");
  const [loggedIn, setLoggedIn] = reactExports.useState(false);
  const handleAccountStatus = reactExports.useCallback((s) => {
    const on = s?.loggedIn ?? false;
    setLoggedIn(on);
    if (!on) setCloudMode(false);
    const name = on ? (s?.email ?? "").split("@")[0] : "";
    setAccountName(name ? name[0].toUpperCase() + name.slice(1) : "");
  }, []);
  const [greetAskKey] = reactExports.useState(
    () => GREET_ASK_KEYS[Math.floor(Math.random() * GREET_ASK_KEYS.length)]
  );
  const [projects, setProjects] = reactExports.useState([]);
  const [selectedProjectId, setSelectedProjectId] = reactExports.useState(null);
  const projectMode = hasProjectApi();
  const requestSeq = reactExports.useRef(0);
  const entriesLen = reactExports.useRef(0);
  entriesLen.current = entries.length;
  const reload = (keepCount) => {
    const seq = ++requestSeq.current;
    const ext = filter === "all" ? void 0 : filter;
    const limit = keepCount ? Math.max(entriesLen.current, PAGE_SIZE) : PAGE_SIZE;
    const primary = view === "recent" ? window.aiOffice.recents : window.aiOffice.starred;
    const secondary = view === "recent" ? window.aiOffice.starred : window.aiOffice.recents;
    void primary({ offset: 0, limit, ext }).then((page) => {
      if (seq !== requestSeq.current) return;
      setEntries(page.entries);
      setListTotal(page.total);
      setNavCounts(
        (prev) => view === "recent" ? { ...prev, recent: visiblePageCount(page) } : { ...prev, starred: visiblePageCount(page) }
      );
    });
    void secondary({ offset: 0, limit: 0, ext }).then((page) => {
      if (seq !== requestSeq.current) return;
      setNavCounts(
        (prev) => view === "recent" ? { ...prev, starred: visiblePageCount(page) } : { ...prev, recent: visiblePageCount(page) }
      );
    });
    if (projectMode) {
      void window.aiOfficeProject.listProjects().then(setProjects);
    }
  };
  const reloadRef = reactExports.useRef(reload);
  reloadRef.current = reload;
  const [projectTick, setProjectTick] = reactExports.useState(0);
  const refresh = () => {
    reloadRef.current(true);
    setProjectTick((n) => n + 1);
  };
  reactExports.useEffect(() => {
    reloadRef.current(false);
  }, [view, filter]);
  reactExports.useEffect(() => {
    const onFocus = () => {
      reloadRef.current(true);
      setProjectTick((n) => n + 1);
    };
    window.addEventListener("focus", onFocus);
    return () => window.removeEventListener("focus", onFocus);
  }, []);
  const hasMore = entries.length < listTotal;
  useDismissablePopover(fileSortMenuOpen, () => setFileSortMenuOpen(false), {
    inside: () => [fileSortRef.current]
  });
  const loadMore = () => {
    if (loadingMore || !hasMore) return;
    setLoadingMore(true);
    const seq = requestSeq.current;
    const ext = filter === "all" ? void 0 : filter;
    const api = view === "recent" ? window.aiOffice.recents : window.aiOffice.starred;
    void api({ offset: entriesLen.current, limit: PAGE_SIZE, ext }).then((page) => {
      setLoadingMore(false);
      if (seq !== requestSeq.current) return;
      setEntries((prev) => [...prev, ...page.entries]);
      setListTotal(page.total);
    });
  };
  const loadMoreRef = reactExports.useRef(loadMore);
  loadMoreRef.current = loadMore;
  reactExports.useEffect(() => {
    if (fileSort === "oldest" && hasMore) loadMoreRef.current();
  }, [fileSort, hasMore, entries.length]);
  const sentinelRef = reactExports.useRef(null);
  reactExports.useEffect(() => {
    const el = sentinelRef.current;
    if (!el) return;
    const observer = new IntersectionObserver(
      (records) => {
        if (records.some((r) => r.isIntersecting)) loadMoreRef.current();
      },
      { rootMargin: "240px" }
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, [hasMore, entries.length]);
  useDismissablePopover(rowMenu !== null, () => setRowMenu(null), {
    inside: () => [rowMenuWrapRef.current]
  });
  reactExports.useEffect(() => {
    if (rowMenu === null && confirmDelete === null && confirmMissing === null) return;
    const onKeyDown = (event) => {
      if (event.key === "Escape") {
        setRowMenu(null);
        setConfirmDelete(null);
        setConfirmMissing(null);
      }
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [rowMenu, confirmDelete, confirmMissing]);
  const [projectFileEntries, setProjectFileEntries] = reactExports.useState([]);
  const [moveFileMenu, setMoveFileMenu] = reactExports.useState(null);
  const [moveMenuFlip, setMoveMenuFlip] = reactExports.useState(false);
  const moveMenuTimers = reactExports.useRef({
    open: null,
    close: null
  });
  const moveMenuWrapRef = reactExports.useRef(null);
  const openMoveMenu = (path) => {
    setMoveMenuFlip(false);
    setMoveFileMenu(path);
  };
  const measureSubmenu = (el) => {
    if (el && el.getBoundingClientRect().right > document.documentElement.clientWidth - 8) {
      setMoveMenuFlip(true);
    }
  };
  const clearMoveMenuTimer = (kind) => {
    const timers = moveMenuTimers.current;
    if (timers[kind] !== null) {
      window.clearTimeout(timers[kind]);
      timers[kind] = null;
    }
  };
  const [bulkMoveMenu, setBulkMoveMenu] = reactExports.useState(false);
  const bulkMoveWrapRef = reactExports.useRef(null);
  reactExports.useEffect(() => {
    if (!projectMode || !selectedProjectId) {
      setProjectFileEntries([]);
      return;
    }
    let active = true;
    const api = window.aiOfficeProject;
    void api.listFiles(selectedProjectId).then(async (paths) => {
      const stats = await window.aiOffice.statPaths(paths);
      if (!active) return;
      setProjectFileEntries(stats.sort((a, b) => b.mtimeMs - a.mtimeMs));
    });
    return () => {
      active = false;
    };
  }, [projectMode, selectedProjectId, projectTick]);
  reactExports.useEffect(() => {
    if (rowMenu === null) {
      clearMoveMenuTimer("open");
      clearMoveMenuTimer("close");
      setMoveFileMenu(null);
    }
  }, [rowMenu]);
  useDismissablePopover(moveFileMenu !== null, () => setMoveFileMenu(null), {
    inside: () => [moveMenuWrapRef.current]
  });
  useDismissablePopover(bulkMoveMenu, () => setBulkMoveMenu(false), {
    inside: () => [bulkMoveWrapRef.current]
  });
  reactExports.useEffect(() => {
    if (!bulkMoveMenu) return;
    const onKeyDown = (e) => {
      if (e.key === "Escape") setBulkMoveMenu(false);
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [bulkMoveMenu]);
  const renderModifiedHeader = () => /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "cloud-col-sort", ref: fileSortRef, children: [
    /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "button",
      {
        className: "cloud-col-sort-btn",
        "aria-haspopup": "menu",
        "aria-expanded": fileSortMenuOpen,
        onClick: () => setFileSortMenuOpen((o) => !o),
        children: [
          t("colModified"),
          /* @__PURE__ */ jsxRuntimeExports.jsx(
            "svg",
            {
              width: "12",
              height: "12",
              viewBox: "0 0 16 16",
              fill: "none",
              "aria-hidden": "true",
              style: fileSort === "oldest" ? { transform: "rotate(180deg)" } : void 0,
              children: /* @__PURE__ */ jsxRuntimeExports.jsx(
                "path",
                {
                  d: "M8 3v10M4.5 9.5L8 13l3.5-3.5",
                  stroke: "currentColor",
                  strokeWidth: "1.4",
                  strokeLinecap: "round",
                  strokeLinejoin: "round"
                }
              )
            }
          )
        ]
      }
    ),
    fileSortMenuOpen && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "cloud-sort-menu", role: "menu", children: ["recent", "oldest"].map((key) => /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "button",
      {
        className: fileSort === key ? "active" : "",
        role: "menuitemradio",
        "aria-checked": fileSort === key,
        onClick: () => {
          setFileSort(key);
          setFileSortMenuOpen(false);
        },
        children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx(SortCheck, { visible: fileSort === key }),
          t(key === "recent" ? "cloudSortRecent" : "cloudSortOldest")
        ]
      },
      key
    )) })
  ] });
  const selectedPaths = entries.filter((e) => selected.has(e.path)).map((e) => e.path);
  const allSelected = entries.length > 0 && selectedPaths.length === entries.length;
  const projSelectedPaths = projectFileEntries.filter((e) => selected.has(e.path)).map((e) => e.path);
  const projAllSelected = projectFileEntries.length > 0 && projSelectedPaths.length === projectFileEntries.length;
  const changeView = (next) => {
    setView(next);
    setSelected(/* @__PURE__ */ new Set());
    setRowMenu(null);
  };
  const changeFilter = (key) => {
    setFilter(key);
    setSelected(/* @__PURE__ */ new Set());
    setRowMenu(null);
  };
  const toggleSelect = (path, on) => {
    setSelected((prev) => {
      const next = new Set(prev);
      if (on) next.add(path);
      else next.delete(path);
      return next;
    });
  };
  const toggleSelectAll = () => {
    setSelected(allSelected ? /* @__PURE__ */ new Set() : new Set(entries.map((e) => e.path)));
  };
  const toggleSelectAllProject = () => {
    setSelected(projAllSelected ? /* @__PURE__ */ new Set() : new Set(projectFileEntries.map((e) => e.path)));
  };
  const toggleStar = (path) => {
    void window.aiOffice.toggleStar(path).then(refresh);
  };
  const removeRecent = (paths) => {
    setRowMenu(null);
    setSelected(/* @__PURE__ */ new Set());
    void window.aiOffice.removeRecent(paths).then(refresh);
  };
  const deleteFiles = (paths) => {
    setRowMenu(null);
    setConfirmDelete(paths);
  };
  const confirmDeleteNow = () => {
    const paths = confirmDelete ?? [];
    setConfirmDelete(null);
    setSelected(/* @__PURE__ */ new Set());
    void window.aiOffice.deleteFiles(paths).then(refresh);
  };
  const duplicateFile = (path) => {
    setRowMenu(null);
    void window.aiOffice.duplicateFile(path).then(refresh);
  };
  const startRename = (entry) => {
    setRowMenu(null);
    setRenaming({ path: entry.path, value: baseName(entry) });
  };
  const commitRename = (entry) => {
    const value = renaming?.value.trim() ?? "";
    setRenaming(null);
    if (!value || value === baseName(entry)) return;
    const newName = entry.ext ? `${value}.${entry.ext}` : value;
    void window.aiOffice.renameFile(entry.path, newName).then((result) => {
      if (!result.ok) window.alert(result.error ?? t("renameFailed"));
      refresh();
    });
  };
  const moveFileTo = async (filePath, targetProjectId) => {
    setMoveFileMenu(null);
    setRowMenu(null);
    try {
      await window.aiOfficeProject?.moveFile(filePath, targetProjectId);
    } catch (error) {
      window.alert(error instanceof Error ? error.message : String(error));
      return;
    }
    refresh();
    if (selectedProjectId) {
      setProjectFileEntries((prev) => prev.filter((e) => e.path !== filePath));
    }
  };
  const moveFilesTo = async (paths, targetProjectId) => {
    setBulkMoveMenu(false);
    setSelected(/* @__PURE__ */ new Set());
    const moved = new Set(paths);
    setProjectFileEntries((prev) => prev.filter((e) => !moved.has(e.path)));
    try {
      for (const path of paths) {
        await window.aiOfficeProject?.moveFile(path, targetProjectId);
      }
    } catch (error) {
      window.alert(error instanceof Error ? error.message : String(error));
    } finally {
      refresh();
    }
  };
  const handleNewDoc = () => {
    void window.aiOffice.newDoc(selectedProjectId ? { projectId: selectedProjectId } : void 0);
  };
  const handleNewSheet = () => {
    void window.aiOffice.newSheet(selectedProjectId ? { projectId: selectedProjectId } : void 0);
  };
  const handleNewSlide = () => {
    void window.aiOffice.newSlide(selectedProjectId ? { projectId: selectedProjectId } : void 0);
  };
  const handleNewMarkdown = () => {
    void window.aiOffice.newMarkdown(
      selectedProjectId ? { projectId: selectedProjectId } : void 0
    );
  };
  const handleNewPdf = () => {
    void window.aiOffice.newPdf(selectedProjectId ? { projectId: selectedProjectId } : void 0);
  };
  const NEW_ITEMS = [
    { ext: "docx", title: t("newDoc"), sub: ".docx", action: handleNewDoc },
    { ext: "xlsx", title: t("newSheet"), sub: ".xlsx", action: handleNewSheet },
    { ext: "pptx", title: t("newSlide"), sub: ".pptx", action: handleNewSlide },
    { ext: "md", title: t("newMarkdown"), sub: ".md", action: handleNewMarkdown },
    { ext: "pdf", title: t("newPdf"), sub: ".pdf", action: handleNewPdf }
  ];
  function renderQuickCards() {
    return /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "quick-cards", children: [
      NEW_ITEMS.map((item) => /* @__PURE__ */ jsxRuntimeExports.jsxs("button", { className: "quick-card", onClick: () => void item.action(), children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx(FileBadge, { ext: item.ext, size: 30 }),
        /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { className: "quick-text", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { className: "quick-title-row", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "quick-title", children: item.title }),
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "ai-chip", children: "AI" })
          ] }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "quick-sub", children: item.sub })
        ] })
      ] }, item.ext)),
      /* @__PURE__ */ jsxRuntimeExports.jsxs(
        "button",
        {
          className: "quick-card",
          onClick: () => void window.aiOffice.browse(),
          "data-tip": OPEN_LOCAL_EXTENSIONS,
          children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "quick-folder", children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "18", height: "18", viewBox: "0 0 16 16", fill: "none", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
              "path",
              {
                d: "M1.5 4A1.5 1.5 0 0 1 3 2.5h3.1c.44 0 .85.19 1.13.52L8.4 4.4H13A1.5 1.5 0 0 1 14.5 5.9v5.6A1.5 1.5 0 0 1 13 13H3a1.5 1.5 0 0 1-1.5-1.5V4z",
                stroke: "currentColor",
                strokeWidth: "1.3",
                strokeLinejoin: "round"
              }
            ) }) }),
            /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { className: "quick-text", children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "quick-title-row", children: /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "quick-title", children: t("openLocal") }) }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "quick-sub", children: OPEN_LOCAL_EXTENSIONS })
            ] })
          ]
        }
      )
    ] });
  }
  function renderFileRow(entry, context) {
    const isRenaming = renaming?.path === entry.path;
    const otherProjects = projects.filter(
      (p) => p.id !== (context === "project" ? selectedProjectId : void 0)
    );
    return /* @__PURE__ */ jsxRuntimeExports.jsx("li", { className: "recent-row", children: /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "div",
      {
        className: `recent-item${entry.missing ? " missing" : ""}`,
        role: "button",
        tabIndex: 0,
        onClick: () => {
          if (isRenaming) return;
          if (entry.missing) setConfirmMissing(entry);
          else void window.aiOffice.openPath(entry.path);
        },
        onKeyDown: (event) => {
          if (event.key === "Enter" && event.target === event.currentTarget) {
            if (entry.missing) setConfirmMissing(entry);
            else void window.aiOffice.openPath(entry.path);
          }
        },
        children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "col-check", onClick: (event) => event.stopPropagation(), children: /* @__PURE__ */ jsxRuntimeExports.jsx(
            "input",
            {
              type: "checkbox",
              className: "row-check",
              checked: selected.has(entry.path),
              onChange: (event) => toggleSelect(entry.path, event.target.checked),
              "aria-label": t("selectFile", { name: entry.name })
            }
          ) }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "recent-icon", children: /* @__PURE__ */ jsxRuntimeExports.jsx(FileBadge, { ext: entry.ext, size: 24 }) }),
          isRenaming ? /* @__PURE__ */ jsxRuntimeExports.jsx(
            "input",
            {
              className: "rename-input",
              value: renaming.value,
              autoFocus: true,
              onFocus: (event) => event.target.select(),
              onClick: (event) => event.stopPropagation(),
              onChange: (event) => setRenaming({ path: entry.path, value: event.target.value }),
              onBlur: () => commitRename(entry),
              onKeyDown: (event) => {
                event.stopPropagation();
                if (event.nativeEvent.isComposing) return;
                if (event.key === "Enter") commitRename(entry);
                if (event.key === "Escape") setRenaming(null);
              }
            }
          ) : /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "recent-name", children: entry.name }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "recent-path", children: parentDir(entry.path) }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "recent-time", children: entry.missing ? "—" : formatModified(entry.mtimeMs, i18n) }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "recent-size", children: entry.missing ? "—" : formatSize(entry.sizeBytes) }),
          /* @__PURE__ */ jsxRuntimeExports.jsx(
            "button",
            {
              className: `star-btn${entry.starred ? " starred" : ""}`,
              "aria-label": entry.starred ? t("unstar") : t("star"),
              onClick: (event) => {
                event.stopPropagation();
                toggleStar(entry.path);
              },
              children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "15", height: "15", viewBox: "0 0 16 16", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
                "path",
                {
                  d: "M8 1.9l1.9 3.85 4.25.62-3.07 3 .72 4.23L8 11.6l-3.8 2 .72-4.23-3.07-3 4.25-.62z",
                  fill: entry.starred ? "#f5a623" : "none",
                  stroke: entry.starred ? "#f5a623" : "currentColor",
                  strokeWidth: "1.2",
                  strokeLinejoin: "round"
                }
              ) })
            }
          ),
          /* @__PURE__ */ jsxRuntimeExports.jsxs(
            "span",
            {
              className: "recent-actions",
              ref: rowMenu === entry.path ? rowMenuWrapRef : void 0,
              onClick: (event) => event.stopPropagation(),
              children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx(
                  "button",
                  {
                    className: "more-btn",
                    "aria-label": t("moreActions"),
                    "aria-expanded": rowMenu === entry.path,
                    onClick: () => setRowMenu(rowMenu === entry.path ? null : entry.path),
                    children: /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 16 16", "aria-hidden": "true", children: [
                      /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "3.2", cy: "8", r: "1.4", fill: "currentColor" }),
                      /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "8", cy: "8", r: "1.4", fill: "currentColor" }),
                      /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "12.8", cy: "8", r: "1.4", fill: "currentColor" })
                    ] })
                  }
                ),
                rowMenu === entry.path && /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "row-menu", role: "menu", children: [
                  /* @__PURE__ */ jsxRuntimeExports.jsx(
                    "button",
                    {
                      role: "menuitem",
                      onClick: () => {
                        setRowMenu(null);
                        void window.aiOffice.openPath(entry.path);
                      },
                      children: t("open")
                    }
                  ),
                  /* @__PURE__ */ jsxRuntimeExports.jsx(
                    "button",
                    {
                      role: "menuitem",
                      onClick: () => {
                        setRowMenu(null);
                        void window.aiOffice.revealPath(entry.path);
                      },
                      children: t("revealInFolder")
                    }
                  ),
                  /* @__PURE__ */ jsxRuntimeExports.jsx(
                    "button",
                    {
                      role: "menuitem",
                      onClick: () => {
                        setRowMenu(null);
                        void navigator.clipboard.writeText(entry.path);
                      },
                      children: t("copyPath")
                    }
                  ),
                  projectMode && otherProjects.length > 0 && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
                    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "row-menu-divider" }),
                    /* @__PURE__ */ jsxRuntimeExports.jsxs(
                      "div",
                      {
                        className: "move-menu-wrap",
                        ref: moveFileMenu === entry.path ? moveMenuWrapRef : void 0,
                        onMouseEnter: () => {
                          clearMoveMenuTimer("close");
                          if (moveFileMenu === entry.path) return;
                          clearMoveMenuTimer("open");
                          moveMenuTimers.current.open = window.setTimeout(
                            () => openMoveMenu(entry.path),
                            160
                          );
                        },
                        onMouseLeave: () => {
                          clearMoveMenuTimer("open");
                          clearMoveMenuTimer("close");
                          moveMenuTimers.current.close = window.setTimeout(
                            () => setMoveFileMenu(null),
                            140
                          );
                        },
                        children: [
                          /* @__PURE__ */ jsxRuntimeExports.jsxs(
                            "button",
                            {
                              role: "menuitem",
                              className: "submenu-trigger",
                              onClick: (e) => {
                                e.stopPropagation();
                                clearMoveMenuTimer("open");
                                clearMoveMenuTimer("close");
                                if (moveFileMenu === entry.path) setMoveFileMenu(null);
                                else openMoveMenu(entry.path);
                              },
                              children: [
                                t("moveToProject"),
                                /* @__PURE__ */ jsxRuntimeExports.jsx(
                                  "svg",
                                  {
                                    width: "11",
                                    height: "11",
                                    viewBox: "0 0 12 12",
                                    "aria-hidden": "true",
                                    style: { marginLeft: "auto" },
                                    children: /* @__PURE__ */ jsxRuntimeExports.jsx(
                                      "path",
                                      {
                                        d: "M4.5 2.5l4 3.5-4 3.5",
                                        stroke: "currentColor",
                                        strokeWidth: "1.3",
                                        strokeLinecap: "round",
                                        fill: "none"
                                      }
                                    )
                                  }
                                )
                              ]
                            }
                          ),
                          moveFileMenu === entry.path && /* @__PURE__ */ jsxRuntimeExports.jsx(
                            "div",
                            {
                              className: `submenu${moveMenuFlip ? " submenu-left" : ""}`,
                              role: "menu",
                              ref: measureSubmenu,
                              children: otherProjects.map((p) => /* @__PURE__ */ jsxRuntimeExports.jsx(
                                "button",
                                {
                                  role: "menuitem",
                                  onClick: () => void moveFileTo(entry.path, p.id),
                                  children: p.isDefault ? t("defaultProject") : p.name
                                },
                                p.id
                              ))
                            }
                          )
                        ]
                      }
                    )
                  ] }),
                  /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "row-menu-divider" }),
                  /* @__PURE__ */ jsxRuntimeExports.jsx("button", { role: "menuitem", onClick: () => startRename(entry), children: t("rename") }),
                  /* @__PURE__ */ jsxRuntimeExports.jsx("button", { role: "menuitem", onClick: () => duplicateFile(entry.path), children: t("duplicate") }),
                  context === "global" && selectedPaths.length === 0 && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
                    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "row-menu-divider" }),
                    /* @__PURE__ */ jsxRuntimeExports.jsx("button", { role: "menuitem", onClick: () => removeRecent([entry.path]), children: t("removeFromList") }),
                    /* @__PURE__ */ jsxRuntimeExports.jsx(
                      "button",
                      {
                        role: "menuitem",
                        className: "danger",
                        onClick: () => deleteFiles([entry.path]),
                        children: t("deleteFiles")
                      }
                    )
                  ] })
                ] })
              ]
            }
          )
        ]
      }
    ) }, entry.path);
  }
  function renderProjectContent() {
    const proj = projects.find((p) => p.id === selectedProjectId);
    if (!proj) return null;
    const otherProjects = projects.filter((p) => p.id !== proj.id);
    return /* @__PURE__ */ jsxRuntimeExports.jsxs("main", { className: "content", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsxs("section", { className: "quick-start", "aria-label": t("secQuickStart"), children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "section-head", children: /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "section-label", children: t("secQuickStart") }) }),
        renderQuickCards()
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("section", { className: "recents", "aria-label": t("secProjectFiles"), children: [
        /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "recents-toolbar", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "recents-heading", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "section-label", children: t("secProjectFiles") }),
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "file-count", children: t(fileCountKey(projectFileEntries.length), { n: projectFileEntries.length }) })
          ] }),
          projSelectedPaths.length > 0 && /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "selection-bar", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "selection-count", children: t("selectedCount", { n: projSelectedPaths.length }) }),
            otherProjects.length > 0 && /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { className: "selection-move-wrap", ref: bulkMoveWrapRef, children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "button",
                {
                  className: "selection-action",
                  "aria-expanded": bulkMoveMenu,
                  onClick: () => setBulkMoveMenu((open) => !open),
                  children: t("moveToProject")
                }
              ),
              bulkMoveMenu && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "selection-move-menu", role: "menu", children: otherProjects.map((p) => /* @__PURE__ */ jsxRuntimeExports.jsx(
                "button",
                {
                  role: "menuitem",
                  onClick: () => void moveFilesTo(projSelectedPaths, p.id),
                  children: p.isDefault ? t("defaultProject") : p.name
                },
                p.id
              )) })
            ] }),
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "button",
              {
                className: "selection-action danger",
                onClick: () => deleteFiles(projSelectedPaths),
                children: t("deleteFiles")
              }
            ),
            /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "selection-action", onClick: () => setSelected(/* @__PURE__ */ new Set()), children: t("cancel") })
          ] })
        ] }),
        projectFileEntries.length === 0 ? /* @__PURE__ */ jsxRuntimeExports.jsxs("p", { className: "empty proj-empty", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsxs(
            "svg",
            {
              className: "proj-empty-icon",
              width: "48",
              height: "48",
              viewBox: "0 0 24 24",
              fill: "none",
              "aria-hidden": "true",
              children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx(
                  "path",
                  {
                    d: "M6.29297 3.75H14.1729C14.4927 3.75 14.7979 3.88392 15.0146 4.11914L18.5566 7.96387C18.7512 8.17512 18.8593 8.45208 18.8594 8.73926V19.1055C18.8593 19.7376 18.346 20.25 17.7139 20.25H6.29297C5.66091 20.2499 5.14855 19.7375 5.14844 19.1055V4.89453C5.14855 4.26247 5.66091 3.75011 6.29297 3.75Z",
                    stroke: "currentColor",
                    strokeWidth: "1.5"
                  }
                ),
                /* @__PURE__ */ jsxRuntimeExports.jsx(
                  "path",
                  {
                    d: "M13.8984 4V7.11C13.8984 8.15382 14.7446 9 15.7884 9H18.8984",
                    stroke: "currentColor",
                    strokeWidth: "1.5"
                  }
                )
              ]
            }
          ),
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "empty-hint", children: t("projEmptyHint") })
        ] }) : /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "recent-table", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "recent-columns", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "col-check", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
              "input",
              {
                type: "checkbox",
                checked: projAllSelected,
                onChange: toggleSelectAllProject,
                "aria-label": t("selectAll")
              }
            ) }),
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "col-name", children: t("colName") }),
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { children: t("colLocation") }),
            renderModifiedHeader(),
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "col-size", children: t("colSize") }),
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", {}),
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", {})
          ] }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("ul", { className: "recent-list", children: (fileSort === "oldest" ? [...projectFileEntries].reverse() : projectFileEntries).map((entry) => renderFileRow(entry, "project")) })
        ] })
      ] })
    ] });
  }
  function renderGlobalContent() {
    const now = /* @__PURE__ */ new Date();
    const hour = now.getHours();
    const greetKey = hour < 6 ? "greetEvening" : hour < 12 ? "greetMorning" : hour < 18 ? "greetAfternoon" : "greetEvening";
    const cjk = lang === "zh" || lang === "zh-TW" || lang === "ja";
    const greeting = `${t(greetKey)}${accountName ? (cjk ? "，" : ", ") + accountName : ""}${cjk ? "。" : ". "}`;
    return /* @__PURE__ */ jsxRuntimeExports.jsxs("main", { className: "content", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsxs("section", { className: "quick-start", "aria-label": t("secQuickStart"), children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "home-hero", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("h1", { className: "hero-title", children: [
          greeting,
          /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "hero-ask", children: t(greetAskKey) })
        ] }) }),
        renderQuickCards()
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs(
        "section",
        {
          className: "recents",
          "aria-label": view === "recent" ? t("secRecent") : t("secStarred"),
          children: [
            /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "recents-toolbar", children: [
              selectedPaths.length > 0 ? /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "selection-bar", children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "selection-count", children: t("selectedCount", { n: selectedPaths.length }) }),
                /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "selection-action", onClick: () => removeRecent(selectedPaths), children: t("removeFromList") }),
                /* @__PURE__ */ jsxRuntimeExports.jsx(
                  "button",
                  {
                    className: "selection-action danger",
                    onClick: () => deleteFiles(selectedPaths),
                    children: t("deleteFiles")
                  }
                ),
                /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "selection-action", onClick: () => setSelected(/* @__PURE__ */ new Set()), children: t("cancel") })
              ] }) : /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "filter-pills", role: "tablist", "aria-label": t("filterAria"), children: FILTERS.map((f) => /* @__PURE__ */ jsxRuntimeExports.jsx(
                "button",
                {
                  className: `filter-pill${filter === f.key ? " active" : ""}`,
                  onClick: () => changeFilter(f.key),
                  children: t(f.label)
                },
                f.key
              )) }),
              /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "recents-heading", children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "section-label", children: view === "recent" ? t("secRecent") : t("secStarred") }),
                /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "file-count", children: t(fileCountKey(listTotal), { n: listTotal }) })
              ] })
            ] }),
            entries.length === 0 ? /* @__PURE__ */ jsxRuntimeExports.jsxs("p", { className: "empty proj-empty", children: [
              /* @__PURE__ */ jsxRuntimeExports.jsxs(
                "svg",
                {
                  className: "proj-empty-icon",
                  width: "48",
                  height: "48",
                  viewBox: "0 0 24 24",
                  fill: "none",
                  "aria-hidden": "true",
                  children: [
                    /* @__PURE__ */ jsxRuntimeExports.jsx(
                      "path",
                      {
                        d: "M6.29297 3.75H14.1729C14.4927 3.75 14.7979 3.88392 15.0146 4.11914L18.5566 7.96387C18.7512 8.17512 18.8593 8.45208 18.8594 8.73926V19.1055C18.8593 19.7376 18.346 20.25 17.7139 20.25H6.29297C5.66091 20.2499 5.14855 19.7375 5.14844 19.1055V4.89453C5.14855 4.26247 5.66091 3.75011 6.29297 3.75Z",
                        stroke: "currentColor",
                        strokeWidth: "1.5"
                      }
                    ),
                    /* @__PURE__ */ jsxRuntimeExports.jsx(
                      "path",
                      {
                        d: "M13.8984 4V7.11C13.8984 8.15382 14.7446 9 15.7884 9H18.8984",
                        stroke: "currentColor",
                        strokeWidth: "1.5"
                      }
                    )
                  ]
                }
              ),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "empty-hint", children: view === "starred" ? t("emptyStarred") : navCounts.recent === 0 ? t("emptyRecent") : t("emptyFiltered") })
            ] }) : /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: `recent-table${selectedPaths.length > 0 ? " has-selection" : ""}`, children: [
              /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "recent-columns", children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "col-check", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
                  "input",
                  {
                    type: "checkbox",
                    checked: allSelected,
                    onChange: toggleSelectAll,
                    "aria-label": t("selectAll")
                  }
                ) }),
                /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "col-name", children: t("colName") }),
                /* @__PURE__ */ jsxRuntimeExports.jsx("span", { children: t("colLocation") }),
                renderModifiedHeader(),
                /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "col-size", children: t("colSize") }),
                /* @__PURE__ */ jsxRuntimeExports.jsx("span", {}),
                /* @__PURE__ */ jsxRuntimeExports.jsx("span", {})
              ] }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("ul", { className: "recent-list", children: (fileSort === "oldest" ? [...entries].reverse() : entries).map(
                (entry) => renderFileRow(entry, "global")
              ) }),
              hasMore && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { ref: sentinelRef, className: "load-more", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "load-more-spinner" }) })
            ] })
          ]
        }
      )
    ] });
  }
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "home", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsxs("aside", { className: `sidebar${sidebarCollapsed ? " collapsed" : ""}`, children: [
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "sidebar-logo", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("img", { className: "logo-lockup", src: logoLockup, alt: "" }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "logo-wordmark", children: t("loginBrandName") }),
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            type: "button",
            className: "sidebar-toggle",
            onClick: () => setSidebarCollapsed((v) => !v),
            title: sidebarCollapsed ? t("navExpandSidebar") : t("navCollapseSidebar"),
            "aria-label": sidebarCollapsed ? t("navExpandSidebar") : t("navCollapseSidebar"),
            "aria-expanded": !sidebarCollapsed,
            children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "15", height: "15", viewBox: "0 0 16 16", fill: "none", "aria-hidden": "true", children: sidebarCollapsed ? /* @__PURE__ */ jsxRuntimeExports.jsx(
              "path",
              {
                d: "M3.5 3.5L8 8l-4.5 4.5M8.5 3.5L13 8l-4.5 4.5",
                stroke: "currentColor",
                strokeWidth: "1.5",
                strokeLinecap: "round",
                strokeLinejoin: "round"
              }
            ) : /* @__PURE__ */ jsxRuntimeExports.jsx(
              "path",
              {
                d: "M12.5 3.5L8 8l4.5 4.5M7.5 3.5L3 8l4.5 4.5",
                stroke: "currentColor",
                strokeWidth: "1.5",
                strokeLinecap: "round",
                strokeLinejoin: "round"
              }
            ) })
          }
        )
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("nav", { className: "sidebar-nav", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsxs(
          "button",
          {
            className: `nav-item${view === "recent" && !selectedProjectId && !cloudMode ? " active" : ""}`,
            title: t("navRecent"),
            onClick: () => {
              changeView("recent");
              setSelectedProjectId(null);
              setCloudMode(false);
            },
            children: [
              /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 16 16", fill: "none", "aria-hidden": "true", children: [
                /* @__PURE__ */ jsxRuntimeExports.jsx("circle", { cx: "8", cy: "8", r: "6.2", stroke: "currentColor", strokeWidth: "1.3" }),
                /* @__PURE__ */ jsxRuntimeExports.jsx(
                  "path",
                  {
                    d: "M8 4.8V8l2.2 1.6",
                    stroke: "currentColor",
                    strokeWidth: "1.3",
                    strokeLinecap: "round"
                  }
                )
              ] }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "nav-label", children: t("navRecent") }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "nav-count", children: navCounts.recent })
            ]
          }
        ),
        /* @__PURE__ */ jsxRuntimeExports.jsxs(
          "button",
          {
            className: `nav-item${view === "starred" && !selectedProjectId && !cloudMode ? " active" : ""}`,
            title: t("navStarred"),
            onClick: () => {
              changeView("starred");
              setSelectedProjectId(null);
              setCloudMode(false);
            },
            children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "16", height: "16", viewBox: "0 0 16 16", fill: "none", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
                "path",
                {
                  d: "M8 1.9l1.9 3.85 4.25.62-3.07 3 .72 4.23L8 11.6l-3.8 2 .72-4.23-3.07-3 4.25-.62z",
                  stroke: "currentColor",
                  strokeWidth: "1.3",
                  strokeLinejoin: "round"
                }
              ) }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "nav-label", children: t("navStarred") }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "nav-count", children: navCounts.starred })
            ]
          }
        ),
        loggedIn && /* @__PURE__ */ jsxRuntimeExports.jsxs(
          "button",
          {
            className: `nav-item${cloudMode && !selectedProjectId ? " active" : ""}`,
            title: t("navCloud"),
            onClick: () => {
              setCloudMode(true);
              setSelectedProjectId(null);
              setSelected(/* @__PURE__ */ new Set());
              setRowMenu(null);
            },
            children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "16", height: "16", viewBox: "0 0 16 16", fill: "none", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
                "path",
                {
                  d: "M8 1.8l1.55 4.65L14.2 8l-4.65 1.55L8 14.2 6.45 9.55 1.8 8l4.65-1.55z",
                  stroke: "currentColor",
                  strokeWidth: "1.3",
                  strokeLinejoin: "round"
                }
              ) }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "nav-label", children: t("navCloud") }),
              /* @__PURE__ */ jsxRuntimeExports.jsx(
                "svg",
                {
                  className: "nav-external",
                  width: "13",
                  height: "13",
                  viewBox: "0 0 16 16",
                  fill: "none",
                  "aria-hidden": "true",
                  children: /* @__PURE__ */ jsxRuntimeExports.jsx(
                    "path",
                    {
                      d: "M6.5 3.5H4a1.5 1.5 0 0 0-1.5 1.5v7A1.5 1.5 0 0 0 4 13.5h7A1.5 1.5 0 0 0 12.5 12V9.5M9.5 2.5h4v4M13 3l-5.5 5.5",
                      stroke: "currentColor",
                      strokeWidth: "1.3",
                      strokeLinecap: "round",
                      strokeLinejoin: "round"
                    }
                  )
                }
              )
            ]
          }
        )
      ] }),
      projectMode && !sidebarCollapsed && /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "sidebar-divider" }),
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          ProjectPanel,
          {
            projects,
            selectedId: selectedProjectId,
            onSelect: (id) => {
              setSelectedProjectId(id);
              setSelected(/* @__PURE__ */ new Set());
              setRowMenu(null);
            },
            onRefresh: refresh
          }
        )
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsx(AccountEntry, { onStatusChange: handleAccountStatus, collapsed: sidebarCollapsed })
    ] }),
    selectedProjectId ? renderProjectContent() : cloudMode ? /* @__PURE__ */ jsxRuntimeExports.jsx(CloudProjectsView, {}) : renderGlobalContent(),
    confirmDelete && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "modal-overlay", onClick: () => setConfirmDelete(null), children: /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "div",
      {
        className: "modal",
        role: "dialog",
        "aria-modal": "true",
        "aria-label": t("deleteModalTitle"),
        onClick: (event) => event.stopPropagation(),
        children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { children: t("deleteModalTitle") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: confirmDelete.length === 1 ? t("deleteConfirmOne", { name: fileName(confirmDelete[0]) }) : t("deleteConfirmMany", { n: confirmDelete.length }) }),
          confirmDelete.length > 1 && /* @__PURE__ */ jsxRuntimeExports.jsxs("ul", { className: "modal-file-list", children: [
            confirmDelete.slice(0, 6).map((p) => /* @__PURE__ */ jsxRuntimeExports.jsx("li", { children: fileName(p) }, p)),
            confirmDelete.length > 6 && /* @__PURE__ */ jsxRuntimeExports.jsx("li", { children: t("deleteMoreCount", { n: confirmDelete.length }) })
          ] }),
          /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "modal-buttons", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "button",
              {
                className: "btn btn-secondary",
                autoFocus: true,
                onClick: () => setConfirmDelete(null),
                children: t("cancel")
              }
            ),
            /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "btn btn-danger", onClick: confirmDeleteNow, children: t("delete") })
          ] })
        ]
      }
    ) }),
    confirmMissing && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "modal-overlay", onClick: () => setConfirmMissing(null), children: /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "div",
      {
        className: "modal",
        role: "dialog",
        "aria-modal": "true",
        "aria-label": t("missingFileTitle"),
        onClick: (event) => event.stopPropagation(),
        children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { children: t("missingFileTitle") }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: t("missingFileBody", { name: confirmMissing.name }) }),
          /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "modal-buttons", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "button",
              {
                className: "btn btn-secondary",
                autoFocus: true,
                onClick: () => setConfirmMissing(null),
                children: t("cancel")
              }
            ),
            /* @__PURE__ */ jsxRuntimeExports.jsx(
              "button",
              {
                className: "btn btn-danger",
                onClick: () => {
                  removeRecent([confirmMissing.path]);
                  setConfirmMissing(null);
                },
                children: t("removeFromList")
              }
            )
          ] })
        ]
      }
    ) }),
    /* @__PURE__ */ jsxRuntimeExports.jsx(DropToOpenOverlay, {})
  ] });
}
const appIcon = "" + new URL("app-icon-BcHXNjHH.png", import.meta.url).href;
const SLIDES = [
  { titleKey: "onbTitle1", subtitleKey: "onbSubtitle1", bodyKey: "onbBody1", art: "logo" },
  { titleKey: "onbTitle2", subtitleKey: "onbBody2", showOffer: true, art: "gift" },
  {
    titleKey: "onbTitle3",
    subtitleKey: "onbBody3",
    bodyKey: "onbNote3",
    bodyDim: true,
    showStar: true,
    showAnalyticsNotice: true,
    art: "check"
  }
];
function renderEmphasis(text) {
  return text.split("**").map((part, i) => i % 2 === 1 ? /* @__PURE__ */ jsxRuntimeExports.jsx("strong", { children: part }, part) : part);
}
function SlideArt({ kind }) {
  if (kind === "logo") {
    return /* @__PURE__ */ jsxRuntimeExports.jsx("img", { className: "onb-art onb-art-logo", src: appIcon, alt: "" });
  }
  if (kind === "gift") {
    return /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "onb-art onb-art-badge onb-art-gift", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "svg",
      {
        viewBox: "0 0 48 48",
        fill: "none",
        stroke: "currentColor",
        strokeWidth: "3.2",
        strokeLinecap: "round",
        strokeLinejoin: "round",
        children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx("rect", { x: "6", y: "14", width: "36", height: "9", rx: "2" }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("path", { d: "M9.5 23v15a4 4 0 0 0 4 4h21a4 4 0 0 0 4-4V23" }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("path", { d: "M24 14v28" }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("path", { d: "M24 14c-7 0-10.5-2.6-10.5-6 0-2.5 2-4.5 4.5-4.5 4.2 0 6 5.3 6 10.5Z" }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("path", { d: "M24 14c7 0 10.5-2.6 10.5-6 0-2.5-2-4.5-4.5-4.5-4.2 0-6 5.3-6 10.5Z" })
        ]
      }
    ) });
  }
  return /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "onb-art onb-art-badge onb-art-check", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { viewBox: "0 0 60 60", fill: "none", stroke: "currentColor", strokeWidth: "4", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M29.9883 5.5C43.5194 5.5 54.4883 16.469 54.4883 30C54.4883 43.5311 43.5194 54.5 29.9883 54.5C16.4573 54.5 5.48828 43.5311 5.48828 30C5.48828 16.469 16.4573 5.5 29.9883 5.5Z",
        strokeLinecap: "round",
        strokeLinejoin: "round"
      }
    ),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M18.125 33.75L24.7764 40.4014C25.8727 41.4977 27.6924 41.342 28.5865 40.0753L41.875 21.25",
        strokeLinecap: "round"
      }
    )
  ] }) });
}
function Onboarding({ onDone }) {
  const { t } = useI18n();
  const [index, setIndex] = reactExports.useState(0);
  const [submitting, setSubmitting] = reactExports.useState(false);
  const cardRef = reactExports.useRef(null);
  const slide = SLIDES[index];
  const isLast = index === SLIDES.length - 1;
  const finish = () => {
    if (submitting) return;
    setSubmitting(true);
    void onDone().catch(() => false).finally(() => setSubmitting(false));
  };
  const next = () => {
    if (isLast) finish();
    else setIndex((current) => current + 1);
  };
  reactExports.useEffect(() => {
    cardRef.current?.focus();
  }, []);
  reactExports.useEffect(() => {
    const card = cardRef.current;
    const active = document.activeElement;
    if (card && (!(active instanceof HTMLElement) || !card.contains(active))) card.focus();
  }, [index]);
  reactExports.useEffect(() => {
    const onKeyDown = (event) => {
      if (event.key === "Escape") {
        finish();
        return;
      }
      if (event.key === "Tab") {
        const card = cardRef.current;
        if (!card) return;
        const focusables = Array.from(card.querySelectorAll("button")).filter(
          (el) => !el.closest("[inert]")
        );
        if (focusables.length === 0) return;
        const first = focusables[0];
        const last = focusables[focusables.length - 1];
        const active = document.activeElement;
        const onButton = active instanceof HTMLElement && focusables.includes(active);
        if (!onButton) {
          event.preventDefault();
          (event.shiftKey ? last : first).focus();
        } else if (event.shiftKey && active === first) {
          event.preventDefault();
          last.focus();
        } else if (!event.shiftKey && active === last) {
          event.preventDefault();
          first.focus();
        }
        return;
      }
      const buttonFocused = event.target instanceof HTMLElement && event.target.closest("button") !== null;
      if (event.key === "Enter" && !buttonFocused || event.key === "ArrowRight") next();
      if (event.key === "ArrowLeft") setIndex((i) => Math.max(0, i - 1));
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  });
  return /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "onb-overlay", role: "dialog", "aria-modal": "true", "aria-label": t(slide.titleKey), children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "onb-card", ref: cardRef, tabIndex: -1, children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "onb-stage", children: SLIDES.map((s, i) => /* @__PURE__ */ jsxRuntimeExports.jsxs(
      "div",
      {
        className: `onb-slide${i === index ? " active" : ""}`,
        inert: i !== index,
        children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx(SlideArt, { kind: s.art }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("h2", { className: "onb-title", children: t(s.titleKey) }),
          /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "onb-subtitle", children: t(s.subtitleKey) }),
          s.bodyKey && /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: `onb-body${s.bodyDim ? " onb-body-dim" : ""}`, children: t(s.bodyKey) }),
          s.showStar && /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "onb-star", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "onb-star-hint", children: t("onbStarHint") }),
            /* @__PURE__ */ jsxRuntimeExports.jsxs(
              "button",
              {
                className: "onb-star-btn",
                onClick: () => void window.aiOffice.openGitHubRepo(),
                children: [
                  /* @__PURE__ */ jsxRuntimeExports.jsx(
                    "svg",
                    {
                      width: "13",
                      height: "13",
                      viewBox: "0 0 24 24",
                      fill: "currentColor",
                      "aria-hidden": "true",
                      children: /* @__PURE__ */ jsxRuntimeExports.jsx("path", { d: "M12 2.5l2.9 5.9 6.5.9-4.7 4.6 1.1 6.5L12 17.3l-5.8 3.1 1.1-6.5L2.6 9.3l6.5-.9L12 2.5z" })
                    }
                  ),
                  t("starOnGitHub")
                ]
              }
            )
          ] }),
          s.showAnalyticsNotice && /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "onb-consent", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { className: "onb-consent-copy", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "onb-consent-title", children: t("setAnalytics") }),
            /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "onb-consent-desc", children: t("setAnalyticsDesc") })
          ] }) }),
          s.showOffer && /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "onb-offer", children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "onb-credits", children: renderEmphasis(t("onbCredits")) }),
            /* @__PURE__ */ jsxRuntimeExports.jsxs("button", { className: "onb-join", onClick: () => void window.aiOffice.openGenTeam(), children: [
              t("onbJoinGenTeam"),
              /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "11", height: "11", viewBox: "0 0 12 12", fill: "none", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
                "path",
                {
                  d: "M3.5 8.5 8.5 3.5M4.5 3.5h4v4",
                  stroke: "currentColor",
                  strokeWidth: "1.4",
                  strokeLinecap: "round",
                  strokeLinejoin: "round"
                }
              ) })
            ] })
          ] })
        ]
      },
      s.titleKey
    )) }),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "onb-footer", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "onb-dots", children: SLIDES.map((s, i) => /* @__PURE__ */ jsxRuntimeExports.jsx(
        "button",
        {
          className: `onb-dot${i === index ? " active" : ""}`,
          "aria-label": t("onbStepAria", { n: i + 1, total: SLIDES.length }),
          "aria-current": i === index,
          onClick: () => setIndex(i)
        },
        s.titleKey
      )) }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "onb-nav", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "onb-skip", disabled: submitting, onClick: finish, children: t("onbSkip") }),
        index > 0 && /* @__PURE__ */ jsxRuntimeExports.jsx(
          "button",
          {
            className: "onb-back",
            disabled: submitting,
            onClick: () => setIndex(index - 1),
            children: t("onbBack")
          }
        ),
        /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "onb-next", disabled: submitting, onClick: next, children: isLast ? t("onbStart") : t("onbNext") })
      ] })
    ] })
  ] }) });
}
const PERSONALIZED_MIN_OPENS = 5;
function StarIcon() {
  return /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "18", height: "18", viewBox: "0 0 24 24", fill: "currentColor", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx("path", { d: "M12 2.5l2.9 5.9 6.5.9-4.7 4.6 1.1 6.5L12 17.3l-5.8 3.1 1.1-6.5L2.6 9.3l6.5-.9L12 2.5z" }) });
}
function StarPromptCard({ docOpens, onClose }) {
  const { t } = useI18n();
  const react2 = (action) => {
    void window.aiOffice.starPromptAction(action).catch(() => {
    });
    onClose();
  };
  const title = docOpens >= PERSONALIZED_MIN_OPENS ? t("starPromptTitleN", { n: docOpens }) : t("starPromptTitle");
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "star-prompt", role: "dialog", "aria-label": title, children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "button",
      {
        className: "star-prompt-close",
        "aria-label": t("starPromptLater"),
        onClick: () => react2("later"),
        children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "12", height: "12", viewBox: "0 0 12 12", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
          "path",
          {
            d: "M2 2l8 8M10 2L2 10",
            stroke: "currentColor",
            strokeWidth: "1.4",
            strokeLinecap: "round"
          }
        ) })
      }
    ),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "star-prompt-head", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "star-prompt-icon", children: /* @__PURE__ */ jsxRuntimeExports.jsx(StarIcon, {}) }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { className: "star-prompt-title", children: title })
    ] }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "star-prompt-body", children: t("starPromptBody") }),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "star-prompt-actions", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsxs(
        "button",
        {
          className: "star-prompt-go",
          onClick: () => {
            void window.aiOffice.openGitHubRepo().catch(() => {
            });
            react2("starred");
          },
          children: [
            /* @__PURE__ */ jsxRuntimeExports.jsx(StarIcon, {}),
            t("starPromptGo")
          ]
        }
      ),
      /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "star-prompt-done", onClick: () => react2("starred"), children: t("starPromptDone") })
    ] })
  ] });
}
function DocIcon() {
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 240 240", fill: "none", "aria-hidden": "true", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("rect", { width: "240", height: "240", rx: "48", fill: "#3276CD" }),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M183.373 72H172.927C168.749 72 165.267 74.4 164.57 78C149.946 136.8 150.642 135 149.946 139.8C149.946 139.2 149.946 138.6 149.249 137.4C148.553 134.4 149.946 137.4 133.232 78C131.839 74.4 129.053 72 124.875 72H115.822C111.643 72 108.161 74.4 107.465 78C90.7509 137.4 90.7509 135.6 90.0544 139.8V137.4C89.358 134.4 80.3047 93.6 76.8227 78C75.4299 74.4 72.6442 72 68.4658 72H56.6268C51.0556 72 46.8771 76.8 48.2699 81C53.8412 100.8 67.073 147 71.2514 162.6C72.6442 166.2 76.1263 168 79.6083 168H97.0185C101.197 168 104.679 166.2 105.375 162.6L117.911 120L120 114L122.089 120C122.089 120 130.446 150 134.625 162.6C135.321 165.6 138.803 168 142.285 168H159.695C163.177 168 166.659 166.2 168.052 162.6C181.98 113.4 188.944 91.2 191.73 81C193.123 76.8 188.944 72 183.373 72Z",
        fill: "#fff"
      }
    )
  ] });
}
function SheetIcon() {
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 240 240", fill: "none", "aria-hidden": "true", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("rect", { width: "240", height: "240", rx: "48", fill: "#4FA16B" }),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M96.3863 61.8047C98.5543 61.8052 100.377 63.1591 101.332 65.1562L101.367 65.2148C114.487 90.1591 110.279 82.597 118.664 100.758L120.445 104.637L121.98 100.641C125.532 91.3552 127.882 85.7897 138.621 65.2031L137.031 64.373L138.632 65.2031L138.656 65.1562C139.61 63.1599 141.434 61.8064 143.601 61.8047H160.933C164.595 61.8047 167.33 65.8991 165.75 69.5977L165.375 70.3242L165.339 70.3828C160.568 79.1142 153.7 91.1365 148.019 101.062C145.182 106.02 142.633 110.459 140.8 113.695C139.885 115.311 139.148 116.643 138.632 117.586C138.378 118.052 138.169 118.436 138.023 118.723C137.951 118.864 137.877 119.002 137.824 119.121C137.799 119.178 137.772 119.259 137.742 119.344C137.727 119.386 137.704 119.452 137.683 119.531C137.669 119.588 137.625 119.77 137.625 120V120.469L137.859 120.879L165.351 169.629L165.375 169.676C167.682 173.542 164.837 178.195 160.933 178.195H143.601C141.455 178.194 139.917 177.443 139.007 176.086L138.656 175.477L138.621 175.418L131.425 161.473C126.778 152.195 128.26 153.91 121.945 139.289L120.316 135.504L118.652 139.277C115.953 145.392 114.95 148.635 109.089 160.395L101.367 175.406L101.332 175.477C100.498 177.217 98.8376 178.195 96.3863 178.195H79.6519C75.0237 178.195 72.3782 173.434 74.6246 169.676L74.6363 169.629L102.14 120.879L102.632 120L102.14 119.121L74.6832 70.4648C73.747 68.4964 73.8839 66.2974 74.8003 64.6172C75.6999 62.9712 77.3618 61.8048 79.6519 61.8047H96.3863Z",
        fill: "#fff"
      }
    )
  ] });
}
function PdfIcon() {
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 240 240", fill: "none", "aria-hidden": "true", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("rect", { width: "240", height: "240", rx: "48", fill: "#EF4444" }),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M102.719 63.0153C105.738 52.1477 126.265 50.9398 128.68 66.6374C131.699 75.6938 127.472 90.7878 125.661 100.448C130.491 113.127 137.133 122.183 147.397 128.22C158.264 127.013 179.395 125.202 186.641 132.447C192.678 138.485 191.471 155.389 175.774 155.389C166.717 155.389 153.434 151.767 141.963 145.126C129.284 147.541 114.19 152.974 100.907 157.804C70.7196 209.727 53.2104 186.181 55.0216 176.521C57.4366 164.446 73.7385 154.786 85.8136 148.749C91.8511 137.277 100.907 117.957 106.944 103.466C102.718 86.5617 100.304 72.6754 102.719 63.0153ZM85.2149 158.437C81.5921 161.456 70.1214 171.117 67.1026 179.569C67.1026 179.569 73.7436 176.55 85.2149 158.437ZM116.605 113.718C112.378 124.586 107.548 136.662 101.511 146.925C111.171 142.699 122.039 137.869 134.718 134.85C127.473 130.02 121.435 122.775 116.605 113.718ZM180.613 143.932C183.028 142.121 179.406 137.291 158.275 139.102C177.595 147.555 180.613 143.932 180.613 143.932ZM116.013 64.2419C114.805 64.2436 114.806 80.5436 117.221 88.9958C120.239 83.5616 120.843 64.2421 116.013 64.2419Z",
        fill: "#fff"
      }
    )
  ] });
}
function HomeIcon() {
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 24 24", fill: "none", "aria-hidden": "true", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M9.06163 4.82633L3.23911 9.92134C2.7398 10.3583 3.07458 11.1343 3.76238 11.1343C4.18259 11.1343 4.52324 11.4489 4.52324 11.8371V15.0806C4.52324 17.871 4.52324 19.2662 5.46176 20.1331C6.40029 21 7.91082 21 10.9319 21H13.0681C16.0892 21 17.5997 21 18.5382 20.1331C19.4768 19.2662 19.4768 17.871 19.4768 15.0806V11.8371C19.4768 11.4489 19.8174 11.1343 20.2376 11.1343C20.9254 11.1343 21.2602 10.3583 20.7609 9.92134L14.9383 4.82633C13.5469 3.60878 12.8512 3 12 3C11.1488 3 10.4531 3.60878 9.06163 4.82633Z",
        stroke: "currentColor",
        strokeWidth: "1.5",
        strokeLinecap: "round",
        strokeLinejoin: "round"
      }
    ),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M12 16.0011H12.0105",
        stroke: "currentColor",
        strokeWidth: "2.57143",
        strokeLinecap: "round",
        strokeLinejoin: "round"
      }
    )
  ] });
}
function SlideIcon() {
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 240 240", fill: "none", "aria-hidden": "true", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("rect", { width: "240", height: "240", rx: "48", fill: "#D33922" }),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M130.5 72C152.5 72 167 87.75 167 109.898C167 154.195 122.5 147.797 111 147.797V175.852C111 179.297 108.5 181.758 105 181.758H90C86.5 181.758 84 179.297 84 175.852V77.9062C84 74.4609 86.5 72 90 72H130.5ZM111 124.664H124.5C129 124.664 132.5 123.188 135 120.727C140 114.82 140 104.484 135.5 99.0703C133 96.1172 129.5 95.1328 125 95.1328H111V124.664Z",
        fill: "#fff"
      }
    )
  ] });
}
function MarkdownIcon() {
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "16", height: "16", viewBox: "0 0 240 240", fill: "none", "aria-hidden": "true", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("rect", { width: "240", height: "240", rx: "48", fill: "#8B5CF6" }),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M36.4103 164L44.1723 164C47.2768 164 49.8641 161.8 50.382 158.5C61.2484 104.6 60.7313 106.25 61.2484 101.85C61.2484 102.4 62 98.9167 63.5 101.85C64.5 105.792 61.2484 92.1333 73.6679 146.583C74.7029 149.883 76.7731 152.083 79.8776 152.083L86.6045 152.083C89.7097 152.083 92.297 149.883 92.8142 146.583C103.693 98.8889 103.329 99.9343 104.415 100.62C104.569 100.717 104.752 100.807 105 100.75L105.752 104.05C106.269 106.8 112.996 144.2 115.583 158.5C116.618 161.8 118.688 164 121.793 164L130.59 164C134.729 164 137.834 159.6 136.799 155.75C132.66 137.6 122.828 95.25 119.723 80.95C118.688 77.65 116.101 76 113.513 76L100.576 76C97.4717 76 94.8843 77.65 94.3672 80.95L85.0522 120L83.5 125.5L81.9477 120C81.9477 120 75.738 92.5 72.6328 80.95C72.1156 78.2 69.5283 76 66.941 76L54.0044 76C51.4171 76 48.8298 77.65 47.7947 80.95C37.4454 126.05 32.2708 146.4 30.2006 155.75C29.1655 159.6 32.2708 164 36.4103 164Z",
        fill: "#fff"
      }
    ),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M168 82C168 78.6863 170.686 76 174 76H184C187.314 76 190 78.6863 190 82V142H168V82Z",
        fill: "#fff"
      }
    ),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "path",
      {
        d: "M175.248 162.741C177.239 165.001 180.761 165.001 182.752 162.741L208.684 133.305C211.528 130.076 209.236 125 204.932 125H153.068C148.765 125 146.472 130.076 149.316 133.305L175.248 162.741Z",
        fill: "#fff"
      }
    )
  ] });
}
const KIND_ICON = {
  home: /* @__PURE__ */ jsxRuntimeExports.jsx(HomeIcon, {}),
  docs: /* @__PURE__ */ jsxRuntimeExports.jsx(DocIcon, {}),
  sheets: /* @__PURE__ */ jsxRuntimeExports.jsx(SheetIcon, {}),
  slides: /* @__PURE__ */ jsxRuntimeExports.jsx(SlideIcon, {}),
  pdf: /* @__PURE__ */ jsxRuntimeExports.jsx(PdfIcon, {}),
  markdown: /* @__PURE__ */ jsxRuntimeExports.jsx(MarkdownIcon, {})
};
function TabBar() {
  const { t } = useI18n();
  const [tabs, setTabs] = reactExports.useState([]);
  const stripRef = reactExports.useRef(null);
  const dragRef = reactExports.useRef(null);
  const [dragVisual, setDragVisual] = reactExports.useState(null);
  const finishDrag = (pointerId, commit) => {
    const drag = dragRef.current;
    if (!drag || pointerId !== drag.pointerId) return;
    dragRef.current = null;
    if (!drag.started) {
      stripRef.current?.querySelector(".tab-item.active")?.scrollIntoView({ inline: "nearest", block: "nearest" });
      return;
    }
    setDragVisual(null);
    if (commit && drag.target !== drag.from) {
      setTabs((prev) => {
        const fromIdx = prev.findIndex((tb) => tb.id === drag.id);
        if (fromIdx < 0) return prev;
        const next = [...prev];
        const [moved] = next.splice(fromIdx, 1);
        next.splice(Math.min(Math.max(drag.target, 1), next.length), 0, moved);
        return next;
      });
      void window.aiOfficeTabs.reorder(drag.id, drag.target);
    }
  };
  reactExports.useEffect(() => {
    void window.aiOfficeTabs.list().then(setTabs);
    return window.aiOfficeTabs.onChanged(setTabs);
  }, []);
  reactExports.useEffect(() => {
    const notify = () => window.aiOfficeTabs.notifyChromePressed?.();
    document.addEventListener("pointerdown", notify, true);
    return () => document.removeEventListener("pointerdown", notify, true);
  }, []);
  reactExports.useEffect(() => {
    const drag = dragRef.current;
    if (drag && !tabs.some((t2) => t2.id === drag.id)) {
      dragRef.current = null;
      setDragVisual(null);
    }
  }, [tabs]);
  reactExports.useEffect(() => {
    const strip = stripRef.current;
    if (!strip) return;
    const onWheel = (event) => {
      if (strip.scrollWidth <= strip.clientWidth) return;
      if (Math.abs(event.deltaY) <= Math.abs(event.deltaX)) return;
      event.preventDefault();
      strip.scrollLeft += event.deltaY;
    };
    strip.addEventListener("wheel", onWheel, { passive: false });
    return () => strip.removeEventListener("wheel", onWheel);
  }, []);
  const activeId = tabs.find((tab) => tab.active)?.id;
  reactExports.useEffect(() => {
    if (dragRef.current) return;
    stripRef.current?.querySelector(".tab-item.active")?.scrollIntoView({ inline: "nearest", block: "nearest" });
  }, [activeId]);
  tabs.find((tab) => tab.active);
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "tab-bar", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "tab-bar-drag-spacer" }),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: dragVisual ? "tab-strip dragging" : "tab-strip", ref: stripRef, children: [
      tabs.map((tab, index) => {
        let dragStyle;
        if (dragVisual) {
          if (dragVisual.id === tab.id) {
            dragStyle = { transform: `translateX(${dragVisual.dx}px)` };
          } else if (dragVisual.target <= index && index < dragVisual.from) {
            dragStyle = { transform: `translateX(${dragVisual.width}px)` };
          } else if (dragVisual.from < index && index <= dragVisual.target) {
            dragStyle = { transform: `translateX(-${dragVisual.width}px)` };
          }
        }
        return /* @__PURE__ */ jsxRuntimeExports.jsxs(
          "div",
          {
            className: `tab-item ${tab.kind === "home" ? "tab-home" : ""} ${tab.active ? "active" : ""} ${dragVisual?.id === tab.id ? "drag-source" : ""}`,
            title: tab.kind === "home" ? t("loginBrandName") : tab.title,
            style: dragStyle,
            onPointerDown: (event) => {
              if (event.button !== 0) return;
              if (event.target.closest(".tab-close")) return;
              if (!tab.active) void window.aiOfficeTabs.activate(tab.id);
              if (tab.id === "home") return;
              const strip = stripRef.current;
              if (!strip) return;
              const rects = Array.from(
                strip.querySelectorAll(".tab-item"),
                (el) => el.getBoundingClientRect()
              );
              dragRef.current = {
                pointerId: event.pointerId,
                id: tab.id,
                from: index,
                startX: event.clientX,
                lefts: rects.map((r) => r.left),
                widths: rects.map((r) => r.width),
                target: index,
                started: false
              };
              event.currentTarget.setPointerCapture(event.pointerId);
            },
            onPointerMove: (event) => {
              const drag = dragRef.current;
              if (!drag || event.pointerId !== drag.pointerId) return;
              let dx = event.clientX - drag.startX;
              if (!drag.started) {
                if (Math.abs(dx) < 4) return;
                const strip = stripRef.current;
                if (strip) {
                  const rects = Array.from(
                    strip.querySelectorAll(".tab-item"),
                    (el) => el.getBoundingClientRect()
                  );
                  drag.lefts = rects.map((r) => r.left);
                  drag.widths = rects.map((r) => r.width);
                }
                drag.started = true;
              }
              const last = drag.lefts.length - 1;
              const minDx = drag.lefts[1] - drag.lefts[drag.from];
              const maxDx = drag.lefts[last] + drag.widths[last] - drag.widths[drag.from] - drag.lefts[drag.from];
              dx = Math.min(Math.max(dx, minDx), Math.max(minDx, maxDx));
              const draggedLeft = drag.lefts[drag.from] + dx;
              const draggedRight = draggedLeft + drag.widths[drag.from];
              let target = drag.from;
              for (let i = 1; i < drag.from; i++) {
                if (draggedLeft < drag.lefts[i] + drag.widths[i] / 2) {
                  target = i;
                  break;
                }
              }
              for (let i = last; i > drag.from; i--) {
                if (draggedRight > drag.lefts[i] + drag.widths[i] / 2) {
                  target = i;
                  break;
                }
              }
              drag.target = target;
              setDragVisual({
                id: drag.id,
                dx,
                from: drag.from,
                target,
                width: drag.widths[drag.from]
              });
            },
            onPointerUp: (event) => finishDrag(event.pointerId, true),
            onPointerCancel: (event) => finishDrag(event.pointerId, false),
            onLostPointerCapture: (event) => finishDrag(event.pointerId, false),
            children: [
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "tab-plate", "aria-hidden": "true" }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "tab-icon", children: KIND_ICON[tab.kind] }),
              /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "tab-title", children: tab.kind === "home" ? t("loginBrandName") : tab.title }),
              tab.closable && /* @__PURE__ */ jsxRuntimeExports.jsx(
                "button",
                {
                  className: "tab-close",
                  title: t("closeTab"),
                  "aria-label": t("closeTab"),
                  onClick: (event) => {
                    event.stopPropagation();
                    void window.aiOfficeTabs.close(tab.id);
                  },
                  children: "×"
                }
              )
            ]
          },
          tab.id
        );
      }),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "button",
        {
          className: "tab-new-btn",
          title: t("newTab"),
          "aria-label": t("newTab"),
          onClick: (event) => {
            const rect = event.currentTarget.getBoundingClientRect();
            void window.aiOfficeTabs.showNewMenu(Math.round(rect.left), Math.round(rect.bottom));
          },
          children: /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { width: "14", height: "14", viewBox: "0 0 24 24", "aria-hidden": "true", children: /* @__PURE__ */ jsxRuntimeExports.jsx(
            "path",
            {
              d: "M12 4.286v15.429M4.286 12h15.429",
              fill: "none",
              stroke: "currentColor",
              strokeWidth: "1.5",
              strokeLinecap: "round",
              strokeLinejoin: "round"
            }
          ) })
        }
      )
    ] }),
    /* @__PURE__ */ jsxRuntimeExports.jsx(
      "button",
      {
        className: "tab-overflow-btn",
        title: t("tabList"),
        "aria-label": t("tabList"),
        onClick: (event) => {
          const rect = event.currentTarget.getBoundingClientRect();
          void window.aiOfficeTabs.showMenu(Math.round(rect.left), Math.round(rect.bottom));
        },
        children: /* @__PURE__ */ jsxRuntimeExports.jsxs("svg", { width: "20", height: "20", viewBox: "0 0 24 24", fill: "none", "aria-hidden": "true", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx(
            "path",
            {
              d: "M21 4H3C2.44772 4 2 4.44772 2 5V19C2 19.5523 2.44772 20 3 20H21C21.5523 20 22 19.5523 22 19V5C22 4.44772 21.5523 4 21 4Z",
              stroke: "currentColor",
              strokeWidth: "1.5",
              strokeLinejoin: "round"
            }
          ),
          /* @__PURE__ */ jsxRuntimeExports.jsx(
            "path",
            {
              d: "M11.5 9.5H22M11.5 9.5L9.5 4M17.5 9.5L15.5 4M2 19V8.5M22 19V8.5M4.5 20H19.5",
              stroke: "currentColor",
              strokeWidth: "1.5",
              strokeLinecap: "round",
              strokeLinejoin: "round"
            }
          )
        ] })
      }
    )
  ] });
}
function AppFrame({ initialOnboardingSeen }) {
  const [homeActive, setHomeActive] = reactExports.useState(true);
  const [showOnboarding, setShowOnboarding] = reactExports.useState(!initialOnboardingSeen);
  const [starPromptDocOpens, setStarPromptDocOpens] = reactExports.useState(null);
  reactExports.useEffect(() => {
    const applyTabs = (tabs) => {
      const active = tabs.find((tab) => tab.active);
      setHomeActive(!active || active.kind === "home");
    };
    void window.aiOfficeTabs.list().then(applyTabs);
    return window.aiOfficeTabs.onChanged(applyTabs);
  }, []);
  reactExports.useEffect(() => {
    if (showOnboarding) return;
    let alive = true;
    void window.aiOffice.starPromptShouldShow?.().then((result) => {
      if (alive && result.show) setStarPromptDocOpens(result.docOpens);
    });
    return () => {
      alive = false;
    };
  }, [showOnboarding]);
  const finishOnboarding = async () => {
    try {
      const persisted = await window.aiOffice.setOnboardingSeen();
      if (!persisted) return false;
      setShowOnboarding(false);
      return true;
    } catch {
      return false;
    }
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "app-frame", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx(TabBar, {}),
    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "app-frame-content", style: { visibility: homeActive ? "visible" : "hidden" }, children: /* @__PURE__ */ jsxRuntimeExports.jsx(Home, {}) }),
    showOnboarding && homeActive && /* @__PURE__ */ jsxRuntimeExports.jsx(Onboarding, { onDone: finishOnboarding }),
    starPromptDocOpens !== null && !showOnboarding && homeActive && /* @__PURE__ */ jsxRuntimeExports.jsx(StarPromptCard, { docOpens: starPromptDocOpens, onClose: () => setStarPromptDocOpens(null) })
  ] });
}
installScreenTips();
if (navigator.platform.toLowerCase().includes("mac")) document.body.classList.add("vib");
void (async () => {
  const plainBrowser = !window.aiOffice;
  if (plainBrowser) {
    const { installBrowserMock } = await __vitePreload(async () => {
      const { installBrowserMock: installBrowserMock2 } = await import("./browser-mock-DHEYBaUE.js");
      return { installBrowserMock: installBrowserMock2 };
    }, true ? [] : void 0, import.meta.url);
    installBrowserMock();
  }
  const [lang, onboardingSeen, theme] = await Promise.all([
    window.aiOffice.getLanguage(),
    // if the flag is unreadable, skip onboarding rather than block the home screen
    window.aiOffice.onboardingSeen().catch(() => true),
    window.aiOffice.getTheme().catch(() => "system")
  ]);
  document.documentElement.lang = htmlLang(lang);
  if (plainBrowser) document.title = brandName(lang);
  if (theme !== "system") {
    document.documentElement.setAttribute("data-theme", theme);
  }
  window.aiOffice.onThemeChanged((next) => {
    if (next === "system") document.documentElement.removeAttribute("data-theme");
    else document.documentElement.setAttribute("data-theme", next);
  });
  clientExports.createRoot(document.getElementById("root")).render(
    /* @__PURE__ */ jsxRuntimeExports.jsx(React.StrictMode, { children: /* @__PURE__ */ jsxRuntimeExports.jsx(LocaleProvider, { initial: lang, children: /* @__PURE__ */ jsxRuntimeExports.jsx(AppFrame, { initialOnboardingSeen: onboardingSeen }) }) })
  );
})();
export {
  defaultAiSettings as d,
  normalizeLang as n,
  strings as s
};
