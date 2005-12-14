<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

 <xsl:output method="text" encoding="UTF-8" indent="no"/>

 <xsl:param name="selectStatement"/>
 <xsl:param name="primaryKey"/>
 <xsl:param name="primaryTable"/>
 <xsl:param name="foreignKey"/>

 <xsl:template match="root">
   <xsl:choose>
     <xsl:when test="$selectStatement != '' ">
      <xsl:value-of select="$selectStatement"/>
     </xsl:when>
     <xsl:otherwise>
      <xsl:text>select </xsl:text><xsl:value-of select="$primaryKey"/>
     </xsl:otherwise>
   </xsl:choose>
   <xsl:text> from </xsl:text><xsl:value-of select="$primaryTable"/><xsl:text> where </xsl:text>
   <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="constraintModel">

   <xsl:variable name="semantic" select="constraint/semantic"/><!-- Use attribute -->
   <xsl:if test="$semantic = '' ">
    <xsl:message terminate="yes">Use attribute not set.</xsl:message>
   </xsl:if>

   <xsl:variable name="fullname" select="document('./config.xml')/config/attributes/semantic[@value = $semantic]/@column"/>
   <xsl:if test="$fullname ='' ">
    <xsl:message terminate="yes">
     <xsl:text>Use attribute not supported: </xsl:text>
     <xsl:value-of select="$semantic"/>
    </xsl:message>
   </xsl:if>

   <xsl:variable name="table" select="substring-before($fullname, '.')"/>
   <xsl:variable name="column" select="substring-after($fullname, '.')"/>
   <xsl:if test="$table = '' or $column ='' ">
    <xsl:message terminate="yes">Table and/or column name not found in configuration file.</xsl:message>
   </xsl:if>

   <xsl:choose>
     <xsl:when test="$table = $primaryTable">
       <xsl:value-of select="$column"/>
      </xsl:when>
     <xsl:otherwise>
       <xsl:text> exists (select * from </xsl:text><xsl:value-of select="$table"/>
       <xsl:text> where </xsl:text><xsl:value-of select="$column"/>
     </xsl:otherwise>
   </xsl:choose>

   <xsl:call-template name="model">
	 <xsl:with-param name="relation" select="constraint/relation" />
   	<xsl:with-param name="truncation" select="constraint/truncation" />
	<xsl:with-param name="model" select="model" />
   </xsl:call-template>

    <xsl:choose>
     <xsl:when test="$table != $primaryTable">
       <xsl:text> and </xsl:text>
       <xsl:value-of select="$foreignKey"/><xsl:text> = </xsl:text><xsl:value-of select="$primaryKey"/><xsl:text>)</xsl:text>
     </xsl:when>
   </xsl:choose>
 </xsl:template>

  <xsl:template name="model">
   <xsl:param name="relation"/>
   <xsl:param name="truncation"/>
   <xsl:param name="model"/>
   <xsl:choose>
    <xsl:when test="$truncation='1'"> <!-- right truncation -->
     <xsl:text> LIKE '</xsl:text><xsl:value-of select="$model"/><xsl:text>%' </xsl:text>
    </xsl:when>
    <xsl:when test="$truncation='2'"> <!-- left truncation -->
     <xsl:text> LIKE '%</xsl:text><xsl:value-of select="$model"/><xsl:text>' </xsl:text>
    </xsl:when>
    <xsl:when test="$truncation='3'"> <!-- left and right truncation -->
     <xsl:text> LIKE '%</xsl:text><xsl:value-of select="$model"/><xsl:text>%' </xsl:text>
    </xsl:when>

    <xsl:when test="$truncation='100' and $relation != '' "> <!-- do not truncate, relation attribute is set -->
     <xsl:value-of select="document('./config.xml')/config/attributes/relation[@value = $relation]/@SQL"/>
     <xsl:text>'</xsl:text><xsl:value-of select="$model"/><xsl:text>' </xsl:text>
    </xsl:when>

    <xsl:when test="$truncation='100' "> <!-- do not truncate, relation attribute not set. Use '=' -->
      <xsl:text> = '</xsl:text><xsl:value-of select="$model"/><xsl:text>' </xsl:text>
    </xsl:when>

    <xsl:when test="$relation != ''"> <!-- truncation not set, or it's not 1,2,3 or 100, and relation attribute is set. -->
     <xsl:value-of select="document('./config.xml')/config/attributes/relation[@value = $relation]/@SQL"/>
     <xsl:text>'</xsl:text><xsl:value-of select="$model"/><xsl:text>' </xsl:text>
    </xsl:when>

    <xsl:otherwise><!-- truncation not set, or it's not 1,2,3 or 100, and relation attribute is not set , use right truncation -->
     <xsl:text> LIKE '</xsl:text><xsl:value-of select="$model"/><xsl:text>%' </xsl:text>
    </xsl:otherwise>
   </xsl:choose>
 </xsl:template>


 <xsl:template match="and | or">
   <xsl:variable name="term1"><xsl:apply-templates select="*[position()=1]"/></xsl:variable>
   <xsl:variable name="term2"><xsl:apply-templates select="*[position()=2]"/></xsl:variable>
   <xsl:choose>
     <xsl:when test="starts-with($term1, 'select')">
       <xsl:text> exists (</xsl:text><xsl:value-of select="$term1"/><xsl:text>) </xsl:text>
     </xsl:when>
     <xsl:otherwise>
       <xsl:text> (</xsl:text><xsl:value-of select="$term1"/><xsl:text>) </xsl:text>
     </xsl:otherwise>
   </xsl:choose>
   <xsl:value-of select="name()"/>
   <xsl:choose>
     <xsl:when test="starts-with($term2, 'select')">
       <xsl:text> exists (</xsl:text><xsl:value-of select="$term2"/><xsl:text>) </xsl:text>
     </xsl:when>
     <xsl:otherwise>
       <xsl:text> (</xsl:text><xsl:value-of select="$term2"/><xsl:text>) </xsl:text>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

 <xsl:template match="not">
   <xsl:variable name="term1"><xsl:apply-templates select="*[position()=1]"/></xsl:variable>
   <xsl:choose>
     <xsl:when test="starts-with($term1, 'select')">
       <xsl:text> not exists (</xsl:text><xsl:value-of select="$term1"/><xsl:text>) </xsl:text>
     </xsl:when>
     <xsl:otherwise>
       <xsl:text> not (</xsl:text><xsl:value-of select="$term1"/><xsl:text>) </xsl:text>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

</xsl:stylesheet>