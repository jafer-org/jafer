package org.jafer.test;

import org.jafer.util.*;
import org.jafer.util.xml.*;
import org.jafer.query.*;
import org.jafer.record.*;
import org.jafer.zclient.*;
import org.jafer.conf.*;
import org.jafer.zserver.*;
import org.jafer.zserver.authenticate.*;
import org.jafer.exception.JaferException;
import org.w3c.dom.*;

/**
 * Test class for testing server.<br/>
 * Starts server (using settings in org.jafer.conf.server.xml), and uses a client to do a search on "127.0.0.1".<br/>
 * First 5 results are serialized to: user directory/results.xml.<br/>
 * <br/>
 * Edit and re-compile to change server settings and other options.
 */

public class ServerTest {

  ZClient client;
  Node root;
  QueryBuilder queryBuilder;

  public ServerTest() {
    client = new ZClient();
    root = client.getDocument().createElement("root");
  }

  public void start() {
    try {
      // configure for server
      client.setHost("127.0.0.1");
      client.setDatabases("advance");
      client.setPort(211);

      // set some properties
      client.setRecordSchema("http://www.loc.gov/mods/");
//      client.setRecordSchema("http://www.openarchives.org/OAI/oai_marc");
      client.setFetchSize(1);
      client.setAutoReconnect(0);
      client.setCheckRecordFormat(true);

      // authentication eg. use server bound to port 213
      /*
      client.setUsername("username");
      client.setGroup("group");
      client.setPassword("password");
      */

      // build query
      queryBuilder = new QueryBuilder();
      Node query = queryBuilder.getNode("author", "smith");
      int nRes = client.submitQuery(query);
      System.out.println("Number or results = " + nRes);

      // loop through 5 records
      nRes = (nRes > 5) ? 5 : nRes;
      for (int i = 1; i <= nRes; i++) {
        try {
          client.setRecordCursor(i);
          Node xmlRecord = client.getCurrentRecord().getXML();
          root.appendChild(xmlRecord);
        } catch (org.jafer.record.RecordException e) {
          System.out.println("RecordException caught in ServerTest: " + e.getMessage());
          e.printStackTrace();
        }
      }

    } catch (org.jafer.exception.JaferException e) {
      System.out.println("JaferException caught in ServerTest: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("Exception caught in ServerTest: " + e.getMessage());
      e.printStackTrace();
    } finally {
      try {
        client.close();
        // results serialized to user directory/results.xml
        XMLSerializer.out(root, false, "results.xml");
      } catch (Exception e) {
        System.out.println("Exception caught in ServerTest: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public static void main (String argv[]) {
    try {
      if (!ZServerManager.isStarted())
        ZServerManager.startUp();
      ServerTest serverTest = new ServerTest();
      serverTest.start();
    } catch (Exception e) {
      System.out.println("Exception caught in ServerTest MAIN: " + e.getMessage());
      e.printStackTrace();
    }
  }
}