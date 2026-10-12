import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "Невозможно обрезать не изображения." },
  "image-panel": {
    arrange: {
      title: "Расположение",
      forward: "Переместить вперёд",
      backward: "Переместить назад",
      front: "На передний план",
      back: "На задний план"
    },
    transform: {
      title: "Трансформация",
      rotate: "Повернуть (°)",
      x: "X (пкс)",
      y: "Y (пкс)",
      width: "Ширина (пкс)",
      height: "Высота (пкс)",
      lock: "Заблокировать пропорции (%)"
    },
    crop: {
      title: "Обрезка",
      start: "Начать обрезку",
      mode: "Свободный"
    },
    group: {
      title: "Группировка",
      group: "Сгруппировать",
      unGroup: "Разгруппировать"
    },
    align: {
      title: "Выравнивание",
      default: "Выберите тип выравнивания",
      left: "Выровнять по левому краю",
      center: "Выровнять по центру",
      right: "Выровнять по правому краю",
      top: "Выровнять по верхнему краю",
      middle: "Выровнять посередине",
      bottom: "Выровнять по нижнему краю",
      horizon: "Распределить горизонтально",
      vertical: "Распределить вертикально"
    },
    null: "Нет выбранных объектов"
  },
  "image-text-wrap": {
    title: "Text Wrapping",
    wrappingStyle: "Wrapping Style",
    square: "Square",
    topAndBottom: "Top and Bottom",
    inline: "In line with text",
    behindText: "Behind text",
    inFrontText: "In front of text",
    wrapText: "Wrap text",
    bothSide: "Both sides",
    leftOnly: "Left only",
    rightOnly: "Right only",
    distanceFromText: "Distance from text",
    top: "Top(px)",
    left: "Left(px)",
    bottom: "Bottom(px)",
    right: "Right(px)"
  },
  "image-popup": {
    replace: "Заменить",
    delete: "Удалить",
    edit: "Редактировать",
    crop: "Обрезать",
    reset: "Сбросить размер"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "Изображение",
  upload: {
    float: "Плавающее изображение",
    cell: "Изображение в ячейке"
  },
  panel: { title: "Редактировать изображение" },
  save: {
    title: "Сохранить изображения ячеек",
    menuLabel: "Сохранить изображения ячеек",
    imageCount: "Количество изображений",
    fileNameConfig: "Имя файла",
    useRowCol: "Использовать адрес ячейки (A1, B2...)",
    useColumnValue: "Использовать значение столбца",
    selectColumn: "Выбрать столбец",
    cancel: "Отмена",
    confirm: "Сохранить",
    saving: "Сохранение...",
    error: "Не удалось сохранить изображения ячеек"
  },
  "image-popup": {
    replace: "Заменить",
    delete: "Удалить",
    edit: "Редактировать",
    crop: "Обрезать",
    reset: "Сбросить размер",
    flipH: "Отразить по горизонтали",
    flipV: "Отразить по вертикали"
  },
  "update-status": {
    exceedMaxSize: "Размер изображения превышает лимит, лимит составляет {0}М",
    invalidImageType: "Недопустимый тип изображения",
    exceedMaxCount: "За один раз можно загрузить только {0} изображений",
    invalidImage: "Недопустимое изображение"
  },
  "drawing-anchor": {
    title: "Свойства привязки",
    both: "Перемещать и изменять размер с ячейками",
    position: "Перемещать, но не изменять размер с ячейками",
    none: "Не перемещать и не изменять размер с ячейками"
  },
  "cell-image": {
    pasteTitle: "Вставить как изображение ячейки",
    pasteContent: "Вставка изображения ячейки перезапишет существующее содержимое ячейки, продолжить вставку",
    pasteError: "Копирование и вставка изображения ячейки не поддерживается в этом блоке"
  },
  permission: { dialog: { editErr: "Диапазон защищен, и у вас нет разрешения на редактирование. Для редактирования свяжитесь с создателем." } },
  shortcut: {
    "drawing-view": "Вид изображения",
    "drawing-move-down": "Переместить изображение вниз",
    "drawing-move-up": "Переместить изображение вверх",
    "drawing-move-left": "Переместить изображение влево",
    "drawing-move-right": "Переместить изображение вправо",
    "drawing-delete": "Удалить изображение"
  }
} };
const t = mergeLocales(
  locale$1,
  locale
);
export {
  t as default
};
