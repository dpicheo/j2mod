/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.j2mod.modbus.net;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.util.ModbusLogger;
import com.j2mod.modbus.util.ThreadPool;

import java.io.IOException;
import java.net.*;

/**
 * Class that implements a ModbusTCPListener.
 *
 * <p>
 * If listening, it accepts incoming requests passing them on to be handled.
 * If not listening, silently drops the requests.
 *
 * @author Dieter Wimberger
 * @author Julie Haugh
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class ModbusTCPListener implements ModbusListener {

    private static final ModbusLogger logger = ModbusLogger.getLogger(ModbusTCPListener.class);

    private ServerSocket m_ServerSocket = null;
    private ThreadPool m_ThreadPool;
    private Thread m_Listener;
    private int m_Port = Modbus.DEFAULT_PORT;
    private int m_Unit = 0;
    private boolean m_Listening;
    private InetAddress m_Address;

    /**
     * Constructs a ModbusTCPListener instance.<br>
     *
     * @param poolsize the size of the <tt>ThreadPool</tt> used to handle incoming
     *                 requests.
     * @param addr     the interface to use for listening.
     */
    public ModbusTCPListener(int poolsize, InetAddress addr) {
        m_ThreadPool = new ThreadPool(poolsize);
        m_Address = addr;
    }

    /**
     * /**
     * Constructs a ModbusTCPListener instance.  This interface is created
     * to listen on the wildcard address, which will accept TCP packets
     * on all available interfaces.
     *
     * @param poolsize the size of the <tt>ThreadPool</tt> used to handle incoming
     *                 requests.
     */
    public ModbusTCPListener(int poolsize) {
        m_ThreadPool = new ThreadPool(poolsize);
        try {
            /*
			 * TODO -- Check for an IPv6 interface and listen on that
			 * interface if it exists.
			 */
            m_Address = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
        }
        catch (UnknownHostException ex) {
            // Can't happen -- size is fixed.
        }
    }

    /**
     * Sets the port to be listened to.
     *
     * @param port the number of the IP port as <tt>int</tt>.
     */
    public void setPort(int port) {
        m_Port = port;
    }

    /**
     * Sets the address of the interface to be listened to.
     *
     * @param addr an <tt>InetAddress</tt> instance.
     */
    public void setAddress(InetAddress addr) {
        m_Address = addr;
    }

    /**
     * Starts this <tt>ModbusTCPListener</tt>.
     *
     * @deprecated
     */
    public void start() {
        m_Listening = true;

        m_Listener = new Thread(this);
        m_Listener.start();
    }

    /**
     * Accepts incoming connections and handles then with
     * <tt>TCPConnectionHandler</tt> instances.
     */
    public void run() {
        try {
            /*
             * A server socket is opened with a connectivity queue of a size
			 * specified in int floodProtection. Concurrent login handling under
			 * normal circumstances should be allright, denial of service
			 * attacks via massive parallel program logins can probably be
			 * prevented.
			 */
            int m_FloodProtection = 5;
            m_ServerSocket = new ServerSocket(m_Port, m_FloodProtection, m_Address);
            logger.debug("Listening to %s (Port %d)", m_ServerSocket.toString(), m_Port);

			/*
             * Infinite loop, taking care of resources in case of a lot of
			 * parallel logins
			 */

            m_Listening = true;
            while (m_Listening) {
                Socket incoming = m_ServerSocket.accept();
                logger.debug("Making new connection %s", incoming.toString());

                if (m_Listening) {
                    // FIXME: Replace with object pool due to resource issues
                    m_ThreadPool.execute(new TCPConnectionHandler(new TCPSlaveConnection(incoming)));
                }
                else {
                    incoming.close();
                }
            }
        }
        catch (SocketException iex) {
            if (m_Listening) {
                logger.debug(iex);
            }
        }
        catch (IOException e) {
            // FIXME: this is a major failure, how do we handle this
        }
    }

    /**
     * Gets the unit number supported by this Modbus/TCP connection. A
     * Modbus/TCP connection, by default, supports unit 0, but may also support
     * a fixed unit number, or a range of unit numbers if the device is a
     * Modbus/TCP gateway.  If the unit number is non-zero, all packets for
     * any other unit number should be discarded.
     *
     * @return unit number supported by this interface.
     */
    public int getUnit() {
        return m_Unit;
    }

    /**
     * Sets the unit number to be listened for.  A Modbus/TCP connection, by
     * default, supports unit 0, but may also support a fixed unit number, or a
     * range of unit numbers if the device is a Modbus/TCP gateway.
     *
     * @param unit the number of the Modbus unit as <tt>int</tt>.
     */
    public void setUnit(int unit) {
        m_Unit = unit;
    }

    /**
     * Tests if this <tt>ModbusTCPListener</tt> is listening and accepting
     * incoming connections.
     *
     * @return true if listening (and accepting incoming connections), false
     * otherwise.
     */
    public boolean isListening() {
        return m_Listening;
    }

    /**
     * Set the listening state of this <tt>ModbusTCPListener</tt> object.
     * A <tt>ModbusTCPListener</tt> will silently drop any requests if the
     * listening state is set to <tt>false</tt>.
     *
     * @param b
     */
    public void setListening(boolean b) {
        m_Listening = b;
    }

    /**
     * Start the listener thread for this serial interface.
     */
    public Thread listen() {
        m_Listening = true;
        Thread result = new Thread(this);
        result.start();

        return result;
    }

    /**
     * Stops this <tt>ModbusTCPListener</tt>.
     */
    public void stop() {
        m_Listening = false;
        try {
            if (m_ServerSocket != null) {
                m_ServerSocket.close();
            }
            if (m_Listener != null) {
                m_Listener.join();
            }
        }
        catch (Exception ex) {
            // ?
        }
    }

}
