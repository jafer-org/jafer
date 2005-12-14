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

  <xsl:param name="totalResults"/><!-- total number of results from search -->
  <xsl:param name="maxHits"/><!-- maximum number of results to display per page -->

  <!-- only matches first element found, (eg: name) in the case of multiple occurrences -->

  <xsl:template match="root">
    <html>
      <head>
        <title>JAFER Toolkit Project: ZServlet</title>
      </head>
      <body bgcolor="#eeeeee">
        <h1>
          <a href="http://jafer.org">JAFER Toolkit Project: ZServlet</a>
        </h1>
        <hr/>

        <h3>
          <xsl:text>Found </xsl:text><xsl:value-of select= "$totalResults"/>
          <xsl:choose>
            <xsl:when test="$totalResults &lt;= $maxHits">
              <xsl:text> matches:</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text> matches, displaying the first </xsl:text><xsl:value-of select= "$maxHits"/><xsl:text> matches:</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </h3>

        <ul>
          <xsl:apply-templates/>
        </ul>

        <xsl:if test="count(exception) > 0">
          <xsl:value-of select="count(exception)"/><xsl:text> record/s were errored, and are not displayed.</xsl:text>
          <p>
            <xsl:value-of select="exception/message"/><br /><!-- not for-each exception, may be too many -->
            <xsl:comment><xsl:value-of select="exception/text()"/></xsl:comment>
            <!-- swap <li> with <xsl:comment> to show/hide stack trace output in HTML source.  -->
            <xsl:for-each select="exception[position() = 1]/stackTrace">
              <xsl:comment>
                <xsl:value-of select="text()"/>
              </xsl:comment><br />
            </xsl:for-each>
            <!-- swap </li> with </xsl:comment> to show/hide stack trace output in HTML source. -->
          </p>
          <p><i>(Additional info may be available by viewing the associated HTML source code.)</i></p>
        </xsl:if>

        <xsl:if test="count(diagnostic) > 0">
          <xsl:value-of select="count(diagnostic)"/><xsl:text> diagnostic record/s were returned, and are not displayed.</xsl:text>
          <xsl:for-each select="diagnostic">
            <!-- swap <li> with <xsl:comment> to show/hide stack trace output in HTML source.  -->
            <xsl:comment>
              <xsl:value-of select="condition[@value]"/>
              <xsl:value-of select="condition"/>
              <xsl:value-of select="additionalInfo"/>
            </xsl:comment>
            <!-- swap </li> with </xsl:comment> to show/hide stack trace output in HTML source. -->
          </xsl:for-each>
          <p><i>(Additional info may be available by viewing the associated HTML source code.)</i></p>
        </xsl:if>

        <hr/>
        <!-- Please acknowledge the JAFER project: -->
        <p><i size="-1">ZServlet 2.0. &#169; <a href="http://www.jafer.org">JAFER Toolkit Project</a></i></p>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="oai_marc:oai_marc"><!-- linking uses DocID -->
   <xsl:variable name="docId"><xsl:value-of select="substring(oai_marc:fixfield[@id='001'],8,8)"/></xsl:variable>
   <li>
     <a><xsl:attribute name="href">OpenURL?sid=jafer:servlet&amp;pid=<xsl:value-of select="$docId"/></xsl:attribute>
     <xsl:value-of select="oai_marc:varfield[@id='100' or @id='110' or @id='111' or @id='130']"/>
     <xsl:text> - </xsl:text>
     <xsl:value-of select="oai_marc:varfield[@id='245' or @id='242' or @id='246']"/></a>
   </li>
 </xsl:template>

</xsl:stylesheet>