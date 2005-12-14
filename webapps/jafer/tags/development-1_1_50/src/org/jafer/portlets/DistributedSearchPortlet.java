package org.jafer.portlets;

import java.io.*;
import java.lang.reflect.*;
import javax.portlet.*;

import org.jafer.exception.*;
import org.jafer.interfaces.*;
import java.util.Enumeration;
import org.jafer.databeans.*;
import java.util.Hashtable;

class TargetConfig {

}

public class DistributedSearchPortlet
    extends GenericPortlet {


  private Databean createBean() {
    DatabeanManager bean = new DatabeanManager();
    Hashtable databeans = new Hashtable();

    Enumeration targets = this.getInitParameterNames();
    while (targets.hasMoreElements()) {
      String target = (String)targets.nextElement();
      String zurl = this.getInitParameter(target);
      ZurlFactory factory = new ZurlFactory(zurl);
      factory.setRecordSchema("http://www.loc.gov/mods/");
      databeans.put(target, factory);
    }

    bean.setDatabeanFactories(databeans);
    bean.setMode( "parallel" );
    bean.setAllDatabases((String[])databeans.keySet().toArray(new String[]{}));
    bean.setDatabases(bean.getAllDatabases());
    bean.setRecordSchema(  "http://www.loc.gov/mods/"  );
    return bean;
  }

  /**
   * storeSession
   * Stores session details as JAFER bean
   *
   * @param request ActionRequest
   * @param bean AbstractClient
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
   * @param bean AbstractClient
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
   * @return AbstractClient
   */
  private Databean getSession(ActionRequest request) {
    Databean bean = null;
    PortletSession session = request.getPortletSession(false);
    if (session != null) {
      bean = (Databean)session.getAttribute("databean", PortletSession.PORTLET_SCOPE);
      if (bean == null) {
        bean = createBean();
        storeSession(request, bean);
      }
    }
    return bean;
  }

  /**
   * getSession
   * get sessions details as JAFER bean
   *
   * @param request RenderRequest
   * @return AbstractClient
   */
  private Databean getSession(RenderRequest request) {
    Databean bean = null;
    PortletSession session = request.getPortletSession(false);
    if (session != null) {
      bean = (Databean) session.getAttribute("databean",
                                            PortletSession.PORTLET_SCOPE);
      if (bean == null) {
        bean = createBean();
        storeSession(request, bean);
      }
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
      Databean bean = (Databean)session.getAttribute("databean", PortletSession.PORTLET_SCOPE);
      if (bean != null) {
        try {
          ((Connection)bean).close();
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
      session.removeAttribute("databean", PortletSession.PORTLET_SCOPE);
    }
  }

  /**
   * jspDispatch
   * utility method for dispatching rendering to jsp
   *
   * @param request RenderRequest
   * @param response RenderResponse
   * @param page String
   * @throws IOException
   * @throws PortletException
   */
  private void jspDispatch(RenderRequest request, RenderResponse response,
                           String page) throws IOException, PortletException {
    PortletContext context = getPortletContext();
    PortletRequestDispatcher rd = context.getRequestDispatcher("/jsp/" +  page + ".jsp");
    rd.include(request, response);
  }

  /**
   * vTableAction
   * vTable for doAction dispatching
   * action parameter, method to call, jsp to render
   */
  private static final String[][] vTableAction = new String[][]{
      new String[]{"search", "search", "list"}
  };

  /**
   * processAction
   * JSR168 API - handle forms submit
   *
   * @param request ActionRequest
   * @param response ActionResponse
   * @throws PortletException
   * @throws IOException
   */
  public void processAction(ActionRequest request, ActionResponse response) throws
      PortletException, java.io.IOException {
    String action = request.getParameter("action");
    String page = "search";
    try {
      for (int n = 0; n < vTableAction.length; n++) {
        if (vTableAction[n][0].equalsIgnoreCase(action)) {
          String method = vTableAction[n][1];
          page = vTableAction[n][2];
          Method func = this.getClass().getMethod(method,
                                                  new Class[] {javax.portlet.ActionRequest.class});
          func.invoke(this, new Object[]{request});
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    response.setRenderParameter("action", page);
  }

  /**
   * search
   * vTable call for doAction
   * handles action=search
   *
   * @param request ActionRequest
   */
  public void search(ActionRequest request) {
    try {
      String author = request.getParameter("author");
      if (author == null)
        author = "";
      String title = request.getParameter("title");
      if (title == null)
        title = "";

      try {
        clearSession(request);
        Databean bean = getSession(request);
        QueryBuilder query = new org.jafer.query.QueryBuilder();
        if (title.length() == 0) {
          ((Search)bean).submitQuery(
              query.getNode("author", author));
        }
        else if (author.length() == 0) {
          ((Search)bean).submitQuery(
              query.getNode("title", title));
        }
        else {
          ((Search)bean).submitQuery(
              query.and(query.getNode("author", author),
                        query.getNode("title", title)));
        }
        if (((Search)bean).getNumberOfResults() > 0) {
          ((Present)bean).setRecordCursor(1);
        }
      }
      catch (JaferException ex) {
        ex.printStackTrace();
        clearSession(request);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * vTableView
   * vTable dispatch for doView
   * action parameter, method to call, jsp to render
   */
  private static final String[][] vTableView = new String[][]{
      new String[] {"start", "initSession", "search"},
      new String[] {"list", "setItemCloud", "list"},
      new String[] {"item", "setItem", "item"},
  };

  /**
   * doView
   * JSR168 for viewing pages
   *
   * @param request RenderRequest
   * @param response RenderResponse
   * @throws PortletException
   * @throws IOException
   */
  protected void doView(RenderRequest request, RenderResponse response) throws PortletException,
      IOException {
    String action = request.getParameter("action");
    String page = "search";
    try {
      for (int n = 0; n < vTableView.length; n++) {
        if (vTableView[n][0].equalsIgnoreCase(action)) {
          String method = vTableView[n][1];
          page = vTableView[n][2];
          Method func = this.getClass().getMethod(method,
                                                  new Class[] {javax.portlet.RenderRequest.class});
          func.invoke(this, new Object[]{request});
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    jspDispatch(request, response, page);
  }

  /**
   * initSession
   * view method for action=start
   *
   * @param request RenderRequest
   */
  public void initSession(RenderRequest request) {
    clearSession(request);
  }

  /**
   * setItem
   * view method for action=item
   *
   * @param request RenderRequest
   */
  public void setItem(RenderRequest request) {
    Databean bean = getSession(request);
    String id = request.getParameter("id");
    if (id != null) {
      try {
        int rec = Integer.parseInt(id);
        ((Present)bean).setRecordCursor(rec);
      }
      catch (Exception ex1) {
      }
    }
  }

  /**
   * setItemCloud
   * view method for action=list
   *
   * @param request RenderRequest
   */
  public void setItemCloud(RenderRequest request) {
    Databean bean = getSession(request);
    String id = request.getParameter("id");
    if (id != null) {
      try {
        int rec = Integer.parseInt(id);
        ((Present)bean).setRecordCursor((rec/10) * 10 + 1);
      }
      catch (Exception ex1) {
      }
    }
  }

  /**
   * doHelp
   * JSR168 API for help
   *
   * @param request RenderRequest
   * @param response RenderResponse
   * @throws PortletException
   * @throws IOException
   */
  protected void doHelp(RenderRequest request, RenderResponse response) throws PortletException,
      IOException {
    jspDispatch(request, response, "help");
  }


  public static void main(String [] args) {
    DatabeanFactory test = null;
    test = new ZurlFactory("z3950s://olis.ox.ac.uk:210/ADVANCE");
    Databean bean = test.getDatabean();

  }
}
