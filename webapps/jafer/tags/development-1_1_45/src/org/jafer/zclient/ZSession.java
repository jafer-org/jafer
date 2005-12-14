/**
 * JAFER Toolkit Project.
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
 *
 *
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.zclient;

import org.jafer.transport.ConnectionException;

import org.jafer.exception.JaferException;
import org.jafer.zclient.operations.*;
import org.jafer.transport.PDUDriver;
import org.jafer.record.TermRecord;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Vector;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;

import org.w3c.dom.Node;


/**
 * <p>Manages a zclient session and sets up connection with zserver using org.jafer.util.PDUDriver.
  * A session can be anonymous or if authentication is required by target, the client sets properties
  * for user/group/password. Changing these properties will terminate an existing session and establish a new one.</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class ZSession
    implements Session {

  private static Logger logger;
  private static int sessionId = 0;
  private PDUDriver pduDriver;
  private Socket socket;
  private String name, host, group, username, password, targetInfo = "Z39.50 server";
  private int port, timeout, targetVersion = 0;

  /**
   * double SOCKET_CONNECT_TIMEOUT
   * Timeout in milliseconds for the Socket.connect () function.
   * The default value for this on a Windows 2000 machine appears to be
   * approximately 23 seconds. If a host isn't responding for some
   * reason then a search will wait for the socket connect () to timeout.
   * Added by Ashley Sanders, University of Manchester, 9/10/2003.
   */

   private final static int SOCKET_CONNECT_TIMEOUT = 3000;

  /**
   * @todo: how do we handle preferredMessageSize and exceptionalRecordSize?
   **/

  public ZSession(String host, int port, int timeout) {

    this.logger = Logger.getLogger("org.jafer.zclient");
    this.sessionId++;
    this.host = host;
    this.port = port;
    this.timeout = timeout;
  }

  private void connect() throws ConnectionException {

    String message = "";
    InetSocketAddress sAdd = new InetSocketAddress(host, port);
    try {
      socket = new Socket();
      socket.connect(sAdd, SOCKET_CONNECT_TIMEOUT);
      setPDUDriver(new PDUDriver(getName(), socket, timeout));
      message = getName() + " connected to " + host + " on port " + port;
      logger.log(Level.FINE, message);
    } catch (java.net.UnknownHostException e) {
      message = getName() + " error starting session: Unknown host " + host;
      logger.log(Level.WARNING, message);
      throw new ConnectionException(message, e);
    } catch (java.lang.SecurityException e) {
      message = getName() + " error starting session: Security error " + "(" + e.toString() + ")";
      logger.log(Level.WARNING, message);
      throw new ConnectionException(message, e);
    } catch (java.lang.NullPointerException e) {
      message = getName() + " error starting session: host (" + host + ") or port (" + port + ") not found";
      logger.log(Level.WARNING, message);
      throw new ConnectionException(message, e);
    } catch (IOException e) {
      message = getName() + " error starting session: IOException (" + e.toString() + ")";
      logger.log(Level.WARNING, message);
      throw new ConnectionException(message, e);
    }
  }

  public void init(String group, String username, String password) throws ConnectionException {

    setName(group, username, password);
    connect();

    Init init = new Init(this);
    try {
      init.init(this.group, this.username, this.password);
      targetVersion = init.getTargetVersion();
      targetInfo = init.getTargetInfo();
      logger.log(Level.INFO, getName() + " established with host on port " + port + "\n" + targetInfo);
    } catch (ConnectionException e) {
      logger.log(Level.WARNING, getName() + " " + e.getMessage() + " - cannot connect to target " + init.getTargetInfo());
      throw e;
    }
  }

  public void close() {

    try {
      if (targetVersion > 2 || targetVersion == 0)
        pduDriver.initClose(0);
      if (socket != null)
        socket.close();
      logger.log(Level.INFO, getName() + " closed connection to " + targetInfo);
    } catch (Exception e) {
      logger.log(Level.WARNING, getName() + " error attempting to close session " + "(" + e.toString() + ")");
    }
  }

//  public int search(Node domQuery, String[] databases, String resultSetName)
//                                  throws JaferException, ConnectionException {
//
//    Search search = new Search(this);
//    return search.search(domQuery, databases, resultSetName);
//  }

  public int[] search(Object queryObject, String[] databases, String resultSetName)
                                  throws JaferException, ConnectionException {

    Search search = new Search(this);
    return search.search(queryObject, databases, resultSetName);
  }

  public  Vector present(int nRecord, int nRecords, int[] recordOID, String eSpec, String resultSetName)
                                      throws PresentException, ConnectionException {

    Present present = new Present(this);
    return present.present(nRecord, nRecords, recordOID, eSpec, resultSetName);
  }

  public Vector scan(String[] databases, int nTerms, int step, int position, Node term) throws JaferException, ConnectionException {

    Scan scan = new Scan(this);
    return scan.scan(databases, nTerms, step, position, term);
  }

  public Vector scan(String[] databases, int nTerms, int step, int position, Object termObject) throws JaferException, ConnectionException {

    Scan scan = new Scan(this);
    return scan.scan(databases, nTerms, step,  position, termObject);
  }

  public void setPDUDriver(PDUDriver pduDriver) {
    this.pduDriver = pduDriver;
  }

  public PDUDriver getPDUDriver() {
    return pduDriver;
  }

  private void setName(String group, String username, String password) {

    name = "session-" + sessionId + " [" ;

    if (group == null && username == null && password == null)
      name += "anonymous";
    else {
      if (group != null)
        name += group;
      if (username != null)
        name += "." + username;
      if (password != null)
        name += "." + "password";
    }
    name += "]" ;

    this.group = group;
    this.username = username;
    this.password = password;
  }

  public int getId() {

    return sessionId;
  }

  public String getName() {

    return name;
  }

  public String getGroup() {

    return group;
  }

  public String getUsername() {

    return username;
  }

  public String getPassword() {

    return password;
  }
}
/*
  public Session(Socket socket)  throws ConnectionException {

    this.logger = Logger.getLogger("org.jafer.zclient");
    this.socket = socket;
    this.closeReason = loadCloseReason(new Hashtable());
    try {
      timeout = socket.getSoTimeout();
      src = new BufferedInputStream(socket.getInputStream());
      dest = new BufferedOutputStream(socket.getOutputStream());
    }
    catch (java.io.IOException e1) {
      try {
        socket.close();
        } catch (java.io.IOException e2) {}
        socket = null;
        String message = "Error starting Session: IO error " + "(" + e1.toString() + ")";
        throw new ConnectionException(message, e1);
    }
  }
*/
