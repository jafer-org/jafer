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

import org.jafer.exception.JaferException;
import org.jafer.record.Field;

/**
 * Methods for record retrieval
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 *
 */
public interface Present {

  /**
   * Set the current record cursor
   * @param nRecord Record position (starting at 1)
   */
  public void setRecordCursor(int nRecord) throws JaferException;

  /**
   * Get the current record position cursor
   * @return get record position
   */
  public int getRecordCursor();

  /**
   * Throw exception is record schema not preferred schema
   * @param checkRecordFormat setting of record schema checking
   */
  public void setCheckRecordFormat(boolean checkRecordFormat);

  /**
   * Get record schema checking setting
   * @return record checking setting
   */
  public boolean isCheckRecordFormat();

  /**
   * Set element specification for record retrieval
   * @param elementSpec element specification
   */

  public void setElementSpec(String elementSpec);

  /**
   * Get current element specification setting
   * @return element specification
   */
  public String getElementSpec();

  /**
   * Set current prefered record schema
   * @param schema record schema
   */
  public void setRecordSchema(String schema);

  /**
   * Get currently set record schema
   * @return record schema
   */
  public String getRecordSchema();

  /**
   * Get current record
   * @return record
   */
  public Field getCurrentRecord() throws JaferException;

  /**
   * Get database of current record
   * @return database
   */
  public String getCurrentDatabase() throws JaferException;
}
