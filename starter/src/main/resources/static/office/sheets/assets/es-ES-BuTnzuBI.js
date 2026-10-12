import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "No se pueden recortar objetos que no sean imágenes." },
  "image-panel": {
    arrange: {
      title: "Organizar",
      forward: "Traer adelante",
      backward: "Enviar atrás",
      front: "Traer al frente",
      back: "Enviar al fondo"
    },
    transform: {
      title: "Transformar",
      rotate: "Rotar (°)",
      x: "X (px)",
      y: "Y (px)",
      width: "Ancho (px)",
      height: "Alto (px)",
      lock: "Bloquear proporción (%)"
    },
    crop: {
      title: "Recortar",
      start: "Iniciar recorte",
      mode: "Libre"
    },
    group: {
      title: "Agrupar",
      group: "Agrupar",
      unGroup: "Desagrupar"
    },
    align: {
      title: "Alinear",
      default: "Seleccionar tipo de alineación",
      left: "Alinear a la izquierda",
      center: "Alinear al centro",
      right: "Alinear a la derecha",
      top: "Alinear arriba",
      middle: "Alinear al medio",
      bottom: "Alinear abajo",
      horizon: "Distribuir horizontalmente",
      vertical: "Distribuir verticalmente"
    },
    null: "Ningún objeto seleccionado"
  },
  "image-text-wrap": {
    title: "Ajuste de texto",
    wrappingStyle: "Estilo de ajuste",
    square: "Cuadrado",
    topAndBottom: "Arriba y abajo",
    inline: "En línea con el texto",
    behindText: "Detrás del texto",
    inFrontText: "Delante del texto",
    wrapText: "Ajustar texto",
    bothSide: "Ambos lados",
    leftOnly: "Solo izquierda",
    rightOnly: "Solo derecha",
    distanceFromText: "Distancia del texto",
    top: "Arriba(px)",
    left: "Izquierda(px)",
    bottom: "Abajo(px)",
    right: "Derecha(px)"
  },
  "image-popup": {
    replace: "Reemplazar",
    delete: "Eliminar",
    edit: "Editar",
    crop: "Recortar",
    reset: "Restablecer tamaño"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "Imagen",
  upload: {
    float: "Imagen flotante",
    cell: "Imagen de celda"
  },
  panel: { title: "Editar imagen" },
  save: {
    title: "Guardar imágenes de celda",
    menuLabel: "Guardar imágenes de celda",
    imageCount: "Cantidad de imágenes",
    fileNameConfig: "Nombre del archivo",
    useRowCol: "Usar dirección de celda (A1, B2...)",
    useColumnValue: "Usar valor de columna",
    selectColumn: "Seleccionar columna",
    cancel: "Cancelar",
    confirm: "Guardar",
    saving: "Guardando...",
    error: "Error al guardar las imágenes de celda"
  },
  "image-popup": {
    replace: "Reemplazar",
    delete: "Eliminar",
    edit: "Editar",
    crop: "Recortar",
    reset: "Restablecer tamaño",
    flipH: "Voltear horizontalmente",
    flipV: "Voltear verticalmente"
  },
  "update-status": {
    exceedMaxSize: "El tamaño de la imagen supera el límite, el límite es {0}M",
    invalidImageType: "Tipo de imagen no válido",
    exceedMaxCount: "Solo se pueden subir {0} imágenes a la vez",
    invalidImage: "Imagen no válida"
  },
  "drawing-anchor": {
    title: "Propiedades de anclaje",
    both: "Mover y cambiar tamaño con las celdas",
    position: "Mover pero no cambiar tamaño con las celdas",
    none: "No mover ni cambiar tamaño con las celdas"
  },
  "cell-image": {
    pasteTitle: "Pegar como imagen de celda",
    pasteContent: "Pegar una imagen de celda sobrescribirá el contenido existente de la celda, continuar pegando",
    pasteError: "La copia y pegado de imágenes de celda de hoja no está soportada en esta unidad"
  },
  permission: { dialog: { editErr: "El rango está protegido y no tienes permiso de edición. Para editar, contacta al creador." } },
  shortcut: {
    "drawing-view": "Vista de dibujo",
    "drawing-move-down": "Mover dibujo hacia abajo",
    "drawing-move-up": "Mover dibujo hacia arriba",
    "drawing-move-left": "Mover dibujo a la izquierda",
    "drawing-move-right": "Mover dibujo a la derecha",
    "drawing-delete": "Eliminar dibujo"
  }
} };
const t = mergeLocales(
  locale$1,
  locale
);
export {
  t as default
};
