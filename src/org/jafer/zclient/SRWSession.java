package org.jafer.zclient;

import org.jafer.conf.Config;
import org.jafer.query.*;
import org.jafer.record.DataObject;
import org.jafer.record.XMLRecord;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.PDUDriver;
import org.jafer.exception.JaferException;
import org.jafer.util.ConnectionException;
import org.jafer.zclient.operations.PresentException;

import gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingStub;
import gov.loc.www.zing.srw.*;
import z3950.v3.RPNQuery;

import org.apache.axis.*;
import org.apache.axis.types.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.Vector;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class SRWSession
    implements Session {

  SRWSoapBindingStub srwBinding;

  public SRWSession(String url) {
    try {
      srwBinding = new SRWSoapBindingStub(new java.net.URL(url), null);
    }
    catch (MalformedURLException ex) {
    }
    catch (AxisFault ex) {
    }
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
   * getPDUDriver
   *
   * @return PDUDriver
   * @todo Implement this org.jafer.zclient.Session method
   */
  public PDUDriver getPDUDriver() {
    return null;
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

      SearchRetrieveResponseType response = srwBinding.searchRetrieveOperation(request);

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
                schema = org.jafer.conf.Config.translateSRWSchemaName(schema);
              } else {
                schema = org.jafer.conf.Config.translateSRWSchemaName(root.getNamespaceURI());
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
            schema = org.jafer.conf.Config.translateSRWSchemaName(schema);
          else
            schema = root.getNamespaceURI();

          XMLRecord record = new XMLRecord(root, schema);
        }
      }

          return dataObjects;
    }
    catch (RemoteException ex) {
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

      XMLCQLQuery q = new XMLCQLQuery();

      if (queryObject instanceof RPNQuery) {
        query = new XMLCQLQuery().getCQLQuery(new XMLRPNQuery().getXMLQuery((RPNQuery)queryObject));
      }
      else if (queryObject instanceof Node)
        query = new XMLCQLQuery().getCQLQuery((Node)queryObject);
      else
        throw new QueryException("Query type: "+ queryObject.getClass().getName() +" not supported", 107, "");

      request.setVersion("1.1");
      request.setQuery(query);
      request.setStartRecord(new PositiveInteger("1"));
      request.setMaximumRecords(new NonNegativeInteger("0"));

      SearchRetrieveResponseType response = srwBinding.searchRetrieveOperation(
          request);
      return new int[]{ response.getNumberOfRecords().intValue()};
    }
    catch (RemoteException ex) {
      throw new ConnectionException(ex);
    }
  }

  /**
   * setPDUDriver
   *
   * @param pduDriver PDUDriver
   * @todo Implement this org.jafer.zclient.Session method
   */
  public void setPDUDriver(PDUDriver pduDriver) {
  }
}
