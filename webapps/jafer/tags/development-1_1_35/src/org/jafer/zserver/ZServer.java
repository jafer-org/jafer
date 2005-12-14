/**
 * JAFER Toolkit Poject.
 * Copyright (C) 2002, JAFER Toolkit Project, Oxford University.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jafer.zserver;

import org.jafer.interfaces.DatabeanFactory;
import org.jafer.interfaces.Authenticate;

import java.util.logging.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * <p>Creates serverSocket and waits for connections.
 * Each new connection is handled by a socket associated with a session in a new thread</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class ZServer extends ZServerThread implements Serializable {

  private int sessionTimeout;
  private int bindingPort;
  private ServerSocket serverSocket;
  private DatabeanFactory databeanFactory;
  private Authenticate authenticate;
  private String bindingAddress;
  private Thread thread;

  public ZServer() {

    super("server");
  }

  public void start() {

    try {
      logger.log(Level.INFO, "starting " + getName() + " bound to " + getBindingPort() + " on " + getBindingAddress() + " using " + getDatabeanFactory().getClass().getName());
      InetAddress address = InetAddress.getByName(getBindingAddress());
      InetSocketAddress socketaddress = new InetSocketAddress(address, getBindingPort());
      serverSocket = new ServerSocket();
      serverSocket.setSoTimeout(0);     // infinite timeout - no need to be interrupted
      serverSocket.setReuseAddress(true);   // allows the socket to be bound even though a previous connection is in a timeout state. Must be set before socket is bound
      serverSocket.bind(socketaddress, 50); // 50?

      thread = new Thread(this);
      thread.start();
    } catch (UnknownHostException ex) {
      logger.log(Level.SEVERE, getName() + " UnknownHostException", ex);
    } catch (SocketException ex) {
      logger.log(Level.SEVERE, getName() + " SocketException", ex);
    } catch (IOException ex) {
      logger.log(Level.SEVERE, getName() + " IOException", ex);
    }
  }

  public void run() {

    setStopping(false);
    setStopped(false);
    setThreads(new Vector());
    setStartTime(System.currentTimeMillis()/1000);

/** @todo should we sleep between loops? */
    while(!isStopping()) {
      try {
        purgeThreads();
        Socket socket = serverSocket.accept();
        logger.log(Level.FINE, getName() + " starting new session...");
        startThread(new Session(socket, getSessionTimeout(), getDatabeanFactory(), getAuthenticate()));
      } catch (InterruptedIOException ex) {
        logger.log(Level.INFO, getName() + " accept interrupted - continuing to wait for connections");
      } catch (SocketException ex) {
        logger.log(Level.INFO, getName() + " socket closed");
      } catch (IOException ex) {
        logger.log(Level.WARNING, getName() + " IOException (" + ex.toString() + ")");
        setStopping(true);
        close();// use halt()?
      }
    }
  }

  public void close() {

    if (!isStopped()) {
      logger.log(Level.FINE, getName() + " close...");
      try {
        if (serverSocket != null)
          serverSocket.close();
      } catch (IOException ex) {
        logger.log(Level.FINE, getName() + " socket already closed");
      } finally {
        setStopping(true);
        setStopped(true);
        serverSocket = null;
        setThreads(null);
        thread = null;
        logger.log(Level.INFO, getName() + " stopped");
        System.gc();
        Runtime.getRuntime().gc();
      }
    }
  }

  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }
  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
  }
  public void setDatabeanFactory(DatabeanFactory databeanFactory) {
    this.databeanFactory = databeanFactory;
  }
  public DatabeanFactory getDatabeanFactory() {
    return databeanFactory;
  }
  public void setAuthenticate(Authenticate authenticate) {
    this.authenticate = authenticate;
  }
  public Authenticate getAuthenticate() {
    return authenticate;
  }
  public void setBindingAddress(String bindingAddress) {
    this.bindingAddress = bindingAddress;
  }
  public String getBindingAddress() {
    return bindingAddress;
  }
  public void setBindingPort(int bindingPort) {
    this.bindingPort = bindingPort;
  }
  public int getBindingPort() {
    return bindingPort;
  }
  public void setSessionTimeout(int sessionTimeout) {
    this.sessionTimeout = sessionTimeout;
  }
  public int getSessionTimeout() {
    return sessionTimeout;
  }
}