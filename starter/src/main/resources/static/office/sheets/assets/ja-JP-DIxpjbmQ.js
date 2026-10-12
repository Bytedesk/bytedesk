import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale = { "find-replace": {
  toolbar: "検索と置換",
  shortcut: {
    "open-find-dialog": "検索ダイアログを開く",
    "open-replace-dialog": "置換ダイアログを開く",
    "close-dialog": "検索と置換ダイアログを閉じる",
    "go-to-next-match": "次の一致項目へ移動",
    "go-to-previous-match": "前の一致項目へ移動",
    "focus-selection": "選択範囲にフォーカス",
    panel: "検索と置換"
  },
  dialog: {
    title: "検索",
    find: "検索",
    replace: "置換",
    "replace-all": "すべて置換",
    "case-sensitive": "大文字と小文字を区別",
    "find-placeholder": "このシート内を検索",
    "advanced-finding": "詳細検索と置換",
    "replace-placeholder": "置換文字列を入力",
    "match-the-whole-cell": "セル全体に一致",
    "find-direction": {
      title: "検索方向",
      row: "行ごとに検索",
      column: "列ごとに検索"
    },
    "find-scope": {
      title: "検索範囲",
      "current-sheet": "現在のシート",
      workbook: "ブック全体"
    },
    "find-by": {
      title: "検索条件",
      value: "値で検索",
      formula: "数式を検索"
    },
    "no-match": "検索が完了しましたが、一致する項目が見つかりませんでした。",
    "no-result": "結果なし"
  },
  replace: {
    "all-success": "一致項目 {0} 件すべてを置換しました",
    "all-failure": "置換に失敗しました",
    confirm: { title: "すべての一致項目を置換してもよろしいですか？" }
  },
  button: {
    confirm: "確認",
    cancel: "キャンセル"
  }
} };
const m = mergeLocales(
  locale
);
export {
  m as default
};
