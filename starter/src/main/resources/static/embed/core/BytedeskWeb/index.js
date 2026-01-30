var H = Object.defineProperty;
var P = (L, e, i) => e in L ? H(L, e, { enumerable: !0, configurable: !0, writable: !0, value: i }) : L[e] = i;
var p = (L, e, i) => P(L, typeof e != "symbol" ? e + "" : e, i);
import { BYTEDESK_UID as M, BYTEDESK_VISITOR_UID as U, BYTEDESK_BROWSE_LAST_TIMESTAMP as V, BYTEDESK_BROWSE_FAILED_TIMESTAMP as z, POST_MESSAGE_LOCALSTORAGE_RESPONSE as A, POST_MESSAGE_INVITE_VISITOR_REJECT as Y, POST_MESSAGE_INVITE_VISITOR_ACCEPT as N, POST_MESSAGE_INVITE_VISITOR as j, POST_MESSAGE_RECEIVE_MESSAGE as X, POST_MESSAGE_MINIMIZE_WINDOW as q, POST_MESSAGE_MAXIMIZE_WINDOW as G, POST_MESSAGE_CLOSE_CHAT_WINDOW as K } from "../../utils/constants/index.js";
import t, { setGlobalConfig as J } from "../../utils/logger/index.js";
class te {
  constructor(e) {
    p(this, "config");
    p(this, "bubble", null);
    p(this, "window", null);
    p(this, "inviteDialog", null);
    p(this, "contextMenu", null);
    p(this, "hideTimeout", null);
    p(this, "isVisible", !1);
    p(this, "isDragging", !1);
    p(this, "windowState", "normal");
    p(this, "loopCount", 0);
    p(this, "loopTimer", null);
    p(this, "isDestroyed", !1);
    // 添加请求状态管理
    p(this, "initVisitorPromise", null);
    p(this, "getUnreadMessageCountPromise", null);
    p(this, "clearUnreadMessagesPromise", null);
    // 文档反馈功能相关属性
    p(this, "feedbackTooltip", null);
    p(this, "feedbackDialog", null);
    p(this, "selectedText", "");
    // 添加防抖和状态管理
    p(this, "selectionDebounceTimer", null);
    p(this, "isTooltipVisible", !1);
    p(this, "lastSelectionText", "");
    p(this, "lastMouseEvent", null);
    p(this, "lastSelectionRect", null);
    this.config = {
      ...this.getDefaultConfig(),
      ...e
    }, J(this.config), this.setupApiUrl();
  }
  async setupApiUrl() {
    try {
      const { setApiUrl: e } = await import("../../apis/request/index.js"), i = this.config.apiUrl || "https://api.weiyuai.cn";
      e(i), t.info("API URL 已设置为:", i);
    } catch (e) {
      t.error("设置API URL时出错:", e);
    }
  }
  getDefaultConfig() {
    return {
      isDebug: !1,
      // isPreload: false,
      forceRefresh: !1,
      htmlUrl: "https://cdn.weiyuai.cn/chat",
      apiUrl: "https://api.weiyuai.cn",
      placement: "bottom-right",
      marginBottom: 20,
      marginSide: 20,
      autoPopup: !1,
      inviteConfig: {
        show: !1,
        text: "邀请您加入对话",
        acceptText: "开始对话",
        rejectText: "稍后再说"
      },
      tabsConfig: {
        home: !1,
        messages: !0,
        help: !1,
        news: !1
      },
      bubbleConfig: {
        show: !0,
        icon: "👋",
        title: "需要帮助吗？",
        subtitle: "点击开始对话"
      },
      buttonConfig: {
        show: !0,
        width: 60,
        height: 60,
        onClick: () => {
          this.showChat();
        }
      },
      feedbackConfig: {
        enabled: !1,
        trigger: "selection",
        showOnSelection: !0,
        selectionText: "文档反馈",
        buttonText: "文档反馈",
        dialogTitle: "提交意见反馈",
        placeholder: "请描述您的问题或优化建议",
        submitText: "提交反馈",
        cancelText: "取消",
        successMessage: "反馈已提交，感谢您的意见！",
        categoryNames: [
          "错别字、拼写错误",
          "链接跳转有问题",
          "文档和实操过程不一致",
          "文档难以理解",
          "建议或其他"
        ],
        requiredTypes: !1,
        typesSectionTitle: "问题类型",
        typesDescription: "（多选）",
        submitScreenshot: !0
      },
      chatConfig: {
        org: "df_org_uid",
        t: "2",
        sid: "df_rt_uid"
      },
      animation: {
        enabled: !0,
        duration: 300,
        type: "ease"
      },
      theme: {
        mode: "system",
        textColor: "#ffffff",
        backgroundColor: "#0066FF"
      },
      window: {
        width: 380,
        height: 640
      },
      draggable: !1,
      locale: "zh-cn"
    };
  }
  async init() {
    var i, o, s;
    if (this.isDestroyed) {
      t.warn("BytedeskWeb 已销毁，跳过初始化");
      return;
    }
    const e = ((i = this.config.buttonConfig) == null ? void 0 : i.show) !== !1;
    if (await this._initVisitor(), !this.isDestroyed) {
      if (e) {
        if (await this._browseVisitor(), this.isDestroyed) return;
      } else
        t.debug("buttonConfig.show=false，跳过自动发送浏览记录");
      if (this.createBubble(), !this.isDestroyed && (this.createInviteDialog(), !this.isDestroyed && (this.setupMessageListener(), this.setupResizeListener(), !this.isDestroyed))) {
        if ((o = this.config.feedbackConfig) != null && o.enabled && (this.config.isDebug && t.debug("BytedeskWeb: 开始初始化文档反馈功能，document.readyState:", document.readyState), this.initFeedbackFeature(), document.readyState !== "complete")) {
          this.config.isDebug && t.debug("BytedeskWeb: DOM未完全加载，设置备用初始化");
          const n = () => {
            this.config.isDebug && t.debug("BytedeskWeb: window load事件触发，重新初始化反馈功能"), this.initFeedbackFeature(), window.removeEventListener("load", n);
          };
          window.addEventListener("load", n);
          const a = () => {
            this.config.isDebug && t.debug("BytedeskWeb: DOMContentLoaded事件触发，重新初始化反馈功能"), setTimeout(() => this.initFeedbackFeature(), 100), document.removeEventListener("DOMContentLoaded", a);
          };
          document.readyState === "loading" && document.addEventListener("DOMContentLoaded", a);
        }
        if (e) {
          if (this._getUnreadMessageCount(), this.isDestroyed) return;
        } else
          t.debug("buttonConfig.show=false，跳过自动获取未读消息数");
        if (this.config.autoPopup) {
          if (this.isDestroyed) return;
          setTimeout(() => {
            this.showChat();
          }, this.config.autoPopupDelay || 1e3);
        }
        if (!this.isDestroyed && (s = this.config.inviteConfig) != null && s.show) {
          if (this.isDestroyed) return;
          setTimeout(() => {
            this.showInviteDialog();
          }, this.config.inviteConfig.delay || 3e3);
        }
      }
    }
  }
  async _initVisitor() {
    var n, a, d, r;
    if (this.initVisitorPromise)
      return t.debug("访客初始化请求正在进行中，返回现有Promise"), this.initVisitorPromise;
    const e = localStorage.getItem(M), i = localStorage.getItem(U);
    t.debug("localUid: ", e), t.debug("localVisitorUid: ", i);
    const s = ((n = this.config.chatConfig) == null ? void 0 : n.visitorUid) && i ? ((a = this.config.chatConfig) == null ? void 0 : a.visitorUid) === i : !0;
    return e && i && s ? (t.debug("访客信息相同，直接返回本地访客信息"), (r = (d = this.config).onVisitorInfo) == null || r.call(d, e || "", i || ""), {
      uid: e,
      visitorUid: i
    }) : (t.debug("开始创建访客初始化Promise"), this.initVisitorPromise = import("../../apis/visitor/index.js").then(
      async ({ initVisitor: h }) => {
        var c, l, g, b, u, x, m, w, T, B, E, W, I, D, f, k, C, S, y, F, O, $, _;
        try {
          const R = {
            uid: String(((c = this.config.chatConfig) == null ? void 0 : c.uid) || e || ""),
            visitorUid: String(
              ((l = this.config.chatConfig) == null ? void 0 : l.visitorUid) || i || ""
            ),
            orgUid: String(((g = this.config.chatConfig) == null ? void 0 : g.org) || ""),
            nickname: String(((b = this.config.chatConfig) == null ? void 0 : b.name) || ""),
            avatar: String(((u = this.config.chatConfig) == null ? void 0 : u.avatar) || ""),
            mobile: String(((x = this.config.chatConfig) == null ? void 0 : x.mobile) || ""),
            email: String(((m = this.config.chatConfig) == null ? void 0 : m.email) || ""),
            note: String(((w = this.config.chatConfig) == null ? void 0 : w.note) || ""),
            extra: typeof ((T = this.config.chatConfig) == null ? void 0 : T.extra) == "string" ? this.config.chatConfig.extra : JSON.stringify(((B = this.config.chatConfig) == null ? void 0 : B.extra) || {}),
            vipLevel: String(((E = this.config.chatConfig) == null ? void 0 : E.vipLevel) || ""),
            debug: ((W = this.config.chatConfig) == null ? void 0 : W.debug) || !1,
            settingsUid: ((I = this.config.chatConfig) == null ? void 0 : I.settingsUid) || "",
            loadHistory: ((D = this.config.chatConfig) == null ? void 0 : D.loadHistory) || !1
          }, v = await h(R);
          return t.debug("访客初始化API响应:", v.data, R), ((f = v.data) == null ? void 0 : f.code) === 200 ? ((C = (k = v.data) == null ? void 0 : k.data) != null && C.uid && (localStorage.setItem(M, v.data.data.uid), t.debug("已保存uid到localStorage:", v.data.data.uid)), (y = (S = v.data) == null ? void 0 : S.data) != null && y.visitorUid && (localStorage.setItem(
            U,
            v.data.data.visitorUid
          ), t.debug(
            "已保存visitorUid到localStorage:",
            v.data.data.visitorUid
          )), (F = v.data) != null && F.data && (t.debug("触发onVisitorInfo回调"), ($ = (O = this.config).onVisitorInfo) == null || $.call(
            O,
            v.data.data.uid || "",
            v.data.data.visitorUid || ""
          )), v.data.data) : (t.error("访客初始化失败:", (_ = v.data) == null ? void 0 : _.message), null);
        } catch (R) {
          return t.error("访客初始化出错:", R), null;
        } finally {
          t.debug("访客初始化Promise完成，清除引用"), this.initVisitorPromise = null;
        }
      }
    ), this.initVisitorPromise);
  }
  // 获取当前页面浏览信息并发送到服务器
  async _browseVisitor() {
    var e, i, o, s;
    try {
      const n = localStorage.getItem(
        V
      );
      if (n) {
        const D = parseInt(n), f = Date.now(), k = 60 * 60 * 1e3;
        if (!Number.isNaN(D) && f - D < k) {
          const C = Math.ceil(
            (k - (f - D)) / 1e3 / 60
          );
          t.warn(`浏览记录1小时内最多发送一次，还需等待 ${C} 分钟`);
          return;
        }
      }
      const a = localStorage.getItem(z);
      if (a) {
        const D = parseInt(a), f = Date.now(), k = 60 * 60 * 1e3;
        if (f - D < k) {
          const C = Math.ceil((k - (f - D)) / 1e3 / 60);
          t.warn(`浏览记录发送失败后1小时内禁止发送，还需等待 ${C} 分钟`);
          return;
        } else
          localStorage.removeItem(z);
      }
      const d = window.location.href, r = document.title, h = document.referrer, c = navigator.userAgent, l = this.getBrowserInfo(c), g = this.getOSInfo(c), b = this.getDeviceInfo(c), u = `${screen.width}x${screen.height}`, x = new URLSearchParams(window.location.search), m = x.get("utm_source") || void 0, w = x.get("utm_medium") || void 0, T = x.get("utm_campaign") || void 0, B = localStorage.getItem(M), E = {
        url: d,
        title: r,
        referrer: h,
        userAgent: c,
        operatingSystem: g,
        browser: l,
        deviceType: b,
        screenResolution: u,
        utmSource: m,
        utmMedium: w,
        utmCampaign: T,
        status: "ONLINE",
        // 注意这里就是uid，不是visitorUid，使用访客系统生成uid
        visitorUid: String(
          ((e = this.config.chatConfig) == null ? void 0 : e.uid) || B || ""
        ),
        orgUid: ((i = this.config.chatConfig) == null ? void 0 : i.org) || ""
      };
      if (!E.visitorUid) {
        t.warn("访客uid为空，跳过browse操作");
        return;
      }
      localStorage.setItem(V, Date.now().toString());
      const { browse: W } = await import("../../apis/visitor/index.js"), I = await W(E);
      ((o = I.data) == null ? void 0 : o.code) === 200 ? localStorage.removeItem(z) : (t.error("浏览记录发送失败:", (s = I.data) == null ? void 0 : s.message), localStorage.setItem(z, Date.now().toString()), t.warn("已记录浏览记录发送失败时间，1小时内将禁止再次发送"));
    } catch (n) {
      t.error("发送浏览记录时出错:", n), localStorage.setItem(z, Date.now().toString()), t.warn("已记录浏览记录发送失败时间，1小时内将禁止再次发送");
    }
  }
  // 获取浏览器信息
  getBrowserInfo(e) {
    return e.includes("Chrome") ? "Chrome" : e.includes("Firefox") ? "Firefox" : e.includes("Safari") ? "Safari" : e.includes("Edge") ? "Edge" : e.includes("Opera") ? "Opera" : "Unknown";
  }
  // 获取操作系统信息
  getOSInfo(e) {
    return e.includes("Windows") ? "Windows" : e.includes("Mac") ? "macOS" : e.includes("Linux") ? "Linux" : e.includes("Android") ? "Android" : e.includes("iOS") ? "iOS" : "Unknown";
  }
  // 获取设备信息
  getDeviceInfo(e) {
    return e.includes("Mobile") ? "Mobile" : e.includes("Tablet") ? "Tablet" : "Desktop";
  }
  async _getUnreadMessageCount() {
    return this.getUnreadMessageCountPromise ? (t.debug("获取未读消息数请求正在进行中，返回现有Promise"), this.getUnreadMessageCountPromise) : (this.getUnreadMessageCountPromise = import("../../apis/message/index.js").then(
      async ({ getUnreadMessageCount: e }) => {
        var i, o, s, n, a;
        try {
          const d = String(((i = this.config.chatConfig) == null ? void 0 : i.visitorUid) || ""), r = localStorage.getItem(M), h = localStorage.getItem(U), c = {
            uid: r || "",
            visitorUid: d || h || "",
            orgUid: ((o = this.config.chatConfig) == null ? void 0 : o.org) || ""
          };
          if (c.uid === "")
            return 0;
          const l = await e(c);
          return ((s = l.data) == null ? void 0 : s.code) === 200 ? ((n = l == null ? void 0 : l.data) != null && n.data && ((a = l == null ? void 0 : l.data) == null ? void 0 : a.data) > 0 ? this.showUnreadBadge(l.data.data) : this.clearUnreadBadge(), l.data.data || 0) : 0;
        } catch (d) {
          return t.error("获取未读消息数出错:", d), 0;
        } finally {
          this.getUnreadMessageCountPromise = null;
        }
      }
    ), this.getUnreadMessageCountPromise);
  }
  // 新增公共方法，供外部调用获取未读消息数
  async getUnreadMessageCount() {
    return this._getUnreadMessageCount();
  }
  // 新增公共方法，供外部调用初始化访客信息
  async initVisitor() {
    return this._initVisitor();
  }
  // 新增公共方法，供外部调用发送浏览记录
  async browseVisitor() {
    return this._browseVisitor();
  }
  // 清除浏览记录发送失败的限制
  clearBrowseFailedLimit() {
    localStorage.removeItem(z), localStorage.removeItem(V), t.info("已清除浏览记录发送失败的限制");
  }
  // 清除本地访客信息，强制重新初始化
  clearVisitorInfo() {
    localStorage.removeItem(M), localStorage.removeItem(U), t.info("已清除本地访客信息");
  }
  // 强制重新初始化访客信息（忽略本地缓存）
  async forceInitVisitor() {
    return this.clearVisitorInfo(), this.initVisitorPromise = null, this._initVisitor();
  }
  // 显示未读消息数角标
  showUnreadBadge(e) {
    if (t.debug("showUnreadBadge() 被调用，count:", e), (this.config.buttonConfig || {}).show === !1) {
      t.debug("showUnreadBadge: buttonConfig.show 为 false，不显示角标");
      return;
    }
    if (!this.bubble) {
      t.debug("showUnreadBadge: bubble 不存在");
      return;
    }
    let o = this.bubble.querySelector(
      ".bytedesk-unread-badge"
    );
    o ? t.debug("showUnreadBadge: 更新现有角标") : (t.debug("showUnreadBadge: 创建新的角标"), o = document.createElement("div"), o.className = "bytedesk-unread-badge", o.style.cssText = `
        position: absolute;
        top: -8px;
        right: -8px;
        min-width: 18px;
        height: 18px;
        padding: 0 4px;
        background: #ff4d4f;
        color: white;
        font-size: 12px;
        font-weight: bold;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
        border: 2px solid white;
      `, this.bubble.appendChild(o)), o.textContent = e > 99 ? "99+" : e.toString(), t.debug("showUnreadBadge: 角标数字已更新为", o.textContent);
  }
  // 清除未读消息数角标
  clearUnreadBadge() {
    if (!this.bubble) {
      t.debug("clearUnreadBadge: bubble 不存在");
      return;
    }
    const e = this.bubble.querySelector(".bytedesk-unread-badge");
    e ? e.remove() : t.debug("clearUnreadBadge: 未找到角标");
  }
  // 清空未读消息
  async clearUnreadMessages() {
    return this.clearUnreadMessagesPromise ? (t.debug("清空未读消息请求正在进行中，返回现有Promise"), this.clearUnreadMessagesPromise) : (this.clearUnreadMessagesPromise = import("../../apis/message/index.js").then(
      async ({ clearUnreadMessages: e }) => {
        var i, o;
        try {
          const s = String(((i = this.config.chatConfig) == null ? void 0 : i.visitorUid) || ""), n = localStorage.getItem(M), a = localStorage.getItem(U), d = {
            uid: n || "",
            visitorUid: s || a || "",
            orgUid: ((o = this.config.chatConfig) == null ? void 0 : o.org) || ""
          }, r = await e(d);
          return t.debug("清空未读消息数:", r.data, d), r.data.code === 200 ? (t.info("清空未读消息数成功:", r.data), this.clearUnreadBadge(), r.data.data || 0) : (t.error("清空未读消息数失败:", r.data.message), 0);
        } catch (s) {
          return t.error("清空未读消息数出错:", s), 0;
        } finally {
          this.clearUnreadMessagesPromise = null;
        }
      }
    ), this.clearUnreadMessagesPromise);
  }
  createBubble() {
    var l, g, b, u, x, m, w, T, B, E, W, I, D;
    if (this.bubble && document.body.contains(this.bubble)) {
      t.debug("createBubble: 气泡已存在，不重复创建");
      return;
    }
    this.bubble && !document.body.contains(this.bubble) && (t.debug("createBubble: 清理已存在的 bubble 引用"), this.bubble = null);
    const e = document.createElement("div");
    e.style.cssText = `
      position: fixed;
      ${this.config.placement === "bottom-left" ? "left" : "right"}: ${this.config.marginSide}px;
      bottom: ${this.config.marginBottom}px;
      display: flex;
      flex-direction: column;
      align-items: ${this.config.placement === "bottom-left" ? "flex-start" : "flex-end"};
      gap: 10px;
      z-index: 9999;
    `;
    let i = null;
    if ((l = this.config.bubbleConfig) != null && l.show) {
      i = document.createElement("div"), i.style.cssText = `
        background: ${((g = this.config.theme) == null ? void 0 : g.mode) === "dark" ? "#1f2937" : "white"};
        color: ${((b = this.config.theme) == null ? void 0 : b.mode) === "dark" ? "#e5e7eb" : "#1f2937"};
        padding: 12px 16px;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        max-width: 220px;
        margin-bottom: 8px;
        opacity: 0;
        transform: translateY(10px);
        transition: all 0.3s ease;
        position: relative;
      `;
      const f = document.createElement("div");
      f.style.cssText = `
        display: flex;
        align-items: center;
        gap: 8px;
        flex-direction: ${this.config.placement === "bottom-left" ? "row" : "row-reverse"};
      `, f.setAttribute(
        "data-placement",
        this.config.placement || "bottom-right"
      );
      const k = document.createElement("span");
      k.textContent = ((u = this.config.bubbleConfig) == null ? void 0 : u.icon) || "", k.style.fontSize = "20px", f.appendChild(k);
      const C = document.createElement("div"), S = document.createElement("div");
      S.textContent = ((x = this.config.bubbleConfig) == null ? void 0 : x.title) || "", S.style.fontWeight = "bold", S.style.color = ((m = this.config.theme) == null ? void 0 : m.mode) === "dark" ? "#e5e7eb" : "#1f2937", S.style.marginBottom = "4px", S.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", C.appendChild(S);
      const y = document.createElement("div");
      y.textContent = ((w = this.config.bubbleConfig) == null ? void 0 : w.subtitle) || "", y.style.fontSize = "0.9em", y.style.color = ((T = this.config.theme) == null ? void 0 : T.mode) === "dark" ? "#9ca3af" : "#4b5563", y.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", C.appendChild(y), f.appendChild(C), i.appendChild(f);
      const F = document.createElement("div");
      F.style.cssText = `
        position: absolute;
        bottom: -6px;
        ${this.config.placement === "bottom-left" ? "left: 24px" : "right: 24px"};
        width: 12px;
        height: 12px;
        background: ${((B = this.config.theme) == null ? void 0 : B.mode) === "dark" ? "#1f2937" : "white"};
        transform: rotate(45deg);
        box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      `;
      const O = document.createElement("div");
      O.style.cssText = `
        position: absolute;
        bottom: 0;
        ${this.config.placement === "bottom-left" ? "left: 18px" : "right: 18px"};
        width: 24px;
        height: 12px;
        background: ${((E = this.config.theme) == null ? void 0 : E.mode) === "dark" ? "#1f2937" : "white"};
      `, i.appendChild(F), i.appendChild(O), e.appendChild(i), setTimeout(() => {
        i && (i.style.opacity = "1", i.style.transform = "translateY(0)");
      }, 500);
    }
    this.bubble = document.createElement("button");
    const o = this.config.buttonConfig || {}, s = o.width || 60, n = o.height || 60, a = Math.min(s, n) / 2, d = ((W = this.config.theme) == null ? void 0 : W.mode) === "dark", r = d ? "#3B82F6" : "#0066FF", h = ((I = this.config.theme) == null ? void 0 : I.backgroundColor) || r;
    this.bubble.style.cssText = `
      background-color: ${h};
      width: ${s}px;
      height: ${n}px;
      border-radius: ${a}px;
      border: none;
      cursor: ${this.config.draggable ? "move" : "pointer"};
      display: ${o.show === !1 ? "none" : "flex"};
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(0, 0, 0, ${d ? "0.3" : "0.12"});
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
    `;
    const c = document.createElement("div");
    if (c.style.cssText = `
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    `, o.icon) {
      const f = document.createElement("span");
      f.textContent = o.icon, f.style.fontSize = `${n * 0.4}px`, c.appendChild(f);
    } else {
      const f = document.createElement("div");
      f.innerHTML = `
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
                fill="white"/>
        </svg>
      `, c.appendChild(f);
    }
    if (o.text) {
      const f = document.createElement("span");
      f.textContent = o.text, f.style.cssText = `
        color: ${((D = this.config.theme) == null ? void 0 : D.textColor) || "#ffffff"};
        font-size: ${n * 0.25}px;
        white-space: nowrap;
      `, c.appendChild(f);
    }
    if (this.bubble.appendChild(c), this.bubble.addEventListener("mouseenter", () => {
      this.bubble.style.transform = "scale(1.1)";
    }), this.bubble.addEventListener("mouseleave", () => {
      this.bubble.style.transform = "scale(1)";
    }), e.appendChild(this.bubble), this.config.draggable) {
      let f = 0, k = 0, C = 0, S = 0;
      this.bubble.addEventListener("mousedown", (y) => {
        y.button === 0 && (this.isDragging = !0, f = y.clientX, k = y.clientY, C = e.offsetLeft, S = e.offsetTop, e.style.transition = "none");
      }), document.addEventListener("mousemove", (y) => {
        if (!this.isDragging) return;
        y.preventDefault();
        const F = y.clientX - f, O = y.clientY - k, $ = C + F, _ = S + O, R = window.innerHeight - e.offsetHeight;
        $ <= window.innerWidth / 2 ? (e.style.left = `${Math.max(0, $)}px`, e.style.right = "auto", e.style.alignItems = "flex-start", this.config.placement = "bottom-left") : (e.style.right = `${Math.max(
          0,
          window.innerWidth - $ - e.offsetWidth
        )}px`, e.style.left = "auto", e.style.alignItems = "flex-end", this.config.placement = "bottom-right"), e.style.bottom = `${Math.min(
          Math.max(0, window.innerHeight - _ - e.offsetHeight),
          R
        )}px`;
      }), document.addEventListener("mouseup", () => {
        this.isDragging && (this.isDragging = !1, e.style.transition = "all 0.3s ease", this.config.marginSide = parseInt(
          this.config.placement === "bottom-left" ? e.style.left : e.style.right
        ) || 20, this.config.marginBottom = parseInt(e.style.bottom || "20"));
      });
    }
    this.bubble.addEventListener("click", () => {
      if (!this.isDragging) {
        t.debug("bubble click");
        const f = this.bubble.messageElement;
        f instanceof HTMLElement && (f.style.display = "none"), this.showChat();
      }
    }), this.bubble.messageElement = i, document.body.appendChild(e), this.bubble.addEventListener("contextmenu", (f) => {
      this.showContextMenu(f);
    }), document.addEventListener("click", () => {
      this.hideContextMenu();
    });
  }
  createChatWindow() {
    var d, r, h, c, l, g;
    if (this.window && document.body.contains(this.window)) {
      t.debug("createChatWindow: 聊天窗口已存在，不重复创建");
      return;
    }
    this.window && !document.body.contains(this.window) && (t.debug("createChatWindow: 清理已存在的 window 引用"), this.window = null), this.window = document.createElement("div");
    const e = window.innerWidth <= 768, i = window.innerWidth, o = window.innerHeight, s = Math.min(
      ((d = this.config.window) == null ? void 0 : d.width) || i * 0.9,
      i * 0.9
    ), n = Math.min(
      ((r = this.config.window) == null ? void 0 : r.height) || o * 0.9,
      o * 0.9
    );
    e ? this.window.style.cssText = `
        position: fixed;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 100vh;
        height: 100dvh;
        display: none;
        z-index: 10000;
        border-top-left-radius: 12px;
        border-top-right-radius: 12px;
        overflow: hidden;
        box-sizing: border-box;
        padding-top: env(safe-area-inset-top);
        padding-bottom: env(safe-area-inset-bottom);
        transition: all ${(h = this.config.animation) == null ? void 0 : h.duration}ms ${(c = this.config.animation) == null ? void 0 : c.type};
      ` : this.window.style.cssText = `
        position: fixed;
        ${this.config.placement === "bottom-right" ? "right" : "left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${s}px;
        height: ${n}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(l = this.config.animation) == null ? void 0 : l.duration}ms ${(g = this.config.animation) == null ? void 0 : g.type};
      `;
    const a = document.createElement("iframe");
    a.style.cssText = `
      width: 100%;
      height: 100%;
      border: none;
      display: block;
      vertical-align: bottom;
    `, a.src = this.generateChatUrl(), t.debug("iframe.src: ", a.src), this.window.appendChild(a), document.body.appendChild(this.window);
  }
  generateChatUrl(e = "messages") {
    t.debug("this.config: ", this.config, e);
    const i = new URLSearchParams(), o = localStorage.getItem(M), s = localStorage.getItem(U);
    o && o.trim() !== "" && i.append("uid", o), s && s.trim() !== "" && i.append("visitorUid", s), Object.entries(this.config.chatConfig || {}).forEach(([a, d]) => {
      if (a === "debug" && d === !0)
        i.append("debug", "1");
      else if (a === "draft" && d === !0)
        i.append("draft", "1");
      else if (a === "loadHistory" && d === !0)
        i.append("loadHistory", "1");
      else if (a === "goodsInfo" || a === "orderInfo")
        try {
          typeof d == "string" ? i.append(a, d) : i.append(a, JSON.stringify(d));
        } catch (r) {
          t.error(`Error processing ${a}:`, r);
        }
      else if (a === "extra")
        try {
          let r = typeof d == "string" ? JSON.parse(d) : d;
          r.goodsInfo && delete r.goodsInfo, r.orderInfo && delete r.orderInfo, Object.keys(r).length > 0 && i.append(a, JSON.stringify(r));
        } catch (r) {
          t.error("Error processing extra parameter:", r);
        }
      else a !== "debug" && a !== "draft" && a !== "loadHistory" && i.append(a, String(d));
    }), Object.entries(this.config.browseConfig || {}).forEach(([a, d]) => {
      i.append(a, String(d));
    }), Object.entries(this.config.theme || {}).forEach(([a, d]) => {
      i.append(a, String(d));
    }), i.append("lang", this.config.locale || "zh-cn");
    const n = `${this.config.htmlUrl}?${i.toString()}`;
    return t.debug("chat url: ", n), n;
  }
  setupMessageListener() {
    window.addEventListener("message", (e) => {
      switch (e.data.type) {
        case K:
          this.hideChat();
          break;
        case G:
          this.toggleMaximize();
          break;
        case q:
          this.minimizeWindow();
          break;
        case X:
          t.debug("RECEIVE_MESSAGE");
          break;
        case j:
          t.debug("INVITE_VISITOR");
          break;
        case N:
          t.debug("INVITE_VISITOR_ACCEPT");
          break;
        case Y:
          t.debug("INVITE_VISITOR_REJECT");
          break;
        case A:
          this.handleLocalStorageData(e);
          break;
      }
    });
  }
  // 处理从 iframe 返回的 localStorage 数据
  handleLocalStorageData(e) {
    var a, d;
    const { uid: i, visitorUid: o } = e.data;
    t.debug("handleLocalStorageData 被调用", i, o, e.data);
    const s = localStorage.getItem(M), n = localStorage.getItem(U);
    if (s === i && n === o) {
      t.debug("handleLocalStorageData: 值相同，跳过设置");
      return;
    }
    localStorage.setItem(M, i), localStorage.setItem(U, o), t.debug("handleLocalStorageData: 已更新localStorage", {
      uid: i,
      visitorUid: o
    }), (d = (a = this.config).onVisitorInfo) == null || d.call(a, i, o);
  }
  // 向 iframe 发送消息
  sendMessageToIframe(e) {
    var o;
    const i = (o = this.window) == null ? void 0 : o.querySelector("iframe");
    i && i.contentWindow && i.contentWindow.postMessage(e, "*");
  }
  showChat(e) {
    var i, o;
    if (e && (this.config = {
      ...this.config,
      ...e
    }, this.window && (document.body.removeChild(this.window), this.window = null)), this.window || this.createChatWindow(), this.window) {
      const s = window.innerWidth <= 768;
      if (this.window.style.display = "block", this.config.forceRefresh) {
        const n = this.window.querySelector("iframe");
        n && (n.src = this.generateChatUrl());
      }
      if (this.setupResizeListener(), s && this.window && (this.window.style.transform = "translateY(100%)", requestAnimationFrame(() => {
        this.window && (this.window.style.transform = "translateY(0)");
      })), this.isVisible = !0, this.bubble) {
        this.bubble.style.display = "none";
        const n = this.bubble.messageElement;
        n instanceof HTMLElement && (n.style.display = "none");
      }
    }
    this.hideInviteDialog(), (o = (i = this.config).onShowChat) == null || o.call(i);
  }
  hideChat() {
    var e, i, o, s, n;
    if (this.window) {
      if (window.innerWidth <= 768 ? (this.window.style.transform = "translateY(100%)", setTimeout(() => {
        this.window && (this.window.style.display = "none");
      }, ((e = this.config.animation) == null ? void 0 : e.duration) || 300)) : this.window.style.display = "none", this.isVisible = !1, this.bubble) {
        this.bubble.style.display = ((i = this.config.buttonConfig) == null ? void 0 : i.show) === !1 ? "none" : "inline-flex";
        const d = this.bubble.messageElement;
        d instanceof HTMLElement && (d.style.display = ((o = this.config.bubbleConfig) == null ? void 0 : o.show) === !1 ? "none" : "block");
      }
      (n = (s = this.config).onHideChat) == null || n.call(s);
    }
  }
  minimizeWindow() {
    this.window && (this.windowState = "minimized", this.window.style.display = "none", this.hideChat());
  }
  toggleMaximize() {
    this.window && window.open(this.generateChatUrl(), "_blank");
  }
  setupResizeListener() {
    const e = () => {
      var a, d;
      if (!this.window || !this.isVisible) return;
      const o = window.innerWidth <= 768, s = window.innerWidth, n = window.innerHeight;
      if (o)
        Object.assign(this.window.style, {
          left: "0",
          bottom: "0",
          width: "100%",
          height: "100vh",
          borderTopLeftRadius: "12px",
          borderTopRightRadius: "12px",
          borderBottomLeftRadius: "0",
          borderBottomRightRadius: "0",
          boxSizing: "border-box",
          paddingTop: "env(safe-area-inset-top)",
          paddingBottom: "env(safe-area-inset-bottom)"
        }), this.window.style.height = "100dvh";
      else {
        let r = this.windowState === "maximized" ? s : Math.min(
          ((a = this.config.window) == null ? void 0 : a.width) || s * 0.9,
          s * 0.9
        ), h = this.windowState === "maximized" ? n : Math.min(
          ((d = this.config.window) == null ? void 0 : d.height) || n * 0.9,
          n * 0.9
        );
        const c = this.config.placement === "bottom-right" ? this.config.marginSide : void 0, l = this.config.placement === "bottom-left" ? this.config.marginSide : void 0;
        Object.assign(this.window.style, {
          width: `${r}px`,
          height: `${h}px`,
          right: c ? `${c}px` : "auto",
          left: l ? `${l}px` : "auto",
          bottom: `${this.config.marginBottom}px`,
          borderRadius: this.windowState === "maximized" ? "0" : "12px"
        });
      }
    };
    let i;
    window.addEventListener("resize", () => {
      clearTimeout(i), i = window.setTimeout(e, 100);
    }), e();
  }
  destroy() {
    var i;
    this.isDestroyed = !0;
    const e = (i = this.bubble) == null ? void 0 : i.parentElement;
    e && document.body.contains(e) && (document.body.removeChild(e), this.bubble = null), this.window && document.body.contains(this.window) && (document.body.removeChild(this.window), this.window = null), window.removeEventListener("resize", this.setupResizeListener.bind(this)), this.loopTimer && (window.clearTimeout(this.loopTimer), this.loopTimer = null), this.inviteDialog && document.body.contains(this.inviteDialog) && (document.body.removeChild(this.inviteDialog), this.inviteDialog = null), this.contextMenu && document.body.contains(this.contextMenu) && (document.body.removeChild(this.contextMenu), this.contextMenu = null), this.hideTimeout && (clearTimeout(this.hideTimeout), this.hideTimeout = null), this.selectionDebounceTimer && (clearTimeout(this.selectionDebounceTimer), this.selectionDebounceTimer = null), this.destroyFeedbackFeature();
  }
  createInviteDialog() {
    var d, r, h, c, l, g;
    if (this.inviteDialog && document.body.contains(this.inviteDialog)) {
      t.debug("createInviteDialog: 邀请框已存在，不重复创建");
      return;
    }
    this.inviteDialog && !document.body.contains(this.inviteDialog) && (t.debug("createInviteDialog: 清理已存在的 inviteDialog 引用"), this.inviteDialog = null);
    const e = ((d = this.config.theme) == null ? void 0 : d.mode) === "dark";
    if (this.inviteDialog = document.createElement("div"), this.inviteDialog.style.cssText = `
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background: ${e ? "#1f2937" : "white"};
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 4px 24px rgba(0, 0, 0, ${e ? "0.3" : "0.15"});
      z-index: 10001;
      display: none;
      max-width: 300px;
      text-align: center;
    `, (r = this.config.inviteConfig) != null && r.icon) {
      const b = document.createElement("div");
      b.style.cssText = `
        font-size: 32px;
        margin-bottom: 12px;
        color: ${e ? "#e5e7eb" : "#333"};
      `, b.textContent = this.config.inviteConfig.icon, this.inviteDialog.appendChild(b);
    }
    const i = document.createElement("div");
    i.style.cssText = `
      margin-bottom: 16px;
      color: ${e ? "#e5e7eb" : "#333"};
    `, i.textContent = ((h = this.config.inviteConfig) == null ? void 0 : h.text) || "需要帮助吗？点击开始对话", this.inviteDialog.appendChild(i);
    const o = document.createElement("div");
    o.style.cssText = `
      display: flex;
      gap: 10px;
      justify-content: center;
    `;
    const s = document.createElement("button");
    s.textContent = ((c = this.config.inviteConfig) == null ? void 0 : c.acceptText) || "开始对话";
    const n = ((l = this.config.theme) == null ? void 0 : l.backgroundColor) || (e ? "#3B82F6" : "#0066FF");
    s.style.cssText = `
      padding: 8px 16px;
      background: ${n};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, s.onclick = () => {
      var b, u;
      this.hideInviteDialog(), this.showChat(), (u = (b = this.config.inviteConfig) == null ? void 0 : b.onAccept) == null || u.call(b);
    };
    const a = document.createElement("button");
    a.textContent = ((g = this.config.inviteConfig) == null ? void 0 : g.rejectText) || "稍后再说", a.style.cssText = `
      padding: 8px 16px;
      background: ${e ? "#374151" : "#f5f5f5"};
      color: ${e ? "#d1d5db" : "#666"};
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, a.onclick = () => {
      var b, u;
      this.hideInviteDialog(), (u = (b = this.config.inviteConfig) == null ? void 0 : b.onReject) == null || u.call(b), this.handleInviteLoop();
    }, o.appendChild(s), o.appendChild(a), this.inviteDialog.appendChild(o), document.body.appendChild(this.inviteDialog);
  }
  showInviteDialog() {
    var e, i;
    this.inviteDialog && (this.inviteDialog.style.display = "block", (i = (e = this.config.inviteConfig) == null ? void 0 : e.onOpen) == null || i.call(e));
  }
  hideInviteDialog() {
    var e, i;
    t.debug("hideInviteDialog before"), this.inviteDialog && (this.inviteDialog.style.display = "none", (i = (e = this.config.inviteConfig) == null ? void 0 : e.onClose) == null || i.call(e), t.debug("hideInviteDialog after"));
  }
  handleInviteLoop() {
    const {
      loop: e,
      loopDelay: i = 3e3,
      loopCount: o = 1 / 0
    } = this.config.inviteConfig || {};
    !e || this.loopCount >= o - 1 || (this.loopTimer && window.clearTimeout(this.loopTimer), this.loopTimer = window.setTimeout(() => {
      this.loopCount++, this.showInviteDialog();
    }, i));
  }
  showButton() {
    if (this.bubble && this.bubble.style.display !== "none") {
      t.debug("showButton: 按钮已经显示，无需重复显示");
      return;
    }
    this.bubble ? (this.bubble.style.display = "inline-flex", t.debug("showButton: 按钮已显示")) : t.debug("showButton: bubble 不存在，需要先创建");
  }
  hideButton() {
    this.bubble && (this.bubble.style.display = "none");
  }
  showBubble() {
    if (this.bubble) {
      const e = this.bubble.messageElement;
      if (e instanceof HTMLElement) {
        if (e.style.display !== "none" && e.style.opacity !== "0") {
          t.debug("showBubble: 气泡已经显示，无需重复显示");
          return;
        }
        e.style.display = "block", setTimeout(() => {
          e.style.opacity = "1", e.style.transform = "translateY(0)";
        }, 100), t.debug("showBubble: 气泡已显示");
      } else
        t.debug("showBubble: messageElement 不存在");
    } else
      t.debug("showBubble: bubble 不存在");
  }
  hideBubble() {
    if (this.bubble) {
      const e = this.bubble.messageElement;
      e instanceof HTMLElement && (e.style.opacity = "0", e.style.transform = "translateY(10px)", setTimeout(() => {
        e.style.display = "none";
      }, 300));
    }
  }
  createContextMenu() {
    this.contextMenu = document.createElement("div"), this.contextMenu.style.cssText = `
      position: fixed;
      background: white;
      border-radius: 4px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      padding: 4px 0;
      display: none;
      z-index: 10000;
      min-width: 150px;
    `;
    const e = [
      {
        text: "隐藏按钮和气泡",
        onClick: () => {
          this.hideButton(), this.hideBubble();
        }
      },
      {
        text: "切换位置",
        onClick: () => {
          this.togglePlacement();
        }
      }
    ];
    e.forEach((i, o) => {
      const s = document.createElement("div");
      if (s.style.cssText = `
        padding: 8px 16px;
        cursor: pointer;
        color: #333;
        font-size: 14px;
        
        &:hover {
          background: #f5f5f5;
        }
      `, s.textContent = i.text, s.onclick = () => {
        i.onClick(), this.hideContextMenu();
      }, this.contextMenu && this.contextMenu.appendChild(s), o < e.length - 1) {
        const n = document.createElement("div");
        n.style.cssText = `
          height: 1px;
          background: #eee;
          margin: 4px 0;
        `, this.contextMenu && this.contextMenu.appendChild(n);
      }
    }), document.body.appendChild(this.contextMenu);
  }
  showContextMenu(e) {
    if (e.preventDefault(), this.contextMenu || this.createContextMenu(), this.contextMenu) {
      this.contextMenu.style.visibility = "hidden", this.contextMenu.style.display = "block";
      const i = this.contextMenu.offsetWidth, o = this.contextMenu.offsetHeight;
      let s = e.clientX, n = e.clientY;
      s + i > window.innerWidth && (s = s - i), n + o > window.innerHeight && (n = n - o), s = Math.max(0, s), n = Math.max(0, n), this.contextMenu.style.left = `${s}px`, this.contextMenu.style.top = `${n}px`, this.contextMenu.style.visibility = "visible";
    }
  }
  hideContextMenu() {
    this.contextMenu && (this.contextMenu.style.display = "none");
  }
  togglePlacement() {
    var i, o;
    if (!this.bubble) return;
    this.config.placement = this.config.placement === "bottom-left" ? "bottom-right" : "bottom-left";
    const e = this.bubble.parentElement;
    e && (e.style.left = this.config.placement === "bottom-left" ? `${this.config.marginSide}px` : "auto", e.style.right = this.config.placement === "bottom-right" ? `${this.config.marginSide}px` : "auto", e.style.alignItems = this.config.placement === "bottom-left" ? "flex-start" : "flex-end", this.window && this.isVisible && (this.window.style.left = this.config.placement === "bottom-left" ? `${this.config.marginSide}px` : "auto", this.window.style.right = this.config.placement === "bottom-right" ? `${this.config.marginSide}px` : "auto"), (o = (i = this.config).onConfigChange) == null || o.call(i, { placement: this.config.placement }));
  }
  // 添加新方法用于更新气泡布局
  // private updateBubbleLayout(placement: 'bottom-left' | 'bottom-right') {
  //   if (!this.bubble) return;
  //   const messageElement = (this.bubble as any).messageElement;
  //   if (messageElement instanceof HTMLElement) {
  //     // 更新消息内容容器的对齐方式
  //     messageElement.style.textAlign = placement === 'bottom-left' ? 'left' : 'right';
  //     const triangle = messageElement.querySelector('div:nth-child(2)') as HTMLElement;
  //     const mask = messageElement.querySelector('div:nth-child(3)') as HTMLElement;
  //     if (triangle && mask) {
  //       if (placement === 'bottom-left') {
  //         // 左下角位置 - 三角形靠左
  //         triangle.style.left = '24px';
  //         triangle.style.right = 'unset'; // 使用 unset 清除右侧定位
  //         mask.style.left = '18px';
  //         mask.style.right = 'unset';
  //       } else {
  //         // 右下角位置 - 三角形靠右
  //         triangle.style.right = '24px';
  //         triangle.style.left = 'unset';
  //         mask.style.right = '18px';
  //         mask.style.left = 'unset';
  //       }
  //     }
  //     // 更新内容布局
  //     const messageContent = messageElement.querySelector('div:first-child') as HTMLElement;
  //     if (messageContent) {
  //       messageContent.style.flexDirection = placement === 'bottom-left' ? 'row' : 'row-reverse';
  //       messageContent.setAttribute('data-placement', placement);
  //       // 更新文本容器内的对齐方式
  //       const textDiv = messageContent.querySelector('div') as HTMLElement;
  //       if (textDiv) {
  //         const title = textDiv.querySelector('div:first-child') as HTMLElement;
  //         const subtitle = textDiv.querySelector('div:last-child') as HTMLElement;
  //         if (title) {
  //           title.style.textAlign = placement === 'bottom-left' ? 'left' : 'right';
  //         }
  //         if (subtitle) {
  //           subtitle.style.textAlign = placement === 'bottom-left' ? 'left' : 'right';
  //         }
  //       }
  //     }
  //   }
  // }
  // ======================== 文档反馈功能 ========================
  /**
   * 初始化文档反馈功能
   */
  initFeedbackFeature() {
    var e, i;
    if (t.debug("BytedeskWeb: 初始化文档反馈功能开始"), t.debug("BytedeskWeb: feedbackConfig:", this.config.feedbackConfig), t.debug("BytedeskWeb: feedbackConfig.enabled:", (e = this.config.feedbackConfig) == null ? void 0 : e.enabled), !((i = this.config.feedbackConfig) != null && i.enabled)) {
      t.debug("BytedeskWeb: 文档反馈功能未启用，退出初始化");
      return;
    }
    (this.feedbackTooltip || this.feedbackDialog) && (t.debug("BytedeskWeb: 反馈功能已存在，先销毁再重新创建"), this.destroyFeedbackFeature()), this.config.feedbackConfig.trigger === "selection" || this.config.feedbackConfig.trigger === "both" ? (t.debug("BytedeskWeb: 触发器匹配，设置文本选择监听器"), t.debug("BytedeskWeb: 触发器类型:", this.config.feedbackConfig.trigger), this.setupTextSelectionListener()) : (t.debug("BytedeskWeb: 触发器不匹配，跳过文本选择监听器"), t.debug("BytedeskWeb: 触发器类型:", this.config.feedbackConfig.trigger)), t.debug("BytedeskWeb: 开始创建反馈提示框"), this.createFeedbackTooltip(), t.debug("BytedeskWeb: 开始创建反馈对话框"), this.createFeedbackDialog(), t.debug("BytedeskWeb: 文档反馈功能初始化完成"), t.debug("BytedeskWeb: 反馈提示框存在:", !!this.feedbackTooltip), t.debug("BytedeskWeb: 反馈对话框存在:", !!this.feedbackDialog);
  }
  /**
   * 设置文本选择监听器
   */
  setupTextSelectionListener() {
    t.debug("BytedeskWeb: 设置文本选择监听器"), document.addEventListener("mouseup", (e) => {
      this.lastMouseEvent = e, t.debug("BytedeskWeb: mouseup事件触发", e), this.handleTextSelectionWithDebounce(e);
    }, { capture: !0, passive: !0 }), document.addEventListener("selectionchange", () => {
      if (!this.lastMouseEvent) {
        t.debug("BytedeskWeb: selectionchange事件触发（无鼠标事件）");
        const e = new MouseEvent("mouseup", {
          clientX: window.innerWidth / 2,
          clientY: window.innerHeight / 2
        });
        this.handleTextSelectionWithDebounce(e);
      }
    }), document.addEventListener("keyup", (e) => {
      (e.shiftKey || e.ctrlKey || e.metaKey) && (t.debug("BytedeskWeb: keyup事件触发（带修饰键）", e), this.handleTextSelectionWithDebounce(e));
    }, { capture: !0, passive: !0 }), document.addEventListener("click", (e) => {
      const i = e.target;
      i != null && i.closest("[data-bytedesk-feedback]") || this.hideFeedbackTooltip();
    }), t.debug("BytedeskWeb: 文本选择监听器设置完成");
  }
  /**
   * 带防抖的文本选择处理
   */
  handleTextSelectionWithDebounce(e) {
    this.config.isDebug && t.debug("BytedeskWeb: handleTextSelectionWithDebounce被调用 - 防抖机制生效"), this.selectionDebounceTimer && (clearTimeout(this.selectionDebounceTimer), this.config.isDebug && t.debug("BytedeskWeb: 清除之前的防抖定时器")), this.selectionDebounceTimer = setTimeout(() => {
      this.config.isDebug && t.debug("BytedeskWeb: 防抖延迟结束，开始处理文本选择"), this.handleTextSelection(e);
    }, 200);
  }
  /**
   * 处理文本选择
   */
  handleTextSelection(e) {
    var s, n;
    this.config.isDebug && t.debug("BytedeskWeb: handleTextSelection被调用");
    const i = window.getSelection();
    if (this.config.isDebug && (t.debug("BytedeskWeb: window.getSelection()结果:", i), t.debug("BytedeskWeb: selection.rangeCount:", i == null ? void 0 : i.rangeCount)), !i || i.rangeCount === 0) {
      this.config.isDebug && t.debug("BytedeskWeb: 没有选择或范围为0，隐藏提示"), this.hideFeedbackTooltip();
      return;
    }
    const o = i.toString().trim();
    if (this.config.isDebug && (t.debug("BytedeskWeb: 检测到文本选择:", `"${o}"`), t.debug("BytedeskWeb: 选中文本长度:", o.length)), o === this.lastSelectionText && this.isTooltipVisible) {
      this.config.isDebug && t.debug("BytedeskWeb: 文本选择未变化且提示框已显示，跳过处理");
      return;
    }
    if (o.length === 0) {
      this.config.isDebug && t.debug("BytedeskWeb: 选中文本为空，隐藏提示"), this.hideFeedbackTooltip();
      return;
    }
    if (o.length < 3) {
      this.config.isDebug && t.debug("BytedeskWeb: 选中文本太短，忽略:", `"${o}"`), this.hideFeedbackTooltip();
      return;
    }
    this.selectedText = o, this.lastSelectionText = o;
    try {
      const a = i.getRangeAt(0);
      this.lastSelectionRect = a.getBoundingClientRect(), this.config.isDebug && t.debug("BytedeskWeb: 存储选中文本位置:", this.lastSelectionRect);
    } catch (a) {
      this.config.isDebug && t.warn("BytedeskWeb: 获取选中文本位置失败:", a), this.lastSelectionRect = null;
    }
    this.config.isDebug && t.debug("BytedeskWeb: 设置selectedText为:", `"${o}"`), (s = this.config.feedbackConfig) != null && s.showOnSelection ? (this.config.isDebug && t.debug("BytedeskWeb: 配置允许显示选择提示，调用showFeedbackTooltip"), this.showFeedbackTooltip(this.lastMouseEvent || void 0)) : this.config.isDebug && (t.debug("BytedeskWeb: 配置不允许显示选择提示"), t.debug("BytedeskWeb: feedbackConfig.showOnSelection:", (n = this.config.feedbackConfig) == null ? void 0 : n.showOnSelection));
  }
  /**
   * 创建反馈提示框
   */
  createFeedbackTooltip() {
    var i;
    if (this.config.isDebug && t.debug("BytedeskWeb: createFeedbackTooltip被调用"), this.feedbackTooltip && document.body.contains(this.feedbackTooltip)) {
      this.config.isDebug && t.debug("BytedeskWeb: 反馈提示框已存在且在DOM中，跳过创建");
      return;
    }
    this.feedbackTooltip && !document.body.contains(this.feedbackTooltip) && (this.config.isDebug && t.debug("BytedeskWeb: 提示框变量存在但不在DOM中，重置变量"), this.feedbackTooltip = null), this.feedbackTooltip = document.createElement("div"), this.feedbackTooltip.setAttribute("data-bytedesk-feedback", "tooltip"), this.feedbackTooltip.style.cssText = `
      position: fixed;
      background: #2e88ff;
      color: white;
      padding: 8px 16px;
      border-radius: 6px;
      font-size: 14px;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
      cursor: pointer;
      z-index: 999999;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      transform: translateY(-100%);
      margin-top: -8px;
      user-select: none;
      opacity: 0;
      transition: opacity 0.2s ease;
      display: none;
    `;
    const e = ((i = this.config.feedbackConfig) == null ? void 0 : i.selectionText) || "文档反馈";
    this.config.isDebug && t.debug("BytedeskWeb: 提示框文本:", e), this.feedbackTooltip.innerHTML = `
      <span style="margin-right: 4px;">📝</span>
      ${e}
    `, this.feedbackTooltip.addEventListener("click", async (o) => {
      this.config.isDebug && (t.debug("BytedeskWeb: 反馈提示框被点击"), t.debug("BytedeskWeb: 点击时选中文字:", this.selectedText)), o.stopPropagation(), o.preventDefault();
      try {
        await this.showFeedbackDialog(), this.config.isDebug && t.debug("BytedeskWeb: 对话框显示完成，现在隐藏提示框"), this.hideFeedbackTooltip();
      } catch (s) {
        this.config.isDebug && t.error("BytedeskWeb: 显示对话框时出错:", s);
      }
    }), document.body.appendChild(this.feedbackTooltip), this.config.isDebug && (t.debug("BytedeskWeb: 反馈提示框已创建并添加到页面"), t.debug("BytedeskWeb: 提示框元素:", this.feedbackTooltip));
  }
  /**
   * 显示反馈提示框
   */
  showFeedbackTooltip(e) {
    this.config.isDebug && (t.debug("BytedeskWeb: showFeedbackTooltip被调用"), t.debug("BytedeskWeb: feedbackTooltip存在:", !!this.feedbackTooltip), t.debug("BytedeskWeb: selectedText存在:", !!this.selectedText));
    const i = this.feedbackTooltip && document.body.contains(this.feedbackTooltip);
    if (this.config.isDebug && t.debug("BytedeskWeb: feedbackTooltip在DOM中:", i), (!this.feedbackTooltip || !i) && (this.config.isDebug && t.debug("BytedeskWeb: 提示框不存在或已从DOM中移除，重新创建"), this.createFeedbackTooltip()), !this.feedbackTooltip || !this.selectedText) {
      this.config.isDebug && t.debug("BytedeskWeb: 提示框或选中文本不存在，退出显示");
      return;
    }
    const o = window.getSelection();
    if (!o || o.rangeCount === 0) {
      this.config.isDebug && t.debug("BytedeskWeb: 无有效选择，无法计算位置");
      return;
    }
    const s = o.getRangeAt(0);
    let n;
    try {
      const m = document.createRange();
      m.setStart(s.startContainer, s.startOffset);
      let w = s.startOffset;
      const T = s.startContainer.textContent || "";
      if (s.startContainer.nodeType === Node.TEXT_NODE) {
        for (; w < Math.min(T.length, s.endOffset); ) {
          const B = document.createRange();
          B.setStart(s.startContainer, s.startOffset), B.setEnd(s.startContainer, w + 1);
          const E = B.getBoundingClientRect(), W = m.getBoundingClientRect();
          if (Math.abs(E.top - W.top) > 5)
            break;
          w++;
        }
        m.setEnd(s.startContainer, Math.max(w, s.startOffset + 1)), n = m.getBoundingClientRect();
      } else
        n = s.getBoundingClientRect();
    } catch (m) {
      this.config.isDebug && t.debug("BytedeskWeb: 获取第一行位置失败，使用整个选择区域:", m), n = s.getBoundingClientRect();
    }
    this.config.isDebug && t.debug("BytedeskWeb: 选中文本第一行位置信息:", {
      left: n.left,
      top: n.top,
      right: n.right,
      bottom: n.bottom,
      width: n.width,
      height: n.height
    });
    const a = 120, d = 40, r = 15, h = 5;
    let c = n.left + h, l = n.top - d - r;
    const g = window.innerWidth, b = window.innerHeight, u = window.scrollX, x = window.scrollY;
    c < 10 && (c = 10), c + a > g - 10 && (c = g - a - 10), l < x + 10 && (l = n.bottom + r, this.config.isDebug && t.debug("BytedeskWeb: 上方空间不足，调整为显示在选中文字第一行下方")), c += u, l += x, this.config.isDebug && t.debug("BytedeskWeb: 最终提示框位置:", {
      x: c,
      y: l,
      说明: "显示在选中文字第一行左上角上方，增加间距避免遮挡",
      verticalOffset: r,
      horizontalOffset: h,
      选中区域: n,
      视口信息: { viewportWidth: g, viewportHeight: b, scrollX: u, scrollY: x }
    }), this.feedbackTooltip.style.position = "absolute", this.feedbackTooltip.style.left = c + "px", this.feedbackTooltip.style.top = l + "px", this.feedbackTooltip.style.display = "block", this.feedbackTooltip.style.visibility = "visible", this.feedbackTooltip.style.opacity = "0", this.feedbackTooltip.style.zIndex = "999999", this.config.isDebug && t.debug("BytedeskWeb: 提示框位置已设置，样式:", {
      position: this.feedbackTooltip.style.position,
      left: this.feedbackTooltip.style.left,
      top: this.feedbackTooltip.style.top,
      display: this.feedbackTooltip.style.display,
      visibility: this.feedbackTooltip.style.visibility,
      opacity: this.feedbackTooltip.style.opacity,
      zIndex: this.feedbackTooltip.style.zIndex
    }), this.isTooltipVisible = !0, setTimeout(() => {
      this.feedbackTooltip && this.isTooltipVisible && (this.feedbackTooltip.style.opacity = "1", this.config.isDebug && t.debug("BytedeskWeb: 提示框透明度设置为1，应该可见了"));
    }, 10);
  }
  /**
   * 隐藏反馈提示框
   */
  hideFeedbackTooltip() {
    const e = this.feedbackTooltip && document.body.contains(this.feedbackTooltip);
    if (this.config.isDebug && (t.debug("BytedeskWeb: hideFeedbackTooltip被调用"), t.debug("BytedeskWeb: feedbackTooltip存在:", !!this.feedbackTooltip), t.debug("BytedeskWeb: feedbackTooltip在DOM中:", e)), !this.feedbackTooltip || !e) {
      this.isTooltipVisible = !1, this.lastSelectionText = "", this.config.isDebug && t.debug("BytedeskWeb: 提示框不存在或不在DOM中，仅重置状态");
      return;
    }
    this.isTooltipVisible = !1, this.lastSelectionText = "", this.feedbackTooltip.style.opacity = "0", setTimeout(() => {
      this.feedbackTooltip && document.body.contains(this.feedbackTooltip) && !this.isTooltipVisible ? (this.feedbackTooltip.style.display = "none", this.feedbackTooltip.style.visibility = "hidden", this.config.isDebug && t.debug("BytedeskWeb: 提示框已隐藏")) : this.config.isDebug && this.isTooltipVisible && t.debug("BytedeskWeb: 跳过隐藏操作，提示框状态已改变为可见");
    }, 100);
  }
  /**
   * 创建反馈对话框
   */
  createFeedbackDialog() {
    var i, o, s, n, a, d, r, h;
    if (this.config.isDebug && t.debug("BytedeskWeb: createFeedbackDialog被调用"), this.feedbackDialog && document.body.contains(this.feedbackDialog)) {
      this.config.isDebug && t.debug("BytedeskWeb: 反馈对话框已存在且在DOM中，跳过创建");
      return;
    }
    this.feedbackDialog && !document.body.contains(this.feedbackDialog) && (this.config.isDebug && t.debug("BytedeskWeb: 对话框变量存在但不在DOM中，重置变量"), this.feedbackDialog = null), this.feedbackDialog = document.createElement("div"), this.feedbackDialog.setAttribute("data-bytedesk-feedback", "dialog"), this.feedbackDialog.style.cssText = `
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      z-index: 1000000;
      display: none;
      justify-content: center;
      align-items: center;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
    `;
    const e = document.createElement("div");
    e.style.cssText = `
      background: white;
      border-radius: 12px;
      padding: 24px;
      width: 90%;
      max-width: 600px;
      max-height: 80vh;
      overflow-y: auto;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      position: relative;
    `, e.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h3 style="margin: 0; font-size: 18px; font-weight: 600; color: #333;">
          ${((i = this.config.feedbackConfig) == null ? void 0 : i.dialogTitle) || "提交意见反馈"}
        </h3>
        <button type="button" data-action="close" style="
          background: none;
          border: none;
          font-size: 24px;
          cursor: pointer;
          color: #999;
          line-height: 1;
          padding: 0;
          width: 24px;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
        ">×</button>
      </div>
      
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #555;">选中的文字：</label>
        <div id="bytedesk-selected-text" style="
          background: #f5f5f5;
          padding: 12px;
          border-radius: 6px;
          border-left: 4px solid #2e88ff;
          font-size: 14px;
          line-height: 1.5;
          color: #333;
          max-height: 100px;
          overflow-y: auto;
        "></div>
      </div>

      ${(o = this.config.feedbackConfig) != null && o.categoryNames && this.config.feedbackConfig.categoryNames.length > 0 ? `
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #333;">
          <span style="color: #ff4d4f;">*</span> ${((s = this.config.feedbackConfig) == null ? void 0 : s.typesSectionTitle) || "问题类型"} ${((n = this.config.feedbackConfig) == null ? void 0 : n.typesDescription) || "（多选）"}
        </label>
        <div id="bytedesk-feedback-types" style="
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 12px;
          margin-bottom: 8px;
        ">
          ${this.config.feedbackConfig.categoryNames.map((c) => `
            <label style="
              display: flex;
              align-items: flex-start;
              gap: 8px;
              cursor: pointer;
              padding: 8px;
              border-radius: 4px;
              transition: background-color 0.2s;
            " onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
              <input type="checkbox" name="feedback-type" value="${c}" style="
                margin: 2px 0 0 0;
                cursor: pointer;
              ">
              <span style="
                font-size: 14px;
                line-height: 1.4;
                color: #333;
                flex: 1;
              ">${c}</span>
            </label>
          `).join("")}
        </div>
      </div>
      ` : ""}

      ${((a = this.config.feedbackConfig) == null ? void 0 : a.submitScreenshot) !== !1 ? `
      <div style="margin-bottom: 16px;">
        <label style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px; font-weight: 500; color: #555;">
          <input type="checkbox" id="bytedesk-submit-screenshot" checked style="cursor: pointer;">
          提交截图内容
        </label>
        <div id="bytedesk-screenshot-container" style="
          border: 2px dashed #ddd;
          border-radius: 6px;
          padding: 20px;
          text-align: center;
          color: #999;
          min-height: 80px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-direction: column;
          gap: 8px;
        ">
          <div style="font-size: 24px;">📷</div>
          <div>正在生成截图预览...</div>
          <div style="font-size: 12px; color: #666;">截图将在提交时上传到服务器</div>
        </div>
      </div>
      ` : ""}

      <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #333;">
          <span style="color: #ff4d4f;">*</span> 问题描述
        </label>
        <textarea id="bytedesk-feedback-text" placeholder="${((d = this.config.feedbackConfig) == null ? void 0 : d.placeholder) || "请详细描述您的问题或优化建议"}" style="
          width: 100%;
          min-height: 120px;
          padding: 12px;
          border: 1px solid #ddd;
          border-radius: 6px;
          font-size: 14px;
          font-family: inherit;
          resize: vertical;
          box-sizing: border-box;
        "></textarea>
      </div>

      <div style="display: flex; justify-content: flex-end; gap: 12px;">
        <button type="button" data-action="cancel" style="
          background: #f5f5f5;
          color: #666;
          border: 1px solid #ddd;
          padding: 10px 20px;
          border-radius: 6px;
          cursor: pointer;
          font-size: 14px;
          font-family: inherit;
        ">${((r = this.config.feedbackConfig) == null ? void 0 : r.cancelText) || "取消"}</button>
        <button type="button" data-action="submit" style="
          background: #2e88ff;
          color: white;
          border: none;
          padding: 10px 20px;
          border-radius: 6px;
          cursor: pointer;
          font-size: 14px;
          font-family: inherit;
        ">${((h = this.config.feedbackConfig) == null ? void 0 : h.submitText) || "提交反馈"}</button>
      </div>

      <div style="margin-top: 12px; text-align: center; font-size: 12px; color: #999;">
        <a href="https://www.weiyuai.cn/" target="_blank" rel="noopener noreferrer" style="color: #aaaaaa; text-decoration: none;">
           微语技术支持
        </a>
      </div>
    `, e.addEventListener("click", (c) => {
      var b, u;
      switch (c.target.getAttribute("data-action")) {
        case "close":
        case "cancel":
          this.hideFeedbackDialog(), (u = (b = this.config.feedbackConfig) == null ? void 0 : b.onCancel) == null || u.call(b);
          break;
        case "submit":
          this.submitFeedback();
          break;
      }
    }), this.feedbackDialog.appendChild(e), this.feedbackDialog.addEventListener("click", (c) => {
      var l, g;
      c.target === this.feedbackDialog && (this.hideFeedbackDialog(), (g = (l = this.config.feedbackConfig) == null ? void 0 : l.onCancel) == null || g.call(l));
    }), document.addEventListener("keydown", (c) => {
      var l, g, b;
      c.key === "Escape" && ((l = this.feedbackDialog) == null ? void 0 : l.style.display) === "flex" && (this.hideFeedbackDialog(), (b = (g = this.config.feedbackConfig) == null ? void 0 : g.onCancel) == null || b.call(g));
    }), document.body.appendChild(this.feedbackDialog);
  }
  /**
   * 显示反馈对话框
   */
  async showFeedbackDialog() {
    this.config.isDebug && (t.debug("BytedeskWeb: showFeedbackDialog被调用"), t.debug("BytedeskWeb: feedbackDialog存在:", !!this.feedbackDialog));
    const e = this.feedbackDialog && document.body.contains(this.feedbackDialog);
    if (this.config.isDebug && t.debug("BytedeskWeb: feedbackDialog在DOM中:", e), (!this.feedbackDialog || !e) && (this.config.isDebug && t.debug("BytedeskWeb: 对话框不存在或已从DOM中移除，重新创建"), this.createFeedbackDialog()), !this.feedbackDialog) {
      this.config.isDebug && t.debug("BytedeskWeb: 对话框创建失败，退出显示");
      return;
    }
    this.config.isDebug && t.debug("BytedeskWeb: 开始填充对话框内容");
    const i = this.feedbackDialog.querySelector("#bytedesk-selected-text");
    i && (i.textContent = this.selectedText || "", this.config.isDebug && t.debug("BytedeskWeb: 已填充选中文字:", this.selectedText));
    const o = this.feedbackDialog.querySelector("#bytedesk-feedback-text");
    o && (o.value = ""), this.feedbackDialog.style.display = "flex", this.config.isDebug && (t.debug("BytedeskWeb: 对话框已设置为显示状态"), t.debug("BytedeskWeb: 对话框样式:", {
      display: this.feedbackDialog.style.display,
      visibility: this.feedbackDialog.style.visibility,
      zIndex: this.feedbackDialog.style.zIndex
    }));
    try {
      await this.generateScreenshotPreview(), this.config.isDebug && t.debug("BytedeskWeb: 截图预览生成完成");
    } catch (s) {
      this.config.isDebug && t.error("BytedeskWeb: 截图预览生成失败:", s);
    }
  }
  /**
   * 隐藏反馈对话框
   */
  hideFeedbackDialog() {
    this.feedbackDialog && (this.feedbackDialog.style.display = "none");
  }
  /**
   * 生成页面截图并上传到服务器
   * @returns 返回上传后的截图URL，如果失败则返回null
   */
  async generateAndUploadScreenshot() {
    var e;
    try {
      let i;
      const o = (e = this.feedbackDialog) == null ? void 0 : e.screenshotCanvas;
      if (o)
        this.config.isDebug && t.debug("BytedeskWeb: 使用已生成的截图canvas"), i = o;
      else {
        const s = await this.loadHtml2Canvas();
        if (!s)
          return this.config.isDebug && t.debug("BytedeskWeb: html2canvas加载失败，跳过截图"), null;
        this.config.isDebug && t.debug("BytedeskWeb: 重新生成截图");
        const n = this.calculateScreenshotArea();
        i = await s(document.body, {
          height: n.height,
          width: n.width,
          x: n.x,
          y: n.y,
          useCORS: !0,
          allowTaint: !0,
          backgroundColor: "#ffffff",
          scale: 1,
          ignoreElements: (a) => a.hasAttribute("data-bytedesk-feedback") || a.closest("[data-bytedesk-feedback]") !== null
        });
      }
      return new Promise((s) => {
        i.toBlob(async (n) => {
          var a;
          if (!n) {
            t.error("无法生成截图blob"), s(null);
            return;
          }
          try {
            const d = `screenshot_${Date.now()}.jpg`, r = new File([n], d, { type: "image/jpeg" });
            this.config.isDebug && t.debug("BytedeskWeb: 截图生成成功，文件大小:", Math.round(n.size / 1024), "KB");
            const { uploadScreenshot: h } = await import("../../apis/upload/index.js"), c = await h(r, {
              orgUid: ((a = this.config.chatConfig) == null ? void 0 : a.org) || "",
              isDebug: this.config.isDebug
            });
            this.config.isDebug && t.debug("BytedeskWeb: 截图上传成功，URL:", c), s(c);
          } catch (d) {
            t.error("截图上传失败:", d), s(null);
          }
        }, "image/jpeg", 0.8);
      });
    } catch (i) {
      return t.error("生成截图失败:", i), null;
    }
  }
  /**
   * 生成截图预览（不上传到服务器）
   */
  async generateScreenshotPreview() {
    var i;
    const e = (i = this.feedbackDialog) == null ? void 0 : i.querySelector("#bytedesk-screenshot-container");
    if (e)
      try {
        const o = await this.loadHtml2Canvas();
        if (!o) {
          e.innerHTML = `
          <div style="color: #999; text-align: center; padding: 20px; flex-direction: column; gap: 8px; display: flex; align-items: center;">
            <div style="font-size: 24px;">📷</div>
            <div>截图功能暂时不可用</div>
            <div style="font-size: 12px; color: #666;">网络连接问题或资源加载失败</div>
          </div>
        `;
          return;
        }
        e.innerHTML = "正在生成截图预览...", this.config.isDebug && t.debug("BytedeskWeb: 开始生成截图预览");
        const s = this.calculateScreenshotArea(), n = await o(document.body, {
          height: s.height,
          width: s.width,
          x: s.x,
          y: s.y,
          useCORS: !0,
          allowTaint: !0,
          backgroundColor: "#ffffff",
          scale: 1,
          ignoreElements: (h) => h.hasAttribute("data-bytedesk-feedback") || h.closest("[data-bytedesk-feedback]") !== null
        }), a = document.createElement("img");
        a.src = n.toDataURL("image/jpeg", 0.8), a.style.cssText = `
        max-width: 100%;
        max-height: 200px;
        border-radius: 4px;
        border: 1px solid #ddd;
        cursor: pointer;
      `, a.onclick = () => {
          const h = document.createElement("img");
          h.src = a.src, h.style.cssText = `
          max-width: 90vw;
          max-height: 90vh;
          border-radius: 8px;
          box-shadow: 0 8px 32px rgba(0,0,0,0.3);
        `;
          const c = document.createElement("div");
          c.style.cssText = `
          position: fixed;
          top: 0;
          left: 0;
          width: 100vw;
          height: 100vh;
          background: rgba(0,0,0,0.8);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 1000001;
          cursor: pointer;
        `;
          const l = document.createElement("div");
          l.style.cssText = `
          position: absolute;
          top: 20px;
          right: 20px;
          color: white;
          font-size: 14px;
          background: rgba(0,0,0,0.6);
          padding: 8px 12px;
          border-radius: 4px;
          user-select: none;
        `, l.textContent = "点击任意位置关闭", c.appendChild(l), c.appendChild(h), c.onclick = () => document.body.removeChild(c), document.body.appendChild(c);
        };
        const d = document.createElement("div");
        d.style.cssText = `
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
      `, d.appendChild(a);
        const r = document.createElement("div");
        r.style.cssText = `
        font-size: 12px;
        color: #666;
        text-align: center;
      `, r.innerHTML = "点击图片可放大查看<br/>提交时将自动上传此截图", d.appendChild(r), e.innerHTML = "", e.appendChild(d), this.feedbackDialog.screenshotCanvas = n, this.config.isDebug && t.debug("BytedeskWeb: 截图预览生成成功");
      } catch (o) {
        t.error("生成截图预览失败:", o), e.innerHTML = `
        <div style="color: #ff6b6b; text-align: center; flex-direction: column; gap: 8px; display: flex; align-items: center;">
          <div style="font-size: 24px;">⚠️</div>
          <div>截图预览生成失败</div>
          <div style="font-size: 12px; margin-top: 4px; color: #999;">请检查页面权限或网络连接</div>
        </div>
      `;
      }
  }
  /**
   * 计算选中文本附近的截图区域
   */
  calculateScreenshotArea() {
    let e = {
      height: window.innerHeight,
      width: window.innerWidth,
      x: 0,
      y: 0,
      scrollX: 0,
      scrollY: 0
    };
    try {
      let i = this.lastSelectionRect;
      if (!i) {
        const o = window.getSelection();
        o && o.rangeCount > 0 && (i = o.getRangeAt(0).getBoundingClientRect());
      }
      if (i && i.width > 0 && i.height > 0) {
        const o = window.pageXOffset || document.documentElement.scrollLeft, s = window.pageYOffset || document.documentElement.scrollTop, n = i.left + o, a = i.top + s, d = Math.min(800, window.innerWidth), r = Math.min(600, window.innerHeight);
        let h = n - d / 2, c = a - r / 2;
        const l = document.documentElement.scrollWidth, g = document.documentElement.scrollHeight;
        h = Math.max(0, Math.min(h, l - d)), c = Math.max(0, Math.min(c, g - r)), e = {
          height: r,
          width: d,
          x: h,
          y: c,
          scrollX: 0,
          scrollY: 0
        }, this.config.isDebug && t.debug("BytedeskWeb: 选中文本截图区域:", {
          selectedRect: i,
          absolutePosition: { left: n, top: a },
          captureArea: { x: h, y: c, width: d, height: r },
          pageSize: { width: l, height: g }
        });
      } else if (this.lastMouseEvent) {
        const o = window.pageXOffset || document.documentElement.scrollLeft, s = window.pageYOffset || document.documentElement.scrollTop, n = this.lastMouseEvent.clientX + o, a = this.lastMouseEvent.clientY + s, d = Math.min(800, window.innerWidth), r = Math.min(600, window.innerHeight);
        let h = n - d / 2, c = a - r / 2;
        const l = document.documentElement.scrollWidth, g = document.documentElement.scrollHeight;
        h = Math.max(0, Math.min(h, l - d)), c = Math.max(0, Math.min(c, g - r)), e = {
          height: r,
          width: d,
          x: h,
          y: c,
          scrollX: 0,
          scrollY: 0
        }, this.config.isDebug && t.debug("BytedeskWeb: 鼠标位置截图区域:", {
          mousePosition: { x: this.lastMouseEvent.clientX, y: this.lastMouseEvent.clientY },
          absolutePosition: { x: n, y: a },
          captureArea: { x: h, y: c, width: d, height: r }
        });
      }
    } catch (i) {
      this.config.isDebug && t.warn("BytedeskWeb: 计算截图区域失败，使用默认区域:", i);
    }
    return e;
  }
  /**
   * 动态加载 html2canvas
   */
  async loadHtml2Canvas() {
    try {
      return window.html2canvas ? window.html2canvas : await this.loadHtml2CanvasFromCDN();
    } catch (e) {
      return this.config.isDebug && t.warn("html2canvas 加载失败:", e), null;
    }
  }
  /**
   * 从CDN加载html2canvas
   */
  async loadHtml2CanvasFromCDN() {
    return new Promise((e, i) => {
      if (window.html2canvas) {
        e(window.html2canvas);
        return;
      }
      const o = document.createElement("script");
      o.src = this.config.apiUrl + "/assets/js/html2canvas.min.js", o.onload = () => {
        window.html2canvas ? e(window.html2canvas) : i(new Error("html2canvas 加载失败"));
      }, o.onerror = () => {
        i(new Error("无法从CDN加载html2canvas"));
      }, document.head.appendChild(o);
    });
  }
  /**
   * 提交反馈
   */
  async submitFeedback() {
    var d, r, h, c, l, g, b;
    const e = (d = this.feedbackDialog) == null ? void 0 : d.querySelector("#bytedesk-feedback-text"), i = (e == null ? void 0 : e.value.trim()) || "";
    if (!i) {
      alert("请填写反馈内容"), e == null || e.focus();
      return;
    }
    const o = [], s = (r = this.feedbackDialog) == null ? void 0 : r.querySelectorAll('input[name="feedback-type"]:checked');
    if (s && s.forEach((u) => {
      o.push(u.value);
    }), (h = this.config.feedbackConfig) != null && h.requiredTypes && o.length === 0) {
      alert("请至少选择一个问题类型");
      return;
    }
    const n = (c = this.feedbackDialog) == null ? void 0 : c.querySelector(".bytedesk-feedback-submit"), a = (n == null ? void 0 : n.textContent) || "提交反馈";
    n && (n.disabled = !0, n.textContent = "提交中...", n.style.opacity = "0.6");
    try {
      const u = (l = this.feedbackDialog) == null ? void 0 : l.querySelector("#bytedesk-submit-screenshot"), x = (u == null ? void 0 : u.checked) !== !1;
      let m = [];
      if (x) {
        this.config.isDebug && t.debug("BytedeskWeb: 开始生成和上传截图"), n && (n.textContent = "正在生成截图...");
        const T = await this.generateAndUploadScreenshot();
        T && (m.push(T), this.config.isDebug && t.debug("BytedeskWeb: 截图上传成功:", T)), n && (n.textContent = "正在提交反馈...");
      }
      const w = {
        selectedText: this.selectedText,
        ...m.length > 0 && { images: m },
        // 将截图URL放入images数组
        content: i,
        url: window.location.href,
        title: document.title,
        userAgent: navigator.userAgent,
        visitorUid: localStorage.getItem("bytedesk_uid") || "",
        orgUid: ((g = this.config.chatConfig) == null ? void 0 : g.org) || "",
        ...o.length > 0 && { categoryNames: o.join(",") }
      };
      (b = this.config.feedbackConfig) != null && b.onSubmit ? this.config.feedbackConfig.onSubmit(w) : await this.submitFeedbackToServer(w), this.showFeedbackSuccess(), setTimeout(() => {
        this.hideFeedbackDialog();
      }, 2e3);
    } catch (u) {
      t.error("提交反馈失败:", u), alert("提交失败，请稍后重试");
    } finally {
      n && (n.disabled = !1, n.textContent = a, n.style.opacity = "1");
    }
  }
  /**
   * 提交反馈到服务器
   */
  async submitFeedbackToServer(e) {
    try {
      const { submitFeedback: i } = await import("../../apis/feedback/index.js"), o = await i(e);
      return this.config.isDebug && t.debug("反馈提交响应:", o), o;
    } catch (i) {
      throw t.error("提交反馈到服务器失败:", i), i;
    }
  }
  /**
   * 显示反馈成功消息
   */
  showFeedbackSuccess() {
    var i;
    if (!this.feedbackDialog) return;
    const e = this.feedbackDialog.querySelector("div > div");
    e && (e.innerHTML = `
      <div style="text-align: center; padding: 40px 20px;">
        <div style="font-size: 48px; margin-bottom: 16px;">✅</div>
        <h3 style="margin: 0 0 12px 0; color: #28a745;">
          ${((i = this.config.feedbackConfig) == null ? void 0 : i.successMessage) || "反馈已提交，感谢您的意见！"}
        </h3>
        <div style="color: #666; font-size: 14px;">
          我们会认真处理您的反馈，不断改进产品体验
        </div>
      </div>
    `);
  }
  /**
   * 公共方法：显示反馈对话框
   */
  showDocumentFeedback(e) {
    var i;
    if (!((i = this.config.feedbackConfig) != null && i.enabled)) {
      t.warn("文档反馈功能未启用");
      return;
    }
    e && (this.selectedText = e), this.showFeedbackDialog();
  }
  /**
   * 公共方法：重新初始化反馈功能
   */
  reinitFeedbackFeature() {
    this.config.isDebug && t.debug("BytedeskWeb: 重新初始化反馈功能"), this.destroyFeedbackFeature(), this.initFeedbackFeature();
  }
  /**
   * 公共方法：强制初始化反馈功能（用于调试）
   */
  forceInitFeedbackFeature() {
    return t.debug("BytedeskWeb: 强制初始化反馈功能被调用"), t.debug("BytedeskWeb: 当前配置:", this.config.feedbackConfig), t.debug("BytedeskWeb: isDebug:", this.config.isDebug), this.config.feedbackConfig || (t.debug("BytedeskWeb: 创建默认反馈配置"), this.config.feedbackConfig = {
      enabled: !0,
      trigger: "selection",
      showOnSelection: !0,
      selectionText: "📝 文档反馈",
      dialogTitle: "提交意见反馈",
      placeholder: "请详细描述您发现的问题、改进建议或其他意见...",
      submitText: "提交反馈",
      cancelText: "取消",
      successMessage: "感谢您的反馈！我们会认真处理您的意见。"
    }), this.config.feedbackConfig.enabled || (t.debug("BytedeskWeb: 启用反馈配置"), this.config.feedbackConfig.enabled = !0), t.debug("BytedeskWeb: 销毁现有反馈功能"), this.destroyFeedbackFeature(), t.debug("BytedeskWeb: 重新初始化反馈功能"), this.initFeedbackFeature(), t.debug("BytedeskWeb: 强制初始化完成，检查结果:"), t.debug("- showDocumentFeedback方法存在:", typeof this.showDocumentFeedback == "function"), t.debug("- testTextSelection方法存在:", typeof this.testTextSelection == "function"), t.debug("- 反馈提示框存在:", !!this.feedbackTooltip), t.debug("- 反馈对话框存在:", !!this.feedbackDialog), t.debug("- 反馈提示框DOM存在:", !!document.querySelector('[data-bytedesk-feedback="tooltip"]')), t.debug("- 反馈对话框DOM存在:", !!document.querySelector('[data-bytedesk-feedback="dialog"]')), {
      success: !!(this.feedbackTooltip && this.feedbackDialog),
      methods: {
        showDocumentFeedback: typeof this.showDocumentFeedback == "function",
        testTextSelection: typeof this.testTextSelection == "function"
      },
      elements: {
        tooltip: !!this.feedbackTooltip,
        dialog: !!this.feedbackDialog,
        tooltipDOM: !!document.querySelector('[data-bytedesk-feedback="tooltip"]'),
        dialogDOM: !!document.querySelector('[data-bytedesk-feedback="dialog"]')
      }
    };
  }
  /**
   * 公共方法：测试文本选择功能
   */
  testTextSelection(e = "测试选中文字") {
    this.config.isDebug && t.debug("BytedeskWeb: 测试文本选择功能，模拟选中文字:", `"${e}"`), this.selectedText = e;
    try {
      const i = document.createElement("div");
      i.textContent = e, i.style.cssText = `
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        padding: 20px;
        background: #f0f0f0;
        border: 2px dashed #ccc;
        border-radius: 8px;
        font-size: 16px;
        z-index: 1000;
        pointer-events: none;
      `, document.body.appendChild(i);
      const o = document.createRange();
      o.selectNodeContents(i);
      const s = window.getSelection();
      s && (s.removeAllRanges(), s.addRange(o), this.config.isDebug && t.debug("BytedeskWeb: 已创建模拟文本选择"), this.feedbackTooltip ? this.showFeedbackTooltip() : t.error("BytedeskWeb: 反馈提示框不存在，无法测试"), setTimeout(() => {
        s && s.removeAllRanges(), document.body.contains(i) && document.body.removeChild(i), this.hideFeedbackTooltip();
      }, 5e3));
    } catch (i) {
      t.error("BytedeskWeb: 创建测试选择失败:", i);
    }
  }
  /**
   * 公共方法：获取调试信息
   */
  getDebugInfo() {
    return {
      config: this.config,
      feedbackConfig: this.config.feedbackConfig,
      feedbackTooltip: !!this.feedbackTooltip,
      feedbackDialog: !!this.feedbackDialog,
      selectedText: this.selectedText,
      methods: {
        showDocumentFeedback: typeof this.showDocumentFeedback,
        testTextSelection: typeof this.testTextSelection,
        forceInitFeedbackFeature: typeof this.forceInitFeedbackFeature
      }
    };
  }
  /**
   * 公共方法：销毁反馈功能
   */
  destroyFeedbackFeature() {
    this.feedbackTooltip && (this.feedbackTooltip.remove(), this.feedbackTooltip = null), this.feedbackDialog && (this.feedbackDialog.remove(), this.feedbackDialog = null), this.selectedText = "";
  }
}
export {
  te as default
};
