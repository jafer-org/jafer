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
package org.jafer.record;

/**
 * This class returns a new instance of a HashtableCache from the configured
 * settings
 */
public class HashtableCacheFactory implements CacheFactory
{

    /**
     *  Stores a reference to DEFAULT_DATACACHE_SIZE of 512
     */
    public final static int DEFAULT_DATACACHE_SIZE = 512;

    /**
     * Stores a reference to data cache size
     */
    private int dataCacheSize;

    /**
     * default constructor
     */
    public HashtableCacheFactory()
    {
        dataCacheSize = DEFAULT_DATACACHE_SIZE;
    }
    
    /**
     * constructor supplying cache size
     * @param dataCacheSize The size of the cache normally this would be 512
     */
    public HashtableCacheFactory(int dataCacheSize)
    {
        this.dataCacheSize = dataCacheSize;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.record.CacheFactory#getCache()
     */
    public Cache getCache()
    {
        return new HashtableCache(getDataCacheSize());
    }

     /**
     * Gets the DataCacheSize attribute
     * 
     * @return The DataCacheSize value
     */
    public int getDataCacheSize()
    {
        return dataCacheSize;
    }

    /**
     * Sets the DataCacheSize attribute
     * 
     * @param dataCacheSize The new DataCacheSize value
     */
    public void setDataCacheSize(int dataCacheSize)
    {
        this.dataCacheSize = dataCacheSize;
    }

}
