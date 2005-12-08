package org.jafer.databeans;

import org.jafer.interfaces.Databean;
import org.jafer.zclient.ZClient;
import org.w3c.dom.Node;
import java.net.URL;
import org.jafer.exception.JaferException;
import org.jafer.zclient.AbstractClient;
import org.jafer.interfaces.DatabeanFactory;
import org.w3c.dom.Document;
import java.io.IOException;
import org.w3c.dom.Element;
import org.jafer.zclient.SRWClient;
import org.jafer.util.Config;
import org.jafer.util.xml.DOMFactory;
import java.util.regex.*;

public class ZurlFactory  extends DatabeanFactory {
   String url;
   private String recordSchema;

   public ZurlFactory(String url) {
     this.url = url;
   }

   private Databean createZ3950Bean() {
     String host  = "";
     String database = "";
     String portString = "";
     AbstractClient bean = new ZClient();
     Pattern p = Pattern.compile("z3950s://(.*):(\\d+)/(.*)");

     Matcher m = p.matcher(url);
     if (m.find() == true) {
       host = m.group(1);
       portString = m.group(2);
       database = m.group(3);
     }

     if (database == null) {
       database = "xxdefault";
     }
     int port = 210;
     try {
       port = Integer.parseInt("210");
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

   private Databean createSRWBean() {
     AbstractClient bean = new SRWClient();
     bean.setHost(url);
     bean.setTimeout(60000);
     bean.setFetchSize(15);
     bean.setAutoReconnect(3);
     bean.setRecordSchema(recordSchema);
     return bean;
   }

   public Databean getDatabean() {
     if (url.startsWith("http://") || url.startsWith("https://")) {
       return createSRWBean();
     } else if (url.startsWith("z3950s://")) {
       return createZ3950Bean();
     } else {
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
