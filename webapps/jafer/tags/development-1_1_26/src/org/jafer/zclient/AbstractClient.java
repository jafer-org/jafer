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
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.zclient;

import org.jafer.util.ConnectionException;

import org.jafer.conf.Config;
import org.jafer.record.Cache;
import org.jafer.record.Field;
import org.jafer.record.RecordFactory;
import org.jafer.record.TermRecord;
import org.jafer.record.DataObject;
import org.jafer.query.QueryParser;
import org.jafer.query.QueryException;
import org.jafer.util.xml.XMLSerializer;
import org.jafer.util.xml.DOMFactory;
import org.jafer.exception.JaferException;
import org.jafer.zclient.operations.PresentException;
import org.jafer.record.RecordException;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import z3950.v3.RPNQuery;
import org.jafer.record.HashtableCache;

public abstract class AbstractClient extends
    org.jafer.interfaces.Databean implements
    org.jafer.interfaces.Cache,
    org.jafer.interfaces.Logging,
    org.jafer.interfaces.Connection,
    org.jafer.interfaces.Z3950Connection,
    org.jafer.interfaces.Authentication,
    org.jafer.interfaces.Search,
    org.jafer.interfaces.Present,
    org.jafer.interfaces.Scan
{

  /**
   * String DEFAULT_DATABASE_NAME
   */
  public static final String DEFAULT_DATABASE_NAME = "xxdefault";
  /**
   * String DEFAULT_RESULTSET_NAME
   */
  public static final String DEFAULT_RESULTSET_NAME = "default";
//  /**
//   * String DEFAULT_SEARCH_PROFILE
//   */
//  public static final String DEFAULT_SEARCH_PROFILE = "0.0.0.0.0.0";
  /**
   * String DEFAULT_ELEMENT_SPEC
   */
  public final static String DEFAULT_ELEMENT_SPEC         = "F";
  /**
   * int DEFAULT_FETCH_SIZE
   */
  public final static int DEFAULT_FETCH_SIZE              = 10;
  /**
   * double DEFAULT_FETCH_VIEW
   */
  public final static double DEFAULT_FETCH_VIEW           = 1.0;
  /**
   * int DEFAULT_DATACACHE_SIZE
   */
  public final static int DEFAULT_DATACACHE_SIZE         = 512;
  /**
   * int MAX_DATACACHE_SIZE
   */
  public final static int MAX_DATACACHE_SIZE             = 2048;
  /**
   * int TIMEOUT
   */
  public final static int TIMEOUT                         = 600000;
  /**
   * int AUTO_RECONNECT
   */
  public final static int AUTO_RECONNECT                  = 5;
  /**
   * String DEFAULT_RECORD_SCHEMA
   */
  public final static String DEFAULT_RECORD_SCHEMA        = "http://www.loc.gov/mods/";
  public final static int[] DEFAULT_RECORD_SYNTAX        = {1,2,840,10003,5,10}; //MARC21


  private int       fetchSize;
  private int       dataCacheSize;
  private int       autoReconnect = -1; /** @todo temporary fix. */
  private int       timeout = -1;  /** @todo temporary fix. */
  private int       attempts;
  private int       numberOfRequestRecords;
  private int       startRecordNumber;
  private int       nResults;
  private int       port;

  private double    fetchView = 1.0;

//  private boolean   debug = false;
  private boolean   checkRecordFormat = false;
  private boolean   parseQuery = true;
  private boolean   dummyNode;

  protected Integer   recordCursor;

  private String    elementSpec;
  protected String    userIP;
  private String    remoteAddress;
  private String    resultSetName;
  private String    recordSchema;

//  private String    recordSyntax;
  private int[]    recordSyntax;

  private String    searchProfile;
  private String    host;
  private String    username;
  private String    password;
  private String    group;

  /**
   * String[] dataBases
   */
  private String[]  dataBases;
  /**
   * int[] dataBases
   */
  private int[]  resultsByDB;
  /**
   * Session session
   */
  private Session session;
  /**
   * Cache cache
   */
  protected Cache cache; // TODO: made protected due to fact that ZClientDB still needs the cache 
  /**
   * RecordFactory recordFactory
   */
  private RecordFactory recordFactory;
  /**
   * Document document
   */
  private Document document; /** @todo remove this, update cache methods */
  /**
   * Node query
   */
  private Object query;
  /**
   * Logger logger
   */
  protected static Logger logger;
  /**
   * Empty Constructor for the ZClient object
   */
  public AbstractClient() {

    logger = Logger.getLogger("org.jafer.zclient");
    logger.log(Level.FINE, "initializing ZClient...");
    setDefaults();
    /** @todo OK to take this out? Called here as well as in submitQuery()?
        timeout isn't set to default value, it's initialised to 0.
        same with autoreconnect. */
  }


  /**
   * sets/checks default properties of the ZClient object
   * @throws JaferException Description of Exception
   */
  private void setDefaults() {

    if (getRemoteAddress() == null)
      userIP = "";
    else
      userIP = "<" + getRemoteAddress() + ">";

    if (getDocument() == null)
      setDocument(DOMFactory.newDocument());

    if (getRecordFactory() == null)
      setRecordFactory(new RecordFactory());

    if (getDatabases() == null)
      setDatabases(DEFAULT_DATABASE_NAME); // moved check for null to setDatabases();

    if (getDataCacheSize() < 1 || getDataCacheSize() > MAX_DATACACHE_SIZE)
      setDataCacheSize(DEFAULT_DATACACHE_SIZE);

          if (getFetchSize() < 1 || getFetchSize() > getDataCacheSize())
              setFetchSize(getDataCacheSize() < DEFAULT_FETCH_SIZE ? getDataCacheSize() : DEFAULT_FETCH_SIZE);

          if (getFetchView() < 0.0 || getFetchView() > 1.0)
              setFetchView(DEFAULT_FETCH_VIEW);

          if (getResultSetName() == null)
              setResultSetName(DEFAULT_RESULTSET_NAME);

          if (getElementSpec() == null)
              setElementSpec(DEFAULT_ELEMENT_SPEC);

          if (getAutoReconnect() < 0)
              setAutoReconnect(AUTO_RECONNECT);

          if (getTimeout() < 0) // if getTimeout() > Integer.MAX_VALUE then returned int is negative value
              setTimeout(TIMEOUT);

//          if (getSearchProfile() == null)
//              setSearchProfile(DEFAULT_SEARCH_PROFILE);

          if (getRecordSchema() == null)
              setRecordSchema(DEFAULT_RECORD_SCHEMA);

          try {
            if (getRecordSyntax() == null)
               setRecordSyntax(Config.convertSyntax(Config.getRecordSyntax(getRecordSchema())));
          }
          catch (JaferException ex) {
           setRecordSyntax(DEFAULT_RECORD_SYNTAX);
          }

          logger.log(Level.FINER, "Java version: " + System.getProperty("java.version"));
          logger.log(Level.FINER, "Java home: " + System.getProperty("java.home"));
          logger.log(Level.FINER, "Classpath: " + System.getProperty("java.class.path"));
          logger.log(Level.FINER, "Operating system: " + System.getProperty("os.name"));
          logger.log(Level.FINER, "ZClient property dataCacheSize: " + getDataCacheSize());
          logger.log(Level.FINER, "ZClient property fetchSize: " + getFetchSize());
          logger.log(Level.FINER, "ZClient property fetchView: " + getFetchView());
          logger.log(Level.FINER, "ZClient property elementSpec: " + getElementSpec());
          logger.log(Level.FINER, "ZClient property recordSchema: " + getRecordSchema());
          logger.log(Level.FINER, "ZClient property host: " + getHost());
          logger.log(Level.FINER, "ZClient property port: " + getPort());
          logger.log(Level.FINER, "ZClient property dataBases: " + getDatabaseNames());
  }


  /**
   * Description of the Method
   * @param query Description of Parameter
   * @return Description of the Returned Value
   */
  public int submitQuery(Object query) throws JaferException {

    logger.entering("ZClient", "public int submitQuery(Object query)");
    try {
      setDefaults();
      //  check if query needs parsing
      if (isParseQuery())
        query = QueryParser.parseQuery(query);
      setQuery(query);

      /**@todo: Read this from config file
       *
       */

      setCache(new HashtableCache(getRecordFactory(), getDataCacheSize()));
      connect();
      logger.exiting("ZClient", "public int submitQuery(Object query)");
      return search();
    } catch (QueryException e) {
      String message = userIP + "ZClient submitQuery(Object query); " + e.getMessage();
      logger.log(Level.SEVERE, message);
      throw new JaferException(e);
    }
  }

  protected abstract Session createSession();

  /**
   * Description of the Method
   * @throws JaferException Description of Exception
   */
  private void connect() throws JaferException {

    logger.entering("ZClient", " private void connect()");
    if (getSession() == null ||
        getSession().getGroup() != this.getGroup() ||
        getSession().getUsername() != this.getUsername() ||
        getSession().getPassword() != this.getPassword()) {

          try {
            setSession(createSession());
            getSession().init(getGroup(), getUsername(), getPassword());
          } catch (ConnectionException e) {
                  String message;
                  if (allowReconnect()) {
                      message = userIP + "ZClient Connection error - connecting again" +
                        " (host: " + getHost() + ", port: "+ getPort() + ", database: "+ getDatabaseNames() +
                        ", username: " + getUsername() + ", group: " + getGroup() + ")";
                      logger.log(Level.WARNING, message);
                      reConnect();
                  } else {
                      message = userIP + "Connection failure - host: " + getHost() + ", port: "+ getPort() +
                        ", username: " + getUsername() + ", group: " + getGroup() + " (" + e.getMessage() + ")";
                      logger.log(Level.SEVERE, message);
                      throw new JaferException(message, e);
                  }
          } catch (JaferException e) {
                  String message = userIP + "ZClient Initialization error; " + e.getMessage();
                  try {
                    logger.log(Level.SEVERE, message);
                  } catch (NullPointerException e2) {
                    System.err.print("SEVERE: " + message + e.toString());
                  }
                  throw e;
          }
          logger.exiting("ZClient", " private void connect()");
    }
  }


  /**
   * Description of the Method
   * @throws JaferException Description of Exception
   */
  protected void reConnect() throws JaferException {

          setAttempts(getAttempts() + 1);
          String message = "Re-starting session... (autoReconnect " + getAttempts() + ")";
          logger.log(Level.INFO, userIP + message);
          close();
          connect();
  }


  /**
   * search
   * @return the returned int
   * @throws JaferException -
   */
  protected int search() throws JaferException {

          logger.entering("ZClient", "private int search()");
          logger.log(Level.FINE, userIP + "Submitting query...");

          try {
            setNumberOfResults(getSession().search(getQuery(), getDatabases(), getResultSetName()));
            setNumberOfRequestRecords(getFetchSize());
            logger.log(Level.FINE, userIP + "Number of search results: " + getNumberOfResults());
            logger.exiting("ZClient", "int search()");
            return getNumberOfResults();
          } catch (ConnectionException e) {
                String message;
                if (allowReconnect()) {
                  message = userIP + "Connection failure whilst submitting query - connecting again";
                  logger.log(Level.WARNING, message);
                  reConnect();
//                  return setNumberOfResults(new int[]{search()});
                  return search();
                } else {
                  message = userIP + "Connection failure whilst submitting query (autoReconnect = " + getAutoReconnect() + ")";
                  logger.log(Level.SEVERE, message);
                  throw new JaferException(message, e);
                }
          } catch (JaferException e) {
            String message = userIP + "Error attempting search (" + getHost() + ":" + getPort() +
                            ", dataBase(s) " + getDatabaseNames() + ", username " + getUsername() + "): ";
            message += e.getMessage();
            logger.log(Level.SEVERE, message);
            throw new JaferException(message, e.getDiagnostic());
          }
  }


  /**
   * Sets the RecordCursor attribute of the ZClient object
   * @param nRecord The new RecordCursor value
   * @throws JaferException Description of Exception
   */
  public void setRecordCursor(int nRecord) throws JaferException {

        logger.entering("ZClient", "public void setRecordCursor(int nRecord)");

        if (nRecord > 0 && nRecord <= getNumberOfResults()) {
                this.recordCursor = new Integer(nRecord);

            if (!getCache().contains(recordCursor)) {
              try {
                  setStartRecordNumber(nRecord);
                  Vector dataObjects =
                      getSession().present(
                          getStartRecordNumber(),
                          getNumberOfRequestRecords(),
//                          Config.convertSyntax(Config.getRecordSyntax(getRecordSchema())),
                          getRecordSyntax(),
                          getElementSpec(),
                          getResultSetName()
                          );

                  for (int n = 0; n < dataObjects.size(); n++) {
                    Integer recordNumber = new Integer(getStartRecordNumber() + n);
                    if (!getCache().contains(recordNumber))
                        getCache().put(recordNumber, (DataObject)dataObjects.get(n));
                  }

                } catch (PresentException e) {
                  int status = e.getStatus();
                  switch (status) {
                    case 1: // Present request terminated try again...
                            handleError(userIP + "Present status = " + status + ": " + e.getMessage() + " (record " + getStartRecordNumber() + "):" +
                                " records requested = " + getNumberOfRequestRecords() + ", records returned = " + e.getNumberOfRecordsReturned());
                            if (allowReconnect()) {
                                reConnect();
                                search();
                                setRecordCursor(getRecordCursor());
                            } else
                                handleError(userIP + "Persistent failure whilst retrieving records (autoReconnect = " + getAutoReconnect() + ")", e);
                    break;
                    case 2: // too many records requested try again...

                            if (e.getNumberOfRecordsReturned() > 1) {
                                handleError(userIP + "Present status = " + status +": " + e.getMessage() + " (record " + getStartRecordNumber() + "):" +
                                " decreasing Fetch size from " + getNumberOfRequestRecords() + " to " + e.getNumberOfRecordsReturned());
                                setNumberOfRequestRecords(e.getNumberOfRecordsReturned());
                                setRecordCursor(getRecordCursor());
                            } else { // message size too small - Present Failure
                                handleError(userIP + "Present status = " + status +
                                    "; requested record was not returned - message size is too small (record " + getStartRecordNumber() + "):" +
                                    " records requested = " + getNumberOfRequestRecords() + ", records returned = " + e.getNumberOfRecordsReturned(), e);
                            }
                    break;
                    case 3: // Present request terminated try again...
                            handleError(userIP + "Present status = " + status +
                                    "; requested record was not returned - message size is too small (record " + getStartRecordNumber() + "):" +
                                    " records requested = " + getNumberOfRequestRecords() + ", records returned = " + e.getNumberOfRecordsReturned());
                            if (allowReconnect()) {
                                reConnect();
                                search();
                                setRecordCursor(getRecordCursor());
                            } else
                                handleError(userIP + "Persistent failure whilst retrieving records (autoReconnect = " + getAutoReconnect() + ")", e);
                    break;
                    case 4: // Present request terminated try again...
                            handleError(userIP + "Present status = " + status + ": " + e.getMessage() + " (record " + getStartRecordNumber() + "):" +
                                " records requested = " + getNumberOfRequestRecords() + ", records returned = " + e.getNumberOfRecordsReturned());
                            if (allowReconnect()) {
                                reConnect();
                                search();
                                setRecordCursor(getRecordCursor());
                            } else
                                handleError(userIP + "Persistent failure whilst retrieving records (autoReconnect = " + getAutoReconnect() + ")", e);
                    break;
                    case 5: // Present failure
                            handleError(userIP + "Present failed status = " + status + ": " + e.getMessage() + " (record " + getStartRecordNumber() + ")", e);
                    }
                } catch (ConnectionException e) { // Present terminated by ConnectionException try again...
                    if (allowReconnect()) {
                        handleError(userIP + "Connection failure whilst retrieving records (record " + getStartRecordNumber() + ") - connecting again (" + e.toString() +")");
                        reConnect();
                        search();
                        setRecordCursor(getRecordCursor());
                    } else
                        handleError(userIP + "Connection failure whilst retrieving records (autoReconnect = " + getAutoReconnect() + ")", e);
                }
            }
        } else {
            String message = userIP + "Record number " + nRecord + " not found (number of search results = " + getNumberOfResults() + ")";
            handleError(message, new JaferException(message));
        }

        logger.exiting("ZClient", "public void setRecordCursor(int nRecord)");
  }


  protected void handleError(String message) {

          logger.log(Level.WARNING, message);
  }



  protected void handleError(String message, JaferException e) throws JaferException {

          logger.log(Level.SEVERE, message);
          throw e;
  }


  /**
   * Gets the RecordCursor attribute of the ZClient object
   * @return The RecordCursor value as int
   */
  public int getRecordCursor() {

          return recordCursor.intValue();
  }



  public void setDocument(Document document) {

          this.document = document;
  }


  public Document getDocument() {

          return document;
  }



  /**
   * getDatabaseNames
   * @return the returned String
   */
  private String getDatabaseNames() {


    String dbNames = "";
    for (int i = 0; i < getDatabases().length; i++)
      dbNames += getDatabases()[i] + " ";

    return dbNames;
  }




  protected void finalize() throws JaferException {

          close();
          setSession(null);
          setCache(null);
          setRecordFactory(null);
//          setQueryBuilder(null);
          setDocument(null);
//          setQueryDocument(null);
  }




  /**
   * close
   * @throws JaferException
   */
  public void close() throws JaferException {

          if (getSession() != null) {
              getSession().close();
              setSession(null);
          }
  }


  /**
   * Sets the remoteAddress attribute of the ZClient object (for Servlets)
   * @param remoteAddress The new remoteAddress value
   */
  public void setRemoteAddress(String remoteAddress) {

          this.remoteAddress = remoteAddress;
  }



  /**
   * Gets the remoteAddress attribute of the ZClient object (for Servlets)
   * @return remoteAddress                    The remoteAddress value
   */
  public String getRemoteAddress() {

          return remoteAddress;
  }




  /**
   * Sets the parseQuery attribute of the ZClient object
   * @param parseQuery The new parseQuery value
   */
  public void setParseQuery(boolean parseQuery) {

          this.parseQuery = parseQuery;
  }




  /**
   * Gets the parseQuery attribute of the ZClient object
   * @return parseQuery                    The parseQuery value
   */
  public boolean isParseQuery() {

          return parseQuery;
  }




  public void setRecordSchema(String recordSchema){

          this.recordSchema = recordSchema;
  }



  public String getRecordSchema() {

          return recordSchema;
  }

  public void setRecordSyntax(int[] recordSyntax) {

          this.recordSyntax = recordSyntax;
  }

  public int[] getRecordSyntax() {

          return recordSyntax;
  }

  private void setRecordFactory(RecordFactory recordFactory) {

          this.recordFactory = recordFactory;
  }


  private RecordFactory getRecordFactory() {

          return recordFactory;
  }


  private void setSession(Session session) {

          this.session = session;
  }


  protected Session getSession() {

          return session;
  }



  private void setCache(Cache cache) {

          this.cache = cache;
  }


  protected Cache getCache() {

          return cache;
  }

//
//
//  private void setQueryDocument(Document queryDocument) {
//
//          this.queryDocument = queryDocument;
//  }
//
//
//  private Document getQueryDocument() {
//
//          return queryDocument;
//  }
//


  /**
   * Sets the timeout attribute of the ZClient object
   * @param timeout The new timeout value
   */
  public void setTimeout(int timeout) {

          this.timeout = timeout;
  }


  /**
   * Gets the timeout attribute of the ZClient object
   * @return The timeout value
   */
  public int getTimeout() {

        return timeout;
  }



  /**
   * Sets the autoReconnect attribute of the ZClient object
   */
  public void setAutoReconnect(int autoReconnect) {

          this.autoReconnect = autoReconnect;
  }


  /**
   * Gets the autoReconnect attribute of the ZClient object
   * @return The autoReconnect value
   */
  public int getAutoReconnect() {

        return autoReconnect;
  }


  /**
   * Sets the reTry attribute of the ZClient object
   */
  private void setAttempts(int attempts) {

          this.attempts = attempts;
  }


  /**
   * Gets the attempts attribute of the ZClient object
   * @return The attempts value
   */
  private int getAttempts() {

        return attempts;
  }

  /**
   * returns true if ZClient should attempt reconnect
   * @return true if getAttempts() <= getAutoReconnect()
   */
  protected boolean allowReconnect() {

        return getAttempts() < getAutoReconnect();
  }

  /**
   * Sets the numberOfRequestRecords attribute of the ZClient object
   * @param numberOfRequestRecords The new numberOfRequestRecords value
   */
  protected void setNumberOfRequestRecords(int numberOfRequestRecords) {

      if (numberOfRequestRecords < 1 || numberOfRequestRecords > getNumberOfResults())
        this.numberOfRequestRecords = 1;
      else
        this.numberOfRequestRecords = numberOfRequestRecords;
  }


  /**
   * Gets the numberOfRequestRecords attribute of the ZClient object
   * @return The numberOfRequestRecords value
   */
  protected int getNumberOfRequestRecords() {

          return numberOfRequestRecords;
  }


  /**
   * sets startRecordNumber for Present
   * @param nRecord The value of setRecordCursor
   */
  protected void setStartRecordNumber(int nRecord) {

          int forwardOffset, backwardOffset;
          forwardOffset = (int) ((getNumberOfRequestRecords() - 1) * getFetchView());
          if (nRecord + forwardOffset > getNumberOfResults()) {
                  forwardOffset = getNumberOfResults() - nRecord;
                  backwardOffset = (getNumberOfRequestRecords() - 1) - forwardOffset;
          } else {
                  backwardOffset = (getNumberOfRequestRecords() - 1) - forwardOffset;
                  if (nRecord - backwardOffset < 1) {
                          backwardOffset = nRecord - 1;
                          forwardOffset = (getNumberOfRequestRecords() - 1) - backwardOffset;
                  }
          }

          this.startRecordNumber = nRecord - backwardOffset;
  }



  /**
   * Gets setStartRecordNumber record number for Present
   * @return startRecordNumber the startRecordNumber value
   */
  protected int getStartRecordNumber() {

          return startRecordNumber;
  }


  /**
   * Gets the CurrentRecord attribute of the ZClient object
   * @return The CurrentRecord value
   * @throws JaferException Description of Exception
   */
  public Field getCurrentRecord() throws JaferException {

      Node recordRoot = cache.getXML(getDocument(), getRecordSchema(), getRecordCursorAsInteger());

      int[] syntax = Config.convertSyntax(((Element)recordRoot).getAttribute("syntax"));

      if (Config.isSyntaxEqual(syntax, Config.convertSyntax(Config.getRecordSyntaxFromName("DIAG_BIB1"))))
          throw new RecordException("Returned record is Surrogate Diagnostic",
                              new Field(recordRoot, recordRoot.getFirstChild()));
      if (Config.isSyntaxEqual(syntax, Config.convertSyntax(Config.getRecordSyntaxFromName("JAFER"))))
          throw new RecordException("Error generating XML from returned record",
                              new Field(recordRoot, recordRoot.getFirstChild()));
      if (isCheckRecordFormat()) {
          String schema = ((Element)recordRoot).getAttribute("schema");
          if (!schema.equals(getRecordSchema()))
            throw new RecordException("Returned record (schema: " + schema + ") does not match requested schema "
                                        + getRecordSchema(), new Field(recordRoot, recordRoot.getFirstChild()));
      }
      return new Field(recordRoot, recordRoot.getFirstChild());
  }

  /**
   * Gets the CurrentDataObject attribute of the ZClient object
   * @return The CurrentDataObject value
   * @throws JaferException Description of Exception
   */
  public DataObject getCurrentDataObject() throws JaferException {

          return cache.getDataObject(getRecordCursorAsInteger());
  }


  /**
   * Gets the CurrentDataBase attribute of the ZClient object
   * @return The CurrentDataBase value
   * @throws JaferException Description of Exception
   */
  public String getCurrentDatabase() throws JaferException {

            return cache.getDataObject(getRecordCursorAsInteger()).getDatabaseName();
  }


  /**
   * Gets the CurrentRecordSyntax attribute of the ZClient object
   * @return The CurrentRecordSyntax value
   * @throws JaferException Description of Exception
   */
  public String getCurrentRecordSyntax() throws JaferException {

            return Config.convertSyntax(cache.getDataObject(getRecordCursorAsInteger()).getRecordSyntax());
  }


  /**
   * Gets the CurrentRecordSyntaxName value of the ZClient object
   * @return The CurrentRecordSyntaxName value
   * @throws JaferException Description of Exception
   */
  public String getCurrentRecordSyntaxName() throws JaferException {

          return Config.getRecordNameFromSyntax(getCurrentRecordSyntax());
  }




  /**
   * Sets the Host attribute of the ZClient object
   * @param host The new Host value
   */
  public void setHost(String host) {
    if (!host.equalsIgnoreCase(this.host)) {
      try {
        close();
      } catch (Exception ex) {
        /**@todo: Do anything here?
         */
      }
      this.host = host;
    }
  }


  /**
   * Sets the Port attribute of the ZClient object
   * @param port The new Port value
   */
  public void setPort(int port) {
          if (this.port != port) {
            try {
              close();
            } catch (Exception ex) {
              /**@todo: Do anything here?
               */
            }
            this.port = port;
          }
  }


  /**
   * Sets the resultSetName attribute of the ZClient object
   * @param resultSetName The new resultSetName value
   */
  public void setResultSetName(String resultSetName) {

          this.resultSetName = resultSetName;
  }



  /**
   * Sets the Databases attribute of the ZClient object
   * @param databases The new Databases value
   */

  /** @todo throw exception if databases param is null? */
  public void setDatabases(String[] databases) {

    if (databases != null) {
      Vector v = new Vector();
      for (int i = 0; i < databases.length; i++) {
        if (databases[i] != null)
          v.add(databases[i]);
      }
      this.dataBases = (String[])v.toArray(new String[]{});
      this.resultsByDB = new int[this.dataBases.length];
    }
  }


  /**
   * Sets the Databases attribute of the ZClient object
   * @param database The new Databases value
   */
  public void setDatabases(String database) {

    setDatabases(new String[] {database});
  }


  /**
   * Sets the username attribute of the ZClient object
   * @param username The new username value
   */
  public void setUsername(String username) {

    this.username = username;
  }


  /**
   * Sets the password attribute of the ZClient object
   * @param password The new password value
   */
  public void setPassword(String password) {

    this.password = password;
  }


  /**
   * Sets the checkRecordFormat attribute of the ZClient object
   * @param checkRecordFormat The new checkRecordFormat value
   */
  public void setCheckRecordFormat(boolean checkRecordFormat) {

          this.checkRecordFormat = checkRecordFormat;
  }


  /**
   * Gets the checkRecordFormat attribute of the ZClient object
   * @return checkRecordFormat  The checkRecordFormat value
   */
  public boolean isCheckRecordFormat() {

          return checkRecordFormat;
  }


  /**
   * Sets the ElementSpec attribute of the ZClient object
   * @param elementSpec The new ElementSpec value
   */
  public void setElementSpec(String elementSpec) {

          this.elementSpec = elementSpec;
  }


  /**
   * Sets the DataCacheSize attribute of the ZClient object
   * @param dataCacheSize The new DataCacheSize value
   */
  public void setDataCacheSize(int dataCacheSize) {

          this.dataCacheSize = dataCacheSize;
  }


  /**
   * Sets the fetchSize attribute of the ZClient object
   * @param fetchSize The new fetchSize value
   */
  public void setFetchSize(int fetchSize) {

          this.fetchSize = fetchSize;
  }


  /**
   * Sets the FetchView attribute of the ZClient object
   * @param fetchView The new FetchView value
   */
  public void setFetchView(double fetchView) {

          this.fetchView = fetchView;
  }


  /**
   * Sets the nResults attribute of the ZClient object
   * @param nResults The new nResults value
   * @return nResults  The new nResults value
   */
  private int setNumberOfResults(int[] resultsByDB) {

    int total = 0;

    for (int i=0; i<resultsByDB.length; i++)
      total += resultsByDB[i];

    this.resultsByDB = resultsByDB;

    return nResults = total;
  }



  /**
   * Gets the Host attribute of the ZClient object
   * @return The Host value
   */
  public String getHost() {
    return host;
  }


  /**
   * Gets the Port attribute of the ZClient object
   * @return The Port value
   */
  public int getPort() {

    return port;
  }


  /**
   * Gets the resultSetName attribute of the ZClient object
   * @return The resultSetName value
   */
  public String getResultSetName() {

    return resultSetName;
  }


  /**
   * Gets the DataBases attribute of the ZClient object
   * @return The DataBases value
   */
  public String[] getDatabases() {

    return dataBases;
  }

  /**
   * Gets the resultsByDB attribute of the ZClient object
   * @return The resultsByDB value
   */
  private int[] getResultsByDatabases() {

    if (resultsByDB != null)
      return resultsByDB;

    return new int[0];
  }


  /**
   * Gets the Username attribute of the ZClient object
   * @return The Username value
   */
  public String getUsername() {

    return username;
  }


  /**
   * Gets the Password attribute of the ZClient object
   * @return The Password value
   */
  public String getPassword() {

    return password;
  }


  public String getGroup() {

    return group;
  }


  public void setGroup(String group) {

    this.group = group;
  }


  /**
   * Gets the ElementSpec attribute of the ZClient object
   * @return The ElementSpec value
   */
  public String getElementSpec() {

    return elementSpec;
  }


  /**
   * Gets the query attribute of the ZClient object
   * @return The query value
   */
  public Object getQuery() {

    return query;
  }


  /**
   * Gets the DataCacheSize attribute of the ZClient object
   * @return The DataCacheSize value
   */
  public int getDataCacheSize() {

    return dataCacheSize;
  }


  /**
   * Gets the fetchSize attribute of the ZClient object
   * @return The fetchSize value
   */
  public int getFetchSize() {

    return fetchSize;
  }


  /**
   * Gets the FetchView attribute of the ZClient object
   * @return The FetchView value
   */
  public double getFetchView() {

    return fetchView;
  }


  /**
   * Gets the NumberOfResults attribute of the ZClient object
   * @return The NumberOfResults value
   */
  public int getNumberOfResults() {

    return nResults;
  }


  /**
   * Gets the NumberOfResults attribute of the ZClient object
   * @return The NumberOfResults value
   */
  public int getNumberOfResults(String databaseName) {

    if (getDatabases().length == getResultsByDatabases().length) {
      for (int i=0; i<getDatabases().length; i++) {
        if (getDatabases()[i].equalsIgnoreCase(databaseName))
          return resultsByDB[i];
      }
    }
    else
      logger.log(Level.SEVERE, "Results per database does not match number of database names set.");

    return -1;
  }


  /**
   * Gets the RecordCursor attribute of the ZClient object
   * @return The RecordCursor value as Integer
   */
  protected Integer getRecordCursorAsInteger() {

    return recordCursor;
  }

  /**
   * Description of the Method
   * @param file Description of Parameter
   */
  public void saveQuery(String file) throws JaferException {

/** @todo save other types of query objects... */
    if (getQuery() instanceof Node) {
      try {
        XMLSerializer.out((Node)getQuery(), false, file);
      } catch (JaferException e) {
        String message = userIP + "ZClient, public void saveQuery(String file): cannot save query to file "
                + file + "; " + e.toString();
        logger.log(Level.SEVERE, message, e);
        throw e;
      }
    }
  }

  /**
   * @todo: Scan added as experimental code
   *
   *
   */

   public Field[] getTerms(int noOfTerms, Node term) {

     return getTerms(noOfTerms, 1, 1, term);
   }

   public Field[] getTerms(int noOfTerms, int termStep, int termPosition, Node term) {

    try {
      Vector rawTerms = session.scan(this.getDatabases(), noOfTerms, termStep, termPosition, term);
      Field[] terms = new Field[rawTerms.size()];

      for (int n = 0; n < rawTerms.size(); n++) {
        TermRecord termRecord = (TermRecord)rawTerms.get(n);
        terms[n] = new Field(termRecord.getXML(this.getDocument()), termRecord.getXML(this.getDocument()));
      }

      return terms;
    } catch (Exception e) {
      /**
       * @todo: catch exception
       *
       */

       return new Field[0];
    }
   }

  /**
   * Sets the query attribute of the ZClient object
   * @param query The new query value
   */
   private void setQuery(Object query) {

     this.query = query;
   }
}
