import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale = { "find-replace": {
  toolbar: "Rechercher & Remplacer",
  shortcut: {
    "open-find-dialog": "Ouvrir la boîte de dialogue de recherche",
    "open-replace-dialog": "Ouvrir la boîte de dialogue de remplacement",
    "close-dialog": "Fermer la boîte de dialogue de recherche et de remplacement",
    "go-to-next-match": "Aller à la correspondance suivante",
    "go-to-previous-match": "Aller à la correspondance précédente",
    "focus-selection": "Concentrer la sélection",
    panel: "Rechercher & Remplacer"
  },
  dialog: {
    title: "Rechercher",
    find: "Rechercher",
    replace: "Remplacer",
    "replace-all": "Remplacer tout",
    "case-sensitive": "Sensible à la casse",
    "find-placeholder": "Rechercher dans cette feuille",
    "advanced-finding": "Recherche et remplacement avancés",
    "replace-placeholder": "Saisir la chaîne de remplacement",
    "match-the-whole-cell": "Correspondance de la cellule entière",
    "find-direction": {
      title: "Direction de la recherche",
      row: "Rechercher par ligne",
      column: "Rechercher par colonne"
    },
    "find-scope": {
      title: "Plage de recherche",
      "current-sheet": "Feuille actuelle",
      workbook: "Classeur"
    },
    "find-by": {
      title: "Rechercher par",
      value: "Rechercher par valeur",
      formula: "Rechercher une formule"
    },
    "no-match": "Recherche terminée mais aucune correspondance trouvée.",
    "no-result": "Aucun résultat"
  },
  replace: {
    "all-success": "Toutes les {0} correspondances ont été remplacées",
    "all-failure": "Échec du remplacement",
    confirm: { title: "Êtes-vous sûr de vouloir remplacer toutes les correspondances ?" }
  },
  button: {
    confirm: "OK",
    cancel: "Annuler"
  }
} };
const m = mergeLocales(
  locale
);
export {
  m as default
};
