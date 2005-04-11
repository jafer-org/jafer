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



	<xsl:param name="pagetitle">Quick find on OLIS...</xsl:param>
	<xsl:param name="visualtitle">Quick find on <a href="http://www.lib.ox.ac.uk/olis/"><img src="http://www.lib.ox.ac.uk/olis/olis-medium.jpg" alt="OLIS" align="middle" /></a>...</xsl:param>


	<xsl:template match="root">
		<html lang="en">
			<head>
				<title><xsl:value-of select="$pagetitle" />: single item</title>
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

	<xsl:template name="main">

				<h1 class="header"><xsl:copy-of select="$visualtitle" /></h1>

				<xsl:apply-templates/>

	</xsl:template>

	<xsl:template match="oai_marc:oai_marc">

		<h2>Bibliographic record</h2>

	<table width="100%" cellspacing="4" class="bib">

		<!-- Title -->
		<xsl:if test="oai_marc:varfield[@id='245']">
			<tr>
				<th width="15%">Title</th>
				<td>
					<xsl:variable name="nodes" select="oai_marc:varfield[@id='245']/oai_marc:subfield[@label='a' or @label='b' or @label='c' or @label='f' or @label='g' or @label='h' or @label='k' or @label='n' or @label='p' or @label='s']" />
					<xsl:call-template name="separate-with-space"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
				</td>
			</tr>
		</xsl:if>

		<!-- Author (inc. Corporate Name and Meeting Name) -->
		<xsl:if test="oai_marc:varfield[@id='100']">
			<tr>
				<th width="15%">Author</th>
				<td>
					<xsl:variable name="nodes" select="oai_marc:varfield[@id='100']/oai_marc:subfield"/>
					<xsl:call-template name="separate-with-space"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="oai_marc:varfield[@id='110']">
			<tr>
				<th width="15%">Author<br />(Corporate Name)</th>
				<td>
					<xsl:variable name="nodes" select="oai_marc:varfield[@id='110']/oai_marc:subfield"/>
					<xsl:call-template name="separate-with-space"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
				</td>
			</tr>
		</xsl:if>
		<xsl:if test="oai_marc:varfield[@id='111']">
			<tr>
				<th width="15%">Author<br />(Meeting Name)</th>
				<td>
					<xsl:variable name="nodes" select="oai_marc:varfield[@id='111']/oai_marc:subfield"/>
					<xsl:call-template name="separate-with-space"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
				</td>
			</tr>
		</xsl:if>

		<xsl:if test="oai_marc:varfield[@id='700']">
			<tr>
				<th width="15%">Other Names</th>
				<td>
					<xsl:variable name="nodes" select="oai_marc:varfield[@id='700']"/>
					<xsl:for-each select="$nodes">
						<xsl:call-template name="separate-with-space"><xsl:with-param name="nodes" select="./oai_marc:subfield" /></xsl:call-template>
						<xsl:if test="position() != last()"><br /></xsl:if>
					</xsl:for-each>
				</td>
			</tr>
		</xsl:if>

		<!-- Publisher -->
		<xsl:if test="oai_marc:varfield[@id='260']">
			<tr>
				<th width="15%">Publisher</th>
				<td>
					<xsl:variable name="nodes" select="oai_marc:varfield[@id='260']/oai_marc:subfield" />
					<xsl:call-template name="separate-with-space"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
				</td>
			</tr>
		</xsl:if>

		<!-- Edition -->
		<xsl:if test="oai_marc:varfield[@id='250']">
			<tr>
				<th width="15%">Edition</th>
				<td>
					<xsl:variable name="nodes" select="oai_marc:varfield[@id='250']/oai_marc:subfield" />
					<xsl:call-template name="separate-with-space"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
				</td>
			</tr>
		</xsl:if>

		<!-- Description -->
		<xsl:if test="oai_marc:varfield[@id='256']/oai_marc:subfield[@label='a']">
			<tr>
				<th width="15%">Description</th>
				<td><xsl:value-of select="oai_marc:varfield[@id='256']/oai_marc:subfield[@label='a']"/></td>
			</tr>
		</xsl:if>

		<xsl:for-each select="oai_marc:varfield[@id='300']">
			<tr>
				<th width="15%">Description</th>
				<td><xsl:value-of select="oai_marc:subfield[@label='a']"/>
				<xsl:value-of select="oai_marc:subfield[@label='b']"/>
				<xsl:value-of select="oai_marc:subfield[@label='c']"/></td>
			</tr>
		</xsl:for-each>

		<!-- Notes -->
		<xsl:if test="oai_marc:varfield[
			@id='500'
			or @id='505'
			or @id='511'
			or @id='518'
			or @id='530'
		]">
			<tr>
				<th width="15%">Notes</th>
				<td>
					<xsl:for-each select="oai_marc:varfield[
						@id='500'
						or @id='505'
						or @id='511'
						or @id='518'
						or @id='530'
					]">
						<xsl:for-each select="oai_marc:subfield">
							<xsl:apply-templates select="." />
							<xsl:text> </xsl:text>
						</xsl:for-each>
						<xsl:if test="position() != last()"><br /></xsl:if>
					</xsl:for-each>
				</td>
			</tr>
		</xsl:if>

		<!-- ISSN -->
		<xsl:if test="oai_marc:varfield[@id='022']">
			<tr>
				<th width="15%">ISSN</th>
				<td>
					<xsl:variable name="nodes" select="oai_marc:varfield[@id='022']" />
					<xsl:call-template name="separate-with-break"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
				</td>
			</tr>
		</xsl:if>

		<!-- ISBN -->
		<xsl:if test="oai_marc:varfield[@id='020']">
			<tr>
				<th width="15%">ISBN</th>
				<td>
					<xsl:variable name="nodes" select="oai_marc:varfield[@id='020']" />
					<xsl:call-template name="separate-with-break"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
				</td>
			</tr>
		</xsl:if>

		<!-- Subject -->
		<xsl:if test="oai_marc:varfield[
			@id='600'
			or @id='610'
			or @id='611'
			or @id='630'
			or @id='650'
			or @id='651'
		]">
			<tr>
				<th width="15%">Subjects</th>
				<td>
					<xsl:for-each select="oai_marc:varfield[
						@id='600'
						or @id='610'
						or @id='611'
						or @id='630'
						or @id='650'
						or @id='651'
					]">
						<xsl:for-each select="oai_marc:subfield">
							<xsl:if test="@label='v' or @label='x' or @label='y' or @label='z'">-- </xsl:if>
							<xsl:apply-templates select="." />
							<xsl:text> </xsl:text>
						</xsl:for-each>
						<xsl:if test="position() != last()"><br /></xsl:if>
					</xsl:for-each>
				</td>
			</tr>
		</xsl:if>

		<!-- Series -->
		<xsl:if test="oai_marc:varfield[
			@id='490'
			or @id='440'
			or @id='800'
			or @id='810'
			or @id='811'
			or @id='830'
		]">
		<tr>
			<th width="15%">Series</th>
			<td>
				<xsl:for-each select="oai_marc:varfield[
					@id='490'
					or @id='440'
					or @id='800'
					or @id='810'
					or @id='811'
					or @id='830'
				]">
					<xsl:variable name="nodes" select="oai_marc:subfield" />
					<xsl:call-template name="separate-with-space"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
					<xsl:if test="position() != last()"><br /></xsl:if>
				</xsl:for-each>
			</td>
		</tr>
		</xsl:if>

		<!-- Analyticals -->
		<xsl:for-each select="oai_marc:varfield[
			@id='773'
		]">
		<tr>
			<th width="15%">IN</th>
			<td>
				<xsl:variable name="lcn" select="substring-after(oai_marc:subfield[@label='w'], '(UkOxU)')" />
				<xsl:choose>
				  <xsl:when test="$lcn">
				  	<a href="ZGet?docid={$lcn}">
							<xsl:call-template name="separate-with-space">
								<xsl:with-param name="nodes" select="oai_marc:subfield[
									@label='a'
									or @label='b'
									or @label='c'
									or @label='d'
									or @label='k'
									or @label='g'
									or @label='p'
									or @label='r'
									or @label='s'
									or @label='t'
									or @label='u'
									or @label='y'
									]" />
							</xsl:call-template>
						</a>
				  </xsl:when>
				  <xsl:otherwise>
						<xsl:call-template name="separate-with-space">
							<xsl:with-param name="nodes" select="oai_marc:subfield[
								@label='a'
								or @label='b'
								or @label='c'
								or @label='d'
								or @label='k'
								or @label='g'
								or @label='p'
								or @label='r'
								or @label='s'
								or @label='t'
								or @label='u'
								or @label='x'
								or @label='y'
								or @label='z'
								]" />
						</xsl:call-template>
				  </xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
		</xsl:for-each>

		<xsl:for-each select="oai_marc:varfield[
			@id='774'
		]">
		<tr>
			<th width="15%">Includes</th>
			<td>
				<xsl:variable name="lcn" select="substring-after(oai_marc:subfield[@label='w'], '(UkOxU)')" />
				<xsl:choose>
				  <xsl:when test="$lcn">
				  	<a href="ZGet?docid={$lcn}">
							<xsl:call-template name="separate-with-space">
								<xsl:with-param name="nodes" select="oai_marc:subfield[
									@label='a'
									or @label='b'
									or @label='c'
									or @label='d'
									or @label='g'
									or @label='k'
									or @label='r'
									or @label='s'
									or @label='t'
									or @label='u'
									or @label='y'
									]" />
							</xsl:call-template>
						</a>
				  </xsl:when>
				  <xsl:otherwise>
						<xsl:call-template name="separate-with-space">
							<xsl:with-param name="nodes" select="oai_marc:subfield[
								@label='a'
								or @label='b'
								or @label='c'
								or @label='d'
								or @label='g'
								or @label='k'
								or @label='r'
								or @label='s'
								or @label='t'
								or @label='u'
								or @label='x'
								or @label='y'
								or @label='z'
								]" />
						</xsl:call-template>
				  </xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
		</xsl:for-each>

		<!-- Additional physical form entry (776) -->
		<xsl:for-each select="oai_marc:varfield[
			@id='776'
		]">
		<tr>
			<th width="15%">
				<xsl:choose>
					<xsl:when test="@oai_marc:i2=' '">Available in another form</xsl:when>
					<xsl:otherwise></xsl:otherwise>
				</xsl:choose>
			</th>
			<td>
				<xsl:variable name="lcn" select="substring-after(oai_marc:subfield[@label='w'], '(UkOxU)')" />
				<xsl:choose>
				  <xsl:when test="$lcn">
				  	<a href="ZGet?docid={$lcn}">
							<xsl:call-template name="separate-with-space">
								<xsl:with-param name="nodes" select="oai_marc:subfield[
									@label='a'
									or @label='b'
									or @label='c'
									or @label='d'
									or @label='k'
									or @label='g'
									or @label='p'
									or @label='r'
									or @label='s'
									or @label='t'
									or @label='u'
									or @label='y'
									]" />
							</xsl:call-template>
						</a>
				  </xsl:when>
				  <xsl:otherwise>
						<xsl:call-template name="separate-with-space">
							<xsl:with-param name="nodes" select="oai_marc:subfield[
								@label='a'
								or @label='b'
								or @label='c'
								or @label='d'
								or @label='k'
								or @label='g'
								or @label='p'
								or @label='r'
								or @label='s'
								or @label='t'
								or @label='u'
								or @label='x'
								or @label='y'
								or @label='z'
								]" />
						</xsl:call-template>
				  </xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
		</xsl:for-each>

		<!-- Related Item -->
		<xsl:for-each select="oai_marc:varfield[
			@id='534'
			or @id='760'
			or @id='770'
			or @id='772'
			or @id='780'
			or @id='785'
			or @id='786'
		]">
			<xsl:variable name="identifier" select="oai_marc:subfield[@label='x' or @label='z' or @label='w']"/>
			<xsl:choose>
				<xsl:when test="oai_marc:subfield[@label='x']">
					<tr>
						<th width="15%">Related Item</th>
						<td><a href="ZGet?issn={$identifier}"><xsl:value-of select="oai_marc:subfield[@label='t']"/></a></td>
					</tr>
				</xsl:when>
				<xsl:when test="oai_marc:subfield[@label='z']">
					<tr>
						<th width="15%">Related Item</th>
						<td><a href="ZGet?isbn={$identifier}"><xsl:value-of select="oai_marc:subfield[@label='t']"/></a></td>
					</tr>
				</xsl:when>
				<xsl:when test="oai_marc:subfield[@label='w']">
					<tr>
						<th width="15%">Related Item</th>
						<td><a href="ZGet?docid={substring($identifier, 8, 8)}"><xsl:value-of select="oai_marc:subfield[@label='t']"/></a></td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr>
						<th width="15%">Related Item</th>
						<td>
							<xsl:for-each select="oai_marc:subfield">
								<xsl:value-of select="."/><xsl:text>	</xsl:text><!-- content can be at top level, or in child element -->
							</xsl:for-each>
						</td>
					</tr>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>

	</table>



	<!-- Electronic Resources -->
	<xsl:if test="oai_marc:varfield[@id='856']">

		<h2>Electronic Resources</h2>
		<table width="100%" cellspacing="4" class="electronicresources">
			<xsl:for-each select="oai_marc:varfield[@id='856']">
				<tr valign="top">
					<th width="15%">
						<xsl:choose>
						  <xsl:when test="@oai_marc:i2='0'">Electronic resource</xsl:when>
						  <xsl:when test="@oai_marc:i2='1'">Electronic version</xsl:when>
						  <xsl:when test="@oai_marc:i2='2'">Related electronic resource</xsl:when>
						  <xsl:when test="@oai_marc:i2='8'"></xsl:when>
						  <xsl:otherwise>Electronic resource</xsl:otherwise>
						</xsl:choose>
					</th>
					<td>
						<xsl:for-each select="oai_marc:subfield">
							<xsl:apply-templates select="." />
							<xsl:if test="position() != last()"><br /></xsl:if>
						</xsl:for-each>
					</td>
				</tr>
			</xsl:for-each>
		</table>

	</xsl:if>
	<!-- end Electronic Resources -->



	<!-- Library Holdings -->
	<xsl:if test="oai_marc:varfield[@id='852']">

		<h2>Library Holdings</h2>
		<table width="100%" cellspacing="4" class="libraryholdings">
			<tr valign="top">
				<th>Location</th>
				<th>Call Number</th>
				<th>Status</th>
			</tr>
			<xsl:for-each select="oai_marc:varfield[@id='852']">
				<tr valign="top">
					<td width="33%">
						<!-- Location -->
						<xsl:variable name="nodes" select="oai_marc:subfield[@label='b']" />
						<xsl:call-template name="separate-with-break"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
					</td>
					<td width="33%">
						<!-- Call Number, Part etc -->
						<xsl:variable name="nodes" select="oai_marc:subfield[
							@label='h'
							or @label='m'
							or @label='c'
							or @label='i'
							or @label='j'
							or @label='k'
							or @label='l'
							or @label='2'
							or @label='t'
							or @label='3'
						]" />
						<xsl:call-template name="separate-with-space"><xsl:with-param name="nodes" select="$nodes" /></xsl:call-template>
					</td>
					<td width="33%">
						<!-- Status -->
						<xsl:for-each select="oai_marc:subfield[@label='y']">
							<xsl:value-of select="."/><xsl:text> </xsl:text>
						</xsl:for-each>
					</td>
				</tr>
			</xsl:for-each>
		</table>

	</xsl:if>
	<!-- end Library Holdings -->


	</xsl:template>



	<xsl:template name="separate-with-space">
		<xsl:param name="nodes" />
		<xsl:for-each select="$nodes">
			<xsl:apply-templates select="." />
			<xsl:if test="position() != last()"><xsl:text> </xsl:text></xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="separate-with-break">
		<xsl:param name="nodes" />
		<xsl:for-each select="$nodes">
			<xsl:apply-templates select="." />
			<xsl:if test="position() != last()"><br /></xsl:if>
		</xsl:for-each>
	</xsl:template>



	<xsl:template match="oai_marc:varfield[@id='856']/oai_marc:subfield[@label='u']">
		<!-- URL - make into hyperlink -->
		<a href="{.}"><xsl:value-of select="."/></a>
	</xsl:template>

	<xsl:template match="oai_marc:varfield[@id='852']/oai_marc:subfield[@label='t']">
		<!-- copy number - add label -->
		copy <xsl:value-of select="."/>
	</xsl:template>


	<xsl:template match="oai_marc:varfield[
			@id='787'
			or @id='786'
			or @id='785'
			or @id='780'
			or @id='777'
			or @id='776'
			or @id='775'
			or @id='774'
			or @id='773'
			or @id='772'
			or @id='770'
			or @id='767'
			or @id='765'
			or @id='762'
			or @id='760'
			or @id='730'
			or @id='711'
			or @id='710'
			or @id='700'
			or @id='534'
			or @id='510'
			or @id='500'
			or @id='490'
			or @id='440'
			or @id='410'
			or @id='400'
			or @id='247'
		]/oai_marc:subfield[
			@label='x'
		]">
		<!-- ISSN - make into hyperlink -->
		<a href="ZGet?issn={.}"><xsl:value-of select="." /></a>
	</xsl:template>



</xsl:stylesheet>