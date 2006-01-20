package org.jafer.zclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import gov.loc.www.zing.srw.*;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.jafer.exception.JaferException;
import org.jafer.query.*;
import org.jafer.record.XMLRecord;
import org.jafer.transport.ConnectionException;
import org.jafer.util.xml.DOMFactory;
import org.jafer.zclient.operations.PresentException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import z3950.v3.RPNQuery;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.Deserializer;

public class SRUSession
    implements Session {

  String endpoint;

  public SRUSession(String url) {
      endpoint = url;
  }

  /**
   * close
   *
   * @todo Implement this org.jafer.zclient.Session method
   */
  public void close() {
  }

  /**
   * getGroup
   *
   * @return String
   * @todo Implement this org.jafer.zclient.Session method
   */
  public String getGroup() {
    return "";
  }

  /**
   * getId
   *
   * @return int
   * @todo Implement this org.jafer.zclient.Session method
   */
  public int getId() {
    return 0;
  }

  /**
   * getName
   *
   * @return String
   * @todo Implement this org.jafer.zclient.Session method
   */
  public String getName() {
    return "";
  }

  /**
   * getPassword
   *
   * @return String
   * @todo Implement this org.jafer.zclient.Session method
   */
  public String getPassword() {
    return "";
  }

  /**
   * getUsername
   *
   * @return String
   * @todo Implement this org.jafer.zclient.Session method
   */
  public String getUsername() {
    return "";
  }

  /**
   * init
   *
   * @param group String
   * @param username String
   * @param password String
   * @throws ConnectionException
   * @todo Implement this org.jafer.zclient.Session method
   */
  public void init(String group, String username, String password) throws
      ConnectionException {
  }


  private String query;

  /**
   * present
   *
   * @param nRecord int
   * @param nRecords int
   * @param recordOID int[]
   * @param eSpec String
   * @param resultSetName String
   * @throws PresentException
   * @throws ConnectionException
   * @return Vector
   * @todo Implement this org.jafer.zclient.Session method
   */
  public Vector present(int nRecord, int nRecords, int[] recordOID,
                        String eSpec, String resultSetName) throws
      PresentException, ConnectionException {

    /** @todo Add new method with more relevant signature to interface? */
    try {
      Vector dataObjects = new Vector();

      SearchRetrieveRequestType request = new SearchRetrieveRequestType();

      request.setVersion("1.1");
      request.setQuery(query);
      request.setStartRecord(new PositiveInteger(Integer.toString(nRecord)));
      request.setMaximumRecords(new PositiveInteger(Integer.toString(nRecords)));
      request.setRecordPacking("string");

      /**@todo: need to set schema by OID
       *
       */

      SearchRetrieveResponseType response = searchRetrieveOperation(request);

      RecordType[] records = response.getRecords().getRecord();

      for (int i = 0; i < records.length; i++) {
        String recordPacking = records[i].getRecordPacking();
        if (recordPacking == null) {
          recordPacking = "string"; //strictly recordPacking should never be null
        }
        if (recordPacking.equalsIgnoreCase("string")) {
          String data = records[i].getRecordData().get_any()[0].getNodeValue();
          try {
            Document doc = DOMFactory.parse(data);
            Node root = doc.getDocumentElement();
            ////////////////////////////
//            org.jafer.util.xml.XMLSerializer.out(root, "xml", "C:/root.xml");
            ////////////////////////////
            String schema = records[i].getRecordSchema();
            if (schema != null)
              if (!schema.equalsIgnoreCase("default")) {
                schema = org.jafer.util.Config.translateSRWSchemaName(schema);
              } else {
                schema = org.jafer.util.Config.translateSRWSchemaName(root.getNamespaceURI());
              }
            else
              schema = root.getNamespaceURI();



            XMLRecord record = new XMLRecord(root, schema);
//            record.setrecordsyntax...?
            dataObjects.add(record);
          }
          catch (JaferException ex1) {
            /** @todo  */
            ex1.printStackTrace();
          }
        } else if (recordPacking.equalsIgnoreCase("xml")) {
          Node root = records[i].getRecordData().get_any()[0].getFirstChild();
          String schema = records[i].getRecordSchema();
          if (schema != null)
            schema = org.jafer.util.Config.translateSRWSchemaName(schema);
          else
            schema = root.getNamespaceURI();

          XMLRecord record = new XMLRecord(root, schema);
        }
      }

          return dataObjects;
    }
    catch (Exception ex) {
      throw new ConnectionException(ex);
    }
  }

  /**
   * scan
   *
   * @param databases String[]
   * @param nTerms int
   * @param step int
   * @param position int
   * @param term Node
   * @throws JaferException
   * @throws ConnectionException
   * @return Vector
   * @todo Implement this org.jafer.zclient.Session method
   */
  public Vector scan(String[] databases, int nTerms, int step, int position,
                     Node term) throws JaferException, ConnectionException {
    return null;
  }

  /**
   * scan
   *
   * @param databases String[]
   * @param nTerms int
   * @param step int
   * @param position int
   * @param termObject Object
   * @throws JaferException
   * @throws ConnectionException
   * @return Vector
   * @todo Implement this org.jafer.zclient.Session method
   */
  public Vector scan(String[] databases, int nTerms, int step, int position,
                     Object termObject) throws JaferException,
      ConnectionException {
    return null;
  }

  /**
   * search
   *
   * @param queryObject Object
   * @param databases String[]
   * @param resultSetName String
   * @throws JaferException
   * @throws ConnectionException
   * @return int[]
   * @todo Implement this org.jafer.zclient.Session method
   */
  public int[] search(Object queryObject, String[] databases,
                      String resultSetName) throws JaferException,
      ConnectionException {

    try {
      SearchRetrieveRequestType request = new SearchRetrieveRequestType();

      if (queryObject instanceof RPNQuery) {
        org.jafer.query.RPNQuery rpnQuery =  new org.jafer.query.RPNQuery((RPNQuery)queryObject);
        query = new CQLQuery(rpnQuery.toJaferQuery()).getCQLQuery();
      }
      else if (queryObject instanceof Node)
        query = new CQLQuery(new JaferQuery((Node)queryObject)).getCQLQuery();
      else
        throw new QueryException("Query type: "+ queryObject.getClass().getName() +" not supported", 107, "");

      request.setVersion("1.1");
      request.setQuery(query);
      request.setStartRecord(new PositiveInteger("1"));
      request.setMaximumRecords(new NonNegativeInteger("0"));

      SearchRetrieveResponseType response = searchRetrieveOperation(
          request);
      return new int[]{ response.getNumberOfRecords().intValue()};
    }
    catch (Exception ex) {
      throw new ConnectionException(ex);
    }
  }

    private SearchRetrieveResponseType searchRetrieveOperation(
            SearchRetrieveRequestType request) {
        StringBuffer parameters = new StringBuffer();

        /**
         * Andy's next mission:
         * Build an SRU URL from the request parameters
         * Send to the endpoint URL
         * Deserialise returned XML into SearchRetrieveResponseType
         *
         */

        QName searchQName = new QName("http://www.loc.gov/zing/srw/", "searchRetrieveResponse");
        Deserializer ser = SearchRetrieveResponseType.getDeserializer(null, SearchRetrieveResponseType.class, searchQName);


        return null;
    }

}
