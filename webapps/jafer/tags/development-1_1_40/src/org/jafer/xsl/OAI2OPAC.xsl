<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:oai="http://www.openarchives.org/OAI/oai_marc" exclude-result-prefixes="oai">

<xsl:template match="oai:oai_marc">
	<bibliographic>
		<xsl:copy-of select="."/>
	</bibliographic>

</xsl:template>


</xsl:stylesheet>
