package org.jafer.portlets;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;

import org.jafer.interfaces.RecordedSearch;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Extends {@link XsltSearchPortlet} to provide a portlet
 * that supports a single search (as set in EDIT mode) and
 * displays the results of that search in VIEW mode.
 * 
 * @author <a href="mailto:jasper.tredgold@bristol.ac.uk">Jasper Tredgold</a>
 * @version $Id$
 *
 */

public class XsltSingleSearchPortlet extends XsltSearchPortlet {

    public XsltSingleSearchPortlet() {
        super();
    }

    // The VIEW table is limited to results list and record item
    @Override
    protected String[][] getVTableView() {
        return new String[][] {
                new String[] {"list", "setItemCloud", "list"},
                new String[] {"item", "setItem", "item"},
        };
    }

    // The EDIT table contains the search construction page
    @Override
    protected String[][] getVTableEdit() {
        return new String[][] {
                new String[] {"start", "initSession", "search"},
        };
    }

    // We override so that we use the history to pull out the
    // last search run. This will be the one to edit.
    @Override
    public Document initSession(RenderRequest request)
        throws PortletException {
        Document xml = super.initSession(request);
        Node srNode = xml.getDocumentElement();
        int last = getHistorySize(request);
        RecordedSearch rs = retrieveSearch(request, last);
        if(rs != null)
            srNode.appendChild(rs.getNode(xml, last));
        return xml;
    }
    
    public void search(ActionRequest request, ActionResponse response) throws PortletException {
        super.search(request);
        // this portlet uses EDIT mode to edit the search. Once run
        // we switch to VIEW mode.
        response.setPortletMode(PortletMode.VIEW);
    }

}
