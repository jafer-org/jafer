<?xml version="1.0" encoding="utf-8"?>
<!--
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
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>
  <xsl:template match="root">
    <html>
      <head>
        <title>Server Admin Info</title>
      </head>
      <body bgcolor="#eeeeee">
        <table cellspacing="10">
          <tr>
            <td></td><td></td>
            <td><font face="Arial"><b>Id</b></font></td>
            <td><font face="Arial"><b>upTime</b></font></td>
            <td><font face="Arial"><b>State</b></font></td>
            <td><font face="Arial"><b>Threads</b></font></td>
            <td><font face="Arial"><b>Address</b></font></td>
            <td><font face="Arial"><b>Port</b></font></td></tr>
          <xsl:for-each select="thread">
           <tr><td colspan="8"><hr/></td></tr>
           <tr>
             <td colspan="2"><font face="Arial"><b>server</b></font></td>
             <td align="right"><font face="Arial"><xsl:value-of select="@id"/></font></td>
             <td align="right"><font face="Arial"><xsl:value-of select="upTime"/></font></td>
             <td align="right"><font face="Arial"><xsl:value-of select="state"/></font></td>
             <td align="right"><font face="Arial"><xsl:value-of select="threads"/></font></td>
             <td align="right"><font face="Arial"><xsl:value-of select="address"/></font></td>
             <td align="right"><font face="Arial"><xsl:value-of select="port"/></font></td></tr>
           <tr><td colspan="8"><hr/></td></tr>
           <xsl:for-each select="thread">
            <tr><td></td>
              <td><font face="Arial"><b>session</b></font></td>
              <td align="right"><font face="Arial"><xsl:value-of select="@id"/></font></td>
              <td align="right"><font face="Arial"><xsl:value-of select="upTime"/></font></td>
              <td align="right"><font face="Arial"><xsl:value-of select="state"/></font></td>
              <td align="right"><font face="Arial"><xsl:value-of select="threads"/></font></td>
              <td align="right"><font face="Arial"><xsl:value-of select="address"/></font></td>
              <td align="right"><font face="Arial"><xsl:value-of select="port"/></font></td></tr>
            </xsl:for-each>
          </xsl:for-each>
         <tr></tr><tr></tr>
         <tr><td colspan="8"><font face="Arial"><xsl:value-of select="message"/></font></td></tr>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>