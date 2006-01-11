package org.jafer.record;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.jafer.exception.JaferException;
import java.util.Map;
import java.util.logging.Logger;
import java.util.TreeMap;
import java.util.logging.Level;

public class AbstractCache {

  protected Map xmlCache;
  protected Map berCache;
  protected Map dataCache;
  protected int dataCacheSize;
  protected static Logger logger = Logger.getLogger("org.jafer.zclient");

  protected RecordFactory recordFactory;
  protected TreeMap dataTimeStamp;
  public double clear = 0.2; // allow user to set this ?

  private AbstractCache() {
  }

  protected AbstractCache(int dataCacheSize) {
    this.dataCacheSize = dataCacheSize;
    this.recordFactory = new RecordFactory();
    dataTimeStamp = new TreeMap();
  }


  public Node getXML(Document document, String targetSchema, Integer recNo) throws
      JaferException {

    if (contains(recNo)) {
      if (!xmlCache.containsKey(recNo)) {
        DataObject dataObject = (DataObject) dataCache.get(recNo);
        Node recordRoot = (Node) recordFactory.getXML(dataObject, document,
            targetSchema, recNo.intValue());
        recordRoot.normalize();
        xmlCache.put(recNo, recordRoot);
      }
      //  need to clone node so that contents of cache not affected by
      //  modifications or results of appending this node to others
      return ( (Node) xmlCache.get(recNo)).cloneNode(true);

    }
    else {
      String message = "Cache, Record (XML) not found - record number: " +
          recNo;
      JaferException exception = new JaferException(message);
      logger.log(Level.SEVERE, message, exception);
      throw exception;
    }
  }

  public Object getBER(Document document, String schema, Integer recNo) throws
      JaferException {

    if (contains(recNo)) {
      if (!berCache.containsKey(recNo)) {
        DataObject dataObject = (DataObject) dataCache.get(recNo);
        Object ber = recordFactory.getBER(dataObject, document, recNo.intValue());
        berCache.put(recNo, ber);
      }
      return berCache.get(recNo);

    }
    else {
      String message = "Cache, Record (BER) not found - record number: " +
          recNo;
      JaferException exception = new JaferException(message);
      logger.log(Level.SEVERE, message, exception);
      throw exception;
    }
  }

  public void clear() {

    if (dataCache != null) {
      dataCache.clear();
    }
    if (dataTimeStamp != null) {
      dataTimeStamp.clear();
    }
    if (xmlCache != null) {
      xmlCache.clear();
    }
    if (berCache != null) {
      berCache.clear();
    }
  }

  public DataObject getDataObject(Integer recNo) throws JaferException {

    if (dataCache.containsKey(recNo)) {
      return (DataObject) dataCache.get(recNo);
    }
    else {
      String message = "Cache, DataObject not found - record number: " + recNo;
      JaferException exception = new JaferException(message);
      logger.log(Level.SEVERE, message, exception);
      throw exception;
    }
  }

  public boolean contains(Integer recNo) {

    if (recNo == null) {
      return false;
    }
    return dataCache.containsKey(recNo);
  }

  public void put(Integer recNo, DataObject dataObject) {

    if (!contains(recNo)) {
      checkCacheSize(dataCacheSize);
      dataTimeStamp.put(new Long(System.currentTimeMillis() + recNo.longValue()),
                        recNo);
      dataCache.put(recNo, dataObject);
    }
  }

  protected void checkCacheSize(int size) {

    if (dataCache.size() + 1 > size) {
      for (int i = 0; i < size * clear; i++) { // remove % of cache
        Integer recNo = (Integer) dataTimeStamp.remove(dataTimeStamp.firstKey());
        dataCache.remove(recNo);
        xmlCache.remove(recNo);
        berCache.remove(recNo);
      }
    }
  }
}
