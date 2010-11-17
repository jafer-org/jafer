<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:portlet="http://www.uportal.org/extensions/portlet" xmlns:mods="http://www.loc.gov/mods/">

    <xsl:output method="html"/>

    <xsl:param name="mediaPath">images</xsl:param>
    <xsl:param name="author"></xsl:param>
    <xsl:param name="title"></xsl:param>
    <xsl:param name="locale">en_US</xsl:param>

    <xsl:template match="/record">
    	<div class="portlet-section-header">
			<img title="Record Details" alt="Record Details" src="{$mediaPath}/record.png"/>
			Record Details
		</div>

				<div class="portlet-msg-info">
					Your search has found <xsl:value-of select="@total"/> records
					<br/>
					Displaying record <xsl:value-of select="@id"/>
					<br/>
			        <a href="renderURL">
			          <portlet:param name="action" value="list"/>
			          <portlet:param name="id" value="{@id}" />
			          <img title="Return to Results" alt="Return to Results"
			          	border="0" src="{$mediaPath}/results.png" align="top"/>
			        </a>
					<xsl:choose>
				        <xsl:when test="@id &gt; 1">
					        <a href="renderURL">
					          <portlet:param name="action" value="item"/>
					          <portlet:param name="id" value="{@id - 1}" />
					          <img title="Previous Record" alt="Previous Record"
			    		      	border="0" src="{$mediaPath}/previous.png" align="top"/>
			        		</a>
			        	</xsl:when>
				        <xsl:otherwise>
				          <img title="No Previous Record" alt="No Previous Record"
				          	border="0" src="{$mediaPath}/previous_inactive.png" align="top"/>
				        </xsl:otherwise>
			        </xsl:choose>
					<xsl:choose>
				        <xsl:when test="@id &lt; @total">
					        <a href="renderURL">
					          <portlet:param name="action" value="item"/>
					          <portlet:param name="id" value="{@id + 1}" />
					          <img title="Next Record" alt="Next Record"
			    		      	border="0" src="{$mediaPath}/next.png" align="top"/>
			        		</a>
			        	</xsl:when>
				        <xsl:otherwise>
				          <img title="No Next Record" alt="No Next Record"
				          	border="0" src="{$mediaPath}/next_inactive.png" align="top"/>
				        </xsl:otherwise>
			        </xsl:choose>
		       </div>

				<div class="portlet-section-body">
    		        <xsl:apply-templates select="//mods:title" />
    		        <xsl:apply-templates select="//mods:name" />
    		        <xsl:apply-templates select="//mods:publisher" />
    		        <xsl:apply-templates select="//mods:extent" />
    		        <xsl:apply-templates select="//mods:topic" />
    		        <xsl:apply-templates select="//mods:identifier" />
	            </div>
    </xsl:template>

    <xsl:template match="mods:name">
      <b>Author: </b><xsl:value-of select="."/><br/>
    </xsl:template>
    <xsl:template match="mods:title">
      <b>Title: </b><xsl:value-of select="."/><br/>
    </xsl:template>
    <xsl:template match="mods:publisher">
      <b>Publisher: </b><xsl:value-of select="."/><br/>
    </xsl:template>
    <xsl:template match="mods:extent">
      <b>Description: </b><xsl:value-of select="."/><br/>
    </xsl:template>
    <xsl:template match="mods:identifier">
      <xsl:choose>
        <xsl:when test="@type = 'isbn'">
          <b>ISBN: </b><xsl:value-of select="."/>
          <span style="font-size:80%;">
          <a href="http://openurl.ac.uk/?isbn={.}">Find the item via OpenURL</a><br/>
          </span>
			<!-- 
            <xsl:choose>
              <xsl:when test="/record/@database = 'INNOPAC'">
                <a href="http://library.hull.ac.uk/search/i{.}" target="_blank">Direct link to catalogue</a><br/>
              </xsl:when>
              <xsl:when test="/record/@database = 'MAIN*BIBMAST'">
                <a href="http://library.ox.ac.uk/find?isbn={.}" target="_blank">Direct link to catalogue</a><br/>
              </xsl:when>
            </xsl:choose>
			-->
        </xsl:when>
        <xsl:when test="@type = 'issn'">
          <b>ISSN: </b><xsl:value-of select="."/>
          <span style="font-size:80%;">
          <a href="http://openurl.ac.uk/?issn={.}">Find the item via OpenURL</a><br/>
          </span>
			<!-- 
            <xsl:choose>
              <xsl:when test="/record/@database = 'INNOPAC'">
                <a href="http://library.hull.ac.uk/search/i{.}" target="_blank">Direct link to catalogue</a><br/>
              </xsl:when>
              <xsl:when test="/record/@database = 'MAIN*BIBMAST'">
                <a href="http://library.ox.ac.uk/find?issn={.}" target="_blank">Direct link to catalogue</a><br/>
              </xsl:when>
            </xsl:choose>
             -->
        </xsl:when>
      </xsl:choose>
    </xsl:template>
    <xsl:template match="mods:topic">
      <b>Subject: </b><xsl:value-of select="."/><br/>
    </xsl:template>
    <xsl:template match="text()">
      <xsl:value-of select="." /><br/>
    </xsl:template>
    <xsl:template match="*">
      <xsl:apply-templates select="*" />
    </xsl:template>
</xsl:stylesheet>
