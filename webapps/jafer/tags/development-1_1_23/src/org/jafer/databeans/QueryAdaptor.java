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
 */

package org.jafer.databeans;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.jafer.exception.JaferException;
import org.jafer.interfaces.Search;
import org.jafer.query.XMLRPNQuery;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.xml.XMLTransformer;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import z3950.v3.RPNQuery;

/**
 * <p>Transforms a query via submitQuery method using templates object (eg. queryAdaptor.xsl - specified in server.xml).
 * Returns number of results from Search operation</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
 public class QueryAdaptor extends Adaptor {

   private static Logger logger;

   public QueryAdaptor() {

     logger = Logger.getLogger("org.jafer.databeans");
   }


   public int submitQuery(Object query) throws JaferException {

     if (query instanceof Node)
       return submitQuery((Node)query);
     else if (query instanceof RPNQuery)
       return submitQuery((RPNQuery)query);
     else {
       String message = "Query type: "+ query.getClass().getName() +" not supported";
       logger.log(Level.SEVERE, message);
       throw new JaferException(message, 107, "");
     }
   }

   public int submitQuery(Node query) throws JaferException {

     Document doc = DOMFactory.newDocument();
     Node root = doc.createElement("query");
     root.appendChild(doc.importNode(query, true));
     query = XMLTransformer.transform(root, getTransform());

     return ((Search)getDatabean()).submitQuery(query);
   }

   public int submitQuery(RPNQuery query) throws JaferException {

     Node xmlQuery = XMLRPNQuery.getXMLQuery(query);
     return submitQuery(xmlQuery);
   }
}