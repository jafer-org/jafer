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
 * This class tests the Explain operation bridge
 */
public class ExplainBridgeTest extends TestCase
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
    private void runBridgeTest(String testName, String webURL, Map params, String expectedXML) throws SRUException,
            MalformedURLException
    {
        ExplainOperationBridge bridge = new ExplainOperationBridge();
        String xml = bridge.execute(params, new URL(webURL));

        // remove line feeds to make compare easier
        String newXML = xml.replaceAll("[\\r\\n\\t]", "");
        // only test if expected CQL supplied otherwise exception was
        // expected
        if (expectedXML.length() > 0)
        {
            // make sure the XML matches as expected
            assertEquals("(" + testName + ") Expected and returned output XML do not match", expectedXML, newXML);
        }

    }

    /**
     * Tests a simple explain request
     */
    public void testSimpleExplainRequest()
    {
        String testName = "";
        Map params = new HashMap();
        try
        {
            // no params
            testName = "simple explain";
                     
            runBridgeTest(testName, STANDARD_WEBURL, params, standardExplainResponse);
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
     * Tests a simple explain request with record packing
     */
    public void testRecordPackingExplainRequest()
    {
        String testName = "";
        Map params = new HashMap();
        try
        {
            // no params
            testName = "record packing param";
            params.put("recordPacking", "xml");
             runBridgeTest(testName, STANDARD_WEBURL, params, standardExplainResponse);
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
     * Tests a simple explain request with record packing
     */
    public void testRecordPackingStringExplainRequest()
    {
        String testName = "";
        Map params = new HashMap();
        try
        {
            // no params
            testName = "record packing param";
            params.put("recordPacking", "xml");
            runBridgeTest(testName, STANDARD_WEBURL, params, standardExplainResponse);
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
     * Tests a simple explain request with stylesheet
     */
    public void testStylesheetExplainRequest()
    {
        String testName = "";
        Map params = new HashMap();
        try
        {
            // no params
            testName = "explain due to no operations";
            params.put("stylesheet", "http://www.a.com/master.xsl");
            String expectedXML = "<?xml-stylesheet type=\"text/xsl\" href=\"http://www.a.com/master.xsl\"?>" + standardExplainResponse;
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
     * Tests a simple explain request
     */
    public void testBADURLRequest()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // no params
            testName = "explain bad URL";
            expectedXML = XMLHEADER + 
            "<ns1:explainResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "1.1</version><record xsi:type=\"ns1:recordType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<recordSchema xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "info:srw/schema/1/diagnostics-v1.1</recordSchema><recordPacking xsi:type=\"xsd:string\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml</recordPacking><recordData xsi:type=\"ns1:stringOrXmlFragment\">" +
            "<ns2:diagnostic xsi:type=\"xsd:string\" xmlns:ns2=\"http://www.loc.gov/zing/srw/diagnostic/\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">&lt;?xml version=&quot;1.0&quot; " +
            "encoding=&quot;UTF-8&quot;?&gt;&lt;ns1:diagnostic xmlns:ns1=&quot;http://www.loc.gov/zing/srw/diagnostic/&quot;" +
            "&gt;&lt;uri xsi:type=&quot;xsd:anyURI&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; " +
            "xmlns:xsd=&quot;http://www.w3.org/2001/XMLSchema&quot;&gt;info:srw/diagnostic/2&lt;/uri&gt;&lt;details " +
            "xsi:type=&quot;xsd:string&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; " +
            "xmlns:xsd=&quot;http://www.w3.org/2001/XMLSchema&quot;&gt;(0)null&lt;/details&gt;&lt;message xsi:type=" +
            "&quot;xsd:string&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;" +
            "http://www.w3.org/2001/XMLSchema&quot;&gt;Unable to contact server&lt;/message&gt;&lt;/ns1:diagnostic&gt;" +
            "</ns2:diagnostic></recordData><recordPosition xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRecordData xsi:type=\"ns1:extraDataType\" " +
            "xsi:nil=\"true\"/></record><echoedExplainRequest xsi:type=\"ns1:explainRequestType\" xsi:nil=\"true\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><diagnostics xsi:type=\"ns1:diagnosticsType\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><diagnostic xsi:type=\"ns3:diagnosticType\" " +
            "xmlns:ns3=\"http://www.loc.gov/zing/srw/diagnostic/\"><uri xsi:type=\"xsd:anyURI\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">info:srw/diagnostic/2</uri><details xsi:type=\"xsd:string\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">(0)null</details><message xsi:type=\"xsd:string\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">Unable to contact server</message></diagnostic>" +
            "</diagnostics><extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:explainResponse>";
            runBridgeTest(testName, "http:/aaa.aaa.com/", params, expectedXML);
        }
        catch (SRUException exc)
        {
            if (exc.getMessage().indexOf("Unable to contact the SRW service") == -1)
            {
                fail("Wrong SRUException:" + exc);
            }
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException Exception: (" + testName + ") " + exc);
        }
    }
    
    /**
     * Stores a reference to the standard explain response
     */
    private String standardExplainResponse  = XMLHEADER +
    "<ns1:explainResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" " +
    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
    "1.1</version><record xsi:type=\"ns1:recordType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    "<recordSchema xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
    "http://explain.z3950.org/dtd/2.0/</recordSchema><recordPacking xsi:type=\"xsd:string\" " +
    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml</recordPacking><recordData " +
    "xsi:type=\"ns1:stringOrXmlFragment\"><ns1:explain authoritative=\"true\" " +
    "xmlns=\"http://explain.z3950.org/dtd/2.0/\" xmlns:ns1=\"http://explain.z3950.org/dtd/2.0/\">        " +
    "<ns1:serverInfo protocol=\"SRW/U\">          <ns1:host>alcme.oclc.org</ns1:host>          " +
    "<ns1:port>80</ns1:port>          <ns1:database>srw/search/GSAFD</ns1:database>          " +
    "</ns1:serverInfo>        <ns1:databaseInfo>          <ns1:title>GSAFD Thesaurus</ns1:title>          " +
    "<ns1:description>GSAFD Thesaurus</ns1:description>          <ns1:contact>Jeffrey A. Young (mailto:jyoung@oclc.org)" +
    "</ns1:contact>          <ns1:implementation indentifier=\"http://www.oclc.org/research/software/srw\" " +
    "version=\"1.1\">            <ns1:title>OCLC Research SRW Server version 1.1</ns1:title>            " +
    "</ns1:implementation>          </ns1:databaseInfo>        <ns1:metaInfo>          </ns1:metaInfo>        " +
    "<ns1:indexInfo>          <ns1:set identifier=\"info:srw/cql-context-set/1/z3919-v1.0\" name=\"z3919\"/>          " +
    "<ns1:index>            <ns1:title>z3919.termID</ns1:title>            <ns1:map>              " +
    "<ns1:name set=\"z3919\">termID</ns1:name>              </ns1:map>            </ns1:index>          " +
    "<ns1:index>            <ns1:title>z3919.termNotes</ns1:title>            <ns1:map>              " +
    "<ns1:name set=\"z3919\">termNotes</ns1:name>              </ns1:map>            </ns1:index>          " +
    "<ns1:set identifier=\"info:srw/cql-context-set/1/cql-v1.1\" name=\"cql\"/>          <ns1:index>            " +
    "<ns1:title>cql.any</ns1:title>            <ns1:map>              <ns1:name set=\"cql\">any</ns1:name>              " +
    "</ns1:map>            </ns1:index>          <ns1:index>            <ns1:title>z3919.terms</ns1:title>            " +
    "<ns1:map>              <ns1:name set=\"z3919\">terms</ns1:name>              </ns1:map>            " +
    "</ns1:index>          <ns1:index>            <ns1:title>z3919.termsWords</ns1:title>            " +
    "<ns1:map>              <ns1:name set=\"z3919\">termsWords</ns1:name>              </ns1:map>            " +
    "</ns1:index>          <ns1:index>            <ns1:title>z3919.preferredTermName</ns1:title>            " +
    "<ns1:map>              <ns1:name set=\"z3919\">preferredTermName</ns1:name>              </ns1:map>            " +
    "</ns1:index>          <ns1:index>            <ns1:title>z3919.mappedTerms</ns1:title>            " +
    "<ns1:map>              <ns1:name set=\"z3919\">mappedTerms</ns1:name>              </ns1:map>            " +
    "</ns1:index>          <ns1:index>            <ns1:title>z3919.termQualifier</ns1:title>            " +
    "<ns1:map>              <ns1:name set=\"z3919\">termQualifier</ns1:name>              </ns1:map>            " +
    "</ns1:index>          <ns1:set identifier=\"info:srw/cql-context-set/1/oai-v1.0\" name=\"oai\"/>          " +
    "<ns1:index>            <ns1:title>oai.datestamp</ns1:title>            <ns1:map>              " +
    "<ns1:name set=\"oai\">datestamp</ns1:name>              </ns1:map>            </ns1:index>          " +
    "<ns1:index>            <ns1:title>z3919.preferredTermNameWords</ns1:title>            <ns1:map>              " +
    "<ns1:name set=\"z3919\">preferredTermNameWords</ns1:name>              </ns1:map>            </ns1:index>     " +
    "     <ns1:index>            <ns1:title>oai.identifier</ns1:title>            <ns1:map>              " +
    "<ns1:name set=\"oai\">identifier</ns1:name>              </ns1:map>            </ns1:index>          " +
    "<ns1:index>            <ns1:title>z3919.mappedTermsWords</ns1:title>            <ns1:map>              " +
    "<ns1:name set=\"z3919\">mappedTermsWords</ns1:name>              </ns1:map>            </ns1:index>          " +
    "<ns1:index>            <ns1:title>cql.resultSetId</ns1:title>            <ns1:map>              " +
    "<ns1:name set=\"cql\">resultSetId</ns1:name>              </ns1:map>            </ns1:index>          " +
    "<ns1:index>            <ns1:title>cql.serverChoice</ns1:title>            <ns1:map>              <ns1:name " +
    "set=\"cql\">serverChoice</ns1:name>              </ns1:map>            </ns1:index>          " +
    "</ns1:indexInfo>        <ns1:schemaInfo>          <ns1:schema identifier=\"info:srw/schema/1/marcxml-v1.1\" " +
    "location=\"http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\" name=\"MarcXML\" retrieve=\"true\" " +
    "sort=\"false\">            <ns1:title>MarcXML: MARC-21 records in XML</ns1:title>            </ns1:schema> " +
    "         <ns1:schema identifier=\"http://www.openarchives.org/OAI/2.0/#header\" " +
    "location=\"http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\" name=\"OaiHeader\" retrieve=\"true\"" +
    " sort=\"false\">            <ns1:title>OaiHeader: OAI Header</ns1:title>            </ns1:schema>         " +
    " <ns1:schema identifier=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" location=\"http://www.openarchives.org" +
    "/OAI/2.0/oai_dc.xsd\" name=\"oai_dc\" retrieve=\"true\" sort=\"false\">            " +
    "<ns1:title>oai_dc: OAI Dublin Core</ns1:title>            </ns1:schema>          </ns1:schemaInfo>        " +
    "<ns1:configInfo>          <ns1:numberOfRecords>0</ns1:numberOfRecords>          <ns1:maximumRecords>10" +
    "</ns1:maximumRecords>        <ns1:supports type=\"info:errol/defaultMDPrefix\"><ns2:defaultMDPrefix " +
    "xsi:schemaLocation=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema " +
    "http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema.xsd\" " +
    "xmlns=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema\" " +
    "xmlns:ns2=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema\">Zthes</ns2:defaultMDPrefix>" +
    "</ns1:supports>        <ns1:supports type=\"info:errol/display\"><ns3:xslIdentifier " +
    "xsi:schemaLocation=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema " +
    "http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema.xsd\" " +
    "xmlns=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema\" " +
    "xmlns:ns3=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema\">oaiViewer/zthes" +
    "</ns3:xslIdentifier></ns1:supports>        <ns1:supports type=\"info:errol/branding\"><ns4:branding " +
    "xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/branding/ " +
    "http://www.openarchives.org/OAI/2.0/branding.xsd\" xmlns=\"http://www.openarchives.org/OAI/2.0/branding/\" " +
    "xmlns:ns4=\"http://www.openarchives.org/OAI/2.0/branding/\"><ns4:metadataRendering metadataNamespace=\"\" " +
    "mimeType=\"text/xsl\">http://errol.oclc.org/oai:xmlregistry.oclc.org:oaiViewer/metadataFormat/Zthes.xsl" +
    "</ns4:metadataRendering></ns4:branding></ns1:supports>          </ns1:configInfo>        </ns1:explain>" +
    "</recordData><recordPosition xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" " +
    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRecordData xsi:type=\"ns1:extraDataType\" " +
    "xsi:nil=\"true\"/></record><echoedExplainRequest xsi:type=\"ns1:explainRequestType\" " +
    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><version xsi:type=\"xsd:string\" " +
     "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><recordPacking xsi:type=\"xsd:string\" " +
    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml</recordPacking><stylesheet xsi:type=\"xsd:anyURI\"" +
    " xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRequestData " +
    "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></echoedExplainRequest><diagnostics " +
    "xsi:type=\"ns1:diagnosticsType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
    "<extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" " +
    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:explainResponse>";
}