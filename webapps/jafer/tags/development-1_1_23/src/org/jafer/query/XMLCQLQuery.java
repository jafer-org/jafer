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

import org.jafer.conf.Config;
import org.jafer.exception.JaferException;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.*;

public class XMLCQLQuery {

  private static CachedXPathAPI xPathAPI;
  private static Node contextNode;

/**
 * <p>Methods to convert XML queries to CQL queries.</p>
 * <p>Default CQL Context Set is used throughout.</p>
 * <p>(Some checking of XML query structure, attribute names, and content.)</p>
 */

  public String getCQLQuery(Node domQuery) throws QueryException {

    StringBuffer cqlQuery;
    cqlQuery = processNode(domQuery);
    return cqlQuery.toString();
  }


  private StringBuffer processNode(Node node) throws QueryException {

    return processNode(node, new StringBuffer(), false);
  }


  private StringBuffer processNode(Node node, StringBuffer buffer, boolean nested) throws QueryException {

    String nodeName = node.getNodeName();

    if (nodeName.equalsIgnoreCase("constraintModel"))
      return processConstraintModel(node, buffer);

    if (nested)
      buffer.append("(");

    if (nodeName.equalsIgnoreCase("and"))
      processAndNode(node, buffer);

    else if (nodeName.equalsIgnoreCase("or"))
      processOrNode(node, buffer);

    else if (nodeName.equalsIgnoreCase("not"))
      processNotNode(node, buffer);

    else {
      String message = "Node not expected: " + nodeName;
      Logger.getLogger("org.jafer.query").log(Level.SEVERE, message);
      throw new QueryException(message, 108, "");
    }

    if (nested)
        buffer.append(")");

    return buffer;
  }

  private String translateUse(String use) {
    if (use.equalsIgnoreCase("author"))
      return "dc.creator";
    if (use.equalsIgnoreCase("title"))
      return "dc.title";
    if (use.equalsIgnoreCase("subject"))
      return "dc.subject";
    return "cql.any";
  }

  private StringBuffer processConstraintModel(Node cM, StringBuffer buffer) throws QueryException {

//    StringBuffer buffer = new StringBuffer();
    Node semantic, relation;
    String index, rel, term = "";
    /** @todo other attributes, and generalise this... */

    semantic = selectNode(cM, "constraint/semantic");
    relation = selectNode(cM, "constraint/relation");

    if (semantic != null) {
      try {
        index = Config.getSemanticAttributeName(getNodeValue(semantic));
        if (index != "" ) {//relation att might not translate to a symbol, eg phonetic, stem?
          buffer.append(Config.translateBib1ToCQLIndex(index) + " ");
        }
        if (relation != null) {// must include both or neither
          try {
            rel = Config.getRelationSymbol(getNodeValue(relation));
            if (rel != "") {//relation att might not translate to a symbol, eg phonetic, stem?
              /** @todo warning if only one of semantic/relation found? */
              buffer.append(rel + " ");
            } else {
              buffer.append("= ");
            }
          }
          catch (JaferException ex) {
            throw new QueryException(
                "Error in converting XML attribute value to name or symbol");
          }
        } else {
          buffer.append("= ");
        }
      }
      catch (JaferException ex) {
        throw new QueryException(
            "Error in converting XML attribute value to name or symbol");
      }

    }

    term = getNodeValue(selectNode(cM, "model"));
    //wrap term in quotes, even if empty:
    term = "\"" + term + "\"";

    buffer.append(term);

    return buffer;
  }


  private StringBuffer processNotNode(Node node, StringBuffer buffer) throws QueryException {

    buffer.append(" " + node.getNodeName() + " ");
    processNode(getFirstChild(node), buffer, true);
    return buffer;
  }


  private StringBuffer processAndNode(Node node, StringBuffer buffer) throws QueryException {

    Node first, second;

    first = getFirstChild(node);
    second = getSecondChild(node);

    switch (findNotChild(node)) {
      case 1:
        processNode(second, buffer, true);
        processNotNode(first, buffer);
        break;
      case 2:
        processNode(first, buffer, true);
        processNotNode(second, buffer);
        break;
      default:
        processNode(first, buffer, true);
        buffer.append(" " + node.getNodeName() + " ");
        processNode(second, buffer, true);
    }

    return buffer;
  }


  private StringBuffer processOrNode(Node node, StringBuffer buffer) throws QueryException {

      Node first, second;

      first = getFirstChild(node);
      second = getSecondChild(node);

      switch (findNotChild(node)) {
        case 1:
          processNode(second, buffer, true);
          buffer.append(" " + node.getNodeName() + " ");
          processNode(first, buffer, true);
          break;
        default:
          processNode(first, buffer, true);
          buffer.append(" " + node.getNodeName() + " ");
          processNode(second, buffer, true);
      }

      return buffer;
    }


  private int findNotChild(Node node) throws QueryException {
/** @todo put into a query util class? */
    int position = 0;
    // first child <NOT> node, position = 1,
    // second child <NOT> node, position = 2,
    // both children <NOT> nodes, position = 3,
    // no <NOT> node, position = 0,
    try {
      if (getFirstChild(node).getNodeName().equalsIgnoreCase("not"))
        position = 1;
      else if (getSecondChild(node).getNodeName().equalsIgnoreCase("not"))
        return 2;
      else
        return 0;

      if (getSecondChild(node).getNodeName().equalsIgnoreCase("not"))
        position = 3;
    }
    catch (Exception ex) {
      String message = "Inconsistency in XML query format. (parent node: "+node.getNodeName()+")";
      Logger.getLogger("org.jafer.query").log(Level.WARNING, message, ex);
      throw new QueryException(message, 108, "");
    }

    return position;
  }


  private Node getFirstChild(Node sourceNode) throws QueryException {
  // this method ignores text nodes, Node.getFirstChild doesn't.
  /** @todo put into a query util class? */
    return selectNode(sourceNode, "./*[position()='1']");
  }


  private Node getSecondChild(Node sourceNode) throws QueryException {
  // this method ignores text nodes, Node.getLastChild doesn't.
  /** @todo put into a query util class? */
    return selectNode(sourceNode, "./*[position()='2']");
  }


  private static String getNodeValue(Node node) {
    /** @todo put into a query util class? */
    try {
      if (selectNode(node, "./text()") != null)
	return selectNode(node, "./text()").getNodeValue();
    }
    catch (QueryException ex) {
      String message = "Error in getting Node value: " + node.getNodeName();
      Logger.getLogger("org.jafer.query").log(Level.WARNING, message, ex);
    }

    return "";
  }


  private static Node selectNode(Node sourceNode, String XPath) throws QueryException {
    /** @todo improve CachedXPathAPI handling... */
    /** @todo put into a query util class? */
    if (sourceNode == null)
      return null;

    try {
      if (sourceNode != contextNode) {
        xPathAPI = new CachedXPathAPI();
        contextNode = sourceNode;
      }

      return xPathAPI.selectSingleNode(sourceNode, XPath);

    }
    catch (Exception ex) {
      String message = "Error in XML query format (parent: " +
          sourceNode.getNodeName() + " XPath: " + XPath + ")";
      Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, ex);
      throw new QueryException(message, ex);
      /** @todo throw new QueryException(message, 108, ""); */
    }
  }


  private static NodeList selectNodeList(Node sourceNode, String XPath) throws QueryException {
  /** @todo improve CachedXPathAPI handling... */
  /** @todo put into a query util class? */

  if (sourceNode == null)
    return null;

  try {
    if (sourceNode != contextNode) {
      xPathAPI = new CachedXPathAPI();
      contextNode = sourceNode;
    }

    return xPathAPI.selectNodeList(sourceNode, XPath);

  }
  catch (Exception ex) {
    String message = "Error in XML query format (parent: " +
        sourceNode.getNodeName() + " XPath: " + XPath + ")";
    Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, ex);
    throw new QueryException(message, ex);
    /** @todo throw new QueryException(message, 108, ""); */
      }
  }
}
