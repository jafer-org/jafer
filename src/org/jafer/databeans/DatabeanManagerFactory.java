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

import java.io.*;
import org.jafer.interfaces.Databean;
import org.jafer.interfaces.DatabeanFactory;
import java.util.Hashtable;


public class DatabeanManagerFactory extends DatabeanFactory {
  public static final String MODE_SERIAL = "serial";
  public static final String MODE_PARALLEL = "parallel";

  private Hashtable databeanFactories = new Hashtable();

  private String mode = MODE_PARALLEL;
  private String[] allDatabases;

  public Databean getDatabean() {
    DatabeanManager bean = new DatabeanManager();
    bean.setDatabeanFactories(databeanFactories);
    bean.setMode(mode);
    bean.setName(this.getName());
    bean.setAllDatabases(allDatabases);
    bean.setDatabases(this.getName());
    return bean;
  }

  public void setDatabeanFactories(org.jafer.interfaces.DatabeanFactory[] databeanFactories) {
    this.databeanFactories.clear();
    allDatabases = new String[databeanFactories.length];
    java.util.Random rnd = new java.util.Random();

    for (int n=0; n < databeanFactories.length; n++) {
      String name = databeanFactories[n].getName();

      if (name == null) {
        name = "DB" + Integer.toHexString(rnd.nextInt());
      }

      this.databeanFactories.put(name, databeanFactories[n]);
      allDatabases[n] = name;
    }
  }

  public org.jafer.interfaces.DatabeanFactory[] getDatabeanFactories() {
    return ((DatabeanFactory[])databeanFactories.values().toArray(new DatabeanFactory[databeanFactories.size()]));
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getMode() {
    return mode;
  }
}