package org.jafer.test;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
import org.jafer.zclient.ZClient;
import org.jafer.query.QueryBuilder;

import org.w3c.dom.Node;

/**
 * Class for testing client and server. Uses a client to access a server running on port 211 at "127.0.0.1".<br/>
 * Default query:<br/>
 *   title = shindig AND (author = Wright OR author = Aldrich)<br/>
 *
 * A maximum of 5 results have name and title displayed.<br/>
 * There is an option to serialise records in an XML format to disk.<br/>
 *<br/>
 * Edit and re-compile to change server settings and other options.
 */

public class TestClient {

  public static void main(String[] args) {
    try {
    QueryBuilder builder = new QueryBuilder();
    Node a = builder.getNode("title", "shindig");
    Node b = builder.getNode(1003, "Wright");

    int[] searchProfile = {1003};  // int[] searchProfile = {1003,3,3,2,1};
    Node c = builder.getNode(searchProfile, "Aldrich");

    Node query = builder.and(a, builder.or(b, c));// title = shindig AND (author = Wright OR author = Aldrich)

    ZClient bean = new ZClient();
    bean.setHost("127.0.0.1");
    bean.setPort(211);
    bean.setDatabases("advance");
    bean.setAutoReconnect(0);
//    bean.setRecordSchema("http://www.openarchives.org/OAI/oai_marc");
    bean.setRecordSchema("http://www.loc.gov/mods/");

    int results = bean.submitQuery(query);
    System.out.println("results: "+results);


    results = (results > 5) ? 5 : results;
    String name = "", title = "";

    for (int i=1; i <= results; i++){
      bean.setRecordCursor(i);
      org.jafer.record.Field field = bean.getCurrentRecord();
//      org.jafer.util.xml.XMLSerializer.out(field.getXML(), "xml", "C:/record"+i+".xml");


      if (bean.getRecordSchema().equals("http://www.openarchives.org/OAI/oai_marc")) {
	// OAI record format:
	title = field.getFirst("varfield", "id", "245").getFirst("subfield").getValue();
	name = field.getFirst("varfield", "id", "100").getFirst("subfield").getValue();
      }
      if (bean.getRecordSchema().equals("http://www.loc.gov/mods/")) {
	// MODS record format:
	name = field.getFirst("name").getValue();
	title = field.getFirst("title").getValue();
//	String[] profile = {"role", "creator", "type", "personal"};
//	String name = field.getFirst("name", profile).getValue();
      }
      System.out.println("title: "+title);
      System.out.println("name: "+name);
    }

    bean.close();

    } catch (Exception e) {
      System.out.println("Exception in Test:");
      e.printStackTrace();
    }
  }
}