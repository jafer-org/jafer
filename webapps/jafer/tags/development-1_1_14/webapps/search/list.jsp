<!DOCTYPE html public "-//W3C//DTD HTML 4.0 Final//EN">
<jsp:useBean id="bean" scope="session" class="org.jafer.zclient.ZClient"/>
<%@ page import="org.jafer.zclient.*" %>
<%@ page import="org.jafer.record.Field" %>
<%@ page import="org.jafer.query.QueryBuilder" %>
<%@ page import="org.jafer.exception.JaferException" %>
<%@ page errorPage="error.jsp" %>
<html>
  <head><title>view list</title></head>
  <body bgcolor="#eeeeee">
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
    response.setContentType("text/html");
    String method, msg = "";
    boolean list, item;
    int total, start, max, last;

    if (new Boolean(request.getParameter("query")).booleanValue()) {

      String title = request.getParameter("title").trim();
      String author = request.getParameter("author").trim();

      if (title.equals("") && author.equals(""))
          throw new JaferException("Please specify a Title and/or Author");

      bean.setHost(request.getParameter("ipAddress"));
      bean.setDatabases(request.getParameter("dbName"));
      bean.setPort(Integer.parseInt(request.getParameter("portNumber")));
      bean.setFetchSize(3);
      bean.setAutoReconnect(3);
      bean.setRecordSchema("http://www.loc.gov/mods/");

      QueryBuilder query = new QueryBuilder();
      if (title.equals(""))
        bean.submitQuery(
          query.getNode("author", author));
      else if (author.length() == 0)
        bean.submitQuery(
          query.getNode("title", title));
      else
        bean.submitQuery(
          query.and(query.getNode("author", author),
                    query.getNode("title", title)));

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

    last = start + max - 1;
    if (last > total || last < 1)
        last = total;

    out.print("<p><b><a href=simpleSearch.html>Search</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    if (start - max > 0)
      out.print("<a href=\"list.jsp?start=" + (start - max) + "\">Back</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    if (start + max <= total)
      out.print("<a href=\"list.jsp?start=" + (start + max) + "\">Next</a>");

    if (total <= 0)
      out.print("</b></p><p><b>Your search has not found any matching records!</b><p>");
    else {
      out.print("</b></p><p><b>Your search has found " + total + " records: viewing records ");
      if (start + max > total)
        out.print(start + " to " + total + "</b><p>");
      else
        out.print(start + " to " + (start + max - 1) + "</b><p>");
    }
    out.print("<p><ul>");

    for (int n = start; n <= last; n++) {
      try {
          bean.setRecordCursor(n);
          Field record = bean.getCurrentRecord();
          String title = record.getFirst("title").getValue();
          String name = record.getFirst("name").getValue();

          if (title != null && name != null) {
              out.print("<li><a href=item.jsp?id=" + n + ">");
              if (title != null)
                out.print(title);
              if (name != null)
                out.print(name);
              out.print("</a></li>");
          }

      } catch (org.jafer.record.RecordException e) {
        msg = "Some records contained errors, and are not displayed";
        System.err.print("Skipping record " + n + ": " + e.toString());

      } catch (org.jafer.exception.JaferException e) {
        msg = "Sorry, an error has ocurred whilst searching";
        System.err.print("Search error: " + e.toString());
      }
    }
    out.print("</ul></p>");
    out.print("<p>" + msg + "</p>");
  %>
  </body>
</html>