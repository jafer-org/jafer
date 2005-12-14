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
import org.jafer.registry.web.struts.ParameterKeys;
import org.jafer.registry.web.struts.SessionKeys;

/**
 * This action checks to see that the user is logged on as expected. If they are
 * they can proceed with the submit action otherwise they are to be redirected
 * to logon first.
 */
public final class CheckLogonStatusAction extends JaferRegistryAction
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
        // get the service manager
        ServiceManager manager = (ServiceManager) session.getAttribute(SessionKeys.SERVICE_MANAGER);
        // check to see if the service manager exists signifying not currently
        // logged on
        if (manager == null)
        {
            // set into the session the actual action being performed so logon
            // screen can continue to do it when a successful logon occurs
            session.setAttribute(SessionKeys.AFTER_LOGON_SUBMIT_ACTION, dynaForm.get(ParameterKeys.SUBMIT_ACTION));
            // set a message for the logon screen to inform user why they are
            // there
            addInfoMessage("logon.required");

            // set the submit action now to be go logon
            // this will send the user to the logon screen and
            // back to the caller to re-request the action
            dynaForm.set(ParameterKeys.SUBMIT_ACTION, "go_logon");
            
            // store the form in the session so that when we return we can reset
            // back to redisplay old screen ot to have all the keys we require to
            // repopulate it
            session.setAttribute(SessionKeys.SAVED_FORM,form);
        }
        // okay they are logged on so proceed to requested action
        return getActionForward();
    }
}
