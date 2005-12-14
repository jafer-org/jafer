<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" encoding="UTF-8" indent="yes"/>

<xsl:param name="style"/>

  <xsl:template match="records">
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
        <xsl:when test="$style = 'item'">
          <xsl:call-template name="item">
          	<xsl:with-param name="dc" select="jaferRecord/dc"/>
          </xsl:call-template>
        </xsl:when>
      </xsl:choose>
  </xsl:template>

  <xsl:template name="style1">
       <table cellpadding='5' cellspacing='0'>
        <tr><td colspan="5"><hr/></td></tr>
        <tr><th>select item</th><th>view item</th><th>Author</th><th>Title</th><th>Publisher</th></tr>
        <xsl:for-each select="jaferRecord">
        <xsl:variable name="recordId" select="@number"/>
          <tr><td width="30"><input type="checkbox" name="recordId" value="{$recordId}"></input></td>
              <td width="30"><a><xsl:attribute name="href">bs_template_viewItem.html?recordId=<xsl:value-of select="$recordId"/></xsl:attribute>
                     <img src="bs_template_viewXML.gif" border="0" alt="view details"></img></a></td>
              <td><xsl:value-of select="dc/creator"/></td>
              <td><xsl:value-of select="dc/title"/></td>
              <td><xsl:value-of select="dc/publisher"/></td></tr>
        </xsl:for-each>
        <tr><td colspan="5"><hr/></td></tr>
       </table>
  </xsl:template>

  <xsl:template name="style2">
       <table cellpadding='5' cellspacing='0'>
        <tr><th>select item</th><th>view item</th><th>Author:</th><th>Title:</th><th>ISBN/ISSN:</th></tr>
	<xsl:for-each select="jaferRecord">
        <xsl:variable name="recordId" select="@number"/>
        <tr><td width="30"><input type="checkbox" name="recordId" value="{$recordId}"></input></td>
            <td width="30"><a><xsl:attribute name="href">bs_template_viewItem.html?recordId=<xsl:value-of select="$recordId"/></xsl:attribute>
                     <img src="bs_template_viewXML.gif" border="0" alt="view details"></img></a></td>
              <td><xsl:value-of select="dc/creator"/></td>
              <xsl:variable name="ref" select="dc/title"/>
              <td><a href="bs_template_OpenURL.html"><xsl:value-of select="dc/title"/></a></td>
              <td><xsl:value-of select="dc/identifier"/></td></tr>
        </xsl:for-each>
	<tr><td colspan="5"><hr/></td></tr>
       </table>
  </xsl:template>

  <xsl:template name="style3">

        <table>
         <xsl:for-each select="jaferRecord">
	 <xsl:variable name="recordId" select="@number"/>
           <tr><td bgcolor='#aaaadd'><i>Record: <xsl:number count='jaferRecord'/></i></td><td></td></tr>
             <tr><td><b>Author:</b></td><td><xsl:value-of select="dc/creator"/></td></tr>
             <tr><td><b>Title:</b></td><td><xsl:value-of select="dc/title"/></td></tr>
             <tr><td><b>Publisher:</b></td><td><xsl:value-of select="dc/publisher"/></td></tr>
	     <tr><td><b>Date:</b></td><td><xsl:value-of select="dc/date"/></td></tr>
	     <tr><td><b>Description:</b></td><td><xsl:value-of select="dc/description"/></td></tr>
	     <tr><td><b>ISBN/ISSN:</b></td><td><xsl:value-of select="dc/identifier"/></td></tr>
	     <xsl:for-each select="dc/subject">
	      <tr><td><b>Subject:</b></td><td><xsl:value-of select="."/></td></tr>
	     </xsl:for-each>
	    <tr><td colspan="5"><hr/></td></tr>
           </xsl:for-each>
       </table>
  </xsl:template>

  <xsl:template name="item">

  	<xsl:param name="dc"/>

       <table cellspacing="10">
        <tr><td colspan="4"><hr/></td></tr>
            <xsl:if test="$dc/creator">
              <tr>
                <td>
                  <b>Author</b>
                </td>
                <td colspan="3"><xsl:value-of select="$dc/creator"/></td>
              </tr>
            </xsl:if>

            <xsl:if test="$dc/title">
              <tr>
                <td>
                  <b>Title</b>
                </td>
                <td colspan="3"><xsl:value-of select="$dc/title"/></td>
              </tr>
            </xsl:if>

            <xsl:if test="$dc/publisher">
              <tr>
                <td>
                  <b>Publisher</b>
                </td>
                <td colspan="3">
                <xsl:value-of select="$dc/publisher"/></td>
              </tr>
            </xsl:if>

            <xsl:if test="$dc/date">
              <tr>
	        <td>
	          <b>Date</b>
	        </td>
	        <td colspan="3">
	       <xsl:value-of select="$dc/date"/></td>
              </tr>
            </xsl:if>

	    <xsl:if test="$dc/description">
	      <tr>
		<td>
		  <b>Description</b>
		</td>
		<td colspan="3">
	       <xsl:value-of select="$dc/description"/></td>
	      </tr>
            </xsl:if>

            <xsl:if test="$dc/identifier">
              <xsl:choose>
                <xsl:when test="$dc/dentifier[@type='issn']">
                  <tr>
                    <td>
                      <b>ISSN</b>
                    </td>
                    <td colspan="3"><xsl:value-of select="$dc/identifier"/></td>
                  </tr>
                </xsl:when>
                <xsl:when test="$dc/identifier[@type='isbn']">
                  <tr>
                    <td>
                      <b>ISBN</b>
                    </td>
                    <td colspan="3"><xsl:value-of select="$dc/identifier"/></td>
                  </tr>
                </xsl:when>
              </xsl:choose>
            </xsl:if>

            <xsl:for-each select="$dc/subject" >
              <tr>
                <td>
                  <b>Subject</b>
                </td>
                <td colspan="3"><xsl:value-of select="."/></td>
              </tr>
            </xsl:for-each>

<!--            <xsl:if test="mods:extension">
             <tr>
               <td align="left"><b>Library Holdings</b></td>
               <td width="150" align="left"><b>location</b></td>
               <td width="150" align="left"><b>call number</b></td>
               <td width="150" align="left"><b>status</b></td>
             </tr>
              <xsl:for-each select="mods:extension">
                <tr><td> </td>
                  <td align="left">
                    <xsl:value-of select="mods:shelvingLocation"/>
                  </td>
                  <td align="left">
                    <xsl:value-of select="mods:callNumber"/>
                  </td>
                  <td align="left">
                    <xsl:value-of select="mods:circulationStatus"/>
                  </td>
                </tr>
              </xsl:for-each>
            </xsl:if>

            <xsl:if test="mods:identifier/@mods:type='uri'">
            <xsl:for-each select="mods:identifier[@mods:type='uri']">
              <tr>
                <td>
                  <b>Online</b>
                </td>
                <td colspan="3">
                <xsl:value-of select="./mods:notes"/>
                  <a><xsl:attribute name="href">
                  <xsl:value-of select="node()"/>
                </xsl:attribute><xsl:value-of select="node()"/></a></td>
              </tr>
              </xsl:for-each>
            </xsl:if> -->
        <tr><td colspan="5"><hr/></td></tr>
        </table>
         </xsl:template>
</xsl:stylesheet>