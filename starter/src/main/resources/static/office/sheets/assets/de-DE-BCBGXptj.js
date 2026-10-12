import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "sheets-filter": {
  command: { "not-valid-filter-range": "Der ausgewählte Bereich hat nur eine Zeile und ist für den Filter ungültig." },
  msg: { "filter-header-forbidden": "Die Kopfzeile eines Filters kann nicht verschoben werden." }
} };
const locale = { "sheets-filter-ui": {
  toolbar: {
    "smart-toggle-filter-tooltip": "Filter ein-/ausschalten",
    "clear-filter-criteria": "Filterbedingungen löschen",
    "re-calc-filter-conditions": "Filterbedingungen neu berechnen"
  },
  shortcut: { "smart-toggle-filter": "Filter ein-/ausschalten" },
  panel: {
    "clear-filter": "Filter löschen",
    cancel: "Abbrechen",
    confirm: "Bestätigen",
    "by-values": "Nach Werten",
    "by-colors": "Nach Farben",
    "filter-by-cell-fill-color": "Nach Zellenfüllfarbe filtern",
    "filter-by-cell-text-color": "Nach Zellentextfarbe filtern",
    "filter-by-color-none": "Die Spalte enthält nur eine Farbe",
    "by-conditions": "Nach Bedingungen",
    "filter-only": "Nur filtern",
    "search-placeholder": "Verwenden Sie Leerzeichen, um Schlüsselwörter zu trennen",
    "select-all": "Alle auswählen",
    "input-values-placeholder": "Werte eingeben",
    and: "UND",
    or: "ODER",
    empty: "(leer)",
    "?": 'Verwenden Sie "?" für ein einzelnes Zeichen.',
    "*": 'Verwenden Sie "*" für mehrere Zeichen.'
  },
  conditions: {
    none: "Keine",
    empty: "Ist leer",
    "not-empty": "Ist nicht leer",
    "text-contains": "Text enthält",
    "does-not-contain": "Text enthält nicht",
    "starts-with": "Text beginnt mit",
    "ends-with": "Text endet mit",
    equals: "Text ist gleich",
    "greater-than": "Größer als",
    "greater-than-or-equal": "Größer als oder gleich",
    "less-than": "Kleiner als",
    "less-than-or-equal": "Kleiner als oder gleich",
    equal: "Gleich",
    "not-equal": "Ungleich",
    between: "Zwischen",
    "not-between": "Nicht zwischen",
    custom: "Benutzerdefiniert"
  },
  date: {
    1: "Januar",
    2: "Februar",
    3: "März",
    4: "April",
    5: "Mai",
    6: "Juni",
    7: "Juli",
    8: "August",
    9: "September",
    10: "Oktober",
    11: "November",
    12: "Dezember"
  },
  sync: {
    title: "Filter ist für alle sichtbar",
    statusTips: {
      on: "Wenn eingeschaltet, sehen alle Bearbeiter die Filterergebnisse",
      off: "Wenn ausgeschaltet, sehen nur Sie die Filterergebnisse"
    },
    switchTips: {
      on: '"Filter ist für alle sichtbar" ist eingeschaltet, alle Bearbeiter sehen die Filterergebnisse',
      off: '"Filter ist für alle sichtbar" ist ausgeschaltet, nur Sie sehen die Filterergebnisse'
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
