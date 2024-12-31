var m = Object.defineProperty;
var b = (l, t, i) => t in l ? m(l, t, { enumerable: !0, configurable: !0, writable: !0, value: i }) : l[t] = i;
var h = (l, t, i) => b(l, typeof t != "symbol" ? t + "" : t, i);
class w {
  constructor(t) {
    h(this, "config");
    h(this, "bubble", null);
    h(this, "window", null);
    h(this, "isVisible", !1);
    h(this, "isDragging", !1);
    h(this, "windowState", "normal");
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
      navbarPreset: "light",
      customColor: "#000000",
      navbarColor: "#ffffff",
      navbarTextColor: "#333333",
      animation: {
        enabled: !0,
        duration: 300,
        type: "ease"
      },
      theme: {
        primaryColor: "#2e88ff",
        secondaryColor: "#ffffff",
        textColor: "#333333",
        backgroundColor: "#ffffff",
        position: "right",
        navbar: {
          backgroundColor: "#ffffff",
          textColor: "#333333"
        }
      },
      window: {
        title: "在线客服",
        width: 380,
        height: 640,
        position: "right"
      },
      draggable: !1
    };
  }
  init() {
    this.createBubble(), this.setupMessageListener(), this.setupResizeListener();
  }
  createBubble() {
    const t = document.createElement("div");
    if (t.style.cssText = `
      position: fixed;
      ${this.config.theme.position === "left" ? "left" : "right"}: ${this.config.marginSide}px;
      bottom: ${this.config.marginBottom}px;
      display: flex;
      flex-direction: column;
      align-items: ${this.config.theme.position === "left" ? "flex-start" : "flex-end"};
      gap: 10px;
      z-index: 9999;
    `, this.config.bubbleConfig.show) {
      const n = document.createElement("div");
      n.style.cssText = `
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
      const o = document.createElement("div");
      o.style.cssText = `
        display: flex;
        align-items: center;
        gap: 8px;
      `;
      const a = document.createElement("span");
      a.textContent = this.config.bubbleConfig.icon, a.style.fontSize = "20px", o.appendChild(a);
      const s = document.createElement("div"), e = document.createElement("div");
      e.textContent = this.config.bubbleConfig.title, e.style.fontWeight = "bold", e.style.marginBottom = "4px", s.appendChild(e);
      const d = document.createElement("div");
      d.textContent = this.config.bubbleConfig.subtitle, d.style.fontSize = "0.9em", d.style.opacity = "0.8", s.appendChild(d), o.appendChild(s), n.appendChild(o);
      const r = document.createElement("div");
      r.style.cssText = `
        position: absolute;
        bottom: -6px;
        ${this.config.theme.position === "left" ? "left: 24px" : "right: 24px"};
        width: 12px;
        height: 12px;
        background: white;
        transform: rotate(45deg);
        box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      `;
      const c = document.createElement("div");
      c.style.cssText = `
        position: absolute;
        bottom: 0;
        ${this.config.theme.position === "left" ? "left: 18px" : "right: 18px"};
        width: 24px;
        height: 12px;
        background: white;
      `, n.appendChild(r), n.appendChild(c), t.appendChild(n), setTimeout(() => {
        n.style.opacity = "1", n.style.transform = "translateY(0)";
      }, 500);
    }
    this.bubble = document.createElement("button"), this.bubble.style.cssText = `
      background-color: ${this.config.theme.primaryColor};
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
      let n = 0, o = 0, a = 0, s = 0;
      this.bubble.addEventListener("mousedown", (e) => {
        e.button === 0 && (this.isDragging = !0, n = e.clientX, o = e.clientY, a = t.offsetLeft, s = t.offsetTop, t.style.transition = "none");
      }), document.addEventListener("mousemove", (e) => {
        if (!this.isDragging) return;
        e.preventDefault();
        const d = e.clientX - n, r = e.clientY - o, c = a + d, g = s + r, f = window.innerHeight - t.offsetHeight;
        c <= window.innerWidth / 2 ? (t.style.left = `${Math.max(0, c)}px`, t.style.right = "auto", this.config.theme.position = "left") : (t.style.right = `${Math.max(0, window.innerWidth - c - t.offsetWidth)}px`, t.style.left = "auto", this.config.theme.position = "right"), t.style.bottom = `${Math.min(Math.max(0, window.innerHeight - g - t.offsetHeight), f)}px`;
      }), document.addEventListener("mouseup", () => {
        this.isDragging && (this.isDragging = !1, t.style.transition = "all 0.3s ease", this.config.marginSide = parseInt(
          this.config.theme.position === "left" ? t.style.left : t.style.right
        ) || 20, this.config.marginBottom = parseInt(t.style.bottom || "20"));
      });
    }
    this.bubble.addEventListener("mousedown", () => {
    }), this.bubble.addEventListener("mousemove", () => {
    }), this.bubble.addEventListener("click", () => {
      this.isDragging || this.showChat();
    }), document.body.appendChild(t);
  }
  createChatWindow() {
    this.window = document.createElement("div");
    const t = window.innerWidth <= 768, i = window.innerWidth, n = window.innerHeight, o = Math.min(this.config.window.width, i * 0.9), a = Math.min(this.config.window.height, n * 0.9);
    if (t)
      this.window.style.cssText = `
        position: fixed;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 90vh;
        background: ${this.config.theme.backgroundColor};
        display: none;
        z-index: 10000;
        border-top-left-radius: 12px;
        border-top-right-radius: 12px;
        overflow: hidden;
        transition: all ${this.config.animation.duration}ms ${this.config.animation.type};
      `;
    else {
      const e = this.config.theme.position || "right";
      this.window.style.cssText = `
        position: fixed;
        ${e}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${o}px;
        height: ${a}px;
        background: ${this.config.theme.backgroundColor};
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${this.config.animation.duration}ms ${this.config.animation.type};
      `;
    }
    const s = document.createElement("iframe");
    if (s.style.cssText = `
      width: 100%;
      height: ${this.config.showSupport ? "calc(100% - 30px)" : "100%"};
      border: none;
      background: ${this.config.theme.backgroundColor};
    `, s.src = this.generateChatUrl(), this.window.appendChild(s), this.config.showSupport) {
      const e = document.createElement("div");
      e.style.cssText = `
        height: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: ${this.config.theme.backgroundColor};
        color: #666;
        font-size: 12px;
        border-top: 1px solid #eee;
      `, e.innerHTML = `
        <span>
            <a href="https://ai.bytedesk.com" 
             target="_blank" 
             style="color: #666; text-decoration: none;">
            bytedesk.com
          </a>
        </span>
      `, this.window.appendChild(e);
    }
    document.body.appendChild(this.window);
  }
  generateChatUrl(t = "messages") {
    const i = new URLSearchParams();
    return i.append("tab", t), i.append("theme", JSON.stringify(this.config.theme)), i.append("window", JSON.stringify(this.config.window)), Object.entries(this.config.chatParams).forEach(([n, o]) => {
      i.append(n, String(o));
    }), `${this.config.baseUrl}?${i.toString()}`;
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
      this.window.style.display = "block", this.setupResizeListener(), t && this.window && (this.window.style.transform = "translateY(100%)", requestAnimationFrame(() => {
        this.window && (this.window.style.transform = "translateY(0)");
      })), this.isVisible = !0, this.bubble && (this.bubble.style.display = "none");
    }
  }
  hideChat() {
    this.window && (window.innerWidth <= 768 ? (this.window.style.transform = "translateY(100%)", setTimeout(() => {
      this.window && (this.window.style.display = "none");
    }, this.config.animation.duration)) : this.window.style.display = "none", this.isVisible = !1, this.bubble && (this.bubble.style.display = "inline-flex"));
  }
  minimizeWindow() {
    this.window && (this.windowState = "minimized", this.window.style.display = "none");
  }
  toggleMaximize() {
    !this.window || window.innerWidth <= 768 || (this.windowState = this.windowState === "maximized" ? "normal" : "maximized", this.setupResizeListener());
  }
  setupResizeListener() {
    const t = () => {
      if (!this.window || !this.isVisible) return;
      const n = window.innerWidth <= 768, o = window.innerWidth, a = window.innerHeight;
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
        let s = this.windowState === "maximized" ? o : Math.min(this.config.window.width, o * 0.9), e = this.windowState === "maximized" ? a : Math.min(this.config.window.height, a * 0.9);
        const d = this.config.window.position === "right" ? this.config.marginSide : void 0, r = this.config.window.position === "left" ? this.config.marginSide : void 0;
        Object.assign(this.window.style, {
          width: `${s}px`,
          height: `${e}px`,
          right: d ? `${d}px` : "auto",
          left: r ? `${r}px` : "auto",
          bottom: `${this.config.marginBottom}px`,
          borderRadius: this.windowState === "maximized" ? "0" : "12px"
        });
      }
    };
    let i;
    window.addEventListener("resize", () => {
      clearTimeout(i), i = window.setTimeout(t, 100);
    }), t();
  }
  destroy() {
    var i;
    const t = (i = this.bubble) == null ? void 0 : i.parentElement;
    t && document.body.contains(t) && (document.body.removeChild(t), this.bubble = null), this.window && document.body.contains(this.window) && (document.body.removeChild(this.window), this.window = null), window.removeEventListener("resize", this.setupResizeListener.bind(this));
  }
}
export {
  w as default
};
