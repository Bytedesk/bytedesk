var A = Object.defineProperty;
var N = (P, e, t) => e in P ? A(P, e, { enumerable: !0, configurable: !0, writable: !0, value: t }) : P[e] = t;
var u = (P, e, t) => N(P, typeof e != "symbol" ? e + "" : e, t);
import { BYTEDESK_UID as S, BYTEDESK_VISITOR_UID as W, BYTEDESK_BROWSE_LAST_TIMESTAMP as L, BYTEDESK_BROWSE_FAILED_TIMESTAMP as $, POST_MESSAGE_LOCALSTORAGE_RESPONSE as Y, POST_MESSAGE_INVITE_VISITOR_REJECT as j, POST_MESSAGE_INVITE_VISITOR_ACCEPT as q, POST_MESSAGE_INVITE_VISITOR as X, POST_MESSAGE_RECEIVE_MESSAGE as G, POST_MESSAGE_MINIMIZE_WINDOW as K, POST_MESSAGE_MAXIMIZE_WINDOW as J, POST_MESSAGE_CLOSE_CHAT_WINDOW as Z, POST_MESSAGE_RESET_ANONYMOUS_VISITOR as Q } from "../../utils/constants/index.js";
import i, { setGlobalConfig as _ } from "../../utils/logger/index.js";
import { serializeBrowseConfig as ee } from "../browseUrl/index.js";
class oe {
  constructor(e) {
    u(this, "config");
    u(this, "unreadBadgeMode", "hidden");
    u(this, "unreadBadgeCount", 0);
    u(this, "bubble", null);
    u(this, "bubbleContainer", null);
    u(this, "buttonElements", []);
    u(this, "buttonPreviewElement", null);
    u(this, "buttonPreviewHideTimer", null);
    u(this, "window", null);
    u(this, "inviteDialog", null);
    u(this, "contextMenu", null);
    u(this, "hideTimeout", null);
    u(this, "isVisible", !1);
    u(this, "isDragging", !1);
    u(this, "windowState", "normal");
    u(this, "loopCount", 0);
    u(this, "loopTimer", null);
    u(this, "isDestroyed", !1);
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
    u(this, "bubbleMessages", []);
    u(this, "bubbleMessageIndex", 0);
    u(this, "bubbleMessageTimer", null);
    u(this, "bubbleMessageTransitionTimer", null);
    u(this, "bubbleMessageViewportElement", null);
    u(this, "bubbleMessageContentElement", null);
    u(this, "bubblePendingMessageElement", null);
    u(this, "bubbleTickerTrackElement", null);
    u(this, "bubbleTickerStyleElement", null);
    u(this, "bubbleIconElement", null);
    u(this, "bubbleTitleElement", null);
    u(this, "bubbleSubtitleElement", null);
    this.config = {
      ...this.getDefaultConfig(),
      ...e
    }, _(this.config), this.setupApiUrl();
  }
  async setupApiUrl() {
    try {
      const { setApiUrl: e } = await import("../../apis/request/index.js"), t = this.config.apiUrl || "https://api.weiyuai.cn";
      e(t), i.info("API URL 已设置为:", t);
    } catch (e) {
      i.error("设置API URL时出错:", e);
    }
  }
  mergeConfig(e) {
    return {
      ...this.config,
      ...e,
      inviteConfig: {
        ...this.config.inviteConfig || {},
        ...e.inviteConfig || {}
      },
      tabsConfig: {
        ...this.config.tabsConfig || {},
        ...e.tabsConfig || {}
      },
      bubbleConfig: {
        ...this.config.bubbleConfig || {},
        ...e.bubbleConfig || {}
      },
      buttonConfig: {
        ...this.config.buttonConfig || {},
        ...e.buttonConfig || {}
      },
      feedbackConfig: {
        ...this.config.feedbackConfig || {},
        ...e.feedbackConfig || {}
      },
      chatConfig: e.chatConfig ? {
        ...this.config.chatConfig || {},
        ...e.chatConfig
      } : this.config.chatConfig,
      browseConfig: {
        ...this.config.browseConfig || {},
        ...e.browseConfig || {}
      },
      animation: {
        ...this.config.animation || {},
        ...e.animation || {}
      },
      window: {
        ...this.config.window || {},
        ...e.window || {}
      },
      theme: {
        ...this.config.theme || {},
        ...e.theme || {}
      },
      buttonsConfig: e.buttonsConfig ?? this.config.buttonsConfig
    };
  }
  refreshFloatingUi() {
    const e = !!(this.inviteDialog && document.body.contains(this.inviteDialog) && this.inviteDialog.style.display !== "none");
    this.stopBubbleMessageRotation(), this.stopBubbleMessageTransition(), this.destroyBubbleTicker(), this.hideButtonPreview(), this.bubbleContainer && document.body.contains(this.bubbleContainer) && this.bubbleContainer.remove(), this.bubbleContainer = null, this.bubble = null, this.buttonElements = [], this.bubbleMessageViewportElement = null, this.bubbleMessageContentElement = null, this.bubblePendingMessageElement = null, this.bubbleTickerTrackElement = null, this.bubbleTickerStyleElement = null, this.bubbleIconElement = null, this.bubbleTitleElement = null, this.bubbleSubtitleElement = null, this.bubbleMessages = [], this.bubbleMessageIndex = 0, this.inviteDialog && document.body.contains(this.inviteDialog) && this.inviteDialog.remove(), this.inviteDialog = null, this.createBubble(), this.createInviteDialog(), e && this.showInviteDialog();
  }
  updateChatWindowLayout() {
    var a, l;
    if (!this.window)
      return;
    const e = window.innerWidth <= 768, t = window.innerWidth, n = window.innerHeight;
    if (e) {
      Object.assign(this.window.style, {
        left: "0",
        right: "auto",
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
      return;
    }
    const o = Math.min(
      ((a = this.config.window) == null ? void 0 : a.width) || t * 0.9,
      t * 0.9
    ), s = Math.min(
      ((l = this.config.window) == null ? void 0 : l.height) || n * 0.9,
      n * 0.9
    );
    Object.assign(this.window.style, {
      width: `${o}px`,
      height: `${s}px`,
      left: this.config.placement === "bottom-left" ? `${this.config.marginSide}px` : "auto",
      right: this.config.placement === "bottom-right" ? `${this.config.marginSide}px` : "auto",
      bottom: `${this.config.marginBottom}px`,
      borderRadius: "12px",
      boxSizing: "border-box",
      paddingTop: "",
      paddingBottom: ""
    });
  }
  refreshChatIframeUrl() {
    if (!this.window)
      return;
    const e = this.window.querySelector("iframe");
    e && (e.src = this.generateChatUrl());
  }
  setTheme(e) {
    this.setConfig({
      theme: {
        ...this.config.theme || {},
        ...e
      }
    });
  }
  setConfig(e) {
    var l, r;
    const t = this.config;
    this.config = this.mergeConfig(e);
    const n = this.getPrimaryActionFromConfig(e), o = Object.prototype.hasOwnProperty.call(e, "chatPath"), s = Object.prototype.hasOwnProperty.call(e, "buttonConfig");
    o || (n ? this.syncChatPathByAction(n) : s && this.syncChatPathByAction("chat")), _(this.config), e.apiUrl && e.apiUrl !== t.apiUrl && this.setupApiUrl(), !!(this.bubbleContainer && document.body.contains(this.bubbleContainer) || this.inviteDialog && document.body.contains(this.inviteDialog)) && this.refreshFloatingUi(), this.window && document.body.contains(this.window) && (this.updateChatWindowLayout(), (e.theme || e.locale || e.chatConfig || e.htmlUrl || e.chatPath || e.threadPath || e.webrtcPath || e.callPath) && this.refreshChatIframeUrl()), (r = (l = this.config).onConfigChange) == null || r.call(l, this.config);
  }
  getPrimaryActionFromConfig(e) {
    var n;
    const t = (n = e.buttonConfig) == null ? void 0 : n.action;
    return t && ["chat", "thread", "webrtc", "call"].includes(t) ? t : null;
  }
  syncChatPathByAction(e) {
    switch (e) {
      case "thread":
        this.config.chatPath = this.normalizePath(this.config.threadPath, "/chat/thread");
        break;
      case "webrtc":
        this.config.chatPath = this.normalizePath(this.config.webrtcPath, "/webrtc");
        break;
      case "call":
        this.config.chatPath = this.normalizePath(this.config.callPath, "/call");
        break;
      case "chat":
      default:
        this.config.chatPath = "/chat";
        break;
    }
  }
  getDefaultConfig() {
    return {
      isDebug: !1,
      // isPreload: false,
      forceRefresh: !1,
      htmlUrl: "https://cdn.weiyuai.cn",
      apiUrl: "https://api.weiyuai.cn",
      chatPath: "/chat",
      threadPath: "/chat/thread",
      webrtcPath: "/webrtc",
      callPath: "/call",
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
  getEffectiveButtonConfigs() {
    const e = Array.isArray(this.config.buttonsConfig) ? this.config.buttonsConfig.filter((t) => !!t) : [];
    return e.length > 0 ? e : [this.config.buttonConfig || {}];
  }
  hasVisibleButtons() {
    return this.getEffectiveButtonConfigs().some((e) => e.show !== !1);
  }
  isMultiButtonLayout(e) {
    return (e || this.getEffectiveButtonConfigs()).filter((n) => n.show !== !1).length > 1;
  }
  applyConfiguredButtonVisibility() {
    const e = this.getEffectiveButtonConfigs();
    this.buttonElements.forEach((t, n) => {
      const o = e[n];
      t.style.display = (o == null ? void 0 : o.show) === !1 ? "none" : "flex";
    });
  }
  hideBubbleMessageElement() {
    var t;
    const e = (t = this.bubble) == null ? void 0 : t.messageElement;
    e instanceof HTMLElement && (this.stopBubbleMessageTransition(), e.style.display = "none", this.stopBubbleMessageRotation());
  }
  triggerButtonAction(e) {
    if (e.onClick) {
      e.onClick();
      return;
    }
    switch (e.action) {
      case "thread":
        this.showThread();
        break;
      case "webrtc":
        this.showWebrtc();
        break;
      case "call":
        this.showCall();
        break;
      case "chat":
      default:
        this.showChat();
        break;
    }
  }
  hideButtonPreview() {
    var e;
    this.buttonPreviewHideTimer && (window.clearTimeout(this.buttonPreviewHideTimer), this.buttonPreviewHideTimer = null), (e = this.buttonPreviewElement) != null && e.parentElement && this.buttonPreviewElement.parentElement.removeChild(this.buttonPreviewElement), this.buttonPreviewElement = null;
  }
  cancelButtonPreviewHide() {
    this.buttonPreviewHideTimer && (window.clearTimeout(this.buttonPreviewHideTimer), this.buttonPreviewHideTimer = null);
  }
  scheduleHideButtonPreview() {
    this.cancelButtonPreviewHide(), this.buttonPreviewHideTimer = window.setTimeout(() => {
      this.hideButtonPreview();
    }, 120);
  }
  showButtonPreview(e, t) {
    var b;
    if (!t.previewImageUrl) {
      this.hideButtonPreview();
      return;
    }
    this.hideButtonPreview();
    const n = document.createElement("div"), o = ((b = this.config.theme) == null ? void 0 : b.mode) === "dark", s = document.createElement("img"), a = document.createElement("div"), l = e.getBoundingClientRect(), r = 180, c = 14, d = Math.min(
      Math.max(12, l.top + l.height / 2 - 110),
      Math.max(12, window.innerHeight - 232)
    ), h = this.config.placement === "bottom-left" ? Math.min(window.innerWidth - r - 12, l.right + c) : Math.max(12, l.left - r - c);
    n.style.cssText = `
      position: fixed;
      top: ${d}px;
      left: ${h}px;
      width: ${r}px;
      padding: 10px;
      border-radius: 16px;
      background: ${o ? "rgba(17, 24, 39, 0.96)" : "rgba(255, 255, 255, 0.98)"};
      box-shadow: 0 12px 32px rgba(0, 0, 0, ${o ? "0.34" : "0.18"});
      border: 1px solid ${o ? "rgba(255,255,255,0.08)" : "rgba(15,23,42,0.08)"};
      z-index: 10001;
      pointer-events: auto;
      display: flex;
      flex-direction: column;
      gap: 8px;
      cursor: pointer;
    `, s.src = t.previewImageUrl, s.alt = t.previewImageAlt || t.text || "preview image", s.style.cssText = `
      width: 100%;
      aspect-ratio: 1 / 1;
      object-fit: contain;
      background: white;
      border-radius: 12px;
      display: block;
    `, a.textContent = t.previewImageAlt || t.text || "", a.style.cssText = `
      color: ${o ? "#e5e7eb" : "#0f172a"};
      font-size: 12px;
      line-height: 1.4;
      text-align: center;
      word-break: break-word;
    `, n.appendChild(s), a.textContent && n.appendChild(a), n.addEventListener("mouseenter", () => {
      this.cancelButtonPreviewHide();
    }), n.addEventListener("mouseleave", () => {
      this.scheduleHideButtonPreview();
    }), n.addEventListener("click", () => {
      window.open(t.previewImageUrl, "_blank", "noopener,noreferrer");
    }), document.body.appendChild(n), this.buttonPreviewElement = n;
  }
  createButtonElement(e, t, n) {
    var T, v, D;
    const o = document.createElement("button"), s = (n == null ? void 0 : n.isMultiLayout) === !0, a = e.width || 60, l = e.height || 60, r = Math.max(a, l), c = s ? r : a, d = s ? r : l, h = s ? 0 : Math.min(c, d) / 2, b = ((T = this.config.theme) == null ? void 0 : T.mode) === "dark", g = b ? "#3B82F6" : "#0066FF", f = ((v = this.config.theme) == null ? void 0 : v.backgroundColor) || g, k = ((D = this.config.theme) == null ? void 0 : D.textColor) || "#ffffff", w = s ? "none" : `0 4px 16px rgba(0, 0, 0, ${b ? "0.3" : "0.12"})`, x = s && !(n != null && n.isLastButton) ? `1px solid rgba(255, 255, 255, ${b ? "0.14" : "0.28"})` : "none", y = s ? "translateY(-1px)" : "scale(1.1)";
    o.style.cssText = `
      background-color: ${s ? "transparent" : f};
      width: ${c}px;
      height: ${d}px;
      border-radius: ${h}px;
      border: none;
      border-bottom: ${x};
      cursor: ${this.config.draggable ? "move" : "pointer"};
      display: ${e.show === !1 ? "none" : "flex"};
      align-items: center;
      justify-content: center;
      box-shadow: ${w};
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
      padding: 0;
    `;
    const m = document.createElement("div");
    if (m.style.cssText = `
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: ${s && e.text ? "column" : "row"};
      gap: ${s ? "4px" : "8px"};
      width: 100%;
      height: 100%;
    `, e.icon) {
      const p = document.createElement("span");
      p.textContent = e.icon, p.style.fontSize = `${d * (s ? 0.34 : 0.4)}px`, p.style.lineHeight = "1", m.appendChild(p);
    } else {
      const p = document.createElement("div");
      p.innerHTML = `
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" fill="white"/>
        </svg>
      `, m.appendChild(p);
    }
    if (e.text) {
      const p = document.createElement("span");
      p.textContent = e.text, p.style.cssText = `
        color: ${k};
        font-size: ${d * (s ? 0.16 : 0.25)}px;
        white-space: nowrap;
        line-height: 1.1;
        text-align: center;
        max-width: ${s ? `${c - 8}px` : "none"};
        overflow: hidden;
        text-overflow: ellipsis;
      `, m.appendChild(p);
    }
    return o.appendChild(m), o.addEventListener("mouseenter", () => {
      this.cancelButtonPreviewHide(), o.style.transform = y, s && (o.style.backgroundColor = "rgba(255, 255, 255, 0.12)"), e.previewImageUrl && this.showButtonPreview(o, e);
    }), o.addEventListener("mouseleave", () => {
      o.style.transform = "scale(1)", s && (o.style.backgroundColor = "transparent"), e.previewImageUrl && this.scheduleHideButtonPreview();
    }), o.addEventListener("click", () => {
      this.isDragging || (i.debug("bubble click", e.action || "chat"), t instanceof HTMLElement && this.hideBubbleMessageElement(), this.triggerButtonAction(e));
    }), o.addEventListener("contextmenu", (p) => {
      this.showContextMenu(p);
    }), o.messageElement = t, o;
  }
  async init() {
    var t, n;
    if (this.isDestroyed) {
      i.warn("BytedeskWeb 已销毁，跳过初始化");
      return;
    }
    const e = this.hasVisibleButtons();
    if (await this._initVisitor(), !this.isDestroyed) {
      if (e) {
        if (await this._browseVisitor(), this.isDestroyed) return;
      } else
        i.debug("buttonConfig.show=false，跳过自动发送浏览记录");
      if (this.createBubble(), !this.isDestroyed && (this.createInviteDialog(), !this.isDestroyed && (this.setupMessageListener(), this.setupResizeListener(), !this.isDestroyed))) {
        if ((t = this.config.feedbackConfig) != null && t.enabled && (this.config.isDebug && i.debug("BytedeskWeb: 开始初始化文档反馈功能，document.readyState:", document.readyState), this.initFeedbackFeature(), document.readyState !== "complete")) {
          this.config.isDebug && i.debug("BytedeskWeb: DOM未完全加载，设置备用初始化");
          const o = () => {
            this.config.isDebug && i.debug("BytedeskWeb: window load事件触发，重新初始化反馈功能"), this.initFeedbackFeature(), window.removeEventListener("load", o);
          };
          window.addEventListener("load", o);
          const s = () => {
            this.config.isDebug && i.debug("BytedeskWeb: DOMContentLoaded事件触发，重新初始化反馈功能"), setTimeout(() => this.initFeedbackFeature(), 100), document.removeEventListener("DOMContentLoaded", s);
          };
          document.readyState === "loading" && document.addEventListener("DOMContentLoaded", s);
        }
        if (e) {
          if (this._getUnreadMessageCount(), this.isDestroyed) return;
        } else
          i.debug("buttonConfig.show=false，跳过自动获取未读消息数");
        if (this.config.autoPopup) {
          if (this.isDestroyed) return;
          setTimeout(() => {
            this.showChat();
          }, this.config.autoPopupDelay || 1e3);
        }
        if (!this.isDestroyed && (n = this.config.inviteConfig) != null && n.show) {
          if (this.isDestroyed) return;
          setTimeout(() => {
            this.showInviteDialog();
          }, this.config.inviteConfig.delay || 3e3);
        }
      }
    }
  }
  async _initVisitor() {
    var s, a, l, r;
    if (this.initVisitorPromise)
      return i.debug("访客初始化请求正在进行中，返回现有Promise"), this.initVisitorPromise;
    const e = localStorage.getItem(S), t = localStorage.getItem(W);
    i.debug("localUid: ", e), i.debug("localVisitorUid: ", t);
    const o = ((s = this.config.chatConfig) == null ? void 0 : s.visitorUid) && t ? ((a = this.config.chatConfig) == null ? void 0 : a.visitorUid) === t : !0;
    return e && t && o ? (i.debug("访客信息相同，直接返回本地访客信息"), (r = (l = this.config).onVisitorInfo) == null || r.call(l, e || "", t || ""), {
      uid: e,
      visitorUid: t
    }) : (i.debug("开始创建访客初始化Promise"), this.initVisitorPromise = import("../../apis/visitor/index.js").then(
      async ({ initVisitor: c }) => {
        var d, h, b, g, f, k, w, x, y, m, T, v, D, p, C, M, E, I, U, F, R, O, z, V;
        try {
          const H = {
            uid: String(((d = this.config.chatConfig) == null ? void 0 : d.uid) || e || ""),
            visitorUid: String(
              ((h = this.config.chatConfig) == null ? void 0 : h.visitorUid) || t || ""
            ),
            orgUid: String(((b = this.config.chatConfig) == null ? void 0 : b.org) || ""),
            nickname: String(((g = this.config.chatConfig) == null ? void 0 : g.name) || ""),
            avatar: String(((f = this.config.chatConfig) == null ? void 0 : f.avatar) || ""),
            mobile: String(((k = this.config.chatConfig) == null ? void 0 : k.mobile) || ""),
            email: String(((w = this.config.chatConfig) == null ? void 0 : w.email) || ""),
            note: String(((x = this.config.chatConfig) == null ? void 0 : x.note) || ""),
            channel: String(((y = this.config.chatConfig) == null ? void 0 : y.channel) || ""),
            extra: typeof ((m = this.config.chatConfig) == null ? void 0 : m.extra) == "string" ? this.config.chatConfig.extra : JSON.stringify(((T = this.config.chatConfig) == null ? void 0 : T.extra) || {}),
            vipLevel: String(((v = this.config.chatConfig) == null ? void 0 : v.vipLevel) || ""),
            debug: ((D = this.config.chatConfig) == null ? void 0 : D.debug) || !1,
            settingsUid: ((p = this.config.chatConfig) == null ? void 0 : p.settingsUid) || "",
            loadHistory: ((C = this.config.chatConfig) == null ? void 0 : C.loadHistory) || !1
          }, B = await c(H);
          return i.debug("访客初始化API响应:", B.data, H), ((M = B.data) == null ? void 0 : M.code) === 200 ? ((I = (E = B.data) == null ? void 0 : E.data) != null && I.uid && (localStorage.setItem(S, B.data.data.uid), i.debug("已保存uid到localStorage:", B.data.data.uid)), (F = (U = B.data) == null ? void 0 : U.data) != null && F.visitorUid && (localStorage.setItem(
            W,
            B.data.data.visitorUid
          ), i.debug(
            "已保存visitorUid到localStorage:",
            B.data.data.visitorUid
          )), (R = B.data) != null && R.data && (i.debug("触发onVisitorInfo回调"), (z = (O = this.config).onVisitorInfo) == null || z.call(
            O,
            B.data.data.uid || "",
            B.data.data.visitorUid || ""
          )), B.data.data) : (i.error("访客初始化失败:", (V = B.data) == null ? void 0 : V.message), null);
        } catch (H) {
          return i.error("访客初始化出错:", H), null;
        } finally {
          i.debug("访客初始化Promise完成，清除引用"), this.initVisitorPromise = null;
        }
      }
    ), this.initVisitorPromise);
  }
  // 获取当前页面浏览信息并发送到服务器
  async _browseVisitor() {
    var e, t, n, o, s;
    try {
      const a = localStorage.getItem(
        L
      );
      if (a) {
        const C = parseInt(a), M = Date.now(), E = 60 * 60 * 1e3;
        if (!Number.isNaN(C) && M - C < E) {
          const I = Math.ceil(
            (E - (M - C)) / 1e3 / 60
          );
          i.warn(`浏览记录1小时内最多发送一次，还需等待 ${I} 分钟`);
          return;
        }
      }
      const l = localStorage.getItem($);
      if (l) {
        const C = parseInt(l), M = Date.now(), E = 60 * 60 * 1e3;
        if (M - C < E) {
          const I = Math.ceil((E - (M - C)) / 1e3 / 60);
          i.warn(`浏览记录发送失败后1小时内禁止发送，还需等待 ${I} 分钟`);
          return;
        } else
          localStorage.removeItem($);
      }
      const r = window.location.href, c = document.title, d = document.referrer, h = navigator.userAgent, b = this.getBrowserInfo(h), g = this.getOSInfo(h), f = this.getDeviceInfo(h), k = `${screen.width}x${screen.height}`, w = new URLSearchParams(window.location.search), x = w.get("utm_source") || void 0, y = w.get("utm_medium") || void 0, m = w.get("utm_campaign") || void 0, T = localStorage.getItem(S), v = {
        url: r,
        title: c,
        referrer: d,
        userAgent: h,
        operatingSystem: g,
        browser: b,
        deviceType: f,
        screenResolution: k,
        utmSource: x,
        utmMedium: y,
        utmCampaign: m,
        status: "ONLINE",
        // 注意这里就是uid，不是visitorUid，使用访客系统生成uid
        visitorUid: String(
          ((e = this.config.chatConfig) == null ? void 0 : e.uid) || T || ""
        ),
        orgUid: ((t = this.config.chatConfig) == null ? void 0 : t.org) || "",
        channel: String(((n = this.config.chatConfig) == null ? void 0 : n.channel) || "")
      };
      if (!v.visitorUid) {
        i.warn("访客uid为空，跳过browse操作");
        return;
      }
      localStorage.setItem(L, Date.now().toString());
      const { browse: D } = await import("../../apis/visitor/index.js"), p = await D(v);
      ((o = p.data) == null ? void 0 : o.code) === 200 ? localStorage.removeItem($) : (i.error("浏览记录发送失败:", (s = p.data) == null ? void 0 : s.message), localStorage.setItem($, Date.now().toString()), i.warn("已记录浏览记录发送失败时间，1小时内将禁止再次发送"));
    } catch (a) {
      i.error("发送浏览记录时出错:", a), localStorage.setItem($, Date.now().toString()), i.warn("已记录浏览记录发送失败时间，1小时内将禁止再次发送");
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
    return this.getUnreadMessageCountPromise ? (i.debug("获取未读消息数请求正在进行中，返回现有Promise"), this.getUnreadMessageCountPromise) : (this.getUnreadMessageCountPromise = import("../../apis/message/index.js").then(
      async ({ getUnreadMessageCount: e }) => {
        var t, n, o;
        try {
          const s = String(((t = this.config.chatConfig) == null ? void 0 : t.visitorUid) || ""), a = localStorage.getItem(S), l = localStorage.getItem(W), r = {
            uid: a || "",
            visitorUid: s || l || "",
            orgUid: ((n = this.config.chatConfig) == null ? void 0 : n.org) || ""
          };
          if (r.uid === "")
            return 0;
          const c = await e(r);
          return ((o = c.data) == null ? void 0 : o.code) === 200 ? (this.setUnreadMessageCount(c.data.data || 0), c.data.data || 0) : 0;
        } catch (s) {
          return i.error("获取未读消息数出错:", s), 0;
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
    localStorage.removeItem($), localStorage.removeItem(L), i.info("已清除浏览记录发送失败的限制");
  }
  // 清除本地访客信息，强制重新初始化
  clearVisitorInfo() {
    localStorage.removeItem(S), localStorage.removeItem(W), i.info("已清除本地访客信息");
  }
  // 强制重新初始化访客信息（忽略本地缓存）
  async forceInitVisitor() {
    return this.clearVisitorInfo(), this.initVisitorPromise = null, this._initVisitor();
  }
  removeUnreadBadgeElement() {
    if (!this.bubble)
      return;
    const e = this.bubble.querySelector(".bytedesk-unread-badge");
    e && e.remove();
  }
  renderUnreadBadge() {
    if (i.debug("renderUnreadBadge() 被调用", {
      mode: this.unreadBadgeMode,
      count: this.unreadBadgeCount
    }), !this.hasVisibleButtons()) {
      this.removeUnreadBadgeElement(), i.debug("renderUnreadBadge: 当前没有可见按钮，不显示角标");
      return;
    }
    if (!this.bubble) {
      i.debug("renderUnreadBadge: bubble 不存在");
      return;
    }
    if (this.unreadBadgeMode === "hidden") {
      this.removeUnreadBadgeElement();
      return;
    }
    let e = this.bubble.querySelector(
      ".bytedesk-unread-badge"
    );
    e || (e = document.createElement("div"), e.className = "bytedesk-unread-badge", this.bubble.appendChild(e));
    const t = this.unreadBadgeMode === "count";
    e.style.cssText = `
      position: absolute;
      top: -4px;
      right: 2px;
      min-width: ${t ? "18px" : "10px"};
      width: ${t ? "auto" : "10px"};
      height: ${t ? "18px" : "10px"};
      padding: ${t ? "0 4px" : "0"};
      background: #ff4d4f;
      color: white;
      font-size: 12px;
      font-weight: bold;
      line-height: 1;
      border-radius: 999px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
      border: 2px solid white;
      box-sizing: border-box;
      pointer-events: none;
      z-index: 1;
    `, e.textContent = t ? this.unreadBadgeCount > 99 ? "99+" : this.unreadBadgeCount.toString() : "";
  }
  setUnreadMessageCount(e) {
    const t = Number.isFinite(e) ? Math.max(0, Math.floor(e)) : 0;
    return this.unreadBadgeCount = t, this.unreadBadgeMode = t > 0 ? "count" : "hidden", this.renderUnreadBadge(), t;
  }
  showUnreadDot() {
    this.unreadBadgeCount = 0, this.unreadBadgeMode = "dot", this.renderUnreadBadge();
  }
  clearUnreadBadge() {
    this.unreadBadgeCount = 0, this.unreadBadgeMode = "hidden", this.removeUnreadBadgeElement();
  }
  // 清空未读消息
  async clearUnreadMessages() {
    return this.clearUnreadMessagesPromise ? (i.debug("清空未读消息请求正在进行中，返回现有Promise"), this.clearUnreadMessagesPromise) : (this.clearUnreadMessagesPromise = import("../../apis/message/index.js").then(
      async ({ clearUnreadMessages: e }) => {
        var t, n;
        try {
          const o = String(((t = this.config.chatConfig) == null ? void 0 : t.visitorUid) || ""), s = localStorage.getItem(S), a = localStorage.getItem(W), l = {
            uid: s || "",
            visitorUid: o || a || "",
            orgUid: ((n = this.config.chatConfig) == null ? void 0 : n.org) || ""
          }, r = await e(l);
          return i.debug("清空未读消息数:", r.data, l), r.data.code === 200 ? (i.info("清空未读消息数成功:", r.data), this.clearUnreadBadge(), r.data.data || 0) : (i.error("清空未读消息数失败:", r.data.message), 0);
        } catch (o) {
          return i.error("清空未读消息数出错:", o), 0;
        } finally {
          this.clearUnreadMessagesPromise = null;
        }
      }
    ), this.clearUnreadMessagesPromise);
  }
  getBubbleMessages() {
    var t, n, o, s;
    const e = (t = this.config.bubbleConfig) == null ? void 0 : t.messages;
    if (Array.isArray(e) && e.length > 0) {
      const a = e.filter(
        (l) => !!l && (!!l.icon || !!l.title || !!l.subtitle)
      );
      if (a.length > 0)
        return a;
    }
    return [
      {
        icon: (n = this.config.bubbleConfig) == null ? void 0 : n.icon,
        title: (o = this.config.bubbleConfig) == null ? void 0 : o.title,
        subtitle: (s = this.config.bubbleConfig) == null ? void 0 : s.subtitle
      }
    ];
  }
  getBubbleSwitchMode() {
    var e;
    return ((e = this.config.bubbleConfig) == null ? void 0 : e.switchMode) || "fade";
  }
  buildBubbleMessageContentNode(e) {
    var l, r;
    const t = document.createElement("div");
    t.style.cssText = `
      display: flex;
      align-items: center;
      gap: 8px;
      flex-direction: ${this.config.placement === "bottom-left" ? "row" : "row-reverse"};
      box-sizing: border-box;
    `, t.setAttribute("data-bytedesk-bubble-content", "true"), t.setAttribute("data-placement", this.config.placement || "bottom-right");
    const n = document.createElement("span");
    n.setAttribute("data-bytedesk-bubble-role", "icon"), n.style.fontSize = "20px", n.textContent = e.icon || "", t.appendChild(n);
    const o = document.createElement("div");
    o.style.cssText = "min-width: 0; flex: 1;";
    const s = document.createElement("div");
    s.setAttribute("data-bytedesk-bubble-role", "title"), s.style.fontWeight = "bold", s.style.color = ((l = this.config.theme) == null ? void 0 : l.mode) === "dark" ? "#e5e7eb" : "#1f2937", s.style.marginBottom = "4px", s.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", s.textContent = e.title || "", o.appendChild(s);
    const a = document.createElement("div");
    return a.setAttribute("data-bytedesk-bubble-role", "subtitle"), a.style.fontSize = "0.9em", a.style.color = ((r = this.config.theme) == null ? void 0 : r.mode) === "dark" ? "#9ca3af" : "#4b5563", a.style.textAlign = this.config.placement === "bottom-left" ? "left" : "right", a.textContent = e.subtitle || "", o.appendChild(a), t.appendChild(o), { messageContent: t, iconSpan: n, title: s, subtitle: a };
  }
  buildBubbleTickerItemNode(e, t, n) {
    var l, r;
    const o = document.createElement("div");
    o.style.cssText = `
      position: relative;
      width: ${t ? `${t}px` : "auto"};
      padding-bottom: 10px;
      box-sizing: border-box;
      display: block;
    `;
    const s = document.createElement("div");
    s.style.cssText = `
      background: ${((l = this.config.theme) == null ? void 0 : l.mode) === "dark" ? "#1f2937" : "white"};
      color: ${((r = this.config.theme) == null ? void 0 : r.mode) === "dark" ? "#e5e7eb" : "#1f2937"};
      padding: 12px 16px;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      max-width: 220px;
      position: relative;
      box-sizing: border-box;
      width: ${t ? `${t}px` : "auto"};
      min-height: ${n ? `${n - 10}px` : "auto"};
    `;
    const { messageContent: a } = this.buildBubbleMessageContentNode(e);
    return t && (a.style.width = `${Math.max(0, t - 32)}px`), s.appendChild(a), o.appendChild(s), o;
  }
  destroyBubbleTicker() {
    var e, t;
    (e = this.bubbleTickerStyleElement) != null && e.parentElement && this.bubbleTickerStyleElement.parentElement.removeChild(this.bubbleTickerStyleElement), this.bubbleTickerStyleElement = null, (t = this.bubbleTickerTrackElement) != null && t.parentElement && this.bubbleTickerTrackElement.parentElement.removeChild(this.bubbleTickerTrackElement), this.bubbleTickerTrackElement = null;
  }
  setBubbleTickerRunning(e) {
    this.bubbleTickerTrackElement && (this.bubbleTickerTrackElement.style.animationPlayState = e ? "running" : "paused");
  }
  initBubbleTicker(e) {
    var k, w;
    const t = this.bubbleMessageViewportElement, n = e || ((k = this.bubble) == null ? void 0 : k.messageElement) || (t == null ? void 0 : t.parentElement);
    if (!(t instanceof HTMLElement))
      return;
    if (this.destroyBubbleTicker(), this.bubbleMessages.length <= 1) {
      this.bubbleMessageContentElement && !t.contains(this.bubbleMessageContentElement) && t.appendChild(this.bubbleMessageContentElement), this.renderBubbleMessage(0);
      return;
    }
    if (!(n instanceof HTMLElement))
      return;
    const o = document.createElement("div");
    o.style.cssText = `
      position: absolute;
      visibility: hidden;
      pointer-events: none;
      left: 0;
      top: 0;
      z-index: -1;
      width: max-content;
      max-width: 220px;
    `, n.appendChild(o);
    const s = this.bubbleMessages.map((x) => {
      const y = this.buildBubbleTickerItemNode(x);
      return o.appendChild(y), y;
    }), a = s.reduce((x, y) => Math.max(x, y.offsetHeight), 0), l = s.reduce((x, y) => Math.max(x, y.offsetWidth), 0);
    if (n.removeChild(o), !a || !l)
      return;
    t.style.width = `${l}px`, this.syncBubbleViewportHeight(a, !1);
    const r = document.createElement("div");
    r.style.cssText = `
      position: relative;
      display: flex;
      flex-direction: column;
      width: ${l}px;
      will-change: transform;
    `, [...this.bubbleMessages, ...this.bubbleMessages].forEach((x) => {
      const y = this.buildBubbleTickerItemNode(x, l, a);
      y.style.height = `${a}px`, y.style.minHeight = `${a}px`, r.appendChild(y);
    });
    const d = a * this.bubbleMessages.length, b = Math.max(1.6, Number(((w = this.config.bubbleConfig) == null ? void 0 : w.rotateInterval) || 3e3) / 1e3) * this.bubbleMessages.length, g = `bytedeskBubbleTicker_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`, f = document.createElement("style");
    f.textContent = `
      @keyframes ${g} {
        from { transform: translateY(0); }
        to { transform: translateY(-${d}px); }
      }
    `, document.head.appendChild(f), r.style.animation = `${g} ${b}s linear infinite`, r.style.animationPlayState = "paused", t.appendChild(r), this.bubbleTickerTrackElement = r, this.bubbleTickerStyleElement = f, this.bubbleMessageIndex = 0;
  }
  renderBubbleMessage(e) {
    if (!this.bubbleMessages.length)
      return;
    if (this.getBubbleSwitchMode() === "ticker") {
      this.bubbleMessageIndex = (e % this.bubbleMessages.length + this.bubbleMessages.length) % this.bubbleMessages.length, this.syncBubbleViewportHeight();
      return;
    }
    if (!this.bubbleIconElement || !this.bubbleTitleElement || !this.bubbleSubtitleElement)
      return;
    const t = this.bubbleMessages.length;
    this.bubbleMessageIndex = (e % t + t) % t;
    const n = this.bubbleMessages[this.bubbleMessageIndex];
    this.bubbleIconElement.textContent = n.icon || "", this.bubbleTitleElement.textContent = n.title || "", this.bubbleSubtitleElement.textContent = n.subtitle || "", this.syncBubbleViewportHeight();
  }
  syncBubbleViewportHeight(e, t = !1) {
    var o;
    if (!(this.bubbleMessageViewportElement instanceof HTMLElement))
      return;
    const n = e ?? ((o = this.bubbleMessageContentElement) == null ? void 0 : o.offsetHeight) ?? 0;
    n && (this.bubbleMessageViewportElement.style.transition = t ? "height 0.3s ease" : "none", this.bubbleMessageViewportElement.style.height = `${n}px`);
  }
  cleanupPendingBubbleMessage() {
    var e;
    (e = this.bubblePendingMessageElement) != null && e.parentElement && this.bubblePendingMessageElement.parentElement.removeChild(this.bubblePendingMessageElement), this.bubblePendingMessageElement = null;
  }
  stopBubbleMessageTransition() {
    this.bubbleMessageTransitionTimer !== null && (window.clearTimeout(this.bubbleMessageTransitionTimer), this.bubbleMessageTransitionTimer = null), this.setBubbleTickerRunning(!1), this.cleanupPendingBubbleMessage(), this.bubbleMessageViewportElement && (this.bubbleMessageViewportElement.style.transition = ""), this.bubbleMessageContentElement && (this.bubbleMessageContentElement.style.transition = "", this.bubbleMessageContentElement.style.transform = "translateY(0)", this.bubbleMessageContentElement.style.opacity = "1");
  }
  transitionBubbleMessage(e) {
    var s, a;
    const t = (s = this.bubble) == null ? void 0 : s.messageElement;
    if (!(t instanceof HTMLElement) || t.style.display === "none") {
      this.renderBubbleMessage(e);
      return;
    }
    const n = this.getBubbleSwitchMode();
    if (n === "ticker") {
      this.renderBubbleMessage(e), this.setBubbleTickerRunning(!0);
      return;
    }
    if (this.stopBubbleMessageTransition(), n === "slide-up") {
      const l = this.bubbleMessageViewportElement, r = this.bubbleMessageContentElement;
      if (!(l instanceof HTMLElement) || !(r instanceof HTMLElement) || !r.parentElement) {
        this.renderBubbleMessage(e);
        return;
      }
      const c = this.bubbleMessages[(e % this.bubbleMessages.length + this.bubbleMessages.length) % this.bubbleMessages.length], d = r.cloneNode(!0), h = d.querySelector('[data-bytedesk-bubble-role="icon"]'), b = d.querySelector('[data-bytedesk-bubble-role="title"]'), g = d.querySelector('[data-bytedesk-bubble-role="subtitle"]');
      h && (h.textContent = c.icon || ""), b && (b.textContent = c.title || ""), g && (g.textContent = c.subtitle || ""), d.style.position = "absolute", d.style.left = "0", d.style.top = "0", d.style.width = "100%", d.style.transform = "translateY(100%)", d.style.opacity = "1", d.style.transition = "transform 0.3s ease", r.style.transition = "transform 0.3s ease";
      const f = r.offsetHeight;
      r.parentElement.appendChild(d);
      const k = d.offsetHeight;
      this.syncBubbleViewportHeight(f, !1), this.bubblePendingMessageElement = d, window.requestAnimationFrame(() => {
        this.syncBubbleViewportHeight(k, !0), r.style.transform = "translateY(-100%)", d.style.transform = "translateY(0)";
      }), this.bubbleMessageTransitionTimer = window.setTimeout(() => {
        this.renderBubbleMessage(e), r.style.transition = "", r.style.transform = "translateY(0)", r.style.opacity = "1", this.syncBubbleViewportHeight(k, !1), this.cleanupPendingBubbleMessage(), this.bubbleMessageTransitionTimer = null;
      }, 320);
      return;
    }
    const o = ((a = this.bubbleMessageContentElement) == null ? void 0 : a.offsetHeight) ?? 0;
    this.syncBubbleViewportHeight(o, !1), t.style.opacity = "0", t.style.transform = "translateY(6px)", this.bubbleMessageTransitionTimer = window.setTimeout(() => {
      var r;
      this.renderBubbleMessage(e);
      const l = ((r = this.bubbleMessageContentElement) == null ? void 0 : r.offsetHeight) ?? o;
      this.syncBubbleViewportHeight(l, !0), t.style.opacity = "1", t.style.transform = "translateY(0)", this.bubbleMessageTransitionTimer = null;
    }, 180);
  }
  stopBubbleMessageRotation() {
    this.bubbleMessageTimer !== null && (window.clearInterval(this.bubbleMessageTimer), this.bubbleMessageTimer = null), this.setBubbleTickerRunning(!1);
  }
  startBubbleMessageRotation() {
    var o, s, a, l;
    if (this.stopBubbleMessageRotation(), !(((o = this.config.bubbleConfig) == null ? void 0 : o.autoRotate) !== !1) || this.bubbleMessages.length <= 1)
      return;
    if (this.getBubbleSwitchMode() === "ticker") {
      this.bubbleTickerTrackElement || this.initBubbleTicker(((s = this.bubble) == null ? void 0 : s.messageElement) || ((a = this.bubbleMessageViewportElement) == null ? void 0 : a.parentElement)), this.setBubbleTickerRunning(!0);
      return;
    }
    const t = Number(((l = this.config.bubbleConfig) == null ? void 0 : l.rotateInterval) || 3e3), n = Number.isFinite(t) ? Math.max(1e3, t) : 3e3;
    this.bubbleMessageTimer = window.setInterval(() => {
      var c;
      const r = (c = this.bubble) == null ? void 0 : c.messageElement;
      r instanceof HTMLElement && r.style.display !== "none" && this.transitionBubbleMessage(this.bubbleMessageIndex + 1);
    }, n);
  }
  createBubble() {
    var c, d, h, b, g, f, k, w, x, y;
    if (this.bubble && document.body.contains(this.bubble)) {
      i.debug("createBubble: 气泡已存在，不重复创建");
      return;
    }
    this.bubble && !document.body.contains(this.bubble) && (i.debug("createBubble: 清理已存在的 bubble 引用"), this.bubble = null), this.bubbleContainer && !document.body.contains(this.bubbleContainer) && (i.debug("createBubble: 清理已存在的 bubbleContainer 引用"), this.bubbleContainer = null), this.buttonElements = [];
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
    if ((c = this.config.bubbleConfig) != null && c.show) {
      const m = this.getBubbleSwitchMode() === "ticker";
      t = document.createElement("div"), t.style.cssText = `
        background: ${m ? "transparent" : ((d = this.config.theme) == null ? void 0 : d.mode) === "dark" ? "#1f2937" : "white"};
        color: ${((h = this.config.theme) == null ? void 0 : h.mode) === "dark" ? "#e5e7eb" : "#1f2937"};
        padding: ${m ? "0" : "12px 16px"};
        border-radius: ${m ? "0" : "8px"};
        box-shadow: ${m ? "none" : "0 2px 12px rgba(0, 0, 0, 0.1)"};
        max-width: ${m ? "none" : "220px"};
        margin-bottom: 8px;
        opacity: 0;
        transform: translateY(10px);
        transition: opacity 0.22s ease, transform 0.22s ease;
        position: relative;
      `;
      const T = document.createElement("div");
      T.style.cssText = `
        position: relative;
        overflow: hidden;
      `;
      const {
        messageContent: v,
        iconSpan: D,
        title: p,
        subtitle: C
      } = this.buildBubbleMessageContentNode({
        icon: (b = this.config.bubbleConfig) == null ? void 0 : b.icon,
        title: (g = this.config.bubbleConfig) == null ? void 0 : g.title,
        subtitle: (f = this.config.bubbleConfig) == null ? void 0 : f.subtitle
      });
      if (m || T.appendChild(v), t.appendChild(T), !m) {
        const M = document.createElement("div");
        M.style.cssText = `
          position: absolute;
          bottom: -6px;
          ${this.config.placement === "bottom-left" ? "left: 24px" : "right: 24px"};
          width: 12px;
          height: 12px;
          background: ${((k = this.config.theme) == null ? void 0 : k.mode) === "dark" ? "#1f2937" : "white"};
          transform: rotate(45deg);
          box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
        `;
        const E = document.createElement("div");
        E.style.cssText = `
          position: absolute;
          bottom: 0;
          ${this.config.placement === "bottom-left" ? "left: 18px" : "right: 18px"};
          width: 24px;
          height: 12px;
          background: ${((w = this.config.theme) == null ? void 0 : w.mode) === "dark" ? "#1f2937" : "white"};
        `, t.appendChild(M), t.appendChild(E);
      }
      e.appendChild(t), this.bubbleMessages = this.getBubbleMessages(), this.bubbleMessageViewportElement = T, this.bubbleMessageContentElement = v, this.bubbleIconElement = D, this.bubbleTitleElement = p, this.bubbleSubtitleElement = C, this.bubbleMessageIndex = 0, this.getBubbleSwitchMode() === "ticker" ? this.initBubbleTicker(t) : this.renderBubbleMessage(0), t.addEventListener("mouseenter", () => {
        this.stopBubbleMessageRotation();
      }), t.addEventListener("mouseleave", () => {
        this.startBubbleMessageRotation();
      }), setTimeout(() => {
        t && (t.style.opacity = "1", t.style.transform = "translateY(0)", this.startBubbleMessageRotation());
      }, 500);
    }
    const n = this.getEffectiveButtonConfigs(), o = document.createElement("div"), s = this.isMultiButtonLayout(n), a = ((x = this.config.theme) == null ? void 0 : x.mode) === "dark", l = a ? "#3B82F6" : "#0066FF", r = ((y = this.config.theme) == null ? void 0 : y.backgroundColor) || l;
    if (o.style.cssText = `
      display: flex;
      flex-direction: column;
      align-items: ${this.config.placement === "bottom-left" ? "flex-start" : "flex-end"};
      gap: ${s ? "0" : "10px"};
      background: ${s ? r : "transparent"};
      border-radius: ${s ? "18px" : "0"};
      overflow: ${s ? "hidden" : "visible"};
      box-shadow: ${s ? `0 10px 28px rgba(0, 0, 0, ${a ? "0.32" : "0.16"})` : "none"};
    `, n.forEach((m, T) => {
      const v = this.createButtonElement(m, t, {
        isMultiLayout: s,
        isLastButton: T === n.length - 1
      });
      this.buttonElements.push(v), T === 0 && (this.bubble = v), o.appendChild(v);
    }), this.renderUnreadBadge(), e.appendChild(o), this.config.draggable && this.buttonElements.length > 0) {
      let m = 0, T = 0, v = 0, D = 0;
      this.buttonElements.forEach((p) => {
        p.addEventListener("mousedown", (C) => {
          C.button === 0 && (this.isDragging = !0, m = C.clientX, T = C.clientY, v = e.offsetLeft, D = e.offsetTop, e.style.transition = "none");
        });
      }), document.addEventListener("mousemove", (p) => {
        if (!this.isDragging) return;
        p.preventDefault();
        const C = p.clientX - m, M = p.clientY - T, E = v + C, I = D + M, U = window.innerHeight - e.offsetHeight;
        E <= window.innerWidth / 2 ? (e.style.left = `${Math.max(0, E)}px`, e.style.right = "auto", e.style.alignItems = "flex-start", this.config.placement = "bottom-left") : (e.style.right = `${Math.max(
          0,
          window.innerWidth - E - e.offsetWidth
        )}px`, e.style.left = "auto", e.style.alignItems = "flex-end", this.config.placement = "bottom-right"), e.style.bottom = `${Math.min(
          Math.max(0, window.innerHeight - I - e.offsetHeight),
          U
        )}px`;
      }), document.addEventListener("mouseup", () => {
        this.isDragging && (this.isDragging = !1, e.style.transition = "all 0.3s ease", this.config.marginSide = parseInt(
          this.config.placement === "bottom-left" ? e.style.left : e.style.right
        ) || 20, this.config.marginBottom = parseInt(e.style.bottom || "20"));
      });
    }
    document.body.appendChild(e), this.bubbleContainer = e, document.addEventListener("click", () => {
      this.hideContextMenu();
    });
  }
  createChatWindow() {
    var l, r, c, d, h, b;
    if (this.window && document.body.contains(this.window)) {
      i.debug("createChatWindow: 聊天窗口已存在，不重复创建");
      return;
    }
    this.window && !document.body.contains(this.window) && (i.debug("createChatWindow: 清理已存在的 window 引用"), this.window = null), this.window = document.createElement("div");
    const e = window.innerWidth <= 768, t = window.innerWidth, n = window.innerHeight, o = Math.min(
      ((l = this.config.window) == null ? void 0 : l.width) || t * 0.9,
      t * 0.9
    ), s = Math.min(
      ((r = this.config.window) == null ? void 0 : r.height) || n * 0.9,
      n * 0.9
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
        transition: all ${(c = this.config.animation) == null ? void 0 : c.duration}ms ${(d = this.config.animation) == null ? void 0 : d.type};
      ` : this.window.style.cssText = `
        position: fixed;
        ${this.config.placement === "bottom-right" ? "right" : "left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${o}px;
        height: ${s}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(h = this.config.animation) == null ? void 0 : h.duration}ms ${(b = this.config.animation) == null ? void 0 : b.type};
      `;
    const a = document.createElement("iframe");
    a.setAttribute("allow", "microphone *; camera *; autoplay *; clipboard-write *"), a.style.cssText = `
      width: 100%;
      height: 100%;
      border: none;
      display: block;
      vertical-align: bottom;
    `, a.src = this.generateChatUrl(), i.debug("iframe.src: ", a.src), this.window.appendChild(a), document.body.appendChild(this.window);
  }
  generateChatUrl(e = "messages") {
    i.debug("this.config: ", this.config, e);
    const t = new URLSearchParams();
    Object.entries(this.config.chatConfig || {}).forEach(([a, l]) => {
      if (!(l == null || String(l).trim() === ""))
        if (a === "debug" && l === !0)
          t.append("debug", "1");
        else if (a === "draft" && l === !0)
          t.append("draft", "1");
        else if (a === "loadHistory" && l === !0)
          t.append("loadHistory", "1");
        else if (a === "goodsInfo" || a === "orderInfo")
          try {
            typeof l == "string" ? t.append(a, l) : t.append(a, JSON.stringify(l));
          } catch (r) {
            i.error(`Error processing ${a}:`, r);
          }
        else if (a === "extra")
          try {
            let r = typeof l == "string" ? JSON.parse(l) : l;
            r.goodsInfo && delete r.goodsInfo, r.orderInfo && delete r.orderInfo, Object.keys(r).length > 0 && t.append(a, JSON.stringify(r));
          } catch (r) {
            i.error("Error processing extra parameter:", r);
          }
        else a !== "debug" && a !== "draft" && a !== "loadHistory" && t.append(a, String(l));
    });
    const n = ee(this.config.browseConfig);
    n && t.append("browse", n), Object.entries(this.config.theme || {}).forEach(([a, l]) => {
      t.append(a, String(l));
    }), t.append("lang", this.config.locale || "zh-cn");
    const s = `${this.getChatPageBaseUrl()}?${t.toString()}`;
    return i.debug("chat url: ", s), s;
  }
  normalizePath(e, t = "/chat") {
    const n = (e || "").trim();
    return n ? n.startsWith("/") ? n : `/${n}` : t;
  }
  getChatPageBaseUrl() {
    const e = this.normalizePath(this.config.chatPath, "/chat"), t = (this.config.htmlUrl || "").trim(), n = t.replace(/\/$/, "");
    if (!t)
      return e;
    if (n.match(/\/(chat(?:\/thread)?|webrtc|call)\/?$/))
      return n.replace(/\/(chat(?:\/thread)?|webrtc|call)\/?$/, e);
    try {
      const s = new URL(t, window.location.origin);
      if (s.pathname && s.pathname !== "/")
        return n;
    } catch {
      if (n.startsWith("/"))
        return n;
    }
    return `${n}${e}`;
  }
  setupMessageListener() {
    window.addEventListener("message", (e) => {
      switch (e.data.type) {
        case Z:
          this.hideChat();
          break;
        case J:
          this.toggleMaximize();
          break;
        case K:
          this.minimizeWindow();
          break;
        case G:
          i.debug("RECEIVE_MESSAGE");
          break;
        case X:
          i.debug("INVITE_VISITOR");
          break;
        case q:
          i.debug("INVITE_VISITOR_ACCEPT");
          break;
        case j:
          i.debug("INVITE_VISITOR_REJECT");
          break;
        case Y:
          this.handleLocalStorageData(e);
          break;
      }
    });
  }
  // 处理从 iframe 返回的 localStorage 数据
  handleLocalStorageData(e) {
    var a, l;
    const { uid: t, visitorUid: n } = e.data;
    i.debug("handleLocalStorageData 被调用", t, n, e.data);
    const o = localStorage.getItem(S), s = localStorage.getItem(W);
    if (o === t && s === n) {
      i.debug("handleLocalStorageData: 值相同，跳过设置");
      return;
    }
    localStorage.setItem(S, t), localStorage.setItem(W, n), i.debug("handleLocalStorageData: 已更新localStorage", {
      uid: t,
      visitorUid: n
    }), (l = (a = this.config).onVisitorInfo) == null || l.call(a, t, n);
  }
  // 向 iframe 发送消息
  sendMessageToIframe(e) {
    var n;
    const t = (n = this.window) == null ? void 0 : n.querySelector("iframe");
    t && t.contentWindow && t.contentWindow.postMessage(e, "*");
  }
  resetAnonymousVisitor() {
    localStorage.removeItem(S), localStorage.removeItem(W), this.sendMessageToIframe({ type: Q });
  }
  showChat(e) {
    var t, n;
    if (e && (this.config = {
      ...this.config,
      ...e
    }, this.window && (document.body.removeChild(this.window), this.window = null)), this.window || this.createChatWindow(), this.window) {
      const o = window.innerWidth <= 768;
      if (this.window.style.display = "block", this.config.forceRefresh) {
        const s = this.window.querySelector("iframe");
        s && (s.src = this.generateChatUrl());
      }
      if (this.setupResizeListener(), o && this.window && (this.window.style.transform = "translateY(100%)", requestAnimationFrame(() => {
        this.window && (this.window.style.transform = "translateY(0)");
      })), this.isVisible = !0, this.bubble) {
        this.bubble.style.display = "none";
        const s = this.bubble.messageElement;
        s instanceof HTMLElement && (s.style.display = "none");
      }
    }
    this.hideInviteDialog(), (n = (t = this.config).onShowChat) == null || n.call(t);
  }
  hideChat() {
    var e, t, n, o;
    if (this.window) {
      if (window.innerWidth <= 768 ? (this.window.style.transform = "translateY(100%)", setTimeout(() => {
        this.window && (this.window.style.display = "none");
      }, ((e = this.config.animation) == null ? void 0 : e.duration) || 300)) : this.window.style.display = "none", this.isVisible = !1, this.buttonElements.length > 0) {
        this.applyConfiguredButtonVisibility();
        const a = this.bubble.messageElement;
        a instanceof HTMLElement && (a.style.display = ((t = this.config.bubbleConfig) == null ? void 0 : t.show) === !1 ? "none" : "block");
      }
      (o = (n = this.config).onHideChat) == null || o.call(n);
    }
  }
  showThread(e) {
    return this.showChat({
      ...e,
      chatPath: this.normalizePath((e == null ? void 0 : e.threadPath) || this.config.threadPath, "/chat/thread")
    });
  }
  showWebrtc(e) {
    return this.showChat({
      ...e,
      chatPath: this.normalizePath((e == null ? void 0 : e.webrtcPath) || this.config.webrtcPath, "/webrtc")
    });
  }
  showCall(e) {
    return this.showChat({
      ...e,
      chatPath: this.normalizePath((e == null ? void 0 : e.callPath) || this.config.callPath, "/call")
    });
  }
  minimizeWindow() {
    this.window && (this.windowState = "minimized", this.window.style.display = "none", this.hideChat());
  }
  toggleMaximize() {
    this.window && window.open(this.generateChatUrl(), "_blank");
  }
  setupResizeListener() {
    const e = () => {
      var a, l;
      if (!this.window || !this.isVisible) return;
      const n = window.innerWidth <= 768, o = window.innerWidth, s = window.innerHeight;
      if (n)
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
        let r = this.windowState === "maximized" ? o : Math.min(
          ((a = this.config.window) == null ? void 0 : a.width) || o * 0.9,
          o * 0.9
        ), c = this.windowState === "maximized" ? s : Math.min(
          ((l = this.config.window) == null ? void 0 : l.height) || s * 0.9,
          s * 0.9
        );
        const d = this.config.placement === "bottom-right" ? this.config.marginSide : void 0, h = this.config.placement === "bottom-left" ? this.config.marginSide : void 0;
        Object.assign(this.window.style, {
          width: `${r}px`,
          height: `${c}px`,
          right: d ? `${d}px` : "auto",
          left: h ? `${h}px` : "auto",
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
    this.isDestroyed = !0, this.stopBubbleMessageRotation(), this.stopBubbleMessageTransition(), this.destroyBubbleTicker(), this.bubbleMessageViewportElement = null, this.bubbleMessageContentElement = null, this.bubblePendingMessageElement = null, this.bubbleTickerTrackElement = null, this.bubbleTickerStyleElement = null, this.bubbleIconElement = null, this.bubbleTitleElement = null, this.bubbleSubtitleElement = null, this.bubbleMessages = [], this.bubbleMessageIndex = 0, this.bubbleContainer && document.body.contains(this.bubbleContainer) && document.body.removeChild(this.bubbleContainer), this.hideButtonPreview(), this.bubbleContainer = null, this.bubble = null, this.buttonElements = [], this.window && document.body.contains(this.window) && (document.body.removeChild(this.window), this.window = null), window.removeEventListener("resize", this.setupResizeListener.bind(this)), this.loopTimer && (window.clearTimeout(this.loopTimer), this.loopTimer = null), this.inviteDialog && document.body.contains(this.inviteDialog) && (document.body.removeChild(this.inviteDialog), this.inviteDialog = null), this.contextMenu && document.body.contains(this.contextMenu) && (document.body.removeChild(this.contextMenu), this.contextMenu = null), this.hideTimeout && (clearTimeout(this.hideTimeout), this.hideTimeout = null), this.selectionDebounceTimer && (clearTimeout(this.selectionDebounceTimer), this.selectionDebounceTimer = null), this.destroyFeedbackFeature();
  }
  createInviteDialog() {
    var l, r, c, d, h, b;
    if (this.inviteDialog && document.body.contains(this.inviteDialog)) {
      i.debug("createInviteDialog: 邀请框已存在，不重复创建");
      return;
    }
    this.inviteDialog && !document.body.contains(this.inviteDialog) && (i.debug("createInviteDialog: 清理已存在的 inviteDialog 引用"), this.inviteDialog = null);
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
    `, (r = this.config.inviteConfig) != null && r.icon) {
      const g = document.createElement("div");
      g.style.cssText = `
        font-size: 32px;
        margin-bottom: 12px;
        color: ${e ? "#e5e7eb" : "#333"};
      `, g.textContent = this.config.inviteConfig.icon, this.inviteDialog.appendChild(g);
    }
    const t = document.createElement("div");
    t.style.cssText = `
      margin-bottom: 16px;
      color: ${e ? "#e5e7eb" : "#333"};
    `, t.textContent = ((c = this.config.inviteConfig) == null ? void 0 : c.text) || "需要帮助吗？点击开始对话", this.inviteDialog.appendChild(t);
    const n = document.createElement("div");
    n.style.cssText = `
      display: flex;
      gap: 10px;
      justify-content: center;
    `;
    const o = document.createElement("button");
    o.textContent = ((d = this.config.inviteConfig) == null ? void 0 : d.acceptText) || "开始对话";
    const s = ((h = this.config.theme) == null ? void 0 : h.backgroundColor) || (e ? "#3B82F6" : "#0066FF");
    o.style.cssText = `
      padding: 8px 16px;
      background: ${s};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, o.onclick = () => {
      var g, f;
      this.hideInviteDialog(), this.showChat(), (f = (g = this.config.inviteConfig) == null ? void 0 : g.onAccept) == null || f.call(g);
    };
    const a = document.createElement("button");
    a.textContent = ((b = this.config.inviteConfig) == null ? void 0 : b.rejectText) || "稍后再说", a.style.cssText = `
      padding: 8px 16px;
      background: ${e ? "#374151" : "#f5f5f5"};
      color: ${e ? "#d1d5db" : "#666"};
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `, a.onclick = () => {
      var g, f;
      this.hideInviteDialog(), (f = (g = this.config.inviteConfig) == null ? void 0 : g.onReject) == null || f.call(g), this.handleInviteLoop();
    }, n.appendChild(o), n.appendChild(a), this.inviteDialog.appendChild(n), document.body.appendChild(this.inviteDialog);
  }
  showInviteDialog() {
    var e, t;
    this.inviteDialog && (this.inviteDialog.style.display = "block", (t = (e = this.config.inviteConfig) == null ? void 0 : e.onOpen) == null || t.call(e));
  }
  hideInviteDialog() {
    var e, t;
    i.debug("hideInviteDialog before"), this.inviteDialog && (this.inviteDialog.style.display = "none", (t = (e = this.config.inviteConfig) == null ? void 0 : e.onClose) == null || t.call(e), i.debug("hideInviteDialog after"));
  }
  handleInviteLoop() {
    const {
      loop: e,
      loopDelay: t = 3e3,
      loopCount: n = 1 / 0
    } = this.config.inviteConfig || {};
    !e || this.loopCount >= n - 1 || (this.loopTimer && window.clearTimeout(this.loopTimer), this.loopTimer = window.setTimeout(() => {
      this.loopCount++, this.showInviteDialog();
    }, t));
  }
  showButton() {
    if (this.buttonElements.length > 0 && this.buttonElements.every((e) => e.style.display !== "none")) {
      i.debug("showButton: 按钮已经显示，无需重复显示");
      return;
    }
    this.buttonElements.length > 0 ? (this.buttonElements.forEach((e) => {
      e.style.display = "flex";
    }), i.debug("showButton: 按钮已显示")) : i.debug("showButton: bubble 不存在，需要先创建");
  }
  hideButton() {
    this.buttonElements.length > 0 && this.buttonElements.forEach((e) => {
      e.style.display = "none";
    });
  }
  showBubble() {
    if (this.bubble) {
      const e = this.bubble.messageElement;
      if (e instanceof HTMLElement) {
        if (e.style.display !== "none" && e.style.opacity !== "0") {
          i.debug("showBubble: 气泡已经显示，无需重复显示");
          return;
        }
        e.style.display = "block", setTimeout(() => {
          e.style.opacity = "1", e.style.transform = "translateY(0)", this.startBubbleMessageRotation();
        }, 100), i.debug("showBubble: 气泡已显示");
      } else
        i.debug("showBubble: messageElement 不存在");
    } else
      i.debug("showBubble: bubble 不存在");
  }
  hideBubble() {
    if (this.bubble) {
      const e = this.bubble.messageElement;
      e instanceof HTMLElement && (this.stopBubbleMessageRotation(), this.stopBubbleMessageTransition(), e.style.opacity = "0", e.style.transform = "translateY(10px)", setTimeout(() => {
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
    e.forEach((t, n) => {
      const o = document.createElement("div");
      if (o.style.cssText = `
        padding: 8px 16px;
        cursor: pointer;
        color: #333;
        font-size: 14px;
        
        &:hover {
          background: #f5f5f5;
        }
      `, o.textContent = t.text, o.onclick = () => {
        t.onClick(), this.hideContextMenu();
      }, this.contextMenu && this.contextMenu.appendChild(o), n < e.length - 1) {
        const s = document.createElement("div");
        s.style.cssText = `
          height: 1px;
          background: #eee;
          margin: 4px 0;
        `, this.contextMenu && this.contextMenu.appendChild(s);
      }
    }), document.body.appendChild(this.contextMenu);
  }
  showContextMenu(e) {
    if (e.preventDefault(), this.contextMenu || this.createContextMenu(), this.contextMenu) {
      this.contextMenu.style.visibility = "hidden", this.contextMenu.style.display = "block";
      const t = this.contextMenu.offsetWidth, n = this.contextMenu.offsetHeight;
      let o = e.clientX, s = e.clientY;
      o + t > window.innerWidth && (o = o - t), s + n > window.innerHeight && (s = s - n), o = Math.max(0, o), s = Math.max(0, s), this.contextMenu.style.left = `${o}px`, this.contextMenu.style.top = `${s}px`, this.contextMenu.style.visibility = "visible";
    }
  }
  hideContextMenu() {
    this.contextMenu && (this.contextMenu.style.display = "none");
  }
  togglePlacement() {
    var t, n;
    if (!this.bubble) return;
    this.config.placement = this.config.placement === "bottom-left" ? "bottom-right" : "bottom-left";
    const e = this.bubble.parentElement;
    e && (e.style.left = this.config.placement === "bottom-left" ? `${this.config.marginSide}px` : "auto", e.style.right = this.config.placement === "bottom-right" ? `${this.config.marginSide}px` : "auto", e.style.alignItems = this.config.placement === "bottom-left" ? "flex-start" : "flex-end", this.window && this.isVisible && (this.window.style.left = this.config.placement === "bottom-left" ? `${this.config.marginSide}px` : "auto", this.window.style.right = this.config.placement === "bottom-right" ? `${this.config.marginSide}px` : "auto"), (n = (t = this.config).onConfigChange) == null || n.call(t, { placement: this.config.placement }));
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
    if (i.debug("BytedeskWeb: 初始化文档反馈功能开始"), i.debug("BytedeskWeb: feedbackConfig:", this.config.feedbackConfig), i.debug("BytedeskWeb: feedbackConfig.enabled:", (e = this.config.feedbackConfig) == null ? void 0 : e.enabled), !((t = this.config.feedbackConfig) != null && t.enabled)) {
      i.debug("BytedeskWeb: 文档反馈功能未启用，退出初始化");
      return;
    }
    (this.feedbackTooltip || this.feedbackDialog) && (i.debug("BytedeskWeb: 反馈功能已存在，先销毁再重新创建"), this.destroyFeedbackFeature()), this.config.feedbackConfig.trigger === "selection" || this.config.feedbackConfig.trigger === "both" ? (i.debug("BytedeskWeb: 触发器匹配，设置文本选择监听器"), i.debug("BytedeskWeb: 触发器类型:", this.config.feedbackConfig.trigger), this.setupTextSelectionListener()) : (i.debug("BytedeskWeb: 触发器不匹配，跳过文本选择监听器"), i.debug("BytedeskWeb: 触发器类型:", this.config.feedbackConfig.trigger)), i.debug("BytedeskWeb: 开始创建反馈提示框"), this.createFeedbackTooltip(), i.debug("BytedeskWeb: 开始创建反馈对话框"), this.createFeedbackDialog(), i.debug("BytedeskWeb: 文档反馈功能初始化完成"), i.debug("BytedeskWeb: 反馈提示框存在:", !!this.feedbackTooltip), i.debug("BytedeskWeb: 反馈对话框存在:", !!this.feedbackDialog);
  }
  /**
   * 设置文本选择监听器
   */
  setupTextSelectionListener() {
    i.debug("BytedeskWeb: 设置文本选择监听器"), document.addEventListener("mouseup", (e) => {
      this.lastMouseEvent = e, i.debug("BytedeskWeb: mouseup事件触发", e), this.handleTextSelectionWithDebounce(e);
    }, { capture: !0, passive: !0 }), document.addEventListener("selectionchange", () => {
      if (!this.lastMouseEvent) {
        i.debug("BytedeskWeb: selectionchange事件触发（无鼠标事件）");
        const e = new MouseEvent("mouseup", {
          clientX: window.innerWidth / 2,
          clientY: window.innerHeight / 2
        });
        this.handleTextSelectionWithDebounce(e);
      }
    }), document.addEventListener("keyup", (e) => {
      (e.shiftKey || e.ctrlKey || e.metaKey) && (i.debug("BytedeskWeb: keyup事件触发（带修饰键）", e), this.handleTextSelectionWithDebounce(e));
    }, { capture: !0, passive: !0 }), document.addEventListener("click", (e) => {
      const t = e.target;
      t != null && t.closest("[data-bytedesk-feedback]") || this.hideFeedbackTooltip();
    }), i.debug("BytedeskWeb: 文本选择监听器设置完成");
  }
  /**
   * 带防抖的文本选择处理
   */
  handleTextSelectionWithDebounce(e) {
    this.config.isDebug && i.debug("BytedeskWeb: handleTextSelectionWithDebounce被调用 - 防抖机制生效"), this.selectionDebounceTimer && (clearTimeout(this.selectionDebounceTimer), this.config.isDebug && i.debug("BytedeskWeb: 清除之前的防抖定时器")), this.selectionDebounceTimer = setTimeout(() => {
      this.config.isDebug && i.debug("BytedeskWeb: 防抖延迟结束，开始处理文本选择"), this.handleTextSelection(e);
    }, 200);
  }
  /**
   * 处理文本选择
   */
  handleTextSelection(e) {
    var o, s;
    this.config.isDebug && i.debug("BytedeskWeb: handleTextSelection被调用");
    const t = window.getSelection();
    if (this.config.isDebug && (i.debug("BytedeskWeb: window.getSelection()结果:", t), i.debug("BytedeskWeb: selection.rangeCount:", t == null ? void 0 : t.rangeCount)), !t || t.rangeCount === 0) {
      this.config.isDebug && i.debug("BytedeskWeb: 没有选择或范围为0，隐藏提示"), this.hideFeedbackTooltip();
      return;
    }
    const n = t.toString().trim();
    if (this.config.isDebug && (i.debug("BytedeskWeb: 检测到文本选择:", `"${n}"`), i.debug("BytedeskWeb: 选中文本长度:", n.length)), n === this.lastSelectionText && this.isTooltipVisible) {
      this.config.isDebug && i.debug("BytedeskWeb: 文本选择未变化且提示框已显示，跳过处理");
      return;
    }
    if (n.length === 0) {
      this.config.isDebug && i.debug("BytedeskWeb: 选中文本为空，隐藏提示"), this.hideFeedbackTooltip();
      return;
    }
    if (n.length < 3) {
      this.config.isDebug && i.debug("BytedeskWeb: 选中文本太短，忽略:", `"${n}"`), this.hideFeedbackTooltip();
      return;
    }
    this.selectedText = n, this.lastSelectionText = n;
    try {
      const a = t.getRangeAt(0);
      this.lastSelectionRect = a.getBoundingClientRect(), this.config.isDebug && i.debug("BytedeskWeb: 存储选中文本位置:", this.lastSelectionRect);
    } catch (a) {
      this.config.isDebug && i.warn("BytedeskWeb: 获取选中文本位置失败:", a), this.lastSelectionRect = null;
    }
    this.config.isDebug && i.debug("BytedeskWeb: 设置selectedText为:", `"${n}"`), (o = this.config.feedbackConfig) != null && o.showOnSelection ? (this.config.isDebug && i.debug("BytedeskWeb: 配置允许显示选择提示，调用showFeedbackTooltip"), this.showFeedbackTooltip(this.lastMouseEvent || void 0)) : this.config.isDebug && (i.debug("BytedeskWeb: 配置不允许显示选择提示"), i.debug("BytedeskWeb: feedbackConfig.showOnSelection:", (s = this.config.feedbackConfig) == null ? void 0 : s.showOnSelection));
  }
  /**
   * 创建反馈提示框
   */
  createFeedbackTooltip() {
    var t;
    if (this.config.isDebug && i.debug("BytedeskWeb: createFeedbackTooltip被调用"), this.feedbackTooltip && document.body.contains(this.feedbackTooltip)) {
      this.config.isDebug && i.debug("BytedeskWeb: 反馈提示框已存在且在DOM中，跳过创建");
      return;
    }
    this.feedbackTooltip && !document.body.contains(this.feedbackTooltip) && (this.config.isDebug && i.debug("BytedeskWeb: 提示框变量存在但不在DOM中，重置变量"), this.feedbackTooltip = null), this.feedbackTooltip = document.createElement("div"), this.feedbackTooltip.setAttribute("data-bytedesk-feedback", "tooltip"), this.feedbackTooltip.style.cssText = `
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
    this.config.isDebug && i.debug("BytedeskWeb: 提示框文本:", e), this.feedbackTooltip.innerHTML = `
      <span style="margin-right: 4px;">📝</span>
      ${e}
    `, this.feedbackTooltip.addEventListener("click", async (n) => {
      this.config.isDebug && (i.debug("BytedeskWeb: 反馈提示框被点击"), i.debug("BytedeskWeb: 点击时选中文字:", this.selectedText)), n.stopPropagation(), n.preventDefault();
      try {
        await this.showFeedbackDialog(), this.config.isDebug && i.debug("BytedeskWeb: 对话框显示完成，现在隐藏提示框"), this.hideFeedbackTooltip();
      } catch (o) {
        this.config.isDebug && i.error("BytedeskWeb: 显示对话框时出错:", o);
      }
    }), document.body.appendChild(this.feedbackTooltip), this.config.isDebug && (i.debug("BytedeskWeb: 反馈提示框已创建并添加到页面"), i.debug("BytedeskWeb: 提示框元素:", this.feedbackTooltip));
  }
  /**
   * 显示反馈提示框
   */
  showFeedbackTooltip(e) {
    this.config.isDebug && (i.debug("BytedeskWeb: showFeedbackTooltip被调用"), i.debug("BytedeskWeb: feedbackTooltip存在:", !!this.feedbackTooltip), i.debug("BytedeskWeb: selectedText存在:", !!this.selectedText));
    const t = this.feedbackTooltip && document.body.contains(this.feedbackTooltip);
    if (this.config.isDebug && i.debug("BytedeskWeb: feedbackTooltip在DOM中:", t), (!this.feedbackTooltip || !t) && (this.config.isDebug && i.debug("BytedeskWeb: 提示框不存在或已从DOM中移除，重新创建"), this.createFeedbackTooltip()), !this.feedbackTooltip || !this.selectedText) {
      this.config.isDebug && i.debug("BytedeskWeb: 提示框或选中文本不存在，退出显示");
      return;
    }
    const n = window.getSelection();
    if (!n || n.rangeCount === 0) {
      this.config.isDebug && i.debug("BytedeskWeb: 无有效选择，无法计算位置");
      return;
    }
    const o = n.getRangeAt(0);
    let s;
    try {
      const w = document.createRange();
      w.setStart(o.startContainer, o.startOffset);
      let x = o.startOffset;
      const y = o.startContainer.textContent || "";
      if (o.startContainer.nodeType === Node.TEXT_NODE) {
        for (; x < Math.min(y.length, o.endOffset); ) {
          const m = document.createRange();
          m.setStart(o.startContainer, o.startOffset), m.setEnd(o.startContainer, x + 1);
          const T = m.getBoundingClientRect(), v = w.getBoundingClientRect();
          if (Math.abs(T.top - v.top) > 5)
            break;
          x++;
        }
        w.setEnd(o.startContainer, Math.max(x, o.startOffset + 1)), s = w.getBoundingClientRect();
      } else
        s = o.getBoundingClientRect();
    } catch (w) {
      this.config.isDebug && i.debug("BytedeskWeb: 获取第一行位置失败，使用整个选择区域:", w), s = o.getBoundingClientRect();
    }
    this.config.isDebug && i.debug("BytedeskWeb: 选中文本第一行位置信息:", {
      left: s.left,
      top: s.top,
      right: s.right,
      bottom: s.bottom,
      width: s.width,
      height: s.height
    });
    const a = 120, l = 40, r = 15, c = 5;
    let d = s.left + c, h = s.top - l - r;
    const b = window.innerWidth, g = window.innerHeight, f = window.scrollX, k = window.scrollY;
    d < 10 && (d = 10), d + a > b - 10 && (d = b - a - 10), h < k + 10 && (h = s.bottom + r, this.config.isDebug && i.debug("BytedeskWeb: 上方空间不足，调整为显示在选中文字第一行下方")), d += f, h += k, this.config.isDebug && i.debug("BytedeskWeb: 最终提示框位置:", {
      x: d,
      y: h,
      说明: "显示在选中文字第一行左上角上方，增加间距避免遮挡",
      verticalOffset: r,
      horizontalOffset: c,
      选中区域: s,
      视口信息: { viewportWidth: b, viewportHeight: g, scrollX: f, scrollY: k }
    }), this.feedbackTooltip.style.position = "absolute", this.feedbackTooltip.style.left = d + "px", this.feedbackTooltip.style.top = h + "px", this.feedbackTooltip.style.display = "block", this.feedbackTooltip.style.visibility = "visible", this.feedbackTooltip.style.opacity = "0", this.feedbackTooltip.style.zIndex = "999999", this.config.isDebug && i.debug("BytedeskWeb: 提示框位置已设置，样式:", {
      position: this.feedbackTooltip.style.position,
      left: this.feedbackTooltip.style.left,
      top: this.feedbackTooltip.style.top,
      display: this.feedbackTooltip.style.display,
      visibility: this.feedbackTooltip.style.visibility,
      opacity: this.feedbackTooltip.style.opacity,
      zIndex: this.feedbackTooltip.style.zIndex
    }), this.isTooltipVisible = !0, setTimeout(() => {
      this.feedbackTooltip && this.isTooltipVisible && (this.feedbackTooltip.style.opacity = "1", this.config.isDebug && i.debug("BytedeskWeb: 提示框透明度设置为1，应该可见了"));
    }, 10);
  }
  /**
   * 隐藏反馈提示框
   */
  hideFeedbackTooltip() {
    const e = this.feedbackTooltip && document.body.contains(this.feedbackTooltip);
    if (this.config.isDebug && (i.debug("BytedeskWeb: hideFeedbackTooltip被调用"), i.debug("BytedeskWeb: feedbackTooltip存在:", !!this.feedbackTooltip), i.debug("BytedeskWeb: feedbackTooltip在DOM中:", e)), !this.feedbackTooltip || !e) {
      this.isTooltipVisible = !1, this.lastSelectionText = "", this.config.isDebug && i.debug("BytedeskWeb: 提示框不存在或不在DOM中，仅重置状态");
      return;
    }
    this.isTooltipVisible = !1, this.lastSelectionText = "", this.feedbackTooltip.style.opacity = "0", setTimeout(() => {
      this.feedbackTooltip && document.body.contains(this.feedbackTooltip) && !this.isTooltipVisible ? (this.feedbackTooltip.style.display = "none", this.feedbackTooltip.style.visibility = "hidden", this.config.isDebug && i.debug("BytedeskWeb: 提示框已隐藏")) : this.config.isDebug && this.isTooltipVisible && i.debug("BytedeskWeb: 跳过隐藏操作，提示框状态已改变为可见");
    }, 100);
  }
  /**
   * 创建反馈对话框
   */
  createFeedbackDialog() {
    var t, n, o, s, a, l, r, c;
    if (this.config.isDebug && i.debug("BytedeskWeb: createFeedbackDialog被调用"), this.feedbackDialog && document.body.contains(this.feedbackDialog)) {
      this.config.isDebug && i.debug("BytedeskWeb: 反馈对话框已存在且在DOM中，跳过创建");
      return;
    }
    this.feedbackDialog && !document.body.contains(this.feedbackDialog) && (this.config.isDebug && i.debug("BytedeskWeb: 对话框变量存在但不在DOM中，重置变量"), this.feedbackDialog = null), this.feedbackDialog = document.createElement("div"), this.feedbackDialog.setAttribute("data-bytedesk-feedback", "dialog"), this.feedbackDialog.style.cssText = `
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

      ${(n = this.config.feedbackConfig) != null && n.categoryNames && this.config.feedbackConfig.categoryNames.length > 0 ? `
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #333;">
          <span style="color: #ff4d4f;">*</span> ${((o = this.config.feedbackConfig) == null ? void 0 : o.typesSectionTitle) || "问题类型"} ${((s = this.config.feedbackConfig) == null ? void 0 : s.typesDescription) || "（多选）"}
        </label>
        <div id="bytedesk-feedback-types" style="
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 12px;
          margin-bottom: 8px;
        ">
          ${this.config.feedbackConfig.categoryNames.map((d) => `
            <label style="
              display: flex;
              align-items: flex-start;
              gap: 8px;
              cursor: pointer;
              padding: 8px;
              border-radius: 4px;
              transition: background-color 0.2s;
            " onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
              <input type="checkbox" name="feedback-type" value="${d}" style="
                margin: 2px 0 0 0;
                cursor: pointer;
              ">
              <span style="
                font-size: 14px;
                line-height: 1.4;
                color: #333;
                flex: 1;
              ">${d}</span>
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
        ">${((c = this.config.feedbackConfig) == null ? void 0 : c.submitText) || "提交反馈"}</button>
      </div>

      <div style="margin-top: 12px; text-align: center; font-size: 12px; color: #999;">
        <a href="https://www.weiyuai.cn/" target="_blank" rel="noopener noreferrer" style="color: #aaaaaa; text-decoration: none;">
           微语技术支持
        </a>
      </div>
    `, e.addEventListener("click", (d) => {
      var g, f;
      switch (d.target.getAttribute("data-action")) {
        case "close":
        case "cancel":
          this.hideFeedbackDialog(), (f = (g = this.config.feedbackConfig) == null ? void 0 : g.onCancel) == null || f.call(g);
          break;
        case "submit":
          this.submitFeedback();
          break;
      }
    }), this.feedbackDialog.appendChild(e), this.feedbackDialog.addEventListener("click", (d) => {
      var h, b;
      d.target === this.feedbackDialog && (this.hideFeedbackDialog(), (b = (h = this.config.feedbackConfig) == null ? void 0 : h.onCancel) == null || b.call(h));
    }), document.addEventListener("keydown", (d) => {
      var h, b, g;
      d.key === "Escape" && ((h = this.feedbackDialog) == null ? void 0 : h.style.display) === "flex" && (this.hideFeedbackDialog(), (g = (b = this.config.feedbackConfig) == null ? void 0 : b.onCancel) == null || g.call(b));
    }), document.body.appendChild(this.feedbackDialog);
  }
  /**
   * 显示反馈对话框
   */
  async showFeedbackDialog() {
    this.config.isDebug && (i.debug("BytedeskWeb: showFeedbackDialog被调用"), i.debug("BytedeskWeb: feedbackDialog存在:", !!this.feedbackDialog));
    const e = this.feedbackDialog && document.body.contains(this.feedbackDialog);
    if (this.config.isDebug && i.debug("BytedeskWeb: feedbackDialog在DOM中:", e), (!this.feedbackDialog || !e) && (this.config.isDebug && i.debug("BytedeskWeb: 对话框不存在或已从DOM中移除，重新创建"), this.createFeedbackDialog()), !this.feedbackDialog) {
      this.config.isDebug && i.debug("BytedeskWeb: 对话框创建失败，退出显示");
      return;
    }
    this.config.isDebug && i.debug("BytedeskWeb: 开始填充对话框内容");
    const t = this.feedbackDialog.querySelector("#bytedesk-selected-text");
    t && (t.textContent = this.selectedText || "", this.config.isDebug && i.debug("BytedeskWeb: 已填充选中文字:", this.selectedText));
    const n = this.feedbackDialog.querySelector("#bytedesk-feedback-text");
    n && (n.value = ""), this.feedbackDialog.style.display = "flex", this.config.isDebug && (i.debug("BytedeskWeb: 对话框已设置为显示状态"), i.debug("BytedeskWeb: 对话框样式:", {
      display: this.feedbackDialog.style.display,
      visibility: this.feedbackDialog.style.visibility,
      zIndex: this.feedbackDialog.style.zIndex
    }));
    try {
      await this.generateScreenshotPreview(), this.config.isDebug && i.debug("BytedeskWeb: 截图预览生成完成");
    } catch (o) {
      this.config.isDebug && i.error("BytedeskWeb: 截图预览生成失败:", o);
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
      const n = (e = this.feedbackDialog) == null ? void 0 : e.screenshotCanvas;
      if (n)
        this.config.isDebug && i.debug("BytedeskWeb: 使用已生成的截图canvas"), t = n;
      else {
        const o = await this.loadHtml2Canvas();
        if (!o)
          return this.config.isDebug && i.debug("BytedeskWeb: html2canvas加载失败，跳过截图"), null;
        this.config.isDebug && i.debug("BytedeskWeb: 重新生成截图");
        const s = this.calculateScreenshotArea();
        t = await o(document.body, {
          height: s.height,
          width: s.width,
          x: s.x,
          y: s.y,
          useCORS: !0,
          allowTaint: !0,
          backgroundColor: "#ffffff",
          scale: 1,
          ignoreElements: (a) => a.hasAttribute("data-bytedesk-feedback") || a.closest("[data-bytedesk-feedback]") !== null
        });
      }
      return new Promise((o) => {
        t.toBlob(async (s) => {
          var a;
          if (!s) {
            i.error("无法生成截图blob"), o(null);
            return;
          }
          try {
            const l = `screenshot_${Date.now()}.jpg`, r = new File([s], l, { type: "image/jpeg" });
            this.config.isDebug && i.debug("BytedeskWeb: 截图生成成功，文件大小:", Math.round(s.size / 1024), "KB");
            const { uploadScreenshot: c } = await import("../../apis/upload/index.js"), d = await c(r, {
              orgUid: ((a = this.config.chatConfig) == null ? void 0 : a.org) || "",
              isDebug: this.config.isDebug
            });
            this.config.isDebug && i.debug("BytedeskWeb: 截图上传成功，URL:", d), o(d);
          } catch (l) {
            i.error("截图上传失败:", l), o(null);
          }
        }, "image/jpeg", 0.8);
      });
    } catch (t) {
      return i.error("生成截图失败:", t), null;
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
        const n = await this.loadHtml2Canvas();
        if (!n) {
          e.innerHTML = `
          <div style="color: #999; text-align: center; padding: 20px; flex-direction: column; gap: 8px; display: flex; align-items: center;">
            <div style="font-size: 24px;">📷</div>
            <div>截图功能暂时不可用</div>
            <div style="font-size: 12px; color: #666;">网络连接问题或资源加载失败</div>
          </div>
        `;
          return;
        }
        e.innerHTML = "正在生成截图预览...", this.config.isDebug && i.debug("BytedeskWeb: 开始生成截图预览");
        const o = this.calculateScreenshotArea(), s = await n(document.body, {
          height: o.height,
          width: o.width,
          x: o.x,
          y: o.y,
          useCORS: !0,
          allowTaint: !0,
          backgroundColor: "#ffffff",
          scale: 1,
          ignoreElements: (c) => c.hasAttribute("data-bytedesk-feedback") || c.closest("[data-bytedesk-feedback]") !== null
        }), a = document.createElement("img");
        a.src = s.toDataURL("image/jpeg", 0.8), a.style.cssText = `
        max-width: 100%;
        max-height: 200px;
        border-radius: 4px;
        border: 1px solid #ddd;
        cursor: pointer;
      `, a.onclick = () => {
          const c = document.createElement("img");
          c.src = a.src, c.style.cssText = `
          max-width: 90vw;
          max-height: 90vh;
          border-radius: 8px;
          box-shadow: 0 8px 32px rgba(0,0,0,0.3);
        `;
          const d = document.createElement("div");
          d.style.cssText = `
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
          const h = document.createElement("div");
          h.style.cssText = `
          position: absolute;
          top: 20px;
          right: 20px;
          color: white;
          font-size: 14px;
          background: rgba(0,0,0,0.6);
          padding: 8px 12px;
          border-radius: 4px;
          user-select: none;
        `, h.textContent = "点击任意位置关闭", d.appendChild(h), d.appendChild(c), d.onclick = () => document.body.removeChild(d), document.body.appendChild(d);
        };
        const l = document.createElement("div");
        l.style.cssText = `
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
      `, l.appendChild(a);
        const r = document.createElement("div");
        r.style.cssText = `
        font-size: 12px;
        color: #666;
        text-align: center;
      `, r.innerHTML = "点击图片可放大查看<br/>提交时将自动上传此截图", l.appendChild(r), e.innerHTML = "", e.appendChild(l), this.feedbackDialog.screenshotCanvas = s, this.config.isDebug && i.debug("BytedeskWeb: 截图预览生成成功");
      } catch (n) {
        i.error("生成截图预览失败:", n), e.innerHTML = `
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
        const n = window.getSelection();
        n && n.rangeCount > 0 && (t = n.getRangeAt(0).getBoundingClientRect());
      }
      if (t && t.width > 0 && t.height > 0) {
        const n = window.pageXOffset || document.documentElement.scrollLeft, o = window.pageYOffset || document.documentElement.scrollTop, s = t.left + n, a = t.top + o, l = Math.min(800, window.innerWidth), r = Math.min(600, window.innerHeight);
        let c = s - l / 2, d = a - r / 2;
        const h = document.documentElement.scrollWidth, b = document.documentElement.scrollHeight;
        c = Math.max(0, Math.min(c, h - l)), d = Math.max(0, Math.min(d, b - r)), e = {
          height: r,
          width: l,
          x: c,
          y: d,
          scrollX: 0,
          scrollY: 0
        }, this.config.isDebug && i.debug("BytedeskWeb: 选中文本截图区域:", {
          selectedRect: t,
          absolutePosition: { left: s, top: a },
          captureArea: { x: c, y: d, width: l, height: r },
          pageSize: { width: h, height: b }
        });
      } else if (this.lastMouseEvent) {
        const n = window.pageXOffset || document.documentElement.scrollLeft, o = window.pageYOffset || document.documentElement.scrollTop, s = this.lastMouseEvent.clientX + n, a = this.lastMouseEvent.clientY + o, l = Math.min(800, window.innerWidth), r = Math.min(600, window.innerHeight);
        let c = s - l / 2, d = a - r / 2;
        const h = document.documentElement.scrollWidth, b = document.documentElement.scrollHeight;
        c = Math.max(0, Math.min(c, h - l)), d = Math.max(0, Math.min(d, b - r)), e = {
          height: r,
          width: l,
          x: c,
          y: d,
          scrollX: 0,
          scrollY: 0
        }, this.config.isDebug && i.debug("BytedeskWeb: 鼠标位置截图区域:", {
          mousePosition: { x: this.lastMouseEvent.clientX, y: this.lastMouseEvent.clientY },
          absolutePosition: { x: s, y: a },
          captureArea: { x: c, y: d, width: l, height: r }
        });
      }
    } catch (t) {
      this.config.isDebug && i.warn("BytedeskWeb: 计算截图区域失败，使用默认区域:", t);
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
      return this.config.isDebug && i.warn("html2canvas 加载失败:", e), null;
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
      const n = document.createElement("script");
      n.src = this.config.apiUrl + "/assets/js/html2canvas.min.js", n.onload = () => {
        window.html2canvas ? e(window.html2canvas) : t(new Error("html2canvas 加载失败"));
      }, n.onerror = () => {
        t(new Error("无法从CDN加载html2canvas"));
      }, document.head.appendChild(n);
    });
  }
  /**
   * 提交反馈
   */
  async submitFeedback() {
    var l, r, c, d, h, b, g;
    const e = (l = this.feedbackDialog) == null ? void 0 : l.querySelector("#bytedesk-feedback-text"), t = (e == null ? void 0 : e.value.trim()) || "";
    if (!t) {
      alert("请填写反馈内容"), e == null || e.focus();
      return;
    }
    const n = [], o = (r = this.feedbackDialog) == null ? void 0 : r.querySelectorAll('input[name="feedback-type"]:checked');
    if (o && o.forEach((f) => {
      n.push(f.value);
    }), (c = this.config.feedbackConfig) != null && c.requiredTypes && n.length === 0) {
      alert("请至少选择一个问题类型");
      return;
    }
    const s = (d = this.feedbackDialog) == null ? void 0 : d.querySelector(".bytedesk-feedback-submit"), a = (s == null ? void 0 : s.textContent) || "提交反馈";
    s && (s.disabled = !0, s.textContent = "提交中...", s.style.opacity = "0.6");
    try {
      const f = (h = this.feedbackDialog) == null ? void 0 : h.querySelector("#bytedesk-submit-screenshot"), k = (f == null ? void 0 : f.checked) !== !1;
      let w = [];
      if (k) {
        this.config.isDebug && i.debug("BytedeskWeb: 开始生成和上传截图"), s && (s.textContent = "正在生成截图...");
        const y = await this.generateAndUploadScreenshot();
        y && (w.push(y), this.config.isDebug && i.debug("BytedeskWeb: 截图上传成功:", y)), s && (s.textContent = "正在提交反馈...");
      }
      const x = {
        selectedText: this.selectedText,
        ...w.length > 0 && { images: w },
        // 将截图URL放入images数组
        content: t,
        url: window.location.href,
        title: document.title,
        userAgent: navigator.userAgent,
        visitorUid: localStorage.getItem("bytedesk_uid") || "",
        orgUid: ((b = this.config.chatConfig) == null ? void 0 : b.org) || "",
        ...n.length > 0 && { categoryNames: n.join(",") }
      };
      (g = this.config.feedbackConfig) != null && g.onSubmit ? this.config.feedbackConfig.onSubmit(x) : await this.submitFeedbackToServer(x), this.showFeedbackSuccess(), setTimeout(() => {
        this.hideFeedbackDialog();
      }, 2e3);
    } catch (f) {
      i.error("提交反馈失败:", f), alert("提交失败，请稍后重试");
    } finally {
      s && (s.disabled = !1, s.textContent = a, s.style.opacity = "1");
    }
  }
  /**
   * 提交反馈到服务器
   */
  async submitFeedbackToServer(e) {
    try {
      const { submitFeedback: t } = await import("../../apis/feedback/index.js"), n = await t(e);
      return this.config.isDebug && i.debug("反馈提交响应:", n), n;
    } catch (t) {
      throw i.error("提交反馈到服务器失败:", t), t;
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
      i.warn("文档反馈功能未启用");
      return;
    }
    e && (this.selectedText = e), this.showFeedbackDialog();
  }
  /**
   * 公共方法：重新初始化反馈功能
   */
  reinitFeedbackFeature() {
    this.config.isDebug && i.debug("BytedeskWeb: 重新初始化反馈功能"), this.destroyFeedbackFeature(), this.initFeedbackFeature();
  }
  /**
   * 公共方法：强制初始化反馈功能（用于调试）
   */
  forceInitFeedbackFeature() {
    return i.debug("BytedeskWeb: 强制初始化反馈功能被调用"), i.debug("BytedeskWeb: 当前配置:", this.config.feedbackConfig), i.debug("BytedeskWeb: isDebug:", this.config.isDebug), this.config.feedbackConfig || (i.debug("BytedeskWeb: 创建默认反馈配置"), this.config.feedbackConfig = {
      enabled: !0,
      trigger: "selection",
      showOnSelection: !0,
      selectionText: "📝 文档反馈",
      dialogTitle: "提交意见反馈",
      placeholder: "请详细描述您发现的问题、改进建议或其他意见...",
      submitText: "提交反馈",
      cancelText: "取消",
      successMessage: "感谢您的反馈！我们会认真处理您的意见。"
    }), this.config.feedbackConfig.enabled || (i.debug("BytedeskWeb: 启用反馈配置"), this.config.feedbackConfig.enabled = !0), i.debug("BytedeskWeb: 销毁现有反馈功能"), this.destroyFeedbackFeature(), i.debug("BytedeskWeb: 重新初始化反馈功能"), this.initFeedbackFeature(), i.debug("BytedeskWeb: 强制初始化完成，检查结果:"), i.debug("- showDocumentFeedback方法存在:", typeof this.showDocumentFeedback == "function"), i.debug("- testTextSelection方法存在:", typeof this.testTextSelection == "function"), i.debug("- 反馈提示框存在:", !!this.feedbackTooltip), i.debug("- 反馈对话框存在:", !!this.feedbackDialog), i.debug("- 反馈提示框DOM存在:", !!document.querySelector('[data-bytedesk-feedback="tooltip"]')), i.debug("- 反馈对话框DOM存在:", !!document.querySelector('[data-bytedesk-feedback="dialog"]')), {
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
    this.config.isDebug && i.debug("BytedeskWeb: 测试文本选择功能，模拟选中文字:", `"${e}"`), this.selectedText = e;
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
      const n = document.createRange();
      n.selectNodeContents(t);
      const o = window.getSelection();
      o && (o.removeAllRanges(), o.addRange(n), this.config.isDebug && i.debug("BytedeskWeb: 已创建模拟文本选择"), this.feedbackTooltip ? this.showFeedbackTooltip() : i.error("BytedeskWeb: 反馈提示框不存在，无法测试"), setTimeout(() => {
        o && o.removeAllRanges(), document.body.contains(t) && document.body.removeChild(t), this.hideFeedbackTooltip();
      }, 5e3));
    } catch (t) {
      i.error("BytedeskWeb: 创建测试选择失败:", t);
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
  oe as default
};
