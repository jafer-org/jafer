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

package org.jafer.zserver.util;

/**
 * <p>Used by org.jafer.zserver.Session so that server can operate in concurrent mode.
 * Checks if another thread has access to lock and waits if necessary - notify() used to wake thread when lock is available.
 * Interrupt method called for threads waiting on a databean that may have been deleted/replaced by another operation in a separate thread.</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
import java.util.*;

public class Lock {
  private Vector waiting;
  private Thread locks;
  private int locksCount;

  public Lock() {
    waiting = new Vector();
    locks = null;
    locksCount = 0;
  }

  public synchronized void getLock() throws Exception {
    while (tryGetLock() == false) {
      waiting.add(Thread.currentThread());
      System.out.println("waiting for lock");
      wait();
      System.out.println("got lock");
      waiting.remove(Thread.currentThread());
    }
  }

  private synchronized boolean tryGetLock() {
    if (locks == null) {
      locks = Thread.currentThread();
      locksCount = 1;
      return true;
    }
    if (locks == Thread.currentThread()) {
      locksCount++;
      return true;
    }
    return false;
  }

  public synchronized void freeLock(String id) {
    if (locks == Thread.currentThread()) {
      locksCount--;
      if (locksCount == 0) {
        locks = null;
        notify();
      }
    }
  }

  public int getWaitingCount() {
    return waiting.size();
  }

  public synchronized void interrupt() {
    Iterator it = waiting.iterator();
    while (it.hasNext())
      ((Thread)it.next()).interrupt();
  }
}