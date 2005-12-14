<jsp:useBean id="bean" scope="session" class="org.jafer.zclient.ZClient" />
<%@ page errorPage="error.jsp" %>
<%@ page contentType="text/xml; charset=UTF-8" %>

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

    response.setContentType("text/xml");
    int recNo;

    try {
      recNo = Integer.parseInt(request.getParameter("recNo"));
    } catch (NumberFormatException n) {
      throw new Exception("A valid integer value for \"recNo\" must be supplied.") ;
    }

    bean.setRecordCursor(recNo);
    org.jafer.util.xml.XMLSerializer.out(bean.getCurrentRecord().getXML(), "xml", out);
%>
