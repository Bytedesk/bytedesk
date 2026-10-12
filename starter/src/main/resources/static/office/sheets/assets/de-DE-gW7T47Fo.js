import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale = { "find-replace": {
  toolbar: "Suchen & Ersetzen",
  shortcut: {
    "open-find-dialog": "Suchdialog öffnen",
    "open-replace-dialog": "Ersetzungsdialog öffnen",
    "close-dialog": "Suchen & Ersetzen-Dialog schließen",
    "go-to-next-match": "Zum nächsten Treffer springen",
    "go-to-previous-match": "Zum vorherigen Treffer springen",
    "focus-selection": "Auswahl fokussieren",
    panel: "Suchen & Ersetzen"
  },
  dialog: {
    title: "Suchen",
    find: "Suchen",
    replace: "Ersetzen",
    "replace-all": "Alle ersetzen",
    "case-sensitive": "Groß-/Kleinschreibung beachten",
    "find-placeholder": "In diesem Blatt suchen",
    "advanced-finding": "Erweitertes Suchen & Ersetzen",
    "replace-placeholder": "Ersetzungstext eingeben",
    "match-the-whole-cell": "Ganze Zelle vergleichen",
    "find-direction": {
      title: "Suchrichtung",
      row: "Nach Zeile suchen",
      column: "Nach Spalte suchen"
    },
    "find-scope": {
      title: "Suchbereich",
      "current-sheet": "Aktuelles Blatt",
      workbook: "Arbeitsmappe"
    },
    "find-by": {
      title: "Suchen nach",
      value: "Nach Wert suchen",
      formula: "Formel suchen"
    },
    "no-match": "Suche abgeschlossen, aber kein Treffer gefunden.",
    "no-result": "Kein Ergebnis"
  },
  replace: {
    "all-success": "Alle {0} Treffer ersetzt",
    "all-failure": "Ersetzen fehlgeschlagen",
    confirm: { title: "Sollen alle Treffer ersetzt werden?" }
  },
  button: {
    confirm: "OK",
    cancel: "Abbrechen"
  }
} };
const t = mergeLocales(
  locale
);
export {
  t as default
};
