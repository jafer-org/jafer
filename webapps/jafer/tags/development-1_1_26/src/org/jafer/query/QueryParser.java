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
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.query;

import org.w3c.dom.Node;
import z3950.v3.RPNQuery;
import java.util.logging.Logger;
import java.util.logging.Level;

public class QueryParser {

  private static Logger logger;

  /**
   * Most of the processing previously done here has been moved to XMLRPNQuery class.
   * This class could be used to parse an XML qury against a schema, and possibly check RPNQuery structure.
   */

  public static Object parseQuery(Object query) throws QueryException {

    logger = Logger.getLogger("org.jafer.databeans");/** @todo databeans or query? */

    if (query instanceof Node)
      return parseQuery((Node)query);
    if (query instanceof RPNQuery)
      return parseQuery((RPNQuery)query);
    else {
      String message = "Query not parsed. (No method available to parse query of Class: "+ query.getClass().getName()+")";
      logger.log(Level.WARNING, message);
    }
    return query;
  }

  public static RPNQuery parseQuery(RPNQuery query) throws QueryException {

    String message = "Query not parsed. (Method to parse RPNQuery not yet implemented.)";
    logger.log(Level.WARNING, message);
    /** @todo implement this.... */
    return query;
  }

  public static Node parseQuery(Node query) throws QueryException {

    String message = "Query not parsed against schema. (General structure is checked in XMLRPNQuery class)";
    logger.log(Level.WARNING, message);
    /** @todo implement this.... */
    return query;
  }
}