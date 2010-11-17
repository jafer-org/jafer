<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:portlet="http://www.uportal.org/extensions/portlet" xmlns:mods="http://www.loc.gov/mods/">

    <xsl:output method="html"/>

    <xsl:param name="mediaPath">images</xsl:param>
    <xsl:param name="author"></xsl:param>
    <xsl:param name="title"></xsl:param>
    <xsl:param name="locale">en_US</xsl:param>

    <xsl:template match="/help">
    	<div class="portlet-section-header">
			<img title="Help" alt="Help" src="{$mediaPath}/help.png"/>
			Help
		</div>
		<div style="float:left;">
			<a href="actionURL">
        		<portlet:param name="action" value="help" />
        		<portlet:param name="page" value="search" />
        		search
			</a><br/>
			<a href="actionURL">
        		<portlet:param name="action" value="help" />
        		<portlet:param name="page" value="list" />
        		results listing
			</a><br/>
			<a href="actionURL">
        		<portlet:param name="action" value="help" />
        		<portlet:param name="page" value="item" />
        		record item
			</a><br/>
			<a href="actionURL">
        		<portlet:param name="action" value="help" />
        		<portlet:param name="page" value="history" />
        		history
			</a><br/>
		</div>
		<div class="portlet-section-body">
			General help goes here...
			<br/>
			<xsl:apply-templates/>
			
		
		</div>
	</xsl:template>
	
	<xsl:template match="search">
			Contextual Search help goes here...
	</xsl:template>

	<xsl:template match="list">
			Contextual Results listing help goes here...
	</xsl:template>

	<xsl:template match="item">
			Contextual Record item help goes here...
	</xsl:template>

	<xsl:template match="history">
			Contextual History help goes here...
	</xsl:template>
	
</xsl:stylesheet>
