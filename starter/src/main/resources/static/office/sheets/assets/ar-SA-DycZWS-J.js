import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "sheets-filter": {
  command: { "not-valid-filter-range": "يحتوي النطاق المحدد على صف واحد فقط وهو غير صالح للتصفية." },
  msg: { "filter-header-forbidden": "لا يمكنك نقل صف الرأس الخاص بالتصفية." }
} };
const locale = { "sheets-filter-ui": {
  toolbar: {
    "smart-toggle-filter-tooltip": "تبديل التصفية",
    "clear-filter-criteria": "مسح شروط التصفية",
    "re-calc-filter-conditions": "إعادة حساب شروط التصفية"
  },
  shortcut: { "smart-toggle-filter": "تبديل التصفية" },
  panel: {
    "clear-filter": "مسح التصفية",
    cancel: "إلغاء",
    confirm: "تأكيد",
    "by-values": "حسب القيم",
    "by-colors": "حسب الألوان",
    "filter-by-cell-fill-color": "تصفية حسب لون تعبئة الخلية",
    "filter-by-cell-text-color": "تصفية حسب لون نص الخلية",
    "filter-by-color-none": "يحتوي العمود على لون واحد فقط",
    "by-conditions": "حسب الشروط",
    "filter-only": "تصفية فقط",
    "search-placeholder": "استخدم المسافة لفصل الكلمات المفتاحية",
    "select-all": "تحديد الكل",
    "input-values-placeholder": "إدخال القيم",
    and: "و",
    or: "أو",
    empty: "(فارغ)",
    "?": 'استخدم "؟" لتمثيل حرف واحد.',
    "*": 'استخدم "*" لتمثيل عدة أحرف.'
  },
  conditions: {
    none: "لا شيء",
    empty: "فارغ",
    "not-empty": "غير فارغ",
    "text-contains": "النص يحتوي على",
    "does-not-contain": "النص لا يحتوي على",
    "starts-with": "النص يبدأ بـ",
    "ends-with": "النص ينتهي بـ",
    equals: "النص يساوي",
    "greater-than": "أكبر من",
    "greater-than-or-equal": "أكبر من أو يساوي",
    "less-than": "أقل من",
    "less-than-or-equal": "أقل من أو يساوي",
    equal: "يساوي",
    "not-equal": "لا يساوي",
    between: "بين",
    "not-between": "ليس بين",
    custom: "مخصص"
  },
  date: {
    1: "يناير",
    2: "فبراير",
    3: "مارس",
    4: "أبريل",
    5: "مايو",
    6: "يونيو",
    7: "يوليو",
    8: "أغسطس",
    9: "سبتمبر",
    10: "أكتوبر",
    11: "نوفمبر",
    12: "ديسمبر"
  },
  sync: {
    title: "التصفية مرئية للجميع",
    statusTips: {
      on: "عند التفعيل، سيرى جميع المتعاونين نتائج التصفية",
      off: "عند الإيقاف، سترى نتائج التصفية أنت فقط"
    },
    switchTips: {
      on: '"التصفية مرئية للجميع" مفعّلة، سيرى جميع المتعاونين نتائج التصفية',
      off: '"التصفية مرئية للجميع" غير مفعّلة، سترى نتائج التصفية أنت فقط'
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
