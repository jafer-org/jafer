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
import org.apache.struts.util.MessageResources;
import org.jafer.registry.RegistryException;
import org.jafer.registry.ServiceLocator;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.web.struts.ParameterKeys;
import org.jafer.registry.web.struts.bean.ServiceAccessUrl;

/**
 * This action views the specified service
 */
public final class ViewServiceAction extends JaferRegistryAction
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

        String id = (String) dynaForm.get(ParameterKeys.SERVICE_ID);
        
        ServiceLocator servLoc = getServiceLocator();
        Service service = servLoc.getService(id);
        
        if(service != null)
        {
            dynaForm.set(ParameterKeys.SERVICE_NAME,service.getName());
            dynaForm.set(ParameterKeys.SERVICE_DESCRIPTION,service.getDescription());
            dynaForm.set(ParameterKeys.SERVICE_CATEGORIES, service.getCategories());
            dynaForm.set(ParameterKeys.PROVIDER_ID, service.getServiceProviderId());
            // need to create a collection of access points
            ServiceAccessUrl point = null;
            String url = "";
            MessageResources messageResources = getResources(request);
            Vector accessPoints = new Vector();
            Iterator iter = Protocol.getAllProtocols().iterator();
            // process each protocol
            while (iter.hasNext())
            {
                Protocol protocol = (Protocol) iter.next();
                // first get access point for protocol
                url = service.getAccessUrl(protocol);
                // only add if not empty
                if (url.length() > 0)
                {
                    point = new ServiceAccessUrl(protocol.getName(),messageResources.getMessage("accessurl.accesspoint.type"),url,false);
                    accessPoints.add(point);
                }
                // now get WSDL for protocol
                url = service.getWSDLUrl(protocol);
                // only add if not empty
                if (url.length() > 0)
                {
                    point = new ServiceAccessUrl(protocol.getName(),messageResources.getMessage("accessurl.wsdl.type"),url,true);
                    accessPoints.add(point);
                }
            }
            dynaForm.set(ParameterKeys.SERVICE_ACCESS_POINTS, accessPoints);
        }
        else
        {
            addErrorMessage("errors.expected.provider.notfound");
            return getErrorPage();
        }
        
        return getActionForward(SUCCESS);
    }
}
