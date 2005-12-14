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
<jsp:useBean id="basket" scope="session" class="org.jafer.readinglist.Basket" />
<jsp:useBean id="bean" scope="session" class="org.jafer.zclient.ZClient" />
<%@ page import="org.jafer.record.Field" %>
<%@ page errorPage="error.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
  <head>
    <title>JAFER: Basket contents</title>
  </head>
  <body bgcolor="#aaaadd">
    <table>
          <tr><td valign="top">
                <img src="tomcat.gif">&nbsp;<br>
                <a href="search.jsp">New Search</a><br><br>
                <a href="search.jsp?start=<%= session.getAttribute("start") %>">View Results</a><br><br>
                <form method="post" action="basket.jsp">
                  <input type="submit" name="operation" value="Mark all"><br><br>
                  <input type="submit" name="reset" value="Reset all">
                </form>
              </td>
              <br>
              <td>
  <%
    String checked = "";

    if (request.getParameter("operation") != null) {
      if (request.getParameter("operation").equalsIgnoreCase("add")) {
        int start = Integer.parseInt(request.getParameter("start"));
        int end = Integer.parseInt(request.getParameter("end"));

        for (int n=start; n <= end; n++) {
          if (request.getParameter("checkbox"+n) != null) {
            bean.setRecordCursor(n);
            basket.addItem(request.getParameter("checkbox"+n), bean.getCurrentRecord());
          }
        }
      }
      else if (request.getParameter("operation").equalsIgnoreCase("remove")) {
        int basketSize = basket.size();
        for (int i=0; i < basketSize ; i++) {
          if (request.getParameter("item"+i) != null)
            basket.removeItem(request.getParameter("item"+i));
        }
      }
      else if (request.getParameter("operation").equalsIgnoreCase("Mark all"))
        checked = "checked";
    }
  %>
          <h1>JAFER Reading List</h1>
          <h2>Demo Basket Contents page:</h2>
  <%
    if (basket.size() > 0) {
  %>
          <form method="post" action="basket.jsp"/>
            <table width="800" border="1" cellpadding="2" cellspacing="0">
              <tr><th>Author:</th>
                  <th>Title:</th>
                  <th><img src="remove.gif"></th>
              </tr>
  <%  String[] keys = basket.getKeyArray();
      Field field;
      String author, title = "";

      for (int n=0; n < keys.length; n++) {
        field = basket.getItem(keys[n]);
        author = field.getFirst("name", "role", "creator").getValue();// first name (@role=creator) element
        title = field.getFirst("title").getValue();// first title element
  %>
              <tr><td width="30%"><%= author %></td>
                  <td width="70%"><a href="recordDetail.jsp?recordID=<%= keys[n] %>"><%= title %></a></td>
                  <td><input type="checkbox" name="item<%= n %>" value="<%= keys[n] %>" <%= checked %>></td>
              </tr>
  <%
      }
  %>
            </table>
            <br>
            <table width="100%">
            <tr><td align="right"><input align="right" type="submit" value="Remove selected items">
                                  <input type="hidden" name="operation" value="remove"></td>

            </table>
          </form>

          <br>
          <form method="post" action="checkout.jsp">
            <select name="style">
              <option selected value="style1">Author, Title, Doc Id
              <option value="style2">Title, Author, Publisher
              <option value="style3">With Title link
              <option value="style4">Multiple tables (No links)
            </select>
            &nbsp;&nbsp;
            <input type="submit" value="Output HTML table">
          </form>
  <%
    }
    else {
  %>
          <b>Your basket is empty!</b>
  <%
    }
  %>
          <br>
          <p>&copy; <i size="-1"><a href="http://www.jafer.org">JAFER Toolkit Project</a></i></p>
    </table>
  </body>
</html>