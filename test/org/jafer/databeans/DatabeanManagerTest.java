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
package org.jafer.databeans;

import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.jafer.exception.JaferException;
import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.jafer.record.CacheFactory;
import org.jafer.record.HashtableCacheFactory;
import org.jafer.registry.web.struts.bean.ModsRecord;
import org.w3c.dom.Node;

/**
 * This class runs a simple Unit tests on the DatabeanManager
 */
public class DatabeanManagerTest extends TestCase
{

    /**
     * Stores a reference to MODS SCHEMA url
     */
    private static final String MODS_SCHEMA = "http://www.loc.gov/mods/v3";

    /**
     * Stores a reference to targets to use for each search. Should be
     * repopulated at each test as used during set up
     */
    private Hashtable targets = new Hashtable();

    /**
     * Stores a reference to mode to use for the test, used to configure during
     * setup
     */
    private String mode = DatabeanManagerFactory.MODE_PARALLEL;

    /**
     * Stores a reference to beanManager created to run the test
     */
    private DatabeanManager beanManager = null;

    /**
     * Stores a reference to the cache factory to use for the test
     */
    private CacheFactory cacheFactory = null;

    /**
     * Utility method to print out the mods record
     * 
     * @param mods themods record
     * @param index the index position
     */
    public void outputModsData(ModsRecord mods, int index)
    {
        System.out.println("MODS RECORD:" + index);
        System.out.println("\tTitle    :" + mods.getTitle());
        System.out.println("\tAuthor   :" + mods.getAuthor());
        System.out.println("\tPublisher:" + mods.getField("publisher"));
        System.out.println("\tType     :" + mods.getType());
    }

    /**
     * Ouputs all the records from 1 to recount
     * 
     * @param recCount number of records to output
     */
    public void outputAllRecords(int recCount)
    {
        try
        {
            for (int index = 1; index <= recCount; index++)
            {
                beanManager.setRecordCursor(index);
                ModsRecord mods = new ModsRecord(beanManager.getCurrentRecord());
                outputModsData(mods, index);
            }
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * called before a test to set upthe databeanManager
     */
    public void initialiseTestGlobals()
    {
        Enumeration enumer = targets.keys();

        beanManager = new DatabeanManager();
        Hashtable databeansFactories = new Hashtable();

        while (enumer.hasMoreElements())
        {
            String targetName = (String) enumer.nextElement();
            // create a ZURLFactory for the target
            ZurlFactory factory = new ZurlFactory((String) targets.get(targetName));
            // set the factory to use MODS schema
            factory.setRecordSchema(MODS_SCHEMA);
            // add the factory to the list of databeans using targetName as key
            databeansFactories.put(targetName, factory);
        }

        // set the factories on the bean manager
        beanManager.setDatabeanFactories(databeansFactories);
        // set the cache factory
        beanManager.setCacheFactory(cacheFactory);
        // set the manager to search in parallel mode
        beanManager.setMode(mode);
        // set the databases with is the set of all the factoy names
        beanManager.setDatabases((String[]) databeansFactories.keySet().toArray(new String[] {}));
        // set the record schema to be MODS
        beanManager.setRecordSchema(MODS_SCHEMA);
    }

    /**
     * called after a test to cleanup data
     */
    public void teardown()
    {
        // set all the variables back to null so no test params creep into next
        // test
        mode = null;
        cacheFactory = null;
        beanManager = null;
        targets.clear();
    }

    /**
     * Perform a simple test to extract one record and make sure all fields
     * match using parallel mode. Internal Databean cache factory will be used
     */
    public void testOneDatabaseSimpleSearchNoCacheFactory()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf :records, facts and champions");

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertEquals("Did not find the expected number of records", 1, recsFound);

            // only have one result so get it
            beanManager.setRecordCursor(1);
            ModsRecord mods = new ModsRecord(beanManager.getCurrentRecord());

            assertEquals("Title does not match", "Golf :records, facts and champions", mods.getTitle().trim());
            assertEquals("Author does not match", "Donald Steel", mods.getAuthor().trim());
            assertEquals("Publisher does not match", "Guinness", mods.getField("publisher").trim());
            assertEquals("Type does not match", "text", mods.getType().trim());

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Perform a simple test to extract one record and make sure all fields
     * match using parallel mode. Databean cache factory will be supplied
     */
    public void testOneDatabaseSimpleSearchCacheFactory()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            cacheFactory = new HashtableCacheFactory();

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Mordillo golf");

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertEquals("Did not find the expected number of records", 1, recsFound);

            // only have one result so get it
            beanManager.setRecordCursor(1);
            ModsRecord mods = new ModsRecord(beanManager.getCurrentRecord());

            assertEquals("Title does not match", "Mordillo golf", mods.getTitle().trim());
            assertEquals("Author does not match", "Mordillo, Guillermo.", mods.getAuthor().trim());
            assertEquals("Publisher does not match", "Stanley Paul", mods.getField("publisher").trim());
            assertEquals("Type does not match", "text", mods.getType().trim());

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Perform a simple test to extract one record and make sure all fields
     * match using serial mode. Internal Databean cache factory will be used
     */
    public void testOneDatabaseSimpleSearchSerialNoCacheFactory()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_SERIAL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf :records, facts and champions");

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertEquals("Did not find the expected number of records", 1, recsFound);

            // only have one result so get it
            beanManager.setRecordCursor(1);
            ModsRecord mods = new ModsRecord(beanManager.getCurrentRecord());

            assertEquals("Title does not match", "Golf :records, facts and champions", mods.getTitle().trim());
            assertEquals("Author does not match", "Donald Steel", mods.getAuthor().trim());
            assertEquals("Publisher does not match", "Guinness", mods.getField("publisher").trim());
            assertEquals("Type does not match", "text", mods.getType().trim());

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Perform a simple test to extract one record and make sure all fields
     * match using serial mode. Databean cache factory will be supplied
     */
    public void testOneDatabaseSimpleSearchSerialCacheFactory()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_SERIAL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            cacheFactory = new HashtableCacheFactory();

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Mordillo golf");

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertEquals("Did not find the expected number of records", 1, recsFound);

            // only have one result so get it
            beanManager.setRecordCursor(1);
            ModsRecord mods = new ModsRecord(beanManager.getCurrentRecord());

            assertEquals("Title does not match", "Mordillo golf", mods.getTitle().trim());
            assertEquals("Author does not match", "Mordillo, Guillermo.", mods.getAuthor().trim());
            assertEquals("Publisher does not match", "Stanley Paul", mods.getField("publisher").trim());
            assertEquals("Type does not match", "text", mods.getType().trim());

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Perform a simple test to extract two record using two databases and make
     * sure all fields match using parallel mode. Internal Databean cache
     * factory will be used
     */
    public void testTwoDatabaseSimpleSearchNoCacheFactory()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Antique golf collectibles :a price and reference guide");

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertEquals("Did not find the expected number of records", 2, recsFound);

            // only have one result so get it
            beanManager.setRecordCursor(1);
            ModsRecord mods = new ModsRecord(beanManager.getCurrentRecord());
            assertEquals("Title does not match", "Antique golf collectibles :a price and reference guide", mods.getTitle().trim());
            assertEquals("Author does not match", "Chuck Furjanic.", mods.getAuthor().trim());
            assertEquals("Publisher does not match", "Krause Publications", mods.getField("publisher").trim());
            assertEquals("Type does not match", "text", mods.getType().trim());
            beanManager.setRecordCursor(2);
            mods = new ModsRecord(beanManager.getCurrentRecord());
            assertEquals("Title does not match", "Antique golf collectibles :a price and reference guide", mods.getTitle().trim());
            assertEquals("Author does not match", "Chuck Furjanic ; edited by Maria Furjanic.", mods.getAuthor().trim());
            assertEquals("Publisher does not match", "Krause Publications", mods.getField("publisher").trim());
            assertEquals("Type does not match", "text", mods.getType().trim());

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Perform a search over two databases and make sure we get the correct
     * number of results. Not possible to varify any deeper as not all the
     * search clause info will be in the mods records fields extracted to verify
     * matches Internal Databean cache factory will be used
     */
    public void testTwoDatabaseHitSearchNoCacheFactory()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertTrue("Did not find the expected minimum number of records", 2128 <= recsFound);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Expect to get less than testTwoDatabaseHitSearchNoCacheFactory test as
     * now in serial mode so second search results will be ignored. Internal
     * Databean cache factory will be used
     */
    public void testTwoDatabaseHitSearchSerialNoCacheFactory()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_SERIAL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertTrue("Did not find the expected minimum number of records", 366 <= recsFound);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * No targets supplied should throw an exception
     */
    public void testNoDatabasesFactories()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);
            fail("Should have had an exception due to no databases defined");

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            assertTrue("Wrong Exception", exc.getMessage().indexOf("Unable to search") != -1);
        }
    }

    /**
     * Tests that the correct database name is returned for the current record
     */
    public void testGetDataBaseName()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertTrue("Did not find the expected minimum number of records", 2128 <= recsFound);
            beanManager.setRecordCursor(3);
            assertEquals("Wrong database name", "db2", beanManager.getCurrentDatabase());
            beanManager.setRecordCursor(2000);
            assertEquals("Wrong database name", "db1", beanManager.getCurrentDatabase());

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Tests that the correct database name return correct counts
     */
    public void testGetNumberOfResultsPerDataBaseName()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertTrue("Did not find the expected minimum number of records", 2128 <= recsFound);
            assertTrue("Wrong count for database name db1", 1762 <= beanManager.getNumberOfResults("db1"));
            assertTrue("Wrong count for database name db2", 366 <= beanManager.getNumberOfResults("db2"));
            assertTrue("Did not find the expected number of records after asking each and adding", 2128 <= beanManager
                    .getNumberOfResults("db1")
                    + beanManager.getNumberOfResults("db2"));
            assertTrue("Did not find the expected number of records when asked bean manager", 2128 <= beanManager
                    .getNumberOfResults());

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Tests that -1 returned for bad database name
     */
    public void testGetNumberOfResultsForBadDataBaseName()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);
            assertEquals("Wrong count for bad database name ", -1, beanManager.getNumberOfResults("rrrr"));

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * databases specified not in factories should throw exception
     */
    public void testDatabasesFactoriesButDatabasesNotFound()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            beanManager.setDatabases(new String[] { "id1", "id2" });

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);
            fail("Should have had an exception due to no databases defined of type id1,id2");

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            assertTrue("Wrong Exception", exc.getMessage().indexOf("Specified database was not found") != -1);
        }
    }

    /**
     * no databases specified to setdatabases() call should throw exception
     */
    public void testDatabasesFactoriesButDatabasesNotFoundDueToNullSettingDatabases()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            beanManager.setDatabases(new String[0]);

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);
            fail("Should have had an exception due to no databases defined");

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            assertTrue("Wrong Exception", exc.getMessage().indexOf("No databases were found") != -1);
        }
    }

    /**
     * not all databases specified in factories should throw exception
     */
    public void testDatabasesFactoriesButNotAllDatabasesFound()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            beanManager.setDatabases(new String[] { "id1", "db1" });

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);
            fail("Should have had an exception due to no databases defined of type id1");

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            assertTrue("Wrong Exception", exc.getMessage().indexOf("Specified database was not found") != -1);
        }
    }

    /**
     * test a bad cursor position returns an error on getting record
     */
    public void testBadCursorOnGettingRecord()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);
            beanManager.setRecordCursor(100000);
            beanManager.getCurrentRecord();
            fail("Should have had an exception due to cursor out of range");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            assertTrue("Wrong Exception", exc.getMessage().indexOf("Record Cursor Out of Range") != -1);
        }
    }

    /**
     * test no schema name supplied to search
     */
    public void testBadSchemaNameOnSearch()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // blank out record schema
            beanManager.setRecordSchema(null);

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);
            beanManager.setRecordCursor(1);
            beanManager.getCurrentRecord();
            fail("Should have had an exception due to no schema set");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            assertTrue("Wrong Exception", exc.getMessage().indexOf("Record Schema must be set to search when prepopulating of the cache is enabled") != -1);
        }
    }
    
    /**
     * test no schema name supplied to search
     */
    public void testBadSchemaNameOnRetrieve()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // blank out record schema
            beanManager.setRecordSchema(null);
            beanManager.setAutoPopulateCache(false);

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);
            beanManager.setRecordCursor(1);
            beanManager.getCurrentRecord();
            fail("Should have had an exception due to no schema set");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            assertTrue("Wrong Exception", exc.getMessage().indexOf("Record Schema must be set to retrieve a record") != -1);
        }
    }

    /**
     * test a bad cursor position returns an error on getting database name
     */
    public void testBadCursorOnGettingDatabaseName()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);
            beanManager.setRecordCursor(100000);
            beanManager.getCurrentDatabase();
            fail("Should have had an exception due to cursor out of range");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            assertTrue("Wrong Exception", exc.getMessage().indexOf("Record Cursor Out of Range") != -1);
        }
    }

    /**
     * bad target url should fail to connect
     */
    public void testBadTargetURL()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);

            assertEquals("Did not find the expected number of records", 0, recsFound);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test that get search exception works for bad target url
     */
    public void testGetSearchExecptionBadTargetURL()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);

            assertEquals("Did not find the expected number of records", 0, recsFound);

            String expectedMsgdb1 = "Error attempting search (library.ox.ac.uk:210, dataBase(s) ADVANC , username null): Diagnostic 109 - Database unavailable (database name: ADVANC)";
            JaferException exc = beanManager.getSearchException("db1");
            assertEquals("wrong exception returned", expectedMsgdb1, exc.getMessage());

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test that get search exception works for bad target url on both databases
     */
    public void testGetSearchExecptionTwoBadTargetURLs()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANC");
            targets.put("db2", "z3950s://130.111.64.9:210/INN");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);

            assertEquals("Did not find the expected number of records", 0, recsFound);

            String expectedMsgdb1 = "Error attempting search (library.ox.ac.uk:210, dataBase(s) ADVANC , username null): Diagnostic 109 - Database unavailable (database name: ADVANC)";
            String expectedMsgdb2 = "Error attempting search (130.111.64.9:210, dataBase(s) INN , username null): Diagnostic 236 - Access to specified database denied (database name: Access to specified database denied)";
            // check single calls first
            JaferException exc = beanManager.getSearchException("db1");
            assertEquals("wrong exception returned", expectedMsgdb1, exc.getMessage());
            exc = beanManager.getSearchException("db2");
            assertEquals("wrong exception returned", expectedMsgdb2, exc.getMessage());

            // check array call
            JaferException[] excArray = beanManager.getSearchException(new String[] { "db1", "db2" });
            assertEquals("Should have two exceptions", 2, excArray.length);

            assertEquals("wrong exception returned for db1", expectedMsgdb1, excArray[0].getMessage());
            assertEquals("wrong exception returned for db2", expectedMsgdb2, excArray[1].getMessage());

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test that get search exception works when no exceptions thrown
     */
    public void testGetSearchExecptionWithNoExceptionsExpected()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);

            // check single calls first
            JaferException exc = beanManager.getSearchException("db1");
            assertNull("no exception should be returned", exc);
            exc = beanManager.getSearchException("db2");
            assertNull("no exception should be returned", exc);

            // check array call
            JaferException[] excArray = beanManager.getSearchException(new String[] { "db1", "db2" });
            assertEquals("Should have two exceptions", 2, excArray.length);
            assertNull("no exception should be returned for db1", excArray[0]);
            assertNull("no exception should be returned for db2", excArray[1]);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test that get search exception works with unkown databases
     */
    public void testGetSearchExecptionWithUnkownDatabases()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_PARALLEL;
            targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
            targets.put("db2", "z3950s://130.111.64.9:210/INN");

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            beanManager.submitQuery(query);

            // check single calls first
            JaferException exc = beanManager.getSearchException("db9");
            assertNull("no exception should be returned", exc);

            // check array call
            JaferException[] excArray = beanManager.getSearchException(new String[] { "db1", "db2", "db9" });
            assertEquals("Should have three exceptions", 3, excArray.length);

            String expectedMsgdb2 = "Error attempting search (130.111.64.9:210, dataBase(s) INN , username null): Diagnostic 236 - Access to specified database denied (database name: Access to specified database denied)";
            assertEquals("Should have thwo exceptions", 3, excArray.length);
            assertNull("no exception should be returned for db1", excArray[0]);

            assertEquals("wrong exception returned for db2", expectedMsgdb2, excArray[1].getMessage());
            assertNull("no exception should be returned for db9", excArray[2]);

        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * test that if you do set databases you get them returned
     */
    public void testGetDatabasesWhenSet()
    {

        // set up globals
        mode = DatabeanManagerFactory.MODE_PARALLEL;
        targets.put("db1", "z3950s://library.ox.ac.uk:210/ADVANCE");
        targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

        // initialise globals
        initialiseTestGlobals();

        String[] databases = beanManager.getDatabases();
        if (!(databases[0].equals("db1") && databases[1].equals("db2") || databases[1].equals("db1")
                && databases[0].equals("db2")))
        {
            fail("Dbnames returned incorrect");
        }

    }

    /**
     * test that if you do not set any databases you don't get any returned
     */
    public void testGetDatabasesWhenNoneSet()
    {

        // set up globals
        mode = DatabeanManagerFactory.MODE_PARALLEL;

        // initialise globals
        initialiseTestGlobals();

        String[] databases = beanManager.getDatabases();
        assertTrue("should have no databases", databases.length == 0);
    }

    /**
     * Perform a simple test to extract mulitple records with a restricted cache size
     */
    public void testOneDatabaseBigSearchLimitedCacheFactory()
    {
        try
        {
            // set up globals
            mode = DatabeanManagerFactory.MODE_SERIAL;
            targets.put("db2", "z3950s://130.111.64.9:210/INNOPAC");

            // restrict the cache to ten records
            cacheFactory = new HashtableCacheFactory(15);

            // initialise globals
            initialiseTestGlobals();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node q1 = builder.getNode("title", "golf");
            Node q2 = builder.getNode("title", "titlest");
            Node query = builder.or(q1, q2);

            // execute it across all the beans to see how many results were
            // found
            int recsFound = beanManager.submitQuery(query);
            assertEquals("Did not find the expected number of records", 367, recsFound);         
             
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("QueryException:" + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }
}
