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

import org.jafer.registry.model.Category;
import org.jafer.registry.model.CategoryDoesNotExistException;
import org.jafer.registry.model.Contact;
import org.jafer.registry.model.InvalidLengthException;
import org.jafer.registry.model.InvalidNameException;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceDoesNotExistException;
import org.jafer.registry.uddi.TModelManager;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.business.Contacts;
import org.uddi4j.datatype.service.BusinessServices;
import org.uddi4j.util.DiscoveryURL;
import org.uddi4j.util.DiscoveryURLs;

/**
 * This class implements a service provider for a UDDI registry. This Class
 * should never be directly instantiated. <b>Updates made using this class will
 * only be made to the uddi registry when the service provider is updated
 * through the service manager </b>
 */
public class BusinessEntity implements org.jafer.registry.model.ServiceProvider
{

    /**
     * Stores a reference to the maximumn number of characters for the name
     * field
     */
    private static final int MAX_NAME_LENGTH = 255;

    /**
     * Stores a reference to the maximumn number of characters for the home page
     * field
     */
    private static final int MAX_HOMEPAGE_LENGTH = 4096;

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
     * Stores a reference to the HOMEPAGE use type for discovery urls
     */
    private static final String HOMEPAGE = "homepage";

    /**
     * Stores a reference to the underlying business entity object
     */
    private org.uddi4j.datatype.business.BusinessEntity businessEntity = null;

    /**
     * Constructor of the business entity
     * 
     * @param tModelManager The tmodel manager for the registry being accessed
     *        by this provider
     * @param entity The actual UDDI Business Entity
     */
    public BusinessEntity(TModelManager tModelManager, org.uddi4j.datatype.business.BusinessEntity entity)
    {
        this.tModelManager = tModelManager;
        businessEntity = entity;
    }

    /**
     * Returns the uniquie id of this service provider.
     * 
     * @return The service id.
     */
    public String getId()
    {
        return businessEntity.getBusinessKey();
    }

    /**
     * Returns the UDDI business entity object. This is not exposed on the
     * interface to the caller and therefore should not be used outside of this
     * framework.
     * 
     * @return Returns the businessEntity.
     */
    public org.uddi4j.datatype.business.BusinessEntity getUDDIBusinessEntity()
    {
        return businessEntity;
    }

    /**
     * Sets the UDDI business entity object. This is not exposed on the
     * interface to the caller and therefore should not be used outside of this
     * framework.
     * 
     * @param businessEntity The businessEntity to set.
     */
    public void setUDDIBusinessEntity(org.uddi4j.datatype.business.BusinessEntity businessEntity)
    {
        this.businessEntity = businessEntity;
    }

    /**
     * Returns the name of the service provider.
     * 
     * @return The service provider name. An empty string will be returned if
     *         not found.
     */
    public String getName()
    {
        return businessEntity.getDefaultName().getText();
    }

    /**
     * Sets the name for the service provider. This will only actually be
     * applied when the service provider is updated with the registry via the
     * service manager.
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
            throw new InvalidLengthException("description", MAX_NAME_LENGTH);
        }
        businessEntity.setDefaultName(new Name(name));
    }

    /**
     * Returns the description for the service provider.
     * 
     * @return The service provider description. An empty string will be
     *         returned if not found.
     */
    public String getDescription()
    {
        // return empty string if null
        return businessEntity.getDefaultDescriptionString() == null ? "" : businessEntity.getDefaultDescriptionString();
    }

    /**
     * Sets the description for the service provider. This will only actually be
     * applied when the service provider is updated with the registry via the
     * service manager.
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
        businessEntity.setDefaultDescriptionString(description);
    }

    /**
     * Returns the home page for the service provider.
     * 
     * @return The url string to the home page. An empty string will be returned
     *         if not found.
     */
    public String getHomePage()
    {
        // make sure we have discovery URLs
        if (businessEntity.getDiscoveryURLs() != null)
        {
            // get an iterator round the discovery URLs
            Iterator iter = businessEntity.getDiscoveryURLs().getDiscoveryURLVector().iterator();
            while (iter.hasNext())
            {
                // get the next url to examine
                DiscoveryURL url = (DiscoveryURL) iter.next();
                if ((url.getUseType() != null) && (url.getUseType().equals(HOMEPAGE)))
                {
                    return businessEntity.getDiscoveryURLs().get(0).getText();
                }
            }
        }
        // nothing found return empty string
        return "";
    }

    /**
     * Sets the home page for the service provider. This will only actually be
     * applied when the service provider is updated with the registry via the
     * service manager.
     * 
     * @param newUrl The url string to the home page
     * @throws InvalidLengthException
     */
    public void setHomePage(String newUrl) throws InvalidLengthException
    {
        if (newUrl.length() > MAX_HOMEPAGE_LENGTH)
        {
            throw new InvalidLengthException("hoome page", MAX_HOMEPAGE_LENGTH);
        }
        // make sure we have discovery URLs
        if (businessEntity.getDiscoveryURLs() != null)
        {
            // get an iterator round the discovery URLs
            Iterator iter = businessEntity.getDiscoveryURLs().getDiscoveryURLVector().iterator();
            while (iter.hasNext())
            {
                // get the next url to examine
                DiscoveryURL url = (DiscoveryURL) iter.next();
                if ((url.getUseType() != null) && (url.getUseType().equals(HOMEPAGE)))
                {
                    // found home page type so update it
                    businessEntity.getDiscoveryURLs().get(0).setText(newUrl);
                    return;
                }
            }
        }
        else
        {
            // create discoverURL structure for new phone
            businessEntity.setDiscoveryURLs(new DiscoveryURLs());
        }
        // add a new one that is the home page
        businessEntity.getDiscoveryURLs().add(new DiscoveryURL(newUrl, HOMEPAGE));
    }

    /**
     * Get a COPY of the contact information for the service provider. This will
     * relate to the first UDDI business entity contact details object. If no
     * contact information is found then null will be returned.
     * 
     * @return Copy of the contact information or null if no contact assotiated
     */
    public Contact getContact()
    {
        // do we have a contact to return
        if (businessEntity.getContacts() != null && businessEntity.getContacts().size() > 0)
        {
            // extract the contact object
            org.uddi4j.datatype.business.Contact uddiContact = (org.uddi4j.datatype.business.Contact) businessEntity
                    .getContacts().get(0);
            return new org.jafer.registry.uddi.model.Contact(uddiContact);
        }
        return null;
    }

    /**
     * Set the contact information for the service provider. This will relate to
     * the first UDDI business entity contact details object. If it already
     * exists it will be overwritten with this contact. This will only actually
     * be applied when the service provider is updated with the registry via the
     * service manager.
     * 
     * @param newContact The contact information
     */
    public void setContact(Contact newContact)
    {
        org.uddi4j.datatype.business.Contact uddiContact = null;
        // cast to internal version
        org.jafer.registry.uddi.model.Contact contact = (org.jafer.registry.uddi.model.Contact) newContact;
        // do we have a contact to update
        if (businessEntity.getContacts() != null && businessEntity.getContacts().size() > 0)
        {
            // extract the contact object
            uddiContact = (org.uddi4j.datatype.business.Contact) businessEntity.getContacts().get(0);
            // get the extracted object updated with contacts details
            contact.updateUDDIContact(uddiContact);
        }
        else
        {
            // refresh the structure as currently no contacts. This also forces
            // the create when not there
            businessEntity.setContacts(new Contacts());
            // need to create a new uddiContact
            uddiContact = new org.uddi4j.datatype.business.Contact();
            // get the extracted object updated with contacts details
            contact.updateUDDIContact(uddiContact);
            businessEntity.getContacts().add(uddiContact);
        }
    }

    /**
     * Removes the contact information for the service provider. This will
     * relate to the first UDDI business entity contact details object. This
     * will only actually be applied when the service provider is updated with
     * the registry via the service manager.
     */
    public void removeContact()
    {
        // do we have more than one contact
        if (businessEntity.getContacts() != null && businessEntity.getContacts().size() > 1)
        {
            // yes so just delete the first one
            businessEntity.getContacts().getContactVector().remove(0);
        }
        // if we only have one contact or none at all just remove everything
        businessEntity.setContacts(null);
    }

    /**
     * This method returns all the categories supported linked to the service
     * provider. Some of these categories may represent categories outside of
     * this toolkit but they are returned to allow the caller to remove them if
     * they wish.
     * 
     * @return A list of categories
     */
    public List getCategories()
    {
        return org.jafer.registry.uddi.model.Category.getCategories(businessEntity.getCategoryBag());
    }

    /**
     * Adds a jafer defined category to the service provider if it does not
     * already exist. This will only actually be applied when the service
     * provider is updated with the registry via the service manager.
     * 
     * @param category The category to add
     */
    public void addCategory(Category category)
    {
        // convert to internal format and created keyed reference from it
        org.jafer.registry.uddi.model.Category cat = (org.jafer.registry.uddi.model.Category) category;
        businessEntity.setCategoryBag(cat.addToCategoryBag(businessEntity.getCategoryBag()));
    }

    /**
     * Removes the category from the service provider if it exists. This will
     * only actually be applied when the service provider is updated with the
     * registry via the service manager.
     * 
     * @param category The category to delete
     * @throws CategoryDoesNotExistException
     */
    public void removeCategory(Category category) throws CategoryDoesNotExistException
    {
        // convert to internal format and created keyed reference from it
        org.jafer.registry.uddi.model.Category cat = (org.jafer.registry.uddi.model.Category) category;
        businessEntity.setCategoryBag(cat.removeFromCategoryBag(businessEntity.getCategoryBag()));
        // if we no longer have any categories attached must remove the category
        // bag from entity so it updates correctly
        if (businessEntity.getCategoryBag().getKeyedReferenceVector().size() == 0)
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
        businessEntity.setCategoryBag(null);
    }

    /**
     * This method returns all the know services for the service provider at the
     * time of retrieval. ServiceInfo objects are returned that can be used to
     * delete the service or to obtain the full detail odf the service through
     * the service locator.
     * 
     * @return A list of ServiceInfo objects describing the known services
     */
    public List getServices()
    {
        List list = new Vector();
        // make sure there are business services to return
        if (businessEntity.getBusinessServices() != null)
        {
            // extract the list of uddi business services from the business
            // entity
            List services = (List) businessEntity.getBusinessServices().getBusinessServiceVector();
            // extract a list of jafer business ervices from the list
            list = org.jafer.registry.uddi.model.BusinessService.extractBusinessServiceInfos(tModelManager, services);
        }
        return list;
    }

    /**
     * This method adds the service to the service provider. This will only
     * actually be applied when the service provider is updated with the
     * registry via the service manager. <br>
     * <b>Note: The service added must exist in the registry for the update to
     * succeed. </b>
     * 
     * @param service The service to add
     */
    public void addService(Service service)
    {
        // cast to internal type;
        org.jafer.registry.uddi.model.BusinessService businessService = (org.jafer.registry.uddi.model.BusinessService) service;

        //  make sure there are business services to return
        if (businessEntity.getBusinessServices() == null)
        {
            // create an empty instance
            businessEntity.setBusinessServices(new BusinessServices());
        }
        businessService.addToServicesList(businessEntity.getBusinessServices().getBusinessServiceVector());
    }

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
    public void removeService(Service service) throws ServiceDoesNotExistException
    {
        // cast to internal type;
        org.jafer.registry.uddi.model.BusinessService businessService = (org.jafer.registry.uddi.model.BusinessService) service;

        //  make sure there are business services to return
        if (businessEntity.getBusinessServices() == null)
        {
            throw new ServiceDoesNotExistException(service.getName());
        }
        // remove the business service
        businessService.removeFromServicesList(businessEntity.getBusinessServices().getBusinessServiceVector());
        // check if we have any services left
        if (businessEntity.getBusinessServices().size() == 0)
        {
            removeAllServices();
        }
    }

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
    public void removeService(org.jafer.registry.model.ServiceInfo serviceInfo) throws ServiceDoesNotExistException
    {
        // cast to internal type;
        ServiceInfo service = (ServiceInfo) serviceInfo;

        //  make sure there are business services to return
        if (businessEntity.getBusinessServices() == null)
        {
            throw new ServiceDoesNotExistException(serviceInfo.getName());
        }
        // remove the business service
        service.removeFromServicesList(businessEntity.getBusinessServices().getBusinessServiceVector());
        // check if we have any services left
        if (businessEntity.getBusinessServices().size() == 0)
        {
            removeAllServices();
        }
    }

    /**
     * This method removes all the services. This will only actually be applied
     * when the service provider is updated with the registry via the service
     * manager.
     */
    public void removeAllServices()
    {
        businessEntity.setBusinessServices(null);
    }

    /**
     * If a service is not owned directly by the provider and has been deleted
     * then the relationship to this provider is left in an invlid state. This
     * method cleans the invalid services so that an update will be successful.
     * This method will not cater for shared services that are deleted since
     * this provider instance was retreieved. This is not exposed on the
     * interface to the caller and therefore should not be used outside of this
     * framework.
     * 
     * @throws ServiceDoesNotExistException
     */
    public void removeAnyBadServiceKeys() throws ServiceDoesNotExistException
    {
        // get an iterator round the services
        Iterator iter = getServices().iterator();
        Vector toDelete = new Vector();
        // process each iterator
        while (iter.hasNext())
        {
            org.jafer.registry.model.ServiceInfo info = (org.jafer.registry.model.ServiceInfo) iter.next();
            if (info.getName().length() == 0)
            {
                // need to delete this service as has become bad
                toDelete.add(info);
            }
        }

        // iterate deleteing all the identified service infos
        iter = toDelete.iterator();
        while (iter.hasNext())
        {
            org.jafer.registry.model.ServiceInfo info = (org.jafer.registry.model.ServiceInfo) iter.next();
            removeService(info);
        }
    }
}
