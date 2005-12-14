package org.jafer.zoom;

import org.z3950.zoom.Record;
import java.lang.reflect.InvocationTargetException;


/***
	* Implementation of Zoom Record class. The getRecordSyntax() method
	* returns the schema of the result record. The getRawData() method
	* returns a org.w3c.dom.Node object.
	* 
	*/
public class RecordImpl implements Record {

	Object value;
	String recordSyntax;

	public RecordImpl(Object value, String recordSyntax) {
		this.value = value;
		this.recordSyntax = recordSyntax;
	}
	
  public String getRecordDatabase() {
		 throw new UnsupportedOperationException();
	}

	/**
	 * Returns the schema of the result record.
	 */
  public String getRecordSyntax() {
		 return recordSyntax;
	}

	/**
	 * Returns a org.w3c.dom.Node object.
	 */
  public Object getRawData() {
		 return value;
	}

  /**
   * render should be implemented by overriding toString
   */
  public String toString() {
		return value.toString();
	}

  // Optional

  public String getElementSet() {
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
