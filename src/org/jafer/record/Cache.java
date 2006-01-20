package org.jafer.record;

import org.w3c.dom.Document;
import org.jafer.exception.JaferException;
import org.w3c.dom.Node;

public interface Cache {
  public void clear();

  public boolean contains(Integer recNo);

  public Object getBER(Document document, String schema, Integer recNo) throws
      JaferException;

  public DataObject getDataObject(Integer recNo) throws JaferException;

  public Node getXML(Document document, String targetSchema, Integer recNo) throws
      JaferException;

  public void put(Integer recNo, DataObject dataObject);
  
  public int availableSlots();
  
  public int getDataCacheSize();
}
