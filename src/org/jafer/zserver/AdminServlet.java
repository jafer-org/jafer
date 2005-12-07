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
import org.jafer.util.xml.*;
import org.jafer.zserver.util.ContextListener;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.jafer.exception.*;

/**
 * <p>Provides servlet server management (via ZServerManager) and reports basic stats via xml/xslt</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class AdminServlet extends HttpServlet {

  private static Logger logger;
  private String remoteAddress;

  /**Initialize global variables*/
  public void init() throws ServletException {
    logger = Logger.getLogger("org.jafer.zserver");
    ZServerManager.startUp();
  }

  /**Process the HTTP Get request*/
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setContentType("text/html");
    setRemoteAddress(request.getRemoteAddr());
    parseRequest(request, response.getWriter());
  }

  private void parseRequest(HttpServletRequest request, PrintWriter out) {

    Enumeration paramNames = request.getParameterNames();

    String param;
    String[] value;

    while (paramNames.hasMoreElements()) {
      param = ((String)paramNames.nextElement());
      value = request.getParameterValues(param);
      if (ZServerManager.isStarted()) {
        if (param.equalsIgnoreCase("shutDown"))
          ZServerManager.shutDown(ZServerManager.STOP_TIMEOUT);
        else if (param.equalsIgnoreCase("loggerLevel"))
          setLoggerLevel(value);
        else if (param.equalsIgnoreCase("startServer"))
          startServer(value);
        else if (param.equalsIgnoreCase("stopServer"))
          stopServer(ZServerManager.STOP_TIMEOUT, value);
        else if (param.equalsIgnoreCase("killServer"))
          stopServer(ZServerManager.KILL_TIMEOUT, value);
        else if (param.equalsIgnoreCase("stopSession"))
          stopSession(ZServerManager.STOP_TIMEOUT, value);
        else if (param.equalsIgnoreCase("killSession"))
          stopSession(ZServerManager.KILL_TIMEOUT, value);
      } else { // !ZServerManager.isStarted()
        if (param.equalsIgnoreCase("startUp"))
          ZServerManager.startUp();
      }
    }
    getInfo(out);
  }

  private void getInfo(PrintWriter out) {

    Node info = null;
    if (ZServerManager.isStarted())
      info = ZServerManager.getManager().getInfo(DOMFactory.newDocument());
    else
      info = getXMLMessage("ZServerManager is not running");
    try {
      URL url = this.getClass().getClassLoader().getResource("org/jafer/xsl/server/info.xsl");
      XMLSerializer.transformOutput(info, url, out);
    } catch (JaferException ex) {
      logger.log(Level.WARNING, "AdminServlet - Error serializing output", ex);
    }
  }

  private Node getXMLMessage(String msg) {

    Document document = DOMFactory.newDocument();
    Node root = document.createElement("root");
    Node message = document.createElement("message");
    message.appendChild(document.createTextNode(msg));
    root.appendChild(message);
    return root;

  }

  private void setLoggerLevel(String[] value) {
    /** @todo not yet implemented in ZServerManager */
    try {
      ZServerManager.getManager().setLoggerLevel(Level.parse(value[0]));
    } catch (IllegalArgumentException ex) {
      logger.log(Level.WARNING, "not a valid Logging Level: " + value[0]);
    } catch (NullPointerException ex) {
      logger.log(Level.WARNING, "not a valid Logging Level: " + value[0]);
    }
  }

  private void startServer(String[] value) {

    if (isAll(value)) {
        ZServerManager.getManager().startServer();
        return;
    }

    for (int i = 0; i < value.length; i++) {
      try {
        ZServerManager.getManager().startServer(Integer.parseInt(value[i]));
      } catch (NumberFormatException ex) {
        logger.log(Level.WARNING, "not a valid server id: " + value[i]);
      }
    }
  }

  private void stopServer(long timeout, String[] value) {

    if (isAll(value)) {
        ZServerManager.getManager().stopServer(timeout);
        return;
    }

    for (int i = 0; i < value.length; i++) {
      try {
        ZServerManager.getManager().stopServer(timeout, Integer.parseInt(value[i]));
      } catch (NumberFormatException ex) {
        logger.log(Level.WARNING, "not a valid server id: " + value[i]);
      }
    }
  }


  private void stopSession(long timeout, String[] value) {

    if (isAll(value)) {
        ZServerManager.getManager().stopSession(timeout);
        return;
    }

    for (int i = 0; i < value.length; i++) {
      try {
        ZServerManager.getManager().stopSession(timeout, Integer.parseInt(value[i]));
      } catch (NumberFormatException ex) {
        logger.log(Level.WARNING, "not a valid session id: " + value[i]);
      }
    }
  }

  private boolean isAll(String[] value) {

    for (int i = 0; i < value.length; i++) {
      if (value[i].equals("0"))
        return true;
    }
    return false;
  }

  /**Clean up resources*/
  public void destroy() {}

  private void setRemoteAddress(String remoteAddress) {

    this.remoteAddress = remoteAddress;
  }

  public String getRemoteAddress() {

    return remoteAddress;
  }
}
