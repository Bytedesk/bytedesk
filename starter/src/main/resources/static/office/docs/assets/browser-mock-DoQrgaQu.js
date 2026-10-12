import { d as defaultAiSettings, n as normalizeLang } from "./index-DC8pB30D.js";
const PREVIEW_DISABLED = "Browser preview: this operation needs the Electron main process";
const SAMPLE_PATH = "/mock/sample.docx";
const noopUnsub = () => {
};
let sampleCache = null;
async function sampleBytes() {
  if (!sampleCache) {
    const res = await fetch(SAMPLE_PATH);
    if (!res.ok) throw new Error(`browser preview: sample document missing (${res.status})`);
    const buf = await res.arrayBuffer();
    const prefix = new Uint8Array(buf.slice(0, 2));
    if (prefix[0] !== 80 || prefix[1] !== 75) {
      throw new Error("browser preview: sample response is not a docx");
    }
    sampleCache = buf;
  }
  return sampleCache.slice(0);
}
async function sha256Hex(buf) {
  const digest = await crypto.subtle.digest("SHA-256", buf);
  return Array.from(new Uint8Array(digest)).map((b) => b.toString(16).padStart(2, "0")).join("");
}
let streamListener = null;
const mockApi = {
  getLanguage: async () => normalizeLang(navigator.language),
  onLanguageChanged: () => noopUnsub,
  getTheme: async () => "system",
  onThemeChanged: () => noopUnsub,
  onChromePressed: () => noopUnsub,
  // serve the bundled sample as the pending-open document; hash mirrors the
  // OpenFileResult contract (used for crash-recovery keying)
  consumePendingOpenDocx: async () => {
    try {
      const data = await sampleBytes();
      return {
        path: SAMPLE_PATH,
        name: "sample.docx",
        data,
        hash: await sha256Hex(data)
      };
    } catch {
      return null;
    }
  },
  consumeNewBlankDoc: async () => false,
  consumeAiDocContent: async () => null,
  openDocx: async () => null,
  openDocxPath: async () => null,
  openDocxDecrypt: async () => ({ ok: false, reason: "error", error: PREVIEW_DISABLED }),
  onOpenDocx: () => noopUnsub,
  onRenamedDocx: () => noopUnsub,
  setDocPassword: async () => ({ ok: false }),
  docPasswordIntentRevision: async () => 0,
  discardDocPasswordIntents: async () => ({ ok: true }),
  // pretend saves succeed: the editor clears its dirty flag and keeps
  // editing in memory — same flow as a real save without touching anything
  saveDocx: async () => ({ ok: true }),
  saveDocxNew: async () => ({ ok: true, path: SAMPLE_PATH }),
  saveDocxAs: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  writeRecoveryCopy: async () => ({ ok: true }),
  onTeardown: () => noopUnsub,
  onCloseCheck: () => noopUnsub,
  reportCloseCheck: () => {
  },
  onCloseSaveRequest: () => noopUnsub,
  reportCloseSaveResult: () => {
  },
  reportViewMenuState: () => {
  },
  createDocument: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  getRecentFiles: async () => [],
  pickImage: async () => null,
  fontMetrics: async () => null,
  print: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  exportPdf: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  printPdfBuffer: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  saveMergedPdf: async () => ({ ok: false, error: PREVIEW_DISABLED }),
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
  onAuthChanged: () => () => {
  },
  webSearch: async () => ({ results: [], method: "error", error: PREVIEW_DISABLED }),
  imageSearch: async () => ({ images: [], method: "error", error: PREVIEW_DISABLED }),
  fetchImage: async () => null,
  aiGenerateImage: async () => ({ error: PREVIEW_DISABLED }),
  pickAttachments: async () => null,
  addAttachmentPaths: async () => ({ accepted: [], rejected: [PREVIEW_DISABLED] }),
  addPastedImage: async () => ({ accepted: [], rejected: [PREVIEW_DISABLED] }),
  copyImageToClipboard: async () => false,
  readAttachment: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  readAttachmentImage: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  getPathForFile: () => "",
  openNewTab: async () => {
  },
  listDocsTabs: async () => [],
  focusDocsTab: async () => {
  },
  onMenuCommand: () => noopUnsub,
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
  rebindChat: async () => ({ projectId: "browser-preview", chatId: "mock" }),
  listProjects: async () => [],
  createProject: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  renameProject: async () => ({ ok: false }),
  deleteProject: async () => ({ ok: false }),
  moveFile: async () => ({ ok: false }),
  getTimeline: async () => []
};
function installBrowserMock() {
  if (window.desktop) return;
  window.desktop = mockApi;
  window.projectApi = projectMock;
  console.info(
    "[docs] browser preview: Electron IPC mocked — UI is explorable, saving/export/AI are no-ops"
  );
}
export {
  installBrowserMock
};
