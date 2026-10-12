import { n as normalizeLang } from "./index-OqF_uRM0.js";
var define_process_env_default = {};
const envDefaultServer = typeof process !== "undefined" ? define_process_env_default?.BYTEDESK_DOCS_DEFAULT_SERVER : void 0;
const BYTEDESK_DOCS_DEFAULT_SERVER = envDefaultServer?.trim() ? envDefaultServer.trim().replace(/\/+$/, "") : "https://api.weiyuai.cn";
const AI_PROVIDERS = [
  {
    id: "bytedesk",
    label: "Bytedesk（微语）",
    // the server routes to its own configured model; the model list stays a
    // free text field until the server reports models
    models: [],
    defaultModel: "",
    keyPlaceholder: "Not required — sign in to Bytedesk",
    needsBaseUrl: true,
    baseUrlLabel: "Server URL"
  },
  {
    id: "custom",
    label: "Custom (BYOK)",
    models: [],
    defaultModel: "",
    keyPlaceholder: "API Key",
    needsBaseUrl: true
  }
];
function defaultAiSettings(defaultApiKeys) {
  const providers = {};
  for (const meta of AI_PROVIDERS) {
    providers[meta.id] = {
      apiKey: "",
      model: meta.defaultModel,
      baseUrl: meta.needsBaseUrl ? meta.id === "bytedesk" ? BYTEDESK_DOCS_DEFAULT_SERVER : "" : void 0
    };
  }
  return { provider: "bytedesk", providers };
}
function metaOf(id) {
  return AI_PROVIDERS.find((m) => m.id === id);
}
({
  bytedesk: {
    meta: metaOf("bytedesk")
  },
  custom: {
    meta: metaOf("custom")
  }
});
const PREVIEW_DISABLED = "Browser preview: this operation needs the Electron main process";
const noopUnsub = () => {
};
let streamListener = null;
const known = {
  getLanguage: async () => normalizeLang(navigator.language),
  onLanguageChanged: () => noopUnsub,
  getTheme: async () => "system",
  onThemeChanged: () => noopUnsub,
  onChromePressed: () => noopUnsub,
  // The window always starts blank; consume the one-shot flags so the boot
  // path behaves like a fresh Electron session with nothing queued.
  consumeNewBlankWorkbook: async () => false,
  hasQueuedWorkbook: async () => false,
  notifyPendingEdits: () => {
  },
  onRecoveryPrompt: () => noopUnsub,
  replyRecoveryPrompt: () => {
  },
  onWorkbookRenamed: () => noopUnsub,
  onMenuAction: () => noopUnsub,
  onCloseSaveRequest: () => noopUnsub,
  reportCloseSaveResult: () => {
  },
  // File dialogs never pick anything: the open/merge flows report "canceled"
  // and the in-memory blank workbook stays as-is.
  selectWorkbook: async () => null,
  selectWorkbooksForMerge: async () => null,
  openWorkbooksForMerge: async () => null,
  closeWorkbook: async () => {
  },
  // Saves report "canceled": no bytes are written (there is no main process),
  // the edit journal stays pending, and the editor keeps working in memory.
  saveWorkbookEdits: async () => ({ canceled: true }),
  writeWorkbookRecovery: async () => ({ ok: false }),
  confirmCsvSave: async () => "cancel",
  autoRenameWorkbook: async () => ({ renamed: false }),
  exportPdf: async () => ({ canceled: true }),
  exportCsv: async () => ({ canceled: true }),
  createDocument: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  getAiSettings: async () => defaultAiSettings(),
  setAiSettings: async () => {
  },
  aiChat: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  // close the run with an error chunk so the chat UI never hangs on a stream
  // that no main process will ever answer
  aiStream: async (request) => {
    window.setTimeout(() => {
      streamListener?.({ type: "error", requestId: request.requestId, error: PREVIEW_DISABLED });
    }, 300);
  },
  aiStreamCancel: async () => {
  },
  onAiStream: (handler) => {
    streamListener = handler;
    return () => {
      streamListener = null;
    };
  },
  aiGskStatus: async () => ({ loggedIn: false }),
  aiGskLogin: async () => {
  },
  webSearch: async () => ({ results: [], method: "error", error: PREVIEW_DISABLED }),
  imageSearch: async () => ({ images: [], method: "error", error: PREVIEW_DISABLED }),
  generateImage: async () => ({ error: PREVIEW_DISABLED }),
  fetchImage: async () => null,
  // Screenshot capture needs the main process (desktopCapturer + macOS screen
  // recording permission); the picker shows its denied-guidance state.
  captureScreenSources: async () => ({ status: "denied", sources: [] }),
  captureScreenSource: async () => null,
  pickAttachments: async () => null,
  addAttachmentPaths: async () => ({ accepted: [], rejected: [PREVIEW_DISABLED] }),
  addPastedImage: async () => ({ accepted: [], rejected: [PREVIEW_DISABLED] }),
  readAttachment: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  readAttachmentImage: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  getPathForFile: () => "",
  openExternal: async () => {
  },
  copyText: async () => false,
  officeDocUpload: async () => ({ error: PREVIEW_DISABLED }),
  authGetState: async () => null
};
const mockApi = new Proxy(known, {
  get(target, prop) {
    if (prop in target) return target[prop];
    if (prop.startsWith("on")) return () => noopUnsub;
    return async () => void 0;
  }
});
function installBrowserMock() {
  if (window.desktopApi) return;
  window.desktopApi = mockApi;
  console.info(
    "[sheets] browser preview: Electron IPC mocked — blank workbook, edits stay in memory (save/export disabled)"
  );
}
export {
  installBrowserMock
};
