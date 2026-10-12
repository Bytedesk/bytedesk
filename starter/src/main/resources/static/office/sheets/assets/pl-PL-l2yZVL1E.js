import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "sheets-filter": {
  command: { "not-valid-filter-range": "Wybrany zakres ma tylko jeden wiersz i nie jest prawidłowy do filtrowania." },
  msg: { "filter-header-forbidden": "Nie można przenieść wiersza nagłówka filtra." }
} };
const locale = { "sheets-filter-ui": {
  toolbar: {
    "smart-toggle-filter-tooltip": "Przełącz filtr",
    "clear-filter-criteria": "Wyczyść warunki filtrowania",
    "re-calc-filter-conditions": "Przelicz warunki filtrowania"
  },
  shortcut: { "smart-toggle-filter": "Przełącz filtr" },
  panel: {
    "clear-filter": "Wyczyść filtr",
    cancel: "Anuluj",
    confirm: "Potwierdź",
    "by-values": "Według wartości",
    "by-colors": "Według kolorów",
    "filter-by-cell-fill-color": "Filtruj według koloru wypełnienia komórki",
    "filter-by-cell-text-color": "Filtruj według koloru tekstu komórki",
    "filter-by-color-none": "Kolumna zawiera tylko jeden kolor",
    "by-conditions": "Według warunków",
    "filter-only": "Tylko filtr",
    "search-placeholder": "Użyj spacji do oddzielenia słów kluczowych",
    "select-all": "Zaznacz wszystko",
    "input-values-placeholder": "Wprowadź wartości",
    and: "ORAZ",
    or: "LUB",
    empty: "(puste)",
    "?": "Użyj „?”, aby reprezentować pojedynczy znak.",
    "*": "Użyj „*”, aby reprezentować dowolną liczbę znaków."
  },
  conditions: {
    none: "Brak",
    empty: "Jest puste",
    "not-empty": "Nie jest puste",
    "text-contains": "Tekst zawiera",
    "does-not-contain": "Tekst nie zawiera",
    "starts-with": "Tekst zaczyna się od",
    "ends-with": "Tekst kończy się na",
    equals: "Tekst równa się",
    "greater-than": "Większe niż",
    "greater-than-or-equal": "Większe lub równe",
    "less-than": "Mniejsze niż",
    "less-than-or-equal": "Mniejsze lub równe",
    equal: "Równe",
    "not-equal": "Różne od",
    between: "Między",
    "not-between": "Nie między",
    custom: "Niestandardowe"
  },
  date: {
    1: "Styczeń",
    2: "Luty",
    3: "Marzec",
    4: "Kwiecień",
    5: "Maj",
    6: "Czerwiec",
    7: "Lipiec",
    8: "Sierpień",
    9: "Wrzesień",
    10: "Październik",
    11: "Listopad",
    12: "Grudzień"
  },
  sync: {
    title: "Filtr jest widoczny dla wszystkich",
    statusTips: {
      on: "Po włączeniu wszyscy współpracownicy zobaczą wyniki filtrowania",
      off: "Po wyłączeniu tylko Ty zobaczysz wyniki filtrowania"
    },
    switchTips: {
      on: "„Filtr jest widoczny dla wszystkich” jest włączony, wszyscy współpracownicy zobaczą wyniki filtrowania",
      off: "„Filtr jest widoczny dla wszystkich” jest wyłączony, tylko Ty zobaczysz wyniki filtrowania"
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
