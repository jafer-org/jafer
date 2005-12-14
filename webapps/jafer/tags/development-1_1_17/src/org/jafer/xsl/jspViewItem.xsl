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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
     xmlns:mods="http://www.loc.gov/mods/">
<xsl:output method="html"/>

<xsl:param name="msg" select="''"/>
<xsl:param name="total" select="0"/>
<xsl:param name="max" select="0"/>
<xsl:param name="start" select="0"/>

  <xsl:template match="root/record">

    <html>
      <head>
        <title>view record</title>
      </head>
      <body bgcolor="#eeeeee">
        <table cellspacing="10">
          <tr>
            <td width="25">
              <xsl:if test="@number - 1 &gt; 0">
                <a><xsl:attribute name="href">client.jsp?start=<xsl:value-of select="$start"/>&amp;id=<xsl:value-of select="@number - 1"/>&amp;item=true&amp;method=html</xsl:attribute>
                <img src="left.gif" border="0"></img></a>
              </xsl:if>
            </td>
            <td width="25">
                <a><xsl:attribute name="href">client.jsp?start=<xsl:value-of select="$start"/>&amp;list=true&amp;html=true</xsl:attribute>
                <img src="return.gif" border="0"/></a></td>
            <td width="25">
              <xsl:if test="@number + 1 &lt;= $total">
                <a><xsl:attribute name="href">client.jsp?start=<xsl:value-of select="$start"/>&amp;id=<xsl:value-of select="@number + 1"/>&amp;item=true&amp;method=html</xsl:attribute>
                <img src="right.gif" border="0"></img></a>
              </xsl:if>
            </td>
            <td colspan="3"><font face="Arial"><b>
              Viewing record <xsl:value-of select="@number"/> of <xsl:value-of select="$total"/></b></font></td></tr>
          <tr>
            <td colspan="6"><hr/></td></tr>

          <xsl:for-each select="mods:mods">

            <xsl:if test="mods:name/@role='creator'">
              <tr>
                <td colspan="3">
                  <font face="Arial"><b>Author</b></font>
                </td>
                <td colspan="3"><font face="Arial"><xsl:value-of select="mods:name"/></font></td>
              </tr>
            </xsl:if>

            <xsl:if test="mods:title">
              <tr>
                <td colspan="3"><font face="Arial">
                  <b>Title</b></font>
                </td>
                <td colspan="3"><font face="Arial"><xsl:value-of select="mods:title"/></font></td>
              </tr>
            </xsl:if>

            <xsl:if test="mods:publication/mods:publisher">
              <tr>
                <td colspan="3"><font face="Arial">
                  <b>Publisher</b></font>
                </td>
                <td colspan="3"><font face="Arial">
                <xsl:value-of select="mods:publication/mods:publisher"/></font></td>
              </tr>
            </xsl:if>

            <xsl:if test="mods:formAndPhysicalDescription/mods:extent">
              <tr>
                <td colspan="3"><font face="Arial">
                  <b>Description</b></font>
                </td>
                <td colspan="3"><font face="Arial"><xsl:value-of select="mods:formAndPhysicalDescription/mods:extent"/></font></td>
              </tr>
            </xsl:if>

            <xsl:if test="mods:notes">
              <tr>
                <td colspan="3"><font face="Arial">
                  <b>Notes</b></font>
                </td>
                <td colspan="3"><font face="Arial"><xsl:value-of select="mods:notes"/></font></td>
              </tr>
            </xsl:if>

            <xsl:if test="mods:identifier">
              <xsl:choose>
                <xsl:when test="mods:dentifier[@type='issn']">
                  <tr>
                    <td colspan="3"><font face="Arial">
                      <b>ISSN</b></font>
                    </td>
                    <td colspan="3"><font face="Arial"><xsl:value-of select="mods:identifier"/></font></td>
                  </tr>
                </xsl:when>
                <xsl:when test="mods:identifier[@type='isbn']">
                  <tr>
                    <td colspan="3"><font face="Arial">
                      <b>ISBN</b></font>
                    </td>
                    <td colspan="3"><font face="Arial"><xsl:value-of select="mods:identifier"/></font></td>
                  </tr>
                </xsl:when>
              </xsl:choose>
            </xsl:if>

            <xsl:for-each select="mods:subject" >
              <tr>
                <td colspan="3"><font face="Arial">
                  <b>Subject</b></font>
                </td>
                <td colspan="3"><font face="Arial"><xsl:value-of select="."/></font></td>
              </tr>
            </xsl:for-each>
            <tr>
              <td colspan="3" align="left"><font face="Arial"><b>Library Holdings</b></font></td>
              <td width="150" align="left"><font face="Arial"><b>location</b></font></td>
              <td width="150" align="left"><font face="Arial"><b>call number</b></font></td>
              <td width="150" align="left"><font face="Arial"><b>status</b></font></td>
            </tr>

            <xsl:if test="mods:extension">
              <xsl:for-each select="mods:extension">
                <tr><td colspan="3" ></td>
                  <td align="left"><font face="Arial">
                    <xsl:value-of select="mods:shelvingLocation"/></font>
                  </td>
                  <td align="left"><font face="Arial">
                    <xsl:value-of select="mods:callNumber"/></font>
                  </td>
                  <td align="left"><font face="Arial">
                    <xsl:value-of select="mods:circulationStatus"/></font>
                  </td>
                </tr>
              </xsl:for-each>
            </xsl:if>

            <xsl:if test="mods:identifier/@type='uri'">
            <xsl:for-each select="mods:identifier[@type='uri']">
              <tr>
                <td colspan="3"><font face="Arial">
                  <b>Online</b></font>
                </td>
                <td colspan="3"><font face="Arial">
                <xsl:value-of select="./mods:notes"/><!-- need to include URL notes? -->
                  <a><xsl:attribute name="href">
                  <xsl:value-of select="node()"/>
                </xsl:attribute><xsl:value-of select="node()"/></a></font></td>
              </tr>
              </xsl:for-each>
            </xsl:if>
            <tr>
              <td colspan="6"><hr/></td></tr>
        </xsl:for-each>

        <tr>
          <td colspan="3"></td>
          <td colspan="3" align = "left"><font face="Arial" size="-1"><xsl:value-of select="$msg"/></font></td></tr>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>