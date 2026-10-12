import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale = { "find-replace": {
  toolbar: "Найти и заменить",
  shortcut: {
    "open-find-dialog": "Открыть диалоговое окно поиска",
    "open-replace-dialog": "Открыть диалоговое окно замены",
    "close-dialog": "Закрыть диалоговое окно поиска и замены",
    "go-to-next-match": "Перейти к следующему совпадению",
    "go-to-previous-match": "Перейти к предыдущему совпадению",
    "focus-selection": "Фокус на выделении",
    panel: "Найти и заменить"
  },
  dialog: {
    title: "Найти",
    find: "Найти",
    replace: "Заменить",
    "replace-all": "Заменить все",
    "case-sensitive": "Учитывать регистр",
    "find-placeholder": "Найти на этом листе",
    "advanced-finding": "Расширенный поиск и замена",
    "replace-placeholder": "Введите строку для замены",
    "match-the-whole-cell": "Соответствие всей ячейке",
    "find-direction": {
      title: "Направление поиска",
      row: "Искать по строкам",
      column: "Искать по столбцам"
    },
    "find-scope": {
      title: "Диапазон поиска",
      "current-sheet": "Текущий лист",
      workbook: "Книга"
    },
    "find-by": {
      title: "Искать по",
      value: "Искать по значению",
      formula: "Искать формулу"
    },
    "no-match": "Поиск завершен, но совпадений не найдено.",
    "no-result": "Нет результатов"
  },
  replace: {
    "all-success": "Заменены все совпадения: {0}",
    "all-failure": "Ошибка замены",
    confirm: { title: "Вы уверены, что хотите заменить все совпадения?" }
  },
  button: {
    confirm: "ОК",
    cancel: "Отмена"
  }
} };
const t = mergeLocales(
  locale
);
export {
  t as default
};
