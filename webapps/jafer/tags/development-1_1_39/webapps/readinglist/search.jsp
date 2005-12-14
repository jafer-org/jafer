<!DOCTYPE html public "-//W3C//DTD HTML 4.0 Final//EN">
<!--
  JAFER Toolkit Project.
  Copyright (C) 2002, JAFER Toolkit Project, Oxford University.

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 -->
<jsp:useBean id="bean" scope="session" class="org.jafer.zclient.ZClient" />
<%@ page import="org.jafer.query.*" %>
<%@ page import="org.jafer.util.xml.*" %>

<%@ page import="org.w3c.dom.*" %>
<%@ page import="java.net.URL" %>

<%@ page errorPage="error.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
  <head>
    <title>JAFER: Reading List generator</title>
  </head>
  <body bgcolor="#aaaadd">
    <table>
      <tr><td valign="top">
            <img src="tomcat.gif">&nbsp;<br>
            <a href="search.jsp">New Search</a><br><br>
            <a href="basket.jsp?">View Basket</a><br><br>
          </td>
            <br>
          <td>
  <%
  /* configuration of target using values in configuration file: */
  String hostName, database = "xxdefault";
  int port = 210;

  if (this.getInitParameter("hostName") != null)
    hostName = this.getInitParameter("hostName");
  else
    throw new ServletException("No value found in configuration file for \"hostName\" parameter.");

  if (this.getInitParameter("database") != null)
    database = this.getInitParameter("database");
  try {
    port = Integer.parseInt(this.getInitParameter("port"));
    } catch (NumberFormatException n) {
      /** @todo log the use of default settings: port: 210 and/or database: xxdefault */
    }

  int perPage, start;
  Node currentNode = null;
  URL stylesheet = this.getClass().getClassLoader().getResource("org/jafer/xsl/query.xsl");


  if (request.getParameterNames().hasMoreElements()) {

    if (request.getParameter("start") == null) { // i.e: search has not been performed yet.

      Node input1 = null, input2 = null, input3 = null;

      QueryBuilder builder = new QueryBuilder();
      // if text has been entered in a search page input box, a query node is made:
      if (!request.getParameter("term1").equalsIgnoreCase(""))
        input1 = builder.getNode(request.getParameter("attribute1"), request.getParameter("term1"));
      if (!request.getParameter("term2").equalsIgnoreCase(""))
        input2 = builder.getNode(request.getParameter("attribute2"), request.getParameter("term2"));
      if (!request.getParameter("term3").equalsIgnoreCase(""))
        input3 = builder.getNode(request.getParameter("attribute3"), request.getParameter("term3"));

      // processing precedence: (term1, term2) term3

      // process input 1 and input 2:
      if (input1 != null && input2 != null) {
        if (request.getParameter("operator1").equalsIgnoreCase("and"))
        currentNode = builder.and(input1, input2);
        else if (request.getParameter("operator1").equalsIgnoreCase("or"))
        currentNode = builder.or(input1, input2);
        else if (request.getParameter("operator1").equalsIgnoreCase("not"))
        currentNode = builder.and(input1, builder.not(input2));
        // then process input 3:
        if (input3 != null) {
          if (request.getParameter("operator2").equalsIgnoreCase("and"))
          currentNode = builder.and(currentNode, input3);
          else if (request.getParameter("operator2").equalsIgnoreCase("or"))
          currentNode = builder.or(currentNode, input3);
          else if (request.getParameter("operator2").equalsIgnoreCase("not"))
          currentNode = builder.and(currentNode, builder.not(input3));
        }
      }
      else if (input2 != null && input3 != null) {
        if (request.getParameter("operator2").equalsIgnoreCase("and"))
        currentNode = builder.and(input2, input3);
        else if (request.getParameter("operator2").equalsIgnoreCase("or"))
        currentNode = builder.or(input2, input3);
        else if (request.getParameter("operator2").equalsIgnoreCase("not"))
        currentNode = builder.and(input2, builder.not(input3));
      }
      else { // no Booleans used, term entered may be in any text box:
        if (input1 != null)
          currentNode = input1;
        else if (input2 != null)
          currentNode = input2;
        else if (input3 != null)
          currentNode = input3;
      }

      if (currentNode == null)
        throw new QueryException("No query built.");
      else {
        bean.setHost(hostName);
        bean.setPort(port);
        bean.setDatabases(database);
        bean.setRecordSchema("http://www.loc.gov/mods/");
        bean.submitQuery(currentNode);
      }
      session.setAttribute("start", "1");
    }

  %>
    <form method="post" action="basket.jsp">
      <h1>JAFER Reading List</h1>
      <h2>Demo Results page:</h2>
  <%
  /* Code to display textual representation of query submitted: */
    if (bean.getQuery() instanceof Node && stylesheet != null) {
      Document doc = DOMFactory.newDocument();
      Node root = doc.createElement("root");
      root.appendChild(doc.importNode((Node)bean.getQuery(), true));
      XMLSerializer.transformOutput(root, stylesheet, out);
    }
  /* end */
    int results = bean.getNumberOfResults();
    if (results == 0) {
    %>
      <p>Your search did not generate any results.</p>
    <%
    }
    else {
      if (request.getParameter("perPage") != null) {
        perPage = Integer.parseInt(request.getParameter("perPage"));
        session.setAttribute("perPage", request.getParameter("perPage"));
      }
      else
        perPage = Integer.parseInt(session.getAttribute("perPage").toString()); // throws java.lang.IllegalStateException

      if (request.getParameter("start") != null) {
        start = Integer.parseInt(request.getParameter("start"));
        session.setAttribute("start", request.getParameter("start"));
      }
      else
        start = Integer.parseInt(session.getAttribute("start").toString()); // throws java.lang.IllegalStateException

      int end = (start + perPage)-1;
      if (end > results)
        end = results;

  %>
      <p>Displaying results <b><%= start %></b> to <b><%= end %></b> of <%= results %> results:</p>
      <table width="800" border="1" cellpadding="2" cellspacing="0" >
        <tr><th><img src="add.gif"></th>
            <th>Author:</th>
            <th>Title:</th>
        </tr>
  <%

      String author, title, docId;

      for (int n = start; n <= end; n++) {
        bean.setRecordCursor(n);
        author = bean.getCurrentRecord().getFieldData("name", "role", "creator");// first name (@role=creator) element
        title = bean.getCurrentRecord().getFirst("title").getValue();// first title element
        docId = bean.getCurrentRecord().getFirst("recordIdentifier").getValue();
  %>
        <tr><td ><input type="checkbox" name="checkbox<%= n %>" value="<%= docId %>" ></td>
            <td width="30%"><%= author %></td>
            <td width="70%"><a href="recordDetail.jsp?recordNo=<%= n %>"><%= title %></a></td>
        </tr>
  <%
      }
  %>
      </table>

      <p><b>Result pages:</b></p>
  <%
      int pageRange = 3; //number of pages displayed on either side of current page
      int currentPage = (perPage > 1) ? (start/perPage) + 1 : start/perPage;
      int displayPageNo = (currentPage - pageRange > 0) ? currentPage - pageRange : 1;
      int lastPage = (results % perPage == 0) ? results/perPage : results/perPage + 1;
      int displayStartRecord = (displayPageNo - 1)*perPage + 1;

  %>
      <table cellpadding="5">
          <tr>
  <%
        for (int n = 1, p = displayStartRecord; (n <= pageRange*2 + 1) && (p <= results); n++, p += perPage) {
          if (p == start) {
  %>
              <td><font color="red"><%= displayPageNo %></font></td>
  <%    }
          else {
  %>
              <td><a href="search.jsp?start=<%= p %>"><%= displayPageNo %></a></td>
  <%
          }
          displayPageNo++;
        }
        if (displayPageNo - 1 < lastPage) {
  %>
              <td>more...</td>
  <%
        }
  %>
          </tr>
      </table>
      <br>
      <input type="submit" value="Add selected items">
      <input type="hidden" name="operation" value="add">
      <input type="hidden" name="start" value="<%= start %>">
      <input type="hidden" name="end" value="<%= end %>">
    </form>
  <%
    }
  }
  else {
  %>
    <form action="search.jsp" method="post">
      <h1>JAFER Reading List</h1>
      <h2>Demo Search page:</h2>
      <br>
      <table>
              <tr><td><input type="text" name="term1">&nbsp;
                      <select name="attribute1">
                        <option value="author">Author
                        <option value="title">Title
                        <option value="subject_heading">Subject
                        <option value="isbn">ISBN
                        <option value="issn">ISSN
                        <option value="any">Any
                      </select>
              <tr>
              <tr><td><select name="operator1">
                      <option value="or">OR
                      <option value="and">AND
                      <option value="not">NOT
                    </select>
              <tr>
              <tr><td><input type="text" name="term2">&nbsp;
                      <select name="attribute2">
                        <option value="author">Author
                        <option selected value="title">Title
                        <option value="subject_heading">Subject
                        <option value="isbn">ISBN
                        <option value="issn">ISSN
                        <option value="any">Any
                      </select>
              <tr>
              <tr><td><select name="operator2">
                        <option value="or">OR
                        <option value="and">AND
                        <option value="not">NOT
                      </select>
              <tr>
              <tr><td><input type="text" name="term3">&nbsp;
                      <select name="attribute3">
                        <option value="author">Author
                        <option value="title">Title
                        <option value="subject_heading">Subject
                        <option value="isbn">ISBN
                        <option value="issn">ISSN
                        <option value="doc_id">Doc ID
                        <option selected value="any">Any
                      </select>
            </table>
  <%
            if (session.getAttribute("perPage") != null)
              perPage = Integer.parseInt(session.getAttribute("perPage").toString());
            else
              perPage = 10;
  %>
      Number of results per page:&nbsp;<input name="perPage" size="2" value="<%= perPage %>" tabindex="6">
      <br><br>
      <table>
        <tr><td><input type="reset" name="reset" value="Reset" tabindex="7"></td>
            <td><input type="submit" name="search" value="Search" tabindex="8"></td>
      </table>
    </form>
  <%
  }
  %>
      <p>&copy; <i size="-1"><a href="http://www.jafer.org">JAFER Toolkit Project</a></i></p>
    </table>
  </body>
</html>