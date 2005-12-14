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

import org.jafer.util.ConnectionException;

import org.jafer.util.PDUDriver;
import org.jafer.zserver.util.Lock;
import org.jafer.zserver.operations.*;
import org.jafer.interfaces.Databean;
import org.jafer.interfaces.DatabeanFactory;
import org.jafer.interfaces.Authenticate;
import org.jafer.exception.JaferException;

import asn1.BEREncoding;
import z3950.v3.PDU;

import java.util.logging.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * <p>Sets up session and waits for request PDUs. Each new request is handled by relevant operation in a new thread.
 * Checks authentication and can operate in concurrent mode using org.jafer.zserver.util.Lock class</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class Session extends ZServerThread {

  private Socket socket;
  private DatabeanFactory databeanFactory;
  private PDUDriver pduDriver;
  private Authenticate authenticate;
  private Hashtable databeans = new Hashtable();
  private Hashtable locks = new Hashtable();
  private int preferredMessageSize;
  private int exceptionalRecordSize;
  private int clientVersion;
  private String clientInfo;
  private boolean concurrent;
  private boolean authenticated;

  /**
   * @todo: how do we handle preferredMessageSize and exceptionalRecordSize?
   */
  public Session(Socket socket, int timeout, DatabeanFactory databeanFactory, Authenticate authenticate) throws IOException {

    super("session");
    this.socket = socket;
    this.databeanFactory = databeanFactory;
    this.authenticate = authenticate;

    try {
      if (timeout < 0)
        timeout = ZServerManager.SESSION_TIMEOUT;
      setPDUDriver(new PDUDriver(getName(), socket, timeout));
      logger.log(Level.INFO, getName() + " established with " + socket.getInetAddress().getHostAddress() + " on port " + socket.getPort());
      /** @todo check host and port */
    } catch (IOException e) {
      logger.log(Level.WARNING, getName() + " Error starting session: IOException (" + e.toString() + ")");
      throw e;
    }
  }

  public void run() {

    setStopping(false);
    setStopped(false);
    setThreads(new Vector());
    setStartTime(System.currentTimeMillis()/1000);

    BEREncoding ber;
    PDU pduRequest = null;
/** @todo should we sleep or wait between loops? */
    while (!isStopping()) {
      try {
        pduRequest = getPDUDriver().getPDU();
        purgeThreads();
        if (pduRequest.c_initRequest != null)
          startThread(new Init(this, pduRequest));
        else if (pduRequest.c_searchRequest != null && getAuthenticated())
          startThread(new Search(this, pduRequest));
        else if (pduRequest.c_deleteResultSetRequest != null && getAuthenticated())
          startThread(new Delete(this, pduRequest));
        else if (pduRequest.c_presentRequest != null && getAuthenticated())
          startThread(new Present(this, pduRequest));
        else if (pduRequest.c_scanRequest != null && getAuthenticated())
          startThread(new Scan(this, pduRequest));
        else if (pduRequest.c_sortRequest != null && getAuthenticated())
          startThread(new Sort(this, pduRequest));
        else {
          if (pduRequest.c_close != null)
            getPDUDriver().respClose(pduRequest);
          else if (!getAuthenticated())
            /** should really send failure via op method! */
            getPDUDriver().initClose(5);
          else
            getPDUDriver().initClose(6);
          halt(ZServerManager.STOP_TIMEOUT);
        }
      } catch (ConnectionException ex) {
        logger.log(Level.INFO, getName() + " " + ex.getMessage());
        close();
      }
    }
  }

  public void close() {

    if (!isStopped()) {
      logger.log(Level.FINE, getName() + " close...");
      cleanUpDatabeans();
      try {
        if (socket != null)
          socket.close();
      } catch (IOException ex) {
        logger.log(Level.FINE, getName() + " socket already closed");
      } finally {
        socket = null;
        setStopping(true);
        setStopped(true);
        setThreads(null);
        logger.log(Level.INFO, getName() + " association closed");
        System.gc();
        Runtime.getRuntime().gc();
      }
    }
  }

  public int getPort() {
    return socket.getPort();
  }

  public int getLocalPort() {
    return socket.getLocalPort();
  }

  /** @todo check this - we want address of connecting client here */
  public String getClientAddress() {
    return socket.getInetAddress().getHostAddress();
  }

  public void setAuthenticated(String user, String group, String password) {

    if (authenticate == null)
      authenticated = true;
    else
      authenticated = authenticate.authenticate(user, group, password, getClientAddress());
  }

  public boolean getAuthenticated() {
    return authenticated;
  }

  public void setPreferredMessageSize(int preferredMessageSize) {
    this.preferredMessageSize = preferredMessageSize;
  }

  public int getPreferredMessageSize() {
    return preferredMessageSize;
  }

  public void setExceptionalRecordSize(int exceptionalRecordSize) {
    this.exceptionalRecordSize = exceptionalRecordSize;
  }

  public int getExceptionalRecordSize() {
    return exceptionalRecordSize;
  }

  public void setClientVersion(int clientVersion) {
    this.clientVersion = clientVersion;
  }

  public int getClientVersion() {
    return clientVersion;
  }

  public void setClientInfo(String clientInfo) {
    this.clientInfo = clientInfo;
  }

  public String getClientInfo() {
    return clientInfo;
  }

  public void setPDUDriver(PDUDriver pduDriver) {
    this.pduDriver = pduDriver;
  }

  public PDUDriver getPDUDriver() {
    return pduDriver;
  }

  public void setConcurrent(boolean concurrent) {
    this.concurrent = concurrent;
    if (concurrent)
      logger.log(Level.INFO, getName() + " running in concurrent mode");
  }

  public boolean isConcurrent() {
    return concurrent;
  }

  public boolean containsDatabean(String name) {
    return databeans.containsKey(name);
  }

  public Databean getDatabean() {
    return databeanFactory.getDatabean();
  }

  public void lockDatabean(String name) throws JaferException {

    if (isConcurrent()) {
      try {
        Lock lock = (Lock)locks.get(name);
        lock.getLock();
      } catch (Exception e) {
        throw new JaferException("Error obtaining lock for bean " + name + "; " + e.toString());
      }
    }
  }

  public void freeDatabean(String name) throws JaferException {

    if (isConcurrent()) {
      if (name == null || !locks.containsKey(name))
        return;
      try {
        Lock lock = (Lock)locks.get(name);
        lock.freeLock(name);
      } catch (Exception e) {
        throw new JaferException("Error releasing lock for bean " + name + "; " + e.toString());
      }
    }
   }

  public synchronized Object getDatabean(String name) {

    if (name == null || !databeans.containsKey(name))
      return null;
    return databeans.get(name);
  }

  public synchronized void setDatabean(String name, Object databean) throws JaferException {

    if (name == null || databean == null)
      return;

    removeDatabean(name);
    if (isConcurrent())
      locks.put(name, new Lock());
    databeans.put(name, databean);
  }

  public synchronized void removeDatabean(String name) throws JaferException {

    if (name == null || !databeans.containsKey(name))
      return;

    if (isConcurrent()) {
      lockDatabean(name);
      Lock lock = (Lock)locks.remove(name);
      lock.interrupt();
    }
    databeans.remove(name);
  }

  public void removeAllDatabeans() throws JaferException {

    if (isConcurrent()) {
      Enumeration keys = databeans.keys();
      while (keys.hasMoreElements())
        removeDatabean((String)keys.nextElement());
    } else
      databeans.clear();
  }


  private void cleanUpDatabeans(){

    if (isConcurrent()) {
      Enumeration keys = locks.keys();
      Lock lock = null;
      while (keys.hasMoreElements()) {
        lock = (Lock)locks.get((String)keys.nextElement());
        if (lock != null)
          lock.interrupt();
      }
    }
    locks.clear();
    databeans.clear();
    locks = null;
    databeans = null;
  }
}