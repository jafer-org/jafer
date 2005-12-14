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

import org.jafer.registry.model.ServiceProviderInfo;
import org.uddi4j.response.BusinessList;

/**
 * When a search can return multiple service providers this class is returned in
 * the list as ServiceProviderInfo's. It provides a cut down view of the found
 * service providers to reduce excess data being returned. It allows the caller
 * to decide if they want the full serive provider information which can be
 * obtained by supplying this object to the service manager get calls.
 */
public class BusinessInfo implements ServiceProviderInfo
{

    /**
     * Stores a reference to the business info object returned from the uddi
     * registry
     */
    private org.uddi4j.response.BusinessInfo businessInfo = null;

    /**
     * Constructor
     * 
     * @param businessInfo The UDDI business info object
     */
    public BusinessInfo(org.uddi4j.response.BusinessInfo businessInfo)
    {
        this.businessInfo = businessInfo;
    }


    /**
     * Returns the uniquie id of this service provider found
     * 
     * @return The service id. 
     */
    public String getId()
    {
        return businessInfo.getBusinessKey();
    }

    /**
     * Returns the name of the service provider found.
     * 
     * @return The service provider name
     */
    public String getName()
    {
        return businessInfo.getDefaultNameString();
    }

    /**
     * Returns the description for the service provider found.
     * 
     * @return The service provider description
     */
    public String getDescription()
    {
        return businessInfo.getDefaultDescriptionString();
    }

    /**
     * This method creates a list of businessInfo objects from the supplied
     * business list. This is not exposed on the interface to the caller and
     * therefore should not be used outside of this framework.
     * 
     * @param businessList The business list to process
     * @return A list of Business Info objects
     */
    public static List extractBusinessInfos(BusinessList businessList)
    {
        Vector busInfos = new Vector();

        // do we have any business infos to process
        if (businessList.getBusinessInfos() != null && businessList.getBusinessInfos().size() > 0)
        {
            //loop round and process each returned result
            Iterator iter = businessList.getBusinessInfos().getBusinessInfoVector().iterator();
            while (iter.hasNext())
            {
                // add a new BusinessInfo object to the returned list
                busInfos.add(new BusinessInfo((org.uddi4j.response.BusinessInfo) iter.next()));
            }
        }
        return busInfos;
    }
}
