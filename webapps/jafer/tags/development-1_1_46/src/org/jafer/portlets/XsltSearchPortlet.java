package org.jafer.portlets;

import java.io.*;
import javax.portlet.*;

import org.jafer.exception.*;
import org.jafer.interfaces.*;
import java.util.Enumeration;
import org.jafer.databeans.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XsltSearchPortlet extends AbstractXSLTPortlet {
    private Hashtable targets = new Hashtable();
    static {
        try {
            domBuilder = DocumentBuilderFactory.newInstance().
                         newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    public XsltSearchPortlet() {
        super();
    }

    public void init() throws PortletException {
        Enumeration enumerate = this.getInitParameterNames();

        while (enumerate.hasMoreElements()) {
            String key = (String) enumerate.nextElement();
            if (key.startsWith("target.")) {
                String targetName = key.substring(7);
                targets.put(targetName, new ZurlFactory(getInitParameter(key)));
            }
        }
    }

    /**
     * createBean
     * Creates a JAFER Z3950/SRW Bean
     *
     * @return Databean
     */
    private Databean createBean(String database) {
        ZurlFactory factory = (ZurlFactory) targets.get(database);
        if (factory == null) {
            return null;
        }
        Databean bean = factory.getDatabean();
        if (bean == null) {
            return null;
        }

        ((Connection) bean).setTimeout(60000);
        ((Cache) bean).setFetchSize(15);
        ((Connection) bean).setAutoReconnect(3);
        ((Present) bean).setRecordSchema(getInitParameter("recordSchema"));
        return bean;
    }

    /**
     * storeSession
     * Stores session details as JAFER bean
     *
     * @param request ActionRequest
     * @param bean Databean
     */
    private void storeSession(ActionRequest request, Databean bean) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            session.setAttribute("databean", bean, PortletSession.PORTLET_SCOPE);
        }
    }

    /**
     * storeSession
     * Stores session details as JAFER bean
     *
     * @param request RenderRequest
     * @param bean Databean
     */
    private void storeSession(RenderRequest request, Databean bean) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            session.setAttribute("databean", bean, PortletSession.PORTLET_SCOPE);
        }
    }

    /**
     * getSession
     * get sessions details as JAFER bean
     *
     * @param request ActionRequest
     * @return Databean
     */
    private Databean getSession(ActionRequest request, String databases) {
        Databean bean = null;
        PortletSession session = request.getPortletSession(true);
        if (session != null) {
            bean = (Databean) session.getAttribute("databean",
                    PortletSession.PORTLET_SCOPE);
        }
        if (bean == null) {
            bean = createBean(databases);
            storeSession(request, bean);
        }
        return bean;
    }

    /**
     * getSession
     * get sessions details as JAFER bean
     *
     * @param request RenderRequest
     * @return Databean
     */
    private Databean getSession(RenderRequest request) {
        Databean bean = null;
        PortletSession session = request.getPortletSession(true);
        if (session != null) {
            bean = (Databean) session.getAttribute("databean",
                    PortletSession.PORTLET_SCOPE);
        }
        return bean;
    }

    /**
     * clearSession
     * clear session (and close JAFER bean connections)
     *
     * @param request ActionRequest
     */
    private void clearSession(ActionRequest request) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            Databean bean = (Databean) session.getAttribute("databean",
                    PortletSession.PORTLET_SCOPE);
            if (bean != null) {
                try {
                    ((Connection) bean).close();
                } catch (JaferException ex) {
                }
            }
            session.removeAttribute("databean", PortletSession.PORTLET_SCOPE);
        }
    }

    /**
     * clearSession
     * clear session (and close JAFER bean connections)
     *
     * @param request RenderRequest
     */
    private void clearSession(RenderRequest request) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            Databean bean = (Databean) session.getAttribute("databean",
                    PortletSession.PORTLET_SCOPE);
            if (bean != null) {
                try {
                    ((Connection) bean).close();
                } catch (JaferException ex) {
                }
            }
            session.removeAttribute("databean", PortletSession.PORTLET_SCOPE);
        }
    }

    protected String[][] getVTableAction() {
        return new String[][] {
                new String[] {"search", "search", "list"}
        };
    }

    protected String[][] getVTableView() {
        return new String[][] {
                new String[] {"start", "initSession", "search"},
                new String[] {"list", "setItemCloud", "list"},
                new String[] {"item", "setItem", "item"},
        };
    }

    protected String[][] getVTableHelp() {
        return null;
    }

    protected String[][] getVTableEdit() {
        return null;
    }


    public void search(ActionRequest request) {
        try {
            String database = request.getParameter("database");
            if (database == null)
                database = "";
            String author = request.getParameter("author");
            if (author == null)
                author = "";
            String title = request.getParameter("title");
            if (title == null)
                title = "";

            try {
                clearSession(request);
                Databean bean = getSession(request, database);
                QueryBuilder query = new org.jafer.query.QueryBuilder();
                if (title.length() == 0) {
                    ((Search) bean).submitQuery(
                            query.getNode("author", author));
                } else if (author.length() == 0) {
                    ((Search) bean).submitQuery(
                            query.getNode("title", title));
                } else {
                    ((Search) bean).submitQuery(
                            query.and(query.getNode("author", author),
                                      query.getNode("title", title)));
                }
                if (((Search) bean).getNumberOfResults() > 0) {
                    ((Present) bean).setRecordCursor(1);
                }
            } catch (JaferException ex) {
                ex.printStackTrace();
                clearSession(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * initSession
     * view method for action=start
     *
     * @param request RenderRequest
     */
    public Document initSession(RenderRequest request) throws PortletException {
        clearSession(request);

        Document xml = domBuilder.newDocument();
        Element srNode = xml.createElement("databases");

        Enumeration enumerate = targets.keys();

        while (enumerate.hasMoreElements()) {
            Element ssrNode = xml.createElement("database");
            Text textNode = xml.createTextNode((String) enumerate.nextElement());
            ssrNode.appendChild(textNode);
            srNode.appendChild(ssrNode);
        }

        xml.appendChild(srNode);
        return xml;
    }

    /**
     * setItem
     * view method for action=item
     *
     * @param request RenderRequest
     */
    public Document setItem(RenderRequest request) throws PortletException {
        Databean bean = getSession(request);
        Document xml = domBuilder.newDocument();
        Element srNode = xml.createElement("record");
        try {
            srNode.setAttribute("database", ((Present) bean).getCurrentDatabase());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String id = request.getParameter("id");
        if (id != null) {
            try {
                int rec = Integer.parseInt(id);
                ((Present) bean).setRecordCursor(rec);
                srNode.setAttribute("id", id);
                srNode.setAttribute("total",
                                    Integer.toString(((Search) bean).getNumberOfResults()));
                srNode.appendChild(xml.importNode(((Present) bean).
                                                  getCurrentRecord().getXML(), true));
            } catch (Exception ex1) {
                ex1.printStackTrace();
                throw new PortletException(ex1.toString());
            }
        }
        xml.appendChild(srNode);
        return xml;
    }

    /**
     * setItemCloud
     * view method for action=list
     *
     * @param request RenderRequest
     */
    public Document setItemCloud(RenderRequest request) throws PortletException {
        Databean bean = getSession(request);
        String id = request.getParameter("id");
        Document xml = domBuilder.newDocument();
        Element srNode = xml.createElement("records");
        try {
            int rec = 1;
            try {
                rec = Integer.parseInt(id);
            } catch (Exception e) {
                ;
            }

            int start = (rec / 10) * 10 + 1;
            int end = start + 9;
            if (end > ((Search) bean).getNumberOfResults()) {
                end = ((Search) bean).getNumberOfResults();
            }

            System.out.println(((Search) bean).getNumberOfResults());

            srNode.setAttribute("start", Integer.toString(start));
            srNode.setAttribute("end", Integer.toString(end));
            srNode.setAttribute("total",
                                Integer.toString(((Search) bean).getNumberOfResults()));

            for (int n = start; n <= end; n++) {
                ((Present) bean).setRecordCursor(n);
                Element srrNode = xml.createElement("record");
                srrNode.setAttribute("database",
                                     ((Present) bean).getCurrentDatabase());
                srrNode.setAttribute("id", Integer.toString(n));
                srrNode.appendChild(xml.importNode(((Present) bean).
                        getCurrentRecord().getXML(), true));
                srNode.appendChild(srrNode);
            }
        } catch (Exception ex1) {
            ex1.printStackTrace();
            throw new PortletException(ex1.toString());
        }
        xml.appendChild(srNode);
        return xml;
    }
}
