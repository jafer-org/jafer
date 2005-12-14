package org.jafer.zoom;


import org.z3950.zoom.Connection;
import org.z3950.zoom.Query;
import org.z3950.zoom.ResultSet;
import org.z3950.zoom.ScanSet;
import org.z3950.zoom.ResultSetListener;
import org.z3950.zoom.ScanSetListener;

import java.util.Vector;
import java.lang.reflect.InvocationTargetException;

import org.jafer.zclient.ZClient;
import org.jafer.query.CQLQuery;
import org.jafer.query.JaferQuery;

/***
	* Implementation of Zoom Connection class. Sets up a Jafer Zclient and handles
	* interfacing to Jafer for querying and processing results. Note that support
	* is presently limited to CQL queries only. Note that this class is loaded
	* by the Zoom ConnectionFactory class, which looks for the class name in a file
	* called META-INF/services/org.z3950.zoom.Connection in the classpath.
	*/
public class ConnectionImpl implements Connection {

	static final String IMPLEMENTATION_ID = "JaferZoom";
	static final String IMPLEMENTATION_NAME = "Jafer Zoom Implementation";
	static final String IMPLEMENTATION_VERSION = "1.0";
	
	static final String DEFAULT_RECORD_SCHEMA = "http://www.openarchives.org/OAI/oai_marc";

	String host;
	int port;
	java.net.URL connectionURL;
	ZClient zclient = null;
	String recordSchema;
	
	/***
	* Connects to a Jafer z39.50 target. 
	*/
	public void connect() throws SystemException {
		try {
			System.out.println("ConnectionImpl, connedcting, host:"+host+" port:"+port+" connectionURL:"+connectionURL);
			zclient = new ZClient();
			if (presentChunk != 0) zclient.setFetchSize(presentChunk);
			//zclient.setDataCacheSize(jaferTarget.getDataCacheSize());
			zclient.setHost(host);
			zclient.setPort(port);
			zclient.setAutoReconnect(0);
		} catch (Exception e) {
			throw new SystemException(e.toString());
		}
	}

	/***
	* Performs a search against a Jafer connection.
	*/
	public ResultSet search(Query q) throws DiagnosticException, SystemException {
		try {
			System.out.println("ConnectionImpl, search query q:"+q.getValue());
			if (zclient == null) throw new DiagnosticException("Connect has not been called on this Connection.");
			if (databaseNames != null) { 
				zclient.setDatabases(databaseNames);
			} else if (databaseName != null) {
				zclient.setDatabases(databaseName);
			}
			recordSchema = DEFAULT_RECORD_SCHEMA;
			if (preferredRecordSyntax != null) recordSchema = preferredRecordSyntax;
			zclient.setRecordSchema(recordSchema);
			CQLQuery cqlQuery = new CQLQuery((String)q.getValue());
			System.out.println("cqlQuery xml:"+cqlQuery.getXML());
			JaferQuery jaferQuery = cqlQuery.toJaferQuery();
			// call submit
			int nCount = zclient.submitQuery(jaferQuery.getQuery());
			System.out.println("ConnectionImpl, search, nCount:"+nCount);
			ResultSetImpl resultSet = new ResultSetImpl(this, nCount);
			resultSet.setPreferredRecordSyntax(recordSchema);
			return resultSet;
		} catch (Exception e) {
			throw new SystemException(e.toString());
		}
	}


  /**
   * Close the connection.
   */
	public void close() {
		if (zclient != null) {
			try { zclient.close(); } catch (Exception e) {}
		}
	}

  /**
   * Get the Jafer ZClient. Used by RsultSet and Record for record processing.
   */
	public ZClient getZClient() {
		return zclient;
	}
		
  //   Supported By Jafer
	
	public String getImplementationId() {
		return IMPLEMENTATION_ID;
	}
	public String getImplementationName() {
		return IMPLEMENTATION_NAME;
	}
	public String getImplementationVersion() {
		return IMPLEMENTATION_VERSION;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	String preferredRecordSyntax = null;
  public void setPreferredRecordSyntax(String preferredRecordSyntax) {
		this.preferredRecordSyntax = preferredRecordSyntax;
	}
  public String getPreferredRecordSyntax() {
		return preferredRecordSyntax;
	}

	int presentChunk;
  public void setPresentChunk(int presentChunk) {
		this.presentChunk = presentChunk;
	}
  public int getPresentChunk() { 
		return presentChunk;
	}

	int numberOfEntries = -1;
  public void setNumberOfEntries(int numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}
  public int getNumberOfEntries() {
		return numberOfEntries;
	}

	String databaseName;
  public String getDatabaseName() {
		return databaseName;
	}
  public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	String[] databaseNames;
  public String[] getDatabaseNames() {
		return databaseNames;
	}
  public void setDatabaseNames(String[] databaseNames) {
		this.databaseNames = databaseNames;
	}


  // Not Supported By Jafer

	public ScanSet scan(Query q) throws DiagnosticException, SystemException {
		throw new UnsupportedOperationException();
	}

  public String getUser() { throw new UnsupportedOperationException(); }
  public void setUser(String user) { throw new UnsupportedOperationException(); }
  public String getGroup() { throw new UnsupportedOperationException(); }
  public void setGroup(String group) { throw new UnsupportedOperationException(); }
  public String getPassword() { throw new UnsupportedOperationException(); }
  public void setPassword(String password) { throw new UnsupportedOperationException(); }

  public String getProxy() { throw new UnsupportedOperationException(); }
  public void setProxy(String proxy) { throw new UnsupportedOperationException(); }
  public int getMaximumRecordSize() { throw new UnsupportedOperationException(); }
  public void setMaximumRecordSize(int maximumRecordSize) { throw new UnsupportedOperationException(); }
  public int getPreferredRecordSize() { throw new UnsupportedOperationException(); }
  public void setPreferredRecordSize(int preferredRecordSize) { throw new UnsupportedOperationException(); }
  public String getLang() { throw new UnsupportedOperationException(); }
  public void setLang(String lang) { throw new UnsupportedOperationException(); }
  public String getCharset() { throw new UnsupportedOperationException(); }
  public void setCharset(String charset) { throw new UnsupportedOperationException(); }
  public String getTargetImplementationId() { throw new UnsupportedOperationException(); }
  public String getTargetImplementationName() { throw new UnsupportedOperationException(); }
  public String getTargetImplementationVersion() { throw new UnsupportedOperationException(); }
  public boolean isNamedResultSets() { throw new UnsupportedOperationException(); }
  public void setNamedResultSets(boolean namedResultsSets) { throw new UnsupportedOperationException(); }
  /** docs say this piggy back should be in Result Set! */
  public boolean isPiggyBack() { throw new UnsupportedOperationException(); }
  public void setPiggyBack(boolean piggyBack) { throw new UnsupportedOperationException(); }
  public int getSmallSetUpperBound() { throw new UnsupportedOperationException(); }
  public void setSmallSetUpperBound(int smallSetUpperBound) { throw new UnsupportedOperationException(); }
  public int getLargeSetLowerBound() { throw new UnsupportedOperationException(); }
  public void setLargeSetLowerBound(int largeSetLowerBound) { throw new UnsupportedOperationException(); }
  public int getMediumSetPresentNumber() { throw new UnsupportedOperationException(); }
  public void setMediumSetPresentNumber(int mediumSetPresentNumber) { throw new UnsupportedOperationException(); }
  public String getSmallSetElementSetName() { throw new UnsupportedOperationException(); }
  public void setSmallSetElementSetName(String smallSetElementSetName) { throw new UnsupportedOperationException(); }
  public String getMediumSetElementSetName() { throw new UnsupportedOperationException(); }
  public void setMediumSetElementSetName(String mediumSetElementSetName) { throw new UnsupportedOperationException(); }
	public java.net.URL getConnectionURL() { throw new UnsupportedOperationException(); }
	public void setConnectionURL(java.net.URL connectionURL) { throw new UnsupportedOperationException(); }
  public void setElementSetName(String elementSetName) { throw new UnsupportedOperationException(); }
  public String getElementSetName() { throw new UnsupportedOperationException(); }


  // Asynchronous (not in Zoom yet)

  public void addResultSetListener(ResultSetListener listener) { throw new UnsupportedOperationException(); }
  public void removeResultSetListener(ResultSetListener listener) { throw new UnsupportedOperationException(); }
  public void addScanSetListener(ResultSetListener listener) { throw new UnsupportedOperationException(); }
  public void removeScanSetListener(ResultSetListener listener) { throw new UnsupportedOperationException(); }

  public void startSearch(Query q) throws DiagnosticException, SystemException { throw new UnsupportedOperationException(); }
  public void startScan(Query q) throws DiagnosticException, SystemException { throw new UnsupportedOperationException(); }


  public void set(String optionName, Object value) throws NoSuchMethodException,
		InvocationTargetException, IllegalAccessException {
		 throw new UnsupportedOperationException();
	}
  public Object get(String optionName) throws NoSuchMethodException, InvocationTargetException,
      IllegalAccessException {
		 throw new UnsupportedOperationException();
	}

}
