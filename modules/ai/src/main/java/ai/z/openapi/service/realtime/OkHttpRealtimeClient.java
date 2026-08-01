package ai.z.openapi.service.realtime;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class OkHttpRealtimeClient implements Closeable {

	private static final Logger logger = LoggerFactory.getLogger(OkHttpRealtimeClient.class);

	private final OkHttpClient client;

	private final CommunicationProvider communicationProvider;

	private final AtomicBoolean isDisposed = new AtomicBoolean(false);

	private final AtomicReference<WebSocket> webSocketRef = new AtomicReference<>();

	private final Consumer<RealtimeServerEvent> serverEventHandler;

	private final ConnectivityMonitor connectivityMonitor = new ConnectivityMonitor();

	private final boolean closeClientOnClose;

	public OkHttpRealtimeClient(CommunicationProvider communicationProvider,
			Consumer<RealtimeServerEvent> serverEventHandler, OkHttpClient client) {
		this.client = client;
		this.communicationProvider = communicationProvider;
		this.serverEventHandler = serverEventHandler;
		this.closeClientOnClose = false;
	}

	public OkHttpRealtimeClient(CommunicationProvider communicationProvider,
			Consumer<RealtimeServerEvent> serverEventHandler, OkHttpClient client, boolean closeClientOnClose) {
		this.client = client;
		this.communicationProvider = communicationProvider;
		this.serverEventHandler = serverEventHandler;
		this.closeClientOnClose = false;
	}

	public OkHttpRealtimeClient(CommunicationProvider communicationProvider,
			Consumer<RealtimeServerEvent> serverEventHandler) {
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		Dispatcher dispatcher = new Dispatcher(executorService);
		dispatcher.setMaxRequests(4);
		dispatcher.setMaxRequestsPerHost(2);
		this.client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS) // Set
																						// connection
																						// timeout
																						// to
																						// 5
																						// seconds
			.readTimeout(20, TimeUnit.SECONDS) // Set read timeout to 20 seconds
			.writeTimeout(20, TimeUnit.SECONDS) // Set write timeout to 20 seconds
			.callTimeout(40, TimeUnit.SECONDS) // Set total call timeout to 40 seconds
			.pingInterval(10, TimeUnit.SECONDS) // Set heartbeat interval to 10 seconds
			.dispatcher(dispatcher) // Set request dispatcher
			.build();
		this.communicationProvider = communicationProvider;
		this.serverEventHandler = serverEventHandler;
		this.closeClientOnClose = true;
	}

	public void start() {
		if (isDisposed.get()) {
			throw new IllegalStateException("Client is closed");
		}

		EnumSet<ConnectivityState> allowedStates = EnumSet.of(ConnectivityState.STOPPED,
				ConnectivityState.DISCONNECTED);

		if (!connectivityMonitor.changeStateOnAnyOf(allowedStates, ConnectivityState.CONNECTING)) {
			throw new IllegalStateException("Cannot start connection in state " + connectivityMonitor.get());
		}

		WebSocketListener listener = new WebSocketListener() {
			@Override
			public void onOpen(WebSocket webSocket, Response response) {
				logger.info("WebSocket connection established");
				webSocketRef.set(webSocket);
				connectivityMonitor.changeState(ConnectivityState.CONNECTED);
			}

			@Override
			public void onMessage(WebSocket webSocket, String text) {
				logger.debug("Received message: {}", text);
				RealtimeServerEvent serverEvent = JasonUtil.fromJsonToServerEvent(text);
				if (serverEvent == null) {
					logger.error("Unable to parse server event: {}", text);
					return;
				}
				serverEventHandler.accept(serverEvent);
			}

			@Override
			public void onClosed(WebSocket webSocket, int code, String reason) {
				logger.info("Connection closed normally, reason: {}", reason);
				webSocketRef.set(null);
				if (!isStoppingState()) {
					connectivityMonitor.changeState(ConnectivityState.DISCONNECTED);
				}
			}

			@Override
			public void onFailure(WebSocket webSocket, Throwable t, Response response) {
				logger.error("Connection error", t);
				if (response != null) {
					logger.error("Error response code: {}, response content: {}", response.code(),
							response.body() != null ? response.body().toString() : "Empty content");
				}
				webSocketRef.set(null);
				if (!isStoppingState()) {
					connectivityMonitor.changeState(ConnectivityState.DISCONNECTED);
				}
			}

			private boolean isStoppingState() {
				ConnectivityState state = connectivityMonitor.get();
				return state == ConnectivityState.STOPPING || state == ConnectivityState.CLOSED;
			}
		};

		Request request = new Request.Builder() //
			.url(communicationProvider.getWebSocketUrl()) //
			.addHeader("Authorization", "Bearer " + communicationProvider.getAuthToken()) //
			.build();
		request.url().redact();
		client.newWebSocket(request, listener);
		client.dispatcher().executorService().submit(() -> logger.info("WebSocket connection thread started"));
	}

	public void stop() {
		ConnectivityState currentState = connectivityMonitor.get();
		if (currentState == ConnectivityState.CLOSED) {
			throw new IllegalStateException("Client is closed");
		}

		if (connectivityMonitor.changeStateOn(ConnectivityState.CONNECTED, ConnectivityState.STOPPING)) {
			WebSocket webSocket = webSocketRef.get();
			if (webSocket != null) {
				webSocket.close(1000, "Normal close");
			}
		}
		else {
			logger.warn("Stop failed, current state: {}", connectivityMonitor.get());
		}
	}

	public void waitForConnection() throws InterruptedException {
		while (connectivityMonitor.get() != ConnectivityState.CONNECTED) {
			Thread.sleep(100);
		}
	}

	public void sendMessage(RealtimeClientEvent clientEvent) {
		ConnectivityState state = connectivityMonitor.get();
		if (state != ConnectivityState.CONNECTED) {
			throw new IllegalStateException("Connection not ready, current state: " + state);
		}

		WebSocket webSocket = webSocketRef.get();
		if (webSocket == null) {
			throw new IllegalStateException("WebSocket connection not established");
		}

		String jsonMessage = JasonUtil.toJsonFromClientEvent(clientEvent);
		if (jsonMessage == null) {
			logger.error("Unable to serialize client event: type={}, event_id={}", clientEvent.getType(),
					clientEvent.getEventId());
			return;
		}
		webSocket.send(jsonMessage);
	}

	@Override
	public void close() throws IOException {
		if (isDisposed.compareAndSet(false, true)) {
			connectivityMonitor.changeState(ConnectivityState.CLOSED);
			WebSocket webSocket = webSocketRef.get();
			if (webSocket != null) {
				webSocket.close(1000, "Client close");
				webSocketRef.set(null);
			}
			if (closeClientOnClose) {
				client.dispatcher().executorService().shutdown();
			}
		}
	}

	public enum ConnectivityState {

		STOPPED, CONNECTING, CONNECTED, DISCONNECTED, STOPPING, CLOSED

	}

	public interface CommunicationProvider {

		String getWebSocketUrl();

		String getAuthToken();

	}

	public static final class ConnectivityMonitor {

		private final AtomicReference<ConnectivityState> clientState = new AtomicReference<>(ConnectivityState.STOPPED);

		public ConnectivityState get() {
			return clientState.get();
		}

		public boolean changeStateOn(ConnectivityState expected, ConnectivityState newState) {
			boolean updated = clientState.compareAndSet(expected, newState);
			if (updated) {
				logger.info("State change: {} -> {}", expected, newState);
			}
			return updated;
		}

		public boolean changeStateOnAnyOf(EnumSet<ConnectivityState> expecteds, ConnectivityState newState) {
			for (;;) {
				ConnectivityState current = clientState.get();
				if (!expecteds.contains(current))
					return false;
				if (clientState.compareAndSet(current, newState)) {
					logger.info("State change: {} -> {}", current, newState);
					return true;
				}
			}
		}

		public void changeState(ConnectivityState newState) {
			ConnectivityState prev = clientState.getAndSet(newState);
			logger.info("State change: {} -> {}", prev, newState);
		}

	}

}