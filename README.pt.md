# Bytedesk - Serviço de Chat

Serviço de atendimento ao cliente omnicanal com IA e colaboração em equipe

:::tip
O Bytedesk ainda está em estágio inicial de iteração rápida, a documentação pode ficar defasada em relação ao desenvolvimento, resultando em possíveis descrições funcionais que não correspondem à versão mais recente do software
:::

## Recursos principais

### Mensagens em equipe

- Estrutura organizacional multinível
- Gerenciamento de funções
- Gerenciamento de permissões
- Mais

### Atendimento ao cliente

- Suporte multicanal
- Múltiplas estratégias de roteamento e indicadores detalhados
- Estação de trabalho dos agentes
- Mais

### Base de conhecimento

- Documentos internos
- Central de ajuda
- Mais

### Gerenciamento de tickets

- Gerenciamento de tickets
- Gerenciamento de SLA de tickets
- Estatísticas e relatórios de tickets
- Mais

### Chat com IA

- Chat com LLM
- Chat com base de conhecimento (RAG)
- Mais

## Início rápido com Docker

### Clonar projeto e iniciar container Docker

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

### Parar container

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

## Visualização

Visualização local

```bash
http://127.0.0.1:9003/dev
```

- [Visualização online](https://www.weiyuai.cn/admin/)

## Cliente de código aberto

- [Desktop](https://github.com/Bytedesk/bytedesk-desktop)
- [Móvel](https://github.com/Bytedesk/bytedesk-mobile)

## Licença

- [Apache License 2.0](./LICENSE.txt) 