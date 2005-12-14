/**
 * JAFER Toolkit Project. Copyright (C) 2002, JAFER Toolkit Project, Oxford
 * University. This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jafer.registry.uddi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.Result;

/**
 * This is the base class for all the Jafer UDDI Exceptions that can occur
 */
public class RegistryExceptionImpl extends org.jafer.registry.RegistryException
{

    /**
     * Stores a reference to the result if available
     */
    private Result result = null;

    /**
     * Stores a reference to the whether an extra message was added other than
     * just the exception. Used to print string correctly
     */
    private boolean hasMessage  = false;

    /**
     * Constructor supplying a message
     * 
     * @param message
     */
    public RegistryExceptionImpl(String message)
    {
        super(message);
        hasMessage = true;
    }

    /**
     * Constructor supplying a message
     * 
     * @param exc
     */
    public RegistryExceptionImpl(Exception exc)
    {
        super(exc);
        extractResult(exc);
    }

    /**
     * Constructor supplying a message and exception
     * 
     * @param message
     * @param exc
     */
    public RegistryExceptionImpl(String message, Exception exc)
    {
        super(message, exc);
        extractResult(exc);
        hasMessage = true;
    }

    /**
     * finds out if Exception is UDDI related and extracts the first result
     * object
     * 
     * @param exc The exception to examine
     */
    private void extractResult(Exception exc)
    {
        // we have to have a UDDI exception to extract result object
        if (exc instanceof org.uddi4j.UDDIException)
        {
            org.uddi4j.UDDIException uddiExc = (org.uddi4j.UDDIException) exc;
            DispositionReport report = uddiExc.getDispositionReport();
            // check we were given a valid report
            if ((report != null) && (report.getResultVector().size() > 0))
            {
                result = (Result) report.getResultVector().firstElement();
            }
        }
    }

    /**
     * Checks if the DispositionReport contains the specified code
     * 
     * @param report The DispositionReport to search
     * @param code code to search for
     * @return boolean true if code found in report
     */
    public static boolean isErrorOfType(DispositionReport report, String code)
    {
        // check we were given a valid report
        if (report != null)
        {
            // get all the results, typically this is only one
            Vector results = report.getResultVector();
            for (int i = 0; i < results.size(); i++)
            {
                Result r = (Result) results.elementAt(i);
                // make sure we have error information for the result
                if (r.getErrInfo() != null)
                {
                    // return if we find a match
                    return r.getErrInfo().getErrCode().equals(code);
                }
            }
        }
        return false;
    }

    /**
     * Returns the UDDI defined error number
     * 
     * @return The erro rnumber
     */
    public String getErrorNumber()
    {
        if (result != null)
        {
            return result.getErrno();
        }
        return "";
    }

    /**
     * Returns the UDDI defined code
     * 
     * @return The code
     */
    public String getErrorCode()
    {
        if ((result != null) && (result.getErrInfo() != null))
        {
            return result.getErrInfo().getErrCode();
        }
        return "";
    }

    /**
     * Returns the UDDI defined error text
     * 
     * @return The error Text
     */
    public String getErrorText()
    {
        if ((result != null) && (result.getErrInfo() != null))
        {
            return result.getErrInfo().getText();
        }
        return "";
    }

    /**
     * This method returns the error message with a full stack trace. This is
     * done by calling printStackTrace on Exception that internally calls
     * toString() to get our message.
     * 
     * @return The full stack trace string of the exception
     */
    public String getStackTraceString()
    {

        String stackString = "";
        try
        {
            // need to use a print writer to store stack information
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            // this calls toString internally.
            printStackTrace(pw);
            stackString = sw.toString();
        }
        catch (Exception exc)
        {
            stackString = exc.getMessage();
        }
        return stackString;
    }

    /**
     * This method returns the String message for this Exception adding in the
     * Result details if available
     * 
     * @return String formated (ErrNo= XX Code=XX Error Text)
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        if (getErrorNumber().length() > 0)
        {
            buf.append(" ErrNo=");
            buf.append(getErrorNumber());
        }
        if (getErrorCode().length() > 0)
        {
            buf.append(" Code=");
            buf.append(getErrorCode());

        }
        if ((getErrorText().length() > 0) && (hasMessage))
        {
            buf.append(" ");
            buf.append(getErrorText());
        }

        return buf.toString();
    }
}
