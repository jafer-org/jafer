/**
 * JAFER Toolkit Project. Copyright (C) 2002, JAFER Toolkit Project, Oxford
 * University. This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jafer.sru.bridge;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jafer.sru.SRUException;

/**
 * This class tests the search and retrieve operation bridge
 */
public class SearchRetrieveBridgeTest extends TestCase
{

    /**
     * Stores a reference to the XMLHEADER text
     */
    private static final String XMLHEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    /**
     * Stores a reference to the common test url
     */
    private static final String STANDARD_WEBURL = "http://alcme.oclc.org/srw/search/GSAFD";

    /**
     * Creates the bridge class for the given url and processes the request with
     * the supplied params
     * 
     * @param testName Name of the test
     * @param webURL The webservice URL
     * @param params The input Params
     * @param expectedXML The expected XML
     * @throws SRUException
     * @throws MalformedURLException
     */
    private String runBridgeTest(String testName, String webURL, Map params, String expectedXML) throws SRUException,
            MalformedURLException
    {
        SearchRetrieveOperationBridge bridge = new SearchRetrieveOperationBridge();
        String xml = bridge.execute(params, new URL(webURL));
        // remove line feeds to make compare easier
        String newXML = xml.replaceAll("[\\r\\n\\t]", "");
        // only test if expected CQL supplied otherwise exception was expected
        if (expectedXML.length() > 0)
        {
            // make sure the XML matches as expected
            assertEquals("(" + testName + ") Expected and returned output XML do not match", expectedXML, newXML);
        }
        return newXML;
    }

    /**
     * Tests a simple search request
     */
    public void testSimpleSearchRequest()
    {
        String testName = "", xml;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response
            // instead we just check a couple of key parts
            String check1 = "<version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version>"
                    + "<numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</numberOfRecords>";
            String check2 = "<echoedSearchRetrieveRequest xsi:type=\"ns1:echoedSearchRetrieveRequestType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + "1.1</version><query xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</query>";
            // specifiy operation
            testName = "search specified as operation with no version";
            params.put("query", "bible");
            params.put("operation", "searchRetrieve");
            xml = runBridgeTest(testName, STANDARD_WEBURL, params, "");
            assertTrue("(" + testName + ") Mismatch on check 1", xml.indexOf(check1) != -1 );
            assertTrue("(" + testName + ") Mismatch on check 2", xml.indexOf(check2) != -1 );

        }
        catch (SRUException exc)
        {
            exc.printStackTrace();
            fail("SRUException Exception: (" + testName + ") " + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests a simple search request wit max records set to 1
     */
    public void testMaxRecordsSearchRequest()
    {
        String testName = "", xml;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response
            // instead we just check a couple of key parts
            String check1 = "<version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version>"
                    + "<numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</numberOfRecords>";
            String check2 = "<echoedSearchRetrieveRequest xsi:type=\"ns1:echoedSearchRetrieveRequestType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + "1.1</version><query xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</query>";
            String check3 = "<records xsi:type=\"ns1:recordsType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "<record xsi:type=\"ns1:recordType\"><recordSchema xsi:type=\"xsd:string\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">info:srw/schema/1/marcxml-v1.1</recordSchema>" +
                    "<recordPacking xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml</recordPacking>" +
                    "<recordData xsi:type=\"ns1:stringOrXmlFragment\"><mx:record xsi:schemaLocation=\"http://www.loc.gov/MARC" +
                    "21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\" xmlns=\"http://www.w3.org/TR/" +
                    "xhtml1/strict\" xmlns:mx=\"http://www.loc.gov/MARC21/slim\"><mx:leader>00000nz  a2200000n  0000" +
                    "</mx:leader><mx:controlfield tag=\"001\">GSAFD000013</mx:controlfield><mx:controlfield tag=\"003\">" +
                    "IlChALCS</mx:controlfield><mx:controlfield tag=\"005\">20040526141352.0</mx:controlfield>" +
                    "<mx:controlfield tag=\"008\">000720 n anznnbabn           a ana     d</mx:controlfield>" +
                    "<mx:datafield ind1=\" \" ind2=\" \" tag=\"040\"><mx:subfield code=\"a\">IlChALCS</mx:subfield>" +
                    "<mx:subfield code=\"b\">eng</mx:subfield><mx:subfield code=\"c\">IEN</mx:subfield><mx:subfield " +
                    "code=\"d\">OCoLC-O</mx:subfield><mx:subfield code=\"f\">gsafd</mx:subfield></mx:datafield>" +
                    "<mx:datafield ind1=\" \" ind2=\" \" tag=\"155\"><mx:subfield code=\"a\">Bible plays</mx:subfield>" +
                    "<mx:subfield code=\"9\">NACO: BIBLE PLAYS</mx:subfield></mx:datafield><mx:datafield ind1=\" \" ind2=\" \" " +
                    "tag=\"555\"><mx:subfield code=\"w\">g</mx:subfield><mx:subfield code=\"a\">Historical drama</mx:subfield>" +
                    "<mx:subfield code=\"0\">(IlChALCS)GSAFD000054</mx:subfield><mx:subfield code=\"9\">NACO: HISTORICAL DRAMA" +
                    "</mx:subfield></mx:datafield><mx:datafield ind1=\" \" ind2=\" \" tag=\"555\"><mx:subfield code=\"w\">h" +
                    "</mx:subfield><mx:subfield code=\"a\">Mysteries and miracle plays</mx:subfield><mx:subfield code=\"0\">" +
                    "(IlChALCS)GSAFD000084</mx:subfield><mx:subfield code=\"9\">NACO: MYSTERIES AND MIRACLE PLAYS</mx:subfield>" +
                    "</mx:datafield><mx:datafield ind1=\" \" ind2=\" \" tag=\"555\"><mx:subfield code=\"w\">h</mx:subfield>" +
                    "<mx:subfield code=\"a\">Passion plays</mx:subfield><mx:subfield code=\"0\">(IlChALCS)GSAFD000095" +
                    "</mx:subfield><mx:subfield code=\"9\">NACO: PASSION PLAYS</mx:subfield></mx:datafield><mx:datafield " +
                    "ind1=\" \" ind2=\" \" tag=\"680\"><mx:subfield code=\"i\">Use for dramatizations of biblical events." +
                    "</mx:subfield></mx:datafield><mx:datafield ind1=\" \" ind2=\"0\" tag=\"750\"><mx:subfield code=\"a\">" +
                    "Bible plays</mx:subfield><mx:subfield code=\"0\">(DLC)sh 85013819 </mx:subfield><mx:subfield code=\"5\">" +
                    "OCoLC-O</mx:subfield></mx:datafield><mx:datafield ind1=\" \" ind2=\" \" tag=\"856\"><mx:subfield " +
                    "code=\"u\">info:kos/concept/gsafd/GSAFD000013</mx:subfield><mx:subfield code=\"z\">KOS info URI" +
                    "</mx:subfield></mx:datafield></mx:record></recordData><recordPosition lowestSetBit=\"0\" " +
                    "xsi:type=\"xsd:positiveInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1</recordPosition>" +
                    "<extraRecordData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></record></records>";
            // specifiy operation
            testName = "search test max records";
            params.put("query", "bible");
            params.put("maximumRecords", "1");
            params.put("operation", "searchRetrieve");
            xml = runBridgeTest(testName, STANDARD_WEBURL, params, "");
            assertTrue("(" + testName + ") Mismatch on check 1", xml.indexOf(check1) != -1 );
            assertTrue("(" + testName + ") Mismatch on check 2", xml.indexOf(check2) != -1 );
            assertTrue("(" + testName + ") Mismatch on check 3", xml.indexOf(check3) != -1 );         
            
        }
        catch (SRUException exc)
        {
            exc.printStackTrace();
            fail("SRUException Exception: (" + testName + ") " + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException Exception: (" + testName + ") " + exc);
        }
    }

    
    

    /**
     * Tests a simple search request with record packing
     */
    public void testRecordPackingSearchRequest()
    {
        String testName = "", xml;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response
            // instead we just check a couple of key parts
            String check1 = "<version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version>"
                    + "<numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</numberOfRecords>";
            String check2 = "<echoedSearchRetrieveRequest xsi:type=\"ns1:echoedSearchRetrieveRequestType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + "1.1</version><query xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</query>";
            String check3 = "<records xsi:type=\"ns1:recordsType\" xmlns:xsi=\"http://www.w3.org/2001/" +
                    "XMLSchema-instance\"><record xsi:type=\"ns1:recordType\"><recordSchema xsi:type=\"xsd:string\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">info:srw/schema/1/marcxml-v1.1</recordSchema>" +
                    "<recordPacking xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">string" +
                    "</recordPacking><recordData xsi:type=\"ns1:stringOrXmlFragment\">&lt;mx:record xmlns:mx=" +
                    "&quot;http://www.loc.gov/MARC21/slim&quot; xmlns=&quot;http://www.w3.org/TR/xhtml1/strict&quot;" +
                    " xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;" +
                    "http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd" +
                    "&quot;&gt;&lt;mx:leader&gt;00000nz  a2200000n  0000&lt;/mx:leader&gt;&lt;mx:controlfield " +
                    "tag=&quot;001&quot;&gt;GSAFD000013&lt;/mx:controlfield&gt;&lt;mx:controlfield tag=&quot;" +
                    "003&quot;&gt;IlChALCS&lt;/mx:controlfield&gt;&lt;mx:controlfield tag=&quot;005&quot;&gt;" +
                    "20040526141352.0&lt;/mx:controlfield&gt;&lt;mx:controlfield tag=&quot;008&quot;&gt;000720 " +
                    "n anznnbabn           a ana     d&lt;/mx:controlfield&gt;&lt;mx:datafield tag=&quot;040&quot;" +
                    " ind1=&quot; &quot; ind2=&quot; &quot;&gt;&lt;mx:subfield code=&quot;a&quot;&gt;IlChALCS&lt;/" +
                    "mx:subfield&gt;&lt;mx:subfield code=&quot;b&quot;&gt;eng&lt;/mx:subfield&gt;&lt;mx:subfield " +
                    "code=&quot;c&quot;&gt;IEN&lt;/mx:subfield&gt;&lt;mx:subfield code=&quot;d&quot;&gt;OCoLC-O&lt;" +
                    "/mx:subfield&gt;&lt;mx:subfield code=&quot;f&quot;&gt;gsafd&lt;/mx:subfield&gt;&lt;/mx:" +
                    "datafield&gt;&lt;mx:datafield tag=&quot;155&quot; ind1=&quot; &quot; ind2=&quot; &quot;&gt;" +
                    "&lt;mx:subfield code=&quot;a&quot;&gt;Bible plays&lt;/mx:subfield&gt;&lt;mx:subfield code=" +
                    "&quot;9&quot;&gt;NACO: BIBLE PLAYS&lt;/mx:subfield&gt;&lt;/mx:datafield&gt;&lt;mx:datafield " +
                    "tag=&quot;555&quot; ind1=&quot; &quot; ind2=&quot; &quot;&gt;&lt;mx:subfield code=&quot;w&quot;" +
                    "&gt;g&lt;/mx:subfield&gt;&lt;mx:subfield code=&quot;a&quot;&gt;Historical drama&lt;/mx:subfield&" +
                    "gt;&lt;mx:subfield code=&quot;0&quot;&gt;(IlChALCS)GSAFD000054&lt;/mx:subfield&gt;&lt;mx:subfield " +
                    "code=&quot;9&quot;&gt;NACO: HISTORICAL DRAMA&lt;/mx:subfield&gt;&lt;/mx:datafield&gt;&lt;" +
                    "mx:datafield tag=&quot;555&quot; ind1=&quot; &quot; ind2=&quot; &quot;&gt;&lt;mx:subfield " +
                    "code=&quot;w&quot;&gt;h&lt;/mx:subfield&gt;&lt;mx:subfield code=&quot;a&quot;&gt;Mysteries " +
                    "and miracle plays&lt;/mx:subfield&gt;&lt;mx:subfield code=&quot;0&quot;&gt;(IlChALCS)" +
                    "GSAFD000084&lt;/mx:subfield&gt;&lt;mx:subfield code=&quot;9&quot;&gt;NACO: MYSTERIES " +
                    "AND MIRACLE PLAYS&lt;/mx:subfield&gt;&lt;/mx:datafield&gt;&lt;mx:datafield tag=&quot;555&quot;" +
                    " ind1=&quot; &quot; ind2=&quot; &quot;&gt;&lt;mx:subfield code=&quot;w&quot;&gt;h&lt;/mx:" +
                    "subfield&gt;&lt;mx:subfield code=&quot;a&quot;&gt;Passion plays&lt;/mx:subfield&gt;&lt;mx:" +
                    "subfield code=&quot;0&quot;&gt;(IlChALCS)GSAFD000095&lt;/mx:subfield&gt;&lt;mx:subfield " +
                    "code=&quot;9&quot;&gt;NACO: PASSION PLAYS&lt;/mx:subfield&gt;&lt;/mx:datafield&gt;&lt;mx:" +
                    "datafield tag=&quot;680&quot; ind1=&quot; &quot; ind2=&quot; &quot;&gt;&lt;mx:subfield " +
                    "code=&quot;i&quot;&gt;Use for dramatizations of biblical events.&lt;/mx:subfield&gt;&lt;/" +
                    "mx:datafield&gt;&lt;mx:datafield tag=&quot;750&quot; ind1=&quot; &quot; ind2=&quot;0&quot;" +
                    "&gt;&lt;mx:subfield code=&quot;a&quot;&gt;Bible plays&lt;/mx:subfield&gt;&lt;mx:subfield " +
                    "code=&quot;0&quot;&gt;(DLC)sh 85013819 &lt;/mx:subfield&gt;&lt;mx:subfield code=&quot;5&" +
                    "quot;&gt;OCoLC-O&lt;/mx:subfield&gt;&lt;/mx:datafield&gt;&lt;mx:datafield tag=&quot;856&" +
                    "quot; ind1=&quot; &quot; ind2=&quot; &quot;&gt;&lt;mx:subfield code=&quot;u&quot;&gt;info:" +
                    "kos/concept/gsafd/GSAFD000013&lt;/mx:subfield&gt;&lt;mx:subfield code=&quot;z&quot;&gt;KOS " +
                    "info URI&lt;/mx:subfield&gt;&lt;/mx:datafield&gt;&lt;/mx:record&gt;</recordData><recordPosition " +
                    "lowestSetBit=\"0\" xsi:type=\"xsd:positiveInteger\" xmlns:xsd=\"http://www.w3.org/" +
                    "2001/XMLSchema\">1</recordPosition><extraRecordData xsi:type=\"ns1:extraDataType\" " +
                    "xsi:nil=\"true\"/></record></records>";
            // specifiy operation
            testName = "search record packing";
            params.put("query", "bible");
            params.put("maximumRecords", "1");
            params.put("recordPacking","string");
            params.put("operation", "searchRetrieve");
            xml = runBridgeTest(testName, STANDARD_WEBURL, params, "");
            assertTrue("(" + testName + ") Mismatch on check 1", xml.indexOf(check1) != -1 );
            assertTrue("(" + testName + ") Mismatch on check 2", xml.indexOf(check2) != -1 );
            assertTrue("(" + testName + ") Mismatch on check 3", xml.indexOf(check3) != -1 );         
            
        }
        catch (SRUException exc)
        {
            exc.printStackTrace();
            fail("SRUException Exception: (" + testName + ") " + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests a simple explain request wit max records set to 1
     */
    public void testMaxRecordsWithStartRecordSearchRequest()
    {
        String testName = "", xml;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response
            // instead we just check a couple of key parts
            String check1 = "<version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version>"
                    + "<numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</numberOfRecords>";
            String check2 = "<echoedSearchRetrieveRequest xsi:type=\"ns1:echoedSearchRetrieveRequestType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + "1.1</version><query xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</query>";
            String check3 = "<records xsi:type=\"ns1:recordsType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "<record xsi:type=\"ns1:recordType\"><recordSchema xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org" +
                    "/2001/XMLSchema\">info:srw/schema/1/marcxml-v1.1</recordSchema><recordPacking xsi:type=\"xsd:string\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml</recordPacking><recordData xsi:type=\"" +
                    "ns1:stringOrXmlFragment\"><mx:record xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http:" +
                    "//www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\" xmlns=\"http://www.w3.org/TR/xhtml1/strict\"" +
                    " xmlns:mx=\"http://www.loc.gov/MARC21/slim\"><mx:leader>00000nz  a2200000n  0000</mx:leader>" +
                    "<mx:controlfield tag=\"001\">GSAFD000012</mx:controlfield><mx:controlfield tag=\"003\">IlChALCS" +
                    "</mx:controlfield><mx:controlfield tag=\"005\">20040526141352.0</mx:controlfield><mx:controlfield " +
                    "tag=\"008\">000720 n anznnbabn           n ana     d</mx:controlfield><mx:datafield ind1=\" \" i" +
                    "nd2=\" \" tag=\"040\"><mx:subfield code=\"a\">IlChALCS</mx:subfield><mx:subfield code=\"b\">eng" +
                    "</mx:subfield><mx:subfield code=\"c\">IEN</mx:subfield><mx:subfield code=\"d\">OCoLC-O</mx:subfield>" +
                    "<mx:subfield code=\"f\">gsafd</mx:subfield></mx:datafield><mx:datafield ind1=\" \" ind2=\" \" " +
                    "tag=\"155\"><mx:subfield code=\"a\">Bible films</mx:subfield><mx:subfield code=\"9\">NACO: BIBLE FILMS" +
                    "</mx:subfield></mx:datafield><mx:datafield ind1=\" \" ind2=\"0\" tag=\"750\"><mx:subfield code=\"a\">" +
                    "Bible films</mx:subfield><mx:subfield code=\"0\">(DLC)sh 85013816 </mx:subfield><mx:subfield code=\"5\">" +
                    "OCoLC-O</mx:subfield></mx:datafield><mx:datafield ind1=\" \" ind2=\" \" tag=\"856\"><mx:subfield " +
                    "code=\"u\">info:kos/concept/gsafd/GSAFD000012</mx:subfield><mx:subfield code=\"z\">KOS info URI" +
                    "</mx:subfield></mx:datafield></mx:record></recordData><recordPosition lowestSetBit=\"1\" " +
                    "xsi:type=\"xsd:positiveInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">2</recordPosition>" +
                    "<extraRecordData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></record></records>" +
                    "<nextRecordPosition lowestSetBit=\"0\" xsi:type=\"xsd:positiveInteger\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</nextRecordPosition>";
            // specifiy operation
            testName = "search start record";
            params.put("query", "bible");
            params.put("maximumRecords", "1");
            params.put("startRecord","2");
            params.put("operation", "searchRetrieve");
            xml = runBridgeTest(testName, STANDARD_WEBURL, params, "");
            assertTrue("(" + testName + ") Mismatch on check 1", xml.indexOf(check1) != -1 );
            assertTrue("(" + testName + ") Mismatch on check 2", xml.indexOf(check2) != -1 );
            assertTrue("(" + testName + ") Mismatch on check 3", xml.indexOf(check3) != -1 );        
        }
        catch (SRUException exc)
        {
            exc.printStackTrace();
            fail("SRUException Exception: (" + testName + ") " + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException Exception: (" + testName + ") " + exc);
        } 
    }

    /**
     * Tests a stylesheet request
     */
    public void testStylesheetSearchRequest()
    {
        String testName = "", xml;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response
            // instead we just check a couple of key parts
            String check1 = "<version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version>"
                    + "<numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</numberOfRecords>";
            String check2 = "<echoedSearchRetrieveRequest xsi:type=\"ns1:echoedSearchRetrieveRequestType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + "1.1</version><query xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</query>";
            String check3 ="<?xml-stylesheet type=\"text/xsl\" href=\"http://www.a.com/master.xsl\"?>";
            // specifiy operation
            testName = "search stylesheet";
            params.put("query", "bible");
            params.put("operation", "searchRetrieve");
            params.put("stylesheet", "http://www.a.com/master.xsl");
            xml = runBridgeTest(testName, STANDARD_WEBURL, params, "");
            assertTrue("(" + testName + ") Mismatch on check 1", xml.indexOf(check1) != -1 );
            assertTrue("(" + testName + ") Mismatch on check 2", xml.indexOf(check2) != -1 );
            assertTrue("(" + testName + ") Mismatch on check 2", xml.indexOf(check3) != -1 );
        }
        catch (SRUException exc)
        {
            exc.printStackTrace();
            fail("SRUException Exception: (" + testName + ") " + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException Exception: (" + testName + ") " + exc);
        }
    }

    

    /**
     * Tests a bad query param
     */
    public void testBADQueryParam()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            testName = "search bad query";
            expectedXML = XMLHEADER + "" +
                    "<ns1:searchRetrieveResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                    "1.1</version><numberOfRecords lowestSetBit=\"-1\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsi=\"" +
                    "http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">0" +
                    "</numberOfRecords><resultSetId xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/" +
                    "2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><resultSetIdleTime xsi:type=\"" +
                    "xsd:positiveInteger\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:" +
                    "xsd=\"http://www.w3.org/2001/XMLSchema\"/><records xsi:type=\"ns1:recordsType\" xsi:nil=\"true\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><nextRecordPosition " +
                    "xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><echoedSearchRetrieveRequest " +
                    "xsi:type=\"ns1:echoedSearchRetrieveRequestType\" xsi:nil=\"true\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><diagnostics xsi:type=\"ns1:diagnosticsType\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><diagnostic xsi:type=\"ns2:diagnosticType\" " +
                    "xmlns:ns2=\"http://www.loc.gov/zing/srw/diagnostic/\"><uri xsi:type=\"xsd:anyURI\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">info:srw/diagnostic/7</uri><details " +
                    "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">query</details>" +
                    "<message xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                    "No query specified</message></diagnostic></diagnostics><extraResponseData xsi:type=\"ns1:extraDataType\" " +
                    "xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:searchRetrieveResponse>";
            runBridgeTest(testName, STANDARD_WEBURL, params, expectedXML);
            
        }
        catch (SRUException exc)
        {
            exc.printStackTrace();
            fail("SRUException Exception: (" + testName + ") " + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests bad url
     */
    public void testBADURLRequest()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // no params
            testName = "search bad URL";
            expectedXML = XMLHEADER + 
                    "<ns1:searchRetrieveResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                    "1.1</version><numberOfRecords lowestSetBit=\"-1\" xsi:type=\"xsd:nonNegativeInteger\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/" +
                    "XMLSchema\">0</numberOfRecords><resultSetId xsi:type=\"xsd:string\" xsi:nil=\"true\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/" +
                    "XMLSchema\"/><resultSetIdleTime xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" xmlns:xsi=\"" +
                    "http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/>" +
                    "<records xsi:type=\"ns1:recordsType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-" +
                    "instance\"/><nextRecordPosition xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" xmlns:xsi=\"http:" +
                    "//www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/>" +
                    "<echoedSearchRetrieveRequest xsi:type=\"ns1:echoedSearchRetrieveRequestType\" xsi:nil=\"true\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><diagnostics xsi:type=\"ns1:diagnosticsType\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><diagnostic xsi:type=\"ns2:diagnosticType\" " +
                    "xmlns:ns2=\"http://www.loc.gov/zing/srw/diagnostic/\"><uri xsi:type=\"xsd:anyURI\" xmlns:xsd=\"http:" +
                    "//www.w3.org/2001/XMLSchema\">info:srw/diagnostic/2</uri><details xsi:type=\"xsd:string\" xmlns:xsd=\"" +
                    "http://www.w3.org/2001/XMLSchema\">(0)null</details><message xsi:type=\"xsd:string\" xmlns:xsd=\"http" +
                    "://www.w3.org/2001/XMLSchema\">Unable to contact server</message></diagnostic></diagnostics>" +
                    "<extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001" +
                    "/XMLSchema-instance\"/></ns1:searchRetrieveResponse>";
            params.put("query", "golf");
            runBridgeTest(testName, "http:/aaa.aaa.com/", params, expectedXML);
        }
        catch (SRUException exc)
        {
            exc.printStackTrace();
            fail("SRUException Exception: (" + testName + ") " + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException Exception: (" + testName + ") " + exc);
        }
    }
}