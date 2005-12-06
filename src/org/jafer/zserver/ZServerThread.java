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

import java.util.logging.*;
import java.util.*;
import java.io.*;
import java.net.*;

import asn1.ASN1Exception;
import z3950.v3.PDU;

/**
 * <p>Enables thread management (live threads stored in vector) and recursive halt of childThreads</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public abstract class ZServerThread extends Thread implements Serializable {

  public static int STATE_RUNNING  = 0;
  public static int STATE_STOPPED  = 1;
  public static int STATE_STOPPING = 2;

  private volatile boolean stopping = true;
  private volatile boolean stopped = true;
  public static Logger logger;
  private Vector threads;
  private int state;
  private long startTime;

  public ZServerThread(String name) {

    logger = Logger.getLogger("org.jafer.zserver");
    setThreadName(name);
  }

  public void setStartTime(long startTime) {

    this.startTime = startTime;
  }

  public long getStartTime() {

    return startTime;
  }

  public void setStopping(boolean stopping) {

    this.stopping = stopping;
  }

  public boolean isStopping() {

    return this.stopping;
  }

  public void setStopped(boolean stopped) {

    this.stopped = stopped;
  }

  public boolean isStopped() {

    return this.stopped;
  }

  public void setThreads(Vector threads) {

    this.threads = threads;
  }

  public Vector getThreads() {

    return this.threads;
  }

  public boolean hasThreads() {

    return !(threads == null);
  }

  public Vector purgeThreads() {

    if (threads == null)
      return null;
    Enumeration en = threads.elements();
    while (en.hasMoreElements()) {
      Thread thread = (Thread)en.nextElement();
      if (!thread.isAlive())
        threads.remove(thread);
    }
    return threads;
  }

  public void startThread(Thread thread) {

    thread.start();
    threads.add(thread);
  }

  public void setThreadName(String name) {

    setName(name + getName().substring(getName().indexOf('-')));
  }

  public int getServerThreadId() {

    return Integer.parseInt(getName().substring(getName().indexOf('-') + 1));
  }

  public void halt(long timeout) {

    logger.log(Level.FINE, getName() + " halt..." );
    setStopping(true);

    if (hasThreads()) {
      Enumeration en = purgeThreads().elements();
      while (en.hasMoreElements()) {
        ZServerThread zThread = (ZServerThread)en.nextElement();
        if (zThread.isAlive())
          zThread.halt(timeout);
      }
      waitForThreads(timeout);
      close();
    }
  }

  private void waitForThreads(long timeout) {

    if (hasThreads()) {
      Enumeration en = purgeThreads().elements();
      logger.log(Level.FINE, getName() + " active child threads: " + threads.size());
      while (en.hasMoreElements()) {
        ZServerThread zThread = (ZServerThread)en.nextElement();
        if (zThread.isAlive()) {
          logger.log(Level.FINE, getName() + " waiting for child " + zThread.getName() + " to die (timeout " + timeout + ")");
          try {
            zThread.join(timeout);
          } catch (InterruptedException ex) {
            logger.log(Level.WARNING, getName() + " InterruptedException " + ex.toString());
          }
        }
        if (!zThread.isAlive())
          logger.log(Level.FINE, getName() + " child " + zThread.getName() + " is dead");
      }
    }
  }

  public void close() {}

  public int getNumberOfActiveThreads() {
    if (purgeThreads() == null)
      return 0;
    return threads.size();
  }

  public Vector getActiveThreads() {
    return purgeThreads();
  }

  public long getUpTime() {
    if (isStopping())
      return 0;
    return System.currentTimeMillis()/1000 - startTime;
  }

  public int getServerThreadState() {
    if (isStopped())
      return STATE_STOPPED;
    if (isStopping())
      return STATE_STOPPING;
    return STATE_RUNNING;
  }
}

/*
  public static int STATE_ACTIVE = 0;
  public static int STATE_PAUSED = 1;
  public static int STATE_HUNG  = 2;


  public int getState() {
    return STATE_ACTIVE;
  }
  public boolean Pause() {
    return false;
  }
  public boolean Resume() {
    return false;
  }
  public boolean Kill() {
    return false;
  }
  public String getClient() {
    return null;
  }
  public int getConnectionTime() {
    return 0;
  }
  public Operation[] getActiveOperations() {
    return null;
  }
*/
/*
  public static int STATE_RUNNING  = 0;
  public static int STATE_STOPPED  = 1;
  public static int STATE_PAUSED   = 2;
  public static int STATE_STOPPING = 4;



  public int getState() {
    return STATE_RUNNING;
  }

  public boolean Start() {
    this.start();

    return true;
  }

  public boolean Pause() {
    return false;
  }

  public boolean Stop() {
    return false;
  }

  public boolean Restart() {
    return false;
  }

  public int getUpTime() {
    return 0;
  }

  public Session[] getActiveSessions() {
    return null;
  }
 */
