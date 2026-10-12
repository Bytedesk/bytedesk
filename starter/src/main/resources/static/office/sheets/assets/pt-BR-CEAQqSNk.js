import { m as mergeLocales } from "./index-OqF_uRM0.js";
const locale = { "find-replace": {
  toolbar: "Localizar e Substituir",
  shortcut: {
    "open-find-dialog": "Abrir Caixa de Localização",
    "open-replace-dialog": "Abrir Caixa de Substituição",
    "close-dialog": "Fechar Caixa de Localizar e Substituir",
    "go-to-next-match": "Ir para Próxima Correspondência",
    "go-to-previous-match": "Ir para Correspondência Anterior",
    "focus-selection": "Focar Seleção",
    panel: "Localizar e Substituir"
  },
  dialog: {
    title: "Localizar",
    find: "Localizar",
    replace: "Substituir",
    "replace-all": "Substituir Tudo",
    "case-sensitive": "Diferenciar Maiúsculas/Minúsculas",
    "find-placeholder": "Localizar nesta Planilha",
    "advanced-finding": "Pesquisa e Substituição Avançadas",
    "replace-placeholder": "Digite o Texto de Substituição",
    "match-the-whole-cell": "Corresponder à Célula Inteira",
    "find-direction": {
      title: "Direção da Pesquisa",
      row: "Pesquisar por Linha",
      column: "Pesquisar por Coluna"
    },
    "find-scope": {
      title: "Intervalo da Pesquisa",
      "current-sheet": "Planilha Atual",
      workbook: "Pasta de Trabalho"
    },
    "find-by": {
      title: "Pesquisar Por",
      value: "Pesquisar por Valor",
      formula: "Localizar Fórmula"
    },
    "no-match": "Pesquisa concluída, mas nenhuma correspondência foi encontrada.",
    "no-result": "Nenhum Resultado"
  },
  replace: {
    "all-success": "Substituídas todas as {0} correspondências",
    "all-failure": "Falha na substituição",
    confirm: { title: "Tem certeza de que deseja substituir todas as correspondências?" }
  },
  button: {
    confirm: "OK",
    cancel: "Cancelar"
  }
} };
const m = mergeLocales(
  locale
);
export {
  m as default
};
