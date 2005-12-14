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

import org.jafer.query.QueryException;
import org.w3c.dom.Node;

/**
 * Interface for implementing QueryBuilder utility class.
 * Allows the building of XML based queries
 * @author : Antony Corfield; Matthew Dovey; Colin Tatham
 */

 public interface QueryBuilder {
   /**
    *  Creates a basic query node incorporating the attribute and term supplied.
    */
   public Node getNode(String attribute, String term) throws QueryException;

   /**
    *  Creates a basic query node incorporating the attribute and term supplied.
    */
   public Node getNode(int useAttribute, String term) throws QueryException;

  /**
   *  Creates a basic query node incorporating the attributes and term supplied.
   */
   public Node getNode(int[] attributes, String term) throws QueryException;

  /**
   *  Creates a basic query node incorporating the attributes and term supplied.
   *  <p>The int[][] parameter holds the attribute type and corresponding value.</p>
   */
  public Node getNode(int[][] attTypesValues, String term) throws QueryException;

  /**
   *  Produces an AND node from 2 nodes.
   */
  public Node and(Node leftNode, Node rightNode) throws QueryException;

  /**
   *  Produces an OR node from 2 nodes.
   */
  public Node or(Node leftNode, Node rightNode) throws QueryException;

  /**
   *  Produces a NOT node from a node.
   */
  public Node not(Node inputNode) throws QueryException;
}
