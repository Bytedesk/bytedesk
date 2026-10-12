import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale = { "find-replace": {
  toolbar: "尋找替換",
  shortcut: {
    "open-find-dialog": "打開尋找對話框",
    "open-replace-dialog": "打開取代對話框",
    "close-dialog": "關閉尋找與取代對話框",
    "go-to-next-match": "前往下一個匹配項",
    "go-to-previous-match": "前往上一個匹配項",
    "focus-selection": "聚焦選取範圍",
    panel: "尋找替換"
  },
  dialog: {
    title: "尋找",
    find: "尋找",
    replace: "取代",
    "replace-all": "全部取代",
    "case-sensitive": "區分大小寫",
    "find-placeholder": "在此工作表中尋找",
    "advanced-finding": "進階搜尋與取代",
    "replace-placeholder": "輸入取代字串",
    "match-the-whole-cell": "完全符合儲存格內容",
    "find-direction": {
      title: "尋找方向",
      row: "按列搜尋",
      column: "按欄搜尋"
    },
    "find-scope": {
      title: "尋找範圍",
      "current-sheet": "目前工作表",
      workbook: "整個活頁簿"
    },
    "find-by": {
      title: "尋找方式",
      value: "按值尋找",
      formula: "尋找公式"
    },
    "no-match": "已完成尋找，但未找到任何相符項目。",
    "no-result": "無結果"
  },
  replace: {
    "all-success": "已取代全部 {0} 個相符項目",
    "all-failure": "取代失敗",
    confirm: { title: "確定要取代所有相符項目嗎？" }
  },
  button: {
    confirm: "確定",
    cancel: "取消"
  }
} };
const t = mergeLocales(
  locale
);
export {
  t as default
};
