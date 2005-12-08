<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns="http://www.openarchives.org/OAI/1.1/oai_marc" xmlns:mx="http://www.loc.gov/MARC21/slim"
 exclude-result-prefixes="mx">

<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>


<xsl:template match="mx:record">
  <xsl:variable name="leader">
    <xsl:value-of select="mx:leader"/>
  </xsl:variable>
  <xsl:variable name="status"><xsl:value-of select="substring($leader, 6,1)"/></xsl:variable>
  <xsl:variable name="type"><xsl:value-of select="substring($leader, 7,1)"/></xsl:variable>
  <xsl:variable name="level"><xsl:value-of select="substring($leader, 8,1)"/></xsl:variable>
  <!--xsl:variable name="encLvl"><xsl:value-of select="substring($leader, 17,1)"/></xsl:variable-->
  <!--xsl:variable name="catForm"><xsl:value-of select="substring($leader, 18,1)"/></xsl:variable-->

  <oai_marc status="{$status}" type="{$type}" level="{$level}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/1.1/oai_marc http://www.openarchives.org/OAI/1.1/oai_marc.xsd">

      <xsl:apply-templates select="mx:controlfield|mx:datafield"/>
  </oai_marc>
 </xsl:template>

 <xsl:template match="mx:controlfield">
   <xsl:variable name="id">
     <xsl:call-template name="tag2id"/>
   </xsl:variable>

   <fixfield id="{$id}">
     <xsl:text>"</xsl:text>
     <xsl:value-of select="text()"/>
     <xsl:text>"</xsl:text>
   </fixfield>
 </xsl:template>


 <xsl:template match="mx:datafield">
   <xsl:variable name="id">
     <xsl:call-template name="tag2id"/>
   </xsl:variable>

   <xsl:variable name="i1">
     <xsl:call-template name="idBlankSpace">
       <xsl:with-param name="value" select="@ind1"/>
     </xsl:call-template>
   </xsl:variable>

   <xsl:variable name="i2">
     <xsl:call-template name="idBlankSpace">
       <xsl:with-param name="value" select="@ind2"/>
     </xsl:call-template>
   </xsl:variable>

   <varfield id="{$id}" i1="{$i1}" i2="{$i2}">
     <xsl:apply-templates select="mx:subfield"/>
   </varfield>
 </xsl:template>

 <xsl:template match="mx:subfield">
   <subfield label="{@code}">
     <xsl:value-of select="text()"/>
   </subfield>
 </xsl:template>

 <xsl:template name="tag2id">
   <xsl:variable name="id" select="@tag"/>
   <xsl:choose>
     <xsl:when test="string-length($id)=1">
       <xsl:text>00</xsl:text>
       <xsl:value-of select="$id"/>
     </xsl:when>
     <xsl:when test="string-length($id)=2">
       <xsl:text>0</xsl:text>
       <xsl:value-of select="$id"/>
     </xsl:when>
     <xsl:when test="string-length($id)=3">
       <xsl:value-of select="$id"/>
     </xsl:when>
   </xsl:choose>
 </xsl:template>

 <xsl:template name="idBlankSpace">
   <xsl:param name="value"/>
    <xsl:choose>
      <xsl:when test="string-length($value)=0">
        <xsl:text> </xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$value"/>
      </xsl:otherwise>
    </xsl:choose>
 </xsl:template>
</xsl:stylesheet>