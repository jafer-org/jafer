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
import org.jafer.interfaces.DatabeanFactory;
import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.jafer.record.CacheFactory;
import org.jafer.record.HashtableCacheFactory;
import org.jafer.registry.web.struts.bean.ModsRecord;
import org.w3c.dom.Node;

/**
 * This class runs a simple Unit test in the DatabeanManagerFactory
 */
public class DatabeanManagerFactoryTest extends TestCase
{

    private static final String MODS_SCHEMA = "http://www.loc.gov/mods/v3";

    /**
     * Stores a reference to beanManager created to run the test
     */
    private DatabeanManager beanManager = null;

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
     * Create a simple DatabeanManager using the factory and perform a search
     */
    public void testOneDatabaseConfig()
    {
        try
        {
            // create ZURL DataBeanFacories
            ZurlFactory zurl1 = new ZurlFactory("z3950s://library.ox.ac.uk:210/ADVANCE");
            zurl1.setRecordSchema(MODS_SCHEMA);
            zurl1.setName("db1");

            DatabeanManagerFactory factory = new DatabeanManagerFactory();
            factory.setMode(DatabeanManagerFactory.MODE_PARALLEL);
            factory.setName("DBFactTest");
            factory.setDatabeanFactories(new DatabeanFactory[] { zurl1 });

            beanManager = (DatabeanManager) factory.getDatabean();

            // ???????????????? SHOULD THIS ALREADY BE SET??????????????
            beanManager.setRecordSchema(MODS_SCHEMA);

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
     * Create a simple DatabeanManager using the factory and perform a search
     */
    public void testTwoDatabaseConfig()
    {
        try
        {
            // create ZURL DataBeanFacories
            ZurlFactory zurl1 = new ZurlFactory("z3950s://library.ox.ac.uk:210/ADVANCE");
            zurl1.setRecordSchema(MODS_SCHEMA);
            zurl1.setName("db1");
            // create ZURL DataBeanFacories
            ZurlFactory zurl2 = new ZurlFactory("z3950s://130.111.64.9:210/INNOPAC");
            zurl2.setRecordSchema(MODS_SCHEMA);
            zurl2.setName("db2");

            DatabeanManagerFactory factory = new DatabeanManagerFactory();
            factory.setMode(DatabeanManagerFactory.MODE_PARALLEL);
            factory.setName("DBFactTest");
            factory.setDatabeanFactories(new DatabeanFactory[] { zurl1, zurl2 });

            beanManager = (DatabeanManager) factory.getDatabean();

            // ???????????????? SHOULD THIS ALREADY BE SET??????????????
            beanManager.setRecordSchema(MODS_SCHEMA);

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
     * No targets supplied should throw an exception
     */
    public void testFactoryNotSetUp()
    {
        try
        {
            DatabeanManagerFactory factory = new DatabeanManagerFactory();
            factory.setMode(DatabeanManagerFactory.MODE_PARALLEL);
            beanManager = (DatabeanManager) factory.getDatabean();

            // ???????????????? SHOULD THIS ALREADY BE SET??????????????
            beanManager.setRecordSchema(MODS_SCHEMA);

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
}
