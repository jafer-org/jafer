<?xml version="1.0"?>
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
    GRS1 xslt stylesheet version 1.0
    Purpose: Mapping Dublin Core metadata 
            to LOM based on the IEEE XML Binding (April 2004]. LOM is the 
            default metadata schema for presenting search results from 
            d+ web services and toolkit. 
             
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://ltsc.ieee.org/xsd/LOM" xmlns:dc="http://purl.org/dc/elements/1.1/" exclude-result-prefixes="dc" >

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	

	<!-- Root element name for DC records not defined. -->
	<!-- need to re-structure stylesheet and provide templates for each DC element instead? -->
	<xsl:template match="*">
		<lom xmlns="http://ltsc.ieee.org/xsd/LOM" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd">
			<!-- <xsl:template match="dc:metadata"> -->
			<general>
				<xsl:if test="../dc:header">
					<identifier>
						<catalog>local</catalog>
						<entry>
							<xsl:value-of select="../dc:header/dc:identifier"/>
						</entry>
					</identifier>
				</xsl:if>
				<xsl:if test="dc:identifier">
					<identifier>
						<catalog>URI</catalog>
						<entry>
							<xsl:value-of select="dc:identifier"/>
						</entry>
					</identifier>
				</xsl:if>
				<!-- *** title *** -->
				<xsl:if test="dc:title">
					<title>
						<string language="en">
							<xsl:value-of select="dc:title"/>
						</string>
					</title>
				</xsl:if>
				<!-- *** description *** -->
				<xsl:if test="dc:description">
					<description>
						<string language="en">
							<xsl:value-of select="dc:description"/>
						</string>
					</description>
				</xsl:if>
				<!-- *** keywords *** -->
				<xsl:for-each select="dc:subject">
					<keyword>
						<string language="en">
							<xsl:value-of select="."/>
						</string>
					</keyword>
				</xsl:for-each>
			</general>
			<lifecycle>
				<!-- *** authors *** -->
				<xsl:for-each select="dc:creator">
					<contribute>
						<role>
							<source>LOMv1.0</source>
							<value>author</value>
						</role>
						<entity>
        BEGIN:VCARD\n
        FN:<xsl:value-of select="."/>\n
        END:VCARD\n
        </entity>
					</contribute>
				</xsl:for-each>
				<!-- *** publishers *** -->
				<xsl:if test="dc:publisher">
					<contribute>
						<role>
							<source>LOMv1.0</source>
							<value>publisher</value>
						</role>
						<entity>
            BEGIN:VCARD\n
            ORG:<xsl:value-of select="dc:publisher"/>\n
            END:VCARD\n
            </entity>
						<date>
							<dateTime>
								<xsl:value-of select="dc:date"/>
							</dateTime>
						</date>
					</contribute>
				</xsl:if>
			</lifecycle>
			<xsl:if test="dc:type">
				<educational>
					<learningResourceType>
						<source>EPRINTS UK</source>
						<value>
							<xsl:value-of select="dc:type"/>
						</value>
					</learningResourceType>
				</educational>
			</xsl:if>
			<technical>
				<xsl:if test="dc:format">
					<location>
						<xsl:value-of select="substring-after(dc:format, ' ')"/>
					</location>
				</xsl:if>
				<xsl:if test="dc:identifier">
					<location>
						<xsl:value-of select="dc:identifier"/>
					</location>
				</xsl:if>
				<xsl:if test="dc:format">
					<format>
						<xsl:value-of select="substring-before(dc:format, ' ')"/>
					</format>
				</xsl:if>
			</technical>
		</lom>
	</xsl:template>
</xsl:stylesheet>
