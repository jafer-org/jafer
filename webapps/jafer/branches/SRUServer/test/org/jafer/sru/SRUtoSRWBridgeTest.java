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
package org.jafer.sru;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jafer.sru.bridge.SRUtoSRWBridge;

/**
 * This class tests the SRUtoSRWBridge
 */
public class SRUtoSRWBridgeTest extends TestCase
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
     * @return The XML generated
     * @throws SRUException
     * @throws MalformedURLException
     */
    private String runBridgeTest(String testName, String webURL, Map params, String expectedXML) throws SRUException,
            MalformedURLException
    {
        SRUtoSRWBridge bridge = new SRUtoSRWBridge(webURL);
        String xml = bridge.processRequest(params);
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
     * Tests the various ways an explain message can be requested
     */
    public void testExplainRequestVersions()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // no params
            testName = "explain due to no operations";
            expectedXML = XMLHEADER
                    + "<ns1:explainResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "1.1</version><record xsi:type=\"ns1:recordType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                    + "<recordSchema xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "http://explain.z3950.org/dtd/2.0/</recordSchema><recordPacking xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml</recordPacking><recordData "
                    + "xsi:type=\"ns1:stringOrXmlFragment\"><ns1:explain authoritative=\"true\" "
                    + "xmlns=\"http://explain.z3950.org/dtd/2.0/\" xmlns:ns1=\"http://explain.z3950.org/dtd/2.0/\">        "
                    + "<ns1:serverInfo protocol=\"SRW/U\">          <ns1:host>alcme.oclc.org</ns1:host>          "
                    + "<ns1:port>80</ns1:port>          <ns1:database>srw/search/GSAFD</ns1:database>          "
                    + "</ns1:serverInfo>        <ns1:databaseInfo>          <ns1:title>GSAFD Thesaurus</ns1:title>          "
                    + "<ns1:description>GSAFD Thesaurus</ns1:description>          <ns1:contact>Jeffrey A. Young (mailto:jyoung@oclc.org)"
                    + "</ns1:contact>          <ns1:implementation indentifier=\"http://www.oclc.org/research/software/srw\" "
                    + "version=\"1.1\">            <ns1:title>OCLC Research SRW Server version 1.1</ns1:title>            "
                    + "</ns1:implementation>          </ns1:databaseInfo>        <ns1:metaInfo>          </ns1:metaInfo>        "
                    + "<ns1:indexInfo>          <ns1:set identifier=\"info:srw/cql-context-set/1/z3919-v1.0\" name=\"z3919\"/>          "
                    + "<ns1:index>            <ns1:title>z3919.termID</ns1:title>            <ns1:map>              "
                    + "<ns1:name set=\"z3919\">termID</ns1:name>              </ns1:map>            </ns1:index>          "
                    + "<ns1:index>            <ns1:title>z3919.termNotes</ns1:title>            <ns1:map>              "
                    + "<ns1:name set=\"z3919\">termNotes</ns1:name>              </ns1:map>            </ns1:index>          "
                    + "<ns1:set identifier=\"info:srw/cql-context-set/1/cql-v1.1\" name=\"cql\"/>          <ns1:index>            "
                    + "<ns1:title>cql.any</ns1:title>            <ns1:map>              <ns1:name set=\"cql\">any</ns1:name>              "
                    + "</ns1:map>            </ns1:index>          <ns1:index>            <ns1:title>z3919.terms</ns1:title>            "
                    + "<ns1:map>              <ns1:name set=\"z3919\">terms</ns1:name>              </ns1:map>            "
                    + "</ns1:index>          <ns1:index>            <ns1:title>z3919.termsWords</ns1:title>            "
                    + "<ns1:map>              <ns1:name set=\"z3919\">termsWords</ns1:name>              </ns1:map>            "
                    + "</ns1:index>          <ns1:index>            <ns1:title>z3919.preferredTermName</ns1:title>            "
                    + "<ns1:map>              <ns1:name set=\"z3919\">preferredTermName</ns1:name>              </ns1:map>            "
                    + "</ns1:index>          <ns1:index>            <ns1:title>z3919.mappedTerms</ns1:title>            "
                    + "<ns1:map>              <ns1:name set=\"z3919\">mappedTerms</ns1:name>              </ns1:map>            "
                    + "</ns1:index>          <ns1:index>            <ns1:title>z3919.termQualifier</ns1:title>            "
                    + "<ns1:map>              <ns1:name set=\"z3919\">termQualifier</ns1:name>              </ns1:map>            "
                    + "</ns1:index>          <ns1:set identifier=\"info:srw/cql-context-set/1/oai-v1.0\" name=\"oai\"/>          "
                    + "<ns1:index>            <ns1:title>oai.datestamp</ns1:title>            <ns1:map>              "
                    + "<ns1:name set=\"oai\">datestamp</ns1:name>              </ns1:map>            </ns1:index>          "
                    + "<ns1:index>            <ns1:title>z3919.preferredTermNameWords</ns1:title>            <ns1:map>              "
                    + "<ns1:name set=\"z3919\">preferredTermNameWords</ns1:name>              </ns1:map>            </ns1:index>     "
                    + "     <ns1:index>            <ns1:title>oai.identifier</ns1:title>            <ns1:map>              "
                    + "<ns1:name set=\"oai\">identifier</ns1:name>              </ns1:map>            </ns1:index>          "
                    + "<ns1:index>            <ns1:title>z3919.mappedTermsWords</ns1:title>            <ns1:map>              "
                    + "<ns1:name set=\"z3919\">mappedTermsWords</ns1:name>              </ns1:map>            </ns1:index>          "
                    + "<ns1:index>            <ns1:title>cql.resultSetId</ns1:title>            <ns1:map>              "
                    + "<ns1:name set=\"cql\">resultSetId</ns1:name>              </ns1:map>            </ns1:index>          "
                    + "<ns1:index>            <ns1:title>cql.serverChoice</ns1:title>            <ns1:map>              <ns1:name "
                    + "set=\"cql\">serverChoice</ns1:name>              </ns1:map>            </ns1:index>          "
                    + "</ns1:indexInfo>        <ns1:schemaInfo>          <ns1:schema identifier=\"info:srw/schema/1/marcxml-v1.1\" "
                    + "location=\"http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\" name=\"MarcXML\" retrieve=\"true\" "
                    + "sort=\"false\">            <ns1:title>MarcXML: MARC-21 records in XML</ns1:title>            </ns1:schema> "
                    + "         <ns1:schema identifier=\"http://www.openarchives.org/OAI/2.0/#header\" "
                    + "location=\"http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\" name=\"OaiHeader\" retrieve=\"true\""
                    + " sort=\"false\">            <ns1:title>OaiHeader: OAI Header</ns1:title>            </ns1:schema>         "
                    + " <ns1:schema identifier=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" location=\"http://www.openarchives.org"
                    + "/OAI/2.0/oai_dc.xsd\" name=\"oai_dc\" retrieve=\"true\" sort=\"false\">            "
                    + "<ns1:title>oai_dc: OAI Dublin Core</ns1:title>            </ns1:schema>          </ns1:schemaInfo>        "
                    + "<ns1:configInfo>          <ns1:numberOfRecords>0</ns1:numberOfRecords>          <ns1:maximumRecords>10"
                    + "</ns1:maximumRecords>        <ns1:supports type=\"info:errol/defaultMDPrefix\"><ns2:defaultMDPrefix "
                    + "xsi:schemaLocation=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema "
                    + "http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema.xsd\" "
                    + "xmlns=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema\" "
                    + "xmlns:ns2=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema\">Zthes</ns2:defaultMDPrefix>"
                    + "</ns1:supports>        <ns1:supports type=\"info:errol/display\"><ns3:xslIdentifier "
                    + "xsi:schemaLocation=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema "
                    + "http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema.xsd\" "
                    + "xmlns=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema\" "
                    + "xmlns:ns3=\"http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/customERRoLSchema\">oaiViewer/zthes"
                    + "</ns3:xslIdentifier></ns1:supports>        <ns1:supports type=\"info:errol/branding\"><ns4:branding "
                    + "xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/branding/ "
                    + "http://www.openarchives.org/OAI/2.0/branding.xsd\" xmlns=\"http://www.openarchives.org/OAI/2.0/branding/\" "
                    + "xmlns:ns4=\"http://www.openarchives.org/OAI/2.0/branding/\"><ns4:metadataRendering metadataNamespace=\"\" "
                    + "mimeType=\"text/xsl\">http://errol.oclc.org/oai:xmlregistry.oclc.org:oaiViewer/metadataFormat/Zthes.xsl"
                    + "</ns4:metadataRendering></ns4:branding></ns1:supports>          </ns1:configInfo>        </ns1:explain>"
                    + "</recordData><recordPosition xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRecordData xsi:type=\"ns1:extraDataType\" "
                    + "xsi:nil=\"true\"/></record><echoedExplainRequest xsi:type=\"ns1:explainRequestType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><recordPacking xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml</recordPacking><stylesheet xsi:type=\"xsd:anyURI\""
                    + " xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRequestData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></echoedExplainRequest><diagnostics "
                    + "xsi:type=\"ns1:diagnosticsType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>"
                    + "<extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:explainResponse>";
            runBridgeTest(testName, STANDARD_WEBURL, params, expectedXML);
            // specifiy operation
            testName = "explain specified as operation with no version";
            params.put("operation", "explain");
            runBridgeTest(testName, STANDARD_WEBURL, params, expectedXML);
            // add version as well as option
            testName = "explain specified as operation and version";
            params.put("version", "1.1");
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
     * Tests the various ways an explain message can be requested.<br>
     * NOTE: This could fail if the data on the server has been updated so check
     * the response carefully if it should fail
     */
    public void testSearchRetrieveRequestVersions()
    {
        String testName = "", expectedXML= "",xml;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response instead we just check a couple of key parts
            String check1 = "<version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version>" +
                    "<numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</numberOfRecords>";
            String check2 = "<version xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                    "1.1</version><query xsi:type=\"xsd:string\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</query>";
            // specifiy operation
            testName = "explain specified as operation with no version";
            params.put("query", "bible");
            params.put("maximumRecords", "1");
            params.put("startRecord", "1");
            params.put("operation", "searchRetrieve");
            xml = runBridgeTest(testName, STANDARD_WEBURL, params, expectedXML);
            assertTrue("(" + testName + ") Mismatch on result",xml.indexOf(check1) != -1 && xml.indexOf(check2)!= -1);
            // add version as well as option
            testName = "explain specified as operation and version";
            params.put("version", "1.1");
            xml = runBridgeTest(testName, STANDARD_WEBURL, params, expectedXML);
            assertTrue("(" + testName + ") Mismatch on result",xml.indexOf(check1) != -1 && xml.indexOf(check2)!= -1);
            
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
     * Tests the various ways an explain message can be requested.<br>
     * NOTE: This could fail if the data on the server has been updated so check
     * the response carefully if it should fail
     */
    public void testScanRequestVersions()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            expectedXML = XMLHEADER + "<ns1:scanResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version " +
                    "xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><terms xsi:type=\"ns1:termsType\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><echoedScanRequest " +
                    "xsi:type=\"ns1:echoedScanRequestType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                    "<version xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version>" +
                    "<scanClause xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</scanClause>" +
                    "<xScanClause xsi:type=\"ns2:searchClauseType\" xmlns:ns2=\"http://www.loc.gov/zing/cql/xcql/\">" +
                    "<index xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">cql.serverChoice</index>" +
                    "<relation xsi:type=\"ns2:relationType\"><value xsi:type=\"xsd:string\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">scr</value><modifiers xsi:type=\"ns2:modifiersType\" " +
                    "xsi:nil=\"true\"/></relation><term xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2" +
                    "001/XMLSchema\">bible</term></xScanClause><responsePosition lowestSetBit=\"0\" " +
                    "xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1</responsePosition>" +
                    "<maximumTerms lowestSetBit=\"0\" xsi:type=\"xsd:positiveInteger\" xmlns:xsd=\"http://" +
                    "www.w3.org/2001/XMLSchema\">1</maximumTerms><stylesheet xsi:type=\"xsd:anyURI\" " +
                    "xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRequestData " +
                    "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></echoedScanRequest><diagnostics " +
                    "xsi:type=\"ns1:diagnosticsType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                    "<extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:scanResponse>";
            // specifiy operation
            testName = "explain specified as operation with no version";
            params.put("operation", "scan");
            params.put("scanClause", "bible");
            params.put("responsePosition", "1");
            params.put("maximumTerms", "1");
            runBridgeTest(testName, STANDARD_WEBURL, params, expectedXML);
            // add version as well as option
            testName = "explain specified as operation and version";
            params.put("version", "1.1");
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
     * Tests calling bad opertaion
     */
    public void testBADOperation()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // bad operation
            params.put("operation", "sillyop");
            testName = "explain due to no operations";
            expectedXML = XMLHEADER
                    + "<ns1:explainResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><record xsi:type=\"ns1:recordType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><recordSchema xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">info:srw/schema/1/diagnostics-v1.1</recordSchema>"
                    + "<recordPacking xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml"
                    + "</recordPacking><recordData xsi:type=\"ns1:stringOrXmlFragment\"><ns2:diagnostic "
                    + "xsi:type=\"xsd:string\" xmlns:ns2=\"http://www.loc.gov/zing/srw/diagnostic/\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">&lt;?xml version=&quot;1.0&quot; "
                    + "encoding=&quot;UTF-8&quot;?&gt;&lt;ns1:diagnostic xmlns:ns1=&quot;http://www.loc.gov/zing"
                    + "/srw/diagnostic/&quot;&gt;&lt;uri xsi:type=&quot;xsd:anyURI&quot; xmlns:xsi=&quot;"
                    + "http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http://www.w3.org/2001/"
                    + "XMLSchema&quot;&gt;info:srw/diagnostic/1&lt;/uri&gt;&lt;details xsi:type=&quot;xsd:string&quot;"
                    + " xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http:"
                    + "//www.w3.org/2001/XMLSchema&quot;&gt;&lt;/details&gt;&lt;message xsi:type=&quot;xsd:string&quot; "
                    + "xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http:"
                    + "//www.w3.org/2001/XMLSchema&quot;&gt;Unable to find supporting bridge for sillyop operation"
                    + " in configuration&lt;/message&gt;&lt;/ns1:diagnostic&gt;</ns2:diagnostic></recordData>"
                    + "<recordPosition xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRecordData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></record><echoedExplainRequest "
                    + "xsi:type=\"ns1:explainRequestType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001"
                    + "/XMLSchema-instance\"/><diagnostics xsi:type=\"ns1:diagnosticsType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><diagnostic "
                    + "xsi:type=\"ns3:diagnosticType\" xmlns:ns3=\"http://www.loc.gov/zing/srw/diagnostic/\">"
                    + "<uri xsi:type=\"xsd:anyURI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "info:srw/diagnostic/1</uri><details xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001"
                    + "/XMLSchema\"></details><message xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/20"
                    + "01/XMLSchema\">Unable to find supporting bridge for sillyop operation in configuration</message>"
                    + "</diagnostic></diagnostics><extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:explainResponse>";
            runBridgeTest(testName, STANDARD_WEBURL, params, expectedXML);

            // correct operation bad version
            params.put("operation", "explain");
            params.put("version", "9.9");
            testName = "explain due to bad version";
            expectedXML = XMLHEADER
                    + "<ns1:explainResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><record xsi:type=\"ns1:recordType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><recordSchema xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">info:srw/schema/1/diagnostics-v1.1</recordSchema>"
                    + "<recordPacking xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml"
                    + "</recordPacking><recordData xsi:type=\"ns1:stringOrXmlFragment\"><ns2:diagnostic "
                    + "xsi:type=\"xsd:string\" xmlns:ns2=\"http://www.loc.gov/zing/srw/diagnostic/\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">&lt;?xml version=&quot;1.0&quot; "
                    + "encoding=&quot;UTF-8&quot;?&gt;&lt;ns1:diagnostic xmlns:ns1=&quot;http://www.loc.gov/zing"
                    + "/srw/diagnostic/&quot;&gt;&lt;uri xsi:type=&quot;xsd:anyURI&quot; xmlns:xsi=&quot;"
                    + "http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http://www.w3.org/2001/"
                    + "XMLSchema&quot;&gt;info:srw/diagnostic/1&lt;/uri&gt;&lt;details xsi:type=&quot;xsd:string&quot;"
                    + " xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http:"
                    + "//www.w3.org/2001/XMLSchema&quot;&gt;&lt;/details&gt;&lt;message xsi:type=&quot;xsd:string&quot; "
                    + "xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http:"
                    + "//www.w3.org/2001/XMLSchema&quot;&gt;Unable to find supporting bridge for explain operation"
                    + " in configuration&lt;/message&gt;&lt;/ns1:diagnostic&gt;</ns2:diagnostic></recordData>"
                    + "<recordPosition xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRecordData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></record><echoedExplainRequest "
                    + "xsi:type=\"ns1:explainRequestType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001"
                    + "/XMLSchema-instance\"/><diagnostics xsi:type=\"ns1:diagnosticsType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><diagnostic "
                    + "xsi:type=\"ns3:diagnosticType\" xmlns:ns3=\"http://www.loc.gov/zing/srw/diagnostic/\">"
                    + "<uri xsi:type=\"xsd:anyURI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "info:srw/diagnostic/1</uri><details xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001"
                    + "/XMLSchema\"></details><message xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/20"
                    + "01/XMLSchema\">Unable to find supporting bridge for explain operation in configuration</message>"
                    + "</diagnostic></diagnostics><extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:explainResponse>";

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
     * Tests calling bad version
     */
    public void testBADVersion()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // correct operation bad version
            params.put("operation", "explain");
            params.put("version", "9.9");
            testName = "explain due to no operations";
            expectedXML = XMLHEADER
                    + "<ns1:explainResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><record xsi:type=\"ns1:recordType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><recordSchema xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">info:srw/schema/1/diagnostics-v1.1</recordSchema>"
                    + "<recordPacking xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml"
                    + "</recordPacking><recordData xsi:type=\"ns1:stringOrXmlFragment\"><ns2:diagnostic "
                    + "xsi:type=\"xsd:string\" xmlns:ns2=\"http://www.loc.gov/zing/srw/diagnostic/\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">&lt;?xml version=&quot;1.0&quot; "
                    + "encoding=&quot;UTF-8&quot;?&gt;&lt;ns1:diagnostic xmlns:ns1=&quot;http://www.loc.gov/zing"
                    + "/srw/diagnostic/&quot;&gt;&lt;uri xsi:type=&quot;xsd:anyURI&quot; xmlns:xsi=&quot;"
                    + "http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http://www.w3.org/2001/"
                    + "XMLSchema&quot;&gt;info:srw/diagnostic/1&lt;/uri&gt;&lt;details xsi:type=&quot;xsd:string&quot;"
                    + " xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http:"
                    + "//www.w3.org/2001/XMLSchema&quot;&gt;&lt;/details&gt;&lt;message xsi:type=&quot;xsd:string&quot; "
                    + "xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http:"
                    + "//www.w3.org/2001/XMLSchema&quot;&gt;Unable to find supporting bridge for explain operation"
                    + " in configuration&lt;/message&gt;&lt;/ns1:diagnostic&gt;</ns2:diagnostic></recordData>"
                    + "<recordPosition xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRecordData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></record><echoedExplainRequest "
                    + "xsi:type=\"ns1:explainRequestType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001"
                    + "/XMLSchema-instance\"/><diagnostics xsi:type=\"ns1:diagnosticsType\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><diagnostic "
                    + "xsi:type=\"ns3:diagnosticType\" xmlns:ns3=\"http://www.loc.gov/zing/srw/diagnostic/\">"
                    + "<uri xsi:type=\"xsd:anyURI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "info:srw/diagnostic/1</uri><details xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001"
                    + "/XMLSchema\"></details><message xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/20"
                    + "01/XMLSchema\">Unable to find supporting bridge for explain operation in configuration</message>"
                    + "</diagnostic></diagnostics><extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:explainResponse>";

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
     * Tests bridge set up to non with invalid url syntax server
     */
    public void testBADURL()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // bad operation
            params.put("operation", "sillyop");
            testName = "explain due to no operations";
            expectedXML = "<ERROR>NEED TO CODE THIS ERROR SECTION</ERROR>";
            runBridgeTest(testName, "a$3fsfsf", params, expectedXML);
            fail("(" + testName + ") should have been bad url");
        }
        catch (SRUException exc)
        {
            exc.printStackTrace();
            fail("SRUException Exception: (" + testName + ") " + exc);
        }
        catch (MalformedURLException exc)
        {
            // success
        }
    }

    /**
     * Tests bridge set up to non existenet server
     */
    public void testValidURLButToNonExistenetServer()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // bad operation
            params.put("operation", "sillyop");
            testName = "explain due to no operations";
            expectedXML = XMLHEADER
                    + "<ns1:explainResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/20"
                    + "01/XMLSchema\">1.1</version><record xsi:type=\"ns1:recordType\" xmlns:xsi=\"http://www.w"
                    + "3.org/2001/XMLSchema-instance\"><recordSchema xsi:type=\"xsd:string\" xmlns:xsd=\"http://"
                    + "www.w3.org/2001/XMLSchema\">info:srw/schema/1/diagnostics-v1.1</recordSchema><recordPacking"
                    + " xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">xml</recordPacking>"
                    + "<recordData xsi:type=\"ns1:stringOrXmlFragment\"><ns2:diagnostic xsi:type=\"xsd:string\" "
                    + "xmlns:ns2=\"http://www.loc.gov/zing/srw/diagnostic/\" xmlns:xsd=\"http://www.w3.org/2001/XM"
                    + "LSchema\">&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&lt;ns1:diagnostic "
                    + "xmlns:ns1=&quot;http://www.loc.gov/zing/srw/diagnostic/&quot;&gt;&lt;uri xsi:type=&quot;xsd"
                    + ":anyURI&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; "
                    + "xmlns:xsd=&quot;http://www.w3.org/2001/XMLSchema&quot;&gt;info:srw/diagnostic/1&lt;/uri&gt;&lt;"
                    + "details xsi:type=&quot;xsd:string&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instan"
                    + "ce&quot; xmlns:xsd=&quot;http://www.w3.org/2001/XMLSchema&quot;&gt;&lt;/details&gt;&lt;message "
                    + "xsi:type=&quot;xsd:string&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; "
                    + "xmlns:xsd=&quot;http://www.w3.org/2001/XMLSchema&quot;&gt;"
                    + "Unable to find supporting bridge for sillyop operation in configuration&lt;/message&gt;&lt;"
                    + "/ns1:diagnostic&gt;</ns2:diagnostic></recordData><recordPosition xsi:type=\"xsd:positiveInteger\" "
                    + "xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><extraRecordData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></record><echoedExplainRequest"
                    + " xsi:type=\"ns1:explainRequestType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/"
                    + "XMLSchema-instance\"/><diagnostics xsi:type=\"ns1:diagnosticsType\" xmlns:xsi=\"http://www."
                    + "w3.org/2001/XMLSchema-instance\"><diagnostic xsi:type=\"ns3:diagnosticType\""
                    + " xmlns:ns3=\"http://www.loc.gov/zing/srw/diagnostic/\"><uri xsi:type=\"xsd:anyURI\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">info:srw/diagnostic/1</uri><details "
                    + "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"></details>"
                    + "<message xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "Unable to find supporting bridge for sillyop operation in configuration</message>"
                    + "</diagnostic></diagnostics><extraResponseData xsi:type=\"ns1:extraDataType\" "
                    + "xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:explainResponse>";
            runBridgeTest(testName, "http://www.notthere.com/", params, expectedXML);
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
