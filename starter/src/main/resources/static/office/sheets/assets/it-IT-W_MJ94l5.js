import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "sheets-filter": {
  command: { "not-valid-filter-range": "L'intervallo selezionato ha una sola riga e non è valido per il filtro." },
  msg: { "filter-header-forbidden": "Non è possibile spostare la riga di intestazione di un filtro." }
} };
const locale = { "sheets-filter-ui": {
  toolbar: {
    "smart-toggle-filter-tooltip": "Attiva/disattiva filtro",
    "clear-filter-criteria": "Cancella condizioni filtro",
    "re-calc-filter-conditions": "Ricalcola condizioni filtro"
  },
  shortcut: { "smart-toggle-filter": "Attiva/disattiva filtro" },
  panel: {
    "clear-filter": "Cancella filtro",
    cancel: "Annulla",
    confirm: "Conferma",
    "by-values": "Per valori",
    "by-colors": "Per colori",
    "filter-by-cell-fill-color": "Filtra per colore di riempimento cella",
    "filter-by-cell-text-color": "Filtra per colore testo cella",
    "filter-by-color-none": "La colonna contiene un solo colore",
    "by-conditions": "Per condizioni",
    "filter-only": "Solo filtro",
    "search-placeholder": "Usa lo spazio per separare le parole chiave",
    "select-all": "Seleziona tutto",
    "input-values-placeholder": "Inserisci valori",
    and: "E",
    or: "O",
    empty: "(vuoto)",
    "?": 'Usa "?" per rappresentare un singolo carattere.',
    "*": 'Usa "*" per rappresentare più caratteri.'
  },
  conditions: {
    none: "Nessuna",
    empty: "È vuoto",
    "not-empty": "Non è vuoto",
    "text-contains": "Il testo contiene",
    "does-not-contain": "Il testo non contiene",
    "starts-with": "Il testo inizia con",
    "ends-with": "Il testo finisce con",
    equals: "Il testo è uguale a",
    "greater-than": "Maggiore di",
    "greater-than-or-equal": "Maggiore o uguale a",
    "less-than": "Minore di",
    "less-than-or-equal": "Minore o uguale a",
    equal: "Uguale",
    "not-equal": "Diverso",
    between: "Tra",
    "not-between": "Non tra",
    custom: "Personalizzato"
  },
  date: {
    1: "Gennaio",
    2: "Febbraio",
    3: "Marzo",
    4: "Aprile",
    5: "Maggio",
    6: "Giugno",
    7: "Luglio",
    8: "Agosto",
    9: "Settembre",
    10: "Ottobre",
    11: "Novembre",
    12: "Dicembre"
  },
  sync: {
    title: "Il filtro è visibile a tutti",
    statusTips: {
      on: "Se attivato, tutti i collaboratori vedranno i risultati del filtro",
      off: "Se disattivato, solo tu vedrai i risultati del filtro"
    },
    switchTips: {
      on: '"Il filtro è visibile a tutti" è attivato, tutti i collaboratori vedranno i risultati del filtro',
      off: '"Il filtro è visibile a tutti" è disattivato, solo tu vedrai i risultati del filtro'
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
