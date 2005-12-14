<?xml version="1.0" encoding="utf-8"?>
<!--
	JAFER Toolkit Project.
	Copyright (C) 2002, JAFER Toolkit Project, Oxford University.

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
								xmlns:oai_marc="http://www.openarchives.org/OAI/oai_marc" exclude-result-prefixes="oai_marc">

	<xsl:output method="html" indent="yes" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

	<xsl:param name="totalResults"/><!-- total number of results from search -->
	<xsl:param name="maxHits"/><!-- maximum number of results to display per page -->

	<xsl:param name="pagetitle">Quick find on OLIS...</xsl:param>
	<xsl:param name="visualtitle">Quick find on <a href="http://www.lib.ox.ac.uk/olis/"><img src="http://www.lib.ox.ac.uk/olis/olis-medium.jpg" alt="OLIS" align="middle" /></a>...</xsl:param>


	<!-- only matches first element found, (eg: name) in the case of multiple occurrences -->

	<xsl:template match="root">
		<html>
			<head>
				<title><xsl:value-of select="$pagetitle" />: list of items</title>
				<link rel="stylesheet" type="text/css" href="http://www.lib.ox.ac.uk/style/default.asp" />
				<link rel="stylesheet" type="text/css" href="zget.css" />
			</head>
			<body>
				<table width="100%" align="center" cellpadding="0" cellspacing="0" summary="This table is used for visual layout.">
					<tr class="outer">
						<td colspan="5" class="outer"><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
					</tr>
					<tr>
						<td width="10" class="outer"><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
						<td width="10"><img src="corner-tl.gif" width="10" height="10" alt="" /></td>
						<td><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
						<td width="10"><img src="corner-tr.gif" width="10" height="10" alt="" /></td>
						<td width="10" class="outer"><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
					</tr>
					<tr>
						<td width="10" class="outer"><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
						<td><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
						<td><xsl:call-template name="main"></xsl:call-template></td>
						<td><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
						<td width="10" class="outer"><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
					</tr>
					<tr>
						<td width="10" class="outer"><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
						<td width="10"><img src="corner-bl.gif" width="10" height="10" alt="" /></td>
						<td><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
						<td width="10"><img src="corner-br.gif" width="10" height="10" alt="" /></td>
						<td width="10" class="outer"><img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /></td>
					</tr>
					<tr class="outer">
						<td colspan="5" class="outer"> <img src="http://www.lib.ox.ac.uk/images/blank.gif" width="10" height="10" alt="" /> </td>
					</tr>
				</table>

				<div class="footer">
					<hr class="hidden" />
					<p>
						Further information about the OLIS library catalogue is given on the <a href="http://www.lib.ox.ac.uk/olis/">OLIS homepage</a>.
					</p>
					<p>
						"<xsl:value-of select="$pagetitle" />" interface &#169; 2004, <a href="http://www.ox.ac.uk/">University of Oxford</a>.
						<br />
						<!-- Please acknowledge the JAFER project: -->
						Built using <i>ZServlet 2.0. &#169; <a href="http://www.jafer.org">JAFER Toolkit Project</a></i>.
					</p>
				</div>

			</body>
		</html>
	</xsl:template>




	<xsl:template match="oai_marc:oai_marc"><!-- linking uses DocID -->
		<xsl:variable name="docId"><xsl:value-of select="substring(oai_marc:fixfield[@id='001'],8,8)"/></xsl:variable>
		<li>
			<!-- title -->
			<a href="ZGet?docid={$docId}">
				<xsl:choose>
					<xsl:when test="oai_marc:varfield[@id='245']">
						<span class="title">
							<xsl:for-each select="oai_marc:varfield[@id='245']/oai_marc:subfield[@label='a' or @label='b' or @label='c' or @label='h']">
								<xsl:value-of select="."/>
								<xsl:if test="position() != last()"><xsl:text> </xsl:text></xsl:if>
							</xsl:for-each>
						</span>
					</xsl:when>
					<xsl:when test="oai_marc:varfield[@id='242' or @id='246']">
						<xsl:value-of select="."/>
					</xsl:when>
				</xsl:choose>
			</a>
			<!-- edition -->
			<xsl:if test="oai_marc:varfield[@id='250']/oai_marc:subfield[@label='a']">
				<xsl:text> </xsl:text>
				<xsl:value-of select="oai_marc:varfield[@id='250']/oai_marc:subfield[@label='a']" />
				<xsl:text> </xsl:text>
			</xsl:if>
			<!-- publication -->
			<xsl:if test="oai_marc:varfield[@id='260']">
				<!--
				<xsl:text> </xsl:text>
				-->
				<br />
				<xsl:for-each select="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='a' or @label='b' or @label='c']">
					<xsl:apply-templates select="." />
					<xsl:if test="position() != last()"><xsl:text> </xsl:text></xsl:if>
				</xsl:for-each>
			</xsl:if>
			<!-- author -->
			<!--
			<br />
			<xsl:value-of select="oai_marc:varfield[@id='100' or @id='110' or @id='111' or @id='130']"/>
			-->
		</li>
	</xsl:template>




	<xsl:template name="main">

				<h1 class="header"><xsl:copy-of select="$visualtitle" /></h1>

				<h2>
					<xsl:text>Found </xsl:text><xsl:value-of select= "$totalResults"/>
					<xsl:choose>
						<xsl:when test="$totalResults &lt;= $maxHits">
							<xsl:text> matches:</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text> matches, displaying the first </xsl:text><xsl:value-of select= "$maxHits"/><xsl:text> matches:</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</h2>

				<xsl:if test="$totalResults &gt; 0">
					<ol>
						<xsl:apply-templates/>
					</ol>
				</xsl:if>

				<xsl:if test="count(exception) > 0">
					<xsl:value-of select="count(exception)"/><xsl:text> record/s were errored, and are not displayed.</xsl:text>
					<p>
						<xsl:value-of select="exception/message"/><br /><!-- not for-each exception, may be too many -->
						<xsl:comment><xsl:value-of select="exception/text()"/></xsl:comment>
						<!-- swap <li> with <xsl:comment> to show/hide stack trace output in HTML source.	-->
						<xsl:for-each select="exception[position() = 1]/stackTrace">
							<xsl:comment>
								<xsl:value-of select="text()"/>
							</xsl:comment><br />
						</xsl:for-each>
						<!-- swap </li> with </xsl:comment> to show/hide stack trace output in HTML source. -->
					</p>
					<p><i>(Additional info may be available by viewing the associated HTML source code.)</i></p>
				</xsl:if>

				<xsl:if test="count(diagnostic) > 0">
					<xsl:value-of select="count(diagnostic)"/><xsl:text> diagnostic record/s were returned, and are not displayed.</xsl:text>
					<xsl:for-each select="diagnostic">
						<!-- swap <li> with <xsl:comment> to show/hide stack trace output in HTML source.	-->
						<xsl:comment>
							<xsl:value-of select="condition[@value]"/>
							<xsl:value-of select="condition"/>
							<xsl:value-of select="additionalInfo"/>
						</xsl:comment>
						<!-- swap </li> with </xsl:comment> to show/hide stack trace output in HTML source. -->
					</xsl:for-each>
					<p><i>(Additional info may be available by viewing the associated HTML source code.)</i></p>
				</xsl:if>
 </xsl:template>

</xsl:stylesheet>