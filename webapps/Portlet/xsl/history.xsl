<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:portlet="http://www.uportal.org/extensions/portlet" xmlns:mods="http://www.loc.gov/mods/">

	<xsl:include href="wrap.xsl"/>

    <xsl:output method="html" omit-xml-declaration="yes" doctype-public="" doctype-system=""/>

    <xsl:param name="mediaPath">images</xsl:param>
    <xsl:param name="author"></xsl:param>
    <xsl:param name="title"></xsl:param>
    <xsl:param name="locale">en_US</xsl:param>

    <xsl:template match="/searches">
    	<div class="portlet-section-header">
			<img title="Session History" alt="Session History" src="{$mediaPath}/history_big.png"/>
			Previous Searches
		</div>

        <xsl:choose>
   	    	<xsl:when test="@total = 0">
				<div class="portlet-msg-info">
	            	You have no previous searches in this session.
				</div>
			</xsl:when>
			<xsl:when test="@total = 1">
				<div class="portlet-msg-info">
					You have <xsl:value-of select="@total"/> previous search in this session.
				</div>
			</xsl:when>
			<xsl:when test="@total > 1">
				<div class="portlet-msg-info">
					You have <xsl:value-of select="@total"/> previous searches in this session.
					<br/>
					Displaying <xsl:value-of select="@start"/> - <xsl:value-of select="@end"/>
				</div>
			</xsl:when>
        </xsl:choose>

					<xsl:choose>
				        <xsl:when test="@start &gt; 10">
					        <a href="renderURL">
					          <portlet:param name="action" value="history"/>
			    		      <portlet:param name="id" value="{@start - 10}" />
					          <img title="Previous Searches" alt="Previous Searches"
					          	border="0" src="{$mediaPath}/previous.png" align="top"/>
					        </a>
				        </xsl:when>
				        <xsl:otherwise>
				          <img title="No Previous Searches" alt="No Previous Searches"
				          	border="0" src="{$mediaPath}/previous_inactive.png" align="top"/>
				        </xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
				        <xsl:when test="@end &lt; @total">
					        <a href="renderURL">
					          <portlet:param name="action" value="history"/>
			        		  <portlet:param name="id" value="{@end + 1}" />
					          <img title="Next Searches" alt="Next Searches"
					          	border="0" src="{$mediaPath}/next.png" align="top"/>
			    		    </a>
				        </xsl:when>
				        <xsl:otherwise>
				          <img title="No Next Searches" alt="No Next Searches"
				          	border="0" src="{$mediaPath}/next_inactive.png" align="top"/>
				        </xsl:otherwise>
					</xsl:choose>
            	<xsl:apply-templates select="search" />

    </xsl:template>

    <xsl:template match="search">
    	<div class="portlet-section-body">
	        <span style="font-style:italic"><xsl:value-of select="@id" />: </span>
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
			<span style="font-size:70%; font-style:italic;">
			<br/>
    		<xsl:value-of select="@date"/>
			<a href="actionURL">
        		<portlet:param name="action" value="search" />
	        	<portlet:param name="history" value="{@id}" />
		          <img title="Re-run" alt="Re-run"
		          	border="0" src="{$mediaPath}/re-run.png" align="top"/>
			</a>
			<a href="renderURL">
        		<portlet:param name="action" value="start" />
		        <portlet:param name="history" value="{@id}" />
		          <img title="Edit" alt="Edit"
		          	border="0" src="{$mediaPath}/edit.png" align="top"/>
			</a>
			</span>
		</div>
		
	</xsl:template>
</xsl:stylesheet>
