<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:portlet="http://www.uportal.org/extensions/portlet">
    <xsl:output method="html"/>

    <xsl:param name="mediaPath">images</xsl:param>
    <xsl:param name="author"></xsl:param>
    <xsl:param name="title"></xsl:param>
    <xsl:param name="locale">en_US</xsl:param>

    <xsl:template match="/databases">
      <form name="JaferSearchForm" method="post" action="actionURL">
        <input type="hidden" name="action" value="search"/>
        <table width="100%" border="0" cellspacing="20">
          <tbody>
            <tr>
              <td width="45"></td>
              <td align = "right" width="70"><img alt="" src="{$mediaPath}/find.gif" align="TOP"/></td>
              <td align = "left"><span class="portlet-font"><xsl:value-of select="$portlet.search.title"/></span></td>
            </tr>
            <tr>
              <td></td>
              <td colspan="3"><hr/></td>
            </tr>
            <tr>
              <td width="45"></td>
              <td align = "right" width="70"><span class="portlet-form-label"><xsl:value-of select="$portlet.search.form.databases.label"/></span></td>
              <td colspan="2">
                <select name="database">
                  <xsl:apply-templates select="database" />
                </select>
              </td>
            </tr>
            <tr>
              <td width="45"></td>
              <td align = "right" width="70"><span class="portlet-form-label"><xsl:value-of select="$portlet.search.form.title.label"/></span></td>
              <td colspan="2"><input class="portlet-form-input-field" type="text" name="title" value="{$title}" size="45" tabindex="1" /></td>
            </tr>
            <tr>
              <td width="45"></td>
              <td align = "right" width="70"><span class="portlet-form-label"><xsl:value-of select="$portlet.search.form.author.label"/></span></td>
              <td colspan="2"><input class="portlet-form-input-field" type="text" name="author" value="{$author}" size="45" tabindex="2" /></td>
            </tr>
            <tr>
              <td width="45"></td>
              <td width="70"></td>
              <td align = "left"><input class="portlet-form-button" type="submit" name="search" value="{$portlet.search.form.button.search}" tabindex="3" /></td>
              <td align = "right"><input class="portlet-form-button" type="reset" name="reset" value=" {$portlet.search.form.button.clear} " tabindex="4" /></td>
            </tr>
            <tr>
              <td></td>
              <td colspan="3"><hr/></td>
            </tr>
          </tbody>
        </table>
      </form>
    </xsl:template>
    <xsl:template match="database">
      <option value="{.}"><xsl:value-of select="." /></option>
    </xsl:template>
    <!-- -->
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
