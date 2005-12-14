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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" indent="yes"/>

  <xsl:template match="root">
    <html>
      <head>
        <title>JAFER Toolkit Project: ZServlet</title>
      </head>
      <body bgcolor="#eeeeee">
        <h1>
          <a href="http://www.lib.ox.ac.uk/jafer/">JAFER Toolkit Project: ZServlet</a>
        </h1>
        <hr/>
        <h2>The following error/s have occurred:</h2>
        <xsl:for-each select="exception">
          <h3>Message:</h3>
          <p><xsl:value-of select="message"/></p>
          <h3>Detail:</h3>
          <p><xsl:value-of select="text()"/></p>
          <xsl:for-each select="stackTrace">
          <!-- swap <li> with <xsl:comment> to show/hide stack trace output in HTML source.  -->
            <xsl:comment>
              <xsl:value-of select="text()"/>
            </xsl:comment>
          <!-- swap </li> with </xsl:comment> to show/hide stack trace output in HTML source. -->
          </xsl:for-each>
        </xsl:for-each>
        <p><i>(Additional info may be available by viewing the associated HTML source code.)</i></p>
        <p></p>
        <hr/>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
