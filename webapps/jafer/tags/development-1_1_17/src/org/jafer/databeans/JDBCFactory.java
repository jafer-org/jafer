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
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2002
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.databeans;

import org.jafer.interfaces.DatabeanFactory;
import org.jafer.interfaces.Z3950Connection;
import org.jafer.interfaces.Databean;

public class JDBCFactory extends DatabeanFactory implements Z3950Connection {

  private String host, schema, className;
  private int port;


  public Databean getDatabean() {

    JDBC databean  = null;
	try {
	  databean = (JDBC)Class.forName(this.getClassName()).newInstance();
	}
	catch (Exception ex) {
	  /** @todo handle exceptions */
	  ex.printStackTrace();
	}

    databean.setHost(getHost());
    databean.setPort(getPort());
//    databean.setDatabases() not used, database name is loaded from JDBC config.xml.
//    databean.setRecordSchema() not used, is set when server requests recordformat.


    return databean;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getClassName() {
    return this.className;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getHost() {
    return this.host;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getPort() {
    return this.port;
  }
}