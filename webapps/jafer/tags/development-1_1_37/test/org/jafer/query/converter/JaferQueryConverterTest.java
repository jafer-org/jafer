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

import org.jafer.exception.JaferException;
import org.jafer.interfaces.QueryBuilder;
import org.jafer.query.JaferQuery;
import org.jafer.query.QueryException;
import org.w3c.dom.Node;

import junit.framework.TestCase;

/**
 * This test class tests the JAFERQueryConverter to make sure that it applies
 * demorgans laws correctly
 */
public class JaferQueryConverterTest extends TestCase
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
     * Checks the normailisation is correct
     * 
     * @param testName the name of the test being run
     * @param inputJQNode The starting jaffer query before normailsation as a
     *        node
     * @param expectedJQ The expected normailsed jafer query XML
     */
    private void checkNormalisation(String testName, Node inputJQNode, String expectedJQ)
    {
        try
        {
            // creating the jafer query automatically runs normailisation
            JaferQuery jq = new JaferQuery(inputJQNode,true);
            // make sure the XML matches as expected
            assertEquals("(" + testName + ") Input and returned output  do not match", expectedJQ, jq.getXML());
        }
        catch (JaferException e)
        {
            e.printStackTrace();
            fail("Jafer Exception: (" + testName + ") " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that only
     * contain constraint models
     */
    public void testNormailiseOfCMOnlyQueries()
    {
        String testName, expectedJQ;
        try
        {
            // build test query
            testName = "constraint only query";
            expectedJQ = XMLHEADER
                    + "<constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>";
            Node author = builder.getNode("author", "Shakespear");
            checkNormalisation(testName, author, expectedJQ);
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that only
     * contain constraint models in ANDs
     */
    public void testNormailiseOfCMOnlyANDQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node query = null;

            // build test query
            testName = "constraint only AND query";
            expectedJQ = XMLHEADER
                    + "<and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></and>";
            query = builder.and(author, author2);
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only nested AND query";
            expectedJQ = XMLHEADER
                    + "<and><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></and>";
            query = builder.and(builder.and(author, author2), author);
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that only
     * contain constraint models in ORs
     */
    public void testNormailiseOfCMOnlyORQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node query = null;

            // build test query
            testName = "constraint only OR query";
            expectedJQ = XMLHEADER
                    + "<or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></or>";
            query = builder.or(author, author2);
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only nested OR query";
            expectedJQ = XMLHEADER
                    + "<or><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></or>";
            query = builder.or(builder.or(author, author2), author);
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that only
     * contain constraint models in ORs or ANDs
     */
    public void testNormailiseOfCMOnlyANDORQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node title = builder.getNode("title", "oliver");
            Node query = null;

            // build test query
            testName = "constraint only ANDOR query";
            expectedJQ = XMLHEADER
                    + "<or><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></or>";
            query = builder.or(builder.and(author, title), author2);
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only nested ANDOR query";
            expectedJQ = XMLHEADER
                    + "<and><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></and>";
            query = builder.and(builder.or(author, title), author);
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only nested ANDOR query";
            expectedJQ = XMLHEADER
                    + "<and><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></or><and><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></and></and>";
            query = builder.and(builder.or(author, title), builder.and(title, author));
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that only
     * contain constraint models in ORs
     */
    public void testNormailiseOfCMOnlyNOTQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node query = null;

            // build test query
            testName = "constraint only NOT query";
            expectedJQ = XMLHEADER
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not>";
            query = builder.not(author);
            checkNormalisation(testName, query, expectedJQ);

            testName = "constraint only NOT query with or";
            expectedJQ = XMLHEADER
                    + "<not><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></or></not>";
            query = builder.not(builder.or(author, author2));
            checkNormalisation(testName, query, expectedJQ);
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that only
     * contain constraint models in ANDs and Single NOTs so demorgan should not
     * be applied
     */
    public void testNormailiseOfCMOnlyANDwithSingleNotsQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node query = null;

            // build test query
            testName = "constraint only AND query with left condition a not";
            expectedJQ = XMLHEADER
                    + "<and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not></and>";
            query = builder.and(builder.not(author), author2);
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only AND query with right condition a not";
            expectedJQ = XMLHEADER
                    + "<and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not></and>";
            query = builder.and(author, builder.not(author2));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only nested AND query with left condition a not";
            expectedJQ = XMLHEADER
                    + "<and><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not>"
                    + "</and><not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not></and>";
            query = builder.and(builder.and(builder.not(author), author2), builder.not(author));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only nested AND query with right condition a not";
            expectedJQ = XMLHEADER
                    + "<and><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not></and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></and>";
            query = builder.and(builder.and(author, builder.not(author2)), author);
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that only
     * contain constraint models in ANDs and Single NOTs so demorgan should not
     * be applied
     */
    public void testNormailiseOfCMOnlyANDORwithSingleNotsQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node query = null;

            // build test query
            testName = "constraint only nested AND query with left condition a not";
            expectedJQ = XMLHEADER
                    + "<not><or><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not>"
                    + "</and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "</or></not>";
            query = builder.and(builder.or(builder.not(author), author2), builder.not(author));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only nested AND query with right condition a not";
            expectedJQ = XMLHEADER
                    + "<or><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not></and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></or>";
            query = builder.or(builder.and(author, builder.not(author2)), author);
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that only
     * contain constraint models in ORs and Single NOTs so demorgan should not
     * be applied
     */
    public void testNormailiseOfCMOnlyORwithSingleNotsQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node query = null;

            // build test query
            testName = "constraint only OR query with left condition a not";
            expectedJQ = XMLHEADER
                    + "<not><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not></and></not>";
            query = builder.or(builder.not(author), author2);
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only OR query with right condition a not";
            expectedJQ = XMLHEADER
                    + "<not><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not></and></not>";
            query = builder.or(author, builder.not(author2));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only nested OR query with left condition a not";
            expectedJQ = XMLHEADER
                    + "<not><and><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not>"
                    + "</and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></and></not>";
            query = builder.or(builder.or(builder.not(author), author2), builder.not(author));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "constraint only nested OR query with right condition a not";
            expectedJQ = XMLHEADER
                    + "<not><and><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not>"
                    + "</and><not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not></and></not>";
            query = builder.or(builder.or(author, builder.not(author2)), author);
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that contain
     * constraint models in double negative NOTs: (NOT(NOT(q))
     */
    public void testNormailiseOfDoubleNegativeNOTQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node title = builder.getNode("title", "oliver");
            Node query = null;

            // build test query
            testName = "Double Negative NOT query";
            expectedJQ = XMLHEADER
                    + "<constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>";
            query = builder.not(builder.not(author));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double Negative NOT query with or";
            expectedJQ = XMLHEADER
                    + "<not><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></or></not>";
            query = builder.not(builder.or(builder.not(builder.not(author)), author2));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double Negative NOT query with and";
            expectedJQ = XMLHEADER
                    + "<not><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></and></not>";
            query = builder.not(builder.and(builder.not(builder.not(author)), author2));
            checkNormalisation(testName, query, expectedJQ);

            // build test query

            testName = "Double Negative NOT query with and / or";
            expectedJQ = XMLHEADER
                    + "<and><and><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not>"
                    + "</and><not><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "<constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel>"
                    + "</and></not></and>";
            query = builder.not(builder.or(builder.and(builder.not(builder.not(author)), title),
            // intresting note below here the double not should cancel out and
                    // demorgan law should no longer apply
                    builder.or(builder.not(builder.not(author2)), builder.not(title))));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double Negative NOT query in or v1";
            expectedJQ = XMLHEADER
                    + "<and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not></and>";
            query = builder.not(builder.or(builder.not(author), author2));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double Negative NOT query in or v2";
            expectedJQ = XMLHEADER
                    + "<and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not></and>";
            query = builder.not(builder.or(author, builder.not(author2)));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double Negative NOT query in and v1";
            expectedJQ = XMLHEADER
                    + "<not><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not></and></not>";
            query = builder.not(builder.and(builder.not(author), author2));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double Negative NOT query in and v2";
            expectedJQ = XMLHEADER
                    + "<not><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not></and></not>";

            query = builder.not(builder.and(author, builder.not(author2)));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double Negative NOT query after and with two nots";
            expectedJQ = XMLHEADER
                    + "<or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "<constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></or>";
            query = builder.not(builder.and(builder.not(author), builder.not(author2)));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double Negative NOT query in or nested";
            expectedJQ = XMLHEADER
                    + "<not><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "<and><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel>"
                    + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not></and>"
                    + "</or></not>";
            query = builder.not(builder.or(author, builder.not(builder.or(author2, builder.not(title)))));
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that contain
     * double nots that should be normailised
     */
    public void testNormailiseOfANDDoubleNotQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node title = builder.getNode("title", "oliver");
            Node query = null;

            // build test query
            testName = "Double not in AND query";
            expectedJQ = XMLHEADER
                    + "<not><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></or></not>";
            query = builder.and(builder.not(author), builder.not(title));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double not nested AND query";
            expectedJQ = XMLHEADER
                    + "<and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel>"
                    + "<not><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></or></not></and>";
            query = builder.and(builder.and(builder.not(author), builder.not(title)), author2);
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double not nested AND query with ripple";
            expectedJQ = XMLHEADER
                    + "<not><or><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></or></not>";
            query = builder.and(builder.and(builder.not(author), builder.not(title)), builder.not(author2));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double not nested AND query with ripple inverse";
            expectedJQ = XMLHEADER
                    + "<not><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></or></or></not>";
            query = builder.and(builder.not(author2), builder.and(builder.not(author), builder.not(title)));
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that contain
     * double nots that should be normailised
     */
    public void testNormailiseOfORDoubleNotQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node title = builder.getNode("title", "oliver");
            Node query = null;

            // build test query
            testName = "Double not in OR query";
            expectedJQ = XMLHEADER
                    + "<not><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></and></not>";
            query = builder.or(builder.not(author), builder.not(title));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double not nested OR query";
            expectedJQ = XMLHEADER
                    + "<not><and><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>"
                    + "<constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel>"
                    + "</and><not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></not>"
                    + "</and></not>";
            query = builder.or(builder.or(builder.not(author), builder.not(title)), author2);
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double not nested OR query with ripple";
            expectedJQ = XMLHEADER
                    + "<not><and><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel></and></not>";
            query = builder.or(builder.or(builder.not(author), builder.not(title)), builder.not(author2));
            checkNormalisation(testName, query, expectedJQ);

            // build test query
            testName = "Double not nested OR query with ripple inverse";
            expectedJQ = XMLHEADER
                    + "<not><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></and></and></not>";
            query = builder.or(builder.not(author2), builder.or(builder.not(author), builder.not(title)));
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }

    /**
     * Tests the normailise routines functionality on a queries that contain
     * double nots that should be normailised
     */
    public void testNormailiseOfANDORDoubleNotQueries()
    {
        String testName, expectedJQ;
        try
        {
            // nodes used for creating queries
            Node author = builder.getNode("author", "Shakespear");
            Node author2 = builder.getNode("author", "Foster");
            Node title = builder.getNode("title", "oliver");
            Node query = null;

            // build test query
            testName = "Double not nested OR query with ripple inverse";
            expectedJQ = XMLHEADER
                    + "<not><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Foster</model></constraintModel><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>oliver</model></constraintModel></or></and></not>";
            query = builder.or(builder.not(author2), builder.and(builder.not(author), builder.not(title)));
            checkNormalisation(testName, query, expectedJQ);

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }
}