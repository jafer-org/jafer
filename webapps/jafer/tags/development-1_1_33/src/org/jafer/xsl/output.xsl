<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:mods="http://www.loc.gov/mods/">

<xsl:output method="html" encoding="UTF-8" indent="yes"/>

<xsl:param name="style"/>

  <xsl:template match="records">
    <html>
      <title>JAFER Toolkit Project: Reading List</title>
      <body bgcolor="#FFFFFF">
      <xsl:choose>
        <xsl:when test="$style = 'style1'">
          <xsl:call-template name="style1"/>
        </xsl:when>
        <xsl:when test="$style = 'style2'">
          <xsl:call-template name="style2"/>
        </xsl:when>
        <xsl:when test="$style = 'style3'">
          <xsl:call-template name="style3"/>
        </xsl:when>
        <xsl:when test="$style = 'style4'">
          <xsl:call-template name="style4"/>
        </xsl:when>
      </xsl:choose>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="style1">
  <h1>Simple table:</h1>
       <table  border='1' cellpadding='5' cellspacing='0'>
        <tr><th>Author:</th><th>Title:</th><th>Doc ID:</th></tr>
        <xsl:for-each select="mods:mods">
          <tr><td><xsl:value-of select="mods:name[@mods:role='creator']/text()"/></td>
              <td><xsl:value-of select="mods:title"/></td>
              <td><xsl:value-of select="mods:recordInfo/mods:recordIdentifier"/></td></tr>
        </xsl:for-each>
        </table>
  </xsl:template>

  <xsl:template name="style2">
  <h1>Change data and display properties:</h1>
       <table  border='1' cellpadding='5' cellspacing='0'>
        <tr><th>Title:</th><th>Author:</th><th>Publisher:</th></tr>
        <xsl:for-each select="mods:mods">
          <tr><td><xsl:value-of select="mods:title"/></td>
              <td><xsl:value-of select="mods:name[@mods:role='creator']/text()"/></td>
              <td><xsl:value-of select="mods:publication/mods:publisher"/></td></tr>
        </xsl:for-each>
       </table>
  </xsl:template>

  <xsl:template name="style3">
  <h1>With ZGet link:</h1>
       <table  border='1' cellpadding='5' cellspacing='0'>
        <tr><th>Author:</th><th>Title:</th><th>Publisher:</th></tr>
        <xsl:for-each select="mods:mods">
          <tr><td><xsl:value-of select="mods:name[@mods:role='creator']/text()"/></td>
              <xsl:variable name="ref"><xsl:value-of select="mods:recordInfo/mods:recordIdentifier"/></xsl:variable>
              <td><a href="./ZGet?docID={$ref}"><xsl:value-of select="mods:title"/></a></td>
              <td><xsl:value-of select="mods:publication/mods:publisher"/></td></tr>
        </xsl:for-each>
       </table>
  </xsl:template>

  <xsl:template name="style4">
  <h1>Multiple tables: </h1>
        <table  border='1'>
         <xsl:for-each select="mods:mods">
           <tr><td bgcolor='#aaaadd'><i>Record: <xsl:number count='mods:mods' from='1'/></i></td><td bgcolor='#aaaadd'></td></tr>
             <tr><td><b>Title:</b></td><td><xsl:value-of select="mods:title"/></td></tr>
             <tr><td><b>Author:</b></td><td><xsl:value-of select="mods:name[@mods:role='creator']/text()"/></td></tr>
             <tr><td><b>Publisher:</b></td><td><xsl:value-of select="mods:publication/mods:publisher"/></td></tr>
           </xsl:for-each>
       </table>
  </xsl:template>
</xsl:stylesheet>


