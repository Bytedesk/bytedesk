import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "sheets-filter": {
  command: { "not-valid-filter-range": "選択範囲に1行しか含まれていないため、フィルタを適用できません。" },
  msg: { "filter-header-forbidden": "フィルタヘッダー行は移動できません。" }
} };
const locale = { "sheets-filter-ui": {
  toolbar: {
    "smart-toggle-filter-tooltip": "フィルタの切替",
    "clear-filter-criteria": "フィルタ条件をクリア",
    "re-calc-filter-conditions": "フィルタ条件を再計算"
  },
  shortcut: { "smart-toggle-filter": "フィルタの切替" },
  panel: {
    "clear-filter": "フィルタをクリア",
    cancel: "キャンセル",
    confirm: "確認",
    "by-values": "値でフィルタ",
    "by-colors": "色でフィルタ",
    "filter-by-cell-fill-color": "セルの塗りつぶし色でフィルタ",
    "filter-by-cell-text-color": "セルの文字色でフィルタ",
    "filter-by-color-none": "この列は単一の色のみを含んでいます",
    "by-conditions": "条件でフィルタ",
    "filter-only": "フィルタのみ表示",
    "search-placeholder": "検索語はスペースで区切る",
    "select-all": "すべて選択",
    "input-values-placeholder": "値を入力",
    and: "AND",
    or: "OR",
    empty: "(空白)",
    "?": "「?」は任意の1文字",
    "*": "「*」は任意の複数文字"
  },
  conditions: {
    none: "なし",
    empty: "空白セル",
    "not-empty": "空白ではない",
    "text-contains": "文字列を含む",
    "does-not-contain": "文字列を含まない",
    "starts-with": "前方一致",
    "ends-with": "後方一致",
    equals: "一致",
    "greater-than": "より大きい",
    "greater-than-or-equal": "以上",
    "less-than": "より小さい",
    "less-than-or-equal": "以下",
    equal: "等しい",
    "not-equal": "等しくない",
    between: "範囲内",
    "not-between": "範囲外",
    custom: "カスタム"
  },
  date: {
    1: "1月",
    2: "2月",
    3: "3月",
    4: "4月",
    5: "5月",
    6: "6月",
    7: "7月",
    8: "8月",
    9: "9月",
    10: "10月",
    11: "11月",
    12: "12月"
  },
  sync: {
    title: "フィルタを全員が表示可能",
    statusTips: {
      on: "有効にすると、すべての共同作業者がフィルタ結果を表示できます。",
      off: "無効にすると、自分のみフィルタ結果を表示できます。"
    },
    switchTips: {
      on: "「フィルタを全員が表示可能」が有効な場合、すべての共同作業者がフィルタ結果を表示できます。",
      off: "「フィルタを全員が表示可能」が無効な場合、自分のみフィルタ結果を表示できます。"
    }
  }
} };
const m = mergeLocales(
  locale$1,
  locale
);
export {
  m as default
};
