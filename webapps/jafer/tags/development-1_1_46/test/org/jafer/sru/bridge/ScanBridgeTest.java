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
 * This class tests the scan operation bridge
 */
public class ScanBridgeTest extends TestCase
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
        ScanOperationBridge bridge = new ScanOperationBridge();
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
     * Tests a simple scan request
     */
    public void testSimpleScanRequest()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response
            // instead we just check a couple of key parts
            expectedXML = XMLHEADER
                    + "<ns1:scanResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "1.1</version><terms xsi:type=\"ns1:termsType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                    + "<term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "beings</value><numberOfRecords lowestSetBit=\"1\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www"
                    + ".w3.org/2001/XMLSchema\">2</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:"
                    + "string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">beliefs</value><numberOfRecords lowestSetBit=\"1\" xsi:"
                    + "type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">2</numberOfRecords><displayTerm "
                    + "xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList "
                    + "xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:"
                    + "termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bellamy's</value>"
                    + "<numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\">1</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3."
                    + "org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" "
                    + "xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://"
                    + "www.w3.org/2001/XMLSchema\">belonging</value><numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger"
                    + "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</numberOfRecords><displayTerm xsi:type=\"xsd:string\" "
                    + "xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value "
                    + "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</value><numberOfRecords l"
                    + "owestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3"
                    + "</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/>"
                    + "</term><term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\">bible fiction</value><numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1</numberOfRecords><displayTerm xsi:type=\"xsd:string\" "
                    + "xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value "
                    + "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible films</value><numberOfRecords "
                    + "lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1"
                    + "</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/>"
                    + "</term><term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\">bible plays</value><numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1</numberOfRecords><displayTerm xsi:type=\"xsd:string\" "
                    + "xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value "
                    + "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">biblical</value><numberOfRecords "
                    + "lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1"
                    + "</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001"
                    + "/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\""
                    + "/></term></terms><echoedScanRequest xsi:type=\"ns1:echoedScanRequestType\" xmlns:xsi=\"http://www.w3.org/2001"
                    + "/XMLSchema-instance\"><version xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1"
                    + "</version><scanClause xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</scanClause>"
                    + "<xScanClause xsi:type=\"ns2:searchClauseType\" xmlns:ns2=\"http://www.loc.gov/zing/cql/xcql/\"><index "
                    + "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">cql.serverChoice</index><relation "
                    + "xsi:type=\"ns2:relationType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "scr</value><modifiers xsi:type=\"ns2:modifiersType\" xsi:nil=\"true\"/></relation><term xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</term></xScanClause><responsePosition "
                    + "xsi:type=\"xsd:nonNegativeInteger\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/>"
                    + "<maximumTerms xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\"/><stylesheet xsi:type=\"xsd:anyURI\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\"/><extraRequestData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></echoedScanRequest>"
                    + "<diagnostics xsi:type=\"ns1:diagnosticsType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/"
                    + "XMLSchema-instance\"/><extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:scanResponse>";
            // specifiy operation
            testName = "scan specified as operation with no version";
            params.put("scanClause", "bible");
            params.put("operation", "scan");
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
     * Tests a simple scan request with response position set to 2
     */
    public void testResponsePostionRequest()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response
            // instead we just check a couple of key parts
            expectedXML = XMLHEADER +
            "<ns1:scanResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1" +
            "</version><terms xsi:type=\"ns1:termsType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "belonging</value><numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http:" +
            "//www.w3.org/2001/XMLSchema\">3</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData xsi:type=\"" +
            "ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</value><numberOfRecords lowestSetBit=\"0\" xsi:type=\"" +
            "xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</numberOfRecords><displayTerm " +
            "xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList " +
            "xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term></terms>" +
            "<echoedScanRequest xsi:type=\"ns1:echoedScanRequestType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-" +
            "instance\"><version xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version>" +
            "<scanClause xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</scanClause>" +
            "<xScanClause xsi:type=\"ns2:searchClauseType\" xmlns:ns2=\"http://www.loc.gov/zing/cql/xcql/\">" +
            "<index xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">cql.serverChoice</index>" +
            "<relation xsi:type=\"ns2:relationType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001" +
            "/XMLSchema\">scr</value><modifiers xsi:type=\"ns2:modifiersType\" xsi:nil=\"true\"/></relation>" +
            "<term xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</term></xScanClause>" +
            "<responsePosition lowestSetBit=\"1\" xsi:type=\"xsd:nonNegativeInteger\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">2</responsePosition><maximumTerms " +
            "lowestSetBit=\"1\" xsi:type=\"xsd:positiveInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">2" +
            "</maximumTerms><stylesheet xsi:type=\"xsd:anyURI\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/" +
            "XMLSchema\"/><extraRequestData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></echoedScanRequest>" +
            "<diagnostics xsi:type=\"ns1:diagnosticsType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-" +
            "instance\"/><extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:scanResponse>";
            // specifiy operation
            testName = "scan specified as operation with no version";
            params.put("scanClause", "bible");
            params.put("operation", "scan");
            params.put("responsePosition", "2");
            params.put("maximumTerms", "2");
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
     * Tests a simple search request with max terms set to 1
     */
    public void testTestMaxTermsScanRequest()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response
            // instead we just check a couple of key parts
            expectedXML = XMLHEADER + 
            "<ns1:scanResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "1.1</version><terms xsi:type=\"ns1:termsType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
            "<echoedScanRequest xsi:type=\"ns1:echoedScanRequestType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<version xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><scanClause " +
            "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</scanClause><xScanClause " +
            "xsi:type=\"ns2:searchClauseType\" xmlns:ns2=\"http://www.loc.gov/zing/cql/xcql/\"><index xsi:type=\"" +
            "xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">cql.serverChoice</index><relation xsi:type=\"" +
            "ns2:relationType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">scr</value>" +
            "<modifiers xsi:type=\"ns2:modifiersType\" xsi:nil=\"true\"/></relation><term xsi:type=\"xsd:string\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</term></xScanClause>" +
            "<responsePosition xsi:type=\"xsd:nonNegativeInteger\" xsi:nil=\"true\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><maximumTerms lowestSetBit=\"0\" " +
            "xsi:type=\"xsd:positiveInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1" +
            "</maximumTerms><stylesheet xsi:type=\"xsd:anyURI\" xsi:nil=\"true\" xmlns:xsd=\"http:" +
            "//www.w3.org/2001/XMLSchema\"/><extraRequestData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/>" +
            "</echoedScanRequest><diagnostics xsi:type=\"ns1:diagnosticsType\" xsi:nil=\"true\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><extraResponseData xsi:type=\"ns1:extraDataType\" " +
            "xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:scanResponse>";
            // specifiy operation
            testName = "scan specified as operation with no version";
            params.put("scanClause", "bible");
            params.put("operation", "scan");
            params.put("maximumTerms", "1");
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
    public void testStylesheetSearchRequest()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            // there is a generated ID so we can not check the whole response
            // instead we just check a couple of key parts
            expectedXML = "<?xml-stylesheet type=\"text/xsl\" href=\"http://www.a.com/master.xsl\"?>"
                    + XMLHEADER
                    + "<ns1:scanResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "1.1</version><terms xsi:type=\"ns1:termsType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                    + "<term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "beings</value><numberOfRecords lowestSetBit=\"1\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www"
                    + ".w3.org/2001/XMLSchema\">2</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:"
                    + "string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">beliefs</value><numberOfRecords lowestSetBit=\"1\" xsi:"
                    + "type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">2</numberOfRecords><displayTerm "
                    + "xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList "
                    + "xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:"
                    + "termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bellamy's</value>"
                    + "<numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\">1</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3."
                    + "org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" "
                    + "xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://"
                    + "www.w3.org/2001/XMLSchema\">belonging</value><numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger"
                    + "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3</numberOfRecords><displayTerm xsi:type=\"xsd:string\" "
                    + "xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value "
                    + "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</value><numberOfRecords l"
                    + "owestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">3"
                    + "</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/>"
                    + "</term><term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\">bible fiction</value><numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1</numberOfRecords><displayTerm xsi:type=\"xsd:string\" "
                    + "xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value "
                    + "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible films</value><numberOfRecords "
                    + "lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1"
                    + "</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/>"
                    + "</term><term xsi:type=\"ns1:termType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\">bible plays</value><numberOfRecords lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1</numberOfRecords><displayTerm xsi:type=\"xsd:string\" "
                    + "xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData "
                    + "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></term><term xsi:type=\"ns1:termType\"><value "
                    + "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">biblical</value><numberOfRecords "
                    + "lowestSetBit=\"0\" xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1"
                    + "</numberOfRecords><displayTerm xsi:type=\"xsd:string\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001"
                    + "/XMLSchema\"/><whereInList xsi:nil=\"true\"/><extraTermData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\""
                    + "/></term></terms><echoedScanRequest xsi:type=\"ns1:echoedScanRequestType\" xmlns:xsi=\"http://www.w3.org/2001"
                    + "/XMLSchema-instance\"><version xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1"
                    + "</version><scanClause xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</scanClause>"
                    + "<xScanClause xsi:type=\"ns2:searchClauseType\" xmlns:ns2=\"http://www.loc.gov/zing/cql/xcql/\"><index "
                    + "xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">cql.serverChoice</index><relation "
                    + "xsi:type=\"ns2:relationType\"><value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                    + "scr</value><modifiers xsi:type=\"ns2:modifiersType\" xsi:nil=\"true\"/></relation><term xsi:type=\"xsd:string\" "
                    + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">bible</term></xScanClause><responsePosition "
                    + "xsi:type=\"xsd:nonNegativeInteger\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/>"
                    + "<maximumTerms xsi:type=\"xsd:positiveInteger\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\"/><stylesheet xsi:type=\"xsd:anyURI\" xsi:nil=\"true\" xmlns:xsd=\"http://www.w3.org/2001/"
                    + "XMLSchema\"/><extraRequestData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\"/></echoedScanRequest>"
                    + "<diagnostics xsi:type=\"ns1:diagnosticsType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/"
                    + "XMLSchema-instance\"/><extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:scanResponse>";
            // specifiy operation
            testName = "scan specified as operation with no version";
            params.put("scanClause", "bible");
            params.put("operation", "scan");
            params.put("stylesheet", "http://www.a.com/master.xsl");
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
     * Tests a bad style sheet param
     */
    public void testBADScanClauseParam()
    {
        String testName = "", expectedXML;
        Map params = new HashMap();
        try
        {
            testName = "search bad query";
            expectedXML = XMLHEADER
                    + "<ns1:scanResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" " +
                            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/" +
                            "XMLSchema\">1.1</version><terms xsi:type=\"ns1:termsType\" xsi:nil=\"true\" xmlns:xsi=\"" +
                            "http://www.w3.org/2001/XMLSchema-instance\"/><echoedScanRequest xsi:type=\"ns1:echoedScan" +
                            "RequestType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                            "<diagnostics xsi:type=\"ns1:diagnosticsType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema" +
                            "-instance\"><diagnostic xsi:type=\"ns2:diagnosticType\" xmlns:ns2=\"http://www.loc.gov/zing" +
                            "/srw/diagnostic/\"><uri xsi:type=\"xsd:anyURI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                            "info:srw/diagnostic/7</uri><details xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001" +
                            "/XMLSchema\">scanClause</details><message xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org" +
                            "/2001/XMLSchema\">No scan clause specified</message></diagnostic></diagnostics><extraResponseData " +
                            "xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema" +
                            "-instance\"/></ns1:scanResponse>";
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
            testName = "scan bad URL";
            expectedXML = XMLHEADER
                    + "<ns1:scanResponse xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" " +
                            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001" +
                            "/XMLSchema\">1.1</version><terms xsi:type=\"ns1:termsType\" xsi:nil=\"true\" xmlns:xsi=\"" +
                            "http://www.w3.org/2001/XMLSchema-instance\"/><echoedScanRequest xsi:type=\"" +
                            "ns1:echoedScanRequestType\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-" +
                            "instance\"/><diagnostics xsi:type=\"ns1:diagnosticsType\" xmlns:xsi=\"http://www.w3.org/2001" +
                            "/XMLSchema-instance\"><diagnostic xsi:type=\"ns2:diagnosticType\" xmlns:ns2=\"http://www.loc.gov" +
                            "/zing/srw/diagnostic/\"><uri xsi:type=\"xsd:anyURI\" xmlns:xsd=\"http://www.w3.org/2001/" +
                            "XMLSchema\">info:srw/diagnostic/2</uri><details xsi:type=\"xsd:string\" xmlns:xsd=\"http://" +
                            "www.w3.org/2001/XMLSchema\">(0)null</details><message xsi:type=\"xsd:string\" xmlns:xsd=\"" +
                            "http://www.w3.org/2001/XMLSchema\">Unable to contact server</message></diagnostic></diagnostics>" +
                            "<extraResponseData xsi:type=\"ns1:extraDataType\" xsi:nil=\"true\" " +
                            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:scanResponse>";
            params.put("scanClause", "golf");
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