<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

  <xsl:template match="bib1">
    <ul>
      <xsl:for-each select="*">
        <li><xsl:value-of select="name()"/></li>
      </xsl:for-each>
    </ul>
  </xsl:template>
</xsl:stylesheet>
