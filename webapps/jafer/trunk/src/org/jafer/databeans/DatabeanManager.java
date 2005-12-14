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

import org.jafer.interfaces.Databean;
import org.jafer.interfaces.DatabeanFactory;
import org.jafer.exception.JaferException;
import org.jafer.interfaces.Present;
import org.jafer.interfaces.Search;
import org.jafer.record.Field;
import org.jafer.util.xml.DOMFactory;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.Document;


class ActiveBean extends java.lang.Thread {
  private int first;
  private int last;
  private Databean bean;
  private Object query;

  private Logger logger = Logger.getLogger("org.jafer.databeans");

  public void setLimits(int first, int last) {
    this.first = first;
    this.last = last;
  }

   public boolean containsRecord(int rec) {
    if (rec >= first && rec <= last)
      return true;
    else
      return false;
  }

  public Databean getDatabean() {
    return bean;
  }

  public void setDatabean(Databean bean) {
    this.bean = bean;
  }

  public void setQuery(Object query) {

    if (query instanceof Node)
      this.query = DOMFactory.newDocument().importNode((Node)query, true);
    else
      this.query = query;
  }

  public Object getQuery() {
    return query;
  }

  public int getNumberOfResults(){

    return ((Search)this.bean).getNumberOfResults();
  }

  public Field getRecord(int rec, String schema) throws JaferException {
    ((Present)this.bean).setRecordSchema(schema);
    ((Present)this.bean).setRecordCursor(rec - this.first + 1);
    return ((Present)this.bean).getCurrentRecord();
  }


  public void run() {
    try {
      this.first = 0;
      this.last = 0;
      int results = ((Search)this.bean).submitQuery(this.query);
      logger.log(Level.FINE, "Search on " + this.getName() + " found " + Integer.toString(results) + " sanity check " + ((Search)this.bean).getNumberOfResults());

    } catch (Exception ex) {
      System.out.println("Exception in databeanManager: " + ex.toString());
      ex.printStackTrace();
      /** @todo: Catch this
       */
    }
  }
}

public class DatabeanManager extends Databean implements Present, Search {

  private Hashtable databeanFactories;
  private ActiveBean[] activeBeans;

  private int recordCursor;
  private String recordSchema;
  private String mode;

  private Logger logger = Logger.getLogger("org.jafer.databeans");

  public void setDatabeanFactories(Hashtable databeanFactories) {

    //avoid case sensitive database names
    this.databeanFactories = new Hashtable();
    Enumeration en = databeanFactories.keys();
    while (en.hasMoreElements()) {
      String key = ((String)en.nextElement());
      Object value = databeanFactories.get(key);
      this.databeanFactories.put(key.toLowerCase(), value);
    }
  }

  public Hashtable getDatabeanFactories() {
    return databeanFactories;
  }

  public void setRecordCursor(int recordCursor) throws JaferException {
    this.recordCursor = recordCursor;
  }

  public int getRecordCursor() {
    return recordCursor;
  }

  public void setRecordSchema(String recordSchema) {
    this.recordSchema = recordSchema;
  }

  public String getRecordSchema() {
    return recordSchema;
  }

  public void setDatabases(String database) {

    this.setDatabases(new String[]{database});
  }

  public void setDatabases(String[] databases) {

    //avoid case sensitive database names
    if (databases[0].equalsIgnoreCase(this.getName())) {
      databases = this.allDatabases;
    }

    activeBeans = new ActiveBean[databases.length];
    for (int n=0; n<databases.length; n++) {
      String database = databases[n].toLowerCase();
      if (databeanFactories.containsKey(database)) {
        activeBeans[n] = new ActiveBean();
        DatabeanFactory factory = (DatabeanFactory)databeanFactories.get(database);
        activeBeans[n].setDatabean(factory.getDatabean());
        activeBeans[n].setName(database);
      }
    }
  }

  public String[] getDatabases() {

    String[] databases = new String[activeBeans.length];
    for (int n=0; n<activeBeans.length; n++) {
      databases[n] = activeBeans[n].getName();
    }
    return databases;
  }

  private int totalRecords = 0;
  private String name;
  private String[] allDatabases;

  public int submitQuery(Object query) throws JaferException {

    totalRecords = 0;
    for (int n=0; n<activeBeans.length; n++) {
      if (activeBeans[n] == null || activeBeans[n].getDatabean() == null)
        throw new JaferException("specified database not found", 235, "");/** @todo addInfo... */
        activeBeans[n].setQuery(query);
        activeBeans[n].start();
    }

    int currentRecord = 1;

    for (int n=0; n<activeBeans.length; n++) {
      while (activeBeans[n].isAlive()) {
	try {
	  Thread.sleep(1000);
	} catch (Exception ex) {
	  ex.printStackTrace();
	}
      }
      int rec = ((Search)activeBeans[n].getDatabean()).getNumberOfResults();

      if (rec > 0) {
	activeBeans[n].setLimits(currentRecord, currentRecord + rec - 1);
	currentRecord += rec;
	totalRecords += rec;
	if (mode.equalsIgnoreCase("serial")) {
	  break;
	}
      }
    }

    return totalRecords;
  }

  public int getNumberOfResults() {
    return totalRecords;
  }

  public int getNumberOfResults(String databaseName) {

    for (int n=0; n<activeBeans.length; n++) {
      if (activeBeans[n].getDatabean() != null) {
	if (activeBeans[n].getName().equalsIgnoreCase(databaseName))
	  return activeBeans[n].getNumberOfResults();
      }
    }
    return -1;/** @todo mention this in javadoc... */
  }

  public Field getCurrentRecord() throws org.jafer.exception.JaferException {
    for (int n=0; n<activeBeans.length; n++) {
      if (activeBeans[n].getDatabean() != null) {
        if (activeBeans[n].containsRecord(this.recordCursor)) {
          return activeBeans[n].getRecord(this.recordCursor, this.recordSchema);
        }
      }
    }

    throw new JaferException("Out of range thingy!!");
  }

  public String getCurrentDatabase() throws JaferException {
    for (int n=0; n<activeBeans.length; n++) {
      if (activeBeans[n].getDatabean() != null) {
        if (activeBeans[n].containsRecord(this.recordCursor)) {
          return activeBeans[n].getName();
        }
      }
    }

    throw new JaferException("Out of range thingy!!");
  }

  public void setCheckRecordFormat(boolean checkRecordFormat) {
    /**@todo Implement this org.jafer.interfaces.Present method*/
    throw new java.lang.UnsupportedOperationException("Method setCheckRecordFormat() not yet implemented.");
  }
  public boolean isCheckRecordFormat() {
    /**@todo Implement this org.jafer.interfaces.Present method*/
    throw new java.lang.UnsupportedOperationException("Method isCheckRecordFormat() not yet implemented.");
  }
  public void setElementSpec(String elementSpec) {
    /**@todo Implement this org.jafer.interfaces.Present method*/
    throw new java.lang.UnsupportedOperationException("Method setElementSpec() not yet implemented.");
  }
  public String getElementSpec() {
    /**@todo Implement this org.jafer.interfaces.Present method*/
    throw new java.lang.UnsupportedOperationException("Method getElementSpec() not yet implemented.");
  }
  public void setResultSetName(String resultSetName) {
    /**@todo Implement this org.jafer.interfaces.Search method*/
    throw new java.lang.UnsupportedOperationException("Method setResultSetName() not yet implemented.");
  }
  public String getResultSetName() {
    /**@todo Implement this org.jafer.interfaces.Search method*/
    throw new java.lang.UnsupportedOperationException("Method getResultSetName() not yet implemented.");
  }
  public void setSearchProfile(String searchProfile) {
    /**@todo Implement this org.jafer.interfaces.Search method*/
    throw new java.lang.UnsupportedOperationException("Method setSearchProfile() not yet implemented.");
  }
  public String getSearchProfile() {
    /**@todo Implement this org.jafer.interfaces.Search method*/
    throw new java.lang.UnsupportedOperationException("Method getSearchProfile() not yet implemented.");
  }
  public void setParseQuery(boolean parseQuery) {
    /**@todo Implement this org.jafer.interfaces.Search method*/
    throw new java.lang.UnsupportedOperationException("Method setParseQuery() not yet implemented.");
  }
  public boolean isParseQuery() {
    /**@todo Implement this org.jafer.interfaces.Search method*/
    throw new java.lang.UnsupportedOperationException("Method isParseQuery() not yet implemented.");
  }
  public void saveQuery(String file) throws JaferException {
    /**@todo Implement this org.jafer.interfaces.Search method*/
    throw new java.lang.UnsupportedOperationException("Method saveQuery() not yet implemented.");
  }
  public Object getQuery() {
    /**@todo Implement this org.jafer.interfaces.Search method*/
    throw new java.lang.UnsupportedOperationException("Method getQuery() not yet implemented.");
  }
  public void setMode(String mode) {
    this.mode = mode;
  }
  public String getMode() {
    return mode;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }
  public void setAllDatabases(String[] allDatabases) {
    this.allDatabases = allDatabases;
  }
  public String[] getAllDatabases() {
    return allDatabases;
  }
}