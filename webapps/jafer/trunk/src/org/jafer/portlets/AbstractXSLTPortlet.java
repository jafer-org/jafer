package org.jafer.portlets;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import javax.portlet.PortletPreferences;
import java.util.Map;
import java.util.HashMap;
import org.jafer.portlets.xslt.XSLTPortletRequestDispatcher;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;

abstract public class AbstractXSLTPortlet extends GenericPortlet {
    protected static DocumentBuilder domBuilder;

    public AbstractXSLTPortlet() {
        super();
    }

    abstract protected String[][] getVTableAction();

    abstract protected String[][] getVTableView();

    abstract protected String[][] getVTableHelp();

    abstract protected String[][] getVTableEdit();

    private String dispatchVTable(PortletRequest request,
                                  String[][] vTable, String action,
                                  Document[] xml) throws
            PortletException {
        String page = vTable[0][2];
        try {
            for (int n = 0; n < vTable.length; n++) {
                if (action.toLowerCase().startsWith(vTable[n][0])) {
                    String method = vTable[n][1];
                    page = vTable[n][2];
                    Class cl = null;
                    if (request instanceof ActionRequest) {
                        cl = ActionRequest.class;
                    }
                    if (request instanceof RenderRequest) {
                        cl = RenderRequest.class;
                    }
                    if (cl == null) {
                        throw new PortletException(
                                "Unknown PortletRequest type");
                    }
                    Method func = this.getClass().getMethod(method,
                            new Class[] {cl});
                    if (xml == null || xml.length == 0) {
                        func.invoke(this, new Object[] {request});
                    } else {
                        xml[0] = ((Document) func.invoke(this,
                                new Object[] {request}));
                    }
                }
            }
        } catch (Exception ex) {
            throw new PortletException(ex);
        }
        return page;
    }

    private void render(RenderRequest request, RenderResponse response,
                        String[][] vTable) throws PortletException {
        String action = request.getParameter("action");
        if (action == null) {
            action = vTable[0][0];
        }
        Document[] xml = new Document[1];
        String page = dispatchVTable(request, vTable, action, xml);

        if (xml[0] == null) {
            page = dispatchVTable(request, vTable, vTable[0][0], xml);
        }

        String xslUrl = getPortletConfig().getInitParameter(page);
        Map urlParams = new HashMap();
        urlParams.put("mode", PortletMode.VIEW.toString());
        request.setAttribute("org.jafer.portlets.xslt.urlParams", urlParams);
        Map xslParams = new HashMap();
        xslParams.put("mediaPath",
                      request.getContextPath() + "/" +
                      getPortletConfig().getInitParameter("images"));
        request.setAttribute("org.jafer.portlets.xslt.xslParams", xslParams);
        request.setAttribute("org.jafer.portlets.xslt.xmlDocument", xml[0]);

        try {
            XSLTPortletRequestDispatcher dispatcher = new
                    XSLTPortletRequestDispatcher(this.getPortletContext(),
                                                 xslUrl);
            response.setContentType("text/html");
            dispatcher.include(request, response);
            response.flushBuffer();
        } catch (Exception ex1) {
            ex1.printStackTrace();
            throw new PortletException(ex1.toString());
        }
    }

    public void processAction(ActionRequest request, ActionResponse response) throws
            PortletException, IOException {
        String[][] vTableAction = getVTableAction();

        String action = request.getParameter("action");
        if (action == null) {
            action = vTableAction[0][0];
        }
        String page = dispatchVTable(request, vTableAction, action, null);
        response.setRenderParameter("action", page);
    }

    protected void doHelp(RenderRequest request, RenderResponse response) throws
            PortletException,
            IOException {
        render(request, response, getVTableHelp());
    }

    protected void doEdit(RenderRequest request, RenderResponse response) throws
            PortletException, IOException {
        render(request, response, getVTableEdit());
    }


    protected void doView(RenderRequest request, RenderResponse response) throws
            PortletException,
            IOException {
        render(request, response, getVTableView());
    }
}
