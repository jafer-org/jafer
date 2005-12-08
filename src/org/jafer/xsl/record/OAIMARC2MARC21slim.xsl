<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns="http://www.loc.gov/MARC21/slim"
 xmlns:oai="http://www.openarchives.org/OAI/1.1/oai_marc" exclude-result-prefixes="oai">

<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>


<xsl:template match="oai:oai_marc">
  <record xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.loc.gov/MARC21/slim  http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd" >

   <leader><!-- need to add test for missing optional attributes? -->
    <xsl:text>     </xsl:text>
    <xsl:value-of select="@status"/>
    <xsl:value-of select="@type"/>
    <xsl:value-of select="@level"/>
    <xsl:text>  22     </xsl:text>
    <xsl:value-of select="@encLvl"/>
    <xsl:value-of select="@catForm"/>
    <xsl:text> 4500</xsl:text>
   </leader>
   <xsl:apply-templates select="oai:fixfield|oai:varfield"/>
  </record>
 </xsl:template>

 <xsl:template match="oai:fixfield">
   <xsl:variable name="tag">
     <xsl:call-template name="id2tag"/>
   </xsl:variable>
    
   <controlfield tag="{$tag}">
     <!--xsl:value-of select="substring(text(),2,string-length(text())-2)"/-->
          <xsl:value-of select="substring(text(),2,string-length(text())-2)"/>

   </controlfield>
 </xsl:template>


 <xsl:template match="oai:varfield">
   <xsl:variable name="tag">
     <xsl:call-template name="id2tag"/>
   </xsl:variable>

   <xsl:variable name="ind1">
     <xsl:call-template name="idBlankSpace">
       <xsl:with-param name="value" select="@i1"/>
     </xsl:call-template>
   </xsl:variable>

   <xsl:variable name="ind2">
     <xsl:call-template name="idBlankSpace">
       <xsl:with-param name="value" select="@i2"/>
     </xsl:call-template>
   </xsl:variable>

   <datafield tag="{$tag}" ind1="{$ind1}" ind2="{$ind2}">
     <xsl:apply-templates select="oai:subfield"/>
   </datafield>
 </xsl:template>

 <xsl:template match="oai:subfield">
   <subfield code="{@label}">
     <xsl:value-of select="text()"/>
   </subfield>
 </xsl:template>

 <xsl:template name="id2tag">
   <xsl:variable name="tag" select="@id"/>
   <xsl:choose>
     <xsl:when test="string-length($tag)=1">
       <xsl:text>00</xsl:text>
       <xsl:value-of select="$tag"/>
     </xsl:when>
     <xsl:when test="string-length($tag)=2">
       <xsl:text>0</xsl:text>
       <xsl:value-of select="$tag"/>
     </xsl:when>
     <xsl:when test="string-length($tag)=3">
       <xsl:value-of select="$tag"/>
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