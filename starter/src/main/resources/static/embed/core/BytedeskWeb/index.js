var D = Object.defineProperty;
var k = (f, t, e) => t in f ? D(f, t, { enumerable: !0, configurable: !0, writable: !0, value: e }) : f[t] = e;
var r = (f, t, e) => k(f, typeof t != "symbol" ? t + "" : t, e);
class $ {
  constructor(t) {
    r(this, "config");
    r(this, "bubble", null);
    r(this, "window", null);
    r(this, "inviteDialog", null);
    r(this, "isVisible", !1);
    r(this, "isDragging", !1);
    r(this, "windowState", "normal");
    r(this, "loopCount", 0);
    r(this, "loopTimer", null);
    this.config = {
      ...this.getDefaultConfig(),
      ...t
    };
  }
  getDefaultConfig() {
    return {
      isDebug: !1,
      isPreload: !1,
      baseUrl: "https://www.weiyuai.cn/chat",
      placement: "bottom-right",
      marginBottom: 20,
      marginSide: 20,
      autoPopup: !1,
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
      showSupport: !0,
      chatParams: {
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
    this.createBubble(), this.createInviteDialog(), this.setupMessageListener(), this.setupResizeListener(), this.preload(), this.config.autoPopup && setTimeout(() => {
      this.showChat();
    }, this.config.autoPopupDelay || 1e3), (t = this.config.inviteParams) != null && t.show && setTimeout(() => {
      this.showInviteDialog();
    }, this.config.inviteParams.delay || 3e3);
  }
  createBubble() {
    var n, s, a, o, l, p, c, m, x, b, E;
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
    if ((n = this.config.bubbleConfig) != null && n.show) {
      e = document.createElement("div"), e.style.cssText = `
        background: ${((s = this.config.theme) == null ? void 0 : s.mode) === "dark" ? "#1f2937" : "white"};
        color: ${((a = this.config.theme) == null ? void 0 : a.mode) === "dark" ? "#e5e7eb" : "#1f2937"};
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
      const d = document.createElement("div");
      d.style.cssText = `
        display: flex;
        align-items: center;
        gap: 8px;
      `;
      const u = document.createElement("span");
      u.textContent = ((o = this.config.bubbleConfig) == null ? void 0 : o.icon) || "", u.style.fontSize = "20px", d.appendChild(u);
      const w = document.createElement("div"), g = document.createElement("div");
      g.textContent = ((l = this.config.bubbleConfig) == null ? void 0 : l.title) || "", g.style.fontWeight = "bold", g.style.color = ((p = this.config.theme) == null ? void 0 : p.mode) === "dark" ? "#e5e7eb" : "#1f2937", g.style.marginBottom = "4px", w.appendChild(g);
      const h = document.createElement("div");
      h.textContent = ((c = this.config.bubbleConfig) == null ? void 0 : c.subtitle) || "", h.style.fontSize = "0.9em", h.style.color = ((m = this.config.theme) == null ? void 0 : m.mode) === "dark" ? "#9ca3af" : "#4b5563", w.appendChild(h), d.appendChild(w), e.appendChild(d);
      const y = document.createElement("div");
      y.style.cssText = `
        position: absolute;
        bottom: -6px;
        ${this.config.placement === "bottom-left" ? "left: 24px" : "right: 24px"};
        width: 12px;
        height: 12px;
        background: ${((x = this.config.theme) == null ? void 0 : x.mode) === "dark" ? "#1f2937" : "white"};
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
        background: ${((b = this.config.theme) == null ? void 0 : b.mode) === "dark" ? "#1f2937" : "white"};
      `, e.appendChild(y), e.appendChild(v), t.appendChild(e), setTimeout(() => {
        e && (e.style.opacity = "1", e.style.transform = "translateY(0)");
      }, 500);
    }
    this.bubble = document.createElement("button"), this.bubble.style.cssText = `
      background-color: ${(E = this.config.theme) == null ? void 0 : E.backgroundColor};
      width: 60px;
      height: 60px;
      border-radius: 30px;
      border: none;
      cursor: ${this.config.draggable ? "move" : "pointer"};
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
    `;
    const i = document.createElement("div");
    if (i.innerHTML = `
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
              fill="white"/>
      </svg>
    `, i.style.cssText = `
      display: flex;
      align-items: center;
      justify-content: center;
    `, this.bubble.appendChild(i), this.bubble.addEventListener("mouseenter", () => {
      this.bubble.style.transform = "scale(1.1)";
    }), this.bubble.addEventListener("mouseleave", () => {
      this.bubble.style.transform = "scale(1)";
    }), t.appendChild(this.bubble), this.config.draggable) {
      let d = 0, u = 0, w = 0, g = 0;
      this.bubble.addEventListener("mousedown", (h) => {
        h.button === 0 && (this.isDragging = !0, d = h.clientX, u = h.clientY, w = t.offsetLeft, g = t.offsetTop, t.style.transition = "none");
      }), document.addEventListener("mousemove", (h) => {
        if (!this.isDragging) return;
        h.preventDefault();
        const y = h.clientX - d, v = h.clientY - u, C = w + y, T = g + v, I = window.innerHeight - t.offsetHeight;
        C <= window.innerWidth / 2 ? (t.style.left = `${Math.max(0, C)}px`, t.style.right = "auto", this.config.placement = "bottom-left") : (t.style.right = `${Math.max(0, window.innerWidth - C - t.offsetWidth)}px`, t.style.left = "auto", this.config.placement = "bottom-right"), t.style.bottom = `${Math.min(Math.max(0, window.innerHeight - T - t.offsetHeight), I)}px`;
      }), document.addEventListener("mouseup", () => {
        this.isDragging && (this.isDragging = !1, t.style.transition = "all 0.3s ease", this.config.marginSide = parseInt(
          this.config.placement === "bottom-left" ? t.style.left : t.style.right
        ) || 20, this.config.marginBottom = parseInt(t.style.bottom || "20"));
      });
    }
    this.bubble.addEventListener("click", () => {
      if (!this.isDragging) {
        console.log("bubble click");
        const d = this.bubble.messageElement;
        d instanceof HTMLElement && (d.style.display = "none"), this.showChat();
      }
    }), this.bubble.messageElement = e, document.body.appendChild(t);
  }
  getSupportText() {
    const t = this.config.locale || "zh-cn", e = {
      "zh-cn": "微语技术支持",
      en: "Powered by Weiyuai",
      "ja-JP": "Weiyuaiによる技術支援",
      "ko-KR": "Weiyuai 기술 지원"
    };
    return e[t] || e["zh-cn"];
  }
  createChatWindow() {
    var o, l, p, c, m, x;
    this.window = document.createElement("div");
    const t = window.innerWidth <= 768, e = window.innerWidth, i = window.innerHeight, n = Math.min(((o = this.config.window) == null ? void 0 : o.width) || e * 0.9, e * 0.9), s = Math.min(((l = this.config.window) == null ? void 0 : l.height) || i * 0.9, i * 0.9);
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
        transition: all ${(p = this.config.animation) == null ? void 0 : p.duration}ms ${(c = this.config.animation) == null ? void 0 : c.type};
      ` : this.window.style.cssText = `
        position: fixed;
        ${this.config.placement === "bottom-right" ? "right" : "left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${n}px;
        height: ${s}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(m = this.config.animation) == null ? void 0 : m.duration}ms ${(x = this.config.animation) == null ? void 0 : x.type};
      `;
    const a = document.createElement("iframe");
    if (a.style.cssText = `
      width: 100%;
      height: ${this.config.showSupport ? "calc(100% - 30px)" : "100%"};
      border: none;
    `, a.src = this.generateChatUrl(), console.log("iframe.src: ", a.src), this.window.appendChild(a), this.config.showSupport) {
      const b = document.createElement("div");
      b.style.cssText = `
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #666;
        font-size: 12px;
        line-height: 30px;
      `, b.innerHTML = `
        <a href="https://ai.bytedesk.com" 
           target="_blank" 
           style="
             color: #666;
             text-decoration: none;
             display: flex;
             align-items: center;
             height: 100%;
           ">
          ${this.getSupportText()}
        </a>
      `, this.window.appendChild(b);
    }
    document.body.appendChild(this.window);
  }
  generateChatUrl(t = !1, e = "messages") {
    console.log("this.config: ", this.config, e);
    const i = new URLSearchParams();
    return Object.entries(this.config.chatParams || {}).forEach(([n, s]) => {
      i.append(n, String(s));
    }), Object.entries(this.config.browseParams || {}).forEach(([n, s]) => {
      i.append(n, String(s));
    }), Object.entries(this.config.theme || {}).forEach(([n, s]) => {
      i.append(n, String(s));
    }), i.append("lang", this.config.locale || "zh-cn"), t && i.append("preload", "1"), `${this.config.baseUrl}?${i.toString()}`;
  }
  setupMessageListener() {
    window.addEventListener("message", (t) => {
      switch (t.data.type) {
        case "CLOSE_CHAT_WINDOW":
          this.hideChat();
          break;
        case "MAXIMIZE_WINDOW":
          this.toggleMaximize();
          break;
        case "MINIMIZE_WINDOW":
          this.minimizeWindow();
          break;
        case "RECEIVE_MESSAGE":
          console.log("RECEIVE_MESSAGE");
          break;
        case "INVITE_VISITOR":
          console.log("INVITE_VISITOR");
          break;
        case "INVITE_VISITOR_ACCEPT":
          console.log("INVITE_VISITOR_ACCEPT");
          break;
        case "INVITE_VISITOR_REJECT":
          console.log("INVITE_VISITOR_REJECT");
          break;
      }
    });
  }
  preload() {
    if (console.log("preload"), this.config.isPreload) {
      const t = this.generateChatUrl(!0);
      console.log("preLoadUrl: ", t);
      const e = document.createElement("iframe");
      e.src = t, e.style.display = "none", document.body.appendChild(e);
    }
  }
  showChat() {
    var t, e;
    if (this.window || this.createChatWindow(), this.window) {
      const i = window.innerWidth <= 768;
      if (this.window.style.display = "block", this.setupResizeListener(), i && this.window && (this.window.style.transform = "translateY(100%)", requestAnimationFrame(() => {
        this.window && (this.window.style.transform = "translateY(0)");
      })), this.isVisible = !0, this.bubble) {
        this.bubble.style.display = "none";
        const n = this.bubble.messageElement;
        n instanceof HTMLElement && (n.style.display = "none");
      }
    }
    this.hideInviteDialog(), (e = (t = this.config).onShowChat) == null || e.call(t);
  }
  hideChat() {
    var t, e, i;
    if (this.window) {
      if (window.innerWidth <= 768 ? (this.window.style.transform = "translateY(100%)", setTimeout(() => {
        this.window && (this.window.style.display = "none");
      }, ((t = this.config.animation) == null ? void 0 : t.duration) || 300)) : this.window.style.display = "none", this.isVisible = !1, this.bubble) {
        this.bubble.style.display = "inline-flex";
        const s = this.bubble.messageElement;
        s instanceof HTMLElement && (s.style.display = "block");
      }
      (i = (e = this.config).onHideChat) == null || i.call(e);
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
      var a, o;
      if (!this.window || !this.isVisible) return;
      const i = window.innerWidth <= 768, n = window.innerWidth, s = window.innerHeight;
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
        let l = this.windowState === "maximized" ? n : Math.min(((a = this.config.window) == null ? void 0 : a.width) || n * 0.9, n * 0.9), p = this.windowState === "maximized" ? s : Math.min(((o = this.config.window) == null ? void 0 : o.height) || s * 0.9, s * 0.9);
        const c = this.config.placement === "bottom-right" ? this.config.marginSide : void 0, m = this.config.placement === "bottom-left" ? this.config.marginSide : void 0;
        Object.assign(this.window.style, {
          width: `${l}px`,
          height: `${p}px`,
          right: c ? `${c}px` : "auto",
          left: m ? `${m}px` : "auto",
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
    t && document.body.contains(t) && (document.body.removeChild(t), this.bubble = null), this.window && document.body.contains(this.window) && (document.body.removeChild(this.window), this.window = null), window.removeEventListener("resize", this.setupResizeListener.bind(this)), this.loopTimer && (window.clearTimeout(this.loopTimer), this.loopTimer = null), this.inviteDialog && document.body.contains(this.inviteDialog) && (document.body.removeChild(this.inviteDialog), this.inviteDialog = null);
  }
  createInviteDialog() {
    var s, a;
    if (!((s = this.config.inviteParams) != null && s.show)) return;
    if (this.inviteDialog = document.createElement("div"), this.inviteDialog.style.cssText = `
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background: white;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
      z-index: 10001;
      display: none;
      max-width: 300px;
      text-align: center;
    `, this.config.inviteParams.icon) {
      const o = document.createElement("div");
      o.style.cssText = `
        font-size: 32px;
        margin-bottom: 12px;
      `, o.textContent = this.config.inviteParams.icon, this.inviteDialog.appendChild(o);
    }
    const t = document.createElement("div");
    t.style.cssText = `
      margin-bottom: 16px;
      color: #333;
    `, t.textContent = this.config.inviteParams.text || "需要帮助吗？点击开始对话", this.inviteDialog.appendChild(t);
    const e = document.createElement("div");
    e.style.cssText = `
      display: flex;
      gap: 10px;
      justify-content: center;
    `;
    const i = document.createElement("button");
    i.textContent = "开始对话", i.style.cssText = `
      padding: 8px 16px;
      background: ${((a = this.config.theme) == null ? void 0 : a.backgroundColor) || "#0066FF"};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, i.onclick = () => {
      var o, l;
      this.hideInviteDialog(), this.showChat(), (l = (o = this.config.inviteParams) == null ? void 0 : o.onAccept) == null || l.call(o);
    };
    const n = document.createElement("button");
    n.textContent = "稍后再说", n.style.cssText = `
      padding: 8px 16px;
      background: #f5f5f5;
      color: #666;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, n.onclick = () => {
      var o, l;
      this.hideInviteDialog(), (l = (o = this.config.inviteParams) == null ? void 0 : o.onReject) == null || l.call(o), this.handleInviteLoop();
    }, e.appendChild(i), e.appendChild(n), this.inviteDialog.appendChild(e), document.body.appendChild(this.inviteDialog);
  }
  showInviteDialog() {
    var t, e;
    this.inviteDialog && (this.inviteDialog.style.display = "block", (e = (t = this.config.inviteParams) == null ? void 0 : t.onOpen) == null || e.call(t));
  }
  hideInviteDialog() {
    var t, e;
    this.inviteDialog && (this.inviteDialog.style.display = "none", (e = (t = this.config.inviteParams) == null ? void 0 : t.onClose) == null || e.call(t));
  }
  handleInviteLoop() {
    const { loop: t, loopDelay: e = 3e3, loopCount: i = 1 / 0 } = this.config.inviteParams || {};
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
}
export {
  $ as default
};
