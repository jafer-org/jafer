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
 *
 */

package org.jafer.zserver.util;

import org.jafer.zserver.ZServerManager;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>Allows automatic startup/shutdown of ZServerManager and servers by reference to listener in web.xml.
 * Tested with Tomcat 4 only; relevant for Servlet/JSP Spec 2.3/1.2 or higher.</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */

public final class ContextListener implements ServletContextListener {

  private static Logger logger;
  private static ServletContext context = null;
  private static boolean contextInitialized = false;

  public ContextListener() {

    logger = Logger.getLogger("org.jafer.zserver");
  }

  /**
   * Record the fact that this web application has been destroyed.
   *
   * @param event The servlet context event
   */
  public void contextDestroyed(ServletContextEvent event) {

    logger.log(Level.INFO, "ZServer Context destroyed");

    if (ZServerManager.isStarted())
      ZServerManager.shutDown(ZServerManager.STOP_TIMEOUT);
    setContextInitialized(false);
    setContext(null);
  }

  /**
   * Record the fact that this web application has been initialized.
   *
   * @param event The servlet context event
   */
  public void contextInitialized(ServletContextEvent event) {

    logger.log(Level.INFO, "ZServer Context initialized");

    setContextInitialized(true);
    setContext(event.getServletContext());
    if (!ZServerManager.isStarted())
      ZServerManager.startUp();
  }

  private void setContext(ServletContext servletContext) {

    context = servletContext;
  }

  public static ServletContext getContext() {

    return context;
  }

  private void setContextInitialized(boolean state) {

    contextInitialized = state;
  }

  public static boolean isContextInitialized() {

    return contextInitialized;
  }
}