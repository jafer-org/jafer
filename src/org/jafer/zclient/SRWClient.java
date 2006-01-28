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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jafer.exception.JaferException;
import org.jafer.util.Config;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Node;

/**
 * This class represents a connection to an SRWClient
 */
public class SRWClient extends AbstractClient
{

    /**
     * Stores a reference to XPATH to locate the protocol information
     */
    private static final String PROTOCOL_XPATH = "/*[local-name() = 'explainResponse']/*[local-name() = 'record']/"
            + "*[local-name() = 'recordData']/*[local-name() = 'explain']/*[local-name() = 'serverInfo']/@protocol";

    /**
     * Constructor
     */
    public SRWClient()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.AbstractClient#createSession()
     */
    protected Session createSession()
    {
        // default the protocol to be SRW for the case when no explain record
        // can be found
        String protocol = "SRW";
        HttpURLConnection connection = null;

        try
        {
            logger.fine("Locating Explain record to detect SRW / SRU protocol support");
            // Performing a straight httpGet request on the URL will return a
            // zeerex explain record that can be interogated to determine the
            // support type at the following XPath:
            // /explain/serverInfo@protocol.
            // <br>
            // This attribute can have three values SRW , SRU , SRW/U
            // If no XML is returned from the HttpGet then SRW support is
            // assumed
            URL sessionURL = new URL(this.getHost());
            connection = (HttpURLConnection) sessionURL.openConnection();
            connection.setRequestMethod("GET");
            // send the GET request
            connection.connect();
            // Read the input stream to get the returned XML
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer xml = new StringBuffer();
            String inputLine;
            // loop round reading each line until non are left
            while ((inputLine = in.readLine()) != null)
            {
                // append the line to the xml buffer
                xml.append(inputLine);
                // add space instead of newline so not to corrupt XML
                xml.append(" ");
            }

            logger.fine("Parsing Explain record to detect SRW / SRU protocol support");
            // parse the returned XML into a document
            Node node = (Node) DOMFactory.parse(xml.toString()).getDocumentElement();
            // extract the protocol from the XML
            protocol = Config.getValue(Config.selectSingleNode(node, PROTOCOL_XPATH));
            logger.fine("Found Protocol: " + protocol);

        }
        catch (MalformedURLException exc)
        {
            // The host URL may be invalid so will assume SRW for now as the
            // error handling later on will trap this properly
            logger.warning("MalformedURLException trying to send GET request assuming SRW: " + exc.getMessage());
        }
        catch (IOException exc)
        {
            // The host URL may be invalid so will assume SRW for now as the
            // error handling later on will trap this properly
            logger.warning("IOException trying to send GET request assuming SRW: " + exc.getMessage());
        }
        catch (JaferException exc)
        {
            // The returned response may not be XML or can not be parsed so will
            // assume SRW
            logger.warning("JaferException parsing the returned XML assuming SRW: " + exc.getMessage());
        }
        finally
        {
            if (connection != null)
            {
                // ensure we close the connection
                connection.disconnect();
            }
        }
        // If the host only supports SRU then we must use the SRUBinding
        // otherwise SRWBinding takes priority for SRW and SRW/U values
        if (protocol != null && protocol.equalsIgnoreCase("SRU"))
        {
            logger.fine("Creating SRU Binding");
            return new SRWSession(new SRUBinding(this.getHost()));
        }
        else
        {
            logger.fine("Creating SRW Binding");
            return new SRWSession(new SRWBinding(this.getHost()));
        }
    }

}
