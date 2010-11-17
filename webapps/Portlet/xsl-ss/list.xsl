<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:portlet="http://www.uportal.org/extensions/portlet" xmlns:mods="http://www.loc.gov/mods/">

    <xsl:output method="html" omit-xml-declaration="yes" doctype-public="" doctype-system=""/>

    <xsl:param name="mediaPath">images</xsl:param>
    <xsl:param name="author"></xsl:param>
    <xsl:param name="title"></xsl:param>
    <xsl:param name="locale">en_US</xsl:param>

	<xsl:template match="/error">
	<div class="portlet-msg-error">
		<xsl:value-of select="."/>
		Please edit it and try again.
	</div>
	</xsl:template>

	<xsl:template match="/results">
    	<div class="portlet-section-header">
			<xsl:apply-templates select="search"/>
		</div>
		<xsl:apply-templates select="records"/>
	</xsl:template>

    <xsl:template match="records">

        <xsl:choose>
   	    	<xsl:when test="@total = 0">
				<div class="portlet-msg-info">
	            	Your search has not found any matching records.
	            	<br/>
	            	Please use 'edit' to setup your search.
				</div>
			</xsl:when>
			<xsl:when test="@total = 1">
				<div class="portlet-msg-info">
					Your search has found <xsl:value-of select="@total"/> record.
				</div>
	            <xsl:apply-templates select="record" />
			</xsl:when>
			<xsl:when test="@total > 1">
				<div class="portlet-msg-info">
					Your search has found <xsl:value-of select="@total"/> records
					<br/>
					Displaying <xsl:value-of select="@start"/> - <xsl:value-of select="@end"/>
					<br/>
					<xsl:choose>
				        <xsl:when test="@start &gt; 10">
					        <a href="renderURL">
					          <portlet:param name="action" value="list"/>
					          <portlet:param name="id" value="{@start - 10}" />
					          <img title="Previous Results" alt="Previous Results"
			    		      	border="0" src="{$mediaPath}/previous.png" align="top"/>
			        		</a>
			        	</xsl:when>
				        <xsl:otherwise>
				          <img title="No Previous Results" alt="No Previous Results"
				          	border="0" src="{$mediaPath}/previous_inactive.png" align="top"/>
				        </xsl:otherwise>
			        </xsl:choose>
					<xsl:choose>
				        <xsl:when test="@end &lt; @total">
					        <a href="renderURL">
					          <portlet:param name="action" value="list"/>
					          <portlet:param name="id" value="{@end + 1}" />
					          <img title="Next Results" alt="Next Results"
			    		      	border="0" src="{$mediaPath}/next.png" align="top"/>
			        		</a>
			        	</xsl:when>
				        <xsl:otherwise>
				          <img title="No Next Results" alt="No Next Results"
				          	border="0" src="{$mediaPath}/next_inactive.png" align="top"/>
				        </xsl:otherwise>
			        </xsl:choose>
			      </div>

	              <xsl:apply-templates select="record" />
          </xsl:when>
        </xsl:choose>
    </xsl:template>

	<xsl:template match="search">
			<a href="actionURL">
        		<portlet:param name="action" value="search" />
	        	<portlet:param name="history" value="{@id}" />
		          <img title="Refresh" alt="Refresh"
		          	border="0" src="{$mediaPath}/re-run.png" align="top"/>
			</a>
    		<xsl:if test="rpn/item[@name='title']">
				<span style="font-size:100%">Title = <xsl:value-of select="rpn/item[@name='title']"/></span>
	    	</xsl:if>
    		<xsl:if test="rpn/item[@name='title']">
	    		<xsl:if test="rpn/item[@name='author']">
	    		<span style="font-size:100%">, </span>
	    		</xsl:if>
    		</xsl:if>
    		<xsl:if test="rpn/item[@name='author']">
				<span style="font-size:100%">Author = <xsl:value-of select="rpn/item[@name='author']"/></span>
	    	</xsl:if>
			<span style="font-size:80%">
			[Catalogue:
			<xsl:for-each select="database">
				<xsl:value-of select="."/>
			</xsl:for-each>
			]
			</span>
	</xsl:template>

    <xsl:template match="record">
      <a href="renderURL">
        <portlet:param name="action" value="item" />
        <portlet:param name="id" value="{@id}" />
        <img border="0" title="Show result" alt="Show result" src="{$mediaPath}/record.png" align="TOP"/>
        <xsl:value-of select="mods:mods/mods:title"/>
      </a>
      <xsl:value-of select="mods:mods/mods:name"/>
      <br />
    </xsl:template>
    
</xsl:stylesheet>
