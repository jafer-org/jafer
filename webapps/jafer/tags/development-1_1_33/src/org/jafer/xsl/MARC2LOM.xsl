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
    MARC2LOM xslt stylesheet version 1.0
    Purpose: Mapping OAI MARC metadata to LOM based on the IEEE XML Binding 
             (April 2004]. LOM is the default metadata schema for 
             presenting search results from d+ web services and toolkit. 
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://ltsc.ieee.org/xsd/LOM" 
                xmlns:oai_marc="http://www.openarchives.org/OAI/1.1/oai_marc" exclude-result-prefixes="oai_marc">

<xsl:template match="oai_marc:oai_marc">

  <lom xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd">
 
  <general>
    
<!-- *** identifiers *** -->

    <!-- isbn 20 -->
    <xsl:if test="oai_marc:varfield[@id='020']">
        <xsl:for-each select="oai_marc:varfield[@id='020']">
        <xsl:if test="oai_marc:subfield[@label='a']">
        <identifier>
            <catalog>URI</catalog>
            <entry>urn:ISBN:<xsl:value-of select="oai_marc:subfield[@label='a']"/></entry>
        </identifier>
        </xsl:if>
        </xsl:for-each>
    </xsl:if>
    
    <!-- issn 22 -->
    <xsl:if test="oai_marc:varfield[@id='022']">
        <xsl:for-each select="oai_marc:varfield[@id='022']">
        <xsl:if test="oai_marc:subfield[@label='a']">
        <identifier>
            <catalog>URI</catalog>
            <entry>urn:ISSN:<xsl:value-of select="oai_marc:subfield[@label='a']"/></entry>
        </identifier>
        </xsl:if>
        </xsl:for-each>
    </xsl:if>
    
    <!-- library of congress control number 10 -->
    <xsl:if test="oai_marc:varfield[@id='010']/oai_marc:subfield[@label='a']">
        <identifier>
            <catalog>URI</catalog>
            <entry>info:lccn:<xsl:value-of select="oai_marc:varfield[@id='010']/oai_marc:subfield[@label='a']"/></entry>
        </identifier>
    </xsl:if>
    
    <!-- oclc control number 19 -->
    <xsl:if test="oai_marc:varfield[@id='019']/oai_marc:subfield[@label='a']">
        <identifier>
            <catalog>URI</catalog>
            <entry>info:oclcnum:<xsl:value-of select="oai_marc:varfield[@id='019']/oai_marc:subfield[@label='a']"/></entry>
        </identifier>
    </xsl:if>
    
    <!-- other unique idenfifier 24 -->
    <xsl:if test="oai_marc:varfield[@id='024']">
    <xsl:for-each select="oai_marc:varfield[@id='024']">
    <xsl:choose>
      <xsl:when test="self::node()/@oai_marc:i1='0'">
        <identifier>
            <catalog>ISRC</catalog>
            <entry><xsl:value-of select="oai_marc:subfield[@label='a']"/></entry>
        </identifier>
      </xsl:when>
      <xsl:when test="self::node()/@oai_marc:i1='1'">
        <identifier>
            <catalog>UPC</catalog>
            <entry><xsl:value-of select="oai_marc:subfield[@label='a']"/></entry>
        </identifier>
      </xsl:when>
      <xsl:when test="self::node()/@oai_marc:i1='2'">
        <identifier>
            <catalog>ISMN</catalog>
            <entry><xsl:value-of select="oai_marc:subfield[@label='a']"/></entry>
        </identifier>
      </xsl:when>
      <xsl:when test="self::node()/@oai_marc:i1='3'">
        <identifier>
            <catalog>IAN</catalog>
            <entry><xsl:value-of select="oai_marc:subfield[@label='a']"/></entry>
        </identifier>
      </xsl:when>
      <xsl:when test="self::node()/@oai_marc:i1='4'">
        <identifier>
            <catalog>URI</catalog>
            <entry>info:sici:<xsl:value-of select="oai_marc:subfield[@label='a']"/></entry>
        </identifier>
      </xsl:when>
      <xsl:when test="self::node()/@oai_marc:i1='7'">
        <identifier>
            <catalog><xsl:value-of select="oai_marc:subfield[@label='2']"/></catalog>
            <entry><xsl:value-of select="oai_marc:subfield[@label='a']"/></entry>
        </identifier>
      </xsl:when>
      <xsl:when test="self::node()/@oai_marc:i1='8'">
        <identifier>
            <catalog>unknown</catalog>
            <entry><xsl:value-of select="oai_marc:subfield[@label='a']"/></entry>
        </identifier>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
  </xsl:if>
  
  <!-- electronic location - url 856 -->
  <xsl:if test="oai_marc:varfield[@id='856']">
  <xsl:for-each select="oai_marc:varfield[@id='856']/oai_marc:subfield[@label='u']">
    <identifier>
            <catalog>URI</catalog>
            <entry><xsl:value-of select="."/></entry>
    </identifier>
  </xsl:for-each>
  </xsl:if>  
    
<!-- *** titles *** -->

  <!-- feasible MARC fields: 130, 210, 240, 242, 245, 246, 247, 730, 740 -->
  <xsl:if test="oai_marc:varfield[@id='130' or @id='210' 
  or @id='222' or @id='240' or @id='242' or @id='245' 
  or @id='246' or @id='247' or @id='730' or @id='740']">
  
  <!-- maps the mandantory 245, other fields pending -->
  <title>
  <xsl:for-each select="oai_marc:varfield[@id='245']">
    <string language="en">
        <xsl:for-each select="oai_marc:subfield[@label='a']">
            <xsl:value-of select="."/>
        </xsl:for-each>
        <xsl:for-each select="oai_marc:subfield[@label='b']">
            <xsl:value-of select="."/>
        </xsl:for-each>
        <xsl:for-each select="oai_marc:subfield[@label='n']">
            <xsl:value-of select="."/>
        </xsl:for-each>
        <xsl:for-each select="oai_marc:subfield[@label='p']">
            <xsl:value-of select="."/>
        </xsl:for-each>
        <xsl:for-each select="oai_marc:subfield[@label='s']">
            <xsl:value-of select="."/>
        </xsl:for-each>
    </string>
  </xsl:for-each>
  </title>
  </xsl:if>
  
<!-- *** Languages *** -->
  <!-- 008, 041 -->
  <xsl:if test="oai_marc:fixfield[@id='008'] or oai_marc:varfield[@id='41']">
    <xsl:choose>
    <xsl:when test="oai_marc:varfield[@id='041']/oai_marc:subfield[@label='2'] = 'rfc3066'">
      <language>
        <xsl:value-of select="oai_marc:varfield[@id='041']/oai_marc:subfield[@label='a']"/>
      </language>
    </xsl:when>
    <xsl:when test="oai_marc:varfield[@id='041']">
      <language>
        <xsl:value-of select="oai_marc:varfield[@id='041']/oai_marc:subfield[@label='a']"/>
      </language>
    </xsl:when>
    <xsl:otherwise>
      <language>
        <xsl:value-of select="substring(oai_marc:fixfield[@id='008'], 37, 3)"/>
      </language>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
    
<!--  *** Description *** -->
   <!-- abstract 520 -->
    <xsl:for-each select="oai_marc:varfield[@id='520']">
    <xsl:choose>
      <xsl:when test="oai_marc:subfield[@label='u']">
        <xsl:for-each select="oai_marc:subfield[@label='u']">
          <description>
            <string language="en">
                <xsl:value-of select="ancestor::oai_marc:varfield[@id='520']/oai_marc:subfield[@label='a']"/>
            </string>
         </description>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <description>
            <string language="en">
                <xsl:value-of select="oai_marc:subfield[@label='a']"/>
            </string>
        </description>
      </xsl:otherwise>
    </xsl:choose>
    </xsl:for-each>
    <!-- note 500 -->
    <xsl:for-each select="oai_marc:varfield[@id='500']">
        <description>
            <string language="en">
                <xsl:value-of select="oai_marc:subfield[@label='a']"/>
            </string>
        </description>
    </xsl:for-each>
    <!-- table of content 505 -->
    <xsl:for-each select="oai_marc:varfield[@id='505']">
    <xsl:choose>
      <xsl:when test="oai_marc:subfield[@label='u']">
        <xsl:for-each select="oai_marc:subfield[@label='u']">
          <description>
            <string language="en">
                <xsl:value-of select="ancestor::oai_marc:varfield[@id='505']/oai_marc:subfield[@label='a']"/>
            </string>
          </description>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <description>
            <string language="en">
                <xsl:value-of select="oai_marc:subfield[@label='a']"/>
            </string>
        </description>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <!-- performers 511, venue notes 518 -->
  <xsl:for-each select="oai_marc:varfield[@id='511']">
    <description>
        <string language="en">
            <xsl:value-of select="oai_marc:subfield[@label='a']"/>
        </string>
    </description>
  </xsl:for-each>
  <xsl:for-each select="oai_marc:varfield[@id='518']">
    <description>
        <string language="en">
            <xsl:value-of select="oai_marc:subfield[@label='a']"/>
        </string>
    </description>
  </xsl:for-each>

<!-- *** Keywords *** -->
    <!-- topic terms 650-->
    <xsl:for-each select="oai_marc:varfield[@id='650']">
        <xsl:for-each select="oai_marc:subfield[@label ='a']">
        <keyword>
            <string language="en"><xsl:value-of select="."/></string>
        </keyword>
        </xsl:for-each>
    </xsl:for-each>
    <!-- controlled person terms 600 -->
    <xsl:for-each select="oai_marc:varfield[@id='600']">
        <keyword>
            <string language="en">
                <xsl:if test="oai_marc:subfield[@label='c']"><xsl:value-of select="oai_marc:subfield[@label='c']"/> </xsl:if>
                <xsl:value-of select="oai_marc:subfield[@label='a']"/>
            </string>
        </keyword>
    </xsl:for-each>
    <!-- other controlled terms 610,611,630 -->
    <xsl:for-each select="oai_marc:varfield[@id='610' or @id='611' or @id='630']">
        <keyword>
            <string language="en">
                <xsl:value-of select="oai_marc:subfield[@label='a']"/>
            </string>
        </keyword>
    </xsl:for-each>
    <!-- uncontrolled index terms 653-->
    <xsl:for-each select="oai_marc:varfield[@id='653']">
        <xsl:for-each select="oai_marc:subfield[@label ='a']">
        <keyword>
            <string language="en"><xsl:value-of select="."/></string>
        </keyword>
        </xsl:for-each>
    </xsl:for-each>
    
<!-- *** Coverage *** -->
    <!-- geographical/temporal codes 033/034/043/044/045, pending -->
    <!-- geographical, cartographics 255 -->
    <xsl:for-each select="oai_marc:varfield[@id='255']">
      <xsl:if test="oai_marc:subfield[@label='a']">
        <coverage>
            <string language="en"><xsl:value-of select="oai_marc:subfield[@label='a']"/></string>
        </coverage>
      </xsl:if>
      <xsl:if test="oai_marc:subfield[@label='b']">
         <coverage>
            <string language="en"><xsl:value-of select="oai_marc:subfield[@label='b']"/></string>
        </coverage>
      </xsl:if>
      <xsl:if test="oai_marc:subfield[@label='c']">
         <coverage>
            <string language="en"><xsl:value-of select="oai_marc:subfield[@label='c']"/></string>
        </coverage>
      </xsl:if>
      <xsl:if test="oai_marc:subfield[@label='d']">
         <coverage>
            <string language="en"><xsl:value-of select="oai_marc:subfield[@label='d']"/></string>
        </coverage>
      </xsl:if>
      <xsl:if test="oai_marc:subfield[@label='e']">
         <coverage>
            <string language="en"><xsl:value-of select="oai_marc:subfield[@label='e']"/></string>
        </coverage>
      </xsl:if>
      <xsl:if test="oai_marc:subfield[@label='f']">
         <coverage>
            <string language="en"><xsl:value-of select="oai_marc:subfield[@label='f']"/></string>
        </coverage>
      </xsl:if>
      <xsl:if test="oai_marc:subfield[@label='g']">
         <coverage>
            <string language="en"><xsl:value-of select="oai_marc:subfield[@label='g']"/></string>
        </coverage>
      </xsl:if>
    </xsl:for-each>
    
    <!-- geographical coverage 651 -->
    <xsl:for-each select="oai_marc:varfield[@id='651']">
        <coverage>
            <string language="en"><xsl:value-of select="oai_marc:subfield[@label='a']"/></string>
        </coverage>
    </xsl:for-each>
    
    <!-- other geographical coverage subfield 'z' of 600,610,611,630,650,651  -->
    <xsl:for-each select="oai_marc:varfield[@id='600' or @id='610' 
    or @id='611' or @id='630' or @id='650' 
    or @id='651']/oai_marc:subfield[@label ='z']">
        <coverage>
            <string language="en"><xsl:value-of select="."/></string>
        </coverage>
    </xsl:for-each>
    
    <!-- other temporal coverage subfield 'y' of 600,610,611,630,650,651  -->
     <xsl:for-each select="oai_marc:varfield[@id='600' 
     or @id='610' or @id='611' or @id='630' 
     or @id='650' or @id='651']/oai_marc:subfield[@label ='y']">
        <coverage>
            <string language="en"><xsl:value-of select="."/></string>
        </coverage>
    </xsl:for-each>
    </general>
    
    <lifecycle>
<!-- *** Authors *** -->
    <!-- personal 100 -->
    <xsl:for-each select="oai_marc:varfield[@id='100']">
        <contribute>
            <role>
                <source>LOMv1.0</source>
                <value>author</value>
            </role>
            <entity>
            BEGIN:VCARD\n
            FN:<xsl:if test="oai_marc:subfield[@label='c']"><xsl:value-of select="oai_marc:subfield[@label='c']"/> </xsl:if>
               <xsl:value-of select="oai_marc:subfield[@label='a']"/>\n
            <xsl:if test="oai_marc:subfield[@label='u']">
            ORG:<xsl:value-of select="oai_marc:subfield[@label='u']"/>\n
            </xsl:if>
            END:VCARD\n
            </entity>
        </contribute>
    </xsl:for-each>
    <!-- corporate author 110-->
    <xsl:for-each select="oai_marc:varfield[@id='110']">
        <contribute>
            <role>
                <source>LOMv1.0</source>
                <value>author</value>
            </role>
            <entity>
            BEGIN:VCARD\n
            ORG:<xsl:value-of select="oai_marc:subfield[@label='a']"/>\n
            END:VCARD\n
            </entity>
        </contribute>
    </xsl:for-each>
<!-- *** Publishers *** -->   
    <!--  260 -->
    <xsl:for-each select="oai_marc:varfield[@id='260']/oai_marc:subfield[@label='b']">
        <contribute>
            <role>
                <source>LOMv1.0</source>
                <value>publisher</value>
            </role>
            <entity>
            BEGIN:VCARD\n
            ORG:<xsl:value-of select="."/>\n
            END:VCARD\n
            </entity>
            <date>
                <dateTime><xsl:value-of select="../oai_marc:subfield[@label='c']"/></dateTime>
            </date>
        </contribute>
    </xsl:for-each>
<!-- *** Editors *** -->   
    <!--  700 'common' field for editor -->
    <xsl:for-each select="oai_marc:varfield[@id='700']">
        <contribute>
            <role>
                <source>LOMv1.0</source>
                <value>editor</value>
            </role>
            <entity>
            BEGIN:VCARD\n
            FN:<xsl:if test="oai_marc:subfield[@label='c']"><xsl:value-of select="oai_marc:subfield[@label='c']"/> </xsl:if>
               <xsl:value-of select="oai_marc:subfield[@label='a']"/>\n
            <xsl:if test="oai_marc:subfield[@label='u']">
            ORG:<xsl:value-of select="oai_marc:subfield[@label='u']"/>\n
            </xsl:if>
            END:VCARD\n
            </entity>
        </contribute>
    </xsl:for-each>
<!-- *** Other roles *** --> 
    <!--  uncontrolled 720 name field -->
    <xsl:for-each select="oai_marc:varfield[@id='720']">
        <xsl:choose>
            <xsl:when test="oai_marc:subfield[@label='e']">
            <xsl:choose>
            <xsl:when test="contains(oai_marc:subfield[@label='e'], 'editor')">
                <contribute>
                    <role>
                        <source>LOMv1.0</source>
                        <value>editor</value>
                    </role>
                    <entity>
                    BEGIN:VCARD\n
                    FN:<xsl:value-of select="oai_marc:subfield[@label='a']"/>\n
                    END:VCARD\n
                    </entity>
                </contribute>
            </xsl:when>
            <xsl:when test="contains(oai_marc:subfield[@label='e'], 'author')">
                <contribute>
                    <role>
                        <source>LOMv1.0</source>
                        <value>author</value>
                    </role>
                    <entity>
                    BEGIN:VCARD\n
                    FN:<xsl:value-of select="oai_marc:subfield[@label='a']"/>\n
                    END:VCARD\n
                    </entity>
                </contribute>
            </xsl:when>
            <xsl:otherwise>
                <contribute>
                    <role>
                        <source>elf</source>
                        <value><xsl:value-of select="oai_marc:subfield[@label='e']"/></value>
                    </role>
                    <entity>
                    BEGIN:VCARD\n
                    FN:<xsl:value-of select="oai_marc:subfield[@label='a']"/>\n
                    END:VCARD\n
                    </entity>
                </contribute>
            </xsl:otherwise>
            </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <contribute>
                    <role>
                        <source>LOMv1.0</source>
                        <value>unknown</value>
                    </role>
                    <entity>
                    BEGIN:VCARD\n
                    FN:<xsl:value-of select="oai_marc:subfield[@label='a']"/>\n
                    END:VCARD\n
                    </entity>
                </contribute>
            </xsl:otherwise>
         </xsl:choose>
    </xsl:for-each>
    <!-- other uncontrolled field 720, unmapped due to non specific role in subfield 'e'  -->
    </lifecycle>
    
    <metaMetadata>
<!-- **** Identifiers for metadata record *** -->
    <!-- local control number, 001 -->
        <identifier>
            <catalog>local</catalog>
            <entry><xsl:value-of select="substring-before(substring(oai_marc:fixfield[@id='001'], 2), '&quot;')"/></entry>
        </identifier>
    <!-- Library of Congress control number, 10 -->
    <xsl:if test="oai_marc:varfield[@id='010']">
        <identifier>
            <catalog>URI</catalog>
            <entry>info:lccn:<xsl:value-of select="oai_marc:varfield[@id='010']/oai_marc:subfield[@label='a']"/></entry>
        </identifier>
    </xsl:if>
    <!-- OCLC control number, 19 -->
    <xsl:if test="oai_marc:varfield[@id='019']">
        <identifier>
            <catalog>URI</catalog>
            <entry>info:oclcnum:<xsl:value-of select="oai_marc:varfield[@id='019']/oai_marc:subfield[@label='a']"/></entry>
        </identifier>
    </xsl:if>
    <!-- other system control number, 035 not used
    <xsl:if test="oai_marc:varfield[@id='035']">
        <identifier>
            <catalog>localsystem</catalog>
            <entry><xsl:value-of select="oai_marc:varfield[@id='035']/oai_marc:subfield[@label='a']"/></entry>
        </identifier>
    </xsl:if>-->
        <metadataSchema>IEEE LOM 1.0</metadataSchema>
    </metaMetadata>
    
    <xsl:if test="oai_marc:varfield[@id='856' or @id='852']">
    <technical>
    <!-- *** MIME types *** -->
    <xsl:for-each select="oai_marc:varfield[@id='856']/oai_marc:subfield[@label='q']">
        <format><xsl:value-of select="."/></format>
    </xsl:for-each>
    <!-- *** size *** -->
    <xsl:if test="oai_marc:varfield[@id='856']/oai_marc:subfield[@label='s']">
        <size><xsl:value-of select="oai_marc:varfield[@id='856']/oai_marc:subfield[@label='s']"/></size>
    </xsl:if>
    <!-- *** url for electronic resources *** -->
    <xsl:for-each select="oai_marc:varfield[@id='856']/oai_marc:subfield[@label='u']">
        <location><xsl:value-of select="."/></location>
    </xsl:for-each>
    <!-- *** physical location *** -->
    <xsl:for-each select="oai_marc:varfield[@id='852']">
        <location>
            <xsl:value-of select="oai_marc:subfield[@label='a']"/><xsl:text>  </xsl:text>
            <xsl:for-each select="oai_marc:subfield[@label='b' or @label='c' or @label='j']">
                <xsl:value-of select="."/><xsl:text>  </xsl:text>
            </xsl:for-each>
            <xsl:if test="oai_marc:subfield[@label='e']">
                <xsl:value-of select="oai_marc:subfield[@label='e']"/><xsl:text>  </xsl:text>
            </xsl:if>
            <xsl:if test="oai_marc:subfield[@label='h']">
                <xsl:value-of select="oai_marc:subfield[@label='h']"/><xsl:text>  </xsl:text>
            </xsl:if>
            <xsl:if test="oai_marc:subfield[@label='m']">
                <xsl:value-of select="oai_marc:subfield[@label='m']"/><xsl:text>  </xsl:text>
            </xsl:if>
        </location>
    </xsl:for-each>
    </technical>
    </xsl:if>
    
    <xsl:if test="oai_marc:varfield[@id='506' or @id='540']">
<!--  *** access restrictions 506, 540 *** -->
    <rights>
        <description>
        <string language="en">
            <xsl:for-each select="oai_marc:varfield[@id='506']">
                <xsl:value-of select="oai_marc:subfield[@label='a']"/>
            </xsl:for-each>
            <xsl:for-each select="oai_marc:varfield[@id='540']">
                <xsl:value-of select="oai_marc:subfield[@label='a']"/>
            </xsl:for-each>
        </string>
        </description>
    </rights>
    </xsl:if>
    
    <xsl:if test="oai_marc:varfield[@id='400' or @id='410' or @id='411' or @id='440'
    or @id='490' or @id='770' or @id='772' or @id='773' or @id='774'
    or @id='775' or @id='776' or @id='800' or @id='810' or @id='811' or @id='830']">
    
<!--  *** Related items *** -->
     <!-- related series personal 400, 800 e.g. shakespeare plays  -->
     <xsl:for-each select="oai_marc:varfield[@id='400' or @id='800']">
     <relation>
        <kind>
            <source>LOMv1.0</source>
            <value>ispartof</value>
        </kind>
        <resource>
            <xsl:if test="oai_marc:subfield[@label='x']">
            <identifier>
                <catalog>URI</catalog>
                <entry>urn:ISSN:<xsl:value-of select="oai_marc:subfield[@label='x']"/></entry>
            </identifier>
            </xsl:if>
            <description>
                <string language="en">
                    <xsl:if test="oai_marc:subfield[@label='c']"><xsl:value-of select="oai_marc:subfield[@label='c']"/><xsl:text> </xsl:text></xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='a']"/><xsl:if test="oai_marc:subfield[@label='t']"><xsl:text> </xsl:text></xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='t']"/><xsl:if test="oai_marc:subfield[@label='d']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='d']"/><xsl:if test="oai_marc:subfield[@label='p']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='p']"/><xsl:if test="oai_marc:subfield[@label='n']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='n']"/><xsl:if test="oai_marc:subfield[@label='v']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='v']"/><xsl:if test="oai_marc:subfield[@label='r']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='r']"/><xsl:if test="oai_marc:subfield[@label='s']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='s']"/>
                </string>
            </description>
        </resource>
    </relation>
    </xsl:for-each>
    <!-- related series title 440,490,810,830 e.g. lecture notes on computer science series  -->
     <xsl:for-each select="oai_marc:varfield[@id='440' or @id='490' 
     or @id='810' or @id='830']">
     <relation>
        <kind>
            <source>LOMv1.0</source>
            <value>ispartof</value>
        </kind>
        <resource>
            <xsl:if test="oai_marc:subfield[@label='x']">
            <identifier>
                <catalog>URI</catalog>
                <entry>urn:ISSN:<xsl:value-of select="oai_marc:subfield[@label='x']"/></entry>
            </identifier>
            </xsl:if>
            <description>
                <string language="en">
                    <xsl:value-of select="oai_marc:subfield[@label='a']"/><xsl:if test="oai_marc:subfield[@label='t']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='t']"/><xsl:if test="oai_marc:subfield[@label='p']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='p']"/><xsl:if test="oai_marc:subfield[@label='n']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='n']"/><xsl:if test="oai_marc:subfield[@label='v']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='v']"/>
                </string>
            </description>
        </resource>
    </relation>
    </xsl:for-each>
    <!-- reproduction 534  -->
     <xsl:for-each select="oai_marc:varfield[@id='534']">
     <relation>
        <kind>
            <source>LOMv1.0</source>
            <value>isformatof</value>
        </kind>
        <resource>
            <xsl:if test="oai_marc:subfield[@label='x']">
            <identifier>
                <catalog>URI</catalog>
                <entry>urn:ISSN:<xsl:value-of select="oai_marc:subfield[@label='x']"/></entry>
            </identifier>
            </xsl:if>
            <xsl:if test="oai_marc:subfield[@label='z']">
            <identifier>
                <catalog>URI</catalog>
                <entry>urn:ISBN:<xsl:value-of select="oai_marc:subfield[@label='z']"/></entry>
            </identifier>
            </xsl:if>
            <description>
                <string language="en">
                    <xsl:value-of select="oai_marc:subfield[@label='p']"/><xsl:if test="oai_marc:subfield[@label='t']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='t']"/><xsl:if test="oai_marc:subfield[@label='a']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='a']"/><xsl:if test="oai_marc:subfield[@label='c']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='c']"/><xsl:if test="oai_marc:subfield[@label='b']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='b']"/><xsl:if test="oai_marc:subfield[@label='f']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='f']"/><xsl:if test="oai_marc:subfield[@label='l']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='l']"/>
                </string>
            </description>
        </resource>
    </relation>
    </xsl:for-each>
    <!-- supplementary, hosting relationship 770,772,773,774,775,776,786  -->
     <xsl:for-each select="oai_marc:varfield[@id='770' or @id='772' 
     or @id='773' or @id='774' or @id='775' or @id='776' or @id='786']">
        <relation>
        <xsl:if test="self::node()/@id='770' or self::node()/@id='773' or self::node()/@id='786'">
        <kind>
            <source>LOMv1.0</source>
            <value>haspart</value>
        </kind>
        </xsl:if>
        <xsl:if test="self::node()/@id='772' or self::node()/@id='774'">
        <kind>
            <source>LOMv1.0</source>
            <value>ispartof</value>
        </kind>
        </xsl:if>
        <xsl:if test="self::node()/@id='775'">
        <kind>
            <source>LOMv1.0</source>
            <value>hasversion</value>
        </kind>
        </xsl:if>
        <xsl:if test="self::node()/@id='776'">
        <kind>
            <source>LOMv1.0</source>
            <value>isversionof</value>
        </kind>
        </xsl:if>
        <resource>
            <xsl:if test="oai_marc:subfield[@label='w']">
            <identifier>
                <catalog>local</catalog>
                <entry><xsl:value-of select="oai_marc:subfield[@label='w']"/></entry>
            </identifier>
            </xsl:if>
            <xsl:if test="oai_marc:subfield[@label='x']">
            <identifier>
                <catalog>URI</catalog>
                <entry>urn:ISSN:<xsl:value-of select="oai_marc:subfield[@label='x']"/></entry>
            </identifier>
            </xsl:if>
            <xsl:if test="oai_marc:subfield[@label='z']">
            <identifier>
                <catalog>URI</catalog>
                <entry>urn:ISBN:<xsl:value-of select="oai_marc:subfield[@label='z']"/></entry>
            </identifier>
            </xsl:if>
            <description>
                <string language="en">
                    <xsl:value-of select="oai_marc:subfield[@label='t']"/><xsl:if test="oai_marc:subfield[@label='a']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='a']"/><xsl:if test="oai_marc:subfield[@label='c']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='c']"/><xsl:if test="oai_marc:subfield[@label='d']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='d']"/><xsl:if test="oai_marc:subfield[@label='b']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='b']"/><xsl:if test="oai_marc:subfield[@label='g']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='g']"/><xsl:if test="oai_marc:subfield[@label='n']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='n']"/>
                </string>
            </description>
        </resource>
    </relation>
    </xsl:for-each>
    <!--  meetings, events 111, 411, 711, 811  -->
    <xsl:if test="oai_marc:varfield[@id='111']">
    <relation>
        <kind>
            <source>LOMv1.0</source>
            <value>isbasedon</value>
        </kind>
        <resource>
            <description>
                <string language="en">
                    event: 
                    <xsl:value-of select="oai_marc:varfield[@id='111']/oai_marc:subfield[@label='a']"/><xsl:if test="oai_marc:varfield[@id='111']/oai_marc:subfield[@label='c']">, </xsl:if>
                    <xsl:value-of select="oai_marc:varfield[@id='111']/oai_marc:subfield[@label='c']"/><xsl:if test="oai_marc:varfield[@id='111']/oai_marc:subfield[@label='d']">, </xsl:if>
                    <xsl:value-of select="oai_marc:varfield[@id='111']/oai_marc:subfield[@label='d']"/>
                </string>
            </description>
        </resource>
    </relation>
    </xsl:if>
    <xsl:for-each select="oai_marc:varfield[@id='411' or @id='711' or @id='811']">
    <relation>
        <kind>
            <source>LOMv1.0</source>
            <value>ispartof</value>
        </kind>
        <resource>
            <xsl:if test="oai_marc:subfield[@label='x']">
            <identifier>
                <catalog>URI</catalog>
                <entry>urn:ISSN:<xsl:value-of select="oai_marc:subfield[@label='x']"/></entry>
            </identifier>
            </xsl:if>
            <description>
                <string language="en">
                    <xsl:value-of select="oai_marc:subfield[@label='a']"/><xsl:if test="oai_marc:subfield[@label='t']"><xsl:text> </xsl:text></xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='t']"/><xsl:if test="oai_marc:subfield[@label='p']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='p']"/><xsl:if test="oai_marc:subfield[@label='n']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='n']"/><xsl:if test="oai_marc:subfield[@label='v']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='v']"/><xsl:if test="oai_marc:subfield[@label='c']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='c']"/><xsl:if test="oai_marc:subfield[@label='d']">, </xsl:if>
                    <xsl:value-of select="oai_marc:subfield[@label='d']"/>
                </string>
            </description>
        </resource>
    </relation>
    </xsl:for-each>
    
    </xsl:if>
    
    <xsl:if test="oai_marc:varfield[@id='050' or @id='060' or @id='080' or @id='082'
                  or @id='600' or @id='610' or @id='611' or @id='630' or @id='650']">
    <classification>
        <purpose>
            <source>LOMv1.0</source>
            <value>discpline</value>
        </purpose>
    <!-- various standard call numbers, LoC 050, NLM 060, Universal Decimal 080, Dewey 082 -->
    <xsl:if test="oai_marc:varfield[@id='050']">
        <taxonpath>
            <source>
                <string language="en">LCC http://lcweb.loc.gov</string>
            </source>
            <taxon>
                <id><xsl:value-of select="oai_marc:varfield[@id='050']/oai_marc:subfield[@label='a']"/></id>
            </taxon>
        </taxonpath>
   </xsl:if>
   <xsl:if test="oai_marc:varfield[@id='060']">
        <taxonpath>
            <source>
                <string language="en">NLM http://wwwcf.nlm.nih.gov/class/</string>
            </source>
            <taxon>
                <id><xsl:value-of select="oai_marc:varfield[@id='060']/oai_marc:subfield[@label='a']"/></id>
            </taxon>
        </taxonpath>
   </xsl:if>
   <xsl:if test="oai_marc:varfield[@id='080']">
        <taxonpath>
            <source>
                <string language="en">UDC http://www.udcc.org/</string>
            </source>
            <taxon>
                <id><xsl:value-of select="oai_marc:varfield[@id='080']/oai_marc:subfield[@label='a']"/></id>
            </taxon>
        </taxonpath>
   </xsl:if>
   <xsl:if test="oai_marc:varfield[@id='082']">
        <taxonpath>
            <source>
                <string language="en">DDC http://www.oclc.org/dewey/</string>
            </source>
            <taxon>
                <id><xsl:value-of select="oai_marc:varfield[@id='082']/oai_marc:subfield[@label='a']"/></id>
            </taxon>
        </taxonpath>
   </xsl:if>
   <!-- controlled subject heading classification 600,610,611,630,650 -->
    <xsl:for-each select="oai_marc:varfield[@id='650' or @id='600' 
    or @id='610' or @id='611' or @id='630']">
        <taxonpath>
        <xsl:choose>
          <xsl:when test="self::node()/@oai_marc:i2='0'">
            <source>
                <string language="en">LCSH http://lcweb.loc.gov/cds/lcsh.html</string>
            </source>
          </xsl:when>
          <xsl:when test="self::node()/@oai_marc:i2='1'">
            <source>
                <string language="en">LCSHAC</string>
            </source>
          </xsl:when>
          <xsl:when test="self::node()/@oai_marc:i2='2'">
            <source>
                <string language="en">MESH http://www.nlm.nih.gov/mesh/</string>
            </source>
          </xsl:when>
          <xsl:when test="self::node()/@oai_marc:i2='3'">
            <source>
                <string language="en">NAL</string>
            </source>
          </xsl:when>
          <xsl:when test="self::node()/@oai_marc:i2='4'">
            <source>
                <string language="en">unknown</string>
            </source>
          </xsl:when>
          <xsl:when test="self::node()/@oai_marc:i2='5'">
            <source>
                <string language="en">CASH</string>
            </source>
          </xsl:when>
          <xsl:when test="self::node()/@oai_marc:i2='6'">
            <source>
                <string language="en">NLC</string>
            </source>
          </xsl:when>
          <xsl:when test="self::node()/@oai_marc:i2='7'">
            <source>
                <string language="en"><xsl:value-of select="oai_marc:subfield[@label ='2']"/></string>
            </source>
          </xsl:when>
          <xsl:when test="self::node()/@oai_marc:i2='8'">
            <source>
                <string language="en">sears</string>
            </source>
          </xsl:when>
        </xsl:choose>
         <xsl:for-each select="oai_marc:subfield[@label ='a']">
         <taxon>
            <entry>
                <string language="en"><xsl:value-of select="."/></string>
            </entry>
        </taxon>
        </xsl:for-each>
    </taxonpath>
    </xsl:for-each>
    </classification>
    </xsl:if>

    </lom>

</xsl:template>

</xsl:stylesheet>