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

import org.jafer.registry.model.ServiceDoesNotExistException;
import org.uddi4j.response.ServiceList;

/**
 * When a search can return multiple service providers this class is returned in
 * the list as ServiceProviderInfo's. It provides a cut down view of the found
 * service providers to reduce excess data being returned. It allows the caller
 * to decide if they want the full serive provider information which can be
 * obtained by supplying this object to the service manager get calls.
 */
public class ServiceInfo implements org.jafer.registry.model.ServiceInfo
{

    /**
     * Stores a reference to the service info object returned from the uddi
     * registry
     */
    private org.uddi4j.response.ServiceInfo serviceInfo = null;

    /**
     * Constructor
     * 
     * @param serviceInfo The UDDI service info object
     */
    public ServiceInfo(org.uddi4j.response.ServiceInfo serviceInfo)
    {
        this.serviceInfo = serviceInfo;
    }

    /**
     * Create service info object from a given uddi business service
     * 
     * @param service The business service to create an Info object from
     */
    public ServiceInfo(org.uddi4j.datatype.service.BusinessService service)
    {
        this.serviceInfo = new org.uddi4j.response.ServiceInfo(service.getServiceKey(), service.getDefaultNameString());
    }

    /**
     * Returns the uniquie id of the service found.
     * 
     * @return The service id. 
     */
    public String getId()
    {
        return serviceInfo.getServiceKey();
    }

    /**
     * Returns the name of the service found.
     * 
     * @return The service name, empty string if not set
     */
    public String getName()
    {
        String name =  serviceInfo.getDefaultNameString();
        // if not set return an empty string
        if (name == null)
        {
            name = "";
        }
        return name;
    }

    /**
     * This method creates a list of serviceInfo objects from the supplied
     * service list. This is not exposed on the interface to the caller and
     * therefore should not be used outside of this framework.
     * 
     * @param serviceList The service list to process
     * @return A list of service Info objects
     */
    public static List extractServiceInfos(ServiceList serviceList)
    {
        Vector serviceInfos = new Vector();

        // do we have any business infos to process
        if (serviceList.getServiceInfos() != null && serviceList.getServiceInfos().size() > 0)
        {
            //loop round and process each returned result
            Iterator iter = serviceList.getServiceInfos().getServiceInfoVector().iterator();
            while (iter.hasNext())
            {
                // add a new BusinessInfo object to the returned list
                serviceInfos.add(new ServiceInfo((org.uddi4j.response.ServiceInfo) iter.next()));
            }
        }
        return serviceInfos;
    }

    /**
     * Removes the service specified by this service info from the list of
     * uddi4j business services. This is not exposed on the interface to the
     * caller and therefore should not be used outside of this framework.
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
}
