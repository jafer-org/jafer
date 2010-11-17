<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:portlet="http://www.uportal.org/extensions/portlet" xmlns:mods="http://www.loc.gov/mods/">

    <xsl:output method="html" omit-xml-declaration="yes" doctype-public="" doctype-system=""/>

    <xsl:param name="mediaPath">images</xsl:param>
    <xsl:param name="author"></xsl:param>
    <xsl:param name="title"></xsl:param>
    <xsl:param name="locale">en_US</xsl:param>

	<xsl:template match="/">
		<!-- header -->
		<div class="portlet-font"
			style="border-bottom: 1px dashed; 
			font-size:smaller">
	        <a style="text-decoration: underline; color: red;" href="renderURL">
    			<portlet:param name="action" value="search"/>
				<img border="0" title="New Search" alt="New Search" src="{$mediaPath}/search.png" align="top"/>
	        </a>
           	<a href="renderURL">
		        <portlet:param name="action" value="history" />
				<img border="0" title="Session History" alt="Session History" src="{$mediaPath}/history.png" align="top"/>
	        </a>
	        <xsl:if test="/results/search/@id">
				<a href="renderURL">
    	    		<portlet:param name="action" value="start" />
			        <portlet:param name="history" value="{/results/search/@id}" />
			          <img title="Edit" alt="Edit"
			          	border="0" src="{$mediaPath}/edit.png" align="top"/>
				</a>
			</xsl:if>
	        <xsl:if test="/record/@id">
		        <a href="renderURL">
		          <portlet:param name="action" value="list"/>
		          <portlet:param name="id" value="{/record/@id}" />
		          <img title="Return to Results" alt="Return to Results"
		          	border="0" src="{$mediaPath}/results.png" align="top"/>
		        </a>
			</xsl:if>
		</div>

		<xsl:apply-templates/>

		<!-- footer -->
	</xsl:template>

</xsl:stylesheet>
