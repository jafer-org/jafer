<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="html" encoding="UTF-8" indent="no"/>

  <xsl:variable name="displayZAttribute">false</xsl:variable>

  <xsl:template match="/">
    <p>Query submitted: <xsl:apply-templates/></p>
  </xsl:template>

  <xsl:template match="constraintModel">
    <xsl:variable name="semantic" select="constraint/semantic"/>
    <xsl:variable name="relation" select="constraint/relation"/>
    <xsl:variable name="semanticValue" select="document('../conf/bib1Attributes.xml')/attributeSets/attributeSet[@name='bib1']/attributeType[@name='semantic']"></xsl:variable>

    <xsl:value-of select="$semanticValue/attribute[@value=$semantic]/@name"/>
    <xsl:if test="$displayZAttribute = 'true'">
      <xsl:text> (</xsl:text><xsl:value-of select="$semantic"/><xsl:text>) </xsl:text>
    </xsl:if>

    <xsl:choose>
      <xsl:when test="$relation = '1'"><xsl:text> &lt; </xsl:text></xsl:when>
      <xsl:when test="$relation = '2'"><xsl:text> &lt;= </xsl:text></xsl:when>
      <xsl:when test="$relation = '3'"><xsl:text> = </xsl:text></xsl:when>
      <xsl:when test="$relation = '4'"><xsl:text> &gt;= </xsl:text></xsl:when>
      <xsl:when test="$relation = '5'"><xsl:text> &gt; </xsl:text></xsl:when>
      <xsl:when test="$relation = '6'"><xsl:text> != </xsl:text></xsl:when>
      <xsl:when test="$relation = '100'"><xsl:text> phonetic </xsl:text></xsl:when>
      <xsl:when test="$relation = '101'"><xsl:text> stem </xsl:text></xsl:when>
      <xsl:when test="$relation = '102'"><xsl:text> relevance </xsl:text></xsl:when>
      <xsl:when test="$relation = '103'"><xsl:text> always matches </xsl:text></xsl:when>
      <xsl:otherwise><xsl:text> = </xsl:text></xsl:otherwise>
    </xsl:choose>

    <xsl:text> '</xsl:text><xsl:value-of select="model"/><xsl:text>'</xsl:text>
  </xsl:template>

  <xsl:template match="and | or | andnot | AND | OR | ANDNOT | andNot">
    <xsl:text>(</xsl:text><xsl:apply-templates select="*[position()=1]"/><xsl:text>) </xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text> (</xsl:text><xsl:apply-templates select="*[position()=2]"/><xsl:text>)</xsl:text>
  </xsl:template>

</xsl:stylesheet>