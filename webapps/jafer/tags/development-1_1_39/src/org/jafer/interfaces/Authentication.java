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
 *
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

 package org.jafer.interfaces;
 /**
  * Interface for methods to set user credentials *
  */

public interface Authentication {

  /**
   * Set username for access to the data source
   * @param username Username to set
   */
  public void setUsername(String username);

  /**
   * Get the username set for accessing the datasource
   * @return Username set
   */
  public String getUsername();

  /**
   * Set password for accessing the datasource
   * @param password Password to set
   */
  public void setPassword(String password);

  /**
   * Get the password set for accessing the datasource
   * @return Password set
   */
  public String getPassword();

  /**
   * Set group name for accessing the data source
   * @param Group Group name to set
   */
  public void setGroup(String Group);

  /**
   * Get the group name set for accessing the datasource
   * @return Group name set
   */
  public String getGroup();
}
