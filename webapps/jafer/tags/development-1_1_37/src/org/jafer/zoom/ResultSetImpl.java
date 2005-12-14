package org.jafer.zoom;

import org.jafer.zclient.ZClient;
import org.jafer.record.Field;

import org.z3950.zoombase.Connection;
import org.z3950.zoom.ResultSet;
import org.z3950.zoom.Record;
import org.z3950.zoom.RecordListener;
import java.lang.reflect.InvocationTargetException;

/***
	* Implementation of Zoom ResultSet class.
	* 
	*/
public class ResultSetImpl implements ResultSet {

	String elementSetName = "";
	String preferredRecordSyntax = "";
	ConnectionImpl conn = null;
	
	int size;

	protected ResultSetImpl(ConnectionImpl connection, int size) {
		this.conn = connection;
		this.size = size;
	}
	
  public int getSize() {
		return size;
	}

	/**
	 * Retrieves a record from a previous query on the connection.
	 */
  public Record getRecord(int i) throws SystemException, DiagnosticException {
		try {
			ZClient zclient = conn.getZClient();
			zclient.setRecordCursor(i);
			// get result as OAI_MARC document
			Field field = zclient.getCurrentRecord();
			String fieldSchema = field.getRecordSchema();
			String fieldSyntax = field.getRecordSyntax();
			System.out.println("getRecord(), i="+i+" fieldSchema:"+fieldSchema+" fieldSyntax:"+fieldSyntax);
			RecordImpl record = new RecordImpl(field.getRoot(), fieldSchema);
			return record;
		} catch (Exception e) {
			throw new SystemException(e.toString());
		}
	}

  public void delete() {
		 throw new UnsupportedOperationException();
	}

  /**
   * options implemented as get... set...
   */
	public String getElementSetName() {
		return elementSetName;
	}
  public void setElementSetName(String elementSetName) {
		this.elementSetName = elementSetName;
	}
  public String getPreferredRecordSyntax() {
		return preferredRecordSyntax;
	}
  public void setPreferredRecordSyntax(String preferredRecordSyntax) {
		this.preferredRecordSyntax = preferredRecordSyntax;
	}

	/**
  * Optional
  */
	public String getSetName() {
		 throw new UnsupportedOperationException();
	}

  /**
   * No explicit delete
   *
   * deletion should be undertaken in the destroy
   */

  /**
  * Not in Zoom
  */
  public String[] getSupportedRecordSyntaxes() {
		 throw new UnsupportedOperationException();
	}

  /**
   *
   * Supported by implemenation
   */
  public String[] getAvailableRecordSyntaxes() {
		 throw new UnsupportedOperationException();
	}

  /**
   * Available from target (subset of above)
   */


  /**
   * Asynchronous (not in Zoom yet)
   */
	public void addRecordListener(RecordListener listener) {
		 throw new UnsupportedOperationException();
	} 
  public void removeRecordListener(RecordListener listener) {
		 throw new UnsupportedOperationException();
	}
  public void startGetRecord(int i) {
		 throw new UnsupportedOperationException();
	}

  public void set(String optionName, Object value) throws NoSuchMethodException,
		InvocationTargetException, IllegalAccessException {
		 throw new UnsupportedOperationException();
	}
			

  public Object get(String optionName) throws NoSuchMethodException, InvocationTargetException,
      IllegalAccessException {
		 throw new UnsupportedOperationException();
	}



}
