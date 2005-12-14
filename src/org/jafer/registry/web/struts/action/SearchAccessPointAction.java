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
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jafer.databeans.ZurlFactory;
import org.jafer.exception.JaferException;
import org.jafer.interfaces.Cache;
import org.jafer.interfaces.Connection;
import org.jafer.interfaces.Databean;
import org.jafer.interfaces.Present;
import org.jafer.interfaces.Search;
import org.jafer.query.QueryBuilder;
import org.jafer.record.Field;
import org.jafer.registry.RegistryException;
import org.jafer.registry.uddi.RegistryExceptionImpl;
import org.jafer.registry.web.struts.ParameterKeys;
import org.jafer.registry.web.struts.bean.ModsRecord;
import org.w3c.dom.Node;
import org.jafer.transport.ConnectionException;

/**
 * This action searches the access point for results given the keyword
 */
public final class SearchAccessPointAction extends JaferRegistryAction
{

    /**
     * Stores a reference to the mods schema
     */
    private final static String MODS_SCHEMA = "http://www.loc.gov/mods/";

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

        String accessPointUrl = (String) dynaForm.get(ParameterKeys.ACCESS_POINT_URL);
        String searchAuthor = (String) dynaForm.get(ParameterKeys.AUTHOR);
        String searchTitle = (String) dynaForm.get(ParameterKeys.TITLE);
        Integer maxRes = (Integer) dynaForm.get(ParameterKeys.MAX_RESULTS);

        if (searchTitle.length() == 0 && searchAuthor.length() == 0)
        {
            addErrorMessage("searchaccesspoint.missing.criteria");
            return mapping.getInputForward();
        }

        Vector records = new Vector();
        int results = 0;
        Databean client = null;
        try
        {
            try
            {
                // create the appropriate client from the URL specified
                ZurlFactory factory = new ZurlFactory(accessPointUrl);
                client = factory.getDatabean();

                if (client == null)
                {
                    // add message to indicate invalid URL
                    addErrorMessage("searchaccesspoint.error.invalidaddress");
                    return mapping.getInputForward();
                }

                // create the query to search on title and author
                QueryBuilder builder = new org.jafer.query.QueryBuilder();
                Node query = null;

                // create the correct query
                if (searchTitle.length() > 0 && searchAuthor.length() > 0)
                {
                    Node author = builder.getNode("author", searchAuthor);
                    Node title = builder.getNode("title", searchTitle);
                    query = builder.or(author, title);
                }
                else if (searchTitle.length() > 0 )
                {
                    query = builder.getNode("title", searchTitle);
                }
                else
                {
                    query = builder.getNode("author", searchAuthor);
                }

                // set the maximum number of results
                ((Cache) client).setFetchSize(maxRes.intValue());
                ((Present) client).setRecordSchema(MODS_SCHEMA);
                ((Present) client).setCheckRecordFormat(true);

                // submit the query
                results = ((Search) client).submitQuery(query);
            }
            catch (JaferException e)
            {
                if (ConnectionException.class.isInstance(e.getCause()))
                {
                    // add message to indicate invalid URL as connection failed
                    addErrorMessage("searchaccesspoint.error.invalidaddress");
                    return mapping.getInputForward();
                }

                throw new RegistryExceptionImpl("Error communicating with Jafer ", e);
            }
            try
            {
                for (int i = 1; i <= results && i <= maxRes.intValue(); i++)
                {
                    // get the record to process
                    ((Present) client).setRecordCursor(i);
                    Field field = ((Present) client).getCurrentRecord();
                    //XMLSerializer.out(field.getXML(), "xml", System.out);

                    if (field.getXML().getNamespaceURI().contentEquals(MODS_SCHEMA))
                    {
                        ModsRecord record = new ModsRecord(field);
                        records.add(record);
                    }
                        else
                    {
                        // the format can not be correct for converting
                        addErrorMessage("searchaccesspoint.error.cannotconvert");
                        return mapping.getInputForward();
                    }
                }
                dynaForm.set(ParameterKeys.ACCESS_POINT_SEARCH_RESULTS, records);
            }
            catch (JaferException e)
            {
                if (ConnectionException.class.isInstance(e.getCause()))
                {
                    // add message to indicate invalid URL as connection failed
                    addErrorMessage("searchaccesspoint.error.invalidaddress");
                    return mapping.getInputForward();
                }

                e.printStackTrace();
                throw new RegistryExceptionImpl("Error processing returned records with Jafer ", e);
            }
            catch (Exception e)
            {
                // the format can not be correct for converting
                addErrorMessage("searchaccesspoint.error.cannotconvert");
                return mapping.getInputForward();
            }
        }
        finally
        {
            //make sure we close the bean if it is open
            if (client != null)
            {
                try
                {
                    ((Connection) client).close();
                }
                catch (JaferException e)
                {
                    throw new RegistryExceptionImpl("Error processing returned records with Jafer ", e);
                }
            }
        }
        return getActionForward(SUCCESS);
    }
}
