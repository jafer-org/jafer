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
 *
 */

package org.jafer.zserver;

import org.jafer.conf.Config;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.xml.XMLSerializer;
import org.jafer.util.xml.XMLTransformer;
import org.jafer.exception.JaferException;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

import org.w3c.dom.*;

/**
 * <p>Sets up and manages servers (configured via org.jafer.conf.server.xml).
 * Includes methods to stop/start servers and sessions.</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class ZServerManager {

  public static final long STOP_TIMEOUT = 10000;
  public static final long KILL_TIMEOUT = 1;
  public static final int SESSION_TIMEOUT = 900000;

  private ZServer[] servers = null;
  private static Logger logger;
  private static ZServerManager zServerManager;

  public ZServerManager() {

    logger = Logger.getLogger("org.jafer.zserver");
  }

  public void startServer() {

    if (servers == null)
      logger.log(Level.WARNING, "ZServerManager not started");
    else {
      for (int i = 0; i < servers.length; i++) {
        if (servers[i].isStopped())
          servers[i].start();
        else
          logger.log(Level.INFO, servers[i].getName() + " is already running");
      }
    }
  }

  public void startServer(int n) {

    if (servers == null)
      logger.log(Level.WARNING, "ZServerManager not started");
    else {
      for (int i = 0; i < servers.length; i++) {
        if (servers[i].getId() == n) {
          if (servers[i].isStopped())
            servers[i].start();
          else
            logger.log(Level.INFO, servers[i].getName() + " is already running");
          break;
        }
      }
    }
  }

  public void stopServer(long timeout) {

    if (servers == null)
      logger.log(Level.WARNING, "ZServerManager not started");
    else {
      for (int i = 0; i < servers.length; i++) {
        if (servers[i].isStopped())
          logger.log(Level.INFO, servers[i].getName() + " has stopped");
        else
          servers[i].halt(timeout);
      }
    }
  }

  public void stopServer(long timeout, int n) {

    if (servers == null)
      logger.log(Level.WARNING, "ZServerManager not started");
    else {
      for (int i = 0; i < servers.length; i++) {
        if (servers[i].getId() == n) {
          if (servers[i].isStopped())
            logger.log(Level.INFO, servers[i].getName() + " has stopped");
          else
            servers[i].halt(timeout);
          break;
        }
      }
    }
  }

  public void reStartServer(long timeout) {

    stopServer(timeout);
    startServer();
  }

  public void stopSession(long timeout) {

    if (servers == null)
      logger.log(Level.WARNING, "ZServerManager not started");
    else {
      for (int i = 0; i < servers.length; i++) {
        if (servers[i].hasThreads()) {
          Vector sessions = servers[i].purgeThreads();
          for (int j = 0; j < sessions.size(); j++) {
            ((ZServerThread)sessions.get(j)).halt(timeout);
          }
        }
      }
    }
  }

  public void stopSession(long timeout, int n) {

    if (servers == null)
      logger.log(Level.WARNING, "ZServerManager not started");
    else {
      for (int i = 0; i < servers.length; i++) {
        if (servers[i].hasThreads()) {
          Vector sessions = servers[i].purgeThreads();
          for (int j = 0; j < sessions.size(); j++) {
            if (((ZServerThread)sessions.get(j)).getId() == n) {
              ((ZServerThread)sessions.get(j)).halt(timeout);
            }
          }
        }
      }
    }
  }

  public void stopSession() {

    stopSession(STOP_TIMEOUT);
  }

  public void stopSession(int n) {

    stopSession(STOP_TIMEOUT, n);
  }

  public void killSession() {

    stopSession(KILL_TIMEOUT);
  }

  public void killSession(int n) {

    stopSession(KILL_TIMEOUT, n);
  }

  public static void shutDown() {

    shutDown(STOP_TIMEOUT);
  }

  public Node getInfo(Document document) {

    Node serverNode = null, sessionNode = null;
    Node root = document.createElement("root");
    if (servers == null)
      logger.log(Level.WARNING, "ZServerManager not started");
    else {
      for (int i = 0; i < servers.length; i++) {
        serverNode = root.appendChild(getInfo(servers[i], document));
        appendSocketInfo(servers[i], document, serverNode);
        if (servers[i].hasThreads()) {
          Vector sessions = servers[i].purgeThreads();
          for (int j = 0; j < sessions.size(); j++) {
            if (!((ZServerThread)sessions.get(j)).isStopped()) {
              sessionNode = serverNode.appendChild(getInfo(((ZServerThread)sessions.get(j)), document));
              appendSocketInfo(servers[i], document, sessionNode);
            }
          }
        }
      }
    }
    return root;
  }

  public void setLoggerLevel(Level level) {
    /** @todo implement this when logging has been sorted out */
  }

  private Node getInfo(ZServerThread zServerThread, Document document) {

    Element root = document.createElement("thread");
    root.setAttribute("id", String.valueOf(zServerThread.getId()));
    Node upTime = root.appendChild(document.createElement("upTime"));
    upTime.appendChild(document.createTextNode(String.valueOf(zServerThread.getUpTime())));
    Node state = root.appendChild(document.createElement("state"));
    state.appendChild(document.createTextNode(String.valueOf(zServerThread.getState())));
    Node threads = root.appendChild(document.createElement("threads"));
    threads.appendChild(document.createTextNode(String.valueOf(zServerThread.getNumberOfActiveThreads())));
    return root;
  }

  private void appendSocketInfo(ZServer zServer, Document document, Node serverNode) {

    Node port = serverNode.appendChild(document.createElement("port"));
    port.appendChild(document.createTextNode(String.valueOf(zServer.getBindingPort())));
    Node address = serverNode.appendChild(document.createElement("address"));
    address.appendChild(document.createTextNode(zServer.getBindingAddress()));
  }

  private void appendSocketInfo(Session session, Document document, Node sessionNode) {

    Node port = sessionNode.appendChild(document.createElement("port"));
    port.appendChild(document.createTextNode(String.valueOf(session.getPort())));
    Node address = sessionNode.appendChild(document.createElement("address"));
    address.appendChild(document.createTextNode(session.getClientAddress()));
  }

  public void save(String path) throws JaferException {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLEncoder toXml = new XMLEncoder(out);
    toXml.writeObject(this);
    toXml.flush();
    toXml.close();
    Document xml = DOMFactory.parse(new ByteArrayInputStream(out.toByteArray()));
    XMLSerializer.out(
        XMLTransformer.transform(xml.getFirstChild(), Config.getServerDecode()), "xml", path);
  }

  private void loadManager(Document document) throws JaferException {

    logger.log(Level.INFO, "JAFER Server Manager 1.00 copyright 2002, JAFER Project (http://www.jafer.org)");

    Node xmlIn = document.getDocumentElement();
    Node xml = XMLTransformer.transform(xmlIn, Config.getServerDecode());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLSerializer.out(xml, "xml", out);
    XMLDecoder fromXml = new XMLDecoder(new ByteArrayInputStream(out.toByteArray()));

    servers = (ZServer[])fromXml.readObject();
  }

  public static ZServerManager startUp() {

    try {
      setManager(new ZServerManager());
      zServerManager.loadManager(Config.getServerConfigDocument());
      zServerManager.startServer();
      return zServerManager;
    } catch (JaferException ex) {
      String message = "Cannot load ZServerManager: " + ex.toString();
      logger.log(Level.SEVERE, message, ex);
      System.err.print("FATAL: " + message);
      ex.printStackTrace(System.err);
      System.exit(-1);
      return null;
    }
  }

  public static void shutDown(long timeout) {

    if (zServerManager != null) {
      getManager().stopServer(timeout);
      setManager(null);
    }

    System.gc();
    Runtime.getRuntime().gc();
  }

  public static boolean isStarted() {

    return !(zServerManager == null);
  }

  public static ZServerManager getManager() {

    if (zServerManager == null)
      logger.log(Level.WARNING, "ZServerManager not started; use ZServerManager startUp()");
    return zServerManager;
  }

  private static void setManager(ZServerManager mgr) {

    zServerManager = mgr;
  }
}