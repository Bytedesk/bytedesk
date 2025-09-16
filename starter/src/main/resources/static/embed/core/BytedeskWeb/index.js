var z = Object.defineProperty;
var _ = (F, e, t) => e in F ? z(F, e, { enumerable: !0, configurable: !0, writable: !0, value: t }) : F[e] = t;
var u = (F, e, t) => _(F, typeof e != "symbol" ? e + "" : e, t);
import { BYTEDESK_UID as W, BYTEDESK_VISITOR_UID as U, BYTEDESK_BROWSE_FAILED_TIMESTAMP as O, POST_MESSAGE_LOCALSTORAGE_RESPONSE as V, POST_MESSAGE_INVITE_VISITOR_REJECT as P, POST_MESSAGE_INVITE_VISITOR_ACCEPT as H, POST_MESSAGE_INVITE_VISITOR as A, POST_MESSAGE_RECEIVE_MESSAGE as Y, POST_MESSAGE_MINIMIZE_WINDOW as N, POST_MESSAGE_MAXIMIZE_WINDOW as j, POST_MESSAGE_CLOSE_CHAT_WINDOW as X } from "../../utils/constants/index.js";
class K {
  constructor(e) {
    u(this, "config");
    u(this, "bubble", null);
    u(this, "window", null);
    u(this, "inviteDialog", null);
    u(this, "contextMenu", null);
    u(this, "hideTimeout", null);
    u(this, "isVisible", !1);
    u(this, "isDragging", !1);
    u(this, "windowState", "normal");
    u(this, "loopCount", 0);
    u(this, "loopTimer", null);
    // 添加请求状态管理
    u(this, "initVisitorPromise", null);
    u(this, "getUnreadMessageCountPromise", null);
    u(this, "clearUnreadMessagesPromise", null);
    // 文档反馈功能相关属性
    u(this, "feedbackTooltip", null);
    u(this, "feedbackDialog", null);
    u(this, "selectedText", "");
    // 添加防抖和状态管理
    u(this, "selectionDebounceTimer", null);
    u(this, "isTooltipVisible", !1);
    u(this, "lastSelectionText", "");
    u(this, "lastMouseEvent", null);
    u(this, "lastSelectionRect", null);
    this.config = {
      ...this.getDefaultConfig(),
      ...e
    }, this.setupApiUrl();
  }
  async setupApiUrl() {
    try {
      const { setApiUrl: e } = await import("../../apis/request/index.js"), t = this.config.apiUrl || "https://api.weiyuai.cn";
      e(t), this.config.isDebug && console.log("API URL 已设置为:", t);
    } catch (e) {
      console.error("设置API URL时出错:", e);
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
    var e, t;
    if (await this._initVisitor(), await this._browseVisitor(), this.createBubble(), this.createInviteDialog(), this.setupMessageListener(), this.setupResizeListener(), (e = this.config.feedbackConfig) != null && e.enabled && (this.config.isDebug && console.log("BytedeskWeb: 开始初始化文档反馈功能，document.readyState:", document.readyState), this.initFeedbackFeature(), document.readyState !== "complete")) {
      this.config.isDebug && console.log("BytedeskWeb: DOM未完全加载，设置备用初始化");
      const i = () => {
        this.config.isDebug && console.log("BytedeskWeb: window load事件触发，重新初始化反馈功能"), this.initFeedbackFeature(), window.removeEventListener("load", i);
      };
      window.addEventListener("load", i);
      const n = () => {
        this.config.isDebug && console.log("BytedeskWeb: DOMContentLoaded事件触发，重新初始化反馈功能"), setTimeout(() => this.initFeedbackFeature(), 100), document.removeEventListener("DOMContentLoaded", n);
      };
      document.readyState === "loading" && document.addEventListener("DOMContentLoaded", n);
    }
    this._getUnreadMessageCount(), this.config.autoPopup && setTimeout(() => {
      this.showChat();
    }, this.config.autoPopupDelay || 1e3), (t = this.config.inviteConfig) != null && t.show && setTimeout(() => {
      this.showInviteDialog();
    }, this.config.inviteConfig.delay || 3e3);
  }
  async _initVisitor() {
    var o, s, l, c;
    if (this.initVisitorPromise)
      return console.log("访客初始化请求正在进行中，返回现有Promise"), this.initVisitorPromise;
    const e = localStorage.getItem(W), t = localStorage.getItem(U);
    console.log("localUid: ", e), console.log("localVisitorUid: ", t);
    const n = ((o = this.config.chatConfig) == null ? void 0 : o.visitorUid) && t ? ((s = this.config.chatConfig) == null ? void 0 : s.visitorUid) === t : !0;
    return e && t && n ? (console.log("访客信息相同，直接返回本地访客信息"), (c = (l = this.config).onVisitorInfo) == null || c.call(l, e || "", t || ""), {
      uid: e,
      visitorUid: t
    }) : (console.log("开始创建访客初始化Promise"), this.initVisitorPromise = import("../../apis/visitor/index.js").then(
      async ({ initVisitor: r }) => {
        var a, d, g, h, b, k, p, x, w, C, S, D, M, I, f, v, E, T, y;
        try {
          const B = {
            uid: String(((a = this.config.chatConfig) == null ? void 0 : a.uid) || e || ""),
            visitorUid: String(
              ((d = this.config.chatConfig) == null ? void 0 : d.visitorUid) || t || ""
            ),
            orgUid: String(((g = this.config.chatConfig) == null ? void 0 : g.org) || ""),
            nickname: String(((h = this.config.chatConfig) == null ? void 0 : h.name) || ""),
            avatar: String(((b = this.config.chatConfig) == null ? void 0 : b.avatar) || ""),
            mobile: String(((k = this.config.chatConfig) == null ? void 0 : k.mobile) || ""),
            email: String(((p = this.config.chatConfig) == null ? void 0 : p.email) || ""),
            note: String(((x = this.config.chatConfig) == null ? void 0 : x.note) || ""),
            extra: typeof ((w = this.config.chatConfig) == null ? void 0 : w.extra) == "string" ? this.config.chatConfig.extra : JSON.stringify(((C = this.config.chatConfig) == null ? void 0 : C.extra) || {})
          }, m = await r(B);
          return console.log("访客初始化API响应:", m.data, B), ((S = m.data) == null ? void 0 : S.code) === 200 ? ((M = (D = m.data) == null ? void 0 : D.data) != null && M.uid && (localStorage.setItem(W, m.data.data.uid), console.log("已保存uid到localStorage:", m.data.data.uid)), (f = (I = m.data) == null ? void 0 : I.data) != null && f.visitorUid && (localStorage.setItem(
            U,
            m.data.data.visitorUid
          ), console.log(
            "已保存visitorUid到localStorage:",
            m.data.data.visitorUid
          )), (v = m.data) != null && v.data && (console.log("触发onVisitorInfo回调"), (T = (E = this.config).onVisitorInfo) == null || T.call(
            E,
            m.data.data.uid || "",
            m.data.data.visitorUid || ""
          )), m.data.data) : (console.error("访客初始化失败:", (y = m.data) == null ? void 0 : y.message), null);
        } catch (B) {
          return console.error("访客初始化出错:", B), null;
        } finally {
          console.log("访客初始化Promise完成，清除引用"), this.initVisitorPromise = null;
        }
      }
    ), this.initVisitorPromise);
  }
  // 获取当前页面浏览信息并发送到服务器
  async _browseVisitor() {
    var e, t, i, n;
    try {
      const o = localStorage.getItem(O);
      if (o) {
        const M = parseInt(o), I = Date.now(), f = 60 * 60 * 1e3;
        if (I - M < f) {
          const v = Math.ceil((f - (I - M)) / 1e3 / 60);
          console.log(`浏览记录发送失败后1小时内禁止发送，还需等待 ${v} 分钟`);
          return;
        } else
          localStorage.removeItem(O);
      }
      const s = window.location.href, l = document.title, c = document.referrer, r = navigator.userAgent, a = this.getBrowserInfo(r), d = this.getOSInfo(r), g = this.getDeviceInfo(r), h = `${screen.width}x${screen.height}`, b = new URLSearchParams(window.location.search), k = b.get("utm_source") || void 0, p = b.get("utm_medium") || void 0, x = b.get("utm_campaign") || void 0, w = localStorage.getItem(W), C = {
        url: s,
        title: l,
        referrer: c,
        userAgent: r,
        operatingSystem: d,
        browser: a,
        deviceType: g,
        screenResolution: h,
        utmSource: k,
        utmMedium: p,
        utmCampaign: x,
        status: "ONLINE",
        // 注意这里就是uid，不是visitorUid，使用访客系统生成uid
        visitorUid: String(
          ((e = this.config.chatConfig) == null ? void 0 : e.uid) || w || ""
        ),
        orgUid: ((t = this.config.chatConfig) == null ? void 0 : t.org) || ""
      };
      if (!C.visitorUid) {
        console.log("访客uid为空，跳过browse操作");
        return;
      }
      const { browse: S } = await import("../../apis/visitor/index.js"), D = await S(C);
      ((i = D.data) == null ? void 0 : i.code) === 200 ? localStorage.removeItem(O) : (console.error("浏览记录发送失败:", (n = D.data) == null ? void 0 : n.message), localStorage.setItem(O, Date.now().toString()), console.log("已记录浏览记录发送失败时间，1小时内将禁止再次发送"));
    } catch (o) {
      console.error("发送浏览记录时出错:", o), localStorage.setItem(O, Date.now().toString()), console.log("已记录浏览记录发送失败时间，1小时内将禁止再次发送");
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
    return this.getUnreadMessageCountPromise ? (this.config.isDebug && console.log("获取未读消息数请求正在进行中，返回现有Promise"), this.getUnreadMessageCountPromise) : (this.getUnreadMessageCountPromise = import("../../apis/message/index.js").then(
      async ({ getUnreadMessageCount: e }) => {
        var t, i, n, o, s;
        try {
          const l = String(((t = this.config.chatConfig) == null ? void 0 : t.visitorUid) || ""), c = localStorage.getItem(W), r = localStorage.getItem(U), a = {
            uid: c || "",
            visitorUid: l || r || "",
            orgUid: ((i = this.config.chatConfig) == null ? void 0 : i.org) || ""
          };
          if (a.uid === "")
            return 0;
          const d = await e(a);
          return ((n = d.data) == null ? void 0 : n.code) === 200 ? ((o = d == null ? void 0 : d.data) != null && o.data && ((s = d == null ? void 0 : d.data) == null ? void 0 : s.data) > 0 ? this.showUnreadBadge(d.data.data) : this.clearUnreadBadge(), d.data.data || 0) : 0;
        } catch (l) {
          return console.error("获取未读消息数出错:", l), 0;
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
    localStorage.removeItem(O), this.config.isDebug && console.log("已清除浏览记录发送失败的限制");
  }
  // 清除本地访客信息，强制重新初始化
  clearVisitorInfo() {
    localStorage.removeItem(W), localStorage.removeItem(U), this.config.isDebug && console.log("已清除本地访客信息");
  }
  // 强制重新初始化访客信息（忽略本地缓存）
  async forceInitVisitor() {
    return this.clearVisitorInfo(), this.initVisitorPromise = null, this._initVisitor();
  }
  // 显示未读消息数角标
  showUnreadBadge(e) {
    if (console.log("showUnreadBadge() 被调用，count:", e), (this.config.buttonConfig || {}).show === !1) {
      console.log("showUnreadBadge: buttonConfig.show 为 false，不显示角标");
      return;
    }
    if (!this.bubble) {
      console.log("showUnreadBadge: bubble 不存在");
      return;
    }
    let i = this.bubble.querySelector(
      ".bytedesk-unread-badge"
    );
    i ? console.log("showUnreadBadge: 更新现有角标") : (console.log("showUnreadBadge: 创建新的角标"), i = document.createElement("div"), i.className = "bytedesk-unread-badge", i.style.cssText = `
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
      `, this.bubble.appendChild(i)), i.textContent = e > 99 ? "99+" : e.toString(), console.log("showUnreadBadge: 角标数字已更新为", i.textContent);
  }
  // 清除未读消息数角标
  clearUnreadBadge() {
    if (!this.bubble) {
      console.log("clearUnreadBadge: bubble 不存在");
      return;
    }
    const e = this.bubble.querySelector(".bytedesk-unread-badge");
    e ? e.remove() : console.log("clearUnreadBadge: 未找到角标");
  }
  // 清空未读消息
  async clearUnreadMessages() {
    return this.clearUnreadMessagesPromise ? (this.config.isDebug && console.log("清空未读消息请求正在进行中，返回现有Promise"), this.clearUnreadMessagesPromise) : (this.clearUnreadMessagesPromise = import("../../apis/message/index.js").then(
      async ({ clearUnreadMessages: e }) => {
        var t, i;
        try {
          const n = String(((t = this.config.chatConfig) == null ? void 0 : t.visitorUid) || ""), o = localStorage.getItem(W), s = localStorage.getItem(U), l = {
            uid: o || "",
            visitorUid: n || s || "",
            orgUid: ((i = this.config.chatConfig) == null ? void 0 : i.org) || ""
          }, c = await e(l);
          return console.log("清空未读消息数:", c.data, l), c.data.code === 200 ? (console.log("清空未读消息数成功:", c.data), this.clearUnreadBadge(), c.data.data || 0) : (console.error("清空未读消息数失败:", c.data.message), 0);
        } catch (n) {
          return console.error("清空未读消息数出错:", n), 0;
        } finally {
          this.clearUnreadMessagesPromise = null;
        }
      }
    ), this.clearUnreadMessagesPromise);
  }
  createBubble() {
    var d, g, h, b, k, p, x, w, C, S, D, M, I;
    if (this.bubble && document.body.contains(this.bubble)) {
      console.log("createBubble: 气泡已存在，不重复创建");
      return;
    }
    this.bubble && !document.body.contains(this.bubble) && (console.log("createBubble: 清理已存在的 bubble 引用"), this.bubble = null);
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
    let t = null;
    if ((d = this.config.bubbleConfig) != null && d.show) {
      t = document.createElement("div"), t.style.cssText = `
        background: ${((g = this.config.theme) == null ? void 0 : g.mode) === "dark" ? "#1f2937" : "white"};
        color: ${((h = this.config.theme) == null ? void 0 : h.mode) === "dark" ? "#e5e7eb" : "#1f2937"};
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
      const v = document.createElement("span");
      v.textContent = ((b = this.config.bubbleConfig) == null ? void 0 : b.icon) || "", v.style.fontSize = "20px", f.appendChild(v);
      const E = document.createElement("div"), T = document.createElement("div");
      T.textContent = ((k = this.config.bubbleConfig) == null ? void 0 : k.title) || "", T.style.fontWeight = "bold", T.style.color = ((p = this.config.theme) == null ? void 0 : p.mode) === "dark" ? "#e5e7eb" : "#1f2937", T.style.marginBottom = "4px", T.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", E.appendChild(T);
      const y = document.createElement("div");
      y.textContent = ((x = this.config.bubbleConfig) == null ? void 0 : x.subtitle) || "", y.style.fontSize = "0.9em", y.style.color = ((w = this.config.theme) == null ? void 0 : w.mode) === "dark" ? "#9ca3af" : "#4b5563", y.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", E.appendChild(y), f.appendChild(E), t.appendChild(f);
      const B = document.createElement("div");
      B.style.cssText = `
        position: absolute;
        bottom: -6px;
        ${this.config.placement === "bottom-left" ? "left: 24px" : "right: 24px"};
        width: 12px;
        height: 12px;
        background: ${((C = this.config.theme) == null ? void 0 : C.mode) === "dark" ? "#1f2937" : "white"};
        transform: rotate(45deg);
        box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      `;
      const m = document.createElement("div");
      m.style.cssText = `
        position: absolute;
        bottom: 0;
        ${this.config.placement === "bottom-left" ? "left: 18px" : "right: 18px"};
        width: 24px;
        height: 12px;
        background: ${((S = this.config.theme) == null ? void 0 : S.mode) === "dark" ? "#1f2937" : "white"};
      `, t.appendChild(B), t.appendChild(m), e.appendChild(t), setTimeout(() => {
        t && (t.style.opacity = "1", t.style.transform = "translateY(0)");
      }, 500);
    }
    this.bubble = document.createElement("button");
    const i = this.config.buttonConfig || {}, n = i.width || 60, o = i.height || 60, s = Math.min(n, o) / 2, l = ((D = this.config.theme) == null ? void 0 : D.mode) === "dark", c = l ? "#3B82F6" : "#0066FF", r = ((M = this.config.theme) == null ? void 0 : M.backgroundColor) || c;
    this.bubble.style.cssText = `
      background-color: ${r};
      width: ${n}px;
      height: ${o}px;
      border-radius: ${s}px;
      border: none;
      cursor: ${this.config.draggable ? "move" : "pointer"};
      display: ${i.show === !1 ? "none" : "flex"};
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(0, 0, 0, ${l ? "0.3" : "0.12"});
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
    `;
    const a = document.createElement("div");
    if (a.style.cssText = `
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    `, i.icon) {
      const f = document.createElement("span");
      f.textContent = i.icon, f.style.fontSize = `${o * 0.4}px`, a.appendChild(f);
    } else {
      const f = document.createElement("div");
      f.innerHTML = `
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
                fill="white"/>
        </svg>
      `, a.appendChild(f);
    }
    if (i.text) {
      const f = document.createElement("span");
      f.textContent = i.text, f.style.cssText = `
        color: ${((I = this.config.theme) == null ? void 0 : I.textColor) || "#ffffff"};
        font-size: ${o * 0.25}px;
        white-space: nowrap;
      `, a.appendChild(f);
    }
    if (this.bubble.appendChild(a), this.bubble.addEventListener("mouseenter", () => {
      this.bubble.style.transform = "scale(1.1)";
    }), this.bubble.addEventListener("mouseleave", () => {
      this.bubble.style.transform = "scale(1)";
    }), e.appendChild(this.bubble), this.config.draggable) {
      let f = 0, v = 0, E = 0, T = 0;
      this.bubble.addEventListener("mousedown", (y) => {
        y.button === 0 && (this.isDragging = !0, f = y.clientX, v = y.clientY, E = e.offsetLeft, T = e.offsetTop, e.style.transition = "none");
      }), document.addEventListener("mousemove", (y) => {
        if (!this.isDragging) return;
        y.preventDefault();
        const B = y.clientX - f, m = y.clientY - v, L = E + B, $ = T + m, R = window.innerHeight - e.offsetHeight;
        L <= window.innerWidth / 2 ? (e.style.left = `${Math.max(0, L)}px`, e.style.right = "auto", e.style.alignItems = "flex-start", this.config.placement = "bottom-left") : (e.style.right = `${Math.max(
          0,
          window.innerWidth - L - e.offsetWidth
        )}px`, e.style.left = "auto", e.style.alignItems = "flex-end", this.config.placement = "bottom-right"), e.style.bottom = `${Math.min(
          Math.max(0, window.innerHeight - $ - e.offsetHeight),
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
        console.log("bubble click");
        const f = this.bubble.messageElement;
        f instanceof HTMLElement && (f.style.display = "none"), this.showChat();
      }
    }), this.bubble.messageElement = t, document.body.appendChild(e), this.bubble.addEventListener("contextmenu", (f) => {
      this.showContextMenu(f);
    }), document.addEventListener("click", () => {
      this.hideContextMenu();
    });
  }
  createChatWindow() {
    var l, c, r, a, d, g;
    if (this.window && document.body.contains(this.window)) {
      console.log("createChatWindow: 聊天窗口已存在，不重复创建");
      return;
    }
    this.window && !document.body.contains(this.window) && (console.log("createChatWindow: 清理已存在的 window 引用"), this.window = null), this.window = document.createElement("div");
    const e = window.innerWidth <= 768, t = window.innerWidth, i = window.innerHeight, n = Math.min(
      ((l = this.config.window) == null ? void 0 : l.width) || t * 0.9,
      t * 0.9
    ), o = Math.min(
      ((c = this.config.window) == null ? void 0 : c.height) || i * 0.9,
      i * 0.9
    );
    e ? this.window.style.cssText = `
        position: fixed;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 90vh;
        display: none;
        z-index: 10000;
        border-top-left-radius: 12px;
        border-top-right-radius: 12px;
        overflow: hidden;
        transition: all ${(r = this.config.animation) == null ? void 0 : r.duration}ms ${(a = this.config.animation) == null ? void 0 : a.type};
      ` : this.window.style.cssText = `
        position: fixed;
        ${this.config.placement === "bottom-right" ? "right" : "left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${n}px;
        height: ${o}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(d = this.config.animation) == null ? void 0 : d.duration}ms ${(g = this.config.animation) == null ? void 0 : g.type};
      `;
    const s = document.createElement("iframe");
    s.style.cssText = `
      width: 100%;
      height: 100%;
      border: none;
      display: block; // 添加这一行
      vertical-align: bottom; // 添加这一行
    `, s.src = this.generateChatUrl(), console.log("iframe.src: ", s.src), this.window.appendChild(s), document.body.appendChild(this.window);
  }
  generateChatUrl(e = "messages") {
    console.log("this.config: ", this.config, e);
    const t = new URLSearchParams(), i = localStorage.getItem(W), n = localStorage.getItem(U);
    i && i.trim() !== "" && t.append("uid", i), n && n.trim() !== "" && t.append("visitorUid", n), Object.entries(this.config.chatConfig || {}).forEach(([s, l]) => {
      if (s === "goodsInfo" || s === "orderInfo")
        try {
          typeof l == "string" ? t.append(s, l) : t.append(s, JSON.stringify(l));
        } catch (c) {
          console.error(`Error processing ${s}:`, c);
        }
      else if (s === "extra")
        try {
          let c = typeof l == "string" ? JSON.parse(l) : l;
          c.goodsInfo && delete c.goodsInfo, c.orderInfo && delete c.orderInfo, Object.keys(c).length > 0 && t.append(s, JSON.stringify(c));
        } catch (c) {
          console.error("Error processing extra parameter:", c);
        }
      else
        t.append(s, String(l));
    }), Object.entries(this.config.browseConfig || {}).forEach(([s, l]) => {
      t.append(s, String(l));
    }), Object.entries(this.config.theme || {}).forEach(([s, l]) => {
      t.append(s, String(l));
    }), t.append("lang", this.config.locale || "zh-cn");
    const o = `${this.config.htmlUrl}?${t.toString()}`;
    return console.log("chat url: ", o), o;
  }
  setupMessageListener() {
    window.addEventListener("message", (e) => {
      switch (e.data.type) {
        case X:
          this.hideChat();
          break;
        case j:
          this.toggleMaximize();
          break;
        case N:
          this.minimizeWindow();
          break;
        case Y:
          console.log("RECEIVE_MESSAGE");
          break;
        case A:
          console.log("INVITE_VISITOR");
          break;
        case H:
          console.log("INVITE_VISITOR_ACCEPT");
          break;
        case P:
          console.log("INVITE_VISITOR_REJECT");
          break;
        case V:
          this.handleLocalStorageData(e);
          break;
      }
    });
  }
  // 处理从 iframe 返回的 localStorage 数据
  handleLocalStorageData(e) {
    var s, l;
    const { uid: t, visitorUid: i } = e.data;
    console.log("handleLocalStorageData 被调用", t, i, e.data);
    const n = localStorage.getItem(W), o = localStorage.getItem(U);
    if (n === t && o === i) {
      console.log("handleLocalStorageData: 值相同，跳过设置");
      return;
    }
    localStorage.setItem(W, t), localStorage.setItem(U, i), console.log("handleLocalStorageData: 已更新localStorage", {
      uid: t,
      visitorUid: i
    }), (l = (s = this.config).onVisitorInfo) == null || l.call(s, t, i);
  }
  // 向 iframe 发送消息
  sendMessageToIframe(e) {
    var i;
    const t = (i = this.window) == null ? void 0 : i.querySelector("iframe");
    t && t.contentWindow && t.contentWindow.postMessage(e, "*");
  }
  showChat(e) {
    var t, i;
    if (e && (this.config = {
      ...this.config,
      ...e
    }, this.window && (document.body.removeChild(this.window), this.window = null)), this.window || this.createChatWindow(), this.window) {
      const n = window.innerWidth <= 768;
      if (this.window.style.display = "block", this.config.forceRefresh) {
        const o = this.window.querySelector("iframe");
        o && (o.src = this.generateChatUrl());
      }
      if (this.setupResizeListener(), n && this.window && (this.window.style.transform = "translateY(100%)", requestAnimationFrame(() => {
        this.window && (this.window.style.transform = "translateY(0)");
      })), this.isVisible = !0, this.bubble) {
        this.bubble.style.display = "none";
        const o = this.bubble.messageElement;
        o instanceof HTMLElement && (o.style.display = "none");
      }
    }
    this.hideInviteDialog(), (i = (t = this.config).onShowChat) == null || i.call(t);
  }
  hideChat() {
    var e, t, i, n, o;
    if (this.window) {
      if (window.innerWidth <= 768 ? (this.window.style.transform = "translateY(100%)", setTimeout(() => {
        this.window && (this.window.style.display = "none");
      }, ((e = this.config.animation) == null ? void 0 : e.duration) || 300)) : this.window.style.display = "none", this.isVisible = !1, this.bubble) {
        this.bubble.style.display = ((t = this.config.buttonConfig) == null ? void 0 : t.show) === !1 ? "none" : "inline-flex";
        const l = this.bubble.messageElement;
        l instanceof HTMLElement && (l.style.display = ((i = this.config.bubbleConfig) == null ? void 0 : i.show) === !1 ? "none" : "block");
      }
      (o = (n = this.config).onHideChat) == null || o.call(n);
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
      var s, l;
      if (!this.window || !this.isVisible) return;
      const i = window.innerWidth <= 768, n = window.innerWidth, o = window.innerHeight;
      if (i)
        Object.assign(this.window.style, {
          left: "0",
          bottom: "0",
          width: "100%",
          height: "90vh",
          borderTopLeftRadius: "12px",
          borderTopRightRadius: "12px",
          borderBottomLeftRadius: "0",
          borderBottomRightRadius: "0"
        });
      else {
        let c = this.windowState === "maximized" ? n : Math.min(
          ((s = this.config.window) == null ? void 0 : s.width) || n * 0.9,
          n * 0.9
        ), r = this.windowState === "maximized" ? o : Math.min(
          ((l = this.config.window) == null ? void 0 : l.height) || o * 0.9,
          o * 0.9
        );
        const a = this.config.placement === "bottom-right" ? this.config.marginSide : void 0, d = this.config.placement === "bottom-left" ? this.config.marginSide : void 0;
        Object.assign(this.window.style, {
          width: `${c}px`,
          height: `${r}px`,
          right: a ? `${a}px` : "auto",
          left: d ? `${d}px` : "auto",
          bottom: `${this.config.marginBottom}px`,
          borderRadius: this.windowState === "maximized" ? "0" : "12px"
        });
      }
    };
    let t;
    window.addEventListener("resize", () => {
      clearTimeout(t), t = window.setTimeout(e, 100);
    }), e();
  }
  destroy() {
    var t;
    const e = (t = this.bubble) == null ? void 0 : t.parentElement;
    e && document.body.contains(e) && (document.body.removeChild(e), this.bubble = null), this.window && document.body.contains(this.window) && (document.body.removeChild(this.window), this.window = null), window.removeEventListener("resize", this.setupResizeListener.bind(this)), this.loopTimer && (window.clearTimeout(this.loopTimer), this.loopTimer = null), this.inviteDialog && document.body.contains(this.inviteDialog) && (document.body.removeChild(this.inviteDialog), this.inviteDialog = null), this.contextMenu && document.body.contains(this.contextMenu) && (document.body.removeChild(this.contextMenu), this.contextMenu = null), this.hideTimeout && (clearTimeout(this.hideTimeout), this.hideTimeout = null), this.selectionDebounceTimer && (clearTimeout(this.selectionDebounceTimer), this.selectionDebounceTimer = null), this.destroyFeedbackFeature();
  }
  createInviteDialog() {
    var l, c, r, a, d, g;
    if (this.inviteDialog && document.body.contains(this.inviteDialog)) {
      console.log("createInviteDialog: 邀请框已存在，不重复创建");
      return;
    }
    this.inviteDialog && !document.body.contains(this.inviteDialog) && (console.log("createInviteDialog: 清理已存在的 inviteDialog 引用"), this.inviteDialog = null);
    const e = ((l = this.config.theme) == null ? void 0 : l.mode) === "dark";
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
    `, (c = this.config.inviteConfig) != null && c.icon) {
      const h = document.createElement("div");
      h.style.cssText = `
        font-size: 32px;
        margin-bottom: 12px;
        color: ${e ? "#e5e7eb" : "#333"};
      `, h.textContent = this.config.inviteConfig.icon, this.inviteDialog.appendChild(h);
    }
    const t = document.createElement("div");
    t.style.cssText = `
      margin-bottom: 16px;
      color: ${e ? "#e5e7eb" : "#333"};
    `, t.textContent = ((r = this.config.inviteConfig) == null ? void 0 : r.text) || "需要帮助吗？点击开始对话", this.inviteDialog.appendChild(t);
    const i = document.createElement("div");
    i.style.cssText = `
      display: flex;
      gap: 10px;
      justify-content: center;
    `;
    const n = document.createElement("button");
    n.textContent = ((a = this.config.inviteConfig) == null ? void 0 : a.acceptText) || "开始对话";
    const o = ((d = this.config.theme) == null ? void 0 : d.backgroundColor) || (e ? "#3B82F6" : "#0066FF");
    n.style.cssText = `
      padding: 8px 16px;
      background: ${o};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, n.onclick = () => {
      var h, b;
      this.hideInviteDialog(), this.showChat(), (b = (h = this.config.inviteConfig) == null ? void 0 : h.onAccept) == null || b.call(h);
    };
    const s = document.createElement("button");
    s.textContent = ((g = this.config.inviteConfig) == null ? void 0 : g.rejectText) || "稍后再说", s.style.cssText = `
      padding: 8px 16px;
      background: ${e ? "#374151" : "#f5f5f5"};
      color: ${e ? "#d1d5db" : "#666"};
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, s.onclick = () => {
      var h, b;
      this.hideInviteDialog(), (b = (h = this.config.inviteConfig) == null ? void 0 : h.onReject) == null || b.call(h), this.handleInviteLoop();
    }, i.appendChild(n), i.appendChild(s), this.inviteDialog.appendChild(i), document.body.appendChild(this.inviteDialog);
  }
  showInviteDialog() {
    var e, t;
    this.inviteDialog && (this.inviteDialog.style.display = "block", (t = (e = this.config.inviteConfig) == null ? void 0 : e.onOpen) == null || t.call(e));
  }
  hideInviteDialog() {
    var e, t;
    console.log("hideInviteDialog before"), this.inviteDialog && (this.inviteDialog.style.display = "none", (t = (e = this.config.inviteConfig) == null ? void 0 : e.onClose) == null || t.call(e), console.log("hideInviteDialog after"));
  }
  handleInviteLoop() {
    const {
      loop: e,
      loopDelay: t = 3e3,
      loopCount: i = 1 / 0
    } = this.config.inviteConfig || {};
    !e || this.loopCount >= i - 1 || (this.loopTimer && window.clearTimeout(this.loopTimer), this.loopTimer = window.setTimeout(() => {
      this.loopCount++, this.showInviteDialog();
    }, t));
  }
  showButton() {
    if (this.bubble && this.bubble.style.display !== "none") {
      console.log("showButton: 按钮已经显示，无需重复显示");
      return;
    }
    this.bubble ? (this.bubble.style.display = "inline-flex", console.log("showButton: 按钮已显示")) : console.log("showButton: bubble 不存在，需要先创建");
  }
  hideButton() {
    this.bubble && (this.bubble.style.display = "none");
  }
  showBubble() {
    if (this.bubble) {
      const e = this.bubble.messageElement;
      if (e instanceof HTMLElement) {
        if (e.style.display !== "none" && e.style.opacity !== "0") {
          console.log("showBubble: 气泡已经显示，无需重复显示");
          return;
        }
        e.style.display = "block", setTimeout(() => {
          e.style.opacity = "1", e.style.transform = "translateY(0)";
        }, 100), console.log("showBubble: 气泡已显示");
      } else
        console.log("showBubble: messageElement 不存在");
    } else
      console.log("showBubble: bubble 不存在");
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
    e.forEach((t, i) => {
      const n = document.createElement("div");
      if (n.style.cssText = `
        padding: 8px 16px;
        cursor: pointer;
        color: #333;
        font-size: 14px;
        
        &:hover {
          background: #f5f5f5;
        }
      `, n.textContent = t.text, n.onclick = () => {
        t.onClick(), this.hideContextMenu();
      }, this.contextMenu && this.contextMenu.appendChild(n), i < e.length - 1) {
        const o = document.createElement("div");
        o.style.cssText = `
          height: 1px;
          background: #eee;
          margin: 4px 0;
        `, this.contextMenu && this.contextMenu.appendChild(o);
      }
    }), document.body.appendChild(this.contextMenu);
  }
  showContextMenu(e) {
    if (e.preventDefault(), this.contextMenu || this.createContextMenu(), this.contextMenu) {
      this.contextMenu.style.visibility = "hidden", this.contextMenu.style.display = "block";
      const t = this.contextMenu.offsetWidth, i = this.contextMenu.offsetHeight;
      let n = e.clientX, o = e.clientY;
      n + t > window.innerWidth && (n = n - t), o + i > window.innerHeight && (o = o - i), n = Math.max(0, n), o = Math.max(0, o), this.contextMenu.style.left = `${n}px`, this.contextMenu.style.top = `${o}px`, this.contextMenu.style.visibility = "visible";
    }
  }
  hideContextMenu() {
    this.contextMenu && (this.contextMenu.style.display = "none");
  }
  togglePlacement() {
    var t, i;
    if (!this.bubble) return;
    this.config.placement = this.config.placement === "bottom-left" ? "bottom-right" : "bottom-left";
    const e = this.bubble.parentElement;
    e && (e.style.left = this.config.placement === "bottom-left" ? `${this.config.marginSide}px` : "auto", e.style.right = this.config.placement === "bottom-right" ? `${this.config.marginSide}px` : "auto", e.style.alignItems = this.config.placement === "bottom-left" ? "flex-start" : "flex-end", this.window && this.isVisible && (this.window.style.left = this.config.placement === "bottom-left" ? `${this.config.marginSide}px` : "auto", this.window.style.right = this.config.placement === "bottom-right" ? `${this.config.marginSide}px` : "auto"), (i = (t = this.config).onConfigChange) == null || i.call(t, { placement: this.config.placement }));
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
    var e, t;
    if (this.config.isDebug && (console.log("BytedeskWeb: 初始化文档反馈功能开始"), console.log("BytedeskWeb: feedbackConfig:", this.config.feedbackConfig), console.log("BytedeskWeb: feedbackConfig.enabled:", (e = this.config.feedbackConfig) == null ? void 0 : e.enabled)), !((t = this.config.feedbackConfig) != null && t.enabled)) {
      this.config.isDebug && console.log("BytedeskWeb: 文档反馈功能未启用，退出初始化");
      return;
    }
    (this.feedbackTooltip || this.feedbackDialog) && (this.config.isDebug && console.log("BytedeskWeb: 反馈功能已存在，先销毁再重新创建"), this.destroyFeedbackFeature()), this.config.feedbackConfig.trigger === "selection" || this.config.feedbackConfig.trigger === "both" ? (this.config.isDebug && (console.log("BytedeskWeb: 触发器匹配，设置文本选择监听器"), console.log("BytedeskWeb: 触发器类型:", this.config.feedbackConfig.trigger)), this.setupTextSelectionListener()) : this.config.isDebug && (console.log("BytedeskWeb: 触发器不匹配，跳过文本选择监听器"), console.log("BytedeskWeb: 触发器类型:", this.config.feedbackConfig.trigger)), this.config.isDebug && console.log("BytedeskWeb: 开始创建反馈提示框"), this.createFeedbackTooltip(), this.config.isDebug && console.log("BytedeskWeb: 开始创建反馈对话框"), this.createFeedbackDialog(), this.config.isDebug && (console.log("BytedeskWeb: 文档反馈功能初始化完成"), console.log("BytedeskWeb: 反馈提示框存在:", !!this.feedbackTooltip), console.log("BytedeskWeb: 反馈对话框存在:", !!this.feedbackDialog));
  }
  /**
   * 设置文本选择监听器
   */
  setupTextSelectionListener() {
    this.config.isDebug && console.log("BytedeskWeb: 设置文本选择监听器"), document.addEventListener("mouseup", (e) => {
      this.lastMouseEvent = e, this.config.isDebug && console.log("BytedeskWeb: mouseup事件触发", e), this.handleTextSelectionWithDebounce(e);
    }, { capture: !0, passive: !0 }), document.addEventListener("selectionchange", () => {
      if (!this.lastMouseEvent) {
        this.config.isDebug && console.log("BytedeskWeb: selectionchange事件触发（无鼠标事件）");
        const e = new MouseEvent("mouseup", {
          clientX: window.innerWidth / 2,
          clientY: window.innerHeight / 2
        });
        this.handleTextSelectionWithDebounce(e);
      }
    }), document.addEventListener("keyup", (e) => {
      (e.shiftKey || e.ctrlKey || e.metaKey) && (this.config.isDebug && console.log("BytedeskWeb: keyup事件触发（带修饰键）", e), this.handleTextSelectionWithDebounce(e));
    }, { capture: !0, passive: !0 }), document.addEventListener("click", (e) => {
      const t = e.target;
      t != null && t.closest("[data-bytedesk-feedback]") || this.hideFeedbackTooltip();
    }), this.config.isDebug && console.log("BytedeskWeb: 文本选择监听器设置完成");
  }
  /**
   * 带防抖的文本选择处理
   */
  handleTextSelectionWithDebounce(e) {
    this.config.isDebug && console.log("BytedeskWeb: handleTextSelectionWithDebounce被调用 - 防抖机制生效"), this.selectionDebounceTimer && (clearTimeout(this.selectionDebounceTimer), this.config.isDebug && console.log("BytedeskWeb: 清除之前的防抖定时器")), this.selectionDebounceTimer = setTimeout(() => {
      this.config.isDebug && console.log("BytedeskWeb: 防抖延迟结束，开始处理文本选择"), this.handleTextSelection(e);
    }, 200);
  }
  /**
   * 处理文本选择
   */
  handleTextSelection(e) {
    var n, o;
    this.config.isDebug && console.log("BytedeskWeb: handleTextSelection被调用");
    const t = window.getSelection();
    if (this.config.isDebug && (console.log("BytedeskWeb: window.getSelection()结果:", t), console.log("BytedeskWeb: selection.rangeCount:", t == null ? void 0 : t.rangeCount)), !t || t.rangeCount === 0) {
      this.config.isDebug && console.log("BytedeskWeb: 没有选择或范围为0，隐藏提示"), this.hideFeedbackTooltip();
      return;
    }
    const i = t.toString().trim();
    if (this.config.isDebug && (console.log("BytedeskWeb: 检测到文本选择:", `"${i}"`), console.log("BytedeskWeb: 选中文本长度:", i.length)), i === this.lastSelectionText && this.isTooltipVisible) {
      this.config.isDebug && console.log("BytedeskWeb: 文本选择未变化且提示框已显示，跳过处理");
      return;
    }
    if (i.length === 0) {
      this.config.isDebug && console.log("BytedeskWeb: 选中文本为空，隐藏提示"), this.hideFeedbackTooltip();
      return;
    }
    if (i.length < 3) {
      this.config.isDebug && console.log("BytedeskWeb: 选中文本太短，忽略:", `"${i}"`), this.hideFeedbackTooltip();
      return;
    }
    this.selectedText = i, this.lastSelectionText = i;
    try {
      const s = t.getRangeAt(0);
      this.lastSelectionRect = s.getBoundingClientRect(), this.config.isDebug && console.log("BytedeskWeb: 存储选中文本位置:", this.lastSelectionRect);
    } catch (s) {
      this.config.isDebug && console.warn("BytedeskWeb: 获取选中文本位置失败:", s), this.lastSelectionRect = null;
    }
    this.config.isDebug && console.log("BytedeskWeb: 设置selectedText为:", `"${i}"`), (n = this.config.feedbackConfig) != null && n.showOnSelection ? (this.config.isDebug && console.log("BytedeskWeb: 配置允许显示选择提示，调用showFeedbackTooltip"), this.showFeedbackTooltip(this.lastMouseEvent || void 0)) : this.config.isDebug && (console.log("BytedeskWeb: 配置不允许显示选择提示"), console.log("BytedeskWeb: feedbackConfig.showOnSelection:", (o = this.config.feedbackConfig) == null ? void 0 : o.showOnSelection));
  }
  /**
   * 创建反馈提示框
   */
  createFeedbackTooltip() {
    var t;
    if (this.config.isDebug && console.log("BytedeskWeb: createFeedbackTooltip被调用"), this.feedbackTooltip && document.body.contains(this.feedbackTooltip)) {
      this.config.isDebug && console.log("BytedeskWeb: 反馈提示框已存在且在DOM中，跳过创建");
      return;
    }
    this.feedbackTooltip && !document.body.contains(this.feedbackTooltip) && (this.config.isDebug && console.log("BytedeskWeb: 提示框变量存在但不在DOM中，重置变量"), this.feedbackTooltip = null), this.feedbackTooltip = document.createElement("div"), this.feedbackTooltip.setAttribute("data-bytedesk-feedback", "tooltip"), this.feedbackTooltip.style.cssText = `
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
    const e = ((t = this.config.feedbackConfig) == null ? void 0 : t.selectionText) || "文档反馈";
    this.config.isDebug && console.log("BytedeskWeb: 提示框文本:", e), this.feedbackTooltip.innerHTML = `
      <span style="margin-right: 4px;">📝</span>
      ${e}
    `, this.feedbackTooltip.addEventListener("click", async (i) => {
      this.config.isDebug && (console.log("BytedeskWeb: 反馈提示框被点击"), console.log("BytedeskWeb: 点击时选中文字:", this.selectedText)), i.stopPropagation(), i.preventDefault();
      try {
        await this.showFeedbackDialog(), this.config.isDebug && console.log("BytedeskWeb: 对话框显示完成，现在隐藏提示框"), this.hideFeedbackTooltip();
      } catch (n) {
        this.config.isDebug && console.error("BytedeskWeb: 显示对话框时出错:", n);
      }
    }), document.body.appendChild(this.feedbackTooltip), this.config.isDebug && (console.log("BytedeskWeb: 反馈提示框已创建并添加到页面"), console.log("BytedeskWeb: 提示框元素:", this.feedbackTooltip));
  }
  /**
   * 显示反馈提示框
   */
  showFeedbackTooltip(e) {
    this.config.isDebug && (console.log("BytedeskWeb: showFeedbackTooltip被调用"), console.log("BytedeskWeb: feedbackTooltip存在:", !!this.feedbackTooltip), console.log("BytedeskWeb: selectedText存在:", !!this.selectedText));
    const t = this.feedbackTooltip && document.body.contains(this.feedbackTooltip);
    if (this.config.isDebug && console.log("BytedeskWeb: feedbackTooltip在DOM中:", t), (!this.feedbackTooltip || !t) && (this.config.isDebug && console.log("BytedeskWeb: 提示框不存在或已从DOM中移除，重新创建"), this.createFeedbackTooltip()), !this.feedbackTooltip || !this.selectedText) {
      this.config.isDebug && console.log("BytedeskWeb: 提示框或选中文本不存在，退出显示");
      return;
    }
    const i = window.getSelection();
    if (!i || i.rangeCount === 0) {
      this.config.isDebug && console.log("BytedeskWeb: 无有效选择，无法计算位置");
      return;
    }
    const n = i.getRangeAt(0);
    let o;
    try {
      const p = document.createRange();
      p.setStart(n.startContainer, n.startOffset);
      let x = n.startOffset;
      const w = n.startContainer.textContent || "";
      if (n.startContainer.nodeType === Node.TEXT_NODE) {
        for (; x < Math.min(w.length, n.endOffset); ) {
          const C = document.createRange();
          C.setStart(n.startContainer, n.startOffset), C.setEnd(n.startContainer, x + 1);
          const S = C.getBoundingClientRect(), D = p.getBoundingClientRect();
          if (Math.abs(S.top - D.top) > 5)
            break;
          x++;
        }
        p.setEnd(n.startContainer, Math.max(x, n.startOffset + 1)), o = p.getBoundingClientRect();
      } else
        o = n.getBoundingClientRect();
    } catch (p) {
      this.config.isDebug && console.log("BytedeskWeb: 获取第一行位置失败，使用整个选择区域:", p), o = n.getBoundingClientRect();
    }
    this.config.isDebug && console.log("BytedeskWeb: 选中文本第一行位置信息:", {
      left: o.left,
      top: o.top,
      right: o.right,
      bottom: o.bottom,
      width: o.width,
      height: o.height
    });
    const s = 120, l = 40, c = 15, r = 5;
    let a = o.left + r, d = o.top - l - c;
    const g = window.innerWidth, h = window.innerHeight, b = window.scrollX, k = window.scrollY;
    a < 10 && (a = 10), a + s > g - 10 && (a = g - s - 10), d < k + 10 && (d = o.bottom + c, this.config.isDebug && console.log("BytedeskWeb: 上方空间不足，调整为显示在选中文字第一行下方")), a += b, d += k, this.config.isDebug && console.log("BytedeskWeb: 最终提示框位置:", {
      x: a,
      y: d,
      说明: "显示在选中文字第一行左上角上方，增加间距避免遮挡",
      verticalOffset: c,
      horizontalOffset: r,
      选中区域: o,
      视口信息: { viewportWidth: g, viewportHeight: h, scrollX: b, scrollY: k }
    }), this.feedbackTooltip.style.position = "absolute", this.feedbackTooltip.style.left = a + "px", this.feedbackTooltip.style.top = d + "px", this.feedbackTooltip.style.display = "block", this.feedbackTooltip.style.visibility = "visible", this.feedbackTooltip.style.opacity = "0", this.feedbackTooltip.style.zIndex = "999999", this.config.isDebug && console.log("BytedeskWeb: 提示框位置已设置，样式:", {
      position: this.feedbackTooltip.style.position,
      left: this.feedbackTooltip.style.left,
      top: this.feedbackTooltip.style.top,
      display: this.feedbackTooltip.style.display,
      visibility: this.feedbackTooltip.style.visibility,
      opacity: this.feedbackTooltip.style.opacity,
      zIndex: this.feedbackTooltip.style.zIndex
    }), this.isTooltipVisible = !0, setTimeout(() => {
      this.feedbackTooltip && this.isTooltipVisible && (this.feedbackTooltip.style.opacity = "1", this.config.isDebug && console.log("BytedeskWeb: 提示框透明度设置为1，应该可见了"));
    }, 10);
  }
  /**
   * 隐藏反馈提示框
   */
  hideFeedbackTooltip() {
    const e = this.feedbackTooltip && document.body.contains(this.feedbackTooltip);
    if (this.config.isDebug && (console.log("BytedeskWeb: hideFeedbackTooltip被调用"), console.log("BytedeskWeb: feedbackTooltip存在:", !!this.feedbackTooltip), console.log("BytedeskWeb: feedbackTooltip在DOM中:", e)), !this.feedbackTooltip || !e) {
      this.isTooltipVisible = !1, this.lastSelectionText = "", this.config.isDebug && console.log("BytedeskWeb: 提示框不存在或不在DOM中，仅重置状态");
      return;
    }
    this.isTooltipVisible = !1, this.lastSelectionText = "", this.feedbackTooltip.style.opacity = "0", setTimeout(() => {
      this.feedbackTooltip && document.body.contains(this.feedbackTooltip) && !this.isTooltipVisible ? (this.feedbackTooltip.style.display = "none", this.feedbackTooltip.style.visibility = "hidden", this.config.isDebug && console.log("BytedeskWeb: 提示框已隐藏")) : this.config.isDebug && this.isTooltipVisible && console.log("BytedeskWeb: 跳过隐藏操作，提示框状态已改变为可见");
    }, 100);
  }
  /**
   * 创建反馈对话框
   */
  createFeedbackDialog() {
    var t, i, n, o, s, l, c, r;
    if (this.config.isDebug && console.log("BytedeskWeb: createFeedbackDialog被调用"), this.feedbackDialog && document.body.contains(this.feedbackDialog)) {
      this.config.isDebug && console.log("BytedeskWeb: 反馈对话框已存在且在DOM中，跳过创建");
      return;
    }
    this.feedbackDialog && !document.body.contains(this.feedbackDialog) && (this.config.isDebug && console.log("BytedeskWeb: 对话框变量存在但不在DOM中，重置变量"), this.feedbackDialog = null), this.feedbackDialog = document.createElement("div"), this.feedbackDialog.setAttribute("data-bytedesk-feedback", "dialog"), this.feedbackDialog.style.cssText = `
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
          ${((t = this.config.feedbackConfig) == null ? void 0 : t.dialogTitle) || "提交意见反馈"}
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

      ${(i = this.config.feedbackConfig) != null && i.categoryNames && this.config.feedbackConfig.categoryNames.length > 0 ? `
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #333;">
          <span style="color: #ff4d4f;">*</span> ${((n = this.config.feedbackConfig) == null ? void 0 : n.typesSectionTitle) || "问题类型"} ${((o = this.config.feedbackConfig) == null ? void 0 : o.typesDescription) || "（多选）"}
        </label>
        <div id="bytedesk-feedback-types" style="
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 12px;
          margin-bottom: 8px;
        ">
          ${this.config.feedbackConfig.categoryNames.map((a) => `
            <label style="
              display: flex;
              align-items: flex-start;
              gap: 8px;
              cursor: pointer;
              padding: 8px;
              border-radius: 4px;
              transition: background-color 0.2s;
            " onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
              <input type="checkbox" name="feedback-type" value="${a}" style="
                margin: 2px 0 0 0;
                cursor: pointer;
              ">
              <span style="
                font-size: 14px;
                line-height: 1.4;
                color: #333;
                flex: 1;
              ">${a}</span>
            </label>
          `).join("")}
        </div>
      </div>
      ` : ""}

      ${((s = this.config.feedbackConfig) == null ? void 0 : s.submitScreenshot) !== !1 ? `
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
        <textarea id="bytedesk-feedback-text" placeholder="${((l = this.config.feedbackConfig) == null ? void 0 : l.placeholder) || "请详细描述您的问题或优化建议"}" style="
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
        ">${((c = this.config.feedbackConfig) == null ? void 0 : c.cancelText) || "取消"}</button>
        <button type="button" data-action="submit" style="
          background: #2e88ff;
          color: white;
          border: none;
          padding: 10px 20px;
          border-radius: 6px;
          cursor: pointer;
          font-size: 14px;
          font-family: inherit;
        ">${((r = this.config.feedbackConfig) == null ? void 0 : r.submitText) || "提交反馈"}</button>
      </div>
    `, e.addEventListener("click", (a) => {
      var h, b;
      switch (a.target.getAttribute("data-action")) {
        case "close":
        case "cancel":
          this.hideFeedbackDialog(), (b = (h = this.config.feedbackConfig) == null ? void 0 : h.onCancel) == null || b.call(h);
          break;
        case "submit":
          this.submitFeedback();
          break;
      }
    }), this.feedbackDialog.appendChild(e), this.feedbackDialog.addEventListener("click", (a) => {
      var d, g;
      a.target === this.feedbackDialog && (this.hideFeedbackDialog(), (g = (d = this.config.feedbackConfig) == null ? void 0 : d.onCancel) == null || g.call(d));
    }), document.addEventListener("keydown", (a) => {
      var d, g, h;
      a.key === "Escape" && ((d = this.feedbackDialog) == null ? void 0 : d.style.display) === "flex" && (this.hideFeedbackDialog(), (h = (g = this.config.feedbackConfig) == null ? void 0 : g.onCancel) == null || h.call(g));
    }), document.body.appendChild(this.feedbackDialog);
  }
  /**
   * 显示反馈对话框
   */
  async showFeedbackDialog() {
    this.config.isDebug && (console.log("BytedeskWeb: showFeedbackDialog被调用"), console.log("BytedeskWeb: feedbackDialog存在:", !!this.feedbackDialog));
    const e = this.feedbackDialog && document.body.contains(this.feedbackDialog);
    if (this.config.isDebug && console.log("BytedeskWeb: feedbackDialog在DOM中:", e), (!this.feedbackDialog || !e) && (this.config.isDebug && console.log("BytedeskWeb: 对话框不存在或已从DOM中移除，重新创建"), this.createFeedbackDialog()), !this.feedbackDialog) {
      this.config.isDebug && console.log("BytedeskWeb: 对话框创建失败，退出显示");
      return;
    }
    this.config.isDebug && console.log("BytedeskWeb: 开始填充对话框内容");
    const t = this.feedbackDialog.querySelector("#bytedesk-selected-text");
    t && (t.textContent = this.selectedText || "", this.config.isDebug && console.log("BytedeskWeb: 已填充选中文字:", this.selectedText));
    const i = this.feedbackDialog.querySelector("#bytedesk-feedback-text");
    i && (i.value = ""), this.feedbackDialog.style.display = "flex", this.config.isDebug && (console.log("BytedeskWeb: 对话框已设置为显示状态"), console.log("BytedeskWeb: 对话框样式:", {
      display: this.feedbackDialog.style.display,
      visibility: this.feedbackDialog.style.visibility,
      zIndex: this.feedbackDialog.style.zIndex
    }));
    try {
      await this.generateScreenshotPreview(), this.config.isDebug && console.log("BytedeskWeb: 截图预览生成完成");
    } catch (n) {
      this.config.isDebug && console.error("BytedeskWeb: 截图预览生成失败:", n);
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
      let t;
      const i = (e = this.feedbackDialog) == null ? void 0 : e.screenshotCanvas;
      if (i)
        this.config.isDebug && console.log("BytedeskWeb: 使用已生成的截图canvas"), t = i;
      else {
        const n = await this.loadHtml2Canvas();
        if (!n)
          return this.config.isDebug && console.log("BytedeskWeb: html2canvas加载失败，跳过截图"), null;
        this.config.isDebug && console.log("BytedeskWeb: 重新生成截图");
        const o = this.calculateScreenshotArea();
        t = await n(document.body, {
          height: o.height,
          width: o.width,
          x: o.x,
          y: o.y,
          useCORS: !0,
          allowTaint: !0,
          backgroundColor: "#ffffff",
          scale: 1,
          ignoreElements: (s) => s.hasAttribute("data-bytedesk-feedback") || s.closest("[data-bytedesk-feedback]") !== null
        });
      }
      return new Promise((n) => {
        t.toBlob(async (o) => {
          var s;
          if (!o) {
            console.error("无法生成截图blob"), n(null);
            return;
          }
          try {
            const l = `screenshot_${Date.now()}.jpg`, c = new File([o], l, { type: "image/jpeg" });
            this.config.isDebug && console.log("BytedeskWeb: 截图生成成功，文件大小:", Math.round(o.size / 1024), "KB");
            const { uploadScreenshot: r } = await import("../../apis/upload/index.js"), a = await r(c, {
              orgUid: ((s = this.config.chatConfig) == null ? void 0 : s.org) || "",
              isDebug: this.config.isDebug
            });
            this.config.isDebug && console.log("BytedeskWeb: 截图上传成功，URL:", a), n(a);
          } catch (l) {
            console.error("截图上传失败:", l), n(null);
          }
        }, "image/jpeg", 0.8);
      });
    } catch (t) {
      return console.error("生成截图失败:", t), null;
    }
  }
  /**
   * 生成截图预览（不上传到服务器）
   */
  async generateScreenshotPreview() {
    var t;
    const e = (t = this.feedbackDialog) == null ? void 0 : t.querySelector("#bytedesk-screenshot-container");
    if (e)
      try {
        const i = await this.loadHtml2Canvas();
        if (!i) {
          e.innerHTML = `
          <div style="color: #999; text-align: center; padding: 20px; flex-direction: column; gap: 8px; display: flex; align-items: center;">
            <div style="font-size: 24px;">📷</div>
            <div>截图功能暂时不可用</div>
            <div style="font-size: 12px; color: #666;">网络连接问题或资源加载失败</div>
          </div>
        `;
          return;
        }
        e.innerHTML = "正在生成截图预览...", this.config.isDebug && console.log("BytedeskWeb: 开始生成截图预览");
        const n = this.calculateScreenshotArea(), o = await i(document.body, {
          height: n.height,
          width: n.width,
          x: n.x,
          y: n.y,
          useCORS: !0,
          allowTaint: !0,
          backgroundColor: "#ffffff",
          scale: 1,
          ignoreElements: (r) => r.hasAttribute("data-bytedesk-feedback") || r.closest("[data-bytedesk-feedback]") !== null
        }), s = document.createElement("img");
        s.src = o.toDataURL("image/jpeg", 0.8), s.style.cssText = `
        max-width: 100%;
        max-height: 200px;
        border-radius: 4px;
        border: 1px solid #ddd;
        cursor: pointer;
      `, s.onclick = () => {
          const r = document.createElement("img");
          r.src = s.src, r.style.cssText = `
          max-width: 90vw;
          max-height: 90vh;
          border-radius: 8px;
          box-shadow: 0 8px 32px rgba(0,0,0,0.3);
        `;
          const a = document.createElement("div");
          a.style.cssText = `
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
          const d = document.createElement("div");
          d.style.cssText = `
          position: absolute;
          top: 20px;
          right: 20px;
          color: white;
          font-size: 14px;
          background: rgba(0,0,0,0.6);
          padding: 8px 12px;
          border-radius: 4px;
          user-select: none;
        `, d.textContent = "点击任意位置关闭", a.appendChild(d), a.appendChild(r), a.onclick = () => document.body.removeChild(a), document.body.appendChild(a);
        };
        const l = document.createElement("div");
        l.style.cssText = `
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
      `, l.appendChild(s);
        const c = document.createElement("div");
        c.style.cssText = `
        font-size: 12px;
        color: #666;
        text-align: center;
      `, c.innerHTML = "点击图片可放大查看<br/>提交时将自动上传此截图", l.appendChild(c), e.innerHTML = "", e.appendChild(l), this.feedbackDialog.screenshotCanvas = o, this.config.isDebug && console.log("BytedeskWeb: 截图预览生成成功");
      } catch (i) {
        console.error("生成截图预览失败:", i), e.innerHTML = `
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
      let t = this.lastSelectionRect;
      if (!t) {
        const i = window.getSelection();
        i && i.rangeCount > 0 && (t = i.getRangeAt(0).getBoundingClientRect());
      }
      if (t && t.width > 0 && t.height > 0) {
        const i = window.pageXOffset || document.documentElement.scrollLeft, n = window.pageYOffset || document.documentElement.scrollTop, o = t.left + i, s = t.top + n, l = Math.min(800, window.innerWidth), c = Math.min(600, window.innerHeight);
        let r = o - l / 2, a = s - c / 2;
        const d = document.documentElement.scrollWidth, g = document.documentElement.scrollHeight;
        r = Math.max(0, Math.min(r, d - l)), a = Math.max(0, Math.min(a, g - c)), e = {
          height: c,
          width: l,
          x: r,
          y: a,
          scrollX: 0,
          scrollY: 0
        }, this.config.isDebug && console.log("BytedeskWeb: 选中文本截图区域:", {
          selectedRect: t,
          absolutePosition: { left: o, top: s },
          captureArea: { x: r, y: a, width: l, height: c },
          pageSize: { width: d, height: g }
        });
      } else if (this.lastMouseEvent) {
        const i = window.pageXOffset || document.documentElement.scrollLeft, n = window.pageYOffset || document.documentElement.scrollTop, o = this.lastMouseEvent.clientX + i, s = this.lastMouseEvent.clientY + n, l = Math.min(800, window.innerWidth), c = Math.min(600, window.innerHeight);
        let r = o - l / 2, a = s - c / 2;
        const d = document.documentElement.scrollWidth, g = document.documentElement.scrollHeight;
        r = Math.max(0, Math.min(r, d - l)), a = Math.max(0, Math.min(a, g - c)), e = {
          height: c,
          width: l,
          x: r,
          y: a,
          scrollX: 0,
          scrollY: 0
        }, this.config.isDebug && console.log("BytedeskWeb: 鼠标位置截图区域:", {
          mousePosition: { x: this.lastMouseEvent.clientX, y: this.lastMouseEvent.clientY },
          absolutePosition: { x: o, y: s },
          captureArea: { x: r, y: a, width: l, height: c }
        });
      }
    } catch (t) {
      this.config.isDebug && console.warn("BytedeskWeb: 计算截图区域失败，使用默认区域:", t);
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
      return this.config.isDebug && console.warn("html2canvas 加载失败:", e), null;
    }
  }
  /**
   * 从CDN加载html2canvas
   */
  async loadHtml2CanvasFromCDN() {
    return new Promise((e, t) => {
      if (window.html2canvas) {
        e(window.html2canvas);
        return;
      }
      const i = document.createElement("script");
      i.src = this.config.apiUrl + "/assets/js/html2canvas.min.js", i.onload = () => {
        window.html2canvas ? e(window.html2canvas) : t(new Error("html2canvas 加载失败"));
      }, i.onerror = () => {
        t(new Error("无法从CDN加载html2canvas"));
      }, document.head.appendChild(i);
    });
  }
  /**
   * 提交反馈
   */
  async submitFeedback() {
    var l, c, r, a, d, g, h;
    const e = (l = this.feedbackDialog) == null ? void 0 : l.querySelector("#bytedesk-feedback-text"), t = (e == null ? void 0 : e.value.trim()) || "";
    if (!t) {
      alert("请填写反馈内容"), e == null || e.focus();
      return;
    }
    const i = [], n = (c = this.feedbackDialog) == null ? void 0 : c.querySelectorAll('input[name="feedback-type"]:checked');
    if (n && n.forEach((b) => {
      i.push(b.value);
    }), (r = this.config.feedbackConfig) != null && r.requiredTypes && i.length === 0) {
      alert("请至少选择一个问题类型");
      return;
    }
    const o = (a = this.feedbackDialog) == null ? void 0 : a.querySelector(".bytedesk-feedback-submit"), s = (o == null ? void 0 : o.textContent) || "提交反馈";
    o && (o.disabled = !0, o.textContent = "提交中...", o.style.opacity = "0.6");
    try {
      const b = (d = this.feedbackDialog) == null ? void 0 : d.querySelector("#bytedesk-submit-screenshot"), k = (b == null ? void 0 : b.checked) !== !1;
      let p = [];
      if (k) {
        this.config.isDebug && console.log("BytedeskWeb: 开始生成和上传截图"), o && (o.textContent = "正在生成截图...");
        const w = await this.generateAndUploadScreenshot();
        w && (p.push(w), this.config.isDebug && console.log("BytedeskWeb: 截图上传成功:", w)), o && (o.textContent = "正在提交反馈...");
      }
      const x = {
        selectedText: this.selectedText,
        ...p.length > 0 && { images: p },
        // 将截图URL放入images数组
        content: t,
        url: window.location.href,
        title: document.title,
        userAgent: navigator.userAgent,
        visitorUid: localStorage.getItem("bytedesk_uid") || "",
        orgUid: ((g = this.config.chatConfig) == null ? void 0 : g.org) || "",
        ...i.length > 0 && { categoryNames: i.join(",") }
      };
      (h = this.config.feedbackConfig) != null && h.onSubmit ? this.config.feedbackConfig.onSubmit(x) : await this.submitFeedbackToServer(x), this.showFeedbackSuccess(), setTimeout(() => {
        this.hideFeedbackDialog();
      }, 2e3);
    } catch (b) {
      console.error("提交反馈失败:", b), alert("提交失败，请稍后重试");
    } finally {
      o && (o.disabled = !1, o.textContent = s, o.style.opacity = "1");
    }
  }
  /**
   * 提交反馈到服务器
   */
  async submitFeedbackToServer(e) {
    try {
      const { submitFeedback: t } = await import("../../apis/feedback/index.js"), i = await t(e);
      return this.config.isDebug && console.log("反馈提交响应:", i), i;
    } catch (t) {
      throw console.error("提交反馈到服务器失败:", t), t;
    }
  }
  /**
   * 显示反馈成功消息
   */
  showFeedbackSuccess() {
    var t;
    if (!this.feedbackDialog) return;
    const e = this.feedbackDialog.querySelector("div > div");
    e && (e.innerHTML = `
      <div style="text-align: center; padding: 40px 20px;">
        <div style="font-size: 48px; margin-bottom: 16px;">✅</div>
        <h3 style="margin: 0 0 12px 0; color: #28a745;">
          ${((t = this.config.feedbackConfig) == null ? void 0 : t.successMessage) || "反馈已提交，感谢您的意见！"}
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
    var t;
    if (!((t = this.config.feedbackConfig) != null && t.enabled)) {
      console.warn("文档反馈功能未启用");
      return;
    }
    e && (this.selectedText = e), this.showFeedbackDialog();
  }
  /**
   * 公共方法：重新初始化反馈功能
   */
  reinitFeedbackFeature() {
    this.config.isDebug && console.log("BytedeskWeb: 重新初始化反馈功能"), this.destroyFeedbackFeature(), this.initFeedbackFeature();
  }
  /**
   * 公共方法：强制初始化反馈功能（用于调试）
   */
  forceInitFeedbackFeature() {
    return console.log("BytedeskWeb: 强制初始化反馈功能被调用"), console.log("BytedeskWeb: 当前配置:", this.config.feedbackConfig), console.log("BytedeskWeb: isDebug:", this.config.isDebug), this.config.feedbackConfig || (console.log("BytedeskWeb: 创建默认反馈配置"), this.config.feedbackConfig = {
      enabled: !0,
      trigger: "selection",
      showOnSelection: !0,
      selectionText: "📝 文档反馈",
      dialogTitle: "提交意见反馈",
      placeholder: "请详细描述您发现的问题、改进建议或其他意见...",
      submitText: "提交反馈",
      cancelText: "取消",
      successMessage: "感谢您的反馈！我们会认真处理您的意见。"
    }), this.config.feedbackConfig.enabled || (console.log("BytedeskWeb: 启用反馈配置"), this.config.feedbackConfig.enabled = !0), console.log("BytedeskWeb: 销毁现有反馈功能"), this.destroyFeedbackFeature(), console.log("BytedeskWeb: 重新初始化反馈功能"), this.initFeedbackFeature(), console.log("BytedeskWeb: 强制初始化完成，检查结果:"), console.log("- showDocumentFeedback方法存在:", typeof this.showDocumentFeedback == "function"), console.log("- testTextSelection方法存在:", typeof this.testTextSelection == "function"), console.log("- 反馈提示框存在:", !!this.feedbackTooltip), console.log("- 反馈对话框存在:", !!this.feedbackDialog), console.log("- 反馈提示框DOM存在:", !!document.querySelector('[data-bytedesk-feedback="tooltip"]')), console.log("- 反馈对话框DOM存在:", !!document.querySelector('[data-bytedesk-feedback="dialog"]')), {
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
    this.config.isDebug && console.log("BytedeskWeb: 测试文本选择功能，模拟选中文字:", `"${e}"`), this.selectedText = e;
    try {
      const t = document.createElement("div");
      t.textContent = e, t.style.cssText = `
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
      `, document.body.appendChild(t);
      const i = document.createRange();
      i.selectNodeContents(t);
      const n = window.getSelection();
      n && (n.removeAllRanges(), n.addRange(i), this.config.isDebug && console.log("BytedeskWeb: 已创建模拟文本选择"), this.feedbackTooltip ? this.showFeedbackTooltip() : console.error("BytedeskWeb: 反馈提示框不存在，无法测试"), setTimeout(() => {
        n && n.removeAllRanges(), document.body.contains(t) && document.body.removeChild(t), this.hideFeedbackTooltip();
      }, 5e3));
    } catch (t) {
      console.error("BytedeskWeb: 创建测试选择失败:", t);
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
  K as default
};
