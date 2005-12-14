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

package org.jafer.registry.model;

import java.io.Serializable;
import java.util.List;

import org.jafer.registry.model.ServiceInfo;

/**
 * This interface represents the service provider model support for JAFER. <br>
 * <br>
 * Updates made using this interface will only be made to the registry when the
 * service provider is updated through the service manager <br>
 * <br>
 * <b>Note: Be aware that the provider instance represents the details in the
 * registry at the point in time of retrieval. When an update to the provider is
 * made the information in the provider will replace any information in the
 * registry and may cause other instances to no longer be up to date <br>
 * <br>
 * Service provider instances should be strictly controlled and never cached by
 * the caller to avoid becoming out of date with the registry. Updating outdated
 * items will replace any changes made with the old values and may cause
 * exceptions to be thrown if referenced objects have already been deleted in
 * the registry </b>
 * 
 * @uml.dependency supplier="org.jafer.registry.model.Category"
 *                 stereotypes="Basic::Call"
 * @uml.dependency supplier="org.jafer.registry.model.Service"
 *                 stereotypes="Basic::Call"
 * @uml.dependency supplier="org.jafer.registry.model.Contact"
 *                 stereotypes="Basic::Call"
 */

public interface ServiceProvider extends Serializable
{

    /**
     * Returns the uniquie id of this service provider.
     * 
     * @return The service id.
     */
    public String getId();

    /**
     * Returns the name of the service provider.
     * 
     * @return The service provider name. An empty string will be returned if
     *         not found.
     */
    public String getName();

    /**
     * Sets the name of the service provider. This will only actually be applied
     * when the service provider is updated with the registry via the service
     * manager.
     * 
     * @param name The name to set
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public void setName(String name) throws InvalidNameException, InvalidLengthException;

    /**
     * Returns the description for the service provider.
     * 
     * @return The service provider description. An empty string will be
     *         returned if not found.
     */
    public String getDescription();

    /**
     * Sets the description for the service provider. This will only actually be
     * applied when the service provider is updated with the registry via the
     * service manager.
     * 
     * @param description The description to set
     * @throws InvalidLengthException
     */
    public void setDescription(String description) throws InvalidLengthException;

    /**
     * Returns the home page for the service provider.
     * 
     * @return The url string to the home page. An empty string will be returned
     *         if not found.
     */
    public String getHomePage();

    /**
     * Sets the home page for the service provider. This will only actually be
     * applied when the service provider is updated with the registry via the
     * service manager.
     * 
     * @param url The url string to the home page
     * @throws InvalidLengthException
     */
    public void setHomePage(String url) throws InvalidLengthException;

    /**
     * Get a COPY of the contact information for the service provider. If no
     * contact information is found then null will be returned.
     * 
     * @return Copy of the contact information or null if no contact assotiated
     */
    public Contact getContact();

    /**
     * Set the contact information for the service provider. If it already
     * exists it will be overwritten with this contact. This will only actually
     * be applied when the service provider is updated with the registry via the
     * service manager.
     * 
     * @param contact The contact information
     */
    public void setContact(Contact contact);

    /**
     * Removes the contact information for the service provider. This will only
     * actually be applied when the service provider is updated with the
     * registry via the service manager.
     */
    public void removeContact();

    /**
     * This method returns all the categories supported linked to the service
     * provider. Some of these categories may represent categories outside of
     * this toolkit but they are returned to allow the caller to remove them if
     * they wish.
     * 
     * @return A list of categories
     */
    public List getCategories();

    /**
     * Adds a jafer defined category to the service provider if it does not
     * already exist. This will only actually be applied when the service
     * provider is updated with the registry via the service manager.
     * 
     * @param category The category to add
     */
    public void addCategory(Category category);

    /**
     * Removes the category from the service provider if it exists. This will
     * only actually be applied when the service provider is updated with the
     * registry via the service manager.
     * 
     * @param category The category to delete
     * @throws CategoryDoesNotExistException
     */
    public void removeCategory(Category category) throws CategoryDoesNotExistException;

    /**
     * Removes all the categories from the service provider. This will only
     * actually be applied when the service provider is updated with the
     * registry via the service manager.
     */
    public void removeAllCategories();

    /**
     * This method returns all the know services for the service provider at the
     * time of retrieval. ServiceInfo objects are returned that can be used to
     * delete the service or to obtain the full detail odf the service through
     * the service locator.
     * 
     * @return A list of ServiceInfo objects describing the known services
     */
    public List getServices();

    /**
     * This method adds the service to the service provider. This will only
     * actually be applied when the service provider is updated with the
     * registry via the service manager. <br>
     * <b>Note: The service added must exist in the registry for the update to
     * succeed. </b>
     * 
     * @param service The service to add
     */
    public void addService(Service service);

    /**
     * This method removes the service from the service provider. This will only
     * actually be applied when the service provider is updated with the
     * registry via the service manager. <br>
     * <b>Note: Once the service provider has been updated by the service
     * manager any instances of the service will no longer be valid and can not
     * be added again later, instead it must be re-registered. </b>
     * 
     * @param service the service to remove
     * @throws ServiceDoesNotExistException
     */
    public void removeService(Service service) throws ServiceDoesNotExistException;

    /**
     * This method removes the service from the service provider. This will only
     * actually be applied when the service provider is updated with the
     * registry via the service manager. <br>
     * <b>Note: Once the service provider has been updated by the service
     * manager any instances of the service will no longer be valid and can not
     * be added again later, instead it must be re-registered. </b>
     * 
     * @param serviceInfo the serviceinfo of the service to remove
     * @throws ServiceDoesNotExistException
     */
    public void removeService(ServiceInfo serviceInfo) throws ServiceDoesNotExistException;

    /**
     * This method removes all the services. This will only actually be applied
     * when the service provider is updated with the registry via the service
     * manager.
     */
    public void removeAllServices();
}
