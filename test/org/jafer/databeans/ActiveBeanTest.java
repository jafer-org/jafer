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

import junit.framework.TestCase;

import org.jafer.exception.JaferException;
import org.jafer.interfaces.Cache;
import org.jafer.interfaces.Databean;
import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.jafer.record.CacheFactory;
import org.jafer.record.Field;
import org.jafer.record.HashtableCacheFactory;
import org.jafer.registry.web.struts.bean.ModsRecord;
import org.w3c.dom.Node;

/**
 * This class runs a simple Unit tests on the ActiveBean thread class of the
 * DatabeanManager
 */
public class ActiveBeanTest extends TestCase
{

    /**
     * Stores a reference to MODS SCHEMA url
     */
    private static final String MODS_SCHEMA = "http://www.loc.gov/mods/v3";

    /**
     * Stores a reference to the default cache factory to use for the test
     */
    private CacheFactory cacheFactory = new HashtableCacheFactory();

    /**
     * Stores a reference to the activebean used in the test
     */
    private ActiveBean activeBean = null;

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
     * Creates an active bean
     * 
     * @param name The name of the bean
     * @param bean The databean that performs the search
     * @param autoPop Whether to enable auto population
     * @return The created ActiveBean
     */
    public ActiveBean createActiveBean(String name, Databean bean, boolean autoPop)
    {
        // set up the active bean for the test
        ActiveBean activeBean = new ActiveBean();
        activeBean.setName(name);
        activeBean.setDatabean(bean);
        activeBean.setAutoPopulateCache(autoPop);
        return activeBean;
    }

    /**
     * Creates a databean on the Oxford Advance server
     * 
     * @param cacheFac The cache Factory to use
     * @return The instantiated bean
     */
    public Databean createOxfordAdvanceBean(CacheFactory cacheFac)
    {
        ZurlFactory factory = new ZurlFactory("z3950s://library.ox.ac.uk:210/ADVANCE");
        factory.setRecordSchema(MODS_SCHEMA);

        Databean databean = factory.getDatabean();
        ((Cache) databean).setCache(cacheFac.getCache());
        return databean;

    }

    /**
     * Creates a databean on the INNOPAC server
     * 
     * @param cacheFac The cache Factory to use
     * @return The instantiated bean
     */
    public Databean createINNOPACBean(CacheFactory cacheFac)
    {
        ZurlFactory factory = new ZurlFactory("z3950s://130.111.64.9:210/INNOPAC");
        factory.setRecordSchema(MODS_SCHEMA);

        Databean databean = factory.getDatabean();
        ((Cache) databean).setCache(cacheFac.getCache());
        return databean;
    }

    /**
     * Wait for the search to complete not the whole thread
     * 
     * @throws JaferException
     */
    public void waitForSearchToComplete() throws JaferException
    {
        // wait until the active bean has completed it's search by checking
        // if it is still searching. Can not check thread alive status as
        // ActiveBean may have started to cache data
        while (activeBean.stillSearching())
        {
            // yield this thread to give active bean chance to execute
            Thread.yield();
        }

        // bean has now completed search so see how many records were found
        int recordsFound = activeBean.getNumberOfResults();
        // if we had results set the offsets
        if (recordsFound > 0)
        {
            activeBean.setOffsets(1, recordsFound);
        }
    }

    /**
     * Wait for the whole thread to complete
     * 
     * @throws JaferException
     */
    public void waitForThreadToComplete() throws JaferException
    {
        // wait until the active bean has completed it's search by checking
        // if it is still searching. Can not check thread alive status as
        // ActiveBean may have started to cache data
        while (activeBean.isAlive())
        {
            // yield this thread to give active bean chance to execute
            Thread.yield();
        }

        // bean has now completed search so see how many records were found
        int recordsFound = activeBean.getNumberOfResults();
        // if we had results set the offsets
        if (recordsFound > 0)
        {
            activeBean.setOffsets(1, recordsFound);
        }
    }

    /**
     * Perform a simple test to make sure the active bean can search and
     * retrieve a single object whilst the cache is filling up.
     */
    public void testSimpleSearch()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), true);

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf :records, facts and champions");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, MODS_SCHEMA);

            waitForSearchToComplete();

            assertEquals("Did not find the expected number of records", 1, activeBean.getNumberOfResults());

            // only have one result so get it
            Field field = activeBean.getRecord(1, MODS_SCHEMA);
            ModsRecord mods = new ModsRecord(field);

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
     * Perform a simple test to make sure the active bean can search and that
     * the cache is full (as size is 30 twice that of the fetch size of 15) when
     * the thread terminates. The records in the cache must allso be the first
     * 30 records
     */
    public void testSmallCacheFillsFullyFactory()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), true);

            // update the databean with a cache factory that has a size of 30
            // FETCH SIZE IS 15
            CacheFactory factory = new HashtableCacheFactory(30);
            ((Cache) activeBean.getDatabean()).setCache(factory.getCache());

            Cache cacheBean = (Cache) activeBean.getDatabean();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, MODS_SCHEMA);

            waitForThreadToComplete();

            assertEquals("Did not find the expected number of records", 1762, activeBean.getNumberOfResults());
            assertTrue("Cache not full or only fetch size spaces free", cacheBean.getAvailableSlots() == 0
                    || cacheBean.getAvailableSlots() <= cacheBean.getFetchSize());

            // check all the cache records are the first 30
            for (int index = 1; index <= 30; index++)
            {
                assertTrue("Bean should have record: " + index, cacheBean.getCache().contains(new Integer(index)));
            }
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
     * Perform a simple test to make sure the active bean can search and that
     * the cache is full with 5 slots free (as size is 35 twice that of the
     * fetch size of 15 leaves 5 free slots) when the thread terminates. The
     * records in the cache must allso be the first 30 records
     */
    public void testSmallCacheFillsLeavingFewFetchslotsFullyFactory()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), true);

            // update the databean with a cache factory that has a size of 45
            // FETCH SIZE IS 15
            CacheFactory factory = new HashtableCacheFactory(35);
            ((Cache) activeBean.getDatabean()).setCache(factory.getCache());

            Cache cacheBean = (Cache) activeBean.getDatabean();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, MODS_SCHEMA);

            waitForThreadToComplete();

            assertEquals("Did not find the expected number of records", 1762, activeBean.getNumberOfResults());
            assertTrue("Cache not full or only fetch size spaces free", cacheBean.getAvailableSlots() == 0
                    || cacheBean.getAvailableSlots() <= cacheBean.getFetchSize());
            assertTrue("Cache should have 5 free spaces spaces free", cacheBean.getAvailableSlots() == 5);

            // check all the cache records are the first 30
            for (int index = 1; index <= 30; index++)
            {
                assertTrue("Bean should have record: " + index, cacheBean.getCache().contains(new Integer(index)));
            }
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
     * Perform a simple test to make sure the active bean can search and that
     * prepopulation can be stopped before it has completed filling the cache
     */
    public void testCachePrepopCanBeStoppedByCaller()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), true);
            Cache cacheBean = (Cache) activeBean.getDatabean();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, MODS_SCHEMA);

            waitForSearchToComplete();

            // sleep this thread for a while to give cache time to populate up a
            // bit
            Thread.sleep(5000);

            // stop the prepopulation
            activeBean.stopActiveBeanSearch(true);

            waitForThreadToComplete();

            assertEquals("Did not find the expected number of records", 1762, activeBean.getNumberOfResults());
            assertFalse("Cache full or only fetch size spaces free should have stopped way before getting full", cacheBean
                    .getAvailableSlots() == 0
                    || cacheBean.getAvailableSlots() <= cacheBean.getFetchSize());
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
        catch (Exception exc)
        {
            exc.printStackTrace();
            fail("Exception:" + exc);
        }
    }

    /**
     * Perform a simple test to make sure the active bean can search and
     * prepopulate whilst records are read by the caller
     */
    public void testAccessRecordsWhilstPopulating()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), true);
            Cache cacheBean = (Cache) activeBean.getDatabean();

            // update the databean with a cache factory that has a size of 160
            CacheFactory factory = new HashtableCacheFactory(160);
            ((Cache) activeBean.getDatabean()).setCache(factory.getCache());

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, MODS_SCHEMA);

            waitForSearchToComplete();

            int recordsFound = activeBean.getNumberOfResults();

            assertEquals("Did not find the expected number of records", 1762, recordsFound);

            // get first 20 records whilst cache populates
            for (int index = 1; index <= 20; index++)
            {
                activeBean.getRecord(index, MODS_SCHEMA);
            }

            // stop the prepopulation
            activeBean.stopActiveBeanSearch(true);

            waitForThreadToComplete();

            assertTrue("Cache not full or only fetch size spaces free", cacheBean.getAvailableSlots() == 0
                    || cacheBean.getAvailableSlots() <= cacheBean.getFetchSize());
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
     * Perform a simple test to make sure the active bean can search and does
     * not auto populate when switched off
     */
    public void testAutoPopulationSwitchedOff()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), false);

            Cache cacheBean = (Cache) activeBean.getDatabean();

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, MODS_SCHEMA);

            waitForThreadToComplete();

            int recordsFound = activeBean.getNumberOfResults();

            assertEquals("Did not find the expected number of records", 1762, recordsFound);
            assertTrue("Cache should be empty", cacheBean.getAvailableSlots() == cacheBean.getDataCacheSize());
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
     * check that on supplying a bad connection the search fails and the
     * exception is stored correctly
     */
    public void testExceptionStoredForBadURL()
    {
        try
        {
            ZurlFactory factory = new ZurlFactory("z3950s://library.ox.ac.uk:210/ADVANC");
            factory.setRecordSchema(MODS_SCHEMA);
            Databean databean = factory.getDatabean();
            ((Cache) databean).setCache(cacheFactory.getCache());

            // set up the active bean for the test
            activeBean = createActiveBean("db1", databean, true);

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf :records, facts and champions");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, MODS_SCHEMA);

            waitForSearchToComplete();

            assertEquals("Did not find the expected number of records", 0, activeBean.getNumberOfResults());

            String expectedMsgdb1 = "Error attempting search (library.ox.ac.uk:210, dataBase(s) ADVANC , username null): Diagnostic 109 - Database unavailable (database name: ADVANC)";
            JaferException exc = activeBean.getSearchException();
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
     * check that if you supply a bad schema to the submit process with auto
     * cache on it fails correctly
     */
    public void testNullSchemaOnSubmit()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), true);

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf :records, facts and champions");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, null);

            waitForSearchToComplete();

            assertEquals("Did not find the expected number of records", 1, activeBean.getNumberOfResults());

            JaferException exc = activeBean.getSearchException();
            assertTrue("Wrong Exception", exc.getMessage().indexOf(
                    "Record Schema must be set to search when prepopulating of the cache is enabled") != -1);

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
     * check that if you supply a bad schema to the retrieve process with auto
     * cache on it fails correctly
     */
    public void testNullSchemaOnRetrieve()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), true);

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf :records, facts and champions");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, MODS_SCHEMA);

            waitForSearchToComplete();

            assertEquals("Did not find the expected number of records", 1, activeBean.getNumberOfResults());

            activeBean.getRecord(1, null);

            fail("should have errored due to bad schema name");
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
     * check that if you supply a bad schema to the submit process with auto
     * cache on it fails correctly
     */
    public void testEmptySchemaOnSubmit()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), true);

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf :records, facts and champions");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, "");

            waitForSearchToComplete();

            assertEquals("Did not find the expected number of records", 1, activeBean.getNumberOfResults());

            JaferException exc = activeBean.getSearchException();
            assertTrue("Wrong Exception", exc.getMessage().indexOf(
                    "Record Schema must be set to search when prepopulating of the cache is enabled") != -1);

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
     * check that if you supply a bad schema to the retrieve process with auto
     * cache on it fails correctly
     */
    public void testEmptySchemaOnRetrieve()
    {
        try
        {
            // set up the active bean for the test
            activeBean = createActiveBean("db1", createOxfordAdvanceBean(cacheFactory), true);

            // form a simple search query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "Golf :records, facts and champions");

            // execute it on the beans to see how many results were found
            activeBean.submitQuery(query, MODS_SCHEMA);

            waitForSearchToComplete();

            assertEquals("Did not find the expected number of records", 1, activeBean.getNumberOfResults());

            activeBean.getRecord(1, "");

            fail("should have errored due to bad schema name");
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
}
