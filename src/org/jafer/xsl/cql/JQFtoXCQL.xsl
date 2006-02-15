<!--
     **********************************************************************************
     * This template converts JQF to XCQL                                             *
     *                                                                                *
     * NOTE - THIS TEMPLATE EXPECTS THAT DE-MORGANS LAW HAS BEEN APPLIED TO THE INPUT *
     *        AND THAT DOUBLE NEGATIVES HAVE BEEN RESOLVED. IT DOES NOT EXPECT ANY    *
     *        OR BLOCKS TO CONTAIN NOTs. IT WILL COPE WITH A TOP LEVEL NOT BUT THE    *
     *        RESULTING QUERY MAY TIME OUT ON THE SERVER                              *
     *                                                                                *
     **********************************************************************************
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" encoding="UTF-8" indent="no"/>
    <!-- **************************************************************************** -->
    <!-- Set up variables used for accessing BIB1 attributes and CQL Context Set data -->
    <!-- **************************************************************************** -->
    <!-- Store ref to the cqlcontextsets.xml file -->
    <xsl:variable name="contextSets" select="document('conf/cqlContextSets.xml')/contextSets"/>
    <!-- Store a ref to the bib one attribute set -->
    <xsl:variable name="bibAttribSet"
        select="document('conf/bib1Attributes.xml')/attributeSets/attributeSet[@name='bib1']"/>
    <xsl:variable name="alwaysMatchRelationshipValue"
        select="$bibAttribSet/attributeType[@name='relation']/attribute[@name='always_matches']/@value"/>
    <!-- ************************************************************************ -->
    <!-- WHEN THIS TEMPLATE IS RUN IT WILL START BY MATCHING THE TOP LEVEL NODE.  -->
    <!-- HENCE IT WILL ONLY ENTER THE TEMPLATES - or, and , not , contraintModel  -->
    <!-- ************************************************************************ -->
    <!-- ******************************************************************** -->
    <!-- ******************************************************************** -->
    <!--                    START TOP LEVEL MATCHING TEMPLATES                -->
    <!-- ******************************************************************** -->
    <!-- ******************************************************************** -->
    <!-- ******************************************************************** -->
    <!-- Matches an OR Node                                                   -->
    <!--                                                                      -->
    <!-- This template can be called when the element to be proccessed is:    -->
    <!--     a) the first node in the tree (the root of the query)            -->
    <!--     b) is part of a sub query and is being called recursivley        -->
    <!-- The actual proccessing is done in the processOr template as this     -->
    <!-- template just takes care of adding the <XCQL> block round the output -->
    <!-- when we are processing the root of the query                         -->
    <!-- ******************************************************************** -->
    <xsl:template match="or">
        <xsl:choose>
            <!-- If the node does not have a parent then call processOr inside
                 an <XCQL> block to form the root of the returned XCQL        -->
            <xsl:when test="not(parent::*)">
                <XCQL>
                    <xsl:call-template name="processOr"/>
                </XCQL>
            </xsl:when>
            <!-- We are being called recursively to process an OR node in
                 a sub query so just call the processOr template              -->
            <xsl:otherwise>
                <xsl:call-template name="processOr"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Matches an AND Node                                                  -->
    <!--                                                                      -->
    <!-- This template can be called when the element to be proccessed is:    -->
    <!--     a) the first node in the tree (the root of the query)            -->
    <!--     b) is part of a sub query and is being called recursivley        -->
    <!-- The actual proccessing is done in the processAnd template as this    -->
    <!-- template just takes care of adding the <XCQL> block round the output -->
    <!-- when we are processing the root of the query                         -->
    <!-- ******************************************************************** -->
    <xsl:template match="and">
        <xsl:choose>
            <!-- If the node does not have a parent then call processAnd inside
                 an <XCQL> block to form the root of the returned XCQL        -->
            <xsl:when test="not(parent::*)">
                <XCQL>
                    <xsl:call-template name="processAnd"/>
                </XCQL>
            </xsl:when>
            <!-- We are being called recursively to process an AND node in
                 a sub query so just call the processAnd template             -->
            <xsl:otherwise>
                <xsl:call-template name="processAnd"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Matches a NOT Node                                                   -->
    <!--                                                                      -->
    <!-- This template can be called when the element to be proccessed is:    -->
    <!--     a) the first node in the tree (the root of the query)            -->
    <!--     b) is part of a sub query and is being called recursivley        -->
    <!-- The actual proccessing is done in the processNot template as this    -->
    <!-- template just takes care of adding the <XCQL> block round the output -->
    <!-- when we are processing the root of the query                         -->
    <!-- ******************************************************************** -->
    <xsl:template match="not">
        <xsl:choose>
            <!-- If the node does not have a parent then call processNot inside
                 an <XCQL> block to form the root of the returned XCQL        -->
            <xsl:when test="not(parent::*)">
                <XCQL>
                    <xsl:call-template name="processNot"/>
                </XCQL>
            </xsl:when>
            <!-- We are being called recursively to process a NOT node in
                 a sub query so just call the processNot template             -->
            <xsl:otherwise>
                <xsl:call-template name="processNot"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Matches a ConstraintModel Node                                       -->
    <!--                                                                      -->
    <!-- This template can be called when the element to be proccessed is:    -->
    <!--     a) the first node in the tree (the root of the query)            -->
    <!--     b) is part of a sub query and is being called recursivley        -->
    <!-- The actual proccessing is done in the processConstraintModel template-->
    <!-- as this template just takes care of adding the <XCQL> block round the-->
    <!-- output when we are processing the root of the query                  -->
    <!-- ******************************************************************** -->
    <xsl:template match="constraintModel">
        <xsl:choose>
            <!-- If the node does not have a parent then call
                 processConstraintModel inside an <XCQL> block to form the
                 root of the returned XCQL                                    -->
            <xsl:when test="not(parent::*)">
                <XCQL>
                    <xsl:call-template name="processConstraintModel"/>
                </XCQL>
            </xsl:when>
            <!-- We are being called recursively to process a ConstraintModel
                 node in a sub query so just call the processConstraintModel
                 template                                                     -->
            <xsl:otherwise>
                <xsl:call-template name="processConstraintModel"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- ******************************************************************** -->
    <!--                    END TOP LEVEL MATCHING TEMPLATES                  -->
    <!-- ******************************************************************** -->
    <!--                                                                      -->
    <!-- ******************************************************************** -->
    <!--                    START TOP LEVEL PROCESS TEMPLATES                 -->
    <!-- ******************************************************************** -->
    <!-- ******************************************************************** -->
    <!-- Processes an OR node creating a triple                               -->
    <!-- This template exists to avoid duplication of code in the OR template -->
    <!-- that matches any OR nodes in the Query                               -->
    <!-- ******************************************************************** -->
    <xsl:template name="processOr">
        <xsl:choose>
            <!-- Neither operand is a NOT node then do a simple and structure -->
            <xsl:when test="not(./not[position()=1])">
                <triple>
                    <boolean>
                        <value>or</value>
                    </boolean>
                    <leftOperand>
                        <xsl:apply-templates select="*[position()=1]"/>
                    </leftOperand>
                    <rightOperand>
                        <xsl:apply-templates select="*[position()=2]"/>
                    </rightOperand>
                </triple>
            </xsl:when>
            <!-- Left operand is a NOT node -->
            <xsl:when test="*[position()=1][self::not] and not(*[position()=2][self::not])">
                <xsl:message terminate="yes">
                    <xsl:text>JQF has not been normailised to remove (A or NOT B) ,  (NOT B or A)</xsl:text>
                </xsl:message>
            </xsl:when>
            <!-- Right operand is a NOT node -->
            <xsl:when test="not(*[position()=1][self::not]) and *[position()=2][self::not]">
                <xsl:message terminate="yes">
                    <xsl:text>JQF has not been normailised to remove (A or NOT B) or (NOT B or A) </xsl:text>
                </xsl:message>
            </xsl:when>
            <!-- Both operands are NOT nodes -->
            <xsl:when test="*[position()=1][self::not] and *[position()=2][self::not]">
                <xsl:message terminate="yes">
                    <xsl:text>JQF has not been normailised to apply demorgans law (NOT A or NOT B) -> Not(A and B) </xsl:text>
                </xsl:message>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Processes an AND node creating a triple                              -->
    <!-- This template exists to avoid duplication of code in the AND         -->
    <!-- template that matches any AND nodes in the Query                     -->
    <!-- ******************************************************************** -->
    <xsl:template name="processAnd">
        <xsl:choose>
            <!-- Neither operand is a NOT node then do a simple and structure -->
            <xsl:when test="not(./not[position()=1])">
                <triple>
                    <boolean>
                        <value>and</value>
                    </boolean>
                    <leftOperand>
                        <xsl:apply-templates select="*[position()=1]"/>
                    </leftOperand>
                    <rightOperand>
                        <xsl:apply-templates select="*[position()=2]"/>
                    </rightOperand>
                </triple>
            </xsl:when>
            <!-- Right operand is a NOT node so change AND to be a NOT -->
            <xsl:when test="not(*[position()=1][self::not]) and *[position()=2][self::not]">
                <triple>
                    <boolean>
                        <value>not</value>
                    </boolean>
                    <leftOperand>
                        <xsl:apply-templates select="*[position()=1]"/>
                    </leftOperand>
                    <rightOperand>
                        <xsl:apply-templates select="*[position()=2]/*"/>
                    </rightOperand>
                </triple>
            </xsl:when>
            <!-- ERROR: Left operand is a NOT node so report error as Jafer query has not been normalised -->
            <xsl:when test="*[position()=1][self::not] and not(*[position()=2][self::not])">
                <xsl:message terminate="yes">
                    <xsl:text>JQF has not been normailised as NOT A AND B would have been convertered to B AND NOT A</xsl:text>
                </xsl:message>
            </xsl:when>
            <!-- ERROR: Both operand are NOT nodes -->
            <xsl:when test="*[position()=1][self::not] and *[position()=2][self::not]">
                <xsl:message terminate="yes">
                    <xsl:text>JQF has not been normailised to apply demorgans law (NOT A and NOT B) -> Not(A or B)</xsl:text>
                </xsl:message>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Processes a NOT node creating a triple                               -->
    <!-- This template exists to avoid duplication of code in the NOT         -->
    <!-- template that matches any NOT nodes in the Query                      -->
    <!-- ******************************************************************** -->
    <xsl:template name="processNot">
        <!-- Unfortunatly a not at the top level can not be converted
             very easily as NOT in CQL needs a left and right operand
             Hence we will make the left operand be all records. However
             their is a risk that the search may now time out but its
             gives the user a chance rather than an error  -->
        <triple>
            <boolean>
                <value>not</value>
            </boolean>
            <leftOperand>
                <!-- may be make this the cql for all records = 1 -->
                <searchClause>
                    <index>cql.allRecords</index>
                    <relation>
                        <value>=</value>
                    </relation>
                    <term>1</term>
                </searchClause>
            </leftOperand>
            <rightOperand>
                <xsl:apply-templates select="*[position()=1]"/>
            </rightOperand>
        </triple>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Processes a ConstraintModel node creating a search clause            -->
    <!-- This template exists to avoid duplication of code in the             -->
    <!-- ConstraintModel template that matches any ConstraintModel nodes in   -->
    <!-- the Query                                                            -->
    <!-- ******************************************************************** -->
    <xsl:template name="processConstraintModel">
        <searchClause>
            <!-- process the semantic and relationship together
                 as they can not exists with out each other    -->
            <xsl:choose>
                <!-- Normal Case: we have semantic and relationship -->
                <xsl:when test="./constraint/semantic[position()=1] and
                    ./constraint/relation[position()=1]">
                    <xsl:apply-templates select="./constraint/semantic"/>
                    <xsl:apply-templates select="./constraint/relation"/>
                </xsl:when>
                <!-- Exception: we have semantic but no relationship
                     so make it implied equals                       -->
                <xsl:when test="./constraint/semantic[position()=1] and
                    not(./constraint/relation[position()=1])">
                    <!-- Will apply SPECIAL CASE A when processing semantic -->
                    <xsl:apply-templates select="./constraint/semantic"/>
                    <!-- No relationship to process so manually add
                         a simple relationship of equals            -->
                    <relation>
                        <value>=</value>
                        <!-- do we have a structure constraint to process that must sit in a modifiers block -->
                        <xsl:if test="./constraint/structure">
                            <modifiers>
                                <xsl:apply-templates select="./constraint/structure"/>
                            </modifiers>
                        </xsl:if>
                    </relation>
                </xsl:when>
                <!-- ERROR: we have no semantic but have relationship so report an error -->
                <xsl:when test="not(./constraint/semantic[position()=1]) and
                    ./constraint/relation[position()=1]">
                    <xsl:message terminate="yes">
                        <xsl:text>JQF is invalid as it has a relation without a semantic</xsl:text>
                    </xsl:message>
                </xsl:when>
            </xsl:choose>
            <!-- Finally process the model/term and its constraints -->
            <xsl:apply-templates select="./model"/>
        </searchClause>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- ******************************************************************** -->
    <!--                    END TOP LEVEL PROCESS TEMPLATES                   -->
    <!-- ******************************************************************** -->
    <!--                                                                      -->
    <!-- ******************************************************************** -->
    <!--                    START UTILITY MATCHING TEMPLATES                  -->
    <!--                                                                      -->
    <!-- The following templates are designed to support the top level        -->
    <!-- process templates and should never be directly called due to a top   -->
    <!-- level element match as this would result in a error or unexpected    -->
    <!-- behaviour                                                            -->
    <!-- ******************************************************************** -->
    <!-- ******************************************************************** -->
    <!-- ******************************************************************** -->
    <!-- Processes any match on a semantic node                               -->
    <!-- This template is only called as part of processing a ConstraintModel -->
    <!-- ******************************************************************** -->
    <xsl:template match="semantic">
        <xsl:choose>
            <!-- SPECIAL CASE A: If the relationship is defined and is always_matches
                 then we must put allrecords instead                  -->
            <xsl:when test="../relation[position()=1] = $alwaysMatchRelationshipValue">
                <index>cql.allRecords</index>
            </xsl:when>
            <!-- Process as normal smantic node -->
            <xsl:otherwise>
                <!-- Get the semantic value to lookup in the bib1 attributes file -->
                <xsl:variable name="semanticValue" select="."/>
                <!-- Get the name of the bib1 attribute specified by the semantic value -->
                <xsl:variable name="semanticBib1Name"
                    select="$bibAttribSet/attributeType[@name='semantic']/attribute
                    [@value=$semanticValue]/@name"/>
                <!-- Find the first matching useAttribute in the cqlContextSets file
                     NOTE: This will locate the first matching value in the file and
                           use it as the context so the ordering in the ContextSets
                           file should be in order of inportance if a bib1 attributes
                           exists in more than one context set                        -->
                <xsl:variable name="CQLIndex"
                    select="$contextSets/contextSet/indexes/index[@useAttribute=$semanticBib1Name
                    and @attributeSet='bib1']/."/>
                <!-- make sure we found a matching index in the context set file -->
                <xsl:if test="not($CQLIndex)">
                    <xsl:message terminate="yes">
                        <xsl:text>Unable to locate matching CQL semantic index</xsl:text>
                    </xsl:message>
                </xsl:if>
                <!-- Finally process the CQL context set index into the format of shortid.indexname -->
                <index>
                    <xsl:value-of select="$CQLIndex/../../@shortId"/>
                    <xsl:text>.</xsl:text>
                    <xsl:value-of select="$CQLIndex/@name"/>
                </index>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Processes any match on a relation node                               -->
    <!-- This template is only called as part of processing a ConstraintModel -->
    <!-- ******************************************************************** -->
    <xsl:template match="relation">
        <relation>
            <!-- Get the relation value to lookup in the bib1 & cql attribute files -->
            <xsl:variable name="relationValue" select="."/>
            <!-- get the appropriate symbol from the bib1attributes as this tells
                 us later if we need to create a modifier or not (see comment below) -->
            <xsl:variable name="relationBib1Symbol"
                select="$bibAttribSet/attributeType[@name='relation']/attribute
                [@value=$relationValue]/@symbol"/>
            <!-- Get the CQL relation attributes name for the relation value -->
            <xsl:variable name="CQLRelation"
                select="$contextSets/contextSet[@shortId='cql']/relations/relation
                [@relationAttribute=$relationValue and @attributeSet='bib1']/@name"/>
            <!-- make sure we found a matching relation as we can not do anything with out it-->
            <xsl:if test="not($CQLRelation)">
                <xsl:message terminate="yes">
                    <xsl:text>Unable to locate matching CQL relation</xsl:text>
                </xsl:message>
            </xsl:if>
            <xsl:choose>
                <!-- SPECIAL CASE A: If the relationship is defined and is always_matches
                     then we must put equals as term will become 1  -->
                <xsl:when test=". = $alwaysMatchRelationshipValue">
                    <value>=</value>
                </xsl:when>
                <!-- A relationship can be of one of two types

                         * A symbol ( = , <> , <= , => , < , > , etc )
                         * A modifier (stem , phonetic , etc )

                     Modifiers are defined in the bib1 attribute files as not having a symbol
                     Hence if we did not find a symbol for this relationship then we have a
                     relation of type modifier.

                     When we have a modifer we have to create a basic equals relationship and
                     add the actual modifier in a modifiers block -->
                <xsl:when test="not($relationBib1Symbol)">
                    <value>=</value>
                    <modifiers>
                        <modifier>
                            <type>
                                <xsl:value-of select="$CQLRelation"/>
                            </type>
                        </modifier>
                        <!-- do we have a structure constraint to process that
                             must sit in the modifiers block as well           -->
                        <xsl:if test="../structure">
                            <xsl:apply-templates select="../structure"/>
                        </xsl:if>
                    </modifiers>
                </xsl:when>
                <!-- We have a relationship of type Symbol rather than a modifier -->
                <xsl:otherwise>
                    <value>
                        <xsl:value-of select="$CQLRelation"/>
                    </value>
                    <!-- do we have a structure constraint to process that
                         must sit in the modifiers block                    -->
                    <xsl:if test="../structure">
                        <modifiers>
                            <xsl:apply-templates select="../structure"/>
                        </modifiers>
                    </xsl:if>
                </xsl:otherwise>
            </xsl:choose>
        </relation>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Processes any match on a structure node                              -->
    <!-- This template is only called as part of processing a ConstraintModel -->
    <!-- ******************************************************************** -->
    <xsl:template match="structure">
        <!-- Get the structure value to lookup in the cql attributes file -->
        <xsl:variable name="structureValue" select="."/>
        <!-- Get the CQL Structure element for the structure value -->
        <xsl:variable name="CQLStructure"
            select="$contextSets/contextSet[@shortId='cql']/structures/structure
            [@structureAttribute=$structureValue and @attributeSet='bib1']"/>
        <xsl:choose>
            <!-- make sure we found a matching structure as we can not do anything with out it-->
            <xsl:when test="not($CQLStructure)">
                <xsl:message terminate="yes">
                    <xsl:text>Unable to locate matching CQL structure</xsl:text>
                </xsl:message>
            </xsl:when>
            <!-- Add the structure as a modifier -->
            <xsl:otherwise>
                <modifier>
                    <type>
                        <xsl:value-of select="$CQLStructure/../../@shortId"/>
                        <xsl:text>.</xsl:text>
                        <xsl:value-of select="$CQLStructure/@name"/>
                    </type>
                </modifier>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Processes any match on a model node                                  -->
    <!-- This template is only called as part of processing a ConstraintModel -->
    <!-- ******************************************************************** -->
    <xsl:template match="model">
        <term>
            <xsl:choose>
                <!-- SPECIAL CASE A: If the relationship is defined and is always_matches
                                     then we must put equals as term will become 1  -->
                <xsl:when test="../constraint/relation = $alwaysMatchRelationshipValue">1</xsl:when>
                <!-- Do we have trunctation, position or completeness to apply to the term -->
                <xsl:when test="../constraint/truncation[position()=1] or
                    ../constraint/position[position()=1] or
                    ../constraint/completeness[position()=1]">
                    <!-- first apply position as this takes priority -->
                    <xsl:apply-templates select="../constraint/position"/>
                    <!-- we now have to do an OR on the completness and truncation values -->
                    <xsl:choose>
                        <!-- We have truncation but not completness -->
                        <xsl:when test="../constraint/truncation[position()=1] and
                            not(../constraint/completeness[position()=1])  ">
                            <!-- Apply just truncation -->
                            <xsl:apply-templates select="../constraint/truncation"/>
                        </xsl:when>
                        <!-- We have completness but not truncation-->
                        <xsl:when test="../constraint/completeness[position()=1] and
                            not(../constraint/truncation[position()=1])">
                            <!-- Apply just completeness -->
                            <xsl:apply-templates select="../constraint/completeness"/>
                        </xsl:when>
                        <!-- We have both so need to work out which takes priority-->
                        <xsl:when test="../constraint/truncation[position()=1] and
                            ../constraint/completeness[position()=1]  ">
                            <!-- Get the completeness value to lookup in the cql attributes file -->
                            <xsl:variable name="completenessValue"
                                select="../constraint/completeness"/>
                            <!-- Get the CQL completeness element for the completeness value -->
                            <xsl:variable name="CQLCompleteness"
                                select="$contextSets/contextSet[@shortId='cql']/completenesses/completeness
                                [@completenessAttribute=$completenessValue and
                                @attributeSet='bib1']/@name"/>
                            <xsl:choose>
                                <!-- make sure we found a matching completeness as we can not do anything with out it-->
                                <xsl:when test="not($CQLCompleteness)">
                                    <xsl:message terminate="yes">
                                        <xsl:text>Unable to locate matching CQL completeness</xsl:text>
                                    </xsl:message>
                                </xsl:when>
                                <!-- when completeness is incomplete then completeness rules take priority -->
                                <xsl:when test="$CQLCompleteness='incomplete'">
                                    <xsl:apply-templates select="../constraint/completeness"/>
                                </xsl:when>
                                <!-- when completeness is complete truncation rules take priority-->
                                <xsl:when test="$CQLCompleteness='complete'">
                                    <xsl:apply-templates select="../constraint/truncation"/>
                                </xsl:when>
                            </xsl:choose>
                        </xsl:when>
                        <!-- normal model/term as only had position -->
                        <xsl:otherwise>
                            <xsl:value-of select="."/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <!-- normal model/term processing -->
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </term>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Processes any match on a position node                               -->
    <!-- This template is only called as part of processing a ConstraintModel -->
    <!-- ******************************************************************** -->
    <xsl:template match="position">
        <!-- Get the position value to lookup in the cql attributes file -->
        <xsl:variable name="positionValue" select="."/>
        <!-- Get the CQL Structure element for the position value -->
        <xsl:variable name="CQLPosition"
            select="$contextSets/contextSet[@shortId='cql']/positions/position
            [@positionAttribute=$positionValue and @attributeSet='bib1']/@name"/>
        <xsl:choose>
            <!-- make sure we found a matching position as we can not do anything with out it-->
            <xsl:when test="not($CQLPosition)">
                <xsl:message terminate="yes">
                    <xsl:text>Unable to locate matching CQL position</xsl:text>
                </xsl:message>
            </xsl:when>
            <!-- process first in field all other values are ignored -->
            <xsl:when test="$CQLPosition='first'">
                <xsl:text>^</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Processes any match on a truncation node                             -->
    <!-- This template is only called as part of processing a ConstraintModel -->
    <!-- ******************************************************************** -->
    <xsl:template match="truncation">
        <!-- Get the truncation value to lookup in the cql attributes file -->
        <xsl:variable name="truncationValue" select="."/>
        <!-- Get the CQL Truncation element for the truncation value -->
        <xsl:variable name="CQLTruncation"
            select="$contextSets/contextSet[@shortId='cql']/truncations/truncation
            [@truncationAttribute=$truncationValue and @attributeSet='bib1']/@name"/>
        <xsl:choose>
            <!-- make sure we found a matching truncation as we can not do anything with out it-->
            <xsl:when test="not($CQLTruncation)">
                <xsl:message terminate="yes">
                    <xsl:text>Unable to locate matching CQL truncation</xsl:text>
                </xsl:message>
            </xsl:when>
            <!-- truncate left -->
            <xsl:when test="$CQLTruncation='left'">
                <xsl:text>*</xsl:text>
                <xsl:value-of select="../../model"/>
            </xsl:when>
            <!-- truncate right -->
            <xsl:when test="$CQLTruncation='right'">
                <xsl:value-of select="../../model"/>
                <xsl:text>*</xsl:text>
            </xsl:when>
            <!-- truncate both -->
            <xsl:when test="$CQLTruncation='both'">
                <xsl:text>*</xsl:text>
                <xsl:value-of select="../../model"/>
                <xsl:text>*</xsl:text>
            </xsl:when>
            <!-- truncate none -->
            <xsl:when test="$CQLTruncation='none'">
                <xsl:text>^</xsl:text>
                <xsl:value-of select="../../model"/>
                <xsl:text>^</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- ******************************************************************** -->
    <!-- Processes any match on a completeness node                           -->
    <!-- This template is only called as part of processing a ConstraintModel -->
    <!-- ******************************************************************** -->
    <xsl:template match="completeness">
        <!-- Get the completeness value to lookup in the cql attributes file -->
        <xsl:variable name="completenessValue" select="."/>
        <!-- Get the CQL completeness element for the completeness value -->
        <xsl:variable name="CQLCompleteness"
            select="$contextSets/contextSet[@shortId='cql']/completenesses/completeness
            [@completenessAttribute=$completenessValue and @attributeSet='bib1']/@name"/>
        <xsl:choose>
            <!-- make sure we found a matching completeness as we can not do anything with out it-->
            <xsl:when test="not($CQLCompleteness)">
                <xsl:message terminate="yes">
                    <xsl:text>Unable to locate matching CQL completeness</xsl:text>
                </xsl:message>
            </xsl:when>
            <!-- completeness is incomplete -->
            <xsl:when test="$CQLCompleteness='incomplete'">
                <xsl:text>*</xsl:text>
                <xsl:value-of select="../../model"/>
                <xsl:text>*</xsl:text>
            </xsl:when>
            <!-- completeness is complete -->
            <xsl:when test="$CQLCompleteness='complete'">
                <xsl:text>^</xsl:text>
                <xsl:value-of select="../../model"/>
                <xsl:text>^</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
