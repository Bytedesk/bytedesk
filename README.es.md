# Bytedesk - Servicio de Chat

Servicio al cliente omnicanal potenciado por IA con cooperación en equipo

:::tip
Bytedesk está todavía en una etapa temprana de iteración rápida, la documentación puede quedarse atrás del desarrollo, resultando en posibles descripciones funcionales que no coinciden con la última versión del software
:::

## Características principales

### Mensajería de equipo

- Estructura organizativa multinivel
- Gestión de roles
- Gestión de permisos
- Más

### Servicio al cliente

- Soporte multicanal
- Múltiples estrategias de enrutamiento e indicadores detallados
- Estación de trabajo para agentes
- Más

### Base de conocimientos

- Documentos internos
- Centro de ayuda
- Más

### Gestión de tickets

- Gestión de tickets
- Gestión de SLA de tickets
- Estadísticas e informes de tickets
- Más

### Chat con IA

- Chat con LLM
- Chat con base de conocimientos (RAG)
- Más

## Inicio rápido con Docker

### Clonar proyecto e iniciar contenedor Docker

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

### Detener contenedor

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

## Vista previa

Vista previa local

```bash
http://127.0.0.1:9003/dev
```

- [Vista previa en línea](https://www.weiyuai.cn/admin/)

## Cliente de código abierto

- [Escritorio](https://github.com/Bytedesk/bytedesk-desktop)
- [Móvil](https://github.com/Bytedesk/bytedesk-mobile)

## Licencia

- [Apache License 2.0](./LICENSE.txt) 