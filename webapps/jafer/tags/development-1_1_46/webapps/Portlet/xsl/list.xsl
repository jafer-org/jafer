<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:portlet="http://www.uportal.org/extensions/portlet" xmlns:mods="http://www.loc.gov/mods/">
    <xsl:output method="html" omit-xml-declaration="yes" doctype-public="" doctype-system=""/>

    <xsl:param name="mediaPath">images</xsl:param>
    <xsl:param name="author"></xsl:param>
    <xsl:param name="title"></xsl:param>
    <xsl:param name="locale">en_US</xsl:param>

    <xsl:template match="/records">
      <p>
        <a href="renderURL">
          <portlet:param name="action" value="search"/>
          <img alt="New Search" src="{$mediaPath}/find.gif" align="TOP"/>
        </a>

        <xsl:if test="@start &gt; 10">
        <a href="renderURL">
          <portlet:param name="action" value="list"/>
          <portlet:param name="id" value="{@start - 10}" />
          <img alt="Previous Records" src="{$mediaPath}/left.gif" align="TOP"/>
        </a>
        </xsl:if>

        <xsl:if test="@end &lt; @total">
        <a href="renderURL">
          <portlet:param name="action" value="list"/>
          <portlet:param name="id" value="{@end + 1}" />
          <img alt="Next Records" src="{$mediaPath}/right.gif" align="TOP"/>
        </a>
        </xsl:if>

        <hr />
        <xsl:choose>
          <xsl:when test="@total = 0">
            <p>Your search has not found any matching records!</p>
          </xsl:when>
          <xsl:when test="@total > 0">
            <p>
              <p>
                Your search has found <xsl:value-of select="@total"/> records: viewing records <xsl:value-of select="@start"/> to <xsl:value-of select="@end"/>
              </p>
              <xsl:apply-templates select="record" />
            </p>
          </xsl:when>
        </xsl:choose>
      </p>
    </xsl:template>

    <xsl:template match="record">
      <a href="renderURL">
        <portlet:param name="action" value="item" />
        <portlet:param name="id" value="{@id}" />
        <img alt="Show result" src="{$mediaPath}/viewHTML.gif" align="TOP"/>
        <xsl:value-of select="mods:mods/mods:title"/> - <xsl:value-of select="mods:mods/mods:name"/>
      </a><br />
    </xsl:template>
</xsl:stylesheet>
