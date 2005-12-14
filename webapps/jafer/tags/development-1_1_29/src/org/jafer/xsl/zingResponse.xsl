<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" omit-xml-declaration="no"/>

<xsl:param name="resultSetName" select="''"/>
<xsl:param name="resultSetTTL" select="''"/>
<xsl:param name="totalHits" select="''"/>
<xsl:param name="recordSchema" select="''"/>
<xsl:param name="diagnosticSchema" select="''"/>
<xsl:param name="diagnosticCondition" select="'0'"/>
<xsl:param name="diagnosticInfo" select="''"/>
<xsl:param name="statusCode" select="'0'"/>
<xsl:param name="statusCondition" select="''"/>
<xsl:param name="statusInfo" select="''"/>

<xsl:template match="root">
  <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:zng="urn:z3950:zng_prototype1">
    <SOAP-ENV:Body>
      <!--<xsl:copy-of select="Fault"/> copy soapExceptions to here? -->
      <zng:searchRetrieveResponse>
        <zng:resultSetReference>
          <zng:resultSetName><xsl:value-of select="$resultSetName"/></zng:resultSetName><!--SERVER-GENERATED-->
          <zng:resultSetTTL><xsl:value-of select="$resultSetTTL"/></zng:resultSetTTL><!--SERVER-GENERATED-->
        </zng:resultSetReference>
        <zng:totalHits><xsl:value-of select="$totalHits"/></zng:totalHits><!---->
        <zng:records>
          <xsl:copy-of select="record"/><!-- copy records to here -->
        </zng:records>
        <xsl:copy-of select="status"/><!-- copy status to here -->
      </zng:searchRetrieveResponse>
    </SOAP-ENV:Body>
  </SOAP-ENV:Envelope>
</xsl:template>
</xsl:stylesheet>