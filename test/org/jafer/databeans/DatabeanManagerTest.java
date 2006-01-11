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

import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.jafer.exception.JaferException;
import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.jafer.record.CacheFactory;
import org.jafer.record.HashtableCacheFactory;
import org.jafer.registry.web.struts.bean.ModsRecord;
import org.jafer.util.xml.XMLSerializer;
import org.w3c.dom.Node;

/**
 * This class runs a simple Unit test in the DatabeanManager
 */
public class DatabeanManagerTest extends TestCase
{

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
            assertEquals("Did not find the expected number of records", 2128, recsFound);

            
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
            assertEquals("Did not find the expected number of records", 366, recsFound);

            
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
