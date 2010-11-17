package org.jafer.portlets;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jafer.databeans.ZurlFactory;
import org.jafer.exception.JaferException;
import org.jafer.interfaces.Cache;
import org.jafer.interfaces.Connection;
import org.jafer.interfaces.Databean;
import org.jafer.interfaces.Present;
import org.jafer.interfaces.QueryBuilder;
import org.jafer.interfaces.RPNItem;
import org.jafer.interfaces.RecordedSearch;
import org.jafer.interfaces.Search;
import org.jafer.query.RPNOperand;
import org.jafer.query.RPNOperator;
import org.jafer.registry.RegistryException;
import org.jafer.registry.RegistryFactory;
import org.jafer.registry.RegistryManager;
import org.jafer.registry.ServiceLocator;
import org.jafer.registry.model.CategoryType;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProviderInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class XsltSearchPortlet extends AbstractXSLTPortlet {

    /**
     *  Constants for context-sensitive help
     */
    public static final int SEARCH_PAGE = 1;
    public static final int LIST_PAGE = 2;
    public static final int ITEM_PAGE = 3;
    public static final int HISTORY_PAGE = 4;
    
    private TreeMap targets = new TreeMap();
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

   private void addRegistryEntries() {
       try {
           String uddiInq = getInitParameter("uddiInquiryUrl");
           String uddiPub = getInitParameter("uddiPublishUrl");

           if (uddiInq == null || uddiInq.length() == 0 || uddiPub == null || uddiPub.length() == 0 ) {
               return;
           }
           RegistryManager manager = RegistryFactory.createRegistryManager(
                   uddiInq,
                   uddiPub);
           ServiceLocator locator = manager.getServiceLocator();

           Vector protocols = new Vector();
           protocols.add(Protocol.PROTOCOL_SRW);
           protocols.add(Protocol.PROTOCOL_Z3950);

           Vector categories = new Vector();
           String uddikey = getInitParameter("uddipartition");
           if (uddikey == null || uddikey.length() == 0) {
               categories = null;
           } else {
               categories.add(manager.getCategory(CategoryType.
                                                  CATEGORY_GENERAL_KEYWORDS,
                                                  "uddipartition"));
           }
           List results = locator.findServiceProvider(null, null, protocols, true);
           Iterator iterate = results.iterator();
           while (iterate.hasNext()) {
               ServiceProviderInfo provider = (ServiceProviderInfo)iterate.next();
               List services = locator.findService(provider, null, categories, protocols, true);
               Iterator iterate2 = services.iterator();
               while (iterate2.hasNext()) {
                   ServiceInfo info = (ServiceInfo) iterate2.next();
                   Service service = locator.getService(info);
                   String targetName = service.getName() + " - " + provider.getName();
                   String endpoint = null;
                   endpoint = service.getAccessUrl(Protocol.PROTOCOL_Z3950);
                   if (endpoint != null && endpoint.length() > 0) {
                       targets.put(targetName, new ZurlFactory(endpoint));
                   }
                   endpoint = service.getAccessUrl(Protocol.PROTOCOL_SRW);
                   if (endpoint != null && endpoint.length() > 0) {
                       targets.put(targetName, new ZurlFactory(endpoint));
                   }
               }
           }
       } catch (RegistryException ex) {
           ex.printStackTrace();
       }
   }

   private void addStaticEntries() {
       Enumeration enumerate = this.getInitParameterNames();

       while (enumerate.hasMoreElements()) {
           String key = (String) enumerate.nextElement();
           if (key.startsWith("target.")) {
               String targetName = key.substring(7);
               targets.put(targetName, new ZurlFactory(getInitParameter(key)));
           }
       }
   }

   public void init() throws PortletException {
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
        // clear any existing error messages
        clearError(request);
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
        // clear any existing error messages
        clearError(request);
    }

    protected String[][] getVTableAction() {
        // includes help action to allow navigation within the HELP
        // mode pages
        return new String[][] {
                new String[] {"search", "search", "list"},
                new String[] {"help", "navigateHelp", "help"},
        };
    }

    /**
     * Triggered by action=help Action URLs. Takes
     * page parameter. Allowed values: search, history,
     * list and item.
     * @param req
     */
    public void navigateHelp (ActionRequest req) {
        String page = req.getParameter("page");
        if (page != null) {
            if(page.equalsIgnoreCase("search"))
                setHelpContext(req, SEARCH_PAGE);
            if(page.equalsIgnoreCase("history"))
                setHelpContext(req, HISTORY_PAGE);
            if(page.equalsIgnoreCase("list"))
                setHelpContext(req, LIST_PAGE);
            if(page.equalsIgnoreCase("item"))
                setHelpContext(req, ITEM_PAGE);
        }
    }
    
    protected String[][] getVTableView() {
        // includes 'history' to support search history
        return new String[][] {
                new String[] {"start", "initSession", "search"},
                new String[] {"list", "setItemCloud", "list"},
                new String[] {"item", "setItem", "item"},
                new String[] {"history", "setHistory", "history"},
        };
    }

    protected String[][] getVTableHelp() {
        return new String[][] {
                new String[] {"help", "setHelp", "help"},
        };
    }

    protected String[][] getVTableEdit() {
        // not supported
        return null;
    }

    /**
     * Sets up the xml for rendering the HELP mode.
     * Uses the HelpContext, if set.
     *  
     * @param request
     * @return
     */
    public Document setHelp(RenderRequest request) {
        Document xml = domBuilder.newDocument();
        Element hNode = xml.createElement("help");

        int loc = getHelpContext(request);

        switch (loc) {
        case HISTORY_PAGE:
            hNode.appendChild(xml.createElement("history"));
            break;
        case SEARCH_PAGE:
            hNode.appendChild(xml.createElement("search"));
            break;
        case LIST_PAGE:
            hNode.appendChild(xml.createElement("list"));
            break;
        case ITEM_PAGE:
            hNode.appendChild(xml.createElement("item"));
            break;
        default: break;
        }
        xml.appendChild(hNode);
        return xml;
    }

    public void search(ActionRequest request)
        throws PortletException {
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
            String historyId = request.getParameter("history");

            clearSession(request);
            Node query_node = null;

            // Set up an RPN version of the original query.
            // This is used for re-editing a search or when
            // displaying the search used to the user
            RPNItem rpn[] = new RPNItem[3];

            if(historyId != null) {
                // got a request to re-run a stored search
                // retrieve the components
                RecordedSearch recordedSearch = retrieveSearch(request, (Integer.valueOf(historyId)).intValue());
                query_node = recordedSearch.getQuery();
                database = recordedSearch.getDatabases()[0];
                rpn = recordedSearch.getRPN();
            } else {
                // build the new query
                // stashing it in RPN as we go
                try {
                    QueryBuilder query = new org.jafer.query.QueryBuilder();
                    if (title.length() == 0) {
                        query_node = query.getNode("author", author);
                        rpn[0] = new RPNOperand("author",author);
                    } else if (author.length() == 0) {
                        query_node = query.getNode("title", title);
                        rpn[0] = new RPNOperand("title",title);
                    } else {
                        query_node = query.and(query.getNode("author", author),
                                query.getNode("title", title));
                        rpn[0] = new RPNOperand("author",author);
                        rpn[1] = new RPNOperand("title",title);
                        rpn[2] = new RPNOperator(RPNOperator.Op.AND);
                    }
                } catch (JaferException ex) {
                    ex.printStackTrace();
                    clearSession(request);
                    setError(request, "There was an error with your search.");
                }
            }
            Databean bean = getSession(request, database);
            // record the search in the history
            recordSearch(request, rpn, query_node, database);
            // run it
            try {
                ((Search) bean).submitQuery(query_node);
                if (((Search) bean).getNumberOfResults() > 0) {
                    ((Present) bean).setRecordCursor(1);
                }
            } catch (JaferException ex) {
                ex.printStackTrace();
                clearSession(request);
                setError(request, "There was an error with your search.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            setError(request, "There was an error with your search.");
        }
    }

    protected void setError(PortletRequest request, String msg) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            session.setAttribute("error", msg);
        }
    }
    
    protected String getError(PortletRequest request) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            return((String)session.getAttribute("error"));
        }
        return null;
    }

    protected void clearError(PortletRequest request) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            session.removeAttribute("error");
        }
    }
    
    // record a search in the session
    private void recordSearch(ActionRequest request, RPNItem[] rpn, Node query, String database) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            List<RecordedSearch> history = (List)session.getAttribute("history");
            if(history == null) {
                history = new CopyOnWriteArrayList<RecordedSearch>();
            }
            Date date = Calendar.getInstance().getTime();
            RecordedSearch rs = new org.jafer.query.RecordedSearch(query, rpn, new String[] { database }, date);
            history.add(rs);
            session.setAttribute("history", history, PortletSession.PORTLET_SCOPE);
        }
    }

    // get a previously stored search by its index
    protected RecordedSearch retrieveSearch(PortletRequest request, int id) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            List<RecordedSearch> history = (List)session.getAttribute("history");
            if(history != null) {
                return(history.get(id-1));
            }
        }
        return null;
    }
    
    // get the size of the session search history
    protected int getHistorySize(PortletRequest request) {
        PortletSession session = request.getPortletSession(false);
        if (session != null) {
            List<RecordedSearch> history = (List)session.getAttribute("history");
            if(history != null) {
                return(history.size());
            }
        }
        return -1;
    }

    /**
     * initSession
     * view method for action=start
     *
     * @param request RenderRequest
     */
    public Document initSession(RenderRequest request) throws PortletException {
        //clearSession(request);

        // record where we are for the contextual help
        setHelpContext(request, SEARCH_PAGE);

        Document xml = domBuilder.newDocument();
        Element srNode = xml.createElement("data");

        targets.clear();
        addStaticEntries();
        addRegistryEntries();

        Element dbsNode = xml.createElement("databases");
        Iterator enumerate = targets.keySet().iterator();
        while (enumerate.hasNext()) {
            Element ssrNode = xml.createElement("database");
            Text textNode = xml.createTextNode((String) enumerate.next());
            ssrNode.appendChild(textNode);
            dbsNode.appendChild(ssrNode);
        }
        srNode.appendChild(dbsNode);

        // if we've a history param we're trying to edit
        // a previous search
        String historyId = request.getParameter("history");
        if(historyId != null){
            PortletSession session = request.getPortletSession(false);
            List<RecordedSearch> history = null;
            if (session != null) {
                history = (List)session.getAttribute("history");
            }
            if(history != null) {
                try {
                    int rec = 0;
                    try {
                        rec = Integer.parseInt(historyId);
                    } catch (Exception e) {
                        ;
                    }
                    if(rec > 0) {
                        RecordedSearch rs = history.get(rec-1);
                        srNode.appendChild(rs.getNode(xml, rec));
                    }
                } catch (Exception ex1) {
                    ex1.printStackTrace();
                }
            }
        }

        xml.appendChild(srNode);
        return xml;
    }

    /**
     * setHistory
     * view method for action=history
     *
     * @param request RenderRequest
     */
    public Document setHistory(RenderRequest request) throws PortletException {
        // record where we are for the contextual help
        setHelpContext(request, HISTORY_PAGE);
        //Databean bean = getSession(request);
        String id = request.getParameter("id");
        
        PortletSession session = request.getPortletSession(false);
        List<RecordedSearch> history = null;
        if (session != null) {
            history = (List)session.getAttribute("history");
        }

        Document xml = domBuilder.newDocument();
        Element srNode = xml.createElement("searches");
        if(history != null) {
            try {
                int rec = 1;
                try {
                    rec = Integer.parseInt(id);
                } catch (Exception e) {
                    ;
                }
    
                int start = (rec / 10) * 10 + 1;
                int end = start + 9;
                if (end > history.size()) {
                    end = history.size();
                }
    
                srNode.setAttribute("start", Integer.toString(start));
                srNode.setAttribute("end", Integer.toString(end));
                srNode.setAttribute("total", Integer.toString(history.size()));
    
                for (int n = start; n <= end; n++) {
                    RecordedSearch rs = history.get(n-1);
                    srNode.appendChild(rs.getNode(xml, n));
                }
            } catch (Exception ex1) {
                ex1.printStackTrace();
                srNode.setAttribute("start", "0");
                srNode.setAttribute("end", "0");
                srNode.setAttribute("total", "0");
            }
        } else {
            srNode.setAttribute("total", "0");
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
        // record where we are for the contextual help
        setHelpContext(request, ITEM_PAGE);
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
                srNode.setAttribute("id", id);
                srNode.setAttribute("total", "0");
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
        // record where we are for the contextual help
        setHelpContext(request, LIST_PAGE);
        Document xml = domBuilder.newDocument();
        // check for search errors
        String error = getError(request);
        if(error != null) {
            Element eNode = xml.createElement("error");
            eNode.appendChild(xml.createTextNode(error));
            xml.appendChild(eNode);
            return xml;
        }
        Databean bean = getSession(request);
        String id = request.getParameter("id");
        Element rNode = xml.createElement("results");
        Element srNode = xml.createElement("records");
        int last = getHistorySize(request);
        RecordedSearch rs = retrieveSearch(request, last);
        if(rs != null)
            rNode.appendChild(rs.getNode(xml, last));

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
            srNode.setAttribute("start", "0");
              srNode.setAttribute("end", "0");
              srNode.setAttribute("total", "0");
        }
        rNode.appendChild(srNode);
        xml.appendChild(rNode);
        return xml;
    }
    
    protected void setHelpContext(PortletRequest request, int loc) {
        PortletSession session = request.getPortletSession();
        if(session != null) {
            session.setAttribute("help_context", new Integer(loc));
        }
    }

    protected int getHelpContext(PortletRequest request) {
        PortletSession session = request.getPortletSession();
        if(session != null) {
            Integer i = (Integer)session.getAttribute("help_context");
            try {
                return(i.intValue());
            } catch (Exception e) {
                ;
            }
        }
        return -1;
    }

}
