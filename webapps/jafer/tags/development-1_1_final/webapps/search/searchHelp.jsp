<!DOCTYPE html public "-//W3C//DTD HTML 4.0 Final//EN">
<%@ page import="org.jafer.util.xml.*" %>
<%@ page import="org.w3c.dom.Node" %>
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
    <title>JAFER Attribute list</title>
  </head>
  <body bgcolor="#eeeeee">
    <table cellspacing = "20">
      <tr>
        <td><a href="advancedSearch.html"><img src="find.gif" alt="search" border="0"></a></td>
        <td><font face="Arial" size="+2"><b>
          The following is a list of valid search attributes</b></font><em> (bib1 use attributes)</em></td></tr>
      <tr>
        <td colspan="2"><hr></td></tr>
      <tr>
        <td></td>
        <td>Enter search attributes and terms seperated by '='<br>eg author=jones and title=fish</td></tr>
      <tr>
        <td></td>
        <td>Valid boolean operators are: and, or, andNot, orNot<br>('not' is equivalent to 'andNot' - they are not case sensitive)</td></tr>
      <tr>
        <td></td>
        <td><font face="Arial" size="+1">
          <%
          DOMFactory domFactory = new DOMFactory();
          Node bib1Doc = domFactory.parse(this.getClass().getClassLoader().getResource("org/jafer/conf/bib1.xml"));
          Node htlmList = XMLTransformer.transform(bib1Doc, this.getClass().getClassLoader().getResource("org/jafer/xsl/bib1List.xsl"));
          XMLSerializer.out (htlmList, "html", out);
          %></font></td></tr>
      <tr>
        <td colspan="2"><hr></td></tr>
      <tr>
        <td colspan=2><font face="Arial" size="-1">
          Further documnetation and help can be found at the <a href="http://www.jafer.org">JAFER website</a></font></td></tr>
      <tr>
    </table>
  </body>
</html>