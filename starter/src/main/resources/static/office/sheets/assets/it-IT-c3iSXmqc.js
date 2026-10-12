import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "Impossibile ritagliare oggetti non immagine." },
  "image-panel": {
    arrange: {
      title: "Disponi",
      forward: "Porta avanti",
      backward: "Porta indietro",
      front: "Porta in primo piano",
      back: "Porta in secondo piano"
    },
    transform: {
      title: "Trasforma",
      rotate: "Ruota (°)",
      x: "X (px)",
      y: "Y (px)",
      width: "Larghezza (px)",
      height: "Altezza (px)",
      lock: "Blocca proporzioni (%)"
    },
    crop: {
      title: "Ritaglia",
      start: "Inizia ritaglio",
      mode: "Libero"
    },
    group: {
      title: "Raggruppa",
      group: "Raggruppa",
      unGroup: "Separa"
    },
    align: {
      title: "Allinea",
      default: "Seleziona tipo allineamento",
      left: "Allinea a sinistra",
      center: "Allinea al centro",
      right: "Allinea a destra",
      top: "Allinea in alto",
      middle: "Allinea al centro",
      bottom: "Allinea in basso",
      horizon: "Distribuisci orizzontalmente ",
      vertical: "Distribuisci verticalmente "
    },
    null: "Nessun oggetto selezionato"
  },
  "image-text-wrap": {
    title: "Testo a capo",
    wrappingStyle: "Stile a capo",
    square: "Quadrato",
    topAndBottom: "Superiore e inferiore",
    inline: "In linea con il testo",
    behindText: "Dietro il testo",
    inFrontText: "Davanti al testo",
    wrapText: "Testo a capo",
    bothSide: "Entrambi i lati",
    leftOnly: "Solo sinistra",
    rightOnly: "Solo destra",
    distanceFromText: "Distanza dal testo",
    top: "Superiore(px)",
    left: "Sinistra(px)",
    bottom: "Inferiore(px)",
    right: "Destra(px)"
  },
  "image-popup": {
    replace: "Sostituisci",
    delete: "Elimina",
    edit: "Modifica",
    crop: "Ritaglia",
    reset: "Reimposta dimensione"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "Immagine",
  upload: {
    float: "Immagine fluttuante",
    cell: "Immagine cella"
  },
  panel: { title: "Modifica immagine" },
  save: {
    title: "Salva immagini celle",
    menuLabel: "Salva immagini celle",
    imageCount: "Numero immagini",
    fileNameConfig: "Nome file",
    useRowCol: "Usa indirizzo cella (A1, B2...)",
    useColumnValue: "Usa valore colonna",
    selectColumn: "Seleziona colonna",
    cancel: "Annulla",
    confirm: "Salva",
    saving: "Salvataggio...",
    error: "Salvataggio immagini celle non riuscito"
  },
  "image-popup": {
    replace: "Sostituisci",
    delete: "Elimina",
    edit: "Modifica",
    crop: "Ritaglia",
    reset: "Reimposta dimensione",
    flipH: "Capovolgi orizzontalmente",
    flipV: "Capovolgi verticalmente"
  },
  "update-status": {
    exceedMaxSize: "La dimensione dell'immagine supera il limite, il limite è {0}M",
    invalidImageType: "Tipo di immagine non valido",
    exceedMaxCount: "È possibile caricare solo {0} immagini alla volta",
    invalidImage: "Immagine non valida"
  },
  "drawing-anchor": {
    title: "Proprietà ancoraggio",
    both: "Sposta e ridimensiona con le celle",
    position: "Sposta ma non ridimensionare con le celle",
    none: "Non spostare né ridimensionare con le celle"
  },
  "cell-image": {
    pasteTitle: "Incolla come immagine cella",
    pasteContent: "L'incollaggio di un'immagine cella sovrascriverà il contenuto esistente della cella, continuare con l'incollaggio",
    pasteError: "Copia e incolla di immagine cella non supportato in questa unità"
  },
  permission: { dialog: { editErr: "L'intervallo è protetto e non hai l'autorizzazione di modifica. Per modificare, contatta il creatore." } },
  shortcut: {
    "drawing-view": "Vista disegno",
    "drawing-move-down": "Sposta disegno in basso",
    "drawing-move-up": "Sposta disegno in alto",
    "drawing-move-left": "Sposta disegno a sinistra",
    "drawing-move-right": "Sposta disegno a destra",
    "drawing-delete": "Elimina disegno"
  }
} };
const s = mergeLocales(
  locale$1,
  locale
);
export {
  s as default
};
