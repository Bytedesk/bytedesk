import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "Tidak dapat memangkas objek non-gambar." },
  "image-panel": {
    arrange: {
      title: "Atur",
      forward: "Bawa ke Depan",
      backward: "Kirim ke Belakang",
      front: "Bawa ke Paling Depan",
      back: "Kirim ke Paling Belakang"
    },
    transform: {
      title: "Transformasi",
      rotate: "Putar (°)",
      x: "X (px)",
      y: "Y (px)",
      width: "Lebar (px)",
      height: "Tinggi (px)",
      lock: "Kunci Rasio (%)"
    },
    crop: {
      title: "Pangkas",
      start: "Mulai Pangkas",
      mode: "Bebas"
    },
    group: {
      title: "Grup",
      group: "Grupkan",
      unGroup: "Pisahkan Grup"
    },
    align: {
      title: "Ratakan",
      default: "Pilih Jenis Perataan",
      left: "Rata Kiri",
      center: "Rata Tengah",
      right: "Rata Kanan",
      top: "Rata Atas",
      middle: "Rata Tengah",
      bottom: "Rata Bawah",
      horizon: "Distribusikan Secara Horizontal",
      vertical: "Distribusikan Secara Vertikal"
    },
    null: "Tidak Ada Objek yang Dipilih"
  },
  "image-text-wrap": {
    title: "Pembungkus Teks",
    wrappingStyle: "Gaya Pembungkus",
    square: "Kotak",
    topAndBottom: "Atas dan Bawah",
    inline: "Sejajar dengan teks",
    behindText: "Di belakang teks",
    inFrontText: "Di depan teks",
    wrapText: "Bungkus teks",
    bothSide: "Kedua sisi",
    leftOnly: "Kiri saja",
    rightOnly: "Kanan saja",
    distanceFromText: "Jarak dari teks",
    top: "Atas(px)",
    left: "Kiri(px)",
    bottom: "Bawah(px)",
    right: "Kanan(px)"
  },
  "image-popup": {
    replace: "Ganti",
    delete: "Hapus",
    edit: "Edit",
    crop: "Pangkas",
    reset: "Atur Ulang Ukuran"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "Gambar",
  upload: {
    float: "Gambar Mengambang",
    cell: "Gambar Sel"
  },
  panel: { title: "Edit Gambar" },
  save: {
    title: "Simpan Gambar Sel",
    menuLabel: "Simpan Gambar Sel",
    imageCount: "Jumlah Gambar",
    fileNameConfig: "Nama File",
    useRowCol: "Gunakan Alamat Sel (A1, B2...)",
    useColumnValue: "Gunakan Nilai Kolom",
    selectColumn: "Pilih Kolom",
    cancel: "Batal",
    confirm: "Simpan",
    saving: "Menyimpan...",
    error: "Gagal menyimpan gambar sel"
  },
  "image-popup": {
    replace: "Ganti",
    delete: "Hapus",
    edit: "Edit",
    crop: "Pangkas",
    reset: "Atur Ulang Ukuran",
    flipH: "Balik Horizontal",
    flipV: "Balik Vertikal"
  },
  "update-status": {
    exceedMaxSize: "Ukuran gambar melebihi batas, batasnya adalah {0}M",
    invalidImageType: "Tipe gambar tidak valid",
    exceedMaxCount: "Hanya {0} gambar yang dapat diunggah dalam satu waktu",
    invalidImage: "Gambar tidak valid"
  },
  "drawing-anchor": {
    title: "Properti Jangkar",
    both: "Pindah dan ukur bersama sel",
    position: "Pindah tapi tidak ukur bersama sel",
    none: "Jangan pindah atau ukur bersama sel"
  },
  "cell-image": {
    pasteTitle: "Tempel sebagai gambar sel",
    pasteContent: "Menempel gambar sel akan menimpa konten yang ada di sel, lanjutkan menempel",
    pasteError: "Salin tempel gambar sel lembar kerja tidak didukung dalam unit ini"
  },
  permission: { dialog: { editErr: "Rentang ini dilindungi, dan Anda tidak memiliki izin edit. Untuk mengedit, silakan hubungi pembuatnya." } },
  shortcut: {
    "drawing-view": "Tampilan Gambar",
    "drawing-move-down": "Pindahkan Gambar ke Bawah",
    "drawing-move-up": "Pindahkan Gambar ke Atas",
    "drawing-move-left": "Pindahkan Gambar ke Kiri",
    "drawing-move-right": "Pindahkan Gambar ke Kanan",
    "drawing-delete": "Hapus Gambar"
  }
} };
const t = mergeLocales(
  locale$1,
  locale
);
export {
  t as default
};
