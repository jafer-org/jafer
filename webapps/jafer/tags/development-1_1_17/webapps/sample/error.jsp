<!DOCTYPE html public "-//W3C//DTD HTML 4.0 Final//EN">
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
  <body>
  <%@ page isErrorPage="true" %>

    <p><font face="Arial" size="+2" align="center">
      <b>JAFER Exception</b></font></p>
    <p><font face="Arial">
      <b>The following error has ocurred</b><br><br>
        <%= exception.toString() %></font></p>
    <pre><% exception.printStackTrace(new java.io.PrintWriter(out)); %></pre>

  </body>
</html>