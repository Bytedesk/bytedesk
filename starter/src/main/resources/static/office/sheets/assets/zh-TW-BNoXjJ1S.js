import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale = { "sheets-sort-ui": {
  general: {
    sort: "排序",
    "sort-asc": "升序",
    "sort-desc": "降序",
    "sort-custom": "自訂排序",
    "sort-asc-ext": "拓展區域升序",
    "sort-desc-ext": "拓展區域降序",
    "sort-asc-cur": "目前區域升序",
    "sort-desc-cur": "當前區域降序"
  },
  error: {
    "merge-size": "所選區域的合併儲存格的大小不一致，無法排序。 ",
    empty: "所選區域無內容，無法排序。 ",
    single: "所選區域只有一列，無法排序。 ",
    "formula-array": "所選區域含陣列公式，無法排序。 "
  },
  dialog: {
    "sort-reminder": "排序提醒",
    "sort-reminder-desc": "目前只會對選取區域進行排序，是否拓展排序範圍？ ",
    "sort-reminder-ext": "拓展排序範圍",
    "sort-reminder-no": "保持所選排序範圍",
    "first-row-check": "標題不參與排序",
    "add-condition": "新增排序條件",
    cancel: "取消",
    confirm: "確認"
  },
  info: { tooltip: "提示" }
} };
const t = mergeLocales(
  locale
);
export {
  t as default
};
