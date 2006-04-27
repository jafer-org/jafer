/**
 * Jafer Poject.
 * Copyright (C) 2005, Jafer Project, Oxford University.
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

package org.jafer.util.xml;


public class DomUtilsException extends Exception {

/**
 * Creates new <code>DomUtilsException</code> with null as its detail message.
 * The cause is not initialized, and may subsequently be initialized by a
 * call to Throwable.initCause(java.lang.Throwable).
 */
  public DomUtilsException() {
  }

/**
 * Creates new <code>DomUtilsException</code> with the specified detail message.
 * The cause is not initialized, and may subsequently be initialized by a
 * call to Throwable.initCause(java.lang.Throwable).
 * @param message the detail message.
 */
  public DomUtilsException(String message) {
    super(message);
  }

/**
 * Creates new <code>DomUtilsException</code> with the specified detail message
 * and cause
 * @param message the detail message (which is saved for later retrieval by
 *  the Throwable.getMessage() method).
 * @param cause the cause (which is saved for later retrieval by the
 *  Throwable.getCause() method). A null value is permitted, and indicates that
 *  the cause is nonexistent or unknown.
 */
  public DomUtilsException(String message, Throwable cause) {
    super(message, cause);
  }

/**
 * Constructs a new exception with the specified cause and a detail message of
 * (cause == null ? null : cause.toString()) which typically contains the class
 * and detail message of cause). This constructor is useful for exceptions that
 * are little more than wrappers for other throwables
 * @param cause the cause (which is saved for later retrieval by the
 *  Throwable.getCause() method). A null value is permitted, and indicates that
 *  the cause is nonexistent or unknown.
 */
  public DomUtilsException(Throwable cause) {
    super(cause);
  }
}
