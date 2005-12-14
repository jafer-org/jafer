<jsp:useBean id="bean" scope="session" class="org.jafer.zclient.ZClient"/>
<%@ page import="org.jafer.zclient.*" %>
<%@ page import="org.jafer.exception.JaferException" %>
<%@ page import="org.jafer.zclient.operations.*" %>
<%@ page import="org.jafer.query.*" %>
<%@ page import="org.jafer.util.xml.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page errorPage="error.jsp" %>

  <%
    java.net.URL styleSheet;
    String method, msg = "";
    boolean list, item;
    int total, start, max, first, last;

    if (new Boolean(request.getParameter("query")).booleanValue()) {

      //  NB check for null params?
      if (request.getParameter("ipAddress").equals(""))
          throw new org.jafer.exception.JaferException("Bad Target IP address");
      if (request.getParameter("dbName").equals(""))
          throw new org.jafer.exception.JaferException("Bad dbName");
      try {
        Integer.parseInt(request.getParameter("portNumber"));
      } catch (NumberFormatException e) {
          throw new org.jafer.exception.JaferException("Bad portNumber");
      }

      bean.setRemoteAddress(request.getRemoteAddr());
      bean.setHost(request.getParameter("ipAddress"));
      bean.setDatabases(request.getParameter("dbName"));
      bean.setPort(Integer.parseInt(request.getParameter("portNumber")));
      bean.setFetchSize(3);
      bean.setAutoReconnect(3);
      bean.setRecordSchema("http://www.loc.gov/mods/");

      QueryBuilder qb = new QueryBuilder();
      try {
        String queryExp = request.getParameter("queryExp");
        bean.submitQuery(((org.jafer.query.QueryBuilder)qb).getNode(queryExp));
      } catch (Exception e) {
        throw new JaferException(
                  "You may not have entered a valid query! Please check your query and try again. " +
                  "<p>A list of valid search attributes can be found via the search page or by clicking " +
                  "<a href=\"searchHelp.jsp\">here</a></p>" +
                  "<p>Enter search attributes and terms seperated by '='</p>" +
                  "<p>eg author=jones and title=fish</p>" +
                  "<p>Valid boolean operators are: and, or, andNot, orNot<br> " +
                  "('not' is equivalent to 'andNot' - they are not case sensitive)</p>"
                  );
      }

      session.setAttribute("total", String.valueOf(bean.getNumberOfResults()));
      try {
        max = Integer.parseInt(request.getParameter("max"));
      } catch (NumberFormatException e) {max = 10;}
      session.setAttribute("max", String.valueOf(max));
    }

    total = Integer.parseInt((String)session.getAttribute("total"));
    max = Integer.parseInt((String)session.getAttribute("max"));
    try {
      start = Integer.parseInt(request.getParameter("start"));
    } catch (NumberFormatException e) {start = 1;}

    method = request.getParameter("method");
    if (method == null) method = "html";

    list = new Boolean(request.getParameter("list")).booleanValue();
    item = new Boolean(request.getParameter("item")).booleanValue();

    if (item) {
      try {
        first = Integer.parseInt(request.getParameter("id"));
      } catch (NumberFormatException e) {first = start;}
      // check resource is available?
      styleSheet = this.getClass().getClassLoader().getResource("org/jafer/xsl/jspViewItem.xsl");
      if (first > total || first < 1)
          first = total;
      last = first;
      start = (((int)(first - 1)/max) * max) + 1;
    } else {
      styleSheet = this.getClass().getClassLoader().getResource("org/jafer/xsl/jspViewList.xsl");
      first = start;
      last = start + max - 1;
      if (last > total || last < 1)
          last = total;
    }

    Node root = bean.getDocument().createElement("root");
    for (int n = first; n <= last; n++) {
      try {
        bean.setRecordCursor(n);
        root.appendChild(bean.getCurrentRecord().getRoot());
      } catch (org.jafer.record.RecordException e) {
        msg = "Some records contained errors, and are not displayed";
        System.err.println("Skipping record " + n + ": "+e.toString());
      } catch (org.jafer.exception.JaferException e) {
        msg = "Sorry, an error has ocurred whilst searching";
        System.err.println("Skipping record " + n + ": "+e.toString());
      }
    }

    java.util.Hashtable params = new java.util.Hashtable();
    params.put("msg", msg);
    params.put("total", Integer.toString(total));
    params.put("max", Integer.toString(max));
    params.put("start", Integer.toString(start));

    if (method.equals("xml")) {
        response.setContentType("text/xml");
        XMLSerializer.out(root.getFirstChild(), "xml", out);
    } else {  // this uses XMLSerializer methods from xerces J2
        response.setContentType("text/html");
        XMLSerializer.out(XMLTransformer.transform(params, root, styleSheet), "html", out);
    }
  %>