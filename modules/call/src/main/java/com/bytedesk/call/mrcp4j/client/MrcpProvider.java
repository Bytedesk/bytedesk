/*
 * MRCP4J - Java API implementation of MRCPv2 specification
 *
 * Copyright (C) 2005-2006 SpeechForge - http://www.speechforge.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact: ngodfredsen@users.sourceforge.net
 *
 */
package com.bytedesk.call.mrcp4j.client;

import com.bytedesk.call.mrcp4j.message.header.IllegalValueException;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides functionality for simplified management of {@link com.bytedesk.call.mrcp4j.client.MrcpChannel} instances by an MRCPv2 client.
 *
 * <p>To construct a {@code MrcpProvider} instance use {@link com.bytedesk.call.mrcp4j.client.MrcpFactory#createProvider()}.</p>
 * 
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class MrcpProvider {

    /**
     * Transport protocol string for MRCPv2 over TCP.
     */
    public static final String PROTOCOL_TCP_MRCPv2 = "TCP/MRCPv2";

    /**
     * Transport protocol string for MRCPv2 over TLS over TCP (not yet supported in current version of MRCP4J).
     */
    public static final String PROTOCOL_TLS_MRCPv2 = "TCP/TLS/MRCPv2";

    /**
     * Compile time flag for setting whether to share {@link com.bytedesk.call.mrcp4j.client.MrcpSocket} instances.
     */
    private static final boolean SHARE_SOCKETS = true;

    private Map<String, MrcpSocket> _sockets = SHARE_SOCKETS ? new HashMap<String, MrcpSocket>() : null;

    MrcpProvider() {
        // restrict constructor to package visibility
    }

    /**
     * Constructs a new MRCP channel and initiates an active connection with the specified MRCP resource. 
     * @param channelID the channel ID for the channel being created.  This ID should be discovered
     *                  during the resource allocation phase which is mediated using SIP messages
     *                  between the client and server.
     * @param host      the location of the MRCP resource being accessed by the channel.
     * @param port      the port at which the MRCP resource is listening for MRCP messages.
     * @param protocol  the transport protocol being used to carry the MRCP messages (currently the only
     *                  supported value is {@link com.bytedesk.call.mrcp4j.client.MrcpProvider#PROTOCOL_TCP_MRCPv2}).
     * 
     * @return                           an active MRCP channel connected to the host and port specified
     * @throws IOException               if an I/O error occurs.
     * @throws IllegalArgumentException  if an unsupported protocol value is passed.
     * @throws IllegalValueException     if the channelID is not a valid value.
     */
    public MrcpChannel createChannel(String channelID, InetAddress host, int port, String protocol)
      throws IOException, IllegalArgumentException, IllegalValueException {

        // currently only TCP/MRCPv2 is supported
        // TODO: add support for TCP/TLS/MRCPv2
        if (!PROTOCOL_TCP_MRCPv2.equalsIgnoreCase(protocol)) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }

        MrcpSocket socket = getSocket(host, port, "tcp");
        MrcpChannel channel = new MrcpChannel(channelID, socket);
        return channel;
        // TODO: provide method to close channel
    }

    private MrcpSocket getSocket(InetAddress host, int port, String transport)
      throws IOException {
        if (!SHARE_SOCKETS) {
            return new MrcpSocket(host, port);
        }

        String key = getSocketKey(host, port, transport);

        synchronized (_sockets) {
            MrcpSocket socket = _sockets.get(key);
            if (socket == null) {
                //TODO: move socket initialization outside synchronization block
                socket = new MrcpSocket(host, port);
                _sockets.put(key, socket);
            }
            return socket;
        }
    }

    private static String getSocketKey(InetAddress host, int port, String transport) {
        StringBuilder key = new StringBuilder(host.getHostAddress());
        key.append(':').append(port);
        key.append('/').append(transport.toLowerCase());
        return key.toString();
    }


}