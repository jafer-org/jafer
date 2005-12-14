<!DOCTYPE html public "-//W3C//DTD HTML 4.0 Final//EN">
<%@ page isErrorPage="true" %>
<%@ page import="org.jafer.util.xml.DOMFactory" %>
<%@ page import="org.jafer.util.xml.XMLSerializer" %>

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
%>

<html>
  <head>
    <title>JAFER Exception</title>
  </head>
  <body bgcolor="#eeeeee">
    <table cellspacing = "20">
      <tr>
        <td><img src="cross.gif" border="0"></td>
        <td><font face="Arial" size="+2"><b>
          An error has occurred whilst searching...</b></font></td></tr>
      <tr>
        <td colspan="2"><hr></td></tr>
      <tr>
        <td></td>
        <td><font face="Arial" size="+1">
          <%out.print(exception.getMessage());%></font></td></tr>
      <tr>
        <td colspan="2"><hr></td></tr>
      <tr>
        <td colspan=2><font face="Arial" size="-1"><em>
          Further details may be found embedded in this HTML page</em></font></td></tr>
      <tr>
        <td colspan=2><font face="Arial" size="-1">
          If this problem persists you should contact your web administrator<br>
          Any problems concerning the software can be reported via the <a href="http://www.jafer.org">JAFER website</a></font></td></tr>
      <tr>
    </table>
    <%
      out.print("<!-- ");
        XMLSerializer.out(DOMFactory.getExceptionNode(DOMFactory.newDocument(), exception, exception.getStackTrace()), "html", out);
      out.print("--> ");
    %>
  </body>
</html>