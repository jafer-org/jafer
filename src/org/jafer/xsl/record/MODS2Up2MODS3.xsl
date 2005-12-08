<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xlink="http://www.w3.org/TR/xlink"
	xmlns="http://www.loc.gov/mods"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.loc.gov/mods/ http://www.loc.gov/standards/mods/mods.xsd">

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<!-- MODS version 2 to MODS version 3 Conversion Stylesheet
		Trail 9/2003
	-->
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="modsCollection">
				<xsl:for-each select="modsCollection">
					<modsCollection xmlns:xlink="http://www.w3.org/1999/xlink"
			 			xmlns="http://www.loc.gov/mods/v3"
						xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
						xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-0.xsd">
						<xsl:for-each select="mods">
							<mods version="3.0">
								<xsl:apply-templates/>
							</mods>
						</xsl:for-each>
					</modsCollection>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="mods">
				<xsl:for-each select="mods">
					<mods version="3.0" 
					  xmlns:xlink="http://www.w3.org/1999/xlink"
			 		  xmlns="http://www.loc.gov/mods/v3"
					  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
					  xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-0.xsd">
						<xsl:apply-templates/>
					</mods>
				</xsl:for-each>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="title"><!--moves parts outsiede title -->		
		<title>
			<xsl:value-of select="."/>
		</title>
		<xsl:apply-templates select="partName|partNumber"/>
	</xsl:template>

	<xsl:template match="role">
		<xsl:choose>
			<xsl:when test="(text='creator' or text='Creator') and (preceding-sibling::role/text='Creator' or preceding-sibling::role/text='creator')"/>
			<xsl:otherwise>
				<role>
					<roleTerm>
						<xsl:attribute name="type">
							<xsl:value-of select="local-name(*)"/>
						</xsl:attribute>
						<xsl:value-of select="*"/>
					</roleTerm>
				</role>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="place">
		<xsl:for-each select="*">
			<place>	
				<placeTerm>
					<xsl:choose>
						<xsl:when test="@authority='marc'">
							<xsl:attribute name="authority">marccountry</xsl:attribute>
						</xsl:when>
						<xsl:when test="not(@authority)"/>
						<xsl:otherwise><xsl:copy-of select="@authority"/></xsl:otherwise>
					</xsl:choose>								
					<xsl:attribute name="type">
						<xsl:value-of select="local-name()"/>
					</xsl:attribute>
					<xsl:value-of select="."/>
				</placeTerm>
			</place>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="form">		
		<form>			
			<xsl:copy-of select="@*"/>
			<xsl:choose>
				<xsl:when test="unControlled">
					<xsl:value-of select="unControlled"/>
				</xsl:when>
				<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
			</xsl:choose>
		</form>
	</xsl:template>

	<xsl:template match="identifier">
<!--1. Convert all <identifier type="uri"> to <location><url>
This would make an assumption that any URIs previously used are really
locations. That is probably a likely assumption.

2. Convert <identifier type="uri"> to both <location><url> and retain the
previously coded <identifier type="uri">. This might be safest but causes
redundancy. A human being generally would have to determine whether it is
really an identifier or location, although in many cases it isn't obvious.

3. Analyze <identifier type="uri"> and if it begins with doi* or hdl* or purl* put it in
both places. The rest go in location.

4. Leave it as is in <identifier> and let the user decide whether to
convert it.
************ option 3 selected ************
-->
		<xsl:choose>
			<xsl:when test="@type='uri'">			
				<xsl:choose>
					<xsl:when test="starts-with(.,'hdl') or starts-with(.,'doi') or starts-with(.,'purl')or starts-with(.,'http://hdl') or starts-with(.,'http://purl')">
						<location>
							<url><xsl:value-of select="."/></url>
						</location>
	
						<identifier>
							<xsl:attribute name="type">
							<xsl:choose>
								<xsl:when test="starts-with(.,'hdl') or starts-with(.,'http://hdl')">
									<xsl:text>hdl</xsl:text>
								</xsl:when>
								<xsl:when test="starts-with(.,'doi')">
									<xsl:text>doi</xsl:text>
								</xsl:when>
								<xsl:when test="starts-with(.,'purl') or starts-with(.,'http://purl')">
									<xsl:text>purl</xsl:text>
								</xsl:when>
								<xsl:otherwise><xsl:text>uri</xsl:text></xsl:otherwise>
							</xsl:choose>
				<!--			<xsl:copy-of select="."/> -->
							</xsl:attribute>
							<xsl:value-of select="."/> 
						</identifier>
				
					</xsl:when>
					<xsl:otherwise>
						<location>
							<url><xsl:value-of select="."/></url>
						</location>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise><xsl:apply-templates select="identifier"/></xsl:otherwise>
		</xsl:choose>		
	</xsl:template>

	<xsl:template match="location">		
		<location>
			<physicalLocation>
				<xsl:copy-of select="@*"/>				
				<xsl:value-of select="."/>		
			</physicalLocation>
		</location>		
	</xsl:template>
	
	<xsl:template match="language">
		<language>
			<languageTerm>				
				<xsl:if test="@authority">
					<xsl:copy-of select="@authority"/>
						<xsl:attribute name="type">code</xsl:attribute>
				</xsl:if>
				<xsl:if test="not(@authority)">
					<xsl:attribute name="type">text</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="."/>			
			</languageTerm>
		</language>
	</xsl:template>

	<xsl:template match="relatedItem">
		<relatedItem>
			<xsl:if test="not(@type='related')">
				<xsl:attribute name="type">
					<xsl:value-of select="@type"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</relatedItem>
	</xsl:template>

	<xsl:template match="languageOfCataloging">
		<languageOfCataloging>
			<languageTerm>				
				<xsl:if test="@authority">
					<xsl:copy-of select="@authority"/>
					<xsl:attribute name="type">
						<xsl:text>code</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="."/>			
			</languageTerm>
		</languageOfCataloging>
	</xsl:template>

	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>	
	</xsl:template>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c)1998-2003 Copyright Sonic Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="..\test_files\modsv2fromMARCXML.xml" htmlbaseurl="" outputurl="..\test_files\modsv3fromv2.xml" processortype="xalan" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext=""/><scenario default="no" name="Scenario2" userelativepaths="yes" externalpreview="no" url="testIhasmods.xml" htmlbaseurl="" outputurl="mods2 to 3 testOutput.xml" processortype="internal" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext=""/></scenarios><MapperInfo srcSchemaPath="" srcSchemaRoot="" srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
</metaInformation>
-->