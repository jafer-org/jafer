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

import org.jafer.exception.JaferException;
import org.jafer.record.Field;
import org.w3c.dom.Node;

/**
 * Methods for implementing keyword and term retrieval
 */
public interface Scan {
   /**
   * Get keywords or terms
   * @param noOfTerms no of terms to retrieve
   * @param term query to identify first term
   * @return terms
   */
  public Field[] getTerms(int noOfTerms, Node term);

   /**

    * Get keywords or terms

   * @param noOfTerms no of terms to retrieve

    * @param termStep value to step through terms

    * @param termPosition position of identified term in response

    * @param term query to identify first term
   * @return terms

    */
  public Field[] getTerms(int noOfTerms, int termStep, int termPosition, Node term);

}
