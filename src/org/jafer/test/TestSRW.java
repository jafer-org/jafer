package org.jafer.test;

import org.jafer.zclient.SRWClient;
import org.jafer.query.QueryBuilder;
import org.w3c.dom.Node;

public class TestSRW {
  public static void main(String[] args) {
    try {
      QueryBuilder builder = new QueryBuilder();
      Node query = builder.getNode("author", "smith");
      SRWClient bean = new SRWClient();
      bean.setHost("http://alcme.oclc.org/srw/search/SOAR");
//      bean.setPort(211);
//      bean.setDatabases("advance");
      bean.setAutoReconnect(0);

      int results = bean.submitQuery(query);
      System.out.println("Records = " + results);

      for (int i=1; i <= results; i++) {
        bean.setRecordCursor(i);
        bean.getCurrentRecord();
      }


    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
