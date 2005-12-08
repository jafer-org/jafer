package org.jafer.databeans;

import java.net.*;

import org.jafer.conf.*;
import org.jafer.exception.*;
import org.jafer.interfaces.*;
import org.jafer.util.xml.*;
import org.jafer.zclient.*;
import org.w3c.dom.*;
import java.io.*;
import org.jafer.util.*;

public class ZeerexFactory extends DatabeanFactory {
  URL url;
  private String recordSchema;

  public ZeerexFactory(URL url) {
    this.url = url;
  }

  private Databean createZ3950Bean(Node zeerex) {
    Node hostNode = null;
    Node portNode = null;
    Node databaseNode = null;
    try {
      hostNode = Config.selectSingleNode(zeerex, "explain/serverInfo/host");
      portNode = Config.selectSingleNode(zeerex, "explain/serverInfo/port");
      databaseNode = Config.selectSingleNode(zeerex,
                                             "explain/serverInfo/database");
    }
    catch (JaferException ex1) {
      return null;
    }
    AbstractClient bean = new ZClient();
    String host  = hostNode.getNodeValue();
    String database = databaseNode.getNodeValue();
    if (database == null) {
      database = "xxdefault";
    }
    int port = 210;
    try {
      port = Integer.parseInt(portNode.getNodeValue());
    } catch (Exception ex) {
    }
    bean.setHost(host);
    bean.setDatabases(database);
    bean.setPort(port);
    bean.setTimeout(60000);
    bean.setFetchSize(15);
    bean.setAutoReconnect(3);
    bean.setRecordSchema(recordSchema);
    return bean;
  }

  private Databean createSRWBean(Node zeerex) {
    Node hostNode = null;
    Node portNode = null;
    Node databaseNode = null;
    try {
      hostNode = Config.selectSingleNode(zeerex, "explain/serverInfo/host");
      portNode = Config.selectSingleNode(zeerex, "explain/serverInfo/port");
      databaseNode = Config.selectSingleNode(zeerex,
                                             "explain/serverInfo/database");
    }
    catch (JaferException ex1) {
      return null;
    }
    AbstractClient bean = new SRWClient();
    String host  = hostNode.getNodeValue();
    String database = databaseNode.getNodeValue();
    if (database == null) {
      database = "xxdefault";
    }
    int port = 80;
    try {
      port = Integer.parseInt(portNode.getNodeValue());
    } catch (Exception ex) {
    }

    String portString = "";
    String protocolString = "http://";
    if (port ==  443) {
      protocolString = "https://";
    } else if (port != 80) {
      portString = ":" + port;
    }

    bean.setHost(protocolString + host + port + "/" + database);
    bean.setTimeout(60000);
    bean.setFetchSize(15);
    bean.setAutoReconnect(3);
    bean.setRecordSchema(recordSchema);
    return bean;
  }

  public Databean getDatabean() {
    try {
        Document doc = DOMFactory.parse( url.openStream());

        Node zeerex = Config.selectSingleNode(doc, "//explain");
        if ( ( (Element) Config.selectSingleNode(zeerex, "explain/serverInfo")).
            getAttribute("protocol").startsWith("SRW")) {
          return this.createSRWBean(zeerex);
        }
        else {
          return this.createZ3950Bean(zeerex);
        }
      }
      catch (IOException ex1) {
        return null;
      }
      catch (JaferException ex1) {
        return null;
      }
  }

  public void setRecordSchema(String recordSchema) {
    this.recordSchema = recordSchema;
  }

  public String getRecordSchema() {
    return recordSchema;
  }

}
