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

import org.jafer.registry.model.Contact;
import org.jafer.registry.model.InvalidLengthException;
import org.jafer.registry.model.InvalidNameException;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.model.ServiceProviderInfo;

/**
 * The service manager is responsible for registering service providers and
 * their services to the registry. As the service manager updates information in
 * the registry it requires a valid username and credential to be supplied.
 * 
 * @uml.dependency supplier="org.jafer.registry.model.ServiceProvider"
 *                 stereotypes="Basic::Call"
 */
public interface ServiceManager
{

    /**
     * Creates a new service provider contact. The name of the contact must
     * always be supplied and can not be a blank string.
     * 
     * @param name The new contacts name
     * @return An instance of a contact
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public Contact createNewContact(String name) throws InvalidNameException, InvalidLengthException;

    /**
     * Creates a new contact object specifying all the contacts details. The
     * name of the contact must always be supplied and can not be a blank
     * string. All other details can be empty strings.
     * 
     * @param name The contacts name (Can not be blank)
     * @param desc The contacts description
     * @param phone The contacts phone number
     * @param email The contacts email
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public Contact createNewContact(String name, String desc, String phone, String email) throws InvalidNameException,
            InvalidLengthException;

    /**
     * This method registers a new Service Provider to the registry.
     * 
     * @param providerName The name of the new service provider
     * @return The created business entity
     * @throws InvalidNameException
     * @throws RegistryException
     * @throws InvalidAuthorisationDetailsException
     */
    public ServiceProvider registerServiceProvider(String providerName) throws InvalidNameException,
            InvalidAuthorisationDetailsException, RegistryException;

    /**
     * Updates the service provider details to the registry. <b>Any service
     * objects obtained from the provider will be invalidated once this call
     * completes. </b> <br>
     * <ul>
     * <li>If a service has been removed this call will also delete the service
     * from the registry. <br>
     * <li>If a service has been added this method will create a new link to
     * the added service in the registry <br>
     * <li>If a service obtained via the providers getService() method has been
     * modified this method will update the changes on that service to the
     * registry <br>
     * <br>
     * </ul>
     * <b>Note: Be aware that providers and services only represent the details
     * in the registry at a point in time of retrieval. The information
     * contained in this provider will overright any other changes that may have
     * been made to the registry since the provider was obtained. </b> <br>
     * <br>
     * 
     * @param provider The service provider to update
     * @return The updated service provider
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */
    public ServiceProvider updateServiceProvider(ServiceProvider provider) throws InvalidAuthorisationDetailsException,
            RegistryException;

    /**
     * Deletes the service provider details in the registry. Any attached
     * services will also be deleted when the provider is removed. <BR>
     * <b>Note:Any instances of provider or services will be invalidated by this
     * call and should no longer be used. Updating them seperatly to the
     * registry will cause a synchronisation error to be thrown </b>
     * 
     * @param provider The service provider to delete
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */
    public void deleteServiceProvider(ServiceProvider provider) throws InvalidAuthorisationDetailsException, RegistryException;

    /**
     * Deletes the service provider details in the registry. Any attached
     * services will also be deleted when the provider is removed. <BR>
     * <b>Note:Any instances of provider or services will be invalidated by this
     * call and should no longer be used. Updating them seperatly to the
     * registry will cause a synchronisation error to be thrown </b>
     * 
     * @param providerInfo The service provider to delete
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */
    public void deleteServiceProvider(ServiceProviderInfo providerInfo) throws InvalidAuthorisationDetailsException,
            RegistryException;

    /**
     * Deletes the service provider details in the registry. Any attached
     * services will also be deleted when the provider is removed. <BR>
     * <b>Note:Any instances of provider or services will be invalidated by this
     * call and should no longer be used. Updating them seperatly to the
     * registry will cause a synchronisation error to be thrown </b>
     * 
     * @param id The id of the service provider to delete
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */
    public void deleteServiceProvider(String id) throws InvalidAuthorisationDetailsException, RegistryException;

    /**
     * This method registers a new service against a service provider. <br>
     * <br>
     * <b>Note: The registered service will not be reflected in the supplied
     * provider. The provider must be refreshed from the registry to see the new
     * service. </b> <br>
     * <br>
     * <b>Warning: If the provider is not refreshed any update to that provider
     * will cause the service to be deleted </b>
     * 
     * @param provider The business entity to register the service agianst
     * @param serviceName The name of the service being registered
     * @param protocol The protocol type for the access point (eg Z3950 or SRW)
     * @param accessPoint The access point for the service. This can not be an
     *        empty string
     * @return The updated business entity object
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     * @throws InvalidNameException
     */
    public Service registerService(ServiceProvider provider, String serviceName, Protocol protocol, String accessPoint)
            throws InvalidAuthorisationDetailsException, RegistryException, InvalidNameException;

    /**
     * This method registers a new service against a service provider. <br>
     * <br>
     * <b>Note: The registered service will not be reflected in the supplied
     * provider. The provider must be refreshed from the registry to see the new
     * service. </b> <br>
     * <br>
     * <b>Warning: If the provider is not refreshed any update to that provider
     * will cause the service to be deleted </b>
     * 
     * @param provider The business entity to register the service agianst
     * @param serviceName The name of the service being registered
     * @return The updated business entity object
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     * @throws InvalidNameException
     */
    public Service registerService(ServiceProvider provider, String serviceName) throws InvalidAuthorisationDetailsException,
            RegistryException, InvalidNameException;

    /**
     * Updates the specified service in the registry. <br>
     * <br>
     * <b>Note:Any provider instances that contain a reference to this service
     * need to be refreshed to obtain these updates. Subsequent updates of that
     * provider if it was not refreshed would cause the updated changes to be
     * reversed </b> <br>
     * 
     * @param service The service to update
     * @return The updated service
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */

    public Service updateService(Service service) throws InvalidAuthorisationDetailsException, RegistryException;

    /**
     * Deletes the specified service from the registry. <br>
     * <br>
     * <b>Note: Any instances of service providers that contain the deleted
     * service will be invalidated by this call and need to be refreshed.
     * Subsequent updates of that provider if it was not refreshed would cause a
     * synchronisation error to be thrown </b>
     * 
     * @param service The service to delete
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */
    public void deleteService(Service service) throws InvalidAuthorisationDetailsException, RegistryException;

    /**
     * Deletes the specified service from the registry. <br>
     * <br>
     * <b>Note: Any instances of service providers that contain the deleted
     * service will be invalidated by this call and need to be refreshed.
     * Subsequent updates of that provider if it was not refreshed would cause a
     * synchronisation error to be thrown </b>
     * 
     * @param serviceInfo The service to delete
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */
    public void deleteService(ServiceInfo serviceInfo) throws InvalidAuthorisationDetailsException, RegistryException;

    /**
     * Deletes the specified service from the registry. <br>
     * <br>
     * <b>Note: Any instances of service providers that contain the deleted
     * service will be invalidated by this call and need to be refreshed.
     * Subsequent updates of that provider if it was not refreshed would cause a
     * synchronisation error to be thrown </b>
     * 
     * @param id The id of the service to delete
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */
    public void deleteService(String id) throws InvalidAuthorisationDetailsException, RegistryException;
}