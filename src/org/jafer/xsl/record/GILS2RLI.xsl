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
    Purpose: Mapping  Global Information Locator Service (GILS) metadata
            to resource metadata based on the IMS RLI XML Binding 
              
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0" 
                xmlns:str="http://exslt.org/strings" extension-element-prefixes="str">
                               
<xsl:import href="http://devil.lib.ed.ac.uk:8080/resources/stylesheet/str.replace.template.xsl"/>

<xsl:template match="gils">

  <resource xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0 http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0.xsd">
  
    <xsl:if test="Local-Control-Number">
    <indexId>
        <xsl:value-of select="Local-Control-Number"/>
    </indexId>
    </xsl:if>
  
    <xsl:if test="Control-Identifier">
    <indexId>
        <xsl:value-of select="Control-Identifier"/>
    </indexId>
    </xsl:if>

    <!-- mapping identifier from copac records -->
    <xsl:if test="_3_OriginalRecords">
    <indexId>
        <xsl:value-of select="_3_OriginalRecords/_3_OriginalRecords_1_13/_3_OriginalRecords_1_13_1_14"/>
    </indexId>
    </xsl:if>
    
    <!-- mapping identifier and metadata creators from RDN records -->
    <xsl:if test="_3_RECORD">
    <indexId>
        <xsl:value-of select="_3_RECORD/_3_RECORD_3_HEADER/_3_RECORD_3_HEADER_3_IDENTIFIER"/>
    </indexId>
    </xsl:if>
      
  <resourceMetadata>
<!-- *** description  *** -->
    <xsl:if test="Abstract">
    <description>
        <metadataLangString><xsl:value-of select="Abstract"/></metadataLangString>
    </description>
    </xsl:if>

    <xsl:if test="Resource-Description">
    <description>
        <metadataLangString><xsl:value-of select="Resource-Description"/></metadataLangString>
    </description>
    </xsl:if>
    
    <!-- RDN description -->
    <xsl:if test="_3_RECORD">
    <description>
        <metadataLangString>
            <xsl:value-of select="normalize-space(_3_RECORD/_3_RECORD_3_METADATA/_3_RECORD_3_METADATA_3_DC/_3_RECORD_3_METADATA_3_DC_3_DESCRIPTION)"/>
        </metadataLangString>
    </description>
    </xsl:if>
    
    <!-- copac description -->
    <xsl:if test="_3_Notes">
    <description>
        <metadataLangString>
        <xsl:for-each select="_3_Notes/_3_Notes_3_Note"> 
            <xsl:value-of select="."/><xsl:text> </xsl:text>
        </xsl:for-each>    
        </metadataLangString>
    </description>
    </xsl:if>
    
    <!-- british library description -->
    <xsl:if test="_3_Note">
    <description>
        <metadataLangString>
         <xsl:for-each select="_3_Note"> 
            <xsl:value-of select="."/><xsl:text> </xsl:text>
        </xsl:for-each>
        </metadataLangString>
    </description>
    </xsl:if>
    
<!-- *** Languages *** -->
    <xsl:if test="Language-Of-Resource">
        <language><xsl:value-of select="Language-Of-Resource"/></language>
    </xsl:if>
    
    <xsl:if test="_2_20">
        <language><xsl:value-of select="_2_20"/></language>
    </xsl:if>
    
    <!-- RDN language -->
    <xsl:if test="_3_RECORD">
        <language><xsl:value-of select="normalize-space(_3_RECORD/_3_RECORD_3_METADATA/_3_RECORD_3_METADATA_3_DC/_3_RECORD_3_METADATA_3_DC_3_LANGUAGE)"/></language>
    </xsl:if>
  
    <citation>
<!-- *** titles *** -->
    <xsl:if test="Title">
    <title>
        <metadataLangString><xsl:value-of select="Title"/></metadataLangString>
    </title>
    </xsl:if>
    
    <!-- RDN title -->
    <xsl:if test="_3_RECORD">
    <title>
        <metadataLangString>
            <xsl:value-of select="normalize-space(_3_RECORD/_3_RECORD_3_METADATA/_3_RECORD_3_METADATA_3_DC/_3_RECORD_3_METADATA_3_DC_3_TITLE)"/>
        </metadataLangString>
    </title>
    </xsl:if>
    
<!-- *** Authors *** -->
    <xsl:for-each select="Author">
    <creator>
        <metadataLangString>
           <xsl:value-of select="."/>
        </metadataLangString>
    </creator>
    </xsl:for-each>
    <!-- copac -->
    <xsl:if test="_3_Authors">
        <xsl:for-each select="_3_Authors/_3_Authors_2_2">
        <creator>
        <metadataLangString>
           <xsl:value-of select="."/>
        </metadataLangString>
        </creator>
        </xsl:for-each>
   </xsl:if>
    
<!-- *** Publication *** -->   
    <!--  zetoc, copac  -->
    <xsl:if test="Date-Of-Publication">
        <publicationDate>
            <metadataDate>
                <xsl:value-of select="Date-Of-Publication"/>
            </metadataDate>
        </publicationDate>
    </xsl:if>
    <xsl:if test="_2_31">
        <publisher>
            <metadataLangString>
                <xsl:value-of select="_2_31"/>
            </metadataLangString>
        </publisher>
    </xsl:if>
    
<!-- *** Volume, zetoc *** -->   
    <xsl:if test="_3_VolumeIssue">
        <volumeDesignation>
            <metadataString>
                <xsl:value-of select="_3_VolumeIssue"/>
            </metadataString>
        </volumeDesignation>
    </xsl:if>
    
<!-- *** Part/Issue number, zetoc*** -->   
    <xsl:if test="_3_VolumeIssue">
    <partDesignation>
        <metadataString>
            <xsl:value-of select="_3_VolumeIssue"/>
        </metadataString>
    </partDesignation>   
    </xsl:if>
    
<!-- *** pages number, zetoc*** -->     
    <xsl:if test="_3_Pages">
    <startingPageNumber>
        <metadataString><xsl:value-of select="_3_Pages"/></metadataString>
    </startingPageNumber>
    
    <endingPageNumber>
        <metadataString><xsl:value-of select="_3_Pages"/></metadataString>
    </endingPageNumber>
    </xsl:if>
                
<!-- *** identifiers *** -->
    <!-- mapping ISBN/ISSN from zetoc records -->
    <xsl:if test="_2_28/_2_28_1_19[../_2_28_1_23='ISSN']">
    <standardIdentifier>
        <standardIdentifierType>URI</standardIdentifierType>
        <identifierString>
            <metadataString>urn:ISSN:<xsl:value-of select="_2_28/_2_28_1_19[../_2_28_1_23='ISSN']"/></metadataString>
        </identifierString>
    </standardIdentifier>
    </xsl:if>
    
    <xsl:if test="_2_28/_2_28_1_19[../_2_28_1_23='ISBN']">
    <standardIdentifier>
        <standardIdentifierType>URI</standardIdentifierType>
        <identifierString>
            <metadataString>urn:ISBN:<xsl:value-of select="_2_28/_2_28_1_19[../_2_28_1_23='ISBN']"/></metadataString>
        </identifierString>
    </standardIdentifier>
    </xsl:if>
    
    <!-- mapping identifiers from copac records -->
    <xsl:if test="_3_ISBNISSN">
    <standardIdentifier>
        <standardIdentifierType>URI</standardIdentifierType>
        <identifierString>
            <metadataString>urn:ISBN:<xsl:value-of select="_3_ISBNISSN"/></metadataString>
        </identifierString>
    </standardIdentifier>
    </xsl:if>

<!-- related series title, e.g. lecture notes on computer science series  -->
  <!-- zetoc -->
    <xsl:if test="_2_28/_2_28_1_19[../_2_28_1_23='ISSN'] or _2_28/_2_28_1_19[../_2_28_1_23='ISBN']">
    <relatedTitle>
        <title>
            <metadataLangString>
                <xsl:value-of select="Title/wellKnown[../_2_1_1_23='journal']"/>
            </metadataLangString>
        </title>
        <xsl:if test="_3_VolumeIssue">
        <volumeDesignation>
            <metadataString>
                <xsl:value-of select="_3_VolumeIssue"/>
            </metadataString>
        </volumeDesignation>
        </xsl:if>
        <xsl:if test="_3_VolumeIssue">
        <partDesignation>
            <metadataString>
                <xsl:value-of select="_3_VolumeIssue"/>
            </metadataString>
        </partDesignation>   
        </xsl:if>
    </relatedTitle>
    </xsl:if>
  </citation>

<!-- *** url for electronic resources *** -->
    <xsl:if test="URL">
    <location>
        <locationType>URI</locationType>
        <locator>
            <metadataString><xsl:value-of select="URL"/></metadataString>
        </locator>
    </location>
    </xsl:if>
    
    <xsl:if test="Availability/Available-Linkage/Linkage">
    <location>
        <locationType>URI</locationType>
        <locator>
            <metadataString><xsl:value-of select="Availability/Available-Linkage/Linkage"/></metadataString>
        </locator>
    </location>
    </xsl:if>
    
  <!-- *** physical location *** -->
    <!-- zetoc -->
    <xsl:if test="_2_28/_2_28_1_23 = 'shelfmark'">
    <location>
        <locationType>physical</locationType>
        <locator>
            <metadataString><xsl:value-of select="_2_28/_2_28_1_19[../_2_28_1_23='shelfmark']"/></metadataString>
        </locator>
    </location>
    </xsl:if>
    <!-- copac -->
    <xsl:for-each select="_3_Holdings/_3_Holdings_3_LocationClassmark">
    <location>
        <locationType>physical</locationType>
        <locator>
            <metadataString><xsl:value-of select="."/></metadataString>
        </locator>
    </location>
    </xsl:for-each>
    <!-- british library -->
    <xsl:for-each select="_1_LHI">
    <location>
        <locationType>physical</locationType>
        <locator>
            <metadataString><xsl:value-of select="."/></metadataString>
        </locator>
    </location>
    </xsl:for-each>
    
    <!-- rdn -->
    <xsl:if test="_3_RECORD">
    <xsl:variable name="url_encoded" select="normalize-space(_3_RECORD/_3_RECORD_3_METADATA/_3_RECORD_3_METADATA_3_DC/_3_RECORD_3_METADATA_3_DC_3_IDENTIFIER)"/>
    <xsl:variable name="url_decode_column">
        <xsl:call-template name="str:replace">
               <xsl:with-param name="string" select="$url_encoded" />
               <xsl:with-param name="search" select="'%3A'" />
               <xsl:with-param name="replace" select="':'" />
         </xsl:call-template>
     </xsl:variable>
     <xsl:variable name="url_decode_slash">
        <xsl:call-template name="str:replace">
               <xsl:with-param name="string" select="$url_decode_column" />
               <xsl:with-param name="search" select="'%2F'" />
               <xsl:with-param name="replace" select="'/'" />
         </xsl:call-template>
     </xsl:variable>
     <xsl:variable name="url_decode_ampersand">
        <xsl:call-template name="str:replace">
               <xsl:with-param name="string" select="$url_decode_slash" />
               <xsl:with-param name="search" select="'%26'" />
               <xsl:with-param name="replace" select="'&amp;'" />
         </xsl:call-template>
     </xsl:variable>
     <xsl:variable name="url_decode_equal">
        <xsl:call-template name="str:replace">
               <xsl:with-param name="string" select="$url_decode_ampersand" />
               <xsl:with-param name="search" select="'%3D'" />
               <xsl:with-param name="replace" select="'='" />
         </xsl:call-template>
     </xsl:variable>
     <xsl:variable name="url_decoded">
        <xsl:call-template name="str:replace">
               <xsl:with-param name="string" select="$url_decode_equal" />
               <xsl:with-param name="search" select="'%3F'" />
               <xsl:with-param name="replace" select="'?'" />
         </xsl:call-template>
     </xsl:variable>
     <location>
        <locationType>URI</locationType>
        <locator>
            <metadataString><xsl:value-of select="$url_decoded"/></metadataString>
        </locator>
    </location>
    </xsl:if>
      
  </resourceMetadata>
  
  </resource>

</xsl:template>

</xsl:stylesheet>