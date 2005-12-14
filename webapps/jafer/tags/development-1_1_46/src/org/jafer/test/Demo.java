/**
 * JAFER Toolkit Project.
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

/**
 * Title:        JAFER Toolkit
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Oxford University
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */

package org.jafer.test;

import org.jafer.zclient.ZClient;
import org.jafer.exception.JaferException;
import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.jafer.record.Field;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.xml.XMLSerializer;
import org.w3c.dom.Node;


public class Demo {

  public void doSearch1(String author, String title) {
    try {
      ZClient client = new ZClient();

// Establish connection parameters
      client.setHost("library.ox.ac.uk");
      client.setPort(210);
      client.setDatabases("ADVANCE");

// See org.jafer.conf.recordDescriptors.xml for valid record schemas
      client.setRecordSchema("http://www.loc.gov/mods/v3");

// Create a QueryBuilder helper
      QueryBuilder builder = new QueryBuilder();
// String forms of Use attributes and matching values. (see org.jafer.conf.bib1Attributes.xml)
      Node a = builder.getNode("author", author);
      Node b = builder.getNode("title", title);
// Submit query
      int results = client.submitQuery(builder.and(a, b));
      System.out.println("Found " + results + " results.");

//limit maximum number of records displayed to 5:
      results = results < 5 ? results : 5;

      for (int n = 1; n <= results; n++) {
// Indicate interest in record number n
        client.setRecordCursor(n);
// Get record as XML
	System.out.println("Record number: "+n+": ");
        Node record = client.getCurrentRecord().getXML();
// Display XML
        XMLSerializer.out(record, "xml", System.out);
        System.out.println();
      }
    } catch (QueryException e) {
      e.printStackTrace();
    } catch (JaferException e) {
      e.printStackTrace();
    }
  }

  public void doSearch2(String author, String title) {
    try {
      ZClient client = new ZClient();

// Establish connection parameters
      client.setHost("library.ox.ac.uk");
      client.setPort(210);
      client.setDatabases("ADVANCE");

// See org.jafer.conf.recordDescriptors.xml for valid record schemas
      client.setRecordSchema("http://www.openarchives.org/OAI/oai_marc");
// Submit query
      int results = client.submitQuery(
          DOMFactory.parse(
               "<AND>" +
                  "<constraintModel>" +
                    "<constraint>" +
                      "<semantic>1003</semantic>" +
                    "</constraint>" +
                    "<model>" + author + "</model>" +
                  "</constraintModel>" +
                  "<constraintModel>" +
                    "<constraint>" +
                      "<semantic>4</semantic>" +
                    "</constraint>" +
                    "<model>" + title + "</model>" +
                  "</constraintModel>" +
                "</AND>"
          ).getFirstChild()
        );
      System.out.println("Found " + results  + " results.");

//limit number of records displayed to 10:
      results = results<10 ? results: 10;

      for (int n = 1; n <= results; n++) {
// Indicate interest in record number n
        client.setRecordCursor(n);
// Get record as XML
        Node record = client.getCurrentRecord().getXML();
// Display XML
	System.out.println("Record number: "+n+": ");
        XMLSerializer.out(record, "xml", System.out);
        System.out.println();
      }
    } catch (QueryException e) {
      e.printStackTrace();
    } catch (JaferException e) {
      e.printStackTrace();
    }
  }

  public void doScan(String author) {
    try {
      ZClient client = new ZClient();

// Establish connection parameters
      client.setHost("library.ox.ac.uk");
      client.setPort(210);
      client.setDatabases("ADVANCE");

// Create a QueryBuilder helper
      QueryBuilder builder = new QueryBuilder();

// Retrieve terms
      Field[] terms = client.getTerms(10, builder.getNode("author", author));


      for (int n=0; n < terms.length; n++) {
// Get term as XML
        Node term = terms[n].getXML();
// Display XML
	System.out.println("Record number: "+n+": ");
        XMLSerializer.out(term, "xml", System.out);
        System.out.println();
      }
    } catch (QueryException e) {
      e.printStackTrace();
    } catch (JaferException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Demo demo = new Demo();

    demo.doSearch1("Smith", "Rights");
//    demo.doSearch2("Shakespeare", "Macbeth");
//    demo.doScan("Shakespeare");
  }
}
