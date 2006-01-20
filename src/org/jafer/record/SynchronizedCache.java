package org.jafer.record;

import org.jafer.exception.JaferException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SynchronizedCache implements Cache {
    Cache cache;

    public SynchronizedCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * clear
     *
     * @todo Implement this org.jafer.record.Cache method
     */
    public synchronized void clear() {
        cache.clear();
    }

    /**
     * contains
     *
     * @param recNo Integer
     * @return boolean
     * @todo Implement this org.jafer.record.Cache method
     */
    public synchronized boolean contains(Integer recNo) {
        return cache.contains(recNo);
    }

    /**
     * getBER
     *
     * @param document Document
     * @param schema String
     * @param recNo Integer
     * @return Object
     * @throws JaferException
     * @todo Implement this org.jafer.record.Cache method
     */
    public synchronized Object getBER(Document document, String schema, Integer recNo) throws
            JaferException {
        return cache.getBER(document, schema, recNo);
    }

    /**
     * getDataObject
     *
     * @param recNo Integer
     * @return DataObject
     * @throws JaferException
     * @todo Implement this org.jafer.record.Cache method
     */
    public synchronized DataObject getDataObject(Integer recNo) throws JaferException {
        return cache.getDataObject(recNo);
    }

    /**
     * getXML
     *
     * @param document Document
     * @param targetSchema String
     * @param recNo Integer
     * @return Node
     * @throws JaferException
     * @todo Implement this org.jafer.record.Cache method
     */
    public synchronized Node getXML(Document document, String targetSchema, Integer recNo) throws
            JaferException {
        return cache.getXML(document, targetSchema, recNo);
    }

    /**
     * put
     *
     * @param recNo Integer
     * @param dataObject DataObject
     * @todo Implement this org.jafer.record.Cache method
     */
    public synchronized void put(Integer recNo, DataObject dataObject) {
        cache.put(recNo, dataObject);
    }

    /**
     * Returns the number of available slots currently in the cache
     * 
     * @return The number of currently availiable slots
     */
    public synchronized int getAvailableSlots()
    {
        return cache.getAvailableSlots();
    }
    
    /**
     * get the data cache size
     * 
     * @return The size of the data cache
     */
    public int getDataCacheSize()
    {
        return cache.getDataCacheSize();
    }

}
