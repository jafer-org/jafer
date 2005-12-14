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

/**
 * Interface for cache configuration methods
 * @author Antony Corfield, Matthew Dovey, Colin Tatham
 * @version 1.0
 *
 */
public interface Cache {
	/**
   * Set the size of the internal cache
   * @param dataCacheSize sizew of cache
   */
  public void setDataCacheSize(int dataCacheSize);
	/**
   * Get the current size of the cache
   * @return cache size
   */
  public int getDataCacheSize();

	/**
   * Set the size of the prefetch buffer
   * @param fetchSize buffer size
   */
  public void setFetchSize(int fetchSize);
	/**
   * Get the current prefetch buffer size
   * @return buffer size
   */
  public int getFetchSize();

	/**
   * Set the prefetch buffer behaviour
   * @param fetchView prefetch beheviour
   */
  public void setFetchView(double fetchView);
	/**
   * Get the current prefetch buffer behaviour setting
   * @return prefetch behaviour
   */
  public double getFetchView();
}