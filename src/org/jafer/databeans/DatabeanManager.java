/**
 * JAFER Toolkit Project.
 * Copyright (C) 2002, JAFER Toolkit Project, Oxford University.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.databeans;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.jafer.exception.JaferException;
import org.jafer.interfaces.Cache;
import org.jafer.interfaces.Databean;
import org.jafer.interfaces.DatabeanFactory;
import org.jafer.interfaces.Present;
import org.jafer.interfaces.Search;
import org.jafer.record.CacheFactory;
import org.jafer.record.Field;
import org.jafer.record.RecordException;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Node;

/**
 * This inner class represents a thread that performs a search on a Jafer Client
 * (AbstractClient). It is configured and utilised by the DataBeanManager in
 * order to facilitate parallel searches.
 */

class ActiveBean extends java.lang.Thread
{

    /**
     * Stores a reference to a flag that indicates that the thread should
     * terminate and prefilling of the cache should stop if it has not yet
     * completed
     */
    private boolean stopActiveBeanSearch = false;

    /**
     * Stores a reference to whether the search is still executing.
     */
    private boolean stillSearching = false;

    /**
     * Stores a reference to whether pre filling of the caching is enabled or
     * not. Enabled as default.
     */
    private boolean autoPopulateCache = true;

    /**
     * Stores a reference to record schema to use when preopoulating the cache
     */
    private String cacheRecordSchema = null;

    /**
     * Stores a reference to the offset this ActiveBean starts at in the super
     * result set of all the ActiveBeans
     */
    private int startOffset;

    /**
     * Stores a reference to the offset this ActiveBean ends at in the super
     * result set of all the ActiveBeans
     */
    private int endOffset;

    /**
     * Stores a reference to the bean that this ActiveBean uses to perform
     * searches and to retrieve results from. It must implement the Search and
     * Present interfaces.
     */
    private Databean databean;

    /**
     * Stores a reference to query that should be submited to the
     * jaferClientBean when the thread is executed
     */
    private Object query;

    /**
     * Stores a reference to logger instance
     */
    private static Logger logger = Logger.getLogger("org.jafer.databeans");

    /**
     * Stores a reference to exception that occured in the last search or null
     * if no errors occured
     */
    private JaferException searchException = null;

    /**
     * Sets the offsets for this ActiveBean result set in respect to the super
     * result set of all the ActiveBeans
     *
     * @param start The starting offset index
     * @param end The ending offset index
     */
    public void setOffsets(int start, int end)
    {
        this.startOffset = start;
        this.endOffset = end;
    }

    /**
     * Returns a boolean to indicate if the record index supplied is contained
     * with in the result set of this ActiveBean. IE that the record index is
     * between the offsets.
     *
     * @param recordIndex The index to search for
     * @return true if the record is contained in this ActiveBean
     */
    public boolean containsRecord(int recordIndex)
    {
        return (recordIndex >= startOffset && recordIndex <= endOffset);
    }

    /**
     * Gets a reference to the JaferClientBean for this ActiveBean
     *
     * @return The instance of the DataBean
     */
    public Databean getDatabean()
    {
        return databean;
    }

    /**
     * sets a reference to the JaferClientBean for this ActiveBean
     *
     * @param bean The JaferClientBean to store in this ActiveBean
     */
    public void setDatabean(Databean bean)
    {
        this.databean = bean;
    }

    /**
     * This method returns the JaferException from the last search.
     *
     * @return JaferException instance or null if no errors were found
     */
    public JaferException getSearchException()
    {
        return this.searchException;
    }

    /**
     * This method sets the last exception that occurred during a search
     *
     * @param exc The exception that occurred
     */
    protected void setSearchException(JaferException exc)
    {
        this.searchException = exc;
    }

    /**
     * Set the query that will be executed when this ActiveBeans thread is run
     *
     * @param query The query to execute
     * @param schema The schema used to auto populate cache
     */
    public void submitQuery(Object query, String schema)
    {
        // if the query is of type Node we need to import it into a new document
        if (query instanceof Node)
            this.query = DOMFactory.newDocument().importNode((Node) query, true);
        else
            this.query = query;

        // set the schema to use when prepopulating the cache
        setPrepoulateCacheRecordSchema(schema);

        // set flag to indicate we have started a search. Need to do this here
        // to avoid this call returning and processing results before the thread
        // itself has set this flag
        stillSearching(true);
        // start the thread running
        start();

    }

    /**
     * get the query that will be executed when this ActiveBeans thread is run
     *
     * @return The query to be executed
     */
    public Object getQuery()
    {
        return query;
    }

    /**
     * Set the record schema to use when prepoulating cache
     *
     * @param recordSchemamThe schema to prepopulate the cache with
     */
    protected void setPrepoulateCacheRecordSchema(String recordSchema)
    {
        this.cacheRecordSchema = recordSchema;
    }

    /**
     * Get the record schema to use when prepoulating cache
     *
     * @return The schema for prepoulating the cache
     */
    protected String getPrepoulateCacheRecordSchema()
    {
        return cacheRecordSchema;
    }

    /**
     * Get the number of results that were found the last time the ActiveBeans
     * thread was executed
     *
     * @return The number of results found
     */
    public int getNumberOfResults()
    {
        // make sure the bean is configured else retun no results
        if (this.databean != null)
        {
            return ((Search) this.databean).getNumberOfResults();
        }
        return 0;
    }

    /**
     * Retrieve the record at the specified recordIndex
     *
     * @param recordIndex The recordIndex to retrieve
     * @param schema The schema to apply
     * @return The retrieved record
     * @throws JaferException
     */
    public Field getRecord(int recordIndex, String schema) throws JaferException
    {
        // make sure a schema is entered
        if (schema == null || schema.length() == 0)
        {
            throw new JaferException("Record Schema must be set to retrieve a record");
        }
        // get the record applying the offsets
        return getRecord(recordIndex, schema, true);
    }

    /**
     * Retrieve the record at the specified recordIndex
     *
     * @param recordIndex The recordIndex to retrieve
     * @param schema The schema to apply
     * @param applyOffsets Should the offsets be applied to the record index,
     *        This is normally true apart from when the autopopulation of cache
     *        needs to refer to local indexs
     * @return The retrieved record
     * @throws JaferException
     */
    protected synchronized Field getRecord(int recordIndex, String schema, boolean applyOffsets) throws JaferException
    {
        // make sure the bean is configured
        if (this.databean != null)
        {
            // make sure this ActiveBean contains the record when we are
            // applying offsets
            if (applyOffsets && containsRecord(recordIndex) == false)
            {
                throw new RecordException("The record is not contained by this ActiveBean");
            }

            // set the record schema and cursor position for retreival
            // applying the offsets to obtain the position in just this
            // activeBeans result set
            ((Present) this.databean).setRecordSchema(schema);

            // set the record cursor applying off sets if required
            if (applyOffsets)
            {
                // apply offsets
                ((Present) this.databean).setRecordCursor(recordIndex - this.startOffset + 1);
            }
            else
            {
                // use straight record index
                ((Present) this.databean).setRecordCursor(recordIndex);
            }

            // return the current record
            return ((Present) this.databean).getCurrentRecord();

        }
        throw new RecordException("The databean for this ActiveBean is not configured");
    }

    /**
     * This method checks if the search is still executing
     *
     * @return Returns the false if the search has completed.
     */
    public boolean stillSearching()
    {
        return stillSearching;
    }

    /**
     * This method sets the flag to indicate if the search is executing or not
     *
     * @param searchStatus The search status - true if executing search
     */
    protected void stillSearching(boolean searchStatus)
    {
        stillSearching = searchStatus;
    }

    /**
     * Returns whether this active bean should prepopulate cache after search
     *
     * @return Returns true is auto population of cache should take place
     */
    public boolean autoPopulateCache()
    {
        return autoPopulateCache;
    }

    /**
     * Sets whether this active bean should prepopulate cache after search
     *
     * @param autoPopulate true if the cache should be auto populated
     */
    public void setAutoPopulateCache(boolean autoPopulate)
    {
        this.autoPopulateCache = autoPopulate;
    }

    /**
     * This method allows the caller to set the stop active bean search flag
     */
    public void stopActiveBeanSearch(boolean stopStatus)
    {
        this.stopActiveBeanSearch = stopStatus;
    }

    /**
     * Checks to see if the search should be stopped premeturely
     *
     * @return true if search process should terminate early
     */
    protected boolean stopActiveBeanSearch()
    {
        return this.stopActiveBeanSearch;
    }

    /**
     * Executes the thread to perform the search
     */
    public void run()
    {
        try
        {
            // stores the number of results found
            int results = 0;
            logger.fine("Starting search on thread - " + getName());
            // reset the stop flag
            this.stopActiveBeanSearch = false;
            // set flag to indicate we have started a search
            stillSearching(true);
            // ensure last search exception is nulled out
            setSearchException(null);
            // set the offsets back to 0
            setOffsets(0, 0);
            // make sure the bean is configured to execute search
            if (this.databean != null)
            {
                // perform the search
                results = ((Search) this.databean).submitQuery(this.query);

                logger.fine("Search on " + this.getName() + " found " + Integer.toString(results)
                        + " results, bean also reports " + ((Search) this.databean).getNumberOfResults());
            }
            // set flag to indicate we have completed search part of thread
            stillSearching(false);
            logger.fine("search completed on thread - " + getName());
            // if prefilling of the cache is enabled start this process
            if (autoPopulateCache())
            {
                // make sure we have a schema set on the active bean
                if (getPrepoulateCacheRecordSchema() == null || getPrepoulateCacheRecordSchema().length() == 0)
                {
                    throw new JaferException("Record Schema must be set to search when prepopulating of the cache is enabled");
                }

                logger.fine("Cache free slots: " + ((Cache) databean).getAvailableSlots() + " on thread - " + getName());

                Cache cacheBean = (Cache) databean;
                // fill the cache with records until:
                // * all the result records have been processed,
                // * the user prematurely stops the process,
                // * the cache runs out of available slots and is therefore full
                // * either the available slots are less than the fetch size
                // (as performing another retrieve would wipe out the first x
                // records) or there are less records than the fetch size to be
                // added and they are greater than the available slotsrecords.
                // <br>The index is moved on by the fetch size as retreiving one
                // record will auto retrieve the next X records
                for (int index = 1; index <= results
                        && stopActiveBeanSearch == false
                        && cacheBean.getAvailableSlots() > 0
                        && (cacheBean.getAvailableSlots() >= cacheBean.getFetchSize() || cacheBean.getAvailableSlots() >= results
                                + 1 - index); index += cacheBean.getFetchSize())
                {
                    logger.fine("Caching Record " + index + " on thread - " + getName());
                    // get the record which will automatically add it into the
                    // cache if it does not already exist, set apply offsets to
                    // false as this is already a local index
                    getRecord(index, getPrepoulateCacheRecordSchema(), false);
                }
            }
        }
        catch (JaferException exc)
        {
            // something went wrong searching this bean so output problem as a
            // warning as it may just be that the client is not on-line at
            // present
            String message = "Jafer Exception in databeanManager ActiveBean( " + this.getName() + ") performing search: "
                    + exc.toString();
            // set the exception so it can be retrieved later
            setSearchException(exc);
            logger.warning(message);
            exc.printStackTrace();
        }
        catch (Exception exc)
        {
            // something went wrong searching this bean so output problem as a
            // warning as it may just be that the client is not on-line at
            // present
            String message = "Unexpected Exception in databeanManager ActiveBean( " + this.getName() + ") performing search: "
                    + exc.toString();
            // set the exception so it can be retrieved later
            setSearchException(new JaferException(message, exc));
            logger.warning(message);
            exc.printStackTrace();
        }
        finally
        {
            logger.fine("stopping search on thread - " + getName());
            // set flag to indicate we have completed search part of thread
            stillSearching(false);
        }
    }
}

/**
 * This class manages a collection of databases
 */
public class DatabeanManager extends Databean implements Present, Search
{

    /**
     * Stores a reference to factories that can create databeans for specified
     * databases. A map entry consists of key = database name and value =
     * factory that creates a databean supporting Search and Present for the
     * specified database
     */
    private Hashtable databeanFactories;

    /**
     * Stores a reference to cache factory that provides the cache for the
     * ActiveBeans data bean to use. If this is set to null then the cache will
     * not be set on the databean and the databeans internal one will be used
     */
    private CacheFactory cacheFactory = null;

    /**
     * Stores a reference to whether pre filling of the caching is enabled or
     * not. Enabled (true) as default.
     */
    private boolean autoPopulateCache = false;

    /**
     * Stores a reference to an array of active beans forthe current set of
     * databases
     */
    private ActiveBean[] activeBeans;

    /**
     * Stores a reference to current record cursor position
     */
    private int recordCursor;

    /**
     * Stores a reference to record schema that should be applied when
     * retrieving records
     */
    private String recordSchema;

    /**
     * Stores a reference to search mode. <br>
     * <br>
     * <ul>
     * <li>serial - The first active bean to return a result set will be used,
     * ignoring all other activebean results</li>
     * <li>parallel - All ActiveBeans results will be combined to provide a
     * super result set (DEFAULT)</li>
     * </ul>
     */
    private String mode;

    /**
     * Stores a reference to the total number of records retrieved
     */
    private int totalRecords = 0;

    /**
     * Stores a reference to the name of this databeanmanager
     */
    private String name;

    /**
     * Stores a reference to this databeanmanagers complete set of configured
     * databases
     */
    private String[] allDatabases;

    /**
     * Set the databean factories that this databeanmanager supports
     *
     * @param databeanFactories A map where key = database name and value =
     *        factory that creates a databean supporting Search and Present for
     *        the specified database
     */
    public void setDatabeanFactories(Hashtable databeanFactories)
    {

        // avoid case sensitive database names by converting keys to lowercase
        this.databeanFactories = new Hashtable();
        // loop round all the keys
        Enumeration en = databeanFactories.keys();
        while (en.hasMoreElements())
        {
            // get the key and value for the databean factory
            String database = ((String) en.nextElement());
            Object value = databeanFactories.get(database);
            // add to internal databean factories map turning database name to
            // lower case
            this.databeanFactories.put(database.toLowerCase(), value);
        }
    }

    /**
     * Returns a map of the supported databean factories
     *
     * @return the supported databean factories map
     */
    public Map getDatabeanFactories()
    {
        return databeanFactories;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Present#setRecordCursor(int)
     */
    public void setRecordCursor(int recordCursor) throws JaferException
    {
        this.recordCursor = recordCursor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Present#getRecordCursor()
     */
    public int getRecordCursor()
    {
        return recordCursor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Present#setRecordSchema(java.lang.String)
     */
    public void setRecordSchema(String recordSchema)
    {
        this.recordSchema = recordSchema;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Present#getRecordSchema()
     */
    public String getRecordSchema()
    {
        return recordSchema;
    }

    /**
     * Returns whether the active beans should prepopulate cache after search
     *
     * @return Returns true is auto population of cache should take place
     */
    public boolean autoPopulateCache()
    {
        return autoPopulateCache;
    }

    /**
     * Sets whether the active beans should prepopulate cache after search
     *
     * @param autoPopulate true if the cache should be auto populated
     */
    public void setAutoPopulateCache(boolean autoPopulate)
    {
        this.autoPopulateCache = autoPopulate;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Search#setDatabases(java.lang.String)
     */
    public void setDatabases(String database)
    {
        // make sure a database is specified. If the DatabeanManagerFactory is
        // not set up correctly that could mean null being passed here
        if (database != null)
        {
            this.setDatabases(new String[] { database });
        }
        else
        {
            // reset active beans back to an empty array so search will return
            // nothing if it is executed rather than the last set of databases
            // results
            activeBeans = new ActiveBean[0];
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Search#setDatabases(java.lang.String[])
     */
    public void setDatabases(String[] databases)
    {
        // reset active beans back to an empty array so search will return
        // nothing if it is executed rather than the last set of databases
        // results
        activeBeans = new ActiveBean[0];

        // Make sure we have databases to set
        if (databases == null || databases.length == 0)
        {
            return;
        }

        // if the first database name equals the name of this database then use
        // it's configured set of databases if they are set and not null
        if (databases[0].equalsIgnoreCase(this.getName()) && this.allDatabases != null)
        {
            databases = this.allDatabases;
        }

        // create a new array of active beans for the new set of databases to
        // search now we know we definitly have some to set
        activeBeans = new ActiveBean[databases.length];
        // for each database create the active bean into the array
        for (int index = 0; index < databases.length; index++)
        {
            // convert the database name supplied to lower case for searching
            // databeanfactories
            String database = databases[index].toLowerCase();
            if (databeanFactories.containsKey(database))
            {
                // get the datbean factory for the database and create a new
                // databean
                DatabeanFactory factory = (DatabeanFactory) databeanFactories.get(database);
                Databean databean = factory.getDatabean();

                // set the cache on the databean if we have a cache factory
                // defined inside this databeanmanager
                if (getCacheFactory() != null)
                {
                    // convert the databean to a cache interface to set the
                    // cache from the factory
                    ((Cache) databean).setCache(getCacheFactory().getCache());
                }

                // create an active bean and set its databean with the one
                // obtained from the factory
                activeBeans[index] = new ActiveBean();
                activeBeans[index].setDatabean(databean);
                // name the activebean thread after the database it searches
                activeBeans[index].setName(database);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Search#getDatabases()
     */
    public String[] getDatabases()
    {
        // create a string array to return all the names of the databases
        // currently active
        String[] databases = new String[activeBeans.length];
        for (int index = 0; index < activeBeans.length; index++)
        {
            // make sure the activebean was configured correctly
            if (activeBeans[index] != null)
            {
                // store the name of the active bean into databases return
                // result.
                databases[index] = activeBeans[index].getName();
            }
            else
            {
                // active bean was configured incorrectly so set database name
                // to error
                databases[index] = "ERROR - NOT FOUND";
            }
        }
        return databases;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Search#submitQuery(java.lang.Object)
     */
    public int submitQuery(Object query) throws JaferException
    {
        // make sure we have active beans to search
        if (activeBeans.length == 0)
        {
            // throw error as trying to search no databases
            throw new JaferException("Unable to search - No databases were found", 235, "");
        }
        // make sure record schema not null to search with auto populate caching
        // on
        if (getRecordSchema() == null && autoPopulateCache())
        {
            throw new JaferException("Record Schema must be set to search when prepopulating of the cache is enabled");
        }

        // set the total records found back to 0
        totalRecords = 0;
        // loop round each activebean
        for (int index = 0; index < activeBeans.length; index++)
        {
            // make sure the active bean is valid and has its data bean set
            if (activeBeans[index] == null || activeBeans[index].getDatabean() == null)
            {
                throw new JaferException("Unable to search - Specified database was not found", 235, "");
            }
            // tell the bean if it should auto populate
            activeBeans[index].setAutoPopulateCache(autoPopulateCache());
            // set the query on the active bean and start the search thread
            activeBeans[index].submitQuery(query, getRecordSchema());
        }

        // set current record to find to 1;
        int currentRecord = 1;

        // loop round each active bean
        for (int index = 0; index < activeBeans.length; index++)
        {
            // wait until the active bean has completed it's search by checking
            // if it is still searching. Can not check thread alive status as
            // ActiveBean may have started to cache data
            while (activeBeans[index].stillSearching())
            {
                // yield this thread to give active bean chance to execute
                Thread.yield();
            }

            // bean has now completed search so see how many records were found
            int recordsFound = activeBeans[index].getNumberOfResults();

            // did we find any records?
            if (recordsFound > 0)
            {
                // yes records were found so we need to set the offsets for the
                // active bean
                // Hence it should start at the current record and end at the
                // current record + the records found -1
                activeBeans[index].setOffsets(currentRecord, currentRecord + recordsFound - 1);
                // update the current record to add on the number of records
                // found so the offset is increase for the next active bean
                // processed
                currentRecord += recordsFound;
                totalRecords += recordsFound;
                // check if we are running mode serial
                if (mode.equalsIgnoreCase(DatabeanManagerFactory.MODE_SERIAL))
                {
                    // yes so break the entire loop as we no longer need to
                    // process any more active beans as we are only going to
                    // return the result set from this active bean
                    break;
                }
            }
        }
        // return the toal number of records found
        return totalRecords;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Search#getNumberOfResults()
     */
    public int getNumberOfResults()
    {
        return totalRecords;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Search#getNumberOfResults(java.lang.String)
     */
    public int getNumberOfResults(String databaseName)
    {
        // loop round active beans
        for (int index = 0; index < activeBeans.length; index++)
        {
            // make sure active bean is configured
            if (activeBeans[index] != null)
            {
                // does the name of the database match the name of the active
                // bean
                if (activeBeans[index].getName().equalsIgnoreCase(databaseName))
                {
                    return activeBeans[index].getNumberOfResults();
                }
            }
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Present#getCurrentRecord()
     */
    public Field getCurrentRecord() throws org.jafer.exception.JaferException
    {
        // loop round active beans
        for (int index = 0; index < activeBeans.length; index++)
        {
            // make sure active bean is configured
            if (activeBeans[index] != null)
            {
                // check to see if this active bean contains the record
                if (activeBeans[index].containsRecord(this.recordCursor))
                {
                    // return the record
                    return activeBeans[index].getRecord(this.recordCursor, this.recordSchema);
                }
            }
        }
        throw new JaferException("Record Cursor Out of Range");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Present#getCurrentDatabase()
     */
    public String getCurrentDatabase() throws JaferException
    {
        // loop round active beans
        for (int index = 0; index < activeBeans.length; index++)
        {
            // make sure active bean is configured
            if (activeBeans[index] != null)
            {
                // does this active bean contain the record
                if (activeBeans[index].containsRecord(this.recordCursor))
                {
                    // return the beans database name
                    return activeBeans[index].getName();
                }
            }
        }
        throw new JaferException("Record Cursor Out of Range");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Present#setCheckRecordFormat(boolean)
     */
    public void setCheckRecordFormat(boolean checkRecordFormat)
    {
        /** @todo Implement this org.jafer.interfaces.Present method */
        throw new java.lang.UnsupportedOperationException("Method setCheckRecordFormat() not yet implemented.");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Present#isCheckRecordFormat()
     */
    public boolean isCheckRecordFormat()
    {
        /** @todo Implement this org.jafer.interfaces.Present method */
        throw new java.lang.UnsupportedOperationException("Method isCheckRecordFormat() not yet implemented.");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Present#setElementSpec(java.lang.String)
     */
    public void setElementSpec(String elementSpec)
    {
        /** @todo Implement this org.jafer.interfaces.Present method */
        throw new java.lang.UnsupportedOperationException("Method setElementSpec() not yet implemented.");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Present#getElementSpec()
     */
    public String getElementSpec()
    {
        /** @todo Implement this org.jafer.interfaces.Present method */
        throw new java.lang.UnsupportedOperationException("Method getElementSpec() not yet implemented.");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Search#setResultSetName(java.lang.String)
     */
    public void setResultSetName(String resultSetName)
    {
        /** @todo Implement this org.jafer.interfaces.Search method */
        throw new java.lang.UnsupportedOperationException("Method setResultSetName() not yet implemented.");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Search#getResultSetName()
     */
    public String getResultSetName()
    {
        /** @todo Implement this org.jafer.interfaces.Search method */
        throw new java.lang.UnsupportedOperationException("Method getResultSetName() not yet implemented.");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Search#setParseQuery(boolean)
     */
    public void setParseQuery(boolean parseQuery)
    {
        /** @todo Implement this org.jafer.interfaces.Search method */
        throw new java.lang.UnsupportedOperationException("Method setParseQuery() not yet implemented.");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Search#isParseQuery()
     */
    public boolean isParseQuery()
    {
        /** @todo Implement this org.jafer.interfaces.Search method */
        throw new java.lang.UnsupportedOperationException("Method isParseQuery() not yet implemented.");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Search#saveQuery(java.lang.String)
     */
    public void saveQuery(String file) throws JaferException
    {
        /** @todo Implement this org.jafer.interfaces.Search method */
        throw new java.lang.UnsupportedOperationException("Method saveQuery() not yet implemented.");
    }

    /**
     * Currently Not Supported
     *
     * @see org.jafer.interfaces.Search#getQuery()
     */
    public Object getQuery()
    {
        /** @todo Implement this org.jafer.interfaces.Search method */
        throw new java.lang.UnsupportedOperationException("Method getQuery() not yet implemented.");
    }

    /**
     * Set the mode that this databeanManager runs in
     *
     * @param mode The mode to set use statics in DatabeanManagerFactory
     */
    public void setMode(String mode)
    {
        this.mode = mode;
    }

    /**
     * get the mode that the DatabeanManager is running in
     *
     * @return The current mode Serial or Parallel
     */
    public String getMode()
    {
        return mode;
    }

    /**
     * Set the name of this DataBeanManager
     *
     * @param name The name of the databean manager
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * get the name of the DataBeanManager
     *
     * @return the name of the databean manager
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the databeanManagers set of all databases it processes. All the
     * databases must be configured in the databeanfactories for the
     * databeanManager
     *
     * @param allDatabases the array of database names
     */
    public void setAllDatabases(String[] allDatabases)
    {
        this.allDatabases = allDatabases;
    }

    /**
     * Get the databeanManagers set of all databases it processes
     *
     * @return An array of all the database names
     */
    public String[] getAllDatabases()
    {
        return allDatabases;
    }

    /**
     * Sets the cache factory to be used by the DatabeanManager
     *
     * @param cacheFactory the cache factory to use
     */
    public void setCacheFactory(CacheFactory cacheFactory)
    {
        this.cacheFactory = cacheFactory;
    }

    /**
     * Returns the cache factory used by this databeanManager
     *
     * @return The cache factory used
     */
    public CacheFactory getCacheFactory()
    {
        return cacheFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Search#getSearchDiagnostic(java.lang.String)
     */
    public JaferException getSearchException(String database)
    {
        // make sure database name specified
        if (database != null)
        {
            // get the JaferException for the database, empty arry if no errors
            // found
            JaferException[] errors = getSearchException(new String[] { database });
            if (errors.length != 0)
            {
                // return the first JaferException
                return errors[0];
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jafer.interfaces.Search#getSearchDiagnostics(java.lang.String[])
     */
    public JaferException[] getSearchException(String[] databases)
    {
        // create empty array for success condition
        ArrayList errors = new ArrayList();

        // Make sure we have databases to set
        if (databases == null || databases.length == 0)
        {
            // return an empty array
            return new JaferException[0];
        }

        // if the first database name equals the name of this database then use
        // it's configured set of databases if they are set and not null
        if (databases[0].equalsIgnoreCase(this.getName()) && this.allDatabases != null)
        {
            databases = this.allDatabases;
        }

        // loop round active beans
        for (int index = 0; index < databases.length; index++)
        {
            // loop round active beans
            for (int activeBeanIndex = 0; activeBeanIndex < activeBeans.length; activeBeanIndex++)
            {
                // make sure active bean is configured
                if (activeBeans[activeBeanIndex] != null)
                {
                    // does this active bean match the database name
                    if (databases[index] != null && activeBeans[activeBeanIndex].getName().equalsIgnoreCase(databases[index]))
                    {
                        errors.add(activeBeans[activeBeanIndex].getSearchException());
                    }
                }
            }
        }
        // convert the array list back to an Array
        return (JaferException[]) errors.toArray(new JaferException[databases.length]);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable
    {
        // before the DatabeanManager can be cleaned up it needs to stop any
        // active bean
        // threads that may still be running
        for (int activeBeanIndex = 0; activeBeanIndex < activeBeans.length; activeBeanIndex++)
        {
            // make sure active bean is configured
            if (activeBeans[activeBeanIndex] != null)
            {
                // tell the bean to stop hence cleaning up correctly
                activeBeans[activeBeanIndex].stopActiveBeanSearch(true);
                // loop while the active bean still alive
                while (activeBeans[activeBeanIndex].isAlive())
                {
                    // yield this thread to give active bean chance to execute
                    Thread.yield();
                }
            }
        }
        super.finalize();
    }
}
