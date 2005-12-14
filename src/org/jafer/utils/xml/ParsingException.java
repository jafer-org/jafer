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

package org.jafer.utils.xml;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This is the base class for all the Jafer UDDI Exceptions that can occur
 */
public class ParsingException extends Exception
{

    /**
     * Constructor supplying a message
     * 
     * @param message
     */
    public ParsingException(String message)
    {
        super(message);
    }

    /**
     * Constructor supplying a message
     * 
     * @param exc
     */
    public ParsingException(Exception exc)
    {
        super(exc);
    }

    /**
     * Constructor supplying a message and exception
     * 
     * @param message
     * @param exc
     */
    public ParsingException(String message, Exception exc)
    {
        super(message, exc);
    }

    /**
     * This method returns the error message with a full stack trace. This is
     * done by calling printStackTrace on Exception that internally calls
     * toString() to get our message.
     * 
     * @return The full stack trace string of the exception
     */
    public String toStackTraceString()
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

}
