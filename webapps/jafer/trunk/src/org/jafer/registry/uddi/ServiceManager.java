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
import java.util.Vector;
import java.util.logging.Logger;

import org.jafer.registry.InvalidAuthorisationDetailsException;
import org.jafer.registry.RegistryException;
import org.jafer.registry.model.Contact;
import org.jafer.registry.model.InvalidLengthException;
import org.jafer.registry.model.InvalidNameException;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.model.ServiceProviderInfo;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.binding.AccessPoint;
import org.uddi4j.datatype.binding.BindingTemplate;
import org.uddi4j.datatype.binding.BindingTemplates;
import org.uddi4j.datatype.binding.TModelInstanceDetails;
import org.uddi4j.datatype.binding.TModelInstanceInfo;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.datatype.service.BusinessService;
import org.uddi4j.response.BindingDetail;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.ServiceDetail;
import org.uddi4j.transport.TransportException;

/**
 * This class maps the service manager interface onto the UDDI registry.
 */
public class ServiceManager implements org.jafer.registry.ServiceManager
{

    /**
     * Stores a reference to the TModelManager that loads and initialises all
     * the required TModels. It is expected to have already been initialised
     * when it is used by this class
     */
    private TModelManager tModelManager = null;

    /**
     * Stores a reference to the username for logging on to publish service
     */
    public String username = null;

    /**
     * Stores a reference to the credential for logging on to publish service
     */
    public String credential = null;

    /**
     * Stores a reference to the UDDI4J Proxy manager that communicates with the
     * registry
     */
    private UDDIProxy registryConnection = new UDDIProxy();

    /**
     * Stores a reference to the Logger
     */
    protected static Logger logger = Logger.getLogger("org.jafer.registry.uddi.ServiceManager");

    /**
     * Constructor for the service manger. The supplied username and credential
     * will be used to obtain an authorisation token per call and an initial
     * test will be done to make sure they are valid
     * 
     * @param registryConnection The connection to the UDDI registry
     * @param tModelManager The tmodel manager for the registry being accessed
     *        by this manager
     * @param username The username of the user using the registry.
     * @param credential The credential required to authenticate user
     * @throws RegistryException
     * @throws InvalidAuthorisationDetailsException
     */
    public ServiceManager(UDDIProxy registryConnection, TModelManager tModelManager, String username, String credential)
            throws RegistryException, InvalidAuthorisationDetailsException
    {
        this.registryConnection = registryConnection;
        this.tModelManager = tModelManager;
        this.username = username;
        this.credential = credential;
        // make sure we can get and discard a token as this proves username and
        // credential is okay
        discardAuthorisationToken(getAuthorisationToken());
    }

    /**
     * Obtain a new authorisation token
     * 
     * @return The new authorisation token
     * @throws RegistryException
     * @throws InvalidAuthorisationDetailsException
     */
    private String getAuthorisationToken() throws RegistryException, InvalidAuthorisationDetailsException
    {
        logger.entering("ServiceManager", "getAuthorisationToken");
        try
        {
            // get authorisation token
            return registryConnection.get_authToken(username, credential).getAuthInfoString();
        }
        catch (org.uddi4j.UDDIException e)
        {
            // did we get invalid token error
            if (RegistryExceptionImpl.isErrorOfType(e.getDispositionReport(), DispositionReport.E_unknownUser))
            {
                // user did not authenticate correctly
                throw new InvalidAuthorisationDetailsException(e);
            }
            throw new RegistryExceptionImpl("Error obtaining authorisation token ", e);
        }
        catch (TransportException e)
        {
            throw new RegistryExceptionImpl(e);
        }
        finally
        {
            logger.exiting("ServiceManager", "getAuthorisationToken");
        }
    }

    /**
     * Discard authorisation token
     * 
     * @param token the token to discard
     * @throws RegistryExceptionImpl
     */
    private void discardAuthorisationToken(String token) throws RegistryException
    {
        logger.entering("ServiceManager", "discardAuthorisationToken");
        try
        {
            if (token != null)
            {
                registryConnection.discard_authToken(token);
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
            logger.exiting("ServiceManager", "discardAuthorisationToken");
        }
    }

    /**
     * Creates a new service provider contact. The name of the contact must
     * always be supplied and can not be a blank string.
     * 
     * @param name The new contacts name
     * @return An instance of a contact
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public Contact createNewContact(String name) throws InvalidNameException, InvalidLengthException
    {
        return new org.jafer.registry.uddi.model.Contact(name);
    }

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
            InvalidLengthException
    {
        return new org.jafer.registry.uddi.model.Contact(name, desc, phone, email);
    }

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
            InvalidAuthorisationDetailsException, RegistryException
    {
        String token = null;
        logger.entering("ServiceManager", "registerBusinessEntity");
        try
        {
            // first check we have specified a valid provider name
            if (providerName == null || providerName.length() == 0)
            {
                throw new InvalidNameException();
            }

            token = getAuthorisationToken();
            // create a new uddi4j business entity object with the specified
            // name. Specify no key so it is generated
            BusinessEntity businessEntity = new BusinessEntity("", providerName);
            Vector entities = new Vector();
            entities.addElement(businessEntity);
            // now save the new entity creating its key
            BusinessDetail businessDetail = registryConnection.save_business(token, entities);
            // now create our business entity encapsulating the result
            ServiceProvider newProvider = new org.jafer.registry.uddi.model.BusinessEntity(tModelManager,
                    (BusinessEntity) businessDetail.getBusinessEntityVector().firstElement());
            // discard the token
            return newProvider;
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            logger.info(e.toString());
            throw e;
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
            discardAuthorisationToken(token);
            logger.exiting("ServiceManager", "registerBusinessEntity");
        }
    }

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
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */
    public ServiceProvider updateServiceProvider(ServiceProvider provider) throws InvalidAuthorisationDetailsException,
            RegistryException
    {
        logger.entering("ServiceManager", "deleteBusinessEntity");
        String token = null;
        try
        {
            token = getAuthorisationToken();
            // cast to internal type;
            org.jafer.registry.uddi.model.BusinessEntity businessEntity = (org.jafer.registry.uddi.model.BusinessEntity) provider;
            // get rid of any bad service keys where the service ID exists but it has no name
            businessEntity.removeAnyBadServiceKeys();
            // store the internal business entity for processing
            Vector entities = new Vector();
            entities.add(businessEntity.getUDDIBusinessEntity());
            // update the internal business entity
            BusinessDetail businessDetail = registryConnection.save_business(token, entities);
            // store the new value into business entity
            businessEntity.setUDDIBusinessEntity((org.uddi4j.datatype.business.BusinessEntity) businessDetail
                    .getBusinessEntityVector().firstElement());
            // return the business entity back
            return businessEntity;
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            logger.info(e.toString());
            throw e;
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
            discardAuthorisationToken(token);
            logger.exiting("ServiceManager", "deleteBusinessEntity");
        }
    }

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
    public void deleteServiceProvider(ServiceProvider provider) throws InvalidAuthorisationDetailsException, RegistryException
    {
        logger.entering("ServiceManager", "deleteBusinessEntity(provider)");
        try
        {
            deleteServiceProvider(provider.getId());
        }
        finally
        {
            logger.exiting("ServiceManager", "deleteBusinessEntity(provider)");
        }
    }

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
            RegistryException
    {
        logger.entering("ServiceManager", "deleteBusinessEntity(providerInfo)");
        try
        {
            deleteServiceProvider(providerInfo.getId());
        }
        finally
        {
            logger.exiting("ServiceManager", "deleteBusinessEntity(providerInfo)");
        }
    }
    
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
    public void deleteServiceProvider(String id) throws InvalidAuthorisationDetailsException,
            RegistryException
    {
        logger.entering("ServiceManager", "deleteBusinessEntity(id)");
        String token = null;
        try
        {
            token = getAuthorisationToken();
            registryConnection.delete_business(token, id);
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            logger.info(e.toString());
            throw e;
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
            discardAuthorisationToken(token);
            logger.exiting("ServiceManager", "deleteBusinessEntity(id)");
        }
    }

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
            throws InvalidAuthorisationDetailsException, RegistryException, InvalidNameException
    {
        String token = null;
        logger.entering("ServiceManager", "registerService(provider,name,protocol,accesspoint)");
        try
        {
            // first check we have specified a valid provider name
            if (serviceName == null || serviceName.length() == 0)
            {
                throw new InvalidNameException();
            }

            logger.fine("Creating business service");
            // cast to internal type;
            org.jafer.registry.uddi.model.BusinessEntity businessEntity = (org.jafer.registry.uddi.model.BusinessEntity) provider;
            // get authorisation token
            token = getAuthorisationToken();
            // create a business service object passing empty string to create a
            // new key
            org.uddi4j.datatype.service.BusinessService businessService = new org.uddi4j.datatype.service.BusinessService("");
            // set the business service name
            businessService.setDefaultName(new Name(serviceName));
            // set link to business entity
            businessService.setBusinessKey(businessEntity.getId());
            // save the new business service
            Vector services = new Vector();
            services.addElement(businessService);
            ServiceDetail serviceDetail = registryConnection.save_service(token, services);

            // The service is now created and registered so extract business
            // service
            Vector businessServices = serviceDetail.getBusinessServiceVector();
            BusinessService newBusinessService = (BusinessService) businessServices.elementAt(0);

            // create a new access point binding templates
            BindingTemplate accessPointBinding = null;

            try
            {

                logger.fine("Creating business service binding templates for access points");
                // create the new binding templates structure for the service
                BindingTemplates templates = new BindingTemplates();

                accessPointBinding = createBindingTemplateForAccessPoint(newBusinessService.getServiceKey(), protocol,
                        accessPoint, org.jafer.registry.uddi.model.BusinessService.END_POINT);
                templates.add(accessPointBinding);
                // add the templates to the new business service
                newBusinessService.setBindingTemplates(templates);
            }
            catch (RegistryException exc)
            {
                // failed to create binding point so must delete service
                // This will also delete any binding templates that did create
                // successfully
                logger.severe("Unable to create binding template " + exc.getStackTraceString());
                registryConnection.delete_service(token, newBusinessService.getServiceKey());
                throw new RegistryExceptionImpl("Unable to create binding template", exc);
            }

            // returning service object
            return new org.jafer.registry.uddi.model.BusinessService(tModelManager, newBusinessService);

        }
        catch (InvalidAuthorisationDetailsException e)
        {
            logger.info(e.toString());
            throw e;
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
            discardAuthorisationToken(token);
            logger.exiting("ServiceManager", "registerService(provider,name,protocol,accesspoint)");
        }
    }

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
            RegistryException, InvalidNameException
    {
        String token = null;
        logger.entering("ServiceManager", "registerService(provider,name)");
        try
        {
            // first check we have specified a valid provider name
            if (serviceName == null || serviceName.length() == 0)
            {
                throw new InvalidNameException();
            }

            logger.fine("Creating business service");
            // cast to internal type;
            org.jafer.registry.uddi.model.BusinessEntity businessEntity = (org.jafer.registry.uddi.model.BusinessEntity) provider;
            // get authorisation token
            token = getAuthorisationToken();
            // create a business service object passing empty string to create a
            // new key
            org.uddi4j.datatype.service.BusinessService businessService = new org.uddi4j.datatype.service.BusinessService("");
            // set the business service name
            businessService.setDefaultName(new Name(serviceName));
            // set link to business entity
            businessService.setBusinessKey(businessEntity.getId());
            // save the new business service
            Vector services = new Vector();
            services.addElement(businessService);
            ServiceDetail serviceDetail = registryConnection.save_service(token, services);

            // The service is now created and registered so extract business
            // service
            Vector businessServices = serviceDetail.getBusinessServiceVector();
            BusinessService newBusinessService = (BusinessService) businessServices.elementAt(0);

           // returning service object
            return new org.jafer.registry.uddi.model.BusinessService(tModelManager, newBusinessService);

        }
        catch (InvalidAuthorisationDetailsException e)
        {
            logger.info(e.toString());
            throw e;
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
            discardAuthorisationToken(token);
            logger.exiting("ServiceManager", "registerService(provider,name)");
        }
    }
    
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
    public Service updateService(Service service) throws InvalidAuthorisationDetailsException, RegistryException
    {
        logger.entering("ServiceManager", "updateBusinessEntity");
        String token = null;
        try
        {
            token = getAuthorisationToken();
            // cast to internal type;
            org.jafer.registry.uddi.model.BusinessService businessService = (org.jafer.registry.uddi.model.BusinessService) service;

            // store the internal business entity for processing
            Vector entities = new Vector();
            entities.add(businessService.getUDDIBusinessService());
            // update the internal business entity
            ServiceDetail serviceDetail = registryConnection.save_service(token, entities);
            // store the new value into business entity
            businessService.setUDDIBusinessService((org.uddi4j.datatype.service.BusinessService) serviceDetail
                    .getBusinessServiceVector().firstElement());
            // return the business entity back
            return businessService;
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            logger.info(e.toString());
            throw e;
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
            discardAuthorisationToken(token);
            logger.exiting("ServiceManager", "updateBusinessEntity");
        }
    }

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
    public void deleteService(Service service) throws InvalidAuthorisationDetailsException, RegistryException
    {
        logger.entering("ServiceManager", "deleteBusinessService(service)");
        try
        {
              deleteService(service.getId());
        }        
        finally
        {
            logger.exiting("ServiceManager", "deleteBusinessService(service)");
        }
    }

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
    public void deleteService(ServiceInfo serviceInfo) throws InvalidAuthorisationDetailsException, RegistryException
    {
        logger.entering("ServiceManager", "deleteBusinessService(serviceInfo)");
        try
        {
              deleteService(serviceInfo.getId());
        }        
        finally
        {
            logger.exiting("ServiceManager", "deleteBusinessService(serviceInfo)");
        }
    }

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
    public void deleteService(String id) throws InvalidAuthorisationDetailsException, RegistryException
    {
        logger.entering("ServiceManager", "deleteBusinessService(id)");
        String token = null;
        try
        {
            token = getAuthorisationToken();
             registryConnection.delete_service(token, id);
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            logger.info(e.toString());
            throw e;
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
            discardAuthorisationToken(token);
            logger.exiting("ServiceManager", "deleteBusinessService(id)");
        }
    }
    
    /**
     * This method creates a binding template object in the registry so that it
     * can be attached to the service
     * 
     * @param serviceKey The service to attach the binding template to
     * @param protocol The protocol type Z3950, SRW for tmodels
     * @param accessPointURL The url to the access point
     * @param accessPointType The type of access point endpoint or wsdl file
     * @return The instance of the binding template
     * @throws InvalidAuthorisationDetailsException
     * @throws RegistryException
     */
    private BindingTemplate createBindingTemplateForAccessPoint(String serviceKey, Protocol protocol, String accessPointURL,
            String accessPointType) throws InvalidAuthorisationDetailsException, RegistryException
    {
        logger.entering("ServiceManager", "createBindingTemplateForAccessPoint");
        String token = null;
        try
        {
            token = getAuthorisationToken();
            // create the new uddi4j binding template
            BindingTemplate binding = createBindingTemplate(tModelManager, serviceKey, protocol, accessPointURL, accessPointType);

            // register the created binding
            Vector templates = new Vector();
            templates.add(binding);
            BindingDetail bindingDetail = registryConnection.save_binding(token, templates);
            // return a new Binding Template
            return (BindingTemplate) bindingDetail.getBindingTemplateVector().firstElement();
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            logger.info(e.toString());
            throw e;
        }
        catch (org.uddi4j.UDDIException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl("Failed creating: " + accessPointType, e);
            logger.severe(exc.toString());
            throw exc;
        }
        catch (TransportException e)
        {
            RegistryExceptionImpl exc = new RegistryExceptionImpl("Failed creating: " + accessPointType, e);
            logger.severe(exc.toString());
            throw exc;
        }
        finally
        {
            discardAuthorisationToken(token);
            logger.exiting("ServiceManager", "createBindingTemplateForAccessPoint");
        }
    }

    /**
     * static function that creates a Binding Template ready for registering
     * with the UBR. This is not exposed on the interface to the caller and
     * therefore should not be used outside of this framework.
     * 
     * @param serviceKey The service to attach the binding template to
     * @param protocol The protocol type Z3950, SRW for tmodels
     * @param accessPointURL The url to the access point
     * @param accessPointType The type of access point endpoint or wsdl file
     * @return The instance of the binding template
     * @throws RegistryException
     */
    public static BindingTemplate createBindingTemplate(TModelManager tModelManager, String serviceKey, Protocol protocol,
            String accessPointURL, String accessPointType) throws RegistryException
    {
        // create the new uddi4j
        BindingTemplate binding = new BindingTemplate();
        // set blank binding key so it is allocated by the registry
        binding.setBindingKey("");
        // set a default description to accessPointType so we can use that
        // when looking for specific access point types
        binding.setDefaultDescriptionString(accessPointType);
        // set relationship to service
        binding.setServiceKey(serviceKey);
        binding.setAccessPoint(new AccessPoint(accessPointURL, accessPointType));
        // now add the TModels for the protocol TModel
        TModelInstanceDetails tmodels = new TModelInstanceDetails();
        // get all the actual tmodels for the prtocol
        Iterator iter = tModelManager.getProtocolTModel(protocol).getActualModels().iterator();
        while (iter.hasNext())
        {
            // get the TModel key
            String key = ((org.uddi4j.datatype.tmodel.TModel) iter.next()).getTModelKey();
            // add a new TModel Instance Info to tmodels
            tmodels.add(new TModelInstanceInfo(key));
        }
        // add tmodels to binding
        binding.setTModelInstanceDetails(tmodels);
        return binding;
    }

}
