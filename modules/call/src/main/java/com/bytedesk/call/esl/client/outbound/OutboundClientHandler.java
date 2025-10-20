/*
 * Copyright 2010 david varnes.
 *
 * Licensed under the Apache License, version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytedesk.call.esl.client.outbound;

import io.netty.channel.ChannelHandlerContext;
import com.bytedesk.call.esl.client.internal.AbstractEslClientHandler;
import com.bytedesk.call.esl.client.internal.Context;
import com.bytedesk.call.esl.client.transport.event.EslEvent;
import com.bytedesk.call.esl.client.transport.message.EslMessage;

import java.util.concurrent.ExecutorService;

/**
 * Specialised {@link AbstractEslClientHandler} that implements the base connecction logic for an
 * 'Outbound' FreeSWITCH Event Socket connection.  The responsibilities for this class are:
 * <ul><li>
 * To send a 'connect' command when the FreeSWITCH server first establishes a new connection with
 * the socket client in Outbound mode.  This will result in an incoming {@link EslMessage} that is
 * transformed into an {@link EslEvent} that sub classes can handle.
 * </ul>
 * Note: implementation requirement is that an {@link ExecutionHandler} is placed in the processing
 * pipeline prior to this handler. This will ensure that each incoming message is processed in its
 * own thread (although still guaranteed to be processed in the order of receipt).
 */
class OutboundClientHandler extends AbstractEslClientHandler {

	private final IClientHandler clientHandler;
	private final ExecutorService callbackExecutor;

	public OutboundClientHandler(IClientHandler clientHandler, ExecutorService callbackExecutor) {
		this.clientHandler = clientHandler;
		this.callbackExecutor = callbackExecutor;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);

		// Have received a connection from FreeSWITCH server, send connect response
		log.debug("Received new connection from server, sending connect message");

		sendApiSingleLineCommand(ctx.channel(), "connect")
				.thenAccept(response -> clientHandler.onConnect(
						new Context(ctx.channel(), OutboundClientHandler.this),
						new EslEvent(response, true)))
				.exceptionally(throwable -> {
					ctx.channel().close();
					handleDisconnectionNotice();
					return null;
				});
	}

	@Override
	protected void handleEslEvent(final ChannelHandlerContext ctx, final EslEvent event) {
		callbackExecutor.execute(() -> clientHandler.onEslEvent(
				new Context(ctx.channel(), OutboundClientHandler.this), event));
	}

	@Override
	protected void handleAuthRequest(io.netty.channel.ChannelHandlerContext ctx) {
		// This should not happen in outbound mode
		log.warn("Auth request received in outbound mode, ignoring");
	}

	@Override
	protected void handleDisconnectionNotice() {
		log.debug("Received disconnection notice");
	}
}
