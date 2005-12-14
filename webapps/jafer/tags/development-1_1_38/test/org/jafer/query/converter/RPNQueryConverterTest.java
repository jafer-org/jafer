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

import org.jafer.interfaces.QueryBuilder;
import org.jafer.query.JaferQuery;
import org.jafer.query.JaferQueryTestObject;
import org.jafer.query.QueryException;
import org.w3c.dom.Node;

/**
 * This test class tests the RPNQueryConverter to make sure that it converts
 * queries correctly
 */
public class RPNQueryConverterTest extends TestCase
{

    /**
     * Stores a reference to the query builder for building test data
     */
    private QueryBuilder builder = new org.jafer.query.QueryBuilder();

    /**
     * Runs a simple test that converts from jafer to RPN and back again and
     * makes sure they still match
     * 
     * @param testName the name of the test being run
     * @param jq The starting JPN query
     */
    private void runConversion(String testName, JaferQuery jq) throws QueryException
    {
        // convert to RPN
        z3950.v3.RPNQuery rpnq = RPNQueryConverter.convertJaferToRPN(jq);
        // convert back to Jafer Query
        JaferQuery jqResult = RPNQueryConverter.convertRPNToJafer(rpnq);

        // make sure the XML matches
        assertEquals("(" + testName + ") Input and returned output reconverted do not match", jq.getXML(), jqResult.getXML());
    }

    /**
     * Tests convertion from Jafer to RPN and back again on simple common use
     * attribute queries. Normalisation is applied during JaferQuery
     * Construction
     */
    public void testSimpleUseAttributeQueries()
    {
        String testName = "";
        try
        {
            // build test queries
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "oliver");
            Node or = builder.or(author, title);
            Node and = builder.and(author, title);

            testName = "Author only";
            runConversion(testName, new JaferQuery(author));
            testName = "title only";
            runConversion(testName, new JaferQuery(title));
            testName = "Author or Title";
            runConversion(testName, new JaferQuery(or));
            testName = "Author and Title";
            runConversion(testName, new JaferQuery(and));
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on less common use
     * attribute queries. Normalisation is applied during JaferQuery
     * Construction
     */
    public void testSimpleUnCommonUseAttributeQueries()
    {
        String testName = "";
        try
        {
            // build test queries
            Node corporate_name = builder.getNode("corporate_name", "corporate_name");
            Node lc_subject_heading = builder.getNode("lc_subject_heading", "lc_subject_heading");
            Node author_name_personal = builder.getNode("author_name_personal", "author_name_personal");
            Node stock_number = builder.getNode("stock_number", "stock_number");
            Node anywhere = builder.getNode("anywhere", "anywhere");
            Node no_natl_biblio = builder.getNode("no_natl_biblio", "no_natl_biblio");

            testName = "corporate_name only";
            runConversion(testName, new JaferQuery(corporate_name));
            testName = "lc_subject_heading only";
            runConversion(testName, new JaferQuery(lc_subject_heading));
            testName = "author_name_personal only";
            runConversion(testName, new JaferQuery(author_name_personal));
            testName = "stock_number only";
            runConversion(testName, new JaferQuery(stock_number));
            testName = "anywhere only";
            runConversion(testName, new JaferQuery(anywhere));
            testName = "no_natl_biblio only";
            runConversion(testName, new JaferQuery(no_natl_biblio));

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on complex logic
     * queries. Normalisation is applied during JaferQuery Construction
     */
    public void testComplexLogicUseAttributeQueries()
    {
        String testName = "";
        try
        {
            // build test queries
            Node author = builder.getNode("author", "author");
            Node title = builder.getNode("title", "title");
            Node corporate_name = builder.getNode("corporate_name", "corporate_name");
            Node lc_subject_heading = builder.getNode("lc_subject_heading", "lc_subject_heading");
            Node author_name_personal = builder.getNode("author_name_personal", "author_name_personal");
            Node stock_number = builder.getNode("stock_number", "stock_number");
            Node anywhere = builder.getNode("anywhere", "anywhere");
            Node no_natl_biblio = builder.getNode("no_natl_biblio", "no_natl_biblio");

            Node complex1 = builder.or(builder.and(builder.or(corporate_name, lc_subject_heading), title), no_natl_biblio);
            Node complex2 = builder.and(builder.and(builder.or(builder.not(title), author), lc_subject_heading), builder
                    .not(stock_number));
            Node complex3 = builder.and(builder.and(author_name_personal, stock_number), builder.or(anywhere, no_natl_biblio));

            testName = "complex boolean 1";
            runConversion(testName, new JaferQuery(complex1, true));
            testName = "complex boolean 2";
            runConversion(testName, new JaferQuery(complex2, true));
            testName = "complex boolean 3";
            runConversion(testName, new JaferQuery(complex3, true));

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception: (" + testName + ") " + exc);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on relation queries.
     * Normalisation is applied during JaferQuery Construction
     */
    public void testRelationshipQueries()
    {

        Node query = null;
        String testName = "";
        try
        {
            // loop round the six types
            for (int i = 1; i <= 103; i++)
            {
                testName = "relation " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", Integer.toString(i) });
                runConversion(testName, new JaferQuery(query, true));
                // if we are at 6 jump up to 99 so move and run 100 to 103
                if (i == 6)
                {
                    i = 99;
                }
            }
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on position queries.
     * Normalisation is applied during JaferQuery Construction
     */
    public void testPositionQueries()
    {

        Node query = null;
        String testName = "";
        try
        {
            // loop round the six types
            for (int i = 1; i <= 3; i++)
            {
                testName = "position " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", "0", Integer.toString(i) });
                runConversion(testName, new JaferQuery(query, true));
            }

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on structure queries.
     * Normalisation is applied during JaferQuery Construction
     */
    public void testStructureQueries()
    {

        Node query = null;
        String testName = "";
        try
        {
            // loop round the six types
            for (int i = 1; i <= 109; i++)
            {
                testName = "structure string[] " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", Integer.toString(i) });
                runConversion(testName, new JaferQuery(query, true));
                // if we are at 6 jump up to 99 so move and run 100 to 109
                if (i == 6)
                {
                    i = 99;
                }
            }
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on truncation queries.
     * Normalisation is applied during JaferQuery Construction
     */
    public void testTruncationQueries()
    {

        Node query = null;
        String testName = "";
        try
        {
            // loop round the six types
            for (int i = 1; i <= 103; i++)
            {
                testName = "truncation " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "0", Integer.toString(i) });
                runConversion(testName, new JaferQuery(query, true));

                // if we are at 6 jump up to 99 so move and run 100 to 109
                if (i == 3)
                {
                    i = 99;
                }
            }

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on completeness
     * queries. Normalisation is applied during JaferQuery Construction
     */
    public void testCompletnessQueries()
    {
        Node query = null;
        String testName = "";
        try
        {
            // loop round the six types
            for (int i = 1; i <= 3; i++)
            {
                testName = "completeness " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "0", "0", Integer.toString(i) });
                runConversion(testName, new JaferQuery(query, true));

            }
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on mixed constraint
     * queries. Normalisation is applied during JaferQuery Construction
     */
    public void testMixedConstraintQueries()
    {
        Node query = null;
        String testName = "";
        try
        {
            testName = "Mixed 1";
            query = builder.getNode(new String[] { "Shakespear", "1003", "100", "4", "1", "2" });
            runConversion(testName, new JaferQuery(query, true));

            testName = "Mixed 2";
            query = builder.getNode(new String[] { "Shakespear", "1001", "100", "0", "1", "2", "3" });
            runConversion(testName, new JaferQuery(query, true));
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on OR constraint
     * queries. Normalisation is applied during JaferQuery Construction
     */
    public void testORQueries()
    {

        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node isbn = builder.getNode("isbn", "34234");

            testName = "Simple Or";
            Node simpleOr = builder.or(author, title);
            runConversion(testName, new JaferQuery(simpleOr, true));

            testName = "Multi Or";
            Node multiOr = builder.or(simpleOr, isbn);
            runConversion(testName, new JaferQuery(multiOr, true));
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on AND constraint
     * queries. Normalisation is applied during JaferQuery Construction
     */
    public void testANDQueries()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node isbn = builder.getNode("isbn", "34234");

            testName = "Simple And";
            Node simpleAnd = builder.and(author, title);
            runConversion(testName, new JaferQuery(simpleAnd, true));
            testName = "Multi And";
            Node multiAnd = builder.and(simpleAnd, isbn);
            runConversion(testName, new JaferQuery(multiAnd, true));
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on unnormalised AND
     * queries. Uses a JaferQueryTestObject to allow unnormalised data.
     */
    public void testUnNormalisedANDQueries()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node query = builder.and(builder.not(author), title);
            testName = "Unnormalised AND - NOT in left operand";
            runConversion(testName, new JaferQueryTestObject(query));
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("not been normailised") == -1)
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

            runConversion(testName, new JaferQueryTestObject(query));
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("not been normailised") == -1)
            {
                fail("Query Exception (" + testName + "): " + e);
            }
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on unnormalised OR
     * queries. Uses a JaferQueryTestObject to allow unnormalised data.
     */
    public void testUnNormalisedORQueries()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node query = builder.or(builder.not(author), title);
            testName = "Unnormalised OR - NOT in left operand";
            runConversion(testName, new JaferQueryTestObject(query));
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("not been normailised") == -1)
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
            runConversion(testName, new JaferQueryTestObject(query));
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("not been normailised") == -1)
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
            runConversion(testName, new JaferQueryTestObject(query));
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("not been normailised") == -1)
            {
                fail("Query Exception (" + testName + "): " + e);
            }
        }
    }

    /**
     * Tests convertion from Jafer to RPN and back again on top level NOT
     * queries. Uses a JaferQueryTestObject to allow unnormalised data.
     */
    public void testNotQueries()
    {
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node query = builder.not(author);
            testName = "TOP LEVEL NOT";
            runConversion(testName, new JaferQueryTestObject(query));
            fail("(" + testName + "):Should have thrown exception for unnormalised data");
        }
        catch (QueryException e)
        {
            // success if message matches
            if (e.getMessage().indexOf("not support top level NOT nodes") == -1)
            {
                fail("Query Exception (" + testName + "): " + e);
            }
        }
    }
}
