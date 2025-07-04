var z = Object.defineProperty;
var O = (v, t, e) => t in v ? z(v, t, { enumerable: !0, configurable: !0, writable: !0, value: e }) : v[t] = e;
var u = (v, t, e) => O(v, typeof t != "symbol" ? t + "" : t, e);
import { BYTEDESK_UID as y, BYTEDESK_VISITOR_UID as C, POST_MESSAGE_LOCALSTORAGE_RESPONSE as R, POST_MESSAGE_INVITE_VISITOR_REJECT as W, POST_MESSAGE_INVITE_VISITOR_ACCEPT as A, POST_MESSAGE_INVITE_VISITOR as H, POST_MESSAGE_RECEIVE_MESSAGE as j, POST_MESSAGE_MINIMIZE_WINDOW as N, POST_MESSAGE_MAXIMIZE_WINDOW as Y, POST_MESSAGE_CLOSE_CHAT_WINDOW as G } from "../../utils/constants/index.js";
class J {
  constructor(t) {
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
  async init() {
    var t;
    await this._initVisitor(), this.createBubble(), this.createInviteDialog(), this.setupMessageListener(), this.setupResizeListener(), this.preload(), this._getUnreadMessageCount(), this.config.autoPopup && setTimeout(() => {
      this.showChat();
    }, this.config.autoPopupDelay || 1e3), (t = this.config.inviteConfig) != null && t.show && setTimeout(() => {
      this.showInviteDialog();
    }, this.config.inviteConfig.delay || 3e3), console.log("BytedeskWeb.init() 执行完成");
  }
  async _initVisitor() {
    var i, o, n, l;
    if (this.initVisitorPromise)
      return console.log("访客初始化请求正在进行中，返回现有Promise"), this.initVisitorPromise;
    const t = localStorage.getItem(y), e = localStorage.getItem(C);
    return console.log("localUid: ", t), console.log("localVisitorUid: ", e), t && e && ((i = this.config.chatConfig) != null && i.visitorUid) && ((o = this.config.chatConfig) == null ? void 0 : o.visitorUid) === e ? (console.log("访客信息相同，直接返回本地访客信息"), (l = (n = this.config).onVisitorInfo) == null || l.call(n, t || "", e || ""), {
      uid: t,
      visitorUid: e
    }) : (console.log("开始创建访客初始化Promise"), this.initVisitorPromise = import("../../apis/visitor/index.js").then(
      async ({ initVisitor: s }) => {
        var a, c, g, d, b, h, m, x, S, I, M, T, U, D, k, $;
        try {
          const r = {
            uid: String(((a = this.config.chatConfig) == null ? void 0 : a.uid) || t || ""),
            visitorUid: String(
              ((c = this.config.chatConfig) == null ? void 0 : c.visitorUid) || e || ""
            ),
            orgUid: String(((g = this.config.chatConfig) == null ? void 0 : g.org) || ""),
            nickname: String(((d = this.config.chatConfig) == null ? void 0 : d.name) || ""),
            avatar: String(((b = this.config.chatConfig) == null ? void 0 : b.avatar) || ""),
            extra: typeof ((h = this.config.chatConfig) == null ? void 0 : h.extra) == "string" ? this.config.chatConfig.extra : JSON.stringify(((m = this.config.chatConfig) == null ? void 0 : m.extra) || {})
          };
          console.log("this.config.chatConfig: ", this.config.chatConfig), console.log("本地无访客信息，开始API初始化，参数:", r), console.log("开始调用initVisitor API");
          const f = await s(r);
          return console.log("访客初始化API响应:", f.data), ((x = f.data) == null ? void 0 : x.code) === 200 ? (console.log("访客初始化成功，保存到localStorage"), (I = (S = f.data) == null ? void 0 : S.data) != null && I.uid && (localStorage.setItem(y, f.data.data.uid), console.log("已保存uid到localStorage:", f.data.data.uid)), (T = (M = f.data) == null ? void 0 : M.data) != null && T.visitorUid && (localStorage.setItem(
            C,
            f.data.data.visitorUid
          ), console.log(
            "已保存visitorUid到localStorage:",
            f.data.data.visitorUid
          )), (U = f.data) != null && U.data && (console.log("触发onVisitorInfo回调"), (k = (D = this.config).onVisitorInfo) == null || k.call(
            D,
            f.data.data.uid || "",
            f.data.data.visitorUid || ""
          )), f.data.data) : (console.error("访客初始化失败:", ($ = f.data) == null ? void 0 : $.message), null);
        } catch (r) {
          return console.error("访客初始化出错:", r), null;
        } finally {
          console.log("访客初始化Promise完成，清除引用"), this.initVisitorPromise = null;
        }
      }
    ), this.initVisitorPromise);
  }
  async _getUnreadMessageCount() {
    return this.getUnreadMessageCountPromise ? (this.config.isDebug && console.log("获取未读消息数请求正在进行中，返回现有Promise"), this.getUnreadMessageCountPromise) : (this.getUnreadMessageCountPromise = import("../../apis/message/index.js").then(
      async ({ getUnreadMessageCount: t }) => {
        var e, i, o, n, l;
        try {
          const s = String((e = this.config.chatConfig) == null ? void 0 : e.visitorUid), a = localStorage.getItem(y), c = localStorage.getItem(C), g = {
            uid: a || "",
            visitorUid: s || c || "",
            orgUid: ((i = this.config.chatConfig) == null ? void 0 : i.org) || ""
          };
          if (g.uid === "")
            return 0;
          const d = await t(g);
          return console.log("获取未读消息数:", d.data, g), ((o = d.data) == null ? void 0 : o.code) === 200 ? ((n = d == null ? void 0 : d.data) != null && n.data && ((l = d == null ? void 0 : d.data) == null ? void 0 : l.data) > 0 ? this.showUnreadBadge(d.data.data) : this.clearUnreadBadge(), d.data.data || 0) : 0;
        } catch (s) {
          return console.error("获取未读消息数出错:", s), 0;
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
  // 清除本地访客信息，强制重新初始化
  clearVisitorInfo() {
    localStorage.removeItem(y), localStorage.removeItem(C), this.config.isDebug && console.log("已清除本地访客信息");
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
          const o = String((e = this.config.chatConfig) == null ? void 0 : e.visitorUid), n = localStorage.getItem(y), l = localStorage.getItem(C), s = {
            uid: n || "",
            visitorUid: o || l || "",
            orgUid: ((i = this.config.chatConfig) == null ? void 0 : i.org) || ""
          }, a = await t(s);
          return console.log("清空未读消息数:", a.data, s), a.data.code === 200 ? (console.log("清空未读消息数成功:", a.data), this.clearUnreadBadge(), a.data.data || 0) : (console.error("清空未读消息数失败:", a.data.message), 0);
        } catch (o) {
          return console.error("清空未读消息数出错:", o), 0;
        } finally {
          this.clearUnreadMessagesPromise = null;
        }
      }
    ), this.clearUnreadMessagesPromise);
  }
  createBubble() {
    var d, b, h, m, x, S, I, M, T, U, D, k, $;
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
    if ((d = this.config.bubbleConfig) != null && d.show) {
      e = document.createElement("div"), e.style.cssText = `
        background: ${((b = this.config.theme) == null ? void 0 : b.mode) === "dark" ? "#1f2937" : "white"};
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
      const r = document.createElement("div");
      r.style.cssText = `
        display: flex;
        align-items: center;
        gap: 8px;
        flex-direction: ${this.config.placement === "bottom-left" ? "row" : "row-reverse"};
      `, r.setAttribute(
        "data-placement",
        this.config.placement || "bottom-right"
      );
      const f = document.createElement("span");
      f.textContent = ((m = this.config.bubbleConfig) == null ? void 0 : m.icon) || "", f.style.fontSize = "20px", r.appendChild(f);
      const E = document.createElement("div"), w = document.createElement("div");
      w.textContent = ((x = this.config.bubbleConfig) == null ? void 0 : x.title) || "", w.style.fontWeight = "bold", w.style.color = ((S = this.config.theme) == null ? void 0 : S.mode) === "dark" ? "#e5e7eb" : "#1f2937", w.style.marginBottom = "4px", w.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", E.appendChild(w);
      const p = document.createElement("div");
      p.textContent = ((I = this.config.bubbleConfig) == null ? void 0 : I.subtitle) || "", p.style.fontSize = "0.9em", p.style.color = ((M = this.config.theme) == null ? void 0 : M.mode) === "dark" ? "#9ca3af" : "#4b5563", p.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", E.appendChild(p), r.appendChild(E), e.appendChild(r);
      const P = document.createElement("div");
      P.style.cssText = `
        position: absolute;
        bottom: -6px;
        ${this.config.placement === "bottom-left" ? "left: 24px" : "right: 24px"};
        width: 12px;
        height: 12px;
        background: ${((T = this.config.theme) == null ? void 0 : T.mode) === "dark" ? "#1f2937" : "white"};
        transform: rotate(45deg);
        box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      `;
      const _ = document.createElement("div");
      _.style.cssText = `
        position: absolute;
        bottom: 0;
        ${this.config.placement === "bottom-left" ? "left: 18px" : "right: 18px"};
        width: 24px;
        height: 12px;
        background: ${((U = this.config.theme) == null ? void 0 : U.mode) === "dark" ? "#1f2937" : "white"};
      `, e.appendChild(P), e.appendChild(_), t.appendChild(e), setTimeout(() => {
        e && (e.style.opacity = "1", e.style.transform = "translateY(0)");
      }, 500);
    }
    this.bubble = document.createElement("button");
    const i = this.config.buttonConfig || {}, o = i.width || 60, n = i.height || 60, l = Math.min(o, n) / 2, s = ((D = this.config.theme) == null ? void 0 : D.mode) === "dark", a = s ? "#3B82F6" : "#0066FF", c = ((k = this.config.theme) == null ? void 0 : k.backgroundColor) || a;
    this.bubble.style.cssText = `
      background-color: ${c};
      width: ${o}px;
      height: ${n}px;
      border-radius: ${l}px;
      border: none;
      cursor: ${this.config.draggable ? "move" : "pointer"};
      display: ${i.show === !1 ? "none" : "flex"};
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(0, 0, 0, ${s ? "0.3" : "0.12"});
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
    `;
    const g = document.createElement("div");
    if (g.style.cssText = `
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    `, i.icon) {
      const r = document.createElement("span");
      r.textContent = i.icon, r.style.fontSize = `${n * 0.4}px`, g.appendChild(r);
    } else {
      const r = document.createElement("div");
      r.innerHTML = `
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
                fill="white"/>
        </svg>
      `, g.appendChild(r);
    }
    if (i.text) {
      const r = document.createElement("span");
      r.textContent = i.text, r.style.cssText = `
        color: ${(($ = this.config.theme) == null ? void 0 : $.textColor) || "#ffffff"};
        font-size: ${n * 0.25}px;
        white-space: nowrap;
      `, g.appendChild(r);
    }
    if (this.bubble.appendChild(g), this.bubble.addEventListener("mouseenter", () => {
      this.bubble.style.transform = "scale(1.1)";
    }), this.bubble.addEventListener("mouseleave", () => {
      this.bubble.style.transform = "scale(1)";
    }), t.appendChild(this.bubble), this.config.draggable) {
      let r = 0, f = 0, E = 0, w = 0;
      this.bubble.addEventListener("mousedown", (p) => {
        p.button === 0 && (this.isDragging = !0, r = p.clientX, f = p.clientY, E = t.offsetLeft, w = t.offsetTop, t.style.transition = "none");
      }), document.addEventListener("mousemove", (p) => {
        if (!this.isDragging) return;
        p.preventDefault();
        const P = p.clientX - r, _ = p.clientY - f, B = E + P, V = w + _, L = window.innerHeight - t.offsetHeight;
        B <= window.innerWidth / 2 ? (t.style.left = `${Math.max(0, B)}px`, t.style.right = "auto", t.style.alignItems = "flex-start", this.config.placement = "bottom-left") : (t.style.right = `${Math.max(
          0,
          window.innerWidth - B - t.offsetWidth
        )}px`, t.style.left = "auto", t.style.alignItems = "flex-end", this.config.placement = "bottom-right"), t.style.bottom = `${Math.min(
          Math.max(0, window.innerHeight - V - t.offsetHeight),
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
        const r = this.bubble.messageElement;
        r instanceof HTMLElement && (r.style.display = "none"), this.showChat();
      }
    }), this.bubble.messageElement = e, document.body.appendChild(t), this.bubble.addEventListener("contextmenu", (r) => {
      this.showContextMenu(r);
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
    var s, a, c, g, d, b, h;
    this.window = document.createElement("div");
    const t = window.innerWidth <= 768, e = window.innerWidth, i = window.innerHeight, o = Math.min(
      ((s = this.config.window) == null ? void 0 : s.width) || e * 0.9,
      e * 0.9
    ), n = Math.min(
      ((a = this.config.window) == null ? void 0 : a.height) || i * 0.9,
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
        transition: all ${(c = this.config.animation) == null ? void 0 : c.duration}ms ${(g = this.config.animation) == null ? void 0 : g.type};
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
        transition: all ${(d = this.config.animation) == null ? void 0 : d.duration}ms ${(b = this.config.animation) == null ? void 0 : b.type};
      `;
    const l = document.createElement("iframe");
    if (l.style.cssText = `
      width: 100%;
      height: ${this.config.showSupport ? "calc(100% - 24px)" : "100%"};
      border: none;
      display: block; // 添加这一行
      vertical-align: bottom; // 添加这一行
    `, l.src = this.generateChatUrl(), console.log("iframe.src: ", l.src), this.window.appendChild(l), this.config.showSupport) {
      const m = document.createElement("div"), x = ((h = this.config.theme) == null ? void 0 : h.mode) === "dark";
      m.style.cssText = `
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
      `, m.innerHTML = `
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
      `, this.window.appendChild(m);
    }
    document.body.appendChild(this.window);
  }
  generateChatUrl(t = !1, e = "messages") {
    console.log("this.config: ", this.config, e);
    const i = new URLSearchParams(), o = localStorage.getItem(y), n = localStorage.getItem(C);
    o && o.trim() !== "" && i.append("uid", o), n && n.trim() !== "" && i.append("visitorUid", n), Object.entries(this.config.chatConfig || {}).forEach(([s, a]) => {
      if (s === "goodsInfo" || s === "orderInfo")
        try {
          typeof a == "string" ? i.append(s, a) : i.append(s, JSON.stringify(a));
        } catch (c) {
          console.error(`Error processing ${s}:`, c);
        }
      else if (s === "extra")
        try {
          let c = typeof a == "string" ? JSON.parse(a) : a;
          c.goodsInfo && delete c.goodsInfo, c.orderInfo && delete c.orderInfo, Object.keys(c).length > 0 && i.append(s, JSON.stringify(c));
        } catch (c) {
          console.error("Error processing extra parameter:", c);
        }
      else
        i.append(s, String(a));
    }), Object.entries(this.config.browseConfig || {}).forEach(([s, a]) => {
      i.append(s, String(a));
    }), Object.entries(this.config.theme || {}).forEach(([s, a]) => {
      i.append(s, String(a));
    }), i.append("lang", this.config.locale || "zh-cn"), t && i.append("preload", "1");
    const l = `${this.config.baseUrl}?${i.toString()}`;
    return console.log("chat url: ", l), l;
  }
  setupMessageListener() {
    window.addEventListener("message", (t) => {
      switch (t.data.type) {
        case G:
          this.hideChat();
          break;
        case Y:
          this.toggleMaximize();
          break;
        case N:
          this.minimizeWindow();
          break;
        case j:
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
    var l, s;
    const { uid: e, visitorUid: i } = t.data;
    console.log("handleLocalStorageData 被调用", e, i, t.data);
    const o = localStorage.getItem(y), n = localStorage.getItem(C);
    if (o === e && n === i) {
      console.log("handleLocalStorageData: 值相同，跳过设置");
      return;
    }
    localStorage.setItem(y, e), localStorage.setItem(C, i), console.log("handleLocalStorageData: 已更新localStorage", {
      uid: e,
      visitorUid: i
    }), (s = (l = this.config).onVisitorInfo) == null || s.call(l, e, i);
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
        const s = this.bubble.messageElement;
        s instanceof HTMLElement && (s.style.display = ((i = this.config.bubbleConfig) == null ? void 0 : i.show) === !1 ? "none" : "block");
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
      var l, s;
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
        let a = this.windowState === "maximized" ? o : Math.min(
          ((l = this.config.window) == null ? void 0 : l.width) || o * 0.9,
          o * 0.9
        ), c = this.windowState === "maximized" ? n : Math.min(
          ((s = this.config.window) == null ? void 0 : s.height) || n * 0.9,
          n * 0.9
        );
        const g = this.config.placement === "bottom-right" ? this.config.marginSide : void 0, d = this.config.placement === "bottom-left" ? this.config.marginSide : void 0;
        Object.assign(this.window.style, {
          width: `${a}px`,
          height: `${c}px`,
          right: g ? `${g}px` : "auto",
          left: d ? `${d}px` : "auto",
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
    var s, a, c, g, d, b;
    if (this.inviteDialog && document.body.contains(this.inviteDialog)) {
      console.log("邀请框已存在，不重复创建");
      return;
    }
    const t = ((s = this.config.theme) == null ? void 0 : s.mode) === "dark";
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
    `, (a = this.config.inviteConfig) != null && a.icon) {
      const h = document.createElement("div");
      h.style.cssText = `
        font-size: 32px;
        margin-bottom: 12px;
        color: ${t ? "#e5e7eb" : "#333"};
      `, h.textContent = this.config.inviteConfig.icon, this.inviteDialog.appendChild(h);
    }
    const e = document.createElement("div");
    e.style.cssText = `
      margin-bottom: 16px;
      color: ${t ? "#e5e7eb" : "#333"};
    `, e.textContent = ((c = this.config.inviteConfig) == null ? void 0 : c.text) || "需要帮助吗？点击开始对话", this.inviteDialog.appendChild(e);
    const i = document.createElement("div");
    i.style.cssText = `
      display: flex;
      gap: 10px;
      justify-content: center;
    `;
    const o = document.createElement("button");
    o.textContent = ((g = this.config.inviteConfig) == null ? void 0 : g.acceptText) || "开始对话";
    const n = ((d = this.config.theme) == null ? void 0 : d.backgroundColor) || (t ? "#3B82F6" : "#0066FF");
    o.style.cssText = `
      padding: 8px 16px;
      background: ${n};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, o.onclick = () => {
      var h, m;
      this.hideInviteDialog(), this.showChat(), (m = (h = this.config.inviteConfig) == null ? void 0 : h.onAccept) == null || m.call(h);
    };
    const l = document.createElement("button");
    l.textContent = ((b = this.config.inviteConfig) == null ? void 0 : b.rejectText) || "稍后再说", l.style.cssText = `
      padding: 8px 16px;
      background: ${t ? "#374151" : "#f5f5f5"};
      color: ${t ? "#d1d5db" : "#666"};
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, l.onclick = () => {
      var h, m;
      this.hideInviteDialog(), (m = (h = this.config.inviteConfig) == null ? void 0 : h.onReject) == null || m.call(h), this.handleInviteLoop();
    }, i.appendChild(o), i.appendChild(l), this.inviteDialog.appendChild(i), document.body.appendChild(this.inviteDialog);
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
