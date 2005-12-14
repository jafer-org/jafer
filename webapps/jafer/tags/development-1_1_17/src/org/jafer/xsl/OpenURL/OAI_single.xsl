<?xml version="1.0" encoding="utf-8"?>
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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:oai_marc="http://www.openarchives.org/OAI/oai_marc" exclude-result-prefixes="oai_marc">

  <xsl:output method="html" indent="yes" doctype-public="-//W3C//DTD HTML 4.0 Final//EN"/>

  <xsl:template match="root">
    <html>
      <head>
        <title>JAFER Toolkit Project: ZServlet</title>
      </head>
      <body bgcolor="#eeeeee">
        <h1>
          <a href="http://www.jafer.org">JAFER Toolkit Project: ZServlet</a>
        </h1>
        <hr/>

        <table border="1" width="100%">
          <xsl:apply-templates/>
        </table>

        <hr/>
        <!-- Please acknowledge the JAFER project: -->
        <p><i size="-1">ZServlet 2.0. &#169; <a href="http://www.jafer.org">JAFER Toolkit Project</a></i></p>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="oai_marc:oai_marc">
    <!-- Author -->
    <xsl:if test="oai_marc:varfield[@id='100' or @id='110' or @id='111']">
      <tr>
        <td width="15%"><b>Author</b></td>
        <td colspan="3"><xsl:value-of select="oai_marc:varfield[@id='100' or @id='110' or @id='111']"/></td>
      </tr>
    </xsl:if>

    <!-- Title -->
    <xsl:if test="oai_marc:varfield[@id='245']">
      <tr>
        <td width="15%"><b>Title</b></td>
        <td colspan="3"><xsl:value-of select="oai_marc:varfield[@id='245']/oai_marc:subfield[@label='a']"/></td>
      </tr>
    </xsl:if>

    <!-- Related Item -->
    <xsl:for-each select="oai_marc:varfield[@id='780' or @id='785' or @id='776' or @id='534' or @id='786' or @id='772' or @id='773'
    or @id='770' or @id='774' or @id='490' or @id='440' or @id='760' or @id='800' or @id='810' or @id='811' or @id='830']">
      <xsl:variable name="identifier" select="oai_marc:subfield[@label='x' or @label='z' or @label='w']"/>
      <xsl:choose>
        <xsl:when test="oai_marc:subfield[@label='x']">
          <tr>
            <td width="15%"><b>Related Item</b></td>
            <td colspan="3"><a href="OpenURL?issn={$identifier}"><xsl:value-of select="oai_marc:subfield[@label='t']"/></a></td>
          </tr>
        </xsl:when>
        <xsl:when test="oai_marc:subfield[@label='z']">
          <tr>
            <td width="15%"><b>Related Item</b></td>
            <td colspan="3"><a href="OpenURL?isbn={$identifier}"><xsl:value-of select="oai_marc:subfield[@label='t']"/></a></td>
          </tr>
        </xsl:when>
        <xsl:otherwise>
          <tr>
            <td width="15%"><b>Related Item</b></td>
            <td colspan="3">
              <xsl:for-each select="oai_marc:subfield">
                <xsl:value-of select="."/><xsl:text>  </xsl:text><!-- content can be at top level, or in child element -->
              </xsl:for-each>
            </td>
          </tr>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>

    <!-- Publisher -->
    <xsl:if test="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='b']">
      <tr>
        <td width="15%"><b>Publisher</b></td>
        <td colspan="3"><xsl:value-of select="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='b']"/></td>
      </tr>
    </xsl:if>

    <!-- Description -->
    <xsl:if test="oai_marc:varfield[@id='256']/oai_marc:subfield[@label='a']">
      <tr>
        <td width="15%"><b>Description</b></td>
        <td colspan="3"><xsl:value-of select="oai_marc:varfield[@id='256']/oai_marc:subfield[@label='a']"/></td>
      </tr>
    </xsl:if>

    <xsl:for-each select="oai_marc:varfield[@id='300']">
      <tr>
        <td width="15%"><b>Description</b></td>
        <td colspan="3"><xsl:value-of select="oai_marc:subfield[@label='a']"/>
        <xsl:value-of select="oai_marc:subfield[@label='b']"/>
        <xsl:value-of select="oai_marc:subfield[@label='c']"/></td>
      </tr>
    </xsl:for-each>

    <!-- Note -->
    <xsl:for-each select="oai_marc:varfield[@id='500' or @id='505' or @id='511' or @id='518']">
      <tr>
        <td width="15%"><b>Note</b></td>
        <td colspan="3"><xsl:value-of select="."/></td>
      </tr>
    </xsl:for-each>

    <!-- ISSN -->
    <xsl:if test="oai_marc:varfield[@id='022']">
      <tr>
        <td width="15%"><b>ISSN</b></td>
        <td colspan="3"><xsl:value-of select="oai_marc:varfield[@id='022']"/></td>
      </tr>
    </xsl:if>

    <!-- ISBN -->
    <xsl:if test="oai_marc:varfield[@id='020']">
      <tr>
        <td width="15%"><b>ISBN</b></td>
        <td colspan="3"><xsl:value-of select="oai_marc:varfield[@id='020']"/></td>
      </tr>
    </xsl:if>

    <!-- Subject -->
    <xsl:for-each select="oai_marc:varfield[@id='600' or @id='610' or @id='611' or @id='630' or @id='650' or @id='651']">
      <tr>
        <td width="15%"><b>Subject</b></td>
        <td colspan="3">
          <xsl:for-each select="oai_marc:subfield">
            <xsl:value-of select="."/><xsl:text> </xsl:text>
          </xsl:for-each>
        </td>
      </tr>
    </xsl:for-each>

    <!-- Library Holdings -->
    <tr>
      <th colspan="4">Library Holdings</th>
    </tr>
    <tr>
      <th colspan="2">Location</th>
      <th>Call Number</th>
      <th>Status</th>
    </tr>

    <xsl:for-each select="oai_marc:varfield[@id='852']">
      <tr>
        <td colspan="2">
          <!-- Location -->
          <xsl:for-each select="oai_marc:subfield[@label='b']">
            <xsl:value-of select="."/><xsl:text> </xsl:text>
          </xsl:for-each>
        </td>
        <td colspan="1">
          <!-- Call Number -->
          <xsl:for-each select="oai_marc:subfield[@label='h' or @label='m']">
            <xsl:value-of select="."/><xsl:text> </xsl:text>
          </xsl:for-each>
        </td>
        <td colspan="1">
          <!-- Status -->
          <xsl:for-each select="oai_marc:subfield[@label='y']">
            <xsl:value-of select="."/><xsl:text> </xsl:text>
          </xsl:for-each>
        </td>
      </tr>
    </xsl:for-each>

    <!-- Online -->
    <xsl:for-each select="oai_marc:varfield[@id='856']">
      <tr>
        <td width="15%">
          <b>Online</b>
        </td>
        <td colspan="3">
          <xsl:value-of select="oai_marc:subfield[@label='z']"/><xsl:text> </xsl:text><!-- URL note -->
          <a><xsl:attribute name="href">
          <xsl:value-of select="oai_marc:subfield[@label='u']"/>
        </xsl:attribute><xsl:value-of select="oai_marc:subfield[@label='u']"/></a></td>
      </tr>
    </xsl:for-each>

  </xsl:template>

</xsl:stylesheet>