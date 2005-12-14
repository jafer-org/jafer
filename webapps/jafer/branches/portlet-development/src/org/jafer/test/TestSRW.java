package org.jafer.test;

import org.jafer.zclient.SRWClient;
import org.jafer.query.QueryBuilder;
import org.jafer.record.Field;
import org.jafer.exception.JaferException;
import org.w3c.dom.*;

public class TestSRW {
  public static void main(String[] args) {
    try {
      QueryBuilder builder = new QueryBuilder();
      Node query = builder.getNode("author", "smith");
      SRWClient bean = new SRWClient();
      bean.setHost("http://repository.ust.hk/SRW/search/DSpace");
//      bean.setHost("http://z3950.loc.gov:7090/voyager");
//      bean.setHost("http://alcme.oclc.org/srw/search/SOAR");// DC
//      bean.setHost("http://alcme.oclc.org:80/srw/search/GSAFD"); // MARCXML
//      bean.setHost("http://srw.cheshire3.org:8080/l5r/");
//      bean.setHost("http://www.indexdata.dk:9000/voyager");
//      bean.setHost("http://www.rdn.ac.uk:8080/xxdefault");
//      bean.setHost("http://tweed.lib.ed.ac.uk:8080/elf/search/oxford");
//      bean.setPort(211);
//      bean.setDatabases("advance");
      bean.setAutoReconnect(5);
//      bean.setRecordSchema("http://www.openarchives.org/OAI/oai_marc");
      bean.setRecordSchema("http://www.loc.gov/mods/");
//      bean.setRecordSchema("http://www.loc.gov/MARC21/slim");
//      bean.setRecordSchema("http://purl.org/dc/elements/1.1/");
//      bean.setRecordSchema("http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0");
//      bean.setRecordSchema("http://ltsc.ieee.org/xsd/LOM");
//      bean.setCheckRecordFormat(true);

      int results = bean.submitQuery(query);
      System.out.println("Records = " + results);

      Field field;
      Document doc = bean.getDocument();
      Node records = doc.createElement("records");// for saving records to file

      String name = "lookup failed", title = "lookup failed";

      for (int i=1; i <= results && i <= 2; i++) {
        bean.setRecordCursor(i);
        field = bean.getCurrentRecord();

//        records.appendChild(field.getXML());
        org.jafer.util.xml.XMLSerializer.out(field.getXML(), "xml", "C:/SRWRecord"+i+".xml");

        String schema = field.getRecordSchema();

        if (schema.equals("http://www.jafer.org/formats/xml"))
          // XML record format: what to do? Here's a hack:
          schema = field.getXML().getNamespaceURI();

        if (schema.equals("http://www.loc.gov/mods/")) {
          // MODS record format:
          name = field.getFirst("name").getValue();
          title = field.getFirst("title").getValue();
        }
        else if (schema.equals("http://purl.org/dc/elements/1.1/")) {
          // Dublin Core record format:
          name = field.getFirst("creator").getValue();
          title = field.getFirst("title").getValue();
        }
        else if (schema.equals("http://www.openarchives.org/OAI/oai_marc")) {
          // OAI Marc
          title = field.getFirst("varfield", "id", "245").getFirst("subfield").getValue();
          name = field.getFirst("varfield", "id", "700").getFirst("subfield").getValue();
        }
        else if (schema.equals("http://www.loc.gov/MARC21/slim")) {
          // MarcXML (i.e. Marc21Slim)
          title = field.getFirst("datafield", "tag", "245").getFirst("subfield").getValue();
          name = field.getFirst("datafield", "tag", "100").getFirst("subfield").getValue();
          if (name.equals(""))
            name = field.getFirst("datafield", "tag", "700").getFirst("subfield").getValue();// OCLC
        }
        else if (schema.equals("http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0")) {
          title = field.getFirst("title").getValue();
          name = field.getFirst("creator").getValue();
        }
        System.out.println("Name: " + name);
        System.out.println("Title: " + title);
      }

      org.jafer.util.xml.XMLSerializer.out(records, "xml", "C:/SRWRecords.xml");
      bean.close();

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
