import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "drawing-ui": {
  "image-cropper": { error: "Não é possível cortar objetos que não são imagens." },
  "image-panel": {
    arrange: {
      title: "Organizar",
      forward: "Trazer para frente",
      backward: "Enviar para trás",
      front: "Trazer para a frente",
      back: "Enviar para trás"
    },
    transform: {
      title: "Transformar",
      rotate: "Girar (°)",
      x: "X (px)",
      y: "Y (px)",
      width: "Largura (px)",
      height: "Altura (px)",
      lock: "Bloquear proporção (%)"
    },
    crop: {
      title: "Cortar",
      start: "Iniciar corte",
      mode: "Livre"
    },
    group: {
      title: "Agrupar",
      group: "Agrupar",
      unGroup: "Desagrupar"
    },
    align: {
      title: "Alinhar",
      default: "Selecionar tipo de alinhamento",
      left: "Alinhar à esquerda",
      center: "Alinhar ao centro",
      right: "Alinhar à direita",
      top: "Alinhar ao topo",
      middle: "Alinhar ao meio",
      bottom: "Alinhar à base",
      horizon: "Distribuir horizontalmente",
      vertical: "Distribuir verticalmente"
    },
    null: "Nenhum objeto selecionado"
  },
  "image-text-wrap": {
    title: "Quebra de texto",
    wrappingStyle: "Estilo de quebra",
    square: "Quadrado",
    topAndBottom: "Superior e inferior",
    inline: "Em linha com o texto",
    behindText: "Atrás do texto",
    inFrontText: "À frente do texto",
    wrapText: "Quebrar texto automaticamente",
    bothSide: "Ambos os lados",
    leftOnly: "Somente esquerda",
    rightOnly: "Somente direita",
    distanceFromText: "Distância do texto",
    top: "Superior(px)",
    left: "Esquerda(px)",
    bottom: "Inferior(px)",
    right: "Direita(px)"
  },
  "image-popup": {
    replace: "Substituir",
    delete: "Excluir",
    edit: "Editar",
    crop: "Cortar",
    reset: "Redefinir tamanho"
  }
} };
const locale = { "sheets-drawing-ui": {
  title: "Imagem",
  upload: {
    float: "Imagem flutuante",
    cell: "Imagem de célula"
  },
  panel: { title: "Editar imagem" },
  save: {
    title: "Salvar imagens de célula",
    menuLabel: "Salvar imagens de célula",
    imageCount: "Contagem de imagens",
    fileNameConfig: "Nome do arquivo",
    useRowCol: "Usar endereço da célula (A1, B2...)",
    useColumnValue: "Usar valor da coluna",
    selectColumn: "Selecionar coluna",
    cancel: "Cancelar",
    confirm: "Salvar",
    saving: "Salvando...",
    error: "Falha ao salvar imagens de célula"
  },
  "image-popup": {
    replace: "Substituir",
    delete: "Excluir",
    edit: "Editar",
    crop: "Cortar",
    reset: "Redefinir tamanho",
    flipH: "Inverter horizontalmente",
    flipV: "Inverter verticalmente"
  },
  "update-status": {
    exceedMaxSize: "O tamanho da imagem excede o limite, o limite é {0}M",
    invalidImageType: "Tipo de imagem inválido",
    exceedMaxCount: "Apenas {0} imagens podem ser enviadas de cada vez",
    invalidImage: "Imagem inválida"
  },
  "drawing-anchor": {
    title: "Propriedades da âncora",
    both: "Mover e dimensionar com as células",
    position: "Mover, mas não dimensionar com as células",
    none: "Não mover nem dimensionar com as células"
  },
  "cell-image": {
    pasteTitle: "Colar como imagem de célula",
    pasteContent: "Colar uma imagem de célula substituirá o conteúdo existente da célula, continuar colando",
    pasteError: "Copiar e colar imagem de célula da planilha não é suportado nesta unidade"
  },
  permission: { dialog: { editErr: "O intervalo está protegido e você não tem permissão de edição. Para editar, entre em contato com o criador." } },
  shortcut: {
    "drawing-view": "Visualização do desenho",
    "drawing-move-down": "Mover desenho para baixo",
    "drawing-move-up": "Mover desenho para cima",
    "drawing-move-left": "Mover desenho para esquerda",
    "drawing-move-right": "Mover desenho para direita",
    "drawing-delete": "Excluir desenho"
  }
} };
const s = mergeLocales(
  locale$1,
  locale
);
export {
  s as default
};
