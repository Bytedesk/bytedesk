# Bytedesk - Chat als Service

KI-gestützter Omnichannel-Kundenservice mit Teamzusammenarbeit

:::tip
Bytedesk befindet sich noch in einer frühen Phase der schnellen Iteration, die Dokumentation kann hinter der Entwicklung zurückbleiben, was zu möglichen Funktionsbeschreibungen führt, die nicht mit der neuesten Softwareversion übereinstimmen
:::

## Hauptfunktionen

### Team-Messaging

- Mehrstufige Organisationsstruktur
- Rollenverwaltung
- Berechtigungsverwaltung
- Mehr

### Kundenservice

- Multichannel-Unterstützung
- Mehrere Routing-Strategien und detaillierte Bewertungsindikatoren
- Agenten-Arbeitsplatz
- Mehr

### Wissensdatenbank

- Interne Dokumente
- Hilfezentrum
- Mehr

### Ticket-Management

- Ticket-Verwaltung
- Ticket-SLA-Management
- Ticket-Statistiken und Berichte
- Mehr

### KI-Chat

- Chat mit LLM
- Chat mit Wissensdatenbank (RAG)
- Mehr

## Docker Schnellstart

### Projekt klonen und Docker-Container starten

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

### Container stoppen

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

## Vorschau

Lokale Vorschau

```bash
http://127.0.0.1:9003/dev
```

- [Online-Vorschau](https://www.weiyuai.cn/admin/)

## Open-Source-Client

- [Desktop](https://github.com/Bytedesk/bytedesk-desktop)
- [Mobil](https://github.com/Bytedesk/bytedesk-mobile)

## Lizenz

- [Apache License 2.0](./LICENSE.txt) 