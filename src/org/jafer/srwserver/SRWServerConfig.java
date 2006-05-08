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
package org.jafer.srwserver;

import java.io.InputStream;
import java.util.logging.Logger;

import org.jafer.databeans.DatabeanManagerFactory;
import org.jafer.exception.JaferException;
import org.jafer.util.Config;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Node;

/**
 * This class extracts the config information for the SRWServer from the
 * specified config file
 */
public class SRWServerConfig
{

    /**
     * Stores a reference to the logger
     */
    protected static Logger logger = Logger.getLogger("org.jafer.srwserver");

    /**
     * Stores a reference to the default schema to return records as if not set
     * in the request.
     */
    private String defaultSchema;

    /**
     * Stores a reference to the default max records to set in the request if
     * not currently set.
     */
    private String defaultMaxRecords;

    /**
     * Stores a reference to the config root
     */
    private Node configRoot = null;

    /**
     * Stores a reference to highest supported version by this SRWServer
     */
    private double highestSupportedSearchVersion;

    /**
     * Initialises the SRWServerConfig parsing in the details from
     * the specified xml
     * 
     * @param xml The XML to process
     * @throws JaferException
     */
    public void initialiseFromXML(String xml) throws JaferException
    {
        logger.fine("Loading configuration details for SRWSever DatabeanManagerFactory");

        configRoot = DOMFactory.parse(xml).getDocumentElement();
        initialise();
    }
    
    /**
     * Initilalises the SRWServerConfig parsing in the details from the specified
     * config file
     * 
     * @param resourceLocation The location of the resource in the distribution
     *        that will be retrieved as a stream using
     *        class.getResourceAsStream() to load the srwserver config details
     * @throws JaferException
     */
    public void initialiseFromResourceStream(String resourceLocation) throws JaferException
    {
        logger.fine("Loading configuration details for SRWSever");

        // load and parse the config file
        InputStream configStream = SRWServer.class.getResourceAsStream(resourceLocation);
        // make sure we found the config file
        if (configStream == null)
        {
            throw new JaferException("Unable to locate srwserver config file: " + resourceLocation);
        }
        configRoot = DOMFactory.parse(configStream).getDocumentElement();
        initialise();
    }

    private void initialise() throws JaferException
    {
        // extract the default schema value from the config
        defaultSchema = Config.getValue(Config.selectSingleNode(configRoot, "defaultschema"));
        // make sure we loaded a correct value
        if (defaultSchema == null)
        {
            throw new JaferException("Unable to locate default schema in config file");
        }
        String version = Config.getValue(Config.selectSingleNode(configRoot, "highestsearchversion"));
        // make sure we loaded a correct value
        if (version == null)
        {
            throw new JaferException("Unable to locate highest supported search version in config file");
        }
        highestSupportedSearchVersion = Double.parseDouble(version);

        String maxRecords = Config.getValue(Config.selectSingleNode(configRoot, "defaultmaxrecords"));
        // make sure we loaded a correct value
        if (maxRecords == null)
        {
            throw new JaferException("Unable to locate default max records in config file");
        }
        defaultMaxRecords = maxRecords;
    }

    /**
     * Returns the default maximum records value to set in a request if it's not
     * specified
     * 
     * @return Returns the defaultMaxRecords.
     */
    public String getDefaultMaxRecords()
    {
        return defaultMaxRecords;
    }

    /**
     * Returns the default schema to use if not specified in requests
     * 
     * @return Returns the defaultSchema.
     */
    public String getDefaultSchema()
    {
        return defaultSchema;
    }

    /**
     * Returns the highest version number supported by the SRWServer
     * 
     * @return Returns the highestSupportedSearchVersion.
     */
    public double getHighestSupportedSearchVersion()
    {
        return highestSupportedSearchVersion;
    }

    /**
     * Returns the diagnostic message for the specified code
     * 
     * @param code
     * @return
     * @throws JaferException
     */
    public String getDiagnosticMessaage(String code) throws JaferException
    {
        return Config.getValue(Config.selectSingleNode(configRoot, "diagnostics/diagnostic[@code='" + code + "']"));
    }
}
