import { n as normalizeLang } from "./index-CpaAJ7mK.js";
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
const SAMPLE_PATH = "/mock/sample.md";
const noopUnsub = () => {
};
let sampleCache = null;
async function sampleText() {
  if (!sampleCache) {
    const res = await fetch(SAMPLE_PATH);
    if (!res.ok) throw new Error(`browser preview: sample document missing (${res.status})`);
    sampleCache = await res.text();
  }
  return sampleCache;
}
let streamListener = null;
const mockApi = {
  consumePending: async () => SAMPLE_PATH,
  readFile: async () => sampleText(),
  // Cancel silently (documented SaveMarkdownResult variant): the editor keeps
  // its in-memory state and the untitled path, exactly like a dismissed dialog.
  save: async () => ({ ok: true, canceled: true }),
  setDirty: () => {
  },
  sendSaveRequestAck: () => {
  },
  sendCloseSaveResult: () => {
  },
  onSaveRequest: () => noopUnsub,
  onCloseSaveRequest: () => noopUnsub,
  onFileRenamed: () => noopUnsub,
  pickImage: async () => null,
  saveImage: async () => null,
  readImage: async () => null,
  onExportRequest: () => noopUnsub,
  onPrintRequest: () => noopUnsub,
  exportDocx: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  exportPdf: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  getLanguage: async () => normalizeLang(navigator.language),
  onLanguageChanged: () => noopUnsub,
  getTheme: async () => "system",
  onThemeChanged: () => noopUnsub,
  onChromePressed: () => noopUnsub,
  getAiSettings: async () => defaultAiSettings(),
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
  webSearch: async () => ({ results: [], method: "error", error: PREVIEW_DISABLED }),
  imageSearch: async () => ({ images: [], method: "error", error: PREVIEW_DISABLED }),
  fetchImage: async () => null,
  aiGenerateImage: async () => ({ error: PREVIEW_DISABLED }),
  // team-sync actions (ribbon sync/share) — disabled in browser preview
  officeDocUpload: async () => ({ error: PREVIEW_DISABLED }),
  copyText: async () => false,
  authGetState: async () => null
};
const projectMock = {
  resolveChat: async () => ({ projectId: "browser-preview", chatId: "mock" }),
  appendChat: async () => {
  },
  loadChat: async () => [],
  rebindChat: async () => ({ projectId: "browser-preview", chatId: "mock" })
};
function installBrowserMock() {
  if (window.markdownApi) return;
  window.markdownApi = mockApi;
  window.projectApi = projectMock;
  console.info(
    "[markdown] browser preview: Electron IPC mocked — UI is explorable, saving/export/AI are no-ops"
  );
}
export {
  installBrowserMock
};
