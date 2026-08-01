package com.bytedesk.call.mrcp.rtp;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;

/**
 * Minimal UDP listener that forwards RTP datagrams into a capture receiver.
 */
@Slf4j
public class UdpRtpCaptureListener implements Closeable {

    static final int DEFAULT_PACKET_BUFFER_SIZE = 2048;
    static final String DEFAULT_BIND_HOST = "0.0.0.0";
    static final int DEFAULT_READ_TIMEOUT_MS = 2000;

    private final BytedeskRtpSessionFactory.RtpSession session;
    private final RtpCaptureReceiver captureReceiver;
    private final DatagramSocket datagramSocket;
    private final int packetBufferSize;
    private final InetAddress expectedRemoteAddress;
    private final int expectedRemotePort;
    private volatile boolean closed;
    private int acceptedPacketLogCount;
    private int unexpectedRemoteLogCount;

    public UdpRtpCaptureListener(BytedeskRtpSessionFactory.RtpSession session, RtpCaptureReceiver captureReceiver) {
        this(session, captureReceiver, DEFAULT_BIND_HOST, -1, DEFAULT_PACKET_BUFFER_SIZE);
    }

    public UdpRtpCaptureListener(
            BytedeskRtpSessionFactory.RtpSession session,
            RtpCaptureReceiver captureReceiver,
            String bindHost,
            int packetBufferSize) {
        this(session, captureReceiver, bindHost, -1, packetBufferSize);
    }

    /**
     * @param localBindPort 0 for OS-assigned ephemeral port, &gt;0 for explicit port, &lt;0 to fall back to {@code session.port()}
     */
    public UdpRtpCaptureListener(
            BytedeskRtpSessionFactory.RtpSession session,
            RtpCaptureReceiver captureReceiver,
            String bindHost,
            int localBindPort,
            int packetBufferSize) {
        if (session == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        if (captureReceiver == null) {
            throw new IllegalArgumentException("captureReceiver must not be null");
        }
        if (session.port() == null || session.port() <= 0) {
            throw new IllegalArgumentException("session port must be a positive integer");
        }
        if (packetBufferSize < 512) {
            throw new IllegalArgumentException("packetBufferSize must be at least 512 bytes");
        }

        this.session = session;
        this.captureReceiver = captureReceiver;
        this.packetBufferSize = packetBufferSize;
        int effectiveBindPort;
        if (localBindPort > 0) {
            effectiveBindPort = localBindPort;
        } else if (localBindPort == 0) {
            effectiveBindPort = 0;
        } else {
            effectiveBindPort = session.port() != null ? session.port() : 0;
        }
        this.datagramSocket = openSocket(resolveBindHost(bindHost), effectiveBindPort);
        this.expectedRemoteAddress = resolveExpectedRemoteAddress(session.host());
        this.expectedRemotePort = session.port() != null ? session.port() : 0;

        try {
            datagramSocket.setSoTimeout(DEFAULT_READ_TIMEOUT_MS);
        } catch (SocketException exception) {
            throw new IllegalStateException("Failed to configure RTP socket read timeout", exception);
        }
    }

    public int receiveNext() {
        if (closed) {
            return -1;
        }
        byte[] buffer = new byte[packetBufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            datagramSocket.receive(packet);
            if (!isExpectedRemote(packet)) {
                logUnexpectedRemote(packet);
            }
            captureReceiver.acceptPacket(packet.getData(), packet.getOffset(), packet.getLength());
            logAcceptedPacket(packet);
            return packet.getLength();
        } catch (SocketTimeoutException timeout) {
            return 0;
        } catch (IOException exception) {
            if (closed || datagramSocket.isClosed()) {
                return -1;
            }
            captureReceiver.fail(exception);
            throw new IllegalStateException("Failed to receive RTP datagram", exception);
        }
    }

    public void complete() {
        captureReceiver.complete();
    }

    public BytedeskRtpSessionFactory.RtpSession session() {
        return session;
    }

    public InetSocketAddress localAddress() {
        return new InetSocketAddress(datagramSocket.getLocalAddress(), datagramSocket.getLocalPort());
    }

    @Override
    public void close() {
        closed = true;
        datagramSocket.close();
    }

    private DatagramSocket openSocket(String bindHost, int port) {
        try {
            return new DatagramSocket(new InetSocketAddress(InetAddress.getByName(bindHost), port));
        } catch (SocketException exception) {
            throw new IllegalStateException("Failed to bind RTP UDP socket on " + bindHost + ":" + port, exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to resolve RTP bind host: " + bindHost, exception);
        }
    }

    private String resolveBindHost(String bindHost) {
        return bindHost == null || bindHost.isBlank() ? DEFAULT_BIND_HOST : bindHost.trim();
    }

    private InetAddress resolveExpectedRemoteAddress(String remoteHost) {
        if (remoteHost == null || remoteHost.isBlank()) {
            return null;
        }
        String normalized = remoteHost.trim();
        if (DEFAULT_BIND_HOST.equals(normalized) || "::".equals(normalized) || "localhost".equalsIgnoreCase(normalized)) {
            return null;
        }
        try {
            return InetAddress.getByName(normalized);
        } catch (UnknownHostException exception) {
            throw new IllegalStateException("Failed to resolve expected RTP remote host: " + normalized, exception);
        }
    }

    private boolean isExpectedRemote(DatagramPacket packet) {
        if (expectedRemoteAddress == null && expectedRemotePort <= 0) {
            return true;
        }
        InetAddress remoteAddress = packet.getAddress();
        if (remoteAddress == null) {
            return false;
        }
        boolean hostMatches = expectedRemoteAddress == null || expectedRemoteAddress.equals(remoteAddress);
        boolean portMatches = expectedRemotePort <= 0 || expectedRemotePort == packet.getPort();
        return hostMatches && portMatches;
    }

    private void logAcceptedPacket(DatagramPacket packet) {
        if (acceptedPacketLogCount >= 3) {
            return;
        }
        acceptedPacketLogCount++;
        log.info(
                "MRCP RTP packet accepted local={} remote={}:{} length={} expectedRemote={}:{}",
                localAddress(),
                packet.getAddress().getHostAddress(),
                packet.getPort(),
                packet.getLength(),
                expectedRemoteAddress != null ? expectedRemoteAddress.getHostAddress() : "any",
                expectedRemotePort > 0 ? expectedRemotePort : "any");
    }

    private void logUnexpectedRemote(DatagramPacket packet) {
        if (unexpectedRemoteLogCount >= 5) {
            return;
        }
        unexpectedRemoteLogCount++;
        log.info(
                "MRCP RTP packet source differs from SDP; accepting for local capture. local={} remote={}:{} length={} expectedRemote={}:{}",
                localAddress(),
                packet.getAddress().getHostAddress(),
                packet.getPort(),
                packet.getLength(),
                expectedRemoteAddress != null ? expectedRemoteAddress.getHostAddress() : "any",
                expectedRemotePort > 0 ? expectedRemotePort : "any");
    }
}
