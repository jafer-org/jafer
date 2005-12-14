/**
 * JAFER Toolkit Poject.
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

package org.jafer.zclient.operations;

import org.jafer.exception.JaferException;
import org.jafer.record.Diagnostic;

/**
 * Exception thrown if problems retrieving records
 */
public class PresentException extends JaferException {

  private int status, numberOfRecordsReturned;
  private Diagnostic[] diagnostics;

  public PresentException(int status, int numberOfRecordsReturned, Diagnostic[] diagnostics, String message) {

    super(message);
    this.status = status;
    this.numberOfRecordsReturned = numberOfRecordsReturned;
    this.diagnostics = diagnostics;
  }

  public PresentException(int status, int numberOfRecordsReturned, String message) {

    super(message);
    this.status = status;
    this.numberOfRecordsReturned = numberOfRecordsReturned;
  }

  public PresentException(int status, int numberOfRecordsReturned, String message, Throwable cause) {

    super(message, cause);
    this.status = status;
    this.numberOfRecordsReturned = numberOfRecordsReturned;
  }

  public int getStatus() {

    return status;
  }

  public int getNumberOfRecordsReturned() {

    return numberOfRecordsReturned;
  }

  public Diagnostic[] getDiagnostics() {

    return diagnostics;
  }

  public boolean hasDiagnostics() {

    return (diagnostics != null && diagnostics.length > 0);
  }
}