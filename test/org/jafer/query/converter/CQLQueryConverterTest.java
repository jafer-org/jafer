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
package org.jafer.query.converter;

import junit.framework.TestCase;

import org.jafer.exception.JaferException;
import org.jafer.interfaces.QueryBuilder;
import org.jafer.query.CQLQuery;
import org.jafer.query.JaferQuery;
import org.jafer.query.JaferQueryTestObject;
import org.jafer.query.QueryException;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This test class tests the CQLQueryConverter to make sure that it converts
 * JAFER to CQL correctly and vice versa
 */
public class CQLQueryConverterTest extends TestCase
{

    /**
     * Stores a reference to the XMLHEADER text
     */
    private static final String XMLHEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";

    /**
     * Stores a reference to the query builder for building test data
     */
    private QueryBuilder builder = new org.jafer.query.QueryBuilder();

    /**
     * Runs a conversion of the jafer query to XCQL and then onto CQL and checks
     * that the result from both conversion match what is expected
     *
     * @param testName The name of the test
     * @param jq the jafer query to convert
     * @param expectedXCQL the expected output XCQL
     * @param expectedCQL the expected output CQL
     * @throws QueryException
     */
    private void runJQFtoCQLTest(String testName, JaferQuery jq, String expectedXCQL, String expectedCQL) throws QueryException
    {
        CQLQuery cqlQ = jq.toCQLQuery();
        String cql = cqlQ.getCQLQuery();
        String xcql = cqlQ.getXML();
        // only test if expected XML supplied otherwise exception was expected
        if (expectedXCQL.length() > 0)
        {
            // make sure the XML matches as expected
            assertEquals("(" + testName + ") Input and returned output XCQL do not match", expectedXCQL, xcql);
        }
        // only test if expected CQL supplied otherwise exception was expected
        if (expectedCQL.length() > 0)
        {
            // make sure the XML matches as expected
            assertEquals("(" + testName + ") Input and returned output CQL do not match", expectedCQL, cql);
        }
    }

    /**
     * Runs a conversion from CQL to JQF and checks that the result from both
     * conversion match what is expected
     *
     * @param testName The name of the test being run
     * @param cq The CQLQuery being converted
     * @param expectedJQF The XML JQF expected
     * @throws QueryException
     */
    private void runXCQLToJQFTest(String testName, CQLQuery cq, String expectedJQF) throws QueryException
    {
        JaferQuery jqf = cq.toJaferQuery();
        // only test if expected XML supplied otherwise exception was expected
        if (expectedJQF.length() > 0)
        {
            // make sure the XML matches as expected
            assertEquals("(" + testName + ") Input and returned output JQF do not match", expectedJQF, jqf.getXML());
        }
    }

    /**
     * Runs a conversion from CQL to JQF and checks that the result from both
     * conversion match what is expected
     *
     * @param testName The name of the test being run
     * @param cql The cql being converted
     * @param expectedCQL The XCQL expected
     * @throws QueryException
     */
    private void runCQLToXCQLTest(String testName, String cql, String expectedXCQL) throws QueryException
    {
        CQLQuery cqf = new CQLQuery(cql);
        // only test if expected XML supplied otherwise exception was expected
        if (expectedXCQL.length() > 0)
        {
            // make sure the XML matches as expected
            assertEquals("(" + testName + ") Input and returned output XCQL do not match", expectedXCQL, cqf.getXML());
        }
    }

    /**
     * Tests the processing of a model only
     */
    public void testJQFtoXCQLandCQLConstraintModelOnlywithJustModel()
    {
        String testName = "";
        try
        {
            // need to manually create query as this is not reaaly supported in
            // Jafer
            Document document = DOMFactory.newDocument();
            Node constraintModel = document.createElement("constraintModel");
            Node n = constraintModel.appendChild(document.createElement("model"));
            n.appendChild(document.createTextNode("Shakespear"));

            String expectedXCQL = XMLHEADER + "<XCQL><searchClause><term>Shakespear</term></searchClause></XCQL>";
            String expectedCQL = "\"Shakespear\"";
            testName = "just Term";
            runJQFtoCQLTest(testName, new JaferQuery(constraintModel), expectedXCQL, expectedCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests retrieval of term,semantic and auto relation generation
     */
    public void testJQFtoXCQLandCQLConstraintModelOnlyWithJustSemanticAndTerm()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");

            String expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation>" + "<term>Shakespear</term></searchClause></XCQL>";
            String expectedCQL = "dc.creator = \"Shakespear\"";
            testName = "Semantic and Term";
            runJQFtoCQLTest(testName, new JaferQuery(author), expectedXCQL, expectedCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests semantic with relation
     */
    public void testJQFtoXCQLandCQLConstraintModelOnlyWithSemanticRelationAndTerm()
    {
        String testName = "";
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "0", "0", "0" });
            String expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>&lt;</value></relation><term>Shakespear</term></searchClause></XCQL>";
            String expectedCQL = "dc.creator < \"Shakespear\"";
            testName = "author(<) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "2", "0", "0", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>&lt;=</value></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator <= \"Shakespear\"";
            testName = "author(<=) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "5", "0", "0", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>&gt;</value></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator > \"Shakespear\"";
            testName = "author(>) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "4", "0", "0", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>&gt;=</value></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator >= \"Shakespear\"";
            testName = "author(>=) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"Shakespear\"";
            testName = "author(=) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "6", "0", "0", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>&lt;&gt;</value></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator <> \"Shakespear\"";
            testName = "author(<>) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "100", "0", "0", "0", "0" });
            expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value><modifiers><modifier><type>phonetic</type></modifier></modifiers></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/phonetic \"Shakespear\"";
            testName = "author(phonetic) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "101", "0", "0", "0", "0" });
            expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value><modifiers><modifier><type>stem</type></modifier></modifiers></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/stem \"Shakespear\"";
            testName = "author(stem) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "102", "0", "0", "0", "0" });
            expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value><modifiers><modifier><type>relevant</type></modifier></modifiers></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/relevant \"Shakespear\"";
            testName = "author(relevance) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests the special case of relevance of all matches converts to all
     * records
     */
    public void testJQFtoXCQLandCQLConstraintModelOnlyWithSpecialCaseAAndTerm()
    {
        String testName = "";
        try
        {

            Node query = builder.getNode(new String[] { "Shakespear", "1003", "103", "0", "0", "0", "0" });
            String expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>cql.allRecords</index>"
                    + "<relation><value>=</value></relation><term>1</term></searchClause></XCQL>";
            String expectedCQL = "cql.allRecords = \"1\"";
            testName = "author(allmatches) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests the case of defining relation but no semantic
     */
    public void testJQFtoXCQLandCQLBADConstraintModelOnlyMissingSemantic()
    {
        String testName = "";
        try
        {
            // need to manually create error
            Document document = DOMFactory.newDocument();
            Node constraintModel, constraint, n;
            constraintModel = document.createElement("constraintModel");
            n = constraintModel.appendChild(document.createElement("model"));
            n.appendChild(document.createTextNode("oliver"));
            constraint = constraintModel.appendChild(document.createElement("constraint"));
            n = constraint.appendChild(document.createElement("relation"));
            n.appendChild(document.createTextNode("3"));

            testName = "author(missing semantic) and Term";
            runJQFtoCQLTest(testName, new JaferQuery(constraintModel), "", "");
            fail("(" + testName + ")Should have failed due to missing semantic");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
    }

    /**
     * Tests failure when semantic not in cql file
     */
    public void testJQFtoXCQLandCQLBADConstraintModelSemanticNotDefined()
    {
        String testName = "";
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1", "1", "0", "0", "0", "0" });
            testName = "Semantic not defined with relation";
            runJQFtoCQLTest(testName, new JaferQuery(query), "", "");
            fail("(" + testName + ")Should have failed due to missing semantic in cql file");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1", "0", "0", "0", "0", "0" });
            testName = "Semantic not defined with no relation";
            runJQFtoCQLTest(testName, new JaferQuery(query), "", "");
            fail("(" + testName + ")Should have failed due to missing semantic in cql file");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
    }

    /**
     * Tests failure when relation not in cql file
     */
    public void testJQFtoXCQLandCQLBADConstraintModelRelationNotDefined()
    {
        String testName = "";
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1003", "55", "0", "0", "0", "0" });
            testName = "relation not defined ";
            runJQFtoCQLTest(testName, new JaferQuery(query), "", "");
            fail("(" + testName + ")Should have failed due to missing relation in cql file");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
    }

    /**
     * Tests failure when structure not in cql file
     */
    public void testJQFtoXCQLandCQLBADConstraintModelStructureNotDefined()
    {
        String testName = "";
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "9", "0", "0" });
            testName = "structure not defined with relation";
            runJQFtoCQLTest(testName, new JaferQuery(query), "", "");
            fail("(" + testName + ")Should have failed due to missing structure in cql file");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "12", "0", "0" });
            testName = "structure not defined without relation";
            runJQFtoCQLTest(testName, new JaferQuery(query), "", "");
            fail("(" + testName + ")Should have failed due to missing structure in cql file");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
    }

    /**
     * Tests failure when position not in cql file
     */
    public void testJQFtoXCQLandCQLBADConstraintModelPositionNotDefined()
    {
        String testName = "";
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "9", "0" });
            testName = "position not defined with relation";
            runJQFtoCQLTest(testName, new JaferQuery(query), "", "");
            fail("(" + testName + ")Should have failed due to missing position in cql file");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
    }

    /**
     * Tests failure when truncation not in cql file
     */
    public void testJQFtoXCQLandCQLBADConstraintModelTruncationNotDefined()
    {
        String testName = "";
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "44", "0", "0" });
            testName = "position not defined with truncation";
            runJQFtoCQLTest(testName, new JaferQuery(query), "", "");
            fail("(" + testName + ")Should have failed due to missing truncation in cql file");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
    }

    /**
     * Tests failure when completeness not in cql file
     */
    public void testJQFtoXCQLandCQLBADConstraintModelCompletenessNotDefined()
    {
        String testName = "";
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "0", "6" });
            testName = "position not defined with completeness";
            runJQFtoCQLTest(testName, new JaferQuery(query), "", "");
            fail("(" + testName + ")Should have failed due to missing completeness in cql file");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }

    }

    /**
     * Tests constrain models with structure
     */
    public void testJQFtoXCQLandCQLConstraintModelOnlyWithStructure()
    {
        String testName = "";
        try
        {

            Node query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "1", "0", "0" });
            String expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            String expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of Phrase";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "2", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.word</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.word \"Shakespear\"";
            testName = "Structure of word";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "3", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of key";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "4", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.number</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.number \"Shakespear\"";
            testName = "Structure of year";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "5", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.isodate</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.isodate \"Shakespear\"";
            testName = "Structure of date";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "6", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of word list";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "100", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of date un normalised";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "101", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of name";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "102", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of name un normalised";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "103", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of stucture";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "104", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.uri</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.uri \"Shakespear\"";
            testName = "Structure of urx";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "105", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of free form text";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "106", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of document text";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "107", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.number</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.number \"Shakespear\"";
            testName = "Structure of local number";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "108", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of string";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "109", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator =/cql.string \"Shakespear\"";
            testName = "Structure of numeric string";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "1", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of Phrase";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "2", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.word</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.word \"Shakespear\"";
            testName = "Structure/Relation of word";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "3", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of key";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "4", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.number</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.number \"Shakespear\"";
            testName = "Structure/Relation of year";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "5", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.isodate</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.isodate \"Shakespear\"";
            testName = "Structure/Relation of date";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "6", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of word list";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "100", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of date un normalised";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "101", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of name";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "102", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of name un normalised";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "103", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of stucture";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "104", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.uri</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.uri \"Shakespear\"";
            testName = "Structure/Relation of urx";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "105", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of free form text";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "106", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of document text";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "107", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.number</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.number \"Shakespear\"";
            testName = "Structure/Relation of local number";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "108", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of string";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "1", "0", "109", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value><modifiers>"
                    + "<modifier><type>cql.string</type></modifier></modifiers></relation>"
                    + "<term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator </cql.string \"Shakespear\"";
            testName = "Structure/Relation of numeric string";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests the position constraint
     */
    public void testJQFtoXCQLandCQLConstraintModelWithPosition()
    {
        String testName = "";
        try
        {

            Node query = builder.getNode(new String[] { "Shakespear", "1003", "3", "1", "0", "0", "0" });
            String expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>^Shakespear</term></searchClause></XCQL>";
            String expectedCQL = "dc.creator = \"^Shakespear\"";
            testName = "Position first";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "2", "0", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"Shakespear\"";
            testName = "Position first sub field";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "3", "0", "0", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"Shakespear\"";
            testName = "Position any";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests the truncation constraint
     */
    public void testJQFtoXCQLandCQLConstraintModelWithTruncation()
    {
        String testName = "";
        try
        {

            Node query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "1", "0" });
            String expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>*Shakespear</term></searchClause></XCQL>";
            String expectedCQL = "dc.creator = \"*Shakespear\"";
            testName = "truncation left";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "2", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>Shakespear*</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"Shakespear*\"";
            testName = "truncation right ";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "3", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>*Shakespear*</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"*Shakespear*\"";
            testName = "truncation left & right";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "100", "0" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>^Shakespear^</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"^Shakespear^\"";
            testName = "no truncation";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests the truncation constraint
     */
    public void testJQFtoXCQLandCQLConstraintModelWithCompletness()
    {
        String testName = "";
        try
        {

            Node query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "0", "1" });
            String expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>*Shakespear*</term></searchClause></XCQL>";
            String expectedCQL = "dc.creator = \"*Shakespear*\"";
            testName = "completeness field";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "0", "2" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>^Shakespear^</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"^Shakespear^\"";
            testName = "completeness sub field complete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "0", "3" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>^Shakespear^</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"^Shakespear^\"";
            testName = "completeness complete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests the truncation and completenessconstraint
     */
    public void testJQFtoXCQLandCQLConstraintModelWithTruncationCompleteness()
    {
        String testName = "";
        try
        {

            Node query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "1", "1" });
            String expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>*Shakespear*</term></searchClause></XCQL>";
            String expectedCQL = "dc.creator = \"*Shakespear*\"";
            testName = "truncation left & incomplete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "2", "1" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>*Shakespear*</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"*Shakespear*\"";
            testName = "truncation right & incomplete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "3", "1" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>*Shakespear*</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"*Shakespear*\"";
            testName = "truncation left & right & incomplete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "100", "1" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>*Shakespear*</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"*Shakespear*\"";
            testName = "no truncation & incomplete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "1", "3" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>*Shakespear</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"*Shakespear\"";
            testName = "truncation left & complete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "2", "3" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>Shakespear*</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"Shakespear*\"";
            testName = "truncation right & complete ";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "3", "3" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>*Shakespear*</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"*Shakespear*\"";
            testName = "truncation left & right & complete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "100", "3" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>^Shakespear^</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"^Shakespear^\"";
            testName = "no truncation & complete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests the truncation constraint
     */
    public void testJQFtoXCQLandCQLConstraintModelWithPositionTruncationCompletness()
    {
        String testName = "";
        try
        {
            Node query = builder.getNode(new String[] { "Shakespear", "1003", "3", "1", "0", "1", "0" });
            String expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>^*Shakespear</term></searchClause></XCQL>";
            String expectedCQL = "dc.creator = \"^*Shakespear\"";
            testName = "first in field and right trunc";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "1", "0", "3", "2" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>^*Shakespear*</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"^*Shakespear*\"";
            testName = "first in field and rightleft trunc";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);

            query = builder.getNode(new String[] { "Shakespear", "1003", "3", "1", "0", "0", "3" });
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index>"
                    + "<relation><value>=</value></relation><term>^^Shakespear^</term></searchClause></XCQL>";
            expectedCQL = "dc.creator = \"^^Shakespear^\"";
            testName = "first in field and complete";
            runJQFtoCQLTest(testName, new JaferQuery(query), expectedXCQL, expectedCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests top level NOT
     */
    public void testJQFtoXCQLandCQLTopLevelNot()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "0", "0" });
            Node query = builder.not(author);
            String expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><searchClause><index>cql.allRecords</index><relation><value>=</value></relation><term>1</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.creator</index><relation><value>=</value></relation>"
                    + "<term>Shakespear</term></searchClause></rightOperand></triple></XCQL>";
            String expectedCQL = "cql.allRecords = \"1\" not dc.creator = \"Shakespear\"";

            testName = "top level not";
            runJQFtoCQLTest(testName, new JaferQuery(query, true), expectedXCQL, expectedCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests top level AND
     */
    public void testJQFtoXCQLandCQLTopLevelAnd()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "0", "0" });
            Node title = builder.getNode(new String[] { "Oliver", "4", "3", "0", "0", "0", "0" });

            Node query = builder.and(author, title);
            String expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>and</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.creator</index><relation><value>=</value></relation><term>Shakespear</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>Oliver</term></searchClause></rightOperand></triple></XCQL>";
            String expectedCQL = "dc.creator = \"Shakespear\" and dc.title = \"Oliver\"";

            testName = "top level and";
            runJQFtoCQLTest(testName, new JaferQuery(query, true), expectedXCQL, expectedCQL);

            query = builder.and(author, builder.not(title));
            expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.creator</index><relation><value>=</value></relation><term>Shakespear</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>Oliver</term></searchClause></rightOperand></triple></XCQL>";
            expectedCQL = "dc.creator = \"Shakespear\" not dc.title = \"Oliver\"";

            testName = "top level and, right NOT";
            runJQFtoCQLTest(testName, new JaferQuery(query, true), expectedXCQL, expectedCQL);

            query = builder.and(builder.not(author), title);
            expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>Oliver</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.creator</index><relation><value>=</value></relation><term>Shakespear</term></searchClause></rightOperand></triple></XCQL>";
            expectedCQL = "dc.title = \"Oliver\" not dc.creator = \"Shakespear\"";

            testName = "top level and, left NOT";
            runJQFtoCQLTest(testName, new JaferQuery(query, true), expectedXCQL, expectedCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests top level OR
     */
    public void testJQFtoXCQLandCQLTopLevelOr()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "0", "0" });
            Node title = builder.getNode(new String[] { "Oliver", "4", "3", "0", "0", "0", "0" });

            Node query = builder.or(author, title);
            String expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>or</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.creator</index><relation><value>=</value></relation><term>Shakespear</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>Oliver</term></searchClause></rightOperand></triple></XCQL>";
            String expectedCQL = "dc.creator = \"Shakespear\" or dc.title = \"Oliver\"";

            testName = "top level or";
            runJQFtoCQLTest(testName, new JaferQuery(query, true), expectedXCQL, expectedCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests complex Query
     */
    public void testJQFtoXCQLandCQLComplexQueries()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode(new String[] { "Shakespear", "1003", "3", "0", "0", "0", "0" });
            Node title = builder.getNode(new String[] { "Oliver", "4", "3", "0", "0", "0", "0" });
            Node title2 = builder.getNode(new String[] { "ChristmasCarol", "4", "3", "0", "0", "0", "0" });

            Node query = builder.or(author, builder.and(builder.not(title), builder.not(title2)));
            String expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><searchClause><index>cql.allRecords</index><relation><value>=</value></relation><term>1</term></searchClause></leftOperand>"
                    + "<rightOperand><triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><triple><boolean><value>or</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>Oliver</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>ChristmasCarol</term></searchClause></rightOperand></triple></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.creator</index><relation><value>=</value></relation><term>Shakespear</term></searchClause></rightOperand></triple></rightOperand></triple></XCQL>";
            String expectedCQL = "cql.allRecords = \"1\" not ((dc.title = \"Oliver\" or dc.title = \"ChristmasCarol\") not dc.creator = \"Shakespear\")";
            testName = "complex query 1";
            runJQFtoCQLTest(testName, new JaferQuery(query, true), expectedXCQL, expectedCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests prefixes convert from XCQL to CQL
     */
    public void testJQFtoXCQLandCQLXCQLPrefixes()
    {
        String testName = "";

        try
        {
            String xCql = XMLHEADER
                    + "<XCQL><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier>"
                    + "</prefix></prefixes><searchClause><term>Shakespear</term></searchClause></XCQL>";
            String expectedCQL = ">bath=\"http://zing.z3950.org/cql/bath/2.0/\" \"Shakespear\"";
            testName = "Top level prefixes";

            Node query = (Node) DOMFactory.parse(xCql);
            query.normalize();
            CQLQuery cq = new CQLQuery(query);
            assertEquals("(" + testName + ") Input and returned output CQL do not match", expectedCQL, cq.getCQLQuery());

            xCql = XMLHEADER
                    + "<XCQL><triple><boolean><value>or</value></boolean>"
                    + "<leftOperand><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.creator</index><relation><value>=</value></relation><term>Shakespear</term></searchClause></leftOperand>"
                    + "<rightOperand><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value></relation><term>Oliver</term></searchClause></rightOperand></triple></XCQL>";
            expectedCQL = ">bath=\"http://zing.z3950.org/cql/bath/2.0/\" dc.creator = \"Shakespear\" or >bath=\"http://zing.z3950.org/cql/bath/2.0/\" dc.title = \"Oliver\"";
            testName = "tripple level prefixes 1";
            query = (Node) DOMFactory.parse(xCql);
            query.normalize();
            cq = new CQLQuery(query);
            assertEquals("(" + testName + ") Input and returned output CQL do not match", expectedCQL, cq.getCQLQuery());

            xCql = XMLHEADER
                    + "<XCQL><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><searchClause><index>cql.allRecords</index><relation><value>=</value></relation><term>1</term></searchClause></leftOperand>"
                    + "<rightOperand><triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><triple><boolean><value>or</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>Oliver</term></searchClause></leftOperand>"
                    + "<rightOperand><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value></relation><term>ChristmasCarol</term></searchClause></rightOperand></triple></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.creator</index><relation><value>=</value></relation><term>Shakespear</term></searchClause></rightOperand></triple></rightOperand></triple></XCQL>";
            expectedCQL = ">bath=\"http://zing.z3950.org/cql/bath/2.0/\" cql.allRecords = \"1\" not ((dc.title = \"Oliver\" or >bath=\"http://zing.z3950.org/cql/bath/2.0/\" dc.title = \"ChristmasCarol\") not dc.creator = \"Shakespear\")";
            testName = "tripple level prefixes 2";

            query = (Node) DOMFactory.parse(xCql);
            query.normalize();
            cq = new CQLQuery(query);
            assertEquals("(" + testName + ") Input and returned output CQL do not match", expectedCQL, cq.getCQLQuery());
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on unnormalised AND
     * queries. Uses a JaferQueryTestObject to allow unnormalised data.
     */
    public void testJQFtoXCQLandCQLUnNormalisedANDQueries()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node query = builder.and(builder.not(author), title);
            testName = "Unnormalised AND - NOT in left operand";
            runJQFtoCQLTest(testName, new JaferQueryTestObject(query), "", "");

            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("Stylesheet directed termination") == -1)
            {
                fail("Query Exception (" + testName + "): " + e);
            }
        }
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node query = builder.and(builder.not(author), builder.not(title));
            testName = "Unnormalised AND - NOT in both operands";

            runJQFtoCQLTest(testName, new JaferQueryTestObject(query), "", "");
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("Stylesheet directed termination") == -1)
            {
                fail("Query Exception (" + testName + "): " + e);
            }
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on unnormalised OR
     * queries. Uses a JaferQueryTestObject to allow unnormalised data.
     */
    public void testJQFtoXCQLandCQLUnNormalisedORQueries()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node query = builder.or(builder.not(author), title);
            testName = "Unnormalised OR - NOT in left operand";
            runJQFtoCQLTest(testName, new JaferQueryTestObject(query), "", "");
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("Stylesheet directed termination") == -1)
            {
                fail("Query Exception (" + testName + "): " + e);
            }
        }
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node query = builder.or(title, builder.not(author));
            testName = "Unnormalised OR - NOT in right operand";
            runJQFtoCQLTest(testName, new JaferQueryTestObject(query), "", "");
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("Stylesheet directed termination") == -1)
            {
                fail("Query Exception (" + testName + "): " + e);
            }
        }
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node query = builder.or(builder.not(author), builder.not(title));
            testName = "Unnormalised OR - NOT in both operands";
            runJQFtoCQLTest(testName, new JaferQueryTestObject(query), "", "");
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("Stylesheet directed termination") == -1)
            {
                fail("Query Exception (" + testName + "): " + e);
            }
        }
    }

    /**
     * Tests convertion from XCQL to JQF on a simple search clause
     */
    public void testXCQLtoJQFSimpleSearchClause()
    {
        String testName = "";
        try
        {
            String XCQL = XMLHEADER + "<XCQL><searchClause><index>bath.title</index><relation><value>=</value>"
                    + "</relation><term>Oliver</term></searchClause></XCQL>";
            String expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>Oliver</model></constraintModel>";
            CQLQuery cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Simple Search Clause";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>title</index><relation><value>=</value>"
                    + "</relation><term>Oliver</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>Oliver</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Simple Search Clause with no index";
            runXCQLToJQFTest(testName, cq, expectedJQF);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on search clauses with indexe special
     * case of all records
     */
    public void testXCQLtoJQFCQLAllRecordsInSearchClause()
    {
        String testName = "";
        try
        {
            String XCQL = XMLHEADER + "<XCQL><searchClause><index>cql.allrecords</index><relation><value>=</value>"
                    + "</relation><term>1</term></searchClause></XCQL>";
            String expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1016</semantic><relation>3</relation>"
                    + "</constraint><model>1</model></constraintModel>";
            CQLQuery cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "All Records";
            runXCQLToJQFTest(testName, cq, expectedJQF);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on search clauses with index server
     * choice
     */
    public void testXCQLtoJQFCQLServerChoiceInSearchClause()
    {
        String testName = "";
        try
        {
            String XCQL = XMLHEADER + "<XCQL><searchClause><index>cql.serverChoice</index><relation><value>scr</value>"
                    + "</relation><term>1</term></searchClause></XCQL>";
            String expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1016</semantic><relation>3</relation>"
                    + "</constraint><model>1</model></constraintModel>";
            CQLQuery cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Server Choice";
            runXCQLToJQFTest(testName, cq, expectedJQF);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on search clauses with set of indexes
     */
    public void testXCQLtoJQFIndexesInSearchClause()
    {
        String testName = "";
        try
        {
            String XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            String expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            CQLQuery cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "dc.creator";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.subject</index><relation><value>=</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>21</semantic><relation>3</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "dc.subject";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.description</index><relation><value>=</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>63</semantic><relation>3</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "dc.description";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.publisher</index><relation><value>=</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1018</semantic><relation>3</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "dc.publisher";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.date</index><relation><value>=</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>30</semantic><relation>3</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "dc.date";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.type</index><relation><value>=</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1034</semantic><relation>3</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "dc.type";
            runXCQLToJQFTest(testName, cq, expectedJQF);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on search clauses with relations
     */
    public void testXCQLtoJQFRelationInSearchClause()
    {
        String testName = "";
        try
        {
            String XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            String expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            CQLQuery cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "= Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>1</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "< Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;=</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>2</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "<= Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&gt;=</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>4</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = ">= Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&gt;</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>5</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "> Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;&gt;</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>6</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "<> Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>stem</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>101</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "stem Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>phonetic</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>100</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "phonetic Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>relevant</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>102</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "relevant Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>any</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>2</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "any Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>exact</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>108</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "exact Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>all</value>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>108</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "all Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on search clauses with relations
     * checking priority rules
     */
    public void testXCQLtoJQFRelationPriorityInSearchClause()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {
            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;&gt;</value>"
                    + "<modifiers><modifier><type>relevant</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>102</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "relevent overides <> Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;&gt;</value>"
                    + "<modifiers><modifier><type>stem</type></modifier><modifier><type>relevant</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>101</relation>"
                    + "</constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "stem overides relevant and <> Relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>&lt;&gt;</value>"
                    + "<modifiers><modifier><type>cql.string</type></modifier>"
                    + "<modifier><type>stem</type></modifier><modifier><type>relevant</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>101</relation>"
                    + "<structure>108</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "stem overides relevant and <> Relation with a structure included";
            runXCQLToJQFTest(testName, cq, expectedJQF);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on search clauses with structures
     */
    public void testXCQLtoJQFStructureInSearchClause()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>cql.string</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>108</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Structure of string";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>cql.number</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>107</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Structure of number";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>cql.uri</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>104</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Structure of uri";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>cql.isodate</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>5</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Structure of isodate";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>cql.word</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>2</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Structure of isodate";
            runXCQLToJQFTest(testName, cq, expectedJQF);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on search clauses with structures
     * checking priority rules
     */
    public void testXCQLtoJQFStructurePriorityInSearchClause()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>exact</value>"
                    + "<modifiers><modifier><type>cql.word</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>108</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "exact overides to string";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>all</value>"
                    + "<modifiers><modifier><type>cql.number</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>108</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "all overides to string";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>any</value>"
                    + "<modifiers><modifier><type>cql.uri</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>2</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "all overides to word";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>cql.isodate</type></modifier>"
                    + "<modifier><type>cql.String</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<structure>5</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "string overides isodate";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "<modifiers><modifier><type>stem</type></modifier><modifier><type>cql.number</type></modifier>"
                    + "<modifier><type>cql.String</type></modifier></modifiers>"
                    + "</relation><term>putting</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>101</relation>"
                    + "<structure>107</structure></constraint><model>putting</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Number overides isodate";
            runXCQLToJQFTest(testName, cq, expectedJQF);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on term word anchoring with structures
     * checking priority rules
     */
    public void testXCQLtoJQFTruncationInSearchClause()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>*golf</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<truncation>1</truncation></constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "left Truncation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>golf*</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<truncation>2</truncation></constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "right Truncation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>*golf*</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<truncation>3</truncation></constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "both Truncation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>^golf^</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<truncation>100</truncation></constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "no Truncation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>^golf</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<position>1</position></constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "position in field no Truncation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>^*golf</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<position>1</position><truncation>1</truncation></constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "position in field left Truncation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>^golf*</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<position>1</position><truncation>2</truncation></constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "position in field right Truncation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>^*golf*</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<position>1</position><truncation>3</truncation></constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "position in field both Truncation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>^^golf^</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<position>1</position><truncation>100</truncation></constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "position in field no Truncation";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>^^go*lf^</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<position>1</position><truncation>100</truncation></constraint><model>go*lf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "check middle star ignored";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.creator</index><relation><value>=</value>"
                    + "</relation><term>^^go^lf^</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>3</relation>"
                    + "<position>1</position><truncation>100</truncation></constraint><model>go^lf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "check middle ^ ignored";
            runXCQLToJQFTest(testName, cq, expectedJQF);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF with bad index
     */
    public void testXCQLtoJQFBADIndexInSearchClause()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.ctor</index><relation><value>=</value>"
                    + "</relation><term>*golf</term></searchClause></XCQL>";
            expectedJQF = "";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "bad index";
            runXCQLToJQFTest(testName, cq, expectedJQF);
            fail("(" + testName + ") should have failed with bad index");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF with bad relation
     */
    public void testXCQLtoJQFBADRelationInSearchClause()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {

            XCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index><relation><value>%</value>"
                    + "</relation><term>*golf</term></searchClause></XCQL>";
            expectedJQF = "";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "bad relation";
            runXCQLToJQFTest(testName, cq, expectedJQF);
            fail("(" + testName + ") should have failed with bad relation");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF with prox operator
     */
    public void testXCQLtoJQFProxOperator()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {

            XCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>prox</value></boolean>"
                    + "<leftOperand><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></rightOperand></triple></XCQL>";
            expectedJQF = "";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "prox operator";
            runXCQLToJQFTest(testName, cq, expectedJQF);
            fail("(" + testName + ") should have failed due to prox operator");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF with bad operator operator
     */
    public void testXCQLtoJQFBADOpertorInTriple()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {

            XCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>ad</value></boolean>"
                    + "<leftOperand><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></rightOperand></triple></XCQL>";
            expectedJQF = "";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "bad operator";
            runXCQLToJQFTest(testName, cq, expectedJQF);
            fail("(" + testName + ") should have failed due to bad operator");
        }
        catch (QueryException exc)
        {
            boolean success = exc.getCause().getMessage().indexOf("Stylesheet directed termination") != -1;
            assertTrue(success);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF ignores prefixes
     */
    public void testXCQLtoJQFPrefixesIgnored()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {
            XCQL = XMLHEADER
                    + "<XCQL><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></XCQL>";
            expectedJQF = XMLHEADER + "<constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>golf</model></constraintModel>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "ignore search prefixes";
            runXCQLToJQFTest(testName, cq, expectedJQF);

            XCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>and</value></boolean>"
                    + "<leftOperand><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><prefixes><prefix><name>bath</name><identifier>http://zing.z3950.org/cql/bath/2.0/</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></rightOperand></triple></XCQL>";
            expectedJQF = XMLHEADER + "<and><constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>golf</model></constraintModel>"
                    + "<constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>golf</model></constraintModel></and>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "ignore operand prefixes";
            runXCQLToJQFTest(testName, cq, expectedJQF);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on OR Triple
     */
    public void testXCQLtoJQFORTriple()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {
            XCQL = XMLHEADER + "<XCQL><triple><boolean><value>or</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></rightOperand></triple></XCQL>";
            expectedJQF = XMLHEADER + "<or><constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>golf</model></constraintModel>"
                    + "<constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>golf</model></constraintModel></or>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "OR Triple";
            runXCQLToJQFTest(testName, cq, expectedJQF);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on AND Triple
     */
    public void testXCQLtoJQFANDTriple()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {
            XCQL = XMLHEADER + "<XCQL><triple><boolean><value>and</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></rightOperand></triple></XCQL>";
            expectedJQF = XMLHEADER + "<and><constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>golf</model></constraintModel>"
                    + "<constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>golf</model></constraintModel></and>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "AND Triple";
            runXCQLToJQFTest(testName, cq, expectedJQF);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on NOT Triple
     */
    public void testXCQLtoJQFNotTriple()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {
            XCQL = XMLHEADER + "<XCQL><triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></rightOperand></triple></XCQL>";
            expectedJQF = XMLHEADER + "<and><constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>golf</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>4</semantic><relation>3</relation>"
                    + "</constraint><model>golf</model></constraintModel></not></and>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "Not Triple";
            runXCQLToJQFTest(testName, cq, expectedJQF);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from XCQL to JQF on NOT Triple
     */
    public void testXCQLtoJQFComplexTriple()
    {
        String XCQL, expectedJQF, testName = "";
        CQLQuery cq = null;
        try
        {
            XCQL = XMLHEADER + "<XCQL><triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><triple><boolean><value>or</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>putting</term></searchClause></rightOperand></triple></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value>"
                    + "</relation><term>chipping</term></searchClause></rightOperand></triple></XCQL>";
            expectedJQF = XMLHEADER
                    + "<and><or><constraintModel><constraint><semantic>4</semantic><relation>3</relation></constraint><model>golf</model></constraintModel>"
                    + "<constraintModel><constraint><semantic>4</semantic><relation>3</relation></constraint><model>putting</model></constraintModel>"
                    + "</or><not><constraintModel><constraint><semantic>4</semantic><relation>3</relation></constraint><model>chipping</model></constraintModel></not></and>";
            cq = new CQLQuery(DOMFactory.parse(XCQL));
            testName = "complex Triple";
            runXCQLToJQFTest(testName, cq, expectedJQF);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException: (" + testName + ") " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from CQL to XCQL on a simple search query
     */
    public void testCQLToXCQLSimpleQuery()
    {
        String cql, expectedXCQL, testName = "";
        try
        {
            cql = "dc.title = \"golf guide\"";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index><relation><value>=</value></relation>"
                    + "<term>golf guide</term></searchClause></XCQL>";
            testName = "Simple Query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from CQL to XCQL on a term only query
     */
    public void testCQLToXCQLTermOnlyQuery()
    {
        String cql, expectedXCQL, testName = "";
        try
        {
            cql = "golf";
            expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>cql.serverChoice</index><relation><value>scr</value></relation>"
                    + "<term>golf</term></searchClause></XCQL>";
            testName = "term only query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from CQL to XCQL on relations in query
     */
    public void testCQLToXCQLRelationQueries()
    {
        String cql, expectedXCQL, testName = "";
        try
        {
            cql = "dc.title = golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>=</value></relation><term>golf</term></searchClause></XCQL>";
            testName = "= relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title <> golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>&lt;&gt;</value></relation><term>golf</term></searchClause></XCQL>";
            testName = "<> relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title > golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>&gt;</value></relation><term>golf</term></searchClause></XCQL>";
            testName = "> relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title >= golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>&gt;=</value></relation><term>golf</term></searchClause></XCQL>";
            testName = "= relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title < golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>&lt;</value></relation><term>golf</term></searchClause></XCQL>";
            testName = "= relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title <= golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>&lt;=</value></relation><term>golf</term></searchClause></XCQL>";
            testName = "= relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title <=/stem golf";
            expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>&lt;=</value><modifiers><modifier><type>stem</type></modifier></modifiers></relation><term>golf</term></searchClause></XCQL>";
            testName = "stem relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title <=/phonetic golf";
            expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>&lt;=</value><modifiers><modifier><type>phonetic</type></modifier></modifiers></relation><term>golf</term></searchClause></XCQL>";
            testName = "phonetic relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title <=/relevant golf";
            expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>&lt;=</value><modifiers><modifier><type>relevant</type></modifier></modifiers></relation><term>golf</term></searchClause></XCQL>";
            testName = "relevant relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title all golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>all</value></relation><term>golf</term></searchClause></XCQL>";
            testName = " all  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title any golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>any</value></relation><term>golf</term></searchClause></XCQL>";
            testName = "any  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title exact golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>exact</value></relation><term>golf</term></searchClause></XCQL>";
            testName = " exact  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title exact/stem golf";
            expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>exact</value><modifiers><modifier><type>stem</type></modifier></modifiers></relation><term>golf</term></searchClause></XCQL>";
            testName = "excat and stem relations  query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from CQL to XCQL on structure in query
     */
    public void testCQLToXCQLStructureQuery()
    {
        String cql, expectedXCQL, testName = "";
        try
        {
            cql = "dc.title any/stem/cql.word golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>any</value><modifiers><modifier><type>stem</type></modifier>"
                    + "<modifier><type>cql.word</type></modifier></modifiers></relation><term>golf</term></searchClause></XCQL>";
            testName = "word structure query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title any/stem/cql.url/cql.String golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>any</value><modifiers><modifier><type>stem</type></modifier>"
                    + "<modifier><type>cql.url</type></modifier><modifier><type>cql.string</type></modifier></modifiers>"
                    + "</relation><term>golf</term></searchClause></XCQL>";
            testName = "two structure query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from CQL to XCQL on truncation in query
     */
    public void testCQLToXCQLTruncationQuery()
    {
        String cql, expectedXCQL, testName = "";
        try
        {
            cql = "dc.title any/stem/cql.word golf*";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>any</value><modifiers><modifier><type>stem</type></modifier>"
                    + "<modifier><type>cql.word</type></modifier></modifiers></relation><term>golf*</term></searchClause></XCQL>";
            testName = "right trunc query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title any/stem/cql.word *golf";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>any</value><modifiers><modifier><type>stem</type></modifier>"
                    + "<modifier><type>cql.word</type></modifier></modifiers></relation><term>*golf</term></searchClause></XCQL>";
            testName = "left trunc query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title any/stem/cql.word *golf*";
            expectedXCQL = XMLHEADER
                    + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>any</value><modifiers><modifier><type>stem</type></modifier>"
                    + "<modifier><type>cql.word</type></modifier></modifiers></relation><term>*golf*</term></searchClause></XCQL>";
            testName = "both trunc query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title any/stem/cql.url/cql.String ^golf^";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>any</value><modifiers><modifier><type>stem</type></modifier>"
                    + "<modifier><type>cql.url</type></modifier><modifier><type>cql.string</type></modifier>"
                    + "</modifiers></relation><term>^golf^</term></searchClause></XCQL>";
            testName = "no trunc query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title any/stem/cql.url/cql.String ^^golf^";
            expectedXCQL = XMLHEADER + "<XCQL><searchClause><index>dc.title</index>"
                    + "<relation><value>any</value><modifiers><modifier><type>stem</type></modifier>"
                    + "<modifier><type>cql.url</type></modifier><modifier><type>cql.string</type></modifier>"
                    + "</modifiers></relation><term>^^golf^</term></searchClause></XCQL>";
            testName = "no trnc first in field query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from CQL to XCQL on structure in query
     */
    public void testCQLToXCQLPrefixQuery()
    {
        String cql, expectedXCQL, testName = "";
        try
        {
            cql = "> dc=\"http://zing.z3950.org/cql/dc/2.0\" putting";
            expectedXCQL = XMLHEADER
                    + "<XCQL><prefixes><prefix><name>dc</name><identifier>http://zing.z3950.org/cql/dc/2.0</identifier></prefix>"
                    + "</prefixes><searchClause><index>cql.serverChoice</index><relation><value>scr</value></relation>"
                    + "<term>putting</term></searchClause></XCQL>";
            testName = "prefix term only query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "> dc=\"http://zing.z3950.org/cql/dc/2.0\"(>bath=\"http://zing.z3950.org/cql/bath/2.0\" dc.title =/stem faldo) and putting";
            expectedXCQL = XMLHEADER
                    + "<XCQL><prefixes><prefix><name>dc</name><identifier>http://zing.z3950.org/cql/dc/2.0</identifier></prefix>"
                    + "</prefixes><triple><boolean><value>and</value></boolean>"
                    + "<leftOperand><prefixes><prefix><name>bath</name>"
                    + "<identifier>http://zing.z3950.org/cql/bath/2.0</identifier></prefix></prefixes>"
                    + "<searchClause><index>dc.title</index><relation><value>=</value><modifiers><modifier><type>stem</type>"
                    + "</modifier></modifiers></relation><term>faldo</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>cql.serverChoice</index><relation><value>scr</value></relation>"
                    + "<term>putting</term></searchClause></rightOperand></triple></XCQL>";
            testName = "prefix in term and operand only query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from CQL to XCQL on triple in query
     */
    public void testCQLToXCQLTriplesQuery()
    {
        String cql, expectedXCQL, testName = "";
        try
        {
            cql = "dc.title = golf and dc.author=woods";
            expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>and</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.author</index><relation><value>=</value></relation><term>woods</term></searchClause></rightOperand>"
                    + "</triple></XCQL>";
            testName = "simple and triple";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title = golf or dc.author=woods";
            expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>or</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.author</index><relation><value>=</value></relation><term>woods</term></searchClause></rightOperand>"
                    + "</triple></XCQL>";
            testName = "simple or triple";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title = golf not dc.author=woods";
            expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>not</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.author</index><relation><value>=</value></relation><term>woods</term></searchClause></rightOperand>"
                    + "</triple></XCQL>";
            testName = "simple not triple";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title = golf prox dc.author=woods";
            expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>prox</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.author</index><relation><value>=</value></relation><term>woods</term></searchClause></rightOperand>"
                    + "</triple></XCQL>";
            testName = "simple prox triple";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title = golf or dc.title = chipping and dc.author=woods";
            expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>and</value></boolean>"
                    + "<leftOperand><triple><boolean><value>or</value></boolean>"
                    + "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>golf</term></searchClause></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>chipping</term></searchClause>"
                    + "</rightOperand></triple></leftOperand>"
                    + "<rightOperand><searchClause><index>dc.author</index><relation><value>=</value></relation><term>woods</term></searchClause></rightOperand></triple></XCQL>";
            testName = "nested or / and no brackets structure query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);

            cql = "dc.title = golf or (dc.title = chipping and dc.author=woods)";
            expectedXCQL = XMLHEADER
                    + "<XCQL><triple><boolean><value>or</value></boolean>" +
                            "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>golf</term></searchClause></leftOperand>" +
                            "<rightOperand><triple><boolean><value>and</value></boolean>" +
                            "<leftOperand><searchClause><index>dc.title</index><relation><value>=</value></relation><term>chipping</term></searchClause></leftOperand>" +
                            "<rightOperand><searchClause><index>dc.author</index><relation><value>=</value></relation><term>woods</term></searchClause></rightOperand></triple></rightOperand>" +
                            "</triple></XCQL>";
            testName = "nested or and with brackets query";
            runCQLToXCQLTest(testName, cql, expectedXCQL);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }
    }
