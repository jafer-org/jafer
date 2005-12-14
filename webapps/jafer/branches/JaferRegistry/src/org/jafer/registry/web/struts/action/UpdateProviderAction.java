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
import org.jafer.registry.model.Contact;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.web.struts.ParameterKeys;

/**
 * This action updates the provider
 */
public final class UpdateProviderAction extends JaferRegistryAction
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
        String contactName = dynaForm.getString(ParameterKeys.CONTACT_NAME);
        String contactDescription = dynaForm.getString(ParameterKeys.CONTACT_DESCRIPTION);
        String contactPhone = dynaForm.getString(ParameterKeys.CONTACT_PHONE);
        String contactEmail = dynaForm.getString(ParameterKeys.CONTACT_EMAIL);
        // we need to manage the complex rule that if any contact details are
        // entered then we must enter a contact name
        if ((contactName.length() == 0)
                && (contactDescription.length() > 0 || contactPhone.length() > 0 || contactEmail.length() > 0))
        {
            // add the error message that you must specify a contact name
            addErrorMessage("editprovider.contact.name.required");
            // return back to input page
            return mapping.getInputForward();
        }

        ServiceProvider provider = null;
        ServiceManager servMan = getServiceManager();

        try
        {
            // find service provider
            provider = getServiceLocator().getServiceProvider(dynaForm.getString(ParameterKeys.PROVIDER_ID));

            // now set all the other data
            provider.setName(dynaForm.getString(ParameterKeys.PROVIDER_NAME));
            provider.setDescription(dynaForm.getString(ParameterKeys.PROVIDER_DESCRIPTION));
            provider.setHomePage(dynaForm.getString(ParameterKeys.PROVIDER_HOMEPAGE));
            // If we do not have a contact name do not add contact
            if (contactName.length() > 0)
            {
                Contact contact = servMan.createNewContact(contactName, contactDescription, contactPhone, contactEmail);
                provider.setContact(contact);
            }
            else
            {
                // make sure we have a blank contact
                provider.removeContact();
            }

            //update the details to the registry
            servMan.updateServiceProvider(provider);

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

        addInfoMessage("editprovider.updatedok");

        return getActionForward(SUCCESS);
    }
}
