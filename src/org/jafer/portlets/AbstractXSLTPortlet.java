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

    /**
     * Get the Action dispatch table for processing JSR-168 action requests<p>
     *
     * Each row should be of the form:<p>
     *
     * action parameter value, java method, next action parameter<p>
     *
     * where action parameter value is the Action parameter passed by the portlet (first row used if no Action parameter present)<br>
     *       java method is a java method in the class of the form  public void ...(ActionRequest request) to handle the action<br>
     *       next action parameter is the Action parameter to set for the subsequent renderRequest<br>
     *
     * @return String[][]
     */
    abstract protected String[][] getVTableAction();

    /**
     * Get the View dispatch table for processing JSR-168 View requests<p>
     *
     * Each row should be of the form:<p>
     *
     * action parameter value, java method, xslt path<p>
     *
     * where action parameter value is the Action parameter passed by the portlet (first row used if no Action parameter present)<br>
     *       java method is a java method in the class of the form public Document ...(RenderRequest request) throws PortletException which should return the required XML<br>
     *       xslt path is the relative path to an XSLT tranform to take the XML into a HTML fragment<br>
     *
     * @return String[][]
     */
    abstract protected String[][] getVTableView();

    /**
     * Get the Help dispatch table for processing JSR-168 Help requests<p>
     *
     * Each row should be of the form:<p>
     *
     * action parameter value, java method, xslt path<p>
     *
     * where action parameter value is the Action parameter passed by the portlet (first row used if no Action parameter present)<br>
     *       java method is a java method in the class of the form public Document ...(RenderRequest request) throws PortletException which should return the required XML<br>
     *       xslt path is the relative path to an XSLT tranform to take the XML into a HTML fragment<br>
     *
     * @return String[][]
     */
    abstract protected String[][] getVTableHelp();

    /**
     * Get the Edit dispatch table for processing JSR-168 Edit requests<p>
     *
     * Each row should be of the form:<p>
     *
     * action parameter value, java method, xslt path<p>
     *
     * where action parameter value is the Action parameter passed by the portlet (first row used if no Action parameter present)<br>
     *       java method is a java method in the class of the form public Document ...(RenderRequest request) throws PortletException which should return the required XML<br>
     *       xslt path is the relative path to an XSLT tranform to take the XML into a HTML fragment<br>
     *
     * @return String[][]
     */
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

    private String[][]vTableAction = this.getVTableAction();
    private String[][]vTableEdit = this.getVTableEdit();
    private String[][]vTableHelp = this.getVTableHelp();
    private String[][]vTableView = this.getVTableView();
    public void processAction(ActionRequest request, ActionResponse response) throws
            PortletException, IOException {

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
        render(request, response, vTableHelp);
    }

    protected void doEdit(RenderRequest request, RenderResponse response) throws
            PortletException, IOException {
        render(request, response, vTableEdit);
    }


    protected void doView(RenderRequest request, RenderResponse response) throws
            PortletException,
            IOException {
        render(request, response, vTableView);
    }
}
