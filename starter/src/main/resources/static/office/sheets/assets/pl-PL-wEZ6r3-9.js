import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "Nie można przyciąć obiektów innych niż obraz." },
  "image-panel": {
    arrange: {
      title: "Rozmieść",
      forward: "Przesuń do przodu",
      backward: "Przesuń do tyłu",
      front: "Przesuń na wierzch",
      back: "Przesuń na spód"
    },
    transform: {
      title: "Przekształć",
      rotate: "Obrót (°)",
      x: "X (px)",
      y: "Y (px)",
      width: "Szerokość (px)",
      height: "Wysokość (px)",
      lock: "Zablokuj proporcje (%)"
    },
    crop: {
      title: "Przytnij",
      start: "Rozpocznij przycinanie",
      mode: "Dowolny"
    },
    group: {
      title: "Grupa",
      group: "Grupuj",
      unGroup: "Rozgrupuj"
    },
    align: {
      title: "Wyrównaj",
      default: "Wybierz typ wyrównania",
      left: "Wyrównaj do lewej",
      center: "Wyrównaj do środka",
      right: "Wyrównaj do prawej",
      top: "Wyrównaj do góry",
      middle: "Wyrównaj do środka w pionie",
      bottom: "Wyrównaj do dołu",
      horizon: "Rozłóż poziomo ",
      vertical: "Rozłóż pionowo "
    },
    null: "Brak zaznaczonego obiektu"
  },
  "image-text-wrap": {
    title: "Zawijanie tekstu",
    wrappingStyle: "Styl zawijania",
    square: "Kwadrat",
    topAndBottom: "Góra i dół",
    inline: "W linii z tekstem",
    behindText: "Za tekstem",
    inFrontText: "Przed tekstem",
    wrapText: "Zawijaj tekst",
    bothSide: "Obie strony",
    leftOnly: "Tylko lewa",
    rightOnly: "Tylko prawa",
    distanceFromText: "Odległość od tekstu",
    top: "Góra(px)",
    left: "Lewo(px)",
    bottom: "Dół(px)",
    right: "Prawo(px)"
  },
  "image-popup": {
    replace: "Zamień",
    delete: "Usuń",
    edit: "Edytuj",
    crop: "Przytnij",
    reset: "Resetuj rozmiar"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "Obraz",
  upload: {
    float: "Obraz swobodny",
    cell: "Obraz w komórce"
  },
  panel: { title: "Edytuj obraz" },
  save: {
    title: "Zapisz obrazy w komórkach",
    menuLabel: "Zapisz obrazy w komórkach",
    imageCount: "Liczba obrazów",
    fileNameConfig: "Nazwa pliku",
    useRowCol: "Użyj adresu komórki (A1, B2...)",
    useColumnValue: "Użyj wartości kolumny",
    selectColumn: "Wybierz kolumnę",
    cancel: "Anuluj",
    confirm: "Zapisz",
    saving: "Zapisywanie...",
    error: "Nie udało się zapisać obrazów w komórkach"
  },
  "image-popup": {
    replace: "Zamień",
    delete: "Usuń",
    edit: "Edytuj",
    crop: "Przytnij",
    reset: "Resetuj rozmiar",
    flipH: "Przerzuć w poziomie",
    flipV: "Przerzuć w pionie"
  },
  "update-status": {
    exceedMaxSize: "Rozmiar obrazu przekracza limit, limit wynosi {0}M",
    invalidImageType: "Nieprawidłowy typ obrazu",
    exceedMaxCount: "Jednocześnie można przesłać tylko {0} obrazów",
    invalidImage: "Nieprawidłowy obraz"
  },
  "drawing-anchor": {
    title: "Właściwości kotwicy",
    both: "Przesuwaj i zmieniaj rozmiar razem z komórkami",
    position: "Przesuwaj, ale nie zmieniaj rozmiaru razem z komórkami",
    none: "Nie przesuwaj ani nie zmieniaj rozmiaru razem z komórkami"
  },
  "cell-image": {
    pasteTitle: "Wklej jako obraz w komórce",
    pasteContent: "Wklejenie obrazu w komórce spowoduje zastąpienie istniejącej zawartości komórki, kontynuować wklejanie",
    pasteError: "Kopiowanie i wklejanie obrazu w komórce arkusza nie jest obsługiwane w tej jednostce"
  },
  permission: { dialog: { editErr: "Zakres jest chroniony i nie masz uprawnień do edycji. Aby edytować, skontaktuj się z twórcą." } },
  shortcut: {
    "drawing-view": "Widok rysunku",
    "drawing-move-down": "Przesuń rysunek w dół",
    "drawing-move-up": "Przesuń rysunek w górę",
    "drawing-move-left": "Przesuń rysunek w lewo",
    "drawing-move-right": "Przesuń rysunek w prawo",
    "drawing-delete": "Usuń rysunek"
  }
} };
const t = mergeLocales(
  locale$1,
  locale
);
export {
  t as default
};
