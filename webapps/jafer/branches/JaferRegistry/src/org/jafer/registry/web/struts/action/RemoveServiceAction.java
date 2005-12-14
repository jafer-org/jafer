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
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jafer.registry.RegistryException;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.web.struts.ParameterKeys;

/**
 * This action removes the service from the provider. A service on a provider
 * must be removed by using the provider rather than deleting the service
 * directly because some services are not always owned by the provider they are
 * attached to. This scenario can not be created using this website but can if
 * using other tools. The service will always be deleted if the provider it is
 * removed from is the owner anyway.
 */
public final class RemoveServiceAction extends JaferRegistryAction
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

        // get the id to delete
        String serviceId = (String) dynaForm.get(ParameterKeys.SERVICE_ID);
        String providerId = (String) dynaForm.get(ParameterKeys.PROVIDER_ID);

        try
        {
            ServiceProvider provider = getServiceLocator().getServiceProvider(providerId);
           
            // holds the service infos identified for deletion
            Vector toDelete = new Vector();
            
            // iterate the services till removing all that match the service ID
            Iterator iter = provider.getServices().iterator();
            while (iter.hasNext())
            {
                ServiceInfo info = (ServiceInfo) iter.next();
                if (info.getId().equals(serviceId))
                {
                    // need to delete this service
                    toDelete.add(info);
                }
            }
            
            // iterate deleteing all the identified service infos
            iter = toDelete.iterator();
            while (iter.hasNext())
            {
                ServiceInfo info = (ServiceInfo) iter.next();
                provider.removeService(info);
            }
            
            // update the provider to confirm the deletes
            getServiceManager().updateServiceProvider(provider);
            
        }

        catch (RegistryException e)
        {
            // are we trying to alter an object we do not own
            if (e.getErrorCode().equals(RegistryException.E_userMismatch))
            {
                addErrorMessage("errors.user.mismatch");
                return mapping.getInputForward();
            }
            throw e;
        }

        addInfoMessage("viewproviderservices.removedok");

        return getActionForward(SUCCESS);
    }
}
