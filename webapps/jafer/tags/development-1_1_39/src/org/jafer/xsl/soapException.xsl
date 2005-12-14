<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" omit-xml-declaration="no"/>

<xsl:param name="faultcode" select="Server"/>
<xsl:param name="faultstring" select="''"/>

<xsl:template match="detail">
  <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:zng="urn:z3950:zng_prototype1">
    <SOAP-ENV:Body>
      <SOAP-ENV:Fault>
        <SOAP-ENV:faultcode><xsl:copy-of select="$faultcode"/></SOAP-ENV:faultcode>
        <SOAP-ENV:faultstring><xsl:copy-of select="$faultstring"/></SOAP-ENV:faultstring>
        <xsl:copy-of select="."/>
      </SOAP-ENV:Fault>
    </SOAP-ENV:Body>
  </SOAP-ENV:Envelope>
</xsl:template>
</xsl:stylesheet>