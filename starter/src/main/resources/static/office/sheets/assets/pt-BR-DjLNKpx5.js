import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale$1 = { "sheets-filter": {
  command: { "not-valid-filter-range": "O intervalo selecionado possui apenas uma linha e não é válido para filtro." },
  msg: { "filter-header-forbidden": "Você não pode mover a linha de cabeçalho de um filtro." }
} };
const locale = { "sheets-filter-ui": {
  toolbar: {
    "smart-toggle-filter-tooltip": "Alternar Filtro",
    "clear-filter-criteria": "Limpar Condições de Filtro",
    "re-calc-filter-conditions": "Recalcular Condições de Filtro"
  },
  shortcut: { "smart-toggle-filter": "Alternar Filtro" },
  panel: {
    "clear-filter": "Limpar Filtro",
    cancel: "Cancelar",
    confirm: "Confirmar",
    "by-values": "Por Valores",
    "by-colors": "Por Cores",
    "filter-by-cell-fill-color": "Filtrar por cor de preenchimento da célula",
    "filter-by-cell-text-color": "Filtrar por cor do texto da célula",
    "filter-by-color-none": "A coluna contém apenas uma cor",
    "by-conditions": "Por Condições",
    "filter-only": "Apenas Filtrar",
    "search-placeholder": "Use espaço para separar palavras-chave",
    "select-all": "Selecionar Todos",
    "input-values-placeholder": "Inserir Valores",
    and: "E",
    or: "OU",
    empty: "(vazio)",
    "?": 'Use "?" para representar um único caractere.',
    "*": 'Use "*" para representar vários caracteres.'
  },
  conditions: {
    none: "Nenhum",
    empty: "Está Vazio",
    "not-empty": "Não Está Vazio",
    "text-contains": "O Texto Contém",
    "does-not-contain": "O Texto Não Contém",
    "starts-with": "O Texto Começa Com",
    "ends-with": "O Texto Termina Com",
    equals: "O Texto É Igual A",
    "greater-than": "Maior Que",
    "greater-than-or-equal": "Maior Ou Igual A",
    "less-than": "Menor Que",
    "less-than-or-equal": "Menor Ou Igual A",
    equal: "Igual",
    "not-equal": "Diferente",
    between: "Entre",
    "not-between": "Não Está Entre",
    custom: "Personalizado"
  },
  date: {
    1: "Janeiro",
    2: "Fevereiro",
    3: "Março",
    4: "Abril",
    5: "Maio",
    6: "Junho",
    7: "Julho",
    8: "Agosto",
    9: "Setembro",
    10: "Outubro",
    11: "Novembro",
    12: "Dezembro"
  },
  sync: {
    title: "O filtro é visível para todos",
    statusTips: {
      on: "Uma vez ativado, todos os colaboradores verão os resultados do filtro",
      off: "Uma vez desativado, apenas você verá os resultados do filtro"
    },
    switchTips: {
      on: '"O filtro é visível para todos" está ativado, todos os colaboradores verão os resultados do filtro',
      off: '"O filtro é visível para todos" está desativado, apenas você verá os resultados do filtro'
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
