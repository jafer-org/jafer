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

package org.jafer.query;

import junit.framework.TestCase;

import org.jafer.interfaces.QueryBuilder;
import org.w3c.dom.Node;

/**
 * this test class tests the query builder methods
 */

public class QueryBuilderTest extends TestCase
{

    /**
     * Stores a reference to the query builder for building test data
     */
    private QueryBuilder builder = new org.jafer.query.QueryBuilder();

    /**
     * Stores a reference to the XMLHEADER text
     */
    private static final String XMLHEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";

    /**
     * Tests the various get node functionality
     * <UL>
     * <li>Build the query Author=Shakespear using getNode(String,String)
     * <li>Build the query Author=Shakespear using getNode(int,String)
     * <li>Build the query Author=Shakespear using getNode(String[])
     * <li>Build the query Author=Shakespear using getNode(int[],String)
     * <li>Build the query Author=Shakespear using getNode(int[][],String)
     * </UL>
     */
    public void testSimpleQueries()
    {
        String expectedXML = XMLHEADER
                + "<constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel>";
        String testName = "";
        try
        {
            // build the same queries using all the various methods
            testName = "getNode(String,String)";
            Node author1 = builder.getNode("author", "Shakespear");
            assertEquals("getNode(String,String) XML do not match", expectedXML, new JaferQuery(author1).getXML());

            testName = "getNode(int,String)";
            Node author2 = builder.getNode(1003, "Shakespear");
            assertEquals("getNode(int,String) XML do not match", expectedXML, new JaferQuery(author2).getXML());

            testName = "getNode(String[]) v1 ";
            Node author3 = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "0", "0", "0" });
            assertEquals("getNode(String[]) v1 XML do not match", expectedXML, new JaferQuery(author3).getXML());

            testName = "getNode(String[]) v2 ";
            Node author4 = builder.getNode(new String[] { "Shakespear", "1003" });
            assertEquals("getNode(String[]) v2 XML do not match", expectedXML, new JaferQuery(author4).getXML());

            testName = "getNode(int[],String) v1 ";
            Node author5 = builder.getNode(new int[] { 1003 }, "Shakespear");
            assertEquals("getNode(int[],String) v1 XML do not match", expectedXML, new JaferQuery(author5).getXML());

            testName = "getNode(int[],String) v2 ";
            Node author6 = builder.getNode(new int[] { 1003, 0, 0, 0, 0, 0 }, "Shakespear");
            assertEquals("getNode(int[],String) v2 XML do not match", expectedXML, new JaferQuery(author6).getXML());

            testName = "getNode(int[][],String) v1 ";
            Node author7 = builder.getNode(new int[][] { { 1, 1003 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 5, 0 }, { 6, 0 } },
                    "Shakespear");
            assertEquals("getNode(int[][],String) v2 XML do not match", expectedXML, new JaferQuery(author7).getXML());

            testName = "getNode(int[][],String) v2 ";
            Node author8 = builder.getNode(new int[][] { { 1, 1003 }, { 3, 0 }, { 5, 0 } }, "Shakespear");
            assertEquals("getNode(int[][],String) v2 XML do not match", expectedXML, new JaferQuery(author8).getXML());
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * This test builds more complex relation ship queries
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
                String expectedXML = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><relation>" + i
                        + "</relation></constraint><model>Shakespear</model></constraintModel>";
                testName = "relation string[] " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", Integer.toString(i) });
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "relation int[] " + Integer.toString(i);
                query = builder.getNode(new int[] { 1003, i }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "relation int[][] " + Integer.toString(i);
                query = builder.getNode(new int[][] { { 1, 1003 }, { 2, i } }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());

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
     * This test builds more complex position ship queries
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
                String expectedXML = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><position>" + i
                        + "</position></constraint><model>Shakespear</model></constraintModel>";
                testName = "position string[] " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", "0", Integer.toString(i) });
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "position int[] " + Integer.toString(i);
                query = builder.getNode(new int[] { 1003, 0, i }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "position int[][] " + Integer.toString(i);
                query = builder.getNode(new int[][] { { 1, 1003 }, { 3, i } }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
            }
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * This test builds more complex structure ship queries
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
                String expectedXML = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><structure>" + i
                        + "</structure></constraint><model>Shakespear</model></constraintModel>";
                testName = "structure string[] " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", Integer.toString(i) });
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "structure int[] " + Integer.toString(i);
                query = builder.getNode(new int[] { 1003, 0, 0, i }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "structure int[][] " + Integer.toString(i);
                query = builder.getNode(new int[][] { { 1, 1003 }, { 4, i } }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());

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
     * This test builds more complex truncation ship queries
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
                String expectedXML = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><truncation>" + i
                        + "</truncation></constraint><model>Shakespear</model></constraintModel>";
                testName = "truncation string[] " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "0", Integer.toString(i) });
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "truncation int[] " + Integer.toString(i);
                query = builder.getNode(new int[] { 1003, 0, 0, 0, i }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "truncation int[][] " + Integer.toString(i);
                query = builder.getNode(new int[][] { { 1, 1003 }, { 5, i } }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());

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
     * This test builds more complex completeness ship queries
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
                String expectedXML = XMLHEADER + "<constraintModel><constraint><semantic>1003</semantic><completeness>" + i
                        + "</completeness></constraint><model>Shakespear</model></constraintModel>";
                testName = "completeness string[] " + Integer.toString(i);
                query = builder.getNode(new String[] { "Shakespear", "1003", "0", "0", "0", "0", Integer.toString(i) });
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "completeness int[] " + Integer.toString(i);
                query = builder.getNode(new int[] { 1003, 0, 0, 0, 0, i }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
                testName = "completeness int[][] " + Integer.toString(i);
                query = builder.getNode(new int[][] { { 1, 1003 }, { 6, i } }, "Shakespear");
                assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
            }
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * This test builds more complex completeness ship queries
     */
    public void testMixedConstraintQueries()
    {
        String XMLStart = "<constraintModel><constraint>";
        String XMLEnd = "</constraint><model>Shakespear</model></constraintModel>";
        Node query = null;
        String testName = "";
        try
        {
            String expectedXML = XMLHEADER + XMLStart + "<semantic>1003</semantic>" + "<relation>100</relation>"
                    + "<position>4</position>" + "<structure>1</structure>" + "<truncation>2</truncation>" + XMLEnd;

            testName = "Mixed 1 string[] ";
            query = builder.getNode(new String[] { "Shakespear", "1003", "100", "4", "1", "2" });
            assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
            testName = "Mixed 1 int[] ";
            query = builder.getNode(new int[] { 1003, 100, 4, 1, 2 }, "Shakespear");
            assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());

            expectedXML = XMLHEADER + XMLStart + "<semantic>1001</semantic>" + "<relation>100</relation>"
                    + "<structure>1</structure>" + "<truncation>2</truncation>" + "<completeness>3</completeness>" + XMLEnd;

            testName = "Mixed 2 string[] ";
            query = builder.getNode(new String[] { "Shakespear", "1001", "100", "0", "1", "2", "3" });
            assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
            testName = "Mixed 2 int[] ";
            query = builder.getNode(new int[] { 1001, 100, 0, 1, 2, 3 }, "Shakespear");
            assertEquals("(" + testName + ") XML do not match", expectedXML, new JaferQuery(query).getXML());
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * This method tests that or queries are created correctly
     */
    public void testORQueries()
    {

        String expectedSimpleOrXML = XMLHEADER
                + "<or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>Oliver</model></constraintModel></or>";
        String expectedMultiOrXML = XMLHEADER
                + "<or><or><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>Oliver</model></constraintModel></or><constraintModel><constraint><semantic>7</semantic></constraint><model>34234</model></constraintModel></or>";
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node isbn = builder.getNode("isbn", "34234");

            testName = "Simple Or";
            Node simpleOr = builder.or(author, title);
            assertEquals(testName + " results do not match", expectedSimpleOrXML, new JaferQuery(simpleOr).getXML());

            testName = "Multi Or";
            Node multiOr = builder.or(simpleOr, isbn);
            assertEquals(testName + " results do not match", expectedMultiOrXML, new JaferQuery(multiOr).getXML());
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * method tests that and queries are created correctly
     */
    public void testANDQueries()
    {
        String expectedSimpleAndXML = XMLHEADER
                + "<and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>Oliver</model></constraintModel></and>";
        String expectedMultiAndXML = XMLHEADER
                + "<and><and><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel><constraintModel><constraint><semantic>4</semantic></constraint><model>Oliver</model></constraintModel></and><constraintModel><constraint><semantic>7</semantic></constraint><model>34234</model></constraintModel></and>";
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node isbn = builder.getNode("isbn", "34234");

            testName = "Simple And";
            Node simpleAnd = builder.and(author, title);
            assertEquals(testName + " results do not match", expectedSimpleAndXML, new JaferQuery(simpleAnd).getXML());

            testName = "Multi And";
            Node multiAnd = builder.and(simpleAnd, isbn);
            assertEquals(testName + " results do not match", expectedMultiAndXML, new JaferQuery(multiAnd).getXML());
       }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * method tests that not queries are created correctly
     */
    public void testNotQueries()
    {
        String expectedSimpleNotXML = XMLHEADER
                + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not>";
        String expectedMultimultiNotAndXML = XMLHEADER
                + "<and><constraintModel><constraint><semantic>7</semantic></constraint><model>34234</model></constraintModel>"
                + "<not><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></not></and>";
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node isbn = builder.getNode("isbn", "34234");

            testName = "Simple Not";
            Node simpleNot = builder.not(author);
            assertEquals(testName + " results do not match", expectedSimpleNotXML, new JaferQuery(simpleNot, true).getXML());

            testName = "Multi And Not";
            Node multiNotAnd = builder.and(simpleNot, isbn);
            assertEquals(testName + " results do not match", expectedMultimultiNotAndXML, new JaferQuery(multiNotAnd, true)
                    .getXML());

        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * this method tests the creation of a complex boolean query with OR and AND
     * conditions
     */
    public void testComplexBooleanQuery()
    {
        String expectedXML = XMLHEADER
                + "<and><or><or><constraintModel><constraint><semantic>4</semantic></constraint><model>Oliver</model></constraintModel><constraintModel><constraint><semantic>1003</semantic></constraint><model>Shakespear</model></constraintModel></or><and><constraintModel><constraint><semantic>1028</semantic></constraint><model>stock_number</model></constraintModel><constraintModel><constraint><semantic>1004</semantic></constraint><model>author_name_personal</model></constraintModel></and></or><and><constraintModel><constraint><semantic>27</semantic></constraint><model>lc_subject_heading</model></constraintModel><constraintModel><constraint><semantic>7</semantic></constraint><model>34234</model></constraintModel></and></and>";
        String testName = "";
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node title = builder.getNode("title", "Oliver");
            Node isbn = builder.getNode("isbn", "34234");
            Node lc_subject_heading = builder.getNode("lc_subject_heading", "lc_subject_heading");
            Node author_name_personal = builder.getNode("author_name_personal", "author_name_personal");
            Node stock_number = builder.getNode("stock_number", "stock_number");

            testName = "Simple Not";
            Node root = builder.and(builder.or(builder.or(title, author), builder.and(stock_number, author_name_personal)),
                    builder.and(lc_subject_heading, isbn));
            assertEquals(testName + " results do not match", expectedXML, new JaferQuery(root).getXML());
        }
        catch (QueryException e)
        {
            e.printStackTrace();
            fail("Query Exception (" + testName + "): " + e);
        }
    }

    /**
     * Tests the various get node functionality fails when bad values entered
     */
    public void testBadMultiDimentionalArrayQueries()
    {
        try
        {
            builder.getNode(new int[][] { { 0, 1003 } }, "Shakespear");
            fail("Exception should have been thrown due to 0 index in inner array elemts");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new int[][] { { 0, -1 } }, "Shakespear");
            fail("Exception should have been thrown due to negative value in  inner array elemts");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new int[][] { { 2, 1, 1003 }, { 1, 1003 } }, "Shakespear");
            fail("Exception should have been thrown due to to many inner array elemts");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new int[][] { { 2, 1003 }, { 1, 2, 1003 } }, "Shakespear");
            fail("Exception should have been thrown due to to many inner array elemts");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new int[][] { { 1, 1003 }, { 1, 1003 }, { 1, 1003 }, { 1, 1003 }, { 1, 1003 }, { 1, 1003 },
                    { 1, 1003 }, { 1, 1003 }, { 13, 0 }, { 5, 0 } }, "Shakespear");
            fail("Exception should have been thrown due to to many array elemts");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new int[][] {}, "Shakespear");
            fail("Exception should have been thrown due to to no array elemts");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new int[][] { { 1, 1003 }, { 13, 0 }, { 5, 0 } }, "Shakespear");
            fail("Exception should have been thrown due to bad index 13");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }

    }

    /**
     * Test bad values to integer use attribute queries
     */
    public void testBadIntegerUseAttributeQueries()
    {
        try
        {
            builder.getNode(-1, "Shakespear");
            fail("Exception should have been thrown due to -1 index ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }

        try
        {
            builder.getNode(1, "");
            fail("Exception should have been thrown due to  no term ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(1, null);
            fail("Exception should have been thrown due to  no term ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
    }

    /**
     * Test bad values to string use attribute queries
     */
    public void testBadStringUseAttributeQueries()
    {
        try
        {
            builder.getNode("badvalue", "Shakespear");
            fail("Exception should have been thrown due to badvalue not being available ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode("badvalue", null);
            fail("Exception should have been thrown due to no term ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode("", "Shakespear");
            fail("Exception should have been thrown due to empty attribute value ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }

    }

    /**
     * Test bad values to int array use attribute queries
     */
    public void testBadIntegerArrayUseAttributeQueries()
    {
        try
        {
            builder.getNode(new int[] {}, "Shakespear");
            fail("Exception should have been thrown due to no array values ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new int[] { 1, 4, 5, 6, 6, 6, 6, 78, 89, 90, 0 }, "Shakespear");
            fail("Exception should have been thrown due to too many array values ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new int[] { 4, -1 }, "Shakespear");
            fail("Exception should have been thrown due to negative array values ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
    }

    /**
     * Test bad values to string array use attribute queries
     */
    public void testBadStringArrayUseAttributeQueries()
    {
        try
        {
            builder.getNode(new String[] {});
            fail("Exception should have been thrown due to no array values ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new String[] { "", "1003", "0", "0", "0", "0", "0" });
            fail("Exception should have been thrown due to empty first array values ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new String[] { "Shakespear", "45", "1003", "0", "0", "0", "0", "0" });
            fail("Exception should have been thrown due to too many array values ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new String[] { "Shakespear", "1003", "-4", "0", "0", "0", "0" });
            fail("Exception should have been thrown due to too negative array values ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
        try
        {
            builder.getNode(new String[] { "Shakespear" });
            fail("Exception should have been thrown due to too negative array values ");
        }
        catch (QueryException e)
        {
            // success exception thrown
        }
    }

    /**
     * Test creating the query builder with parameters
     */
    public void testBadQueryBuilderConstruction()
    {
        try
        {
            new org.jafer.query.QueryBuilder(new int[] {});
            fail("Exception should have been thrown due to no values ");
        }
        catch (RuntimeException e)
        {
            // success exception thrown
        }
        try
        {
            new org.jafer.query.QueryBuilder(new int[] { 1, 3, 4, 5, 6, 8, 0, 0, 5 });
            fail("Exception should have been thrown due to too many values ");
        }
        catch (RuntimeException e)
        {
            // success exception thrown
        }
        try
        {
            new org.jafer.query.QueryBuilder(new int[] { 1, 3, 4, 5, 6, -6 });
            fail("Exception should have been thrown due to negative values ");
        }
        catch (RuntimeException e)
        {
            // success exception thrown
        }
        try
        {
            new org.jafer.query.QueryBuilder("sdfafsf");
            fail("Exception should have been thrown due to bad value ");
        }
        catch (RuntimeException e)
        {
            // success exception thrown
        }
        try
        {
            new org.jafer.query.QueryBuilder("1.1.1.1.1.1.1.1.1.1.1");
            fail("Exception should have been thrown due to long a value ");
        }
        catch (RuntimeException e)
        {
            // success exception thrown
        }
        try
        {
            new org.jafer.query.QueryBuilder("0.2.a.5.5.8");
            fail("Exception should have been thrown due to characters ");
        }
        catch (RuntimeException e)
        {
            // success exception thrown
        }
        try
        {
            new org.jafer.query.QueryBuilder("0.-2.5.5.5.8");
            fail("Exception should have been thrown due to negatives ");
        }
        catch (RuntimeException e)
        {
            // success exception thrown
        }
    }

    /**
     * Test creating the query builder with parameters
     */
    public void testBadBooleanQueries()
    {
        try
        {
            Node author = builder.getNode("author", "Shakespear");
            Node isbn = builder.getNode("isbn", "34234");

            try
            {
                builder.not(null);
                fail("Exception should have been thrown due null values ");
            }
            catch (QueryException e)
            {
                // success exception thrown
            }
            try
            {
                builder.or(author, null);
                fail("Exception should have been thrown due null values ");
            }
            catch (QueryException e)
            {
                // success exception thrown
            }
            try
            {
                builder.or(null, null);
                fail("Exception should have been thrown due null values ");
            }
            catch (QueryException e)
            {
                // success exception thrown
            }
            try
            {
                builder.and(isbn, null);
                fail("Exception should have been thrown due null values ");
            }
            catch (QueryException e)
            {
                // success exception thrown
            }
            try
            {
                builder.and(null, null);
                fail("Exception should have been thrown due null values ");
            }
            catch (QueryException e)
            {
                // success exception thrown
            }
        }
        catch (QueryException e)

        {
            e.printStackTrace();
            fail("Query Exception: " + e);
        }
    }
}
