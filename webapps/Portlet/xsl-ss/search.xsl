<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:portlet="http://www.uportal.org/extensions/portlet">

    <xsl:output method="html"/>

    <xsl:param name="mediaPath">images</xsl:param>
    <xsl:param name="author"></xsl:param>
    <xsl:param name="title"></xsl:param>
    <xsl:param name="locale">en_US</xsl:param>

    <xsl:template match="/data/databases">
    	<div class="portlet-section-header">
			<img title="Search" alt="Search" src="{$mediaPath}/search_big.png"/>
			Edit Search
		</div>
      <form name="JaferSearchForm" method="post" action="actionURL">
        <input type="hidden" name="action" value="search"/>

        <table width="100%" border="0" cellspacing="20" cellpadding="3">
          <tbody>
            <tr>
              <td align = "right" width="70">
	              <span class="portlet-form-field">
	              	<xsl:value-of select="$portlet.search.form.databases.label"/>
	              </span>
              </td>
              <td>
	                <select class="portlet-form-field" name="database">
    	              <xsl:apply-templates mode="data" />
        	        </select>
              </td>
            </tr>
            <tr>
              <td align = "right" width="70">
              	<span class="portlet-form-field">
              		<xsl:value-of select="$portlet.search.form.title.label"/>
              	</span>
              </td>
              <td>
              	<input class="portlet-form-input-field" type="text" name="title" value="{$title}" size="45" tabindex="1" />
              </td>
            </tr>
            <tr>
              <td align = "right" width="70">
              	<span class="portlet-form-field">
              		<xsl:value-of select="$portlet.search.form.author.label"/>
              	</span>
              </td>
              <td>
              	<input class="portlet-form-input-field" type="text" name="author" value="{$author}" size="45" tabindex="2" />
              </td>
            </tr>
            <tr>
              <td width="70"></td>
              <td align = "left">
              	<input class="portlet-form-button" type="image"
              		alt="Start Search" title="Start Search"
              		src="{$mediaPath}/search_go.png"
              		name="search" value="{$portlet.search.form.button.search}" tabindex="3" />
              </td>
            </tr>
          </tbody>
        </table>
      </form>
    </xsl:template>
    
    <xsl:template match="database" mode="data">
        <xsl:choose>
          <xsl:when test=". = $database">
			<option value="{.}" selected="selected"><xsl:value-of select="." /></option>
          </xsl:when>
          <xsl:otherwise>
			<option value="{.}"><xsl:value-of select="." /></option>
          </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

	<xsl:template match="@*|text()"/>

    <!-- -->
    
    <xsl:variable name="database"><xsl:value-of select="/data/search/database"/></xsl:variable>
    <xsl:variable name="author"><xsl:value-of select="/data/search/rpn/item[@name='author']"/></xsl:variable>
    <xsl:variable name="title"><xsl:value-of select="/data/search/rpn/item[@name='title']"/></xsl:variable>
    
    <!--LANGUAGE-->
    <xsl:variable name="portlet.search.title">Search Library</xsl:variable>
    <xsl:variable name="portlet.search.form.databases.label">Catalogue: </xsl:variable>
    <xsl:variable name="portlet.search.form.author.label">Author: </xsl:variable>
    <xsl:variable name="portlet.search.form.title.label">Title: </xsl:variable>
    <xsl:variable name="portlet.search.form.button.search">Submit</xsl:variable>
    <xsl:variable name="portlet.search.form.button.clear"> Clear </xsl:variable>
    <!-- -->
    <!-- -->
</xsl:stylesheet>
