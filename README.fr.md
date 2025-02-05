# Bytedesk - Service de Chat

Service client omnicanal alimenté par l'IA avec collaboration d'équipe

:::tip
Bytedesk est encore dans une phase précoce d'itération rapide, la documentation peut être en retard sur le développement, ce qui peut entraîner des descriptions fonctionnelles qui ne correspondent pas à la dernière version du logiciel
:::

## Fonctionnalités principales

### Messagerie d'équipe

- Structure organisationnelle multi-niveaux
- Gestion des rôles
- Gestion des permissions
- Plus

### Service client

- Support multicanal
- Stratégies de routage multiples et indicateurs détaillés
- Poste de travail des agents
- Plus

### Base de connaissances

- Documents internes
- Centre d'aide
- Plus

### Gestion des tickets

- Gestion des tickets
- Gestion des SLA des tickets
- Statistiques et rapports des tickets
- Plus

### Chat IA

- Chat avec LLM
- Chat avec base de connaissances (RAG)
- Plus

## Démarrage rapide avec Docker

### Cloner le projet et démarrer le conteneur Docker

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

### Arrêter le conteneur

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

## Aperçu

Aperçu local

```bash
http://127.0.0.1:9003/dev
```

- [Aperçu en ligne](https://www.weiyuai.cn/admin/)

## Client open source

- [Bureau](https://github.com/Bytedesk/bytedesk-desktop)
- [Mobile](https://github.com/Bytedesk/bytedesk-mobile)

## Licence

- [Apache License 2.0](./LICENSE.txt) 