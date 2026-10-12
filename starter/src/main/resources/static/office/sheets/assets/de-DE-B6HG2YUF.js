import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "Nicht-Bildobjekte können nicht zugeschnitten werden." },
  "image-panel": {
    arrange: {
      title: "Anordnen",
      forward: "Eine Ebene nach vorne",
      backward: "Eine Ebene nach hinten",
      front: "In den Vordergrund",
      back: "In den Hintergrund"
    },
    transform: {
      title: "Transformieren",
      rotate: "Drehen (°)",
      x: "X (px)",
      y: "Y (px)",
      width: "Breite (px)",
      height: "Höhe (px)",
      lock: "Verhältnis sperren (%)"
    },
    crop: {
      title: "Zuschneiden",
      start: "Zuschneiden starten",
      mode: "Frei"
    },
    group: {
      title: "Gruppe",
      group: "Gruppieren",
      unGroup: "Gruppierung aufheben"
    },
    align: {
      title: "Ausrichten",
      default: "Ausrichtungstyp auswählen",
      left: "Linksbündig",
      center: "Zentriert",
      right: "Rechtsbündig",
      top: "Oben bündig",
      middle: "Mittig",
      bottom: "Unten bündig",
      horizon: "Horizontal verteilen",
      vertical: "Vertikal verteilen"
    },
    null: "Keine Objektauswahl"
  },
  "image-text-wrap": {
    title: "Textumbruch",
    wrappingStyle: "Umbruchstil",
    square: "Quadratisch",
    topAndBottom: "Oben und unten",
    inline: "Im Textfluss",
    behindText: "Hinter dem Text",
    inFrontText: "Vor dem Text",
    wrapText: "Text umbrechen",
    bothSide: "Beide Seiten",
    leftOnly: "Nur links",
    rightOnly: "Nur rechts",
    distanceFromText: "Abstand vom Text",
    top: "Oben(px)",
    left: "Links(px)",
    bottom: "Unten(px)",
    right: "Rechts(px)"
  },
  "image-popup": {
    replace: "Ersetzen",
    delete: "Löschen",
    edit: "Bearbeiten",
    crop: "Zuschneiden",
    reset: "Größe zurücksetzen"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "Bild",
  upload: {
    float: "Schwebendes Bild",
    cell: "Zellenbild"
  },
  panel: { title: "Bild bearbeiten" },
  save: {
    title: "Zellenbilder speichern",
    menuLabel: "Zellenbilder speichern",
    imageCount: "Bildanzahl",
    fileNameConfig: "Dateiname",
    useRowCol: "Zellenadresse verwenden (A1, B2...)",
    useColumnValue: "Spaltenwert verwenden",
    selectColumn: "Spalte auswählen",
    cancel: "Abbrechen",
    confirm: "Speichern",
    saving: "Speichern...",
    error: "Zellenbilder konnten nicht gespeichert werden"
  },
  "image-popup": {
    replace: "Ersetzen",
    delete: "Löschen",
    edit: "Bearbeiten",
    crop: "Zuschneiden",
    reset: "Größe zurücksetzen",
    flipH: "Horizontal spiegeln",
    flipV: "Vertikal spiegeln"
  },
  "update-status": {
    exceedMaxSize: "Bildgröße überschreitet das Limit, Limit ist {0}M",
    invalidImageType: "Ungültiger Bildtyp",
    exceedMaxCount: "Es können nur {0} Bilder gleichzeitig hochgeladen werden",
    invalidImage: "Ungültiges Bild"
  },
  "drawing-anchor": {
    title: "Anker-Eigenschaften",
    both: "Mit Zellen verschieben und skalieren",
    position: "Mit Zellen verschieben, aber nicht skalieren",
    none: "Weder verschieben noch skalieren mit Zellen"
  },
  "cell-image": {
    pasteTitle: "Als Zellenbild einfügen",
    pasteContent: "Das Einfügen eines Zellenbilds überschreibt den bestehenden Inhalt der Zelle, mit dem Einfügen fortfahren?",
    pasteError: "Kopieren und Einfügen von Zellenbildern wird in dieser Einheit nicht unterstützt"
  },
  permission: { dialog: { editErr: "Der Bereich ist geschützt, und Sie haben keine Bearbeitungsberechtigung. Um zu bearbeiten, wenden Sie sich bitte an den Ersteller." } },
  shortcut: {
    "drawing-view": "Zeichnungsansicht",
    "drawing-move-down": "Zeichnung nach unten verschieben",
    "drawing-move-up": "Zeichnung nach oben verschieben",
    "drawing-move-left": "Zeichnung nach links verschieben",
    "drawing-move-right": "Zeichnung nach rechts verschieben",
    "drawing-delete": "Zeichnung löschen"
  }
} };
const t = mergeLocales(
  locale$1,
  locale
);
export {
  t as default
};
