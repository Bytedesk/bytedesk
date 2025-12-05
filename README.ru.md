# Bytedesk - Сервис чата

Омниканальный клиентский сервис с ИИ и совместной работой команд

## Язык

- [English](./README.md)
- [中文](./README.zh.md)
- [Русский](./README.ru.md)

## Админ-панель

![statistics](./images/admin/statistics.png)

## Админ-чат

![chat](./images/admin/chat.png)

## LLM + Агент

![llm_agent](./images/admin/llm_agent.png)

## Каналы

![channel](./images/admin/channel.png)

## Рабочее место агента

![agent](./images/agent/agent_chat.png)

## Обзор

### [TeamIM](./modules/team/readme.md)

- Многоуровневая иерархия
- Управление ролями и правами
- Аудит и контроль истории переписки
- ...

### [Клиентский сервис](./modules/service/readme.md)

- Интеграция веба, приложений, соцсетей, магазинов
- Умные стратегии маршрутизации с KPI
- Единое рабочее место агента
- ...

### [База знаний](./modules/kbase/readme.md)

- Внутренние документы и Help Center
- FAQ и RAG-библиотеки
- Синхронизация с AI-агентами
- ...

### [Тикетная система](./modules/ticket/readme.md)

- Полный жизненный цикл тикета
- SLA-мониторинг и оповещения
- Отчеты и аналитика
- ...

### [AI Agent](./modules/ai/readme.md)

- Диалоги с Ollama / DeepSeek / ZhipuAI / ...
- Чат с базой знаний (RAG)
- Function Calling и MCP
- ...

### [Workflow](./modules/core/readme.workflow.md)

- Кастомные формы
- Визуальный конструктор процессов
- Автоматизация сценариев
- ...

### [Голос клиента](./modules/voc/readme.md)

- Обратная связь, опросы, жалобы
- Мониторинг удовлетворенности
- ...

### [Колл-центр](./plugins/freeswitch/readme.zh.md)

- Платформа на FreeSwitch
- Pop-up карточки, автодозвон, запись
- Единая статистика по голосу и тексту

### [Видео-сервис](./plugins/webrtc/readme.zh.md)

- HD-видеозвонки на WebRTC
- Видеочат и шаринг экрана в один клик
- Подходит для демонстраций и premium-сценариев

### [Open Platform](./plugins/readme.md)

- Полный набор RESTful API и SDK
- Легкая интеграция с системами третьих лиц
- Ускорение разработки расширений

## Быстрый старт

```bash
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker
# Запуск без модулей ИИ
docker compose -p bytedesk -f docker-compose-noai.yaml up -d
# Запуск с ZhipuAI (нужен API-ключ)
docker compose -p bytedesk -f docker-compose.yaml up -d
# Запуск с локальным Ollama
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

- [Docker-деплой](https://www.weiyuai.cn/docs/docs/deploy/docker/)
- [Деплой через Baota](https://www.weiyuai.cn/docs/docs/deploy/baota)
- [Запуск из исходников](https://www.weiyuai.cn/docs/docs/deploy/source)

## Доступ к демо

```bash
# Замените 127.0.0.1 на IP сервера
http://127.0.0.1:9003/
# Порты: 9003, 9885
Логин: admin@email.com
Пароль: admin
```

## Структура проекта

Maven-монорепозиторий (корневой `pom.xml`) с множеством модулей и артефактов деплоя.

```text
bytedesk/
├─ channels/           # Канальные интеграции (Douyin, магазины, соцсети, WeChat)
├─ demos/              # Демопроекты и примеры
├─ deploy/             # Docker, K8s и серверные конфиги
├─ enterprise/         # Enterprise-модули (ai, call, core, kbase, service, ticket)
├─ images/             # Скриншоты и иллюстрации
├─ jmeter/             # Нагрузочные тесты
├─ logs/               # Логи (local/dev)
├─ modules/            # Базовые модули (TeamIM, Service, KBase, Ticket, AI ...)
├─ plugins/            # Доп. плагины (freeswitch, webrtc, open platform)
├─ projects/           # Кастомные проекты
├─ starter/            # Стартовые приложения
```

## Архитектура

- [Диаграмма архитектуры](https://www.weiyuai.cn/architecture.html)

## Клиенты с открытым кодом

- [Desktop](https://github.com/Bytedesk/bytedesk-desktop)
- [Mobile](https://github.com/Bytedesk/bytedesk-mobile)
- [SipPhone](https://github.com/Bytedesk/bytedesk-phone)
- [Conference](https://github.com/Bytedesk/bytedesk-conference)
- [FreeSwitch Docker](https://github.com/Bytedesk/bytedesk-freeswitch)
- [Jitsi Docker](https://github.com/Bytedesk/bytedesk-jitsi)

## Open Source Demo + SDK

| Проект | Описание | Forks | Stars |
|--------|----------|-------|-------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | Нативное приложение iOS | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift) |
| [Android](https://github.com/bytedesk/bytedesk-android) | Нативное приложение Android | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android) |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | Flutter SDK | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | Пакет UniApp | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | Фронтенд Vue/React/Angular/Next.js | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |
| [WordPress](https://github.com/bytedesk/bytedesk-wordpress) | Плагин WordPress | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-wordpress) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-wordpress) |
| [WooCommerce](https://github.com/bytedesk/bytedesk-woocommerce) | Интеграция WooCommerce | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-woocommerce) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-woocommerce) |
| [Magento](https://github.com/bytedesk/bytedesk-magento) | Расширение Magento | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-magento) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-magento) |
| [PrestaShop](https://github.com/bytedesk/bytedesk-prestashop) | Модуль PrestaShop | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-prestashop) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-prestashop) |
| [Shopify](https://github.com/bytedesk/bytedesk-shopify) | Приложение Shopify | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-shopify) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-shopify) |
| [OpenCart](https://github.com/bytedesk/bytedesk-opencart) | Плагин OpenCart | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-opencart) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-opencart) |
| [Laravel](https://github.com/bytedesk/bytedesk-laravel) | Пакет Laravel | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-laravel) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-laravel) |
| [Django](https://github.com/bytedesk/bytedesk-django) | Приложение Django | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-django) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-django) |

## Ссылки

- [Загрузка](https://www.weiyuai.cn/download.html)
- [Документация](https://www.weiyuai.cn/docs/)

## Лицензия

Copyright (c) 2013-2025 Bytedesk.com.

Проект распространяется по лицензии GNU AFFERO GENERAL PUBLIC LICENSE (AGPL v3):

<https://www.gnu.org/licenses/agpl-3.0.html>

ПО предоставляется «как есть» без каких-либо гарантий.

## Условия использования

- **Разрешено**: коммерческое использование, но перепродажа без разрешения запрещена
- **Запрещено**: любые нелегальные сценарии (вирусы, мошенничество, азартные игры и т. д.)
- **Отказ от ответственности**: все риски и юридическая ответственность несет пользователь