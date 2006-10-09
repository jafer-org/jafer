<!-- 
    **********************************************************************************
    * This template converts XCQL to CQL                                             *
    *                                                                                *
    * This template starts by processing the top level node XCQL and outputs one     *
    * node of type <CQL> that contains the CQL string                                *     
    *                                                                                *     
    **********************************************************************************
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="XML" encoding="UTF-8" indent="no"/>
    <!-- ************************************** -->
    <!-- Processes an XCQL node                -->
    <!-- ************************************** -->
    <xsl:template match="XCQL">
        <CQL>
            <!-- apply the templates for the child nodes -->
            <xsl:apply-templates select="./*"/>
        </CQL>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes a triple node                -->
    <!-- ************************************** -->
    <xsl:template match="triple">
        <!-- convert to triple to (leftOperand booleanvalue rightOperand) 
             adding brackets only if this triple is not under the XCQL 
             root element -->
        <!-- Only add opening bracket if parent is not XCQL node -->
        <xsl:if test="not(parent::XCQL)">
            <xsl:text>(</xsl:text>
        </xsl:if>
        <!-- Apply the Triple or SearchClause template to left operand -->
        <xsl:apply-templates select="./leftOperand/*"/>
        <xsl:text> </xsl:text>
        <!-- Apply the boolean information -->
        <xsl:apply-templates select="./boolean"/>
        <xsl:text> </xsl:text>
        <!-- Apply the Triple or SearchClause template to right operand -->
        <xsl:apply-templates select="./rightOperand/*"/>
        <!-- Only add closing bracket if parent is not XCQL node -->
        <xsl:if test="not(parent::XCQL)">
            <xsl:text>)</xsl:text>
        </xsl:if>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes a search clause              -->
    <!-- ************************************** -->
    <xsl:template match="searchClause">
        
        <!-- output the index and relation if it is not serverchoice -->
        <xsl:if test="./index[text() != 'cql.serverChoice']">
            <xsl:value-of select="./index"/>
            <xsl:if test="./relation">
                <xsl:text> </xsl:text>
                <xsl:apply-templates select="./relation"/>
                <xsl:text> </xsl:text>
            </xsl:if>
        </xsl:if>
        <!-- output the term -->
        <xsl:text>&quot;</xsl:text>
        <xsl:value-of select="./term"/>
        <xsl:text>&quot;</xsl:text>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes a boolean and its modifiers  -->
    <!-- ************************************** -->
    <xsl:template match="boolean">
        <xsl:value-of select="./value"/>
        <xsl:apply-templates select="./modifiers"/>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes relation node and its modifiers  -->
    <!-- ************************************** -->
    <xsl:template match="relation">
        <xsl:value-of select="./value"/>
        <xsl:apply-templates select="./modifiers"/>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes modifiers  -->
    <!-- ************************************** -->
    <xsl:template match="modifiers">
        <xsl:for-each select="./modifier">
            <xsl:text>/</xsl:text>
            <xsl:value-of select="./type"/>
            <!-- comparison and value must exist together to 
                 form proper CQL but we will assume input is correct -->
            <xsl:value-of select="./comparison"/>
            <xsl:value-of select="./value"/>
        </xsl:for-each>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes prefixes node                -->
    <!-- ************************************** -->
    <xsl:template match="prefixes">
        <!-- Process each prefix in the file -->
        <xsl:for-each select="./prefix">
            <!-- Translate prefix to name="identifier" -->
            <xsl:text>&gt;</xsl:text>
            <xsl:value-of select="./name"/>
            <xsl:text>=&quot;</xsl:text>
            <xsl:value-of select="./identifier"/>
            <xsl:text>&quot; </xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
