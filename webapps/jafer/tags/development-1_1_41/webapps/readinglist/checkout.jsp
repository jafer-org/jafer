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
<jsp:useBean id="basket" scope="session" class="org.jafer.readinglist.Basket" />
<%@ page import="org.jafer.util.xml.*" %>
<%@ page import="org.jafer.exception.JaferException" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.URL" %>
<%@ page errorPage="basketError.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

  <%
    URL stylesheet = this.getClass().getClassLoader().getResource("org/jafer/xsl/output.xsl");
    Document doc = bean.getDocument();
    Node record, records = doc.createElement("records");

    String[] keys = basket.getKeyArray();

    for (int n=0; n < keys.length; n++) {
      record = basket.getItem(keys[n]).getXML();
      records.appendChild(record);
    }

    if (stylesheet != null) {
      String style = request.getParameter("style");
      Map paramMap = new Hashtable(1);// only 1 parameter needed
      paramMap.put("style", style);

      XMLSerializer.transformOutput(records, stylesheet, paramMap, out);
    }
    else
      throw new JaferException("stylesheet (org/jafer/xsl/output.xsl) not loaded");
  %>
