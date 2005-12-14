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
    Purpose: Mapping OAI MARC metadata to metadata based on the IMS RLI XML Binding

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:rli="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0"
xmlns:rlx="http://www.imsglobal.org/services/rli/rlicommon/imsRLICommonSchema_v1p0" xsi:schemaLocation="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0 http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0.xsd" xmlns:oai_marc="http://www.openarchives.org/OAI/1.1/oai_marc" exclude-result-prefixes="oai_marc">

	<xsl:template match="oai_marc:oai_marc">
	
		<xsl:param name="lang">
			<xsl:choose>
				<xsl:when test="substring(oai_marc:fixfield[@id='008'], 37, 3) = 'eng'">en-GB</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="substring(oai_marc:fixfield[@id='008'], 37, 3)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		
		<rli:resource xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0 http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0.xsd">
			<rli:indexId>
				<xsl:value-of select="substring-before(substring(oai_marc:fixfield[@id='001'], 2), '&quot;')"/>
			</rli:indexId>
			<rli:resourceMetadata>
				<!-- *** description  *** -->
				<!-- abstract 520 -->
				<xsl:if test="oai_marc:varfield[@id='520']">
					<rli:description>
						<rli:metadataLangString>
							<rlx:LangString>
								<rlx:language>
									<xsl:value-of select="$lang"/>
								</rlx:language>
								<rlx:text>
									<xsl:value-of select="oai_marc:varfield[@id='520']/oai_marc:subfield[@label='a']"/>
								</rlx:text>
							</rlx:LangString>
						</rli:metadataLangString>
					</rli:description>
				</xsl:if>
				<!-- *** Languages *** -->
				<!-- 008 -->
				<xsl:if test="oai_marc:fixfield[@id='008']">
					<rli:language>
					<rli:metadataToken>					
						<xsl:value-of select="$lang"/>
						</rli:metadataToken>
					</rli:language>
				</xsl:if>
				<!-- *** MIME types *** -->
				<xsl:for-each select="oai_marc:varfield[@id='856']/oai_marc:subfield[@label='q']">
					<rli:format>
						<xsl:value-of select="."/>
					</rli:format>
				</xsl:for-each>
				<rli:citation>
					<!-- *** titles *** -->
					<!-- feasible MARC fields: 130, 210, 240, 242, 245, 246, 247, 730, 740 -->
					<xsl:if test="oai_marc:varfield[@id='130' or @id='210'
    or @id='222' or @id='240' or @id='242' or @id='245'
    or @id='246' or @id='247' or @id='730' or @id='740']">
						<!-- maps the mandantory 245, other fields pending -->
						<rli:title>
							<xsl:for-each select="oai_marc:varfield[@id='245']">
								<rli:metadataLangString>
									<rlx:LangString>
										<rlx:language>
											<xsl:value-of select="$lang"/>
										</rlx:language>
										<rlx:text>
											<xsl:for-each select="oai_marc:subfield[@label='a']">
												<xsl:value-of select="."/>
											</xsl:for-each>
											<xsl:for-each select="oai_marc:subfield[@label='b']">
												<xsl:value-of select="."/>
											</xsl:for-each>
											<xsl:for-each select="oai_marc:subfield[@label='n']">
												<xsl:value-of select="."/>
											</xsl:for-each>
											<xsl:for-each select="oai_marc:subfield[@label='p']">
												<xsl:value-of select="."/>
											</xsl:for-each>
											<xsl:for-each select="oai_marc:subfield[@label='s']">
												<xsl:value-of select="."/>
											</xsl:for-each>
										</rlx:text>
									</rlx:LangString>
								</rli:metadataLangString>
							</xsl:for-each>
						</rli:title>
					</xsl:if>
					<!-- *** Authors *** -->
					<!-- personal 100 -->
					<xsl:for-each select="oai_marc:varfield[@id='100']">
						<rli:creator>
							<rli:metadataLangString>
								<rlx:LangString>
									<rlx:language>
										<xsl:value-of select="$lang"/>
									</rlx:language>
									<rlx:text>
										<xsl:if test="oai_marc:subfield[@label='c']">
											<xsl:value-of select="oai_marc:subfield[@label='c']"/>
										</xsl:if>
										<xsl:value-of select="oai_marc:subfield[@label='a']"/>
									</rlx:text>
								</rlx:LangString>
							</rli:metadataLangString>
						</rli:creator>
					</xsl:for-each>
					<!-- corporate author 110-->
					<xsl:for-each select="oai_marc:varfield[@id='110']">
						<rli:creator>
							<rli:metadataLangString>
								<rlx:LangString>
									<rlx:language>
										<xsl:value-of select="$lang"/>
									</rlx:language>
									<rlx:text>
										<xsl:value-of select="oai_marc:subfield[@label='a']"/>
									</rlx:text>
								</rlx:LangString>
							</rli:metadataLangString>
						</rli:creator>
					</xsl:for-each>
					<!-- *** Edition *** -->
					<!--  250 -->
					<xsl:if test="oai_marc:varfield[@id='250']/oai_marc:subfield[@label='a']">
						<rli:edition>
							<rli:metadataLangString>
								<rlx:LangString>
									<rlx:language>
										<xsl:value-of select="$lang"/>
									</rlx:language>
									<rlx:text>
										<xsl:value-of select="oai_marc:varfield[@id='250']/oai_marc:subfield[@label='a']"/>
									</rlx:text>
								</rlx:LangString>
							</rli:metadataLangString>
						</rli:edition>
					</xsl:if>
					<!-- *** Publication *** -->
					<!--  260 -->
					<xsl:if test="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='c']">
						<rli:publicationDate>
							<rli:metadataDate>
								<xsl:value-of select="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='c']"/>
							</rli:metadataDate>
						</rli:publicationDate>
					</xsl:if>
					<xsl:if test="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='a']">
						<rli:publicationPlace>
							<rli:metadataLangString>
								<rlx:LangString>
									<rlx:language>
										<xsl:value-of select="$lang"/>
									</rlx:language>
									<rlx:text>
										<xsl:value-of select="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='a']"/>
									</rlx:text>
								</rlx:LangString>
							</rli:metadataLangString>
						</rli:publicationPlace>
					</xsl:if>
					<xsl:if test="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='b']">
						<rli:publisher>
							<rli:metadataLangString>
								<rlx:LangString>
									<rlx:language>
										<xsl:value-of select="$lang"/>
									</rlx:language>
									<rlx:text>
										<xsl:value-of select="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='b']"/>
									</rlx:text>
								</rlx:LangString>
							</rli:metadataLangString>
						</rli:publisher>
					</xsl:if>
					<!-- *** Volume *** -->
					<!--  440 -->
					<xsl:if test="oai_marc:varfield[@id='440']/oai_marc:subfield[@label='v']">
						<rli:volumeDesignation>
							<rli:metadataString>
								<xsl:value-of select="oai_marc:varfield[@id='440']/oai_marc:subfield[@label='v']"/>
							</rli:metadataString>
						</rli:volumeDesignation>
					</xsl:if>
					<!-- *** Part/Issue number*** -->
					<!--  440 -->
					<xsl:if test="oai_marc:varfield[@id='440']/oai_marc:subfield[@label='n']">
						<rli:partDesignation>
							<rli:metadataString>
								<xsl:value-of select="oai_marc:varfield[@id='440']/oai_marc:subfield[@label='n']"/>
							</rli:metadataString>
						</rli:partDesignation>
					</xsl:if>
					<!-- *** identifiers *** -->
					<!-- isbn 20 -->
					<xsl:if test="oai_marc:varfield[@id='020']">
						<xsl:for-each select="oai_marc:varfield[@id='020']">
							<xsl:if test="oai_marc:subfield[@label='a']">
								<rli:standardIdentifier>
									<rli:standardIdentifierType>
									<rli:metadataToken>URI</rli:metadataToken>
									</rli:standardIdentifierType>
									<rli:identifierString>
										<rli:metadataString>urn:ISBN:<xsl:value-of select="oai_marc:subfield[@label='a']"/>
										</rli:metadataString>
									</rli:identifierString>
								</rli:standardIdentifier>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>
					<!-- issn 22 -->
					<xsl:if test="oai_marc:varfield[@id='022']">
						<xsl:for-each select="oai_marc:varfield[@id='022']">
							<xsl:if test="oai_marc:subfield[@label='a']">
								<rli:standardIdentifier>
									<rli:standardIdentifierType>
									<rli:metadataToken>URI</rli:metadataToken>
									</rli:standardIdentifierType>
									<rli:identifierString>
										<rli:metadataString>urn:ISSN:<xsl:value-of select="oai_marc:subfield[@label='a']"/>
										</rli:metadataString>
									</rli:identifierString>
								</rli:standardIdentifier>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>
					<!-- library of congress control number 10 -->
					<xsl:if test="oai_marc:varfield[@id='010']/oai_marc:subfield[@label='a']">
						<rli:standardIdentifier>
							<rli:standardIdentifierType>
									<rli:metadataToken>URI</rli:metadataToken>
									</rli:standardIdentifierType>
							<rli:identifierString>
								<rli:metadataString>
                    info:lccn:<xsl:value-of select="oai_marc:varfield[@id='010']/oai_marc:subfield[@label='a']"/>
								</rli:metadataString>
							</rli:identifierString>
						</rli:standardIdentifier>
					</xsl:if>
					<!-- oclc control number 19 -->
					<xsl:if test="oai_marc:varfield[@id='019']/oai_marc:subfield[@label='a']">
						<rli:standardIdentifier>
							<rli:standardIdentifierType>
									<rli:metadataToken>URI</rli:metadataToken>
									</rli:standardIdentifierType>
							<rli:identifierString>
								<rli:metadataString>
                    info:oclcnum:<xsl:value-of select="oai_marc:varfield[@id='019']/oai_marc:subfield[@label='a']"/>
								</rli:metadataString>
							</rli:identifierString>
						</rli:standardIdentifier>
					</xsl:if>
					<!-- other unique idenfifier 24 -->
					<xsl:if test="oai_marc:varfield[@id='024']">
						<xsl:for-each select="oai_marc:varfield[@id='024']">
							<xsl:choose>
								<xsl:when test="self::node()/@oai_marc:i1='0'">
									<rli:standardIdentifier>
										<rli:standardIdentifierType>									
									<rli:metadataToken>ISRC</rli:metadataToken>
									</rli:standardIdentifierType>
										<rli:identifierString>
											<rli:metadataString>
												<xsl:value-of select="oai_marc:subfield[@label='a']"/>
											</rli:metadataString>
										</rli:identifierString>
									</rli:standardIdentifier>
								</xsl:when>
								<xsl:when test="self::node()/@oai_marc:i1='1'">
									<rli:standardIdentifier>
										<rli:standardIdentifierType>									
									<rli:metadataToken>UPC</rli:metadataToken>
</rli:standardIdentifierType>
										<rli:identifierString>
											<rli:metadataString>
												<xsl:value-of select="oai_marc:subfield[@label='a']"/>
											</rli:metadataString>
										</rli:identifierString>
									</rli:standardIdentifier>
								</xsl:when>
								<xsl:when test="self::node()/@oai_marc:i1='2'">
									<rli:standardIdentifier>
										<rli:standardIdentifierType>								
									<rli:metadataToken>ISMN</rli:metadataToken>
</rli:standardIdentifierType>
										<rli:identifierString>
											<rli:metadataString>
												<xsl:value-of select="oai_marc:subfield[@label='a']"/>
											</rli:metadataString>
										</rli:identifierString>
									</rli:standardIdentifier>
								</xsl:when>
								<xsl:when test="self::node()/@oai_marc:i1='3'">
									<rli:standardIdentifier>
										<rli:standardIdentifierType>								
									<rli:metadataToken>IAN</rli:metadataToken>
</rli:standardIdentifierType>
										<rli:identifierString>
											<rli:metadataString>
												<xsl:value-of select="oai_marc:subfield[@label='a']"/>
											</rli:metadataString>
										</rli:identifierString>
									</rli:standardIdentifier>
								</xsl:when>
								<xsl:when test="self::node()/@oai_marc:i1='4'">
									<rli:standardIdentifier>
										<rli:standardIdentifierType>								
									<rli:metadataToken>URI</rli:metadataToken>
</rli:standardIdentifierType>
										<rli:identifierString>
											<rli:metadataString>
                   info:sici:<xsl:value-of select="oai_marc:subfield[@label='a']"/>
											</rli:metadataString>
										</rli:identifierString>
									</rli:standardIdentifier>
								</xsl:when>
								<xsl:when test="self::node()/@oai_marc:i1='7'">
									<rli:standardIdentifier>
										<rli:standardIdentifierType>								
									<rli:metadataToken>
											<xsl:value-of select="oai_marc:subfield[@label='2']"/>
											</rli:metadataToken>
										</rli:standardIdentifierType>
										<rli:identifierString>
											<rli:metadataString>
												<xsl:value-of select="oai_marc:subfield[@label='a']"/>
											</rli:metadataString>
										</rli:identifierString>
									</rli:standardIdentifier>
								</xsl:when>
								<xsl:when test="self::node()/@oai_marc:i1='8'">
									<rli:standardIdentifier>
										<rli:standardIdentifierType><rli:metadataToken>
unknown</rli:metadataToken>
</rli:standardIdentifierType>
										<rli:identifierString>
											<rli:metadataString>
												<xsl:value-of select="oai_marc:subfield[@label='a']"/>
											</rli:metadataString>
										</rli:identifierString>
									</rli:standardIdentifier>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
					</xsl:if>
					<!-- related series title 440, e.g. lecture notes on computer science series  -->
					<xsl:if test="oai_marc:varfield[@id='440']">
						<rli:relatedTitle>
							<rli:title>
								<rli:metadataLangString>
									<rlx:LangString>
										<rlx:language>
											<xsl:value-of select="$lang"/>
										</rlx:language>
										<rlx:text>
											<xsl:value-of select="oai_marc:varfield[@id='440']/oai_marc:subfield[@label='a']"/>
										</rlx:text>
									</rlx:LangString>
								</rli:metadataLangString>
							</rli:title>
							<!-- *** Volume *** -->
							<!--  440 -->
							<xsl:if test="oai_marc:varfield[@id='440']/oai_marc:subfield[@label='v']">
								<rli:volumeDesignation>
									<rli:metadataString>
										<xsl:value-of select="oai_marc:varfield[@id='440']/oai_marc:subfield[@label='v']"/>
									</rli:metadataString>
								</rli:volumeDesignation>
							</xsl:if>
							<!-- *** Part/Issue number*** -->
							<!--  440 -->
							<xsl:if test="oai_marc:varfield[@id='440']/oai_marc:subfield[@label='n']">
								<rli:partDesignation>
									<rli:metadataString>
										<xsl:value-of select="oai_marc:varfield[@id='440']/oai_marc:subfield[@label='n']"/>
									</rli:metadataString>
								</rli:partDesignation>
							</xsl:if>
						</rli:relatedTitle>
					</xsl:if>
				</rli:citation>
				<!-- *** online location *** -->
				<xsl:for-each select="oai_marc:varfield[@id='856']/oai_marc:subfield[@label='u']">
					<rli:location>
						<rli:locationType><rli:metadataToken>URI</rli:metadataToken></rli:locationType>
						<rli:locator>
							<rli:metadataString>
								<xsl:value-of select="."/>
							</rli:metadataString>
						</rli:locator>
					</rli:location>
				</xsl:for-each>
				<!-- *** physical location *** -->
				<xsl:for-each select="oai_marc:varfield[@id='852']">
					<rli:location>
						<rli:locationType><rli:metadataToken>physical</rli:metadataToken></rli:locationType>
						<rli:locator>
							<rli:metadataString>
								<xsl:value-of select="oai_marc:subfield[@label='a']"/>
								<xsl:text>  </xsl:text>
								<xsl:for-each select="oai_marc:subfield[@label='b' or @label='c' or @label='j']">
									<xsl:value-of select="."/>
									<xsl:text>  </xsl:text>
								</xsl:for-each>
								<xsl:if test="oai_marc:subfield[@label='e']">
									<xsl:value-of select="oai_marc:subfield[@label='e']"/>
									<xsl:text>  </xsl:text>
								</xsl:if>
								<xsl:if test="oai_marc:subfield[@label='h']">
									<xsl:value-of select="oai_marc:subfield[@label='h']"/>
									<xsl:text>  </xsl:text>
								</xsl:if>
								<xsl:if test="oai_marc:subfield[@label='m']">
									<xsl:value-of select="oai_marc:subfield[@label='m']"/>
									<xsl:text>  </xsl:text>
								</xsl:if>
							</rli:metadataString>
						</rli:locator>
					</rli:location>
				</xsl:for-each>
			</rli:resourceMetadata>
		</rli:resource>
	</xsl:template>
</xsl:stylesheet>
