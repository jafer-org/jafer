<?xml version="1.0"?>
<!--
/**
 * JAFER Toolkit Poject.
 * Copyright (C) 2002, JAFER Toolkit Project, Oxford University.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- stylesheet generated from "http://www.gils.net/xml-grs.xsd" version="0.1"-->

<xsl:template match="GRS1Record">
<xsl:element name="gils">
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</xsl:element>
</xsl:template>

<xsl:template match="*">
<xsl:copy>
<xsl:copy-of select="*|@*|text()"/>
</xsl:copy>
</xsl:template>

<xsl:template match="_1_1">
<Schema-Identifier>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Schema-Identifier>
</xsl:template>

<xsl:template match="_1_10">
<Rank>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Rank>
</xsl:template>

<xsl:template match="_1_12">
<URL>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</URL>
</xsl:template>

<xsl:template match="_1_14">
<Local-Control-Number>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Local-Control-Number>
</xsl:template>

<xsl:template match="_2_1">
<Title>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Title>
</xsl:template>

<xsl:template match="_2_1_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_52">
<Originator>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator>
</xsl:template>

<xsl:template match="_4_52_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_52_2_7">
<Originator-Name>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Name>
</xsl:template>

<xsl:template match="_4_52_2_10">
<Originator-Organization>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Organization>
</xsl:template>

<xsl:template match="_4_52_4_2">
<Originator-Street-Address>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Street-Address>
</xsl:template>

<xsl:template match="_4_52_4_3">
<Originator-City>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-City>
</xsl:template>

<xsl:template match="_4_52_4_4">
<Originator-State-Or-Province>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-State-Or-Province>
</xsl:template>

<xsl:template match="_4_52_4_5">
<Originator-Zip-Or-Postal-Code>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Zip-Or-Postal-Code>
</xsl:template>

<xsl:template match="_4_52_2_16">
<Originator-Country>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Country>
</xsl:template>

<xsl:template match="_4_52_2_12">
<Originator-Network-Address>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Network-Address>
</xsl:template>

<xsl:template match="_4_52_4_6">
<Originator-Hours-Of-Service>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Hours-Of-Service>
</xsl:template>

<xsl:template match="_4_52_2_14">
<Originator-Telephone>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Telephone>
</xsl:template>

<xsl:template match="_4_52_2_15">
<Originator-Fax>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Fax>
</xsl:template>

<xsl:template match="_2_2">
<Author>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author>
</xsl:template>

<xsl:template match="_2_2_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_2_2_2_7">
<Author-Name>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-Name>
</xsl:template>

<xsl:template match="_2_2_2_10">
<Author-Organization>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-Organization>
</xsl:template>

<xsl:template match="_2_2_4_2">
<Author-Street-Address>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-Street-Address>
</xsl:template>

<xsl:template match="_2_2_4_3">
<Author-City>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-City>
</xsl:template>

<xsl:template match="_2_2_4_4">
<Author-State-Or-Province>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-State-Or-Province>
</xsl:template>

<xsl:template match="_2_2_4_5">
<Author-Zip-Or-Postal-Code>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-Zip-Or-Postal-Code>
</xsl:template>

<xsl:template match="_2_2_2_16">
<Author-Country>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-Country>
</xsl:template>

<xsl:template match="_2_2_2_12">
<Author-Network-Address>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-Network-Address>
</xsl:template>

<xsl:template match="_2_2_4_6">
<Author-Hours-Of-Service>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-Hours-Of-Service>
</xsl:template>

<xsl:template match="_2_2_2_14">
<Author-Telephone>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-Telephone>
</xsl:template>

<xsl:template match="_2_2_2_15">
<Author-Fax>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Author-Fax>
</xsl:template>

<xsl:template match="_2_4">
<Date-Of-Publication>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Date-Of-Publication>
</xsl:template>

<xsl:template match="_2_3">
<Place-Of-Publication>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Place-Of-Publication>
</xsl:template>

<xsl:template match="_4_32">
<Language-Of-Resource>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Language-Of-Resource>
</xsl:template>

<xsl:template match="_2_6">
<Abstract>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Abstract>
</xsl:template>

<xsl:template match="_2_6_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_95">
<Controlled-Subject-Index>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Controlled-Subject-Index>
</xsl:template>

<xsl:template match="_4_95_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_95_4_21">
<Subject-Thesaurus>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Subject-Thesaurus>
</xsl:template>

<xsl:template match="_4_95_4_96">
<Subject-Terms-Controlled>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Subject-Terms-Controlled>
</xsl:template>

<xsl:template match="_4_95_4_96_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_95_4_96_4_20">
<Controlled-Term>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Controlled-Term>
</xsl:template>

<xsl:template match="_4_97">
<Subject-Terms-Uncontrolled>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Subject-Terms-Uncontrolled>
</xsl:template>

<xsl:template match="_4_97_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_97_4_22">
<Uncontrolled-Term>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Uncontrolled-Term>
</xsl:template>

<xsl:template match="_4_71">
<Spatial-Domain>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Spatial-Domain>
</xsl:template>

<xsl:template match="_4_71_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_71_4_91">
<Bounding-Coordinates>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Bounding-Coordinates>
</xsl:template>

<xsl:template match="_4_71_4_91_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_71_4_91_4_9">
<West-Bounding-Coordinate>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</West-Bounding-Coordinate>
</xsl:template>

<xsl:template match="_4_71_4_91_4_10">
<East-Bounding-Coordinate>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</East-Bounding-Coordinate>
</xsl:template>

<xsl:template match="_4_71_4_91_4_11">
<North-Bounding-Coordinate>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</North-Bounding-Coordinate>
</xsl:template>

<xsl:template match="_4_71_4_91_4_12">
<South-Bounding-Coordinate>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</South-Bounding-Coordinate>
</xsl:template>

<xsl:template match="_4_71_4_92">
<Place>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Place>
</xsl:template>

<xsl:template match="_4_71_4_92_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_71_4_92_4_14">
<Place-Keyword-Thesaurus>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Place-Keyword-Thesaurus>
</xsl:template>

<xsl:template match="_4_71_4_92_4_13">
<Place-Keyword>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Place-Keyword>
</xsl:template>

<xsl:template match="_4_93">
<Time-Period>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Time-Period>
</xsl:template>

<xsl:template match="_4_93_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_93_4_16">
<Time-Period-Textual>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Time-Period-Textual>
</xsl:template>

<xsl:template match="_4_93_4_101">
<Time-Period-Structured>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Time-Period-Structured>
</xsl:template>

<xsl:template match="_4_93_4_101_4_15">
<Beginning-Date>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Beginning-Date>
</xsl:template>

<xsl:template match="_4_93_4_101_4_36">
<Ending-Date>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Ending-Date>
</xsl:template>

<xsl:template match="_4_70">
<Availability>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Availability>
</xsl:template>

<xsl:template match="_4_70_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_70_4_33">
<Medium>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Medium>
</xsl:template>

<xsl:template match="_4_70_4_90">
<Distributor>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor>
</xsl:template>

<xsl:template match="_4_70_4_90_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_70_4_90_2_7">
<Distributor-Name>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-Name>
</xsl:template>

<xsl:template match="_4_70_4_90_2_10">
<Distributor-Organization>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-Organization>
</xsl:template>

<xsl:template match="_4_70_4_90_4_2">
<Distributor-Street-Address>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-Street-Address>
</xsl:template>

<xsl:template match="_4_70_4_90_4_3">
<Distributor-City>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-City>
</xsl:template>

<xsl:template match="_4_70_4_90_4_4">
<Distributor-State-Or-Province>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-State-Or-Province>
</xsl:template>

<xsl:template match="_4_70_4_90_4_5">
<Distributor-Zip-Or-Postal-Code>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-Zip-Or-Postal-Code>
</xsl:template>

<xsl:template match="_4_70_4_90_2_16">
<Distributor-Country>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-Country>
</xsl:template>

<xsl:template match="_4_70_4_90_2_12">
<Distributor-Network-Address>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-Network-Address>
</xsl:template>

<xsl:template match="_4_70_4_90_4_6">
<Distributor-Hours-Of-Service>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-Hours-Of-Service>
</xsl:template>

<xsl:template match="_4_70_4_90_2_14">
<Distributor-Telephone>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-Telephone>
</xsl:template>

<xsl:template match="_4_70_4_90_2_15">
<Distributor-Fax>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Distributor-Fax>
</xsl:template>

<xsl:template match="_4_70_4_7">
<Resource-Description>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Resource-Description>
</xsl:template>

<xsl:template match="_4_70_4_55">
<Order-Process>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Order-Process>
</xsl:template>

<xsl:template match="_4_70_4_55_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_70_4_55_4_28">
<Order-Information>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Order-Information>
</xsl:template>

<xsl:template match="_4_70_4_55_4_29">
<Cost>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Cost>
</xsl:template>

<xsl:template match="_4_70_4_55_4_30">
<Cost-Information>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Cost-Information>
</xsl:template>

<xsl:template match="_4_70_4_8">
<Technical-Prerequisites>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Technical-Prerequisites>
</xsl:template>

<xsl:template match="_4_70_4_8_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_70_4_93">
<Available-Time-Period>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Available-Time-Period>
</xsl:template>

<xsl:template match="_4_70_4_93_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_70_4_93_4_16">
<Available-Time-Textual>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Available-Time-Textual>
</xsl:template>

<xsl:template match="_4_70_4_93_4_102">
<Available-Time-Structured>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Available-Time-Structured>
</xsl:template>

<xsl:template match="_4_70_4_93_4_102_4_15">
<Beginning-Date>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Beginning-Date>
</xsl:template>

<xsl:template match="_4_70_4_93_4_102_4_36">
<Ending-Date>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Ending-Date>
</xsl:template>

<xsl:template match="_4_70_4_99">
<Available-Linkage>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Available-Linkage>
</xsl:template>

<xsl:template match="_4_70_4_99_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_70_4_99_4_18">
<Linkage-Type>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Linkage-Type>
</xsl:template>

<xsl:template match="_4_70_4_99_4_17">
<Linkage>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Linkage>
</xsl:template>

<xsl:template match="_4_57">
<Sources-Of-Data>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Sources-Of-Data>
</xsl:template>

<xsl:template match="_4_57_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_58">
<Methodology>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Methodology>
</xsl:template>

<xsl:template match="_4_58_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_53">
<Access-Constraints>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Access-Constraints>
</xsl:template>

<xsl:template match="_4_53_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_53_4_25">
<General-Access-Constraints>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</General-Access-Constraints>
</xsl:template>

<xsl:template match="_4_53_4_26">
<Originator-Dissemination-Control>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Originator-Dissemination-Control>
</xsl:template>

<xsl:template match="_4_53_4_27">
<Security-Classification-Control>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Security-Classification-Control>
</xsl:template>

<xsl:template match="_4_54">
<Use-Constraints>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Use-Constraints>
</xsl:template>

<xsl:template match="_4_54_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_94">
<Point-Of-Contact>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Point-Of-Contact>
</xsl:template>

<xsl:template match="_4_94_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_94_2_7">
<Contact-Name>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-Name>
</xsl:template>

<xsl:template match="_4_94_2_10">
<Contact-Organization>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-Organization>
</xsl:template>

<xsl:template match="_4_94_4_2">
<Contact-Street-Address>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-Street-Address>
</xsl:template>

<xsl:template match="_4_94_4_3">
<Contact-City>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-City>
</xsl:template>

<xsl:template match="_4_94_4_4">
<Contact-State-Or-Province>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-State-Or-Province>
</xsl:template>

<xsl:template match="_4_94_4_5">
<Contact-Zip-Or-Postal-Code>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-Zip-Or-Postal-Code>
</xsl:template>

<xsl:template match="_4_94_2_16">
<Contact-Country>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-Country>
</xsl:template>

<xsl:template match="_4_94_2_12">
<Contact-Network_Address>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-Network_Address>
</xsl:template>

<xsl:template match="_4_94_4_6">
<Contact-Hours-Of-Service>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-Hours-Of-Service>
</xsl:template>

<xsl:template match="_4_94_2_14">
<Contact-Telephone>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-Telephone>
</xsl:template>

<xsl:template match="_4_94_2_15">
<Contact-Fax>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Contact-Fax>
</xsl:template>

<xsl:template match="_4_59">
<Supplemental-Information>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Supplemental-Information>
</xsl:template>

<xsl:template match="_4_59_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_51">
<Purpose>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Purpose>
</xsl:template>

<xsl:template match="_4_51_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_56">
<Agency-Program>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Agency-Program>
</xsl:template>

<xsl:template match="_4_56_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_98">
<Cross-Reference>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Cross-Reference>
</xsl:template>

<xsl:template match="_4_98_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_98_2_1">
<Cross-Reference-Title>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Cross-Reference-Title>
</xsl:template>

<xsl:template match="_4_98_2_35">
<Cross-Reference-Relationship>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Cross-Reference-Relationship>
</xsl:template>

<xsl:template match="_4_98_4_100">
<Cross-Reference-Linkage>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Cross-Reference-Linkage>
</xsl:template>

<xsl:template match="_4_98_4_100_1_19">
<wellKnown>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</wellKnown>
</xsl:template>

<xsl:template match="_4_98_4_100_4_18">
<Linkage-Type>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Linkage-Type>
</xsl:template>

<xsl:template match="_4_98_4_100_4_17">
<Linkage>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Linkage>
</xsl:template>

<xsl:template match="_4_31">
<Schedule-Number>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Schedule-Number>
</xsl:template>

<xsl:template match="_4_1">
<Control-Identifier>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Control-Identifier>
</xsl:template>

<xsl:template match="_4_23">
<Original-Control-Identifier>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Original-Control-Identifier>
</xsl:template>

<xsl:template match="_4_19">
<Record-Source>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Record-Source>
</xsl:template>

<xsl:template match="_4_34">
<Language-Of-Record>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Language-Of-Record>
</xsl:template>

<xsl:template match="_1_16">
<Date-Of-Last-Modification>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Date-Of-Last-Modification>
</xsl:template>

<xsl:template match="_4_24">
<Record-Review-Date>
<xsl:copy-of select="@*"/>
<xsl:apply-templates/>
</Record-Review-Date>
</xsl:template>

</xsl:stylesheet>