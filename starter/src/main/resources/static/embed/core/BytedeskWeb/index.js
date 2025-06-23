var R = Object.defineProperty;
var W = (x, t, e) => t in x ? R(x, t, { enumerable: !0, configurable: !0, writable: !0, value: e }) : x[t] = e;
var p = (x, t, e) => W(x, typeof t != "symbol" ? t + "" : t, e);
import { BYTEDESK_UID as S, BYTEDESK_VISITOR_UID as M, POST_MESSAGE_LOCALSTORAGE_RESPONSE as A, POST_MESSAGE_INVITE_VISITOR_REJECT as V, POST_MESSAGE_INVITE_VISITOR_ACCEPT as H, POST_MESSAGE_INVITE_VISITOR as P, POST_MESSAGE_RECEIVE_MESSAGE as j, POST_MESSAGE_MINIMIZE_WINDOW as Y, POST_MESSAGE_MAXIMIZE_WINDOW as N, POST_MESSAGE_CLOSE_CHAT_WINDOW as G } from "../../utils/constants/index.js";
class q {
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
      isPreload: !1,
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
  init() {
    var t;
    this.createBubble(), this.createInviteDialog(), this.setupMessageListener(), this.setupResizeListener(), this.preload(), this._getUnreadMessageCount(), this.config.autoPopup && setTimeout(() => {
      this.showChat();
    }, this.config.autoPopupDelay || 1e3), (t = this.config.inviteConfig) != null && t.show && setTimeout(() => {
      this.showInviteDialog();
    }, this.config.inviteConfig.delay || 3e3);
  }
  async _getUnreadMessageCount() {
    return import("../../apis/message/index.js").then(async ({ getMessageUnreadCount: t }) => {
      var l, c, f, d, h;
      const e = String((l = this.config.chatConfig) == null ? void 0 : l.uid), i = localStorage.getItem(S), o = localStorage.getItem(M), n = {
        uid: i || "",
        visitorUid: e || o || "",
        orgUid: ((c = this.config.chatConfig) == null ? void 0 : c.org) || ""
      }, s = await t(n);
      return console.log("获取未读消息数:", s.data, n), ((f = s.data) == null ? void 0 : f.code) === 200 ? ((d = s == null ? void 0 : s.data) != null && d.data && ((h = s == null ? void 0 : s.data) == null ? void 0 : h.data) > 0 ? this.showUnreadBadge(s.data.data) : this.clearUnreadBadge(), s.data.data || 0) : 0;
    });
  }
  // 新增公共方法，供外部调用获取未读消息数
  async getUnreadMessageCount() {
    return this._getUnreadMessageCount();
  }
  // 显示未读消息数角标
  showUnreadBadge(t) {
    if (!this.bubble) return;
    let e = this.bubble.querySelector(".bytedesk-unread-badge");
    e || (e = document.createElement("div"), e.className = "bytedesk-unread-badge", e.style.cssText = `
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
      `, this.bubble.appendChild(e)), e.textContent = t > 99 ? "99+" : t.toString();
  }
  // 清除未读消息数角标
  clearUnreadBadge() {
    if (!this.bubble) return;
    const t = this.bubble.querySelector(".bytedesk-unread-badge");
    t && t.remove();
  }
  // 清空未读消息数
  async clearMessageUnread() {
    return import("../../apis/message/index.js").then(async ({ clearMessageUnread: t }) => {
      var l, c;
      const e = String((l = this.config.chatConfig) == null ? void 0 : l.uid), i = localStorage.getItem(S), o = localStorage.getItem(M), n = {
        uid: i || "",
        visitorUid: e || o || "",
        orgUid: ((c = this.config.chatConfig) == null ? void 0 : c.org) || ""
      }, s = await t(n);
      return console.log("清空未读消息数:", s.data, n), s.data.code === 200 ? (console.log("清空未读消息数成功:", s.data), s.data.data === 0 && this.clearUnreadBadge(), s.data.data || 0) : (console.error("清空未读消息数失败:", s.data.message), 0);
    });
  }
  createBubble() {
    var h, u, r, m, w, I, k, $, D, _, L, U, B;
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
    if ((h = this.config.bubbleConfig) != null && h.show) {
      e = document.createElement("div"), e.style.cssText = `
        background: ${((u = this.config.theme) == null ? void 0 : u.mode) === "dark" ? "#1f2937" : "white"};
        color: ${((r = this.config.theme) == null ? void 0 : r.mode) === "dark" ? "#e5e7eb" : "#1f2937"};
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
      const a = document.createElement("div");
      a.style.cssText = `
        display: flex;
        align-items: center;
        gap: 8px;
        flex-direction: ${this.config.placement === "bottom-left" ? "row" : "row-reverse"};
      `, a.setAttribute("data-placement", this.config.placement || "bottom-right");
      const y = document.createElement("span");
      y.textContent = ((m = this.config.bubbleConfig) == null ? void 0 : m.icon) || "", y.style.fontSize = "20px", a.appendChild(y);
      const C = document.createElement("div"), b = document.createElement("div");
      b.textContent = ((w = this.config.bubbleConfig) == null ? void 0 : w.title) || "", b.style.fontWeight = "bold", b.style.color = ((I = this.config.theme) == null ? void 0 : I.mode) === "dark" ? "#e5e7eb" : "#1f2937", b.style.marginBottom = "4px", b.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", C.appendChild(b);
      const g = document.createElement("div");
      g.textContent = ((k = this.config.bubbleConfig) == null ? void 0 : k.subtitle) || "", g.style.fontSize = "0.9em", g.style.color = (($ = this.config.theme) == null ? void 0 : $.mode) === "dark" ? "#9ca3af" : "#4b5563", g.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", C.appendChild(g), a.appendChild(C), e.appendChild(a);
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
      const v = document.createElement("div");
      v.style.cssText = `
        position: absolute;
        bottom: 0;
        ${this.config.placement === "bottom-left" ? "left: 18px" : "right: 18px"};
        width: 24px;
        height: 12px;
        background: ${((_ = this.config.theme) == null ? void 0 : _.mode) === "dark" ? "#1f2937" : "white"};
      `, e.appendChild(E), e.appendChild(v), t.appendChild(e), setTimeout(() => {
        e && (e.style.opacity = "1", e.style.transform = "translateY(0)");
      }, 500);
    }
    this.bubble = document.createElement("button");
    const i = this.config.buttonConfig || {}, o = i.width || 60, n = i.height || 60, s = Math.min(o, n) / 2, l = ((L = this.config.theme) == null ? void 0 : L.mode) === "dark", c = l ? "#3B82F6" : "#0066FF", f = ((U = this.config.theme) == null ? void 0 : U.backgroundColor) || c;
    this.bubble.style.cssText = `
      background-color: ${f};
      width: ${o}px;
      height: ${n}px;
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
    const d = document.createElement("div");
    if (d.style.cssText = `
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    `, i.icon) {
      const a = document.createElement("span");
      a.textContent = i.icon, a.style.fontSize = `${n * 0.4}px`, d.appendChild(a);
    } else {
      const a = document.createElement("div");
      a.innerHTML = `
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
                fill="white"/>
        </svg>
      `, d.appendChild(a);
    }
    if (i.text) {
      const a = document.createElement("span");
      a.textContent = i.text, a.style.cssText = `
        color: ${((B = this.config.theme) == null ? void 0 : B.textColor) || "#ffffff"};
        font-size: ${n * 0.25}px;
        white-space: nowrap;
      `, d.appendChild(a);
    }
    if (this.bubble.appendChild(d), this.bubble.addEventListener("mouseenter", () => {
      this.bubble.style.transform = "scale(1.1)";
    }), this.bubble.addEventListener("mouseleave", () => {
      this.bubble.style.transform = "scale(1)";
    }), t.appendChild(this.bubble), this.config.draggable) {
      let a = 0, y = 0, C = 0, b = 0;
      this.bubble.addEventListener("mousedown", (g) => {
        g.button === 0 && (this.isDragging = !0, a = g.clientX, y = g.clientY, C = t.offsetLeft, b = t.offsetTop, t.style.transition = "none");
      }), document.addEventListener("mousemove", (g) => {
        if (!this.isDragging) return;
        g.preventDefault();
        const E = g.clientX - a, v = g.clientY - y, T = C + E, z = b + v, O = window.innerHeight - t.offsetHeight;
        T <= window.innerWidth / 2 ? (t.style.left = `${Math.max(0, T)}px`, t.style.right = "auto", t.style.alignItems = "flex-start", this.config.placement = "bottom-left") : (t.style.right = `${Math.max(0, window.innerWidth - T - t.offsetWidth)}px`, t.style.left = "auto", t.style.alignItems = "flex-end", this.config.placement = "bottom-right"), t.style.bottom = `${Math.min(Math.max(0, window.innerHeight - z - t.offsetHeight), O)}px`;
      }), document.addEventListener("mouseup", () => {
        this.isDragging && (this.isDragging = !1, t.style.transition = "all 0.3s ease", this.config.marginSide = parseInt(
          this.config.placement === "bottom-left" ? t.style.left : t.style.right
        ) || 20, this.config.marginBottom = parseInt(t.style.bottom || "20"));
      });
    }
    this.bubble.addEventListener("click", () => {
      if (!this.isDragging) {
        console.log("bubble click");
        const a = this.bubble.messageElement;
        a instanceof HTMLElement && (a.style.display = "none"), this.showChat();
      }
    }), this.bubble.messageElement = e, document.body.appendChild(t), this.bubble.addEventListener("contextmenu", (a) => {
      this.showContextMenu(a);
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
    var l, c, f, d, h, u, r;
    this.window = document.createElement("div");
    const t = window.innerWidth <= 768, e = window.innerWidth, i = window.innerHeight, o = Math.min(((l = this.config.window) == null ? void 0 : l.width) || e * 0.9, e * 0.9), n = Math.min(((c = this.config.window) == null ? void 0 : c.height) || i * 0.9, i * 0.9);
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
        transition: all ${(f = this.config.animation) == null ? void 0 : f.duration}ms ${(d = this.config.animation) == null ? void 0 : d.type};
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
        transition: all ${(h = this.config.animation) == null ? void 0 : h.duration}ms ${(u = this.config.animation) == null ? void 0 : u.type};
      `;
    const s = document.createElement("iframe");
    if (s.style.cssText = `
      width: 100%;
      height: ${this.config.showSupport ? "calc(100% - 24px)" : "100%"};
      border: none;
      display: block; // 添加这一行
      vertical-align: bottom; // 添加这一行
    `, s.src = this.generateChatUrl(), console.log("iframe.src: ", s.src), this.window.appendChild(s), this.config.showSupport) {
      const m = document.createElement("div"), w = ((r = this.config.theme) == null ? void 0 : r.mode) === "dark";
      m.style.cssText = `
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: ${w ? "#aaa" : "#666"};
        font-size: 12px;
        line-height: 24px;
        background: ${w ? "#1f2937" : "#ffffff"};
        padding: 0;
        margin: 0;
        border-top: none; // 确保没有边框
      `, m.innerHTML = `
        <a href="https://www.bytedesk.com" 
           target="_blank" 
           style="
             color: ${w ? "#aaa" : "#666"};
             text-decoration: none;
             display: flex;
             align-items: center;
             height: 100%;
             width: 100%;
             justify-content: center;
           ">
          ${this.getSupportText()}
        </a>
      `, this.window.appendChild(m);
    }
    document.body.appendChild(this.window);
  }
  generateChatUrl(t = !1, e = "messages") {
    console.log("this.config: ", this.config, e);
    const i = new URLSearchParams();
    return Object.entries(this.config.chatConfig || {}).forEach(([o, n]) => {
      if (o === "goodsInfo" || o === "orderInfo")
        try {
          typeof n == "string" ? i.append(o, n) : i.append(o, JSON.stringify(n));
        } catch (s) {
          console.error(`Error processing ${o}:`, s);
        }
      else if (o === "extra")
        try {
          let s = typeof n == "string" ? JSON.parse(n) : n;
          s.goodsInfo && delete s.goodsInfo, s.orderInfo && delete s.orderInfo, Object.keys(s).length > 0 && i.append(o, JSON.stringify(s));
        } catch (s) {
          console.error("Error processing extra parameter:", s);
        }
      else
        i.append(o, String(n));
    }), Object.entries(this.config.browseConfig || {}).forEach(([o, n]) => {
      i.append(o, String(n));
    }), Object.entries(this.config.theme || {}).forEach(([o, n]) => {
      i.append(o, String(n));
    }), i.append("lang", this.config.locale || "zh-cn"), t && i.append("preload", "1"), `${this.config.baseUrl}?${i.toString()}`;
  }
  setupMessageListener() {
    window.addEventListener("message", (t) => {
      switch (t.data.type) {
        case G:
          this.hideChat();
          break;
        case N:
          this.toggleMaximize();
          break;
        case Y:
          this.minimizeWindow();
          break;
        case j:
          console.log("RECEIVE_MESSAGE");
          break;
        case P:
          console.log("INVITE_VISITOR");
          break;
        case H:
          console.log("INVITE_VISITOR_ACCEPT");
          break;
        case V:
          console.log("INVITE_VISITOR_REJECT");
          break;
        case A:
          this.handleLocalStorageData(t);
          break;
      }
    });
  }
  // 处理从 iframe 返回的 localStorage 数据
  handleLocalStorageData(t) {
    var o, n;
    const { uid: e, visitorUid: i } = t.data;
    localStorage.setItem(S, e), localStorage.setItem(M, i), (n = (o = this.config).onVisitorInfo) == null || n.call(o, e, i);
  }
  // 向 iframe 发送消息
  sendMessageToIframe(t) {
    var i;
    const e = (i = this.window) == null ? void 0 : i.querySelector("iframe");
    e && e.contentWindow && e.contentWindow.postMessage(t, "*");
  }
  preload() {
    if (console.log("preload"), this.config.isPreload) {
      const t = this.generateChatUrl(!0);
      console.log("preLoadUrl: ", t);
      const e = document.createElement("iframe");
      e.src = t, e.style.display = "none", document.body.appendChild(e);
    }
  }
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
        const l = this.bubble.messageElement;
        l instanceof HTMLElement && (l.style.display = ((i = this.config.bubbleConfig) == null ? void 0 : i.show) === !1 ? "none" : "block");
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
      var s, l;
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
        let c = this.windowState === "maximized" ? o : Math.min(((s = this.config.window) == null ? void 0 : s.width) || o * 0.9, o * 0.9), f = this.windowState === "maximized" ? n : Math.min(((l = this.config.window) == null ? void 0 : l.height) || n * 0.9, n * 0.9);
        const d = this.config.placement === "bottom-right" ? this.config.marginSide : void 0, h = this.config.placement === "bottom-left" ? this.config.marginSide : void 0;
        Object.assign(this.window.style, {
          width: `${c}px`,
          height: `${f}px`,
          right: d ? `${d}px` : "auto",
          left: h ? `${h}px` : "auto",
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
    var l, c, f, d, h, u;
    if (this.inviteDialog && document.body.contains(this.inviteDialog)) {
      console.log("邀请框已存在，不重复创建");
      return;
    }
    const t = ((l = this.config.theme) == null ? void 0 : l.mode) === "dark";
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
    `, (c = this.config.inviteConfig) != null && c.icon) {
      const r = document.createElement("div");
      r.style.cssText = `
        font-size: 32px;
        margin-bottom: 12px;
        color: ${t ? "#e5e7eb" : "#333"};
      `, r.textContent = this.config.inviteConfig.icon, this.inviteDialog.appendChild(r);
    }
    const e = document.createElement("div");
    e.style.cssText = `
      margin-bottom: 16px;
      color: ${t ? "#e5e7eb" : "#333"};
    `, e.textContent = ((f = this.config.inviteConfig) == null ? void 0 : f.text) || "需要帮助吗？点击开始对话", this.inviteDialog.appendChild(e);
    const i = document.createElement("div");
    i.style.cssText = `
      display: flex;
      gap: 10px;
      justify-content: center;
    `;
    const o = document.createElement("button");
    o.textContent = ((d = this.config.inviteConfig) == null ? void 0 : d.acceptText) || "开始对话";
    const n = ((h = this.config.theme) == null ? void 0 : h.backgroundColor) || (t ? "#3B82F6" : "#0066FF");
    o.style.cssText = `
      padding: 8px 16px;
      background: ${n};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, o.onclick = () => {
      var r, m;
      this.hideInviteDialog(), this.showChat(), (m = (r = this.config.inviteConfig) == null ? void 0 : r.onAccept) == null || m.call(r);
    };
    const s = document.createElement("button");
    s.textContent = ((u = this.config.inviteConfig) == null ? void 0 : u.rejectText) || "稍后再说", s.style.cssText = `
      padding: 8px 16px;
      background: ${t ? "#374151" : "#f5f5f5"};
      color: ${t ? "#d1d5db" : "#666"};
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, s.onclick = () => {
      var r, m;
      this.hideInviteDialog(), (m = (r = this.config.inviteConfig) == null ? void 0 : r.onReject) == null || m.call(r), this.handleInviteLoop();
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
    const { loop: t, loopDelay: e = 3e3, loopCount: i = 1 / 0 } = this.config.inviteConfig || {};
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
  q as default
};
