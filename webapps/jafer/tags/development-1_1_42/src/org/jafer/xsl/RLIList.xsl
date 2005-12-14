<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rli="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0" xmlns:rlx="http://www.imsglobal.org/services/rli/rlicommon/imsRLICommonSchema_v1p0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" >

	<xsl:template match="records">
		<rli:resourceList xsi:schemaLocation="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0
http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0.xsd">
			<rli:resourceListMetadata>
				<rli:title>
					<rli:metadataLangString>
						<rlx:LangString>
							<rlx:language>en-GB</rlx:language>
							<rlx:text>My Resource List</rlx:text>
						</rlx:LangString>
					</rli:metadataLangString>
				</rli:title>
				<rli:rightsDescription>
					<rli:metadataLangString>
						<rlx:LangString>
							<rlx:language>en-GB</rlx:language>
							<rlx:text>xxxxxxxx</rlx:text>
						</rlx:LangString>
					</rli:metadataLangString>
				</rli:rightsDescription>
			</rli:resourceListMetadata>
			
			<xsl:for-each select="rli:resource">
				<xsl:copy-of select="."/>
			</xsl:for-each>
			
		</rli:resourceList>
	</xsl:template>
</xsl:stylesheet>
