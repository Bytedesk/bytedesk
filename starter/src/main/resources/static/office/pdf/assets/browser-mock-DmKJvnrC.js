import { n as normalizeLang, _ as __vitePreload } from "./index-CAfj7CyG.js";
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
const SAMPLE_PATH = "/mock/sample.pdf";
const noopUnsub = () => {
};
let sampleCache = null;
async function sampleBytes() {
  if (!sampleCache) {
    try {
      const res = await fetch(SAMPLE_PATH);
      if (!res.ok) throw new Error(`browser preview: sample document missing (${res.status})`);
      const buf = await res.arrayBuffer();
      const prefix = new Uint8Array(buf.slice(0, 4));
      if (prefix[0] !== 37 || prefix[1] !== 80 || prefix[2] !== 68 || prefix[3] !== 70) {
        throw new Error("browser preview: sample response is not a PDF");
      }
      sampleCache = buf;
    } catch {
      const { PDFDocument } = await __vitePreload(async () => {
        const { PDFDocument: PDFDocument2 } = await import("./index-a5xaXGXv.js");
        return { PDFDocument: PDFDocument2 };
      }, true ? [] : void 0, import.meta.url);
      const doc = await PDFDocument.create();
      doc.addPage([595.28, 841.89]);
      const bytes = await doc.save();
      sampleCache = new ArrayBuffer(bytes.byteLength);
      new Uint8Array(sampleCache).set(bytes);
    }
  }
  return sampleCache.slice(0);
}
let streamListener = null;
const mockApi = {
  consumePending: async () => SAMPLE_PATH,
  readFile: async () => sampleBytes(),
  // Pretend the save worked: the app reloads the (unchanged) sample and clears
  // pending overlays — same flow as a real save, without touching anything.
  // Failing loudly here would spam error toasts on every blur-autosave.
  save: async () => ({ ok: true }),
  autoRename: async () => ({ renamed: false }),
  isUntitled: async () => false,
  validateTextEdits: async ({ edits }) => edits.map(() => ({ reason: "Browser preview: edits are not applied" })),
  listEditFonts: async () => ["arial", "times", "courier"],
  canDrawText: async () => true,
  listPageImages: async () => [],
  listStaticFormFills: async () => [],
  ocrPage: async () => null,
  // documented contract: null = no engine on this platform
  pageImagePng: async () => null,
  pagePreviewPng: async () => null,
  // page ops: variants with a `canceled: true` outcome cancel silently; the
  // rest report the preview limitation through the app's error toast
  extractPages: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  insertPdf: async () => ({ ok: true, canceled: true }),
  insertBlankPage: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  splitPdf: async () => ({ ok: true, canceled: true }),
  mergePdf: async () => ({ ok: true, canceled: true }),
  mergePages: async () => ({ ok: true, canceled: true }),
  replacePages: async () => ({ ok: true, canceled: true }),
  setPageSize: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  splitPages: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  cropPages: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  exportImages: async () => ({ ok: true, canceled: true }),
  convertOffice: async () => {
  },
  createDocument: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  imageSearch: async () => ({ images: [], method: "error", error: PREVIEW_DISABLED }),
  fetchImage: async () => null,
  generateImage: async () => ({ error: PREVIEW_DISABLED }),
  listSavedSignatures: async () => [],
  addSavedSignature: async (data) => [
    { id: `browser-preview-${Date.now()}`, createdAt: Date.now(), data }
  ],
  removeSavedSignature: async () => [],
  getUsername: async () => "Browser Preview",
  setDirty: () => {
  },
  sendCloseSaveResult: () => {
  },
  sendSaveAsResult: () => {
  },
  onCloseSaveRequest: () => noopUnsub,
  onSaveAsRequest: () => noopUnsub,
  onSaveAsFlow: () => noopUnsub,
  onPrintRequest: () => noopUnsub,
  getLanguage: async () => normalizeLang(navigator.language),
  onLanguageChanged: () => noopUnsub,
  getTheme: async () => "system",
  onThemeChanged: () => noopUnsub,
  onChromePressed: () => noopUnsub,
  getAiSettings: async () => defaultAiSettings(),
  gskStatus: async () => ({ loggedIn: false }),
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
  if (window.pdfApi) return;
  window.pdfApi = mockApi;
  window.projectApi = projectMock;
  console.info(
    "[pdf] browser preview: Electron IPC mocked — UI is explorable, saving/AI are no-ops"
  );
}
export {
  installBrowserMock
};
