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
<%@ page import="org.jafer.zclient.*" %>
<%@ page import="org.jafer.record.Field" %>
<%@ page import="org.jafer.exception.JaferException" %>

<%@ page errorPage="error.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
  <head>
    <title>JAFER: Record detail</title>
  </head>
  <body bgcolor="#aaaadd">
    <table>
      <tr><td valign="top">
            <img src="tomcat.gif">&nbsp;<br>
            <a href="search.jsp">New Search</a><br><br>
            <a href="search.jsp?start=<%= session.getAttribute("start") %>">View Results</a><br><br>
            <a href="basket.jsp?">View Basket</a><br><br>
          </td>
          <br>
          <td>
            <h1>JAFER Reading List</h1>
            <h2>Demo record detail page:</h2>
            <br>
  <%
    Field record;
    /** @todo
     *  error handling if parameters are invalid or missing.
     */
    if (request.getParameter("recordNo") != null) {
      int index;
      try {
        index = Integer.parseInt(request.getParameter("recordNo"));
      } catch (NumberFormatException e) {
        throw new JaferException("Invalid value for recordNo parameter: "+request.getParameter("recordNo"));
      }
      bean.setRecordCursor(index);
      record = bean.getCurrentRecord();
    }
    else if (request.getParameter("recordID") != null)
      record = basket.getItem(request.getParameter("recordID"));

    else
      throw new JaferException("Missing parameter: recordNo or recordID parameter needed.");

    String author = record.getFirst("name", "role", "creator").getValue();// first name (@role=creator) element
    String title = record.getFirst("title").getValue();// first title element
    String docId = record.getFirst("recordIdentifier").getValue();

  %>
    <table width="800" border="1" cellpadding="2" cellspacing="0" >
      <tr><td width="15%"><b>Author</b></td>
          <td colspan="3"><%= author %></td></tr>
      <tr><td width="15%"><b>Title</b></td>
          <td colspan="3"><%= title %></td></tr>
  <%
    for (int n=0; n < record.get("controlledValue").length; n++) {
  %>
      <tr><td width="15%"><b>Genre</b></td>
          <td colspan="3"><%= record.get("controlledValue")[n].getValue() %></td></tr>
  <%
    }
    if (record.get("publisher").length > 0) {
  %>
      <tr><td width="15%"><b>Publisher</b></td>
          <td colspan="3"><%= record.getFirst("publisher").getValue() %></td></tr>
  <%
    }
    if (record.get("extent").length > 0) {
  %>
      <tr><td width="15%"><b>Description</b></td>
          <td colspan="3"><%= record.getFirst("extent").getValue() %></td></tr>
  <%
    }
    String note;
    for (int n=0; n < record.get("note").length; n++) {
      note = record.get("note")[n].getValue() + "  ";
  %>
      <tr><td width="15%"><b>Note</b></td>
          <td colspan="3"><%= note %></td></tr>
  <%
  }
    String identifier;
    for (int n=0; n < record.get("identifier", "type", "isbn").length; n++) {
      identifier = record.get("identifier", "type", "isbn")[n].getValue() + "  ";
  %>
      <tr><td width="15%"><b>ISBN</b></td>
          <td colspan="3"><%= identifier %></td></tr>
  <%
    }
    for (int n=0; n < record.get("identifier", "type", "issn").length; n++) {
      identifier = record.get("identifier", "type", "issn")[n].getValue() + "  ";
  %>
      <tr><td width="15%"><b>ISSN</b></td>
          <td colspan="3"><%= identifier %></td></tr>
  <%
    }
    String subject;
    for (int n=0; n < record.get("topic").length; n++) { //topical subject
      subject = record.get("topic")[n].getValue() + "  ";
  %>
      <tr><td width="15%"><b>Subject</b></td>
          <td colspan="3"><%= subject %></td></tr>
  <%
    }
    for (int n=0; n < record.get("geographic").length; n++) { //geographic subject
      subject = record.get("geographic")[n].getValue() + "";
  %>
      <tr><td width="15%"><b>Subject</b></td>
          <td colspan="3"><%= subject %></td></tr>
  <%
    }
  %>
      <tr><th colspan="4">Library Holdings</th></tr>
      <tr><th colspan="2">Location</th>
          <th>Call Number</th>
          <th>Status</th></tr>
  <%
    String location, callNo, status;
    Field[] extensions = record.get("extension");
    for (int n=0; n < extensions.length; n++) {
      location = "";
      for (int i=0; i < extensions[n].get("shelvingLocation").length; i++) {
        location += extensions[n].get("shelvingLocation")[i].getValue() + " ";
      }
      callNo = "";
      for (int j=0; j < extensions[n].get("callNumber").length; j++) {
        callNo += extensions[n].get("callNumber")[j].getValue() + " ";
      }
      status = extensions[n].getFirst("circulationStatus").getValue();
  %>

      <tr><td colspan="2"><%= location %></td>
          <td colspan="1"><%= callNo %></td>
          <td colspan="1"><%= status %></td></tr>
  <%
    }
    String uri, notes;
    for (int n=0; n < record.get("identifier", "type", "uri").length; n++) {
      uri = record.get("identifier", "type", "uri")[n].getValue();
      notes = record.get("identifier", "type", "uri")[n].getFirst("notes").getValue() + " ";
  %>
      <tr><td width="15%"><b>Online</b></td>
          <td colspan="3"><%= notes %><a href="<%= uri %>"><%= uri %></a></td></tr>
  <%
    }
  %>
    </table>
    <p>&copy; <i size="-1"><a href="http://www.jafer.org">JAFER Toolkit Project</a></i></p>
    </table>
  </body>
</html>