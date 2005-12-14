/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.record;
import org.jafer.exception.JaferException;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class RecordException extends JaferException{

  private Field field;
  private DataObject dataObject;

  public RecordException(String message) {

    super(message);
  }

  public RecordException(String message, Throwable e) {

    super(message, e);
  }

  public RecordException(String message, Field field) {

    super(message);
    this.field = field;
  }

  public RecordException(String message, DataObject dataObject) {

    super(message);
    this.dataObject = dataObject;
  }

  public Field getRecord() {

    return field;
  }

  public DataObject getDataObject() {
//       if (OID.equals(oid, OID.DIAG_BIB1)) convert to Diagnostic and return?
    return dataObject;
  }
}