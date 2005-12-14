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

import org.jafer.registry.RegistryException;

/**
 * This interface represents the service model support for JAFER. Services can
 * be added to a service provider to create their portfolio. <br>
 * <br>
 * Updates made using this interface will only be made to the registry when the
 * service is updated through the service manager update methods <br>
 * <br>
 * <b>Note: Be aware that the service instance represents the details in the
 * registry at the point in time of retrieval. When an update to the service is
 * made the information in the provider will replace any information in the
 * registry and may cause other instances to no longer be up to date <br>
 * <br>
 * Service instances should be strictly controlled and never cached by the
 * caller to avoid becoming out of date with the registry. Updating outdated
 * items will replace any changes made with the old values and may cause
 * exceptions to be thrown if referenced objects have already been deleted in
 * the registry </b>
 * 
 * @uml.dependency supplier="org.jafer.registry.model.Category"
 *                 stereotypes="Basic::Call"
 */

public interface Service extends Serializable
{

    /**
     * Returns the uniquie id of this service.
     * 
     * @return The service id.
     */
    public String getId();

    /**
     * Returns the ID of the service provider that owns this service
     * 
     * @return The owning service provider ID
     */
    public String getServiceProviderId();

    /**
     * Returns the name of this service.
     * 
     * @return The service name. An empty string will be returned if not found.
     */
    public String getName();

    /**
     * Sets the name of this service. This will only actually be applied when
     * the service is updated with the registry via the service manager.
     * 
     * @param name The name to set
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public void setName(String name) throws InvalidNameException, InvalidLengthException;

    /**
     * Returns the description of the service.
     * 
     * @return The business service description. An empty string will be
     *         returned if not found.
     */
    public String getDescription();

    /**
     * Sets the description of th service. This will only actually be applied
     * when the service is updated with the registry via the service manager.
     * 
     * @param description The description to set
     * @throws InvalidLengthException
     */
    public void setDescription(String description) throws InvalidLengthException;

    /**
     * This method returns all the categories supported linked to the service.
     * Some of these categories may represent categories outside of this toolkit
     * but they are returned to allow the caller to remove them if they wish.
     * 
     * @return A list of categories
     */
    public List getCategories();

    /**
     * Adds a jafer defined category to the service if it does not already
     * exist. This will only actually be applied when the service provider is
     * updated with the registry via the service manager.
     * 
     * @param category The category to add
     */
    public void addCategory(Category category);

    /**
     * Removes the category from the service if it exists. This will only
     * actually be applied when the service provider is updated with the
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
     * Returns the access url for the service. This can be used to connect to
     * the actual service. This will only actually be applied when the service
     * provider is updated with the registry via the service manager.
     * 
     * @param protocol The protocol type of the accesspoint to find
     * @return The access point url. An empty string will be returned if not
     *         found.
     * @throws RegistryException
     */
    public String getAccessUrl(Protocol protocol) throws RegistryException;

    /**
     * Sets the access url for the service. This can be used to connect to the
     * actual service. This will only actually be applied when the service
     * provider is updated with the registry via the service manager.
     * 
     * @param protocol The protocol type of the accesspoint to set
     * @param url The url to the access point. A blank string will remove the
     *        URL.
     * @throws RegistryException
     * @throws InvalidLengthException
     */
    public void setAccessUrl(Protocol protocol, String url) throws RegistryException, InvalidLengthException;

    /**
     * Returns the WSDL file for the service. This describes the service and can
     * also point to a connection point internally. This will only actually be
     * applied when the service provider is updated with the registry via the
     * service manager.
     * 
     * @param protocol The protocol type of the accesspoint to find
     * @return The wsdl file url. An empty string will be returned if not found.
     * @throws RegistryException
     */
    public String getWSDLUrl(Protocol protocol) throws RegistryException;

    /**
     * Sets the WSDL file for the service. This describes the service and can
     * also point to a connection point internally. This will only actually be
     * applied when the service provider is updated with the registry via the
     * service manager.
     * 
     * @param protocol The protocol type of the accesspoint
     * @param url The url to the access point to set. A blank string will remove
     *        the access point.
     * @throws RegistryException
     * @throws InvalidLengthException
     */
    public void setWSDLUrl(Protocol protocol, String url) throws RegistryException, InvalidLengthException;

    /**
     * This method checks the service to see if it supports the specified
     * protocol
     * 
     * @param protocol The protocol type of the accesspoint to set
     * @return true if the template supports the protocol
     * @throws RegistryException
     */
    public boolean supportsProtocol(Protocol protocol) throws RegistryException;
}
