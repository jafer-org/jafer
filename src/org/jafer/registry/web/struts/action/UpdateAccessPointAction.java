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
import org.apache.struts.util.MessageResources;
import org.jafer.registry.RegistryException;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.web.struts.ParameterKeys;

/**
 * This action updates the access point for the service
 */
public final class UpdateAccessPointAction extends JaferRegistryAction
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

        MessageResources messageResources = getResources(request);
        String serviceId = (String) dynaForm.get(ParameterKeys.SERVICE_ID);
        String protocolType = (String) dynaForm.get(ParameterKeys.PROTOCOL_TYPE);
        String accessPointType = (String) dynaForm.get(ParameterKeys.ACCESS_POINT_TYPE);
        String accessPointUrl = (String) dynaForm.get(ParameterKeys.ACCESS_POINT_URL);

        Protocol protocol = null;
        // the protocol name is displayed on the page so map up aginst it
        if(Protocol.PROTOCOL_SRW.getName().equals(protocolType))
        {
            protocol = Protocol.PROTOCOL_SRW;
        }
        else
        {
            protocol = Protocol.PROTOCOL_Z3950;
        }

        // get the service
        Service service = getServiceLocator().getService(serviceId);

        // are we setting WSDL or Access point
        if (accessPointType.equals(messageResources.getMessage("accessurl.wsdl.type")))
        {
            service.setWSDLUrl(protocol,accessPointUrl);
        }
        else
        {
            service.setAccessUrl(protocol,accessPointUrl);
        }
        
        // update the service
        getServiceManager().updateService(service);
        
        return getActionForward(SUCCESS);
    }
}
