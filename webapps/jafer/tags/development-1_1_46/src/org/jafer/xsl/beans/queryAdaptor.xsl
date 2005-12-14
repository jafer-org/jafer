<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Example stylesheet:
Use attributes set to '1' or '1000' are changed to '1003'.
A Relation attribute is added to every query, with a value set to 3.
Any Truncation attributes are removed. -->

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="constraintModel">
  <constraintModel>
    <xsl:apply-templates/>
  </constraintModel>
</xsl:template>

<xsl:template match="constraint">
  <constraint>
    <xsl:apply-templates/>
    <xsl:call-template name="relation">
      <!-- Example code: modifies relation attribute by calling template named 'relation' below: -->
    </xsl:call-template>
  </constraint>
</xsl:template>

<xsl:template match="model"><!-- i.e.  query term -->
  <model>
    <xsl:value-of select="."/>
  </model>
</xsl:template>


<xsl:template match="semantic"><!-- i.e.  Use Attribute -->
<!-- Example code: values of 1 or 1000 are converted to 1003, others are unchanged. -->
  <semantic>
    <xsl:variable name="semantic" select="."/>
    <xsl:choose>
      <xsl:when test="$semantic = '1' or $semantic = '1000'">1003</xsl:when>
      <xsl:otherwise><xsl:value-of select="$semantic"/></xsl:otherwise>
    </xsl:choose>
  </semantic>
</xsl:template>

<xsl:template name="relation" match="relation">
<!-- Example code: relation attribute with a value of 3 is used regardless of value set in original query -->
  <xsl:choose>
    <xsl:when test="name(.) = 'relation'"/>
    <xsl:otherwise>
      <relation>
        <xsl:text>3</xsl:text>
      </relation>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="position">
  <position>
    <xsl:value-of select="."/>
  </position>
</xsl:template>

<xsl:template match="structure">
  <structure>
    <xsl:value-of select="."/>
  </structure>
</xsl:template>

<xsl:template match="truncation"/><!-- removes any truncation attribute -->

<xsl:template match="completeness">
  <completeness>
    <xsl:value-of select="."/>
  </completeness>
</xsl:template>



<xsl:template match="and | AND | And">
  <and>
    <xsl:apply-templates/>
  </and>
</xsl:template>

<xsl:template match="or | OR | Or">
  <or>
    <xsl:apply-templates/>
  </or>
</xsl:template>

<xsl:template match="not | NOT | Not">
  <not>
    <xsl:apply-templates/>
  </not>
</xsl:template>

</xsl:stylesheet>