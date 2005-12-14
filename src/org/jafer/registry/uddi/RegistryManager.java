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

import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.jafer.registry.InvalidAuthorisationDetailsException;
import org.jafer.registry.RegistryException;
import org.jafer.registry.RegistryNotInitialisedException;
import org.jafer.registry.ServiceLocator;
import org.jafer.registry.ServiceManager;
import org.jafer.registry.model.Category;
import org.jafer.registry.model.CategoryType;
import org.uddi4j.client.UDDIProxy;

/**
 * This class defines the registry manager that provides all the objects and
 * managers for accessing a specificly defined registry
 */
public class RegistryManager implements org.jafer.registry.RegistryManager
{

    /**
     * Stores a reference to the TModelManager that loads and initialises all
     * the required TModels
     */
    private TModelManager tModelManager = null;

    /**
     * Stores a reference to the UDDI4J Proxy manager that communicates with the
     * registry
     */
    private UDDIProxy registryConnection = new UDDIProxy();

    /**
     * Stores a reference to the Logger
     */
    protected static Logger logger = Logger.getLogger("org.jafer.registry.uddi.RegistryManager");

    /**
     * Constructor for the Registry Manager. The URLS define the registry to be
     * connected to and that needs to be initialised for use
     * 
     * @param inquiryURL The URL to the registry inquiry service
     * @param publishURL The URL to the registry publish service
     * @throws RegistryException
     */
    public RegistryManager(String inquiryURL, String publishURL) throws RegistryException
    {
        logger.entering("RegistryManager", "Constructor");
        try
        {
            // set the system properties for the transport mechanisum that is
            // used to connect to the registry with
            System.setProperty("org.uddi4j.TransportClassName", "org.uddi4j.transport.ApacheAxisTransport");
            // Configure JSSE support against SUN
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        }
        catch (Exception e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl("Error configuring JSSE support ", e);
            logger.severe(exc.toString());
            throw exc;
        }

        try
        {
            // store the URLs
            registryConnection.setInquiryURL(inquiryURL);
            registryConnection.setPublishURL(publishURL);
        }
        catch (MalformedURLException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl("Bad Inquiry/Publish URL", e);
            logger.severe(exc.toString());
            throw exc;
        }
        finally
        {
            logger.exiting("RegistryManager", "Constructor");
        }
    }

    /**
     * This method can be used to initialises the registry with the required
     * support objects. It only needs to be called once per registry set up.
     * <br>
     * This call is required when a RegistryNotInitialisedException is thrown as
     * by default the registry manager will only attempt to load the support
     * objects.
     * 
     * @param username The username of the user using the registry.
     * @param credential The credential required to authenticate user
     * @throws RegistryException
     */
    public void initialiseRegistry(String username, String credential) throws RegistryException,
            InvalidAuthorisationDetailsException
    {
        try
        {
            // initialise tmodels
            tModelManager = (TModelManager) new org.jafer.registry.uddi.TModelManager(registryConnection, username, credential);
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            logger.info(e.toString());
            throw e;
        }
        catch (RegistryException e)
        {
            logger.severe(e.toString());
            throw e;
        }
    }

    /**
     * This method creates a new service locator that has been enabled for
     * searching the registry for service provides and available services. <br>
     * This method will check that all the support objects have been initialised
     * correctly before returning the service locator.
     * 
     * @return An instance of the service locator
     * @throws RegistryException
     * @throws RegistryNotInitialisedException
     */
    public ServiceLocator getServiceLocator() throws RegistryException, RegistryNotInitialisedException
    {
        return new org.jafer.registry.uddi.ServiceLocator(registryConnection, getTModelManager());
    }

    /**
     * This method returns a service manager that has been enbled for creating
     * and maintaining service providers and services. To update information in
     * the registry a valid username and credential must be supplied. <br>
     * This method will check that all the support objects have been initialised
     * correctly before returning the service manager.
     * 
     * @param username The username of the user using the registry.
     * @param credential The credential required to authenticate user
     * @return An instance of the service manager configured for updates by the
     *         specified user
     * @throws RegistryException
     * @throws RegistryNotInitialisedException
     */
    public ServiceManager getServiceManager(String username, String credential) throws RegistryException,
            RegistryNotInitialisedException
    {
        return new org.jafer.registry.uddi.ServiceManager(registryConnection, getTModelManager(), username, credential);
    }

    /**
     * This method returns an instance of a category with the defined value.
     * This method exists on this class as categories are used by both the
     * service manager and service locator
     * 
     * @param categoryType The type of category to create
     * @param value The value assigned to the category
     * @return An instance of a category with the specified value
     * @throws RegistryException
     * @throws RegistryNotInitialisedException
     */
    public Category getCategory(CategoryType categoryType, String value) throws RegistryException,
            RegistryNotInitialisedException
    {
        // return the new category created from the TModel
        return new org.jafer.registry.uddi.model.Category(getTModelManager().getCategoryTModel(categoryType), value);
    }

    /**
     * Returns a fully initialised TModel Manager. This method expects that all
     * the TModel have already been registered with the Registry else it will
     * throw an exception
     * 
     * @return The TModel manager
     * @throws RegistryException
     * @throws RegistryNotInitialisedException
     */
    private TModelManager getTModelManager() throws RegistryException, RegistryNotInitialisedException
    {
        // check if the tmodel manager has already been created
        if (tModelManager == null)
        {
            try
            {
                // try and initialise the tmodel manager without creating any
                // missing TModels
                tModelManager = (TModelManager) new org.jafer.registry.uddi.TModelManager(registryConnection);
            }
            catch (RegistryException e)
            {
                logger.severe(e.toString());
                throw e;
            }
        }
        return tModelManager;
    }

}
