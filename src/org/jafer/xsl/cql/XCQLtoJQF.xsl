<!--
    **********************************************************************************
    * This template converts XCQL to JQF                                             *
    *                                                                                *
    * This template starts by processing the top level node XCQL and outputs one     *
    * node of JQF type (NOT,AND,OR,ConstrainModel)                                   *
    *                                                                                *
    **********************************************************************************
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="XML" encoding="UTF-8" indent="no"/>
    <!-- **************************************************************************** -->
    <!-- Set up variables used for accessing BIB1 attributes and CQL Context Set data -->
    <!-- **************************************************************************** -->
    <!-- Store ref to the cqlcontextsets.xml file -->
    <xsl:variable name="contextSets" select="document('conf/cqlContextSets.xml')/contextSets"/>
    <!-- Store a ref to the bib one attribute set -->
    <xsl:variable name="bibAttribSet"
        select="document('conf/bib1Attributes.xml')/attributeSets/attributeSet[@name='bib1']"/>
    <!-- ************************************** -->
    <!-- Processes an XCQL node                -->
    <!-- ************************************** -->
    <xsl:template match="XCQL">
        <!-- apply the templates for the child nodes -->
        <xsl:apply-templates select="./*"/>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes prefixes node                -->
    <!-- ************************************** -->
    <xsl:template match="prefixes">
        <!-- Prefixes are ignored in the conversion to JQF -->
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes a triple node                -->
    <!-- ************************************** -->
    <xsl:template match="triple">
        <!-- convert to triple to JQF <AND> or <OR> -->
        <xsl:choose>
            <!-- Triple is AND so create <AND> -->
            <xsl:when test="./boolean/value='and'">
                <and>
                    <!-- Apply the Triple or SearchClause template to left operand -->
                    <xsl:apply-templates select="./leftOperand/*"/>
                    <!-- Apply the Triple or SearchClause template to right operand -->
                    <xsl:apply-templates select="./rightOperand/*"/>
                </and>
            </xsl:when>
            <!-- Triple is AND so create <OR> -->
            <xsl:when test="./boolean/value='or'">
                <or>
                    <!-- Apply the Triple or SearchClause template to left operand -->
                    <xsl:apply-templates select="./leftOperand/*"/>
                    <!-- Apply the Triple or SearchClause template to right operand -->
                    <xsl:apply-templates select="./rightOperand/*"/>
                </or>
            </xsl:when>
            <!-- Triple is NOT so create <AND> and <NOT>round right operand -->
            <xsl:when test="./boolean/value='not'">
                <and>
                    <!-- Apply the Triple or SearchClause template to left operand -->
                    <xsl:apply-templates select="./leftOperand/*"/>
                    <not>
                        <!-- Apply the Triple or SearchClause template to right operand -->
                        <xsl:apply-templates select="./rightOperand/*"/>
                    </not>
                </and>
            </xsl:when>
            <!-- Triple is prox which can not be supported -->
            <xsl:when test="./boolean/value='prox'">
                <xsl:message terminate="yes">
                    <xsl:text>Prox operator can not be supported in JQF </xsl:text>
                </xsl:message>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">
                    <xsl:text>unknown operator can not be supported in JQF </xsl:text>
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes a search clause              -->
    <!-- ************************************** -->
    <xsl:template match="searchClause">
        <constraintModel>
            <constraint>
                <xsl:apply-templates select="./index"/>
                <xsl:apply-templates select="./relation"/>
                <!-- only process for truncation/position if relation type is not "exact" -->
                <xsl:if test="not(./relation/value='exact')">
                    <xsl:call-template name="constructTruncationPosition">
                        <xsl:with-param name="term" select="./term"/>
                    </xsl:call-template>
                </xsl:if>
            </constraint>
            <xsl:apply-templates select="./term"/>
        </constraintModel>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes the term node                -->
    <!-- ************************************** -->
    <xsl:template match="term">
        <xsl:variable name="term" select="."/>
        <xsl:choose>
            <!-- only remove *and ^ if relation value is not "exact" -->
            <xsl:when test="not(../relation/value='exact')">
                <!-- get the last character as for some reason the end-with function is not found -->
                <xsl:variable name="termLength" select="string-length($term)"/>
                <xsl:variable name="lastChar" select="substring($term,$termLength)"/>
                <xsl:variable name="trimLast" select="starts-with($lastChar,'^') or
                    starts-with($lastChar,'*')"/>
                <xsl:variable name="trimFirst" select="starts-with($term,'^') or
                    starts-with($term,'*')"/>
                <xsl:variable name="trimFirstTwo" select="starts-with($term,'^^') or
                    starts-with($term,'^*')"/>
                <xsl:choose>
                    <!-- trim none -->
                    <xsl:when test="not($trimFirst) and not($trimLast)">
                        <xsl:call-template name="constructModel">
                            <xsl:with-param name="term" select="$term"/>
                        </xsl:call-template>
                    </xsl:when>
                    <!-- just trim last -->
                    <xsl:when test="not($trimFirst) and $trimLast">
                        <xsl:call-template name="constructModel">
                            <xsl:with-param name="term" select="substring($term,1,($termLength)-1)"
                            />
                        </xsl:call-template>
                    </xsl:when>
                    <!-- just trim first -->
                    <xsl:when test="$trimFirst and not($trimLast)">
                        <xsl:choose>
                            <!-- do we need to trim the first two actually -->
                            <xsl:when test="$trimFirstTwo">
                                <xsl:call-template name="constructModel">
                                    <xsl:with-param name="term" select="substring($term,3)"/>
                                </xsl:call-template>
                            </xsl:when>
                            <!-- just trim first -->
                            <xsl:otherwise>
                                <xsl:call-template name="constructModel">
                                    <xsl:with-param name="term" select="substring($term,2)"/>
                                </xsl:call-template>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <!-- trim first and last -->
                    <xsl:when test="$trimFirst and $trimLast">
                        <xsl:choose>
                            <!-- do we need to trim the first two actually -->
                            <xsl:when test="$trimFirstTwo">
                                <xsl:call-template name="constructModel">
                                    <xsl:with-param name="term"
                                        select="substring($term,3,($termLength)-3)"/>
                                </xsl:call-template>
                            </xsl:when>
                            <!-- just trim first -->
                            <xsl:otherwise>
                                <xsl:call-template name="constructModel">
                                    <xsl:with-param name="term"
                                        select="substring($term,2,($termLength)-2)"/>
                                </xsl:call-template>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <!-- relation is exact so ouput term as specified -->
            <xsl:otherwise>
                <xsl:call-template name="constructModel">
                    <xsl:with-param name="term" select="$term"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes the index node                -->
    <!-- ************************************** -->
    <xsl:template match="index">
        <!--  Extract the prefix(shortID) and attribute ID to search for the semantic
              This extracts around the first period in the index. Hence

             dc.creator      = shortID of "dc" and nameID of "creator"
             bath.dc.creator = shortID of "bath" and nameID of "dc.creator"

          -->
        <xsl:variable name="index" select="."/>
        <xsl:variable name="shortID" select="substring-before($index,'.')"/>
        <xsl:variable name="nameID" select="substring-after($index,'.')"/>
        <xsl:choose>
            <xsl:when test="not($shortID)">
                <!-- Get the BIB1 Attribute name for just and Name using index value -->
                <xsl:variable name="attributeName"
                    select="$contextSets/contextSet/indexes/index[@name=$index and
                    @attributeSet='bib1']/@useAttribute"/>
                <!-- construct semantic from found use attribute -->
                <xsl:call-template name="constructSemantic">
                    <xsl:with-param name="useAttribute" select="$attributeName"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <!-- Get the BIB1 Attribute name for the shortId and NameID -->
                <xsl:variable name="attributeName"
                    select="$contextSets/contextSet[@shortId=$shortID]/indexes/index
                    [@name=$nameID and @attributeSet='bib1']/@useAttribute"/>
                <!-- construct semantic from found use attribute -->
                <xsl:call-template name="constructSemantic">
                    <xsl:with-param name="useAttribute" select="$attributeName"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ************************************** -->
    <!-- Processes the relation node            -->
    <!-- ************************************** -->
    <xsl:template match="relation">
        <!-- Process modifiers looking for relation  -->
        <xsl:variable name="relationModifier" select="./modifiers/modifier/type[text()=
            $contextSets/contextSet[@shortId='cql']/relations/relation/@name]"/>
        <xsl:choose>
            <!-- we did not find relation modifier so search using relation type -->
            <xsl:when test="not($relationModifier)">
                <!-- construct relation from found relation attribute -->
                <xsl:call-template name="constructRelation">
                    <xsl:with-param name="relationType" select="./value"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <!-- construct relation from found relation attribute -->
                <xsl:call-template name="constructRelation">
                    <xsl:with-param name="relationType" select="$relationModifier"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
        <!-- now check for structure -->
        <xsl:call-template name="constructStructure"/>
    </xsl:template>
    <!-- ************************************************** -->
    <!-- Processes the relation node for structure content  -->
    <!-- ************************************************** -->
    <xsl:template name="constructStructure">
        <xsl:variable name="relationType" select="./value"/>
        <!-- does the relation type match to a structure -->
        <xsl:variable name="structureAttribute"
            select="$contextSets/contextSet[@shortId='cql']/structures/structure[@name=$relationType
            and @attributeSet='cql']/@structureAttribute"/>
        <xsl:choose>
            <xsl:when test="not($structureAttribute)">
                <!-- Search for first modifier that maps to a structure type -->
                <xsl:variable name="structureModifier" select="./modifiers/modifier/type
                    [substring-after(text(),'.')=$contextSets/contextSet[@shortId='cql']
                    /structures/structure/@name]"/>
                <!-- only output structure if we get a match otherwise ignore structure altogether -->
                <xsl:if test="$structureModifier">
                    <structure>
                        <xsl:value-of select="$contextSets/contextSet[@shortId='cql']
                            /structures/structure[@name=
                            substring-after($structureModifier[position()='1']/text(),'.')
                            and @attributeSet='cql']/@structureAttribute"/>
                    </structure>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <!-- construct structure from found structure attribute -->
                <structure>
                    <xsl:value-of select="$structureAttribute"/>
                </structure>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ****************************************************************** -->
    <!-- comstructs the semantic for the supplied use attribute             -->
    <!-- ****************************************************************** -->
    <xsl:template name="constructSemantic">
        <xsl:param name="useAttribute"/>
        <!-- Get the BIB1 semantic value for the specified bib1 use attribute name-->
        <xsl:variable name="semanticValue"
            select="$bibAttribSet/attributeType[@name='semantic']/attribute
            [@name=$useAttribute]/@value"/>
        <!-- make sure we found a matching semantic value  -->
        <xsl:if test="not($semanticValue)">
            <xsl:message terminate="yes">
                <xsl:text>Unable to locate matching BIB1 semantic index</xsl:text>
            </xsl:message>
        </xsl:if>
        <semantic>
            <xsl:value-of select="$semanticValue"/>
        </semantic>
    </xsl:template>
    <!-- ****************************************************************** -->
    <!-- constructs the relation for the supplied relation attribute        -->
    <!-- ****************************************************************** -->
    <xsl:template name="constructRelation">
        <xsl:param name="relationType"/>
        <!-- Get the BIB1 relation value for the specified bib1 use attribute name-->
        <xsl:variable name="relationValue"
            select="$contextSets/contextSet[@shortId='cql']/relations/relation
            [@name=$relationType[position()='1']/text()]/@relationAttribute"/>
        <!-- make sure we found a matching semantic value  -->
        <xsl:if test="not($relationValue)">
            <xsl:message terminate="yes">
                <xsl:text>Unable to locate matching BIB1 relation</xsl:text>
            </xsl:message>
        </xsl:if>
        <relation>
            <xsl:value-of select="$relationValue"/>
        </relation>
    </xsl:template>
    <!-- ****************************************************************** -->
    <!-- constructs the truncation and position for the supplied term       -->
    <!-- ****************************************************************** -->
    <xsl:template name="constructTruncationPosition">
        <xsl:param name="term"/>
        <!-- get the last character as for some reason the end-with function is not found -->
        <xsl:variable name="lastChar" select="substring($term,string-length($term))"/>
        <!-- Detect if first in field needs adding -->
        <xsl:variable name="firstInField" select="starts-with($term,'^^') or
            (starts-with($term,'^') and not(starts-with($lastChar,'^'))) "/>
        <!-- Detect if left Truncation needs adding -->
        <xsl:variable name="leftTrunc" select="starts-with($term,'*') or starts-with($term,'^*')"/>
        <!-- Detect if right Truncation needs adding -->
        <xsl:variable name="rightTrunc" select="starts-with($lastChar,'*')"/>
        <!-- Detect no Truncation needs adding -->
        <xsl:variable name="noTrunc" select="(starts-with($term,'^') or starts-with($term,'^^'))
            and starts-with($lastChar,'^') "/>
        <!-- Do we need to set first in field -->
        <xsl:if test="$firstInField">
            <xsl:call-template name="constructPosition">
                <xsl:with-param name="position" select="$contextSets/contextSet[@shortId='cql']
                    /positions/position[@name='first' and
                    @attributeSet='bib1']/@positionAttribute"/>
            </xsl:call-template>
        </xsl:if>
        <!-- now apply truncation -->
        <xsl:choose>
            <!-- no truncation -->
            <xsl:when test="$noTrunc">
                <xsl:call-template name="constructTruncation">
                    <xsl:with-param name="truncation"
                        select="$contextSets/contextSet[@shortId='cql']
                        /truncations/truncation[@name='none' and
                        @attributeSet='bib1']/@truncationAttribute"/>
                </xsl:call-template>
            </xsl:when>
            <!-- both left and right truncation -->
            <xsl:when test="$leftTrunc and $rightTrunc">
                <xsl:call-template name="constructTruncation">
                    <xsl:with-param name="truncation"
                        select="$contextSets/contextSet[@shortId='cql']
                        /truncations/truncation[@name='both' and
                        @attributeSet='bib1']/@truncationAttribute"/>
                </xsl:call-template>
            </xsl:when>
            <!-- left only truncation -->
            <xsl:when test="$leftTrunc and not($rightTrunc)">
                <xsl:call-template name="constructTruncation">
                    <xsl:with-param name="truncation"
                        select="$contextSets/contextSet[@shortId='cql']
                        /truncations/truncation[@name='left' and
                        @attributeSet='bib1']/@truncationAttribute"/>
                </xsl:call-template>
            </xsl:when>
            <!-- right only truncation -->
            <xsl:when test="not($leftTrunc) and $rightTrunc">
                <xsl:call-template name="constructTruncation">
                    <xsl:with-param name="truncation"
                        select="$contextSets/contextSet[@shortId='cql']
                        /truncations/truncation[@name='right' and
                        @attributeSet='bib1']/@truncationAttribute"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- ************************* -->
    <!-- constructs position       -->
    <!-- ************************* -->
    <xsl:template name="constructPosition">
        <xsl:param name="position"/>
        <!-- make sure we matched up a position value -->
        <xsl:if test="not($position)">
            <xsl:message terminate="yes">
                <xsl:text>Unable to locate matching BIB1 position</xsl:text>
            </xsl:message>
        </xsl:if>
        <position>
            <xsl:value-of select="$position"/>
        </position>
    </xsl:template>
    <!-- ************************* -->
    <!-- constructs truncation     -->
    <!-- ************************* -->
    <xsl:template name="constructTruncation">
        <xsl:param name="truncation"/>
        <!-- make sure we matched up a position value -->
        <xsl:if test="not($truncation)">
            <xsl:message terminate="yes">
                <xsl:text>Unable to locate matching BIB1 truncation</xsl:text>
            </xsl:message>
        </xsl:if>
        <truncation>
            <xsl:value-of select="$truncation"/>
        </truncation>
    </xsl:template>
    <!-- ************************* -->
    <!-- constructs a model     -->
    <!-- ************************* -->
    <xsl:template name="constructModel">
        <xsl:param name="term"/>
        <model>
            <xsl:value-of select="$term"/>
        </model>
    </xsl:template>
</xsl:stylesheet>
