var y = Object.defineProperty;
var C = (b, t, e) => t in b ? y(b, t, { enumerable: !0, configurable: !0, writable: !0, value: e }) : b[t] = e;
var g = (b, t, e) => C(b, typeof t != "symbol" ? t + "" : t, e);
class E {
  constructor(t) {
    g(this, "config");
    g(this, "bubble", null);
    g(this, "window", null);
    g(this, "isVisible", !1);
    g(this, "isDragging", !1);
    g(this, "windowState", "normal");
    this.config = {
      ...this.getDefaultConfig(),
      ...t
    };
  }
  getDefaultConfig() {
    return {
      baseUrl: "http://127.0.0.1:9006",
      placement: "bottom-right",
      marginBottom: 20,
      marginSide: 20,
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
      locale: "zh-CN"
    };
  }
  init() {
    this.createBubble(), this.setupMessageListener(), this.setupResizeListener();
  }
  createBubble() {
    var s, o, r, c, m;
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
    if ((s = this.config.bubbleConfig) != null && s.show) {
      e = document.createElement("div"), e.style.cssText = `
        background: white;
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
      `;
      const l = document.createElement("span");
      l.textContent = ((o = this.config.bubbleConfig) == null ? void 0 : o.icon) || "", l.style.fontSize = "20px", a.appendChild(l);
      const d = document.createElement("div"), h = document.createElement("div");
      h.textContent = ((r = this.config.bubbleConfig) == null ? void 0 : r.title) || "", h.style.fontWeight = "bold", h.style.marginBottom = "4px", d.appendChild(h);
      const i = document.createElement("div");
      i.textContent = ((c = this.config.bubbleConfig) == null ? void 0 : c.subtitle) || "", i.style.fontSize = "0.9em", i.style.opacity = "0.8", d.appendChild(i), a.appendChild(d), e.appendChild(a);
      const f = document.createElement("div");
      f.style.cssText = `
        position: absolute;
        bottom: -6px;
        ${this.config.placement === "bottom-left" ? "left: 24px" : "right: 24px"};
        width: 12px;
        height: 12px;
        background: white;
        transform: rotate(45deg);
        box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      `;
      const p = document.createElement("div");
      p.style.cssText = `
        position: absolute;
        bottom: 0;
        ${this.config.placement === "bottom-left" ? "left: 18px" : "right: 18px"};
        width: 24px;
        height: 12px;
        background: white;
      `, e.appendChild(f), e.appendChild(p), t.appendChild(e), setTimeout(() => {
        e && (e.style.opacity = "1", e.style.transform = "translateY(0)");
      }, 500);
    }
    this.bubble = document.createElement("button"), this.bubble.style.cssText = `
      background-color: ${(m = this.config.theme) == null ? void 0 : m.backgroundColor};
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
    const n = document.createElement("div");
    if (n.innerHTML = `
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
              fill="white"/>
      </svg>
    `, n.style.cssText = `
      display: flex;
      align-items: center;
      justify-content: center;
    `, this.bubble.appendChild(n), this.bubble.addEventListener("mouseenter", () => {
      this.bubble.style.transform = "scale(1.1)";
    }), this.bubble.addEventListener("mouseleave", () => {
      this.bubble.style.transform = "scale(1)";
    }), t.appendChild(this.bubble), this.config.draggable) {
      let a = 0, l = 0, d = 0, h = 0;
      this.bubble.addEventListener("mousedown", (i) => {
        i.button === 0 && (this.isDragging = !0, a = i.clientX, l = i.clientY, d = t.offsetLeft, h = t.offsetTop, t.style.transition = "none");
      }), document.addEventListener("mousemove", (i) => {
        if (!this.isDragging) return;
        i.preventDefault();
        const f = i.clientX - a, p = i.clientY - l, w = d + f, u = h + p, x = window.innerHeight - t.offsetHeight;
        w <= window.innerWidth / 2 ? (t.style.left = `${Math.max(0, w)}px`, t.style.right = "auto", this.config.placement = "bottom-left") : (t.style.right = `${Math.max(0, window.innerWidth - w - t.offsetWidth)}px`, t.style.left = "auto", this.config.placement = "bottom-right"), t.style.bottom = `${Math.min(Math.max(0, window.innerHeight - u - t.offsetHeight), x)}px`;
      }), document.addEventListener("mouseup", () => {
        this.isDragging && (this.isDragging = !1, t.style.transition = "all 0.3s ease", this.config.marginSide = parseInt(
          this.config.placement === "bottom-left" ? t.style.left : t.style.right
        ) || 20, this.config.marginBottom = parseInt(t.style.bottom || "20"));
      });
    }
    this.bubble.addEventListener("mousedown", () => {
    }), this.bubble.addEventListener("mousemove", () => {
    }), this.bubble.addEventListener("click", () => {
      this.isDragging || this.showChat();
    }), this.bubble.messageElement = e, document.body.appendChild(t);
  }
  getSupportText() {
    const t = this.config.locale || "zh-CN", e = {
      "zh-CN": "微语技术支持",
      "en-US": "Powered by Weiyuai",
      "ja-JP": "Weiyuaiによる技術支援",
      "ko-KR": "Weiyuai 기술 지원"
    };
    return e[t] || e["zh-CN"];
  }
  createChatWindow() {
    var c, m, a, l, d, h;
    this.window = document.createElement("div");
    const t = window.innerWidth <= 768, e = window.innerWidth, n = window.innerHeight, s = Math.min(((c = this.config.window) == null ? void 0 : c.width) || e * 0.9, e * 0.9), o = Math.min(((m = this.config.window) == null ? void 0 : m.height) || n * 0.9, n * 0.9);
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
        transition: all ${(a = this.config.animation) == null ? void 0 : a.duration}ms ${(l = this.config.animation) == null ? void 0 : l.type};
      ` : this.window.style.cssText = `
        position: fixed;
        ${this.config.placement === "bottom-right" ? "right" : "left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${s}px;
        height: ${o}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(d = this.config.animation) == null ? void 0 : d.duration}ms ${(h = this.config.animation) == null ? void 0 : h.type};
      `;
    const r = document.createElement("iframe");
    if (r.style.cssText = `
      width: 100%;
      height: ${this.config.showSupport ? "calc(100% - 30px)" : "100%"};
      border: none;
    `, r.src = this.generateChatUrl(), this.window.appendChild(r), this.config.showSupport) {
      const i = document.createElement("div");
      i.style.cssText = `
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #666;
        font-size: 12px;
        line-height: 30px;
      `, i.innerHTML = `
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
      `, this.window.appendChild(i);
    }
    document.body.appendChild(this.window);
  }
  generateChatUrl(t = "messages") {
    console.log("this.config: ", this.config, t);
    const e = new URLSearchParams();
    Object.entries(this.config.theme || {}).forEach(([s, o]) => {
      e.append(s, String(o));
    }), Object.entries(this.config.chatParams || {}).forEach(([s, o]) => {
      e.append(s, String(o));
    });
    let n = `${this.config.baseUrl}?${e.toString()}`;
    return console.log("chatUrl: ", n), n;
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
      }
    });
  }
  showChat() {
    if (this.window || this.createChatWindow(), this.window) {
      const t = window.innerWidth <= 768;
      if (this.window.style.display = "block", this.setupResizeListener(), t && this.window && (this.window.style.transform = "translateY(100%)", requestAnimationFrame(() => {
        this.window && (this.window.style.transform = "translateY(0)");
      })), this.isVisible = !0, this.bubble) {
        this.bubble.style.display = "none";
        const e = this.bubble.messageElement;
        e instanceof HTMLElement && (e.style.display = "none");
      }
    }
  }
  hideChat() {
    var t;
    if (this.window && (window.innerWidth <= 768 ? (this.window.style.transform = "translateY(100%)", setTimeout(() => {
      this.window && (this.window.style.display = "none");
    }, ((t = this.config.animation) == null ? void 0 : t.duration) || 300)) : this.window.style.display = "none", this.isVisible = !1, this.bubble)) {
      this.bubble.style.display = "inline-flex";
      const n = this.bubble.messageElement;
      n instanceof HTMLElement && (n.style.display = "block");
    }
  }
  minimizeWindow() {
    this.window && (this.windowState = "minimized", this.window.style.display = "none");
  }
  toggleMaximize() {
    !this.window || window.innerWidth <= 768 || (this.windowState = this.windowState === "maximized" ? "normal" : "maximized", this.setupResizeListener());
  }
  setupResizeListener() {
    const t = () => {
      var r, c;
      if (!this.window || !this.isVisible) return;
      const n = window.innerWidth <= 768, s = window.innerWidth, o = window.innerHeight;
      if (n)
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
        let m = this.windowState === "maximized" ? s : Math.min(((r = this.config.window) == null ? void 0 : r.width) || s * 0.9, s * 0.9), a = this.windowState === "maximized" ? o : Math.min(((c = this.config.window) == null ? void 0 : c.height) || o * 0.9, o * 0.9);
        const l = this.config.placement === "bottom-right" ? this.config.marginSide : void 0, d = this.config.placement === "bottom-left" ? this.config.marginSide : void 0;
        Object.assign(this.window.style, {
          width: `${m}px`,
          height: `${a}px`,
          right: l ? `${l}px` : "auto",
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
    t && document.body.contains(t) && (document.body.removeChild(t), this.bubble = null), this.window && document.body.contains(this.window) && (document.body.removeChild(this.window), this.window = null), window.removeEventListener("resize", this.setupResizeListener.bind(this));
  }
}
export {
  E as default
};
