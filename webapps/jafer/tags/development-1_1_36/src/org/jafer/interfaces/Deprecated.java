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

import java.util.Hashtable;

/**
 * Methods deprecated during development
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 *
 */
public interface Deprecated {
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
  public void setRecordMap(Hashtable recordMap);
	/**
   * @deprecated Replaced by RecordDescriptor
   *
   */
  public Hashtable getRecordMap();
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public void setStyleSheetMap(Hashtable styleSheetMap);
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public Hashtable getStyleSheetMap();
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public void setXMLCacheSize(int xmlCacheSize);
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public int getXMLCacheSize();
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public Hashtable getTemplatesMap();
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public void setXMLRecords(boolean xmlRecords);
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public boolean isXMLRecords();
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public void setTransformXMLRecords(boolean transformXMLRecords);
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public boolean isTransformXMLRecords();
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public void setRecordFormatName(String recordFormatName);
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public void setRecordFormat(int[] recordFormat);
	/**
   * @deprecated Replaced by RecordDescriptors
   *
   */
	public int[] getRecordFormat();
}
