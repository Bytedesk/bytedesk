# Bytedesk - Service de Chat

Service client omnicanal propulsé par l'IA avec collaboration d'équipe

## Langue

- [English](./README.md)
- [中文](./README.zh.md)
- [Français](./README.fr.md)

## Dashboard administrateur

![statistics](./images/admin/statistics.png)

## Chat administrateur

![chat](./images/admin/chat.png)

## LLM + Agent

![llm_agent](./images/admin/llm_agent.png)

## Canaux administrateur

![channel](./images/admin/channel.png)

## Poste de travail agent

![agent](./images/agent/agent_chat.png)

## Présentation

### [TeamIM](./modules/team/readme.md)

- Structure organisationnelle multi-niveaux
- Gestion des rôles et des permissions
- Audit et conformité intégrés
- ...

### [Service client](./modules/service/readme.md)

- Connexion aux canaux web, app, social et e-commerce
- Stratégies de routage intelligentes avec KPIs
- Espace de travail unifié pour les agents
- ...

### [Base de connaissances](./modules/kbase/readme.md)

- Documentation interne et Help Center
- FAQ et bibliothèques RAG pour l'IA
- Synchronisation avec les agents intelligents
- ...

### [Gestion des tickets](./modules/ticket/readme.md)

- Gestion du cycle de vie des tickets
- SLA personnalisables et suivi automatique
- Tableaux de bord statistiques
- ...

### [Agent IA](./modules/ai/readme.md)

- Chat avec Ollama / DeepSeek / ZhipuAI / ...
- RAG connecté à la base de connaissances
- Function Calling & MCP
- ...

### [Workflow](./modules/core/readme.workflow.md)

- Formulaires personnalisés
- Concepteur de processus visuel
- Automatisation des flux de tickets
- ...

### [Voix du client](./modules/voc/readme.md)

- Feedback, enquêtes, réclamations
- Mesure continue de la satisfaction
- Rapports prêts pour la direction
- ...

### [Centre d'appels](./plugins/freeswitch/readme.zh.md)

- Plateforme professionnelle basée sur FreeSwitch
- Pop-up d'appel, distribution automatique, enregistrement
- Fusion voix + texte dans un même écran

### [Service vidéo](./plugins/webrtc/readme.zh.md)

- Appels vidéo HD WebRTC
- Partage d'écran et vidéo en un clic
- Adapté aux démonstrations et aux services premium

### [Plateforme ouverte](./plugins/readme.md)

- APIs RESTful complètes et SDK multi-langues
- Intégration fluide avec des systèmes tiers
- Simplifie les projets d'extension

## Démarrage rapide

```bash
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker
# Démarrer sans IA
docker compose -p bytedesk -f docker-compose-noai.yaml up -d
# Démarrer avec ZhipuAI (clé API requise)
docker compose -p bytedesk -f docker-compose.yaml up -d
# Démarrer avec Ollama local
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

- [Déploiement Docker](https://www.weiyuai.cn/docs/docs/deploy/docker/)
- [Déploiement Baota](https://www.weiyuai.cn/docs/docs/deploy/baota)
- [Lancement depuis le code source](https://www.weiyuai.cn/docs/docs/deploy/source)

## Accès de démonstration

```bash
# Remplacez 127.0.0.1 par l'IP de votre serveur
http://127.0.0.1:9003/
# Ports ouverts : 9003, 9885
Compte par défaut : admin@email.com
Mot de passe par défaut : admin
```

## Structure du projet

Monorepo basé sur Maven (fichier `pom.xml` racine) incluant plusieurs modules et ressources de déploiement.

```text
bytedesk/
├─ channels/           # Intégrations Douyin, boutiques, social, WeChat
├─ demos/              # Projets exemples et code de démonstration
├─ deploy/             # Docker, K8s, configurations serveur
├─ enterprise/         # Modules entreprise (ai, call, core, kbase, service, ticket)
├─ images/             # Illustrations pour docs et UI
├─ jmeter/             # Tests de performance
├─ logs/               # Journaux locaux/dev
├─ modules/            # Modules cœur (TeamIM, Service, KBase, Ticket, AI ...)
├─ plugins/            # Plugins optionnels (freeswitch, webrtc, open platform)
├─ projects/           # Projets personnalisés
├─ starter/            # Starters et points d'entrée
```

## Architecture

- [Schéma d'architecture](https://www.weiyuai.cn/architecture.html)

## Clients open source

- [Desktop](https://github.com/Bytedesk/bytedesk-desktop)
- [Mobile](https://github.com/Bytedesk/bytedesk-mobile)
- [SipPhone](https://github.com/Bytedesk/bytedesk-phone)
- [Conference](https://github.com/Bytedesk/bytedesk-conference)
- [FreeSwitch Docker](https://github.com/Bytedesk/bytedesk-freeswitch)
- [Jitsi Docker](https://github.com/Bytedesk/bytedesk-jitsi)

## Démos et SDK open source

| Projet | Description | Forks | Stars |
|--------|-------------|-------|-------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | Application iOS native | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift) |
| [Android](https://github.com/bytedesk/bytedesk-android) | Application Android native | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android) |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | SDK Flutter | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | Package UniApp | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | Frontend Vue/React/Angular/Next.js | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |
| [WordPress](https://github.com/bytedesk/bytedesk-wordpress) | Plugin WordPress | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-wordpress) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-wordpress) |
| [WooCommerce](https://github.com/bytedesk/bytedesk-woocommerce) | Intégration WooCommerce | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-woocommerce) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-woocommerce) |
| [Magento](https://github.com/bytedesk/bytedesk-magento) | Extension Magento | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-magento) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-magento) |
| [PrestaShop](https://github.com/bytedesk/bytedesk-prestashop) | Module PrestaShop | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-prestashop) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-prestashop) |
| [Shopify](https://github.com/bytedesk/bytedesk-shopify) | Application Shopify | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-shopify) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-shopify) |
| [OpenCart](https://github.com/bytedesk/bytedesk-opencart) | Plugin OpenCart | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-opencart) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-opencart) |
| [Laravel](https://github.com/bytedesk/bytedesk-laravel) | Package Laravel | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-laravel) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-laravel) |
| [Django](https://github.com/bytedesk/bytedesk-django) | Application Django | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-django) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-django) |

## Liens

- [Téléchargement](https://www.weiyuai.cn/download.html)
- [Documentation](https://www.weiyuai.cn/docs/)

## Licence

Copyright (c) 2013-2025 Bytedesk.com.

Distribué sous GNU AFFERO GENERAL PUBLIC LICENSE (AGPL v3) :

<https://www.gnu.org/licenses/agpl-3.0.html>

Logiciel fourni "en l'état" sans garantie explicite ou implicite. Vérifiez les conditions avant toute utilisation commerciale.

## Conditions d'utilisation

- **Utilisation autorisée** : usage commercial possible, revente interdite sans autorisation
- **Utilisation interdite** : activités illégales (malware, fraude, jeux, etc.)
- **Clause de non-responsabilité** : utilisation à vos risques et périls