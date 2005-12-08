<?xml version="1.0" encoding="utf-8"?>
<!--
    JISC ELF Discovery+ Poject - http://devil.lib.ed.ac.uk
    Copyright (C) 2004, University of Edinburgh.

    This stylesheet is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This styelsheet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

    Author: Boon Low
    MARC2RLI xslt stylesheet version 1.0
    Purpose: Mapping  Dublin Core metadata (from Eprint UK)
            to resource metadata based on the IMS RLI XML Binding

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:rli="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0"
xmlns:rlx="http://www.imsglobal.org/services/rli/rlicommon/imsRLICommonSchema_v1p0"
xsi:schemaLocation="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0 http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0.xsd"
xmlns:dc="http://purl.org/dc/elements/1.1/"
xmlns:srw_dc="info:srw/schema/1/dc-v1.1" exclude-result-prefixes="dc">

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<!-- Root element name for DC records not defined. -->
	<!-- need to re-structure stylesheet and provide templates for each DC element instead? -->
	<xsl:template match="*">
		<xsl:param name="lang">
			<xsl:choose>
				<xsl:when test="dc:language = 'eng' or dc:language = 'en'">en-GB</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="dc:language"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>

		<rli:resource xsi:schemaLocation="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0 http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0.xsd">
			<xsl:if test="dc:header">
				<rli:indexId>
					<xsl:value-of select="dc:header/dc:identifier"/>
				</rli:indexId>
			</xsl:if>
			<xsl:if test="dc:identifier">
				<rli:indexId>
					<xsl:value-of select="dc:identifier"/>
				</rli:indexId>
			</xsl:if>
			<rli:resourceMetadata>
				<!-- *** description  *** -->
				<xsl:if test="dc:description">
					<rli:description>
						<rli:metadataLangString>
							<rlx:LangString>
								<rlx:language>
									<xsl:value-of select="$lang"/>
								</rlx:language>
								<rlx:text>
									<xsl:value-of select="dc:description"/>
								</rlx:text>
							</rlx:LangString>
						</rli:metadataLangString>
					</rli:description>
				</xsl:if>
				<rli:citation>
					<!-- *** titles *** -->
					<xsl:if test="dc:title">
						<rli:title>
							<rli:metadataLangString>
								<rlx:LangString>
									<rlx:language>
										<xsl:value-of select="$lang"/>
									</rlx:language>
									<rlx:text>
										<xsl:value-of select="dc:title"/>
									</rlx:text>
								</rlx:LangString>
							</rli:metadataLangString>
						</rli:title>
					</xsl:if>
					<!-- *** authors *** -->
					<xsl:for-each select="dc:creator">
						<rli:creator>
							<rli:metadataLangString>
								<rlx:LangString>
									<rlx:language>
										<xsl:value-of select="$lang"/>
									</rlx:language>
									<rlx:text>
										<xsl:value-of select="."/>
									</rlx:text>
								</rlx:LangString>
							</rli:metadataLangString>
						</rli:creator>
					</xsl:for-each>
					<!-- *** Publication *** -->
					<xsl:if test="dc:date">
						<rli:publicationDate>
							<rli:metadataDate>
								<xsl:value-of select="dc:date"/>
							</rli:metadataDate>
						</rli:publicationDate>
					</xsl:if>
					<xsl:if test="dc:publisher">
						<rli:publisher>
							<rli:metadataLangString>
								<rlx:LangString>
									<rlx:language>
										<xsl:value-of select="$lang"/>
									</rlx:language>
									<rlx:text>
										<xsl:value-of select="dc:publisher"/>
									</rlx:text>
								</rlx:LangString>
							</rli:metadataLangString>
						</rli:publisher>
					</xsl:if>
					<!-- *** identifiers *** -->
					<xsl:if test="dc:identifier">
						<rli:standardIdentifier>
							<rli:standardIdentifierType>
								<rli:metadataToken>URI</rli:metadataToken>
							</rli:standardIdentifierType>
							<rli:identifierString>
								<rli:metadataString>
									<xsl:value-of select="dc:identifier"/>
								</rli:metadataString>
							</rli:identifierString>
						</rli:standardIdentifier>
					</xsl:if>
				</rli:citation>
				<!-- *** url for electronic resources *** -->
				<xsl:if test="dc:identifier">
					<rli:location>
						<rli:locationType>
							<rli:metadataToken>URI</rli:metadataToken>
						</rli:locationType>
						<rli:locator>
							<rli:metadataString>
								<xsl:value-of select="dc:identifier"/>
							</rli:metadataString>
						</rli:locator>
					</rli:location>
				</xsl:if>
				<xsl:if test="starts-with(substring-after(dc:format, ' '), 'http')">
					<rli:location>
						<rli:locationType>URI</rli:locationType>
						<rli:locator>
							<rli:metadataString>
								<xsl:value-of select="substring-after(dc:format, ' ')"/>
							</rli:metadataString>
						</rli:locator>
					</rli:location>
				</xsl:if>
			</rli:resourceMetadata>
		</rli:resource>
	</xsl:template>
</xsl:stylesheet>
