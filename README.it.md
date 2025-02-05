# Bytedesk - Servizio di Chat

Servizio clienti omnicanale potenziato dall'IA con collaborazione di team

:::tip
Bytedesk è ancora in una fase iniziale di rapida iterazione, la documentazione potrebbe essere in ritardo rispetto allo sviluppo, risultando in possibili descrizioni funzionali che non corrispondono all'ultima versione del software
:::

## Caratteristiche principali

### Messaggistica di team

- Struttura organizzativa multilivello
- Gestione dei ruoli
- Gestione dei permessi
- Altro

### Servizio clienti

- Supporto multicanale
- Strategie di routing multiple e indicatori dettagliati
- Postazione di lavoro degli agenti
- Altro

### Base di conoscenza

- Documenti interni
- Centro assistenza
- Altro

### Gestione ticket

- Gestione dei ticket
- Gestione SLA dei ticket
- Statistiche e report dei ticket
- Altro

### Chat IA

- Chat con LLM
- Chat con base di conoscenza (RAG)
- Altro

## Avvio rapido con Docker

### Clonare il progetto e avviare il container Docker

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

### Arrestare il container

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

## Anteprima

Anteprima locale

```bash
http://127.0.0.1:9003/dev
```

- [Anteprima online](https://www.weiyuai.cn/admin/)

## Client open source

- [Desktop](https://github.com/Bytedesk/bytedesk-desktop)
- [Mobile](https://github.com/Bytedesk/bytedesk-mobile)

## Licenza

- [Apache License 2.0](./LICENSE.txt) 