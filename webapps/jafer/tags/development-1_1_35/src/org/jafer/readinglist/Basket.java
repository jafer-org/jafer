/**
 * JAFER Toolkit Poject.
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

package org.jafer.readinglist;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;

import org.w3c.dom.*;

import org.jafer.record.Field;
import org.jafer.util.xml.XMLSerializer;
import org.jafer.exception.JaferException;

public class Basket {

private Hashtable basket; //holds records as Field objects.
private Vector keys; //holds record IDs in the order that the records were added to the Hashtable.

  public Basket(){

    basket = new Hashtable();
    keys = new Vector();
  }

  public Hashtable getBasket() {

    return basket;
  }


  public int size() {

    return basket.size();
  }


  public void addItem(String docId, Field field) {

    if (!basket.containsKey(docId)) {
      basket.put(docId, field);
      keys.add(docId);
    }
  }


  public Field getItem(String docId) throws JaferException {

    if (basket.get(docId) != null)
      return (Field)basket.get(docId);
    else
      throw new JaferException("Basket does not contain item requested. (key:"+docId+")");
  }


  public void removeItem(String docId) {

    if (basket.containsKey(docId)) {
      basket.remove(docId);
      keys.remove(docId);
    }
  }


  public String[] getKeyArray() {

    String [] array = new String[keys.size()];
    Enumeration e = keys.elements();
    int n = 0;
    while (e.hasMoreElements()) {
      array[n] = e.nextElement().toString();
      n++;
    }
    return array;
  }


  public Vector getKeys() {

    return keys;
  }


  public void saveBasket(Node records, String filename) throws JaferException {

    XMLSerializer.out(records, "xml", filename);

  }
}