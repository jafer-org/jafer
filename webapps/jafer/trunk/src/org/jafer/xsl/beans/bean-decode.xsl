<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:template match="property|arrayMember">
    <xsl:element name="void">
      <xsl:if test="name(.)='property'">
      <xsl:attribute name="property"><xsl:value-of select="@name"/></xsl:attribute></xsl:if>
      <xsl:if test="name(.)='arrayMember'">
      <xsl:attribute name="index"><xsl:number value="position()-1"></xsl:number></xsl:attribute></xsl:if>
      <xsl:if test="@type">
        <xsl:element name="{@type}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:if>
      <xsl:if test="@class">
        <object class="{@class}">
          <xsl:apply-templates select="*"/>
        </object>
      </xsl:if>
      <xsl:if test="@array">
        <array class="{@array}" length="{count(arrayMember)}">
          <xsl:apply-templates select="*"/>
        </array>
      </xsl:if>
    </xsl:element>
  </xsl:template>
  <xsl:template match="//property|//arrayMember">
    <java version="1.4.0_01" class="java.beans.XMLDecoder">
      <xsl:if test="@type">
        <xsl:element name="{@type}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:if>
      <xsl:if test="@class">
        <object class="{@class}">
          <xsl:apply-templates select="*"/>
        </object>
      </xsl:if>
      <xsl:if test="@array">
        <array class="{@array}" length="{count(arrayMember)}">
          <xsl:apply-templates select="*"/>
        </array>
      </xsl:if>
    </java>
  </xsl:template>
</xsl:stylesheet>
