/**
 * session package for remote assistance session lifecycle, signaling, and session APIs.
 * session 包，负责远程协助会话生命周期、信令与会话接口。
 *
 * <p>Session entities, repositories, application services, request and response models, and session APIs are
 * implemented here. Session transitions are published as audit events for the enterprise audit trail, while
 * TURN/STUN credentials are issued by the enterprise module.
 * 这里实现了会话实体、仓库、应用服务、请求响应模型与会话接口。会话状态变化以审计事件形式发布给企业版审计轨迹，
 * TURN/STUN 凭证由企业版模块签发。
 *
 * @author bytedesk.com
 */
@NullMarked
package com.bytedesk.remote.session;

import org.jspecify.annotations.NullMarked;
