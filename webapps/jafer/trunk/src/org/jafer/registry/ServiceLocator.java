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

package org.jafer.registry;

import java.util.List;

import org.jafer.registry.model.Category;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.model.ServiceProviderInfo;

/**
 * The service locator is responsible for finding providers and services in the
 * registry. Usernames and passwords are not required to search the registry.
 * 
 * @uml.dependency supplier="org.jafer.registry.model.ServiceProvider"
 *                 stereotypes="Basic::Call"
 */

public interface ServiceLocator
{

    /**
     * Allows the caller to overload the default number of instances returned
     * from a find call. Default value is set to 0 so no limits applied
     * 
     * @param maxReturned The maximum number of instances to return
     */
    public void setMaxReturned(int maxReturned);

    /**
     * Finds the service providers that match the supplied name. The name can
     * contain one or more % wildcard characters.
     * 
     * @param name The name of the provider to search for.
     * @return A list of ServiceProviderInfo objects for each service provider
     *         found
     * @throws RegistryException
     */
    public List findServiceProvider(String name) throws RegistryException;

    /**
     * Finds the service providers that have the supplied category attached
     * 
     * @param category The category and value to search by
     * @return A list of ServiceProviderInfo objects for each service provider
     *         found
     * @throws RegistryException
     */
    public List findServiceProvider(Category category) throws RegistryException;

    /**
     * Finds the service providers that have services supporting the supplied
     * protocol
     * 
     * @param protocol The protocol to search by
     * @return A list of ServiceProviderInfo objects for each service provider
     *         found
     * @throws RegistryException
     */
    public List findServiceProvider(Protocol protocol) throws RegistryException;

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
    public List findServiceProvider(String name, List categories, List protocols, boolean useLogicalOR) throws RegistryException;

    /**
     * Gets the service provider that owns the service
     * 
     * @param service The service to search by
     * @return The service provider that owns the service or NULL if not found
     * @throws RegistryException
     */
    public ServiceProvider getServiceProvider(Service service) throws RegistryException;
    
    /**
     * Gets the service provider from its unique ID
     * 
     * @param id The ID of the service provider
     * @return The service provider for the ID or NULL if not found
     * @throws RegistryException
     */
    public ServiceProvider getServiceProvider(String id) throws RegistryException;

    /**
     * Retrieves the full service provider object for the supplied service
     * provider info.
     * 
     * @param providerInfo The service provider info identifying the service
     *        provider to retrieve
     * @return A ServiceProvider object for the supplied service provider info
     * @throws RegistryException
     */
    public ServiceProvider getServiceProvider(ServiceProviderInfo providerInfo) throws RegistryException;

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
    public ServiceProvider refreshServiceProvider(ServiceProvider provider) throws RegistryException;

    /**
     * Finds the services that match the supplied service provider info.
     * 
     * @param serviceProviderInfo perform the search over the services on this
     *        service provider
     * @return A list of ServiceInfo objects for each service found
     * @throws RegistryException
     */
    public List findService(ServiceProviderInfo serviceProviderInfo) throws RegistryException;

    /**
     * Finds the service that match the supplied name. The name can contain one
     * or more % wildcard characters.
     * 
     * @param name The name of the service to search for.
     * @return A list of ServiceInfo objects for each service found
     * @throws RegistryException
     */
    public List findService(String name) throws RegistryException;

    /**
     * Finds the services that have the supplied category attached
     * 
     * @param category The category and value to search by
     * @return A list of ServiceInfo objects for each service found
     * @throws RegistryException
     */
    public List findService(Category category) throws RegistryException;

    /**
     * Finds the services that support the supplied protocol
     * 
     * @param protocol The protocol to search by
     * @return A list of ServiceInfo objects for each service found
     * @throws RegistryException
     */
    public List findService(Protocol protocol) throws RegistryException;

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
            boolean useLogicalOR) throws RegistryException;

    /**
     * Retrieves the full service object for the supplied service info.
     * 
     * @param serviceInfo The service info identifying the service to retrieve
     * @return A Service object for the supplied service info
     * @throws RegistryException
     */
    public Service getService(ServiceInfo serviceInfo) throws RegistryException;
    
    /**
     * Gets the service from its unique ID
     * 
     * @param id The ID of the service 
     * @return The service  for the ID or NULL if not found
     * @throws RegistryException
     */
    public Service getService(String id) throws RegistryException;
    /**
     * Returns the latest version of the service found. Any changes to the
     * supplied service will be ignored and a fresh copy will be retrieved from
     * the registry. <br>
     * 
     * @param service The service to refresh
     * @return The refreshed service object
     * @throws RegistryException
     */
    public Service refreshService(Service service) throws RegistryException;

}
