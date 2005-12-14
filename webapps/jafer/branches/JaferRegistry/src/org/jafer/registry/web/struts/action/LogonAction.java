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
import org.jafer.registry.InvalidAuthorisationDetailsException;
import org.jafer.registry.RegistryException;
import org.jafer.registry.RegistryManager;
import org.jafer.registry.ServiceManager;
import org.jafer.registry.web.struts.ParameterKeys;
import org.jafer.registry.web.struts.SessionKeys;

/**
 * This class takes care of logging a user in
 */
public final class LogonAction extends JaferRegistryAction
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
        ActionForward forward = null;
        String username = (String) dynaForm.get(ParameterKeys.USERNAME);
        String credential = (String) dynaForm.get(ParameterKeys.CREDENTIAL);
        try
        {
            RegistryManager regMan = getRegistryManager();
            // construction will validate the username and credential
            ServiceManager servman = regMan.getServiceManager(username, credential);
            session.setAttribute(SessionKeys.SERVICE_MANAGER, servman);
            // credential valid so store in the session
            session.setAttribute(SessionKeys.USERNAME, username);
            session.setAttribute(SessionKeys.CREDENTIAL, credential);
            // get the after logon submit action. This is only set if the user
            // performs an action when not logged on and is used to complete the
            // action after the logon is successful
            String action = (String) session.getAttribute(SessionKeys.AFTER_LOGON_SUBMIT_ACTION);
            // Is action set
            if (action == null || action.length() == 0)
            {
                // action not set then the user justed clicked logon from menu
                // hence just go to return page
                forward = getActionForward(SUCCESS);
            }
            else
            {
                // action set so complete the intended action now logged on okay
                // remove the action from the session and get that actions
                // forward. The return page will already be set for that action
                // to use when it completes
                session.removeAttribute(SessionKeys.AFTER_LOGON_SUBMIT_ACTION);
                // set the last saved from back into the request from the session so
                // that the old page will be able to display or have all the keys it
                // requires to reobtain its data. Note because its a differnet object it
                // will reset to its default state and not the one in the former action
                request.setAttribute(ParameterKeys.REGISTRY_FORM, session.getAttribute(SessionKeys.SAVED_FORM));
                // now clean it out of the session
                session.removeAttribute(SessionKeys.SAVED_FORM);
                forward = getActionForward(action);
            }
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            addErrorMessage("logon.invalid");
            forward = mapping.getInputForward();
        }
        return forward;
        

    }
}
