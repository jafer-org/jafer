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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.jafer.registry.RegistryException;
import org.jafer.registry.model.Category;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.model.ServiceProviderInfo;
import org.jafer.registry.uddi.model.BusinessEntity;
import org.jafer.registry.uddi.model.BusinessInfo;
import org.jafer.registry.uddi.model.BusinessService;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.Name;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.BusinessList;
import org.uddi4j.response.ServiceDetail;
import org.uddi4j.response.ServiceList;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.CategoryBag;
import org.uddi4j.util.FindQualifier;
import org.uddi4j.util.FindQualifiers;
import org.uddi4j.util.TModelBag;

/**
 * The service locator is responsible for finding providers and services in the
 * registry. Usernames and passwords are not required to search the registry.
 */
public class ServiceLocator implements org.jafer.registry.ServiceLocator
{

    /**
     * Stores a reference to the TModelManager that loads and initialises all
     * the required TModels. It is expected to have already been initialised
     * when it is used by this class
     */
    private TModelManager tModelManager = null;

    /**
     * Stores a reference to the maximum objects to return on any find call,
     * defaulted to 0 for no limit
     */
    private int maxReturned = 0;

    /**
     * Stores a reference to the UDDI4J Proxy manager that communicates with the
     * registry
     */
    private UDDIProxy registryConnection = new UDDIProxy();

    /**
     * Stores a reference to the Logger
     */
    protected static Logger logger = Logger.getLogger("org.jafer.registry.uddi.ServiceLocator");

    /**
     * Constructor for the service locator.
     * 
     * @param registryConnection The connection to the UDDI registry
     * @param tModelManager The tmodel manager for the registry being accessed
     *        by this manager
     */
    public ServiceLocator(UDDIProxy registryConnection, TModelManager tModelManager)
    {
        this.registryConnection = registryConnection;
        this.tModelManager = tModelManager;
    }

    /**
     * Allows the caller to overload the default number of instances returned
     * from a find call. Default value is set to 0 so no limits applied
     * 
     * @param maxReturned The maximum number of instances to return
     */
    public void setMaxReturned(int maxReturned)
    {
        this.maxReturned = maxReturned;
    }

    /**
     * Finds the service providers that match the supplied name. The name can
     * contain one or more % wildcard characters.
     * 
     * @param name The name of the provider to search for.
     * @return A list of ServiceProviderInfo objects for each service provider
     *         found
     * @throws RegistryException
     */
    public List findServiceProvider(String name) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "findServiceProvider(String name)");
            return findServiceProvider(name, null, null, false);
        }
        finally
        {
            logger.exiting("ServiceLocator", "findServiceProvider(String name)");
        }
    }

    /**
     * Finds the service providers that have the supplied category attached
     * 
     * @param category The category and value to search by
     * @return A list of ServiceProviderInfo objects for each service provider
     *         found
     * @throws RegistryException
     */
    public List findServiceProvider(Category category) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "findServiceProvider(Category category)");
            Vector categories = new Vector();
            categories.add(category);
            return findServiceProvider("", categories, null, true);
        }
        finally
        {
            logger.exiting("ServiceLocator", "findServiceProvider(Category category)");
        }
    }

    /**
     * Finds the service providers that have services supporting the supplied
     * protocol
     * 
     * @param protocol The protocol to search by
     * @return A list of ServiceProviderInfo objects for each service provider
     *         found
     * @throws RegistryException
     */
    public List findServiceProvider(Protocol protocol) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "findServiceProvider(Protocol protocol)");

            Vector protocols = new Vector();
            protocols.add(protocol);
            return findServiceProvider("", null, protocols, true);
        }
        finally
        {
            logger.exiting("ServiceLocator", "findServiceProvider(Protocol protocol)");
        }
    }

    /**
     * This method allows service providers to be located by using all the
     * possible search combinations. Name, categories and protocols are all
     * optional to ignore pass:
     * <ul>
     * <li>Name : an empty String
     * <li>Categories: NULL
     * <li>Protocols: NULL
     * </ul>
     * <br>
     * The name can contain one or more % wildcard characters. If useLogicalOR
     * is set to false then all the categories and protocols must be attached to
     * the service provider for it to be returned rather than just one of the
     * categories or protocols. <br>
     * <br>
     * 
     * @param name The name of the provider to search for.
     * @param categories A list of categories and values to search by
     * @param protocols A list of protocols s to search by
     * @param useLogicalOR true to use OR operator, false to use AND operator
     * @return A list of ServiceProviderInfo objects for each service provider
     *         found
     * @throws RegistryException
     */
    public List findServiceProvider(String name, List categories, List protocols, boolean useLogicalOR) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "findServiceProvider(name,categories,protocols)");
            // prepare all the data ready for the uddi call
            Vector names = buildNames(name);
            CategoryBag categoryBag = buildCategoryBag(categories);
            TModelBag tModelBag = buildTModelBag(protocols);
            FindQualifiers qualifiers = buildQualifiers(useLogicalOR);

            // we are now ready to perform the actual search
            BusinessList businessList = registryConnection.find_business(names, null, null, categoryBag, tModelBag, qualifiers,
                    maxReturned);

            // return a list of the found service providers
            return BusinessInfo.extractBusinessInfos(businessList);

        }
        catch (org.uddi4j.UDDIException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl(e);
            logger.severe(exc.toString());
            throw exc;
        }
        catch (TransportException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl(e);
            logger.severe(exc.toString());
            throw exc;
        }
        finally
        {
            logger.exiting("ServiceLocator", "findServiceProvider(name,categories,protocols)");
        }
    }

    /**
     * Gets the service provider that owns the service
     * 
     * @param service The service to search by
     * @return The service provider that owns the service or NULL if not found
     * @throws RegistryException
     */
    public ServiceProvider getServiceProvider(Service service) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "getServiceProvider(Service service)");
            // cast service to internal type
            BusinessService businessService = (BusinessService) service;
            return getServiceProvider(businessService.getServiceProviderId());
        }
        finally
        {
            logger.exiting("ServiceLocator", "getServiceProvider(Service service)");
        }
    }

    /**
     * Retrieves the full service provider object for the supplied service
     * provider info.
     * 
     * @param providerInfo The service provider info identifying the service
     *        provider to retrieve
     * @return A ServiceProvider object for the supplied service provider info
     * @throws RegistryException
     */
    public ServiceProvider getServiceProvider(ServiceProviderInfo providerInfo) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "getServiceProvider(ServiceProviderInfo providerInfo)");
            // cast service to internal type
            BusinessInfo businessInfo = (BusinessInfo) providerInfo;
            return getServiceProvider(businessInfo.getId());
        }
        finally
        {
            logger.exiting("ServiceLocator", "getServiceProvider(ServiceProviderInfo providerInfo)");
        }
    }

    /**
     * Returns the latest version of the service provider found. Any changes to
     * the supplied service provider will be ignored and a fresh copy will be
     * retrieved from the registry. <br>
     * <br>
     * <b>Note: Any service objects retrieved of the orignal provider will
     * contain out dates information after this call and should no longer be
     * used. Updating the old provider or services will cause the old
     * information to replace the current in the registry </b>
     * 
     * @param provider The provider to refresh
     * @return The refreshed service provider object
     * @throws RegistryException
     */
    public ServiceProvider refreshServiceProvider(ServiceProvider provider) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "refreshServiceProvider");
            // cast service to internal type
            BusinessEntity businessEntity = (BusinessEntity) provider;
            return getServiceProvider(businessEntity.getId());
        }
        finally
        {
            logger.exiting("ServiceLocator", "refreshServiceProvider");
        }
    }

    /**
     * Finds the services that match the supplied service provider info.
     * 
     * @param serviceProviderInfo perform the search over the services on this
     *        service provider
     * @return A list of ServiceInfo objects for each service found
     * @throws RegistryException
     */
    public List findService(ServiceProviderInfo serviceProviderInfo) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "findService(ServiceProviderInfo serviceProviderInfo)");
            return findService(serviceProviderInfo, "", null, null, false);
        }
        finally
        {
            logger.exiting("ServiceLocator", "findServiceProvider(ServiceProviderInfo serviceProviderInfo)");
        }
    }

    /**
     * Finds the service that match the supplied name. The name can contain one
     * or more % wildcard characters.
     * 
     * @param name The name of the service to search for.
     * @return A list of ServiceInfo objects for each service found
     * @throws RegistryException
     */
    public List findService(String name) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "findService(String name)");
            return findService(null, name, null, null, false);
        }
        finally
        {
            logger.exiting("ServiceLocator", "findServiceProvider(String name)");
        }
    }

    /**
     * Finds the services that have the supplied category attached
     * 
     * @param category The category and value to search by
     * @return A list of ServiceInfo objects for each service found
     * @throws RegistryException
     */
    public List findService(Category category) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "findService(Category category)");
            Vector categories = new Vector();
            categories.add(category);
            return findService(null, "", categories, null, true);
        }
        finally
        {
            logger.exiting("ServiceLocator", "findService(Category category)");
        }
    }

    /**
     * Finds the services that support the supplied protocol
     * 
     * @param protocol The protocol to search by
     * @return A list of ServiceInfo objects for each service found
     * @throws RegistryException
     */
    public List findService(Protocol protocol) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "findService(Protocol protocol)");

            Vector protocols = new Vector();
            protocols.add(protocol);
            return findService(null, "", null, protocols, true);
        }
        finally
        {
            logger.exiting("ServiceLocator", "findService(Protocol protocol)");
        }
    }

    /**
     * This method allows services to be located by using all the possible
     * search combinations. ServiceProviderInfo, Name, categories and protocols
     * are all optional to ignore pass:
     * <ul>
     * <li>serviceProviderInfo: NULL
     * <li>Name : an empty String
     * <li>Categories: NULL
     * <li>Protocols: NULL
     * </ul>
     * <br>
     * The name can contain one or more % wildcard characters. If useLogicalOR
     * is set to false then all the categories and protocols must be attached to
     * the service for it to be returned rather than just one of the categories
     * or protocols. <br>
     * <br>
     * 
     * @param serviceProviderInfo perform the search over the services on this
     *        provider
     * @param name The name of the service to search for.
     * @param categories A list of categories and values to search by
     * @param protocols A list of protocols s to search by
     * @param useLogicalOR true to use OR operator, false to use AND operator
     * @return A list of ServiceInfo objects for each service found
     * @throws RegistryException
     */
    public List findService(ServiceProviderInfo serviceProviderInfo, String name, List categories, List protocols,
            boolean useLogicalOR) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "findService(name,categories,protocols)");
            // prepare all the data ready for the uddi call
            Vector names = buildNames(name);
            CategoryBag categoryBag = buildCategoryBag(categories);
            TModelBag tModelBag = buildTModelBag(protocols);
            FindQualifiers qualifiers = buildQualifiers(useLogicalOR);

            // set the key if we have a service provider supplied to the call
            String key = null;
            if (serviceProviderInfo != null)
            {
                key = ((BusinessInfo) serviceProviderInfo).getId();
            }

            // we are now ready to perform the actual search
            ServiceList serviceList = registryConnection
                    .find_service(key, names, categoryBag, tModelBag, qualifiers, maxReturned);

            // return a list of the found service providers
            return org.jafer.registry.uddi.model.ServiceInfo.extractServiceInfos(serviceList);

        }
        catch (org.uddi4j.UDDIException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl(e);
            logger.severe(exc.toString());
            throw exc;
        }
        catch (TransportException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl(e);
            logger.severe(exc.toString());
            throw exc;
        }
        finally
        {
            logger.exiting("ServiceLocator", "findService(name,categories,protocols)");
        }
    }

    /**
     * Retrieves the full service object for the supplied service info.
     * 
     * @param serviceInfo The service info identifying the service to retrieve
     * @return A Service object for the supplied service info
     * @throws RegistryException
     */
    public Service getService(ServiceInfo serviceInfo) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "getService(ServiceInfo serviceInfo)");
            // cast service to internal type
            org.jafer.registry.uddi.model.ServiceInfo info = (org.jafer.registry.uddi.model.ServiceInfo) serviceInfo;
            return getService(info.getId());
        }
        finally
        {
            logger.exiting("ServiceLocator", "getService(ServiceInfo serviceInfo)");
        }
    }

    /**
     * Returns the latest version of the service found. Any changes to the
     * supplied service will be ignored and a fresh copy will be retrieved from
     * the registry. <br>
     * 
     * @param service The service to refresh
     * @return The refreshed service object
     * @throws RegistryException
     */
    public Service refreshService(Service service) throws RegistryException
    {
        try
        {
            logger.entering("ServiceLocator", "refreshService");
            // cast service to internal type
            BusinessService businessService = (BusinessService) service;
            return getService(businessService.getId());
        }
        finally
        {
            logger.exiting("ServiceLocator", "refreshService");
        }
    }

    /**
     * Gets the service provider from its unique ID
     * 
     * @param id The ID of the service provider
     * @return The service provider for the ID or NULL if not found
     * @throws RegistryException
     */
    public ServiceProvider getServiceProvider(String id) throws RegistryException
    {
        logger.entering("ServiceLocator", "getServiceProvider(String key)");
        try
        {
            // get the business detail object for the business key supplied
            BusinessDetail businessDetail = registryConnection.get_businessDetail(id);
            // make sure we found a result
            if (businessDetail.getBusinessEntityVector().size() > 0)
            {
                // return a new business entity object as the service provider
                return new BusinessEntity(tModelManager, (org.uddi4j.datatype.business.BusinessEntity) businessDetail
                        .getBusinessEntityVector().firstElement());
            }
        }
        catch (org.uddi4j.UDDIException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl(e);
            logger.severe(exc.toString());
            throw exc;
        }
        catch (TransportException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl(e);
            logger.severe(exc.toString());
            throw exc;
        }
        finally
        {
            logger.exiting("ServiceLocator", "getServiceProvider(String key)");
        }
        return null;
    }

    /**
     * Gets the service from its unique ID
     * 
     * @param id The ID of the service 
     * @return The service  for the ID or NULL if not found
     * @throws RegistryException
     */
    public Service getService(String id) throws RegistryException
    {
        logger.entering("ServiceLocator", "getService(String key)");
        try
        {
            // get the business detail object for the business key supplied
            ServiceDetail serviceDetail = registryConnection.get_serviceDetail(id);
            // make sure we found a result
            if (serviceDetail.getBusinessServiceVector().size() > 0)
            {
                // return a new business entity object as the service provider
                return new BusinessService(tModelManager, (org.uddi4j.datatype.service.BusinessService) serviceDetail
                        .getBusinessServiceVector().firstElement());
            }
        }
        catch (org.uddi4j.UDDIException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl(e);
            logger.severe(exc.toString());
            throw exc;
        }
        catch (TransportException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl(e);
            logger.severe(exc.toString());
            throw exc;
        }
        finally
        {
            logger.exiting("ServiceLocator", "getService(String key)");
        }
        return null;
    }

    /**
     * Builds the vector of search names required for searching by name
     * 
     * @param name The name of the service to search for.
     * @return A vector of Name objects
     */
    private Vector buildNames(String name)
    {
        // prepare name
        Vector names = null;
        // make sure we have a name to process
        if (name != null && name.length() > 0)
        {
            names = new Vector();
            names.add(new Name(name));
        }
        return names;
    }

    /**
     * Builds a category bag containg all the categories to be used by the
     * search
     * 
     * @param categories A list of categories and values to search by
     * @return category bag containg all the categories
     */
    private CategoryBag buildCategoryBag(List categories)
    {
        // process categories
        CategoryBag categoryBag = null;
        // do we have any categories to process
        if (categories != null && categories.size() > 0)
        {
            categoryBag = new CategoryBag();
            Iterator iter = categories.iterator();
            while (iter.hasNext())
            {
                // add the category to the category bag
                ((org.jafer.registry.uddi.model.Category) iter.next()).addToCategoryBag(categoryBag);
            }
        }
        return categoryBag;
    }

    /**
     * Builds a tmodel bag containg all the protocols tmodels to be used by the
     * search
     * 
     * @param protocols A list of protocols to search by
     * @return tmodel bag containg all the protocols tmodels
     * @throws RegistryException
     */
    private TModelBag buildTModelBag(List protocols) throws RegistryException
    {

        // process protocols
        TModelBag tModelBag = null;
        // do we have any categories to process
        if (protocols != null && protocols.size() > 0)
        {
            tModelBag = new TModelBag();
            Iterator iter = protocols.iterator();
            while (iter.hasNext())
            {
                // add the tmodels to the tmodelbag
                tModelManager.getProtocolTModel((Protocol) iter.next()).addToTModelBag(tModelBag);
            }
        }
        return tModelBag;
    }

    /**
     * Builds the find qualifiers for the search
     * 
     * @param useLogicalOR true to use OR operator, false to use AND operator
     * @return the findqualifers for performing logical or / logical and
     */
    private FindQualifiers buildQualifiers(boolean useLogicalOR)
    {
        // set up the find qualifiers if we are doing or
        FindQualifiers qualifiers = new FindQualifiers();

        // if we are using or we need to set up the find qualifiers
        if (useLogicalOR)
        {
            qualifiers.add(new FindQualifier(FindQualifier.orAllKeys));
        }
        return qualifiers;
    }

}