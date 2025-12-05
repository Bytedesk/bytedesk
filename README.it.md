# Bytedesk - Servizio di Chat

Servizio clienti omnicanale alimentato dall'IA con collaborazione dei team

## Lingua

- [English](./README.md)
- [中文](./README.zh.md)
- [Italiano](./README.it.md)

## Dashboard Admin

![statistics](./images/admin/statistics.png)

## Chat Admin

![chat](./images/admin/chat.png)

## Admin LLM + Agent

![llm_agent](./images/admin/llm_agent.png)

## Canali Admin

![channel](./images/admin/channel.png)

## Postazione Agente

![agent](./images/agent/agent_chat.png)

## Introduzione

### [TeamIM](./modules/team/readme.md)

- Struttura organizzativa multilivello
- Gestione avanzata di ruoli e permessi
- Audit trail e controllo compliance
- ...

### [Customer Service](./modules/service/readme.md)

- Supporto per web, app, social e shop
- Strategie di routing intelligenti con KPI dettagliati
- Workspace unificato per gli agenti
- ...

### [Knowledge Base](./modules/kbase/readme.md)

- Documentazione interna e Help Center
- FAQ e knowledge base RAG per i modelli
- Sync con agenti AI
- ...

### [Ticketing](./modules/ticket/readme.md)

- Gestione end-to-end del ciclo ticket
- SLA configurabili e monitorati
- Report e dashboard in tempo reale
- ...

### [AI Agent](./modules/ai/readme.md)

- Chat con Ollama / DeepSeek / ZhipuAI / ...
- Conversazioni basate sulla knowledge base (RAG)
- Function Calling e MCP
- ...

### [Workflow](./modules/core/readme.workflow.md)

- Moduli personalizzati
- Designer visuale dei processi
- Automazione dei flussi di ticket
- ...

### [Voice of Customer](./modules/voc/readme.md)

- Feedback, survey e reclami
- Misurazione continua della soddisfazione
- ...

### [Call Center](./plugins/freeswitch/readme.zh.md)

- Piattaforma professionale basata su FreeSwitch
- Screen pop, assegnazione automatica, registrazione chiamate
- Integrazione tra voce e testo

### [Video Customer Service](./plugins/webrtc/readme.zh.md)

- Videochiamate HD via WebRTC
- Video e condivisione schermo con un clic
- Ideale per supporto visivo

### [Open Platform](./plugins/readme.md)

- API RESTful complete e SDK multilingua
- Integrazione semplice con sistemi terzi
- Accelerazione di sviluppo e deploy

## Avvio rapido

```bash
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker
# Avvio senza moduli AI
docker compose -p bytedesk -f docker-compose-noai.yaml up -d
# Avvio con ZhipuAI (API Key necessaria)
docker compose -p bytedesk -f docker-compose.yaml up -d
# Avvio con Ollama locale
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

- [Deploy Docker](https://www.weiyuai.cn/docs/docs/deploy/docker/)
- [Deploy Baota](https://www.weiyuai.cn/docs/docs/deploy/baota)
- [Avvio da sorgente](https://www.weiyuai.cn/docs/docs/deploy/source)

## Accesso demo

```bash
# Sostituisci 127.0.0.1 con l'IP del server
http://127.0.0.1:9003/
# Porte aperte: 9003, 9885
Account predefinito: admin@email.com
Password predefinita: admin
```

## Struttura del progetto

Monorepo Maven (`pom.xml` root) con moduli multipli e asset di deploy.

```text
bytedesk/
├─ channels/           # Integrazioni canali (Douyin, shop, social, WeChat)
├─ demos/              # Progetti demo ed esempi
├─ deploy/             # Asset di deploy: Docker, K8s, configurazioni server
├─ enterprise/         # Funzioni enterprise (ai, call, core, kbase, service, ticket)
├─ images/             # Immagini per documenti e UI
├─ jmeter/             # Test di performance
├─ logs/               # Log locali/dev
├─ modules/            # Moduli core (TeamIM, Service, KBase, Ticket, AI ...)
├─ plugins/            # Plugin opzionali (freeswitch, webrtc, open platform)
├─ projects/           # Estensioni personalizzate
├─ starter/            # Starter e entry point
```

## Architettura

- [Diagramma architetturale](https://www.weiyuai.cn/architecture.html)

## Client open source

- [Desktop](https://github.com/Bytedesk/bytedesk-desktop)
- [Mobile](https://github.com/Bytedesk/bytedesk-mobile)
- [SipPhone](https://github.com/Bytedesk/bytedesk-phone)
- [Conference](https://github.com/Bytedesk/bytedesk-conference)
- [FreeSwitch Docker](https://github.com/Bytedesk/bytedesk-freeswitch)
- [Jitsi Docker](https://github.com/Bytedesk/bytedesk-jitsi)

## Demo e SDK open source

| Progetto | Descrizione | Forks | Stars |
|----------|-------------|-------|-------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | App iOS nativa | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift) |
| [Android](https://github.com/bytedesk/bytedesk-android) | App Android nativa | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android) |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | SDK Flutter | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | Pacchetto UniApp | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | Frontend Vue/React/Angular/Next.js | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |
| [WordPress](https://github.com/bytedesk/bytedesk-wordpress) | Plugin WordPress | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-wordpress) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-wordpress) |
| [WooCommerce](https://github.com/bytedesk/bytedesk-woocommerce) | Integrazione WooCommerce | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-woocommerce) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-woocommerce) |
| [Magento](https://github.com/bytedesk/bytedesk-magento) | Estensione Magento | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-magento) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-magento) |
| [PrestaShop](https://github.com/bytedesk/bytedesk-prestashop) | Modulo PrestaShop | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-prestashop) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-prestashop) |
| [Shopify](https://github.com/bytedesk/bytedesk-shopify) | App Shopify | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-shopify) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-shopify) |
| [OpenCart](https://github.com/bytedesk/bytedesk-opencart) | Plugin OpenCart | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-opencart) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-opencart) |
| [Laravel](https://github.com/bytedesk/bytedesk-laravel) | Pacchetto Laravel | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-laravel) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-laravel) |
| [Django](https://github.com/bytedesk/bytedesk-django) | App Django | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-django) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-django) |

## Link

- [Download](https://www.weiyuai.cn/download.html)
- [Documentazione](https://www.weiyuai.cn/docs/)

## Licenza

Copyright (c) 2013-2025 Bytedesk.com.

Distribuito sotto GNU AFFERO GENERAL PUBLIC LICENSE (AGPL v3):

<https://www.gnu.org/licenses/agpl-3.0.html>

Software fornito "così com'è" senza garanzie. Verificare i termini prima dell'uso commerciale.

## Condizioni d'uso

- **Uso consentito**: utilizzo commerciale ok, vietata la rivendita senza permesso
- **Uso vietato**: attività illegali (malware, frodi, gioco d'azzardo, ecc.)
- **Disclaimer**: uso a proprio rischio, nessuna responsabilità legale