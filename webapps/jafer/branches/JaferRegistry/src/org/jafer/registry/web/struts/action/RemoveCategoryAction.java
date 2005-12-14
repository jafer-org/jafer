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

package org.jafer.registry.web.struts.action;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jafer.registry.RegistryException;
import org.jafer.registry.model.Category;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.web.struts.ParameterKeys;

/**
 * This action removes the category to the service or provider
 */
public final class RemoveCategoryAction extends JaferRegistryAction
{

    /**
     * Execute the action
     * 
     * @param mapping The struts action mapping
     * @param form The action form sent in the request
     * @param request The request object
     * @param response The response object
     * @return The action to forward to
     * @throws IOException
     * @throws ServletException
     * @throws RegistryException
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws RegistryException, IOException, ServletException
    {
        // initialise the action
        initAction(mapping, form, request, response);

        String serviceId = (String) dynaForm.get(ParameterKeys.SERVICE_ID);
        String providerId = (String) dynaForm.get(ParameterKeys.PROVIDER_ID);
        String categoryType = (String) dynaForm.get(ParameterKeys.CATEGORY_TYPE);
        String categoryValue = (String) dynaForm.get(ParameterKeys.CATEGORY_VALUE);

        // are we removing from service or provider
        if (serviceId == null || serviceId.length() == 0)
        {
            // add to provider
            ServiceProvider provider = getServiceLocator().getServiceProvider(providerId);
            Category cat = getCategoryToDelete(provider.getCategories(), categoryType, categoryValue);
            // if found we can delete
            if (cat != null)
            {
                provider.removeCategory(cat);
                getServiceManager().updateServiceProvider(provider);
            }
        }
        else
        {
            // add to service
            Service service = getServiceLocator().getService(serviceId);
            Category cat = getCategoryToDelete(service.getCategories(), categoryType, categoryValue);
            // if found we can delete
            if (cat != null)
            {
                service.removeCategory(cat);
                getServiceManager().updateService(service);
            }
        }

        return getActionForward(SUCCESS);
    }

    /**
     * Gets an instance of a category for removing from the provider or service
     * 
     * @param categories The list of categories to search
     * @param type The type to match up
     * @param value The value to match up
     * @return The category instance
     */
    private Category getCategoryToDelete(List categories, String type, String value)
    {
        Iterator iter = categories.iterator();
        while (iter.hasNext())
        {
            Category cat = (Category) iter.next();
            // check for matching value and type
            if (cat.getValue().equals(value) && cat.getName().equals(type))
            {
                return cat;
            }
        }
        return null;
    }
}
