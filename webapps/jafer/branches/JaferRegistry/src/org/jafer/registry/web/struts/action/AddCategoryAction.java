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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jafer.registry.RegistryException;
import org.jafer.registry.model.Category;
import org.jafer.registry.model.CategoryType;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.uddi.RegistryExceptionImpl;
import org.jafer.registry.web.struts.CategoryTypesDisplayLink;
import org.jafer.registry.web.struts.ParameterKeys;

/**
 * This action adds the category to the service or provider
 */
public final class AddCategoryAction extends JaferRegistryAction
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
        
        // if the user has not selected a value display an error to them
        if (CategoryTypesDisplayLink.SELECT.equals(categoryType))
        {
            addErrorMessage("addcategory.type.notselected");
            return mapping.getInputForward();
        }
        
        CategoryType category = CategoryTypesDisplayLink.getCategoryType(categoryType);
        
        // make sure we got a match which we must do if system set up correctly
        if (category == null)
        {
            throw new RegistryExceptionImpl("Invalid system setup - Mismatch on category type");
        }     
        
        // create the category
        Category cat = getRegistryManager().getCategory(category,categoryValue);
        
        // are we adding to service or provider
        if (serviceId == null ||serviceId.length() == 0)
        {
           // add to provider 
           ServiceProvider provider = getServiceLocator().getServiceProvider(providerId);
           provider.addCategory(cat);
           getServiceManager().updateServiceProvider(provider);
        }
        else
        {
            // add to service
            Service service = getServiceLocator().getService(serviceId);
            service.addCategory(cat);
            getServiceManager().updateService(service);
        }
        
        return getActionForward(SUCCESS);
    }
}
