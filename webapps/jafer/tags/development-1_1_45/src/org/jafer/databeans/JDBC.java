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
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2002
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.databeans;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.sql.Connection;
import java.util.*;
import java.util.logging.*;

import javax.sql.*;
import javax.xml.transform.*;

import org.apache.xpath.*;
import org.jafer.exception.*;
import org.jafer.interfaces.*;
import org.jafer.record.*;
import org.jafer.util.xml.*;

import org.w3c.dom.*;

import z3950.v3.RPNQuery;
import org.jafer.util.*;

/**
 * <p>Superclass with behaviour common to subclasses. Specific subclasses configured to make use of specific JDBC drivers should be instantiated.<br/>
 *
 * No caching available, apart from the ResultSet object (which may have a Forward Only cursor).<br/>
 * <p><b>XML configuration files:</b><br/>
 * <b>config.xml</b><br/>
 * Database name, username, password for JDBC driver to use.<br/>
 * Toggle to return results with initial search (piggyback) on/off.<br/>
 * List of XML templates for creating records of different formats, with locations of the files.<br/>
 * Mappings of Z39.50 Use attributes to SQL database tables and columns. (Also prefix/append values to add wildcards to SQL query for each.)<br/>
 * Mappings for translation of Relation attribute values to SQL operators.<br/>
 * <b>OAITemplate.xml</b><br/>
 * Template record conforming to OAI schema. Data from database is inserted in place of <jafer:insertElement> nodes, from the column specified.<br/>
 * <b>MODSTemplate.xml</b><br/>
 * Template record conforming to MODS schema. Data from database is inserted in place of <jafer:insertElement> nodes, from the column specified.<br/>
 * </p>
 * <p>(Column names used in building the SQL query are taken from the XML record template in use, and are expected to be of the form <b>tableName.columnName</b>.)</p>
 * <b>query.xsl</b><br/>
 * The incoming query is translated from an XML format to SQL via this stylesheet.
 */


public abstract class JDBC extends Databean implements Z3950Connection, Present, Search {

  public static Logger logger;
  private URL queryXSLT;
  private Document configDocument;
  private Document recordTemplate, recordDocument;
  private Hashtable paramMap, recordSchemas;

  protected Node query;
  protected DataSource dataSource;
  protected Connection connection;
  protected ResultSet resultSet;
  private String hostName, currentDatabase, recordSchema, queryString;
  protected String username, password, primaryTable, primaryKey, foreignKey;
  private int port, recordCursor;
  protected int nResults;

  private static CachedXPathAPI cachedXPath = new CachedXPathAPI();

  public final static String CONFIG_FILE = "org/jafer/conf/jdbcConfig/config.xml";
  public final static String QUERY_XSLT = "org/jafer/conf/jdbcConfig/query.xsl";

/**
 * Loads configuration info from config files.
 * @see initialise()
 */

  public JDBC() {
/** @todo move initialise() methods out of constructor, to prevent unecessary
 processing when called by JDBCFactory getDatabean() for interface probing.*/

/** @todo handle exceptions, rather than passing... */

/** @todo logging in rest of Class... */

    logger = Logger.getLogger("org.jafer.zserver");
    try {
      initialise();
    }
    catch (JaferException ex) {
      logger.log(Level.SEVERE, "Error in configuring JDBC object", ex);
      throw new RuntimeException("Error in configuring JDBC object", ex);
    }
  }

  public void setHost(String hostName) {
/** @todo method used by JDBCFactory using setting in server.xml. */
    this.hostName = hostName;
  }

  public String getHost() {
    return hostName;
  }

  public void setPort(int port) {
/** @todo method used by JDBCFactory using setting in server.xml. */
    this.port = port;
  }

  public int getPort() {
    return port;
  }

  public void setRecordCursor(int nRecord) throws JaferException {

    if (nRecord > 0 && nRecord <= getNumberOfResults())
      recordCursor = nRecord;
    else
      throw new JaferException("Record cursor cannot be set to a value higher than the number of results returned, or zero");
  }

  public int getRecordCursor() {
    return recordCursor;
  }

  public void setCheckRecordFormat(boolean checkRecordFormat) {
    throw new java.lang.UnsupportedOperationException("Method setCheckRecordFormat() not yet implemented.");
  }

  public boolean isCheckRecordFormat() {
    throw new java.lang.UnsupportedOperationException("Method isCheckRecordFormat() not yet implemented.");
  }

  public void setElementSpec(String elementSpec) {
    throw new java.lang.UnsupportedOperationException("Method setElementSpec() not yet implemented.");
  }

  public String getElementSpec() {
    throw new java.lang.UnsupportedOperationException("Method getElementSpec() not yet implemented.");
  }

/**
 * Sets record format to be produced, using name of XML Schema for record format.<br/>
 * If a suitable template is not available, uses default record format set in config.xml, or last schema set successfully.
 */
  public void setRecordSchema(String schema) {

    if (schema.equals(getRecordSchema()))
      return;

    if (recordSchemas.containsKey(schema)) {
      recordSchema = schema;
      setRecordTemplate(getRecordSchema());
    }
    else
      logger.log(Level.WARNING, "Record Schema requested is not available: "+ schema);
  }

  public String getRecordSchema() {

    return recordSchema;
  }

/**
 * Creates record from data in database, following the layout of the chosen record template.
 */
  public Field getCurrentRecord() throws JaferException {

    Field field = null;
    if (resultSet == null)
        try {
          search();
        }
        catch (JaferException ex) {
          ex.printStackTrace();
        }
        catch (SQLException ex) {
          ex.printStackTrace();
        }

      if (getRecordSchema() == null)
	throw new JaferException("Record schema not set");

      try {
        field = createRecord();
      }
      catch (JaferException ex1) {
          ex1.printStackTrace();
      }
      catch (SQLException ex1) {
          ex1.printStackTrace();
      }

      return field;
//    }
//    catch (SQLException ex) {
//      throw new JaferException("Error executing query on database", ex);
//    }
  }

  public String getCurrentDatabase() {

    return currentDatabase;
  }

  public void setSearchProfile(String searchProfile) {
    throw new java.lang.UnsupportedOperationException("Method setSearchProfile() not yet implemented.");
  }

  public String getSearchProfile() {
    throw new java.lang.UnsupportedOperationException("Method getSearchProfile() not yet implemented.");
  }

  public void setResultSetName(String resultSetName) {
  throw new java.lang.UnsupportedOperationException("Method setResultSetName() not yet implemented.");
  }

  public String getResultSetName() {
  throw new java.lang.UnsupportedOperationException("Method getResultSetName() not yet implemented.");
  }

/**
 * Method doesn't do anything. (Single database name is loaded
 *  from XML configuration file: <code>config.xml</code>)
 *@param  database (not used)
 *
 */
  public void setDatabases(String database) {

  }

/**
   * Method doesn't do anything. (Single database name is loaded
   *  from XML configuration file: <code>config.xml</code>)
   *@param  databases (not used)
   *
 */
  public void setDatabases(String[] databases) {

  }


/**
 * Returns an array containing the name of the single database that can be searched. Database name is loaded
 *  from XML configuration file: <code>config.xml</code>)
 * @returns name of database that can be searched.
 */
  public String[] getDatabases() {

    return new String[] {this.currentDatabase};
  }

  public void setParseQuery(boolean parseQuery) {
    throw new java.lang.UnsupportedOperationException("Method setParseQuery() not yet implemented.");
  }

  public boolean isParseQuery() {
    throw new java.lang.UnsupportedOperationException("Method isParseQuery() not yet implemented.");
  }

  public int submitQuery(Object query) throws JaferException {

    if (query instanceof Node)
      return submitQuery((Node)query);
    else if (query instanceof RPNQuery)
      return submitQuery((RPNQuery)query);
    else
      throw new JaferException("Only queries of type Node or RPNQuery accepted. (See www.jafer.org)");
  }

  public int submitQuery(RPNQuery query) throws JaferException {
    org.jafer.query.RPNQuery rpnQuery = new org.jafer.query.RPNQuery(query);
    return submitQuery(rpnQuery.toJaferQuery().getQuery());
  }

  public int submitQuery(Node query) throws JaferException {
  /** Subclasses will need to override this if ResultSet in use is scrollable,
  and a ResultSet is to be re-used for Present operation. */
/** @todo more specific error message: when connection isn't made... */
    if (dataSource == null)
      configureDataSource();

    this.query = query;
    setQueryString("select count(*)");

    try {
      System.out.println(getQueryString());
      ResultSet results = getStatement().executeQuery(getQueryString());
      logger.log(Level.FINE, "submitQuery(): "+getQueryString());
      results.next();
      nResults = results.getInt(1);

      return nResults;
    }
    catch (SQLException ex) {
//      throw new JaferException("Error in database connection, or in generation/execution of SQL statement", ex);
      throw new JaferException(ex.getMessage());
    }
  }

  public void saveQuery(String filePath) throws JaferException {

    if (getQuery() instanceof Node)
      XMLSerializer.out((Node)getQuery(), "xml", filePath);
    /** @todo more? */
  }

  public int getNumberOfResults() {

    return nResults;
  }

  public int getNumberOfResults(String databaseName) {
/** @todo do something else here... */
    return nResults;
  }

  public Object getQuery() {

    return query;
  }

/**
 * Executes search on database using current query.
 */
  protected void search() throws SQLException, JaferException {

    setQueryString("");
    System.out.print(getQueryString());
    resultSet = getStatement().executeQuery(getQueryString());
    logger.log(Level.FINE, "search(): "+getQueryString());
  }

/**
 * Gets a conection from the DataSource in use.
 */
  protected Connection getConnection() throws SQLException {
/** @todo test to see if timed out? */
    if (connection != null)
      return connection;

    connection = dataSource.getConnection();
    return connection;
  }

/**
 * Gets a new Statement, which can be modified in subclasses.
 */
  protected Statement getStatement() throws SQLException {
 /** Subclasses will need to override if a scrollable ResultSet is not available: */
    return getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					    ResultSet.CONCUR_UPDATABLE);
  }

/**
 * Transforms XML query to SQL string via stylesheet.
 */
  protected void setQueryString(String selectPhrase) throws JaferException {

    StringWriter writer = new StringWriter();
    paramMap.put("selectStatement", selectPhrase);
    paramMap.put("primaryKey", primaryKey);
    paramMap.put("primaryTable", primaryTable);
    paramMap.put("foreignKey", foreignKey);

    Element root = query.getOwnerDocument().createElement("root");
/** @todo handle cases where ownerDocument is null... */
    root.appendChild(query);
    try {
      XMLSerializer.transformOutput(root, queryXSLT, paramMap, writer);
    }
    catch (JaferException ex) {
      throw new JaferException(ex.getMessage(), ex);
//      throw new JaferException("Error in transformation of XML query to SQL string.", ex);
    }
    queryString = writer.toString();
  }

  protected String getQueryString() {

    return queryString;
  }

  private Field createRecord() throws SQLException, JaferException {

    Node templateNode = recordTemplate.getDocumentElement();
    Node fieldNode = processTemplate(templateNode);

    Element rootNode = recordDocument.createElement("record");
    rootNode.appendChild(fieldNode);

    try {
      recordSchema = getAttributeValue(templateNode, "xmlns");
      rootNode.setAttribute("schema", recordSchema);
      rootNode.setAttribute("syntax", Config.getRecordSyntax(recordSchema));
//      rootNode.appendChild(recordDocument.importNode(getQuery(), true));
    }
    catch (NullPointerException ex) {
      throw new JaferException("No \"xmlns\" attribute found in record template: " + templateNode.getNodeName());
    }

    rootNode.setAttribute("dbName", getCurrentDatabase());
    rootNode.setAttribute("number", Integer.toString(getRecordCursor()));

    return new Field(rootNode, fieldNode);
  }


  private Node processTemplate(Node template)  throws SQLException, JaferException {

    Node fieldNode = processNode(template).item(0);
    ((Element)fieldNode).removeAttribute("xmlns:jafer");

    return fieldNode;
  }


  private NodeSet processNode(Node node) throws SQLException, JaferException {

    Node child, newNode;
    NodeList list = node.getChildNodes();

    NodeSet childSet = new NodeSet();
    for (int i=0; i < list.getLength(); i++) {
      child = list.item(i);
      if (child.getNodeType() != Node.TEXT_NODE)
	childSet.addNodes(processNode(child));
    }

    if (node.getNodeName().equals("jafer:insertElement"))
      return processJaferElement(node, childSet);
    else if (node.getNodeName().equals("jafer:insertData"))
      return processJaferData(node);

    else {
      newNode = recordDocument.importNode(node, false);
      for (int i = 0; i < childSet.getLength(); i++) {
	newNode.appendChild(childSet.item(i));
      }
      return new NodeSet(newNode);
    }
  }

  private NodeSet processJaferElement(Node jaferNode, NodeSet childSet) throws JaferException, SQLException {
/** @todo use String[] instead of Vector?
    Throw exception if no column name found, and handle in processNode()? */
    NodeSet set = new NodeSet();
    Vector data;
    Node newNode, textNode, copy;

    newNode = recordDocument.createElement(getAttributeValue(jaferNode, "name"));
    NamedNodeMap map = jaferNode.getAttributes();
    for (int j = 0; j < map.getLength(); j++) {
      if (map.item(j).getNodeName() != "column" && map.item(j).getNodeName() != "name")
	((Element)newNode).setAttribute(map.item(j).getNodeName(), map.item(j).getNodeValue());
    }

    for (int k = 0; k < childSet.getLength(); k++)
	  newNode.appendChild(childSet.item(k));

    String columnName = getAttributeValue(jaferNode, "column");

    if (columnName != null && !columnName.equals("")) {
      data = processInsert(columnName);
      String text;
      for (int i = 0; i < data.size(); i++) {
        copy = newNode.cloneNode(true);
        text = (data.get(i) == null) ? "" : String.valueOf(data.get(i));
        if (copy.getNodeName().equalsIgnoreCase("fixfield")) // fixfields need to be enclosed in quotes
          textNode = recordDocument.createTextNode("\""+text+"\"");
        else
          textNode = recordDocument.createTextNode(text);
        copy.appendChild(textNode);
        set.addElement(copy);
      }
    }
    else
      set.addElement(newNode);

    return set;
  }

  private NodeSet processJaferData(Node jaferNode) throws JaferException, SQLException {
/** @todo use String[] instead of Vector?
    Throw exception if no column name found, and handle in processNode() */
    Vector data;
    String columnName, fieldData = "", delimiter = "";
    Node textNode;

    columnName = getAttributeValue(jaferNode, "column");

    if (columnName != null && !columnName.equals("")) {
      data = processInsert(columnName);
      if (getAttributeValue(jaferNode, "delimiter") != null)
        delimiter = getAttributeValue(jaferNode, "delimiter");

      for (int i = 0; i < data.size(); i++) {
        if (data.get(i) != null && !data.get(i).equals("")) {
          if (!fieldData.equals(""))
            fieldData += delimiter;
          fieldData += String.valueOf(data.get(i));
        }
      }
    }
    else
      logger.log(Level.WARNING, "No column name found in record template for insertData element.");
//    throw new JaferException("No column name found in record template for insertData element.");

    textNode = recordDocument.createTextNode(fieldData);
    return  new NodeSet(textNode);
  }

  protected Vector processInsert(String columnName) throws SQLException, JaferException {
/** @todo use String[] instead of Vector? */
    Vector data = new Vector();
    String tableName, pKey, sql;
    ResultSet results; /** @todo optimize: re-use ResultSet, need to clear when updating cursor */

    if (columnName != null && !columnName.equals("") && alignCursor()) {// i.e. no value in <jafer:insert> column attribute
      pKey = resultSet.getString(1);
      if (columnName.indexOf('.') < 0)
        throw new JaferException("Column name must be specified in record template as tablename.columnname");

      tableName = columnName.substring(0, columnName.indexOf('.'));

      if (tableName.equalsIgnoreCase(primaryTable))
        sql = "select " +columnName+ " as '" + columnName + "' from " + tableName + " where " + primaryKey + " = " + pKey;
      else
        sql = "select " +columnName+ " as '" + columnName + "' from " + tableName + " where " + foreignKey + " = " + pKey;
      try {
        results = getStatement().executeQuery(sql);
      }
      catch (SQLException ex) {
    /** @todo more detail in message: */
	logger.log(Level.WARNING, "Error in retrieving data from database, column skipped: "+ columnName, ex);
        return data;
      }
      while (results.next())
	data.add(results.getString(columnName));
    }
    return data;
  }

  protected boolean alignCursor() throws SQLException, JaferException {
// subclass needs to override this if absolute() not supported by jdbc driver in use.
    return resultSet.absolute(getRecordCursor());
  }

// CONFIGURATION methods: //
/**
 * Loads XML config file, sets available schemas (and default schema).<br/>
 * Loads query transform stylesheet.<br/>
 * Loads database name, user name and password for JDBC conection from XML config file.<br/>
 */

  private void initialise() throws JaferException {

    URL configFile = this.getClass().getClassLoader().getResource(CONFIG_FILE);
    configDocument = DOMFactory.parse(configFile);

    queryXSLT = this.getClass().getClassLoader().getResource(QUERY_XSLT);
    paramMap = new Hashtable();

    recordDocument = DOMFactory.newDocument();

    setDatabaseConfiguration();

    recordSchemas = new Hashtable();
    loadRecordSchemas();
  }

  private void setDatabaseConfiguration() throws JaferException {

    currentDatabase = getXMLConfigValue("config/database/@name");
    username = getXMLConfigValue("config/database/@username");
    password = getXMLConfigValue("config/database/@password");
/** @todo identify primary table if more than one table listed in xml config... */
    primaryTable = getXMLConfigValue("config/database/tables/table/@name");
    primaryKey = getXMLConfigValue("config/database/tables/table/@primaryKey");
    foreignKey = getXMLConfigValue("config/database/tables/table/@foreignKey");
  }


  abstract protected void configureDataSource() throws JaferException ;


  private void loadRecordSchemas() throws JaferException {

    Node child;
    String schema, location;

    NodeList list = selectNodeList(configDocument, "config/recordTemplates/template");
    for (int i=0; i<list.getLength(); i++) {
      child = list.item(i);
      schema = getAttributeValue(child, "schema");
      location = getAttributeValue(child, "location");
      recordSchemas.put(schema, location);
    }
  }

  private void setRecordTemplate(String schema) {

    String filePath = (String)recordSchemas.get(schema);
    URL template = this.getClass().getClassLoader().getResource(filePath);
    try {
      recordTemplate = DOMFactory.parse(template);
    }
    catch (JaferException ex) {
      logger.log(Level.WARNING, "Problem loading XML record template for schema: " + schema);
    }
  }

// UTILITY methods: //

  protected String getXMLConfigValue(String XPath) throws JaferException {

    if (selectNode(configDocument, XPath) != null)
     return selectNode(configDocument, XPath).getNodeValue();

    return "";
  }


  private Node selectNode(Node sourceNode, String XPath) throws JaferException {

    Node node;
    try {
      node = cachedXPath.selectSingleNode(sourceNode, XPath);
    }
    catch (TransformerException ex) {
      throw new JaferException("Error in accessing XML configuration information");
    }

    return node;
  }

  private NodeList selectNodeList(Node sourceNode, String XPath) throws JaferException {

    NodeList list;
    try {
      list = cachedXPath.selectNodeList(sourceNode, XPath);
    }
    catch (TransformerException ex) {
      throw new JaferException("Error in accessing XML configuration information");
    }

    return list;
  }

  private String getAttributeValue(Node node, String attName) {

    if (node.getAttributes().getNamedItem(attName) != null)
      return node.getAttributes().getNamedItem(attName).getNodeValue();

    return null;
  }
}
