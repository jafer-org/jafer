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

import org.jafer.registry.model.Category;
import org.jafer.registry.model.CategoryType;

/**
 * This interface defines the registry manager that provides all the objects and
 * managers for accessing a specificly defined registry
 * 
 * @uml.dependency supplier="org.jafer.registry.ServiceManager"
 *                 stereotypes="Basic::Create"
 * @uml.dependency supplier="org.jafer.registry.ServiceLocator"
 *                 stereotypes="Basic::Create"
 * @uml.dependency supplier="org.jafer.registry.uddi.BusinessManager"
 *                 stereotypes="Basic::Create"
 * @uml.dependency supplier="org.jafer.registry.uddi.TModelManager"
 *                 stereotypes="Basic::Create"
 */

public interface RegistryManager
{

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
            InvalidAuthorisationDetailsException;

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
    public ServiceLocator getServiceLocator() throws RegistryException, RegistryNotInitialisedException;

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
            RegistryNotInitialisedException;

    /**
     * This method returns an instance of a category with the defined value.
     * This method exists on this interface as categories are used by both the
     * service manager and service locator
     * 
     * @param categoryType The type of category to create
     * @param value The value assigned to the category
     * @return An instance of a category with the specified value
     * @throws RegistryException
     * @throws RegistryNotInitialisedException
     */
    public Category getCategory(CategoryType categoryType, String value) throws RegistryException,
            RegistryNotInitialisedException;
}
