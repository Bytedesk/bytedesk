package com.bytedesk.video.janus.utils;

import java.util.Map;

/**
 * A record representing the server information returned by the Janus Gateway's "info" request.
 */
public record ServerInfo(
		String janus,
		String name,
		int version,
		String versionString,
		String author,
		String commitHash,
		String compileTime,
		boolean logToStdout,
		boolean logToFile,
		boolean dataChannels,
		boolean acceptingNewSessions,
		int sessionTimeout,
		int reclaimSessionTimeout,
		int candidatesTimeout,
		String serverName,
		String localIp,
		String publicIp,
		boolean ipv6,
		boolean iceLite,
		boolean iceTcp,
		String iceNomination,
		boolean iceConsentFreshness,
		boolean iceKeepaliveConncheck,
		boolean hangupOnFailed,
		boolean fullTrickle,
		boolean mdnsEnabled,
		int minNackQueue,
		boolean nackOptimizations,
		int twccPeriod,
		int dtlsMtu,
		int staticEventLoops,
		boolean apiSecret,
		boolean authToken,
		boolean eventHandlers,
		boolean opaqueidInApi,
		Map<String, Dependency> dependencies,
		Map<String, Transport> transports,
		Map<String, Plugin> plugins
) {
	/**
	 * A record representing a dependency used by the Janus Gateway.
	 */
	public record Dependency(String version) {}
	
	/**
	 * A record representing a transport plugin.
	 */
	public record Transport(
			String name,
			String author,
			String description,
			String versionString,
			int version
	) {}
	
	/**
	 * A record representing a Janus plugin.
	 */
	public record Plugin(
			String name,
			String author,
			String description,
			String versionString,
			int version
	) {}
}
