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
package org.jafer.databeans;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import org.jafer.exception.JaferException;
import org.jafer.interfaces.DatabeanFactory;
import org.jafer.record.CacheFactory;
import org.jafer.srwserver.SRWServer;
import org.jafer.util.Config;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class extracts the config information for a DatabeanManagerFactory from
 * the specified config file
 */
public class DatabeanManagerFactoryConfig
{

    /**
     * Stores a reference to the logger
     */
    protected static Logger logger = Logger.getLogger("org.jafer.srwserver");

    /**
     * Stores a reference to the config root
     */
    private Node configRoot = null;

    /**
     * Stores a reference to the data bean manager factory to use
     */
    private DatabeanManagerFactory databeanManagerFactory = null;

    /**
     * Constructs the DatabeanManagerFactoryConfig parsing in the details from
     * the specified config file
     * 
     * @param resourceLocation The location of the resource in the distribution
     *        that will be retrieved as a stream using
     *        class.getResourceAsStream() to load the databeanmanager config
     *        details
     * @throws JaferException
     */
    public DatabeanManagerFactoryConfig(String resourceLocation) throws JaferException
    {
        logger.fine("Loading configuration details for SRWSever DatabeanManagerFactory");

        databeanManagerFactory = new DatabeanManagerFactory();

        // load and parse the config file
        InputStream configStream = SRWServer.class.getResourceAsStream(resourceLocation);
        // make sure we found the config file
        if (configStream == null)
        {
            throw new JaferException("Unable to locate databeanmanagerfactory config file: " + resourceLocation);
        }
        configRoot = DOMFactory.parse(configStream).getDocumentElement();

        String dbManagerName = Config.getValue(Config.selectSingleNode(configRoot, "databeanmanager/@name"));
        // make sure we loaded a correct value
        if (dbManagerName == null || dbManagerName.length() == 0)
        {
            throw new JaferException("Unable to locate databean manager name in config file");
        }
        databeanManagerFactory.setName(dbManagerName);

        String mode = Config.getValue(Config.selectSingleNode(configRoot, "databeanmanager/@mode"));
        // make sure we loaded a correct value
        if (mode == null)
        {
            logger.warning("Could not find mode in configuration file setting to parallel");
            databeanManagerFactory.setMode(DatabeanManagerFactory.MODE_PARALLEL);
        }
        else if (mode.equalsIgnoreCase(DatabeanManagerFactory.MODE_PARALLEL))
        {
            databeanManagerFactory.setMode(DatabeanManagerFactory.MODE_PARALLEL);
        }
        else if (mode.equalsIgnoreCase(DatabeanManagerFactory.MODE_SERIAL))
        {
            databeanManagerFactory.setMode(DatabeanManagerFactory.MODE_SERIAL);
        }
        else
        {
            logger.warning("Mode has invalid value in configuration file setting to parallel");
            databeanManagerFactory.setMode(DatabeanManagerFactory.MODE_PARALLEL);
        }

        // see if we have a cache factory defined
        Node cacheFactoryRoot = Config.selectSingleNode(configRoot, "databeanmanager/cachefactory");
        if (cacheFactoryRoot != null)
        {
            logger.fine("Configuring cache factory for SRWSever DatabeanManagerFactory");
            String cacheFactoryClassName = Config.getValue(Config.selectSingleNode(cacheFactoryRoot, "@class"));

            if (cacheFactoryClassName == null || cacheFactoryClassName.length() == 0)
            {
                throw new JaferException("Unable to locate cacheFactoryClassName in config file");
            }

            String initialSize = Config.getValue(Config.selectSingleNode(cacheFactoryRoot, "initialsize"));
            CacheFactory factory = null;
            try
            {
                // do we need to initialise it's size
                if (initialSize == null || initialSize.length() == 0)
                {
                    logger.fine("Using CacheFactory default constructor");
                    factory = (CacheFactory) Class.forName(cacheFactoryClassName).newInstance();
                }
                else
                {
                    Constructor constructor = Class.forName(cacheFactoryClassName).getConstructor(new Class[] { int.class });
                    if (constructor != null)
                    {
                        logger.fine("Using CacheFactory(int) constructor");
                        factory = (CacheFactory) constructor.newInstance(new Object[] { Integer.valueOf(initialSize) });
                    }
                    else
                    {
                        logger.warning("Unable to find CacheFactory(int) constructor using default");
                        factory = (CacheFactory) Class.forName(cacheFactoryClassName).newInstance();
                    }
                }
            }
            catch (Exception exc)
            {
                String msg = "Unable to construct class " + cacheFactoryClassName;
                logger.severe("msg" + exc.getMessage());
                throw new JaferException(msg, exc);
            }
            // set the cache factory
            databeanManagerFactory.setCacheFactory(factory);
        }
        logger.fine("Configuring URL factory for SRWSever DatabeanManagerFactory");

        NodeList factories = Config.selectNodeList(configRoot, "databeanmanager/factories/*");
        if (factories == null || factories.getLength() == 0)
        {
            throw new JaferException("No factory information defined for SRWSever DatabeanManagerFactory");
        }

        DatabeanFactory[] databeanFactories = new DatabeanFactory[factories.getLength()];
        String name, url, factoryClassName = "";
        // process eache factory
        for (int index = 0; index < factories.getLength(); index++)
        {
            try
            {
                Node factoryNode = factories.item(index);
                name = Config.getValue(Config.selectSingleNode(factoryNode, "@name"));
                url = Config.getValue(Config.selectSingleNode(factoryNode, "@url"));
                factoryClassName = Config.getValue(Config.selectSingleNode(factoryNode, "@class"));

                if (name == null || name.length() == 0)
                {
                    throw new JaferException("Factory[" + index + "] does not have a name attribute defined");
                }
                if (url == null || url.length() == 0)
                {
                    throw new JaferException("Factory[" + index + "] does not have a url attribute defined");
                }
                if (factoryClassName == null || factoryClassName.length() == 0)
                {
                    throw new JaferException("Factory[" + index + "] does not have a factory class attribute defined");
                }

                logger.fine("Creating Factory " + name + " " + factoryClassName + "(" + url + ")");

                Constructor constructor = Class.forName(factoryClassName).getConstructor(new Class[] { String.class });
                if (constructor == null)
                {
                    String msg = "Unable to find Factory(string) constructor";
                    logger.severe(msg);
                    throw new JaferException(msg);
                }
                databeanFactories[index] = (DatabeanFactory) constructor.newInstance(new Object[] { url });
            }
            catch (Exception exc)
            {
                String msg = "Unable to construct url factory class " + factoryClassName;
                logger.severe("msg" + exc.getMessage());
                throw new JaferException(msg, exc);
            }
        }
        databeanManagerFactory.setDatabeanFactories(databeanFactories);

    }

    public DatabeanManagerFactory getDatabeanManagerFactory()
    {
        return databeanManagerFactory;
    }
}
