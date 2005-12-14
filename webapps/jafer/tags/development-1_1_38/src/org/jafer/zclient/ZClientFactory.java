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

package org.jafer.zclient;

public class ZClientFactory extends org.jafer.interfaces.DatabeanFactory {

  private String host;
  private String[] databases;
  private String recordSchema;
  private int[] recordSyntax;
  private String username;
  private String password;
  private int port;
//  private String name;

  /**
   *  Constructor for the ZClient object
   */

  public org.jafer.interfaces.Databean getDatabean() {

    ZClient bean = new ZClient();
    bean.setHost(getHost());
    bean.setPort(getPort());
    bean.setDatabases(getDatabases());
    bean.setRecordSchema(getRecordSchema());
    bean.setRecordSyntax(getRecordSyntax());
    bean.setUsername(getUsername());
    bean.setPassword(getPassword());

    return bean;
  }

  public void setHost(String host) {
    this.host = host;
  }
  public String getHost() {
    return host;
  }
  public void setPort(int port) {
    this.port = port;
  }
  public int getPort() {
    return port;
  }
  public void setDatabases(String[] databases) {
    this.databases = databases;
  }
  public String[] getDatabases() {
    return databases;
  }
  public void setRecordSchema(String recordSchema) {
    this.recordSchema = recordSchema;
  }
  public String getRecordSchema() {
    return recordSchema;
  }
  public void setRecordSyntax(int[] recordSyntax) {
    this.recordSyntax = recordSyntax;
  }
  public int[] getRecordSyntax() {
    return recordSyntax;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getUsername() {
    return username;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getPassword() {
    return password;
  }
}