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
package com.bytedesk.call.mrcp4j.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 * @param <E> the type of elements held in this queue
 * @param <T> the type of throwable that can be thrown by this queue
 *
 */
public class ThrowingQueue<E, T extends Throwable> {

    private BlockingQueue<Wrapper> _queue = new LinkedBlockingQueue<Wrapper>();
    
    public void put(E element) throws InterruptedException {
        Wrapper wrapper = new Wrapper(element);
        _queue.put(wrapper);
    }

    public void put(T e) throws InterruptedException {
        Wrapper wrapper = new Wrapper(e);
        _queue.put(wrapper);
    }

    public E take() throws T, InterruptedException {
        Wrapper wrapper = _queue.take();
        return wrapper.getElement();
    }
    
    private class Wrapper {
        
        private T _t;
        private E _element;

        /**
         * TODOC
         * @param element
         */
        public Wrapper(E element) {
            super();
            // TODO Auto-generated constructor stub
            _element = element;
        }

        /**
         * TODOC
         * @param t 
         */
        public Wrapper(T t) {
            _t = t;
        }

        /**
         * TODOC
         * @return Returns the element.
         * @throws T 
         */
        public E getElement() throws T {
            if (_t != null) {
                throw _t;
            }
            return _element;
        }
        
    }

}
