var O = Object.defineProperty;
var z = (k, t, e) => t in k ? O(k, t, { enumerable: !0, configurable: !0, writable: !0, value: e }) : k[t] = e;
var p = (k, t, e) => z(k, typeof t != "symbol" ? t + "" : t, e);
import { BYTEDESK_UID as I, BYTEDESK_VISITOR_UID as M, POST_MESSAGE_LOCALSTORAGE_RESPONSE as R, POST_MESSAGE_INVITE_VISITOR_REJECT as W, POST_MESSAGE_INVITE_VISITOR_ACCEPT as A, POST_MESSAGE_INVITE_VISITOR as H, POST_MESSAGE_RECEIVE_MESSAGE as N, POST_MESSAGE_MINIMIZE_WINDOW as j, POST_MESSAGE_MAXIMIZE_WINDOW as Y, POST_MESSAGE_CLOSE_CHAT_WINDOW as F } from "../../utils/constants/index.js";
class J {
  constructor(t) {
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
    // 添加请求状态管理
    p(this, "initVisitorPromise", null);
    p(this, "getUnreadMessageCountPromise", null);
    p(this, "clearUnreadMessagesPromise", null);
    this.config = {
      ...this.getDefaultConfig(),
      ...t
    }, this.setupApiUrl();
  }
  async setupApiUrl() {
    try {
      const { setApiUrl: t } = await import("../../apis/request/index.js"), e = this.config.apiUrl || "https://api.weiyuai.cn";
      t(e), this.config.isDebug && console.log("API URL 已设置为:", e);
    } catch (t) {
      console.error("设置API URL时出错:", t);
    }
  }
  getDefaultConfig() {
    return {
      isDebug: !1,
      // isPreload: false,
      forceRefresh: !1,
      baseUrl: "https://cdn.weiyuai.cn/chat",
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
      showSupport: !0,
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
    var t;
    await this._initVisitor(), await this._browseVisitor(), this.createBubble(), this.createInviteDialog(), this.setupMessageListener(), this.setupResizeListener(), this._getUnreadMessageCount(), this.config.autoPopup && setTimeout(() => {
      this.showChat();
    }, this.config.autoPopupDelay || 1e3), (t = this.config.inviteConfig) != null && t.show && setTimeout(() => {
      this.showInviteDialog();
    }, this.config.inviteConfig.delay || 3e3);
  }
  async _initVisitor() {
    var n, s, a, r;
    if (this.initVisitorPromise)
      return console.log("访客初始化请求正在进行中，返回现有Promise"), this.initVisitorPromise;
    const t = localStorage.getItem(I), e = localStorage.getItem(M);
    console.log("localUid: ", t), console.log("localVisitorUid: ", e);
    const o = ((n = this.config.chatConfig) == null ? void 0 : n.visitorUid) && e ? ((s = this.config.chatConfig) == null ? void 0 : s.visitorUid) === e : !0;
    return t && e && o ? (console.log("访客信息相同，直接返回本地访客信息"), (r = (a = this.config).onVisitorInfo) == null || r.call(a, t || "", e || ""), {
      uid: t,
      visitorUid: e
    }) : (console.log("开始创建访客初始化Promise"), this.initVisitorPromise = import("../../apis/visitor/index.js").then(
      async ({ initVisitor: u }) => {
        var h, l, b, d, f, x, T, U, y, D, C, $, _, V, c, v, S, w, m;
        try {
          const E = {
            uid: String(((h = this.config.chatConfig) == null ? void 0 : h.uid) || t || ""),
            visitorUid: String(
              ((l = this.config.chatConfig) == null ? void 0 : l.visitorUid) || e || ""
            ),
            orgUid: String(((b = this.config.chatConfig) == null ? void 0 : b.org) || ""),
            nickname: String(((d = this.config.chatConfig) == null ? void 0 : d.name) || ""),
            avatar: String(((f = this.config.chatConfig) == null ? void 0 : f.avatar) || ""),
            mobile: String(((x = this.config.chatConfig) == null ? void 0 : x.mobile) || ""),
            email: String(((T = this.config.chatConfig) == null ? void 0 : T.email) || ""),
            note: String(((U = this.config.chatConfig) == null ? void 0 : U.note) || ""),
            extra: typeof ((y = this.config.chatConfig) == null ? void 0 : y.extra) == "string" ? this.config.chatConfig.extra : JSON.stringify(((D = this.config.chatConfig) == null ? void 0 : D.extra) || {})
          }, g = await u(E);
          return console.log("访客初始化API响应:", g.data, E), ((C = g.data) == null ? void 0 : C.code) === 200 ? ((_ = ($ = g.data) == null ? void 0 : $.data) != null && _.uid && (localStorage.setItem(I, g.data.data.uid), console.log("已保存uid到localStorage:", g.data.data.uid)), (c = (V = g.data) == null ? void 0 : V.data) != null && c.visitorUid && (localStorage.setItem(
            M,
            g.data.data.visitorUid
          ), console.log(
            "已保存visitorUid到localStorage:",
            g.data.data.visitorUid
          )), (v = g.data) != null && v.data && (console.log("触发onVisitorInfo回调"), (w = (S = this.config).onVisitorInfo) == null || w.call(
            S,
            g.data.data.uid || "",
            g.data.data.visitorUid || ""
          )), g.data.data) : (console.error("访客初始化失败:", (m = g.data) == null ? void 0 : m.message), null);
        } catch (E) {
          return console.error("访客初始化出错:", E), null;
        } finally {
          console.log("访客初始化Promise完成，清除引用"), this.initVisitorPromise = null;
        }
      }
    ), this.initVisitorPromise);
  }
  // 获取当前页面浏览信息并发送到服务器
  async _browseVisitor() {
    var t, e, i, o;
    try {
      const n = window.location.href, s = document.title, a = document.referrer, r = navigator.userAgent, u = this.getBrowserInfo(r), h = this.getOSInfo(r), l = this.getDeviceInfo(r), b = `${screen.width}x${screen.height}`, d = new URLSearchParams(window.location.search), f = d.get("utm_source") || void 0, x = d.get("utm_medium") || void 0, T = d.get("utm_campaign") || void 0, U = localStorage.getItem(I), y = {
        url: n,
        title: s,
        referrer: a,
        userAgent: r,
        operatingSystem: h,
        browser: u,
        deviceType: l,
        screenResolution: b,
        utmSource: f,
        utmMedium: x,
        utmCampaign: T,
        status: "ONLINE",
        // 注意这里就是uid，不是visitorUid，使用访客系统生成uid
        visitorUid: String(
          ((t = this.config.chatConfig) == null ? void 0 : t.uid) || U || ""
        ),
        orgUid: ((e = this.config.chatConfig) == null ? void 0 : e.org) || ""
      };
      if (!y.visitorUid) {
        console.log("访客uid为空，跳过browse操作");
        return;
      }
      const { browse: D } = await import("../../apis/visitor/index.js"), C = await D(y);
      console.log("浏览记录发送结果:", C.data, y), ((i = C.data) == null ? void 0 : i.code) === 200 ? console.log("浏览记录发送成功") : console.error("浏览记录发送失败:", (o = C.data) == null ? void 0 : o.message);
    } catch (n) {
      console.error("发送浏览记录时出错:", n);
    }
  }
  // 获取浏览器信息
  getBrowserInfo(t) {
    return t.includes("Chrome") ? "Chrome" : t.includes("Firefox") ? "Firefox" : t.includes("Safari") ? "Safari" : t.includes("Edge") ? "Edge" : t.includes("Opera") ? "Opera" : "Unknown";
  }
  // 获取操作系统信息
  getOSInfo(t) {
    return t.includes("Windows") ? "Windows" : t.includes("Mac") ? "macOS" : t.includes("Linux") ? "Linux" : t.includes("Android") ? "Android" : t.includes("iOS") ? "iOS" : "Unknown";
  }
  // 获取设备信息
  getDeviceInfo(t) {
    return t.includes("Mobile") ? "Mobile" : t.includes("Tablet") ? "Tablet" : "Desktop";
  }
  async _getUnreadMessageCount() {
    return this.getUnreadMessageCountPromise ? (this.config.isDebug && console.log("获取未读消息数请求正在进行中，返回现有Promise"), this.getUnreadMessageCountPromise) : (this.getUnreadMessageCountPromise = import("../../apis/message/index.js").then(
      async ({ getUnreadMessageCount: t }) => {
        var e, i, o, n, s;
        try {
          const a = String(((e = this.config.chatConfig) == null ? void 0 : e.visitorUid) || ""), r = localStorage.getItem(I), u = localStorage.getItem(M), h = {
            uid: r || "",
            visitorUid: a || u || "",
            orgUid: ((i = this.config.chatConfig) == null ? void 0 : i.org) || ""
          };
          if (h.uid === "")
            return 0;
          const l = await t(h);
          return console.log("获取未读消息数:", l.data, h), ((o = l.data) == null ? void 0 : o.code) === 200 ? ((n = l == null ? void 0 : l.data) != null && n.data && ((s = l == null ? void 0 : l.data) == null ? void 0 : s.data) > 0 ? this.showUnreadBadge(l.data.data) : this.clearUnreadBadge(), l.data.data || 0) : 0;
        } catch (a) {
          return console.error("获取未读消息数出错:", a), 0;
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
  // 清除本地访客信息，强制重新初始化
  clearVisitorInfo() {
    localStorage.removeItem(I), localStorage.removeItem(M), this.config.isDebug && console.log("已清除本地访客信息");
  }
  // 强制重新初始化访客信息（忽略本地缓存）
  async forceInitVisitor() {
    return this.clearVisitorInfo(), this.initVisitorPromise = null, this._initVisitor();
  }
  // 显示未读消息数角标
  showUnreadBadge(t) {
    if (console.log("showUnreadBadge() 被调用，count:", t), !this.bubble) {
      console.log("showUnreadBadge: bubble 不存在");
      return;
    }
    let e = this.bubble.querySelector(
      ".bytedesk-unread-badge"
    );
    e ? console.log("showUnreadBadge: 更新现有角标") : (console.log("showUnreadBadge: 创建新的角标"), e = document.createElement("div"), e.className = "bytedesk-unread-badge", e.style.cssText = `
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
      `, this.bubble.appendChild(e)), e.textContent = t > 99 ? "99+" : t.toString(), console.log("showUnreadBadge: 角标数字已更新为", e.textContent);
  }
  // 清除未读消息数角标
  clearUnreadBadge() {
    if (console.log("clearUnreadBadge() 被调用"), !this.bubble) {
      console.log("clearUnreadBadge: bubble 不存在");
      return;
    }
    const t = this.bubble.querySelector(".bytedesk-unread-badge");
    t ? (console.log("clearUnreadBadge: 找到角标，正在移除"), t.remove()) : console.log("clearUnreadBadge: 未找到角标");
  }
  // 清空未读消息
  async clearUnreadMessages() {
    return this.clearUnreadMessagesPromise ? (this.config.isDebug && console.log("清空未读消息请求正在进行中，返回现有Promise"), this.clearUnreadMessagesPromise) : (this.clearUnreadMessagesPromise = import("../../apis/message/index.js").then(
      async ({ clearUnreadMessages: t }) => {
        var e, i;
        try {
          const o = String(((e = this.config.chatConfig) == null ? void 0 : e.visitorUid) || ""), n = localStorage.getItem(I), s = localStorage.getItem(M), a = {
            uid: n || "",
            visitorUid: o || s || "",
            orgUid: ((i = this.config.chatConfig) == null ? void 0 : i.org) || ""
          }, r = await t(a);
          return console.log("清空未读消息数:", r.data, a), r.data.code === 200 ? (console.log("清空未读消息数成功:", r.data), this.clearUnreadBadge(), r.data.data || 0) : (console.error("清空未读消息数失败:", r.data.message), 0);
        } catch (o) {
          return console.error("清空未读消息数出错:", o), 0;
        } finally {
          this.clearUnreadMessagesPromise = null;
        }
      }
    ), this.clearUnreadMessagesPromise);
  }
  createBubble() {
    var l, b, d, f, x, T, U, y, D, C, $, _, V;
    if (this.bubble && document.body.contains(this.bubble)) {
      console.log("气泡已存在，不重复创建");
      return;
    }
    const t = document.createElement("div");
    t.style.cssText = `
      position: fixed;
      ${this.config.placement === "bottom-left" ? "left" : "right"}: ${this.config.marginSide}px;
      bottom: ${this.config.marginBottom}px;
      display: flex;
      flex-direction: column;
      align-items: ${this.config.placement === "bottom-left" ? "flex-start" : "flex-end"};
      gap: 10px;
      z-index: 9999;
    `;
    let e = null;
    if ((l = this.config.bubbleConfig) != null && l.show) {
      e = document.createElement("div"), e.style.cssText = `
        background: ${((b = this.config.theme) == null ? void 0 : b.mode) === "dark" ? "#1f2937" : "white"};
        color: ${((d = this.config.theme) == null ? void 0 : d.mode) === "dark" ? "#e5e7eb" : "#1f2937"};
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
      const c = document.createElement("div");
      c.style.cssText = `
        display: flex;
        align-items: center;
        gap: 8px;
        flex-direction: ${this.config.placement === "bottom-left" ? "row" : "row-reverse"};
      `, c.setAttribute(
        "data-placement",
        this.config.placement || "bottom-right"
      );
      const v = document.createElement("span");
      v.textContent = ((f = this.config.bubbleConfig) == null ? void 0 : f.icon) || "", v.style.fontSize = "20px", c.appendChild(v);
      const S = document.createElement("div"), w = document.createElement("div");
      w.textContent = ((x = this.config.bubbleConfig) == null ? void 0 : x.title) || "", w.style.fontWeight = "bold", w.style.color = ((T = this.config.theme) == null ? void 0 : T.mode) === "dark" ? "#e5e7eb" : "#1f2937", w.style.marginBottom = "4px", w.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", S.appendChild(w);
      const m = document.createElement("div");
      m.textContent = ((U = this.config.bubbleConfig) == null ? void 0 : U.subtitle) || "", m.style.fontSize = "0.9em", m.style.color = ((y = this.config.theme) == null ? void 0 : y.mode) === "dark" ? "#9ca3af" : "#4b5563", m.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", S.appendChild(m), c.appendChild(S), e.appendChild(c);
      const E = document.createElement("div");
      E.style.cssText = `
        position: absolute;
        bottom: -6px;
        ${this.config.placement === "bottom-left" ? "left: 24px" : "right: 24px"};
        width: 12px;
        height: 12px;
        background: ${((D = this.config.theme) == null ? void 0 : D.mode) === "dark" ? "#1f2937" : "white"};
        transform: rotate(45deg);
        box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      `;
      const g = document.createElement("div");
      g.style.cssText = `
        position: absolute;
        bottom: 0;
        ${this.config.placement === "bottom-left" ? "left: 18px" : "right: 18px"};
        width: 24px;
        height: 12px;
        background: ${((C = this.config.theme) == null ? void 0 : C.mode) === "dark" ? "#1f2937" : "white"};
      `, e.appendChild(E), e.appendChild(g), t.appendChild(e), setTimeout(() => {
        e && (e.style.opacity = "1", e.style.transform = "translateY(0)");
      }, 500);
    }
    this.bubble = document.createElement("button");
    const i = this.config.buttonConfig || {}, o = i.width || 60, n = i.height || 60, s = Math.min(o, n) / 2, a = (($ = this.config.theme) == null ? void 0 : $.mode) === "dark", r = a ? "#3B82F6" : "#0066FF", u = ((_ = this.config.theme) == null ? void 0 : _.backgroundColor) || r;
    this.bubble.style.cssText = `
      background-color: ${u};
      width: ${o}px;
      height: ${n}px;
      border-radius: ${s}px;
      border: none;
      cursor: ${this.config.draggable ? "move" : "pointer"};
      display: ${i.show === !1 ? "none" : "flex"};
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(0, 0, 0, ${a ? "0.3" : "0.12"});
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
    `;
    const h = document.createElement("div");
    if (h.style.cssText = `
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    `, i.icon) {
      const c = document.createElement("span");
      c.textContent = i.icon, c.style.fontSize = `${n * 0.4}px`, h.appendChild(c);
    } else {
      const c = document.createElement("div");
      c.innerHTML = `
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
                fill="white"/>
        </svg>
      `, h.appendChild(c);
    }
    if (i.text) {
      const c = document.createElement("span");
      c.textContent = i.text, c.style.cssText = `
        color: ${((V = this.config.theme) == null ? void 0 : V.textColor) || "#ffffff"};
        font-size: ${n * 0.25}px;
        white-space: nowrap;
      `, h.appendChild(c);
    }
    if (this.bubble.appendChild(h), this.bubble.addEventListener("mouseenter", () => {
      this.bubble.style.transform = "scale(1.1)";
    }), this.bubble.addEventListener("mouseleave", () => {
      this.bubble.style.transform = "scale(1)";
    }), t.appendChild(this.bubble), this.config.draggable) {
      let c = 0, v = 0, S = 0, w = 0;
      this.bubble.addEventListener("mousedown", (m) => {
        m.button === 0 && (this.isDragging = !0, c = m.clientX, v = m.clientY, S = t.offsetLeft, w = t.offsetTop, t.style.transition = "none");
      }), document.addEventListener("mousemove", (m) => {
        if (!this.isDragging) return;
        m.preventDefault();
        const E = m.clientX - c, g = m.clientY - v, B = S + E, P = w + g, L = window.innerHeight - t.offsetHeight;
        B <= window.innerWidth / 2 ? (t.style.left = `${Math.max(0, B)}px`, t.style.right = "auto", t.style.alignItems = "flex-start", this.config.placement = "bottom-left") : (t.style.right = `${Math.max(
          0,
          window.innerWidth - B - t.offsetWidth
        )}px`, t.style.left = "auto", t.style.alignItems = "flex-end", this.config.placement = "bottom-right"), t.style.bottom = `${Math.min(
          Math.max(0, window.innerHeight - P - t.offsetHeight),
          L
        )}px`;
      }), document.addEventListener("mouseup", () => {
        this.isDragging && (this.isDragging = !1, t.style.transition = "all 0.3s ease", this.config.marginSide = parseInt(
          this.config.placement === "bottom-left" ? t.style.left : t.style.right
        ) || 20, this.config.marginBottom = parseInt(t.style.bottom || "20"));
      });
    }
    this.bubble.addEventListener("click", () => {
      if (!this.isDragging) {
        console.log("bubble click");
        const c = this.bubble.messageElement;
        c instanceof HTMLElement && (c.style.display = "none"), this.showChat();
      }
    }), this.bubble.messageElement = e, document.body.appendChild(t), this.bubble.addEventListener("contextmenu", (c) => {
      this.showContextMenu(c);
    }), document.addEventListener("click", () => {
      this.hideContextMenu();
    });
  }
  getSupportText() {
    var i;
    const t = ((i = this.config) == null ? void 0 : i.locale) || "zh-cn", e = {
      "zh-cn": "微语技术支持",
      "zh-tw": "微語技術支援",
      en: "Powered by Bytedesk",
      ja: "Bytedeskによる技術支援",
      ko: "Bytedesk 기술 지원"
    };
    return e[t] || e["zh-cn"];
  }
  createChatWindow() {
    var a, r, u, h, l, b, d;
    this.window = document.createElement("div");
    const t = window.innerWidth <= 768, e = window.innerWidth, i = window.innerHeight, o = Math.min(
      ((a = this.config.window) == null ? void 0 : a.width) || e * 0.9,
      e * 0.9
    ), n = Math.min(
      ((r = this.config.window) == null ? void 0 : r.height) || i * 0.9,
      i * 0.9
    );
    t ? this.window.style.cssText = `
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
        transition: all ${(u = this.config.animation) == null ? void 0 : u.duration}ms ${(h = this.config.animation) == null ? void 0 : h.type};
      ` : this.window.style.cssText = `
        position: fixed;
        ${this.config.placement === "bottom-right" ? "right" : "left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${o}px;
        height: ${n}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(l = this.config.animation) == null ? void 0 : l.duration}ms ${(b = this.config.animation) == null ? void 0 : b.type};
      `;
    const s = document.createElement("iframe");
    if (s.style.cssText = `
      width: 100%;
      height: ${this.config.showSupport ? "calc(100% - 24px)" : "100%"};
      border: none;
      display: block; // 添加这一行
      vertical-align: bottom; // 添加这一行
    `, s.src = this.generateChatUrl(), console.log("iframe.src: ", s.src), this.window.appendChild(s), this.config.showSupport) {
      const f = document.createElement("div"), x = ((d = this.config.theme) == null ? void 0 : d.mode) === "dark";
      f.style.cssText = `
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: ${x ? "#aaa" : "#666"};
        font-size: 12px;
        line-height: 24px;
        background: ${x ? "#1f2937" : "#ffffff"};
        padding: 0;
        margin: 0;
        border-top: none; // 确保没有边框
      `, f.innerHTML = `
        <a href="https://www.bytedesk.com" 
           target="_blank" 
           style="
             color: ${x ? "#aaa" : "#666"};
             text-decoration: none;
             display: flex;
             align-items: center;
             height: 100%;
             width: 100%;
             justify-content: center;
           ">
          ${this.getSupportText()}
        </a>
      `, this.window.appendChild(f);
    }
    document.body.appendChild(this.window);
  }
  generateChatUrl(t = "messages") {
    console.log("this.config: ", this.config, t);
    const e = new URLSearchParams(), i = localStorage.getItem(I), o = localStorage.getItem(M);
    i && i.trim() !== "" && e.append("uid", i), o && o.trim() !== "" && e.append("visitorUid", o), Object.entries(this.config.chatConfig || {}).forEach(([s, a]) => {
      if (s === "goodsInfo" || s === "orderInfo")
        try {
          typeof a == "string" ? e.append(s, a) : e.append(s, JSON.stringify(a));
        } catch (r) {
          console.error(`Error processing ${s}:`, r);
        }
      else if (s === "extra")
        try {
          let r = typeof a == "string" ? JSON.parse(a) : a;
          r.goodsInfo && delete r.goodsInfo, r.orderInfo && delete r.orderInfo, Object.keys(r).length > 0 && e.append(s, JSON.stringify(r));
        } catch (r) {
          console.error("Error processing extra parameter:", r);
        }
      else
        e.append(s, String(a));
    }), Object.entries(this.config.browseConfig || {}).forEach(([s, a]) => {
      e.append(s, String(a));
    }), Object.entries(this.config.theme || {}).forEach(([s, a]) => {
      e.append(s, String(a));
    }), e.append("lang", this.config.locale || "zh-cn");
    const n = `${this.config.baseUrl}?${e.toString()}`;
    return console.log("chat url: ", n), n;
  }
  setupMessageListener() {
    window.addEventListener("message", (t) => {
      switch (t.data.type) {
        case F:
          this.hideChat();
          break;
        case Y:
          this.toggleMaximize();
          break;
        case j:
          this.minimizeWindow();
          break;
        case N:
          console.log("RECEIVE_MESSAGE");
          break;
        case H:
          console.log("INVITE_VISITOR");
          break;
        case A:
          console.log("INVITE_VISITOR_ACCEPT");
          break;
        case W:
          console.log("INVITE_VISITOR_REJECT");
          break;
        case R:
          this.handleLocalStorageData(t);
          break;
      }
    });
  }
  // 处理从 iframe 返回的 localStorage 数据
  handleLocalStorageData(t) {
    var s, a;
    const { uid: e, visitorUid: i } = t.data;
    console.log("handleLocalStorageData 被调用", e, i, t.data);
    const o = localStorage.getItem(I), n = localStorage.getItem(M);
    if (o === e && n === i) {
      console.log("handleLocalStorageData: 值相同，跳过设置");
      return;
    }
    localStorage.setItem(I, e), localStorage.setItem(M, i), console.log("handleLocalStorageData: 已更新localStorage", {
      uid: e,
      visitorUid: i
    }), (a = (s = this.config).onVisitorInfo) == null || a.call(s, e, i);
  }
  // 向 iframe 发送消息
  sendMessageToIframe(t) {
    var i;
    const e = (i = this.window) == null ? void 0 : i.querySelector("iframe");
    e && e.contentWindow && e.contentWindow.postMessage(t, "*");
  }
  // preload() {
  //   console.log("preload");
  //   if (this.config.isPreload) {
  //     const preLoadUrl = this.generateChatUrl(true);
  //     console.log("preLoadUrl: ", preLoadUrl);
  //     // 预加载URL
  //     const preLoadIframe = document.createElement("iframe");
  //     preLoadIframe.src = preLoadUrl;
  //     preLoadIframe.style.display = "none";
  //     document.body.appendChild(preLoadIframe);
  //   }
  // }
  showChat(t) {
    var e, i;
    if (t && (this.config = {
      ...this.config,
      ...t
    }, this.window && (document.body.removeChild(this.window), this.window = null)), this.window || this.createChatWindow(), this.window) {
      const o = window.innerWidth <= 768;
      if (this.window.style.display = "block", this.config.forceRefresh) {
        const n = this.window.querySelector("iframe");
        n && (n.src = this.generateChatUrl());
      }
      if (this.setupResizeListener(), o && this.window && (this.window.style.transform = "translateY(100%)", requestAnimationFrame(() => {
        this.window && (this.window.style.transform = "translateY(0)");
      })), this.isVisible = !0, this.bubble) {
        this.bubble.style.display = "none";
        const n = this.bubble.messageElement;
        n instanceof HTMLElement && (n.style.display = "none");
      }
    }
    this.hideInviteDialog(), (i = (e = this.config).onShowChat) == null || i.call(e);
  }
  hideChat() {
    var t, e, i, o, n;
    if (this.window) {
      if (window.innerWidth <= 768 ? (this.window.style.transform = "translateY(100%)", setTimeout(() => {
        this.window && (this.window.style.display = "none");
      }, ((t = this.config.animation) == null ? void 0 : t.duration) || 300)) : this.window.style.display = "none", this.isVisible = !1, this.bubble) {
        this.bubble.style.display = ((e = this.config.buttonConfig) == null ? void 0 : e.show) === !1 ? "none" : "inline-flex";
        const a = this.bubble.messageElement;
        a instanceof HTMLElement && (a.style.display = ((i = this.config.bubbleConfig) == null ? void 0 : i.show) === !1 ? "none" : "block");
      }
      (n = (o = this.config).onHideChat) == null || n.call(o);
    }
  }
  minimizeWindow() {
    this.window && (this.windowState = "minimized", this.window.style.display = "none", this.hideChat());
  }
  toggleMaximize() {
    this.window && window.open(this.generateChatUrl(), "_blank");
  }
  setupResizeListener() {
    const t = () => {
      var s, a;
      if (!this.window || !this.isVisible) return;
      const i = window.innerWidth <= 768, o = window.innerWidth, n = window.innerHeight;
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
        let r = this.windowState === "maximized" ? o : Math.min(
          ((s = this.config.window) == null ? void 0 : s.width) || o * 0.9,
          o * 0.9
        ), u = this.windowState === "maximized" ? n : Math.min(
          ((a = this.config.window) == null ? void 0 : a.height) || n * 0.9,
          n * 0.9
        );
        const h = this.config.placement === "bottom-right" ? this.config.marginSide : void 0, l = this.config.placement === "bottom-left" ? this.config.marginSide : void 0;
        Object.assign(this.window.style, {
          width: `${r}px`,
          height: `${u}px`,
          right: h ? `${h}px` : "auto",
          left: l ? `${l}px` : "auto",
          bottom: `${this.config.marginBottom}px`,
          borderRadius: this.windowState === "maximized" ? "0" : "12px"
        });
      }
    };
    let e;
    window.addEventListener("resize", () => {
      clearTimeout(e), e = window.setTimeout(t, 100);
    }), t();
  }
  destroy() {
    var e;
    const t = (e = this.bubble) == null ? void 0 : e.parentElement;
    t && document.body.contains(t) && (document.body.removeChild(t), this.bubble = null), this.window && document.body.contains(this.window) && (document.body.removeChild(this.window), this.window = null), window.removeEventListener("resize", this.setupResizeListener.bind(this)), this.loopTimer && (window.clearTimeout(this.loopTimer), this.loopTimer = null), this.inviteDialog && document.body.contains(this.inviteDialog) && (document.body.removeChild(this.inviteDialog), this.inviteDialog = null), this.contextMenu && document.body.contains(this.contextMenu) && (document.body.removeChild(this.contextMenu), this.contextMenu = null), this.hideTimeout && (clearTimeout(this.hideTimeout), this.hideTimeout = null);
  }
  createInviteDialog() {
    var a, r, u, h, l, b;
    if (this.inviteDialog && document.body.contains(this.inviteDialog)) {
      console.log("邀请框已存在，不重复创建");
      return;
    }
    const t = ((a = this.config.theme) == null ? void 0 : a.mode) === "dark";
    if (this.inviteDialog = document.createElement("div"), this.inviteDialog.style.cssText = `
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background: ${t ? "#1f2937" : "white"};
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 4px 24px rgba(0, 0, 0, ${t ? "0.3" : "0.15"});
      z-index: 10001;
      display: none;
      max-width: 300px;
      text-align: center;
    `, (r = this.config.inviteConfig) != null && r.icon) {
      const d = document.createElement("div");
      d.style.cssText = `
        font-size: 32px;
        margin-bottom: 12px;
        color: ${t ? "#e5e7eb" : "#333"};
      `, d.textContent = this.config.inviteConfig.icon, this.inviteDialog.appendChild(d);
    }
    const e = document.createElement("div");
    e.style.cssText = `
      margin-bottom: 16px;
      color: ${t ? "#e5e7eb" : "#333"};
    `, e.textContent = ((u = this.config.inviteConfig) == null ? void 0 : u.text) || "需要帮助吗？点击开始对话", this.inviteDialog.appendChild(e);
    const i = document.createElement("div");
    i.style.cssText = `
      display: flex;
      gap: 10px;
      justify-content: center;
    `;
    const o = document.createElement("button");
    o.textContent = ((h = this.config.inviteConfig) == null ? void 0 : h.acceptText) || "开始对话";
    const n = ((l = this.config.theme) == null ? void 0 : l.backgroundColor) || (t ? "#3B82F6" : "#0066FF");
    o.style.cssText = `
      padding: 8px 16px;
      background: ${n};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, o.onclick = () => {
      var d, f;
      this.hideInviteDialog(), this.showChat(), (f = (d = this.config.inviteConfig) == null ? void 0 : d.onAccept) == null || f.call(d);
    };
    const s = document.createElement("button");
    s.textContent = ((b = this.config.inviteConfig) == null ? void 0 : b.rejectText) || "稍后再说", s.style.cssText = `
      padding: 8px 16px;
      background: ${t ? "#374151" : "#f5f5f5"};
      color: ${t ? "#d1d5db" : "#666"};
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, s.onclick = () => {
      var d, f;
      this.hideInviteDialog(), (f = (d = this.config.inviteConfig) == null ? void 0 : d.onReject) == null || f.call(d), this.handleInviteLoop();
    }, i.appendChild(o), i.appendChild(s), this.inviteDialog.appendChild(i), document.body.appendChild(this.inviteDialog);
  }
  showInviteDialog() {
    var t, e;
    this.inviteDialog && (this.inviteDialog.style.display = "block", (e = (t = this.config.inviteConfig) == null ? void 0 : t.onOpen) == null || e.call(t));
  }
  hideInviteDialog() {
    var t, e;
    console.log("hideInviteDialog before"), this.inviteDialog && (this.inviteDialog.style.display = "none", (e = (t = this.config.inviteConfig) == null ? void 0 : t.onClose) == null || e.call(t), console.log("hideInviteDialog after"));
  }
  handleInviteLoop() {
    const {
      loop: t,
      loopDelay: e = 3e3,
      loopCount: i = 1 / 0
    } = this.config.inviteConfig || {};
    !t || this.loopCount >= i - 1 || (this.loopTimer && window.clearTimeout(this.loopTimer), this.loopTimer = window.setTimeout(() => {
      this.loopCount++, this.showInviteDialog();
    }, e));
  }
  showButton() {
    this.bubble && (this.bubble.style.display = "inline-flex");
  }
  hideButton() {
    this.bubble && (this.bubble.style.display = "none");
  }
  showBubble() {
    if (this.bubble) {
      const t = this.bubble.messageElement;
      t instanceof HTMLElement && (t.style.display = "block", setTimeout(() => {
        t.style.opacity = "1", t.style.transform = "translateY(0)";
      }, 100));
    }
  }
  hideBubble() {
    if (this.bubble) {
      const t = this.bubble.messageElement;
      t instanceof HTMLElement && (t.style.opacity = "0", t.style.transform = "translateY(10px)", setTimeout(() => {
        t.style.display = "none";
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
    const t = [
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
    t.forEach((e, i) => {
      const o = document.createElement("div");
      if (o.style.cssText = `
        padding: 8px 16px;
        cursor: pointer;
        color: #333;
        font-size: 14px;
        
        &:hover {
          background: #f5f5f5;
        }
      `, o.textContent = e.text, o.onclick = () => {
        e.onClick(), this.hideContextMenu();
      }, this.contextMenu && this.contextMenu.appendChild(o), i < t.length - 1) {
        const n = document.createElement("div");
        n.style.cssText = `
          height: 1px;
          background: #eee;
          margin: 4px 0;
        `, this.contextMenu && this.contextMenu.appendChild(n);
      }
    }), document.body.appendChild(this.contextMenu);
  }
  showContextMenu(t) {
    if (t.preventDefault(), this.contextMenu || this.createContextMenu(), this.contextMenu) {
      this.contextMenu.style.visibility = "hidden", this.contextMenu.style.display = "block";
      const e = this.contextMenu.offsetWidth, i = this.contextMenu.offsetHeight;
      let o = t.clientX, n = t.clientY;
      o + e > window.innerWidth && (o = o - e), n + i > window.innerHeight && (n = n - i), o = Math.max(0, o), n = Math.max(0, n), this.contextMenu.style.left = `${o}px`, this.contextMenu.style.top = `${n}px`, this.contextMenu.style.visibility = "visible";
    }
  }
  hideContextMenu() {
    this.contextMenu && (this.contextMenu.style.display = "none");
  }
  togglePlacement() {
    var e, i;
    if (!this.bubble) return;
    this.config.placement = this.config.placement === "bottom-left" ? "bottom-right" : "bottom-left";
    const t = this.bubble.parentElement;
    t && (t.style.left = this.config.placement === "bottom-left" ? `${this.config.marginSide}px` : "auto", t.style.right = this.config.placement === "bottom-right" ? `${this.config.marginSide}px` : "auto", t.style.alignItems = this.config.placement === "bottom-left" ? "flex-start" : "flex-end", this.window && this.isVisible && (this.window.style.left = this.config.placement === "bottom-left" ? `${this.config.marginSide}px` : "auto", this.window.style.right = this.config.placement === "bottom-right" ? `${this.config.marginSide}px` : "auto"), (i = (e = this.config).onConfigChange) == null || i.call(e, { placement: this.config.placement }));
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
}
export {
  J as default
};
