<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:portlet="http://www.uportal.org/extensions/portlet" xmlns:mods="http://www.loc.gov/mods/">
    <xsl:output method="html"/>

    <xsl:param name="mediaPath">images</xsl:param>
    <xsl:param name="author"></xsl:param>
    <xsl:param name="title"></xsl:param>
    <xsl:param name="locale">en_US</xsl:param>

    <xsl:template match="/record">
      <p>
        <a href="renderURL">
          <portlet:param name="action" value="search"/>
          <img alt="New Search" src="{$mediaPath}/find.gif" align="TOP"/>
        </a>

        <xsl:if test="@id &gt; 1">
        <a href="renderURL">
          <portlet:param name="action" value="item"/>
          <portlet:param name="id" value="{@id - 1}" />
          <img alt="Previous Record" src="{$mediaPath}/left.gif" align="TOP"/>
        </a>
        </xsl:if>

        <a href="renderURL">
          <portlet:param name="action" value="list"/>
          <portlet:param name="id" value="{@id}" />
          <img alt="List Records" src="{$mediaPath}/return.gif" align="TOP"/>
        </a>

        <xsl:if test="@id &lt; @total">
        <a href="renderURL">
          <portlet:param name="action" value="item"/>
          <portlet:param name="id" value="{@id + 1}" />
          <img alt="Next Record" src="{$mediaPath}/right.gif" align="TOP"/>
        </a>
        </xsl:if>

        <p>
          <p>
            Your search has found <xsl:value-of select="@total"/> records: viewing record <xsl:value-of select="@id"/>
          </p>
          <dl>
            <xsl:apply-templates select="*" />
          </dl>
        </p>
      </p>
    </xsl:template>

    <xsl:template match="mods:name">
      <dt><b>Author</b></dt><dd><xsl:value-of select="."/></dd>
    </xsl:template>
    <xsl:template match="mods:title">
      <dt><b>Title</b></dt><dd><xsl:value-of select="."/></dd>
    </xsl:template>
    <xsl:template match="mods:publisher">
      <dt><b>Publisher</b></dt><dd><xsl:value-of select="."/></dd>
    </xsl:template>
    <xsl:template match="mods:extent">
      <dt><b>Description</b></dt><dd><xsl:value-of select="."/></dd>
    </xsl:template>
    <xsl:template match="mods:identifier">
      <xsl:choose>
        <xsl:when test="@type = 'isbn'">
          <dt><b>ISBN</b></dt>
          <dd>
            <xsl:value-of select="."/><br />
            <xsl:choose>
              <xsl:when test="/record/@database = 'INNOPAC'">
                <a href="http://library.hull.ac.uk/search/i{.}" target="_blank">Direct link to catalogue</a>
              </xsl:when>
              <xsl:when test="/record/@database = 'MAIN*BIBMAST'">
                <a href="http://library.ox.ac.uk/find?isbn={.}" target="_blank">Direct link to catalogue</a>
              </xsl:when>
            </xsl:choose>
          </dd>
        </xsl:when>
        <xsl:when test="@type = 'issn'">
          <dt><b>ISSN</b></dt><dd><xsl:value-of select="."/></dd>
            <xsl:choose>
              <xsl:when test="/record/@database = 'INNOPAC'">
                <a href="http://library.hull.ac.uk/search/i{.}" target="_blank">Direct link to catalogue</a>
              </xsl:when>
              <xsl:when test="/record/@database = 'MAIN*BIBMAST'">
                <a href="http://library.ox.ac.uk/find?issn={.}" target="_blank">Direct link to catalogue</a>
              </xsl:when>
            </xsl:choose>
        </xsl:when>
      </xsl:choose>
    </xsl:template>
    <xsl:template match="mods:topic">
      <dt><b>Subject</b></dt><dd><xsl:value-of select="."/></dd>
    </xsl:template>
    <xsl:template match="text()">
      <xsl:value-of select="." />
    </xsl:template>
    <xsl:template match="*">
      <xsl:apply-templates select="*" />
    </xsl:template>
</xsl:stylesheet>
