import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "画像以外のオブジェクトはトリミングできません。" },
  "image-panel": {
    arrange: {
      title: "配置",
      forward: "前面へ移動",
      backward: "背面へ移動",
      front: "最前面へ移動",
      back: "最背面へ移動"
    },
    transform: {
      title: "変形",
      rotate: "回転 (°)",
      x: "X座標 (px)",
      y: "Y座標 (px)",
      width: "幅 (px)",
      height: "高さ (px)",
      lock: "縦横比を固定 (%)"
    },
    crop: {
      title: "トリミング",
      start: "トリミングを開始",
      mode: "自由モード"
    },
    group: {
      title: "グループ",
      group: "グループ化",
      unGroup: "グループ解除"
    },
    align: {
      title: "整列",
      default: "整列タイプを選択",
      left: "左揃え",
      center: "中央揃え",
      right: "右揃え",
      top: "上揃え",
      middle: "中央揃え",
      bottom: "下揃え",
      horizon: "水平間隔を均等に配分",
      vertical: "垂直間隔を均等に配分"
    },
    null: "選択されたオブジェクトがありません"
  },
  "image-text-wrap": {
    title: "文字列の折り返し",
    wrappingStyle: "折り返しのスタイル",
    square: "四角形",
    topAndBottom: "上下",
    inline: "行内",
    behindText: "文字列の背面",
    inFrontText: "文字列の前面",
    wrapText: "文字列の折り返し",
    bothSide: "両側",
    leftOnly: "左のみ",
    rightOnly: "右のみ",
    distanceFromText: "文字列からの距離",
    top: "上(px)",
    left: "左(px)",
    bottom: "下(px)",
    right: "右(px)"
  },
  "image-popup": {
    replace: "置換",
    delete: "削除",
    edit: "編集",
    crop: "トリミング",
    reset: "サイズをリセット"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "画像",
  upload: {
    float: "セル上に配置",
    cell: "セル内に配置"
  },
  panel: { title: "画像の編集" },
  save: {
    title: "セル画像を保存",
    menuLabel: "セル画像を保存",
    imageCount: "画像数",
    fileNameConfig: "ファイル名",
    useRowCol: "セルアドレスを使用 (A1, B2...)",
    useColumnValue: "列の値を使用",
    selectColumn: "列を選択",
    cancel: "キャンセル",
    confirm: "保存",
    saving: "保存中...",
    error: "セル画像の保存に失敗しました"
  },
  "image-popup": {
    replace: "画像の変更",
    delete: "削除",
    edit: "編集",
    crop: "トリミング",
    reset: "元のサイズに戻す",
    flipH: "左右反転",
    flipV: "上下反転"
  },
  "update-status": {
    exceedMaxSize: "画像サイズが制限（{0}MB）を超えています",
    invalidImageType: "サポートされていない画像形式です",
    exceedMaxCount: "一度にアップロードできるのは {0} 個までです",
    invalidImage: "無効な画像です"
  },
  "drawing-anchor": {
    title: "プロパティ",
    both: "セルに合わせて移動やサイズ変更をする",
    position: "セルに合わせて移動するがサイズ変更はしない",
    none: "セルに合わせて移動やサイズ変更をしない"
  },
  "cell-image": {
    pasteTitle: "セル内画像として貼り付け",
    pasteContent: "セル内画像を貼り付けると、既存のセル内容が上書きされます。続行しますか？",
    pasteError: "この箇所では、シートのセル内画像のコピー＆ペーストはサポートされていません"
  },
  permission: { dialog: { editErr: "この範囲は保護されており、編集権限がありません。編集するには作成者に連絡してください。" } },
  shortcut: {
    "drawing-view": "図形表示",
    "drawing-move-down": "図形を下へ移動",
    "drawing-move-up": "図形を上へ移動",
    "drawing-move-left": "図形を左へ移動",
    "drawing-move-right": "図形を右へ移動",
    "drawing-delete": "図形を削除"
  }
} };
const t = mergeLocales(
  locale$1,
  locale
);
export {
  t as default
};
