/*
 * MDC Desktop Client Created on 28-Jun-2005
 */
package org.jafer.registry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * This class loads and stores the configuration properties
 */
public class UDDITestConfig
{

    /**
     * Stores a reference to the singleton instance
     */
    private static UDDITestConfig instance = new UDDITestConfig();

    /**
     * Stores a reference to the configuration filename
     */
    public final static String UDDI_CONFIG = "udditest.properties";

    //  property file lookups
    public final static String UDDI_LEVEL = "level";

    public final static String UDDI_INQUIRE_URL = "inquire";

    public final static String UDDI_PUBLISH_URL = "publish";

    public final static String UDDI_USERNAME = "username";

    public final static String UDDI_CREDENTIAL = "credential";

    /**
     * Stores a reference to the properties loaded
     */
    private Properties config = new Properties();

    /**
     * Private constructor
     */
    private UDDITestConfig()
    {
        // attempt to load configuration
        loadConfig();
    }

    /**
     * Get an instance of the MDC configuratiion settings
     *
     * @return The static instance of MDCConfig
     */
    public static UDDITestConfig getInstance()
    {
        return instance;
    }

    /**
     * Loads the MDC application config properites in from file
     *
     * @return true if file loaded ok
     */
    public boolean loadConfig()
    {
        boolean loaded = false;

        try
        {
            // open the properties file
            FileInputStream instream = new FileInputStream(UDDI_CONFIG);
            config.load(instream);
            instream.close();
            loaded = true;
        }
        catch (FileNotFoundException e)
        {
            System.err.println("PROPERTIES FILE NOT FOUND");
        }
        catch (IOException e)
        {
            System.err.println("PROPERTIES FILE LOAD IO EXCEPTION");
            e.printStackTrace();
        }
        return loaded;
    }


    /**
     * Returns the property specified by the key in the configuration settings
     *
     * @param key The key to search for
     * @return The value or null if not found
     */
    public String getProperty(String key)
    {
        return config.getProperty(key);
    }

    /**
     * Returns the property specified by the key in the configuration settings
     *
     * @param key The key to search for
     * @param defaultValue The default value returned if not found
     * @return The value or null if not found
     */
    public String getProperty(String key, String defaultValue)
    {
        return config.getProperty(key, defaultValue);
    }


}
