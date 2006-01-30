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
public class PresentException extends JaferException
{

    /**
     * Stores a reference to the status code of 1 for STATUS_REQUEST_TERMINATED
     */
    public static final int STATUS_REQUEST_TERMINATED = 1;

    /**
     * Stores a reference to the status code of 2 for STATUS_TO_MANY_RECORDS
     */
    public static final int STATUS_TO_MANY_RECORDS = 2;

    /**
     * Stores a reference to the status code of 3 for STATUS_ORIGIN_FAILURE
     */
    public static final int STATUS_ORIGIN_FAILURE = 3;

    /**
     * Stores a reference to the status code of 4 for STATUS_TARGET_FAILURE
     */
    public static final int STATUS_TARGET_FAILURE = 4;

    /**
     * Stores a reference to the status code of 5 for STATUS_TERMINAL_FAILURE
     */
    public static final int STATUS_TERMINAL_FAILURE = 5;

    /**
     * Stores a reference to ststus of this execptin
     */
    private int status;

    /**
     * Stores a reference to the number of records that the search returned
     */
    private int numberOfRecordsReturned;

    /**
     * Stores a reference to the diagnostics that were reported
     */
    private Diagnostic[] diagnostics;

    /**
     * Constructor
     * 
     * @param status the status number for this execption
     * @param numberOfRecordsReturned The number of records that were returned
     * @param diagnostics The diagnostics that were returned
     * @param message The detailed message string for the execption
     */
    public PresentException(int status, int numberOfRecordsReturned, Diagnostic[] diagnostics, String message)
    {

        super(message);
        this.status = status;
        this.numberOfRecordsReturned = numberOfRecordsReturned;
        this.diagnostics = diagnostics;
    }

    /**
     * Constructor
     * 
     * @param status the status number for this execption
     * @param numberOfRecordsReturned The number of records that were returned
     * @param message The detailed message string for the execption
     */
    public PresentException(int status, int numberOfRecordsReturned, String message)
    {

        super(message);
        this.status = status;
        this.numberOfRecordsReturned = numberOfRecordsReturned;
    }

    /**
     * Constructor
     * 
     * @param status the status number for this execption
     * @param numberOfRecordsReturned The number of records that were returned
     * @param message The detailed message string for the execption
     * @param cause The exception that caused this PresentException to be
     *        created
     */
    public PresentException(int status, int numberOfRecordsReturned, String message, Throwable cause)
    {

        super(message, cause);
        this.status = status;
        this.numberOfRecordsReturned = numberOfRecordsReturned;
    }

    /**
     * Returns the status of this execption
     * 
     * @return The status code that matches the ststus constants
     */
    public int getStatus()
    {

        return status;
    }

    /**
     * Returns the number of records that were returned by the search
     * 
     * @return
     */
    public int getNumberOfRecordsReturned()
    {

        return numberOfRecordsReturned;
    }

    /**
     * Returns the diagnostics for this execption
     * 
     * @return An array of diagnostics
     */
    public Diagnostic[] getDiagnostics()
    {

        return diagnostics;
    }

    /**
     * Returns a boolean to indicate if this exception has diagnostic
     * information
     * 
     * @return true if diagnostics are contained in this execption
     */
    public boolean hasDiagnostics()
    {

        return (diagnostics != null && diagnostics.length > 0);
    }
}