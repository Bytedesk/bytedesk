import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "Impossible de rogner des objets non image." },
  "image-panel": {
    arrange: {
      title: "Arranger",
      forward: "Avancer",
      backward: "Reculer",
      front: "Mettre au premier plan",
      back: "Mettre à l'arrière-plan"
    },
    transform: {
      title: "Transformer",
      rotate: "Pivoter (°)",
      x: "X (px)",
      y: "Y (px)",
      width: "Largeur (px)",
      height: "Hauteur (px)",
      lock: "Verrouiller le ratio (%)"
    },
    crop: {
      title: "Rogner",
      start: "Commencer à rogner",
      mode: "Libre"
    },
    group: {
      title: "Grouper",
      group: "Grouper",
      unGroup: "Dégrouper"
    },
    align: {
      title: "Aligner",
      default: "Sélectionner le type d'alignement",
      left: "Aligner à gauche",
      center: "Aligner au centre",
      right: "Aligner à droite",
      top: "Aligner en haut",
      middle: "Aligner au milieu",
      bottom: "Aligner en bas",
      horizon: "Distribuer horizontalement",
      vertical: "Distribuer verticalement"
    },
    null: "Aucune sélection d'objet"
  },
  "image-text-wrap": {
    title: "Habillage du texte",
    wrappingStyle: "Style d'habillage",
    square: "Carré",
    topAndBottom: "Haut et bas",
    inline: "Aligné avec le texte",
    behindText: "Derrière le texte",
    inFrontText: "Devant le texte",
    wrapText: "Habiller le texte",
    bothSide: "Des deux côtés",
    leftOnly: "Seulement à gauche",
    rightOnly: "Seulement à droite",
    distanceFromText: "Distance du texte",
    top: "Haut(px)",
    left: "Gauche(px)",
    bottom: "Bas(px)",
    right: "Droite(px)"
  },
  "image-popup": {
    replace: "Remplacer",
    delete: "Supprimer",
    edit: "Modifier",
    crop: "Rogner",
    reset: "Réinitialiser la taille"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "Image",
  upload: {
    float: "Image flottante",
    cell: "Image de cellule"
  },
  panel: { title: "Modifier l'image" },
  save: {
    title: "Enregistrer les images de cellule",
    menuLabel: "Enregistrer les images de cellule",
    imageCount: "Nombre d'images",
    fileNameConfig: "Nom du fichier",
    useRowCol: "Utiliser l'adresse de cellule (A1, B2...)",
    useColumnValue: "Utiliser la valeur de la colonne",
    selectColumn: "Sélectionner la colonne",
    cancel: "Annuler",
    confirm: "Enregistrer",
    saving: "Enregistrement...",
    error: "Échec de l'enregistrement des images de cellule"
  },
  "image-popup": {
    replace: "Remplacer",
    delete: "Supprimer",
    edit: "Éditer",
    crop: "Rogner",
    reset: "Réinitialiser la taille",
    flipH: "Retournement horizontal",
    flipV: "Retournement vertical"
  },
  "update-status": {
    exceedMaxSize: "La taille de l'image dépasse la limite, la limite est de {0}M",
    invalidImageType: "Type d'image invalide",
    exceedMaxCount: "Seulement {0} images peuvent être téléchargées à la fois",
    invalidImage: "Image invalide"
  },
  "drawing-anchor": {
    title: "Propriétés de l'ancre",
    both: "Déplacer et redimensionner avec les cellules",
    position: "Déplacer mais ne pas redimensionner avec les cellules",
    none: "Ne pas déplacer ni redimensionner avec les cellules"
  },
  "cell-image": {
    pasteTitle: "Coller comme image de cellule",
    pasteContent: "Coller une image de cellule écrasera le contenu existant de la cellule, continuer le collage",
    pasteError: "Le copier-coller d'image de cellule n'est pas pris en charge dans cette unité"
  },
  permission: { dialog: { editErr: "La plage est protégée, et vous n'avez pas la permission de modifier. Pour modifier, veuillez contacter le créateur." } },
  shortcut: {
    "drawing-view": "Vue du dessin",
    "drawing-move-down": "Déplacer le dessin vers le bas",
    "drawing-move-up": "Déplacer le dessin vers le haut",
    "drawing-move-left": "Déplacer le dessin vers la gauche",
    "drawing-move-right": "Déplacer le dessin vers la droite",
    "drawing-delete": "Supprimer le dessin"
  }
} };
const t = mergeLocales(
  locale$1,
  locale
);
export {
  t as default
};
