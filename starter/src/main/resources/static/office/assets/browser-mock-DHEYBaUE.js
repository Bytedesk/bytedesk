import { s as strings, d as defaultAiSettings, n as normalizeLang } from "./index-BLV0hXKr.js";
const PREVIEW_DISABLED = "Browser preview: this operation needs the Electron main process";
const noopUnsub = () => {
};
const emptyPage = () => ({ entries: [], total: 0, totalAll: 0 });
let previewLang = normalizeLang(navigator.language);
function editorUrl(kind) {
  const base = location.pathname.replace(/index\.html$/, "").replace(/([^/])$/, "$1/");
  return `${base}${kind}/index.html`;
}
let previewTabs = [{ id: "home", kind: "home", title: "", active: true }];
const tabHandlers = /* @__PURE__ */ new Set();
let nextTabId = 1;
let tabsHost = null;
const tabFrames = /* @__PURE__ */ new Map();
function editorLabels() {
  const s = strings[previewLang];
  return {
    docs: s.newDoc,
    sheets: s.newSheet,
    slides: s.newSlide,
    markdown: s.newMarkdown,
    pdf: s.newPdf
  };
}
function ensureTabsHost() {
  if (tabsHost) return;
  tabsHost = document.createElement("div");
  tabsHost.style.cssText = [
    "position: fixed",
    "top: 40px",
    "left: 0",
    "right: 0",
    "bottom: 0",
    "z-index: 8",
    "display: none",
    "background: var(--surface)"
  ].join("; ");
  document.body.append(tabsHost);
}
function syncTabViews() {
  ensureTabsHost();
  const active = previewTabs.find((t) => t.active);
  const editors = previewTabs.filter((t) => t.kind !== "home");
  for (const [id, frame] of tabFrames) {
    if (!editors.some((t) => t.id === id)) {
      frame.remove();
      tabFrames.delete(id);
    }
  }
  for (const tab of editors) {
    let frame = tabFrames.get(tab.id);
    if (!frame) {
      frame = document.createElement("iframe");
      frame.src = editorUrl(tab.kind);
      frame.title = tab.title;
      frame.style.cssText = "width: 100%; height: 100%; border: 0; display: none";
      tabsHost.append(frame);
      tabFrames.set(tab.id, frame);
    }
    frame.style.display = active?.id === tab.id ? "block" : "none";
  }
  tabsHost.style.display = !active || active.kind === "home" ? "none" : "block";
}
function toTabSummary() {
  return previewTabs.map((t) => ({
    id: t.id,
    kind: t.kind,
    title: t.title,
    closable: t.kind !== "home",
    active: t.active
  }));
}
function notifyTabs() {
  const snapshot = toTabSummary();
  for (const handler of tabHandlers) handler(snapshot);
}
function createEditorTab(kind) {
  closePreviewMenu();
  const id = `t${nextTabId++}`;
  previewTabs = previewTabs.map((t) => ({ ...t, active: false }));
  previewTabs.push({ id, kind, title: editorLabels()[kind], active: true });
  syncTabViews();
  notifyTabs();
}
function activatePreviewTab(id) {
  if (!previewTabs.some((t) => t.id === id)) return;
  previewTabs = previewTabs.map((t) => ({ ...t, active: t.id === id }));
  syncTabViews();
  notifyTabs();
}
function closePreviewTab(id) {
  const idx = previewTabs.findIndex((t) => t.id === id);
  if (idx < 0 || previewTabs[idx].kind === "home") return;
  const wasActive = previewTabs[idx].active;
  const rest = previewTabs.filter((t) => t.id !== id);
  if (wasActive) {
    const neighbor = rest[Math.min(idx, rest.length - 1)];
    if (neighbor) neighbor.active = true;
  }
  previewTabs = rest;
  syncTabViews();
  notifyTabs();
}
let previewMenu = null;
function closePreviewMenu() {
  previewMenu?.remove();
  previewMenu = null;
  window.removeEventListener("pointerdown", onMenuDismiss, true);
  window.removeEventListener("keydown", onMenuKey, true);
  window.removeEventListener("resize", closePreviewMenu);
}
function onMenuDismiss(event) {
  if (previewMenu && !previewMenu.contains(event.target)) closePreviewMenu();
}
function onMenuKey(event) {
  if (event.key === "Escape") closePreviewMenu();
}
function showPreviewMenu(x, y, items) {
  closePreviewMenu();
  const menu = document.createElement("div");
  menu.setAttribute("role", "menu");
  menu.style.cssText = [
    "position: fixed",
    "z-index: 2147483647",
    "min-width: 220px",
    "padding: 4px",
    "border-radius: 8px",
    "border: 1px solid var(--border)",
    "background: var(--surface)",
    "box-shadow: var(--shadow-menu)",
    "font: 13px/1.4 var(--gs-font-sans, system-ui, sans-serif)",
    "color: var(--text)"
  ].join("; ");
  for (const item of items) {
    const row = document.createElement(item.onPick ? "button" : "div");
    if (row instanceof HTMLButtonElement) row.type = "button";
    row.setAttribute("role", "menuitem");
    if (item.disabled) row.setAttribute("aria-disabled", "true");
    row.style.cssText = [
      "display: flex",
      "align-items: baseline",
      "gap: 8px",
      "width: 100%",
      "padding: 7px 10px",
      "border: 0",
      "border-radius: 5px",
      "background: none",
      "text-align: left",
      "cursor: default",
      item.onPick ? "cursor: pointer" : "",
      item.disabled ? "color: var(--text-tertiary)" : ""
    ].filter(Boolean).join("; ");
    const label = document.createElement("span");
    label.textContent = item.label;
    row.append(label);
    if (item.hint) {
      const hint = document.createElement("span");
      hint.textContent = item.hint;
      hint.style.cssText = "margin-left: auto; color: var(--text-tertiary); font-size: 11px";
      row.append(hint);
    }
    if (item.onPick) {
      const pick = item.onPick;
      row.addEventListener("click", () => {
        closePreviewMenu();
        pick();
      });
      row.addEventListener("pointerenter", () => {
        row.style.background = "var(--hover)";
      });
      row.addEventListener("pointerleave", () => {
        row.style.background = "none";
      });
    }
    menu.append(row);
  }
  document.body.append(menu);
  const rect = menu.getBoundingClientRect();
  const left = Math.min(x, window.innerWidth - rect.width - 8);
  const top = rect.bottom + y > window.innerHeight ? Math.max(8, y - rect.height) : y;
  menu.style.left = `${Math.max(8, left)}px`;
  menu.style.top = `${top}px`;
  previewMenu = menu;
  window.addEventListener("pointerdown", onMenuDismiss, true);
  window.addEventListener("keydown", onMenuKey, true);
  window.addEventListener("resize", closePreviewMenu);
}
const newFileItems = () => {
  const s = strings[previewLang];
  const exts = [
    ["newDoc", "docs", ".docx"],
    ["newSheet", "sheets", ".xlsx"],
    ["newSlide", "slides", ".pptx"],
    ["newMarkdown", "markdown", ".md"],
    ["newPdf", "pdf", ".pdf"]
  ];
  return [
    ...exts.map(([key, kind, ext]) => ({
      label: s[key],
      hint: ext,
      onPick: () => createEditorTab(kind)
    })),
    { label: s.openLocal, disabled: true }
  ];
};
const EMBED_AUTH_KEY = "bytedesk-office.embed-auth";
function readEmbedAuth() {
  try {
    const raw = localStorage.getItem(EMBED_AUTH_KEY);
    if (!raw) return null;
    const st = JSON.parse(raw);
    return st.accessToken && st.serverUrl ? st : null;
  } catch {
    return null;
  }
}
function stripEmbedParams(params) {
  let changed = false;
  for (const k of ["accessToken", "server", "serverUrl"]) {
    if (params.has(k)) {
      params.delete(k);
      changed = true;
    }
  }
  if (!changed) return;
  const qs = params.toString();
  history.replaceState(null, "", location.pathname + (qs ? `?${qs}` : "") + location.hash);
}
const embedAuthReady = (async () => {
  if (typeof location === "undefined") return;
  const params = new URLSearchParams(location.search);
  const token = params.get("accessToken");
  const server = (params.get("server") || params.get("serverUrl") || "").replace(/\/+$/, "");
  if (!token || !/^https?:\/\//i.test(server)) {
    stripEmbedParams(params);
    return;
  }
  try {
    const resp = await fetch(`${server}/auth/v1/login/accessToken`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      // channel mirrors the desktop console's own call to the same endpoint
      body: JSON.stringify({ accessToken: token, channel: "WEB_OFFICE" })
    });
    const json = await resp.json();
    if (json.code === 200 && json.data?.accessToken) {
      localStorage.setItem(
        EMBED_AUTH_KEY,
        JSON.stringify({
          serverUrl: server,
          accessToken: json.data.accessToken,
          user: json.data.user ?? null
        })
      );
    } else {
      console.warn("[shell] embed access-token sign-in rejected:", json.code);
    }
  } catch (err) {
    console.warn("[shell] embed access-token sign-in failed:", err);
  } finally {
    stripEmbedParams(params);
  }
})();
const homeApi = {
  getLanguage: async () => {
    previewLang = normalizeLang(navigator.language);
    return previewLang;
  },
  setLanguage: async () => {
  },
  onLanguageChanged: () => noopUnsub,
  getTheme: async () => "system",
  setTheme: async () => {
  },
  onThemeChanged: () => noopUnsub,
  // first-run onboarding never shows in a browser preview
  onboardingSeen: async () => true,
  setOnboardingSeen: async () => true,
  starPromptShouldShow: async () => ({ show: false, docOpens: 0 }),
  starPromptAction: async () => {
  },
  // home lists: everything empty, counts zero
  recents: async () => emptyPage(),
  starred: async () => emptyPage(),
  statPaths: async () => [],
  toggleStar: async () => {
  },
  removeRecent: async () => {
  },
  deleteFiles: async () => {
  },
  duplicateFile: async () => {
  },
  renameFile: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  revealPath: async () => {
  },
  openTrash: async () => {
  },
  // Creating a document opens the editor as an in-window tab hosted on an
  // iframe (each editor serves a blank/sample doc via its own browser mock)
  // — the closest equivalent of the main process spawning a WebContentsView.
  openPath: async () => {
  },
  browse: async () => {
    showPreviewMenu(
      Math.round(window.innerWidth / 2 - 110),
      Math.round(window.innerHeight / 3),
      newFileItems()
    );
  },
  newDoc: async () => createEditorTab("docs"),
  newSheet: async () => createEditorTab("sheets"),
  newSlide: async () => createEditorTab("slides"),
  newMarkdown: async () => createEditorTab("markdown"),
  newPdf: async () => createEditorTab("pdf"),
  // signed out in a plain browser; signed in when the embedding host passed
  // a valid accessToken via URL (see embedAuthReady above)
  accountStatus: async () => {
    await embedAuthReady;
    const st = readEmbedAuth();
    if (!st) return { loggedIn: false };
    const email = st.user?.email || st.user?.username;
    return { loggedIn: true, ...email ? { email } : {}, serverUrl: st.serverUrl };
  },
  accountLogin: async () => false,
  onAccountLogin: () => noopUnsub,
  onShowLogin: () => noopUnsub,
  accountLogout: async () => {
    localStorage.removeItem(EMBED_AUTH_KEY);
  },
  authGetState: async () => {
    await embedAuthReady;
    const st = readEmbedAuth();
    return {
      loggedIn: Boolean(st),
      serverUrl: st?.serverUrl ?? "",
      ...st?.serverUrl ? { defaultServerUrl: st.serverUrl } : {},
      user: st?.user ?? null
    };
  },
  authLogin: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  authLoginMobile: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  authSendMobileCode: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  authCheckKaptcha: async () => false,
  authKaptcha: async () => ({ error: PREVIEW_DISABLED }),
  authServerConfig: async () => ({}),
  authOpenExternal: async () => {
  },
  authLogout: async () => {
    localStorage.removeItem(EMBED_AUTH_KEY);
  },
  // team documents: signed-out empty pages; the file picker cancels so the
  // upload chain never proceeds
  officeDocsList: async () => ({ content: [], totalElements: 0, totalPages: 1, number: 0, size: 20 }),
  officeProjectsList: async () => ({ content: [], totalElements: 0, totalPages: 1, number: 0, size: 20 }),
  officeProjectSync: async () => ({ ok: false }),
  pickDocumentFile: async () => ({ canceled: true, filePaths: [] }),
  openExternalUrl: async () => {
  },
  openLoginUrl: async () => {
  },
  openGenTeam: async () => {
  },
  openCreditUsage: async () => {
  },
  openGitHubRepo: async () => {
  },
  copyText: async () => false,
  // settings panel: defaults everywhere
  getAppVersion: async () => "dev",
  githubStars: async () => null,
  getUpdateChannel: async () => "stable",
  setUpdateChannel: async () => {
  },
  // analytics disabled (2026-09-07): getAnalyticsEnabled: async () => true,
  // analytics disabled: setAnalyticsEnabled: async () => true,
  getDefaultSaveDir: async () => "",
  pickDefaultSaveDir: async () => null,
  getAiSettings: async () => defaultAiSettings(),
  setAiSettings: async () => {
  },
  getAiProviders: () => [],
  testAiSettings: async () => ({ ok: false, error: PREVIEW_DISABLED })
};
const mockHomeApi = new Proxy(homeApi, {
  get(target, prop) {
    if (prop in target) return target[prop];
    if (prop.startsWith("on")) return () => noopUnsub;
    return async () => void 0;
  }
});
const tabsApi = {
  // stateful in-window tab strip: Home pinned first, editor tabs hosted on
  // iframes below the strip (see syncTabViews) — same semantics as the
  // main-process tab manager in Electron
  list: async () => toTabSummary(),
  activate: async (id) => activatePreviewTab(id),
  close: async (id) => closePreviewTab(id),
  showMenu: (x, y) => {
    const s = strings[previewLang];
    showPreviewMenu(
      x,
      y,
      previewTabs.map((t) => ({
        label: t.kind === "home" ? s.loginBrandName : t.title,
        onPick: t.kind === "home" ? void 0 : () => activatePreviewTab(t.id),
        disabled: t.kind === "home"
      }))
    );
    return Promise.resolve();
  },
  showNewMenu: (x, y) => {
    showPreviewMenu(x, y, newFileItems());
    return Promise.resolve();
  },
  reorder: async (id, toIndex) => {
    const from = previewTabs.findIndex((t) => t.id === id);
    if (from < 1) return;
    const clamped = Math.max(1, Math.min(toIndex, previewTabs.length - 1));
    const moved = previewTabs[from];
    const rest = previewTabs.filter((_, i) => i !== from);
    previewTabs = [...rest.slice(0, clamped), moved, ...rest.slice(clamped)];
    notifyTabs();
  },
  onChanged: (handler) => {
    tabHandlers.add(handler);
    return () => {
      tabHandlers.delete(handler);
    };
  },
  // In Electron this tells the editor WebContentsViews to dismiss their
  // popovers. It must NOT close the preview menu: it fires on every
  // document pointerdown (capture), i.e. on the menu items themselves —
  // closing here would destroy the menu before the click event reaches the
  // item. The menu has its own outside-press dismissal.
  notifyChromePressed: () => {
  },
  onChromePressed: () => noopUnsub
};
function installBrowserMock() {
  if (window.aiOffice) return;
  window.aiOffice = mockHomeApi;
  window.aiOfficeTabs = tabsApi;
  const style = document.createElement("style");
  style.textContent = ".tab-bar { -webkit-app-region: no-drag !important }";
  document.head.append(style);
  console.info(
    "[shell] browser preview: Electron IPC mocked — new documents open as in-window editor tabs"
  );
}
export {
  installBrowserMock
};
