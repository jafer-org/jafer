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
import org.jafer.registry.ServiceManager;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.web.struts.ParameterKeys;

/**
 * This action creates a new service
 */
public final class CreateServiceAction extends JaferRegistryAction
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
        
        ServiceProvider provider = null;
        Service service = null;
        ServiceManager servMan = getServiceManager();

        try
        {
            // get the new service provider
            provider = getServiceLocator().getServiceProvider(dynaForm.getString(ParameterKeys.PROVIDER_ID));

            String name = dynaForm.getString(ParameterKeys.SERVICE_NAME);
            service = servMan.registerService(provider,name);
            service.setDescription(dynaForm.getString(ParameterKeys.SERVICE_DESCRIPTION));
            //update the details to the registry
            servMan.updateService(service);

        }
        catch (RegistryException e)
        {
            // have we just exceed the limits
            if (e.getErrorCode().equals(RegistryException.E_accountLimitExceeded))
            {
               addErrorMessage("errors.account.limit.service.exceeded");
               return mapping.getInputForward();
            }
            // if we failed on the update call try and delete the service
            if (service != null)
            {
                servMan.deleteService(service);
            }
            throw e;
        }

        addInfoMessage("createservice.createdok");

        return getActionForward(SUCCESS);
    }
}
