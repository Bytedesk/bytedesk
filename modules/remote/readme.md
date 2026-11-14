<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:24:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-23 11:33:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# remote module

The MVP uses P2P WebRTC for desktop video and an ordered DataChannel for input, with a Web controller and an explicitly consenting Electron host. This module owns scoped invitations, session authorization/lifecycle, temporary signaling, TURN credentials and key-event audit records. Room/Participant metadata is reused without routing media through Janus.

See the [client guide](../../frontend/apps/remote/README.md) and [implementation plan](../../docs/plans/2026-11-12-remote-desktop-control-plan.md). Real-device OS input, full backend integration and cross-network/platform acceptance remain pending.

## Package organization

Like `modules/core`, this module groups domain classes by feature rather than by technical layer. Entities, repositories, services, controllers, DTOs and enums for a feature share one package:

- [`device`](src/main/java/com/bytedesk/remote/device/): device records, repository, registration request and device management skeletons.
- [`session`](src/main/java/com/bytedesk/remote/session/): session lifecycle, participant APIs, signaling/ICE, requests/responses and session enums.
- [`audit`](src/main/java/com/bytedesk/remote/audit/): audit records, repository and audit service skeleton.
- `config`, `controller`, `protocol`, `websocket`: module health configuration, frontend page routing and legacy transport concerns; WebSocket configuration and controller are colocated.

Tests mirror the feature packages; the cross-feature schema test stays at the module root. Package moves do not change HTTP paths, payloads, entity/table names or Liquibase changesets. Commented-out skeletons remain inactive.
