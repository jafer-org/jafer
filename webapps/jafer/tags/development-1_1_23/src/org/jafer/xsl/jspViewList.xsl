<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
     xmlns:mods="http://www.loc.gov/mods/">
<xsl:output method="html" />

<xsl:param name="msg" select="''"/>
<xsl:param name="total" select="0"/>
<xsl:param name="max" select="0"/>
<xsl:param name="start" select="0"/>

  <xsl:template match="root">
    <html>
      <head>
        <title>view list</title>
      </head>
      <body bgcolor="#eeeeee">
        <table cellspacing="10">
          <tr>
          <td width="25">
            <xsl:if test="$start - $max &gt; 0">
              <a><xsl:attribute name="href">client.jsp?start=<xsl:value-of select="$start - $max"/>&amp;list=true&amp;method=html</xsl:attribute>
              <img src="left.gif" border="0"/></a>
            </xsl:if></td>
            <td width="25">
              <a href="advancedSearch.html"><img src="find.gif" border="0"/></a></td>
            <td width="25">
            <xsl:if test="$start + $max &lt;= $total">
              <a><xsl:attribute name="href">client.jsp?start=<xsl:value-of select="$start + $max"/>&amp;list=true&amp;method=html</xsl:attribute>
              <img src="right.gif" border="0"/></a>
            </xsl:if></td>
          <td><font face="Arial"><b>

          <xsl:choose>
            <xsl:when test="$total &lt;= 0">
              Your search has not found any matching records!
            </xsl:when>
            <xsl:otherwise>
                Your search has found <xsl:value-of select="$total"/> records: viewing records

                  <xsl:choose>
                    <xsl:when test="$start + $max &gt; $total">
                      <xsl:value-of select="$start"/> to
                                                <xsl:value-of select="$total"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$start"/> to
                                                <xsl:value-of select="$start + $max - 1"/>
                    </xsl:otherwise>
                  </xsl:choose>
            </xsl:otherwise>
          </xsl:choose></b></font></td></tr>
          <xsl:if test="$total &gt; 0">
            <tr>
              <td colspan="4">
              <hr/></td></tr>
          </xsl:if>

          <xsl:for-each select="record">
            <tr>
              <td width="25"></td>
              <td width="25">
                  <a><xsl:attribute name="href">client.jsp?start=<xsl:value-of select="$start"/>&amp;id=<xsl:value-of select="@number"/>&amp;item=true&amp;method=html</xsl:attribute>
                  <img src="viewHTML.gif" border="0"></img></a></td>
              <td width="25">
                  <a><xsl:attribute name="href">client.jsp?start=<xsl:value-of select="$start"/>&amp;id=<xsl:value-of select="@number"/>&amp;item=true&amp;method=xml</xsl:attribute>
                  <img src="viewXML.gif" border="0"></img></a></td>
              <td align = "left"><font face="Arial" size="-1">
                  <xsl:value-of select="mods:mods/mods:name"/><!-- fullstop needed? -->
                    <xsl:value-of select="mods:mods/mods:title"/></font></td></tr>
          </xsl:for-each>
          <xsl:if test="$total &gt; 0">
            <tr>
              <td colspan="4">
              <hr/></td></tr>
          </xsl:if>
          <tr>
            <td colspan="3"></td>
            <td align = "left"><font face="Arial" size="-1"><xsl:value-of select="$msg"/></font></td></tr>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>