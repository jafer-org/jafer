<jsp:useBean id="bean" scope="session" class="org.jafer.zclient.ZClient" />
<%@ page import="org.jafer.record.*" %>
<%@ page errorPage="error.jsp" %>
<html>
  <head><title>view item</title></head>
  <body bgcolor="#eeeeee">
<%
/**
 * JAFER Toolkit Poject.
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
    String data, msg = "";
    int id;
    try {
      id = Integer.parseInt(request.getParameter("id"));
    } catch (NumberFormatException n) {
      throw new Exception("<p>A valid integer value for \"id\" (record number) must be supplied<p>") ;
    }

    int max = Integer.parseInt((String)session.getAttribute("max"));
    int start = (((int)(id - 1 ) / max) * max) + 1;

    out.print("<p><b><a href=\"list.jsp?start=" + start + "\">Back</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
      "Vewing record " + id + " of " + Integer.parseInt((String)session.getAttribute("total")) + "</b></p>");
    out.print("<p><dl>");

    try {
      bean.setRecordCursor(id);
      Field record = bean.getCurrentRecord();

      data = record.getFirst("name").getValue();
      if (!data.equals(""))
        out.print("<dt><b>Author</b><dd>" + data);
      data = record.getFirst("title").getValue();
      if (!data.equals(""))
        out.print("<dt><b>Title</b><dd>" + data);
      data = record.getFirst("publisher").getValue();
      if (!data.equals(""))
        out.print("<dt><b>Publisher</b><dd>" + data);
      data = record.getFirst("extent").getValue();
      if (!data.equals(""))
        out.print("<dt><b>Description</b><dd>" + data);
      data = record.getFirst("identifier", "type", "isbn").getValue();
      if (!data.equals(""))
        out.print("<dt><b>ISBN</b><dd>" + data);
      data = record.getFirst("identifier", "type", "issn").getValue();
      if (!data.equals(""))
        out.print("<dt><b>ISSN</b><dd>" + data);
      data = record.getFirst("topic").getValue();
      if (!data.equals(""))
        out.print("<dt><b>Subject</b><dd>" + data);

    } catch (RecordException e) {
      msg = "Some records contained errors, and are not displayed";
      System.err.print("Skipping record " + id + ": "+e.toString());
    } catch (org.jafer.exception.JaferException e) {
      msg = "Sorry, an error has ocurred whilst searching";
      System.err.print("Skipping record " + id + ": "+e.toString());
    }

    out.print("</dl></p>");
    out.print("<p>" + msg + "</p>");
  %>
  </body>
</html>
