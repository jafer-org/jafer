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
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jafer.registry.RegistryException;
import org.jafer.registry.ServiceLocator;
import org.jafer.registry.model.CategoryType;
import org.jafer.registry.uddi.RegistryExceptionImpl;
import org.jafer.registry.web.struts.CategoryTypesDisplayLink;
import org.jafer.registry.web.struts.ParameterKeys;
import org.jafer.registry.web.struts.ProtocolTypesDisplayLink;

/**
 * This action searchs for the provider using the search criteria
 */
public final class SearchProviderAction extends JaferRegistryAction
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

        String name = (String) dynaForm.get(ParameterKeys.PROVIDER_NAME);
        String categoryType = (String) dynaForm.get(ParameterKeys.CATEGORY_TYPE);
        String categoryValue = (String) dynaForm.get(ParameterKeys.CATEGORY_VALUE);
        Integer maxRes = (Integer) dynaForm.get(ParameterKeys.MAX_RESULTS);
        String protocolType = (String) dynaForm.get(ParameterKeys.PROTOCOL_TYPE);
        
        ServiceLocator servLoc = getServiceLocator();

        // only set it if greater than 0
        if (maxRes != null && maxRes.intValue() >= 0)
        {
            servLoc.setMaxReturned(maxRes.intValue());
        }

        // check for valid category value and type
        if ((categoryValue != null && categoryValue.length() > 0) && categoryType.equals(CategoryTypesDisplayLink.SELECT))
        {
            addErrorMessage("search.type.notset");
            return mapping.getInputForward();
        }
        // check for valid category value and type
        else if ((categoryValue != null && categoryValue.length() == 0) && !categoryType.equals(CategoryTypesDisplayLink.SELECT))
        {
            addErrorMessage("search.value.notset");
            return mapping.getInputForward();
        }
        else if (name != null && name.length() == 0 && categoryValue != null && categoryValue.length() == 0
                && categoryType.equals(CategoryTypesDisplayLink.SELECT) && protocolType.equals(ProtocolTypesDisplayLink.SELECT))
        {
            addErrorMessage("search.criteria.notset");
            return mapping.getInputForward();
        }
        
        Vector categories = new Vector();
        // if category type was specified then create a category
        if(!categoryType.equals(CategoryTypesDisplayLink.SELECT))
        {
            CategoryType category = CategoryTypesDisplayLink.getCategoryType(categoryType);
            
            // make sure we got a match which we must do if system set up correctly
            if (category == null)
            {
                throw new RegistryExceptionImpl("Invalid system setup - Mismatch on category type");
            }  

            // create the category adding to the search list
            categories.add(getRegistryManager().getCategory(category,categoryValue));
        }
        
        // add the protocol
        Vector protocols = new Vector();
        if (!protocolType.equals(ProtocolTypesDisplayLink.SELECT))
        {
            protocols.add(ProtocolTypesDisplayLink.getProtocolType(protocolType));
        }
        
        List providers = servLoc.findServiceProvider(name,categories,protocols,true);

        if (providers.isEmpty())
        {
            dynaForm.set(ParameterKeys.FOUND_PROVIDERS, null);
        }
        else
        {
            dynaForm.set(ParameterKeys.FOUND_PROVIDERS, providers);
        }
        return getActionForward(SUCCESS);
    }
}
