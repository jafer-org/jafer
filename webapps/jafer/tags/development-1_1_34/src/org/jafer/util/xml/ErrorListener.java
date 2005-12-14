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
package org.jafer.util.xml;

import javax.xml.transform.TransformerException;
import org.jafer.exception.JaferException;

/**
 * <p>Implements methods for handling transformation and parsing errors.</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */

public class ErrorListener implements javax.xml.transform.ErrorListener {

  public void warning(TransformerException ex) throws TransformerException {
    throw ex;
  }

  public void fatalError(TransformerException ex) throws TransformerException {
    throw ex;
  }

  public void error(TransformerException ex) throws TransformerException {
    throw ex;
  }

}