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
package com.bytedesk.call.mrcp4j.server.mina;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
@Slf4j
public class IoTextLoggingFilter extends IoFilterAdapter {

    /**
     * Name of the log used for logging session events.
     */
    public static final String SESSION_LOG_NAME = "com.bytedesk.call.mrcp4j.server.SESSION";

    /* (non-Javadoc)
     * @see org.apache.mina.io.IoFilter#sessionOpened(org.apache.mina.io.IoFilter.NextFilter,
     *      org.apache.mina.io.IoSession)
     */
    @Override
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
        log.debug("OPENED");
        nextFilter.sessionOpened(session);
    }

    /* (non-Javadoc)
     * @see org.apache.mina.io.IoFilter#sessionClosed(org.apache.mina.io.IoFilter.NextFilter,
     *      org.apache.mina.io.IoSession)
     */
    @Override
    public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
        log.debug("CLOSED");
        nextFilter.sessionClosed(session);
    }

    /* (non-Javadoc)
     * @see org.apache.mina.io.IoFilter#sessionIdle(org.apache.mina.io.IoFilter.NextFilter,
     *      org.apache.mina.io.IoSession, org.apache.mina.common.IdleStatus)
     */
    @Override
    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("IDLE: " + status);
        }
        nextFilter.sessionIdle(session, status);
    }

    /* (non-Javadoc)
     * @see org.apache.mina.io.IoFilter#exceptionCaught(org.apache.mina.io.IoFilter.NextFilter,
     *      org.apache.mina.io.IoSession, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
        log.warn("EXCEPTION: " + cause.getMessage() + '\n', cause);
        nextFilter.exceptionCaught(session, cause);
    }

    /* (non-Javadoc)
     * @see org.apache.mina.io.IoFilter#dataRead(org.apache.mina.io.IoFilter.NextFilter,
     *      org.apache.mina.io.IoSession, org.apache.mina.common.ByteBuffer)
     */
    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("READ:\n" + message);
        }
        nextFilter.messageReceived(session, message);
    }

    /* (non-Javadoc)
     * @see org.apache.mina.io.IoFilter#dataWritten(org.apache.mina.io.IoFilter.NextFilter,
     *      org.apache.mina.io.IoSession, java.lang.Object)
     */
    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("WRITTEN:\n" + writeRequest.getMessage());
        }
        nextFilter.messageSent(session, writeRequest);
    }

    /* (non-Javadoc)
     * @see org.apache.mina.io.IoFilter#filterWrite(org.apache.mina.io.IoFilter.NextFilter,
     *      org.apache.mina.io.IoSession, org.apache.mina.common.ByteBuffer,
     *      java.lang.Object)
     */
    @Override
    public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("WRITE:\n" + writeRequest.getMessage());
        }
        nextFilter.filterWrite(session, writeRequest);
    }
}
