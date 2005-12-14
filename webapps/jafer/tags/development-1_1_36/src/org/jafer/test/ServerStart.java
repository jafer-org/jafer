package org.jafer.test;

import org.jafer.zserver.*;
import java.util.logging.Level;


/**
 * Test class for starting server/s without Servlet Admin inteface.<br/>
 * Uses settings in org.jafer.conf.server.xml
 */
public class ServerStart {

  public static void main(String[] args) {
    try {
      if (!ZServerManager.isStarted())
	ZServerManager.startUp();

//      ZServerManager.getManager().setLoggerLevel(Level.INFO);

    } catch (Exception e) {
      System.out.println("Exception caught in ServerTest main(): " + e.getMessage());
      e.printStackTrace();
    }
  }
}