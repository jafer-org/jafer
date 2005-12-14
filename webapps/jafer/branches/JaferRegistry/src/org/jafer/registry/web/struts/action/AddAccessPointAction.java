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
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.uddi.RegistryExceptionImpl;
import org.jafer.registry.web.struts.ParameterKeys;
import org.jafer.registry.web.struts.ProtocolTypesDisplayLink;

/**
 * This action adds the access point to the service
 */
public final class AddAccessPointAction extends JaferRegistryAction
{

    /**
     * Stores a reference to the WSDL radio value
     */
    public final static String WSDL = "WSDL";

    /**
     * Stores a reference to the access point radio value
     */
    public final static String ACCESS = "Access";

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
        String protocolType = (String) dynaForm.get(ParameterKeys.PROTOCOL_TYPE);
        String accessPointType = (String) dynaForm.get(ParameterKeys.ACCESS_POINT_TYPE);
        String accessPointUrl = (String) dynaForm.get(ParameterKeys.ACCESS_POINT_URL);

        // if the user has not selected a value display an error to them
        if (ProtocolTypesDisplayLink.SELECT.equals(protocolType))
        {
            addErrorMessage("addaccesspoint.protocol.notselected");
            return mapping.getInputForward();
        }

        Protocol protocol = ProtocolTypesDisplayLink.getProtocolType(protocolType);

        // make sure we got a match which we must do if system set up correctly
        if (protocol == null)
        {
            throw new RegistryExceptionImpl("Invalid system setup - Mismatch on protocol type");
        }

        // get the service
        Service service = getServiceLocator().getService(serviceId);

        // are we setting WSDL or Access point
        if (accessPointType.equals(WSDL))
        {
            // check to see if already added
            if (service.getWSDLUrl(protocol).length() > 0)
            {
                addErrorMessage("addaccesspoint.wsdl.alreadyadded");
                return mapping.getInputForward();
            }
            service.setWSDLUrl(protocol,accessPointUrl);
        }
        else
        {
            // check to see if already added
            if (service.getAccessUrl(protocol).length() > 0)
            {
                addErrorMessage("addaccesspoint.access.alreadyadded");
                return mapping.getInputForward();
            }
            service.setAccessUrl(protocol,accessPointUrl);
        }

        // update the chages to the registry
        getServiceManager().updateService(service);
        
        return getActionForward(SUCCESS);
    }
}
