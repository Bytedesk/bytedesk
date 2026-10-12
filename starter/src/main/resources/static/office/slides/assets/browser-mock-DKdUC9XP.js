import { d as defaultAiSettings, n as normalizeLang } from "./index-7DFVX90e.js";
const PREVIEW_DISABLED = "Browser preview: this operation needs the Electron main process";
const noopUnsub = () => {
};
const SAMPLE_URL = "/mock/sample-deck.json";
function blankOpen() {
  const cx = 12192e3;
  const cy = 6858e3;
  const fitWidthPx = 960;
  const scale = fitWidthPx / (cx / 9525);
  return {
    path: "blank.pptx",
    slides: [
      {
        widthPx: fitWidthPx,
        heightPx: Math.round(fitWidthPx * cy / cx),
        scale,
        background: { kind: "solid", color: "#FFFFFF" },
        nodes: []
      }
    ],
    size: { cx, cy },
    defaultFont: "Calibri"
  };
}
let snapshotPromise = null;
function sampleOpen() {
  snapshotPromise ??= (async () => {
    try {
      const res = await fetch(SAMPLE_URL);
      if (!res.ok) throw new Error(`browser preview: sample deck missing (${res.status})`);
      const payload = await res.json();
      if (!payload?.open) throw new Error("browser preview: sample payload has no .open");
      return payload.open;
    } catch {
      return blankOpen();
    }
  })();
  return snapshotPromise;
}
let streamListener = null;
const known = {
  getLanguage: async () => normalizeLang(navigator.language),
  onLanguageChanged: () => noopUnsub,
  getTheme: async () => "system",
  onThemeChanged: () => noopUnsub,
  onChromePressed: () => noopUnsub,
  setShowFullScreen: async () => {
  },
  openPptx: async () => null,
  openPptxPath: async () => null,
  consumePendingOpen: async () => sampleOpen(),
  newBlank: async () => sampleOpen(),
  landGeneratedPages: async () => {
    throw new Error(PREVIEW_DISABLED);
  },
  privateFontFaces: async () => [],
  privateFontData: async () => null,
  fontCatalog: async () => [],
  fontDownload: async () => ({ ok: false, error: PREVIEW_DISABLED }),
  fontInstallLocal: async () => ({ families: [] }),
  fontMissing: async () => [],
  onFontsChanged: () => noopUnsub,
  getRenderSlides: async () => (await sampleOpen()).slides,
  getSlideSize: async () => (await sampleOpen()).size,
  getLayouts: async () => null,
  // per-slide state fillers: real empty shapes, never undefined — several of
  // these flow straight into render state on mount/open
  getSections: async () => [],
  getTransition: async () => "none",
  getAnimations: async () => [],
  getShapeKeys: async () => [],
  getNotes: async () => "",
  getComments: async () => [],
  getSlideLinks: async () => [],
  getRunLinks: async () => [],
  getLink: async () => null,
  getHeaderFooter: async () => ({ footer: null, slideNum: false, date: null }),
  getChartData: async () => null,
  getMediaData: async () => null,
  cloudGenStatus: async () => ({ enabled: false }),
  undo: async () => (await sampleOpen()).slides,
  redo: async () => (await sampleOpen()).slides,
  isDirty: async () => false,
  getRecentFiles: async () => [],
  hasSlideClipboard: async () => false,
  onDeckChanged: () => noopUnsub,
  onHistoryChanged: (handler) => {
    handler({ canUndo: false, canRedo: false });
    return noopUnsub;
  },
  onOpened: () => noopUnsub,
  onRenamed: () => noopUnsub,
  onMenuCommand: () => noopUnsub,
  onCloseSaveRequest: () => noopUnsub,
  reportCloseSaveResult: () => {
  },
  setAutoSavePref: () => {
  },
  getAiSettings: async () => defaultAiSettings(),
  setAiSettings: async () => {
  },
  aiGskStatus: async () => ({ loggedIn: false }),
  aiGskLogin: async () => {
  },
  aiLogRunFailure: async () => {
  },
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
  imageSearch: async () => ({ images: [], method: "error", error: PREVIEW_DISABLED })
  // everything else (edits, tables, charts, media, masters, notes, comments,
  // sections, animations, export, clipboard, …) → Proxy fallback below
};
const mockApi = new Proxy(known, {
  get(target, prop) {
    if (prop in target) return target[prop];
    if (prop.startsWith("on")) return () => noopUnsub;
    return async () => void 0;
  }
});
function installBrowserMock() {
  if (window.slidesApi) return;
  window.slidesApi = mockApi;
  window.projectApi = {
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
  console.info(
    "[slides] browser preview: Electron IPC mocked — static deck served, view-only (edits no-op)"
  );
}
export {
  installBrowserMock
};
