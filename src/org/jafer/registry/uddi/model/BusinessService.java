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

package org.jafer.registry.uddi.model;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jafer.registry.RegistryException;
import org.jafer.registry.model.Category;
import org.jafer.registry.model.CategoryDoesNotExistException;
import org.jafer.registry.model.InvalidLengthException;
import org.jafer.registry.model.InvalidNameException;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.ServiceDoesNotExistException;
import org.jafer.registry.uddi.ServiceManager;
import org.jafer.registry.uddi.TModelManager;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.binding.BindingTemplate;
import org.uddi4j.datatype.binding.BindingTemplates;

/**
 * This class implements a service for a UDDI registry. This Class should never
 * be directly instantiated. <b>Updates made using this class will only be made
 * to the uddi registry when the service provider is updated through the service
 * manager </b>
 */
public class BusinessService implements org.jafer.registry.model.Service
{

    /**
     * Stores a reference to the maximumn number of characters for the name
     * field
     */
    private static final int MAX_NAME_LENGTH = 255;

    /**
     * Stores a reference to the maximumn number of characters for the url field
     */
    private static final int MAX_URL_LENGTH = 4096;

    /**
     * Stores a reference to the maximumn number of characters for the
     * description field
     */
    private static final int MAX_DESCRIPTION_LENGTH = 255;

    /**
     * Stores a reference to the TModelManager that loads and initialises all
     * the required TModels. It is expected to have already been initialised
     * when it is used by this class
     */
    private TModelManager tModelManager = null;

    /**
     * Stores a reference to the binding template endpoint access type. <br>
     * <b>Note: This needs changing to 'endPoint' when moving to UDDI V3 </b>
     */
    public final static String END_POINT = "http";

    /**
     * Stores a reference to the binding template wsdl access type. <br>
     * <b>Note: This needs changing to 'wsdlDeployment' when moving to UDDI V3
     * </b>
     */
    public final static String WSDL_POINT = "other";

    /**
     * Stores a reference to the underlying business service object
     */
    private org.uddi4j.datatype.service.BusinessService businessService = null;

    /**
     * Constructor of the business service
     *
     * @param tModelManager The tmodel manager for the registry being accessed
     *        by this service
     * @param service The actual UDDI Business Service
     */
    public BusinessService(TModelManager tModelManager, org.uddi4j.datatype.service.BusinessService service)
    {
        this.tModelManager = tModelManager;
        businessService = service;
    }

    /**
     * Returns the UDDI business service object. This is not exposed on the
     * interface to the caller and therefore should not be used outside of this
     * framework.
     *
     * @return Returns the businessEntity.
     */
    public org.uddi4j.datatype.service.BusinessService getUDDIBusinessService()
    {
        return businessService;
    }

    /**
     * Sets the UDDI business service object. This is not exposed on the
     * interface to the caller and therefore should not be used outside of this
     * framework.
     *
     * @param businessService The businessService to set.
     */
    public void setUDDIBusinessService(org.uddi4j.datatype.service.BusinessService businessService)
    {
        this.businessService = businessService;
    }

    /**
     * Returns the uniquie id of this service.
     *
     * @return The service id.
     */
    public String getId()
    {
        return businessService.getServiceKey();
    }

    /**
     * Returns the ID of the service provider that owns this service
     *
     * @return The owning service provider ID
     */
    public String getServiceProviderId()
    {
        return businessService.getBusinessKey();
    }

    /**
     * Returns the name of this service.
     *
     * @return The service name. An empty string will be returned if not found.
     */
    public String getName()
    {
        String name = businessService.getDefaultNameString();
        // if not set return an empty string
        if (name == null)
        {
            name = "";
        }
        return name;
    }

    /**
     * Sets the name of this service. This will only actually be applied when
     * the service is updated with the registry via the service manager.
     *
     * @param name The name to set
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public void setName(String name) throws InvalidNameException, InvalidLengthException
    {
        if (name == null || name.length() == 0)
        {
            throw new InvalidNameException();
        }
        if (name.length() > MAX_NAME_LENGTH)
        {
            throw new InvalidLengthException("name", MAX_NAME_LENGTH);
        }
        businessService.setDefaultName(new Name(name));
    }

    /**
     * Returns the description of the service.
     *
     * @return The business service name. An empty string will be returned if
     *         not found.
     */
    public String getDescription()
    {
        // return empty string if null
        return businessService.getDefaultDescriptionString() == null ? "" : businessService.getDefaultDescriptionString();
    }

    /**
     * Sets the description of th service. This will only actually be applied
     * when the service is updated with the registry via the service manager.
     *
     * @param description The description to set
     * @throws InvalidLengthException
     */
    public void setDescription(String description) throws InvalidLengthException
    {
        if (description.length() > MAX_DESCRIPTION_LENGTH)
        {
            throw new InvalidLengthException("description", MAX_DESCRIPTION_LENGTH);
        }
        businessService.setDefaultDescriptionString(description);
    }

    /**
     * This method returns all the categories supported linked to the service.
     * Some of these categories may represent categories outside of this toolkit
     * but they are returned to allow the caller to remove them if they wish.
     *
     * @return A list of categories
     */
    public List getCategories()
    {
        return org.jafer.registry.uddi.model.Category.getCategories(businessService.getCategoryBag());
    }

    /**
     * Adds a jafer defined category to the service if it does not already
     * exist. This will only actually be applied when the service provider is
     * updated with the registry via the service manager.
     *
     * @param category The category to add
     */
    public void addCategory(Category category)
    {
        // convert to internal format and created keyed reference from it
        org.jafer.registry.uddi.model.Category cat = (org.jafer.registry.uddi.model.Category) category;
        businessService.setCategoryBag(cat.addToCategoryBag(businessService.getCategoryBag()));
    }

    /**
     * Removes the category from the service if it exists. This will only
     * actually be applied when the service provider is updated with the
     * registry via the service manager.
     *
     * @param category The category to delete
     * @throws CategoryDoesNotExistException
     */
    public void removeCategory(Category category) throws CategoryDoesNotExistException
    {
        // convert to internal format and created keyed reference from it
        org.jafer.registry.uddi.model.Category cat = (org.jafer.registry.uddi.model.Category) category;
        businessService.setCategoryBag(cat.removeFromCategoryBag(businessService.getCategoryBag()));
        // if we no longer have any categories attached must remove the category
        // bag from entity so it updates correctly
        if (businessService.getCategoryBag().getKeyedReferenceVector().size() == 0)
        {
            removeAllCategories();
        }
    }

    /**
     * Removes all the categories from the service provider. This will only
     * actually be applied when the service provider is updated with the
     * registry via the service manager.
     */
    public void removeAllCategories()
    {
        businessService.setCategoryBag(null);
    }

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
    public String getAccessUrl(Protocol protocol) throws RegistryException
    {
            return getAccessPointURL(protocol, END_POINT);
    }

    /**
     * Sets the access url for the service. This can be used to connect to the
     * actual service. This will only actually be applied when the service
     * provider is updated with the registry via the service manager.
     *
     * @param protocol The protocol type of the accesspoint to set
     * @param url The url to the access point
     * @throws RegistryException
     * @throws InvalidLengthException
     */
    public void setAccessUrl(Protocol protocol, String url) throws RegistryException, InvalidLengthException
    {
        if (url.length() > MAX_URL_LENGTH)
        {
            throw new InvalidLengthException("access url", MAX_URL_LENGTH);
        }
        setAccessPointURL(protocol, END_POINT, url);
    }

    /**
     * Returns the WSDL file for the service. This describes the service and can
     * also point to a connection point internally. This will only actually be
     * applied when the service provider is updated with the registry via the
     * service manager.
     *
     * @param protocol The protocol type of the accesspoint to find
     * @return The wsdl url. An empty string will be returned if not found.
     * @throws RegistryException
     * @throws RegistryException
     */
    public String getWSDLUrl(Protocol protocol) throws RegistryException

    {
        return getAccessPointURL(protocol, WSDL_POINT);
    }

    /**
     * Sets the WSDL file for the service. This describes the service and can
     * also point to a connection point internally. This will only actually be
     * applied when the service provider is updated with the registry via the
     * service manager.
     *
     * @param url The url to the access point to set
     * @param protocol The protocol type of the accesspoint
     * @throws InvalidLengthException
     */
    public void setWSDLUrl(Protocol protocol, String url) throws RegistryException, InvalidLengthException
    {
        if (url.length() > MAX_URL_LENGTH)
        {
            throw new InvalidLengthException("wsdl url", MAX_URL_LENGTH);
        }
        setAccessPointURL(protocol, WSDL_POINT, url);
    }

    /**
     * This method checks the service to see if it supports the specified
     * protocol
     *
     * @param protocol The protocol type of the accesspoint to set
     * @return true if the template supports the protocol
     * @throws RegistryException
     */
    public boolean supportsProtocol(Protocol protocol) throws RegistryException
    {
        // we should have two binding templates if created by this api
        if (businessService.getBindingTemplates() != null)
        {
            Iterator iter = businessService.getBindingTemplates().getBindingTemplateVector().iterator();
            while (iter.hasNext())
            {
                if (supportsProtocol((BindingTemplate) iter.next(), protocol))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method returns all the business services for the business entity.
     * This is not exposed on the interface to the caller and therefore should
     * not be used outside of this framework.
     *
     * @param tModelManager The tmodel manager for the registry being accessed
     *        by this service
     * @param uddiBusinessServices A list of uddi business service descriptions
     * @return A list of ServiceInfo objects
     */
    public static List extractBusinessServiceInfos(TModelManager tModelManager, List uddiBusinessServices)
    {
        Vector list = new Vector();
        Iterator iter = uddiBusinessServices.iterator();
        // loop round processing each uddi business service
        while (iter.hasNext())
        {
            org.uddi4j.datatype.service.BusinessService service = (org.uddi4j.datatype.service.BusinessService) iter.next();
            list.add(new ServiceInfo(service));
        }
        return list;
    }

    /**
     * Adds the service to the list of uddi4j business services. This is not
     * exposed on the interface to the caller and therefore should not be used
     * outside of this framework.
     *
     * @param uddiBusinessServices the list of uddi4j business services to add
     *        to
     */
    public void addToServicesList(List uddiBusinessServices)
    {
        // check if this service already exists
        Iterator iter = uddiBusinessServices.iterator();
        while (iter.hasNext())
        {
            org.uddi4j.datatype.service.BusinessService service = (org.uddi4j.datatype.service.BusinessService) iter.next();
            // check if the service keys match
            if (this.getId().equals(service.getServiceKey()))
            {
                // service already added so ignore
                return;
            }
        }
        // not found so add it
        uddiBusinessServices.add(this.getUDDIBusinessService());
    }

    /**
     * Removes the service from the list of uddi4j business services. This is
     * not exposed on the interface to the caller and therefore should not be
     * used outside of this framework.
     *
     * @param uddiBusinessServices The list of uddi services to remove from
     * @throws ServiceDoesNotExistException
     */
    public void removeFromServicesList(List uddiBusinessServices) throws ServiceDoesNotExistException
    {
        //signals if the service was removed
        boolean removed = false;

        // check if this service already exists
        Iterator iter = uddiBusinessServices.iterator();
        while (iter.hasNext())
        {
            org.uddi4j.datatype.service.BusinessService service = (org.uddi4j.datatype.service.BusinessService) iter.next();
            // check if the service keys match
            if (this.getId().equals(service.getServiceKey()))
            {
                // remove the service
                uddiBusinessServices.remove(service);
                removed = true;
                break;
            }
        }
        // check if we managed to remove
        if (!removed)
        {
            // did not remove so throw exception
            throw new ServiceDoesNotExistException(this.getName());
        }
    }

    /**
     * Returns the URL for the specified protocol access point
     *
     * @param protocol the protocol to search under
     * @param accessPointType The type of access point looking for
     * @return The URL found
     * @throws RegistryException
     */
    private String getAccessPointURL(Protocol protocol, String accessPointType) throws RegistryException
    {
        // we should have two binding templates if created by this api
        if (businessService.getBindingTemplates() != null)
        {
            Iterator iter = businessService.getBindingTemplates().getBindingTemplateVector().iterator();
            while (iter.hasNext())
            {
                BindingTemplate template = (BindingTemplate) iter.next();
                // is the access point of the specified type
                if (isAccessPointType(template, protocol, accessPointType))
                {
                    return template.getAccessPoint().getText();
                }
            }
        }
        return "";
    }

    /**
     * sets the URL for the specified protocol access point
     *
     * @param protocol the protocol to search under
     * @param accessPointType The type of access point looking to set URL on
     * @param url The URL to set
     * @throws RegistryException
     */
    private void setAccessPointURL(Protocol protocol, String accessPointType, String url) throws RegistryException
    {
        // if we have no binding templates then create holder
        if (businessService.getBindingTemplates() == null)
        {
            businessService.setBindingTemplates(new BindingTemplates());
        }
        // find and set value
        Iterator iter = businessService.getBindingTemplates().getBindingTemplateVector().iterator();
        while (iter.hasNext())
        {
            BindingTemplate template = (BindingTemplate) iter.next();
            // is the access point of the specified type
            if (isAccessPointType(template, protocol, accessPointType))
            {
                // If the url is not empty we need to replace the text otherwise
                // the access point must be removed for the update to be
                // successful as empty access point URLs are not allowed
                if (url.length() > 0)
                {
                    template.getAccessPoint().setText(url);
                }
                else
                {
                    // remove the binding template and return
                    businessService.getBindingTemplates().remove(template);
                }
                return;
            }
        }
        // we have not found it so need to add a blank entry that will be
        // created when the service manager next updates the service if the url
        // is not empty
        if (url.length() > 0)
        {
            BindingTemplate binding = ServiceManager
                    .createBindingTemplate(tModelManager, getId(), protocol, url, accessPointType);
            businessService.getBindingTemplates().add(binding);
        }
    }

    /**
     * This method checks the template to see if it describes the specified
     * access point type. A match is defined as the binding template decription
     * and accesspoint url type having the same text as the accessPointType
     * supplied <br>
     * <b>Note: This needs changing to just check the useType when moving to
     * UDDI V3 </b>
     *
     * @param template The template to check
     * @param protocol The protocol type of the accesspoint to set
     * @param accessPointType The access point type to check for
     * @return true if the template describes the specified access point type
     * @throws RegistryException
     */
    private boolean isAccessPointType(BindingTemplate template, Protocol protocol, String accessPointType)
            throws RegistryException
    {
        // check it supports the protocol first
        if (supportsProtocol(template, protocol))
        {
            return true;
            /**
             * @todo This seems overkill - simplify for now but check with Andy
            // first check the urlType match
            if (template.getAccessPoint().getURLType().equalsIgnoreCase(accessPointType))
            {
                // now we do a secondary check on the default description. This
                // is only done at the moment as UBR's support v2 not v3. This
                // can be removed when V3 registries are more common
                if (template.getDefaultDescriptionString() != null
                        && template.getDefaultDescriptionString().equalsIgnoreCase(accessPointType))
                {
                    return true;
                }
            }
            */
        }
        return false;
    }

    /**
     * This method checks the template to see if it supports the specified
     * protocol
     *
     * @param template The template to check
     * @param protocol The protocol type of the accesspoint to set
     * @return true if the template supports the protocol
     * @throws RegistryException
     */
    private boolean supportsProtocol(BindingTemplate template, Protocol protocol) throws RegistryException
    {
        return tModelManager.getProtocolTModel(protocol).supportsTModel(template.getTModelInstanceDetails());
    }

}
