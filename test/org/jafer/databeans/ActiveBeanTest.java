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
            activeBean.setOffsets(1,recordsFound);
        }
    }

    /**
     * Perform a simple test to make sure the active bean can search
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

            // wait for the search to complete
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
}
