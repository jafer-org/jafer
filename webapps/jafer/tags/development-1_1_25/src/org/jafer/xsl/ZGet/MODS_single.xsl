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
                xmlns:mods="http://www.loc.gov/mods/" exclude-result-prefixes="mods">

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

  <xsl:template match="mods:mods">
    <!-- Author -->
    <xsl:if test="mods:name">
      <tr>
        <td width="15%"><b>Author</b></td>
        <td colspan="3"><xsl:value-of select="mods:name/text()"/></td>
      </tr>
    </xsl:if>

    <!-- Title -->
    <xsl:if test="mods:title">
      <tr>
        <td width="15%"><b>Title</b></td>
        <td colspan="3"><xsl:value-of select="mods:title/text()"/></td>
      </tr>
    </xsl:if>

    <!-- Related Item -->
    <xsl:for-each select="mods:relatedItem">
      <xsl:variable name="identifier" select="mods:identifier"/>
      <xsl:choose>
        <xsl:when test="mods:identifier[@type='isbn']">
          <tr>
            <td width="15%"><b>Related Item</b></td>
            <td colspan="3"><a href="ZGet?isbn={$identifier}"><xsl:value-of select="mods:title"/></a></td>
          </tr>
        </xsl:when>
        <xsl:when test="mods:identifier[@type='issn']">
          <tr>
            <td width="15%"><b>Related Item</b></td>
            <td colspan="3"><a href="ZGet?issn={$identifier}"><xsl:value-of select="mods:title"/></a></td>
          </tr>
        </xsl:when>
        <xsl:when test="mods:identifier[@type='local' and substring($identifier, 2, 5)='UkOxU']">
          <tr>
            <td width="15%"><b>Related Item</b></td>
            <td colspan="3"><a href="ZGet?docid={substring($identifier, 8, 8)}"><xsl:value-of select="mods:title"/></a></td>
          </tr>
        </xsl:when>
        <xsl:otherwise>
          <tr>
            <td width="15%"><b>Related Item</b></td>
            <td colspan="3">
              <xsl:choose>
                <xsl:when test="mods:title">
                  <xsl:value-of select="mods:title"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="."/><!-- content can be at top level, or in title element -->
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </tr>
        </xsl:otherwise>
      </xsl:choose>

    </xsl:for-each>

    <!-- Publisher -->
    <xsl:if test="mods:publication/mods:publisher">
      <tr>
        <td width="15%"><b>Publisher</b></td>
        <td colspan="3"><xsl:value-of select="mods:publication/mods:publisher"/></td>
      </tr>
    </xsl:if>

    <!-- Description -->
    <xsl:if test="mods:formAndPhysicalDescription/mods:extent">
      <tr>
        <td width="15%"><b>Description</b></td>
        <td colspan="3"><xsl:value-of select="mods:formAndPhysicalDescription/mods:extent"/></td>
      </tr>
    </xsl:if>

    <!-- Note -->
    <xsl:if test="mods:note">
      <tr>
        <td width="15%"><b>Note</b></td>
        <td colspan="3"><xsl:value-of select="mods:note"/></td>
      </tr>
    </xsl:if>

    <!-- ISSN -->
    <xsl:if test="mods:identifier[@type='issn']">
      <tr>
        <td width="15%"><b>ISSN</b></td>
        <td colspan="3"><xsl:value-of select="mods:identifier"/></td>
      </tr>
    </xsl:if>

    <!-- ISBN -->
    <xsl:if test="mods:identifier[@type='isbn']">
      <tr>
        <td width="15%"><b>ISBN</b></td>
        <td colspan="3"><xsl:value-of select="mods:identifier"/></td>
      </tr>
    </xsl:if>

    <!-- Subject -->
    <xsl:for-each select="mods:subject/mods:topic | mods:subject/mods:geographic" >
      <tr>
        <td width="15%"><b>Subject</b></td>
        <td colspan="3"><xsl:value-of select="."/></td>
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

    <xsl:for-each select="mods:extension">
      <tr>
        <td colspan="2">
          <xsl:for-each select="mods:shelvingLocation">
            <xsl:value-of select="."/><xsl:text>  </xsl:text>
          </xsl:for-each>
        </td>
        <td colspan="1">
          <xsl:for-each select="mods:callNumber">
            <xsl:value-of select="."/><xsl:text>  </xsl:text>
          </xsl:for-each>
        </td>
        <td colspan="1">
          <xsl:value-of select="mods:circulationStatus"/>
        </td>
      </tr>
    </xsl:for-each>

    <!-- Online -->
    <xsl:for-each select="mods:identifier[@type='uri']">
      <tr>
        <td width="15%">
          <b>Online</b>
        </td>
        <td colspan="3">
          <xsl:value-of select="./mods:notes"/><xsl:text> </xsl:text><!--URL note -->
          <a><xsl:attribute name="href">
          <xsl:value-of select="node()"/>
        </xsl:attribute><xsl:value-of select="node()"/></a></td>
      </tr>
    </xsl:for-each>

  </xsl:template>

</xsl:stylesheet>