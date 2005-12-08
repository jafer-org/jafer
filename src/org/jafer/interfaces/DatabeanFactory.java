package org.jafer.interfaces;

import java.rmi.RemoteException;
import org.jafer.util.CompressedXMLDecoder;
import java.net.URL;
import java.io.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public abstract class DatabeanFactory implements Serializable {
  private String name;

  public abstract Databean getDatabean();

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }

  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
  }

  public static DatabeanFactory load(URL config) {
    try {
      CompressedXMLDecoder decoder = new CompressedXMLDecoder(config.openStream());
      return (DatabeanFactory)decoder.readObject();
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
}