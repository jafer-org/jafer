<!DOCTYPE html public "-//W3C//DTD HTML 4.0 Final//EN">
<jsp:useBean id="bean" scope="session" class="org.jafer.zclient.ZClient" />
<%@ page import="org.jafer.zclient.*" %>
<%@ page import="org.jafer.util.ConnectionException" %>
<%@ page import="org.jafer.zclient.operations.*" %>
<%@ page import="org.jafer.query.*" %>
<%@ page errorPage="error.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%
/**
 * JAFER Toolkit Project.
 * Copyright (C) 2002, JAFER Toolkit Project, Oxford University.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
%>

<html>
  <head>
    <title>JAFER: search results</title>
  </head>
  <body>

  <%
      if (request.getParameter("ipAddress").equalsIgnoreCase(""))
        throw new ConnectionException("An IP address must be supplied.");
      bean.setHost(request.getParameter("ipAddress"));

      try {
        bean.setPort(Integer.parseInt(request.getParameter("portNumber")));
      } catch (NumberFormatException n) {
        throw new ConnectionException("A valid integer value for \"port\" must be supplied.") ;
      }

      bean.setDatabases(new String[]{request.getParameter("dbName")});
//      bean.setXMLRecords(true);

      QueryBuilder query = new QueryBuilder();

      int nResults;
      String title = request.getParameter("title").trim();
      String author = request.getParameter("author").trim();

      if (title.length() == 0 && author.length() == 0)
        nResults = 0;
      else if (title.length() == 0)
        nResults = bean.submitQuery(query.getNode("author", author));
      else if (author.length() == 0)
        nResults = bean.submitQuery(query.getNode("title", title));
      else
        nResults = bean.submitQuery(query.and(
          query.getNode("author", author),
          query.getNode("title", title)));

      int perPage = Integer.parseInt(request.getParameter("perPage"));
      if (perPage > nResults) perPage = nResults;

  %>
      <h1>SEARCH RESULTS:</h1>
  <%
      if (nResults == 0)
      out.println("<p>Your search did not generate any results</p>");

      else {
        out.print("<p>Your search has generated " + nResults + " results</p>");
        out.print("<p>Displaying results " + 1 + " - " + perPage + "</p>");
  %>
      <table border="1" cellpadding="2" cellspacing="0" >
      <tr>
          <th>Author:</th>
          <th>Title:</th>
          <th>DocId:</th></tr>

  <%
      String currentAuthor, currentTitle, docId;

      for (int n = 1; n <= perPage; n++) {

      bean.setRecordCursor(n);

      currentAuthor = bean.getCurrentRecord().getFirst("name", "role", "creator").getValue();
      currentTitle = bean.getCurrentRecord().getFirst("title").getValue();
      docId = bean.getCurrentRecord().getFirst("recordIdentifier").getValue();
  %>
      <tr>
          <td><%= currentAuthor %></td>
          <td><%= currentTitle %></td>
          <td><a href="<%= response.encodeURL("show.jsp?recNo="+n) %>"><%= docId %></a></td>
      </tr>
  <%
        }
  %>
      </table>
  <%
      }
  %>

<form method="post" action="query.html"/>
<table>
  <tr><td>
    <input type="submit" value="Additional search" >
  </td></tr>
</table>
</form>
</body>
</html>